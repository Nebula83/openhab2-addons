/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.evohome.handler;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseBridgeHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.openhab.binding.evohome.configuration.EvohomeGatewayConfiguration;
import org.openhab.binding.evohome.internal.api.EvohomeApiClient;
import org.openhab.binding.evohome.internal.api.EvohomeApiClientV2;
import org.openhab.binding.evohome.internal.api.models.ControlSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides the bridge for this binding. Controls the authentication sequence.
 * Manages the scheduler for getting updates from the API and updates the Things it contains.
 *
 * @author Jasper van Zuijlen - Initial contribution
 *
 */
public class EvohomeGatewayHandler extends BaseBridgeHandler {

    private final Logger logger = LoggerFactory.getLogger(EvohomeGatewayHandler.class);
    private EvohomeGatewayConfiguration configuration;
    private EvohomeApiClient apiClient;
    private List<GatewayStatusListener> listeners = new CopyOnWriteArrayList<GatewayStatusListener>();

    protected ScheduledFuture<?> refreshTask;

    public EvohomeGatewayHandler(Bridge thing) {
        super(thing);
    }

    @Override
    public void initialize() {
        logger.info("Initializing Evohome Gateway handler.");

        configuration = getConfigAs(EvohomeGatewayConfiguration.class);

        if (checkConfig()) {
            disposeApiClient();
            apiClient = new EvohomeApiClientV2(configuration);

            // Initialization can take a while, so kick if off on a separate thread
            scheduler.schedule(new Runnable() {
                @Override
                public void run() {
                    if (apiClient.login()) {
                        startRefreshTask();
                    } else {
                        updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                                "Authentication failed");
                    }
                }
            }, 0, TimeUnit.SECONDS);

        }
    }

    @Override
    public void dispose() {
        disposeRefreshTask();
        disposeApiClient();
        listeners.clear();
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (command == RefreshType.REFRESH) {

        }
    }

    public ControlSystem[] getControlSystems() {
        if (apiClient != null) {
            return apiClient.getControlSystems();
        }
        return null;
    }

    public void addGatewayStatusListener(GatewayStatusListener listener) {
        listeners.add(listener);
        listener.gatewayStatusChanged(getThing().getStatus());
    }

    private void disposeApiClient() {
        if (apiClient != null) {
            apiClient.logout();
        }
        apiClient = null;
    }

    private void disposeRefreshTask() {
        if (refreshTask != null) {
            refreshTask.cancel(true);
        }
    }

    private boolean checkConfig() {
        try {
            if (configuration == null) {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
                        "Configuration is missing or corrupted");
            } else if (StringUtils.isEmpty(configuration.username)) {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, "Username not configured");
            } else if (StringUtils.isEmpty(configuration.password)) {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, "Password not configured");
            } else if (StringUtils.isEmpty(configuration.applicationId)) {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
                        "Application Id not configured");
            } else {
                return true;
            }
        } catch (Exception e) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.NONE, e.getMessage());
        }

        return false;
    }

    private void startRefreshTask() {
        disposeRefreshTask();

        refreshTask = scheduler.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                update();
            }
        }, 0, configuration.refreshInterval, TimeUnit.SECONDS);
    }

    private void update() {
        try {
            try {
                apiClient.update();
            } catch (Exception e) {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, e.getMessage());
                return;
            }

            updateGatewayStatus();
            // updateThings();
        } catch (Exception e) {
            logger.debug("update failed", e);
        }
    }

    private void updateGatewayStatus() {
        ThingStatus newStatus = ThingStatus.ONLINE;
        ThingStatusDetail statusDetail = ThingStatusDetail.NONE;
        String statusMessage = null;

        /*
         * for (GatewayStatus status : apiClient.getGateways()) {
         * if (status.activeFaults.size() > 0) {
         * newStatus = ThingStatus.OFFLINE;
         * statusDetail = ThingStatusDetail.COMMUNICATION_ERROR;
         * statusMessage = status.activeFaults.get(0).faultType;
         * }
         * }
         */

        // Prevent spamming the log file
        if (!newStatus.equals(getThing().getStatus())) {
            updateListeners(newStatus);
            updateStatus(newStatus, statusDetail, statusMessage);
        }
    }

    private void updateListeners(ThingStatus status) {
        for (GatewayStatusListener listener : listeners) {
            listener.gatewayStatusChanged(status);
        }
    }

    private void updateThings() {
        /*
         * for (Thing handler : getThing().getThings()) {
         * ThingHandler thingHandler = handler.getHandler();
         * if (thingHandler instanceof BaseEvohomeHandler) {
         * BaseEvohomeHandler moduleHandler = (BaseEvohomeHandler) thingHandler;
         * moduleHandler.update(apiClient);
         * }
         * }
         */
    }
}

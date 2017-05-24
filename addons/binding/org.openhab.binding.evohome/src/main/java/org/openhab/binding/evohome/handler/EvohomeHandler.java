/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.evohome.handler;

import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.evohome.EvohomeBindingConstants;
import org.openhab.binding.evohome.internal.api.EvohomeApiClient;
import org.openhab.binding.evohome.internal.api.models.v2.response.ZoneStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link EvohomeHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Jasper van Zuijlen - Initial contribution
 */
public class EvohomeHandler extends BaseThingHandler {

    private final Logger logger = LoggerFactory.getLogger(EvohomeHandler.class);

    public EvohomeHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
/*
        if (channelUID.getId().equals(CHANNEL_1)) {
            int i = 5;

            i++;
            // TODO: handle command

            // Note: if communication with thing fails for some reason,
            // indicate that by setting the status with detail information
            // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
            // "Could not control device at IP address x.x.x.x");
        }
*/
    }

    @Override
    public void initialize() {
        if(getBridge() != null && getBridge().getHandler() != null){
            EvohomeApiClient apiClient = ((EvohomeGatewayHandler) getBridge().getHandler()).getApiClient();
            int zoneId = Integer.valueOf(getThing().getProperties().get(EvohomeBindingConstants.ZONE_ID));
            int locationId = Integer.valueOf(getThing().getProperties().get(EvohomeBindingConstants.LOCATION_ID));
            ZoneStatus zoneStatus = apiClient.getHeatingZone(locationId, zoneId);
            if(zoneStatus == null){
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR);
                return;
            }
            updateState(EvohomeBindingConstants.SYSTEM_MODE_CHANNEL, new DecimalType(zoneStatus.Temperature.Temperature));
            updateState(EvohomeBindingConstants.SET_POINT_CHANNEL, new DecimalType(zoneStatus.HeatSetpoint.TargetTemperature));
            updateState(EvohomeBindingConstants.SET_POINT_STATUS_CHANNEL, new StringType(zoneStatus.HeatSetpoint.SetpointMode));
            SET_POINT_CHANNEL
            updateStatus(ThingStatus.ONLINE);
        }
    }
}
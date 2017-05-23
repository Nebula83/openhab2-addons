/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.evohome.discovery;

import static org.openhab.binding.evohome.EvohomeBindingConstants.*;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.openhab.binding.evohome.EvohomeBindingConstants;
import org.openhab.binding.evohome.handler.EvohomeGatewayHandler;
import org.openhab.binding.evohome.internal.api.EvohomeApiClient;
import org.openhab.binding.evohome.internal.api.models.ControlSystem;
import org.openhab.binding.evohome.internal.api.models.v1.Weather;
import org.openhab.binding.evohome.internal.api.models.v2.response.HeatSetpointCapabilities;
import org.openhab.binding.evohome.internal.api.models.v2.response.ScheduleCapabilities;
import org.openhab.binding.evohome.internal.api.models.v2.response.TemperatureControlSystem;
import org.openhab.binding.evohome.internal.api.models.v2.response.Zone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link EvohomeDiscoveryService} class is capable of discovering the available data from Evohome
 *
 * @author Neil Renaud - Initial contribution
 */
public class EvohomeDiscoveryService extends AbstractDiscoveryService {
    private Logger logger = LoggerFactory.getLogger(EvohomeDiscoveryService.class);
    private static final int SEARCH_TIME = 2;
    private static final String LOCATION_NAME = "Location Name";
    private static final String LOCATION_ID = "Location Id";
    private static final String DEVICE_NAME = "Device Name";
    private static final String DEVICE_ID = "Device Id";

    private EvohomeGatewayHandler evohomeBridgeHandler;

    public EvohomeDiscoveryService(EvohomeGatewayHandler evohomeBridgeHandler) {
        super(SUPPORTED_THING_TYPES_UIDS, SEARCH_TIME);
        this.evohomeBridgeHandler = evohomeBridgeHandler;
    }

    @Override
    public void startScan() {
        logger.debug("Evohome start scan");
        if (evohomeBridgeHandler != null) {
            try {
                EvohomeApiClient client = evohomeBridgeHandler.getApiClient();
                if (client != null) {

//                    client.update();
                    for (ControlSystem gateway : client.getControlSystems()) {
                        discoverGateway(gateway);
                        discoverHeatingZones(gateway.getId(), gateway.getHeatingZones());
                    }

//                    DataModelResponse[] dataArray = client.getData();
//                    for (DataModelResponse data : dataArray) {
//                        discoverWeather(data.getWeather(), data.getName(), data.getLocationId());
//                    }
                }
            } catch (Exception e) {
                logger.warn("{}", e.getMessage(), e);
            }
        }
        stopScan();
    }

    private void discoverHeatingZones(int locationId, TemperatureControlSystem heatingZones) {
        for(Zone zone : heatingZones.Zones){
            String zoneName = zone.Name;
            long zoneId = zone.ZoneId;
            String modelType = zone.ModelType;
            String zoneType = zone.ZoneType;
            HeatSetpointCapabilities heatSetpointCapabilities = zone.HeatSetpointCapabilities;
            ScheduleCapabilities scheduleCapabilities = zone.ScheduleCapabilities;
            ThingUID thingUID = findThingUID(THING_TYPE_EVOHOME_HEATING_ZONE.getId(), zoneName);
            Map<String, Object> properties = new HashMap<>();
            properties.put(EvohomeBindingConstants.LOCATION_ID, locationId);
            properties.put(EvohomeBindingConstants.ZONE_ID, zoneId);
            properties.put(EvohomeBindingConstants.ZONE_NAME, zoneName);
            properties.put(EvohomeBindingConstants.ZONE_TYPE, zoneType);
            properties.put(EvohomeBindingConstants.ZONE_MODEL_TYPE, modelType);
            addDiscoveredThing(thingUID, properties, zoneName);
        }
        // TODO Auto-generated method stub

    }

    private void discoverGateway(ControlSystem controlSystem) {
        String name = controlSystem.getName();
        ThingUID thingUID = findThingUID(THING_TYPE_EVOHOME_DISPLAY.getId(), name);
        Map<String, Object> properties = new HashMap<>();
        properties.put(EvohomeBindingConstants.LOCATION_NAME, name);
        properties.put(EvohomeBindingConstants.LOCATION_ID, controlSystem.getId());
        addDiscoveredThing(thingUID, properties, name);
    }

    private void discoverWeather(Weather weather, String name, String locationId) throws IllegalArgumentException {
        ThingUID thingUID = findThingUID(THING_TYPE_EVOHOME_LOCATION.getId(), name);
        Map<String, Object> properties = new HashMap<>();
        properties.put(EvohomeBindingConstants.LOCATION_NAME, name);
        properties.put(EvohomeBindingConstants.LOCATION_ID, locationId);
        addDiscoveredThing(thingUID, properties, name);
    }
    private void addDiscoveredThing(ThingUID thingUID, Map<String, Object> properties, String displayLabel) {
        DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(thingUID).withProperties(properties)
                .withBridge(evohomeBridgeHandler.getThing().getUID()).withLabel(displayLabel).build();

        thingDiscovered(discoveryResult);
    }

    private ThingUID findThingUID(String thingType, String thingId) throws IllegalArgumentException {
        for (ThingTypeUID supportedThingTypeUID : getSupportedThingTypes()) {
            String uid = supportedThingTypeUID.getId();

            if (uid.equalsIgnoreCase(thingType)) {

                return new ThingUID(supportedThingTypeUID, evohomeBridgeHandler.getThing().getUID(),
                        thingId.replaceAll("[^a-zA-Z0-9_]", ""));
            }
        }

        throw new IllegalArgumentException("Unsupported device type discovered: " + thingType);
    }
}

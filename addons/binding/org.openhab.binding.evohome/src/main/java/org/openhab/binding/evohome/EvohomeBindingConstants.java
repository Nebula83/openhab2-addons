/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.evohome;

import java.util.Set;

import org.eclipse.smarthome.core.thing.ThingTypeUID;

import com.google.common.collect.ImmutableSet;

/**
 * The {@link EvohomeBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Jasper van Zuijlen - Initial contribution
 * @author Neil Renaud - Heating Zones
 */
public class EvohomeBindingConstants {

    private static final String BINDING_ID = "evohome";

    // List of all Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_EVOHOME_ACCOUNT = new ThingTypeUID(BINDING_ID, "account");
    public static final ThingTypeUID THING_TYPE_EVOHOME_DISPLAY = new ThingTypeUID(BINDING_ID, "display");
    public static final ThingTypeUID THING_TYPE_EVOHOME_HEATING_ZONE = new ThingTypeUID(BINDING_ID, "heatingzone");

    // List of all Channel ids
    public static final String DISPLAY_SYSTEM_MODE_CHANNEL = "SystemMode";
    public static final String ZONE_TEMPERATURE_CHANNEL = "Temperature";
    public static final String ZONE_CURRENT_SET_POINT_CHANNEL = "CurrentSetPoint";
    public static final String ZONE_SET_POINT_CHANNEL = "SetPoint";
    public static final String ZONE_SET_POINT_STATUS_CHANNEL = "SetPointStatus";

    // List of Discovery properties
    public static final String PROPERTY_ID = "id";
    public static final String PROPERTY_NAME = "name";

    // List of all addressable things in OH = SUPPORTED_DEVICE_THING_TYPES_UIDS + the virtual bridge
    public static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = ImmutableSet.of(THING_TYPE_EVOHOME_ACCOUNT,
            THING_TYPE_EVOHOME_DISPLAY, THING_TYPE_EVOHOME_HEATING_ZONE);
}

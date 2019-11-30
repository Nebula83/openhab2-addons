/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.evohome.internal.configuration;

/**
 * List of evohome API constants
 *
 * @author James Kinsman - Initial Contribution
 *
 */
public class EvohomeOverrideModeConstants {
    public static final int BRIDGE_INHERIT = -1;
    public static final int PERMANENT_OVERRIDE = 0;
    public static final int TEMPORARY_OVERRIDE_SCHEDULE_NEXT = 1;
    public static final int TEMPORARY_OVERRIDE_TIME_ADD = 2;
}

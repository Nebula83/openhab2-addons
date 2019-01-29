/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.evohome.internal.api.models.v2.response;

import java.time.LocalTime;

import com.google.gson.annotations.SerializedName;

/**
 * Response model for a schedule switch point
 *
 * @author Jeroen Peters
 *
 */
public class SwitchPoint {

    @SerializedName("heatSetpoint")
    private double heatSetpoint;

    @SerializedName("timeOfDay")
    private LocalTime timeOfDay;

    public double getheatSetpoint() {
        return heatSetpoint;
    }

    public LocalTime getTimeOfDay() {
        return timeOfDay;
    }
}

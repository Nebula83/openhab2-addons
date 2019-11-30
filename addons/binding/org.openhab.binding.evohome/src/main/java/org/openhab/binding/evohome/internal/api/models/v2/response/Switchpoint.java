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
 * Response model for the daily schedule
 *
 * @author James Kinsman - Initial Contribution
 *
 */
public class Switchpoint {

    @SerializedName("heatSetpoint")
    private double heatSetpoint;

    @SerializedName("timeOfDay")
    private String timeOfDay;

    public Switchpoint() {
        heatSetpoint = 0.0;
        timeOfDay = "";
    }

    public double getHeatSetpoint() {
        return heatSetpoint;
    }

    public LocalTime getTimeOfDay() {
        return LocalTime.parse(timeOfDay);
    }

}


/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.evohome.internal.api.models.v2.response;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * Response model for the daily schedule
 *
 * @author James Kinsman - Initial Contribution
 *
 */
public class DailySchedule {

    @SerializedName("dayOfWeek")
    private String dayOfWeek;

    @SerializedName("switchpoints")
    private List<Switchpoint> switchpoints;

    public DailySchedule() {
        dayOfWeek = "Monday";
        switchpoints = new ArrayList<>();
    }

    public List<Switchpoint> getSwitchpoints() {
        return switchpoints;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public int getWeekday() {
        switch (dayOfWeek.toLowerCase()) {
            case "monday":
                return 0;
            case "tuesday":
                return 1;
            case "wednesday":
                return 2;
            case "thursday":
                return 3;
            case "friday":
                return 4;
            case "saturday":
                return 5;
            case "sunday":
                return 6;
            default:
                return -1;
        }
    }

}


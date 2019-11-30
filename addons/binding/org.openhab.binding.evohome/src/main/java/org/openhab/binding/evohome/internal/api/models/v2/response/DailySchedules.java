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
 * Response model for the daily schedules
 *
 * @author James Kinsman - Initial Contribution
 *
 */
public class DailySchedules {

    @SerializedName("dailySchedules")
    private List<DailySchedule> schedules;

    public DailySchedules() {
        schedules = new ArrayList<>();
    }

    public List<DailySchedule> getSchedules() {
        return schedules;
    }
}


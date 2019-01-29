/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.evohome.handler;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.List;

import org.openhab.binding.evohome.internal.api.models.v2.response.DailySchedule;
import org.openhab.binding.evohome.internal.api.models.v2.response.DailySchedules;
import org.openhab.binding.evohome.internal.api.models.v2.response.SwitchPoint;

/**
 * Class to find current and future schedules.
 *
 * @author Jeroen Peters
 *
 */
public class ScheduleHelper {

    private DailySchedules schedules;

    public ScheduleHelper(DailySchedules schedules) {
        this.schedules = schedules;
    }

    public boolean hasSchedule() {
        boolean result = false;

        for (DailySchedule schedule : schedules) {
            if (schedule.getSwitchPoints().size() > 0) {
                result = true;
                break;
            }
        }
        return result;
    }

    public ZonedDateTime getStartNextSwitchPoint(ZonedDateTime zoned) {
        // Map zoned day index to schedule list (Monday = 0, Sunday = 6)
        int todayIndex = zoned.getDayOfWeek().getValue() - 1;
        LocalTime time = zoned.toLocalTime();

        List<SwitchPoint> switchPoints = schedules.get(todayIndex).getSwitchPoints();
        for (SwitchPoint switchPoint : switchPoints) {
            if (time.compareTo(switchPoint.getTimeOfDay()) < 0) {
                return zoned.with(switchPoint.getTimeOfDay());
            }
        }

        // No start time found, look for switch point in upcoming days
        for (int day = (todayIndex + 1) % 7; day != todayIndex; day = (day + 1) % 7) {
            if (schedules.get(day).getSwitchPoints().size() > 0) {
                LocalTime timeOfDay = schedules.get(day).getSwitchPoints().get(0).getTimeOfDay();
                int days = day - todayIndex;
                if (days < 0) {
                    days = days + 7;
                }
                return zoned.plusDays(days).with(timeOfDay);
            }
        }

        // If still not found, return the time of the first switch point of today next week
        if (switchPoints.size() > 0) {
            return zoned.plusDays(7).with(switchPoints.get(0).getTimeOfDay());
        }

        return null;
    }

    public double getScheduleTemperature(ZonedDateTime zoned) {

        // Map zoned day index to schedule list (Monday = 0, Sunday = 6)
        int todayIndex = zoned.getDayOfWeek().getValue() - 1;
        LocalTime time = zoned.toLocalTime();

        List<SwitchPoint> switchPoints = schedules.get(todayIndex).getSwitchPoints();
        for (int i = switchPoints.size() - 1; i >= 0; i--) {
            if (time.compareTo(switchPoints.get(i).getTimeOfDay()) > 0) {
                return switchPoints.get(i).getheatSetpoint();
            }
        }

        // No temperature found, look for switch point in previous days
        for (int day = (todayIndex - 1) % 7; day != todayIndex; day = (day - 1) % 7) {
            if (day < 0) {
                day = day + 7;
            }
            if (schedules.get(day).getSwitchPoints().size() > 0) {
                return schedules.get(day).getSwitchPoints().get(schedules.get(day).getSwitchPoints().size() - 1)
                        .getheatSetpoint();
            }

        }

        // If still not found, return the temperature of the last switch point of zoned day
        if (switchPoints.size() > 0) {
            return switchPoints.get(switchPoints.size() - 1).getheatSetpoint();
        }

        return 0.0;
    }
}

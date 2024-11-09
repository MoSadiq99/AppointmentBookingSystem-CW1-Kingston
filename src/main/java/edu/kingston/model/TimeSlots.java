package edu.kingston.model;

import java.time.LocalTime;

public enum TimeSlots {

    MONDAY("Monday", "10:00 AM - 01:00 PM", LocalTime.of(10, 0), LocalTime.of(13, 0)),
    WEDNESDAY("Wednesday", "02:00 PM - 05:00 PM", LocalTime.of(14, 0), LocalTime.of(17, 0)),
    FRIDAY("Friday", "04:00 PM - 08:00 PM", LocalTime.of(16, 0), LocalTime.of(20, 0)),
    SATURDAY("Saturday", "09:00 AM - 01:00 PM", LocalTime.of(9, 0), LocalTime.of(13, 0));

    private final String day;
    private final String timeRange;
    private final LocalTime startTime;
    private final LocalTime endTime;

    TimeSlots(String day, String timeRange, LocalTime startTime, LocalTime endTime) {
        this.day = day;
        this.timeRange = timeRange;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }


    // Getter for day
    public String getDay() {
        return day;
    }

    // Getter for time range
    public String getTimeRange() {
        return timeRange;
    }

    @Override
    public String toString() {
        return day + ": " + timeRange;
    }

    //? Method to convert String to TimeSlots
    public static TimeSlots fromString(String text) {
        for (TimeSlots slot : TimeSlots.values()) {
            if (slot.day.equalsIgnoreCase(text)) {
                return slot;
            }
        }
        return null;
    }
}
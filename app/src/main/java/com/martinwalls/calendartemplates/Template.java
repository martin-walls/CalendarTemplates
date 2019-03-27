package com.martinwalls.calendartemplates;

public class Template {
    private String name;
    private String location;
    private String description;
    private Time startTime;
    private Time endTime;
    private Colour colour;

    public Template() {
    }

    public Template(String name, String location, String description, Time startTime, Time endTime, Colour colour) {
        this.name = name;
        this.location = location;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.colour = colour;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public Colour getColour() {
        return colour;
    }

    public void setColour(Colour colour) {
        this.colour = colour;
    }

    public static class Time {
        private String hour;
        private String minute;

        public Time() {
        }

        public Time(String hour, String minute) {
            this.hour = hour;
            this.minute = minute;
        }

        public String getHour() {
            return hour;
        }

        public void setHour(String hour) {
            this.hour = hour;
        }

        public String getMinute() {
            return minute;
        }

        public void setMinute(String minute) {
            this.minute = minute;
        }

        public String getTimeString() {
            return hour + ":" + minute;
        }
    }

    /**
     * Converts a time from a String, in the form HH:MM, to a Time object
     * @param timeString The String to convert
     * @return The timeString as a Time object
     */
    public static Time parseTime(String timeString) {
        if (timeString == null || timeString.length() != 5) {
            return null;
        }
        Time time = new Time();
        time.setHour(timeString.substring(0, 2));
        time.setMinute(timeString.substring(3));
        return time;
    }
}

package com.mabem.homebook.Model;

import java.util.Calendar;

enum Frequency {
    Never,
    Daily,
    Weekly,
    Monthly
}

public class Reminder {
    private String id = "";
    private String name = "";
    private Calendar date;
    private Frequency frequency;

    public Reminder(String id, String name, Frequency frequency, int year, int month, int day, int hour, int minute) {
        this.id = id;
        this.name = name;
        this.date.set(year, month, day, hour, minute);
        this.frequency = frequency;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Calendar getDate() {
        return date;
    }

    public Frequency getFrequency() {
        return frequency;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Reminder rm = (Reminder) o;
        return id.equals(rm.id);
    }
}

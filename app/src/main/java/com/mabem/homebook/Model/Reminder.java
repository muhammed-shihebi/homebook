package com.mabem.homebook.Model;

import java.util.Calendar;
import java.util.Date;
public class Reminder {

    public static final String ONCE = "Once";
    public static final String DAILY = "Daily";
    public static final String WEEKLY = "Weekly";
    public static final String MONTHLY = "Monthly";


    private String id = "";
    private String name = "";
    private Date date;
    private String frequency;

    public Reminder(String id, String name, String frequency, Date date) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.frequency = frequency;
    }

    public Reminder(String name, Date date, String frequency) {
        this.name = name;
        this.date = date;
        this.frequency = frequency;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Date getDate() {
        return date;
    }

    public String getFrequency() {
        return frequency;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Reminder rm = (Reminder) o;
        return id.equals(rm.id);
    }
}

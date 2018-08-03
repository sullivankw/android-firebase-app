package com.apps.ksullivan.firstfirebaseapp.model;

import java.util.EnumSet;

public enum Hobby {

    Cycling(1, "Cycling"),
    Hiking(2, "Hiking"),
    Reading(3, "Reading"),
    Running(4, "Running"),
    Kayaking(5, "Kayaking"),
    Swimming(6, "Swimming");

    private int id;
    private String item;

    Hobby(int id, String item) {
        this.id = id;
        this.item = item;
    }

    public int getId() {
        return id;
    }

    public String getItem() {
        return item;
    }

    public static Hobby fromItem(String item) {
        for (final Hobby element : EnumSet.allOf(Hobby.class)) {
            if (element.getItem().equalsIgnoreCase(item)) {
                return element;
            }
        }
        return null;
    }
}

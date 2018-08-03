package com.apps.ksullivan.firstfirebaseapp.model;

import java.util.EnumSet;

public enum Sort {

    Alphabetical(1, "Alphabetical", "name"),
    ReverseAlphabetical(2, "Reverse Alphabetical", "name"),
    AgeAscending(3, "Age Ascending", "age"),
    AgeDescending(4, "Age Descending", "age");

    private int id;
    private String item;
    private String column;

    Sort(int id, String item, String column) {
        this.id = id;
        this.item = item;
        this.column = column;
    }

    public int getId() {
        return id;
    }

    public String getItem() {
        return item;
    }

    public String getColumn() {
        return column;
    }

    public static Sort fromItem(String item) {
        for (final Sort element : EnumSet.allOf(Sort.class)) {
            if (element.getItem().equalsIgnoreCase(item)) {
                return element;
            }
        }
        return null;
    }
    public static Sort fromColumn(String column) {
        for (final Sort element : EnumSet.allOf(Sort.class)) {
            if (element.getColumn().equalsIgnoreCase(column)) {
                return element;
            }
        }
        return null;
    }
}

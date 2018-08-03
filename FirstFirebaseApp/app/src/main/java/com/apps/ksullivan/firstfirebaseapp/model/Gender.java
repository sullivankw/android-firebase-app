package com.apps.ksullivan.firstfirebaseapp.model;

import java.util.EnumSet;

public enum Gender {

    Male(1, "Male", "M"),
    Female(2, "Female", "F");

    private int id;
    private String item;
    private String code;

    Gender(int id, String item, String code) {
        this.id = id;
        this.item = item;
        this.code = code;
    }

    public int getId() {
        return id;
    }

    public String getItem() {
        return item;
    }

    public String getCode() {
        return code;
    }

    public static Gender fromItem(String item) {
        for (final Gender element : EnumSet.allOf(Gender.class)) {
            if (element.getItem().equalsIgnoreCase(item)) {
                return element;
            }
        }
        return null;
    }

    public static Gender fromCode(String code) {
        for (final Gender element : EnumSet.allOf(Gender.class)) {
            if (element.getCode().equalsIgnoreCase(code)) {
                return element;
            }
        }
        return null;
    }
}

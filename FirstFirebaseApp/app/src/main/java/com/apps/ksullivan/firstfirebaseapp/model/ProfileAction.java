package com.apps.ksullivan.firstfirebaseapp.model;

public enum ProfileAction {
    DELETE(1,"delete"),
    EDIT(2, "edit");

    private int id;
    private String item;

    ProfileAction(int id, String item) {
        this.id = id;
        this.item = item;
    }

    public String getItem() {
        return item;
    }
}

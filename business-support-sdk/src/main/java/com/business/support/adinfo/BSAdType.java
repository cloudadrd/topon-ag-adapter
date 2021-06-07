package com.business.support.adinfo;

public enum BSAdType {

    PANGLE("tt"),

    GDT("gdt");

    public String getName() {
        return name;
    }

    private String name;

    BSAdType(String name) {
        this.name = name;
    }
}

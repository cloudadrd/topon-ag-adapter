package com.business.support.adinfo;

public enum BSAdType {

    PANGLE("tt"),

    GDT("gdt"),

    KS("ks");

    public String getName() {
        return name;
    }

    private String name;

    BSAdType(String name) {
        this.name = name;
    }
}

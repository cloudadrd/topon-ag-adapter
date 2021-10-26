package com.business.support.adinfo;

public enum BSAdType {

    PANGLE("tt"),

    GDT("gdt"),

    KS("ks"),

    MV("mv"),

    OTHER("other");

    public String getName() {
        return name;
    }

    private final String name;

    BSAdType(String name) {
        this.name = name;
    }

    public static BSAdType get(String value) {
        if (value.equals(PANGLE.name)) {
            return PANGLE;
        } else if (value.equals(GDT.name)) {
            return GDT;
        } else if (value.equals(KS.name)) {
            return KS;
        } else if (value.equals(MV.name)) {
            return MV;
        } else if (value.equals(OTHER.name)) {
            return OTHER;
        }
        return null;
    }
}

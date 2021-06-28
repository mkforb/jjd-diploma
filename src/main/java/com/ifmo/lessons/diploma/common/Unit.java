package com.ifmo.lessons.diploma.common;

/**
 * Created by User on 25.06.2021.
 */
public enum Unit {
    PC("ШТ"),
    KG("КГ");

    private final String code;

    Unit(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}

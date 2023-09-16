package com.litvintsev.accounts.model;

import java.util.HashMap;
import java.util.Map;

public enum Source {

    MAIL("mail"),
    MOBILE("mobile"),
    BANK("bank"),
    GOSUSLUGI("gosuslugi");

    private static final Map<String, Source> BY_LABEL = new HashMap<>();
    public final String label;

    Source(String label) {
        this.label = label;
    }

    static {
        for (Source e: values())
            BY_LABEL.put(e.label, e);
    }

    public static Source valueOfLabel(String label) {
        return BY_LABEL.get(label);
    }
}


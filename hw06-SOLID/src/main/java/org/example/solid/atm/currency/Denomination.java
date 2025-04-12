package org.example.solid.atm.currency;

public enum Denomination {
    R_10(10),
    R_50(50),
    R_100(100),
    R_200(200),
    R_500(500),
    R_1000(1000);

    private final int value;

    Denomination(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static java.util.List<Denomination> getSortedDescending() {
        java.util.List<Denomination> list = new java.util.ArrayList<>(java.util.Arrays.asList(values()));
        list.sort((d1, d2) -> Integer.compare(d2.getValue(), d1.getValue()));
        return list;
    }
}

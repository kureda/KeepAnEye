package com.kureda.android.keepaneye.carer.sync;

public class Entry {

    public final String id;
    public final String name;
    public final String phone;
    final int report;
    final int login;
    final int walk;
    final int ride;

    public Entry(String id, String name, String phone, String report, String login,
                 String walk, String ride) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.report = JsonParser.parseInt(report);
        this.login = JsonParser.parseInt(login);
        this.walk = JsonParser.parseInt(walk);
        this.ride = JsonParser.parseInt(ride);
    }
}

package com.senacor.bitc.demo.rest.dto;

public enum LinkRelations {

    CUSTOMER("customer"),
    ADDRESS("address");

    private String name;

    LinkRelations(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

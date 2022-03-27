package com.crypto.enums;

public enum Environment {

    UAT("UAT_Environment"), PROD("PROD_Environment");

    private final String environmentName;

    Environment(String environmentName) {

        this.environmentName = environmentName;
    }

    public String getEnvironment() {
        return environmentName;
    }
}

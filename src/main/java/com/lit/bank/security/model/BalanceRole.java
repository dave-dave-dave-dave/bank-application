package com.lit.bank.security.model;

public enum BalanceRole {

    READER,
    EDITOR;

    public String toClaimValue() {
        return "balance-role:" + this.name().toLowerCase();
    }
}

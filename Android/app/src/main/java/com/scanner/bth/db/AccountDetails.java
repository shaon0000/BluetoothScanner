package com.scanner.bth.db;

/**
 * Created by shaon on 6/10/2015.
 */
public class AccountDetails {
    private boolean isAdmin;

    public AccountDetails(boolean b) {
        this.isAdmin = b;
    }

    public boolean getIsAdmin() {
        return isAdmin;
    }
}

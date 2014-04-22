/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bios.model;

/**
 *
 * @author Viet Huy
 */
public class Admin {

    private String adminid;
    private String password;

    /**
     * Generates an admin with the given variables
     *
     * @param username username of the admin
     * @param password password of the admin
     */
    public Admin(String username, String password) {
        this.adminid = username;
        this.password = password;
    }

    /**
     * Returns the username of the admin
     *
     * @return the username of the admin
     */
    public String getUsername() {
        return adminid;
    }

    /**
     * Returns the password of the admin
     *
     * @return the password of the admin
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password of the admin
     *
     * @param password the new password for the admin
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Sets the username of the admin
     *
     * @param username the new username for the admin
     */
    public void setUsername(String username) {
        this.adminid = username;
    }
}

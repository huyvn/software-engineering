/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bios.model;

/**
 *
 * @author Shane
 */
public class Bid {

    private String userid;
    private double amount;
    private String code;
    private String section;

    /**
     * Generates a Bid object with the given variables
     *
     * @param userID the id of the student
     * @param amount the amount that the student place
     * @param code the course name
     * @param section the section name
     * @return
     */
    public Bid(String userID, double amount, String code, String section) {
        this.userid = userID;
        this.amount = amount;
        this.code = code;
        this.section = section;
    }

    /**
     * Returns the userID of the student who placed the bid
     *
     * @return the id of the student
     */
    public String getUserID() {
        return userid;
    }

    /**
     * Returns the bid amount
     *
     * @return the amount that the student place
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Returns the course code that the bid is for
     *
     * @return the course name
     */
    public String getCode() {
        return code;
    }

    /**
     * Returns the section code that the bid is for
     *
     * @return the section name
     */
    public String getSection() {
        return section;
    }

    /**
     * Sets the userID of the bid
     *
     * @param userID the id of the student
     */
    public void setUserID(String userID) {
        this.userid = userID;
    }

    /**
     * Sets the bid amount
     *
     * @param amount the amount that the student place
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }

    /**
     * Sets the course code that the bid is for
     *
     * @param code the course name
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Sets the section code that the bid is for
     *
     * @param section the section name
     */
    public void setSection(String section) {
        this.section = section;
    }
}

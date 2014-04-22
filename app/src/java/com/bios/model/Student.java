/**
 * Student contains the student ID, password, name, school and eDollar the
 * student have
 */
package com.bios.model;

/**
 *
 * @author Shane
 */
public class Student {

    private String userid;
    private String password;
    private String name;
    private String school;
    private double edollar;

    /**
     * Creates a new student object that specified the userID, password, name,
     * school, eDollar
     *
     * @param userID the ID of the student
     * @param password the password of the student
     * @param name the name of the student
     * @param school the school the student attends
     * @param eDollar the amount of eDollars the student has
     *
     */
    public Student(String userID, String password, String name, String school, double eDollars) {
        this.userid = userID;
        this.password = password;
        this.name = name;
        this.school = school;
        this.edollar = eDollars;
    }

    /**
     * Generates a student with the following variables
     *
     * @param userID the userID of the student
     * @param password the password of the student
     */
    public Student(String userID, String password) {
        this.userid = userID;
        this.password = password;
    }

    /**
     * Gets the user ID of the student
     *
     * @return userID of the student
     */
    public String getUserID() {
        return userid;
    }

    /**
     * Gets the student password
     *
     * @return the password of the student
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets the name of the student
     *
     * @return the name of the student
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the school of the student
     *
     * @return the school of the student
     */
    public String getSchool() {
        return school;
    }

    /**
     * Gets the eDollar of the student
     *
     * @return the eDollar of the student
     */
    public double getEDollars() {
        return edollar;
    }

    /**
     * sets the userID of the student
     *
     * @param userID the student id
     */
    public void setUserID(String userID) {
        this.userid = userID;
    }

    /**
     * sets the password of the student
     *
     * @param password the password of the student
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * sets the name of the student
     *
     * @param name the name of the student
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * sets the school of the student
     *
     * @param school the name of the school the student belongs to
     */
    public void setSchool(String school) {
        this.school = school;
    }

    /**
     * sets the amount of the student
     *
     * @param eDollars the amount of money the student has
     */
    public void setEDollars(double eDollars) {
        this.edollar = eDollars;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bios.model;

/**
 *
 * @author Shane
 */
public class Prerequisite {

    private String course;
    private String prerequisite;

    /**
     * Generates a prerequisite for a specified course
     *
     * @param course the course name
     * @param prerequisite the prerequisite for the course
     */
    public Prerequisite(String course, String prerequisite) {
        this.course = course;
        this.prerequisite = prerequisite;
    }

    /**
     * Return the course code that the prerequisite is for
     *
     * @return the course name
     */
    public String getCourse() {
        return course;
    }

    /**
     * Return the prerequisite code for a course
     *
     * @return the prerequisite for the course
     */
    public String getPrerequisite() {
        return prerequisite;
    }

    /**
     * Set the course code that the prerequisite is for
     *
     * @param course the course name
     */
    public void setCourse(String course) {
        this.course = course;
    }

    /**
     * Set the prerequisite for a course
     *
     * @param prerequisite prerequisite for the course
     */
    public void setPrerequisite(String prerequisite) {
        this.prerequisite = prerequisite;
    }
}

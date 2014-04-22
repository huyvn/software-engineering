/**
 * CourseComplete contains the student ID and the course code for the completed
 * course
 */
package com.bios.model;

/**
 *
 * @author Shane
 */
public class CourseComplete {

    private String userid;
    private String code;

    /**
     * Creates a Course Completed object with the specified useID and course
     * code
     *
     * @param userID the id of the student
     * @param code the course code
     */
    public CourseComplete(String userID, String code) {
        this.userid = userID;
        this.code = code;
    }

    /**
     * Gets the userID of the student
     *
     * @return the student id
     */
    public String getUserID() {
        return userid;
    }

    /**
     * Gets the course code
     *
     * @return the course code
     */
    public String getCode() {
        return code;
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
     * sets the course code
     *
     * @param code the course code
     */
    public void setCode(String code) {
        this.code = code;
    }
}

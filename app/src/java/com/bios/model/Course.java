/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bios.model;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author Shane
 */
public class Course {

    private String course;
    private String school;
    private String title;
    private String description;
    private transient String exam_date;
    private transient String exam_start;
    private transient String exam_end;
    @SerializedName("exam date")
    private String jsonDate;
    @SerializedName("exam start")
    private String jsonStart;
    @SerializedName("exam end")
    private String jsonEnd;

    /**
     * Generates a Course with the specified variables
     *
     * @param course the course name
     * @param school the school name
     * @param title the title of the course
     * @param description the description of the course
     * @param exam_Date the exam date for the course
     * @param exam_Start the start time for the exam
     * @param exam_End the end time for the exam
     *
     */
    public Course(String code, String school, String title, String description, String exam_Date, String exam_Start, String exam_End) {
        this.course = code;
        this.school = school;
        this.title = title;
        this.description = description;
        this.exam_date = exam_Date;
        this.exam_start = exam_Start;
        this.exam_end = exam_End;

        this.jsonDate = exam_Date.replace("_", "");
        this.jsonStart = exam_start.replaceAll(":", "");
        this.jsonEnd = exam_end.replaceAll(":", "");
    }

    /**
     * Returns the course code
     *
     * @return the course code
     */
    public String getCode() {
        return course;
    }

    /**
     * Returns the school of the course
     *
     * @return the school name
     */
    public String getSchool() {
        return school;
    }

    /**
     * Returns the title of the course
     *
     * @return the title of the course
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the course description
     *
     * @return the description of the course
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the date of the course exam
     *
     * @return the exam date for the course
     */
    public String getExam_Date() {
        return exam_date;
    }

    /**
     * Returns the start time of the course exam
     *
     * @return the start time for the exam
     */
    public String getExam_Start() {
        return exam_start;
    }

    /**
     * Returns the end time of the course exam
     *
     * @return the end time for the exam
     */
    public String getExam_End() {
        return exam_end;
    }

    /**
     *
     * @param code the course name
     */
    public void setCode(String code) {
        this.course = code;
    }

    /**
     * Sets the name of the school of the course
     *
     * @param school the school name
     */
    public void setSchool(String school) {
        this.school = school;
    }

    /**
     * Sets the title of the course
     *
     * @param title the title of the course
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Sets the description of the course
     *
     * @param description the description of the course
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets the date of the course exam
     *
     * @param exam_Date the exam date for the course
     */
    public void setExam_Date(String exam_Date) {
        this.exam_date = exam_Date;
    }

    /**
     * Sets the start time of the course exam
     *
     * @param exam_Start the start time for the exam
     */
    public void setExam_Start(String exam_Start) {
        this.exam_start = exam_Start;
    }

    /**
     * Sets the end time of the course exam
     *
     * @param exam_End the end time for the exam
     */
    public void setExam_End(String exam_End) {
        this.exam_end = exam_End;
    }
}

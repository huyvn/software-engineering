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
public class Section {
    private String course;
    private String section;
    private transient int day;
    private transient String start;
    private transient String end;
    
    @SerializedName("day") private String jsonDay;
    @SerializedName("start") private String jsonStart;
    @SerializedName("end") private String jsonEnd;
    
    private String instructor;
    private String venue;
    private int size;
    
    /**
     * Generates a section object with the specified variables
     * @param course the course name
     * @param section the section name
     * @param day the integer value of the day
     * @param start the start time of the section
     * @param end the end time of the section
     * @param instructor the instructor for the section
     * @param venue the venue of the section
     * @param size the capacity of the section
     * 
     */
    public Section(String course, String section, int day, String start, String end, String instructor, String venue, int size){
        this.course = course;
        this.section = section;
        this.day = day;
        this.start = start;
        this.end = end;
        this.instructor = instructor;
        this.venue = venue;
        this.size = size;
        
        this.jsonStart = start.replaceAll(":", "");
        this.jsonEnd = end.replaceAll(":", "");
        switch(day){
            case 1:
                this.jsonDay = "Monday";
                break;
            case 2:
                this.jsonDay = "Tuesday";
                break;
            case 3:
                this.jsonDay = "Wednesday";
                break;
            case 4:
                this.jsonDay = "Thursday";
                break;
            case 5:
                this.jsonDay = "Friday";
                break;
            case 6:
                this.jsonDay = "Saturday";
                break;
            case 7:
                this.jsonDay = "Sunday";
                break;
            default:
                this.jsonDay = "Invalid Day";
                break;
        }
    }
    
    /**
     * Returns the course code of the given section
     * @return course name
     */
    public String getCourse(){
        return course;
    }
    
    /**
     * Returns the section code
     * @return section name
     */
    
    public String getSection(){
        return section;
    }
    
    /**
     * Returns the day of the section
     * @return a day as an integer
     */
    public int getDay(){
        return day;
    }
    
    /**
     * Returns the start time of the section
     * @return the start time of the section
     */
    public String getStart(){
        return start;
    }
    
    /**
     * Returns the end time of the section
     * @return the end time of the section
     */
    public String getEnd(){
        return end;
    }
    
    /**
     * Returns the name of the instructor of that section
     * @return the instructor of the section
     */
    public String getInstructor(){
        return instructor;
    }
    
    /**
     * Returns the venue of the section
     * @return the venue of the section
     */
    public String getVenue(){
        return venue;
    }
    
    /**
     * Returns the size of the section
     * @return the capacity of that section
     */
    public int getSize(){
        return size;
    }
    
    /**
     * Sets the course code of the section
     * @param course the course name 
     */
    public void setCourse(String course){
        this.course = course;
    }
    
    /**
     * Sets the section code for the course
     * @param section the section name
     */
    public void setSection(String section){
        this.section = section;
    }
    
    /**
     * Sets the day of the section
     * @param day the day of the section as an integer (e.g: Monday = 1, Tuesday = 2)
     */
    public void setDay(int day){
        this.day = day;
    }
    
    /**
     * Sets the starting time of the section
     * @param start the start time of the the section
     */
    public void setStart(String start){
        this.start = start;
    }
    
    /**
     * Sets the ending time of the section
     * @param end the end time of the section
     */
    public void setEnd(String end){
        this.end = end;
    }
    
    /**
     * Sets the instructor of the section
     * @param instructor the instructor of the section
     */
    public void setInstructor(String instructor){
        this.instructor = instructor;
    }
    
    /**
     * Sets the venue of the section
     * @param venue the venue of the section
     */
    public void setVenue(String venue){
        this.venue = venue;
    }
    
    /**
     * Sets the size of the section
     * @param size the capacity of the section
     */
    public void setSize(int size){
        this.size = size;
    }
    
}

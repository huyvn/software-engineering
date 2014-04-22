
package com.bios.dao;

import static com.bios.dao.sectionDAO.generateSectionList;
import com.bios.util.ConnectionManager;
import com.bios.model.Course;
import com.bios.model.Section;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 *
 * @author hpkhoo.2012
 */
public class courseDAO {

    private static List<Course> courseList = new ArrayList<Course>();

    /**
     * Create a new course object
     * @param code the course code
     * @param school the school name
     * @param title the title of the course
     * @param description the description of the course
     * @param exam_Date the exam date for the course
     * @param exam_Start the start time for the exam
     * @param exam_End the end time for the exam
     * @return a new course object
     */
    public static Course createCourse(String code, String school, String title, String description, String exam_Date, String exam_Start, String exam_End) {
        return new Course(code, school, title, description, exam_Date, exam_Start, exam_End);
    }

  
    /**
     * Adds a new course to the list of courses
     * @param newCourse a course that stores the information of course
     */
    public static void addCourse(Course newCourse) {
        courseList.add(newCourse);
    }

    /**
     * Generates the list of Courses by retrieving from the Course table from the database
     * @throws SQLException
     */
    public static void generateCourseList() throws SQLException {


        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        if (courseList != null) {
            courseList.clear();
        }

        try {
            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("SELECT * FROM COURSE ORDER BY COURSE");
            rs = stmt.executeQuery();

            while (rs.next()) {
                String course = rs.getString("course");
                String school = rs.getString("school");
                String title = rs.getString("title");
                String description = rs.getString("description");
                String examDate = rs.getString("examdate");
                String examStart = rs.getString("exam_start");
                String examEnd = rs.getString("exam_end");

                Course newCourse = createCourse(course, school, title, description, examDate, examStart, examEnd);
                addCourse(newCourse);

            }

        } catch (SQLException e) {
            throw e;
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
    }
    
     /**
     *Validate if a course exist in the database
     * @param course course code
     * @return boolean 
     * @throws SQLException 
     */
    public static boolean validateCourseByCode(String course) throws SQLException {

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("SELECT * FROM COURSE WHERE COURSE=" + "'" + course + "';");
            rs = stmt.executeQuery();
            while (!rs.next()) {
                return false;
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return true;
    }

    /**
     * Retrieves the list of courses
     *
     * @return get the course list
     * @throws SQLException
     */
    public static List<Course> retrieveAll()throws SQLException {
        try {
            generateCourseList();
        } catch (SQLException e) {
        throw new SQLException(e);
            
        }
        return courseList;
    }
    
    /**
    * Given a school code, this method returns a List of courses 
    * under the corresponding school.
    *
    * @param List<Course> school code eg. SIS, SOA, SOB
    * @return a list of completed courses
    * @throws SQLException
    */
    public static List<Course> retreiveCourseStudentSchool(String school) throws SQLException{
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        List<Course> courseByStudentSchool = new ArrayList<Course>();

        try {
            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("SELECT * FROM COURSE WHERE school=?");
            stmt.setString(1, school);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                String course = rs.getString("course");
                String sch = rs.getString("school");
                String title = rs.getString("title");
                String description = rs.getString("description");
                String examDate = rs.getString("examdate");
                String examStart = rs.getString("exam_start");
                String examEnd = rs.getString("exam_end");

                Course newCourse = createCourse(course, sch, title, description, examDate, examStart, examEnd);
               
                courseByStudentSchool.add(newCourse);
            }

        } catch (SQLException e) {
            throw e;
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return courseByStudentSchool;
    }
     
    /**
    * Given a school code, this method returns a List of courses 
    * other than the given.
    *
    * @param List<Course> school code eg. SIS, SOA, SOB
    * @return a list of completed courses
    *
    */
     public static List<Course> retreiveCourseStudentOtherSchool(String school) throws SQLException{
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        List<Course> otherCourseByStudent = new ArrayList<Course>();

        try {
            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("SELECT * FROM COURSE WHERE school<>?");
            stmt.setString(1, school);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                String course = rs.getString("course");
                String sch = rs.getString("school");
                String title = rs.getString("title");
                String description = rs.getString("description");
                String examDate = rs.getString("examdate");
                String examStart = rs.getString("exam_start");
                String examEnd = rs.getString("exam_end");

                Course newCourse = createCourse(course, sch, title, description, examDate, examStart, examEnd);
               
                otherCourseByStudent.add(newCourse);
            }

        } catch (SQLException e) {
            throw e;
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return otherCourseByStudent;
    }

    /**
    * Given a course code, this method returns the corresponding course object. 
    *
    * @param code
    * @return a course object
    * @throws SQLException
    */
     public static Course retreiveCourseByCode(String code) throws SQLException{
      
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        List<Course> courseByCode = new ArrayList<Course>();
        Course newCourse = null;
        try {
            conn = ConnectionManager.getConnection();
            
            stmt = conn.prepareStatement("SELECT * FROM COURSE WHERE course=?");
            stmt.setString(1, code);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                String course = rs.getString("course");
                String sch = rs.getString("school");
                String title = rs.getString("title");
                String description = rs.getString("description");
                String examDate = rs.getString("examdate");
                String examStart = rs.getString("exam_start");
                String examEnd = rs.getString("exam_end");

                newCourse = createCourse(course, sch, title, description, examDate, examStart, examEnd);
               
            }

        } catch (SQLException e) {
            throw e;
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return newCourse;
    }
     
    /**
    * Given a two courses, this method returns true when there is an overlap. 
    *
    * @param course
    * @param course
    * @return boolean, true when overlap occurs, else false. 
    * @throws SQLException
    */
    public static boolean checkOverlapExam(Course c1, Course c2) {

        boolean result = true;

        if (c1.getExam_Date().equals(c2.getExam_Date())) {

            String time1_start = c1.getExam_Start().replace(":", "");
            String time1_end = c1.getExam_End().replace(":", "");

            String time2_start = c2.getExam_Start().replace(":", "");
            String time2_end = c2.getExam_End().replace(":", "");

            int startTime_1 = Integer.parseInt(time1_start);
            int endTime_1 = Integer.parseInt(time1_end);

            int startTime_2 = Integer.parseInt(time2_start);
            int endTime_2 = Integer.parseInt(time2_end);

            if (startTime_2 > endTime_1 || startTime_1 > endTime_2) {
                result = false;
            }
        } else {
            result = false;
        }

        return result;

    }
    
   
}
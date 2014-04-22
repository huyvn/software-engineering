package com.bios.dao;

import static com.bios.dao.sectionDAO.generateSectionList;
import com.bios.model.CourseComplete;
import com.bios.util.ConnectionManager;
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
public class completedCourseDAO {

    private static List<CourseComplete> courseCompletedList = new ArrayList<CourseComplete>();

    /**
     * Create a new course completed object
     *
     * @param userID the id of the student
     * @param code the course code
     * @return a new course completed object
     */
    public static CourseComplete createCourseCompleted(String userID, String code) {
        return new CourseComplete(userID, code);
    }

    /**
     * Adds a new course completed object into to the list of course completed
     *
     * @param newCourseCompleted a courseComplete object that stores the
     * information of course completed such as userID and course code
     */
    public static void addCourseCompleted(CourseComplete newCourseCompleted) {
        courseCompletedList.add(newCourseCompleted);
    }

    /**
     * Generates a list of completed courses by retrieving from the
     * CourseCompleted table in the database
     *
     * @throws SQLException
     */
    public static void generateCourseCompletedList() throws SQLException {

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        if (courseCompletedList != null) {
            courseCompletedList.clear();
        }

        try {
            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("SELECT*FROM COURSE_COMPLETED ORDER BY code, userID");
            rs = stmt.executeQuery();
            while (rs.next()) {
                String studentID = rs.getString("userID");
                String courseID = rs.getString("code");


                CourseComplete newCourseCompleted = createCourseCompleted(studentID, courseID);

                addCourseCompleted(newCourseCompleted);
            }

        } catch (SQLException e) {
            throw e;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }

    /**
     * Returns the list of completed courses
     *
     * @return a list of completed courses
     * @throws SQLException
     */
    public static List<CourseComplete> retrieveAll() throws SQLException {
        try {
            generateCourseCompletedList();
        } catch (SQLException e) {
            throw new SQLException(e);

        }
        return courseCompletedList;
    }

    
     /**
     * Given a userid this method returns a list of completed courses
     * by that particular student.
     * 
     * @param userid userID eg. amy.ng.2009
     * @return a list of completed courses
     * @throws SQLException
     */
    public static List<CourseComplete> retrieveCourseCompletedByStudent(String userid) throws SQLException{

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        List<CourseComplete> courseCompleteStudentList= new ArrayList<CourseComplete>();

        try {
            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("SELECT code FROM COURSE_COMPLETED WHERE userID=?");
            stmt.setString(1, userid);
            rs = stmt.executeQuery();
            while (rs.next()) {

                String courseID = rs.getString("code");

                CourseComplete newCourseCompleted = createCourseCompleted(userid, courseID);

                courseCompleteStudentList.add(newCourseCompleted);
            }

        } catch (SQLException e) {
            throw e;
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }

        return courseCompleteStudentList;
    }
}

package com.bios.dao;

import static com.bios.dao.studentDAO.generateStudentList;
import com.bios.model.Section;
import com.bios.model.Student;
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
public class sectionDAO {

    private static List<Section> sectionList = new ArrayList<Section>();

    /**
     * Creates a new section object
     *
     * @param course the course code
     * @param section the section name
     * @param day the integer value of the day
     * @param start the start time of the section
     * @param end the end time of the section
     * @param instructor the instructor for the section
     * @param venue the venue of the section
     * @param size the capacity of the section
     * @return
     */
    public static Section createSection(String course, String section, int day, String start, String end, String instructor, String venue, int size) {
        return new Section(course, section, day, start, end, instructor, venue, size);
    }

    /**
     * Adds a new section to the list of sections
     *
     * @param newSection a section object that stores the information of section
     */
    public static void addSection(Section newSection) {
        sectionList.add(newSection);
    }

    /**
     * Generates a list of sections by retrieving from the Section table from
     * the database
     *
     * @throws SQLException
     */
    public static void generateSectionList() throws SQLException {

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        if (sectionList != null) {
            sectionList.clear();
        }

        try {
            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("SELECT * FROM SECTION ORDER BY course, section");
            rs = stmt.executeQuery();

            while (rs.next()) {
                String course = rs.getString("course");
                String section = rs.getString("section");
                int day = Integer.parseInt(rs.getString("day"));
                String start = rs.getString("start");
                String end = rs.getString("end");
                String instructor = rs.getString("instructor");
                String venue = rs.getString("venue");
                int size = Integer.parseInt(rs.getString("size"));

                Section newSection = createSection(course, section, day, start, end, instructor, venue, size);
                addSection(newSection);
            }


        } catch (SQLException e) {
            throw e;
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
    }

    /**
     * Returns a list of sections
     *
     * @return the list of sections
     * @throws SQLException
     */
    public static List<Section> retrieveAll() throws SQLException {
        try {
            generateSectionList();
        } catch (SQLException e) {
            throw new SQLException(e);

        }


        return sectionList;
    }

    /**
     * retrieve a Section of that particular course
     *
     * @param course the course code
     * @return section object
     * @throws SQLException
     */
    public static Section retrieveCourse(String course) throws SQLException {

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        Section retrieveCourse = null;

        try {
            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("SELECT * FROM SECTION WHERE COURSE=?");
            stmt.setString(1, course);

            rs = stmt.executeQuery();

            while (rs.next()) {
                String c = rs.getString("course");
                String sec = rs.getString("section");
                int day = Integer.parseInt(rs.getString("day"));
                String start = rs.getString("start");
                String end = rs.getString("end");
                String instructor = rs.getString("instructor");
                String venue = rs.getString("venue");
                int size = Integer.parseInt(rs.getString("size"));

                retrieveCourse = createSection(c, sec, day, start, end, instructor, venue, size);

            }


        } catch (SQLException e) {
            throw e;
        } finally {

            ConnectionManager.close(conn, stmt, rs);
        }

        return retrieveCourse;
    }

    /**
     * Retrieve a section object after specifying the section code
     *
     * @param section the section code
     * @return a section object
     * @throws SQLException
     */
    public static Section retrieveSection(String section) throws SQLException {

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        Section retrieveSection = null;

        try {
            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("SELECT * FROM SECTION WHERE SECTION=?");
            stmt.setString(1, section);

            rs = stmt.executeQuery();

            while (rs.next()) {
                String course = rs.getString("course");
                String sec = rs.getString("section");
                int day = Integer.parseInt(rs.getString("day"));
                String start = rs.getString("start");
                String end = rs.getString("end");
                String instructor = rs.getString("instructor");
                String venue = rs.getString("venue");
                int size = Integer.parseInt(rs.getString("size"));

                retrieveSection = createSection(course, sec, day, start, end, instructor, venue, size);

            }


        } catch (SQLException e) {
            throw e;
        } finally {

            ConnectionManager.close(conn, stmt, rs);
        }

        return retrieveSection;
    }

    /**
     * This method gets the list of student specified in that section
     *
     * @param course
     * @param section
     * @return ArrayList<LinkedHashMap<String, Object>> @throw
     * s SQLException
     */
    public static ArrayList<LinkedHashMap<String, Object>> getListOfStudentOfThatSection(String course, String section) throws SQLException {

        ArrayList<LinkedHashMap<String, Object>> studentSectionList = new ArrayList<LinkedHashMap<String, Object>>();

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("SELECT b.userID,b.amount "
                    + "FROM SECTION sec "
                    + "INNER JOIN BID b ON sec.course=b.code "
                    + "AND sec.section = b.section "
                    + "INNER JOIN STUDENT s ON b.userID=s.userID "
                    + "WHERE sec.course=? AND sec.section=? ORDER BY userID ");
            stmt.setString(1, course);
            stmt.setString(2, section);
            rs = stmt.executeQuery();

            while (rs.next()) {
                LinkedHashMap<String, Object> studentlinkedMap = new LinkedHashMap<String, Object>();
                String userID = rs.getString("userID");
                double amount = rs.getDouble("amount");

                studentlinkedMap.put("userid", userID);
                studentlinkedMap.put("amount", amount);
                studentSectionList.add(studentlinkedMap);

            }

        } catch (SQLException e) {
            throw e;
        } finally {

            ConnectionManager.close(conn, stmt, rs);
        }
        return studentSectionList;
    }

    /**
     * Return the size of the section
     *
     * @param cid course code
     * @param secID section id
     * @return Size of specific section
     * @throws SQLException
     */
    public static int retrieveSize(String cid, String secID) throws SQLException {

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int SizeOfSection = 0;

        try {
            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("SELECT * FROM SECTION WHERE course=" + "'" + cid + "'" + " AND section=" + "'" + secID + "'" + ";");
            rs = stmt.executeQuery();

            while (rs.next()) {
                SizeOfSection = rs.getInt("size");
            }

        } catch (SQLException e) {
            throw e;
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }

        return SizeOfSection;

    }

    /**
     * Validate if section ID exists for the particular
     *
     * @param course course code
     * @param section section ID
     * @return boolean
     * @throws SQLException course
     */
    public static boolean validateSection(String course, String section) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("SELECT * FROM SECTION WHERE COURSE=" + "'" + course + "' AND SECTION=" + "'" + section + "';");
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
     * Given a course ID return a list of Sections under it.
     *
     * @param course course code
     * @return a list of Sections , List<Section> 
     * @throws SQLException course
     */
    public static List<Section> retrieveSectionList(String course) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        List<Section> sectionListByCourse = new ArrayList<Section>();
        try {
            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("SELECT * FROM SECTION WHERE COURSE=?");
            stmt.setString(1, course);
            rs = stmt.executeQuery();
            while (rs.next()) {
                String courseCode = rs.getString("course");
                String section = rs.getString("section");
                int day = Integer.parseInt(rs.getString("day"));
                String start = rs.getString("start");
                String end = rs.getString("end");
                String instructor = rs.getString("instructor");
                String venue = rs.getString("venue");
                int size = Integer.parseInt(rs.getString("size"));

                Section newSection = createSection(courseCode, section, day, start, end, instructor, venue, size);
                sectionListByCourse.add(newSection);


            }
        } catch (SQLException e) {
            throw e;
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }

        return sectionListByCourse;
    }

    
     /**
     * Given a course and section code this method returns a the corresponding
     * bid in the section table. 
     *
     * @param course course code
     * @param section course code
     * @return a section object
     * @throws SQLException course
     */
    public static Section retrieveCourseSection(String course, String section) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Section retrieveSection = null;

        try {
            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("SELECT * FROM SECTION WHERE COURSE=? AND SECTION=? ");
            stmt.setString(1, course);
            stmt.setString(2, section);

            rs = stmt.executeQuery();

            while (rs.next()) {
                int day = Integer.parseInt(rs.getString("day"));
                String start = rs.getString("start");
                String end = rs.getString("end");
                String instructor = rs.getString("instructor");
                String venue = rs.getString("venue");
                int size = Integer.parseInt(rs.getString("size"));
                retrieveSection = createSection(course, section, day, start, end, instructor, venue, size);
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return retrieveSection;
    }

    
     /**
     * Given a course and section code this method returns a the corresponding
     * bid in the section table. 
     *
     * @param Section first section object for comparison
     * @param Section second section object for comparison
     * @return boolean true if overlap, false otherwise. 
     * @throws SQLException course
     */
    public static boolean checkOverlapClass(Section s1, Section s2) {

        boolean result = true;

        if (s1.getDay() == s2.getDay()) {

            String time1_start = s1.getStart().replace(":", "");
            String time1_end = s1.getEnd().replace(":", "");

            String time2_start = s2.getStart().replace(":", "");
            String time2_end = s2.getEnd().replace(":", "");

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
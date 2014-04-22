/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bios.dao;

import com.bios.model.*;
import com.bios.util.*;
import java.sql.*;
import java.util.*;

public class SectionStudentDAO {

    /**
     * Add a successful bid into table section-student
     *
     * @param userid the id of the student
     * @param course the course code
     * @param section the section code
     * @param amount the amount that the student placed
     * @throws SQLException
     */
    public static void addSuccessfulBid(String userid, String course, String section, double amount) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConnectionManager.getConnection();
            String updateSuccessfulBid = "INSERT INTO SECTION_STUDENT (userid,course,section,amount) VALUES (?,?,?,?)";

            stmt = conn.prepareStatement(updateSuccessfulBid);
            stmt.setString(1, userid);
            stmt.setString(2, course);
            stmt.setString(3, section);
            stmt.setDouble(4, amount);

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw e;
        } finally {
            ConnectionManager.close(conn, stmt);
        }
    }
    
    /**
     * Returns the list of successful bids made by a selected student
     * 
     * @param userid the id of the student
     * @return the bidding list
     * @throws SQLException
     */
    public static List<Bid> retrieveBidsByStudent(String userid) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        List<Bid> bidStudentList = new ArrayList<Bid>();

        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement("SELECT amount, course, section FROM SECTION_STUDENT WHERE userid = ?");
            stmt.setString(1, userid);
            rs = stmt.executeQuery();

            while (rs.next()) {
                double amount = Double.parseDouble(rs.getString("amount"));
                String course = rs.getString("course");
                String section = rs.getString("section");

                Bid newBid = bidDAO.createBid(userid, amount, course, section);
                bidStudentList.add(newBid);
            }
            return bidStudentList;
        } catch (SQLException e) {
            throw e;
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
    }

    /**
    * Given a specific course and section,
    * this method retrieves all bids made in the Section_Student table.
    * Section_Student Table contains all the successful bids at the end
    * of every clearing round logic.
    *
    * @param course the course code
    * @param section the section code
    * @return List of Bids
    * @throws SQLException
    */
    public static List<Bid> retrieveBidsBySection(String course, String section) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        List<Bid> bidSectionList = new ArrayList<Bid>();

        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement("SELECT userID, amount FROM SECTION_STUDENT "
                    + "WHERE `course` = ? AND `section` =? ORDER BY userID" );
            stmt.setString(1, course);
            stmt.setString(2, section);
            rs = stmt.executeQuery();

            while (rs.next()) {
                double amount = Double.parseDouble(rs.getString("amount"));
                String userid = rs.getString("userID");
                Bid newBid = bidDAO.createBid(userid, amount, course, section);
                bidSectionList.add(newBid);
            }
            return bidSectionList;
        } catch (SQLException e) {
            throw e;
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
    }
    
    /**
    * Given a specific userID and course code,
    * This method retrieves successful bids made by that student for that particular course.
    * This method only retrieves from Section_Student table (Round 1 only)
    * This method returns null when nothing is found. 
    *
    * @param userID the unique student ID eg. amy.ng.2009
    * @param course unique course code. eg. IS200
    * @return the Bid Object
    * @throws SQLException
    */
    public static Bid retrieveBidsByStudentCourse(String userID, String course) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Bid bidToReturn = null;
        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM SECTION_STUDENT "
                    + "WHERE userID=? AND course = ?");
            stmt.setString(1, userID);
            stmt.setString(2, course);
            rs = stmt.executeQuery();

            while (rs.next()) {
                double amount = Double.parseDouble(rs.getString("amount"));
                String section = rs.getString("section");
                Bid newBid = bidDAO.createBid(userID, amount, course, section);
                bidToReturn = newBid;
            }
            return bidToReturn;
        } catch (SQLException e) {
            throw e;
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
    }
    
    /**
     * This method returns all successful bids after round 1 or 2 is cleared. 
     * Note: No sorting is done in this method. 
     * Returns null when table is empty. 
     * 
     * @return the bidding list
     * @throws SQLException
     */
    public static List<Bid> retrieveAll() throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        List<Bid> bidList = new ArrayList<Bid>();

        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM SECTION_STUDENT ORDER BY course, userID");
            rs = stmt.executeQuery();

            while (rs.next()) {
                String userid = rs.getString("userid");
                double amount = Double.parseDouble(rs.getString("amount"));
                String course = rs.getString("course");
                String section = rs.getString("section");

                Bid newBid = bidDAO.createBid(userid, amount, course, section);
                bidList.add(newBid);
            }
            return bidList;
        } catch (SQLException e) {
            throw e;
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
    }
  
    /**
    * Given a specific student and courseCode,
    * this method removes that particular bid from Section_Student table.
    * Note: Section_Student Table contains all the successful bids at the end
    * of every clearing round logic.
    *
    * @param student the unique student ID eg. amy.ng.2009
    * @param courseCode unique course code. eg. IS200
    * @throws SQLException
    */
    public static void dropStudentSection(Student student, String courseCode) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConnectionManager.getConnection();
            String updateSuccessfulBid = "DELETE FROM SECTION_STUDENT WHERE userID=? AND course=?";

            stmt = conn.prepareStatement(updateSuccessfulBid);
            stmt.setString(1, student.getUserID());
            stmt.setString(2, courseCode);
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw e;
        } finally {
            ConnectionManager.close(conn, stmt);
        }
        
    }   
    
}

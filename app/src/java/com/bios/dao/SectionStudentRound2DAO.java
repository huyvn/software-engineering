/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bios.dao;

import com.bios.model.Bid;
import com.bios.model.Student;
import com.bios.util.ConnectionManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Meng Yun
 */
public class SectionStudentRound2DAO {

    public static void createSectionStudentRound2Table() throws SQLException {
        Connection conn = null;
        Statement stmt = null;
        try {

            conn = ConnectionManager.getConnection();
            stmt = conn.createStatement();

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS SECTION_STUDENT_ROUND2("
                    + "`userID` VARCHAR(100) NOT NULL,"
                    + "`course` VARCHAR(100) NOT NULL,"
                    + "`section` VARCHAR(100) NOT NULL,"
                    + "`amount` DECIMAL(10,2),"
                    + "PRIMARY KEY (`userID`, `course`, `section`));");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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
            String updateSuccessfulBid = "INSERT INTO SECTION_STUDENT_ROUND2 (userid,course,section,amount) VALUES (?,?,?,?);";

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
            stmt = conn.prepareStatement("SELECT amount, course, section FROM SECTION_STUDENT_ROUND2 WHERE userid = ?;");
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
     * Given a userID and course code, this method returns a bid from
     * Section_Student2 Table. All successful bids during round 2 will be 
     * placed into Section_Student2 table. Returns null when no result is found.
     * 
     * @param userid the id of the student
     * @return a bid object
     * @throws SQLException
     */
    public static Bid retrieveBidsByStudentCourse(String userID, String course) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Bid bidToReturn = null;
        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM SECTION_STUDENT_ROUND2 "
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
     * Given a course id and section this method
     * returns the list of bids for the given course and section in the 
     * Section_Student2 table. Results are sorted according to bid amount
     * (Highest to lowest). 
     *
     * @param userid the id of the student
     * @return the bidding list
     * @throws SQLException
     */
    public static List<Bid> retrieveBidsBySection(String course, String section) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        List<Bid> bidSectionList = new ArrayList<Bid>();

        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement("SELECT userID, amount FROM SECTION_STUDENT_ROUND2 "
                    + "WHERE `course` = ? AND `section` =? ORDER BY amount DESC;");
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
     * Returns the whole list of successful bids in the Section_Student2 table
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
            stmt = conn.prepareStatement("SELECT * FROM SECTION_STUDENT_ROUND2;");
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
     * Given a userID, a course ID and section this method
     * deletes the corresponding bid in the Section_Student2 table. 
     *
     * @param userid the id of the student
     * @param code the code id eg. IS200
     * @param section course section eg. S1, S2, S4
     * @throws SQLException
     */
    public static void deleteSuccessfulBid(String userID, String code, String sec) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement("DELETE FROM SECTION_STUDENT_ROUND2"
                    + " WHERE userID = ?"
                    + " AND course = ?"
                    + " AND section = ?;");
            stmt.setString(1, userID);
            stmt.setString(2, code);
            stmt.setString(3, sec);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw e;
        } finally {
            ConnectionManager.close(conn, stmt);
        }
    }
    
    /**
     * Given a userID and a course id this method
     * deletes the corresponding bid in the Section_Student2 table. 
     *
     * @param userid the id of the student
     * @param code the code id eg. IS200
     * @throws SQLException
     */
    public static void dropStudentSection(Student student, String courseCode) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConnectionManager.getConnection();
            String updateSuccessfulBid = "DELETE FROM SECTION_STUDENT_ROUND2 WHERE userID=? AND course=?";

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
    
    /**
     * This method simply deletes all records from Section_Student2 table. 
     *
     * @throws SQLException
     */
    public static void deleteAll() throws SQLException{
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement("DELETE FROM SECTION_STUDENT_ROUND2;");
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw e;
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }

    }
}

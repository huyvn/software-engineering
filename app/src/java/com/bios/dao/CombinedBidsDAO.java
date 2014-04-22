/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bios.dao;

import com.bios.model.Bid;
import com.bios.util.ConnectionManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author andreng
 */
public class CombinedBidsDAO {
    
    
    /**
        * Given a specific courseid and section,
        * this method retrieves all bids made in that courseid and section
        * (Successful or not) from round 1 and 2. 
        * This method also sorts the results
        * in order of amount (Highest to lowest) followed by 
        * userid in (alphabetical order).
        *
        * @param course the course code
        * @param section the section code
        * @throws SQLException
        */
    
    public static List<Bid> retrieveBidsByCourseSection(String course, String section) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        List<Bid> bidSectionList = new ArrayList<Bid>();

        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement("SELECT userID, code AS course, section, amount FROM BID "
                    + "WHERE code = ? AND section = ? UNION SELECT * FROM SECTION_STUDENT "
                    + "WHERE course = ? AND section = ? UNION SELECT * FROM SECTION_STUDENT_ROUND2  "
                    + "WHERE course = ? AND section = ? ORDER by amount DESC, userID ASC;");
            stmt.setString(1, course);
            stmt.setString(2, section);
            stmt.setString(3, course);
            stmt.setString(4, section);
            stmt.setString(5, course);
            stmt.setString(6, section);
            rs = stmt.executeQuery();

            while (rs.next()) {
                double amount = Double.parseDouble(rs.getString("amount"));
                String userid = rs.getString("userID");
                Bid newBid = bidDAO.createBid(userid, amount, course, section);
                bidSectionList.add(newBid);
            }
            
        } catch (SQLException e) {
            throw e;
        } finally {
            ConnectionManager.close(conn, stmt, rs);
            return bidSectionList;
        }
    }
             
    
    /**
        * Given a specific courseid and section,
        * this method retrieves all bids made in that courseid and section
        * (Successful or not) from round 2. 
        * This method also sorts the results
        * in order of amount (Highest to lowest) followed by 
        * userid in (alphabetical order).
        *
        * @param course the course code
        * @param section the section code
        * @throws SQLException
        */
    
    public static List<Bid> retrieveBidsByCourseSectionRound2(String course, String section) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        List<Bid> bidSectionList = new ArrayList<Bid>();

        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement("SELECT userID, code AS course, section, amount FROM BID "
                    + "WHERE code = ? AND section = ? UNION SELECT * FROM SECTION_STUDENT_ROUND2 "
                    + "WHERE course = ? AND section = ? ORDER by amount DESC, userID ASC;");
            stmt.setString(1, course);
            stmt.setString(2, section);
            stmt.setString(3, course);
            stmt.setString(4, section);
            stmt.setString(5, course);
            stmt.setString(6, section);
            rs = stmt.executeQuery();

            while (rs.next()) {
                double amount = Double.parseDouble(rs.getString("amount"));
                String userid = rs.getString("userID");
                Bid newBid = bidDAO.createBid(userid, amount, course, section);
                bidSectionList.add(newBid);
            }
            
        } catch (SQLException e) {
            throw e;
        } finally {
            ConnectionManager.close(conn, stmt, rs);
            return bidSectionList;
        }
    }
    
    /**
    * This method retrieves ALL bids made (Successful or not)
    * from round 1 and 2. This method also sorts the results
    * in order of amount (Highest to lowest) followed by 
    * userid in (alphabetical order).

    * @throws SQLException
    */     
        
    public static List<Bid> retrieveAll() throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        List<Bid> bidSectionList = new ArrayList<Bid>();

        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement("SELECT userID, code AS course, section, amount FROM BID "
                    + " UNION SELECT * FROM SECTION_STUDENT"
                    + " UNION SELECT * FROM SECTION_STUDENT_ROUND2"
                    + " ORDER by amount DESC, userID ASC;");

            rs = stmt.executeQuery();

            while (rs.next()) {
                double amount = Double.parseDouble(rs.getString("amount"));
                String userid = rs.getString("userID");
                String course = rs.getString("course");
                String section = rs.getString("section");
                Bid newBid = bidDAO.createBid(userid, amount, course, section);
                bidSectionList.add(newBid);
            }
            
        } catch (SQLException e) {
            throw e;
        } finally {
            ConnectionManager.close(conn, stmt, rs);
            return bidSectionList;
        }
    }     

}

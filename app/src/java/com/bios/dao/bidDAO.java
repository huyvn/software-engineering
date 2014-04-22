package com.bios.dao;

import static com.bios.dao.sectionDAO.generateSectionList;
import com.bios.util.ConnectionManager;
import com.bios.model.Bid;

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
public class bidDAO {

    private static List<Bid> bidList = new ArrayList<Bid>();
    private static List<Bid> SelectUpdateBidList = new ArrayList<Bid>();

    /**
     * Creates a new bid object
     *
     * @param userID the id of the student
     * @param amount the amount that the student place
     * @param code the course name
     * @param section the section name
     * @return the new Bid object
     */
    public static Bid createBid(String userID, double amount, String code, String section) {
        return new Bid(userID, amount, code, section);
    }

    /**
     * Adds a new Bid object into to the list of bid
     *
     * @param newBid a Bid object that stores the information of bid
     */
    public static void addBid(Bid newBid) {
        bidList.add(newBid);
    }

    /**
     * Creates the List of Bids by retrieving from the BID table in the database
     *
     * @throws SQLException
     */
    public static void generateBidList() throws SQLException {

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        if (bidList != null) {
            bidList.clear();
        }

        try {
            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("SELECT * FROM BID ORDER BY code, section, amount DESC, userID");
            rs = stmt.executeQuery();

            while (rs.next()) {


                String userID = rs.getString("userID");
                double amount = Double.parseDouble(rs.getString("amount"));
                String code = rs.getString("code");
                String section = rs.getString("section");

                Bid newBid = createBid(userID, amount, code, section);

                addBid(newBid);
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            ConnectionManager.close(conn, stmt, rs);

        }
    }

    /**
     * Get courseID and section as parameters Retrieve all bids from database
     * where courseID and section matched Based on retrieved bids, create new
     * bids Assign them into bidList and return it
     *
     * @param cID coure code
     * @param sec section code
     * @return List<Bid> bidList
     * @throws SQLException
     *
     */
    public static List<Bid> searchBidEntries(String cID, String sec) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        if (bidList != null) {
            bidList.clear();
        }

        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM BID WHERE code =" + "'" + cID + "'" + " AND section =" + "'" + sec + "'" + " ORDER BY code, amount DESC, userID ASC ;");
            rs = stmt.executeQuery();

            while (rs.next()) {

                String userID = rs.getString("userID");
                double amount = Double.parseDouble(rs.getString("amount"));
                String code = rs.getString("code");
                String section = rs.getString("section");

                Bid newBid = createBid(userID, amount, code, section);

                bidList.add(newBid);
            }


        } catch (SQLException e) {
            throw e;
        } finally {
            ConnectionManager.close(conn, stmt, rs);

        }

        return bidList;

    }

    /**
     * Returns the whole list of bids
     *
     * @return the bidding list
     * @throws SQLException
     */
    public static List<Bid> retrieveAll() throws SQLException {
        try {
            generateBidList();
        } catch (SQLException e) {
            throw new SQLException(e);

        }
        return bidList;
    }
    
    /**
     * Given a userid, this method returns a list of Bids for the corresponding userid
     *
     * @param userid , user id of the student. Eg. amy.ng.2012
     * @return List<Bid> the bidding list
     * @throws SQLException
     */
    public static List<Bid> retrieveBidByStudent(String userid) throws SQLException {

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        List<Bid> bidStudentList = new ArrayList<Bid>();

        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement("SELECT amount, code, section FROM BID WHERE userID=?");
            stmt.setString(1, userid);
            rs = stmt.executeQuery();

            while (rs.next()) {


                double amount = Double.parseDouble(rs.getString("amount"));
                String code = rs.getString("code");
                String section = rs.getString("section");

                Bid newBid = createBid(userid, amount, code, section);

                bidStudentList.add(newBid);
            }


        } catch (SQLException e) {
            throw e;
        } finally {
            ConnectionManager.close(conn, stmt, rs);

        }

        return bidStudentList;
    }
  
     /**
     * Given a userid, section id and code id 
     * this method deletes the corresponding bid from the bid table. 
     *
     * @param userid , user id of the student. Eg. amy.ng.2012
     * @param section , section id eg. S1, S2, S3
     * @param code , code id eg. IS100, IS200
     * @throws SQLException
     */
    public static void deleteBid(String ID, String sec, String code) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement("DELETE FROM BID"
                    + " WHERE userID = '" + ID + "'"
                    + " AND code = '" + code + "'"
                    + " AND section = '" + sec + "'");
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw e;
        } finally {
            ConnectionManager.close(conn, stmt);
        }
    }
  
     /**
     * Given a userid, section id and code id 
     * this method deleted the corresponding bid from the bid table. 
     *
     * @param userid , user id of the student. Eg. amy.ng.2012
     * @param section , section id eg. S1, S2, S3
     * @param code , code id eg. IS100, IS200
     * @throws SQLException
     */
    public static List<Bid> retrieveSelectedUpdateBid(String userid, String[] courseCode) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        if (SelectUpdateBidList != null) {
            SelectUpdateBidList.clear();
        }

        try {
            conn = ConnectionManager.getConnection();

            for (int i = 0; i < courseCode.length; i++) {
                stmt = conn.prepareStatement("SELECT * FROM BID WHERE userID=? and code=?");
                stmt.setString(1, userid);
                stmt.setString(2, courseCode[i]);
                rs = stmt.executeQuery();

                while (rs.next()) {

                    double amount = Double.parseDouble(rs.getString("amount"));
                    String codes = rs.getString("code");
                    String section = rs.getString("section");

                    Bid newBid = createBid(userid, amount, codes, section);

                    SelectUpdateBidList.add(newBid);
                }
            }

        } catch (SQLException e) {
            throw e;
        } finally {
            ConnectionManager.close(conn, stmt, rs);

        }
        return SelectUpdateBidList;
    }

    
     /**
      * Gets a List of Updated Bids
      * @return list of bids
      */
    public static List<Bid> getListOfUpdateBids() {
        return SelectUpdateBidList;
    }

    /**
     * Gets a list of bids that match the student and course ID
     * @param userid user ID of the student
     * @param code the course ID to use
     * @return a list of bids that have the student and course ID
     * @throws SQLException 
     */
    public static List<Bid> retrieveBidByStudentCourse(String userid, String code) throws SQLException {

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        List<Bid> studentCourseBidList = new ArrayList<Bid>();


        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement("SELECT amount, code, section FROM BID WHERE userID=? and code=?");
            stmt.setString(1, userid);
            stmt.setString(2, code);
            rs = stmt.executeQuery();

            while (rs.next()) {


                double amount = Double.parseDouble(rs.getString("amount"));
                String courseCode = rs.getString("code");
                String section = rs.getString("section");

                Bid newBid = createBid(userid, amount, courseCode, section);

                studentCourseBidList.add(newBid);
            }


        } catch (SQLException e) {
            throw e;
        } finally {
            ConnectionManager.close(conn, stmt, rs);

        }
        return studentCourseBidList;
    }

    /**
     * Given a userid, an array of string and List of bids 
     * this method Updates the list of given bids to the corresponding 
     * bid value in the array. 
     * 
     * @param userid user id of the student
     * @param newBidAmt an array of bid amount
     * @param bidToBeUpdated a list of bids to be updated. 
     * @throws SQLException 
     */
    public static void updateStudentBid(String userid, String[] newBidAmt, List<Bid> bidToBeUpdated) throws SQLException {

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = ConnectionManager.getConnection();

            for (int i = 0; i < bidToBeUpdated.size(); i++) {
                stmt = conn.prepareStatement("UPDATE BID SET amount=? WHERE userID=? AND code=?");
                stmt.setDouble(1, Double.parseDouble(newBidAmt[i]));
                stmt.setString(2, userid);
                stmt.setString(3, bidToBeUpdated.get(i).getCode());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            ConnectionManager.close(conn, stmt);

        }
    }
    
    
    /**
     * Inserts a bid entry in the bid table. 
     * @param userId eg. amy.ng.2009    
     * @param amount in double, bid amount
     * @param code code id, eg. IS200
     * @param section section id, eg. S1
     * @throws SQLException 
     */
    public static void insertStudentBid(String userId, double amount, String code, String section) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement("INSERT INTO BID (userID, amount, code, section) VALUES(?,?,?,?)");
            stmt.setString(1, userId);
            stmt.setDouble(2, amount);
            stmt.setString(3, code);
            stmt.setString(4, section);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw e;
        } finally {
            ConnectionManager.close(conn, stmt);

        }
    }
        
    
    /**
     * Updates the corresponding bid in the bid table.
     * @param userid
     * @param newBidAmt
     * @param course
     * @param section
     * @throws SQLException 
     */
    
    public static void updateSingleBid(String userid, double newBidAmt, String course, String section) throws SQLException {

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = ConnectionManager.getConnection();

                stmt = conn.prepareStatement("UPDATE BID SET amount=?, section=? WHERE userID=? AND code=? ");
                stmt.setDouble(1, newBidAmt);
                stmt.setString(2, section);
                stmt.setString(3, userid);
                stmt.setString(4, course);
                stmt.executeUpdate();
       
        } catch (SQLException e) {
            throw e;
        } finally {
            ConnectionManager.close(conn, stmt);
        }
    }
    
/**
 * this method deletes all entries from BID table.
 * @throws SQLException 
 */
    
    public static void deleteAll() throws SQLException{
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        List<Bid> bidStudentList = new ArrayList<Bid>();

        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement("DELETE FROM BID");
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw e;
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }

    }
       
    
   /**
    * This method returns a bid object from the Bid table based on the 
    * corresponding parameter inputs. 
    * 
    * @param userid
    * @param course
    * @param section
    * @return Bid object
    * @throws SQLException 
    */ 
    
    public static Bid retrieveSingleBid(String userid, String course) throws SQLException {

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;


        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM BID WHERE userID=? AND code =?" /*AND section =?*/);
            stmt.setString(1, userid);
            stmt.setString(2, course);
//            stmt.setString(3, section);
            rs = stmt.executeQuery();

            while (rs.next()) {
                double amount = Double.parseDouble(rs.getString("amount"));
                String code = rs.getString("code");
                String section_bid = rs.getString("section");
                String userid_bid = rs.getString("userid");

                return createBid(userid_bid, amount, code, section_bid);

            }
        } catch (SQLException e) {
            throw e;
        } finally {
            ConnectionManager.close(conn, stmt, rs);

        }
        return null;
    }
    
    
}//end of bidDAO
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bios.dao;

import com.bios.model.Bid;
import com.bios.util.ConnectionManager;
import com.bios.model.Student;

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
public class studentDAO {

    private static List<Student> studentList = new ArrayList<Student>();

    /**
     * Creates a new student object
     *
     * @param userID the ID of the student
     * @param password the password of the student
     * @param name the name of the student
     * @param school the school the student attends
     * @param eDollar the amount of eDollars the student has
     * @return a new student object
     */
    public static Student createStudent(String userID, String password, String name, String school, double eDollar) {
        return new Student(userID, password, name, school, eDollar);
    }

    /**
     * Adds a new student to the student list
     *
     * @param newStudent a linked hash map that stores the information of
     * student
     */
    public static void addStudent(Student newStudent) {
        studentList.add(newStudent);
    }

    /**
     * Generates a list of student by retrieving from the Student table from the
     * database
     *
     * @throws SQLException
     */
    public static void generateStudentList() throws SQLException {

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;


        if (studentList != null) {
            studentList.clear();
        }

        try {

            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("SELECT * FROM STUDENT ORDER BY userID");
            rs = stmt.executeQuery();


            while (rs.next()) {

                String userID = rs.getString("userID");
                String password = rs.getString("password");
                String name = rs.getString("name");
                String school = rs.getString("school");
                double eDollar = Double.parseDouble(rs.getString("edollar"));
               
                
                Student newStudent = createStudent(userID, password, name, school, eDollar);
                addStudent(newStudent);

            }

        } catch (SQLException e) {
            throw e;
        } finally {
          
            ConnectionManager.close(conn,stmt,rs);
        }
    }

    /**
     * Retrieves the list of students
     *
     * @return the list of students generated
     * @throws SQLException
     */
    public static List<Student> retrieveAll() throws SQLException {
        try {
            generateStudentList();
        } catch (SQLException e) {
            // create andre in event of failure.
            throw new SQLException(e);

        }
        return studentList;
    }

    /**
     * Uses a Student ID to retrieve the desired Student object
     *
     * @param studentID Student ID
     * @return a Student Object
     * @throws SQLException
     */
    public static Student retrieveStudentByUserid(String studentID) throws SQLException {

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Student newStudent = null;

        try {

            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("SELECT * FROM STUDENT WHERE userID=? ORDER BY userID ");
            stmt.setString(1, studentID);
            rs = stmt.executeQuery();


            while (rs.next()) {

                String userID = rs.getString("userID");
                String password = rs.getString("password");
                String name = rs.getString("name");
                String school = rs.getString("school");
                double eDollar = Double.parseDouble(rs.getString("edollar"));
                
                newStudent = createStudent(userID, password, name, school, eDollar);

            }

            return newStudent;

        } catch (SQLException e) {
            throw e;
        } finally {
            ConnectionManager.close(conn, stmt, rs);

        }
    }

    /**
     * Uses a Student ID and amount to refund the Student
     *
     * @param studentID Student to be refunded
     * @param Amount Amount to be refunded
     * @return nothing
     * @throws SQL Exception
     */
    public static void refundMoney(String studentID, double amount) throws SQLException {

        Connection conn = null;
        PreparedStatement stmt = null;

        try {

            conn = ConnectionManager.getConnection();
//PDATE `some_table` SET `value` = `value` + 1000 WHERE `id` = 1
            stmt = conn.prepareStatement("UPDATE STUDENT SET `edollar`"
                    + " = `edollar` + ? WHERE `userID` = ?");
            stmt.setDouble(1, amount);
            stmt.setString(2, studentID);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw e;
        } finally {
            ConnectionManager.close(conn, stmt);

        }
    }

    /**
     * Given a student ID this method retrieves the amount balance
     * for that particular student. 
     *
     * @param studentID Student to be refunded
     * @return amount in Double
     * @throws SQL Exception
     */
    public static double retrieveStudentEdollar(Student stu) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        double eDollar = 0;

        try {

            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("SELECT edollar FROM STUDENT WHERE userID=?");
            stmt.setString(1, stu.getUserID());
            rs = stmt.executeQuery();


            while (rs.next()) {

                eDollar = Double.parseDouble(rs.getString("edollar"));

            }

            return eDollar;

        } catch (SQLException e) {
            throw e;
        } finally {
            ConnectionManager.close(conn, stmt, rs);

        }
    }

    /**
     * Given a student ID , a new bid amount and a list of Bids,
     * this method will make sure that existing bids are updated
     * with the NewBidAmt. 
     *
     * @param studentID Student to be refunded
     * @param Amount Amounts to be updated.
     * @param listOfBids list of bids List<Bids>
     * @throws SQL Exception
     */
    public static void studentBalanceAfterDeduction(Student stu, double NewBidAmt, List<Bid> listOfOldBids) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        double studentOldBalance = retrieveStudentEdollar(stu);
        double studentNewBalance = 0;
        double oldBidAmt = 0;

        for (int i = 0; i < listOfOldBids.size(); i++) {
            oldBidAmt += listOfOldBids.get(i).getAmount();
        }

        if (NewBidAmt > oldBidAmt || NewBidAmt == oldBidAmt) {
            studentNewBalance = studentOldBalance - (NewBidAmt - oldBidAmt);
        }
        if (NewBidAmt < oldBidAmt) {
            studentNewBalance = studentOldBalance + (oldBidAmt - NewBidAmt);
        }

        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement("UPDATE STUDENT SET edollar=? WHERE userID=?");
            stmt.setDouble(1, studentNewBalance);
            stmt.setString(2, stu.getUserID());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw e;
        } finally {
            ConnectionManager.close(conn, stmt);
        }
    }

    /**
     * Given a student ID and amount this method will set the student amount to
     * the given amount. 
     * 
     * @param Amount Amounts to be updated.
     * @param studentID Student to be refunded
     * @throws SQL Exception
     */
    public static void setBalance(double amt, String studentID) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {

            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("UPDATE STUDENT SET edollar=? WHERE userID = ?");
            stmt.setDouble(1, amt);
            stmt.setString(2, studentID);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw e;
        } finally {
            ConnectionManager.close(conn, stmt);

        }
    }

    /**
     * Given a student ID and amount this method will deduct the amount from the student. 
     * 
     * @param Amount Amounts to be updated.
     * @param studentID Student to be refunded
     * @throws SQL Exception
     */
    public static void deductAmt(double amt, String studentID) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {

            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("UPDATE STUDENT SET edollar = edollar - ? WHERE userID = ?");
            stmt.setDouble(1, amt);
            stmt.setString(2, studentID);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw e;
        } finally {
            ConnectionManager.close(conn, stmt);

        }
    }
}
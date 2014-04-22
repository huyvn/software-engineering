/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bios.dao;

import com.bios.model.Admin;
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
 * @author andre.ng.2012
 */
public class AdminDAO {

    private static List<Admin> adminList = new ArrayList<Admin>();

    /**
     * Retrieves the complete list of all students
     *
     * @return the list of students generated
     * @throws SQLException
     */
    public static List<Admin> retrieveAll() throws SQLException {

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;


        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM ADMIN;");
            rs = stmt.executeQuery();

            while (rs.next()) {

                String adminID = rs.getString("adminID");
                String password = rs.getString("password");

                Admin newAdmin = new Admin(adminID, password);
                adminList.add(newAdmin);
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
        return adminList;
    }
}

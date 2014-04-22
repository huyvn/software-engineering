package com.bios.dao;

import static com.bios.dao.sectionDAO.generateSectionList;
import com.bios.model.Prerequisite;
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
public class prerequisiteDAO {

    private static List<Prerequisite> prerequisiteList = new ArrayList<Prerequisite>();

    
    /**
    * Create a new prerequisite object
    * @param course the course code
    * @param prerequisite the prerequisite course code
    * @return a new Prerequisite object
    */
    public static Prerequisite createPrerequiste(String course, String prerequisite) {
        return new Prerequisite(course, prerequisite);
    }
    
    /**
    * Adds a new prerequisite to the list of prerequisite
    * @param newPrerequisite a Prerequisite object that store the prerequisite information
    */
    public static void addPrerequisite(Prerequisite newPrerequisite) {
        prerequisiteList.add(newPrerequisite);
    }

    /**
     * Generates the list of prerequisite by retrieving from the Prerequisite table from the database
     * @throws SQLException
     */
    public static void generatePrerequisiteList() throws SQLException {

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        if (prerequisiteList != null) {
            prerequisiteList.clear();
        }

        try {

            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("SELECT * FROM PREREQUISITE ORDER BY COURSE, PREREQUISITE;");
            rs = stmt.executeQuery();
            while (rs.next()) {
                String courseID = rs.getString("course");
                String prerequisiteID = rs.getString("prerequisite");


                Prerequisite newPrerequisite = createPrerequiste(courseID, prerequisiteID);
                addPrerequisite(newPrerequisite);

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
     * Returns all prerequisites
     *
     * @return the list of prerequisites
     * @throws SQLException
     */
    public static List<Prerequisite> retrieveAll() throws SQLException {
        try {
            generatePrerequisiteList();
        } catch (SQLException e) {
            
        throw new SQLException(e);
            
        }

        return prerequisiteList;
    }
}

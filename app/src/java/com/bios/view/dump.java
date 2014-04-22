/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bios.view;

import com.bios.dao.CombinedBidsDAO;
import com.bios.dao.SectionStudentDAO;
import com.bios.dao.SectionStudentRound2DAO;
import com.bios.dao.bidDAO;
import com.bios.dao.completedCourseDAO;
import com.bios.dao.courseDAO;
import com.bios.dao.prerequisiteDAO;
import com.bios.dao.sectionDAO;
import com.bios.dao.studentDAO;
import com.bios.model.Bid;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
 * @author hpkhoo.2012
 */
/**
 * dump does the dumping of all the tables such as student, bid, section,
 * course, coursecompleted, prerequisite to be display as json
 *
 */
public class dump extends HttpServlet {

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        PrintWriter out = response.getWriter();
        LinkedHashMap<String, Object> theDump = null;
        Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
        theDump = new LinkedHashMap<String, Object>();
        try {
            ServletContext application = getServletConfig().getServletContext();
            Object roundNumObj = getServletConfig().getServletContext().getAttribute("roundNumber");
            int roundObj;
            if (roundNumObj == null) {
                roundObj = 0;
                theDump.put("status", "error");
                theDump.put("message", "Nothing has been uploaded yet");
            } else {
                theDump.put("status", "success");
                theDump.put("course", courseDAO.retrieveAll());
                theDump.put("section", sectionDAO.retrieveAll());
                theDump.put("student", studentDAO.retrieveAll());
                theDump.put("prerequisite", prerequisiteDAO.retrieveAll());

                int roundNumber = (Integer) roundNumObj;
                theDump.put("bid", bidDAO.retrieveAll());
                theDump.put("completed-course", completedCourseDAO.retrieveAll());
                if (roundNumber == 1 || roundNumber == 10) {
                    theDump.put("section-student", SectionStudentDAO.retrieveAll());              
                } else if (roundNumber == 2 || roundNumber == 20) {
                    theDump.put("section-student", SectionStudentDAO.retrieveAll());    
                }
//                if (roundNumber == 1) {
//                    theDump.put("bid", bidDAO.retrieveAll());
//                } else if (roundNumber == 10 || roundNumber == 20) {
//
//                    List<Bid> total = CombinedBidsDAO.retrieveAll();
//
//                    theDump.put("bid", total);
//                } else {
//                    //get list of latest bids by students for sections
//                    theDump.put("bid", bidDAO.retrieveAll());
//                }
//
//                theDump.put("completed-course", completedCourseDAO.retrieveAll());
//                if(!SectionStudentDAO.retrieveAll().isEmpty()){
//                    theDump.put("section-student", SectionStudentDAO.retrieveAll());
//                }

            }
            
            String viewAll = gson.toJson(theDump);
            out.println(viewAll);
        } catch (SQLException e) {
            theDump.put("status", "error");
            theDump.put("message", "Cannot dump due to SQL exception");
            String viewError = gson.toJson(theDump);
            out.println(viewError);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}

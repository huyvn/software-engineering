/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bios.controller;

import com.bios.dao.*;
import com.bios.model.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Meng Yun
 */
public class ClearingRoundTwo extends HttpServlet {

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
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            List<Bid> successfulBidsRound2 = SectionStudentRound2DAO.retrieveAll();
            for (Bid eachBid: successfulBidsRound2){
                String userID = eachBid.getUserID();
                String course = eachBid.getCode();
                String section = eachBid.getSection();
                double amt = eachBid.getAmount();
                SectionStudentDAO.addSuccessfulBid(userID, course, section, amt);
            }
            SectionStudentRound2DAO.deleteAll();
            
            LinkedHashMap<String, Object> hmReply = (LinkedHashMap<String, Object>) request.getAttribute("hmReply");
            request.setAttribute("hmReply", hmReply);
            RequestDispatcher rd = request.getRequestDispatcher("clearedRound.jsp");
            rd.forward(request, response);
            
        } catch (SQLException e) {
            // DO something. 
            out.println(e);
        } finally {
            out.close();
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

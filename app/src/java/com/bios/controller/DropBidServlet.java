/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bios.controller;

import com.bios.dao.bidDAO;
import com.bios.dao.studentDAO;
import com.bios.model.Student;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Jaffe
 */
public class DropBidServlet extends HttpServlet {

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

        Student studentObj = (Student) request.getSession().getAttribute("student");

        if (studentObj == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        String course = request.getParameter("course");
        String section = request.getParameter("section");
        double amount = Double.parseDouble(request.getParameter("amount"));

        RequestDispatcher rd = request.getRequestDispatcher("viewBid.jsp");

        try {
            bidDAO.deleteBid(studentObj.getUserID(), section, course);
            studentDAO.refundMoney(studentObj.getUserID(), amount);
            request.setAttribute("resultForSuccessDelete", "You have successfully deleted the bid");

        } catch (SQLException e) {
            request.setAttribute("resultForSuccessBid", "Sorry an SQLException occur");
        }
        rd.forward(request, response);
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

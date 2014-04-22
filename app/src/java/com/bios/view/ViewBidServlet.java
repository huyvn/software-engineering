/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bios.view;

import com.bios.dao.bidDAO;
import com.bios.dao.studentDAO;
import com.bios.model.Bid;
import com.bios.model.Student;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author hpkhoo.2012
 */
public class ViewBidServlet extends HttpServlet {

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

        String userId = request.getParameter("userid");

        if (userId == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        //ServletContext context = getServletConfig().getServletContext();

        try {
            //Student student= studentDAO.retrieveStudentByUserid(userId);

            List<Bid> bidStudentList = bidDAO.retrieveBidByStudent(userId);

            //request.setAttribute("student", student);

            request.setAttribute("studentID", userId);
            request.setAttribute("bidStudentList", bidStudentList);

            getServletContext().getRequestDispatcher("/viewBid.jsp").forward(request, response);

        } catch (SQLException ex) {
            out.println(ex.getMessage());
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

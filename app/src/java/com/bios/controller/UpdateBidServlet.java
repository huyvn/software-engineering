/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bios.controller;

import com.bios.dao.bidDAO;
import com.bios.dao.studentDAO;
import com.bios.model.Bid;
import com.bios.model.Student;
import com.bios.util.Validate;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author hpkhoo.2012
 */
public class UpdateBidServlet extends HttpServlet {

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

        String[] newBidAmt = (String[]) request.getParameterValues("amount");
        String[] bidsReceive = (String[]) request.getParameterValues("bidsCourseCode");
        try {
            List<Bid> bidStudentCourseList = bidDAO.retrieveSelectedUpdateBid(studentObj.getUserID(), bidsReceive);
        } catch (SQLException ex) {
            Logger.getLogger(UpdateBidServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<Bid> bidToBeUpdated = bidDAO.getListOfUpdateBids();


        double totalBidAmt = 0;
        boolean checkInputContainString = false;
        double bidAmt = 0;
        for (int i = 0; i < newBidAmt.length; i++) {
            try {
                bidAmt = Double.parseDouble(newBidAmt[i]);
                if (!Validate.checkIfAmountValid(bidAmt)) {
                    checkInputContainString = true;
                }
            } catch (NumberFormatException nfe) {
                checkInputContainString = true;
            }

            totalBidAmt += bidAmt;


            if (bidAmt <= 0 || checkInputContainString == true) {
                try {
                    RequestDispatcher rd = request.getRequestDispatcher("viewBid.jsp");
                    List<Bid> bidStudentList = bidDAO.retrieveBidByStudent(studentObj.getUserID());
                    request.setAttribute("bidStudentList", bidStudentList);
                    request.setAttribute("result", "You have entered an invalid amount");
                    rd.forward(request, response);
                } catch (SQLException ex) {
                    RequestDispatcher rd = request.getRequestDispatcher("viewBid.jsp");
                    request.setAttribute("result", "Sorry an SQL exception occured");
                    rd.forward(request, response);
                }
                return;
            }
        }
        // Final check to see if student has enough money overall.
        double totalOriginalAmt = 0;
        for (Bid b : bidToBeUpdated){
            totalOriginalAmt += b.getAmount();
        }
        if (totalBidAmt <= studentObj.getEDollars() + totalOriginalAmt) {
            try {

                bidDAO.updateStudentBid(studentObj.getUserID(), newBidAmt, bidToBeUpdated);
                studentDAO.studentBalanceAfterDeduction(studentObj, totalBidAmt, bidToBeUpdated);

                RequestDispatcher rd = request.getRequestDispatcher("viewBid.jsp");
                List<Bid> bidStudentList = bidDAO.retrieveBidByStudent(studentObj.getUserID());
                request.setAttribute("bidStudentList", bidStudentList);
                request.setAttribute("result", "You have successfully updated your bids");
                rd.forward(request, response);

            } catch (SQLException ex) {
                RequestDispatcher rd = request.getRequestDispatcher("updateBid.jsp");
                request.setAttribute("result", "Sorry an SQL exception occured");
                rd.forward(request, response);
            }
        } else { // else show that you have insufficient money
            try {
                RequestDispatcher rd = request.getRequestDispatcher("viewBid.jsp");
                List<Bid> bidStudentList = bidDAO.retrieveBidByStudent(studentObj.getUserID());
                request.setAttribute("bidStudentList", bidStudentList);
                request.setAttribute("result", "You have insufficient eDollars");
                rd.forward(request, response);
            } catch (SQLException ex) {
                RequestDispatcher rd = request.getRequestDispatcher("viewBid.jsp");
                request.setAttribute("result", "Sorry an SQL exception occured");
                rd.forward(request, response);
            }
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

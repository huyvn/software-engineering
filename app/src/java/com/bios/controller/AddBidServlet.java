/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bios.controller;

import com.bios.dao.*;
import com.bios.model.*;
import com.bios.util.Validate;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.*;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author hpkhoo.2012
 */
public class AddBidServlet extends HttpServlet {

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
        ServletContext context = getServletConfig().getServletContext();

        LinkedHashMap<String, List<Section>> courseWithSections =
                (LinkedHashMap<String, List<Section>>) request.getAttribute("courseWithSections");
        request.setAttribute("courseWithSections", courseWithSections);

        //getting all the neccessary info from addBid.jsp
        Student studentObj = (Student) request.getSession().getAttribute("student");
        String code = request.getParameter("courseCode");
        String section = request.getParameter("section");
        String bidDollarString = request.getParameter("amount");

        //for error messaging storage:
        ArrayList<String> errorMsg = new ArrayList<String>();
        double bidDollar = 0;
        try {
            bidDollar = Double.parseDouble(bidDollarString);
        } catch (NumberFormatException e) {
            errorMsg.add("invalid bid amount entered.");
        }

        double studentEdollar = studentObj.getEDollars();
        try {
            if (!Validate.checkIfAmountValid(bidDollar)) {
                errorMsg.add("invalid bid amount entered.");
            } else {
                HashMap<String, Double> minBidPrices = (HashMap<String, Double>) getServletContext().getAttribute("minBidPrices");
                if (minBidPrices == null) {
                    minBidPrices = new HashMap<String, Double>();
                }
                double minBidPrice = 10.00;
                Double pastMinBidPrice = minBidPrices.get(code + section);
                if (pastMinBidPrice != null) {
                    minBidPrice = pastMinBidPrice;
                }
                if (Double.compare(bidDollar, minBidPrice) < 0) {
                    errorMsg.add("You bid too low.");
                }
            }
            if (!Validate.checkIfEnoughMoney(studentObj.getUserID(), bidDollar)) {
                errorMsg.add("You do not have enough money.");
            }
            if (Validate.checkIfCompleted(studentObj.getUserID(), code)) {
                errorMsg.add("You have already completed this course.");
            }
            if (Validate.checkIfSuccessfulBid(studentObj.getUserID(), code, section)) {
                errorMsg.add("You are enrolled in this section");
            }
            if (Validate.checkIfBidExist(studentObj.getUserID(), code)) {
                errorMsg.add("You already have an existing bid.");
            }
            if (!Validate.prerequisiteCheck(studentObj.getUserID(), code)) {
                errorMsg.add("You do not have the required prerequisites.");
            }
            if (Validate.checkIfSectionLimitReached(studentObj.getUserID())) {
                errorMsg.add("You have reached maximum bid limit.");
            }
            if (Validate.checkClashWithAllTimetable(studentObj.getUserID(), code, section)) {
                errorMsg.add("You have a lesson timetable clash.");
            }
            if (Validate.checkClashWithExams(studentObj.getUserID(), code)) {
                errorMsg.add("You have an exam timetable clash.");
            }
        } catch (NumberFormatException e) {
            errorMsg.add("You have entered an invalid amount.");
        } catch (SQLException e) {
            errorMsg.add("Database error, please contact administrator.");
        } finally { // after all validation checks
            if (!errorMsg.isEmpty()) { // if there are errors
                request.setAttribute("errorList", errorMsg);
                request.setAttribute("code", code);
                request.setAttribute("section", section);
                getServletContext().getRequestDispatcher("/viewSection?courseCodes="+code+"&section=" + section).forward(request, response);
            } else { // if there are NO ERRORS.
                try {
                    int roundNum = (Integer) context.getAttribute("roundNumber");
                    if (roundNum == 1) {
                        bidDAO.insertStudentBid(studentObj.getUserID(), bidDollar, code, section);
                        studentDAO.deductAmt(bidDollar, studentObj.getUserID());
                        response.sendRedirect("./viewAccount");
                    } else if (roundNum == 2) {
                        Bid newBid = new Bid(studentObj.getUserID(), bidDollar, code, section);
                        request.setAttribute("roundTwoNewBid", newBid);
                        getServletContext().getRequestDispatcher("/CalculateRoundTwoResult").forward(request, response);
                    }
                } catch (SQLException e) {
                    errorMsg.add("Database error, please contact administrator.");
                    request.setAttribute("errorList", errorMsg);
                    request.setAttribute("code", code);
                    request.setAttribute("section", section);
                    getServletContext().getRequestDispatcher("/viewSection?courseCodes="+code+"&section=" + section).forward(request, response);
                }
            }
        }
    }//end of processRequest

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
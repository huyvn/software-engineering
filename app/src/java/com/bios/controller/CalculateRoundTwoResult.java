/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bios.controller;

import com.bios.dao.CombinedBidsDAO;
import com.bios.dao.SectionStudentDAO;
import com.bios.dao.SectionStudentRound2DAO;
import com.bios.dao.bidDAO;
import com.bios.dao.sectionDAO;
import com.bios.dao.studentDAO;
import com.bios.model.Bid;
import com.bios.model.Section;
import com.bios.model.Student;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Meng Yun
 */
public class CalculateRoundTwoResult extends HttpServlet {

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
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            Student studentObj = (Student) request.getSession().getAttribute("student");
            if (studentObj == null) {
                studentObj = (Student) request.getAttribute("student");
            }
            Bid newBid = (Bid) request.getAttribute("roundTwoNewBid");
            Boolean fromJSON = (Boolean) request.getAttribute("fromJSON");
            LinkedHashMap<String, Object> reply = (LinkedHashMap<String, Object>) request.getAttribute("JSONReply");

            SectionStudentRound2DAO.createSectionStudentRound2Table();

            List<Bid> currentSuccessfulBids = SectionStudentRound2DAO.retrieveBidsBySection(newBid.getCode(), newBid.getSection());

            Section s = sectionDAO.retrieveCourseSection(newBid.getCode(), newBid.getSection());
            List<Bid> pastSuccessfulBids = SectionStudentDAO.retrieveBidsBySection(newBid.getCode(), newBid.getSection());
            int vacancies = s.getSize() - pastSuccessfulBids.size();

            HashMap<String, Double> minBidPrices = (HashMap<String, Double>) getServletContext().getAttribute("minBidPrices");
            if (minBidPrices == null) {
                minBidPrices = new HashMap<String, Double>();
            }
            double minBidPrice = 10.0;
            Double pastMinBidPrice = minBidPrices.get(newBid.getCode() + newBid.getSection());
            if (pastMinBidPrice != null) {
                minBidPrice = pastMinBidPrice;
            }
            if (Double.compare(newBid.getAmount(), minBidPrice) >= 0) {
                if (currentSuccessfulBids.size() + 1 < vacancies) {
                    SectionStudentRound2DAO.addSuccessfulBid(newBid.getUserID(), newBid.getCode(), newBid.getSection(), newBid.getAmount());
                    studentDAO.deductAmt(newBid.getAmount(), studentObj.getUserID());
                } else if (currentSuccessfulBids.size() + 1 == vacancies) {
                    SectionStudentRound2DAO.addSuccessfulBid(newBid.getUserID(), newBid.getCode(), newBid.getSection(), newBid.getAmount());
                    studentDAO.deductAmt(newBid.getAmount(), studentObj.getUserID());
                    currentSuccessfulBids = SectionStudentRound2DAO.retrieveBidsBySection(newBid.getCode(), newBid.getSection());
                    minBidPrice = currentSuccessfulBids.get(vacancies - 1).getAmount() + 1;
                    minBidPrices.put(newBid.getCode() + newBid.getSection(), minBidPrice);
                } else {
                    currentSuccessfulBids = SectionStudentRound2DAO.retrieveBidsBySection(newBid.getCode(), newBid.getSection());
                    double lowestSuccessfulBidAmt = currentSuccessfulBids.get(vacancies - 1).getAmount();
                    for (Bid eachBid : currentSuccessfulBids) {
                        if (Double.compare(eachBid.getAmount(), lowestSuccessfulBidAmt) == 0) {
                            SectionStudentRound2DAO.deleteSuccessfulBid(eachBid.getUserID(), eachBid.getCode(), eachBid.getSection());
//                            studentDAO.refundMoney(studentObj.getUserID(), eachBid.getAmount());
                            bidDAO.insertStudentBid(eachBid.getUserID(), eachBid.getAmount(), eachBid.getCode(), eachBid.getSection());
                        }
                    }
                    SectionStudentRound2DAO.addSuccessfulBid(newBid.getUserID(), newBid.getCode(), newBid.getSection(), newBid.getAmount());
                    studentDAO.deductAmt(newBid.getAmount(), studentObj.getUserID());
                    currentSuccessfulBids = SectionStudentRound2DAO.retrieveBidsBySection(newBid.getCode(), newBid.getSection());
                    minBidPrice = currentSuccessfulBids.get(currentSuccessfulBids.size() - 1).getAmount() + 1;
                    minBidPrices.put(newBid.getCode() + newBid.getSection(), minBidPrice);
                }
            } else {
                bidDAO.insertStudentBid(newBid.getUserID(), newBid.getAmount(), newBid.getCode(), newBid.getSection());
                studentDAO.deductAmt(newBid.getAmount(), studentObj.getUserID());
            }
            getServletContext().setAttribute("minBidPrices", minBidPrices);

            if (fromJSON != null && fromJSON.booleanValue() && reply != null) {
                out.println(gson.toJson(reply));
            } else {
                response.sendRedirect("./viewAccount");
            }
        } catch (SQLException ex) {
            Logger.getLogger(CalculateRoundTwoResult.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
            out.println(e.getMessage());
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

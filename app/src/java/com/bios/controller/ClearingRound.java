/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bios.controller;

import com.bios.dao.SectionStudentDAO;
import com.bios.dao.bidDAO;
import static com.bios.dao.bidDAO.createBid;
import com.bios.dao.sectionDAO;
import com.bios.dao.studentDAO;
import com.bios.model.Bid;
import com.bios.model.Section;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import javax.naming.Context;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author andreng
 */
public class ClearingRound extends HttpServlet {

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

        ServletContext context = getServletContext();
        Integer roundNumber = (Integer) context.getAttribute("roundNumber");
        if (roundNumber == null) {
            roundNumber = 0;
        }

        if (roundNumber != 10) {
            request.setAttribute("msg", "You cannot access this page yet!");
            RequestDispatcher rd = request.getRequestDispatcher("clearedRound.jsp");
            rd.forward(request, response);

        }

        try {
            List<Section> sections = sectionDAO.retrieveAll();
            double lowestBid = 0.0;

            //for each section, build a list of unsuccessful bids and successful bids.
            for (Section s : sections) {
                List<Bid> successfulBids = new ArrayList<Bid>();
                List<Bid> unfilteredBids = bidDAO.searchBidEntries(s.getCourse(), s.getSection());

                List<Bid> pastSuccessfulBids = SectionStudentDAO.retrieveBidsBySection(s.getCourse(), s.getSection());
                int vacancies = s.getSize() - pastSuccessfulBids.size();

                if (unfilteredBids.size() <= vacancies) {
                    //calculate and take in the highest nth bids
                    for (Bid eachBid : unfilteredBids) {
                        successfulBids.add(eachBid);
                        lowestBid = eachBid.getAmount();
                    }

                } else {
                    for (int i = 0; i < vacancies; i++) {
                        Bid eachBid = unfilteredBids.get(i);
                        successfulBids.add(eachBid);
                        lowestBid = unfilteredBids.get(i + 1).getAmount();
                    }
                }
                // this section of code checks if all the bids are the same. 
                // once there is a different amount detected allSame becomes false.
                boolean allSame = true;
                for (int i = 0; i < successfulBids.size() - 1; i++) {
                    if (successfulBids.get(i).getAmount() != successfulBids.get(i + 1).getAmount()) {
                        allSame = false;
                    }
                }

                // To check and remove all bids from the successful bids list
                // that are same price as the clearing price called "lowestBid".
                // however this is only done when there is more than one bid.
                // single bids within vacant slots should be accepted. 
                if (unfilteredBids.size() > 1) {
                    if (vacancies <= unfilteredBids.size()) {
                        Iterator it = successfulBids.iterator();
                        while (it.hasNext()) {
                            Bid eachBid = (Bid) it.next();
                            if (Double.compare(eachBid.getAmount(), lowestBid) == 0) {
                                it.remove();
                            }
                        }
                    } else if (allSame) {
                        successfulBids.clear();     
                    }
                }
                // once the clearing logic is done. The successful bids array
                // is now written to the SectionStudent table and removed from
                // the bids table. 
                for (Bid eachBid : successfulBids) {
                    String userID = eachBid.getUserID();
                    String course = eachBid.getCode();
                    String section = eachBid.getSection();
                    double amount = eachBid.getAmount();
                    // insert successfulbids into Section Student table.
                    SectionStudentDAO.addSuccessfulBid(userID, course, section, amount);
                    // remove from the bids table
                    bidDAO.deleteBid(userID, section, course);
                }
            }

            // now that the successful bids are removed, we retrieve the list of
            // unsuccessful bids in a list to refund the money
            List<Bid> unsuccessfulBids = bidDAO.retrieveAll();
            for (Bid eachBid : unsuccessfulBids) {
                String userID = eachBid.getUserID();
                double amount = eachBid.getAmount();
                studentDAO.refundMoney(userID, amount);
            }

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
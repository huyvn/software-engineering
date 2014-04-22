/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bios.view;

import com.bios.dao.*;
import com.bios.model.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Viet Huy
 */
public class ViewTimetableServlet extends HttpServlet {

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
            List<String> courseCodes = new ArrayList<String>();
            List<Course> examList = new ArrayList<Course>();

            Student stu = (Student) request.getSession().getAttribute("student");
            String userid = stu.getUserID();
            List<Bid> successfulBids = SectionStudentDAO.retrieveBidsByStudent(userid);
            Object roundNoObj = getServletConfig().getServletContext().getAttribute("roundNumber");
            int roundNo = 0;
            if (roundNoObj != null) {
                roundNo = (Integer) roundNoObj;
            }
            if (roundNo == 2) {
                List<Bid> round2Bids = SectionStudentRound2DAO.retrieveBidsByStudent(userid);
                successfulBids.addAll(round2Bids);
            }
            List<Section> enrolledClasses = new ArrayList<Section>();
            for (Bid eachBid : successfulBids) {
                String courseCode = eachBid.getCode();
                String sectionCode = eachBid.getSection();
                Section section = sectionDAO.retrieveCourseSection(courseCode, sectionCode);
                enrolledClasses.add(section);
                courseCodes.add(courseCode);
            }

            for (String course : courseCodes) {
                Course eachCourse = courseDAO.retreiveCourseByCode(course);
                examList.add(eachCourse);
            }

            request.setAttribute("examList", examList);
            request.setAttribute("enrolledClasses", enrolledClasses);
            request.getRequestDispatcher("viewTimetable.jsp").forward(request, response);
        } catch (SQLException ex) {
            out.println(ex.getMessage());
        } catch (Exception e) {
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

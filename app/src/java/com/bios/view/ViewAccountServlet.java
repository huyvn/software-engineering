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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Viet Huy
 */
public class ViewAccountServlet extends HttpServlet {

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
        
        if(studentObj == null){
            response.sendRedirect("index.jsp");
            return;
        }
        
        String userId = studentObj.getUserID();

        try {
            ServletContext context = getServletConfig().getServletContext();
            List<String> courseCodes = new ArrayList<String>();
            List<Course> examList = new ArrayList<Course>();
            List<Section> enrolledClasses = new ArrayList<Section>();
            
            List<Bid> bidStudentList = bidDAO.retrieveBidByStudent(userId);
            List<CourseComplete> courseCompleteStudentList = completedCourseDAO.retrieveCourseCompletedByStudent(userId);           
            List<Bid> successfulBids = SectionStudentDAO.retrieveBidsByStudent(userId);
            
            if ( context.getAttribute("roundNumber") != null &&(Integer) context.getAttribute("roundNumber") == 2) {
                List<Bid> successfulBidsRoundTwo = SectionStudentRound2DAO.retrieveBidsByStudent(userId);
                successfulBids.addAll(successfulBidsRoundTwo);
            }
            
            //iterate through successful bids and add Section to enrolledClasses
            for (Bid eachBid : successfulBids) {
                String courseCode = eachBid.getCode();
                String sectionCode = eachBid.getSection();
                Section section = sectionDAO.retrieveCourseSection(courseCode, sectionCode);
                enrolledClasses.add(section);
                //for each enrolled class, get the course code
                courseCodes.add(courseCode);
            }   
            
            
            //for each course code, retrieve the Course object 
            for (String course : courseCodes) {
                Course eachCourse = courseDAO.retreiveCourseByCode(course);
                examList.add(eachCourse);
            }
            
            List<Bid> pendingBids = new ArrayList<Bid>();
            List<Section> pendingClasses = new ArrayList<Section>();
            
            if ( context.getAttribute("roundNumber") != null &&(Integer) context.getAttribute("roundNumber") != 10 && (Integer) context.getAttribute("roundNumber") != 20) {
                pendingBids = bidDAO.retrieveBidByStudent(userId);
            }
            for (Bid eachBid: pendingBids){
                String courseCode = eachBid.getCode();
                String sectionCode = eachBid.getSection();
                Section section = sectionDAO.retrieveCourseSection(courseCode, sectionCode);
                pendingClasses.add(section);
            }
            
            
            request.setAttribute("pendingClasses", pendingClasses);
            request.setAttribute("examList", examList);
            request.setAttribute("enrolledClasses", enrolledClasses);
            request.setAttribute("bidStudentList", bidStudentList);
            request.setAttribute("courseCompletedStudentList", courseCompleteStudentList);
            getServletContext().getRequestDispatcher("/homepage.jsp").forward(request, response);

        } catch (SQLException ex) {
            out.println(ex.getMessage());
        } catch (Exception e){
            out.println(e.getMessage());
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

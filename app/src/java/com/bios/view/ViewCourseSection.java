/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bios.view;

import com.bios.dao.*;
import com.bios.model.*;
import com.bios.util.Validate;
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
public class ViewCourseSection extends HttpServlet {

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
        Student sessionStudent = (Student) request.getSession().getAttribute("student");
        String userID;
        try {
            userID = sessionStudent.getUserID();
        } catch (Exception e) {
            response.sendRedirect("index.jsp");
            return;
        }
        request.getSession().setAttribute("userid", userID);
        String courseid = (String) request.getParameter("courseid");
        ServletContext context = getServletConfig().getServletContext();
        String errorMsg = null;

        try {
            if (context.getAttribute("roundNumber") == null) {
                errorMsg = "Round has not started yet!";
                request.setAttribute("errorMsg", errorMsg);
            } else {
                int roundNo = (Integer) context.getAttribute("roundNumber");
                try {
                    if (roundNo == 0) {
                        errorMsg = "The round has not started yet";
                    } else if (roundNo == 1) {
                        Student student = studentDAO.retrieveStudentByUserid(userID);
                        List<Course> courseListOwnSchool = courseDAO.retreiveCourseStudentSchool(student.getSchool());
                        List<Course> courseStudentList = new ArrayList<Course>();

                        /**
                         * for each course in the list, check if bids exist, if
                         * course has been completed, and if the course matches
                         * the search
                         */
                        for (Course eachCourse : courseListOwnSchool) {
                            boolean bidExist = Validate.checkIfBidExist(userID, eachCourse.getCode());
                            boolean checkCompleted = Validate.checkIfCompleted(userID, eachCourse.getCode());

                            /**
                             * to check if matches, search for ':'
                             */
                            boolean courseMatches = true;
                            String courseCode = eachCourse.getCode().toLowerCase();
                            String courseTitle = eachCourse.getTitle().toLowerCase();
                            courseid = courseid.toLowerCase();
                            if (courseid.contains(":")) {
                                /**
                                 * if ':' exists, change courseid into the code
                                 * before it
                                 */
                                courseid = courseid.substring(0, courseid.indexOf(':'));
                                if (courseid != null || !courseid.isEmpty()) {
                                    courseid = courseid.toLowerCase().trim();
                                    if (!courseCode.toLowerCase().contains(courseid)) {
                                        courseMatches = false;
                                    }
                                }
                            } else {
                                /**
                                 * check if this search is a code or is a title.
                                 * If either, deal accordingly
                                 */
                                boolean isCode = courseCode.contains(courseid);
                                boolean isTitle = courseTitle.contains(courseid);
                                if (isCode || isTitle) {
                                    courseMatches = true;
                                } else {
                                    courseMatches = false;
                                }
                            }

                            /**
                             * if no bids exist, course is not completed, and
                             * the course matches the search, then add to list
                             */
                            if (!bidExist && !checkCompleted && courseMatches) {
                                courseStudentList.add(eachCourse);
                            }
                        }
                        request.setAttribute("courseStudent", courseStudentList);
                    } else if (roundNo == 10) {
                        errorMsg = "Round has already ended";
                    } else if (roundNo == 2) {
                        List<Course> courseList = courseDAO.retrieveAll();
                        List<Course> courseListRound2 = new ArrayList<Course>();
                        for (Course eachCourse : courseList) {
                            boolean bidExist = Validate.checkIfBidExist(userID, eachCourse.getCode());
                            boolean checkCompleted = Validate.checkIfCompleted(userID, eachCourse.getCode());
                            Section sectionObj = sectionDAO.retrieveCourse(eachCourse.getCode());
                            boolean checkSuccessful = true;
                            String courseCode = eachCourse.getCode().toLowerCase();
                            String courseTitle = eachCourse.getTitle().toLowerCase();
                            courseid = courseid.toLowerCase();
                            boolean courseMatches = true;
                            if (sectionObj != null) {
                                checkSuccessful = Validate.checkIfSuccessfulBid(userID, eachCourse.getCode(), sectionObj.getSection());
                            }
                            
                            if (courseid.contains(":")) {
                                /**
                                 * if ':' exists, change courseid into the code
                                 * before it
                                 */
                                courseid = courseid.substring(0, courseid.indexOf(':'));
                                if (courseid != null || !courseid.isEmpty()) {
                                    courseid = courseid.toLowerCase().trim();
                                    if (!courseCode.toLowerCase().contains(courseid)) {
                                        courseMatches = false;
                                    }
                                }
                            } else {
                                /**
                                 * check if this search is a code or is a title.
                                 * If either, deal accordingly
                                 */
                                boolean isCode = courseCode.contains(courseid);
                                boolean isTitle = courseTitle.contains(courseid);
                                if (isCode || isTitle) {
                                    courseMatches = true;
                                } else {
                                    courseMatches = false;
                                }
                            }
                            /**
                             * If bid does not exist for the course, if not
                             * completed, if bid not successful, and if course
                             * matches the search query, then include in list
                             */
                            if (!bidExist && !checkCompleted && !checkSuccessful && courseMatches) {
                                courseListRound2.add(eachCourse);
                            }
                        }
                        request.setAttribute("courseStudent", courseListRound2);
                    } else if (roundNo == 20) {
                        errorMsg = "Round has already ended";
                    }
                    request.setAttribute("errorMsg", errorMsg);
                } catch (SQLException ex) {
                    out.println(ex.getMessage());
                }
            }
        } catch (Exception e) {
            out.println(e);
        } finally {
            getServletContext().getRequestDispatcher("/viewCourse.jsp").forward(request, response);
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

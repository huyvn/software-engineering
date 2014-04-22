/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bios.view;

import com.bios.dao.sectionDAO;
import com.bios.model.Section;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
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
 * @author Viet Huy
 */
public class ViewSection extends HttpServlet {

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

        //String[] courseCodes = request.getParameterValues("courseCodes");
        String courseCode = request.getParameter("courseCodes");
        String section = request.getParameter("section");
        ArrayList<String> errorMsgList = (ArrayList<String>) request.getAttribute("errorList");

        if (courseCode != null) {
            try {
                LinkedHashMap<String, List<Section>> courseWithSections = new LinkedHashMap<String, List<Section>>();
                //for (String courseCode : courseCodes) {
                    List<Section> sectionList = sectionDAO.retrieveSectionList(courseCode);
                    courseWithSections.put(courseCode, sectionList);
                //}
                request.setAttribute("courseWithSections", courseWithSections);
                request.setAttribute("code", courseCode);
                request.setAttribute("section", section);
                request.setAttribute("errorList", errorMsgList);

            } catch (SQLException ex) {
                out.println(ex.getMessage());
            }
        } else {
            request.setAttribute("errorMsg", "Please select at least one section for viewing");
        }
        getServletContext().getRequestDispatcher("/viewSection.jsp").forward(request, response);
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

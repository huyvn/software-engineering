/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bios.view;

import com.bios.dao.SectionStudentDAO;
import com.bios.dao.courseDAO;
import com.bios.dao.sectionDAO;
import com.bios.model.Bid;
import com.bios.model.Course;
import com.bios.model.Section;
import com.bios.util.Validate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author hpkhoo.2012
 */
@WebServlet(name = "dumpSection", urlPatterns = {"/section-dump"})
public class dumpSection extends HttpServlet {

    /**
     * Dumps the information of a specific user by turning it into a jsonObject
     * jsonObject After that retrieve that value of the object by using
     * json.get().getAsString() then get getListOfStudentOfThatSection from
     * SectionDAO Processes requests for both HTTP
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
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonStringFromURL = request.getParameter("r");
     
        ArrayList<String> errorList = new ArrayList<String>();
        LinkedHashMap<String, Object> sectionDump = new LinkedHashMap<String, Object>();

        try {
            JsonObject jsonObj = gson.fromJson(jsonStringFromURL, JsonObject.class);
            JsonArray missingArrays = Validate.checkMissing(jsonObj, "course", "section");
            if (missingArrays.size() != 0) {
                JsonObject missingObj = new JsonObject();
                missingObj.addProperty("status", "error");
                missingObj.add("message", missingArrays);
                out.print(gson.toJson(missingObj));
                return;
            }
            
            
            String courseCode = jsonObj.get("course").getAsString();
            String section = jsonObj.get("section").getAsString();

            Course theCourse = courseDAO.retreiveCourseByCode(courseCode);
            Section theSection = sectionDAO.retrieveCourseSection(courseCode, section);

            if (theCourse == null) {
                String errorMsg = "invalid course";
                errorList.add(errorMsg);
                sectionDump.put("status", "error");
                sectionDump.put("message", errorList);

                String sectionDumpJson = gson.toJson(sectionDump);
                out.println(sectionDumpJson);
            } else if (theSection == null) {
                String errorMsg = "invalid section";
                errorList.add(errorMsg);
                sectionDump.put("status", "error");
                sectionDump.put("message", errorList);

                String sectionDumpJson = gson.toJson(sectionDump);
                out.println(sectionDumpJson);
            } else {

                List<Bid> successfulBids = SectionStudentDAO.retrieveBidsBySection(courseCode, section);

                if (successfulBids == null || successfulBids.isEmpty()) {
                    sectionDump.put("status", "success");
                    sectionDump.put("students", successfulBids);
                } else {
                    /*
                     * Use successfulBids to generate ArrayList<LinkedHashMap<String,Object>>
                     * of userid and amount
                     */
                    ArrayList<LinkedHashMap<String, Object>> displayList = new ArrayList<LinkedHashMap<String, Object>>();
                    for (Bid b : successfulBids) {
                        String code = b.getCode();
                        if (code.equals(courseCode)) {
                            LinkedHashMap<String, Object> item = new LinkedHashMap<String, Object>();
                            item.put("userid", b.getUserID());
                            item.put("amount", b.getAmount());
                            displayList.add(item);
                        }
                    }

                    sectionDump.put("status", "success");
                    sectionDump.put("students", displayList); //finally if no more errors, show the details
                }

                String sectionDumpJson = gson.toJson(sectionDump);
                out.println(sectionDumpJson);
            }

        } catch (SQLException e) {
            sectionDump.put("status", "error");
            errorList.add("An SQL exception occur");
            sectionDump.put("message", errorList);
            String viewDumpBid = gson.toJson(sectionDump);
            out.println(viewDumpBid);
        } catch (JsonSyntaxException e) {
            sectionDump.put("status", "error");
            errorList.add("invalid json syntax");
            sectionDump.put("message", errorList);
            String viewDumpBid = gson.toJson(sectionDump);
            out.println(viewDumpBid);
        } catch (NullPointerException ex) {
            sectionDump.put("status", "error");
            errorList.add("The request entered is invalid");
            sectionDump.put("message", errorList);
            String viewDumpBid = gson.toJson(sectionDump);
            out.println(viewDumpBid);
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

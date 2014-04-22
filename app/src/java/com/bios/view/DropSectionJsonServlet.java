/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bios.view;

import com.bios.dao.SectionStudentDAO;
import com.bios.dao.sectionDAO;
import com.bios.dao.studentDAO;
import com.bios.model.Bid;
import com.bios.model.Section;
import com.bios.model.Student;
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
import java.util.LinkedHashMap;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Shane
 */
public class DropSectionJsonServlet extends HttpServlet {

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
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        ArrayList<String> errorList = new ArrayList<String>();
        String jsonStringFromURL = request.getParameter("r");

        LinkedHashMap<String, Object> dropSectionDump = new LinkedHashMap<String, Object>();
        JsonObject missingObj = new JsonObject();
        try {
            JsonObject jsonObj = gson.fromJson(jsonStringFromURL, JsonObject.class);
            JsonArray missingArrays = Validate.checkMissing(jsonObj, "userid", "code", "section");
            if (missingArrays.size() != 0) {
                missingObj.addProperty("status", "error");
                missingObj.add("message", missingArrays);
                return;
            }
          
            String userID = jsonObj.get("userid").getAsString();
            String course = jsonObj.get("code").getAsString();
            String section = jsonObj.get("section").getAsString();
            Student student = studentDAO.retrieveStudentByUserid(userID);

           
            //check if the successful bid exists
            List<Bid> list = SectionStudentDAO.retrieveBidsBySection(course, section);
            boolean courseExist = false;

            for (Bid b : list) {
                String checkStudent = b.getUserID();
                if (checkStudent.equals(userID)) {
                    courseExist = true;
                }
            }

            //if the course exists, drop the student
            if (courseExist) {
                SectionStudentDAO.dropStudentSection(student, course);
                for (Bid b : list) {
                    String checkStudent = b.getUserID();
                    if (checkStudent.equals(userID)) {
                        studentDAO.refundMoney(userID, b.getAmount());
                    }
                }

                dropSectionDump.put("status", "success");
            } else { //else, display error message
                String errorMsg = "No such enrollment record";
                errorList.add(errorMsg);
                dropSectionDump.put("status", "error");
                dropSectionDump.put("message", errorList);
            }

            //String dropSectionJson = gson.toJson(dropSectionDump);
            //out.println(dropSectionJson);

        } catch (SQLException ex) {
            dropSectionDump.put("status", "error");
            errorList.add("invalid SQL statement");
            dropSectionDump.put("message", errorList);

        } catch (JsonSyntaxException ex) {
            dropSectionDump.put("status", "error");
            errorList.add("invalid json syntax");
            dropSectionDump.put("message", errorList);

        } catch (NullPointerException ex) {
            dropSectionDump.put("status", "error");
            errorList.add("The request entered is invalid");
            dropSectionDump.put("message", errorList);

        } finally {
            if (dropSectionDump.isEmpty()) {
                out.print(gson.toJson(missingObj));
            } else {
                String dropSectionJson = gson.toJson(dropSectionDump);
                out.println(dropSectionJson);
            }
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

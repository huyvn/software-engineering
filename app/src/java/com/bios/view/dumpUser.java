/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bios.view;

import com.bios.dao.studentDAO;
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
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class dumpUser extends HttpServlet {

    /**
     * Dumps the information of a specific user by turning it into a jsonObject
     * jsonObject After that retrieve that value of the object by using
     * json.get().getAsString Then let the studentDAO process that username to
     * check with the database if they have it or not Then from the student
     * object retrieve turn it back to a json to print it out. Processes
     * requests for both HTTP
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
        

        LinkedHashMap<String, Object> studentDump = new LinkedHashMap<String, Object>();
        ArrayList<String> errorList = new ArrayList<String>();
        
        JsonObject missingObj = new JsonObject();
        try {
            JsonObject jsonObj = gson.fromJson(jsonStringFromURL, JsonObject.class);
            
            JsonArray missingArrays = Validate.checkMissing(jsonObj, "userid");
            if (missingArrays.size() != 0) {
                
                missingObj.addProperty("status", "error");
                missingObj.add("message", missingArrays);
                return;
            }
            
            String userid = jsonObj.get("userid").getAsString();
            Student student = studentDAO.retrieveStudentByUserid(userid);

            if (student == null) {

                String message = "invalid userid";
                errorList.add(message);
                studentDump.put("status", "error");
                studentDump.put("message", errorList);


            } else {

                studentDump.put("status", "success");
                studentDump.put("userid", student.getUserID());
                studentDump.put("password", student.getPassword());
                studentDump.put("name", student.getName());
                studentDump.put("school", student.getSchool());
                studentDump.put("edollar", student.getEDollars());

            }

            // String studentDumpJson = gson.toJson(studentDump);
            //out.println(studentDumpJson);

        } catch (SQLException e) {
            studentDump.put("status", "error");
            errorList.add("An SQL exception occur");
            studentDump.put("message", errorList);

        } catch (JsonSyntaxException e) {
            studentDump.put("status", "error");
            errorList.add("invalid json syntax");
            studentDump.put("message", errorList);

        } catch (NullPointerException ex) {
            studentDump.put("status", "error");
            errorList.add("The request entered is invalid");
            studentDump.put("message", errorList);

        } finally {
            if (studentDump.isEmpty()) {
                 out.print(gson.toJson(missingObj));
            } else {
                String studentDumpJson = gson.toJson(studentDump);
                out.println(studentDumpJson);
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

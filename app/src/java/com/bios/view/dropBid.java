/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bios.view;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.bios.dao.*;
import com.bios.model.Bid;
import com.bios.util.Validate;
import com.google.gson.JsonArray;
import com.google.gson.JsonSyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import javax.servlet.ServletContext;

/**
 *
 * @author Shane
 */
public class dropBid extends HttpServlet {

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
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        LinkedHashMap<String, Object> hmResponse = new LinkedHashMap<String, Object>();
        ArrayList<String> errorList = new ArrayList<String>();

        int roundNum = 0;
        ServletContext context = getServletConfig().getServletContext();
        try {
            roundNum = (Integer) context.getAttribute("roundNumber");
        } catch (NullPointerException e) {
            roundNum = 0;
        }
        JsonObject missingObj = new JsonObject();
        try {
            String jsonString = request.getParameter("r");
            JsonObject obj = gson.fromJson(jsonString, JsonObject.class);

            JsonArray missingArrays = Validate.checkMissing(obj, "userid", "code", "section");
            if (missingArrays.size() != 0) {
                missingObj.addProperty("status", "error");
                missingObj.add("message", missingArrays);

                return;
            }

            String ID = obj.get("userid").getAsString();
            String sec = obj.get("section").getAsString();
            String code = obj.get("code").getAsString();

            boolean sectionValidation = Validate.checkIfCourseSectionExist(code, sec);
            boolean courseValidation = Validate.checkIfValidCourse(code);
            boolean studentValidation = Validate.checkIfStudentExist(ID);

            if (!studentValidation) {
                errorList.add("invalid userid");
            }
            if (!courseValidation) {
                errorList.add("invalid course");
            }
            if (!sectionValidation && courseValidation) {
                errorList.add("invalid section");
            }


            if (errorList.isEmpty()) {
                if (roundNum == 10 || roundNum == 20 || roundNum == 0) {
                    errorList.add("round ended");
                }

                if (roundNum == 1 || roundNum == 2) {
                    boolean bidExistValidation = Validate.checkIfBidExist(ID, code);
                    List<Bid> bidList = bidDAO.retrieveBidByStudentCourse(ID, code);
                    if (!bidExistValidation) {
                        errorList.add("no such bid");
                    }
                    for (Bid eachBid : bidList) {  //very important to check if correct code, but different section

                        if (!eachBid.getSection().equals(sec)) {
                            errorList.add("no such bid");
                        }
                    }
                }
                if (errorList.isEmpty()) {

                    List<Bid> bidList = bidDAO.retrieveBidByStudentCourse(ID, code);
                    for (Bid eachBid : bidList) {
                        studentDAO.refundMoney(ID, eachBid.getAmount());
                    }
                    bidDAO.deleteBid(ID, sec, code);
                    hmResponse.put("status", "success");
                } else {
                    hmResponse.put("status", "error");
                    hmResponse.put("message", errorList);
                }
            } else {
                hmResponse.put("status", "error");
                hmResponse.put("message", errorList);
            }
        } catch (SQLException e) {
            errorList.add("An SQL exception occur");
            hmResponse.put("status", "error");
            hmResponse.put("message", errorList);
        } catch (JsonSyntaxException e) {
            hmResponse.put("status", "error");
            errorList.add("invalid json syntax");
            hmResponse.put("message", errorList);
        } catch (NullPointerException e) {
            hmResponse.put("status", "error");
            errorList.add("The request entered is invalid");
            hmResponse.put("message", errorList);
        } finally {
            if (hmResponse.isEmpty()) {
                out.print(gson.toJson(missingObj));
            } else {
                out.println(gson.toJson(hmResponse));
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

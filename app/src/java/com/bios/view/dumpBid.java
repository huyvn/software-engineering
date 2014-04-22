package com.bios.view;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import com.bios.dao.CombinedBidsDAO;
import com.bios.dao.SectionStudentDAO;
import com.bios.dao.bidDAO;
import com.bios.dao.courseDAO;
import com.bios.dao.sectionDAO;
import com.bios.model.Bid;
import com.bios.util.ConnectionManager;
import com.bios.util.Validate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Meng yun
 */
public class dumpBid extends HttpServlet {

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     * @throws IOException if an I/O error occurs Validate course input and
     * section input Retrieve List<Bid> sectionList from bidDAO with parameter
     * courseID and section Retrieve int sectionSize from sectionDAO with
     * parameter courseID and section Get userID and bid amount from sectionList
     * Create new bids with parameter row number, userID, bid amount and result
     * (in/out) Assign new bids into linkedHashMap as value, status as key.
     * Convert linkedHashMap into json format and Print it out.
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        PrintWriter out = response.getWriter();
        Connection conn = null;
        PreparedStatement stmt = null;
        LinkedHashMap<String, Object> dumpBid = new LinkedHashMap<String, Object>();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        ArrayList<String> errorList = new ArrayList<String>();

        String jsonStringFromURL = request.getParameter("r");

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
             
            String courseID = jsonObj.get("course").getAsString();
            String section = jsonObj.get("section").getAsString();

            boolean courseValidation = courseDAO.validateCourseByCode(courseID);
            boolean sectionValidation = sectionDAO.validateSection(courseID, section);

            if (!courseValidation) {
                ArrayList<String> msg = new ArrayList<String>();
                msg.add("invalid course");
                dumpBid.put("status", "error");
                dumpBid.put("message", msg);
                String viewError = gson.toJson(dumpBid);
                out.println(viewError);
                return;
            } else if (!sectionValidation) {
                ArrayList<String> msg = new ArrayList<String>();
                msg.add("invalid section");
                dumpBid.put("status", "error");
                dumpBid.put("message", msg);
                String viewError = gson.toJson(dumpBid);
                out.println(viewError);
                return;
            }

            ServletContext context = getServletConfig().getServletContext();
            String errorMsg = null;

            if (context.getAttribute("roundNumber") == null) {
                errorMsg = "Round has not started yet!";

            } else {
                int roundNo = (Integer) context.getAttribute("roundNumber");

                if (roundNo == 0) {
                    errorMsg = "The round has not started yet";
                } else if (roundNo == 1 || roundNo == 2) {
                    List<Bid> combinedBidsList = new ArrayList<Bid>();
                    if (roundNo == 1){
                        combinedBidsList = CombinedBidsDAO.retrieveBidsByCourseSection(courseID, section);
                    } else {
                        combinedBidsList = CombinedBidsDAO.retrieveBidsByCourseSectionRound2(courseID, section);
                    }
                    dumpBid.put("status", "success");
                    int rowNo = 1;
                    ArrayList<JsonObject> bidsListing = new ArrayList<JsonObject>();
                    for (Bid eachBid : combinedBidsList) {

                        JsonObject obj = new JsonObject();
                        obj.addProperty("row", rowNo++);
                        obj.addProperty("userid", eachBid.getUserID());
                        obj.addProperty("amount", eachBid.getAmount());
                        obj.addProperty("result", "-");
                        bidsListing.add(obj);

                    }

                    dumpBid.put("bids", bidsListing);
                    String viewDumpBid = gson.toJson(dumpBid);
                    out.println(viewDumpBid);
                } else {
                    List<Bid> combinedBidsList = CombinedBidsDAO.retrieveBidsByCourseSection(courseID, section);
                    dumpBid.put("status", "success");
                    int rowNo = 1;
                    ArrayList<JsonObject> bidsListing = new ArrayList<JsonObject>();
                    for (Bid eachBid : combinedBidsList) {

                        JsonObject obj = new JsonObject();
                        obj.addProperty("row", rowNo++);
                        obj.addProperty("userid", eachBid.getUserID());
                        obj.addProperty("amount", eachBid.getAmount());
                        if (Validate.checkIfSuccessfulBid(eachBid.getUserID(),
                                eachBid.getCode(), eachBid.getSection())) {
                            obj.addProperty("result", "in");
                        } else {
                            obj.addProperty("result", "out");
                        }
                        bidsListing.add(obj);
                    }


                    dumpBid.put("bids", bidsListing);
                    String viewDumpBid = gson.toJson(dumpBid);
                    out.println(viewDumpBid);
                }////end of else
            }//end of outer else


        } catch (SQLException e) {
            dumpBid.put("status", "error");
            errorList.add("An SQL exception occur");
            dumpBid.put("message", errorList);
            String viewDumpBid = gson.toJson(dumpBid);
            out.println(viewDumpBid);
        } catch (JsonSyntaxException e) {
            dumpBid.put("status", "error");
            errorList.add("invalid json syntax");
            dumpBid.put("message", errorList);
            String viewDumpBid = gson.toJson(dumpBid);
            out.println(viewDumpBid);
        } catch (NullPointerException ex) {
            dumpBid.put("status", "error");
            errorList.add("The request entered is invalid");
            dumpBid.put("message", errorList);
            String viewDumpBid = gson.toJson(dumpBid);
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

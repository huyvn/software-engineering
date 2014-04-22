package com.bios.view;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import com.bios.dao.SectionStudentDAO;
import com.bios.dao.SectionStudentRound2DAO;
import com.bios.dao.bidDAO;
import com.bios.dao.courseDAO;
import com.bios.dao.sectionDAO;
import com.bios.dao.studentDAO;
import com.bios.model.Bid;
import com.bios.model.Student;
import com.bios.util.Validate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import javax.servlet.ServletException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Meng yun
 */
public class JsonUpdateBids extends HttpServlet {

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
        ArrayList<String> msg = new ArrayList<String>();

        String jsonStringFromURL = request.getParameter("r");

        String studentID = null;
        String courseID = null;
        String amountStr = null;
        Double amount = null;
        String section = null;
        int roundNum = 0;

        ServletContext context = getServletConfig().getServletContext();
        try {
            roundNum = (Integer) context.getAttribute("roundNumber");
        } catch (NullPointerException e) {
            roundNum = 0;
        }

        try {
            JsonObject jsonObj = gson.fromJson(jsonStringFromURL, JsonObject.class);
            JsonArray missingArrays = Validate.checkMissing(jsonObj, "userid" ,"code", "section", "amount");
            if (missingArrays.size() != 0) {
                JsonObject missingObj = new JsonObject();
                missingObj.addProperty("status", "error");
                missingObj.add("message", missingArrays);
                out.print(gson.toJson(missingObj));
                return;
            }
            
            boolean checkIfFieldBlank = false;
            try {
                //check for missing & blank parameters in Json String
                JsonElement studentIDJson = jsonObj.get("userid");
                JsonElement courseIDJson = jsonObj.get("code");
                JsonElement amountJson = jsonObj.get("amount");
                JsonElement sectionJson = jsonObj.get("section");

                if (amountJson == null) {
                    msg.add("amount is missing");
                } else {
                    amountStr = jsonObj.get("amount").getAsString();
                    amount = jsonObj.get("amount").getAsDouble();
                }
                if (amountStr != null && Validate.checkIfFieldBlank(amountStr)) {
                    msg.add("amount is blank");
                    checkIfFieldBlank = true;
                }
                if (courseIDJson == null) {
                    msg.add("code is missing");
                } else {
                    courseID = jsonObj.get("code").getAsString();
                }
                if (Validate.checkIfFieldBlank(courseID)) {
                    msg.add("code is blank");
                    checkIfFieldBlank = true;
                }
                if (sectionJson == null) {
                    msg.add("section is missing");
                } else {
                    section = jsonObj.get("section").getAsString();
                }
                if (Validate.checkIfFieldBlank(section)) {
                    msg.add("section is blank");
                    checkIfFieldBlank = true;
                }
                if (studentIDJson == null) {
                    msg.add("userid is missing");
                } else {
                    studentID = jsonObj.get("userid").getAsString();
                }
                if (Validate.checkIfFieldBlank(studentID)) {
                    msg.add("userid is blank");
                    checkIfFieldBlank = true;
                }
            } catch (NumberFormatException e) {
                msg.add("invalid amount");
            }

            if (!checkIfFieldBlank) {
                boolean courseValidation = Validate.checkIfValidCourse(courseID);
                boolean sectionValidation = Validate.checkIfSectionValidAfterBootstrap(courseID, section);
                Validate.checkIfCourseSectionExist(courseID, section);
                boolean studentValidation = Validate.checkIfStudentExist(studentID);

                if (!Validate.checkIfAmountValid(amountStr)) {
                    msg.add("invalid amount");
                }
                if (!courseValidation) {
                    msg.add("invalid code");
                } else if (!sectionValidation) {
                    msg.add("invalid section");
                }
                if (!studentValidation) {
                    msg.add("invalid userid");
                }
            }
            if (msg.isEmpty()) {
                // This check is imcomplete because it does not 
                // take into consideration round 2 minimum bid logic
                boolean checkIfBidExist = Validate.checkIfBidExist(studentID, courseID);
                boolean checkIfSuccessfulBids = Validate.checkIfSuccessfulBid(studentID, courseID, section);
                if (!Validate.checkIfAmountValid(amount)) {
                    msg.add("bid too low");
                } else {
                    HashMap<String, Double> minBidPrices = (HashMap<String, Double>) getServletContext().getAttribute("minBidPrices");
                    if (minBidPrices == null) {
                        minBidPrices = new HashMap<String, Double>();
                    }
                    double minBidPrice = 10.00;
                    if (roundNum == 2) {
                        Double pastMinBidPrice = minBidPrices.get(courseID + section);

                        if (pastMinBidPrice != null) {
                            minBidPrice = pastMinBidPrice;
                        }
                        if (Double.compare(amount, minBidPrice) < 0) {
                            msg.add("bid too low");
                        }
                    }
                }
                if (!checkIfBidExist) {
                    if (!Validate.checkIfEnoughMoney(studentID, amount)) {
                        msg.add("insufficient e$");
                    }
                } else {
                    Student student = studentDAO.retrieveStudentByUserid(studentID);
                    Bid oldBid = bidDAO.retrieveSingleBid(studentID, courseID);
                    double oldAmt = oldBid.getAmount();
                    double balanceToCompare = student.getEDollars() + oldAmt;
                    if (balanceToCompare < amount) {
                        msg.add("insufficient e$");
                    }
                }
                if (!(checkIfBidExist || checkIfSuccessfulBids) && Validate.checkClashWithAllTimetable(studentID, courseID, section)) {
                    msg.add("class timetable clash");
                }
                if (!(checkIfBidExist || checkIfSuccessfulBids) && Validate.checkClashWithExams(studentID, courseID)) {
                    msg.add("exam timetable clash");
                }
                if (!Validate.prerequisiteCheck(studentID, courseID)) {
                    msg.add("incomplete prerequisites");
                }
                if (roundNum == 10 || roundNum == 20 || roundNum == 0) {
                    msg.add("round ended");
                }
                if (Validate.checkIfCompleted(studentID, courseID)) {
                    msg.add("course completed");
                }
                //check if the bid is already successful
                if (roundNum != 2 && Validate.checkIfSuccessfulBid(studentID, courseID, section)) {
                    msg.add("course enrolled");
                }else {
                    Bid bidRound1 = SectionStudentDAO.retrieveBidsByStudentCourse(studentID, courseID);
                    if (bidRound1 != null){
                        msg.add("course enrolled");
                    }
                }
                //if the bid is an update, do not check if section limit is reached
                if (!checkIfBidExist && Validate.checkIfSectionLimitReached(studentID)) {
                    msg.add("section limit reached");
                }
                if (roundNum == 1 && !Validate.checkIfCourseFromStudentSchool(studentID, courseID)) {
                    msg.add("not own school course");
                }
                if (!Validate.checkIfSectionHasVacancies(courseID, section)) {
                    msg.add("no vacancy");
                }
                if (msg.isEmpty()) {
                    if (!checkIfBidExist) {
                        if (roundNum == 1) {
                            bidDAO.insertStudentBid(studentID, amount, courseID, section);
                            studentDAO.deductAmt(amount, studentID);
                            dumpBid.put("status", "success");
                        } else if (roundNum == 2) {
                            Student student = studentDAO.retrieveStudentByUserid(studentID);
                            Bid pastSuccessfulBid = SectionStudentRound2DAO.retrieveBidsByStudentCourse(studentID, courseID);
                            if (pastSuccessfulBid != null){
                                double oldAmt = pastSuccessfulBid.getAmount();
                                studentDAO.refundMoney(studentID, oldAmt);
                                SectionStudentRound2DAO.dropStudentSection(student, courseID);
                            }
                            Bid newBid = bidDAO.createBid(studentID, amount, courseID, section);
                            boolean fromJSON = true;
                            dumpBid.put("status", "success");
                            request.setAttribute("student", student);
                            request.setAttribute("fromJSON", fromJSON);
                            request.setAttribute("roundTwoNewBid", newBid);
                            request.setAttribute("JSONReply", dumpBid);
                            getServletContext().getRequestDispatcher("/CalculateRoundTwoResult").forward(request, response);
                        }

                    } else {
                        if (roundNum == 1) {
                            Bid oldBid = bidDAO.retrieveSingleBid(studentID, courseID);
                            double oldAmt = oldBid.getAmount();
                            bidDAO.updateSingleBid(studentID, amount, courseID, section);
                            studentDAO.refundMoney(studentID, oldAmt - amount);
                            dumpBid.put("status", "success");
                        } else if (roundNum == 2) {
                            // problem here
                            // also need to delete from successful bids
                            Student student = studentDAO.retrieveStudentByUserid(studentID);
                            Bid newBid = bidDAO.createBid(studentID, amount, courseID, section);
                            // retrieve unsuccessful bid
                            Bid pastUnsuccessfulBid = bidDAO.retrieveSingleBid(section, courseID);
                            if (pastUnsuccessfulBid != null){
                                double oldAmt = pastUnsuccessfulBid.getAmount();
                                studentDAO.refundMoney(studentID, oldAmt);
                                bidDAO.deleteBid(studentID, section, courseID);
                            }
                            boolean fromJSON = true;
                            dumpBid.put("status", "success");
                            request.setAttribute("student", student);
                            request.setAttribute("fromJSON", fromJSON);
                            request.setAttribute("roundTwoNewBid", newBid);
                            request.setAttribute("JSONReply", dumpBid);
                            getServletContext().getRequestDispatcher("/CalculateRoundTwoResult").forward(request, response);
                        }
                    }

                } else {
                    dumpBid.put("status", "error");
                    dumpBid.put("message", msg);
                }
            } else {
                dumpBid.put("status", "error");
                dumpBid.put("message", msg);
            }
            String viewDumpBid = gson.toJson(dumpBid);
            out.println(viewDumpBid);
        } catch (SQLException e) {
            dumpBid.put("status", "error");
            msg.add("invalid SQL statement");
            dumpBid.put("message", msg);
            String viewDumpBid = gson.toJson(dumpBid);
            out.println(viewDumpBid);
        } catch (JsonSyntaxException e) {
            dumpBid.put("status", "error");
            msg.add("invalid json syntax");
            dumpBid.put("message", msg);
            String viewDumpBid = gson.toJson(dumpBid);
            out.println(viewDumpBid);
        } catch (NullPointerException ex) {
            dumpBid.put("status", "error");
            msg.add("The request entered is invalid");
            dumpBid.put("message", msg);
            String viewDumpBid = gson.toJson(dumpBid);
            out.println(viewDumpBid);
        } catch (Exception e){ 
            out.println(e.getMessage());
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

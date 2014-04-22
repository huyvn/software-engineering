package com.bios.controller;

import com.bios.dao.AdminDAO;
import com.bios.dao.studentDAO;
import static com.bios.dao.studentDAO.generateStudentList;
import com.bios.model.Admin;
import com.bios.model.Student;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.util.*;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author andreng
 */
public class Login extends HttpServlet {

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Get Path info 'Login/*' (i.e. authenticate_student or authenticate_admin)
     * If Path info equals "authenticate_student", authenticate_student method invoked 
     * If Path info equals "authenticate_admin", authenticate_admin method invoked 
     * Create a new Json object that contains the a Json object that "authenticate_student" or "authenticate_admin" returns 
     * Print out Json object to Json format Strings
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        PrintWriter out = response.getWriter();

        String action = request.getPathInfo().substring(1);
        JsonObject servletResponse = new JsonObject();

        if (action.equalsIgnoreCase("authenticate_student")) {
            // Invoke authenticate_student method
            servletResponse = authenticate_student(request);
            out.println(gson.toJson(servletResponse));
            HttpSession hs = request.getSession();
            hs.setAttribute("status", "success");
        } else if (action.equalsIgnoreCase("authenticate_admin")) {
             servletResponse = authenticate_admin(request);
            out.println(gson.toJson(servletResponse));
            // to set session variable here.
            HttpSession hs = request.getSession();
            hs.setAttribute("admin", "true");
        } else {
            servletResponse.addProperty("status", "error");
            servletResponse.addProperty("message", "Invalid request parameters");
            out.println(gson.toJson(servletResponse));
        }
    }

    /**
     * Create a new Json object 
     * Validates request parameter 
     * Converts Json format Strings into an object of Student class 
     * Get student object userID and Password 
     * Retrieves student list from StudentDAO and checks if student userID and Password exists 
     * If exists, set Json object keys and values respectively.
     * If does not exist, set error message into Json object
     *
     * @param request
     * @return JsonObject
     */
    public JsonObject authenticate_student(HttpServletRequest request) {
        JsonObject jsonMsg = new JsonObject();

        try {
            validateParam(request, "r");

            String jsonString = request.getParameter("r");
            Student login_student = gson.fromJson(jsonString, Student.class);

            String userID = login_student.getUserID();
            String password = login_student.getPassword();

            List<Student> students = studentDAO.retrieveAll();

            for (Student eachStudent : students) {
                if (userID.equals(eachStudent.getUserID()) && password.equals(eachStudent.getPassword())) {
                    jsonMsg.addProperty("status", "success");
                    jsonMsg.addProperty("name", eachStudent.getName());
                    request.getSession().setAttribute("student", eachStudent);
                    return jsonMsg;
                } else {
                    jsonMsg.addProperty("status", "failure");
                }
            }
        } catch (Exception e) {
            jsonMsg.addProperty("status", "error");
            jsonMsg.addProperty("message", "Invalid request parameters");
            jsonMsg.addProperty("details", e.toString());
        }

        return jsonMsg;
    }
    
    
        /**
     * Create a new Json object 
     * Validates request parameter 
     * Converts Json format Strings into an object of Student class 
     * Get student object userID and Password 
     * Retrieves student list from StudentDAO and checks if student userID and Password exists 
     * If exists, set Json object keys and values respectively.
     * If does not exist, set error message into Json object
     *
     * @param request
     * @return JsonObject
     */
    public JsonObject authenticate_admin(HttpServletRequest request) {
        JsonObject jsonMsg = new JsonObject();
        
        try {
            validateParam(request, "r");

            String jsonString = request.getParameter("r");
            Admin login_admin = gson.fromJson(jsonString, Admin.class);

            String userID = login_admin.getUsername();
            String password = login_admin.getPassword();

            List<Admin> admins = AdminDAO.retrieveAll();
       
            for (Admin eachAdmin : admins) {
                if (userID.equals(eachAdmin.getUsername()) && password.equals(eachAdmin.getPassword())) {
                    jsonMsg.addProperty("status", "success");
                    jsonMsg.addProperty("name", eachAdmin.getUsername());
                    request.getSession().setAttribute("admin", eachAdmin);
                    return jsonMsg;
                } else {
                    jsonMsg.addProperty("status", "failure");
                }
            }
        } catch (Exception e) {
            jsonMsg.addProperty("status", "error");
            jsonMsg.addProperty("message", "Invalid request parameters");
            jsonMsg.addProperty("details", e.toString());
        }

        return jsonMsg;
    }


    // Validate the given parameters
    /**
     * Check if request parameter is missing,if condition is true, throw IllegalArgumentException
     * @param request
     * @param requiredParameters (could be one or more string parameters)
     * @throws IllegalArgumentException 
     */
    private void validateParam(HttpServletRequest request, String... requiredParameters)
            throws IllegalArgumentException {

        for (String eachParameter : requiredParameters) {
            // calls the request parameter by a list of required names
            // if no such parameters exists it will give a null 

            // Note: any other extra parameters given that is not 
            // included in the required parameters will be ignored.
            String givenParam = request.getParameter(eachParameter);

            if (givenParam == null || givenParam.equals("")) {
                throw new IllegalArgumentException("Invalid request parameters");
            }
        }
    }
}

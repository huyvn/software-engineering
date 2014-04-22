/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bios.view;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author andreng
 */
public class stop extends HttpServlet {

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
        PrintWriter out = response.getWriter();
        try {

            LinkedHashMap<String, Object> hmReply = new LinkedHashMap<String, Object>();
            ServletContext application = getServletConfig().getServletContext();
            ArrayList<String> errorList = new ArrayList<String>();

            if (application.getAttribute("roundNumber") == null) {
                application.setAttribute("roundNumber", 0);
            }

            int roundNo = (Integer) application.getAttribute("roundNumber");
            if (roundNo == 0) {
                errorList.add("The round has not started yet");
                hmReply.put("status", "error");
                hmReply.put("message", errorList);
            } else if (roundNo == 1) {
                hmReply.put("status", "success");
                application.setAttribute("roundNumber", 10);
                request.setAttribute("hmReply", hmReply);
                getServletContext().getRequestDispatcher("/ClearingRound").forward(request, response);
            } else if (roundNo == 10) {
                errorList.add("round already ended");
                hmReply.put("status", "error");
                hmReply.put("message", errorList);
            } else if (roundNo == 2) {
                hmReply.put("status", "success");
                application.setAttribute("roundNumber", 20);
                request.setAttribute("hmReply", hmReply);
                getServletContext().getRequestDispatcher("/ClearingRoundTwo").forward(request, response);
            } else if (roundNo == 20) {
                errorList.add("round already ended");
                hmReply.put("status", "error");
                hmReply.put("message", errorList);
            }

            String checkAdmin = (String) request.getSession().getAttribute("admin");

            if (checkAdmin == null) {
                out.println(gson.toJson(hmReply));
                return;
            } else {
                 response.setContentType("text/html;charset=UTF-8");
                Set keyList = hmReply.keySet();
                Iterator it = keyList.iterator();
                out.println("<html>");
                out.println("<head>");
                out.println("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>"
                        + "<link href='../css/bootstrap.min.css' rel='stylesheet' media='screen'>"
                        + "<link type='text/css' rel='stylesheet' href='../css/style.css'>"
                        + "<link href='//netdna.bootstrapcdn.com/font-awesome/3.2.1/css/font-awesome.css' rel='stylesheet'>"
                        + "<link href='http://fonts.googleapis.com/css?family=Archivo+Narrow' rel='stylesheet' type='text/css'>"
                        + "<script src='../js/bootstrap.min.js'></script>"
                        + "<script src='../js/bootstrap.js'></script>");
                out.println("</head>");
                out.println("<div class='navbar navbar-default navbar-fixed-top'><div class='navbar-header'><a href=''>"
                        + "<i class='navbar-brand MediumIcon icon-home'> Merlion University Bidding Online System (BIOS)</i></a>"
                        + "</div>"
                        + "</div></br></br>");
                out.println("<body class='fontCSS'></br></br>");
                out.println("<table style='margin-left:20px'>");
                while (it.hasNext()) {
                    Object key = it.next();
                    Object keyItem = hmReply.get(key);
                    out.println("<tr>");
                    out.println("<td>");
                    out.println("<b>" + key.toString().toUpperCase() + ": </b>");
                    out.println("</td>");
                    out.println("<td>");
                    out.println(keyItem.toString().replace("[", "").replace("]", ""));
                    out.println("</td>");
                    out.println("</tr>");
                }
                out.println("</table></br>");
                out.println("<form action=\"../adminHome.jsp\">");
                out.println("<input class='btn btn-info' style='margin-left:20px' type=\"submit\" value=\"Back to admin home\" />");
                out.println("</form>");
                out.println("</body>");
                out.println("</html>");
            }
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

<%-- 
    Document   : clearedRound
    Created on : Oct 9, 2013, 5:31:03 PM
    Author     : andreng
--%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Set"%>
<%@page import="com.google.gson.GsonBuilder"%>
<%@page import="com.google.gson.Gson"%>
<%@page import="java.util.LinkedHashMap"%>
<%--<%@include file="protectAdmin.jsp"%>--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    LinkedHashMap<String, Object> hmReply = (LinkedHashMap<String, Object>) request.getAttribute("hmReply");
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
%>




<%@page import="com.bios.dao.SectionStudentRound2DAO"%>
<%@page import="com.bios.dao.courseDAO"%>
<%@page import="com.bios.dao.bidDAO"%>
<%@page import="com.bios.dao.SectionStudentDAO"%>
<%@page import="com.bios.model.Bid"%>
<%@page import="java.util.List"%>
<%@page import="com.bios.model.Student"%>
<%@include file="protect.jsp"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:include page="NavigationBar.jsp" />
<!DOCTYPE html>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href='http://fonts.googleapis.com/css?family=Archivo+Narrow' rel='stylesheet' type='text/css'>
        <title>Update Bids</title>
    </head>
    <body>


        <%
            Object roundNumObj = getServletConfig().getServletContext().getAttribute("roundNumber");
            int roundObj;
            if (roundNumObj == null) {
                roundObj = 0;
            } else {
                roundObj = (Integer) roundNumObj;
            }
            String resultMsg = (String) request.getAttribute("result");

            Student student = (Student) request.getSession().getAttribute("student");
            String deleteBidMessage = (String) request.getAttribute("resultForSuccessDelete");
            String error = (String) request.getAttribute("errorMsg");
            String userid = student.getUserID();
            List<Bid> studentBidList = bidDAO.retrieveBidByStudent(userid);

        %>
        <form name="updateBid" action="./updateBidServlet">

            <div style="margin-left: 100px"><br>

                <label class="fontCSS"> Your current bids placed: </label><br>
                <%
                    if (resultMsg != null) {
                        out.println("<a class='icon-exclamation-sign largeIcon'><a class='fontCSS'>" + resultMsg + "</a></a>");
                    }
                    if (deleteBidMessage != null) {
                        out.println("<a class='icon-exclamation-sign largeIcon'><a class='fontCSS'>" + deleteBidMessage + "</a></a><br>");
                    }

                    if (studentBidList.size() == 0) {
                        out.println("<a class='icon-exclamation-sign largeIcon'><a class='fontCSS'> You have not yet placed any bids at the moment!</a></a><br/>");
                    } else {

                %>

                <table class="fontCSS table-hover table-bordered" width="700px">
                    <tr class="alert-danger">
                        <th>Course Code</th>
                        <th>Course Title</th>
                        <th>Section</th>
                        <th>Amount</th>
                            <%
//                                if (roundObj != 2) {
                            %>
                        <!--<th>Update</th>-->
                            <% // }%>
                        <th>Delete</th>
                    </tr>

                    <%
                        for (Bid b : studentBidList) {
                            out.println("<tr>");
                            out.println("<td>" + b.getCode() + "</td>");
                            out.println("<td>" + courseDAO.retreiveCourseByCode(b.getCode()).getTitle() + "</td>");
                            out.println("<td>" + b.getSection() + "</td>");
                            if (roundObj != 2) {
                                out.println("<td><input type='text' name='amount' value='" + b.getAmount() + "'/></td>");
                                out.println("<input type='hidden' name='bidsCourseCode' value='" + b.getCode() + "'/>");
                            } else {
                                out.println("<td>" + b.getAmount() + "</td>");
                            }
                            out.println("<td><a href='./DropBidServlet?course=" + b.getCode() + " &section=" + b.getSection() + " &amount=" + b.getAmount() + "'><button type='button' class='btn btn-success icon-trash MediumIcon'></button></a></td>");
                            out.println("</tr>");
                        }
                    %>
                </table><br>
                <%
                    if (roundObj != 2) {
                %>
                <input class='btn btn-success' type ="submit" value="Update Bid"/><br><br>
                <% }%>
                </form>
                <%
                    }
                %>

                <label class="fontCSS"> Your course(s): </label><br>
                <%

                    String msg = (String) request.getAttribute("resultForSuccessBid");

                    if (msg != null) {
                        out.println(msg + "</br>");
                    }
                    List<Bid> successfulBids = SectionStudentDAO.retrieveBidsByStudent(userid);


                    if (roundObj == 2) {
                        List<Bid> successfulBidsRound2 = SectionStudentRound2DAO.retrieveBidsByStudent(userid);
                        successfulBids.addAll(successfulBidsRound2);
                    }

                    if (error != null) {
                        out.println("<a class='icon-exclamation-sign largeIcon'><a class='fontCSS'>" + error + "</a></a><br/>");
                    }

                    if (successfulBids.size() == 0) {
                        out.println("<a class='icon-exclamation-sign largeIcon'><a class='fontCSS'> You don't have any confirmed courses at the moment yet!</a></a><br/>");

                    } else {

                %>
                <table class="fontCSS table-hover table-bordered">
                    <tr class="alert-danger">
                        <th>Course</th>
                        <th>Section</th>
                        <th>Amount Bided</th>
                        <th>Drop Course</th>
                    </tr>
                    <%
                        for (Bid b : successfulBids) {
                            out.println("<tr>");
                            out.println("<td>" + b.getCode() + "</td>");
                            out.println("<td>" + b.getSection() + "</td>");
                            out.println("<td>" + b.getAmount() + "</td>");
                            out.println("<td><a href='./dropSectionServlet?course=" + b.getCode() + " &amount=" + b.getAmount() + "'>drop</a></td>");
                            out.println("</tr>");
                        }
                    %>
                </table>
                <%
                    }
                %>

            </div>

            
    </body>
</html>

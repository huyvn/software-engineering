<%@page import="com.bios.dao.SectionStudentRound2DAO"%>
<%@include file="protect.jsp"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.bios.dao.bidDAO"%>
<%@page import="com.bios.dao.SectionStudentDAO"%>
<%@page import="com.bios.dao.courseDAO"%>
<%@page import="java.util.List"%>
<%@page import="com.bios.model.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<jsp:include page="NavigationBar.jsp" />
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="css/bootstrap.min.css" rel="stylesheet" media="screen">
        <link type="text/css" rel="stylesheet" href="css/style.css">
        <link href="//netdna.bootstrapcdn.com/font-awesome/3.2.1/css/font-awesome.css" rel="stylesheet">
        <link href='http://fonts.googleapis.com/css?family=Archivo+Narrow' rel='stylesheet' type='text/css'>
        <script src="js/bootstrap.min.js"></script>
        <script src="js/bootstrap.js"></script>
    </head>

    <body>


        <%
            List<Bid> studentBidList = (List<Bid>) request.getAttribute("bidStudentList");
            List<CourseComplete> courseCompleteStudentList = (List<CourseComplete>) request.getAttribute("courseCompletedStudentList");
            Student student = (Student) request.getSession().getAttribute("student");
            String userid = student.getUserID();

        %>

     

           
                <div class="col-sm-5" style="margin-left: 10px">
                    <label class="fontCSS"> Completed Course(s):</label><br>


                    <%


                        if (courseCompleteStudentList.size() != 0) {


                            out.println("<table class='fontCSS table-hover table-bordered' width='380px'><tr style='background-color:#EFEEEC'>");
                            out.println("<th>Course Code&nbsp;</th><th>Title&nbsp;</th><th>Status&nbsp;</th></tr>");


                            for (CourseComplete c : courseCompleteStudentList) {
                                out.println("<tr><td>" + c.getCode() + "</td>");
                                out.println("<td>" + courseDAO.retreiveCourseByCode(c.getCode()).getTitle() + "</td>");
                                out.println("<td>Completed</td></tr>");
                            }

                            out.println("</table>");
                        } else {
                            out.println("<a class='icon-exclamation-sign largeIcon'><a class='fontCSS'> You have not yet completed any courses.</a></a><br>");
                        }


                    %>


                    <br>

                    <label class="fontCSS"> Bids Placed:</label><br>



                    <%
                        Object roundNoObj = getServletConfig().getServletContext().getAttribute("roundNumber");
                        int roundNo = 0;
                        if (roundNoObj != null) {
                            roundNo = (Integer) roundNoObj;
                        }
                        if (studentBidList.size() != 0 && (roundNo == 1)) {

                            out.println("<table class='fontCSS table-hover table-bordered' width='380px'><tr class='alert-warning'>");
                            out.println("<th>Course Code&nbsp;</th><th>Title&nbsp;</th><th>Section&nbsp;</th><th>Amount&nbsp;</th><th>Status&nbsp;</th></tr>");


                            for (Bid b : studentBidList) {
                                out.println("<tr>");
                                out.println("<td>" + b.getCode() + "</td>");
                                out.println("<td>" + courseDAO.retreiveCourseByCode(b.getCode()).getTitle() + "</td>");
                                out.println("<td>" + b.getSection() + "</td>");
                                out.println("<td>" + b.getAmount() + "</td>");
                                if (roundNo == 2) {
                                    out.println("<td>Unsuccessful</td>");
                                } else {
                                    out.println("<td>Pending</td>");
                                }
                                out.println("</tr>");
                            }
                            out.println("</table>");

                        } else {
                            out.println("<a class='icon-exclamation-sign largeIcon'><a class='fontCSS'> You have not yet placed any bids.</a></a><br>");
                        }
                    %>


                    <br>

                    <label class="fontCSS"> Your confirmed course(s): </label><br>
                    <%

                        String msg = (String) request.getAttribute("resultForSuccessBid");

                        if (msg != null) {
                            out.println(msg + "</br>");
                        }
                        List<Bid> successfulBids = SectionStudentDAO.retrieveBidsByStudent(userid);
                        if (roundNo == 2) {
                            List<Bid> round2Bids = SectionStudentRound2DAO.retrieveBidsByStudent(userid);
                            successfulBids.addAll(round2Bids);
                        }
                        if (successfulBids.size() == 0) {
                            out.println("<a class='icon-exclamation-sign largeIcon'><a class='fontCSS'> You don't have any confirmed courses at the moment!</a></a><br/>");

                        } else {

                    %>


                    <table class="fontCSS table-hover table-bordered" width='380px'>
                        <tr class="alert-success">
                            <th>Course</th>
                            <th>Section</th>
                            <th>Title</th>
                            <th>Amount</th>
                            <th>Status</th>
                        </tr>

                        <%

                            for (Bid b : successfulBids) {
                                out.println("<tr>");
                                out.println("<td>" + b.getCode() + "</td>");
                                out.println("<td>" + b.getSection() + "</td>");
                                out.println("<td>" + courseDAO.retreiveCourseByCode(b.getCode()).getTitle() + "</td>");
                                out.println("<td>" + b.getAmount() + "</td>");
                                out.println("<td> Success</td>");
                                out.println("</tr>");
                            }
                        %>
                    </table>
                    <%
                        }
                    %>

                    <br>

                    <%
                        Object roundNumObj = getServletConfig().getServletContext().getAttribute("roundNumber");

                        int roundObj;
                        if (roundNumObj == null) {
                            roundObj = 0;

                        } else {

                            roundObj = (Integer) roundNumObj;
                            if (roundObj == 10 || roundObj == 20 || roundObj == 2) {
                                List<Bid> unsuccessfulBids = bidDAO.retrieveBidByStudent(userid);
                                if (unsuccessfulBids.isEmpty()) {
                                    out.println("<label class=\"fontCSS\"> Your unsuccessful Courses: </label><br>");
                                    out.println("<a class='icon-exclamation-sign large"
                                            + "Icon'><a class='fontCSS'> You don't have "
                                            + "any unsuccessful bids at the moment!</a>"
                                            + "</a><br/>");
                                } else {

                    %>  
                    <label class="fontCSS"> Your unsuccessful Course(s): </label><br>
                    <table class="fontCSS table-hover table-bordered" width='380px'>
                        <tr  class="alert-danger">
                            <th>Course</th>
                            <th>Section</th>
                            <th>Title</th>
                            <th>Amount</th>
                            <th>Status</th>
                        </tr>

                        <%
                                        for (Bid bid : unsuccessfulBids) {
                                            out.println("<tr>");
                                            out.println("<td>" + bid.getCode() + "</td>");
                                            out.println("<td>" + bid.getSection() + "</td>");
                                            out.println("<td>" + courseDAO.retreiveCourseByCode(bid.getCode()).getTitle() + "</td>");
                                            out.println("<td>" + bid.getAmount() + "</td>");
                                            out.println("<td>" + "Failed" + "</td>");
                                            out.println("</tr>");
                                        }
                                    }
                                }
                            }
                        %> 
                    </table>
                </div>
                <div class="col-sm-6"><jsp:include page="viewTimetable.jsp"/></div>

        <%
            if (roundObj == 2) {
        %>
        <script type="text/javascript">
            setTimeout(function() {
                location.reload(true);
            }, 10000);
        </script>
        <%            }
        %>

    </body>
</html>



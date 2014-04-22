<%@page import="com.bios.dao.*"%>
<%@include file="protect.jsp"%>
<%@page import="com.bios.dao.courseDAO"%>
<%@page import="com.bios.model.*"%>
<%@page import="java.util.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:include page="NavigationBar.jsp" />
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href='http://fonts.googleapis.com/css?family=Archivo+Narrow' rel='stylesheet' type='text/css'>
    </head>
    <body>

        <%
            String error = (String) request.getAttribute("errorMsg");
            Student student = (Student) session.getAttribute("student");
            if (error != null) {
                out.println("<ul><li class='fontCSS'>" + error + "</li></ul>");

                response.sendRedirect("/viewCourseSection?userid=" + student.getUserID());
                return;

            } else {

                LinkedHashMap<String, List<Section>> courseWithSections =
                        (LinkedHashMap<String, List<Section>>) request.getAttribute("courseWithSections");

                Set<String> courseCodes = courseWithSections.keySet();
                Iterator iter = courseCodes.iterator();
                while (iter.hasNext()) {
                    String courseCode = (String) iter.next();
                    List<Section> sectionList = courseWithSections.get(courseCode);

        %>

        <div style="margin-left: 100px">
            <label class="fontCSS"> For Course: <%= courseCode%>, <%=courseDAO.retreiveCourseByCode(courseCode).getTitle()%> </label>


            <form action="viewSection.jsp">
                <table class="fontCSS table-hover table-bordered" width="800px">
                    <tr class="alert-warning">
                        <th>Section</th>
                        <th>Day</th>
                        <th>Start Time</th>
                        <th>End Time</th>
                        <th>Instructor</th>
                        <th>Venue</th>
                        <th>Size</th>
                        <th>Add Bid</th>
                    </tr>
                    <%
                        for (Section s : sectionList) {
                    %>

                    <tr>
                        <td> <%= s.getSection()%> </td>         
                        <td> <%= s.getDay()%> </td>           
                        <td> <%= s.getStart()%> </td>            
                        <td> <%= s.getEnd()%> </td>
                        <td> <%= s.getInstructor()%> </td>          
                        <td> <%= s.getVenue()%> </td>          
                        <td> <%= s.getSize()%> </td>
                        <td> <a href="viewSection?courseCodes=<%=courseCode%>&section=<%=s.getSection()%>"class='icon-ok'/> Bid </a>
                    </tr>
                    <%
                        }
                    %>
                </table>
                <br>
                <%
                    }//end of while loop

                %>
            </form>
        </div>
        <%
            request.setAttribute("courseWithSections", courseWithSections);
            Object roundNumObj = getServletConfig().getServletContext().getAttribute("roundNumber");
            int roundObj;
            if (roundNumObj == null) {
                roundObj = 0;
            } else {
                roundObj = (Integer) roundNumObj;
            }
            String courseCode = (String) request.getAttribute("code"); //from addBidServlet
            String section = (String) request.getAttribute("section"); //from addBidServlet
            student = (Student) request.getSession().getAttribute("student");
            String userid = student.getUserID();

//            if (courseCode == null || section == null) {
//                courseCode = request.getParameter("course"); //from viewSection
//                section = request.getParameter("section"); //from viewSection
//            }

            if (courseCode != null && section != null) {

                ArrayList<String> errorMsgList = (ArrayList<String>) request.getAttribute("errorList");
        %>
        <form action="./addBidServlet">
            <div style="margin-left: 100px"><br>
                <label class="fontCSS">Selected Course Detail:</label>
                <table class="fontCSS table-hover table-bordered">
                    <tr class="alert-link">
                        <th>Course</th>
                        <td><label><%=courseCode%></label>
                        </td>
                    </tr>
                    <tr>
                        <th>Course Title</th>
                        <td><label><%=courseDAO.retreiveCourseByCode(courseCode).getTitle()%></label></td>
                    </tr>
                    <tr>
                        <th>Section</th>
                        <td><label><%=section%></label></td>
                    </tr>
                    <%
                        if (roundObj == 2) {
                            Section s = sectionDAO.retrieveCourseSection(courseCode, section);
                            List<Bid> pastSuccessfulBids = SectionStudentDAO.retrieveBidsBySection(courseCode, section);
                            int availableSlots = s.getSize() - pastSuccessfulBids.size();
//                            List<Bid> currentSuccessfulBids = SectionStudentRound2DAO.retrieveBidsBySection(courseCode, section);
//                            int freeSlots = availableSlots - currentSuccessfulBids.size();
                            out.println("<tr>");
                            out.println("<th>Vacancies</th>");
                            out.println("<td><label>" + availableSlots + "</label></td>");
                            out.println("</tr>");

                            HashMap<String, Double> minBidPrices = (HashMap<String, Double>) getServletContext().getAttribute("minBidPrices");
                            if (minBidPrices == null) {
                                minBidPrices = new HashMap<String, Double>();
                            }
                            double minBidPrice = 10.0;
                            Double pastMinBidPrice = minBidPrices.get(courseCode + section);
                            if (pastMinBidPrice != null) {
                                minBidPrice = pastMinBidPrice;
                            }
                            out.println("<tr>");
                            out.println("<th>Min Bid</th>");
                            out.println("<td><label>" + minBidPrice + "</label></td>");
                            out.println("</tr>");
                        }
                    %>
                    <tr>
                        <th>New Bid Amount:</th>
                        <td><input type="text" name="amount" required/></td>
                    </tr>

                </table><br>
                <div>
                    <input type="submit" class="btn btn-success" value="Add Bid"/>
                    <a href="viewCourseSection?courseid=" class='btn btn-primary'/>Back</a><br><br>
                </div>

                <%
                    //String userid = (String) request.getSession().getAttribute("userid");
                    if (errorMsgList != null) {
                        out.println("<a class='icon-exclamation-sign largeIcon'><a class='fontCSS'> Error Occured:</a></a><br/>");
                        out.println("<a><li class='fontCSS'>" + errorMsgList.get(0) + "</li></a>");

                    }
                %>


            </div>

            <input type="hidden" name="courseCode" value="<%=courseCode%>"/>
            <input type="hidden" name="section" value="<%=section%>"/>
        </form> 
        <%
                }//end of else
            }
        %>
    </body>
</html>

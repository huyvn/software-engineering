<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.google.gson.Gson"%>
<%@page import="com.bios.dao.courseDAO"%>
<%@include file="protect.jsp"%>
<%@page import="com.bios.model.*"%>
<%@page import="java.util.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:include page="NavigationBar.jsp" />

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="css/bootstrap.min.css" rel="stylesheet" media="screen">
        <link type="text/css" rel="stylesheet" href="css/style.css">
        <script src="js/jquery-1.4.4.min.js" type="text/javascript"></script>
        <script src="js/smartpaginator.js" type="text/javascript"></script>
        <link href="css/smartpaginator.css" rel="stylesheet" type="text/css" />
        <link href='http://fonts.googleapis.com/css?family=Archivo+Narrow' rel='stylesheet' type='text/css'>
        <link rel="stylesheet" href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css" />
        <script src="http://code.jquery.com/jquery-1.9.1.js"></script>
        <script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>
        <title>Course</title>
    </head>
    <body>
        <%
            ServletContext context = getServletConfig().getServletContext();
            Integer roundNumber = (Integer) context.getAttribute("roundNumber");

            String error = (String) request.getAttribute("errorMsg");
            List<Course> courseStudent = (List<Course>) request.getAttribute("courseStudent");
            int totalRecord = 0;
            if (courseStudent == null || courseStudent.size() == 0) {
                courseStudent = new ArrayList<Course>();
                totalRecord = 0;
            } else {
                totalRecord = courseStudent.size();
            }

            List<Course> listAll = courseDAO.retrieveAll();
            String[] courseCodeList = new String[listAll.size()];
            for (int i = 0; i < listAll.size(); i++) {
                courseCodeList[i] = listAll.get(i).getCode() + " : " + listAll.get(i).getTitle();
            }
            Gson gson = new Gson();
            String courseJson = gson.toJson(courseCodeList);
            if (error != null) {
                out.println("<a class='icon-exclamation-sign largeIcon'><a class='fontCSS'>" + error + "</a></a>");
            } else {
        %>

        <script>
            $(document).ready(function() {
                var availableTags = <%=courseJson%>;
                $("#tags").autocomplete({
                    source: $.each(availableTags, function(index, value) {
                        return this;
                    })
                });
            });
        </script>
        <script type="text/javascript">
            function UserSelectedCourse() {
                var Searchtxt = document.getElementById("tags").value;
                window.location = "./viewCourseSection?courseid=" + encodeURIComponent(Searchtxt);
            }
        </script>
        <div class="ui-widget" style="margin-left: 10%">
            <label for="tags" class="fontCSS">Search for Course: </label>
            <input name="tags" id="tags" style="width: 45%"/> 
            <input type="submit" class ="btn btn-success" value="Get Course" onclick="javascript:UserSelectedCourse();
                return false;" />
        </div>

        <div style="align: center; width:90%; margin-left: 60px" >
            <div ><br>
                <%if (courseStudent == null || courseStudent.isEmpty()) {
                        out.println("<h3>Invalid Search Query</h3>");
                        out.println("<h5>Either you keyed in an invalid query</h5>");
                        out.println("<h5>or you have already completed this course</h5>");
                        out.println("<h5>or you have already bidded for it.</h5>");
                    } else {%>
                <table id="courseTable" class="content fontCSS table-hover table-bordered"><br>
                    <tr class="alert-success">
                        <th>Course Code</th>
                        <th>School</th>
                        <th>Title</th>
                        <th>Description</th>
                        <th>Exam Date</th>
                        <th>Start Time</th>
                        <th>End Time</th>
                        <th> View section</th>
                    </tr>
                    <%
                        SimpleDateFormat spd = new SimpleDateFormat("dd-MM-yyyy");
                        for (Course c : courseStudent) {
                            String examDate = c.getExam_Date();
                            String stringYear = examDate.substring(0, 4);
                            String stringMonth = examDate.substring(4, 6);
                            String stringDay = examDate.substring(6);
                            int year = Integer.parseInt(stringYear);
                            int month = Integer.parseInt(stringMonth);
                            int day = Integer.parseInt(stringDay);
                    %>
                    <tr id="courseDetails">
                        <td style="width: 8%"> <%= c.getCode()%> </td>         
                        <td> <%= c.getSchool()%> </td>           
                        <td> <%= c.getTitle()%> </td>            
                        <td> <%= c.getDescription()%> </td>
                        <td style="width: 8%"> <%= day+"-"+month+"-"+year%> </td>          
                        <td style="width: 8%"> <%= c.getExam_Start()%> </td>          
                        <td style="width: 8%"> <%= c.getExam_End()%> </td>
                        <td><a href="./viewSection?courseCodes=<%= c.getCode()%>"><button type="button" class="btn btn-success">View</button></a></td>
                    </tr>
                    <%
                        }
                    %>
                </table><br>   
                <div id="green"></div>
                <%}
                    }                    //end of else %>
            </div>
        </div> <br>  
        <script type="text/javascript">
            $(document).ready(function() {
                $('#green').smartpaginator({totalrecords: <%=totalRecord%>, recordsperpage: 6, datacontainer: 'courseTable', dataelement: 'tr', initval: 0, next: 'Next', prev: 'Prev', first: 'First', last: 'Last', theme: 'green'});

            });
        </script>
    </body>
</html>

<%@page import="com.bios.dao.SectionStudentRound2DAO"%>
<%@include file="protect.jsp"%>
<%@page import="com.bios.dao.courseDAO"%>
<%@page import="com.bios.dao.sectionDAO"%>
<%@page import="com.bios.dao.bidDAO"%>
<%@page import="com.bios.dao.SectionStudentDAO"%>
<%@page import="java.util.*"%>
<%@page import="com.bios.model.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    </head>
    <body>
        <table width="500px">
            <th>    
                <label class="fontCSS"> My Time-Table</label> </th> 
            <th><input type="text" style="width: 15px; border: 0; height: 15px; padding: 2px; background-color: #BCED91;" disabled/>
                <label class="fontCSS">Success</label> 
                <input type="text" style="width: 15px; border: 0; height: 15px; padding: 2px; background-color: #FFCBA4;" disabled/>
                <label class="fontCSS">Pending</label></th>
        </table>
        <%!
            private String getRowTimetable(List<Section> classesPerTimeslot,
                    List<Section> enrolledClasses, List<Section> pendingClasses) {
                String tableRow = "";
                Section[] classes = new Section[7];
                for (Section eachClass : classesPerTimeslot) {
                    int day = eachClass.getDay();
                    classes[day - 1] = eachClass;
                }
                for (int i = 0; i <= 6; i++) {
                    Section enrolledClass = classes[i];
                    if (enrolledClass != null) {
                        if (enrolledClasses.contains(enrolledClass)) {

                            //tableRow += "<td bgcolor='#BCED91' style='font-size: 13px'>";
                            tableRow += "<td  style='font-size: 13px; background-color:#BCED91;'>";
                        } else if (pendingClasses.contains(enrolledClass)) {
                            //tableRow += "<td bgcolor='#FFCBA4' style='font-size: 13px'>";
                            tableRow += "<td style='font-size: 13px; background-color:#FFCBA4;'>";
                        }
                        tableRow += enrolledClass.getCourse() + "</br>";
                        tableRow += enrolledClass.getSection() + "</br>";
                        tableRow += enrolledClass.getInstructor() + "</br>";
                        tableRow += enrolledClass.getVenue();
                        tableRow += "</td>";
                    } else {
                        tableRow += "<td></td>";
                    }
                }
                return tableRow;
            }

            private ArrayList<Section> getClassesPerTimeslot(List<Section> totalSections, int endTime) {
                ArrayList<Section> classesPerTimeslot = new ArrayList<Section>();
                for (Section eachSection : totalSections) {
                    String classEndTimeStr = eachSection.getEnd();
                    String clearClassEndTimeStr = classEndTimeStr.replace(":", "");
                    int classEndTime = Integer.parseInt(clearClassEndTimeStr);
                    if (classEndTime == endTime) {
                        classesPerTimeslot.add(eachSection);
                    }
                }
                return classesPerTimeslot;
            }
        %>
        <%
            try {
                Object roundNoObj = getServletConfig().getServletContext().getAttribute("roundNumber");
                int roundNo = 0;
                if (roundNoObj != null) {
                    roundNo = (Integer) roundNoObj;
                }
                List<Section> enrolledClasses = (List<Section>) request.getAttribute("enrolledClasses");
                List<Section> pendingClasses = (List<Section>) request.getAttribute("pendingClasses");

                List<Section> totalClasses = new ArrayList<Section>();

                Iterator<Section> it = enrolledClasses.iterator();
                while (it.hasNext()) {
                    Section s = it.next();
                    totalClasses.add(s);
                }
                if (pendingClasses != null) {
                    it = pendingClasses.iterator();
                    while (it.hasNext()) {
                        Section s = it.next();
                        totalClasses.add(s);
                    }
                }

                List<Section> classesEnd1145 = getClassesPerTimeslot(totalClasses, 1145);
                List<Section> classesEnd1515 = getClassesPerTimeslot(totalClasses, 1515);
                List<Section> classesEnd1845 = getClassesPerTimeslot(totalClasses, 1845);
                List<Section> classesEnd2215 = getClassesPerTimeslot(totalClasses, 2215);
                String row1145 = getRowTimetable(classesEnd1145, enrolledClasses, pendingClasses);
                String row1515 = getRowTimetable(classesEnd1515, enrolledClasses, pendingClasses);
                String row1845 = getRowTimetable(classesEnd1845, enrolledClasses, pendingClasses);
                String row2215 = getRowTimetable(classesEnd2215, enrolledClasses, pendingClasses);
        %>
        <table class="fontCSS table-striped" width="560px">
            <tr class="alert-warning">
                <th></th>
                <th> Mon. &nbsp;</th>
                <th> Tues. &nbsp;</th>
                <th> Weds. &nbsp;</th>
                <th> Thurs. &nbsp;</th>
                <th> Fri. &nbsp;</th>
                <th> Sat. &nbsp;</th>
                <th> Sun. &nbsp;</th>
            </tr>
            <tr>
                <td>8:30<br/>-<br/>11:45</td>
                    <%=row1145%>
            </tr>
            <tr>
                <td>12:00<br/>-<br/>15:15</td>
                    <%=row1515%>
            </tr>
            <tr>
                <td>15:30<br/>-<br/>18:45</td>
                    <%=row1845%>
            </tr>
            <tr>
                <td>19:00<br/>-<br/>22:15</td>
                    <%=row2215%>
            </tr>
        </table>
        <br>


        <%

            List<String> SuccessCourseCodes = new ArrayList<String>();
            List<String> PendingCourseCodes = new ArrayList<String>();
            List<Course> SuccessExamList = new ArrayList<Course>();
            List<Course> PendingExamList = new ArrayList<Course>();

            Student stu = (Student) request.getSession().getAttribute("student");
            String userid = stu.getUserID();
            List<Bid> successfulBids = SectionStudentDAO.retrieveBidsByStudent(userid);
            List<Bid> pendingBids = null;

            if (roundNo == 2) {
                List<Bid> round2Bids = SectionStudentRound2DAO.retrieveBidsByStudent(userid);
                successfulBids.addAll(round2Bids);
            }

            if (roundNo == 0 || roundNo == 10 || roundNo == 20) {
                pendingBids = new ArrayList<Bid>();
            } else {
                pendingBids = bidDAO.retrieveBidByStudent(userid);
            }

            //loop successfulbids list 
            //pass successful bids course code to successCourseCodes array
            for (Bid eachBid : successfulBids) {
                String courseCode = eachBid.getCode();
                SuccessCourseCodes.add(courseCode);
            }

            //loop pendingBids list 
            //pass pending bids course code to pendingCourseCodes array
            for (Bid eachBid : pendingBids) {
                String courseCode = eachBid.getCode();
                PendingCourseCodes.add(courseCode);
            }

            //loop SuccessCourseCodes array and retrive course object 
            //pass this course object to successExamList
            for (String course : SuccessCourseCodes) {
                Course eachCourse = courseDAO.retreiveCourseByCode(course);
                SuccessExamList.add(eachCourse);
            }

            //loop PendingCourseCodes array and retrive course object 
            //pass this course object to pendingExamList
            for (String course : PendingCourseCodes) {
                Course eachCourse = courseDAO.retreiveCourseByCode(course);
                PendingExamList.add(eachCourse);
            }
        %>

        <label class="fontCSS"> Exam Time-Table for Confirmed Courses:</label><br>

        <%
            if (SuccessExamList == null || SuccessExamList.size() == 0 || roundNo == 0) {
                out.println("<a class='icon-exclamation-sign largeIcon'><a class='fontCSS'> You don't have any exams at the moment!</a></a><br/>");
            } else {

        %>

        <table class="fontCSS table-hover table-bordered" width="560px">
            <tr class="alert-success">
                <th>Code</th>
                <th>Title</th>
                <th style="width: 100px">Date</th>
                <th>Start Time</th>
                <th>End Time</th>          
            </tr>
            <%
                for (Course eachSuccessCourse : SuccessExamList) {

                    String examDate = eachSuccessCourse.getExam_Date();
                    String stringYear = examDate.substring(0, 4);
                    String stringMonth = examDate.substring(4, 6);
                    String stringDay = examDate.substring(6);
                    int year = Integer.parseInt(stringYear);
                    int month = Integer.parseInt(stringMonth);
                    int day = Integer.parseInt(stringDay);
            %>
            <tr> 
                <td><%= eachSuccessCourse.getCode()%></td>
                <td><%= eachSuccessCourse.getTitle()%></td>
                <td style="width: 100px"><%= day+"-"+month+"-"+year%></td>
                <td><%= eachSuccessCourse.getExam_Start()%></td>
                <td><%= eachSuccessCourse.getExam_End()%></td>         
            </tr>

            <%
                    }
                }

            %>
        </table><br>
        <label class="fontCSS"> Exam Time-Table for Pending Courses:</label><br>
        <%

            if (PendingExamList == null || PendingExamList.size() == 0 || roundNo == 0) {
                out.println("<a class='icon-exclamation-sign largeIcon'><a class='fontCSS'> You don't have any exams at the moment!</a></a><br/>");
            } else {
        %>

        <table class="fontCSS table-hover table-bordered" width="560px">
            <tr class="alert-warning">
                <th>Code</th>
                <th>Title</th>
                <th style="width: 100px">Date</th>
                <th>Start Time</th>
                <th>End Time</th>          
            </tr>
            <%

                for (Course eachPendingCourse : PendingExamList) {
                    String examDate = eachPendingCourse.getExam_Date();
                    String stringYear = examDate.substring(0, 4);
                    String stringMonth = examDate.substring(4, 6);
                    String stringDay = examDate.substring(6);
                    int year = Integer.parseInt(stringYear);
                    int month = Integer.parseInt(stringMonth);
                    int day = Integer.parseInt(stringDay);
            %>
            <tr> 
                <td><%= eachPendingCourse.getCode()%></td>
                <td><%= eachPendingCourse.getTitle()%></td>
                <td style="width: 100px"><%= day+"-"+month+"-"+year%></td>
                <td><%= eachPendingCourse.getExam_Start()%></td>
                <td><%= eachPendingCourse.getExam_End()%></td>         
            </tr>
            <%
                    }
                }
            %>
        </table><br>


        <%

                //}end else

            } catch (Exception e) {
                out.println(e);
            }
        %>
    </body>
</html>

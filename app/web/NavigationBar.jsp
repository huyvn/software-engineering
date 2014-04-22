
<%@page import="java.text.DecimalFormat"%>
<%@page import="com.bios.dao.studentDAO"%>
<%@page import="com.bios.model.Bid"%>
<%@page import="com.bios.model.CourseComplete"%>
<%@page import="java.util.List"%>
<%@page import="com.bios.model.Student"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="css/bootstrap.min.css" rel="stylesheet" media="screen">
        <link type="text/css" rel="stylesheet" href="css/style.css">
        <link href="//netdna.bootstrapcdn.com/font-awesome/3.2.1/css/font-awesome.css" rel="stylesheet">
        <link href='http://fonts.googleapis.com/css?family=Shadows+Into+Light+Two' rel='stylesheet' type='text/css'>
        <script src="js/bootstrap.min.js"></script>
        <script src="js/bootstrap.js"></script>
    </head>
    <%
        ServletContext context = getServletConfig().getServletContext();
        Student student = (Student) session.getAttribute("student");
        String roundInfo = "";
        
        double edollar = studentDAO.retrieveStudentEdollar(student);
        DecimalFormat df = new DecimalFormat("#.##");
        String edollarStr = df.format(edollar);
        if (context.getAttribute("roundNumber") == null) {
            roundInfo = "No Round Started!";
        } else {
            int roundNum = (Integer) context.getAttribute("roundNumber");
            if (roundNum == 1) 
                roundInfo = "Current Round: 1";
            else if(roundNum == 2)
                roundInfo = "Current Round: 2";
            else if(roundNum == 10)
                roundInfo = "Round 1 Ended";
            else
                roundInfo = "Round 2 Ended";



        }

    %>
    <body>
        <div class=" navbar-fixed-top nav fontCSS" style="padding: 10px 8px 8px 8px;">
            
            <i class="MediumIcon icon-home"></i><a href="./viewAccount" class="fontColor"  style="margin-right: 20px">BIOS</a>        
                <i class="MediumIcon icon-time"></i>  <%=roundInfo%>
                <i class="MediumIcon icon-male"  style="margin-left: 20px;"></i>   Name:<%= student.getName()%>
                <i class="MediumIcon icon-dollar" style="margin-left: 20px"> </i> Balance:<%= edollarStr%>
                <i class="MediumIcon icon-building" style="margin-left: 20px"> </i> School:<%= student.getSchool()%>
                <a href="./viewCourseSection?courseid="><label style="color: #ffffff;margin-left: 70px; text-decoration: underline"> Add Bids</label></a>
                <a href="./viewBid.jsp"><label style="color: #ffffff;margin-left: 20px; text-decoration: underline">Edit Bids</label></a>
                <a href="./Logout"><label style="color: #ffffff;margin-left: 20px; text-decoration: underline"> Logout</label></a>

        </div>
                


        <div class="row">
            <!-- empty --><br></br>    
            <!-- empty --><br></br> 

        </div>
    </body>
</html>

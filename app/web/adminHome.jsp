
<%@include file="protectAdmin.jsp"%>
<%@page import="com.bios.model.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%
    String admin = (String) request.getSession().getAttribute("admin");
    if (admin == null) {
        response.sendRedirect("index.jsp");
        return;
    }
%>
<html  style="overflow: hidden">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="css/bootstrap.min.css" rel="stylesheet" media="screen">
        <link type="text/css" rel="stylesheet" href="css/style.css">
        <link href="//netdna.bootstrapcdn.com/font-awesome/3.2.1/css/font-awesome.css" rel="stylesheet">
        <link href='http://fonts.googleapis.com/css?family=Archivo+Narrow' rel='stylesheet' type='text/css'>
        <script src="js/bootstrap.min.js"></script>
        <script src="js/bootstrap.js"></script>
        <title>Admin Panel</title>
    </head>
    <body>
        <div class="navbar navbar-default navbar-fixed-top">
            <div class="navbar-header">
               <a href=""><i class="navbar-brand MediumIcon icon-home"> Merlion University Bidding Online System (BIOS)</i></a> 
            </div>
        </div> <br><br><br><br><br>
            <div align="center">
            <h1 class="fontCSS largeIcon">Welcome! Admin</h1>
            <form method="POST" action="index.jsp">
                <input class='btn btn-success' type="submit" value="Log Out">
            </form>
            <br/>
            <br/>
            <label class="fontCSS MediumIcon">TOOLS</label><br/><br/>
            <table class="fontCSS table-bordered" width="300px">
                <tr class="alert-info" align="left">
                    <th>Bootstrap</th>
                </tr>
                <tr>
                    <td>
                        <form method="post" action="./knngx/bootstrap" enctype="multipart/form-data">
                            Select file to upload: </br>
                            <input type="file" name="dataFile" id="fileChooser" onchange="checkSelectedFile();"/><br/>
                    
            <input class='btn btn-success' type="submit" value="Upload" id="submit" disabled/>
        </form>
                        </td>
                    
                </tr>
        </table>
            
        <script>
                            function checkSelectedFile() {
                                if (document.getElementById('fileChooser') !== null
                                        || document.getElementById('fileChooser').value !== "No file chosen") {
                                    document.getElementById('submit').disabled = false;
                                }
                                else {
                                    document.getElementById('submit').disabled = true;
                                }
                            }
        </script>
        <p/>

        <%
            ServletContext context = getServletConfig().getServletContext();
            String roundInfo = "";

            if (context.getAttribute("roundNumber") == null) {
                roundInfo = "No Round Started!";
            } else {
                int roundNum = (Integer) context.getAttribute("roundNumber");
                if (roundNum == 0) {
                    roundInfo = "No Round Started!";
                } else if (roundNum == 1) {
                    roundInfo = "Current Round: 1";
                } else if (roundNum == 2) {
                    roundInfo = "Current Round: 2";
                } else if (roundNum == 10) {
                    roundInfo = "Round 1 Ended";
                } else {
                    roundInfo = "Round 2 Ended";
                }
            }

        %>

        <br/>
        <br/>

        <strong class="fontCSS"><%=roundInfo%></strong>
        <table class="fontCSS table-hover table-bordered" border ="1" width="300px">
            <tr class="alert-info">
                <th>Start Round</th>
                <th>Stop Round</th>
            </tr>
            <tr>
                <td align="center">
                    <form action="./knngx/start">
                        <input class='btn btn-success' type="submit" value="Start">
                    </form>
                </td>
                <td align="center">
                    <form action="./knngx/stop">
                        <input class='btn btn-danger' type="submit" value="Stop">
                    </form>
                </td>
            </tr>
        </table>
        
    </div>
</body>
</html>

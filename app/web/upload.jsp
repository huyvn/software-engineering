<%@include file="protectAdmin.jsp"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>File Upload</title>
    </head>
    <body>
        <%
            String errorMessage = (String) request.getAttribute("error");
            if (errorMessage != null) {
                out.println("<font color='red'>"+errorMessage+"</font>");
            }
        %>
        <form method="post" action="./knngx/bootstrap" enctype="multipart/form-data">
            Select file to upload: </br>
            <input type="file" name="dataFile" id="fileChooser" onchange="checkSelectedFile();"/><br/><br/>
            <input type="submit" value="Upload" id="submit" disabled/>
        </form>
        <script>
            function checkSelectedFile(){
                if (document.getElementById('fileChooser') !== null
                        || document.getElementById('fileChooser').value !== "No file chosen"){
                    document.getElementById('submit').disabled = false;
                }
                else {
                    document.getElementById('submit').disabled = true;
                }
            }
        </script>
    </body>
</html>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <%
        String admin = (String) request.getSession().getAttribute("admin");
        if (admin != null) {
            request.getSession().removeAttribute("admin");
        }
    %>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link href="css/bootstrap.min.css" rel="stylesheet" media="screen">
        <link type="text/css" rel="stylesheet" href="css/style.css">
        <link href="//netdna.bootstrapcdn.com/font-awesome/3.2.1/css/font-awesome.css" rel="stylesheet">
        <script src="//code.jquery.com/jquery.js"></script>
        <script src="js/bootstrap.min.js"></script>
        <script src="js/bootstrap.js"></script>
        <title>BIOS Login Page</title>
        <script>

            $(document).ready(function() {

                $("#buttonAdmin").click(function() {
                    var str = "";
                    var userid = $("#adminid").val();
                    var password = $("#adminPassword").val();

//                    var url = "http://localhost:8084/BIOS_Team/Login/" +
//                            "authenticate_admin?r={adminid=\"" +
//                            userid + "\",password=\"" + password + "\"}";

                    var url = "http://2013-g1t8.rhcloud.com/Login/" +
                            "authenticate_admin?r={adminid=\"" +
                            userid + "\",password=\"" + password + "\"}";


                    console.log(url);

                    $.getJSON(url, function(data) {
                        var msg = "";
                        console.log(data.status);
                        var status = data.status;
                        if (status == "success") {
//                            window.location.href = "http://localhost:8084/BIOS_Team/adminHome.jsp";
                            window.location.href = "http://2013-g1t8.rhcloud.com/adminHome.jsp";
                        } else {
                            msg += "Your login was unsucessful. Please try again.";
                        }
                        str = "<h1>";
                        str += msg + " </h1>";
                        $("#adminLoginFailMessage").html(str);
                    });
                });
            });
        </script>
    </head>

    <body style="overflow-y: hidden;">

        <%--slide1--%>

        <img src="images/image2.jpg"/>
        <div class="container">
            <div class="carousel-caption" style="height:400px;">


                <div class="col-md-7 col-lg-offset-1">
                    <form class="form-horizontal span8">
                        <div class="form-group">
                            <label id="adminLoginFailMessage"></label>
                            <label class="icon-user col-md-3 MediumIcon"> Admin Login</label>
                        </div>
                        <div class="form-group">

                            <div class="input-group col-md-8">
                                <input type="text" id="adminid" placeholder="Your Username" class="form-control" required/>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="input-group col-md-8">
                                <input type="password" id="adminPassword" placeholder="Your Password" class="form-control" required>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="control-group">

                                <input type="button" class="btn btn-primary" value="Sign in" id="buttonAdmin" />
                                <input type="reset" class="btn btn-warning" value="Clear" id="clear" />


                            </div>
                        </div>
                    </form>
                </div> 

            </div>

        </div>
    </body>
</html>

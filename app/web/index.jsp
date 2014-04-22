

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html style="overflow: hidden">
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

                $("#buttonStudent").click(function() {
                    var str = "";
                    var userid = $("#userid").val();
                    var password = $("#password").val();

//                    var url = "http://localhost:8084/BIOS_Team/Login/" +
//                            "authenticate_student?r={userid=\"" +
//                            userid + "\",password=\"" + password + "\"}";

                    var url = "http://2013-g1t8.rhcloud.com/Login/" +
                            "authenticate_student?r={userid=\"" +
                            userid + "\",password=\"" + password + "\"}";


                    console.log(url);

                    $.getJSON(url, function(data) {
                        var msg = "";
                        console.log(data.status);
                        var status = data.status;
                        if (status == "success") {
//                            window.location.href = "http://localhost:8084/BIOS_Team/viewAccount";
                            window.location.href = "http://2013-g1t8.rhcloud.com/viewAccount";
                        } else {
                            msg += "Your login was unsucessful. Please try again.";

                        }
                        str = "<h1>";
                        str += msg + " </h1>";
                        $("#loginFailMessage").html(str);
                    });
                });
            });
        </script>
    </head>

    <body>

        <div class="navbar navbar-default navbar-fixed-top">
            <div class="navbar-header">

                <a href=""><i class="navbar-brand MediumIcon icon-home"> Merlion University Bidding Online System (BIOS)</i></a> 

            </div>
        </div>


        <div id="myCarousel" class="Carousel slide">   
            <ol class="carousel-indicators">
                <li data-target="#myCarousel" data-slide-to="0" class="active"></li>
                <li data-target="#myCarousel" data-slide-to="1"></li>
            </ol>


            <div class="carousel-inner">
                <%--slide1--%>
                <div class="item active">
                    <img src="images/image1.jpg"/>
                    <div class="container">
                        <div class="carousel-caption" style="height:400px;">


                            <div class="col-md-7 col-lg-offset-1">
                                <form class="form-horizontal span8">
                                    <div class="form-group">
                                        <label id="loginFailMessage"></label>
                                        <label class="icon-group col-md-3 MediumIcon"> Student Login</label>
                                    </div>
                                    <div class="form-group">
                                        <div class="input-group col-md-8">
                                            <input type="text" id="userid" placeholder="Your Username" class="form-control" required/>
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <div class="input-group col-md-8">
                                            <input type="password" id="password" placeholder="Your Password" class="form-control" required>

                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <div class="control-group">

                                            <input type="button" class="btn btn-primary" value="Sign in" id="buttonStudent" />
                                            <input type="reset" class="btn btn-warning" value="Clear" id="clear" />

                                        </div>
                                    </div>
                                </form>
                            </div> 

                        </div>

                    </div>

                </div>


                <%--slide2--%>

                <div class="item">

                    <jsp:include page="adminLogin.jsp"/>

                </div>

            </div>
            <div>
                <a class="left carousel-control" href="#myCarousel" data-slide="prev">
                    <span class="glyphicon glyphicon-chevron-left"><img src="images/left.png"/></span>
                </a>
                <a class="right carousel-control" href="#myCarousel" data-slide="next"> 
                    <span class="glyphicon-chevron-right"><img src="images/right.png"/></span>
                </a>
            </div>
        </div>

        <div class="navbar-fixed-bottom">
            <div class="navbar-header">
                <i class="fontColor"> © Copyright 2013 Merlion University. All rights reserved</i>
            </div>
        </div>

    </body>
</html>




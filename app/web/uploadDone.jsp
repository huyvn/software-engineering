<%@page import="com.google.gson.stream.JsonReader"%>
<%@page import="java.io.StringReader"%>
<%@page import="com.google.gson.*"%>
<%@page import="java.util.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>


<%
    LinkedHashMap<String, Object> messageReturn = (LinkedHashMap<String, Object>) request.getAttribute("status");
    application.setAttribute("roundNumber", 1);
    application.removeAttribute("minBidPrices");

    String status = (String) messageReturn.get("status");
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    String checkAdmin = (String) request.getSession().getAttribute("admin");

    if (checkAdmin == null) {

        String viewAll = gson.toJson(messageReturn);
        out.println(viewAll);
    } else {
        ArrayList<LinkedHashMap<String, Object>> num_records_loaded = (ArrayList<LinkedHashMap<String, Object>>) messageReturn.get("num-record-loaded");
        int recordsList = num_records_loaded.size();

        ArrayList<LinkedHashMap<String, Object>> errors = (ArrayList<LinkedHashMap<String, Object>>) messageReturn.get("error");
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="../css/bootstrap.min.css" rel="stylesheet" media="screen">
        <link type="text/css" rel="stylesheet" href="../css/style.css">
        <link href="//netdna.bootstrapcdn.com/font-awesome/3.2.1/css/font-awesome.css" rel="stylesheet">
        <link href='http://fonts.googleapis.com/css?family=Archivo+Narrow' rel='stylesheet' type='text/css'>
        <script src="../js/bootstrap.min.js"></script>
        <script src="../js/bootstrap.js"></script>
    </head>
    <body>
         <div class="navbar navbar-default navbar-fixed-top">
            <div class="navbar-header">
               <a href=""><i class="navbar-brand MediumIcon icon-home"> Merlion University Bidding Online System (BIOS)</i></a> 
            </div>
        </div> <br><br>
        
        <div style="margin-left: 40px;">
        <h3 class="fontCSS">Status: <%=status%></h3>

        <h3 class="fontCSS">Number of Records Loaded: <%=recordsList%></h3>
        <table class="fontCSS table-hover table-bordered" width="600px">


            <%

                out.println("<tr class='alert-info'>");
                out.println("<th>" + "File Name" + "</th>");
                out.println("<th>" + "Number of Entries" + "</th>");
                out.println("</tr>");
                for (int i = 0; i < recordsList; i++) {
                    Object record = num_records_loaded.get(i);

                    LinkedHashMap<String, Object> result = gson.fromJson(record.toString(), LinkedHashMap.class);
                    Iterator<String> key = result.keySet().iterator();

                    while (key.hasNext()) {
                        String keyName = key.next();
                        out.println("<tr>");
                        out.println("<td>" + keyName + "</td>");
                        out.println("<td>" + result.get(keyName).toString().replace(".0", "") + "</td>");
                        out.println("</tr>");

                    }
                }
            %>
        </table>
        </br>


        <table class="fontCSS table-hover table-bordered" width="600px">
            <%

                if (errors != null) {

                    out.println("<tr class='alert-danger'>");
                    out.println("<th>" + "File Name" + "</th>");
                    out.println("<th>" + "Line Number" + "</th>");
                    out.println("<th>" + "Error Details" + "</th>");
                    out.println("</tr>");
                    for (LinkedHashMap<String, Object> eachError : errors) { //ArrayList of errors gives a linkedhashmap

                        Iterator<String> errorKey = eachError.keySet().iterator(); //eachError is a linkedHashmap


                        out.println("<tr>");
                        while (errorKey.hasNext()) {
                            String errorKeyValue = errorKey.next();
                            out.println("<td>" + eachError.get(errorKeyValue).toString().replace("[", "").replace("]", "") + "</td>");
                        }
                        out.println("</tr>");
                    }

                }

          
       %>
        </table></br>
        <form action="../adminHome.jsp">
        <input type="submit" value="Back to admin home" class="btn btn-danger"/>
        </form>
    </div>

<% }//end of else %>
</body>
</html>
<%
    String checkStatus = (String)request.getSession().getAttribute("status");
    if(checkStatus==null){
        response.sendRedirect("index.jsp");
        return;
    }
%>
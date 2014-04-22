<%
    String checkAdmin = (String) request.getSession().getAttribute("admin");
    if(checkAdmin==null){
        response.sendRedirect("index.jsp");
        return;
    }
%>

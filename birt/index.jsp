<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@ page contentType="text/html; charset=utf-8" %>
<%@ page session="false" buffer="none" %>

<head>
<LINK REL="stylesheet" HREF="<%= request.getContextPath( ) + "/webcontent/birt/styles/style.css" %>" TYPE="text/css">
<LINK REL="stylesheet" HREF="<%= request.getContextPath( ) + "/webcontent/birt/styles/index.css" %>" TYPE="text/css">
</head>
<jsp>

<%      String username=request.getParameter("username");
        if ( username.equals("root")){ %>
                <br/>
                <br/>
                <br/>

                <a href=/birt/frameset?__showtitle=false&__report=reports/user_spends.rptdesign> User Spend </a>
                <hr/>
                <a href=/birt/frameset?__showtitle=false&__report=reports/top_spenders.rptdesign> Top Spenders </a>
                <hr/>

<% } else { %>
        <br/>
        <p> Reports for <%=  request.getParameter("username") %> </p>
        <br/>
        <br/>
        <br/>
        <a href=/birt/frameset?__showtitle=false&__report=reports/user_spends.rptdesign&username=<%=  request.getParameter("username") %>> User Spend </a>
        <hr/>
<% } %>

</jsp>

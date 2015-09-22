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

                <hr/>
                <a href=/birt/frameset?__showtitle=false&__report=reports/catalog_products.rptdesign> Catalog Listing</a>
                <hr/>
                <a href=/birt/frameset?__showtitle=false&__report=reports/customer_usage.rptdesign> Usage Summary</a>
                <hr/>
                <a href=/birt/frameset?__showtitle=false&__report=reports/vm_utilization.rptdesign> VM utilization</a>
                <hr/>
                <a href=/birt/frameset?__showtitle=false&__report=reports/storage_utilization.rptdesign> Storage utilization</a>
                <hr/>
                <a href=/birt/frameset?__showtitle=false&__report=reports/network_utilization.rptdesign> Network utilization</a>
                <hr/>
                <a href=/birt/frameset?__showtitle=false&__report=reports/custom_resource_report.rptdesign %> Custom Report </a> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <i> Create your own report </i>
                <hr/>

<% } else { %>
        <br/>
        <p> Reports for <%=  request.getParameter("username") %> </p>
        <br/>
        <br/>
        <br/>
        <a href=/birt/frameset?__showtitle=false&__report=reports/customer_usage.rptdesign&username=<%=  request.getParameter("username") %>> Usage Summary</a>
        <hr/>
        <a href=/birt/frameset?__showtitle=false&__report=reports/vm_utilization.rptdesign&username=<%=  request.getParameter("username") %>> VM utilization</a>
        <hr/>
        <a href=/birt/frameset?__showtitle=false&__report=reports/storage_utilization.rptdesign&username=<%=  request.getParameter("username") %>> Storage utilization</a>
        <hr/>
        <a href=/birt/frameset?__showtitle=false&__report=reports/network_utilization.rptdesign&username=<%=  request.getParameter("username") %>> Network utilization</a>
        <hr/>
        <a href=/birt/frameset?__showtitle=false&__report=reports/custom_resource_report.rptdesign&username=<%=  request.getParameter("username") %> Custom report </a> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <i> Create your own report </i>
        <hr/>
<% } %>

</jsp>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<jsp>

<%      String username=request.getParameter("username");
        if ( username.equals("root")){ %>

                <hr/>
                <a href=/birt/frameset?__report=reports/user_spends.rptdesign> User Spend </a>
                <hr/>
                <a href=/birt/frameset?__report=reports/top_spenders.rptdesign> Top Spenders </a>

<% } else { %>
	<p> Reports for <%=  request.getParameter("username") %> </p>
        <a href=/birt/frameset?__report=reports/user_spends.rptdesign&username=<%=  request.getParameter("username") %>> User Spend </a>
<% } %>

</jsp>

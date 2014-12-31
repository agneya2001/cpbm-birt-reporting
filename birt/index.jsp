<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<jsp>

<%      String username=request.getParameter("username");
        if ( username.equals("root")){ %>

                <hr/>
                <a href=/birt/frameset?__report=reports/user_spends.rptdesign&sample=<%=  request.getParameter("username") %>&__showtitle=false> User Spend </a>
                <hr/>
                <a href=/birt/frameset?__report=reports/top_spenders.rptdesign&sample=<%=  request.getParameter("username") %>&__showtitle=false> Top Spenders </a>

<% } else { %>
        <p> No reports for users yet </p>
        <a href=/birt/frameset?__report=test.rptdesign&sample=<%=  request.getParameter("username") %>&__showtitle=false  > Sample Report </a>
<% } %>

</jsp>

<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. --> 
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page import="com.vmops.web.forms.UserForm"  %>
<%@ page import="com.vmops.model.User" %>
<%@ page import="java.util.Map.Entry"  %>
<%@ page import="java.io.File"  %>
<%@ page import="java.net.URI"  %>
<%
UserForm form = (UserForm)(request.getAttribute("user")); 
for (Entry<String, String> cf: ((User) form.getUser().getObject()).getCustomFieldMap().entrySet()) {
  String filename = "../../../custom/jsp/tiles/users/myprofile.customfield." + cf.getKey() + ".jsp";
  %>
  <c:catch var="e">
    <jsp:include page="<%=filename%>"></jsp:include>  
  </c:catch>
  <%
}
%>

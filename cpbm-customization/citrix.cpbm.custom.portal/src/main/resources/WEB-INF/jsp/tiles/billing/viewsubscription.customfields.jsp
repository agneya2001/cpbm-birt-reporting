<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. --> 
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page import="com.vmops.model.Subscription"  %>
<%@ page import="java.util.Map.Entry"  %>

<%
Subscription subscriptionObj = (Subscription)(request.getAttribute("subscription"));
for (Entry<String, String> cf: subscriptionObj.getCustomFieldMap().entrySet()) {
  String filename = "../../../custom/jsp/tiles/billing/viewsubscription.customfield." + cf.getKey() + ".jsp";
  %>
  <c:catch var="e">
    <jsp:include page="<%=filename%>"></jsp:include>  
  </c:catch>
  <%
}
%>
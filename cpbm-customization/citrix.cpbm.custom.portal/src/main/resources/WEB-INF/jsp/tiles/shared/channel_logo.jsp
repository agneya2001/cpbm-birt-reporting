<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. --> 
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="loginlogo">
  <c:if test="${not empty globalConfiguration and not empty globalConfiguration.channelid}">
   <c:if test="${not empty globalConfiguration.channellogo}">
    <img src="<%=request.getContextPath()%>/portal/theme/<c:out value="${globalConfiguration.channelid}/${globalConfiguration.channellogo}"/>"/> 
   </c:if>
  </c:if>
  <c:if test="${empty globalConfiguration.channellogo}">
      <img src="/portal/portal/splogo"/>
  </c:if>
 </div>

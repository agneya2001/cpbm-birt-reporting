<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<script type="text/javascript" src="<%=request.getContextPath() %>/js/notifications.js"></script>
<jsp:include page="js_messages.jsp"></jsp:include>
  <!--  List of secondary verified emails here-->
<div>
      <!-- Start List of Notifications -->	
      <div class="clearboth"></div>
      <div style="margin: 20px 40px 30px;">
      	   <div class="main_addnew_formpanels" style="border: none">
              <div>
                <b><label for="additional_email"><spring:message code="label.myprofile.changePrimaryEmail"></spring:message></label></b>
                <select id="secondaryEmailsSelect">
                        <c:forEach items="${verifiedAlertsPrefs}" var="alertPref" varStatus="status">
                          <option value="<c:out value="${alertPref.id}"></c:out>"><c:out value="${alertPref.emailAddress}"></c:out></option> 
                        </c:forEach>
                 </select> 
                <input tabindex="100" id="changePrimaryEmailButton" class="" type="button" value="<spring:message code="label.myprofile.change"></spring:message>"/>
                
              </div>                      
            </div>
      </div>
      
</div>

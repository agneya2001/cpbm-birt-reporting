<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<script type="text/javascript" src="<%=request.getContextPath() %>/js/health.js"></script>
<jsp:include page="js_messages.jsp"></jsp:include>
<script type="text/javascript">
var ops = false;
<sec:authorize access="hasRole('ROLE_OPS_COMMUNICATION')">
ops = true;
</sec:authorize>

var healthUrl = "<%=request.getContextPath() %>/portal/health/";

</script>

<style>
.dialog_formcontent label.error {
	 padding-left: 0;
}
</style>

<div class="dialog_formcontent">
  
    <div class="details_lightboxformbox" style="border-bottom-style:none;padding-bottom:0px;width:auto">
    <spring:url value="/portal/health/add_status" var="add_new_status_path" htmlEscape="false" />
    <form:form commandName="serviceNotificationForm" id="healthStatusForm" cssClass="ajaxform"  action="${add_new_status_path}" onsubmit="addNewStatus(event,this)">
      <form:input type="hidden" id="dateFormat" path="dateFormat"></form:input>
      
        
        
          <ul>
            <c:choose>
              <c:when test="${empty selectedServiceInstanceUUID || selectedServiceInstanceUUID == null}">
                <li>
                  <form:label path="serviceInstanceUUID" ><spring:message code="ui.label.serviceinstance"/></form:label>
                  <div class="mandatory_wrapper">
                  <form:select cssClass="select" path="serviceInstanceUUID"  tabindex="1" style="margin:0px 0px 0px 10px;">
                    <form:option value=""><spring:message code="ui.option.service.health.choose"/></form:option>
                    <c:forEach items="${cloudTypeServiceInstances}" var="serviceInstance">
                      <form:option value="${serviceInstance.uuid}"> <c:out value="${serviceInstance.name}"/> </form:option>
                    </c:forEach>
                  </form:select>
                  </div>
                  <div class="main_addnew_formbox_errormsg" id="serviceInstanceUUIDError"></div>
                </li>
              </c:when>
              <c:otherwise>  
                <form:input type="hidden" path="serviceInstanceUUID" id="serviceInstanceUUID" value="${selectedServiceInstanceUUID}"></form:input>
              </c:otherwise> 
            </c:choose>
            <li>
              <form:label path="serviceNotification.notificationType"><spring:message code="ui.label.service.health.status.type"/></form:label>
              <div class="mandatory_wrapper">
              <form:select cssClass="select" path="serviceNotification.notificationType" tabindex="2" style="margin:0px 0px 0px 10px;">
                <option value=""><spring:message code="ui.option.service.health.choose"/></option>
                <option value="DISRUPTION"><spring:message code="ui.option.service.health.status.type.disruption"/></option>
                <option value="ISSUE"><spring:message code="ui.option.service.health.status.type.issue"/></option>
                <option value="RESOLUTION"><spring:message code="ui.option.service.health.status.type.resolution"/></option>
              </form:select>
              </div>
              <div class="main_addnew_formbox_errormsg" id="serviceNotification.notificationTypeError"></div>
            </li>
            <li>
              <form:label path="serviceNotification.subject" ><spring:message code="ui.label.service.health.subject"/></form:label>
              <div class="mandatory_wrapper">
              <form:input cssClass="text" path="serviceNotification.subject" tabindex="3" style="margin:0px 0px 0px 10px;"></form:input>
              </div>
              <div class="main_addnew_formbox_errormsg" id="serviceNotification.subjectError"></div>
            </li>
            <li>
              <form:label path="serviceNotification.description" ><spring:message code="ui.label.service.health.description"/></form:label>
              <div class="mandatory_wrapper">
              <form:textarea cssClass="longtextbox" rows="3" cols="20" path="serviceNotification.description" tabindex="6" style="margin:0px 0px 0px 10px;"></form:textarea>
              </div>
              <div class="main_addnew_formbox_errormsg" id="serviceNotification.descriptionError"></div>
            </li>
          </ul>
        
        
        <div class="main_addnew_submitbuttonpanel">
          <div class="main_addnew_submitbuttonbox">
            <a id="maintcancel" onclick="closehealthDialog(this);" style="cursor:pointer;"><spring:message code="ui.label.service.health.cancel"/></a>
            <input tabindex="100"  class="commonbutton submitmsg" rel="<spring:message code="ui.label.service.health.status.adding"/>" type="submit" value="<spring:message code="ui.label.service.health.status.add"/>"/>
          </div>
        </div>
      

      
    </form:form>
  </div>
</div>

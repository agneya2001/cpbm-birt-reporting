<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<script type="text/javascript" src="<%=request.getContextPath() %>/js/healthmaintainance.js"></script>
<jsp:include page="js_messages.jsp"></jsp:include>
<script type="text/javascript">
var ops = false;
<sec:authorize access="hasRole('ROLE_OPS_COMMUNICATION')">
ops = true;
</sec:authorize>

var healthUrl = "<%=request.getContextPath() %>/portal/health/";
var newSchedule = "<c:out value="${newSchedule}"/>";

</script>

<div class="dialog_formcontent">
  <div class="details_lightboxformbox">
    <c:if test="${newSchedule}">
      
      <spring:url value="/portal/health/save_maintenance_schedule" var="save_status_path" htmlEscape="false" />
    </c:if>
    <c:if test="${!newSchedule}">
      
      <spring:url value="/portal/health/update_maintenance_schedule" var="save_status_path" htmlEscape="false" />
    </c:if>
    <form:form commandName="serviceNotificationForm" id="healthStatusForm" cssClass="ajaxform"  action="${save_status_path}" onsubmit="saveSchedMaintenance(event,this, ${serviceNotificationForm.serviceNotification.id})">
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
              <spring:message code="maintenance.date.format" var="health_date_format"/>
              <form:hidden path="serviceNotification.notificationType"/>
            <li>
              <form:label path="serviceNotification.plannedStart" ><spring:message code="ui.label.serviceNotification.plannedStart"/></form:label>
              <div class="mandatory_wrapper">
              <form:hidden path="dateFormat" value="${date_format}"/>
              <form:hidden path="startDateString" id="startDateString"/>
              <form:hidden path="endDateString" id="endDateString"/>
              <input id="startDateField" class="text j_startDate" name="serviceNotification.plannedStart" readonly="readonly"
                value='<fmt:formatDate value="${serviceNotificationForm.serviceNotification.plannedStart}"
                 pattern="${health_date_format}" timeZone="${currentUser.timeZone}"/>' 
                 tabindex="3" style="margin:0px 0px 0px 10px;"
                 />
              <div class="datepicker_icon" id="startDate"></div>
              </div>
              <div class="main_addnew_formbox_errormsg" id="startDateFieldError"></div>
            </li>
            <li>
              <form:label path="serviceNotification.plannedEnd" ><spring:message code="ui.label.serviceNotification.plannedEnd"/></form:label>
              <div class="mandatory_wrapper">
              <input id="endDateField" class="text j_startDate" name="serviceNotification.plannedEnd" readonly="readonly"
                value='<fmt:formatDate value="${serviceNotificationForm.serviceNotification.plannedEnd}"
                 pattern="${health_date_format}" timeZone="${currentUser.timeZone}"/>' 
                 tabindex="4" style="margin:0px 0px 0px 10px;"
                 />
              <div class="datepicker_icon" id="endDate"></div>
              </div>
              <div class="main_addnew_formbox_errormsg" id="endDateFieldError"></div>
            </li>            
            <li>
              <form:label path="serviceNotification.subject" ><spring:message code="ui.label.service.health.subject"/></form:label>
              <div class="mandatory_wrapper">
              <form:input cssClass="text" path="serviceNotification.subject" tabindex="5" maxlength="200" style="margin:0px 0px 0px 10px;"></form:input>
              </div>
              <div class="main_addnew_formbox_errormsg" id="serviceNotification.subjectError"></div>
            </li>
            <li>
              <form:label path="serviceNotification.description" ><spring:message code="ui.label.service.health.description"/></form:label>
              <div class="mandatory_wrapper">
              <form:textarea cssClass="longtextbox" style="margin:0px 0px 0px 10px;width: 248px; height: 160px;" rows="3" cols="20" path="serviceNotification.description" tabindex="6" maxlength="1024"></form:textarea>
              </div>
              <div class="main_addnew_formbox_errormsg" id="serviceNotification.descriptionError"></div>
            </li>
          </ul>
        
        
        <form:hidden path="notificationId" ></form:hidden>

      <div class="main_addnew_submitbuttonpanel">
        <div class="main_addnew_submitbuttonbox">
         
          <a id="maintcancel" onclick="closemaintDialog(this);" style="cursor:pointer;"><spring:message code="ui.label.service.health.cancel"/></a>
          <c:if test="${newSchedule}">
            <input tabindex="210"  class="commonbutton submitmsg" rel="<spring:message code="ui.label.serviceNotification.scheduling"/>" type="submit" value="<spring:message code="ui.label.serviceNotification.schedule"/>"/>
          </c:if>
          <c:if test="${!newSchedule}">
            <input tabindex="210"  class="commonbutton submitmsg" rel="<spring:message code="ui.label.serviceNotification.scheduling"/>" type="submit" value="<spring:message code="ui.label.serviceNotification.update"/>"/>
          </c:if>
          
        </div>
      </div>
      
      
    </form:form>
  </div>
</div>

<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="js_messages.jsp"></jsp:include>
<style>
.datepicker_stlying{
  z-index: 9999
}
</style>

<input type="hidden" id="isTodayAllowed"  value="<c:out value="${isTodayAllowed}"/>"/>
<spring:message code="dateonly.filter.format" var="dateonly_format"/>
<input type="hidden" id="date_today" value="<fmt:formatDate value="${date_today}" pattern="${dateonly_format}" />"/>

<div class="dialog_formcontent wizard">
  <c:choose>
    <c:when test="${isPlanDateThere}">
      <span class="helptext"> <spring:message code="ui.text.products.planned.edit.date.text"/></span>
    </c:when>
    <c:otherwise>
      <span class="helptext"> <spring:message code="ui.text.products.planned.set.date.text"/></span>
    </c:otherwise>
  </c:choose>
  <spring:url value="/portal/products/setplandate" var="plan_charges_date_path" htmlEscape="false" /> 
  <form:form commandName="planDateForm" id="planDateForm" cssClass="formPanel ajaxForm"  action="${plan_charges_date_path}">
      <ol>
          <li style="margin:10px 0 0 10px;">
                <label for="startDate" style="color:#111;font-weight: bold; width:80px; margin:4px 0 0 8px;" ><spring:message code="label.start.date"/></label>
                <div class="mandatory_wrapper" style="margin:0 0 0 20px;width:300px;">
                   <input type="text" id="planstartDate" name="startDate" class="text j_startDate" readonly="readonly" tabindex="1" value="<fmt:formatDate  value="${planDateForm.startDate}" pattern="${dateonly_format}" />" />
                   <input type="hidden" id="planstartDateString" name="startDateString"/>
                 </div>
                   <div class="main_addnew_formbox_errormsg" style="margin:5px 0 0 100px" id="planstartDateError" ></div>
          </li>
       </ol>
  </form:form> 

</div>

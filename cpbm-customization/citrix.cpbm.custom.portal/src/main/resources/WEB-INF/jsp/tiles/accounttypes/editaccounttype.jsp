<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<jsp:include page="/WEB-INF/jsp/tiles/accounttypes/js_messages.jsp"></jsp:include>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/accounttype.js"></script>
<!--  Edit Account Type starts here-->
<div class="dialog_formcontent" >
  <div class="details_lightboxtitlebox">
  </div>
  <div class="details_lightboxformbox">
    <spring:url value="/portal/admin/edit_account_type" var="edit_account_type_path" htmlEscape="false" /> 
    <form:form commandName="accountTypeForm" cssClass="ajaxform" id="accountTypeEditForm" action="${edit_account_type_path}">
    <ul>
      <c:set var="detailStyle" value="display:none;" />
      <c:set var="onBoardStyle" value="display:none;" />
      <c:set var="iaasStyle" value="display:none;" />
      <c:set var="billingStyle" value="display:none;" />
      <c:if test='${mode == "1"}'>
        <c:set var="detailStyle" value="" />
      </c:if>
      <c:if test='${mode == "2"}'>
        <c:set var="onBoardStyle" value="" />
      </c:if>
      <c:if test='${mode == "3"}'>
        <c:set var="iaasStyle" value="" />
      </c:if>
      <c:if test='${mode == "4"}'>
        <c:set var="billingStyle" value="" />
      </c:if>
      
      <!-- Details -->
      <li style='<c:out value="${detailStyle}" />'>
        <form:label path="accountType.maxUsers"><spring:message code="ui.accounttypes.list.page.mu"/></form:label>
        <div class="mandatory_wrapper">
           <form:input path="accountType.maxUsers" cssClass="text"/>
        </div>
        <div class="main_addnew_formbox_errormsg edit_error_msg" id="accountType.maxUsersError"></div>
      </li>
      <!-- /Details -->
      <!-- On Boarding Controls --> 
      <li style='<c:out value="${onBoardStyle}" />'>
        <form:label path="accountType.selfRegistrationAllowed"><spring:message code="ui.accounttypes.list.page.sra"/></form:label>
        <div class="nonmandatory_wrapper">
          <form:checkbox path="accountType.selfRegistrationAllowed" cssClass="checkbox"/>
        </div>
      </li>
      
      <c:if test="${!(accountTypeForm.accountType.trial)}">
      <li style='<c:out value="${onBoardStyle}" />'>
        <form:label path="accountType.manualRegistrationAllowed"><spring:message code="ui.accounttypes.list.page.mra"/></form:label>
        <div class="nonmandatory_wrapper">
           <form:checkbox path="accountType.manualRegistrationAllowed" cssClass="checkbox"/>
        </div>
      </li>
      </c:if>
      <li style='<c:out value="${onBoardStyle}" />'>
        <form:label path="accountType.defaultSelfRegistered"><spring:message code="ui.accounttypes.list.page.dsr"/></form:label>
        <div class="nonmandatory_wrapper">
           <form:checkbox path="accountType.defaultSelfRegistered" cssClass="checkbox"/>
        </div>
      </li>
      <li style='<c:out value="${onBoardStyle}" />'>
        <form:label path="accountType.defaultRegistered"><spring:message code="ui.accounttypes.list.page.dr"/></form:label>
        <div class="nonmandatory_wrapper">
           <form:checkbox path="accountType.defaultRegistered" cssClass="checkbox"/>
        </div>
      </li>
      <li style='<c:out value="${onBoardStyle}" />'>
        <form:label path="accountType.accountRestrictionGracePeriod"><spring:message code="ui.accounttypes.list.page.accountRestrictionGracePeriod"/></form:label>
        <div class="mandatory_wrapper">
            <form:input path="accountType.accountRestrictionGracePeriod" cssClass="text"/>
        </div>
        <div class="main_addnew_formbox_errormsg edit_error_msg" id="accountType.accountRestrictionGracePeriodError" ></div>
      </li>
      <c:if test="${!(accountTypeForm.accountType.trial)}">
      <li style='<c:out value="${onBoardStyle}" />'>
        <form:label path="accountType.depositRequired"><spring:message code="ui.accounttypes.list.page.initialDepositRequired"/></form:label>
        <div class="nonmandatory_wrapper">
            <form:checkbox path="accountType.depositRequired" cssClass="checkbox"/>
        </div>
      </li>
      </c:if>
      <!-- /On Boarding Controls -->
      
      

      
      <!-- Billing Controls -->
      <li style='<c:out value="${billingStyle}"/>'>
        <form:label path="accountType.creditExposureBreach"><spring:message code="ui.accounttypes.list.page.ceb"/></form:label>
        <div class="nonmandatory_wrapper">
           <form:select cssClass="select" path="accountType.creditExposureBreach">
              <c:forEach items="${creditBreachActions}" var="choice">
                <form:option value="${choice}">
                  <spring:message code='ui.label.creditExposureBreach.${choice}'/>
                </form:option>
              </c:forEach>
           </form:select>
        </div>
      </li>
      <c:if test="${(!accountTypeForm.accountType.trial && accountTypeForm.accountType.name != 'SYSTEM' && accountTypeForm.accountType.paymentModes != 8)}">
      <li style='<c:out value="${billingStyle}"/>'>
        <form:label path="accountType.preAuthRequired"><spring:message code="ui.accounttypes.list.page.preAuthRequired"/></form:label>
        <div class="nonmandatory_wrapper">
            <form:checkbox path="accountType.preAuthRequired" cssClass="checkbox"/>
        </div>
      </li> 
      </c:if>
      <!-- /Billing Controls -->
     </ul>
    </form:form>
    <div class="main_addnew_formbox_errormsg" id="miscFormErrors" style="margin:10px 0 0 5px; width:100%;"></div>
  </div>
</div>
<!--  Edit Account Type ends here-->

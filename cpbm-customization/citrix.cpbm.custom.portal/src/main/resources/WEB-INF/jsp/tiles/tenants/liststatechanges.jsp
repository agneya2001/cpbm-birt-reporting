<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %> 
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<script type="text/javascript">
<!--
$(document).ready(function(){
  $(".transaction_row").click(function(){
    var transaction_id = $(this).attr('id').replace("transaction_row","");
    $("#transaction_details"+transaction_id).toggle();
  });
});
//-->
</script>
 <div class="widget_browsergrid_wrapper fixed">
         
      <div class="widget_grid details header">
          <div class="widget_grid_cell" style="padding:1px;width:33%;">
           <span class="header statechangecustom"><spring:message code="ui.task.transaction"/></span>
        </div> 
        <div class="widget_grid_cell" style="padding:1px;width:12%;">
           <span class="header statechangecustom"><spring:message code="ui.label.tenant.view.transactionHistory.state"/></span>
        </div> 
        <div class="widget_grid_cell" style="padding:1px;width:20%;">
           <span class="header statechangecustom"><spring:message code="ui.label.tenant.view.transactionHistory.start.date"/></span>
        </div> 
        <div class="widget_grid_cell" style="padding:1px;width:20%;">
           <span class="header statechangecustom"><spring:message code="ui.label.tenant.view.transactionHistory.end.date"/></span>
        </div>
        <div class="widget_grid_cell" style="padding:1px;width:10%;">
           <span class="header statechangecustom"><spring:message code="ui.label.tenant.view.transactionHistory.workflow"/></span>
        </div>
      </div>
	<spring:message code="date.format" var="date_format"/>
      <c:forEach var="transaction" items="${transactionHistory}" varStatus="status">
        <c:choose>
          <c:when test="${status.index % 2 == 0}">
            <c:set var="rowClass" value="odd" />
          </c:when>
          <c:otherwise>
            <c:set var="rowClass" value="even" />
          </c:otherwise>
        </c:choose>
      
        <div class="<c:out value="grid_rows ${rowClass}"/>" >
        <spring:message code="dateonly.format" var="dateonly_format"/>  
          <div class="grid_row_cell transaction_row" style="padding:1px;width: 33%;" id="transaction_row${transaction.id}">
            <div class="row_celltitles expand_link"><spring:message code="ui.task.transaction.type.${transaction.type}"/></div>
          </div>
          <div class="grid_row_cell" style="padding:1px;width: 12%;">
            <div class="row_celltitles statechangecustom"><spring:message code="businessTransaction.state.${transaction.state}"/></div>
          </div>
          <div class="grid_row_cell" style="padding:1px;width: 20%;">
            <div class="row_celltitles statechangecustom">
            	<fmt:formatDate value="${transaction.startDate}" pattern="${date_format}" timeZone="${currentUser.timeZone}"/>
            </div>
          </div>
          <div class="grid_row_cell" style="padding:1px;width: 20%;">
            <div class="row_celltitles statechangecustom"><fmt:formatDate value="${transaction.endDate}" pattern="${date_format}" timeZone="${currentUser.timeZone}"/></div>
          </div>
          <c:if test="${transaction.workflowId != null}">
          	<div class="grid_row_cell" style="padding:1px;width: 10%;">
            	<div class="row_celltitles statechangecustom">                      
            		<a href="javascript:void(0);" id="workflowdetails${transaction.workflowId}" class="workflowDetailsPopup"><spring:message code="ui.label.tenant.view.transactionHistory.workflow.view" /></a>
            	</div>      
          	</div>
          </c:if>
          <div class="transaction_details" id="transaction_details${transaction.id}">
            <c:choose>
            <c:when test="${transaction.type == 'tenantStateChange' }">
              <ul>
                <li class="subselection">
                  <span class="title"><spring:message code="ui.task.tenant.intialState" />:&nbsp;</span> 
                  <span class="description subdescription ellipsis" >
                    <spring:message code="tenant.state.${fn:toLowerCase(transaction.tenantInitialState)}" />
                  </span>
                </li>
                <li class="subselection">
                  <span class="title"><spring:message code="ui.task.tenant.targetState" />:&nbsp;</span> 
                  <span class="description subdescription ellipsis" >
                    <spring:message code="tenant.state.${fn:toLowerCase(transaction.tenantTargetState)}" />
                  </span>
                </li>
              </ul>
            </c:when>
            <c:when test="${transaction.type == 'tenantAccountTypeConversion' }">
              <ul>
                <li class="subselection">
                  <span class="title"><spring:message code="ui.task.transaction.type.tenantAccountTypeConversion.initial.accounttype" />:&nbsp;</span> 
                  <span class="description subdescription ellipsis" >
                    <spring:message code="registration.accounttype.${transaction.accountTypeInitial.nameLower}"/>
                  </span>
                </li>
                <li class="subselection">
                  <span class="title"><spring:message code="ui.task.transaction.type.tenantAccountTypeConversion.target.accounttype" />:&nbsp;</span> 
                  <span class="description subdescription ellipsis" >
                    <spring:message code="registration.accounttype.${transaction.accountType.nameLower}"/>
                  </span>
                </li>
                <c:if test="${not empty transaction.paymentInfo}" >
                  <li class="subselection">
                    <span class="title"><spring:message code="ui.task.transaction.type.paymentInfoChange.paymentInfo.label" />:&nbsp;</span> 
                    <span class="description subdescription ellipsis" >
                      <c:out value="${transaction.paymentInfo}"/>
                    </span>
                  </li>
                </c:if>
              </ul>
            </c:when>
            <c:when test="${transaction.type == 'paymentInfoChange' }">
              <ul>
                <li class="subselection">
                  <span class="title"><spring:message code="ui.task.transaction.type.paymentInfoChange.paymentInfo.label" />:&nbsp;</span> 
                  <span class="description subdescription ellipsis" >
                    <c:out value="${transaction.paymentInfo}"/>
                  </span>
                </li>
              </ul>
            </c:when>
            <c:when test="${transaction.type == 'cloudServiceActivation' }">
              <ul>
                <li class="subselection">
                  <span class="title"><spring:message code="label.service.activation.initial.state" />:&nbsp;</span> 
                  <span class="description subdescription ellipsis" >
                    <spring:message code="tenantHandle.state.${fn:toLowerCase(transaction.initialState)}" />
                  </span>
                </li>
                <li class="subselection">
                  <span class="title"><spring:message code="label.service.activation.target.state" />:&nbsp;</span> 
                  <span class="description subdescription ellipsis" >
                    <spring:message code="tenantHandle.state.${fn:toLowerCase(transaction.targetState)}" />
                  </span>
                </li>
              </ul>
            </c:when>
            </c:choose> 
          </div>
        </div>
      </c:forEach>
  
</div>
<div class="workflow_details_popup" title="<spring:message code="dialogue.title.workflow" />" style="display:none"></div>


<!--  View state changes ends here-->
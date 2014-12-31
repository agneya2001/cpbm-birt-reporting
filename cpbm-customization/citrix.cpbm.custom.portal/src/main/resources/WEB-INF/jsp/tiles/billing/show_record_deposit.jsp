<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<script type="text/javascript" src="<%=request.getContextPath() %>/js/initial_record_deposit.js"></script>

<div class="maintitlebox">
    <h1><spring:message code="ui.deposit.record.title"/></h1>
  </div>
<div class="maincontent_bigverticalpanel" style="width:100%;">
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

  
 <div id="changeAccountTypeDiv">
  <div class="commonboxes_container">
    <div class="commonboxes_titlebox">
        <h2></h2>
      </div>
    <div class="commonboxes">
    <div class="commonboxes_contentcontainer bigbox" style="width:100%;">
      <c:choose>  
        <c:when test ="${show_deposit_record}">
          <div class="commonboxes_formbox bigformbox">
            <div class="commonboxes_formbox_panels" style="border:none;">
              <ul>
                <li style="border:none;">
                  <label for=""><spring:message code="label.initial.deposit.record.received.on"/></label>
                  <div class="commonboxes_formbox_withouttextbox"><c:out value="${depositRecord.receivedOn}"/></div>
                </li>                                                                                                     
                <li style="border:none;">
                  <label for=""><spring:message code="label.initial.deposit.record.amount"/></label>
                  <div class="commonboxes_formbox_withouttextbox"><fmt:formatNumber pattern="${currencyFormat}" minFractionDigits="${minFractionDigits}" value="${depositRecord.amount}"/></div>
                </li>            
                <li style="border:none;">
                  <label for=""><spring:message code="label.initial.deposit.record.recorded.by"/></label>
                  <div class="commonboxes_formbox_withouttextbox"><c:out value="${depositRecord.recordedBy.username}"/></div>
                </li>
              </ul>
            </div>
          </div>
        </c:when>
        <c:otherwise>
          <p style="margin:7px 0 5px 2px;"><spring:message code="ui.no.initial.deposit.recorded"/></p>
          <div class="commonbox_submitbuttonpanel">
            <div class="commonbox_submitbuttonbox">
              <spring:url value="/portal/billing/record_deposit" var="record_deposit_path" htmlEscape="false">
                <spring:param name="tenant"><c:out value="${tenant.param}"/></spring:param>
              </spring:url>
              <p><a href="javascript:void(0);" class="selection_commonbutton" id="record_initial_deposit_button"><spring:message code="record.initial.deposit.action"/></a></p>
            </div>
          </div>
        </c:otherwise>
      </c:choose> 
    </div>
    </div>
  </div>
</div>
</div>
<div id="recordDepositDiv" title='<spring:message code="ui.deposit.record.title"/>' style="display:none"> 
</div>

<input id="tenantId" type="hidden" name="tenant" value="<c:out value="${tenant.param}"/>"/>

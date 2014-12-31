<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<div class="thirdlevel_subsubmenu">
  <div class="thirdlevel_subsubmenu left"></div>
    <div class="thirdlevel_subsubmenu mid">
          <div class="thirdlevel_subtab off" id="l3_usage_details_tab" >
            <div class="thirdlevel_menuicons activity"></div>
            <p style="max-width:170px;"><spring:message code="label.billing.leftnav.usage.activity"/>
            </p>
          </div>
          <c:if test="${userHasCloudServiceAccount}">
          <div class="thirdlevel_subtab off" id="l3_subscriptions_tab" >
            <div class="thirdlevel_menuicons subscription"></div>
            <p style="max-width:170px;"><spring:message code="label.billing.leftnav.subscriptions"/>
            </p>
          </div>
          </c:if>
         <div class="thirdlevel_subtab off" id="l3_billing_invoices_tab" >
            <div class="thirdlevel_menuicons invoices"></div>
            <p style="max-width:170px;"><spring:message code="label.billing.thirdlevelmenu.invoices"/>
            </p>
          </div>
          <sec:authorize access="hasAnyRole('ROLE_ACCOUNT_ADMIN','ROLE_ACCOUNT_BILLING_ADMIN','ROLE_FINANCE_CRUD')">
	          <div class="thirdlevel_subtab off" id="l3_billing_payments_tab" >
	            <div class="thirdlevel_menuicons payments"></div>
	            <p style="max-width:170px;"><spring:message code="label.billing.thirdlevelmenu.payments"/>
	            </p>
	          </div>
          </sec:authorize>
          <sec:authorize access="hasRole('ROLE_FINANCE_CRUD')">
            <c:if test="${tenant.accountType.depositRequired}">
              <div class="thirdlevel_subtab off" id="l3_billing_record_deposit_tab" >
                <div class="thirdlevel_menuicons payments"></div>
                <p style="max-width:170px;"><spring:message code="label.billing.leftnav.record.deposit"/>
                </p>
              </div>
          </c:if>
        </sec:authorize>
        </div> 
        <div class="thirdlevel_subsubmenu right"></div>
</div>
  <spring:url value="/portal/billing/show_record_deposit" var="show_record_deposit_path" htmlEscape="false">
    <spring:param name="tenant"><c:out value="${tenant.param}"/></spring:param>
  </spring:url> 
  <sec:authorize access="!hasAnyRole('ROLE_ACCOUNT_ADMIN','ROLE_ACCOUNT_BILLING_ADMIN','ROLE_FINANCE_CRUD')">
 
    <input type="hidden" id="usage_billing_my_usage" value='true'/>

  </sec:authorize> 
    <c:choose>
  		<c:when test="${perPage != null && perPage > 0}">
      <input type="hidden" id="usage_billing_page_pagination" value='true'/>
      <input type="hidden" id="usage_billing_page" value='1'/>
      <input type="hidden" id="usage_billing_perPage" value='100'/>
  		</c:when>
  	</c:choose>
  
    


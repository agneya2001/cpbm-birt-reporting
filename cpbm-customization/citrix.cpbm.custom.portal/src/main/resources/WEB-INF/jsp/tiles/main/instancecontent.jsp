<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<input type="hidden" id="selectedCategory" value="<c:out value="${selectedCategory}"/>" />

<div class="sliding_statswrapper">
    <jsp:include page="/WEB-INF/jsp/tiles/main/serviceCategoryList.jsp"/>        
</div>

<script type="text/javascript">
  var isOwner = false;
</script>
<c:if test='${isOwner eq true}' >
<script type="text/javascript">
  isOwner = true;
</script>
</c:if>

<input type="hidden" id="tenantParam" name="tenantParam" value="<c:out value="${tenant.param}"/>"/>
<input type="hidden" id="tenantCurrency" name="tenantCurrency" value="<c:out value="${tenant.currency.sign}" />" />
<input type="hidden" id="minFractionDigit" name="tenantCurrencyPrecision" value="<c:out value="${minFractionDigits}"/>"/>
<input type="hidden" id="tnc" name="tnc" value="<c:out value="${tnc}"/>"/>
<input type="hidden" id="currentUserTimezoneCode" name="currentUserTimezoneCode" value="<c:out value="${currentUserTimezoneCode}"/>"/>
<input type="hidden" id="isTrialUser" name="isTrialUser" value="<c:out value="${isTrialUser}"/>"/>
<div id="serviceInstanceViewTabs"></div>


<iframe src="" id="serviceInstanceResourceView" style="border-style:none;height:823px;width:960px;"></iframe>  
<script type="text/javascript" src="<%=request.getContextPath()%>/js/instanceheader.js"></script>

<sec:authorize access="!hasAnyRole('ROLE_ACCOUNT_ADMIN','ROLE_ACCOUNT_BILLING_ADMIN','ROLE_FINANCE_CRUD')">
    <input type="hidden" id="usage_billing_my_usage" value='true'/>
</sec:authorize> 


  <script type="text/javascript">
    var isDelinquent = false;
    var redirectToBilling = false;
    var redirectToDashBoard = false;
    var showMakePaymentMessage = "";
  </script>
  
  <c:if test="${showDelinquent}">
  
    <sec:authorize access="hasAnyRole('ROLE_ACCOUNT_BILLING_ADMIN', 'ROLE_FINANCE_CRUD')">
      <script type="text/javascript">
        var isDelinquent = true;
        var redirectToBilling = true;
      </script>
    </sec:authorize>
    
    <sec:authorize access="!hasAnyRole('ROLE_ACCOUNT_BILLING_ADMIN', 'ROLE_FINANCE_CRUD')">
      <script type="text/javascript">
          var isDelinquent = true;
          var redirectToDashBoard = true;
      </script>
      
      <c:if test="${stateChangeCause == 'PAYMENT_FAILURE'}">
        <script type="text/javascript">
        var showMakePaymentMessage = "<spring:message javaScriptEscape="true" code="message.tenant.delinquent.make.payment"/>";
        </script>
      </c:if>
      
      <c:if test="${stateChangeCause != 'PAYMENT_FAILURE'}">
        <script type="text/javascript">
        var showMakePaymentMessage = "<spring:message javaScriptEscape="true" code="message.tenant.delinquent.admin.contact.support"/>";
        </script>
      </c:if>
    </sec:authorize>
</c:if>
<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<div class="secondlevel_withoutsubmenu">
  <div class="secondlevel_breadcrumb_panel">
    <div class="secondlevel_breadcrumbbox_tenant">
      <p title="${tenant.name}">
        <c:out value="${tenant.name}" />
      </p>
    </div>
    <div class="doc_help_link"><i class="fa fa-question-circle"></i></div>
  </div>
</div>
<tiles:insertDefinition name="warnings"></tiles:insertDefinition>

<script type="text/javascript">
    var isDelinquent = false;
  </script>
<c:if test="${showDelinquent}">
  <sec:authorize access="hasAnyRole('ROLE_ACCOUNT_BILLING_ADMIN', 'ROLE_FINANCE_CRUD')">
    <script type="text/javascript">
        var isDelinquent = true;
        var showMakePaymentMessage = "<spring:message javaScriptEscape="true" code="message.tenant.delinquent.make.payment"/>";
      </script>
  </sec:authorize>
  <sec:authorize access="!hasAnyRole('ROLE_ACCOUNT_BILLING_ADMIN', 'ROLE_FINANCE_CRUD')">
    <script type="text/javascript">
        var isDelinquent = true;
      </script>
  </sec:authorize>
</c:if>

<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/WEB-INF/jsp/tiles/accounttypes/js_messages.jsp"></jsp:include>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/accounttype.js"></script>
<script type="text/javascript">
	var currPrecision="<c:out value="${currencyFractionalDigitsLimit}"/>";
</script>
<style>
.dialog_formcontent label.error {
   margin-left: -10px;
}
</style>
<!--  Edit Initial Deposit starts here-->
<div class="dialog_formcontent" >
  <div class="details_lightboxtitlebox">
  </div>
  <div class="details_lightboxformbox">
    <spring:url value="/portal/admin/edit_initial_deposit" var="edit_credt_exposure_path" htmlEscape="false" /> 
    <form:form commandName="accountTypeForm" cssClass="ajaxform" id="accountTypeInitialDepositForm" action="${edit_credt_exposure_path}" onsubmit="editinitialdepositPost(event,this)">
    <ul>
      <c:forEach items="${accountTypeForm.accountType.accountTypeCreditExposureList}" varStatus="cestatus"  var="accountTypeCreditExposure">
          <li> 
            <label><c:out value="${accountTypeCreditExposure.currencyValue.currencyName}"></c:out>(<c:out value="${accountTypeCreditExposure.currencyValue.currencyCode}"></c:out>)</label>
            <div class="mandatory_wrapper">
              <input id="accountType.accountTypeCreditExposureList<c:out value='${cestatus.index}' />.initialDeposit" value='<fmt:formatNumber groupingUsed="false" maxFractionDigits="${currencyFractionalDigitsLimit}" value="${accountTypeCreditExposure.initialDeposit}"/>' name="accountType.accountTypeCreditExposureList[<c:out value='${cestatus.index}' />].initialDeposit" class="text initialdepositclass"/>
            </div>                   
            <div class="main_addnew_formbox_errormsg edit_error_msg" id="accountType.accountTypeCreditExposureList<c:out value='${cestatus.index}' />.initialDepositError" ></div>
       </c:forEach>         
    </ul>
    </form:form>
  </div>
</div>
<!--  Edit Initial Deposit ends here-->


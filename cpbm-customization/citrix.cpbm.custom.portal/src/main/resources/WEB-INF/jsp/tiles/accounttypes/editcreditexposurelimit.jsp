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
<div class="dialog_formcontent" >
  <div class="details_lightboxtitlebox">
  </div>
  <div class="details_lightboxformbox">
    <spring:url value="/portal/admin/edit_credit_exposure" var="edit_credt_exposure_path" htmlEscape="false" /> 
    <form:form commandName="accountTypeForm" cssClass="ajaxform" id="accountTypeCreditExposureEditForm" action="${edit_credt_exposure_path}">
    <ul>
      <c:forEach items="${accountTypeForm.accountType.accountTypeCreditExposureList}" varStatus="cestatus"  var="accountTypeCreditExposure">
         <li> 
            <label><spring:message code="currency.longname.${accountTypeCreditExposure.currencyValue.currencyCode}"></spring:message></label>
            <div class="mandatory_wrapper">
              <c:if test="${accountTypeCreditExposure.creditExposureLimit != null }">
              	<input id="accountType.accountTypeCreditExposureList<c:out value='${cestatus.index}' />.creditExposureLimit" value='<fmt:formatNumber groupingUsed="false" maxFractionDigits="${currencyFractionalDigitsLimit}" value="${accountTypeCreditExposure.creditExposureLimit}"/>' name="accountType.accountTypeCreditExposureList[<c:out value='${cestatus.index}' />].creditExposureLimit" class="text creditexposureclass required"/>
              </c:if> 
              <c:if test="${accountTypeCreditExposure.creditExposureLimit == null }">
              	<input id="accountType.accountTypeCreditExposureList<c:out value='${cestatus.index}' />.creditExposureLimit" value='<c:out value="0.0"/>' name="accountType.accountTypeCreditExposureList[<c:out value='${cestatus.index}' />].creditExposureLimit" class="text creditexposureclass required"/>
              </c:if>                  
            </div>
             <div class="main_addnew_formbox_errormsg edit_error_msg" id="accountType.accountTypeCreditExposureList<c:out value='${cestatus.index}' />.creditExposureLimitError" ></div>
         </li>
       </c:forEach>         
    </ul>
    </form:form>
  </div>
</div>
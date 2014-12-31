<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script language="javascript">
if( typeof i18n === 'undefined' ) {
  var i18n = {};
}
if( typeof i18n.errors === 'undefined' ) {
  i18n.errors = {};
}

i18n.errors.trialtoken = {
	compaignCodeReq : '<spring:message javaScriptEscape="true" code="js.errors.trialtoken.compaign.code.required"/>',
	nameReq : '<spring:message javaScriptEscape="true" code="js.errors.trialtoken.name.required"/>',
	companyNameReq : '<spring:message javaScriptEscape="true" code="js.errors.trialtoken.company.name.required" />',
	emailAddressNeed : '<spring:message javaScriptEscape="true" code="js.errors.trialtoken.email.address.need"/>',
	emailAddressInvalid : '<spring:message javaScriptEscape="true" code="js.errors.trialtoken.email.address.invalid"/>',
	phoneNumReq : '<spring:message javaScriptEscape="true" code="js.errors.trialtoken.phone.number"/>',
	currencySel : '<spring:message javaScriptEscape="true" code="js.errors.trialtoken.currency.select"/>'
	
	
};

i18n.errors.trialregister = {
		TRVATIdMatch : '<spring:message javaScriptEscape="true" code="js.errors.trialregister.vat.id.match"/>',
		TRPostalZipValid : '<spring:message javaScriptEscape="true" code="js.errors.trialregister.postal.zip.valid"/>',
		TRStateSelect : '<spring:message javaScriptEscape="true" code="js.errors.trialregister.state.select"/>',
		TRFirstName : '<spring:message javaScriptEscape="true" code="js.errors.trialregister.first.name"/>',
		TRLastName : '<spring:message javaScriptEscape="true" code="js.errors.trialregister.last.name"/>',
		TRTitle : '<spring:message javaScriptEscape="true" code="js.errors.trialregister.title"/>',
		TREmailAddressNeed : '<spring:message javaScriptEscape="true" code="js.errors.trialregister.email.address.need"/>',
		TREmailAddressInvalid : '<spring:message javaScriptEscape="true" code="js.errors.trialregister.email.address.invalid"/>',

		TREmailConfirm : '<spring:message javaScriptEscape="true" code="js.errors.trialregister.email.confirm"/>',
		TREmailMatch : '<spring:message javaScriptEscape="true" code="js.errors.trialregister.email.match"/>',
		TRUsernameRequired : '<spring:message javaScriptEscape="true" code="js.errors.trialregister.username.required"/>',
		TRUsernameExists : '<spring:message javaScriptEscape="true" code="js.errors.trialregister.username.exists"/>',
		TRPassword : '<spring:message javaScriptEscape="true" code="js.errors.trialregister.password"/>',
		TRPhoneNumReq : '<spring:message javaScriptEscape="true" code="js.errors.trialregister.phone.number.required"/>',
		TRPasswordConfim : '<spring:message javaScriptEscape="true" code="js.errors.trialregister.password.confim"/>',
		TRPasswordMatch : '<spring:message javaScriptEscape="true" code="js.errors.trialregister.password.match"/>',
		TRPasswordequsername : '<spring:message javaScriptEscape="true" code="js.errors.trialregister.password.equsername"/>',

		TRCompanyNameRequired : '<spring:message javaScriptEscape="true" code="js.errors.trialregister.company.name.required"/>',
		TRStreetAddressRequired : '<spring:message javaScriptEscape="true" code="js.errors.trialregister.street.address.required"/>',
		TRPostalCodeRequired : '<spring:message javaScriptEscape="true" code="js.errors.trialregister.postal.code.required"/>',
		TRCountrySelect : '<spring:message javaScriptEscape="true" code="js.errors.trialregister.country.select"/>',
		TRBillingAddressRequired : '<spring:message javaScriptEscape="true" code="js.errors.trialregister.billing.address.required"/>',
		TRBillingPostalCodeRequired : '<spring:message javaScriptEscape="true" code="js.errors.trialregister.billing.postal.code.required"/>',
		TRBillingCountrySelect : '<spring:message javaScriptEscape="true" code="js.errors.trialregister.country.select"/>',
		TRTermsServiceAccept : '<spring:message javaScriptEscape="true" code="js.errors.trialregister.terms.service.accept"/>'
		
		
	};
</script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/jquery.selectboxes.min.js"></script>
<c:set var="countries" scope="page" value="${registration.countryList}"/>
<jsp:include page="/WEB-INF/jsp/tiles/shared/country_states.jsp"></jsp:include>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/jquery.stateselect.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/trialRegister.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/trialtoken.js"></script>

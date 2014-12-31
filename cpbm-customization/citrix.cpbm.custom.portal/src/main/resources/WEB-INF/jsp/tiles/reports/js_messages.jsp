<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<script language="javascript">
var i18n = {
	    errors: {
	        validationMonth  		: '<spring:message javaScriptEscape="true" code="js.report.errors.validationMonth"/>', 
	        validationYear  		: '<spring:message javaScriptEscape="true" code="js.report.errors.validationYear"/>', 
	        validationStartDate  	: '<spring:message javaScriptEscape="true" code="js.report.errors.validationStartDate"/>',
	        validationEndDate  		: '<spring:message javaScriptEscape="true" code="js.report.errors.validationEndDate"/>',
	        validationDate  		: '<spring:message javaScriptEscape="true" code="js.report.errors.validationDate"/>',
	        validationCustomReport	: '<spring:message javaScriptEscape="true" code="js.report.errors.validationCustomReport"/>',
	        generateFailure			: '<spring:message javaScriptEscape="true" code="js.report.errors.generateFailure"/>',
	        sendemailFailed			: '<spring:message javaScriptEscape="true" code="js.report.errors.sendemailFailed"/>',
	        emptyEmailField			: '<spring:message javaScriptEscape="true" code="js.report.errors.emptyEmailField"/>',
	        invalidEmailIds			: '<spring:message javaScriptEscape="true" code="js.report.errors.invalidEmailIds"/>',
	        reportEnterValidDateRange : '<spring:message javaScriptEscape="true" code="js.errors.campaigns.campaignEnterValidDateRange"/>',
	        validationChannel   : '<spring:message javaScriptEscape="true" code="js.report.errors.channel"/>',
	        validationBillingGroup    : '<spring:message javaScriptEscape="true" code="js.report.errors.billing.group"/>'
	        
		},
		text:{
	        startDateButtonText  	: '<spring:message javaScriptEscape="true" code="js.report.text.startDateButtonText"/>', 
	        endDateButtonText  		: '<spring:message javaScriptEscape="true" code="js.report.text.endDateButtonText"/>',
	        generateButtonText		: '<spring:message javaScriptEscape="true" code="ui.label.report.generate"/>',
	        downloadstartshortly : '<spring:message javaScriptEscape="true" code="label.message.download.start"/>'
		},
		alerts:{
	        genericChartEmailSuccess  		: '<spring:message javaScriptEscape="true" code="js.report.alerts.genericChartEmailSuccess"/>',
	        genericChartEmailFailure  		: '<spring:message javaScriptEscape="true" code="js.report.alerts.genericChartEmailFailure"/>',
	        exportFailure  					: '<spring:message javaScriptEscape="true" code="js.report.alerts.exportFailure"/>',
	        noData							: '<spring:message javaScriptEscape="true" code="js.report.alerts.noData"/>',
	        generateSuccess					: '<spring:message javaScriptEscape="true" code="js.report.alerts.generateSuccess"/>',
	        sendemailSuccess				: '<spring:message javaScriptEscape="true" code="js.report.alerts.sendemailSuccess"/>'
		} 
};
</script>
<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<script language="javascript">
if( typeof i18n === 'undefined' ) {
  var i18n = {};
}
if( typeof i18n.errors === 'undefined' ) {
  i18n.errors = {};
}

i18n.errors.connector = {
    createfailed	: '<spring:message javaScriptEscape="true" code="connector.instance.add.failure"/>',
    disable			: '<spring:message javaScriptEscape="true" code="label.disable"/>',
    enable			: '<spring:message javaScriptEscape="true" code="label.enable"/>',
    disablesuccess	: '<spring:message javaScriptEscape="true" code="oss.service.disable.success"/>',
    enablesuccess	: '<spring:message javaScriptEscape="true" code="oss.service.enable.success"/>',
   
	fields : {
        maxlength	: '<spring:message javaScriptEscape="true" code="js.errors.connector.field.maxlength"/>',
        minlength	: '<spring:message javaScriptEscape="true" code="js.errors.connector.field.minlength"/>',
        maxvalue	: '<spring:message javaScriptEscape="true" code="js.errors.connector.field.maxvalue"/>',
        minvalue	: '<spring:message javaScriptEscape="true" code="js.errors.connector.field.minvalue"/>',
        digits		: '<spring:message javaScriptEscape="true" code="js.errors.connector.field.digits"/>',
        password	: '<spring:message javaScriptEscape="true" code="js.errors.connector.field.password"/>',
        url		: '<spring:message javaScriptEscape="true" code="connector.please.enter.valid.url"/>',
        required	: '<spring:message javaScriptEscape="true" code="js.errors.connector.field.required"/>',
        regex   	: '<spring:message javaScriptEscape="true" code="js.errors.connector.field.regex"/>'
    }
  };
</script>
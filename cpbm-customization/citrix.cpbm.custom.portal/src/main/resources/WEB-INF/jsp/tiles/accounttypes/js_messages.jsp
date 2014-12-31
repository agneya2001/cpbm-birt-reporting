<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<script language="javascript">
if( typeof accountTypeDictionary === 'undefined' ) {
    var accountTypeDictionary = {};
  }
accountTypeDictionary = {    
    SAVING: '<spring:message javaScriptEscape="true" code="label.saving.processing"/>',
    SAVE: '<spring:message javaScriptEscape="true" code="ui.accounts.all.save"/>',
    REQUIEREDMAXUSERERROR:'<spring:message javaScriptEscape="true" code="js.errors.accounttype.maxuser.required"/>',
    INVALIDVALUEUSERERROR:'<spring:message javaScriptEscape="true" code="js.errors.accounttype.maxuser.valueerror"/>',
    INVALIDVALUELCEERROR:'<spring:message javaScriptEscape="true" code="js.errors.accounttype.lce.valueerror"/>',
    INVALIDVALUEERROR:'<spring:message javaScriptEscape="true" code="js.errors.accounttype.generic.valueerror"/>',
    REQUIEREDGRACEPERIODERROR:'<spring:message javaScriptEscape="true" code="js.errors.accounttype.graceperiod.requiered"/>',
    FAILEDTOEDITCREDITEXPOSURE:'<spring:message javaScriptEscape="true" code="js.errors.accounttypes.creditexposure.error"/>',
    FAILEDTOEDITINITIALDEPOSITAMOUNT:'<spring:message javaScriptEscape="true" code="js.errors.accounttype.ida.valueerror"/>',
    FAILEDTOEDITUSERLIMIT:'<spring:message javaScriptEscape="true" code="js.errors.accounttype.maxuser.limiterror"/>',
    ZEROMAXUSERLIMIT:'<spring:message javaScriptEscape="true" code="js.errors.accounttype.maxuser.zeroerror"/>',
    MAXPRECISION:'<spring:message javaScriptEscape="true" arguments="${currencyFractionalDigitsLimit}"  code="js.error.precision.value"/>'
      
            	
};
</script>
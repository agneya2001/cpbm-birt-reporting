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

i18n.errors.register = {
      postalcode 					: '<spring:message javaScriptEscape="true" code="js.errors.register.postalcode"/>',
      validateState 				: '<spring:message javaScriptEscape="true" code="js.errors.register.validateState"/>',
      countryCode					: '<spring:message javaScriptEscape="true" code="js.errors.register.countryCode"/>',
      countryCodeValidationError	: '<spring:message javaScriptEscape="true" code="js.errors.countryCode"/>',
      currency          : '<spring:message javaScriptEscape="true" code="js.errors.select.currency.error"/>',
      phonePin 						: '<spring:message javaScriptEscape="true" code="js.errors.register.phonePin"/>',
      phoneDetails 					: '<spring:message javaScriptEscape="true" code="js.errors.register.phoneDetails"/>',
      callRequested					: '<spring:message javaScriptEscape="true" code="js.errors.register.callRequested"/>',
      callFailed					: '<spring:message javaScriptEscape="true" code="js.errors.register.callFailed"/>',
      textMessageRequested			: '<spring:message javaScriptEscape="true" code="js.errors.register.textMessageRequested"/>',
      textMessageFailed				: '<spring:message javaScriptEscape="true" code="js.errors.register.textMessageFailed"/>',      
      secondaryAddress :{
        street1 : '<spring:message javaScriptEscape="true" code="js.errors.tenants.user.address.street1"/>',
        city : '<spring:message javaScriptEscape="true" code="js.errors.tenants.user.address.city"/>',
        postalCode : '<spring:message javaScriptEscape="true" code="js.errors.tenants.user.address.postalCode"/>',
        state : '<spring:message javaScriptEscape="true" code="js.errors.tenants.user.address.state"/>',
        country : '<spring:message javaScriptEscape="true" code="js.errors.tenants.user.address.country"/>'
      },
      user : {
        flnameValidationError		: "<spring:message javaScriptEscape="true" code="js.errors.flname"/>",
        firstName 					: '<spring:message javaScriptEscape="true" code="js.errors.register.user.firstName"/>',
        lastName 					: '<spring:message javaScriptEscape="true" code="js.errors.register.user.lastName"/>',
        title 						: '<spring:message javaScriptEscape="true" code="js.errors.register.user.title"/>',
        emailRequired 				: '<spring:message javaScriptEscape="true" code="js.errors.register.user.emailRequired"/>',
        email 						: '<spring:message javaScriptEscape="true" code="js.errors.register.user.email"/>',
        confirmEmail 				: '<spring:message javaScriptEscape="true" code="js.errors.register.user.confirmEmail"/>',
        confirmEmailEqualTo 		: '<spring:message javaScriptEscape="true" code="js.errors.register.user.confirmEmailEqualTo"/>',
        username 					: '<spring:message javaScriptEscape="true" code="js.errors.register.user.username"/>',
        usernameRemote 				: '<spring:message javaScriptEscape="true" code="js.errors.register.user.usernameRemote"/>',
        validateUsername 			: '<spring:message javaScriptEscape="true" code="js.errors.register.user.validateUsername"/>',
        minLengthUsername 			: '<spring:message javaScriptEscape="true" code="js.errors.register.user.username.minlength"/>',
        clearPassword 				: '<spring:message javaScriptEscape="true" code="js.errors.register.user.clearPassword"/>',
        passwordValidationeError	: '<spring:message javaScriptEscape="true" code="js.errors.password"/>',
        passwordConfirm 			: '<spring:message javaScriptEscape="true" code="js.errors.register.user.passwordConfirm"/>',
        passwordConfirmEqualTo 		: '<spring:message javaScriptEscape="true" code="js.errors.register.user.passwordConfirmEqualTo"/>',
        passwordequsername 			: '<spring:message javaScriptEscape="true" code="js.errors.register.user.passwordequsername"/>',
        phone 						: '<spring:message javaScriptEscape="true" code="js.errors.register.user.phone"/>',
        phoneValidationError		: "<spring:message javaScriptEscape="true" code="js.errors.phone"/>",
        address :  {
          street1 					: '<spring:message javaScriptEscape="true" code="js.errors.register.user.address.street1"/>',
          city 						: '<spring:message javaScriptEscape="true" code="js.errors.register.user.address.city"/>',
          state 					: '<spring:message javaScriptEscape="true" code="js.errors.register.user.address.state"/>',
          postalCode 				: '<spring:message javaScriptEscape="true" code="js.errors.register.postalcode"/>',
          country 					: '<spring:message javaScriptEscape="true" code="js.errors.register.user.address.country"/>'
        }
      },
      tenant : {
        name 						: '<spring:message javaScriptEscape="true" code="js.errors.register.tenant.name"/>'
      },
      trailCodeRequired				: '<spring:message javaScriptEscape="true" code="js.errors.register.trailCodeRequired"/>',
      trailCode 					: '<spring:message javaScriptEscape="true" code="js.errors.register.trailCode"/>',
      acceptedTerms 				: '<spring:message javaScriptEscape="true" code="js.errors.register.acceptedTerms"/>',
      tncDialog : {
        title 						: '<spring:message javaScriptEscape="true" code="js.errors.register.tncDialog.title"/>',
        buttons : {
          ok 						: '<spring:message javaScriptEscape="true" code="js.errors.register.tncDialog.buttons.ok"/>'
        }
      }
    };

  i18n.labels = {
    	phoneVerificationCallMe			:	'<spring:message javaScriptEscape="true" code="label.phoneVerification.callMe"/>',
  		phoneVerificationCalling		:	'<spring:message javaScriptEscape="true" code="label.phoneVerification.calling"/>',
    	phoneVerificationTextMe			:	'<spring:message javaScriptEscape="true" code="label.phoneVerification.textMe"/>',
  		phoneVerificationSending		:	'<spring:message javaScriptEscape="true" code="label.phoneVerification.sending"/>',  		
  		countryCode						:	'<spring:message javaScriptEscape="true" code="label.moreUserInfo.country.code"/>'
      };

</script>
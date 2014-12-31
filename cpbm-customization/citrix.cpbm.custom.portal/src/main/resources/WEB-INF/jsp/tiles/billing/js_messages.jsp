<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<script language="javascript">
var i18n = {
  errors: {
    priceRequired : '<spring:message javaScriptEscape="true" code="js.errors.priceRequired"/>',
    validateState : '<spring:message javaScriptEscape="true" code="js.errors.validateState"/>',
    checkcardExp : '<spring:message javaScriptEscape="true" code="js.errors.checkcardExp"/>',
    creditCard : {
      creditCardType : '<spring:message javaScriptEscape="true" code="js.errors.creditCard.creditCardType"/>',
      creditCardNumber : '<spring:message javaScriptEscape="true" code="js.errors.creditCard.creditCardNumber"/>',
			creditCardNumberValid : '<spring:message javaScriptEscape="true" code="js.errors.creditCard.creditCardNumberValid"/>',
      creditCardCVV : '<spring:message javaScriptEscape="true" code="js.errors.creditCard.creditCardCVV"/>',
      creditCardCVVDigits : '<spring:message javaScriptEscape="true" code="js.errors.creditCard.creditCardCVVDigits"/>',
      creditCardExpirationMonth : '<spring:message javaScriptEscape="true" code="js.errors.creditCard.creditCardExpiration.month"/>',
      creditCardExpirationYear : '<spring:message javaScriptEscape="true" code="js.errors.creditCard.creditCardExpiration.year"/>',
      firstNameOnCard : '<spring:message javaScriptEscape="true" code="js.errors.creditCard.firstNameOnCard"/>',
      maxLength: '<spring:message javaScriptEscape="true" code="js.errors.billingForm.maxLength"/>',
      lastNameOnCard : '<spring:message javaScriptEscape="true" code="js.errors.creditCard.lastNameOnCard"/>',
      creditCardAddress : {
        street1 : '<spring:message javaScriptEscape="true" code="js.errors.creditCard.creditCardAddress.street1"/>',
        city : '<spring:message javaScriptEscape="true" code="js.errors.creditCard.creditCardAddress.city"/>',
        postalCode : '<spring:message javaScriptEscape="true" code="js.errors.creditCard.creditCardAddress.postalCode"/>',
        country : '<spring:message javaScriptEscape="true" code="js.errors.creditCard.creditCardAddress.country"/>',
        state : '<spring:message javaScriptEscape="true" code="js.errors.creditCard.creditCardAddress.state"/>'
      }
    },
    invoicePayAmount : '<spring:message javaScriptEscape="true" code="js.errors.invoicePayAmount"/>',
    invoicePayAmountMaximum : '<spring:message javaScriptEscape="true" code="js.errors.invoicePayAmount.maximum"/>',
    miscFormErrors : '<spring:message javaScriptEscape="true" code="js.errors.miscFormErrors"/>',
    subscription : {
      editSubscription : '<spring:message javaScriptEscape="true" code="js.errors.subscription.editSubscription"/>',
      termSubscription : '<spring:message javaScriptEscape="true" code="js.errors.subscription.termSubscription"/>'
    } 
  }  
};
</script>
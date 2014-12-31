<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/WEB-INF/jsp/tiles/billing/js_messages.jsp"></jsp:include>
<script language="javascript">
if( typeof i18n === 'undefined' ) {
  var i18n = {};
}
if( typeof i18n.errors === 'undefined' ) {
  i18n.errors = {};
}
if( typeof i18n.confirm === 'undefined' ) {
  i18n.confirm = {};
}

if( typeof i18n.label === 'undefined' ) {
  i18n.label = {};
}

i18n.label = {
    choose :  '<spring:message javaScriptEscape="true" code="label.choose"/>'  
};

i18n.confirm = {
    convertaccounttypeAction : '<spring:message javaScriptEscape="true" code="js.confirm.convertaccounttypeAction"/>',
    terminateAccount : '<spring:message javaScriptEscape="true" code="js.confirm.terminateTenant"/>',
    cleantAccount : '<spring:message javaScriptEscape="true" code="js.confirm.cleantAccount"/>',
    removeTenant : '<spring:message javaScriptEscape="true" code="js.confirm.removeTenant"/>'
};

i18n.errors.tenants = {
    channelParam : '<spring:message javaScriptEscape="true" code="js.errors.tenants.channelParam"/>',
    confirmEmail : '<spring:message javaScriptEscape="true" code="js.errors.tenants.confirmEmail"/>',
    confirmEmailEqualTo : '<spring:message javaScriptEscape="true" code="js.errors.tenants.confirmEmailEqualTo"/>',
    currency          : '<spring:message javaScriptEscape="true" code="js.errors.select.currency.error"/>',
    trailCodeRequired : '<spring:message javaScriptEscape="true" code="js.errors.register.trailCodeRequired"/>',
    trailCode : '<spring:message javaScriptEscape="true" code="js.errors.register.trailCode"/>',
    secondaryAddress :{
      street1 : '<spring:message javaScriptEscape="true" code="js.errors.tenants.user.address.street1"/>',
      city : '<spring:message javaScriptEscape="true" code="js.errors.tenants.user.address.city"/>',
      postalCode : '<spring:message javaScriptEscape="true" code="js.errors.tenants.user.address.postalCode"/>',
      state : '<spring:message javaScriptEscape="true" code="js.errors.tenants.user.address.state"/>',
      country : '<spring:message javaScriptEscape="true" code="js.errors.tenants.user.address.country"/>'
    },
    user : {
      flnameValidationError : "<spring:message javaScriptEscape="true" code="js.errors.flname"/>",
      firstName : '<spring:message javaScriptEscape="true" code="js.errors.tenants.user.firstName"/>',
      lastName : '<spring:message javaScriptEscape="true" code="js.errors.tenants.user.lastName"/>',
      emailRequired : '<spring:message javaScriptEscape="true" code="js.errors.tenants.user.emailRequired"/>',
      email : '<spring:message javaScriptEscape="true" code="js.errors.tenants.user.email"/>',
      emailNotSupported : '<spring:message javaScriptEscape="true" code="js.errors.tenants.user.emailNotSupported"/>',
      username : '<spring:message javaScriptEscape="true" code="js.errors.tenants.user.username"/>',
      validateUsername: '<spring:message javaScriptEscape="true" code="js.errors.tenants.user.validateUsername"/>',
      minLengthUsername : '<spring:message javaScriptEscape="true" code="js.errors.tenants.user.username.minlength"/>',
      usernameRemote : '<spring:message javaScriptEscape="true" code="js.errors.tenants.user.usernameRemote"/>',
      clearPassword : '<spring:message javaScriptEscape="true" code="js.errors.tenants.user.clearPassword"/>',
      passwordValidationError : '<spring:message javaScriptEscape="true" code="js.errors.password"/>',
      passwordequsername : '<spring:message javaScriptEscape="true" code="js.errors.tenants.user.passwordequsername"/>',
      phone : '<spring:message javaScriptEscape="true" code="js.errors.tenants.user.phone"/>',
      phoneValidationError : "<spring:message javaScriptEscape="true" code="js.errors.phone"/>",
      countryCode : '<spring:message javaScriptEscape="true" code="js.errors.tenants.user.countryCode"/>',
      countryCodeValidationError : '<spring:message javaScriptEscape="true" code="js.errors.countryCode"/>',
      noServiceEnabledForApiCreds : '<spring:message htmlEscape="false" javaScriptEscape="true" code="error.no.cloud.service.account.for.api.credentials" arguments="${tenant.param}"/>',
      address : {
        street1 : '<spring:message javaScriptEscape="true" code="js.errors.tenants.user.address.street1"/>',
        city : '<spring:message javaScriptEscape="true" code="js.errors.tenants.user.address.city"/>',
        postalCode : '<spring:message javaScriptEscape="true" code="js.errors.tenants.user.address.postalCode"/>',
        state : '<spring:message javaScriptEscape="true" code="js.errors.tenants.user.address.state"/>',
        country : '<spring:message javaScriptEscape="true" code="js.errors.tenants.user.address.country"/>'
      }
    },
    tenant: {
  	    name : '<spring:message javaScriptEscape="true" code="js.errors.tenants.tenant.name"/>',
        suffix : '<spring:message javaScriptEscape="true" code="js.errors.tenants.tenant.suffix"/>',
        suffixMinLength : '<spring:message javaScriptEscape="true" code="js.errors.tenants.tenant.suffixMinLength"/>',
        suffixValidate : '<spring:message javaScriptEscape="true" code="js.errors.tenants.tenant.suffixValidate"/>',
        suffixRemoteValidate : '<spring:message javaScriptEscape="true" code="js.errors.tenants.tenant.suffixRemoteValidate"/>',
        country : '<spring:message javaScriptEscape="true" code="js.errors.tenants.tenant.country"/>',
        state : '<spring:message javaScriptEscape="true" code="js.errors.tenants.tenant.state"/>',
        street1 : '<spring:message javaScriptEscape="true" code="js.errors.tenants.tenant.street1"/>',
        postalCode : '<spring:message javaScriptEscape="true" code="js.errors.tenants.tenant.postalCode"/>',
        spendlimit : '<spring:message javaScriptEscape="true" code="js.errors.spendbudget.number"/>',
        city : '<spring:message javaScriptEscape="true" code="js.errors.tenants.tenant.city"/>',
        max_four_decimal_value: '<spring:message javaScriptEscape="true" arguments="${currencyFractionalDigitsLimit}"  code="js.errors.maxFourDecimalLimit"/>',
        currency : {
				error : '<spring:message javaScriptEscape="true" code="js.errors.tenants.tenant.currency"/>'
			}
      },

    passwordconfirm : '<spring:message javaScriptEscape="true" code="js.errors.tenants.passwordconfirm"/>',
    passwordconfirmEqualTo : '<spring:message javaScriptEscape="true" code="js.errors.tenants.passwordconfirmEqualTo"/>',
    passwordIsMandatory: '<spring:message javaScriptEscape="true" code="errors.password.required"/>',
    wrongPassword: '<spring:message javaScriptEscape="true" code="errors.password.invalid"/>',        
    ChangeToCorporateDialog : {
      title : '<spring:message javaScriptEscape="true" code="js.errors.tenants.ChangeToCorporateDialog.title"/>',
      buttons : {
        ok : '<spring:message javaScriptEscape="true" code="js.errors.tenants.ChangeToCorporateDialog.buttons.ok"/>',
        cancel : '<spring:message javaScriptEscape="true" code="js.errors.tenants.ChangeToCorporateDialog.buttons.cancel"/>'
      }
    },
    resourceLimitDialog : {
      title : '<spring:message javaScriptEscape="true" code="js.errors.tenants.resourceLimitDialog.title"/>',
      buttons : {
        ok : '<spring:message javaScriptEscape="true" code="js.errors.tenants.resourceLimitDialog.buttons.ok"/>',
        cancel : '<spring:message javaScriptEscape="true" code="js.errors.tenants.resourceLimitDialog.buttons.cancel"/>'
      }
    },
    addCreditDialog : {
      title : '<spring:message javaScriptEscape="true" code="js.errors.tenants.addCreditDialog.title"/>',
      buttons : {
        ok : '<spring:message javaScriptEscape="true" code="js.errors.tenants.addCreditDialog.buttons.ok"/>',
        cancel : '<spring:message javaScriptEscape="true" code="js.errors.tenants.addCreditDialog.buttons.cancel"/>'
      },
      validate : {
        amount : '<spring:message javaScriptEscape="true" code="js.errors.tenants.addCreditDialog.validate.amount"/>'
      }
    },
    changeOwnerDialog : {
      title : '<spring:message javaScriptEscape="true" code="js.errors.tenants.changeOwnerDialog.title"/>',
      buttons : {
        ok : '<spring:message javaScriptEscape="true" code="js.errors.tenants.changeOwnerDialog.buttons.ok"/>',
        cancel : '<spring:message javaScriptEscape="true" code="js.errors.tenants.changeOwnerDialog.buttons.cancel"/>'
      }
    },
    recordDepositDialog : {
      title : '<spring:message javaScriptEscape="true" code="js.errors.tenants.recordDepositDialog.title"/>',
      buttons : {
        cancel : '<spring:message javaScriptEscape="true" code="js.errors.tenants.recordDepositDialog.buttons.cancel"/>',
        save : '<spring:message javaScriptEscape="true" code="js.errors.tenants.recordDepositDialog.buttons.save"/>'
      },
      validate : {
        receivedOn : '<spring:message javaScriptEscape="true" code="js.errors.tenants.recordDepositDialog.validate.receivedOn"/>',
        amount : '<spring:message javaScriptEscape="true" code="js.errors.tenants.recordDepositDialog.validate.amount"/>'
      }
    },
    resourceLimitForm : {
      error : '<spring:message javaScriptEscape="true" code="js.errors.tenants.resourceLimitForm.error"/>',
      maxProjects : '<spring:message javaScriptEscape="true" code="js.errors.tenants.resourceLimitForm.maxProjects"/>',
      userLimitError : '<spring:message javaScriptEscape="true" code="js.errors.tenants.resourceLimitForm.maxUserLimiterror"/>',
      maxUsers : '<spring:message javaScriptEscape="true" code="js.errors.tenants.resourceLimitForm.maxUsers"/>'
    },
    tenantEditForm : {
      title : '<spring:message javaScriptEscape="true" code="js.errors.tenants.tenantEditForm.title"/>',
      saving : '<spring:message javaScriptEscape="true" code="js.errors.tenants.tenantEditForm.saving"/>',
      save : '<spring:message javaScriptEscape="true" code="js.errors.tenants.tenantEditForm.save"/>',
      error : '<spring:message javaScriptEscape="true" code="js.errors.tenants.tenantEditForm.error"/>'
    },
    issueCreditForm : {
      title : '<spring:message javaScriptEscape="true" code="js.errors.tenants.issueCreditForm.title"/>',
      saving : '<spring:message javaScriptEscape="true" code="js.errors.tenants.issueCreditForm.saving"/>',
      error : '<spring:message javaScriptEscape="true" code="js.errors.tenants.issueCreditForm.error"/>'
    },
    changeStateForm : {
      title : '<spring:message javaScriptEscape="true" code="js.errors.tenants.changeStateForm.title"/>',
      saving : '<spring:message javaScriptEscape="true" code="js.errors.tenants.changeStateForm.saving"/>',
      errors : '<spring:message javaScriptEscape="true" code="js.errors.tenants.changeStateForm.errors"/>'
    },
    cleantAccount : {
      error : '<spring:message javaScriptEscape="true" code="js.errors.tenants.cleantAccount.error"/>'
    },
    removeTenant : {
      error : '<spring:message javaScriptEscape="true" code="js.errors.tenants.removeTenant.error"/>'
    },
    approve : {
    	error : '<spring:message javaScriptEscape="true" code="js.errors.tenants.approve.error"/>'
    },	
    editAccount : {
      error : '<spring:message javaScriptEscape="true" code="js.errors.tenants.editAccount.error"/>'
    },
    accountType : {
        prePaidAmount : {
         error : '<spring:message javaScriptEscape="true" code="js.errors.tenants.prePaidAmount"/>'
        },
        noManualRegistrationAccountType : {
          error : '<spring:message javaScriptEscape="true" code="js.errors.tenants.accounttypes.nomanualregistration"/>'
         }
    },
  	creditCard : {
       creditCardType : {
         error : '<spring:message javaScriptEscape="true" code="js.errors.tenants.creditCard.creditCardType"/>'
       },
  			creditCardNumber : {
  				error : '<spring:message javaScriptEscape="true" code="js.errors.tenants.creditCard.creditCardNumber"/>'
  			},
  			firstNameOnCard : {
  				error : '<spring:message javaScriptEscape="true" code="js.errors.tenants.creditCard.firstNameOnCard"/>'
  			},
  			lastNameOnCard : {
  				error : '<spring:message javaScriptEscape="true" code="js.errors.tenants.creditCard.lastNameOnCard"/>'
  			},
  			creditCardCVV : {
  				error : '<spring:message javaScriptEscape="true" code="js.errors.tenants.creditCard.creditCardCVV"/>'
  			},
  			creditCardExpirationMonth : {
  				error : '<spring:message javaScriptEscape="true" code="js.errors.tenants.creditCard.creditCardExpirationMonth"/>'
  			},
  			creditCardExpirationYear : {
  				error : '<spring:message javaScriptEscape="true" code="js.errors.tenants.creditCard.creditCardExpirationYear"/>'
  			}
  		},
  		initialPayment : {
  			error : '<spring:message javaScriptEscape="true" code="errors.general"/>'
  		},
  		
  		billingAddress : {
  			street1 : {
  				error : '<spring:message javaScriptEscape="true" code="js.errors.tenants.billingAddress.street1"/>'
  			},
  			postalCode : {
  				error : '<spring:message javaScriptEscape="true" code="js.errors.tenants.billingAddress.postalCode"/>'
  			},
  			country : {
  				error : '<spring:message javaScriptEscape="true" code="js.errors.tenants.billingAddress.country"/>'
  			}
  		},
  		accountLimitsForm: {
             title : '<spring:message javaScriptEscape="true" code="js.errors.tenants.accountLimitsForm.title"/>',
 			       save: '<spring:message javaScriptEscape="true" code="js.errors.tenants.accountLimitsForm.save"/>',
 			       saving: '<spring:message javaScriptEscape="true" code="js.errors.tenants.accountLimitsForm.saving"/>',
 			       error: '<spring:message javaScriptEscape="true" code="js.errors.tenants.accountLimitsForm.error"/>',
 			       resourceLimit: {
 			         valError: '<spring:message javaScriptEscape="true" code="js.errors.tenants.accountLimitsForm.resourceLimit.valError"/>'
 			       }
  		}
	};
var currPrecision="<c:out value="${currencyFractionalDigitsLimit}"/>";
</script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/jquery.selectboxes.min.js"></script>

<c:choose>
	<c:when test="${not empty tenantForm}">
		<c:set var="countries" scope="request" value="${tenantForm.countryList}"/>
	</c:when>
	<c:otherwise>
		<c:set var="countries" scope="request" value="${billingInfo.countryList}"/>
	</c:otherwise>
</c:choose>
<jsp:include page="/WEB-INF/jsp/tiles/shared/country_states.jsp"></jsp:include>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/jquery.stateselect.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/tenants.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/jquery.validate.creditcard2-1.0.1.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/billing.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/tasks.js"></script>

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
if( typeof i18n.label === 'undefined' ) {
  i18n.label = {};
}
if( typeof i18n.confirm === 'undefined' ) {
  i18n.confirm = {};
}
if( typeof i18n.alert === 'undefined' ) {
  i18n.alert = {};
}
if( typeof i18n.user === 'undefined' ) {
  i18n.user = {};
}

i18n.errors = {
    tenantPercentage : {
      required : '<spring:message javaScriptEscape="true" code="js.errors.tenantPercentage.required"/>',
      percentage : '<spring:message javaScriptEscape="true" code="js.errors.tenantPercentage.percentage"/>',
      remote : '<spring:message javaScriptEscape="true" code="js.errors.tenantPercentage.remote"/>',
      regex : '<spring:message javaScriptEscape="true" code="js.errors.tenantPercentage.regex"/>'
    },
    spendLimit : {
      required : '<spring:message javaScriptEscape="true" code="js.errors.spendLimit.required"/>',
      number: '<spring:message javaScriptEscape="true" code="js.errors.spendbudget.number"/>',
      greaterThanZero: '<spring:message javaScriptEscape="true" code="js.errors.spendbudget.greaterthanzero"/>'
    },
    addsecAlert: {
        requiredEmail: '<spring:message javaScriptEscape="true" code="userAlertEmailForm.email[NotNull]"/>',
        validEmail : '<spring:message javaScriptEscape="true" code="js.errors.addsecAlert.validEmail"/>',
        failedAddEmail : '<spring:message javaScriptEscape="true" code="js.errors.addsecAlert.failedAddEmail"/>',
        sameAsPrimaryEmail : '<spring:message javaScriptEscape="true" code="js.errors.addsecAlert.sameAsPrimaryEmail"/>',
        emailAlreadyExists : '<spring:message javaScriptEscape="true" code="js.errors.addsecAlert.emailAlreadyExists"/>',
        unableAddEmail : '<spring:message javaScriptEscape="true" code="js.errors.addsecAlert.unableAddEmail"/>',
        failDeleteEmail : '<spring:message javaScriptEscape="true" code="js.errors.addsecAlert.failDeleteEmail"/>',
        failVerifyEmail : '<spring:message javaScriptEscape="true" code="js.errors.addsecAlert.failVerifyEmail"/>',
        failMakePrimary :'<spring:message javaScriptEscape="true" code="js.errors.addsecAlert.failMakePrimary"/>',
        longEmail : '<spring:message javaScriptEscape="true" code="js.errors.billingForm.maxLength"/>'
      },
      countryCode : '<spring:message javaScriptEscape="true" code="js.errors.register.countryCode"/>',
      countryCodeValidationError : '<spring:message javaScriptEscape="true" code="js.errors.countryCode"/>',
      countryCodeNumber : '<spring:message javaScriptEscape="true" code="js.errors.register.countryCodeNumber"/>',
      phonePin : '<spring:message javaScriptEscape="true" code="js.user.phonePin"/>',
      phoneDetails : '<spring:message javaScriptEscape="true" code="js.errors.register.phoneDetails"/>',
      callRequested: '<spring:message javaScriptEscape="true" code="js.errors.register.callRequested"/>',
      callFailed: '<spring:message javaScriptEscape="true" code="js.errors.register.callFailed"/>',
      textMessageRequested      : '<spring:message javaScriptEscape="true" code="js.errors.register.textMessageRequested"/>',
      textMessageFailed       : '<spring:message javaScriptEscape="true" code="js.errors.register.textMessageFailed"/>',
      isdCodeFetchFailed        : '<spring:message javaScriptEscape="true" code="js.error.user.isdCodeFetchFailed"/>',
      isEmailBlacklisted : '<spring:message javaScriptEscape="true" code="signup.emaildomain.blacklist.error"/>',
      keyNameIsRequired: '<spring:message javaScriptEscape="true" code="key.name.is.required"/>',
      failedToGenerateSSHKey: '<spring:message javaScriptEscape="true" code="failed.to.generate.SSH.key"/>',
      wrongPassword: '<spring:message javaScriptEscape="true" code="errors.password.invalid"/>',
      passwordIsMandatory: '<spring:message javaScriptEscape="true" code="errors.password.required"/>',
      failedToUploadSSHKey: '<spring:message javaScriptEscape="true" code="failed.to.upload.SSH.key"/>',
      publicKeyIsRequiredWhenUploadingSSHKey: '<spring:message javaScriptEscape="true" code="public.key.is.required.when.uploading.SSH.key"/>',
      publicKeyShouldBeginWith: '<spring:message javaScriptEscape="true" code="public.key.should.begin.with"/>',
      failedToDeleteSSHKey: '<spring:message javaScriptEscape="true" code="failed.to.delete.SSH.key"/>',
      onlyAlphanumericCharactersAreAllowed: '<spring:message javaScriptEscape="true" code="only.alphanumeric.characters.are.allowed"/>'
  };

i18n.label={  
    addsecAlert : {
     addingEmail : '<spring:message javaScriptEscape="true" code="js.label.addsecAlert.addingEmail"/>',
     addEmail : '<spring:message javaScriptEscape="true" code="js.label.addsecAlert.addEmail"/>'     
    },
    alertlist:{
      spendbudget: '<spring:message javaScriptEscape="true" code="ui.alerts.details.spendbudget.label"/>',
      type: {
        tenant: '<spring:message javaScriptEscape="true" code="js.label.alertlist.type.tenant"/>',
        user: '<spring:message javaScriptEscape="true" code="js.label.alertlist.type.user"/>',
        project: '<spring:message javaScriptEscape="true" code="js.label.alertlist.type.project"/>',
        membership: '<spring:message javaScriptEscape="true" code="js.label.alertlist.type.membership"/>'
      }
    }
  };

  i18n.confirm = {
    addsecAlert : {
       verificationEmail : '<spring:message javaScriptEscape="true" code="js.confirm.addsecAlert.verificationEmail"/>',
       deleteEmail : '<spring:message javaScriptEscape="true" code="js.confirm.addsecAlert.deleteEmail"/>',
       makePrimary : '<spring:message javaScriptEscape="true" code="js.confirm.addsecAlert.makePrimary"/>'
    },
    removeAlert : '<spring:message javaScriptEscape="true" code="js.confirm.removeAlert"/>'
  };
  
 i18n.alert = {
    createNewAlert: '<spring:message javaScriptEscape="true" code="js.alert.createNewAlert"/>',
    editAlert: '<spring:message javaScriptEscape="true" code="js.alert.editAlert"/>',
    removeAlert: '<spring:message javaScriptEscape="true" code="js.alert.removeAlert"/>'
  };

 i18n.user = {
    delfail: '<spring:message javaScriptEscape="true" code="js.user.del.fail"/>',
    deactfail: '<spring:message javaScriptEscape="true" code="js.user.deact.fail"/>',
    actfail: '<spring:message javaScriptEscape="true" code="js.user.act.fail"/>',
    channel: '<spring:message javaScriptEscape="true" code="js.user.channel"/>',
    title: '<spring:message javaScriptEscape="true" code="js.user.title"/>',
  del : '<spring:message javaScriptEscape="true" code="js.user.del.confirm"/>',
  generateAPIKey : '<spring:message javaScriptEscape="true" code="js.user.generate.api.key"/>',
  deact: '<spring:message javaScriptEscape="true" code="js.user.deact.confirm"/>',
  act: '<spring:message javaScriptEscape="true" code="js.user.act.confirm"/>',
  delproject : '<spring:message javaScriptEscape="true" code="js.user.del.confirmproject"/>',           
  max : '<spring:message javaScriptEscape="true" code="js.user.max"/>',
  cloudstorage: '<spring:message javaScriptEscape="true" code="js.user.cloudstorage.subscribe"/>',
  profile: '<spring:message javaScriptEscape="true" code="js.user.profile.edit"/>',
  flnameValidationError: "<spring:message javaScriptEscape="true" code="js.errors.flname"/>",
  firstname: '<spring:message javaScriptEscape="true" code="js.user.firstname"/>',
  lastname: '<spring:message javaScriptEscape="true" code="js.user.lastname"/>',
  email: '<spring:message javaScriptEscape="true" code="js.user.email"/>',
  emailmatch: '<spring:message javaScriptEscape="true" code="js.user.email.match"/>',
  confirmemail: '<spring:message javaScriptEscape="true" code="js.user.confirmemail"/>',
  emailformat: '<spring:message javaScriptEscape="true" code="js.user.email.format"/>',
  username: '<spring:message javaScriptEscape="true" code="js.user.username"/>',
  usernameexists: '<spring:message javaScriptEscape="true" code="js.user.username.exists"/>',
  password: '<spring:message javaScriptEscape="true" code="js.user.password"/>',
  passwordValidationError: '<spring:message javaScriptEscape="true" code="js.errors.password"/>',
  passwordconfirm: '<spring:message javaScriptEscape="true" code="js.user.password.confirm"/>',
  passwordmatch: '<spring:message javaScriptEscape="true" code="js.user.password.match"/>',
  profilerequired: '<spring:message javaScriptEscape="true" code="js.user.profile.required"/>',
  spendlimit: '<spring:message javaScriptEscape="true" code="js.errors.spendbudget.number"/>',
  passwordequsername: '<spring:message javaScriptEscape="true" code="js.user.passwordequsername"/>',
  phone : '<spring:message javaScriptEscape="true" code="js.errors.register.user.phone"/>',
  phoneValidationError:"<spring:message javaScriptEscape="true" code="js.errors.phone"/>"   
};
</script>

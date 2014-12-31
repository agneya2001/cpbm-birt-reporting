<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
	
<script type="text/javascript" src="<%=request.getContextPath() %>/js/jquery.selectboxes.min.js"></script>
<jsp:include page="/WEB-INF/jsp/tiles/shared/country_states.jsp"></jsp:include>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/jquery.stateselect.js"></script>
<script language="javascript">

  if( typeof i18n === 'undefined' ) {
	  var i18n = {};
	}
	if( typeof i18n.errors === 'undefined' ) {
	  i18n.errors = {};
	}
  var i18n = {
          user: {
            delfail: '<spring:message javaScriptEscape="true" code="js.user.del.fail"/>',
            deactfail: '<spring:message javaScriptEscape="true" code="js.user.deact.fail"/>',
            verfail: '<spring:message javaScriptEscape="true" code="js.user.ver.fail"/>',
            actfail: '<spring:message javaScriptEscape="true" code="js.user.act.fail"/>',
            channel: '<spring:message javaScriptEscape="true" code="js.user.channel"/>',
            title: '<spring:message javaScriptEscape="true" code="js.user.title"/>',
          del : '<spring:message javaScriptEscape="true" code="js.user.del.confirm"/>',
          generateAPIKey : '<spring:message javaScriptEscape="true" code="js.user.generate.api.key"/>',
          ver: '<spring:message javaScriptEscape="true" code="js.user.ver.confirm"/>',
          versucc: '<spring:message javaScriptEscape="true" code="js.user.ver.successstatus"/>',
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
          validateUsername : '<spring:message javaScriptEscape="true" code="js.errors.register.user.validateUsername"/>',
          minLengthUsername: '<spring:message javaScriptEscape="true" code="js.errors.register.user.username.minlength"/>',
          password: '<spring:message javaScriptEscape="true" code="js.user.password"/>',
          passwordValidationError: '<spring:message javaScriptEscape="true" code="js.errors.password"/>',
          oldPasswordNullError:'<spring:message javaScriptEscape="true" code="message.myprofile.oldpassword.null.error"/>',
          passwordconfirm: '<spring:message javaScriptEscape="true" code="js.user.password.confirm"/>',
          passwordmatch: '<spring:message javaScriptEscape="true" code="js.user.password.match"/>',
          profilerequired: '<spring:message javaScriptEscape="true" code="js.user.profile.required"/>',
          spendlimit: '<spring:message javaScriptEscape="true" code="js.errors.spendbudget.number"/>',
          passwordequsername: '<spring:message javaScriptEscape="true" code="js.user.passwordequsername"/>',
          phone : '<spring:message javaScriptEscape="true" code="js.errors.register.user.phone"/>',
          phoneValidationError: "<spring:message javaScriptEscape="true" code="js.errors.phone"/>"
          },
          errors:{
            countryCode : '<spring:message javaScriptEscape="true" code="js.errors.register.countryCode"/>',
            countryCodeValidationError : '<spring:message javaScriptEscape="true" code="js.errors.countryCode"/>',
            countryCodeNumber : '<spring:message javaScriptEscape="true" code="js.errors.register.countryCodeNumber"/>',
            phonePin : '<spring:message javaScriptEscape="true" code="js.user.phonePin"/>',
            phoneDetails : '<spring:message javaScriptEscape="true" code="js.errors.register.phoneDetails"/>',
            callRequested: '<spring:message javaScriptEscape="true" code="js.errors.register.callRequested"/>',
            callFailed: '<spring:message javaScriptEscape="true" code="js.errors.register.callFailed"/>',
		    textMessageRequested			: '<spring:message javaScriptEscape="true" code="js.errors.register.textMessageRequested"/>',
		    textMessageFailed				: '<spring:message javaScriptEscape="true" code="js.errors.register.textMessageFailed"/>'            
        }
    };

	  i18n.labels = {
	    phoneVerificationCallMe 		: '<spring:message javaScriptEscape="true" code="label.phoneVerification.callMe"/>',
	    phoneVerificationCalling  		: '<spring:message javaScriptEscape="true" code="label.phoneVerification.calling"/>',
    	phoneVerificationTextMe			:	'<spring:message javaScriptEscape="true" code="label.phoneVerification.textMe"/>',
  		phoneVerificationSending		:	'<spring:message javaScriptEscape="true" code="label.phoneVerification.sending"/>' 	    
	  };
</script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/users.js"></script>


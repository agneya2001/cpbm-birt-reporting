<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<script language="javascript">

var phoneVerificationEnabled=<c:out value="${isTelesignVerificationEnable}" />;

var i18n = {
  errors: {
    auth: {
      providePassword: '<spring:message javaScriptEscape="true" code="js.errors.register.user.clearPassword"/>',
      password: '<spring:message javaScriptEscape="true" code="js.errors.password"/>',
      confirmPassword: '<spring:message javaScriptEscape="true" code="js.errors.register.user.passwordConfirm"/>',
      passwordConfirmEqualTo: '<spring:message javaScriptEscape="true" code="js.errors.register.user.passwordConfirmEqualTo"/>',
      pinRequired: '<spring:message javaScriptEscape="true" code="js.user.phonePin"/>',
      callRequested: '<spring:message javaScriptEscape="true" code="js.errors.register.callRequested"/>',
      callFailed: '<spring:message javaScriptEscape="true" code="js.errors.register.callFailed"/>',
      textMessageRequested: '<spring:message javaScriptEscape="true" code="js.errors.register.textMessageRequested"/>',
      textMessageFailed: '<spring:message javaScriptEscape="true" code="js.errors.register.textMessageFailed"/>'      
    }
  },

	labels: {
	    phoneVerificationCallMe     : '<spring:message javaScriptEscape="true" code="label.phoneVerification.callMe"/>',
	    phoneVerificationCalling    : '<spring:message javaScriptEscape="true" code="label.phoneVerification.calling"/>',
	    phoneVerificationTextMe     : '<spring:message javaScriptEscape="true" code="label.phoneVerification.textMe"/>',
	    phoneVerificationSending    : '<spring:message javaScriptEscape="true" code="label.phoneVerification.sending"/>'  
	  }
};
</script>

<script type="text/javascript" src="<%=request.getContextPath() %>/js/phoneVerification.js"></script>


<div class="login_headerarea">
   	<div class="login_headerarea_left">
       	<%@ include file="../shared/channel_logo.jsp" %>
    </div>
</div>`
<div class="login_maincontentarea">
	<div class="login_maincontentarea_titlepanel">
	  <h1>
	    <c:if test="${reg_reset}">
        <spring:message code="label.reset.set.password"/>
	    </c:if>
	    <c:if test="${!reg_reset}">
        <spring:message code="label.reset.reset.password"/>
	    </c:if>
	  </h1>
    </div> 
	<div class="login_formbox">
		<form id="passwdResetForm" method="post" action="<%= request.getContextPath() %>/portal/reset_password" name="passwdResetForm">
		<c:if test="${isTelesignVerificationEnable}">
        <div class="phoneverification_titlebox">
          <div class="phone_vericon"></div>
          <h2><spring:message code="label.phoneVerification.title.identity.verification.by.telephone"/></h2>
          <p><spring:message code="label.phoneVerification.help.text.registered.user"/></p>
        </div>

        <div class="callme_submitpanel">
          <div>
            <a id="phoneVerificationCallByUser" class="callme_button" tabindex="120" href="#">
              <span class="call_icon"></span>
              <spring:message code="label.phoneVerification.callMe"/>
            </a>
            <a id="phoneVerificationSMSByUser" class="callme_button" tabindex="130" href="#" style="margin-left:17px;">
              <span class="text_icon"></span>
              <spring:message code="label.phoneVerification.textMe"/>
            </a>
          </div>
        </div>
    </c:if>
	  <ol>
       <c:if test="${isTelesignVerificationEnable}">
       <li>
            <div class="mandatory_wrapper"> 
            <label style="margin-top: 0px; margin-left: 10px;"><spring:message code="label.moreUserInfo.phoneVerificationPin"/></label>
            </div>
            <input class="text" tabindex="1" id="userEnteredPhoneVerificationPin" name="userEnteredPhoneVerificationPin" title='<spring:message code="label.moreUserInfo.phoneVerificationPin"/>'/>
            <div class="login_formbox_errormsg" id="userEnteredPhoneVerificationPinError"></div> 
            <div class="login_formbox_errormsg" id="verificationStatusSuccess" style="display:none">
              <div class="phonever_statusicon verified"></div>
              <span class="verified"><spring:message code="label.phoneVerification.verified"/></span>
            </div>
            <div class="login_formbox_errormsg" id="verificationStatusFailed" style="display:none">
              <div class="phonever_statusicon unverified"></div>
              <span class="unverified"><spring:message code="label.phoneVerification.unverified"/></span>
            </div>                                    
        </li>
        </c:if>
		    <li>
              <label for="password"><spring:message code="label.reset.new.password"/></label>
              <spring:message code="label.newPassword.tooltip" var="i18nNewPasswordTooltip"/>
              <input class="text" id="password" autocomplete="off" type="password" tabindex="2" name="password" title="<c:out value="${i18nNewPasswordTooltip}"/>"/>
              <div class="login_formbox_errormsg" id="passwordError"></div>
		    </li>
		    <li>
              <label for="password_confirm"><spring:message code="label.reset.confirm.password"/></label>
              <spring:message code="label.newPassword.confirm.tooltip" var="i18nConfirmNewPasswordTooltip"/>
             <input class="text" id="password_confirm" type="password" autocomplete="off" tabindex="3" name="password_confirm" title="<c:out value="${i18nConfirmNewPasswordTooltip}"/>"/>
             <div class="login_formbox_errormsg" id="password_confirmError"></div>
		    </li>
	    </ol>
		</form>
        <div class="login_formbox_submitpanel">
        	<div class="login_buttonscontainer">
            	<a id="ResetsubmitButton" tabindex="4" name="ResetsubmitButton" class="logincommonbutton" href="#" ><spring:message code="label.reset.submit"/></a>
            </div>
        </div>			
  </div>
</div>

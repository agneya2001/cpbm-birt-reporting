<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<script language="javascript">

var i18n = {
  errors: {
    auth: {
      providePassword: '<spring:message javaScriptEscape="true" code="js.errors.register.user.clearPassword"/>',
      password: '<spring:message javaScriptEscape="true" code="js.errors.password"/>',
      confirmPassword: '<spring:message javaScriptEscape="true" code="js.errors.register.user.passwordConfirm"/>',
      passwordConfirmEqualTo: '<spring:message javaScriptEscape="true" code="js.errors.register.user.passwordConfirmEqualTo"/>'
    }
  },

  labels: {
    }
};


</script>

<script type="text/javascript" src="<%=request.getContextPath() %>/js/setpassword.js"></script>


<div class="login_headerarea">
    <div class="login_headerarea_left">
        <%@ include file="../shared/channel_logo.jsp" %>
    </div>
</div>`
<div class="login_maincontentarea">
  <div class="login_maincontentarea_titlepanel">
      <h1> 
          <label ><spring:message code="label.reset.set.password"/></label>
      </h1>
  </div> 
  <div class="login_formbox">
    <form id="passwdResetForm" method="post" action="<%= request.getContextPath() %>/portal/setpassword" name="passwdResetForm">
    <ol>
        <li>
          <label for="password"><spring:message code="label.reset.new.password"/></label>
              <spring:message code="label.newPassword.tooltip" var="i18nNewPasswordTooltip"/>
              <input class="text" id="password" autocomplete="off" type="password" tabindex="2" name="password" title="<c:out value="${i18nNewPasswordTooltip}"/>"/>
          <div class="login_formbox_errormsg" id="passwordError"></div>
        </li>
        <li>
          <label for="password_confirm"><spring:message code="label.reset.confirm.password"/></label>
              <spring:message code="label.newPassword.confirm.tooltip" var="i18nConfirmNewPasswordTooltip"/>
              <input class="text" id="password_confirm" type="password" autocomplete="off" tabindex="2" name="password_confirm" title="<c:out value="${i18nConfirmNewPasswordTooltip}"/>"/>
              <div class="login_formbox_errormsg" id="password_confirmError"></div>
        </li>
      </ol>
    </form>
        <div class="login_formbox_submitpanel">
          <div class="login_buttonscontainer">
              <a id="ResetsubmitButton" name="ResetsubmitButton" class="logincommonbutton" href="#" tabindex="3"><spring:message code="label.reset.submit"/></a>
            </div>
        </div>      
  </div>
</div>

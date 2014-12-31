<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ page import="net.tanesha.recaptcha.ReCaptcha" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<script type="text/javascript">

var dictionary = { 
      
};

</script>
        <div class="maintitlebox">
            <h1><spring:message code="label.newUserStep2.heading"/></h1>
        </div>
        <spring:url value="/portal/users/new/step3" var="newUserStep3" htmlEscape="false" />
        <form:form name="add_user_form_step2" commandName="user" id="add_user_form_step2" cssClass="registration formPanel" action="${newUserStep3}">
        <div id="step2" class="wizard_box small" style="display: block;">
            <div class="wizard_box_top small">
                <div class="wizardbox_maintabbox">
                    <spring:message code="label.newUserStep2.credentials"/></div>
                    <div class="user_wizard step2">
                        <span class="steps1text"><spring:message code="label.newUserStep1.addUser"/></span>
                        <span class="steps2text"><spring:message code="label.newUserStep2.credentials"/></span>
                        <span class="steps3text"><spring:message code="label.newUserStep2.customize"/></span>
                    </div>
             </div>
         </div>
         <div class="wizard_box_bot small">
               <div class="registration_formbox">                       
                    <ul>
                        <li>
                          <label  for="user.username"><spring:message code="label.newUserStep2.username"/></label>
                          <div class="mandatory_wrapper">
                            <spring:message code="label.userInfo.username.tooltip" var="i18nUsernameTooltip"/>
                            <input class="text" type="text" tabindex="107" maxlength="64" title="<c:out value="${i18nUsernameTooltip}"/>" name="user.username" id="user.username"/>
                          </div>
                            
                        <div class="registration_formbox_errormsg" id="user.usernameError"><form:errors path="user.username"></form:errors></div>
                        </li>
                        <li>
                          <form:label path="clearPassword" ><spring:message code="label.newUserStep2.password"/></form:label>
                          <div class="mandatory_wrapper">
                            <spring:message code="label.userInfo.password.tooltip" var="i18nPasswordTooltip"/>
                            <form:password cssClass="text" tabindex="108" path="clearPassword" autocomplete="off" title="${i18nPasswordTooltip}"/>
                          </div>
                          
                          <div class="registration_formbox_errormsg" id="clearPasswordError"><form:errors path="clearPassword"></form:errors></div>
                        </li>
                        
                        <li>
                          <label for="passwordconfirm" ><spring:message code="label.newUserStep2.confirmPassword"/></label>
                          <div class="mandatory_wrapper">
                            <spring:message code="label.userInfo.password.confirm.tooltip" var="i18nConfirmPasswordTooltip"/>
                            <input class="text" tabindex="109" id="passwordconfirm" type="password" name="passwordconfirm" autocomplete="off" title="<c:out value="${i18nConfirmPasswordTooltip}"/>"/>
                          </div>
                          <div class="registration_formbox_errormsg" id="passwordconfirmError"></div>
                        </li>
                    </ul>
    
            </div>
            <div class="commonbox_submitbuttonpanel">
                  <div class="commonbox_submitbuttonbox position_right">
                  <spring:url value="/portal/users/new/step1" var="cancel_path">
                  	<spring:param name="tenant"><c:out value="${effectiveTenant.param}"/></spring:param>
                  </spring:url>
                      <p><a href="<c:out value="${cancel_path}"/>"><spring:message code="label.newUserStep2.cancel"/></a></p> 
                       <input class="commonbutton" tabindex="221" name="submitButtonFinish" type="submit" value="<spring:message code="label.newUserStep2.finish"/>"/>   
                       <p><spring:message code="label.newUserStep2.or"/></p>
                       <input class="commonbutton" tabindex="222" name="submitButtonEmail" type="submit" value="<spring:message code="label.newUserStep2.customize"/>"/>   
                  </div>
            </div>
      </div>
		  <!--Step 1 ends here-->
      </form:form>
                        
            
            
 

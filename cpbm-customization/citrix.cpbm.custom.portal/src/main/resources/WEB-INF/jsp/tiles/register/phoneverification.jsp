<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<script type="text/javascript">
$(document).ready(function() {
    $("#user\\.phone").focus();
});
  var phoneVerificationEnabled = true;
  var phoneVerified = false;

  dictionary = {
    phoneVerificationPinMandatory : '<spring:message javaScriptEscape="true" code="js.errors.register.phonePin"/>',
    phoneVerificationFailed : '<spring:message code="label.phoneVerification.unverified"/>'
  };
</script>
<tiles:insertAttribute name="pageHeader" />

            	<div class="login_headerarea">
                	<div class="login_headerarea_left">
                    	<%@ include file="../shared/channel_logo.jsp" %>
                    </div>
                    <div class="login_headerarea_right">
                      <div class="login_wizard step3of4">
                        <span class="steps1text"><spring:message code="label.accountSignUp.step.basicinfo"/></span>
                        <span class="steps2text"><spring:message code="label.accountSignUp.step.accountinfo"/></span>
                        <span class="steps3text"><spring:message code="label.accountSignUp.step.telephoneverification"/></span>
                        <span class="steps4text"><spring:message code="label.accountSignUp.step.success"/></span>
                      </div>
                    </div>
                </div>
                <div class="login_maincontentarea">
                	<div class="login_maincontentarea_titlepanel">
                    	<h1><spring:message code="ui.label.phoneverification.dialog.title"/></h1>
                        <p><span><spring:message code="label.moreUserInfo.asterix"/></span><spring:message code="label.moreUserInfo.mandatory"/></p>
                    </div>
                    
                    <div class="registration_formbox">
                    	<spring:url value="/portal/register" var="register" htmlEscape="false" />
                    	<form:form name="registration" commandName="registration" id="registrationForm" cssClass="registration formPanel" action="${register}" method="post" >
                    	<c:if test="${not empty errormsg || not empty registrationError}">
							<div class="registration_formbox_errormsg" id="error_success" >
								<span><spring:message code="${registrationError}" /></span>
								<c:forEach var="field" items="${errorMsgList}">
									<span><c:out value="${field}" escapeXml="false"/></span><br></br>
								</c:forEach>
							</div>
						</c:if> 
                            <div class="phoneverification_wrapper">
                        	<div class="phoneverification_titlebox">
                            	<div class="phone_vericon"></div>
                                <h2><spring:message code="label.phoneVerification.title.identity.verification.by.telephone"/></h2>
                                <p><spring:message code="label.phoneVerification.help.text"/></p>
                            </div>
                            <ul>
                            	<li>
                                	<label for="user.address.country"><spring:message code="label.moreUserInfo.country"/></label>
                                	<form:label  path="countryName" cssStyle="font-weight: bold; text-align: left;margin-left:15px;"><c:out value="${registration.countryName}" /></form:label>
                               </li>                               
                               <li>
                                	<form:label path="user.phone"><spring:message code="label.moreUserInfo.phoneNumber"/></form:label>
                                    <div class="red_compulsoryicon">*</div><div class="commonboxes_formbox_withouttextbox_plus_sign" ></div>
                                    <label  id="countryCode" style="font-weight: bold; text-align: left;margin-left:15px;width:auto">+<c:out value="${registration.countryCode}" /></label>
                                    <c:set var="title_i18n_phone"><spring:message code="label.moreUserInfo.phoneNumber"/></c:set>
                                    <form:input cssClass="text" tabindex="110" path="user.phone" title="${title_i18n_phone}" cssStyle="width:175px"/>
                                    <div id="user.phoneError" class="registration_formbox_errormsg"></div>
							   </li>
                                
                            </ul>
                        	<div class="callme_submitpanel">
                            	<div class="callme_submitbox">
                                	<a id="phoneVerificationCall" class="callme_button" tabindex="120" href="#">
                                        <span class="call_icon"></span>
                                        <spring:message code="label.phoneVerification.callMe"/>
                                    </a>
                                    <a id="phoneVerificationSMS" class="callme_button" tabindex="130" href="#" style="margin-left:17px;">
                                        <span class="text_icon"></span>
                                        <spring:message code="label.phoneVerification.textMe"/>
                                    </a>
                                </div>
                            </div>
                            
<!--                              <h3>In the next few seconds, you will receive an SMS message that contains a PIN code. Enter this PIN code below to verify your telephone number. </h3> -->
                            
                        	<ul>
                            	<li>
                                    <form:label path="userEnteredPhoneVerificationPin"><spring:message code="label.moreUserInfo.phoneVerificationPin"/></form:label>
                                    <div class="red_compulsoryicon">*</div> 
                                    <c:set var="title_i18n_phoneVerificationPin"><spring:message code="label.moreUserInfo.phoneVerificationPin"/></c:set>
                                    <form:input cssClass="text" tabindex="140" path="userEnteredPhoneVerificationPin" title="${title_i18n_phoneVerificationPin}"/>
                                    <div class="registration_formbox_errormsg" id="userEnteredPhoneVerificationPinError"></div>	
                                   	<div class="phonever_statusbox" id="verificationStatusSuccess" style="display:none">
                                        <div class="phonever_statusicon verified"></div>
                                        <span class="verified"><spring:message code="label.phoneVerification.verified"/></span>
                                    </div>
                                   	<div class="phonever_statusbox" id="verificationStatusFailed" style="display:none">
                                        <span class="unverified"></span>
                                    </div>                                    
                                </li>
                            </ul>
                               
                       		<div class="phoneverification_botbg"></div>         
                      </div>
                        </form:form>
                        
                        <div class="login_formbox_submitpanel">
                        	<div class="register_buttonscontainer">
                            	<a id="registrationSubmit" class="logincommonbutton" href="#" tabindex="200" ><spring:message code="label.moreUserInfo.submit"/></a>
                            </div>
                        	
                        </div>
                    </div>
                
                
                </div>

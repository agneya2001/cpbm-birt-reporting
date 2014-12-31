<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ page import="net.tanesha.recaptcha.ReCaptcha" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<tiles:insertAttribute name="pageHeader" />
<script type="text/javascript">
var trialAcountSelected = "<c:out value='${trialAcountSelected}'/>";
</script>
<c:if test="${ThreatMetrixEnabled == 'True'}">
<iframe style="color:rgb(0,0,0);float:left; position:absolute; top:-200px; left:-200px; border:0px"
        src="https://<%= request.getSession().getAttribute("fraudProfilingHost") %>/tags?org_id=<%= request.getSession().getAttribute("fraudOrgid") %>&session_id=<%= request.getSession().getId() %>"
        height=100
        width=100>
</iframe>
</c:if>
<c:choose>
  <c:when test="${! empty signupwarningmessage}">
     <div class="common_messagebox success">
          <p><c:out value="${signupwarningmessage}"></c:out> </p>
      </div>
  </c:when>
  <c:otherwise>

            	<div class="login_headerarea">
                	<div class="login_headerarea_left">
                    	<%@ include file="../shared/channel_logo.jsp" %>
                    </div>
                    <div class="login_headerarea_right">
                   		<c:choose>
                   			<c:when test="${registration.phoneVerificationEnabled}">
                          <div class="login_wizard step1of4">
                            <span class="steps1text"><spring:message code="label.accountSignUp.step.basicinfo"/></span>
                            <span class="steps2text"><spring:message code="label.accountSignUp.step.accountinfo"/></span>
                            <span class="steps3text"><spring:message code="label.accountSignUp.step.telephoneverification"/></span>
                            <span class="steps4text"><spring:message code="label.accountSignUp.step.success"/></span>
                          </div>
                   			</c:when>
                   			<c:otherwise>
                          <div class="login_wizard step1of3">
                            <span class="steps1text"><spring:message code="label.accountSignUp.step.basicinfo"/></span>
                            <span class="steps2text"><spring:message code="label.accountSignUp.step.accountinfo"/></span>
                            <span class="steps3text"><spring:message code="label.accountSignUp.step.success"/></span>
                          </div>
                   			</c:otherwise>
                   		</c:choose>                     	
                    </div>
                </div>
                <div class="login_maincontentarea">
                	<div class="login_maincontentarea_titlepanel">
                    	<h1><spring:message code="label.userInfo.title"/></h1>
                        <p><span><spring:message code="label.userInfo.asterix"/></span><spring:message code="label.userInfo.mandatory"/></p>
                    </div>
                    <c:if test="${signuperror != null && signuperror=='emaildomainblacklisted'}">
                          <div class="common_messagebox success"><p><spring:message code="signup.emaildomain.blacklist.error"/></p></div>
                    </c:if>
                    <c:if test="${signuperror != null && signuperror=='devicefrauddetected'}">
                          <div class="common_messagebox success"><p><spring:message code="signup.integration.device.fraud.error"/></p></div>
                    </c:if>
                    <div class="registration_formbox">
                    	<spring:url value="/portal/user_info" var="userinfo" htmlEscape="false" />
                    	<form:form name="registration" commandName="registration" id="registrationForm" cssClass="registration formPanel" action="${userinfo}">
                            <ul>
                           
                              <div class="user_name_order">
                                <div class="user_first_name">
                            	<li>
                                	<form:label path="user.firstName"><spring:message code="label.userInfo.firstName"/></form:label><div class="red_compulsoryicon"><spring:message code="label.userInfo.asterix"/></div>
                                    <spring:message code="label.userInfo.firstName.tooltip" var="i18nFirstNameTooltip"/>
                                    <form:input cssClass="text" tabindex="101" path="user.firstName" title="${i18nFirstNameTooltip}" maxlength="255"/>
                                    <div class="registration_formbox_errormsg" id="user.firstNameError"></div>
                                
                                </li>
                                </div>
                                <div class="user_last_name">
								<li>
                                	<form:label path="user.lastName"><spring:message code="label.userInfo.lastName"/></form:label><div class="red_compulsoryicon"><spring:message code="label.userInfo.asterix"/></div>
                                    <spring:message code="label.userInfo.lastName.tooltip" var="i18nLastNameTooltip"/>
                                    <form:input cssClass="text" tabindex="102" path="user.lastName" title="${i18nLastNameTooltip}" maxlength="255"/>
                                    <div class="registration_formbox_errormsg" id="user.lastNameError"></div>
                                
                                </li>  
                                </div>
                                </div>                              
                                <li>
                                	<form:label path="user.email" ><spring:message code="label.userInfo.yourEmail"/></form:label><div class="red_compulsoryicon"><spring:message code="label.userInfo.asterix"/></div>
                                    <spring:message code="label.userInfo.yourEmail.tooltip" var="i18nEmailTooltip"/>
                                    <form:input  cssClass="text" tabindex="106" path="user.email" title="${i18nEmailTooltip}" maxlength="255"/>
                                	<div class="registration_formbox_errormsg" id="user.emailError"></div>
                                </li>
                                
                                <li>
                                	<label  for="confirmEmail"><spring:message code="label.userInfo.confirmEmail"/></label><div class="red_compulsoryicon"><spring:message code="label.userInfo.asterix"/></div>
                                    <input class="text" type="text" value="<c:out value="${registration.user.email}" />" tabindex="107" title="<spring:message code="label.userInfo.confirmEmail.tooltip"/>" name="confirmEmail" id="confirmEmail"/>
                                    <div class="registration_formbox_errormsg" id="confirmEmailError"></div>
                                
                                </li>
                                <li>
                                	<label  for="user.username"><spring:message code="label.userInfo.username"/></label><div class="red_compulsoryicon"><spring:message code="label.userInfo.asterix"/></div>
                                    <input class="text" type="text" value="<c:out value="${registration.user.username}" />" tabindex="107" title="<spring:message code="label.userInfo.username.tooltip"/>" name="user.username" id="user.username" maxlength="64"/>
                                    <div class="registration_formbox_errormsg" id="user.usernameError"></div>
                                
                                </li>
                                                                
                                <li id="promoCode">
                                  <label for="trialCode" ><spring:message code="label.userInfo.promo.code"/></label>
                                  
                                  <c:if test="${trialAcountSelected == true}">
                                    <div class="red_compulsoryicon"><spring:message code="label.userInfo.asterix"/></div>
                                  </c:if>
                                  <c:if test="${trialAcountSelected != true}">
                                    <div class="red_compulsoryicon">&nbsp;</div>
                                  </c:if>
                                  <spring:message code="label.userInfo.promo.code.tooltip" var="i18nPromoTooltip"/>
                                  <form:input  cssClass="text" tabindex="110" path="trialCode"
                                               title="${i18nPromoTooltip}" value="${promoCode}"/>
                                  <div class="registration_formbox_errormsg" id="trialCodeError"></div>
                                </li>                            
                               <li>
                                 <form:label path="user.locale" ><spring:message code="label.newUserStep1.locale"/></form:label>
                                 <div class="red_compulsoryicon"><spring:message code="label.userInfo.asterix"/></div>
                                   <spring:message code="label.language.tooltip" var="i18nLanguageTooltip"/>
                                   <form:select cssClass="text" tabindex="111" path="user.locale" title="${i18nLanguageTooltip}">
                                     <c:set var="userLoc" value="${user.locale}"/> 
                                      <c:if test="${empty userLoc}">
                                    <!-- This will get set when server side validation fails -->
                                          <c:set var="userLoc" value="${registration.user.locale}"/>
                                        </c:if>
                                        <c:if test="${empty userLoc}">
                                    <!-- This will take locale from request -->
                                          <c:set var="userLoc" value="${currentLocale}"></c:set>
                                        </c:if>
                                        <c:if test="${empty userLoc}">
                                    <!-- This will get set when client side validation fails -->
                                          <c:set var="userLoc" value="${defaultLocale}"></c:set>
                                        </c:if>
                                      <c:forEach items="${supportedLocaleList}" var="locale" varStatus="status">
                                        <option value='<c:out value="${locale.key}" />' <c:if test="${locale.key == userLoc}">selected="selected"</c:if> >
                                          <c:out value="${locale.value}"></c:out>
                                        </option> 
                                       
                                    </c:forEach>
                                </form:select>                               
                               </li>
                                <li>
                                   <form:label path="currency" ><spring:message code="label.moreUserInfo.currency"/></form:label>  <div class="red_compulsoryicon">*</div>                   
                                   <spring:message code="label.moreUserInfo.currency.tooltip" var="i18ncurrencyTooltip"/>
                                    <form:select path="currency"  cssClass="text" tabindex="112" title="${i18ncurrencyTooltip}">
                                    <c:forEach var="currency" items="${registration.currencyValueList}" varStatus="status">
                                        <option value="<c:out value="${currency.currencyCode}" ></c:out>" <c:if test="${registration.currency == currency.currencyCode}" >selected="selected" </c:if>><spring:message code="currency.longname.${currency.currencyCode}"></spring:message></option>
                                    </c:forEach>
                                  </form:select>
                                   <div class="registration_formbox_errormsg" id="currencyError"></div>                       
                                </li>
                                
                        <tiles:insertDefinition name="user.custom.fields"></tiles:insertDefinition>
                            </ul>
                        </form:form>
                        
                        <div class="login_formbox_submitpanel">
                        	<div class="register_buttonscontainer">
                            	<a tabindex="113" id="continuebutton" class="logincommonbutton" href="#"><spring:message code="label.userInfo.continue"/></a>
                            </div>
                        	
                        </div>
                    </div>
                
                
                </div>
</c:otherwise>
</c:choose>

<script type="text/javascript">
  swap_name_order_tab_index("user_first_name", "user_last_name");
  $(function(){
	  if(parseInt($("#user\\.lastName").attr("tabindex")) < parseInt($("#user\\.firstName").attr("tabindex"))){
		  $("#user\\.lastName").focus();
	  }else{
		  $("#user\\.firstName").focus();
	  }
	    
	});
</script>
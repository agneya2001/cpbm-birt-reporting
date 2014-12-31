<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<script type="text/javascript">

var dictionary = { 
			
};

</script>



        <div class="maintitlebox">
            <h1><spring:message code="label.newUserStep1.heading"/></h1>
        </div>
        <c:if test="${signuperror != null && signuperror=='emaildomainblacklisted'}">
          <div class="common_messagebox success"><p><spring:message code="signup.emaildomain.blacklist.error"/></p></div>
        </c:if>
       <spring:url value="/portal/users/new/step2" var="newUserStep2" htmlEscape="false" />
        <form:form name="add_user_form_step1" commandName="user" id="add_user_form_step1" cssClass="registration formPanel" action="${newUserStep2}">
        <!--Step 1 starts here-->
        <div id="step1" class="wizard_box small" style="display: block;">
            <div class="wizard_box_top small">
                <div class="wizardbox_maintabbox">
                    <spring:message code="label.newUserStep1.addUser"/></div>
                    <div class="user_wizard step1">
                        <span class="steps1text"><spring:message code="label.newUserStep1.addUser"/></span>
                        <span class="steps2text"><spring:message code="label.newUserStep2.credentials"/></span>
                        <span class="steps3text"><spring:message code="label.newUserStep2.customize"/></span>
                    </div>
                </div>
            </div>
            <div class="wizard_box_bot small">
              <div class="registration_formbox">
                            <ul>
                                <li style="margin-top: 45px;">
                                  <form:label path="user.email"><spring:message code="label.newUserStep1.email"/></form:label>
                                    <div class="mandatory_wrapper">
                                      <spring:message code="label.userInfo.yourEmail.tooltip" var="i18nEmailTooltip"/>
                                      <form:input  cssClass="text" tabindex="106" path="user.email" title="${i18nEmailTooltip}"/>
                                    </div>
                                    <div class="registration_formbox_errormsg" id="user.emailError"><form:errors path="user.email"></form:errors></div>
                                    
                                </li>
                                
                                <li style="margin-top: 20px;">
                                  <form:label path="user.firstName"><spring:message code="label.newUserStep1.firstName"/></form:label>
                                  <div class="mandatory_wrapper">
                                    <spring:message code="label.userInfo.firstName.tooltip" var="i18nFirstNameTooltip"/>
                                    <form:input  cssClass="text" tabindex="106" path="user.firstName" title="${i18nFirstNameTooltip}"/>
                                   </div>
                                  <div class="registration_formbox_errormsg" id="user.firstNameError"><form:errors path="user.firstName"></form:errors></div>
                                </li>
                                
                                <li style="margin-top: 20px;">
                                  <form:label path="user.lastName"><spring:message code="label.newUserStep1.lastName"/></form:label>
                                  <div class="mandatory_wrapper">
                                    <spring:message code="label.userInfo.lastName.tooltip" var="i18nLastNameTooltip"/>
                                    <form:input  cssClass="text" tabindex="106" path="user.lastName" title="${i18nLastNameTooltip}"/>
                                   </div>
                                  <div class="registration_formbox_errormsg" id="user.lastNameError"><form:errors path="user.lastName"></form:errors></div>
                                </li>
                                    <%--Added by amit for single step registration as password is set on emailverification --%>
                          <li>
                          <label  for="user.username"><spring:message code="label.newUserStep2.username"/></label>
                          <div class="mandatory_wrapper">
                            <spring:message code="label.userInfo.username.tooltip" var="i18nUsernameTooltip"/>
                            <input class="text" type="text" tabindex="107" title="<c:out value="${i18nUsernameTooltip}"/>" name="user.username" id="user.username"/>
                          </div>
                           <div class="registration_formbox_errormsg" id="user.usernameError"><form:errors path="user.username"></form:errors></div>
                        </li>
                                 
                                <c:if test="${displayChannel}">
                                  <li>
                                   <form:label path="channelParam" ><spring:message code="label.newUserStep1.channel"/> </form:label>
                                   <div class="mandatory_wrapper">
                                     <spring:message code="label.channel.tooltip" var="i18nChannelTooltip"/>
                                     <form:select cssClass="text" tabindex="208" path="channelParam" title="${i18nChannelTooltip}">
                                        <c:forEach items="${channels}" var="choice" varStatus="status">
                                          <form:option value="${choice.param}"><c:out value="${choice.name}"/></form:option> 
                                        </c:forEach>
                                     </form:select>                 
                                   </div>   
                                   <div class="registration_formbox_errormsg" id="channelParamError"></div>
                                  </li>       
                                </c:if>                        
                                <li>
                                 <form:label path="userProfile" ><spring:message code="label.newUserStep1.profile"/></form:label>
                                 <div class="mandatory_wrapper">
                                   <spring:message code="label.profile.tooltip" var="i18nProfileTooltip"/>
                                   <form:select cssClass="text" tabindex="208" path="userProfile" title="${i18nProfileTooltip}">
                                      <form:option value=""><spring:message code="label.myprofile.choose"/></form:option>
                                      <c:forEach items="${user.validProfiles}" var="choice" varStatus="status">
                                        <form:option value="${choice.id}"><c:out value="${choice.name}"/></form:option> 
                                      </c:forEach>
                                   </form:select>                 
                                 </div>   
                                        <div class="registration_formbox_errormsg" id="userProfile.error"><form:errors path="userProfile"></form:errors></div>
                                                               
                                </li>                                

                          	    <li>
                                 <form:label path="timeZone" ><spring:message code="label.newUserStep1.timezone"/></form:label>
                                 <div class="nonmandatory_wrapper">
                                   <spring:message code="label.timezone.tooltip" var="i18nTimezoneTooltip"/>
                                   <form:select cssClass="text" tabindex="208" path="timeZone" title="${i18nTimezoneTooltip}">
                                      <form:option value=""><spring:message code="label.myprofile.choose"/></form:option>
                                      <c:forEach items="${user.timeZones}" var="choice" varStatus="status">
                                        <option value='<c:out value="${choice.value}" />' <c:if test="${choice.value == user.timeZone}">selected="selected"</c:if>>
                                          <c:out value="${choice.key}"/>
                                        </option> 
                                    </c:forEach>
                                   </form:select>
                                 </div>
                                 	<div class="registration_formbox_errormsg" id="timeZone.error"></div>
                               </li>
                               <li>
                                 <form:label path="userLocale" ><spring:message code="label.newUserStep1.locale"/></form:label>
                                   <spring:message code="label.language.tooltip" var="i18nLanguageTooltip"/>
                                   <form:select cssClass="text" tabindex="208" path="userLocale" title="${i18nLanguageTooltip}">
                                     <c:set var="userLoc" value="${user.userLocale}"/> 
                                      <c:if test="${empty userLoc}">
                                          <c:set var="userLoc" value="${defaultLocale}"/>
                                        </c:if>
                                      <c:forEach items="${supportedLocaleList}" var="locale" varStatus="status">
                                        <option value='<c:out value="${locale.key}" />' <c:if test="${locale.key == userLoc}">selected="selected"</c:if> >
                                          <c:out value="${locale.value}"></c:out>
                                        </option> 
                                       
                                    </c:forEach>
                                </form:select>                               
                               </li>
                               <tiles:insertDefinition name="user.custom.fields"></tiles:insertDefinition>
                                                    </ul>
		                        
              </div>
              
            
              <div class="commonbox_submitbuttonpanel" >
		                          <div class="commonbox_submitbuttonbox position_right">
		                               <p><a href="<%=request.getContextPath() %>/portal/home"><spring:message code="label.newUserStep1.cancel"/></a></p> 
		                               <p></p> 
                                    <input class="commonbutton" tabindex="221" name="submitButtonFinish" type="submit" value="<spring:message code="label.newUserStep2.finish"/>"/>   
                                    <p>or</p>
                                    <input class="commonbutton" tabindex="222" name="submitButtonEmail" type="submit" value="<spring:message code="label.newUserStep2.customize"/>"/>   
                                     
		                          </div>
		                        </div>
            </div>
        <!--Step 1 ends here-->
        </form:form>

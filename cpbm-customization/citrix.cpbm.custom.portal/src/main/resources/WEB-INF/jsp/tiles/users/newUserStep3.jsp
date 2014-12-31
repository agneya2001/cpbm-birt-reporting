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
            <h1><spring:message code="label.newUserStep3.heading"/></h1>
        </div>
        <spring:url value="/portal/users/new/step3" var="newUserStep4" htmlEscape="false" />
        <form:form name="add_user_form_step3" commandName="user" id="add_user_form_step3" cssClass="registration formPanel" action="${newUserStep4}">
        <div id="step3" class="wizard_box small" style="display: block;">
            <div class="wizard_box_top small">
                <div class="wizardbox_maintabbox">
                    <spring:message code="label.newUserStep3.customize"/></div>
                    <div class="user_wizard step3">
                        <span class="steps1text"><spring:message code="label.newUserStep1.addUser"/></span>
                        <span class="steps2text"><spring:message code="label.newUserStep2.credentials"/></span>
                        <span class="steps3text"><spring:message code="label.newUserStep2.customize"/></span>
                    </div>
             </div>
         </div>
         <div class="wizard_box_bot small">
              <div class="registration_formbox">
              <c:out value="${emailText}" escapeXml="false"/>
              <!--  
                <ul>
                    <li style="margin: 45px 0px 0px 15px;">Custom welcoming email subject and text
                    </li>
                    <li>                                  
                        <form:input  cssStyle="width:500px;" cssClass="text" tabindex="107" path="customEmailSubject"/>
                        <div class="registration_formbox_errormsg" id="user.email.subjectError"></div>                                
                   </li>
                    <li>                                  
                      <form:textarea  cssStyle="height:300px; width:500px;" cssClass="text" tabindex="108" path="emailText"/>
                      <div class="registration_formbox_errormsg" id="emailText"></div>
                    </li>
                </ul> 
                -->
              </div>
              <div class="commonbox_submitbuttonpanel">
                  <div class="commonbox_submitbuttonbox position_right">
                      <spring:url value="/portal/users/new/step1" var="cancel_path">
                        <spring:param name="tenant"><c:out value="${effectiveTenant.param}"/></spring:param>
                      </spring:url>
                      <p><a href="<c:out value="${cancel_path}"/>"><spring:message code="label.newUserStep3.cancel"/></a></p> 
                       <input class="commonbutton" tabindex="221" name="submit" type="submit" value="<spring:message code="label.newUserStep3.finish"/>"/>    
                  </div>
                </div>
         </div>
         </form:form>


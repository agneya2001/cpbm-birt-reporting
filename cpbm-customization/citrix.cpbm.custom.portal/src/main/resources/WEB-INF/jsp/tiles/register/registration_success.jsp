<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

              <div class="login_headerarea">
                  <div class="login_headerarea_left">
                      <%@ include file="../shared/channel_logo.jsp" %>
                    </div>
                    <div class="login_headerarea_right">
                   		<c:choose>
                   			<c:when test="${registration.phoneVerificationEnabled}">
                          <div class="login_wizard step4of4">
                            <span class="steps1text"><spring:message code="label.accountSignUp.step.basicinfo"/></span>
                            <span class="steps2text"><spring:message code="label.accountSignUp.step.accountinfo"/></span>
                            <span class="steps3text"><spring:message code="label.accountSignUp.step.telephoneverification"/></span>
                            <span class="steps4text"><spring:message code="label.accountSignUp.step.success"/></span>
                          </div>
                   			</c:when>
                   			<c:otherwise>
                          <div class="login_wizard step3of3">
                            <span class="steps1text"><spring:message code="label.accountSignUp.step.basicinfo"/></span>
                            <span class="steps2text"><spring:message code="label.accountSignUp.step.accountinfo"/></span>
                            <span class="steps3text"><spring:message code="label.accountSignUp.step.success"/></span>
                          </div>
                   			</c:otherwise>
                   		</c:choose>                    	
                    </div>                   
                </div>

<div class="order-now" style="margin:30px 0 0 80px; display:inline;  height:267px; float:left;">  
		  <div class="message regsuccess" id="message_success" >
	    <span></span>
	    <div class="registrationSuccess">
        <h3>
          <spring:message code="label.registration.success.account.created.thankYou" arguments="${user.firstName}, ${user.lastName}" />
          <br></br>
          <spring:message code="label.registration.success.account.created"/>
        </h3>
	    </div> 
	    <br>
	    <p>
      <c:choose>
      <c:when test ="${tenant.accountType.manualActivation}">
       <spring:message code="label.registration.success.account.created.confirmation.manual.activation"/>
      </c:when>
      <c:when test ="${deviceFraudDetected}">
       <spring:message code="label.registration.success.account.created.confirmation.device.fraud.detected"/>
      </c:when>
      <c:otherwise>
       <spring:message code="label.registration.success.account.created.confirmation"/>
      </c:otherwise>
     
      </c:choose>
      
      <c:if test ="${tenant.trialAccount != NULL}">
        <c:if test ="${tenant.trialAccount.expiryDate != NULL}">
            <spring:message code="date.format" var="date_format"/>
          <fmt:formatDate value="${tenant.trialAccount.expiryDate}" pattern="${date_format}" timeZone="${currentUser.timeZone}" var="formatted_date"/>
          
            <spring:message code="label.registration.success.trial.account.created.confirmation" arguments="${formatted_date}"/>
        </c:if>      
      </c:if>
      
      </p>
		</div>
</div>


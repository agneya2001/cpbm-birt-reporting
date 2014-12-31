<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<script type="text/javascript">
$(function(){
  $("#user\\.address\\.country").focus();
});
</script>
<tiles:insertAttribute name="pageHeader" />

            	<div class="login_headerarea">
                	<div class="login_headerarea_left">
                    	<%@ include file="../shared/channel_logo.jsp" %>
                    </div>
                    <div class="login_headerarea_right">
                   		<c:choose>
                   			<c:when test="${registration.phoneVerificationEnabled}">
                          <div class="login_wizard step2of4">
                            <span class="steps1text"><spring:message code="label.accountSignUp.step.basicinfo"/></span>
                            <span class="steps2text"><spring:message code="label.accountSignUp.step.accountinfo"/></span>
                            <span class="steps3text"><spring:message code="label.accountSignUp.step.telephoneverification"/></span>
                            <span class="steps4text"><spring:message code="label.accountSignUp.step.success"/></span>
                          </div>
                   			</c:when>
                   			<c:otherwise>
                          <div class="login_wizard step2of3">
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
                    	<h1><spring:message code="label.moreUserInfo.contact"/></h1>
                        <p><span><spring:message code="label.moreUserInfo.asterix"/></span><spring:message code="label.moreUserInfo.mandatory"/></p>
                    </div>
                    
                    <div class="registration_formbox">
                    <c:choose>
	                    <c:when test="${registration.phoneVerificationEnabled}">
	                    	<spring:url value="/portal/phone_verification" var="register" htmlEscape="false" />
	                    </c:when>
	                    <c:otherwise>
	                    	<spring:url value="/portal/register" var="register" htmlEscape="false" />
	                    </c:otherwise>
                    </c:choose>
                    	<form:form name="registration" commandName="registration" id="registrationForm" cssClass="registration formPanel" action="${register}">
                    	<c:if test="${not empty errormsg || not empty registrationError}">
							<div class="registration_formbox_errormsg extra_margin" id="error_success" >
								<span><spring:message code="${registrationError}" /></span>
								<c:forEach var="field" items="${errorMsgList}">
									<span><c:out value="${field}" escapeXml="false"/></span><br></br>
								</c:forEach>
								<%-- <spring:hasBindErrors name="registration">
								<c:forEach var="error" items="${errors.allErrors}">
								<span><spring:message code="${error.code}" arguments="${error.arguments}"/></span><br></br>
								</c:forEach>
								</spring:hasBindErrors> --%>
							</div>
						</c:if> 
                            <ul>
                            	<li>
                                	<form:label path="user.firstName"><spring:message code="label.moreUserInfo.yourName"/></form:label><div class="red_compulsoryicon"></div>
                                    <spring:message code="label.moreUserInfo.name.tooltip" var="i18nNameTooltip"/>
                                    <form:label path="user.firstName" title="${i18nNameTooltip}" cssStyle="font-weight: bold; text-align: left;margin-left:15px"><c:out value="${registration.user.firstName}"/> <c:out value="${registration.user.lastName}"/></form:label>
                                </li>
                                
                                  <li>
                                	<form:label path="user.address.country" ><spring:message code="label.moreUserInfo.country"/></form:label><div class="red_compulsoryicon">*</div>
		             				<spring:message code="label.moreUserInfo.country.tooltip" var="i18nCountryTooltip"/>
                        <form:select cssClass="select" cssStyle="width:254px;" tabindex="2080" path="user.address.country" title="${i18nCountryTooltip}">
		                  			 	<option value=""><spring:message code="label.choose"/></option>
		                  			 	<c:forEach items="${filteredCountryList}" var="choice" varStatus="status">
		                     				<option value="<c:out value="${choice.countryCode2}"/>" <c:if test="${ipToCountryCode == choice.countryCode2}" >selected="selected" </c:if>><c:out value="${choice.name}" escapeXml="false"/></option> 
		                   				</c:forEach>
	                 				</form:select>              			
                                	<div class="registration_formbox_errormsg" id="user.address.countryError"></div>
                                </li>
                                <li>
							   	 	<div id="stateInput">
					      			 	<form:label path="user.address.state" ><spring:message code="label.moreUserInfo.state"/></form:label><div class="red_compulsoryicon">*</div>                			
                      <spring:message code="label.moreUserInfo.state.tooltip" var="i18nStateTooltip"/>
                      <form:input cssClass="text" tabindex="2090" path="user.address.state" title="${i18nStateTooltip}" maxlength="255"/>
							   	 	</div> 
							   	 	 <div id="stateSelect" style="display:none">
					                    <div id="otherstateDiv" style="display:none">
					                    <form:label path="user.address.state" ><spring:message code="label.moreUserInfo.state"/></form:label>
					                    </div>
					                    <div id="JPstateDiv">
					                    <form:label path="user.address.state" ><spring:message code="label.moreUserInfo.state.jp"/></form:label>
					                    </div>
					                        <div class="red_compulsoryicon">*</div>                     
                                        <select class="text" tabindex="2100" id="userAddressStateSelect" name="userAddressStateSelect" title="<spring:message code="label.moreUserInfo.state.tooltip"/>"></select>
					                    </div>  
                                	<div class="registration_formbox_errormsg" id="user.address.stateError"></div>							   	 	
                                  <div class="registration_formbox_errormsg" id="userAddressStateSelectError"></div>                                 
                                </li>
                                
                                <li>
			          				<form:label path="user.address.street1" ><spring:message code="label.moreUserInfo.address1"/></form:label><div class="red_compulsoryicon">*</div>               			
			         				<spring:message code="label.moreUserInfo.address1.tooltip" var="i18nAddress1Tooltip"/>
                      <form:input cssClass="text" tabindex="2110" path="user.address.street1" title="${i18nAddress1Tooltip}" maxlength="255"/>
                                	<div class="registration_formbox_errormsg" id="user.address.street1Error"></div>				         				
                                </li>
                                <li>
			          				<form:label path="user.address.street2" ><spring:message code="label.moreUserInfo.address2"/></form:label><div class="red_compulsoryicon"></div>              			
			         				<spring:message code="label.moreUserInfo.address2.tooltip" var="i18nAddress2Tooltip"/>
                      <form:input cssClass="text" tabindex="2120" path="user.address.street2" title="${i18nAddress2Tooltip}" maxlength="255"/>
                                	<div class="registration_formbox_errormsg" id="user.address.street2Error"></div>				         				
                                </li>                                
                                
                                 <li>
			          				<form:label path="user.address.city" ><spring:message code="label.moreUserInfo.city"/></form:label> <div class="red_compulsoryicon">*</div>              			
			         				<spring:message code="label.moreUserInfo.city.tooltip" var="i18nCityTooltip"/>
                      <form:input cssClass="text" tabindex="2130" path="user.address.city" title="${i18nCityTooltip}" maxlength="255"/>
                                	<div class="registration_formbox_errormsg" id="user.address.cityError"></div>				         				
                                </li>
                                
                                <li>
                        <form:label path="user.address.postalCode" ><spring:message code="label.moreUserInfo.zipCode"/></form:label>  <div class="red_compulsoryicon">*</div>                   
                      <spring:message code="label.moreUserInfo.zipCode" var="i18nZipCodeToolTip"/>
                      <form:input cssClass="text" tabindex="2140"  path="user.address.postalCode" title="${i18nZipCodeToolTip}" maxlength="25"/>
                                  <div class="registration_formbox_errormsg" id="user.address.postalCodeError"></div>                       
                                </li>  
                                
                                <li id="allowSecondaryLi" <c:if test="${!allowSecondaryCheckBox}">style="display:none"</c:if>>
                                  
                                 <label> <spring:message code="ui.accounts.all.header.add.secondary.address"/></label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<form:checkbox id="allowSecondaryId" path="allowSecondary" tabindex="2141"/>
                                </li> 
                                 <li id="syncAddressli">
                                        <label> <spring:message code="ui.accounts.all.header.add.secondary.syncaddress"/></label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="checkbox" class="checkbox " id="syncAddress" tabindex="2142" name="syncAddress">
                                </li> 
                           
                                <li id="billingAddressLabel">
                                        <b><form:label path="secondaryAddress.country"><spring:message code="ui.accounts.all.billing.address"/></form:label></b>
                                </li>
                                     
                                <li id="secondaryCountry">
                                  <form:label path="secondaryAddress.country" ><spring:message code="label.tenants.country"/></form:label><div class="red_compulsoryicon">*</div>
                        <spring:message code="label.moreUserInfo.country.tooltip" var="i18nCountryTooltip"/>
                        <form:select cssClass="text" tabindex="2143" path="secondaryAddress.country" title="${i18nCountryTooltip}">
                              <option value=""><spring:message code="label.choose"/></option>
                              <c:forEach items="${filteredCountryList}" var="choice" varStatus="status">
                                <option value="<c:out value="${choice.countryCode2}"/>" <c:if test="${ipToCountryCode == choice.countryCode2}" >selected="selected" </c:if>><c:out value="${choice.name}" escapeXml="false"/></option> 
                              </c:forEach>
                          </form:select>                    
                                  <div class="registration_formbox_errormsg" id="secondaryAddress.countryError"></div>
                                </li>
                                
                                <li id="secondaryState">
                    <div id="stateSecondaryInput">
                        <form:label path="secondaryAddress.state" ><spring:message code="label.tenants.state"/></form:label><div class="red_compulsoryicon">*</div>                      
                      <spring:message code="label.moreUserInfo.state.tooltip" var="i18nStateTooltip"/>
                      <form:input cssClass="text" tabindex="2144" path="secondaryAddress.state" title="${i18nStateTooltip}"/>
                    </div> 
                     <div  id="stateSecondarySelect" style="display:none">
                    <div id="otherstateSecondaryDiv" style="display:none">
                    <form:label path="secondaryAddress.state" ><spring:message code="label.moreUserInfo.state"/></form:label>
                    </div>
                    <div id="JPSecondarystateDiv">
                    <form:label path="secondaryAddress.state" ><spring:message code="label.moreUserInfo.state.jp"/></form:label>
                    </div>
                    
                        <div class="red_compulsoryicon">*</div>
                    <select class="select" tabindex="2145" id="tenantSecondaryAddressStateSelect" name="tenantSecondaryAddressStateSelect" title="<spring:message code="label.moreUserInfo.state.tooltip"/>"></select>
                    </div>
                                  <div class="registration_formbox_errormsg" id="secondaryAddress.stateError"></div>                    
                                </li>
                                
                                <li id="secondaryStreet1">
                        <form:label path="secondaryAddress.street1" ><spring:message code="label.tenants.address1"/></form:label><div class="red_compulsoryicon">*</div>                     
                      <spring:message code="label.moreUserInfo.address1.tooltip" var="i18nAddress1Tooltip"/>
                      <form:input cssClass="text" tabindex="2146" path="secondaryAddress.street1" title="${i18nAddress1Tooltip}"/>
                                  <div class="registration_formbox_errormsg" id="secondaryAddress.street1Error"></div>                        
                                </li>
                                <li id="secondaryStreet2">
                        <form:label path="secondaryAddress.street2" ><spring:message code="label.tenants.address2"/></form:label><div class="red_compulsoryicon"></div>                    
                      <spring:message code="label.moreUserInfo.address2.tooltip" var="i18nAddress2Tooltip"/>
                      <form:input cssClass="text" tabindex="2147" path="secondaryAddress.street2" title="${i18nAddress2Tooltip}"/>
                                  <div class="registration_formbox_errormsg" id="secondaryAddress.street2Error"></div>                        
                                </li>                                
                               
                                 <li id="secondaryCity">
                        <form:label path="secondaryAddress.city" ><spring:message code="label.tenants.city"/></form:label> <div class="red_compulsoryicon">*</div>                   
                      <spring:message code="label.moreUserInfo.city.tooltip" var="i18nCityTooltip"/>
                      <form:input cssClass="text" tabindex="2148" path="secondaryAddress.city" title="${i18nCityTooltip}"/>
                                  <div class="registration_formbox_errormsg" id="secondaryAddress.cityError"></div>                       
                                </li>
                                <li id="secondaryPostalCode">
                        <form:label path="secondaryAddress.postalCode" ><spring:message code="label.tenants.zipCode"/></form:label>  <div class="red_compulsoryicon">*</div>                  
                      <spring:message code="label.moreUserInfo.zipCode" var="i18nZipCodeToolTip"/>
                      <form:input cssClass="text" tabindex="2149" path="secondaryAddress.postalCode" title="${i18nZipCodeToolTip}"/>
                                  <div class="registration_formbox_errormsg" id="secondaryAddress.postalCodeError"></div>                       
                                </li>
                                
                                <c:if test="${!registration.phoneVerificationEnabled}">                            
	                                <li>
				          				<form:label path="user.phone" ><spring:message code="label.moreUserInfo.phoneNumber"/></form:label><div class="red_compulsoryicon">*</div><div class="commonboxes_formbox_withouttextbox_plus_sign" >+</div>           			
				         				<spring:message code="label.moreUserInfo.countryCode.tooltip" var="i18nCountryCodeTooltip"/>
                        <form:input cssClass="text" tabindex="2150" path="countryCode" title="${i18nCountryCodeTooltip}" cssStyle="width:60px"/>
                        <spring:message code="label.moreUserInfo.phoneNumber.tooltip" var="i18nPhoneTooltip"/>
				         				<form:input cssClass="text" tabindex="2151" path="user.phone" title="${i18nPhoneTooltip}" cssStyle="width:152px"/>
	                                	<div class="registration_formbox_errormsg" id="user.phoneError"></div>
	                                	<div class="registration_formbox_errormsg" id="countryCodeError"></div>			         				
	                                </li>  
                                </c:if> 
                                 
                                <li>
			          				<form:label path="tenant.name" ><spring:message code="label.moreUserInfo.companyName"/></form:label>  <div class="red_compulsoryicon">*</div>             			
			         				<spring:message code="label.moreUserInfo.companyName.tooltip" var="i18nCompanyNameTooltip"/>
                      <form:input cssClass="text" tabindex="2160" path="tenant.name" title="${i18nCompanyNameTooltip}" maxlength="128"/>
                                	<div class="registration_formbox_errormsg" id="tenant.nameError"></div>				         				
                                </li>
                                <tiles:insertDefinition name="tenant.custom.fields"></tiles:insertDefinition>
                                <li>
                                    <c:if test="${showCaptcha}">
                                    	<tiles:insertDefinition name="captcha"/>
                                    </c:if>
                                 </li>
                                 <li>
			              			 <div class="clearboth"></div> 
			              			 <div style="margin:15px 0px 15px 190px; display:inline; float:left;" >
                                        <form:checkbox cssClass="text" id="acceptedTerms" tabindex="2180" path="acceptedTerms" value="false"/>
                                        <div id="tncLink" style="color: #000;font-size: 14px;"><spring:message code="label.moreUserInfo.agree" htmlEscape="false" /></div>
                                        <div class="registration_formbox_errormsg" style="margin-left:0px;" id="acceptedTermsError"></div>
                                        <div id="tncDialog">
                                          <c:out value="${tnc}" escapeXml="false"/> 
                                        </div>
                                	</div>	
                                </li>                       
                            </ul>
                        </form:form>
                        
                        <div class="login_formbox_submitpanel">
                        	<div class="register_buttonscontainer">
                            	<a id="registrationSubmit" class="logincommonbutton" href="#" tabindex="2190">
                            		<c:choose>
                            			<c:when test="${registration.phoneVerificationEnabled}">
                            				<spring:message code="label.moreUserInfo.continue"/>
                            			</c:when>
                            			<c:otherwise>
                            				<spring:message code="label.moreUserInfo.submit"/>
                            			</c:otherwise>
                            		</c:choose>
                            	</a>
                            </div>
                        	
                        </div>
                    </div>
                
                
                </div>

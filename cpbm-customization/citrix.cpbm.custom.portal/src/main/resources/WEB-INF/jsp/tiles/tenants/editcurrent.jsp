<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %> 

<script type="text/javascript" src="<%=request.getContextPath() %>/js/creditcarddetails.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/billing.js"></script>

<c:if test ="${billingInfo.creditCard == NULL || billingInfo.creditCard.creditCardNumber == NULL}">
  <input type="hidden" id="showPrimaryAddressOnEditCreditCard" value="<c:out value="true"/>"/>
  <input type="hidden" id="primaryAddressCountry" value="<c:out value="${tenant.address.country}"/>"/>
  <input type="hidden" id="primaryAddressState" value="<c:out value="${tenant.address.state}"/>"/>
  <input type="hidden" id="primaryAddressCity" value="<c:out value="${tenant.address.city}"/>"/>
  <input type="hidden" id="primaryAddressStreet" value="<c:out value="${tenant.address.street1}"/>"/>
  <input type="hidden" id="primaryAddressPostalCode" value="<c:out value="${tenant.address.postalCode}"/>"/>
  
  <c:if test="${tenant.secondaryAddress != null}">
  <input type="hidden" id="secondaryAddressPresent" value="<c:out value="true"/>"/>
  <input type="hidden" id="secondaryAddressCountry" value="<c:out value="${tenant.secondaryAddress.country}"/>"/>
  <input type="hidden" id="secondaryAddressState" value="<c:out value="${tenant.secondaryAddress.state}"/>"/>
  <input type="hidden" id="secondaryAddressCity" value="<c:out value="${tenant.secondaryAddress.city}"/>"/>
  <input type="hidden" id="secondaryAddressStreet" value="<c:out value="${tenant.secondaryAddress.street1}"/>"/>
  <input type="hidden" id="secondaryAddressStreet2" value="<c:out value="${tenant.secondaryAddress.street2}"/>"/>
  <input type="hidden" id="secondaryAddressPostalCode" value="<c:out value="${tenant.secondaryAddress.postalCode}"/>"/>
  </c:if>
</c:if>


<spring:message code="dateonly.format" var="dateonly_format"/>
<input type="hidden" id="displayCreditCardTabVar" value="<c:out value="${showCreditCardTabVar}"/>"/>
<input type="hidden" id="currenttab" value="<c:out value="${currenttab}"/>"/>

<input id="previous_tab" type="hidden" name="previous_tab" value="profile"> 
<c:set var="displayGeneralInfoDiv" value="none"/>
<c:if test="${General=='on'}">
  <c:set var="displayGeneralInfoDiv" value=""/>
</c:if>
<div id="generalInfoDiv" style="display:${displayGeneralInfoDiv}">

<div class="maintitlebox">
    <h1> 
      <spring:message code="page.level2.general"/>
    </h1>
 </div>

  <div class="maincontent_equalverticalpanel">
    <div class="commonboxes_container" >
        <div class="commonboxes_titlebox">
              <h2><spring:message code="label.editcurrent.activationDetails"/></h2>
        </div>
          
      <div class="commonboxes">
          <div class="commonboxes_contentcontainer">
          
              <div class="commonboxes_formbox">             
                  <div class="commonboxes_formbox_panels equalwidth" style="border:none;">
                      <ul>                                                              
                          <li class="commonboxes_formbox equalwidth">
                                <label for="name" style="width:130px;"><spring:message code="label.editcurrent.signupdate"/></label>
                                <div class="commonboxes_formbox_withouttextbox equalwidth"><fmt:formatDate value="${tenantForm.tenant.createdAt}" pattern="${dateonly_format}"/></div>
                            </li>
                            
                            <li class="commonboxes_formbox equalwidth">
                                <label for="name" style="width:130px;"><spring:message code="label.editcurrent.activationDate"/></label>
                                <div class="commonboxes_formbox_withouttextbox equalwidth"><fmt:formatDate value="${tenantForm.tenant.createdAt}" pattern="${dateonly_format}"/></div>
                            </li>
                            
                             <li class="commonboxes_formbox equalwidth last">
                                <label for="name" style="width:130px;"><spring:message code="label.editcurrent.currentstatus"/></label>
                                <div class="commonboxes_formbox_withouttextbox equalwidth"><spring:message code="${tenantForm.tenant.state.code}"/></div>
                            </li>
                              
                        </ul>
                    </div>
              </div>
            </div>
        </div>
    </div>
  
  </div>
    
    <c:if test="${currentTenant.id != 1}">
  <div class="maincontent_equalverticalpanel" style="float:right;">  
       <div class="commonboxes_container" >
        <div class="commonboxes_titlebox">
              <h2><spring:message code="label.editcurrent.billingDetails"/></h2>
        </div>
        <div class="commonboxes">
          <div class="commonboxes_contentcontainer">
          
              <div class="commonboxes_formbox">             
                  <div class="commonboxes_formbox_panels equalwidth" style="border:none;">
                      <ul>                                                              
                          <li class="commonboxes_formbox equalwidth">
                                <label for="name" style="width:130px;"><spring:message code="label.editcurrent.cbp"/></label>
                                <div class="commonboxes_formbox_withouttextbox equalwidth">
                                    <fmt:formatDate value="${currentBillingStart}" pattern="${dateonly_format}"/>
                                    <c:out value=" - "></c:out>
                                   <fmt:formatDate value="${currentBillingEnd}" pattern="${dateonly_format}"/>
                                </div>
                            </li>
                            
                            <li class="commonboxes_formbox equalwidth">
                                <label for="name" style="width:130px;"><spring:message code="label.editcurrent.bp"/></label>
                                <c:if test="${BillingPeriodLength != null}">
                                   <c:if test="${BillingPeriodLength eq 1}"><div class="commonboxes_formbox_withouttextbox equalwidth"><spring:message code="label.editcurrent.bp.day" arguments="${BillingPeriodLength}"/></div></c:if>
                                   <c:if test="${BillingPeriodLength ne 1}"><div class="commonboxes_formbox_withouttextbox equalwidth"><spring:message code="label.editcurrent.days.format" arguments="${BillingPeriodLength}"/></div></c:if>
                                </c:if>
                            </li>
                            
                             <li class="commonboxes_formbox equalwidth last">
                              <label for="name" style="width:130px;"><spring:message code="label.editcurrent.accountBalance"/></label>
                                <div class="commonboxes_formbox_withouttextbox equalwidth">
                                <div style="float:left;"><c:out value="${tenantForm.tenant.currency.sign}"/></div> <fmt:formatNumber pattern="${currencyFormat}"  minFractionDigits="${minFractionDigits}"  value="${currentBalanceTopRightNav}"  />
                                </div>
                            </li>
                              
                        </ul>
                    </div>
              </div>
            </div>
          </div>
        </div>
  </div>
    </c:if>
<div class="commonboxes_container" style="margin-top:30px;">
    <div class="commonboxes_titlebox">
          <h2><spring:message code="label.editcurrent.accountInfo"/></h2>
        </div>
  <div class="commonboxes">
      <div class="commonboxes_contentcontainer">
      
          <div class="commonboxes_formbox fullwidthbox">             
              <div class="commonboxes_formbox_panels equalwidth" style="width:465px;">
                  <ul>                                                              
                      <li class="commonboxes_formbox equalwidth" style="width:445px;">
                            <label for="name"><spring:message code="label.editcurrent.masterUser.name"/></label>
                            <div class="commonboxes_formbox_withouttextbox"><c:out value="${tenantForm.tenant.owner.username}"></c:out></div>
                        </li>
                        
                        <li class="commonboxes_formbox equalwidth last" style="width:445px;">
                            <label for="name"><spring:message code="ui.accounts.all.header.accounttype"/></label>
                            <div class="commonboxes_formbox_withouttextbox"><spring:message code="registration.accounttype.${tenantForm.tenant.accountType.nameLower}"/>
                              <sec:authorize access="hasRole('ROLE_ACCOUNT_BILLING_ADMIN')">
                               <c:if test="${showChangeAccountType == true}">
                                <p><a id="requestAccountTypeConversionLink" href="#"><spring:message code="label.editcurrent.requestAccountConversionLink"></spring:message></a></p>
                               </c:if>
                               </sec:authorize>
                            </div>
                          <c:if test ="${showMessagePendingConversion == true}">
                               <br><p style="margin:7px 0 5px 2px;"><spring:message code="message.change.account.type.pending.conversion"/></p>
                            </c:if>
                        </li>
                          
                    </ul>
                </div>
                
                <div class="commonboxes_formbox_panels equalwidth" style="width:465px; border:none; ">
                  <ul>                                                              
                      <li class="commonboxes_formbox equalwidth" style="width:445px;">
                            <label for="name"><spring:message code="form.tenant.search.account_id"/></label>
                            <div class="commonboxes_formbox_withouttextbox"><c:out value="${tenantForm.tenant.accountId}"></c:out></div>
                        </li>
                    </ul>
                </div>
          </div>
        </div>
    </div>
</div>
  
    
</div>
<c:set var="displayBillingDiv" value="none"/>
<c:if test="${Contact == 'on'}">
  <c:set var="displayBillingDiv" value=""/>
</c:if>

<div id="billingdiv" style="display:${displayBillingDiv}">
<div class="maintitlebox">
    <h1> 
      <spring:message code="label.editcurrent.heading"/>
    </h1>
 </div>
<spring:url value="/portal/tenants/edit" var="edit_tenant_path" htmlEscape="false" /> 
  
 
<div class="maincontent_equalverticalpanel">
<div id="editCurrentTenant">
  <form:form commandName="tenantForm" cssClass="ajaxform" id="tenantEditForm" action="${edit_tenant_path}" 
  onsubmit="editTenant(event,this)">
    <div class="commonboxes_container">
      <div class="commonboxes_titlebox ellipsis">
        <h2 class="ellipsis" style="width: 100%" title='<c:out value="${tenantForm.tenant.name}" />'><spring:message code="label.editcurrent.edit"/> - <c:out value="${tenantForm.tenant.name}" /></h2>
      </div>
      <div class="commonboxes">
        <div class="commonboxes_contentcontainer">
          <div class="commonboxes_formbox">
            <div class="commonboxes_formbox_panels equalwidth" style="border:none;">
               <input type="hidden" id="secondaryAddressPresent" value="<c:out value="${tenantForm.secondaryAddress}"/>"/>
               <input type="hidden" id="allowSecondaryCheckBox" value="<c:out value="${allowSecondaryCheckBox}"/>"/>
               <input type="hidden" id="loggedInUserParam" value="<c:out value="${tenantForm.tenant.owner.param}"/>"/>
               <input type="hidden" id="tenantParam" value="<c:out value="${tenantForm.tenant.param}"/>"/>
              <ul>
                <li class="commonboxes_formbox equalwidth" style="width: 430px;">
                  <form:label path="tenant.address.country"><spring:message code="label.editcurrent.country"/></form:label>
                  <div class="commonboxes_formbox_withouttextbox read"><c:out value="${tenantForm.tenant.address.countryName}"></c:out></div>
                  <input type="hidden" id="countrycode_readvalue" value="<c:out value="${tenantForm.tenant.address.country}"/>"/> 
                  <div class="mandatory_wrapper write"> 
                    <form:select cssClass="select" tabindex="1" path="tenant.address.country" >
                      <c:forEach items="${tenantForm.countryList}" var="choice" varStatus="status">
                        <form:option value="${choice.countryCode2}"> <c:out value="${choice.name}" escapeXml="false"/></form:option>
                      </c:forEach>
                    </form:select>
                  </div>
                  <div class="main_addnew_formbox_myprofile_errormsg" id="tenant.address.countryError"></div>
                </li>
                
                 <li class="commonboxes_formbox equalwidth" style="width: 430px;">
                   <div id="otherstateDiv" >
                      <label for="tenantAddressStateSelect" ><spring:message code="label.moreUserInfo.state"/></label>
                    </div>
                    <div id="JPstateDiv" style="display:none">
                      <label for="tenantAddressStateSelect" ><spring:message code="label.moreUserInfo.state.jp"/></label>
                    </div>
                    <div id="stateInputReadHidden" style="display:none;"><c:out value="${tenantForm.tenant.address.state}"/></div>
                    <div class="read" id="state_readvalue">
                      <div class="commonboxes_formbox_withouttextbox" >
                        <c:choose>
                          <c:when test="${tenantForm.tenant.address.country == 'JP' || tenantForm.tenant.address.country == 'US' || tenantForm.tenant.address.country == 'AU' || tenantForm.tenant.address.country == 'IN'}">
                            <spring:message code="${tenantForm.tenant.address.country}.${tenantForm.tenant.address.state}"/>
                              </c:when>
                          <c:otherwise>
                            <c:out value="${tenantForm.tenant.address.state}"/>
                          </c:otherwise>
                        </c:choose>
                      </div>
                    </div>
                    <div class="write">                    
                      <div id="stateInput">
                        <div class="mandatory_wrapper" > 
                          <spring:message code="label.moreUserInfo.state.tooltip" var="i18nStateTooltip"/>
                          <form:input cssClass="text" tabindex="2" path="tenant.address.state" title="${i18nStateTooltip}"/>
                        </div>
                      </div> 
                      <div  id="stateSelect" style="display:none">
                        <div class="mandatory_wrapper">
                          <select class="text" tabindex="2" id="tenantAddressStateSelect" name="tenantAddressStateSelect" title="<spring:message code="label.moreUserInfo.state.tooltip"/>"></select>
                        </div>
                      </div>
                    </div> 
                    <div class="main_addnew_formbox_myprofile_errormsg" id="tenant.address.stateError"></div>                    
               </li>  
               <li class="commonboxes_formbox equalwidth" style="width: 430px;">
                  <form:label path="tenant.address.street1"><spring:message code="label.editcurrent.address1"/></form:label>
                  <div class="commonboxes_formbox_withouttextbox read ellipsis" id="address1_readvalue" style="max-width: 70%" title='<c:out value="${tenantForm.tenant.address.street1}"></c:out>'><c:out value="${tenantForm.tenant.address.street1}"></c:out></div> 
                  <div class="mandatory_wrapper write" ><form:input  cssClass="text" path="tenant.address.street1" tabindex="3"></form:input></div>
                  <div class="main_addnew_formbox_myprofile_errormsg" id="tenant.address.street1Error"></div>
                </li>     
                <li class="commonboxes_formbox equalwidth" style="width: 430px;">
                  <form:label path="tenant.address.street2"><spring:message code="label.editcurrent.address2"/></form:label> 
                  <div class="commonboxes_formbox_withouttextbox read ellipsis" id="address2_readvalue" style="max-width: 70%" title='<c:out value="${tenantForm.tenant.address.street2}"></c:out>'><c:out value="${tenantForm.tenant.address.street2}"></c:out></div>
                  <div class="nonmandatory_wrapper write" ><form:input cssClass="text" path="tenant.address.street2" tabindex="4"></form:input></div>
                </li>
                <li class="commonboxes_formbox equalwidth" style="width: 430px;">
                  <form:label path="tenant.address.city"><spring:message code="label.editcurrent.city"/></form:label>
                  <div class="commonboxes_formbox_withouttextbox read ellipsis" id="city_readvalue" style="max-width: 70%" title='<c:out value="${tenantForm.tenant.address.city}"></c:out>'><c:out value="${tenantForm.tenant.address.city}"></c:out></div> 
                  <div class="mandatory_wrapper write" ><form:input  cssClass="text" path="tenant.address.city" tabindex="5"></form:input></div>
                  <div class="main_addnew_formbox_myprofile_errormsg" id="tenant.address.cityError"></div>
                </li>
                <li class="commonboxes_formbox equalwidth last" style="width: 430px;">
                  <form:label path="tenant.address.postalCode"><spring:message code="label.editcurrent.zip"/></form:label>
                  <div class="commonboxes_formbox_withouttextbox read" id="postalcode_readvalue"><c:out value="${tenantForm.tenant.address.postalCode}"></c:out></div>
                  <div class="mandatory_wrapper write" ><form:input cssClass="text" path="tenant.address.postalCode" maxLength="25" tabindex="6"></form:input></div>
                  <div class="main_addnew_formbox_myprofile_errormsg" id="tenant.address.postalCodeError"></div>
                </li>    
                 </ul>
                 </div>
        </div>
        <div class="commonbox_submitbuttonpanel">
          <div id="edittenant" class="commonbox_submitbuttonbox read">
            <p><a href="javascript:void(0);"><spring:message code="label.edit"/></a></p>
          </div>
          <div class="commonbox_submitbuttonbox write">
            <p><a id="edittenantcancel" href="javascript:void(0);"><spring:message code="label.editcurrent.cancel"/></a></p>
            <input tabindex="210" id="editTenantSave" class="commonbutton" type="submit" value="<spring:message code="label.editcurrent.save"/>" />
            <input type="hidden" id="tenantAddressHiddenStateSelect" value="<c:out value="${tenantForm.tenant.address.state}"/>" />
          </div>
        </div>
       </div>
      </div>
    </div>
  </form:form>
  </div>
</div>


<div class="maincontent_equalverticalpanel" style="float:right;margin-top:35px;">  
        <div class="commonboxes_titlebox">
              <h2><spring:message code="label.editcurrent.masteruser.info"/></h2>
        </div>
        <div class="commonboxes">
          <div class="commonboxes_contentcontainer">
          
              <div class="commonboxes_formbox">             
                  <div class="commonboxes_formbox_panels equalwidth" style="border:none;">
                      <ul>                                                              
                          <li class="commonboxes_formbox equalwidth">
                                <label for="name" style="width:130px;"><spring:message code="label.myprofile.firstName"/></label>
                                <div class="commonboxes_formbox_withouttextbox equalwidth"><c:out value="${tenantForm.tenant.owner.firstName}"/></div>
                            </li>
                            
                            <li class="commonboxes_formbox equalwidth">
                                <label for="name" style="width:130px;"><spring:message code="label.myprofile.lastName"/></label>
                                <div class="commonboxes_formbox_withouttextbox equalwidth"><c:out value="${tenantForm.tenant.owner.lastName}"/></div>
                            </li>
                            
                             <li class="commonboxes_formbox equalwidth">
                              <label for="name" style="width:130px;"><spring:message code="label.myprofile.username"/></label>
                                <div class="commonboxes_formbox_withouttextbox equalwidth"><c:out value="${tenantForm.tenant.owner.username}"/></div>
                            </li>
                             <li class="commonboxes_formbox equalwidth">
                                <label for="name" style="width:130px;"><spring:message code="label.myprofile.email"/></label>
                                <div class="commonboxes_formbox_withouttextbox equalwidth"><c:out value="${tenantForm.tenant.owner.email}"/></div>
                            </li>
                             <li class="commonboxes_formbox equalwidth last">
                                <label for="name" style="width:130px;"><spring:message code="label.myprofile.phone"/></label>
                                <div class="commonboxes_formbox_withouttextbox equalwidth">
                                <c:out value="${tenantForm.tenant.owner.phone}"/>
                                </div>
                            </li>
                              
                        </ul>
                    </div>
              </div>
            </div>
          </div>
        </div>

</div>
<c:set var="displayCreditCardDetailsDiv" value="none"/>
<c:if test="${BillingDetails == 'on' }">
  <c:set var="displayCreditCardDetailsDiv" value=""/>
</c:if>

<div id="creditCardDetailsDiv" style="display:${displayCreditCardDetailsDiv}">

  <c:choose>
    <c:when test="${tenantForm.tenant.accountType.enableSecondaryAddress == true}">
        
        <div id="creditCardTypeDetailsDiv">
        <div class="maintitlebox">
          <h1><spring:message code="page.level2.billingdetails"/></h1>
        </div>
  
    <div class="maincontent_equalverticalpanel">
      <form:form commandName="tenantForm" cssClass="ajaxform" id="tenantSecondaryAddressEditForm" action="${edit_tenant_path}" onsubmit="editTenantForSecAddress(event,this)">
     <div class="commonboxes_container">
      <div class="commonboxes_titlebox">
      <h2><spring:message code="edit.payment.info.billing.address.title"/></h2>
      </div>
            <div class="commonboxes">
            <div class="commonboxes_contentcontainer">
          <div class="commonboxes_formbox">
            <div class="commonboxes_formbox_panels equalwidth" style="border:none;">
            <ul id="secondaryAddressDetailsDiv">

                 <li class="commonboxes_formbox equalwidth" >
                  <form:label path="secondaryAddress.country"><spring:message code="label.editcurrent.country"/></form:label>
                  <div class="commonboxes_formbox_withouttextbox read"><c:out value="${tenantForm.secondaryAddress.countryName}"></c:out></div>
                  <input type="hidden" id="countrycode_readvalue" value="<c:out value="${tenantForm.secondaryAddress.country}"/>"/> 
                  <div class="mandatory_wrapper write"> 
                    <form:select cssClass="select" tabindex="1" path="secondaryAddress.country" >
                      <c:forEach items="${tenantForm.countryList}" var="choice" varStatus="status">
                        <form:option value="${choice.countryCode2}"> <c:out value="${choice.name}" escapeXml="false"/></form:option>
                      </c:forEach>
                    </form:select>
                  </div>
                  <div class="main_addnew_formbox_myprofile_errormsg" id="secondaryAddress.countryError"></div>
                </li>
                
                <li class="commonboxes_formbox equalwidth" >
                   <div id="otherstateSecondaryDiv" >
                      <label for="tenantSecondaryAddressStateSelect" ><spring:message code="label.moreUserInfo.state"/></label>
                    </div>
                    <div id="JPSecondarystateDiv" style="display:none">
                      <label for="tenantSecondaryAddressStateSelect" ><spring:message code="label.moreUserInfo.state.jp"/></label>
                    </div>
                    <div id="stateSecondaryInputReadHidden" style="display:none;"><c:out value="${tenantForm.secondaryAddress.state}"/></div>
                    <div class="read" id="stateSecondary_readvalue">
                      <div class="commonboxes_formbox_withouttextbox" >
                        <c:choose>
                          <c:when test="${tenantForm.secondaryAddress.country == 'JP' || tenantForm.secondaryAddress.country == 'US' || tenantForm.secondaryAddress.country == 'AU' || tenantForm.secondaryAddress.country == 'IN'}">
                            <spring:message code="${tenantForm.secondaryAddress.country}.${tenantForm.secondaryAddress.state}"/>
                              </c:when>
                          <c:otherwise>
                            <c:out value="${tenantForm.secondaryAddress.state}"/>
                          </c:otherwise>
                        </c:choose>
                      </div>
                    </div>
                    <div class="write">                    
                      <div id="stateSecondaryInput">
                        <div class="mandatory_wrapper" > 
                          <spring:message code="label.moreUserInfo.state.tooltip" var="i18nStateTooltip"/>
                          <form:input cssClass="text" tabindex="2" path="secondaryAddress.state" title="${i18nStateTooltip}"/>
                        </div>
                      </div> 
                      <div  id="stateSecondarySelect" style="display:none">
                        <div class="mandatory_wrapper">
                          <select class="text" tabindex="2" id="tenantSecondaryAddressStateSelect" name="tenantSecondaryAddressStateSelect" title="<spring:message code="label.moreUserInfo.state.tooltip"/>"></select>
                        </div>
                      </div>
                    </div> 
                    <div class="main_addnew_formbox_myprofile_errormsg" id="secondaryAddress.stateError"></div>                    
               </li>           
                
               <li class="commonboxes_formbox equalwidth" >
                  <form:label path="secondaryAddress.street1"><spring:message code="label.editcurrent.address1"/></form:label>
                  <div class="commonboxes_formbox_withouttextbox read" id="secondary_address1_readvalue"><c:out value="${tenantForm.secondaryAddress.street1}"></c:out></div> 
                  <div class="mandatory_wrapper write" ><form:input  cssClass="text" path="secondaryAddress.street1" tabindex="3"></form:input></div>
                  <div class="main_addnew_formbox_myprofile_errormsg" id="secondaryAddress.street1Error"></div>
                </li>
                <li class="commonboxes_formbox equalwidth" >
                  <form:label path="secondaryAddress.street2"><spring:message code="label.editcurrent.address2"/></form:label> 
                  <div class="commonboxes_formbox_withouttextbox read" id="secondary_address2_readvalue"><c:out value="${tenantForm.secondaryAddress.street2}"></c:out></div>
                  <div class="nonmandatory_wrapper write" ><form:input cssClass="text" path="secondaryAddress.street2" tabindex="4"></form:input></div>
                </li>
                <li class="commonboxes_formbox equalwidth" >
                  <form:label path="secondaryAddress.city"><spring:message code="label.editcurrent.city"/></form:label>
                  <div class="commonboxes_formbox_withouttextbox read" id="secondary_city_readvalue"><c:out value="${tenantForm.secondaryAddress.city}"></c:out></div> 
                  <div class="mandatory_wrapper write" ><form:input  cssClass="text" path="secondaryAddress.city" tabindex="5"></form:input></div>
                  <div class="main_addnew_formbox_myprofile_errormsg" id="secondaryAddress.cityError"></div>
                </li>
                <li class="commonboxes_formbox equalwidth" >
                  <form:label path="secondaryAddress.postalCode"><spring:message code="label.editcurrent.zip"/></form:label>
                  <div class="commonboxes_formbox_withouttextbox read" id="secondary_postalcode_readvalue"><c:out value="${tenantForm.secondaryAddress.postalCode}"></c:out></div>
                  <div class="mandatory_wrapper write" ><form:input cssClass="text" path="secondaryAddress.postalCode" maxLength="25" tabindex="6"></form:input></div>
                  <div class="main_addnew_formbox_myprofile_errormsg" id="secondaryAddress.postalCodeError"></div>
                </li>
                
                  <li id="allowSecondaryIdLi" style="display:none" class="commonboxes_formbox equalwidth last">
                  <form:label path="tenant.address.postalCode"><spring:message code="ui.accounts.all.header.sync.addressess"/></form:label>
                  <input type="checkbox" id="syncAddressCurrent" style="margin-left: 12px; margin-top: 5px;">
                </li> 
                
              </ul>
              </div>
            </div>   
            <div class="commonbox_submitbuttonpanel">
            <div id="editCreditCardTypeDetails" class="commonbox_submitbuttonbox read">
            <p><a href="javascript:void(0);"><spring:message code="label.edit"/></a></p>
            </div>
              <div class="commonbox_submitbuttonbox write">
                <p><a id="editCreditCardDetailsCancel" href="javascript:void(0);"><spring:message code="label.editcurrent.cancel"/></a></p>
                <input tabindex="120" type="submit" id="editSecondaryAddressSaveButton" rel="<spring:message code="label.saving"/>" class="commonbutton submitmsg" value="<spring:message code="label.save"/>"/>
              </div>
            </div>   
            </div>
            </div>
            
            </div>
      </form:form>
    </div>
</div>
        
    </c:when>

   <c:otherwise>
      <c:if test="${tenantForm.tenant.accountType.name != 'Trial'}">
      <c:set var="showChangeAccountType" value="${showChangeAccountType}" scope="request"></c:set>
        <c:set var="showMessagePendingConversion" value="${showMessagePendingConversion}" scope="request"></c:set>
        <c:set var="billingInfo" value="${billingInfo}" scope="request"></c:set>
        <c:set var="show_deposit_record_message" value="${show_deposit_record_message}" scope="request"></c:set>
        <jsp:include page="../billing/showcreditcarddetails.jsp"></jsp:include>  
        </c:if>
</c:otherwise>

</c:choose>

</div>


<div id="changeAccountTypeJsp" style="display: none;">
        <c:set var="tenant" value="${tenantForm.tenant}" scope="request"></c:set>
        <c:set var="setAccountTypeForm" value="${setAccountTypeForm}" scope="request"></c:set>
        <c:set var="targetAccountTypes" value="${targetAccountTypes}" scope="request"></c:set>
        <jsp:include page="../billing/changeaccounttype.jsp"></jsp:include>
</div>


<!-- API Credentials  -->
<div  class="maincontent" style="margin:0;">
    <div id="userCredentialDiv" class="maincontent_horizontalpanels" style="margin:0;">
         <div id="apiCredentialsDiv" class="commonboxes_container" style="display: none;">
             <div id="titleDiv" class="commonboxes_titlebox">
                 <h2><spring:message code="page.level2.cloudstack"/> - <spring:message code="page.level2.apicredentials"/></h2>
              </div>
              <div class="commonboxes">
                <div class="commonboxes_contentcontainer fullwidthbox">
                  <div class="commonboxes_formbox fullwidthbox">
                     <span id="serviceLogo" class="logo"></span>  
                     <div class="commonboxes_formbox_panels fullwidth" style="float:right; width:750px;">
                            <ul id="tenantCredentialUl">    
                               <li  id="tenantCredentialLi"   class="commonboxes_formbox fullwidth" style="width:730px;" >
                                    <label id="tenantCredentialLabel" for="lableFor" ></label>
                                    <div id="liCredentialValue" class="commonboxes_formbox_withouttextbox fullwidth" style="width:620px;" ></div>
                               </li>                                                          
                            </ul>
                      </div>
                        
                  </div>
                 </div>
               </div>
          </div>
     </div>
</div>

<div id="historyDiv" class="history" style="display: none;"  >
	<tiles:insertDefinition name="tenant.state.changes"/>
</div>
<input id="doNotShowVerifyUserDiv" type="hidden" name="doNotShowVerifyUserDiv" value="${doNotShowPasswordEditLink}">
<div id="verifyUserDiv" title='<spring:message code="user.password.verification" />' style="min-height:150px; overflow: hidden;display: none;">
	<tiles:insertDefinition name="user.password_verification"/>
  <div id="wrongPasswordError"></div>
</div>
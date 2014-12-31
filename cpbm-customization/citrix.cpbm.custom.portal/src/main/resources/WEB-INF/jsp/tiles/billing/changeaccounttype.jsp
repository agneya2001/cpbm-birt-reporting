<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="csrf" uri="http://www.owasp.org/index.php/Category:OWASP_CSRFGuard_Project/Owasp.CsrfGuard.tld" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:if test="${fn:length(targetAccountTypes) > 0}">
  <script type="text/javascript">
    $(document).ready(function() {
      accountTypeChanged($("#accountTypeNameSelect"));
    });
    var currentPaymentMode= '<c:out value="${tenant.accountType.paymentModes}"/>';
    var dictionary = {      
      payInitialDepositMessage: '<spring:message javaScriptEscape="true" code="pay.initial.deposit"/>',
      doYouWantToProceed: '<spring:message javaScriptEscape="true" code="do.you.want.to.proceed"/>'
    };
  </script>
</c:if>
<spring:url value="/portal/billing/changeaccounttype" var="change_account_type_path" htmlEscape="false">
  <spring:param name="tenant">
    <c:out value="${tenant.param}"/>
  </spring:param>
</spring:url>
<div class="commonboxes_container" style="margin:20px 0 0 0;">
  <div class="commonboxes_titlebox">
    <h2><spring:message code="label.change.account.type.title"/></h2>
    <p style='color:#000000'>
      <span>*</span>
      <spring:message code="message.mandatory.fields"/>
    </p>
  </div>
  
   <div id="creditcard_update_error" class="common_messagebox error" style="display:none;">
      <span class="erroricon"></span>
         <p id="p_message">
      </p>
   </div>
        
  <div class="commonboxes">
    <div class="commonboxes_contentcontainer bigbox" style="width:100%">
      <div class="clearboth">
      </div>
      <form:form commandName="setAccountTypeForm" id="setAccountTypeForm" cssClass="ajaxform formPanel billingForm" action="${change_account_type_path}" method="POST" onsubmit="changeAccountType(event,this,'${tenant.param}')">
       
        <div id="selectAccountType" style="margin-top:15px;">
          <div style="float:left; margin-left:10px;">
            <label for="accountTypeName" style="color:#5E5E5E;">
              <spring:message code="label.change.account.type.convert.to"/>
            </label>
          </div>
          <div style="float:left;margin-left:30px;width:600px;">
            <div class="mandatory_wrapper">
              <spring:message code="label.change.account.type.tooltip" var="i18nAccountTypeTooltip"/>
              <form:select tabindex="1" path="accountTypeName" id="accountTypeNameSelect" title="${i18nAccountTypeTooltip}" cssClass="select" onChange="accountTypeChanged(this)" cssStyle='margin-left:10px'>
                <c:forEach items="${targetAccountTypes}" var="choice">
                  <form:option value="${choice.name}">
                    <spring:message code="registration.accounttype.description.${choice.nameLower}"/>
                  </form:option>
                </c:forEach>
              </form:select>
            </div>
          </div>
        </div>
        <div id="addCreditCardDivForAccountTypeChange" style="display:none;margin:0 15px;">
        <div class="common_messagebox success" id="initialDepositAmount_msg" style="display:none;">
          <p><spring:message code="pay.initial.deposit"/>&nbsp;<span id="initialDepositAmount"></span>.</p>
        </div>
        <div class="maincontent_equalverticalpanel">
          <div class="creditcard_box front">
            <div class="creditcard_box front top">
            </div>
            <div class="creditcard_box front cardtypepanel">
              <h2><spring:message code="edit.payment.info.credit.card.title"/></h2>
              <div class="creditcard_typebox">
                <c:forEach items="${billingInfo.ccTypes}" var="cc_choice" varStatus="status">
                      <c:if test="${status.index==0}">
                        <span id="<c:out value='${cc_choice.name}'/>" class="cc_icon <c:out value='${fn:toLowerCase(cc_choice.code)}'/> active"></span>
                        <c:set var="defaultCreditCardName" value="${cc_choice.name}"></c:set>
                      </c:if>
                      <c:if test="${status.index!=0}">
                        <span id="<c:out value='${cc_choice.name}'/>" class="cc_icon <c:out value='${fn:toLowerCase(cc_choice.code)}'/> nonactive"></span>
                      </c:if>
                 </c:forEach>
              </div>
            </div>
            <div class="creditcard_box front mid">
              <div class="creditcard_box front mid contentarea ">
                <ul>
                  <li style="display: none">
                    <input class="text" id="defaultCreditCardNameHidden"  value="<c:out value='${defaultCreditCardName}'/>"/>
                    <form:input autocomplete="off" tabindex="102" path="creditCard.creditCardType" cssClass="text" value="${defaultCreditCardName}"/>
                  </li>
                  <li style="width:200px;">
                    <span class="label"><spring:message code="label.payment.info.first.name.on.card"/></span>
                    <div class="mandatory_wrapper withlabel">
                    </div>
                    <input class="text" tabindex="103" id="creditCard.firstNameOnCard" name="creditCard.firstNameOnCard" value="<c:out value='${tenant.owner.firstName}'/>" title=""/>
                    <div class="main_addnew_formbox_errormsg_paymentinfo" id="creditCard.firstNameOnCardError" style="margin-left: 0px;">
                    </div>
                  </li>
                  <li style="width:200px; float:right;">
                    <span class="label"><spring:message code="label.payment.info.last.name.on.card"/></span>
                    <div class="mandatory_wrapper withlabel">
                    </div>
                    <input class="text" tabindex="104" id="creditCard.lastNameOnCard" name="creditCard.lastNameOnCard" value="<c:out value='${tenant.owner.lastName}'/>" title=""/>
                    <div class="main_addnew_formbox_errormsg_paymentinfo" id="creditCard.lastNameOnCardError" style="margin-left: 0px;">
                    </div>
                  </li>
                  <li style="width:250px;">
                    <span class="label"><spring:message code="label.payment.info.card.number"/></span>
                    <div class="mandatory_wrapper withlabel">
                    </div>
                    <form:input autocomplete="off" tabindex="105" path="creditCard.creditCardNumber" title="0000 0000 0000 0000" cssClass="text"/>
                    <div class="main_addnew_formbox_errormsg_paymentinfo" id="creditCard.creditCardNumberError" style="margin-left: 0px;">
                    </div>
                  </li>
                  <li style="width:160px; float:right;">
                    <span class="label"><spring:message code="label.payment.info.cvv"/></span>
                    <div class="mandatory_wrapper withlabel">
                    </div>
                    <a class="helpicon" style="margin:0 0 0 5px;" title="<spring:message code="message.payment.info.cvv.help"/>" href="#"></a>
                    <form:password autocomplete="off" tabindex="106" size="4" maxlength="4" path="creditCard.creditCardCVV" title="" cssClass="text j_credit_card_cvv"/>
                    <div class="main_addnew_formbox_errormsg_paymentinfo" id="creditCard.creditCardCVVError" style="margin-left: 0px;">
                    </div>
                  </li>
                  <li style="width:260px;">
                    <span class="label"><spring:message code="label.payment.info.expiry"/></span>
                    <div class="mandatory_wrapper withlabel">
                    </div>
                    <span class="expirationwrapper">
                      <div id="monthDiv" style="float: left;margin-right: 10px;">
                        <spring:message code="label.payment.info.expiry.month" htmlEscape="false" var="i18nCreditCardExpMonth"/>
                        <form:select tabindex="107" path="creditCard.creditCardExpirationMonth" title="${i18nCreditCardExpMonth}" cssClass="smallselect">
                          <form:option value="">
                            <spring:message code="label.choose"/>
                          </form:option>
                          <form:option value="01">
                            <spring:message code="label.payment.info.month.01"/>
                          </form:option>
                          <form:option value="02">
                            <spring:message code="label.payment.info.month.02"/>
                          </form:option>
                          <form:option value="03">
                            <spring:message code="label.payment.info.month.03"/>
                          </form:option>
                          <form:option value="04">
                            <spring:message code="label.payment.info.month.04"/>
                          </form:option>
                          <form:option value="05">
                            <spring:message code="label.payment.info.month.05"/>
                          </form:option>
                          <form:option value="06">
                            <spring:message code="label.payment.info.month.06"/>
                          </form:option>
                          <form:option value="07">
                            <spring:message code="label.payment.info.month.07"/>
                          </form:option>
                          <form:option value="08">
                            <spring:message code="label.payment.info.month.08"/>
                          </form:option>
                          <form:option value="09">
                            <spring:message code="label.payment.info.month.09"/>
                          </form:option>
                          <form:option value="10">
                            <spring:message code="label.payment.info.month.10"/>
                          </form:option>
                          <form:option value="11">
                            <spring:message code="label.payment.info.month.11"/>
                          </form:option>
                          <form:option value="12">
                            <spring:message code="label.payment.info.month.12"/>
                          </form:option>
                        </form:select>
                      </div>
                      <div id="yearDiv" style="float: left;">
                        <spring:message code="label.payment.info.expiry.year" htmlEscape="false" var="i18nCreditCardExpYear"/>
                        <form:select tabindex="108" cssStyle="margin-left: 10px;" path="creditCard.creditCardExpirationYear" title="${i18nCreditCardExpYear}" cssClass="smallselect">
                          <form:option value="">
                            <spring:message code="label.choose"/>
                          </form:option>
                          <c:forEach items="${billingInfo.creditCardExpYearList}" var="choice" varStatus="status">
                            <form:option value="${choice}">
                              <c:out value="${choice}"/>
                            </form:option>
                          </c:forEach>
                        </form:select>
                      </div>
                    </span>
                    <div class="main_addnew_formbox_errormsg_paymentinfo" id="creditCardExpirationMonthError" style="margin-left: 0px;">
                    </div>
                  </li>
                </ul>
              </div>
            </div>
            <div class="creditcard_box front bot">
            </div>
          </div>
        </div>
        <div class="maincontent_equalverticalpanel" style="float:right;">
          <div class="creditcard_box front">
            <div class="creditcard_box back top">
            </div>
            <div class="creditcard_box back mid">
              <div class="creditcard_box back mid titlearea">
                <span class="address_icon"></span>
                <h2><spring:message code="edit.payment.info.billing.address.title"/></h2>
              </div>
              <div class="creditcard_box back mid contentarea">
                <ul>
                  <li>
                    <span class="label"><spring:message code="label.payment.info.country"/></span>
                    <div class="mandatory_wrapper">
                      <spring:message code="label.payment.info.country" var="i18nCreditCardCountry"/>
                      <form:select tabindex="109" path="creditCard.creditCardAddress.country" title="${i18nCreditCardCountry}" cssClass="select">
                        <option value=""><spring:message code="label.choose"/></option>
                        <c:forEach items="${billingInfo.countryList}" var="choice" varStatus="status">
                          <option value="<c:out value="${choice.countryCode2}"/>"><c:out value="${choice.name}" escapeXml="false"/></option>
                        </c:forEach>
                      </form:select>
                    </div>
                    <div class="main_addnew_formbox_errormsg_paymentinfo" id="creditCard.creditCardAddress.countryError" style="margin-left: 132px;">
                    </div>
                  </li>
                  <li>
                    <span class="label"><spring:message code="label.payment.info.state"/></span>
                    <div class="form_element" id="billingStateInput">
                      <div class="nonmandatory_wrapper">
                        <form:input tabindex="110" path="creditCard.creditCardAddress.state" cssClass="text" title="${i18nCreditCardState}"/>
                      </div>
                      <div class="main_addnew_formbox_errormsg_paymentinfo" id="creditCard.creditCardAddress.stateError" style="margin-left: 132px;">
                      </div>
                      <form:errors element="label" path="creditCard.creditCardAddress.state" cssClass="error serverError"/>
                    </div>
                    <div class="form_element" id="billingStateSelect" style="display:none">
                      <div id="otherstateDivCC" style="display:none">
                      </div>
                      <div id="JPstateDivCC">
                      </div>
                      <div class="mandatory_wrapper">
                        <select tabindex="111" id="billingAddressStateSelect" name="billingAddressStateSelect" class="select" title='<c:out value="${i18nCreditCardState}"/>'></select>

                      </div>
                      <div class="main_addnew_formbox_errormsg_paymentinfo" id="billingAddressStateSelectError" style="margin-left: 132px;">
                      </div>
                    </div>
                  </li>
                  <li>
                    <span class="label"><spring:message code="label.payment.info.address"/></span>
                    <div class="mandatory_wrapper">
                      <spring:message code="label.payment.info.address" var="i18nCreditCardAddress"/><form:input tabindex="111" path="creditCard.creditCardAddress.street1" title="${i18nCreditCardAddress}" cssClass="text"/>
                    </div>
                    <div class="main_addnew_formbox_errormsg_paymentinfo" id="creditCard.creditCardAddress.street1Error" style="margin-left: 132px;">
                    </div>
                    <form:errors element="label" path="creditCard.creditCardAddress.street1" cssClass="error serverError"/>
                  </li>
                  <li>
                    <span class="label"><spring:message code="label.payment.info.city"/></span>
                    <div class="mandatory_wrapper">
                      <spring:message code="label.payment.info.city" var="i18nCreditCardCity"/><form:input tabindex="112" path="creditCard.creditCardAddress.city" cssClass="text" title="${i18nCreditCardCity}"/>
                    </div>
                    <div class="main_addnew_formbox_errormsg_paymentinfo" id="creditCard.creditCardAddress.cityError" style="margin-left: 132px;">
                    </div>
                    <form:errors element="label" path="creditCard.creditCardAddress.city" cssClass="error serverError"/>
                  </li>
                  <li>
                    <span class="label"><spring:message code="label.payment.info.zip"/></span>
                    <div class="mandatory_wrapper">
                      <spring:message code="label.payment.info.zip" var="i18nCreditCardPostalCode"/><form:input tabindex="113" path="creditCard.creditCardAddress.postalCode" title="${i18nCreditCardPostalCode}" cssClass="text"/>
                    </div>
                    <div class="main_addnew_formbox_errormsg_paymentinfo" id="creditCard.creditCardAddress.postalCodeError" style="margin-left: 132px;">
                    </div>
                    <form:errors element="label" path="creditCard.creditCardAddress.postalCode" cssClass="error serverError"/>
                  </li>
                </ul>
              </div>
            </div>
            <div class="creditcard_box back bot">
            </div>
          </div>
        </div>
        </div>
        <div id="deferredActivationDiv" class="commonboxes_formbox bigformbox paymentModes" style="margin-top:15px; display:none;">
          <p style="margin-left:10px;"><spring:message code="message.change.account.type.manual.activation"/></p>
        </div>
        <div class="commonbox_submitbuttonpanel">
          <div class="commonbox_submitbuttonbox">
            <p>
              <a href="#"  tabindex="115" id="changeAccountTypeCancelLink"><spring:message code="label.cancel"/></a>
            </p>
            <input type="hidden" name="<csrf:tokenname/>" value="<csrf:tokenvalue uri="portal/billing/changeaccounttype"/>"/>
            <input tabindex="114" type="submit" rel="<spring:message code="label.saving"/>" class="commonbutton submitmsg" value="<spring:message code="label.save"/>"/>
          </div>
        </div>
      </form:form>
    </div>
  </div>
</div>

<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<script language="javascript">
$(document).ready(
		  function() {
		    function swap_name_order_tab_index(first_name_css_class, last_name_css_class) {
	        if (first_name_css_class != null && last_name_css_class != null) {
	          var $first_name_input = $("." + first_name_css_class).find("input");
	          var $last_name_input = $("." + last_name_css_class).find("input");
	          var first_name_tabindex = $first_name_input.attr("tabindex");
	          var last_name_tabindex = $last_name_input.attr("tabindex");
	            
	          if (($("." + first_name_css_class).css("display") == "table-footer-group" && $("." + last_name_css_class).css("display") == "table-header-group") || ($("." + first_name_css_class).css("float") == "right" && $("." + last_name_css_class).css("float") == "left") ) {
	            $first_name_input.attr("tabindex", last_name_tabindex);
	            $last_name_input.attr("tabindex", first_name_tabindex);
	          }
	        }
	      }
		    swap_name_order_tab_index("user_first_name", "user_last_name");
		});
var dictionary = {      
  save: '<spring:message javaScriptEscape="true" code="label.save"/>',
  payInitialDepositMessage: '<spring:message javaScriptEscape="true" code="pay.initial.deposit"/>',
  doYouWantToProceed: '<spring:message javaScriptEscape="true" code="do.you.want.to.proceed"/>'
};
</script> 

  <spring:url value="/portal/billing/editcreditcarddetails" var="update_billing_path" htmlEscape="false">
        <spring:param name="tenant"><c:out value="${tenant.param}"/></spring:param>
      </spring:url>
<!-- if have some payment gateway we don't get cc details so in that we should ask for add again. so checking for paymentgateway profile id. -->
<c:choose>

 <c:when test ="${billingInfo.creditCard == NULL || isAccountExistInPaymentGateway == false}">
  
<div id="addCreditCardDiv" class="maincontent" style="float:left;margin:0;">
  <c:if test="${billingInfo.action == 'launchvm' && paymentGWErrorString == null}">
      <div class="common_messagebox success">
          <p><spring:message code="message.payment.info.launchvm"/></p>
      </div>
  </c:if>

  <c:if test="${show_deposit_record_message}">
      <div class="common_messagebox success">
          <p><spring:message code="pay.initial.deposit"/>&nbsp;<c:out value="${initialDepositAmount}"></c:out>.</p>
      </div>
  </c:if>

  <form:form commandName="billingInfo" id="billingForm" cssClass="ajaxform formPanel billingForm" action="${update_billing_path}" method="POST" onsubmit="editCreditCardDetails(event,this,'${tenant.param}', '${show_deposit_record_message}','${initialDepositAmount}')">      
    
  <div id="creditcard_update_error" class="common_messagebox error" style="display: none"><span class="erroricon"></span><p id="p_message"></p></div>
  
 <div class="maincontent_equalverticalpanel">
  <div class="creditcard_box front">
      <div class="creditcard_box front top"></div>
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
               <li style="display: None">
                  <input class="text" id="defaultCreditCardNameHidden"  value="<c:out value='${defaultCreditCardName}'/>"/>
                   <form:input autocomplete="off" tabindex="101" path="creditCard.creditCardType" cssClass="text" value="${defaultCreditCardName}"/>
              </li>
               <li style="width:200px;" class="inline_user_first_name">
                <span class="label"><spring:message code="label.payment.info.first.name.on.card"/></span><div class="mandatory_wrapper withlabel"></div>
                  <input  class="text" tabindex="102" id="creditCard.firstNameOnCard" name="creditCard.firstNameOnCard" value="<c:out value='${tenant.owner.firstName}'/>" title=""/>
                <div class="main_addnew_formbox_errormsg_paymentinfo" id="creditCard.firstNameOnCardError" style="margin-left: 0px;"></div>
              </li>                                                                  
              <li style="width:200px;" class="inline_user_last_name">
                <span class="label"><spring:message code="label.payment.info.last.name.on.card"/></span><div class="mandatory_wrapper withlabel"></div>
                  <input  class="text" tabindex="103" id="creditCard.lastNameOnCard" name="creditCard.lastNameOnCard" value="<c:out value='${tenant.owner.lastName}'/>" title=""/>
                <div class="main_addnew_formbox_errormsg_paymentinfo" id="creditCard.lastNameOnCardError" style="margin-left: 0px;"></div>
              </li>    
              <li style="width:240px;">
              <span class="label"><spring:message code="label.payment.info.card.number"/></span><div class="mandatory_wrapper withlabel"></div>
                    <form:input autocomplete="off" tabindex="104" path="creditCard.creditCardNumber" title="0000 0000 0000 0000" cssClass="text"/>
                  <div class="main_addnew_formbox_errormsg_paymentinfo" id="creditCard.creditCardNumberError" style="margin-left: 0px;"></div>
              </li>
               <li style="width:150px; float:right;">
               <span class="label"><spring:message code="label.payment.info.cvv"/></span><div class="mandatory_wrapper withlabel"></div> <a class="helpicon js_cvv_help" style="margin:0 0 0 5px;" data-toggle="popover" data-trigger="hover" data-placement="right" data-container="body" data-content='<spring:message code="message.payment.info.cvv.help"/>' href="javascript:void(0);"></a>
                    <form:password autocomplete="off" tabindex="105" size="4" maxlength="4" path="creditCard.creditCardCVV" title="" cssClass="text j_credit_card_cvv"/>
                    <div class="main_addnew_formbox_errormsg_paymentinfo" id="creditCard.creditCardCVVError" style="margin-left: 0px;"></div>
              </li>                                                            
                                                                            
              <li style="width:260px;">
                <span class="label"><spring:message code="label.payment.info.expiry"/></span><div class="mandatory_wrapper withlabel"></div>
                <span class="expirationwrapper">                                           
                 <div id="monthDiv" style="float: left;margin-right: 10px;">
                    <spring:message code="label.payment.info.expiry.month" htmlEscape="false" var="i18nCreditCardExpMonth"/>
                    <form:select tabindex="106" path="creditCard.creditCardExpirationMonth" title="${i18nCreditCardExpMonth}" cssClass="smallselect">
                      <form:option value=""><spring:message code="label.choose"/></form:option>
                      <form:option value="01"><spring:message code="label.payment.info.month.01"/></form:option>
                      <form:option value="02"><spring:message code="label.payment.info.month.02"/></form:option>
                      <form:option value="03"><spring:message code="label.payment.info.month.03"/></form:option>
                      <form:option value="04"><spring:message code="label.payment.info.month.04"/></form:option>
                      <form:option value="05"><spring:message code="label.payment.info.month.05"/></form:option>
                      <form:option value="06"><spring:message code="label.payment.info.month.06"/></form:option>
                      <form:option value="07"><spring:message code="label.payment.info.month.07"/></form:option>
                      <form:option value="08"><spring:message code="label.payment.info.month.08"/></form:option>
                      <form:option value="09"><spring:message code="label.payment.info.month.09"/></form:option>
                      <form:option value="10"><spring:message code="label.payment.info.month.10"/></form:option>
                      <form:option value="11"><spring:message code="label.payment.info.month.11"/></form:option>
                      <form:option value="12"><spring:message code="label.payment.info.month.12"/></form:option>
                    </form:select>
                  </div>
                  
                   <div id="yearDiv" style="float: left;">
                  <spring:message code="label.payment.info.expiry.year" htmlEscape="false" var="i18nCreditCardExpYear"/>
                  <form:select tabindex="107" cssStyle="margin-left: 10px;" path="creditCard.creditCardExpirationYear" title="${i18nCreditCardExpYear}" cssClass="smallselect">
                  <form:option value=""><spring:message code="label.choose"/></form:option>
                  <c:forEach items="${billingInfo.creditCardExpYearList}" var="choice" varStatus="status">
                    <form:option value="${choice}">
                       <c:out value="${choice}"/>
                    </form:option> 
                  </c:forEach>
                 </form:select>
               </div>
                </span>                
                <div class="main_addnew_formbox_errormsg_paymentinfo" id="creditCardExpirationMonthError" style="margin-left: 0px;"></div>
              </li>              
             
            </ul>
           </div>
        </div>
        <div class="creditcard_box front bot"></div>
    </div>
 </div>
  <div class="maincontent_equalverticalpanel" style="float:right;">
  <div class="creditcard_box front">
      <div class="creditcard_box back top"></div>
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
                 <form:select tabindex="108" path="creditCard.creditCardAddress.country" title="${i18nCreditCardCountry}" 

cssClass="select">
                  <option value=""><spring:message code="label.choose"/></option>
                <c:forEach items="${billingInfo.countryList}" var="choice" varStatus="status">
                  <option value="<c:out value="${choice.countryCode2}"/>">
                  <c:out value="${choice.name}" escapeXml="false"/>
                  </option> 
                </c:forEach>
                 </form:select> 
                </div>
                <div class="main_addnew_formbox_errormsg_paymentinfo" id="creditCard.creditCardAddress.countryError" style="margin-left: 132px;"></div>        

              </li>              
              <li>
                <span class="label"><spring:message code="label.payment.info.state"/></span>
                <div class="form_element" id="billingStateInput">
                  <div class="nonmandatory_wrapper">                                             
                   <form:input tabindex="109" path="creditCard.creditCardAddress.state" cssClass="text" 

title="${i18nCreditCardState}"/>
                  </div>
                  <div class="main_addnew_formbox_errormsg_paymentinfo" id="creditCard.creditCardAddress.stateError" style="margin-left: 132px;"></div>
                  <form:errors  element="label" path="creditCard.creditCardAddress.state" cssClass="error serverError"/> 
                </div>
                <div class="form_element" id="billingStateSelect" style="display:none">
                  <div id="otherstateDivCC" style="display:none">
                    </div>
                    <div id="JPstateDivCC">
                    </div>                
                 <div class="mandatory_wrapper">   
                   <select tabindex="110" id="billingAddressStateSelect" name="billingAddressStateSelect" class="select" 

title="<c:out value="${i18nCreditCardState}"/>"></select>
                 </div>                                 
                 <div class="main_addnew_formbox_errormsg_paymentinfo" id="billingAddressStateSelectError" style="margin-left: 132px;"></div>    
                </div>                                 
              </li>
              <li>
                <span class="label"><spring:message code="label.payment.info.address"/></span>
                <div class="mandatory_wrapper">                                           
                 <spring:message code="label.payment.info.address" var="i18nCreditCardAddress"/>
                 <form:input tabindex="111" path="creditCard.creditCardAddress.street1" title="${i18nCreditCardAddress}" 

cssClass="text"/>
                </div>
                <div class="main_addnew_formbox_errormsg_paymentinfo" id="creditCard.creditCardAddress.street1Error" style="margin-left: 132px;"></div>
                <form:errors  element="label" path="creditCard.creditCardAddress.street1" cssClass="error serverError"/>  
              </li>   
              <li>
                <span class="label"><spring:message code="label.payment.info.city"/></span>
                <div class="mandatory_wrapper">                                                   
                  <spring:message code="label.payment.info.city" var="i18nCreditCardCity"/>
                 <form:input  tabindex="112" path="creditCard.creditCardAddress.city" cssClass="text" 

title="${i18nCreditCardCity}"/>
                </div>
                <div class="main_addnew_formbox_errormsg_paymentinfo" id="creditCard.creditCardAddress.cityError" style="margin-left: 132px;"></div>
                <form:errors  element="label" path="creditCard.creditCardAddress.city" cssClass="error serverError"/>              
              </li>                                  
              <li>
                <span class="label"><spring:message code="label.payment.info.zip"/></span>
                <div class="mandatory_wrapper">                                                 
                 <spring:message code="label.payment.info.zip" var="i18nCreditCardPostalCode"/>
                 <form:input tabindex="113" path="creditCard.creditCardAddress.postalCode" title="${i18nCreditCardPostalCode}" 

cssClass="text"/>
                </div>
                <div class="main_addnew_formbox_errormsg_paymentinfo" id="creditCard.creditCardAddress.postalCodeError" style="margin-left: 132px;"></div>
                <form:errors element="label" path="creditCard.creditCardAddress.postalCode" cssClass="error serverError"/>         
              </li>
             </ul>
            </div>
        </div>
        <div class="creditcard_box back bot"></div>
    </div>
 </div> 
   <div class="commonbox_submitbuttonpanel">
          <div class="commonbox_submitbuttonbox">
                <p><a tabindex="121" href="javascript:history.back();" ><spring:message code="label.cancel"/></a></p>
                <input  id="addCreditCardButton" tabindex="120" type="submit" class="commonbutton submitmsg" value="<spring:message code="label.save"/>"/>         
          </div>
         </div>      
 <form:hidden path="action"/>
 </form:form> 
  </div> 
                                                                                                                           
</c:when>
<c:otherwise>
<div id="editCreditCardDiv">
  
 <div id="creditCardTypeDetailsDiv">
  <form:form commandName="billingInfo" id="billingForm" cssClass="ajaxform formPanel billingForm" action="${update_billing_path}" method="POST" onsubmit="editCreditCardDetails(event,this,'${tenant.param}', '${show_deposit_record_message}','${initialDepositAmount}');">
  <div class="maincontent_equalverticalpanel">
  <div class="creditcard_box front">
      <div class="creditcard_box front top"></div>
        <div class="creditcard_box front cardtypepanel">
          <h2><spring:message code="edit.payment.info.credit.card.title"/></h2>
            <div class="creditcard_typebox" id="from_edit">
              <c:forEach items="${billingInfo.ccTypes}" var="cc_choice" varStatus="status">
                      <c:if test="${empty billingInfo.creditCard.creditCardType}">
                            <c:if test="${status.index==0}">
                                <span id="<c:out value='${cc_choice.name}'/>" class="cc_icon <c:out value='${fn:toLowerCase(cc_choice.code)}'/> active edit_page"></span>
                                <c:set var="defaultCreditCardName" value="${cc_choice.name}"></c:set>
                            </c:if>
                              <c:if test="${status.index!=0}">
                                <span id="<c:out value='${cc_choice.name}'/>" class="cc_icon <c:out value='${fn:toLowerCase(cc_choice.code)}'/> nonactive edit_page"></span>
                              </c:if>
                      </c:if>
                      <c:if test="${not empty billingInfo.creditCard.creditCardType}">
                         <c:if test="${billingInfo.creditCard.creditCardType eq cc_choice.name}">
                            <span id="<c:out value='${cc_choice.name}'/>" class="cc_icon <c:out value='${fn:toLowerCase(cc_choice.code)}'/> active edit_page"></span>
                            <c:set var="defaultCreditCardName" value="${cc_choice.name}"></c:set>
                         </c:if>
                          <c:if test="${billingInfo.creditCard.creditCardType ne cc_choice.name}">
                            <span id="<c:out value='${cc_choice.name}'/>" class="cc_icon <c:out value='${fn:toLowerCase(cc_choice.code)}'/> nonactive edit_page"></span>
                          </c:if>
                      </c:if>
               </c:forEach>
            </div>
        </div>
   <input id="enableEdit" value="false" style="display: None">
   <div class="creditcard_box front mid">
          <div class="creditcard_box front mid contentarea ">
         
            <ul>
             <li style="display: None">
                    <input class="text" id="defaultCreditCardNameHidden"  value="<c:out value='${defaultCreditCardName}'/>"/>
                   <form:input autocomplete="off" tabindex="101" path="creditCard.creditCardType" cssClass="text" value="${defaultCreditCardName}"/>
              </li>
            <li style="width:200px;">
                <span class="label"><spring:message code="label.payment.info.first.name.on.card"/></span><div class="mandatory_wrapper withlabel" style="display:none;"></div>
                <div class="commonboxes_formbox_withouttextbox ccbox read"><c:out value="${billingInfo.creditCard.firstNameOnCard}"></c:out></div>
                <div> 
                <div class="write">                                            
                  <form:input cssClass="text" tabindex="102" id="creditCard.firstNameOnCard" path="creditCard.firstNameOnCard" title=""/>
                  <span class="errormsg" id="creditCard.firstNameOnCardError"></span>
                </div>
                </div>
                
              </li>
              <li style="width:200px; float:right;">
                <span class="label"><spring:message code="label.payment.info.last.name.on.card"/></span><div class="mandatory_wrapper withlabel" style="display:none;"></div>
                <div class="commonboxes_formbox_withouttextbox ccbox read"><c:out value="${billingInfo.creditCard.lastNameOnCard}"></c:out></div>
                <div class="write">                                          
                  <form:input cssClass="text" tabindex="103" id="creditCard.lastNameOnCard" path="creditCard.lastNameOnCard" title=""/>
				            <span class="errormsg" id="creditCard.lastNameOnCardError"></span>
                </div>
                
              </li>
              
              <li style="width:250px;">
                <span class="label"><spring:message code="label.payment.info.card.number"/></span><div class="mandatory_wrapper withlabel" style="display:none;"></div>
                  <div class="commonboxes_formbox_withouttextbox ccbox read"><c:out value="${billingInfo.creditCard.creditCardNumber}"></c:out></div>
                    <div class="write">                         
                    <form:input autocomplete="off" tabindex="104" path="creditCard.creditCardNumber" title="0000 0000 0000 0000" cssClass="text"/>
					           <span class="errormsg" id="creditCard.creditCardNumberError"></span>
                    </div>
                  
              </li>            
                <li style="width:160px; float:right;">
                <span class="label"><spring:message code="label.payment.info.cvv"/></span><div class="mandatory_wrapper withlabel" style="display:none;"></div> <a class="helpicon js_cvv_help" style="margin:0 0 0 5px;" data-toggle="popover" data-trigger="hover" data-placement="right" data-container="body" data-content='<spring:message code="message.payment.info.cvv.help"/>' href="javascript:void(0);"></a>
                  <div class="commonboxes_formbox_withouttextbox ccbox read"><c:out value="${billingInfo.creditCard.creditCardCVV}"></c:out></div>
 					 <div class="write"> 
                    <form:password autocomplete="off" tabindex="105" size="4" maxlength="4" path="creditCard.creditCardCVV" title="" cssClass="text j_credit_card_cvv" />
                   <span class="errormsg" id="creditCard.creditCardCVVError"></span>
               		</div>
                    
              </li>
              <li style="width:260px;">
                <span class="label"><spring:message code="label.payment.info.expiry"/></span><div class="mandatory_wrapper withlabel" style="display:none;"></div>
                 <div class="commonboxes_formbox_withouttextbox ccbox read"></div>
                 
                 <span class="expirationwrapper write">                     
                 <div id="monthDiv" style="float: left;margin-right: 10px;">
                    <spring:message code="label.payment.info.expiry.month" htmlEscape="false" var="i18nCreditCardExpMonth"/>
                    <form:select tabindex="106" path="creditCard.creditCardExpirationMonth" title="${i18nCreditCardExpMonth}" cssClass="smallselect" >
                      <form:option value=""><spring:message code="label.choose"/></form:option>
                      <form:option value="01"><spring:message code="label.payment.info.month.01"/></form:option>
                      <form:option value="02"><spring:message code="label.payment.info.month.02"/></form:option>
                      <form:option value="03"><spring:message code="label.payment.info.month.03"/></form:option>
                      <form:option value="04"><spring:message code="label.payment.info.month.04"/></form:option>
                      <form:option value="05"><spring:message code="label.payment.info.month.05"/></form:option>
                      <form:option value="06"><spring:message code="label.payment.info.month.06"/></form:option>
                      <form:option value="07"><spring:message code="label.payment.info.month.07"/></form:option>
                      <form:option value="08"><spring:message code="label.payment.info.month.08"/></form:option>
                      <form:option value="09"><spring:message code="label.payment.info.month.09"/></form:option>
                      <form:option value="10"><spring:message code="label.payment.info.month.10"/></form:option>
                      <form:option value="11"><spring:message code="label.payment.info.month.11"/></form:option>
                      <form:option value="12"><spring:message code="label.payment.info.month.12"/></form:option>
                    </form:select>
                </div>
                                          
                 <div id="yearDiv" style="float: left;">
                  <spring:message code="label.payment.info.expiry.year" htmlEscape="false" var="i18nCreditCardExpYear"/>
                  <form:select tabindex="107" cssStyle="margin-left: 10px;" path="creditCard.creditCardExpirationYear" title="${i18nCreditCardExpYear}" cssClass="smallselect">
                  <form:option value=""><spring:message code="label.choose"/></form:option>
                  <c:forEach items="${billingInfo.creditCardExpYearList}" var="choice" varStatus="status">
                    <form:option value="${choice}">
                       <c:out value="${choice}"/>
                    </form:option> 
                  </c:forEach>
                 </form:select>
               </div>
                               
                <span class="errormsg" id="creditCardExpirationMonthError"></span>
                </span>
              </li>
            
            
            </ul>
      </div>
    </div>
    <div class="creditcard_box front bot">
      <div id="editCreditCardTypeDetails" class="commonbox_submitbuttonbox read">
        <p><a href="javascript:void(0);"><spring:message code="label.edit"/></a></p>
      </div>
      <div class="commonbox_submitbuttonbox write">
        <p><a tabindex="109" id="editCreditCardDetailsCancel" href="javascript:void(0);"><spring:message code="label.editcurrent.cancel"/></a></p>
        <input id="editCreditCardDetailsSaveButton" tabindex="108" type="submit" rel="<spring:message code="label.saving"/>" class="commonbutton submitmsg" value="<spring:message code="label.save"/>"/>
      </div>
    </div>
  </div>
   </div>
     
   </form:form>
  </div>
  
  <div id="showCreditCardDetailsDiv">
  <form:form commandName="billingInfo" id="billingAddressForm" cssClass="ajaxform formPanel billingForm" action="${update_billing_path}" method="POST" onsubmit="editCreditCardDetails(event,this,'${tenant.param}', '${show_deposit_record_message}','${initialDepositAmount}')">      
      <div class="maincontent_equalverticalpanel" style="float:right;">
  <div class="creditcard_box front">
      <div class="creditcard_box back top"></div>
        <div class="creditcard_box back mid">
          <div class="creditcard_box back mid titlearea">
                  <span class="address_icon"></span>
                    <h2><spring:message code="edit.payment.info.billing.address.title"/></h2>
            </div>
          <div class="creditcard_box back mid contentarea">
    
    <input type="hidden" id="ccerrormessage" name="ccerrormessage" value="<c:out value="${errormsg}"></c:out>"/>
    <div id='creditcard_update_error' class='common_messagebox error'  style='display:none'><span class="erroricon"></span>
    <p id="p_message"></p>
      </div>  
    
            <ul>
              <li>
                <span class="label"><spring:message code="label.payment.info.country"/></span>
                <div class="commonboxes_formbox_withouttextbox ccboxback read"><c:out value="${billingInfo.creditCard.creditCardAddress.countryName}"></c:out></div>
                <div class="mandatory_wrapper write">                                               
                 <spring:message code="label.payment.info.country" var="i18nCreditCardCountry"/>
                 <form:select tabindex="110" path="creditCard.creditCardAddress.country" title="${i18nCreditCardCountry}" cssClass="select">
                <c:forEach items="${billingInfo.countryList}" var="choice" varStatus="status">
                  <option value='<c:out value="${choice.countryCode2}"/>' 
                          <c:if test='${billingInfo.creditCard.creditCardAddress.country == choice.countryCode2}'>selected="selected"</c:if>>
                    <c:out value="${choice.name}" escapeXml="false"/>
                 </option>
                </c:forEach>
                 </form:select> 
                </div>
                <span class="errormsg" id="creditCard.creditCardAddress.countryError"></span>                       
              </li>              
              <li>
                  <spring:message code="label.payment.info.state" var="i18nCreditCardState"/>
                  <span class="label"><spring:message code="label.payment.info.state"/></span>
                  <div class="form_element" id="billingStateInput">
                    <div class="mandatory_wrapper write">                                             
                     <form:input tabindex="111" path="creditCard.creditCardAddress.state" cssClass="text" title="${i18nCreditCardState}"/>
                      </div>
                      <form:errors  element="label" path="creditCard.creditCardAddress.state" cssClass="error serverError"/>
                  </div>
                  <div class="form_element" id="billingStateSelect" style="display:none">
                   <div id="otherstateDivCC" style="display:none">
                    </div>
                    <div id="JPstateDivCC">
                    </div>   
                   <div class="mandatory_wrapper write">   
                     <select tabindex="112" id="billingAddressStateSelect" name="billingAddressStateSelect" class="select" title="<c:out value="${i18nCreditCardState}"/>"></select>
                   </div>                                 
                   <div class="main_addnew_formbox_errormsg_paymentinfo" id="billingAddressStateSelectError" style="margin-left: 132px;"></div>    
                  </div>
                  <div id="billingCCStateInputReadHidden" style="display:none;"><c:out value="${billingInfo.creditCard.creditCardAddress.state}"/></div>
                  <div class="commonboxes_formbox_withouttextbox ccboxback read" id="state_readvalue">
                        <c:choose>
                          <c:when test="${billingInfo.creditCard.creditCardAddress.country == 'JP' || billingInfo.creditCard.creditCardAddress.country == 'US' || billingInfo.creditCard.creditCardAddress.country == 'AU' || billingInfo.creditCard.creditCardAddress.country == 'IN'}">
                            <spring:message code="${billingInfo.creditCard.creditCardAddress.country}.${billingInfo.creditCard.creditCardAddress.state}"/>
        </c:when>
        <c:otherwise>
                            <c:out value="${billingInfo.creditCard.creditCardAddress.state}"/>
        </c:otherwise>
                        </c:choose>
                      </div>
                    <span class="errormsg" id="creditCard.creditCardAddress.stateError"></span>
                    <div class="read" id="dummy"></div>
                </li>
              
              <li>
              <span class="label"><spring:message code="ui.accounts.all.header.streetaddress"/></span>
                <div class="commonboxes_formbox_withouttextbox ccboxback read"><c:out value="${billingInfo.creditCard.creditCardAddress.street1}"></c:out></div>
                <div class="mandatory_wrapper write">                                           
                 <spring:message code="label.payment.info.address" var="i18nCreditCardAddress"/>
                 <form:input tabindex="113" path="creditCard.creditCardAddress.street1" title="${i18nCreditCardAddress}" cssClass="text"/>
                </div>
                <span class="errormsg" id="creditCard.creditCardAddress.street1Error"></span>
                <form:errors  element="label" path="creditCard.creditCardAddress.street1" cssClass="error serverError"/>  
              </li>
              
               <li>
                  <span class="label"><spring:message code="label.payment.info.city"/></span>
                  <div class="commonboxes_formbox_withouttextbox ccboxback read"><c:out value="${billingInfo.creditCard.creditCardAddress.city}"></c:out></div>
                  <div class="mandatory_wrapper write">                                                   
                   <spring:message code="label.payment.info.city" var="i18nCreditCardCity"/>
                   <form:input tabindex="114" path="creditCard.creditCardAddress.city" cssClass="text" title="${i18nCreditCardCity}"/>
                  </div>
                  <span class="errormsg" id="creditCard.creditCardAddress.cityError"></span>
                  <form:errors  element="label" path="creditCard.creditCardAddress.city" cssClass="error serverError"/>                               
                </li>                        
                                             
                <li>
                  <span class="label"><spring:message code="label.payment.info.zip"/></span>
                  <div class="commonboxes_formbox_withouttextbox ccboxback read"><c:out value="${billingInfo.creditCard.creditCardAddress.postalCode}"></c:out></div>
                  <div class="mandatory_wrapper write">                                                 
                   <spring:message code="label.payment.info.zip" var="i18nCreditCardPostalCode"/>
                   <form:input tabindex="115" path="creditCard.creditCardAddress.postalCode" title="${i18nCreditCardPostalCode}" cssClass="text"/>
                  </div>
                  <span class="errormsg" id="creditCard.creditCardAddress.postalCodeError"></span>
                  <form:errors element="label" path="creditCard.creditCardAddress.postalCode" cssClass="error serverError"/>                  
                </li>                      
            
           
               </ul>
            </div>
                                                                                                                          
            </div>
        <div class="creditcard_box back bot">
          <div id="editCreditCardAddress" class="commonbox_submitbuttonbox read">
            <p><a href="javascript:void(0);"><spring:message code="label.edit"/></a></p>
          </div>
          <div class="commonbox_submitbuttonbox write">
            <p><a tabindex="123" id="editCreditCardAddressCancel" href="javascript:void(0);"><spring:message code="label.editcurrent.cancel"/></a></p>
            <input id="editCreditCardAddressSaveButton" tabindex="122" type="submit" rel="<spring:message code="label.saving"/>" class="commonbutton submitmsg" value="<spring:message code="label.save"/>"/>
          </div>
        </div>
            </div>
     
      </div>
        
      
       </form:form>
 </div>
  
  
  </div>
</c:otherwise>
</c:choose>
<script type="text/javascript">
$(function() {
    $(".js_cvv_help").popover();
  });

</script>

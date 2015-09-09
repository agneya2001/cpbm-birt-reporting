<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/tenants.js"></script>
<style>
.ui-datepicker-trigger {
    margin-left : 5px;
    margin-top : 5px;
}
</style>

 <!--  Edit ProductBundle starts here-->
<div class="dialog_formcontent">
  <!-- Title -->
    
    <spring:url value="/portal/tenants/edit" var="edit_tenant_path" htmlEscape="false" /> 
    <form:form commandName="tenantForm" cssClass="ajaxform" id="tenantEditForm" tenantid="" action="${edit_tenant_path}" onsubmit="editTenant(event,this)">
     <!-- Edit fields -->
      <ul>
          <li>
            <label for="name"><spring:message code="ui.accounts.all.header.name"/></label>
            <div class="mandatory_wrapper">
              <div class="nonmandatory_wrapper">&nbsp;&nbsp;&nbsp;</div>
              <form:input cssClass="text"  path="tenant.name" tabindex="1"/>
            </div>
            <div class="main_addnew_formbox_errormsg edit_error_msg" id="tenant.nameError" ></div>
          </li>     
          <li>
            <label for="name"><spring:message code="ui.accounts.all.header.discount.percent"/></label>
            <div class="nonmandatory_wrapper">
              <div class="nonmandatory_wrapper">&nbsp;&nbsp;&nbsp;</div>
              <form:input cssClass="text" path="tenant.tenantExtraInformation.discountPercent" tabindex="2"></form:input>
            </div>
             <div class="main_addnew_formbox_errormsg edit_error_msg" id="tenant.tenantExtraInformation.discountPercentError" ></div>
          </li>
          <li> 
            <label for="name"><spring:message code="ui.accounts.all.header.spendlimit"/>(<c:out value="${tenantForm.tenant.currency.sign}" />)</label>
             <div class="nonmandatory_wrapper">
                    <div class="nonmandatory_wrapper">&nbsp;&nbsp;&nbsp;</div>
                    <form:input cssClass="text" path="tenant.spendLimit" tabindex="3"></form:input>
                    </div>
             <div class="main_addnew_formbox_errormsg edit_error_msg" id="tenant.spendLimitError" ></div>
          </li>       
          <li>
            <label for="name"><spring:message code="ui.accounts.all.header.country"/></label>
            
             <div class="mandatory_wrapper">
              <div class="nonmandatory_wrapper">&nbsp;&nbsp;&nbsp;</div>
               <form:select cssClass="text" path="tenant.address.country">
                  <c:forEach items="${tenantForm.countryList}" var="choice" varStatus="status">
                    <form:option value="${choice.countryCode2}">
                      <c:out value="${choice.name}" escapeXml="false"/>
                    </form:option>
                  </c:forEach>
               </form:select>
             </div>
             <div class="main_addnew_formbox_errormsg edit_error_msg" id="tenant.address.countryError" ></div>
          </li>

          <li>
            <label for="name"><spring:message code="ui.accounts.all.header.streetaddress"/></label>
            
              <div class="mandatory_wrapper">
                <div class="nonmandatory_wrapper">&nbsp;&nbsp;&nbsp;</div>
                <form:input cssClass="text" path="tenant.address.street1" tabindex="2"></form:input>
              </div>
            
            <div class="main_addnew_formbox_errormsg edit_error_msg" id="tenant.address.street1Error" ></div>
          </li>
    
          <li>
            <label for="name"><spring:message code="ui.accounts.all.header.streetaddress2"/></label>
              <div class="nonmandatory_wrapper">
              <div class="nonmandatory_wrapper">&nbsp;&nbsp;&nbsp;</div>            
                <form:input cssClass="text" path="tenant.address.street2" tabindex="2"></form:input>
              </div>
            
          </li>

          <li>
            <label for="name"><spring:message code="ui.accounts.all.header.city"/></label>
              <div class="mandatory_wrapper">
                <div class="nonmandatory_wrapper">&nbsp;&nbsp;&nbsp;</div>
                <form:input cssClass="text" path="tenant.address.city" tabindex="2"></form:input>
              </div>
            
            <div class="main_addnew_formbox_errormsg edit_error_msg" id="tenant.address.cityError" ></div>
          </li>
             
           <li>
            <label for="name"><spring:message code="label.moreUserInfo.state"/></label>
            
            <div class="mandatory_wrapper">
              
              <div id="stateInput">
                <div class="nonmandatory_wrapper">&nbsp;&nbsp;&nbsp;</div>   
                <spring:message code="label.moreUserInfo.state.tooltip" var="i18nStateTooltip"/>
                <form:input cssClass="text" tabindex="2" path="tenant.address.state" title="${i18nStateTooltip}"/>
              </div> 
              <div  id="stateSelect" style="display:none">
                <div class="nonmandatory_wrapper">&nbsp;&nbsp;&nbsp;</div>
                <select class="text" tabindex="2" id="tenantAddressStateSelect" name="tenantAddressStateSelect" title="<spring:message code="label.moreUserInfo.state.tooltip"/>"></select>
              </div> 
            </div>
            <div class="main_addnew_formbox_errormsg edit_error_msg" id="tenant.address.stateError" ></div>
           </li>
                           
           <li>
            <label for="name"><spring:message code="ui.accounts.all.header.postalcode"/></label>
              <div class="mandatory_wrapper">
                <div class="nonmandatory_wrapper">&nbsp;&nbsp;&nbsp;</div>
                <form:input cssClass="text" path="tenant.address.postalCode" maxLength="25" tabindex="2"></form:input>
              </div>
            <div class="main_addnew_formbox_errormsg edit_error_msg" id="tenant.address.postalCodeError" ></div>
           </li> 
            
            <li>
            <label for="name"><spring:message code="ui.label.tenant.view.UserLimit"/></label>
              <div class="mandatory_wrapper">
              <div class="nonmandatory_wrapper">&nbsp;&nbsp;&nbsp;</div>
                <form:input cssClass="text" path="tenant.maxUsers" tabindex="2"></form:input>
               </div>
            <div class="main_addnew_formbox_errormsg edit_error_msg" id="tenant.maxUsersError"></div>
            </li>
                       
           <li <c:if test="${tenantForm.tenant.secondaryAddress != null || !allowSecondaryCheckBox}">style="display:none"</c:if>>
            <label for="name"><spring:message code="ui.accounts.all.header.add.secondary.address"/></label>
            <div class="nonmandatory_wrapper">
              <div class="nonmandatory_wrapper">&nbsp;&nbsp;&nbsp;</div>
                <form:checkbox id="allowSecondaryId" path="allowSecondary" class="checkbox"/>
            </div>
           </li>
           </ul>
            <div id="secondaryAddressDetailsDiv" <c:if test="${tenantForm.tenant.secondaryAddress == null}">style="display:none"</c:if> >
              <ul>
                  <li style="width:100%;margin-top:20px;">
                    <label for="name" style="font-weight:bold;"><spring:message code="ui.accounts.all.billing.address"/></label>
                  </li>
                  <li>
                  <label for="name"><spring:message code="ui.accounts.all.header.add.secondary.syncaddress"/></label>
                  <div class="nonmandatory_wrapper">
                    <div class="nonmandatory_wrapper">&nbsp;&nbsp;&nbsp;</div>
                      <input type="checkbox" class="checkbox " id="syncAddressEdit" tabindex="23" name="syncAddressEdit" >
                    </div>
                  </li>

                  <li>
                  <label for="name"><spring:message code="ui.accounts.all.header.country"/></label>
                  
                   <div class="mandatory_wrapper">
                    <div class="nonmandatory_wrapper">&nbsp;&nbsp;&nbsp;</div>
                     <form:select cssClass="text" path="tenant.secondaryAddress.country">
                        <c:forEach items="${tenantForm.countryList}" var="choice" varStatus="status">
                          <form:option value="${choice.countryCode2}">
                            <c:out value="${choice.name}" escapeXml="false"/>
                          </form:option>
                        </c:forEach>
                     </form:select>
                   </div>
                   <div class="main_addnew_formbox_errormsg edit_error_msg" id="tenant.secondaryAddress.countryError" ></div>
                  
                </li>
      
                <li>
                  <label for="name"><spring:message code="ui.accounts.all.header.streetaddress"/></label>
                  
                    <div class="mandatory_wrapper">
                      <div class="nonmandatory_wrapper">&nbsp;&nbsp;&nbsp;</div>
                      <form:input cssClass="text" path="tenant.secondaryAddress.street1" tabindex="2"></form:input>
                    </div>
                  
                  <div class="main_addnew_formbox_errormsg edit_error_msg" id="tenant.secondaryAddress.street1Error" ></div>
                </li>
          
                <li>
                  <label for="name"><spring:message code="ui.accounts.all.header.streetaddress2"/></label>
                  
                    <div class="nonmandatory_wrapper">
                      <div class="nonmandatory_wrapper">&nbsp;&nbsp;&nbsp;</div>            
                      <form:input cssClass="text" path="tenant.secondaryAddress.street2" tabindex="2"></form:input>
                    </div>
                  
                </li>
      
                <li>
                  <label for="name"><spring:message code="ui.accounts.all.header.city"/></label>
                  
                    <div class="mandatory_wrapper">
                      <div class="nonmandatory_wrapper">&nbsp;&nbsp;&nbsp;</div>
                      <form:input cssClass="text" path="tenant.secondaryAddress.city" tabindex="2"></form:input>
                    </div>
                  
                  <div class="main_addnew_formbox_errormsg edit_error_msg" id="tenant.secondaryAddress.cityError" ></div>
                </li>
                   
                 <li>
                    <label for="name"><spring:message code="label.moreUserInfo.state"/></label>
                    <div class="mandatory_wrapper">
                    
                    <div id="stateSecondaryInput">
                      <div class="nonmandatory_wrapper">&nbsp;&nbsp;&nbsp;</div>
                      <spring:message code="label.moreUserInfo.state.tooltip" var="i18nStateTooltip"/>
                      <form:input cssClass="text" tabindex="2" path="tenant.secondaryAddress.state" title="${i18nStateTooltip}"/>
                    </div> 
                    <div  id="stateSecondarySelect" style="display:none">
                      <div class="nonmandatory_wrapper">&nbsp;&nbsp;&nbsp;</div>
                      <select class="text" tabindex="2" id="tenantSecondaryAddressStateSelect" name="tenantSecondaryAddressStateSelect" title="<spring:message code="label.moreUserInfo.state.tooltip"/>"></select>
                    </div> 
                    </div>
                    <div class="main_addnew_formbox_errormsg edit_error_msg" id="tenant.secondaryAddress.stateError" ></div>
                 </li>                    
                  
      
                  <li>
                  <label for="name"><spring:message code="ui.accounts.all.header.postalcode"/></label>
                  
                    <div class="mandatory_wrapper">
                      <div class="nonmandatory_wrapper">&nbsp;&nbsp;&nbsp;</div>
                      <form:input cssClass="text" path="tenant.secondaryAddress.postalCode" maxLength="25" tabindex="2"></form:input>
                    </div>
                  
                  <div class="main_addnew_formbox_errormsg edit_error_msg" id="tenant.secondaryAddress.postalCodeError" ></div>
                 </li>
              </ul> 
           </div>
           <ul>
           <tiles:insertDefinition name="tenant.custom.fields"></tiles:insertDefinition>
           </ul>
          
                                  
    <div class="main_addnew_formbox_errormsg" id="miscFormErrors" style="margin:10px 0 0 5px"></div>
    <div class="main_addnew_submitbuttonpanel">
      <div class="main_addnew_submitbuttonbox">
       <a id="edittenantcancel" onclick="closeChangeEditDialog(this);" style="cursor:pointer;"><spring:message code="label.cancel"/></a>
       <input tabindex="210" id="editTenantSave"  class="commonbutton submitmsg" rel="<spring:message code="ui.accounts.all.save.in.action"/>" type="submit" value="<spring:message code="ui.accounts.all.save"/>"/>
       
       </div>
    </div>
    
    </form:form>
</div>
<!--  Edit Tenant ends here-->

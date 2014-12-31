<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<script type="text/javascript">
var confirmation_msg = '<spring:message javaScriptEscape="true" code="ui.account.successfully.completed.text"/>';
var currentstep ="";
var $currentDialog="";
var product_action="";
  var tenanturl = "<%=request.getContextPath() %>/portal/tenants/";
  $(document).ready(function() {
    currentstep = "step1";
    product_action = "create";
    $(".j_accounttype").each(function(idx, i) {
      var accountType = $(i);
      var isSelected = accountType.attr("defaultRegistered");
      if(isSelected == "true") {
        accountType.click();
        return false;
      }
    });
  });
</script>
<!--  from old -->
<c:if test="${signuperror != null && signuperror=='emaildomainblacklisted'}">
      <div class="common_messagebox success"><p><spring:message code="signup.emaildomain.blacklist.error"/></p></div>
    </c:if>

<jsp:include page="../shared/state_to_statecodes.jsp"></jsp:include>
    
<spring:url value="/portal/tenants" var="create_tenant_path" htmlEscape="false" />  
<form:form commandName="account" id="accountForm" cssClass="formPanel" action="${create_tenant_path}">
<!--  Add new Account starts here-->
<div class="widget_wizardcontainer sixstepswizard">
    <!--step 1 starts here-->
  <div id="step1" class="j_tenantspopup">
    <input type="hidden" id="nextstep" name="nextstep" value="step2" >
  <input type="hidden" id="prevstep" name="prevstep" value="" >
        <div class="widgetwizard_stepsbox">
            <div class="widgetwizard_steps_contentcontainer sixstepswizard">
                <div class="widgetwizard_stepscenterbar sixstepswizard">
                    <ul>
                        <li class="widgetwizard_stepscenterbar fivestepswizard first"><span class="steps active"><span class="stepsnumbers active">1</span></span><span class="stepstitle active"><spring:message htmlEscape="false" code="ui.account.step1.title"/></span></li>
                        <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps"><span class="stepsnumbers">2</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.account.step2.title"/></span></li>
                        <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps"><span class="stepsnumbers">3</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.account.step3.title"/></span></li>
                        <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps"><span class="stepsnumbers">4</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.account.step4.title"/></span></li>
                        <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps"><span class="stepsnumbers">5</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.account.step5.title"/></span></li>
                        <li class="widgetwizard_stepscenterbar fivestepswizard last"><span class="steps last"><span class="stepsnumbers last">6</span></span><span class="stepstitle last"><spring:message htmlEscape="false" code="ui.account.step6.title"/></span></li>
                    </ul>
                </div>
            </div>
        </div>
        <div class="widgetwizard_contentarea sixstepswizard ">
            <div class="widgetwizard_boxes sixstepswizard fullheight2">
                <div class="widgetwizard_titleboxes">
                    <h2><spring:message code="label.tenants.channel"/></h2>
                    <span><spring:message code="label.tenants.channelDescription"/></span>
                </div>
                <div class="widgetwizard_selectionbox" style="background:none; border:none;">
                    <ul>
                      <li class="widgetwizard_selectionbox" style="background:none; border:none;">
						<span class="channelicon"></span>
                        <spring:message code="label.channel.tooltip" var="i18nChannelTooltip"/>
                        <form:select class="channelselect" path="channelParam" title="${i18nChannelTooltip}" data-selected="${defaultChannel.name}">
                          <c:forEach items="${channels}" var="choice" varStatus="status">
                            <form:option value="${choice.param}" cssStyle="text-overflow: ellipsis; overflow:hidden;">
                              <c:out value="${choice.name}"></c:out>
                            </form:option> 
                          </c:forEach>
                        </form:select>
                        <div class="main_addnew_formbox_errormsg_popup channelParamError" id="channelParamError" ></div>
                      </li>
                    </ul>
                </div>
            </div>
            <div class="widgetwizard_boxes sixstepswizard" style="margin-top:10px;">
                <div class="widgetwizard_titleboxes">
                    <h2><spring:message code="label.tenants.accountType"/></h2>
                    <span><spring:message code="ui.account.accountType.title.desc"/></span>
                </div>
                <div class="widgetwizard_selectionbox sixstepswizard producttupe ">
                    <ul>
                         <c:set var="accounttypes" value="${account.accountTypes}" scope="session" />
                         <c:forEach items="${account.accountTypes}" var="choice" varStatus="status">
                            <li class="widgetwizard_selectionbox  j_accounttype" defaultRegistered="<c:out value='${choice.defaultRegistered}' />" id='accountTypeId' onclick="changeAccountType1(this);" >
                              <div class="widget_radiobuttons">
                                  <span class="unchecked">
                                    <form:radiobutton  cssClass="radiobuttons_hidden" path="accountTypeId" value="${choice.id}" />
                                  </span>
                              </div>                       
                              <span class="description j_description"><spring:message code="registration.accounttype.${choice.nameLower}"/></span>
                              <c:if test="${choice.id == 1}">
                                <span class="account_systemicon"></span>
                              </c:if>
                              <c:if test="${choice.id == 3}">
                                <span class="account_retailicon"></span>
                              </c:if>
                              <c:if test="${choice.id == 4}">
                                <span class="account_corporateicon"></span>
                              </c:if>
                              <c:if test="${choice.id == 5}">
                                <span class="account_trialicon"></span>
                              </c:if>
                              <div class="main_addnew_formbox_errormsg_popup" id="accountTypeIdError"></div>
                            </li>
                          </c:forEach>                              
                    </ul>
                </div>
            </div>
        </div>
        <div id="buttons" class="widgetwizard_nextprevpanel sixstepswizard">
            <input class="widgetwizard_nextprevpanel nextbutton" data-primary type="button" onclick="addTenantNext(this)" value="<spring:message code="label.next.step"/>" name="<spring:message code="label.next.step"/>" id="add_tenant_next1">
        </div>
    </div>
    <!--step 1 ends here-->
    
    
   <!--step 2 starts here-->
  <div id="step2" class="j_tenantspopup" style="display:none;">
  <input type="hidden" id="nextstep" name="nextstep" value="step3" >
  <input type="hidden" id="prevstep" name="prevstep" value="step1" >
        <div class="widgetwizard_stepsbox">
            <div class="widgetwizard_steps_contentcontainer sixstepswizard">
                <div class="widgetwizard_stepscenterbar sixstepswizard">
                    <ul>
                        <li class="widgetwizard_stepscenterbar fivestepswizard first"><span class="steps completedsteps"><span class="stepsnumbers">1</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.account.step1.title"/></span></li>
                        <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps active"><span class="stepsnumbers active">2</span></span><span class="stepstitle active"><spring:message htmlEscape="false" code="ui.account.step2.title"/></span></li>
                        <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps"><span class="stepsnumbers">3</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.account.step3.title"/></span></li>
                        <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps"><span class="stepsnumbers">4</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.account.step4.title"/></span></li>
                        <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps"><span class="stepsnumbers">5</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.account.step5.title"/></span></li>
                        <li class="widgetwizard_stepscenterbar fivestepswizard last"><span class="steps last"><span class="stepsnumbers last">6</span></span><span class="stepstitle last"><spring:message htmlEscape="false" code="ui.account.step6.title"/></span></li>
                    </ul>
                </div>
            </div>
        </div>
        <div class="widgetwizard_contentarea sixstepswizard">
            <div class="widgetwizard_boxes sixstepswizard fullheight2">
                <div class="widgetwizard_titleboxes">
                    <h2><spring:message code="ui.account.enter.master.user.deteails.title"/></h2>
                    <span><spring:message code="ui.account.enter.master.user.deteails.title.desc"/></span>
                </div>
                <div class="widgetwizard_detailsbox sixstepswizard">
                    <ul>
                       <li>
                          <span class="label"><form:label  path="user.username"><spring:message code="label.tenants.username"/></form:label></span>
                          <div class="mandatory_wrapper">
                            <spring:message code="label.userInfo.username.tooltip" var="i18nUsernameTooltip"/>
                            <form:input cssClass="text" path="user.username" tabindex="104" title="${i18nUsernameTooltip}" id="user.username"/>
                          </div>
                          <c:if test="${showImportAdButton=='true'}">
                            <input class="basic_button adimport" type="button" onclick="importAdUserToAddAccount(this)"  value="<spring:message code="label.tenants.uername.import.ds.button"/>"  name="<spring:message code="label.tenants.uername.import.ds.button"/>" id="import_user_info_from_ad" disabled="disabled"/>
                            <input type="hidden" id="ad_import_enabled" class="basic_button adimport" value="ad_import_enabled"/>
                           </c:if>
                          <div class="main_addnew_formbox_errormsg_popup" id="user.usernameError"></div>
                           
                        </li>
                        <div class="user_name_order">
                          <div class="user_first_name">
                            <li>
                              <span class="label"><form:label path="user.firstName"><spring:message code="label.tenants.firstName"/></form:label></span>
                              <div class="mandatory_wrapper">
                                <spring:message code="label.userInfo.firstName.tooltip" var="i18nFirstNameTooltip"/>
                                <form:input cssClass="text" tabindex="105" path="user.firstName" title="${i18nFirstNameTooltip}"/>
                              </div>
                              <div class="main_addnew_formbox_errormsg_popup" id="user.firstNameError"></div>
                            </li>
                          </div>
                          <div class="user_last_name">
                            <li>
                              <span class="label"><form:label path="user.lastName"><spring:message code="label.tenants.lastName"/></form:label></span>
                              <div class="mandatory_wrapper">
                                <spring:message code="label.userInfo.lastName.tooltip" var="i18nLastNameTooltip"/>
                                <form:input cssClass="text" tabindex="106" path="user.lastName" title="${i18nLastNameTooltip}"/>
                              </div>
                              <div class="main_addnew_formbox_errormsg_popup" id="user.lastNameError"></div>
                            </li>
                          </div>
                        </div>
                        
                        <li>
                          <span class="label"><form:label path="user.email" ><spring:message code="label.tenants.email"/></form:label></span>
                          <div class="mandatory_wrapper">
                            <spring:message code="label.userInfo.yourEmail.tooltip" var="i18nEmailTooltip"/>
                            <form:input  cssClass="text" tabindex="107" path="user.email" title="${i18nEmailTooltip}"/>
                          </div>
                          <div class="main_addnew_formbox_errormsg_popup" id="user.emailError"></div>
                        </li>
                        
                        <li>
                          <span class="label"><form:label  path="confirmEmail"><spring:message code="label.tenants.confirmEmail"/></form:label></span>
                          <div class="mandatory_wrapper">
                            <spring:message code="label.userInfo.confirmEmail.tooltip" var="i18nConfirmEmailTooltip"/>
                            <form:input cssClass="text" path="confirmEmail" tabindex="108" title="${i18nConfirmEmailTooltip}" id="confirmEmail"/><c:out value="${confirmEmail}"></c:out>
                          </div>
                          <div class="main_addnew_formbox_errormsg_popup" id="confirmEmailError"></div>
                        </li>
                        <tiles:insertDefinition name="user.custom.fields"></tiles:insertDefinition>
                        <li id ="trialCodeBox">
                          <span class="label" ><form:label  path="trialCode"><spring:message code="label.userInfo.promo.code"/></form:label></span>
                            <spring:message code="label.userInfo.promo.code.tooltip" var="i18nUsernameTooltip"/>
                            <form:input cssClass="text" path="trialCode" tabindex="109" title="${i18nUsernameTooltip}" id="trialCode"/>
                          <div class="label.userInfo.promo.code" id="trialCodeError"></div>
                        </li>
                        
                    </ul>
                </div>
            </div>
            <div id="spinning_wheel2" style="display:none;">
                                  <div class="widget_blackoverlay widget_wizard">
                                  </div>
                                  <div class="widget_loadingbox widget_wizard">
                                    <div class="widget_loaderbox">
                                      <span class="bigloader"></span>
                                    </div>
                                    <div class="widget_loadertext">
                                      <p id="in_process_text"><spring:message code="label.loading.in.process"/></p>
                                    </div>
                                  </div>
                            </div>
        </div>
        <div id="buttons" class="widgetwizard_nextprevpanel sixstepswizard">
            <input class="widgetwizard_nextprevpanel prevbutton" type="button" onclick="addTenantPrevious(this);" value="<spring:message code="label.previous.step"/>" name="<spring:message code="label.previous.step"/>" id="add_tenant_previous1" tabindex="110">
            <input class="widgetwizard_nextprevpanel nextbutton" data-primary type="button" onclick="addTenantNext(this);" value="<spring:message code="label.next.step"/>" name="<spring:message code="label.next.step"/>" id="add_tenant_next2" tabindex="109">
        </div>
    </div>
    <!--step 2 ends here-->
    
     <!--step 3 starts here-->
  <div id="step3" class="j_tenantspopup" style="display:none">
   <input type="hidden" id="nextstep" name="nextstep" value="step4" >
  <input type="hidden" id="prevstep" name="prevstep" value="step2" >
        <div class="widgetwizard_stepsbox">
            <div class="widgetwizard_steps_contentcontainer sixstepswizard">
                <div class="widgetwizard_stepscenterbar sixstepswizard">
                    <ul>
                        <li class="widgetwizard_stepscenterbar fivestepswizard first"><span class="steps completedsteps"><span class="stepsnumbers">1</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.account.step1.title"/></span></li>
                        <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps completedsteps"><span class="stepsnumbers">2</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.account.step2.title"/></span></li>
                        <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps active"><span class="stepsnumbers active">3</span></span><span class="stepstitle active"><spring:message htmlEscape="false" code="ui.account.step3.title"/></span></li>
                        <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps"><span class="stepsnumbers">4</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.account.step4.title"/></span></li>
                        <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps"><span class="stepsnumbers">5</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.account.step5.title"/></span></li>
                        <li class="widgetwizard_stepscenterbar fivestepswizard last"><span class="steps last"><span class="stepsnumbers last">6</span></span><span class="stepstitle last"><spring:message htmlEscape="false" code="ui.account.step6.title"/></span></li>
                    </ul>
                </div>
            </div>
        </div>
        <div class="widgetwizard_contentarea sixstepswizard">
            <div class="widgetwizard_boxes sixstepswizard">
                <div class="widgetwizard_titleboxes">
                    <h2><spring:message code="ui.account.enter.company.info.title"/></h2>
                    <span><spring:message code="ui.account.enter.company.info.title.desc"/></span>
                </div>
                <div class="widgetwizard_detailsbox sixstepswizard">
                    <ul>
                        <li>
                            <span class="label"><form:label path="tenant.name" ><spring:message code="label.tenants.companyName"/></form:label></span>
                            <div class="mandatory_wrapper">
                              <spring:message code="label.moreUserInfo.companyName.tooltip" var="i18nCompanyNameTooltip"/>
                              <form:input cssClass="text" tabindex="111" path="tenant.name" title="${i18nCompanyNameTooltip}" maxlength="150" />
                            </div>
                            <div class="main_addnew_formbox_errormsg_popup" id="tenant.nameError"></div>
                        </li>
                        
                          <c:if test="${showSuffixTextBox=='true'}">
                          <li>
                            <span class="label"><form:label path="tenant.usernameSuffix" ><spring:message code="label.tenants.suffix"/></form:label></span>
                            <div class="mandatory_wrapper">
                              <spring:message code="label.moreUserInfo.suffix.tooltip" var="i18nSuffixNameTooltip"/>
                              <form:input cssClass="text" tabindex="111" path="tenant.usernameSuffix" title="${i18nSuffixNameTooltip}" maxlength="150" />
                            </div>
                             <div class="main_addnew_formbox_errormsg_popup" id="tenant.usernameSuffixError"></div>
                          </li>
                        </c:if>
                        
                        <li>
                           <span class="label"><form:label path="currency" ><spring:message code="label.moreUserInfo.currency"/></form:label></span>
                           <div class="mandatory_wrapper">                   
                              <spring:message code="label.moreUserInfo.currency.tooltip" var="i18ncurrencyTooltip"/>
                              <form:select path="currency"  cssClass="select" tabindex="112" title="${i18ncurrencyTooltip}">
                                <option value=""><spring:message code="label.choose"></spring:message> </option>
                                <c:forEach var="currency" items="${account.currencyValueList}" varStatus="status">
                                  <option value="<c:out value="${currency.currencyCode}"></c:out>"  <c:if test="${currency.currencyCode == account.currency}">selected="selected"</c:if> ><spring:message code="currency.longname.${currency.currencyCode}"></spring:message>
                                  </option>
                                </c:forEach>
                              </form:select>
                            </div>
                           <div class="main_addnew_formbox_errormsg_popup" id="currencyError"></div>                       
                        </li>
                        <li>
                          <span class="label"><form:label path="user.locale" ><spring:message code="label.myprofile.language"/></form:label></span>
                           <div class="mandatory_wrapper">  
                           <spring:message code="label.language.tooltip" var="i18nLanguageTooltip"/>
                           <form:select cssClass="select" tabindex="113" path="user.locale" title="${i18nLanguageTooltip}">
                              <c:set var="userLoc" value="${account.user.locale}"/> 
                              <c:if test="${empty userLoc}">
                                  <c:set var="userLoc" value="${defaultLocale}"/>
                              </c:if>
                              <c:forEach items="${supportedLocaleList}" var="locale" varStatus="status">
                                <option value='<c:out value="${locale.key}" />' <c:if test="${locale.key == userLoc}">selected="selected"</c:if> >
                                  <c:out value="${locale.value}"></c:out>
                                </option> 
                              </c:forEach>
                           </form:select> 
                           </div>                              
                         </li>
                    </ul>
                </div>
            </div>
        </div>
        <div id="buttons" class="widgetwizard_nextprevpanel sixstepswizard">
            <input class="widgetwizard_nextprevpanel prevbutton" type="button" onclick="addTenantPrevious(this)" value="<spring:message code="label.previous.step"/>" name="<spring:message code="label.previous.step"/>" id="add_tenant_previous2" tabindex="115">
            <input class="widgetwizard_nextprevpanel nextbutton" type="button" data-primary onclick="addTenantNext(this)" value="<spring:message code="label.next.step"/>" name="<spring:message code="label.next.step"/>" id="add_tenant_next3" tabindex="114">
        </div>
    </div>
    <!--step 3 ends here-->
    
    <!--step 4 starts here-->
  <div id="step4" class="j_tenantspopup" style="display:none;">
   <input type="hidden" id="nextstep" name="nextstep" value="step5" >
  <input type="hidden"  id="prevstep"name="prevstep" value="step3" >
        <div class="widgetwizard_stepsbox">
            <div class="widgetwizard_steps_contentcontainer sixstepswizard">
                <div class="widgetwizard_stepscenterbar sixstepswizard">
                    <ul>
                        <li class="widgetwizard_stepscenterbar fivestepswizard first"><span class="steps completedsteps"><span class="stepsnumbers">1</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.account.step1.title"/></span></li>
                        <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps completedsteps"><span class="stepsnumbers">2</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.account.step2.title"/></span></li>
                        <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps completedsteps"><span class="stepsnumbers">3</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.account.step3.title"/></span></li>
                        <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps active"><span class="stepsnumbers active">4</span></span><span class="stepstitle active"><spring:message htmlEscape="false" code="ui.account.step4.title"/></span></li>
                        <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps"><span class="stepsnumbers">5</span></span><span class="stepstitle  active"><spring:message htmlEscape="false" code="ui.account.step5.title"/></span></li>
                        <li class="widgetwizard_stepscenterbar fivestepswizard last"><span class="steps last"><span class="stepsnumbers last">6</span></span><span class="stepstitle last"><spring:message htmlEscape="false" code="ui.account.step6.title"/></span></li>
                    </ul>
                </div>
            </div>
        </div>
        <div class="widgetwizard_contentarea sixstepswizard">
            <div class="widgetwizard_boxes sixstepswizard fullheight">
                <div class="widgetwizard_titleboxes">
                    <h2><spring:message code="ui.account.enter.company.address.info.title"/></h2>
                    <span><spring:message code="ui.account.enter.company.address.info.title.desc"/></span>
                </div>
                <div class="widgetwizard_detailsbox sixstepswizard">
                  <ul>
                        <li>
                            <span class="label"><form:label path="user.address.country" ><spring:message code="label.tenants.country"/></form:label></span>
                            <div class="mandatory_wrapper">
                              <spring:message code="label.moreUserInfo.country.tooltip" var="i18nCountryTooltip"/>
                              <form:select cssClass="text" tabindex="116" path="user.address.country" style="padding:3px 0 0;" title="${i18nCountryTooltip}">
                                    <option value=""><spring:message code="label.choose"/></option>
                                    <c:forEach items="${filteredCountryList}" var="choice" varStatus="status">
                                      <option value="<c:out value="${choice.countryCode2}"/>" <c:if test="${ipToCountryCode == choice.countryCode2}" >selected="selected" </c:if>><c:out value="${choice.name}" escapeXml="false"/></option> 
                                    </c:forEach>
                              </form:select>                    
                            </div>
                            <div class="main_addnew_formbox_errormsg_popup" id="user.address.countryError"></div>
                        </li>                        
                         <li>
                            <div id="stateInput">
                              <span class="label"><form:label path="user.address.state" ><spring:message code="label.tenants.state"/></form:label></span>
                              <div class="mandatory_wrapper">
                              <spring:message code="label.moreUserInfo.state.tooltip" var="i18nStateTooltip"/>
                                <form:input cssClass="text" tabindex="117" path="user.address.state" title="${i18nStateTooltip}" maxlength="255"/>
                              </div>
                            </div>
                            <div  id="stateSelect" style="display:none">
                              
                              <div id="otherstateDiv" style="display:none">
                                <span class="label"><form:label path="user.address.state" ><spring:message code="label.moreUserInfo.state"/></form:label></span>
                              </div>
                              
                              <div id="JPstateDiv">
                                <span class="label"><form:label path="user.address.state" ><spring:message code="label.moreUserInfo.state.jp"/></form:label></span>
                              </div>
                              
                              <div class="mandatory_wrapper">
                                <select class="select" tabindex="118" id="userAddressStateSelect" name="userAddressStateSelect" title="<spring:message code="label.moreUserInfo.state.tooltip"/>"></select>
                              </div>
                              
                            </div>
                            <div class="main_addnew_formbox_errormsg_popup" id="user.address.stateError"></div>
                        </li>
                        
                        <li>
                            <span class="label"><form:label path="user.address.street1" ><spring:message code="label.tenants.address1"/></form:label></span>
                            <div class="mandatory_wrapper">
                              <spring:message code="label.moreUserInfo.address1.tooltip" var="i18nAddress1Tooltip"/>
                              <form:input cssClass="text" tabindex="119" path="user.address.street1" title="${i18nAddress1Tooltip}" maxlength="255"/>                
                            </div>
                            <div class="main_addnew_formbox_errormsg_popup" id="user.address.street1Error"></div>
                        </li>  
                        
                        <li>
                            <span class="label"><form:label path="user.address.street2" ><spring:message code="label.tenants.address2"/></form:label></span>
                            <div class="nonmandatory_wrapper">
                              <spring:message code="label.moreUserInfo.address2.tooltip" var="i18nAddress2Tooltip"/>
                              <form:input cssClass="text" tabindex="120" path="user.address.street2" title="${i18nAddress2Tooltip}" maxlength="255"/>                    
                            </div>
                            <div class="main_addnew_formbox_errormsg_popup" id="user.address.street2Error"></div>
                        </li>  
                        <li>
                            <span class="label"><form:label path="user.address.city" ><spring:message code="label.tenants.city"/></form:label></span>
                            <div class="mandatory_wrapper">
                              <spring:message code="label.moreUserInfo.city.tooltip" var="i18nCityTooltip"/>
                              <form:input cssClass="text" tabindex="121" path="user.address.city" title="${i18nCityTooltip}" maxlength="255"/>                    
                            </div>
                            <div class="main_addnew_formbox_errormsg_popup" id="user.address.cityError"></div>
                        </li>
                        <li>
                            <span class="label"><form:label path="user.address.postalCode" ><spring:message code="label.tenants.zipCode"/></form:label></span>
                            <div class="mandatory_wrapper">
                              <spring:message code="label.moreUserInfo.zipCode.tooltip" var="i18nZipCodeToolTip"/>
                              <form:input maxLength="25" cssClass="text" tabindex="122" path="user.address.postalCode" title="${i18nZipCodeToolTip}"/>                    
                            </div>
                            <div class="main_addnew_formbox_errormsg_popup" id="user.address.postalCodeError"></div>
                        </li>
                        
                        <li id="allowSecondaryLi" style="display: none;">          
                          <span class="label"><spring:message code="ui.accounts.all.header.add.secondary.address"/></span>
                          <form:checkbox id="allowSecondaryId" path="allowSecondary"/>
                        </li>
                        
                        <li id="syncAddressli">
                          <span class="label"><spring:message code="ui.accounts.all.header.add.secondary.syncaddress"/></span>
                          <input type="checkbox" class="checkbox " id="syncAddress" tabindex="123" name="syncAddress">
                        </li> 
                        
                        <li id="billingAddressLabel">
                          <span class="label"><form:label path="secondaryAddress.country"><spring:message code="ui.accounts.all.billing.address"/></form:label></span>
                        </li>
                        
                        <li id="secondaryCountry">
                          <span class="label"><form:label path="secondaryAddress.country" ><spring:message code="label.tenants.country"/></form:label></span><div class="red_compulsoryicon">*</div>
                          <spring:message code="label.moreUserInfo.country.tooltip" var="i18nCountryTooltip"/>
                          <form:select cssClass="text" tabindex="124" path="secondaryAddress.country" title="${i18nCountryTooltip}">
                              <option value=""><spring:message code="label.choose"/></option>
                              <c:forEach items="${filteredCountryList}" var="choice" varStatus="status">
                                <option value="<c:out value="${choice.countryCode2}"/>" <c:if test="${ipToCountryCode == choice.countryCode2}" >selected="selected" </c:if>><c:out value="${choice.name}" escapeXml="false"/></option> 
                              </c:forEach>
                          </form:select>                    
                          <div class="main_addnew_formbox_errormsg_popup" id="secondaryAddress.countryError"></div>
                        </li>
                        
                        <li id="secondaryState">
                          
                          <div id="stateSecondaryInput">
                              <span class="label"><form:label path="secondaryAddress.state" ><spring:message code="label.tenants.state"/></form:label></span>
                              <div class="mandatory_wrapper">
                                <spring:message code="label.moreUserInfo.state.tooltip" var="i18nStateTooltip"/>                      
                                <form:input cssClass="text" tabindex="125" path="secondaryAddress.state" title="${i18nStateTooltip}"/>
                              </div>
                          </div> 
                          <div  id="stateSecondarySelect" style="display:none">
                            <div id="otherstateSecondaryDiv" style="display:none">
                              <span class="label"><form:label path="secondaryAddress.state" ><spring:message code="label.moreUserInfo.state"/></form:label></span>
                            </div>
                            <div id="JPSecondarystateDiv">
                              <span class="label"><form:label path="secondaryAddress.state" ><spring:message code="label.moreUserInfo.state.jp"/></form:label></span>
                            </div>
                            <div class="mandatory_wrapper">
                              <select class="select" tabindex="126" id="tenantSecondaryAddressStateSelect" name="tenantSecondaryAddressStateSelect" title="<spring:message code="label.moreUserInfo.state.tooltip"/>"></select>
                            </div>
                          </div>
                          <div class="main_addnew_formbox_errormsg_popup" id="secondaryAddress.stateError"></div>                    
                        </li>
                        <li id="secondaryStreet1">
                          <span class="label"><form:label path="secondaryAddress.street1" ><spring:message code="label.tenants.address1"/></form:label></span>
                          <div class="mandatory_wrapper">                  
                            <spring:message code="label.moreUserInfo.address1.tooltip" var="i18nAddress1Tooltip"/>
                            <form:input cssClass="text" tabindex="127" path="secondaryAddress.street1" title="${i18nAddress1Tooltip}"/>
                          </div>
                          <div class="main_addnew_formbox_errormsg_popup" id="secondaryAddress.street1Error"></div>                        
                        </li>
                        
                        
                        <li id="secondaryStreet2">
                          <span class="label"><form:label path="secondaryAddress.street2" ><spring:message code="label.tenants.address2"/></form:label></span>
                          <div class="mandatory_wrapper">                    
                            <spring:message code="label.moreUserInfo.address2.tooltip" var="i18nAddress2Tooltip"/>
                            <form:input cssClass="text" tabindex="128" path="secondaryAddress.street2" title="${i18nAddress2Tooltip}"/>
                          </div>
                          <div class="main_addnew_formbox_errormsg_popup" id="secondaryAddress.street2Error"></div>                        
                        </li>                                
                               
                         <li id="secondaryCity">
                          <span class="label"><form:label path="secondaryAddress.city" ><spring:message code="label.tenants.city"/></form:label></span>
                          <div class="mandatory_wrapper">                   
                            <spring:message code="label.moreUserInfo.city.tooltip" var="i18nCityTooltip"/>
                            <form:input cssClass="text" tabindex="129" path="secondaryAddress.city" title="${i18nCityTooltip}"/>
                          </div>
                          <div class="main_addnew_formbox_errormsg_popup" id="secondaryAddress.cityError"></div>                       
                         </li>
                         
                         <li id="secondaryPostalCode">
                          <span class="label"><form:label path="secondaryAddress.postalCode" ><spring:message code="label.tenants.zipCode"/></form:label></span>
                          <div class="mandatory_wrapper">                  
                            <spring:message code="label.moreUserInfo.zipCode.tooltip" var="i18nZipCodeToolTip"/>
                            <form:input cssClass="text" tabindex="130" path="secondaryAddress.postalCode" title="${i18nZipCodeToolTip}"/>
                          </div>
                          <div class="main_addnew_formbox_errormsg_popup" id="secondaryAddress.postalCodeError"></div>                       
                         </li>
                         
                         <li>
                            <span class="label"><form:label path="user.phone" ><spring:message code="label.tenants.phoneNumber"/></form:label></span>
                            <div class="mandatory_wrapper">
                              <div class="commonboxes_formbox_withouttextbox_plus_sign" style="margin: 15px 0px 0px 10px;">+</div>
                              <spring:message code="label.moreUserInfo.countryCode.tooltip" var="i18nCountryCodeTooltip"/>
                              <form:input cssClass="text" tabindex="131" path="user.countryCode" title="${i18nCountryCodeTooltip}" cssStyle="width:60px" maxlength="5"/>
                              <spring:message code="label.moreUserInfo.phoneNumber.tooltip" var="i18nPhoneTooltip"/>
                              <form:input cssClass="text" tabindex="132" path="user.phone" title="${i18nPhoneTooltip}" maxlength="15"/>
                            </div>
                            <div class="main_addnew_formbox_errormsg_popup" id="user.countryCodeError"></div>                                
                            <div class="main_addnew_formbox_errormsg_popup" id="user.phoneError"></div>                        
                        </li>
                        <tiles:insertDefinition name="tenant.custom.fields"></tiles:insertDefinition>
                    </ul>
                </div>
            </div>
            <div id="spinning_wheel4" style="display:none;">
                                  <div class="widget_blackoverlay widget_wizard">
                                  </div>
                                  <div class="widget_loadingbox widget_wizard">
                                    <div class="widget_loaderbox">
                                      <span class="bigloader"></span>
                                    </div>
                                    <div class="widget_loadertext">
                                      <p id="in_process_text"><spring:message code="label.loading.in.process"/></p>
                                    </div>
                                  </div>
                            </div>
        </div>
        <div id="buttons" class="widgetwizard_nextprevpanel sixstepswizard">
            <input class="widgetwizard_nextprevpanel prevbutton" type="button" onclick="addTenantPrevious(this)" value="<spring:message code="label.previous.step"/>" name="<spring:message code="label.previous.step"/>" id="add_tenant_previous3" tabindex="134">
            <input class="widgetwizard_nextprevpanel nextbutton" type="button" data-primary onclick="addTenantNext(this)" value="<spring:message code="label.next.step"/>" name="<spring:message code="label.next.step"/>" id="add_tenant_next4" tabindex="133">
        </div>
    </div>
    <!--step 4 ends here-->
    
  <!--step 5 starts here-->
  <div id="step5" class="j_tenantspopup" style="display:none;">
   <input type="hidden" id="nextstep" name="nextstep" value="step6" >
  <input type="hidden"  id="prevstep"name="prevstep" value="step4" >
        <div class="widgetwizard_stepsbox">
            <div class="widgetwizard_steps_contentcontainer sixstepswizard">
                <div class="widgetwizard_stepscenterbar sixstepswizard">
                    <ul>
                        <li class="widgetwizard_stepscenterbar fivestepswizard first"><span class="steps completedsteps"><span class="stepsnumbers">1</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.account.step1.title"/></span></li>
                        <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps completedsteps"><span class="stepsnumbers">2</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.account.step2.title"/></span></li>
                        <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps completedsteps"><span class="stepsnumbers">3</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.account.step3.title"/></span></li>
                        <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps completedsteps"><span class="stepsnumbers">4</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.account.step4.title"/></span></li>
                        <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps active"><span class="stepsnumbers active">5</span></span><span class="stepstitle  active"><spring:message htmlEscape="false" code="ui.account.step5.title"/></span></li>
                        <li class="widgetwizard_stepscenterbar fivestepswizard last"><span class="steps last"><span class="stepsnumbers last">6</span></span><span class="stepstitle last"><spring:message htmlEscape="false" code="ui.account.step6.title"/></span></li>
                    </ul>
                </div>
            </div>
        </div>
        <div class="widgetwizard_contentarea sixstepswizard">
            <div class="widgetwizard_boxes fullheight sixstepswizard js_scroll_container">
            <div id="tenantCreateErrorDiv" class="alert alert-error" style="margin-top: 5px; float: left; width: 90%; margin-left: 15px;display: none">
            	<spring:message code="errors.tenant.registration"/>
            </div>
                <div class="widgetwizard_titleboxes">
                    <h2><spring:message code="ui.account.review.confirm.title"/></h2>
                    <span><spring:message code="ui.account.review.confirm.title.desc"/></span>
                </div>
                <div class="widgetwizard_reviewbox">
                  <ul>
                        <li style="padding:0;" id="confirmStep1">
                          <span class="label"><spring:message code="ui.account.channel.accountType"/></span>
                            <span class="description j_description"></span>
                            <span class="edit" style="margin-right:60px">
                            <a class="confirm_edit_link" tabindex="140" id="backtostep1" href="javascript:void(0);"><spring:message code="label.edit"/></a></span>
                            <ul>
                              <li class="subselection" >
                                <span class="label sublabel"><spring:message code="label.tenants.channel"/></span>
                                <span class="description subdescription" id="tenant_selected_channel"></span>
                              </li>
                               <li class="subselection" >
                                <span class="label sublabel"><spring:message code="label.tenants.accountType"/></span>
                                <span class="description subdescription" id="tenant_selected_accounttype"></span>
                              </li>
                            </ul>
                        </li>
                        <li id="confirmStep2" style="padding:0;">
                            <span class="label"><spring:message code="ui.account.master.user.deteails"/></span>
                            <span class="description j_description"></span>
                            <span class="edit" style="margin-right:60px"><a class="confirm_edit_link" tabindex="141" id="backtostep2" href="javascript:void(0);"><spring:message code="label.edit"/></a></span>
                            <ul>
                              <li class="subselection" >
                                <span class="label sublabel"><spring:message code="label.tenants.username"/></span>
                                <span class="description subdescription" id="tenant_username"></span>
                              </li>
                              <li class="subselection" >
                                <span class="label sublabel"><spring:message code="label.tenants.firstName"/></span>
                                <span class="description subdescription" id="tenant_firstname"></span>
                              </li>
                               <li class="subselection" >
                                <span class="label sublabel"><spring:message code="label.tenants.lastName"/></span>
                                <span class="description subdescription" id="tenant_lastname"></span>
                              </li>
                               <li class="subselection" >
                                <span class="label sublabel"><spring:message code="label.tenants.email"/></span>
                                <span class="description subdescription" id="tenant_email"></span>
                              </li>
                               <li class="subselection" >
                                <span class="label sublabel"><spring:message code="label.tenants.confirmEmail"/></span>
                                <span class="description subdescription" id="tenant_confirm_email"></span>
                              </li>
                            </ul>
                        </li>
                        <li id="confirmStep3" style="padding:0;">
                          <span class="label"><spring:message code="ui.account.company.info"/></span>
                            <span class="description j_description"></span>
                            <span class="edit" style="margin-right:60px"><a class="confirm_edit_link" tabindex="142" id="backtostep3" href="javascript:void(0);"><spring:message code="label.edit"/></a></span>
                            <ul>
                              <li class="subselection" >
                                <span class="label sublabel"><spring:message code="label.tenants.companyName"/></span>
                                <span class="description subdescription" id="tenant_company_name"></span>
                              </li>
                              <li class="subselection" >
                                <span class="label sublabel"><spring:message code="label.moreUserInfo.currency"/></span>
                                <span class="description subdescription" id="tenant_currency"></span>
                              </li>
                               <li class="subselection" >
                                <span class="label sublabel"><spring:message code="label.myprofile.language"/></span>
                                <span class="description subdescription" id="tenant_language"></span>
                              </li>
                            </ul>
                        </li>
                        <li id="confirmStep4" style="padding:0;">
                          <span class="label"><spring:message code="ui.account.company.address.info"/></span>
                            <span class="description j_description"></span>
                            <span class="edit" style="margin-right:60px"><a class="confirm_edit_link" tabindex="143" id="backtostep4" href="javascript:void(0);"><spring:message code="label.edit"/></a></span>
                            <ul>
                              <li class="subselection" >
                                <span class="label sublabel"><spring:message code="label.tenants.country"/></span>
                                <span class="description subdescription" id="tenant_country"></span>
                              </li>
                               <li class="subselection" >
                                <span class="label sublabel"><spring:message code="label.tenants.state"/></span>
                                <span class="description subdescription" id="tenant_state"></span>
                              </li>
                               <li class="subselection" >
                                <span class="label sublabel"><spring:message code="label.tenants.address1"/></span>
                                <span class="description subdescription" id="tenant_address1"></span>
                              </li>
                               <li class="subselection" >
                                <span class="label sublabel"><spring:message code="label.tenants.address2"/></span>
                                <span class="description subdescription" id="tenant_address2"></span>
                              </li>
                               <li class="subselection" >
                                <span class="label sublabel"><spring:message code="label.tenants.city"/></span>
                                <span class="description subdescription" id="tenant_city"></span>
                              </li>
                              <li class="subselection" >
                                <span class="label sublabel"><spring:message code="label.tenants.zipCode"/></span>
                                <span class="description subdescription" id="tenant_zipCode"></span>
                              </li>
                              <li class="subselection" >
                                <span class="label sublabel"><spring:message code="label.tenants.phoneNumber"/></span>
                                <span class="description subdescription" id="tenant_phoneNumber"></span>
                              </li>
                            </ul>
                        </li>
                    </ul>
                </div>
            </div>
            <div id="spinning_wheel5" style="display:none;">
                                  <div class="widget_blackoverlay widget_wizard">
                                  </div>
                                  <div class="widget_loadingbox widget_wizard">
                                    <div class="widget_loaderbox">
                                    	<span class="bigloader"></span>
                                    </div>
                                    <div class="widget_loadertext">
                                    	<p id="in_process_text"><spring:message code="label.loading.in.process"/></p>
                                    </div>
                                  </div>
                            </div>
        </div>
        <div id="buttons" class="widgetwizard_nextprevpanel sixstepswizard">
            <input class="widgetwizard_nextprevpanel prevbutton" type="button" onclick="addTenantPrevious(this)" value="<spring:message code="label.previous.step"/>" name="<spring:message code="label.previous.step"/>" id="add_tenant_previous4" tabindex="145">
            <input class="widgetwizard_nextprevpanel nextbutton" type="button" data-primary onclick="addTenantNext(this)" value="<spring:message code="label.next.step"/>" name="<spring:message code="label.next.step"/>" id="add_tenant_next5" tabindex="144">
        </div>
        
    </div>
    <!--step 5 ends here-->
    
     <!--step 6 starts here-->
  <div id="step6" class="j_tenantspopup" style="display:none;">
   <input type="hidden" id="nextstep" name="nextstep" value="" >
  <input type="hidden" id="prevstep" name="prevstep" value="step5" >
  <input type="hidden" id="tenantParam" name="tenantParam" value="">
        <div class="widgetwizard_stepsbox">
            <div class="widgetwizard_steps_contentcontainer sixstepswizard">
                <div class="widgetwizard_stepscenterbar sixstepswizard">
                    <ul>
                        <li class="widgetwizard_stepscenterbar fivestepswizard first"><span class="steps completedsteps"><span class="stepsnumbers">1</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.account.step1.title"/></span></li>
                        <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps completedsteps"><span class="stepsnumbers">2</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.account.step2.title"/></span></li>
                        <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps completedsteps"><span class="stepsnumbers">3</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.account.step3.title"/></span></li>
                        <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps completedsteps"><span class="stepsnumbers">4</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.account.step4.title"/></span></li>
                        <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps completedsteps"><span class="stepsnumbers">5</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.account.step5.title"/></span></li>
                        <li class="widgetwizard_stepscenterbar fivestepswizard last"><span class="steps last active"><span class="stepsnumbers last">6</span></span><span class="stepstitle last"><spring:message htmlEscape="false" code="ui.account.step6.title"/></span></li>
                    </ul>
                </div>
            </div>
        </div>
        <div class="widgetwizard_contentarea sixstepswizard">
            <div class="widgetwizard_boxes  sixstepswizard fullheight">
                <div class="widgetwizard_successbox">
                  <div class="widgetwizard_successbox">
                      <div class="widget_resulticon success"></div>
                        <p id="successmessage"><spring:message code="ui.account.successfully.completed.text"/></p>
                        <a onclick="redirectToTenantsList();" tabindex="150" href="javascript:void(0);"><spring:message code="ui.account.view.details.text"/></a>
                    </div>
                </div>
            </div>
        </div>
        <div id="buttons" class="widgetwizard_nextprevpanel sixstepswizard">
            <input class="widgetwizard_nextprevpanel submitbutton" type="button" data-primary onclick="addTenantNext(this)" value="<spring:message code="label.close"/>" name="Close" id="add_tenant_next6" tabindex="151">
        </div>
    </div>
    <!--step 6 ends here-->
    
</div>
</form:form>
<script>
  swap_name_order_tab_index("user_first_name", "user_last_name");
</script>


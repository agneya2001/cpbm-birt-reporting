
<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<script type="text/javascript" src="<%=request.getContextPath() %>/js/date.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/date.format.js"></script>

<script language="javascript">
$(document).ready(
  function() {
    swap_name_order_tab_index("user_first_name", "user_last_name");
});
var dictionary = {
    lightboxbuttoncancel: '<spring:message javaScriptEscape="true" code="label.cancel"/>',  
    lightboxbuttonconfirm: '<spring:message javaScriptEscape="true" code="label.confirm"/>',
    lightboxrequestPasswordReset : '<spring:message javaScriptEscape="true" code="message.myprofile.reset.password.request"/>'
};
var dictionary2={
    changePasswordError : '<spring:message javaScriptEscape="true" code="message.myprofile.changepassword.error"/>',
    wrongPassword: '<spring:message javaScriptEscape="true" code="errors.password.invalid"/>',
    changePasswordSuccessful : '<spring:message javaScriptEscape="true" code="message.change.password.successful"/>'
};
if( typeof i18n === 'undefined' ) {
  var i18n = {};
}
if( typeof i18n.errors === 'undefined' ) {
  i18n.errors = {};
}
if( typeof i18n.labels === 'undefined' ) {
  i18n.labels = {};
}
  i18n = {
          user: {
            delfail: '<spring:message javaScriptEscape="true" code="js.user.del.fail"/>',
            deactfail: '<spring:message javaScriptEscape="true" code="js.user.deact.fail"/>',
            actfail: '<spring:message javaScriptEscape="true" code="js.user.act.fail"/>',
            channel: '<spring:message javaScriptEscape="true" code="js.user.channel"/>',
            title: '<spring:message javaScriptEscape="true" code="js.user.title"/>',
          del : '<spring:message javaScriptEscape="true" code="js.user.del.confirm"/>',
          generateAPIKey : '<spring:message javaScriptEscape="true" code="js.user.generate.api.key"/>',
          deact: '<spring:message javaScriptEscape="true" code="js.user.deact.confirm"/>',
          act: '<spring:message javaScriptEscape="true" code="js.user.act.confirm"/>',
          delproject : '<spring:message javaScriptEscape="true" code="js.user.del.confirmproject"/>',           
          max : '<spring:message javaScriptEscape="true" code="js.user.max"/>',
          cloudstorage: '<spring:message javaScriptEscape="true" code="js.user.cloudstorage.subscribe"/>',
          profile: '<spring:message javaScriptEscape="true" code="js.user.profile.edit"/>',
          flnameValidationError: "<spring:message javaScriptEscape="true" code="js.errors.flname"/>",
          firstname: '<spring:message javaScriptEscape="true" code="js.user.firstname"/>',
          lastname: '<spring:message javaScriptEscape="true" code="js.user.lastname"/>',
          email: '<spring:message javaScriptEscape="true" code="js.user.email"/>',
          emailmatch: '<spring:message javaScriptEscape="true" code="js.user.email.match"/>',
          confirmemail: '<spring:message javaScriptEscape="true" code="js.user.confirmemail"/>',
          emailformat: '<spring:message javaScriptEscape="true" code="js.user.email.format"/>',
          username: '<spring:message javaScriptEscape="true" code="js.user.username"/>',
          usernameexists: '<spring:message javaScriptEscape="true" code="js.user.username.exists"/>',
          password: '<spring:message javaScriptEscape="true" code="js.user.password"/>',
          passwordValidationError: '<spring:message javaScriptEscape="true" code="js.errors.password"/>',
          oldPasswordNullError:'<spring:message javaScriptEscape="true" code="message.myprofile.oldpassword.null.error"/>',
          passwordconfirm: '<spring:message javaScriptEscape="true" code="js.user.password.confirm"/>',
          passwordmatch: '<spring:message javaScriptEscape="true" code="js.user.password.match"/>',
          profilerequired: '<spring:message javaScriptEscape="true" code="js.user.profile.required"/>',
          spendlimit: '<spring:message javaScriptEscape="true" code="js.errors.spendbudget.number"/>',
          passwordequsername: '<spring:message javaScriptEscape="true" code="js.user.passwordequsername"/>',
          phone : '<spring:message javaScriptEscape="true" code="js.errors.register.user.phone"/>',
          phoneValidationError:"<spring:message javaScriptEscape="true" code="js.errors.phone"/>" 
          },
          errors:{
            countryCode : '<spring:message javaScriptEscape="true" code="js.errors.register.countryCode"/>',
            countryCodeValidationError : '<spring:message javaScriptEscape="true" code="js.errors.countryCode"/>',
            countryCodeNumber : '<spring:message javaScriptEscape="true" code="js.errors.register.countryCodeNumber"/>',
            phonePin : '<spring:message javaScriptEscape="true" code="js.user.phonePin"/>',
            phoneDetails : '<spring:message javaScriptEscape="true" code="js.errors.register.phoneDetails"/>',
            callRequested: '<spring:message javaScriptEscape="true" code="js.errors.register.callRequested"/>',
            callFailed: '<spring:message javaScriptEscape="true" code="js.errors.register.callFailed"/>',
            textMessageRequested      : '<spring:message javaScriptEscape="true" code="js.errors.register.textMessageRequested"/>',
            textMessageFailed       : '<spring:message javaScriptEscape="true" code="js.errors.register.textMessageFailed"/>',
            isdCodeFetchFailed        : '<spring:message javaScriptEscape="true" code="js.error.user.isdCodeFetchFailed"/>',
            isEmailBlacklisted : '<spring:message javaScriptEscape="true" code="signup.emaildomain.blacklist.error"/>',
            keyNameIsRequired: '<spring:message javaScriptEscape="true" code="key.name.is.required"/>',
            failedToGenerateSSHKey: '<spring:message javaScriptEscape="true" code="failed.to.generate.SSH.key"/>',
            passwordIsMandatory: '<spring:message javaScriptEscape="true" code="errors.password.required"/>',
            failedToUploadSSHKey: '<spring:message javaScriptEscape="true" code="failed.to.upload.SSH.key"/>',
            publicKeyIsRequiredWhenUploadingSSHKey: '<spring:message javaScriptEscape="true" code="public.key.is.required.when.uploading.SSH.key"/>',
            publicKeyShouldBeginWith: '<spring:message javaScriptEscape="true" code="public.key.should.begin.with"/>',
            failedToDeleteSSHKey: '<spring:message javaScriptEscape="true" code="failed.to.delete.SSH.key"/>',
            onlyAlphanumericCharactersAreAllowed: '<spring:message javaScriptEscape="true" code="only.alphanumeric.characters.are.allowed"/>'
        },
    labels : {
      phoneVerificationCallMe     : '<spring:message javaScriptEscape="true" code="label.phoneVerification.callMe"/>',
    phoneVerificationCalling    : '<spring:message javaScriptEscape="true" code="label.phoneVerification.calling"/>',
      phoneVerificationTextMe     : '<spring:message javaScriptEscape="true" code="label.phoneVerification.textMe"/>',
    phoneVerificationSending    : '<spring:message javaScriptEscape="true" code="label.phoneVerification.sending"/>',
    remove: '<spring:message javaScriptEscape="true" code="label.remove"/>'
    }
   };

  var phoneVerificationEnabled="<c:out value="${user.phoneVerificationEnabled}" />";
  var PhoneNumberMandatory="<c:out value="${PhoneNumberMandatory}" />";
  var notificationUrl = "<%=request.getContextPath() %>/portal/tenants/";
  var tenantParam = "<c:out value="${tenant.param}"/>";
  var totalpages = "0";
  var currentPage = "0";
  var perPageValue = "0";
  var notificationListLen = "0";
  var usersUrl = "<%=request.getContextPath() %>/portal/users/";
</script>

<script type="text/javascript" src="<%=request.getContextPath() %>/js/jquery.selectboxes.min.js"></script>
<c:set var="countries" scope="page" value="${user.countryList}"/>
<jsp:include page="/WEB-INF/jsp/tiles/shared/country_states.jsp"></jsp:include>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/jquery.stateselect.js"></script>

<!-- Top links -->
<div class="maintitlebox ellipsis">
<input type="hidden" id="loggedInUserParam" value="<c:out value="${user.user.param}"/>"/>
<input type="hidden" id="activeTabName" value="<c:out value="${activeTab}"/>"/>
               
<h1 title='<c:out value="${user.user.firstName} ${user.user.lastName}"></c:out>'>
<c:out value="${user.user.firstName} ${user.user.lastName}"></c:out>
</h1>
      <c:if test="${user.user.id ne currentUser.id && user.user.tenant.owner.id ne user.user.id}">
      <div class="maintitle_boxlinks_tab">
      
      <c:choose >
       <c:when test="${user.user.enabled == true && user.user.locked == false}">
       <spring:url value="/portal/users/{user}/deactivate_user" var="deactivateUserPath" htmlEscape="false"> 
          <spring:param name="user"><c:out value="${user.user.param}"/></spring:param>
      </spring:url>
      <p><a id="profile_deactivateuser" href="javascript:void(0)" callbackurl="<c:out value="${deactivateUserPath}" />"><spring:message code="label.myprofile.deactivate"/></a> |</p>
      </c:when>
      <c:otherwise>
       <c:if test="${user.user.emailVerified == true}">
        <spring:url value="/portal/users/{user}/activate_user" var="activateUserPath" htmlEscape="false"> 
          <spring:param name="user"><c:out value="${user.user.param}"/></spring:param>
      </spring:url>
      <p><a id="profile_activateuser" href="javascript:void(0)" callbackurl="<c:out value="${activateUserPath}" />"><spring:message code="label.myprofile.activate"/></a> |</p>
      </c:if>
      </c:otherwise>
      </c:choose>
      
      <spring:url value="/portal/users/{user}/delete" var="deleteUserPath" htmlEscape="false"> 
          <spring:param name="user"><c:out value="${user.user.param}"/></spring:param>
      </spring:url>
          <p><a href="javascript:void(0);" onclick='removeUser("<c:out value="${user.user.param}"/>");'><spring:message code="label.myprofile.remove"/></a></p>
      </div>
      </c:if>    
  </div>
<!-- Left panel for image and preferences --> 
<c:set var="displaySideLeftPanel" value="none" />

<c:if test="${Profile == 'on' }">
  <c:set var="displaySideLeftPanel" value="" />
</c:if>

<div id="sideLeftPanel" class="maincontent_smallverticalpanel" style="float:left;display:${displaySideLeftPanel}">
    <div id="gravatarImageBoxDiv "class="commonboxes_container">
        <div class="commonboxes profileimagebox">
            <div class="commonboxes_contentcontainer smallbox">
                <div class="profileimage_leftpanel">
                    <div class="profileimage_container" <c:if test="${doNotShowGravatarLink == true}">style="margin: 7px 0 0 50px;"</c:if>>
                        <div class="profilepic"><img src=<c:out value="${gravatarUrl}"></c:out> width="80px" height="80px"></img></div>
                      </div>
                  </div>
                  <div class="profileimage_rightpanel">
                    <div class="profileimage_rightlinksbox" <c:if test="${doNotShowGravatarLink == true}">style="display:none;"</c:if>>
                        <p><a href="http://en.gravatar.com/site/signup/" target="_blank"><spring:message code="label.myprofile.manage"/></a></p>
                      </div>
                  </div>
              </div>
          </div>
      </div>
    <div id="preferencesDiv" class="commonboxes_container" style="margin-top:30px;">
      <div class="commonboxes_titlebox">
          <h2><spring:message code="label.myprofile.preferences"/></h2>
        </div>
      <div class="commonboxes">
          <div class="commonboxes_contentcontainer smallbox">
                  <div class="commonboxes_formbox_small">
                      <ul>
                          <li style="border:none;">
                               <label for=""><spring:message code="label.myprofile.timezone"/></label>
                               <div id="showTimezone"
                                    title="<c:out value='${user.user.timeZone}'></c:out>"
                                    class="commonboxes_formbox_small_withouttextbox">
                               <c:out value="${user.user.timeZone}"></c:out> </div>
                               <div id="editTimezoneSelect" style="display:none;">
                                <spring:message code="label.timezone.tooltip" var="i18nTimezoneTooltip"/>
                                <select tabindex="200" class="select expandable-select" id="timeZone" name="timeZone" title="<c:out value="${i18nTimezoneTooltip}"/>">
                                  <option value="" ><spring:message code="label.myprofile.choose"/></option>
                                  <c:forEach items="${user.timeZones}" var="choice" varStatus="status">
                                    <c:set var="timeZoneKey" value="${choice.key}"></c:set>
                                    <c:set var="strippedtimeZoneKey" value="${fn:substringBefore(timeZoneKey,')')})"></c:set>
                                    <option value='<c:out value="${choice.value}" />' 
                                      <c:if test="${choice.value == user.user.timeZone}"><spring:message code="label.myprofile.selected"/></c:if>
                                       >
                                      <c:out value="${strippedtimeZoneKey} ${choice.value}"/>
                                    </option> 
                                  </c:forEach>
                               </select>
                               </div>
                            </li>
                          <li style="border:none;">
                               <label for=""><spring:message code="label.myprofile.language"/></label>
                               <div id="showLanguage"
                                    title="<c:out value='${userLocale}'></c:out>"
                                    class="commonboxes_formbox_small_withouttextbox">
                               <c:out value="${userLocale}"></c:out>
                                 </div>
                               <div id="editLanguage" style="display:none;">
                                <spring:message code="label.language.tooltip" var="i18nLanguageTooltip"/>
                                <select tabindex="200" class="select" id="locale" name="locale" title="<c:out value="${i18nLanguageTooltip}"/>">
                                  <c:forEach items="${supportedLocaleList}" var="locale" varStatus="status">
                                        <option value='<c:out value="${locale.key}" />' <c:if test="${locale.key == user.user.locale}">selected="selected"</c:if> >
                                          <c:out value="${locale.value}"></c:out>
                                        </option> 
                                  </c:forEach>
                               </select>
                               </div>
                            </li>
                         </ul>
                    </div>
                    
                    <div class="commonbox_submitbuttonpanel">
                        <div id="editTimeZoneDiv" class="commonbox_submitbuttonbox">
                            <p><a id="editTimeZone" href="#"><spring:message code="label.myprofile.edit"/></a></p>                                     
                        </div>
                        <div id="saveTimeZoneDiv" class="commonbox_submitbuttonbox" style="display:none;">
                            <p><a id="cancelTimeZoneEdit" href="#"><spring:message code="label.myprofile.cancel"/></a></p> 
                             <input id="savePref" class="commonbutton" rel="<spring:message code="label.saving.processing"/>" tabindex="221" type="button" value="<spring:message code="label.myprofile.save"/>"/>   
                        </div>
                    </div>
            </div>
        </div>
    </div>
                    
</div>
<!-- Right panel for profile and login info -->     
          
<div class="maincontent_bigverticalpanel" style="float:right;">
    <div id="top_message_panel" class="common_messagebox widget" style="display:none;">
      <button type="button" class="close js_close_parent" >&times;</button>
      <span id="status_icon"></span><p id="msg"></p>
    </div>

    <c:if test="${errormsg}">
    <div class="main_addnew_formbox_myprofile_errormsg" id="error_success" >
      <span><spring:message code="${userEditError}" /></span><br>
      <c:forEach var="field" items="${errorMsgList}">
        <span><c:out value="${field}" escapeXml="false"/></span><br>
      </c:forEach>
    </div>
  </c:if> 
<spring:url value="/portal/users/{user}/myprofile" var="editProfilePath" htmlEscape="false"> 
  <spring:param name="user"><c:out value="${user.user.param}"/></spring:param>
</spring:url>
<spring:url value="/portal/users/{user}/validateemail" var="validateemailpath" htmlEscape="false"> 
    <spring:param name="user"><c:out value="${user.user.param}"/></spring:param>
</spring:url>

<c:set var="displayUserForm" value="none" />

<c:if test="${Profile == 'on' }">
  <c:set var="displayUserForm" value="" />
</c:if>
<form:form commandName="user" id="userForm" cssClass="formPanel" action="${editProfilePath}" method="post" style="display:${displayUserForm}" >
  <div id="profileDetailsDiv" class="commonboxes_container">
      <div class="commonboxes_titlebox">
          <h2><spring:message code="label.details"/></h2>
        </div>
    <div class="commonboxes">
        <div class="commonboxes_contentcontainer bigbox">
            <div class="commonboxes_formbox bigformbox">
                <div class="commonboxes_formbox_panels">
                    <ul>
                        <div class="user_name_order">
                        <div class="user_first_name">
                        <li class="commonboxes_formbox">
                              <form:label path="user.firstName"><spring:message code="label.myprofile.firstName"/></form:label>
                              <div class="commonboxes_formbox_withouttextbox read" id="firstname_readvalue"><c:out value="${user.user.firstName}"></c:out></div>
                              <div class="mandatory_wrapper write" style="display:none;">
                                <spring:message code="label.userInfo.firstName.tooltip" var="i18nFirstNameTooltip"/>
                                <form:input id="firstname_writevalue" cssClass="text" tabindex="201" path="user.firstName" title="${i18nFirstNameTooltip}" maxlength="255"/>
                              </div>
                              <div class="main_addnew_formbox_myprofile_errormsg" id="firstname_writevalueError"></div>
                          </li>
                          </div>
                          <div class="user_last_name">
                          <li class="commonboxes_formbox">
                              <form:label path="user.lastName"><spring:message code="label.myprofile.lastName"/></form:label>
                              <div class="commonboxes_formbox_withouttextbox read" id="lastname_readvalue"><c:out value="${user.user.lastName}"></c:out></div>
                              <div class="mandatory_wrapper write" style="display:none;">   
                                <spring:message code="label.userInfo.lastName.tooltip" var="i18nLastNameTooltip"/>
                                <form:input id="lastname_writevalue" cssClass="text" tabindex="202" path="user.lastName" title="${i18nLastNameTooltip}" maxlength="255"/>
                              </div>                                            
                              <div class="main_addnew_formbox_myprofile_errormsg" id="lastname_writevalueError"></div>
                          </li>
                          </div>
                          </div>
                          <li class="commonboxes_formbox last">
                            <form:label path="user.enabled"><spring:message code="label.status"/></form:label>
                            <c:choose>
                              <c:when test="${!user.user.enabled}">
                                <div class="commonboxes_formbox_withouttextbox"><spring:message code="label.myprofile.disabled"/></div>
                              </c:when> 
                              <c:otherwise>
                                <c:choose>
                                  <c:when test="${!user.user.locked}">
                                    <div class="commonboxes_formbox_withouttextbox"><spring:message code="label.myprofile.active"/></div>
                                  </c:when>  
                                  <c:otherwise>
                                    <div class="commonboxes_formbox_withouttextbox"><spring:message code="label.myprofile.locked"/></div>
                                  </c:otherwise>
                                </c:choose>
                              </c:otherwise>
                            </c:choose>
                          </li>                                        
                           
                      </ul>
                  </div>
                  <div class="commonboxes_formbox_panels" style="border:none;">
                    <ul>
                    <li class="commonboxes_formbox">
                              <form:label path="user.emailVerified"><spring:message code="ui.users.all.header.email"/></form:label>
                        <div class="commonboxes_formbox_withouttextbox" id="PrimaryEmailDivId">
                             <c:out value="${user.user.email}"></c:out>
                        </div>
                    </li>

                    <c:if test="${user.phoneVerificationEnabled}">
                          <li class="write">
                                <label for="country"><spring:message code="label.moreUserInfo.country"/></label>
                                <div class="commonboxes_formbox_withouttextbox read" id="countryISDCode_readvalue" style="display:none;">US</div>
                        <div class="mandatory_wrapper">
                           <select class="select" tabindex="2080" id="country">
                             <c:forEach items="${filteredCountryList}" var="choice" varStatus="status">
                               <option value="<c:out value="${choice.countryCode2}"/>" <c:if test="${country_code_XX == choice.countryCode2}" >selected="selected" </c:if>><c:out value="${choice.name}" escapeXml="false"/></option>  
                             </c:forEach>
                           </select>
                        </div>
                        <div class="main_addnew_formbox_myprofile_errormsg" id="countryError"></div>
                         </li>
                     </c:if>

                          <li class="commonboxes_formbox last">
                              <form:label path="user.phone"><spring:message code="label.myprofile.phone"/></form:label>

                              <c:choose>
                                <c:when test="${user.countryCode != ''}">
                               <div class="<c:if test="${PhoneNumberMandatory}">mandatory_wrapper</c:if> write" style="display:none;">
                                  <div class="commonboxes_formbox_withouttextbox_plus_sign" >+</div>
                               </div>
                               <div class="read" >
                                  <div class="commonboxes_formbox_withouttextbox_plus_sign" >+</div>
                               </div>
                                    <div class="commonboxes_formbox_withouttextbox read" id="countryCode_readvalue" style= "width:35px;margin-left:0px;">
                                      <c:out value="${user.countryCode}"/>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                  <div class="commonboxes_formbox_withouttextbox read" id="countryCode_readvalue" style="text-align:right; width:35px;"></div>
                                </c:otherwise>
                             </c:choose>
                              
                              <div class="commonboxes_formbox_withouttextbox read" id="phone_readvalue" style="margin: 3px 0pt 10pt;width:150px;">
                                  <c:out value="${user.phone}"></c:out>
                              </div>
                                <c:choose>
                                    <c:when test="${PhoneNumberMandatory}">
                                        
                                 <c:choose>
                                   <c:when test="${user.phoneVerificationEnabled}">
                                   
                                      <label class="write" id="countryCode" style="width:15px;margin-left:10px"><c:out value="${user.countryCode}"/></label>
                                       <form:hidden id="countryCodeFormValue" path="countryCode" />
                                   </c:when>
                                   <c:otherwise>
                                     <spring:message code="label.moreUserInfo.countryCode.tooltip" var="i18nCountryCodeTooltip"/>
                                     <form:input cssClass="text write" tabindex="205" path="countryCode" title="${i18nCountryCodeTooltip}" style="width:30px;display:none;" maxlength="5"/>
                                    </c:otherwise>
                                  </c:choose>
                                   <spring:message code="label.moreUserInfo.phoneNumber.tooltip" var="i18nPhoneTooltip"/>
                                   <form:input cssClass="text write" tabindex="205" path="phone" title="${i18nPhoneTooltip}" style="width:90px;display:none;" maxlength="15"/>
                                   
                                    </c:when>                               
                               <c:otherwise>
                                    <div>
                                 <c:choose>
                                   <c:when test="${user.phoneVerificationEnabled}">
                                      <label class="write" id="countryCode" style="width:30px;margin-left:10px"><c:out value="${user.countryCode}"/></label>
                                       <form:hidden id="countryCodeFormValue" path="countryCode" />
                                   </c:when>
                                   <c:otherwise>
                                     <spring:message code="label.moreUserInfo.countryCode.tooltip" var="i18nCountryCodeTooltip"/>
                                     <form:input cssClass="text write" tabindex="205" path="countryCode" title="${i18nCountryCodeTooltip}" style="width:30px;display:none;"/>
                                    </c:otherwise>
                                  </c:choose>
                                   <spring:message code="label.moreUserInfo.phoneNumber.tooltip" var="i18nPhoneTooltip"/>
                                   <form:input cssClass="text write" tabindex="205" path="phone" title="${i18nPhoneTooltip}" style="width:90px;display:none;"/>
                                   </div>
                               </c:otherwise>
                               </c:choose>
                                
                              <c:if test="${user.phoneVerificationEnabled}">
                                    <div style="margin-top:4px"><a id="phoneVerify"  href="#" tabindex="205" style="margin-left:5px; height:20px;display:none;"><spring:message code="label.verify"/></a></div>
                                      <div class="phonever_statusbox" id="verificationStatusSuccess" style="margin-left: 10px;display:none;width: 60px; margin-top: -2px;">
                                          <div class="phonever_statusicon verified"></div>
                                          <span class="verified" style="margin-left: 3px"><spring:message code="label.phoneVerification.verified"/></span>
                                      </div>
                                    <form:hidden path="userEnteredPhoneVerificationPin" />
                              </c:if> 
                                <div class="main_addnew_formbox_myprofile_errormsg" id="countryCodeError"></div>
                                <div class="main_addnew_formbox_myprofile_errormsg" style="margin: 5px 0 0 110px;" id="phoneError"></div>
                                <div class="main_addnew_formbox_myprofile_errormsg" style="margin: 5px 0 0 110px;" id="userEnteredPhoneVerificationPinError"></div>
                                <form:errors path="phone" cssClass="main_addnew_formbox_myprofile_errormsg"></form:errors>
                                <c:if test="${user.phoneVerificationEnabled}">
                                 <div id="phone-dialog-modal" title="<spring:message code="ui.label.phoneverification.dialog.title"/>">
                                      <div style="margin-top:10px; margin-left:20px;">
                                        <form:label path="userEnteredPhoneVerificationPin"><spring:message code="label.moreUserInfo.phoneVerificationPin"/></form:label> 
                                        <spring:message code="label.moreUserInfo.phoneVerificationPin.tooltip" var="i18nPhoneVerificationPinTooltip"/>
                                        <input class="text" tabindex="205" id="phoneVerificationPin" title="<c:out value="${i18nPhoneVerificationPinTooltip}"/>"/>
                                      </div>
                                      <a id="phoneVerificationCall"  class="callme_button" href="#" tabindex="205" style="margin-left:25px; margin-top:25px; height:20px;"><span class="call_icon"></span><spring:message code="label.phoneVerification.callMe"/></a>
                                      <a id="phoneVerificationSMS"  class="callme_button" href="#" tabindex="205" style="margin-left:17px; margin-top:25px; height:20px;"><span class="text_icon"></span><spring:message code="label.phoneVerification.textMe"/></a>
                                      <div class="phonever_statusbox" id="verificationStatusFailed" style="margin-left: 10px;display:none">
                                          <div class="phonever_statusicon unverified"></div>
                                          <span class="unverified"><spring:message code="label.phoneVerification.unverified"/></span>
                                      </div>
                                   </div>
                                </c:if>
                          </li>
   
                           <c:set var="userObj" value="${user.user}" scope="request"></c:set>
                        <tiles:insertDefinition name="user.custom.fields"></tiles:insertDefinition>

                      </ul>
                    </div>
                  </div>
            </div>

              <div class="commonbox_submitbuttonpanel">
                  <div id="editProfileDetails" class="commonbox_submitbuttonbox read">
                      <p><a href="javascript:void(0);"><spring:message code="label.myprofile.edit"/></a> </p>                                     
                  </div>
                  <div id="saveProfileDetails" class="commonbox_submitbuttonbox write">
                    <p><a id="cancelSaveProfileDetails" href="javascript:void(0);"><spring:message code="label.myprofile.cancel"/></a></p> 
                    <input id="profileDetailsSubmit" class="commonbutton" tabindex="221" type="submit" value="<spring:message code="label.myprofile.save"/>"/>   
                  </div>
              </div>

          </div>
      </div>
   </form:form>
                     
<!-- login info  -->
<c:set var="displayLoginDiv" value="none" />

<c:if test="${Profile == 'on' }">
  <c:set var="displayLoginDiv" value="" />
</c:if>

  <div id="loginDiv" class="commonboxes_container" style="margin-top:30px;display:${displayLoginDiv}" >
    <div class="commonboxes_titlebox">
          <h2><spring:message code="label.myprofile.loginInfo"/></h2>
        </div>
  <div class="commonboxes">
      <div class="commonboxes_contentcontainer bigbox">
       <spring:url value="/portal/users/changePassword" var="editProfilePath" htmlEscape="false"> 
                <spring:param name="user"><c:out value="${user.user.param}"/></spring:param>
       </spring:url>     
       <spring:url value="/portal/portal/users/{user}/validateemail" var="validateemailpath" htmlEscape="false"> 
        <spring:param name="user"><c:out value="${user.user.param}"/></spring:param>
        </spring:url>
        <form:form commandName="user" id="userPasswordForm" cssClass="ajaxform" action="${editProfilePath}" onsubmit="changePassword(event,this)" method="post">
          <div class="commonboxes_formbox bigformbox">             
              <div class="commonboxes_formbox_panels" style="border:none;">
                  <ul>                                                              
                      <li class="commonboxes_formbox">
                      <input id="username_hidden" type="hidden" value="<c:out value="${user.user.username}"></c:out>"/>
                            <label for="name"><spring:message code="label.myprofile.username"/></label>
                            <div class="commonboxes_formbox_withouttextbox"><c:out value="${user.user.username}"></c:out></div>
                        </li>
                        <li class="commonboxes_formbox last">
                            <label for="name"><spring:message code="label.myprofile.profileType"/></label>
                            <div class="commonboxes_formbox_withouttextbox"><spring:message code="profileName.${fn:replace(user.user.profile.name, ' ', '')}"/></div>
                        </li>
                        
                    </ul>
                </div>
                <div class="commonboxes_formbox_panels" style="border-left: 1px dotted #333333; border-right:none;">
                  <ul>
                        <li class="commonboxes_formbox">
                            <label for="name"><spring:message code="label.myprofile.lastLogin"/></label>
                            <div id='last_login_time' class="commonboxes_formbox_withouttextbox"> 
                               <spring:message code="date.format" var="date_format"/>
                               <fmt:formatDate value="${lastLogin}" pattern="${date_format}" timeZone="${user.user.timeZone}"/>
                            </div>
                        </li>
                        <li class="commonboxes_formbox last" style="border-bottom: none;">
                            <label for="name"><spring:message code="label.myprofile.logins"/></label>
                            <div class="commonboxes_formbox_withouttextbox"> <c:out value="${logins}"></c:out></div>
                        </li>
                       
                        <li class="write" style="border-top:1px dotted #666666; padding: 8px 0 0;display:none;">
                            <form:hidden path="username" />
                         <form:label cssClass="read" path="user.oldPassword" ><spring:message code="label.myprofile.oldPassword"/></form:label>
                          <form:label cssClass="write" path="user.oldPassword"><spring:message code="label.myprofile.oldPassword"/></form:label>
                            <div class="mandatory_wrapper write">
                              <spring:message code="label.oldPassword.tooltip" var="i18nOldPasswordTooltip"/>
                              <form:password cssClass="text" tabindex="202" path="user.oldPassword" title="${i18nOldPasswordTooltip}" maxlength="50"/>
                            </div>
                            <div class="main_addnew_formbox_myprofile_errormsg" id="user.oldPasswordError"></div>
                         </li>
                        <li class="write" style="display:none;">
                            <form:label cssClass="read" path="user.clearPassword" ><spring:message code="label.myprofile.password"/></form:label>
                            <form:label cssClass="write" path="user.clearPassword"><spring:message code="label.myprofile.newPassword"/></form:label>
                            <div class="mandatory_wrapper write">
                              <spring:message code="label.newPassword.tooltip" var="i18nNewPasswordTooltip"/>
                              <form:password cssClass="text" tabindex="202" path="user.clearPassword" title="${i18nNewPasswordTooltip}" maxlength="50"/>
                            </div>                                            
                            <div class="main_addnew_formbox_myprofile_errormsg" id="user.clearPasswordError"></div>
                        </li>
                        <li class="write" style = "border-bottom: none;display:none;">
                            <label for="password_confirm"><spring:message code="label.myprofile.confirmNewPassword"/></label>
                            <div class="mandatory_wrapper">
                              <spring:message code="label.newPassword.confirm.tooltip" var="i18nConfirmNewPasswordTooltip"/>
                              <input type="password" class="text" tabindex="202" id="password_confirm"  name="password_confirm" maxlength="50" title="<c:out value="${i18nConfirmNewPasswordTooltip}"/>"/>
                            </div>                                            
                            <div class="main_addnew_formbox_myprofile_errormsg" id="password_confirmError"></div>
                        </li>
                    </ul>
                </div>
          </div>
          <c:choose>
            <c:when test="${currentUser.param ne user.user.param}">
              <div class="commonbox_submitbuttonpanel">
                  <div id="resetPasswordForm" class="commonbox_submitbuttonbox read">
                      <c:if test="${doNotShowPasswordEditLink != 'true'}">
                        <p><a class="request_password_reset_link" href="javascript:void(0);"><spring:message code="label.instances.reset.password"/></a> </p>
                      </c:if>                                     
                  </div>
                </div>
              </c:when>
              <c:otherwise>
                 <div class="commonbox_submitbuttonpanel">
                    <div id="editPasswordForm" class="commonbox_submitbuttonbox read">
                       <c:if test="${doNotShowPasswordEditLink != 'true'}">
                        <p><a href="javascript:void(0);"><spring:message code="label.myprofile.edit.password"/></a> </p>                                     
                        </c:if>
                    </div>
                  <div id="savePasswordForm" class="commonbox_submitbuttonbox write">
                      <p><a id="cancelSavePasswordForm" href="javascript:void(0);"><spring:message code="label.myprofile.cancel"/></a></p> 
                      <input rel="<spring:message code="label.saving.processing"/>" class="commonbutton" tabindex="221" type="submit" value="<spring:message code="label.myprofile.save"/>"/>   
                   </div>
                 </div>
              </c:otherwise>
            </c:choose>
            </form:form>
        </div>
    </div>
    </div>
    </div>  

<!-- API Credentials  -->

<div  class="maincontent" style="margin:0;">
    <div id="userCredentialDiv" class="maincontent_horizontalpanels" style="margin:0;">
    
        <div id="bssApiCredentialsDiv" class="commonboxes_container" style="display:none;" >
             <div id="titleDiv" class="commonboxes_titlebox">
                 <h2><spring:message code="label.myprofile.bss" /> - <spring:message code="page.level2.apicredentials"/></h2>
              </div>
              <div class="commonboxes">
                <div class="commonboxes_contentcontainer fullwidthbox">
                  <div class="commonboxes_formbox fullwidthbox">
                     <span id="serviceLogo" class="apikeyLogo"></span>  
                     <div class="commonboxes_formbox_panels fullwidth" style="float:right; width:750px;">
                            <ul>    
                               <li class="commonboxes_formbox fullwidth" style="width:730px;" >
                                    <label ><spring:message code="label.myprofile.apikey"/></label>
                                    <div class="commonboxes_formbox_withouttextbox fullwidth" style="width:620px;" id="bssApiKey"></div>
                               </li> 
                               <li  class="commonboxes_formbox fullwidth" style="width:730px;" >
                                    <label><spring:message code="label.myprofile.secretKey" /></label>
                                    <div class="commonboxes_formbox_withouttextbox fullwidth" style="width:620px;" id="bssSecretKey"></div>
                               </li>  
                               <li  class="commonboxes_formbox fullwidth" style="width:730px;" >
                                    <label><spring:message code="label.myprofile.endPointURL" /></label>
                                    <div class="commonboxes_formbox_withouttextbox fullwidth" style="width:620px;" id="bssEndpointURL"></div>
                               </li>  
                            </ul>
                            <input type="button" tabindex="100" style="margin-top:10px;" class="commonbutton submitmsg" value="<spring:message code="label.myprofile.generate.apikey.secretkey" />" id="generateAPIKey">
                      </div>
                  </div>
                 </div>
               </div>
         </div>
         
    
         <div id="apiCredentialsDiv" class="commonboxes_container" style="display: none;">
             <div id="titleDiv" class="commonboxes_titlebox">
                 <h2><spring:message code="page.level2.cloudstack"/> - <spring:message code="page.level2.apicredentials"/></h2>
              </div>
              <div class="commonboxes">
                <div class="commonboxes_contentcontainer fullwidthbox">
                  <div class="commonboxes_formbox fullwidthbox">
                     <span id="serviceLogo" class="apikeyLogo"></span>  
                     <div class="commonboxes_formbox_panels fullwidth" style="float:right; width:750px;">
                            <ul id="userCredentialUl">    
                               <li  id="userCredentialLi"   class="commonboxes_formbox fullwidth" style="width:730px;" >
                                    <label id="userCredentialLable" for="lableFor" ></label>
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
<!-- Notification Preference  -->
<c:set var="displayNotPrefDiv" value="none" />

<c:if test="${NotifictaionPrefrences == 'on' }">
  <c:set var="displayNotPrefDiv" value="" />
</c:if>

<div id="notPrefDiv" class="maincontent" style="margin:0; display:${displayNotPrefDiv}">
  <div class="maincontent_horizontalpanels" style="margin:0;">
      <h3><spring:message code="label.myprofile.preferences.setupChannels"></spring:message></h3>
      <span class="subtitle"><spring:message code="label.myprofile.preferences.configure"></spring:message></span> 
      <div class="maincontent_equalverticalpanel">
        <div class="delivery_channelsbox">
          <div class="db_statsbox title user">
              
                <h2><spring:message code="label.myprofile.email"></spring:message></h2>
                
            </div>
            
            <div class="deliverychannel_list">
              <ul id="notPrefEmailReadOnlyDiv">
                  <c:forEach var="alertsPref" items="${alertsPrefs}" varStatus="status">
                    <li id="notPrefEmailReadOnlyDivLi<c:out value="${alertsPref.id}"></c:out>"><c:out value="${alertsPref.emailAddress}"></c:out></li>
                    </c:forEach>
                </ul>
            </div>
            <div class="db_statsbox_footerlinksbox" >
               <p><a id="addDeleteEmailLink" href="#"><spring:message code="label.myprofile.addDeleteEmail"></spring:message></a></p>
            </div>
        </div>
      </div>
  </div>
    
    <div class="maincontent_horizontalpanels" style="margin-top:30px;display: none">
      <h3><spring:message code="label.myprofile.preferences.enablenotification"/></h3>
      <span class="subtitle"><spring:message code="label.myprofile.preferences.receivealertmedium"/>:</span>
    </div>
</div>

<!-- email div -->
<div id="emailAddressesDiv" class="maincontent" style="margin:0; display:none;">
  <div class="maincontent_horizontalpanels" style="margin:0;">
      <span class="subtitle"><spring:message code="label.myprofile.preferences.configure"></spring:message></span> 
     
       
        <div id="managenotificationDiv">
        <c:set var="alertsPrefs" value="${alertsPrefs}" scope="request"></c:set>
         <c:set var="alertsPrefsSize" value="${alertsPrefsSize}" scope="request"></c:set>
          <c:set var="addAlertEmailLimit" value="${addAlertEmailLimit}" scope="request"></c:set>
           <c:set var="user" value="${user.user}" scope="request"></c:set>
            <c:set var="tenant" value="${tenant}" scope="request"></c:set>
        <jsp:include page="../notifications/alerts_delivery_options.jsp"></jsp:include>  
        </div>
  </div>
    
</div>


<input id="doNotShowVerifyUserDiv" type="hidden" name="doNotShowVerifyUserDiv" value="${doNotShowPasswordEditLink}"> 
<div id="verifyUserDiv" title='<spring:message code="user.password.verification" />' style="min-height:150px; overflow: hidden; display:none">
  <tiles:insertDefinition name="user.password_verification"/>
</div>

<input id="previous_tab" type="hidden" name="previous_tab" value="profile"> 
 

<div id="alert_message_dialog" style="display:none; height: auto; min-height: 65px; width: auto; margin: 25px 10px 20px;"
       title="<spring:message code="ui.label.message"/>">
</div>


<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="csrf" uri="http://www.owasp.org/index.php/Category:OWASP_CSRFGuard_Project/Owasp.CsrfGuard.tld"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<script type="text/javascript">
//validation for add user functionality
$(document).ready(function() {
$("#add_user_form_step1").validate( {
    //debug : true,
    success : "valid",
    ignoreTitle : true,
    rules : {
      "user.firstName" : {
      required: true,
        minlength : 1,
        maxlength: 255,
        flname:true
      },
      "user.lastName" : {
        required:true,
        minlength : 1,
        maxlength: 255,
        flname:true
      },
      "user.email": {
      required : true,
      email : true
      },
      "confirmEmail": {
          required : true,
        email : true,
        equalTo : "#user\\.email"
          },
      "user.username": {
      required : true,
      minlength : 5,
      validateUsername : true,
      remote : {
          url : '/portal/portal/users/validate_username'
        }
      },        
      "userProfile" : {
        required: true
        
      },       
      "channelParam" : {
        required: true
      }  
    },
    messages: {
       "user.title":{
          required: i18n.user.title
      },
      "user.firstName":{
        required: i18n.user.firstname,
        flname: i18n.user.flnameValidationError
       },
      "user.lastName": {
        required: i18n.user.lastname,
        flname: i18n.user.flnameValidationError
    },
      "user.email": {
        required: i18n.user.email,
        email: i18n.user.emailformat
      },
      "confirmEmail" : {
          required: i18n.user.confirmemail,
        email: i18n.user.emailformat,
        equalTo: i18n.user.emailmatch
          },
      "user.username": {
         required: i18n.user.username,
           minlength: i18n.user.minLengthUsername,
           validateUsername: i18n.user.validateUsername,
           remote: i18n.user.usernameexists
      },                  
      "userProfile" : {
        required: i18n.user.profilerequired
    },
      "channelParam": {
        required:i18n.user.channel
      }
    },
    errorPlacement: function(error, element) { 
      var name = element.attr('id');
      name =name.replace(".","\\.");      
      if(error.html() !=""){
        error.appendTo( "#"+name+"Error" );
      }
        }
  });

});

function toggleAdImport(){
  if($("#add_user_form_step1").validate().element("#user\\.username"))
    {
      $("#import_user_info_from_ad").removeAttr("disabled");
    }
    else{
      $("#import_user_info_from_ad").attr("disabled", true);
    }
}

$("#user\\.username").mouseout(function(){
  toggleAdImport();
});

$("#user\\.username").focusout(function(){
  toggleAdImport();
});

var step1FinishUrl="<%=request.getContextPath() %>/portal/users/new/step1";
var customEmailFinishURL="<%=request.getContextPath() %>/portal/users/new/step2";
</script>
<input id="newUsersPageCount" type="hidden" name="newUsersPageCount" value="<c:out value="${newUsersPageCount}"/>"/><!-- Set from Abstractusercontroller step1 post -->
<input id="newUsersPerPageCount" type="hidden" name="newUsersPerPageCount" value="<c:out value="${currentPerPagesize}"/>"/><!-- Set from Abstractusercontroller listusers -->


<form:form name="add_user_form" commandName="user" id="add_user_form_step1" cssClass="ajaxform">
 <div class="widget_wizardcontainer">
         <c:if test="${signuperror != null && signuperror=='emaildomainblacklisted'}">
          <div class="common_messagebox success"><p><spring:message code="signup.emaildomain.blacklist.error"/></p></div>
        </c:if>
        <%System.out.println("CurrentNewuser Step"+session.getAttribute("currentStep")); %>
   <c:if test="${currentStep=='newUserCreationStep1'}">
        <%System.out.println("CurrentNewuser Step processing"+session.getAttribute("currentStep")); %>
          <div id="step1" class="j_productspopup" ><input type="hidden" id="nextstep" name="nextstep" value="step2">
                      <input type="hidden" id="prevstep" name="prevstep" value=""> <input type='hidden' id='submitButtonEmail'   name="submitButtonEmail" value="">
              <div class="widgetwizard_stepsbox">
                           <div class="widgetwizard_steps_contentcontainer">
                              <div class="widgetwizard_stepscenterbar">
                                    <ul>
                                      <li class="widgetwizard_stepscenterbar first" style="width: 80px"><span class="steps active"><span
                                        class="stepsnumbers active">1</span></span> <span class="stepstitle active"><spring:message htmlEscape="false"
                                        code="label.newUserStep1.addUser" /> </span></li>
                                      <li class="widgetwizard_stepscenterbar fivestepswizard centerAlignStep" style="width: 80px"><span class="steps"><span
                                        class="stepsnumbers">2</span></span><span class="stepstitle"><spring:message htmlEscape="false"
                                        code="label.newUserStep2.customize" /></span></li>
                                  
                                      <li class="widgetwizard_stepscenterbar fivestepswizard last" style="width: 80px"><span class="steps last"><span
                                        class="stepsnumbers last">3</span></span><span class="stepstitle last"><spring:message htmlEscape="false"
                                        code="label.newUserStep2.finish" /></span></li>
                                  
                                    </ul>
                                 </div>
                              </div>
                 </div>
    
              <div class="widgetwizard_contentarea widgetwizard_contentareabig">
                         <div class="widgetwizard_boxes fullheight3step">
                         
                         <div id="inprogress_spinning_wheel_1" style="height:100%;width:100%; position:absolute;display:none;" >
                           <div class="widget_blackoverlay " style="height:100%;"></div>
                            <div class="widget_loadingbox " style="top:45%;">
                              <div class="widget_loaderbox">
                                <span class="bigloader"></span>
                              </div>
                              <div class="widget_loadertext">
                                <p id="in_process_text">
                                  <spring:message code="label.loading" />
                                  &hellip;
                                </p>
                              </div>
                            </div>
                          </div> 
                              <div class="widgetwizard_titleboxes">
                                     <h2><spring:message code="label.newUserStep1.addUser" /></h2>
                             </div>
            
                           <div class="widgetwizard_detailsbox">
                                                <ul>
                                                   <li><span class="label"> <label for="user.username"><spring:message
                                                    code="label.newUserStep2.username" /></label></span>
                                                  <div class="mandatory_wrapper"><spring:message code="label.userInfo.username.tooltip"
                                                    var="i18nUsernameTooltip" /> <form:input cssClass="text" tabindex="106" path="user.username"
                                                    title="${i18nUsernameTooltip}" maxlength="50"/></div>
                                                  <div class="main_addnew_formbox_errormsg_popup" id="user.usernameError"><form:errors path="user.username"
                                                    cssStyle="color:red"></form:errors></div>
                                                    <c:if test="${showImportAdButton=='true'}">
                                                      <input class="basic_button adimport" type="button" onclick="importAdUser(this)" value="<spring:message code="label.tenants.uername.import.ds.button" />" name="<spring:message code="label.tenants.uername.import.ds.button"/>" id="import_user_info_from_ad" style="padding-left: 10px; margin-left: 88px; margin-top: -33px; margin-bottom: 0px;" disabled="disabled"/>
                                                      <input type="hidden" id="ad_import_enabled" value="ad_import_enabled"/>
                                                    </c:if>
                                                  </li>
                                                                                                  
                                                  <li><span class="label"><form:label path="user.email">
                                                    <spring:message code="label.newUserStep1.email" />
                                                  </form:label></span>
                                                  <div class="mandatory_wrapper"><spring:message code="label.userInfo.yourEmail.tooltip" var="i18nEmailTooltip" />
                                                  <form:input cssClass="text" tabindex="107" path="user.email" title="${i18nEmailTooltip}" maxlength="50"/></div>
                                                       <div class="main_addnew_formbox_errormsg_popup"  id="user.emailError"> <form:errors path="user.email"></form:errors></div>
                                                     </li>
                                                  <div class="user_name_order">
                                                    <div class="user_first_name">
                                                      <li><span class="label"> <form:label path="user.firstName">
                                                        <spring:message code="label.newUserStep1.firstName" />
                                                      </form:label></span>
                                                      <div class="mandatory_wrapper"><spring:message code="label.userInfo.firstName.tooltip"
                                                        var="i18nFirstNameTooltip" /> <form:input cssClass="text" tabindex="108" path="user.firstName"
                                                        title="${i18nFirstNameTooltip}" /></div>
                                                      <div class="main_addnew_formbox_errormsg_popup" id="user.firstNameError"><form:errors path="user.firstName"
                                                        cssClass="ErrorColor"></form:errors></div>
                                                      </li>
                                                    </div>
                                                    <div class="user_last_name">
                                                      <li><span class="label"> <form:label path="user.lastName">
                                                        <spring:message code="label.newUserStep1.lastName" />
                                                      </form:label></span>
                                                  
                                                      <div class="mandatory_wrapper"><spring:message code="label.userInfo.lastName.tooltip"
                                                        var="i18nLastNameTooltip" /> <form:input cssClass="text" tabindex="109" path="user.lastName"
                                                        title="${i18nLastNameTooltip}" /></div>
                                                        <div class="main_addnew_formbox_errormsg_popup" id="user.lastNameError"><form:errors path="user.lastName"
                                                        cssStyle="color:red"></form:errors></div>
                                                      </li>
                                                    </div>
                                                  </div>

                                                  <c:if test="${displayChannel}">
                                                    <li><span class="label"> <form:label path="channelParam">
                                                      <spring:message code="label.newUserStep1.channel" />
                                                    </form:label></span>
                                                    <div class="mandatory_wrapper"><spring:message code="label.channel.tooltip" var="i18nChannelTooltip" /> <form:select
                                                      cssClass="text" tabindex="110" path="channelParam" title="${i18nChannelTooltip}">
                                                      <c:forEach items="${channels}" var="choice" varStatus="status">
                                                        <form:option value="${choice.param}">
                                                          <c:out value="${choice.name}" />
                                                        </form:option>
                                                      </c:forEach>
                                                    </form:select></div>
                                                    <div class="main_addnew_formbox_errormsg_popup" id="channelParamError" style="color:red"></div>
                                                    </li>
                                                  </c:if>
                                                  <li><span class="label"> <form:label path="userProfile">
                                                    <spring:message code="label.newUserStep1.profile" />
                                                  </form:label></span>
                                                  <div class="mandatory_wrapper"><spring:message code="label.profile.tooltip" var="i18nProfileTooltip" /> <form:select
                                                    cssClass="text" tabindex="111" path="userProfile" title="${i18nProfileTooltip}">
                                                    <form:option value="">
                                                      <spring:message code="label.myprofile.choose" />
                                                    </form:option>
                                                    <c:forEach items="${user.validProfiles}" var="choice" varStatus="status">
                                                      <form:option value="${choice.id}">
                                                        <spring:message code="profileName.${fn:replace(choice.name, ' ', '')}"/>
                                                      </form:option>
                                                    </c:forEach>
                                                  </form:select></div>
                                                  <div class="main_addnew_formbox_errormsg_popup" id="userProfileError"><form:errors path="userProfile"
                                                    cssStyle="color:red"></form:errors></div>
                                              
                                                  </li>
                                              
                                                  <li><span class="label"> <form:label path="timeZone">
                                                    <spring:message code="label.newUserStep1.timezone" />
                                                  </form:label></span>
                                                  <div class="nonmandatory_wrapper"><spring:message code="label.timezone.tooltip" var="i18nTimezoneTooltip" />
                                                  <form:select cssClass="text" tabindex="112" path="timeZone" title="${i18nTimezoneTooltip}">
                                                    <form:option value="">
                                                      <spring:message code="label.myprofile.choose" />
                                                    </form:option>
                                                    <c:forEach items="${user.timeZones}" var="choice" varStatus="status">
                                                      <option value='<c:out value="${choice.value}" />'
                                                        <c:if test="${choice.value == user.timeZone}">selected="selected"</c:if>><c:out value="${choice.key}" />
                                                      </option>
                                                    </c:forEach>
                                                  </form:select></div>
                                                  <div class="main_addnew_formbox_errormsg_popup" id="timeZone.error" style="color:red"></div>
                                                  </li>
                                                  <li><span class="label"><form:label path="userLocale">
                                                    <spring:message code="label.newUserStep1.locale" />
                                                  </form:label></span> 
                                                  
                                                  <div class="nonmandatory_wrapper"><spring:message code="label.language.tooltip" var="i18nLanguageTooltip" />
                                                   <form:select cssClass="text"
                                                    tabindex="113" path="userLocale" title="${i18nLanguageTooltip}">
                                                    <c:set var="userLoc" value="${user.userLocale}" />
                                                    <c:if test="${empty userLoc}">
                                                      <c:set var="userLoc" value="${defaultLocale}" />
                                                    </c:if>
                                                    <c:forEach items="${supportedLocaleList}" var="locale" varStatus="status">
                                                      <option value='<c:out value="${locale.key}" />' <c:if test="${locale.key == userLoc}">selected="selected"</c:if>>
                                                      <c:out value="${locale.value}"></c:out></option>
                                              
                                                    </c:forEach>
                                                  </form:select></div>
                                                  <div class="main_addnew_formbox_errormsg_popup" id="userLocale.error" style="color:red"></div>
                                                  </li>
                                                 <tiles:insertDefinition name="user.custom.fields"></tiles:insertDefinition>
                                                                                              
                                                </ul>
                                              
                                              
                                                </div>
                                        </div>
                                   </div>
                              </div>
                               <div class="commonbox_submitbuttonbox position_right">
                                 <p><a href="javascript:void(0);"  onclick="return destroyDialog()"  ><spring:message code="label.newUserStep1.cancel"/></a></p>
                                  <input class="commonbutton" tabindex="114" name="submitButtonFinish" id="submitButtonFinish" type="button"
                                     onclick="addNewUserButtonStep1Finish()" value="<spring:message code="label.newUserStep2.finish"/>" 
                                      <c:if test="${showImportAdButton=='true'}">disabled</c:if> 
                                      />
                                                  <p><spring:message code="label.newUserStep2.or"/></p>
                                                  <input class="commonbutton" tabindex="115" name="submitButtonEmail" id="ButtonEmail" type="button"
                                                    onclick="addNewUserButtonStep1CustomEmail()" value="<spring:message code="label.newUserStep2.customize"/>" 
                                                     <c:if test="${showImportAdButton=='true'}">disabled</c:if>
                                                    /> 
                                  </div>              
                                                 
                              <%--  Step1 ends --%>
    </c:if>
    
    <c:if test="${currentStep=='newUserCreationStep2'}">
   
                              <%--Step2 Starts --%>
                              <div id="step2" class="j_productspopup" >
                                 <input type="hidden" id="nextstep" name="nextstep" value="step2">
                                  <div class="widgetwizard_stepsbox">
                                    <div class="widgetwizard_steps_contentcontainer">
                                      <div class="widgetwizard_stepscenterbar">
                                        <ul>
                                            <li class="widgetwizard_stepscenterbar first" style="width: 80px"><span class="steps completedsteps"><span
                                                class="stepsnumbers">1</span></span> <span class="stepstitle active"><spring:message htmlEscape="false"
                                                code="label.newUserStep1.addUser" /> </span></li>
                                            <li class="widgetwizard_stepscenterbar fivestepswizard centerAlignStep" style="width: 80px"><span class="steps active"><span
                                              class="stepsnumbers active">2</span></span><span class="stepstitle"><spring:message htmlEscape="false"
                                              code="label.newUserStep2.customize" /></span></li>
                                              
                                            <li class="widgetwizard_stepscenterbar fivestepswizard last" style="width: 80px"><span class="steps last"><span
                                              class="stepsnumbers last">3</span></span><span class="stepstitle last"><spring:message htmlEscape="false"
                                              code="label.newUserStep2.finish" /></span></li>
                                        </ul>
                                      </div>
                                   </div>
                                  </div>
                                  <div class="widgetwizard_contentarea widgetwizard_contentareabig" style="height:auto;">
                                     <div class="widgetwizard_boxes fullheight3step">
                                       <div id="inprogress_spinning_wheel_2" style="height:100%;width:100%; position:absolute;display:none;" >
                                         <div class="widget_blackoverlay " style="height:100%;"></div>
                                          <div class="widget_loadingbox " style="top:45%;">
                                            <div class="widget_loaderbox">
                                              <span class="bigloader"></span>
                                            </div>
                                            <div class="widget_loadertext">
                                              <p id="in_process_text">
                                                <spring:message code="label.loading" />
                                                &hellip;
                                              </p>
                                            </div>
                                          </div>
                                        </div> 
                                         <div class="widgetwizard_titleboxes">
                                            <h2><spring:message code="label.newUserStep1.addUser" /></h2>
                                           </div>
                                                 <div class="widgetwizard_detailsbox">
                                                  <c:out value="${emailText}" escapeXml="false"/>
                                                 </div>
                                      </div>
                                  </div>
                                   <div class="commonbox_submitbuttonpanel">
                                                  <div class="commonbox_submitbuttonbox position_right">
                                                      <p><a href="javascript:void(0);"  onclick="return destroyDialog()"  ><spring:message code="label.newUserStep1.cancel"/></a></p>
                                                       <input class="commonbutton" tabindex="221" name="submit" type="button" onclick="addNewUserCustomEmailFinish()" value="<spring:message code="label.newUserStep3.finish"/>"/>    
                                                  </div>
                                     </div>
                                  </div>
          </c:if>

     <c:if test="${currentStep=='newUserCreationStep3'}">
   
                                  <%--Step3 Finish--%>
                              <div id="step3" class="j_productspopup">
                                     <input type="hidden" id="nextstep" name="nextstep" value="" >
                                     <input type="hidden" id="prevstep" name="prevstep" value="step4" >
                                      <div class="widgetwizard_stepsbox">
                                          <div class="widgetwizard_steps_contentcontainer">
                                              <div class="widgetwizard_stepscenterbar ">
                                                   <ul>
                                                        <li class="widgetwizard_stepscenterbar first" style="width: 80px"><span class="steps completedsteps"><span
                                                          class="stepsnumbers">1</span></span> <span class="stepstitle active"><spring:message htmlEscape="false"
                                                          code="label.newUserStep1.addUser" /> </span></li>
                                                        <li class="widgetwizard_stepscenterbar fivestepswizard centerAlignStep" style="width: 80px"><span class="steps completedsteps"><span
                                                          class="stepsnumbers">2</span></span><span class="stepstitle"><spring:message htmlEscape="false"
                                                          code="label.newUserStep2.customize" /></span></li>
                                                          
                                                        <li class="widgetwizard_stepscenterbar fivestepswizard last" style="width: 80px"><span class="steps active last">
                                                        <span class="stepsnumbers last">3</span></span><span class="stepstitle last"><spring:message htmlEscape="false"
                                                          code="label.newUserStep2.finish" /></span></li>
                                                          
                                                 </ul>
                                              </div>
                                          </div>
                                      </div>
                                      <div class="widgetwizard_contentarea widgetwizard_contentareabig">
                                          <div class="widgetwizard_boxes fullheight3step">
                                              <div class="widgetwizard_successbox">
                                                <div class="widgetwizard_successbox">
                                                    <div class="widget_resulticon success"></div>
                                                      <p><spring:message code="ui.user.successfully.completed.text"/></p>
                                                       <p><spring:message code="label.newUserStep4.thankyou" arguments="${currentUser.firstName}, ${currentUser.lastName}"/>
                                                           <br/><br/><spring:message code="label.newUserStep4.message"/><c:if test="${tenant.state == 'ACTIVE'}"> 
                                                                         <spring:message code="label.newUserStep4.emailconfirmation"/>
                                                                         </c:if></p>
                                                     </div>             
                                              </div>
                                          </div>
                                      </div>
                 </div>
                  <div id="buttons" class="widgetwizard_nextprevpanel">  
            <input class="widgetwizard_nextprevpanel submitbutton" type="button" onclick="closeUserCreationWizard()" value="<spring:message code="label.close"/>" name="Close" id="add_product_next">
          </div>
            </c:if>

           </div>

</form:form>

<script type="text/javascript">
  swap_name_order_tab_index("user_first_name", "user_last_name");
</script>

<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/enable_instance.js"></script>
<script language="javascript" type="text/javascript">
  var dictionary = {viewMasked: '<spring:message javaScriptEscape="true" code="label.show"/>',
        hideMasked: '<spring:message javaScriptEscape="true" code="label.hide"/>',
        tncAcceptMessage:'<spring:message javaScriptEscape="true" code="you.can.not.continue.until.you.accept.the.terms.and.conditions"/>',
        code_not_unique: '<spring:message javaScriptEscape="true" code="js.errors.channel.code.notunique"/>',
        max_length_exceeded:'<spring:message javaScriptEscape="true" code="js.errors.channel.length.upperLimit"/>',
        code_invalid:'<spring:message javaScriptEscape="true" code="js.errors.channel.catalogcode.invalid"/>',
        noUtilityRateAvailable: '<spring:message javaScriptEscape="true" code="message.no.utility.rates"/>',
        yes:"<spring:message javaScriptEscape="true" code='label.yes'/>",
        no:"<spring:message javaScriptEscape="true" code='label.no'/>"
        };
</script>


<input type="hidden" id="currentServiceInstanceUUID" value="<c:out value="${serviceInstanceUUID}"/>"/>
<input type="hidden" id="tenantParam" name="tenantParam" value="<c:out value="${tenant.param}"/>"/>
<input type="hidden" id="service_account_config_properties_list" value="<c:out value="${service_account_config_properties_list}"/>"/>
<spring:message code="enable.service.dialog.configure.account.title" var="step2Active"/>
<spring:message code="label.service.users.enable" var="step3Active"/>
<spring:message code="label.enable.service.review" var="step4Active"/>
<!-- 5 step wizard -->
<c:if test="${not empty service_account_config_properties && enableServiceForAllUsers eq true}">
  <c:set value="5" var="totalSteps"/>
  <c:set var ="step1Next" value="stepOfAccountConfig"/>
  <c:set var ="step2Next" value="stepOfEnableServiceUser"/>
  <c:set var ="step3Previous" value="stepOfAccountConfig"/>
  <c:set var ="step4Previous" value="stepOfEnableServiceUser"/>
  <c:set var ="stepcenterbarclass" value="extendedfivestepswizard"/>
</c:if>
<!-- 4 step wizard -->
<c:if test="${not empty service_account_config_properties && enableServiceForAllUsers eq false}">
  <c:set value="4" var="totalSteps"/>
  <c:set var ="step1Next" value="stepOfAccountConfig"/>
  <c:set var ="step2Next" value="stepOfReviewAndConfirm"/>
  <c:set var ="step3Previous" value="stepOfTnc"/>
  <c:set var ="step4Previous" value="stepOfAccountConfig"/>
  <c:set var ="stepcenterbarclass" value="extendedfourstepswizard"/>
</c:if>
<!-- 4 step wizard -->
<c:if test="${ empty service_account_config_properties && enableServiceForAllUsers eq true}">
  <c:set value="4" var="totalSteps"/>
  <c:set var ="step1Next" value="stepOfEnableServiceUser"/>
  <c:set var ="step2Next" value="stepOfEnableServiceUser"/>
  <c:set var ="step3Previous" value="stepOfTnc"/>
  <c:set var ="step4Previous" value="stepOfEnableServiceUser"/>
  <c:set var ="stepcenterbarclass" value="extendedfourstepswizard"/>
</c:if>
<!-- 3 step wizard -->
<c:if test="${ empty service_account_config_properties && enableServiceForAllUsers eq false}">
  <c:set value="3" var="totalSteps"/>
  <c:set var ="step1Next" value="stepOfReviewAndConfirm"/>
  <c:set var ="step2Next" value="stepOfReviewAndConfirm"/>
  <c:set var ="step3Previous" value="stepOfTnc"/>
  <c:set var ="step4Previous" value="stepOfTnc"/>
  <c:set var ="stepcenterbarclass" value="extendedthreestepswizard"/>
</c:if>

<div class="dialog_formcontent wizard">
   <form id="serviceEnableForm">
  <div class="widget_wizardcontainer sixstepswizard">
    <input type="hidden" value="${uuid}" name="serviceParam" id="serviceParam">

    <!--step 1 starts here-->
    <div style="" class="j_cloudservicepopup" id="stepOfTnc">
      <input type="hidden" value='<c:out value="${step1Next}"/>' name="nextstep" id="nextstep">
      <input type="hidden" value="" name="prevstep" id="prevstep">
      <div class="widgetwizard_stepsbox">
        <div class="widgetwizard_steps_contentcontainer sixstepswizard">
          <div class="widgetwizard_stepscenterbar sixstepswizard">
            <ul>
              <c:forEach var="step" items="${steps}" varStatus="num">
                  <c:choose>
                    <c:when test="${num.index eq 0}">
                     <li class="widgetwizard_stepscenterbar <c:out value="${stepcenterbarclass}"/> first"><span class="steps active"><span class="stepsnumbers active"><c:out value="${num.index+1}"></c:out></span></span><span class="stepstitle active"><spring:message code="${step}"  /></span></li>
                    </c:when>
                    <c:when test="${num.index eq totalSteps-1}">
                      <li class="widgetwizard_stepscenterbar <c:out value="${stepcenterbarclass}"/> last"><span class="steps last"><span class="stepsnumbers last"><c:out value="${num.index+1}"/></span></span><span class="stepstitle last"><spring:message code="${step}"/></span></li>
                    </c:when>
                    <c:otherwise>
                      <li class="widgetwizard_stepscenterbar <c:out value="${stepcenterbarclass}"/>"><span class="steps"><span class="stepsnumbers"><c:out value="${num.index+1}"/></span></span><span class="stepstitle"><spring:message code="${step}"/></span></li>
                    </c:otherwise>
                  </c:choose>
              </c:forEach>     
            </ul>
          </div>
        </div>
      </div>
      <div class="widgetwizard_contentarea sixstepswizard" style="overflow-y:hidden;height:380px;">
        <div class="widgetwizard_boxes fullheight sixstepswizard"  style="overflow-y:hidden;height:220px;">
         <div class="widgetwizard_titleboxes" style="width:800px;">
         	<span class="heading"><b><spring:message code="enable.service.dialog.rates.and.conditions.heading" /></b></span>
            <span><spring:message code="enable.service.dialog.rates.and.conditions.description" /></span>
         </div>
          <div class="widgetwizard_tncbox js_extra_usage_div" style="height:150px;margin-top:5px;width:800px;">
            <div class="alert" id="utility_rates_message_box" style="display:none;margin:5px;"></div>
            <div id="utilityrate_table_bundle_details" class="full_width_box utility_table" style="color:#111111;margin:5px;width:99%;">
              <div id="table_load_spinning_wheel" style="height:155px;" >
                <div class="widget_blackoverlay " style="height: 140px; position:relative;"></div>
                <div class="widget_loadingbox " style="top:90px;">
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
            </div>
          </div>
             
          
        </div>
        
        <div class="termsandconditions small">
        <table style="height:80px;margin-top:20px;border-color:#D6D6D6;">
            <thead>
                <tr>
                    <th>
                        <spring:message code="important.notice" />
                    </th>
                </tr>
            </thead>
            <tbody style="height:50px;">
                <tr>
                    <td>
                        <p><spring:message code="resources.agree.tnc.message.new"/></p>
                    </td>
                </tr>
            </tbody>
        </table>
        <div class="agree">
          <input type="checkbox" class="checkbox" id="tncAccept">
          <span><spring:message code="label.moreUserInfo.agree" htmlEscape="false"/></span>  
        </div>
        <div class="main_addnew_formbox_errormsg" id="tncAcceptError"  style="width:800px;margin:0px;"></div> 
      </div>
      </div>
      
      <div class="widgetwizard_nextprevpanel sixstepswizard" id="buttons">
        <input class="widgetwizard_nextprevpanel nextbutton" type="button" data-primary onclick="goToNextStepForTenant(this)" value="<spring:message code="label.next.step"/>" name="<spring:message code="label.next.step"/>" id="add_service_instance_next">
        <a href="javascript:void(0);" class="cancel close_enable_service_user_wizard" ><spring:message code="label.cancel" /></a>
      </div>
    </div>
    <!--step 1 ends here-->

    <!--step 2 starts here-->
<c:if test="${not empty service_account_config_properties}">
    <div style="display: none;" class="j_cloudservicepopup" id="stepOfAccountConfig">
      <input type="hidden" value='<c:out value="${step2Next}"/>' name="nextstep" id="nextstep">
      <input type="hidden" value="stepOfTnc" name="prevstep" id="prevstep">
      <div class="widgetwizard_stepsbox">
        <div class="widgetwizard_steps_contentcontainer sixstepswizard">
          <div class="widgetwizard_stepscenterbar sixstepswizard">
            <ul>
            <c:set var="stepPassed" value="${true}" />
              <c:forEach var="step" items="${steps}" varStatus="num">
                <c:if test="${step2Active eq step}"> 
                 <c:set var="stepPassed" value="${false}" />
                </c:if>
                  <c:choose>
                    <c:when test="${num.index eq 0}">
                     <li class="widgetwizard_stepscenterbar <c:out value="${stepcenterbarclass}"/> first"><span class="steps completedsteps"><span class="stepsnumbers"><c:out value="${num.index+1}"></c:out></span></span><span class="stepstitle"><spring:message code="${step}"  /></span></li>
                    </c:when>
                    <c:when test="${stepPassed==true}">
                     <li class="widgetwizard_stepscenterbar <c:out value="${stepcenterbarclass}"/>"><span class="steps completedsteps"><span class="stepsnumbers"><c:out value="${num.index+1}"></c:out></span></span><span class="stepstitle"><spring:message code="${step}"  /></span></li>
                    </c:when>
                    <c:when test="${num.index eq totalSteps-1}">
                      <li class="widgetwizard_stepscenterbar <c:out value="${stepcenterbarclass}"/> last"><span class="steps last"><span class="stepsnumbers last"><c:out value="${num.index+1}"/></span></span><span class="stepstitle last"><spring:message code="${step}"/></span></li>
                    </c:when>
                    <c:otherwise>
                      <li class="widgetwizard_stepscenterbar <c:out value="${stepcenterbarclass}"/>"><span class="steps <c:if test="${step2Active eq step}"> <c:out value="active"/></c:if>"><span class="stepsnumbers <c:if test="${step2Active eq step}"> <c:out value="active"/></c:if>"><c:out value="${num.index+1}"/></span></span><span class="stepstitle"><spring:message code="${step}"/></span></li>
                    </c:otherwise>
                  </c:choose>
              </c:forEach>            
             </ul>
          </div>
        </div>
      </div>
      
      <div class="widgetwizard_contentarea sixstepswizard" style="height:380px;">
            <div class="widgetwizard_boxes fullheight sixstepswizard" style="height:360px;">
            <div class="widgetwizard_titleboxes" style="width:800px;">
              <h2><spring:message code="label.service.account.configuration"/></h2>
              <span><spring:message code="service.instance.account.configuration.message"/></span>
            </div>
           <c:if test="${ latestTenantHandle ne null  and latestTenantHandle.state == 'ERROR'}">
            <div class="widgetwizard_detailsbox sixstepswizard" style="margin:0px 0px 0px 15px;width:767px;margin-left:15px;">
                <a class="js_account_registration_error" style="cursor:pointer; padding-top: 8px; color: red;" data-toggle="popover" data-trigger="hover" data-placement="right"  data-content="<spring:message javaScriptEscape="true" text="${latestTenantHandle.data}"/>"><spring:message code="service.connectorlist.retry.error.details"/></a>
             </div>
             
           </c:if>
            <c:choose>
            <c:when test="${service_account_config_properties != null && empty accountConfigEditor}">
            
              <div class="widgetwizard_detailsbox sixstepswizard">
              <tiles:insertDefinition name="service.account.config" />
              
            </div>
            </c:when>
            <c:otherwise>
              <tiles:insertDefinition name="${accountConfigEditor}" />
            </c:otherwise>
            </c:choose>
            </div>
      </div>
      <div class="widgetwizard_nextprevpanel sixstepswizard" id="buttons">
        <input class="widgetwizard_nextprevpanel prevbutton" type="button" onclick="goToPreviousStepForTenant(this)" value="<spring:message code="label.previous.step"/>" name="<spring:message code="label.previous.step"/>" >
        <input class="widgetwizard_nextprevpanel nextbutton" type="button" data-primary onclick="goToNextStepForTenant(this)" value="<spring:message code="label.next.step"/>" name="<spring:message code="label.next.step"/>" >
        <a href="javascript:void(0);" class="cancel close_enable_service_user_wizard" ><spring:message code="label.cancel" /></a>
      </div>
    </div>
</c:if>
    
    <!--step 2 ends here-->

    <!--step 3 starts here-->
<c:if test="${enableServiceForAllUsers eq true}">
    <div style="display: none;" class="j_cloudservicepopup" id="stepOfEnableServiceUser">
 
      <input type="hidden" value='<c:out value="${step3Previous}"/>' name="prevstep" id="prevstep">
      <input type="hidden" value="stepOfReviewAndConfirm" name="nextstep" id="nextstep">
      <div class="widgetwizard_stepsbox">
        <div class="widgetwizard_steps_contentcontainer sixstepswizard">
          <div class="widgetwizard_stepscenterbar sixstepswizard">
            <ul>
             <c:set var="stepPassed" value="${true}" />
             <c:forEach var="step" items="${steps}" varStatus="num">
             <c:if test="${step3Active eq step}"> 
                <c:set var="stepPassed" value="${false}" />
              </c:if>
                  <c:choose>
                    <c:when test="${num.index eq 0}">
                     <li class="widgetwizard_stepscenterbar <c:out value="${stepcenterbarclass}"/> first"><span class="steps completedsteps"><span class="stepsnumbers"><c:out value="${num.index+1}"></c:out></span></span><span class="stepstitle"><spring:message code="${step}"  /></span></li>
                    </c:when>
                    <c:when test="${stepPassed==true}">
                     <li class="widgetwizard_stepscenterbar <c:out value="${stepcenterbarclass}"/>"><span class="steps completedsteps"><span class="stepsnumbers"><c:out value="${num.index+1}"></c:out></span></span><span class="stepstitle"><spring:message code="${step}"  /></span></li>
                    </c:when>
                    <c:when test="${num.index eq totalSteps-1}">
                      <li class="widgetwizard_stepscenterbar <c:out value="${stepcenterbarclass}"/> last"><span class="steps last"><span class="stepsnumbers last"><c:out value="${num.index+1}"/></span></span><span class="stepstitle last"><spring:message code="${step}"/></span></li>
                    </c:when>
                    <c:otherwise>
                      <li class="widgetwizard_stepscenterbar <c:out value="${stepcenterbarclass}"/>"><span class="steps <c:if test="${step3Active eq step}"> <c:out value="active"/></c:if>"><span class="stepsnumbers <c:if test="${step3Active eq step}"> <c:out value="active"/></c:if>"><c:out value="${num.index+1}"/></span></span><span class="stepstitle"><spring:message code="${step}"/></span></li>
                    </c:otherwise>
                  </c:choose>
              </c:forEach>   
            </ul>
          </div>
        </div>
      </div>
      <div class="widgetwizard_contentarea sixstepswizard" style="height:380px;">
           <div class="widgetwizard_boxes fullheight sixstepswizard" style="height:360px;">
             <div class="widgetwizard_titleboxes" style="width:800px;">
              <h2><spring:message code="label.service.users.enable" htmlEscape="false"></spring:message></h2>
              <span><spring:message code="ui.service.enable.all.user.desc"/></span>
             <div>
               <ul>
                <li style="padding:0;margin:20px 0 0 3px;width:300px;">
                  <span style="line-height:20px;"><input type="radio" style="width:20px;margin-top:0px;" name="enableAllUsers" checked="checked" value="false"/><spring:message  code="label.no"/></span>
                  <span style="line-height:20px;"><input type="radio" style="width:20px;margin-top:0px;" name="enableAllUsers"  value ="true" /><spring:message  code="label.yes"/></span>
                </li>
              </ul>
            </div>
            </div>
          </div>
          
      </div>  
      <div class="widgetwizard_nextprevpanel sixstepswizard" id="buttons">
        <input class="widgetwizard_nextprevpanel prevbutton" type="button" onclick="goToPreviousStepForTenant(this)" value="<spring:message code="label.previous.step"/>" name="<spring:message code="label.previous.step"/>" >
        <input class="widgetwizard_nextprevpanel nextbutton" type="button" data-primary onclick="goToNextStepForTenant(this)" value="<spring:message code="label.next.step"/>" name="<spring:message code="label.next.step"/>" >
        <a href="javascript:void(0);" class="cancel close_enable_service_user_wizard" ><spring:message code="label.cancel" /></a>
      </div>
    </div>
</c:if>
    <!--step 3 ends here-->

    <!--step 4 starts here-->
    <div style="display: none;" class="j_cloudservicepopup" id="stepOfReviewAndConfirm">
      <input type="hidden" value='<c:out value="${step4Previous}"/>' name="prevstep" id="prevstep">
      <input type="hidden" value="stepOfSubmitAndFinish" name="nextstep" id="nextstep">
      <div class="widgetwizard_stepsbox">
        <div class="widgetwizard_steps_contentcontainer sixstepswizard">
          <div class="widgetwizard_stepscenterbar sixstepswizard">
            <ul>
             <c:set var="stepPassed" value="${true}" />
              <c:forEach var="step" items="${steps}" varStatus="num">
                <c:if test="${step4Active eq step}"> 
                  <c:set var="stepPassed" value="${false}" />
                </c:if>
                  <c:choose>
                   <c:when test="${num.index eq 0 }">
                     <li class="widgetwizard_stepscenterbar <c:out value="${stepcenterbarclass}"/> first"><span class="steps completedsteps"><span class="stepsnumbers"><c:out value="${num.index+1}"></c:out></span></span><span class="stepstitle"><spring:message code="${step}"  /></span></li>
                    </c:when>
                    <c:when test="${stepPassed==true}">
                     <li class="widgetwizard_stepscenterbar <c:out value="${stepcenterbarclass}"/>"><span class="steps completedsteps"><span class="stepsnumbers"><c:out value="${num.index+1}"></c:out></span></span><span class="stepstitle"><spring:message code="${step}"  /></span></li>
                    </c:when>
                    <c:when test="${num.index eq totalSteps-1}">
                      <li class="widgetwizard_stepscenterbar <c:out value="${stepcenterbarclass}"/> last"><span class="steps last"><span class="stepsnumbers last"><c:out value="${num.index+1}"/></span></span><span class="stepstitle last"><spring:message code="${step}"/></span></li>
                    </c:when>
                    <c:otherwise>
                      <li class="widgetwizard_stepscenterbar <c:out value="${stepcenterbarclass}"/>"><span class="steps <c:if test="${step4Active eq step}"> <c:out value="active"/></c:if>"><span class="stepsnumbers <c:if test="${step4Active eq step}"> <c:out value="active"/></c:if>"><c:out value="${num.index+1}"/></span></span><span class="stepstitle"><spring:message code="${step}"/></span></li>
                    </c:otherwise>
                  </c:choose>
              </c:forEach>
             </ul>
          </div>
        </div>
      </div>
      <div class="widgetwizard_contentarea sixstepswizard" style="height:380px;">
          <div class="widgetwizard_boxes fullheight sixstepswizard" style="height:360px;">
          <div class="widgetwizard_titleboxes" style="width:800px;">
            <h2><spring:message code="label.enable.service.review" htmlEscape="false"></spring:message></h2>
            <span><spring:message code="ui.service.enable.review.confirm.title.desc"/></span>
          </div>
          <div class="widgetwizard_detailsbox sixstepswizard" style="width:auto;">
            <div  id="serviceEnableError" style="display:block;display:none;  margin:0px 0px 0px 15px;">
              
              <div class="alert alert-error" id="validationError"></div>
            </div>
            <div class="widgetwizard_reviewbox sixstepswizard">
              <ul>
                <li id="tncReviewBox">
                <span class="label" style="width:750px;"><spring:message code="label.tnc.review" /></span>
                </li>
                <c:if test="${not empty service_account_config_properties }">
                  <li id="confirmAccountConfigurationDetails">
                    <span class="label"><spring:message code="label.service.account.configuration" />:</span>
                    <span class="description " ></span>
                    <span class="edit" style="margin-right:60px"><a class="confirm_edit_link" id="backToaccountConfigurationDetails" href="javascript:void(0);"><spring:message code="label.edit"/></a></span>
                    <ul>
                    <c:forEach var="service_config_property" items="${service_account_config_properties}" varStatus="status">
                    <c:if test="${service_config_property.name ne 'EDITOR'}">
                        <li class="subselection" >
                        <span class="label sublabel" style="width:220px;"><spring:message  code="${service.serviceName}.${service_config_property.name}.name"/></span>
                        <span class="description subdescription ellipsis" id="${service_config_property.name}" ></span>
                      </li>
                      </c:if>
                    </c:forEach>
                    </ul>  
                  </li>
                  </c:if>
                  <c:if test="${enableServiceForAllUsers eq true}">
                  <li id="enableServiceForAllUsers">
                    <span class="label"><spring:message code="label.service.users.enable" />:</span>
                    <span class="description " ></span>
                    <span class="edit" style="margin-right:60px"><a class="confirm_edit_link" id="backToenableServiceForAllUsersDetails" href="javascript:void(0);"><spring:message code="label.edit"/></a></span>
                    <ul>
                      <li class="subselection" >
                        <span class="label sublabel" style="width:220px;"><spring:message  code="label.service.users.enable"/></span>
                        <span class="description subdescription ellipsis" id="enableServiceForAllUsersDesc" ></span>
                      </li>
                    </ul>
                  </li>
                </c:if>
               </ul>
            </div> 
      </div>
      </div>
      </div>
              <div id="spinning_wheel" style="display:none" >
                <div class="widget_blackoverlay " style="height: 345px; margin: 113px 0 0 30px;width: 836px;"></div>
                <div class="widget_loadingbox " style="top:235px;">
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
      <div class="widgetwizard_nextprevpanel sixstepswizard" id="buttons">
        <input class="widgetwizard_nextprevpanel prevbutton" type="button" id="prevButtonEnableServiceStep" onclick="goToPreviousStepForTenant(this)" value="<spring:message code="label.previous.step"/>" name="<spring:message code="label.previous.step"/>" >
        <input class="widgetwizard_nextprevpanel nextbutton" type="button" id="enableServiceButton" uuid="${service.uuid}" action="save"  data-primary onclick="goToNextStepForTenant(this)" value="<spring:message code="label.enable"/>" name="<spring:message code="label.enable"/>" >
        <a href="javascript:void(0);" class="cancel close_enable_service_user_wizard" ><spring:message code="label.cancel" /></a>
      </div>
    </div>
    <!--step 4 ends here-->

    <!--step 5 starts here-->
    <div style="display: none;" class="j_cloudservicepopup" id="stepOfSubmitAndFinish">
      <input type="hidden" value="" name="nextstep" id="nextstep">
      <input type="hidden" value="stepOfReviewAndConfirm" name="prevstep" id="prevstep">
      <div class="widgetwizard_stepsbox">
        <div class="widgetwizard_steps_contentcontainer sixstepswizard">
          <div class="widgetwizard_stepscenterbar sixstepswizard">
            <ul>
            <c:set var="stepPassed" value="${true}" />
              <c:forEach var="step" items="${steps}" varStatus="num">
               <c:if test="${num.index eq totalSteps-1}"> 
                <c:set var="stepPassed" value="${false}" />
              </c:if>
                  <c:choose>
                    <c:when test="${num.index eq 0}">
                     <li class="widgetwizard_stepscenterbar <c:out value="${stepcenterbarclass}"/> first"><span class="steps completedsteps"><span class="stepsnumbers"><c:out value="${num.index+1}"></c:out></span></span><span class="stepstitle"><spring:message code="${step}"  /></span></li>
                    </c:when>
                    <c:when test="${stepPassed==true}">
                     <li class="widgetwizard_stepscenterbar <c:out value="${stepcenterbarclass}"/>"><span class="steps completedsteps"><span class="stepsnumbers"><c:out value="${num.index+1}"></c:out></span></span><span class="stepstitle"><spring:message code="${step}"  /></span></li>
                    </c:when>
                    <c:when test="${num.index eq totalSteps-1}">
                      <li class="widgetwizard_stepscenterbar <c:out value="${stepcenterbarclass}"/> last"><span class="steps last active "><span class="stepsnumbers last"><c:out value="${num.index+1}"/></span></span><span class="stepstitle last active"><spring:message code="${step}"/></span></li>
                    </c:when>
                    <c:otherwise>
                      <li class="widgetwizard_stepscenterbar <c:out value="${stepcenterbarclass}"/>"><span class="steps"><span class="stepsnumbers"><c:out value="${num.index+1}"/></span></span><span class="stepstitle"><spring:message code="${step}"/></span></li>
                    </c:otherwise>
                  </c:choose>
              </c:forEach>               
            </ul>
          </div>
        </div>
      </div>
      <div class="widgetwizard_contentarea sixstepswizard" style="height:380px;">
        <div class="widgetwizard_boxes fullheight sixstepswizard" style="height:360px;">
              <div class="widgetwizard_successbox">
                <div class="widget_resulticon success"></div>
                <p id="successmessage" style="text-align:left;width:600px;margin-left:-100px;"><spring:message code="ui.message.enable.tenant.service.success"/></p>
                <ul style="float:left;margin-top:10px;margin-left: -100px; width: 100%;" class="list">
                  <li><spring:message code="ui.message.enable.tenant.service.success.action1"/></li>
                  <li><spring:message code="ui.message.enable.tenant.service.success.action2"/></li>
                  <li><spring:message code="ui.message.enable.tenant.service.success.action3"/></li>
                  <li><spring:message code="ui.message.enable.tenant.service.success.action4"/></li>
                </ul>
              </div>
          </div>
        </div>
      <div id="buttons" class="widgetwizard_nextprevpanel sixstepswizard">
        <input class="widgetwizard_nextprevpanel submitbutton" type="button" data-primary onclick="javascript:closeEnableServiceDialog();" value="<spring:message code="label.close"/>" name="Close" >
      </div>
    </div>
    <!--step 5 ends here-->
  </div>

  <div class="ui-resizable-handle ui-resizable-n" unselectable="on" style="-moz-user-select: none;"></div>
  <div class="ui-resizable-handle ui-resizable-e" unselectable="on" style="-moz-user-select: none;"></div>
  <div class="ui-resizable-handle ui-resizable-s" unselectable="on" style="-moz-user-select: none;"></div>
  <div class="ui-resizable-handle ui-resizable-w" unselectable="on" style="-moz-user-select: none;"></div>
  <div class="ui-resizable-handle ui-resizable-se ui-icon ui-icon-gripsmall-diagonal-se ui-icon-grip-diagonal-se"
    style="z-index: 1001; -moz-user-select: none;" unselectable="on"></div>
  <div class="ui-resizable-handle ui-resizable-sw" style="z-index: 1002; -moz-user-select: none;" unselectable="on"></div>
  <div class="ui-resizable-handle ui-resizable-ne" style="z-index: 1003; -moz-user-select: none;" unselectable="on"></div>
  <div class="ui-resizable-handle ui-resizable-nw" style="z-index: 1004; -moz-user-select: none;" unselectable="on"></div>
  </form>
</div>

<div id="tncDialog" title='<spring:message code="js.errors.register.tncDialog.title"/>' style="display:none;padding:10px;height:200px;">
    <div style="background-color:#ffffff;min-height:200px;padding:5px;">
      <c:out value="${tnc}" escapeXml="false" />
    </div>
</div>
<script type="text/javascript">

  $(function() {
    $(".js_account_registration_error").popover();
  });
</script>
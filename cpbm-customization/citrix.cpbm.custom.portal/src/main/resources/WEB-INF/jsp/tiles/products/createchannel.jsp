<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<link rel="stylesheet" href="<%=request.getContextPath() %>/select2/select2.css" type="text/css" />
<script type="text/javascript" src="<%=request.getContextPath() %>/select2/select2.min.js"></script>

<jsp:include page="js_messages.jsp"></jsp:include>

<script type="text/javascript">
  $(".widget_checkbox").off('click');
  $(".widget_checkbox").on('click', function() {
    if($(this).find("span").attr("class") == "unchecked") {
         $(this).find("span").removeClass('unchecked').addClass('checked').css("margin-left", "0px");
         $(this).find(".js_currency_input").val($(this).attr('currCode'));
       } else {
         operation = "remove";
         $(this).find("span").removeClass('checked').addClass('unchecked');
         $(this).find(".js_currency_input").val("");
       }
  });
  $("#whitelistcountries").select2();
  $("#blacklistcountries").select2();
  var billingGroup_list = [];
  <c:forEach items="${billingGroups}" var="billingGroup">
   <c:if test="${billingGroup.name != 'Uncategorized'}" >
    billingGroup_list.push('${billingGroup.name}')
    </c:if>
  </c:forEach>
  
  $("#billingGroup").select2({tags:billingGroup_list,
      maximumSelectionSize: 1});
    
</script>

<div class="dialog_formcontent wizard">
<input type="hidden" id="publicHost" value="${publicHost}" >
<input type="hidden" id="publicProtocol" value="${publicProtocol}" >
<input type="hidden" id="publicPort" value="${publicPort}" >
  <form:form commandName="channelForm" id="channelForm" cssClass="ajaxform">
  <div class="widget_wizardcontainer sixstepswizard">

<!--  Step 1 starts here  -->
    <div id="step1"  class="j_channelspopup">
      <input type="hidden" id="nextstep" name="nextstep" value="step2" >
      <input type="hidden" id="prevstep" name="prevstep" value="" >

       <div class="widgetwizard_stepsbox">
            <div class="widgetwizard_steps_contentcontainer sixstepswizard">
                <div class="widgetwizard_stepscenterbar sixstepswizard">
                    <ul>
                        <li class="widgetwizard_stepscenterbar fivestepswizard first"><span class="steps active"><span class="stepsnumbers active">1</span></span><span class="stepstitle active"><spring:message htmlEscape="false" code="ui.channel.create.step1.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps"><span class="stepsnumbers">2</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.channel.create.step2.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps"><span class="stepsnumbers">3</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.channel.create.step3.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps"><span class="stepsnumbers">4</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.channel.create.step4.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps"><span class="stepsnumbers">5</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.channel.create.step5.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar fivestepswizard last"><span class="steps last"><span class="stepsnumbers last">6</span></span><span class="stepstitle last"><spring:message htmlEscape="false" code="ui.channel.create.step6.title" /></span></li>
                   </ul>
                </div>
            </div>
        </div>

       <div class="widgetwizard_contentarea sixstepswizard">
         <div class="widgetwizard_boxes fullheight sixstepswizard js_scroll_container">
	          <div class="widgetwizard_titleboxes">
	                    <h2><spring:message code="ui.channel.create.step1.title"/></h2>
	                    <span><spring:message code="ui.channel.deteailsstep.desc"/></span>
	           </div>
	           <div class="widgetwizard_detailsbox sixstepswizard">
	             <ul>
	               <li>
				            <span class="label"><spring:message code="label.name"/></span>
				            <div class="mandatory_wrapper"> 
	 				      <spring:message code='channel.tooltip.name' htmlEscape="false" var="tooltip_message"/>
				              <form:input class="text js_input_help" tabindex="1" data-toggle="tooltip" data-placement="right" data-original-title='${tooltip_message}' path="channel.name" id="channelName" onchange="validate_channelname(event,this)"/>
				            </div>
				            <div class="main_addnew_formbox_errormsg_popup" id="name_errormsg"></div>
				        </li>
				        <li>
				            <span class="label"><spring:message code="label.channel.code"/></span>
				            <div class="mandatory_wrapper">
					      <spring:message code='channel.tooltip.code' htmlEscape="false" var="tooltip_message"/>
				              <form:input class="text js_input_help" id="channelCode" data-toggle="tooltip" data-placement="right" data-original-title='${tooltip_message}' path="channel.code" onchange="validate_channelcode(event,this)" tabindex="2"/>
				            </div>
				            <div class="main_addnew_formbox_errormsg_popup" id="code_errormsg"></div>
				        </li>
				 <li>
                    <span class="label"><spring:message code="label.channel.fqdn"/></span>
                    <div class="nonmandatory_wrapper">
                      <spring:message code='channel.tooltip.fqdn' htmlEscape="false" var="tooltip_message"/>
                      <form:input class="text js_input_help" data-toggle="tooltip" data-placement="right" data-original-title='${tooltip_message}' path="channel.fqdnPrefix" id="channelFQDNPrefix" onchange="validate_channel_fqdn_prefix(event,this)" tabindex="3" maxlength="35" style="width:100px"/>
                      <span class="label">.${publicHost}</span>
                    </div>
                    <div class="main_addnew_formbox_errormsg_popup" id="fqdn_prefix_errormsg"></div>
                </li>

                <li>
                   
                    <span class="label"><spring:message code="label.billing.group"/></span>
                    <div class="nonmandatory_wrapper">
                      <spring:message code='channel.tooltip.billing.group' htmlEscape="false" var="tooltip_message"/>
                      <div style="float:left;" class="js_input_help" data-toggle="tooltip" data-placement="right" data-original-title='${tooltip_message}'>
                      <form:input type="hidden" id="billingGroup"  tabindex="4"  path="channel.billingGroup.name" />
                    </div>
                    </div>
                     <div class="main_addnew_formbox_errormsg_popup" id="billinggroupcode_errormsg"></div>
                </li>

				<li>
                    <span class="label"><spring:message code="label.channel.description"/></span>
                    <div class="nonmandatory_wrapper">
                       <spring:message code='channel.tooltip.desc' htmlEscape="false" var="tooltip_message"/>
                       <form:textarea class="textarea js_input_help" data-toggle="tooltip" data-placement="right" data-original-title='${tooltip_message}' path="channel.description" id="channelDescription" onchange="validate_channeldesc(event,this)"
                          style="width: 300px; height: 50px; margin-left: 10px;" tabindex="5"></form:textarea>
                    </div>
                   <div class="main_addnew_formbox_errormsg_popup" id="description_errormsg"></div>
                </li>
	             </ul>
	           </div>
         </div>
       </div>

        <div id="buttons" class="widgetwizard_nextprevpanel sixstepswizard">
            <input class="widgetwizard_nextprevpanel nextbutton" type="button" onclick="addChannelNext(this)" value="<spring:message code="label.next.step"/>" name="<spring:message code="label.next.step"/>">
            <a href="javascript:void(0);" class="cancel close_channel_wizard" ><spring:message code="label.cancel" /></a>
        </div>

    </div>
<!--  Step 1 ends here  -->

<!--  Step 2 starts here  -->
    <div id="step2" class="j_channelspopup" style="display:none;">
      <input type="hidden" id="nextstep" name="nextstep" value="step3" >
      <input type="hidden"  id="prevstep"name="prevstep" value="step1" >

      <div class="widgetwizard_stepsbox">
         <div class="widgetwizard_steps_contentcontainer sixstepswizard">
             <div class="widgetwizard_stepscenterbar sixstepswizard">
                 <ul>
                     <li class="widgetwizard_stepscenterbar  first"><span class="steps completedsteps"><span class="stepsnumbers ">1</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.channel.create.step1.title" /></span></li>
                     <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps active"><span class="stepsnumbers active">2</span></span><span class="stepstitle active"><spring:message htmlEscape="false" code="ui.channel.create.step2.title" /></span></li>
                     <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps"><span class="stepsnumbers">3</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.channel.create.step3.title" /></span></li>
                     <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps"><span class="stepsnumbers">4</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.channel.create.step4.title" /></span></li>
                     <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps"><span class="stepsnumbers">5</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.channel.create.step5.title" /></span></li>
                     <li class="widgetwizard_stepscenterbar fivestepswizard last"><span class="steps last"><span class="stepsnumbers last">6</span></span><span class="stepstitle last"><spring:message htmlEscape="false" code="ui.channel.create.step6.title" /></span></li>
                 </ul>
             </div>
         </div>
       </div>

        <div class="widgetwizard_contentarea sixstepswizard">
          <div class="widgetwizard_boxes fullheight sixstepswizard js_scroll_container">
            <div class="widgetwizard_titleboxes">
                <h2><spring:message code="ui.channel.create.step2.title" htmlEscape="false"></spring:message></h2>
                <span><spring:message code="ui.channel.currencyselect.desc"/></span>
            </div>
            <div class="widgetwizard_reviewbox">
              <ul>
                <li>
                  <span class="label" style="margin-bottom: 6px;"><spring:message code="label.channel.currencies" /></span>

				            <div class="mandatory_wrapper">
				              <div id="currency_row_container">
									      <c:forEach var="currency" items="${currencies}" varStatus="status">
									        <c:choose>
									          <c:when test="${(status.index) % 2 == 0}">
									            <c:set var="rowClass" value="odd"/>
									          </c:when>
									          <c:otherwise>
									              <c:set var="rowClass" value="even"/>
									          </c:otherwise>
									        </c:choose>
									        <div class="<c:out value="widget_grid details ${rowClass}"/>">
									            <div class="widget_checkbox widget_checkbox_wide"
									                currCode="<c:out value="${currency.currencyCode}"/>"
									                currSign="<c:out value="${currency.sign}"/>"
									                currName="<spring:message javaScriptEscape="true" code="currency.longname.${currency.currencyCode}"/>">
									              <span class="unchecked"></span>
									               <input type="hidden" name="currencies[${status.index}]" class="js_currency_input"/>
									            </div>
									            <div class="widget_grid_description" style="margin:0;">
									              <span><strong><c:out value="${currency.sign}"/> - <spring:message code="currency.longname.${currency.currencyCode}"/></strong></span>
									            </div>
									            <div class="widget_flagbox">
									              <div class="widget_currencyflag">
									                  <img src="../../images/flags/<c:out value="${currency.currencyCode}"/>.gif" alt="" />
									              </div>
									            </div>
									        </div>
									        
									      </c:forEach>
									  </div>
	              </div>
		            <div class="main_addnew_formbox_errormsg_popup" id="currency_errormsg"></div>
	            </li>
	          </ul>
	         </div>
	       </div>
	     </div>

        <div id="buttons" class="widgetwizard_nextprevpanel sixstepswizard">
            <input class="widgetwizard_nextprevpanel prevbutton" type="button" onclick="addEditChannelPrevious(this)" value="<spring:message code="label.previous.step"/>" name="<spring:message code="label.previous.step"/>">
            <input class="widgetwizard_nextprevpanel nextbutton" type="button" onclick="addChannelNext(this)" value="<spring:message code="label.next.step"/>" name="<spring:message code="label.add"/>">
            <a href="javascript:void(0);" class="cancel close_channel_wizard" ><spring:message code="label.cancel" /></a>
        </div>
    </div>
<!--  Step 2 ends here  -->
<!--  Step 3 starts here  -->
  <div id="step3" class="j_channelspopup" style="display:none">
    <input type="hidden" id="nextstep" name="nextstep" value="step4" >
    <input type="hidden" id="prevstep" name="prevstep" value="step2" >
    <div class="widgetwizard_stepsbox">
         <div class="widgetwizard_steps_contentcontainer sixstepswizard">
             <div class="widgetwizard_stepscenterbar sixstepswizard">
              <ul>
                  <li class="widgetwizard_stepscenterbar  first"><span class="steps completedsteps"><span class="stepsnumbers ">1</span></span><span class="stepstitle "><spring:message htmlEscape="false" code="ui.channel.create.step1.title" /></span></li>
                  <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps completedsteps"><span class="stepsnumbers">2</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.channel.create.step2.title" /></span></li>
                  <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps active"><span class="stepsnumbers active">3</span></span><span class="stepstitle active"><spring:message htmlEscape="false" code="ui.channel.create.step3.title" /></span></li>
                  <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps"><span class="stepsnumbers">4</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.channel.create.step4.title" /></span></li>
                  <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps "><span class="stepsnumbers">5</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.channel.create.step5.title" /></span></li>
                  <li class="widgetwizard_stepscenterbar fivestepswizard last"><span class="steps last"><span class="stepsnumbers last">6</span></span><span class="stepstitle last"><spring:message htmlEscape="false" code="ui.channel.create.step6.title" /></span></li>
                </ul>
             </div>
         </div>
    </div>
    <div class="widgetwizard_contentarea sixstepswizard">
      <div class="widgetwizard_boxes fullheight sixstepswizard js_scroll_container">
          <div class="widgetwizard_titleboxes">
            <h2><spring:message code="ui.channel.create.step3.title" htmlEscape="false"></spring:message></h2>
            <span><spring:message code="ui.channel.defaultsettings.desc"/></span>
          </div>
            
          <div class="widgetwizard_detailsbox sixstepswizard wide threecolumns">
            <ul>
            <li class="header">
                    <span class="label">&nbsp;</span>
                    <span class="label"><spring:message code="label.default"/></span>
                    <span class="label"><spring:message code="label.channel.custom.url.new"/></span>
                </li>
              <li>
                <span class="label"><spring:message code="label.locale"/></span>
                
                <c:choose>
                  <c:when test="${not empty defaultLocaleValue}">
                    <span class="label">${defaultLocaleValue}</span>
                  </c:when>
                  <c:otherwise>
                    <span class="label none"><spring:message code="label.none"/></span>
                  </c:otherwise>
                </c:choose>
                
                <div class="nonmandatory_wrapper">
                    <spring:message code='channel.tooltip.locale' htmlEscape="false" var="tooltip_message"/>
                    <form:select cssClass="text js_input_help" data-global_value="${defaultLocaleValue}" tabindex="1" data-toggle="tooltip" data-placement="top" data-original-title='${tooltip_message}' path="channel.channelBrandingConfigurations.defaultLocale" id="channel_locale"
                      title="${i18nLanguageTooltip}">
                      <option value="" selected="selected"><spring:message code="label.choose"/></option>
                      <c:forEach items="${supportedLocaleList}" var="locale" varStatus="status">
                        <option value='<c:out value="${locale.key}" />'>
                          <c:out value="${locale.value}"></c:out>
                        </option>
                      </c:forEach>
                    </form:select>
                </div>
              </li>
              <li>
                <span class="label"><spring:message code="label.time.zone"/></span>
                
                <c:choose>
                  <c:when test="${not empty defaultTimeZone}">
                    <span class="label">${defaultTimeZone}</span>
                  </c:when>
                  <c:otherwise>
                    <span class="label none"><spring:message code="label.none"/></span>
                  </c:otherwise>
                </c:choose>
                
                <div class="nonmandatory_wrapper">
                  <spring:message code='channel.tooltip.timezone' htmlEscape="false" var="tooltip_message"/>
                  <form:select cssClass="text js_input_help" data-global_value="${defaultTimeZone}" tabindex="2" data-toggle="tooltip" data-placement="top" data-original-title='${tooltip_message}' path="channel.channelBrandingConfigurations.defaultTimeZone" id="channel_time_zone"
                      title="${i18nLanguageTooltip}">
                    <option value=""><spring:message code="label.myprofile.choose"/></option>
                    <c:forEach items="${supportedZoneList}" var="choice" varStatus="status">
                      <option value='<c:out value="${choice.value}" />' <c:if test="${choice.value == defaultTimeZone}">selected="selected"</c:if>>
                        <c:out value="${choice.key}"/>
                      </option> 
                    </c:forEach>
                  </form:select>
                </div>
              </li>
              <li>
                <span class="label"><spring:message code="label.channel.helpdesk.email"/></span>
                
                <c:choose>
                  <c:when test="${not empty help_desk_email}">
                    <span class="label">${help_desk_email}</span>
                  </c:when>
                  <c:otherwise>
                    <span class="label none"><spring:message code="label.none"/></span>
                  </c:otherwise>
                </c:choose>
                
                
                <div> 
                  <spring:message code='channel.tooltip.helpdesk_email' htmlEscape="false" var="tooltip_message"/>
                  <form:input class="text js_input_help" data-global_value="${help_desk_email}" tabindex="3" data-toggle="tooltip" data-placement="top" data-original-title='${tooltip_message}' path="channel.channelBrandingConfigurations.helpDeskEmail" id="help_desk_email"/>
                </div>
                <div class="main_addnew_formbox_errormsg_popup" id="help_desk_email_errormsg"></div>
              </li>
              <li>
                <span class="label"><spring:message code="label.channel.helpdesk.phone"/></span>
                
                  <c:choose>
                  <c:when test="${not empty help_desk_phone}">
                    <span class="label">${help_desk_phone}</span>
                  </c:when>
                  <c:otherwise>
                    <span class="label none"><spring:message code="label.none"/></span>
                  </c:otherwise>
                </c:choose>
                
                <div> 
                  <spring:message code='channel.tooltip.helpdesk_phone' htmlEscape="false" var="tooltip_message"/>
                  <form:input class="text js_input_help" data-global_value="${help_desk_phone}" tabindex="4" data-toggle="tooltip" data-placement="top" data-original-title='${tooltip_message}' path="channel.channelBrandingConfigurations.helpDeskPhone" id="help_desk_phone"/>
                </div>
                <div class="main_addnew_formbox_errormsg_popup" id="help_desk_phone_errormsg"></div>
              </li>
              <li>
                <span class="label"><spring:message code="label.channel.allow.signup"/></span>
                
                <c:choose>
                  <c:when test="${not empty signup_allowed}">
                    <c:choose>
                      <c:when test="${signup_allowed == 'true' }">
                        <span class="label"><spring:message code='label.yes' /></span>
                      </c:when>
                      <c:when test="${signup_allowed == 'false' }">
                        <span class="label"><spring:message code='label.no' /></span>
                      </c:when>
                    </c:choose>
                  </c:when>
                  <c:otherwise>
                    <span class="label none"><spring:message code="label.none"/></span>
                  </c:otherwise>
                </c:choose>
                
                <div> 
                  <spring:message code='channel.tooltip.allow.signup' htmlEscape="false" var="tooltip_message"/>
                  <span class="label" style="width:80px;">
                  <form:radiobutton  style="margin-top:0px;" class="js_input_help" tabindex="5" data-toggle="tooltip" data-placement="right" data-original-title='${tooltip_message}' path="channel.channelBrandingConfigurations.signupAllowed" data-global_value="${signup_allowed}" value="yes" />
                  <spring:message code='label.yes' />
                  </span>
                  <span class="label" style="width:80px;">
                  <form:radiobutton   tabindex="5" style="margin-top:0px;" class="js_input_help" data-toggle="tooltip" data-placement="right" data-original-title='${tooltip_message}' path="channel.channelBrandingConfigurations.signupAllowed" data-global_value="${signup_allowed}" value="no" />
                  <spring:message code='label.no' />
                  </span>
                </div>
                <div class="main_addnew_formbox_errormsg_popup" id="signup_allowed_errormsg"></div>
              </li>
                <li>
                <span class="label"><spring:message code="label.channel.whitelist.countries"/></span>
                
                <c:choose>
                  <c:when test="${not empty whitelistcountries}">
                    <span class="label" >${whitelistcountries}</span>
                  </c:when>
                  <c:otherwise>
                    <span class="label none"><spring:message code="label.none"/></span>
                  </c:otherwise>
                </c:choose>
                <spring:message code="message.whitelist.blacklist.countries.feature.condition" htmlEscape="false" var="tooltip_message"/>
                <div style="float:left;" class="js_input_help" data-toggle="tooltip" data-placement="top" data-original-title='${tooltip_message}'>
                   
                  <form:select tabindex="6" multiple="multiple"  data-global_value="${whitelistcountries}" path="channel.channelBrandingConfigurations.whitelistcountries" id="whitelistcountries">
                  <c:forEach items="${all_countries}" var="country">
                    <option value="${country.countryCode2}">${country.name}</option>
                  </c:forEach>
                  </form:select>
                  
                </div>
                </li>
              
              
                <li>
                <span class="label"><spring:message code="label.channel.blacklist.countries"/></span>
                
                <c:choose>
                  <c:when test="${not empty blacklistcountries}">
                    <span class="label">${blacklistcountries}</span>
                  </c:when>
                  <c:otherwise>
                    <span class="label none"><spring:message code="label.none"/></span>
                  </c:otherwise>
                </c:choose>
                <spring:message code="message.whitelist.blacklist.countries.feature.condition" htmlEscape="false" var="tooltip_message"/>
                <div style="float:left;" class="js_input_help" data-toggle="tooltip" data-placement="top" data-original-title='${tooltip_message}'> 
                  
                  <form:select tabindex="7" multiple="multiple" data-global_value="${blacklistcountries}" path="channel.channelBrandingConfigurations.blacklistcountries" id="blacklistcountries">
                  <c:forEach items="${all_countries}" var="country">
                    <option value="${country.countryCode2}">${country.name}</option>
                  </c:forEach>
                  </form:select>
                </div>
              </li>
                <li>
                <span class="label"><spring:message code="label.channel.whitelist.email.domains"/></span>
                
                <c:choose>
                  <c:when test="${not empty whitelistdomains}">
                    <span class="label">${whitelistdomains}</span>
                  </c:when>
                  <c:otherwise>
                    <span class="label none"><spring:message code="label.none"/></span>
                  </c:otherwise>
                </c:choose>
                
                <div> 
                <spring:message code="config.com.citrix.cpbm.accountManagement.onboarding.emailDomain.whitelist.description" htmlEscape="false" var="tooltip_message"/>
                  <form:input class="text js_input_help" tabindex="8" data-global_value="${whitelistdomains}" data-toggle="tooltip" data-placement="top" data-original-title='${tooltip_message}' path="channel.channelBrandingConfigurations.whitelistdomains" id="whitelistdomains" />
                </div>
                  <div class="main_addnew_formbox_errormsg_popup" id="whitelistdomains_errormsg"></div>
              </li>
                <li>
                <span class="label"><spring:message code="label.channel.blacklist.email.domains"/></span>
                
                <c:choose>
                  <c:when test="${not empty blacklistdomains}">
                    <span class="label">${blacklistdomains}</span>
                  </c:when>
                  <c:otherwise>
                  <span class="label none"><spring:message code="label.none"/></span>
                  </c:otherwise>
                </c:choose>
                
                <div> 
                <spring:message code="config.com.citrix.cpbm.accountManagement.onboarding.emailDomain.blacklist.description" htmlEscape="false" var="tooltip_message"/>
                  <form:input class="text js_input_help" data-global_value="${blacklistdomains}" tabindex="9" data-toggle="tooltip" data-placement="top" data-original-title='${tooltip_message}' path="channel.channelBrandingConfigurations.blacklistdomains" id="blacklistdomains" />
                </div>
                <div class="main_addnew_formbox_errormsg_popup" id="blacklistdomains_errormsg"></div>
              </li>
            </ul>
          </div>
      </div>
    </div>
    <div id="buttons" class="widgetwizard_nextprevpanel sixstepswizard">
      <input class="widgetwizard_nextprevpanel prevbutton" type="button" onclick="addEditChannelPrevious(this)" value="<spring:message code="label.previous.step"/>" name="<spring:message code="label.previous.step"/>">
      <input class="widgetwizard_nextprevpanel nextbutton" type="button" onclick="addChannelNext(this)" value="<spring:message code="label.next.step"/>" name="<spring:message code="label.next.step"/>">
      <a href="javascript:void(0);" class="cancel close_channel_wizard" ><spring:message code="label.cancel" /></a>
    </div>
  </div>
<!--  Step 3 ends here  -->

<!--  Step 4 starts here  -->
  <div id="step4" class="j_channelspopup" style="display:none">
    <input type="hidden" id="nextstep" name="nextstep" value="step5" >
    <input type="hidden" id="prevstep" name="prevstep" value="step3" >
    <div class="widgetwizard_stepsbox">
         <div class="widgetwizard_steps_contentcontainer sixstepswizard">
             <div class="widgetwizard_stepscenterbar sixstepswizard">
              <ul>
                  <li class="widgetwizard_stepscenterbar  first"><span class="steps completedsteps"><span class="stepsnumbers ">1</span></span><span class="stepstitle "><spring:message htmlEscape="false" code="ui.channel.create.step1.title" /></span></li>
                  <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps completedsteps"><span class="stepsnumbers">2</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.channel.create.step2.title" /></span></li>
                  <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps completedsteps"><span class="stepsnumbers">3</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.channel.create.step3.title" /></span></li>
                  <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps active"><span class="stepsnumbers active">4</span></span><span class="stepstitle active"><spring:message htmlEscape="false" code="ui.channel.create.step4.title" /></span></li>
                  <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps"><span class="stepsnumbers">5</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.channel.create.step5.title" /></span></li>
                  <li class="widgetwizard_stepscenterbar fivestepswizard last"><span class="steps last"><span class="stepsnumbers last">6</span></span><span class="stepstitle last"><spring:message htmlEscape="false" code="ui.channel.create.step6.title" /></span></li>
                </ul>
             </div>
         </div>
    </div>
    <div class="widgetwizard_contentarea sixstepswizard">
         <div class="widgetwizard_boxes fullheight sixstepswizard js_scroll_container">
             <div class="widgetwizard_titleboxes">
               <h2><spring:message code="ui.channel.create.step4.title" htmlEscape="false"></spring:message></h2>
               <span><spring:message code="ui.channel.defaulturls.desc"/></span>
             </div>
             <div class="widgetwizard_detailsbox sixstepswizard wide threecolumns">
               <ul>
               <li class="header">
                    <span class="label">&nbsp;</span>
                    <span class="label"><spring:message code="label.default"/></span>
                    <span class="label"><spring:message code="label.channel.custom.url.new"/></span>
                </li>
                <li>
                    <span class="label"><spring:message code="label.channel.url.blog"/></span>
                    
                    <c:choose>
		                  <c:when test="${(not empty marketing_blog_url) and (marketing_blog_url != '#') }">
		                    <span class="label">${marketing_blog_url}</span>
		                  </c:when>
		                  <c:otherwise>
		                  <span class="label none"><spring:message code="message.no.url.set"/></span>
		                  </c:otherwise>
		                </c:choose>
                    <div> 
                      <spring:message code='channel.tooltip.url_blog' htmlEscape="false" var="tooltip_message"/>
                      <form:input class="text js_input_help" data-global_value="${marketing_blog_url}" tabindex="1" data-toggle="tooltip" data-placement="top" data-original-title='${tooltip_message}' path="channel.channelBrandingConfigurations.blogUrl" id="marketing_blog_url" />
                    </div>
                    <div class="main_addnew_formbox_errormsg_popup" id="marketing_blog_url_errormsg"></div>
                </li>
                <li>
                    <span class="label"><spring:message code="label.channel.url.contact"/></span>
                    
                    <c:choose>
                      <c:when test="${(not empty marketing_contact_url) and (marketing_contact_url != '#')}">
                        <span class="label">${marketing_contact_url}</span>
                      </c:when>
                      <c:otherwise>
                      <span class="label none"><spring:message code="message.no.url.set"/></span>
                      </c:otherwise>
                    </c:choose>
                    <div> 
                      <spring:message code='channel.tooltip.url_contact' htmlEscape="false" var="tooltip_message"/>
                      <form:input class="text js_input_help" tabindex="2" data-global_value="${marketing_contact_url}" data-toggle="tooltip" data-placement="top" data-original-title='${tooltip_message}' path="channel.channelBrandingConfigurations.contactUrl" id="marketing_contact_url"/>
                    </div>
                    <div class="main_addnew_formbox_errormsg_popup" id="marketing_contact_url_errormsg"></div>
                </li>
                <li>
                    <span class="label"><spring:message code="label.channel.url.forum"/></span>
                    
                    
                    <c:choose>
                      <c:when test="${(not empty marketing_forum_url) and (marketing_forum_url != '#')}">
                        <span class="label">${marketing_forum_url}</span>
                      </c:when>
                      <c:otherwise>
                      <span class="label none"><spring:message code="message.no.url.set"/></span>
                      </c:otherwise>
                    </c:choose>
                    <div> 
                      <spring:message code='channel.tooltip.url_forum' htmlEscape="false" var="tooltip_message"/>
                      <form:input class="text js_input_help" tabindex="3" data-global_value="${marketing_forum_url}" data-toggle="tooltip" data-placement="top" data-original-title='${tooltip_message}' path="channel.channelBrandingConfigurations.forumUrl" id="marketing_forum_url" />
                    </div>
                    <div class="main_addnew_formbox_errormsg_popup" id="marketing_forum_url_errormsg"></div>
                </li>
                <li>
                    <span class="label"><spring:message code="label.channel.url.help"/></span>
                    
                    <c:choose>
                      <c:when test="${(not empty marketing_help_url) and (marketing_help_url !='#')}">
                        <span class="label">${marketing_help_url}</span>
                      </c:when>
                      <c:otherwise>
                      <span class="label none"><spring:message code="message.no.url.set"/></span>
                      </c:otherwise>
                    </c:choose>
                    <div> 
                      <spring:message code='channel.tooltip.url_help' htmlEscape="false" var="tooltip_message"/>
                      <form:input class="text js_input_help" tabindex="4" data-global_value="${marketing_help_url}" data-toggle="tooltip" data-placement="top" data-original-title='${tooltip_message}' path="channel.channelBrandingConfigurations.helpUrl" id="marketing_help_url" />
                    </div>
                    <div class="main_addnew_formbox_errormsg_popup" id="marketing_help_url_errormsg"></div>
                </li>
                <li>
                    <span class="label"><spring:message code="label.channel.url.privacy"/></span>
                    
                    <c:choose>
                      <c:when test="${(not empty marketing_privacy_url) and (marketing_privacy_url !='#')}">
                        <span class="label">${marketing_privacy_url}</span>
                      </c:when>
                      <c:otherwise>
                      <span class="label none"><spring:message code="message.no.url.set"/></span>
                      </c:otherwise>
                    </c:choose>
                    <div> 
                      <spring:message code='channel.tooltip.url_privacy' htmlEscape="false" var="tooltip_message"/>
                      <form:input class="text js_input_help" tabindex="7" data-global_value="${marketing_privacy_url}" data-toggle="tooltip" data-placement="top" data-original-title='${tooltip_message}' path="channel.channelBrandingConfigurations.privacyUrl" id="marketing_privacy_url" />
                    </div>
                    <div class="main_addnew_formbox_errormsg_popup" id="marketing_privacy_url_errormsg"></div>
                </li>
                <li>
                    <span class="label"><spring:message code="label.channel.url.support"/></span>
                    
                    <c:choose>
                      <c:when test="${(not empty marketing_support_url) and (marketing_support_url !='#')}">
                        <span class="label">${marketing_support_url}</span>
                      </c:when>
                      <c:otherwise>
                      <span class="label none"><spring:message code="message.no.url.set"/></span>
                      </c:otherwise>
                    </c:choose>
                    <div> 
                      <spring:message code='channel.tooltip.url_support' htmlEscape="false" var="tooltip_message"/>
                      <form:input class="text js_input_help" tabindex="8" data-global_value="${marketing_support_url}" data-toggle="tooltip" data-placement="top" data-original-title='${tooltip_message}' path="channel.channelBrandingConfigurations.supportUrl" id="marketing_support_url" />
                    </div>
                    <div class="main_addnew_formbox_errormsg_popup" id="marketing_support_url_errormsg"></div>
                </li>
                <li>
                    <span class="label"><spring:message code="label.channel.url.tou"/></span>
                    <c:choose>
                      <c:when test="${(not empty marketing_tou_url) and (marketing_tou_url != '#')}">
                        <span class="label">${marketing_tou_url}</span>
                      </c:when>
                      <c:otherwise>
                      <span class="label none"><spring:message code="message.no.url.set"/></span>
                      </c:otherwise>
                    </c:choose>
                    <div> 
                      <spring:message code='channel.tooltip.url_tou' htmlEscape="false" var="tooltip_message"/>
                      <form:input class="text js_input_help" tabindex="9" data-global_value="${marketing_tou_url}" data-toggle="tooltip" data-placement="top" data-original-title='${tooltip_message}' path="channel.channelBrandingConfigurations.touUrl" id="marketing_tou_url" />
                    </div>
                    <div class="main_addnew_formbox_errormsg_popup" id="marketing_tou_url_errormsg"></div>
                </li>
                
               </ul>
             </div>
         </div>
       </div>
    <div id="buttons" class="widgetwizard_nextprevpanel sixstepswizard">
      <input class="widgetwizard_nextprevpanel prevbutton" type="button" onclick="addEditChannelPrevious(this)" value="<spring:message code="label.previous.step"/>" name="<spring:message code="label.previous.step"/>">
      <input class="widgetwizard_nextprevpanel nextbutton" type="button" onclick="addChannelNext(this)" value="<spring:message code="label.next.step"/>" name="<spring:message code="label.next.step"/>">
      <a href="javascript:void(0);" class="cancel close_channel_wizard" ><spring:message code="label.cancel" /></a>
    </div>
  </div>
<!--  Step 4 ends here  -->

<!--  Step 5 starts here  -->
  <div id="step5" class="j_channelspopup" style="display:none">
    <input type="hidden" id="nextstep" name="nextstep" value="step6" >
    <input type="hidden" id="prevstep" name="prevstep" value="step4" >
     <div class="widgetwizard_stepsbox">
         <div class="widgetwizard_steps_contentcontainer sixstepswizard">
             <div class="widgetwizard_stepscenterbar sixstepswizard">
              <ul>
                  <li class="widgetwizard_stepscenterbar  first"><span class="steps completedsteps"><span class="stepsnumbers ">1</span></span><span class="stepstitle "><spring:message htmlEscape="false" code="ui.channel.create.step1.title" /></span></li>
                  <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps completedsteps"><span class="stepsnumbers">2</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.channel.create.step2.title" /></span></li>
                  <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps completedsteps"><span class="stepsnumbers">3</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.channel.create.step3.title" /></span></li>
                  <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps completedsteps"><span class="stepsnumbers">4</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.channel.create.step4.title" /></span></li>
                  <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps active"><span class="stepsnumbers active">5</span></span><span class="stepstitle active"><spring:message htmlEscape="false" code="ui.channel.create.step5.title" /></span></li>
                  <li class="widgetwizard_stepscenterbar fivestepswizard last"><span class="steps last"><span class="stepsnumbers last">6</span></span><span class="stepstitle last"><spring:message htmlEscape="false" code="ui.channel.create.step6.title" /></span></li>
                </ul>
             </div>
         </div>
     </div>
      <div class="widgetwizard_contentarea sixstepswizard">
            <div class="widgetwizard_boxes fullheight sixstepswizard js_scroll_container">
                <div class="widgetwizard_titleboxes">
                    <h2><spring:message code="ui.channel.review.confirm.title" htmlEscape="false"></spring:message></h2>
                    <span>
                      <c:if test="">
                        <spring:message code="ui.channel.review.confirm.title.desc"/>
                      </c:if>
                      <spring:message code="ui.channel.review.confirm.title.desc"/>
                    </span>
                </div>
                <div class="widgetwizard_reviewbox">
                  <ul>
                      <li style="padding:0;" id="confirmChannelDetails">
                         <span class="label"><spring:message code="ui.channel.create.step1.title"/>:</span>
                          <span class="edit" style="margin-right:60px">
                            <a class="confirm_edit_link" onclick="backToChannelDetails(this);" href="javascript:void(0);"><spring:message code="label.edit"/></a>
                          </span>
                          <ul>
                           <li class="subselection" >
                                 <span class="label sublabel"><spring:message code="label.name"/></span>
                                 <span class="description subdescription ellipsis" id="conf_name"></span>
                            </li>
                            <li class="subselection" >
                                 <span class="label sublabel"><spring:message code="label.channel.code"/></span>
                                 <span class="description subdescription ellipsis" id="conf_code"></span>
                            </li>
                            <li class="subselection" >
                                 <span class="label sublabel"><spring:message code="label.channel.fqdn"/></span>
                                 <span class="description subdescription ellipsis" id="conf_fqdn_prefix"></span>
                            </li>
                            <li class="subselection" >
                                 <span class="label sublabel"><spring:message code="label.billing.group"/></span>
                                 <span class="description subdescription ellipsis" id="conf_billinggroup"></span>
                            </li>
                            <li class="subselection" >
                                 <span class="label sublabel"><spring:message code="label.channel.description"/></span>
                                 <span class="description subdescription ellipsis" id="conf_channel_description"></span>
                            </li>
                          </ul>
                      </li>
                      <li id="confirmChannelCurrencies">
                         <span class="label"><spring:message code="ui.channel.supported.currencies"/>:</span>
                          <span class="edit" style="margin-right:60px">
                            <a class="confirm_edit_link" onclick="backTourrencySelection(this);" href="javascript:void(0);"><spring:message code="label.edit"/></a>
                          </span>
                          <ul>
                            <li class="subselection" >
                                 <span class="label sublabel"><spring:message code="label.channel.currencies"/></span>
                                 <span class="description subdescription" id="conf_currencies"></span>
                            </li>
                          </ul>
                      </li>
                      <li id="confirmChannelEditDefaults">
                        <span class="label"><spring:message code="label.channels.tab.defaults"/>:</span>
                        <span class="edit" style="margin-right:60px">
                          <a class="confirm_edit_link" onclick="backToDefaultSettingsSelection(this);" href="javascript:void(0);"><spring:message code="label.edit"/></a>
                        </span>
                        <ul>
                          <li class="subselection" >
                            <span class="label sublabel"><spring:message code="label.locale"/></span>
                            <span class="description subdescription" id="channel_default_locale"></span>
                          </li>
                          <li class="subselection" >
                            <span class="label sublabel"><spring:message code="label.time.zone"/></span>
                            <span class="description subdescription" id="channel_default_timezone"></span>
                          </li>
                          <li class="subselection" >
                            <span class="label sublabel"><spring:message code="label.channel.helpdesk.email"/></span>
                            <span class="description subdescription" id="channel_helpdesk_email"></span>
                          </li>
                          <li class="subselection" >
                            <span class="label sublabel"><spring:message code="label.channel.helpdesk.phone"/></span>
                            <span class="description subdescription" id="channel_helpdesk_phone"></span>
                          </li>
                          <li class="subselection" >
                            <span class="label sublabel"><spring:message code="label.channel.allow.signup"/></span>
                            <span class="description subdescription" id="channel_allow_signup"></span>
                          </li>
                          <li class="subselection" >
                            <span class="label sublabel"><spring:message code="label.channel.whitelist.countries"/></span>
                            <span class="description subdescription" id="channel_whitelist_countries"></span>
                          </li>
                          <li class="subselection" >
                            <span class="label sublabel"><spring:message code="label.channel.blacklist.countries"/></span>
                            <span class="description subdescription" id="channel_blacklist_countries"></span>
                          </li>
                          <li class="subselection" >
                            <span class="label sublabel"><spring:message code="label.channel.whitelist.email.domains"/></span>
                            <span class="description subdescription" id="channel_whitelist_email_domains"></span>
                          </li>
                          <li class="subselection" >
                            <span class="label sublabel"><spring:message code="label.channel.blacklist.email.domains"/></span>
                            <span class="description subdescription" id="channel_blacklist_email_domains"></span>
                          </li>
                        </ul>
                      </li>
                      <li id="confirmChannelUrls">
                         <span class="label"><spring:message code="label.channels.tab.urls"/>:</span>
                          <span class="edit" style="margin-right:60px">
                            <a class="confirm_edit_link" onclick="backToUrlSelection(this);" href="javascript:void(0);"><spring:message code="label.edit"/></a>
                          </span>
                          <ul>
                            <li class="subselection" >
                                 <span class="label sublabel"><spring:message code="label.channel.url.blog"/></span>
                                 <span class="description subdescription" id="channel_url_blog"></span>
                            </li>
                            <li class="subselection" >
                                 <span class="label sublabel"><spring:message code="label.channel.url.contact"/></span>
                                 <span class="description subdescription" id="channel_url_contact"></span>
                            </li>
                            <li class="subselection" >
                                 <span class="label sublabel"><spring:message code="label.channel.url.forum"/></span>
                                 <span class="description subdescription" id="channel_url_forum"></span>
                            </li>
                            <li class="subselection" >
                                 <span class="label sublabel"><spring:message code="label.channel.url.help"/></span>
                                 <span class="description subdescription" id="channel_url_help"></span>
                            </li>
                            <li class="subselection" >
                                 <span class="label sublabel"><spring:message code="label.channel.url.privacy"/></span>
                                 <span class="description subdescription" id="channel_url_privacy"></span>
                            </li>
                            <li class="subselection" >
                                 <span class="label sublabel"><spring:message code="label.channel.url.support"/></span>
                                 <span class="description subdescription" id="channel_url_support"></span>
                            </li>
                            <li class="subselection" >
                                 <span class="label sublabel"><spring:message code="label.channel.url.tou"/></span>
                                 <span class="description subdescription" id="channel_url_tou"></span>
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
            <input class="widgetwizard_nextprevpanel prevbutton" type="button" onclick="addEditChannelPrevious(this)" value="<spring:message code="label.previous.step"/>" name="<spring:message code="label.previous.step"/>">
            <input class="widgetwizard_nextprevpanel nextbutton" type="button" onclick="addChannelNext(this)" value="<spring:message code="label.add"/>" name="<spring:message code="label.add"/>">
            <a href="javascript:void(0);" class="cancel close_channel_wizard" ><spring:message code="label.cancel" /></a>
        </div>

    </div>
<!--  Step 5 starts here  -->

<!--  Step 6 starts here  -->
  <div id="step6" class="j_channelspopup" style="display:none;">
	  <input type="hidden" id="nextstep" name="nextstep" value="" >
	  <input type="hidden" id="prevstep" name="prevstep" value="step5" >

    <div class="widgetwizard_stepsbox">
       <div class="widgetwizard_steps_contentcontainer sixstepswizard">
           <div class="widgetwizard_stepscenterbar sixstepswizard">
               <ul>
                   <li class="widgetwizard_stepscenterbar  first"><span class="steps completedsteps"><span class="stepsnumbers ">1</span></span><span class="stepstitle "><spring:message htmlEscape="false" code="ui.channel.create.step1.title" /></span></li>
                  <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps completedsteps"><span class="stepsnumbers">2</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.channel.create.step2.title" /></span></li>
                  <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps completedsteps"><span class="stepsnumbers">3</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.channel.create.step3.title" /></span></li>
                  <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps completedsteps"><span class="stepsnumbers">4</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.channel.create.step4.title" /></span></li>
                  <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps completedsteps"><span class="stepsnumbers">5</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.channel.create.step5.title" /></span></li>
                  <li class="widgetwizard_stepscenterbar fivestepswizard last"><span class="steps last active"><span class="stepsnumbers last">6</span></span><span class="stepstitle last"><spring:message htmlEscape="false" code="ui.channel.create.step6.title" /></span></li>
               </ul>
           </div>
       </div>
    </div>

    <div class="widgetwizard_contentarea sixstepswizard">
      <div class="widgetwizard_boxes fullheight sixstepswizard js_scroll_container">
           <div class="widgetwizard_successbox">
               <div class="widgetwizard_successbox">
                    <div class="widget_resulticon success"></div>
                      <p id="successmessage"><spring:message htmlEscape="false" code="ui.channel.successfully.completed.text"/>&nbsp;</p>
                      <c:if test="${fn:length(channels) == 0}">
                        <p id="defaultchannelmessage" style="font-weight:bold"><spring:message htmlEscape="false" code="ui.first.channel.created.as.default.message"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</p>
                      </c:if>
                  </div>
              </div>
          </div>
      </div>

      <div id="buttons" class="widgetwizard_nextprevpanel sixstepswizard">
          <input class="widgetwizard_nextprevpanel submitbutton" type="button" onclick="addChannelNext(this)" value="<spring:message code="label.close"/>" name="Close">
      </div>

  </div>

<!--  Step 6 ends here  -->

  </div>
  </form:form>
</div>

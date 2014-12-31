<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/channels.js"></script>
<jsp:include page="/WEB-INF/jsp/tiles/shared/js_messages.jsp"></jsp:include>
<c:set var="req" value="${pageContext.request}" />
<c:set var="baseURL" value="${fn:replace(req.requestURL, fn:substring(req.requestURI, 1, fn:length(req.requestURI)), req.contextPath)}" />


<script type="text/javascript">
    var planned_word = '<spring:message javaScriptEscape="true" code="label.next.planned.version"/>';
    var invalid_date_msg = '<spring:message javaScriptEscape="true" code="ui.datepicker.date.value.invalid"/>';
    var date_before_or_today_msg = '<spring:message javaScriptEscape="true" code="ui.datepicker.date.value.before.or.today"/>';
    var dialogProceed = '<spring:message javaScriptEscape="true" code="label.proceed"/>';
    var notYetSet= '<spring:message javaScriptEscape="true" code="ui.label.plan.date.not.yet.set"/>';
    var channelServiceSettingDialogTitle= '<spring:message javaScriptEscape="true" code="ui.label.channel.service.settings.dialog.title"/>';
    
    <c:if test="${not empty previewModeSettings && previewModeSettings.isPreviewMode == true}">
      var previewModeChannelId="${previewModeSettings.previewChannelId}";
    </c:if>

   function popup_date_picker(event){
     var channelId = $("li[id^='channel'].selected.channels").attr('id').substr(7);
     initDialog("plan_date_div", 500);
     
     var $thisDialog = $("#plan_date_div");
     $thisDialog.data("height.dialog", 210);
     $thisDialog.bind('dialogclose', function(event) {
       $("#ui-datepicker-div").hide();
     });
     $.ajax( {
         type : "GET",
         url : "/portal/portal/channels/showdatepicker",
         data:{channelId: channelId},
         cache: false,
         async: false,
         dataType : "html",
         success : function(html) {
             $thisDialog.html(html);
             $thisDialog.dialog('option', 'buttons', {
                   "Proceed": function () {
                        // Start the backend call
                        var prevValue = $thisDialog.find("#planstartDate").attr("prevvalue");
                        var nowVal = $thisDialog.find("#planstartDate").attr("value");
                        var format = $thisDialog.find("#planstartDate").attr("dateformat");
                        // Checks if the date entered is of form M/D/YYYY or MM/DD/YYYY or M/DD/YYYY or MM/D/YYYY
                        if(!/^\s*\d{1,2}\/\d{1,2}\/\d{4}\s*$/.test(nowVal)){
                          popUpDialogForAlerts("alert_dialog", invalid_date_msg);
                          return;
                        }
                        // Check if the date entered, which is done manually, obviously, is earleir than today
                        var now = Date.parse($("#date_today").val());                       
                        var dateSplt = nowVal.split("/");
                        var newDate = new Date(dateSplt[2], parseInt(dateSplt[0]-1), dateSplt[1]);
                        var isTodayAllowed = $("#isTodayAllowed").val();
                        if((now == newDate.getTime() && isTodayAllowed =="false") ||
                            (now > newDate.getTime())){
                          popUpDialogForAlerts("alert_dialog", date_before_or_today_msg);
                          return;
                        }

                        $.ajax( {
                              type : "POST",
                              url : "/portal/portal/channels/changeplandate",
                              data: {"channelId": channelId,
                                     "newDate": nowVal,
                                     "dateFormat": format
                                     },
                              dataType : "html",
                              success : function(html) {
                                  //$thisDialog.find("#planstartDate").attr("prevvalue", nowVal);
                                  $thisDialog.find("#planstartDate").attr("value", nowVal);
                                  $("#second_line_under_planned").closest(".widget_details_actionbox").attr("style", "height: 45px");
                                  $("#second_line_under_planned").show();
                                  $("#planned_or_not").html(planned_word);
                                  $("#planned_or_not").attr("planned", "1");
                                  if(now < newDate.getTime()){
                                    var formatted_nowVal = dateFormat(nowVal, g_dictionary.dateonlyFormat, false);
                                    $("#effective_date").text(formatted_nowVal);
                                  } else {
                                    $("#effective_date").text('<spring:message javaScriptEscape="true" code="ui.label.plan.date.not.yet.set"/>');
                                  }
                                  if(isTodayAllowed == "true"){
                                     $("li[id^='channel'].selected.channels").click(); 
                                     $("#catalog_tab").click();
                                  }
                                  },
                              error: function(XMLHttpRequest){
                                 //TODO
                              }
                            });
                      $(this).dialog("close");
                      $(this).dialog("destroy");
                     },
                     "Cancel": function () {
                       
                       $thisDialog.dialog("close");
                       $(this).dialog("destroy");
                         }
                   });
                   dialogButtonsLocalizer($thisDialog, {'Proceed': dialogProceed, 'Cancel': g_dictionary.dialogCancel});
                   $thisDialog.dialog("open");
         },error:function(){
           // need to handle TO-DO
         }
      });
    }

  
</script>

<style>
.ui-datepicker-trigger {
    margin-left : 5px;
    margin-top : 3px;
}

.datepicker_stlying{
  z-index: 9999
}
</style>

<spring:message code="date.format" var="ddMMMyyyy_format"/>
<input id="planning_for_first_time" type="hidden" name="planning_for_first_time" value="<c:out value="${futureRevisionDate}"/>"/>

<div class="widget_actionbar">
 <div class="widget_actionarea" id="top_actions" >

   <div id="spinning_wheel" style="display:none;">
         <div class="widget_blackoverlay widget_rightpanel">
         </div>
         <div class="widget_loadingbox widget_rightpanel">
           <div class="widget_loaderbox">
             <span class="bigloader"></span>
           </div>
           <div class="widget_loadertext">
             <p id="in_process_text"><spring:message code='label.loading.withdots'/></p>
           </div>
         </div>
   </div>

   <div class="widget_moreactions action_menu_container" title="<spring:message code='manage'/>" id="action_menu_container">
      <!--Actions popover starts here-->
      <div class="widget_actionpopover" id="action_menu" style="display:none">
          <div class="widget_actionpopover_top"></div>
            <div class="widget_actionpopover_mid">
              <ul class="widget_actionpoplist">
              <c:choose>
              <c:when test="${not empty channel}">
                <li id="editchannel_action" ><a href="javascript:void(0);"><spring:message code="label.channel.edit"/></a></li>
                <c:if test="${isChannelDeletionAllowed eq true}">  
                  <li id="deletechannel_action" ><a href="javascript:void(0);"><spring:message code="label.channel.delete"/></a></li>
                </c:if>
               </c:when>
               <c:otherwise>
                <li id="no_actions_available"><a href="javascript:void(0);"><spring:message code="label.no.actions.available"/></a></li>
               </c:otherwise>
               </c:choose>
              </ul>
            </div>
            <div class="widget_actionpopover_bot"></div>
        </div>
        <!--Actions popover ends here-->
      </div>
    </div>
</div>

<div class="top_notifications">
  <div id="top_message_panel" class="common_messagebox widget" style="display:none;">
    <button type="button" class="close js_close_parent" >&times;</button>
    <span id="status_icon"></span><p id="msg"></p>
  </div>
  <div id="action_result_panel" class="common_messagebox widget" style="display:none;">
    <button type="button" class="close js_close_parent" >&times;</button>
    <span id="status_icon"></span><p id="msg"></p>
  </div>
</div>


<div class="widget_browser">
<!-- Start View Channel Details -->
<div class="widget_browsermaster">
  <div class="widget_browser_contentarea">
      <div class="widget_browsergrid_wrapper master">
            <div class="widget_grid master even first">
                <div class="widget_grid_labels">
                    <span><spring:message code="label.name"/></span>
              </div>
              <div class="widget_grid_description">
                  <span class = "ellipsis"  title = "<c:out value="${channel.name}"/>"> <c:out value="${channel.name}"/></span>
              </div>
          </div>
          <div class="widget_grid master odd">
               <div class="widget_grid_labels">
                    <span><spring:message code="label.channel.fqdn"/></span>
              </div>
              <div class="widget_grid_description">
                <span class = "ellipsis"  title = "<c:out value="${channel.fqdnPrefix}.${publicHost}"/>"> 
                  <c:if test="${not empty channel.fqdnPrefix}"> ${channel.fqdnPrefix}.${publicHost} </c:if> 
                </span>
              </div>
          </div>
          <div class="widget_grid master even">
               <div class="widget_grid_labels">
                    <span><spring:message code="ui.products.label.create.catalog.select.currency"/></span>
              </div>
              <div class="widget_grid_description">
                  <span id="channeCurrencies<c:out value="${channel.id}"/>">
                    <c:forEach var="supported_currency" items="${supportedCurrencies}" varStatus="status">

                       <c:if test="${status.index > 0}">
                       ,
                       </c:if>

                       <c:out value="${supported_currency.currencyCode}"/>
                     </c:forEach>
                  </span>
              </div>
          </div>
      </div>
      <div class="widget_masterbigicons defaultbox_noedit">
          <div class="thumbnail_defaultcontainer">
              <div class="thumbnail_defaulticon channels">
                <!--  <img src="<%=request.getContextPath() %>/portal/logo/channel/<c:out value="${channel.id}"/>" class="js_channel_logo_${channel.id}" style="height:99px;width:99px;" />-->
              </div>
           </div>    
        </div>
  </div>
</div>
<!-- End view Channel Details -->

  <!-- The Details and other tabs' content area  -->
  <div class="widget_browser_contentarea">
  
      <ul class="widgets_detailstab">
          <li id="details_tab" class="widgets_detailstab active"><spring:message code="label.details"/></li>
          <c:if test="${not empty channel}">
          <li id="currencies_tab" class="widgets_detailstab nonactive"><spring:message code="label.channel.currencies"/></li>
          <li id="catalog_tab" class="widgets_detailstab nonactive"><spring:message code="label.channel.catalog"/></li>
          <li id="service_controls_tab" class="widgets_detailstab nonactive"><spring:message code="label.channel.service.controls"/></li>
          <li id="urls_tab" class="widgets_detailstab nonactive"><spring:message code="label.channels.tab.urls"/></li>
          <li id="branding_tab" class="widgets_detailstab nonactive"><spring:message code="label.channels.tab.branding"/>
          <c:if test="${previewModeInCurrentChannel ne null}">
          <i style="margin-left:5px;" title='<spring:message code="label.unpublished.channel.branding"/>' class="fa fa-file-image-o"></i>
          </c:if>
          </li>
          </c:if>
      </ul>
  
      <div class="widget_details_actionbox" id="main_action_box" style="height: 30px">

        <c:set var="plannedIsLast" value="false"/>
        <c:set var="currentIsLast" value="false"/>
        <c:if test="${!isCurrentThere && !isHistoryThere}">
          <c:set var="plannedIsLast" value="true"/>
        </c:if>
        <c:if test="${isCurrentThere && !isHistoryThere}">
          <c:set var="currentIsLast" value="true"/>
        </c:if>
        <ul class="widget_detail_navpanel" id="catalog_links" style="display:None">
           <li class="widget_detail_navpanel <c:if test="${plannedIsLast}">last</c:if>"  style="float:left; color:#000;" id="catalog_planned_tab">
              <a href="javascript:void(0);" onclick="preViewCatalogPlanned(event);"
                         planned="1" channelid="<c:out value="${channel.id}"/>"
                         id="planned_or_not">
                 <spring:message code="label.next.planned.version"/>
              </a>
           </li>

           <c:if test="${isCurrentThere}">
             <li class="widget_detail_navpanel <c:if test="${currentIsLast}">last</c:if>"  style="float:left;" id="catalog_current_tab">
                  <a href="javascript:void(0);" onclick="viewCatalogCurrent();"><spring:message code="label.current"/>
                  </a>
              </li>
            </c:if>

            <c:if test="${isHistoryThere}">
             <li class="widget_detail_navpanel last"  style="float:left;" id="catalog_history_tab">
                <a href="javascript:void(0);" onclick="viewCatalogHistory();"><spring:message code="label.history"/>
                </a>
             </li>
           </c:if>
        </ul>
  
        <div class="widget_subactions grid action_menu_container" id="action_currency" style="display:None;float:right;">
          <div class="widget_actionpopover_grid" id="currency_action_menu" style="display:None;">
            <div class="widget_actionpopover_top grid"></div>
                <div class="widget_actionpopover_mid">
                  <ul class="widget_actionpoplist">
                    <c:choose>
                      <c:when test="${currenciestoadd}">
                        <li class="view_volume_details_link" id="add_currencies" onclick="editCurrencies(event,this)" style="display: block;">
                          <spring:message code="label.channel.currencies.Add"/>
                        </li>
                      </c:when>
                      <c:otherwise>
                        <li id="no_actions_available"><spring:message code="label.no.actions.available"/></li>
                      </c:otherwise>
                    </c:choose>
                  </ul>
                </div>
            <div class="widget_actionpopover_bot"></div>
         </div>
       </div>

      <div id="second_line_under_planned" class="widget_detail_navpanel" style="width: 100%; margin-bottom:2px; display:none">
          
          
          <span class="widget_detail_navpanel" style="margin: 0 0 0 10px;" id="second_line_under_planned_span">
          <spring:message code="label.effective.date"/>
            <span id="effective_date">
                <c:choose>
                  <c:when test="${effectiveDate != null}">
                      <fmt:formatDate value="${effectiveDate}" pattern="${ddMMMyyyy_format}" />
                  </c:when>
                  <c:otherwise>
                      <spring:message code="ui.label.plan.date.not.yet.set"/>
                  </c:otherwise>
               </c:choose>
            </span>
          </span>
      </div>
      
       <div id="action_service_settings" style="display:None;">
          <div style="height:35px;">
			    <div style="padding:8px;">
				    <c:if test="${services}">
				   		<span>
	     					 <spring:message code="ui.label.service.sub.title" />
	     				</span>
						 <select  id="selectedInstance" class="select" style="margin: 0 0px; width:20px" onchange="refreshChannelServiceSettings();"  >
						      <option value=""><spring:message code="label.view.channel.choose.service"/></option>
							    <c:forEach items="${cloudService}" var="service">
							    	<optgroup label="${service.servicename}" class="highlight">
						                       <c:forEach items="${service.instances}" var="instance">
						                        	<option value=<c:out value="${instance.instanceuuid}"/>>${instance.instancename}</option>
						        			   </c:forEach> 
						        	</optgroup>		   
						        </c:forEach>
				         </select>
				    </c:if>   
				</div>
          </div>
       </div>
       <ul style="display:none;" class="widget_detail_navpanel" id="branding_actions" >
       <li id="show_current_channel_branding" style="border-right:none;"><a href="javascript:void(0);" style="color:#000;"><spring:message code="label.current"/></a></li>
       <c:if test="${previewModeInCurrentChannel ne null}">
         <li id="show_unpublished_channel_branding" class="last" style="border-left:1px solid #999;padding-left:10px;"><a href="javascript:void(0);"><spring:message code="label.look.and.feel.unpublished"/></a></li>
       </c:if>
       </ul>
    </div>
  
      <!-- The channel details starts here -->
      <div id="details_content" class="widget_browsergrid_wrapper details">
            <div class="widget_grid details even">
                  <div class="widget_grid_labels">
                      <span><spring:message code="ui.label.code"/></span>
                  </div>
                  <div class="widget_grid_description" >
                    <span class = "ellipsis"  title = "<c:out value="${channel.code}"/>" ><c:out value="${channel.code}"/></span>
                  </div>
            </div>
            <div class="widget_grid details even">
                  <div class="widget_grid_labels">
                      <span><spring:message code="label.channel.description"/></span>
                  </div>
                  <div class="widget_grid_description" >
                      <span class = "ellipsis"  title = "<c:out value="${channel.description}"/>"> <c:out value="${channel.description}"/></span>
                  </div>
            </div> 
            <div class="widget_grid details even">
                  <div class="widget_grid_labels">
                      <span><spring:message code="label.billing.group"/></span>
                  </div>
                  <div class="widget_grid_description" >
                      <span class = "ellipsis"  title = "<c:out value="${channel.billingGroup.name}"/>"> <c:out value="${channel.billingGroup.name}"/></span>
                  </div>
            </div>
             <div class="widget_grid details even">
                  <div class="widget_grid_labels">
                      <span><spring:message code="label.locale"/></span>
                  </div>
                  <div class="widget_grid_description" >
                  <c:choose>
                    <c:when test="${not empty channel.channelBrandingConfigurations.defaultLocale}">
                      <span class = "ellipsis"  title = "<c:out value="${defaultLocaleValue}"/>" ><c:out value="${defaultLocaleValue}"/></span>
                    </c:when>
                    <c:when test="${not empty global_default_locale}">
                      <span class = "ellipsis"  title = "<c:out value="${global_default_locale}"/>" ><c:out value="${global_default_locale}"/>&nbsp;(<spring:message code="label.default"/>)</span>
                    </c:when>
                    <c:otherwise>
                    <c:if test="${not empty channel}">
                      <span class="ellipsis none"><spring:message code="label.none"/>&nbsp;(<spring:message code="label.default"/>)</span>
                     </c:if>
                    </c:otherwise>
                  </c:choose>
                    
                  </div>
            </div>
            <div class="widget_grid details even">
                  <div class="widget_grid_labels">
                      <span><spring:message code="label.time.zone"/></span>
                  </div>
                  <div class="widget_grid_description" >
                  <c:choose>
                    <c:when test="${not empty channel.channelBrandingConfigurations.defaultTimeZone}">
                      <span class = "ellipsis"  title = "<c:out value="${defaultTimeZone}"/>"> <c:out value="${defaultTimeZone}"/></span>
                    </c:when>
                    <c:when test="${not empty global_default_timezone}">
                      <span class = "ellipsis"  title = "<c:out value="${global_default_timezone}"/>" ><c:out value="${global_default_timezone}"/>&nbsp;(<spring:message code="label.default"/>)</span>
                    </c:when>
                    <c:otherwise>
                    <c:if test="${not empty channel}">
                      <span class="ellipsis none"><spring:message code="label.none"/>&nbsp;(<spring:message code="label.default"/>)</span>
                    </c:if>
                    </c:otherwise>
                  </c:choose>
                      
                  </div>
            </div>
            <div class="widget_grid details">
               <div class="widget_grid_labels">
                  <span><spring:message code="label.channel.allow.signup"/></span>
               </div>
               <div class="widget_grid_description" >
                 <span class="details_wide">
                   <c:choose>
						<c:when test="${isDefaultChannel}">
						  <c:if test="${global_signup_allowed}">
							 <spring:message code='label.yes' />
							</c:if>
							<c:if test="${!global_signup_allowed}">
							  <spring:message code='label.no' />
							</c:if>
						</c:when>
						<c:otherwise>
							<c:if test="${channel.channelBrandingConfigurations.signupAllowed == 'true' }">
							  <spring:message code='label.yes' />
							</c:if>
							<c:if test="${channel.channelBrandingConfigurations.signupAllowed == 'false' }">
							  <spring:message code='label.no' />
							</c:if>
						</c:otherwise>
					</c:choose>
                 </span>
               </div>
            </div>
            <div class="widget_grid details even">
                  <div class="widget_grid_labels">
                      <span><spring:message code="label.channel.helpdesk.email"/></span>
                  </div>
                  <div class="widget_grid_description" >
                  <c:choose>
                    <c:when test="${not empty channel.channelBrandingConfigurations.helpDeskEmail}">
                      <span class = "ellipsis"  title = "<c:out value="${channel.channelBrandingConfigurations.helpDeskEmail}"/>" ><c:out value="${channel.channelBrandingConfigurations.helpDeskEmail}"/></span>
                    </c:when>
                    <c:when test="${not empty help_desk_email}">
                      <span class = "ellipsis"  title = "<c:out value="${help_desk_email}"/>" ><c:out value="${help_desk_email}"/>&nbsp;(<spring:message code="label.default"/>)</span>
                    </c:when>
                    <c:otherwise>
                    <c:if test="${not empty channel}">
                      <span class="ellipsis none"><spring:message code="label.none"/>&nbsp;(<spring:message code="label.default"/>)</span>
                     </c:if>
                    </c:otherwise>
                  </c:choose>
                    
                  </div>
            </div>
            <div class="widget_grid details even">
                  <div class="widget_grid_labels">
                      <span><spring:message code="label.channel.helpdesk.phone"/></span>
                  </div>
                  <div class="widget_grid_description" >
                      <c:choose>
                    <c:when test="${not empty channel.channelBrandingConfigurations.helpDeskPhone}">
                      <span class = "ellipsis"  title = "<c:out value="${channel.channelBrandingConfigurations.helpDeskPhone}"/>"> <c:out value="${channel.channelBrandingConfigurations.helpDeskPhone}"/></span>
                    </c:when>
                    <c:when test="${not empty help_desk_phone}">
                      <span class = "ellipsis"  title = "<c:out value="${help_desk_phone}"/>" ><c:out value="${help_desk_phone}"/>&nbsp;(<spring:message code="label.default"/>)</span>
                    </c:when>
                    <c:otherwise>
                     <c:if test="${not empty channel}">
                      <span class="ellipsis none"><spring:message code="label.none"/>&nbsp;(<spring:message code="label.default"/>)</span>
                     </c:if>
                    </c:otherwise>
                  </c:choose>
                      
                  </div>
            </div>
            <div class="widget_grid details even">
                  <div class="widget_grid_labels">
                      <span><spring:message code="label.channel.whitelist.countries"/></span>
                  </div>
                  <div class="widget_grid_description" >
                      <c:choose>
                    
                    <c:when test="${not empty whitelistcountries}">
                      <span class = "details_wide" >
                      <c:forEach items="${whitelistcountries}" var="country" varStatus="status">
                        <c:out
                          value="${country.name}" /><c:if test="${!status.last}">,</c:if>
                      </c:forEach>
                      <c:if test="${whitelistcountries_global}">&nbsp;(<spring:message code="label.default"/>)</c:if></span>
                    </c:when>
                    <c:otherwise>
                     <c:if test="${not empty channel}">
                      <span class="ellipsis none"><spring:message code="label.none"/>&nbsp;(<spring:message code="label.default"/>)</span>
                     </c:if>
                    </c:otherwise>
                  </c:choose>
                      
                  </div>
            </div>
            <div class="widget_grid details even">
                  <div class="widget_grid_labels">
                      <span><spring:message code="label.channel.blacklist.countries"/></span>
                  </div>
                  <div class="widget_grid_description" >
									<c:choose>
										<c:when test="${not empty blacklistcountries}">
											<span class="details_wide">
											<c:forEach items="${blacklistcountries}" var="country" varStatus="status">
											  <c:out
                          value="${country.name}" /><c:if test="${!status.last}">,</c:if>
											</c:forEach>
											<c:if test="${blacklistcountries_global}">&nbsp;(<spring:message
													code="label.default" />)</c:if>
													</span>
										</c:when>
										<c:otherwise>
										 <c:if test="${not empty channel}">
											<span class="ellipsis none"><spring:message
													code="label.none" />&nbsp;(<spring:message code="label.default"/>)</span>
										 </c:if>
										</c:otherwise>
									</c:choose>
                </div>
            </div>
            <div class="widget_grid details even">
                  <div class="widget_grid_labels">
                      <span><spring:message code="label.channel.whitelist.email.domains"/></span>
                  </div>
                  <div class="widget_grid_description" >
                  <c:choose>
                    <c:when
                      test="${not empty channel.channelBrandingConfigurations.whitelistdomains}">
                      <span class = "ellipsis"  title = "<c:out value="${channel.channelBrandingConfigurations.whitelistdomains}"/>"> <c:out value="${channel.channelBrandingConfigurations.whitelistdomains}"/></span>
                    </c:when>
                    <c:when test="${not empty whitelistdomains}">
                      <span class="ellipsis"
                        title="<c:out value="${whitelistdomains}"/>"><c:out
                          value="${whitelistdomains}" />&nbsp;(<spring:message
                          code="label.default" />)</span>
                    </c:when>
                    <c:otherwise>
                     <c:if test="${not empty channel}">
                      <span class="ellipsis none"><spring:message
                          code="label.none" />&nbsp;(<spring:message code="label.default"/>)</span>
                      </c:if>
                    </c:otherwise>
                  </c:choose>
                      
                  </div>
            </div>
            <div class="widget_grid details even">
                  <div class="widget_grid_labels">
                      <span><spring:message code="label.channel.blacklist.email.domains"/></span>
                  </div>
                  <div class="widget_grid_description" >
                    <c:choose>
                    <c:when
                      test="${not empty channel.channelBrandingConfigurations.blacklistdomains}">
                      <span class = "ellipsis"  title = "<c:out value="${channel.channelBrandingConfigurations.blacklistdomains}"/>"> <c:out value="${channel.channelBrandingConfigurations.blacklistdomains}"/></span>
                    </c:when>
                    <c:when test="${not empty blacklistdomains}">
                      <span class="ellipsis"
                        title="<c:out value="${blacklistdomains}"/>"><c:out
                          value="${blacklistdomains}" />&nbsp;(<spring:message
                          code="label.default" />)</span>
                    </c:when>
                    <c:otherwise>
                     <c:if test="${not empty channel}">
                      <span class="ellipsis none"><spring:message
                          code="label.none" />&nbsp;(<spring:message code="label.default"/>)</span>
                     </c:if>
                    </c:otherwise>
                  </c:choose>
                      
                  </div>
            </div>                                               
      
  
      </div>
      <!-- The channel details ends here -->
  
      <!-- The channel currencies' details starts here -->
      <div class="widget_browsergrid_wrapper details" id="currencies_content" style="display:none;overflow-x: hidden;overflow-y: auto;">
        <div id="currency_row_container">
            <c:forEach var="currency" items="${supportedCurrencies}" varStatus="status">

              <c:choose>
                <c:when test="${status.index % 2 == 0}">
                  <c:set var="rowClass" value="odd"/>
                </c:when>
                <c:otherwise>
                    <c:set var="rowClass" value="even"/>
                </c:otherwise>
              </c:choose>

              <div class="<c:out value="widget_grid details ${rowClass}"/>">
                  <div class="widget_checkbox">
                    <span class="checked"></span> 
                  </div>
                  <div class="widget_grid_description" style="border:none;margin:0;">
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
      <!-- The channel currencies' details ends here -->
      
            <!-- The channel URLs starts here -->
      <div id="channel_urls" class="widget_browsergrid_wrapper details" style="display:none;">
            <div class="widget_grid details">
                  <div class="widget_grid_labels">
                      <span><spring:message code="label.channel.url.blog"/></span>
                  </div>
                  <div class="widget_grid_description wide" >
                    <c:choose>
	                    <c:when
	                      test="${not empty channel.channelBrandingConfigurations.blogUrl}">
	                      <c:choose>
	                        <c:when test="${channel.channelBrandingConfigurations.blogUrl == '#'}">
	                          <span class="none"><spring:message code="message.no.url.set" /></span>
	                        </c:when>
	                        <c:otherwise>
	                          <span class = "details_wide"  title = "<c:out value="${channel.channelBrandingConfigurations.blogUrl}"/>" ><a href="${channel.channelBrandingConfigurations.blogUrl}" target="_blank"><c:out value="${channel.channelBrandingConfigurations.blogUrl}"/></a></span>
	                        </c:otherwise>
	                      </c:choose>
	                    </c:when>
	                    <c:when test="${not empty marketing_blog_url}">
	                      <c:choose>
                          <c:when test="${marketing_blog_url == '#'}">
                            <span class="none details_small"><spring:message code="message.no.url.set" /></span><span class="details_small margin_left_small">(<spring:message
                            code="label.default" />)</span>
                          </c:when>
                          <c:otherwise>
                            <span class="details_wide"
                          title="<c:out value="${marketing_blog_url}"/>"><a href="${marketing_blog_url}" target="_blank"><c:out
                            value="${marketing_blog_url}" /></a>&nbsp;(<spring:message
                            code="label.default" />)</span>
                          </c:otherwise>
                        </c:choose>
	                      
	                    </c:when>
	                    <c:otherwise>
	                     <c:if test="${not empty channel}">
	                      <span class="details_wide none"><spring:message
	                          code="label.none" />&nbsp;(<spring:message code="label.default"/>)</span>
	                     </c:if>
	                    </c:otherwise>
	                  </c:choose>
                      
                  </div>
            </div>
            <div class="widget_grid details">
                  <div class="widget_grid_labels">
                      <span><spring:message code="label.channel.url.contact"/></span>
                  </div>
                  <div class="widget_grid_description wide" >
                    <c:choose>
                      <c:when
                        test="${not empty channel.channelBrandingConfigurations.contactUrl}">
                        <c:choose>
                          <c:when test="${channel.channelBrandingConfigurations.contactUrl == '#'}">
                            <span class="details_wide none"><spring:message code="message.no.url.set" /></span>
                          </c:when>
                          <c:otherwise>
                            <span class = "details_wide"  title = "<c:out value="${channel.channelBrandingConfigurations.contactUrl}"/>" ><a href="${channel.channelBrandingConfigurations.contactUrl}" target="_blank"><c:out value="${channel.channelBrandingConfigurations.contactUrl}"/></a></span>
                          </c:otherwise>
                        </c:choose>
                        
                      </c:when>
                      <c:when test="${not empty marketing_contact_url}">
                      <c:choose>
                          <c:when test="${marketing_contact_url == '#'}">
                            <span class="none details_small"><spring:message code="message.no.url.set" /></span><span class="details_small margin_left_small">(<spring:message
                            code="label.default" />)</span>
                          </c:when>
                          <c:otherwise>
                            <span class="details_wide"
                          title="<c:out value="${marketing_contact_url}"/>"><a href="${marketing_contact_url}" target="_blank"><c:out
                            value="${marketing_contact_url}" /></a>&nbsp;(<spring:message
                            code="label.default" />)</span>
                          </c:otherwise>
                        </c:choose>
                        
                      </c:when>
                      <c:otherwise>
                       <c:if test="${not empty channel}">
                        <span class="details_wide none"><spring:message
                            code="label.none" />&nbsp;(<spring:message code="label.default"/>)</span>
                       </c:if>
                      </c:otherwise>
                    </c:choose>
                      
                  </div>
            </div>
            <div class="widget_grid details">
                  <div class="widget_grid_labels">
                      <span><spring:message code="label.channel.url.forum"/></span>
                  </div>
                  <div class="widget_grid_description wide" >
                    <c:choose>
                      <c:when
                        test="${not empty channel.channelBrandingConfigurations.forumUrl}">
                        <c:choose>
                          <c:when test="${channel.channelBrandingConfigurations.forumUrl == '#'}">
                            <span class="details_wide none"><spring:message code="message.no.url.set" /></span>
                          </c:when>
                          <c:otherwise>
                            <span class = "details_wide"  title = "<c:out value="${channel.channelBrandingConfigurations.forumUrl}"/>" ><a href="${channel.channelBrandingConfigurations.forumUrl}" target="_blank"><c:out value="${channel.channelBrandingConfigurations.forumUrl}"/></a></span>
                          </c:otherwise>
                        </c:choose>
                        
                      </c:when>
                      <c:when test="${not empty marketing_forum_url}">
                        <c:choose>
                          <c:when test="${marketing_forum_url == '#'}">
                            <span class="none details_small"><spring:message code="message.no.url.set" /></span><span class="details_small margin_left_small">(<spring:message
                            code="label.default" />)</span>
                          </c:when>
                          <c:otherwise>
                            <span class="details_wide"
                          title="<c:out value="${marketing_forum_url}"/>"><a href="${marketing_forum_url}" target="_blank"><c:out
                            value="${marketing_forum_url}" /></a>&nbsp;(<spring:message
                            code="label.default" />)</span>
                          </c:otherwise>
                        </c:choose>
                        
                      </c:when>
                      <c:otherwise>
                       <c:if test="${not empty channel}">
                        <span class="details_wide none"><spring:message
                            code="label.none" />&nbsp;(<spring:message code="label.default"/>)</span>
                       </c:if>
                      </c:otherwise>
                    </c:choose>
                      
                  </div>
            </div>
            <div class="widget_grid details">
                  <div class="widget_grid_labels">
                      <span><spring:message code="label.channel.url.help"/></span>
                  </div>
                  <div class="widget_grid_description wide" >
                    <c:choose>
                      <c:when
                        test="${not empty channel.channelBrandingConfigurations.helpUrl}">
                        <c:choose>
                          <c:when test="${channel.channelBrandingConfigurations.helpUrl == '#'}">
                            <span class="details_wide none"><spring:message code="message.no.url.set" /></span>
                          </c:when>
                          <c:otherwise>
                           <span class = "details_wide"  title = "<c:out value="${channel.channelBrandingConfigurations.helpUrl}"/>" ><a href="${channel.channelBrandingConfigurations.helpUrl}" target="_blank"><c:out value="${channel.channelBrandingConfigurations.helpUrl}"/></a></span>
                          </c:otherwise>
                        </c:choose>
                        
                      </c:when>
                      <c:when test="${not empty marketing_help_url}">
                      <c:choose>
                          <c:when test="${marketing_help_url == '#'}">
                            <span class="none details_small"><spring:message code="message.no.url.set" /></span><span class="details_small margin_left_small">(<spring:message
                            code="label.default" />)</span>
                          </c:when>
                          <c:otherwise>
                            <span class="details_wide"
                          title="<c:out value="${marketing_help_url}"/>"><a href="${marketing_help_url}" target="_blank"><c:out
                            value="${marketing_help_url}" /></a>&nbsp;(<spring:message
                            code="label.default" />)</span>
                          </c:otherwise>
                        </c:choose>
                        
                      </c:when>
                      <c:otherwise>
                       <c:if test="${not empty channel}">
                        <span class="details_wide none"><spring:message
                            code="label.none" />&nbsp;(<spring:message code="label.default"/>)</span>
                       </c:if>
                      </c:otherwise>
                    </c:choose>
                      
                  </div>
            </div>
            <div class="widget_grid details">
                  <div class="widget_grid_labels">
                      <span><spring:message code="label.channel.url.privacy"/></span>
                  </div>
                  <div class="widget_grid_description wide" >
                    <c:choose>
                      <c:when
                        test="${not empty channel.channelBrandingConfigurations.privacyUrl}">
                        <c:choose>
                          <c:when test="${channel.channelBrandingConfigurations.privacyUrl == '#'}">
                            <span class="details_wide none"><spring:message code="message.no.url.set" /></span>
                          </c:when>
                          <c:otherwise>
                           <span class = "details_wide"  title = "<c:out value="${channel.channelBrandingConfigurations.privacyUrl}"/>" ><a href="${channel.channelBrandingConfigurations.privacyUrl}" target="_blank"><c:out value="${channel.channelBrandingConfigurations.privacyUrl}"/></a></span>
                          </c:otherwise>
                        </c:choose>
                        
                      </c:when>
                      <c:when test="${not empty marketing_privacy_url}">
                      <c:choose>
                          <c:when test="${marketing_privacy_url == '#'}">
                            <span class="none details_small"><spring:message code="message.no.url.set" /></span><span class="details_small margin_left_small">(<spring:message
                            code="label.default" />)</span>
                          </c:when>
                          <c:otherwise>
                            <span class="details_wide"
                          title="<c:out value="${marketing_privacy_url}"/>"><a href="${marketing_privacy_url}" target="_blank"><c:out
                            value="${marketing_privacy_url}" /></a>&nbsp;(<spring:message
                            code="label.default" />)</span>
                          </c:otherwise>
                        </c:choose>
                        
                      </c:when>
                      <c:otherwise>
                       <c:if test="${not empty channel}">
                        <span class="details_wide none"><spring:message
                            code="label.none" />&nbsp;(<spring:message code="label.default"/>)</span>
                       </c:if>
                      </c:otherwise>
                    </c:choose>
                      
                  </div>
            </div>
            <div class="widget_grid details">
                  <div class="widget_grid_labels">
                      <span><spring:message code="label.channel.url.support"/></span>
                  </div>
                  <div class="widget_grid_description wide" >
                    <c:choose>
                      <c:when
                        test="${not empty channel.channelBrandingConfigurations.supportUrl}">
                        <c:choose>
                          <c:when test="${channel.channelBrandingConfigurations.supportUrl == '#'}">
                            <span class="details_wide none"><spring:message code="message.no.url.set" /></span>
                          </c:when>
                          <c:otherwise>
                           <span class = "details_wide"  title = "<c:out value="${channel.channelBrandingConfigurations.supportUrl}"/>" ><a href="${channel.channelBrandingConfigurations.supportUrl}" target="_blank"><c:out value="${channel.channelBrandingConfigurations.supportUrl}"/></a></span>
                          </c:otherwise>
                        </c:choose>
                        
                      </c:when>
                      <c:when test="${not empty marketing_support_url}">
                        <c:choose>
                          <c:when test="${marketing_support_url == '#'}">
                            <span class="none details_small"><spring:message code="message.no.url.set" /></span><span class="details_small margin_left_small">(<spring:message
                            code="label.default" />)</span>
                          </c:when>
                          <c:otherwise>
                            <span class="details_wide"
                          title="<c:out value="${marketing_support_url}"/>"><a href="${marketing_support_url}" target="_blank"><c:out
                            value="${marketing_support_url}" /></a>&nbsp;(<spring:message
                            code="label.default" />)</span>
                          </c:otherwise>
                        </c:choose>
                        
                      </c:when>
                      <c:otherwise>
                       <c:if test="${not empty channel}">
                        <span class="details_wide none"><spring:message
                            code="label.none" />&nbsp;(<spring:message code="label.default"/>)</span>
                       </c:if>
                      </c:otherwise>
                    </c:choose>
                      
                  </div>
            </div>
            <div class="widget_grid details">
                  <div class="widget_grid_labels">
                      <span><spring:message code="label.channel.url.tou"/></span>
                  </div>
                  <div class="widget_grid_description wide" >
                  <c:choose>
                      <c:when
                        test="${not empty channel.channelBrandingConfigurations.touUrl}">
                        <c:choose>
                          <c:when test="${channel.channelBrandingConfigurations.touUrl == '#'}">
                            <span class="details_wide none"><spring:message code="message.no.url.set" /></span>
                          </c:when>
                          <c:otherwise>
                           <span class = "details_wide"  title = "<c:out value="${channel.channelBrandingConfigurations.touUrl}"/>" ><a href="${channel.channelBrandingConfigurations.touUrl}" target="_blank"><c:out value="${channel.channelBrandingConfigurations.touUrl}"/></a></span>
                          </c:otherwise>
                        </c:choose>
                        
                      </c:when>
                      <c:when test="${not empty marketing_tou_url}">
                        <c:choose>
                          <c:when test="${marketing_tou_url == '#'}">
                            <span class="none details_small"><spring:message code="message.no.url.set" /></span><span class="details_small margin_left_small">(<spring:message
                            code="label.default" />)</span>
                          </c:when>
                          <c:otherwise>
                            <span class="details_wide"
                          title="<c:out value="${marketing_tou_url}"/>"><a href="${marketing_tou_url}" target="_blank"><c:out
                            value="${marketing_tou_url}" /></a>&nbsp;(<spring:message
                            code="label.default" />)</span>
                          </c:otherwise>
                        </c:choose>
                        
                      </c:when>
                      <c:otherwise>
                       <c:if test="${not empty channel}">
                        <span class="details_wide none"><spring:message
                            code="label.none" />&nbsp;(<spring:message code="label.default"/>)</span>
                       </c:if>
                      </c:otherwise>
                    </c:choose>
                      
                  </div>
            </div>
  
      </div>
      <!-- The channel URLs ends here -->
      <!-- Look & Feel starts here -->
      <c:if test="${not empty channel}">
       <div id="branding_tab_details" class="widget_browsergrid_wrapper details" style="display:none;">
       <div class="widget_details_actionbox">
         <div class="widget_subactions grid action_menu_container" id="current_branding" style="float: right; display: block;">
        <div class="widget_actionpopover_grid" id="current_branding_action_menu" style="display: none;">
          <div class="widget_actionpopover_top grid"></div>
              <div class="widget_actionpopover_mid">
                <ul class="widget_actionpoplist">
                  <li class="js_edit_channel_branding_action" ><a href="javascript:void(0);"><spring:message code="label.edit.channel.branding"/></a></li>
                    <li id="reset_channel_brandings_action" class="last"><a href="javascript:void(0);"><spring:message code="label.reset.channel.brandings"/></a></li>
                </ul>
              </div>
          <div class="widget_actionpopover_bot"></div>
       </div>
     </div>
       </div>
       <div class="widget_grid details">
           <div class="widget_grid_labels">
             <span><spring:message code="label.channel.custom.css"/></span>
           </div>
           <div class="widget_grid_description" >
             
             <c:choose>
               <c:when test="${empty channel.channelBrandingConfigurations.cssFileName}">
               <span class = "details_wide none"  id="channel_custom_css">
               <spring:message code="label.none"/>&nbsp;(<spring:message code="label.default"/>)
               </span>
               </c:when>
               <c:otherwise>
               <span class = "details_wide"  id="channel_custom_css">
               <a href="/portal/portal/channels/channel/${channel.id}/css" >${channel.channelBrandingConfigurations.cssFileName}</a>
               </span>
               </c:otherwise>
               </c:choose>
             
           </div>
         </div>
         <div class="widget_grid details">
           <div class="widget_grid_labels">
             <span><spring:message code="label.channel.logo"/></span>
           </div>
           <div class="widget_grid_description" >
             <span class="none"><img class="js_channel_logo_${channel.id}" src="/portal/portal/logo/channel/${channel.id}">
             <c:if test="${empty channel.channelBrandingConfigurations.logoImageFileName}">
              (<spring:message code="label.default"/>)
             </c:if>
             </span>
             
               
           </div>
         </div>
         <div class="widget_grid details">
           <div class="widget_grid_labels">
             <span><spring:message code="label.channel.favicon"/></span>
           </div>
           <div class="widget_grid_description" >
             <span class="favicon_img none"><img class="js_channel_favicon_${channel.id}" src="/portal/portal/logo/channel/${channel.id}/favicon">
             <c:if test="${empty channel.channelBrandingConfigurations.favIconImageFileName}">
              (<spring:message code="label.default"/>)
             </c:if>
             </span>
             
           </div>
         </div>
      </div>
      </c:if>
      <div id="unpublished_branding_tab_details" class="widget_browsergrid_wrapper details" style="display:none;">
       <div class="widget_details_actionbox">
         <div class="widget_subactions grid action_menu_container" id="unpublished_branding" style="float: right; display: block;">
        <div class="widget_actionpopover_grid" id="unpublished_branding_action_menu" style="display: none;">
          <div class="widget_actionpopover_top grid"></div>
              <div class="widget_actionpopover_mid">
                <ul class="widget_actionpoplist">
                 <li class="js_edit_channel_branding_action" ><a href="javascript:void(0);"><spring:message code="label.edit.channel.branding"/></a></li>
                  <c:if test="${previewModeInCurrentChannel ne null}">
				           <li id="publishchannel_brandings_action"><a href="javascript:void(0);"><spring:message code="label.publish"/></a></li>
				         </c:if>
                </ul>
              </div>
          <div class="widget_actionpopover_bot"></div>
       </div>
     </div>
       </div>
       <c:if test="${previewModeInCurrentChannel ne null && previewModeInCurrentChannel.cssFileName ne null}">
       <div class="widget_grid details">
           <div class="widget_grid_labels">
             <span><spring:message code="label.channel.custom.css"/></span>
           </div>
           <div class="widget_grid_description" >
             
               <span class = "details_wide"  id="channel_custom_css">
               <a href="/portal/portal/channels/channel/${channel.id}/css?unpublished=true" >${previewModeInCurrentChannel.cssFileName}</a>
               </span>
             
           </div>
         </div>
         </c:if>
         <c:if test="${previewModeInCurrentChannel ne null && previewModeInCurrentChannel.logoImageFileName ne null}">
         <div class="widget_grid details">
           <div class="widget_grid_labels">
             <span><spring:message code="label.channel.logo"/></span>
           </div>
           <div class="widget_grid_description" >
             <span><img class="js_unpublished_channel_logo_${channel.id}" src="/portal/portal/logo/channel/${channel.id}?unpublished=true"></span>
               
           </div>
         </div>
         </c:if>
         <c:if test="${previewModeInCurrentChannel ne null && previewModeInCurrentChannel.favIconImageFileName ne null}">
         <div class="widget_grid details">
           <div class="widget_grid_labels">
             <span><spring:message code="label.channel.favicon"/></span>
           </div>
           <div class="widget_grid_description" >
             <span class="favicon_img"><img class="js_unpublished_channel_favicon_${channel.id}" src="/portal/portal/logo/channel/${channel.id}/favicon?unpublished=true"></span>
             
           </div>
         </div>
         </c:if>
         <div class="widget_grid details">
           <div class="widget_grid_labels">
             <span><spring:message code="label.channel.branding.preview.link"/></span>
           </div>
           <div class="widget_grid_description" id="look_and_feel_preview">
             <c:choose>
               <c:when test="${previewModeInCurrentChannel ne null}" >
                 <span class="details_wide" ><a href="<%= request.getContextPath() %>/portal/channels/list?previewchannelcode=${channel.code}">${baseURL}/portal/channels/list?previewchannelcode=${channel.code}</a></span>
               </c:when>
               <c:otherwise>
                 <span class = "details_wide none">
                   <spring:message code="label.none"/>
                 </span>
               </c:otherwise>
             
             </c:choose>
             
             
           </div>
         </div>
         <div class="widget_grid details">
           <div class="widget_grid_labels">
             <span><spring:message code="label.channel.branding.anonymous.preview.link"/></span>
           </div>
           <div class="widget_grid_description" id="anonymous_look_and_feel_preview">
             <c:choose>
               <c:when test="${previewModeInCurrentChannel ne null}" >
               
               
                 <span class="details_wide"><a id="anonymous_preview" data-current_user_param="${currentUser.param}" data-channel_code="${channel.code}" href="<%= request.getContextPath() %>/portal/login?previewchannelcode=${channel.code}">${baseURL}/portal/login?previewchannelcode=${channel.code}</a></span>
               </c:when>
               <c:otherwise>
                 <span class = "details_wide none">
                   <spring:message code="label.none"/>
                 </span>
               </c:otherwise>
             
             </c:choose>
             
             
           </div>
         </div>
      </div>
      <!-- Look & Feel ends here -->
  
      <div id="dialog_edit_currencies" title='<spring:message code="label.channel.currencies.Add"/>' style="overflow-x: hidden; overflow-y: auto;">
      </div>
  
      <div id="catalog_content">
      </div>
    
      
    <div class="widget_browsergrid_wrapper details" id="service_controls_content" style="display:none;overflow-x: hidden;overflow-y: auto;">
    <div id="service_controls_content_row_container">
    <div id="channelServiceSettingsDiv" class="widget_details_actionbox">
    </div>      
    </div>
    </div>
  </div>
</div>

<div id="plan_date_div" title='<spring:message code="label.set.plan.date"/>'>
</div>

<div id="dialog_bundle_pricing" title='<spring:message code="ui.label.bundle.pricing"/>'>
</div>

<div id="dialog_utility_pricing" title='<spring:message code="ui.label.utility.pricing"/>'>
</div>
<div id="dialog_div_container">
	<div  id="dialog_edit_channel_branding" title='<spring:message code="label.edit.channel.branding"/>' style="display: none">
	</div>
</div>
<div id="entitlements_dialog" title='<spring:message code="ui.label.product.entitlements"/>' style="display:none;"></div>


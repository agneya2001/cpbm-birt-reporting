<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>                
<script type="text/javascript" src="<%=request.getContextPath() %>/js/subscription.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/convertSubscription.js"></script>
<jsp:include page="js_messages.jsp"></jsp:include>

<style>
  .popover {
    max-width: 600px;
    word-wrap: break-word;
  }
</style>

<script language="javascript">

  var dictionary = {  
      terminatesubscription: '<spring:message javaScriptEscape="true" htmlEscape="false" code="label.subscription.details.terminate"/>',
      terminateResource: '<spring:message javaScriptEscape="true" htmlEscape="false" code="label.utility.charge.terminate.resource"/>',
      lightboxterminatesubscription:'<spring:message javaScriptEscape="true" htmlEscape="false" code="message.confirm.terminate.subscription"/>',
      lightboxterminateresource:'<spring:message javaScriptEscape="true" htmlEscape="false" code="message.confirm.terminate.resource"/>',
      cancelsubscription: '<spring:message javaScriptEscape="true" htmlEscape="false" code="label.subscription.details.cancel"/>',
      lightboxcancelsubscription:'<spring:message javaScriptEscape="true" htmlEscape="false" code="message.confirm.cancel.subscription"/>',
      lightboxbuttoncancel: '<spring:message javaScriptEscape="true" htmlEscape="false" code="label.cancel"/>',  
      lightboxbuttonconfirm: '<spring:message javaScriptEscape="true" htmlEscape="false" code="label.confirm"/>',
      terminatingSubscription:'<spring:message javaScriptEscape="true" htmlEscape="false" code="message.terminating.subscription"/>',
      terminatingResource:'<spring:message javaScriptEscape="true" htmlEscape="false" code="message.terminating.resource"/>',
      cancellingSubscription:'<spring:message javaScriptEscape="true" htmlEscape="false" code="message.cancelling.subscription"/>',
      subscriptionEndDate:'<spring:message javaScriptEscape="true" htmlEscape="false" code="label.usage.billing.subscription.charge.service.end"/>',
      cloudServiceException:'<spring:message javaScriptEscape="true" htmlEscape="false" code="exception.cloud.service"/>'
  };

  var con_sub_dictionary = {
      confirmConvertSubscriptionCancel:'<spring:message javaScriptEscape="true" htmlEscape="false" code="confirm.abort.convert.subscription"/>',
      chooseSubscriptionToContinue:'<spring:message javaScriptEscape="true" htmlEscape="false" code="warning.convert.subscription.choose.subscription"/>',
      chooseBundleToContinue:'<spring:message javaScriptEscape="true" htmlEscape="false" code="warning.convert.subscription.choose.bundle"/>',
      convertSubscriptionSuccessNew:'<spring:message javaScriptEscape="true" htmlEscape="false" code="message.convert.subscription.success.new"/>',
      convertSubscriptionSuccessExisting:'<spring:message javaScriptEscape="true" htmlEscape="false" code="message.convert.subscription.success.existing"/>',
      reviewTitleNew:'<spring:message javaScriptEscape="true" htmlEscape="false" code="message.convert.subscription.review.new"/>',
      reviewTitleExisting:'<spring:message javaScriptEscape="true" htmlEscape="false" code="message.convert.subscription.review.existing"/>',
      reviewLabelNew:"<spring:message javaScriptEscape='true' code='label.bundle.name' htmlEscape='false'/>",
      reviewLabelExisting:'<spring:message javaScriptEscape="true" htmlEscape="false" code="subscription"/>',
      newChargeLabel:'<spring:message javaScriptEscape="true" htmlEscape="false" code="label.convert.subscription.new.charge"/>',
      existingChargeLabel:'<spring:message javaScriptEscape="true" htmlEscape="false" code="label.convert.subscription.existing.charge"/>',
      billingUtility:'<spring:message javaScriptEscape="true" htmlEscape="false" code="message.convert.subscription.success.new.billing.first.cycle.utility"/>',
      billingNonUtility:'<spring:message javaScriptEscape="true" htmlEscape="false" code="message.convert.subscription.success.new.billing.first.cycle.nonutility"/>',
      futureBillingRecurring:'<spring:message javaScriptEscape="true" htmlEscape="false" code="message.convert.subscription.success.new.billing.future.cycle"/>',
      subscriptionExistingBilling:'<spring:message javaScriptEscape="true" htmlEscape="false" code="message.convert.subscription.success.existing.billing"/>',
      errorConvertSubscription:'<spring:message javaScriptEscape="true" htmlEscape="false" code="error.convert.subscription.failed"/>'
  };
  
  // Whether the 'Utility Charges' tab is selected
  var utilityTabSelected = false;
  <c:if test="${utility_charges_view}">
  utilityTabSelected = true;
  </c:if>
</script>

<c:if test="${!isPayAsYouGoChosen}">
<div>
  <ul class="widget_detail_navpanel" id="subscription_type_select">
     <li class="widget_detail_navpanel" style="border:none;font-style:italic;font-weight:bold;">
       <spring:message code="label.subscription.type.prefix.for.your" />:
     </li>
     <li class="widget_detail_navpanel" id="bundle_subscriptions">
       <a id="bundle_subscriptions_link" class="subscription_sub_tab <c:if test='${!utility_charges_view}'>on</c:if>" href="javascript:void(0);"><spring:message code="label.subscription.type.purchased.bundles"/></a>
     </li>
     <li class="widget_detail_navpanel last" id="resource_subscriptions">
       <a id="resource_subscriptions_link" class="subscription_sub_tab <c:if test='${utility_charges_view}'>on</c:if>" href="javascript:void(0);"><spring:message code="label.subscription.type.resource.bundles"/></a>
     </li>
  </ul>
       <div id="resource_subscriptions_info_link" style="margin:9px 0 0 2px;cursor:pointer;" href="javascript:void(0);" data-toggle="popover" data-trigger="click" data-placement="right" data-container="body" data-html="true" class="helpicon"></div>
</div>
</c:if>
<c:if test="${isPayAsYouGoChosen}">
	<div>
		<ul class="widget_detail_navpanel" id="subscription_type_select">
			<li class="widget_detail_navpanel"
				style="border: none; font-style: italic; font-weight: bold;"><spring:message code="label.payg.chosen.no.subscriptions"/></li>
		</ul>
	</div>
</c:if>
<div class="widget_box" style="margin-top: 10px;">
  <div class="widget_leftpanel">
    <div class="widget_titlebar">
      <h2 id="list_titlebar"><span id="list_all" class="title_listall_arrow"><spring:message code="label.list.all"/></span></h2>
    </div>
     
    <div class="widget_searchpanel">
      <div id="search_panel" class="widget_searchcontentarea">
        
        <span class="label" id="filters_applied">
          <c:if test="${filtersApplied==0}"><spring:message code="label.no"/> </c:if> 
          <c:if test="${filtersApplied>0}">${filtersApplied} </c:if>
        </span>
        <span class="label js_filter_details_popover" style="text-decoration: underline;margin-left:5px;"><spring:message code="label.filter"/></span>
        <span class="label ellipsis" style="margin-left:5px;width: 100px" title='<spring:message code="label.applied"/>'><spring:message code="label.applied"/></span>
        
        <div id="js_filter_details_popover" style="display: none;">
          <div class="popover_content_container" style="margin-top:50px;">
            <div class="popover_rows">
              <div class="row_contents_title">
                <span>
                  <spring:message code="ui.label.user" />:
                </span>
              </div>
              <div class="row_contents_value">
                <span id="_filter_user"></span>
              </div>
            </div>
            <div class="popover_rows">
              <div class="row_contents_title">
                <span>
                  <spring:message code="ui.label.state" />:
                </span>
              </div>
              <div class="row_contents_value">
                <span id="_filter_state"></span>
              </div>
            </div>
            <div class="popover_rows">
              <div class="row_contents_title">
                <span>
                  <spring:message code="ui.label.instance" />:
                </span>
              </div>
              <div class="row_contents_value">
                <span id="_filter_instance"></span>
              </div>
            </div>
            <c:choose>
              <c:when test="${utility_charges_view}">
                <div class="popover_rows">
                  <div class="row_contents_title">
                    <span><spring:message code="label.resource.type" />:</span>
                  </div>
                  <div class="row_contents_value">
                    <span id="_filter_resourceType"></span>
                  </div>
                </div>
              </c:when>
              <c:otherwise>
                <div class="popover_rows">
                  <div class="row_contents_title">
                    <span><spring:message code="ui.label.product.bundle" />:</span>
                  </div>
                  <div class="row_contents_value">
                    <span id="_filter_bundle"></span>
                  </div>
                </div>
              </c:otherwise>
            </c:choose>
          </div>
        </div>
                              
        <a class="advancesearch_button" id="advancesearchButton" style="float:right"></a>
        <div class="widget_actionpopover advancesearch_dropdown" id="advanceSearchDropdownDiv" style="display: none;">
          <jsp:include page="search.jsp"></jsp:include>
        </div>
        
      </div>
    </div>
    <div class="widget_navigation">
      <ul class="widget_navigationlist" id="grid_row_container">
                          
         <c:choose>
           <c:when test="${empty subscriptions || subscriptions == null}">
            <!--look when there is no list starts here-->
            <li class="widget_navigationlist nonlist" id="non_list">
                <span class="navicon subscription"></span>
                  <div class="widget_navtitlebox">
                    <span class="newlist">             
                      <c:choose>
                        <c:when test="${utility_charges_view}"><spring:message code="message.no.utility.charges.available"/></c:when>
                        <c:otherwise><spring:message code="message.no.subscriptions.available"/></c:otherwise>
                      </c:choose>
                    </span>
                  </div>
              </li>
              <!--look when there is no list ends here-->
      
           </c:when>
          <c:otherwise> 
          <c:forEach items="${subscriptions}" var="subscription" varStatus="status">
            <c:choose>
              <c:when test="${status.index == 0}">
                  <c:set var="firstSubscription" value="${subscription}"/>
                  <c:set var="selected" value="selected"/>
              </c:when>
              <c:otherwise>
                  <c:set var="selected" value=""/>
              </c:otherwise>
            </c:choose> 
            
          <li class='<c:out value="widget_navigationlist ${selected} subscriptions"/>' id="sub<c:out value="${subscription.uuid}" />" onclick="timerFunction($(this))" onmouseover="showInfoBubble(this)" onmouseout="hideInfoBubble(this)">
                            <span class="navicon subscription" id="nav_icon"></span>
                            <div class="widget_navtitlebox">
                              <span class="title">
                               <c:choose>
                                <c:when test="${not empty subscription.handle.resourceName}">
                                  <c:out value="${subscription.handle.resourceName}"/>
                                </c:when>
                                <c:otherwise>
                                  <c:out value="${subscription.uuid}"/>
                                </c:otherwise>
                              </c:choose>
                              </span>
                              
                              <span class="subtitle">
                                <fmt:timeZone value="${currentUser.timeZone}">
                                <spring:message code="dateonly.format" var="dateonly_format"/>  
                                  <fmt:formatDate value="${subscription.activationDate}" pattern="${dateonly_format}"/>                      
                                </fmt:timeZone>
                                <span id="sub_end_date_subtitle" <c:if test="${subscription.state != 'EXPIRED'}">style="display:none;"</c:if>>
                                   -
                                   <fmt:timeZone value="${currentUser.timeZone}">
                                      <spring:message code="dateonly.format" var="dateonly_format"/>  
                                    <fmt:formatDate value="${subscription.terminationDate}" pattern="${dateonly_format}"/>                      
                                    </fmt:timeZone>
                                </span>
                              </span>
                            </div>
                            <c:choose>
                              <c:when test="${subscription.state == 'ACTIVE'}">
                                <c:choose>
                                  <c:when test="${subscription.resourceType != null && (subscription.handle == null || subscription.handle.state == 'ERROR' || subscription.handle.state == 'TERMINATED')}">
                                    <c:set var="status_icon" value="interrupted"/>
                                  </c:when>
                                  <c:otherwise>
                                    <c:set var="status_icon" value="running"/>
                                 </c:otherwise>
                               </c:choose>
                             </c:when>
                             <c:when test="${subscription.state == 'EXPIRED'}">
                                  <c:set var="status_icon" value="stopped"/>
                              </c:when>
                              <c:otherwise>
                                  <c:set var="status_icon" value="nostate"/>
                              </c:otherwise>
                            </c:choose> 
                              <div class="<c:out value="widget_statusicon ${status_icon}" />"></div>
                              <!--Info popover starts here-->
                              <div class="widget_info_popover" id="info_bubble" style="display:none">
                              <div class="popover_wrapper" >
                              <div class="popover_shadow"></div>
                              <div class="popover_contents">
                              <div class="raw_contents">
                                      <div class="raw_content_row">
                                        <div class="raw_contents_title">
                                          <span><spring:message code="label.subscription.details.bundle"/>:</span>
                                        </div>
                                        <div class="raw_contents_value">
                                          <c:if test="${subscription.productBundle == null}">
                                            <spring:message code="launchvm.utility.bundle.name" />
                                          </c:if>
                                          <c:if test="${subscription.productBundle != null}">
                                            <span><c:out value="${subscription.productBundle.name}"/></span>
                                          </c:if>
                                        </div>
                                      </div>
                                      <div class="raw_content_row">
                                        <div class="raw_contents_title">
                                          <span><spring:message code="label.state"/>:</span>
                                        </div>
                                        <div class="raw_contents_value" id = "subscriptionStateDivId">
                                          <span><spring:message code="${subscription.state.code}" /></span>
                                        </div>
                                      </div>
                                      <div class="raw_content_row">
                                        <div class="raw_contents_title">
                                          <span><spring:message code="label.usage.billing.subscription.charge.service.start"/>:</span>
                                        </div>
                                        <div class="raw_contents_value">
                                          <span>
                                            <fmt:timeZone value="${currentUser.timeZone}">
                                              <spring:message code="dateonly.format" var="dateonly_format"/>  
                                              <fmt:formatDate value="${subscription.activationDate}" pattern="${dateonly_format}"/>                      
                                            </fmt:timeZone>
                                          </span>
                                        </div>
                                      </div>
                                      <div class="raw_content_row" <c:if test="${subscription.state != 'EXPIRED'}">style="display:none;"</c:if>>
                                      <div class="raw_contents_title">
                                        <span><spring:message code="label.usage.billing.subscription.charge.service.end"/>:</span>
                                      </div>
                                      <div class="raw_contents_value">
                                        <span id="info_sub_end_date">
                                          <fmt:timeZone value="${currentUser.timeZone}">
                                            <spring:message code="dateonly.format" var="dateonly_format"/>  
                                            <fmt:formatDate value="${subscription.terminationDate}" pattern="${dateonly_format}"/>                      
                                          </fmt:timeZone>
                                        </span>
                                      </div>
                                    </div>
                                </div>
                                  </div>
                                </div>
                                </div>
                                <!--Info popover ends here-->
                            
                           </li>
        </c:forEach>
        
        </c:otherwise>
        </c:choose>
                          

        </ul>
      </div>
      <div class="widget_panelnext">
        <div class="widget_navnextbox">
            <c:choose>
              <c:when test="${current_page <= 1}">
                  <a class="widget_navnext_buttons prev nonactive" href="javascript:void(0);" id="click_previous"><spring:message code="label.previous.short"/></a>
              </c:when>
              <c:otherwise>
                  <a class="widget_navnext_buttons prev" href="javascript:void(0);" id="click_previous" onclick="previousClick()"><spring:message code="label.previous.short"/></a>
              </c:otherwise>
            </c:choose> 
            
            <c:choose>
              <c:when test="${enable_next == true}">
              <a class="widget_navnext_buttons next" href="javascript:void(0);" id="click_next" onclick="nextClick()"><spring:message code="label.next"/></a>
          </c:when>
              <c:otherwise>
              <a class="widget_navnext_buttons next nonactive" href="javascript:void(0);" id="click_next"><spring:message code="label.next"/></a>
                </c:otherwise>
            </c:choose> 
          </div>
      </div>
  </div>
  <div class="widget_rightpanel">
  <div id="viewDetailsDiv" class="subscription_details_container">
  <c:if test="${(empty subscriptions || subscriptions == null)&& current_page == 1}">
    <jsp:include page="/WEB-INF/jsp/tiles/billing/viewsubscription.jsp"></jsp:include>
  </c:if>
  </div>
  <div id="spinning_wheel2" style="display: none;">
  <div class="widget_blackoverlay widget_full_page" style="position:absolute;"></div>
  <div class="widget_loadingbox fullpage" style="position:absolute;">
    <div class="widget_loaderbox">
      <span class="bigloader"></span>
    </div>
    <div class="widget_loadertext">
      <p>
        <spring:message code="label.loading" />
        &hellip;
      </p>
    </div>
  </div>
</div>
  </div>
</div>

<div id="resource_subscription_info_text" style="display:none;"><spring:message htmlEscape="false" code="content.resource.subscription.help.text" /></div>

<input type="hidden" id="filtersApplied"  value="<c:out value="${filtersApplied}"/>"/>
<input type="hidden" id="useruuid"  value="<c:out value="${useruuid}"/>"/>
<input type="hidden" id="instanceuuid"  value="<c:out value="${instanceuuid}"/>"/>
<input type="hidden" id="productBundleID"  value="<c:out value="${productBundleID}"/>"/>
<input type="hidden" id="resourceTypeID"  value="<c:out value="${resourceTypeID}"/>"/>
<input type="hidden" id="stateSelected"  value="<c:out value="${stateSelected}"/>"/>
<input type="hidden" id="current_page"  value="<c:out value="${current_page}"/>"/>
<input type="hidden" id="selected_subs_for_details"  value="<c:out value="${idForDetails}"/>"/>
<input type="hidden" id="tenantParam"  value="<c:out value="${tenant.param}"/>"/>
<input type="hidden" id="states"  value="<c:out value="${states}"/>"/>

<div id="dialog_convert_subscription" title='<spring:message code="ui.label.edit.resource"/>' style="display: none; overflow:hidden;"></div>

<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<style>
  .popover {
    max-width:400px;
    word-wrap: break-word;
  }
</style>

<jsp:include page="/WEB-INF/jsp/tiles/shared/js_messages.jsp"></jsp:include>
<script type="text/javascript">
  var connectorPath = "<%=request.getContextPath()%>/portal/connector";
  var type ="cs";
</script>
<script type="text/javascript">
   var isDelinquent = false;
</script>
<c:if test="${showDelinquent}">
    <sec:authorize access="hasAnyRole('ROLE_ACCOUNT_BILLING_ADMIN', 'ROLE_FINANCE_CRUD')">
      <c:if test="${stateChangeCause == 'PAYMENT_FAILURE'}">
        <script type="text/javascript">
        var isDelinquent = true;
        var showMakePaymentMessage = "<spring:message javaScriptEscape="true" code="message.tenant.delinquent.make.payment"/>";
        </script>
      </c:if>
      <c:if test="${stateChangeCause != 'PAYMENT_FAILURE'}">
        <script type="text/javascript">
        var isDelinquent = true;
        var showMakePaymentMessage = "<spring:message javaScriptEscape="true" code="message.tenant.delinquent.admin.contact.support"/>";
        </script>
      </c:if>
      <c:if test="${effectiveTenant.state=='TERMINATED'}">
        <script type="text/javascript">
        var isDelinquent = true;
        var showMakePaymentMessage = "<spring:message javaScriptEscape="true" code="message.tenant.account.state.terminated"/>";
        </script>
      </c:if>

    </sec:authorize>
    <sec:authorize access="!hasAnyRole('ROLE_ACCOUNT_CRUD', 'ROLE_ACCOUNT_ADMIN')">
      <script type="text/javascript">
        var isDelinquent = true;
      </script>
    </sec:authorize>
</c:if>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/csconnector.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/validator.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/tasks.js"></script>
<script type="text/javascript">
if( typeof i18n === 'undefined' ) {
  var i18n = {};
}
if( typeof i18n.errors === 'undefined' ) {
  i18n.errors = {};
}

i18n.errors.config = {
  valueRequired: '<spring:message javaScriptEscape="true" code="js.errors.config.value.required"/>'
};
var dictionary = {
    toggleButtonEnable:'<spring:message javaScriptEscape="true" code="label.configuration.integration.togglebutton.enable"/>',
    noSettingsFound:'<spring:message javaScriptEscape="true" code="message.no.settings.found"/>',
    autoProvisioningEnabled:'<spring:message javaScriptEscape="true" code="ui.message.autoprovision.enabled"/>',
    _true:'<spring:message javaScriptEscape="true" code="label.true"/>',
    _false:'<spring:message javaScriptEscape="true" code="label.false"/>',
    autoProvisioningError:'<spring:message javaScriptEscape="true" code="ui.message.autoprovision.error"/>'
};
</script>
<c:set var="is_admin" value="false"/>
<sec:authorize  access="hasRole('ROLE_ACCOUNT_ADMIN')">
    <c:set var="is_admin" value="true"/>
</sec:authorize>
<c:set var="auth_enable_service" value="false"/>
<sec:authorize  access="hasAnyRole('ROLE_ACCOUNT_MGMT','ROLE_ACCOUNT_USER_CRUD')">
    <c:set var="auth_enable_service" value="true"/>
</sec:authorize>
<c:set var="apostropy" value="'"/>
<c:set var="escapedeapostropy" value="\\'"/>
<c:choose>
    <c:when test="${(is_admin || spUserHasCloudServiceAccount || userHasCloudServiceAccount) && !iframe_view && not empty serviceInstanceMap}">
    <div id="manage_services_info">
        <div class="page_heading">
            <h3><spring:message code="message.myservices.header"/></h3>
        </div>
        <div class="page_heading_description" >
            <c:choose>
                <c:when test="${is_admin}">
                    <spring:message code="message.myservices.admin.user.header.description"/>
                </c:when>
                <c:otherwise>
                    <spring:message code="message.myservices.normal.user.header.description"/>
                </c:otherwise>
            </c:choose>
        </div>
      </div>
    <div class="widgetcatalog_contentarea" style="display: block;">
        <div id="myServicesDiv" class="service_listpanel" style="display: block;">
        <jsp:include page="/WEB-INF/jsp/tiles/main/cs_instance_left_menu.jsp"></jsp:include>
        
     
        <input type="hidden" id="selectedcategory" value="All"/>
        
        <c:forEach items="${serviceInstanceMap}" var="serviceInstanceEntry" varStatus="status">
        <c:if test="${(serviceInstanceEntry.value == true) || auth_enable_service}">
          <div class="servicelist mainbox" category="${serviceInstanceEntry.key.service.category}" serviceid="${serviceInstanceEntry.key.service.uuid}">
            <div class="servicelist sections col6" style="width:470px;min-height:88px;">
            <c:choose>
                <c:when test="${not empty serviceInstanceEntry.key.imagePath}">
                  <span class="logobox">
                    <img src="/portal/portal/logo/serviceInstance/${serviceInstanceEntry.key.uuid}"/>
                  </span>
                </c:when>
                <c:otherwise>
                  <span class="logobox">
                    <img src="/portal/portal/logo/connector/${serviceInstanceEntry.key.service.uuid}/logo"/>
                  </span>
                </c:otherwise>
              </c:choose>              

              <div class="servicelist sections col7">
                <ul>
                  <li>
                    <h3 class="ellipsis" style="width:100%;" title="<c:out value="${serviceInstanceEntry.key.name}"/>">
                      <c:out value="${serviceInstanceEntry.key.name}"/>
                    </h3>
                  </li>
                  <li>
                      <c:choose>
                          <c:when test="${fn:length(serviceInstanceEntry.key.description) > 120}">
                              <div id="stripped_content_${serviceInstanceEntry.key.uuid}" style="word-wrap:break-word;">
                                  <c:out value="${fn:substring(serviceInstanceEntry.key.description, 0, 116)}"/> &hellip;
                              </div>
                              <div class="hidden_content">
                                  <div id="learn_more_content_${serviceInstanceEntry.key.uuid}" style="display:none;word-wrap:break-word;">
                                      <c:out value="${serviceInstanceEntry.key.description}"/>
                                  </div>
                                  <a class="more_info more_down learn_more_link" id="learn_more_link_${serviceInstanceEntry.key.uuid}" href="javascript:void(0);"><spring:message  code="label.learn.more" /></a>
                              </div>
                          </c:when>
                          <c:otherwise>
                              <c:out value="${serviceInstanceEntry.key.description}"/>
                          </c:otherwise>
                      </c:choose>
                  </li>
                  <li>
                      <a href="javascript:void(0);" id="uRates_${serviceInstanceEntry.key.uuid}" class="utility_rates_link" style="padding-top: 2px;"><spring:message code="view.utility.rates"/></a>
                  </li>  
                                           
                </ul>
              </div>
            </div>
            <div class="servicelist sections col3 no_border" style="width: 195px;">
             
              <c:set var="instanceName" value="${serviceInstanceEntry.key.name}"/>
              <c:set var="escapedInstanceName" value="${fn:replace(instanceName,apostropy,escapedeapostropy)}" /> 
             <c:choose>
                  <c:when test="${serviceInstanceEntry.value == true && serviceInstanceHandles[serviceInstanceEntry.key].state == 'ACTIVE'}">
                      <div class="link_wrapper" >
                        <c:if test="${userHasCloudServiceAccount}">
                            <a id="${serviceInstanceEntry.key.uuid}" class="vertical_stacked_links button_manage_service"  href="javascript:void(0);" style="padding-top: 2px;">
                             <spring:message  code="service.connectorlist.manage" />
                            </a>
                            
                            <sec:authorize access="hasAnyRole('ROLE_ACCOUNT_MGMT','ROLE_ACCOUNT_USER_CRUD')">
                              <c:if test="${enableServiceSupportedMap[serviceInstanceEntry.key.service.uuid] eq true}">
                                <a id="${serviceInstanceEntry.key.uuid}" enabled="${serviceInstanceProvisioningMap[serviceInstanceEntry.key.uuid]}" class="vertical_stacked_links button_manage_user_provisioning"  href="javascript:void(0);" style="padding-top: 2px;">
                                  <spring:message  code="label.manage.user.provisioning"/>
                                </a>
                              </c:if>
                            </sec:authorize>
                            <a href="javascript:void(0);" id="subscribe_${serviceInstanceEntry.key.uuid}" class="vertical_stacked_links subscibe_to_bundles_link"  style="padding-top: 2px;">
                            <c:choose>
                                <c:when test="${!payAsYouGoMode}">
                                    <spring:message code="label.catalog.subscribe.to.bundles"/>
                                </c:when>
                                <c:otherwise>
                                    <spring:message code="subscribe"/>
                                </c:otherwise>
                            </c:choose>
                            </a>
                        </c:if>
                        <c:if test="${userHasCloudServiceAccount && currentUser.id == effectiveUser.id}">
                           <a class="vertical_stacked_links" href="javascript:resolveViewForSettingFromServiceInstance2('${serviceInstanceEntry.key.uuid}');" style="padding-top: 2px;"> <spring:message code="message.myprofile.mysettings"/> </a>
                        </c:if> 
                        <c:if test="${serviceInstanceViewMap[serviceInstanceEntry.key] == true && is_admin && effectiveTenant.id == currentTenant.id}">
                            <a id="${serviceInstanceEntry.key.uuid}" class="vertical_stacked_links" href="javascript:resolveViewForAccountSettingFromServiceInstance('${serviceInstanceEntry.key.uuid}','${currentTenant.param}','${escapedInstanceName}');" style="padding-top: 2px;">
                                <spring:message  code="service.connectorlist.companysettings" />
                            </a>
                        </c:if>
                      </div>
                  </c:when>
                  <c:when test="${auth_enable_service && effectiveTenant.state.name == 'ACTIVE'}">
                    <c:choose>
                      <c:when test="${empty serviceInstanceHandles[serviceInstanceEntry.key] && empty serviceInstanceWfMap[serviceInstanceEntry.key]['hasWfInRunning'] && empty serviceInstanceWfMap[serviceInstanceEntry.key]['hasWfInSuccess']}">
                        <div class="button_wrapper">
                          <a id="${serviceInstanceEntry.key.uuid}" class="add_button active"
                            href="javascript:resolveViewForSettingFromServiceInstance('${serviceInstanceEntry.key.uuid}','${currentTenant.param}','${escapedInstanceName}');">
                            <spring:message code="service.connectorlist.signup" />
                          </a>
                        </div>
                     <div class="link_wrapper" >
                     </div>
                      </c:when>
						<c:when test="${empty serviceInstanceHandles[serviceInstanceEntry.key] && (not empty serviceInstanceWfMap[serviceInstanceEntry.key]['hasWfInRunning'] || not empty serviceInstanceWfMap[serviceInstanceEntry.key]['hasWfInSuccess'])}">
							<div class="button_wrapper vertical_stacked_links">
								<spring:message code="label.cloud.service.instance.activation.processing"/>
							</div>
							<c:if test="${ not empty serviceInstanceWfMap[serviceInstanceEntry.key]['workflowId']}">
								<div class="link_wrapper">
									<a href="javascript:void(0);"
										id="workflowdetails${serviceInstanceWfMap[serviceInstanceEntry.key]['workflowId']}"
										class="workflowDetailsPopup vertical_stacked_links"><spring:message
											code="label.cloud.service.instance.workflow.state" /></a>
								</div>
							</c:if>
						</c:when>
						<c:when test="${serviceInstanceHandles[serviceInstanceEntry.key].state == 'ERROR'}">
                        <div class="button_wrapper">
                          <a id="${serviceInstanceEntry.key.uuid}" class="add_button active"
                            href="javascript:resolveViewForSettingFromServiceInstance('${serviceInstanceEntry.key.uuid}','${currentTenant.param}','${escapedInstanceName}');">
                            <spring:message code="service.connectorlist.retry" />
                          </a>
                          <a class="vertical_stacked_links js_account_registration_error" style="cursor:pointer; padding-top: 8px; color: red;" data-toggle="popover" data-trigger="hover" data-placement="bottom" data-container="body" data-content="<spring:message javaScriptEscape="true" text="${serviceInstanceHandles[serviceInstanceEntry.key].data}"/>"> <spring:message code="service.connectorlist.retry.error.details"/></a>
                        </div>
                      </c:when>
                      <c:when test="${serviceInstanceHandles[serviceInstanceEntry.key].state == 'PROVISIONING'}">
                        <div class="vertical_stacked_links js_provisioning_service" serviceInstanceUuid="${serviceInstanceEntry.key.uuid}" style="padding-top:25px;width:70px;padding-left:60px;">
                          <span><spring:message code="label.enabling" /></span>
                          <span class="maindetails_footer_loadingicon" style="float:right;margin:0;"></span>
                        </div>
                      </c:when>
                    </c:choose>
                  </c:when>               
		            <c:when test="${auth_enable_service}">
                    <div class="button_wrapper" >
	                  <a id="${serviceInstanceEntry.key.uuid}" class="add_button nonactive" href="javascript:void(0);"> 
	                    <spring:message code="service.connectorlist.signup"/>
	                  </a>
                     </div>
		            </c:when>
                        
                </c:choose>
          
            </div>
          </div>
          </c:if>
        </c:forEach>
      </div>
      
       <div id="serviceAccountConfigDiv" style="margin:0; display:none;">     
          <div>
          <h3 id="selectedInstanceH1"></h3>
            <iframe id="serviceAccountConfigViewFrame" width="940px" height="700px" frameborder="0"></iframe>
          </div>
        </div>
      
    
      <div id="spinning_wheel" style="display: none;">
        <div class="widget_blackoverlay widget_rightpanel"
          style="position: fixed; height: 100%"></div>
        <div class="widget_loadingbox fullpage" style="position: fixed;">
          <div class="widget_loaderbox">
            <span class="bigloader"></span>
          </div>
          <div class="widget_loadertext">
            <p id="in_process_text">
              <spring:message code="label.loading" />
            </p>
          </div>
        </div>
      </div>
    
      <div class="service_detailpanel" style="display: none;"></div>
      
      <div id="dialog_service_details" title='<spring:message code="label.service.details"/>' style="display: none"></div>
    
      <div id="dialog_enable_service" title='<spring:message code="label.enable.service"/>' style="display: none; overflow:hidden;"></div>
      
        <div id="userSubscribedServiceDetails" style="margin:0; display:none;">
            <span id="backToSubscribedServiceListing" class="title_listall_arrow biggerback" style="float:right;"><spring:message javaScriptEscape="true" code="label.backtolisting"/></span>
            <div>
                <iframe id="userOrAccountSettingsViewFrame" width="940px" height="700px" frameborder="0"></iframe>
            </div>
        </div>
        
    
    </div>
    </c:when>
    <c:when test="${(is_admin || spUserHasCloudServiceAccount || userHasCloudServiceAccount) && iframe_view}">
      <script type="text/javascript">
        var iframe_view = true;
        var service_instance_uuid_for_iframe = '<c:out value="${serviceInstanceUUID}"/>';
      </script>
    </c:when>
    <c:otherwise>
        <div class="subscribe_dialog_message_box error">
            <p><spring:message code='no.cloud.service.account'/></p>
        </div>
    </c:otherwise>
</c:choose>
<div class="workflow_details_popup" title="<spring:message code="dialogue.title.workflow" />" style="display:none"></div>

<!-- wizard for changing auto provisioning flag -->
<div id="manage_user_provisioning_popup" title="<spring:message code="label.manage.user.provisioning"/>" style="display: none">
  <div class="j_cloudservicepopup">
    <div class="widgetwizard_contentarea sixstepswizard" style="height: 380px;">
      <div class="widgetwizard_boxes fullheight sixstepswizard" style="height: 320px; margin-top: 30px;" id="_stepOfEnableServiceUser">
        <div class="widgetwizard_titleboxes" style="width: 800px;">
          <h2>
            <spring:message code="label.service.users.autoprovision" htmlEscape="false"></spring:message>
          </h2>
          <br>
          <hr>
          <span>
            <spring:message code="ui.service.enable.autoprovisioning.user.desc" />
          </span>
          <div>
            <ul style="list-style-type: none">
              <li style="padding: 0; margin: 20px 0 0 3px; width: 300px;">
                <span style="line-height: 20px;">
                  <input type="radio" style="width: 20px; margin-top: 0px;" id="enable_no" name="_enableAllUsers" checked="checked" value="false" />
                  <spring:message code="label.no" />
                </span>
                <span style="line-height: 20px;">
                  <input type="radio" style="width: 20px; margin-top: 0px;" id="enable_yes" name="_enableAllUsers" value="true" />
                  <spring:message code="label.yes" />
                </span>
                <input type="hidden" id="_currentServiceInstanceUUID">
              </li>
            </ul>
          </div>
        </div>
      </div>

      <div class="widgetwizard_boxes fullheight sixstepswizard" style="height: 320px; margin-top: 30px;" id="final_step">
        <div class="widgetwizard_titleboxes" style="width: 800px;">
          <div class="widgetwizard_successbox">
            <div class="widgetwizard_successbox">
              <div class="widget_resulticon success"></div>
              <p id="successmessage">
                <spring:message code="ui.message.autoprovision.enabled" />
              </p>
              <p>
                <a href="/portal/portal/connector/csinstances?tenant=${effectiveUser.tenant.uuid}"><spring:message code="ui.message.goto.services" /></a>
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <div id="spinning_wheel" style="display: none">
    <div class="widget_blackoverlay " style="width: 836px; height: 330px; margin: 45px 0px 0px 30px;"></div>
    <div class="widget_loadingbox " style="top: 235px;">
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
<!-- wizard for changing auto provisioning flag ends-->

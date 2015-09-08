<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<link rel="stylesheet" href="<%=request.getContextPath() %>/css/jslider.css" type="text/css">
<link rel="stylesheet" href="<%=request.getContextPath() %>/css/jslider.plastic.css" type="text/css">
<link rel="stylesheet" href="<%=request.getContextPath() %>/css/customproperties.css" type="text/css">
<script type="text/javascript" src="<%=request.getContextPath() %>/js/createSubscription.js"></script>

<jsp:include page="/WEB-INF/jsp/tiles/shared/js_messages.jsp"></jsp:include>

<script type="text/javascript">
var subscriptionButtonVisiblity = true;
    var dictionary = {    
        onlyAlphanumericCharactersAreAllowed: '<spring:message javaScriptEscape="true" code="only.alphanumeric.characters.are.allowed"/>',
        oneTimeChargeType: '<spring:message javaScriptEscape="true" code="launchvm.chargetype.text.onetime"/>',
        unlimited:'<spring:message javaScriptEscape="true" code="label.catalog.Unlimited.Usage"/>',
        more:'<spring:message javaScriptEscape="true" code="label.more"/>',
        goToSubscriptions:'<spring:message javaScriptEscape="true" code="label.go.to.subscriptions"/>',
        goToMyResources:'<spring:message javaScriptEscape="true" code="go.to.my.resources"/>',
        goToCatalog:'<spring:message javaScriptEscape="true" code="go.to.catalog"/>',
        subscriptionSuccess:'<spring:message javaScriptEscape="true" code="message.subscription.creation.success"/>',
        cloudSubscriptionSuccess:'<spring:message javaScriptEscape="true" code="message.cloud.subscription.creation.success"/>',
        subscriptionReconfigured:'<spring:message javaScriptEscape="true" code="message.subscription.reconfigure.success"/>',
        subscriptionProvisioned:'<spring:message javaScriptEscape="true" code="message.subscription.provision.success"/>',
        subscriptionFailure:'<spring:message javaScriptEscape="true" code="message.subscription.creation.failure"/>',
        subscriptionReconfiguredFailure:'<spring:message javaScriptEscape="true" code="message.subscription.reconfigure.failure"/>',
        subscriptionProvisionedFailure:'<spring:message javaScriptEscape="true" code="message.subscription.provision.failure"/>',
        goToDashboard:'<spring:message javaScriptEscape="true" code="go.to.dashboard"/>',
        of:'<spring:message javaScriptEscape="true" code="label.subscribe.select.of"/>',
        all:'<spring:message javaScriptEscape="true" code="label.all"/>',
        label_Any:'<spring:message javaScriptEscape="true" code="label.Any"/>',
        label_Bundle:'<spring:message javaScriptEscape="true" code="label.bundle"/>',
        label_None:'<spring:message javaScriptEscape="true" code="label.none"/>',
        error_Group_Not_Satisfied:'<spring:message javaScriptEscape="true" code="error.Resource.Provision.Group.Not.Satisfied"/>',
        label_charged_at:'<spring:message javaScriptEscape="true" code="label.charged.at"/>',
        msg_no_extra_charge_upto:'<spring:message javaScriptEscape="true" code="label.no.extra.charge.up.to"/>',
        msg_overages_charged_at:'<spring:message javaScriptEscape="true" code="label.overages.charged.at"/>',
        msg_overages_charges:'<spring:message javaScriptEscape="true" code="label.overages.charges"/>',
        label_Not_Applicable:'<spring:message javaScriptEscape="true" code="label.Not.Applicable"/>',
        label_na:'<spring:message javaScriptEscape="true" code="ui.label.na" />',
        label_Reconfigure:'<spring:message javaScriptEscape="true" code="ui.label.subscription.Reconfigure" />',
        view_details:'<spring:message javaScriptEscape="true" code="label.view.details" />',
        view_utility_pricing:'<spring:message javaScriptEscape="true" code="view.utility.rates" />',
        label_Using:'<spring:message javaScriptEscape="true" code="label.catalog.Using" />',
        label_no_entitlements:'<spring:message javaScriptEscape="true" code="message.bundle.details.dialog.no.entitlements" />',
        error_select_required_rcs:'<spring:message javaScriptEscape="true" code="error.catalog.Select.missing.components" />',
        label_configure:'<spring:message javaScriptEscape="true" code="label.configure" />',
        label_back_to_catalog:'<spring:message javaScriptEscape="true" code="label.subscribe.backtocatalog" />',
        msg_no_values_for_required_components:'<spring:message javaScriptEscape="true" code="message.no.values.for.required.components" />',
        msg_any_required_component:'<spring:message javaScriptEscape="true" code="message.catalog.any.resource.component" />',
        cloud_service_down:'<spring:message javaScriptEscape="true" code="cloud.service.down" />',
        eff_date_not_yet_set:'<spring:message javaScriptEscape="true" code="ui.label.plan.date.not.yet.set" />',
        error_details:'<spring:message javaScriptEscape="true" code="label.error.details" />',
        labelMonthlyNoRecurring:'<spring:message javaScriptEscape="true" code="label.catalog.monthly.no.recurring" />',
        messageMonthlyNoRecurring:'<spring:message javaScriptEscape="true" htmlEscape="false" code="message.catalog.monthly.no.recurring" />'
    };

    var l10dict = new Array();
    l10dict['currentBundle']='<spring:message javaScriptEscape="true" code="subscription.current.bundle"/>';
    l10dict['subscriptionBundles']='<spring:message javaScriptEscape="true" code="subscription.current.subscriptions"/>';
    l10dict['Provision']='<spring:message javaScriptEscape="true" code="ui.label.provision"/>';
    l10dict['Incompatible']='<spring:message javaScriptEscape="true" code="ui.label.incompatible"/>';
    
    var uniqueResourceComponents = [];
    var uniqueResourceComponentsDescl10dict = [];
    var uniqueResourceComponentsKey = [];
    <c:if test="${not empty uniqueResourceComponentNames}">
      <c:forEach items="${uniqueResourceComponentNames}" var="uniqueResourceComponentName">
        l10dict['${uniqueResourceComponentName}' + '-name']='<spring:message javaScriptEscape="true" code="${service.serviceName}.ResourceType.${resourceType}.${uniqueResourceComponentName}.name"/>';
        l10dict['${uniqueResourceComponentName}' + '-desc']='<spring:message javaScriptEscape="true" code="${service.serviceName}.ResourceType.${resourceType}.${uniqueResourceComponentName}.desc"/>';
        uniqueResourceComponents.push('${uniqueResourceComponentName}');
        uniqueResourceComponentsDescl10dict.push('<spring:message javaScriptEscape="true" code="${service.serviceName}.ResourceType.${resourceType}.${uniqueResourceComponentName}.detail.desc"/>');
        uniqueResourceComponentsKey.push('${service.serviceName}' + '.ResourceType.' + '${resourceType}'+'.' + '${uniqueResourceComponentName}'+'.detail.desc');
      </c:forEach>
    </c:if>
    
    var serviceFilterNames = [];
    var serviceFilterDescl10dict = [];
    var serviceFilterDescKey = [];
    <c:if test="${not empty service.serviceFilters && resourceType != '__SERVICE__'}">
      <c:forEach items="${service.serviceFilters}" var="filter" varStatus="status">
        l10dict['${filter.discriminatorName}' + '-name']='<spring:message javaScriptEscape="true" code="${service.serviceName}.Filter.${filter.discriminatorName}.name"/>';
        l10dict['${filter.discriminatorName}' + '-desc']='<spring:message javaScriptEscape="true" code="${service.serviceName}.Filter.${filter.discriminatorName}.description"/>';
        serviceFilterNames.push('${filter.discriminatorName}');
        serviceFilterDescl10dict.push('<spring:message javaScriptEscape="true" code="${service.serviceName}.Filter.${filter.discriminatorName}.detail.desc"/>');
        serviceFilterDescKey.push('${service.serviceName}' + '.Filter.' + '${filter.discriminatorName}'+'.detail.desc');
      </c:forEach>
    </c:if>
    
    var reconfigurableMap = {};
    <c:if test="${not empty resourceComponents}">
      <c:forEach items="${resourceComponents}" var="serviceResourceGroupComponent">
        reconfigurableMap['${serviceResourceGroupComponent.resourceComponentName}'] = '${serviceResourceGroupComponent.reconfigurable}';
      </c:forEach>
    </c:if>
    
    var resourceProperties = [];
    <c:if test="${not empty resourceProperties}">
      <c:forEach items="${resourceProperties}" var="resourceProperty">
        l10dict['${resourceProperty.name}' + '-name'] = '<spring:message javaScriptEscape="true" htmlEscape="false" code="${service.serviceName}.ResourceType.${resourceType}.${resourceProperty.name}.name"/>';
        l10dict['${resourceProperty.name}' + '-desc'] = '<spring:message javaScriptEscape="true" htmlEscape="false" code="${service.serviceName}.ResourceType.${resourceType}.${resourceProperty.name}.description"/>';
        resourceProperties.push('${resourceProperty.name}');
      </c:forEach>
    </c:if>
    
    var allConfigurationProperties = [];
    $.merge(allConfigurationProperties, serviceFilterNames);
    $.merge(allConfigurationProperties, uniqueResourceComponents);
    $.merge(allConfigurationProperties, resourceProperties);
    
    var pricingReccurenceFrequencyList = {};
    <c:forEach items="${chargeRecurrenceFrequencyList}" var="chargeType">
    pricingReccurenceFrequencyList['${chargeType.name}'] = 0;
    </c:forEach>
</script>

<sec:authorize access="hasAnyRole('ROLE_ACCOUNT_CRUD','ROLE_ACCOUNT_MGMT')">
    <sec:authorize ifNotGranted="ROLE_FINANCE_CRUD">
    <script type="text/javascript">
         var   subscriptionButtonVisiblity = false;
          </script>
</sec:authorize>
</sec:authorize>

<c:if test="${not empty subscription && subscription.activeHandle == null}">
  <c:set var="isReprovision" value="true" scope="request"></c:set>
</c:if>

<c:if test="${not empty subscription && subscription.activeHandle != null}">
  <c:set var="isReconfigure" value="true" scope="request"></c:set>
</c:if>


<div class="sliding_statswrapper"><jsp:include page="/WEB-INF/jsp/tiles/main/serviceCategoryList.jsp" /></div>
<!--catalog list will starts here-->
<c:choose>
  <c:when test="${not empty cloudServiceException && userHasCloudServiceAccount}">
      <div style="width: 95%;float:left;" class="alert alert-error">
        <spring:message code="cloud.service.down" />
      </div>
  </c:when>
  <c:otherwise>
    <div id="catalog_content_area" class="widgetcatalog_contentarea">
      <div id="componentselector" class="row">

        <div id="SECTION_1" style="display: none;">
          <tiles:insertDefinition name="subscribe.PAYG" />
        </div>

        <div id="SECTION_2" style="display: none;" class="catalog_span_narrow pull-left">
          <tiles:insertDefinition name="subscribe.bundle.LHS" />
        </div>
        <div class="catalog_span_wide pull-right" id="SECTION_3" style="display: none;">

          <!-- Listing Pricing Filters Start -->
          <div class="row">
            <div class="navbar pull-right" id="pricing">
              <div class="pull-left" style="padding:10px;font-size:12px;">
                <spring:message code="label.subscribe.pricing" />
                :
              </div>
              <div class="navbar-inner pull-right bs_catalog_pricing_nav_bar">
                <ul id="pricing_filters" class="nav">
                  <li class="active"><a href="javascript:void(0)" id="ALL"><spring:message code="label.all"/></a></li>
                  <c:forEach items="${chargeRecurrenceFrequencyList}" var="chargeType">
                    <c:if test="${chargeType.name != 'NONE'}">
                      <c:set var="chargeReccurrenceCode" value="charge.type.${chargeType.name}"></c:set>
                      <c:set var="chargeReccurrenceDescCode" value="charge.type.${chargeType.name}.description"></c:set>
                      <spring:message code="${chargeReccurrenceCode}" var="chargeReccurenceDisplay" />
                      <spring:message code="${chargeReccurrenceDescCode}" var="chargeReccurenceDescDisplay" />
                      <c:if test="${chargeReccurenceDescDisplay eq chargeReccurrenceDescCode}">
                        <c:if test="${chargeReccurenceDisplay eq chargeReccurrenceCode}">
                          <c:set var="chargeReccurenceDescDisplay" value="${chargeType.displayName}" />
                        </c:if>
                        <c:if test="${chargeReccurenceDisplay ne chargeReccurrenceCode}">
                          <c:set var="chargeReccurenceDescDisplay" value="${chargeReccurenceDisplay}" />
                        </c:if>
                      </c:if>
                      <li><a href="javascript:void(0)" id="<c:out value="${chargeType.name}"/>"
                        desc="${chargeReccurenceDescDisplay}"> <!-- If property is given in Application Resources take from there or the display name from db -->
                          <c:if test="${chargeReccurenceDisplay eq chargeReccurrenceCode}">
                            <c:out value="${chargeType.displayName}" />
                          </c:if> <c:if test="${chargeReccurenceDisplay ne chargeReccurrenceCode}">
                            <c:out value="${chargeReccurenceDisplay}" />
                          </c:if>
                          &nbsp;(<span class="js_filter_count">0</span>)
                      </a></li>
                    </c:if>
                  </c:forEach>
                </ul>
              </div>
            </div>
          </div>
          <!-- Listing Pricing Filters End -->
          <tiles:insertDefinition name="resource.provision.configuration" />
          <tiles:insertDefinition name="list.bundles.container" />
        </div>
        <div id="SECTION_4" style="display: none;">
          <tiles:insertDefinition name="provision.resource.RHS" />
        </div>
      </div>
    </div>
  </c:otherwise>
</c:choose>



<!-- Component UI Template to clone from -->
<div class="filterlistbox" style="display: none;float:left;width:100%;" id="list_box_container" >
  <div class="bs_selectbox title user">
      <h2 id="filterBoxTitle" class="ellipsis" style="width:95%;"></h2>
    </div>
  <div id="filterBoxSelection" class="catalogfilter_filterlist templates">
  </div>
</div>
<!-- Component UI Template to clone from -->

<!-- List RC Element to clone from Start-->
<li id="list_rc_element_clone" style="display:none;">
  <span id="span_rc_name" class="catalog_rc_list ellipsis js_displaytext"></span>
  <span id="span_rc_desc" class="catalog_rc_list description ellipsis"></span>
</li>
<!-- List RC Element to clone from End -->

<!-- Hidden fields for filters, components and properties Start -->

<c:if test="${not empty service.serviceFilters && resourceType != '__SERVICE__'}">
  <c:forEach items="${service.serviceFilters}" var="service_filter" varStatus="status">
    <input type="hidden" id="conf_prop_${service_filter.discriminatorName}" name="${service_filter.discriminatorName}" />
  </c:forEach>
</c:if>

<c:if test="${not empty uniqueResourceComponentNames}">
  <c:forEach items="${uniqueResourceComponentNames}" var="uniqueResourceComponentName">
    <input type="hidden" id="conf_prop_${uniqueResourceComponentName}" name="${uniqueResourceComponentName}" />
  </c:forEach>
</c:if>

<c:if test="${not empty resourceProperties}">
  <c:forEach items="${resourceProperties}" var="resourceProperty">
    <input type="hidden" id="conf_prop_${resourceProperty.name}" name="${resourceProperty.name}" />
  </c:forEach>
</c:if>

<!-- Hidden fields for filters, components and properties End -->

<!-- /Hidden fields for component names -->
<input type="hidden" id="selectedCurrencySign" value="${tenant.currency.sign}" />
<input type="hidden" name="resourceType" id="resourceType" value="${resourceType}" />
<input type="hidden" name="serviceInstanceUuid" id="serviceInstanceUuid" value="${serviceInstanceUuid}" />
<input type="hidden" name="isPayAsYouGoChosen" id="isPayAsYouGoChosen" value="${isPayAsYouGoChosen}" />
<input type="hidden" name="tenantParam" id="tenantParam" value='<c:out value="${tenant.param}"/>' />
<input type="hidden" name="customComponentSelector" id="customComponentSelector" value="${customComponentSelector}" />
<input type="hidden" name="customEditorTag" id="customEditorTag" value="${customEditorTag}" />
<input type="hidden" name="tenantDataJsonStr" id="tenantDataJsonStr" value='${tenantDataJsonStr}' />
<input type="hidden" name="cloudServiceException" id="cloudServiceException" value="${cloudServiceException}" />
<input type="hidden" id="channelParam" name="channelParam" value="<c:out value="${channel.param}"/>" />
<input type="hidden" id="minFractionDigits" name="minFractionDigits" value="<c:out value="${minFractionDigits}" />" />
<c:if test="${not empty subscription}">
  <input type="hidden" name="subscriptionId" id="subscriptionId" value="<c:out value="${subscription.id}" />" />
  <input type="hidden" name="subscriptionUuid" id="subscriptionUuid" value="<c:out value="${subscription.uuid}" />" />
  <input type="hidden" name="subscriptionConfJson" id="subscriptionConfJson"
    value="<c:out value="${configurationData}" />" />
  <c:if test="${subscription.activeHandle != null}">
    <input type="hidden" name="isReconfigure" id="isReconfigure" value="true" />
  </c:if>
  <input type="hidden" name="subscriptionUserDataJsonStr" id="subscriptionUserDataJsonStr" value="<c:out value="${subscriptionUserDataJsonStr}" />" />
</c:if>
<input type="hidden" id="selectedCategory" value="<c:out value="${selectedCategory}"/>" />
<input type="hidden" id="viewChannelCatalog" value="${viewChannelCatalog}">

<div id="spinning_wheel2" style="display: none;">
  <div class="widget_blackoverlay widget_full_page"></div>
  <div class="widget_loadingbox fullpage">
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

<c:set var="title_arg_1">
  <spring:message code="${service.serviceName}.ResourceType.${resourceType}.name" />
</c:set>
<c:if test="${resourceType == serviceBundleResourceType}">
  <c:set var="title_arg_1">
    <spring:message code="catalog.resource.type.selection.service.bundle"/>
  </c:set>
</c:if>
<div id="launch_vm_dialog" title='<spring:message code="label.subscribing.to" arguments="${title_arg_1}"/>'
  style="display: none">

  <div class="catalogconfigure_sectionox_contentarea">
    <div id="resource_status_icon" class="widgetconfigure_softwarebox">
      <i id="resource_status_loading" class="fa fa-spinner fa-spin fa-5x" style="display:none;margin:40px;"></i>    
    </div>
    <div class="widgetconfigure_softwarebox textarea" id="launch_vm_progress" style="width: 70%; margin-left: 30px;">
      <div id="launchingVm_template" class="subscribe_dialog_message_box" style="display: none;">

        <p id="message2" style="margin: 8px 0px 0px 5px;"></p>
        <a id="resource_error" style="display:none; padding: 5px">(<spring:message code="handle.label.data"/>)</a>
      </div>
      <p id="in_progress_message2">
        <spring:message code="launching.subscribe.launch.vm.in.process.part2" htmlEscape="false" />
      </p>
    </div>
  </div>
</div>

<div id="spinning_wheel" style="display: none;">
  <div class="widget_blackoverlay widget_full_page"></div>
  <div class="widget_loadingbox fullpage">
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

<div id="tncDialog" title='<spring:message code="js.errors.register.tncDialog.title"/>' style="display:none;padding:10px 10px 10px 20px;">
    <c:out value="${tnc}" escapeXml="false" />
</div>
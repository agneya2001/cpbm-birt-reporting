<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<script language="javascript">
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
    configurationEnabled: '<spring:message javaScriptEscape="true" code="message.configuration.setting.enabled"/>',
    configurationDisabled: '<spring:message javaScriptEscape="true" code="message.configuration.setting.disabled"/>',
    configurationEnableFailure: '<spring:message javaScriptEscape="true" code="message.configuration.setting.enable.failure"/>',
    configurationDisableFailure: '<spring:message javaScriptEscape="true" code="message.configuration.setting.disable.failure"/>',
    configurationFetchError: '<spring:message javaScriptEscape="true" code="message.configuration.dialog.fetch.error"/>',
    toggleButtonEnable:'<spring:message javaScriptEscape="true" code="label.configuration.integration.togglebutton.enable"/>',
    toggleButtonDisable:'<spring:message javaScriptEscape="true" code="label.configuration.integration.togglebutton.disable"/>',
    configurationRestartRequiredAlert : '<spring:message javaScriptEscape="true" code="label.configuration.restart.info"/>'
};

</script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/jquery.easing.1.3.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/configurationList.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/thirdlevelmenuSlide.js"></script>
<script type="text/javascript">
var configPath = "<%=request.getContextPath() %>/portal/admin/config/show_configuration";
</script>

<link rel="stylesheet" type="text/css" media="all" href="<%= request.getContextPath() %>/css/jquery/themes/base/jquery-ui-1.7.2.custom.css"/>
	
<div class="widget_box widget_noborders">
	<div class="configure_boxpanel first">
    
    <c:forEach items="${componentTypes}" var="choice" varStatus="status">
      <c:set var="item_index" value=""/>
      <c:if test="${status.index % 4 == 0}">
           <c:set var="item_index" value="first"/>
      </c:if>
      
        <div class="configure_box_wrapper ${item_index}">
          <div class="configure_box ${fn:toLowerCase(moduleName)}">
              <div class="configure_box titlepanel">
                  <h2><spring:message code="ui.configuration.filter.${choice}"/></h2>
                    <c:forEach items="${is_enabled_map}" var="enabled_map" varStatus="status">
                      
                    </c:forEach>
                    <c:set var="enable_service_config" value="${fn:toLowerCase(moduleName)}.${fn:toLowerCase(fn:replace(choice.componentName, ' ', ''))}.enabled" />
                    <c:if test='${ ! empty is_enabled_map[enable_service_config]}'>
                      <c:choose>
                        <c:when test="${is_enabled_map[enable_service_config].value== true}">
                          <input type="checkbox" class="toggleButton checkbox_hidden" id="save<c:out value="${is_enabled_map[enable_service_config].id}"/>" component='<c:out value="${choice.componentName}"/>' checked="checked"/>
                        </c:when>
                        <c:otherwise>
                          <input type="checkbox" class="toggleButton checkbox_hidden" id="save<c:out value="${is_enabled_map[enable_service_config].id}"/>" component='<c:out value="${choice.componentName}"/>'/>
                        </c:otherwise>
                        </c:choose>
                    </c:if>
                    
                    
                  
                </div>
                <div class="configure_box logopanel">
                  <div class="configure_logobox">
                    <div class="configure_icons">
                      <c:choose>
                        <c:when test="${choice.componentName == 'Payment Gateway'}">
                          <img src="../../../images/configuration/<c:out value="${fn:toLowerCase(fn:replace(moduleName, ' ', ''))}"></c:out>_<c:out value="${fn:toLowerCase(fn:replace(choice.componentName, ' ', ''))}"></c:out>_<c:out value="${payment_gateway_service}"></c:out>.gif" />
                        </c:when>
                        <c:otherwise>
                          <img src="../../../images/configuration/<c:out value="${fn:toLowerCase(fn:replace(moduleName, ' ', ''))}"></c:out>_<c:out value="${fn:toLowerCase(fn:replace(choice.componentName, ' ', ''))}"></c:out>.gif" />
                       </c:otherwise>
                      </c:choose>
                    </div>
                  </div>
                </div>
                
                <div class="configure_contentbox">
                  <p><spring:message code="ui.configuration.filter.description.${fn:toLowerCase(fn:replace(moduleName, ' ', ''))}.${fn:toLowerCase(fn:replace(choice.componentName, ' ', ''))}"/></p>
                  
                </div>
                
                <div class="configure_footerlinksbox">
                 <a href="#" style="display:none;"><spring:message code="label.administration.configuration.items.link.more"/> &hellip;</a>
                    <a class="configurebutton" id="<c:out value="${fn:toLowerCase(fn:replace(moduleName, ' ', ''))}"></c:out>_<c:out value="${fn:toLowerCase(fn:replace(choice.componentName, ' ', ''))}"></c:out>" href="javascript:void(0);" onclick="configuration_edit_dialog('<c:out value="${module}" />','<c:out value="${choice.componentName}"/>');" ><spring:message code="label.administration.configuration.items.button.configure"/></a>
                </div>
                
                   </div>
            <div class="configure_box_shadow"></div>
        </div>
    </c:forEach>
        
    </div>
    
    
</div>

<div id="spinning_wheel" style="display:none;">
  <div class="widget_blackoverlay widget_rightpanel" style="position:fixed;height:100%">
  </div>
  <div class="widget_loadingbox fullpage" style="position:fixed;">
   <div class="widget_loaderbox" >
     <span class="bigloader"></span>
   </div>
   <div class="widget_loadertext" >
     <p id="in_process_text"><spring:message code="label.loading"/> &hellip;</p>
   </div>
  </div>
</div>
<div id="configuration_edit_panel" title="<spring:message code="label.edit"/>" style="display:none;"> 
    <div class="dialog_formcontent">
    </div>
</div>



<input type="hidden" id="module"  value="<c:out value="${module}"/>"/>
<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<jsp:include page="/WEB-INF/jsp/tiles/shared/js_messages.jsp"></jsp:include>
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
    toggleButtonDisable:'<spring:message javaScriptEscape="true" code="label.configuration.integration.togglebutton.disable"/>',
    tncAcceptMessage:'<spring:message javaScriptEscape="true" code="you.can.not.continue.until.you.accept.the.terms.and.conditions"/>',
    serviceConfigureSuccess:'<spring:message javaScriptEscape="true" code="connector.service.configure.success"/>',
    serviceConfigureFailure:'<spring:message javaScriptEscape="true" code="connector.service.configure.failure"/>',
    serviceAlreadyConfigured:'<spring:message javaScriptEscape="true" code="connector.service.already.configured"/>',
    configure:'<spring:message javaScriptEscape="true" code="label.configure"/>'
};
</script>
<script type="text/javascript">
  var connectorPath = "<%=request.getContextPath()%>/portal/connector";
  var type = "oss";

  $(document).ready(function(){

    function toggleHandler(current){
      if($(current).hasClass("on")){
        enableDisableService(current, $(current).attr("id"), false);
      }
      else{
        enableDisableService(current, $(current).attr("id"), true);
      }
    }

    function enableDisableService(current, id, enable){
      initDialogWithOK("dialog_info", 350, false);
      $("#dialog_info").dialog("option", "height", 150);
      var $thisDialog = $("#dialog_info");
      dialogButtonsLocalizer($thisDialog, {
          'OK': g_dictionary.dialogOK,
          'Cancel': g_dictionary.dialogCancel
      });
      $.ajax({
          type: "POST",
          url: connectorPath + "/enable?id=" + id.substr(8) + "&enable=" + enable,
          dataType: 'json',
          success: function (json) {
            if (json.result == "success") {
                if(enable){
                  $(current).addClass('on').html(dictionary.toggleButtonEnable);
                }else{
                  $(current).removeClass('on').html(dictionary.toggleButtonDisable);
                }
                $("#dialog_info").text(json.message).dialog("open");
            } else {
                $("#dialog_info").text(json.message).dialog("open");
            }
          },
          error: function (request) {
              
          }
      });
    }
    $('.slider-button').toggle(function(){
      toggleHandler(this);
    },function(){
      toggleHandler(this);
    });
  });
  
</script>
<!--[if IE 8]>
<style type="text/css">
  .slider-frame {
    border:1px solid #BBBBBB; 
  }
</style>
<![endif]-->

<script type="text/javascript" src="<%=request.getContextPath() %>/js/ossconnector.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/validator.js"></script>
<div class="widgetcatalog_contentarea" style="display: block;">
  <c:set var="onOffStatus" value="nonactive" />
  <c:if test="${!isSystemActive}">
    <c:set var="onOffStatus" value="active" />
  </c:if>

  <div class="service_listpanel" style="display: block;">
    <jsp:include page="/WEB-INF/jsp/tiles/main/cs_instance_left_menu.jsp"></jsp:include>
    <input type="hidden" id="selectedcategory" value="All"/>
    <c:forEach items="${services}" var="service" varStatus="status">
      <div class="servicelist mainbox" category="${service.category}" serviceid="${service.uuid}">
        <div class="servicelist sections col1">
          <span class="logobox" style="padding"><img src="/portal/portal/logo/connector/${service.uuid}/logo"/></span>
          <div class="servicelist sections col5" style="width: 320px;">
            <ul>
              <li>
                <h3>
                  <spring:message  code="${service.serviceName}.service.name"/>
                </h3>
              </li>
              <li><spring:message  code="label.version"/> <c:out value="${service.vendorVersion}" /></li>
              <li><spring:message  code="label.category"/>: <spring:message  code="${service.serviceName}.service.category"/></li>
              <li><a id="${service.uuid}" class="servicedetails" href="javascript:void(0);"><spring:message  code="label.details"/></a></li>
            </ul>
          </div>
        </div>
        <div class="servicelist sections col2" style="display: none;">
          <ul>
            <li><strong><c:out value="${service.category}" /></strong></li>
            <li><a href="#"></a></li>
            <!-- TO be got from Manifest file. -->
            <li><spring:message  code="label.publishedby"/> <c:out value="${service.vendor}" /></li>
            <li><spring:message  code="label.lastupdated"/>:<c:out value="${service.updatedAt}" /></li>
          </ul>
        </div>
        <div class="servicelist sections col3" style="width:140px;" >
          <div class="button_wrapper">
            <c:choose>
              <c:when test="${service.termsAndConditionsAccepted}">
                <c:choose>
                  <c:when test="${fn:length(service.serviceInstances)>0}">
                    <c:forEach var="instance" items="${service.serviceInstances}" end="0">
                      <c:set var="instanceid" value="${instance.uuid}" />
                    </c:forEach>
                    <a id="configure_${instanceid}" class="add_button edit ${onOffStatus}" href="javascript:void(0);">
                      <spring:message code="label.configure" />
                    </a>
                  </c:when>
                  <c:otherwise>
                    <a id="configure_${service.uuid}" singleton="${service.singleton}" class="add_button add ${onOffStatus}"
                      href="javascript:void(0);">
                      <spring:message code="label.configure" />
                    </a>
                  </c:otherwise>
                </c:choose>
              </c:when>
              <c:otherwise>
                <a id="${service.uuid}" class="termsandconditions" href="javascript:void(0);">
                  <spring:message code="label.configure" />
                </a>
              </c:otherwise>
            </c:choose>
          </div>
        </div>
        <div class="servicelist sections col4" style="width:80px;">
          <c:choose>
            <c:when test="${service.enabled}">
              <div class="slider-frame">
                <span class="slider-button on" id="checkbox${service.uuid}"
                component="<c:out value="${service.serviceName}"/>"><spring:message code="label.configuration.integration.togglebutton.enable"/></span>
              </div>
            </c:when>
            <c:otherwise>
              <div class="slider-frame">
                <span class="slider-button" id="checkbox${service.uuid}"
                component="<c:out value="${service.serviceName}"/>"><spring:message code="label.configuration.integration.togglebutton.disable"/></span>
              </div>
            </c:otherwise>
          </c:choose>
        </div>
      </div>
    </c:forEach>
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

  <div id="dialog_enable_service" title='<spring:message code="terms.and.conditions"/>' style="display: none; overflow:hidden;"></div>
  
  <input type="hidden" id="reloadlist" value="false">
  
</div>

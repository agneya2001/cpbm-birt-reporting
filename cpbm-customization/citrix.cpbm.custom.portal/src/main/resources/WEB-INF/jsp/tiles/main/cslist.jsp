<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<jsp:include page="/WEB-INF/jsp/tiles/shared/js_messages.jsp"></jsp:include>
<script type="text/javascript">
  var connectorPath = "<%=request.getContextPath()%>/portal/connector";
  var type ="cs";
</script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/csconnector.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/jquery.iframe-post-form.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/validator.js"></script>
<script type="text/javascript">
if( typeof i18n === 'undefined' ) {
  var i18n = {};
}
if( typeof i18n.errors === 'undefined' ) {
  i18n.errors = {};
}

i18n.errors.config = {
  valueRequired: '<spring:message javaScriptEscape="true" code="js.errors.config.value.required"/>',
  validPriceRequired: '<spring:message javaScriptEscape="true" code="js.errors.channel.price.error"/>',
  max_four_decimal_value: '<spring:message javaScriptEscape="true" arguments="${currencyFractionalDigitsLimit}"  code="js.errors.maxFourDecimalPrice"/>'
};
var dictionary = {
    toggleButtonEnable:'<spring:message javaScriptEscape="true" code="label.configuration.integration.togglebutton.enable"/>',
    toggleButtonDisable:'<spring:message javaScriptEscape="true" code="label.configuration.integration.togglebutton.disable"/>',
    tncAcceptMessage:'<spring:message javaScriptEscape="true" htmlEscape="false" code="you.can.not.continue.until.you.accept.the.terms.and.conditions"/>',
    editImagePathInvalidMessage:'<spring:message javaScriptEscape="true" code="ui.image.error.invalid.path"/>',
    imageFailed:'<spring:message javaScriptEscape="true" code="ui.image.failed"/>',
    imageUploadedSuccessfully:'<spring:message javaScriptEscape="true" code="ui.service.instance.image.uploaded.successful"/>'
    
};
</script>
<div class="widgetcatalog_contentarea" style="display: block;">
  <div id="myServicesDiv" class="service_listpanel" style="display: block;">
   <sec:authorize access="hasRole('ROLE_CONFIGURATION_CRUD')">
    <jsp:include page="/WEB-INF/jsp/tiles/main/cs_instance_left_menu.jsp"></jsp:include>
   </sec:authorize> 
 
  <sec:authorize access="hasRole('ROLE_CONFIGURATION_CRUD')">    
    <input type="hidden" id="selectedcategory" value="All"/>
    
    <c:forEach items="${services}" var="service" varStatus="status">
      <div class="servicelist mainbox" category="${service.category}" serviceid="${service.uuid}">
        <div class="servicelist sections col6" style="width:470px">
          <span class="logobox"><img src="/portal/portal/logo/connector/${service.uuid}/logo"/></span>
          <div class="servicelist sections col7" style="width: 340px;">
            <ul>
              <li>
                <h3>
									<spring:message  code="${service.serviceName}.service.name"/>
                </h3>
              </li>
							<li><spring:message  code="label.version"/>: <c:out value="${service.vendorVersion}" /></li>
							<li><spring:message  code="label.category"/>: <spring:message  code="${service.serviceName}.service.category"/></li>
							<li><a id="${service.uuid}" class="servicedetails" href="javascript:void(0);"><spring:message  code="label.details"/></a>							
            </ul>
          </div>
        </div>
        <div class="servicelist sections col2" style="display: none;">
          <ul>
            <li><strong><c:out value="${service.category}" /></strong></li>
            <li><a href="javascript:void(0);"></a></li>
            <!-- TO be got from Manifest file. -->
						<li><spring:message  code="label.publishedby"/> <c:out value="${service.vendor}" /></li>
						<li><spring:message  code="label.lastupdated"/>:<c:out value="${service.updatedAt}" /></li>
          </ul>
        </div>
        <div class="servicelist sections col3" style="width: 150px;">
          <div class="button_wrapper">
            <c:choose>
              <c:when test="${service.termsAndConditionsAccepted}">
                <c:choose>
                  <c:when test="${service.singleton && fn:length(service.serviceInstances)>0}">   <!-- if it is singleton service and alreay has an instance then no need to show active add button -->
                    <a id="${service.uuid}" class="add_button nonactive" href="javascript:void(0);">
											<spring:message  code="label.add" />
                    </a>
                  </c:when>
                  <c:otherwise>
                    <a id="${service.uuid}" singleton="${service.singleton}" class="add_button active add_service" href="javascript:void(0);">
											<spring:message  code="label.add" />
                    </a>
                  </c:otherwise>
                </c:choose>
              </c:when>
              <c:otherwise>
                <a id="${service.uuid}" class="termsandconditions" href="javascript:void(0);">
									<spring:message  code="label.enable" />
                </a>
              </c:otherwise>
            </c:choose>
          </div>
        </div>
        <div class="servicelist sections col4">
          <div class="button_wrapper">
            <c:choose>
              <c:when test="${fn:length(service.serviceInstances) eq 0}">
                <a id='<c:out value="${service.uuid}"/>' class="cloud_button nonactive" href="javascript:void(0);"></a>
              </c:when>
              <c:otherwise>
                <a id="${service.uuid}" class="cloud_button active" href="javascript:void(0);">
                  <c:if test="${fn:length(service.serviceInstances) gt 0}">
                    <div class="service_count">${fn:length(service.serviceInstances)}</div>
                  </c:if>
                </a>
              </c:otherwise>
            </c:choose>
          </div>
        </div>
      </div>
      
      
      <div class="servicelist_extended" category="${service.category}" serviceid="${service.uuid}" style="display: none;">
        <ul id="service_instance_list">
          <c:set var="borderwidth" value="none"/>
          <c:forEach items="${service.serviceInstances}" var="instance" varStatus="status">
            <c:if test="${status.last}">
              <c:set var="borderwidth" value="border:0px"/>
            </c:if>
            <li id="service_instances" style="${borderwidth}" serviceid="${instance.uuid}">
              <div class="gridcell" style="width: 5%;">
              	<span id="instance_icon"></span>
                <div class="widget_loaderbox" style="display: none;position:relative;top:-42px;left:-10px">
                  <span class="bigloader cs_instance"></span>
                </div>
              </div>
              <div class="gridcell" style="width: 45%;">
                <c:choose>
                  <c:when test="${not empty instance.imagePath}">
                    <img src="/portal/portal/logo/serviceInstance/<c:out value='${instance.uuid}'/>" width="52px" height="20px" style="background: #CCC;float: left;margin: 5px 2px;">
                  </c:when>
                  <c:otherwise>
                    <img src="/portal/portal/logo/connector/${service.uuid}/logo" width="52px" height="20px" style="background: #CCC;float: left;margin: 5px 2px;">
                  </c:otherwise>
                </c:choose>    
                <span id="instance_name" class="celltext ellipsis" style="width:62%;" title="${instance.name}"><c:out value="${instance.name}"/></span>
              </div>
              <div class="gridcell" style="width: 45%;">
                <span id="instance_description" class="celltext ellipsis" title='<c:out value="${instance.description}"/>'><c:out value="${instance.description}"/></span>
              </div>
              <div class="gridcell" style="width: 5%;">
                <a class="actionbutton" href="javascript:void(0);" id="${instance.uuid}"></a>
                <div class="widget_moreactions action_menu_container actionbutton"
                  style="margin-top: -25px; margin-left: 11px; background: none; display: none;">
                  <!--Actions popover starts here-->
                  <div class="widget_actionpopover" id="action_menu"
                    style="left: -225px; top: 20px;">
                    <div class="widget_actionpopover_top"></div>
                    <div class="widget_actionpopover_mid">
                      <ul class="widget_actionpoplist">
												<li class="edit"><spring:message  code="label.edit.configuration"/></li>
												<li class="reload"><spring:message  code="label.reload.status"/></li>
                        <li class="uploadLogo"><spring:message  code="label.upload.logo"/></li>
                      </ul>
                    </div>
                    <div class="widget_actionpopover_bot"></div>
                  </div>
                </div>
              </div>
            </li>
          </c:forEach>
        </ul>
      </div>
    </c:forEach>
  </sec:authorize>  
    
  </div>
  
   <div id="serviceAccountConfigDiv" style="margin:0; display:none;">     
      <div>
      <h3 id="selectedInstanceH1"></h3>
        <iframe id="serviceAccountConfigViewFrame" width="940px" height="700px" frameborder="0"></iframe>
      </div>
    </div>
  
  <li id="service_instance_default_template" style="display: none;">
    <div class="gridcell" style="width: 5%;">
      <span id="instance_icon" class="running_listicon"></span>
      <div class="widget_loaderbox" style="display: none;position:relative;top:-42px;left:-10px">
        <span class="bigloader"></span> 
      </div>
    </div>
    
    <div class="gridcell" style="width: 45%;">
      <img id="instance_image_id" src="" width="52px" height="20px" style="background: #CCC;float: left;margin: 5px 2px;">
      <span id="instance_name" class="celltext" style="width:none;"></span>
    </div>
    <div class="gridcell" style="width: 45%;">
      <span id="instance_description" class="celltext ellipsis"></span>
    </div>
    <div class="gridcell" style="width: 5%;">
      <a class="actionbutton" href="javascript:void(0);"></a>
      <div class="widget_moreactions action_menu_container actionbutton"
        style="margin-top: -25px; margin-left: 11px; background: none; display: none;">
        <div class="widget_actionpopover" id="action_menu" style="left: -225px; top: 20px;">
          <div class="widget_actionpopover_top"></div>
          <div class="widget_actionpopover_mid">
            <ul class="widget_actionpoplist"> 
							<li class="edit"><spring:message  code="label.edit.configuration"/></li>
							<li class="reload"><spring:message  code="label.reload.status"/></li>
            </ul>
          </div>
          <div class="widget_actionpopover_bot"></div>
        </div>
      </div>
    </div>
  </li>

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
<div id="dialog_upload_service_instance_image" title='<spring:message code="title.custom.service.instance.image"/>' style="display: none">
</div>


  <div id="dialog_add_service_instance" title='<spring:message code="label.add.service.instance"/>' style="display: none; overflow:hidden;"></div>
  <div id="dialog_edit_service_instance" title='<spring:message code="label.edit.service.instance"/>' style="display: none; overflow:hidden;"></div>

</div>

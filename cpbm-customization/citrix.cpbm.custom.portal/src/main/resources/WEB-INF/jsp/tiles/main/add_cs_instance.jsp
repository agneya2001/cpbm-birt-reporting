<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<script language="javascript">
  var dictionary = {viewMasked: '<spring:message javaScriptEscape="true" code="label.show"/>',
        hideMasked: '<spring:message javaScriptEscape="true" code="label.hide"/>',
        code_not_unique: '<spring:message javaScriptEscape="true" code="js.errors.channel.code.notunique"/>',
        max_length_exceeded:"<spring:message javaScriptEscape="true" code='js.errors.channel.length.upperLimit'/>",
        code_invalid:"<spring:message javaScriptEscape="true" code='js.errors.channel.catalogcode.invalid'/>",
        product_name_invalid:"<spring:message javaScriptEscape="true" code='js.errors.product.name'/>",
        product_code_empty:"<spring:message javaScriptEscape="true" code='js.errors.product.code'/>"
        };
</script>



<div class="dialog_formcontent wizard">
  <form id="serviceInstanceForm">
	<div class="widget_wizardcontainer sixstepswizard">
		<input type="hidden" value="${uuid}" name="serviceParam" id="serviceParam">
		
		<!--step 1 starts here-->
		<div style="" class="j_cloudservicepopup" id="step1">
			<input type="hidden" value="step2" name="nextstep" id="nextstep">
			<input type="hidden" value="" name="prevstep" id="prevstep">
			<div class="widgetwizard_stepsbox">
				<div class="widgetwizard_steps_contentcontainer sixstepswizard">
					<div class="widgetwizard_stepscenterbar sixstepswizard">
						<ul>
							<li class="widgetwizard_stepscenterbar fivestepswizard first"><span class="steps active"><span class="stepsnumbers active">1</span></span><span class="stepstitle active"><spring:message code="service.instance.description"/></span></li>
							<li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps"><span class="stepsnumbers">2</span></span><span class="stepstitle"><spring:message code="service.instance.details"/></span></li>
							<li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps"><span class="stepsnumbers">3</span></span><span class="stepstitle"><spring:message code="default.product.selection"/></span></li>
              <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps"><span class="stepsnumbers">4</span></span><span class="stepstitle"><spring:message code="default.product.charges"/></span></li>
              <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps"><span class="stepsnumbers">5</span></span><span class="stepstitle"><spring:message code="service.instance.review"/></span></li>
							<li class="widgetwizard_stepscenterbar fivestepswizard last"><span class="steps last"><span class="stepsnumbers last">6</span></span><span class="stepstitle last"><spring:message code="label.add.service.instance.finish"/></span></li>
						</ul>
					</div>
				</div>
			</div>
			<div class="widgetwizard_contentarea sixstepswizard">
				<div class="widgetwizard_boxes fullheight sixstepswizard">
					<div class="widgetwizard_titleboxes">
						<h2><spring:message code="service.instance.description"/></h2>
						<span><spring:message code="service.instance.description.message"/></span>
					</div>
          <div class="widgetwizard_detailsbox sixstepswizard">
            <ul>
              <li>
                <span class="label"><spring:message code="instance.name" /></span>
                <div class="mandatory_wrapper">
                  <input id="configproperty_instance_name" value="" name="instancename" title="<spring:message  code="instance.name"/>" type="text" class="text required"  maxlen="128" tabindex="1" />
                </div>
                <div class="main_addnew_formbox_errormsg_popup" id="configproperty_instance_nameError"></div>
              </li>

              <li>
                <span class="label"><spring:message code="instance.code" /></span>
                <div class="mandatory_wrapper">
                  <input id="configproperty_instance_code" value="" name="instancecode" title="<spring:message  code="instance.code"/>" type="text" class="text required" maxlen="255" tabindex="2" onchange="validate_code(event, this, 'serviceInstanceCode')"/>
                </div> 
                <div class="main_addnew_formbox_errormsg_popup" id="configproperty_instance_codeError"></div>
              </li>

              <li>
                <span class="label"><spring:message code="instance.description" /></span>
                <div>
                  <textarea id="configproperty_instance_description" name="instancedescription" title="<spring:message  code="instance.description"/>" class="textarea" maxlen="4000" tabindex="3"></textarea>
                </div> 
              </li>

            </ul>
          </div>
				</div>
			</div>
      
			<div class="widgetwizard_nextprevpanel sixstepswizard" id="buttons">
        <input class="widgetwizard_nextprevpanel nextbutton" type="button" data-primary onclick="addServiceInstanceNext(this)" value="<spring:message code="label.next.step"/>" name="<spring:message code="label.next.step"/>" id="add_service_instance_next">
        <a href="javascript:void(0);" class="cancel close_service_instance_wizard" ><spring:message code="label.cancel" /></a>
			</div>
		</div>
		<!--step 1 ends here-->

		<!--step 2 starts here-->
		<div style="display: none;" class="j_cloudservicepopup" id="step2">
			<input type="hidden" value="step3" name="nextstep" id="nextstep">
			<input type="hidden" value="step1" name="prevstep" id="prevstep">
			<div class="widgetwizard_stepsbox">
				<div class="widgetwizard_steps_contentcontainer sixstepswizard">
					<div class="widgetwizard_stepscenterbar sixstepswizard">
						<ul>
              <li class="widgetwizard_stepscenterbar fivestepswizard first"><span class="steps completedsteps"><span class="stepsnumbers completedsteps">1</span></span><span class="stepstitle completedsteps"><spring:message code="service.instance.description"/></span></li>
              <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps active"><span class="stepsnumbers active">2</span></span><span class="stepstitle active"><spring:message code="service.instance.details"/></span></li>
              <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps"><span class="stepsnumbers">3</span></span><span class="stepstitle"><spring:message code="default.product.selection"/></span></li>
              <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps"><span class="stepsnumbers">4</span></span><span class="stepstitle"><spring:message code="default.product.charges"/></span></li>
              <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps"><span class="stepsnumbers">5</span></span><span class="stepstitle"><spring:message code="service.instance.review"/></span></li>
              <li class="widgetwizard_stepscenterbar fivestepswizard last"><span class="steps last"><span class="stepsnumbers last">6</span></span><span class="stepstitle last"><spring:message code="label.add.service.instance.finish"/></span></li>
						</ul>
					</div>
				</div>
			</div>
			<div class="widgetwizard_contentarea sixstepswizard">
				<div class="widgetwizard_boxes fullheight sixstepswizard">
					<div class="widgetwizard_titleboxes">
            <h2><spring:message code="label.configuration"/></h2>
            <span><spring:message code="service.instance.configuration.message"/></span>
					</div>
          <div class="widgetwizard_detailsbox sixstepswizard">
            <c:set var="mandatory_fields" value="Y" scope="request"></c:set>
            <tiles:insertDefinition name="main.home_cs.instance.config.properties"/>
            <ul>
              <li id="optionalSettings" style="background:none;display:none;">
                <a href="javascript:void(0);" class="cancel optional_settings" style="margin:0 0 0 10px;font-weight:normal; font-size:13px;cursor: pointer; opacity: 1; visibility: visible;line-height:30px;"><spring:message code="ui.service.instance.advanced.settings" /></a>
              </li>
            </ul>
            <div id="optional_settings_div" style="display: none;">
                <c:set var="mandatory_fields" value="N" scope="request"></c:set>
                <tiles:insertDefinition name="main.home_cs.instance.config.properties"/>
            </div>
          </div>
          			
				</div>
			</div>
			<div class="widgetwizard_nextprevpanel sixstepswizard" id="buttons">
        <input class="widgetwizard_nextprevpanel prevbutton" type="button" onclick="addServiceInstancePrevious(this)" value="<spring:message code="label.previous.step"/>" name="<spring:message code="label.previous.step"/>" id="add_service_instance_previous">
        <input class="widgetwizard_nextprevpanel nextbutton" type="button" data-primary onclick="addServiceInstanceNext(this)" value="<spring:message code="label.next.step"/>" name="<spring:message code="label.next.step"/>" id="add_service_instance_next">
        <a href="javascript:void(0);" class="cancel close_service_instance_wizard" ><spring:message code="label.cancel" /></a>
			</div>
		</div>
		<!--step 2 ends here-->

		<!--step 3 starts here-->
		<div style="display: none;" class="j_cloudservicepopup" id="step3">
			<input type="hidden" value="step4" name="nextstep" id="nextstep">
			<input type="hidden" value="step2" name="prevstep" id="prevstep">
			<div class="widgetwizard_stepsbox">
				<div class="widgetwizard_steps_contentcontainer sixstepswizard">
					<div class="widgetwizard_stepscenterbar sixstepswizard">
						<ul>
              <li class="widgetwizard_stepscenterbar fivestepswizard first"><span class="steps completedsteps"><span class="stepsnumbers completedsteps">1</span></span><span class="stepstitle completedsteps"><spring:message code="service.instance.description"/></span></li>
              <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps completedsteps"><span class="stepsnumbers completedsteps">2</span></span><span class="stepstitle completedsteps"><spring:message code="service.instance.details"/></span></li>
              <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps active"><span class="stepsnumbers active">3</span></span><span class="stepstitle active"><spring:message code="default.product.selection"/></span></li>
              <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps"><span class="stepsnumbers">4</span></span><span class="stepstitle"><spring:message code="default.product.charges"/></span></li>
              <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps"><span class="stepsnumbers">5</span></span><span class="stepstitle"><spring:message code="service.instance.review"/></span></li>
              <li class="widgetwizard_stepscenterbar fivestepswizard last"><span class="steps last"><span class="stepsnumbers last">6</span></span><span class="stepstitle last"><spring:message code="label.add.service.instance.finish"/></span></li>
						</ul>
					</div>
				</div>
			</div>
      <div class="widgetwizard_contentarea sixstepswizard">
        <div class="widgetwizard_boxes fullheight sixstepswizard">
          <div class="widgetwizard_titleboxes">
            <h2><spring:message code="default.product.selection"/></h2>
            <span><spring:message code="service.instance.product.selection"/></span>
          </div>
          <div class="widgetwizard_detailsbox sixstepswizard gridbox" style="overflow:hidden;">
            <div class="row header" style="overflow:hidden;">
              <div class="gridcell header" style="width: 5%;">
                <input class="text" style="height:15px;width:15px;margin:5px 0 0 8px;" type="checkbox" id="all_selected_usage_type" name="all_selected_usage_type" />
              </div>
              <div class="gridcell header" style="width: 19%;">
                <span class="gridtext header"><spring:message htmlEscape="false" code="ui.label.usage.type" /></span>
              </div>
              <div class="gridcell header" style="width: 19%;">
                <span class="gridtext header"><spring:message htmlEscape="false" code="ui.label.product.name" /></span>
              </div>
              <div class="gridcell header" style="width: 19%;">
                <span class="gridtext header"><spring:message htmlEscape="false" code="ui.label.product.code" /></span>
              </div>
              <div class="gridcell header" style="width: 19%;">
                <span class="gridtext header"><spring:message htmlEscape="false" code="ui.products.label.create.product.category" /></span>
              </div>
              <div class="gridcell header" style="width: 19%;">
                <span class="gridtext header required_product_units_header"><spring:message htmlEscape="false" code="ui.label.required.products" /></span>
              </div>
            </div>                
            <div class="widgetwizard_detailsbox sixstepswizard gridbox" id="productsList" style="overflow-x: hidden; overflow-y: auto; margin-left: 0px; margin-top: 0px; border-width: 0px; height: 237px;">               
              <c:forEach var="usageTypes" items="${service.serviceUsageTypes}" varStatus="usageStatus">
                 <div class="row highlighted" id="productListDiv.<c:out value="${usageTypes.usageTypeName}" />">
                  <div class="gridcell" style="width: 5%;">
                    <input class="text" style="height:15px;width:15px;margin:13px 0 0 8px;" type="checkbox" id="selected_usage_type" name="<c:out value="${usageTypes.usageTypeName}" />" value="<c:out value="${usageTypes.id}" />"/>
                  </div>
                  <div class="gridcell" style="width: 19%;">
                    <span style="width:145px;margin:14px 0 0 10px;" class="gridtext ellipsis" id="usageType.name.<c:out value="${usageTypes.usageTypeName}" />" title="<spring:message htmlEscape="false" code="${service.serviceName}.UsageType.${usageTypes.usageTypeName}.name"/>" ><spring:message htmlEscape="false" code="${service.serviceName}.UsageType.${usageTypes.usageTypeName}.name"/></span>
                  </div>
                  <div class="gridcell" style="width: 19%;">
                      <input class="text" id="product.name.<c:out value="${usageTypes.usageTypeName}" />" value='<spring:message htmlEscape="false" code="${service.serviceName}.UsageType.${usageTypes.usageTypeName}.name"/>' name="product.name.[<c:out value="${usageTypes.usageTypeName}" />]" onchange="validate_name(this)"/>
                       <div class="main_addnew_formbox_errormsg_popup" id="product.name.<c:out value="${usageTypes.usageTypeName}" />Error" style="margin: 0px 0 0 -5px;"></div>
                  </div>
                  <div class="gridcell" style="width: 19%;">
                      <input class="text" id="product.code.<c:out value="${usageTypes.usageTypeName}" />" value='' name="product.code.[<c:out value="${usageTypes.usageTypeName}" />]" onchange="validate_code(event, this, 'productCode')"/>
                      <div class="main_addnew_formbox_errormsg_popup" id="product.code.<c:out value="${usageTypes.usageTypeName}" />Error" style="margin: 0px 0 0 -5px;"></div>
                  </div>
                  <div class="gridcell" style="width: 19%;">
                    <select id="product.category.<c:out value="${usageTypes.usageTypeName}" />" class="select1">
                      <c:forEach var="category" items="${categories}" varStatus="categoryStatus">
                        <option value='<c:out value="${category.id}"/>' >
                          <c:out value="${category.name}" escapeXml="false"/>
                        </option>
                      </c:forEach>                  
                    </select>
                  </div>
                  <div class="gridcell" style="width: 19%;">
                    <select id="product.scale.<c:out value="${usageTypes.usageTypeName}" />" class="select1">
                      <c:forEach var="scale" items="${usageTypes.serviceUsageTypeUom.serviceUsageTypeUomScale}" varStatus="uomScale">
                        <option value='<c:out value="${scale.conversionFactor}"/>' <c:if test="${scale.defaultScale}">selected=selected</c:if>>
                          <c:out value="${scale.name}" escapeXml="false"/>
                        </option>
                      </c:forEach>                  
                    </select>
                  </div>
                </div>
              </c:forEach>
            </div>
          </div>
        </div>
      </div>  
			<div class="widgetwizard_nextprevpanel sixstepswizard" id="buttons">
        <input class="widgetwizard_nextprevpanel prevbutton" type="button" onclick="addServiceInstancePrevious(this)" value="<spring:message code="label.previous.step"/>" name="<spring:message code="label.previous.step"/>" id="add_service_instance_previous">
        <input class="widgetwizard_nextprevpanel nextbutton" type="button" data-primary onclick="addServiceInstanceNext(this)" value="<spring:message code="label.next.step"/>" name="<spring:message code="label.next.step"/>" id="add_service_instance_next">
        <a href="javascript:void(0);" class="cancel close_service_instance_wizard" ><spring:message code="label.cancel" /></a>
			</div>
		</div>
		<!--step 3 ends here-->

    <!--step 4 starts here-->
    <div style="display: none;" class="j_cloudservicepopup" id="step4">
      <input type="hidden" value="step5" name="nextstep" id="nextstep">
      <input type="hidden" value="step3" name="prevstep" id="prevstep">
      <input type="hidden" name="activeCurrencies" id="activeCurrencies" value="<c:out value="${activeCurrencies}"/>">
      <div class="widgetwizard_stepsbox">
        <div class="widgetwizard_steps_contentcontainer sixstepswizard">
          <div class="widgetwizard_stepscenterbar sixstepswizard">
            <ul>
              <li class="widgetwizard_stepscenterbar fivestepswizard first"><span class="steps completedsteps"><span class="stepsnumbers completedsteps">1</span></span><span class="stepstitle completedsteps"><spring:message code="service.instance.description"/></span></li>
              <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps completedsteps"><span class="stepsnumbers completedsteps">2</span></span><span class="stepstitle completedsteps"><spring:message code="service.instance.details"/></span></li>
              <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps completedsteps"><span class="stepsnumbers completedsteps">3</span></span><span class="stepstitle completedsteps"><spring:message code="default.product.selection"/></span></li>
              <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps active"><span class="stepsnumbers active">4</span></span><span class="stepstitle active"><spring:message code="default.product.charges"/></span></li>
              <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps"><span class="stepsnumbers">5</span></span><span class="stepstitle"><spring:message code="service.instance.review"/></span></li>
              <li class="widgetwizard_stepscenterbar fivestepswizard last"><span class="steps last"><span class="stepsnumbers last">6</span></span><span class="stepstitle last"><spring:message code="label.add.service.instance.finish"/></span></li>
            </ul>
          </div>
        </div>
      </div>
      <div class="widgetwizard_contentarea sixstepswizard">
        <div class="widgetwizard_boxes fullheight sixstepswizard">
          <div class="widgetwizard_titleboxes">
            <h2><spring:message code="default.product.charges" htmlEscape="false"></spring:message></h2>
            <span><spring:message code="service.instance.product.charges"/></span>
          </div>
          <div style="width: 830px; padding: 0px 0px 5px;display:none" class="common_messagebox error" id="error_div">
            <span class="erroricon"></span>
            <p style="margin-top:7px;" id="priceError"> <spring:message htmlEscape="false" code="js.errors.channel.price.error" /></p>
          </div>
          <div class="widgetwizard_detailsbox sixstepswizard" style="width: auto">
            <div class="common_messagebox error product_plan_charges_grid" style="padding:0 0 5px 0; border:1px solid #CCCCCC; display:block;display:none;">
              <span class="erroricon"></span>
              <p id="priceRequiredError" style="margin-top:7px;"></p>
            </div>
            <div class="details_lightboxformbox" style="padding-bottom: 0px; border-bottom-width: 0px;">
                <div class="widget_details_inlinegrid product_plan_charges_grid" id="productPriceDiv" style="margin-left: 0px; margin-top: 0px; border-top-width: 0px;width:1000px;">

                  <div class="widget_grid inline subheader product_plan_charges_grid" style="padding-left: 20px;">
                    <div class="widget_grid_cell" style="width:150px;">
                      <span class="subheader" style="margin-left: 2px;"><spring:message htmlEscape="false" code="ui.label.product.name" /></span>
                    </div>
                    <div class="widget_grid_cell" style="width:150px;">
                      <span class="subheader"><spring:message htmlEscape="false" code="ui.label.product.unit" /></span>
                    </div>
                    <c:forEach var="currency" items="${activeCurrencies}">
                      <div class="widget_grid_cell currency_cell" >
                        <div class="widget_flagbox" style="float:left;padding:0;margin:5px 0 0 5px;">
                          <div class="widget_currencyflag">
                             <img alt="" src="../../images/flags/<c:out value="${currency.flag}" />">
                          </div>
                        </div>
                        <span class="subheader"><c:out value="${currency.currencyCode}"/>&nbsp;(&nbsp;<c:out value="${currency.sign}" />&nbsp;)</span>
                      </div>
                    </c:forEach>
                  </div>

                  <div class="widgetgrid_wrapper plangrid_lightbox product_plan_charges_grid" id="productPriceListDiv" style="overflow-x: hidden; overflow-y: auto; height: auto;width:100%;">
                  </div>
                  <div class="widget_grid inline odd product_plan_charges_innergrid" id="productItem" style="display:none; padding-left: 20px;">
                    <div class="widget_grid_cell" style="width:150px;">
                      <span class="subheader ellipsis" id="selectedProductName" style="margin-left: 2px;width:100%;"><spring:message htmlEscape="false" code="ui.label.product.name" /></span>
                      <span class="subheader" style="margin-left: 2px; margin-top:0px;font-weight:normal;" id="selectedProductCategory"><spring:message htmlEscape="false" code="ui.products.label.create.product.category" /></span>
                      <span class="levelicon INFORMATION js_product_details_popover" rel="popover" style="margin-top: 0px; padding-bottom: 0px; margin-left: 5px;position:relative;"></span>
                    </div>
                    <div class="widget_grid_cell" style="width:150px;">
                      <span class="subheader" style="font-weight:normal;" id="selectedUOM"><spring:message htmlEscape="false" code="ui.label.product.unit" /></span>
                    </div>
                    <c:forEach var="productCharge" items="${productCharges}" varStatus="priceStatus">
                      <div class="widget_grid_cell currency_cell" >
                        <div class="mandatory_wrapper" style="margin:5px 0 0 1px;">
                          <input style="width:60px; margin-top: 2px;"
                                 id="<c:out value='${productCharge.currencyValue.currencyCode}' />" 
                                 value='<c:out value="${productCharge.price }" />'
                                 name="productCharges[<c:out value='${priceStatus.index}' />].price"
                                 class="text priceRequired j_pricerequired">
                        </div>
                      </div>
                    </c:forEach>
                    <div class="widget_details_popover js_info_popover" id="info_bubble" style="display:none;">
                      <div class="popover_content_container" >
                        <div class="popover_rows">
                          <div class="row_contents_title">
                            <span><spring:message htmlEscape="false" code="ui.label.product.code" />:</span>
                          </div>
                          <div class="row_contents_value">
                            <span id="selectedProductCode">
                            </span>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                  
                </div>
              </div>
            </div>
          </div>
        </div>
          
      <div class="widgetwizard_nextprevpanel sixstepswizard" id="buttons">
        <input class="widgetwizard_nextprevpanel prevbutton" type="button" onclick="addServiceInstancePrevious(this)" value="<spring:message code="label.previous.step"/>" name="<spring:message code="label.previous.step"/>" id="add_service_instance_previous">
        <input class="widgetwizard_nextprevpanel nextbutton" type="button" data-primary onclick="addServiceInstanceNext(this)" value="<spring:message code="label.next.step"/>" name="<spring:message code="label.next.step"/>" id="add_service_instance_next">
        <a href="javascript:void(0);" class="cancel close_service_instance_wizard" ><spring:message code="label.cancel" /></a>
      </div>
    </div>
    <!--step 4 ends here-->

    <!--step 5 starts here-->
    <div style="display: none;" class="j_cloudservicepopup" id="step5">
      <input type="hidden" value="step6" name="nextstep" id="nextstep">
      <input type="hidden" value="step4" name="prevstep" id="prevstep">
      <div class="widgetwizard_stepsbox">
        <div class="widgetwizard_steps_contentcontainer sixstepswizard">
          <div class="widgetwizard_stepscenterbar sixstepswizard">
            <ul>
              <li class="widgetwizard_stepscenterbar fivestepswizard first"><span class="steps completedsteps"><span class="stepsnumbers completedsteps">1</span></span><span class="stepstitle completedsteps"><spring:message code="service.instance.description"/></span></li>
              <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps completedsteps"><span class="stepsnumbers completedsteps">2</span></span><span class="stepstitle completedsteps"><spring:message code="service.instance.details"/></span></li>
              <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps completedsteps"><span class="stepsnumbers completedsteps">3</span></span><span class="stepstitle completedsteps"><spring:message code="default.product.selection"/></span></li>
              <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps completedsteps"><span class="stepsnumbers completedsteps">4</span></span><span class="stepstitle completedsteps"><spring:message code="default.product.charges"/></span></li>
              <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps active"><span class="stepsnumbers active">5</span></span><span class="stepstitle active"><spring:message code="service.instance.review"/></span></li>
              <li class="widgetwizard_stepscenterbar fivestepswizard last"><span class="steps last"><span class="stepsnumbers last">6</span></span><span class="stepstitle last"><spring:message code="label.add.service.instance.finish"/></span></li>
            </ul>
          </div>
        </div>
      </div>
      <div class="widgetwizard_contentarea sixstepswizard">
        <div class="widgetwizard_boxes fullheight sixstepswizard">
          <div class="widgetwizard_titleboxes">
            <h2><spring:message code="ui.service.instance.review.confirm.title" htmlEscape="false"></spring:message></h2>
            <span><spring:message code="ui.service.instance.review.confirm.title.desc"/></span>
          </div>
          <div class="widgetwizard_detailsbox sixstepswizard" style="width:auto;">
            <div class="common_messagebox error product_plan_charges_grid" id="serviceInstanceError" style="padding:0 0 5px 0; border:1px solid #CCCCCC; display:block;display:none;">
              <span class="erroricon"></span>
              <p id="validationError" style="margin-top:7px;"></p>
            </div>
            <div class="widgetwizard_reviewbox sixstepswizard">
              <ul>
                <li id="confirmServiceInstanceDetails">
                  <span class="label"><spring:message code="service.instance.description" />:</span>
                  <span class="description " ></span>
                  <span class="edit" style="margin-right:60px"><a class="confirm_edit_link" id="backToServiceInstanceDetails" href="javascript:void(0);"><spring:message code="label.edit"/></a></span>
                  <ul>  
                    <li class="subselection" >
                      <span class="label sublabel"><spring:message code="label.name"/>  </span>
                      <span class="description subdescription ellipsis" id="name" ></span>
                    </li>                          
                    <li class="subselection" >
                      <span class="label sublabel"><spring:message code="instance.code"/>  </span>
                      <span class="description subdescription ellipsis" id="code"></span>
                    </li>
                    <li class="subselection" >
                      <span class="label sublabel"><spring:message code="label.description"/>  </span>
                      <span class="description subdescription ellipsis" id="service_description"></span>
                    </li>
                  </ul>
                  </li>
                  <li id="confirmProductDetails">
                    <span class="label"><spring:message code="ui.service.instance.default.products.selected" /></span>
                    <span class="description j_description"></span>
                    <span class="edit" style="margin-right:60px"><a class="confirm_edit_link" id="backToProductSelection" href="javascript:void(0);"><spring:message code="label.edit"/></a></span>
                  </li>
                  </li>
                  <li id="confirmCharges">
                    <span class="label"><spring:message code="ui.service.instance.default.product.charges" /></span>
                    <span class="description j_description"></span>
                    <span class="edit" style="margin-right:60px"><a class="confirm_edit_link" id="backToProductCharges" href="javascript:void(0);"><spring:message code="label.edit"/></a></span>
                  </li>
                </ul>
              </div>
            </div>  
            <div id="spinning_wheel" style="display: none;">
              <div class="widget_blackoverlay widget_rightpanel" style="position: fixed; height: 100%"></div>
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
            
        </div>
      </div>  
      <div class="widgetwizard_nextprevpanel sixstepswizard" id="buttons">
        <input class="widgetwizard_nextprevpanel prevbutton" type="button" onclick="addServiceInstancePrevious(this)" value="<spring:message code="label.previous.step"/>" name="<spring:message code="label.previous.step"/>" id="add_service_instance_previous">
        <input class="widgetwizard_nextprevpanel nextbutton" type="button" uuid="${service.uuid}" action="save"  data-primary onclick="addServiceInstanceNext(this)" value="<spring:message code="label.add"/>" name="<spring:message code="label.add"/>" id="add_service_instance_next">
        <a href="javascript:void(0);" class="cancel close_service_instance_wizard" ><spring:message code="label.cancel" /></a>
      </div>
    </div>
    <!--step 5 ends here-->

		<!--step 6 starts here-->
		<div style="display: none;" class="j_cloudservicepopup" id="step6">
			<input type="hidden" value="" name="nextstep" id="nextstep">
			<input type="hidden" value="step5" name="prevstep" id="prevstep">
      <div class="widgetwizard_stepsbox">
        <div class="widgetwizard_steps_contentcontainer sixstepswizard">
          <div class="widgetwizard_stepscenterbar sixstepswizard">
            <ul>
              <li class="widgetwizard_stepscenterbar fivestepswizard first"><span class="steps completedsteps"><span class="stepsnumbers completedsteps">1</span></span><span class="stepstitle completedsteps"><spring:message code="service.instance.description"/></span></li>
              <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps completedsteps"><span class="stepsnumbers completedsteps">2</span></span><span class="stepstitle completedsteps"><spring:message code="service.instance.details"/></span></li>
              <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps completedsteps"><span class="stepsnumbers completedsteps">3</span></span><span class="stepstitle completedsteps"><spring:message code="default.product.selection"/></span></li>
              <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps completedsteps"><span class="stepsnumbers completedsteps">4</span></span><span class="stepstitle completedsteps"><spring:message code="default.product.charges"/></span></li>
              <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps completedsteps"><span class="stepsnumbers completedsteps">5</span></span><span class="stepstitle completedsteps"><spring:message code="service.instance.review"/></span></li>
              <li class="widgetwizard_stepscenterbar fivestepswizard last"><span class="steps last active"><span class="stepsnumbers last">6</span></span><span class="stepstitle last"><spring:message code="label.add.service.instance.finish"/></span></li>
            </ul>
          </div>
        </div>
      </div>
      <div class="widgetwizard_contentarea sixstepswizard">
        <div class="widgetwizard_boxes fullheight sixstepswizard">
          <div class="widgetwizard_successbox">
            <div class="widgetwizard_successbox">
              <div class="widget_resulticon success"></div>
                <p id="successmessage"><spring:message htmlEscape="false" code="ui.service.instance.successfully.completed.text"/>&nbsp;</p>
              </div>
            </div>
          </div>
        </div>
      <div id="buttons" class="widgetwizard_nextprevpanel sixstepswizard">
        <input class="widgetwizard_nextprevpanel submitbutton" type="button" data-primary onclick="addServiceInstanceNext(this)" value="<spring:message code="label.close"/>" name="Close" id="add_service_instance_next">
      </div>
		</div>
		<!--step 6 ends here-->
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

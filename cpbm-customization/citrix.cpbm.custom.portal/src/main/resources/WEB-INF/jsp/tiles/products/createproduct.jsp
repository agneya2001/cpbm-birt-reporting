<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<jsp:include page="js_messages.jsp"></jsp:include>

<script type="text/javascript">
  var currentstep ="";
  var $currentDialog="";
  var product_action="";
  var productsUrl = "<%=request.getContextPath()%>/portal/products/";
  currentstep = "step1";
  product_action = "create";
  $("#dialog_edit_product").empty();
  // select firstproduct type
  $(".j_producttype:first").click();
  $(".breadcrumbs").each(function() {
    $(this).text($("#serviceCategories").find(".on").text().trim() + 
                 ' / ' +
                 $("#instances").find(".instance_selected").text().trim());
  });
  $("#productServiceName").text($("#serviceCategories").find(".on").text().trim());

  var l10discAndUsageTypeNames = new Array();
  <c:forEach items="${serviceUsageTypeNames}" var="serviceUsageTypeName">
    l10discAndUsageTypeNames['${serviceUsageTypeName}-name']='<spring:message javaScriptEscape="true" code="${serviceName}.UsageType.${serviceUsageTypeName}.name"/>';
  </c:forEach>

  <c:forEach items="${discrimintaorNames}" var="discrimintaorName">
    l10discAndUsageTypeNames['${discrimintaorName}-name']='<spring:message javaScriptEscape="true" code="${serviceName}.UsageType.Discriminator.${discrimintaorName}.name"/>';
  </c:forEach>
  
  var serviceUuid = '${serviceUuid}';
  var currPrecision="<c:out value="${currencyFractionalDigitsLimit}"/>";
  </script>


<script type="text/javascript" src="<%=request.getContextPath() %>/js/products.js"></script>


<div class="dialog_formcontent wizard">
<spring:url value="/portal/products/createproduct" var="create_product_path" htmlEscape="false" /> 
<form:form commandName="productForm" id="productForm" cssClass="ajaxform" action="${create_product_path}">
<!--  Add new Product starts here-->
  <div class="widget_wizardcontainer sixstepswizard">
  <!--step 1 starts here-->
  <div id="step1"  class="j_productspopup">
    <input type="hidden" id="nextstep" name="nextstep" value="step2" >
    <input type="hidden" id="prevstep" name="prevstep" value="" >
    <input type="hidden" id="jsonServiceinstancemap" value="<c:out value="${jsonServiceInstanceMap}"/>"/>
    <form:input type="hidden" path="serviceInstanceUUID" id="serviceinstanceuuid"/>

        <div class="widgetwizard_stepsbox">
              <div class="widgetwizard_steps_contentcontainer sixstepswizard">
                  <div class="widgetwizard_stepscenterbar sixstepswizard">
                    <ul>
                        <li class="widgetwizard_stepscenterbar sevenStepsWizard first"><span class="steps active"><span class="stepsnumbers active">1</span></span><span class="stepstitle active"><spring:message htmlEscape="false" code="ui.product.step1.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sevenStepsWizard"><span class="steps"><span class="stepsnumbers">2</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.product.step2.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sevenStepsWizard"><span class="steps"><span class="stepsnumbers">3</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.product.step3.title" /> </span></li>
                        <li class="widgetwizard_stepscenterbar sevenStepsWizard"><span class="steps"><span class="stepsnumbers">4</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.product.conversionFactor.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sevenStepsWizard"><span class="steps"><span class="stepsnumbers">5</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.product.add.charges.step.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sevenStepsWizard"><span class="steps"><span class="stepsnumbers">6</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.product.step6.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sevenStepsWizard last"><span class="steps last"><span class="stepsnumbers last">7</span></span><span class="stepstitle last"><spring:message htmlEscape="false" code="ui.product.step5.title" /></span></li>
                   </ul>
                </div>
            </div>
        </div>

        <div class="widgetwizard_projectbc">
            <span class="breadcrumbs ellipsis"></span>
        </div>

        <div class="widgetwizard_contentarea sixstepswizard" style="margin-top:0;">
          <div class="widgetwizard_boxes fullheight sixstepswizard">

            <div class="widgetwizard_titleboxes">
              <h2>
                <spring:message code="ui.product.step1.title" />
              </h2>
              <span><spring:message code="ui.product.deteailsstep.desc" /></span>
            </div>

            <div class="widgetwizard_detailsbox sixstepswizard">
              <ul>

                <li id="productname"><span class="label"><spring:message
                      code="ui.products.label.create.name" /></span>
                  <div class="mandatory_wrapper">
                    <form:input cssClass="text" path="product.name" tabindex="1" maxlength="255"/>
                  </div>
                  <div class="main_addnew_formbox_errormsg_popup" id="product.nameError" ></div>
                </li>

                <li id="productcode"><span class="label"><spring:message
                      code="ui.products.label.create.product.code" /></span>
                  <div class="mandatory_wrapper">
                    <form:input cssClass="text" path="product.code" tabindex="2" maxlength="64"/>
                  </div> <input id="product_code" type="hidden" value="<c:out  value="${productForm.product.code}" />" />
                  <div class="main_addnew_formbox_errormsg_popup" id="product.codeError" ></div>
                </li>

                <li id="description"><span class="label"><spring:message
                      code="ui.products.label.create.product.description" /></span>
                  <div>
                    <form:textarea cssClass="textarea" path="product.description" tabindex="4" maxlength="4096"></form:textarea>
                  </div>
                </li>

                <li id="productCategory"><span class="label"><spring:message
                      code="ui.products.label.create.product.category" /></span>
                  <div class="mandatory_wrapper">
                  <form:select tabindex="6" path="categoryID" title="${i18nCreditCardCountry}" cssClass="select">
                    <c:forEach items="${categories}" var="choice" varStatus="status">
                      <option value='<c:out value="${choice.id}"/>' >
                        <c:out value="${choice.name}" escapeXml="false"/>
                     </option>
                    </c:forEach>
                 </form:select>
                  </div>
                  <div class="main_addnew_formbox_errormsg_popup" id="categoryError" ></div>
                </li>
                
                <li id="replacement_product_check">
                <div class="nonmandatory_wrapper">
                   <input id="isNewProduct" class="text" type="checkbox" value="true" checked="checked" tabindex="8" style="width:auto;" name="isNewProduct">
                   <span class="label" style="width: 450px; margin: 15px 0pt 0pt 10px;"><spring:message htmlEscape="false" code="ui.product.create.is.newProduct" /></span>
                   </div>
                </li>

              </ul>
            </div>
          </div>
        </div>
        <div id="buttons" class="widgetwizard_nextprevpanel sixstepswizard">
            <input class="widgetwizard_nextprevpanel nextbutton" type="button" data-primary onclick="addProductNext(this)" value="<spring:message code="label.next.step"/>" name="<spring:message code="label.next.step"/>" id="add_product_next">
                <a href="javascript:void(0);" class="cancel close_product_wizard" ><spring:message code="label.cancel" /></a>
          </div>
     <input type="hidden" id="usageTypesPopulated" name="usageTypesPopulated" value="false" >
  </div>
    <!--step 1 ends here-->
    
   <!--step 2 starts here-->
  <div id="step2" class="j_productspopup" style="display:none;">
      <input type="hidden" id="nextstep" name="nextstep" value="step3" >
      <input type="hidden" id="prevstep" name="prevstep" value="step1" >
      <input type="hidden" id="jsonFinalMap" name="jsonFinalMap" value='<c:out value="${jsonFinalMap}"/>' >
      <input type="hidden" id="discDict" name="discDict" value='{}' >
      <input type="hidden" id="serviceUsageTypes" name="serviceUsageTypes" value='{}' >
      <form:input type="hidden" path="productMediationRules" id="productmediationrules"/>

        <div class="widgetwizard_stepsbox">
            <div class="widgetwizard_steps_contentcontainer sixstepswizard">
                <div class="widgetwizard_stepscenterbar sixstepswizard">
                    <ul>
                        <li class="widgetwizard_stepscenterbar sevenStepsWizard first"><span class="steps completedsteps"><span class="stepsnumbers">1</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.product.step1.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sevenStepsWizard"><span class="steps active"><span class="stepsnumbers active">2</span></span><span class="stepstitle active"><spring:message htmlEscape="false" code="ui.product.step2.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sevenStepsWizard"><span class="steps"><span class="stepsnumbers">3</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.product.step3.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sevenStepsWizard"><span class="steps"><span class="stepsnumbers">4</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.product.conversionFactor.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sevenStepsWizard"><span class="steps"><span class="stepsnumbers">5</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.product.add.charges.step.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sevenStepsWizard"><span class="steps"><span class="stepsnumbers">6</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.product.step6.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sevenStepsWizard last"><span class="steps last"><span class="stepsnumbers last">7</span></span><span class="stepstitle last"><spring:message htmlEscape="false" code="ui.product.step5.title" /></span></li>                   
                    </ul>
                </div>
            </div>
        </div>

        <div class="widgetwizard_projectbc">
            <span class="breadcrumbs ellipsis"></span>
        </div>

        <div class="widgetwizard_contentarea sixstepswizard" style="margin-top:0;">
          <div class="widgetwizard_boxes fullheight sixstepswizard">
            <div class="widgetwizard_titleboxes">
              <h2><spring:message htmlEscape="false" code="ui.product.step2.title" /></h2>
              <span><spring:message htmlEscape="false" code="ui.label.product.add.usage.handling.step.tiltle.desc" /></span>
            </div>
            <div class="widgetwizard_detailsbox sixstepswizard gridbox" id="mediationRules">
              <div class="row header">
                <div class="gridcell header" style="width: 25%;">
                  <span class="gridtext header"><spring:message htmlEscape="false" code="ui.label.usage.type" /></span>
                </div>
                <div class="gridcell header" style="width: 40%;">
                  <span class="gridtext header"><spring:message htmlEscape="false" code="ui.label.generates.usage" /></span>
                </div>
                <div class="gridcell header" style="width: 20%;">
                  <span class="gridtext header"><spring:message htmlEscape="false" code="ui.label.aggregation.handling" /></span>
                </div>
                <div class="gridcell header" style="width: 10%;">
                  <span class="gridtext header"></span>
                </div>
              </div>

              <%-- row template for usage type --%>
              <div class="row highlighted" id="usageTypeAdd">
                <input type="hidden" id="uom" name="uom" />
                <div class="gridcell" style="width: 25%;">
                  <select class="select1 expandable-select2" tabindex="116" id="usagetype" onchange="changeUom(this)">
                  </select>
                </div>
                <div class="gridcell" style="width: 40%;">
                  <span class="gridtext" id="uomtext"></span>
                </div>
                <div class="gridcell" style="width: 20%;">
                  <select class="select1" id="operator"><option value="combine">
                      <spring:message htmlEscape="false" code="ui.label.combine" />
                    </option>
                    <option value="exclude">
                      <spring:message htmlEscape="false" code="ui.label.exclude" />
                    </option></select>
                </div>
                <div class="gridcell" style="width: 10%;">
                  <span class="gridtext" id="operations"><a class="add" href="javascript:addUsageType($(this))"></a></span>
                </div>
              </div>

              <div class="row highlighted" id="usageTypeAdded" style="display:none">
                <input type="hidden" id="uom" name="uom" />
                <div class="gridcell" style="width: 25%;">
                  <span class="gridtext" value="" id="usagetype"></span>
                </div>
                <div class="gridcell" style="width: 41%;">
                  <span class="gridtext" value="" id="uomtext"></span>
                </div>
                <div class="gridcell" style="width: 18%;">
                  <span class="gridtext" value="" id="operator"></span>
                </div>
                <div class="gridcell" style="width: 10%;">
                  <span class="gridtext" id="operations"><a class="add" href="javascript:addUsageType($(this))"></a></span>
                </div>
            </div>

           </div>
          </div>
        </div>

        <div class="main_addnew_formbox_errormsg_popup" id="productMedRuleSelectError" style="margin: 5px 0 0 280px; width:500px;"></div>

        <div id="buttons" class="widgetwizard_nextprevpanel sixstepswizard">
            <input class="widgetwizard_nextprevpanel prevbutton" type="button" onclick="addProductPrevious(this)" value="<spring:message code="label.previous.step"/>" name="<spring:message code="label.previous.step"/>" id="add_product_previous">
              <input class="widgetwizard_nextprevpanel nextbutton" type="button" data-primary onclick="addProductNext(this)" value="<spring:message code="label.next.step"/>" name="<spring:message code="label.next.step"/>" id="add_product_next">
              <a href="javascript:void(0);" class="cancel close_product_wizard" ><spring:message code="label.cancel" /></a>
        </div>

    </div>
    <!--step 2 ends here-->
    
     <!--step 3 starts here-->
  <div id="step3" class="j_productspopup" style="display:none">
  <input type="hidden" id="nextstep" name="nextstep" value="step4" >
  <input type="hidden" id="prevstep" name="prevstep" value="step2" >
  <input type="hidden" id="step3AlreadyReached" name="step3AlreadyReached" value='false' >

        <div class="widgetwizard_stepsbox">
            <div class="widgetwizard_steps_contentcontainer sixstepswizard">
                <div class="widgetwizard_stepscenterbar sixstepswizard">
                    <ul>
                        <li class="widgetwizard_stepscenterbar sevenStepsWizard first"><span class="steps completedsteps"><span class="stepsnumbers">1</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.product.step1.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sevenStepsWizard"><span class="steps completedsteps"><span class="stepsnumbers">2</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.product.step2.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sevenStepsWizard"><span class="steps active"><span class="stepsnumbers active">3</span></span><span class="stepstitle active"><spring:message htmlEscape="false" code="ui.product.step3.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sevenStepsWizard"><span class="steps"><span class="stepsnumbers">4</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.product.conversionFactor.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sevenStepsWizard"><span class="steps"><span class="stepsnumbers">5</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.product.add.charges.step.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sevenStepsWizard"><span class="steps"><span class="stepsnumbers">6</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.product.step6.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sevenStepsWizard last"><span class="steps last"><span class="stepsnumbers last">7</span></span><span class="stepstitle last"><spring:message htmlEscape="false" code="ui.product.step5.title" /></span></li>                    </ul>
                </div>
            </div>
        </div>

        <div class="widgetwizard_projectbc">
            <span class="breadcrumbs ellipsis"></span>
        </div>

        <div class="widgetwizard_contentarea sixstepswizard" style="margin-top:0;">
            <div class="widgetwizard_boxes fullheight sixstepswizard">
                <div class="widgetwizard_titleboxes">
                    <h2><spring:message htmlEscape="false" code="ui.product.step3.title" /></h2>
                    <span><spring:message htmlEscape="false" code="ui.label.product.add.advanced.mediation.step.tiltle.desc" /></span>
                </div>
                <div class="widgetwizard_gridmenubox">
                  <div class="row header">
                    <div class="gridcell header" style="width:90%;">
                        <span class="gridtext header"><spring:message htmlEscape="false" code="ui.label.usage.type" /></span>
                    </div>
                  </div>
                  <div class="widgetwizard_navigationlist">
                        <ul id="usageTypeDisp"></ul>
                    </div>
                </div>

                <div class="widgetwizard_detailsbox sixstepswizard gridbox griddescriptionbox" id="discriminatorsContainer" >
                    <div class="row header">
                      <div class="gridcell header">
                          <span class="gridtext header"><spring:message htmlEscape="false" code="ui.label.collect.usage.where" /></span>
                      </div>
                    </div>

                    <div id="mediationRuleDiscriminators">
                      <div id="usagetype_discriminator_block_template" style="display:none">
                        <div class="row highlighted" id="discriminatorRow">
                            <div class="gridcell" style="width:5%;">
                              <span class="gridtext"><a class="vm_tooltip" href="#" style="display:none"></a></span>
                            </div>

                            <div class="gridcell" style="width:30%;">
                              <select class="select1 select_desc_name_class" id ="discriminatorName">
                                    <option value=""></option>
                              </select>
                              <span class="gridtext" style="display:none" value="" id="discriminatorNameSpan"></span>
                            </div>

                             <div class="gridcell" style="width:30%; display:none;" id="discriminatorOperatorDiv">
                              <select class="select1" id ="discriminatorOperator" onchange="changeDiscriminatorOperator(this);">
                                  <option value="equals"><spring:message htmlEscape="false" code="ui.label.includes" /></option>
                                  <option value="not_equals"><spring:message htmlEscape="false" code="ui.label.excludes" /></option>
                               </select>
                            </div>

                          <div class="gridcell  mandatory_wrapper" style="width:20%; display:none;" id="grid_cell_discvalue_selectbox">
                            <select class="select1 select_desc_name_class" id="discvalue_selectbox">
                              <option value="">
                                <spring:message htmlEscape="false" code="label.choose" />
                              </option>
                            </select>
                            <div><label class="js_error error" style="width:150px;"></label></div> 
                          </div>
  
                          <div class="gridcell" style="width:20%;display: none;" id="grid_cell_discvalue_inputbox">
                            <input class="text" id="discvalue_inputbox" />
                          </div>

                          <div class="gridcell" style="width:10%;">
                                <span class="gridtext"><a id="add_discriminator_link" class="add" href="javascript:void(0)" onClick="addDiscriminatorRow($(this))"></a></span>
                          </div>
                        </div>
                      </div>
                    </div>

                </div>
            </div>
        </div>

        <div id="buttons" class="widgetwizard_nextprevpanel sixstepswizard">
            <input class="widgetwizard_nextprevpanel prevbutton" type="button" onclick="addProductPrevious(this)" value="<spring:message code="label.previous.step"/>" name="<spring:message code="label.previous.step"/>" id="add_product_previous">
            <input class="widgetwizard_nextprevpanel nextbutton" type="button" data-primary onclick="addProductNext(this)" value="<spring:message code="label.next.step"/>" name="<spring:message code="label.next.step"/>" id="add_product_next">
                <a href="javascript:void(0);" class="cancel close_product_wizard" ><spring:message code="label.cancel" /></a>
        </div>
  </div>
    <!--step 3 ends here-->
    
    <!--step 4 starts here-->
   <div id="step4" class="j_productspopup" style="display:none;">
      <input type="hidden" id="nextstep" name="nextstep" value="step5" >
      <input type="hidden" id="prevstep" name="prevstep" value="step3" >
      <input type="hidden" id="jsonFinalMap" name="jsonFinalMap" value='<c:out value="${jsonFinalMap}"/>' >
      <input type="hidden" id="discDict" name="discDict" value='{}' >
      <input type="hidden" id="serviceUsageTypes" name="serviceUsageTypes" value='{}' >
      <input type="hidden" id="usageTypeChanged" name="usageTypeChanged" value='true' >
      <input type="hidden" id="selectedUsageType" name="selectedUsageType">
      <input type="hidden" id="isProductDiscrete" name="isProductDiscrete">
      <input type="hidden" id="showingScalesFor" name="showingScalesFor">
      <input type="hidden" id="originalScales" name="originalScales">
      <form:input type="hidden" path="product.uom" id="product.uom"/>
      <form:input type="hidden" path="conversionFactor" id="conversionFactor"/>

        <div class="widgetwizard_stepsbox">
            <div class="widgetwizard_steps_contentcontainer sixstepswizard">
                <div class="widgetwizard_stepscenterbar sixstepswizard">
                    <ul>
                        <li class="widgetwizard_stepscenterbar sevenStepsWizard first"><span class="steps completedsteps"><span class="stepsnumbers">1</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.product.step1.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sevenStepsWizard"><span class="steps completedsteps"><span class="stepsnumbers">2</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.product.step2.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sevenStepsWizard"><span class="steps completedsteps"><span class="stepsnumbers">3</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.product.step3.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sevenStepsWizard"><span class="steps active"><span class="stepsnumbers active">4</span></span><span class="stepstitle active"><spring:message htmlEscape="false" code="ui.product.conversionFactor.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sevenStepsWizard"><span class="steps"><span class="stepsnumbers">5</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.product.add.charges.step.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sevenStepsWizard"><span class="steps"><span class="stepsnumbers">6</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.product.step6.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sevenStepsWizard last"><span class="steps last"><span class="stepsnumbers last">7</span></span><span class="stepstitle last"><spring:message htmlEscape="false" code="ui.product.step5.title" /></span></li>                   
                    </ul>
                </div>
            </div>
        </div>

        <div class="widgetwizard_projectbc">
            <span class="breadcrumbs ellipsis"></span>
        </div>

        <div class="widgetwizard_contentarea sixstepswizard" style="margin-top:0;">
          <div class="widgetwizard_boxes fullheight sixstepswizard">
            <div class="widgetwizard_titleboxes">
               <h2><spring:message htmlEscape="false" code="ui.product.conversionFactor.title" /></h2>
                    <span><spring:message htmlEscape="false" code="ui.label.product.add.conversion.factor.step.tiltle.desc" /></span>
            </div>
            <div class="widgetwizard_detailsbox sixstepswizard gridbox" id="mediationRules">
              <div class="row header">
                <div class="gridcell header" style="width: 25%;">
                  <span class="gridtext header"><spring:message htmlEscape="false" code="ui.label.generates.usage" /></span>
                </div>
                <div class="gridcell header" style="width: 25%;">
                  <span class="gridtext header"><spring:message htmlEscape="false" code="ui.label.required.products" /></span>
                </div>
                <div class="gridcell header" style="width: 27%;display:none" id="step4_customUnits">
                  <span class="gridtext header"><spring:message htmlEscape="false" code="ui.label.custom.product.unit" /></span>
                </div>
                <div class="gridcell header" style="width: 23%;">
                  <span class="gridtext header"><spring:message htmlEscape="false" code="ui.label.comversion.factor" /></span>
                </div>
              </div>

              <div class="row highlighted" id="usageTypeAdd">
                <div class="gridcell" style="width: 25%;">
                  <span class="gridtext" id="step4_uom"></span>
                </div>
                <div class="gridcell" style="width: 25%;">
                  <select class="select1" id="step4_scale"style="width: 66%;"></select>
                </div>
                
                <div class="gridcell" style="width: 27%;display:none" id="step4_customUnitsName">
                <div class="mandatory_wrapper">
                  <input type="text" id="step4_custom_units"  class="text" /> 
                </div>
                </div>
                
                <div class="gridcell" style="width: 23%;">
                  <span class="gridtext" id="step4_conversionFactor_label"></span>
                  <div class="mandatory_wrapper">
                  <input type="text" id="step4_conversionFactor" name="step4_conversionFactor" class="text" style="display:none;"/>
                  </div>
                </div>
              </div>
              
           </div>
          </div>
        </div>

        <div class="main_addnew_formbox_errormsg_popup" id="conversionFactorValuesError" style="margin: 5px 0 0 280px; width:500px;"></div>

        <div id="buttons" class="widgetwizard_nextprevpanel sixstepswizard">
            <input class="widgetwizard_nextprevpanel prevbutton" type="button" onclick="addProductPrevious(this)" value="<spring:message code="label.previous.step"/>" name="<spring:message code="label.previous.step"/>" id="add_product_previous">
              <input class="widgetwizard_nextprevpanel nextbutton" type="button" data-primary onclick="addProductNext(this)" value="<spring:message code="label.next.step"/>" name="<spring:message code="label.next.step"/>" id="add_product_next">
              <a href="javascript:void(0);" class="cancel close_product_wizard" ><spring:message code="label.cancel" /></a>
        </div>

    </div>
    <!--step 4 ends here-->

     <!--step 5 starts here-->
  <div id="step5" class="j_productspopup" style="display:none;">
  <input type="hidden" id="nextstep" name="nextstep" value="step6" >
  <input type="hidden"  id="prevstep"name="prevstep" value="step4" >

        <div class="widgetwizard_stepsbox">
            <div class="widgetwizard_steps_contentcontainer sixstepswizard">
                <div class="widgetwizard_stepscenterbar sixstepswizard">
                    <ul>
                        <li class="widgetwizard_stepscenterbar sevenStepsWizard first"><span class="steps completedsteps"><span class="stepsnumbers">1</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.product.step1.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sevenStepsWizard"><span class="steps completedsteps"><span class="stepsnumbers">2</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.product.step2.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sevenStepsWizard"><span class="steps completedsteps"><span class="stepsnumbers">3</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.product.step3.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sevenStepsWizard"><span class="steps completedsteps"><span class="stepsnumbers">4</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.product.conversionFactor.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sevenStepsWizard"><span class="steps active"><span class="stepsnumbers active">5</span></span><span class="stepstitle active"><spring:message htmlEscape="false" code="ui.product.add.charges.step.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sevenStepsWizard"><span class="steps"><span class="stepsnumbers">6</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.product.step6.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sevenStepsWizard last"><span class="steps last"><span class="stepsnumbers last">7</span></span><span class="stepstitle last"><spring:message htmlEscape="false" code="ui.product.step5.title" /></span></li></ul>
                </div>
            </div>
        </div>

        <div class="widgetwizard_projectbc">
            <span class="breadcrumbs ellipsis"></span>
        </div>

        <div class="widgetwizard_contentarea sixstepswizard" style="margin-top:0;">
            <div class="widgetwizard_boxes fullheight sixstepswizard" style="overflow: hidden;">
                <div class="widgetwizard_titleboxes">
                    <h2><spring:message code="ui.product.add.charges.step.title" htmlEscape="false"></spring:message></h2>
                    <span><spring:message code="ui.product.add.charges.step.title.desc"/></span>
                </div>
                <div class="widgetwizard_detailsbox sixstepswizard" style="width: auto">
                  <div class="common_messagebox error product_plan_charges_grid" style="padding:0 0 5px 0; border:1px solid #CCCCCC; display:block;display:none;">
                    <span class="erroricon"></span>
                    <p id="priceRequiredError" style="margin-top:7px;"></p>
                  </div>
                  <div class="details_lightboxformbox" style="padding-bottom: 0px; border-bottom-width: 0px;">
                    <div class="widget_details_inlinegrid product_plan_charges_grid" style="margin-left: 0px; margin-top: 0px; border-top-width: 0px;">

                      <div class="widget_grid inline subheader product_plan_charges_grid" style="padding-left: 20px;">
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

                      <div class="widgetgrid_wrapper plangrid_lightbox product_plan_charges_grid" style="overflow-x: hidden; overflow-y: auto;">

                        <div class="widget_grid inline odd product_plan_charges_innergrid" style="padding-left: 20px;">

                          <c:forEach var="productCharge" items="${productForm.productCharges}" varStatus="priceStatus">
                            <div class="widget_grid_cell currency_cell" >
                              <div class="mandatory_wrapper" style="margin:5px 0 0 1px;">
                                <input style="width:60px; margin-top: 2px;"
                                       id="productCharges.<c:out value='${priceStatus.index}' />" 
                                       value='<c:out value="${productCharge.price }" />'
                                       name="productCharges[<c:out value='${priceStatus.index}' />].price"
                                       class="text priceRequired j_pricerequired">
                              </div>
                              <div id="productCharges.<c:out value='${priceStatus.index}' />Error">
                              </div>
                            </div>
                          </c:forEach>
                        </div>

                      <c:forEach items="${productForm.catalogChargesFormList}" var="catalogChargesForm" varStatus="status">
                          <c:choose>
                            <c:when test="${status.index % 2 == 0}">
                              <c:set var="rowClass" value="even"/>
                            </c:when>
                            <c:otherwise>
                              <c:set var="rowClass" value="odddark"/>
                            </c:otherwise>
                          </c:choose>

                          <div class="<c:out value="widget_grid inline ${rowClass}"/> product_plan_charges_innergrid" >
                            <div class="widget_grid_cell product_displayname_cell">
                               <span class="celltext ellipsis" style="font-weight:bold;width:105px; color:#0A79AC; margin:15px 0 0 5px;"><c:out value="${catalogChargesForm.catalog.name}"/></span>
                            </div>

                            <c:forEach var="currency" items="${activeCurrencies}" varStatus="priceStatus">
                              <c:forEach items="${catalogChargesForm.catalogProductCharges}" var="catalogProductCharge" varStatus="priceStatus"> 
                                  <div class="widget_grid_cell currency_cell">
                                    <div class="mandatory_wrapper" style="margin:5px 0 0 1px;">
                                      <c:if test="${currency == catalogProductCharge.currencyValue}">
                                        <input style="width:60px; margin-top: 2px;"
                                               id="catalogChargesFormList<c:out value='${status.index}' />.catalogProductCharges<c:out value='${priceStatus.index}' />"
                                               value='<c:out value="${catalogProductCharge.price }" />'
                                               name="catalogChargesFormList[<c:out value='${status.index}' />].catalogProductCharges[<c:out value='${priceStatus.index}' />].price"
                                               class="text priceRequired j_pricerequired">
                                      </c:if>
                                    </div>
                                  </div>
                              </c:forEach>
                            </c:forEach>
                          </div>
                        </c:forEach>

                      </div>
                    </div>
                  </div>

                </div>
            </div>
            
        </div>
        <div id="buttons" class="widgetwizard_nextprevpanel sixstepswizard">
            <input class="widgetwizard_nextprevpanel prevbutton" type="button" onclick="addProductPrevious(this)" value="<spring:message code="label.previous.step"/>" name="<spring:message code="label.previous.step"/>" id="add_product_previous">
            <input class="widgetwizard_nextprevpanel nextbutton" type="button" data-primary onclick="addProductNext(this)" value="<spring:message code="label.next.step"/>" name="<spring:message code="label.next.step"/>" id="add_product_next">
            <a href="javascript:void(0);" class="cancel close_product_wizard" ><spring:message code="label.cancel" /></a>
        </div>
    </div>
    <!--step 5 ends here-->

     <!--step 6 starts here-->
  <div id="step6" class="j_productspopup" style="display:none;">
  <input type="hidden" id="nextstep" name="nextstep" value="step7" >
  <input type="hidden"  id="prevstep"name="prevstep" value="step5" >

        <div class="widgetwizard_stepsbox">
            <div class="widgetwizard_steps_contentcontainer sixstepswizard">
                <div class="widgetwizard_stepscenterbar sixstepswizard">
                    <ul>
                        <li class="widgetwizard_stepscenterbar sevenStepsWizard first"><span class="steps completedsteps"><span class="stepsnumbers">1</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.product.step1.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sevenStepsWizard"><span class="steps completedsteps"><span class="stepsnumbers">2</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.product.step2.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sevenStepsWizard"><span class="steps completedsteps"><span class="stepsnumbers">3</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.product.step3.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sevenStepsWizard"><span class="steps completedsteps"><span class="stepsnumbers">4</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.product.conversionFactor.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sevenStepsWizard"><span class="steps completedsteps"><span class="stepsnumbers">5</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.product.add.charges.step.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sevenStepsWizard"><span class="steps active"><span class="stepsnumbers active">6</span></span><span class="stepstitle active"><spring:message htmlEscape="false" code="ui.product.step6.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sevenStepsWizard last"><span class="steps last"><span class="stepsnumbers last">7</span></span><span class="stepstitle last"><spring:message htmlEscape="false" code="ui.product.step5.title" /></span></li>                    </ul>
                </div>
            </div>
        </div>

        <div class="widgetwizard_projectbc">
            <span class="breadcrumbs ellipsis"></span>
        </div>

        <div class="widgetwizard_contentarea sixstepswizard" style="margin-top:0;">
            <div class="widgetwizard_boxes fullheight sixstepswizard">
                <div class="widgetwizard_titleboxes">
                    <h2><spring:message code="ui.product.review.confirm.title" htmlEscape="false"></spring:message></h2>
                    <span style="width:100%"><spring:message code="ui.product.review.confirm.title.desc"/></span>
                </div>
                <div class="widgetwizard_reviewbox sixstepswizard">
                  <ul>
                      <li>
                            <span class="label"><spring:message code="ui.label.service.sub.title"/></span>
                            <span class="description" id="productServiceName"></span>
                        </li>

                       <li id="confirmProductDetails">
                          <span class="label"><spring:message code="ui.product.step3.title" />:</span>
                            <span class="description j_description"></span>
                            <span class="edit" style="margin-right:60px"><a class="confirm_edit_link" id="backtoproductdetails" href="javascript:void(0);"><spring:message code="label.edit"/></a></span>
                            <ul> 
                            <li class="subselection" >
                                  <span class="label sublabel"><spring:message code="label.name"/>  </span>
                                  <span class="description subdescription ellipsis" id="name"></span>
                            </li>
                              <li class="subselection" >
                                  <span class="label sublabel"><spring:message code="ui.products.label.create.product.code"/>  </span>
                                  <span class="description subdescription ellipsis" id="code"></span>
                            </li>
                             <li class="subselection" >
                                  <span class="label sublabel"><spring:message code="ui.products.label.create.product.category"/>  </span>
                                  <span class="description subdescription ellipsis" id="product_category"></span>
                            </li>
                            </ul>
                        </li>
                         
                        <li id="confirmProductDetails">
                          <span class="label"><spring:message code="label.charges" /></span>
                            <span class="description j_description"></span>
                            <span class="edit" style="margin-right:60px"><a class="confirm_edit_link" id="backtoaddcharges" href="javascript:void(0);"><spring:message code="label.edit"/></a></span>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
        <div id="buttons" class="widgetwizard_nextprevpanel sixstepswizard">
            <input class="widgetwizard_nextprevpanel prevbutton" type="button" onclick="addProductPrevious(this)" value="<spring:message code="label.previous.step"/>" name="<spring:message code="label.previous.step"/>" id="add_product_previous">
            <input class="widgetwizard_nextprevpanel nextbutton" type="button" data-primary onclick="addProductNext(this)" value="<spring:message code="label.add"/>" name="<spring:message code="label.add"/>" id="add_product_next">
                <a href="javascript:void(0);" class="cancel close_product_wizard" ><spring:message code="label.cancel" /></a>
        </div>
    </div>
    <!--step 6 ends here-->
    
     <!--step 7 starts here-->
  <div id="step7" class="j_productspopup" style="display:none;">
  <input type="hidden" id="nextstep" name="nextstep" value="" >
    <input type="hidden" id="prevstep" name="prevstep" value="step6" >
        <div class="widgetwizard_stepsbox">
        <div class="widgetwizard_steps_contentcontainer sixstepswizard">
            <div class="widgetwizard_stepscenterbar sixstepswizard">
                    <ul>
                        <li class="widgetwizard_stepscenterbar sevenStepsWizard first"><span class="steps completedsteps"><span class="stepsnumbers">1</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.product.step1.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sevenStepsWizard"><span class="steps completedsteps"><span class="stepsnumbers">2</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.product.step2.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sevenStepsWizard"><span class="steps completedsteps"><span class="stepsnumbers">3</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.product.step3.title" /></span></li>
                    <li class="widgetwizard_stepscenterbar sevenStepsWizard"><span class="steps completedsteps"><span class="stepsnumbers">4</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.product.conversionFactor.title" /></span></li>
                    <li class="widgetwizard_stepscenterbar sevenStepsWizard"><span class="steps completedsteps"><span class="stepsnumbers">5</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.product.add.charges.step.title" /></span></li>
                    <li class="widgetwizard_stepscenterbar sevenStepsWizard"><span class="steps completedsteps"><span class="stepsnumbers">6</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.product.step6.title" /></span></li>
                    <li class="widgetwizard_stepscenterbar sevenStepsWizard last"><span class="steps last active"><span class="stepsnumbers last">7</span></span><span class="stepstitle last"><spring:message htmlEscape="false" code="ui.product.step5.title" /></span></li>
                    </ul>
                </div>
            </div>
        </div>
    <div class="widgetwizard_contentarea sixstepswizard">
        <div class="widgetwizard_boxes fullheight sixstepswizard">
                <div class="widgetwizard_successbox">
                  <div class="widgetwizard_successbox">
                      <div class="widget_resulticon success"></div>
                        <p id="successmessage"><spring:message htmlEscape="false" code="ui.product.successfully.completed.text"/>&nbsp;</p>
                        <a href="#" class="close_product_wizard" id="viewproductdetails_configure"><spring:message htmlEscape="false" code="ui.product.view.details.configure.text"/></a>
                    </div>
                </div>
                <div class="infomessage" style="margin:10px auto 0;float:none;text-align:center;">
                   <c:choose>
                      <c:when test="${date != null}">
                          <spring:message code="dateonly.short.format" var="dateonly_format"/>
                          <fmt:formatDate var="displayDate" value="${date}" pattern="${dateonly_format}"/>
                          <spring:message code="ui.add.product.success.page.note.with.current.ratecard" htmlEscape="false" arguments="${displayDate}"></spring:message>
                      </c:when>
                      <c:otherwise>
                          <spring:message code="ui.add.product.success.page.note.future.plan.date.undefined"/>
                      </c:otherwise>
                    </c:choose>
                 </div>
            </div>
        </div>
    <div id="buttons" class="widgetwizard_nextprevpanel sixstepswizard">
            <input class="widgetwizard_nextprevpanel submitbutton" type="button" data-primary onclick="addProductNext(this)" value="<spring:message code="label.close"/>" name="Close" id="add_product_next">
        </div>
    </div>
    <!--step 7 ends here-->
    
</div>

</form:form>
</div>

<li class="subselection" id="confirmoffering" style="display: none; background:none">
    <span class="description subdescription j_subdescription" style="font-weight: bold; margin : 0 0 0"></span>
</li>

<div id="spinning_wheel_cp" style="display:none;">
   <div class="widget_blackoverlay widget_rightpanel">
   </div>
   <div class="widget_loadingbox fullpage">
     <div class="widget_loaderbox">
       <span class="bigloader"></span>
     </div>
     <div class="widget_loadertext">
       <p id="in_process_text"><spring:message code="label.loading"/> &hellip;</p>
     </div>
   </div>
</div>

<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<jsp:include page="js_messages.jsp"></jsp:include>

<script type="text/javascript">
  var currentstep ="";
  var $currentDialog="";
  var product_action="";//create/edit
  var productsUrl = "<%=request.getContextPath()%>/portal/products/";
  currentstep = "step1";
  product_action="edit";
  $("#dialog_add_product").empty();
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

  l10discAndUsageTypeNames["INCLUDES"] = '<spring:message javaScriptEscape="true" code="ui.label.includes"/>';
  l10discAndUsageTypeNames["EXCLUDES"] = '<spring:message javaScriptEscape="true" code="ui.label.excludes"/>';

</script>

<script type="text/javascript" src="<%=request.getContextPath() %>/js/products.js"></script>

<div class="dialog_formcontent wizard" >
 <spring:url value="/portal/products/editproduct" var="edit_product_path" htmlEscape="false" /> 
 <form:form commandName="productForm" cssClass="ajaxform" id="productForm"  action="${edit_product_path}" onsubmit="editProduct(event,this)">
<!--  Add new Product starts here-->
<div class="widget_wizardcontainer">
  <!--step 1 starts here-->
  <div id="step1"  class="j_productspopup">
<input type="hidden" id="nextstep" name="nextstep" value="step2" >
  <input type="hidden" id="prevstep" name="prevstep" value="" >
        <div class="widgetwizard_stepsbox">
            <div class="widgetwizard_steps_contentcontainer">
                <div class="widgetwizard_stepscenterbar">
                    <ul>
                        <li class="widgetwizard_stepscenterbar sixStepsWizard first"><span class="steps active"><span class="stepsnumbers active">1</span></span><span class="stepstitle active"><spring:message htmlEscape="false" code="ui.product.step1.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sixStepsWizard"><span class="steps"><span class="stepsnumbers">2</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.product.step2.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sixStepsWizard"><span class="steps"><span class="stepsnumbers">3</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.product.step3.title" /> </span></li>
                        <li class="widgetwizard_stepscenterbar sixStepsWizard"><span class="steps"><span class="stepsnumbers">4</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.product.conversionFactor.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sixStepsWizard"><span class="steps"><span class="stepsnumbers">5</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.product.step4.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sixStepsWizard last"><span class="steps last"><span class="stepsnumbers last">6</span></span><span class="stepstitle last"><spring:message htmlEscape="false" code="ui.product.step5.title" /></span></li>                    </ul>
                </div>
            </div>
        </div>

        <div class="widgetwizard_projectbc">
            <span class="breadcrumbs ellipsis"></span>
        </div>

        <div class="widgetwizard_contentarea fivestepswizard" style="margin-top:0;">
          <div class="widgetwizard_boxes fullheight fivestepswizard">

            <div class="widgetwizard_titleboxes">
              <h2>
                <spring:message code="ui.product.step1.title" />
              </h2>
              <span><spring:message code="ui.product.deteailsstep.desc" /></span>
            </div>

            <div class="widgetwizard_detailsbox fivestepswizard">
              <ul>

                <li id="productname"><span class="label"><spring:message
                      code="ui.products.label.create.name" /></span>
                  <div class="mandatory_wrapper">
                    <form:input cssClass="text" path="product.name" tabindex="1" maxlength="255"/>
                  </div>
                  <div class="main_addnew_formbox_errormsg_popup" id="product.nameError"></div>
                </li>

                <li id="productcode"><span class="label"><spring:message
                      code="ui.products.label.create.product.code" /></span>
                  <div class="mandatory_wrapper">
                    <form:input cssClass="text" path="product.code" tabindex="2" maxlength="64"/>
                  </div>
                  <input id="product_code" type="hidden" value="<c:out  value="${productForm.product.code}" />" />
                  <div class="main_addnew_formbox_errormsg_popup" id="product.codeError"></div>
                </li>

                <li id="description" style="height:68px;"><span class="label"><spring:message
                      code="ui.products.label.create.product.description" /></span>
                  <div>
                    <form:textarea cssClass="textarea" path="product.description" tabindex="4" maxlength="4096"></form:textarea>
                  </div>
                </li>
                
                <li id="productCategory" style="height:38px;"><span class="label"><spring:message
                      code="ui.products.label.create.product.category" /></span>
                  <div class="mandatory_wrapper">
                  <form:select tabindex="6" path="categoryID" title="${i18nCreditCardCountry}" cssClass="select">
                    <c:forEach items="${categories}" var="choice" varStatus="status">
                      <option value='<c:out value="${choice.id}"/>' <c:if test="${productForm.product.category.name == choice.name}">selected="selected"</c:if> >
                        <c:out value="${choice.name}" escapeXml="false"/>
                     </option>
                    </c:forEach>
                 </form:select>
                  </div>
                  <div class="main_addnew_formbox_errormsg_popup" id="categoryError" style="margin: -8px 0 0 155px;"></div>
                </li>

              </ul>
            </div>
          </div>
        </div>
        <div id="buttons" class="widgetwizard_nextprevpanel">
            <input class="widgetwizard_nextprevpanel nextbutton" type="button" data-primary onclick="addProductNext(this)" value="<spring:message code="label.next.step"/>" name="<spring:message code="label.next.step"/>" id="add_product_next">
                 <a href="javascript:void(0);" class="cancel close_product_wizard" ><spring:message code="label.cancel" /></a>
        </div>
    </div>
    <!--step 1 ends here-->
    
    
   <!--step 2 starts here-->
  <div id="step2" class="j_productspopup" style="display:none;">
  <input type="hidden" id="nextstep" name="nextstep" value="step3" >
  <input type="hidden" id="prevstep" name="prevstep" value="step1" >
  <input type="hidden" id="mediationRulesPopulated" name="mediationRulesPopulated" value="false" >
  <input type="hidden" id="jsonMediationRuleMap" name="jsonMediationRuleMap" value='<c:out value="${jsonMediationRuleMap}"/>' >
  <input type="hidden" id="jsonUsageTypeDiscriminatorMap" name="jsonUsageTypeDiscriminatorMap" value='<c:out value="${jsonUsageTypeDiscriminatorMap}"/>' >
  <form:input type="hidden" path="productMediationRules" id="productmediationrules"/>
        <div class="widgetwizard_stepsbox">
            <div class="widgetwizard_steps_contentcontainer">
                <div class="widgetwizard_stepscenterbar">
                    <ul>
                        <li class="widgetwizard_stepscenterbar sixStepsWizard first"><span class="steps completedsteps"><span class="stepsnumbers">1</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.product.step1.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sixStepsWizard"><span class="steps active"><span class="stepsnumbers active">2</span></span><span class="stepstitle active"><spring:message htmlEscape="false" code="ui.product.step2.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sixStepsWizard"><span class="steps"><span class="stepsnumbers">3</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.product.step3.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sixStepsWizard"><span class="steps"><span class="stepsnumbers">4</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.product.conversionFactor.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sixStepsWizard"><span class="steps"><span class="stepsnumbers">5</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.product.step4.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sixStepsWizard last"><span class="steps last"><span class="stepsnumbers last">6</span></span><span class="stepstitle last"><spring:message htmlEscape="false" code="ui.product.step5.title" /></span></li>                    </ul>
                </div>
            </div>
        </div>

        <div class="widgetwizard_projectbc">
            <span class="breadcrumbs ellipsis"></span>
        </div>

        <div class="widgetwizard_contentarea fivestepswizard" style="margin-top:0;">
          <div class="widgetwizard_boxes fullheight fivestepswizard">
            <div class="widgetwizard_titleboxes">
              <h2><spring:message htmlEscape="false" code="ui.label.product.add.usage.handling.step.tiltle" /></h2>
              <span><spring:message htmlEscape="false" code="ui.label.product.add.usage.handling.step.tiltle.desc" /></span>
            </div>
            <div class="widgetwizard_detailsbox sixstepswizard gridbox" id="mediationRules" style="width: 685px">
              <div class="row header">
                <div class="gridcell header" style="width: 26%;">
                  <span class="gridtext header"><spring:message htmlEscape="false" code="ui.label.usage.type" /></span>
                </div>
                <div class="gridcell header" style="width: 40%;">
                  <span class="gridtext header"><spring:message htmlEscape="false" code="ui.label.generates.usage" /></span>
                </div>
                <div class="gridcell header" style="width: 28%;">
                  <span class="gridtext header"><spring:message htmlEscape="false" code="ui.label.aggregation.handling" /></span>
                </div>
              </div>
              <%-- row template for usage type --%>
              <div class="row highlighted" id="usageTypeAdd" style="display:none">
                <div class="gridcell" style="width: 26%;">
                  <span class="gridtext" id="usagetype"></span>
                </div>
                <div class="gridcell" style="width: 40%;">
                  <span class="gridtext" id="uom"></span>
                </div>
                <div class="gridcell" style="width: 28%;">
                  <span class="gridtext" id="operator"></span>
                </div>
              </div>
            </div>

          </div>
        </div>
         <div class="main_addnew_formbox_errormsg_popup" id="productItemsError"></div>
        <div id="buttons" class="widgetwizard_nextprevpanel">
            <input class="widgetwizard_nextprevpanel prevbutton" type="button" onclick="editProductPrevious(this)" value="<spring:message code="label.previous.step"/>" name="<spring:message code="label.previous.step"/>" id="add_product_previous">
            <input class="widgetwizard_nextprevpanel nextbutton" type="button" data-primary onclick="addProductNext(this)" value="<spring:message code="label.next.step"/>" name="<spring:message code="label.next.step"/>" id="add_product_next">
                 <a href="javascript:void(0);" class="cancel close_product_wizard" ><spring:message code="label.cancel" /></a>
        </div>
    </div>
    <!--step 2 ends here-->
    
     <!--step 3 starts here-->
  <div id="step3" class="j_productspopup" style="display:none">
  <input type="hidden" id="nextstep" name="nextstep" value="step4" >
  <input type="hidden" id="prevstep" name="prevstep" value="step2" >
        <div class="widgetwizard_stepsbox">
            <div class="widgetwizard_steps_contentcontainer">
                <div class="widgetwizard_stepscenterbar">
                    <ul>
                        <li class="widgetwizard_stepscenterbar sixStepsWizard first"><span class="steps completedsteps"><span class="stepsnumbers">1</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.product.step1.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sixStepsWizard"><span class="steps completedsteps"><span class="stepsnumbers">2</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.product.step2.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sixStepsWizard"><span class="steps active"><span class="stepsnumbers active">3</span></span><span class="stepstitle active"><spring:message htmlEscape="false" code="ui.product.step3.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sixStepsWizard"><span class="steps"><span class="stepsnumbers">4</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.product.conversionFactor.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sixStepsWizard"><span class="steps"><span class="stepsnumbers">5</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.product.step4.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sixStepsWizard last"><span class="steps last"><span class="stepsnumbers last">6</span></span><span class="stepstitle last"><spring:message htmlEscape="false" code="ui.product.step5.title" /></span></li>                    </ul>
                </div>
            </div>
        </div>

        <div class="widgetwizard_projectbc">
            <span class="breadcrumbs ellipsis"></span>
        </div>

        <div class="widgetwizard_contentarea fivestepswizard" style="margin-top:0;">
            <div class="widgetwizard_boxes fullheight fivestepswizard">
                <div class="widgetwizard_titleboxes">
                    <h2><spring:message htmlEscape="false" code="ui.label.product.add.advanced.mediation.step.tiltle" /></h2>
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

                <div class="widgetwizard_detailsbox sixstepswizard gridbox griddescriptionbox" style="width:475px;'">
                    <div class="row header">
                      <div class="gridcell header" style="width:50%;">
                          <span class="gridtext header"><spring:message htmlEscape="false" code="ui.label.collect.usage.where" /></span>
                        </div>
                    </div>
                    <div id="mediationRuleDiscriminators">

                      <div class="row highlighted" id="discriminatorRowForAlreadyAddedDisc" style="display:none" alreadyAdded="true">
                          <div class="gridcell" style="width:5%;">
                            <span class="gridtext"><a class="vm_tooltip" href="#"></a></span>
                          </div>

                          <div class="gridcell" style="width: 30%;">
                            <span class="gridtext" id="discriminatorName"></span>
                          </div>

                          <div class="gridcell" style="width: 30%;">
                            <span class="gridtext" id="operator"></span>
                          </div>

                          <div class="gridcell" style="width: 20%;">
                            <span class="gridtext" id="discValue"></span>
                          </div>

                          <div style="width:10%;" class="gridcell">
                              <span class="gridtext"><a href="javascript:none;" class="delete"></a></span>
                          </div>
                      </div>

                      <div id="usagetype_discriminator_block_template" style="display:none" alreadyAdded="false">
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
                              <select class="select1" id ="discriminatorOperator">
                                  <option value="equals"><spring:message htmlEscape="false" code="ui.label.includes" /></option>
                                  <option value="not_equals"><spring:message htmlEscape="false" code="ui.label.excludes" /></option>
                               </select>
                            </div>
  
                          <div class="gridcell" style="width:20%; display:none;" id="grid_cell_discvalue_selectbox">
                            <select class="select1 select_desc_name_class" id="discvalue_selectbox">
                              <option value="">
                                <spring:message htmlEscape="false" code="label.choose" />
                              </option>
                            </select> 
                          </div>
  
                          <div class="gridcell" style="width:20%;display: none;" id="grid_cell_discvalue_inputbox">
                            <input class="text" id="discvalue_inputbox" />
                          </div>
  
                          <div class="gridcell" style="width:10%;">
                                <span class="gridtext"><a id="add_discriminator_link" class="add" href="javascript:void(0)" onClick="addDiscriminatorRowInEdit(this)"></a></span>
                          </div>
                        </div>
                      </div>


                    </div>
                </div>
            </div>
        </div>
        <div id="buttons" class="widgetwizard_nextprevpanel">
            <input class="widgetwizard_nextprevpanel prevbutton" type="button" onclick="editProductPrevious(this)" value="<spring:message code="label.previous.step"/>" name="<spring:message code="label.previous.step"/>" id="add_product_previous">
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
      <form:input type="hidden" path="productMediationRules" id="productmediationrules"/>

        <div class="widgetwizard_stepsbox">
            <div class="widgetwizard_steps_contentcontainer fivestepswizard">
                <div class="widgetwizard_stepscenterbar fivestepswizard">
                    <ul>
                       <li class="widgetwizard_stepscenterbar sixStepsWizard first"><span class="steps completedsteps"><span class="stepsnumbers">1</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.product.step1.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sixStepsWizard"><span class="steps completedsteps"><span class="stepsnumbers">2</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.product.step2.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sixStepsWizard"><span class="steps completedsteps"><span class="stepsnumbers">3</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.product.step3.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sixStepsWizard"><span class="steps active"><span class="stepsnumbers active">4</span></span><span class="stepstitle active"><spring:message htmlEscape="false" code="ui.product.conversionFactor.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sixStepsWizard"><span class="steps"><span class="stepsnumbers">5</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.product.step4.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sixStepsWizard last"><span class="steps last"><span class="stepsnumbers last">6</span></span><span class="stepstitle last"><spring:message htmlEscape="false" code="ui.product.step5.title" /></span></li>                    
                    </ul>
                </div>
            </div>
        </div>

        <div class="widgetwizard_projectbc">
            <span class="breadcrumbs ellipsis"></span>
        </div>

        <div class="widgetwizard_contentarea fivestepswizard" style="margin-top:0;">
          <div class="widgetwizard_boxes fullheight fivestepswizard">
            <div class="widgetwizard_titleboxes">
               <h2><spring:message htmlEscape="false" code="ui.product.conversionFactor.title" /></h2>
                    <span><spring:message htmlEscape="false" code="ui.label.product.add.conversion.factor.step.tiltle.desc" /></span>
            </div>
            <div class="widgetwizard_detailsbox sixstepswizard gridbox" style="width:688px;" id="conversionFactor">
              <div class="row header">
                <div class="gridcell header" style="width: 25%;">
                  <span class="gridtext header"><spring:message htmlEscape="false" code="ui.label.generates.usage" /></span>
                </div>
                <div class="gridcell header" style="width: 25%;">
                  <span class="gridtext header"><spring:message htmlEscape="false" code="ui.label.product.unit" /></span>
                </div>
                <div class="gridcell header" style="width: 27%;display:none" id="step4_customUnits">
                  <span class="gridtext header"><spring:message htmlEscape="false" code="ui.label.custom.product.unit" /></span>
                </div>
                <div class="gridcell header" style="width: 23%;">
                  <span class="gridtext header"><spring:message htmlEscape="false" code="ui.label.comversion.factor" /></span>
                </div>
                
                
              </div>

              <%-- row template for usage type --%>
              <div class="row highlighted" id="usageTypeAdd">
                <div class="gridcell" style="width: 25%;">
                  <span id="step4_uom" class="gridtext"></span>
                </div>
                <div class="gridcell" style="width: 25%;">
                  <span id="step4_scale" class="gridtext"></span>
                </div>
                <div class="gridcell" style="width: 27%; display: none" id="step4_customUnitsName">
                  <div>
                    <span id="step4_custom_units" class="gridtext"></span>
                  </div>
                </div>
                <div class="gridcell" style="width: 23%;">
                  <div>
                    <span disabled="disabled" class="gridtext" id="step4_conversionfactor"></span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="main_addnew_formbox_errormsg_popup" id="productMedRuleSelectError" style="margin: 5px 0 0 280px; width:500px;"></div>

        <div id="buttons" class="widgetwizard_nextprevpanel">
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
            <div class="widgetwizard_steps_contentcontainer">
                <div class="widgetwizard_stepscenterbar">
                    <ul>
                        <li class="widgetwizard_stepscenterbar sixStepsWizard first"><span class="steps completedsteps"><span class="stepsnumbers">1</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.product.step1.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sixStepsWizard"><span class="steps completedsteps"><span class="stepsnumbers">2</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.product.step2.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sixStepsWizard"><span class="steps completedsteps"><span class="stepsnumbers">3</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.product.step3.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sixStepsWizard"><span class="steps completedsteps"><span class="stepsnumbers">4</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.product.conversionFactor.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sixStepsWizard"><span class="steps active"><span class="stepsnumbers active">5</span></span><span class="stepstitle  active"><spring:message htmlEscape="false" code="ui.product.step4.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sixStepsWizard last"><span class="steps last"><span class="stepsnumbers last">6</span></span><span class="stepstitle last"><spring:message htmlEscape="false" code="ui.product.step5.title" /></span></li>                    </ul>
                </div>
            </div>
        </div>
        <div class="widgetwizard_contentarea">
            <div class="widgetwizard_boxes fullheight">
                <div class="widgetwizard_titleboxes">
                    <h2><spring:message code="ui.product.review.confirm.title" htmlEscape="false"></spring:message></h2>
                    <span><spring:message code="ui.product.review.confirm.title.desc"/></span>
                </div>
                <div class="widgetwizard_reviewbox">
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
                                  <span class="description subdescription ellipsis " title = "<c:out value="${productForm.product.name}"></c:out>"  id="name"><c:out value="${productForm.product.name}"></c:out></span>
                            </li>
                              <li class="subselection" >
                                  <span class="label sublabel"><spring:message code="ui.products.label.create.product.code"/>  </span>
                                  <span class="description subdescription ellipsis" title = "<c:out value="${productForm.product.code}"></c:out>"  id="code"><c:out value="${productForm.product.code}"></c:out></span>
                            </li>
                             <li class="subselection" >
                                  <span class="label sublabel"><spring:message code="ui.products.label.create.product.category"/>  </span>
                                  <span class="description subdescription ellipsis" title = "<c:out value="${productForm.product.category.name }"></c:out> " id="product_category"><c:out value="${productForm.product.category.name }"></c:out></span>
                            </li>
                            </ul>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
        <div id="buttons" class="widgetwizard_nextprevpanel">
            <input class="widgetwizard_nextprevpanel prevbutton" type="button" onclick="editProductPrevious(this)" value="<spring:message code="label.previous.step"/>" name="<spring:message code="label.previous.step"/>" id="add_product_previous">
            <input class="widgetwizard_nextprevpanel nextbutton" type="button" data-primary onclick="addProductNext(this)" value="<spring:message code="label.save"/>" name="<spring:message code="label.edit"/>" id="add_product_next">
                 <a href="javascript:void(0);" class="cancel close_product_wizard" ><spring:message code="label.cancel" /></a>
        </div>
    </div>
    <!--step 5 ends here-->
    
     <!--step 6 starts here-->
  <div id="step6" class="j_productspopup" style="display:none;">
  <input type="hidden" id="nextstep" name="nextstep" value="" >
  <input type="hidden" id="prevstep" name="prevstep" value="step5" >
        <div class="widgetwizard_stepsbox">
            <div class="widgetwizard_steps_contentcontainer">
                <div class="widgetwizard_stepscenterbar">
                    <ul>
                        <li class="widgetwizard_stepscenterbar sixStepsWizard first"><span class="steps completedsteps"><span class="stepsnumbers">1</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.product.step1.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sixStepsWizard"><span class="steps completedsteps"><span class="stepsnumbers">2</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.product.step2.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sixStepsWizard"><span class="steps completedsteps"><span class="stepsnumbers">3</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.product.step3.title" /></span></li>
                         <li class="widgetwizard_stepscenterbar sixStepsWizard"><span class="steps completedsteps"><span class="stepsnumbers">4</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.product.conversionFactor.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sixStepsWizard"><span class="steps completedsteps"><span class="stepsnumbers">5</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.product.step4.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar sixStepsWizard last"><span class="steps last active"><span class="stepsnumbers last">6</span></span><span class="stepstitle last"><spring:message htmlEscape="false" code="ui.product.step5.title" /></span></li>
                    </ul>
                </div>
            </div>
        </div>
        <div class="widgetwizard_contentarea">
            <div class="widgetwizard_boxes fullheight">
                <div class="widgetwizard_successbox">
                  <div class="widgetwizard_successbox">
                      <div class="widget_resulticon success"></div>
                        <p id="successmessage"><spring:message htmlEscape="false" code="ui.product.edit.successfully.completed.text"/>&nbsp;</p>
                        <a href="#" class="close_product_wizard" id="viewproductdetails_configure"><spring:message htmlEscape="false" code="ui.product.view.details.configure.text"/></a>
                    </div>
                </div>
            </div>
        </div>
        <div id="buttons" class="widgetwizard_nextprevpanel">
            <input class="widgetwizard_nextprevpanel submitbutton" type="button" data-primary onclick="addProductNext(this)" value="<spring:message code="label.close"/>" name="Close" id="add_product_next">
        </div>
    </div>
    <!--step 6 ends here-->
    
</div>

</form:form>
</div>
<li class="subselection" id="confirmoffering" style="display: none; background:none">
    <span class="label"><spring:message code="ui.product.offerings"/>  </span>
    <span class="description subdescription j_subdescription"> </span>
</li>

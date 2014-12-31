
<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="js_messages.jsp"></jsp:include>

<script type="text/javascript">
  var currentstep ="";
  var $currentDialog="";
  var bundle_action="";
  var productBundlesUrl = "<%=request.getContextPath() %>/portal/productBundles/";
  var currPrecision="<c:out value="${currencyFractionalDigitsLimit}"/>";

  function onClickOfWidgetCheckbox(current){
    if($(current).find("span").attr("class") == "unchecked") {
      $(current).find("span").removeClass('unchecked').addClass('checked');
    } else {
      operation = "remove";
      $(current).find("span").removeClass('checked').addClass('unchecked');
    }
  }

  function showComponentValues(component){
    $("#bundleComponents").find("li[id^=componentLeftPanel_]").each(function(){
      $(this).removeClass("active").addClass("nonactive");
    });
    $("#bundleComponents").find("#componentLeftPanel_"+component).toggleClass("nonactive active");

    $("#componentValues").find("div[id^=componenttypeValues]").each(function(){
      $(this).hide();
    });
    $("#componentValues").find("#componenttypeValues\\."+component).show();
  }

  function onAssociationRadioButtonClick(current){
    $(current).parent().parent().find(".discountTypeRadio").each(function(){
      $(this).removeAttr("checked");
    });
    $(current).attr("checked", "true");
  }

  currentstep = "step1";
  bundle_action = "create";
  $("#dialog_edit_bundle").empty();
  // select first bundle
  $(".j_bundle:first").click();
  $(".breadcrumbs").each(function() {
    $(this).text($("#serviceCategories").find(".on").text().trim() + 
        ' / ' +
        $("#instances").find(".instance_selected").text().trim());
  });

  var l10resourceTypeAndComponentNames = new Array();
  <c:forEach items="${serviceResourceTypesAndComponentsMap}" var="serviceResourceType">
    l10resourceTypeAndComponentNames['${serviceResourceType.key}-name']='<spring:message javaScriptEscape="true" code="${serviceName}.ResourceType.${serviceResourceType.key}.name"/>';
    <c:forEach items="${serviceResourceType.value}" var="resourceComponent">
      l10resourceTypeAndComponentNames['${resourceComponent}-name']='<spring:message javaScriptEscape="true" code="${serviceName}.ResourceType.${serviceResourceType.key}.${resourceComponent}.name"/>';
    </c:forEach>
  </c:forEach>
</script>

<script type="text/javascript" src="<%=request.getContextPath() %>/js/commonBundles.js"></script>

<div class="dialog_formcontent wizard">
<spring:url value="/portal/productBundles/create" var="create_bundle_path" htmlEscape="false" /> 
<form:form commandName="productBundleForm" id="productBundleForm" cssClass="ajaxform" action="${create_bundle_path}">
<!--  Add new Bundle starts here-->
<div class="widget_wizardcontainer sixstepswizard">
  <!--step 1 starts here-->
  <div id="step1"  class="j_bundlespopup">
    <input type="hidden" id="nextstep" name="nextstep" value="step2" >
    <input type="hidden" id="prevstep" name="prevstep" value="" >
    <form:input type="hidden" path="serviceInstanceUUID" id="serviceinstanceuuid"/>

        <div class="widgetwizard_stepsbox">
            <div class="widgetwizard_steps_contentcontainer sixstepswizard">
                <div class="widgetwizard_stepscenterbar sixstepswizard">
                    <ul>
                        <li class="widgetwizard_stepscenterbar first"><span class="steps active"><span class="stepsnumbers active">1</span></span><span class="stepstitle active"><spring:message htmlEscape="false" code="ui.bundle.step1.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps"><span class="stepsnumbers">2</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.bundle.step2.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps"><span class="stepsnumbers">3</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.bundle.step3.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps"><span class="stepsnumbers">4</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.bundle.charges.step.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps"><span class="stepsnumbers">5</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.bundle.step4.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar fivestepswizard last"><span class="steps last"><span class="stepsnumbers last">6</span></span><span class="stepstitle last"><spring:message htmlEscape="false" code="ui.bundle.step5.title" /></span></li>
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
                    <h2><spring:message code="ui.bundle.step1.title"/></h2>
                    <span><spring:message code="ui.bundle.description.step.desc"/></span>
                </div>
                <div class="widgetwizard_detailsbox sixstepswizard">
                    <ul>
                  
                        <li id="bundlename">
                              <span class="label"><spring:message code="label.name"/></span>
                            <div class="mandatory_wrapper">
                              <form:input cssClass="text" path="productBundle.name" tabindex="1" maxlength="255" />
                            </div>
                            <div class="main_addnew_formbox_errormsg_popup" id="productBundle.nameError"></div>
                        </li>
                        
                         <li id="bundlecode">
                            <span class="label"><spring:message code="ui.products.label.create.product.bundle.code"/></span>
                             <div class="mandatory_wrapper">
                              <form:input cssClass="text" path="productBundle.code" tabindex="2" maxlength="64" />
                             </div>
                               <input id="productBundle_code" type="hidden" value="<c:out  value="${productBundleForm.productBundle.code}" />"/>
                            <div class="main_addnew_formbox_errormsg_popup" id="productBundle.codeError"></div>
                        </li>                        
                         <li id="description">
                            <span class="label"><spring:message code="label.description"/></span>
                            <div class="nonmandatory_wrapper">
                              <form:textarea cssClass="textarea"   rows="3"  path="productBundle.description" tabindex="3"></form:textarea>   
                           </div>
                            <div class="main_addnew_formbox_errormsg_popup" id="productBundle.descriptionError"></div> 
                        </li>
                    </ul>
                </div>
            </div>
        </div>
        <div id="buttons" class="widgetwizard_nextprevpanel sixstepswizard">
            <input class="widgetwizard_nextprevpanel nextbutton" type="button" onclick="addBundleNext(this)" value="<spring:message code="label.next.step"/>" name="<spring:message code="label.next.step"/>" id="add_bundle_next">
            <a href="javascript:void(0);" class="cancel close_product_wizard" ><spring:message code="label.cancel" /></a>
        </div>
    </div>
    <!--step 1 ends here-->
    
    
   <!--step 2 starts here-->
  <div id="step2" class="j_bundlespopup" style="display:none;">
      <input type="hidden" id="nextstep" name="nextstep" value="step3" >
      <input type="hidden" id="prevstep" name="prevstep" value="step1" >
      <input type="hidden" id="step2AlreadyReached" name="step2AlreadyReached" value="false" >
      <input type="hidden" id="step2SelectionChanged" name="step2SelectionChanged" value="true" >
      <input type="hidden" id="skipStep3" name="skipStep3" value="false" >
      <form:input type="hidden" path="compAssociationJson" id="compAssociationJson"/>
        <div class="widgetwizard_stepsbox">
            <div class="widgetwizard_steps_contentcontainer sixstepswizard">
                <div class="widgetwizard_stepscenterbar sixstepswizard">
                 <ul>
                        <li class="widgetwizard_stepscenterbar first"><span class="steps completedsteps"><span class="stepsnumbers">1</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.bundle.step1.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps active"><span class="stepsnumbers active">2</span></span><span class="stepstitle active"><spring:message htmlEscape="false" code="ui.bundle.step2.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps"><span class="stepsnumbers">3</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.bundle.step3.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps"><span class="stepsnumbers">4</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.bundle.charges.step.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps"><span class="stepsnumbers">5</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.bundle.step4.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar fivestepswizard last"><span class="steps last"><span class="stepsnumbers last">6</span></span><span class="stepstitle last"><spring:message htmlEscape="false" code="ui.bundle.step5.title" /></span></li>
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
                    <h2><spring:message code="ui.bundle.step2.title"/></h2>
                    <span><spring:message code="ui.bundle.deteailsstep.step.desc"/></span>
                </div>
                <div class="widgetwizard_detailsbox sixstepswizard">
                    <ul>
                        <li id="resourcetype">
                              <span class="label"><spring:message code="label.resource.type"/></span>
                            <div class="mandatory_wrapper">
                              <select class="select" tabindex="5" id="resourceType" name="resourceType">
                              </select>
                            </div>
                            <div class="main_addnew_formbox_errormsg_popup" id="resourceTypeError"></div>
                        </li>
                        <li id="businessconstraint">
                              <span class="label"><spring:message code="label.bundle.business.constraint"/></span>
                            <div class="mandatory_wrapper">
                              <select class="select expandable-select" tabindex="6" id="productBundle.businessConstraint" name="productBundle.businessConstraint">
                                <option id="" value=""><spring:message code="label.choose"/></option>
                                  <c:forEach items="${constraints}" var="constraint" varStatus="status">
                                  <c:set var="constraintName" value="label.bundle.business.constraint.${constraint.name}"></c:set>
                                  <spring:message code="${constraintName}" var="constraintDisplay"/>
                                  <option value=<c:out value="${constraint}"/>>
                                    <c:out value="${constraintDisplay}"/>
                                  </option>
                                </c:forEach>
                               </select>
                            </div>
                            <div class="main_addnew_formbox_errormsg_popup" id="productBundle.businessConstraintError"></div>
                        </li>
                         <li id="chargeType">
                            <span class="label"><spring:message code="label.charge.type"/></span>
                             <div class="mandatory_wrapper">
                              <select class="select chargeTypeSelect" id="chargeType" tabindex="7" name="chargeType" >
                                <option value=""><spring:message code="label.choose"/></option>
                                <c:forEach items="${productBundleForm.chargeRecurrenceFrequencyList}" var="charge" varStatus="status">
                                  <c:set var="chargeReccurrenceCode" value="charge.type.${charge.name}"></c:set>
                                  <spring:message code="${chargeReccurrenceCode}" var="chargeReccurenceDisplay"/>
                                  <option value=<c:out value="${charge.name}"/>>
                                  <c:if test="${chargeReccurenceDisplay eq chargeReccurrenceCode}"><c:out value="${charge.displayName}"/></c:if>
                                  <c:if test="${chargeReccurenceDisplay ne chargeReccurrenceCode}"><c:out value="${chargeReccurenceDisplay}"/></c:if>
                                  </option>
                              </c:forEach>
                             </select> 
                             </div>
                            <div class="main_addnew_formbox_errormsg_popup" id="chargeTypeError"></div>
                        </li>                        
                         <li id="trialEligibility">
                            <span class="label"><spring:message code="label.bundle.create.eligible.trial"/></span>
                            <div class="nonmandatory_wrapper" >
                             <form:checkbox cssStyle="width:auto;float:left;margin:16px 0 0 10px;" tabindex="8" path="productBundle.trialEligibility" />  
                             <span style="width: 450px; margin: 15px 0pt 0pt 10px;" class="helptext">(<spring:message code="ui.bundle.trail.eligibility.text"/>)</span>
                              </div>
                        </li>
                        <li id="notificationEnabled">
                            <span class="label"><spring:message code="label.bundle.create.enable.notification"/></span>
                            <div class="nonmandatory_wrapper" >
                             <form:checkbox cssStyle="width:auto;float:left;margin:16px 0 0 10px;" tabindex="8" path="productBundle.notificationEnabled" />  
                             <span style="width: 450px; margin: 15px 0pt 0pt 10px;" class="helptext">(<spring:message code="ui.bundle.enable.notification.text"/>)</span>
                              </div>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
        <div id="buttons" class="widgetwizard_nextprevpanel sixstepswizard">
          <input class="widgetwizard_nextprevpanel prevbutton" type="button" onclick="addBundlePrevious(this)" value="<spring:message code="label.previous.step"/>" name="<spring:message code="label.previous.step"/>" id="add_bundle_previous">
          <input class="widgetwizard_nextprevpanel nextbutton" type="button" onclick="addBundleNext(this)" value="<spring:message code="label.next.step"/>" name="<spring:message code="label.next.step"/>" id="add_bundle_next">
          <a href="javascript:void(0);" class="cancel close_product_wizard" ><spring:message code="label.cancel" /></a>
        </div>
    </div>
    <!--step 2 ends here-->

    <!--step 3 starts here-->
  <div id="step3" class="j_bundlespopup" style="display:none;">
      <input type="hidden" id="nextstep" name="nextstep" value="step4" >
      <input type="hidden" id="prevstep" name="prevstep" value="step2" >
      <input type="hidden" id="step3AlreadyReached" name="step3AlreadyReached" value='false' >

      <div class="widgetwizard_stepsbox">
          <div class="widgetwizard_steps_contentcontainer sixstepswizard">
              <div class="widgetwizard_stepscenterbar sixstepswizard">
               <ul>
                      <li class="widgetwizard_stepscenterbar first"><span class="steps completedsteps"><span class="stepsnumbers ">1</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.bundle.step1.title" /></span></li>
                      <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps completedsteps"><span class="stepsnumbers">2</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.bundle.step2.title" /></span></li>
                      <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps active"><span class="stepsnumbers active">3</span></span><span class="stepstitle active"><spring:message htmlEscape="false" code="ui.bundle.step3.title" /></span></li>
                      <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps"><span class="stepsnumbers">4</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.bundle.charges.step.title" /></span></li>
                      <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps"><span class="stepsnumbers">5</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.bundle.step4.title" /></span></li>
                      <li class="widgetwizard_stepscenterbar fivestepswizard last"><span class="steps last"><span class="stepsnumbers last">6</span></span><span class="stepstitle last"><spring:message htmlEscape="false" code="ui.bundle.step5.title" /></span></li>
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
                    <h2><spring:message code="ui.bundle.step3.title"/></h2>
                    <span><spring:message code="ui.bundle.provisioning.step.desc"/></span>
                </div>

                <div class="widgetwizard_gridmenubox">
                  <div class="row header">
                    <div class="gridcell header" style="width:95%;">
                        <span class="gridtext header"><spring:message htmlEscape="false" code="ui.label.resource.component" /></span>
                    </div>
                    
                  </div>
                  <div class="widgetwizard_navigationlist">
                        <ul id="bundleComponents"></ul>
                    </div>
                </div>

                <div class="widgetwizard_detailsbox sixstepswizard gridbox griddescriptionbox">
                    <div class="row header">
                      <div class="gridcell header" style="width:100%;">
                          <span class="gridtext header"><spring:message htmlEscape="false" code="ui.label.provisioning.constraint.text" /></span>
                        </div>
                    </div>

                    <div id="componentValues" style="margin-left: 15px; margin-top: 35px;">
                      <div id="componentValuesBlock" style="display:none">
                         <div id="compAsscoiation">
                         </div>
                         <div id="compValuesList">
                         </div>
                      </div>
                    </div>
                </div>

            </div>
        </div>

        <div class="main_addnew_formbox_errormsg_popup" id="provisioningConstraintError" style="margin: 5px 0 0 280px; width:500px;"></div>

        <div id="buttons" class="widgetwizard_nextprevpanel  sixstepswizard">
            <input class="widgetwizard_nextprevpanel prevbutton" type="button" onclick="addBundlePrevious(this)" value="<spring:message code="label.previous.step"/>" name="<spring:message code="label.previous.step"/>" id="add_bundle_previous">
            <input class="widgetwizard_nextprevpanel nextbutton" type="button" onclick="addBundleNext(this)" value="<spring:message code="label.next.step"/>" name="<spring:message code="label.next.step"/>" id="add_bundle_next">
            <a href="javascript:void(0);" class="cancel close_product_wizard" ><spring:message code="label.cancel" /></a>
        </div>
 </div>
<!--step 3 ends here-->

<!--step 4 starts here-->
  <div id="step4" class="j_bundlespopup" style="display:none;">
  <input type="hidden" id="nextstep" name="nextstep" value="step5" >
  <input type="hidden"  id="prevstep"name="prevstep" value="step3" >
        <div class="widgetwizard_stepsbox">
            <div class="widgetwizard_steps_contentcontainer sixstepswizard">
                <div class="widgetwizard_stepscenterbar sixstepswizard">
                    <ul>
                        <li class="widgetwizard_stepscenterbar  first"><span class="steps completedsteps"><span class="stepsnumbers">1</span></span><span class="stepstitle "><spring:message htmlEscape="false" code="ui.bundle.step1.title" /></span></li>
                     <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps completedsteps"><span class="stepsnumbers">2</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.bundle.step2.title" /></span></li>
                     <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps completedsteps"><span class="stepsnumbers">3</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.bundle.step3.title" /></span></li>
                     <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps active"><span class="stepsnumbers active">4</span></span><span class="stepstitle active"><spring:message htmlEscape="false" code="ui.bundle.charges.step.title" /></span></li>
                     <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps"><span class="stepsnumbers">5</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.bundle.step4.title" /></span></li>
                     <li class="widgetwizard_stepscenterbar fivestepswizard last"><span class="steps last"><span class="stepsnumbers last">6</span></span><span class="stepstitle last"><spring:message htmlEscape="false" code="ui.bundle.step5.title" /></span></li>
                </div>
            </div>
        </div>

        <div class="widgetwizard_projectbc">
            <span class="breadcrumbs ellipsis"></span>
        </div>

        <div class="widgetwizard_contentarea sixstepswizard" style="overflow: hidden; margin-top:0;">
            <div class="widgetwizard_boxes fullheight sixstepswizard">
                <div class="widgetwizard_titleboxes">
                    <h2><spring:message code="ui.bundle.charges.step.title" htmlEscape="false"></spring:message></h2>
                    <span><spring:message code="ui.product.bundle.add.charges.step.title.desc"/></span>
                </div>
                <div class="widgetwizard_detailsbox sixstepswizard" style="width: auto">
                  <div class="common_messagebox error product_plan_charges_grid" style="padding:0 0 5px 0; border:1px solid #CCCCCC; display:block;display:none;">
                    <span class="erroricon"></span>
                    <p id="priceRequiredError" style="margin-top:7px;"></p>
                  </div>
                  <div class="details_lightboxformbox" style="padding-bottom: 0px; border-bottom-width: 0px;">
                    <div class="widget_details_inlinegrid product_plan_charges_grid" style="margin-left: 0px; margin-top: 0px; border-top-width: 0px;">

                      <div class="widget_grid inline subheader product_plan_charges_grid">
                        <div class="widget_grid_cell product_displayname_cell" >
                          <span class="subheader" ><spring:message code="label.charges"/></span>
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

                      <div class="widgetgrid_wrapper plangrid_lightbox product_plan_charges_grid edit_rate_card_grid" style="overflow-x: hidden; overflow-y: auto;height:auto;">

                        <div class="widget_grid inline odd product_plan_charges_innergrid" >
                           <div class="widget_grid_cell product_displayname_cell">
                             <span class="celltext ellipsis" style="font-weight:bold;width:105px; color:#0A79AC; margin:15px 0 0 5px;"><spring:message code="label.one.time"/></span>
                          </div>

                          <c:forEach var="bundleOneTimeCharge" items="${productBundleForm.bundleOneTimeCharges}" varStatus="priceStatus">
                            <div class="widget_grid_cell currency_cell" >
                              <div class="mandatory_wrapper" style="margin:5px 0 0 1px;">
                                <input style="width:60px; margin-top: 2px;"
                                       id="bundleOneTimeCharges.<c:out value='${priceStatus.index}' />" 
                                       value='<c:out value="${bundleOneTimeCharge.price }" />'
                                       name="bundleOneTimeCharges[<c:out value='${priceStatus.index}' />].price"
                                       class="text priceRequired j_pricerequired">
                              </div>
                              <div id="bundleOneTimeCharges.<c:out value='${priceStatus.index}' />Error"></div>
                            </div>
                          </c:forEach>
                        </div>

                        <div class="widget_grid inline odd product_plan_charges_innergrid" id="recurringCharges" style="display:none">
                           <div class="widget_grid_cell product_displayname_cell">
                             <span class="celltext ellipsis" style="font-weight:bold;width:105px; color:#0A79AC; margin:15px 0 0 5px;"><spring:message code="label.recurring"/></span>
                          </div>

                          <c:forEach var="bundleRecurringCharge" items="${productBundleForm.bundleRecurringCharges}" varStatus="priceStatus">
                            <div class="widget_grid_cell currency_cell" >
                              <div class="mandatory_wrapper" style="margin:5px 0 0 1px;">
                                <input style="width:60px; margin-top: 2px;"
                                       id="bundleRecurringCharges.<c:out value='${priceStatus.index}' />" 
                                       value='<c:out value="${bundleRecurringCharge.price }" />'
                                       name="bundleRecurringCharges[<c:out value='${priceStatus.index}' />].price"
                                       class="text priceRequired j_pricerequired">
                              </div>
                              <div id="bundleRecurringCharges.<c:out value='${priceStatus.index}' />Error"></div>
                            </div>
                          </c:forEach>
                        </div>

                      </div>
                    </div>
                  </div>

                </div>
            </div>
            
        </div>
        <div id="buttons" class="widgetwizard_nextprevpanel sixstepswizard">
            <input class="widgetwizard_nextprevpanel prevbutton" type="button" onclick="addBundlePrevious(this)" value="<spring:message code="label.previous.step"/>" name="<spring:message code="label.previous.step"/>" id="add_product_previous">
            <input class="widgetwizard_nextprevpanel nextbutton" type="button" data-primary onclick="addBundleNext(this)" value="<spring:message code="label.next.step"/>" name="<spring:message code="label.next.step"/>" id="add_product_next">
            <a href="javascript:void(0);" class="cancel close_product_wizard" ><spring:message code="label.cancel" /></a>
        </div>
    </div>
<!--step 4 ends here-->

<!--step 5 starts here-->
  <div id="step5" class="j_bundlespopup" style="display:none">
  <input type="hidden" id="nextstep" name="nextstep" value="step6" >
  <input type="hidden" id="prevstep" name="prevstep" value="step4" >
        <div class="widgetwizard_stepsbox">
            <div class="widgetwizard_steps_contentcontainer sixstepswizard">
                <div class="widgetwizard_stepscenterbar sixstepswizard">
                 <ul>
                     <li class="widgetwizard_stepscenterbar  first"><span class="steps completedsteps"><span class="stepsnumbers ">1</span></span><span class="stepstitle "><spring:message htmlEscape="false" code="ui.bundle.step1.title" /></span></li>
                     <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps completedsteps"><span class="stepsnumbers">2</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.bundle.step2.title" /></span></li>
                     <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps completedsteps"><span class="stepsnumbers">3</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.bundle.step3.title" /></span></li>
                     <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps completedsteps"><span class="stepsnumbers">4</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.bundle.charges.step.title" /></span></li>
                     <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps active"><span class="stepsnumbers active">5</span></span><span class="stepstitle active"><spring:message htmlEscape="false" code="ui.bundle.step4.title" /></span></li>
                     <li class="widgetwizard_stepscenterbar fivestepswizard last"><span class="steps last"><span class="stepsnumbers last">6</span></span><span class="stepstitle last"><spring:message htmlEscape="false" code="ui.bundle.step5.title" /></span></li>
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
                    <h2><spring:message code="ui.product.review.confirm.title" htmlEscape="false"></spring:message></h2>
                    <span><spring:message code="ui.bundle.review.confirm.title.desc"/></span>
                </div>
                <div class="widgetwizard_reviewbox sixstepswizard">
                  <ul>
                      
                       <li id="confirmBundleDetails">
                            <span class="label"><spring:message code="ui.bundle.step1.title" />:</span>
                            <span class="description " ></span>
                            <span class="edit" style="margin-right:60px"><a class="confirm_edit_link" id="backtobundledetails" href="javascript:void(0);"><spring:message code="label.edit"/></a></span>
                            <ul>  
                             <li class="subselection" >
                                  <span class="label sublabel"><spring:message code="label.name"/>  </span>
                                  <span class="description subdescription ellipsis" id="name" ></span>
                            </li>                          
                            <li class="subselection" >
                                  <span class="label sublabel"><spring:message code="ui.products.label.create.product.bundle.code"/>  </span>
                                  <span class="description subdescription ellipsis" id="code"></span>
                            </li>
                             <li class="subselection" >
                                  <span class="label sublabel"><spring:message code="label.description"/>  </span>
                                  <span class="description subdescription ellipsis" id="bundle_description"></span>
                            </li>
                             <li class="subselection" >
                                  <span class="label sublabel"><spring:message code="label.charge.type"/>  </span>
                                  <span class="description subdescription" id="chargefrequncy"></span>
                            </li>
                             <li class="subselection" >
                                  <span class="label sublabel"><spring:message code="label.bundle.create.eligible.trial"/>  </span>
                                  <span class="description subdescription" id="trialeligibility"></span>
                            </li>
                            <li class="subselection" >
                                  <span class="label sublabel"><spring:message code="label.bundle.create.enable.notification"/>  </span>
                                  <span class="description subdescription" id="notificationEnabled"></span>
                            </li>
                            </ul>
                        </li>
                        <li id="confirmProductDetails">
                          <span class="label"><spring:message code="label.charges" /></span>
                            <span class="description j_description"></span>
                            <span class="edit" style="margin-right:60px"><a class="confirm_edit_link" id="backtocharges" href="javascript:void(0);"><spring:message code="label.edit"/></a></span>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
        <div id="buttons" class="widgetwizard_nextprevpanel sixstepswizard">
            <input class="widgetwizard_nextprevpanel prevbutton" type="button" onclick="addBundlePrevious(this)" value="<spring:message code="label.previous.step"/>" name="<spring:message code="label.previous.step"/>" id="add_bundle_previous">
            <input class="widgetwizard_nextprevpanel nextbutton" type="button" onclick="addBundleNext(this)" value="<spring:message code="label.add"/>" name="<spring:message code="label.add"/>" id="add_bundle_next">
            <a href="javascript:void(0);" class="cancel close_product_wizard" ><spring:message code="label.cancel" /></a>
        </div>
    </div>
    <!--step 5 ends here-->

     <!--step 6 starts here-->
  <div id="step6" class="j_bundlespopup" style="display:none;">
  <input type="hidden" id="nextstep" name="nextstep" value="" >
  <input type="hidden" id="prevstep" name="prevstep" value="step5" >
        <div class="widgetwizard_stepsbox">
            <div class="widgetwizard_steps_contentcontainer sixstepswizard">
                <div class="widgetwizard_stepscenterbar sixstepswizard">
                 <ul>
                     <li class="widgetwizard_stepscenterbar  first"><span class="steps completedsteps"><span class="stepsnumbers ">1</span></span><span class="stepstitle active"><spring:message htmlEscape="false" code="ui.bundle.step1.title" /></span></li>
                     <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps completedsteps"><span class="stepsnumbers">2</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.bundle.step2.title" /></span></li>
                     <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps completedsteps"><span class="stepsnumbers">3</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.bundle.step3.title" /></span></li>
                     <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps completedsteps"><span class="stepsnumbers">4</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.bundle.charges.step.title" /></span></li>
                     <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps completedsteps"><span class="stepsnumbers">4</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.bundle.step4.title" /></span></li>
                     <li class="widgetwizard_stepscenterbar fivestepswizard last"><span class="steps last active"><span class="stepsnumbers last ">5</span></span><span class="stepstitle last"><spring:message htmlEscape="false" code="ui.bundle.step5.title" /></span></li>
                   </ul>                   
                </div>
            </div>
        </div>
        <div class="widgetwizard_contentarea sixstepswizard">
            <div class="widgetwizard_boxes fullheight sixstepswizard">
                <div class="widgetwizard_successbox">
                  <div class="widgetwizard_successbox">
                      <div class="widget_resulticon success"></div>
                        <p id="successmessage" class="width:550px;"><spring:message htmlEscape="false" code="ui.bundle.successfully.completed.text"/>&nbsp;</p>
                        <a href="#" class="close_product_wizard" id="viewbundledetails_configure"><spring:message htmlEscape="false" code="ui.product.view.details.configure.text"/></a>
                    </div>
                </div>
                <div class="infomessage" style="float: none;text-align: center;">
                   <c:choose>
                      <c:when test="${date != null}">
                          <spring:message code="dateonly.short.format" var="dateonly_format"/>
                          <fmt:formatDate var="displayDate" value="${date}" pattern="${dateonly_format}"/>
                          <spring:message code="ui.add.bundle.success.page.note.with.current.ratecard" htmlEscape="false" arguments="${displayDate}"></spring:message>
                      </c:when>
                      <c:otherwise>
                          <spring:message code="ui.add.bundle.success.page.note.future.plan.date.undefined"/>
                      </c:otherwise>
                    </c:choose>
                 </div>
            </div>
        </div>

        <div id="buttons" class="widgetwizard_nextprevpanel sixstepswizard">
            <input class="widgetwizard_nextprevpanel submitbutton" type="button" onclick="addBundleNext(this)" value="<spring:message code="label.close"/>" name="Close" id="add_bundle_next">
        </div>
    </div>
  <!--step 6 ends here-->

</div>
</form:form>
</div>

<div id="spinning_wheel_cpb" style="display:none;">
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
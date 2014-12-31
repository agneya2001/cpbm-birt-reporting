
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
  var bundle_action="";
  var productBundlesUrl = "<%=request.getContextPath() %>/portal/productBundles/";
  
  
  function onClickOfWidgetCheckbox(current){
    if($(current).find("span").attr("class") == "unchecked") {
      $(current).find("span").removeClass('unchecked').addClass('checked');
    } else {
      operation = "remove";
      $(current).find("span").removeClass('checked').addClass('unchecked');
    }
  }
  
  function onAssociationRadioButtonClick(current){
    $(current).parent().parent().find(".discountTypeRadio").each(function(){
      $(this).removeAttr("checked");
    });
    $(current).attr("checked", "true");
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

  currentstep = "step1";
  bundle_action = "edit";
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

<!--  Edit Bundle starts here -->

<div class="dialog_formcontent wizard">
  <spring:url value="/portal/productBundles/edit" var="edit_bundle_path" htmlEscape="false" /> 
  <form:form commandName="productBundleForm" id="productBundleForm" cssClass="ajaxform" action="${edit_bundle_path}">
  <input id="resourceTypeName" type="hidden" value="<c:out  value="${productBundleForm.productBundle.resourceType.id}" />"/>
  <input type="hidden" id="jsonProvisionalConstraints" name="jsonProvisionalConstraints" value='<c:out value="${jsonProvisionalConstraints}"/>' >
  <div class="widget_wizardcontainer">

<!--step 1 starts here-->
    <div id="step1"  class="j_bundlespopup">
      <input type="hidden" id="nextstep" name="nextstep" value="step2" >
        <input type="hidden" id="prevstep" name="prevstep" value="" >
          <div class="widgetwizard_stepsbox">
              <div class="widgetwizard_steps_contentcontainer">
                  <div class="widgetwizard_stepscenterbar">
                      <ul>
                          <li class="widgetwizard_stepscenterbar  first"><span class="steps active"><span class="stepsnumbers active">1</span></span><span class="stepstitle active"><spring:message htmlEscape="false" code="ui.bundle.step1.title" /></span></li>
                          <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps"><span class="stepsnumbers">2</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.bundle.step2.title" /></span></li>
                          <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps"><span class="stepsnumbers">3</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.bundle.step3.title" /></span></li>
                          <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps"><span class="stepsnumbers">4</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.bundle.step4.title" /></span></li>
                          <li class="widgetwizard_stepscenterbar fivestepswizard last"><span class="steps last"><span class="stepsnumbers last">5</span></span><span class="stepstitle last"><spring:message htmlEscape="false" code="ui.bundle.step5.title" /></span></li>
                     </ul>
                  </div>
              </div>
          </div>
  
          <div class="widgetwizard_projectbc">
              <span class="breadcrumbs ellipsis"></span>
          </div>
  
          <div class="widgetwizard_contentarea" style="margin-top:0;">
              <div class="widgetwizard_boxes fullheight">
                  <div class="widgetwizard_titleboxes">
                      <h2><spring:message code="ui.bundle.step1.title"/></h2>
                      <span><spring:message code="ui.bundle.deteailsstep.edit.desc"/></span>
                  </div>
                  <div class="widgetwizard_detailsbox">
                      <ul>
                    
                          <li id="bundlename">
                                <span class="label"><spring:message code="label.name"/></span>
                              <div class="mandatory_wrapper">
                                <form:input cssClass="text" path="productBundle.name" tabindex="1" />
                              </div>
                                <input id="productBundle_name" type="hidden" value="<c:out  value="${productBundleForm.productBundle.name}" />"/>
                              <div class="main_addnew_formbox_errormsg_popup" id="productBundle.nameError"></div>
                          </li>
                          
                           <li id="bundlecode">
                              <span class="label"><spring:message code="ui.products.label.create.product.bundle.code"/></span>
                               <div class="mandatory_wrapper">
                                <form:input cssClass="text" path="productBundle.code" tabindex="2" />
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
          <div id="buttons" class="widgetwizard_nextprevpanel">
              <input class="widgetwizard_nextprevpanel nextbutton" type="button" onclick="addBundleNext(this)" value="<spring:message code="label.next.step"/>" name="<spring:message code="label.next.step"/>" id="add_bundle_next">
                  <a href="javascript:void(0);" class="cancel close_product_wizard" ><spring:message code="label.cancel" /></a>
          </div>
    </div>
<!--step 1 ends here-->

<!--step 2 starts here-->
  <div id="step2" class="j_bundlespopup" style="display:none;">
      <form:input type="hidden" path="compAssociationJson" id="compAssociationJson"/>
      <input type="hidden" id="nextstep" name="nextstep" value="step3" >
      <input type="hidden" id="prevstep" name="prevstep" value="step1" >
      <input type="hidden" id="skipStep3" name="skipStep3" value="false" >
      <input type="hidden" id="step3EntriesFetched" name="step3EntriesFetched" value="false" >
        <div class="widgetwizard_stepsbox">
            <div class="widgetwizard_steps_contentcontainer">
                <div class="widgetwizard_stepscenterbar">
                 <ul>
                        <li class="widgetwizard_stepscenterbar  first"><span class="steps completedsteps"><span class="stepsnumbers completedsteps">1</span></span><span class="stepstitle completedsteps"><spring:message htmlEscape="false" code="ui.bundle.step1.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps active"><span class="stepsnumbers active">2</span></span><span class="stepstitle active"><spring:message htmlEscape="false" code="ui.bundle.step2.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps"><span class="stepsnumbers">3</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.bundle.step3.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps"><span class="stepsnumbers">4</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.bundle.step4.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar fivestepswizard last"><span class="steps last"><span class="stepsnumbers last">5</span></span><span class="stepstitle last"><spring:message htmlEscape="false" code="ui.bundle.step5.title" /></span></li>
                   </ul>                   
                </div>
            </div>
        </div>

        <div class="widgetwizard_projectbc">
            <span class="breadcrumbs ellipsis"></span>
        </div>

       <div class="widgetwizard_contentarea" style="margin-top:0;">
            <div class="widgetwizard_boxes fullheight">
                <div class="widgetwizard_titleboxes">
                    <h2><spring:message code="ui.bundle.step1.title"/></h2>
                    <span><spring:message code="ui.bundle.deteailsstep.edit.desc"/></span>
                </div>
                <div class="widgetwizard_detailsbox">
                    <ul>
                         <li id="chargeType">
                            <span class="label"><spring:message code="label.charge.type"/></span>
                             <span class="label"><spring:message code="charge.type.${productBundleForm.productBundle.rateCard.chargeType.name}"></spring:message></span>
                         </li>
                         <li id="trialEligibility">
                            <span class="label"><spring:message code="label.bundle.create.eligible.trial"/></span>
                            <div class="nonmandatory_wrapper" >
                             <form:checkbox  cssStyle="width:auto;float:left;margin:16px 0 0 10px;" tabindex="6" path="productBundle.trialEligibility" /> 
                              <span style="width: 450px; margin: 15px 0pt 0pt 10px;" class="helptext">(<spring:message code="ui.bundle.trail.eligibility.text"/>)</span> 
                              </div>
                        </li>
                        <li id="notificationEnabled">
                            <span class="label"><spring:message code="label.bundle.create.enable.notification"/></span>
                            <div class="nonmandatory_wrapper" >
                             <form:checkbox  cssStyle="width:auto;float:left;margin:16px 0 0 10px;" tabindex="6" path="productBundle.notificationEnabled" /> 
                              <span style="width: 450px; margin: 15px 0pt 0pt 10px;" class="helptext">(<spring:message code="ui.bundle.enable.notification.text"/>)</span> 
                              </div>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
        <div id="buttons" class="widgetwizard_nextprevpanel">
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
          <div class="widgetwizard_steps_contentcontainer fivestepswizard">
              <div class="widgetwizard_stepscenterbar fivestepswizard">
               <ul>
                        <li class="widgetwizard_stepscenterbar  first"><span class="steps completedsteps"><span class="stepsnumbers completedsteps">1</span></span><span class="stepstitle completedsteps"><spring:message htmlEscape="false" code="ui.bundle.step1.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps completedsteps"><span class="stepsnumbers completedsteps">2</span></span><span class="stepstitle completedsteps"><spring:message htmlEscape="false" code="ui.bundle.step2.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps active"><span class="stepsnumbers active">3</span></span><span class="stepstitle active"><spring:message htmlEscape="false" code="ui.bundle.step3.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps"><span class="stepsnumbers">4</span></span><span class="stepstitle"><spring:message htmlEscape="false" code="ui.bundle.step4.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar fivestepswizard last"><span class="steps last"><span class="stepsnumbers last">5</span></span><span class="stepstitle last"><spring:message htmlEscape="false" code="ui.bundle.step5.title" /></span></li>
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

                <div class="widgetwizard_detailsbox fivestepswizard gridbox griddescriptionbox">
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

        <div id="buttons" class="widgetwizard_nextprevpanel  fivestepswizard">
            <input class="widgetwizard_nextprevpanel prevbutton" type="button" onclick="addBundlePrevious(this)" value="<spring:message code="label.previous.step"/>" name="<spring:message code="label.previous.step"/>" id="add_bundle_previous">
            <input class="widgetwizard_nextprevpanel nextbutton" type="button" onclick="addBundleNext(this)" value="<spring:message code="label.next.step"/>" name="<spring:message code="label.next.step"/>" id="add_bundle_next">
            <a href="javascript:void(0);" class="cancel close_product_wizard" ><spring:message code="label.cancel" /></a>
        </div>
 </div>
<!--step 3 ends here-->

<!--step 4 starts here-->
  <div id="step4" class="j_bundlespopup" style="display:none">
    <input type="hidden" id="nextstep" name="nextstep" value="step5" >
    <input type="hidden" id="prevstep" name="prevstep" value="step3" >
      <div class="widgetwizard_stepsbox">
          <div class="widgetwizard_steps_contentcontainer">
              <div class="widgetwizard_stepscenterbar">
               <ul>
                      <li class="widgetwizard_stepscenterbar  first"><span class="steps completedsteps"><span class="stepsnumbers completedsteps">1</span></span><span class="stepstitle completedsteps"><spring:message htmlEscape="false" code="ui.bundle.step1.title" /></span></li>
                      <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps completedsteps"><span class="stepsnumbers completedsteps">2</span></span><span class="stepstitle completedsteps"><spring:message htmlEscape="false" code="ui.bundle.step2.title" /></span></li>
                      <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps completedsteps"><span class="stepsnumbers completedsteps">3</span></span><span class="stepstitle completedsteps"><spring:message htmlEscape="false" code="ui.bundle.step3.title" /></span></li>
                      <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps active"><span class="stepsnumbers active">4</span></span><span class="stepstitle active"><spring:message htmlEscape="false" code="ui.bundle.step4.title" /></span></li>
                      <li class="widgetwizard_stepscenterbar fivestepswizard last"><span class="steps last"><span class="stepsnumbers last">5</span></span><span class="stepstitle last"><spring:message htmlEscape="false" code="ui.bundle.step5.title" /></span></li>
                 </ul>
              </div>
          </div>
      </div>

      <div class="widgetwizard_projectbc">
          <span class="breadcrumbs ellipsis"></span>
      </div>

      <div class="widgetwizard_contentarea" style="margin-top:0;">
            <div class="widgetwizard_boxes fullheight">
                <div class="widgetwizard_titleboxes">
                    <h2><spring:message code="ui.product.review.confirm.title" htmlEscape="false"></spring:message></h2>
                    <span><spring:message code="ui.bundle.edit.review.confirm.title.desc"/></span>
                </div>
                <div class="widgetwizard_reviewbox">
                  <ul>
                       <li id="confirmBundleDetails">
                            <span class="label"><spring:message code="ui.bundle.step1.title" />:</span>
                            <span class="description " ></span>
                            <span class="edit" style="margin-right:60px"><a class="confirm_edit_link" id="backtobundledetails" href="javascript:void(0);"><spring:message code="label.edit"/></a></span>
                            <ul>   
                              <li class="subselection" >
                                    <span class="label sublabel"><spring:message code="label.name"/>  </span>
                                    <span class="description subdescription ellipsis" id="name"></span>
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
                                    <span class="description subdescription" id="chargefrequncy"><spring:message code="charge.type.${productBundleForm.productBundle.rateCard.chargeType.name}"></spring:message></span>
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
                    </ul>
                </div>
            </div>
        </div>
        <div id="buttons" class="widgetwizard_nextprevpanel">
            <input class="widgetwizard_nextprevpanel prevbutton" type="button" onclick="addBundlePrevious(this)" value="<spring:message code="label.previous.step"/>" name="<spring:message code="label.previous.step"/>" id="add_bundle_previous">
            <input class="widgetwizard_nextprevpanel nextbutton" type="button" onclick="addBundleNext(this)" value="<spring:message code="label.save"/>" name="<spring:message code="label.edit"/>" id="add_bundle_next">
                <a href="javascript:void(0);" class="cancel close_product_wizard" ><spring:message code="label.cancel" /></a>
        </div>
  </div>
<!--step 4 ends here-->

<!--step 5 starts here-->
  <div id="step5" class="j_bundlespopup" style="display:none;">
    <input type="hidden" id="nextstep" name="nextstep" value="" >
    <input type="hidden" id="prevstep" name="prevstep" value="step4" >
        <div class="widgetwizard_stepsbox">
            <div class="widgetwizard_steps_contentcontainer">
                <div class="widgetwizard_stepscenterbar">
                 <ul>
                        <li class="widgetwizard_stepscenterbar  first"><span class="steps completedsteps"><span class="stepsnumbers completedsteps">1</span></span><span class="stepstitle completedsteps"><spring:message htmlEscape="false" code="ui.bundle.step1.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps completedsteps"><span class="stepsnumbers completedsteps">2</span></span><span class="stepstitle completedsteps"><spring:message htmlEscape="false" code="ui.bundle.step2.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps completedsteps"><span class="stepsnumbers completedsteps">3</span></span><span class="stepstitle completedsteps"><spring:message htmlEscape="false" code="ui.bundle.step3.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar fivestepswizard"><span class="steps completedsteps"><span class="stepsnumbers completedsteps">4</span></span><span class="stepstitle completedsteps"><spring:message htmlEscape="false" code="ui.bundle.step4.title" /></span></li>
                        <li class="widgetwizard_stepscenterbar fivestepswizard last"><span class="steps last active"><span class="stepsnumbers last">5</span></span><span class="stepstitle last"><spring:message htmlEscape="false" code="ui.bundle.step5.title" /></span></li>
                   </ul>
                </div>
            </div>
        </div>
        <div class="widgetwizard_contentarea">
            <div class="widgetwizard_boxes fullheight">
                <div class="widgetwizard_successbox">
                  <div class="widgetwizard_successbox">
                      <div class="widget_resulticon success"></div>
                        <p id="successmessage"><spring:message htmlEscape="false" code="ui.bundle.edit.successfully.completed.text"/>&nbsp;</p>
                        <a href="#" class="close_product_wizard" id="viewbundledetails_configure"><spring:message htmlEscape="false" code="ui.product.view.details.configure.text"/></a>
                    </div>
                </div>
            </div>
        </div>
        <div id="buttons" class="widgetwizard_nextprevpanel">
            <input class="widgetwizard_nextprevpanel submitbutton" type="button" onclick="addBundleNext(this)" value="<spring:message code="label.close"/>" name="Close" id="add_bundle_next">
        </div>
    </div>
    <!--step 5 ends here-->
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
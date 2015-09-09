<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<script type="text/javascript">
var productBundlesUrl = "<%=request.getContextPath() %>/portal/productBundles/";
</script>                      

<div class="widget_actionbar">
    <div class="widget_actionarea" id="top_actions" >
       <div id="spinning_wheel" style="display:none;">
           <div class="widget_blackoverlay widget_rightpanel">
           </div>
           <div class="widget_loadingbox widget_rightpanel">
             <div class="widget_loaderbox">
               <span class="bigloader"></span>
             </div>
             <div class="widget_loadertext">
               <p id="in_process_text"><spring:message code="label.loading"/> &hellip;</p>
             </div>
           </div>
     </div>
     <c:if test="${whichPlan == 'planned'}">
       <div class="widget_moreactions action_menu_container" title="<spring:message code='manage'/>">
          <!--Actions popover starts here-->
          <div class="widget_actionpopover" id="action_menu"  style="display:none;">
              <div class="widget_actionpopover_top"></div>
                <div class="widget_actionpopover_mid">
                  <ul class="widget_actionpoplist">
                    <li id="<c:out value="edit${productBundle.id}"/>" onclick="editProductBundleGet(this)"><a href="javascript:void(0);" >
                      <spring:message code="ui.products.label.view.edit"/></a>
                    </li>
                    <c:choose>
                      <c:when test="${productBundle.publish == false}">
                        <li  onclick="publishBundle(this, 'true', '<c:out value="${productBundle.id}"/>', '<c:out value="${productBundle.code}"/>');">
                            <a href="javascript:void(0);"><spring:message code="label.bundle.create.publish"/></a>
                        </li>  
                      </c:when>
                      <c:otherwise>
                        <li  onclick="publishBundle(this,'false', '<c:out value="${productBundle.id}"/>', '<c:out value="${productBundle.code}"/>')">
                            <a href="javascript:void(0);"><spring:message code="label.bundle.create.unpublish"/></a>
                        </li>  
                      </c:otherwise>
                    </c:choose>

                  </ul>
                </div>
                <div class="widget_actionpopover_bot"></div>
            </div>
            <!--Actions popover ends here-->
       </div>
     </c:if>
   </div>
</div>

<div class="top_notifications">
  <div id="top_message_panel" class="common_messagebox widget" style="display:none;">
    <button type="button" class="close js_close_parent" >&times;</button>
    <span id="status_icon"></span><p id="msg"></p>
  </div>
  <div id="action_result_panel" class="common_messagebox widget" style="display:none;">
    <button type="button" class="close js_close_parent" >&times;</button>
    <span id="status_icon"></span><p id="msg"></p>
  </div>
</div>

<div class="widget_browser">
  <div id="spinning_wheel" style="display:none">
    <div class="widget_loadingpanel">
    </div>
    <div class="maindetails_footer_loadingbox first">
      <div class="maindetails_footer_loadingicon"></div>
      <p id="in_process_text"></p>
    </div>
  </div>
  <div class="widget_browsermaster">
      <div class="widget_browser_contentarea">
          <div class="widget_browsergrid_wrapper master">
                                      
                <div class="widget_grid master even first">
                    <div class="widget_grid_labels">
                        <span><spring:message code="ui.products.label.create.name"/></span>
                    </div>
                    <div class="widget_grid_description">
                        <span class = "ellipsis" title = "<c:out value = "${productBundle.name}"></c:out>"  id="productname"><c:out value="${productBundle.name}"></c:out></span>
                    </div>
                </div>

                <div class="widget_grid master even">
                    <div class="widget_grid_labels">
                        <span><spring:message code="ui.products.label.create.product.bundle.code"/></span>
                    </div>
                    <div class="widget_grid_description">
                        <span class = "ellipsis" title = "<c:out value = "${productBundle.code}"></c:out>"  id="productcode"><c:out value="${productBundle.code}"></c:out></span>
                    </div>
                </div> 
                <div class="widget_grid master even">
                    <div class="widget_grid_labels">
                        <span><spring:message code="label.bundle.type"/></span>
                    </div>
						          <c:set var="bundleTypeClass" value="computebundles"></c:set>
                    <div class="widget_grid_description">
                    <span class='navicon <c:out value="${bundleTypeClass}"/>' id="details_nav_icon" style="height: 16px;margin-top:5px;"></span>
                        <span style="width:175px;" id="bundletype">
                          <c:set var="currentServiceName" value="${productBundle.serviceInstanceId.service.serviceName}"></c:set>
                          <c:choose>
                            <c:when test="${empty productBundle.resourceType}">
                              <spring:message code="bundle.type.service"/>
                            </c:when>
                            <c:otherwise>
                              <spring:message code="${currentServiceName}.ResourceType.${productBundle.resourceType.resourceTypeName}.name"/>
                            </c:otherwise>
                          </c:choose>
                        </span>
                    </div>
                </div> 

            </div>
            <div class="widget_masterbigicons defaultbox">
              <div class="thumbnail_defaultcontainer">
              <div class="thumbnail_defaulticon product">
              <c:choose>
                <c:when test="${not empty productBundle.imagePath}">
                <img src="/portal/portal/logo/productBundles/<c:out value="${productBundle.id}"/>" id="bundleimage<c:out value="${productBundle.id}"/>" style="height:99px;width:99px;" />
                </c:when>
                <c:otherwise>
                  <img src="<%=request.getContextPath() %>/images/default_productsicon.png" id="bundleimage<c:out value="${productBundle.id}"/>" style="height:99px;width:99px;" />
                </c:otherwise>
              </c:choose>
              </div>
              </div>
              <div class="widget_masterbigicons_linkbox default"><a href="javascript:void(0);"  onclick="editBundleImageGet(this,<c:out value="${productBundle.id}" />)" class=" default editBundleLogo"><spring:message code="ui.products.label.view.editimage"/></a></div>
            </div>            
     </div>
  </div>

  <div class="widget_browser_contentarea">
    <ul class="widgets_detailstab">
        <li class="widgets_detailstab active" id="details_tab"><spring:message code="label.details"/></li>
        <li class="widgets_detailstab nonactive" onclick="viewBundleEntitlements(this);"   id="entitlements_tab"><spring:message code="ui.label.product.entitlements"/></li>
        <li class="widgets_detailstab nonactive" onclick="viewBundlePricing(this, false, 4);"  id="bundlepricing_tab"><spring:message code="ui.label.bundle.pricing"/></li>
        <li class="widgets_detailstab nonactive" onclick="viewBundleChannelPricing(this, false, 4);"  id="channelpricing_tab"><spring:message code="ui.label.channel.pricing"/></li>
        <li class="widgets_detailstab nonactive" onclick="viewProvisioningConstraints(this, false);"  id="provisioningconstraints_tab"><spring:message code="ui.label.provisioning.constraints"/></li>
    </ul>

            <div class="widget_browsergrid_wrapper details" id="details_content">
                <div class="widget_details_actionbox">
              <ul class="widget_detail_actionpanel" style="float:left;">
              </ul>
            </div>

                 <div class="widget_grid details even">
                    <div class="widget_grid_labels">
                        <span><spring:message code="ui.products.label.create.listorder"/></span>
                    </div>
                    <div class="widget_grid_description">
                        <span id="bundle_sortOder"><c:out value="${productBundle.sortOrder}"></c:out></span>
                    </div>
                </div>
             <div class="widget_grid details odd">
                <div class="widget_grid_labels">
                    <span><spring:message code="label.charge.type"/></span>
                </div>
                <div class="widget_grid_description">
                    <span id="bundle_charge_type"><spring:message code="charge.type.${productBundle.rateCard.chargeType.name}"/>
                    </span>
                </div>
            </div>
               <div class="widget_grid details even">
                <div class="widget_grid_labels">
                    <span><spring:message code="ui.products.label.create.description"/></span>
                </div>
                <div class="widget_grid_description">
                    <span class = "ellipsis" title = "<c:out value = "${productBundle.description}"></c:out>"  id="bundle_description"><c:out value="${productBundle.description}"></c:out></span>
                </div>
          </div>    

          <div class="widget_grid details odd">
                <div class="widget_grid_labels">
                    <span><spring:message code="label.bundle.create.published"/></span>
                </div>
                <div class="widget_grid_description">
                    <span id="bundle_publish"><spring:message code="label.${productBundle.publish}"/></span>
                </div>
          </div> 

          <div class="widget_grid details even">
                <div class="widget_grid_labels">
                    <span><spring:message code="label.bundle.create.eligible.trial"/></span>
                </div>
                <div class="widget_grid_description" >
                    <span id="bundle_trialEligibility"><spring:message code="label.${productBundle.trialEligibility}"/></span>
                </div>
         </div>
         <div class="widget_grid details even">
                <div class="widget_grid_labels">
                    <span><spring:message code="label.bundle.create.notification.enabled"/></span>
                </div>
                <div class="widget_grid_description" >
                    <span id="bundle_notificationEnabled"><spring:message code="label.${productBundle.notificationEnabled}"/></span>
                </div>
         </div>

          <div class="widget_grid details odd">
                <div class="widget_grid_labels">
                    <span><spring:message code="label.bundle.business.constraint"/></span>
                </div>
                <div class="widget_grid_description" >
                    <c:set var="constraintName" value="label.bundle.business.constraint.${productBundle.businessConstraint.name}"></c:set>
                    <span id="bundle_businessConstraint"><spring:message code="${constraintName}"/></span>
                </div>
         </div>
     </div>
    <div id="tab_spinning_wheel" class="rightpanel_mainloader_panel" style="display: none;">
      <input type="hidden" value="<c:out value='${productBundle.code}'/>" id="bundleCode"/>
      <input type="hidden" id="bundleId" value="<c:out value="${productBundle.id}"/>"/>
        <div class="rightpanel_mainloaderbox">
            <div class="rightpanel_mainloader_animatedicon">
            </div>
            <p>
              <spring:message code="label.loading"/> &hellip;
            </p>
        </div>
      </div> 
      <div class="widget_browsergrid_wrapper details" id="entitlements_content" style="display: none; height: 450px;">
        
      </div>
        <div class="widget_browsergrid_wrapper " id="bundlepricing_content" style="overflow:hidden; display: none;">
        
      </div>
      <div class="widget_browsergrid_wrapper" id="channelpricing_content" style="display: none;">

      </div>
      <div class="widget_browsergrid_wrapper" id="provisioningconstraint_content" style="display: none;">

      </div>
    </div>
   </div>

 <input type="hidden" value="<c:out value='${productBundle.code}'/>" id="bundleCode"/>
 <div  id="dialog_edit_bundle_image" title='<spring:message code="title.custom.budle.image"/>' style="display: none">
  </div> 
    
  <div  id="dialog_add_bundle_default_price" title='<spring:message code="label.add.current.charges"/>' style="display: none">
  </div> 
  <div  id="dialog_edit_bundle" title="<spring:message code='label.bundle.edit.product.bundle'/>" style="display: none">
  </div>
  <div id="dialog_view_bundle_pricing" title='<spring:message code="ui.label.bundle.pricing"/>' style="display: none">
  </div>
  <div id="dialog_view_channle_bundle_pricing" title='<spring:message code="ui.label.channel.pricing"/>' style="display: none">
  </div>
	<div id="dialog_view_provisioningconstraint_content" title='<spring:message code="ui.label.provisioning.constraints"/>' style="display: none">
  </div>

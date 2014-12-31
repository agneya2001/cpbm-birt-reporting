<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<script type="text/javascript" src="<%=request.getContextPath() %>/js/channels.js"></script>

<spring:message code="date.format" var="ddMMMyyyy_format"/>
<div class="widget_details_actionbox">
   <ul class="widget_detail_navpanel" style="float:right; 0 5px 0 0">
     <li class="widget_detail_navpanel"  style="float:left;" id="product_bundle_current"><a href="javascript:void(0);" style="color:#000;"><spring:message code="page.level2.bundles"/></a></li>
     <li class="widget_detail_navpanel last"  style="float:left;" id="product_utility_current"><a href="javascript:void(0);"><spring:message code="label.catalog.utilityrate.title"/></a></li>
  </ul>
</div>

<input type="hidden" id="currentHistoryPlanned" value="planned" />

<div id="catalog_productbundle_current">

     <div class="widget_details_actionbox">
          <span class="widget_detail_navpanel" style="margin: 10px 10px;" id="second_line_under_planned_span">
          <spring:message code="label.last.sync.with.reference.catalog"/> 
            <span id="last_sync_date">
                <c:choose>
                  <c:when test="${lastSyncDate != null}">
                      <fmt:formatDate value="${lastSyncDate}" pattern="${ddMMMyyyy_format}" />
                  </c:when>
                  <c:otherwise>
                      <spring:message code="ui.label.plan.date.not.yet.set"/>
                  </c:otherwise>
               </c:choose>
            </span>
          </span>
      <div style="float: right; display: block;" id="bundle_level_menu" class="widget_subactions grid action_menu_container">
        <div style="display:None;" id="bundle_add_menu" class="widget_actionpopover_grid">
          <div class="widget_actionpopover_top grid"></div>
              <div class="widget_actionpopover_mid">
                <ul class="widget_actionpoplist">
                    <c:if test="${bundlestoadd}">
                       <li style="display: block;" onclick="attachProductBundle(event, this)" class="view_volume_details_link">
		                      <spring:message code="label.channel.bundles.atatch"/>
		                   </li>
                    </c:if>
                    <li id="sync_channel" onclick="syncChannel(event, this)"><spring:message code="ui.label.channel.sync.with.ref.price.book"/></li>
                    <li href="javascript:void(0);" onclick="popup_date_picker(event);" channelid="<c:out value="${channel.id}"/>"><spring:message code="ui.label.schedule.activation"/>
                    </li>
                    <li id="view_catalog_action" ><a href="javascript:void(0);"><spring:message code="ui.label.preview.catalog"/></a></li>
                </ul>
              </div>
          <div class="widget_actionpopover_bot"></div>
       </div>
     </div>
    </div>

    <div class="widget_browsergrid_wrapper wsubaction" style="overflow-x: hidden; overflow-y: auto; height: 365px;" id="bundles_detail_area" which="planned" gotall="false">
       <div id="catalog_row_container">
         <c:forEach var="productBundleRevision" items="${productBundleRevisions}" varStatus="status">

           <c:set var="index" value="${status.index}" scope="request"></c:set>
           <c:set var="productBundleRevision" value="${productBundleRevision}" scope="request"></c:set>
           <c:set var="entitlements" value="${productBundleRevision.entitlements}" scope="request"></c:set>
           <c:set var="currencies" value="${supportedCurrencies}" scope="request"></c:set>
           <c:set var="fullBundlePricingMap" value="${fullBundlePricingMap}" scope="request"></c:set>
           <c:set var="arechargestobeshown" value="${productBundleRevision.productBundle.rateCard}" scope="request"></c:set>
           <c:set var="actionstoshow" value="1" scope="request"></c:set>
           <c:set var="toalloweditprices" value="${toalloweditprices}" scope="request"></c:set>
           <c:set var="noDialog" value="true" scope="request"></c:set>

           <jsp:include page="catalogproductbundleview.jsp"></jsp:include>
         </c:forEach>
      </div>
    </div>
</div>


  <div id="catalog_products_current" class="dialog_formcontent" style="display:None; margin: 0 0 0 0;">
      <div class="widget_browsergrid_wrapper wsubaction"  style="overflow-x: hidden;overflow-y: auto; height: 397px;">
        <div id="product_row_template" class="widget_details_inlinegrid j_product_row_template" style="margin-left: 2px;">
        <div class="widget_grid inline header widget_navtitlebox">
          <span  class="title" style="margin-left:15px;width: auto;color:#FFF;">
          </span>
          <div style="float: right;" class="widget_subactions grid action_menu_container" id="plan_product_charges">
            <!--Actions popover starts here-->
            <div style="display: none;" id="product_action_menu" class="widget_actionpopover_grid">
                <div class="widget_actionpopover_top grid"></div>
                  <div class="widget_actionpopover_mid">
                    <ul class="widget_actionpoplist">
                      <c:choose>
                        <c:when test="${toalloweditprices}">
                            <li style="display: block;" onclick="editCatalogProductCharges(this)"><spring:message code="label.ui.edit.pricing"/></li>
                        </c:when>
                        <c:otherwise>
                           <li id="no_actions_available"><spring:message code="label.no.actions.available"/></li>
                        </c:otherwise>
                      </c:choose>
                    </ul>
                  </div>
                  <div class="widget_actionpopover_bot"></div>
              </div>
              <!--Actions popover ends here-->
            </div>
        </div>
       
       <div class="widget_details_inlinegrid" id= "utility_rate_card_details" style="border-width: 0px 0px 1px; margin-top: 0px; margin-left: 0px; width:608px;">
          <c:set var="fullProductPricingMap" value="${fullProductPricingMap}" scope="request"></c:set>
          <c:set var="currencies" value="${supportedCurrencies}" scope="request"></c:set>
          <c:set var="totalproducts" value="${noOfProducts}" scope="request"></c:set>
          <c:set var="noDialog" value="true" scope="request"></c:set>

          <jsp:include page="catalogutilityratecardview.jsp"></jsp:include>

       </div>
       
      </div>
      </div>
  </div>

<div id="dialog_product_pricing_edit" title='<spring:message code="label.channel.product.edit.price"/>'>
</div>

<div id="dialog_edit_bundle_charges" title='<spring:message code="label.ui.edit.bundle.charges"/>'>
</div>

<div id="attach_product_bundle" title='<spring:message code="label.channel.bundles.atatch"/>' style="overflow-x: hidden; overflow-y: auto;">
</div>

<div id="dialog_sync_channel" title='<spring:message code="ui.label.sync.channel"/>' style="overflow-x: hidden; overflow-y: auto;">
   <span class="catalog_datepicker_dialog_titles" style="margin-top: 30px; margin-left: 18px;"><spring:message code="js.confirm.channel.sync"/></span>
   <span id="e_msg_channel_failed_sync" style="display: none;" text='<spring:message code="js.errors.channel.failed.sync"/>'></span>
</div>

<div  id="activate_catalog" title='<spring:message code="ui.label.activate.catalog"/>' style="display: none">
     <span class="catalog_datepicker_dialog_titles" style="margin-top: 30px; margin-left: 30px;"><spring:message code="js.confirm.channel.catalog.activate"/></span>
     <span id="e_msg_catalog_activate" style="display: none;" text='<spring:message code="js.errors.channel.failed.catalog.activate"/>'></span>
  </div>


<div id="dialog_detach_bundle" title='<spring:message code="label.channel.bundle.detach"/>' style="display: none">
     <span class="catalog_datepicker_dialog_titles" style="margin-top: 30px; margin-left: 30px;">
        <spring:message code="js.confirm.channel.detach.bundle"/>
     </span>
     <span id="e_msg_detach_bundle" style="display: none;" text='<spring:message code="js.errors.channel.failed.detach.bundle"/>'>
     </span>
</div>
<input type="hidden" id="currentEffectiveDate" value="<c:out value="${effectiveDate}"/>" />

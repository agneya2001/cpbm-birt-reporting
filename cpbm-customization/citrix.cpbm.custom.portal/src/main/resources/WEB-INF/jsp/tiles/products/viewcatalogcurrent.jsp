<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<script type="text/javascript" src="<%=request.getContextPath() %>/js/channels.js"></script>
<spring:message code="date.format" var="ddMMMyyyy_format"/>
<div class="widget_details_actionbox">
   <ul class="widget_detail_navpanel" style="float:right; 0 5px 0 0">
     <li class="widget_detail_navpanel"  style="float:left;" id="product_bundle_current"><a href="javascript:void(0);" style="color:#000;"><spring:message code="page.level2.bundles"/></a></li>
     <li class="widget_detail_navpanel last"  style="float:left;" id="product_utility_current"><a href="javascript:void(0);"><spring:message code="label.catalog.utilityrate.title"/></a></li>
  </ul>
</div>

<input type="hidden" id="currentHistoryPlanned" value="current" />

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
                    <li id="view_catalog_action" ><a href="javascript:void(0);"><spring:message code="ui.label.preview.catalog"/></a></li>
                </ul>
              </div>
          <div class="widget_actionpopover_bot"></div>
       </div>
     </div>
    </div>
  <div class="widget_browsergrid_wrapper wsubaction" style="overflow-x: hidden; overflow-y: auto; height: 365px;" id="bundles_detail_area" which="current" gotall="false">
     <div id="catalog_row_container">
        <c:forEach var="productBundleRevision" items="${productBundleRevisions}" varStatus="status">
           <c:set var="index" value="${status.index}" scope="request"></c:set>
           <c:set var="productBundleRevision" value="${productBundleRevision}" scope="request"></c:set>
           <c:set var="entitlements" value="${productBundleRevision.entitlements}" scope="request"></c:set>
           <c:set var="fullBundlePricingMap" value="${fullBundlePricingMap}" scope="request"></c:set>
           <c:set var="currencies" value="${supportedCurrencies}" scope="request"></c:set>
           <c:set var="arechargestobeshown" value="${productBundleRevision.productBundle.rateCard}" scope="request"></c:set>
           <c:set var="actionstoshow" value="0" scope="request"></c:set>
           <c:set var="noDialog" value="true" scope="request"></c:set>
  
           <jsp:include page="catalogproductbundleview.jsp"></jsp:include>

       </c:forEach>
    </div>
  </div>
</div>

<div id="catalog_products_current" class="dialog_formcontent" style="display:None; margin: 0 0 0 0;">
    <div class="widget_browsergrid_wrapper wsubaction"  style="overflow-x: hidden;overflow-y: auto; height: 391px;">
      <div id="product_row_template" class="widget_details_inlinegrid j_product_row_template" style="margin-left: 2px;">
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
<input type="hidden" id="currentEffectiveDate" value="<c:out value="${effectiveDate}"/>" />

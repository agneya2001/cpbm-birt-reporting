<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<script type="text/javascript" src="<%=request.getContextPath() %>/js/channels.js"></script>


<script type="text/javascript">

  $("#product_bundle_history").unbind("click").bind("click", function (event) {
    $("#product_bundle_history").find("a").attr("style", "color: #000");
    $("#product_utility_history").find("a").attr("style", "color: #2C8BBC");

    $("#productsOrBundle").attr("value", "bundle");
    $('#catalog_productbundle_history').show();
    $('#catalog_products_history').hide();
  });


  $("#product_utility_history").unbind("click").bind("click", function (event) {
    $("#product_bundle_history").find("a").attr("style", "color: #2C8BBC");
    $("#product_utility_history").find("a").attr("style", "color: #000");

    $("#productsOrBundle").attr("value", "product");
    $('#catalog_productbundle_history').hide();
    $('#catalog_products_history').show();
  });

</script>

<spring:message code="dateonly.short.format" var="dateonly_format"/>
<spring:message code="revision.date.format" var="revision_format"/>
<input type="hidden" id="currentHistoryPlanned" value="history" />
<input type="hidden" id="historyDateFormat" value="<c:out value="${revision_format}"/>" />
<input type="hidden" id="productsOrBundle" value="<c:choose><c:when test="${!showProductHistory}">bundle</c:when><c:otherwise>product</c:otherwise></c:choose>" />

<c:if test="${!noHistory}">
  <div class="widget_details_actionbox">
     <ul class="widget_detail_navpanel" style="float:right; 0 5px 0 0">
  
        <li class="widget_detail_navpanel"  style="float:left;" id="product_bundle_history"><a href="javascript:void(0);" <c:if test="${!showProductHistory}"> style="color:#000;" </c:if>><spring:message code="page.level2.bundles"/></a></li>
         <li class="widget_detail_navpanel"  style="float:left;" id="product_utility_history"><a href="javascript:void(0);"  <c:if test="${showProductHistory}"> style="color:#000;" </c:if>><spring:message code="label.catalog.utilityrate.title"/></a></li>
  
         <li class="widget_detail_navpanel last"  style="float:left;">
         <fmt:formatDate value="${chosenHistoryDate}" pattern="${revision_format}" var="chosenHistoryDateFormatted"/>
          <c:if test="${catalogHistoryDates != null}">
            <select id="catalog_history_dates" style="margin-top: -5px; width:200px;" class="select">
  	          <c:forEach var="historyDate" items="${catalogHistoryDates}">
               <fmt:formatDate value="${historyDate}" pattern="${revision_format}" var="historyDateFormatted"/>
  	            <option <c:if test="${historyDateFormatted == chosenHistoryDateFormatted}">selected="selected"</c:if>><fmt:formatDate value="${historyDate}" pattern="${revision_format}" />
                </option>
  	          </c:forEach>
  	        </select>
          </c:if>
         </li>
    </ul>
  </div>

  <div id="catalog_productbundle_history"
    <c:if test="${showProductHistory}"> style="display: None"</c:if>
  >
    <div class="widget_details_actionbox">
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
    <div class="widget_browsergrid_wrapper wsubaction" style="overflow-x: hidden; overflow-y: auto; height: 382px;" >
	     <div id="catalog_row_container">
	        <c:forEach var="productBundleRevision" items="${productBundleRevisions}" varStatus="status">
	          <c:set var="index" value="${status.index}" scope="request"></c:set>
	          <c:set var="productBundleRevision" value="${productBundleRevision}" scope="request"></c:set>
	          <c:set var="entitlements" value="${productBundle.entitlements}" scope="request"></c:set>
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
  <div id="catalog_products_history" class="dialog_formcontent" 
      <c:choose>
        <c:when test="${!showProductHistory}">
          style="display:None; margin: 0;"
        </c:when>
        <c:otherwise>
          style="margin: 0;"
        </c:otherwise>
      </c:choose>
  >
    <div class="widget_browsergrid_wrapper wsubaction"  style="overflow-x: hidden;overflow-y: auto;">
           <c:set var="historydate" value="${chosenHistoryDate}"></c:set>
            <div id="pb_history_row_template<c:out value="${historydate}" />"  class="widget_details_inlinegrid j_product_row_template" style="margin-left: 2px;">
                <div class="widget_details_inlinegrid" style="border-width: 0px 0px 1px; margin-top: 0px; margin-left: 0px; width:608px;">
                  <c:set var="fullProductPricingMap" value="${fullProductPricingMap}" scope="request"></c:set>
				          <c:set var="currencies" value="${supportedCurrencies}" scope="request"></c:set>
				          <c:set var="totalproducts" value="${noOfProducts}" scope="request"></c:set>
				          <c:set var="noDialog" value="true" scope="request"></c:set>
				
				          <jsp:include page="catalogutilityratecardview.jsp"></jsp:include>

                </div>
          </div>
       
      </div>
  </div>    
</c:if>
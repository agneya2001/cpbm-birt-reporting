<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/ratecards.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/commonBundles.js"></script>
<jsp:include page="js_messages.jsp"></jsp:include>
<script type="text/javascript">
var JS_LOADED;
var productBundlesUrl = "<%=request.getContextPath() %>/portal/productBundles/";
var thisCatalog = "<c:out value='${catalog.id}'/>";
</script>
<div class="maintitlebox" style="width:98%;">
 <div class="secondlevel_breadcrumb_panel" style="width:auto;">
      <div class="secondlevel_breadcrumbbox">
          <p><a href="<%=request.getContextPath()%>/portal/products/listcatalogs"><spring:message code="label.bundle.list.all.catalogs"/></a></p>
      </div>           
        <div class="secondlevel_breadcrumbbox">
          <p><c:out value="${catalog.name}"/></p>
        </div>
             
    </div>   
  
</div>
<div class="main_detailsbox" style="width:98%;">
 <spring:message code="date.format" var="date_format"/>	
  <!-- Title -->
   <c:set var="startIndex" value="1"/>     
  <div id="currentRateCard">
      <div class="main_details_titlebox" style="width:100%;">
          <div style="float:left;"><h2><spring:message code="label.bundle.view.utility.ratecard.title"/></h2></div>
          <div class="rateCardTitleBarText"> <spring:message code="label.bundle.list.ratecard.effective.date"/>
          <fmt:formatDate value="${date}" pattern="${date_format}"/>
          <div class="rateCardTitleBarLinks"> 
                <a href="javascript:void(0);" onclick="editURC(event);"><spring:message code="ui.products.label.view.edit"/></a>
              </div>
          </div>           
             
     </div> 
       <input type="hidden" id="catalogid" value="<c:out value="${catalog.id}"/>"/>
       <c:set var="rateCardChargesMap" value="${rateCardChargesMap}" scope="request"/>
       <div class="rateCardDetails" id="rateCardDetails0" style="display:block">
        <jsp:include page="urcratecard.jsp"/>
       </div>
     </div>
     <div class="clearboth"></div>
     <div class="displayPrevRateCards">
      <p><a href="javascript:void(0);" id="viewPrevRateCards"><spring:message code="label.bundle.list.ratecard.show.previous.title"/></a></p>
      <p><a href="javascript:void(0);" id="hidePrevRateCards" style="display:none"><spring:message code="label.bundle.list.ratecard.hide.previous.title"/></a></p>
     </div>
     <div id="prevRateCardsDiv" style="display:none">
      <c:if test="${numberofcards <= startIndex}">
        <div class="nodata"><spring:message code="label.bundle.view.utility.ratecard.no.previous.title"/></div>
       </c:if>
          <c:forEach var="temprateCard" items="${productBundle.rateCards}" varStatus="rateCardstatus" begin="${startIndex}">
            <!-- Starting of rate card data -->
            <!-- Header -->
            <div class="main_details_titlebox " style="margin-top:10px">
              <div style="float:left;"><h2><spring:message code="label.bundle.view.utility.ratecard.title"/></h2> </div>
              <div class="rateCardTitleBarText">
                <spring:message code="label.bundle.list.ratecard.effective.date"/> <fmt:formatDate value="${temprateCard.startDate}" pattern="${date_format}"/>
                <div class="rateCardTitleBarLinks">
                   <a href="javascript:void(0);" id="viewDetails<c:out value="${rateCardstatus.index}"/>" class="viewrateCard"><spring:message code="label.bundle.list.ratecard.show.details"/></a>
                   <a href="javascript:void(0);" id="hideDetails<c:out value="${rateCardstatus.index}"/>" class="hiderateCard" style="display:none"><spring:message code="label.bundle.list.ratecard.hide.details"/></a>
                </div>
                </div>
           </div>
           <c:set var="rateCard" value="${temprateCard}" scope="request"/>
           <div class="rateCardDetails" id="rateCardDetails<c:out value="${rateCardstatus.index}"/>" style="display:none">
              <jsp:include page="urcratecard.jsp"/>
           </div>
         </c:forEach>
     
  </div>         
         <input type="hidden" id="bundleId" value="<c:out value="${productBundle.id}"/>"/>
  </div>
               

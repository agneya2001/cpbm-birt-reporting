<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/ratecards.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/commonBundles.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/date.format.js"></script>
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
          <p><a href="<%=request.getContextPath()%>/portal/productBundles/<c:out value="${catalog.id}"/>/listbundles"><c:out value="${catalog.name}"/></a></p>
        </div>
         <div class="secondlevel_breadcrumbbox">
          <p><c:out value="${productBundle.name}"/></p>
        </div>
             
    </div>
</div>
<div class="main_detailsbox" style="width:98%;">  
<spring:message code="dateonly.format" var="dateonly_format"/>
     <div id="futureRateCard">
     <c:set var="startIndex" value="1"/>
	     <c:if test="${!empty futureRateCardChargesMap}">
	     <c:set var="startIndex" value="2"/>
	         <div class="main_details_titlebox" style="width:100%;">
	            <h2><spring:message code="label.bundle.list.rate.card.future.title"/></h2>
	            
	            <div class="rateCardTitleBarText">
		            <div style="float:left;">
                <spring:message code="label.bundle.list.ratecard.effective.date"/> <fmt:formatDate value="${futuredate}" pattern="${dateonly_format}"/> 
                </div>
		            <div class="rateCardTitleBarLinks"> 
		            <a href="javascript:void(0);" onclick="editFutureRateCard(event);"><spring:message code="ui.products.label.view.edit"/></a> | 
		            <a href="javascript:void(0);" onclick="deleteFutureRateCard(event);"><spring:message code="label.bundle.list.entitlement.delete"/></a> | 
		            <a href="javascript:void(0);" id="viewDetails0" class="viewrateCard"><spring:message code="label.bundle.list.ratecard.show.details"/></a>
		            <a href="javascript:void(0);" id="hideDetails0" class="hiderateCard" style="display:none"><spring:message code="label.bundle.list.ratecard.hide.details"/></a>
		            </div>
	         </div> 
           </div>  
	       <!--  <input type="hidden" id="fratecardId" value="<c:out value="${futureRateCard.id}"/>"/>    -->
			<c:set var="rateCardChargesMap" value="${futureRateCardChargesMap}" scope="request"/>
			<div class="rateCardDetails" id="rateCardDetails0" style="display:none">
				<jsp:include page="ratecard.jsp"/>
			</div>
		</c:if>
	</div>
	
	<div id="currentRateCard">
	     <div class="main_details_titlebox rateCard" >
	        <h2><spring:message code="label.bundle.list.ratecard.current.title"/></h2> 
		        <div class="rateCardTitleBarText">
			        <div style="float:left;"><spring:message code="label.bundle.list.ratecard.effective.date"/> <fmt:formatDate value="${date}" pattern="${dateonly_format}"/></div>
			        <div id="currentRateCardLinks" class="rateCardTitleBarLinks"> 
				        <c:if test="${empty futureRateCardChargesMap}">
				        	<a href="javascript:void(0);" onclick="planRateCard(event);"><spring:message code="label.bundle.list.ratecard.plan.title"/></a> | 				        	
                 </c:if>
                 <!-- current RC is editable only : 1. bundle should not publish 2. and first ratecard. -->
                 <c:if test="${isCurrentRateCardEditable}">
                  <a href="javascript:void(0);" onclick="editCurrentRateCard(event);"><spring:message code="ui.products.label.view.edit"/></a> |
				        </c:if>
				        <a href="javascript:void(0);" id="viewDetails<c:out value="${startIndex-1}"/>" class="viewrateCard" style="display:none"><spring:message code="label.bundle.list.ratecard.show.details"/></a>
				        <a href="javascript:void(0);" id="hideDetails<c:out value="${startIndex-1}"/>" class="hiderateCard"><spring:message code="label.bundle.list.ratecard.hide.details"/></a>
			        </div>
           </div> 
	     </div><!--
	     <input type="hidden" id="cratecardId" value="<c:out value="${currentRateCard.id}"/>"/>
	     --><c:set var="rateCardChargesMap" value="${currentRateCardChargesMap}" scope="request"/>
	     <div class="rateCardDetails" id="rateCardDetails<c:out value="${startIndex-1}"/>" style="display:block">
	     	<jsp:include page="ratecard.jsp"/>
	     </div>
     </div>
     <div class="clearboth"></div>
     <div class="displayPrevRateCards">
     	<p><a href="javascript:void(0);" id="viewPrevRateCards"><spring:message code="label.bundle.list.ratecard.show.previous.title"/></a></p>
     	<p><a href="javascript:void(0);" id="hidePrevRateCards" style="display:none"><spring:message code="label.bundle.list.ratecard.hide.previous.title"/></a></p>
     </div>
     <div id="prevRateCardsDiv" style="display:none">
     	<c:if test="${numberofcards <= startIndex}">
     		<div class="nodata"><spring:message code="label.bundle.list.ratecard.no.previous.title"/></div>
     	 </c:if>
          <c:forEach var="temprateCard" items="${productBundle.allRateCards}" varStatus="rateCardstatus" begin="${startIndex}">
	          <!-- Starting of rate card data -->
	          <!-- Header -->
	          <div class="main_details_titlebox " style="margin-top:10px;width:100%;" >
	            <h2><spring:message code="label.bundle.list.ratecard.title"/></h2> 
	            
	            <div class="rateCardTitleBarText">
	            	<spring:message code="label.bundle.list.ratecard.effective.date"/> <fmt:formatDate value="${temprateCard.startDate}" pattern="${dateonly_format}"/>
	            	 </div>
	            	<div class="rateCardTitleBarLinks">
	            		 <a href="javascript:void(0);" id="viewDetails<c:out value="${rateCardstatus.index}"/>" class="viewrateCard"><spring:message code="label.bundle.list.ratecard.show.details"/></a>
	            		 <a href="javascript:void(0);" id="hideDetails<c:out value="${rateCardstatus.index}"/>" class="hiderateCard" style="display:none"><spring:message code="label.bundle.list.ratecard.hide.details"/></a>
	            	</div>
	         </div>
	         <c:set var="rateCard" value="${temprateCard}" scope="request"/>
	         <div class="rateCardDetails" id="rateCardDetails<c:out value="${rateCardstatus.index}"/>" style="display:none">
	          	<jsp:include page="ratecard.jsp"/>
	         </div>
         </c:forEach>
     
 	</div>         
         
  </div>         

	<input type="hidden" id="bundleId" value="<c:out value="${productBundle.id}"/>"/>

  <div class="main_detailsbox" style="width:98%;">
  <!-- Starting of entitlement data -->
        <div id="entitlementsDiv"> 
         
             <jsp:include page="listentitlements.jsp"></jsp:include>    
        </div>  
       <div id="addnewentitlementDiv" class="editable_gridformbox">
        <jsp:include page="createentitlement.jsp"></jsp:include>  
       </div>
       
  </div>
  <c:if test="${productBundle.vm ==true}">
    <div class ="common_messagebox success successMessage" style="width:98%">
       <o1 > <spring:message code="product.bundle.entitlement.note"></spring:message>
              <li style="margin-left:25px;"><spring:message code="product.bundle.entitlement.iso.note.text"></spring:message></li>
       </o1>
    </div>
  </c:if>   
  

                     
                

<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div class="widget_details_inlineboxes" style="display:block;" id="bundleNo<c:out value='${index + 1}'/>">
  <div class="widget_details_inlinevolumetitlebar">
      <div class="widget_details_inlineboxes_contentarea">
          <div class="widget_details_inlineboxes_titlearea">
               <c:choose>
                 <c:when test="${productBundleRevision.productBundle.publish}">
                   <span class="widget_statusicon running" style="margin-top: -3px; margin-right: 5px;"></span>
                 </c:when>
                 <c:otherwise>
                   <span class="widget_statusicon stopped" style="margin-top: -3px; margin-right: 5px;"></span>
                 </c:otherwise>
               </c:choose>

               <span class="title ellipsis" title = "<c:out value="${productBundleRevision.productBundle.name}"/>" id="name" style="margin: 0px 0px 0px 10px; border-left-width: 0px; width: 300px;"><c:out value="${productBundleRevision.productBundle.name}"/></span>
          </div>
     </div>
     <c:if test="${actionstoshow == 1}">
       <div class="widget_subactions grid action_menu_container" id="action_per_product_bundle" style="float:right;">
         <div class="widget_actionpopover_grid" id="per_bundle_action_menu" style="display:none;">
             <div class="widget_actionpopover_top grid"></div>
               <div class="widget_actionpopover_mid">
                 <ul class="widget_actionpoplist">
                    <c:choose>
                      <c:when test="${toalloweditprices}">
			                   <li id="edit_bundle_charges" style="display: block;" bundleId="<c:out value="${productBundleRevision.productBundle.id}"/>" onclick="editBundleCharges(event,this)">
			                     <spring:message code="label.ui.edit.pricing"/>
			                   </li>
                      </c:when>
                      <c:otherwise>
                         <li id="no_actions_available"><spring:message code="label.no.actions.available"/></li>
                      </c:otherwise>
                    </c:choose>
                   
                 </ul>
               </div>
               <div class="widget_actionpopover_bot"></div>
         </div>
       </div>
     </c:if>
  </div>

  <div class="widget_details_inlinevolumedetailsbar cat_bundlesdeatilsbar">
    <div class="widget_inline_computebox" id="mainentitlementscontentdiv<c:out value='${index}'/>" >
      <div class="widget_inline_computebox mainiconbox">
        <span class="computeicon"></span>
      </div>



      <div class="widget_inline_computebox featuresbox">
        <span class="featurestext ellipsis" title = "<c:out value="${productBundleRevision.productBundle.description}"/>" style= "width:300px"> <c:out value="${productBundleRevision.productBundle.description}"/></span>
      </div>

      <div class="widget_inline_computebox inc_usagebox ">
        <a style="margin:10px 0 0 15px; float:left; display:inline;" productbundleid="<c:out value="${productBundleRevision.productBundle.id}"/>" href="javascript:void(0);" onclick="viewEntitlements(event, this);">
          <spring:message code="label.included.usage"/>
        </a>
      </div>

      

    </div>
    <c:if test="${arechargestobeshown != null}">
      <c:set var="productBundleRevision" value="${productBundleRevision}" scope="request"></c:set>
      <c:set var="fullBundlePricingMap" value="${fullBundlePricingMap}" scope="request"></c:set>
      <c:set var="currencies" value="${currencies}" scope="request"></c:set>
      <c:set var="noDialog" value="${noDialog}" scope="request"></c:set>

      <jsp:include page="catalogproductbundlecharges.jsp"></jsp:include>
    </c:if>
   </div>
</div>

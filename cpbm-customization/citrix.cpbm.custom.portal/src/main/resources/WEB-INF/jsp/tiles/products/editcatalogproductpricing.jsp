<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<spring:message code="dateonly.format" var="dateonly_format"/>

<div class="dialog_formcontent wizard" style="height:auto">
  <div class="details_lightboxtitlebox" >
  </div>
    <div class="details_lightboxformbox" style="border-bottom: none;" >
    <ul> 
        <li style="margin: 10px 0pt 10px 25px;">
           <label for="startDate" style="width:150px; color: #111; font-weight: bold;" ><spring:message code="label.start.date"/></label>
           <label for="startDate" style="width:80px; color: #333333;" >
              <c:choose>
                <c:when test="${planDate != null}">
                    <fmt:formatDate  value="${planDate}" pattern="${dateonly_format}" />
                </c:when>
                <c:otherwise>
                    <spring:message code="ui.label.plan.date.not.yet.set"/>
                </c:otherwise>
              </c:choose>
              
           </label>
        </li>
     </ul>
     <div class="widget_details_inlinegrid" style="width:830px;">

          <div class="widget_grid inline subheader" style="width:830px;">
             <div class="widget_grid_cell" style="width:150px;">
		             <span class="subheader"><spring:message code="label.name"/></span>
		         </div>
             <c:forEach var="currency" items="${supportedCurrencies}" varStatus="status">
               <div class="widget_grid_cell" style="width: 108px;">
                <div class="widget_flagbox" style="float:left; padding:0; margin:5px 0 0 10px;">
                   <div class="widget_currencyflag">
                     <img src="../../images/flags/<c:out value="${currency.currencyCode}"/>.gif" alt="" />
                   </div>
               </div>
               <span class="subheader" style="margin-left: 2px;"><c:out value="${currency.currencyCode}"/>&nbsp;(<c:out value="${currency.sign}" />)</span>
               </div>
             </c:forEach>
          </div>

        <div class="widgetgrid_wrapper plangrid_lightbox" style="width:830px; overflow-x: hidden; overflow-y: auto;min-width:830px;">
          <c:forEach items="${fullProductPricingMap}" var="product" varStatus="status">
            <c:choose>
              <c:when test="${status.index % 2 == 0}">
                <c:set var="rowClass" value="odd"/>
              </c:when>
              <c:otherwise>
                  <c:set var="rowClass" value="even"/>
              </c:otherwise>
            </c:choose>

            <div class="<c:out value="widget_grid inline ${rowClass}"/>" style="width:830px;">
               <div class="widget_grid_cell" style="width: 150px;">
	                  <span class="celltext ellipsis" style="font-weight:bold; width:105px; color:#0A79AC; margin:15px 0 0 5px;"><c:out value="${product.key.name}"/></span>
               </div>

                <c:forEach var="currency" items="${supportedCurrencies}">
                  <div class="widget_grid_cell" style="width:108px;">
                    <span class="celltext">
                    <c:set var="previousValue" value="${product.value[currency]['catalog'].price}" />
                    <c:if test="${empty previousValue }">
                      <fmt:formatNumber var="previousValue" pattern="${currencyFormat}" minFractionDigits="${currencyFractionalDigitsLimit}" value="0" />
                    </c:if>
                       <div class="mandatory_wrapper" style="margin:0px 0px 0px -5px;">
                          <input style="margin: 0px 0px 0px 10px; width: 80px; height: 16px;" id="currencyValsWeNeed<c:out value='${currency.currencyCode}${product.key.name} }'/>"
				                         class="text priceRequired j_pricerequired"
				                         value='${previousValue}'
                                 previousvalue='${previousValue}'
				                         currencyId='<c:out value="${currency.id}" />'
				                         currencycode='<c:out value="${currency.currencyCode}" />'
				                         productId='<c:out value="${product.key.id}" />' />
				                   <span style="height:auto; margin: 0px 0px 0px 10px; width: 80px; height: 16px; font-style:italic; color:#666;" title="<spring:message code='label.reference.price'/>">(<fmt:formatNumber pattern="${currencyFormat}" minFractionDigits="${currencyFractionalDigitsLimit}" value="${product.value[currency]['rpb'].price}"/>)</span>
                       </div>
                  </span>
                 </div>
                </c:forEach>

            </div>

          </c:forEach>
       </div>

    </div>

	<div id="error_div" class="common_messagebox error" style="width:830px; margin:0 0 0 10px; padding:0 0 5px 0; border:1px solid #CCCCCC; display:block;display:none;">
	    <span class="erroricon"></span>
	     <p id="priceError" style="margin-top:7px;"></p>
	</div>
	</div>


</div>

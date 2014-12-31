<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<script type="text/javascript">
var productsUrl = "<%=request.getContextPath() %>/portal/products/";
var history_totalpages = parseInt(<c:out value="${history_totalpages}"/>);
var history_CurrentPage = parseInt(<c:out value="${history_CurrentPage}"/>);
var history_PerPage = parseInt(<c:out value="${history_PerPage}"/>);
</script>

 <div id="catalog_productbundle_current">
  <div class="widget_browsergrid_wrapper wsubaction" style="overflow-x: hidden; overflow-y: auto;" >
     <div id="catalog_row_container">
          <div id="history_row_template" class="widget_details_inlinegrid" >

            <div class="widget_grid inline header widget_navtitlebox">
              <span class="widget_gridicon historycalendar "  style="margin:7px 0 0 5px;"> </span>
                <span  class="title"  style="margin-left:5px;width: auto;color:#FFF;">  
                <spring:message code="label.bundle.list.ratecard.effective.date"></spring:message>
              </span>
              <span  class="title" style="margin-left:5px;width: auto;color:#FFF;">
                <c:choose>
                  <c:when test="${historyDate != null}">
                      <spring:message code="date.format" var="dateonly_format"/>
                      <fmt:formatDate value="${historyDate}" pattern="${dateonly_format}"/>
                  </c:when>
                  <c:otherwise>
                      <spring:message code="ui.label.plan.date.not.yet.set"/>
                  </c:otherwise>
                </c:choose>
              </span>
            </div>

             <c:forEach items="${productChargesList}" var="productPrice" varStatus="priceStatus">
						   <c:choose>
						     <c:when test="${status.index % 2 == 0}">
						         <c:set var="rowClass" value="odd"/>
						     </c:when>
						     <c:otherwise>
						         <c:set var="rowClass" value="even"/>
						     </c:otherwise>
						     </c:choose>   
						     <div class='widget_grid inline<c:out  value="${rowClass}" />' id="<c:out value="${productPrice.currencyValue.currencyCode}" />">
						        <div class="widget_grid_labels">
						            <span> <spring:message code="currency.longname.${productPrice.currencyValue.currencyCode}"></spring:message></span>
						        </div>
						        <div class="widget_grid_description">
						          <span id="price" style="width:250px;">
						            <div style="float:left;"><c:out value="${productPrice.currencyValue.sign}"/></div>
						            <div style="float:left;"><fmt:formatNumber pattern="${currencyFormat}" minFractionDigits="${minFractionDigits}" value="${productPrice.price}"/></div>
						          </span>
						        </div>
						        <div class="widget_flagbox">
						                  <div class="widget_currencyflag">
						                      <img alt="" src="../../images/flags/<c:out value="${productPrice.currencyValue.flag}" />">
						                  </div>
						        </div>
						      </div> 

						  </c:forEach>
           </div>
    </div>

  </div>

</div>
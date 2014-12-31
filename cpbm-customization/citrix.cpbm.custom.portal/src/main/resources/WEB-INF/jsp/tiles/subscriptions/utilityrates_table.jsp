<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<spring:message code="dateonly.format" var="date_format"/> 
<script type="text/javascript">
  var effective_date_str = '<fmt:formatDate value="${startDate}" pattern="${date_format}" timeZone="${currentUser.timeZone}"/>';
</script>
<c:if test="${isDialog eq 'true' }">
  <div class="utilityrates_dialog_header">
    <div class="page_heading">
      <span  class="sub_title">
          <spring:message code="label.catalog.utilityrate.effective.date"/>
          <fmt:formatDate value="${startDate}" pattern="${date_format}" timeZone="${currentUser.timeZone}"/>
      </span>
    </div>
  </div>
</c:if>
<table class="table table-condensed table-bordered">  
  <thead class="utility_rate_table_header">  
    <tr>  
      <th><p><spring:message code="label.catalog.utility.card.table.header.product"/></p></th>
      <th><p><spring:message code="label.catalog.utility.card.table.header.price"/>&nbsp;(<c:out value="${currency.currencyCode}" />)</p></th>  
    </tr>  
  </thead>  
  <tbody>
  <c:choose>
    <c:when test="${empty retMap }">
      <tr>
        <td colspan="2">
          <p class="alert alert-info"><spring:message code="ui.warning.message.no.products.available"/></p>
        </td>
      </tr>
    </c:when>
    <c:otherwise>
    <c:forEach items="${retMap}" var="serviceMap" varStatus="serviceLoopStatus">
    
    <c:set var="serviceInstanceMap" value="${serviceMap.value}"></c:set>
    
      <c:forEach items="${serviceInstanceMap}" var="instanceMapVar" varStatus="instanceLoopStatus">
       <c:if test="${isDialog eq 'true' }">
         <tr style="background:#C2E6F8;">
            <td colspan="3"><c:out value="${serviceMap.key.category}"/>:&nbsp;<c:out value="${instanceMapVar.key.name}"></c:out></td>
          </tr>
         </c:if>
        <c:set var="productsMap" value="${instanceMapVar.value}"></c:set>
        <c:set var="productCategory" value=""></c:set>
        <c:forEach items="${productsMap}" var="productsMapVar" varStatus="productMapLoopStatus">
          <c:if test="${productsMapVar.key.category.id != productCategory}">
            <c:set var="productCategory" value="${productsMapVar.key.category.id}"></c:set>
            <tr>
                <td class="catRow" colspan="3"><spring:message code="label.catalog.utility.card.table.header.category"/>:&nbsp;<c:out value="${productsMapVar.key.category.name}"/></td> 
            </tr>
          </c:if>
          <tr class="hover_enabled"> 
            <td>
              <p class="ur_title"><c:out value="${productsMapVar.key.name}"/></p>
              
                <c:choose>
                    <c:when test="${fn:length(productsMapVar.key.description) > 70}">
                    <p class="description" id="stripped_content_${productMapLoopStatus.count}" style="float:left;">
                        <c:out value="${fn:substring(productsMapVar.key.description, 0, 70)}"/>&hellip;
                    </p>
                    <p class="description" id="hidden_content_${productMapLoopStatus.count}" style="display:none;">
                        
                          <c:out value="${productsMapVar.key.description}"/>&nbsp;
                        
                        
                    </p>
                    <p style="margin-top:-10px;clear:both;float:left;"><a class="more_down js_learn_more_link" href="javascript:void(0);"><spring:message code="label.catalog.more"/></a></p>
                    </c:when>
                    <c:otherwise>
                     <p class="description">  
                       <c:out value="${productsMapVar.key.description}"/>
                     </p>
                    </c:otherwise>
                </c:choose>
              
            </td> 
            <fmt:formatNumber var="price" pattern="${currencyFormat}"  minFractionDigits="${minFractionDigits}" value="${productsMapVar.value.price}"  />
            <td>
              <spring:message code="label.catalog.utility.card.table.price.value" arguments='${currency.sign}${price}; ${productsMapVar.key.uom}' argumentSeparator=";"/>
            </td>  
          </tr>  
        </c:forEach>
      </c:forEach>
    
  </c:forEach>
  </c:otherwise>
  </c:choose>
        
  </tbody>  
</table>

<script>  
  $(function (){
    $(".js_learn_more_link").unbind("click").bind("click", function(e) {
      e.preventDefault();
      $(this).toggleClass("more_down");
      $(this).toggleClass("more_up");
      $(this).parent().siblings('.description').toggle();
    });  
  });  
</script>
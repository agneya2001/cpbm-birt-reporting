<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
 <%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:choose>
  <c:when test="${isRecurring == 'true'}">
    <div class="widget_grid inline odd j_recurringrccrow" id="recurringrccrow_<c:out value='${recurring_current_rcc_row}' />">   
    <div class="widget_grid_cell" style="width:100px;">
      <textarea class="text" style="width:70%;" rows="2" cols="10" name="recurringRateCardChargesFormList[<c:out value='${recurring_current_rcc_row}' />].rcc.description"></textarea>   
    </div> 
    <c:forEach items="${currencyValueList}" var="currencyValue" varStatus="currencyStatus">
      <div class="widget_grid_cell" style="width:100px;">
       <input class="text" style="width:70%;"type="text" name="recurringRateCardChargesFormList[<c:out value='${recurring_current_rcc_row}' />].charges[<c:out value='${currencyStatus.index}' />].price" />
    </div> 
    </c:forEach>
    <div class="widget_grid_cell" style="width:100px;">
        <span class="celltext"><span class="cancelicon" onclick="deleteRateCardComponent('true',<c:out value='${recurring_current_rcc_row}' />)" title="cancel"></span></span>
    </div> 
</div>
  </c:when>
  <c:otherwise>
    <div class="widget_grid inline odd j_nonrecurringrccrow" id="nonrecurringrccrow_<c:out value='${nonrecurring_current_rcc_row}' />">   
    <div class="widget_grid_cell" style="width:100px;">
 <textarea class="text" style="width:70%;" rows="2" cols="10" name="nonRecurringRateCardChargesFormList[<c:out value='${nonrecurring_current_rcc_row}' />].rcc.description"></textarea>   
    </div> 
    <c:forEach items="${currencyValueList}" var="currencyValue" varStatus="currencyStatus">
      <div class="widget_grid_cell" style="width:100px;">
       <input class="text" style="width:70%;"type="text" name="nonRecurringRateCardChargesFormList[<c:out value='${nonrecurring_current_rcc_row}' />].charges[<c:out value='${currencyStatus.index}' />].price" />
    </div> 
    </c:forEach> 
    <div class="widget_grid_cell" style="width:100px;">
        <span class="celltext"><span class="cancelicon" onclick="deleteRateCardComponent('false',<c:out value='${nonrecurring_current_rcc_row}' />)" title="cancel"></span></span>
    </div> 
</div>
  </c:otherwise>
</c:choose>

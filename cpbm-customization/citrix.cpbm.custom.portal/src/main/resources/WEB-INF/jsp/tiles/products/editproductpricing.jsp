<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<script type="text/javascript" src="<%=request.getContextPath() %>/js/products.js"></script>

<script type="text/javascript">
  var productsUrl = "<%=request.getContextPath() %>/portal/products/";
  $("#dialog_plan_product_charges").empty();
</script>

<style>
.ui-datepicker-trigger {
    margin-left : 5px;
    margin-top : 0px;
}
</style>

<div class="dialog_formcontent">
  <div class="details_lightboxtitlebox" ></div>
  <div class="details_lightboxformbox" >
    <spring:url value="/portal/products/editproductpricing" var="edit_product_charges_path" htmlEscape="false" /> 
    <form:form commandName="productForm" id="productForm" cssClass="formPanel ajaxForm"  action="${edit_product_charges_path}"  >   
      <ul> 
        <li style="margin:10px 0 0 25px;">
          <label for="startDate" style="width:150px;color:#0A79AC;font-weight: bold;" ><spring:message code="label.start.date"/></label>
          <spring:message code="dateonly.filter.format" var="dateonly_format"/>
          <div class="mandatory_wrapper" style="margin:0 0 0 20px;">
            <input type="text" id="editstartDate" name="startDate" class="text j_startDate" tabindex="1" value="<fmt:formatDate  value="${date}" pattern="${dateonly_format}" />" />
          </div>
            <div class="main_addnew_formbox_errormsg" style="margin:5px 0 0 166px" id="editstartDateError" ></div>
       </li> 
      </ul> 

      <div id="product_row_template" class="widget_details_inlinegrid" style="overflow-x: hidden;overflow-y: auto;height:500px;" >
        <c:forEach items="${productForm.productCharges}" var="productCharge" varStatus="priceStatus">
          <c:choose>
            <c:when test="${priceStatus.index % 2 == 0}">
              <c:set var="rowClass" value="odd"/>
            </c:when>
            <c:otherwise>
              <c:set var="rowClass" value="even"/>
            </c:otherwise>
          </c:choose>   

          <div class='widget_grid inline<c:out  value="${rowClass}" />'>
            <div class="widget_grid_labels">
              <span> <spring:message code="currency.longname.${productCharge.currencyValue.currencyCode}"></spring:message></span>
            </div>
            <div class="widget_grid_description">
              <span style="width:250px;" ><div style="float:left;"><c:out value="${productCharge.currencyValue.sign}" />&nbsp;</div><input id="productCharges<c:out value='${priceStatus.index}' />.price"  value='<c:out value="${productCharge.price }" />'
              class="text priceRequired" name="productCharges[<c:out value='${priceStatus.index}' />].price" ></span>
              <div class="main_addnew_formbox_errormsg" style="margin:-7px 0 0 20px;" id="productCharges<c:out value='${priceStatus.index}' />.priceError"></div>
            </div>
            <div class="widget_flagbox">
              <div class="widget_currencyflag">
                <img alt="" src="../../images/flags/<c:out value="${productCharge.currencyValue.flag}" />">
              </div>
            </div>
          </div>

        </c:forEach>
      </div>

      <input type="hidden" id="planforcurrentchargesflag" value="false" >
    </form:form>
  </div>
</div>

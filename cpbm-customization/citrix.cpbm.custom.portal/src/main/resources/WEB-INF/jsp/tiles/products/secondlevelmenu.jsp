<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="js_messages.jsp"></jsp:include>

<script type="text/javascript" src="<%=request.getContextPath() %>/js/commonproducts.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/products.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/commonBundles.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/jquery.iframe-post-form.js"></script>

<script type="text/javascript">
  var tenantParam = "<c:out value="${tenant.param}"/>";
  var productsUrl = "<%=request.getContextPath() %>/portal/products/";
  var productBundlesUrl = "<%=request.getContextPath() %>/portal/productBundles/";

  $(document).ready(function() {
   var firstServiceCategory = $("#serviceCategories").find("a:first");
   if(firstServiceCategory != undefined){
     fillInstancesList(firstServiceCategory);
   }
 });
</script>

<spring:message code="dateonly.short.format" var="dateonly_format"/>
  <spring:message code="date.format" var="date_format"/>
<spring:message code="revision.date.format" var="revision_format"/>
<div class="secondlevel_withsubmenu">

    <div class="secondlevel_breadcrumb_panel">
      <div class="secondlevel_breadcrumbbox">
      	<p><spring:message code="page.level1.products"/></p>
      </div>

      <c:if test="${whichPlan == 'planned'}">
        <div class="secondlevel_breadcrumbbox">
        	<p><spring:message code="ui.label.plan.next"/></p>
        </div>
        <div class="secondlevel_breadcrumbbox">
          <p>
            <a onclick="setPlanDate();" href="javascript:void(0);" id="plan_charges_date"><spring:message code="ui.label.schedule.activation"/></a>
            <c:if test="${futurePlanDate != null}">
               &nbsp;&nbsp;(<spring:message code="label.activating.on"/>&nbsp;<fmt:formatDate value="${futurePlanDate}" pattern="${revision_format}" />)
            </c:if>
          </p>
        </div>
      </c:if>

      <c:if test="${whichPlan =='current'}">
        <div class="secondlevel_breadcrumbbox">
          <p><spring:message code="ui.label.view.current"/></p>
        </div>
        <div class="secondlevel_breadcrumbbox">
          <p>
            <spring:message code="label.effective.date"/>
             <span id="effective_date">
                <c:choose>
                  <c:when test="${currentPlanDate != null}">
                    <fmt:formatDate value="${currentPlanDate}" pattern="${date_format}" />
                  </c:when>
                  <c:otherwise>
                      <spring:message code="ui.label.plan.date.not.yet.set"/>
                  </c:otherwise>
               </c:choose>
             </span>
           </p>
         </div>
      </c:if>

      <c:if test="${whichPlan == 'history'}">
        <div class="secondlevel_breadcrumbbox">
          <p><spring:message code="ui.label.view.history"/></p>
        </div>
        <div class="secondlevel_breadcrumbbox">
          <p>
            <select style="margin-top: 1px; border-width: 1px; padding: 1px;" id="rpb_history_dates" onchange="viewReferencePriceBookHistory('<c:out value="${ProductsInner}"/>');">
              <c:forEach var="historyDate" items="${historyDates}" varStatus="status">
                <option value='<fmt:formatDate value="${historyDate}" pattern="MM/dd/yyyy HH:mm:ss" />' <c:if test="${historyDate == revisionDate}">selected</c:if>> <fmt:formatDate value="${historyDate}" pattern="${revision_format}" /> &nbsp;
                </option>
              </c:forEach>
            </select>
          </p>
        </div>
      </c:if>
      <div class="doc_help_link"><i class="fa fa-question-circle"></i></div>
    </div>

    <div class="secondlevel_menupanel" id="serviceCategories">
      <c:forEach var="serviceCategory" items="${serviceCategoryList}" varStatus="status">
        <a  class='secondlevel_menutabs <c:if test="${serviceCategory == selectedCategory}">on</c:if>' onclick="fillInstancesList(this);" serviceName='<spring:message  code="${serviceCategory}.category.text"/>' id='<c:out value="${serviceCategory}"/>'>
          <spring:message  code="${serviceCategory}.category.text"/>
        </a>
      </c:forEach>
   </div>
   <div class="clearboth"></div>

   <tiles:insertDefinition name="warnings"></tiles:insertDefinition>

    <input id="whichPlan" type="hidden" name="whichPlan" value="<c:out value="${whichPlan}"/>"/>
    
</div>


<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<script language="javascript">
var invoicesPage = true;
if( typeof i18n === 'undefined' ) {
  var i18n = {};
}
if( typeof i18n.errors === 'undefined' ) {
  i18n.errors = {};
}
if( typeof i18n.buttons === 'undefined' ) {
    i18n.buttons = {};
  }
i18n.errors.billingHistory = {
    showDetails : '<spring:message javaScriptEscape="true" code="js.errors.billingHistory.showDetails"/>'
};

i18n.buttons.billingHistory = {
      viewDetails : '<spring:message javaScriptEscape="true" code="js.button.billingHistory.viewDetails"/>',
      hideDetails : '<spring:message javaScriptEscape="true" code="js.button.billingHistory.hideDetails"/>'
  };
var dictionary={
    paymentSuccessMessage:'<spring:message javaScriptEscape="true" code="message.invoices.payment.successful"/>',
    paymentFailureMessage:'<spring:message javaScriptEscape="true" code="message.invoices.payment.failed"/>',
    processingPayment: '<spring:message javaScriptEscape="true" code="message.invoices.make.payment"/>'
};
</script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/billingHistory.js"></script>
<script type="text/javascript">
  var billingUrl = "<%=request.getContextPath() %>/portal/billing/";
</script>




<div class="widget_box">
  <div class="widget_leftpanel">
    <div class="widget_titlebar">
      <h2 id="list_titlebar"><span id="list_all"><spring:message code="label.list.all"/> </span></h2>
    </div>
    <div class="widget_searchpanel">
        
                <div class="widget_searchcontentarea" id="search_panel">
                <sec:authorize access="hasAnyRole('ROLE_ACCOUNT_ADMIN','ROLE_ACCOUNT_BILLING_ADMIN','ROLE_FINANCE_CRUD')">         
		          <span class="label"><spring:message code="label.filter.by"/>:</span>
		            <select id="selectedUserfilter" class="select" >
		              <option value="All" ><spring:message code="label.invoice.user.filter.all"/></option>
		              <c:forEach items="${users}" var="filter">
                         <option value="<c:out value="${filter.param}"/>" ><c:out value="${filter.username}" /></option>
		              </c:forEach>
		            </select>
		            </sec:authorize>
		          </div>
            
        
        </div>
    <div class="widget_navigation">
      <ul class="widget_navigationlist" id="grid_row_container">
                          
         <c:choose>
            <c:when test="${empty billingActivities || billingActivities == null}">
              <!-- Empty list -->
            <li class="widget_navigationlist nonlist" id="non_list">
                            <span class="navicon invoices"></span>
                              <div class="widget_navtitlebox">
                                <span class="newlist"><spring:message code="message.no.invoices.available"/></span>
                              </div>
                              
                          </li>
            <!-- end Empty list -->
      
           </c:when>
           <c:otherwise>
           <c:forEach var="billingActivity" items="${billingActivities}" varStatus="status">
          
          <c:choose>
            <c:when test="${status.index == 0}">
                <c:set var="firstActivity" value="${billingActivity}"/>
                            <c:set var="selected" value="selected active"/>
            </c:when>
            <c:otherwise>
                <c:set var="selected" value=""/>
            </c:otherwise>
            </c:choose>                                   
             
             <c:set var="activityId" value="${billingActivity.uuid}"></c:set>
                      
                          <li class='<c:out value="widget_navigationlist ${selected} invoices"/>' id="<c:out value='row${activityId}'/>" bill_type="Invoice" onclick="viewbillingActivity(this)" onmouseover="showInfoBubble(this)" onmouseout="hideInfoBubble(this)">
                            <span class="navicon invoices" id="nav_icon">
                                <span class="year">
                                <fmt:formatDate pattern="yyyy" value="${billingActivity.billingPeriodStartDate}" />
                              </span>
                            </span>
                            
                            <div class="widget_navtitlebox">
                              <span class="title" style="width:180px;">
                                <spring:message code="date.format" var="dateonly_format"/> 
                                <spring:message code="month.date.hour.minute.format" var="month_date"/>
                                <fmt:formatDate pattern="${month_date}" value="${billingActivity.billingPeriodStartDate}" timeZone="${currentUser.timeZone}"/>&nbsp;&nbsp;-&nbsp;&nbsp;<fmt:formatDate pattern="${month_date}" value="${billingActivity.billingPeriodEndDate}" timeZone="${currentUser.timeZone}"/>
                                
                              </span>
                              <span class="subtitle">
                                
                              </span>
                            </div>
                              <!--Info popover starts here-->
                              <div class="widget_info_popover" id="info_bubble" style="display:none">
                              <div class="popover_wrapper" >
                              <div class="popover_shadow"></div>
                              <div class="popover_contents">
                              <div class="raw_contents">
                                  <div class="raw_content_row">
                                        <div class="raw_contents_title" style="width:120px;">
                                          <span><spring:message code="label.usage.billing.balance.forward"/>:</span>
                                        </div>
                                        <div class="raw_contents_value" style="width:100px;">
                                          <span>
                                              <c:out value="${tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}" maxFractionDigits="${minFractionDigits}" minFractionDigits="${minFractionDigits}" value="${billingActivity.balanceForwardAmount}" />
                                          </span>
                                        </div>
                                      </div>
                                  <div class="raw_content_row">
                                        <div class="raw_contents_title" style="width:120px;">
                                          <span><spring:message code="label.usage.billing.payments.credits"/>:</span>
                                        </div>
                                        <div class="raw_contents_value" style="width:100px;">
                                          <span><c:out value="${tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}" maxFractionDigits="${minFractionDigits}" minFractionDigits="${minFractionDigits}" value="${billingActivity.credits}" /></span>
                                        </div>
                                      </div>
                                  <div class="raw_content_row">
                                        <div class="raw_contents_title" style="width:120px;">
                                          <span><spring:message code="label.usage.billing.new.charges"/>:</span>
                                        </div>
                                        <div class="raw_contents_value" style="width:100px;">
                                          <span><c:out value="${tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}" maxFractionDigits="${minFractionDigits}" minFractionDigits="${minFractionDigits}" value="${billingActivity.subscriptionCharges + billingActivity.utilityCharges - billingActivity.discounts + billingActivity.taxes}" /></span>
                                        </div>
                                      </div>
                                  <div class="raw_content_row">
                                        <div class="raw_contents_title" style="width:120px;">
                                          <span><spring:message code="label.usage.billing.renewal.charges"/>:</span>
                                        </div>
                                        <div class="raw_contents_value" style="width:100px;">
                                          <span><c:out value="${tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}" maxFractionDigits="${minFractionDigits}" minFractionDigits="${minFractionDigits}" value="${billingActivity.renewalCharges}" /></span>
                                        </div>
                                      </div> 
                                
                                <div class="raw_content_row">
                                  <div class="raw_contents_title">
                                    <span><spring:message code="label.state"/>:</span>
                                  </div>
                                  <div class="raw_contents_value">
                                    <span>
                                       <spring:message code="label.accountstatement.status.${billingActivity.state}"/>
                                    </span>
                                  </div>
                                </div>

                               </div>
                               </div>
                               </div>
                               </div>
                                <!--Info popover ends here-->
                            
                           </li>
            </c:forEach>
          </c:otherwise>
        </c:choose>

        </ul>
      </div>
      <div class="widget_panelnext">
        <div class="widget_navnextbox">
            <c:choose>
              <c:when test="${current_page <= 1}">
                  <a class="widget_navnext_buttons prev nonactive" href="javascript:void(0);" id="click_previous"><spring:message code="label.previous.short"/></a>
              </c:when>
              <c:otherwise>
                  <a class="widget_navnext_buttons prev" href="javascript:void(0);" id="click_previous" onclick="previousClick()"><spring:message code="label.previous.short"/></a>
              </c:otherwise>
            </c:choose> 
            
            <c:choose>
              <c:when test="${enable_next == 'True'}">
              <a class="widget_navnext_buttons next" href="javascript:void(0);" id="click_next" onclick="nextClick()"><spring:message code="label.next"/></a>
          </c:when>
              <c:otherwise>
              <a class="widget_navnext_buttons next nonactive" href="javascript:void(0);" id="click_next" ><spring:message code="label.next"/></a>
                </c:otherwise>
            </c:choose> 
          </div>
      </div>
  </div>
  <div class="widget_rightpanel" id="viewBillingActivityDiv">
    <c:if test="${empty billingActivities || billingActivities == null}">
    <jsp:include page="/WEB-INF/jsp/tiles/billing/viewbillingactivity.jsp"></jsp:include>
    </c:if>
    
  </div>
</div>
  <input type="hidden" id="tenantParam" value="<c:out value="${tenant.param}"/>"/>
  <input type="hidden" id="current_page"  value="<c:out value="${current_page}"/>"/>
  <script type="text/javascript">
    var isDelinquent = false;
  </script>
  <c:if test="${showDelinquent}">
      <sec:authorize access="hasAnyRole('ROLE_ACCOUNT_BILLING_ADMIN', 'ROLE_FINANCE_CRUD')">
        <c:if test="${stateChangeCause == 'PAYMENT_FAILURE'}">
          <script type="text/javascript">
          var isDelinquent = true;
          var showMakePaymentMessage = "<spring:message javaScriptEscape="true" code="message.tenant.delinquent.make.payment"/>";
          </script>
        </c:if>
      </sec:authorize>
      <sec:authorize access="!hasAnyRole('ROLE_ACCOUNT_CRUD', 'ROLE_ACCOUNT_ADMIN')">
        <script type="text/javascript">
          var isDelinquent = true;
        </script>
      </sec:authorize>
  </c:if>
 
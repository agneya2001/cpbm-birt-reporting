<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<script> var budget = "<c:out value="${budget}"/>";</script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/alertsubscriptions.js"></script>

<jsp:include page="js_messages.jsp"></jsp:include>
<script type="text/javascript">

var alertsUrl = "<%=request.getContextPath() %>/portal/tenants/";
var alertDetailsHtml="";

var dictionary = {  
    removealert: '<spring:message javaScriptEscape="true" code="label.remove.alert"/>',
    lightboxremovealert: '<spring:message javaScriptEscape="true" code="js.confirm.removeAlert"/>',
    lightboxbuttoncancel: '<spring:message javaScriptEscape="true" code="label.cancel"/>',  
    lightboxbuttonconfirm: '<spring:message javaScriptEscape="true" code="label.confirm"/>',
    removingAlert: '<spring:message javaScriptEscape="true" code="message.removing.alert"/>',
    editalert: '<spring:message javaScriptEscape="true" code="label.edit.alert"/>',
    editingAlert: '<spring:message javaScriptEscape="true" code="label.editing.alert"/>',
    alertHighchartTitle: '<spring:message javaScriptEscape="true" code="all.alerts.chart.title"/>',
    alertHighchartYAxisTitle: '<spring:message javaScriptEscape="true" code="all.alerts.chart.y.axis.name"/>',
    alertHighchartCategory1: '<spring:message javaScriptEscape="true" code="all.alerts.chart.category.1"/>',
    alertHighchartSeriesName1: '<spring:message javaScriptEscape="true" code="all.alerts.chart.label.spend.level"/>',
    alertHighchartCriteriaTooltip:'<spring:message javaScriptEscape="true" htmlEscape="false" code="all.alerts.chart.criteria.tooltip"/>',
    alertHighchartCriteriaTooltipPercentage: '<spring:message javaScriptEscape="true" code="all.alerts.chart.criteria.tooltip.percentage" arguments="${subscription.percentage}"/>',
    alertHighchartCriteriaTooltipEmail: '<spring:message javaScriptEscape="true" code="all.alerts.chart.criteria.tooltip.lable.email"/>',
    alertHighchartCriteriaTooltipPhone: '<spring:message javaScriptEscape="true" code="all.alerts.chart.criteria.tooltip.lable.phone"/>',
    alertHighchartYAxisStackLabel: '<spring:message javaScriptEscape="true" code="all.alerts.chart.yAxis.stacklabel.spend.budget"/>',
    alertHighchartCriteriaToolstart: '<spring:message javaScriptEscape="true" code="all.alerts.chart.criteria.tooltip.start"/>',
    settingAccountBudgetProcess:  '<spring:message javaScriptEscape="true" code="ui.label.alert.setbudget.process"/>',
    settingAccountBudgetSuccess:  '<spring:message javaScriptEscape="true" code="ui.label.alert.setbudget.success"/>',
    settingAccountBudgetFail:  '<spring:message javaScriptEscape="true" code="ui.label.alert.setbudget.fail"/>',
    alertCreateErrorDialog:  '<spring:message javaScriptEscape="true" code="label.listalerts.create.error.dialog.text"/>'
};
</script>

<div class="widget_box">
  <div class="widget_leftpanel">
    <div class="widget_titlebar">
      <h2 id="list_titlebar"><span id="list_all"><spring:message code="label.list.all"/> </span></h2>
      <c:if test="${tenant.state != 'TERMINATED'}">
      	<a class="widget_addbutton" id="add_alert_link" href="javascript:void(0);"><spring:message code="label.add.new"/></a>
      </c:if>
    </div>
    <div class="widget_searchpanel big_search_box">
      <div id="search_panel" style="margin:8px 0 2px 13px;color:#FFFFFF;">
		<sec:authorize access="hasAnyRole('ROLE_USER_CRUD','ROLE_ACCOUNT_USER_CRUD','ROLE_ACCOUNT_BILLING_ADMIN')">
			<c:set var="tenantBudgetNotSet" value="display:none"/>
			<c:set var="tenantBudgetSet" value="display:none"/>
        	 <c:choose>
     			<c:when test="${ not empty tenant.spendBudget && tenant.spendBudget > 0}">
					<c:set var="tenantBudgetSet" value=""/>	
				</c:when>
				<c:otherwise>
					<c:set var="tenantBudgetNotSet" value=""/>
				</c:otherwise>
     		</c:choose> 
        	<span style="float:left;">
        	  <spring:message code="label.alert.page.budget"/> :
            </span> 
        	<span id="tenantBudgetNotSet" style="<c:out value="${tenantBudgetNotSet}"/>"><spring:message code="label.alert.page.budget.not.set"/></span>	
        	<span id="tenantBudgetSet" style="<c:out value="${tenantBudgetSet}"/>;float: left; max-width: 90px;" class="ellipsis"><c:out value="${tenant.currency.sign}" /><span id="budgetValue"><fmt:formatNumber pattern="${currencyFormat}"  minFractionDigits="${minFractionDigits}" maxFractionDigits="${currencyFractionalDigitsLimit}" value="${tenant.spendBudget}"/></span>	</span>
        	<c:if test="${tenant.state != 'TERMINATED'}">
        	   &nbsp; | &nbsp; <a id="set_account_budget_link" href="javascript:void(0);"><spring:message code="label.set.budget"/></a>
      	  	</c:if>
        </sec:authorize>      	
      </div>
    </div>
    <div class="widget_navigation">
      <ul class="widget_navigationlist" id="grid_row_container">
                          
         <c:choose>
                     <c:when test="${(empty subscriptions || subscriptions == null) && current_page == 1}">
                        <!--look when there is no list starts here-->
                        <li class="widget_navigationlist nonlist" id="non_list">
                            <span class="navicon volumedata"></span>
                              <div class="widget_navtitlebox">
                                <span class="newlist"><spring:message code="message.no.alerts.available"/></span>
                              </div>
                              
                          </li>
                          <!--look when there is no list ends here-->
                
                     </c:when>
                          <c:otherwise>                            
                               <c:forEach var="subscriptionObj" items="${subscriptions}" varStatus="status">
                      <c:choose>
                      <c:when test="${status.index == 0}">
                          <c:set var="firstSubscriptionObj" value="${subscriptionObj}"/>
                                      <c:set var="selected" value="selected"/>
                      </c:when>
                      <c:otherwise>
                          <c:set var="selected" value=""/>
                      </c:otherwise>
                      </c:choose>  
                      
                          <li class='<c:out value="widget_navigationlist ${selected} alerts"/>' id="<c:out value="row${subscriptionObj.id}"/>" onclick="viewAlert(this)" onmouseover="showInfoBubble(this)" onmouseout="hideInfoBubble(this)">
                            <span class="navicon alerts" id="nav_icon"></span>
                            
                            <div class="widget_navtitlebox">
                              <span class="title"><c:out value="${subscriptionObj.accountHolder.accontHolderName}"/></span>
                              <span class="subtitle" id="<c:out value="spendBudgetDiv${subscriptionObj.id}"/>">
                                <spring:message code="ui.alerts.details.spendbudget.label" arguments="${subscriptionObj.percentage}"/> 
                              </span>
                            </div>
                              <!--Info popover starts here-->
                              <div class="widget_info_popover" id="info_bubble" style="display:none">
                              <div class="popover_wrapper" >
                              <div class="popover_shadow"></div>
                              <div class="popover_contents">
                              <div class="raw_contents">
                                      <div class="raw_content_row">
                                        <div class="raw_contents_title">
                                          <span><spring:message code="ui.alerts.details.header.name"/>:</span>
                                        </div>
                                        <div class="raw_contents_value">
                                          <span><c:out value="${subscriptionObj.accountHolder.accontHolderName}" /></span>
                                        </div>
                                      </div>
                                      <div class="raw_content_row">
                                        <div class="raw_contents_title">
                                          <span><spring:message code="ui.alerts.details.header.criteria"/>:</span>
                                        </div>
                                        <div class="raw_contents_value">
                                          <span id="<c:out value="criteria_hover${subscriptionObj.id}"/>"><spring:message code="ui.alerts.details.spendbudget.label" arguments="${subscriptionObj.percentage}"/></span>
                                        </div>
                                      </div>
                                      <div class="raw_content_row">
                                        <div class="raw_contents_title">
                                          <span><spring:message code="ui.alerts.details.header.type"/>:</span>
                                        </div>
                                        <div class="raw_contents_value">
                                          <span><spring:message code="subscription.type.${subscriptionObj.type}"/></span>
                                        </div>
                                      </div>
                                      <div class="raw_content_row">
                                        <div class="raw_contents_title">
                                          <span><spring:message code="ui.alerts.details.spendbudget"/>(<c:out value="${tenant.currency.sign}" />):</span>
                                        </div>
                                        <div class="raw_contents_value">
                                          <span id = "spendBudgetOnHover">
                                            <fmt:formatNumber pattern="${currencyFormat}"  minFractionDigits="${minFractionDigits}" value="${spendbudget_effectiveTenant}" />
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
  <div class="widget_rightpanel" id="viewalertDiv">
    <c:if test="${(empty subscriptions || subscriptions == null) && current_page == 1}">
      <jsp:include page="/WEB-INF/jsp/tiles/notifications/viewalert.jsp"></jsp:include>
    </c:if>
    
    
  </div>
</div>
<div id="createnewalertDiv" title='<spring:message code="label.listalerts.create"/>' style="display:none"> 
</div>
<div id="setaccountbudgetDiv" title='<spring:message code="label.listalerts.setAccountBudget"/>' style="display:none">
</div>

<div id="editalertDiv" title='<spring:message code="label.listalerts.edit.alert"/>' style="display:none"> 
</div>

<input id="tenantId" type="hidden" name="tenant" value="<c:out value="${tenant.param}"/>"/>    
<input type="hidden" id="current_page"  value="<c:out value="${current_page}"/>"/>
<input type="hidden" id="currencyFractionalDigitsLimit" value="<c:out value="${currencyFractionalDigitsLimit}" />"/>
<script> if(budget == "true"){
  setAccountBudgetGet();
};</script>
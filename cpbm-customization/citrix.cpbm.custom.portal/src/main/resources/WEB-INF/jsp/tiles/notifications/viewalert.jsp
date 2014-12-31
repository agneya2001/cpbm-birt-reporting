<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<script type="text/javascript" src="<%=request.getContextPath() %>/js/lib/HighCharts/highcharts.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/lib/HighCharts/modules/exporting.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/util/HighChartsUtil.js"></script>

<script type="text/javascript">

var alertsUrl = "<%=request.getContextPath() %>/portal/tenants/";

</script> 
<c:set var="hasWriteOnAlertsUser" value="false"/>
<sec:accesscontrollist hasPermission="WRITE" domainObject="${subscription.user}">
		<c:set var="hasWriteOnAlertsUser" value="true"/>
</sec:accesscontrollist>
<div class="widget_actionbar">
  
   <div class="widget_actionarea" id="top_actions">
      <div id="spinning_wheel" style="display:none">
        <div class="maindetails_footer_loadingpanel">
        </div>
        <div class="maindetails_footer_loadingbox first">
          <div class="maindetails_footer_loadingicon"></div>
          <p id="in_process_text"></p>
        </div>
      </div>
      <c:if test="${subscription.id != null}">
       <div class="widget_moreactions action_menu_container" title="<spring:message code='manage'/>">
          <!--Actions popover starts here-->
          <div class="widget_actionpopover" id="action_menu" style="display:none;">
              <div class="widget_actionpopover_top"></div>
                <div class="widget_actionpopover_mid">
                  <ul class="widget_actionpoplist">
<c:choose>
  <c:when test="${allowEdit && hasWriteOnAlertsUser}">
                    <li class="editalert_link" id="<c:out value="edit${subscription.id}"/>" title='<spring:message code="label.edit"/>'><spring:message code="label.edit"/></li>
                    <li class="removealert_link" id="<c:out value="remove${subscription.id}"/>" title='<spring:message code="label.remove"/>'><spring:message code="label.remove"/></li>
  </c:when>
  <c:otherwise>
                    <li title='<spring:message code="label.no.actions.available"/>'><spring:message code="label.no.actions.available"/></li>
  </c:otherwise>
</c:choose>
                    </ul>
                </div>
                <div class="widget_actionpopover_bot"></div>
            </div>
            <!--Actions popover ends here-->
         </div>
         </c:if>
    </div>
    
</div>
  
   
<div class="top_notifications">
  <div id="top_message_panel" class="common_messagebox widget" style="display:none;">
    <button type="button" class="close js_close_parent" >&times;</button>
    <span id="status_icon"></span><p id="msg"></p>
  </div>
  <div id="action_result_panel" class="common_messagebox widget" style="display:none;">
    <button type="button" class="close js_close_parent" >&times;</button>
    <span id="status_icon"></span><p id="msg"></p>
  </div>
</div>  
  <div class="widget_browser">
    <div class="widget_browsermaster">
        <div class="widget_browser_contentarea">
            <div class="widget_browsergrid_wrapper master">
                  <div class="widget_grid master even first">
                      <div class="widget_grid_labels">
                          <span><spring:message code="ui.alerts.details.header.name"/></span>
                    </div>
                    <div class="widget_grid_description">
                        <span id="account_name"><c:out value="${subscription.accountHolder.accontHolderName}" /></span>
                    </div>
                </div>
                
                <div class="widget_grid master even">
                    <div class="widget_grid_labels">
                        <span><spring:message code="ui.alerts.details.header.criteria"/></span>
                    </div>
                    <div class="widget_grid_description">
                      <span>
                        <c:if test="${subscription.percentage != null}">
                        <label id="spendBudgetPercentageDiv"><spring:message code="ui.alerts.details.spendbudget.label" arguments="${subscription.percentage}"/></label>
                        <label id="spendBudgetCurrencyDiv">&nbsp;(<c:out value="${tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}"  minFractionDigits="${minFractionDigits}" maxFractionDigits="${currencyFractionalDigitsLimit}" value="${spendbudget}"/>)</label>
                        </c:if>
                      </span>
                    </div>
                </div>
                
                <div class="widget_grid master even">
                    <div class="widget_grid_labels">
                        <span><spring:message code="all.alerts.label.spend.budget.type"/></span>
                    </div>  
                    <div class="widget_grid_description">
                      
                      <span id="alert_type">
                      <c:if test="${subscription.type != null}">
                      <spring:message code="all.alerts.type.${subscription.type}"/>
                      </c:if>
                      </span>
                    </div>
                </div>
            </div>
            <div class="widget_masterbigicons alerts">
            </div>
        </div>
    </div>
    
    <div class="widget_browser_contentarea">
      <ul class="widgets_detailstab">
          <li class="widgets_detailstab active" id="details_tab">
            <spring:message code="label.details"/>
          </li>
        </ul>
        
        <div class="widget_details_actionbox">
          <ul class="widget_detail_actionpanel">
           
          </ul>
        </div>
        
        <div class="widget_browsergrid_wrapper details">
           <div class="widget_grid details even">
            <div id="spendvsbudgetChart" style="height: 245px;width:620px;margin:6px;"></div>
          </div>
        </div>
    </div>
</div>
<input id="alertId" type="hidden" value="<c:out value="${subscription.id}"/>"/>  
<input id="currency_sign" type="hidden" value="<c:out value="${effectiveTenant.currency.sign}" />"/>

<input id="spend_cap" type="hidden" value='<c:out value="${spend_budget_alert_cap}" escapeXml="false"/>'/>
<input id="total_budget" type="hidden" value='<c:out value="${spendbudget}" escapeXml="false"/>'/>
<input id="minFractionDigits" type="hidden" value='<c:out value="${currencyFractionalDigitsLimit}" escapeXml="false"/>'/>
<input id="user_email" type="hidden" value='<c:out value="${user_email}"/>'/>
<input id="user_phone" type="hidden" value='<c:out value="${user_phone}"/>'/>
<input id="criteria_percent" type="hidden" value='<c:out value="${subscription.percentage}"/>'/>

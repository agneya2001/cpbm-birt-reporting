<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<script language="javascript">
if( typeof i18n === 'undefined' ) {
  var i18n = {};
}
if( typeof i18n.errors === 'undefined' ) {
  i18n.errors = {};
}

i18n.errors.usage = {
  usageReportExport : '<spring:message javaScriptEscape="true" code="js.errors.usage.report.export"/>',
  usageReportUnable : '<spring:message javaScriptEscape="true" code="js.errors.usage.report.unable.export"/>',
  usageReportError : '<spring:message javaScriptEscape="true" code="js.errors.usage.report.error"/>'
};

</script>


<script type="text/javascript" src="<%=request.getContextPath() %>/js/usage.js"></script>

<script type="text/javascript">
var usageBillingUrl = "<%=request.getContextPath() %>/portal/billing/usageBilling?tenant=<c:out value="${tenant.param}"/>";
var generateURDUrl = "<%=request.getContextPath() %>/portal/billing/generateUDR?tenant=<c:out value="${tenant.param}"/>";
var generatePdfUrl = "<%=request.getContextPath() %>/portal/billing/generatePdfInvoice?tenant=<c:out value="${tenant.param}"/>";
var sendEmailInvoicePDF = "<%=request.getContextPath() %>/portal/billing/sendEmailPdfInvoice?tenant=<c:out value="${tenant.param}"/>";
var dictionary = { 
    lightboxbuttoncancel: '<spring:message javaScriptEscape="true" htmlEscape="false" code="label.cancel"/>',  
    lightboxbuttonconfirm: '<spring:message javaScriptEscape="true" htmlEscape="false" code="label.confirm"/>',
    lightboxSendInvoiceEmail: '<spring:message javaScriptEscape="true" htmlEscape="false" code="js.invoice.act.send.email.confirm"/>',
    sendingInvoiceEmail: '<spring:message javaScriptEscape="true" htmlEscape="false" code="message.sending.email.invoice"/>',
    sendInvoiceEmail: '<spring:message javaScriptEscape="true" htmlEscape="false" code="label.send.invoice.email"/>'

};
</script>
<c:set var="auth" value="n" />
<sec:authorize access="hasAnyRole('ROLE_ACCOUNT_ADMIN','ROLE_ACCOUNT_BILLING_ADMIN','ROLE_FINANCE_CRUD')">
<c:set var="auth" value="y" />
<input type="hidden"  id="authorizeForPopUp" value="y">
</sec:authorize>


<input type="hidden" id="accountStatementUuid"  value="<c:out value="${accountStatementUuid}"/>"/>
<input type="hidden" id="useruuid"  value="<c:out value="${useruuid}"/>"/>
<input type="hidden" id="state"  value="<c:out value="${state}"/>"/>
<input type="hidden" id="current_page"  value="<c:out value="${current_page}"/>"/>
<input type="hidden" id="accountStatementState"  value="<c:out value="${accountStatementState}"/>"/>

<div class="widget_box">
  <div class="widget_leftpanel">
    <div class="widget_titlebar">
      <h2 id="list_titlebar"><span id="list_all"><spring:message code="label.list.all"/> </span></h2>
    </div>
    <div class="widget_searchpanel">
      <div id="search_panel" class="widget_searchcontentarea">
         <sec:authorize access="hasAnyRole('ROLE_ACCOUNT_ADMIN','ROLE_ACCOUNT_BILLING_ADMIN','ROLE_FINANCE_CRUD')">     
            <spring:message code="page.level2.allusers" var="all_users"/>
            
            <span class="label"><spring:message code="label.filter.by"/>:</span>
            <select id="userfilterdropdownforinvoices" onchange="changeUser(this);" class="select">
            <option id="ALL_USERS" value="ALL_USERS"><c:out value="${all_users}"></c:out></option>
            <c:forEach var="user" items="${tenant.users}" varStatus="usersStatus">
              <option id="${user.uuid}"  value="${user.uuid}" <c:if test="${user.uuid eq useruuid}">selected</c:if>><c:out value="${user.username}"></c:out> </option>
            </c:forEach>
            </select>
        </sec:authorize>
      </div>
    </div>
    <div class="widget_navigation">
      <ul class="widget_navigationlist" id="grid_row_container">
                          
         <c:choose>
            <c:when test="${empty accountStatements || accountStatements == null}">
              <!-- Empty list -->
            <li class="widget_navigationlist nonlist" id="non_list">
                            <span class="navicon volumedata"></span>
                              <div class="widget_navtitlebox">
                                <span class="newlist"><spring:message code="message.no.invoices.available"/></span>
                              </div>
                              
                          </li>
            <!-- end Empty list -->
      
           </c:when>
           <c:otherwise>
           <c:forEach var="currentAccountStatement" items="${accountStatements}" varStatus="status">
          
          <c:choose>
            <c:when test="${accountStatementUuid == currentAccountStatement.uuid}" >
              <c:set var="selected" value="selected active"/>
            </c:when>
            <c:otherwise>
                <c:set var="selected" value=""/>
            </c:otherwise>
            </c:choose>              
                          <li class='<c:out value="widget_navigationlist ${selected} activities"/>' id="<c:out value="row${currentAccountStatement.uuid}" />" onclick="viewActivityBill(this);" onmouseover="showInfoBubble(this)" onmouseout="hideInfoBubble(this)">
                            <span class="navicon notifications" id="nav_icon">
                            	<span class="year">
                                <fmt:formatDate pattern="yyyy" value="${currentAccountStatement.billingPeriodStartDate}" />
                              </span>
                            </span>

                            <div class="widget_navtitlebox">
                              <span class="title" style="width:180px;">
                                <spring:message code="month.date.hour.minute.format" var="month_date"/>
                                <fmt:formatDate pattern="${month_date}" value="${currentAccountStatement.billingPeriodStartDate}" timeZone="${currentUser.timeZone}"/>&nbsp;&nbsp;-&nbsp;&nbsp;<fmt:formatDate pattern="${month_date}" value="${currentAccountStatement.billingPeriodEndDate}" timeZone="${currentUser.timeZone}"/>

                              </span>
                            <c:if test="${auth eq 'y'}">
                              <span>
                                <spring:message code="label.usage.billing.net.balance.colon"/>
                                <c:out value="${tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}" minFractionDigits="${minFractionDigits}" value="${currentAccountStatement.finalCharges}"  />
                              </span>
                              </c:if>
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
                                              <c:out value="${tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}" minFractionDigits="${minFractionDigits}" value="${currentAccountStatement.balanceForwardAmount}" />
                                          </span>
                                        </div>
                                      </div>
                              <div class="raw_content_row">
                                        <div class="raw_contents_title" style="width:120px;">
                                          <span><spring:message code="label.usage.billing.payments.credits"/>:</span>
                                        </div>
                                        <div class="raw_contents_value" style="width:100px;">
                                          <span><c:out value="${tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}" minFractionDigits="${minFractionDigits}" value="${currentAccountStatement.credits}" /></span>
                                        </div>
                                      </div>
                              <div class="raw_content_row">
                                        <div class="raw_contents_title" style="width:120px;">
                                          <span><spring:message code="label.usage.billing.new.charges"/>:</span>
                                        </div>
                                        <div class="raw_contents_value" style="width:100px;">
                                          <span><c:out value="${tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}" minFractionDigits="${minFractionDigits}" value="${currentAccountStatement.subscriptionCharges + currentAccountStatement.utilityCharges - currentAccountStatement.discounts + currentAccountStatement.taxes}" /></span>
                                        </div>
                                      </div>
                              <c:if test="${currentAccountStatement.state eq 'PROVISIONAL'}">
                                <div class="raw_content_row">
                                          <div class="raw_contents_title" style="width:120px;">
                                            <span><spring:message code="label.usage.billing.renewal.charges"/>:</span>
                                          </div>
                                          <div class="raw_contents_value" style="width:100px;">
                                            <span><c:out value="${tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}" minFractionDigits="${minFractionDigits}" value="${currentAccountStatement.renewalCharges}" /></span>
                                          </div>
                                        </div>                                                                            
                               </c:if>
                                                                            
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
         <sec:authorize access="hasAnyRole('ROLE_ACCOUNT_ADMIN','ROLE_ACCOUNT_BILLING_ADMIN','ROLE_FINANCE_CRUD')">
          <div class="widget_moreactions action_menu_container" title="<spring:message code='manage'/>">
            <!--Actions popover starts here-->
            <div class="widget_actionpopover" id="action_menu" style="display:none;">
              <div class="widget_actionpopover_top"></div>
                <div class="widget_actionpopover_mid">
                  <c:choose>
                    <c:when test="${(accountStatementUuid ne null) && (accountStatementState eq 'POSTED') && (fn:length(accountStatements) gt 0) && (empty useruuid || useruuid eq 'ALL_USERS') }">
                      <ul class="widget_actionpoplist">
                        <li  class="generateurd" id="generateurd"><spring:message code="label.usage.billing.generate.udr"/></li>
                      </ul>
                      <ul class="widget_actionpoplist">
                        <li  class="generatePdfInvoice" ><a id="generatePdfInvoice" href="javascript:void(0);"><spring:message code="label.usage.billing.generate.pdf"/></a></li>
                      </ul>
                      <c:if test="${isSystemProviderUser ne 'Y'}">
                        <ul class="widget_actionpoplist">
                          <li  class="sendEmailInvoicePdf" ><a id="sendEmailInvoicePdf" href="javascript:void(0);"><spring:message code="label.usage.billing.email.invoice.pdf"/></a></li>
                        </ul>
                      </c:if>
                    </c:when>
                    <c:otherwise>
                      <ul class="widget_actionpoplist">
                        <li title='<spring:message code="label.no.actions.available"/>'><spring:message code="label.no.actions.available"/></li>
                      </ul>
                    </c:otherwise>
                  </c:choose>
  
                </div>
              <div class="widget_actionpopover_bot"></div>
            </div>
            <!--Actions popover ends here-->
          </div>
        </sec:authorize>
         
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
    <div id="spinning_wheel" style="display:none">
      <div class="widget_loadingpanel">
      </div>
      <div class="maindetails_footer_loadingbox first">
        <div class="maindetails_footer_loadingicon"></div>
          <p id="in_process_text"></p>
        </div>
      </div>
    <spring:message code="date.format" var="date_format"/>
    <spring:message code="dateonly.format" var="date_only_format"/>
    
    <sec:authorize access="hasAnyRole('ROLE_ACCOUNT_ADMIN','ROLE_ACCOUNT_BILLING_ADMIN','ROLE_FINANCE_CRUD')">
    <c:if test="${useruuid == null or useruuid == 'ALL_USERS'}">
    <c:set var="subtotal" value="${accountStatement.subscriptionCharges + accountStatement.utilityCharges - accountStatement.discounts}" ></c:set>
    <c:set var="newCharges" value="${subtotal + accountStatement.taxes}" ></c:set>
    
    <div class="widget_browsermaster">
      <div class="widget_browser_contentarea usagenbilling">
      <div class="usage_tablesummarybox">
    	<div class="usage_tablesummarypanels">
      	<div class="usage_tablescell" style="z-index:5;">
          	<div class="usage_tablescell_header balance">
              	<h2><spring:message code="label.usage.billing.balance.forward"/></h2>
              </div>
              <div class="usage_tablescell_contentbox topline ">
              	<p>  
                  <span><c:out value="${tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}" minFractionDigits="${minFractionDigits}" value="${accountStatement.balanceForwardAmount}" /></span>
                </p>
              </div>
              <div class="usage_tablescell_icons minus"></div>
          </div>
          <div class="usage_tablescell" style="z-index:4; width:128px;">
          	<div class="usage_tablescell_header payment">
                <h2 style="width:128px;"><spring:message code="label.usage.billing.payments.credits"/></h2>
              </div>
              <div class="usage_tablescell_contentbox topline ">
                <p style="margin-left:12px;">
                  <span><c:out value="${tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}" minFractionDigits="${minFractionDigits}" value="${accountStatement.credits}" /></span>
                </p>
              </div>
              <div class="usage_tablescell_icons plus"></div>
          </div>
          <div class="usage_tablescell" style="z-index:3;">
          	<div class="usage_tablescell_header newcharges topline ">
              	<h2><spring:message code="label.usage.billing.new.charges"/></h2>
              </div>
               <div class="usage_tablescell_contentbox topline">
               	<p>
                  <span><c:out value="${tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}" minFractionDigits="${minFractionDigits}" value="${newCharges}" /></span>
                </p>
               </div>
               <div class="usage_tablescell_icons equals"></div>
          </div>
           <div class="usage_tablescell" style="z-index:2;padding-right:10px;">
           	<div class="usage_tablescell_netbalance">
              	<div class="usage_tablescell_header netbalance">
                  	<h2><spring:message code="label.usage.billing.net.balance"/></h2>
                  </div>
                  <div class="usage_tablescell_contentbox" style="background:none;">
                   	<p style="margin-left:10px; display:inline; width:110px;"> 
                      <strong>
                        <span><c:out value="${tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}" minFractionDigits="${minFractionDigits}" value="${accountStatement.finalCharges}" />
                        </span>
                      </strong>
                    </p>
               	  </div>
                </div>
           </div>
           <c:if test="${(accountStatementUuid ne null) && (accountStatementState eq 'PROVISIONAL')}">
            <div class="usage_tablescell" style="z-index:1;width:124px;border-right:none;">
            	<div class="usage_tablescell_header pending">
                  <h2 style="width:124px;"><spring:message code="label.usage.billing.renewal.charges"/></h2>
                </div>
                 <div class="usage_tablescell_contentbox">
                  <p style="margin-left:10px;">
                    <span><c:out value="${tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}" minFractionDigits="${minFractionDigits}"  maxFractionDigits="${minFractionDigits}" value="${accountStatement.renewalCharges}" /></span>
                  </p>
                 </div>
            </div>
          </c:if>  
      </div>
      <div style="clear:both;"></div>
      <div class="usage_tablesummarypanels" style="margin-top:20px;">
          <div class="usage_tablescell" style="z-index:6; width:112px;">
          	<div class="usage_tablescell_header others">
                <h2 style="width:114px;"><spring:message code="label.usage.billing.subscription.charges"/></h2>
              </div>
               <div class="usage_tablescell_contentbox">
                <p style="margin-left:4px;">
                  <span><c:out value="${tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}" minFractionDigits="${minFractionDigits}" value="${accountStatement.subscriptionCharges}" /></span>
                </p>
               </div>
               <div class="usage_tablescell_icons plus"></div>
          </div>
          <div class="usage_tablescell" style="z-index:5;">
          	<div class="usage_tablescell_header others">
              	<h2><spring:message code="label.usage.billing.utility.charges"/></h2>
              </div>
               <div class="usage_tablescell_contentbox">
               	<p> 
                  <span><c:out value="${tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}" minFractionDigits="${minFractionDigits}" value="${accountStatement.utilityCharges}" /></span>
                </p>
               </div>
                <div class="usage_tablescell_icons minus"></div>
          </div>
          <div class="usage_tablescell" style="z-index:4;">
          	<div class="usage_tablescell_header others">
              	<h2><spring:message code="label.usage.billing.discount"/></h2>
              </div>
               <div class="usage_tablescell_contentbox">
               	<p>
                  <span><c:out value="${tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}" minFractionDigits="${minFractionDigits}" value="${accountStatement.discounts}" /></span>
                </p>
               </div>
               <div class="usage_tablescell_icons equals"></div>
          </div>
          <div class="usage_tablescell" style="z-index:3;">
          	<div class="usage_tablescell_header others">
              	<h2><spring:message code="label.usage.billing.sub.total"/></h2>
              </div>
               <div class="usage_tablescell_contentbox">
               	<p> 
                  <span><c:out value="${tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}" minFractionDigits="${minFractionDigits}" value="${subtotal}" /></span>
                </p>
               </div>
               <div class="usage_tablescell_icons plus"></div>
          </div>
          <div class="usage_tablescell" style="z-index:2;">
          	<div class="usage_tablescell_header others">
              	<h2><spring:message code="label.usage.billing.taxes"/></h2>
              </div>
               <div class="usage_tablescell_contentbox">
                <p> 
                  <span><c:out value="${tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}" minFractionDigits="${minFractionDigits}" value="${accountStatement.taxes}" /></span>
                </p>
               </div>
               <div class="usage_tablescell_icons equals"></div>
          </div>
            <div class="usage_tablescell last" style="z-index:1;">
          	 <div class="usage_tablescell_header newcharges">
              	<h2><spring:message code="label.usage.billing.new.charges"/></h2>
              </div>
               <div class="usage_tablescell_contentbox topline ">
               	<p>
                  <span><c:out value="${tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}" minFractionDigits="${minFractionDigits}" value="${newCharges}" /></span>
                </p>
               </div>
            </div>    
          </div>
     
        </div>
    	</div>
    </div>
    </c:if>
    </sec:authorize>
    <!--Payments and credits-->
    <c:if test="${useruuid == null or useruuid == 'ALL_USERS'}">
      <div  class="activity_usagelist_main" >
    </c:if>
    <c:if test="${useruuid != null and useruuid != 'ALL_USERS'}">
      <div  class="activity_usagelist_main_user" >
    </c:if>
    
    <sec:authorize access="hasAnyRole('ROLE_ACCOUNT_ADMIN','ROLE_ACCOUNT_BILLING_ADMIN','ROLE_FINANCE_CRUD')">
    <c:if test="${useruuid == null or useruuid == 'ALL_USERS'}">
  	<div class="usagelist_container" style="margin-top:35px;">
     <h2><spring:message code="label.usage.billing.payments.credits"/></h2>
      <div class="usagelist_box">

    <!-- Payments received --> 
    <c:choose>     
      <c:when test="${empty payments}">
        <div class="usagelist_panel">
          <div class="usagelist_header off" id="tenantPaymentsReceived">
            <div class="usagelist_header_content">
              <div class="usagelist_arrows closed" id="arrowPaymentsReceived"></div>
              <div class="usagelist_title off" id="titlePaymentsReceived"><spring:message code="label.usage.billing.payments.received"/></div>
            </div>
          </div>

          <div class="usagelist_gridbox" id="tenantPaymentsReceiveddata" style="display:none; padding: 0px; width: 100%; margin-left: 0px;">
            <div class="db_gridbox_rows">    
              <div class="db_gridbox_columns" style="width:99%;">
                <div class="db_gridbox_celltitles" >
                  <spring:message code="message.no.payments.received.data"/>
                </div>
              </div>
            </div>
          </div>
        </div>
      </c:when>       
      <c:otherwise>
          <div class="usagelist_panel">
            <div class="usagelist_header off" id="tenantPaymentsReceived">
              <div class="usagelist_header_content">
                <div class="usagelist_arrows closed" id="arrowPaymentsReceived"></div>
                <div class="usagelist_title off" id="titlePaymentsReceived"><spring:message code="label.usage.billing.payments.received"/></div>
              </div>
            </div>
             
            <div class="usagelist_gridbox" id="tenantPaymentsReceiveddata" style="display:none; padding: 0px; width: 100%; margin-left: 0px;">
              <div class="db_gridbox_rows header">
                <div class="db_gridbox_columns" style="width:60%;">
                  <div class="db_gridbox_celltitles header"><spring:message code="label.usage.billing.payments.received.payment.date"/></div>
                </div>
                <div class="db_gridbox_columns" style="width:40%;">
                  <div class="db_gridbox_celltitles header"><spring:message code="label.usage.billing.payments.received.payment.amount"/></div>
                </div>
              </div>
              <c:forEach items="${payments}" var="payment" varStatus="status">
                <c:choose>
                  <c:when test="${status.index % 2 == 0}">
                    <c:set var="rowClass" value="odd"/>
                  </c:when>
                  <c:otherwise>
                    <c:set var="rowClass" value="even"/>
                  </c:otherwise>
                </c:choose>                                          
              
                <div class="db_gridbox_rows odd">
                  <div class="db_gridbox_columns" style="width:60%;">
                    <div class="db_gridbox_celltitles">
                      <fmt:timeZone value="${currentUser.timeZone}">
                        <fmt:formatDate value="${payment.createdAt}" pattern="${date_format}"/>                                          
                      </fmt:timeZone>
                    </div>
                  </div>
                  <div class="db_gridbox_columns" style="width:40%;">
                    <div class="db_gridbox_celltitles">
                      <c:out value="${tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}"  value="${payment.transactionAmount}" minFractionDigits="${minFractionDigits}" /> 
                    </div>
                  </div>
                </div>   
               </c:forEach>           
            </div>           
          </div>
        </c:otherwise> 
        </c:choose>
       
    <!-- Credits Issued --> 
    <c:choose>     
      <c:when test="${empty creditsIssued}">
        <div class="usagelist_panel">
          <div class="usagelist_header off" id="tenantCreditsIssued">
            <div class="usagelist_header_content">
              <div class="usagelist_arrows closed" id="arrowCreditsIssued"></div>
              <div class="usagelist_title off" id="titleCreditsIssued"><spring:message code="label.usage.billing.credits.issued"/></div>
            </div>
          </div>

          <div class="usagelist_gridbox" id="tenantCreditsIssueddata" style="display:none; padding: 0px; width: 100%; margin-left: 0px;">
            <div class="db_gridbox_rows">    
              <div class="db_gridbox_columns" style="width:99%;">
                <div class="db_gridbox_celltitles" >
                  <spring:message code="message.no.credits.issued.data"/>
                </div>
              </div>
            </div>
          </div>
        </div>
      </c:when>       
      <c:otherwise>
          <div class="usagelist_panel">
            <div class="usagelist_header off" id="tenantCreditsIssued">
              <div class="usagelist_header_content">
                <div class="usagelist_arrows closed" id="arrowCreditsIssued"></div>
                <div class="usagelist_title off" id="titleCreditsIssued"><spring:message code="label.usage.billing.credits.issued"/></div>
              </div>
            </div>
             
            <div class="usagelist_gridbox" id="tenantCreditsIssueddata" style="display:none; padding: 0px; width: 100%; margin-left: 0px;">
              <div class="db_gridbox_rows header">
                <div class="db_gridbox_columns" style="width:60%;">
                  <div class="db_gridbox_celltitles header"><spring:message code="label.usage.billing.credits.issued.credit.date"/></div>
                </div>
                <div class="db_gridbox_columns" style="width:40%;">
                  <div class="db_gridbox_celltitles header"><spring:message code="label.usage.billing.credits.issued.credit.amount"/></div>
                </div>

              </div>
              <c:forEach items="${creditsIssued}" var="credit" varStatus="status">
                <c:choose>
                  <c:when test="${status.index % 2 == 0}">
                    <c:set var="rowClass" value="odd"/>
                  </c:when>
                  <c:otherwise>
                    <c:set var="rowClass" value="even"/>
                  </c:otherwise>
                </c:choose>                                          
              
                <div class="db_gridbox_rows odd">
                  <div class="db_gridbox_columns" style="width:60%;">
                    <div class="db_gridbox_celltitles">
                      <fmt:timeZone value="${currentUser.timeZone}">
                        <fmt:formatDate value="${credit.createdAt}" pattern="${date_format}"/>                      
                      </fmt:timeZone>
                    </div>
                  </div>
                  <div class="db_gridbox_columns" style="width:40%;">
                    <div class="db_gridbox_celltitles">
                      <c:out value="${tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}"  value="${credit.transactionAmount}" minFractionDigits="${minFractionDigits}" />
                    </div>
                  </div>                  
                </div>   
               </c:forEach>           
            </div>           
          </div>
        </c:otherwise> 
        </c:choose>
        </div>      
        <div class="usage_subtotalpanel custom balforwd ">
          <div class="usage_subtotalbox">
            <div class="launchvm_subtotalbox_label usage"><spring:message code="label.usage.billing.payments.credits"/></div>
            <div class="launchvm_subtotalbox_amount">
            <c:out value="${tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}" minFractionDigits="${minFractionDigits}" value="${bigPaymentsSum}" />
            </div>
          </div>
        </div>
    </div>
    </c:if>
		</sec:authorize>  
        
        	  <div class="usage_newcharges_box">
            <h2><spring:message code="label.usage.billing.new.charges"/></h2>
            <div class="usage_newcharges_wrapper">
                <div class="usagelist_container">
                  <div class="usagelist_box">
                  <spring:message code="label.billing.leftnav.subscriptions" var="subsciptions_label"/>
                  <spring:message code="phone.type.other" var="otherchargeslabel"/>
                   <c:forEach items="${newChargesMap}" var="entry" varStatus="status">
                      <c:set var="resourceType" value="${entry.key}" ></c:set>
                      <c:set var="invoiceList" value="${entry.value}" ></c:set>
                      <c:if test="${not empty invoiceList }">
                        <c:set var="resourceTypeName" value="${resourceType.resourceTypeName}" ></c:set>
                        <c:set var="invoiceType" value="subscriptionInvoice"></c:set>
                        <c:if test="${resourceTypeName=='__UTILITY__CHARGES__INVOICES__'}">
                          <spring:message code="label.usage.billing.utility.charges" var="utilitychargeslabel"/>
                          <c:set var="resourceTypeName" value="${utilitychargeslabel} + ${otherchargeslabel}" ></c:set>
                          <c:set var="invoiceType" value="utilityInvoice"></c:set>
                        </c:if>
                         <c:if test="${resourceTypeName=='__SERVICE__BUNDLE__INVOICES__'}">
                          <c:set var="resourceTypeName" value="${otherchargeslabel} ${subsciptions_label}" ></c:set>
                          <c:set var="invoiceType" value="serviceBundleInvoice"></c:set>
                         </c:if>
                         <c:if test="${invoiceType eq 'subscriptionInvoice'}">
                          <spring:message code="${resourceType.service.serviceName}.ResourceType.${resourceType.resourceTypeName}.name" var="localizedResourceTypeName"/>
                          <c:set var="resourceTypeName" value="${localizedResourceTypeName} ${subsciptions_label}" ></c:set>
                         </c:if>
                         <div class="usagelist_panel">
                        <div class="usagelist_header off" id="tenantVMBundle${status.index}">
                          <div class="usagelist_header_content">
                            <div class="usagelist_arrows closed" id="arrowVMBundle${status.index}"></div>
                            <div class="usagelist_title off" id="titleVMBundle${status.index}"><c:out value="${resourceTypeName}" /></div>
                          </div>
                        </div>
              
                        <div class="usagelist_gridbox" id="tenantVMBundle${status.index}data" style="display:none; padding: 0px; width: 100%; margin-left: 0px;">
                        <div class="db_gridbox_rows">
                               <c:choose>
                                   <c:when test="${invoiceType eq 'utilityInvoice'}">
                                    <div class="db_gridbox_columns" style="width:15%;">
                                      <div class="db_gridbox_celltitles header"><spring:message code="label.type"/></div>
                                    </div>
                                  </c:when>
                                  <c:otherwise>
                                    <div class="db_gridbox_columns" style="width:15%;">
                                      <div class="db_gridbox_celltitles header"><spring:message code="ui.label.emptylist.billing.leftnav.subscription"/></div>
                                    </div>
                                  </c:otherwise>
                                </c:choose>
                              <div class="db_gridbox_columns" style="width:11%;">
                                <div class="db_gridbox_celltitles header"><spring:message code="ui.label.emptylist.user"/></div>
                              </div>
                              <c:choose>
                                   <c:when test="${invoiceType ne 'utilityInvoice'}">
                                    <div class="db_gridbox_columns" style="width:26%;">
                                      <div class="db_gridbox_celltitles header"><spring:message code="label.usage.billing.subscription.charge.service.start"/> - <spring:message code="label.usage.billing.subscription.charge.service.end"/></div>
                                    </div>
                                  </c:when>
                                  <c:otherwise>
                                    <div class="db_gridbox_columns" style="width:26%;">
                                      <div class="db_gridbox_celltitles header"><spring:message code="ui.label.tenant.view.stateChanges.date"/></div>
                                    </div>
                                  </c:otherwise>
                                </c:choose>
                              <div class="db_gridbox_columns" style="width:13%;">
                                <div class="db_gridbox_celltitles header"><spring:message code="label.usage.billing.subscription.charge.amount"/></div>
                              </div>
                               <div class="db_gridbox_columns" style="width:13%;">
                                <div class="db_gridbox_celltitles header"><spring:message code="label.usage.billing.discount"/></div>
                              </div>
                               <div class="db_gridbox_columns" style="width:9%;" title="<spring:message code="label.usage.billing.taxes"/>">
                                <div class="db_gridbox_celltitles header ellipsis" style="width:45px;"><spring:message code="label.usage.billing.taxes"/></div>
                              </div>
                               <div class="db_gridbox_columns" style="width:13%;">
                                <div class="db_gridbox_celltitles header"><spring:message code="label.subscribe.summary.total"/></div>
                              </div>
                         </div>
                          <c:forEach items="${invoiceList}" var="invoice" varStatus="invoiceLoopStatus">
                              <c:choose>
                                <c:when test="${invoiceLoopStatus.index % 2 == 0}">
                                  <c:set var="rowClass" value="odd"/>
                                </c:when>
                                <c:otherwise>
                                  <c:set var="rowClass" value="even"/>
                                </c:otherwise>
                              </c:choose>
                                 <div class="db_gridbox_rows ${rowClass}">
                                        <c:choose>
                                          <c:when test="${invoiceType eq 'utilityInvoice'}">
                                            <div class="db_gridbox_columns" style="width:15%;">
                                              <c:if test="${invoice.type eq 'CloseOut'}">
                                                <div class="db_gridbox_celltitles"><spring:message code="label.usage.billing.closeout.invoice"/></div>                                            
                                              </c:if>
                                              <c:if test="${invoice.type eq 'DebitNote'}">
                                                <div class="db_gridbox_celltitles"><spring:message code="label.usage.billing.debitnote.invoice"/></div>
                                              </c:if>
                                              <c:if test="${invoice.type eq 'Adjustment'}">
                                                <div class="db_gridbox_celltitles"><spring:message code="label.usage.billing.adjustment.invoice"/></div>
                                              </c:if>
                                            </div>
                                          </c:when>
                                          <c:otherwise>
                                            <div class="db_gridbox_columns" style="width:15%;">
                                              <div class="db_gridbox_celltitles" > 
                                                <c:choose>
                                                  <c:when test="${not empty invoice.subscription.handle.resourceName}">
                                                    <c:set var="subscription_name" value="${invoice.subscription.handle.resourceName}"></c:set>
                                                  </c:when>
                                                  <c:otherwise>
                                                    <c:set var="subscription_name" value="${invoice.subscription.uuid}"></c:set>
                                                  </c:otherwise>
                                                </c:choose>
																										<c:set var="isBillingAdmin" value="n" />
																										  <sec:authorize access="hasRole('ROLE_ACCOUNT_BILLING_ADMIN')">
																										  <c:set var="isBillingAdmin" value="y" />
																										</sec:authorize>
                                                      <sec:authorize access="hasAnyRole('ROLE_ACCOUNT_ADMIN','ROLE_FINANCE_CRUD')">
                                                      <c:set var="isBillingAdmin" value="n" />
                                                    </sec:authorize>																										
                                                <c:choose>
                                                  <c:when test="${isBillingAdmin == 'n'}">
                                                    <a id="subscription_details_link_${invoice.subscription.uuid}" href="/portal/portal/billing/subscriptions?tenant=<c:out value="${tenant.param}"/>&id=<c:out value="${invoice.subscription.uuid}" />" 
                                                     class="js_subscription_details_popover"> ${fn:substring(subscription_name, 0,15)}</a> 
                                                  </c:when>
                                                  <c:otherwise>
                                                    <c:out value="${fn:substring(subscription_name, 0,15)}"></c:out>
                                                  </c:otherwise>
                                                </c:choose>
                                                <sec:authorize>
                                                </sec:authorize>
                                                <div id="subscription_popover_${invoice.subscription.uuid}" style="display:none;">
                                                  <div class="popover_content_container"> 
                                                    <div class="popover_rows">
                                                      <div class="row_contents_title">
                                                        <span><spring:message code="label.subscriptions.uuid"/>:</span>
                                                      </div>
                                                      <div class="row_contents_value">
                                                        <span>
                                                            <c:out value="${invoice.subscription.uuid}" />
                                                        </span>
                                                      </div>
                                                    </div>
                                                    <div class="popover_rows">
                                                      <div class="row_contents_title">
                                                        <span><spring:message code="label.subscription.details.bundle"/>:</span>
                                                      </div>
                                                      <div class="row_contents_value">
                                                        <span>
                                                            <c:out value="${invoice.subscription.productBundle.name}" />
                                                        </span>
                                                      </div>
                                                    </div>
                                                    <div class="popover_rows">
                                                      <div class="row_contents_title">
                                                        <span><spring:message code="ui.label.serviceinstance"/>:</span>
                                                      </div>
                                                      <div class="row_contents_value">
                                                        <span><c:out value="${invoice.subscription.serviceInstance.name}" /></span>
                                                      </div>
                                                    </div>
                                                    <div class="popover_rows">
                                                      <div class="row_contents_title">
                                                        <span><spring:message code="label.state"/>:</span>
                                                      </div>
                                                      <div class="row_contents_value">
                                                        <span><spring:message code="${invoice.subscription.state.code}" /></span>
                                                      </div>
                                                    </div>
                                                    </div>
                                                </div>
                                                
                                              </div>
                                            </div>
                                          </c:otherwise>
                                        </c:choose>
                                        <div class="db_gridbox_columns" style="width:11%;">
                                           <div class="db_gridbox_celltitles" title="${invoice.user.firstName} ${invoice.user.lastName}"> <c:out value="${invoice.user.firstName} ${invoice.user.lastName}"></c:out> </div>
                                        </div>
                                        <c:choose>
                                           <c:when test="${invoiceType ne 'utilityInvoice'}">
                                             <div class="db_gridbox_columns" style="width:26%;">
                                              <div class="db_gridbox_celltitles">
                                                <fmt:timeZone value="${currentUser.timeZone}">
                                                    <fmt:formatDate value="${invoice.serviceStartDate}" pattern="${date_only_format}"/>                      
                                                  </fmt:timeZone> - 
                                                  <fmt:timeZone value="${currentUser.timeZone}">
                                                    <fmt:formatDate value="${invoice.serviceEndDate}" pattern="${date_only_format}"/>                      
                                                  </fmt:timeZone>
                                              </div>
                                            </div>
                                          </c:when>
                                          <c:otherwise>
                                           <div class="db_gridbox_columns" style="width:26%;">
                                              <div class="db_gridbox_celltitles">
                                                 <fmt:timeZone value="${currentUser.timeZone}">
                                                  <fmt:formatDate value="${invoice.serviceStartDate}" pattern="${date_only_format}"/>                      
                                                </fmt:timeZone>
                                              </div>
                                            </div>
                                          </c:otherwise>
                                        </c:choose>
                                        <div class="db_gridbox_columns" style="width:13%;">
                                          <div class="db_gridbox_celltitles">
                                            <c:out value="${tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}"  value="${invoice.rawAmount}" minFractionDigits="${minFractionDigits}" />
                                           </div>
                                        </div>
                                         <div class="db_gridbox_columns" style="width:13%;">
                                          <div class="db_gridbox_celltitles">
                                          <c:out value="${tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}"  value="${invoice.discountAmount}" minFractionDigits="${minFractionDigits}" />
                                          </div>
                                        </div>
                                        <div class="db_gridbox_columns" style="width:9%;">
                                          <div class="db_gridbox_celltitles">
                                            <c:out value="${tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}"  value="${invoice.taxAmount}" minFractionDigits="${minFractionDigits}" />
                                          </div>
                                        </div>
                                        <div class="db_gridbox_columns" style="width:13%;">
                                          <div class="db_gridbox_celltitles">
                                            <a class="netUsageObjectDiv"  id="moreLink_<c:out value='${invoice.uuid}'/>" type="<c:out value='${invoice.type}'/>" style="cursor: pointer;" >
                                              <c:out value="${tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}"  value="${invoice.amount}" minFractionDigits="${minFractionDigits}" />
                                            </a>
                                          </div>
                                        </div>
                                        
                                  </div>
                                    <c:set var="invoice" value="${invoice}" scope="request"></c:set>
                                    <c:set var="currentUser" value="${currentUser}" scope="request"></c:set>
                                    <jsp:include page="dialog_invoice_details.jsp"></jsp:include>
                          </c:forEach>
                        </div>
                      </div>
                      </c:if>
                   </c:forEach>
                    </div>  
                </div>

                <div class="usagelist_container">
                   <div class="usage_subtotalpanel others">
                    <div class="usage_subtotalicons blueplus"></div> 
                      <div class="usage_subtotalbox">
                        <div class="launchvm_subtotalbox_label usage"><spring:message code="label.usage.billing.subscription.charge.amount"/></div>
                        <div class="launchvm_subtotalbox_amount">
                          <c:out value="${tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}"  value="${newBigAmount}" minFractionDigits="${minFractionDigits}"  />
                              </div>
                      </div>
                    </div>
                             
                    <div class="usage_subtotalpanel  tax">
                       <div class="usage_subtotalicons greyminus"></div>     
                      <div class="usage_subtotalbox">
                        <div class="launchvm_subtotalbox_label"><spring:message code="label.usage.billing.discount"/></div>
                        <div class="launchvm_subtotalbox_amount">
                        <c:out value="${tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}"  value="${newBigDiscount}" minFractionDigits="${minFractionDigits}" />
                       </div>
                      </div> 
                    </div>
                    
                    <div class="usage_subtotalpanel"> 
                    	<div class="usage_subtotalicons greyequals"></div>      
                      <div class="usage_subtotalbox">
                        <div class="launchvm_subtotalbox_label"><spring:message code="label.usage.billing.sub.total"/></div>
                        <div class="launchvm_subtotalbox_amount">
                        <c:out value="${tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}"  value="${newBigSubTotal}" minFractionDigits="${minFractionDigits}" />
                       </div>
                      </div> 
                    </div>
                    
                    <div class="usage_subtotalpanel tax">
                    <div class="usage_subtotalicons greyplus"></div>           
                      <div class="usage_subtotalbox">
                        <div class="launchvm_subtotalbox_label" style="left:1px;"><spring:message code="label.usage.billing.taxes"/></div>
                        <div class="launchvm_subtotalbox_amount">
                        <c:out value="${tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}"  value="${newBigTax}" minFractionDigits="${minFractionDigits}" />
                       </div>
                      </div>
                     </div>
                     <div class="usage_subtotalpanel newcharges">
                     <div class="usage_subtotalicons greyequals"></div> 
                      <div class="usage_totalbox">
                        <div class="launchvm_subtotalbox_label usage"><spring:message code="label.usage.billing.new.charges"/></div>
                        <div class="launchvm_subtotalbox_amount">
                        <c:out value="${tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}" minFractionDigits="${minFractionDigits}" value="${newBigTotal}"  />
                        </div>
                      </div>
                    </div>
                </div>
              </div>
              </div>
   
   <!--Renewal Charges-->
        <c:if test="${(accountStatementUuid ne null) && (accountStatementState eq 'PROVISIONAL')}">
          <div class="usage_newcharges_box">
            <h2><spring:message code="label.usage.billing.renewal.charges"/></h2>
            <div class="usage_newcharges_wrapper">
                <div class="usagelist_container">
                  <div class="usagelist_box">
                   <spring:message code="label.billing.leftnav.subscriptions" var="subsciptions_label"/>
                    <spring:message code="phone.type.other" var="otherchargeslabel"/>
                   <c:forEach items="${renewalChargesMap}" var="entry" varStatus="status">
                      <c:set var="resourceType" value="${entry.key}" ></c:set>
                      <c:set var="invoiceList" value="${entry.value}" ></c:set>
                      <c:set var="invoiceType" value="subscriptionInvoice"></c:set>
                      <c:if test="${not empty invoiceList }">
                      <c:set var="resourceTypeName" value="${resourceType.resourceTypeName}" ></c:set>
                      <c:choose>
                         <c:when test="${resourceTypeName=='__SERVICE__BUNDLE__INVOICES__'}">
                           <c:set var="resourceTypeName" value="${otherchargeslabel} ${subsciptions_label}" ></c:set>
                         </c:when>
                         <c:otherwise>
                           <spring:message code="${resourceType.service.serviceName}.ResourceType.${resourceType.resourceTypeName}.name" var="localizedResourceTypeName"/>
                           <c:set var="resourceTypeName" value="${localizedResourceTypeName} ${subsciptions_label}" ></c:set>
                         </c:otherwise>
                       </c:choose>
                       <div class="usagelist_panel">
                      <div class="usagelist_header off" id="renewtenantVMBundle${status.index}">
                        <div class="usagelist_header_content">
                          <div class="usagelist_arrows closed" id="renewarrowVMBundle${status.index}"></div>
                          <div class="usagelist_title off" id="renewtitleVMBundle${status.index}"><c:out value="${resourceTypeName}" /></div>
                        </div>
                      </div>
            
                      <div class="usagelist_gridbox" id="renewtenantVMBundle${status.index}data" style="display:none; padding: 0px; width: 100%; margin-left: 0px;">
                      <div class="db_gridbox_rows">
                            <div class="db_gridbox_columns" style="width:15%;">
                                <div class="db_gridbox_celltitles header"><spring:message code="ui.label.emptylist.billing.leftnav.subscription"/></div>
                            </div>   
                            <div class="db_gridbox_columns" style="width:11%;">
                              <div class="db_gridbox_celltitles header"><spring:message code="ui.label.emptylist.user"/></div>
                            </div>
                            <div class="db_gridbox_columns" style="width:26%;">
                              <div class="db_gridbox_celltitles header"><spring:message code="charge.sub.type.RENEWAL"/> <spring:message code="ui.label.tenant.view.stateChanges.date"/></div>
                            </div>
                            <div class="db_gridbox_columns" style="width:13%;">
                              <div class="db_gridbox_celltitles header"><spring:message code="label.usage.billing.subscription.charge.amount"/></div>
                            </div>
                             <div class="db_gridbox_columns" style="width:13%;">
                              <div class="db_gridbox_celltitles header"><spring:message code="label.usage.billing.discount"/></div>
                            </div>
                             <div class="db_gridbox_columns" style="width:9%;">
                              <div class="db_gridbox_celltitles header"><spring:message code="label.usage.billing.taxes"/></div>
                            </div>
                             <div class="db_gridbox_columns" style="width:13%;">
                              <div class="db_gridbox_celltitles header"><spring:message code="label.subscribe.summary.total"/></div>
                            </div>
                       </div>
                        <c:forEach items="${invoiceList}" var="invoice" varStatus="invoiceLoopStatus">
                            <c:choose>
                              <c:when test="${invoiceLoopStatus.index % 2 == 0}">
                                <c:set var="rowClass" value="odd"/>
                              </c:when>
                              <c:otherwise>
                                <c:set var="rowClass" value="even"/>
                              </c:otherwise>
                            </c:choose>
                            <c:if test="${invoice.rawAmount > 0 }">
                               <div class="db_gridbox_rows ${rowClass}">    
                                      <div class="db_gridbox_columns" style="width:15%;">
                                       <c:choose>
                                          <c:when test="${not empty invoice.subscription.handle.resourceName}">
                                            <c:set var="subscription_name"
                                              value="${invoice.subscription.handle.resourceName}"></c:set>
                                          </c:when>
                                          <c:otherwise>
                                            <c:set var="subscription_name" value="${invoice.subscription.uuid}"></c:set>
                                          </c:otherwise>
                                        </c:choose>
                                        <div class="db_gridbox_celltitles" title="${invoice.subscription.uuid}"> 
                                          <a id="subscription_details_link_${invoice.subscription.uuid}" class="js_subscription_details_popover" href="/portal/portal/billing/subscriptions?tenant=<c:out value="${tenant.param}"/>&id=<c:out value="${invoice.subscription.uuid}" />">
                                          <c:out value="${fn:substring(subscription_name, 0,15)}"> </c:out> </a> 
                                          <div id="subscription_popover_${invoice.subscription.uuid}" style="display:none;">
                                                  <div class="popover_content_container"> 
                                                    <div class="popover_rows">
                                                      <div class="row_contents_title">
                                                        <span><spring:message code="label.subscriptions.uuid"/>:</span>
                                                      </div>
                                                      <div class="row_contents_value">
                                                        <span>
                                                            <c:out value="${invoice.subscription.uuid}" />
                                                        </span>
                                                      </div>
                                                    </div>
                                                    <div class="popover_rows">
                                                      <div class="row_contents_title">
                                                        <span><spring:message code="label.subscription.details.bundle"/>:</span>
                                                      </div>
                                                      <div class="row_contents_value">
                                                        <span>
                                                            <c:out value="${invoice.subscription.productBundle.name}" />
                                                        </span>
                                                      </div>
                                                    </div>
                                                    <div class="popover_rows">
                                                      <div class="row_contents_title">
                                                        <span><spring:message code="ui.label.serviceinstance"/>:</span>
                                                      </div>
                                                      <div class="row_contents_value">
                                                        <span><c:out value="${invoice.subscription.serviceInstance.name}" /></span>
                                                      </div>
                                                    </div>
                                                    <div class="popover_rows">
                                                      <div class="row_contents_title">
                                                        <span><spring:message code="label.state"/>:</span>
                                                      </div>
                                                      <div class="row_contents_value">
                                                        <span><spring:message code="${invoice.subscription.state.code}" /></span>
                                                      </div>
                                                    </div>
                                                    </div>
                                                </div>
                                        </div>
                                      </div>
                                      <div class="db_gridbox_columns" style="width:11%;">
                                        <div class="db_gridbox_celltitles" title="${invoice.user.firstName} ${invoice.user.lastName}"> <c:out value="${invoice.user.firstName} ${invoice.user.lastName}"></c:out> </div>
                                      </div>
                                      <div class="db_gridbox_columns" style="width:26%;">
                                        <div class="db_gridbox_celltitles">
                                          <fmt:timeZone value="${currentUser.timeZone}">
                                            <fmt:formatDate value="${invoice.serviceStartDate}" pattern="${date_only_format}"/>                       
                                          </fmt:timeZone>
                                        </div>
                                      </div>
                                      <div class="db_gridbox_columns" style="width:13%;">
                                        <div class="db_gridbox_celltitles">
                                          <c:out value="${tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}"  value="${invoice.rawAmount}" minFractionDigits="${minFractionDigits}" />
                                         </div>
                                      </div>
                                       <div class="db_gridbox_columns" style="width:13%;">
                                        <div class="db_gridbox_celltitles">
                                        <c:out value="${tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}"  value="${invoice.discountAmount}" minFractionDigits="${minFractionDigits}" />
                                        </div>
                                      </div>
                                      <div class="db_gridbox_columns" style="width:9%;">
                                        <div class="db_gridbox_celltitles">
                                          <c:out value="${tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}"  value="${invoice.taxAmount}" minFractionDigits="${minFractionDigits}" />
                                        </div>
                                      </div>
                                      <div class="db_gridbox_columns" style="width:13%;">
                                        <div class="db_gridbox_celltitles">
                                          <a class="netUsageObjectDiv"  id="moreLink_<c:out value='${invoice.uuid}'/>" style="cursor: pointer;" >
                                            <c:out value="${tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}"  value="${invoice.amount}" minFractionDigits="${minFractionDigits}" />
                                          </a>
                                        </div>
                                      </div>
                                      
                                </div>
                                 <c:set var="invoice" value="${invoice}" scope="request"></c:set>
                                 <c:set var="currentUser" value="${currentUser}" scope="request"></c:set>
                                 <jsp:include page="dialog_invoice_details.jsp"></jsp:include>
                              </c:if>            
                        </c:forEach>
                      </div>
                    </div>
                    </c:if>
                   </c:forEach>
                    </div>  
                </div>

                <div class="usagelist_container">
                <div class="usage_subtotalpanel others">
                    <div class="usage_subtotalicons blueplus"></div> 
                      <div class="usage_subtotalbox">
                        <div class="launchvm_subtotalbox_label usage"><spring:message code="label.usage.billing.subscription.charge.amount"/></div>
                        <div class="launchvm_subtotalbox_amount">
                          <c:out value="${tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}"  value="${renewBigAmount}" minFractionDigits="${minFractionDigits}"  />
                              </div>
                      </div>
                    </div>
               <div class="usage_subtotalpanel  tax">
           <div class="usage_subtotalicons greyminus"></div>     
          <div class="usage_subtotalbox">
            <div class="launchvm_subtotalbox_label"><spring:message code="label.usage.billing.discount"/></div>
            <div class="launchvm_subtotalbox_amount">
            <c:out value="${tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}"  value="${renewBigDiscount}" minFractionDigits="${minFractionDigits}" />
           </div>
          </div> 
        </div>
        
        <div class="usage_subtotalpanel"> 
          <div class="usage_subtotalicons greyequals"></div>      
          <div class="usage_subtotalbox">
            <div class="launchvm_subtotalbox_label"><spring:message code="label.usage.billing.sub.total"/></div>
            <div class="launchvm_subtotalbox_amount">
            <c:out value="${tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}"  value="${renewBigSubTotal}" minFractionDigits="${minFractionDigits}" />
           </div>
          </div> 
        </div>
        
        <div class="usage_subtotalpanel tax">
        <div class="usage_subtotalicons greyplus"></div>           
          <div class="usage_subtotalbox">
            <div class="launchvm_subtotalbox_label" style="left:1px;"><spring:message code="label.usage.billing.taxes"/></div>
            <div class="launchvm_subtotalbox_amount">
            <c:out value="${tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}"  value="${renewBigTax}" minFractionDigits="${minFractionDigits}" />
           </div>
          </div>
         </div>        
            
        <div class="usage_subtotalpanel custom renewalcharges ">
        <div class="usage_subtotalicons greyequals"></div>  
          <div class="usage_subtotalbox">
            <div class="launchvm_subtotalbox_label usage"><spring:message code="label.usage.billing.renewal.charges"/></div>
            <div class="launchvm_subtotalbox_amount">
            <c:out value="${tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}"  value="${renewBigTotal}" minFractionDigits="${minFractionDigits}"  maxFractionDigits="${minFractionDigits}" />
            </div>
          </div>
         </div>
             </div>
         </div>
      </div>
   </c:if>

  <sec:authorize access="hasAnyRole('ROLE_ACCOUNT_ADMIN','ROLE_ACCOUNT_BILLING_ADMIN','ROLE_FINANCE_CRUD')">
  <c:set var="auth" value="y" />    
    <c:if test="${showUserUsages}">
      <div class="usagelist_container">
        <sec:authorize access="hasAnyRole('ROLE_ACCOUNT_ADMIN','ROLE_ACCOUNT_BILLING_ADMIN','ROLE_FINANCE_CRUD')">
          <h2><spring:message code="label.usage.billing.by.charge"/></h2>
        </sec:authorize>
        <div class="usagelist_box">
      
          <c:forEach items="${userUsageMap}" var="usgEntry" varStatus="var">
            <c:if test="${!empty usgEntry.value}">
              <div class="usagelist_panel">
                <sec:authorize access="hasAnyRole('ROLE_ACCOUNT_ADMIN','ROLE_ACCOUNT_BILLING_ADMIN','ROLE_FINANCE_CRUD')">
                  <div class="usagelist_header off" id="user__<c:out value="${usgEntry.key.username}"/>">
                    <div class="usagelist_header_content">
                      <div class="usagelist_arrows closed" id="arrow<c:out value="${usgEntry.key.username}"/>"></div>
                      <div class="usagelist_title off" id="title<c:out value="${usgEntry.key.username}"/>"><c:out value="${usgEntry.key.username}"/></div>
                      <div class="usagelist_total off" id="total<c:out value="${usgEntry.key.username}"/>">
          					  <c:out value="${tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}" minFractionDigits="${minFractionDigits}" value="${userUsageTotals[usgEntry.key]}"  />
          					  </div>
                    </div>
                  </div>
                </sec:authorize>
                <sec:authorize access="!hasAnyRole('ROLE_ACCOUNT_ADMIN','ROLE_ACCOUNT_BILLING_ADMIN','ROLE_FINANCE_CRUD')">
                  <div class="usagelist_header on" id="user__<c:out value="${usgEntry.key.username}"/>">
                    <div class="usagelist_header_content">
                      <div class="open"></div>
                      <div class="usagelist_title on" id="title<c:out value="${usgEntry.key.username}"/>"><spring:message code="label.usage.billing.your.usage"/></div>
                      <div class="usagelist_total off" id="total<c:out value="${usgEntry.key.username}"/>"> <c:out value="${tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}" minFractionDigits="${minFractionDigits}" value="${userUsageTotals[usgEntry.key]}"  />
                       </div>
                    </div>
                  </div>
                </sec:authorize>
              </div>
                
              <sec:authorize access="!hasAnyRole('ROLE_ACCOUNT_ADMIN','ROLE_ACCOUNT_BILLING_ADMIN','ROLE_FINANCE_CRUD')">
                <c:set var="dsiplayDetails" value="display:block; padding: 0px; width: 100%; margin-left: 0px;" />
              </sec:authorize>
              <sec:authorize access="hasAnyRole('ROLE_ACCOUNT_ADMIN','ROLE_ACCOUNT_BILLING_ADMIN','ROLE_FINANCE_CRUD')">
                <c:set var="dsiplayDetails" value="display:none; padding: 0px; width: 100%; margin-left: 0px;" />
              </sec:authorize>
            
              <div class="usagelist_gridbox" id="user__<c:out value="${usgEntry.key.username}"/>data" style="<c:out value="${dsiplayDetails}"/>">
                <div class="db_gridbox_rows header">
                  <div class="db_gridbox_columns" style="width:17%;">
                    <div class="db_gridbox_celltitles header"><spring:message code="label.usage.billing.product"/></div>
                  </div>
                  <div class="db_gridbox_columns" style="width:35%;">
                    <div class="db_gridbox_celltitles header"><spring:message code="label.usage.billing.bundle"/></div>
                  </div>
                  <div class="db_gridbox_columns" style="width:15%;">
                    <div class="db_gridbox_celltitles header"><spring:message code="label.usage.billing.subscribedon"/></div>
                  </div>
                  <div class="db_gridbox_columns" style="width:20%;">
                    <div class="db_gridbox_celltitles header"><spring:message code="label.usage.billing.total.usage"/></div>
                  </div>
                  <div class="db_gridbox_columns" style="width:13%;">
                    <div class="db_gridbox_celltitles header"><spring:message code="label.usage.billing.total.value"/></div>
                  </div>
                </div>
    
                <c:forEach items="${usgEntry.value}" var="usgObj" varStatus="status">
                  <c:choose>
                    <c:when test="${status.index % 2 == 0}">
                      <c:set var="rowClass" value="odd"/>
                    </c:when>
                    <c:otherwise>
                      <c:set var="rowClass" value="even"/>
                    </c:otherwise>
                  </c:choose>                                          
                
                  <div class="db_gridbox_rows odd">
                    <div class="db_gridbox_columns" style="width:17%;">
                      <div class="db_gridbox_celltitles"><c:out value="${usgObj.productName}"/></div>
                    </div>
                    <div class="db_gridbox_columns" style="width:35%;">
                        <div class="db_gridbox_celltitles"><c:out value="${usgObj.productBundleName}"/></div>
                    </div>
                    <div class="db_gridbox_columns" style="width:15%;">
                      <div class="db_gridbox_celltitles">
                      <fmt:timeZone value="${currentUser.timeZone}">
                        <fmt:formatDate value="${usgObj.subscriptionDate}" pattern="${date_only_format}"/>                      
                      </fmt:timeZone>
                      </div>
                    </div>
                    <div class="db_gridbox_columns" style="width:20%;">
                      <div class="db_gridbox_celltitles"><c:out value="${usgObj.currentUsage}" default="0.00"/> <c:out value="${usgObj.usageUnits}"/></div>
                    </div>
                    <div class="db_gridbox_columns" style="width:13%;">
                      <div class="db_gridbox_celltitles">
                      <c:out value="${tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}" minFractionDigits="${minFractionDigits}" value="${usgObj.currentSpend}"  />
                        </div>
                    </div>
                  </div>
                </c:forEach>                
              </div>
          </c:if>
        
        </c:forEach>
      </div>
      
        <div class="usage_subtotalpanel">
          <div class="usage_subtotalbox">
            <div class="launchvm_subtotalbox_label"><spring:message code="label.usage.billing.sub.total"/></div>
            <div class="launchvm_subtotalbox_amount"><c:out value="${tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}" minFractionDigits="${minFractionDigits}" value="${userBigtotal}"  />
			       </div>
          </div>
        </div>
        <div class="usage_subtotalpanel">
          <div class="usage_subtotalbox">
            <div class="launchvm_subtotalbox_label"><spring:message code="label.usage.billing.taxes"/></div>
            <div class="launchvm_subtotalbox_amount"><c:out value="${tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}" minFractionDigits="${minFractionDigits}" value="${userTax}"  />
            </div></div>
        </div>
        <div class="usage_totalpanel">
          <div class="usage_totalbox">
            <div class="launchvm_totalbox_label"><spring:message code="label.usage.billing.total"/></div>
            <div class="launchvm_totalbox_amount"><c:out value="${tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}" minFractionDigits="${minFractionDigits}" value="${userTotalAmount}"  />
      			</div>
          </div>
        </div>
    </div>
    </c:if>
  </sec:authorize>
  </div>
    </div>
  </div>
</div>

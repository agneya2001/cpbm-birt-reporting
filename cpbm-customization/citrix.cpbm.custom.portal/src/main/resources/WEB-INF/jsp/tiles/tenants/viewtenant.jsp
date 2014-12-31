<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="/WEB-INF/jsp/tiles/shared/js_messages.jsp"></jsp:include>
<script language="javascript">
var viewtenantDictionary = {    
		MEMOREQUIRED: '<spring:message javaScriptEscape="true" code="ui.accounts.all.pending.changes.memorequired"/>',
		MEMOLENGTH: '<spring:message javaScriptEscape="true" code="ui.accounts.all.pending.changes.memolength"/>'
};
</script>
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
      <div class="widget_moreactions action_menu_container" title="<spring:message code='manage'/>">
          <!--Actions popover starts here-->
          <div class="widget_actionpopover" id="action_menu" style="display:none;">
            <div class="widget_actionpopover_top"></div>
              <div class="widget_actionpopover_mid">
                <ul class="widget_actionpoplist">
                  <c:set var="hasAnyOfActions" value="false"/>
                  <spring:url value="/portal/users/listusersforaccount" var="listusers" htmlEscape="false">
                    <spring:param name="tenant"><c:out value="${tenant.param}"/></spring:param>
                    <spring:param name="secondLevel"><c:out value="true"/></spring:param>
                  </spring:url>
                  <sec:authorize access="hasRole('ROLE_USER_CRUD')">
                    <c:if test="${tenant.state != 'TERMINATED'}">
                     <c:set var="hasAnyOfActions" value="true"/>
                      <li id="editchannel_action2"><a href="<c:out value="${listusers}"/>" class="js_listusers" id="<c:out value="listusers${tenant.param}"/>"><spring:message code="ui.label.tenant.view.listUsers"/></a></li>
                    </c:if>
                  </sec:authorize>
                  <c:if test="${tenant.accountId != '00000000'}">
                    <sec:authorize access="hasAnyRole('ROLE_ACCOUNT_USER_CRUD','ROLE_ACCOUNT_BILLING_ADMIN','ROLE_FINANCE_CRUD')">
                      <spring:url value="/portal/billing/usageBilling" var="billinghistory" htmlEscape="false">
                        <spring:param name="tenant"><c:out value="${tenant.param}"/></spring:param>
                      </spring:url>  
                      <c:set var="hasAnyOfActions" value="true"/>                   
                      <li id="editchannel_action2"><a href="<c:out value="${billinghistory}"/>" class="js_billinghistory"><spring:message code="ui.label.tenant.view.billinghistory"/></a></li>
                    </sec:authorize>
                  </c:if>
                  <sec:authorize access="hasRole('ROLE_ACCOUNT_CRUD')">
                    <c:if test="${tenant.accountId != '00000000' && (tenant.state != 'TERMINATED') }"> 
                    <c:set var="hasAnyOfActions" value="true"/> 
                      <li id="editchannel_action4"><a href="javascript:void(0);" onclick="changeStateGet(this);" class="changeState" id="<c:out value="changeState${tenant.param}"/>"><spring:message code="ui.label.tenant.view.changeState"/></a></li>
                    </c:if>
                  </sec:authorize>
                  
                  <sec:authorize access="hasRole('ROLE_FINANCE_CRUD')">
                    <c:if test="${tenant.accountId != '00000000' && tenant.state != 'TERMINATED' }">
                      <c:set var="hasAnyOfActions" value="true"/>
                      <li id="editchannel_action5"><a href="javascript:void(0);" onclick="issueCreditGet(this);" class="issueCredit" id="<c:out value="issueCredit${tenant.param}"/>"><spring:message code="ui.label.tenant.view.issueCredit"/></a></li> 
                    </c:if>
                  </sec:authorize>
                  
                  <c:if test="${tenant.accountId != '00000000'}"> 
                      <spring:url value="/portal/connector/csinstances" var="connectors_list" htmlEscape="false">
                          <spring:param name="tenant"><c:out value="${tenant.param}"/></spring:param>
                      </spring:url>
                      <c:set var="hasAnyOfActions" value="true"/>
                      <li id="editchannel_action6"> 
                          <a href="<c:out value="${connectors_list}"/>">
                            <spring:message code="page.level2.services"/> 
                          </a>
                      </li>
                  </c:if>
                   
                  <sec:authorize access="hasRole('ROLE_ACCOUNT_CRUD')">        
                    <c:if test="${tenant.accountId != '00000000' && tenant.state == 'TERMINATED' }"> 
                      <c:set var="hasAnyOfActions" value="true"/>
                      <li class="remove_tenant_link"><spring:message code="ui.label.tenant.view.remove"/></li>
                    </c:if>
                  </sec:authorize>
                  
                  <c:if test="${hasAnyOfActions eq 'false'}">
                      <li id="no_actions_available_volume" title='<spring:message code="label.no.actions.available"/>'><spring:message code="label.no.actions.available"/></li>
                 </c:if>
                </ul>
              </div>
            <div class="widget_actionpopover_bot"></div>
          </div>
          <!--Actions popover ends here-->
        </div>
        <spring:url value="/portal/home" var="tenant_dashboard" htmlEscape="false">
          <spring:param name="tenant"><c:out value="${tenant.param}"/></spring:param>
          <spring:param name="secondLevel"><c:out value="true"/></spring:param>
        </spring:url>
        <div class="widget_actionbutton" id="<c:out value="${tenant_dashboard}"/>" title='<spring:message code="ui.label.tenant.view.dashboard"/>' onclick="gotoDashborad(this);">
            <div class="widget_actionsicon dashboard" ></div>
        </div>
        <c:set var="hasConfigurationRole" value="false"/>
        <sec:authorize access="hasRole('ROLE_CONFIGURATION_CRUD')">
          <c:set var="hasConfigurationRole" value="true"/>
        </sec:authorize>
        <c:if test="${tenant.accountId != '00000000' || hasConfigurationRole eq 'true' }">
        <sec:authorize access="hasRole('ROLE_ACCOUNT_CRUD')">
          <div class="widget_actionbutton" id="<c:out value="edit${tenant.param}"/>" title='<spring:message code="ui.label.tenant.view.edit"/>' onclick="editTenantGet(this);">
            <div class="widget_actionsicon edit" ></div>
        </div>
        </sec:authorize>
        </c:if>
          <input type='hidden' id='tenant_param' value='<c:out value="${tenant.param}"/>'>
    
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
              <span><spring:message code="ui.accounts.all.header.name"/></span>
            </div>
            <div class="widget_grid_description">
              <span><c:out value="${tenant.name}"></c:out></span>
            </div>
          </div>
          
          <div class="widget_grid master even">
            <div class="widget_grid_labels">
              <span><spring:message code="ui.accounts.all.header.accounttype"/></span>
            </div>  
            <div class="widget_grid_description">
              <span>
                <spring:message code="registration.accounttype.${tenant.accountType.nameLower}"/>
              </span>
            </div>
          </div>
          
          <div class="widget_grid master even">
            <div class="widget_grid_labels" style="height:39px;">
              <span><spring:message code="ui.accounts.all.header.state"/></span>
            </div>  
            <div class="widget_grid_description">
              <c:set var="state_class" value="" />
              <c:if test="${tenant.state=='NEW'}">
                <c:set var="state_class" value="new_accounts" />
              </c:if>
              <c:if test="${tenant.state=='ACTIVE'}">
                <c:set var="state_class" value="active_accounts" />
              </c:if>
              <c:if test="${tenant.state=='LOCKED'}">        
                <c:set var="state_class" value="restricted_accounts" />
              </c:if>
              <c:if test="${tenant.state=='TERMINATED'}">
                <c:set var="state_class" value="terminated_accounts" />
              </c:if>
              <c:if test="${tenant.state=='SUSPENDED'}">        
                <c:set var="state_class" value="suspended_accounts" />
              </c:if>
              <span class="state" style="padding:0 4px 0;">
                <spring:message code="${tenant.state.code}"/>
              </span>
            </div>
          </div>
          
      </div>
      <c:set var="accountClass" value="" />
      <c:if test="${tenant.accountType.name=='SYSTEM'}">
        <c:set var="accountClass" value="system" />
      </c:if>
      <c:if test="${tenant.accountType.name=='RETAIL'}">
        <c:set var="accountClass" value="retail" />
      </c:if> <c:if test="${tenant.accountType.name=='Corporate'}">
        <c:set var="accountClass" value="corporate" />
      </c:if>
      <c:if test="${tenant.accountType.name=='Trial'}">
        <c:set var="accountClass" value="trial" />
      </c:if>       
      <div class="widget_masterbigicons accounts <c:out value="${accountClass}" />"></div>
    </div>
  </div>
        
  <div class="widget_browser_contentarea">
    <spring:message code="dateonly.format" var="dateonly_format"/>
    <ul class="widgets_detailstab">
      <li class="widgets_detailstab active" id="details_tab" onclick="details_tab_click(this);">
         <spring:message code="ui.label.tenant.viewDeatails"/>
      </li>
      <sec:authorize access="hasRole('ROLE_CLOUD_MANAGEMENT')">
      <c:if test="${tenant.accountId != '00000000' && (tenant.state != 'TERMINATED') }"> 
        <li class="widgets_detailstab nonactive" id="viewLimits_tab" onclick="listAccountLimits(this);" uuid="<c:out value="${tenant.param}" />"
                         tenantid="<c:out value="${tenant.id}" />" >
              <spring:message code="ui.accounttypes.list.page.servicecontrols"/>
        </li>
      </c:if>
      </sec:authorize>
      <sec:authorize access="hasRole('ROLE_ACCOUNT_CRUD')">
        <c:if test="${tenant.accountId != '00000000'}">
          <li class="widgets_detailstab nonactive" id="viewStateChanges_tab" refid="<c:out value="viewStateChanges${tenant.param}"/>"><spring:message code="ui.label.tenant.view.stateChanges"/></li> 
        </c:if>
      </sec:authorize>
      <sec:authorize access="hasAnyRole('ROLE_ACCOUNT_MGMT')">
        <li class="widgets_detailstab nonactive" id="viewPendingChanges_tab" onclick="viewPendingChangesGet(this);" ><spring:message code="ui.accounts.all.pending.tasks"/></li>
      </sec:authorize>
    </ul>
    
    <div id="details_div">
    <div class="widget_details_actionbox">
      <ul class="widget_detail_actionpanel"></ul>
    </div>
    <div class="widget_browsergrid_wrapper details" id="details_content">
      <div class="widget_grid details even">
        <div class="widget_grid_labels">
           <span><spring:message code="ui.accounts.all.header.accountid"/></span>
        </div>
        <div class="widget_grid_description" >
          <span>
            <c:out value="${tenant.accountId}"></c:out>
          </span>
        </div>
      </div>
      <div class="widget_grid details odd">
        <div class="widget_grid_labels">
           <span><spring:message code="ui.accounts.all.header.masteruser"/></span>
        </div>
        <div class="widget_grid_description" >
          <span>
            <c:out value="${tenant.owner.username}"></c:out>
          </span>
        </div>
      </div>
      <div class="widget_grid details even">
        <div class="widget_grid_labels">
           <span><spring:message code="ui.accounts.all.header.channel"/></span>
        </div>
        <div class="widget_grid_description" >
          <span>
            <c:out value="${tenant.sourceChannel.name}"></c:out>
          </span>
        </div>
      </div>      
      <div class="widget_grid details odd">
        <div class="widget_grid_labels">
           <span><spring:message code="ui.accounts.all.header.anniversarydate"/></span>
        </div>
        <div class="widget_grid_description" >
          <span>
            <fmt:formatDate value="${tenant.createdAt}" pattern="${dateonly_format}" timeZone="${currentUser.timeZone}"/>
          </span>
        </div>
      </div>
      <div class="widget_grid details even">
        <div class="widget_grid_labels">
          <span><spring:message code="ui.products.label.create.catalog.select.currency"/></span>
        </div>
        <div class="widget_grid_description">
          <span><c:out value="${tenant.currency.currencyCode}" /> (<c:out value="${tenant.currency.sign}" />)</span>
        </div>
      </div>
      <div class="widget_grid details odd">
        <div class="widget_grid_labels">
           <span><spring:message code="ui.accounts.all.header.creditbalance"/></span>
        </div>
        <div class="widget_grid_description" >
          <span>
            <c:out value="${tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}" minFractionDigits="${minFractionDigits}"  value="${creditBalance}"  />
          </span>
        </div>
      </div>
      <div class="widget_grid details even">
        <div class="widget_grid_labels">
           <span><spring:message code="ui.accounts.all.header.spendlimit"/></span>
        </div>
        <div class="widget_grid_description" >
          <span>
           <c:choose>
				<c:when test="${tenant.spendLimit != null}">
					<c:out value="${tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}" minFractionDigits="${minFractionDigits}"  value="${tenant.spendLimit}"  />
				</c:when>
				<c:otherwise>
					<spring:message code="label.alert.page.budget.not.set"/>
				</c:otherwise>
		   </c:choose>
          </span>
        </div>
      </div>
      <div class="widget_grid details odd">
        <div class="widget_grid_labels">
           <span><spring:message code="ui.accounts.all.header.discount.percent"/></span>
        </div>
        <div class="widget_grid_description" >
          <span>
            <fmt:formatNumber pattern="${currencyFormat}" minFractionDigits="${minFractionDigits}" value="${tenant.tenantExtraInformation.discountPercent}"/>
          </span>
        </div>
      </div>
      <div class="widget_grid details even">
        <div class="widget_grid_labels">
           <span><spring:message code="ui.accounts.all.header.expires"/></span>
        </div>
        <div class="widget_grid_description" >
          <span>
            <c:choose>
              <c:when test="${tenant.accountType.trial && tenant.trialAccount != NULL && tenant.trialAccount.expiryDate != NULL}">
                <fmt:formatDate value="${tenant.trialAccount.expiryDate}" pattern="${dateonly_format}" type="date" dateStyle="MEDIUM"/>
              </c:when>
              <c:otherwise>
                <spring:message code="ui.accounts.all.header.expires.never"/>
              </c:otherwise>               
            </c:choose>
          </span>
        </div>
      </div>
    <div class="widget_grid details odd">
        <div class="widget_grid_labels">
           <span><spring:message code="ui.accounttypes.list.page.pm"/></span>
        </div>
        <div class="widget_grid_description" >
          <span>
            <c:if test="${tenant.accountType.paymentModes == 1}">
              <spring:message code="ui.accounts.all.header.paymentmode.1"/>
            </c:if>
            <c:if test="${tenant.accountType.paymentModes == 2}">
              <spring:message code="ui.accounts.all.header.paymentmode.2"/>
            </c:if>
            <c:if test="${tenant.accountType.paymentModes == 4}">
              <spring:message code="ui.accounts.all.header.paymentmode.4"/>
            </c:if>
            <c:if test="${tenant.accountType.paymentModes == 8}">
              <spring:message code="ui.accounts.all.header.paymentmode.8"/>
            </c:if>
          </span>
        </div>
     </div>
    <div class="widget_grid details even">
    	<div class="widget_grid_labels">
       		<span><spring:message code="ui.label.tenant.view.UserLimit"/></span>
    	</div>
    	<div class="widget_grid_description" >
      		<span>
        		<c:out value="${tenant.maxUsers}"></c:out>
      		</span>
    	</div>
   </div>
    </div>
    </div>
    <sec:authorize access="hasAnyRole('ROLE_ACCOUNT_MGMT')">
      <div id="viewPendingChanges_details" style="display:none;">
      <div class="widget_details_actionbox">
        <ul class="widget_detail_actionpanel"></ul>
      </div>
      
        <div class="widget_browsergrid_wrapper fixed" >
          <div class="widget_grid details header">
            <div class="widget_grid_cell" style="padding:1px;width:58%;">
              <span class="header"><spring:message code="ui.accounts.all.pending.tasks"/></span>
            </div>
            <div class="widget_grid_cell" style="padding:1px;width:20%;">
              <span class="header"><spring:message code="ui.accounts.all.pending.changes.create.date"/></span>
            </div>
          </div>
          <c:choose>
          <c:when test="${empty pendingActions || pendingActions == null}">
				<c:set var="noListDisplayMode" value="display:block;"/>
			</c:when>
			<c:otherwise>
				<c:set var="noListDisplayMode" value="display:none;"/>
			</c:otherwise>
			</c:choose>
			<spring:message var="pendingTasksMsg" code="ui.label.emptylist.pending.tasks"></spring:message>
	        <div class="grid_rows even" id="task_non_item_list" style="<c:out value="${noListDisplayMode}" />">
	            <div class="grid_row_cell" style="padding:1px;width:90%;">
	              <div class="row_celltitles"><spring:message code="ui.label.emptylist.notavailable" arguments="${pendingTasksMsg}" htmlEscape="false"/></div>
	            </div>
	         </div>
          <c:forEach items="${pendingActions}" var="pendingChange" varStatus="pendingChangeStatus">
              <c:choose>
                <c:when test="${pendingChangeStatus.index % 2 == 0}">
                  <c:set var="rowClass" value="odd"/>
                </c:when>
                <c:otherwise>
                    <c:set var="rowClass" value="even"/>
                </c:otherwise>
              </c:choose>  
              <div class="<c:out value="grid_rows ${rowClass} pendingTask"/>" id="taskRow${pendingChange.key.id}">                   
                <div class="grid_row_cell" style="padding:1px;width:58%;">
                      <div class="row_celltitles"><spring:message code="ui.task.type.${pendingChange.key.type}.name" /></div>
                </div>
                <div class="grid_row_cell" style="padding:1px;width:20%;">
                  <div class="row_celltitles">
                  	<fmt:formatDate value="${pendingChange.key.createdAt}" pattern="${dateonly_format}" type="date" dateStyle="MEDIUM"/>
                  </div>
                </div> 
                <div class="grid_row_cell" style="padding:1px;width:20%;">
                  <div class="row_celltitles"> 
										<c:choose>
										  <c:when test="${pendingChange.key.displayMode == 'POPUP'}">
                        <a href="javascript:void(0);" class="taskPopup taskname" id="taskPopup${pendingChange.key.uuid}">
														<spring:message code="message.view.pendingactions.click" />
											  </a>
										  </c:when>
											<c:otherwise>
											  <a href="${pendingChange.value}"><spring:message code="message.view.pendingactions.click" /></a>
											</c:otherwise>
										</c:choose>
									</div>
                </div>     
              </div>
         </c:forEach>
        </div>
        </div>
      </sec:authorize>
		<div id="tenantAccountLimitsDivOuter" style="display: none">
		  <div class="widget_details_actionbox" style="height:35px;">
		    <div style="padding:8px;">
					<input type="hidden" id="tenantParam" value="${effectiveTenant.param}" />
					<c:if test="${not empty services}">
						<span>
							<spring:message code="ui.label.service.sub.title" />
						</span>
						<select id="selectedService" class="select" style="max-width: 200px;" onchange="changeInstances()">
							<c:forEach var="service" items="${services}" varStatus="status">
								<option value="${service.uuid}"><spring:message  code="${service.serviceName}.service.name"/></option>
							</c:forEach>
						</select>
					</c:if>
					<c:if test="${not empty instances}">
						<span><spring:message code="ui.home_service.page.title.service.instance" />:&nbsp;</span>
						<select id="selectedInstance" class="select" style="max-width: 240px;" onchange="showControls(this)">
							<!-- populate on service change -->
						</select>
					</c:if>
					<span style="display: none">
						<c:if test="${not empty instances}">
							<select id="hiddeninstances" class="select">
								<c:forEach var="instance" items="${instances}" varStatus="status">
									<option value="${instance.service.uuid}.${instance.uuid}">${instance.name}</option>
								</c:forEach>
							</select>
						</c:if>
					</span>
				</div>
               <c:if test="${empty instances}">
                  <div class="grid_header_title"><spring:message code="label.no.instance.enabled" /></div>
               </c:if>
			</div>
			<div id="tenantAccountLimitsDiv"></div>
		</div>

		<div id="viewStateChangesDiv"></div>
  </div>
   
</div>

<div id="approvalTask_panel" title="<spring:message code="task.approval.dialog.title" />" style="display: none">    
</div>

<div id="spinning_wheel_rhs" style="display: none;">
	<div class="widget_blackoverlay widget_rightpanel" style="height: 100%"></div>
	<div class="widget_loadingbox fullpage">
		<div class="widget_loaderbox">
			<span class="bigloader"></span>
		</div>
		<div class="widget_loadertext">
			<p id="in_process_text">
				<spring:message code="label.loading" />
			</p>
		</div>
	</div>
</div>

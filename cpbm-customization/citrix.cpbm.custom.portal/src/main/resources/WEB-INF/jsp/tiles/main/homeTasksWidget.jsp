<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec"	uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<script language="javascript">
	var tenantParam = '<c:out value="${tenant.param}"/>';
</script>
<div class="db_statsbox default">
	<div class="db_statsbox title task <c:out value="${userHome}"/>">
		<h2>
			<spring:message code="ui.home.page.title.my.tasks" />
		</h2>
	</div>
	<div class="db_statsbox contentarea">
		<c:choose>
			<c:when test="${empty taskUrlMap || taskUrlMap == null}">
				<c:set var="noListDisplayMode" value="display:block;"/>
			</c:when>
			<c:otherwise>
				<c:set var="noListDisplayMode" value="display:none;"/>
			</c:otherwise>
			</c:choose>
				<spring:message var="pendingTasksMsg"
					code="ui.label.emptylist.pending.tasks"></spring:message>
				<div id="task_non_item_list" class="db_flipcounter_liststyle" style="<c:out value="${noListDisplayMode}" />">
					<div class="dashboard_noitem_messagebox">
						<span class="non_list"><spring:message var="noTasksMessage" code="ui.label.emptylist.tasks" ></spring:message>
                            <spring:message code="ui.label.emptylist.notavailable" arguments="${noTasksMessage}" htmlEscape="false"/></span>
					</div>
				</div>
				<spring:message code="dateonly.format" var="dateonly_format"/>
				<div class="db_tasklist">
				<ul>
				<c:forEach items="${taskUrlMap}" var="pendingChange"
					varStatus="pendingChangeStatus">
					<li id="taskRow${pendingChange.key.id}">
						<c:choose>
							<c:when test="${pendingChange.key.displayMode == 'POPUP'}">
								<c:choose>
									<c:when
										test="${pendingChange.key.businessTransaction.type == 'tenantStateChange'}">
										<c:set var="initialState">
						            		<spring:message code="tenant.state.${fn:toLowerCase(pendingChange.key.businessTransaction.tenantInitialState)}"/>
						            	</c:set>
						            	<c:set var="targetState">
						            		<spring:message code="tenant.state.${fn:toLowerCase(pendingChange.key.businessTransaction.tenantTargetState)}"/>
						            	</c:set>
										<c:set var="transactionMessage">
											<spring:message
												code="ui.task.businesstransaction.${pendingChange.key.businessTransaction.type}"
												arguments="${initialState},${targetState},${pendingChange.key.tenant.name}"></spring:message>
										</c:set>
									</c:when>
									<c:when
										test="${pendingChange.key.businessTransaction.type == 'tenantAccountTypeConversion'}">
										<c:set var="initialAT">
						            		<spring:message code="page.level2.${fn:toLowerCase(pendingChange.key.businessTransaction.accountTypeInitial.name)}"/>
						            	</c:set>
						            	<c:set var="targetAT">
						            		<spring:message code="page.level2.${fn:toLowerCase(pendingChange.key.businessTransaction.accountType.name)}"/>
						            	</c:set>
										<c:set var="transactionMessage">
											<spring:message
												code="ui.task.businesstransaction.${pendingChange.key.businessTransaction.type}"
												arguments="${initialAT},${targetAT},${pendingChange.key.tenant.name}"></spring:message>
										</c:set> 										
									</c:when>
									<c:when
										test="${pendingChange.key.businessTransaction.type == 'subscriptionActivation'}">
										<c:set var="subscriptionInitialState">
						            		<spring:message code="subscription.state.${fn:toLowerCase(pendingChange.key.businessTransaction.subscriptionInitialState)}"/>
						            	</c:set>
						            	<c:set var="subscriptionTargetState">
						            		<spring:message code="subscription.state.${fn:toLowerCase(pendingChange.key.businessTransaction.subscriptionTargetState)}"/>
						            	</c:set>
						            	<c:set var="transactionMessage">
											<spring:message code="ui.task.businesstransaction.${pendingChange.key.businessTransaction.type}"
												arguments="${subscriptionInitialState},${subscriptionTargetState},${pendingChange.key.businessTransaction.subscription.user.lastName}"></spring:message>
										</c:set>										
									</c:when>
									<c:otherwise>
										<c:set var="transactionMessage">
											<spring:message
												code="ui.task.businesstransaction.${pendingChange.key.businessTransaction.type}"
												arguments="null,null,${pendingChange.key.tenant.name}"></spring:message>
										</c:set>
									</c:otherwise>
								</c:choose>
								<c:set var="taskMessage">
										<spring:message code="ui.widget.task.type.${pendingChange.key.type}.name"/>
								</c:set>
								<a href="javascript:void(0);" class="taskPopup taskname"
									id="taskPopup<c:out value='${pendingChange.key.uuid}'/>"> <spring:message
										code="ui.widget.task.approval"
										arguments="${taskMessage},${transactionMessage}" htmlEscape="false"/>
								</a>
							</c:when>
							<c:otherwise>
								<a href="<%=request.getContextPath() %>/portal/<c:out value='${pendingChange.value}'/>" class="taskname">
									<spring:message code="ui.widget.task.type.${pendingChange.key.type}.name" arguments="${pendingChange.key.tenant.name}"/>
								</a>
							</c:otherwise>
						</c:choose>
						<span class="datestamp">
								<fmt:formatDate value="${pendingChange.key.createdAt}" pattern="${dateonly_format}" type="date" dateStyle="MEDIUM" timeZone="${currentUser.timeZone}"/>
						</span>
					</li>
				</c:forEach>
				</ul>
				</div>
				<div class="db_statsbox_footerlinksbox">
		          <spring:url value="/portal/tasks/" var="all_tasks_path" htmlEscape="false">
		            <spring:param name="tenant"><c:out value="${tenant.param}"/></spring:param>
		          </spring:url> 
		            <p><a href="<c:out value="${all_tasks_path}"/>"><spring:message code="ui.home.page.title.viewalltasks"/></a></p>
		        </div>				
	</div>
</div>
<div id="approvalTask_panel" title="<spring:message code="task.approval.dialog.title"/>" style="display: none">    
</div>
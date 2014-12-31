<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec"	uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div class="dialog_formcontent ">
<spring:message code="date.format" var="date_format"/>
	<div class="dialog_detailsbox">
		<div class="widgetwizard_titleboxes details">
			<h2>
				<spring:message
					code="header.workflow.details.${workflow.workflowName}.name" />
			</h2>
			<span><spring:message
					code="header.workflow.details.${workflow.workflowName}.description" /></span>
			<sec:authorize access="hasAnyRole('ROLE_ACCOUNT_CRUD','ROLE_ACCOUNT_MGMT')">
				<c:if test="${workflow.state == 'ERROR' || workflow.state == 'FAILURE'}">
					<a class="workflow_resetbutton" href="javascript:void(0);"><spring:message code="lable.workflow.reset" /></a>
				</c:if>
			</sec:authorize>
		</div>
		<div class="workflow_box">
			<c:forEach var="bucketMap" items="${bucketMap}">
				<div class="bucket_wrapper">
					<div class="titlepanel">
						<div class="default_spacer bucket"
							onclick="expandActivityDetails(<c:out value="${bucketMap.key.order}"/>)">
							<c:choose>
								<c:when test="${workflow.currentBucket ==  bucketMap.key.order && workflow.state == 'COMPLETED'}">
									<c:set var="style1" value="completed"></c:set>
								</c:when>
								<c:when test="${workflow.currentBucket ==  bucketMap.key.order}">
									<c:set var="style1" value="current"></c:set>
								</c:when>
								<c:when test="${workflow.currentBucket >  bucketMap.key.order}">
									<c:set var="style1" value="completed"></c:set>
								</c:when>
								<c:otherwise>
									<c:set var="style1" value="upcoming"></c:set>
								</c:otherwise>
							</c:choose>
							<span class="stepbox <c:out value="${style1}"/>"> <c:if
									test="${workflow.currentBucket <=  bucketMap.key.order && workflow.state != 'COMPLETED'}">
									<c:out value="${bucketMap.key.order}"></c:out>
								</c:if>
							</span>
							<div class="titlearea">
								<h3>
									<c:choose>
										<c:when test="${not empty bucketMap.key.name}">
											<spring:message code="title.workflow.bucket.${bucketMap.key.name}.name" />
										</c:when>
										<c:otherwise>
											<spring:message code="title.workflow.bucket.default.name" arguments="${bucketMap.key.order}"/>
										</c:otherwise>
									</c:choose>
								</h3>
							</div>
							<div class="statusearea">
								<c:choose>
									<c:when
										test="${workflow.currentBucket ==  bucketMap.key.order && workflow.state == 'COMPLETED'}">
										<span class="status"><spring:message
												code="label.workflow.bucket.completed" /></span>
									</c:when>
									<c:when
										test="${workflow.currentBucket ==  bucketMap.key.order}">
										<span class="status"><spring:message
												code="label.workflow.bucket.current" /></span>
									</c:when>
									<c:when test="${workflow.currentBucket >  bucketMap.key.order}">
										<span class="status"><spring:message
												code="label.workflow.bucket.completed" /></span>
									</c:when>
									<c:otherwise>
										<span class="status"><spring:message
												code="label.workflow.bucket.not.yet.started" /></span>
									</c:otherwise>
								</c:choose>
							</div>
						</div>
					</div>
					<c:choose>
						<c:when test="${workflow.currentBucket ==  bucketMap.key.order && workflow.state != 'COMPLETED'}">
							<c:set var="style" value=""></c:set>
						</c:when>
						<c:otherwise>
							<c:set var="style" value="hide"></c:set>
						</c:otherwise>
					</c:choose>
					<div class="activitypanel <c:out value="${style}"/>"
						id="activitypanel<c:out value="${bucketMap.key.order}"/>">
						<div class="workflow_timleine"></div>
						<c:forEach var="activity" items="${bucketMap.value}">
							<div class="workflow_activitycomponents">
								<c:choose> 
									<c:when test="${activity.status == 'NEW'}">
										<div class="workflow_statusicon new"></div>
									</c:when>
									<c:when test="${activity.status == 'SUCCESS'}">
										<div class="workflow_statusicon success"></div>
									</c:when>
									<c:when
										test="${activity.status == 'ERROR' || activity.status == 'FAILURE' ||  activity.status == 'TERMINATED'}">
										<div class="workflow_statusicon error" ></div>										
									</c:when>
									<c:otherwise>
											<div class="workflow_statusicon waiting"></div>
									</c:otherwise>
								</c:choose>
								<div class="workflow_activitycontainer">
									<div class="default_spacer">
										<div class="titlearea activity">
											<h3>
												<spring:message
													code="label.workflow.activity.${activity.name}.name" />
											</h3>
											<c:if test="${activity.status != 'NEW'}">
												<h4>
													<fmt:formatDate value="${activity.activityRecord.lastRun}" pattern="${date_format}" type="date" dateStyle="MEDIUM" timeZone="${currentUser.timeZone}"/>
												</h4>
											</c:if>
										</div>
										<div class="statusearea"  >
											<span class="status js_workflow_status_error" data-toggle="popover" data-trigger="hover" data-placement="bottom" data-container="body" data-content="${activity.activityRecord.task.memo}">
												<c:choose>
													<c:when test="${not empty workflow.memo}">
														<a href="javascript:void(0)" title="<spring:message code='label.view.details' />">
															<spring:message code="label.workflow.activity.state.${fn:toLowerCase(activity.status)}" />
														</a>
													</c:when>
													<c:otherwise>
														<spring:message code="label.workflow.activity.state.${fn:toLowerCase(activity.status)}" />
													</c:otherwise>
												</c:choose>
											</span>
										</div>
									</div>
									<c:if test="${not empty workflow.memo  and (activity.status == 'ERROR' or activity.status == 'FAILURE' or activity.status == 'TERMINATED')}">
										<div class="error"  style="display:none;">
											<c:out value="${workflow.memo}" />
										</div>
									</c:if>
								</div>
							</div>
						</c:forEach>
					</div>
				</div>
			</c:forEach>
		</div>
	</div>
</div>
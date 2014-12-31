<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/tenants.js"></script>

<div class="widget_details_actionbox">
	<ul class="widget_detail_actionpanel">
		<spring:url value="/portal/home" var="tenant_dashboard" htmlEscape="false">
			<spring:param name="tenant">
				<c:out value="${tenantuuid}" />
			</spring:param>
			<spring:param name="secondLevel">
				<c:out value="true" />
			</spring:param>
		</spring:url>

		<sec:authorize access="hasAnyRole('ROLE_ACCOUNT_CRUD')">
			<c:if test="${tenantid != 1 && fn:length(resourceLimitsMap) > 0}">
				<li>
					<a href="javascript:void(0);" onclick="editAccountLimits(this);" class="editAccountLimits"
						tenantuuid="${tenantuuid}" instanceparam="${instance.uuid}">
						<spring:message code="ui.label.tenant.view.edit" />
					</a>
				</li>
			</c:if>
		</sec:authorize>
	</ul>
</div>
<div class="widget_browsergrid_wrapper details" id="instanceparam_${instance.uuid}">
	<c:forEach var="account_control_property" items="${account_control_edit_properties}" varStatus="status">
		<div class="widget_grid details even">
			<div class="widget_grid_labels" style="width:200px">
				<span style="width:auto">
					<spring:message code="${instance.service.serviceName}.${account_control_property.name}.name" />
				</span>
			</div>
			<div class="widget_grid_description">
				 <span id="${account_control_property.name}"> 
					<c:out value="${resourceLimitsMap[account_control_property.name]}"></c:out>
				</span>
			</div>
		</div>
	</c:forEach>
</div>

<!-- Title -->
<div id='messageBox' class='common_messagebox success successMessage' style="margin: 0; display: none"></div>



<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec"	uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<script type="text/javascript"	src="<%=request.getContextPath() %>/js/tickets.js"></script>


		<c:forEach var="ticket" items="${tickets}" varStatus="status">
			<c:choose>
				<c:when test="${status.index == 0 && isFirst}">
					<c:set var="firstTicket" value="${ticket}" scope="request"/>
					<c:set var="selected" value="selected" />
				</c:when>
				<c:otherwise>
					<c:set var="selected" value="" />
				</c:otherwise>
			</c:choose>
			<c:choose>
				<c:when test="${status.index % 2 == 0}">
					<c:set var="rowClass" value="odd" />
				</c:when>
				<c:otherwise>
					<c:set var="rowClass" value="even" />
				</c:otherwise>
			</c:choose>
			<div class="<c:out value="db_gridbox_rows ${selected} ${rowClass}"/>"
				onclick="viewTicket(this)"
				id="ticket<c:out value="${ticket.caseNumber}"/>">
			<div id="gb_CN_<c:out value="${ticket.caseNumber}"/>"
				class="db_gridbox_columns" style="width: 12%;">
			<div id="gbt_CN<c:out value="${ticket.caseNumber}"/>"
				class="db_gridbox_celltitles"><c:out
				value="${ticket.caseNumber}" /></div>
			</div>
			<sec:authorize access="!hasRole('ROLE_TICKET_MANAGEMENT')">
				<div id="gb_sub<c:out value="${ticket.caseNumber}"/>"
					class="db_gridbox_columns" style="width: 40%;">
				<div id="gbt_sub<c:out value="${ticket.caseNumber}"/>"
					class="db_gridbox_celltitles"><c:out
					value="${ticket.subject}" /></div>
				</div>
			</sec:authorize> <sec:authorize access="hasRole('ROLE_TICKET_MANAGEMENT')">
				<div id="gb_sub<c:out value="${ticket.caseNumber}"/>"
					class="db_gridbox_columns" style="width: 30%;">
				<div id="gbt_sub<c:out value="${ticket.caseNumber}"/>"
					class="db_gridbox_celltitles"><c:out
					value="${ticket.subject}" /></div>
				</div>
				<div id="gb_cust<c:out value="${ticket.caseNumber}"/>"
					class="db_gridbox_columns" style="width: 10%;">
				<div id="gbt_cust<c:out value="${ticket.caseNumber}"/>"
					class="db_gridbox_celltitles"><c:out
					value="${ticket.owner.tenant.name}" /> (<c:out
					value="${ticket.owner.tenant.accountId}" />)</div>
				</div>
			</sec:authorize>
			<div id="gbt_un<c:out value="${ticket.caseNumber}"/>"
				class="db_gridbox_celltitles"><c:out
				value="${ticket.owner.name}" /></div>
			</div>
			<div id="gb_crt_at<c:out value="${ticket.caseNumber}"/>"
				class="db_gridbox_columns" style="width: 12%;">
			<div id="gbt_crt_at<c:out value="${ticket.caseNumber}"/>"
				class="db_gridbox_celltitles"><spring:message
				code="date.format" var="date_format" /> <fmt:formatDate
				value="${ticket.createdAt}" pattern="${date_format}"
				timeZone="${currentUser.timeZone}" /></div>
			</div>
			<div id="gb_status<c:out value="${ticket.caseNumber}"/>"
				class="db_gridbox_columns" style="width: 8%;">
			<div id="gbt_status<c:out value="${ticket.caseNumber}"/>"
				class="db_gridbox_celltitles"><spring:message
				code="ui.label.support.tickets.status.${ticket.status.name}" /></div>
			</div>
			<div id="gb_last<c:out value="${ticket.caseNumber}"/>"
				class="db_gridbox_columns" style="width: 15%;">
			<div id="gbt_last<c:out value="${ticket.caseNumber}"/>"
				class="db_gridbox_celltitles"><spring:message
				code="date.format" var="date_format" /> <fmt:formatDate
				value="${ticket.updatedAt}" pattern="${date_format}"
				timeZone="${currentUser.timeZone}" /></div>
			</div>
			</div>
		</c:forEach>
<c:if test="${(! empty tickets && tickets != null) || !isFirst}">
<div class="db_gridbox_row_show_more" onclick="getMoreTickets()" id="showMore">
	<input type="hidden" id="queryLocator" value="<c:out value="${queryLocator}"/>"/>
	<div style="margin-left: 400px;">
	<c:choose>
		<c:when test = "${! empty queryLocator}">
			<spring:message code="label.show.more"/>
			<div id="showMoreLoading" class="small_loader" style="display: none;"></div>
		</c:when>
		<c:otherwise>
			<spring:message code="ui.label.support.tickets.no.more.tickets"/>
		</c:otherwise>
	</c:choose>
	</div>
</div>
</c:if>
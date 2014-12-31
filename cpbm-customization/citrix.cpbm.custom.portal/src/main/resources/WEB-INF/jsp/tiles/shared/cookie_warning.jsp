<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<spring:url var="actionURL" value="/portal/acceptCookies"></spring:url>

<div class="dialog_formcontent wizard">
	<div class="j_actionForm">
		<div class="widgetwizard_contentarea sixstepswizard"
			style="height: auto;">
			<div class="widgetwizard_boxes fullheight sixstepswizard"
				style="height: auto;">
				<div class="widgetwizard_titleboxes">
					<h2>
						<spring:message code="ui.label.header.cookie.law" />
					</h2>
					<span style="width: 100%">
						<spring:message code="content.cookie.law.warning" />
					</span>
				</div>
				<div class="widgetwizard_reviewbox sixstepswizard ">
					<span id="accept_cookies_errormsg"></span>
				</div>
			</div>
		</div>
		<div id="buttons" class="widgetwizard_nextprevpanel sixstepswizard">
			<input type="hidden" id="userParam"
				value="<c:out value="${currentUser.param}"/>" /> 
			<input type="button" id="accept_cookies"
				name="<spring:message code="ui.label.cookies.accept"/>"
				value="<spring:message code="ui.label.cookies.accept"/>"
				class="widgetwizard_nextprevpanel submitbutton"/> 
			<input type="button" id="reject_cookies"
				name="<spring:message code="ui.label.cookies.reject"/>"
				value="<spring:message code="ui.label.cookies.reject"/>"
				class="widgetwizard_nextprevpanel submitbutton" />
		</div>
	</div>
</div>

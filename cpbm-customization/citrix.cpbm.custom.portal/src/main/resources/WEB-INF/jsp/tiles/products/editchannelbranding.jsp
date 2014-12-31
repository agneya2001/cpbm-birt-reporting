<!-- Copyright 2014 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>

<div class="uploadfileform">
	<span class="helpertext"> </span>
	<spring:url value="/portal/channels/editchannelbranding"
		var="edit_channel_branding" htmlEscape="false" />
	<form:form commandName="channelBrandingForm" cssClass="formPanel"
		action="${edit_channel_branding}" enctype="multipart/form-data">
		<div class="uploader_box">
			<div class="upload_file_box">
				<form:label path="css" class="label">
					<spring:message code="label.channel.select.css.file" />:</form:label>
				<form:input type="file" id="css" class="cssrequired filerequired"
					path="css" />
				<div class="main_addnew_formbox_errormsg" id="cssError"></div>
			</div>
			<div class="upload_file_box">
				<form:label path="logo" class="label">
					<spring:message code="label.channel.select.logo.file" />:</form:label>
				<form:input type="file" id="logo" class="logorequired filerequired"
					path="logo" />
				<div class="main_addnew_formbox_errormsg" id="logoError"></div>
			</div>
			<div class="upload_file_box">
				<form:label path="favicon" class="label">
					<spring:message code="label.channel.select.favicon.file" />:</form:label>
				<form:input type="file" id="favicon"
					class="faviconrequired filerequired" path="favicon" />
				<div class="main_addnew_formbox_errormsg" id="faviconError"></div>
			</div>
			<form:input type="hidden" id="channelId" path="channelId"
				value="${channelBrandingForm.channel.id}" />
			<form:input type="hidden" path="channel.code" />
			<form:input type="hidden" path="publish" />

			<div class="main_addnew_formbox_errormsg" id="errormessageError"></div>
		</div>
	</form:form>

	<input type="hidden" id="currentLoggedInUserParam" value="${currentLoggedInUserParam}" />
</div>



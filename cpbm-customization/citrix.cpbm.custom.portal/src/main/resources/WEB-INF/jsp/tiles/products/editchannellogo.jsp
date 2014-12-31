<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<div class="uploadfileform">
 <span class="helpertext">
   <spring:message code="ui.text.message.channel.image.message"></spring:message>
 </span>

	   <spring:url value="/portal/channels/editlogo" var="edit_channel_logo_path" htmlEscape="false" /> 
	   <form:form commandName="channelLogoForm"  cssClass="formPanel"  action="${edit_channel_logo_path}"  enctype="multipart/form-data" >
	        <div class="uploader_box">
	            <form:label path="logo" style="float:left;line-height:30px;"><spring:message code="label.custom.select.channel.image"/></form:label>
	            <form:input type="file" id="logo" class="logorequired filerequired" path="logo"/>
	            <div class="main_addnew_formbox_errormsg" id="logoError"></div>
	         </div>
	    </form:form>

</div>

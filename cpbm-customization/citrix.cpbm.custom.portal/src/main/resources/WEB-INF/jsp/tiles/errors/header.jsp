<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="${pageContext.request.locale}" scope="request"/>
<c:set var="home_path" value="/home"/>
<div class="loginheader">
	<div class="loginheader_left">
    	<div class="loginheader_logo">    	
    	<a title="CloudPortal Home" href="http://www.cloud.com" >
			<img alt="logo" src="/portal/images/logo.png" class="mainlogo-img">
		</a>
    	</div>
    </div>
    <div class="loginheader_right">
    	<div class="loginheader_righttop">
    	<div style="float: left;">
    	<p>    	
    	 <strong>1-877-349-7564</strong></p></div>
    	 <div class="loginheader_banner"></div>
    	 </div>
    </div>
</div>

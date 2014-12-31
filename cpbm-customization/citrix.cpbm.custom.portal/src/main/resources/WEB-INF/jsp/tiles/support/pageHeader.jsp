<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/jquery/jquery.treeview.css" type="text/css" />
<script type="text/javascript" src="<%=request.getContextPath() %>/js/developerapi.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/jquery.treeview.min.js"></script>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<div class="main_titlebox" style="margin:0;">
   	<h1><spring:message code="page.level1.support"/></h1>
</div>
<div class="submenu_panel">
	<div class="submenu_tabwrapper">
		<div class="submenu_tabbox <c:out value="${Blog}"/>">	
			<a target="_blank" href="#" /><spring:message code="page.level2.blogs"/></a>
		</div>
	    <div class="submenu_pointerbox"><div class="submenu_pointer <c:out value="${Blogs}"/>"></div></div>
	</div>
	 <!--<div class="submenu_tabwrapper">
		<div class="submenu_tabbox <c:out value="${Forum}"/>">	
			<a target="_blank" href="#" /><spring:message code="page.level2.forums"/></a>
		</div>
	    <div class="submenu_pointerbox"><div class="submenu_pointer <c:out value="${Forums}"/>"></div></div>
	</div> -->
	<div class="submenu_tabwrapper">
		<div class="submenu_tabbox <c:out value="${Wiki}"/>">	
			<a target="_blank" href="#" /><spring:message code="page.level2.wikis"/></a>
		</div>
	    <div class="submenu_pointerbox"><div class="submenu_pointer <c:out value="${Wikis}"/>"></div></div>
	</div>
	<div class="submenu_tabwrapper">
		<div class="submenu_tabbox <c:out value="${FAQ}"/>">	
			<a target="_blank" href="#" /><spring:message code="page.level2.faqs"/></a>
		</div>
	    <div class="submenu_pointerbox"><div class="submenu_pointer <c:out value="${FAQs}"/>"></div></div>
	</div>
	<div class="submenu_tabwrapper">
		<div class="submenu_tabbox <c:out value="${Health}"/>">	
			<a href="<%=request.getContextPath() %>/portal/health" /><spring:message code="page.level2.health"/></a>
		</div>
	    <div class="submenu_pointerbox"><div class="submenu_pointer <c:out value="${Health}"/>"></div></div>
	</div>

</div>


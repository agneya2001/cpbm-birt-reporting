<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <title>
      <spring:message code="company.name"/> -      
      <spring:message code="webapp.tagline"/>      
    </title>
    <link href="/portal/favicon.ico" type="image/x-icon" rel="shortcut icon" />
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript" src="<%=request.getContextPath() %>/resources/all.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath() %>/resources/app.js"></script>
 	<link rel="stylesheet" type="text/css" media="all" href="<%= request.getContextPath() %>/resources/all.css"/>
 	<link rel="stylesheet" type="text/css" media="all" href="<%=request.getContextPath()%>/css/main.css"/>
   <script type="text/javascript" src="<%=request.getContextPath() %>/js/catalog.js"></script> 
  </head>
  <body>
		<div id="main_master" class="main_master">
			<div class="header" id="header">
				<tiles:insertAttribute name="header" />
			</div>					
			<div class="main_contentmaster" id="main_contentmaster">
			<div class="clearboth"></div>				
			<tiles:insertAttribute name="body" />			
			</div>
			<div class="clearboth"></div>
			<div class="catalog_footer" id="catalog_footer">
				<tiles:insertAttribute name="footer" />
			</div>
		</div>
	</body>
</html>



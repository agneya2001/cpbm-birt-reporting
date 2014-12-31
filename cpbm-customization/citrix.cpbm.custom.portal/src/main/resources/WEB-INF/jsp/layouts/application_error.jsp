<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>

<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <title></title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/>
	  <script type="text/javascript" src="<%=request.getContextPath() %>/resources/all.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath() %>/csrf_js_servlet"></script> 
	  <script type="text/javascript" src="<%=request.getContextPath() %>/resources/app.js"></script>    
	  <link rel="stylesheet" type="text/css" media="all" href="<%= request.getContextPath() %>/resources/all.css"/>
	  <link rel="stylesheet" type="text/css" media="all" href="<%=request.getContextPath()%>/css/main.css"/>
    <link rel="stylesheet" type="text/css" media="all" href="<%= request.getContextPath() %>/<spring:theme code="css" />"/>
		<tiles:insertAttribute name="customHeader" ignore="true"/>
  </head>
  <body>
    <div id="login_master">
    
      <tiles:insertAttribute name="header"/>
			<div class="loginbox">
			 
			  <div class="loginbox_mid" style="margin:0 0 0 150px;">
          <tiles:insertAttribute name="body"/>
     </div>
			 
			</div>
			<div class="clear_both"></div>
    </div>
  </body>  
  
</html>



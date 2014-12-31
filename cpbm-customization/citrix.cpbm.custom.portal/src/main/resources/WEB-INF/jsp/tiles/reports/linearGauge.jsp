<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>  

<!--START Code Block for Linear Gauge  -->
<c:if test="${empty chartData['gaugeCurr']}">
<div style="margin: 30px 0px 25px 250px;color:#666666">
	No data to display
</div>
</c:if>
<c:if test="${!empty chartData['gaugeCurr']}">
	<c:set var ="gaugaDataXML" value="${chartData['gaugeCurr']}"/>
	<object classid="clsid:d27cdb6e-ae6d-11cf-96b8-444553540000" codebase="http://fpdownload.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=8,0,0,0" id="Gauge" height="90" width="580">
		<param name="movie" value="/portal/fusion/charts/HLinearGauge.swf">
		<param name="FlashVars" value="&dataXML=<c:out value="${gaugaDataXML}"/>">
		<param name="quality" value="high">
		<embed src="/portal/fusion/charts/HLinearGauge.swf" flashvars="&dataXML=<c:out value="${gaugaDataXML}"/>" quality="high" name="LinearGauge" type="application/x-shockwave-flash" pluginspage="http://www.macromedia.com/go/getflashplayer" height="90" width="580">
	</object> 
</c:if>	
<!--END Code Block for Linear Gauge  -->
<div style="margin:10px 0 0 0px">
	<div class="linear_gauge_legends">
		<img src="/portal/images/portal/gauge_black.png"> <spring:message code="ui.label.report.lineargauge.budget"/>
	</div>
	<div class="linear_gauge_legends">
		<img src="/portal/images/portal/gauge_orange.png"> <spring:message code="ui.label.report.lineargauge.actual"/>
	</div>
	<div class="linear_gauge_legends">
		<img src="/portal/images/portal/gauge_blue.png"> <spring:message code="ui.label.report.lineargauge.lastmonth"/>
	</div>
</div>			
<div class="clearboth"></div>			

<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>  

<jsp:include page="js_messages.jsp"></jsp:include>

<c:set var="chartId" value="${report.attributes['chartId']}"  />
<c:set var="chartContainerId" value="${report.attributes['chartId']}Container"  />
<c:set var="chartTitle" value="${report.title}"  />
<c:set var="xAxisTitle" value="${report.attributes['xAxisTitle']}"  />
<c:set var="yAxisTitle" value="${report.attributes['yAxisTitle']}"  />
<c:set var="chartType" value="${report.attributes['fusionChartType']}"  />
<c:set var="numberPrefix" value="${report.params['defaultCurrency']}"  />

<div id="<c:out value="${chartContainerId}"/>" > 
</div>
<script type="text/javascript">                     

$(document).ready(function(){
  var chartType='<c:out value="${chartType}" />';
  var chartId='<c:out value="${chartId}" />';
  var containerId = '<c:out value="${chartContainerId}" />';
  var chartData = '<c:out value="${report.data}" escapeXml="false"/>';
  var chartDataJSON = $.parseJSON(chartData);
  var numberPrefix = '<c:out value="${numberPrefix}" escapeXml="false"/>';
  numberPrefix = numberPrefix || "";
  var rotateYAxisLabels='<c:out value="${rotateYAxisLabels}" />' || '<c:out value="${report.attributes['rotateYAxisLabels']}"/>';
  var rotateXAxisLabels='<c:out value="${rotateXAxisLabels}" />';
  var formatNumber='<c:out value="${formatNumber}" />' || '<c:out value="${report.attributes['formatNumber']}"/>';
  
  var options = {"caption" : '<c:out value="${chartTitle}" />' ,
      "xAxisName" : '<c:out value="${xAxisTitle}" />',
      "yAxisName" : '<c:out value="${yAxisTitle}" />',
      "showlabels": "1",
      "showvalues": "1",
      "decimals": "2",
      "showBorder":"0",
      "showPlotBorder":'0',
      "canvasBorderThickness":"1",
      "canvasBorderAlpha":"30",
      "canvasbgAlpha":"0",
      "labelDisplay":"AUTO",
      "adjustDiv":"0",
      "numDivLines":"0",
      "legendPosition":"RIGHT",
      "numberprefix": numberPrefix,
      "basefont":"Arial",
      "outCnvBaseFontColor":"808080",
      "bgColor":"F6F7F8, FFF" 
      
      
      
  };
  if(chartType=='Column2D'){
    options["plotgradientcolor"]="333333";
    createColumnChart2D(containerId, "440", "275", chartId, options, chartDataJSON);
  } else if(chartType=='MSColumn2D'){
    options["plotgradientcolor"]="";
    options["plotFillRatio"]="60,40";
    createMultiSeriesColumnChart2D(containerId, "440", "275", chartId, options, chartDataJSON.categories, chartDataJSON.series);
  }
  
});

</script>
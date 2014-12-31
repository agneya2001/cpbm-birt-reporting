<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>  

<jsp:include page="js_messages.jsp"></jsp:include>

<c:set var="chartId" value="${report.attributes['chartId']}Container"  />
<c:set var="chartTitle" value="${report.title}"  />
<c:set var="xAxisTitle" value="${report.attributes['xAxisTitle']}"  />
<c:set var="yAxisTitle" value="${report.attributes['yAxisTitle']}"  />
<c:set var="chartWidth" value="${report.attributes['width']}"  />
<c:set var="chartHeight" value="${report.attributes['height']}"  />
<c:set var="numberPrefix" value="${report.params['defaultCurrency']}"  />
<c:set var="inverted" value="${report.attributes['inverted']}"  />
<c:set var="useXLabelformater" value="${report.attributes['useXLabelformater']}"  />
<c:set var="stacking" value="${report.attributes['stacking']}"  />
<c:set var="allowDecimals" value="${report.attributes['allowDecimals']}"  />

<div id="<c:out value="${chartId}"/>" style="height: <c:out value="${chartHeight}"/>px;width:<c:out value="${chartWidth}" />px;"> 
  <spring:message code="ui.label.report.charts.message"/>
</div>
<script type="text/javascript">                     

$(document).ready(function(){

  var containerId = '<c:out value="${chartId}" />';
  var chartData = '<c:out value="${report.data}" escapeXml="false"/>';
  var chartDataJSON = $.parseJSON(chartData);
  var numberPrefix = '<c:out value="${numberPrefix}" escapeXml="false"/>';
  numberPrefix = numberPrefix || null;
  var rotateYAxisLabels='<c:out value="${rotateYAxisLabels}" />' || '<c:out value="${report.attributes['rotateYAxisLabels']}"/>';
  var rotateXAxisLabels='<c:out value="${rotateXAxisLabels}" />';
  var formatNumber='<c:out value="${formatNumber}" />' || '<c:out value="${report.attributes['formatNumber']}"/>';
  
  var options = {
    "chartTitle" : '<c:out value="${chartTitle}" />',
    "xAxisTitle" : '<c:out value="${xAxisTitle}" />',
    "yAxisTitle" : '<c:out value="${yAxisTitle}" />',
    "rotateYAxisLabels": rotateYAxisLabels ,
    "rotateXAxisLabels":rotateXAxisLabels,
    "formatNumber":formatNumber,
    "decPoint": g_dictionary.decPoint,
    "thousandsSep": g_dictionary.thousandsSep,
    "type" : "column",
    "numberPrefix" : numberPrefix,
    "allowDecimals" :  '<c:out value="${allowDecimals}" />',
	"inverted" :  '<c:out value="${inverted}" />',
	"useXLabelformater" :  '<c:out value="${useXLabelformater}" />',
	"precision" : '<c:out value="${report.attributes['precision']}"/>',
	"stacking" :  '<c:out value="${stacking}" />',
    "lang": {
      printButtonTitle: g_dictionary.highChartPrint,
      exportButtonTitle:g_dictionary.highChartExport,
      downloadPNG:g_dictionary.highChartDownloadPNG,
      downloadJPEG:g_dictionary.highChartDownloadJPEG,
      downloadPDF:g_dictionary.highChartDownloadPDF,
      downloadSVG:g_dictionary.highChartDownloadSVG,
      noData: g_dictionary.noDataToDisplay
    }
  };
  HighChartsUtil.renderChart(containerId, chartDataJSON.series, chartDataJSON.categories, options);
});

</script>
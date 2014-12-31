<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>  

<script type="text/javascript">$(document).ready(function(){

  var currencySymbol = "<c:out value="${effectiveTenant.currency.sign}" />";  
  
  var chartData = '<c:out value="${chartData}" escapeXml="false"/>';
  var spendvstype = $.parseJSON(chartData).spendvstype;
  var chartTitle = '<spring:message javaScriptEscape="true" code="report.label.spendDetails"/>';
  var yAxisTitle = '<spring:message javaScriptEscape="true" code="report.label.usage"/>';
  var rotateXAxisLabels=true;
  
	var options = {
    "numberPrefix" : currencySymbol,
    "chartTitle" : chartTitle,
    "yAxisTitle" : yAxisTitle,
    "rotateXAxisLabels":rotateXAxisLabels,
    "inverted" : false,
    "decPoint": g_dictionary.decPoint,
    "thousandsSep": g_dictionary.thousandsSep,
		"type" : "column",
		"min" : spendvstype.min,
    "max" : spendvstype.max,
    "lang": {
      printButtonTitle: g_dictionary.highChartPrint,
      exportButtonTitle:g_dictionary.highChartExport,
      downloadPNG:g_dictionary.highChartDownloadPNG,
      downloadJPEG:g_dictionary.highChartDownloadJPEG,
      downloadPDF:g_dictionary.highChartDownloadPDF,
      downloadSVG:g_dictionary.highChartDownloadSVG
    }
  };
	
  HighChartsUtil.renderChart('spendvstypeChart', spendvstype.series, spendvstype.categories, options);
});
</script>

<div id="spendvstypeChart" style="height: 245px;width:435px;"></div>

								

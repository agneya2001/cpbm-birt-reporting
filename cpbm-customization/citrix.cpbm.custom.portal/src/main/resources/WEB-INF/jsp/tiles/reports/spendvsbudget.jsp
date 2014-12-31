<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>  
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


<c:set var="chartId" value="spend_vs_budget"  />
<script type="text/javascript">                          
$(document).ready(function(){

  var currencySymbol = "<c:out value="${effectiveTenant.currency.sign}" />"; 
  var max_decimal_digits="<c:out value="${currencyFractionalDigitsLimit}" />";
  var chartId='<c:out value="${chartId}" />';
	var chartData = '<c:out value="${chartData}" escapeXml="false"/>';
	var formatted_budget_value='<fmt:formatNumber pattern="${currencyFormat}" maxFractionDigits="0" minFractionDigits="0" value="${spend_vs_budget_chart_data_obj.spendvsbudget.spendLimit}" />';

	if(typeof chartData != "undefined" && chartData != "" && chartData != null){
	
	var spendvsbudget = $.parseJSON(chartData).spendvsbudget;	
	var budget = spendvsbudget.spendLimit;
	var initialBudgetValueDisplay=parseFloat(budget);
	var currTotal = spendvsbudget.currTotal;
    var prevTotal = spendvsbudget.prevTotal;
    
    if(budget==0){
    	if(currTotal > 0 && currTotal > prevTotal){
    	budget=currTotal*1.5;
    	} else if(prevTotal >0){
    		budget=prevTotal*1.5;
    	} 
    }
    
    var divider1=(parseFloat(budget)*parseFloat(spendvsbudget.firstThreshold)/100);
    var divider2=(parseFloat(budget)*parseFloat(spendvsbudget.secondThreshold)/100);
    
    
    var spend_vs_budget_chart_dictionary = {
    		lastmonth: '<spring:message javaScriptEscape="true" code="ui.label.report.lineargauge.lastmonth"/>',
    		actual: '<spring:message javaScriptEscape="true" code="ui.label.report.lineargauge.actual"/>',
    		budgetNotSet:'<spring:message javaScriptEscape="true" code="label.budget.not.set"/>',
    		budgetLabel:'<spring:message javaScriptEscape="true" code="ui.label.report.lineargauge.budget"/>'
    		};
    
   var minValue=0;
   if(currTotal < 0 && currTotal < prevTotal){
	   minValue=currTotal;
   } else if(prevTotal < 0){
	   minValue=prevTotal;
	   
   }
	var chartData= 

	{
	    "chart": {
	        "bgcolor": "FFFFFF",
	        "bgalpha": "0",
	        "showborder": "0",
	        "upperlimit": budget,
	        "lowerlimit": "0",
	        "gaugeroundradius": "5",
	        "chartbottommargin": "20",
	        "chartTopMargin":"20",
	        "chartLeftMargin":"35",
	        "chartRightMargin":"35",
	        "showLimits":"0",
	        "showgaugelabels": "1",
	        "valueabovepointer": "1",
	        "showTickMarks":"0",
	        "showTickValues":"0",
	        "pointerradius": "7",
	        "pointerOnTop":'0',
	        "numberprefix": currencySymbol,
	        "animation":"1",
	        "autoScale":"1",
	        "manageResize":"1",
	        "decimals":max_decimal_digits
	        
	        
	    },
	    "colorrange": {
	        "color": [
	            {
	                "minvalue": minValue,
	                "maxvalue": divider1
	                
	            },
	            {
	                "minvalue": divider1,
	                "maxvalue": divider2
	                
	            },
	            {
	                "minvalue": divider2,
	                "maxvalue": budget
	                
	            }
	        ]
	    },
	    "pointers": {
	        "pointer": [
	            {
	                "value":currTotal,
	                "bgColor":"FF6600",
	                "borderColor":"FF6600",
	                "toolText":spend_vs_budget_chart_dictionary.actual
	            }
	        ]
	    },
	    "trendpoints": {
	        "point": [
	          {
	            "startvalue": prevTotal,
	           "dashed":"1",
	            "color": "3366FF",
	            "borderColor":"3366FF",
	            "thickness": "1",
	            "useMarker":"1",
	            "alpha": "100",
	            "showOnTop":"0",
	            "markerTooltext":spend_vs_budget_chart_dictionary.lastmonth
	          }
	          
	        ]
	      }
	    ,
	    "styles": {
	        "definition": [
	            {
	                "name": "ValueFont",
	                "type": "Font",
	                "bgcolor": "333333",
	                "size": "10",
	                "color": "000000"
	            }
	        ],
	        "application": [
	            {
	                "toobject": "VALUE",
	                "styles": "valueFont"
	            }
	        ]
	    }
	};
	
	
	
	if(initialBudgetValueDisplay==0 && budget==0){
		chartData.chart['upperLimitDisplay']=spend_vs_budget_chart_dictionary.budgetNotSet;
	}else if(initialBudgetValueDisplay!=0){
		
		$("#budget_value").text("("+currencySymbol+formatted_budget_value+")");
	}
	var budgetMarker={
            "startvalue": budget,
            "dashed":"1",
             "color": "666666",
             "borderColor":"666666",
             "thickness": "1",
             "useMarker":"1",
             "alpha": "100",
             "markerTooltext":spend_vs_budget_chart_dictionary.budgetLabel
             
           };
	
		chartData.trendpoints.point.push(budgetMarker);
	
	
   $("#budget").text(currencySymbol+budget);
	createLinearGuageChart('spendvsbudgetChart', "430", "110", chartId,  chartData);
	}
  
});
</script>

<div id="spendvsbudgetChart" style="height:115px;width:435px;float:left;margin-left:5px;"></div>
<div class="stats_legendarea">
    <ul>
        <li><span class="legend actual"></span><span class="label"><spring:message code="ui.label.report.lineargauge.actual"/></span></li>
        <li><span class="legend last_month"></span><span class="label"><spring:message code="ui.label.report.lineargauge.lastmonth"/></span></li>
    </ul> 
</div>

/**
 * HighCharts util
 * 
 * @dependency jquery api - http://docs.jquery.com/Main_Page
 * @dependency highcharts api - http://docs.jquery.com/Main_Page
 *
 * @author Rajani
 */

var HighChartsUtil;
if (typeof(HighChartsUtil) === 'undefined') {
  HighChartsUtil = {};
}

/**
 * Function to convert html code to actual symbol (uses jquery)
 * 
 * @param value: value to decode Eg: "&gt;"
 * 
 * @return decoded value Eg: ">"
 */
HighChartsUtil.htmlDecode = function(value) {
  return $('<div/>').html(value).text();
};

/**
 * Function to get html code of a symbol (uses jquery)
 * 
 * @param value: value to encode Eg: ">"
 * 
 * @return encoded value Eg: "&gt;"
 * 
 */
HighChartsUtil.htmlEncode = function(value) {
  return $('<div/>').text(value).html();
};

/**
 * Function to render high chart to a given container
 *
 * @param containerId : id of the container to which the chart is rendered Eg: "container"
 * @param series : array of data Eg: [{"data":[27.5, 0, 2], "name": "series name"}]
 * @param categories : categories to be displayed (series.data and categories should exactly match) Eg:['Budget', 'This Billing Period', 'Previous Billing Period']
 * @param options (optional): dictionary of optional chart options. allowed options are 
 *                            xAxisTitle - default null (actual text on the x-axis title)
 *                            yAxisTitle - default null (actual text on the y-axis title)
 *                            chartTitle - default null (actual text on the chart title)
 *                            inverted - default false ( whether to invert the axes so that x-axis is horizontal and y-axis is vertical)
 *                            type - default "bar" (type of the chart. Can be one of line, spline, area, areaspline, column, bar, pie and scatter)
 *                            min - default null (the minimum value of the chart)
 *                            max - default null (the maximum value of the chart)
 *                            numberPrefix - default "" (number prefix to be displayed on axis) 
 *                            alternateGridColor - default null (alternate grid color of the chart) 
 *                            backgroundColor - default "#FFFFFF" (background color of the chart)
 *
 * @return A reference to the created Chart object.
 */

HighChartsUtil.renderChart = function(containerId, series, categories, options){
	options = options || {};
	
	var yAxisTitle = options["yAxisTitle"] || null;
	var xAxisTitle = options["xAxisTitle"] || null;
	var chartTitle = HighChartsUtil.htmlDecode(options["chartTitle"]) || null;
	var inverted = options["inverted"] || false;
	var formatNumber = options["formatNumber"] || false;
	var decPoint = options["decPoint"] || '.';
	var thousandsSep = options["thousandsSep"] || ',';
	var rotateYAxisLabels=options["rotateYAxisLabels"] || false;
	var rotateXAxisLabels=options["rotateXAxisLabels"] || false;
	var chars_before_ellipsis=options["chars_before_ellipsis"] || "25";
	chars_before_ellipsis=parseInt(chars_before_ellipsis);
	var useXLabelformater = options["useXLabelformater"] || false;
	var stacking = options["stacking"] || null;
	var allowDecimals = (options["allowDecimals"]==="true")?true : false || false;
	var type = options["type"] || "bar";
	var yAxisMin = options["min"] || null;
	var yAxisMax = options["max"] || null;
	var numberPrefix = options["numberPrefix"] || "";
	var xAxisAlternateGridColor = options["xAxisAlternateGridColor"] || null;
	var yAxisAlternateGridColor = options["yAxisAlternateGridColor"] || null;
	var backgroundColor = options["backgroundColor"] || "#FFFFFF";
	var precision = options["precision"] || '2';
	var toolTipFormatter = options['toolTipFormatter'] || function(){  
                                                          var name = showLegend ? this.series.name + ', ' : '';
                                                          name = name + this.x + ', ' + numberPrefix + ' ' + this.y;
                                                          return name;     };
  var lang = options["lang"] || {};

  var yAxisStackLabelFormatter=options['yAxisStackLabelFormatter'] || function(){
                                                                        return Highcharts.numberFormat(this.total, precision, decPoint, thousandsSep);
                                                                       };

  var toolTipPositioner = options['toolTipPositioner'] || null;
  var chartSpacingRight=options['chartSpacingRight'] || 30;
  var chartSpacingLeft=options['chartSpacingLeft'] || 10;
   var toolTipStyle=options['toolTipStyle'] || {};
   var useHTML=options['useHTML'] || false;
   var exportEnabled=true;
   if(typeof options['exportEnabled']=="boolean")
     exportEnabled=options['exportEnabled'];
	numberPrefix = HighChartsUtil.htmlDecode(numberPrefix);
	var showLegend = series.length > 1 || false;
	$.each(series, function(index, series){
		var data = series["data"];
    var length = data.length;
		for(var i=0; i<length; i++) {
			if (!isNaN(data[i])) {
				data[i] = parseFloat(data[i]);
			}
		}
	});
  
  var legendItemClick = options['legendItemClick'] || function(){};
  var itemStyle = options['itemStyle'] || {};
  
	var chartOptions = {
    chart: {
      renderTo: containerId,
      inverted: inverted,
      type: type,
      plotBorderWidth: 1,
      spacingRight: chartSpacingRight,
      spacingLeft:chartSpacingLeft,
      backgroundColor: backgroundColor
    },
    legend: {
      enabled: showLegend,
      itemStyle: itemStyle
    },
    xAxis: {
      categories: categories,
      alternateGridColor: xAxisAlternateGridColor,
			title: {
        enabled: true,
        text: xAxisTitle,
        style: {
          fontWeight: 'bold',
          color: '#7E7A7A'
        }
      },
      labels: {
          formatter: function(){
    	  var text = this.value;
    		  if(useXLabelformater && text.length > 9){
    			  return text.substring(0 , 6) + "...";
    		  }else{
    			  return text;
    		  }
             
          }
          
        }
    },
    yAxis: {
      min: yAxisMin,
      max: yAxisMax,
      gridLineWidth: 0,
      alternateGridColor: yAxisAlternateGridColor,
      tickWidth:1,
      allowDecimals: allowDecimals,
      labels: {
        formatter: function(){
          var decimals = 0;
          if(!formatNumber){
            var texts = this.value.toString().split(".");
            if (texts.length == 2)
              decimals = texts[1].length;
          }
          return numberPrefix + Highcharts.numberFormat(this.value, decimals, decPoint, thousandsSep);
        }
      },
      stackLabels: {
        enabled: true,
        formatter: yAxisStackLabelFormatter
        
      },
      title: {
        enabled: true,
        text: yAxisTitle,
        style: {
          fontWeight: 'bold',
          color: '#7E7A7A'
        }
      }
    },
    plotOptions: {
      bar: {
        stacking: 'normal',
        events: {
          legendItemClick: legendItemClick
        }
      },
      column: {
        stacking: stacking,
        events: {
          legendItemClick: legendItemClick
        }
      }
    },
    title: {
      enabled: true,
      text: chartTitle,
      style: {
        fontWeight: 'bold',
        color: '#7E7A7A',
        fontSize: '12px'
      }
    },
    exporting: {
      enabled:exportEnabled,
      filename: "PortalChart"
    },
    tooltip: {
      enabled: true,
      formatter: toolTipFormatter,
      style:toolTipStyle,
      positioner: toolTipPositioner,
      useHTML:useHTML
    },
    series: series,
    credits: {
      enabled: false
    },
    lang:lang
  };
	if(rotateXAxisLabels){
	  chartOptions.xAxis.labels["rotation"]=-45;
	  chartOptions.xAxis.labels["align"]="right";
	  chartOptions.xAxis.labels["x"]=0;
	  chartOptions.xAxis.labels["y"]=20;
	  chartOptions.xAxis.labels["formatter"]=function(){
      var text = this.value;
      if(text.length > chars_before_ellipsis){
        return text.substring(0 , (chars_before_ellipsis-3)) + "...";
      }else{
        return text;
      }
         
      };
	}
	
	 if(rotateYAxisLabels){
	   chartOptions.yAxis.labels["rotation"]=-45;
	    chartOptions.yAxis.labels["align"]="right";
	    chartOptions.yAxis.labels["x"]=0;
	    chartOptions.yAxis.labels["y"]=20;
	  } 
	
	return new Highcharts.Chart(chartOptions);
};

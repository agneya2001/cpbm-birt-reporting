/*
*  Copyright © 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/

function createPieChartDoughnut2D(chart_container_id, width, height, chart_id, data) {
  if (FusionCharts(chart_id)) {
    FusionCharts(chart_id).dispose();
  }
  $("#" + chart_container_id).insertFusionCharts({
    swfUrl: "../../fusion/charts/Doughnut2D.swf",
    renderer: 'JavaScript',
    width: width,
    height: height,
    id: chart_id,

    dataFormat: "json",
    dataSource: {
      "chart": {
        "bgcolor": "FFFFFF,CCCCCC",
        "showpercentagevalues": "1",
        "plotbordercolor": "FFFFFF",
        "numberprefix": "$",
        "issmartlineslanted": "0",
        "showvalues": "0",
        "showlabels": "0",
        "showlegend": "0",
        "pieRadius": '40'
      },
      "data": data
    }
  });
}


function createColumnChart2D(chart_container_id, width, height, chart_id, options, data) {
  if (FusionCharts(chart_id)) {
    FusionCharts(chart_id).dispose();
  }
  $("#" + chart_container_id).insertFusionCharts({
    swfUrl: "../../fusion/charts/Column2D.swf",
    renderer: 'JavaScript',
    width: width,
    height: height,
    id: chart_id,

    dataFormat: "json",
    dataSource: {
      "chart": options,

      "data": data,
      "styles": {
        "definition": [{
          "name": "myCaptionFont",
          "type": "font",
          "font": "Arial",
          "size": "12",
          "color": "808080",
          "bold": "0",
          "underline": "0"
        }],
        "application": [{
          "toobject": "Caption",
          "styles": "myCaptionFont"
        }]
      }
    }
  });
}





function createMultiSeriesColumnChart2D(chart_container_id, width, height, chart_id, options, categories, data) {
  if (FusionCharts(chart_id)) {
    FusionCharts(chart_id).dispose();
  }
  $("#" + chart_container_id).insertFusionCharts({
    swfUrl: "../../fusion/charts/MSColumn2D.swf",
    renderer: 'JavaScript',
    width: width,
    height: height,
    id: chart_id,

    dataFormat: "json",
    dataSource: {
      "chart": options,

      "categories": [{
        "category": categories
      }],
      "dataset": data,
      "styles": {
        "definition": [{
          "name": "myCaptionFont",
          "type": "font",
          "font": "Arial",
          "size": "12",
          "color": "808080",
          "bold": "0",
          "underline": "0"
        }],
        "application": [{
          "toobject": "Caption",
          "styles": "myCaptionFont"
        }]
      }
    }
  });
}

function createPieChart2D(chart_container_id, width, height, chart_id, data, options) {
  if (FusionCharts(chart_id)) {
    FusionCharts(chart_id).dispose();
  }

  $("#" + chart_container_id).insertFusionCharts({
    swfUrl: "../../fusion/charts/Pie2D.swf",
    renderer: 'JavaScript',
    width: width,
    height: height,
    id: chart_id,

    dataFormat: "json",
    dataSource: {
      "chart": options,

      "data": data

    }
  });
}


function createGuageChart(chart_container_id, width, height, chart_id, data) {
  if (FusionCharts(chart_id)) {
    FusionCharts(chart_id).dispose();
  }

  $("#" + chart_container_id).insertFusionCharts({
    swfUrl: "../../fusion/charts/AngularGauge.swf",
    renderer: 'JavaScript',
    width: width,
    height: height,
    id: chart_id,

    dataFormat: "json",
    dataSource: data


  });
}

function createLinearGuageChart(chart_container_id, width, height, chart_id, data) {
  if (FusionCharts(chart_id)) {
    FusionCharts(chart_id).dispose();
  }

  $("#" + chart_container_id).insertFusionCharts({
    swfUrl: "../../fusion/charts/HLinearGauge.swf",
    renderer: 'JavaScript',
    width: width,
    height: height,
    id: chart_id,

    dataFormat: "json",
    dataSource: data

  });

}

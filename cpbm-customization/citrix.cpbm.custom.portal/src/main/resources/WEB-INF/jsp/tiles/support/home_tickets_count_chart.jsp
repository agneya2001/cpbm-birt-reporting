<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>


<c:set var="chartId" value="tickets_count_pie_chart"  />
<c:set var="chartContainerId" value="tickets_count_pie_chartContainer"  />



<div id="<c:out value="${chartContainerId}"/>" > 
</div>
<script type="text/javascript">                     

$(document).ready(function(){
	var chart_height="110";
  function create_data_dict_for_ticket_count(){

    var new_tickets_count='<c:out value="${new_tickets_count}"/>';
    var working_tickets_count='<c:out value="${working_tickets_count}"/>';
    var closed_tickets_count='<c:out value="${closed_tickets_count}"/>';
    var escalated_tickets_count='<c:out value="${escalated_tickets_count}"/>';
    var tickets_chart_dict={
    		ticket_status_new:'<spring:message javaScriptEscape="true" code="ui.label.support.tickets.status.NEW"/>',
    	      ticket_status_working:'<spring:message javaScriptEscape="true" code="ui.label.support.tickets.status.WORKING"/>',
    	      ticket_status_escalated:'<spring:message javaScriptEscape="true" code="ui.label.support.tickets.status.ESCALATED"/>',
    	      ticket_status_closed:'<spring:message javaScriptEscape="true" code="ui.label.support.tickets.status.CLOSED"/>'
    };
    var chart_data=[];
    
    if(new_tickets_count > 0 || working_tickets_count > 0 || closed_tickets_count > 0 || escalated_tickets_count > 0){
    	chart_height="225";
        chart_data =[
                   { "label" : tickets_chart_dict.ticket_status_new, "value" : new_tickets_count, "link":"JavaScript:go_to_new_tickets();", "color":"9cd8e3" },
                   { "label" : tickets_chart_dict.ticket_status_working, "value" : working_tickets_count, "link":"JavaScript:go_to_working_tickets();", "color":"f1eda2" },
                   { "label" : tickets_chart_dict.ticket_status_closed, "value" : closed_tickets_count, "isSliced":"1", "link":"JavaScript:go_to_closed_tickets();", "color":"d8e7a2" },
                   { "label" : tickets_chart_dict.ticket_status_escalated, "value" : escalated_tickets_count, "link":"JavaScript:go_to_escalated_tickets();", "color":"f0b996"  }
          ];
    }
    
    return chart_data;
  }
  var chartId='<c:out value="${chartId}" />';
  var containerId = '<c:out value="${chartContainerId}" />';
  var chartData = create_data_dict_for_ticket_count();
  
  
    var options = {
      "pieRadius" : '85',
      "manageLabelOverflow":'0',
      "showBorder":"0",
      "basefont":"Arial",
      "outCnvBaseFontColor":"808080",
      "baseFontSize":"12",
      "bgColor":"F6F7F8, FFF"
      };
  createPieChart2D(containerId, "440", chart_height, chartId,  chartData, options);
  
});

</script>


<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>


<c:set var="chartId" value="service_health_chart"  />
<c:set var="chartContainerId" value="service_health_chartContainer"  />



<div id="<c:out value="${chartContainerId}"/>" class="service_health_chart_container"> 
</div>
<div class="service_health_chart_info_box">
    <div class="service_health_chart_info_content">
        <span class="title"><spring:message code="system.health.${status}" /></span>
        <span class="descrip" style="font-weight:bold;"><c:out value="${message}"/></span>
        <c:if test="${not empty latestNotification }">
            <span class="descrip">
                <c:out value="${latestNotification.description}" />
            </span>
        </c:if>
        
    </div>
</div>

<script type="text/javascript">                     

$(document).ready(function(){
    var health_status = '<c:out value="${status}"/>';
    var dialValue = "0";
    if(health_status=='NORMAL'){
    	dialValue = "16.65";
    }else if(health_status=='ISSUE'){
    	dialValue = "49.95";
    }else if(health_status=='DOWN'){
    	dialValue = "83.25";
    }
    label_dict={
    		healthStatusLabel: '<spring:message javaScriptEscape="true" code="system.health.${status}"/>',
    };
    
    var chartId='<c:out value="${chartId}" />';
    var containerId = '<c:out value="${chartContainerId}" />';
    var chartData=    {
    	  "chart": {
    		  "manageresize": "1",
              "origw": "300",
              "origh": "300",
              "bgColor":"FFFFFF",
              "palette": "3",
              "lowerlimit": "0",
              "upperlimit": "100",
              "gaugestartangle": "240",
              "gaugeendangle": "-60",
              "gaugeouterradius": "120",
              "gaugeinnerradius": "60%",
              "gaugefillmix": "{light-10},{light-30},{light-20},{dark-5},{color},{light-30},{light-20},{dark-10}",
              "gaugefillratio": "",
              "basefontcolor": "CCCCCC",
              "decimals": "1",
              "gaugeoriginx": "150",
              "gaugeoriginy": "150",
              "showtickmarks": "0",
              "showtickvalues": "0",
              "showBorder":"0",
              "showValue":"0",
              "valueBelowPivot":"0"
    	    
    	  },
    	  "colorrange": {
    	    "color": [
    	      {
    	        "minvalue": "0",
    	        "maxvalue": "33.3"
    	      },
    	      {
    	        "minvalue": "33.3",
    	        "maxvalue": "66.6"
    	      },
    	      {
    	        "minvalue": "66.6",
    	        "maxvalue": "100"
    	      }
    	    ]
    	  },
    	  "dials": {
    	    "dial": [
    	      {
    	        "id": "Dial1",
    	        "value": dialValue,
    	        "basewidth": "6",
    	        "topwidth": "1",
    	        "editmode": "1",
    	        "showvalue": "0",
    	        "rearextension": "10",
    	        "valuey": "270",
    	        "bgColor":"CCCCCC",
    	        "toolText":label_dict.healthStatusLabel
    	      }
    	    ]
    	  }
    	};
    createGuageChart(containerId, "110", "110", chartId,  chartData);
  
});

</script>


<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<script type="text/javascript">
	var today = $.datepicker.parseDate(g_dictionary.friendlyDate, $("#date_today").val());
	var tomorrow = new Date(today.getTime() + (24 * 60 * 60 * 1000));
	$('#planstartDate').datepicker({
	    duration: '',
	    showOn: "button",
	    changeYear: true,
	    buttonImage: "/portal/images/calendar_icon.png",
	    buttonImageOnly: true,
	    buttonText: "",
	    dateFormat: g_dictionary.friendlyDate,
	    showTime: false,
	    minDate: tomorrow,
	    beforeShow: function(dateText, inst){ 
	        $("#ui-datepicker-div").addClass("datepicker_stlying");
	        var isTodayAllowed = $("#isTodayAllowed").val();
	        if(isTodayAllowed == "true"){
	          $(this).datepicker("option", "minDate", today);
	        } else {
	          $(this).datepicker("option", "minDate", tomorrow);
	        }
	    },
	    onSelect: function(dateText, inst) {
	        $(this).attr("value", dateText);
	        if(dateText == $("#date_today").val()){
	          $("#plan_date_warning").show();
	        }
	        else{
	          $("#plan_date_warning").hide();
	        }
	        $("#planstartDate").each(function() {
	          $(this).attr("value", dateText);
	        });
	       /*  $("#plan_date_div").data("height.dialog", 150);
	        $("button").each(function(){
	        	$(this).attr('style', 'margin-top: 5px;');
	        });  */
	  },
	  onClose: function(dateText, inst) {
		 /*  $("#plan_date_div").data("height.dialog", 150);
		  $("button").each(function(){
			  $(this).attr("style", "margin-top: 5px;");
		  }); */
	  }
	 });
</script>

<spring:message code="dateonly.filter.format" var="ddmmyyyy_format"/>
<input type="hidden" id="date_today" value="<fmt:formatDate value="${date_today}" pattern="${ddmmyyyy_format}" />"/>

<div id="plan_date_div" class="dialog_formcontent wizard">
    <br/>
    <span class="helptext">
    <c:choose>
	    <c:when test="${plan_date != null}">
			  <spring:message code="label.catalog.edit.plan.date"/>
		</c:when>
		<c:otherwise>
		   <spring:message code="label.catalog.set.plan.date"/>
		</c:otherwise>
	</c:choose>
    </span>
    <br/><br/>
    <ol>
      <li style="margin:10px 0 0 10px;">
        <span style="color:#111;font-weight: bold; width:40px; margin:4px 0 0 8px;">
          <spring:message code="label.date"/>&nbsp;&nbsp;
        </span>
        <div style="margin:0 0 0 20px;width:300px;" class="mandatory_wrapper">
            <input id="isTodayAllowed" type="hidden" name="isTodayAllowed" value="<c:out value="${isTodayAllowed}"/>"/>
          <input type="text" id="planstartDate" name="startDate" readonly="readonly" class="text j_startDate" tabindex="1"
               value="<c:choose><c:when test="${planDateInFuture}"><fmt:formatDate value="${plan_date}" pattern="${ddmmyyyy_format}" /></c:when><c:otherwise><c:choose><c:when test="${isTodayAllowed}"><fmt:formatDate  value="${date_today}" pattern="${ddmmyyyy_format}" /></c:when><c:otherwise><fmt:formatDate  value="${date_tomorrow}" pattern="${ddmmyyyy_format}" /></c:otherwise></c:choose></c:otherwise></c:choose>"
               style="float:left;"
               prevvalue="<c:choose><c:when test="${plan_date != null}"><fmt:formatDate  value="${plan_date}" pattern="${ddmmyyyy_format}" /></c:when><c:otherwise></c:otherwise></c:choose>"
               dateformat="<c:out value="${ddmmyyyy_format}"/>" />
         </div>
       </li>
     </ol>
    
    <span class="helptext">
      <div id="plan_date_warning"></br><spring:message code="label.catalog.set.plan.date.today.warning"/></br></div>
    </span>
</div>

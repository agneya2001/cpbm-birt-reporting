<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/report.js"></script>
<jsp:include page="js_messages.jsp"></jsp:include>
<div class="maintitlebox">
	<h1><c:out value="${report.title}"/> </h1>
</div>
<div class="clearboth"></div>
<div class="main_addnewbox" style="display:block;">
	 <div class="main_addnewbox_contentbox">
	    <div class="main_addnewbox_titlebox">
	      <h2><spring:message code="ui.label.report.parameters"/></h2>
	    </div>
	    <form id="customReportForm" action="/portal/portal/reports/generate_custom_reports" method="GET" class="ajaxform" onsubmit="generateCustReport(this,event)">
		<div class="main_addnew_formbox" id="params">
			<div class="main_addnew_formpanels" style="border:none;">
				<ol>
					<li style="margin-left: 15px; display: inline;">
						<label for="customreport"><spring:message code="ui.label.report.custom.title" /></label> <div class="red_compulsoryicon">*</div>
						<select class="select" name="customreport" id="customreport"  tabindex="1">
		                	<option value=""><spring:message code="ui.label.report.choose"/></option>
		                		<c:forEach items="${reportMapping}" var="item" varStatus="status">
		                  			<option value="<c:out value="${item.key}"/>"> <c:out value="${item.value}"/> </option>
		                		</c:forEach>
		              	</select>
						<div class="main_addnew_formbox_errormsg" id="customreportError"></div>
					</li>
				</ol>
			</div>
			<div id="monthparam" class="main_addnew_formpanels" style="border-right:none;border-left: 1px dotted #333333;padding-left:7px;display:none;">
				<ol>
					<li style="margin-left: 15px; display: inline;">
						<label for="month"><spring:message code="ui.label.report.month"/></label> <div class="red_compulsoryicon">*</div>
						<select class="select" name="month" id="month"  tabindex="1">
		                	<option value=""><spring:message code="ui.label.report.choose"/></option>
		                		<c:forEach items="${months}" var="month" varStatus="status">
		                  			<option value="<c:out value="${status.index + 1}"/>"> <c:out value="${month}"/> </option>
		                		</c:forEach>
		              	</select>
						<div class="main_addnew_formbox_errormsg" id="monthError"></div>
					</li>				
					<li style="margin-left: 15px; display: inline;">
						<label for="year"><spring:message code="ui.label.report.year"/></label> <div class="red_compulsoryicon">*</div>
						<select class="select" name="year" id="year"  tabindex="1">
		                	<option value=""><spring:message code="ui.label.report.choose"/></option>
		                		<c:forEach items="${years}" var="year">
		                  			<option value="<c:out value="${year}"/>"> <c:out value="${year}"/> </option>
		                		</c:forEach>
		              	</select>						
						<div class="main_addnew_formbox_errormsg" id="yearError"></div>
					</li>
				</ol>
			</div>	
			<div id="dateparam" class="main_addnew_formpanels" style="border-right:none;border-left: 1px dotted #333333;padding-left:7px;display:none;">
				<ol>
					<li style="margin-left: 15px; display: inline;">
						<label for="date"><spring:message code="ui.label.report.date" /></label> <div class="red_compulsoryicon">*</div>
						<input name="date" id="date" class="text" tabindex="3" />
						<div class="main_addnew_formbox_errormsg" id="dateError"></div>
					</li>
				</ol>
			</div>					
 		</div>
      <div class="main_addnew_submitbuttonpanel">
        <div class="main_addnew_submitbuttonbox">
          <input tabindex="100"  class="commonbutton submitmsg" rel="<spring:message code="ui.label.report.rel.generating"/>" id="reportgenerate" name="mode" type="submit" value="<spring:message code="ui.label.report.generate"/>"/>
          <a tabindex="101"  class="commonbuttondisabled" rel="<spring:message code="ui.label.report.rel.downloading"/>" id="reportdownload"><spring:message code="ui.label.report.download"/></a>
          <a tabindex="102"  class="commonbuttondisabled" rel="<spring:message code="ui.label.report.rel.sending"/>" id="reportemail"><spring:message code="ui.label.report.send.email"/></a>
			<div id="email-dialog-modal" title="<spring:message code="ui.label.report.dialog.title"/>">
				<p style="margin-left: 5px;"><spring:message code="ui.label.report.dialog.emailids"/>:</p>
				<input type="text" id="emailids" style="width: 90%; margin-left: 5px;"/>
				&nbsp;&nbsp;<spring:message code="ui.label.report.dialog.endDateButtonText"/>.
				<div class="main_addnew_formbox_errormsg" id="emailidsError" style="margin:10px 0px 0px"></div>
			</div>          
        </div>
      </div>
      </form>
	</div>
</div>
<div class="clearboth"></div>
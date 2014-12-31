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
	    <form id="reportForm" action="/portal/portal/reports/<c:out value="${usageReportName}"/>" method="POST">
		<div class="main_addnew_formbox" id="params">
			<div class="main_addnew_formpanels" style="border: none">
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
				</ol>
			</div>
			<div class="main_addnew_formpanels" style="border-right:none;border-left: 1px dotted #333333;padding-left:7px;">
				<ol>
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
		</div>
      <div class="main_addnew_submitbuttonpanel">
        <div class="main_addnew_submitbuttonbox">
          <input tabindex="100"  class="commonbutton submitmsg" rel="<spring:message code="ui.label.report.rel.generating"/>" type="submit" value="<spring:message code="ui.label.report.submit"/>"/>
        </div>
      </div>
    </form>
	</div>
</div>
<div class="clearboth"></div>
<div class="charts">
<tiles:insertAttribute name="chart" />
</div>
<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
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
	    <form:form id="newRegistrationForm"  commandName="registrationFormReport" action="/portal/portal/reports/new_registrations" method="POST">
		<div class="main_addnew_formbox" id="params">
			<div class="main_addnew_formpanels" style="border: none">
				<ol>
					<li style="margin-left: 15px; display: inline;">
						<label for="start"><spring:message code="ui.label.report.startDate"/></label> <div class="red_compulsoryicon">*</div>
						<form:input id="start"  path="startDate" type="hidden"/>
						<input id="startDisplay" class="text" name="startDisplay" tabindex="3" readonly="true"/>
            <div class="main_addnew_formbox_errormsg" id="startError"><form:errors path="startDate"></form:errors></div>
            <div class="main_addnew_formbox_errormsg" id="startDisplayError"></div>
					</li>
				</ol>
			</div>
			<div class="main_addnew_formpanels" style="border-right:none;border-left: 1px dotted #333333;padding-left:7px;">
				<ol>
					<li style="margin-left: 15px; display: inline;">
						<label for="end"><spring:message code="ui.label.report.endDate"/></label> <div class="red_compulsoryicon">*</div>
						<form:input id="end"  path="endDate" type="hidden"/>
						<input id="endDisplay" class="text" name="endDisplay" tabindex="3" readonly="true"/>
						<div class="main_addnew_formbox_errormsg" id="endError"><form:errors path="endDate"></form:errors></div>
						<div class="main_addnew_formbox_errormsg" id="endDisplayError"></div>
					</li>
				</ol>
			</div>
		</div>
      <div class="main_addnew_submitbuttonpanel">
        <div class="main_addnew_submitbuttonbox">
          <input tabindex="100"  class="commonbutton submitmsg" rel="<spring:message code="ui.label.report.rel.generating"/>" type="submit" value="<spring:message code="ui.label.report.submit"/>"/>
        </div>
      </div>
    </form:form>
	</div>
</div>
<div class="clearboth"></div>
<div class="charts">
<tiles:insertAttribute name="chart" />
</div>
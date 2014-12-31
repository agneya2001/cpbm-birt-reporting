<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
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
	    <form:form id="reportForm" commandName="customerRankReport" action="/portal/portal/reports/customer_rank" method="POST">
		<div class="main_addnew_formbox" id="params">
			<div class="main_addnew_formpanels" style="border: none">
				<ol>
					<li style="margin-left: 15px; display: inline;">
						<label for="month"><spring:message code="ui.label.report.month"/></label>
						<div class="red_compulsoryicon">*</div> 
						<form:select cssClass="select" path="reportMonth" tabindex="1">
		                	<option value=""><spring:message code="ui.label.report.choose"/></option>
		                		<c:forEach items="${months}" var="month" varStatus="status">
		                  			<option value="<c:out value="${status.index + 1}"/>" <c:if test="${customerRankReport.reportMonth == (status.index + 1)}" >selected="selected" </c:if>> <c:out value="${month}"/> </option>
		                		</c:forEach>
		              	</form:select>
						<div class="main_addnew_formbox_errormsg" id="reportMonthError"><form:errors path="reportMonth"></form:errors></div>
					</li>
				</ol>
			</div>
			<div class="main_addnew_formpanels" style="border-right:none;border-left: 1px dotted #333333;padding-left:7px;">
				<ol>
					<li style="margin-left: 15px; display: inline;">
						<label for="year"><spring:message code="ui.label.report.year"/></label> <div class="red_compulsoryicon">*</div>
						<form:select cssClass="select" path="reportYear" tabindex="1">
		                	<option value=""><spring:message code="ui.label.report.choose"/></option>
		                		<c:forEach items="${years}" var="year">
		                  			<option value="<c:out value="${year}"/>" <c:if test="${customerRankReport.reportYear == year}" >selected="selected" </c:if>> <c:out value="${year}"/> </option>
		                		</c:forEach>
		              	</form:select>						
						<div class="main_addnew_formbox_errormsg" id="reportYearError"><form:errors path="reportYear"></form:errors></div>
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
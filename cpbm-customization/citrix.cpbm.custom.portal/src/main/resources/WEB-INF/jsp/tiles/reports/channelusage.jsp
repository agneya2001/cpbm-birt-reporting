<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<script type="text/javascript" src="<%=request.getContextPath() %>/js/report.js"></script>
<jsp:include page="js_messages.jsp"></jsp:include>
<div class="clearboth"></div>
<div class="main_addnewbox" style="display:block;">
   <div class="main_addnewbox_contentbox">
      <div class="main_addnewbox_titlebox">
        <h2><spring:message code="ui.label.report.parameters"/></h2>
      </div>
      <form:form id="channelUsageReportForm" commandName="channelUsageReportForm" action="/portal/portal/reports/channel_usage" method="POST">
        <div class="main_addnew_formbox" id="params">
          <div class="main_addnew_formpanels" style="border: none">
            <ol>
            <li style="margin-left: 15px; display: inline;">
            <label for="start"><spring:message code="ui.label.report.startDate"/></label> <div class="red_compulsoryicon">*</div>
            <form:input id="start"  path="startDate" type="hidden"/>
            <input id="startDisplay" name="startDisplay" class="text" tabindex="3" readonly="true"/>
            <div class="main_addnew_formbox_errormsg" id="startError"><form:errors path="startDate"></form:errors></div>
            <div class="main_addnew_formbox_errormsg" id="startDisplayError"></div>
          </li>
          <li style="margin-left: 15px; display: inline;">
            <label for="end"><spring:message code="ui.label.report.endDate"/></label> <div class="red_compulsoryicon">*</div>
            <form:input id="end"  path="endDate" type="hidden"/>
            <input id="endDisplay" name="endDisplay" class="text" tabindex="3" readonly="true"/>
            <div class="main_addnew_formbox_errormsg" id="endError"><form:errors path="endDate"></form:errors></div>
            <div class="main_addnew_formbox_errormsg" id="endDisplayError"></div>
          </li>
            </ol>
          </div>
          
          
        <div class="main_addnew_formpanels" style="border: none">
         <ol id= "filterByChannel">
          <li style="margin-left: 15px; display: inline;">
            <label for="channelId"><spring:message code="ui.accounts.all.header.channel"/></label> <div class="red_compulsoryicon">*</div>
            <form:select  id ="channelId" path="channelId" class="select" tabindex="3">
            <option value=""><spring:message code="label.choose"/></option>
             <c:forEach items="${channels}" var="channel">
               <option value="${channel.id}" <c:if test="${channelUsageReport.channelId == channel.id}" >selected="selected" </c:if>> ${channel.name}</option>
             </c:forEach>
            </form:select>
            <div class="main_addnew_formbox_errormsg" id="channelIdError"><form:errors path="channelId"></form:errors></div>
          </li>
          </ol>
          <ol id="filterByBillingGroup" style="display: none;">
          <li style="margin-left: 15px; display: inline;"> 
            <label for="billingGroupId"><spring:message code="label.billing.group"/></label> <div class="red_compulsoryicon">*</div>
            <form:select id="billingGroupId" path="billingGroupId" class="select" tabindex="3">
            <option value=""><spring:message code="label.choose"/></option>
             <c:forEach items="${billingGroups}" var="billingGroup">
               <option value="${billingGroup.id}" <c:if test="${channelUsageReport.billingGroupId == billingGroup.id}" >selected="selected" </c:if>>  ${billingGroup.name}</option>
             </c:forEach>
            </form:select>
            <div class="main_addnew_formbox_errormsg" id="billingGroupIdError"><form:errors path="billingGroupId"></form:errors></div>
          </li>
          </ol>
          </div>
        </div>
			<div class="main_addnew_submitbuttonpanel">
			<span class="downloadmsg">
			<c:if test="${noRecordsFound}">
              <spring:message code="label.message.no.records.found" />
        </c:if>
			</span>
			<div class="main_addnew_submitbuttonbox" >
            <input tabindex="100"  id="csvgenerate" class="commonbutton submitmsg"   type="submit" value="<spring:message code="label.download.usage.csv"/>"/>
          </div>
       </div>
        
      </form:form>
  </div>
</div>
<div class="clearboth"></div>
<input type="hidden" id = "filterProductUsage" value="${usageReportName}" />
<input type="hidden" id = "selectedChannel" value="${selectedChannel}" />
<input type="hidden" id = "selectedBillingGroup" value="${selectedBillingGroup}" />
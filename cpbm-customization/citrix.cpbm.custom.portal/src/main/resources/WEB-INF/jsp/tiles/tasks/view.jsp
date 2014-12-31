<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:if test="${!empty task}">
  <spring:message code="date.format" var="date_format"/>
  <div class="widget_actionbar"></div>
  <div class="top_notifications">
    <div id="top_message_panel" class="common_messagebox widget" style="display:none;">
      <button type="button" class="close js_close_parent" >&times;</button>
      <span id="status_icon"></span><p id="msg"></p>
    </div>
    <div id="action_result_panel" class="common_messagebox widget" style="display:none;">
      <button type="button" class="close js_close_parent" >&times;</button>
      <span id="status_icon"></span><p id="msg"></p>
    </div>
  </div>
        
  <div class="widget_browser">
    <div class="widget_browsermaster">
      <div class="widget_browser_contentarea">
        <div class="widget_browsergrid_wrapper master">
          <div class="widget_grid master even first">
            <div class="widget_grid_labels">
              <span><spring:message code="ui.task.type" /></span>
            </div>
            <div class="widget_grid_description">
              <span><spring:message code="ui.task.type.${task.type}.name" /></span>
            </div>
          </div>
            
          <div class="widget_grid master even">
              <c:choose>
              <c:when test="${task.state == 'PENDING' }">
               <div class="widget_grid_labels" style="height:39px;">
              		<span><spring:message code="ui.task.createdAt" /> </span>
            	</div>  
            	<div class="widget_grid_description">
              		<span>
              			<fmt:formatDate value="${task.createdAt}" pattern="${date_format}" type="date" dateStyle="MEDIUM" timeZone="${currentUser.timeZone}"/>
              		</span>
              	</div>
              </c:when>
              <c:otherwise>
              <div class="widget_grid_labels" style="height:39px;">
              		<span><spring:message code="ui.task.completedAt" /> </span>
            	</div>  
            	<div class="widget_grid_description">
              		<span>
              			<fmt:formatDate value="${task.updatedAt}" pattern="${date_format}" type="date" dateStyle="MEDIUM" timeZone="${currentUser.timeZone}"/>
              		</span>
              	</div>
              </c:otherwise>
              </c:choose>
          </div>

          <div class="widget_grid master even">
            <div class="widget_grid_labels">
              <span><spring:message code="ui.task.state" /></span>
            </div>  
            <div class="widget_grid_description">
              <span class="taskState">
                <spring:message code="ui.task.state.${task.state}" />
              </span>
            </div>
          </div>
        </div>
        <div class="widget_masterbigicons task"></div>
      </div>
    </div>
          
    <div class="widget_browser_contentarea">
      <ul class="widgets_detailstab">
          <li class="widgets_detailstab active"><spring:message code="ui.task.transaction"/></li>
      </ul>
      
      <div id="details_div">
        <div class="widget_details_actionbox">
          <ul class="widget_detail_actionpanel"></ul>
        </div>
        <div class="widget_browsergrid_wrapper details" id="details_content">
        <c:if test="${task.state == 'PENDING' }">
          <div id="taskDiv" class="widget_grid details even">
            <div class="widget_grid_labels">
               <span><spring:message code="ui.task"/></span>
            </div>
            <div class="widget_grid_description" >
              <span>
                <c:choose>
                  <c:when test="${task.displayMode == 'POPUP'}">
                    <a href="javascript:void(0);" class="taskPopup taskname" id="taskPopup${task.uuid}">
                      <spring:message code="message.view.pendingactions.click" />
                    </a>
                    <div id="approvalTask_panel" title="<spring:message code="task.approval.dialog.title" />" style="display: none"></div>
                  </c:when>
                  <c:otherwise>
                    <a href="<%=request.getContextPath() %>/portal/${taskUrl}"><spring:message code="message.view.pendingactions.click" /></a>
                  </c:otherwise>
                </c:choose>
              </span>
            </div>
          </div>
          </c:if>
          <div class="widget_grid details odd">
            <div class="widget_grid_labels">
               <span><spring:message code="ui.task.transaction.workflow"/></span>
            </div>
            <div class="widget_grid_description" >
              <span>
                <a href="javascript:void(0);" class="workflowDetailsPopup" id="workflowdetails${task.businessTransaction.workflowId}">
                  <spring:message code="message.view.workflow.details.click" />
                </a>
                <div class="workflow_details_popup" title="<c:out value="${title.workflow.details.popup.name}"/>" style="display: none"> </div>
              </span>
            </div>
          </div>
          <div class="widget_grid details even">
            <div class="widget_grid_labels">
               <span><spring:message code="ui.accounts.all.header.name"/></span>
            </div>
            <div class="widget_grid_description" >
              <span>
                <c:out value="${task.tenant.name}"></c:out>
              </span>
            </div>
          </div>
          <div class="widget_grid details odd">
            <div class="widget_grid_labels">
               <span><spring:message code="ui.task.transaction.state"/></span>
            </div>
            <div class="widget_grid_description" >
              <span>
                <spring:message code="label.business.transaction.state.${fn:toLowerCase(task.businessTransaction.state)}"/>
              </span>
            </div>
          </div>
          <div class="widget_grid details even">
            <div class="widget_grid_labels">
               <span><spring:message code="ui.task.transaction.startedAt"/></span>
            </div>
            <div class="widget_grid_description" >
              <span>
                <fmt:formatDate value="${task.businessTransaction.startDate}" pattern="${date_format}" type="date" dateStyle="MEDIUM" timeZone="${currentUser.timeZone}"/>
              </span>
            </div>
          </div>
          <div class="widget_grid details odd">
            <div class="widget_grid_labels">
               <span><spring:message code="ui.task.transaction.endedAt"/></span>
            </div>
            <div class="widget_grid_description" >
              <span>
                <fmt:formatDate value="${task.businessTransaction.endDate}" pattern="${date_format}" type="date" dateStyle="MEDIUM" timeZone="${currentUser.timeZone}"/>
              </span>
            </div>
          </div>
          <c:choose>
            <c:when test="${task.businessTransaction.type == 'tenantStateChange' }">
              <div class="widget_grid details even">
                <div class="widget_grid_labels">
                  <span><spring:message code="ui.task.tenant.intialState" /></span>
                </div>
                <div class="widget_grid_description" >
                  <span>
                    <spring:message code="tenant.state.${fn:toLowerCase(task.businessTransaction.tenantInitialState)}" />
                  </span>
                </div>
              </div> 
              <div class="widget_grid details odd">
                <div class="widget_grid_labels">
                  <span><spring:message code="ui.task.tenant.targetState" /></span>
                </div>
                <div class="widget_grid_description" >
                  <span>
                    <spring:message code="tenant.state.${fn:toLowerCase(task.businessTransaction.tenantTargetState)}" />
                  </span>
                </div>
              </div> 
            </c:when>
            <c:otherwise></c:otherwise>
          </c:choose> 
        </div>
      </div>
    </div>
  </div>
</c:if>
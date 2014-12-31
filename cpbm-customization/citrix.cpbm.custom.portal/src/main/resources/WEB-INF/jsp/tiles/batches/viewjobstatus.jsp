<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<div class="widget_actionbar">
    <div class="widget_actionarea" id="top_actions">
      <div id="spinning_wheel" style="display:none">
        <div class="maindetails_footer_loadingpanel">
        </div>
        <div class="maindetails_footer_loadingbox first">
          <div class="maindetails_footer_loadingicon"></div>
          <p id="in_process_text"></p>
        </div>
      </div>
    </div>
</div>

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
              <span><spring:message code="ui.bath.job.view.status.page.id"/></span>
            </div>
            <div class="widget_grid_description">
              <span><c:out value="${jobStatus.id}"/></span>
            </div>
          </div>
          
          <div class="widget_grid master even">
            <div class="widget_grid_labels">
              <span><spring:message code="ui.bath.job.view.status.page.name"/></span>
            </div>  
            <div class="widget_grid_description">
              <span>
                <c:if test="${jobStatus != null && jobStatus != ''}">
                  <spring:message var="i18nJobName" htmlEscape="false" code="batch.jobName.${jobStatus.jobName}" text=""/>
                  <c:choose>
                    <c:when test="${not empty i18nJobName}">
                      <c:out value="${i18nJobName}"/>
                    </c:when>
                    <c:otherwise>
                      <c:set var="jobName" value="${jobStatus.jobName}"/>
                      <c:set var="jobNameWithoutUnderScore" value="${fn:replace(jobName, '_', ' ')}"/>
                      <c:forEach var="str" items="${fn:split(jobNameWithoutUnderScore, ' ')}">
                        <c:out value="${fn:toUpperCase(fn:substring(str, 0, 1))}${fn:toLowerCase(fn:substring(str, 1, -1))} "/>
                      </c:forEach>
                    </c:otherwise>
                  </c:choose>
                </c:if>
              </span>
            </div>
          </div>
          
          <div class="widget_grid master even">
            <div class="widget_grid_labels" style="height:39px;">
              <span><spring:message code="ui.bath.job.view.status.page.state"/></span>
            </div>  
            <div class="widget_grid_description">
              <span>
                <c:if test="${jobStatus != null && jobStatus != ''}">
                  <spring:message code="ui.bath.job.list.page.state.${jobStatus.stateLower}"/>
                </c:if>
              </span>
            </div>
          </div>
          
      </div>
      <div class="widget_masterbigicons batchjobs"></div>
    </div>
  </div>

  <div class="widget_browser_contentarea">
    <spring:message code="dateonly.format" var="dateonly_format"/>
    <ul class="widgets_detailstab">
      <li class="widgets_detailstab active" id="details_tab" onclick="details_tab_click(this);">
         <spring:message code="ui.bath.job.view.viewDetails"/>
      </li>
    </ul>
    
    <div id="details_div">
    <div class="widget_details_actionbox">
      <ul class="widget_detail_actionpanel"></ul>
    </div>
    <div class="widget_browsergrid_wrapper details" id="details_content">
      
      <div class="widget_grid details even">
        <div class="widget_grid_labels">
           <span><spring:message code="ui.bath.job.view.status.page.description"/></span>
        </div>
        <div class="widget_grid_description" >
          <span class="details_wide">
            <c:out value="${jobStatus.description}"/>
          </span>
        </div>
      </div> 
      <div class="widget_grid details odd">
        <div class="widget_grid_labels">
           <span><spring:message code="ui.bath.job.list.page.start.time"/></span>
        </div>
        <div class="widget_grid_description" >
          <span>
            <spring:message code="date.format" var="date_format"/>
            <fmt:formatDate value="${jobStatus.startTime}" pattern="${date_format}" timeZone="${currentUser.timeZone}"/>
          </span>
        </div>
      </div>
      <div class="widget_grid details even">
        <div class="widget_grid_labels">
           <span><spring:message code="ui.bath.job.list.page.end.time"/></span>
        </div>
        <div class="widget_grid_description" >
          <span>
            <spring:message code="date.format" var="date_format"/>
            <fmt:formatDate value="${jobStatus.endTime}" pattern="${date_format}" timeZone="${currentUser.timeZone}"/>
          </span>
        </div>
      </div>
    </div>
    </div>
    
  </div>

</div>

              
<!-- End view Product Details -->
                    

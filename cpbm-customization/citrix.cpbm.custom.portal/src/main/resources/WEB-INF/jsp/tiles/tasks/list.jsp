<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<script type="text/javascript">
  var nextPage='<c:out value="${nextPage}"/>';
  var prevPage='<c:out value="${prevPage}"/>';
</script>
<spring:message code="dateonly.format" var="dateonly_format"/>
<div class="widget_box" id="all_tasks">
    <div class="widget_leftpanel">
        <div class="widget_titlebar">
          <h2><spring:message code="label.list.all"/></h2>
        </div>
        <div class="widget_searchpanel">
          <div class="widget_searchcontentarea" id="search_panel">         
            <span class="label"><spring:message code="label.filter.by"/>:</span>
              <select id="selectedTaskFilter" class="select" >
                <c:forEach items="${taskfilters}" var="taskfilter">
                  <c:choose>
                    <c:when test="${currentFilter == taskfilter}">
                      <c:set var="selected" value="selected" />
                    </c:when>
                    <c:otherwise>
                      <c:set var="selected" value="" />
                    </c:otherwise>
                  </c:choose>
                  <option value="<c:out value="${taskfilter}"/>" <c:out value="${selected}"/>>
                    <spring:message code="ui.alltasks.filter.${taskfilter}"></spring:message>
                  </option>
                </c:forEach>
              </select>
          </div>
        </div>
        <div class="widget_navigation">
          <ul class="widget_navigationlist" id="grid_row_container">
              <c:choose>
                <c:when test="${empty tasks || tasks == null}">
                  <c:set var="tasksLen" value="0"/>
                  <!--look when there is no list starts here-->
                  <li class="widget_navigationlist nonlist" id="non_list">
                      <span class="navicon notifications"></span>
                        <div class="widget_navtitlebox">
                          <span class="newlist">
                            <spring:message var="noTasksMessage" code="ui.label.emptylist.tasks" ></spring:message>
                            <spring:message code="ui.label.emptylist.notavailable" arguments="${noTasksMessage}" htmlEscape="false"/>
                          </span>
                        </div>
                        
                  </li>
                  <!--look when there is no list ends here-->
                </c:when>
                <c:otherwise> 
                  <c:forEach items="${tasks}" var="currentTask" varStatus="taskStatus">                           
                      <c:choose>
                        <c:when test="${taskStatus.index == 0}">
                          <c:set var="task" value="${currentTask}" scope="request"/>
                          <c:set var="taskUrl" value="${tasksUrlMap.get(currentTask.uuid)}" scope="request"/>
                          <c:set var="selected" value="selected"/>
                          <c:set var="active" value="active"/>
                        </c:when>
                        <c:otherwise>
                            <c:set var="selected" value=""/>
                            <c:set var="active" value=""/>
                        </c:otherwise>
                      </c:choose> 
                      <li class="<c:out value="widget_navigationlist ${selected} ${active} "/>" id="row${currentTask.uuid}">
                        <span id="nav_icon" class="navicon notifications"></span>
                        <div class="widget_navtitlebox db_gridbox_rows">
                          <span class="title">
                            <spring:message code="ui.task.type.${currentTask.type}.name" />
                          </span>
                          <span class="subtitle">
                            <spring:message code="ui.task.createdAt" /> : <fmt:formatDate value="${currentTask.createdAt}" pattern="${dateonly_format}" type="date" dateStyle="MEDIUM" timeZone="${currentUser.timeZone}"/>
                          </span>
                        </div>
                        <div class="widget_statusicon">
                          
                        </div>
                        <!--Info popover starts here-->
                        <div class="widget_info_popover" id="info_bubble" style="display:none">
                          <div class="popover_wrapper" >
                          <div class="popover_shadow"></div>
                          <div class="popover_contents">
                            <div class="raw_contents">
                              
                              <div class="raw_content_row">
                                <div class="raw_contents_title">
                                  <span>
                                    <spring:message code="ui.accounts.all.header.name" />
                                  </span>
                                </div>
                                <div class="raw_contents_value">
                                  <span>
                                    ${currentTask.tenant.name}
                                  </span>
                                </div>
                               </div>
                               <div class="raw_content_row">
                                <div class="raw_contents_title">
                                  <span>
                                    <spring:message code="ui.task.state" />
                                  </span>
                                </div>
                                <div class="raw_contents_value">
                                  <span>
                                    <spring:message code="ui.task.state.${currentTask.state}" />
                                  </span>
                                </div>
                                 </div>
                              </div>
                            </div>
                          </div>
                        </div>
                        <!--Info popover ends here-->
                      </li>
                  </c:forEach>                               
                </c:otherwise> 
              </c:choose>
          </ul>
        </div>
        <c:if test="${!enableNext}">
          <c:set var="nextButton" value="nonactive"/>
        </c:if>
        <c:if test="${!enablePrevious}">
          <c:set var="prevButton" value="nonactive"/>
        </c:if>
        <div class="widget_panelnext">
          <div class="widget_navnextbox">
              <a class="widget_navnext_buttons prev ${prevButton}" href="javascript:void(0);" id="click_previous"><spring:message code="label.previous.short"/></a>
              <a class="widget_navnext_buttons next ${nextButton}" href="javascript:void(0);" id="click_next"><spring:message code="label.next"/></a>
            </div>
        </div>
    </div>
    <div class="widget_rightpanel" id="viewTaskDiv">
        <tiles:insertDefinition name="task.view"/>
    </div>
</div>
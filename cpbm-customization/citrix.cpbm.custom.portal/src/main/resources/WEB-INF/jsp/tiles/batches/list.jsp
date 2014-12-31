<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/batch.js"></script>
<script type="text/javascript">
var batchJobUrl = "<%=request.getContextPath() %>/portal/admin/batch/status";
var totalpages = "<c:out value="${totalpages}"/>";
var currentPage = "<c:out value="${currentPage}"/>";
var perPageValue = "<c:out value="${perPage}"/>";
</script>

<div class="widget_box">
    <div class="widget_leftpanel">
        <div class="widget_titlebar">
            <h2>
            <span class=""><spring:message code="label.list.all"/></span>
            </h2>
        </div>
        <div class="widget_searchpanel">
        <div class="widget_searchcontentarea" id="search_panel">
          </div>
        </div>
        
        <div class="widget_navigation">
          <ul class="widget_navigationlist" id="grid_row_container">
              <c:choose>
                <c:when test="${empty batchList || batchList == null}">
                  <c:set var="batchjobListLen" value="0"/>
                  <!--look when there is no list starts here-->
                  <li class="widget_navigationlist nonlist" id="non_list">
                      <span class="navicon batchjobs"></span>
                        <div class="widget_navtitlebox">
                          <span class="newlist">
                            <spring:message var="batchjobsMsg" code="ui.label.emptylist.batchjobs" ></spring:message>
                            <spring:message code="ui.label.emptylist.notavailable" arguments="${batchjobsMsg}" htmlEscape="false"/>
                          </span>
                        </div>
                        
                  </li>
                  <!--look when there is no list ends here-->
                </c:when>
                <c:otherwise>                            
                  <c:forEach var="batch" items="${batchList}" varStatus="status">
                      <c:set var="batchjobListLen" value="${batchjobListLen+1}"/>
                      <c:choose>
                        <c:when test="${status.index == 0}">
                          <c:set var="firstjob" value="${batch}"/>
                          <c:set var="selected" value="selected"/>
                          <c:set var="active" value="active"/>
                        </c:when>
                        <c:otherwise>
                            <c:set var="selected" value=""/>
                            <c:set var="active" value=""/>
                        </c:otherwise>
                      </c:choose> 
                      <li class="<c:out value="widget_navigationlist ${selected} ${active} "/>" id="<c:out value="row${batch.id}"/>" onclick="viewbatchjob(this);" onmouseover="onBatchJobMouseover(this);" onmouseout="onBatchJobMouseout(this);">
                        <span id="nav_icon" class="navicon batchjobs"></span>
                        <div class="widget_navtitlebox <c:out value="db_gridbox_rows"/>">
                          <span class="title">
                            <spring:message var="i18nJobName" htmlEscape="false" code="batch.jobName.${batch.jobName}" text=""/>
                            <c:choose>
                              <c:when test="${not empty i18nJobName}">
                                <spring:message htmlEscape="false" code="batch.jobName.${batch.jobName}"/>
                              </c:when>
                              <c:otherwise>
                                <c:set var="jobName" value="${batch.jobName}"/>
                                <c:set var="jobNameWithoutUnderScore" value="${fn:replace(jobName, '_', ' ')}"/>
                                <c:forEach var="str" items="${fn:split(jobNameWithoutUnderScore, ' ')}">
                                  <c:out value="${fn:toUpperCase(fn:substring(str, 0, 1))}${fn:toLowerCase(fn:substring(str, 1, -1))} "/>
                                </c:forEach>
                              </c:otherwise>
                            </c:choose>
                          </span>
                          <span class="subtitle">
                            <spring:message code="ui.bath.job.list.page.state"/>:
                            <spring:message code="ui.bath.job.list.page.state.${batch.stateLower}"/>
                          </span>
                        </div>
                        <c:choose>
                          <c:when test="${batch.state == 'UNDEFINED'}">
                              <c:set var="status_icon" value="undefined"/>
                          </c:when>
                          <c:when test="${batch.state == 'RUNNING'}">
                              <c:set var="status_icon" value="running"/>
                          </c:when>
                          <c:when test="${batch.state == 'INTERRUPTED'}">
                              <c:set var="status_icon" value="interrupted"/>
                          </c:when>
                          <c:when test="${batch.state == 'ERRORED'}">
                              <c:set var="status_icon" value="errored"/>
                          </c:when>
                          <c:when test="${batch.state == 'PARTIAL_ERROR'}">
                              <c:set var="status_icon" value="partial_error"/>
                          </c:when>
                          <c:when test="${batch.state == 'COMPLETED'}">
                              <c:set var="status_icon" value="completed"/>
                          </c:when>
                          <c:otherwise>
                              <c:set var="status_icon" value="undefined"/>
                          </c:otherwise>
                        </c:choose> 
                        <div class="<c:out value="widget_statusicon ${status_icon}" />"></div>
                        <!--Info popover starts here-->
                        <div class="widget_info_popover" id="info_bubble" style="display:none">
                          <div class="popover_wrapper" >
                          <div class="popover_shadow"></div>
                          <div class="popover_contents">
                            <div class="raw_contents">
                              <spring:message code="date.format" var="date_format"/>
                              <div class="raw_content_row">
                                <div class="raw_contents_title">
                                  <span>
                                  <spring:message code="ui.bath.job.list.page.id"/>
                                  </span>
                                </div>
                                <div class="raw_contents_value">
                                  <span>
                                    <c:out value="${batch.id}"/>
                                  </span>
                                </div>
                              </div>
                              
                              <div class="raw_content_row">
                                <div class="raw_contents_title">
                                  <span>
                                  <spring:message code="ui.bath.job.list.page.start.time"/>
                                  </span>
                                </div>
                                <div class="raw_contents_value">
                                  <span>
                                    <fmt:formatDate value="${batch.startTime}" pattern="${date_format}" timeZone="${currentUser.timeZone}"/>
                                  </span>
                                </div>
                              </div>
                              
                              <div class="raw_content_row">
                                <div class="raw_contents_title">
                                  <span>
                                  <spring:message code="ui.bath.job.list.page.end.time"/>
                                  </span>
                                </div>
                                <div class="raw_contents_value">
                                  <span>
                                    <fmt:formatDate value="${batch.endTime}" pattern="${date_format}" timeZone="${currentUser.timeZone}"/>
                                  </span>
                                </div>
                              </div>
                              
                              <div class="raw_content_row">
                                <div class="raw_contents_title">
                                  <span>
                                  <spring:message code="ui.bath.job.list.page.state"/>
                                  </span>
                                </div>
                                <div class="raw_contents_value">
                                  <span>
                                    <spring:message code="ui.bath.job.list.page.state.${batch.stateLower}"/>
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
        <script type="text/javascript">
          var batchjobListLen = "<c:out value="${batchjobListLen}"/>";
        </script>
        <div class="widget_panelnext">
          <div class="widget_navnextbox">
              <a class="widget_navnext_buttons prev nonactive" href="javascript:void(0);" id="click_previous"><spring:message code="label.previous.short"/></a>
                <a class="widget_navnext_buttons next" href="javascript:void(0);" id="click_next"><spring:message code="label.next"/></a>
            </div>
        </div>
    </div>
    <div id="<c:out value="count${size}"/>" class="countDiv"></div>
    <div class="widget_rightpanel" id="viewjobstatusDiv">
        <c:if test="${firstjob != null}">
          <script>
          var li_id = "<c:out value="row${firstjob.id}"/>";
          viewFirstBatchJob(li_id);
          </script>
        </c:if>
        <jsp:include page="viewjobstatus.jsp"></jsp:include>
    </div>
        
</div>



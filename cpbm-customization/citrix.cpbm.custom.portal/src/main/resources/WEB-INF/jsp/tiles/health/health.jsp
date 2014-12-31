<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<script type="text/javascript" src="<%=request.getContextPath() %>/js/health.js"></script>
<jsp:include page="js_messages.jsp"></jsp:include>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/timepicker.js"></script>
<script type="text/javascript">

  var ops = false;

  <sec:authorize access="hasRole('ROLE_OPS_COMMUNICATION')">

  ops = true;

  </sec:authorize>

  var healthUrl = "<%=request.getContextPath() %>/portal/health/";
  
  var totalpages = "<c:out value="${totalpages}"/>";
  var currentPage = "<c:out value="${currentPage}"/>";
  var perPageValue = "<c:out value="${perPage}"/>";
  var dateStatusLen = "<c:out value="${dateStatusLen}"/>";

</script>

<div class="widget_box" id="health_status_div">
    <div class="widget_leftpanel">
        <div class="widget_titlebar">
          <h2><spring:message code="label.list.all"/></h2>
          <sec:authorize access="hasRole('ROLE_OPS_COMMUNICATION')">
            <a class="widget_addbutton" href="javascript:void(0);" onclick="addNewStatusGet()" class="addNewStatus"><spring:message code="ui.system.health.addstatus"/></a>
          </sec:authorize>
        </div>
        <div class="widget_searchpanel">
          <div class="widget_searchcontentarea">
          </div>
        </div>

         <div class="widget_navigation">
          <ul class="widget_navigationlist" id="grid_row_container">
              <c:choose>
                <c:when test="${empty dateStatus || dateStatus == null}">
                  <c:set var="systemHealthListLen" value="0"/>
                  <!--look when there is no list starts here-->
                  <li class="widget_navigationlist nonlist" id="non_list">
                      <span class="navicon servicehealth"></span>
                        <div class="widget_navtitlebox">
                          <span class="newlist">
                            <spring:message var="systemhealthMsg" code="ui.label.emptylist.systemhealth" ></spring:message>
                            <spring:message code="ui.label.emptylist.notavailable" arguments="${systemhealthMsg}" htmlEscape="false"/>
                          </span>
                        </div>
                        
                  </li>
                  <!--look when there is no list ends here-->
                </c:when>
                <c:otherwise>                            
                  <c:forEach var="item" items="${dateStatus}" varStatus="status">
                      <c:set var="systemHealthListLen" value="${systemHealthListLen+1}"/>
                      <c:choose>
                        <c:when test="${status.index == 0}">
                            <c:set var="firstHealth" value="${item}"/>
                            <c:set var="selected" value="selected"/>
                            <c:set var="active" value="active"/>
                        </c:when>
                        <c:otherwise>
                            <c:set var="selected" value=""/>
                            <c:set var="active" value=""/>
                        </c:otherwise>
                      </c:choose>
                      <c:set var="iso8601_date_format" value="yyyy-MM-dd"/>
                      <li class="<c:out value="widget_navigationlist ${selected} ${active} "/>" id="row<fmt:formatDate value="${item.key}"  pattern="${iso8601_date_format}" timeZone="${currentUser.timeZone}"/>" >
                          <c:set var="doesHistoryHasIssue" value="none"/>
                          <c:if test="${not empty dateStatusHistory[item.key]}"   >  
                              <c:forEach items="${dateStatusHistory[item.key]}" var="statusitem" varStatus="historystatus">
                                <c:if test="${statusitem.notificationType=='DISRUPTION'}">
                                  <c:set var="doesHistoryHasIssue" value=""/>
                                </c:if>
                              </c:forEach>
                          </c:if>
                          <c:if test="${item.value=='NORMAL'}">
                            <span id="nav_icon" class="navicon normal_health"><span class="navicon overlay_infoicon" style="display:<c:out value="${doesHistoryHasIssue}"/>;"></span></span>
                          </c:if>
                          <c:if test="${item.value=='ISSUE'}">
                            <span id="nav_icon" class="navicon perfissue_health"><span class="navicon overlay_infoicon" style="display:<c:out value="${doesHistoryHasIssue}"/>;"></span></span>
                          </c:if> <c:if test="${item.value=='DOWN'}">
                            <span id="nav_icon" class="navicon disruption_health"><span class="navicon overlay_infoicon" style="display:<c:out value="${doesHistoryHasIssue}"/>;"></span></span>
                          </c:if>
                          
                        <input type="hidden" id="dateFormat" value="<c:out value="${iso8601_date_format}"/>" />
                        <div class="widget_navtitlebox <c:out value="db_gridbox_rows"/>">
                          <span class="title">
                            <spring:message code="${item.value.description}" />
                          </span>
                          <span class="subtitle">
                            <spring:message code="dateonly.format" var="dateonly_format" />
                            <fmt:formatDate value="${item.key}" pattern="${dateonly_format}" timeZone="${currentUser.timeZone}"/>
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
                                  <span><spring:message code="ui.system.health.status"/>:</span>
                                </div>
                                <div class="raw_contents_value">
                                  <span>
                                    <spring:message code="system.health.${item.value}"/>
                                  </span>
                                </div>
                              </div>
                              <div class="raw_content_row">
                                <div class="raw_contents_title">
                                  <span><spring:message code="ui.system.health.date"/>:</span>
                                </div>
                                <div class="raw_contents_value">
                                  <span>
                                    <fmt:formatDate value="${item.key}" pattern="${dateonly_format}" timeZone="${currentUser.timeZone}"/>
                                  </span>
                                </div>
                              </div>
                              <div class="raw_content_row">
                                <div class="raw_contents_title">
                                  <span><spring:message code="ui.system.health.description"/>:</span>
                                </div>
                                <div class="raw_contents_value">
                                  <span>
                                    <spring:message code="${item.value.description}" />
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
          var systemHealthListLen = "<c:out value="${systemHealthListLen}"/>";
        </script>
        <div class="widget_panelnext">
          <div class="widget_navnextbox">
              <a class="widget_navnext_buttons prev nonactive" href="javascript:void(0);" id="click_previous"><spring:message code="label.previous.short"/></a>
                <a class="widget_navnext_buttons next" href="javascript:void(0);" id="click_next"><spring:message code="label.next"/></a>
            </div>
        </div>
    </div>
    
    <div class="widget_rightpanel" id="viewStatusDetailsDiv">
        <c:if test="${firstHealth != null}">
          <c:set var="health" value="${firstHealth}" scope="request"></c:set>
          <c:set var="isfirsthealth" value="true" scope="request"></c:set>
          <jsp:include page="details.jsp"></jsp:include>  
        </c:if>
    </div>
</div>

<div id="addNewStatusDiv" title='<spring:message code="ui.label.service.health.status.new" />' style="display: none;">

</div>

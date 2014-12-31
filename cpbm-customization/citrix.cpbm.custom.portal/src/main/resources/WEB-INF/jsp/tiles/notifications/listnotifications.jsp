<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/notifications.js"></script>
<jsp:include page="js_messages.jsp"></jsp:include>
<script type="text/javascript">
var notificationUrl = "<%=request.getContextPath() %>/portal/tenants/";
var tenantParam = "<c:out value="${tenant.param}"/>";
var totalpages = "<c:out value="${totalpages}"/>";
var currentPage = "<c:out value="${currentPage}"/>";
var perPageValue = "<c:out value="${perPage}"/>";
</script>

<!-- Starts body -->

		<!-- Body Header  -->
<div class="widget_box">
    <div class="widget_leftpanel">
        <div class="widget_titlebar">
          <h2><spring:message code="label.list.all"/></h2>
        </div>
        <div class="widget_searchpanel">
        <div class="widget_searchcontentarea" id="search_panel">         
          <span class="label"><spring:message code="label.filter.by"/>:</span>
            <select id="selectedDatefilter" class="select" onchange="filterNotifications();">
              <c:forEach items="${filtersMap}" var="filter">
                <c:choose>
                  <c:when test="${filterBy==filter.key}">
                    <c:set var="selected" value="selected" />
                  </c:when>
                  <c:otherwise>
                    <c:set var="selected" value="" />
                  </c:otherwise>
                </c:choose>
                <option value="<c:out value="${filter.key}"/>" <c:out value="${selected}"/>><c:out value="${filter.value}" /></option>
              </c:forEach>
            </select>
          </div>
        </div>
        <div class="widget_navigation">
          <ul class="widget_navigationlist" id="grid_row_container">
              <c:choose>
                <c:when test="${empty notificationsList || notificationsList == null}">
                  <c:set var="notificationListLen" value="0"/>
                  <!--look when there is no list starts here-->
                  <li class="widget_navigationlist nonlist" id="non_list">
                      <span class="navicon notifications"></span>
                        <div class="widget_navtitlebox">
                          <span class="newlist">
                            <spring:message var="notificationsMsg" code="ui.label.emptylist.notifications" ></spring:message>
                            <spring:message code="ui.label.emptylist.notavailable" arguments="${notificationsMsg}" htmlEscape="false"/>
                          </span>
                        </div>
                        
                  </li>
                  <!--look when there is no list ends here-->
                </c:when>
                <c:otherwise>                            
                  <c:forEach var="notification" items="${notificationsList}" varStatus="status">
                      <c:set var="notificationListLen" value="${notificationListLen+1}"/>
                      <c:choose>
                        <c:when test="${status.index == 0}">
                          <c:set var="firstNotification" value="${notification}"/>
                          <c:set var="selected" value="selected"/>
                          <c:set var="active" value="active"/>
                        </c:when>
                        <c:otherwise>
                            <c:set var="selected" value=""/>
                            <c:set var="active" value=""/>
                        </c:otherwise>
                      </c:choose> 
                      <li class="<c:out value="widget_navigationlist ${selected} ${active} "/>" id="row<fmt:formatDate value="${notification.generatedAt}" pattern="yyyy-MM-dd HH:mm:ss" timeZone="${currentUser.timeZone}"/>" onclick="viewNotification(this);" onmouseover="onNotificationMouseover(this);" onmouseout="onNotificationMouseout(this);">
                        <span id="nav_icon" class="navicon notifications"></span>
                        <div class="widget_navtitlebox <c:out value="db_gridbox_rows"/>">
                          <span class="title">
                            <spring:message code="dateonly.format" var="date_format" />
                            <fmt:formatDate value="${notification.generatedAt}" pattern="${date_format}" timeZone="${currentUser.timeZone}"/>
                          </span>
                          <span class="subtitle">
                            <spring:message code="notification.last.event" />
                            <c:set var="time_format" value="HH:mm:ss" />
                            <fmt:formatDate value="${notification.generatedAt}" pattern="${time_format}" timeZone="${currentUser.timeZone}"/>
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
                                  <spring:message code="notification.last.event" />
                                  </span>
                                </div>
                                <div class="raw_contents_value">
                                  <span>
                                    <c:set var="time_format" value="HH:mm:ss" />
                                    <fmt:formatDate value="${notification.generatedAt}" pattern="${time_format}" timeZone="${currentUser.timeZone}"/>
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
          var notificationListLen = "<c:out value="${notificationListLen}"/>";
        </script>
        <div class="widget_panelnext">
          <div class="widget_navnextbox">
              <a class="widget_navnext_buttons prev nonactive" href="javascript:void(0);" id="click_previous"><spring:message code="label.previous.short"/></a>
                <a class="widget_navnext_buttons next" href="javascript:void(0);" id="click_next"><spring:message code="label.next"/></a>
            </div>
        </div>
    </div>
    <div class="widget_rightpanel" id="viewnotificationDiv">
        <c:if test="${firstNotification != null}">
          <script>
            var li_id = 'row<fmt:formatDate value="${firstNotification.generatedAt}" pattern="yyyy-MM-dd HH:mm:ss" timeZone="${currentUser.timeZone}"/>';
            viewFirstNotification(li_id);
          </script>  
        </c:if>
        
    </div>
</div>

<div id="managenotificationDiv" title='<spring:message code="notification.delivery.options" />' style="display: none;">

</div>


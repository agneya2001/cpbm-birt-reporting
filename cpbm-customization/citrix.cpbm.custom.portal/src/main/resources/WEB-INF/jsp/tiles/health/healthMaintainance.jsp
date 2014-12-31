<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/healthmaintainance.js"></script>
<jsp:include page="js_messages.jsp"></jsp:include>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/timepicker.js"></script>
<c:if test="${empty currentLocale && pageContext.request.locale.language ne 'en'}">
  <script type="text/javascript" src='<%=request.getContextPath()%>/js/i18n/timepicker/jquery-ui-timepicker-<c:out value="${pageContext.request.locale.language}"/>.js'></script>
  <c:if test="${not empty pageContext.request.locale.country}">
    <script type="text/javascript" src='<%=request.getContextPath()%>/js/i18n/timepicker/jquery-ui-timepicker-<c:out value="${pageContext.request.locale.language}"/>-<c:out value="${pageContext.request.locale.country}"/>.js'></script>
  </c:if>
</c:if>
<c:if test="${not empty currentLocale && currentLocale.language ne 'en'}">
  <script type="text/javascript" src='<%=request.getContextPath()%>/js/i18n/timepicker/jquery-ui-timepicker-<c:out value="${currentLocale.language}"/>.js'></script>
  <c:if test="${not empty currentLocale.country}">
    <script type="text/javascript" src='<%=request.getContextPath()%>/js/i18n/timepicker/jquery-ui-timepicker-<c:out value="${currentLocale.language}"/>-<c:out value="${currentLocale.country}"/>.js'></script>
  </c:if>
</c:if>
<script type="text/javascript">

  var ops = false;

  <sec:authorize access="hasRole('ROLE_OPS_COMMUNICATION')">

  ops = true;

  </sec:authorize>

  var healthUrl = "<%=request.getContextPath() %>/portal/health/";

  var totalpages = "<c:out value="${totalpages}"/>";
  var currentPage = "<c:out value="${currentPage}"/>";
  var perPageValue = "<c:out value="${perPage}"/>";
  
</script>

<style>
	/* css for timepicker */
	.ui-timepicker-div .ui-widget-header { margin-bottom: 8px;}
	.ui-timepicker-div dl { text-align: left; }
	.ui-timepicker-div dl dt { height: 25px; margin-bottom: -25px; }
	.ui-timepicker-div dl dd { margin: 0 10px 10px 65px; }
	.ui-timepicker-div td { font-size: 90%; }
	.ui-tpicker-grid-label { background: none; border: none; margin: 0; padding: 0; }
</style>

<div class="widget_box" id="health_maintainance_div">
  <div class="widget_leftpanel">
      <div class="widget_titlebar">
        <h2 class="wide"><spring:message code="label.list.all"/></h2>
        <sec:authorize access="hasRole('ROLE_OPS_COMMUNICATION')">
          <a class="widget_addbutton wide" href="javascript:void(0);" onclick="addSchedMaintenanceGet()" class="addNewStatus"><spring:message code="ui.system.health.addmaintenance"/></a>
        </sec:authorize>
      </div>
      <div class="widget_searchpanel">
          <div class="widget_searchcontentarea">
          </div>
        </div>
      <div class="widget_navigation">
        <ul class="widget_navigationlist" id="grid_row_container2">
          <c:choose>
            <c:when test="${empty maintenance}">
              <c:set var="maintainanceListLen" value="0"/>
              <!--look when there is no list starts here-->
              <li class="widget_navigationlist nonlist" id="non_list">
                  <span class="navicon maintenance"></span>
                    <div class="widget_navtitlebox">
                      <span class="newlist">
                        <spring:message code="ui.system.health.no.maintenance"/>
                      </span>
                    </div>
                    
              </li>
              <!--look when there is no list ends here-->
            </c:when>
            <c:otherwise>
              <c:forEach items="${maintenance}" var="item" varStatus="status">
                <c:set var="maintainanceListLen" value="${maintainanceListLen+1}"/>
                <c:choose>
                  <c:when test="${status.index == 0}">
                      <c:set var="firstMaintainance" value="${item}"/>
                      <c:set var="selected" value="selected"/>
                      <c:set var="active" value="active"/>
                  </c:when>
                  <c:otherwise>
                      <c:set var="selected" value=""/>
                      <c:set var="active" value=""/>
                  </c:otherwise>
                </c:choose>
                
                <li class="<c:out value="widget_navigationlist ${selected} ${active} "/>" id="smrow<c:out value="${item.id}"/>" onclick="viewMaintainanceDetails(this);" onmouseover="onHealthMouseover(this);" onmouseout="onHealthMouseout(this);">
                        <span id="nav_icon" class="navicon maintenance"></span>
                        
                        <div class="widget_navtitlebox <c:out value="db_gridbox_rows"/>">
                          <span class="title">
                            <c:out value="${item.subject}" />
                          </span>
                          <span class="subtitle">
                            <spring:message code="date.format" var="date_format"/>
                            <fmt:formatDate value="${item.plannedStart}" pattern="${date_format}" timeZone="${currentUser.timeZone}"/>
                          </span>
                        </div>
                        <!--Info popover starts here-->
                        <div class="widget_info_popover" id="info_bubble" style="display:none">
                          <div class="popover_wrapper" >
                          <div class="popover_shadow"></div>
                          <div class="popover_contents">
                            <div class="raw_contents">
                              <div class="raw_content_row">
                                <div class="raw_contents_title">
                                  <span><spring:message code="ui.system.health.maintenance.subject"/>:</span>
                                </div>
                                <div class="raw_contents_value">
                                  <span>
                                    <c:out value="${item.subject}" />
                                  </span>
                                </div>
                              </div>
                              <div class="raw_content_row">
                                <div class="raw_contents_title">
                                  <span><spring:message code="ui.system.health.maintenance.description"/>:</span>
                                </div>
                                <div class="raw_contents_value">
                                  <span>
                                    <c:out value="${item.description}" />
                                  </span>
                                </div>
                              </div>
                              <div class="raw_content_row">
                                <div class="raw_contents_title">
                                  <span><spring:message code="ui.system.health.maintenance.start"/>:</span>
                                </div>
                                <div class="raw_contents_value">
                                  <span>
                                    <spring:message code="date.format" var="date_format"/>
                                    <fmt:formatDate value="${item.plannedStart}" pattern="${date_format}" timeZone="${currentUser.timeZone}"/>
                                  </span>
                                </div>
                              </div>
                              <div class="raw_content_row">
                                <div class="raw_contents_title">
                                  <span><spring:message code="ui.system.health.maintenance.end"/>:</span>
                                </div>
                                <div class="raw_contents_value">
                                  <span>
                                    <spring:message code="date.format" var="date_format"/>
                                    <fmt:formatDate value="${item.plannedEnd}" pattern="${date_format}" timeZone="${currentUser.timeZone}"/>
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
          var maintainanceListLen = "<c:out value="${maintainanceListLen}"/>";
        </script>
        <div class="widget_panelnext">
          <div class="widget_navnextbox">
              <a class="widget_navnext_buttons prev nonactive" href="javascript:void(0);" id="click_previous"><spring:message code="label.previous.short"/></a>
                <a class="widget_navnext_buttons next" href="javascript:void(0);" id="click_next"><spring:message code="label.next"/></a>
            </div>
        </div>
  </div>
  <input type="hidden" id="selectedZone" value="<c:out value="${selectedZone.id}"/>" />
  <div class="widget_rightpanel" id="viewScheduledMaintainanceDiv">
      <c:if test="${firstMaintainance != null}">
          <c:set var="item" value="${firstMaintainance}" scope="request"></c:set>
          <script>
          var li_id = "<c:out value="${item.id}"/>";
          viewFirstMaintainanceDetails(li_id);
          </script>
          
      </c:if>
      <jsp:include page="maintenanceView.jsp"></jsp:include>
  </div>
</div>

<div id="addNewStatusDiv" title='<spring:message code="ui.label.schedule.maintenance.new" />' style="display: none;">

</div>

<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>


<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<script type="text/javascript">
	var healthUrl = "<%=request.getContextPath() %>/portal/health";
	var cloudServiceConsoleUrl = "<%=request.getContextPath() %>/portal/users/cloud_login";
	var fetchTicketCount = false;
	var getTopProducts=false;
	
	var dictionary = { 
	    dropdownAllPods: '<spring:message javaScriptEscape="true" code="home.pod.dropdown.all.pods"/>',
      zone_name: '<spring:message javaScriptEscape="true" code="label.zone"/>',
      
      label_products:'<spring:message javaScriptEscape="true" code="page.level1.products"/>',
      label_bundles:'<spring:message javaScriptEscape="true" code="page.level2.bundles"/>',
      getTopProductsFailure:'<spring:message javaScriptEscape="true" code="message.get.top.products.failure"/>',
      getTopBundlesFailure:'<spring:message javaScriptEscape="true" code="message.get.top.bundles.failure"/>'
      
	};
	
</script>
<sec:authorize access="hasAnyRole('ROLE_REPORTING_ADMIN')">
    <script type="text/javascript">
        getTopProducts=true;
    </script>
</sec:authorize>
<sec:authorize access="hasRole('ROLE_TICKET_MANAGEMENT')">
    <script type="text/javascript">
        <c:if test="${ticketCapabilities == 'CRUD'}">
            fetchTicketCount = true;
        </c:if>
    </script>
</sec:authorize>
 <c:if test="${effectiveTenant.state == 'ACTIVE' && userHasCloudServiceAccount}">
     <script type="text/javascript">
         var getServiceInstanceHealth = true;
     </script>
 </c:if>


<script type="text/javascript" src="<%=request.getContextPath() %>/js/jquery.cookies.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/jquery.easing.1.3.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/fusion/charts/FusionCharts.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/fusion/charts/FusionCharts.jqueryplugin.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/jquery.flipCounter.1.2.js"></script>

<script type="text/javascript" src="<%=request.getContextPath() %>/js/home_common.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/home_service.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/chart.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/tasks.js"></script>
<div style="clear:both"></div>

    <c:if test="${userHasCloudServiceAccount}">
    <div class="sliding_statswrapper">
        <jsp:include page="/WEB-INF/jsp/tiles/main/serviceCategoryList.jsp"/>        
        <div id="home_items_view" class="slider_gridwrapper">
        </div>
    </div>
    </c:if>

    <div class="maincontent_horizontalpanels">
      <div class="db_gridverticalpanel">
      <c:if test="${effectiveTenant.state == 'ACTIVE' && userHasCloudServiceAccount}">
        <div class="db_statsbox default">
          <div class="db_statsbox title">
            <h2><spring:message code="ui.home.page.title.service.health"/></h2>
            
            
                     
          </div>
          <div class="db_statsbox contentarea" >
               <div id="serviceHealthChart" style="min-height:30px;">
               </div>
          </div>
        </div>
        </c:if>
        <div class="db_statsbox default">
          <div class="db_statsbox title">
            <h2><spring:message code="ui.home_service.page.title.customer.summary" htmlEscape="false"/></h2>
            <div class="dbboxes_tabswrapper">
              <ul>
                <li class="first current" id="newRegTab"><a class="current" href="#"><spring:message code="ui.home_service.page.title.new.registration"/></a></li>
                <li class="last" id="custRankTab"><a href="#"><spring:message code="ui.home_service.page.title.top.customer"/></a></li>
              </ul>
            </div>
          </div>
          <div class="db_statsbox contentarea">
              <div id="newRegChart" >
                <c:set var="report" value="${reportFusionNR}" scope="request"/>
                <jsp:include page="../reports/genericFusionChart.jsp" ></jsp:include> 
                <sec:authorize access="hasRole('ROLE_REPORTING_ADMIN')">
               <div class="db_statsbox_footerlinksbox">
                 <p><a href="<%=request.getContextPath() %>/portal/reports/new_registrations"><spring:message code="ui.home_service.page.title.complete.report"/></a></p>
               </div>
              </sec:authorize>
              </div>
          
              <div id="custRankChart" style="display:none;">
                <c:set var="report" value="${reportFusionCR}" scope="request"/>
                <jsp:include page="../reports/genericFusionChart.jsp" ></jsp:include> 
                <sec:authorize access="hasRole('ROLE_REPORTING_ADMIN')">
                <div class="db_statsbox_footerlinksbox">
                  <p><a href="<%=request.getContextPath() %>/portal/reports/customer_rank"><spring:message code="ui.home_service.page.title.complete.report"/></a></p>
                </div>
              </sec:authorize>
              </div>
          </div>
          
          
        </div>
        <c:if test="${ticketCapabilities == 'CRUD'}">
            <sec:authorize access="hasRole('ROLE_TICKET_MANAGEMENT')">
	          <div class="db_statsbox default">
	            <div class="db_statsbox title">
	              <h2><spring:message code="ui.home.page.title.tickets"/></h2>
	            </div>
              <div id="top_message_panel" class="alert alert-success" style="float:left;width:390px;word-wrap:break-word;display:none;"><p id="msg"></p></div>
	           <div class="db_statsbox contentarea">
	                <div id="ticketsCountChart">
	                  <div id="spinnerDiv" class="spinnerDiv" style="margin: 70px 0 0 200px;"> </div>
	                </div>
	                <div class="db_statsbox_footerlinksbox">
                      <spring:url value="/portal/support/tickets" var="view_tickets_path" htmlEscape="false">
                        <spring:param name="tenant"><c:out value="${tenant.param}"/></spring:param>
                      </spring:url> 
                    <p><a href="<c:out value="${view_tickets_path}"/>"><spring:message code="ui.home.page.title.view.all.tickets"/></a></p>
                    <c:if test="${!effectiveTenant.owner.profile.operationsProfile}">
	                    <sec:authorize access="hasAnyRole('ROLE_USER_TICKET_MANAGEMENT', 'ROLE_TICKET_MANAGEMENT')">
	                     <p>|</p> <p><a href="<c:out value="${view_tickets_path}&showNewTicket=1"/>"><spring:message code="ui.home.page.title.submit.ticket"/></a></p>
	                    </sec:authorize>
                    </c:if>
                    
                    </div>
	            </div>
	          </div>
          </sec:authorize>
        </c:if>
        
        
      
      </div>
      <div class="db_gridverticalpanel right">
        <tiles:insertDefinition name="home.task.widget"/>
        <div class="db_statsbox default">
         <div class="db_statsbox title">
            <h2><spring:message code="ui.home.page.title.notifications"/></h2>
          </div>
          <div class="db_statsbox contentarea">
              <div class="db_box_timelinewrapper">
                  <span class="timestamp">
                    <spring:message code="label.today"/>
                  </span>                          
              </div>
               <c:choose>
                    <c:when test="${empty alerts_for_today}">
                       <div id="non_item_list" class="db_flipcounter_liststyle">
                          <div class="dashboard_noitem_messagebox">
                              <span class="non_list"><spring:message code="message.no.data.to.show"/></span> 
                          </div>
                        </div>
                    </c:when>
                    <c:otherwise>
		          		<div class="db_liststyle">
		                	<spring:message code="date.format" var="date_format"/>
		                  <ul>
		                  
		                    <c:forEach items="${alerts_for_today}" var="alertObj" varStatus="status" end="4">
			                    <c:choose>
			                      <c:when test="${not empty alertObj.severity}">
			                        <c:set var="level" value="${alertObj.severity}"/>
			                      </c:when>
			                      <c:otherwise>
			                        <c:set var="level" value="INFORMATION"/>
			                      </c:otherwise>
			                    </c:choose>
			                    <c:choose>
			                      <c:when test="${not empty alertObj.category}">
			                        <c:set var="category" value="${alertObj.category}"/>
			                      </c:when>
			                      <c:otherwise>
			                        <c:set var="category" value="SYSTEM"/>
			                      </c:otherwise>
			                    </c:choose>
		                    	<li>
		                        	<div class="db_notificationwrapper">
		                            <span class="title ${level}"><spring:message code="ui.home.notification.${category}.${level}"/></span>
		                            <c:choose>
		                              <c:when test="${alertObj.useMessageKey eq true}">
		                               <span class="descp ellipsis" title="<spring:message code="${alertObj.message}" arguments="${alertObj.messageArguments}"/>">
		                                <spring:message code="${alertObj.message}" arguments="${alertObj.messageArguments}"/>: <fmt:formatDate value="${alertObj.generatedAt}" pattern="${date_format}" timeZone="${currentUser.timeZone}"/></span>
		                              </c:when>
		                            <c:otherwise>
		                                <span class="descp ellipsis" title="<c:out value="${alertObj.message}"/>"><c:out value="${alertObj.message}"/>: <fmt:formatDate value="${alertObj.generatedAt}" pattern="${date_format}" timeZone="${currentUser.timeZone}"/></span>
		                            </c:otherwise>
		                            </c:choose>
		                          </div>
		                            <span class="icon notification ${level}" ref="today" onclick="go_to_notifications(this);"></span>
		                        </li>
		                      </c:forEach>
		                        
		                    </ul>
		                    <input type="hidden" id="isOwner" name="isOwner" value="<c:out value="${isOwner}"/>"/>
		                    <input type="hidden" id="accountState" name="accountState" value="<c:out value="${effectiveTenant.state}"/>"/> 
		                    <input type="hidden" name="tenantParam" id="tenantParam" value="<c:out value="${tenant.param}"/>">  
		                </div>
		            </c:otherwise>
                </c:choose>
              
              <div class="db_box_timelinewrapper">
                  <span class="timestamp">
                    <spring:message code="label.yesterday"/>
                  </span>                          
              </div>
              <c:choose>
				    <c:when test="${empty alerts_for_yesterday}">
				       <div id="non_item_list" class="db_flipcounter_liststyle">
		                  <div class="dashboard_noitem_messagebox">
		                      <span class="non_list"><spring:message code="message.no.data.to.show"/></span> 
		                  </div>
		                </div>
				    </c:when>
				    <c:otherwise>
				       <div class="db_liststyle">
		                  <ul>
		                    <c:forEach items="${alerts_for_yesterday}" var="alertObj" varStatus="status" end="4">
		                        <c:choose>
                                  <c:when test="${not empty alertObj.severity}">
                                    <c:set var="level" value="${alertObj.severity}"/>
                                  </c:when>
                                  <c:otherwise>
                                    <c:set var="level" value="INFORMATION"/>
                                  </c:otherwise>
                                </c:choose>
                                <c:choose>
                                  <c:when test="${not empty alertObj.category}">
                                    <c:set var="category" value="${alertObj.category}"/>
                                  </c:when>
                                  <c:otherwise>
                                    <c:set var="category" value="SYSTEM"/>
                                  </c:otherwise>
                                </c:choose>
		                      <li>
		                          <div class="db_notificationwrapper">
		                            <span class="title ${level}"><spring:message code="ui.home.notification.${category}.${level}"/></span>
		                            <c:choose>
		                              <c:when test="${alertObj.useMessageKey eq true}">
		                                <span class="descp ellipsis" title="<spring:message code="${alertObj.message}" arguments="${alertObj.messageArguments}"/>">
		                                  <spring:message code="${alertObj.message}" arguments="${alertObj.messageArguments}"/>: <fmt:formatDate value="${alertObj.generatedAt}" pattern="${date_format}" timeZone="${currentUser.timeZone}"/></span>
		                              </c:when>
		                              <c:otherwise>
		                                  <span class="descp ellipsis" title="<c:out value="${alertObj.message}"/>"><c:out value="${alertObj.message}"/>: <fmt:formatDate value="${alertObj.generatedAt}" pattern="${date_format}" timeZone="${currentUser.timeZone}"/></span>
		                              </c:otherwise>
		                            </c:choose>
		                          </div>
		                          <span class="icon notification ${level}" ref="yesterday" onclick="go_to_notifications(this);"></span>
		                        </li>
		                      </c:forEach>
		                        
		                    </ul>
		                </div>
				    </c:otherwise>
				</c:choose>
				<div class="db_statsbox_footerlinksbox">
                  <spring:url value="/portal/tenants/notifications" var="all_notifications_path" htmlEscape="false">
                    <spring:param name="tenant"><c:out value="${tenant.param}"/></spring:param>
                  </spring:url> 
                  <spring:url value="/portal/users/${user.param}/myprofile" var="manage_notifications_path" htmlEscape="false">
                  </spring:url> 
                    <!-- TODO too coupled... use read permission on object in UI -->
                    <c:set var="userCRUD" value="F"></c:set>
                    <sec:authorize access="hasRole('ROLE_USER_CRUD')">
                      <c:set var="userCRUD" value="T"></c:set>
                    </sec:authorize>

                    <p><a href="<c:out value="${all_notifications_path}"/>"><spring:message code="ui.home.page.title.viewallnotifications"/></a><c:if test="${currentTenant.id != 1 || effectiveTenant.id == 1 || userCRUD == 'T'}"> |</c:if></p>
                    <c:if test="${currentTenant.id != 1 || effectiveTenant.id == 1 || userCRUD == 'T'}">
                      <p><a href="<c:out value="${manage_notifications_path}?&activeTab=notPref"/>"><spring:message code="ui.home.page.title.manage.notifications.preferences"/></a></p>
                    </c:if>
                    
                  </div>
            </div>
        </div>
        
      </div>
    </div>





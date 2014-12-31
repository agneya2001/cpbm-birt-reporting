<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/health.js"></script>
<jsp:include page="js_messages.jsp"></jsp:include>
<script type="text/javascript">
var healthUrl = "<%=request.getContextPath() %>/portal/health/";
</script>
<!-- Start View Product Details --> 

    <c:choose>
	<c:when test="${isfirsthealth == true}">
  
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
                        <span><spring:message code="ui.system.health.status"/></span>
                      </div>
                      <div class="widget_grid_description">
                        <span><spring:message code="system.health.${health.value}"/></span>
                      </div>
                   </div>
                      
                    <div class="widget_grid master even">
                      <div class="widget_grid_labels">
                        <span><spring:message code="ui.system.health.date"/></span>
                      </div>
                      <div class="widget_grid_description">
                        <span>  
                          <spring:message code="dateonly.format" var="dateonly_format" />
                          <fmt:formatDate value="${health.key}" pattern="${dateonly_format}" timeZone="${currentUser.timeZone}" />
                        </span>
                      </div>
                    </div>
                    
                    <div class="widget_grid master even">
                      <div class="widget_grid_labels">
                        <span><spring:message code="ui.system.health.description"/></span>
                      </div>  
                      <div class="widget_grid_description">
                        <span><spring:message code="${health.value.description}" /></span>
                      </div>
                    </div>
                    
                </div>
                <c:if test="${health.value=='NORMAL'}">
                  <div class="widget_masterbigicons servicehealth normal"></div>
                </c:if>
                <c:if test="${health.value=='ISSUE'}">
                  <div class="widget_masterbigicons servicehealth performanceissue"></div>
                </c:if> <c:if test="${health.value=='DOWN'}">
                  <div class="widget_masterbigicons servicehealth disruption"></div>
                </c:if>
                
            </div>
        </div>
        <div class="widget_browser_contentarea">
          <ul class="widgets_detailstab">
               <li class="widgets_detailstab active"><spring:message code="ui.system.health.status.history"/></li>
           </ul>
           
          <div class="widget_details_actionbox">
            <ul class="widget_detail_actionpanel"> 
              </ul>
          </div>
          <div class="widget_browsergrid_wrapper details" id="details_content">
            
            
            
            <c:forEach items="${dateStatusHistory}" var="entry" varStatus="dateStatus">
	            <fmt:formatDate value="${health.key}" pattern="MM/dd/yyyy HH:mm:ss" var="healthKey" />
	            <fmt:formatDate value="${entry.key}" pattern="MM/dd/yyyy HH:mm:ss" var="entryKey" />
	            <c:if test="${healthKey == entryKey}">
	              <c:set var="healthKeyFound" value="true" />
	            	<c:forEach items="${entry.value}" var="item" varStatus="status">
			            <c:choose>
			              <c:when test="${status.index % 2 == 0}">
			                  <c:set var="rowClass" value="odd"/>
			              </c:when>
			              <c:otherwise>
			                  <c:set var="rowClass" value="even"/>
			              </c:otherwise>
			            </c:choose>
			            <div class="<c:out value="widget_grid details  ${rowClass}"/>">
			              <div class="widget_grid_labels statushealth">
			                <span class="healthstatusbox">
			                  <c:if test="${item.notificationType=='RESOLUTION'}">
			                    <div class="details_comments_statusicons normal" style="margin:5px 0 0 5px; display:inline;"></div>
			                  </c:if> <c:if test="${item.notificationType=='ISSUE'}">
			                    <div class="details_comments_statusicons peformanceissue" style="margin:5px 0 0 5px; display:inline;"></div>
			                  </c:if> <c:if test="${item.notificationType=='DISRUPTION'}">
			                    <div class="details_comments_statusicons disruption" style="margin:5px 0 0 5px; display:inline;"></div>
			                  </c:if>
			                </span>
			              </div>
			              <div class="widget_grid_description">
			                  <span style="width:520px">
			                  <span class="status_date">
			                  <spring:message code="ui.label.service.health.date"/> : 
			                  <spring:message code="date.format" var="date_format"/>
			                  <fmt:formatDate value="${item.recordedOn}" pattern="${date_format}" timeZone="${currentUser.timeZone}" />
			                  </span><br/>
			                  <span class="status_subject" style="width:520px"><spring:message code="ui.label.service.health.subject"/> : <c:out value="${item.subject}"/></span>
			                  <span class="status_description" style="width:520px"><spring:message code="ui.label.service.health.description"/> : <c:out value="${item.description}"/></span>
			                  <sec:authorize access="hasRole('ROLE_OPS_COMMUNICATION')">
			                  </sec:authorize>
			                  </span>
			              </div>
			            </div>
		            </c:forEach>
	            </c:if>
            </c:forEach>
            <c:if test='${healthKeyFound != "true"}'>  
              <div class="widget_details_actionbox addlistbox" style="padding:5px;">
                
                  <span>
                    <fmt:formatDate var="localDate" value="${health.key}" pattern="${dateonly_format}" timeZone="${currentUser.timeZone}"/>
                    <spring:message code="ui.system.health.no.notifications" arguments="${localDate}"/>
                  </span>
                
              </div>
            </c:if>
            <div id="grid_content"> </div>
          </div>
        </div>
      </div>
    </c:when>
      <c:otherwise>
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
                        <span><spring:message code="ui.system.health.status"/></span>
                      </div>
                      <div class="widget_grid_description">
                        <span><spring:message code="system.health.${health}"/></span>
                      </div>
                    </div>
                      
                    <div class="widget_grid master even">
                      <div class="widget_grid_labels">
                        <span><spring:message code="ui.system.health.date"/></span>
                      </div>
                      <div class="widget_grid_description">
                        <span>  
                          <spring:message code="dateonly.format" var="dateonly_format"/>
                                <fmt:formatDate value="${date}"
                                pattern="${dateonly_format}" timeZone="${currentUser.timeZone}"/>
                        </span>
                      </div>
                    </div>
                    
                    <div class="widget_grid master even">
                      <div class="widget_grid_labels">
                        <span><spring:message code="ui.system.health.description"/></span>
                      </div>  
                      <div class="widget_grid_description">
                        <span><spring:message code="${health.description}" /></span>
                      </div>
                    </div>
                    
                </div>
                <c:if test="${health=='NORMAL'}">
                  <div class="widget_masterbigicons servicehealth normal"></div>
                </c:if>
                <c:if test="${health=='ISSUE'}">
                  <div class="widget_masterbigicons servicehealth performanceissue"></div>
                </c:if> <c:if test="${health=='DOWN'}">
                  <div class="widget_masterbigicons servicehealth disruption"></div>
                </c:if>
            </div>
        </div>
        <div class="widget_browser_contentarea">
          <ul class="widgets_detailstab">
               <li class="widgets_detailstab active"><spring:message code="ui.system.health.status.history"/></li>
           </ul>
           
          <div class="widget_details_actionbox">
            <ul class="widget_detail_actionpanel"> 
              </ul>
          </div>
          <div class="widget_browsergrid_wrapper details" id="details_content">
            
            <c:if test="${empty notifications}"   >  
              <div class="widget_grid details odd" style="padding:5px;">
                
                  <span>
                    <fmt:formatDate var="localDate" value="${date}" pattern="${dateonly_format}" timeZone="${currentUser.timeZone}"/>
                    <spring:message code="ui.system.health.no.notifications" arguments="${localDate}"/>
                  </span>
                
              </div>
            </c:if>
            <c:forEach items="${notifications}" var="item" varStatus="status">
            <c:choose>
              <c:when test="${status.index % 2 == 0}">
                  <c:set var="rowClass" value="odd"/>
              </c:when>
              <c:otherwise>
                  <c:set var="rowClass" value="even"/>
              </c:otherwise>
            </c:choose>
            <div class="<c:out value="widget_grid details  ${rowClass}"/>">
              <div class="widget_grid_labels statushealth">
                <span>
                  <c:if test="${item.notificationType=='RESOLUTION'}">
                    <div class="details_comments_statusicons normal" style="margin:5px 0 0 5px; display:inline;"></div>
                  </c:if> <c:if test="${item.notificationType=='ISSUE'}">
                    <div class="details_comments_statusicons peformanceissue" style="margin:5px 0 0 5px; display:inline;"></div>
                  </c:if> <c:if test="${item.notificationType=='DISRUPTION'}">
                    <div class="details_comments_statusicons disruption" style="margin:5px 0 0 5px; display:inline;"></div>
                  </c:if>
                </span>
              </div>
              <div class="widget_grid_description">
                   <span style="width:520px">
                   <span class="status_date">
                   <spring:message code="ui.label.service.health.date"/> : 
                   <spring:message code="date.format" var="date_format"/> 
                  <fmt:formatDate value="${item.recordedOn}" pattern="${date_format}" timeZone="${currentUser.timeZone}"/>
                  
                  </span><br />
                  <span class="status_subject" style="width:520px"><spring:message code="ui.label.service.health.subject"/> : <c:out value="${item.subject}"/></span><br>
                  <span class="status_description" style="width:520px"><spring:message code="ui.label.service.health.description"/> : <c:out value="${item.description}"/></span>
                  
                  <sec:authorize access="hasRole('ROLE_OPS_COMMUNICATION')">
                    
                  </sec:authorize>
                  </span>
              </div>
            </div>
            </c:forEach>
            <div id="grid_content"> </div>
          </div>
        </div>
      </div>     
      </c:otherwise>
    </c:choose>
<!-- End view Product Details -->
                    

<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

    
      
      <div class="widget_actionbar">
        <c:if test="${item != null}">
          <div class="widget_actionarea" id="top_actions">
            <div id="spinning_wheel" style="display:none">
              <div class="maindetails_footer_loadingpanel">
              </div>
              <div class="maindetails_footer_loadingbox first">
                <div class="maindetails_footer_loadingicon"></div>
                <p id="in_process_text"></p>
              </div>
            </div>
             <sec:authorize access="hasRole('ROLE_OPS_COMMUNICATION')">
              <div class="widget_moreactions action_menu_container" title="<spring:message code='manage'/>">
              <div class="widget_actionpopover" style="display:none;" id="action_menu">
                <div class="widget_actionpopover_top"></div>
                <div class="widget_actionpopover_mid">
                  <ul class="widget_actionpoplist">
                    <li class="editnetwork_link" id="<c:out value="edil${item.id}"/>" onclick="javascript:editSchedMaintenanceGet(this);"><spring:message code="label.edit"/></li>
                    <li class="deletenetwork_link" id="<c:out value="removl${item.id}"/>" onclick="javascript:removeMaintenanceSchdule(this);"><spring:message code="label.remove"/></li>
                  </ul> 
                </div>
                <div class="widget_actionpopover_bot"></div>
              </div>
            </div>
             <div class="widget_actionbutton" title="<spring:message code="ui.system.health.maintenance.edit"/>" style="display: block;" onclick="editSchedMaintenanceGet(this)" id="<c:out value="edit${item.id}"/>">
                <div class="widget_actionsicon edit"></div>
              </div>
              <div class="widget_actionbutton" title="<spring:message code="ui.system.health.maintenance.delete"/>" style="display: block;" onclick="removeMaintenanceSchdule(this)" id="<c:out value="remove${item.id}"/>">
                <div class="widget_actionsicon destroy"></div>
              </div>
            </sec:authorize>
          </div>
          </c:if>
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
                        <span><spring:message code="ui.system.health.maintenance.subject"/></span>
                      </div>
                      <div class="widget_grid_description">
                        <span><c:out value="${item.subject}" /></span>
                      </div>
                    </div>
                    
                    
                    <div class="widget_grid master even">
                      <div class="widget_grid_labels">
                        <span><spring:message code="ui.system.health.maintenance.start"/></span>
                      </div>  
                      <div class="widget_grid_description">
                        <span>
                          <spring:message code="date.format" var="date_format"/>
                          <fmt:formatDate value="${item.plannedStart}" pattern="${date_format}" timeZone="${currentUser.timeZone}"/>
                        </span>
                      </div>
                    </div>
                    
                    <div class="widget_grid master even">
                      <div class="widget_grid_labels">
                        <span><spring:message code="ui.system.health.maintenance.end"/></span>
                      </div>  
                      <div class="widget_grid_description">
                        <span>
                          <spring:message code="date.format" var="date_format"/>
                          <fmt:formatDate value="${item.plannedEnd}" pattern="${date_format}" timeZone="${currentUser.timeZone}"/>
                        </span>
                      </div>
                    </div>
                    
                </div>
                <div class="widget_masterbigicons maintenance"></div>
            </div>
        </div>
        
        <div class="widget_browser_contentarea">
         <ul class="widgets_detailstab">
             <li class="widgets_detailstab active"><spring:message code="label.details"/></li>
         </ul>
         
        <div class="widget_details_actionbox">
          <ul class="widget_detail_actionpanel"> 
            </ul>
        </div>
  
        <div class="widget_browsergrid_wrapper details" id="details_content">
            <div class="widget_grid details even first">
                   <div class="widget_grid_labels">
                       <span><spring:message code="ui.system.health.maintenance.description"/></span>
                   </div>
                   <div class="widget_grid_description" >
                       <span>
                            <c:out value="${item.description}" />
                      </span>
                   </div>
             </div>    
        </div>
      </div>
   
      </div> 

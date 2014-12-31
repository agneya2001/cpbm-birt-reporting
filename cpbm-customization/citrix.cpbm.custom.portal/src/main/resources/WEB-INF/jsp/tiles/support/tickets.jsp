<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>

<script type="text/javascript">
var sortType = "<c:out value="${sortType}"/>";
var sortColumn = "<c:out value="${sortColumn}"/>";
var showNewTicket = "<c:out value="${showNewTicket}"/>";

var dictionary = { 

  closeTicket : '<spring:message javaScriptEscape="true" code="ui.label.support.tickets.close.ticket"/>',
  closingTicket : '<spring:message javaScriptEscape="true" code="label.close.ticket"/>',
  lightboxCloseTicket : '<spring:message javaScriptEscape="true" code="js.confirm.tickets.close"/>',
    lightboxbuttoncancel: '<spring:message javaScriptEscape="true" code="label.cancel"/>',  
    lightboxbuttonconfirm: '<spring:message javaScriptEscape="true" code="label.confirm"/>',
    fetchingDetails: '<spring:message javaScriptEscape="true" code="message.ticket.fetch.details"/>'

}
</script>

<script type="text/javascript" src="<%=request.getContextPath() %>/js/tickets.js"></script>


<input type="hidden" id="tenantParam" value="<c:out value="${tenant.param}"/>"/>
<c:if test="${not ticketServiceConfigured}">
          <div class="subscribe_dialog_message_box error" style="margin-left:20px;">
            <p><spring:message code='no.cloud.service.account'/></p>
        </div>
</c:if>
<div class="widget_box">
    <div class="widget_leftpanel">
        <div class="widget_titlebar">
            <h2><spring:message code="label.list.all" /></h2>
            <sec:authorize access="hasRole('ROLE_USER_TICKET_MANAGEMENT')">  
            <a class="widget_addbutton" id="addticket" href="javascript:void(0);" onclick="showNewTicketDiv()"><spring:message code="label.add.new"/></a>
          </sec:authorize>
        </div>
        <div class="widget_searchpanel">
               <div class="widget_searchcontentarea" id="search_panel"> 
                    <span class="label"><spring:message code="label.filter.by"/>:</span> 
                    <select class="filterby select" id="ticketFilterByStatus" style="margin:0 0 0 5px;">                
                        <option value="All"><spring:message code="label.all"/></option>
                        <c:forEach items="${statuses}" var="ts" >
                        <c:set var="statusFilterValue" value=""/>
                        <c:if test="${ !empty statusFilter && statusFilter ne 'All' && statusFilter == ts}">
                        <c:set var="statusFilterValue" value="selected"/>
                        </c:if>
                        <option value="<c:out value="${ts}"/>" <c:out value="${statusFilterValue}"/>><spring:message code="ui.label.support.tickets.status.${ts.name}"/></option>
                        </c:forEach>
                    </select>
              </div>
        </div>
        
        <div class="widget_navigation">
            <ul class="widget_navigationlist" id="grid_row_container">
                <c:choose>
                <c:when test="${(empty tickets || tickets == null)}">
                <!--Empty list-->
                <li class="widget_navigationlist nonlist" id="non_list">
                    <span class="navicon ticket_new"></span>
                    <div class="widget_navtitlebox">
                        <span class="newlist"><spring:message code="message.no.tickets.available"/></span> 
                    </div>
                </li>   
                <!--Empty list-->
                </c:when>
                <c:otherwise>                            
                <c:forEach var="ticket" items="${tickets}" varStatus="status">
                  
                  
               <c:choose>
        <c:when test="${status.index == 0}">
          <c:set var="firstTicket" value="${ticket}" scope="request"/>
          <c:set var="selected" value="selected" />
        </c:when>
        <c:otherwise>
          <c:set var="selected" value="" />
        </c:otherwise>
      </c:choose>
                  
                
                <li class='<c:out value="widget_navigationlist ${selected} tickets"/>'  id="ticket<c:out value="${ticket.caseNumber}"/>" onclick="viewTicket(this)" onmouseover="showInfoBubble(this)" onmouseout="hideInfoBubble(this)">
                <c:choose>
                    <c:when test="${ticket.status.name == 'CLOSED'}">
                      <c:set var="status_icon" value="ticket_closed"/>                      
                    </c:when>
                    <c:when test="${ticket.status.name == 'WORKING'}">
                      <c:set var="status_icon" value="ticket_working"/>
                    </c:when>
                    <c:when test="${ticket.status.name == 'ESCALATED'}">
                      <c:set var="status_icon" value="ticket_escalated"/>                      
                    </c:when>
                    <c:when test="${ticket.status.name == 'NEW'}">
                      <c:set var="status_icon" value="ticket_new"/>
                    </c:when>
                    </c:choose>
                <span class="navicon ${status_icon}" id = "status_icon"></span>
                  
                  
                    <div class="widget_navtitlebox">
                        <span class="title" id="name_nav"><c:out value="${ticket.caseNumber}" /></span>
                        <span class="subtitle" id="displaytext_nav"><c:out value="${ticket.subject}" /></span>
                        
                    </div>
                    
                    <!--Info popover starts here-->
                    <div class="widget_info_popover" id="info_bubble" style="display:none">
                        <div class="popover_wrapper">
                            <div class="popover_shadow">
                            </div>
                            <div class="popover_contents">
                                <div class="raw_contents">
                                    <div class="raw_content_row">
                                        <div class="raw_contents_title">
                                            <span><spring:message code="ui.label.support.tickets.status"/>:</span>
                                        </div>
                                        <div class="raw_contents_value">
                                            <span id="info_status" />
                                            <spring:message code="ui.label.support.tickets.status.${ticket.status.name}" />
                                            </span>
                                        </div>
                                    </div>
                                    <div class="raw_content_row">
                                        <div class="raw_contents_title">
                                            <span><spring:message code="ui.label.support.tickets.customer"/>:</span>
                                        </div>
                                        <div class="raw_contents_value">
                                            <span id="info_customer" />
                                            <c:out value="${ticket.owner.tenant.accountId}" />
                                            </span>
                                        </div>
                                    </div>
                                    <div class="raw_content_row">
                                        <div class="raw_contents_title">
                                            <span><spring:message code="ui.label.support.tickets.created.by"/>:</span>
                                        </div>
                                        <div class="raw_contents_value">
                                            <span id="info_created_by" />
                                            <c:out value="${ticket.owner.username}" />
                                            </span>
                                        </div>
                                    </div>
                                    <div class="raw_content_row">
                                        <div class="raw_contents_title">
                                            <span><spring:message code="ui.label.support.tickets.created.at"/>:</span>
                                        </div>
                                        <div class="raw_contents_value">
                                            <span id="info_ticket_created_at">
                                                <spring:message code="date.format" var="date_format" /> 
                                                <fmt:formatDate value="${ticket.createdAt}" pattern="${date_format}" timeZone="${currentUser.timeZone}" />
                                            </span>
                                        </div>
                                    </div>
                                    
                                    
                                       <div class="raw_content_row">
                                        <div class="raw_contents_title">
                                            <span><spring:message code="ui.label.support.tickets.last.updated.at"/>:</span>
                                        </div>
                                        <div class="raw_contents_value">
                                            <span id="info_ticket_updated_at">
                                                <spring:message code="date.format" var="date_format" /> 
                                                 <fmt:formatDate value="${ticket.updatedAt}" pattern="${date_format}" timeZone="${currentUser.timeZone}"/> 
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
      <div class="widget_panelnext">
        <div class="widget_navnextbox">
            <c:choose>
              <c:when test="${current_page <= 1}">
                  <a class="widget_navnext_buttons prev nonactive" href="javascript:void(0);" id="click_previous"><spring:message code="label.previous.short"/></a>
              </c:when>
              <c:otherwise>
                  <a class="widget_navnext_buttons prev" href="javascript:void(0);" id="click_previous" onclick="previousClick()"><spring:message code="label.previous.short"/></a>
              </c:otherwise>
            </c:choose> 
            
            <c:choose>
              <c:when test="${enable_next == 'True'}">
              <a class="widget_navnext_buttons next" href="javascript:void(0);" id="click_next" onclick="nextClick()"><spring:message code="label.next"/></a>
          </c:when>
              <c:otherwise>
              <a class="widget_navnext_buttons next nonactive" href="javascript:void(0);" id="click_next" ><spring:message code="label.next"/></a>
                </c:otherwise>
            </c:choose> 
          </div>
      </div>                        
<input type="hidden" id="current_page"  value="<c:out value="${current_page}"/>"/>
<input type="hidden" id="default_page_size"  value="<c:out value="${defaultPageSize}"/>"/>
    </div>
    <div class="widget_rightpanel" id="viewTicketDiv">    
    <div class="widget_actionbar">
      <div class="widget_actionarea" id="top_actions">
        <div id="spinning_wheel" style="display:none;">
             <div class="widget_blackoverlay widget_rightpanel">
             </div>
             <div class="widget_loadingbox widget_rightpanel">
               <div class="widget_loaderbox">
                 <span class="bigloader"></span>
               </div>
               <div class="widget_loadertext">
                 <p id="in_process_text"><spring:message code="label.loading"/> &hellip;</p>
               </div>
             </div>
       </div>
        <div class="widget_moreactions action_menu_container" id="top_action_menu" title="<spring:message code="manage"/>">
            <!--Actions popover starts here-->
            <div class="widget_actionpopover" id="action_menu"  style="display:none;">
                <div class="widget_actionpopover_top">
                </div>
                <div class="widget_actionpopover_mid">                   
                </div>
                <div class="widget_actionpopover_bot">
                </div>
            </div>
            <!--Actions popover ends here-->
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
                        <span><spring:message code="ui.label.support.tickets.ticket.number"/></span>
                    </div>
                    <div class="widget_grid_description">
                        <span id="ticket_number"></span>
                    </div>
                </div>
                <div class="widget_grid master even">
                    <div class="widget_grid_labels">
                        <span><spring:message code="ui.label.support.tickets.title"/></span>
                    </div>
                    <div class="widget_grid_description">
                        <span id="ticket_title"></span>
                    </div>
                </div>
                <div class="widget_grid master even">
                    <div class="widget_grid_labels">
                        <span><spring:message code="ui.label.support.tickets.status"/></span>
                    </div>
                    <div class="widget_grid_description">
                        <span id="ticket_status"></span>
                    </div>
                </div>
            </div>
            <div class="widget_masterbigicons tickets">
            </div>
        </div>
    </div>
    <div class="widget_browser_contentarea">
        <ul class="widgets_detailstab">
            <li class="widgets_detailstab active"  id="tab_details"><spring:message code="label.details"/></li>
            <li class="widgets_detailstab nonactive" id="tab_comments"><spring:message code="ui.label.support.tickets.comments"/></li>
        </ul>
        <div class="widget_details_actionbox">
            <ul class="widget_detail_actionpanel">
            </ul>
        </div>
        <div class="widget_browsergrid_wrapper details" id="details_content" >
            <div class="widget_grid details even">
                <div class="widget_grid_labels">
                    <span><spring:message code="ui.label.support.tickets.description"/></span>
                </div>
                <div class="widget_grid_description">
                    <span id="ticket_description">                    
                    </span>
                </div>
            </div>
            <div class="widget_grid details odd">
                <div class="widget_grid_labels">
                    <span><spring:message code="ui.label.support.tickets.customer"/></span>
                </div>
                <div class="widget_grid_description">
                    <span id="ticket_customer">                      
                    </span>
                </div>
            </div>
            <div class="widget_grid details even">
                <div class="widget_grid_labels">
                    <span><spring:message code="ui.label.support.tickets.created.by"/></span>
                </div>
                <div class="widget_grid_description">
                    <span id="ticket_created_by"></span>
                </div>
            </div>
            <div class="widget_grid details odd">
                <div class="widget_grid_labels">
                    <span><spring:message code="ui.label.support.tickets.created.at"/></span>
                </div>
                <div class="widget_grid_description">
                    <span id="ticket_created_at"></span>
                </div>
            </div>
            <div class="widget_grid details even">
                <div class="widget_grid_labels">
                    <span><spring:message code="ui.label.support.tickets.last.updated.at"/></span>
                </div>
                <div class="widget_grid_description">
                    <span id="ticket_updated_at"></span>
                </div>
            </div>
        </div>
        <div class="widget_browsergrid_wrapper details" id="comments_content" style="display:none">
        </div>
    </div>
</div>
    </div>
<div id="addticketDiv" title='<spring:message code="ui.label.support.tickets.add.new.ticket"/>' style="display:none;">
</div>


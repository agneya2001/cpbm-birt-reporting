<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="csrf" uri="http://www.owasp.org/index.php/Category:OWASP_CSRFGuard_Project/Owasp.CsrfGuard.tld"%>
<script type="text/javascript">
$(document).ready(function() {
$("#createTicketCommentForm").validate( {
  // debug : true,
  
  rules : {
    "comment.comment" : {
      required : true
    }     
  },
  messages : {
    "comment.comment" : {
      required : i18n.errors.commentBody
    }     
  },
      errorPlacement: function(error, element) { 
        var name = element.attr('id');
        name =name.replace(".","\\.");      
        if(error.html() !=""){
          error.appendTo( "#"+name+"Error" );
        }
      }
});
});
</script>

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
                    <ul class="widget_actionpoplist">
                    <sec:authorize access="hasRole('ROLE_TICKET_MANAGEMENT')">
                        <c:set var="ticketAdmin" value="true"/>
                    </sec:authorize>
                    
                    <sec:authorize access="hasAnyRole('ROLE_TICKET_MANAGEMENT', 'ROLE_USER_TICKET_MANAGEMENT')">
                        <c:if test="${ticketAdmin || ticket.owner == currentUser}">
                            <li id="edit_ticket_link" ><spring:message code="ui.label.support.tickets.ticket.edit.details.header"/></li>
                        </c:if>  
                    </sec:authorize>  
                    
                    
                    <c:if test="${ticket.status.name != 'CLOSED'}">
                         <sec:authorize access="hasAnyRole('ROLE_TICKET_MANAGEMENT', 'ROLE_TENANT_TICKET_MANAGEMENT')">
                             <li class="close_ticket_link"><spring:message code="ui.label.support.tickets.close.ticket"/></li>
                         </sec:authorize>
                    </c:if>
                         
                          <li id="comment_ticket_link"><spring:message code="ui.label.issueCredit.comment"/></li>
                          <div id="ticketCommentDiv" class="dialog_formcontent wizard" title='<spring:message code="label.comment"/>' style="display:none;">
                                  <form:form commandName="ticketCommentForm" id="createTicketCommentForm"  cssClass="ajaxform" >
                                      <form:hidden path="comment.parentId"/>
                                      <ol>   
                                        <li style="margin:10px 0 0 10px;">
                                               <form:textarea cssClass="textarea" rows="3" cols="70" path="comment.comment" tabindex="31"></form:textarea>
                                                 <div class="main_addnew_formbox_errormsg" id="comment.commentBodyError" style="margin-left:-15px;"></div>
                                            </li>       
                                         </ol>
                                      <input type="hidden" name="ticketId" id="ticketId"  value="<c:out value="${ticket.caseNumber}"/>">
                                      <input type="hidden" name="tenant" id="tenant"  value="<c:out value="${tenant.param}"/>">
                                  </form:form>
                          </div>

                         <c:set var="noAction" value="true"/>  
                         <sec:authorize access="hasAnyRole('ROLE_TICKET_MANAGEMENT', 'ROLE_USER_TICKET_MANAGEMENT', 'ROLE_SPL_USER_TICKET_MANAGEMENT', 'ROLE_TENANT_TICKET_MANAGEMENT')">
                              <c:set var="noAction" value="false"/>  
                         </sec:authorize>    
                        <c:if test="${noAction}">
                              <li id="no_actions_available_volume" title='<spring:message code="label.no.actions.available"/>'><spring:message code="label.no.actions.available"/></li>
                        </c:if>
                        
                    </ul>
                </div>
                <div class="widget_actionpopover_bot">
                </div>
            </div>
            <!--Actions popover ends here-->
        </div>
    </div>
    <input type='hidden' id='case_number' value='<c:out value="${ticket.caseNumber}"/>'>
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
                        <span id="ticket_number"><c:out value="${ticket.caseNumber}" /></span>
                    </div>
                </div>
                <div class="widget_grid master even">
                    <div class="widget_grid_labels">
                        <span><spring:message code="ui.label.support.tickets.title"/></span>
                    </div>
                    <div class="widget_grid_description">
                        <span id="ticket_title" class="ellipsis" title="<c:out value="${ticket.subject}" />"><c:out value="${ticket.subject}" /></span>
                    </div>
                </div>
                <div class="widget_grid master even">
                    <div class="widget_grid_labels">
                        <span><spring:message code="ui.label.support.tickets.status"/></span>
                    </div>
                    <div class="widget_grid_description">
                        <span id="ticket_status"><spring:message code="ui.label.support.tickets.status.${ticket.status.name}" /></span>
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
                    <c:forEach items="${ticket.formattedDescription}" var="desc">
                        <c:out value="${desc}" escapeXml="true"/><br>
                    </c:forEach>
                    </span>
                </div>
            </div>
            <div class="widget_grid details odd">
                <div class="widget_grid_labels">
                    <span><spring:message code="ui.label.support.tickets.customer"/></span>
                </div>
                <div class="widget_grid_description">
                    <span id="ticket_customer">
                        <c:out value="${ticket.owner.tenant.name}" /> 
                    </span>
                </div>
            </div>
            <div class="widget_grid details even">
                <div class="widget_grid_labels">
                    <span><spring:message code="ui.label.support.tickets.created.by"/></span>
                </div>
                <div class="widget_grid_description">
                    <span id="ticket_created_by"><c:out value="${ticket.owner.username}" /></span>
                </div>
            </div>
            <div class="widget_grid details odd">
                <div class="widget_grid_labels">
                    <span><spring:message code="ui.label.support.tickets.created.at"/></span>
                </div>
                <div class="widget_grid_description">
                    <span id="ticket_created_at">
                    <spring:message code="date.format" var="date_format"/>
                    <fmt:formatDate value="${ticket.createdAt}" pattern="${date_format}" timeZone="${currentUser.timeZone}"/>         
                    </span>
                </div>
            </div>
            <div class="widget_grid details even">
                <div class="widget_grid_labels">
                    <span><spring:message code="ui.label.support.tickets.last.updated.at"/></span>
                </div>
                <div class="widget_grid_description">
                    <span id="ticket_updated_at">
                    <spring:message code="date.format" var="date_format"/>
                    <fmt:formatDate value="${ticket.updatedAt}" pattern="${date_format}" timeZone="${currentUser.timeZone}"/>  
                    </span>
                </div>
            </div>
        </div>
        <div class="widget_browsergrid_wrapper details" id="comments_content" style="display:none">
            <c:forEach var="comment" items="${ticketcomments}" varStatus="status">
                <div class="othercommentsbox">
                    <div class="details_commentslist_description">
                        <p class="ellipsis" style="width:610px;" title="${comment.formattedComment[1]}">
                            <c:forEach items="${comment.formattedComment}" var="commentLine" varStatus="commentLineCount">
                                <c:if test="${!commentLineCount.first}">
                                              <c:out value="${commentLine}" escapeXml="true"/><br>
                                </c:if>
                            </c:forEach>
                        </p>
                    </div>
                    <div class="details_commentslist_morebox">
                    </div>
                </div>
                <div class="details_commentslist_authorbox" style="float:left;">
                    <p style="color:#666; margin:7px 0 0 0;"><c:out value="${comment.formattedComment[0]}" escapeXml="true"/></p>
                </div>
                <div class="details_commentslist_authorbox">
                      <spring:message code="date.format" var="date_format"/>
                    <p style="margin:7px 0 0 0;"><spring:message code="ui.label.support.tickets.created.at"/>:
                     <fmt:formatDate value="${comment.createdAt}" pattern="${date_format}"  timeZone="${currentUser.timeZone}"/>
                    </p>
                </div>
            </c:forEach>
        </div>
        
    </div>
    
    
</div>


<sec:authorize access="hasAnyRole('ROLE_TICKET_MANAGEMENT', 'ROLE_USER_TICKET_MANAGEMENT')">
    <div id="editTicketDiv" title="<spring:message code="support.help.helpdesk.edit.ticket"/>" style="display:none">

        <form:form commandName="ticketForm" id="ticketForm" >      
         <!-- View ticket Details -->  
         <form:hidden path="ticket.uuid"/>                   
             <div class="main_details_contentbox" >
                  <div class="main_detailsistbox" >
                        <div class="db_gridbox_columns" style="width:20%;">
                            <div class="db_gridbox_celltitles details"><strong><spring:message code="ui.label.support.tickets.ticket.number"/></strong></div>
                         </div>
                         <div class="db_gridbox_columns" style="width:75%;">
                         <div class="db_gridbox_celltitles details">
                            <c:out value="${ticket.caseNumber}" />                   
                         </div>
                         </div>
                        <div class="db_gridbox_columns" style="width:20%;">
                            <div class="db_gridbox_celltitles details">
                            <strong><form:label path="ticket.status" ><spring:message code="ui.label.support.tickets.status"/></form:label></strong></div>
                         </div>
                         <div class="db_gridbox_columns" style="width:75%;">
                         <div class="db_gridbox_celltitles details">
                            <div class="red_compulsoryicon">*</div>
                            <form:select cssClass="select" path="ticket.status">
                                <c:forEach items="${statuses}" var="ts" >
                                  <c:if test="${ts == 'CLOSED'}"><!-- Close action not available for Normal Users  -->
                                    <sec:authorize access="hasAnyRole('ROLE_TICKET_MANAGEMENT', 'ROLE_TENANT_TICKET_MANAGEMENT')">
                                      <form:option value="${ts}"><spring:message code="ui.label.support.tickets.status.${ts}"/></form:option>
                                    </sec:authorize>
                                  </c:if>
                                  <c:if test="${ts != 'CLOSED'}">
                                    <form:option value="${ts}"><spring:message code="ui.label.support.tickets.status.${ts}"/></form:option>
                                  </c:if>
                                </c:forEach>
                            </form:select>  
                            <div class="main_addnew_formbox_errormsg" id="ticket.statusError"></div>                 
                         </div>
                         </div>
                        <div class="db_gridbox_columns" style="width:20%;">
                            <div class="db_gridbox_celltitles details"><strong><spring:message code="ui.label.support.tickets.created.at"/></strong></div>
                         </div>
                         <div class="db_gridbox_columns" style="width:75%;">
                         <div class="db_gridbox_celltitles details">
                         <spring:message code="date.format" var="date_format"/>
                            <fmt:formatDate value="${ticket.createdAt}" pattern="${date_format}"
                                    timeZone="${currentUser.timeZone}"/>                   
                         </div>
                         </div>
                        <div class="db_gridbox_columns" style="width:20%;">
                            <div class="db_gridbox_celltitles details"><strong><spring:message code="ui.label.support.tickets.last.updated.at"/></strong></div>
                         </div>
                         <div class="db_gridbox_columns" style="width:75%;">
                         <div class="db_gridbox_celltitles details">
                         <spring:message code="date.format" var="date_format"/>
                            <fmt:formatDate value="${ticket.updatedAt}" pattern="${date_format}"
                                    timeZone="${currentUser.timeZone}"/>                   
                         </div>
                         </div>
                        <div class="db_gridbox_columns" style="width:20%;">
                            <div class="db_gridbox_celltitles details">
                                <strong><form:label path="ticket.subject" ><spring:message code="ui.label.support.tickets.title"/></form:label></strong>
                            </div>
                         </div>
                         <div class="db_gridbox_columns" style="width:75%;">
                         <div class="db_gridbox_celltitles details">
                            <div class="red_compulsoryicon">*</div>
                            <form:input cssClass="text" cssStyle="width:470px" path="ticket.subject" tabindex="22" maxlength="255" />
                            <div class="main_addnew_formbox_errormsg" id="ticket.subjectError"></div>                  
                         </div>
                         </div>
                        <div class="db_gridbox_columns" style="width:20%;">
                            <div class="db_gridbox_celltitles details">
                                <strong><form:label path="ticket.description" ><spring:message code="ui.label.support.tickets.description"/></form:label></strong>
                            </div>
                         </div>
                         <div class="db_gridbox_columns" style="width:75%;">
                         <div class="db_gridbox_celltitles details">
                            <div class="red_compulsoryicon">*</div>
                            <form:textarea htmlEscape="false" cssClass="longtextbox" cssStyle="width:470px" rows="3" cols="20" path="ticket.description" tabindex="23"></form:textarea>
                                <div class="main_addnew_formbox_errormsg" id="ticket.descriptionError"></div>                    
                         </div>
                         </div>
                 </div>                            
             </div>
        <input type="hidden" name="<csrf:tokenname/>" value="<csrf:tokenvalue uri="portal/support/tickets/edit"/>"/>
            <input type="hidden"  id="statusFilter" name="statusFilter" value="<c:out value="${statusFilter}"/>"/>
            <input type="hidden" id="tenant" name="tenant" value="<c:out value="${tenant.param}"/>"/>
            <input type="hidden"  id="sortType" name="sortType" value="<c:out value="${sortType}"/>"/>
            <input type="hidden"  id="sortColumn" name="sortColumn" value="<c:out value="${sortColumn}"/>"/>
            <input type="hidden"  id="caseNumber" name="caseNumber" value="<c:out value="${ticket.caseNumber}"/>"/>
        </form:form>   
    </div>
</sec:authorize>

<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<script type="text/javascript">
$("#createTicketForm").validate( {
      // debug : true,
      
      rules : {
        "ticket.description" : {
          required : true
        },
        "ticket.subject" : {
          required : true,
          maxlength: 255
        },
        "ticket.status" : {
          required : true
        }
      },
      messages : {
        "ticket.description" : {
          required : i18n.errors.ticketDescription
        },
        "ticket.subject" : {
          required : i18n.errors.ticketTitle,
          maxlength: i18n.errors.ticketTitleMaxlength
        },
        "ticket.status" : {
          required : i18n.errors.ticketStatus
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

 </script>

<div class="dialog_formcontent" title='<spring:message code="support.help.helpdesk.create.new.ticket"/>'>
  
    <spring:url value="/portal/support/tickets/create" var="create_new_create_path" htmlEscape="false" />
    <form:form commandName="createTicketForm" id="createTicketForm" cssClass="ajaxform"  action="${create_new_create_path}" onsubmit="postNewTicket(event,this)">
      <input type="hidden" id="currentTenantId" value="<c:out value="${currentTenant.param}"/>"/>
      
          <ol>
            <li class="long">
              <form:label path="ticket.subject" style="width:70px;"><spring:message code="ui.label.support.tickets.title"/></form:label>
              <div class="mandatory_wrapper">
                <form:input cssClass="text" cssStyle="width:575px;margin-left:10px;padding:2px 0 0 2px;" path="ticket.subject"  tabindex="1" maxlength="255"/>
              </div>
              <div class="main_addnew_formbox_errormsg" id="ticket.subjectError" style="margin-left:85px;">
                <label for="ticket.subject" generated="true" class="error" style="width:200px;padding-left:6px;"></label>
              </div>
            </li>
            
            <li class="long" style="display:inline;">
              <form:label path="ticket.description" style="width:70px;"><spring:message code="ui.label.support.tickets.description"/></form:label>
              <div class="mandatory_wrapper">
                <form:textarea cssClass="longtextbox" cssStyle="width:575px;margin-left:10px;" rows="3" cols="20" path="ticket.description" tabindex="4"></form:textarea>
              </div>
              <div class="main_addnew_formbox_errormsg" id="ticket.descriptionError" style="margin-left:85px;">
                <label for="ticket.description" generated="true" class="error" style="width:200px;padding-left:6px;"></label>
              </div>
            </li>            
          </ol>
    </form:form>
  
</div>

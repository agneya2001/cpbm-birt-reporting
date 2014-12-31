<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/tickets.js"></script>

<script type="text/javascript">
    var dictionary={};
</script>
<!-- Body Header  -->
<div class="maintitlebox" style="width:98%;">
        <h1><spring:message code="tenants.tickets.title" /></h1>
</div>

<c:if test="${! empty message}">
    <c:choose>
		<c:when test="${status}">    
			<div id="top_message_panel" class="common_messagebox"><p id="msg"><c:out value="${message}"/></p></div>
		</c:when>
		<c:otherwise>
			<div class="main_addnew_formbox_myprofile_errormsg" id="error_success" ><p id="msg"><c:out value="${message}"/></div>
		</c:otherwise>
	</c:choose>
</c:if>
<div class="main_addnewbox" style="display:block;">
  <div class="main_addnewbox_contentbox">
    <div class="main_addnewbox_titlebox">
      <h2><spring:message code="support.help.helpdesk.create.new.ticket"/></h2>
      <p><span>*</span><spring:message code="message.mandatory.fields"/></p>
    </div>
    <spring:url value="/portal/support/tickets/create/email" var="create_new_create_path" htmlEscape="false">
    	<spring:param name="tenant"><c:out value="${effectiveTenant.param}"/></spring:param>
    </spring:url>
    <form:form commandName="ticketForm" action="${create_new_create_path}">
      <input type="hidden" id="currentTenantId" value="<c:out value="${currentTenant.param}"/>"/>
      <div class="main_addnew_formbox">
        <div class="main_addnew_formpanels " style="border:none;width: 98%;">
          <ol>
            <li class="long">
              <form:label path="ticket.subject" ><spring:message code="ui.label.support.tickets.title"/></form:label><div class="red_compulsoryicon">*</div>
              <form:input cssClass="text" cssStyle="width:750px" path="ticket.subject"  tabindex="1" maxlength="255"/>
              <div class="main_addnew_formbox_errormsg" id="ticket.subjectError"></div>
            </li>
            <div class="clearboth"></div>
            <li class="long" style="display:inline;">
              <form:label path="ticket.description" ><spring:message code="ui.label.support.tickets.description"/></form:label><div class="red_compulsoryicon">*</div>
              <form:textarea cssClass="longtextbox" cssStyle="width:750px" rows="3" cols="20" path="ticket.description" tabindex="6"></form:textarea>
              <div class="main_addnew_formbox_errormsg" id="ticket.descriptionError"></div>
            </li>            
          </ol>
        </div>
      </div>

      <div class="main_addnew_submitbuttonpanel">
        <div class="main_addnew_submitbuttonbox">
          <input tabindex="100"  class="commonbutton submitmsg" rel="<spring:message code="ui.label.support.tickets.creating.ticket"/>" type="submit" value="<spring:message code="label.submit"/>"/>
        </div>
      </div>
    </form:form>
  </div>
</div>

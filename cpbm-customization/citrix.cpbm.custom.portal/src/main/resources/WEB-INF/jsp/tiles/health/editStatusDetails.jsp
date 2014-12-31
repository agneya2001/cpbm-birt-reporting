<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/health.js"></script>
<jsp:include page="js_messages.jsp"></jsp:include>
<script type="text/javascript">
var healthUrl = "<%=request.getContextPath() %>/portal/health/";
</script>
<!-- Start View Product Details --> 
<div class="main_detailsbox"  style="width:600px" >
	<!-- Title -->
     <div class="main_details_titlebox">
          <h2><spring:message code="ui.system.health.edit.status"/></h2>
     </div>   
     <!-- View Product Details --> 
        <spring:url value="/portal/health/editStatus" var="edit_status_path" htmlEscape="false" /> 
        <form:form commandName="serviceNotificationForm" id="healthStatusForm" cssClass="ajaxform" action="${edit_status_path}" onsubmit="editStatus(event,this)">
     <div class="main_details_contentbox"  style="width:97%">
          <div class="main_detailsistbox"  style="width:100%">
          	                           
             <div class="db_gridbox_rows detailsodd">
             	<div class="db_gridbox_columns" style="width:20%;">
                 	<div class="db_gridbox_celltitles details"><strong><spring:message code="ui.system.health.status"/></strong></div>
                 </div>
                 <div class="db_gridbox_columns" style="width:75%;">
                 <div class="db_gridbox_celltitles details"  style="margin-left: 20px;">
				          <c:out value="${serviceNotification.notificationType}" />
                 </div>
                 </div>
			</div>
			
            <div class="db_gridbox_rows detailsodd">
             	<div class="db_gridbox_columns" style="width:20%;">
                 	<div class="db_gridbox_celltitles details"><strong><spring:message code="ui.system.health.date"/></strong></div>
                 </div>
                 <div class="db_gridbox_columns" style="width:75%;">
                 	<div class="db_gridbox_celltitles details" style="margin-left: 20px;">
                 		<fmt:formatDate value="${serviceNotification.recordedOn}"
										pattern="dd MMM yyyy hh:mm aa" timeZone="${currentUser.timeZone}"/>
                 	</div>
                 </div>
			 </div>

            <div class="db_gridbox_rows detailsodd">
             	<div class="db_gridbox_columns" style="width:20%;">
                 	<div class="db_gridbox_celltitles details"><strong><spring:message code="ui.system.health.zone"/></strong></div>
                 	<div class="red_compulsoryicon"></div>
                 </div>
                 <div class="db_gridbox_columns" style="width:75%;">
                 	<div class="db_gridbox_celltitles details" style="margin-left: 20px;">
                 		<c:out value="${serviceNotification.zone}" />
                 	</div>
                 </div>
			 </div>
			 
             <div class="db_gridbox_rows detailsodd">
            	<div class="db_gridbox_columns" style="width:20%;">
                	<div class="db_gridbox_celltitles details"><strong><spring:message code="ui.system.health.subject"/></strong></div>
                </div><div class="red_compulsoryicon">*</div>
                <div class="db_gridbox_columns" style="width:75%;">
                	<div class="db_gridbox_celltitles details">
                            <form:input cssClass="text" path="serviceNotification.subject" tabindex="3"></form:input>  
                             
                             <div class="main_addnew_formbox_errormsg" id="serviceNotification.subjectError"></div>
                	</div>
                </div>
			 </div>
			 
             <div class="db_gridbox_rows detailsodd">
             	<div class="db_gridbox_columns" style="width:20%;">
                 	<div class="db_gridbox_celltitles details"><strong><spring:message code="ui.system.health.description"/></strong></div>
                 </div><div class="red_compulsoryicon">*</div>
                 <div class="db_gridbox_columns" style="width:75%;">
                 	<div class="db_gridbox_celltitles details">
                            <form:textarea cssClass="longtextbox" rows="3" cols="20" path="serviceNotification.description" tabindex="6" cssStyle="width:220px" maxlength="1024"></form:textarea>     
                             <div class="main_addnew_formbox_errormsg" id="serviceNotification.descriptionError"></div>    
					</div>
                 </div>
			 </div>
			      
         </div>                            
     </div>
      <form:hidden path="notificationId" ></form:hidden>              
    <div class="maindetails_footerlinksbox">
      <sec:authorize access="hasRole('ROLE_OPS_COMMUNICATION')">
        <p><a href="#" id="editstatuscancel"><spring:message code="label.cancel"/></a></p>
        <p><input tabindex="100" class="commonbutton submitmsg" rel="<spring:message code="label.submitting"/>" type="submit" value="<spring:message code="label.submit"/>"/></p>
      </sec:authorize>
    </div>
    </form:form>
</div>
               
<!-- End view Product Details -->
                    

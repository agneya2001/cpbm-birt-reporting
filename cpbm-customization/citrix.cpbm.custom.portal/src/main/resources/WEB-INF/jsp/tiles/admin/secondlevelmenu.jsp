<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<c:if test="${isAdmin}">
	<div class="secondlevel_withsubmenu">
		<div class="secondlevel_breadcrumb_panel">
   		<div class="secondlevel_breadcrumbbox" style="border-right:none;">
        <p><spring:message code="ui.menu.admin.secondlevel.page.detail"/></p>       		
     	</div> 
        <div class="doc_help_link"><i class="fa fa-question-circle"></i></div>      
    </div>   
	     
	     <div class="secondlevel_menupanel">
	     	<sec:authorize access="hasRole('ROLE_CONFIGURATION_CRUD')">
	   			<a  class="secondlevel_menutabs auto_width <c:out value="${Configuiration}"/>" href="<%=request.getContextPath() %>/portal/admin/config/show_configuration">
	        		<spring:message code="page.level2.configuration"/>
	        	</a>
	   		</sec:authorize>
	   		<sec:authorize access="hasRole('ROLE_PROFILE_CRUD')">
	        	<a  class="secondlevel_menutabs auto_width <c:out value="${Profiles}"/>" href="<%=request.getContextPath() %>/portal/profiles/show">
	        		 <spring:message code="page.level2.profiles"/>
	        	</a>
	       </sec:authorize>
	       <sec:authorize access="hasRole('ROLE_USER_CRUD')">
            <a  class="secondlevel_menutabs auto_width <c:out value="${Users}"/>" href="<%=request.getContextPath() %>/portal/users/listusersforaccount">
		        		<spring:message code="page.level2.adminusers"/>
		        	</a>
	        </sec:authorize>
	       <sec:authorize access="hasAnyRole('ROLE_CONFIGURATION_CRUD')">
	        	<a  class="secondlevel_menutabs auto_width <c:out value="${BatchJobs}"/>"href="<%=request.getContextPath() %>/portal/admin/batch/status">
	        		<spring:message code="page.level2.batchjobs"/>
	        	</a>	
	        	</sec:authorize>
          <sec:authorize access="hasRole('ROLE_CONFIGURATION_CRUD')">
            <a  class="secondlevel_menutabs auto_width <c:out value="${AccountTypes}"/>"href="<%=request.getContextPath() %>/portal/admin/account_types/list">
              <spring:message code="page.level2.accounttypes"/>
            </a>  
            </sec:authorize>
            <sec:authorize access="hasRole('ROLE_CONFIGURATION_CRUD')">
            <a  class="secondlevel_menutabs auto_width <c:out value="${ContentTemplates}"/>"href="<%=request.getContextPath() %>/portal/admin/email_templates">
              <spring:message code="page.level2.contenttemplates"/>
            </a>  
            </sec:authorize>
		 </div>
     <div class="clearboth"></div>
 <tiles:insertDefinition name="warnings"></tiles:insertDefinition>  

	</div>
</c:if>

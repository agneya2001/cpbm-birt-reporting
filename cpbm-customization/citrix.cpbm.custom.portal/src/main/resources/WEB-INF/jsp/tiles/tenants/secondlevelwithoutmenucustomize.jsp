<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
 <%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
 <div class="secondlevel_withoutsubmenu">
  <div class="secondlevel_breadcrumb_panel">
     <div class="secondlevel_breadcrumbbox_tenant">
       <p title="${tenantLogoForm.tenant.name}"><c:out value="${tenantLogoForm.tenant.name}"/></p>
     </div> 
     <div class="doc_help_link"><i class="fa fa-question-circle"></i></div>    
  </div>
</div>

<jsp:include page="../shared/warnings.jsp"></jsp:include>  

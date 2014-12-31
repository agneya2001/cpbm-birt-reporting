<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<input type="hidden" id="selectedCategory" value="<c:out value="${selectedCategory}"/>" />

<div class="sliding_statswrapper">
  <jsp:include page="/WEB-INF/jsp/tiles/main/serviceCategoryList.jsp"/>        
</div>

<div class="thirdlevel_subsubmenu">
  <div class="thirdlevel_subsubmenu left"></div>
  <div class="thirdlevel_subsubmenu mid" style="position:inherit;">
    <div class="thirdlevel_subtab off" id="13_health_status_tab">
      <div class="thirdlevel_menuicons health"></div>
      <p><spring:message code="page.level3.healthstatus"/>
      </p>
    </div>
    <div class="thirdlevel_subtab off" id="13_health_scheduled_maintainence_tab">
      <div class="thirdlevel_menuicons maintenance"></div>
      <p><spring:message code="page.level3.scheduledmaintenance"/>
      </p>
    </div>
  </div> 
  <div class="thirdlevel_subsubmenu right"></div>
</div>
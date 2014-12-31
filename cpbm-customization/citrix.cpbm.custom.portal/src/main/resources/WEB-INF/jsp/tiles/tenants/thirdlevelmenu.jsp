<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<div class="thirdlevel_subsubmenu">
  <div class="thirdlevel_subsubmenu left"></div>
  <div class="thirdlevel_subsubmenu mid">
    <c:forEach items="${filtersMap}" var="filter">
      <div class="thirdlevel_subtab small off" id="l3_account_<c:out value="${filter.key}" />_tab">
        <div class="thirdlevel_menuicons
          <c:if test="${filter.key == 'All'}">
            all_accounts</c:if>
          <c:if test="${filter.key == '0'}">
            new_accounts</c:if>
          <c:if test="${filter.key == '1'}">
            active_accounts</c:if>
          <c:if test="${filter.key == '2'}">
            restricted_accounts</c:if> 
          <c:if test="${filter.key == '3'}">
            suspended_accounts</c:if>
          <c:if test="${filter.key == '4'}">
            terminated_accounts</c:if>"></div>
        <p><c:out value="${filter.value}" />
        </p>
      </div>
    </c:forEach>
  </div> 
  <div class="thirdlevel_subsubmenu right"></div>
</div>
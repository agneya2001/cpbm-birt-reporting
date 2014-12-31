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
      <div class="thirdlevel_subtab off" id="12_content_<c:out value="${filter.key}" />_tab">
        <div class="thirdlevel_menuicons
          <c:if test="${filter.key == '0'}">
            email</c:if>
          <c:if test="${filter.key == '1'}">
            includes</c:if>
          <c:if test="${filter.key == '2'}">
            styles</c:if>
          <c:if test="${filter.key == '3'}">
            invoices</c:if>
          <c:if test="${filter.key == '4'}">
            contents</c:if>"></div>
        <p><c:out value="${filter.value}" />
        </p>
      </div>
    </c:forEach>
  </div> 
  <div class="thirdlevel_subsubmenu right"></div>
</div>
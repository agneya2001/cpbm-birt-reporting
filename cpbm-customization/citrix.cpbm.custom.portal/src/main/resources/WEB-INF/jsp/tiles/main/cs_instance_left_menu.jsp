<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>  
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
    <div class="left_filtermenu">
      <span class="title"><spring:message  code="label.category"/></span>
      <div class="filterlist">
        <ul>
          <li>
            <span class="filters">
              <a href="javascript:void(0);" class="filters selected" id="All"><spring:message  code="label.all"/> (${countPerCategory['All']})</a>
            </span>
          </li>
          <c:forEach var="category" items="${categories}" varStatus="status">
            <c:if test="${!empty countPerCategory[category]}">
	            <li>
	              <span class="filters">
	                <a href="javascript:void(0);" class="filters" id="${category}"><spring:message  code="${category}.category.text"/> 
	                	(${countPerCategory[category]})
	                </a>
	              </span>
	            </li>
            </c:if>
          </c:forEach>
        </ul>
      </div>
      <sec:authorize access="hasRole('ROLE_PROFILE_CRUD')">
        <c:if test="${(effectiveTenant.id == currentTenant.id) }">
          <div class="left_filtermenu_info_box description" >
              <span class="icon active_subscription"></span>
              <spring:message  code="message.all.services.lhs.connector.marketplace.info" htmlEscape="false"/>
          </div>
        </c:if>
      </sec:authorize>
    </div>
    
    
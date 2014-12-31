<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<div class="full_width_box">
  <div class="page_heading">
    <h3>
      <spring:message code="label.catalog.utilityrate.title" />
    </h3>
    <span id="effective_date_box" class="sub_title"><spring:message code="label.catalog.utilityrate.effective.date"/> </span>
  </div>
  
  
  <div class="page_heading_description">
    <p>
    <c:choose>
      <c:when test="${not noResourceType}">
        <spring:message code="label.catalog.pay.as.you.go.description" />
      </c:when>
      <c:otherwise>
        <spring:message code="label.catalog.pay.as.you.go.description.without.resourcetype"/>
      </c:otherwise>
    </c:choose>
    </p>
  </div>
  
</div>
  <c:if test="${not viewChannelCatalog && not anonymousBrowsing && not noResourceType}">
<div class="full_width_box">
  <div class="utility_view_launch_form pull-right">
    <div class="go_button_class"> <label class="pull-left"> <spring:message code="label.launch" />:
    </label> <div class="pull-left" style="margin-right: 5px;"> <select id="utility_resource_selection_1" class="js_utility_resource_selection js_resourceType">
          <c:forEach items="${resourceTypes}" var="resourceTypeObject">
            <option value="<c:out value="${resourceTypeObject.resourceTypeName}"/>"
              <c:if test="${resourceTypeObject.resourceTypeName == resourceType}">selected="selected"</c:if>>
              <spring:message code="${service.serviceName}.ResourceType.${resourceTypeObject.resourceTypeName}.name" />
            </option>
          </c:forEach>
      </select>
    </div>
    <div class="btn-group">
      <button class="btn btn-primary js_pay_as_you_go_action"  data-hover="dropdown" data-delay="1000" data-close-others="false"><spring:message code="label.button.go" /></button>
    </div>
    
    </div>
  </div>
</div>
    </c:if>

<div id="utilityrate_table" class="full_width_box utility_table"></div>
  <c:if test="${not viewChannelCatalog && not anonymousBrowsing && not noResourceType}">
<div class="full_width_box" id="pay_as_you_go_action_container_2" style="display: none;">
  <div class="utility_view_launch_form pull-right">
    <div class="go_button_class"> <label class="pull-left"> <spring:message code="label.launch" />:
    </label> <span class="pull-left" style="margin-right: 5px;"> <select id="utility_resource_selection_2" class="js_utility_resource_selection js_resourceType">
          <c:forEach items="${resourceTypes}" var="resourceTypeObject">
            <option value="<c:out value="${resourceTypeObject.resourceTypeName}"/>"
              <c:if test="${resourceTypeObject.resourceTypeName == resourceType}">selected="selected"</c:if>>
              <spring:message code="${service.serviceName}.ResourceType.${resourceTypeObject.resourceTypeName}.name" />
            </option>
          </c:forEach>
      </select>
    </span>
    <div class="btn-group">
      <button class="btn btn-primary js_pay_as_you_go_action"  data-hover="dropdown" data-delay="1000" data-close-others="false"><spring:message code="label.button.go" /></button>
    </div>
    </div>
  </div>
</div>
  </c:if>
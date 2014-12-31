<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<div class="filterlistbox" style="float:left;width:100%;">


  <div class="bs_selectbox title user">
    <h2>
      <spring:message code="label.catalog.browse.by" />
    </h2>
  </div>
  
  <div id="filterBoxSelection" class="catalogfilter_filterlist templates" style="padding:0;">
    <c:forEach items="${resourceTypes}" var="resourceTypeObject">
      <li class="js_resource_type_default <c:if test="${resourceTypeObject.resourceTypeName == resourceType}">active</c:if>" id="<c:out value="${resourceTypeObject.resourceTypeName}"/>">
        <span title="<spring:message code="${service.serviceName}.ResourceType.${resourceTypeObject.resourceTypeName}.name"/>" class="catalog_rc_list ellipsis"><spring:message code="${service.serviceName}.ResourceType.${resourceTypeObject.resourceTypeName}.name" /></span>
      </li>
    </c:forEach>
    <li class="js_resource_type_default <c:if test="${serviceBundleResourceType == resourceType}">active</c:if>" id="${serviceBundleResourceType}">
        <span title="<spring:message code="catalog.resource.type.selection.service.bundle"/>" class="catalog_rc_list ellipsis"><spring:message code="catalog.resource.type.selection.service.bundle"/></span>
    </li>
  </div>
</div>
<c:if test="${resourceType ne serviceBundleResourceType}">
  <div id="filter_by_string_label" class="service_filters_label" style="display: none;">
    <span class="title"><spring:message code="filter.by" />:</span>
  </div>
  <div id="filters_SECTION_2"></div>
  <div id="components_SECTION_2">
  <div id="defaultUIRCsListingStep1" style="display:none;"></div>
  </div>
</c:if>
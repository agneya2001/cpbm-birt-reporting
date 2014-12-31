<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div class="clearboth"></div>
<div class="widget_actionpopover_top OSdropdown"></div>
<div class="widget_actionpopover_mid OSdropdown">
        <div id="atleastonefield" class="errormsg" style="color: #FF0000;">
        </div>
        <ul class="widget_actionpoplist advancesearchdropdown">
          
          <sec:authorize access="hasAnyRole('ROLE_ACCOUNT_ADMIN','ROLE_ACCOUNT_BILLING_ADMIN','ROLE_FINANCE_CRUD')">
            <li>
              <span class="label"><spring:message code="ui.label.user"/>:</span>
              <select id="dropdownfilter_users" class="select">
                <option id="ALL_USERS" value="ALL">
                  <spring:message code="label.all"/>
                </option>
                <c:forEach var="user" items="${searchForm.users}">
                  <option id="${user.uuid}" value="${user.uuid}" <c:if test="${user.uuid eq useruuid}">selected</c:if>>
                    <c:out value="${user.username}"></c:out>
                  </option>
                </c:forEach>
              </select>
            </li>
          </sec:authorize>
          
          <li>
            <span class="label"><spring:message code="ui.label.state"/>:</span>
            <select id="dropdownfilter_states" class="select">
              <option id="ALL_STATES" value="ALL">
                <spring:message code="label.all"/>
              </option>
              <c:forEach var="state" items="${searchForm.states}">
                <option id="${state.name}" value="${state.name}" <c:if test="${stateSelected eq state.name}">selected</c:if>>
                  <spring:message code="subscription.state.${fn:toLowerCase(state.name)}"/>
                </option>
              </c:forEach>
            </select>
          </li>
          
          <li>
            <span class="label"><spring:message code="ui.label.instance"/>:</span>
            <select id="dropdownfilter_instances" class="select">
              <option id="ALL_INSTANCES" value="ALL">
                <spring:message code="label.all"/>
              </option>
              <c:forEach var="instance" items="${searchForm.serviceInstances}">
                <option id="${instance.uuid}" serviceUuid="${instance.service.uuid}" value="${instance.uuid}" <c:if test="${instance.uuid eq instanceuuid}">selected</c:if>>
                  <c:out value="${instance.name}"></c:out>
                </option>
              </c:forEach>
            </select>
          </li>
          
          
          <c:choose>
          <c:when test="${utility_charges_view}">
          <li>
            <span class="label"><spring:message code="label.resource.type"/>:</span>
            <select id="dropdownfilter_resourceTypes" class="select">
              <!-- This will be filled with product bundles on selecting  -->
            </select>
            <select id="hidden_resourceTypes" style="display: none">
              <option id="ALL_RT" value="ALL">
                <spring:message code="label.all"/>
              </option>
              <c:forEach var="resourceType" items="${searchForm.resourceTypes}">
                <option id="${resourceType.id}" value="${resourceType.id}" serviceUuid="${resourceType.service.uuid}">
                  <spring:message code="${resourceType.service.serviceName}.ResourceType.${resourceType.resourceTypeName}.name"/>
                </option>
              </c:forEach>
            </select>
          </li>
          <input type="hidden" id="selectedResourceTypeID" value="${resourceTypeID}">
          </c:when>
          <c:otherwise>
          <li>
            <span class="label"><spring:message code="ui.label.product.bundle"/>:</span>
            <select id="dropdownfilter_bundles" class="select">
              <!-- This will be filled with product bundles on selecting  -->
            </select>
            <select id="hidden_bundles" style="display: none">
              <option id="ALL_PB" value="ALL">
                <spring:message code="label.all"/>
              </option>
              <c:forEach var="productBundle" items="${searchForm.productBundles}">
                <option id="${productBundle.id}" value="${productBundle.id}" instance="${productBundle.serviceInstanceId.uuid}">
                  <c:out value="${productBundle.name}"></c:out>
                </option>
              </c:forEach>
            </select>
          </li>
          <input type="hidden" id="selectedBundleID" value="${productBundleID}">
          </c:otherwise>
          </c:choose>
      </ul>
      <div class="button_panel">
        <input id="advSrchSubmit" class="submitbutton" tabindex="4" type="button" value="<spring:message code="ui.label.search.go"/>" />
        <input id="advSrchCancel" class="submitbutton" tabindex="5" type="button" value="<spring:message code="ui.label.search.cancel"/>" />
      </div>
</div>
<div class="widget_actionpopover_bot OSdropdown"></div>


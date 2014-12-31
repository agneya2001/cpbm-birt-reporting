<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<c:choose>
<c:when test="${not empty serviceCategoryList}">
<div class="navigation">
    <c:choose> 
      <c:when test="${currentUser.profile.operationsProfile && currentUser == effectiveTenant.owner}">
        <c:set var="userStyle" value=""/>
      </c:when> 
      <c:otherwise> 
        <c:set var="userStyle" value="user"/> 
      </c:otherwise>
    </c:choose> 
    <div class="flevel">
        <div class="menutab" id="service_category_list_container">
            <ul>
               <c:forEach items="${serviceCategoryList}" var="serviceCategory" varStatus="status">
                     <c:choose> 
                         <c:when test="${(selectedCategory!=null && selectedCategory==serviceCategory) || (selectedCategory == null && status.index == 0)}">
                             <c:set var="tabStatus" value="current ${userStyle}"/> 
                         </c:when> 
                         <c:otherwise> 
                             <c:set var="tabStatus" value="off"/> 
                         </c:otherwise> 
                     </c:choose>
                     <li class="categorytabs ${tabStatus}" id="l3_tab_<c:out value='${serviceCategory}'/>" category="<c:out value='${serviceCategory}'/>">
                       <spring:message code='label.service.category.${serviceCategory}'/>
                     </li>
                </c:forEach>
            </ul>
        </div>
    </div>
    <div class="slevel ${userStyle}">
      <div class="selectedservice ${userStyle}" id="serviceInstanceListContainer">
        <a class="selecteddropdown ${userStyle}" href="#" id="selectedServiceInstance"> </a>
        <div  id="serviceInstanceDropdownlist" class="dropdownlist">
          <ul id="serviceInstanceList">
          </ul>
        </div>
      </div>

      <c:choose> 

      <c:when test="${userHasCloudServiceAccount && showCloudConsoleLink}">
        <input type="hidden" name="currentTenantParam" id="currentTenantParam" value="<c:out value="${currentTenant.param}"/>">
        <div id="cloudServiceConsoleAll" style="float:right;margin:3px 20px 0 0;display:none;">
          <div class="btn-group">
            <button class="btn btn-info btn-mini" data-toggle="dropdown" data-hover="dropdown" data-delay="1000" data-close-others="false"><spring:message code="ui.home_service.page.title.launch.cloud.console"/></button>
            <button class="btn btn-info btn-mini dropdown-toggle" data-toggle="dropdown" data-toggle="dropdown" data-hover="dropdown" data-delay="1000" data-close-others="false" style="padding-bottom:8px;padding-top:0px;">
              <span class="caret"></span>
            </button>
            <ul class="dropdown-menu dropdown-menu-blue" style="z-index:10000;left:-50px;width: 150px">
                <c:forEach items="${cloudTypeServiceInstances}" var="cloudTypeServiceInstance">
                  <li><a href="javascript:void(0);" onclick="launchCloudServiceConsoleWithServiceInstanceUUID(this, '<c:out value="${cloudTypeServiceInstance.uuid}" />');" class='ellipsis' title='<c:out value="${cloudTypeServiceInstance.name}" />'><c:out value="${cloudTypeServiceInstance.name}" /></a></li>
            </c:forEach>
            </ul>
          </div>
        </div>


        <div id="cloudServiceConsoleID" style="float:right;margin:3px 20px 0 0;display:none">
            <div class="btn-group">
              <button class="btn btn-info btn-mini" onclick="launchCloudServiceConsole(this);"><spring:message code="ui.home_service.page.title.launch.cloud.console"/></button>
            </div>
        </div>
      </c:when> 

      <c:otherwise> 
        <c:if test="${userHasCloudServiceAccount}">
        <div id="cloudServiceResourcesAll" style="float:right;margin:3px 20px 0 0;display:none;">
          <div class="btn-group">
            <button class="btn btn-info btn-mini" data-hover="dropdown" data-delay="1000" data-close-others="false"><spring:message  code="service.connectorlist.manage" /></button>
            <button class="btn btn-info btn-mini dropdown-toggle" data-toggle="dropdown" data-hover="dropdown" data-delay="1000" data-close-others="false" style="padding-bottom:8px;padding-top:0px;">
              <span class="caret"></span>
            </button>
            <ul class="dropdown-menu dropdown-menu-blue" style="z-index:10000;left:-34px;width: 150px">
                <li><a href="javascript:void(0);" onclick="launchMyResourcesWithServiceInstanceUUID();"><spring:message code="page.level2.allservices"/></a></li>
                <c:forEach items="${cloudTypeServiceInstances}" var="cloudTypeServiceInstance">
                  <li><a href="javascript:void(0);" onclick="launchMyResourcesWithServiceInstanceUUID('<c:out value="${cloudTypeServiceInstance.uuid}" />');" class='ellipsis' title='<c:out value="${cloudTypeServiceInstance.name}" />'><c:out value="${cloudTypeServiceInstance.name}" /></a></li>
            </c:forEach>
            </ul>
          </div>
        </div>
        <div id="cloudServiceResources" style="float:right;margin:3px 20px 0 0;display:none;">
            <div class="btn-group">
              <button class="btn btn-info btn-mini"><spring:message  code="service.connectorlist.manage" /></button>
            </div>
        </div>
        </c:if>
      </c:otherwise>  
      </c:choose> 
      <c:if test="${not empty selectedCloudServiceInstance}">
        <input type="hidden" name="selectedCloudServiceInstance" id="selectedCloudServiceInstance" value="<c:out value="${selectedCloudServiceInstance}"/>">  
      </c:if>
    </div>
</div>
</c:when>
<c:otherwise>
  <div class="subscribe_dialog_message_box error" style="margin-left:20px;">
            <p><spring:message code='no.cloud.service.account'/></p>
        </div>
</c:otherwise>
</c:choose>
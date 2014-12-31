<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/channels.js"></script>
<c:if test="${serviceSettingsCount == 1}">

 <ul class="widget_detail_actionpanel">
   <li>
     <a href="javascript:void(0);" onclick="editChannelServiceSettings(this);" serviceSettingsChanelID="${serviceSettingsChanelID}" serviceSettingsInstanceUUID="${serviceSettingsInstanceUUID}">
       <spring:message code="label.edit"/>
     </a>
   </li>
 </ul>
<div id="chnServiceSettingsDiv">
<input type="hidden" id="serviceSettingsChanelID" value="${serviceSettingsChanelID}">
<input type="hidden" id="serviceSettingsInstanceUUID" value="${serviceSettingsInstanceUUID}">
    <c:forEach var="entry" items="${viewChannelServiceSettingsForm.channelServiceSettings}" varStatus="status">
      <div class="widget_grid details even">
         <div class="widget_grid_labels" style="width:200px;">
               <span style="width:auto;">
               		<spring:message code="${entry.serviceName}.ChannelSettings.${entry.name}.name" />
               </span>
         </div>
         <div class="widget_grid_description" >
               <span><c:out value="${entry.value}"></c:out></span>
         </div>
      </div>
    </c:forEach>
</div>
</c:if>
<c:if test="${serviceSettingsCount == 0}">
  <div id="chnServiceSettingsDiv">
    <div class="widget_grid details even" style="border-width: 0px 1px 1px 0px;">
      <div class="widget_grid_description">
        <span style="width: auto;"> 
          <spring:message code="ui.label.no.channel.service.settings.message" />
        </span>
      </div>
    </div>
  </div>
</c:if>
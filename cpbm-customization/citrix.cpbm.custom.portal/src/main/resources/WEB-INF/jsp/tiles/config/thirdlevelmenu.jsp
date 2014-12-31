<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<input type="hidden" id="l3_tenant_param" value='<c:out value="${tenant.param}"/>'/>

<div class='slider thirdlevel_subsubmenu'>
  <div class="thirdlevel_subsubmenu left"></div>
    <div class="thirdlevel_subsubmenu mid">
    <div class='thirdlevel_slidingbutton prev' style="display:none;"></div>
    <div class='thirdlevel_slidingbutton next' style="display:none;"></div>
      <div id="items_container"> 
        <ul>
          <li class="thirdlevel_subtab big off" id="l3_config_configaccountmanagement_tab" >
            <div class="thirdlevel_menuicons account_mgmt"></div>
            <p><spring:message code="page.level3.configaccountmanagement"/>
            </p>
          </li>
          <li class="thirdlevel_subtab big off" id="l3_config_crm_tab" >
            <div class="thirdlevel_menuicons CRM"></div>
            <p><spring:message code="page.level3.crm"/>
            </p>
          </li>
          <li class="thirdlevel_subtab big off" id="l3_config_integration_tab" >
            <div class="thirdlevel_menuicons integration"></div>
            <p> <spring:message code="page.level3.integration"/>
            </p>
          </li>
          <li class="thirdlevel_subtab big off" id="l3_config_portal_tab" >
            <div class="thirdlevel_menuicons portal"></div>
            <p> <spring:message code="page.level3.portal"/>
            </p>
          </li>
          <li class="thirdlevel_subtab big off" id="l3_config_server_tab" >
            <div class="thirdlevel_menuicons server"></div>
            <p> <spring:message code="page.level3.server"/>
            </p>
          </li>
          <li class="thirdlevel_subtab big off" id="l3_config_trialmanagement_tab" >
            <div class="thirdlevel_menuicons trialmanagement"></div>
            <p> <spring:message code="page.level3.trialmanagement"/>
            </p>
          </li>
         </ul>
     </div>
  </div>
  <div class="thirdlevel_subsubmenu right"></div>
</div>
    

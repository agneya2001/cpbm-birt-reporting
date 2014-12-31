<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<sec:authorize access="hasAnyRole('ROLE_ACCOUNT_ADMIN','ROLE_CONFIGURATION_CRUD')">
<div class='slider thirdlevel_subsubmenu'>
  <div class="thirdlevel_subsubmenu left"></div>
    <div class="thirdlevel_subsubmenu mid">
    <div class='thirdlevel_slidingbutton prev' style="display:none;"></div>
    <div class='thirdlevel_slidingbutton next' style="display:none;"></div>
      <div id="items_container"> 
        <ul>
          <li class="thirdlevel_subtab big ${Services}" id="l3_home_connectors_cs_tab" >
            <div class="thirdlevel_menuicons cloudservices"></div>
            <p><spring:message code="page.level3.cloudservices"/></p>
          </li>
         <sec:authorize access="hasRole('ROLE_CONFIGURATION_CRUD')"> 
          <li class="thirdlevel_subtab big off" id="l3_home_connectors_oss_tab" style="width:165px;">
            <div class="thirdlevel_menuicons boss"></div>
            <p> <spring:message code="page.level3.osservices"/>
            </p>
          </li>
          </sec:authorize>
         </ul>         
     </div>
  </div>
  <div class="thirdlevel_subsubmenu right"></div>
</div>
</sec:authorize>    

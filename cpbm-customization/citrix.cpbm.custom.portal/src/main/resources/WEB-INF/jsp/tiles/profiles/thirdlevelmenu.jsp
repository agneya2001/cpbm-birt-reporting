<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<div class="thirdlevel_subsubmenu">
  <div class="thirdlevel_subsubmenu left"></div>
    <div class="thirdlevel_subsubmenu mid">
          <div class="thirdlevel_subtab big off" id="l3_profile_serviceprovider_tab">
            <div class="thirdlevel_menuicons service_provider_profile"></div>
            <p><spring:message code="ui.profiles.show.page.service.provider"/>
            </p>
          </div>
          <div class="thirdlevel_subtab big off" id="l3_profile_customer_tab">
            <div class="thirdlevel_menuicons customer_profile"></div>
            <p><spring:message code="ui.profiles.show.page.customer"/>
            </p>
          </div>
          <!--  div class="thirdlevel_subtab big off" id="l3_profile_partner_tab">
            <div class="thirdlevel_menuicons partner_profile"></div>
            <p><spring:message code="ui.profiles.show.page.partner"/>
            </p>
          </div-->
        </div> 
        <div class="thirdlevel_subsubmenu right"></div>
</div>
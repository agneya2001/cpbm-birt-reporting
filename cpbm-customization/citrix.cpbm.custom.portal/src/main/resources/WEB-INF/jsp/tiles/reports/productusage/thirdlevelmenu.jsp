<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<div class="thirdlevel_subsubmenu">
  <div class="thirdlevel_subsubmenu left"></div>
  <div class="thirdlevel_subsubmenu mid">

    <div class="thirdlevel_subtab big on" id="channelusageByChannel">
      <div class="thirdlevel_menuicons navicon channels" style="margin-left:20px; width:20px"></div>
      <p>
        <spring:message code="ui.accounts.all.header.channel" />
      </p>
    </div>

    <div class="thirdlevel_subtab big off" id="channelusageByBillingGroup">
       <div class="thirdlevel_menuicons navicon channels" style="margin-left:20px; width:20px"></div>
      <p>
        <spring:message code="label.billing.group" />
      </p>
    </div>

  </div>

  <div class="thirdlevel_subsubmenu right"></div>
</div>
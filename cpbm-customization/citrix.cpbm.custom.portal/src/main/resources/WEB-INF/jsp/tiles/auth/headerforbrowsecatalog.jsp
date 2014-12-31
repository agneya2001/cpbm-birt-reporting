<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>

<script type="text/javascript" src="<%=request.getContextPath() %>/js/header.js"></script>
<fmt:setLocale value="${pageContext.request.locale}" scope="request"/>
<c:choose>
  <c:when test="${currentUser != null}">
    <c:set var="home_path" value="/home"/>
  </c:when>
  <c:otherwise>
    <c:set var="home_path" value="/"/>
  </c:otherwise>
</c:choose>
<div id="catalog_signupmenubox_container">
<div class="catalog_signupmenubox">
      <div class="catalog_signupmenubox_left"></div>
       <div class="catalog_signupmenubox_mid">
          <ul>
              <li class="catalog_signupmenubox_mid"><span class="icon login"></span><span id="signup_login_li" class="link"><spring:message code="page.title.signup.or.login"/></span></li>
               <li class="catalog_signupmenubox_mid last"  id="language_selector"><span class="icon language"></span><span class="link"><spring:message code="page.title.select.language"/></span>
                 <div class="catalog_signupmenubox_popover anonymous_catalog" id="language_selector_dropdown" style="display: None">
                    <ul class="catalog_signuppopoverlist">
                      <c:forEach items="${supportedLocaleList}" var="locale" varStatus="status">
                        <li class="language_select_option" id="<c:out value="${locale.key}"></c:out>"><span class="flagicons <c:out value="${locale.key}"></c:out>"></span><span class="languagelist"><c:out value="${locale.value}"></c:out></span></li>
                          </c:forEach>
                        </ul>
                  </div>
                </li>
            </ul>
        </div>
        <div class="catalog_signupmenubox_right"></div>
        
    </div>
</div>
<div id="header">
  <div class="header_left">
    <tiles:insertAttribute name="logo" />
  </div>
</div>

        

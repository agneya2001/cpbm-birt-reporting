<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
 <%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
 <div class="menu_dropdown_selectioncontainer_mid">
     <div class="menu_dropdown_selectionlistbox">
              <c:forEach items="${tenants}" var="tenant" varStatus="status">
                   <div class="menu_dropdown_selectionlist">
                    <spring:url value="/portal/tenants/list" var="account_path" htmlEscape="false">
                      <spring:param name="tenantUUId"><c:out value="${tenant.param}"/></spring:param>
                    </spring:url>
                    <a class="ellipsis" href="<c:out value="${account_path}"/>" title='<c:out value="${tenant.name}"/>'><c:out value="${tenant.name}"/> </a>
                   </div>
              </c:forEach>
      </div>
</div>
        <div class="menu_dropdown_selectioncontainer_bot">
          <div class="menu_dropdown_prevnxtbox">
          
          
          <c:choose>
            <c:when test="${currentPage==1}">
            <div class="menu_dropdown_previcon" ></div>
            </c:when>
           <c:otherwise>
              <a href="#" class="menu_dropdown_previcon"  
              onclick="getCurrentPageAccounts(<c:out value="${prevPage}" />,'<c:out value="${size}" />','<c:out value="${charRange}" />','<c:out value="${pattern}" />');">
               </a>
          </c:otherwise>
          </c:choose>
         <p><span><c:out value="${currentPage}" /></span> of <span><c:out value="${totalpages}" /></span></p>
                
        <c:choose>
          <c:when test="${currentPage==totalpages}">
            <div class="menu_dropdown_nexticon"></div>
          </c:when>
        <c:otherwise>
            <a href="#" class="menu_dropdown_nexticon"  
            onclick="getCurrentPageAccounts(<c:out value="${nextPage}" />,'<c:out value="${size}" />','<c:out value="${charRange}" />','<c:out value="${pattern}" />');">
            </a>
          </c:otherwise>
        </c:choose>                
</div>
</div>

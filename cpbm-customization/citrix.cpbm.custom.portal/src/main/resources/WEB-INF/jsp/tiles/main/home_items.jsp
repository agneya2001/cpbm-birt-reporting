<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<script type="text/javascript" src="<%=request.getContextPath() %>/js/jquery.carouFredSel-5.6.4.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/home_stats.js"></script>

            <div class="image_carousel" id="system_wide_capacity_container">
                <div id="capacity_carousel_container" style="display:none;">
                 
                  
                  <c:forEach items="${dashboardItems}" var="dashboardItem" varStatus="status">
                  
                  <div class="stats_container" >
                    <div class="stats_innercontainer">
                      <div class="stats_innercontainer col1">
                            <div class="capacity_data_box">
                              <h3 class="ellipsis" style="width:105px" title="${dashboardItem.itemName}"><c:out value="${dashboardItem.itemName}"/></h3>
                              <c:choose>
                                <c:when test="${dashboardItem.itemValue ne null}">
                                  <c:choose>
                                      <c:when test="${dashboardItem.itemValueType ne null && dashboardItem.itemValueType=='currency'}">
                                          <span class="percentage ellipsis" style="font-size:21px;" title='<c:out value="${effectiveTenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}" maxFractionDigits="${currencyFractionalDigitsLimit}" minFractionDigits="${minFractionDigits}" value="${dashboardItem.itemValue}" />'>
                                              <c:out value="${effectiveTenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}" maxFractionDigits="${currencyFractionalDigitsLimit}" minFractionDigits="${minFractionDigits}" value="${dashboardItem.itemValue}" />
                                          </span>
                                      </c:when>
                                      <c:otherwise>
                                          <span class="percentage"><c:out value="${dashboardItem.itemValue}"/></span>
                                          <c:if test="${dashboardItem.itemUom ne null}">
                                            <p><span id="capacityused"><c:out value="${dashboardItem.itemUom}"/></span></p>
                                          </c:if>
                                      </c:otherwise>
                                  </c:choose>
                                    
                                </c:when>
                                <c:otherwise>
                                    <span class="percentage">N/A</span>
                                </c:otherwise>
                            </c:choose>

                            </div>
                   </div>
                      <div class="stats_innercontainer col2" >
                      
                      <c:choose>
                      <c:when test="${dashboardItem.itemImage ne null}">
                          <span class="statsicon ${dashboardItem.itemImage}"></span>
                      </c:when>
                      <c:otherwise>
                        <c:if test="${dashboardItem.itemCustomImage ne null}">
                          <img style="height:auto;width:98px;margin:0px;padding:0px;border:none;"  src="${dashboardItem.itemCustomImage}" />
                        </c:if>
                      </c:otherwise>
                      </c:choose>
                      
                      
                      
                      
                       <div  style="float:right;"></div>
                       </div>

                    </div>
                    <span class="zonewrapper">
                        
                           <c:if test="${dashboardItem.itemValue2 ne null }">
                               <c:if test="${dashboardItem.itemValue2Prefix ne null }">
                                   <c:out value="${dashboardItem.itemValue2Prefix}"/>:
                               </c:if>
                               <c:choose>
                                   <c:when test="${dashboardItem.itemValue2Type ne null && dashboardItem.itemValue2Type=='currency'}">
                                       <c:out value="${effectiveTenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}" maxFractionDigits="${currencyFractionalDigitsLimit}" minFractionDigits="${minFractionDigits}" value="${dashboardItem.itemValue2}" />
                                   </c:when>
                               
                                   <c:otherwise>
                                       <c:out value="${dashboardItem.itemValue2}"/>
                                   </c:otherwise>
                               </c:choose>
                           </c:if>
                        
                    
                    </span>
                  </div>
                  
                  </c:forEach>

                 
                </div>
                <div class="clearfix"></div>
                <a class="slider_prev" id="foo2_prev" href="#"><span><spring:message code="label.previous.short" htmlEscape="false"/></span></a>
                <a class="slider_next" id="foo2_next" href="#"><span><spring:message code="label.next" htmlEscape="false"/></span></a>
                <div class="pagination"> <div class="pagination_bgwrapper"  id="foo2_pag" style="display:none;"></div></div>
            </div>
           
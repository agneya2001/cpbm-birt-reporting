<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<ul class="widget_navigationlist" id="channelgridcontent" style="height:616px">
  <c:choose>
    <c:when test="${empty channels || channels == null}">
      <!-- Empty list -->
      <li class="widget_navigationlist nonlist" id="non_list">
        <span class="navicon"></span>
        <div class="widget_navtitlebox">
          <span class="newlist">
            <spring:message code="ui.warning.message.no.channels.available"></spring:message>
          </span>
        </div>
        <div class="widget_statusicon nostate"></div>
      </li>
    </c:when>
    <c:otherwise>
	    <c:forEach var="channel" items="${channels}" varStatus="status">
	
	      <c:choose>
	        <c:when test="${status.index == 0}">
	            <c:set var="firstChannel" value="${channel}"/>
	            <c:set var="selected" value="selected"/>
	        </c:when>
	        <c:otherwise>
	            <c:set var="selected" value=""/>
	        </c:otherwise>
	      </c:choose>
	
	      <c:choose>
	        <c:when test="${status.index % 2 == 0}">
	          <c:set var="rowClass" value="odd"/>
	        </c:when>
	        <c:otherwise>
	            <c:set var="rowClass" value="even"/>
	        </c:otherwise>
	      </c:choose>    
	
	      <li class="<c:out value="widget_navigationlist ${selected} ${rowClass} channels"/>"
	           id="channel<c:out value="${channel.id}"/>"
	           onclick="viewChannel(this)"
	           onmouseover="viewInfoBubble(this)"
	           onmouseout="clearInfoBubble(this)"  
	           >
	           <span id="nav_icon" class="navicon channels"></span>
	           <div class="widget_navtitlebox" style="width:150px;">
	            <span id="channel_name" class="title"><c:out value="${channel.name}"/>
	            
	            </span>
	            
	            <span id="channel_currencies" class="subtitle">
	               <c:forEach var="supported_currency" items="${channel.catalog.supportedCurrencies}" varStatus="status">
	
	                  <c:if test="${status.index > 0}">
	                  ,
	                  </c:if>
	
	                   <c:out value="${supported_currency.currency.currencyCode}"/>
	                 </c:forEach>
	             </span>
	             
	           </div>
	           <div style="width:50px;float:left;">
            <c:if test="${defaultChannel ne null && defaultChannel.id eq channel.id}">
              <span id="default_channel_icon" class="navicon defaultchannel" style="margin-left:0px;margin-top:13px;" title="<spring:message code="ui.image.title.default.channel"/>"></span>
             </c:if>
             <div id="is_unpublished_icon">
             <c:if test="${not empty channel.unPublishedChannelBrandingConfigurations}">
             <i class="fa fa-file-image-o" style="margin-top:17px;float:right;padding-right:18px;"></i>
             </c:if>
             </div>
             </div>
	           <div id="info_bubble" class="widget_info_popover" style="display: none;">
	                <div class="popover_wrapper">
	                  <div class="popover_shadow"></div>
	                    <div class="popover_contents">
	                      <div class="raw_contents">
	                        <div class="raw_content_row" id="info_bubble_displayname">
	                            <div class="raw_contents_title">
	                              <span><spring:message code="label.name"/>:</span>
	                              </div>
	                              <div class="raw_contents_value">
	                              <span><c:out value="${channel.name}"/></span>
	                            </div>
	                            <div class="raw_contents_title" id="info_bubble_code">
	                              <span><spring:message code="ui.label.code"/>:</span>
	                              </div>
	                              <div class="raw_contents_value">
	                              <span>
	                                 <c:choose>
	                                   <c:when test="${channel.code == '' or channel.code == null}">
	                                     &nbsp;
	                                   </c:when>
	                                   <c:otherwise>
	                                     <c:out value="${channel.code}"/>
	                                   </c:otherwise>
	                                 </c:choose>
	                              </span>
	                            </div>
	                            <div class="raw_contents_title" id="info_bubble_currencies">
	                              <span><spring:message code="ui.products.label.create.catalog.select.currency"/>:</span>
	                              </div>
	                              <div class="raw_contents_value">
	                                <span>
	                                  <c:forEach var="supported_currency" items="${channel.catalog.supportedCurrencies}" varStatus="status">
	
	                                    <c:if test="${status.index > 0}">
	                                    ,
	                                    </c:if>
	
	                                     <c:out value="${supported_currency.currency.currencyCode}"/>
	                                 </c:forEach>
	                                </span>
	                            </div>
	                          </div>
	                      </div>
	                    </div>
	                </div>
	           </div>
	      </li>
	    </c:forEach>
	   </c:otherwise>
	 </c:choose>
</ul>

<div class="widget_panelnext">
  <div class="widget_navnextbox">
      <c:choose>
        <c:when test="${current_page <= 1}">
            <a class="widget_navnext_buttons prev nonactive" href="javascript:void(0);" id="click_previous">
               <spring:message code="label.previous.short"/>
            </a>
        </c:when>
        <c:otherwise>
            <a class="widget_navnext_buttons prev" href="javascript:void(0);" id="click_previous" onclick="previousClick()">
              <spring:message code="label.previous.short"/>
            </a>
        </c:otherwise>
      </c:choose> 
      
      <c:choose>
        <c:when test="${enable_next == true}">
         <a class="widget_navnext_buttons next" href="javascript:void(0);" id="click_next" onclick="nextClick()">
             <spring:message code="label.next"/>
          </a>
          </c:when>
        <c:otherwise>
           <a class="widget_navnext_buttons next nonactive" href="javascript:void(0);" id="click_next" >
             <spring:message code="label.next"/>
            </a>
          </c:otherwise>
      </c:choose> 
    </div>
</div>
<input type="hidden" id="current_page" value="<c:out value="${current_page}"/>" />
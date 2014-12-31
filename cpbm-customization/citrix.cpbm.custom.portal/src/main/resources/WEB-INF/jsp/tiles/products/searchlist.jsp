<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<ul class="widget_navigationlist" id="productgridcontent" style="height:616px">
	<c:choose>
		<c:when test="${empty productsList || productsList == null}">
			<c:set var="productsLen" value="0" />
			<!-- Empty list -->
			<!--look when there is no list starts here-->
			<li class="widget_navigationlist nonlist" id="non_list"><span
				class="navicon"></span>
			<div class="widget_navtitlebox"><span class="newlist"><spring:message
				code="ui.warning.message.no.products.available"></spring:message> </span></div>
			<div class="widget_statusicon nostate"></div>
			</li>
			<!--look when there is no list ends here-->
		</c:when>
		<c:otherwise>
			<c:forEach var="product" items="${productsList}" varStatus="status">
				<c:set var="productsLen" value="${productsLen+1}" />
				<c:choose>
					<c:when test="${status.index == 0}">
						<c:set var="selected" value="selected" />
					</c:when>
					<c:otherwise>
						<c:set var="selected" value="" />
					</c:otherwise>
				</c:choose>
				<c:choose>
					<c:when test="${status.index % 2 == 0}">
						<c:set var="rowClass" value="odd" />
					</c:when>
					<c:otherwise>
						<c:set var="rowClass" value="even" />
					</c:otherwise>
				</c:choose>

				<li
					class='<c:out value="widget_navigationlist ${selected}"/> j_viewproduct'
					onclick="viewProduct(this)" id="<c:out value="row${product.id}"/>"
					onmouseover="showInfoBubble(this)"
					onmouseout="hideInfoBubble(this)"><span
					<%-- TODO: neeraj FIXME ( removed Product Type ) 
					class='navicon <c:out value="${product.productType.name}" />'
					--%>
					id="nav_icon"></span>
				<div class="widget_navtitlebox"><span class="title"><c:out
					value="${product.name}" /></span> <span class="subtitle">
          <c:out value="${product.category.name}" />
					<%-- TODO: neeraj FIXME ( removed Product Type ) 
					<spring:message
					code="product.type.${product.productType.name}" />
					--%>
					</span></div>
				<!--  we dont have any state for product
              <c:set var="status_icon" value="nostate"/>  
  
              <div class="<c:out value="widget_statusicon ${status_icon}" />"></div> -->

				<!--Info popover starts here-->
				<div class="widget_info_popover" id="info_bubble"
					style="display: none">
				<div class="popover_wrapper">
				<div class="popover_shadow"></div>
				<div class="popover_contents">
				<div class="raw_contents">
				<div class="raw_content_row" id="info_bubble_displayname">
				<div class="raw_contents_title"><span><spring:message
					code="ui.products.label.create.product.uom" />:</span></div>
				<div class="raw_contents_value">
          <span id="value">
            <c:out value="${product.uom}" />
          </span>
        </div>
				</div>

				<div class="raw_content_row" id="info_bubble_code">
				<div class="raw_contents_title"><span><spring:message
					code="ui.products.label.create.product.code" />:</span></div>
				<div class="raw_contents_value"><span id="value"><c:out
					value="${product.code}" /></span></div>
				</div>
        
       <div class="raw_content_row" id="info_bubble_code">
         <div class="raw_contents_title">
                <span><spring:message code="ui.products.label.create.type"/></span>
         </div>
         <div class="raw_contents_value">
              <c:choose>
              <c:when test="${product.discrete}">
                   <span class = "ellipsis" title="<spring:message code="ui.products.label.type"/>" id="value"><spring:message code="ui.products.label.discrete"/></span>
          
             </c:when>
            <c:otherwise>
                  <span class = "ellipsis" title="<spring:message code="ui.products.label.type"/>" id="value"><spring:message code="ui.products.label.metered"/></span>
             </c:otherwise>
            </c:choose>
            </div>
       </div>

				</div>
				</div>
				</div>
				</div>
				<!--Info popover ends here--></li>


			</c:forEach>
		</c:otherwise>
	</c:choose>

</ul>
<div id="<c:out value="count${size}"/>" class="countDiv"></div>
<script type="text/javascript">
          var productsLen = "<c:out value="${productsLen}"/>";
        </script> 
        <div class="widget_panelnext">
              <div class="widget_navnextbox">
                <c:choose>
                  <c:when test="${!enablePrevious}">
                      <a class="widget_navnext_buttons prev nonactive"  href="javascript:void(0);" id="click_previous">
                        <spring:message code="label.previous.short" /></a>
                  </c:when>
                  <c:otherwise>
                    <a class="widget_navnext_buttons prev" href="javascript:void(0);" id="click_previous" onclick="previousClick()">
                      <spring:message code="label.previous.short" /></a>
                  </c:otherwise>
                </c:choose>
                <c:choose>
								  <c:when test="${enableNext}">
								    <a class="widget_navnext_buttons next" href="javascript:void(0);"
								      id="click_next" onclick="nextClick()"><spring:message
								      code="label.next" /></a>
								  </c:when>
								  <c:otherwise>
								    <a class="widget_navnext_buttons next nonactive"
								      href="javascript:void(0);" id="click_next"><spring:message
								      code="label.next" /></a>
								  </c:otherwise>
								</c:choose>
				  </div>
      </div>
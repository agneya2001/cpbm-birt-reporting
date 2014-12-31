<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec"	uri="http://www.springframework.org/security/tags"%>

<ul class="widget_navigationlist " id="productBundlegridcontent">
	<c:choose>
		<c:when		test="${empty productBundlesList || productBundlesList == null}">
			<c:set var="bundlesLen" value="0" />
			<!-- Empty list -->
			<!--look when there is no list starts here-->
			<li class="widget_navigationlist nonlist" id="non_list"><span	class="navicon"></span>
			<div class="widget_navtitlebox"><span class="newlist"><spring:message
				code="ui.warning.message.no.bundles.available"></spring:message> </span></div>
			<div class="widget_statusicon nostate"></div>
			</li>
			<!--look when there is no list ends here-->
		</c:when>
		<c:otherwise>
			<c:forEach var="productBundle" items="${productBundlesList}"	varStatus="status">
				<c:set var="bundlesLen" value="${bundlesLen+1}" />
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
				<c:set var="bundleTypeClass" value="computebundles"></c:set>
				
				<li
					class='<c:out value="widget_navigationlist ${selected}"/> j_viewbundle'
					onclick="viewProductBundle(this)"
					id="<c:out value="row${productBundle.id}"/>"
					onmouseover="showInfoBubble(this)"
					onmouseout="hideInfoBubble(this)"><span class='navicon <c:out value="${bundleTypeClass}"/>'
					id="nav_icon"></span>
				<div class="widget_navtitlebox"><span class="title"><c:out
					value="${productBundle.name}" /></span> <span class="subtitle"><spring:message
          code="charge.type.${productBundle.rateCard.chargeType.name}" /></span></div>
			
				<div class="widget_info_popover" id="info_bubble"
					style="display: none">
				<div class="popover_wrapper">
				<div class="popover_shadow"></div>
				<div class="popover_contents">
				<div class="raw_contents">
				<div class="raw_content_row">
				<div class="raw_contents_title"><span><spring:message
					code="label.bundle.create.publish" />:</span></div>
				<div class="raw_contents_value"><span id="bundle_publish_status"><spring:message code="label.${productBundle.publish}"/></span></div>
				</div>

				<div class="raw_content_row">
				<div class="raw_contents_title"><span><spring:message
					code="ui.products.label.create.product.code" />:</span></div>
				<div class="raw_contents_value"><span><c:out
					value="${productBundle.code}" /></span></div>
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
<script type="text/javascript">
          var bundlesLen = "<c:out value="${productsLen}"/>";
        </script>
<div class="widget_panelnext">
<div class="widget_navnextbox"><c:choose>
	<c:when test="${!enablePrevious}">
		<a class="widget_navnext_buttons prev nonactive"
			href="javascript:void(0);" id="click_previous"><spring:message
			code="label.previous.short" /></a>
	</c:when>
	<c:otherwise>
		<a class="widget_navnext_buttons prev" href="javascript:void(0);"
			id="click_previous" onclick="previousClick()"><spring:message
			code="label.previous.short" /></a>
	</c:otherwise>
</c:choose> <c:choose>
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
</c:choose></div>
</div>

 
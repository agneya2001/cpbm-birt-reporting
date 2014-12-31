<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<jsp:include page="js_messages.jsp"></jsp:include>

<script type="text/javascript" src="<%=request.getContextPath() %>/js/commonproducts.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/products.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/commonBundles.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/jquery.iframe-post-form.js"></script>

<script type="text/javascript">

  $(document).ready(function() {
   var firstServiceInstance = $("#instances").find("a:first");
   if(firstServiceInstance != undefined){
     getProductOrBundleListing(firstServiceInstance);
   }
 });
</script>

<ul id="instances" class="widget_detail_navpanel">
  <c:forEach var="serviceInstance" items="${serviceInstances}" varStatus="status">

    <c:set var="first" value="color: #2C8BBC;"/>
    <c:if test="${status.index == 0}">
      <c:set var="first" value="color: #000;"/>
    </c:if>

    <c:set var="last" value=""/>
    <c:if test="${status.index == (fn:length(serviceInstances) -1) }">
      <c:set var="last" value=" last"/>
    </c:if>

    <c:set var="selected" value="instance_not_selected"/>
    <c:if test="${status.index == 0}">
      <c:set var="selected" value="instance_selected"/>
    </c:if>
    
    <c:set var="serviceHasUsageType" value="false" />
    <c:if test="${fn:length(serviceInstance.service.serviceUsageTypes) gt 0}">
    	<c:set var="serviceHasUsageType" value="true" />
    </c:if>

    <li class='widget_detail_navpanel <c:out value="${last}"/>'>
      <a title='<c:out value="${serviceInstance.name}"/>' id='<c:out value="${serviceInstance.uuid}"/>' onclick="getProductOrBundleListing(this);" href="javascript:void(0);" style='max-width:120px;<c:out value="${first}"/>' class='ellipsis <c:out value="${selected}"/>' serviceHasUsageType='<c:out value="${serviceHasUsageType}"/>'>
         <c:out value="${serviceInstance.name}"/>
      </a>
    </li>
  </c:forEach>
</ul>

<div class="slider thirdlevel_subsubmenu">
  <div class="thirdlevel_subsubmenu left"></div>
  <div class="thirdlevel_subsubmenu mid">
    <div id="prod_bundles_container"> 
      <ul>
       <li id="product_tab" class="thirdlevel_subtab big on" onclick="listProducts(this);" style="display:none">
          <div class="thirdlevel_menuicons products"></div>
          <p>
            <spring:message code="page.level2.products"/>
          </p>
        </li>
        <li id="product_bundle_tab" class="thirdlevel_subtab big off" onclick="listBundles(this);">
          <div class="thirdlevel_menuicons product_bundles"></div>
          <p>
            <spring:message code="ui.label.product.bundle.title"/>
          </p>
        </li>
       </ul>
    </div>
  </div>
  <div class="thirdlevel_subsubmenu right"></div>
</div>

<div>
  <ul class="widget_detail_navpanel" id="product_select">
     <li class="widget_detail_navpanel" id="all_product_separater">
       <a id="all_product" href="javascript:void(0);" onclick="listProductByFilter(this, 'all')"><spring:message code="ui.product.all.label"/></a>
     </li>
     <li class="widget_detail_navpanel" id="active_product_separater">
       <a id="active_product" href="javascript:void(0);" onclick="listProductByFilter(this, 'active')"style="display:none"><spring:message code="ui.product.active.label"/></a>
     </li>
     <li class="widget_detail_navpanel" id="retire_product_separater">
       <a id="retire_product" href="javascript:void(0);" onclick="listProductByFilter(this, 'retire')" style="display:none"><spring:message code="ui.product.retired.label"/></a>
     </li>
     <li class="widget_detail_navpanel" id="all_productbundle_separater">
       <a id="all_productbundle" href="javascript:void(0);" onclick="listProductBundlesByFilter(this, 'all')" style="display:none"><spring:message code="ui.productbundles.all"/></a>
     </li>
     <li class="widget_detail_navpanel" id="publish_productbundle_separater">
       <a id="publish_productbundle" href="javascript:void(0);" onclick="listProductBundlesByFilter(this, 'publish')" style="display:none"><spring:message code="ui.productbundles.publish"/></a>
     </li>
     <li class="widget_detail_navpanel" id="unpublish_productbundle_separater">
       <a id="unpublish_productbundle" href="javascript:void(0);" onclick="listProductBundlesByFilter(this, 'unpublish')" style="display:none"><spring:message code="ui.productbundles.unpublish"/></a>
     </li>  
  </ul>
</div>

<div id="productBundleListingDiv" />
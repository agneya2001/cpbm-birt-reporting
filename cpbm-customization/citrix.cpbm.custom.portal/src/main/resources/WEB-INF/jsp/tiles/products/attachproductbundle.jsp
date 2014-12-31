<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<script>
  $(".widget_checkbox").off('click');
  $(".widget_checkbox").on('click', function() {
    if($(this).find("span").attr("class") == "unchecked") {
         $(this).find("span").removeClass('unchecked').addClass('checked');
       } else {
         operation = "remove";
         $(this).find("span").removeClass('checked').addClass('unchecked');
       }
  });
</script>

<div class="widget_browsergrid_wrapper" id="attach_product_bundle" style="width:auto; height: 300px;" >

      <div id="currency_row_container" style="margin-left: 15px; margin-right: 20px; margin-top: 20px;">
      <c:forEach var="bundle" items="${productBundles}" varStatus="status">

        <c:choose>
          <c:when test="${status.index % 2 == 0}">
            <c:set var="rowClass" value="odd"/>
          </c:when>
          <c:otherwise>
              <c:set var="rowClass" value="even"/>
          </c:otherwise>
        </c:choose>
        
        <div class="<c:out value="widget_grid details ${rowClass}"/>" style="width: 468px;">
            <div class="widget_checkbox widget_checkbox_wide"
                 bundleId="<c:out value="${bundle.productBundle.id}"/>">
              <span class="unchecked"></span> 
            </div>
            <div class="widget_grid_description" style="margin:0;">
              <span class = "ellipsis" title = "<c:out value="${bundle.productBundle.name}"/>" ><strong><c:out value="${bundle.productBundle.name}"/> </strong></span>
            </div>
        </div>
      </c:forEach>
  </div>
</div>

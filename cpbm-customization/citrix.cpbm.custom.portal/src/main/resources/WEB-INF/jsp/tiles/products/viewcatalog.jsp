<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/catalogs.js"></script>
<script type="text/javascript">
var productsUrl = "<%=request.getContextPath() %>/portal/products/";
var productBundlesUrl = "<%=request.getContextPath() %>/portal/productBundles/";
</script>
<!-- Start View Catalog Details --> 
<div class="main_detailsbox" style="width:100%;">
	<!-- Title -->
     <div class="main_details_titlebox">
          <h2><spring:message code="ui.products.label.view.details"/></h2>
     </div>   
     <!-- View Catalog Details -->                     
     <div class="main_details_contentbox" style="width:98%;">
          <div class="main_detailsistbox" style="width:100%;">
          	                           
             <div class="db_gridbox_rows detailsodd">
             	<div class="db_gridbox_columns" style="width:20%;">
                 	<div class="db_gridbox_celltitles details"><strong><spring:message code="ui.products.label.create.name"/></strong></div>
                 </div>
                 <div class="db_gridbox_columns" style="width:75%;">
                 <div class="db_gridbox_celltitles details">
                 	 <c:out value="${catalog.name}"></c:out>
                 </div>
                 </div>
			</div>
			
            <div class="db_gridbox_rows detailsodd">
             	<div class="db_gridbox_columns" style="width:20%;">
                 	<div class="db_gridbox_celltitles details"><strong><spring:message code="ui.products.label.create.description"/></strong></div>
                 </div>
                 <div class="db_gridbox_columns" style="width:75%;">
                 	<div class="db_gridbox_celltitles details">
                 	<c:out value="${catalog.description}" />
                 	</div>
                 </div>
			 </div>

             <div class="db_gridbox_rows detailsodd">
                <div class="db_gridbox_columns" style="width:20%;">
                    <div class="db_gridbox_celltitles details"><strong><spring:message code="ui.products.label.create.catalog.code"/></strong></div>
                 </div>
                 <div class="db_gridbox_columns" style="width:75%;">
                 <div class="db_gridbox_celltitles details">
                     <c:out value="${catalog.code}"></c:out>
                 </div>
                 </div>
            </div>
       <c:if test="${isSystemRoot}">
         <div class="db_gridbox_rows detailsodd">
           <div class="db_gridbox_columns" style="width:20%;">
              <div class="db_gridbox_celltitles details"><strong><spring:message code="ui.products.label.view.catalog.channels"/></strong></div>
           </div>
             <div class="db_gridbox_columns" style="width:75%;">
              <div class="db_gridbox_celltitles details">
                <c:forEach items="${catalog.channels}" var="item" varStatus="status">
                    <c:if test="${item.removed == null}">
                      <p>
                        <c:out value="${item.name}"></c:out>
                      </p>
                    </c:if>
                </c:forEach>
              </div>
             </div>
         </div>
       </c:if>
       
       <div class="db_gridbox_rows detailsodd">
       	<div class="db_gridbox_columns" style="width:20%;">
        	<div class="db_gridbox_celltitles details"><strong><spring:message code="ui.products.label.create.catalog.select.currency"/></strong></div>
        </div>
        <div class="db_gridbox_columns" style="width:75%;">
        	<div class="db_gridbox_celltitles details">
        		<c:forEach var="supported_currency" items="${supportedCurrencies}" varStatus="status"><spring:message code="currency.longname.${supported_currency.currency.currencyCode}"></spring:message>
                	<br/>
               	</c:forEach>
            </div>
       	</div>
       </div>
       	</div>
       </div>
                   
    <div class="maindetails_footerlinksbox">
         <p><a href="javascript:void(0);" onclick="editCatalogGet(this)" class="editCatalog" 
            id="<c:out value="edit${catalog.id}"/>"><spring:message code="ui.products.label.view.edit"/></a>  <span > |</span> </p> 
         <p><a href="<%=request.getContextPath()%>/portal/productBundles/<c:out value="${catalog.id}"/>/listbundles"><spring:message code="ui.products.label.view.catalog.manage.bundle"/></a> <span > |</span> </p>
         <p><a href="<%=request.getContextPath()%>/portal/productBundles/<c:out value="${catalog.id}"/>/manageurc"><spring:message code="ui.products.label.view.catalog.urc"/></a></p>
    </div>
</div>
               
<!-- End view Catalog Details -->
                    

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
<link rel="stylesheet" type="text/css" media="all" href="<%= request.getContextPath() %>/css/jquery/themes/base/jquery-ui-1.7.2.custom.css"/>

<!-- Starts body -->

		<!-- Body Header  -->
		<div class="maintitlebox" style="width:98%;">
	    <div class="secondlevel_breadcrumb_panel" style="width:auto;">
      <div class="secondlevel_breadcrumbbox">
          <p><spring:message code="ui.products.label.list.all.catalogs"/></p>
      </div>           
        
    </div>  
           <c:if test="${hasCreateCatalogPermission}">
  	         <div class="maintitle_boxlinks_tab">
  	              <p><a href="javascript:void(0);" onclick="addNewCatalogGet()" class="addnewcatalog"><spring:message code="ui.products.label.list.add.new.catalog"/></a>
  	         </div>
           </c:if>
	    </div>
	     <div class="clearboth"></div>
         <div id="addnewcatalogDiv"> </div>
	    <div class="maincontent_bigverticalpanel"  style="width:98%;">
	   
                	
               
                  <!-- Start List catalogs -->	
                  
                	<div  class="main_listbox" style="width:100%;">
                    	<div class="main_gridpanel" style="width:100%;">
                    	<!-- Header -->
                            	<div class="db_gridbox_rows header" >
                            		<div class="db_gridbox_columns" style="width:20%;">
                                    	<div class="db_gridbox_celltitles header">
                                      <spring:message code="ui.products.label.list.id"/></div>
                                    </div>
                                	<div class="db_gridbox_columns" style="width:30%;">
                                    	<div class="db_gridbox_celltitles header">
                                      <spring:message code="ui.products.label.create.name"/></div>
                                    </div>                                   
                                    <div class="db_gridbox_columns" style="width:49%;">
                                    	<div class="db_gridbox_celltitles header">
                                      <spring:message code="ui.products.label.create.description"/></div>
                                    </div>                                     
                                </div>
                                 <!-- Header end -->
                 				 <!-- Data Start-->
                 							
                                <div class="main_gridlistbox" id="cataloggridcontent" style="width:100%;">  
                                  <c:choose>
								 <c:when test="${empty catalogsList || catalogsList == null}">
									<!-- Empty list -->
									<div id="cataloggridcontentDiv" class="emptylist" >
									  <spring:message var="catalogsMsg" code="ui.label.emptylist.catalogs" ></spring:message>
									  <spring:message var="catalogMsg" code="ui.label.emptylist.catalog" ></spring:message>
									  <p><spring:message code="ui.label.emptylist.notavailable" arguments="${catalogsMsg}" htmlEscape="false"/></p>
									  <c:if test="${hasCreateCatalogPermission}">
										  <p><spring:message code="ui.label.emptylist.create.first" arguments="${catalogMsg}" htmlEscape="false"/></p>
										  <div class="button" onclick="addNewCatalogGet()">
											<div class="left"></div>
											<a href="javascript:void(0);"  class="addnewcatalog"><spring:message code="ui.products.label.list.add.new.catalog"/></a>
											<div class="right"></div>
										  </div>
									  </c:if>
									</div>
									<!-- end Empty list -->
								 </c:when>
     							<c:otherwise>                          
                                <c:forEach var="catalog" items="${catalogsList}" varStatus="status">
                                <c:choose>
	    								<c:when test="${status.index == 0}">
	      									<c:set var="firstCatalog" value="${catalog}"/>
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
                                    <div class="<c:out value="db_gridbox_rows ${selected} ${rowClass}"/>" onclick="viewCatalog(this)" id="<c:out value="row${catalog.id}"/>">
                                     <div class="db_gridbox_columns" style="width:20%;">
                                            <div class="db_gridbox_celltitles">
                                           		<c:out value="${catalog.id}"/>
                                            </div>
                                        </div>
                                        <div class="db_gridbox_columns" style="width:30%;">
                                            <div class="db_gridbox_celltitles">
                                           		<c:out value="${catalog.name}"/>
                                            </div>
                                        </div>                                        
                                        <div class="db_gridbox_columns" style="width:49%;">
                                            <div class="db_gridbox_celltitles">
                                            	<c:out value="${catalog.description}"/>
                                            </div>
                                        </div>                                        
                                    </div>
                                </c:forEach>
                             </c:otherwise> 
                           </c:choose>	   
                              	</div>                                
                        <div class="admin_main_grid_botarrow" ></div>     
                            </div>
				    </div>
	    <!-- End List Products -->
	    
	    <div id="<c:out value="count${size}"/>" class="countDiv"></div>
	     <div class="clearboth"></div>
         <div id="viewcatalogDiv"> 
         
         
          <c:if test="${firstCatalog != null}">
            <c:set var="catalog" value="${firstCatalog}" scope="request"></c:set>
          	<jsp:include page="viewcatalog.jsp"></jsp:include>          
          </c:if>
         
         </div>
         <div id="editcatalogDiv"> </div>
	  </div>

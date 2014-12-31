<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/catalogs.js"></script>
<script type="text/javascript">
var productsUrl = "<%=request.getContextPath() %>/portal/products/";
</script>


<style>
.ui-datepicker-trigger {
    margin-left : 5px;
    margin-top : 5px;
}
</style>

 <!--  Edit Product starts here-->
<div class="main_detailsbox" style="width:100%;">
	<!-- Title -->
     <div class="main_details_titlebox">
          <h2><spring:message code="ui.products.label.edit.edit"/><c:out value="${catalogForm.catalog.name}" /></h2>
     </div> 
    <spring:url value="/portal/products/editcatalog" var="edit_catalog_path" htmlEscape="false" /> 
    <form:form commandName="catalogForm" cssClass="ajaxform" id="catalogForm"  action="${edit_catalog_path}" onsubmit="editCatalog(event,this)">
     <!-- Edit fields -->
      <div class="main_details_contentbox" style="width:98%;">
          <div class="main_detailsistbox" style="width:100%;">
             <div class="db_gridbox_rows detailsodd">
             	<div class="db_gridbox_columns" style="width:20%;">
                 	<div class="db_gridbox_celltitles details"><strong><spring:message code="ui.products.label.create.name"/></strong></div>
                 </div>
                 <div class="mandatory_wrapper db_gridbox_columns" style="width:75%;">
                 	<form:input cssClass="text"  path="catalog.name" tabindex="1" />
                 	<div class="main_addnew_formbox_errormsg" id="catalog.nameError" style="margin:10px 0 0 5px"></div>
                 </div>
			</div>
			
            <div class="db_gridbox_rows detailsodd">
             	<div class="db_gridbox_columns" style="width:20%;">
                 	<div class="db_gridbox_celltitles details"><strong><spring:message code="ui.products.label.create.description"/></strong></div>
                 </div>
                 <div class="nonmandatory_wrapper db_gridbox_columns" style="width:75%;">
                 	 <form:textarea cssClass="longtextbox" rows="3" cols="20" path="catalog.description" tabindex="2"></form:textarea>
                 </div>
			 </div>		 

             <div class="db_gridbox_rows detailsodd">
                <div class="db_gridbox_columns" style="width:20%;">
                    <div class="db_gridbox_celltitles details"><strong><spring:message code="ui.products.label.create.code"/></strong></div>
                 </div>
                 <div class="nonmandatory_wrapper db_gridbox_columns" style="width:75%;">
                    <form:input cssClass="text"  path="catalog.code" tabindex="1" />
                     <input id="catalog_code" type="hidden" value="<c:out  value="${catalogForm.catalog.code}" />"/>
                    <div class="main_addnew_formbox_errormsg" id="catalog.codeError" style="margin:10px 0 0 5px"></div>
                 </div>
            </div>

         <div class="db_gridbox_rows detailsodd">
            <div class="db_gridbox_columns" style="width:20%;">
                <div class="db_gridbox_celltitles details"><strong><spring:message code="ui.products.label.edit.channel"/></strong></div>
               </div>
               <div class="nonmandatory_wrapper db_gridbox_columns" style="width:75%;">
                  <div class="templatesCheckboxes">
                    <c:forEach items="${channels}" var="item" varStatus="status">
                      <div class="checkboxRow">
                        <input type="checkbox" name="channels"
                          <c:if test="${selectedChannels[item.id] != null}">
                            checked="true"
                          </c:if>
                          value="<c:out value="${item.param}"></c:out>"/>
                        <div><c:out value="${item.name}"></c:out></div>
                      </div>
                    </c:forEach>
                  </div>
               </div>
         </div> 
         
         <div class="db_gridbox_rows detailsodd">
              <div class="db_gridbox_columns" style="width:20%;">
                    <div class="db_gridbox_celltitles details"><strong><spring:message code="ui.products.label.create.catalog.select.currency"/></strong></div>
              </div>
             <div class="mandatory_wrapper db_gridbox_columns" style="width:75%;">
                <form:select  path="currencies" cssStyle="height:auto;" size="10" cssClass="select" multiple="multiple" tabindex="10" itemLabel="Supported Currencies">
                        <c:forEach var="supported_currency" items="${supportedCurrencies}" varStatus="status">
                            <option value="<c:out value="${supported_currency.currency.currencyCode}"></c:out>" disabled="disabled"><spring:message code="currency.longname.${supported_currency.currency.currencyCode}"></spring:message></option>
                      </c:forEach>
                      <c:forEach var="currency" items="${availableCurrencies}" varStatus="status">
                            <option value="<c:out value="${currency.currencyCode}"></c:out>"><spring:message code="currency.longname.${currency.currencyCode}"></spring:message></option>
                      </c:forEach>
                  </form:select>
               <div class="main_addnew_formbox_errormsg" id="currenciesError" style="margin:10px 0 0 5px"></div>
             </div>
        </div>
			   <input type="hidden" name="editcatalogflag" id="editcatalogflag" value="edit">
         </div>                            
     </div>
    <div class="maindetails_footerlinksbox" style="z-index: 0;">
       <p><a href="#" id="editcatalogcancel"><spring:message code="ui.products.label.create.cancel"/></a> <span > |</span> </p> <p>
       <input tabindex="210"  id="editcatalog" rel="<spring:message code="ui.products.label.edit.saving"/>"  class="commonbutton submitmsg" type="submit" value="<spring:message code="ui.products.label.edit.save"/>"/> </p>
    </div>
    
    </form:form>
</div>
<!--  Add Catalog ends here-->

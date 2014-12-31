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
    vertical-align : top;
}
</style>

  <!--  Add new Product starts here-->
<div class="main_addnewbox" style="display:block;">
	<div class="main_addnewbox_contentbox">
    	<div class="main_addnewbox_titlebox">
    		<h2><spring:message code="ui.products.label.create.catalog.name"/></h2>
            <p><span>*</span><spring:message code="label.moreUserInfo.mandatory"/></p>
    	</div>
    	<spring:url value="/portal/products/createcatalog" var="create_catalog_path" htmlEscape="false" /> 
            <form:form commandName="catalogForm" id="catalogForm" cssClass="ajaxform" action="${create_catalog_path}" onsubmit="addNewCatalog(event,this)">
         <div class="main_addnew_formbox">
         
                <div class="main_addnew_formpanels" style="border:none;">
                    <ol>
                        <li>
                            <form:label path="catalog.name" ><spring:message code="ui.products.label.create.name"/></form:label>
                            <div class="mandatory_wrapper">
                              <form:input cssClass="text"  path="catalog.name" tabindex="1" />
                            </div>
                             <div class="main_addnew_formbox_errormsg" id="catalog.nameError"></div>
                         </li>                         

                        <li>
                            <form:label path="channels" ><spring:message code="ui.products.label.create.channel.assignment"/></form:label>
                            <div class="nonmandatory_wrapper templatesCheckboxes">
                              <c:forEach items="${channels}" var="item" varStatus="status">
                                <div class="checkboxRow">
                                  <input type="checkbox" name="channels"
                                    value="<c:out value="${item.param}"></c:out>"/>
                                  <div><c:out value="${item.name}"></c:out></div>
                                </div>
                              </c:forEach>
                            </div>
                            
                         </li>                         

                        <li>
                       <div> <label><spring:message code="ui.products.label.create.catalog.select.currency"/></lable></div>
                         <div class="mandatory_wrapper">
                              <form:select path="currencies" cssStyle="height:auto;" size="10" cssClass="select" multiple="multiple" tabindex="60" itemLabel="Supported Currencies">
                                    <c:forEach var="currency" items="${availableCurrencies}" varStatus="status">
                                        <option value="<c:out value="${currency.currencyCode}"></c:out>"><spring:message code="currency.longname.${currency.currencyCode}"></spring:message></option>
                                    </c:forEach>
                                  </form:select>
                         </div>
                        <div class="main_addnew_formbox_errormsg" id="currenciesError"></div>
                            
                         </li>                         
                         
                    </ol>
                </div>
            
            <div class="main_addnew_formpanels" style="border-left:1px dotted #333333;border-right: none;">
            	<ol>
                       <li class="liRhs">
                           <form:label path="catalog.code" ><spring:message code="ui.products.label.create.catalog.code"/></form:label>
                           <div class="mandatory_wrapper">
                             <form:input cssClass="text"  path="catalog.code" tabindex="1" />
                             <input id="catalog_code" type="hidden" value="<c:out  value="${catalogForm.catalog.code}" />"/>
                           </div>
                           <div class="main_addnew_formbox_errormsg" id="catalog.codeError"></div>
                        </li>                         

                        <li class="liRhs">
                            <form:label path="catalog.description" ><spring:message code="ui.products.label.create.description"/></form:label>
                            <form:textarea cssClass="longtextbox" rows="3" cols="20" path="catalog.description" tabindex="2"></form:textarea>                            
                         </li>
            	</ol>
            </div>
        </div>
        
        <div class="main_addnew_submitbuttonpanel">
        	<div class="main_addnew_submitbuttonbox">
            	<a href="#" id="addnewcatalogcancel"><spring:message code="ui.products.label.create.cancel"/></a>
            	<input tabindex="100" id="addcatalog" rel="<spring:message code="ui.products.label.create.adding.catalog"/>"  class="commonbutton submitmsg" type="submit" value="<spring:message code="ui.products.label.create.add.catalog"/>"/>  
        	</div>
        </div>
         </form:form>
    </div>
</div>
<!--  Add new product ends here-->
                

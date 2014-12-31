<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
 <%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/entitlements.js"></script>
<script type="text/javascript">
var productBundlesUrl = "<%=request.getContextPath() %>/portal/productBundles/";
</script>

<jsp:include page="js_messages.jsp"></jsp:include>

    <spring:url value="/portal/productBundles/{bundleCode}/entitlement/create" var="create_entitle_path" htmlEscape="false" >
      <spring:param name="bundleCode"><c:out value="${productBundle.code}"/></spring:param>
    </spring:url>
    <form:form commandName="entitlementForm" cssClass="ajaxform" id="entitlementForm" 
         action="${create_entitle_path}"  onsubmit="addNewEntitlement(event, this)">
      <div class="widget_grid_cell" style="width: 34%;margin-top:7px;">
                    <div class="mandatory_wrapper" style="width: 100%;">
                    <form:select cssClass="select entitlement" id="productId" path="productId" cssStyle="min-width: 190px;width:90%;" onchange="hideUnlimitedEntitlementCheckBoxIfNecessary(this);">
                      <option class="noProductSelected _hypervisor" value=""><spring:message code="label.bundle.entitlement.create.choose.product"/></option>
                      <c:set var="current_service_instance_uuid" value="" />
                      <c:forEach items="${products}" var="choice" varStatus="status">
                        <c:if test="${ current_service_instance_uuid!= choice.serviceInstance.uuid}" >
                            <c:set var="current_service_instance_uuid" value="${choice.serviceInstance.uuid}" />
                            <optgroup label="${choice.serviceInstance.name}" class="highlight">
                        </c:if>
                        <option value=<c:out value="${choice.id}"/> unlimitedallow= "${choice.allowedUnlimitedEntitlement}" uom="${choice.uom}"> 
                          <c:out value="${choice.name}" />
                        </option>
                        <c:if test="${ current_service_instance_uuid!= choice.serviceInstance.uuid}" >
                            </optgroup>
                        </c:if>
                      </c:forEach>
                    </form:select>
                    <div class="main_addnew_formbox_errormsg" id="productIdError" style="margin:5px 0 0 10px"></div>
                    </div>
      </div>
      <div class="widget_grid_cell" style="width: 41%; margin-top: 7px">
        <div class="mandatory_wrapper" style="width: 100%;">
          <form:input path="entitlement.includedUnits" size="4" id="entitlement.includedUnits" cssStyle="margin-left:10px; width:50px;padding:0;"/>
          <span id="product_uom" style="margin-right: 20px;"></span>
          <span id="unlimitedEntitlementCheckBox" style="display:none;"><form:checkbox path="unlimitedUsage" cssStyle="margin:0px 5px 0px 0px;" id="unlimitedUsage" onchange="removeError()"/><spring:message code="label.bundle.list.entitlement.unlimited.usage"/></span>
          <div class="main_addnew_formbox_errormsg" id="entitlement.includedUnitsError" style="margin:5px 0 0 10px"></div>
        </div>
      </div>
      <div class="widget_grid_cell" style="width: 17%;">
        <div class="row_celltitles" style="padding:1px;"></div>
      </div>                  
      <div class="widget_grid_cell" style="width: 16%;">
        <div class="row_celltitles" style="padding:1px;"></div>        
      </div>                  
      <div class="widget_grid_cell" style="width: 7%;  margin-top: 3px">
        <a class="widget_addicon j_add_entitlement_link" id="add_link" href="javascript:void(0);" onclick="addNewEntitlement(event, this)"></a>
      </div>
    </form:form>

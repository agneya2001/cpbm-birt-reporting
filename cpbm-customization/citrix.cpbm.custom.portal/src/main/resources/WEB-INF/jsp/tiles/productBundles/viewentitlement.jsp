<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/ratecards.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/commonBundles.js"></script>
<jsp:include page="js_messages.jsp"></jsp:include>
<script type="text/javascript">
var JS_LOADED;
var productBundlesUrl = "<%=request.getContextPath() %>/portal/productBundles/";
</script>
<c:if test="${activeEntitlement == true}">
<div class="<c:out value="db_gridbox_rows ${rowClass}"/>"
            id="entitlementrootdiv<c:out value='${entitlement.id}' />" >
            <spring:message code="dateonly.format" var="dateonly_format"/>
            <div class="db_gridbox_columns" style="width:32%;">
                <div class="db_gridbox_celltitles">
                  <c:out value="${entitlement.product.name}"/></div>
              </div>
              <div class="db_gridbox_columns" style="width:23%;">
                <div class="db_gridbox_celltitles">
                
                
                  <div id="valuenotedit<c:out value="${entitlement.id}"/>">
                  
                   <c:choose>                
                    <c:when test="${entitlement.includedUnits == -1}">
                     <c:set var="includedunitsvalue" value="0"/>
                    <span style="font-weight: normal;"><spring:message code="label.bundle.list.entitlement.unlimited"/></span>
                    <span style="margin-left:5px;font-weight: normal;"> <spring:message code="${entitlement.product.uom}"/></span>
                    </c:when>
                    <c:otherwise>
                     <c:set var="includedunitsvalue" value="${entitlement.includedUnits}"/>
                    <c:out value="${entitlement.includedUnits}"/>
                    <span style="margin-left:5px;font-weight: normal;"> <spring:message code="${entitlement.product.uom}"/></span>
                    </c:otherwise>
                  </c:choose>
                  </div>  
                <div id="valueedit<c:out value="${entitlement.id}"/>" style="display: none;">
                
                
                
                <input type="text" size="10" id="value<c:out value="${entitlement.id}"/>" 
                  class="entitlementtext numberRequired" value="<c:out value="${includedunitsvalue}"/>"
                   name="value" />
                    <span style="margin-left:5px;font-weight: normal;"> <spring:message code="${entitlement.product.uom}"/></span>
                  <div class="main_addnew_formbox_errormsg" style="margin:0;width:180px;"  id="valueerror<c:out value="${entitlement.id}"/>"></div> 
                </div>                             
                </div>
              </div> 
              <div class="db_gridbox_columns" style="width:8%;">
                <div class="db_gridbox_celltitles">                     
                <div id="unlimitedvalueedit<c:out value="${entitlement.id}"/>" style="display: none;">
                <input  type="checkbox"  style="margin:0px 3px 3px 0px;" id="unlimitedUsage<c:out value="${entitlement.id}"/>" tabindex= "23" name="unlimitedUsage"
                 <c:if test="${entitlement.includedUnits == -1 }">  checked </c:if> >
                </div>
                
                </div>   
              </div>              
              <div class="db_gridbox_columns" style="width:11%;">
                <div class="db_gridbox_celltitles" id="entitlementStartDateDiv<c:out value="${entitlement.id}" />">
                  <fmt:formatDate value="${entitlement.startDate}" pattern="${dateonly_format}"/></div>
              </div>
              <div class="db_gridbox_columns" style="width:11%;">
                <div class="db_gridbox_celltitles">
                 <fmt:formatDate value="${entitlement.endDate}" pattern="${dateonly_format}" /></div>
              </div>
             <div class="db_gridbox_columns" style="width:14%;">
              <div class="db_gridbox_celltitles" style="float: right;">
                   <a class="editentitlement" href="javascript:void(0);" onclick="editEntitlement(this)" 
                   id="editpe<c:out value="${entitlement.id}" />" ><spring:message code="ui.products.label.view.edit"/></a>
                   <a class="deleteentitlement" href="javascript:void(0);" onclick="deleteEntitlement(this)" 
                   id="deletepe<c:out value="${entitlement.id}" />" ><span style="margin-left:2px;margin-right:5px;">|</span><spring:message code="label.bundle.edit.urc.delete"/></a>
                   <a class="saveentitlement" href="javascript:void(0);" onclick="saveEntitlement(this)" 
                   id="savepe<c:out value="${entitlement.id}" />"  style="display: none;"><spring:message code="ui.products.label.edit.save"/></a>
                   <a class="cancelentitlement" href="javascript:void(0);" onclick="cancelEntitlement(this)" 
                   id="cancelpe<c:out value="${entitlement.id}" />"  style="display: none;"><span style="margin-left:2px;margin-right:5px;">|</span><spring:message code="ui.products.label.create.cancel"/></a>
              </div>
            </div>   
            </div>
     </c:if>
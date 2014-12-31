<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="csrf" uri="http://www.owasp.org/index.php/Category:OWASP_CSRFGuard_Project/Owasp.CsrfGuard.tld"%>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/ratecards.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/commonBundles.js"></script>
<jsp:include page="js_messages.jsp"></jsp:include>
<script type="text/javascript">
  var productBundlesUrl = "<%=request.getContextPath() %>/portal/productBundles/";
</script>
<style>
.ui-datepicker-trigger {
    margin-left : 5px;
}
.main_addnew_formbox_errormsg label {
    width: 140px;
}
</style>

 <!--  Edit ProductBundle starts here-->
     <div class="main_details_titlebox">
              <h2><spring:message code="label.bundle.edit.urc.utility.rate.card"/></h2>
     </div>
      <div class="main_details_contentbox" style="width:98%;margin:0 0 0 9px">
    <!-- Edit rate card -->
    
    <spring:url value="/portal/productBundles/{catalogId}/editurc" var="edit_ratecard_path" htmlEscape="false">
      <spring:param name="catalogId"><c:out value="${catalog.id}"/></spring:param>
    </spring:url> 
    <form:form commandName="rateCardForm"  id="rateCardForm" action="${edit_ratecard_path}">
     <!-- Edit fields -->
      <div id="currentRateCardDiv">
      <div class="main_addnew_formbox_gridbox" style="width:100%" >
            <div class="editable_gridformbox">
            <div class="db_gridbox_rows detailsheader">
               <div class="db_gridbox_columns" style="width:25%;">
                    <div class="db_gridbox_celltitles header" style="float: left;"><spring:message code="label.bundle.edit.rc.start.date"/></div>
                  </div>                  
                  <div class="db_gridbox_columns" style="width:74%;">
                    <div class="db_gridbox_celltitles header" style="float: left;"><spring:message code="ui.products.label.create.description"/></div>
                  </div>                  
                  
            </div>                                      
               <div class="db_gridbox_rows dotted_odd" >               
                <div class="db_gridbox_columns" style="width:25%;">
                 <spring:message code="date.format" var="date_format"/>	
            <fmt:formatDate value="${date}" pattern="${date_format}"/>
                </div>               
                
                <div class="db_gridbox_columns" style="width:74%;">
                 <form:textarea cssClass="longtextbox" rows="2" cols="40" 
                              path="rateCard.description" tabindex="6"></form:textarea>   
                </div>             
              </div> 
             </div>
         </div>    
     
      <div class="main_addnew_formbox_gridbox" style="width:100%;">
            <div class="editable_gridformbox">
            <div class="db_gridbox_rows detailsheader">
            
                <div class="db_gridbox_columns" style="width:20%;">
                   <div class="db_gridbox_celltitles header"><spring:message code="label.bundle.edit.urc.product"/></div>
                </div> 
                <div class="db_gridbox_columns" style="width:10%;">
                   <div class="db_gridbox_celltitles header"><spring:message code="label.bundle.edit.urc.uom"/></div>
                </div>
                 
                 <c:forEach var="supportedCurrency" items="${catalog.supportedCurrenciesByOrder}" end="3">
                  <div class="db_gridbox_columns " style="width:17%;">
                   <div class="db_gridbox_celltitles header">
                   <spring:message code="label.price"/>(<c:out value="${supportedCurrency.currency.currencyCode}"></c:out>)
                   </div>
                </div>
                </c:forEach> 
            </div>
            
            
             <div id="rccDiv">
             
             <c:forEach var="rateCardChargesForm" items="${rateCardForm.rateCardChargesFormList}" varStatus="rccstatus">    
             <c:if test="${! empty rateCardChargesForm.rateCardComponent.chargeType}">                           
               <div class="db_gridbox_rows dotted_odd" id="rccrootdiv<c:out value='${rccstatus.index}' />">               
             
              <div class="db_gridbox_columns" style="width:20%;">
                  <div class="db_gridbox_celltitles">
                  <c:out value="${rateCardChargesForm.rateCardComponent.product.name}"/>
                  </div>
               </div> 
                <div class="db_gridbox_columns" style="width:10%;">
                  <div class="db_gridbox_celltitles">
                  <spring:message code="${rateCardChargesForm.rateCardComponent.product.uom}"/>
                  </div>
               </div>
                <c:forEach end="3" items="${rateCardChargesForm.rateCardCharges}" var="charge" varStatus="chargeStatus">
                   <div class="db_gridbox_columns" style="width:17%;">
                    <input class="text priceRequired"
                     id="rateCardChargesFormList<c:out value='${rccstatus.index}' />.rateCardCharges<c:out value='${chargeStatus.index}' />.price" 
                     type="text" tabindex="12" value="<c:out value='${charge.price}' />" 
                     name="rateCardChargesFormList[<c:out value='${rccstatus.index}' />].rateCardCharges[<c:out value='${chargeStatus.index}' />].price" />
                    <div class="main_addnew_formbox_errormsg" id="rateCardChargesFormList<c:out value='${rccstatus.index}' />.rateCardCharges<c:out value='${chargeStatus.index}' />.priceError" style="margin:5px 0 0 10px"></div>
                  </div>
                </c:forEach>
              </div> 
              </c:if>
             </c:forEach>
             </div>
         </div>
    </div>
    
    </div>
    
    <!-- end of edit rate card -->
     
    
    <div class="maindetails_footerlinksbox">
       <p>
       <a  style="padding:5px 15px 0 0" id="edit<c:out value='${ratecardedited}' />ratecardcancel" 
       href="<%=request.getContextPath()%>/portal/productBundles/<c:out value="${catalog.id}"/>/manageurc">
       <spring:message code="ui.products.label.create.cancel"/>
       </a>
        <span style="padding:5px 0 0 0;margin-left:10px;"> |</span>
         </p> 
       <p>
       <input type="hidden" name="<csrf:tokenname/>" value="<csrf:tokenvalue uri="portal/productBundles/${catalog.id}/editurc"/>"/>
       <input tabindex="210"   class="commonbutton submitmsg" rel="<spring:message code="ui.products.label.edit.saving"/>" type="submit" value="<spring:message code="ui.products.label.edit.save"/>"/> </p>
       <div class="main_addnew_formbox_errormsg" id="miscFormErrors" style="margin:5px 0 0 10px"></div>
    </div>    
    </form:form>
    </div>
<!--  Add ProductBundle ends here-->

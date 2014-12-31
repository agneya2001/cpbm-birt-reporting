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
<div class="main_detailsbox" style="width:100%;margin:0;">
    <spring:url value="/portal/productBundles/{catalogId}/ratecard/edit" var="edit_ratecard_path" htmlEscape="false">
    	<spring:param name="bundleId"><c:out value="${productBundle.id}"/></spring:param>
      <spring:param name="action"><c:out value="${ratecardedited}"/></spring:param>
    </spring:url> 
    <form:form commandName="rateCardForm"  id="rateCardForm" action="${edit_ratecard_path}" onsubmit="validateRateCardForm(event,this)">
     <!-- Edit fields -->
      <div class="main_details_contentbox" style="width:100%;margin:0;">
    	<div id="currentRateCardDiv">
    
    <!-- Edit rate card -->
     <div class="main_details_titlebox">
     	<c:choose>
     		<c:when test="${ratecardedited=='future'}">
            	<h2><spring:message code="label.ui.plan.rate.card"/></h2>
            </c:when>
            <c:when test="${ratecardedited=='current'}">
            	<h2><spring:message code="label.ui.edit.current.rate.card"/></h2>
            </c:when>
        </c:choose>
     </div>
      <div class="main_addnew_formbox_gridbox" style="width:100%;">
            <div class="editable_gridformbox">
            <div class="db_gridbox_rows detailsheader">
               <div class="db_gridbox_columns" style="width:35%;">
                    <div class="db_gridbox_celltitles header" style="float: left;"><spring:message code="label.bundle.edit.rc.start.date"/></div>
                  </div>                  
                  <div class="db_gridbox_columns" style="width:64%;">
                    <div class="db_gridbox_celltitles header" style="float: left;"><spring:message code="ui.products.label.create.description"/></div>
                  </div>                  
                  
            </div>                                      
               <div class="db_gridbox_rows dotted_odd" >               
	              <div class="db_gridbox_columns" style="width:35%;">
	              	<c:choose>
     					<c:when test="${ratecardedited=='future'}">
              <spring:message code="dateonly.format" var="dateonly_format"/>  
              <input type="text" id="startDate" name="startDate" class="text" tabindex="1" value="<fmt:formatDate  value="${date}" pattern="${dateonly_format}" />" />		               
			                <div class="main_addnew_formbox_errormsg" id="startDateError" 
			                style="margin:5px 0 0 10px"></div>
		                </c:when>
		                <c:when test="${ratecardedited=='current'}">
		                <spring:message code="dateonly.format" var="dateonly_format"/>	
		                <fmt:formatDate  value="${date}" pattern="${dateonly_format}" />
		                </c:when>
		            </c:choose>
	              </div>               
	              
	              <div class="db_gridbox_columns" style="width:64%;">
	               <form:textarea cssClass="longtextbox" rows="2" cssStyle="height:auto;width:auto;" cols="40" 
	                            path="rateCard.description" tabindex="6"></form:textarea>   
	              </div>             
              </div> 
             </div>
         </div>    
     <div id="row<c:out value='${ratecardsize}' />" class="currentRCCrow"></div> 
     
      <div class="main_addnew_formbox_gridbox" style="width:100%;">
            <div class="editable_gridformbox">
            
            
            
             <div class="db_gridbox_rows detailsheader">
                <div class="db_gridbox_columns" style="width:18%;">
               <div class="db_gridbox_celltitles header"><spring:message code="label.bundle.edit.rc.charge.type"/></div>
                </div>
                  <div class="db_gridbox_columns" style="width:18%;">
                   <div class="db_gridbox_celltitles header"><spring:message code="ui.products.label.create.description"/></div>
                </div>
                <c:forEach var="supportedCurrency" items="${productBundle.catalog.supportedCurrenciesByOrder}" end="3">
                  <div class="db_gridbox_columns " style="width:13%;">
                   <div class="db_gridbox_celltitles header">
                   <spring:message code="label.price"/>&nbsp;(<c:out value="${supportedCurrency.currency.currencyCode}"></c:out>)
                   </div>
                </div>
                </c:forEach> 
                 <div class="db_gridbox_columns" style="width:10%;">
                    <div class="db_gridbox_celltitles header"></div>
                  </div>
              </div>
            
             <div id="rccDiv">
             
            <c:forEach var="rateCardChargesForm" items="${rateCardForm.rateCardChargesFormList}" varStatus="rccstatus">    
             <c:if test="${! empty rateCardChargesForm.rateCardComponent.chargeType}">                           
               <div class="db_gridbox_rows dotted_odd" id="rccrootdiv<c:out value='${rccstatus.index}' />">               

              <div class="db_gridbox_columns" style="width:18%;">
                <select class="select chargeTypeSelect" tabindex="11" name="rateCardChargesFormList[<c:out value='${rccstatus.index}' />].chargeType" >
                <option value=""><spring:message code="label.bundle.edit.rc.choose.charge.type"/></option>
                  <c:forEach items="${rateCardForm.chargeRecurrenceFrequencyList}" var="charge" varStatus="status">
                    <option value=<c:out value="${charge.name}"/>
                    
                    <c:if test="${charge.name == rateCardChargesForm.chargeType}">
                          selected</c:if>     
                          >
                      <spring:message code="charge.type.${charge.name}"/>
                    </option>
                  </c:forEach>
                </select>
              </div>  
              <div class="db_gridbox_columns" style="width:18%;">
                 <textarea class="longtextbox" style="height:auto;width:auto;" id="rateCardChargesFormList<c:out value='${rccstatus.index}' />.rateCardComponent.description" 
                 rows="2" cols="20"  name="rateCardChargesFormList[<c:out value='${rccstatus.index}' />].rateCardComponent.description"  
                  tabindex="12"><c:out value="${rateCardChargesForm.rateCardComponent.description}" /></textarea>
              </div>
              
               <c:forEach end="3" items="${rateCardChargesForm.rateCardCharges}" var="charge" varStatus="chargeStatus">
                   <div class="db_gridbox_columns" style="width:13%;">
                    <input class="text priceRequired"
                     id="rateCardChargesFormList<c:out value='${rccstatus.index}' />.rateCardCharges<c:out value='${chargeStatus.index}' />.price" 
                     type="text" tabindex="12" value="<c:out value='${charge.price}' />" 
                     name="rateCardChargesFormList[<c:out value='${rccstatus.index}' />].rateCardCharges[<c:out value='${chargeStatus.index}' />].price" />
                    <div class="main_addnew_formbox_errormsg" id="rateCardChargesFormList<c:out value='${rccstatus.index}' />.rateCardCharges<c:out value='${chargeStatus.index}' />.priceError" style="margin:5px 0 0 10px"></div>
                  </div>
                </c:forEach> 
              <div class="db_gridbox_columns" style="width:10%;">
                <div class="db_gridbox_celltitles" style="float: right;margin:7px 30px 0 10px;">
                <a class="deletercc" href="javascript:void(0);" 
                onclick="deleteRateCardComponent(this,'current')" id="delete<c:out value='${rccstatus.index}' />"><spring:message code="label.delete"/></a></div>
                </div>
              </div> 
              </c:if>
             </c:forEach>
             
             <div class="db_gridbox_rows dotted_odd" id="rccrootdiv<c:out value='${ratecardsize}' />">               
              <div class="db_gridbox_columns" style="width:18%;">
                <select class="select chargeTypeSelect" tabindex="11" name="rateCardChargesFormList[<c:out value='${ratecardsize}' />].chargeType" >
                <option value=""><spring:message code="label.bundle.edit.rc.choose.charge.type"/></option>
                  <c:forEach items="${rateCardForm.chargeRecurrenceFrequencyList}" var="charge" varStatus="status">
                    <option value=<c:out value="${charge.name}"/>                                        
                    >
                      <spring:message code="charge.type.${charge.name}"/>
                    </option>
                  </c:forEach>
                </select>
              </div>                
              <div class="db_gridbox_columns" style="width:18%;">
                 <textarea class="longtextbox" style="height:auto;width:auto;" id="rateCardChargesFormList<c:out value='${ratecardsize}' />.rateCardComponent.description"
                  rows="2" cols="20" 
                          name="rateCardChargesFormList[<c:out value='${ratecardsize}' />].rateCardComponent.description"   tabindex="12"></textarea>
              </div> 
               <c:forEach end="3" items="${rateCardForm.rateCardChargesFormList[ratecardsize].rateCardCharges}" 
               var="charge" varStatus="chargeStatus">
                   <div class="db_gridbox_columns" style="width:13%;">
                    <input class="text priceRequired"
                     id="rateCardChargesFormList<c:out value='${ratecardsize}' />.rateCardCharges<c:out value='${chargeStatus.index}' />.price" 
                     type="text" tabindex="12" value="<c:out value='${charge.price}' />" 
                     name="rateCardChargesFormList[<c:out value='${ratecardsize}' />].rateCardCharges[<c:out value='${chargeStatus.index}' />].price" style="width:60%;"/>
                    <div class="main_addnew_formbox_errormsg" id="rateCardChargesFormList<c:out value='${ratecardsize}' />.rateCardCharges<c:out value='${chargeStatus.index}' />.priceError" style="margin:5px 0 0 10px">
                      <label for="rateCardChargesFormList<c:out value='${ratecardsize}' />.rateCardCharges<c:out value='${chargeStatus.index}' />.price" generated="true" class="error" style="width:160px;word-wrap:break-word;"></label>
                    </div>
                  </div>
                </c:forEach> 
             
              <div class="db_gridbox_columns" style="width:10%;">
                <div class="db_gridbox_celltitles" style="float: right;margin:7px 30px 0 10px;">
                <a class="add_icon" onclick="createRateCardComponent(this,'current')" 
                id="add_rate_card_component" href="javascript:void(0);"></a></div>
                </div>
              </div> 
             </div>
         </div>
    </div>
    
    </div>
    
    <!-- end of edit rate card -->
     </div>
    
    <div class="maindetails_footerlinksbox">
       <p>
       <a id="edit<c:out value='${ratecardedited}' />ratecardcancel" 
       style="padding:5px 15px 0 0" href="<%=request.getContextPath()%>/portal/productBundles/<c:out value="${productBundle.id}"/>/manage">
      <spring:message code="ui.products.label.create.cancel"/>
       </a>
        <span > |</span>
         </p> 
       <p>
       <input type="hidden" name="<csrf:tokenname/>" value="<csrf:tokenvalue uri="portal/productBundles/${productBundle.id}/ratecard/edit"/>"/>
       <input tabindex="210" id="editratecard"  class="commonbutton submitmsg" rel="<spring:message code="ui.products.label.edit.saving"/>" type="submit" value="<spring:message code="ui.products.label.edit.save"/>"/> </p>
       <div class="main_addnew_formbox_errormsg" id="miscFormErrors" style="margin:5px 0 0 10px"></div>
    </div>
    
    </form:form>
</div>
<!--  Add ProductBundle ends here-->

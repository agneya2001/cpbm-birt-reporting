<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<script language="javascript">
$(document).ready(function(){
  $("#advSrchCancel").click(function() {

    $("#advanceSearchDropdownDiv").hide();  
  });
  
  $("form[id^='searchForm']").find("#name").focus();
  
});
function cancelSearch(){
  $('#searchCompanyName').val($('#compName').val());
  $('#searchMasterUser').val($('#masterUser').val());
  $('#searchAccountId').val($('#accId').val());
	$("#advanceSearchDropdownDiv").hide();
}
function getResults(event,form){
  var valid = false;
  var formData= $(form).serialize();
  var formFields = formData.split('&');
  $.each(formFields, function(index, value) { 
    var field =  value.split("=");
    if(field[1] != ""){
      valid = true;
    }
  });
  if(!valid ){	
  	$("#atleastonefield").html('<spring:message javaScriptEscape="true" code="js.errors.searchfield.atleastone"/>');
  	return false;
  }else{
    return true;
  }
}
</script> 
<div class="clearboth"></div>
<div class="widget_actionpopover_top OSdropdown"></div>
<div class="widget_actionpopover_mid OSdropdown">
    <spring:url value="/portal/tenants/search" var="search_tenant_path" htmlEscape="false" /> 
    <form:form commandName="searchForm" id="searchForm" action="${search_tenant_path}" cssClass="ajaxform" method="GET" onsubmit="return getResults(event,this)">
        <div id="atleastonefield" class="errormsg" style="color: #FF0000;">
        </div>
        <ul class="widget_actionpoplist advancesearchdropdown">
          <li>
            <span class="label"><spring:message code="ui.label.search.companyName"/>:</span>
            <form:input id="searchCompanyName" cssClass="text" tabindex="1" path="name" value="${name}" />
          </li>
           <li>
            <span class="label"><spring:message code="ui.label.search.masterUser"/>:</span>
            <form:input id="searchMasterUser" cssClass="text" tabindex="2" path="fieldName" value="${fieldName}" />
          </li>
         <li>
            <span class="label"><spring:message code="ui.label.search.accountId"/>:</span>
            <form:input id="searchAccountId" cssClass="text" tabindex="3" path="accountId" value="${accountId}" />
          </li>
          <c:if test="${customFieldList != null}">
            <c:forEach items="${customFieldList}"  varStatus="status">
            <c:choose>
              <c:when test="${(3 + status.index) % 2 == 0}">
                <li style="display: inline;">
                  <span class="label"><spring:message htmlEscape="false" code="${customFieldList[status.index]}" />:</span>             
                  <form:input cssClass="text" path="customFields[${customFieldList[status.index]}]"/>  
                </li>
              </c:when>        
              <c:otherwise>
                <li style="display: inline;">
                  <span class="label"><spring:message htmlEscape="false" code="${customFieldList[status.index]}" />:</span>             
                  <form:input cssClass="text" path="customFields[${customFieldList[status.index]}]"/>  
                </li>
              </c:otherwise>
            </c:choose> 
            </c:forEach>
          </c:if>
      </ul>
      <input type="hidden" name="accountType" id="accountType" value="<c:out value="${selectedAccountType}"/>" />
      <input type="hidden" name="filterBy" id="filterBy" value="<c:out value="${filterBy}"/>" />
      <input type="hidden" name="compName" id="compName" value="<c:out value="${name}"/>" />
      <input type="hidden" name="masterUser" id="masterUser" value="<c:out value="${fieldName}"/>" />
      <input type="hidden" name="accId" id="accId" value="<c:out value="${accountId}"/>" />
      <input id="advSrchSubmit" class="submitbutton" tabindex="4" rel="<spring:message code="ui.label.search.rel.Searching"/>" type="submit" value="<spring:message code="ui.label.search.go"/>" />
      <input id="advSrchCancel" class="submitbutton" tabindex="5" onClick="javascript:cancelSearch();" type="button" value="<spring:message code="ui.label.search.cancel"/>" />
      
    </form:form>
    
</div>
<div class="widget_actionpopover_bot OSdropdown"></div>


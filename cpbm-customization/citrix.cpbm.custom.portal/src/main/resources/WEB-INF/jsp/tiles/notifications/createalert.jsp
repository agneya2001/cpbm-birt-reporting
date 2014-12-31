<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


<script type="text/javascript">
var alertsUrl = "<%=request.getContextPath() %>/portal/tenants/";

$.validator.addMethod(
    "regex",
    function(value, element, regexp) {
        var re = new RegExp(regexp);
        return this.optional(element) || re.test(value);
    },
    "Please check your input."
);

$("#subscriptionForm").validate( {
  // debug : true,
  success : "valid",
  ignoreTitle : true,
  rules : {     
    
    "tenantPercentage" : {
      required: true,
      percentage: true,
      regex: /^(\d)*(\.\d{1,2})?$/
    }
  },
  messages : {      
    "tenantPercentage" : {
      required : i18n.errors.tenantPercentage.required,
      percentage : i18n.errors.tenantPercentage.percentage,
      regex: i18n.errors.tenantPercentage.regex
    }
  },errorPlacement: function(error, element) {
    var name = element.attr('id');
      name =name.replace(".","\\.");      
        error.appendTo( "#"+name+"Error" );
  }
      
});

</script>

  <!--  Add new Alert starts here-->
<div class="dialog_formcontent">
  <form:form commandName="subscriptionForm" id="subscriptionForm">
    <ol>
         <li style="color:#333333;">
         <div>
            <spring:message code="label.alerts.current.budget.message"/>
            <c:out value="${tenant.currency.sign}" />
            <fmt:formatNumber pattern="${currencyFormat}" value="${tenant.spendBudget}"  minFractionDigits="${minFractionDigits}"/>
          </div>
         </li>                         
        <li>
            <form:label path="tenantPercentage" style="width:150px;"><spring:message code="label.alerts.new.message"/></form:label>
            <div class="mandatory_wrapper">
              <form:input cssClass="text greaterThanZero"  path="tenantPercentage" tabindex="1" style="margin:0px 0px 0px 10px;width:50px;"/>
            </div>
            <div class="main_addnew_formbox_errormsg" id="tenantPercentageError">
              <label for="tenantPercentage" generated="true" class="error" style="width:200px;padding-left:6px;"></label>
            </div>
               
           </li>
         
      </ol>
  <input id="type" type="hidden" name="type" value="<c:out value="${subscriptionForm.type}"/>"/>
  <input id="budgetid" type="hidden" name="budget" value="<c:out value="${tenant.spendBudget}"/>"/>
  </form:form>
  <input id="tenantId" type="hidden" name="tenant" value="<c:out value="${tenant.param}"/>"/>

</div>
<!--  Add new Alert ends here-->
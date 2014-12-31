<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="csrf" uri="http://www.owasp.org/index.php/Category:OWASP_CSRFGuard_Project/Owasp.CsrfGuard.tld"%>

<jsp:include page="js_messages.jsp"></jsp:include>
<script type="text/javascript">
var alertsUrl = "<%=request.getContextPath() %>/portal/tenants/";


$("#tenantForm").validate( {
  success : "valid",
  ignoreTitle : true,
  rules : {     
    "spendLimit" : {
      required : true,
    twoDecimal: true

    } ,
  "tenant.spendBudget" : {
        required : true,
        twoDecimal: true
      } 
  },
  messages : {      
    "spendLimit" : {
      required : i18n.errors.spendLimit.required
    },
  "tenant.spendBudget" : {
        required : i18n.errors.spendLimit.required,
        greaterThanZero:i18n.errors.spendLimit.greaterThanZero
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
	            <form:form commandName="tenantForm" id="tenantForm">
         
                    <ol>
                        <li>
                            <form:label path="spendLimit"><spring:message code="label.account.budget"/> (<c:out value="${tenant.currency.sign}" />)
                            </form:label>
                            <div class="mandatory_wrapper">
                            <span style="margin:2px 1px 0px 1px;">
                            
                            </span>
                            <form:input path="tenant.spendBudget" id="tenant.spendBudget" cssClass="text greaterThanZero" style="width:100px;"/>
                            
                            </div>
                            <div class="main_addnew_formbox_errormsg"  id="tenant.spendBudgetError">
                              <label for="tenant.spendBudget" generated="true" class="error" style="width:200px;padding-left:6px;"></label>
                            </div>
                         </li>                         
                        
                    </ol>
         
                <input type="hidden" name="<csrf:tokenname/>" value="<csrf:tokenvalue uri="portal/tenants/set_account_budget"/>"/>
            	
         </form:form>

</div>
<!--  Add new Alert ends here-->
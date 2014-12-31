<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<script type="text/javascript">
var alertsUrl = "<%=request.getContextPath() %>/portal/tenants/";

$("#editSubscriptionForm").validate( {
  // debug : true,
  success : "valid",
  ignoreTitle : true,
  rules : {     
    
    "tenantPercentage" : {
      required : true,
        percentage:true
    }     
  },
  messages : {      
    "tenantPercentage" : {
      required : i18n.errors.tenantPercentage.required,
      percentage : i18n.errors.tenantPercentage.percentage,
      remote : i18n.errors.tenantPercentage.remote
    }
  },errorPlacement: function(error, element) {
    var name = element.attr('id');
      name =name.replace(".","\\.");      
        error.appendTo( "#"+name+"Error" );
  }
      
});
</script>

 <!--  Edit Alert starts here-->
<div class="dialog_formcontent">
	<!-- Title -->
     
    <form:form commandName="subscriptionForm" id="editSubscriptionForm">
      <ol>
        <li>
          <form:label path="tenantPercentage" style="width:150px;"><spring:message code="label.listalerts.edit.alert.spend.reaches"/></form:label>
            <div class="mandatory_wrapper">
              <form:input cssClass="text greaterThanZero"  path="tenantPercentage" tabindex="1" style="margin:0px 0px 0px 10px;width:50px;"/>
            </div>
            <div class="main_addnew_formbox_errormsg" id="tenantPercentageError">
              <label for="tenantPercentage" generated="true" class="error" style="width:200px;padding-left:6px;"></label>
            </div>
			
     </li>
    </ol>
    
    </form:form>
</div>
<!--  Add Alert ends here-->

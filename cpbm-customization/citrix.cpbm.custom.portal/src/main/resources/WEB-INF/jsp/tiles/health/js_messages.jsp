<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<script language="javascript">
var i18n = {
  errors: {
    notificationType : {
      required : '<spring:message javaScriptEscape="true" code="js.errors.notificationType.required"/>'      
    },
    subject : {
      required : '<spring:message javaScriptEscape="true" code="js.errors.subject.required"/>'      
    },
    description : {
      required : '<spring:message javaScriptEscape="true" code="js.errors.description.required"/>'      
    }, 
    serviceInstance : {
      required : '<spring:message javaScriptEscape="true" code="js.errors.serviceInstance.required"/>'      
    },          
    startDateField : {
      required : '<spring:message javaScriptEscape="true" code="js.errors.startDateField.required"/>',
      date : '<spring:message javaScriptEscape="true" code="js.errors.startDateField.date"/>'      
    },
    endDateField : {
      required : '<spring:message javaScriptEscape="true" code="js.errors.endDateField.required"/>',
      date : '<spring:message javaScriptEscape="true" code="js.errors.endDateField.date"/>',
      validate : '<spring:message javaScriptEscape="true" code="js.errors.endDateField.validate"/>'
    },
    status: {
        failedCreateStatus : '<spring:message javaScriptEscape="true" code="js.errors.status.failedCreateStatus"/>',
        failedEditStatus : '<spring:message javaScriptEscape="true" code="js.errors.status.failedEditStatus"/>'
    },
    MaintenanceSchdule: {
      remove : '<spring:message javaScriptEscape="true" code="js.errors.MaintenanceSchdule.remove"/>'
    }    
  },
  confirm : {
    MaintenanceSchdule : {
    remove : '<spring:message javaScriptEscape="true" htmlEscape="false" code="js.confirm.MaintenanceSchdule.remove"/>'
   }
  },
  label : {
    NoMaintenanceSchdule: '<spring:message javaScriptEscape="true" code="js.label.NoMaintenanceSchdule"/>'
  }
};
</script>
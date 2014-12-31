<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/tenants.js"></script>
<script language="javascript">
i18n.errors.tenants = {
		credit: '<spring:message javaScriptEscape="true" code="js.errors.issuecredit.credit"/>',
		comment: '<spring:message javaScriptEscape="true" code="js.errors.issuecredit.comment"/>',
	    channelParam : '<spring:message javaScriptEscape="true" code="js.errors.tenants.channelParam"/>',
	    confirmEmail : '<spring:message javaScriptEscape="true" code="js.errors.tenants.confirmEmail"/>',
	    confirmEmailEqualTo : '<spring:message javaScriptEscape="true" code="js.errors.tenants.confirmEmailEqualTo"/>',
	    user : {
	      firstName : '<spring:message javaScriptEscape="true" code="js.errors.tenants.user.firstName"/>',
	      lastName : '<spring:message javaScriptEscape="true" code="js.errors.tenants.user.lastName"/>',
	      emailRequired : '<spring:message javaScriptEscape="true" code="js.errors.tenants.user.emailRequired"/>',
	      email : '<spring:message javaScriptEscape="true" code="js.errors.tenants.user.email"/>',
	      username : '<spring:message javaScriptEscape="true" code="js.errors.tenants.user.username"/>',
	      usernameRemote : '<spring:message javaScriptEscape="true" code="js.errors.tenants.user.usernameRemote"/>',
	      clearPassword : '<spring:message javaScriptEscape="true" code="js.errors.tenants.user.clearPassword"/>',
	      phone : '<spring:message javaScriptEscape="true" code="js.errors.tenants.user.phone"/>',
	      address : {
	        street1 : '<spring:message javaScriptEscape="true" code="js.errors.tenants.user.address.street1"/>',
	        city : '<spring:message javaScriptEscape="true" code="js.errors.tenants.user.address.city"/>',
	        postalCode : '<spring:message javaScriptEscape="true" code="js.errors.tenants.user.address.postalCode"/>',
	        state : '<spring:message javaScriptEscape="true" code="js.errors.tenants.user.address.state"/>',
	        country : '<spring:message javaScriptEscape="true" code="js.errors.tenants.user.address.country"/>'
	      }
	    },
	    passwordconfirm : '<spring:message javaScriptEscape="true" code="js.errors.tenants.passwordconfirm"/>',
	    passwordconfirmEqualTo : '<spring:message javaScriptEscape="true" code="js.errors.tenants.passwordconfirmEqualTo"/>',
	    tenant: {
	      name : '<spring:message javaScriptEscape="true" code="js.errors.tenants.tenant.name"/>',
	      country : '<spring:message javaScriptEscape="true" code="js.errors.tenants.tenant.country"/>',
	      state : '<spring:message javaScriptEscape="true" code="js.errors.tenants.tenant.state"/>',
	      street1 : '<spring:message javaScriptEscape="true" code="js.errors.tenants.tenant.street1"/>',
	      city : '<spring:message javaScriptEscape="true" code="js.errors.tenants.tenant.city"/>',
	      postalCode : '<spring:message javaScriptEscape="true" code="js.errors.tenants.tenant.postalCode"/>'
	    },
	    postalcode : '<spring:message javaScriptEscape="true" code="js.errors.tenants.postalcode"/>',
	    prePaidAmount : '<spring:message javaScriptEscape="true" code="js.errors.tenants.prePaidAmount"/>',
	    ChangeToCorporateDialog : {
	      title : '<spring:message javaScriptEscape="true" code="js.errors.tenants.ChangeToCorporateDialog.title"/>',
	      buttons : {
	        ok : '<spring:message javaScriptEscape="true" code="js.errors.tenants.ChangeToCorporateDialog.buttons.ok"/>',
	        cancel : '<spring:message javaScriptEscape="true" code="js.errors.tenants.ChangeToCorporateDialog.buttons.cancel"/>'
	      }
	    },
	    resourceLimitDialog : {
	      title : '<spring:message javaScriptEscape="true" code="js.errors.tenants.resourceLimitDialog.title"/>',
	      buttons : {
	        ok : '<spring:message javaScriptEscape="true" code="js.errors.tenants.resourceLimitDialog.buttons.ok"/>',
	        cancel : '<spring:message javaScriptEscape="true" code="js.errors.tenants.resourceLimitDialog.buttons.cancel"/>'
	      }
	    },
	    addCreditDialog : {
	      title : '<spring:message javaScriptEscape="true" code="js.errors.tenants.addCreditDialog.title"/>',
	      buttons : {
	        ok : '<spring:message javaScriptEscape="true" code="js.errors.tenants.addCreditDialog.buttons.ok"/>',
	        cancel : '<spring:message javaScriptEscape="true" code="js.errors.tenants.addCreditDialog.buttons.cancel"/>'
	      },
	      validate : {
	        amount : '<spring:message javaScriptEscape="true" code="js.errors.tenants.addCreditDialog.validate.amount"/>'
	      }
	    },
	    changeOwnerDialog : {
	      title : '<spring:message javaScriptEscape="true" code="js.errors.tenants.changeOwnerDialog.title"/>',
	      buttons : {
	        ok : '<spring:message javaScriptEscape="true" code="js.errors.tenants.changeOwnerDialog.buttons.ok"/>',
	        cancel : '<spring:message javaScriptEscape="true" code="js.errors.tenants.changeOwnerDialog.buttons.cancel"/>'
	      }
	    },
	    recordDepositDialog : {
	      title : '<spring:message javaScriptEscape="true" code="js.errors.tenants.recordDepositDialog.title"/>',
	      buttons : {
	        cancel : '<spring:message javaScriptEscape="true" code="js.errors.tenants.recordDepositDialog.buttons.cancel"/>',
	        save : '<spring:message javaScriptEscape="true" code="js.errors.tenants.recordDepositDialog.buttons.save"/>'
	      },
	      validate : {
	        receivedOn : '<spring:message javaScriptEscape="true" code="js.errors.tenants.recordDepositDialog.validate.receivedOn"/>',
	        amount : '<spring:message javaScriptEscape="true" code="js.errors.tenants.recordDepositDialog.validate.amount"/>'
	      }
	    },
	    resourceLimitForm : {
	      error : '<spring:message javaScriptEscape="true" code="js.errors.tenants.resourceLimitForm.error"/>',
	      maxProjects : '<spring:message javaScriptEscape="true" code="js.errors.tenants.resourceLimitForm.maxProjects"/>',
	      maxUsers : '<spring:message javaScriptEscape="true" code="js.errors.tenants.resourceLimitForm.maxUsers"/>'
	    },
	    tenantEditForm : {
	      title : '<spring:message javaScriptEscape="true" code="js.errors.tenants.tenantEditForm.title"/>',
	      saving : '<spring:message javaScriptEscape="true" code="js.errors.tenants.tenantEditForm.saving"/>',
	      save : '<spring:message javaScriptEscape="true" code="js.errors.tenants.tenantEditForm.save"/>',
	      error : '<spring:message javaScriptEscape="true" code="js.errors.tenants.tenantEditForm.error"/>'
	    },
	    issueCreditForm : {
	      title : '<spring:message javaScriptEscape="true" code="js.errors.tenants.issueCreditForm.title"/>',
	      saving : '<spring:message javaScriptEscape="true" code="js.errors.tenants.issueCreditForm.saving"/>',
	      error : '<spring:message javaScriptEscape="true" code="js.errors.tenants.issueCreditForm.error"/>',
	      invalidCreditBalance : '<spring:message javaScriptEscape="true" code="js.errors.tenants.issueCreditForm.invalidCreditBalance"/>'
	    },
	    changeStateForm : {
	      title : '<spring:message javaScriptEscape="true" code="js.errors.tenants.changeStateForm.title"/>',
	      saving : '<spring:message javaScriptEscape="true" code="js.errors.tenants.changeStateForm.saving"/>',
	      errors : '<spring:message javaScriptEscape="true" code="js.errors.tenants.changeStateForm.errors"/>'
	    },
	    cleantAccount : {
	      error : '<spring:message javaScriptEscape="true" code="js.errors.tenants.cleantAccount.error"/>'
	    },
	    removeTenant : {
	      error : '<spring:message javaScriptEscape="true" code="js.errors.tenants.removeTenant.error"/>'
	    },
	    editAccount : {
	      error : '<spring:message javaScriptEscape="true" code="js.errors.tenants.editAccount.error"/>'
	    },
	    accountLimitsForm: {
	      title : '<spring:message javaScriptEscape="true" code="js.errors.tenants.accountLimitsForm.title"/>',
	      save: '<spring:message javaScriptEscape="true" code="js.errors.tenants.accountLimitsForm.save"/>',
	      saving: '<spring:message javaScriptEscape="true" code="js.errors.tenants.accountLimitsForm.saving"/>',
	      error: '<spring:message javaScriptEscape="true" code="js.errors.tenants.accountLimitsForm.error"/>',
	      resourceLimit: {
	        valError: '<spring:message javaScriptEscape="true" code="js.errors.tenants.accountLimitsForm.resourceLimit.valError"/>'
	      }
	    }
	};
</script> 

<style>
.ui-datepicker-trigger {
    margin-left : 5px;
    margin-top : 5px;
}
</style>

 <!--  Issue credit starts here-->
<div class="dialog_formcontent" >
  <!-- Title -->
     <!-- div class="main_details_titlebox">
          <h2><spring:message code="ui.label.issueCredit.header"/><c:out value="${tenant.name}" /></h2>
     </div --> 
    <spring:url value="/portal/tenants/{tenant}/add_credit" var="issue_credit_path" htmlEscape="false" >
        <spring:param name="tenant"><c:out value="${tenant.param}"/></spring:param>
    </spring:url>         
     
    <form class="ajaxform" id="issueCreditForm" name="issueCreditForm" action="<c:out value="${issue_credit_path}"/>" onsubmit="issueCreditPost(event,this,'<c:out value="${tenant.param}"/>');">
     <!-- Edit fields -->
     <ul>
       <li>    
          <label for="name"><spring:message code="ui.label.issueCredit.credit"/> (<c:out value="${tenant.currency.sign}" />)</label>
          <div class="mandatory_wrapper">
          <textarea id="credit" name="credit" class="text" tabindex="1"></textarea>
          </div>
          <div class="main_addnew_formbox_errormsg" id="creditError"></div>                  
      </li>
      <li>
          <label for="name"><spring:message code="ui.label.issueCredit.comment"/></label>
          <div class="mandatory_wrapper">
          <textarea id="comment" name="comment" class="longtextbox" rows="3" cols="20" tabindex="2" style="width: 247px; height: 80px;"></textarea>
          </div>                              
          <div class="main_addnew_formbox_errormsg" id="commentError"></div>          
      </li>
      </ul>
    <div class="main_addnew_formbox_errormsg" id="miscFormErrors" style="margin:10px 0 0 140px"></div>
    <div class="main_addnew_submitbuttonpanel">
    <div class="main_addnew_submitbuttonbox">
       <a id="issuecreditcancel" onclick="closeIssueCreditDialog(this);" style="cursor:pointer;"><spring:message code="ui.label.issueCredit.cancel" /></a> 
       <input tabindex="3" id="issueCreditSave" name="issueCreditSave"  class="commonbutton submitmsg" rel="<spring:message code="ui.label.issueCredit.issueCredit.in.action"/>" type="submit" value="<spring:message code="ui.label.issueCredit.issueCredit"/>"/>
       
    </div>
    </div>
    </form>
</div>
<!--  Issue credit ends here-->
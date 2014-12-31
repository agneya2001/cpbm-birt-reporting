<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<script language="javascript">

i18n.change = {
  state: {
	  memo : '<spring:message javaScriptEscape="true" code="js.account.state.memo"/>',
	  memomaxlength : '<spring:message javaScriptEscape="true" code="js.account.state.memo.maxlength"/>'
  }
};

$(document).ready(function(){
  newStateChange($("#new_state"));
});

</script>

<script type="text/javascript" src="<%=request.getContextPath() %>/js/validateChangeAccountState.js"></script>

<style>
.ui-datepicker-trigger {
    margin-left : 5px;
    margin-top : 5px;
}
</style>

 <!--  Change state starts here-->
<div class="dialog_formcontent" >
  <!-- Title -->
     <spring:url value="/portal/tenants/{tenant}/changeState" var="change_tenant_state_path" htmlEscape="false" >
        <spring:param name="tenant"><c:out value="${tenant.param}"/></spring:param>
    </spring:url>
    <form class="ajaxform" id="changeStateForm" name="changeStateForm" action="<c:out value="${change_tenant_state_path}"/>" onsubmit="changeAccountState(event,this);">
     <!-- Edit fields -->
      <ul>
        <li>
            <label for="name"><spring:message code="ui.label.tenant.view.stateChanges.state"/></label>
            <div class="mandatory_wrapper">
               <select id="new_state" name="new_state" class="select" onchange="newStateChange(this);">
                  <c:forEach items="${tenant.nextStatesForUI}" var="ts">
                        <c:choose>
                        <c:when test="${tenant.state.name == ts.name}">
                          <c:set var="selected" value="selected=\"true\""/>
                        </c:when>
                        <c:otherwise>
                            <c:set var="selected" value=""/>
                        </c:otherwise>
                        </c:choose>
                        <option value="<c:out value="${ts.name}"/>" <c:out value="${selected}"/>>
                          <spring:message code="${ts.code}"/>
                        </option>  
                        </c:forEach>
                </select>
              </div>
              <div>
                <label id="label_new" class="label_state" style="display:none">
                  <spring:message code="tenant.changeState.new.description"/>
                </label>
                <label id="label_active" class="label_state" style="display:none">
                  <spring:message code="tenant.changeState.active.description"/>
                </label>
                <label id="label_locked" class="label_state" style="display:none">
                  <spring:message code="tenant.changeState.restricted.description"/>
                </label>
                <label id="label_suspended" class="label_state" style="display:none">
                  <spring:message code="tenant.changeState.suspended.description"/>
                </label>
                <label id="label_terminated" class="label_state" style="display:none">
                  <spring:message code="tenant.changeState.terminated.description"/>
                </label>
              </div>
              <div class="main_addnew_formbox_errormsg" id="" style="margin:10px 0 0 5px"></div>
        </li>
        <li>
              <label for="name"><spring:message code="ui.label.tenant.view.stateChanges.notes"/></label>
              <div class="mandatory_wrapper">
                 <textarea id="memo" name="memo" class="longtextbox" rows="3" cols="20"></textarea>    
              </div>
              <div class="main_addnew_formbox_errormsg" id="memoError" style="margin:0 0 0 128px"></div>
        </li>                    
      </ul>
      <div class="main_addnew_formbox_errormsg" id="miscFormErrors" style="margin:10px 0 0 5px"></div>
    
    <div class="main_addnew_submitbuttonpanel">
      <div class="main_addnew_submitbuttonbox">
        <a id="changestatecancel" onclick="closeChangeStateDialog(this);" style="cursor:pointer;"><spring:message code="label.cancel"/></a>
        <input tabindex="210" id="changeAccountStateSave" name="changeAccountStateSave" class="commonbutton submitmsg" rel="<spring:message code="label.saving"/>" type="submit" value="<spring:message code="label.save"/>"/>
      </div>
    </div>
    
    </form>
</div>
<!--  Change state ends here-->
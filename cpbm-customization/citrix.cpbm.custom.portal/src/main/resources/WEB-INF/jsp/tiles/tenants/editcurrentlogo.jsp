<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<script language="javascript">
var dictionary = { 
  logoName: '<spring:message javaScriptEscape="true" code="label.editcurrentlogo.logoName"/>'
};
</script>
<div class="dialog_formcontent">
       
   
 <div class="login_maincontentarea">
    <div class="login_maincontentarea_titlepanel">
        <h1><spring:message code="title.custom.tenant.image"/><c:out value="${tenantLogoForm.tenant.name}" /></h1>
          <p></p>
      </div>
      
      <div class="registration_formbox">
        <spring:url value="/portal/tenants/{tenantParam}/editlogo" var="edit_tenant_logo_path" htmlEscape="false">
              <spring:param name="tenantParam"><c:out value="${effectiveTenant.param}"/></spring:param>
     </spring:url>
    <form:form commandName="tenantLogoForm" cssClass="formPanel ajaxform"  action="${edit_tenant_logo_path}"  enctype="multipart/form-data">
              <ul>
                <li>
                  <form:label path="logo"><spring:message code="label.custom.tenant.image.logo"/></form:label>
                  <form:input type="file" path="logo" id="imglogo"/>
                  <div class="registration_formbox_errormsg" id="logoError"><form:errors path="logo"></form:errors></div>
                  <span id="logo_file_name" style="float:left; margin-left: 172px; margin-top:5px;display:none;"></span>
                </li>
                 <li>
                  <form:label path="favicon"><spring:message code="label.custom.tenant.image.favicon"/></form:label>
                  <form:input type="file" path="favicon" id="imgfavicon"/>
                  <div class="registration_formbox_errormsg" id="faviconError"><form:errors path="favicon"></form:errors></div>
                   <span id="favicon_file_name" style="float:left; margin-left:172px; margin-top:5px;display:none;"></span>
                </li>
              </ul>

            <div class="registration_formbox_errormsg"
               id="error_success">
               <spring:hasBindErrors name="tenantLogoForm">
                  <c:forEach var="error" items="${errors.globalErrors}">
                     </br>
                     <spring:message code="${error.code}"
                        arguments="${error.arguments}" />
                     <br>
                  </c:forEach>
               </spring:hasBindErrors>
            </div>

              <div class="login_formbox_submitpanel" style="width:400px;"> 
                <div class="register_buttonscontainer">
                <div style="margin: 5px 10px;float:left;">
                <a href="#" onClick="editCurrentLogoCancel()"><spring:message code="label.custom.tenant.cancel"/></a></div>
                <input tabindex="220" rel="<spring:message code="label.custom.tenant.customizing.account"/>" class="commonbutton submitmsg" type="submit" value="<spring:message code="label.custom.tenant.submit"/>"/>
                </div>
                
               </div>
             
          </form:form>
          
          
      </div>
  
                
</div>
  
    </div>           

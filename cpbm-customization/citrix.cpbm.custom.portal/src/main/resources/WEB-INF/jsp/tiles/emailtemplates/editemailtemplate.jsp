<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<c:if test="${not empty templateName}">
<div id="edit_email_template" title="<spring:message code='label.edit.email.template'/>&nbsp;-&nbsp;<c:out value='${templateName}'/>" style="display: none;">
  <div class="dialog_formcontent">
    <div class="details_lightboxtitlebox">
    </div> 
      <!--new starts here-->
      <div class="details_lightboxformbox">
        <ul> 
          <li>
              <label><spring:message code="label.details"/></label>
              <br/>
              <div class="nonmandatory_wrapper">
                <textarea id="email_template_text" style="overflow: auto" cols="135" rows="20"><c:out value="${emailText}" escapeXml="true" /></textarea>
              </div>
          </li>
        </ul>
    </div> 
  </div>
</div>
</c:if>

<!-- End view Email Template Details -->
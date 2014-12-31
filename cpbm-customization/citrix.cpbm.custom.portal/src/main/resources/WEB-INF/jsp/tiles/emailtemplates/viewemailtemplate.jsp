<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/date.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/date.format.js"></script>
<script language="javascript">
$(document).ready(function(){
  $("#details_tab").unbind("click").bind("click", function (event) {
    $("#details_tab").removeClass("nonactive").addClass("active").show();
    $("#preview_tab").removeClass("active").addClass("nonactive").show();
    $("#details_div").show();
    $("#preview_div").hide();
  });

  $("#preview_tab").unbind("click").bind("click", function (event) {
    $("#preview_tab").removeClass("nonactive").addClass("active").show();
    $("#details_tab").removeClass("active").addClass("nonactive").show();
    $("#preview_div").show();
    $("#details_div").hide();
  });
    
});
</script>
<div class="widget_actionbar">
  <div class="widget_actionarea" id="top_actions">
    <div id="spinning_wheel" style="display:none">
      <div class="maindetails_footer_loadingpanel">
      </div>
      <div class="maindetails_footer_loadingbox first">
        <div class="maindetails_footer_loadingicon"></div>
        <p id="in_process_text"></p>
      </div>
    </div>
    <div class="widget_moreactions action_menu_container" title="<spring:message code='manage'/>">
      <!--Actions popover starts here-->
      <div class="widget_actionpopover" id="action_menu" style="display:none;">
        <div class="widget_actionpopover_top"></div>
        <div class="widget_actionpopover_mid">
          <ul class="widget_actionpoplist">
            <li id="editemailtemplate_action"><a onclick='editemailtemplate("<c:out value="${templateName}"/>")'><spring:message code="label.edit"/></a></li>
            <li id="sendemailtemplate_action" style="display:none;"><a onclick='sendemailtemplate("<c:out value="${templateName}"/>")'><spring:message code="email.send.test.mail"/></a></li>
            <c:if test="${template.channel ne null}">
            <li id="reset_to_default"><a onclick='reset_to_default("<c:out value="${template.id}"/>",this,"<c:out value="${template.templateName}"/>", "<c:out value="${template.category}"/>")'><spring:message code="label.reset.to.default"/></a></li>
            </c:if>
          </ul>
        </div>
        <div class="widget_actionpopover_bot"></div>
      </div>
    </div>
  </div>
</div>
<div class="top_notifications">
  <div id="top_message_panel" class="common_messagebox widget" style="display:none;">
    <button type="button" class="close js_close_parent" >&times;</button>
    <span id="status_icon"></span><p id="msg"></p>
  </div>
  <div id="action_result_panel" class="common_messagebox widget" style="display:none;">
    <button type="button" class="close js_close_parent" >&times;</button>
    <span id="status_icon"></span><p id="msg"></p>
  </div>
</div>
<div class="widget_browser">
  <div class="widget_browsermaster">
    <div class="widget_browser_contentarea">
    <spring:message code="date.format" var="date_format"/>
      <div class="widget_browsergrid_wrapper master">
          <div class="widget_grid master even first">
            <div class="widget_grid_labels">
              <span><spring:message code="ui.email.templates.page.name"/></span>
            </div>
            <div class="widget_grid_description">
              <span class="ellipsis"  title='<spring:message code="message.emailtemplate.${template.templateName}"/>'><spring:message code="message.emailtemplate.${template.templateName}"/></span>
            </div>
          </div>
          
          <div class="widget_grid master even">
            <div class="widget_grid_labels">
              <span><spring:message code="label.email.templates.overridden.check"/></span>
            </div>  
            <div class="widget_grid_description">
              <span>
              <c:choose>
                <c:when test="${template.channel ne null}">
                <spring:message code="label.yes"/>
                </c:when>
                <c:otherwise>
                <spring:message code="label.no"/>
                </c:otherwise>
              </c:choose>
              
              </span>
            </div>
          </div>
          
          <div class="widget_grid master even">
            <div class="widget_grid_labels">
              <span><spring:message code="ui.email.templates.page.last.updated.at"/></span>
            </div>  
            <div class="widget_grid_description" id="email_last_updated_at">
              <span>
                <fmt:formatDate value="${template.lastUpdatedAt}" pattern="${date_format}" type="date" dateStyle="MEDIUM"/>
              </span>
            </div>
          </div>
          
      </div>
      <div class="widget_masterbigicons ${fn:toLowerCase(template.category)}"></div>
    </div>
  </div>

  <div class="widget_browser_contentarea">
    <ul class="widgets_detailstab">
      <li class="widgets_detailstab active" id="details_tab">
         <spring:message code="label.details"/>
      </li>
      <c:if test="${template.category != 'INCLUDES'}">
        <li class="widgets_detailstab nonactive" id="preview_tab">
              <spring:message code="ui.email.templates.preview"/>
        </li>        
      </c:if>
    </ul>
    <div id="details_div">
      <div class="widget_details_actionbox">
        <ul class="widget_detail_actionpanel">
          <li>
            <a href="javascript:void(0);" onclick='editemailtemplate("<c:out value="${templateName}"/>")' class="editAccountLimits" id="editemailtemplate_action2"><spring:message code="label.edit"/></a>
          </li>
        </ul>
      </div>
      <div class="widget_browsergrid_wrapper details" id="details_content">
        <div class="widget_grid details even">
          <div class="widget_grid_labels">
            <span><spring:message code="ui.email.templates.page.description"/></span>
          </div>
          <div class="widget_grid_description" id="viewEmailTemplateDetailDiv">
            <span>
              <spring:message code="ui.emailtemplate.${template.templateName}.description"/>
            </span>
          </div>
        </div>
      </div>
    </div>
    <c:if test="${template.category != 'INCLUDES'}">
    <div id="preview_div" style="display:none;">
      <div class="widget_details_actionbox">
        <ul class="widget_detail_actionpanel">
          <li>
            <a href="javascript:void(0);" onclick='editemailtemplate("<c:out value="${templateName}"/>")' class="editAccountLimits" id="editemailtemplate_action3"><spring:message code="label.edit"/></a>
          </li>
        </ul>
      </div>
      <div class="widget_browsergrid_wrapper details" id="details_content" style="border-bottom:1px solid #CCC;">
      <div class="widget_grid emailtemplate" id="viewEmailTemplatePreviewDiv" style="width:99%">
      <c:if test="${not empty templateName}">
        <c:choose>
          <c:when test="${parseError}">
            <c:out value="${emailText}" escapeXml="true" />
          </c:when>
          <c:otherwise>
            <c:out value="${emailText}" escapeXml="false" />
          </c:otherwise>
        </c:choose>
      </c:if>
      </div>
      </div>
    </div>
    </c:if>
  </div>
</div>

<c:if test="${not empty templateName}">
<div id="email_id" title='<spring:message code="email.send.test.mail"/>' style="display: none;">
  <div class="dialog_formcontent">
    <div class="details_lightboxtitlebox">
    </div> 
    <div class="details_lightboxformbox">
      <label><spring:message code="label.userInfo.confirmEmail"/></label>
      <div class="mandatory_wrapper"><input id="confirm_email_id" type="text" class="text" value="<c:out value="${EmailId}"/>"></input></div>
      <div id="confirm_email_id_error" class="errormsg" style="display: none;"></div>
    </div> 
  </div>
</div>
</c:if>

<!-- End view Email Template Details -->


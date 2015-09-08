<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/emailtemplate.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/date.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/date.format.js"></script>
<script language="javascript">
var templatePageUrl = "<%=request.getContextPath() %>/portal/admin/email_templates";
var filterBy = "<c:out value="${filterBy}"/>";
var totalpages = "<c:out value="${totalpages}"/>";
var currentPage = "<c:out value="${currentPage}"/>";
var perPageValue = "<c:out value="${perPage}"/>";
var selectedLanguage = "<c:out value="${selectedLanguage}"/>";
var dictionary = { 
  emailTemplateSendMessage: '<spring:message javaScriptEscape="true" code="message.emailtemplate.send"/>',
  sendTestEmailLabel: '<spring:message javaScriptEscape="true" code="email.send.test.mail"/>',
  resetToDefaultSuccess: '<spring:message javaScriptEscape="true" code="message.success.reset.to.default"/>',
  resetToDefaultError: '<spring:message javaScriptEscape="true" code="message.error.reset.to.default"/>',
  resetToDefaultConfirmation: '<spring:message javaScriptEscape="true" code="message.confirmation.reset.to.default"/>',
  editTemplateSuccess: '<spring:message javaScriptEscape="true" code="message.template.edit.success"/>'
};
</script>


<div class="widget_box">
  <div class="widget_leftpanel">
    <div class="widget_titlebar">
      <h2>
        <span class=""><spring:message code="label.list.all"/></span>
      </h2>
    </div>
    <div class="commonboxes_formbox_small">
      <li style="border: none;">
        <label for="">
          <spring:message code="label.channel.tooltip" />
        </label>
        <div id="editChannel">
          <spring:message code="label.channel.tooltip" var="channel" />
          <select tabindex="200" class="select" id="channellist" name="channel"
            title="<c:out value="${channelTooltip}"/>">
            
            <c:if test="${empty selectedChannel}">
	            <option value='' selected>
	              <spring:message code="label.default.configurations" />
	            </option>
            </c:if>
            <c:if test="${not empty selectedChannel}">
              <option value=''>
                <spring:message code="label.default.configurations" />
              </option>
            </c:if>
            <optgroup label="<spring:message code="label.channels" />"
              class="highlight">
              <c:forEach items="${channels}" var="channel"
                varStatus="status">
                <c:choose>
                  <c:when test="${selectedChannel eq channel.id}">
                    <option value='<c:out value="${channel.id}"/>'
                      selected>
                      <c:out value="${channel.name}"></c:out>
                    </option>
                  </c:when>
                  <c:otherwise>
                    <option value='<c:out value="${channel.id}" />'>
                      <c:out value="${channel.name}"></c:out>
                    </option>
                  </c:otherwise>
                </c:choose>
              </c:forEach>
            </optgroup>
          </select>
        </div>
      </li>
      <li style="border: none;">
        <label for="">
          <spring:message code="label.language.emailtemplate.tooltip" />
        </label>
        <div id="editLanguage">
          <spring:message code="label.language.tooltip" var="i18nLanguageTooltip" />
          <select tabindex="200" class="select" id="localelist" name="locale"
            title="<c:out value="${i18nLanguageTooltip}"/>">
            <c:forEach items="${supportedLocaleList}" var="locale" varStatus="status">
              <c:choose>
                <c:when test="${selectedLanguage eq locale.key}">
                  <option value='<c:out value="${locale.key}"/>' selected>
                    <c:out value="${locale.value}"></c:out>
                  </option>
                </c:when>
                <c:otherwise>
                  <option value='<c:out value="${locale.key}" />'>
                    <c:out value="${locale.value}"></c:out>
                  </option>
                </c:otherwise>
              </c:choose>
            </c:forEach>
          </select>
        </div>
      </li>
    </div>
    <div class="widget_navigation">
      <ul class="widget_navigationlist" id="grid_row_container">
      <c:choose>
       <c:when test="${empty templates || templates == null}">
        <li class="widget_navigationlist nonlist" id="non_list">
          <c:if test="${filterBy==0}">
            <span id="nav_icon" class="navicon email"></span>
            <div class="widget_navtitlebox">
              <span class="newlist"> <spring:message code="message.emailtemplate.no.Emails.found"/></span>
            </div>
          </c:if>
          <c:if test="${filterBy==1}">
            <span id="nav_icon" class="navicon includes"></span>
            <div class="widget_navtitlebox">
              <span class="newlist"> <spring:message code="message.emailtemplate.no.Includes.found"/></span>
            </div>
          </c:if>
          <c:if test="${filterBy==2}">
            <span id="nav_icon" class="navicon styles"></span>
            <div class="widget_navtitlebox">
              <span class="newlist"> <spring:message code="message.emailtemplate.no.Styles.found"/></span>
            </div>
          </c:if>
          <c:if test="${filterBy==3}">
            <span id="nav_icon" class="navicon invoices"></span>
            <div class="widget_navtitlebox">
              <span class="newlist"> <spring:message code="message.emailtemplate.no.Invoices.found"/></span>
            </div>
          </c:if>
		  <c:if test="${filterBy==4}">
            <span id="nav_icon" class="navicon contents"></span>
            <div class="widget_navtitlebox">
              <span class="newlist"> <spring:message code="message.emailtemplate.no.Contents.found"/></span>
            </div>
          </c:if>          
        </li>
       </c:when>
       <c:otherwise>
          <c:forEach var="template" items="${templates}"  varStatus="status">
            <c:set var="templateListLen" value="${templateListLen + 1}"/>
            <c:choose>
              <c:when test="${status.index == 0}">
                <c:set var="firsttemplate" value="${template}" />
                <c:set var="selected" value="selected" />
                <c:set var="active" value="active"/>
              </c:when>
              <c:otherwise>
                <c:set var="selected" value="" />
                <c:set var="active" value=""/>
              </c:otherwise>
            </c:choose>
             <li class="<c:out value="widget_navigationlist ${selected} ${active} "/>" id="<c:out value="row${template.id}"/>" name="<c:out value="${template.templateName}"/>" category="<c:out value="${template.category}"/>"
                onclick='viewemailtemplate(this,"<c:out value="${template.templateName}"/>", "<c:out value="${template.category}"/>")' onmouseover="onTemplateMouseover(this);" onmouseout="onTemplateMouseout(this);">
              <c:if test="${filterBy==0}">
                <span id="nav_icon" class="navicon email"></span>
              </c:if>
              <c:if test="${filterBy==1}">
                <span id="nav_icon" class="navicon includes"></span>
              </c:if>
              <c:if test="${filterBy==2}">
                <span id="nav_icon" class="navicon styles"></span>
              </c:if>
              <c:if test="${filterBy==3}">
                <span id="nav_icon" class="navicon invoices"></span>
              </c:if>
              <c:if test="${filterBy==4}">
                <span id="nav_icon" class="navicon contents"></span>
              </c:if>
              <div class="widget_navtitlebox <c:out value="db_gridbox_rows"/>">
                <span class="title">
                <spring:message code="message.emailtemplate.${template.templateName}"/>
                </span>
                <span class="subtitle">
                  <spring:message code="ui.email.templates.page.category"/>: <spring:message code="ui.email.templates.type.${fn:toLowerCase(template.category)}"/>
                </span>
              </div>
              <c:if test="${template.channel ne null}">
              
                <i class="fa fa-edit" style="margin-top:14px;" title='<spring:message code="label.email.templates.overridden"/>'></i>
              </c:if>
              <div class="widget_info_popover" id="info_bubble" style="display:none">
                <div class="popover_wrapper" >
                <div class="popover_shadow"></div>
                <div class="popover_contents">
                  <div class="raw_contents">
                    <div class="raw_content_row">
                      <div class="raw_contents_title">
                        <span><spring:message code="ui.email.templates.page.name"/>:</span>
                      </div>
                      <div class="raw_contents_value">
                        <span>
                          <spring:message code="message.emailtemplate.${template.templateName}"/>
                        </span>
                      </div>
                    </div>
                    <div class="raw_content_row">
                      <div class="raw_contents_title">
                        <span><spring:message code="ui.email.templates.page.category"/>:</span>
                      </div>
                      <div class="raw_contents_value">
                        <span>
                          <spring:message code="ui.email.templates.type.${fn:toLowerCase(template.category)}"/>
                        </span>
                      </div>
                    </div>
                    <div class="raw_content_row">
                      <div class="raw_contents_title">
                        <span><spring:message code="ui.email.templates.page.description"/>:</span>
                      </div>
                      <div class="raw_contents_value">
                        <span>
                          <spring:message code="ui.emailtemplate.${template.templateName}.description"/>
                        </span>
                      </div>
                    </div>
                  </div>
                </div>
                </div>
              </div>
            </li>        
          </c:forEach>  
       </c:otherwise>
      </c:choose>   
      </ul>
    </div>
    <script type="text/javascript">
      var templateListLen = "<c:out value="${templateListLen}"/>";
    </script>
    <div class="widget_panelnext">
      <div class="widget_navnextbox">
        <a class="widget_navnext_buttons prev nonactive" href="javascript:void(0);" id="click_previous"><spring:message code="label.previous.short"/></a>
        <a class="widget_navnext_buttons next" href="javascript:void(0);" id="click_next"><spring:message code="label.next"/></a>
      </div>
    </div>
  </div>
  
  <input type="hidden" value="${selectedLanguage}" name="selectedLang" id="selected_locale"/>
  <input type="hidden" value="${selectedChannel}" name="selectedChannel" id="selected_channel"/>
  
  <div id="<c:out value="count${size}"/>" class="countDiv"></div>
  <div class="widget_rightpanel" id="viewEmailTemplateDiv">
    <c:if test="${firsttemplate != null}">                                             
      <script>
        var t_name = '<c:out value="${firsttemplate.templateName}"/>';
        var t_category = '<c:out value="${firsttemplate.category}"/>';
        var t_lang = '<c:out value="${selectedLanguage}"/>';
        viewemailtemplate(null, t_name, t_category, t_lang, null); 
      </script>
    </c:if> 
  </div>
</div>

<div id="editEmailTemplateDiv"></div>


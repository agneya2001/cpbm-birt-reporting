<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<jsp:include page="/WEB-INF/jsp/tiles/accounttypes/js_messages.jsp"></jsp:include>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/accounttype.js"></script>
<script language="javascript">
   function writeDesc(sra, ma){
	   document.write(makeDesc(sra, ma));
   }
   function makeDesc(sra, ma){
     var str = "";
     if(sra && sra != "false"){
       str = '<spring:message javaScriptEscape="true" code="ui.accounttypes.list.page.selfserve"/>:<spring:message javaScriptEscape="true" code="label.ok" />;';
     }
     str += '<spring:message javaScriptEscape="true" code="ui.accounttypes.list.page.mu"/>:' + ma;
     return str;
    }
</script>

<div class="widget_box" id="health_status_div">
    <div class="widget_leftpanel">
        <div class="widget_titlebar">
          <h2><spring:message code="label.list.all"/></h2>
        </div>
        <div class="widget_searchpanel">
          <div style="margin:6px 0 0 10px;">
          </div>
        </div>
        <div class="widget_navigation">
          <ul class="widget_navigationlist" id="grid_row_container">
            <c:forEach var="accountType" items="${accountTypesList}" varStatus="status">
                <c:choose>
                  <c:when test="${status.index == 0}">
                      <c:set var="firstAccountType" value="${accountType}"/>
                      <c:set var="selected" value="selected"/>
                      <c:set var="active" value="active"/>
                  </c:when>
                  <c:otherwise>
                      <c:set var="selected" value=""/>
                      <c:set var="active" value=""/>
                  </c:otherwise>
                </c:choose>
                <li class="<c:out value="widget_navigationlist ${selected} ${active}"/>" id="row<c:out value="${accountType.id}"/>"  onclick="viewaccounttype(this);" onmouseover="onAccountTypeMouseover(this);" onmouseout="onAccountTypeMouseout(this);">
                     <input type="hidden" value="<spring:message code="registration.accounttype.${accountType.nameLower}"/>" id="accounttype_name<c:out value="${accountType.id}"/>" />
                     <span id="nav_icon" class='navicon accounts <c:out value="${accountType.nameLower}" />'></span>
                     <div class="widget_navtitlebox <c:out value="db_gridbox_rows"/>">
                       <span class="title">
                         <spring:message code="registration.accounttype.${accountType.nameLower}"/>
                       </span>
                       <span id="accounnttype_desc<c:out value="${accountType.id}"/>" class="subtitle">
                          <script language="javascript">
                              writeDesc('<c:out value="${accountType.selfRegistrationAllowed}"/>', '<c:out value="${accountType.maxUsers}"/>');
                          </script>
                       </span>
                     </div>
                     <c:choose>
                      <c:when test="${(accountType.trial)}">
                       <div class="widget_statusicon nostate"></div>
                      </c:when>
                      <c:otherwise>
                       <div class="widget_statusicon running"></div>
                      </c:otherwise>
                    </c:choose>  
                     
                     <!--Info popover starts here-->
                     <div class="widget_info_popover" id="info_bubble" style="display:none">
                       <div class="popover_wrapper" >
                       <div class="popover_shadow"></div>
                       <div class="popover_contents">
                         <div class="raw_contents">
                           <div class="raw_content_row">
                             <div class="raw_contents_title">
                               <span><spring:message code="ui.accounttypes.list.page.name"/>:</span>
                             </div>
                             <div class="raw_contents_value">
                               <span>
                                 <spring:message code="registration.accounttype.${accountType.nameLower}"/>
                               </span>
                             </div>
                           </div>
                           <div class="raw_content_row">
                             <div class="raw_contents_title">
                               <span><spring:message code="ui.accounttypes.list.page.desc"/>:</span>
                             </div>
                             <div class="raw_contents_value">
                               <span>
                                 <spring:message code="registration.accounttype.description.${accountType.nameLower}"/>
                               </span>
                             </div>
                           </div>
                         </div>
                       </div>
                       </div>
                     </div>
                     <!--Info popover ends here-->
                   </li>
              </c:forEach>
          </ul>
        </div>
    </div>
    <div class="widget_rightpanel" id="viewaccounttypeDiv">
      <c:if test="${firstAccountType != null}">
        <script>
        var li_id = "<c:out value="row${firstAccountType.id}"/>";
        viewaccounttype($("#" + li_id));
        </script>
      </c:if> 
    </div>
    <div id="editAccountTypeDivDetail" title="<spring:message code="label.editaccounttype.edit"/>" style="height:300px;">
    </div>
    <div id="editAccountTypeDivOnBoard" title="<spring:message code="label.editaccounttype.edit"/>">
    </div>
    <div id="editAccountTypeDivServiceControls" style="overflow:hidden" title="<spring:message code="label.editaccounttype.edit"/>">
    </div>
    <div id="editAccountTypeDivBilling" title="<spring:message code="label.editaccounttype.edit"/>">
    </div>
    <div id="creditExposureLimitsDiv" title="<spring:message code="ui.accounttypes.edit.credit.exposure.title"/>">
    </div>
    <div id="initialDepositEditDiv" title="<spring:message code="ui.accounttypes.edit.initial.deposit.title"/>">   
    </div>
</div>

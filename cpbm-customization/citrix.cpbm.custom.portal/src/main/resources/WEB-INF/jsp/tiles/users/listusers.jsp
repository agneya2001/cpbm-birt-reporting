<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ page import="com.vmops.model.Tenant" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<script type="text/javascript">
 var usersUrl = "<%=request.getContextPath() %>/portal/users/";

<%String addNewUserUrll = (String)request.getContextPath()+"/portal/users/new/step1?tenant="+(String)(((Tenant)request.getAttribute("tenant")).getParam());%>
var addNewUserUrl="<%=addNewUserUrll %>";
var showAddNewUserWizard="<c:out value="${showAddNewUserWizard}"/>";
</script>

<script type="text/javascript" src="<%=request.getContextPath() %>/js/users.js"></script>
<script type="text/javascript">

var dictionary = { 
		lightboxremoveuser: '<spring:message javaScriptEscape="true" code="js.user.del.confirm"/>',
		removeuser: '<spring:message javaScriptEscape="true" code="label.remove.user"/>',
		removingUser: '<spring:message javaScriptEscape="true" code="message.removing.user"/>',
	    lightboxbuttoncancel: '<spring:message javaScriptEscape="true" code="label.cancel"/>',  
	    lightboxbuttonconfirm: '<spring:message javaScriptEscape="true" code="label.confirm"/>',
	    lightboxactivateuser: '<spring:message javaScriptEscape="true" code="js.user.act.confirm"/>',
	    activatingUser: '<spring:message javaScriptEscape="true" code="message.activating.user"/>',
	    activateuser: '<spring:message javaScriptEscape="true" code="label.activate.user"/>',
	    lightboxdeactivateuser: '<spring:message javaScriptEscape="true" code="js.user.deact.confirm"/>',
	    deactivateuser: '<spring:message javaScriptEscape="true" code="label.deactivate.user"/>',
		deactivatingUser: '<spring:message javaScriptEscape="true" code="message.deactivating.user"/>',
		lightboxresendemail: '<spring:message javaScriptEscape="true" code="js.user.ver.confirm"/>',
		resendEmail: '<spring:message javaScriptEscape="true" code="label.resendemail.verification"/>',
		resendingEmail: '<spring:message javaScriptEscape="true" code="message.resendingemail.verification"/>',
		userdisabled: '<spring:message javaScriptEscape="true" code="label.user.disabled"/>',
		selectservice: '<spring:message javaScriptEscape="true" code="label.select.service"/>',
		enableserviceerror: '<spring:message javaScriptEscape="true" code="label.error.service.enable"/>',
		provisioning: '<spring:message javaScriptEscape="true" code="userHandle.state.provisioning"/>',
		active: '<spring:message javaScriptEscape="true" code="userHandle.state.active"/>', 
		notprovisioned: '<spring:message javaScriptEscape="true" code="label.not.provisioned"/>',
		provisioningerror: '<spring:message javaScriptEscape="true" code="userHandle.state.error"/>'
};

</script>


<!--  US585 changes starts here -->

 
                <div class="widget_box">
                  <div class="widget_leftpanel">
                      <div class="widget_titlebar">
                          <h2 id="list_titlebar">
                          <c:choose>
                            <c:when test="${empty selectedUser || selectedUser == null}">
                              <span id="list_all"><spring:message code="label.list.all"/> </span>
                            </c:when>
                            <c:otherwise>
                              <span id="list_all" class="title_listall_arrow"><a href="/portal/portal/users/listusersforaccount"><spring:message code="label.list.all"/></a></span>
                            </c:otherwise>
                          </c:choose>
                          </h2>
        <c:if test="${tenant.state eq 'ACTIVE'}">
         <sec:authorize access="hasAnyRole('ROLE_USER_CRUD','ROLE_ACCOUNT_USER_CRUD')">
             <spring:url value="/portal/users/new/step1" var="create_user_path" htmlEscape="false">
                 <spring:param name="tenant"><c:out value="${tenant.param}"/></spring:param>
             </spring:url>
         </sec:authorize>
    
         <sec:accesscontrollist hasPermission="USERMGMT" domainObject="${tenant}">
       		<c:set var="hasUserMgmt" value="true"/>
         </sec:accesscontrollist>
         <sec:authorize access="hasRole('ROLE_ACCOUNT_USER_CRUD')">
       		<c:set var="hasAccountUserCRUD" value="true"/>
         </sec:authorize>
         <c:if test="${hasUserMgmt eq 'true' || hasAccountUserCRUD eq 'true'}">
            <a class="widget_addbutton" onclick="return addNewUserButton()"  href="javascript:void(0);"/><spring:message code="label.add.new"/></a>
              <div  id="dialog_add_user" title='<spring:message code="label.newUserStep1.addUser"/>' style="display: none">
                <div id="dialog_formcontent"  class="dialog_formcontent wizard"> </div>
             </div>
         </c:if>	
        </c:if>
                        </div>
                        <div class="widget_searchpanel">
                          <div id="search_panel" style="margin:8px 0 0 13px;color:#FFFFFF;">
                          </div>
                        </div>
                        <div class="widget_navigation">
                          <ul class="widget_navigationlist" id="grid_row_container">
                              
                                     
                                     <c:choose>
										   <c:when test="${empty users || users == null}">
										      <!-- Empty list -->
											
                						        <!--look when there is no list starts here-->
                        					<li class="widget_navigationlist nonlist" id="non_list">
                            					<span class="navicon user"></span>
                              						<div class="widget_navtitlebox">
                                						<span class="newlist"><spring:message code="message.no.users.available"/></span>
                              						</div>
                              						<div class="widget_statusicon nostate"></div>
                          					</li>
                          <!--look when there is no list ends here-->
												
									
										   </c:when>
										        <c:otherwise> 
                                                              
                                <c:forEach var="user" items="${users}" varStatus="status">
                                <c:choose>
                                   <c:when test='${selectedUser == user.param}'>
                                      <c:set var="firstUser" value="${user}"/>
                                      <c:set var="selected" value="selected"/> 
                                  </c:when>
                                  <c:otherwise>
                                 <c:choose> 
 				                     <c:when test="${status.index == 0}"> 
                		          <c:set var="firstUser" value="${user}"/> 
                                      <c:set var="selected" value="selected"/> 
                      			</c:when> 
                      			<c:otherwise> 
                          <c:set var="selected" value=""/> 
                      </c:otherwise> 
                      </c:choose>
                                    
                                    
                                    
                                  </c:otherwise>
                                  
	  						     </c:choose>	
                      			
                              <li  class='<c:out value="widget_navigationlist ${selected} users"/>' id="<c:out value="row${user.param}"/>" onclick="viewUser(this)" onmouseover="showInfoBubble(this)" onmouseout="hideInfoBubble(this)">
                            <c:if test="${'root' eq fn:toLowerCase(user.profile.name)}">
                              <span class='navicon ${fn:replace((fn:toLowerCase(user.profile.name)), " ", "")}user' id="nav_icon"></span>
                            </c:if>
                           <c:if test="${'root' ne fn:toLowerCase(user.profile.name)}">
                             <span class='navicon ${fn:replace((fn:toLowerCase(user.profile.name)), " ", "")}' id="nav_icon"></span>
                            </c:if>
                            
                            <div class="widget_navtitlebox">
                              <span class="title"><c:out value="${user.username}"/></span>
                              <span class="subtitle"><c:out value="${user.email}"/></span>
                            </div>
						              	<c:choose> 
                              <c:when test="${!user.enabled}"> 
                                  
                                  <c:set var="status_icon" value="nostate"/> 
                              </c:when> 
                              <c:when test="${!user.locked}"> 
                                  
                                  <c:set var="status_icon" value="running"/> 
                              </c:when> 
                              <c:otherwise> 
                                  <c:set var="status_icon" value="stopped"/> 
                              </c:otherwise> 
                            </c:choose> 
                              <div id="statusIcon" class="<c:out value="widget_statusicon ${status_icon}" />"></div>
                              
                              <!--Info popover starts here-->
                              <div class="widget_info_popover" id="info_bubble" style="display:none">
                              <div class="popover_wrapper" >
                              <div class="popover_shadow"></div>
                              <div class="popover_contents">
                              <div class="raw_contents">
                              <div class="raw_content_row">
                                        <div class="raw_contents_title">
                                          <span><spring:message code="ui.users.all.header.name"/>:</span>
                                        </div>
                                        <div class="raw_contents_value">
                                          <span><c:out value="${user.name}" /></span>
                                        </div>
                                      </div>
                              <div class="raw_content_row">
                                        <div class="raw_contents_title">
                                          <span><spring:message code="label.myprofile.phone"/>:</span>
                                        </div>
                                        <div class="raw_contents_value">
                                          <span><c:out value="${user.phone}" /></span>
                                        </div>
                                      </div>
                              <div class="raw_content_row">
                                        <div class="raw_contents_title">
                                          <span><spring:message code="label.myprofile.timezone"/>:</span>
                                        </div>
                                        <div class="raw_contents_value">
                                          <span><c:out value="${user.timeZone}" /></span>
                                        </div>
                                      </div>
                              <div class="raw_content_row">
                                        <div class="raw_contents_title">
                                          <span><spring:message code="label.myprofile.profilename"/>:</span>
                                        </div>
                                        <div class="raw_contents_value">
                                          <span><spring:message code="profileName.${fn:replace(user.profile.name, ' ', '')}"/></span>
                                        </div>
                                      </div>                                                                            
                              <div class="raw_content_row">
                                        <div class="raw_contents_title">
                                          <span><spring:message code="ui.users.all.header.status"/>:</span>
                                        </div>
                                        <div class="raw_contents_value">
                                          <span>
                                         <c:choose>
                                                <c:when test="${!user.enabled}">
                                                  <spring:message var="usersMsgDisabled" code="label.myprofile.disabled" ></spring:message>
                                                  <c:out value="${usersMsgDisabled}"/>
                                                </c:when> 
                                                <c:when test="${!user.locked}">
                                                  <spring:message var="usersMsgActive" code="label.myprofile.active" ></spring:message>
                                                  <c:out value="${usersMsgActive}"/>
                                                </c:when>  
                                                <c:otherwise>
                                                  <spring:message var="usersMsgLocked" code="label.myprofile.locked" ></spring:message>
                                                  <c:out value="${usersMsgLocked}"/>
                                                </c:otherwise>
                                              </c:choose>
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
                                </c:otherwise>
           						</c:choose>                              
                              
                          </ul>
                        </div>
                        
                        
      <div class="widget_panelnext">
        <div class="widget_navnextbox">
            <c:choose>
              <c:when test="${current_page <= 1}">
                  <a class="widget_navnext_buttons prev nonactive" href="javascript:void(0);" id="click_previous"><spring:message code="label.previous.short"/></a>
              </c:when>
              <c:otherwise>
                  <a class="widget_navnext_buttons prev" href="javascript:void(0);" id="click_previous" onclick="previousClick()"><spring:message code="label.previous.short"/></a>
              </c:otherwise>
            </c:choose> 
            
            <c:choose>
              <c:when test="${enable_next == 'True'}">
              <a class="widget_navnext_buttons next" href="javascript:void(0);" id="click_next" onclick="nextClick()"><spring:message code="label.next"/></a>
          </c:when>
              <c:otherwise>
              <a class="widget_navnext_buttons next nonactive" href="javascript:void(0);" id="click_next" ><spring:message code="label.next"/></a>
                </c:otherwise>
            </c:choose> 
          </div>
      </div>                        
   </div>
     <div class="widget_rightpanel" id="viewUserDiv">
     </div>
     
     
        </div>

<input type="hidden" id="current_page"  value="<c:out value="${current_page}"/>"/>
<input type="hidden" id="default_page_size"  value="<c:out value="${defaultPageSize}"/>"/>
<input id="tenantId" type="hidden" name="tenant" value="<c:out value="${tenant.param}"/>"/>

<!-- Starts body -->
    <!-- Body Header  -->
    <div class="maintitlebox" style="width:98%;">
                  <!-- Start List Users -->  
                      <!-- Header -->
                                 <!-- Header end -->
                       			
      <!-- End List Users -->
      
      <div id="<c:out value="count${size}"/>" class="countDiv"></div>
       <div class="clearboth"></div>
         <div id="viewUserDiv_1">
         	<c:if test="${firstUser != null}">
			 <c:set var="user" value="${firstUser}" scope="request"></c:set>
       <c:forEach items="${profileNames}" var="profileEntry">
           <c:if test="${profileEntry.key == firstUser.profile.id}">
              <c:set var="profileName" value="${profileEntry.value}" scope="request"></c:set>
           </c:if>
      </c:forEach>
   		</c:if> 
          </div>
         <div id="editUserDiv"> </div>
    </div>
  <input id="isUsersMaxReached" name="isUsersMaxReached" type="hidden" value="<c:out value="${isUsersMaxReached}" />" />  
   <div id="userMaxDiv" class="userMaxDiv" style="display:none;">
    
     <div class="dialog_formcontent">
       <div style="float:left; width:320px;">
      <p style="color: #111111"><spring:message code="max.users.reached.popup.message"></spring:message></p>
     </div>
      </div>  
 </div> 

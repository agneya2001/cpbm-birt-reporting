<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>


<script type="text/javascript">
var usersUrl = "<%=request.getContextPath() %>/portal/users/";
var selectedRow = "#row" + "<c:out value='${user.param}'/>";
$(selectedRow).addClass('selected');
</script>
<!-- Start View User Details --> 
<!-- US 585 changes starts here -->   
  <div class="widget_rightpanel">
    <div class="widget_actionbar">
      <div class="widget_actionarea" id="top_actions" >
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
          <div class="widget_actionpopover" id="action_menu"  style="display:none;">
            <div class="widget_actionpopover_top"></div>
            <div class="widget_actionpopover_mid">
              <ul class="widget_actionpoplist">
                <sec:authorize access="hasAnyRole('ROLE_USER_CRUD','ROLE_ACCOUNT_USER_CRUD')">
	                <c:if test="${!(user.id == Handle.PORTAL.systemUerId || (currentUser.id != Hanlde.ROOT.systemUerId && user.id == Hanlde.ROOT.systemUerId))}">
	                 <c:choose> 
                  		<c:when test="${user.tenant.state == 'ACTIVE'}">                   
                    <li class="edituser_link"><spring:message code="label.myprofile.edit"/></li>
                        <c:if test="${currentUser != user && user.tenant.owner !=user}"> 
			                <c:if test="${user.emailVerified == true}">
			                  <c:choose >
			  	                <c:when test="${user.enabled == true && user.locked == false}">      
			  		                <li class="deactivateuser_link"><spring:message code="label.myprofile.deactivate"/></li>
			  	                </c:when>
			  	                <c:otherwise>        
				  	                <li class="activateuser_link"><spring:message code="label.myprofile.activate"/></li>
			  	                </c:otherwise>
			                  </c:choose>
			                </c:if>
			                <li class="removeuser_link"><spring:message code="label.myprofile.remove"/></li>
                    </c:if>
                		<c:if test="${user.emailVerified == false }">
			                <li class="resenduserverification_link"><spring:message code="label.myprofile.resend"/></li>
		                </c:if>
		                 </c:when>
                     <c:when test="${user.tenant.state == 'NEW'}">
                     <li class="edituser_link"><spring:message code="label.myprofile.edit"/></li>
                     <c:if test="${user.emailVerified == false }">
                      <li class="resenduserverification_link"><spring:message code="label.myprofile.resend"/></li>
                    </c:if>
                     </c:when>
                  <c:otherwise>
                      <li id="no_actions_available_volume" title='<spring:message code="label.no.actions.available"/>'><spring:message code="label.no.actions.available"/></li>
                  </c:otherwise>
                  </c:choose>
                   </c:if>
                </sec:authorize>
              </ul>
            </div>
            <div class="widget_actionpopover_bot"></div>
          </div>
          <!--Actions popover ends here-->
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
     <div id="spinning_wheel" style="display:none">
       <div class="widget_loadingpanel">
       </div>
       <div class="maindetails_footer_loadingbox first">
         <div class="maindetails_footer_loadingicon"></div>
         <p id="in_process_text"></p>
       </div>
     </div>
     <div class="widget_browsermaster">
       <div class="widget_browser_contentarea">
         <div class="widget_browsergrid_wrapper master">
           <div class="widget_grid master even first">
             <div class="widget_grid_labels">
               <span><spring:message code="ui.users.all.header.username"/></span>
             </div>
             <div class="widget_grid_description">
               <span id="username"><c:out value="${user.username}"></c:out></span>
             </div>
           </div>
           <div class="widget_grid master even">
             <div class="widget_grid_labels">
               <span><spring:message code="ui.users.all.header.email"/></span>
             </div>
             <div class="widget_grid_description">
               <span id="email"><c:out value="${user.email}"></c:out></span>
             </div>
           </div>
           <div class="widget_grid master even">
             <div class="widget_grid_labels">
               <span><spring:message code="ui.users.all.header.status"/></span>
             </div>  
             <div class="widget_grid_description">
               
                 <c:choose>
         				   <c:when test="${!user.enabled}">
                   <span class="destroyedicon" id="details_status_icon"></span>
                      <span class="destroyed" id="details_status_text"><spring:message code="label.myprofile.disabled"/></span>
                   </c:when> 
                   <c:when test="${!user.locked}">
                      <span class="runningicon" id="details_status_icon"></span>
                      <span class="running" id="details_status_text"><spring:message code="label.myprofile.active"/></span>
                       
          		     </c:when>  
                   <c:otherwise>
                    <span class="stoppedicon" id="details_status_icon"></span>
                      <span class="stopped" id="details_status_text"><spring:message code="label.myprofile.locked"/></span>
                    </c:otherwise>
                	</c:choose>
              
             </div>
           </div>
         </div>
          <c:if test="${'root' eq fn:toLowerCase(user.profile.name)}">
           <div class='widget_masterbigicons users ${fn:replace((fn:toLowerCase(user.profile.name)), " ", "")}user'>
        </div>
         </c:if>
         <c:if test="${'root' ne fn:toLowerCase(user.profile.name)}">
            <div class='widget_masterbigicons users ${fn:replace((fn:toLowerCase(user.profile.name)), " ", "")}'>
            </div>
        </c:if>
         
       </div>
     </div>
     <div class="widget_browser_contentarea">
       <ul class="widgets_detailstab">
         <li class="widgets_detailstab active" id="details_tab" onclick="showUserDetails(this)"><spring:message code="label.details"/></li>
         <li  class="widgets_detailstab nonactive" id="viewServiceSubscriptionStatus_tab" onclick="showUserService(this);"> <spring:message code="label.user.service"/> </li>
       </ul>
       <sec:accesscontrollist hasPermission="USERMGMT" domainObject="${tenant}">
       		<c:set var="hasUserMgmt" value="true"/>
       </sec:accesscontrollist>
       <sec:authorize access="hasRole('ROLE_ACCOUNT_USER_CRUD')">
       		<c:set var="hasAccountUserCRUD" value="true"/>
       </sec:authorize>
       <c:if test="${hasUserMgmt eq 'true' || hasAccountUserCRUD eq 'true'}">
          <div class="widget_details_actionbox">
           <ul class="widget_detail_actionpanel">
                 <c:if test="${showEnableServiceLink}">
		           <li><a id="enableServiceButton" class="enableAllservicesForUser_link" class="widget_addbutton"  href="javascript:void(0);" onclick="enableAllServiceForUser('${user.uuid}','${currentTenant.param}')"><spring:message code="label.enable.selected.services"/></a> </li>
                 </c:if>
		   </ul>
          </div>
       </c:if>	
       
       <div class="widget_browsergrid_wrapper details" id="details_content">
         <div class="widget_grid details even">
           <div class="widget_grid_labels">
             <span><spring:message code="label.userInfo.firstName"/></span>
           </div>
           <div class="widget_grid_description" >
             <span id="firstName"><c:out value="${user.firstName}"></c:out></span>
           </div>
         </div>    
         <div class="widget_grid details odd">
           <div class="widget_grid_labels">
             <span><spring:message code="label.userInfo.lastName"/></span>
           </div>
           <div class="widget_grid_description">
             <span id="lastName"><c:out value="${user.lastName}"></c:out></span>
           </div>
         </div>    
         <div class="widget_grid details even">
           <div class="widget_grid_labels">
             <span><spring:message code="label.myprofile.phone"/></span>
           </div>
           <div class="widget_grid_description">
             <span id="phone"><c:if test="${user.phone != null && user.phone != ''}">+</c:if><c:out value="${user.phone}"></c:out></span>
           </div>
         </div>     
         <div class="widget_grid details odd">
           <div class="widget_grid_labels">
             <span><spring:message code="label.myprofile.timezone"/></span>
           </div>
           <div class="widget_grid_description">
             <span id="timezone"><c:out value="${user.timeZone}"></c:out></span>
           </div>
         </div>    
         <div class="widget_grid details even">
           <div class="widget_grid_labels">
           
             <span><spring:message code="label.myprofile.profilename"/></span>
           </div>
           <div class="widget_grid_description">
             <c:if test="${'root' eq fn:toLowerCase(user.profile.name)}">
               <span id="details_profile_icon" class='${fn:replace((fn:toLowerCase(user.profile.name)), " ", "")}user'></span>
             </c:if>
             <c:if test="${'root' ne fn:toLowerCase(user.profile.name)}">
               <span id="details_profile_icon" class='baseprofile ${fn:replace((fn:toLowerCase(user.profile.name)), " ", "")}'></span>
             </c:if>
             <span id="profilename" style="width:auto;margin:11px 0px 0px 10px;"><spring:message code="profileName.${fn:replace(user.profile.name, ' ', '')}"/></span>
           </div>
         </div>           
         <div class="widget_grid details odd">
           <div class="widget_grid_labels">
             <span><spring:message code="label.myprofile.language"/></span>
           </div>
           <div class="widget_grid_description">
             <span id="locale"><c:out value="${userLocale}"></c:out></span>
           </div>
         </div>           
       </div>
        <div class="widget_browsergrid_wrapper details" id="service_content" style="display:none;">
        <input style="margin:10px;" id="userstatus" type="hidden" value=${user.enabled}>
         <c:forEach items="${serviceRegistrationStatus}" var="serviceInstanceEntry" varStatus="status">
          <div id="service_contentInner">
            <c:set var="row_color" value="even"/>
            <c:if test="${status.index%2 eq 0}">
              <c:set var="row_color" value="odd" />
            </c:if>
            <div class="grid_rows ${row_color}">
              <div class="ellipsis" style="margin: 5px; width: 15px; display: inline; float: left" title="${serviceInstanceEntry.key.name}">
                <c:set var="no_role" value="false"></c:set>
                <c:if test="${user.getAuthorities(serviceInstanceEntry.key.service).size() eq 0}">
                  <c:set var="no_role" value="true"></c:set>
                </c:if>
                <c:choose>
                  <c:when
                    test="${mapOfInstanceVsHandle[serviceInstanceEntry.key.uuid].state.name() eq 'ACTIVE' || mapOfInstanceVsHandle[serviceInstanceEntry.key.uuid].state.name() eq 'PROVISIONING'}">
                    <input id="${serviceInstanceEntry.key.uuid}" type="checkbox" disabled="true" checked="checked" />
                  </c:when>
                  <c:otherwise>
                    <input id="${serviceInstanceEntry.key.uuid}" type="checkbox" <c:if test="${no_role}"><c:out value="disabled"/> </c:if> />
                  </c:otherwise>
                </c:choose>
              </div>
              <div class="ellipsis" style="margin: 9px 0px 0px 0px; width: 225px; display: inline; float: left;" title="${serviceInstanceEntry.key.name}">
                <c:out value="${serviceInstanceEntry.key.name}"></c:out>
              </div>
              <c:choose>
                <c:when test="${mapOfInstanceVsHandle[serviceInstanceEntry.key.uuid] != null}">
                  <div style="margin: 9px 0px 0px 0px; display: inline; float: left" " id="service_state_${serviceInstanceEntry.key.uuid}">
                        <spring:message code="userHandle.state.${fn:toLowerCase(mapOfInstanceVsHandle[serviceInstanceEntry.key.uuid].state.name())}" />
                        <c:if test="${mapOfInstanceVsHandle[serviceInstanceEntry.key.uuid].state.name() eq 'ERROR' || mapOfInstanceVsHandle[serviceInstanceEntry.key.uuid].state.name() eq 'INVALID'}">
                          <spring:message code="${mapOfInstanceVsHandle[serviceInstanceEntry.key.uuid].data}" var="error_data"/>
                          (<a class="js_user_enable_service_error user_enable_service_error" data-toggle="popover" data-trigger="hover" data-placement="bottom" data-container="body" data-content="<spring:message javaScriptEscape="true" text="${error_data}"/>"><spring:message code="label.details"/></a>)
                        </c:if>
                  </div>
                </c:when>
                <c:otherwise>
                  <c:choose>
                    <c:when test="${no_role}">
                      <div style="margin: 9px 0px 0px 0px; display: inline; float: left" " id="service_state_${serviceInstanceEntry.key.uuid}">
                        <spring:message code="label.no.role" />
                      </div>
                    </c:when>
                    <c:otherwise>
                      <div style="margin: 9px 0px 0px 0px; display: inline; float: left" " id="service_state_${serviceInstanceEntry.key.uuid}">
                        <spring:message code="label.not.provisioned" />
                      </div>
                    </c:otherwise>
                  </c:choose>
                </c:otherwise>
              </c:choose>
              <div id="service_provisioning_${serviceInstanceEntry.key.uuid}" class="maindetails_footer_loadingicon"
                style="display: none; width: 20px;"></div>
            </div>
          </div>
        </c:forEach>
       </div>   
       <div id="tab_spinning_wheel" class="rightpanel_mainloader_panel" style="display: none;">
         <div class="rightpanel_mainloaderbox">
           <div class="rightpanel_mainloader_animatedicon">
           </div>
           <p><spring:message code="label.loading"/> &hellip;</p>
         </div>
       </div> 
       <div class="widget_browsergrid_wrapper wsubaction" id="snapshots_details_content" style="display:none">
         <div id="snapshots_row_container">
         </div>
         <div class="widget_browsergrid_panelnext">
           <div class="widget_navnextbox grid">
             <a class="widget_navnext_buttons prev nonactive" href="javascript:void(0);" id="snapshots_click_previous"><spring:message code="label.previous.short"/></a>
             <a class="widget_navnext_buttons next" href="javascript:void(0);" id="snapshots_click_next"><spring:message code="label.next"/></a>
           </div>
         </div>
       </div>
     </div>
   </div>
 </div>
 <input type="hidden" id="userEnabled" value="${user.enabled}">
 <input type='hidden' id='user_param' value='<c:out value="${user.param}"/>'>
 <input id="isUsersMaxReached" name="isUsersMaxReached" type="hidden" value="<c:out value="${isUsersMaxReached}" />" />                    

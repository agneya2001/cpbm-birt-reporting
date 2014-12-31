<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/profiles.js"></script>

<script type="text/javascript">
var isOpsProfile = "<c:out value="${isOpsProfile}"/>";
var selectedProfile = "<c:out value="${selectedProfile}"/>";
var selectedTab = "<c:out value="${selectedTab}"/>";
var updatedProfile_id = "<c:out value="${updatedProfile}"/>";
<!--
$(document).ready(function() {
	
	$('#opSave').click(function() {
		$('#editProfileForm').submit();
	});
	
	$('#nonopSave').click(function() {
		$('#editProfileForm').submit();
	});

	
});
//-->
</script>
 <div class="widget_box">
    <div class="widget_leftpanel">
        <div class="widget_titlebar">
            <h2>
            <span class=""><spring:message code="label.list.all"/></span>
            </h2>
        </div>
        <div class="widget_searchpanel">
        <div class="widget_searchcontentarea" id="search_panel">
          </div>
        </div>
        
        <div class="widget_navigation">
          
          
          <div id="spProfiles" style="display:none;">
          <ul class="widget_navigationlist" id="grid_row_container">
            <c:choose>
                <c:when test="${empty opsProfileList || opsProfileList == null}">
                  <c:set var="profile_icon" value="rootuser"/>
                  <c:set var="opsProfileListLen" value="0"/>
                  <!--look when there is no list starts here-->
                  <li class="widget_navigationlist nonlist" id="non_list">
                      <span class="navicon ${profile_icon} }"></span>
                        <div class="widget_navtitlebox">
                          <span class="newlist">
                            <spring:message var="batchjobsMsg" code="ui.label.emptylist.batchjobs" ></spring:message>
                            <spring:message code="ui.label.emptylist.notavailable" arguments="${batchjobsMsg}" htmlEscape="false"/>
                          </span>
                        </div>
                        
                  </li>
                  <!--look when there is no list ends here-->
                </c:when>
                <c:otherwise>
                  <c:forEach items="${opsProfileList}" var="profile_choice" varStatus="status">
                      <c:if test="${profile_choice.profile.name != systemProfileName}">
                      <c:set var="opsProfileListLen" value="${opsProfileListLen+1}"/>
                      <c:choose>
                        <c:when test="${status.index == 1}">
                          <c:set var="firstProfilElem" value="${profile_choice.profile.id}"/>
                          <c:set var="selected" value="selected"/>
                          <c:set var="active" value="active"/>
                        </c:when>
                        <c:otherwise>
                            <c:set var="selected" value=""/>
                            <c:set var="active" value=""/>
                        </c:otherwise>
                      </c:choose> 
                      <c:choose>
                      <c:when test="${profile_choice.profile.id == updatedProfile}">
                        <c:set var="dotted_blue" value="dotted_blue"/>
                      </c:when>
                      <c:when test="${empty updatedProfile  && profile_choice.profile.name == rootProfileName }">
                        <c:set var="dotted_blue" value="dotted_blue"/>
                      </c:when>   
                      <c:otherwise>
                        <c:set var="dotted_blue" value=""/>
                      </c:otherwise>
                    </c:choose>     
                    
                      <li class="<c:out value="widget_navigationlist ${selected} ${active} "/>" id="<c:out value="${profile_choice.profile.id}"/>" onclick="viewProfile(this);">
                        <c:set var="profile_icon" value="rootuser"/>
                        <c:if test="${profile_choice.profile.id == 2}">
                          <c:set var="profile_icon" value="rootuser"/>
                        </c:if>
                        <c:if test="${profile_choice.profile.id == 3}">
                          <c:set var="profile_icon" value="opsadmin"/>
                        </c:if>
                        <c:if test="${profile_choice.profile.id == 4}">
                          <c:set var="profile_icon" value="financeadmin"/>
                        </c:if>
                        <c:if test="${profile_choice.profile.id == 5}">
                          <c:set var="profile_icon" value="salessupport"/>
                        </c:if>
                        <c:if test="${profile_choice.profile.id == 6}">
                          <c:set var="profile_icon" value="helpdesk"/>
                        </c:if>
                        <c:if test="${profile_choice.profile.id == 7}">
                          <c:set var="profile_icon" value="productmanager"/>
                        </c:if>
                        <span id="nav_icon" class="navicon ${profile_icon}"></span>
                        <div class="widget_navtitlebox <c:out value="db_gridbox_rows"/>" title="<spring:message code="profileName.${fn:replace(profile_choice.profile.name, ' ', '')}"/>">
                          <span class="title">
                            <spring:message code="profileName.${fn:replace(profile_choice.profile.name, ' ', '')}"/>
                          </span>
                          <span class="subtitle"></span>
                        </div>
                        <div class="widget_statusicon"></div>
                      </li>
                    </c:if>
                  </c:forEach>
                </c:otherwise> 
              </c:choose>
            </ul>
          </div>
          <div id="customerProfiles" style="display:block;">
            <ul class="widget_navigationlist" id="">
            <c:choose>
                <c:when test="${empty nonOpsProfileList || nonOpsProfileList == null}">
                  <c:set var="nonprofile_icon" value="masteruser"/>
                  <c:set var="opsProfileListLen" value="0"/>
                  <!--look when there is no list starts here-->
                  <li class="widget_navigationlist nonlist" id="non_list">
                      <span class="navicon ${nonprofile_icon}"></span>
                        <div class="widget_navtitlebox">
                          <span class="newlist">
                            <spring:message var="batchjobsMsg" code="ui.label.emptylist.batchjobs" ></spring:message>
                            <spring:message code="ui.label.emptylist.notavailable" arguments="${batchjobsMsg}" htmlEscape="false"/>
                          </span>
                        </div>
                        
                  </li>
                  <!--look when there is no list ends here-->
                </c:when>
                <c:otherwise>
                  <c:forEach items="${nonOpsProfileList}" var="nonprofile_choice" varStatus="status">
                      <c:set var="opsProfileListLen" value="${opsProfileListLen+1}"/>
                      <c:choose>
                        <c:when test="${status.index == 0}">
                          <c:set var="firstNonProfileElem" value="${nonprofile_choice.profile.id}"/>
                          <c:set var="selected" value="selected"/>
                          <c:set var="active" value="active"/>
                        </c:when>
                        <c:otherwise>
                            <c:set var="selected" value=""/>
                            <c:set var="active" value=""/>
                        </c:otherwise>
                      </c:choose>
                      <c:choose>
                      <c:when test="${nonprofile_choice.profile.id == updatedProfile}">
                        <c:set var="dotted_blue" value="dotted_blue"/>
                      </c:when> 
                      <c:otherwise>
                        <c:set var="dotted_blue" value=""/>
                      </c:otherwise>
                    </c:choose>     
                    <li class="<c:out value="widget_navigationlist ${selected} ${active} "/>" id="<c:out value="${nonprofile_choice.profile.id}"/>" onclick="viewProfile(this);">
                      <c:set var="nonprofile_icon" value="masteruser"/>
                      <c:if test="${nonprofile_choice.profile.id == 8}">
                        <c:set var="nonprofile_icon" value="masteruser"/>
                      </c:if>
                      <c:if test="${nonprofile_choice.profile.id == 9}">
                        <c:set var="nonprofile_icon" value="billingadmin"/>
                      </c:if>
                      <c:if test="${nonprofile_choice.profile.id == 10}">
                        <c:set var="nonprofile_icon" value="user"/>
                      </c:if>
                      <c:if test="${nonprofile_choice.profile.id == 11}">
                        <c:set var="nonprofile_icon" value="poweruser"/>
                      </c:if>
                      <span id="nav_icon" class="navicon ${nonprofile_icon}"></span>
                        <div class="widget_navtitlebox <c:out value="db_gridbox_rows"/>">
                          <span class="title">
                            <spring:message code="profileName.${fn:replace(nonprofile_choice.profile.name, ' ', '')}"/>
                          </span>
                          <span class="subtitle"></span>
                        </div>
                        <div class="widget_statusicon"></div>
                    </li>
                  </c:forEach>
                </c:otherwise> 
              </c:choose>
            </ul>
          </div>                            
              
        </div>
        <script type="text/javascript">
          var opsProfileListLen = "<c:out value="${opsProfileListLen}"/>";
        </script>
        <!--  div class="widget_panelnext">
          <div class="widget_navnextbox">
              <a class="widget_navnext_buttons prev nonactive" href="javascript:void(0);" id="click_previous"><spring:message code="label.previous.short"/></a>
                <a class="widget_navnext_buttons next" href="javascript:void(0);" id="click_next"><spring:message code="label.next"/></a>
            </div>
        </div-->
    </div>
    <div id="<c:out value="count${size}"/>" class="countDiv"></div>
        



<!-- Start of Right panel -->
<div class="widget_rightpanel" id="viewProfileDiv">
<script>
          var ops_id = "<c:out value="${firstProfilElem}"/>";
          var noops_id = "<c:out value="${firstNonProfileElem}"/>";
          
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

<div class="maincontent_bigverticalpanel" style="float:right; margin:0;">       
       <!-- Start Grid -->
  <c:forEach items="${allProfileList}" var="profile_choice" varStatus="status">
    <c:if test="${profile_choice.profile.name != systemProfileName}">
    	<c:choose>
  			<c:when test="${profile_choice.profile.id == updatedProfile}">
  				<c:set var="displayProfile" value="display:block"/>
  			</c:when>
  			<c:when test="${empty updatedProfile  && profile_choice.profile.name == rootProfileName}"> <!-- inconceivably messy this one is.. :( -->
  				<c:set var="displayProfile" value="display:block"/>
  			</c:when>		
  			<c:otherwise>
  				<c:set var="displayProfile" value="display:none"/>
  			</c:otherwise>
  		</c:choose>
      <div class="main_listbox" id="profile<c:out value="${profile_choice.profile.id}"/>div" style="<c:out value="${displayProfile}"/>">
       
        <div class="widget_rightselectedheader">
          <c:set var="profile_icon" value="rootuser"/>
          <c:if test="${profile_choice.profile.id == 2}">
            <c:set var="profile_icon" value="rootuser"/>
          </c:if>
          <c:if test="${profile_choice.profile.id == 3}">
            <c:set var="profile_icon" value="opsadmin"/>
          </c:if>
          <c:if test="${profile_choice.profile.id == 4}">
            <c:set var="profile_icon" value="financeadmin"/>
          </c:if>
          <c:if test="${profile_choice.profile.id == 5}">
            <c:set var="profile_icon" value="salessupport"/>
          </c:if>
          <c:if test="${profile_choice.profile.id == 6}">
            <c:set var="profile_icon" value="helpdesk"/>
          </c:if>
          <c:if test="${profile_choice.profile.id == 7}">
            <c:set var="profile_icon" value="productmanager"/>
          </c:if>
          <c:if test="${profile_choice.profile.id == 8}">
            <c:set var="profile_icon" value="masteruser"/>
          </c:if>
          <c:if test="${profile_choice.profile.id == 9}">
            <c:set var="profile_icon" value="billingadmin"/>
          </c:if>
          <c:if test="${profile_choice.profile.id == 10}">
            <c:set var="profile_icon" value="user"/>
          </c:if>
          <c:if test="${profile_choice.profile.id == 11}">
            <c:set var="profile_icon" value="poweruser"/>
          </c:if>
          <span class="common_icon ${profile_icon}"></span>
          <p>
            <spring:message code="profileName.${fn:replace(profile_choice.profile.name, ' ', '')}"/>
          </p>
        </div>
        
        <div class="widget_browsermaster fullscreen">
          <div class="widget_browser_contentarea">
              <div class="widget_browsergrid_wrapper fullpagegrid">
              
                <div class="widget_grid inline header">
                    <div class="widget_grid_cell" style="width:25%;">
                      <span class="header">
                        <spring:message code="ui.profiles.show.page.role.name"/>
                      </span>
                    </div>
                    <div class="widget_grid_cell" style="width:60%;">
                      <span class="header">
                        <spring:message code="ui.profiles.show.page.description"/>
                      </span>
                    </div>
                    <div class="widget_grid_cell" style="width:12%;">
                      <span class="header">
                        <spring:message code="ui.profiles.show.page.allow"/>
                      </span>
                    </div>
                </div>
                 <!-- Data Start-->
                 <c:set var="runningRowNum" value="-1"/>
                  <c:forEach items="${profile_choice.typeList}" var="authority_choice" varStatus="status">
                    <c:choose>  
                        <c:when test="${authority_choice.type == 'ROLE_SYSTEM'}">
                          <c:set var="IsDisplay" value="display:none"/>                            
                        </c:when>
                       <c:otherwise>
                          <c:set var="IsDisplay" value=""/>
                          <c:set var="runningRowNum" value="${runningRowNum + 1}"/>
                       </c:otherwise>
                    </c:choose>  
                    <c:choose>
                      <c:when test="${runningRowNum % 2 == 0}">
                          <c:set var="rowClass" value="odd"/>
                      </c:when>
                      <c:otherwise>
                            <c:set var="rowClass" value="even"/>
                      </c:otherwise>
                    </c:choose>  
            
                    <c:choose>
                    	<c:when test="${authority_choice.type == 'ROLE_ACCOUNT_MGMT' || authority_choice.type == 'ROLE_USER' || authority_choice.required eq true || profile_choice.profile.name eq rootProfileName || authority_choice.type eq 'ROLE_PROFILE_CRUD'}">
                    		<c:set var="readOnlyClass" value="disabled"></c:set>
                    		<c:set var="disabledValue" value="disabled='disabled'"></c:set>
                    	</c:when>
                    	<c:otherwise>
                    		<c:set var="readOnlyClass" value=""></c:set>
                    		<c:set var="disabledValue" value=""></c:set>
                    	</c:otherwise>
                    </c:choose>
                      
                
                    <div class="<c:out value="widget_grid fixheight ${rowClass} "/>" style="<c:out value="${IsDisplay}"/>">
                        <div class="widget_grid_cell" style="width:25%;">
                          <span class="celltext">
                            <c:choose>
                              <c:when test="${empty authority_choice.serviceName}">
                                <spring:message htmlEscape="true" code="profiles.${authority_choice.type}" />
                              </c:when>
                              <c:otherwise>
                                <spring:message htmlEscape="true" code="${authority_choice.serviceName}.profiles.${authority_choice.type}" />
                              </c:otherwise>
                            </c:choose>
                          </span>
                        </div>
                        <div class="widget_grid_cell" style="width:60%;">
                          <span class="descpwrapper" style="width:340px;">
                            <span class="descpwrapper desctext">
                              <c:choose>
                                <c:when test="${empty authority_choice.serviceName}">
                                  <spring:message htmlEscape="true" code="profiles.description.${authority_choice.type}" />
                                </c:when>
                                <c:otherwise>
                                  <spring:message htmlEscape="true" code="${authority_choice.serviceName}.profiles.description.${authority_choice.type}" />
                                </c:otherwise>
                              </c:choose>
                            </span>
                            
                            <a href="#" style="cursor:default;" class="js_profiles_details_popover_link" rel="popover" 
                            data-content="<c:choose>
                                <c:when test="${empty authority_choice.serviceName}">
                                  <spring:message htmlEscape="true" code="profiles.description.${authority_choice.type}" />
                                </c:when>
                                <c:otherwise>
                                  <spring:message htmlEscape="true" code="${authority_choice.serviceName}.profiles.description.${authority_choice.type}" />
                                </c:otherwise>
                              </c:choose>">
                              <spring:message code="notification.detail.more" /></a>
                          </span>
                        </div>
                        
                        
                        <div class="widget_grid_cell" style="width:8%;">
                        <span class="celltext">
                        <div class="widget_checkbox widget_checkbox_wide opcheck ${readOnlyClass}" 
                             name="opcheck<c:out value="${profile_choice.profile.id}"/>" 
                             value="<c:out value="${authority_choice.type}"/>">

                        <c:choose>
                          <c:when test="${authority_choice.present == 'true' }"><span class="checked ${readOnlyClass}"/></c:when>
                          <c:otherwise>
                            <span class="unchecked ${readOnlyClass}"/>
                            <input type="hidden" name="roles"  value="<c:out value="${profile_choice.authorityStr}"/>"/>
                          </c:otherwise>
                        </c:choose>

                        
                       </div>
                       </span>
                       
                       </div>
                </div>
            </c:forEach>
            </div>
          <c:if test="${profile_choice.profile.name != rootProfileName}">      
          <spring:url value="/portal/profiles/{profileId}/edit" var="edit_profile_path" htmlEscape="false">
            <spring:param name="profileId"><c:out value="${profile_choice.profile.id}" /></spring:param>
          </spring:url>
          <form class="editProfileForm" action="<c:out value="${edit_profile_path}"/>" method="post" name="editProfile<c:out value="${profile_choice.profile.id}"/>" id="editProfileForm">
            <input type="hidden" id="roles<c:out value="${profile_choice.profile.id}"/>" name="roles" value="<c:out value="${profile_choice.authorityStr}"/>"> 
            <div class="widget_browsergrid_panelnext">
                 <div class="widget_navnextbox grid">
                     <a class="widget_navnext_buttons save" href="#" id="opsave"name="saveprofile<c:out value="${profile_choice.profile.id}"/>" onclick="document.editProfile<c:out value="${profile_choice.profile.id}"/>.submit()"><spring:message code="ui.profiles.show.page.save"/></a>
                     <a class="widget_navnext_buttons" href="javascript:actionCancel();" id="cancel"><spring:message code="ui.profiles.show.page.cancel"/></a>
                  </div>
            </div>            

          </form>
          </c:if>           
                 
                 <!-- Data end -->       
          
              
          </div>
        </div>
       
      		
      </div>
    </c:if>
  </c:forEach>
  <!-- End Grid -->
</div>

</div>
</div>
<!-- End of Right panel -->
</div>

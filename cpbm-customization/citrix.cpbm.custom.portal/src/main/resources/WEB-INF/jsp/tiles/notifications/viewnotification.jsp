<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<script type="text/javascript">
  
  var totalpages2 = "<c:out value="${totalpages2}"/>";
  var currentPage2 = "<c:out value="${currentPage2}"/>";
  var perPageValue2 = "<c:out value="${perPage2}"/>";
  var notificationListLen2 = "<c:out value="${notificationListLen2}"/>";
  var selectedDate = "<c:out value="${selectedDate}"/>";
  
  currentPage2 = parseInt(currentPage2);
  perPageValue2 = parseInt(perPageValue2);
  notificationListLen2 = parseInt(notificationListLen2);

  function viewNotification2(tenantParam, selectedDate, currentPage2, perPageValue2){
    var url = notificationUrl+"view_notification";
    $.ajax( {
       type : "GET",
       url : url,
       data:{tenant:tenantParam, date:selectedDate, currentPage:currentPage2, perPage:perPageValue2},
       dataType : "html",
       success : function(html) {        
         $("#viewnotificationDiv").html(html); 
       },error:function(){ 
         //need to handle TO-DO
       }
    });
 }
  
  function nextClick2(event) {
    
    $("#click_next2").unbind("click", nextClick2);
    $("#click_next2").addClass("nonactive");
    
    currentPage2 = currentPage2 + 1;
    
    $("#click_previous2").unbind("click").bind("click", previousClick2);
    $("#click_previous2").removeClass("nonactive");
    viewNotification2(tenantParam, selectedDate, currentPage2, perPageValue2);
  }

  function previousClick2(event) {
    $("#click_previous2").unbind("click", previousClick2);
    $("#click_previous2").addClass("nonactive");

    currentPage2 = currentPage2 - 1;
    
    $("#click_next2").removeClass("nonactive");
    $("#click_next2").unbind("click").bind("click", nextClick2);
    
    viewNotification2(tenantParam, selectedDate, currentPage2, perPageValue2);
  }
  
  if (currentPage2 > 1) {
    $("#click_previous2").removeClass("nonactive");
    $("#click_previous2").unbind("click").bind("click", previousClick2);
  }
  
  if (notificationListLen2 < perPageValue2) {
    $("#click_next2").unbind("click");
    $("#click_next2").addClass("nonactive");

  } else if (notificationListLen2 == perPageValue2) {
    
    if (currentPage2 < totalpages2) {
      
      $("#click_next2").removeClass("nonactive");
      $("#click_next2").unbind("click").bind("click", nextClick2);
    } else {
      $("#click_next2").unbind("click");
      $("#click_next2").addClass("nonactive");
    }
  }
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
  <c:choose>
    <c:when test="${empty notificationsList2 || notificationsList2 == null}">
      
    </c:when>
    <c:otherwise>
      <c:forEach var="notification2" items="${notificationsList2}" varStatus="status">
            
            <c:choose>
              <c:when test="${status.index == 0}">
                  <c:set var="firstNotification2" value="${notification2}"/>
              </c:when>
           </c:choose>
       </c:forEach>              
    </c:otherwise> 
  </c:choose>
	<div class="widget_rightselectedheader">
    	<span class="icon"></span>
        <p>
          <spring:message code="dateonly.format" var="date_format" />
          <fmt:formatDate value="${firstNotification2.generatedAt}" pattern="${date_format}" timeZone="${currentUser.timeZone}"/>
        </p>
  </div>
  <div class="widget_browsermaster fullscreen">
    	<div class="widget_browser_contentarea">
    		<div class="widget_browsergrid_wrapper fullpagegrid">
                	<div class="widget_grid inline header">
                    	<div class="widget_grid_cell" style="width:15%;">
                        	<span class="header">
                            <spring:message code="notification.detail.category"/>
                          </span>
                        </div>
                        <div class="widget_grid_cell" style="width:15%;">
                        	<span class="header">
                            <spring:message code="notification.detail.time"/>
                          </span>
                        </div>
                        <div class="widget_grid_cell" style="width:60%;">
                        	<span class="header">
                            <spring:message code="notification.detail.description"/>
                          </span>
                        </div>
                    </div>
                    <c:forEach var="notification2" items="${notificationsList2}" varStatus="status">
                    <c:choose>
                      <c:when test="${status.index % 2 == 0}">
                          <c:set var="rowClass" value="odd"/>
                      </c:when>
                      <c:otherwise>
                          <c:set var="rowClass" value="even"/>
                      </c:otherwise>
                      </c:choose> 
                    <div class="<c:out value="widget_grid fullpage ${rowClass} fixheight"/>" >
                    	<div class="widget_grid_cell" style="width:15%;">
                        	<span class="celltext">
                            <c:choose>
		                      <c:when test="${not empty notification2.severity}">
		                        <c:set var="level" value="${notification2.severity}"/>
		                      </c:when>
		                      <c:otherwise>
		                        <c:set var="level" value="INFORMATION"/>
		                      </c:otherwise>
		                    </c:choose>
		                    <c:choose>
		                      <c:when test="${not empty notification2.category}">
		                        <c:set var="category" value="${notification2.category}"/>
		                      </c:when>
		                      <c:otherwise>
		                        <c:set var="category" value="SYSTEM"/>
		                      </c:otherwise>
		                    </c:choose>
                            <spring:message code="ui.notification.category.${category}"/>
                          </span>
                        </div>
                        <div class="widget_grid_cell" style="width:15%;">
                        	<span class="celltext">
                            <c:set var="time_format" value="HH:mm:ss" />
                            <fmt:formatDate value="${notification2.generatedAt}" pattern="${time_format}" timeZone="${currentUser.timeZone}"/>
                          </span>
                        </div>
                        <div class="widget_grid_cell" style="width:60%;">
                          
              	           <span class="levelicon <c:out value="${level}"></c:out>"></span>
                          
                          <span class="descpwrapper" style="width:340px;">
                            <span class="descpwrapper desctext">
                              <c:choose>
                                <c:when test="${notification2.useMessageKey eq true}">
                                  <strong><spring:message code="${notification2.message}" arguments="${notification2.messageArguments}"/></strong>
                                </c:when>
                                <c:otherwise>
                                  <strong><c:out value="${notification2.message}"></c:out></strong>
                                </c:otherwise>
                              </c:choose>
                            </span>
                            <span class="descpwrapper desctext">
                              <c:choose>
                                <c:when test="${notification2.useMessageKey eq true}">
                                  <strong><spring:message code="${notification2.description}" arguments="${notification2.messageArguments}"/></strong>
                                </c:when>
                                <c:otherwise>
                                  <strong><c:out value="${notification2.attributesJson}"></c:out></strong>
                                </c:otherwise>
                              </c:choose>
                            </span>
                            <a href="javascript:void(0);" style="cursor:default;" rel="popover" id="details_link_${status.index}" class="js_notification_details_popover">
                              <spring:message code="notification.detail.more" /></a>
                          </span>
                        </div>
                        <!--Info popover starts here-->
                        <div  id="info_bubble2_<c:out value='${status.index}' />" style="display:none;">
                              <div style="max-width:300px;word-wrap: break-word;">
                                  <span>
                                    <c:choose>
                                      <c:when test="${notification2.useMessageKey eq true}">
                                        <strong><spring:message code="${notification2.message}" arguments="${notification2.messageArguments}"/></strong>
                                      </c:when>
                                      <c:otherwise>
                                        <strong><c:out value="${notification2.message}"></c:out></strong>
                                      </c:otherwise>
                                    </c:choose>
                                  </span>
                              </div>
                              <div style="max-width:300px;word-wrap: break-word;">
                                <span>
                                  <c:choose>
                                    <c:when test="${notification2.useMessageKey eq true}">
                                      <strong><spring:message code="${notification2.description}" arguments="${notification2.messageArguments}"/></strong>
                                    </c:when>
                                    <c:otherwise>
                                      <strong><c:out value="${notification2.attributesJson}"></c:out></strong>
                                    </c:otherwise>
                                  </c:choose>
                                </span>
                              </div>
                        </div>
                        <!--Info popover ends here-->
                    </div>
                    </c:forEach>
            
            
                <div class="widget_browsergrid_panelnext">
                     <div class="widget_navnextbox grid">
                         <a class="widget_navnext_buttons prev nonactive" href="javascript:void(0);" id="click_previous2"><spring:message code="label.previous.short"/></a>
                         <a class="widget_navnext_buttons next nonactive" href="javascript:void(0);" id="click_next2"><spring:message code="label.next"/></a>
                      </div>
                </div>
        </div>
    </div>
	
  </div>
</div>
<script>  
  $(function (){
      $(".js_notification_details_popover").popover({trigger:"hover",html : true, content: function() {
    	var popover_index = $(this).attr('id').substr(13);
        return $('#info_bubble2_'+popover_index).html();
    }});
  });
</script>  
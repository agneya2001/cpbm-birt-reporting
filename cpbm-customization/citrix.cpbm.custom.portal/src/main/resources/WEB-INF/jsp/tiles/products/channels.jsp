<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<script type="text/javascript" src="<%=request.getContextPath() %>/js/channels.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/jquery.iframe-post-form.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/date.format.js"></script>

<script>
$(document).ready(function() {
	<c:choose>
	<c:when test="${not empty previewModeSettings && previewModeSettings.isPreviewMode == true}">
	  var $previewChannelLink = $("#channelgridcontent").find("#channel${previewModeSettings.previewChannelId}");
	  if(($previewChannelLink != null) && (typeof $previewChannelLink.attr('id') !="undefined")){
		  $previewChannelLink.click();
		  brandingTabToggle();
	  }else{
		  $("#channelgridcontent").find(".channels:first").click();
	  }
    
  </c:when>
  <c:otherwise>
  $("#channelgridcontent").find(".channels:first").click();
  </c:otherwise>
  </c:choose>
  $("#channelSearchPanel").attr('value', '');
  initDialog("editServiceChannelSettingsDiv", 600);
});
</script>

  <div class="widget_box">

    <!-- Left Panel Code starts here -->
    <div class="widget_leftpanel">

        <div class="widget_titlebar">
            <h2 id="list_titlebar"><span id="list_all"><spring:message code="label.list.all"/> </span></h2>
            <c:if test="${channelCreationAllowed}">
              <a class="widget_addbutton" id="add_channel_link" href="javascript:void(0);"><spring:message code="label.add.new"/></a>
            </c:if>
        </div>

        <div class="widget_searchpanel">
        <c:if test="${channelCreationAllowed}"> 
          <div id="search_panel">
            <div class="widget_searchpanel textbg">
                <input type="text" class="text" name="search" id="channelSearchPanel"
                       onkeyup="searchChannelByFilter(this)" value="<c:out value="${namePattern}"/>"/>
                <a class="searchicon"></a>
            </div>
          </div>
         </c:if> 
         </div> 
         
         <c:if test="${channelCreationAllowed}">
           <div class="widget_searchpanel">
         <div class="widget_searchcontentarea" id="advanced_search">
            <span class="label"><spring:message code="label.billing.group"/>:</span>     
                 <select class="filterby select expandable-select" id="filter_dropdown">                
                 <option value="all"><spring:message code="ui.product.category.all"/></option>
                 <c:forEach items="${billingGroups}" var="choice" varStatus="status">
                      <option value='<c:out value="${choice.id}"/>' >
                        <c:out value="${choice.name}" escapeXml="false"/>
                     </option>
                 </c:forEach>
            </select>
          </div>
        </div> 
        </c:if>

				<div class="widget_navigation smaller" id="channellistdiv"> 
				      <jsp:include page="/WEB-INF/jsp/tiles/products/searchchannellist.jsp"></jsp:include>
				</div>

      </div>
      <!-- Left Panel Code ends here -->

      <!-- Right Panel Code starts here -->
      
       <div class="widget_rightpanel" id="main_details_content">
      <c:if test="${!channelCreationAllowed}">
          <div style="" class="common_messagebox widget" id="top_message_panel">
            <p id="msg"><spring:message code="js.errors.channel.failed.create.channel_precondition"/></p>
          </div>
       </c:if>
        <jsp:include page="/WEB-INF/jsp/tiles/products/viewchannel.jsp"></jsp:include>
      </div>
      
      <!-- Right Panel Code ends here -->

  </div>
<div id="dialog_edit_channel_container">
  <div  id="dialog_edit_channel" title='<spring:message code="label.channel.edit"/>' style="display: none">
  </div>
</div>
<div id="dialog_add_channel_container">
  <div  id="dialog_add_channel" title='<spring:message code="label.channel.add"/>' style="display: none">
  </div>
</div>

  <div  id="dialog_delete_channel" title='<spring:message code="label.channel.delete"/>' style="display: none">
     <span class="catalog_datepicker_dialog_titles" style="margin-top: 30px; margin-left: 30px;"><spring:message code="js.confirm.channel.removeChannel"/></span>
     <span id="e_msg_channel_delete" style="display: none;" text='<spring:message code="js.errors.channel.failed.delete.channel"/>'></span>
  </div>
  
  <div  id="dialog_reset_channel_brandings" title='<spring:message code="label.reset.channel.brandings"/>' style="display: none">
     <span class="catalog_datepicker_dialog_titles" style="margin-top: 30px; margin-left: 30px;"><spring:message code="js.confirm.reset.channel.brandings"/></span>
  </div>

  <div id="alert_dialog" style="display:none; height: auto; min-height: 65px; width: auto; margin: 25px 10px -20px;"
       title="<spring:message code="ui.label.message"/>"></div>

  <!-- Template for left side view of channel -->
  <li  class="widget_navigationlist channels" onclick="viewChannel(this)" id="channelleftviewtemplate" onmouseover="viewInfoBubble(this)" onmouseout="clearInfoBubble(this)" style="display:none;">
	  <span class="navicon channels" id="nav_icon"></span>
	  <div class="widget_navtitlebox">
	    <span class="title" id="channel_name"></span>
	    <span class="subtitle" id="channel_currencies"></span>
	  </div>
	  <div class="widget_info_popover" id="info_bubble" style="display:none">
	    <div class="popover_wrapper" >
		    <div class="popover_shadow"></div>
		    <div class="popover_contents">
			    <div class="raw_contents">
			    <div class="raw_content_row" id="info_bubble_displayname">
			      <div class="raw_contents_title">
			        <span><spring:message code="label.name"/>:</span>
			      </div>
			      <div class="raw_contents_value">
			        <span id="value"></span>
			      </div>
			    </div>
		
		     <div class="raw_content_row" id="info_bubble_code">
			      <div class="raw_contents_title">
			        <span><spring:message code="ui.label.code"/>:</span>
			      </div>
			      <div class="raw_contents_value">
			        <span id="value"></span>
			      </div>
			    </div>

          <div class="raw_content_row" id="info_bubble_currencies">
            <div class="raw_contents_title">
              <span><spring:message code="ui.products.label.create.catalog.select.currency"/>:</span>
            </div>
            <div class="raw_contents_value">
              <br/>
              <span id="value"></span>
            </div>
          </div>

		     </div>
	     </div>
     </div>
    </div>
  </li>
  
  

<div id="editServiceChannelSettingsDiv" style="overflow:hidden">
</div>
  
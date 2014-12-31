<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<script type="text/javascript">
var campaignsUrl = "<%=request.getContextPath() %>/portal/promotions/";
</script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/campaign.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/date.format.js"></script>
<!-- Starts body -->

		<!-- Body Header  -->
    
<!--  US677 changes starts here -->
 
<spring:message code="dateonly.format" var="date_format"/> 
  
  <div class="widget_box">
    <div class="widget_leftpanel">
      <div class="widget_titlebar">
        <h2 id="list_titlebar"><span id="list_all"><spring:message code="label.list.all"/> </span></h2>
        <c:if test="${atleastOneChannelPresent}">
          <a class="widget_addbutton" onclick="addNewCampaignGet()" href="javascript:void(0);"  ><spring:message code="ui.products.label.list.add.new.campaign"/></a>
        </c:if>
      </div>
      <div class="widget_searchpanel">
        <div id="search_panel" style="margin:8px 0 0 13px;color:#FFFFFF;">
        </div>
      </div>
      <div class="widget_navigation">
        <ul class="widget_navigationlist" id="grid_row_container">
          <c:choose>
            <c:when test="${empty campaignsList || campaignsList == null}">
              <!-- Empty list -->
              <!--look when there is no list starts here-->
              <li class="widget_navigationlist nonlist" id="non_list">
                <span class="navicon userdata"></span>
                <div class="widget_navtitlebox">
                  <span class="newlist">
                    <spring:message code="message.no.campaigns.available"/>
                  </span>
                </div>
                <div class="widget_statusicon nostate">
                </div>
                </li>
                  <!--look when there is no list ends here-->
            </c:when>
            <c:otherwise> 
              <c:forEach var="campaign" items="${campaignsList}" varStatus="status">
                <c:choose>
                  <c:when test="${status.index == 0 && idToBeSelected == null}">
                    <c:set var="selectedCampaign" value="${campaign}"/>
                    <c:set var="selected" value="selected"/>
                  </c:when>
                  <c:when test="${campaign.id == idToBeSelected}" >
                    <c:set var="selectedCampaign" value="${campaign}"/>
                    <c:set var="selected" value="selected"/>
                  </c:when>
                  <c:otherwise>                                        
                    <c:set var="selected" value=""/>
                  </c:otherwise>
                </c:choose>                
                <li  class='<c:out value="widget_navigationlist ${selected} campaignsList"/>' id="<c:out value="row${campaign.id}"/>" onclick="viewCampaign(this)" onmouseover="showInfoBubble(this)" onmouseout="hideInfoBubble(this)">
                  <span class="navicon campaign" id="nav_icon"></span>
                  <div class="widget_navtitlebox">
                    <span class="title"><c:out value="${campaign.code}"/></span>
                    <span class="subtitle">
                    <spring:message code="ui.label.type.subtitle"/>
                    <c:set var="timeOff" value="N"/>
                    <c:if test="${campaign.discountType == 'PERCENTAGE'}"> <!-- This logic needs to be changed, as There is nothing like TimeOff -->
                      <fmt:parseNumber var="roundedPercentage" integerOnly="true" type="number" value="${campaign.percentOff}" />
                      <c:if test="${roundedPercentage eq '100'}">  
                        <spring:message code="ui.campaigns.label.campaign.type.time.off"/>;
                        <c:set var="timeOff" value="Y"/>
                      </c:if>
                    </c:if> 
                    <c:if test="${timeOff eq 'N'}">
                      <spring:message code="ui.campaigns.label.campaign.type.money.off"/>;
                    </c:if>
                   <spring:message code="ui.campaigns.label.create.add.channel"/>:
                    <c:if test="${campaign.campaignPromotionsInChannels != null}">
                          <c:forEach var="campPromoChannel" items="${campaign.campaignPromotionsInChannels}" varStatus="count">
                               <c:if test="${count.index > 0}">,</c:if>
                               <c:out value="${campPromoChannel.channel.code}"/>
                          </c:forEach>
                    </c:if>
                    <c:if test="${campaign.campaignPromotionsInChannels == null}">
                          <spring:message code="ui.campaigns.label.create.channel.type"/>
                    </c:if>
                    </span>
                  </div>
                              
<!--                  Info popover starts here-->
                    <div class="widget_info_popover" id="info_bubble" style="display:none">
                      <div class="popover_wrapper" >
                        <div class="popover_shadow"></div>
                        <div class="popover_contents">
                          <div class="raw_contents">
                            <div class="raw_content_row">
                              <div class="raw_contents_title">
                                <span><spring:message code="ui.campaigns.label.create.promo.code"/>:</span>
                              </div>
                              <div class="raw_contents_value">
                                <span><c:out value="${campaign.promoCode}" /></span>
                              </div>
                            </div>
                            <div class="raw_content_row">
                              <div class="raw_contents_title">
                                <span><spring:message code="ui.campaigns.label.create.trial"/>:</span>
                              </div>
                              <div class="raw_contents_value">
                                <span><spring:message code="label.${campaign.trial}"/></span>
                              </div>
                            </div>
                            <div class="raw_content_row">
                              <div class="raw_contents_title">
                                <span><spring:message code="ui.campaigns.label.create.startdate"/>:</span>
                              </div>
                              <div class="raw_contents_value">
                                <span><fmt:formatDate value="${campaign.startDate}" pattern="${date_format}"/></span>
                              </div>
                            </div>                                                                            
                            <div class="raw_content_row">
                              <div class="raw_contents_title">
                                <span><spring:message code="ui.campaigns.label.create.enddate"/>:</span>
                              </div>
                              <div class="raw_contents_value">
                                <span id="endDate"><fmt:formatDate value="${campaign.endDate}" pattern="${date_format}"/></span>
                              </div>
                            </div> 
                            <div class="raw_content_row">
                              <div class="raw_contents_title">
                                <span><spring:message code="ui.campaigns.label.create.title"/>:</span>
                              </div>
                              <div class="raw_contents_value">
                                <span class="subtitle" id="title"><c:out value="${campaign.title}"/></span>
                              </div>
                            </div>
                            <div class="raw_content_row">
                              <div class="raw_contents_title">
                                <span><spring:message code="ui.campaigns.label.edit.enabled"/>:</span>
                              </div>
                              <div class="raw_contents_value">
                                <span id="enable"><spring:message code="label.${campaign.enabled}"/></span>
                              </div>
                            </div>
                            
                           <div class="raw_content_row">
                              <div class="raw_contents_title">
                                  <span><spring:message code="ui.campaigns.label.create.add.channel"/>:</span>
                              </div>
                              <div class="raw_contents_value">
                               <c:if test="${campaign.campaignPromotionsInChannels != null}">
                                  <c:forEach var="campPromoChannel" items="${campaign.campaignPromotionsInChannels}" varStatus="count">
                                      <c:if test="${count.index > 0}">,</c:if>
                                      <c:out value="${campPromoChannel.channel.code}"/>
                                  </c:forEach>
                               </c:if>
                               <c:if test="${campaign.campaignPromotionsInChannels == null}">
                                   <spring:message code="ui.campaigns.label.create.channel.type"/>
                                </c:if>
                              </div>
                           </div>  
                                
                          </div>
                        </div>
                      </div>
                    </div>
<!--                  Info popover ends here-->
                </li>
              </c:forEach>
            </c:otherwise>
          </c:choose>                              
        </ul>
      </div>
<input type="hidden" id="current_page"  value="<c:out value="${current_page}"/>"/>
<input type="hidden" id="default_page_size"  value="<c:out value="${pageSize}"/>"/>
                        
     <!-- link to next and previous page navigation -->                   
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
    <div class="widget_rightpanel" id="viewcampaignDiv">
      <c:if test="${empty campaignsList || campaignsList == null}">
        <jsp:include page="/WEB-INF/jsp/tiles/products/viewcampaign.jsp"></jsp:include>
      </c:if>
     
    </div>
    
 </div>
 
 
 <li   class = "widget_navigationlist" id="campaignViewTemplate" onmouseout="hideInfoBubble(this)" onmouseover="showInfoBubble(this)" onclick="viewCampaign(this)" style = "display:none">
  <span id="nav_icon" class="navicon campaign"> </span>
      <div class="widget_navtitlebox">
           <span class="title"> </span>
           <span class="subtitle"> </span>
      </div>
      <div style="display:none" id="info_bubble" class="widget_info_popover">
           <div class="popover_wrapper">
              <div class="popover_shadow"> </div>
              <div class="popover_contents">
                  <div class="raw_contents">
                      <div class="raw_content_row">
                          <div class="raw_contents_title">
                                <span> <spring:message code="ui.campaigns.label.create.promo.code"/>: </span>
                          </div>
                          <div class="raw_contents_value">
                                <span id = "promoCode" > </span>
                          </div>
                      </div>
                      <div class="raw_content_row">
                          <div class="raw_contents_title">
                                <span><spring:message code="ui.campaigns.label.create.trial"/>:</span>
                          </div>
                          <div class="raw_contents_value">
                                <span id = "trial" ></span>
                          </div>
                    </div>
                    <div class="raw_content_row">
                         <div class="raw_contents_title">
                               <span><spring:message code="ui.campaigns.label.create.startdate"/>:</span>
                          </div>
                          <div class="raw_contents_value">
                               <span id = "startDate"> </span>
                          </div>
                    </div>      
                    <div class="raw_content_row">
                         <div class="raw_contents_title">
                               <span><spring:message code="ui.campaigns.label.create.enddate"/>:</span>
                         </div>
                         <div class="raw_contents_value">
                               <span id = "endDate"> </span>
                         </div>
                    </div> 
                    <div class="raw_content_row">
                         <div class="raw_contents_title">
                              <span><spring:message code="ui.campaigns.label.create.title"/>:</span>
                         </div>
                         <div class="raw_contents_value">
                              <span class="subtitle" id = "title"> </span>
                         </div>
                    </div>
                    <div class="raw_content_row">
                         <div class="raw_contents_title">
                             <span><spring:message code="ui.campaigns.label.edit.enabled"/>:</span>
                         </div>
                         <div class="raw_contents_value">
                              <span id = "enable"></span>
                         </div>
                    </div>
                    <div class="raw_content_row">
                         <div class="raw_contents_title">
                             <span><spring:message code="ui.campaigns.label.create.add.channel"/>:</span>
                          </div>
                          <div class="raw_contents_value" id = "channelSubtitle">
                          </div>
                    </div>  
             </div>
            </div>
           </div>
          </div>
      </li>
 
 <div id="addnewcampaignDiv" title="<spring:message code="ui.products.label.list.add.new.campaign"/>"> </div>
 <!--  US677 changes ends here -->   
	   
                	
               
                  <!-- Start List Campaigns -->	
                 
	    <!-- End List Campaigns -->

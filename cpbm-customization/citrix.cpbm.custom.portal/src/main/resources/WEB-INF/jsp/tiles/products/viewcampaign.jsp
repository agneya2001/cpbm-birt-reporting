<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<script type="text/javascript">
var campaignsUrl = "<%=request.getContextPath() %>/portal/promotions/";
</script>
<spring:message code="dateonly.format" var="date_format"/> 

<!--  US677 changes starts here -->   
  
                      
  <div class="widget_rightpanel">
    <div class="widget_actionbar">
      <div class="widget_actionarea" id="top_actions" >
        <div id="spinning_wheel" style="display:none">
          <div class="maindetails_footer_loadingpanel">
          </div>
          <div class="maindetails_footer_loadingbox first">
            <div class="maindetails_footer_loadingicon">
            </div>
            <p id="in_process_text"></p>
          </div>
        </div>
        <div class="widget_moreactions action_menu_container" title="<spring:message code='manage'/>">
          <!--Actions popover starts here-->
            <div class="widget_actionpopover" id="action_menu"  style="display:none;">
              <div class="widget_actionpopover_top"></div>
              <div class="widget_actionpopover_mid">
                <ul class="widget_actionpoplist">
                  <c:if test="${campaign.state != 'EXPIRED'}">
                    <li class="editCampaign_link"><spring:message code="ui.campaigns.label.view.edit"/></li>
                  </c:if>
                  <c:if test="${campaign.state == 'EXPIRED'}">
                    <li><spring:message code="label.no.actions.available"/></li>
                  </c:if>
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
                <span><spring:message code="ui.campaigns.label.create.code"/></span>
              </div>
              <div class="widget_grid_description">
                <span id="campaignCode"><c:out value="${campaign.code}"></c:out></span>
              </div>
            </div>
            
            
            <div class="widget_grid master even">
            <div class="widget_grid_labels">
              <span><spring:message code="ui.campaigns.label.create.title"/></span>
            </div>
            <div class="widget_grid_description" title="<c:out value="${campaign.title}"></c:out>">
              <span class="ellipsis" id="title"><c:out value="${campaign.title}"></c:out></span>
            </div>
          </div>
          
            <div class="widget_grid master even">
              <div class="widget_grid_labels">
                <span><spring:message code="label.status"/></span>
              </div>
              <div class="widget_grid_description">
                <c:if test="${campaign.stateLower ne null}">
                  <span id="title"><spring:message code="ui.campaign.label.state.${campaign.stateLower}"/></span>
                </c:if>
              </div>
            </div>
          </div>
          <div class="widget_masterbigicons campaign">
          </div>
        </div>
      </div>
      <div class="widget_browser_contentarea">
        <ul class="widgets_detailstab">
          <li class="widgets_detailstab active" id="details_tab"><spring:message code="label.details"/></li>
          <li class="widgets_detailstab nonactive" id="channels_tab"><spring:message code="ui.campaigns.label.create.add.channel"/></li>
        </ul>
        <div class="widget_details_actionbox">
          <ul class="widget_detail_actionpanel"></ul>
        </div>
        <div class="widget_browsergrid_wrapper details" id="details_content">
          <div class="widget_grid details even">
            <div class="widget_grid_labels">
              <span><spring:message code="ui.campaigns.label.create.promo.code"/></span>
            </div>
            <div class="widget_grid_description" >
              <span id="promoCode"><c:out value="${campaign.promoCode}"></c:out></span>
            </div>
          </div>    
          <div class="widget_grid details odd">
            <div class="widget_grid_labels">
              <span><spring:message code="label.type"/></span>
            </div>
            <div class="widget_grid_description" >
              <c:if test="${campaign ne null}">
                <c:set var="timeOff" value="N"/>
                <c:if test="${campaign.discountType == 'PERCENTAGE'}"> <!-- This logic needs to be changed, as There is nothing like TimeOff -->
                  <fmt:parseNumber var="roundedPercentage" integerOnly="true" type="number" value="${campaign.percentOff}" />
                      <c:if test="${roundedPercentage eq '100'}"> 
                    <span><spring:message code="ui.campaigns.label.campaign.type.time.off"/></span>
                    <c:set var="timeOff" value="Y"/>
                  </c:if>
                </c:if> 
                <c:if test="${timeOff eq 'N'}">
                  <span><spring:message code="ui.campaigns.label.campaign.type.money.off"/></span>
                </c:if>
              </c:if>
            </div>
          </div>    
              
          <div class="widget_grid details odd">
            <div class="widget_grid_labels">
              <span><spring:message code="ui.campaigns.label.create.startdate"/></span>
            </div>
            <div class="widget_grid_description">
              <span id="startDate"><fmt:formatDate value="${campaign.startDate}" pattern="${date_format}"/></span>
            </div>
          </div>           
          <div class="widget_grid details even">
            <div class="widget_grid_labels">
              <span><spring:message code="ui.campaigns.label.create.enddate"/></span>
            </div>
            <div class="widget_grid_description">
              <span id="startDate"><fmt:formatDate value="${campaign.endDate}" pattern="${date_format}"/></span>
            </div>
          </div>
          <div class="widget_grid details odd">
            <div class="widget_grid_labels">
              <span><spring:message code="ui.campaigns.label.create.trial"/></span>
            </div>
            <div class="widget_grid_description">
              <span id="trial">
                <c:if test="${campaign.trial == true}">
                  <spring:message code="label.true"/>
                </c:if>
                <c:if test="${campaign.trial == false}">
                  <spring:message code="label.false"/>
                </c:if>
              </span>
            </div>
          </div>    
        <c:if test="${campaign == null ||campaign.durationDays != 0}">  
          <div class="widget_grid details even">
            <div class="widget_grid_labels">
              <span>
                <c:if test="${campaign == null || campaign.trial == true}">
                  <spring:message code="ui.campaigns.label.create.duration"/>
                </c:if>
                <c:if test="${campaign.trial == false}">
                  <spring:message code="ui.campaigns.label.create.duration.billingperiod"/>
                </c:if>
              </span>
            </div>
            <div class="widget_grid_description">
              <span id="durationDays"><c:out value="${campaign.durationDays}"></c:out></span>
            </div>
          </div>
        </c:if>
        <c:if test="${campaign.durationDays == 0}">  
          <div class="widget_grid details even">
            <div class="widget_grid_labels">
              <span><spring:message code="ui.campaigns.label.create.unlimitedduration"/></span>
            </div>
            <div class="widget_grid_description">
              <span id="durationDays"><spring:message code="label.true"/></span>
            </div>
          </div>           
        </c:if>    
        <c:if test="${campaign.maxAccounts != 0}">
          <div class="widget_grid details odd">
            <div class="widget_grid_labels">
              <span><spring:message code="ui.campaigns.label.create.max.accounts"/></span>
            </div>
            <div class="widget_grid_description">
              <span id="maxAccounts"><c:out value="${campaign.maxAccounts}" /></span>
            </div>
          </div>
        </c:if>
        <c:if test="${campaign.maxAccounts == 0}">
          <div class="widget_grid details odd">
            <div class="widget_grid_labels">
              <span><spring:message code="ui.campaigns.label.create.unlimitedaccount"/></span>
            </div>
            <div class="widget_grid_description">
              <span id="maxAccounts"><spring:message code="label.true"/></span>
            </div>
          </div>
        </c:if>             
          <div class="widget_grid details even">
            <div class="widget_grid_labels">
              <span><spring:message code="ui.campaigns.label.edit.enabled"/></span>
            </div>
            <div class="widget_grid_description">
              <span id="enabled">
                <c:if test="${campaign.stateLower ne null}">
                  <c:if test="${campaign.enabled}">
                    <spring:message code="label.true"/>
                  </c:if>
                  <c:if test="${!campaign.enabled}">
                    <spring:message code="label.false"/>
                  </c:if>
                </c:if>
              </span>
            </div>
          </div>
                <c:if test="${campaign.discountType == 'PERCENTAGE'}">  
          <div class="widget_grid details odd">
            <div class="widget_grid_labels">
              <span><spring:message code="ui.campaigns.label.create.discount.type.percent.off"/></span>
            </div>
            <div class="widget_grid_description">
              <span id="percentOff"><fmt:formatNumber pattern="${currencyFormat}" minFractionDigits="${minFractionDigits}" value="${campaign.percentOff}"/></span>
            </div>
          </div>
        </c:if>
        <c:if test="${campaign.discountType == 'FIXED_AMOUNT'}">
          <div class="widget_grid details odd">
            <div class="widget_details_inlinegrid" >
              <div class="widget_grid inline header widget_navtitlebox">
                <span  class="title" style="margin-left:5px;width: auto;color:#FFF;"><spring:message code="ui.campaigns.label.create.discount.type.amount.off"></spring:message> 
                </span>
              </div>
              <div class="widget_inline_chargesbox">
                <div class="widget_grid inline subheader">
                  <c:forEach var="amount" items="${campaign.campaignPromotionDiscountAmount}" varStatus="status">
                    <div class="widget_grid_cell borders" style="width:76px;">
                      <div class="widget_flagbox catbundles">
                        <div class="widget_currencyflag">
                          <img src="../../images/flags/<c:out value="${amount.currencyValue.currencyCode}"/>.gif" alt="" />
                        </div>
                      </div>
                      <span class="subheader"><c:out value="${amount.currencyValue.currencyCode}"/></span>
                    </div>
                  </c:forEach>
                </div>
                <div class="<c:out value="widget_grid inline odd"/>">
                  <c:forEach var="amount" items="${campaign.campaignPromotionDiscountAmount}">
                    <div class="widget_grid_cell borders" style="height:27px; width:76px;">
                      <span class="celltext">
                        <c:out value="${amount.currencyValue.sign}"/><fmt:formatNumber pattern="${currencyFormat}" minFractionDigits="${minFractionDigits}" value="${amount.discount}"/>
                      </span>
                    </div>
                  </c:forEach>
                </div> 
              </div>
            </div>
          </div>
        </c:if>                              
      </div>
      <div id="tab_spinning_wheel" class="rightpanel_mainloader_panel" style="display: none;">
        <div class="rightpanel_mainloaderbox">
          <div class="rightpanel_mainloader_animatedicon">
          </div>
          <p><spring:message code="label.loading"/> &hellip;</p>
        </div>
      </div>
      <div class="widget_browsergrid_wrapper wsubaction" id="associated_channel_content" style="overflow-x: hidden;overflow-y: auto;display: none;">
        <div class="widget_details_inlinegrid" id="associated_channels_details">
        <div class="widget_grid inline subheader">
      
        <div class="widget_grid_cell" style="width:40%;">
            <span class="subheader"><spring:message code="label.name"/></span>
          </div> 
          <div class="widget_grid_cell" style="width:24%;">
            <span class="subheader"><spring:message code="ui.label.code"/></span>
          </div>
          
    
         </div>
  
          <c:if test="${campaign ne null}">
            <c:if test="${campaign.campaignPromotionsInChannels != null}">
                <c:forEach var="campPromoChannel" items="${campaign.campaignPromotionsInChannels}">
                <c:choose>
                  <c:when test="${status.index % 2 == 0}">
                    <c:set var="rowClass" value="odd"/>
                  </c:when>
                  <c:otherwise>
                      <c:set var="rowClass" value="even"/>
                  </c:otherwise>
                </c:choose>
                  <div class="<c:out value="widget_grid inline ${rowClass}"/>"  >
                    <div class="widget_grid_cell" style="width:40%;">
                    <span class="celltext" style= "overflow:hidden; text-overflow:ellipsis; white-space:nowrap; width: 90%" title = "<c:out value="${campPromoChannel.channel.name}" />"> <c:out value="${campPromoChannel.channel.name}" /></span>
                    </div>
                    <div class="widget_grid_cell" style="width:24%;">
                    <span class="celltext" style= "overflow:hidden; text-overflow:ellipsis; white-space:nowrap; width: 90%" title = "<c:out value="${campPromoChannel.channel.code}" />"> <c:out value="${campPromoChannel.channel.code}" /></span>
                    </div>
                    
                  </div>
                  
                </c:forEach>
            </c:if>
            <c:if test="${campaign.campaignPromotionsInChannels == null}">
              <div class="<c:out value="widget_grid inline ${rowClass}"/>"  >
                <div class="widget_grid_cell" style="width:40%;">
                  <span class="celltext"><spring:message code="ui.campaigns.label.create.channel.type"/></span>
                </div>
                <div class="widget_grid_cell" style="width:24%;">
                  <span class="celltext">&nbsp;</span>
                </div>
             </div>
              
            </c:if>
          </c:if>
            
        </div>

      </div> 
    </div>
  </div>
</div>


<input type='hidden' id='campaign_id' value='<c:out value="${campaign.id}"/>'>
         
<div id="editcampaignDiv" title="<spring:message code="ui.campaign.label.edit.edit"/> <c:out value="${campaign.code}"/>"> </div>

<!--  US677 changes ends here -->   
  


<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:include page="/WEB-INF/jsp/tiles/shared/js_messages.jsp"></jsp:include>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/validator.js"></script>

<!-- Start Account Type Details --> 

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
  <div class="widget_browsermaster">
      <div class="widget_browser_contentarea">
          <div class="widget_browsergrid_wrapper master">
              <div class="widget_grid master even first">
                <div class="widget_grid_labels">
                  <span><spring:message code="ui.accounttypes.list.page.ID"/></span>
                </div>
                <div class="widget_grid_description">
                  <span><c:out value="${accounttype.id}"/></span>
                </div>
              </div>
                
              <div class="widget_grid master even">
                <div class="widget_grid_labels">
                  <span><spring:message code="ui.accounttypes.list.page.name"/></span>
                </div>
                <div class="widget_grid_description">
                  <span>  
                   <spring:message code="registration.accounttype.${accounttype.nameLower}"/>
                  </span>
                </div>
              </div>
              <div class="widget_grid master even">
                <div class="widget_grid_labels">
                  <span><spring:message code="ui.accounttypes.list.page.trialorregular"/></span>
                </div>
                <div class="widget_grid_description">
                  <c:choose>
                    <c:when test="${(accounttype.trial)}">
                     <span id="vm_state_icon" class="destroyedicon"></span>
                     <span id="state" class="stopped"><spring:message code="ui.accounttypes.list.page.trial"/></span>
                    </c:when>
                    <c:otherwise>
                     <span id="vm_state_icon" class="runningicon"></span>
                     <span id="state" class="running"><spring:message code="ui.accounttypes.list.page.regular"/></span>
                    </c:otherwise>
                  </c:choose>   
                </div>
              </div>
          </div>
          <div class='widget_masterbigicons accounts <c:out value="${accounttype.nameLower}"/>'></div>
      </div>
  </div>
  <div class="widget_browser_contentarea">
    <ul class="widgets_detailstab">
        <li class='widgets_detailstab <c:choose><c:when test="${tab=='1'}">active</c:when><c:otherwise>nonactive</c:otherwise></c:choose>' id="details_tab"><spring:message code="label.details"/></li>
        <li class="widgets_detailstab <c:choose><c:when test="${tab=='2'}">active</c:when><c:otherwise>nonactive</c:otherwise></c:choose>" id="onboarding_tab"><spring:message code="ui.accounttypes.list.page.onboardingcontrols"/></li>
        <li class="widgets_detailstab <c:choose><c:when test="${tab=='3'}">active</c:when><c:otherwise>nonactive</c:otherwise></c:choose>" id="iaas_tab"><spring:message code="ui.accounttypes.list.page.servicecontrols"/></li>
        <li class="widgets_detailstab <c:choose><c:when test="${tab=='4'}">active</c:when><c:otherwise>nonactive</c:otherwise></c:choose>" id="billing_tab"><spring:message code="ui.accounttypes.list.page.billingcontrols"/></li>
        <li class="widgets_detailstab <c:choose><c:when test="${tab=='5'}">active</c:when><c:otherwise>nonactive</c:otherwise></c:choose>" id="creditexposure_tab"><spring:message code="ui.accounttypes.list.page.lce"/></li>
        <c:if test="${accounttype.depositRequired}">
          <li class="widgets_detailstab <c:choose><c:when test="${tab=='6'}">active</c:when><c:otherwise>nonactive</c:otherwise></c:choose>" id="initialdeposit_tab"><spring:message code="ui.accounttypes.list.page.initialdeposit" /></li>
        </c:if>
    </ul>
    <div id="details_content" <c:choose><c:when test="${tab=='1'}"></c:when><c:otherwise>style="display:none"</c:otherwise></c:choose>>
      <div class="widget_details_actionbox">
        <ul class="widget_detail_actionpanel">
          <c:if test="${(accounttype.name != 'SYSTEM')}">
            <a href="javascript:void(0);" onclick="editAccountTypeGet(this, 1);" id="<c:out value="edit${accounttype.id}"/>"><spring:message code="ui.label.tenant.view.edit"/></a>
          </c:if>                 
        </ul>
      </div>
      <div class="widget_browsergrid_wrapper details" >
           <div class="widget_grid details even">
              <div class="widget_grid_labels">
                  <span><spring:message code="ui.accounttypes.list.page.desc"/></span>
              </div>
              <div class="widget_grid_description">
                  <span id="accounttype_description"><spring:message code="registration.accounttype.description.${accounttype.nameLower}"/></span>
              </div>
            
          </div>
          <div class="widget_grid details odd">
	            <div class="widget_grid_labels">
	                <span><spring:message code="ui.accounttypes.list.page.mu"/></span>
	            </div>
	            <div class="widget_grid_description" >
	                <span id="accounttype_max_users"><c:out value="${accounttype.maxUsers}"/></span>
	            </div>
          </div>    
       </div>
    </div>
    <div id="onboarding_content" <c:choose><c:when test="${tab=='2'}"></c:when><c:otherwise>style="display:none"</c:otherwise></c:choose>>
       <div class="widget_details_actionbox">
        <ul class="widget_detail_actionpanel">
          <c:if test="${(accounttype.name != 'SYSTEM')}">
            <a href="javascript:void(0);" onclick="editAccountTypeGet(this, 2);" id="<c:out value="edit${accounttype.id}"/>"><spring:message code="ui.label.tenant.view.edit"/></a>
          </c:if>                 
        </ul>
      </div>
      <div class="widget_browsergrid_wrapper details" >
          <div class="widget_grid details even">
              <div class="widget_grid_labels" style="width:215px;">
                  <span style="width:200px;"><spring:message code="ui.accounttypes.list.page.sra"/></span>
              </div>
              <div class="widget_grid_description">
                  <span id="accounttype_sra"><spring:message code="label.${accounttype.selfRegistrationAllowed}"/></span>
              </div>
          </div>
           
          <c:if test="${!(accounttype.trial)}">         
          <div class="widget_grid details odd">
              <div class="widget_grid_labels" style="width:215px;">
                  <span style="width:200px;" ><spring:message code="ui.accounttypes.list.page.mra"/></span>
              </div>
              <div class="widget_grid_description" >
                  <span id="accounttype_mra"><spring:message code="label.${accounttype.manualRegistrationAllowed}"/></span>
              </div>
          </div>    
          </c:if>
          <div class="widget_grid details even">
              <div class="widget_grid_labels" style="width:215px;">
                  <span style="width:200px;"><spring:message code="ui.accounttypes.list.page.ma"/></span>
              </div>
              <div class="widget_grid_description">
                  <span id="accounttype_ma"><spring:message code="label.${accounttype.manualActivation}"/></span>
              </div>
          </div>
          <div class="widget_grid details odd">
              <div class="widget_grid_labels" style="width:215px;">
                  <span style="width:200px;"> <spring:message code="ui.accounttypes.list.page.dsr"/></span>
              </div>
              <div class="widget_grid_description" >
                  <span id="accounttype_dsr"><spring:message code="label.${accounttype.defaultSelfRegistered}"/></span>
              </div>
          </div>
          <div class="widget_grid details even">
              <div class="widget_grid_labels" style="width:215px;">
                  <span style="width:200px;"><spring:message code="ui.accounttypes.list.page.dr"/></span>
              </div>
              <div class="widget_grid_description">
                  <span id="accounttype_dr"> <spring:message code="label.${accounttype.defaultRegistered}"/></span>
              </div>
          </div>
          <div class="widget_grid details odd">
              <div class="widget_grid_labels" style="width:215px;">
                  <span style="width:200px;"><spring:message code="ui.accounttypes.list.page.accountRestrictionGracePeriod"/></span>
              </div>
              <div class="widget_grid_description" >
                  <span id="accounttype_rgp"><c:out value="${accounttype.accountRestrictionGracePeriod}"/></span>
              </div>
          </div>  
          <c:if test="${!(accounttype.trial)}">  
	          <div class="widget_grid details even">
	              <div class="widget_grid_labels" style="width:215px;">
	                  <span style="width:200px;"><spring:message code="ui.accounttypes.list.page.initialDepositRequired"/></span>
	              </div>
	              <div class="widget_grid_description">
	                  <span id="accounttype_depreq"><spring:message code="label.${accounttype.depositRequired}"/></span>
	              </div>
	          </div>
          </c:if>
          <div class="widget_grid details odd">
              <div class="widget_grid_labels" style="width:215px;">
                  <span style="width:200px;"><spring:message code="ui.accounttypes.list.page.pin"/></span>
              </div>
              <div class="widget_grid_description" >
                  <span id="accounttype_payinforeq"><spring:message code="label.${accounttype.paymentInfoRequired}"/></span>
              </div>
          </div>    
              
      </div>
    </div>

		<div  id="servicecontrols_content" <c:choose><c:when test="${tab=='3'}"></c:when><c:otherwise>style="display:none"</c:otherwise></c:choose>>
			
			<c:if test="${(accounttype.name != 'SYSTEM')}">
				<c:if test="${not empty mapOfControlsPerInstance}">
				<div class="widget_details_actionbox" style="height:35px;">
        <div style="padding:8px;">
					<c:if test="${not empty services}">
						<spring:message code="ui.label.service.sub.title" />
						<select id="selectedService" class="select" style="max-width:200px;" onchange="changeInstances()">
							<c:forEach var="service" items="${services}" varStatus="status">
								<option value="${service.uuid}"><spring:message code="${service.serviceName}.service.name" /></option>
							</c:forEach>
						</select>
					</c:if>
					<c:if test="${not empty instances}">
						<spring:message code="ui.home_service.page.title.service.instance" />
						<select id="selectedInstance" class="select" style="max-width: 200px;" onchange="showControls(this)">
							<!-- populate on service change -->
						</select>
					</c:if>
					<c:if test="${not empty instances}">
						<select id="hiddeninstances" class="select" style="display: none;">
							<c:forEach var="instance" items="${instances}" varStatus="status">
								<option value="${instance.service.uuid}.${instance.uuid}">${instance.name}</option>
							</c:forEach>
						</select>
					</c:if>
        </div>
      </div>
      <c:forEach var="entry" items="${mapOfControlsPerInstance}">
            <c:set var="key" value="${entry.key}" />
            <c:set var="controls" value="${entry.value}" />

            <div class="widget_details_actionbox" id="instanceparam_${key}">
              <ul class="widget_detail_actionpanel">
                <c:if test="${mapOfControlMetaDataPerInstance[key] > 0}">
                  <a href="javascript:void(0);"
                   onclick="addAccountTypeControlsGet('<c:out value="${key}"/>', '<c:out value="${accounttype.id}"/>')">
                   <spring:message code="label.edit" /> 
                  </a>
                </c:if>
              </ul>
            </div>
            <c:choose>
            <c:when test="${not empty controls}">
            <div class="widget_browsergrid_wrapper details" id="instanceparam_${key}" style="height:380px" >
              <c:forEach var="control" items="${controls}" varStatus="status">
                <c:set var="rowClass" value="odd" />
                <c:if test="${status.index % 2 ==0}">
                  <c:set var="rowClass" value="even" />
                </c:if>
                <div class="widget_grid details ${rowClass}">
                  <div class="widget_grid_labels" style="width:200px">
                    <span style="width:auto">
                      <spring:message code="${control.service.serviceName}.${control.name}.name" />
                    </span>
                  </div>
                  <div class="widget_grid_description">
                    <span id="${control.name}">
                      <c:out value="${control.value}" />
                    </span>
                  </div>
                </div>
              </c:forEach>
            </div>
            </c:when>
            <c:otherwise>
              <div class="widget_browsergrid_wrapper details" id="instanceparam_${key}" style="height:380px" >
              <c:set var="controls" value="${initialMapForView[key]}"></c:set>
              <c:forEach var="control" items="${controls}" varStatus="status">
                <c:set var="rowClass" value="odd" />
                <c:if test="${status.index % 2 ==0}">
                  <c:set var="rowClass" value="even" /> 
                </c:if>
                <div class="widget_grid details ${rowClass}">
                  <div class="widget_grid_labels" style="width:200px">
                    <span style="width:auto">
                      <spring:message code="${control.service.serviceName}.${control.name}.name" />
                    </span>
                  </div>
                  <div class="widget_grid_description">
                  <span id="${control.name}">
                    </span>
                  </div>
                </div>
              </c:forEach>
            </div>
            </c:otherwise>
            </c:choose>
          </c:forEach>
				</c:if>
			</c:if>
		</div>

		<div id="billing_content" <c:choose><c:when test="${tab=='4'}"></c:when><c:otherwise>style="display:none"</c:otherwise></c:choose>>
      <div class="widget_details_actionbox">
        <ul class="widget_detail_actionpanel">
          <c:if test="${(accounttype.name != 'SYSTEM')}">
            <a href="javascript:void(0);" onclick="editAccountTypeGet(this, 4);" id="<c:out value="edit${accounttype.id}"/>"><spring:message code="ui.label.tenant.view.edit"/></a>
          </c:if>                 
        </ul>
      </div>
      <div class="widget_browsergrid_wrapper details" >
          <div class="widget_grid details even">
              <div class="widget_grid_labels" style="width:215px;">
                  <span style="width:200px;"><spring:message code="ui.accounttypes.list.page.nb"/></span>
              </div>
              <div class="widget_grid_description">
                  <span id="accounttype_description"><spring:message code="label.${accounttype.notionalBilling}"/></span>
              </div>
          </div>
          <div class="widget_grid details odd">
              <div class="widget_grid_labels" style="width:215px;">
                  <span style="width:200px;"><spring:message code="ui.accounttypes.list.page.apn"/></span>
              </div>
              <div class="widget_grid_description" >
                  <span id="accounttype_max_users"><spring:message code="label.${accounttype.autoPayRequired}"/></span>
              </div>
          </div>
          <div class="widget_grid details even">
              <div class="widget_grid_labels" style="width:215px;">
                  <span style="width:200px;"><spring:message code="ui.accounttypes.list.page.ceb"/></span>
              </div>
              <div class="widget_grid_description">
                  <span id="accounttype_description"><spring:message code="ui.label.creditExposureBreach.${accounttype.creditExposureBreach}"/></span>
              </div>
          </div>
          <div class="widget_grid details odd">
              <div class="widget_grid_labels" style="width:215px;">
                  <span style="width:200px;"><spring:message code="ui.accounttypes.list.page.ec"/></span>
              </div>
              <div class="widget_grid_description" >
                  <span id="accounttype_max_users"><spring:message code="label.${accounttype.extendCredit}"/></span>
              </div>
          </div>
          <c:if test="${(!accounttype.trial && accounttype.name != 'SYSTEM' && accounttype.paymentModes != 8)}">
          <div class="widget_grid details even">
              <div class="widget_grid_labels" style="width:215px;">
                  <span style="width:200px;"><spring:message code="ui.accounttypes.list.page.preAuthRequired"/></span>
              </div>
              <div class="widget_grid_description">
                  <span id="accounttype_"><spring:message code="label.${accounttype.preAuthRequired}"/></span>
              </div>
          </div>
          </c:if>
      </div>
    </div>
    <div id="creditexposure_content" <c:choose><c:when test="${tab=='5'}"></c:when><c:otherwise>style="display:none"</c:otherwise></c:choose>>
      <div class="widget_details_actionbox">
        <ul class="widget_detail_actionpanel">  
         <c:if test="${(accounttype.name != 'SYSTEM')}">
          <a href="javascript:void(0);" onclick="editcreditlimitexposureGet(this);" id="<c:out value="edit${accounttype.id}"/>"><spring:message code="ui.label.tenant.view.edit"/></a>
         </c:if>
        </ul>
      </div>
      <div class="widget_browsergrid_wrapper details" >
        <c:forEach items="${accounttype.accountTypeCreditExposureList}" varStatus="status" var="accountTypeCreditExposure">
          <div class='widget_grid inlineodd' style="width:629px;"id="<c:out value="${productPrice.currencyValue.currencyCode}" />">
	          <div class="widget_grid_labels">
	              <span><spring:message code="currency.longname.${accountTypeCreditExposure.currencyValue.currencyCode}"></spring:message></span>
	          </div>
	          <div class="widget_grid_description">
              <span id="price">
                <div style="float:left;"><c:out value="${accountTypeCreditExposure.currencyValue.sign}"/>&nbsp;</div>
                <div style="float:left;"><fmt:formatNumber pattern="${currencyFormat}" minFractionDigits="${minFractionDigits}" value="${accountTypeCreditExposure.creditExposureLimit}"/></div>
              </span>
	          </div>
	          <div class="widget_flagbox">
	             <div class="widget_currencyflag">
	               <img alt="" src="../../../images/flags/<c:out value="${accountTypeCreditExposure.currencyValue.flag}" />">
	             </div>
	          </div>
          </div>
        </c:forEach>
      </div>
    </div>
    <c:if test="${accounttype.depositRequired}">
    <div id="initialdeposit_content" <c:choose><c:when test="${tab=='6'}"></c:when><c:otherwise>style="display:none"</c:otherwise></c:choose>>
      <div class="widget_details_actionbox">
        <ul class="widget_detail_actionpanel"> 
        <c:if test="${(accounttype.name != 'SYSTEM')}">
          <a href="javascript:void(0);" onclick="editinitialdepositGet(this);" class="editinitialdeposit" id="<c:out value="edit${accounttype.id}"/>"><spring:message code="ui.label.tenant.view.edit" /></a>
         </c:if>                   
        </ul>
      </div>
      <div class="widget_browsergrid_wrapper details" >
        <c:forEach items="${accounttype.accountTypeCreditExposureList}" varStatus="status" var="accountTypeCreditExposure">
          <div class='widget_grid inlineodd' id="<c:out value="${productPrice.currencyValue.currencyCode}" />">
            <div class="widget_grid_labels">
              <span><spring:message code="currency.longname.${accountTypeCreditExposure.currencyValue.currencyCode}"></spring:message></span>
            </div>
            <div class="widget_grid_description">
              <span id="price">
                <div style="float:left;"><c:out value="${accountTypeCreditExposure.currencyValue.sign}"/>&nbsp;</div>
                <div style="float:left;"><fmt:formatNumber pattern="${currencyFormat}" minFractionDigits="${minFractionDigits}" value="${accountTypeCreditExposure.initialDeposit}"/></div>
              </span>
            </div>
            <div class="widget_flagbox">
               <div class="widget_currencyflag">
                   <img alt="" src="../../../images/flags/<c:out value="${accountTypeCreditExposure.currencyValue.flag}" />">
               </div>
            </div>
          </div>
        </c:forEach>
      </div>
    </div>
    </c:if>
  </div>
</div>     
<!-- End view Product Details -->
                    

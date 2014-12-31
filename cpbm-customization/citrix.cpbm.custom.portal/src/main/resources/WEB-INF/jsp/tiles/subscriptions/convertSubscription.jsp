<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<style>
.popover-content {
    padding: 5px;
}
</style>

<c:set var="subscriptionsAvailable" value="false" scope="request" />
<c:if test="${not empty activeSubscriptions}">
  <c:set var="subscriptionsAvailable" value="true" scope="request" />
</c:if>

<c:set var="bundlesAvailable" value="false" scope="request" />
<c:if test="${not empty activeBundles}">
  <c:set var="bundlesAvailable" value="true" scope="request" />
</c:if>

<c:choose>
  <c:when test="${bundlesAvailable && !subscriptionsAvailable}">
    <spring:message code="message.title.convert.subscription.no.unused.purchase.new" var="step1Title" />
  </c:when>
  <c:when test="${!bundlesAvailable && subscriptionsAvailable}">
    <spring:message code="message.title.convert.subscription.choose.existing" var="step1Title" />
  </c:when>
  <c:when test="${bundlesAvailable && subscriptionsAvailable}">
    <spring:message code="message.title.convert.subscription.purchase.or.select.existing" var="step1Title" />
  </c:when>
</c:choose>

<div class="widget_wizardcontainer convert_subscription">

  <!--step 1 starts here-->
    <div id="step1" class="js_stepcontent">
      <input type="hidden" value="step2" name="nextstep" id="nextstep">
      <div class="widgetwizard_stepsbox">
        <div class="widgetwizard_steps_contentcontainer">
          <div class="widgetwizard_stepscenterbar">
            <ul>
              <li class="widgetwizard_stepscenterbar first">
                <span class="steps active"> 
                  <span class="stepsnumbers active">1</span>
                </span> 
                <span class="stepstitle wide active">${step1Title}</span>
              </li>
              <li class="widgetwizard_stepscenterbar threestepswizard">
                <span class="steps"> 
                  <span class="stepsnumbers">2</span>
                </span> 
                <span class="stepstitle"><spring:message code="label.review.confirm" /></span>
              </li>
              <li class="widgetwizard_stepscenterbar last">
                <span class="steps last"> 
                  <span class="stepsnumbers last">3</span>
                </span> 
                <span class="stepstitle last"><spring:message code="label.finish" /></span>
              </li>
            </ul>
          </div>
        </div>
      </div>
      <div class="widgetwizard_contentarea">
        <div class="widgetwizard_boxes fullheight">
        <div style="height:90%;">
        <c:if test="${bundlesAvailable}">
            <div style="float:left;" class="widgetwizard_titleboxes">
                <input style="float:left;" name="purchaseOrExisting" type="radio" id="none_purchase_new" value="none_purchase_new" checked="true"/>
                <h2 style="margin:3px 0 0 5px;"><spring:message code="message.convert.subscription.no.unused.purchase.new" /></h2>
            </div>
            <div class="convert_subcription_table fixed_header_table_wrapper" id="bundles_table_div" style="max-height:300px;">
          <div class="convert_subcription_table fixed_header_table_inner">
          <table class="table table-condensed table-bordered">
            <thead class="convert_subcription_table_header table_header fixedHeader">
              <tr class="header" style="cursor:default;">
                <th><p style="width:15px;"></p></th>
                <th><p style="width:215px;"><spring:message code="label.bundle.name" /></p></th>
                <th><p style="width:130px;"><spring:message code="label.convert.subscription.header.billing.frequency" /></p></th>
                <th><p style="width:135px;"><spring:message code="label.convert.subscription.header.onetime.charge" />&nbsp;(<c:out value="${currency.currencyCode}"/>)</p></th>
                <th><p style="width:145px;"><spring:message code="label.subscribe.price.message" />&nbsp;(<c:out value="${currency.currencyCode}"/>)</p></th>
              </tr>
            </thead>
            <tbody>
              <c:forEach items="${activeBundles}" var="bundleRevision">
                <c:set var="monthlyNoRecurring" value="false" />
                <c:if test="${bundleRevision.productBundle.rateCard.chargeType.name == 'NONE'}">
                  <c:set var="monthlyNoRecurring" value="true" />
                </c:if>
                <tr class="hover_enabled" bundleName="${bundleRevision.productBundle.name}" bundleDesc="${bundleRevision.productBundle.description}" isMonthlyNoRecurring="${monthlyNoRecurring}" isExisting="false" productBundleId="${bundleRevision.pbid}">
                  <td><div style="width:15px;"><input name="conversion_choice" type="radio" id="${bundleRevision.pbid}" value="${bundleRevision.pbid}"></div></td>
                  <td>
                    <div class="ellipsis" style="width:215px;" title="${bundleRevision.productBundle.name}" id="subscriptionName">${bundleRevision.productBundle.name}</div>
                    <div style="width:155px;" class="ellipsis"><span class="js_entitlement_details_div"><a class="js_entitlement_details" href="javascript:void(0);" isExisting="false" detailsUuid="${bundleRevision.pbid}" data-toggle="popover" data-trigger="click" data-placement="bottom" data-container="body" data-html="true"><spring:message code="label.view.details" /></a></span>
                  </td>
                  
                  <c:choose>
                  <c:when test="${bundleRevision.productBundle.rateCard.chargeType.name == 'NONE'}">
                  <spring:message code="charge.type.MONTHLY" var="td_billingFrequency"/>
                  </c:when>
                  <c:otherwise>
                    <spring:message code="charge.type.${bundleRevision.productBundle.rateCard.chargeType.name}" var="td_billingFrequency"/>
                  </c:otherwise>
                  </c:choose>
                  
                  <td><div id="billingFrequency" style="width:130px;">${td_billingFrequency}</div></td>
                  <c:set var="onetTmeCharge" value="0" />
                  <c:set var="recurringCharge" value="0" />
                  <c:forEach items="${bundleRevision.rateCardCharges}" var="rateCardPrice">
                    <c:if test="${rateCardPrice.rateCardComponent.isRecurring}">
                      <c:set var="recurringCharge" value="${rateCardPrice.price}" />
                    </c:if>
                    <c:if test="${!rateCardPrice.rateCardComponent.isRecurring}">
                      <c:set var="onetTmeCharge" value="${rateCardPrice.price}" />
                    </c:if>
                  </c:forEach>
                  <td><div style="width:135px;" class="ellipsis" id="onetimeCharge"><c:out value="${currency.sign}"/><fmt:formatNumber pattern="${currencyFormat}" minFractionDigits="${minFractionDigits}" value="${onetTmeCharge}" /></div></td>
                  <td><div style="width:145px;" class="ellipsis" id="recurringCharge">
                  <c:choose>
                    <c:when test="${bundleRevision.productBundle.rateCard.chargeType.name == 'NONE'}">
                      <spring:message code="ui.label.na"/>
                    </c:when>
                    <c:otherwise><c:out value="${currency.sign}"/><fmt:formatNumber pattern="${currencyFormat}" minFractionDigits="${minFractionDigits}" value="${recurringCharge}" /></c:otherwise>
                  </c:choose>
                  </div></td>
                </tr>
              </c:forEach>
            </tbody>
          </table>
          </div>
          </div>
          </c:if>
          <c:if test="${subscriptionsAvailable}">
          <div class="widgetwizard_titleboxes">
          <input style="float:left;" name="purchaseOrExisting" type="radio" id="use_existing" value="use_existing" <c:if test="${!bundlesAvailable}">checked="true"</c:if> />
          <h2 style="margin:3px 0 0 5px;"><spring:message code="message.convert.subscription.choose.existing" /></h2>
          </div>
          <div class="convert_subcription_table fixed_header_table_wrapper" id="subscriptions_table_div"  <c:if test="${bundlesAvailable}">style="display:none;"</c:if>>
          <div class="convert_subcription_table fixed_header_table_inner">
          <table class="table table-condensed table-bordered">
            <thead class="convert_subcription_table_header table_header fixedHeader">
              <tr class="header" style="cursor:default;">
                <th><p style="width:15px;"></p></th>
                <th><p style="width:215px;"><spring:message code="label.convert.subscription.header.subscription.name" /></p></th>
                <th><p style="width:120px;"><spring:message code="label.convert.subscription.header.billing.frequency" /></p></th>
                <th><p style="width:135px;"><spring:message code="label.convert.subscription.header.onetime.charge" />&nbsp;(<c:out value="${currency.currencyCode}"/>)</p></th>
                <th><p style="width:145px;"><spring:message code="label.subscribe.price.message" />&nbsp;(<c:out value="${currency.currencyCode}"/>)</p></th>
              </tr>
            </thead>
            <tbody>
              <c:forEach items="${activeSubscriptions}" var="subscriptionRevisionMap">
                <c:set var="monthlyNoRecurring" value="false" />
                <c:if test="${subscriptionRevisionMap.value.productBundle.rateCard.chargeType.name == 'NONE'}">
                  <c:set var="monthlyNoRecurring" value="true" />
                </c:if>
                <tr class="hover_enabled" bundleName="${subscriptionRevisionMap.value.productBundle.name}" bundleDesc="${subscriptionRevisionMap.value.productBundle.description}" isMonthlyNoRecurring="${monthlyNoRecurring}" isExisting="true" subscriptionId="${subscriptionRevisionMap.key.id}" subscriptionUuid="${subscriptionRevisionMap.key.uuid}" productBundleId="${subscriptionRevisionMap.value.pbid}">
                  <td><div style="width:15px;"><input name="conversion_choice" type="radio" id="${subscriptionRevisionMap.key.uuid}" value="${subscriptionRevisionMap.key.uuid}"></div></td>
                  <td>
                    <div class="ellipsis" style="width:215px;" title="${subscriptionRevisionMap.key.handle.resourceName}" id="subscriptionName">${subscriptionRevisionMap.key.handle.resourceName}</div>
                    <div style="width:100px;" class="ellipsis"><span class="js_entitlement_details_div"><a class="js_entitlement_details" href="javascript:void(0);" isExisting="true" detailsUuid="${subscriptionRevisionMap.key.uuid}" data-toggle="dropdown" data-trigger="click" data-placement="bottom" data-container="body" data-html="true"><spring:message code="label.view.details" /></a></span>
                  </td>
                  <c:choose>
                  <c:when test="${subscriptionRevisionMap.value.productBundle.rateCard.chargeType.name == 'NONE'}">
                  <spring:message code="charge.type.MONTHLY" var="td_billingFrequency"/>
                  </c:when>
                  <c:otherwise>
                    <spring:message code="charge.type.${subscriptionRevisionMap.value.productBundle.rateCard.chargeType.name}" var="td_billingFrequency"/>
                  </c:otherwise>
                  </c:choose>
                  
                  <td><div style="width:120px;" id="billingFrequency">${td_billingFrequency}</div></td>
                  <c:set var="onetTmeCharge" value="0" />
                  <c:set var="recurringCharge" value="0" />
                  <c:forEach items="${subscriptionRevisionMap.value.rateCardCharges}" var="rateCardPrice">
                    <c:if test="${rateCardPrice.rateCardComponent.isRecurring}">
                      <c:set var="recurringCharge" value="${rateCardPrice.price}" />
                    </c:if>
                    <c:if test="${!rateCardPrice.rateCardComponent.isRecurring}">
                      <c:set var="onetTmeCharge" value="${rateCardPrice.price}" />
                    </c:if>
                  </c:forEach>
                  <td><div style="width:135px;" class="ellipsis" id="onetimeCharge"><c:out value="${currency.sign}"/><fmt:formatNumber pattern="${currencyFormat}" minFractionDigits="${minFractionDigits}" value="${onetTmeCharge}" /></div></td>
                  <td><div style="width:145px;" class="ellipsis" id="recurringCharge">
                  <c:choose>
                    <c:when test="${subscriptionRevisionMap.value.productBundle.rateCard.chargeType.name == 'NONE'}">
                      <spring:message code="ui.label.na"/>
                    </c:when>
                    <c:otherwise><c:out value="${currency.sign}"/><fmt:formatNumber pattern="${currencyFormat}" minFractionDigits="${minFractionDigits}" value="${recurringCharge}" /></c:otherwise>
                  </c:choose>
                  </div></td>
                </tr>
              </c:forEach>
              
            </tbody>
          </table>
          </div>
          </div>
          </c:if>
          
          </div>
          <div class="entitlementlightbox_bundledetails js_extra_usage_tab" >
              <span style="font-weight:bold;"><spring:message code="message.bundle.details.dialog.utility.rates" />&nbsp;(<a href="javascript:void(0);" class="utility_rate_link"><spring:message code="view.utility.rates" /></a>)</span>
          </div>
        </div>
        
      </div>
      <div class="widgetwizard_nextprevpanel" id="buttons">
        <input type="button" name="<spring:message code="label.next.step"/>" value="<spring:message code="label.next.step"/>" class="widgetwizard_nextprevpanel nextbutton">
        <a class="cancel" href="javascript:void(0);"><spring:message code="label.cancel" /></a>
      </div>
    </div>
    <!--step 1 ends here-->

  <!--step 2 starts here-->
  <div style="display: none;" class="js_stepcontent" id="step2">
    <input type="hidden" value="step3" name="nextstep" id="nextstep"> <input type="hidden" value="step1" name="prevstep" id="prevstep">
    <div class="widgetwizard_stepsbox">
      <div class="widgetwizard_steps_contentcontainer">
        <div class="widgetwizard_stepscenterbar">
          <ul>
            <li class="widgetwizard_stepscenterbar first">
              <span class="steps completedsteps"> 
                <span class="stepsnumbers">1</span>
              </span> 
              <span class="stepstitle wide">${step1Title}</span>
            </li>
            <li class="widgetwizard_stepscenterbar threestepswizard">
                <span class="steps active"> 
                  <span class="stepsnumbers active">2</span>
                </span> 
                <span class="stepstitle active"><spring:message code="label.review.confirm" /></span>
              </li>
              <li class="widgetwizard_stepscenterbar last">
                <span class="steps last"> 
                  <span class="stepsnumbers last">3</span>
                </span> 
                <span class="stepstitle last"><spring:message code="label.finish" /></span>
              </li>
          </ul>
        </div>
      </div>
    </div>
    <div class="widgetwizard_contentarea">
    <div class="widgetwizard_boxes fullheight">
      <div class="widgetwizard_titleboxes"><h2><spring:message code="label.review.confirm" /></h2>
      <span><span id="subscription_review_title" style="width:auto;"></span><span style="width:auto;">:</span></span>
      </div>
        <div id="convert_review_details" class="widgetwizard_reviewbox">
          <ul>
            <li>
              <span id="selection_label" class="label"></span>
              <span class="description">
                <span id="subscriptionName" class="ellipsis" style="width:auto;max-width:80%;display:block;float:left;"></span>
                <span class="js_entitlement_details_div" style="margin-left:10px;">
                  <a id="entitlements_review" class="js_entitlement_details" href="javascript:void(0);" data-toggle="popover" data-trigger="click" data-placement="bottom" data-container="body" data-html="true"><spring:message code="label.view.details" /></a>
                </span>
              </span>
            </li>
            <li><span class="label"><spring:message code="label.convert.subscription.header.billing.frequency" /></span> <span class="description" id="billingFrequency"></span></li>
            <li><span class="label"><spring:message code="label.convert.subscription.header.onetime.charge" /></span> 
              <span class="description">
                <span id="onetimeCharge"></span>&nbsp;
                <sup id="charge_sup_one"></sup>
              </span>
            </li>
            <li><span class="label"><spring:message code="label.subscribe.price.message" /></span> 
              <span class="description">
                <span id="recurringCharge"></span>&nbsp;
                <sup id="charge_sup_rec"></sup>
              </span>
            </li>
          </ul>
        </div>
      <div class="termsandconditions small widgetwizard_termsandconditions">
        <table>
          <thead>
            <tr>
              <th><spring:message code="important.notice" /></th>
            </tr>
          </thead>
          <tbody style="height: 50px;">
            <tr>
              <td>
                <p>
                  <spring:message code="resources.agree.tnc.message.new" />
                </p>
              </td>
            </tr>
          </tbody>
        </table>
        <div class="agree">
          <input type="checkbox" class="checkbox" id="tncAccept"> <div style="margin-top:3px;"><spring:message code="label.subscribe.notice.terms" htmlEscape="false" /></div>
        </div>
        <div class="main_addnew_formbox_errormsg" id="tncAcceptError" style="width: 800px; margin: 0px;"></div>
      </div>
    </div>
    </div>
    <div class="widgetwizard_nextprevpanel" id="buttons">
      <input type="button" name="<spring:message code="label.previous"/>" value="<spring:message code="label.previous"/>" class="widgetwizard_nextprevpanel prevbutton">
      <input type="button" name="<spring:message code="ui.accounts.all.pending.changes.convert"/>" value="<spring:message code="ui.accounts.all.pending.changes.convert"/>" class="widgetwizard_nextprevpanel nextbutton">
      <a class="cancel" href="javascript:void(0);"><spring:message code="label.cancel" /></a>
    </div>
  </div>
  <!--step 2 ends here-->
  
  <!--step 3 starts here-->
  <div class="dialog_formcontent js_stepcontent" style="display: none;" id="step3">
    <input type="hidden" value="step4" name="nextstep" id="nextstep">
    <input type="hidden" value="step2" name="prevstep" id="prevstep">
    <div class="widgetwizard_stepsbox">
      <div class="widgetwizard_steps_contentcontainer">
        <div class="widgetwizard_stepscenterbar">
          <ul>
            <li class="widgetwizard_stepscenterbar first threestepswizard">
              <span class="steps completedsteps"> 
                <span class="stepsnumbers">1</span>
              </span> 
              <span class="stepstitle wide">${step1Title}</span>
            </li>
            <li class="widgetwizard_stepscenterbar threestepswizard">
                <span class="steps completedsteps"> 
                  <span class="stepsnumbers">2</span>
                </span> 
                <span class="stepstitle"><spring:message code="label.review.confirm" /></span>
              </li>
              <li class="widgetwizard_stepscenterbar last">
                <span class="steps last active"> 
                  <span class="stepsnumbers last">3</span>
                </span> 
                <span class="stepstitle last active"><spring:message code="label.finish" /></span>
              </li>
          </ul>
        </div>
      </div>
    </div>
    <div class="widgetwizard_contentarea">
    <div class="widgetwizard_boxes fullheight">
      <div class="widgetwizard_successbox">
                      <div id="result_icon" class="widget_resulticon success"></div>
                      <p id="successmessage"></p>
                      <ul id="additional_list" class="list">
                        <li><spring:message code="message.convert.subscription.success.common" /></li>
                        <li id="billing_info"></li>
                        <li id="future_billing_info"></li>
                        <li><spring:message code="message.convert.subscription.success.common.excess.usage" /></li>
                      </ul>
                </div>
    </div>
    </div>
    <div class="widgetwizard_nextprevpanel" id="buttons">
      <input id="prev_last_screen" type="button" name="<spring:message code="label.previous"/>" value="<spring:message code="label.previous"/>" class="widgetwizard_nextprevpanel prevbutton">
      <input type="button" name="<spring:message code="ui.accounts.all.pending.changes.convert"/>" value="<spring:message code="label.close"/>" class="widgetwizard_nextprevpanel submitbutton">
    </div>
  </div>
  <!--step 3 ends here-->
  
  <div id="entitlement_divs_container" style="display: none;"></div>
  <div id="tncDialog" title='<spring:message code="js.errors.register.tncDialog.title"/>' style="display:none;padding:10px 10px 10px 20px;">
    <c:out value="${tnc}" escapeXml="false" />
  </div>
  <div id="entitlements_loading_spinner" style="display:none;">
  <div class="entitlements_table_container" id="entitlement_container">
  <div id="bundleName" class="rc_info_bubble_header" style="border-bottom: 1px dotted #CCCCCC;"></div>
  <div id="bundleDesc" class="rc_info_bubble_desc"></div>
  <div style="overflow: hidden;">
    <div style="max-height: 500px; overflow: auto; float: left; width: 100%;">
      <div class="entitlementlightbox_bundledetails entitlements_div fixed_header_table_wrapper" style="margin:0;width:100%;">
    <div class="utility_table entitlements_table fixed_header_table_inner entitlements_table_inner convert_subscription" id="entitlements_table_container">
          <table id="entitlements_table" class="fixed table table-condensed table-hover table-bordered entitlements_table" style="width:400px;margin-bottom:0px;">
            <col width="115">
            <col width="120">
            <col width="170">
            <thead class="table_header fixedHeader">
              <tr>
                <th><p><spring:message code="label.bundle.list.includes.title" /></p></th>
                <th><p><spring:message code="label.catalog.utility.card.table.header.product" /></p></th>
                <th><p style="width:160px;" class="ellipsis" title='<spring:message code="label.bundle.list.overages.title" />'><spring:message code="label.bundle.list.overages.title" /></p></th>
              </tr>
            </thead>
            <tbody id="totalentitlments" style="overflow-x: hidden;">
        <tr>
          <td colspan="3">
            <span style="margin-left:180px;" class="maindetails_footer_loadingicon" style="float:right;margin:0;"></span>                      
          </td>
        </tr>
      </tbody>
    </table>
    </div></div></div></div></div>
  </div>
</div>


<input type="hidden" id="subscriptionId"  value="<c:out value="${subscription.id}"/>"/>
<input type="hidden" id="subscriptionUuid"  value="<c:out value="${subscription.uuid}"/>"/>
<input type="hidden" id="resourceTypeName"  value="<c:out value="${subscription.resourceType.resourceTypeName}"/>"/>
<input type="hidden" id="serviceInstanceUuid"  value="<c:out value="${subscription.serviceInstance.uuid}"/>"/>

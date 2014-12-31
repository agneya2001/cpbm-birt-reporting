<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="js_messages.jsp"></jsp:include>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/date.format.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/commonBundles.js"></script>

<script type="text/javascript">
  var JS_LOADED;
  var productBundlesUrl = "<%=request.getContextPath() %>/portal/productBundles/";
  
  function onMouseOverOfAction(current){
    $(current).find('.j_entitlements_action_menu').show();
  }
  
  function onMouseOutOfAction(current){
    $(current).find('.j_entitlements_action_menu').hide();
  }

	
  var entitlementsPages = $('#entitlementsPages').val();
  var entitlementsCurrentPage = $('#entitlementsCurrentPage').val();

  if(entitlementsCurrentPage > 1) {
    $("#entitlements_click_previous").removeClass("nonactive").addClass("active");
  } else {
    $("#entitlements_click_previous").addClass("nonactive").removeClass("active");
  }

  if(entitlementsCurrentPage >=entitlementsPages) {
    $("#entitlements_click_next").addClass("nonactive").removeClass("active");
  } else {
    $("#entitlements_click_next").removeClass("nonactive").addClass("active");
  }

</script>

<style>

.unlimitedUsage {
}

</style>

<div class="widget_details_actionbox">
    <ul class="widget_detail_actionpanel" style="float:left;">
    </ul>
</div>

<div class="widget_browsergrid_wrapper wsubaction" id="entitlements_content" style="display:block; overflow-x: hidden; overflow-y: hidden; height: 92%">
  <div class="widget_details_inlinegrid" id="entitlements_details">
    <div class="widget_grid inline header">
          <div class="widget_details_inlineboxes_contentarea">
               <div class="widget_details_inlineboxes_titlearea" style="width: 100%">
                  <spring:message code="date.format" var="dateonly_format"/> 
                  <span class="widget_gridicon currentcalendar" style="margin: 0px 0px 3px 5px;"></span>

                  <span style="margin-left:5px;width: auto;color:#FFF;" class="title">
                    <spring:message code="label.bundle.list.ratecard.effective.date"/>
                    <c:choose>
                      <c:when test="${entitlementFilterDate != null}">
                          <fmt:formatDate value="${entitlementFilterDate}" pattern="${dateonly_format}"/>
                      </c:when>
                      <c:otherwise>
                          <spring:message code="ui.label.plan.date.not.yet.set"/>
                      </c:otherwise>
                    </c:choose> 
                  </span>
               </div>
           </div>
           <div id="entitlement_spinning_wheel" class="indicator loader" style="display:none">
             <div class="widget_blackoverlay widget_rightpanel">
               <div class="widget_loadingbox widget_rightpanel" style="margin-top: -100px">
                 <div class="widget_loaderbox">
                   <span class="bigloader"></span>
                 </div>
                 <div class="widget_loadertext">
                   <p id="in_process_text"><spring:message code="label.adding.processing"/> &hellip;</p>
                 </div>
               </div>
             </div>
           </div>
    </div>
    <div class="widget_grid inline subheader">
        <div class="widget_grid_cell" style="width:34%;">
            <span class="subheader"><spring:message code="label.bundle.edit.urc.product"/></span>
        </div>
        <div class="widget_grid_cell" style="width:34%;">
          <span class="subheader"><spring:message code="label.bundle.list.entitlement.included.units"/></span>
        </div> 
        <div class="widget_grid_cell" style="width:7%;">
          <span class="subheader"></span>
        </div>
    </div>

    <c:if test="${filterDateInFuture}">
      <div class="widget_details_actionbox addlistbox" id="create_entitlement_row" style="width: 100%; padding:0px;">
              <input type="hidden" value="<c:out value='${productBundle.code}'/>" id="bundleCode"/>
              <jsp:include page="createentitlement.jsp"></jsp:include>
      </div>
    </c:if>

    <c:forEach var="entitlementComponent" items="${entitlements}" varStatus="status">
       <c:choose>
          <c:when test="${status.index % 2 == 0}">
            <c:set var="rowClass" value="odd"/>
          </c:when>
          <c:otherwise>
              <c:set var="rowClass" value="even"/>
          </c:otherwise>
      </c:choose>

       <div class="<c:out value="widget_grid inline ${rowClass}"/>" id="entitlementrootdiv<c:out value='${entitlementComponent.id}' />" >

         <div class="widget_grid_cell" style="width:34%;">
            <span class="celltext"  style= "overflow:hidden; text-overflow:ellipsis; white-space:nowrap; width: 80%" title = "<c:out value="${entitlementComponent.product.name}"/>"> <c:out value="${entitlementComponent.product.name}"/></span>
         </div>

         <div class="widget_grid_cell" style="width:25%;">
            <span class="celltext"> 
                <div id="valuenotedit<c:out value="${entitlementComponent.id}"/>">
                  <c:choose>
                    <c:when test="${entitlementComponent.includedUnits == -1}">
                      <c:set var="includedunitsvalue" value="0"/>
                      <span style="font-weight: normal;"><spring:message code="label.bundle.list.entitlement.unlimited"/></span>
                      <span style="margin-left:5px;font-weight: normal;"> <spring:message code="${entitlementComponent.product.uom}"/></span>
                    </c:when>
                    <c:otherwise>
                      <c:set var="includedunitsvalue" value="${entitlementComponent.includedUnits}"/>
                      <c:out value="${entitlementComponent.includedUnits}"/>
                      <span style="margin-left:5px;font-weight: normal;"> <spring:message code="${entitlementComponent.product.uom}"/></span>
                    </c:otherwise>
                  </c:choose>
                </div>  

                <div id="valueedit<c:out value="${entitlementComponent.id}"/>" style="display: none;">
                  <input type="text" style="width:25%; margin-left:0; margin-top: -5px" size="10" id="value<c:out value="${entitlementComponent.id}"/>" 
                         class="entitlementtext text numberRequired" value="<c:out value="${includedunitsvalue}"/>"
                         name="value" />
                    <c:choose>
                    <c:when test="${entitlementComponent.allowedUnlimitedEntitlement eq true}">
                    <input type="checkbox" style="margin:0px 3px 3px 3px;float:left;" id="unlimitedUsage<c:out value="${entitlementComponent.id}"/>" tabindex= "23" name="unlimitedUsage"
                         class="unlimitedUsage"
                      <c:if test="${entitlementComponent.includedUnits == -1 }">  checked </c:if> >
                   <span style="margin-left:5px;float:left;width:50px;word-wrap:break-word;"><spring:message code="label.bundle.list.entitlement.unlimited.usage"/> <spring:message code="${entitlementComponent.product.uom}"/></span>
                    </c:when>
                    <c:otherwise>
                    <span style="margin-left:5px;"> <spring:message code="${entitlementComponent.product.uom}"/></span>
                    </c:otherwise>
                    </c:choose>
                   
                  <div class="main_addnew_formbox_errormsg" style="margin:0;width:120px;word-wrap:break-word;"  id="valueerror<c:out value="${entitlementComponent.id}"/>"></div>
                </div>

            </span>
         </div>

        <div style="width:16%;" class="widget_grid_cell"><span class="celltext"></span></div>
        <div style="width:17%;" class="widget_grid_cell"><span class="celltext"></span></div>

        <div class="widget_grid_cell" style="width:7%;">
           <c:if test="${filterDateInFuture}">
         	  <div class="widget_subactions grid action_menu_container j_action_menu_container" 
         	  		id="action_entitlements<c:out value="${entitlementComponent.id}" />" title='<spring:message code="label.actions"/>' 
         	  		style="float:left;margin:3px 0px 0px 8px; position: absolute;" onmouseover="onMouseOverOfAction(this)" onmouseout="onMouseOutOfAction(this)">
                <!--Actions popover starts here-->
                <div class="widget_actionpopover_grid j_entitlements_action_menu" id="entitlements_action_menu<c:out value="${entitlementComponent.id}" />" style="display:none;">
                    <div class="widget_actionpopover_top grid"></div>
                      <div class="widget_actionpopover_mid">
                        <ul class="widget_actionpoplist">
                            <li id="editpe<c:out value="${entitlementComponent.id}" />" style="display: block;" onclick="editEntitlement(this)"><spring:message code="label.edit"/>
                            </li>
                            <li id="deletepe<c:out value="${entitlementComponent.id}" />" style="display: block;" onclick="deleteEntitlement(this)"><spring:message code="label.remove"/>
                            </li>
                            <li class="saveentitlement" onclick="saveEntitlement(this)" 
                                id="savepe<c:out value="${entitlementComponent.id}" />"  style="display: none;"><spring:message code="ui.products.label.edit.save"/></li>
                            <li class="cancelentitlement" onclick="cancelEntitlement(this)" 
                                id="cancelpe<c:out value="${entitlementComponent.id}" />" style="display: none;"><spring:message code="ui.products.label.create.cancel"/></li>
                        </ul>
                      </div>
                      <div class="widget_actionpopover_bot"></div>
                  </div>
                  <!--Actions popover ends here-->
            </div>
           </c:if>
        </div>
      </div>
    </c:forEach>

    <div class="widget_browsergrid_panelnext">
      <input type="hidden" value="<c:out value="${perPage}"/>" id="entitlementsPerPage"/>
      <input type="hidden" value="<c:out value="${currentPage}"/>" id="entitlementsCurrentPage"/>
      <input type="hidden" value="<c:out value="${pages}"/>" id="entitlementsPages"/>
      <input type="hidden" value="<c:out value="${currentPageRecords}"/>" id="entitlementscurrentPageRecords"/>
      <div class="widget_navnextbox grid">
        <a class="widget_navnext_buttons prev nonactive" href="javascript:void(0);" id="entitlements_click_previous"><spring:message code="label.previous.short"/></a>
        <a class="widget_navnext_buttons next nonactive" href="javascript:void(0);" id="entitlements_click_next"><spring:message code="label.next"/></a>
      </div>
    </div>
  </div>
</div>

<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/WEB-INF/jsp/tiles/products/js_messages.jsp"></jsp:include>
<script type="text/javascript">
  var JS_LOADED;
  //Need products url for plan date common code.

  var tenantParam = "<c:out value="${tenant.param}"/>";
  var totalpages = "<c:out value="${totalpages}"/>";
  var currentPage = "<c:out value="${currentPage}"/>";
  var perPageValue = "<c:out value="${perPage}"/>";
  var bundlesLen = "<c:out value="${bundlesLen}"/>";

  currentPage = parseInt(currentPage);
  perPageValue = parseInt(perPageValue);
  bundlesLen = parseInt(bundlesLen);
  
  $("#entitlements_click_next").die("click").live("click", function (event) {
    var currentPage = Number($("#entitlementsCurrentPage").val());
    var bundleCode = $('#bundleCode').val();
    var entitlementsPages = $('#entitlementsPages').val();
    if(currentPage < entitlementsPages) {
      if($("#whichPlan").val() == "history"){
        if($("#rpb_history_dates option:selected").val().trim() != ""){
          viewEntitlements($("#whichPlan").val(), bundleCode, currentPage + 1, $("#rpb_history_dates option:selected").val().trim());
        }
      } else {
        viewEntitlements($("#whichPlan").val(), bundleCode, currentPage + 1);
      }
    }
  });

  $("#entitlements_click_previous").die("click").live("click", function (event) {
    var currentPage = Number($("#entitlementsCurrentPage").val());
    var filterDate = dateFormat($("#bundleEntitlementsHistoryId option:selected").val(),g_dictionary.filterDateFormat,false);
    var bundleCode = $('#bundleCode').val();
    var entitlementsPages = $('#entitlementsPages').val();
    if(currentPage > 1) {
      if($("#whichPlan").val() == "history"){
        if($("#rpb_history_dates option:selected").val().trim() != ""){
          viewEntitlements($("#whichPlan").val(), bundleCode, currentPage - 1, $("#rpb_history_dates option:selected").val().trim());
        }
      } else {
        viewEntitlements($("#whichPlan").val(), bundleCode, currentPage - 1);
      }
    }
  });

  if (currentPage > 1) {
    $("#click_previous").removeClass("nonactive");
    $("#click_previous").unbind("click").bind("click", previousClick);
  }
  
  if (bundlesLen < perPageValue) {
    $("#click_next").unbind("click");
    $("#click_next").addClass("nonactive");

  } else if (bundlesLen == perPageValue) {
    if (currentPage < totalpages) {
      $("#click_next").removeClass("nonactive");
      $("#click_next").unbind("click").bind("click", nextClick);
    } else {
      $("#click_next").unbind("click");
      $("#click_next").addClass("nonactive");
    }
  }

  $(".dropdownbutton").hover(function() {
    $("#plansdropdown").show();
  }, function() {
    $("#plansdropdown").hide();
  });

</script>

<script type="text/javascript" src="<%=request.getContextPath() %>/js/commonproducts.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/products.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/commonBundles.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/jquery.iframe-post-form.js"></script>

<jsp:include page="js_messages.jsp"></jsp:include>

<div class="widget_box products">
    <div class="widget_leftpanel">
        <div class="widget_titlebar">
          <h2 id="list_titlebar"><span id="list_all"><spring:message code="label.list.all"/> </span></h2>
          <c:if test="${whichPlan == 'planned'}">
             <div class="widget_titlebar dropdownbutton" ><span class="planicon"></span><span class="text"><spring:message code="label.plans" /></span><span class="downarrow"></span>
               <div class="widget_actionpopover plansdropdown" id="plansdropdown" style="display:none;">
                    <div class="widget_actionpopover_top OSdropdown"></div>
                    <div class="widget_actionpopover_mid OSdropdown">
                      <ul class="widget_actionpoplist OSdropdown">
                        <li><a onclick="editplannedCharges(this)" href="javascript:void(0);" id="edit_product_charges"  ><spring:message code="ui.label.edit.charges"/></a></li>
                      </ul>
                    </div>
                    <div class="widget_actionpopover_bot OSdropdown"></div>
                </div>
              </div>
              <a href="javascript:void(0);" id="add_bundle_link" onclick="addNewProductBundleGet(this)" class="widget_addbutton"><spring:message code="label.add.new"/></a>
          </c:if>
        </div>

        <div class="widget_searchpanel">
          <div id="search_panel">
            <div class="widget_searchpanel textbg">
                <input type="text" class="text" name="search" id="productBundleSearchPanel" onkeyup="searchProductBundleByName(this)" value="<c:out value="${namePattern}"/>"/>
                <a class="searchicon" href="#"></a>
            </div>
            <span class="movebutton" title="<spring:message code="ui.productbundles.sort.title"/>" id="sortorder" onclick="sortorder()"></span>
          </div>
        </div>

        <div class="widget_navigation" id="productbundlelist_div">
          <jsp:include page="/WEB-INF/jsp/tiles/productBundles/searchlist.jsp"></jsp:include>
        </div>
     </div>

    <div class="widget_rightpanel" id="viewproductBundleDiv"> 
        <div class="widget_rightpanel">
          <div class="widget_actionbar">
            <div class="widget_actionarea" id="top_actions" >
               <div class="widget_moreactions action_menu_container" title="<spring:message code='manage'/>">
                  <!--Actions popover starts here-->
                  <div class="widget_actionpopover" id="action_menu"  style="display:none;">
                    <div class="widget_actionpopover_top"></div>
                    <div class="widget_actionpopover_bot"></div>
                  </div>
                 <!--Actions popover ends here-->
              </div>
            </div>
          </div>

          <div id="top_message_panel" class="common_messagebox widget" style="display:none;"><span id="status_icon"></span><p id="msg"></p></div>

          <div class="widget_browser">
            <div class="widget_browsermaster">
                <div class="widget_browser_contentarea">
                    <div class="widget_browsergrid_wrapper master">
                                                
                          <div class="widget_grid master even first">
                              <div class="widget_grid_labels">
                                  <span><spring:message code="ui.products.label.create.name"/></span>
                              </div>
                              <div class="widget_grid_description">
                                  <span id="productname"></span>
                              </div>
                          </div>
                                                  
                          <div class="widget_grid master even">
                              <div class="widget_grid_labels">
                                  <span><spring:message code="ui.products.label.create.product.bundle.code"/></span>
                              </div>
                              <div class="widget_grid_description">
                                  <span id="productcode"></span>
                              </div>
                          </div>  
                          <div class="widget_grid master even">
                              <div class="widget_grid_labels">
                                  <span><spring:message code="label.bundle.type"/></span>
                              </div>
                              <div class="widget_grid_description">
                                  <span id="productcode"></span>
                              </div>
                          </div> 
                      </div>
                      <div class="widget_masterbigicons defaultbox">
                        <div class="thumbnail_defaultcontainer">
                          <div class="thumbnail_defaulticon bundle">
                          </div>
                        </div>
                        <div class="widget_masterbigicons_linkbox default"><a href="javascript:void(0);"  class=" default editBundleLogo"><spring:message code="ui.products.label.view.editimage"/></a>
                        </div>
                      </div>
               </div>
            </div>
    
            <div class="widget_browser_contentarea">
              <ul class="widgets_detailstab">
                  <li class="widgets_detailstab active" id="details_tab"><spring:message code="label.details"/></li>
                  <li class="widgets_detailstab nonactive" ><spring:message code="ui.label.product.entitlements"/></li>
                    <li class="widgets_detailstab nonactive" ><spring:message code="ui.label.bundle.pricing"/></li>
                   <li class="widgets_detailstab nonactive"  ><spring:message code="ui.label.channel.pricing"/></li>
              </ul>
    
              <div class="widget_browsergrid_wrapper details" id="details_content">
                <div class="widget_grid details even">
                    <div class="widget_grid_labels">
                        <span><spring:message code="ui.products.label.create.listorder"/></span>
                    </div>
                    <div class="widget_grid_description">
                        <span id="bundle_sortOder"></span>
                    </div>
                </div>
     
                <div class="widget_grid details odd">
                  <div class="widget_grid_labels">
                      <span><spring:message code="label.bundle.type"/></span>
                  </div>
                  <div class="widget_grid_description">
                      <span id="bundle_type">
                      </span>
                  </div>
                </div> 
    
                <div class="widget_grid details even">
                  <div class="widget_grid_labels">
                      <span><spring:message code="label.charge.type"/></span>
                  </div>
                  <div class="widget_grid_description">
                      <span id="bundle_charge_type"></span>
                  </div>
                </div>
    
                <div class="widget_grid details odd">
                    <div class="widget_grid_labels">
                        <span><spring:message code="label.bundle.create.publish"/></span>
                    </div>
                    <div class="widget_grid_description">
                        <span id="bundle_publish"></span>
                    </div>
                </div> 
    
                <div class="widget_grid details even">
                  <div class="widget_grid_labels">
                      <span><spring:message code="label.bundle.create.account.wide"/></span>
                  </div>
                  <div class="widget_grid_description">
                      <span id="bundle_accountwide"></span>
                  </div>
                </div>
    
                <div class="widget_grid details odd">
                    <div class="widget_grid_labels">
                        <span><spring:message code="ui.products.label.create.description"/></span>
                    </div>
                    <div class="widget_grid_description">
                        <span id="bundle_description"></span>
                    </div>
                </div> 
    
                <div class="widget_grid details even">
                    <div class="widget_grid_labels">
                        <span><spring:message code="label.bundle.create.eligible.trial"/></span>
                    </div>
                    <div class="widget_grid_description" >
                        <span id="bundle_trialEligibility"></span>
                    </div>
                </div>

              </div>

            </div>

          </div>

        </div>

    </div>

</div>

<div  id="dialog_add_bundle" title='<spring:message code="label.bundle.create.product.bundle"/>' style="display: none">
</div>

<div id="addEntitlement_div" style="display: none;">
</div>

<div  id="dialog_sortorder_productbundle" title="<spring:message code='ui.productbundles.sort.title'/>" style="display: none">
</div>

<div  id="dialog_plan_charges"  title='<spring:message code="label.plan.charges"/>' style="display: none;">
</div>

<div  id="dialog_view_planned_charges"  title='<spring:message code="label.view.planned.charges"/>' style="display: none;">
</div>

<div  id="dialog_edit_planned_charges"  title='<spring:message code="label.edit.planned.charges"/>' style="display: none;">
</div>

<div  id="dialog_set_plan_date"  title='<spring:message code="label.default.catalog.set.plan.date"/>' style="display: none;">
</div>

<div  id="dialog_edit_planned_date"  title='<spring:message code="label.default.catalog.edit.plan.date"/>' style="display: none;">
</div>

<div  id="common_dialog"  title='<spring:message code="lightbox.title.confirmation"/>' style="display: none;">
    <div class="dialog_formcontent wizard">
      <span class="helptext" id="helptext">
      </span>
    </div>
 </div>

<input type="hidden" id="current_page"  value="<c:out value="${current_page}"/>"/>
<input type="hidden" id="whichPlan"  value="<c:out value="${whichPlan}"/>"/>


<li  class="widget_navigationlist  j_viewbundle" onclick="viewProductBundle(this)" id="bundleviewtemplate" onmouseover="showInfoBubble(this)" onmouseout="hideInfoBubble(this)" style="display:none;">
  <span class='navicon' id="nav_icon">
  </span>
  <div class="widget_navtitlebox">
    <span class="title"></span>
    <span class="subtitle"></span>
  </div>
  <div class="widget_info_popover" id="info_bubble" style="display:none">

    <div class="popover_wrapper" >
 
      <div class="popover_shadow">
      </div>
 
      <div class="popover_contents">

        <div class="raw_contents">
          <div class="raw_content_row" id="info_bubble_displayname">
            <div class="raw_contents_title">
              <span><spring:message code="label.bundle.create.publish" />:</span>
            </div>
            <div class="raw_contents_value">
              <span id="bundle_publish_status"></span>
            </div>
          </div>

          <div class="raw_content_row" id="info_bubble_code">
            <div class="raw_contents_title">
             <span><spring:message code="ui.products.label.create.product.code" />:</span>
            </div>
            <div class="raw_contents_value">
              <span id="value"></span>
            </div>

          </div>

        </div>

      </div>

    </div>

  </div>
</li>

<div id="alert_dialog" style="display:none; height: auto; min-height: 65px; width: auto; margin: 25px 10px -20px;"
       title="<spring:message code="ui.label.message"/>">
</div>

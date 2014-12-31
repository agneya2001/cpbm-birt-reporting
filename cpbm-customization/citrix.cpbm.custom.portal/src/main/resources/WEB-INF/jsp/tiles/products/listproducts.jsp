<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<script type="text/javascript">

  var productBaseTypeMap = []; 
  var tenantParam = "<c:out value="${tenant.param}"/>";
  var totalpages = "<c:out value="${totalpages}"/>";
  var currentPage = "<c:out value="${currentPage}"/>";
  var perPageValue = "<c:out value="${perPage}"/>";
  var productsLen = "<c:out value="${size}"/>";

  currentPage = parseInt(currentPage);
  perPageValue = parseInt(perPageValue);
  productsLen = parseInt(productsLen);

  if (currentPage > 1) {
     $("#click_previous").removeClass("nonactive");
     $("#click_previous").unbind("click").bind("click", previousClick);
   }
   
   if (productsLen < perPageValue) {
     $("#click_next").unbind("click");
     $("#click_next").addClass("nonactive");

   } else if (productsLen == perPageValue) {
     
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
<script type="text/javascript" src="<%=request.getContextPath() %>/js/jquery.iframe-post-form.js"></script>


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
	                      <li><a onclick="editplannedCharges(this)" href="javascript:void(0);" id="edit_bundle_charges"  ><spring:message code="ui.label.edit.charges"/></a></li>
	                  </ul>
	                </div>
	              <div class="widget_actionpopover_bot OSdropdown"></div>
	            </div>
            </div>
            <a class="widget_addbutton" id="add_product_link" onclick="addNewProductGet();" href="javascript:void(0);"><spring:message code="label.add.new"/></a>
          </c:if>
        </div>

        <div class="widget_searchpanel">
          <div id="search_panel">
            <div class="widget_searchpanel textbg">
              <input type="text" class="text" name="search" id="productSearchPanel" onkeyup="searchProductByName(this)" value="<c:out value="${namePattern}"/>"/>
              <a class="searchicon" href="#"></a>
            </div>
            <span class="movebutton" title="<spring:message code="ui.products.sort.title"/>" id="sortorder" onclick="sortorder()"></span>
          </div>
        </div>
        <div class="widget_searchpanel">
         <div class="widget_searchcontentarea" id="advanced_search">
            <span class="label"><spring:message code="label.category"/>:</span>     
                 <select class="filterby select expandable-select" id="filter_dropdown">                
                 <option value="all"><spring:message code="ui.product.category.all"/></option>
                 <c:forEach items="${categories}" var="choice" varStatus="status">
                      <option value='<c:out value="${choice.name}"/>' >
                        <c:out value="${choice.name}" escapeXml="false"/>
                     </option>
                 </c:forEach>
            </select>
          </div>
        </div>

        <div class="widget_navigation" id="productlist_div"> 
            <jsp:include page="/WEB-INF/jsp/tiles/products/searchlist.jsp"></jsp:include>
        
          </div>
      </div>                        
 
     <div class="widget_rightpanel" id="viewproductDiv">    
     </div> 
     </div>    
       <div  id="dialog_add_product" title='<spring:message code="ui.products.label.create.add.product"/>' style="display: none"></div>
       <div  id="dialog_edit_product" title='<spring:message code="ui.products.label.edit.edit"/>' style="display: none"></div>       
       <div  id="dialog_sortorder_product" title="<spring:message code='ui.products.sort.title'/>" style="display: none"></div>
       <div  id="dialog_plan_charges"  title='<spring:message code="label.plan.charges"/>' style="display: none;"></div>
       <div  id="dialog_view_planned_charges"  title='<spring:message code="label.view.planned.charges"/>' style="display: none;"></div>
       <div  id="dialog_edit_planned_charges"  title='<spring:message code="label.edit.planned.charges"/>' style="display: none;"></div>
       <div  id="dialog_set_plan_date"  title='<spring:message code="label.default.catalog.set.plan.date"/>' style="display: none;"></div>
       <div  id="dialog_edit_planned_date"  title='<spring:message code="label.default.catalog.edit.plan.date"/>' style="display: none;"></div>
       
       <div  id="common_dialog"  title='<spring:message code="lightbox.title.confirmation"/>' style="display: none;">
        <div class="dialog_formcontent wizard">
          <span class="helptext" id="helptext">          
          </span>
        </div>
       </div>
     
<input type="hidden" id="current_page"  value="<c:out value="${current_page}"/>"/>
<input type="hidden" id="whichPlan"  value="<c:out value="${whichPlan}"/>"/>


 <li  class="widget_navigationlist  j_viewproduct" onclick="viewProduct(this)" id="productviewtemplate" onmouseover="showInfoBubble(this)" onmouseout="hideInfoBubble(this)" style="display:none;">
  <!-- <span class='navicon' id="nav_icon"></span>  -->
  <div class="widget_navtitlebox">
    <span class="title"></span>
    <span class="subtitle"></span>
  </div>
  <div class="widget_info_popover" id="info_bubble" style="display:none">
    <div class="popover_wrapper" >
    <div class="popover_shadow"></div>
    <div class="popover_contents">
    <div class="raw_contents">
    <div class="raw_content_row" id="info_bubble_displayname">
      <div class="raw_contents_title">
        <span><spring:message code="label.category"/>:</span>
      </div>
      <div class="raw_contents_value">
        <span id="value"></span>
      </div>
    </div>
    
     <div class="raw_content_row" id="info_bubble_code">
      <div class="raw_contents_title">
        <span><spring:message code="ui.products.label.create.product.code"/>:</span>
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
       title="<spring:message code="ui.label.message"/>"></div>

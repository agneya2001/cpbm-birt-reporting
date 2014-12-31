<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
 <%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<script language="javascript">
var tenantPageUrl = "<%=request.getContextPath() %>/portal/tenants/list";
var tenantUrl = "<%=request.getContextPath() %>/portal/tenants/";
var statesdictionary = {    
		NEW: '<spring:message javaScriptEscape="true" code="tenant.state.new"/>',
		ACTIVE: '<spring:message javaScriptEscape="true" code="tenant.state.active"/>',
		LOCKED: '<spring:message javaScriptEscape="true" code="tenant.state.locked"/>',
		SUSPENDED: '<spring:message javaScriptEscape="true" code="tenant.state.suspended"/>',
		TERMINATED: '<spring:message javaScriptEscape="true" code="tenant.state.terminated"/>'
}

var dictionary = { 
  lightboxremovetenant: '<spring:message javaScriptEscape="true" code="js.confirm.removeTenant"/>',
  lightboxbuttoncancel: '<spring:message javaScriptEscape="true" code="label.cancel"/>',
  lightboxbuttonconfirm: '<spring:message javaScriptEscape="true" code="label.confirm"/>',
  removeTenant: '<spring:message javaScriptEscape="true" code="label.remove.tenant"/>',
  removingTenant: '<spring:message javaScriptEscape="true" code="message.removing.tenant"/>',
  label_adding: '<spring:message javaScriptEscape="true" code="label.adding.processing"/>',
  label_next: '<spring:message javaScriptEscape="true" code="label.next"/>'
};
var totalpages = "<c:out value="${totalpages}"/>";
var currentPage = "<c:out value="${currentPage}"/>";
var selectedAccountType = "<c:out value="${selectedAccountType}"/>";
var filterBy = "<c:out value="${filterBy}"/>";
var perPageValue = "<c:out value="${perPage}"/>";
var showAddAccountWizard = "<c:out value="${showAddAccountWizard}"/>";
var accountId = "<c:out value="${accountId}" />";

$(document).ready(function(){
  if(filterBy == ""){
    activateThirdMenuItem("l3_account_All_tab");
  }else if(filterBy == "All"){
    activateThirdMenuItem("l3_account_All_tab");
  }else if(filterBy == "0"){
    activateThirdMenuItem("l3_account_0_tab");
  }else if(filterBy == "1"){
    activateThirdMenuItem("l3_account_1_tab");
  }else if(filterBy == "2"){
    activateThirdMenuItem("l3_account_2_tab");
  }else if(filterBy == "3"){
    activateThirdMenuItem("l3_account_3_tab");
  }else if(filterBy == "4"){
    activateThirdMenuItem("l3_account_4_tab");
  }
  $("#advSrch").click(function() {
  	$("#advancedSearch").show();
  });
  
  if(accountId && accountId != ""){
    document.getElementById('normalSearch').value = accountId;
    $("input[id^='normalSearch']").attr('class','textbg text');
  }
  $("input[id^='normalSearch']").click(function() {
    var searchText="<spring:message javaScriptEscape="true" code="ui.label.search.by.accountId"/>";
    if(document.getElementById('normalSearch').value == searchText){
      document.getElementById('normalSearch').value = '';
      $("input[id^='normalSearch']").attr('class','textbg text');
    }
  });
  $("input[id^='normalSearch']").bind('keypress', function(e) {
    if(e.keyCode==13){
        var searchText="<spring:message javaScriptEscape="true" code="ui.label.search.by.accountId"/>";
        if(document.getElementById('normalSearch').value == searchText){
          $("input[id^='searchAccountId']").attr('value', "");
        }
        else{
          $("input[id^='searchAccountId']").attr('value', document.getElementById('normalSearch').value);
          $("input[id^='fieldName']").attr('value', '');
          $("input[id^='name']").attr('value', '');
        }
        $("#searchForm").submit();
      }
    });
  
  $("#normalSearchButton").click(function() {
    $("#normalSearchButton").unbind("click");
    var searchText="<spring:message javaScriptEscape="true" code="ui.label.search.by.accountId"/>";
    if(document.getElementById('normalSearch').value == searchText){
      $("input[id^='searchAccountId']").attr('value', "");
    }
    else{
      $("input[id^='searchAccountId']").attr('value', document.getElementById('normalSearch').value);
      $("input[id^='fieldName']").attr('value', '');
      $("input[id^='name']").attr('value', '');
    }
    
    $("#searchForm").submit();
    $("#normalSearchButton").bind("click");
  });
  $("#advancesearchButton").click(function() {
    $("#filterDropdownDiv").hide();
    $("#advanceSearchDropdownDiv").toggle();
    $("input[id^='name']").focus();
  });
  $("#filterButton").click(function() {
    $("#advanceSearchDropdownDiv").hide();
    $("#filterDropdownDiv").show();
  });
  function showFilterPopup(event){
    $("#filterDropdownDiv").show();
    event.stopPropagation();
  }
  function hideFilterPopup(event){
    $("#filterDropdownDiv").hide();
    event.stopPropagation();
  }
   
  $("#filterDropdownDiv").bind("mouseover", function (event) {
    showFilterPopup(event);
  });

  $("#filterDropdownDiv").bind("mouseout", function (event) {
    hideFilterPopup(event); 
  });
  
  currentPage = parseInt(currentPage);
  perPageValue = parseInt(perPageValue);
  accountsListLen = parseInt(accountsListLen);
  
  if (currentPage > 1) {
    $("#click_previous").removeClass("nonactive");
    $("#click_previous").unbind("click").bind("click", previousClick);
  }
  if (accountsListLen < perPageValue) {
    $("#click_next").unbind("click");
    $("#click_next").addClass("nonactive");

  } else if (accountsListLen == perPageValue) {
    
    if (currentPage < totalpages) {
      
      $("#click_next").removeClass("nonactive");
      $("#click_next").unbind("click").bind("click", nextClick);
    } else {
      $("#click_next").unbind("click");
      $("#click_next").addClass("nonactive");
    }
  }
function nextClick(event) {
    
    $("#click_next").unbind("click", nextClick);
    $("#click_next").addClass("nonactive");
    
    currentPage = currentPage + 1;
    
    $("#click_previous").unbind("click").bind("click", previousClick);
    $("#click_previous").removeClass("nonactive");
    
    window.location=tenantPageUrl+"?accountType="+selectedAccountType+"&filterBy="+filterBy+"&currentPage=" + currentPage;
  }

  function previousClick(event) {
    $("#click_previous").unbind("click", previousClick);
    $("#click_previous").addClass("nonactive");

    currentPage = currentPage - 1;
    
    $("#click_next").removeClass("nonactive");
    $("#click_next").unbind("click").bind("click", nextClick);
    
    window.location=tenantPageUrl+"?accountType="+selectedAccountType+"&filterBy="+filterBy+"&currentPage=" + currentPage;
  }
});

var i18nAccountTypeDictionary = {
  'System': '<spring:message javaScriptEscape="true" code="registration.accounttype.system"/>',
  'Retail': '<spring:message javaScriptEscape="true" code="registration.accounttype.retail"/>',
  'Corporate': '<spring:message javaScriptEscape="true" code="registration.accounttype.corporate"/>',
  'Trial': '<spring:message javaScriptEscape="true" code="registration.accounttype.trial"/>',
  'Default': '<spring:message javaScriptEscape="true" code="registration.accounttype.default"/>'
};
</script>  


<div class="widget_box">
    <div class="widget_leftpanel">
        <div class="widget_titlebar">
          <c:choose>
          <c:when test="${searchresult == 'true'}">
            <h2>
            <c:set var="complete_url" value='?accountType=${selectedAccountType}&filterBy=${filterBy}' />
            
            <a class=""  href='/portal/portal/tenants/list<c:out value="${complete_url}"/>'>
            <span class="title_listall_arrow"><spring:message code="label.list.all"/></span>
            </a>
            </h2>
          </c:when>
          <c:otherwise>
            <h2>
            <span class=""><spring:message code="label.list.all"/></span>
            </h2>
          </c:otherwise>
          </c:choose>
          <c:if test="${channelSize ne 0}">
	          <sec:authorize access="hasRole('ROLE_ACCOUNT_CRUD')">
	           <a id="add_account_link" href="javascript:void(0);" onclick="addNewTenantGet();" class="widget_addbutton"><spring:message code="ui.accounts.all.newaccount"/></a>
	          </sec:authorize>
          </c:if>
        </div>
        <div class="widget_searchpanel">
           <div id="search_panel">
				<div class="widget_searchpanel textbg" style="width:180px;">
					<input type="text" class="textbg text default" style="width:146px;" name="search" id="normalSearch" value="<spring:message code="ui.label.search.by.accountId"/>" />
                    <a class="searchicon" id="normalSearchButton" href="#"></a>
                </div>
                <a class="advancesearch_button searchpanel" id="advancesearchButton" href="#"></a>
                	<div class="widget_actionpopover advancesearch_dropdown" id="advanceSearchDropdownDiv" style="display:none;">
                      
                        <jsp:include page="search.jsp"></jsp:include>
                    </div>
                
                
                   
                 <!--    
                <a class="filter_button" id="filterButton" href="#"></a>
                
                	<div class="widget_actionpopover filtersdropdown" id="filterDropdownDiv" style="display:none;">
                      <div class="widget_actionpopover_top OSdropdown"></div>
                      <div class="widget_actionpopover_mid OSdropdown">
                      	<ul class="widget_actionpoplist OSdropdown">
                          <c:forEach items="${filtersMap}" var="filter">
                            <c:choose>
                              <c:when test="${filterBy==filter.key}">
                                <c:set var="selected" value="filter_selected" />
                              </c:when>
                              <c:otherwise>
                                <c:set var="selected" value="" />
                              </c:otherwise>
                            </c:choose>
                            <li class='<c:out value="${selected}"/>' style="cursor:pointer;" id='<c:out value="${filter.key}"/>' onclick="filterAccounts(this);"><c:out value="${filter.value}" /></li>
                          </c:forEach>
                        </ul>
                        
                      </div>
                      <div class="widget_actionpopover_bot OSdropdown"></div> 
                </div>  
                   -->
                
            </div>
            
          
        </div>
        <div class="widget_navigation">
          <ul class="widget_navigationlist" id="grid_row_container">
            <c:choose>
              <c:when test="${searchresult == 'true' && (empty tenants || tenants == null)}">
                  <c:set var="accountsListLen" value="0"/>
                  <li class="widget_navigationlist nonlist" id="non_list">
                  <c:if test="${selectedAccountType == 1}">
                    <span class="navicon accounts system"></span>
                  </c:if>
                  <c:if test="${selectedAccountType == 3}">
                    <span class="navicon accounts retail"></span>
                  </c:if>
                  <c:if test="${selectedAccountType == 4}">
                    <span class="navicon accounts corporate"></span>
                  </c:if>
                  <c:if test="${selectedAccountType == 5}">
                    <span class="navicon accounts trial"></span>
                  </c:if>
                  <c:if test="${selectedAccountType != 1 && selectedAccountType != 3 && selectedAccountType != 4 && selectedAccountType != 5}">
                    <span class="navicon accounts default"></span>
                  </c:if>
                    <div class="widget_navtitlebox">
                      <span class="newlist">
                        <spring:message code="ui.label.search.emptylist"/>
                      </span>
                    </div> 
                  </li>
              </c:when>
              <c:when test="${searchresult != 'true' && (empty tenants || tenants == null)}">
                  <c:set var="accountsListLen" value="0"/>
                  <li class="widget_navigationlist nonlist" id="non_list">
                    <c:if test="${selectedAccountType == 1}">
                      <span class="navicon accounts system"></span>
                    </c:if>
                    <c:if test="${selectedAccountType == 3}">
                      <span class="navicon accounts retail"></span>
                    </c:if>
                    <c:if test="${selectedAccountType == 4}">
                      <span class="navicon accounts corporate"></span>
                    </c:if>
                    <c:if test="${selectedAccountType == 5}">
                      <span class="navicon accounts trial"></span>
                    </c:if>
                    <c:if test="${selectedAccountType != 1 && selectedAccountType != 3 && selectedAccountType != 4 && selectedAccountType != 5}">
                      <span class="navicon accounts default"></span>
                    </c:if>
                    <div class="widget_navtitlebox">
                      <span class="newlist">
                        <spring:message var="accountsMsg" code="ui.label.emptylist.accounts" ></spring:message>
                        <spring:message var="accountMsg" code="ui.label.emptylist.account" ></spring:message>
                        <spring:message code="ui.label.emptylist.notavailable" arguments="${accountsMsg}" htmlEscape="false"/>
                        
                      </span>
                    </div> 
                  </li>
              </c:when>
              <c:otherwise>
              <c:set var="selectFirst" value="1"/>
              <c:if test="${empty selectedAccount}">
              <c:set var="selectFirst" value="0"/>
              </c:if>
                <c:forEach var="tenant" items="${tenants}" varStatus="status">
                    <c:set var="accountsListLen" value="${accountsListLen+1}"/>
                    <c:choose>
                      <c:when test="${status.index == 0 && selectFirst == 0}">
                        <c:set var="firstTenant" value="${tenant}"/>
                        <c:set var="selected" value="selected"/>
                        <c:set var="active" value="active"/>
                      </c:when>
                       <c:when test="${tenant.uuid == selectedAccount && selectFirst == 1}">
                        <c:set var="firstTenant" value="${tenant}"/>
                        <c:set var="selected" value="selected"/>
                        <c:set var="active" value="active"/>
                      </c:when>
                      <c:otherwise>
                        <c:set var="selected" value=""/>
                        <c:set var="active" value=""/>
                      </c:otherwise>
                    </c:choose> 

                    <li class="<c:out value="widget_navigationlist ${selected} ${active} "/>"
                        id="<c:out value="row${tenant.param}"/>" 
                        tenantid="<c:out value="${tenant.id}"/>" 
                        onclick="viewTenant(this);" onmouseover="onAccountMouseover(this);" onmouseout="onAccountMouseout(this);">
                      <c:if test="${tenant.accountType.name=='SYSTEM'}">
                        <span id="nav_icon" class="navicon accounts system"></span>
                      </c:if>
                      <c:if test="${tenant.accountType.name=='RETAIL'}">
                        <span id="nav_icon" class="navicon accounts retail"></span>
                      </c:if> <c:if test="${tenant.accountType.name=='Corporate'}">
                        <span id="nav_icon" class="navicon accounts corporate"></span>
                      </c:if>
                      <c:if test="${tenant.accountType.name=='Trial'}">
                        <span id="nav_icon" class="navicon accounts trial"></span>
                      </c:if>
                      <c:if test="${tenant.accountType.name != 'SYSTEM' && tenant.accountType.name!='RETAIL' && tenant.accountType.name!='Corporate' && tenant.accountType.name!='Trial'}">
                        <span id="nav_icon" class="navicon accounts default"></span>
                      </c:if>

                      <div class="widget_navtitlebox db_gridbox_rows">
                        <span class="title">
                          <c:out value="${tenant.name}"/>
                        </span>
                        <span class="subtitle">
                          <spring:message code="ui.accounts.all.header.state"/>: <spring:message code="${tenant.state.code}"/>
                        </span>
                      </div>

                      <!--Info popover starts here-->
                      <div class="widget_info_popover" id="info_bubble" style="display:none">
                        <div class="popover_wrapper" >
                          <div class="popover_shadow"></div>
                          <div class="popover_contents">
                            <div class="raw_contents">

                              <div class="raw_content_row">
                                <div class="raw_contents_title">
                                  <span><spring:message code="ui.accounts.all.header.name"/>:</span>
                                </div>
                                <div class="raw_contents_value">
                                  <span class="account_name">
                                    <c:out value="${tenant.name}"/>
                                  </span>
                                </div>
                              </div>

                              <div class="raw_content_row">
                                <div class="raw_contents_title">
                                  <span><spring:message code="ui.accounts.all.header.accounttype"/>:</span>
                                </div>
                                <div class="raw_contents_value">
                                  <span>
                                    <spring:message code="registration.accounttype.${tenant.accountType.nameLower}"/>
                                  </span>
                                </div>
                              </div>

                              <div class="raw_content_row">
                                <div class="raw_contents_title">
                                  <span><spring:message code="ui.accounts.all.header.state"/>:</span>
                                </div>
                                <div class="raw_contents_value">
                                  <span>
                                    <spring:message code="${tenant.state.code}"/>
                                  </span>
                                </div>
                              </div>

                              <div class="raw_content_row">
                                <div class="raw_contents_title">
                                  <span><spring:message code="ui.accounts.all.header.accountid"/>:</span>
                                </div>
                                <div class="raw_contents_value">
                                  <span>
                                    <c:out value="${tenant.accountId}"/>
                                  </span>
                                </div>
                              </div>

                              <div class="raw_content_row">
                                <div class="raw_contents_title">
                                  <span><spring:message code="ui.accounts.all.header.masteruser"/>:</span>
                                </div>
                                <div class="raw_contents_value">
                                  <span>
                                    <c:out value="${tenant.owner.username}"/>
                                  </span>
                                </div>
                              </div>

                            </div>

                          </div>

                        </div>
                      </div>
                      <!--Info popover ends here-->

                    </li>

                </c:forEach>
              </c:otherwise> 
            </c:choose> 
          </ul>
        </div>
        <script type="text/javascript">
          var accountsListLen = "<c:out value="${accountsListLen}"/>";
        </script>
        <div class="widget_panelnext">
          <div class="widget_navnextbox">
              <a class="widget_navnext_buttons prev nonactive" href="javascript:void(0);" id="click_previous"><spring:message code="label.previous.short"/></a>
                <a class="widget_navnext_buttons next" href="javascript:void(0);" id="click_next"><spring:message code="label.next"/></a>
            </div>
        </div>
    </div>
    
    <div id="<c:out value="count${size}"/>" class="countDiv"></div>
    <div class="widget_rightpanel" id="viewTenantDiv">
        <c:if test="${firstTenant != null}">
          <script>
          var li_id = "<c:out value="row${firstTenant.param}"/>";
          viewFirstTenant(li_id);
          </script>
        </c:if> 
		<div id="spinning_wheel_rhs">
			<div class="widget_blackoverlay widget_rightpanel" style="height: 100%"></div>
			<div class="widget_loadingbox fullpage">
				<div class="widget_loaderbox">
					<span class="bigloader"></span>
				</div>
				<div class="widget_loadertext">
					<p id="in_process_text">
						<spring:message code="label.loading" />
					</p>
				</div>
			</div>
		</div>        
    </div>
    
  
</div>

<div id="newTenantDiv" title='<spring:message code="label.tenants.heading"/>' style="display: none">
  <div class="dialog_formcontent wizard">
  </div>
</div>
<div id="editTenantDiv"> </div>
<div id="issueCreditDiv"> </div>  
<div id="editTenantsLimitDiv" style="overflow:hidden"></div>

<script>
if(showAddAccountWizard == "true"){
  addNewTenantGet();
}
</script>


<li class="widget_navigationlist"
    style="display:none"
    id="newTenantRowTemplate"
    tenantid="" 
    onclick="viewTenant(this);" onmouseover="onAccountMouseover(this);" onmouseout="onAccountMouseout(this);">

  <span id="nav_icon" class="navicon accounts"></span>
  <div class="widget_navtitlebox db_gridbox_rows">
    <span class="title" id="new_tenant_name">
    </span>
    <span class="subtitle">
      <spring:message code="ui.accounts.all.header.state"/>: <spring:message code="tenant.state.new"/>
    </span>
  </div>

  <!--Info popover starts here-->
  <div class="widget_info_popover" id="info_bubble" style="display:none">
    <div class="popover_wrapper" >
      <div class="popover_shadow"></div>
      <div class="popover_contents">
        <div class="raw_contents">

          <div class="raw_content_row">
            <div class="raw_contents_title">
              <span><spring:message code="ui.accounts.all.header.name"/>:</span>
            </div>
            <div class="raw_contents_value">
              <span id="new_tenant_name">
              </span>
            </div>
          </div>

          <div class="raw_content_row">
            <div class="raw_contents_title">
              <span><spring:message code="ui.accounts.all.header.accounttype"/>:</span>
            </div>
            <div class="raw_contents_value">
              <span id="new_tenant_account_type">
              </span>
            </div>
          </div>

          <div class="raw_content_row">
            <div class="raw_contents_title">
              <span><spring:message code="ui.accounts.all.header.state"/>:</span>
            </div>
            <div class="raw_contents_value">
              <span>
                <spring:message code="tenant.state.new"/>
              </span>
            </div>
          </div>

          <div class="raw_content_row">
            <div class="raw_contents_title">
              <span><spring:message code="ui.accounts.all.header.accountid"/>:</span>
            </div>
            <div class="raw_contents_value">
              <span id="new_tenant_account_id">
              </span>
            </div>
          </div>

          <div class="raw_content_row">
            <div class="raw_contents_title">
              <span><spring:message code="ui.accounts.all.header.masteruser"/>:</span>
            </div>
            <div class="raw_contents_value">
              <span id="new_tenant_owner_username">
              </span>
            </div>
          </div>

        </div>

      </div>

    </div>
  </div>
  <!--Info popover ends here-->
</li>
<input type="hidden" id="defaultChannel" name="defaultChannel" value="${defaultChannel}" >
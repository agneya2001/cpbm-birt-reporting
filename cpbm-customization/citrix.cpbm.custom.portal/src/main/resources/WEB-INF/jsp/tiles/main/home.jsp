<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page import="com.vmops.model.Tenant" %>
<sec:authorize access="hasRole('ROLE_TICKET_MANAGEMENT')">
	<c:set var="isTicketAdmin" value="true"/>
</sec:authorize>

<script type="text/javascript">
 var usersUrl = "<%=request.getContextPath() %>/portal/users/";
<%String addNewUserUrll = (String)request.getContextPath()+"/portal/users/new/step1?tenant="+(String)(((Tenant)request.getAttribute("tenant")).getParam());%>
var addNewUserUrl="<%=addNewUserUrll %>";
var loadGravtars = true;
</script>
<script language="javascript">
	var fetchTicketCount = false;
	 <c:if test="${(isTicketAdmin || currentTenant == tenant) && ticketCapabilities == 'CRUD'}">
	 fetchTicketCount = true;
	</c:if>	   
</script>
 <c:if test="${!empty top_nav_cs_instances}">
     <script type="text/javascript">
         var getServiceInstanceHealth = true;
     </script>
 </c:if>

<script type="text/javascript" src="<%=request.getContextPath() %>/js/users.js"></script>
<jsp:include page="/WEB-INF/jsp/tiles/shared/country_states.jsp"></jsp:include>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/jquery.stateselect.js"></script>

<script type="text/javascript" src="<%=request.getContextPath() %>/js/home_user.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/home_common.js"></script>

<script type="text/javascript" src="<%=request.getContextPath() %>/js/jquery.easing.1.3.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/fusion/charts/FusionCharts.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/fusion/charts/FusionCharts.jqueryplugin.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/chart.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/tasks.js"></script>


<script language="javascript">
	var i18n = {
		      user: {
	      		delfail: '<spring:message javaScriptEscape="true" code="js.user.del.fail"/>',
	      		deactfail: '<spring:message javaScriptEscape="true" code="js.user.deact.fail"/>',
	      		actfail: '<spring:message javaScriptEscape="true" code="js.user.act.fail"/>',
	      		channel: '<spring:message javaScriptEscape="true" code="js.user.channel"/>',
	      		title: '<spring:message javaScriptEscape="true" code="js.user.title"/>',
	    		del : '<spring:message javaScriptEscape="true" code="js.user.del.confirm"/>',
	    		generateAPIKey : '<spring:message javaScriptEscape="true" code="js.user.generate.api.key"/>',
          deact: '<spring:message javaScriptEscape="true" code="js.user.deact.confirm"/>',
	    		act: '<spring:message javaScriptEscape="true" code="js.user.act.confirm"/>',
	    		delproject : '<spring:message javaScriptEscape="true" code="js.user.del.confirmproject"/>',	      		
			    max : '<spring:message javaScriptEscape="true" code="js.user.max"/>',
			    cloudstorage: '<spring:message javaScriptEscape="true" code="js.user.cloudstorage.subscribe"/>',
			    profile: '<spring:message javaScriptEscape="true" code="js.user.profile.edit"/>',
			    firstname: '<spring:message javaScriptEscape="true" code="js.user.firstname"/>',
			    lastname: '<spring:message javaScriptEscape="true" code="js.user.lastname"/>',
			    flnameValidationError:'<spring:message javaScriptEscape="true" code="js.errors.flname"/>',
			    validateUsername: '<spring:message javaScriptEscape="true" code="js.errors.tenants.user.validateUsername"/>',
			    email: '<spring:message javaScriptEscape="true" code="js.user.email"/>',
			    emailmatch: '<spring:message javaScriptEscape="true" code="js.user.email.match"/>',
			    confirmemail: '<spring:message javaScriptEscape="true" code="js.user.confirmemail"/>',
			    emailformat: '<spring:message javaScriptEscape="true" code="js.user.email.format"/>',
			    username: '<spring:message javaScriptEscape="true" code="js.user.username"/>',
			    usernameexists: '<spring:message javaScriptEscape="true" code="js.user.username.exists"/>',
			    password: '<spring:message javaScriptEscape="true" code="js.user.password"/>',
			    passwordconfirm: '<spring:message javaScriptEscape="true" code="js.user.password.confirm"/>',
			    passwordmatch: '<spring:message javaScriptEscape="true" code="js.user.password.match"/>',
			    profilerequired: '<spring:message javaScriptEscape="true" code="js.user.profile.required"/>',
			    passwordequsername: '<spring:message javaScriptEscape="true" code="js.user.passwordequsername"/>'
		      },
		      errors:{
            countryCode : '<spring:message javaScriptEscape="true" code="js.errors.register.countryCode"/>',
            phonePin : '<spring:message javaScriptEscape="true" code="js.user.phonePin"/>',
            phoneDetails : '<spring:message javaScriptEscape="true" code="js.errors.register.phoneDetails"/>',
            callRequested: '<spring:message javaScriptEscape="true" code="js.errors.register.callRequested"/>',
            callFailed: '<spring:message javaScriptEscape="true" code="js.errors.register.callFailed"/>'
        }
		};

var dictionary = {  
  userNoBilling: '<spring:message javaScriptEscape="true" code="message.user.no.billing"/>'
};

</script> 
<script type="text/javascript">
  var healthUrl = "<%=request.getContextPath() %>/portal/health";
</script>



<div class="sliding_statswrapper">
    <jsp:include page="/WEB-INF/jsp/tiles/main/serviceCategoryList.jsp"/>        
    <div id="home_items_view" class="slider_gridwrapper user">
    </div>
</div>

<div class="maincontent_horizontalpanels">
    <div class="db_gridverticalpanel">
       <div class="db_statsbox default">
  		  <div class="db_statsbox title user">
  		    <h2><spring:message code="ui.home.page.title.service.health"/></h2>
  		  </div>
  		  <div class="db_statsbox contentarea" >
           <c:choose>
             <c:when test="${not empty cloudTypeServiceInstances}">
               <div id="serviceHealthChart" style="min-height:30px;"></div>
             </c:when>
             <c:otherwise>
               <div class="db_flipcounter_liststyle" style="display: block;">
                 <div class="dashboard_noitem_messagebox">
                   <span class="non_list"><spring:message code="message.no.data.to.show"/></span> 
                 </div>
               </div>
              </c:otherwise>
           </c:choose>
	      </div>
  		</div> 
		<c:set var="normalUser" value="true"/>
		<sec:authorize access="hasAnyRole('ROLE_ACCOUNT_ADMIN','ROLE_ACCOUNT_BILLING_ADMIN','ROLE_FINANCE_CRUD')">
		<div class="db_statsbox default">
	        <div class="db_statsbox title user">
	          <h2><spring:message code="ui.home.page.title.spend.budget" htmlEscape="false"/></h2>
	          <span id="budget_value" style="float:left;margin:8px 0px 0px 5px;font-size:14px;"></span>
	        </div>
	        <div class="db_statsbox contentarea">
	          
	            <div id="spendBudgetChart">
	              <tiles:insertAttribute name="spendvsbudget"/>
	            </div>
	          <div class="db_statsbox_footerlinksbox">
	     <c:set var="showUsageBilling" value="N"></c:set>
	            <sec:authorize access="hasAnyRole('ROLE_ACCOUNT_ADMIN','ROLE_ACCOUNT_BILLING_ADMIN','ROLE_FINANCE_CRUD')">
	                <!--Info popover starts here-->
	                           <c:set var="showUsageBilling" value="Y"></c:set>
	                <p id="showCreditBalance">
	                    <a href="javascript:void(0);"><spring:message code="label.usage.billing.net.balance.colon"/></a>
	                    <c:out value="${tenant.currency.sign}" /><strong><span><fmt:formatNumber pattern="${currencyFormat}" maxFractionDigits="${currencyFractionalDigitsLimit}" minFractionDigits="${minFractionDigits}" value="${currentBalance}" /></span>
	                    </strong> |
	                </p>                
	                <!--Info popover ends here-->        
	            </sec:authorize>
	             <sec:authorize access="hasAnyRole('ROLE_USER_CRUD','ROLE_ACCOUNT_USER_CRUD','ROLE_ACCOUNT_BILLING_ADMIN')">
	             <p>
	            <spring:url value="/portal/tenants/alerts" var="all_alerts_path" htmlEscape="false">
	              <spring:param name="tenant"><c:out value="${tenant.param}"/></spring:param>
	            </spring:url>
	             <spring:url value="/portal/tenants/alerts" var="budget_path" htmlEscape="false">
	              <spring:param name="tenant"><c:out value="${tenant.param}"/></spring:param>
	              <spring:param name="budget">true</spring:param>
	            </spring:url>
	            <a href="<c:out value="${all_alerts_path}"/>"><spring:message code="ui.home.page.title.manage.spend.alerts"/></a>&nbsp;
                <c:if test="${tenant.state != 'TERMINATED'}">
	              |</p><p><a href="<c:out value="${budget_path}"/>"><spring:message code="ui.home.page.title.set.budget"/></a></p>
                </c:if>
	            </sec:authorize>
	          </div>
	        </div>
       
      </div>
      </sec:authorize>
      <sec:authorize access="hasAnyRole('ROLE_ACCOUNT_ADMIN','ROLE_ACCOUNT_BILLING_ADMIN','ROLE_FINANCE_CRUD')">
           <div class="db_statsbox default">
             <div class="db_statsbox title user">
                 <h2><spring:message code="label.dashboard.understand.bill"/></h2>
             </div>
             <div class="db_statsbox contentarea">
                 <div class="db_billing_calendarblock">
                     <div class="calendaricon">
                          <span class="title"><spring:message code="label.dashboard.next.invoice" /></span>
                         <span class="date">
                         <spring:message code="dayonly.format" var="dayonly_format"/>
                         <fmt:formatDate value="${nextInvoiceDate}" pattern="${dayonly_format}"/></span>
                     </div>
                    <span class="infotext">
                      <fmt:formatDate dateStyle="medium" value="${nextInvoiceDate}" pattern="MMM"/>,
                      <fmt:formatDate value="${nextInvoiceDate}" pattern="yyyy"/>
                    </span>
                 </div>
                 <div class="db_billinglist">
                     <ul>
                         <li><span class="listtext"><a href="/portal/portal/billing/usageBilling?tenant=${effectiveTenant.param}"><spring:message code="label.dashboard.link.check.balance.usage"/></a></span></li>
                         
                         <c:if test="${effectiveTenant.accountType.paymentModes eq 2}">
                             <sec:authorize access="hasRole('ROLE_ACCOUNT_BILLING_ADMIN')">
                               <li><span class="listtext"><a href="/portal/portal/billing/paymenthistory?tenant=${effectiveTenant.param}"><spring:message code="label.dashboard.link.make.payment"/></a></span></li>
                             </sec:authorize>
                         </c:if>
                         
                          <c:if test="${effectiveTenant.accountType.name != 'SYSTEM' && effectiveTenant.accountType.name != 'Trial'}">
                              <sec:authorize access="hasAnyRole('ROLE_ACCOUNT_ADMIN','ROLE_ACCOUNT_BILLING_ADMIN')">
                                  <li><span class="listtext"><a href="/portal/portal/tenants/editcurrent?currenttab=billingAddress"><spring:message code="label.dashboard.link.update.billing.address"/></a></span></li>
                              </sec:authorize>
                          </c:if>
                           <li><span class="listtext"><a href="javascript:void(0);" style="float: right;" class="utility_rates_link" ><spring:message code="page.level2.utilityrates"/></a></span></li>
                     </ul>
                 </div>
             </div>
         </div>
      </sec:authorize>
      
      
    </div>
       <div class="db_gridverticalpanel right">
	       
          <div class="db_statsbox launch user">
	          
	          <div class="db_statsbox launch textarea">
	            <span class="title"><spring:message code="ui.home.page.title.gettingstarted"/></span>
	            <span class="descrp"><spring:message code="ui.home.page.title.startvmgeneraltext.message"/></span>
	          </div>
            <c:choose>
	            <c:when test="${userHasCloudServiceAccount}">
	          <div class="db_statsbox launch buttonarea">
			                <a class="launch_button btn btn-info"  href="javascript:void(0);" id="browseCatalogButton" browseCatalog="${userHasCloudServiceAccount}">
                    <span class="cloud"></span>
                    <spring:message code="label.catalog.browse"/>
                    </a>
                    <sec:authorize access="hasRole('ROLE_ACCOUNT_ADMIN')">
		                 <a style="float:left;margin-left: 10px; margin-top: 2px" href="<%=request.getContextPath() %>/portal/connector/csinstances?tenant=<c:out value="${effectiveTenant.param}"/>">
                        <spring:message code="ui.home.page.title.manage.services"/>
                     </a>
                     </sec:authorize>
	          </div>
	            </c:when>
	            <c:otherwise>
            <div class="db_statsbox launch buttonarea">
                      <a class="launch_button nonactive"  id="browseCatalogButton" browseCatalog="${userHasCloudServiceAccount}">
                    <span class="cloud"></span>
                        <spring:message code="label.catalog.browse"/>
                    </a>
            </div>
	            </c:otherwise>
            </c:choose>
	        </div>
	    <c:set var="userHome" value="user" scope="request"/>
	    
        <sec:authorize access="hasRole('ROLE_ACCOUNT_ADMIN')">
            <c:if test="${!userHasCloudServiceAccount && tenant.state == 'ACTIVE'}">
	          <div class ="common_messagebox error"> 
	            <p>                              
                 <spring:message code="service.no.instance.warning" htmlEscape="false" arguments="${tenant.param}"> </spring:message>
              </p>   	                    
	          </div>
            </c:if>	        
        </sec:authorize>
        <tiles:insertDefinition name="home.task.widget" />
        <sec:authorize access="hasAnyRole('ROLE_ACCOUNT_USER_CRUD','ROLE_USER_CRUD')">
        <div class="db_statsbox default">
          <div class="db_statsbox title user">
            <h2><spring:message code="ui.home.page.title.users"/></h2>
          </div>
          <div class="db_statsbox contentarea">
            <div class="db_userslist" id="users_gravtars">
                <div id="users_gravtars_spinner_div" class="spinnerDiv"> </div>
            </div>
            <div class="db_statsbox_footerlinksbox">
              <spring:url value="/portal/users/listusersforaccount" var="manage_users_path" htmlEscape="false">
                <spring:param name="tenant"><c:out value="${tenant.param}"/></spring:param>
              </spring:url>
              <p><a href="<c:out value="${manage_users_path}"/>"><spring:message code="ui.home.page.title.manage.users"/></a></p>
              <c:if test="${isUsersMaxReached != 'Y' && tenant.state eq 'ACTIVE'}">
	              <p>|&nbsp;&nbsp;<a onclick="return addNewUserButton()"  href="javascript:void(0);"><spring:message code="ui.home.page.title.adduser"/></a>
	              <div  id="dialog_add_user" title='<spring:message code="label.newUserStep1.addUser"/>' style="display: none">
	                <div id="dialog_formcontent"  class="dialog_formcontent wizard"> </div>
	              </div>
	               <input id="tenantId" type="hidden" name="tenant" value="<c:out value="${tenant.param}"/>"/>
	              </p>
              </c:if>
            </div>
          </div>
        </div>
      </sec:authorize> 
      
      <div class="db_statsbox default">
        <div class="db_statsbox title user">
          <h2><spring:message code="ui.home.page.title.notifications"/></h2>
        </div>
        <div class="db_statsbox contentarea">
            <div class="db_box_timelinewrapper">
                  <span class="timestamp">
                    <spring:message code="label.today"/>
                  </span>                          
              </div>
              <c:choose>
                    <c:when test="${empty alerts_for_today}">
                       <div id="non_item_list" class="db_flipcounter_liststyle">
                          <div class="dashboard_noitem_messagebox">
                              <span class="non_list"><spring:message code="message.no.data.to.show"/></span> 
                          </div>
                        </div>
                    </c:when>
                    <c:otherwise>
              
              <div class="db_liststyle">
                    <spring:message code="date.format" var="date_format"/>
                  <ul>
                  
                    <c:forEach items="${alerts_for_today}" var="alertObj" varStatus="status" end="4">
                    <c:choose>
                      <c:when test="${not empty alertObj.severity}">
                        <c:set var="level" value="${alertObj.severity}"/>
                      </c:when>
                      <c:otherwise>
                        <c:set var="level" value="INFORMATION"/>
                      </c:otherwise>
                    </c:choose>
                    <c:choose>
                      <c:when test="${not empty alertObj.category}">
                        <c:set var="category" value="${alertObj.category}"/>
                      </c:when>
                      <c:otherwise>
                        <c:set var="category" value="SYSTEM"/>
                      </c:otherwise>
                    </c:choose>
                        <li>
                            <div class="db_notificationwrapper">
                            <span class="title ${level}"><spring:message code="ui.home.notification.${category}.${level}"/></span>
                            
                            <c:choose>
                              <c:when test="${alertObj.useMessageKey eq true}">
                               <span class="descp ellipsis" title="<spring:message code="${alertObj.message}" arguments="${alertObj.messageArguments}"/>">
                                <spring:message code="${alertObj.message}" arguments="${alertObj.messageArguments}"/>: <fmt:formatDate value="${alertObj.generatedAt}" pattern="${date_format}" timeZone="${currentUser.timeZone}"/></span>
                              </c:when>
                            <c:otherwise>
                                <span class="descp ellipsis" title="<c:out value="${alertObj.message}"/>"><c:out value="${alertObj.message}"/>: <fmt:formatDate value="${alertObj.generatedAt}" pattern="${date_format}" timeZone="${currentUser.timeZone}"/></span>
                            </c:otherwise>
                            </c:choose>
                          </div>
                            <span class="icon notification ${level}" ref="today" onclick="go_to_notifications(this);"></span>
                        </li>
                      </c:forEach>
                        
                    </ul>
                    
                </div>
                </c:otherwise>
                </c:choose>
              <div class="db_box_timelinewrapper">
                  <span class="timestamp">
                    <spring:message code="label.yesterday"/>
                  </span>                          
              </div>
              <c:choose>
                    <c:when test="${empty alerts_for_yesterday}">
                       <div id="non_item_list" class="db_flipcounter_liststyle">
                          <div class="dashboard_noitem_messagebox">
                              <span class="non_list"><spring:message code="message.no.data.to.show"/></span> 
                          </div>
                        </div>
                    </c:when>
                    <c:otherwise>
		          
		          <div class="db_liststyle">
                  <ul>
                    <c:forEach items="${alerts_for_yesterday}" var="alertObj" varStatus="status" end="4">
                    <c:choose>
                      <c:when test="${not empty alertObj.severity}">
                        <c:set var="level" value="${alertObj.severity}"/>
                      </c:when>
                      <c:otherwise>
                        <c:set var="level" value="INFORMATION"/>
                      </c:otherwise>
                    </c:choose>
                    <c:choose>
                      <c:when test="${not empty alertObj.category}">
                        <c:set var="category" value="${alertObj.category}"/>
                      </c:when>
                      <c:otherwise>
                        <c:set var="category" value="SYSTEM"/>
                      </c:otherwise>
                    </c:choose>
                      <li>
                          <div class="db_notificationwrapper">
                            <span class="title ${level}"><spring:message code="ui.home.notification.${category}.${level}"/></span>
                            
                            <c:choose>
                              <c:when test="${alertObj.useMessageKey eq true}">
                                <span class="descp ellipsis" title="<spring:message code="${alertObj.message}" arguments="${alertObj.messageArguments}"/>">
                                  <spring:message code="${alertObj.message}" arguments="${alertObj.messageArguments}"/>: <fmt:formatDate value="${alertObj.generatedAt}" pattern="${date_format}" timeZone="${currentUser.timeZone}"/></span>
                              </c:when>
                              <c:otherwise>
                                  <span class="descp ellipsis" title="<c:out value="${alertObj.message}"/>"><c:out value="${alertObj.message}"/>: <fmt:formatDate value="${alertObj.generatedAt}" pattern="${date_format}" timeZone="${currentUser.timeZone}"/></span>
                              </c:otherwise>
                            </c:choose>
                          </div>
                          <span class="icon notification ${level}" ref="yesterday" onclick="go_to_notifications(this);"></span>
                        </li>
                      </c:forEach>
                        
                    </ul>
                </div>
                </c:otherwise>
                </c:choose>
		          <div class="db_statsbox_footerlinksbox">
		          <spring:url value="/portal/tenants/notifications" var="all_notifications_path" htmlEscape="false">
		            <spring:param name="tenant"><c:out value="${tenant.param}"/></spring:param>
		          </spring:url> 
		          <spring:url value="/portal/users/${user.param}/myprofile" var="manage_notifications_path" htmlEscape="false">
		          </spring:url> 
                    <!-- TODO too coupled... use read permission on object in UI -->
                    <c:set var="userCRUD" value="F"></c:set>
                    <sec:authorize access="hasRole('ROLE_USER_CRUD')">
                      <c:set var="userCRUD" value="T"></c:set>
                    </sec:authorize>

		            <p><a href="<c:out value="${all_notifications_path}"/>"><spring:message code="ui.home.page.title.viewallnotifications"/></a><c:if test="${currentTenant.id != 1 || effectiveTenant.id == 1 || userCRUD == 'T'}"> |</c:if></p>
		            <c:if test="${currentTenant.id != 1 || effectiveTenant.id == 1 || userCRUD == 'T'}">
		              <p><a href="<c:out value="${manage_notifications_path}?&activeTab=notPref"/>"><spring:message code="ui.home.page.title.manage.notifications.preferences"/></a></p>
		            </c:if>
		          </div>
                    <input type="hidden" id="isOwner" name="isOwner" value="<c:out value="${isOwner}"/>"/>  
                    <input type="hidden" id="accountState" name="accountState" value="<c:out value="${effectiveTenant.state}"/>"/>
                    <input type="hidden" name="tenantParam" id="tenantParam" value="<c:out value="${tenant.param}"/>">    
                </div>
            </div> 
            
            <c:if test="${(isTicketAdmin || currentTenant == tenant) && ticketCapabilities == 'CRUD' && isTicketServiceEnabled}">
              <c:set var="showTicketWidget" value="no" />
              <sec:authorize access="hasAnyRole('ROLE_TICKET_MANAGEMENT')">
                <c:if test="${ticketServiceInstance}">
                  <c:set var="showTicketWidget" value="yes" />
                </c:if>
              </sec:authorize>
              <sec:authorize access="hasAnyRole('ROLE_USER_TICKET_MANAGEMENT', 'ROLE_SPL_USER_TICKET_MANAGEMENT', 'ROLE_TENANT_TICKET_MANAGEMENT')">
                <c:if test="${ticketServiceInstance}">
                  <c:set var="showTicketWidget" value="yes" />
                </c:if>
              </sec:authorize>

              <c:if test="${showTicketWidget == 'yes'}">
                <div class="db_statsbox default">
                  <div class="db_statsbox title">
                    <h2><spring:message code="ui.home.page.title.tickets"/></h2>
                  </div>
                  <div id="top_message_panel" class="alert alert-success" style="float:left;width:390px;word-wrap:break-word;display:none;" ><p id="msg"></p></div>
                  <div class="db_statsbox contentarea">
                      <div id="ticketsCountChart">
                        <div id="spinnerDiv" class="spinnerDiv" style="margin: 70px 0 0 200px;"> </div>
                      </div>
                      <div class="db_statsbox_footerlinksbox">
  	                  <spring:url value="/portal/support/tickets" var="view_tickets_path" htmlEscape="false">
  	                    <spring:param name="tenant"><c:out value="${tenant.param}"/></spring:param>
  	                  </spring:url> 
                        <p><a href="<c:out value="${view_tickets_path}"/>"><spring:message code="ui.home.page.title.view.all.tickets"/></a></p>
                        <c:if test="${!effectiveTenant.owner.profile.operationsProfile}">
    	                    <sec:authorize access="hasRole('ROLE_USER_TICKET_MANAGEMENT')">
    	                     <p>|</p> <p><a href="<c:out value="${view_tickets_path}&showNewTicket=1"/>"><spring:message code="ui.home.page.title.submit.ticket"/></a></p>
    	                    </sec:authorize>
                        </c:if>
                      </div>
                  </div>
                </div>
              </c:if>
            </c:if>
    
       </div>
    </div>

    <script type="text/javascript">
    var isDelinquent = false;
    var redirectToBilling = false;
    var tenantParam2 = "<c:out value="${tenant.param}"/>";
  </script>
  <c:if test="${showDelinquent}">
    <sec:authorize access="hasAnyRole('ROLE_ACCOUNT_BILLING_ADMIN', 'ROLE_FINANCE_CRUD')">
      <script type="text/javascript">
        var isDelinquent = true;
        var redirectToBilling = true;
      </script>
    </sec:authorize>
    <sec:authorize access="!hasAnyRole('ROLE_ACCOUNT_BILLING_ADMIN', 'ROLE_FINANCE_CRUD')">
      <script type="text/javascript">
        var isDelinquent = true;
      </script>
    </sec:authorize>
  </c:if>

<sec:authorize access="hasAnyRole('ROLE_ACCOUNT_ADMIN','ROLE_ACCOUNT_BILLING_ADMIN','ROLE_FINANCE_CRUD')">
<script type="text/javascript">
  var showNetBalanceDialog = true;
</script>
<div  id="dialog_net_balance" title="<spring:message code='label.dialog.title.charges.summary'/>" style="display: none; overflow: hidden;">
   <div class="more_details_lightbox_container" >
        <div class="more_details_lightboxformbox">
           <ul > 
               <li class="first">
                       <span class="more_details_dialog_row_title"><spring:message code="label.usage.billing.balance.forward"/></span>
                       <span class="more_details_dialog_row_value">
                        <c:out value="${tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}" maxFractionDigits="${minFractionDigits}" minFractionDigits="${minFractionDigits}" value="${balanceForward}" />
                       </span>
                  </li> 
                  <li>
                       <span class="more_details_dialog_row_title"><spring:message code="label.usage.billing.payments.credits"/></span>
                       <span class="more_details_dialog_row_value">
                        <c:out value="${tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}" maxFractionDigits="${minFractionDigits}" minFractionDigits="${minFractionDigits}" value="${paymentsCreditsTotal}" />
                       </span>
                  </li> 
                  <li>
                       <span class="more_details_dialog_row_title"><spring:message code="label.usage.billing.new.charges"/></span>
                       <span class="more_details_dialog_row_value">
                        <c:out value="${tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}" maxFractionDigits="${minFractionDigits}" minFractionDigits="${minFractionDigits}" value="${totalAmount}" />
                       </span>
                  </li> 
                  <li>
                       <span class="more_details_dialog_row_title"><spring:message code="label.usage.billing.net.balance"/></span>
                       <span class="more_details_dialog_row_value">
                        <c:out value="${tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}" maxFractionDigits="${minFractionDigits}" minFractionDigits="${minFractionDigits}" value="${currentBalance}" />
                       </span>
                  </li> 
                   <li>
                       <span class="more_details_dialog_row_title"><spring:message code="label.usage.billing.renewal.charges" /></span>
                       <span class="more_details_dialog_row_value">
                         <c:out value="${tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}" maxFractionDigits="${minFractionDigits}" minFractionDigits="${minFractionDigits}" value="${renewalCharge}" />
                       </span>
                  </li> 
             </ul>
      </div>
    </div>
</div>
</sec:authorize>

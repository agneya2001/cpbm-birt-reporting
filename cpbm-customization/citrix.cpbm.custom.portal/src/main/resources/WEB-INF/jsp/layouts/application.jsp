<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>

<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <title>
      SmartLogic - GGC    
    </title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/>
    <meta name = "format-detection" content = "telephone=no">
    <script type="text/javascript" src="<%=request.getContextPath()%>/resources/all.js"></script>
    <c:if test="${empty currentLocale && pageContext.request.locale.language ne 'en'}">
      <script type="text/javascript" src='<%=request.getContextPath()%>/js/i18n/jquery-validate/messages_<c:out value="${pageContext.request.locale.language}"/>.js'></script>
      <c:if test="${not empty pageContext.request.locale.country}">
        <script type="text/javascript" src='<%=request.getContextPath()%>/js/i18n/jquery-validate/messages_<c:out value="${pageContext.request.locale.language}"/>-<c:out value="${pageContext.request.locale.country}"/>.js'></script>
      </c:if>
    </c:if>
    <c:if test="${not empty currentLocale && currentLocale.language ne 'en'}">
      <script type="text/javascript" src='<%=request.getContextPath()%>/js/i18n/jquery-validate/messages_<c:out value="${currentLocale.language}"/>.js'></script>
      <c:if test="${not empty currentLocale.country}">
        <script type="text/javascript" src='<%=request.getContextPath()%>/js/i18n/jquery-validate/messages_<c:out value="${currentLocale.language}"/>-<c:out value="${currentLocale.country}"/>.js'></script>
      </c:if>
    </c:if>
    <script type="text/javascript" src="<%=request.getContextPath()%>/csrf_js_servlet"></script>
    <link rel="stylesheet" href="<%=request.getContextPath() %>/css/bootstrap.min.css" type="text/css">
    <script type="text/javascript" src="<%=request.getContextPath() %>/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath() %>/js/bootstrap.dropdown.menu.js"></script>
    <link rel="stylesheet" href="<%=request.getContextPath() %>/css/font-awesome/css/font-awesome.min.css" type="text/css">
    <link rel="stylesheet" href="<%=request.getContextPath() %>/css/font-awesome/css/MyFontsWebfontsKit.css" type="text/css" >
    <link rel="stylesheet" type="text/css" media="all" href="<%=request.getContextPath()%>/resources/all.css"/>
    <link rel="stylesheet" type="text/css" media="all" href="<%=request.getContextPath()%>/css/main.css"/>
    
    <c:if test="${empty currentLocale && pageContext.request.locale.language ne 'en'}">
      <link rel="stylesheet" type="text/css" media="all" href="<%=request.getContextPath()%>/css/main_<c:out value="${pageContext.request.locale.language}"/>.css"/>
      <c:if test="${not empty pageContext.request.locale.country}">
        <link rel="stylesheet" type="text/css" media="all" href="<%=request.getContextPath()%>/css/main_<c:out value="${pageContext.request.locale.language}"/>-<c:out value="${pageContext.request.locale.country}"/>.css"/>
      </c:if>
    </c:if>
    <c:if test="${not empty currentLocale && currentLocale.language ne 'en'}">
      <link rel="stylesheet" type="text/css" media="all" href="<%=request.getContextPath()%>/css/main_<c:out value="${currentLocale.language}"/>.css"/>
      <c:if test="${not empty currentLocale.country}">
        <link rel="stylesheet" type="text/css" media="all" href="<%=request.getContextPath()%>/css/main_<c:out value="${currentLocale.language}"/>-<c:out value="${currentLocale.country}"/>.css"/>
      </c:if>
    </c:if>
    <link rel="stylesheet" type="text/css" media="all" href="<%=request.getContextPath()%>/custom/css/custom.css"/>
    <c:if test="${empty currentLocale && pageContext.request.locale.language ne 'en'}">
      <link rel="stylesheet" type="text/css" media="all" href="<%=request.getContextPath()%>/custom/css/custom_<c:out value="${pageContext.request.locale.language}"/>.css"/>
      <c:if test="${not empty pageContext.request.locale.country}">
        <link rel="stylesheet" type="text/css" media="all" href="<%=request.getContextPath()%>/custom/css/custom_<c:out value="${pageContext.request.locale.language}"/>-<c:out value="${pageContext.request.locale.country}"/>.css"/>
      </c:if>
    </c:if>
    <c:if test="${not empty currentLocale && currentLocale.language ne 'en'}">
      <link rel="stylesheet" type="text/css" media="all" href="<%=request.getContextPath()%>/custom/css/custom_<c:out value="${currentLocale.language}"/>.css"/>
      <c:if test="${not empty currentLocale.country}">
        <link rel="stylesheet" type="text/css" media="all" href="<%=request.getContextPath()%>/custom/css/custom_<c:out value="${currentLocale.language}"/>-<c:out value="${currentLocale.country}"/>.css"/>
      </c:if>
    </c:if>
    <link rel="stylesheet" type="text/css" media="all" href="<%=request.getContextPath()%>/<spring:theme code="css"/>"/>
    <link rel="stylesheet" media="all and (min-device-width: 481px) and (max-device-width: 1024px) and (orientationortrait)" href="<%=request.getContextPath()%>/css/ipad_custom.css" />
    <link rel="stylesheet" media="all and (min-device-width: 481px) and (max-device-width: 1024px) and (orientation:landscape)" href="<%=request.getContextPath()%>/css/ipad_custom.css" /> 
 
    <link rel="stylesheet" type="text/css" media="only screen and (device-width: 768px) and (-webkit-min-device-pixel-ratio: 1)" href="<%=request.getContextPath()%>/css/ipad_custom.css"/>
		<tiles:insertAttribute name="pageHeader" ignore="true"/>
		<tiles:insertAttribute name="customHeader" ignore="true"/>
		
   <!-- Check for channel specific css -->

   
    <c:if test="${not empty globalConfiguration and not empty globalConfiguration.channelid}">
      <c:if test="${not empty globalConfiguration.channelcss}">
       <link rel="stylesheet" type="text/css" media="all" href="<%=request.getContextPath()%>/portal/theme/<c:out value="${globalConfiguration.channelid}/${globalConfiguration.channelcss}"/>"/> 
      </c:if>
    </c:if>
<tiles:insertDefinition name="google.analytics"/>
<!-- Adding css fix for IE8 - select box does not show full options - gets cut by defined width on expanding select box  -->
<!--[if IE 8]>
<style>

.expandable-select:focus {
  width: auto;
  position: absolute;
}
.expandable-select2:focus {
  width: auto;
}

</style>
<![endif]-->
<script type="text/javascript">
//for localization
var language = '<c:out value="${currentLocale.language}"/>';
var country = '<c:out value="${currentLocale.country}"/>';
$(document).ready(function() {
  $.datepicker.setDefaults($.datepicker.regional['']);
  if(country == ""){
    $.datepicker.setDefaults($.datepicker.regional['<c:out value="${currentLocale.language}"/>']);
  }else{
    $.datepicker.setDefaults($.datepicker.regional['<c:out value="${currentLocale.language}"/>-<c:out value="${currentLocale.country}"/>']);
  }
});
var g_dictionary = { 	
  errorFromAPICall : '<spring:message javaScriptEscape="true" code="error.from.API.call"/>',	
  yes : '<spring:message javaScriptEscape="true" code="label.yes"/>',	
  no : '<spring:message javaScriptEscape="true" code="label.no"/>',	
  labelTrue : '<spring:message javaScriptEscape="true" code="label.true"/>',	
  labelFalse : '<spring:message javaScriptEscape="true" code="label.false"/>',	
  required : '<spring:message javaScriptEscape="true" code="label.required"/>',
  save: '<spring:message javaScriptEscape="true" code="label.save"/>',
  edit: '<spring:message javaScriptEscape="true" code="label.edit"/>',
  doubleQuotesNotAllowed : '<spring:message javaScriptEscape="true" code="label.double.quotes.not.allowed"/>',      
  invalidNumber : '<spring:message javaScriptEscape="true" code="label.invalid.number"/>',	
  invalidInteger : '<spring:message javaScriptEscape="true" code="label.invalid.integer"/>',	
  minimum : '<spring:message javaScriptEscape="true" code="label.minimum"/>',	
  maximum : '<spring:message javaScriptEscape="true" code="label.maximum"/>',	
  actionFailed: '<spring:message javaScriptEscape="true" code="label.action.failed"/>',		
  actionSucceeded: '<spring:message javaScriptEscape="true" code="label.action.succeeded"/>',		
  failed: '<spring:message javaScriptEscape="true" code="label.failed"/>',		
  succeeded: '<spring:message javaScriptEscape="true" code="label.succeeded"/>',
  addingProcessing: '<spring:message javaScriptEscape="true" code="label.adding.processing"/>',
  example: '<spring:message javaScriptEscape="true" code="label.example"/>',
  youCanNotSubscribeUntilYouAcceptTheTermsAndConditions: '<spring:message javaScriptEscape="true" code="you.can.not.subscribe.until.you.accept.the.terms.and.conditions"/>',
  youCanNotContinueUntilYouAcceptTheTermsAndConditions: '<spring:message javaScriptEscape="true" code="you.can.not.continue.until.you.accept.the.terms.and.conditions"/>',
  passwordValidationeError: '<spring:message javaScriptEscape="true" code="js.errors.password"/>',
  inAction:'<spring:message javaScriptEscape="true" code="label.in.action"/>',
  phoneValidationError:"<spring:message javaScriptEscape="true" code="js.errors.phone"/>",
  flnameValidationError:"<spring:message javaScriptEscape="true" code="js.errors.flname"/>",
  dateonlyFormat:"<spring:message javaScriptEscape="true" code="js.dateonly.format"/>",
  filterDateFormat:"<spring:message javaScriptEscape="true" code="js.dateonly.filter.format"/>",
  friendlyDate:"<spring:message javaScriptEscape="true" code="js.friendly.date.format"/>",
  dateFormatForServer:"<spring:message javaScriptEscape="true" code="date.format"/>",
  jsDateFormat:"<spring:message javaScriptEscape="true" code="js.date.format"/>",
  jsGetTicketError:"<spring:message javaScriptEscape="true" code="js.alert.error.tickets.fetch"/>",  
  positiveInteger : '<spring:message javaScriptEscape="true" code="js.errors.positive.integer"/>',
  pricePositiveInteger : '<spring:message javaScriptEscape="true" code="js.errors.priceRequired"/>',
  dialogCancel: '<spring:message javaScriptEscape="true" code="label.cancel"/>',
  dialogOK: '<spring:message javaScriptEscape="true" code="label.ok"/>',
  dialogDone: '<spring:message javaScriptEscape="true" code="label.done"/>',
  dialogClose: '<spring:message javaScriptEscape="true" code="label.close"/>',
  dialogApply: '<spring:message javaScriptEscape="true" code="label.apply"/>',
  dialogDisable: '<spring:message javaScriptEscape="true" code="label.disable"/>',
  dialogConfirm: '<spring:message javaScriptEscape="true" code="label.confirm"/>',
  dialogSubmit: '<spring:message javaScriptEscape="true" code="label.submit"/>',
  highChartPrint:'<spring:message javaScriptEscape="true" code="highchart.chart.print.tooltip"/>',
  highChartExport: '<spring:message javaScriptEscape="true" code="highchart.chart.export.tooltip"/>',
  highChartDownloadPNG: '<spring:message javaScriptEscape="true" code="highchart.chart.export.menu.downloadPNG"/>',
  highChartDownloadJPEG: '<spring:message javaScriptEscape="true" code="highchart.chart.export.menu.downloadJPEG"/>',
  highChartDownloadPDF: '<spring:message javaScriptEscape="true" code="highchart.chart.export.menu.downloadPDF"/>',
  noDataToDisplay: '<spring:message javaScriptEscape="true" code="highchart.chart.no.data"/>',
  highChartDownloadSVG: '<spring:message javaScriptEscape="true" code="highchart.chart.export.menu.downloadSVG"/>',
  decPoint: '<fmt:formatNumber var="str" value="0.1"/><c:out value="${fn:substring(str,1,2)}"/>',
  thousandsSep: '<fmt:formatNumber var="str" value="1000"/><c:out value="${fn:substring(str,1,2)}"/>',
  KB: '<spring:message javaScriptEscape="true" code="KB"/>',
  MB: '<spring:message javaScriptEscape="true" code="MB"/>',
  GB: '<spring:message javaScriptEscape="true" code="GB"/>',
  TB: '<spring:message javaScriptEscape="true" code="TB"/>',
  MHZ: '<spring:message javaScriptEscape="true" code="MHZ"/>',
  GHZ: '<spring:message javaScriptEscape="true" code="GHZ"/>',
  IP_Month: '<spring:message javaScriptEscape="true" code="IP-Month"/>',
  Hours: '<spring:message javaScriptEscape="true" code="Hours"/>',
  GB_Months: '<spring:message javaScriptEscape="true" code="GB-Months"/>',
  Rules: '<spring:message javaScriptEscape="true" code="Rules"/>',
  oldPasswordNullError:'<spring:message javaScriptEscape="true" code="message.myprofile.oldpassword.null.error"/>',
  campaignCreationSuccess:'<spring:message javaScriptEscape="true" code="label.campaign.creation.success"/>',
  campaignCreationFailure:'<spring:message javaScriptEscape="true" code="label.campaign.creation.failure"/>',
  campaignEditedSuccess:'<spring:message javaScriptEscape="true" code="label.campaign.edit.success"/>',  
  campaignEditedFailure:'<spring:message javaScriptEscape="true" code="label.campaign.edit.falure"/>',
  provisionUnCheckMessage: '<spring:message javaScriptEscape="true" code="provision.uncheck.message"/>',
  lableChoose: '<spring:message javaScriptEscape="true" code="label.choose"/>', 
  custom: '<spring:message javaScriptEscape="true" code="label.custom"/>',
  dialogInvalidUnit: '<spring:message javaScriptEscape="true" code="label.invalidUnit"/>',
  dialogInvalidFactor: '<spring:message javaScriptEscape="true" code="label.invalidFactor"/>',
  dialogInvalidFactorValue : '<spring:message javaScriptEscape="true" code="label.invalidValueForFactor"/>',
  dialogNumberLessThanZero : '<spring:message javaScriptEscape="true" code="js.errors.priceRequired"/>',
  error_single_sign_on: '<spring:message javaScriptEscape="true" code="error.single.signon.failure"/>',
  error_cloud_service_down: '<spring:message javaScriptEscape="true" code="cloud.service.down"/>',
  dialogSaveAndPublish: '<spring:message javaScriptEscape="true" code="label.save.and.publish"/>',
  dialogSaveAndPreview: '<spring:message javaScriptEscape="true" code="label.save.and.preview"/>',
  closeWizardConfirmation:'<spring:message javaScriptEscape="true" code="message.close.wizard.confirmation"/>',
  labelDefault:'<spring:message javaScriptEscape="true" code="label.default"/>',
  labelNone:'<spring:message javaScriptEscape="true" code="label.none"/>',
  errorDetails:'<spring:message javaScriptEscape="true" code="label.error.details" />',
  details:'<spring:message javaScriptEscape="true" code="label.details" />',
  noMediationRulesForUsageType:'<spring:message javaScriptEscape="true" code="message.no.mediation.rules.for.usage.type" />',
  passwordResetSuccessful    : '<spring:message javaScriptEscape="true" code="label.reset.reset.password"/>',
  resetPasswordRequestSuccessful:'<spring:message javaScriptEscape="true" code="message.reset.password.request.successful"/>',
  noMatchesFound:'<spring:message javaScriptEscape="true" code="message.no.matches.found"/>',
  maxSelectableItems:'<spring:message javaScriptEscape="true" code="message.select2.max.selectable.items"/>',
  oneSelectableItem:'<spring:message javaScriptEscape="true" code="message.select2.one.selectable.item"/>'
};
var fusion_chart_localized_strings={
		ChartNoDataText:'<spring:message javaScriptEscape="true" code="message.fusioncharts.no.data.to.show"/>',
		LoadDataErrorText:'<spring:message javaScriptEscape="true" code="message.fusioncharts.error.loading.data"/>',
		XMLLoadingText:'<spring:message javaScriptEscape="true" code="message.fusioncharts.retrieving.data"/>',
		InvalidXMLText:'<spring:message javaScriptEscape="true" code="message.fusioncharts.invalid.XML.text"/>',
		ReadingDataText:'<spring:message javaScriptEscape="true" code="message.fusioncharts.reading.data.text"/>',
		ChartNotSupported:'<spring:message javaScriptEscape="true" code="message.fusioncharts.chart.not.supported"/>',
		LoadingText:'<spring:message javaScriptEscape="true" code="message.fusioncharts.loading.text"/>',
		RenderChartErrorText:'<spring:message javaScriptEscape="true" code="message.fusioncharts.render.chart.error.text"/>'
}
var i18nDayNames = [
  '<fmt:formatDate pattern="EEE" value="<%=new java.util.Date(0,0,0)%>"/>',
  '<fmt:formatDate pattern="EEE" value="<%=new java.util.Date(0,0,1)%>"/>',
  '<fmt:formatDate pattern="EEE" value="<%=new java.util.Date(0,0,2)%>"/>',
  '<fmt:formatDate pattern="EEE" value="<%=new java.util.Date(0,0,3)%>"/>',
  '<fmt:formatDate pattern="EEE" value="<%=new java.util.Date(0,0,4)%>"/>',
  '<fmt:formatDate pattern="EEE" value="<%=new java.util.Date(0,0,5)%>"/>',
  '<fmt:formatDate pattern="EEE" value="<%=new java.util.Date(0,0,6)%>"/>',
  '<fmt:formatDate pattern="EEEE" value="<%=new java.util.Date(0,0,0)%>"/>',
  '<fmt:formatDate pattern="EEEE" value="<%=new java.util.Date(0,0,1)%>"/>',
  '<fmt:formatDate pattern="EEEE" value="<%=new java.util.Date(0,0,2)%>"/>',
  '<fmt:formatDate pattern="EEEE" value="<%=new java.util.Date(0,0,3)%>"/>',
  '<fmt:formatDate pattern="EEEE" value="<%=new java.util.Date(0,0,4)%>"/>',
  '<fmt:formatDate pattern="EEEE" value="<%=new java.util.Date(0,0,5)%>"/>',
  '<fmt:formatDate pattern="EEEE" value="<%=new java.util.Date(0,0,6)%>"/>'
];
var i18nMonthNames = [
  '<fmt:formatDate pattern="MMM" value="<%=new java.util.Date(0,1,0)%>"/>',
  '<fmt:formatDate pattern="MMM" value="<%=new java.util.Date(0,2,0)%>"/>',
  '<fmt:formatDate pattern="MMM" value="<%=new java.util.Date(0,3,0)%>"/>',
  '<fmt:formatDate pattern="MMM" value="<%=new java.util.Date(0,4,0)%>"/>',
  '<fmt:formatDate pattern="MMM" value="<%=new java.util.Date(0,5,0)%>"/>',
  '<fmt:formatDate pattern="MMM" value="<%=new java.util.Date(0,6,0)%>"/>',
  '<fmt:formatDate pattern="MMM" value="<%=new java.util.Date(0,7,0)%>"/>',
  '<fmt:formatDate pattern="MMM" value="<%=new java.util.Date(0,8,0)%>"/>',
  '<fmt:formatDate pattern="MMM" value="<%=new java.util.Date(0,9,0)%>"/>',
  '<fmt:formatDate pattern="MMM" value="<%=new java.util.Date(0,10,0)%>"/>',
  '<fmt:formatDate pattern="MMM" value="<%=new java.util.Date(0,11,0)%>"/>',
  '<fmt:formatDate pattern="MMM" value="<%=new java.util.Date(0,12,0)%>"/>',
  '<fmt:formatDate pattern="MMMM" value="<%=new java.util.Date(0,1,0)%>"/>',
  '<fmt:formatDate pattern="MMMM" value="<%=new java.util.Date(0,2,0)%>"/>',
  '<fmt:formatDate pattern="MMMM" value="<%=new java.util.Date(0,3,0)%>"/>',
  '<fmt:formatDate pattern="MMMM" value="<%=new java.util.Date(0,4,0)%>"/>',
  '<fmt:formatDate pattern="MMMM" value="<%=new java.util.Date(0,5,0)%>"/>',
  '<fmt:formatDate pattern="MMMM" value="<%=new java.util.Date(0,6,0)%>"/>',
  '<fmt:formatDate pattern="MMMM" value="<%=new java.util.Date(0,7,0)%>"/>',
  '<fmt:formatDate pattern="MMMM" value="<%=new java.util.Date(0,8,0)%>"/>',
  '<fmt:formatDate pattern="MMMM" value="<%=new java.util.Date(0,9,0)%>"/>',
  '<fmt:formatDate pattern="MMMM" value="<%=new java.util.Date(0,10,0)%>"/>',
  '<fmt:formatDate pattern="MMMM" value="<%=new java.util.Date(0,11,0)%>"/>',
  '<fmt:formatDate pattern="MMMM" value="<%=new java.util.Date(0,12,0)%>"/>'
];
</script>
  </head>
<body>
    <div id="overlay_black" style="display: none">
    </div>
	  <div id="main">
	  <c:if test="${not empty previewModeSettings && previewModeSettings.isPreviewMode == true}">
	  <div class="page_top_alert">
			<div id="preview_mode_header" class="alert" >
			    <a class="alertaction" id="exit_preview_mode_link" href="javascript:void(0);"><spring:message code="label.exit.preview.mode"/></a>
			    <span class="alerticon"></span>
			    <span class="alerttext"><spring:message code="label.channel.branding.preview.mode" arguments="${previewModeSettings.previewChannelName}"/></span>
			  </div>
		</div>
		</c:if>
    	<tiles:insertAttribute name="header" />  				
    	<tiles:insertAttribute name="secondlevelheader" ignore="true" />
        
    	<div id="maincontent_container">
        	<div class="maincontent">
          	<tiles:insertAttribute name="thirdlevelheader" ignore="true" />
        	  <tiles:insertAttribute name="leftNav" ignore="true" />		
    		    <tiles:insertAttribute name="body" ignore="true"/>
    		    <tiles:insertAttribute name="instancecontent" ignore="true" />
    		</div>
    	</div>
        <div id="manage_resources_container" class="manage_resources_container" style="display:none;">
          <div id="iframe_spinning_wheel" class="iframe_spinner" style="display: none;">
            <div class="widget_blackoverlay iframe_loader"></div>
            <div class="widget_loadingbox iframe_loader_text">
              <div class="widget_loaderbox">
                <span class="bigloader"></span>
              </div>
              <div class="widget_loadertext">
                <p id="in_process_text">
                  <spring:message code="label.loading" />
                  &hellip;
                </p>
              </div>
            </div>
          </div> 
          
          <iframe id="manage_resources_iframe" width="100%" height="100%" frameborder="0">
          </iframe>
        </div>
    	<div class="clearboth">
    	</div>
   		<div id="footer">
		    <tiles:insertAttribute name="footer" />
   		</div>
    </div>
    
    <c:if test="${displayCookiesWarning}">
    	<div id="accept_cookies_panel" title='<spring:message code="ui.title.cookie.law.warning" />'>
    		<tiles:insertDefinition name="cookie_warning" />    
		</div>
    </c:if>
    <div id="dialog_confirmation" title='<spring:message code="lightbox.title.confirmation"/>' style="margin:10px;display: none">
    </div>    
    <div id="dialog_info" title='<spring:message code="lightbox.title.information"/>' style="margin:10px;display: none">
    </div>
    <div id="utilityrates_lightbox" class="utility_table" title='<spring:message code="label.view.utility.rates.dialog.title"/>' style="display:none;padding:10px;max-height:500px;">
    </div> 
    <div id="full_page_spinning_wheel" style="display: none;">
      <div class="widget_blackoverlay widget_full_page"></div>
      <div class="widget_loadingbox fullpage">
        <div class="widget_loaderbox">
          <span class="bigloader"></span>
        </div>
        <div class="widget_loadertext">
          <p id="in_process_text">
            <spring:message code="label.loading" />
            &hellip;
          </p>
        </div>
      </div>
    </div>
    <div id="errorMessageTemplate" class="alert alert-error" style="display: none;">
    	<span id="messageSpan"></span>
    	<span id="additionalMessageSpan" style="display: none;">(<a id="detailedMessageAnchor"></a>)</span>
    </div> 
    <script type="text/javascript" src="<%=request.getContextPath() %>/resources/app.js"></script>
</body>	
</html>



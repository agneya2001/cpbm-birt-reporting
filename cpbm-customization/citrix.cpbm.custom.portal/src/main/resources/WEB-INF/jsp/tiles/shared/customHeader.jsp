<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. --> 
   <%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
  
   <c:if test="${not empty globalConfiguration and not empty globalConfiguration.channelid}">
     <c:if test="${not empty globalConfiguration.channelfav}">
     <link type="image/x-icon" rel="shortcut icon" href="<%=request.getContextPath()%>/portal/theme/<c:out value="${globalConfiguration.channelid}/${globalConfiguration.channelfav}"/>"/> 
     </c:if>
  </c:if>
  <c:if test="${empty globalConfiguration.channelfav}">
    <link href="/portal/images/spfavicon.ico" type="image/x-icon" rel="shortcut icon" />
  </c:if>
   <script language="javascript">
   if( typeof unsupportedDictionary === 'undefined' ) {
		    var unsupportedDictionary = {};
	 }
   unsupportedDictionary = {    
    header: '<spring:message javaScriptEscape="true" code="browser.unsupported.header"/>',
    paragraph1: '<spring:message javaScriptEscape="true" code="browser.unsupported.paragraph1"/>',
    paragraph2: '<spring:message javaScriptEscape="true" code="browser.unsupported.paragraph2"/>',
    ie7RejectParagraph1: '<spring:message javaScriptEscape="true" code="browser.unsupported.paragraph1.for.ie7.compat.view"/>',
    ie7RejectHeader:'<spring:message javaScriptEscape="true" code="browser.unsupported.header.for.ie7.compat.view"/>'
    
   };
   </script>
   <script type="text/javascript" src="/portal/js/appIE6reject.js"></script>

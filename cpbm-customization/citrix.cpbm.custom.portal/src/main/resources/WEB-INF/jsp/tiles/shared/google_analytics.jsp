<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. --> 
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${isGoogleAnalyticsEnabled}">
    <script type="text/javascript">

  var _gaq = _gaq || [];
  _gaq.push(['_setAccount', '<c:out value="${googleAnalyticsAccount}"/>']);
  _gaq.push(['_setDomainName', '<c:out value="${googleAnalyticsDomain}"/>']);
  _gaq.push(['_trackPageview']);

  (function() {
    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
  })();

</script>
</c:if>
<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec"
  uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<script type="text/javascript" src="<%=request.getContextPath() %>/js/commonproducts.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/commonBundles.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/jquery.iframe-post-form.js"></script>

<script type="text/javascript">
function listProducts(){
  var data = {};
  if($("#whichPlan").val() == "history"){
    if($("#rpb_history_dates option:selected").val().trim() == ""){
      return;
    }
    data["revisionDate"] =  $("#rpb_history_dates option:selected").val().trim();
  }
  data["whichPlan"] =  $("#whichPlan").val();
  $.ajax({
    url : "<%=request.getContextPath()%>/portal/productBundles/list",
    dataType : "html",
    data : data,
    async : true,
    cache : false,
    success : function(html) {
      $("#bundleslist").empty();
      $("#bundleslist").html(html);
    },
    error : function(XMLHttpResponse) {
      handleError(XMLHttpResponse);
    }
  });
}
$(document).ready(function() {
  listProducts();
});
</script>

<div id="bundleslist"></div>
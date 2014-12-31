<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. --> 
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<div class="dialog_formcontent wizard">
    <span class="helptext">
   <spring:message code="ui.bundle.sort.help.text1"></spring:message>&nbsp;&nbsp;&nbsp;<span class="moveicon_description"></span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   <spring:message code="ui.bundle.sort.help.text2"></spring:message>
    </span>
   <form id="bundlesOrder">
            <input type="hidden" name="bundlesOrderData" id="bundlesOrderData"/>
   </form> 
   <div class="widgetwizard_selectionbox sortproduct" style="margin-bottom: 20px;"  >
       <ul class="ui-sortable" id="sortbundleslist">
           <c:forEach items="${bundlesList}" var="bundle" varStatus="bundlestatus">
                  <c:set var="bundleTypeClass" value="computebundles"></c:set>
        
             <li class="widgetwizard_selectionbox" style="cursor: pointer;" id="<c:out value="sort${bundle.id}"/>" >
                 <span style="height: 19px;margin-top:7px;" id="nav_icon" class="navicon <c:out value="${bundleTypeClass}"/>"></span>                
                 <span class="description"><c:out value="${bundle.name}" /> </span>
                 <span class="movebox">
                     <span class="moveicon"></span>
                 </span>
             </li>
           </c:forEach>
          
         </ul>
     </div>
</div>

<script type="text/javascript">
$("#sortbundleslist").sortable({
    start: function(event, ui) {
      ui.item.addClass('active');
    },
    update: function(event, ui) {
      var sortableArray = $(this).sortable('toArray');
      for (var i = 0; i < sortableArray.length; i++) {
        sortableArray[i] = sortableArray[i].substr(4);
      }
      $("#bundlesOrderData").val(sortableArray.toString());
      $.ajax({
        type: "POST",
        url: "/portal/portal/productBundles/editbundlesorder",
        data: $("#bundlesOrderData").serialize(),
        dataType: "json",
        success: function() {
          //location.reload(true);      
          //Do Nothing
        },
        error: function() {
          //location.reload(true);
        }
      });
    }
});
</script>
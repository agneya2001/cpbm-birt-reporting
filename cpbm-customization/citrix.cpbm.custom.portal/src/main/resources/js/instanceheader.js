/*
*  Copyright © 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/
/*js which will load instance specific resource body by calling fragment controller and let it provide the tile def.*/
$(document).ready(function() {

  var tenantParam = $("#tenantParam").val();
  var serviceCategory = $("#selectedCategory").val();

  if (typeof serviceCategory == 'undefined' || serviceCategory == null || serviceCategory == "") {
    serviceCategory = $('#service_category_list_container li').first().attr("category");
  }
  populateServiceInstances(serviceCategory, tenantParam, refreshViewTabs, refreshViewTabs);

  var serviceCategoryListItems = $("#service_category_list_container li");
  serviceCategoryListItems.each(function() {
    $(this).unbind("click").bind("click", function() {
      $(".categorytabs").removeClass("current user");
      $(this).removeClass().addClass("categorytabs current user");
      serviceCategory = $(this).attr("category");

      populateServiceInstances(serviceCategory, tenantParam, refreshViewTabs, refreshViewTabs);
    });
  });

});

var refreshViewTabs = function loadViewTabs(serviceInstanceUUID, tenantParam) {
  var frame = document.getElementById("serviceInstanceResourceView");
  frame.src = "about:blank";
  $.ajax({
    url: "/portal/portal/manage_resource/get_resource_views",
    dataType: "html",
    data: {
      serviceInstanceUUID: serviceInstanceUUID,
      tenant: tenantParam
    },
    async: true,
    cache: false,
    success: function(html) {
      $("#serviceInstanceViewTabs").empty();
      $("#serviceInstanceViewTabs").html(html);
      initializeResourceTab(tenantParam, serviceInstanceUUID);
    },
    error: function(XMLHttpResponse) {
      handleError(XMLHttpResponse);
    }
  });

};

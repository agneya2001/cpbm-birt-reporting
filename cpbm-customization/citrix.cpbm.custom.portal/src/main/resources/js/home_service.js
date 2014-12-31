/*
*  Copyright � 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/
$(document).ready(function() {
  var serviceCategory = $('#service_category_list_container li').first().attr("category");

  populateServiceInstances(serviceCategory, effectiveTenantParam, refreshHomeItems, refreshHomeItems);

  var serviceCategoryListItems = $("#service_category_list_container li");
  serviceCategoryListItems.each(function() {
    $(this).unbind("click").bind("click", function() {
      $(".categorytabs").removeClass("current user");
      $(this).removeClass().addClass("categorytabs current");
      serviceCategory = $(this).attr("category");

      populateServiceInstances(serviceCategory, effectiveTenantParam, refreshHomeItems, refreshHomeItems);
    });
  });
  if (typeof getServiceInstanceHealth != 'undefined' && getServiceInstanceHealth) {
    getServiceInstanceStatus();
  }
});

var refreshHomeItems = function loadHomeItems(serviceInstanceUUID, tenantParam) {
  if (serviceInstanceUUID == null || serviceInstanceUUID == "") {
    $("#cloudServiceConsoleID").hide();
    $("#cloudServiceConsoleAll").show();
  } else {
    $("#cloudServiceConsoleAll").hide();
    $("#cloudServiceConsoleID").show();
  }
  if (typeof serviceInstanceUUID != "undefined" && typeof tenantParam != "undefined") {
    selectedServiceInstance = serviceInstanceUUID;
    $.ajax({
      url: "/portal/portal/home/get_home_items",
      data: {
        serviceInstanceUUID: serviceInstanceUUID,
        tenant: tenantParam
      },
      dataType: "html",
      async: true,
      cache: false,
      success: function(html) {
        $("#home_items_view").empty().html(html);
      }
    });
  }

};

function launchCloudServiceConsole(current) {
  var selectedCloudServiceInstance = $("#selectedServiceInstance").find(".downarrow").attr("id");
  launchCloudServiceConsoleWithServiceInstanceUUID(current, selectedCloudServiceInstance);
}

function launchCloudServiceConsoleWithServiceInstanceUUID(current, serviceInstanceUUID) {
  var currentTenantParam = $("#currentTenantParam").val();
  if (serviceInstanceUUID != null) {
    singleSignOn(currentTenantParam, serviceInstanceUUID);
    $.cookie('lang', '<c:out value="${pageContext.request.locale.language}"/>', {
      path: '/'
    });
    $.ajax({
      type: "GET",
      url: cloudServiceConsoleUrl,
      data: {
        tenant: currentTenantParam,
        serviceInstanceUUID: serviceInstanceUUID
      },
      dataType: "text",
      success: function(url) {
        window.open(url, "_blank");
      },
      error: function() {
        //TODO: handle error
      }
    });
  }
}

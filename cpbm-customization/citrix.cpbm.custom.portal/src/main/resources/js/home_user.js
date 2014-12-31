/*
*  Copyright © 2013 Citrix Systems, Inc.
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
      $(this).removeClass().addClass("categorytabs current user");
      serviceCategory = $(this).attr("category");

      populateServiceInstances(serviceCategory, effectiveTenantParam, refreshHomeItems, refreshHomeItems);
    });
  });

  $("#browseCatalogButton").unbind("click").bind("click", function(event) {
    var showBrowseCatalog = $(this).attr('browseCatalog');
    if (showBrowseCatalog == 'false') {
      return;
    }

    if (isDelinquent == true) {
      if (redirectToBilling == true) {
        window.location = "/portal/portal/billing/history?tenant=" + tenantParam2 + "&action=launchvm"; //redirect them to payment page   
      }
      return false;
    }
    var tenantParam = $("#tenantParam").val();
    if ($("#accountState").val() != "NEW") {
      window.location = "/portal/portal/subscription/createsubscription?tenant=" + tenantParam;

    } else {
      if ($("#isOwner").val() == "true") {
        //If user hasn't provided billing info    
        window.location = "/portal/portal/tenants/editcurrent?tenant=" + tenantParam + "&action=launchvm"; //redirect them to payment page    

      } else {
        initDialogWithOK("dialog_info", 350, false);
        $("#dialog_info").dialog("option", "height", 150);
        $("#dialog_info").text(dictionary.userNoBilling).dialog("open");
        return;
      }
      return;
    }
  });
  if (typeof showNetBalanceDialog != 'undefined' && showNetBalanceDialog) {
    $("#showCreditBalance").unbind("click").bind("click", function(event) {
      var dialogId = "dialog_net_balance";
      initDialog(dialogId, 350);
      var $thisDialog = $("#" + dialogId);
      $thisDialog.dialog('option', 'buttons', {
        "Cancel": function() {
          $(this).dialog("close");
        }
      });
      dialogButtonsLocalizer($thisDialog, {
        'Cancel': g_dictionary.dialogClose
      });
      $thisDialog.dialog("open");
    });
  }
  if (typeof getServiceInstanceHealth != 'undefined' && getServiceInstanceHealth) {
    getServiceInstanceStatus();
  }
});

var refreshHomeItems = function loadHomeItems(serviceInstanceUUID, tenantParam) {
  if (serviceInstanceUUID == null || serviceInstanceUUID == "") {
    $("#cloudServiceResources").hide();
    $("#cloudServiceResourcesAll").show();
  } else {
    $("#cloudServiceResourcesAll").hide();
    $("#cloudServiceResources").show();
    $("#cloudServiceResources").unbind("click").bind("click", function() {
      launchMyResourcesWithServiceInstanceUUID(serviceInstanceUUID);
    });
  }
  if (typeof serviceInstanceUUID != "undefined" && typeof tenantParam != "undefined") {
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

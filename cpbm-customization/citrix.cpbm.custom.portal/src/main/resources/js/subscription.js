/*
*  Copyright � 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/
var request_url = "subscriptions";
$(document).ready(function() {
  
  activateThirdMenuItem("l3_subscriptions_tab");
  
  $("#resource_subscriptions_info_link").attr("data-content", $("#resource_subscription_info_text").html());
  $("#resource_subscriptions_info_link").popover();
  $('body').on('click', function (e) {
    if($(e.target).attr("id") != "resource_subscriptions_info_link" && !$(e.target).hasClass("js_entitlement_details")) {
      $(".popover").hide();
    }
  });
  
  
  
  if(utilityTabSelected) {
    request_url = "utility_charges";
  }
  $("#resource_subscriptions_link").unbind("click").bind("click", function() {
    window.location = "/portal/portal/billing/utility_charges?tenant=" + effectiveTenantParam + "&perPage=14&page=1";
  });
  $("#bundle_subscriptions_link").unbind("click").bind("click", function() {
    window.location = "/portal/portal/billing/subscriptions?tenant=" + effectiveTenantParam + "&perPage=14&page=1";
  });
  
  terminateActionMap = {
      subscription: {
          label: dictionary.terminatesubscription,
          confirmation: dictionary.lightboxterminatesubscription,
          inProcessText: dictionary.terminatingSubscription
      },
      resource: {
          label: dictionary.terminateResource,
          confirmation: dictionary.lightboxterminateresource,
          inProcessText: dictionary.terminatingResource
      }
  };
  
  function getCurrentCommand(item) {
    if(utilityTabSelected) {
      return terminateActionMap["resource"][item];
    }
    return terminateActionMap["subscription"][item];
  }

  // Decide how to show 'List All' link and bind unbind accordingly
  var selectedDetails = $("#selected_subs_for_details").val();
  var filtersApplied = $("#filtersApplied").val();
  if ((filtersApplied != null && filtersApplied > 0) || (selectedDetails != null && selectedDetails != '')) {
    $("#list_all").addClass("title_listall_arrow");
    $("#list_titlebar").unbind("click").bind("click", function() {
      if ("true" == $("#usage_billing_my_usage").val()) {
        window.location = "/portal/portal/usage/" + request_url + "?tenant=" + $("#tenantParam").val() + "&state=All";
      } else {
        window.location = "/portal/portal/billing/" + request_url + "?tenant=" + $("#tenantParam").val() + "&state=All";
      }
    });
    if( selectedDetails != null && selectedDetails != ''){
      $("#search_panel").hide();
    }    
  } else {
    $("#list_all").removeClass("title_listall_arrow");
    $("#list_titlebar").unbind("click");
  }
  
  
  
  $("#advancesearchButton").click(function() {
    $("#advanceSearchDropdownDiv").toggle();
  });

  $("#advSrchCancel").click(function() {
	$("#dropdownfilter_users").val($('#useruuid').val());
	$("#dropdownfilter_states").val($('#stateSelected').val());
	$("#dropdownfilter_instances").val($('#instanceuuid').val());
	$("#dropdownfilter_bundles").val($('#productBundleID').val());
	$("#dropdownfilter_resourceTypes").val($('#resourceTypeID').val());
  $("#advanceSearchDropdownDiv").hide(); 
  });
  
  $("#selected_filters").text();
  $("#dropdownfilter_instances").change(function() {
    
    if(utilityTabSelected) {
      var selected_service = $("#dropdownfilter_instances option:selected").attr('serviceUuid');
      $("#dropdownfilter_resourceTypes").empty();
      $("#dropdownfilter_resourceTypes").append($("#hidden_resourceTypes option").first().clone());
      $("#dropdownfilter_resourceTypes").append($("#hidden_resourceTypes option[serviceUuid="+ selected_service +"]").clone());
      if($("#selectedResourceTypeID").val() != null && $("#selectedResourceTypeID").val() !=""){
        $("#dropdownfilter_resourceTypes option[id="+ $("#selectedResourceTypeID").val() +"]").attr('selected','selected');
      }
    } else {
      var selected_instance = $("#dropdownfilter_instances option:selected").attr('id');
      $("#dropdownfilter_bundles").empty();
      $("#dropdownfilter_bundles").append($("#hidden_bundles option").first().clone());
      $("#dropdownfilter_bundles").append($("#hidden_bundles option[instance="+ selected_instance +"]").clone());
      if($("#selectedBundleID").val() != null && $("#selectedBundleID").val() !=""){
        $("#dropdownfilter_bundles option[id="+ $("#selectedBundleID").val() +"]").attr('selected','selected');
      }
    }
  });
  
  $("#dropdownfilter_instances").change();
  
  $("#advSrchSubmit").click(function() {
    
	  var useruuid = $("#dropdownfilter_users option:selected").val();
	  var state = $("#dropdownfilter_states option:selected").val();
	  var instanceParam = $("#dropdownfilter_instances option:selected").val();
	  var bundleId = $("#dropdownfilter_bundles option:selected").val();
	  var resourceTypeId = $("#dropdownfilter_resourceTypes option:selected").val();
	  
    var $currentPage = 1;
    
    if ("true" == $("#usage_billing_my_usage").val()) {
      filterurl = "/portal/portal/usage/" + request_url + "?tenant=" + $("#tenantParam").val()  + "&state=" + state;
    } else {
      filterurl = "/portal/portal/billing/" + request_url + "?tenant=" + $("#tenantParam").val() + "&state=" + state;
    }
    
    if (useruuid != null && useruuid != 'ALL') {
      filterurl = filterurl + "&useruuid=" + useruuid;
    }
    if (bundleId != null && bundleId != 'ALL') {
      filterurl = filterurl + "&productBundleID=" + bundleId;
    }
    if (resourceTypeId != null && resourceTypeId != 'ALL') {
      filterurl = filterurl + "&resourceTypeId=" + resourceTypeId;
    }
    if (instanceParam != null && instanceParam != 'ALL') {
      filterurl = filterurl + "&instanceuuid=" + instanceParam;
    }
    
    window.location = filterurl + "&page=" + (parseInt($currentPage));
  });  
  
  function refreshGridRow(jsonObj, $template) {
    if (jsonObj.state == "EXPIRED")
      $template.find(".widget_statusicon").removeClass().addClass("widget_statusicon stopped");
    else if (jsonObj.state == "ACTIVE")
      $template.find(".widget_statusicon").removeClass().addClass("widget_statusicon running");
    else
      $template.find(".widget_statusicon").removeClass().addClass("widget_statusicon nostate");

  }

  initDialog("dialog_confirmation", 350, false);
  var topActionMap = {
    terminatesubscription: {
      label: getCurrentCommand("label"),
      elementIdPrefix: "terminatesubscription",
      inProcessText: getCurrentCommand("inProcessText"),
      type: "POST",
      afterActionSeccessFn: function(resultObj) {
        $("#subscriptionState").html(resultObj.state);
        refreshGridRow(resultObj, $("li[id^='sub'].selected.subscriptions"));
        viewSubscription($("li[id^='sub'].selected.subscriptions"));
      },
      afterActionFailureFn : function(xhr, status) {
        if(isNotBlank(xhr.responseText)) {
          return dictionary.cloudServiceException + " : " + xhr.responseText;
        }
        return null;
      },
      afterActionCompleteFn: function(jqXHR, textStatus){
        // adding this to override global ajaxSetup complete event handler
        return;
      }
    },
    cancelsubscription: {
      label: dictionary.cancelsubscription,
      elementIdPrefix: "cancelsubscription",
      inProcessText: dictionary.cancellingSubscription,
      type: "POST",
      afterActionSeccessFn: function(resultObj) {
        $("#subscriptionState").html(resultObj.state);
        refreshGridRow(resultObj, $("li[id^='sub'].selected.subscriptions"));
        viewSubscription($("li[id^='sub'].selected.subscriptions"));
      },
      afterActionFailureFn : function(xhr, status) {
        if(isNotBlank(xhr.responseText)) {
          return dictionary.cloudServiceException + " : " + xhr.responseText;
        }
        return null;
      },
      afterActionCompleteFn: function(jqXHR, textStatus){
        // adding this to override global ajaxSetup complete event handler
        return;
      }
    }
  };

  function getConfirmationDialogButtons(command) {

    var buttonCallBacks = {};
    var actionMapItem;
    if (command == "terminatesubscription") {
      actionMapItem = topActionMap.terminatesubscription;
    } else if (command == "cancelsubscription") {
      actionMapItem = topActionMap.cancelsubscription;
    }

    buttonCallBacks[dictionary.lightboxbuttonconfirm] = function() {
      $(this).dialog("close");

      var apiCommand;
      if (command == "terminatesubscription") {
        var subscriptionParam = $('#current_subscription_param').val();
        apiCommand = billingPath + "subscriptions/terminate/" + subscriptionParam;

      }
      if (command == "cancelsubscription") {
        var subscriptionParam = $('#current_subscription_param').val();
        apiCommand = billingPath + "subscriptions/cancel/" + subscriptionParam;
      }

      doActionButton(actionMapItem, apiCommand);

    };

    buttonCallBacks[dictionary.lightboxbuttoncancel] = function() {
      $(this).dialog("close");
    };

    return buttonCallBacks;
  }

  $(".terminatesubscription_link").live("click", function(event) {
    $("#dialog_confirmation").text(getCurrentCommand("confirmation")).dialog('option', 'buttons',
      getConfirmationDialogButtons("terminatesubscription")).dialog("open");
      viewSubscription($("li[id^='sub'].selected.subscriptions"));
  });
  $(".cancelsubscription_link").live("click", function(event) {
    $("#dialog_confirmation").text(dictionary.lightboxcancelsubscription).dialog('option', 'buttons',
      getConfirmationDialogButtons("cancelsubscription")).dialog("open");
  });
  timerFunction($("li[id^='sub'].selected.subscriptions"));
});

/**
 * Update subscription row
 */
$.editSubscription = function(jsonResponse) {

  if (jsonResponse == null) {
    popUpDialogForAlerts("dialog_info", i18n.errors.subscription.editSubscription);
  } else {
    $("#viewDetailsDiv").html("");
    var content = "";
    content = content + "<div class='db_gridbox_columns' style='width:33%;'>";
    content = content + "<div class='db_gridbox_celltitles'>";
    content = content + jsonResponse.id;
    content = content + "</div>";
    content = content + "</div>";
    content = content + "<div class='db_gridbox_columns' style='width:33%;'>";
    content = content + "<div class='db_gridbox_celltitles'>";
    content = content + jsonResponse.state;
    content = content + "</div>";
    content = content + "</div>";
    content = content + "<div class='db_gridbox_columns' style='width:33%;'>";
    content = content + "<div class='db_gridbox_celltitles'>";
    content = content + jsonResponse.productBundle.name;
    content = content + "</div>";
    content = content + "</div>";
    $("#row" + jsonResponse.param).html(content);
    timerFunction($("#row" + jsonResponse.id));
  }
};

function refreshRow($template, subState, handleState, isServiceSubscription) {
  if (subState == "EXPIRED") {
    $template.find(".widget_statusicon").removeClass().addClass("widget_statusicon stopped");
  } else if (subState == "ACTIVE") {
    if(isServiceSubscription || (handleState != null && handleState == "ACTIVE")) {
      $template.find(".widget_statusicon").removeClass().addClass("widget_statusicon running");
    } else {
      $template.find(".widget_statusicon").removeClass().addClass("widget_statusicon interrupted");
    }
  } else {
    $template.find(".widget_statusicon").removeClass().addClass("widget_statusicon nostate");
  }
}

function timerFunction(current) {
  hideInfoBubble(current);
  $("#spinning_wheel2").show();
  var timerKey = "timerKey";
  $("body").stopTime(timerKey);
  var promise = viewSubscription(current);
  
  if(promise == null) {
    $("#spinning_wheel2").hide();
    return;
  }
  
  promise.done(function(){
    $("#spinning_wheel2").hide();
    var subscriptionState = $("#viewDetailsDiv").find("#subscription_state").val();
    var handleState = $("#viewDetailsDiv").find("#subscription_handle_state").val();
    var refresh = (subscriptionState == "NEW" && (handleState == "PROVISIONING" || handleState == "ACTIVE")) || (subscriptionState == "ACTIVE" && handleState == "PROVISIONING");
    if(refresh) {
      $("body").everyTime(5000, timerKey, function() {
        var lastActionMsg = $("#viewDetailsDiv").find(".top_notifications").html();
        var promise = viewSubscription(current);
        if(promise == null) {
          $("body").stopTime(timerKey);
          return;
        }
        promise.done(function(){
          var subscriptionState = $("#viewDetailsDiv").find("#subscription_state").val();
          var handleState = $("#viewDetailsDiv").find("#subscription_handle_state").val();
          var refresh = (subscriptionState == "NEW" && (handleState == "PROVISIONING" || handleState == "ACTIVE")) || (subscriptionState == "ACTIVE" && handleState == "PROVISIONING");
          refreshRow(current, subscriptionState, handleState);
          if(!refresh) {
            $("body").stopTime(timerKey);
          } else {
            $("#viewDetailsDiv").find(".top_notifications").html(lastActionMsg);
          }
        });
      }, 0);
    }
  });
}
  
/**
 * View Subscription details
 * 
 * @param current
 * @return
 */

function viewSubscription(current) {
  var deferred = $.Deferred();
  var divId = $(current).attr('id');
  if (divId == null) {
    return null;
  }
  var id = divId.substr(3);
  resetGridRowStyle();
  $(current).addClass("selected active");
  var url = billingPath + request_url + "/showDetails?tenant=" + $("#tenantParam").val();
  $.ajax({
    type: "POST",
    url: url,
    async: false,
    data: {
      id: id
    },
    dataType: "html",
    success: function(html) {
      $("#viewDetailsDiv").html("");
      $("#viewDetailsDiv").html(html);
      
      $("#viewDetailsDiv").find("#js_resource_error").popover();
      
      bindActionMenuContainers();
      
      var subscriptionState = $("#viewDetailsDiv").find("#subscription_state").val();
      var handleState = $("#viewDetailsDiv").find("#subscription_handle_state").val();
      var resourceType = $("#viewDetailsDiv").find("#subscription_resource_type").val() ;
      var isServiceSubs = (resourceType == null || resourceType == "" );
      refreshRow(current, subscriptionState, handleState,isServiceSubs);
      var $currentGridRow = $("#sub" + id);
      $currentGridRow.find("#subscriptionStateDivId").text($("#viewDetailsDiv").find("#subscriptionState").text());
      var endDate = $("#viewDetailsDiv").find("#endDate").val();
      if (endDate != null && subscriptionState == "EXPIRED"){
        $currentGridRow.find("#info_sub_end_date").html(endDate);
        $currentGridRow.find("#info_sub_end_date").parents(".raw_content_row").show();
        $currentGridRow.find("#sub_end_date_subtitle").html(" - " + endDate).show();
      }
      
      $("#details_tab").bind("click", function(event) {
        $(this).siblings(".active").removeClass('active').addClass("nonactive");
        $(this).removeClass("nonactive").addClass("active");
        $('#entitlements').hide();
        $('#configurations').hide();
        $('#resource_details').hide();
        $('#subscription_history_details').hide();
        $('#subscription_charges').show();
      });

      $("#entitlements_tab").bind("click", function() {
        $(this).siblings(".active").removeClass('active').addClass("nonactive");
        $(this).removeClass("nonactive").addClass("active");
        $('#resource_details').hide();
        $('#subscription_charges').hide();
        $('#configurations').hide();
        $('#subscription_history_details').hide();
        $('#entitlements').show();
      });

      $("#configurations_tab").bind("click", function() {
        $(this).siblings(".active").removeClass('active').addClass("nonactive");
        $(this).removeClass("nonactive").addClass("active");
        $('#entitlements').hide();
        $('#subscription_charges').hide();
        $('#resource_details').hide();
        $('#subscription_history_details').hide();
        $('#configurations').show();
      });
      

      $("#resource_details_tab").bind("click", function() {
        $(this).siblings(".active").removeClass('active').addClass("nonactive");
        $(this).removeClass("nonactive").addClass("active");
        $('#entitlements').hide();
        $('#subscription_charges').hide();
        $('#configurations').hide();
        $('#subscription_history_details').hide();
        $('#resource_details').show();
      });
      
      $("#subscription_history_tab").bind("click", function() {
        $(this).siblings(".active").removeClass('active').addClass("nonactive");
        $(this).removeClass("nonactive").addClass("active");
        $('#entitlements').hide();
        $('#subscription_charges').hide();
        $('#resource_details').hide();
        $('#configurations').hide();
        $('#subscription_history_details').show();
      });
      
      deferred.resolve();
    },
    error: function(xhr) {
      $("#spinning_wheel2").hide();
    },
    complete: function(xhr, status) {
      // Just added to prevent it from going to generic handler
    }
  });
  return deferred.promise();
}


/**
 * Reset data row style
 * @return
 */

function resetGridRowStyle() {
  $(".widget_navigationlist").each(function() {
    $(this).removeClass("selected active");
  });
}

function showInfoBubble(current) {
  if ($(current).hasClass('active')) return
  $(current).find("#info_bubble").show();
  return false;
};

function hideInfoBubble(current) {
  $(current).find("#info_bubble").hide();
  return false;
};

function nextClick() {
  var $currentPage = $('#current_page').val();
  var useruuid = $("#dropdownfilter_users option:selected").val();
  var state = $("#dropdownfilter_states option:selected").val();
  var instanceParam = $("#dropdownfilter_instances option:selected").val();
  var bundleId = $("#dropdownfilter_bundles option:selected").val();
  var resourceTypeId = $("#dropdownfilter_resourceTypes option:selected").val();

  var filterurl = "";
  if (useruuid != null && useruuid != 'ALL') {
    filterurl = filterurl + "&useruuid=" + useruuid;
  }
  if (bundleId != null && bundleId != 'ALL') {
    filterurl = filterurl + "&productBundleID=" + bundleId;
  }
  if (resourceTypeId != null && resourceTypeId != 'ALL') {
    filterurl = filterurl + "&resourceTypeId=" + resourceTypeId;
  }
  if (instanceParam != null && instanceParam != 'ALL') {
    filterurl = filterurl + "&instanceuuid=" + instanceParam;
  }
  
  filterurl = filterurl + "&state=" + state + "&page=" + (parseInt($currentPage) + 1);
  
  if ("true" == $("#usage_billing_my_usage").val()) {
    window.location = "/portal/portal/usage/" + request_url + "?tenant=" + $("#tenantParam").val() + filterurl;
  } else {
    window.location = "/portal/portal/billing/" + request_url + "?tenant=" + $("#tenantParam").val() + filterurl;
  }
}

function previousClick() {
  var $currentPage = $('#current_page').val();
  var useruuid = $("#dropdownfilter_users option:selected").val();
  var state = $("#dropdownfilter_states option:selected").val();
  var instanceParam = $("#dropdownfilter_instances option:selected").val();
  var bundleId = $("#dropdownfilter_bundles option:selected").val();
  var resourceTypeId = $("#dropdownfilter_resourceTypes option:selected").val();
  
  var filterurl = "";
  if (useruuid != null && useruuid != 'ALL') {
    filterurl = filterurl + "&useruuid=" + useruuid;
  }
  if (bundleId != null && bundleId != 'ALL') {
    filterurl = filterurl + "&productBundleID=" + bundleId;
  }
  if (resourceTypeId != null && resourceTypeId != 'ALL') {
    filterurl = filterurl + "&resourceTypeId=" + resourceTypeId;
  }
  if (instanceParam != null && instanceParam != 'ALL') {
    filterurl = filterurl + "&instanceuuid=" + instanceParam;
  }
  filterurl = filterurl + "&state=" + state + "&page=" + (parseInt($currentPage) - 1);
  
  if ("true" == $("#usage_billing_my_usage").val()) {
    window.location = "/portal/portal/usage/" + request_url + "?tenant=" + $("#tenantParam").val() + filterurl;
  } else {
    window.location = "/portal/portal/billing/" + request_url + "?tenant=" + $("#tenantParam").val() + filterurl;
  }
}
/*
function filter_subscriptions(current) {
  var useruuid = $("#userfilterdropdownforinvoices option:selected").val();
  var state = $("#filter_dropdown option:selected").val();
  var $currentPage = $('#current_page').val();
  var filterurl = "/portal/portal/usage/subscriptions?tenant=" + $("#tenantParam").val() + "&page=" + (parseInt(
    $currentPage));
  if (useruuid != null && useruuid != 'ALL_USERS') {
    filterurl = filterurl + "&useruuid=" + useruuid;
  }
  filterurl = filterurl + "&state=" + state;
  window.location = filterurl;
}
*/

function provisionSubscription(subscriptionId) {
  window.location = "/portal/portal/subscription/createsubscription?tenant=" + $("#tenantParam").val() +
    "&subscriptionId=" + subscriptionId;
}

function convertToSubscription(subscriptionUuid) {
  $("#full_page_spinning_wheel").show();
  initDialog("dialog_convert_subscription");
  $.ajax({
    type: "GET",
    url: "/portal/portal/subscription/convertToSubscription?tenant=" + $("#tenantParam").val() +
    "&subscriptionUuid=" + subscriptionUuid,
    dataType: "html",
    success: function(html) {
      var $thisDialog = $("#dialog_convert_subscription");
      $thisDialog.dialog("option", {
        height: "auto",
        width: 785
      });
      $thisDialog.html(html);
      $thisDialog.bind("dialogbeforeclose", function(event, ui) {
        $(".popover").hide();
        $thisDialog.empty();
      });
      initConvertSubscription();
      $("#full_page_spinning_wheel").hide();
      $thisDialog.dialog('open');
    },
    error: function() {
      $("#full_page_spinning_wheel").hide();
    },
    complete: function(xhr, status) {
      // Just added to prevent it from going to generic handler
    }
  });
}

$(function (){
  $(".js_filter_details_popover").popover({trigger:"hover",html : true, content: function() {

    var filterUser = $("#dropdownfilter_users option:selected").text();
    var filterState = $("#dropdownfilter_states option:selected").text();
    var filterInstance = $("#dropdownfilter_instances  option:selected").text();
    var filterBundle = $("#dropdownfilter_bundles option:selected").text();
    var filterResourceType = $("#dropdownfilter_resourceTypes option:selected").text();

    $('#js_filter_details_popover').find("#_filter_state").text(filterState);
    $('#js_filter_details_popover').find("#_filter_instance").text(filterInstance);
    $('#js_filter_details_popover').find("#_filter_bundle").text(filterBundle);
    $('#js_filter_details_popover').find("#_filter_resourceType").text(filterResourceType);
    
    if (filterUser !="" && filterUser.length > 0) {
      $('#js_filter_details_popover').find("#_filter_user").text(filterUser);
    }else{
      $('#js_filter_details_popover .popover_rows:first').hide();
    }

    return $('#js_filter_details_popover').html();
  }});
});

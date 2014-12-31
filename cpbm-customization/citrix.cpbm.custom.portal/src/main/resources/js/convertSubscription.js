/*
*  Copyright � 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/

function prepareReviewAndConfirm($selectedRow) {
  $("#dialog_convert_subscription").find("#subscription_review_title").text(getRespectiveMessage("reviewTitle"));
  $("#convert_review_details").find("#selection_label").text(getRespectiveMessage("reviewLabel"));
  $("#convert_review_details").find("#charge_sup_one").text("(" + getRespectiveMessage("chargeType") + ")");
    if($selectedRow.attr("isMonthlyNoRecurring") == "true") {
    $("#convert_review_details").find("#charge_sup_rec").text("");
    } else {
    $("#convert_review_details").find("#charge_sup_rec").text("(" + getRespectiveMessage("chargeType") + ")");
    }
    
  $("#entitlements_review").attr("isExisting", $selectedRow.attr("isExisting"));
  $("#entitlements_review").attr("selectedSub", $selectedRow.find('input').val());
  
  if(entitlementDetailsMap[$selectedRow.find('input').val()] == null) {
    var $loadingHtml = $("#entitlements_loading_spinner").clone();
    var $parentRow = $selectedRow;
    $loadingHtml.find("#bundleName").text($parentRow.attr("bundleName"));
    $loadingHtml.find("#bundleDesc").text($parentRow.attr("bundleDesc"));
    $("#entitlements_review").attr("data-content", $loadingHtml.html());
  }
  
  $("#convert_review_details").find("#subscriptionName").html($selectedRow.find("#subscriptionName").html());
  $("#convert_review_details").find("#subscriptionName").attr("title", $selectedRow.find("#subscriptionName").html());
  $("#convert_review_details").find("#billingFrequency").html($selectedRow.find("#billingFrequency").html());
  $("#convert_review_details").find("#onetimeCharge").html($selectedRow.find("#onetimeCharge").html());
  $("#convert_review_details").find("#recurringCharge").html($selectedRow.find("#recurringCharge").html());
}

var subscriptionId = "";
function nextStep(current) {
  var $currentstep = $(current).parents(".js_stepcontent");
  var nextstep = $currentstep.find("#nextstep").val();
  
  
  if(nextstep == "step2") {
    var $selectedRow = $("#dialog_convert_subscription").find("input[name='conversion_choice']:checked:visible").parents('tr');
    if($selectedRow.length == 0) {
      var bundleChoice = ($("#dialog_convert_subscription").find("input[name='purchaseOrExisting']:checked").attr("id") == "none_purchase_new");
      if(bundleChoice) {
        popUpDialogForAlerts("dialog_info", con_sub_dictionary.chooseBundleToContinue);
        return;
      } else {
        popUpDialogForAlerts("dialog_info", con_sub_dictionary.chooseSubscriptionToContinue);
        return;
      }
    } else {
      prepareReviewAndConfirm($selectedRow);
    }
  }
  
  if(nextstep == "step3") {
    // Finish
    if ($("#tncAccept").is(":checked") == false) {
      popUpDialogForAlerts("dialog_info", g_dictionary.youCanNotContinueUntilYouAcceptTheTermsAndConditions);
      $("#accept_checkbox").focus();
      return;
    }
    finishConversion();
  }
  
  if(nextstep == "step4") {
      var location = "/portal/portal/billing/subscriptions?tenant=" + $("#tenantParam").val();
      if(isNotBlank(subscriptionId)) {
        location += "&id=" + subscriptionId;
      }
      window.location = location;
      $("#dialog_convert_subscription").dialog("close");
  }
  
  $currentstep.hide();
  $("#dialog_convert_subscription").find("#" + nextstep).show();
}
var conSubMessageDictionary = null;
$(document).ready(function() {
  conSubMessageDictionary = {
      bundle : {
        successConfirm : con_sub_dictionary.convertSubscriptionSuccessNew,
        reviewTitle : con_sub_dictionary.reviewTitleNew,
        reviewLabel : con_sub_dictionary.reviewLabelNew,
        chargeType : con_sub_dictionary.newChargeLabel,
        billing_info : con_sub_dictionary.billingNonUtility,
        future_billing_info : con_sub_dictionary.futureBillingRecurring
      },
      subscription : {
        successConfirm : con_sub_dictionary.convertSubscriptionSuccessExisting,
        reviewTitle : con_sub_dictionary.reviewTitleExisting,
        reviewLabel : con_sub_dictionary.reviewLabelExisting,
        chargeType : con_sub_dictionary.existingChargeLabel,
        billing_info : con_sub_dictionary.subscriptionExistingBilling,
        future_billing_info : ""
      }
  };
});


function getRespectiveMessage(key) {
  var $selectedRow = $("#dialog_convert_subscription").find("input[name='conversion_choice']:checked").parents('tr');
  var isExisting = ($selectedRow.attr("isExisting") == "true");
  if(isExisting) {
    return conSubMessageDictionary.subscription[key];
  } else {
    if($selectedRow.attr("isMonthlyNoRecurring") == "true") {
      if(key == "billing_info") {
        return con_sub_dictionary.billingUtility;
      } else if(key == "future_billing_info") {
        return "";
      }
    }
    return conSubMessageDictionary.bundle[key];
  }
}

function finishConversion() {
  var $selectedRow = $("#dialog_convert_subscription").find("input[name='conversion_choice']:checked").parents('tr');
  $.ajax({
    type: "POST",
    url: "/portal/portal/subscription/subscribe_resource",
    async: false,
    cache: false,
    data: {
      tenant : $("#tenantParam").val(),
      productBundleId : $selectedRow.attr("productBundleId"),
      newSubscriptionId : $selectedRow.attr("subscriptionId"),
      subscriptionId : $("#subscriptionId").val(),
      resourceType : $("#resourceTypeName").val(),
      serviceInstaceUuid : $("#serviceInstanceUuid").val()
    },
    dataType: "json",
    success: function(returnVal) {
      if (isNotBlank(returnVal) && isNotBlank(returnVal.subscriptionId)) {
        subscriptionId = returnVal.subscriptionId;
        handleConversionSuccess();
      } else {
        handleConversionError(con_sub_dictionary.errorConvertSubscription);
      }
    },
    error: function(XMLHttpRequest) {
      handleConversionError(XMLHttpRequest.responseText);
    }
    });
}

function handleConversionSuccess() {
  $("#dialog_convert_subscription").find("#prev_last_screen").hide();
  $("#dialog_convert_subscription").find("#result_icon").removeClass("failure").addClass("success");
  $("#dialog_convert_subscription").find("#successmessage").html(getRespectiveMessage("successConfirm"));
  $("#dialog_convert_subscription").find("#billing_info").html(getRespectiveMessage("billing_info"));
  
  if(getRespectiveMessage("future_billing_info") != "") {
    $("#dialog_convert_subscription").find("#future_billing_info").html(getRespectiveMessage("future_billing_info"));
    $("#dialog_convert_subscription").find("#future_billing_info").show();
  } else {
    $("#dialog_convert_subscription").find("#future_billing_info").hide();
  }
  
  $("#dialog_convert_subscription").find("#additional_list").show();
}

function handleConversionError(errorText) {
  $("#dialog_convert_subscription").find("#additional_list").hide();
  $("#dialog_convert_subscription").find("#result_icon").removeClass("success").addClass("failure");
  $("#dialog_convert_subscription").find("#prev_last_screen").show();
  $("#dialog_convert_subscription").find("#successmessage").html(errorText);
}

function prevStep(current) {
  var $currentstep = $(current).parents(".js_stepcontent");
  var prevstep = $currentstep.find("#prevstep").val();
  $currentstep.hide();
  $("#dialog_convert_subscription").find("#" + prevstep).show();
}

function getCloseConfirmationButtons() {
  var buttonCallBacks = {};
  buttonCallBacks[dictionary.lightboxbuttonconfirm] = function () {
    $(this).dialog("close");
    $("#dialog_convert_subscription").dialog("close");
  };buttonCallBacks[dictionary.lightboxbuttoncancel] = function () {
    $(this).dialog("close");
  };
  return buttonCallBacks;
}

  var entitlementDetailsMap = {};
function prepareEntitlementPopovers() {
  entitlementDetailsMap = {};
  $(".js_entitlement_details").popover();
  $(".js_entitlement_details").each(function() {
    var $loadingHtml = $("#entitlements_loading_spinner").clone();
    var $parentRow = $(this).parents("tr");
    $loadingHtml.find("#bundleName").text($parentRow.attr("bundleName"));
    $loadingHtml.find("#bundleDesc").text($parentRow.attr("bundleDesc"));
    $(this).attr("data-content", $loadingHtml.html());
  });
  $(".js_entitlement_details_div").unbind("click").bind("click", function() {
    
    var $detailsLink = $(this).find(".js_entitlement_details");
    var targetId = $detailsLink.parents("tr").find("input").val();
    if(targetId == null) {
      targetId = $detailsLink.attr("selectedSub");
    } else {
      $(".js_entitlement_details").each(function() {
        var thisId = $(this).parents("tr").find("input").val();
        if(thisId != targetId) {
          $(this).popover("hide");
        }
      });
    }
    
    if(entitlementDetailsMap[targetId] != null) {
      var html = entitlementDetailsMap[targetId];
      $(".popover-content:visible").html(html);
      $detailsLink.attr("data-content", html);
      return;
    }
    
    var callData = {};
    if($detailsLink.attr("isExisting") == "true") {
      callData["subscriptionUuid"] = targetId;
    } else {
      callData["productBundleId"] = targetId;
    }
    callData["tenant"] = $("#tenantParam").val();
    
    $.ajax({
      type: "GET",
      url: "/portal/portal/subscription/entitlements_table",
      data : callData,
      async : true,
      cache : true,
      dataType: "html",
      success: function(html) {
        $(".popover-content:visible").html(html);
        entitlementDetailsMap[targetId] = html;
        $detailsLink.attr("data-content", html);
      },
      error: function() {
        
      },
      complete: function(xhr, status) {
        // Just added to prevent it from going to generic handler
      }
    });
  });
}

function bindFunctions() {
  $("#use_existing").unbind("click").bind("click", function() {
    $("#subscriptions_table_div").slideDown();
    $("#bundles_table_div").slideUp();
  });
  $("#none_purchase_new").unbind("click").bind("click", function() {
    $("#subscriptions_table_div").slideUp();
    $("#bundles_table_div").slideDown();
  });
  $("#dialog_convert_subscription").find(".nextbutton, .submitbutton").unbind("click").bind("click", function() {
    nextStep(this);
  });
  $("#dialog_convert_subscription").find(".prevbutton").unbind("click").bind("click", function() {
    prevStep(this);
  });
  $("#dialog_convert_subscription").find(".cancel").unbind("click").bind("click", function() {
    $("#dialog_confirmation").text(con_sub_dictionary.confirmConvertSubscriptionCancel).dialog('option', 'buttons', getCloseConfirmationButtons()).dialog("open");
  });
  $("#dialog_convert_subscription").find("input[name='conversion_choice']").unbind("change").bind("change", function() {
    $("#dialog_convert_subscription").find("tr").addClass("hover_enabled").removeClass("selected");
    $(this).parents("tr").removeClass("hover_enabled").addClass("selected");
  });
  $("#dialog_convert_subscription").find("tr").unbind("click").bind("click", function(e) {
    if($(e.target).hasClass("js_entitlement_details") || $(e.target).hasClass("js_entitlement_details_div")) {
      return;
    }
    $(this).find("input[type='radio']").prop("checked", true).change();
  });
}

function prepareTnC() {
  initDialogWithOK("tncDialog", 750);
  $("#tncLink").click(function(e) {
    e.preventDefault();
    dialogButtonsLocalizer($("#tncDialog"), {
      'OK': g_dictionary.dialogOK
    });
    $("#tncDialog").dialog("open");
  });
}

function prepareUtilityLinks() {
  $(".utility_rate_link").bind("click", function () {    
    viewUtilitRates($("#tenantParam").val(), "utilityrates_lightbox", null, $("#serviceInstanceUuid").val());
    $("#utilityrates_lightbox").dialog("option", {
      height: 100,
      width: 650
    });
  });
}

function initConvertSubscription() {
  bindFunctions();
  prepareEntitlementPopovers();
  prepareTnC();
  prepareUtilityLinks();
}
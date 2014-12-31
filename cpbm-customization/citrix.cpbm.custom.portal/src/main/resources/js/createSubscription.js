/*
*  Copyright � 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/

var currentBundleTemplate = null;
var isPayAsYouGoChosen = false;
var lastRCFilterSelection = "";

var pageNumber = 0;
var pageLimit = 10;
var pageNumberForActive = 0;
var pageLimitForActive = 10;
var enableMore = 0;
var enableMoreForActive = 0;
var moreClick = 0;
var moreClickForActive = 0;
var newSubsBundlesList = null;
var pricingFiltersCountUpToDate = false;
var activeSubsBundlesList = null;
var anyOptionStaticValue = "__ANY__";


$(document).ready(function() {
  try{
    initCreateSubscription();
  }catch(Error){
  }
});

function initCreateSubscription() {

  isPayAsYouGoChosen = ($("#isPayAsYouGoChosen").val() == "true");

  // Build category and instance bar
  prepareSelectedCategory();

  if (isNotBlank($("#cloudServiceException").val())) {
    return;
  }

  initDialogWithOK("tncDialog", 750);
  $("#tncLink").click(function(e) {
    e.preventDefault();
    dialogButtonsLocalizer($("#tncDialog"), {
      'OK': g_dictionary.dialogOK
    });
    $("#tncDialog").dialog("open");
  });

  var newSubscriptionId = null;
  var customEditorPage = $("#customEditorTag").val();
  var customComponentSelector = $("#customComponentSelector").val();
  var resourceTypeSelection = $("#resourceType").val();
  var tenantParam = $("#tenantParam").val();
  var serviceInstaceUuid = $("#serviceInstanceUuid").val();
  var pricingFilter = 'ALL';

  var subscriptionId = $("#subscriptionId").val();
  var isReconfigure = isNotBlank($("#isReconfigure").val());
  var isReprovision = isNotBlank(subscriptionId) && !isReconfigure;

  if(isReconfigure) {
    // If hit the reconfigure case, redirect to the subscriptions page.
    window.location = "/portal/portal/billing/subscriptions?tenant=" + tenantParam + "&id=" + $("#subscriptionUuid").val();
  }
  
  var selectedSubscriptionId = null;
  
  // This is the id used to keep track of the selected tab out of 'New Subscriptions' or 'Unprovisioned subscriptions'
  var selectedBundleTab = isNotBlank(subscriptionId) ? "view_subscriptions_tab" : "view_bundles_tab";

  var $bundleContainer = $("#bundle_container");
  var $bundleTemplate = $bundleContainer.find("#bundle_template");

  // 
  var bundleObj_id = null;

  // In step2 we show entitled products for current bundle and products likely to generate for current selection. Following map prevents 
  var productIdsWithEntitlements = {};

  var currentPage = 1;
  var SERVICE_RESOURCE_TYPE = "__SERVICE__";

  // These are ready checks for step1 and step2 respectively. Used to prevent list bundles and other duplicate calls while the page is in transition state
  var isStep1Ready = false;
  var isStep2Ready = false;

  //Obselete. Reset this.
  var selectedResourceComponentsForStep2 = uniqueResourceComponents;

  //PayG change resource type
  $(".js_resourceType").unbind("change").bind("change", function() {
    var target = $(this);
    if (target.attr('disabled') == true || target.attr('disabled') == 'disabled') {
      return;
    }
    changeResourceType($(this).val());
  });

  //Subscribe bundle change resource type
  $(".js_resource_type_default").unbind("click").bind("click", function() {
    var target = $(this);
    if (target.attr('disabled') == true || target.attr('disabled') == 'disabled') {
      return;
    }
    changeResourceType($(this).attr('id'));
  });

  //Pagination show more handler
  $("#show_more").unbind("click").bind("click", function() {
    getMoreBundles();
  });

  //Window scroll of right panel first and then left panel in subscribe bundle page 1
  $(window).scroll(function() {
    var pricing_div = $('#pricing');
    var sliding_wrapper = $('.sliding_statswrapper');
    var section2 = $('#SECTION_2');
    var section3 = $('#SECTION_3');
    var section4 = $('#SECTION_4');
    var pricingFilters = $("#pricing");
    var section2Top = section2.top;
    var footerTop = $("#bundle_container").top + $("#bundle_container").height();
    $("#bundle_container").css('min-height', section2.height() - pricingFilters.height());
    var footer = $('#footer');
    if (footer.offset() != null) {
      footerTop = footer.offset().top;
    }
    if (currentPage == 1) {
      section2.css({
        'position': 'fixed',
        'top': '5px'
      });
      var stopHeight = pricing_div.offset().top;
      var section2Foot = section2.offset().top + section2.height();
      if (section2Foot > footerTop - 10) {
        section2.css({
          position: 'absolute',
          top: (footerTop - 10) - section2.height()
        });
      } else {
        if (stopHeight > section2.offset().top) {
          section2.css({
            position: 'absolute',
            top: stopHeight
          });
        }
      }
    } else if (currentPage == 2) {
      /*
      var stopHeight = sliding_wrapper.offset().top + sliding_wrapper.height();
      var section3Foot = section3.offset().top + section3.height();
      var section4Foot = section4.offset().top + section4.height();
      var sticker = (section3Foot<section4Foot)? section3 : section4;
      var stickerFoot = (section3Foot<section4Foot)? section3Foot : section4Foot;
      sticker.css({'position':'fixed','top':'5px'});
      if(stickerFoot > footerTop -10){
        sticker.css({position:'absolute',top: (footerTop - 10) - sticker.height()});
      } else {
        if ( stopHeight > sticker.offset().top) {
          sticker.css({position:'absolute',top: stopHeight});
        }
      }
    */
    }
  });

  $("#back_to_catalog").unbind("click").bind("click", function() {
    $("#top_message_panel").hide();
    if (isReprovision) {
      window.location = "/portal/portal/billing/subscriptions?tenant=" + tenantParam;
      return;
    }

    $("#spinning_wheel").show();
    currentPage = 1;
    isStep1Ready = false;
    isStep2Ready = false;
    currentBundleTemplate = null;
    $("#SECTION_3").hide();
    //hide left panel in step 2
    $("#filterSectionProvisionPage").hide();
    $("#configure_subscribe").hide();
    $("#componentsSectionProvisionPage").hide();

    // Hide right panel in step 2
    $("#SECTION_4").hide();
    $("#componentsHeaderProvisionPage").hide();

    // Showing custom component if hidden due RCs fullfillment in step 1 to step 2
    $("#customComponentSelectorContent").show();

    if (resourceTypeSelection != SERVICE_RESOURCE_TYPE && !isPayAsYouGoChosen) {
      var returnValue = loadFiltersAndRCs();
      if (returnValue == false) {
        $("#spinning_wheel").hide();
        return false;
      }
    }

    //
    $("#bundle_with_selection_summary_div").show();
    $("#bundle_container").show();
    $("#pricing").show();
    if (isPayAsYouGoChosen) {
      resetInputFields();
      showPAYGTab();
    } else {
      $("#SECTION_3").removeClass("pull-left").addClass("pull-right");
      $("#SECTION_3").show();
      $("#SECTION_2").show();
    }
    $("#spinning_wheel").hide();
  });


  for (var i = 0; i < uniqueResourceComponents.length; i++) {
    $("input[name='" + uniqueResourceComponents[i] + "']").unbind("change").bind("change", function() {
      if ((currentPage == 1 && isStep1Ready) || (currentPage == 2 && isStep2Ready)) {
        refreshBundlesOrSelectionSummary();
      }
    });
  }

  for (var i = 0; i < resourceProperties.length; i++) {
    $("input[name='" + resourceProperties[i] + "']").unbind("change").bind("change", function() {
      if (currentPage == 2 && isStep2Ready) {
        refreshYourSelectionSummary();
      }
    });
  }

  $('input[name^="prop_"]').unbind("change").bind("change", function(event) {
    updateProperty(event.target);
  });

  $(".js_bundle_subscriptions_tabs").unbind("click").bind("click", function() {
    resetPricingFilterReccurrenceCount();
    selectedBundleTab = $(this).attr("id");
    refreshBundlesListingForSelectedTab();
  });

  function refreshBundlesListingForSelectedTab() {
    if (isNotBlank(subscriptionId)) {
      $("#bundle_subscriptions_tabs").show(100);
    }
    $selectedBundleTab = $("#" + selectedBundleTab);
    if ($selectedBundleTab.length > 0) {
      $(".js_bundle_subscriptions_tabs").removeClass('active');
      $(".js_bundle_subscriptions_tabs").addClass('nonactive');
      $selectedBundleTab.removeClass('nonactive');
      $selectedBundleTab.addClass('active');
      pageNumber = 0;
      enableMore = 0;
      pageNumberForActive = 0;
      enableMoreForActive = 0;
    }
    $bundleContainer.empty();
    $("#no_screen_wrapper_div").hide();
    if (selectedBundleTab == 'view_subscriptions_tab') {
      moreClick = 0;
      moreClickForActive = 1;
      populateActiveSubscriptions();
    } else {
      moreClick = 1;
      moreClickForActive = 0;
      populateBundles();
    }
    $bundleContainer.show();
  }

  $(".js_pay_as_you_go_action").unbind("click").bind("click", function() {
    setAndSubscribeSelectedResourceComponentsForStep2($(this).parents(".btn-group"), uniqueResourceComponents);
  });

  $(".utility_rates_link").unbind("click").bind("click", function() {
    viewUtilityPricing();
  });

  $("#launchResource").unbind("click").bind("click", function() {
    launchVM(true);
  });

  $("#currency_selector").bind('mouseover', function() {
    $("#catalog_currencybox_dropdown").show();
  });

  $("#currency_selector").bind('mouseout', function() {
    $("#catalog_currencybox_dropdown").hide();
  });

  $(".currencyLi").unbind('click').bind('click', function() {
    $("#spinning_wheel2").show();
    $("#selectedcurrencytext").html($(this).attr('id'));
    $("#selectedcurrencyflag").html('<img src="/portal/images/flags/' + $(this).attr('id') + '.gif"/>');
    $("#catalog_currencybox_dropdown").hide();
    $("#selectedCurrencySign").val($(this).attr('sign'));
    if (currentPage == 1 && isPayAsYouGoChosen) {
      populateUtilityRatesTable(tenantParam, serviceInstaceUuid);
    } else {
      refreshBundlesOrSelectionSummary();
    }
    $("#spinning_wheel2").hide();
  });

  loadHook();

  function loadHook() {
    $("#spinning_wheel").show();

    // Since the first load of the page set the current step to 1
    currentPage = 1;

    // Prefill the hidden inputs generated for properties if user has come for reprovision or reconfigure
    if (isNotBlank(subscriptionId)) {
      var subscriptionConfJsonStr = $("#subscriptionConfJson").val();
      var subscriptionConfJsonObj = jQuery.parseJSON(subscriptionConfJsonStr);

      for (var i = 0; i < allConfigurationProperties.length; i++) {
        var value = subscriptionConfJsonObj[allConfigurationProperties[i]];
        if (isNotBlank(value)) {
          $("input[name='" + allConfigurationProperties[i] + "']").val(value);
          var name = subscriptionConfJsonObj[allConfigurationProperties[i] + "_name"];
          if(isNotBlank(name)) {
            $("input[name='" + allConfigurationProperties[i] + "']").data('valueDisplayName', name);
          }
        }
      }
    }

    // If pay as you go mode enabled, then show utility table, otherwise show bundles listing
    if (isPayAsYouGoChosen) {
      showPAYGTab();
      $("#spinning_wheel").hide();
    } else {
      showBundlesViewTab();
      loadFiltersAndRCs();
    }
  }

  function showPAYGTab() {
    $("#SECTION_1").show();
    $("#SECTION_2").hide();
    $("#SECTION_3").hide();
    $("#SECTION_4").hide();
    populateUtilityRatesTable($("#tenantParam").val(), $("#serviceInstanceUuid").val());
    $("#effective_date_box").append(effective_date_str);
    if(isBlank(effective_date_str)) {
      $("#effective_date_box").append(dictionary.eff_date_not_yet_set);  
    }
    $("#pay_as_you_go_action_container_2").show();
  }

  function showBundlesViewTab() {
    $("#spinning_wheel2").show();
    $("#pay_as_you_go_action_container_2").hide();
    $("#SECTION_1").hide();
    $("#SECTION_2").show();
    $("#SECTION_3").show();
    $("#SECTION_4").hide();
    $("#spinning_wheel2").hide();
  }

  function refreshYourSelectionSummary() {
    if (typeof(bundleObj) == "undefined" && !isPayAsYouGoChosen) {
      return;
    }

    $("#RHS_your_selection_contentArea").html('');

    if (!isPayAsYouGoChosen) {
      var html = $("#RHS_your_selection").clone();
      html.find("#RHS_your_selection_header").text(dictionary.label_Bundle);
      html.find("#RHS_your_selection_value").text(bundleObj.name);
      $("#RHS_your_selection_contentArea").append(html.show());
    }

    for (var i = 0; i < allConfigurationProperties.length; i++) {
      var $inputField = $("input[name='" + allConfigurationProperties[i] + "']");
      var fieldDisplayName = $inputField.data('fieldDisplayName');
      var valueDisplayName = $inputField.data('valueDisplayName');
      var fieldValue = $inputField.val();
      if (isNotBlank(fieldValue) && isBlank(valueDisplayName)) {
        valueDisplayName = fieldValue;
      }
      if (isBlank(fieldDisplayName)) {
        fieldDisplayName = l10dict[allConfigurationProperties[i] + "-name"];
      }
      if (isNotBlank(fieldValue)) {
        var html = $("#RHS_your_selection").clone();
        html.find("#RHS_your_selection_header").text(fieldDisplayName);
        html.find("#RHS_your_selection_value").text(valueDisplayName);
        $("#RHS_your_selection_contentArea").append(html.show());
      }
    }
  }

  function populateEntitlements() {
    var bundleRevision = $(currentBundleTemplate).data("bundleRevisionObj");
    $("#msg_RHS_entitlements").hide();
    if (typeof(bundleRevision) == "undefined" || bundleRevision == null || isPayAsYouGoChosen) {
      return;
    }

    var productRatesMap = {};
    $.ajax({
      type: "GET",
      async: true,
      cache: true,
      url: "/portal/portal/products/listProductsForSelectedContext",
      data: {
        serviceInstanceUuid: $("#serviceInstanceUuid").val(),
        resourceType: resourceTypeSelection,
        contextString: "",
        filters: "",
        listAll: "true",
        currencyCode: $("#selectedcurrencytext").text(),
        tenant: tenantParam
      },
      dataType: "json",
      success: function(productCharges) {
        var $listBox = $("#included_usage").empty();
        if (productCharges != undefined && productCharges != null && productCharges.length > 0) {
          for (var i = 0; i < productCharges.length; i++) {
            var productCharge = productCharges[i];
            productRatesMap[productCharge.product.id] = productCharge;
          }
        }
        var bundle = bundleRevision.productBundle;
        var entitlements = bundleRevision.entitlements;
        if (entitlements != null && entitlements.length > 0) {
          $("#msg_RHS_entitlements").show();
          for (var a = 0; a < entitlements.length; a++) {
            var entitlement = entitlements[a];
            productIdsWithEntitlements[entitlement.product.id] = "true";
            var usage = entitlement.includedUnits;
            var entitlementText = "<strong>" + entitlement.product.name + "</strong><br/>"
            if (entitlement.includedUnits == -1) {
              entitlementText += dictionary.unlimited + " ";
            } else {
              entitlementText += dictionary.msg_no_extra_charge_upto + " " + usage;
              entitlementText += " " + i18nUomText(entitlement.product.uom);
            }
            var productCharge = productRatesMap[entitlement.product.id];
            var chargeText = "";
            if (entitlement.includedUnits == -1) {
              chargeText += dictionary.msg_overages_charges + " ";
              chargeText += dictionary.label_Not_Applicable;
            } else {
              chargeText += dictionary.msg_overages_charged_at + " ";
              chargeText += $('#selectedCurrencySign').val() + formatNumber(roundNumber((productCharge.price), $(
                "#minFractionDigits").val()));
              chargeText += " / " + i18nUomText(entitlement.product.uom);
            }
            entitlementText += "<br />" + chargeText;
            $listBox.append("<li>" + entitlementText + "</li>");
          }
        }
      }

    });
  }

  function populateEntitlementsTable(bundleRevision, table_container_id) {

    if (typeof(bundleRevision) == "undefined" || bundleRevision == null || isPayAsYouGoChosen) {
      return;
    }

    var productRatesMap = {};
    var $table_body = $("#" + table_container_id).find('#totalentitlments');

    $.ajax({
      type: "GET",
      async: true,
      cache: false,
      url: appendChannelAndRevision("/portal/portal/products/listProductsForSelectedContext"),
      data: {
        serviceInstanceUuid: $("#serviceInstanceUuid").val(),
        resourceType: resourceTypeSelection,
        contextString: "",
        filters: "",
        listAll: "true",
        currencyCode: $("#selectedcurrencytext").text(),
        tenant: tenantParam
      },
      dataType: "json",
      success: function(productCharges) {
        $table_body.empty();
        if (productCharges != undefined && productCharges != null && productCharges.length > 0) {
          for (var i = 0; i < productCharges.length; i++) {
            var productCharge = productCharges[i];
            productRatesMap[productCharge.product.id] = productCharge;
          }
        }
        var bundle = bundleRevision.productBundle;
        var entitlements = bundleRevision.entitlements;
        if (entitlements != null && entitlements.length > 0) {

          for (var a = 0; a < entitlements.length; a++) {
            var entitlement = entitlements[a];
            productIdsWithEntitlements[entitlement.product.id] = "true";
            var usage = "";
            if (entitlement.includedUnits == -1) {
              usage = dictionary.unlimited;
            } else {
              usage = entitlement.includedUnits + " " + i18nUomText(entitlement.product.uom);
            }
            var entitlementText = "<td style='width:170px;'>" + usage + "</td>";

            entitlementText += "<td style='width:231px;'>" + entitlement.product.name + "</td>";

            var productCharge = productRatesMap[entitlement.product.id];
            var chargeText = "";
            if (entitlement.includedUnits == -1) {
              chargeText += dictionary.label_Not_Applicable;
            } else {
              chargeText += $('#selectedCurrencySign').val() + formatNumber(roundNumber((productCharge.price), $(
                "#minFractionDigits").val()));
              chargeText += " / " + i18nUomText(entitlement.product.uom);
            }
            entitlementText += "<td style='width:230px;'>" + chargeText + "</td>";
            $table_body.append("<tr class='hover_enabled'>" + entitlementText + "</tr>");
          }
        } else {
          var noEntitlementText = "<td style='width:656px;'><p class='alert alert-info empty_body' >" +
            dictionary.label_no_entitlements + "</p></td>";
          $table_body.append("<tr>" + noEntitlementText + "</tr>");
        }
      },
      error: function(XMLHttpResponse) {
        //TODO: handle error
      }

    });
  }

  function populateProductsForGeneratedUsage() {
    var returnValues;
    $("#msg_RHS_generated_usage").hide();
    $.ajax({
      type: "GET",
      async: true,
      cache: true,
      url: "/portal/portal/products/listProductsForSelectedContext",
      data: {
        serviceInstanceUuid: $("#serviceInstanceUuid").val(),
        resourceType: resourceTypeSelection,
        contextString: getContextStringOverrideEffective(),
        filters: getSelectedFilterString(),
        currencyCode: $("#selectedcurrencytext").text(),
        tenant: tenantParam
      },
      dataType: "json",
      success: function(productCharges) {

        var $listBox = $("#generated_products_div").empty();
        if (productCharges != undefined && productCharges != null && productCharges.length > 0) {
          for (var i = 0; i < productCharges.length; i++) {
            var productCharge = productCharges[i];
            if (typeof(productIdsWithEntitlements[productCharge.product.id]) != "undefined" &&
              productIdsWithEntitlements[productCharge.product.id] == "true") {
              continue;
            }
            $("#msg_RHS_generated_usage").show();
            var chargeText = "";
            chargeText += dictionary.label_charged_at + " ";
            chargeText += $('#selectedCurrencySign').val() + formatNumber(roundNumber((productCharge.price), $(
              "#minFractionDigits").val()));
            chargeText += " / " + i18nUomText(productCharge.product.uom);
            $listBox.append("<li><strong>" + productCharge.product.name + "</strong><br />" + chargeText + "</li>");
          }
        }
      }
    });
  }

  function subscribePAYG() {
    $("#SECTION_1").hide();
    $("#no_screen_wrapper_div").hide();
    $("#msg_RHS_entitlements").hide();
    $("#pricing").hide();
    $("#SECTION_2").hide();
    $("#one_time_charges_content_area").hide();
    $("#recurring_charges_content_area").hide();
    $("#subtotal_content_area").hide();
    $("#total_content_area").hide();
    $("#tax_content_area").hide();
    $("#bundle_with_selection_summary_div").hide();
    productIdsWithEntitlements = {};

    currentPage = 2;

    populateRCsAndFiltersStep2();
  }

  function prepareStep2PricingAndEntitlements() {
    if (isNotBlank(customEditorPage)) {
      initializeEditor();
    }
    isStep2Ready = true;

    refreshYourSelectionSummary();
    populateProductsForGeneratedUsage();
    if (!isPayAsYouGoChosen) {
      var selected_bundle_ribbon_class = $(currentBundleTemplate).find("#entitlmentsribbon").attr('class');
      $('#bundle_entitlements_ribbon').removeClass().addClass(selected_bundle_ribbon_class);
      $('#bundle_entitlements_ribbon').html("");
      $('#bundle_entitlements_ribbon').html($(currentBundleTemplate).find("#entitlmentsribbon").html());

      $("#subtotal_content_area").show();
      $("#total_content_area").show();
      $("#tax_content_area").show();
      $("#one_time_charges_content_area").show();
      $("#recurring_charges_content_area").show();

      newSubscriptionId = selectedSubscriptionId;
      if (isReconfigure) {
        for (var i = 0; i < resourceProperties.length; i++) {
          $("input[name='prop_" + resourceProperties[i] + "']").prop('disabled', true);
        }
      }

      if (resourceTypeSelection === SERVICE_RESOURCE_TYPE) {
        $("#msg_RHS_generated_usage").hide();
        $("#servicebundle-info-description").text(bundleObj.name);
        if (bundleObj.description) {
          $("#servicebundle-info-description").append(" : " + bundleObj.description);
        }
      }

      update_price_summary();

      if (isReconfigure) {
        $("#componentsSectionProvisionPage").find('input:radio').each(function() {
          var componentId = $(this).data('componentId');
          if (isBlank(componentId) || reconfigurableMap[componentId] == "false" || reconfigurableMap[componentId] ==
            undefined) {
            $(this).prop('disabled', true);
          }
        });
        $("#filterSectionProvisionPage").find('input:radio').each(function() {
          $(this).prop('disabled', true);
        });
      }
    }

    $("#configure_subscribe").show();
    $("#SECTION_3").removeClass("pull-right").addClass("pull-left");
    $("#SECTION_3").show();
    $("#SECTION_4").show();
    $("#componentsSectionProvisionPage").show();
    $("#filterSectionProvisionPage").show();
    $("#spinning_wheel").hide();
  }

  function setAndSubscribeSelectedResourceComponentsForStep2(current, resourceComponents) {
    $("#accept_checkbox").prop("checked", false);
    selectedResourceComponentsForStep2 = resourceComponents;
    if (isPayAsYouGoChosen) {
      subscribePAYG();
    } else {
      currentBundleTemplate = current;
      actionConfigureAndSubscribe();
    }
  }

  function actionConfigureAndSubscribe() {
    currentPage = 2;
    $("#spinning_wheel").show();
    $("#SECTION_1").hide();
    $("#SECTION_2").hide();
    $("#SECTION_3").hide();
    $("#SECTION_2").css({
      top: '289px'
    });
    $("#bundle_with_selection_summary_div").hide();
    $("#pricing").hide();
    $("#filterSectionProvisionPage").hide();

    productIdsWithEntitlements = {};
    populateEntitlements();

    if (resourceTypeSelection != SERVICE_RESOURCE_TYPE) {
      //Reset those resource components which do not belong to the selected group
      var nonChosenResourceComponents = arr_diff(uniqueResourceComponents, selectedResourceComponentsForStep2);
      for (var i = 0; i < nonChosenResourceComponents.length; i++) {
        resetInputFieldByName(nonChosenResourceComponents[i]);
      }
    }

    var bundleRevisionObj = $(currentBundleTemplate).data("bundleRevisionObj");
    bundleObj = bundleRevisionObj.productBundle;
    selectedSubscriptionId = $(currentBundleTemplate).data("subscriptionId");
    bundleObj_id = bundleRevisionObj.pbid;
    populateRCsAndFiltersStep2();
  }

  function viewUtilityPricing() {
    var utility_url = appendChannelAndRevision("/portal/portal/subscription/utilityrates_table");
    utility_url += "&tenant=" + tenantParam + "&currencyCode=" + $("#selectedcurrencytext").text();
    if (serviceInstaceUuid != null) {
      var filterString = getSelectedFilterString();
      var contextString = getContextString();
      utility_url = utility_url + "&serviceInstanceUuid=" + serviceInstaceUuid + "&contextString=" + contextString +
        "&filters=" + filterString;
    }
    viewUtilitRates(tenantParam, "utilityrates_lightbox", utility_url);
  }

  function update_price_summary() {

    var bundle_activation_charges = parseFloat($(currentBundleTemplate).data("activationCharges"));
    var bundle_recurring_charges = parseFloat($(currentBundleTemplate).data("recurringCharges"));

    var bundle_price_before_tax = bundle_recurring_charges + bundle_activation_charges;
    var subscription_total_amount_before_tax = parseFloat(bundle_price_before_tax);
    var num = new Number(subscription_total_amount_before_tax);
    var tax_amount = Number(getTaxableAmount(num));
    var chargeWithTax = num + tax_amount;
    var subscription_total_amount_after_tax = roundNumber(chargeWithTax, $("#minFractionDigits").val());
    var new_sub_total = (subscription_total_amount_before_tax);

    $('#pricing_reccurence_frequency').text($(currentBundleTemplate).data("pricingFilterDisplay"));
    if ($(currentBundleTemplate).data("bundleRevisionObj").productBundle.rateCard.chargeType.name == "NONE") {
      $("#recurring_charges_content_area").hide();
    } else {
      $("#recurring_charges_content_area").show();
    }

    $('#one_time_charges').text($('#selectedCurrencySign').val() + formatNumber(roundNumber((bundle_activation_charges),
      $("#minFractionDigits").val())));
    $('#recurring_charges').text($('#selectedCurrencySign').val() + formatNumber(roundNumber((bundle_recurring_charges),
      $("#minFractionDigits").val())));
    $('#sub_total_amount').text($('#selectedCurrencySign').val() + formatNumber(roundNumber(new_sub_total, $(
      "#minFractionDigits").val())));
    $('#subscription_total_amount').text($('#selectedCurrencySign').val() + formatNumber(roundNumber((
      subscription_total_amount_after_tax), $("#minFractionDigits").val())));
    $('#subscription_tax_amount').text($('#selectedCurrencySign').val() + formatNumber(roundNumber((tax_amount), $(
      "#minFractionDigits").val())));
    $('#confirm_activation_charges').find('#price_before_tax').text($('#selectedCurrencySign').val() + formatNumber(
      roundNumber((bundle_activation_charges), $("#minFractionDigits").val())));
    $('#confirm_selected_bundle').find('#price_before_tax').text($('#selectedCurrencySign').val() + formatNumber(
      roundNumber((bundle_recurring_charges), $("#minFractionDigits").val())));
  }

  $("#pricing_filters").find('a').unbind('click').bind('click', function() {

    if ($(this).attr('disabled') == 'disabled' || $(this).attr('disabled') == true) {
      return;
    }

    $("#spinning_wheel").show();
    pageNumber = 0;
    enableMore = 0;
    pageNumberForActive = 0;
    enableMoreForActive = 0;
    pricingFilter = $(this).attr('id')
    $("#pricing_filters").find('li').each(function() {
      $(this).removeClass("nonactive");
      $(this).removeClass("active");
    });
    $(this).parent('li').addClass("active");
    refreshBundlesListingForSelectedTab();
    $("#spinning_wheel").hide();
  });

  function getActionUrlForListBundle() {
    var actionUrl = "/portal/portal/productBundles/list.json?&resourceType=" + $("#resourceType").val();
    if ($("#viewChannelCatalog").val() == 'true') {
      var channelId = $("#channelId").val();
      var revision = $("#revision").val();
      var dateFormat = $("#dateFormat").val();
      var revisionDate = $("#revisionDate").val();
      var currencyCode = $("#selectedcurrencytext").html();
      actionUrl = actionUrl + "&viewCatalog=true&revision=" + revision + "&channelParam=" + channelId +
        "&currencyCode=" + currencyCode + "&revisionDate=" + revisionDate + "&dateFormat=" + dateFormat
    } else if ($("#anonymousBrowsing").val() == 'true') {
      var currencyCode = $("#selectedcurrencytext").html();
      var channelId = $("#channelId").val();
      actionUrl = actionUrl + "&currencyCode=" + currencyCode + "&channelParam=" + channelId;
    }
    return actionUrl;
  }

  function populateCurrentSubscription() {
    var currentListed = false;
    if ($("#subscriptionId").length > 0) {
      var subscriptionBundleInfo = null;
      var subscriptionBundle = $.ajax({
        type: "POST",
        url: "/portal/portal/productBundles/getBundleBySubscription.json",
        async: false,
        data: {
          tenant: tenantParam,
          serviceInstaceUuid: serviceInstaceUuid,
          resourceType: resourceTypeSelection,
          filters: getSelectedFilterString(),
          context: getContextStringOverrideEffective(),
          subscriptionId: $("#subscriptionId").val()
        },
        dataType: "json",
        success: function(subscriptionBundleInfo) {
          if (subscriptionBundleInfo != null) {
            var bundleRevision = subscriptionBundleInfo.bundleRevision;
            if (!isValidForCurrentPricingFilter(bundleRevision)) {
              return currentListed;
            }
            visibleSubscription = true;
            var $newBundle;
            if (subscriptionBundleInfo.isCompatible || isReprovision) {
              $newBundle = buildBundleRow(bundleRevision, l10dict['Provision'], null, subscriptionBundleInfo.subscriptionId, true);
            } else {
              $newBundle = buildBundleRow(bundleRevision, l10dict['Incompatible'], "incompatible", subscriptionBundleInfo.subscriptionId, false);
              $newBundle.find(".btn").removeClass("btn btn-info").addClass("incompatible");
            }
            if (isReconfigure) {
              $newBundle.find(".subscribebutton").text(dictionary.label_Reconfigure);
            }
            $bundleContainer.append($newBundle.show());
            currentListed = true;
          }
        }
      });
    }
    return currentListed;
  }

  function populateActiveSubscriptions() {
    var currentListed = populateCurrentSubscription();
    var visibleSubscriptions = 0;
    if (activeSubsBundlesList == null) {
      $.ajax({
        type: "POST",
        url: "/portal/portal/productBundles/listValidSubscriptions.json",
        async: false,
        data: {
          tenant: tenantParam,
          serviceInstaceUuid: serviceInstaceUuid,
          resourceType: resourceTypeSelection,
          filters: getSelectedFilterString(),
          context: getContextStringOverrideEffective()
        },
        dataType: "json",
        success: function(bundleRevisions) {
          if (!$.isEmptyObject(bundleRevisions)) {
            activeSubsBundlesList = bundleRevisions;
          }
        }
      });
    }
    var filteredBundles = getActiveBundlesForCurrentPricingFilter();
    paginateActiveSubsBundle(filteredBundles);
    if (filteredBundles.length == 0 && !currentListed) {
      $("#no_screen_wrapper_div").find("#msg_no_subscriptions").show();
      $("#no_screen_wrapper_div").find("#msg_no_product_bundles").hide();
      $("#no_screen_wrapper_div").show();
      $("#infinite_scrollbarbox_div").hide();
    } else {
      if (enableMoreForActive == 1) {
        $("#infinite_scrollbarbox_div").show();
      } else {
        $("#infinite_scrollbarbox_div").hide();
      }
    }
  }

  function getActiveBundlesForCurrentPricingFilter() {
    var count = 0;
    var activeFilteredBundles = new Array();
    var subscriptionId = null;
    if (activeSubsBundlesList != null) {
      for (subscriptionId in activeSubsBundlesList) {
        updatePricingFilterReccurrenceCount(activeSubsBundlesList[subscriptionId]);
        if (!isValidForCurrentPricingFilter(activeSubsBundlesList[subscriptionId])) {
          continue;
        }
        activeFilteredBundles[count] = subscriptionId;
        count = count + 1;
      }
    }
    enableDisablePricingFilters();
    if (activeFilteredBundles.length != 0 && (activeFilteredBundles.length - ((pageNumberForActive + 1) *
      pageLimitForActive) > 0)) {
      enableMoreForActive = 1;
    } else {
      enableMoreForActive = 0;
    }
    return activeFilteredBundles;
  }

  function paginateActiveSubsBundle(bundleRevisions, currentListed) {
    var start = pageNumberForActive * pageLimitForActive;
    var limit = (start + pageLimitForActive) < bundleRevisions.length ? (start + pageLimitForActive) : bundleRevisions.length
    var subscriptionId = null;
    for (var index = start; index < limit; index++) {
      if (currentListed) {
        $bundleContainer.append("<div class='catalog_current_header'>" + l10dict['subscriptionBundles'] + "</div>");
      }
      subscriptionId = bundleRevisions[index];
      var buttonLabel = l10dict['Provision'];
      if (isReconfigure) {
        buttonLabel = dictionary.label_configure;
      }

      var $newBundle = buildBundleRow(activeSubsBundlesList[subscriptionId], buttonLabel, null, subscriptionId, true);
      $bundleContainer.append($newBundle.show());
    }
  }

  function populateBundles() {
    if (newSubsBundlesList == null) { //  'newSubsBundlesList' is the global variable to cache the bundles list

      // Gets the count of matching unprovisioned subscriptions to decide whether to show the 'Matching unprovisioned subscriptions' tab
      if (isBlank(subscriptionId) && $("#anonymousBrowsing").val() != 'true' && resourceTypeSelection !=
        SERVICE_RESOURCE_TYPE && $("#viewChannelCatalog").val() != 'true') {
        $.ajax({
          type: "POST",
          url: "/portal/portal/productBundles/getValidSubscriptionsCount.json",
          async: true,
          data: {
            tenant:tenantParam,
            serviceInstaceUuid : serviceInstaceUuid,
            resourceType : resourceTypeSelection,
            filters : getSelectedFilterString(),
            context : getContextStringOverrideEffective()
          },
          dataType: "json",
          success: function(bundlesCount) {
            if (bundlesCount > 0) {
              $("#bundle_subscriptions_tabs").show(100);
            } else {
              //Ideally this code block should never be reached
              $("#bundle_subscriptions_tabs").hide();
            }
          }
        });
      }

      $.ajax({
        type: "POST",
        url: getActionUrlForListBundle(),
        async: false,
        data: {
          tenant: tenantParam,
          serviceInstaceUuid: serviceInstaceUuid,
          filters: getSelectedFilterString(),
          context: getContextStringOverrideEffective()
        },
        dataType: "json",
        success: function(bundleRevisions) {
          if (bundleRevisions != null && bundleRevisions.length > 0) {
            newSubsBundlesList = bundleRevisions;
          }
        },
        error: function() {
          //TODO
        }
      });
    }
    var filteredBundles = getBundlesForCurrentPricingFilter(newSubsBundlesList);
    paginateBundlesList(filteredBundles);
    if (filteredBundles.length == 0) {
      $("#no_screen_wrapper_div").find("#msg_no_subscriptions").hide();
      $("#no_screen_wrapper_div").find("#msg_no_product_bundles").show();
      $("#no_screen_wrapper_div").show();
      $("#infinite_scrollbarbox_div").hide();
    } else {
      if (enableMore == 1) {
        $("#infinite_scrollbarbox_div").show();
      } else {
        $("#infinite_scrollbarbox_div").hide();
      }
    }
  }

  function enableDisablePricingFilters() {
    pricingFiltersCountUpToDate = true;
    for (var data in pricingReccurenceFrequencyList) {
      var $filterField = $("#" + data);
      if (pricingReccurenceFrequencyList[data] == 0) {
        $filterField.attr("disabled", true);
        $filterField.addClass("is_disabled");
      } else {
        $filterField.attr("disabled", false);
        $filterField.removeClass("is_disabled");
      }
      $filterField.find(".js_filter_count").text(pricingReccurenceFrequencyList[data]);
    }
  }

  function resetPricingFilterReccurrenceCount() {
    for (var data in pricingReccurenceFrequencyList) {
      pricingReccurenceFrequencyList[data] = 0;
    }
    pricingFiltersCountUpToDate = false;
  }

  function updatePricingFilterReccurrenceCount(bundleRevision) {
    if (!pricingFiltersCountUpToDate) {
      pricingReccurenceFrequencyList[bundleRevision.productBundle.rateCard.chargeType.name] += 1;
      if (bundleRevision.productBundle.rateCard.chargeType.name == "NONE") {
        pricingReccurenceFrequencyList["MONTHLY"] += 1;
      }
    }
  }

  function getBundlesForCurrentPricingFilter(bundleList) {
    var count = 0;
    var filteredBundles = new Array();
    if (bundleList != null) {
      for (var p = 0; p < bundleList.length; p++) {
        var bundleRevision = bundleList[p];
        updatePricingFilterReccurrenceCount(bundleRevision);
        if (!isValidForCurrentPricingFilter(bundleRevision)) {
          continue;
        }
        filteredBundles[count] = bundleRevision;
        count = count + 1;
      }
    }
    enableDisablePricingFilters();
    if (filteredBundles.length != 0 && (filteredBundles.length - ((pageNumber + 1) * pageLimit) > 0)) {
      enableMore = 1;
    } else {
      enableMore = 0;
    }
    return filteredBundles;
  }

  function paginateBundlesList(productBundles) {
    var start = pageNumber * pageLimit;
    var limit = (start + pageLimit) < productBundles.length ? (start + pageLimit) : productBundles.length
    for (var index = start; index < limit; index++) {
      var $newBundle = buildBundleRow(productBundles[index], null, null, null, true);
      $bundleContainer.append($newBundle.show());
    }

  }

  function getMoreBundles() {
    if (moreClick == 1 && moreClickForActive == 0) {
      pageNumber = pageNumber + 1
      populateBundles();
    } else if (moreClick == 0 && moreClickForActive == 1) {
      pageNumberForActive = pageNumberForActive + 1
      populateActiveSubscriptions();
    }
  }

  function isValidForCurrentPricingFilter(bundleRevision) {
    if (pricingFilter == 'ALL') {
      return true;
    } else if (bundleRevision.productBundle.rateCard.chargeType.name == pricingFilter) {
      return true;
    } else if (bundleRevision.productBundle.rateCard.chargeType.name == 'NONE' && pricingFilter == 'MONTHLY') {
      return true;
    }
    return false;
  }

  function buildBundleRow(bundleRevision, buttonLabel, buttonClass, subscriptionId, isCompatible) {
    // bundleRevision is a light-weight object.
    // so bundle id can be found using bundleRevision.pbid
    var bundle = bundleRevision.productBundle;

    var entitlements = bundleRevision.entitlements;
    var entitlementHtml = "";
    var totalentitlementhtml = "";
    var link_added = false;
    for (var a = 0; a < entitlements.length; a++) {
      var entitlement = entitlements[a];
      var usage = "";
      if (entitlement.includedUnits == -1) {
        usage = dictionary.unlimited;
      } else {
        usage = entitlement.includedUnits + "&nbsp;" + i18nUomText(entitlement.product.uom);
      }
      if (a >= 3) {
        if (link_added == false) {
          entitlementHtml = entitlementHtml + '<li>' + '<a id="more_link' + bundleRevision.pbid + '" value="' +
            dictionary.more + '" href="javascript:void(0);">' + dictionary.view_details + '</a>' + '</li>';
          link_added = true;
        }

        continue;
      }

      entitlementHtml = entitlementHtml + '<li class = "ellipsis" style="color: #000" title = "'+ entitlement.product.name +'"><span class="navicon ' + entitlement.product.name +
        '"></span><span class="text ellipsis" style="margin-top: 14px;">' + usage + "&nbsp;" + dictionary.of + "&nbsp;" +
        entitlement.product.name + "</span></li>";
    }
    if (!link_added) {
      entitlementHtml = entitlementHtml + '<li>' + '<a id="more_link' + bundleRevision.pbid + '" value="' + dictionary.more +
        '" href="javascript:void(0);">' + dictionary.view_details + '</a>' + '</li>';
      link_added = true;
    }
    var $newBundle = $bundleTemplate.clone(false);
    //Add service offering details.
    $newBundle.find("#bundle_name").text(bundle.name).attr("title", bundle.name);
    $newBundle.find("#bundle_description").text(bundle.description);
    $newBundle.find("#bundleDescription").html(bundle.description);
    if (bundle.imagePath != null){
    	$newBundle.find("#totalentitlmentsribbon").html('<img src="/portal/portal/logo/productBundles/' + bundleRevision.pbid + '" />');
    	$newBundle.find("#entitlmentsribbon").html('<img src="/portal/portal/logo/productBundles/' + bundleRevision.pbid + '" />');
    }
    else{
    	var ribbonId = Math.floor((Math.random() * 7) + 1);
    	$newBundle.find("#entitlmentsribbon").addClass("col" + ribbonId);
    	$newBundle.find("#totalentitlmentsribbon").addClass("col" + ribbonId);
    }
    $newBundle.find("#entitlements").html(entitlementHtml);
    $newBundle.find("#entitlements").attr("title", bundle.name);

    //$newBundle.find("#totalentitlments").html(totalentitlementhtml);
    $newBundle.attr("id", "bundle_" + bundleRevision.pbid + "vmproduct_" + bundleRevision.pbid);
    $newBundle.data("bundleRevisionObj", bundleRevision);
    if (subscriptionId != null) {
      $newBundle.data("subscriptionId", subscriptionId);
    }
    $newBundle.data("link_added", link_added);
    $newBundle.find("#name").text(bundle.name);
    $newBundle.find("#name").attr("title", bundle.name);
    $newBundle.find("#currencySign").text($("#selectedCurrencySign").val());
    $newBundle.find("#recurrenceType").text("/" + $("#" + bundle.rateCard.chargeType.name).attr('desc'));
    $newBundle.data("pricingFilterDisplay", $("#" + bundle.rateCard.chargeType.name).text().split("(")[0]);
    $newBundle.find("#more_link" + bundleRevision.pbid).attr("id", "more_link" + "bundle_" + bundleRevision.pbid +
      "vmproduct_" + bundleRevision.pbid);
    $newBundle.find("#totalentitlmentsdiv").attr("id", "totalentitlmentsdiv" + "bundle_" + bundleRevision.pbid +
      "vmproduct_" + bundleRevision.pbid);
    var charges = bundleRevision.rateCardCharges;
    var activationCharge = new Number(0);
    var recurringCharge = new Number(0);
    for (var n = 0; n < charges.length; n++) {
      var rateCardPrice = charges[n];
      if (rateCardPrice == null) {
        continue;
      }
      if (rateCardPrice.rateCardComponent.isRecurring == true) {
        recurringCharge = recurringCharge + rateCardPrice.price;
      } else {
        activationCharge = activationCharge + rateCardPrice.price;
      }
    }
    $newBundle.data('recurringCharges', recurringCharge);
    $newBundle.data('activationCharges', activationCharge);
    activationCharge = formatNumber(roundNumber((activationCharge),
        $("#minFractionDigits").val())) ;
    recurringCharge = formatNumber(roundNumber((recurringCharge),
        $("#minFractionDigits").val())) ;
    $newBundle.find("#activationCharges").text("+ " + $("#selectedCurrencySign").val() + activationCharge + " " +
      dictionary.oneTimeChargeType);
    if (bundle.rateCard.chargeType.name == 'NONE') {
      $newBundle.find("#currencySign").remove();
      $newBundle.find("#recurringCharges").remove();
      $newBundle.find("#bundle_row_charges_header").html(dictionary.labelMonthlyNoRecurring);
      $newBundle.find("#bundle_row_charges_header").parents(".js_charges_contentarea").addClass("monthlyNoRecurring");
      $newBundle.find("#recurrenceType").html(dictionary.messageMonthlyNoRecurring);
      $newBundle.find(".view_utility_pricing_link").unbind("click").bind("click", function() {
        viewUtilityPricing();
      });
    } else {
      $newBundle.find("#recurringCharges").html(recurringCharge);
    }

    $newBundle.find("#more_link" + "bundle_" + bundleRevision.pbid + "vmproduct_" + bundleRevision.pbid).bind("click",
      function(event) {
        actionMoreLink(event, $(this));
      });
    $newBundle.find("#subscribe").attr("id", "subscribe" + bundleRevision.pbid);
    if (buttonLabel != null) {
      $newBundle.find("#subscribe" + bundleRevision.pbid).text(buttonLabel);
    }
    if (isCompatible) {
      $newBundle.find("#subscribe" + bundleRevision.pbid).unbind("click").bind("click", function() {
        setAndSubscribeSelectedResourceComponentsForStep2($(this).closest("div[id^='bundle_']"), uniqueResourceComponents);
      });
    }
    if (buttonClass) {
      $newBundle.find(".subscribebutton").removeClass("subscribebutton").addClass(buttonClass);
    }
    return $newBundle;
  }

  function actionMoreLink(event, current, from_page) {
    var current_bundle = $(current).closest("div[id^='bundle_']");
    var bundle_revision = $(current_bundle).data("bundleRevisionObj");

    var dialogId = "totalentitlmentsdiv";
    var targetId;
    if (from_page == 'from_subscribe')
      targetId = $(current).attr("id");
    else
      targetId = $(current).attr("id").substr(9);

    dialogId = dialogId + targetId;

    initDialog(dialogId, 700);
    var $thisDialog = $("#" + dialogId);
    $thisDialog.find(".js_rate_details").html(current_bundle.find('.js_rate_details').html());
    var suffix = $thisDialog.find(".js_rate_details").find("#recurrenceType").find(".view_utility_pricing_link").text();
    if(suffix != "") {
      $thisDialog.find(".js_rate_details").parents(".js_charges_contentarea").addClass("monthlyNoRecurring");
      $thisDialog.find(".js_rate_details").find("#recurrenceType").find(".view_utility_pricing_link").remove();
      var prefix = $thisDialog.find(".js_rate_details").find("#recurrenceType").text();
      $thisDialog.find(".js_rate_details").find("#recurrenceType").text(prefix + suffix);
    }
    $thisDialog.dialog('option', 'reSizable', false);
    $thisDialog.dialog('option', 'minWidth', 700);
    $thisDialog.dialog('option', 'minHeight', 300);
    $thisDialog.dialog('option', 'height', "auto");
    $thisDialog.dialog('option', 'buttons', {
      "Close": function() {

        $(this).dialog("close");
      }
    });
    dialogButtonsLocalizer($thisDialog, {
      'Close': g_dictionary.dialogClose
    });
    $thisDialog.find(".js_view_utility_link").removeClass("more_up").addClass("more_down");
    $thisDialog.find("#utilityrate_table_bundle_details").empty();
    $thisDialog.find(".js_extra_usage_div").hide();
    $thisDialog.find("#totalentitlments").empty();
    populateEntitlementsTable(bundle_revision, dialogId);
    $thisDialog.dialog("open");

    $thisDialog.find(".js_view_utility_link").unbind("click").bind("click", function(e) {
      var $extra_usage_div = $thisDialog.find(".js_extra_usage_div");
      var html = "";

      if ($extra_usage_div.css('display') == 'none') {
        $thisDialog.find(".js_loading").show();
        html = populateUtilityRatesTable($("#tenantParam").val(), $("#serviceInstanceUuid").val());
        $thisDialog.find("#utilityrate_table_bundle_details").html(html);
        $thisDialog.find(".js_loading").hide();
      }
      $(this).toggleClass("more_down").toggleClass("more_up");
      $thisDialog.find(".js_extra_usage_div").toggle();
    });
  }

  function openSubscribeDialog(message, messageType, returnVal) {
    initDialog("launch_vm_dialog", 700);
    var $launchVmDialog = $("#launch_vm_dialog");
    $launchVmDialog.dialog("option", {
      height: "auto",
      width: 700,
      "resizable": false,
      dialogClass: 'no-close',
      closeOnEscape: false
    });

    var buttons = {};
    if(subscriptionButtonVisiblity==true){
      
      var redirect_url = "subscriptions";
      if(isPayAsYouGoChosen) {
        redirect_url = "utility_charges";
      }
      
    buttons["Go to Subscriptions"] = function() {
      var location = "/portal/portal/billing/" + redirect_url + "?tenant=" + tenantParam;
      if (isNotBlank(returnVal) && isNotBlank(returnVal.subscriptionId)) {
        location += "&id=" + returnVal.subscriptionId;
      }
      window.location = location;
    };
    }
    buttons["Go to Catalog"] = function() {
      window.location = "/portal/portal/subscription/createsubscription?tenant=" + tenantParam;
    };

    if (resourceTypeSelection != SERVICE_RESOURCE_TYPE) {
      buttons["Go to My Resources"] = function() {
        $launchVmDialog.dialog("close");
        launchMyResourcesWithServiceInstanceUUID($("#serviceInstanceUuid").val(), "/portal/portal/connector/csinstances?tenant=" + effectiveTenantParam);
      };
    }


    buttons["Go to DashBoard"] = function() {
      window.location = "/portal/portal/home?tenant=" + tenantParam + "&secondLevel=true";
    };

    $launchVmDialog.dialog('option', 'buttons', buttons);
    dialogButtonsLocalizer($launchVmDialog, {
      'Go to Subscriptions': dictionary.goToSubscriptions,
      'Go to Catalog': dictionary.goToCatalog,
      "Go to My Resources": dictionary.goToMyResources,
      'Go to DashBoard': dictionary.goToDashboard
    });
    $launchVmDialog.dialog("open");
    $("#launchingVm_template").find("#message2").html(message);

    var messageClass = "success";
    if (messageType == "error") {
      messageClass = "error";
    }
    $("#launchingVm_template").addClass(messageClass);
    $("#launchingVm_template").show();
    
    if (isNotBlank(returnVal) && isNotBlank(returnVal.subscriptionId) && resourceTypeSelection != SERVICE_RESOURCE_TYPE) {
      $("#resource_status_icon").addClass("loading");
      $("#resource_status_loading").show();
      $("body").everyTime(2000, "timerKey", function() {
        var $element = $(this);
        $.ajax({
          type: "GET",
          url: "/portal/portal/subscription/getBusinessTransactionDetailsForErroredSubscription",
          async: true,
          cache: false,
          data : {
            subscriptionUuid : returnVal.subscriptionId
          },
          dataType: "json",
          success: function(transactionMap) {
            if(transactionMap != null && transactionMap.businessTransactionState != null && transactionMap.businessTransactionState != "NEW") {
              $("#resource_status_icon").removeClass("loading");
              $("#resource_status_loading").hide();
              $("body").stopTime("timerKey");
              if(transactionMap.businessTransactionState == "ERROR") {
                $("#launchingVm_template").addClass("error");
                if(isNotBlank(transactionMap.message)) {
                  $("#launchingVm_template").find("#message2").html(transactionMap.message);
                } else {
                  $("#launchingVm_template").find("#message2").html(dictionary.subscriptionFailure);
                }
              }
            } else {
              $("#resource_status_icon").removeClass("loading");
              $("#resource_status_loading").hide();
              $("body").stopTime("timerKey");
            }
          }, error: function() {
            // If this call fails then ignore and dont do anything
          }, complete: function() {
            // If this call fails then ignore and dont do anything
          }
        });
      }, 0);
    }
  }

  function launchVM(provision) {

    $("#top_message_panel").find("#msg").text("");
    $("#top_message_panel").hide();

    if ($("#launchResource").attr('disabledByCustom') == "true") {
      msg = $("#launchResource").data('message');
      if (msg != null) {
        popUpDialogForAlerts("dialog_info", msg);
      }
      return;
    }

    if (!checkRCsFullFillment()) {
      // If RCs still not fulfilled on final provision click, then generate a popup to let user know
      // abput the components stull not selected
      var msg = dictionary.error_select_required_rcs + ":";
      var remainingRCs = arr_diff(selectedResourceComponentsForStep2, getCurrentSelectedComponentsNames());
      msg += "<ul style='margin-top:5px;'>";
      for (var k = 0; k < remainingRCs.length; k++) {
        var rcName = l10dict[remainingRCs[k] + '-name'];
        msg += "<li class='missing_resource_components ellipsis'>" + rcName + "</li>";
      }
      msg += "</ul>";
      popUpDialogForAlerts("dialog_info", msg);
      return;
    }

    if ($("#accept_checkbox").is(":checked") == false) {
      popUpDialogForAlerts("dialog_info", g_dictionary.youCanNotSubscribeUntilYouAcceptTheTermsAndConditions);
      $("#accept_checkbox").focus();
      return;
    }
    
    if(typeof(validationHook) != "undefined" && typeof(validationHook) == "function") {
      var customUiValidateStatus = validationHook();
      if(!customUiValidateStatus) {
        return;
      }
    }

    $("#spinning_wheel").show();
    var productPropertyObj = new Object();
    for (var i = 0; i < resourceProperties.length; i++) {
      target = $("input[name='" + resourceProperties[i] + "']");
      var eleName = target.attr("name");
      var eleValue = target.val();
      productPropertyObj[eleName] = eleValue;
    }
    
    var configurationNamesObj = new Object();
    for (var i = 0; i < allConfigurationProperties.length; i++) {
      target = $("input[name='" + allConfigurationProperties[i] + "']");
      var eleName = target.attr("name");
      var eleValue = target.data('valueDisplayName');
      if(isNotBlank(eleValue)) {
        configurationNamesObj[eleName + "_name"] = eleValue;
      }
    }

    var configurationNames = JSON.stringify(configurationNamesObj);
    configurationNames = encodeURIComponent(configurationNames);
    
    var deployVmUrl = "/portal/portal/subscription/subscribe_resource?tenant=" + tenantParam;
    
    if(bundleObj_id != null) {
      deployVmUrl += "&productBundleId=" + bundleObj_id;
    }
    
    var propConfigs = JSON.stringify(productPropertyObj);

    propConfigs = encodeURIComponent(propConfigs);
    //console.log('...Prop Values..',propConfigs);
    deployVmUrl += "&configurationData=" + propConfigs;
    deployVmUrl += "&configurationNames=" + configurationNames;
    deployVmUrl += "&serviceInstaceUuid=" + serviceInstaceUuid;
    deployVmUrl += "&resourceType=" + resourceTypeSelection;
    deployVmUrl += "&filters=" + getSelectedFilterString();
    deployVmUrl += "&context=" + getContextString();
    if (provision) {
      deployVmUrl += "&isProvision=true";
    } else {
      deployVmUrl += "&isProvision=false";
    }
    if ($("#subscriptionId").length > 0) {
      deployVmUrl += "&subscriptionId=" + $("#subscriptionId").val();
    }
    if (newSubscriptionId != null && newSubscriptionId != "" && typeof(newSubscriptionId) != "undefined") {
      deployVmUrl += "&newSubscriptionId=" + newSubscriptionId;
    }
    var customFieldError = false;
    $.ajax({
      type: "POST",
      url: deployVmUrl,
      data: $("#subscriptionForm").serialize(),
      dataType: "json",
      success: function(returnVal) {
        $("#spinning_wheel").hide();
        var message = dictionary.subscriptionSuccess;
        if(SERVICE_RESOURCE_TYPE != resourceTypeSelection){
          message = dictionary.cloudSubscriptionSuccess;
        }
        if (returnVal.subscriptionResultMessage == "RECONFIGURED") {
          message = dictionary.subscriptionReconfigured;
        }
        if (returnVal.subscriptionResultMessage == "PROVISIONED") {
          message = dictionary.subscriptionProvisioned;
        }

        openSubscribeDialog(message, "success", returnVal);
      },
      error: function(jqXHR) {
          $("#spinning_wheel").hide();
          var responseText = jqXHR.responseText.trim();
          if (jqXHR.status != AJAX_FORM_VALIDATION_FAILED_CODE) {
            var message = "";
            var status="";
            var errorObjet = CPBM.parseJsonSafely(responseText);
            if (responseText.startsWith("RECONFIGURED:") || (errorObjet && errorObjet.additionalInfo.action =="RECONFIGURED")) {
              message = dictionary.subscriptionReconfiguredFailure;
              responseText = "";
            } else if (responseText.startsWith("PROVISIONED:") ||( errorObjet && errorObjet.additionalInfo.action =="PROVISIONED")) {
              message = dictionary.subscriptionProvisionedFailure;
              status = "failed";
              responseText = "";
            } else if (responseText.startsWith("NEWLY_CREATED:") || (errorObjet && errorObjet.additionalInfo.action =="NEWLY_CREATED")) {
              message = dictionary.subscriptionFailure;
              responseText = "";
            } else {
              message = dictionary.subscriptionFailure;
            }
            if(errorObjet){
              message = message +'</br>' + errorObjet.message;
              openSubscribeDialog(message, "error");
              $(function () {
                $("#resource_error").popover({
                  trigger: "hover",
                  html: true,
                  content: errorObjet.detailedMessage
                });
              });
              $("#resource_error").show();
            }else{
              message = message + "<br /> " + responseText;
              openSubscribeDialog(message, "error");
            }

          } else {
            var message = "";
            var status="";
            if (responseText.startsWith("RECONFIGURED:")) {
              message = dictionary.subscriptionReconfiguredFailure;
              responseText = "";
            } else if (responseText.startsWith("PROVISIONED:")) {
              message = dictionary.subscriptionProvisionedFailure;
              status = "failed";
              responseText = "";
            } else if (responseText.startsWith("NEWLY_CREATED:")) {
              message = dictionary.subscriptionFailure;
              responseText = "";
            } else {
              var fieldErrorList = displayAjaxFormError(jqXHR,
                "subscriptionForm",
                "registration_formbox_errormsg");
              if (fieldErrorList.length > 0) {
                customFieldError = true;
              } else {
                message = dictionary.subscriptionFailure;
              }
            }
            if (!customFieldError) {
          	  if(status=="failed"){
          		  message = message;  
          	  }else{
          		  message = message + ": " + $.parseJSON(responseText)["validationResult"];
          	  }
              $("#top_message_panel").find("#msg").html(message);
              $("#top_message_panel").find("#status_icon").removeClass("successicon").addClass("erroricon");
              $("#top_message_panel").removeClass("success").addClass("error").show();
            }
          }
        },
        complete: function(){
        }
      });
    }

  function refreshBundlesOrSelectionSummary() {
    if (currentPage == 2) {
      refreshYourSelectionSummary();
      populateProductsForGeneratedUsage();
    } else {
      var selectedRCsFiltersString = getContextStringOverrideEffective() + " : " + getSelectedFilterString() + " : " +
        $("#selectedcurrencytext").html();
      if (lastRCFilterSelection == selectedRCsFiltersString) {
        return;
      }
      lastRCFilterSelection = selectedRCsFiltersString;
      newSubsBundlesList = null;
      resetPricingFilterReccurrenceCount();
      activeSubsBundlesList = null;
      refreshBundlesListingForSelectedTab();
    }
  }

  function prepareSelectedCategory() {

    var refreshPage = function rePopulateCreateSubscription(serviceInstanceUuid, tenantParam) {
      window.location = getPrefixedLocation($("#resourceType").val(), serviceInstanceUuid, tenantParam);
    };

    var selectedCategory = $("#selectedCategory").val();

    if (isBlank(selectedCategory)) {
      selectedCategory = $('#service_category_list_container li')
        .first().attr("category");
    }

    populateServiceInstances(selectedCategory, $("#tenantParam").val(), refreshPage);
    var serviceCategoryListItems = $("#service_category_list_container li");
    serviceCategoryListItems.each(function() {
      $(this).unbind("click").bind("click",
        function() {
          $(".categorytabs").removeClass("current user");
          $(this).removeClass().addClass(
            "categorytabs current user");
          selectedCategory = $(this).attr("category");
          populateServiceInstances(selectedCategory, $(
            "#tenantParam").val(), refreshPage, refreshPage);
        });
    });
  }

  function loadFiltersAndRCs() {
    var returnValue = loadServiceFilters();
    if (returnValue == false){
      return false;
    }
    refreshRCsListing();
    return true;
  }

  function preparePopOver($element, header, content) {
    var data = "";
    if(isNotBlank(header)) {
      if(isBlank(content)) {
        data  = "<div class='rc_info_bubble_header'>" + header + "</div>";
      } else {
        data = "<div class='rc_info_bubble_header' style='border-bottom: 1px dotted #CCCCCC;'>" + header + "</div><br/>";
      }
    }
    data += content;
    $element.popover({
      content : data,
      trigger : "hover",
      placement : "right",
      container : "body",
      html : true
    });
  }
  
  
  //This function populates the filter choices for the service for step 1
  function loadServiceFilters() {
    var $container = $("#filters_SECTION_2").empty();
    for (i = 0; i < serviceFilterNames.length; i++) {
      $("#filter_by_string_label").show();
      var html = $("#list_box_container").clone();
      html.find("#filterBoxTitle").text(l10dict[serviceFilterNames[i] + "-name"]);
      preparePopOver(html.find("#filterBoxTitle").parent(), l10dict[serviceFilterNames[i] + "-name"], l10dict[serviceFilterNames[i] + "-desc"]);
      var $listBox = html.find("#filterBoxSelection").empty();
      $listBox.attr("name", serviceFilterNames[i]);
      var serviceFilterValues = getValuesForFilter(serviceFilterNames[i]);
      if (serviceFilterValues == false) {
        //handleConnectorError();
        return false;
      }
      for (j = 0; j < serviceFilterValues.length; j++) {
        var serviceFilterValue = serviceFilterValues[j];
        if (isBlank(JSON.stringify(serviceFilterValue)) || JSON.stringify(serviceFilterValue) == "undefined") {
          continue;
        }

        var name = serviceFilterValue["name"];
        var value = serviceFilterValue["value"];

        var extras = getl10nComponentDetails("Filter", serviceFilterValue["attributes"], serviceFilterValue["displayAttributes"], i);
        var extrasInfo = getl10nComponentDetails("Filter", serviceFilterValue["attributes"], serviceFilterValue["displayAttributes"], i, true);

        var $li = $("#list_rc_element_clone").clone();
        $li.attr("id", value);
        $li.attr("fieldDisplayName", l10dict[serviceFilterNames[i] + "-name"]);
        $li.find("#span_rc_name").html(name);
        $li.find("#span_rc_desc").html(extras);
        preparePopOver($li, name, extrasInfo);
        $listBox.append($li.show());
      }
      $listBox.find('li').unbind("click").bind("click", function() {
        var $target = $(this);

        if ($target.attr('disabled') == true || $target.attr('disabled') == 'disabled') {
          return;
        }

        $target.siblings(".active").removeClass('active');
        $target.addClass('active');
        refreshFilterInputField($target);
        refreshRCsListing();
      });

      var currentInput = $("input[name='" + serviceFilterNames[i] + "']").val();
      var found = false;
      if (isNotBlank(currentInput)) {
        html.find('li').each(function() {
          if ($(this).attr('id') == currentInput) {
            found = true;
            $(this).siblings(".active").removeClass('active');
            $(this).addClass('active');
            refreshFilterInputField($(this));
          }
        });
      }

      if (!found) {
        var target = html.find('li').first();
        target.siblings(".active").removeClass('active');
        target.addClass('active');
        refreshFilterInputField(target);
      }
      $container.append(html.show());
    }
  }

  function refreshInputField(target) {
    var $inputField = $("input[name='" + target.parent().attr('name') + "']");
    $inputField.val(target.attr("id"));
    $inputField.data('valueDisplayName', target.find('.js_displaytext').text());
    $inputField.data('fieldDisplayName', target.attr('fieldDisplayName'));
    $inputField.data('effectiveValue', target.attr("effectiveValue"));
    $inputField.data("componentAttributes", target.data("componentAttributes"));
  }

  function refreshFilterInputField($target) {
    var $inputField = $("input[name='" + $target.parent().attr('name') + "']");
    $inputField.val($target.attr("id"));
    $inputField.data('valueDisplayName', $target.find('.js_displaytext').text());
    $inputField.data('fieldDisplayName', $target.attr("fieldDisplayName"));
  }

  function getl10nComponentDetails(type, attributes, displayAttributes, index, forInfo) {
    var compKey;
    var compValue;
    if (type == "ResourceComponent") {
      compKey = uniqueResourceComponentsKey[index];
      compValue = uniqueResourceComponentsDescl10dict[index];
    } else if (type == "Filter") {
      compKey = serviceFilterDescKey[index];
      compValue = serviceFilterDescl10dict[index];
    }
    var extras = "";
    var extras = getFormattedDisplayAttribtutesString(displayAttributes, forInfo);
    if (isNotBlank(extras)) {
      if (compKey != compValue) {
        extras = getFormattedDisplayAttribtutesDescription(compValue, displayAttributes);
      }
    } else {
      extras = getFormattedAttribtutesString(attributes, forInfo);
    }
    return extras;
  }

  function loadServiceResourceComponents() {
    var $leftFilterPanel = $("#defaultUIRCsListingStep1").clone();
    $leftFilterPanel.empty();
    for (var i = 0; i < uniqueResourceComponents.length; i++) {
      $("#filter_by_string_label").show();
      var data = getValuesForComponent(uniqueResourceComponents[i]);

      if (data.length == 0) {
        resetInputField($("input[name='" + uniqueResourceComponents[i] + "']"));
        continue;
      }

      var html = $("#list_box_container").clone();
      html.find("#filterBoxTitle").text(l10dict[uniqueResourceComponents[i] + "-name"]);
      preparePopOver(html.find("#filterBoxTitle").parent(), l10dict[uniqueResourceComponents[i] + "-name"], l10dict[uniqueResourceComponents[i] + "-desc"]);
      var $listBox = html.find("#filterBoxSelection").empty();
      $listBox.attr("name", uniqueResourceComponents[i]);

      var $li = $("#list_rc_element_clone").clone();
      $li.addClass("active");
      $li.attr("id", "");
      $li.attr("effectiveValue", anyOptionStaticValue);
      $li.find("#span_rc_name").html(dictionary.label_Any);
      preparePopOver($li, null, dictionary.msg_any_required_component);
      $listBox.append($li.show());
      
      for (var component in data) {

        if (isBlank(JSON.stringify(data[component])) || JSON.stringify(data[component]) == "undefined") {
          continue;
        }

        var effectiveValue = data[component]["value"];
        if (data[component]["parent"] != null) {
          effectiveValue = data[component]["parent"]["value"];
        }

        var extras = getl10nComponentDetails("ResourceComponent", data[component]["attributes"], data[component][
          "displayAttributes"
        ], i);

        var extrasInfo = getl10nComponentDetails("ResourceComponent", data[component]["attributes"], data[component]["displayAttributes"], i, true);

        var $li = $("#list_rc_element_clone").clone();
        $li.attr("id", data[component]["value"]);
        $li.attr("effectiveValue", effectiveValue);
        $li.attr("fieldDisplayName", l10dict[uniqueResourceComponents[i] + "-name"]);
        $li.data("componentAttributes", data[component]["attributes"]);
        $li.find("#span_rc_name").html(data[component]["name"]);
        $li.find("#span_rc_desc").html(extras);
        preparePopOver($li, data[component]["name"], extrasInfo);
        $listBox.append($li.show());
      }

      $listBox.find('li').unbind("click").bind("click", function() {
        var target = $(this);
        if (target.attr('disabled') == true || target.attr('disabled') == 'disabled') {
          return;
        }
        $(".popover").hide();
        target.siblings(".active").removeClass("active");
        target.addClass("active");
        refreshInputField(target);
        refreshRCsListing();
      });

      var currentInput = $("input[name='" + uniqueResourceComponents[i] + "']").val();
      var found = false;

      if (isNotBlank(currentInput)) {
        html.find('li').each(function() {
          if ($(this).attr('id') == currentInput) {
            found = true;
            $(this).siblings(".active").removeClass("active");
            $(this).addClass("active");
            refreshInputField($(this));
          }
        });
      }

      if (!found) {
        var target = html.find("li").first();
        target.siblings(".active").removeClass("active");
        target.addClass("active");
        refreshInputField(target);
      }
      $leftFilterPanel.append(html.show());
    }
    $("#components_SECTION_2").html($leftFilterPanel.show());
  }

  function getSelectedFiltersAndComponents(filtersOnly, componentsOnly) {
    var selected = [];
    for (var i = 0; i < serviceFilterNames.length; i++) {
      var filterValue = $("input[name='" + serviceFilterNames[i] + "']").data('valueDisplayName');
      if (isNotBlank(filterValue)) { // && filterValue != dictionary.all) {
        selected.push(filterValue);
      }
    }
    if (filtersOnly != null && filtersOnly == true) {
      return selected;
    }
    if (componentsOnly != null && componentsOnly == true) {
      selected = [];
    }
    for (var i = 0; i < uniqueResourceComponents.length; i++) {
      var compInputVal = $("input[name='" + uniqueResourceComponents[i] + "']").data('valueDisplayName');
      if (isNotBlank(compInputVal)) { // && compInputVal != dictionary.all) {
        selected.push(compInputVal);
      }
    }
    if (selected.length == 0) {
      selected.push(dictionary.label_None);
    }
    return selected;
  }

  function changeResourceType(selectedResourceType) {
    window.location = getPrefixedLocation(selectedResourceType, $("#serviceInstanceUuid").val(), $("#tenantParam").val());
  }

  function callBackAfterPromiseStep1() {
    refreshBundlesOrSelectionSummary();
    isStep1Ready = true;
    if (isReconfigure) {
      $("#SECTION_2").find('.catalogfilter_filterlist').each(function() {
        var componentId = $(this).attr('name');
        if (isBlank(componentId) || reconfigurableMap[componentId] == "false" || reconfigurableMap[componentId] ==
          undefined) {
          $(this).find('li').attr('disabled', true);
        }
      });
    }

    // In case of reprovision, the user should be directed to step2 directly with the current subscription bundle selected
    if (isReprovision && currentPage == 1) {
      setAndSubscribeSelectedResourceComponentsForStep2($bundleContainer.find('div[id^=bundle_]').filter(":first"), uniqueResourceComponents);
    }

    $("#spinning_wheel").hide();
  }

  function refreshRCsListing() {
    $("#spinning_wheel").show();
    var promise = null;
    if (isNotBlank($("#customComponentSelector").val())) {
      promise = initializeComponentSelector(false, 1);
      if (promise != null) {
        promise.done(function() {
          callBackAfterPromiseStep1();
        });
      } else {
        callBackAfterPromiseStep1();
      }
    } else {
      loadServiceResourceComponents();
      callBackAfterPromiseStep1();
    }
  }

  function appendChannelAndRevision(url) {
    var channelId = $("#channelId").length == 0 ? "" : $("#channelId").val();
    var revision = $("#revision").length == 0 ? "" : $("#revision").val();
    var dateFormat = $("#dateFormat").length == 0 ? "" : $("#dateFormat").val();
    var revisionDate = $("#revisionDate").length == 0 ? "" : $("#revisionDate").val();
    return url += "?revision=" + revision + "&channelParam=" + channelId + "&revisionDate=" + revisionDate + "&dateFormat=" + dateFormat;
  }
  
  function populateUtilityRatesTable(tenant, serviceInstanceUuid, revision) {
    var currencyCode = $("#selectedcurrencytext").text();
    var returnHtml = null;
    $.ajax({
      type: "GET",
      async: false,
      url: appendChannelAndRevision("/portal/portal/subscription/utilityrates_table"),
      data: {
        tenant: tenant,
        serviceInstanceUuid: serviceInstanceUuid,
        currencyCode: currencyCode
      },
      dataType: "html",
      cache: false,
      success: function(html) {
        $("#utilityrate_table").empty();
        $("#utilityrate_table").html(html);
        returnHtml = html
      }
    });
    return returnHtml;
  }

  function getCurrentSelectedComponentsNames() {
    var currentComponents = [];
    for (var i = 0; i < uniqueResourceComponents.length; i++) {
      var compInputVal = $("input[name='" + uniqueResourceComponents[i] + "']").val();
      if (isNotBlank(compInputVal)) { // && compInputVal != dictionary.all) {
        currentComponents.push(uniqueResourceComponents[i]);
      }
    }
    return currentComponents;
  }

  function checkRCsFullFillment() {
    // This function return true or false on basis of whether all the RCs required for provision are selected ot not
    for (var i = 0; i < selectedResourceComponentsForStep2.length; i++) {
      var compInputVal = $("input[name='" + selectedResourceComponentsForStep2[i] + "']").val();
      if (isBlank(compInputVal)) {
        return false;
      }
    }
    return true;
  }

  function refreshFiltersInputStep2(target) {
    var $inputField = $("input[name='" + target.data('filterId') + "']");
    $inputField.val(target.val());
    $inputField.data('valueDisplayName', target.data('valueDisplayName'));
    $inputField.data('fieldDisplayName', target.data('fieldDisplayName'));
    $inputField.change();
  }

  function refreshRCsInputStep2(target) {
    var $inputField = $("input[name='" + target.data('componentId') + "']");
    $inputField.val(target.val());
    $inputField.data('valueDisplayName', target.data('valueDisplayName'));
    $inputField.data('fieldDisplayName', target.data('fieldDisplayName'));
    $inputField.data('effectiveValue', target.data("effectiveValue"));
    $inputField.data('componentAttributes', target.data("componentAttributes"));
    $inputField.change();
  }

  function populateRCsAndFiltersStep2() {
    // Default Filters will be selected on step1 for subscribe bundles, so no need to repopulate in step2
    if (isPayAsYouGoChosen || isReprovision) {
      loadServiceFiltersStep2();
    }
    if (!checkRCsFullFillment() || isReprovision) {
      // If any RCs are pending selection or its the case of reprovision, then only load resource components
      // view in step2
      refreshRCsListingStep2();
      $("#componentsHeaderProvisionPage").show();
      $("#componentsSectionProvisionPage").show();
    } else {
      // Else skip the connector call and hide respective div
      $("#customComponentSelectorContent").hide();
      prepareStep2PricingAndEntitlements();
      $("#componentsHeaderProvisionPage").hide();
      $("#componentsSectionProvisionPage").hide();
    }
  }

  function callBackAfterPromiseStep2() {
    prepareStep2PricingAndEntitlements();
    $("#spinning_wheel").hide();
  }

  function refreshRCsListingStep2(hidePreselectedFields) {
    isStep2Ready = false;
    var promise = null;
    if (isNotBlank($("#customComponentSelector").val())) {
      promise = initializeComponentSelector(!isPayAsYouGoChosen, 2, selectedResourceComponentsForStep2);
      if (promise != null) {
        promise.done(function() {
          callBackAfterPromiseStep2();
        });
      } else {
        callBackAfterPromiseStep2();
      }
    } else {
      loadServiceResourceComponentsStep2(hidePreselectedFields);
      callBackAfterPromiseStep2()
    }
  }


  function loadServiceFiltersStep2() {

    var $container = $("#filterSectionProvisionPage").empty();
    for (i = 0; i < serviceFilterNames.length; i++) {

      var fcName = serviceFilterNames[i];

      var html = $("#componentSelectionContainer").clone();
      html.find("#componentSelectionHeader").text(l10dict[fcName + "-name"]);
      var $listBox = html.find("#component_values_container").empty();
      $listBox.attr("name", fcName);
      var serviceFilterValues = getValuesForFilter(fcName);
      if (serviceFilterValues == null || serviceFilterValues.length == 0) {
        continue;
      }
      for (j = 0; j < serviceFilterValues.length; j++) {
        var serviceFilterValue = serviceFilterValues[j];

        if (isBlank(JSON.stringify(serviceFilterValue)) || JSON.stringify(serviceFilterValue) == "undefined") {
          continue;
        }

        var name = serviceFilterValue["name"];
        var value = serviceFilterValue["value"];


        var extras = getl10nComponentDetails("Filter", serviceFilterValue["attributes"], serviceFilterValue[
          "displayAttributes"], i);

        var $newResourceValue = $("#component_value_template").clone();

        $newResourceValue.find("#name").text(name);
        $newResourceValue.find("#attributes_description").html(extras);
        $newResourceValue.find("#attributes_description").attr('title', $newResourceValue.find(
          "#attributes_description").text());
        $newResourceValue.find(".radio").val(value);
        $newResourceValue.find(".radio").attr('id', value);
        $newResourceValue.find(".radio").data('fieldDisplayName', l10dict[fcName + "-name"]);
        $newResourceValue.find(".radio").data('valueDisplayName', name);
        $newResourceValue.find(".radio").data('filterId', fcName);
        $newResourceValue.find(".radio").data('text', name);
        $newResourceValue.find(".radio").prop('checked', false);
        $listBox.append($newResourceValue.show());
      }
      html.find('.radio').unbind("click").bind("click", function(event) {
        $("#spinning_wheel").show();
        refreshFiltersInputStep2($(event.target));
        refreshRCsListingStep2(false);
      });

      var currentInput = $("input[name='" + fcName + "']").val();
      var found = false;
      if (isNotBlank(currentInput)) {
        html.find("input:radio").each(function() {
          if ($(this).val() == currentInput) {
            found = true;
            $(this).prop('checked', true);
            refreshFiltersInputStep2($(this));
          }
        });
      }

      if (!found) {
        var target = html.find("input:radio:first");
        target.prop('checked', true);
        refreshFiltersInputStep2(target);
      }

      $container.append(html.show());
    }
  }

  function loadServiceResourceComponentsStep2(hidePreselectedFields) {

    var resourceSelectionHTML = $("#defaultRCsContent").clone();
    resourceSelectionHTML.empty();
    for (var i = 0; i < selectedResourceComponentsForStep2.length; i++) {

      var rcName = selectedResourceComponentsForStep2[i];

      var currentInput = $("input[name='" + rcName + "']").val();
      if (isNotBlank(currentInput)) {
        if (isReconfigure) {
          if (reconfigurableMap[rcName] == "false" || reconfigurableMap[rcName] == undefined) {
            continue;
          }
        } else if ((hidePreselectedFields == null || hidePreselectedFields == true) && !isPayAsYouGoChosen && !
          isReprovision) {
          continue;
        }
      }

      var html = $("#componentSelectionContainer").clone();
      html.find("#componentSelectionHeader").text(l10dict[rcName + "-name"]);
      var $listBox = html.find("#component_values_container").empty();
      $listBox.attr("name", rcName);
      var data = getValuesForComponent(rcName);
      if (data == null || data.length == 0) {
        if (isPayAsYouGoChosen) {
          // In PAYG case if any of the RCs dont have any values for this user, then dont allow him to continue

          var msg = dictionary.msg_no_values_for_required_components + ":";
          msg += "<ul style='margin-top:5px;'>";
          msg += "<li class='missing_resource_components ellipsis'>" + l10dict[rcName + "-name"] + "</li>";
          msg += "</ul>";

          initDialog("dialog_info", 390);
          $thisDialog = $("#dialog_info");
          $thisDialog.html(msg);
          $thisDialog.dialog('option', 'buttons', {
            "Back to Catalog": function() {
              $(this).dialog("close");
              $("#back_to_catalog").click();
            }
          });
          $thisDialog.dialog('option', 'buttons', {
            "Back to Catalog": function() {
              $(this).dialog("close");
              $("#back_to_catalog").click();
            }
          });
          dialogButtonsLocalizer($thisDialog, {
            'Back to Catalog': dictionary.label_back_to_catalog
          });
          $thisDialog.dialog("open");
          return;
        } else {
          resetInputField($("input[name='" + rcName + "']"));
          continue;
        }
      }
      for (var component in data) {

        if (isBlank(JSON.stringify(data[component])) || JSON.stringify(data[component]) == "undefined") {
          continue;
        }

        var effectiveValue = data[component]["value"];
        if (data[component]["parent"] != null) {
          effectiveValue = data[component]["parent"]["value"];
        }

        var $newResourceValue = $("#component_value_template").clone();

        var extras = getl10nComponentDetails("ResourceComponent", data[component]["attributes"], data[component][
          "displayAttributes"
        ], i);

        $newResourceValue.find("#name").text(data[component]["name"]);
        $newResourceValue.find("#attributes_description").html(extras);
        $newResourceValue.find("#attributes_description").attr('title', $newResourceValue.find(
          "#attributes_description").text());
        $newResourceValue.find(".radio").val(data[component]["value"]);
        $newResourceValue.find(".radio").attr('id', data[component]["value"]);
        $newResourceValue.find(".radio").data('componentId', rcName);
        $newResourceValue.find(".radio").data('componentAttributes', data[component]["attributes"]);
        $newResourceValue.find(".radio").data('fieldDisplayName', l10dict[rcName + "-name"]);
        $newResourceValue.find(".radio").data('valueDisplayName', data[component]["name"]);
        $newResourceValue.find(".radio").data('effectiveValue', effectiveValue);
        $newResourceValue.find(".radio").prop('checked', false);
        $listBox.append($newResourceValue.show());
      }

      html.find('.radio').unbind("click").bind("click", function(event) {
        refreshRCsInputStep2($(event.target));
        refreshRCsListingStep2(false);
      });

      var currentInput = $("input[name='" + rcName + "']").val();

      var found = false;
      if (isNotBlank(currentInput)) {
        html.find("input:radio").each(function() {
          if ($(this).val() == currentInput) {
            found = true;
            $(this).prop('checked', true);
            refreshRCsInputStep2($(this));
          }
        });
      }
      if (!found) {
        resetInputFieldByName(rcName);
      }

      if ($listBox.find('.radio').length == 1) {
        refreshRCsInputStep2($listBox.find('.radio').prop('checked', 'true').first());
      }

      resourceSelectionHTML.append(html.show());
    }
    $("#componentsSectionProvisionPage").html(resourceSelectionHTML.show());
  }

  function resetInputFields() {
    for (var i = 0; i < allConfigurationProperties.length; i++) {
      var $inputField = $("input[name='" + allConfigurationProperties[i] + "']");
      $inputField.val(null);
      $inputField.data('valueDisplayName', null);
      $inputField.data('fieldDisplayName', null);
      $inputField.data('effectiveValue', null);
      $inputField.data('componentAttributes', null);
    }
  }

  function resetInputField($inputField) {
    $inputField.val(null);
    $inputField.data('valueDisplayName', null);
    $inputField.data('fieldDisplayName', null);
    $inputField.data('effectiveValue', null);
    $inputField.data('componentAttributes', null);
  }

  function resetInputFieldByName(name) {
    var $inputField = $('input[name="' + name + '"]');
    $inputField.val(null);
    $inputField.data('valueDisplayName', null);
    $inputField.data('fieldDisplayName', null);
    $inputField.data('effectiveValue', null);
    $inputField.data('componentAttributes', null);
    $inputField.change();
  }

  function updateProperty(visibleField) {
    var $hiddenField = $("#conf_" + $(visibleField).attr('name'));
    $hiddenField.val($(visibleField).val());
    $hiddenField.change();
  }
}

function getValuesForComponent(componentTypeName, effComponentType) {
  var url;
  if (currentBundleTemplate != null) {
    var bundleRevisionObj = $(currentBundleTemplate).data("bundleRevisionObj");
  }
  if (bundleRevisionObj != null && bundleRevisionObj != undefined && !isPayAsYouGoChosen && bundleRevisionObj.pbid != 0) {
    url = "/portal/portal/subscription/getResourceComponentsForBundle?bundleId=" + bundleRevisionObj.pbid;
  } else {
    url = "/portal/portal/subscription/getResourceComponents";
  }
  if ($("#viewChannelCatalog").val() == 'true') {
    url = url + "?viewCatalog=true"
  }
  var returnValues;
  $.ajax({
    type: "GET",
    async: false,
    url: url,
    data: {
      serviceInstanceUuid: $("#serviceInstanceUuid").val(),
      resourceType: $("#resourceType").val(),
      componentType: componentTypeName,
      effComponentType: effComponentType,
      filters: getSelectedFilterString(),
      contextString: getContextString(),
      tenant: $("#tenantParam").val(),
      subscriptionId: $(currentBundleTemplate).data("subscriptionId")
    },
    dataType: "json",
    success: function(data) {
      returnValues = data;
    },
    error: function(error) {
      handleConnectorError(error.responseText);
    },
    complete: function(xhr, status) {
    }
  });
  return returnValues;
}

function getValuesForFilter(filterType) {
  var url;
  if (currentBundleTemplate != null) {
    var bundleRevisionObj = $(currentBundleTemplate).data("bundleRevisionObj");
  }
  if (bundleRevisionObj != null && bundleRevisionObj != undefined && !isPayAsYouGoChosen) {
    url = "/portal/portal/subscription/getFilterComponentsForBundle?bundleId=" + bundleRevisionObj.pbid;
  } else {
    url = "/portal/portal/subscription/getFilterComponents";
  }
  if ($("#viewChannelCatalog").val() == 'true') {
    url = url + "?viewCatalog=true"
  }
  var returnValues;
  $.ajax({
    type: "GET",
    async: false,
    url: url,
    data: {
      serviceInstanceUuid: $("#serviceInstanceUuid").val(),
      filterType: filterType,
      tenant: $("#tenantParam").val(),
      subscriptionId: $(currentBundleTemplate).data("subscriptionId")
    },
    dataType: "json",
    success: function(data) {
      returnValues = data;
    },
    error: function(error) {
      handleConnectorError(error.responseText);
      returnValues = false;
    },
    complete: function(xhr, status) {
    }
  });
  return returnValues;
}

function enableProvisionButton(enable, msg) {
  if (enable) {
    $("#launchResource").css({
      opacity: 1.0,
      visibility: "visible"
    });
    $("#launchResource").attr('disabledByCustom', "false");
  } else {
    $("#launchResource").css({
      opacity: 0.5,
      visibility: "visible"
    });
    $("#launchResource").attr('disabledByCustom', "true");

    if (msg != null && msg != "") {
      $("#launchResource").data('message', msg);
    }
  }
}

function getPrefixedLocation(selectedResourceType, serviceInstanceUuid, tenantParam) {
  var locationPrefix = "";
  if ($("#anonymousBrowsing").val() == 'true') {
    locationPrefix = "/portal/portal/catalog/browse_catalog";
    var currencyCode = $("#selectedcurrencytext").html();
    var channelCode = $("#channelCode").val();
    locationPrefix = locationPrefix + "?&currencyCode=" + currencyCode
    if (channelCode != null && channelCode != 'undefined') {
      locationPrefix = locationPrefix + "&channelCode=" + channelCode;
    }
  } else if ($("#viewChannelCatalog").val() == 'true') {
    locationPrefix = "/portal/portal/channel/catalog/view_catalog";
    var channelId = $("#channelId").val();
    var revision = $("#revision").val();
    var dateFormat = $("#dateFormat").val();
    var revisionDate = $("#revisionDate").val();
    var currencyCode = $("#selectedcurrencytext").html();
    locationPrefix = locationPrefix + "?viewCatalog=true&revision=" + revision + "&channelParam=" + channelId +
      "&currencyCode=" + currencyCode + "&revisionDate=" + revisionDate + "&dateFormat=" + dateFormat;
    locationPrefix = locationPrefix + "&tenant=" + tenantParam;
  } else {
    locationPrefix = "/portal/portal/subscription/createsubscription?tenant=" + tenantParam;
  }
  if (selectedResourceType != null && serviceInstanceUuid == $("#serviceInstanceUuid").val()) {
    // Reset the selected resource type in case service instance is changed (either of same category or different category)
    // Because its not necessary that the new service instance has the resource type selected previously
    locationPrefix = locationPrefix + "&resourceType=" + selectedResourceType;
  }
  locationPrefix = locationPrefix + "&serviceInstanceUUID=" + serviceInstanceUuid;

  return locationPrefix;
}

function getContextString() {
  var contextString = "";
  for (var i = 0; i < uniqueResourceComponents.length; i++) {
    var compInputVal = $("input[name='" + uniqueResourceComponents[i] + "']").val();
    if (isNotBlank(compInputVal)) {
      if (contextString !== "") {
        contextString += ",";
      }
      contextString += uniqueResourceComponents[i] + "=" + compInputVal;
    }
  }
  return contextString;
}

function getAllValuesForPopulatedRCsAndFilters(name) {
  var fieldValues = [];
  var $FieldDiv = $("#SECTION_2").find('div[name="' + name + '"]');
  $FieldDiv.find('li').each(function() {
    var value = $(this).attr('effectiveValue');
    if (isNotBlank(value) && value != anyOptionStaticValue) {
      fieldValues.push(name + "=" + value);
    }
  });
  return fieldValues.join(',');
}


//Only getting called while listing bundles and active unused subscriptions

function getContextStringOverrideEffective() {
  var contextString = "";
  for (var i = 0; i < uniqueResourceComponents.length; i++) {
    var $inputField = $("input[name='" + uniqueResourceComponents[i] + "']");
    var compInputVal = $inputField.data('effectiveValue');
    // This gets called in case of default UI RC selection for 'ANY' option
    if (compInputVal == anyOptionStaticValue) {
      if (contextString !== "") {
        contextString += ",";
      }
      contextString += getAllValuesForPopulatedRCsAndFilters(uniqueResourceComponents[i]);
      continue;
    }
    if (isBlank(compInputVal)) {
      compInputVal = $inputField.val();
    }
    // This gets called in case of custom UI RC selection or default UI RC selection other than 'ANY' option
    if (isNotBlank(compInputVal)) {
      var rcValues = compInputVal.split(",");
      for (var j = 0; j < rcValues.length; j++) {
        if (contextString !== "") {
          contextString += ",";
        }
        contextString += uniqueResourceComponents[i] + "=" + rcValues[j];
      }
    }
  }
  return contextString;
}

function getSelectedFilterString() {
  var filterStr = "";
  for (var i = 0; i < serviceFilterNames.length; i++) {
    var filterName = serviceFilterNames[i];
    var filterValue = $("input[name='" + serviceFilterNames[i] + "']").val();
    if (isNotBlank(filterValue)) {
      if (filterStr != "") {
        filterStr += ",";
      }
      filterStr += filterName + "=" + filterValue;
    }
  }
  return filterStr;
}

function handleConnectorError(errorMessage) {
  $("#spinning_wheel").hide();
  if(isBlank(errorMessage)){
	  errorMessage = CPBM.getErrorMessageObject(dictionary.cloud_service_down);
  }
  
  var customStyle = {
	'width' : '95%',
	'float' : 'left'
  };
  
  CPBM.showErrorWithPopover($("#catalog_content_area"), errorMessage, customStyle);
  
  throw new Error("Cloud Service Connection Error");
}

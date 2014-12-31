/*
*  Copyright © 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/
var DEFAULT_PER_PAGE_SIZE = 5;
var DEFAULT_CURRENT_PAGE = 1;
var KEY_VALUE_ITEM_SEPERATOR = "::";
var MAX_LENGTH = 255; 

$(document).ready(function() {
  $("#filter_dropdown").unbind("change").bind("change", function(event) {
    searchChannelByFilter();
  }); 
  
  $("#catalog_history_dates").change(function() {
    if ($("#productsOrBundle").val() == "product") {
      $("#product_utility_history").find("a").attr("style", "color: #000");
      $("#product_bundle_history").find("a").attr("style", "color: #2C8BBC");
      viewCatalogHistory($("#catalog_history_dates").val(), $("#historyDateFormat").val(), "showProductHistory");
    } else {
      $("#product_utility_history").find("a").attr("style", "color: #2C8BBC");
      $("#product_bundle_history").find("a").attr("style", "color: #000"); 
      viewCatalogHistory($("#catalog_history_dates").val(), $("#historyDateFormat").val());
    }
  });

  // Edit channel action click event handler  ( in viewchannel)
  $("#editchannel_action").unbind("click").bind("click", function(event) {
    editChannel(event, $("li[id^='channel'].selected.channels"));
  });
  
  $(".js_edit_channel_branding_action").unbind("click").bind("click", function(event) {
	  initDialog("dialog_edit_channel_branding", 650);
	  $("#top_message_panel").hide();
	  editChannelBranding(event, $("li[id^='channel'].selected.channels"));
  });
  
  // Add channel action click event handler  ( in viewchannel)
  $("#add_channel_link").unbind("click").bind("click", function(event) {
    addChannel(event);
  });

  // Mouse-over event of action menu container ( in viewchannel)
  $("#action_menu_container").unbind("click").bind("mouseover", function(event) {
    showActionMenu(event);
  });

  // Mouse-out event of action menu container ( in viewchannel)
  $("#action_menu_container").unbind("click").bind("mouseout", function(event) {
    hideActionMenu(event);
  });

  // Delete channel action click event handler  ( in viewchannel)
  $("#deletechannel_action").unbind("click").bind("click", function(event) {
    deleteChannel(event, $("li[id^='channel'].selected.channels"));
  });
  
//Resets channel brandings  ( in viewchannel)
  $("#reset_channel_brandings_action").unbind("click").bind("click", function(event) {
	$("#top_message_panel").hide();
    resetChannelBrandings(event, $("li[id^='channel'].selected.channels"));
  });
  
  $("#view_catalog_action").unbind("click").bind("click", function(event) {
    var channelParam = $("li[id^='channel'].selected.channels").attr('id').substr(7);
    var revisionDate = $("#catalog_history_dates").val();
    var revision = $("#currentHistoryPlanned").val();
    var dateFormat = $("#historyDateFormat").val();
    window.open("/portal/portal/channel/catalog/view_catalog?channelParam=" + channelParam + "&revision=" +
      revision + "&revisionDate=" + revisionDate + "&dateFormat=" + dateFormat, "_blank",
      "width=1000,height=850,resizable=yes,menubar=no,status=no,scrollbars=yes,toolbar=no,location=no");

  });

  
 // Reset channel Brandings
  
  function resetChannelBrandings(event, current) {
  initDialog("dialog_reset_channel_brandings", 390);
    var divId = $(current).attr('id');
    var id = divId.substr(7);
    var $thisDialog = $("#dialog_reset_channel_brandings");
    
    $thisDialog.dialog('option', 'buttons', {
    	"Cancel": function() {
            $(this).dialog("close");
          },
      "OK": function() {
        var url = "/portal/portal/channels/resetchannelbrandings";
        $.ajax({
          type: "POST",
          url: url,
          async: false,
          cache: false,
          data: {
            "Id": id
          },
          dataType: "html",
          success: function(result) {
            $thisDialog.dialog("close");
            var date = new Date();
            viewChannel($("#channelgridcontent li.active"));
  		    brandingTabToggle();
            updateTopMessagePanel(commmonmessages.resetChannelBrandingSuccess, "success");
          },
          error: function(XMLHttpRequest) {
            
          }
        });
        $(this).dialog("close");
      }
      
    });
    dialogButtonsLocalizer($thisDialog, {
      'OK': g_dictionary.yes,
      'Cancel': g_dictionary.no
    });
    $thisDialog.dialog("open");
  }

  
  // Delete channel dialog
  
  function deleteChannel(event, current) {
	initDialog("dialog_delete_channel", 390);
    var divId = $(current).attr('id');
    var id = divId.substr(7);
    var $thisDialog = $("#dialog_delete_channel");
    
    $thisDialog.dialog('option', 'buttons', {
      "OK": function() {
        var url = "/portal/portal/channels/deletechannel";
        $.ajax({
          type: "POST",
          url: url,
          async: false,
          cache: false,
          data: {
            "Id": id
          },
          dataType: "html",
          success: function(result) {
            $thisDialog.dialog("close");
            if (result == "failure") {
              popUpDialogForAlerts("alert_dialog", i18n.errors.channels.failed_delete_channel);
            } else {
              location.reload(true);
            }
          },
          error: function(XMLHttpRequest) {
            popUpDialogForAlerts("alert_dialog", i18n.errors.channels.failed_delete_channel);
          }
        });
        $(this).dialog("close");
      },
      "Cancel": function() {
        $(this).dialog("close");
      }
    });
    dialogButtonsLocalizer($thisDialog, {
      'OK': g_dictionary.dialogOK,
      'Cancel': g_dictionary.dialogCancel
    });
    $thisDialog.dialog("open");
  }

  // Edit channel dialog
  initDialog("dialog_edit_channel", 900);

  function editChannel(event, current) {
    var id = $(current).attr('id').substr(7);
    var url = "/portal/portal/channels/editchannel";
    $.ajax({
      type: "GET",
      url: url,
      cache: false,
      data: {
        Id: id
      },
      dataType: "html",
      success: function(html) {
        var $thisDialog = $("#dialog_edit_channel");
        $thisDialog.html("");
        $thisDialog.html(html);
        $thisDialog.bind('dialogclose', function(event, ui) {
          $thisDialog.remove();
          $("#dialog_edit_channel_container").html("<div id='dialog_edit_channel' title='"+commmonmessages.dialogEditChannelTitle+"' style='display:none;'></div>");
          initDialog("dialog_edit_channel", 900);
        });
        $thisDialog.dialog("open");
      },
      error: function() {
        // need to handle TO-DO
      }
    });
  }
  
  $("#anonymous_preview").unbind("click").bind("click", function(event) {
	  event.preventDefault();
	  var channelCode = $(this).data("channel_code");
	  var currentLoggedInUserParam = $(this).data("current_user_param");
	  setupChannelBrandingPreview(channelCode,currentLoggedInUserParam);
  });

  function editChannelBranding(event, current) {
    var id = $(current).attr('id').substr(7);
    var url = "/portal/portal/channels/editchannelbranding";
    $.ajax({
      type: "GET",
      url: url,
      cache: false,
      data: {
      channelId: id
      },
      dataType: "html",
      success: function(html) {
        var $thisDialog = $("#dialog_edit_channel_branding");
        $thisDialog.html("");
        $thisDialog.html(html);
        $thisDialog.dialog('option', 'buttons', {
            "Preview": function() {
            	$("#publish").val(false);
		var channelCode = $("#channel\\.code").val();
		var currentLoggedInUserParam = $("#currentLoggedInUserParam").val();
                $('#channelBrandingForm').iframePostForm({
                    iframeID: 'channelBrandingForm-iframe-post-form',
                    json: true,
                    post: function() {
                      $("#channelBrandingForm-iframe-post-form").hide();
                      return true;
                    },
                    complete: function(response) {
                    	updateChannelBrandingDetails(response, $thisDialog);
                    }
                  });
                  $('#channelBrandingForm').submit();
          },
            "Cancel": function() {
              
              $thisDialog.dialog('close');
            }
          });
          dialogButtonsLocalizer($thisDialog, {
	    'Preview': g_dictionary.dialogSaveAndPreview,
            'Cancel': g_dictionary.dialogCancel
          });
        $thisDialog.dialog("open");
      },
      error: function() {
        // need to handle TO-DO
      }
    });
  }

  
  // Add Channel Dialog
  initDialog("dialog_add_channel", 900);

  function addChannel() {
    var url = "/portal/portal/channels/createchannel";
    $.ajax({
      type: "GET",
      url: url,
      cache: false,
      dataType: "html",
      success: function(html) {
        var $thisDialog = $("#dialog_add_channel");
        $thisDialog.bind('dialogclose', function(event, ui) {
          $thisDialog.remove();
          $("#dialog_add_channel_container").html("<div id='dialog_add_channel' title='"+commmonmessages.dialogAddChannelTitle+"' style='display:none;'></div>");
          initDialog("dialog_add_channel", 900);
        });
        
        $thisDialog.html("");
        $thisDialog.html(html);
        $thisDialog.dialog("open");
        $('#channelName').focus();
      },
      error: function(XMLHttpResponse) {
        if (XMLHttpResponse.status === PRECONDITION_FAILED) {
          popUpDialogForAlerts("alert_dialog", i18n.errors.channels.failed_create_channel_precondition);
        }
        // need to handle TO-DO
      }
    });
  }

  // Show action menu.

  function showActionMenu(event) {
    $("#action_menu").show();
  }

  // Hide action menu.

  function hideActionMenu(event) {
    $("#action_menu").hide();
  }

  scrollUrlHitMap = {};
  
  $(function () {
    $(".js_input_help").tooltip({
      trigger: 'focus'
    });
  });

  $('body').tooltip({
    selector: '.js_input_help'
  });
  
  $("#publishchannel_brandings_action").unbind("click").bind("click", function(event) {
	  $("#top_message_panel").hide();
	  publishChannelBranding(event, $("li[id^='channel'].selected.channels"));
  });
});

// Reset the styling of all the items

function resetGridRowStyle() {
  $(".widget_navigationlist").each(function() {
    $(this).removeClass("selected");
    $(this).removeClass("active");
  });
}

// View Channel info bubble

function viewInfoBubble(current) {
  if ($(current).hasClass('active')) return
  $(current).find("#info_bubble").show();
}

// Clear Channel info bubble

function clearInfoBubble(current) {
  $(current).find("#info_bubble").hide();
}

// View Channel details

function viewChannel(current) {
  $("div[id^='plan_date_div']").each(function() {
    $(this).remove();
  });
  var divId = $(current).attr('id');
  var id = divId.substr(7);
    resetGridRowStyle();
    $(current).addClass("selected");
    $(current).addClass("active");
  var url = "/portal/portal/channels/viewchannel";
  $("#spinning_wheel").show();
  $.ajax({
    type: "GET",
    url: url,
    data: {
      Id: id
    },
    cache: false,
    async: false,
    dataType: "html",
    success: function(html) {
      $("#main_details_content").html("");
      $("#main_details_content").html(html);
      var date = new Date();
      $(".js_channel_logo_" + id).attr('src', "/portal/portal/logo/channel/" + id + "?t=" + date.getMilliseconds());
	  $(".js_channel_favicon_" + id).attr('src', "/portal/portal/logo/channel/" + id+ "/favicon?t=" + date.getMilliseconds());
    },
    error: function() {
      // need to handle TO-DO
    }
  });
  $("#spinning_wheel").hide();
}

// Checks if the channel name is unique or not

function validate_channelname(event, input) {
  clearErrorsBeforeValidate("channelName", "name_errormsg");
  var channelName = $(input).val().trim();
  if (input.defaultValue.trim() == channelName) {
    return;
  }
  var err_msg = "";
  if (channelName.length >= MAX_LENGTH) {
    err_msg = i18n.errors.channels.max_length_exceeded + " 255";
  }
  if (err_msg.trim().length > 0) {
	  setErrorClassesAndShowError("channelName", "name_errormsg",
      getErrorLabel(err_msg));
    return;
  }
  $.ajax({
    type: "GET",
    url: "/portal/portal/channels/validate_channelname",
    data: {
      "channelName": channelName
    },
    dataType: "html",
    async: false,
    cache: false,
    success: function(result) {
      if (result == "false") {
        setErrorClassesAndShowError("channelName", "name_errormsg",
          getErrorLabel(i18n.errors.channels.channel_name_not_unique));
      }
    },
    error: function(html) {
      setErrorClassesAndShowError("channelName", "name_errormsg",
        getErrorLabel(html));
    }
  });
}

// Checks if the channel code is unique or not
function validate_channelcode(event, input) {
  clearErrorsBeforeValidate("channelCode", "code_errormsg");
  var channelCode = $(input).val().trim();
  if (input.defaultValue.trim() == channelCode) {
    return;
  }
  var err_msg = "";
  if (channelCode.length > 64) {
    err_msg = i18n.errors.channels.max_length_exceeded + " 64";
  }

  if (channelCode.length > 0 && !/^[a-zA-Z0-9_.-]+$/.test(channelCode)) {
    err_msg = i18n.errors.channels.code_invalid;
  }
  if (channelCode.trim().length == 0) {
    return;
  }

  if (err_msg.trim().length > 0) {
    setErrorClassesAndShowError("channelCode", "code_errormsg",
      getErrorLabel(err_msg));
    return;
  }
  $.ajax({
    type: "GET",
    url: "/portal/portal/products/validateCode",
    data: {
      "channelCode": channelCode
    },
    dataType: "html",
    async: false,
    cache: false,
    success: function(result) {
      if (result == "false") {
        setErrorClassesAndShowError("channelCode", "code_errormsg",
          getErrorLabel(i18n.errors.channels.channel_code_not_unique));
      }
    },
    error: function(html) {
      setErrorClassesAndShowError("channelCode", "code_errormsg",
        getErrorLabel(html));
    }
  });
}
function validate_channel_fqdn_prefix(event, input) {
  clearErrorsBeforeValidate("channelFQDNPrefix", "fqdn_prefix_errormsg");
  var channelFQDNPrefix = $(input).val().trim();
  if (input.defaultValue.trim() == channelFQDNPrefix) {
    return;
  }
  var err_msg = "";

  var fqdnRegExp1 = new RegExp("^(?:[A-Za-z0-9][A-Za-z0-9-]{0,34}[A-Za-z0-9]|[A-Za-z0-9])$");
  var fqdnRegExp2 = new RegExp("^(?:[0-9]*[-]{0,1}[0-9]*)$");
  if (channelFQDNPrefix.length > 0 && (!fqdnRegExp1.test(channelFQDNPrefix) || fqdnRegExp2.test(channelFQDNPrefix))) {
    err_msg = i18n.errors.channels.fqdn_invalid;
  }
  if (channelFQDNPrefix.trim().length == 0) {
    return;
  }

  if (err_msg.trim().length > 0) {
    setErrorClassesAndShowError("channelFQDNPrefix", "fqdn_prefix_errormsg",
      getErrorLabel(err_msg));
    return;
  }
  $.ajax({
    type: "GET",
    url: "/portal/portal/channels/validate_fqdn_prefix",
    data: {
      "fqdnPrefix": channelFQDNPrefix
    },
    dataType: "json",
    async: false,
    cache: false,
    success: function(result) {
      if (false == result.valid) {
        setErrorClassesAndShowError("channelFQDNPrefix", "fqdn_prefix_errormsg",
            getErrorLabel(i18n.errors.channels.channel_fqdn_not_unique));
      }
    },
    error: function(result) {
      setErrorClassesAndShowError("channelFQDNPrefix", "fqdn_prefix_errormsg",
          getErrorLabel(i18n.errors.channels.channel_fqdn_not_unique));
    }
  });
}

// When next is clicked on the left side panel "Next". We are right now showing only 14 entries

function nextClick() {
  var $currentPage = $('#current_page').val();
  var searchPattern = $("#channelSearchPanel").val(); 
 searchChannelByFilter(null, parseInt($currentPage) + 1, todb(searchPattern), todb($("#filter_dropdown").val()))
}

// When Previous is clicked on the left side panel "Prev". We are right now showing only 14 entries

function previousClick() {
  var $currentPage = $('#current_page').val();
  var searchPattern = $("#channelSearchPanel").val();
  searchChannelByFilter(null, parseInt($currentPage) - 1, todb(searchPattern), todb($("#filter_dropdown").val()))

}

// Channel description validator. We limit description to 255 characters.

function validate_channeldesc(event, input) {
  clearErrorsBeforeValidate("channelDescription", "description_errormsg");
  var channelDesc = $(input).val().trim();
  if (channelDesc.length > MAX_LENGTH) {
    $("#description_errormsg").text(i18n.errors.channels.max_length_exceeded + " 255");
    $("#description_errormsg").show();
  }
}

// Edit the bundle charges, as in over-riding bundle charges at the catalog
// level

function editBundleCharges(event, current) {
  initDialog("dialog_edit_bundle_charges", 850, 200);
  var $thisDialog = $("#dialog_edit_bundle_charges");
  $thisDialog.html("");
  var channelId = $("li[id^='channel'].selected.channels").attr('id').substr(7);
  var bundleId = $(current).attr('bundleId');
  var url = "/portal/portal/channels/editcatalogproductbundlepricing";
  $.ajax({
    type: "GET",
    url: url,
    data: {
      "channelId": channelId,
      "bundleId": bundleId
    },
    cache: false,
    dataType: "html",
    success: function(html) {
      $thisDialog.html(html);
      $thisDialog.find("#error_div").hide();
      $thisDialog.find("#priceError").html("");
      $thisDialog.dialog('option', 'buttons', {
        "OK": function() {
          var currencyVals = new Array();
          var inError = false;
          $('input[id^="currencyValsWeNeed"][class^="text"]').each(function() {
            var currencyNumberValue = formatFromCurrency($(this).attr("value").trim(), g_dictionary.thousandsSep, g_dictionary.decPoint);
            if (!isValidNonNegativeNo(currencyNumberValue)) {
              $thisDialog.find("#error_div").show();
              $thisDialog.find("#priceError").html(i18n.errors.channels.validPriceRequired);
              $(this).addClass("error");
              $(this).css("background-color", "#FFE4E4");
              $(this).css("background-image", "none");
              inError = true;
              return;
            }
            if (!isValidCurrencyPrecision(currencyNumberValue)) {
              $thisDialog.find("#error_div").show();
              $thisDialog.find("#priceError").html(i18n.errors.channels.max_four_decimal_value);
              $(this).addClass("error");
              $(this).css("background-color", "#FFE4E4");
              $(this).css("background-image", "none");
              inError = true;
              return;
            }
            var currencyObj = new Object();
            currencyObj.previousvalue = $(this).attr("previousvalue");
            currencyObj.value = currencyNumberValue;
            currencyObj.currencycode = $(this).attr("currencycode");
            currencyObj.currencyId = $(this).attr("currencyId");
            currencyObj.isRecurring = $(this).attr("isRecurring");
            currencyVals.push(currencyObj);
          });
          if (inError) {
            return;
          }
          $("#spinning_wheel").show();
          $.ajax({
            type: "POST",
            url: url,
            data: {
              "currencyValData": JSON.stringify(currencyVals),
              "channelId": channelId,
              "bundleId": bundleId
            },
            dataType: "json",
            async: false,
            cache: false,
            success: function(productCharges) {
              $thisDialog.find("#priceError").html("");
              $thisDialog.find("#error_div").hide();
              $thisDialog.html("");
              $thisDialog.dialog("close");
              viewCatalogPlanned();
            },
            error: function(XMLHttpRequest) {
              if (XMLHttpRequest.status === CODE_NOT_UNIQUE_ERROR_CODE) {
                popUpDialogForAlerts("alert_dialog", i18n.errors.common.codeNotUnique);
              } else {
                // TODO
              }
            }
          });
          $("#spinning_wheel").hide();
        },
        "Cancel": function() {
          $thisDialog.dialog("close");
        }
      });
      dialogButtonsLocalizer($thisDialog, {
        'OK': g_dictionary.dialogOK,
        'Cancel': g_dictionary.dialogCancel
      });
      $thisDialog.dialog("open");
    },
    error: function(XMLHttpResponse) {
      if (XMLHttpResponse.status === PRECONDITION_FAILED) {
         popUpDialogForAlerts("alert_dialog", i18n.errors.common.uncompatible_currency_precision);
       }
     }
  });
}

// Handler to view the catalog's current bundle / utility rate card charges. By default, bundle is selected

function viewCatalogCurrent() {
  $('#details_tab').removeClass('active').addClass("nonactive");
  $('#currencies_tab').removeClass('active').addClass("nonactive");
  $('#catalog_tab').removeClass('nonactive').addClass("active");
  $('#details_content').hide();
  $('#currencies_content').hide();

  $('#catalog_links').show();
  $("#second_line_under_planned").hide();
  $("#catalog_content").show();
  $('#action_currency').hide();
  $('#plan_date_picker').hide();

  $("#catalog_current_tab").find("a").attr("style", "color: #000");
  $("#catalog_planned_tab").find("a").attr("style", "color: #2C8BBC");
  $("#catalog_history_tab").find("a").attr("style", "color: #2C8BBC");
  $("#catalog_current_tab").closest(".widget_details_actionbox").attr("style", "height: auto");
  $("#catalog_content").html("");
  var channelId = $("li[id^='channel'].selected.channels").attr('id').substr(7);
  var url = "/portal/portal/channels/viewcatalogcurrent";
  $("#spinning_wheel").show();
  $.ajax({
    type: "GET",
    url: url,
    data: {
      channelId: channelId,
      page: DEFAULT_CURRENT_PAGE,
      perPage: DEFAULT_PER_PAGE_SIZE
    },
    cache: false,
    async: false,
    dataType: "html",
    success: function(html) {
      $("#catalog_content").html(html);
      $("#second_line_under_planned").show();
      if ($("#currentEffectiveDate").val() != "") {
        $("#second_line_under_planned").find("#effective_date").text($("#currentEffectiveDate").val());
      }
    },
    error: function() {
      // need to handle TO-DO
    }
  });
  $("#spinning_wheel").hide();
}

// Handler to edit ( add ) the currencies that the channel is going to support

function editCurrencies(event, current) {
  initDialog("dialog_edit_currencies", 665, 600);
  var $thisDialog = $("#dialog_edit_currencies");
  var id = $("li[id^='channel'].selected.channels").attr('id').substr(7);
  var url = "/portal/portal/channels/editchannelcurrency";
  $.ajax({
    type: "GET",
    url: url,
    data: {
      "channelId": id
    },
    cache: false,
    dataType: "html",
    success: function(html) {
      $thisDialog.html("");
      $thisDialog.html(html);
      $thisDialog.dialog('option', 'buttons', {
        "OK": function() {
          var id = $("li[id^='channel'].selected.channels").attr('id').substr(7);
          var currencyCodeArray = new Array();
          var currencySignArray = new Array();
          $($("#currencies_add").find('.widget_checkbox')).each(function() {
            if ($(this).find("span").attr("class") == "checked") {
              currencyCodeArray.push($(this).attr('currCode'));
              currencySignArray.push([$(this).attr("currCode"), $(this).attr("currSign"), $(this).attr('currName')]);
            }
          });
          if (currencyCodeArray.length == 0) {
            $(this).dialog("close");
            return;
          }
          $.ajax({
            type: "POST",
            url: url,
            data: {
              "channelId": id,
              "currencyCodeArray": JSON.stringify(currencyCodeArray)
            },
            cache: false,
            async: false,
            dataType: "html",
            success: function(html) {
              var htmlStr = "";
              var alreadySuportedCurrencies = $("#currency_row_container").find('.widget_currencyflag').length;
              for (var i = 0; i < currencySignArray.length; i++) {
                var thisCurrArr = currencySignArray[i];
                htmlStr += '<div class="widget_grid details ';
                if ((i + alreadySuportedCurrencies - 1) % 2) {
                  htmlStr += "odd";
                } else {
                  htmlStr += "even";
                }
                htmlStr += '"><div class="widget_checkbox"><span class="checked"></span></div>';
                htmlStr += '<div class="widget_grid_description" style="border:none;"><span><strong>';
                htmlStr += thisCurrArr[1] + ' - ' + thisCurrArr[2] + '</strong></span>';
                htmlStr += '</div><div class="widget_flagbox"><div class="widget_currencyflag">';
                htmlStr += '<img src="../../images/flags/' + thisCurrArr[0] + '.gif" alt="" /></div></div></div>';
                if ($("#channeCurrencies" + id).html().length > 0) {
                  $("#channeCurrencies" + id).html($("#channeCurrencies" + id).html() + ", " + thisCurrArr[0]);
                }
                if ($("li[id^='channel'].selected.channels").find("#channel_currencies").html().length > 0) {
                  $("li[id^='channel'].selected.channels").find("#channel_currencies").html(
                    $("li[id^='channel'].selected.channels").find("#channel_currencies").html() + ", " +
                    thisCurrArr[0]);
                }
              }
              $("#currency_row_container").html($("#currency_row_container").html() + htmlStr);
            },
            error: function() {}
          });
          $(this).dialog("close");
        },
        "Cancel": function() {
          $(this).dialog("close");
        }
      });
      dialogButtonsLocalizer($thisDialog, {
        'OK': g_dictionary.dialogOK,
        'Cancel': g_dictionary.dialogCancel
      });
      $thisDialog.dialog("open");
    },
    error: function() {
      // need to handle TO-DO
    }
  });
}

// Handler to view the catalog's current bundle / utility rate planned charges. By default, bundle is selected

function viewCatalogPlanned() {
  $("#catalog_content").html("");
  $("#second_line_under_planned").hide();
  var channelId = $("li[id^='channel'].selected.channels").attr('id').substr(7);
  var editpriceisvalid = "0";
  if ($("#planning_for_first_time").attr("value") == "") {
    editpriceisvalid = "1";
  }
  var url = "/portal/portal/channels/viewcatalogplanned";
  $("#spinning_wheel").show();
  $.ajax({
    type: "GET",
    url: url,
    data: {
      channelId: channelId,
      page: DEFAULT_CURRENT_PAGE,
      perPage: DEFAULT_PER_PAGE_SIZE,
      editpriceisvalid: editpriceisvalid
    },
    cache: false,
    async: false,
    dataType: "html",
    success: function(html) {
      $("#catalog_content").html(html);
      $("#second_line_under_planned").show();
      if ($("#currentEffectiveDate").val() != "") {
        $("#second_line_under_planned").find("#effective_date").text($("#currentEffectiveDate").val());
      } else{
    	  $("#second_line_under_planned").find("#effective_date").text(notYetSet);
      }
    },
    error: function() {
      // need to handle TO-DO
    }
  });
  $("#spinning_wheel").hide();
}

// Handler to view the catalog's history of bundle / utility rate card charges. By default, bundle is selected

function viewCatalogHistory(historyDate, format, showProductHistory) {
  $('#details_tab').removeClass('active').addClass("nonactive");
  $('#currencies_tab').removeClass('active').addClass("nonactive");
  $('#catalog_tab').removeClass('nonactive').addClass("active");
  $('#details_content').hide();
  $('#currencies_content').hide();

  $('#catalog_links').show();
  $("#catalog_content").show();
  $("#second_line_under_planned").hide();
  $('#action_currency').hide();
  $('#plan_date_picker').hide();

  $("#catalog_current_tab").find("a").attr("style", "color: #2C8BBC");
  $("#catalog_planned_tab").find("a").attr("style", "color: #2C8BBC");
  $("#catalog_history_tab").find("a").attr("style", "color: #000");
  $("#catalog_history_tab").closest(".widget_details_actionbox").attr("style", "height: 30px");

  $("#catalog_content").html("");
  var channelId = $("li[id^='channel'].selected.channels").attr('id').substr(7);
  var inputData = {
    channelId: channelId
  };
  if (historyDate != null) {
    inputData = {
      channelId: channelId,
      historyDate: historyDate,
      dateFormat: format,
      showProductHistory: showProductHistory
    };
  }
  var url = "/portal/portal/channels/viewcataloghistory";
  $("#spinning_wheel").show();
  $.ajax({
    type: "GET",
    url: url,
    data: inputData,
    cache: false,
    dataType: "html",
    async: false,
    success: function(html) {
      $("#catalog_content").html(html);
    },
    error: function() {
      // need to handle TO-DO
    }
  });
  $("#spinning_wheel").hide();
}

// Handler to edit the catalog's utility rate card charges

function editCatalogProductCharges() {
  initDialog("dialog_product_pricing_edit", 850, 600);
  var $thisDialog = $("#dialog_product_pricing_edit");
  $thisDialog.html("");
  $thisDialog.find("#error_div").hide();
  $thisDialog.find("#priceError").html("");

  var channelId = $("li[id^='channel'].selected.channels").attr('id').substr(7);
  var actionurl = "/portal/portal/channels/editcatalogproductpricing";
  $.ajax({
    type: "GET",
    url: actionurl,
    dataType: "html",
    async: false,
    cache: false,
    data: {
      "channelId": channelId
    },
    dataType: "html",
    success: function(html) {
      $thisDialog.html(html);
      $thisDialog.find("#priceError").html("");
      $thisDialog.dialog('option', 'buttons', {
        "OK": function() {
          var currencyVals = new Array();
          var inError = false;
          $('input[id^="currencyValsWeNeed"][class^="text"]').each(function() {
            var currencyNumberValue = formatFromCurrency($(this).attr("value").trim(), g_dictionary.thousandsSep, g_dictionary.decPoint);
            if (!isValidNonNegativeNo(currencyNumberValue)) {
              $thisDialog.find("#error_div").show();
              $thisDialog.find("#priceError").html(i18n.errors.channels.validPriceRequired);
              $(this).addClass("error");
              $(this).css("background-color", "#FFE4E4");
              $(this).css("background-image", "none");
              inError = true;
              return;
            }
            if (!isValidCurrencyPrecision(currencyNumberValue)) {
              $thisDialog.find("#error_div").show();
              $thisDialog.find("#priceError").html(i18n.errors.channels.max_four_decimal_value);
              $(this).addClass("error");
              $(this).css("background-color", "#FFE4E4");
              $(this).css("background-image", "none");
              inError = true;
              return;
            }
            var currencyObj = new Object();
            currencyObj.previousvalue = $(this).attr("previousvalue");
            currencyObj.value = currencyNumberValue;
            currencyObj.currencycode = $(this).attr("currencycode");
            currencyObj.currencyId = $(this).attr("currencyId");
            currencyObj.productId = $(this).attr("productId");
            currencyVals.push(currencyObj);
          });
          if (inError) {
            return;
          }
          $("#spinning_wheel").show();
          $.ajax({
            type: "POST",
            url: actionurl,
            data: {
              "currencyValData": JSON.stringify(currencyVals),
              "channelId": channelId
            },
            dataType: "json",
            async: false,
            cache: false,
            success: function(productCharges) {
              $thisDialog.find("#priceError").html("");
              $thisDialog.html("");
              $thisDialog.dialog("close");
              viewCatalogPlanned();
            },
            error: function(XMLHttpRequest) {
              if (XMLHttpRequest.status === CODE_NOT_UNIQUE_ERROR_CODE) {
                popUpDialogForAlerts("alert_dialog", i18n.errors.common.codeNotUnique);
              } else {
                // To do
              }
            }
          });
          $("#spinning_wheel").hide();
        },
        "Cancel": function() {
          $thisDialog.html("");
          $(this).dialog("close");
        }
      });
      dialogButtonsLocalizer($thisDialog, {
        'OK': g_dictionary.dialogOK,
        'Cancel': g_dictionary.dialogCancel
      });
      $thisDialog.dialog("open");
    },
    error: function(XMLHttpResponse) {
      if (XMLHttpResponse.status === PRECONDITION_FAILED) {
         popUpDialogForAlerts("alert_dialog", i18n.errors.common.uncompatible_currency_precision);
       }
     }
  });
}

// Handler to attach ( add ) a bundle to the catalog. Only published bundles will be listed. 

function attachProductBundle() {
  initDialog("attach_product_bundle", 500, 200);
  var $thisDialog = $("#attach_product_bundle");
  //$thisDialog.data("height.dialog", 400);
  $thisDialog.html("");

  var id = $("li[id^='channel'].selected.channels").attr('id').substr(7);
  var url = "/portal/portal/channels/listbundles";
  $.ajax({
    type: "GET",
    url: url,
    data: {
      "channelId": id
    },
    cache: false,
    dataType: "html",
    success: function(html) {
      $thisDialog.html(html);
      $thisDialog.dialog('option', 'buttons', {
        "OK": function() {
          var selectProductBundles = new Array();
          $($thisDialog.find('.widget_checkbox')).each(function() {
            if ($(this).find("span").attr("class") == "checked") {
              selectProductBundles.push($(this).attr('bundleId'));
            }
          });
          $("#spinning_wheel").show();
          $.ajax({
            type: "POST",
            url: "/portal/portal/channels/attachproductbundles",
            data: {
              "channelId": id,
              "selectProductBundles": JSON.stringify(selectProductBundles)
            },
            cache: false,
            dataType: "html",
            success: function(html) {
              viewCatalogPlanned();
            },
            error: function() {}
          });
          $("#spinning_wheel").hide();
          $(this).dialog("close");
        },
        "Cancel": function() {
          $thisDialog.dialog("close");
        }
      });
      dialogButtonsLocalizer($thisDialog, {
        'OK': g_dictionary.dialogOK,
        'Cancel': g_dictionary.dialogCancel
      });
      $thisDialog.dialog("open");
    },
    error: function() {
      // need to handle TO-DO
    }
  });
}

// Handler to search the channel on text entered. Search right now is text*
var searchRequest;

function searchChannelByFilter(event, currentPage, searchPattern, filterBy) {
  $("#click_previous").unbind("click", previousClick);
  $("#click_previous").addClass("nonactive");
  if(searchPattern == undefined){
    searchPattern = $("#channelSearchPanel").val();  
  }
  if(filterBy == undefined){
    filterBy = $("#filter_dropdown").val();
  }
  if(currentPage == undefined){
    currentPage = 1;
  }
  $("#click_next").removeClass("nonactive");
  $("#click_next").unbind("click").bind("click", nextClick);

  var url = "/portal/portal/channels/searchchannel?currentPage=" + currentPage  + "&namePattern=" + todb(searchPattern);
  if(todb($("#filter_dropdown").val()) != "all"){
    url += "&billingGroup=" + todb(filterBy);
  }
  
  if (searchRequest && searchRequest.readyState != 4) {
    searchRequest.abort();
  }
  searchRequest = $.ajax({
    type: "GET",
    url: url,
    dataType: "html",
    async: true,
    cache: false,
    success: function(html) {
      $("#channellistdiv").empty();
      $("#channellistdiv").html(html);
      if(html.indexOf("non_list") > 0){
       resetRightPanel(); 
      }else{
      $("#channelgridcontent").find(".channels:first").click();
      }
    },
    error: function() {
      //need to handle
    }
  });
}

function resetRightPanel(){
  $("#main_details_content").find(".widget_grid_description span").empty();
  $("#currencies_tab").hide();
  $("#catalog_tab").hide();
  $("#service_controls_tab").hide();
  $("#urls_tab").hide();
  $("#branding_tab").hide();
  $("#action_menu_container").hide();
}

// Handler of mouse over on " Included Usage" in bundle view 
$("a[id^='moreOf']").bind("mouseover", function(event) {
  var count = $(this).attr("count");
  $("#moreEntitlements" + count).show();
});

// Handler of mouse out on " Included Usage" in bundle view
$("a[id^='moreOf']").bind("mouseout", function(event) {
  var count = $(this).attr("count");
  $("#moreEntitlements" + count).hide();
});

// When "Bundles" under current is clicked
$("#product_bundle_current").unbind("click").bind("click", function(event) {
  $("#product_bundle_current").find("a").attr("style", "color: #000");
  $("#product_utility_current").find("a").attr("style", "color: #2C8BBC");

  $('#catalog_productbundle_current').show();
  $('#catalog_products_current').hide();
});

// When "Utility rate card" under current is clicked
$("#product_utility_current").unbind("click").bind("click", function(event) {
  $("#product_utility_current").find("a").attr("style", "color: #000");
  $("#product_bundle_current").find("a").attr("style", "color: #2C8BBC");

  $('#catalog_products_current').show();
  $('#catalog_productbundle_current').hide();
});

// When action icon mouse-over on a bundle ( under planned ) is done 
$("div[id^='action_per_product_bundle']").bind("mouseover", function(event) {
  $(this).find('#per_bundle_action_menu').show();
});

// When action icon mouse-out on a bundle ( under planned ) is done 
$("div[id^='action_per_product_bundle']").bind("mouseout", function(event) {
  $(this).find('#per_bundle_action_menu').hide();
});

// When action icon mouse-over on utility rate card ( under planned ) view is done 
$("#plan_product_charges").bind("mouseover", function(event) {
  $(this).find('#product_action_menu').show();
});

// When action icon mouse-out on utility rate card ( under planned ) view is done 
$("#plan_product_charges").bind("mouseout", function(event) {
  $(this).find('#product_action_menu').hide();
});

// When action icon mouse-over is done in area before listing of bundles ( under planned ) is done 
$("#bundle_level_menu").bind("mouseover", function(event) {
  $(this).find('#bundle_add_menu').show();
});

// When action icon mouse-out is done in area before listing of bundles ( under planned ) is done
$("#bundle_level_menu").bind("mouseout", function(event) {
  $(this).find('#bundle_add_menu').hide();
});

// Dialog box to show the entitlements of a bundle

function viewEntitlements(event, current) {
  var bundleId = $(current).attr("productbundleid");
  var channelId = $("li[id^='channel'].selected.channels").attr('id');
  channelId = channelId.substr(7);
  var entitlementsurl = "/portal/portal/productBundles/getPlannedEntitlements?bundleid=" + bundleId + "&channelid=" + channelId;
  $("#entitlements_dialog").html("");
  initDialog("entitlements_dialog", 400);
  $.ajax({
    type: "GET",
    url: entitlementsurl,
    dataType: "html",
    success: function(html) {
      $("#entitlements_dialog").html(html);
      var $thisDialog = $("#entitlements_dialog");
      $thisDialog.dialog('option', 'buttons', {
        "OK": function() {
          $thisDialog.dialog("close");
        }
      });
      dialogButtonsLocalizer($thisDialog, {
          'OK': g_dictionary.dialogOK
        });

      $thisDialog.dialog("open");

    }
  });

}

function createDialogDivContainer() {

}

function createDialogDiv(){
  var html = '<div  id="dialog_edit_channel_branding" title="'+commmonmessages.editChannelBranding+'" style="display: none"></div>';
  $("#dialog_div_container").html(html);
}
// Handler to update the Branding details of a channel.

function updateChannelBrandingDetails(response, dialog) {
  if (response == null || response == "null" || response == "") {
    popUpDialogForAlerts("alert_dialog", i18n.errors.channels.failed_upload_file);
    return false;
  }
  
  $(".main_addnew_formbox_errormsg").text("");
  try {
	  if(!$.isEmptyObject(response)  && !response.hasOwnProperty("success")){

	  $.each(response,function(key, val) {
          $("#"+key+"Error").text(val);
      });
	  return false;
	  }else if(response.hasOwnProperty("success")) {
		  
		  if(response['unpublished_css'] != null || response['unpublished_logo'] != null || response['unpublished_favicon'] != null) {
			  window.location = "/portal/portal/channels/list?page="+$('#current_page').val()+"&previewchannelcode="+response['channelCode'];
		  }
		  dialog.dialog("close");
		  dialog.dialog("destroy").remove();
		  createDialogDiv();
		  
	  } else{
		  dialog.dialog("close");
		  dialog.dialog("destroy").remove();
		  createDialogDiv();
	  }
  } catch (e) {
    popUpDialogForAlerts("alert_dialog", i18n.errors.channels.failed_upload_file);
    return false;
  }
  return true;
}


// Function to check if a string is valid, as in length doesn't exceed maximum, and length is not 0 and quotes aren't there

function isValidString(value, maxLength) {
  if (maxLength == undefined) {
    maxLength = 65537;
  }
  var isValid = true;
  if (value == null || value.length == 0) { //required field   
    errMsg = g_dictionary.required;
    isValid = false;
  } else if (value != null && value.length >= maxLength) {
    errMsg = g_dictionary.maximum + ": " + max + " character";
    isValid = false;
  } else if (value != null && value.indexOf('"') != -1) {
    errMsg = g_dictionary.doubleQuotesNotAllowed;
    isValid = false;
  }
  return isValid;
}

function isValidPhone(value) {
  return value.length==0 || value.length > 0 && /^\+?[\d]+([-]?[\d]+)*$/.test(value) && value.replace(/[^\d]/g, "").length <
    13 && value.replace(/[^\d]/g, "").length > 4;
}

function isEmailValid(label, field, errMsgField, isOptional) {
  if (validateString(label, field, errMsgField, isOptional) == false)
    return true;
  var isValid = true;
  var value = field.val();
  if (value != null && value.length > 0) {
    myregexp = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/;
    var isMatch = myregexp.test(value);
    if (!isMatch) {
      isValid = false;
    }
  }
  return isValid;
}

function validEmailDomains(domains) {
  if (domains.trim().length > 0) {
    myregexp = /^([ ]*[a-zA-Z0-9]+(\.[a-zA-Z0-9]+)+[ ]*)([,][ ]*([a-zA-Z0-9]+(\.[a-zA-Z0-9]+)+)[ ]*)*$/;
    return myregexp.test(domains);
  }
  return true;
}

// Sets the error classes in the channel add / edit wizard and show the errors

function setErrorClassesAndShowError(entityDivId, errorDivId, message) {
  $("#" + entityDivId).addClass("error");
  if ($("#" + errorDivId).find("label.error").length == 0) {
    $("#" + errorDivId).append(message);
  }
  $("#" + errorDivId).show();

}

// The error message label to be appended

function getErrorLabel(errorMsg) {
  return '<label class="error" style="display: block;">' + errorMsg + '</label>';
}

// Clears the errors and removes error class just before validation is done so that we are not left
// with errors even if validation succeeds. If it doesn't get validated again, the setErrorClassesAndShowError
// above will set the error again.

function clearErrorsBeforeValidate(entityDivId, errorDivId) {
  $("#" + errorDivId).find(".error").remove();
  $("#" + errorDivId).text("");
  $("#" + entityDivId).removeClass("error");
}

function isValidUrl(url) {
	  if(url=="#" || url=="")
	    return true;
	  var urlRegEx = /^(?:(?:https?|ftp):\/\/)(?:\S+(?::\S*)?@)?(?:(?!(?:10|127)(?:\.\d{1,3}){3})(?!(?:169\.254|192\.168)(?:\.\d{1,3}){2})(?!172\.(?:1[6-9]|2\d|3[0-1])(?:\.\d{1,3}){2})(?:[1-9]\d?|1\d\d|2[01]\d|22[0-3])(?:\.(?:1?\d{1,2}|2[0-4]\d|25[0-5])){2}(?:\.(?:[1-9]\d?|1\d\d|2[0-4]\d|25[0-4]))|(?:(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)(?:\.(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)*(?:\.(?:[a-z\u00a1-\uffff]{2,})))(?::\d{2,5})?(?:\/\S*)?$/;
	  var result = urlRegEx.test(url);
	  if(!result){
		var numericUrl = url.split("://");
			if(numericUrl.length == 2){	
				var regExp1 = /^[0-9].*$/
				var nextRegEx=regExp1.test(numericUrl[1]);
				console.log("nextRedEx:"+nextRegEx);
				if(nextRegEx){
					result= validateIpAndPort(numericUrl[1]);
				}
			}
	  }
	  return result;
}

function validateIpAndPort(input) {
    var parts = input.split(":");
    var ip = parts[0].split(".");
    var port = parts[1];
    var portValid = true;
    if(port != undefined){
	var portAndPath = port.split("/");
	portValid = validateNum(portAndPath[0], 1, 65535);
    }
    return  portValid &&
        ip.length == 4 &&
        ip.every(function (segment) {
            return validateNum(segment, 0, 255);
        });
}

function validateNum(input, min, max) {
	if(isNaN(input)){
		return false;
	}
    var num = +input;
    return num >= min && num <= max && input === num.toString();
}

function validateUrlAndShowError(entityDivId){
  clearErrorsBeforeValidate(entityDivId, entityDivId + "_errormsg");
  var value = $("#" + entityDivId).val();
  var isValid = isValidUrl(value);
  if (!isValid) {
    setErrorClassesAndShowError(entityDivId, entityDivId + "_errormsg", getErrorLabel(i18n.errors.channels.url));
  }
  return isValid;
}


// Checks if the channel form is valid. Checks for the correctness of name, code and currency selection. Atleast
// one currency needs to be selected.

function isChannelFormValid(channelForm, currentstep) {
  var name_err_msg = $(channelForm).find("#name_errormsg").text().trim();
  var code_err_msg = $(channelForm).find("#code_errormsg").text().trim();

  var isValidName = true;
  isValidName &= isValidString($(channelForm).find("#channelName").val());
  if ($(channelForm).find("#channelName").val().trim() == "") {
    setErrorClassesAndShowError("channelName", "name_errormsg", getErrorLabel(i18n.errors.channels.name));
  } else if (name_err_msg.length > 0) {
    setErrorClassesAndShowError("channelName", "name_errormsg", "");
  }

  var isValidCode = isValidString($(channelForm).find("#channelCode").val());
  if ($(channelForm).find("#channelCode").val().trim() == "") {
    var setCodeError = true;
    if ($(channelForm).find("#channelCode").attr("prevValue") != undefined) {
      if ($(channelForm).find("#channelCode").attr("prevValue") == "") {
        setCodeError = false;
        isValidCode = true;
      }
    }
    if (setCodeError) {
      setErrorClassesAndShowError("channelCode", "code_errormsg", getErrorLabel(i18n.errors.channels.code));
    }
  } else if (code_err_msg.length > 0) {
    setErrorClassesAndShowError("channelCode", "code_errormsg", "");
  }

  clearErrorsBeforeValidate("billingGroup", "billinggroupcode_errormsg");
  var isValidbillingGroupCode = true;
  if ($(channelForm).find("#billingGroup").val().length > MAX_LENGTH ) {
      isValidbillingGroupCode = false;
      setErrorClassesAndShowError("billingGroup", "billinggroupcode_errormsg", getErrorLabel(i18n.errors.channels.max_length_billingGroup));
  } else if (code_err_msg.length > 0) {
    setErrorClassesAndShowError("billingGroup", "billinggroupcode_errormsg", "");
  }
  
  var isValidCurrency = true;
  var isChannelCurrErrMsgThere = false;
  clearErrorsBeforeValidate("currencyList", "currency_errormsg");
  if (currentstep == 'step2') {
    if ($(channelForm).find("#currency_row_container").find('.widget_checkbox').find(".checked").length == 0) {
      isValidCurrency = false;
      setErrorClassesAndShowError("currencyList", "currency_errormsg", getErrorLabel(i18n.errors.channels.channel_currency_required));
    }
    if ($(channelForm).find("#currency_errormsg").text().trim().length > 0) {
      isChannelCurrErrMsgThere = true;
    }
  }

  var isValidPhoneNumber = true;
  var isValidEmail = true;
  var validWhitelistDomains = true;
  var validBlacklistDomains = true;
  
  if (currentstep == 'step3') {
    
    clearErrorsBeforeValidate("help_desk_email", "help_desk_email_errormsg");
    isValidEmail = isEmailValid("", $("#help_desk_email"), $("#help_desk_email_errormsg"), true);
    if (!isValidEmail) {
      setErrorClassesAndShowError("help_desk_email", "help_desk_email_errormsg",
          getErrorLabel(i18n.errors.channels.help_desk_email));
    }
    
    clearErrorsBeforeValidate("help_desk_phone", "help_desk_phone_errormsg");
    isValidPhoneNumber = isValidPhone($(channelForm).find("#help_desk_phone").val());
    if (!isValidPhoneNumber) {
      setErrorClassesAndShowError("help_desk_phone", "help_desk_phone_errormsg",
          getErrorLabel(i18n.errors.channels.help_desk_phone));
    }
    
    clearErrorsBeforeValidate("whitelistdomains", "whitelistdomains_errormsg");
    validWhitelistDomains = validEmailDomains($(channelForm).find("#whitelistdomains").val());
    if (!validWhitelistDomains) {
      setErrorClassesAndShowError("whitelistdomains", "whitelistdomains_errormsg",
          getErrorLabel(i18n.errors.channels.domainemails));
    }
    
    clearErrorsBeforeValidate("blacklistdomains", "blacklistdomains_errormsg");
    validBlacklistDomains = validEmailDomains($(channelForm).find("#blacklistdomains").val());
    if (!validBlacklistDomains) {
      setErrorClassesAndShowError("blacklistdomains", "blacklistdomains_errormsg",
          getErrorLabel(i18n.errors.channels.domainemails));
    }
    
  }
  
  var isUrlValid = true;
  if (currentstep == 'step4') {
    isUrlValid = validateUrlAndShowError("marketing_blog_url");
    isUrlValid = validateUrlAndShowError("marketing_contact_url") && isUrlValid;
    isUrlValid = validateUrlAndShowError("marketing_forum_url") && isUrlValid;
    isUrlValid = validateUrlAndShowError("marketing_help_url") && isUrlValid;
    isUrlValid = validateUrlAndShowError("marketing_privacy_url") && isUrlValid;
    isUrlValid = validateUrlAndShowError("marketing_support_url") && isUrlValid;
    isUrlValid = validateUrlAndShowError("marketing_tou_url") && isUrlValid;
  }
  
  if (!isValidName || !isValidCode || !isValidbillingGroupCode || !isValidPhoneNumber || !isValidEmail || !isUrlValid || !isValidCurrency || !validBlacklistDomains|| !validWhitelistDomains || $("#name_errormsg").text().trim().length > 0 ||
      $("#fqdn_prefix_errormsg").text().trim().length > 0 ||
    $(channelForm).find("#code_errormsg").text().trim().length > 0 ||
    $(channelForm).find("#description_errormsg").text().trim().length > 0 ||
    isChannelCurrErrMsgThere) {
    return false;
  }
  return true;
}

// When "Prev" is clicked in edit channel dialog

function addEditChannelPrevious(current) {
  var prevStep = $(current).parents(".j_channelspopup").find('#prevstep').val();
  if (prevStep != "") {
    $(".j_channelspopup").hide();
    $("#" + prevStep).show();
  }
}

// Get back to channel details' edit ( in both channel add / edit wizard ) 

function backToChannelDetails(current) {
  $(current).parents(".j_channelspopup").hide();
  $("#step1").show();
}

// Get back to currency selection ( in create dialog )

function backTourrencySelection(current) {
  $(current).parents(".j_channelspopup").hide();
  $("#step2").show();
}

function backToUrlSelection(current) {
	  $(current).parents(".j_channelspopup").hide();
	  $("#step4").show();
}
function backToDefaultSettingsSelection(current) {
  $(current).parents(".j_channelspopup").hide();
  $("#step3").show();
}
// Adds channel details in left panel when a new channel is created. We make the channel created the very first entry in the view

function addChannelDetailsInListView(channel, currencies) {
  var $channelListTemplate = $("#channelleftviewtemplate").clone();
  $channelListTemplate.attr('id', "channel" + channel.id);
  var isOdd = $("#channelgridcontent").find(".widget_navigationlist.channels:first").hasClass('odd');
  if (isOdd) {
    $channelListTemplate.addClass('even');
  } else {
    $channelListTemplate.addClass('odd');
  }
  $channelListTemplate.addClass('selected');
  $channelListTemplate.addClass('active');
  $channelListTemplate.find(".widget_navtitlebox").find('.title').text(channel.name);

  $channelListTemplate.find(".widget_navtitlebox").find('.subtitle').text(currencies);
  $channelListTemplate.find(".widget_info_popover").find('.raw_contents').find("#info_bubble_displayname").find(
    '.raw_contents_value').find("#value").text(channel.name);
  $channelListTemplate.find(".widget_info_popover").find('.raw_contents').find("#info_bubble_code").find(
    '.raw_contents_value').find("#value").text(channel.code);
  $channelListTemplate.find(".widget_info_popover").find('.raw_contents').find("#info_bubble_code").find(
    '.raw_contents_value').find("#value").text(channel.code);

  $channelListTemplate.show();
  $("#channelgridcontent").prepend($channelListTemplate);
  var channelsCount = $("#channelgridcontent").find(".channels.widget_navigationlist").size();
  //remove last element if count grater than pagination value
  if (channelsCount > 14) {
    $("#channelgridcontent").find(".widget_navigationlist.channels:last").remove();
  }
  //reset styling
  resetGridRowStyle();
  $("#channelgridcontent").find(
    ".channels:first").click();
  if (channelsCount > 13) {
    $("#click_next").removeClass("nonactive");
	$("#click_next").unbind("click").bind("click", nextClick);
  } 
}

// Gets currencies selected

function getCurrencyArray(channelForm) {
  var currencyCodeArray = new Array();
  $(channelForm).find("#currency_row_container").find('.widget_checkbox').each(function() {
    if ($(this).find("span").attr("class") == "checked") {
      currencyCodeArray.push($(this).attr('currCode'));
    }
  });
  return currencyCodeArray;
}

// Gets a comma separated supported currency codes' string, as in like "INR, USD, EUR" 

function getCurrencyString(currencyArray) {
  var currencies = "";
  var first = 1;

  for (var i = 0; i < currencyArray.length; i++) {
    if (first == 1) {
      first = 0;
    } else {
      if (i != currencyArray.length) {
        currencies += ", ";
      } else {
        currencies;
      }
    }
    currencies += currencyArray[i];
  }
  return currencies;
}

function channelWizardReviewStepUrlUpdate(new_url, default_url, target){
	if(new_url != ""){
		if(new_url == "#"){
			target.html(commmonmessages.noUrlSet);
		}else{
			target.html(new_url);
		}
		
	} else if(typeof default_url !="undefined" && default_url !=""){
		if(default_url =="#"){
			target.html(commmonmessages.noUrlSet + " ("+g_dictionary.labelDefault+")");
		}
		else{
			target.html(default_url+" ("+g_dictionary.labelDefault+")");
		}
		
	}else{
		target.html(g_dictionary.labelNone+" ("+g_dictionary.labelDefault+")");
	}
	
}

// When "Next" is clicked in channel create wizard

function addChannelNext(current) {
  var currentstep = $(current).parents(".j_channelspopup").attr('id');
  var $currentstep = $("#" + currentstep);
  var nextstep = $currentstep.find("#nextstep").val();
  var channelForm = $(current).closest("form");
  var url = "/portal/portal/channels/createchannel";
  $thisDialog = $("#dialog_add_channel");
  $thisDialog.bind("dialogbeforeclose", function(event, ui) {
    $thisDialog.empty();
  });

  if (isChannelFormValid(channelForm, currentstep)) {
    if (currentstep == "step2") {
      $(channelForm).find("#conf_name").text($("#channelName").val());
      $(channelForm).find("#conf_name").attr("title", $("#channelName").val());
      $(channelForm).find("#conf_code").text($("#channelCode").val());
      var fqdnPrefix = $("#channelFQDNPrefix").val();
      $(channelForm).find("#conf_fqdn_prefix").text(getFqdnUrl(fqdnPrefix));
      $(channelForm).find("#conf_fqdn_prefix").attr("title", getFqdnUrl(fqdnPrefix));
      if(isBlank($("#billingGroup").val())){
        $(channelForm).find("#conf_billinggroup").text("Uncategorized");
        $(channelForm).find("#conf_billinggroup").attr("title", "Uncategorized");
      }else{
        $(channelForm).find("#conf_billinggroup").text($("#billingGroup").val());
        $(channelForm).find("#conf_billinggroup").attr("title", $("#billingGroup").val());
      }
        
      $(channelForm).find("#conf_code").attr("title", $("#channelCode").val());
      $(channelForm).find("#conf_channel_description").text($("#channelDescription").val());
      $(channelForm).find("#conf_channel_description").attr("title", $("#channelDescription").val());
      $(channelForm).find("#conf_currencies").text(getCurrencyString(getCurrencyArray(channelForm)));
    }
    if (currentstep == "step3"){
      if("" != $("#channel_locale").val()){
        $(channelForm).find("#channel_default_locale").text($("#channel_locale option:selected").text());
      } else if(typeof $("#channel_locale").data("global_value")!="undefined" && $("#channel_locale").data("global_value")!=""){
  		$(channelForm).find("#channel_default_locale").text($("#channel_locale").data("global_value")+" ("+g_dictionary.labelDefault+")");
  	  } else {
  		$(channelForm).find("#channel_default_locale").text(g_dictionary.labelNone+" ("+g_dictionary.labelDefault+")");
  	  }
      
      if("" != $("#channel_time_zone").val()){
        $(channelForm).find("#channel_default_timezone").text($("#channel_time_zone option:selected").text());
      } else if(typeof $("#channel_time_zone").data("global_value")!="undefined" && $("#channel_time_zone").data("global_value")!=""){
  		$(channelForm).find("#channel_default_timezone").text($("#channel_time_zone").data("global_value")+" ("+g_dictionary.labelDefault+")");
  	  } else {
  		$(channelForm).find("#channel_default_timezone").text(g_dictionary.labelNone+" ("+g_dictionary.labelDefault+")");
  	  }
      if($("#help_desk_email").val()!=""){
	  	$(channelForm).find("#channel_helpdesk_email").text($("#help_desk_email").val());
      } else if(typeof $("#help_desk_email").data("global_value")!="undefined" && $("#help_desk_email").data("global_value")!=""){
	  		$(channelForm).find("#channel_helpdesk_email").text($("#help_desk_email").data("global_value")+" ("+g_dictionary.labelDefault+")");
	  	}else{
	  		$(channelForm).find("#channel_helpdesk_email").text(g_dictionary.labelNone+" ("+g_dictionary.labelDefault+")");
	  	}
      if($("#help_desk_phone").val()!=""){
  	  	$(channelForm).find("#channel_helpdesk_phone").text($("#help_desk_phone").val());
        } else if(typeof $("#help_desk_phone").data("global_value")!="undefined" && $("#help_desk_phone").data("global_value")!=""){
  	  		$(channelForm).find("#channel_helpdesk_phone").text($("#help_desk_phone").data("global_value")+" ("+g_dictionary.labelDefault+")");
  	  	}else{
  	  		$(channelForm).find("#channel_helpdesk_phone").text(g_dictionary.labelNone+" ("+g_dictionary.labelDefault+")");
  	  	}
      
      if($("#channel\\.channelBrandingConfigurations\\.signupAllowed1").is(":checked")){
        $(channelForm).find("#channel_allow_signup").text(g_dictionary.yes);
      }else if($("#channel\\.channelBrandingConfigurations\\.signupAllowed2").is(":checked")){
        $(channelForm).find("#channel_allow_signup").text(g_dictionary.no);
      }
      

      var whiteListCountries = "";
      $("#whitelistcountries").parent().find("li.select2-search-choice").each(function(i){
    	  whiteListCountries = whiteListCountries + $(this).find("div").text()+', ';
      });
      var blackListCountries = "";
      $("#blacklistcountries").parent().find("li.select2-search-choice").each(function(i){
    	  blackListCountries = blackListCountries + $(this).find("div").text()+', ';
    	  
      });
      
      if(whiteListCountries!=""){
    	  $(channelForm).find("#channel_whitelist_countries").text(whiteListCountries.slice(0, -2));
          } else if(typeof $("#whitelistcountries").data("global_value")!="undefined" && $("#whitelistcountries").data("global_value")!=""){
    	  		$(channelForm).find("#channel_whitelist_countries").text($("#whitelistcountries").data("global_value")+" ("+g_dictionary.labelDefault+")");
    	  	}else{
    	  		$(channelForm).find("#channel_whitelist_countries").text(g_dictionary.labelNone+" ("+g_dictionary.labelDefault+")");
    	  	}
      if(blackListCountries!=""){
    	  $(channelForm).find("#channel_blacklist_countries").text(blackListCountries.slice(0, -2));
          } else if(typeof $("#blacklistcountries").data("global_value")!="undefined" && $("#blacklistcountries").data("global_value")!=""){
    	  		$(channelForm).find("#channel_blacklist_countries").text($("#blacklistcountries").data("global_value")+" ("+g_dictionary.labelDefault+")");
    	  	}else{
    	  		$(channelForm).find("#channel_blacklist_countries").text(g_dictionary.labelNone+" ("+g_dictionary.labelDefault+")");
    	  	}
      
      if($("#blacklistdomains").val()!=""){
    	  	$(channelForm).find("#channel_blacklist_email_domains").text($("#blacklistdomains").val());
          } else if(typeof $("#blacklistdomains").data("global_value")!="undefined" && $("#blacklistdomains").data("global_value")!=""){
    	  		$(channelForm).find("#channel_blacklist_email_domains").text($("#blacklistdomains").data("global_value")+" ("+g_dictionary.labelDefault+")");
    	  	}else{
    	  		$(channelForm).find("#channel_blacklist_email_domains").text(g_dictionary.labelNone+" ("+g_dictionary.labelDefault+")");
    	  	}
      if($("#whitelistdomains").val()!=""){
  	  	$(channelForm).find("#channel_whitelist_email_domains").text($("#whitelistdomains").val());
        } else if(typeof $("#whitelistdomains").data("global_value")!="undefined" && $("#whitelistdomains").data("global_value")!=""){
  	  		$(channelForm).find("#channel_whitelist_email_domains").text($("#whitelistdomains").data("global_value")+" ("+g_dictionary.labelDefault+")");
  	  	}else{
  	  		$(channelForm).find("#channel_whitelist_email_domains").text(g_dictionary.labelNone+" ("+g_dictionary.labelDefault+")");
  	  	}
      
         
    }
    if (currentstep == "step4"){
    	var urlArray = ["blog", "contact", "forum", "help", "privacy", "support", "tou"];
    	var urlArrayLength = urlArray.length;
    	for (var i = 0; i < urlArrayLength; i++) {
    	    channelWizardReviewStepUrlUpdate($("#marketing_"+urlArray[i]+"_url").val(), $("#marketing_"+urlArray[i]+"_url").data("global_value"), $(channelForm).find("#channel_url_"+urlArray[i]));
    	}
    	
    }
    if (currentstep == "step5") {

      var channelName = $("#channelName").val();
      var channelNameToDisplay = "<br>";
      var size = channelName.length;
      var maxsize = 50;
      var count = 0;
      while (size > 50) {
        channelNameToDisplay += channelName.substring(count, count + maxsize) + "<br>";
        count = count + maxsize;
        size = size - 50;
      }

      channelNameToDisplay += channelName.substring(count) + "<br>";

      $("#step6").find("#successmessage").append(channelNameToDisplay);
      //$("#spinning_wheel5").show();
      var serializedForm = $(channelForm).serialize();
      $.ajax({
        type: "POST",
        url: url,
        cache: false,
        data: serializedForm,
        success: function(channel) {
          addChannelDetailsInListView(channel, getCurrencyString(getCurrencyArray(channelForm)));
          $(".j_channelspopup").hide();
          $("#non_list").hide();
          $("#" + nextstep).show();
          updateTopMessagePanel(commmonmessages.channel_add_success, "success");
        },
        error: function(XMLHttpRequest) {
          updateTopMessagePanel(commmonmessages.channel_add_error, "error");
          
          if (XMLHttpRequest.status === AJAX_FORM_VALIDATION_FAILED_CODE) {
            displayAjaxFormError(XMLHttpRequest,
              "channelForm",
              "main_addnew_formbox_errormsg");
          } else if (XMLHttpRequest.status === CODE_NOT_UNIQUE_ERROR_CODE) {
            popUpDialogForAlerts("alert_dialog", i18n.errors.channels.channel_name_not_unique);
          } else {
            popUpDialogForAlerts("alert_dialog", i18n.errors.channels.failed_create_channel);
          }
        },
        complete: function() {
          $("#spinning_wheel5").hide();
        }
      });

    } else if (currentstep == "step6") {
      $thisDialog.dialog("close");
      $thisDialog.find(".dialog_formcontent").empty();
      $(".select2-hidden-accessible").hide();
    } else {
      $(".j_channelspopup").hide();
      $("#" + nextstep).show();
    }
  } else {
	  if($currentstep.find("input.error") !=null){
	  var top = $currentstep.find("input.error").offset().top - $thisDialog.offset().top - 120;
	  $currentstep.find('.js_scroll_container').animate({scrollTop: top},'slow');
	  }
  }
}

// Updates the channel listing in the left hand panel of view channel

function editChannelDetailsInListViiew(channelId, channelName, channelCode) {
  var $chanelEdited = $("#channelgridcontent").find("#channel" + channelId);
  $chanelEdited.find(".widget_navtitlebox").find(".title").text(channelName);
  $chanelEdited.find(".widget_info_popover").find('.raw_contents').find("#info_bubble_displayname").find(
    '.raw_contents_value').find("#value").text(channelName);
  $chanelEdited.find(".widget_info_popover").find('.raw_contents').find("#info_bubble_code").find('.raw_contents_value')
    .find("#value").text(channelCode);
  $("#channelgridcontent").find("#channel" + channelId).click();
}

// When "Next" is clcked in the channel "edit' wizard 

function editChannelNext(current) {
  var currentstep = $(current).parents(".j_channelspopup").attr('id');
  var $currentstep = $("#" + currentstep);
  var nextstep = $currentstep.find("#nextstep").val();
  var channelForm = $(current).closest("form");
  var url = "/portal/portal/channels/editchannel";
  var channelId = $("li[id^='channel'].selected.channels").attr('id').substr(7);
  $thisDialog = $("#dialog_edit_channel");
  $thisDialog.bind("dialogbeforeclose", function(event, ui) {
    $thisDialog.empty();
  });
 
  if (isChannelFormValid(channelForm, currentstep)) {
    if (currentstep == "step2") {
      $(channelForm).find("#conf_edit_name").text($("#channelName").val());
      $(channelForm).find("#conf_edit_name").attr("title", $("#channelName").val());
      $(channelForm).find("#conf_edit_code").text($("#channelCode").val());
      $(channelForm).find("#conf_edit_code").attr("title", $("#channelCode").val());
      $(channelForm).find("#conf_edit_fqdn_prefix").text(getFqdnUrl($("#channelFQDNPrefix").val()));
      $(channelForm).find("#conf_edit_fqdn_prefix").attr("title", getFqdnUrl($("#channelFQDNPrefix").val()));
      if(isBlank($("#billingGroup").val())){
        $(channelForm).find("#conf_edit_billinggroup").text("Uncategorized");
        $(channelForm).find("#conf_edit_billinggroup").attr("title", "Uncategorized");
      }else{
        $(channelForm).find("#conf_edit_billinggroup").text($("#billingGroup").val());
        $(channelForm).find("#conf_edit_billinggroup").attr("title", $("#billingGroup").val());  
      }
      $(channelForm).find("#conf_edit_channel_description").text($("#channelDescription").val());
      $(channelForm).find("#conf_edit_channel_description").attr("title", $("#channelDescription").val());
      $(channelForm).find("#conf_currencies").text(getCurrencyString(getCurrencyArray(channelForm)));
    }
    if (currentstep == "step3"){

    	if("" != $("#channel_locale").val()){
            $(channelForm).find("#channel_default_locale").text($("#channel_locale option:selected").text());
          } else if(typeof $("#channel_locale").data("global_value")!="undefined" && $("#channel_locale").data("global_value")!=""){
      		$(channelForm).find("#channel_default_locale").text($("#channel_locale").data("global_value")+" ("+g_dictionary.labelDefault+")");
      	  } else {
      		$(channelForm).find("#channel_default_locale").text(g_dictionary.labelNone+" ("+g_dictionary.labelDefault+")");
      	  }
          
          if("" != $("#channel_time_zone").val()){
            $(channelForm).find("#channel_default_timezone").text($("#channel_time_zone option:selected").text());
          } else if(typeof $("#channel_time_zone").data("global_value")!="undefined" && $("#channel_time_zone").data("global_value")!=""){
      		$(channelForm).find("#channel_default_timezone").text($("#channel_time_zone").data("global_value")+" ("+g_dictionary.labelDefault+")");
      	  } else {
      		$(channelForm).find("#channel_default_timezone").text(g_dictionary.labelNone+" ("+g_dictionary.labelDefault+")");
      	  }
          if($("#help_desk_email").val()!=""){
    	  	$(channelForm).find("#channel_helpdesk_email").text($("#help_desk_email").val());
          } else if(typeof $("#help_desk_email").data("global_value")!="undefined" && $("#help_desk_email").data("global_value")!=""){
    	  		$(channelForm).find("#channel_helpdesk_email").text($("#help_desk_email").data("global_value")+" ("+g_dictionary.labelDefault+")");
    	  	}else{
    	  		$(channelForm).find("#channel_helpdesk_email").text(g_dictionary.labelNone+" ("+g_dictionary.labelDefault+")");
    	  	}
          if($("#help_desk_phone").val()!=""){
      	  	$(channelForm).find("#channel_helpdesk_phone").text($("#help_desk_phone").val());
            } else if(typeof $("#help_desk_phone").data("global_value")!="undefined" && $("#help_desk_phone").data("global_value")!=""){
      	  		$(channelForm).find("#channel_helpdesk_phone").text($("#help_desk_phone").data("global_value")+" ("+g_dictionary.labelDefault+")");
      	  	}else{
      	  		$(channelForm).find("#channel_helpdesk_phone").text(g_dictionary.labelNone+" ("+g_dictionary.labelDefault+")");
      	  	}
        
        if($("#channel\\.channelBrandingConfigurations\\.signupAllowed1").is(":checked")){
            $(channelForm).find("#channel_allow_signup").text(g_dictionary.yes);
          }else if($("#channel\\.channelBrandingConfigurations\\.signupAllowed2").is(":checked")){
            $(channelForm).find("#channel_allow_signup").text(g_dictionary.no);
          }
        var whiteListCountries = "";
        $("#whitelistcountries").parent().find("li.select2-search-choice").each(function(i){
      	  whiteListCountries = whiteListCountries + $(this).find("div").text()+', ';
        });
        var blackListCountries = "";
        $("#blacklistcountries").parent().find("li.select2-search-choice").each(function(i){
      	  blackListCountries = blackListCountries + $(this).find("div").text()+', ';
      	  
        });
        
        if(whiteListCountries!=""){
      	  $(channelForm).find("#channel_whitelist_countries").text(whiteListCountries.slice(0, -2));
            } else if(typeof $("#whitelistcountries").data("global_value")!="undefined" && $("#whitelistcountries").data("global_value")!=""){
      	  		$(channelForm).find("#channel_whitelist_countries").text($("#whitelistcountries").data("global_value")+" ("+g_dictionary.labelDefault+")");
      	  	}else{
      	  		$(channelForm).find("#channel_whitelist_countries").text(g_dictionary.labelNone+" ("+g_dictionary.labelDefault+")");
      	  	}
        if(blackListCountries!=""){
      	  $(channelForm).find("#channel_blacklist_countries").text(blackListCountries.slice(0, -2));
            } else if(typeof $("#blacklistcountries").data("global_value")!="undefined" && $("#blacklistcountries").data("global_value")!=""){
      	  		$(channelForm).find("#channel_blacklist_countries").text($("#blacklistcountries").data("global_value")+" ("+g_dictionary.labelDefault+")");
      	  	}else{
      	  		$(channelForm).find("#channel_blacklist_countries").text(g_dictionary.labelNone+" ("+g_dictionary.labelDefault+")");
      	  	}

        if($("#blacklistdomains").val()!=""){
      	  	$(channelForm).find("#channel_blacklist_email_domains").text($("#blacklistdomains").val());
            } else if(typeof $("#blacklistdomains").data("global_value")!="undefined" && $("#blacklistdomains").data("global_value")!=""){
      	  		$(channelForm).find("#channel_blacklist_email_domains").text($("#blacklistdomains").data("global_value")+" ("+g_dictionary.labelDefault+")");
      	  	}else{
      	  		$(channelForm).find("#channel_blacklist_email_domains").text(g_dictionary.labelNone+" ("+g_dictionary.labelDefault+")");
      	  	}
        if($("#whitelistdomains").val()!=""){
    	  	$(channelForm).find("#channel_whitelist_email_domains").text($("#whitelistdomains").val());
          } else if(typeof $("#whitelistdomains").data("global_value")!="undefined" && $("#whitelistdomains").data("global_value")!=""){
    	  		$(channelForm).find("#channel_whitelist_email_domains").text($("#whitelistdomains").data("global_value")+" ("+g_dictionary.labelDefault+")");
    	  	}else{
    	  		$(channelForm).find("#channel_whitelist_email_domains").text(g_dictionary.labelNone+" ("+g_dictionary.labelDefault+")");
    	  	}
      }
    if (currentstep == "step4"){
    	var urlArray = ["blog", "contact", "forum", "help", "privacy", "support", "tou"];
    	var urlArrayLength = urlArray.length;
    	for (var i = 0; i < urlArrayLength; i++) {
    	    channelWizardReviewStepUrlUpdate($("#marketing_"+urlArray[i]+"_url").val(), $("#marketing_"+urlArray[i]+"_url").data("global_value"), $(channelForm).find("#channel_url_"+urlArray[i]));
    	}
    	
    }
    if (currentstep == "step5") {

      var channelName = $("#channelName").val();
      var channelNameToDisplay = "<br>";
      var size = channelName.length;
      var maxsize = 50;
      var count = 0;
      while (size > 50) {
        channelNameToDisplay += channelName.substring(count, count + maxsize) + "<br>";
        count = count + maxsize;
        size = size - 50;
      }
      channelNameToDisplay += channelName.substring(count) + "<br>";

      $("#step6").find("#successmessage").append(channelNameToDisplay);
      var serializedForm = $(channelForm).serialize();
      $.ajax({
        type: "POST",
        url: url,
        cache: false,
        data: serializedForm,
        success: function(channel) {
          editChannelDetailsInListViiew(channelId, $thisDialog.find("#channelName").val(), $thisDialog.find(
            "#channelCode").val());
          $(".j_channelspopup").hide();
          $("#" + nextstep).show();
          updateTopMessagePanel(commmonmessages.channel_edit_success, "success");
        },
        error: function(XMLHttpRequest) {
          updateTopMessagePanel(commmonmessages.channel_edit_error, "error");
          
          if (XMLHttpRequest.status === AJAX_FORM_VALIDATION_FAILED_CODE) {
            displayAjaxFormError(XMLHttpRequest,
              "channelForm",
              "main_addnew_formbox_errormsg");
          } else if (XMLHttpRequest.status === CODE_NOT_UNIQUE_ERROR_CODE) {
            popUpDialogForAlerts("alert_dialog", i18n.errors.common.codeNotUnique);
          } else {
            popUpDialogForAlerts("alert_dialog", i18n.errors.channels.failed_edit_channel);
          }
        }
      });

    } else if (currentstep == "step6") {
      $thisDialog.dialog("close");
      $thisDialog.find(".dialog_formcontent").empty();
      
      } else {
      $(".j_channelspopup").hide();
      $("#" + nextstep).show();
    }
  } else {
	if($currentstep.find("input.error") !=null){
    var top = $currentstep.find("input.error").offset().top - $thisDialog.offset().top - 120;
    $currentstep.find('.js_scroll_container').animate({scrollTop: top},'slow');
	}
  }
}


// Add/edit channel wizard last step link.
$("#viewchanneldetails_configure").live("click", function(event) {
  $("#dialog_add_channel").dialog("close");
  $("#dialog_edit_channel").dialog("close");
  $("#dialog_add_channel").empty();
  $("#dialog_edit_channel").empty();
});
$(".close_channel_wizard").live("click", function(event) {
	initDialog("dialog_info", 390);
	  $thisDialog = $("#dialog_info");
	  $thisDialog.html(g_dictionary.closeWizardConfirmation);
	  $thisDialog.dialog('option', 'buttons', {
	    "No":function(){
	    	$(this).dialog("close");
	    },
	    "Yes": function() {
		      $(this).dialog("close");
		      $("#dialog_add_channel").dialog("close");
			  $("#dialog_edit_channel").dialog("close");
			  $("#dialog_add_channel").empty();
			  $("#dialog_edit_channel").empty();
		    }
	  });
	  dialogButtonsLocalizer($thisDialog, {
	    'Yes': g_dictionary.yes,
	    'No': g_dictionary.no
	  });
	  $thisDialog.bind("dialogbeforeclose", function(event, ui) {
	    $thisDialog.empty();
	  });
	  $thisDialog.dialog("open");
	  
	});

var loadingBarHtml =
  '<div class="infinite_scrollbarbox" id="loading_intimation"><div class="infinite_loading"></div></div>';
var scrollUrlHitMap = {};

$("#bundles_detail_area").scroll(function() {
  if ($("#bundles_detail_area").scrollTop() + $("#bundles_detail_area").height() >= $(this)[0].scrollHeight) {
    var gotAll = $("#bundles_detail_area").attr("gotall");
    var which = $("#bundles_detail_area").attr("which");
    if (gotAll == "true") {
      return;
    }
    // Check if the url has already been hit, then skip it..
    var lastBundleNo = $("#bundles_detail_area").find("div[id^='bundleNo']:last").attr("id").substr(8);
    var url = "/portal/portal/channels/getnextsetofbundles";
    var channelId = $("li[id^='channel'].selected.channels").attr('id').substr(7);
    var editpriceisvalid = "0";
    if ($("#planning_for_first_time").attr("value") == "") {
      editpriceisvalid = "1";
    }
    var scrollUrlKey = channelId + KEY_VALUE_ITEM_SEPERATOR + lastBundleNo + KEY_VALUE_ITEM_SEPERATOR +
      which + KEY_VALUE_ITEM_SEPERATOR + editpriceisvalid;
    if (scrollUrlHitMap[scrollUrlKey] != undefined) {
      return;
    }
    scrollUrlHitMap[scrollUrlKey] = true;
    $("#bundles_detail_area").find("div[id^='bundleNo']:last").after(loadingBarHtml);
    $.ajax({
      type: "GET",
      url: url,
      async: false,
      cache: false,
      data: {
        "channelId": channelId,
        "lastBundleNo": lastBundleNo,
        "which": which,
        "editpriceisvalid": editpriceisvalid
      },
      dataType: "html",
      success: function(result) {
        $("#bundles_detail_area").find("#loading_intimation").remove();
        if (result == null || result.trim().length == 0) {
          $("#bundles_detail_area").attr("gotall", "true");
        } else {
          $("#bundles_detail_area").find("div[id^='bundleNo']:last").after(result);
          // Had to add bindings to cater to the action items..Because we are getting a static
          // html, we need to add bindings again for the newly arrived entities.
          $("div[id^='action_per_product_bundle']").bind("mouseover", function(event) {
            $(this).find('#per_bundle_action_menu').show();
          });
          $("div[id^='action_per_product_bundle']").bind("mouseout", function(event) {
            $(this).find('#per_bundle_action_menu').hide();
          });
        }
      },
      error: function(XMLHttpRequest) {}
    });
  }
});

function getFullListingOfCharges(event, bundleId) {
  var data = {};
  data["currentHistoryPlanned"] = $("#currentHistoryPlanned").attr("value");
  data["channelId"] = $("li[id^='channel'].selected.channels").attr('id').substr(7);
  if (bundleId != undefined) {
    data["bundleId"] = bundleId;
  }
  if ($("#currentHistoryPlanned").attr("value") == "history") {
    data["dateFormat"] = $("#historyDateFormat").val();
    data["historyDate"] = $("#catalog_history_dates").val();
  }
  $.ajax({
    type: "GET",
    url: "/portal/portal/channels/getfulllistingofcharges",
    data: data,
    dataType: "html",
    async: false,
    success: function(html) {
      if (bundleId != undefined) {
        initDialog("dialog_bundle_pricing", 782);
        var $thisDialog = $("#dialog_bundle_pricing");
      } else {
        initDialog("dialog_utility_pricing", 782);
        var $thisDialog = $("#dialog_utility_pricing");
      }
      $thisDialog.html("");
      $thisDialog.html(html);
      $thisDialog.dialog('option', 'buttons', {
        "OK": function() {
          $(this).dialog("close");
          $thisDialog.empty();
        }
      });
      dialogButtonsLocalizer($thisDialog, {
        'OK': g_dictionary.dialogOK
      });
      $thisDialog.bind("dialogbeforeclose", function(event, ui) {
        $thisDialog.empty();
      });
      //		       $thisDialog.find(".widget_details_actionbox").remove();
      //		       $thisDialog.find(".widget_subactions.action_menu_container").remove();
      //		       $thisDialog.find(".widget_grid_cell .moretabbutton").parent().remove();
      $thisDialog.dialog("open");
    },
    error: function() {
      // need to do
    }
  });
}

function syncChannel(event, current) {
  var divId = $("li[id^='channel'].selected.channels").attr('id');
  var channelId = divId.substr(7);
  initDialog("dialog_sync_channel", 390);
  var $thisDialog = $("#dialog_sync_channel");
  $thisDialog.data("height.dialog", 100);
  $thisDialog.dialog('option', 'buttons', {
    "OK": function() {
      var url = "/portal/portal/channels/syncchannel";
      $.ajax({
        type: "POST",
        url: url,
        async: false,
        cache: false,
        data: {
          "channelId": channelId
        },
        dataType: "html",
        success: function(result) {
          $thisDialog.dialog("close");
          if (result == "failure") {
            popUpDialogForAlerts("alert_dialog", i18n.errors.channels.failed_to_sync_channel);
          }
          var channelDivIdRef = "#channel" + channelId;
          $(channelDivIdRef).click();
          $("#catalog_tab").click();
        },
        error: function(XMLHttpRequest) {
          popUpDialogForAlerts("alert_dialog", i18n.errors.channels.failed_to_sync_channel);
        }
      });
      $(this).dialog("close");
    },
    "Cancel": function() {
      $(this).dialog("close");
    }
  });
  dialogButtonsLocalizer($thisDialog, {
    'OK': g_dictionary.dialogOK,
    'Cancel': g_dictionary.dialogCancel
  });
  $thisDialog.dialog("open");
}

function changeChannelServiceInstances(){
	
}

function showChannelServiceSettings(){
	
}

function refreshChannelServiceSettings(){
	var selectedServiceInstance = $("#selectedInstance option:selected").val()
	var channelId = $("li[id^='channel'].selected.channels").attr('id').substr(7);		
	var actionurl = "/portal/portal/channels/servicesettings";
	if(selectedServiceInstance != undefined && selectedServiceInstance != null){
		$("#spinning_wheel").show();
		$.ajax({
			type: "GET",
			url: actionurl,
			data: {
				channelId: channelId,
				instanceUUID: selectedServiceInstance
			},
			dataType: "html",
			success: function(html) {
				$("#channelServiceSettingsDiv").html("");
				$("#channelServiceSettingsDiv").html(html);
				$("#spinning_wheel").hide();
			},
			error: function() {
				$("#spinning_wheel").hide();
			}
		});
	}else{
		$("#channelServiceSettingsDiv").html("");
	}
}

function editChannelServiceSettings(current){
	 var serviceSettingsChanelID = $('#serviceSettingsChanelID').val();
	 var serviceSettingsInstanceUUID = $('#serviceSettingsInstanceUUID').val();
	 console.log(serviceSettingsChanelID,'....',serviceSettingsInstanceUUID);
	  var actionurl = "/portal/portal/channels/editservicesettings";
      var $thisPanel = $("#editServiceChannelSettingsDiv");
	 $("#spinning_wheel").show();
	 $.ajax({ 
	    type: "GET",
	    url: actionurl,
	    data: {
	    	channelId: serviceSettingsChanelID,
	    	instanceUUID: serviceSettingsInstanceUUID
	    },
	    dataType: "html",
	    success: function(html) {
	      $("#editServiceChannelSettingsDiv").html("");
	      $("#editServiceChannelSettingsDiv").html(html);
	      $("#spinning_wheel").hide();
	      //$thisPanel.dialog({ height: 100, width : 600 });
	      $thisPanel.dialog("option", "title", channelServiceSettingDialogTitle);
	      $thisPanel.dialog('option', 'buttons', {
    	  "OK": function() {
    			$('#channelServiceSettingsForm').submit();
    		  	//saveChannelServiceSettings($(this));
    	      },
    	      "Cancel": function() {
    	        $(this).dialog("close");
    	      }
	      }); 
	      dialogButtonsLocalizer($thisPanel, {
	        'OK': g_dictionary.dialogOK,
	        'Cancel': g_dictionary.dialogCancel
	    });
	      
	    },
	    error: function() {
	      $("#spinning_wheel").hide();
	    }
	  });
	 
	 $thisPanel.dialog("open");
}

function saveChannelServiceSettings(){
	var actionurl = "/portal/portal/channels/editservicesettings";
	$.ajax({
		    type: "POST",
		    url: actionurl,
		    data:$('#channelServiceSettingsForm').serialize(),
		    success: function(data) {
		    	console.log('..success');
		    	var $thisDialog = $("#editServiceChannelSettingsDiv");
		    	$thisDialog.dialog("close");
 	        	refreshChannelServiceSettings();
		    },
		    error: function(jqXHR,textStatus,errorThrown) {
		    	console.log(textStatus,'..fail',errorThrown);
		    }
	});
}

function formatFromCurrency(value, thouSeparator, decSeparator) {
  var decSeparator = decSeparator == undefined ? "." : decSeparator;
  var thouSeparator = thouSeparator == undefined ? "," : thouSeparator;
  value = value.split(thouSeparator).join("");
  value = value.split(decSeparator).join(".");
  return value;
}
function getFqdnUrl(fqdnPrefix){
  var publicProtocol = $("#publicProtocol").val();
  var publicHost = $("#publicHost").val();
  var publicPort = $("#publicPort").val();
  var fqdnUrl = "";
  if(fqdnPrefix.length>0){
    fqdnUrl += fqdnPrefix + ".";
  }
  fqdnUrl += publicHost;
  if("80"!=publicPort){
    fqdnUrl += ":" +publicPort;
  }
  return fqdnUrl;
}


$("#service_controls_tab").unbind("click").bind("click", function (event) {
    $('#details_tab').removeClass('active').addClass("nonactive");
    $('#currencies_tab').removeClass('active').addClass("nonactive"); 
    $('#catalog_tab').removeClass('active').addClass("nonactive");
    $('#urls_tab').removeClass('active').addClass("nonactive");
    $('#branding_tab').removeClass('active').addClass("nonactive");
    $("#branding_actions").hide();

    $('#details_content').hide();
    $("#channel_urls").hide();
    $('#currencies_content').hide();
    $("#branding_tab_details").hide();
    $('#action_currency').hide();
    $("#catalog_content").hide();
    $('#catalog_links').hide();
    $("#second_line_under_planned").hide();
    $("#main_action_box").attr("style", "height: 35px");
    $('#service_controls_tab').removeClass('nonactive').addClass("active");
    $('#action_service_settings').show();
    $('#service_controls_content').show();
});


$("#details_tab").unbind("click").bind("click", function (event) {
    $('#details_tab').removeClass('nonactive').addClass("active");
    $('#currencies_tab').removeClass('active').addClass("nonactive");
    $('#catalog_tab').removeClass('active').addClass("nonactive");
    $('#urls_tab').removeClass('active').addClass("nonactive");
    $('#branding_tab').removeClass('active').addClass("nonactive");
        
    $('#details_content').show();

    $('#currencies_content').hide();
    $("#branding_actions").hide();
    $("#channel_urls").hide();
    $("#branding_tab_details").hide();
    $('#catalog_links').hide();
    $("#catalog_content").hide();
    $("#second_line_under_planned").hide();
    $('#action_currency').hide();
    $("#main_action_box").attr("style", "height: 30px");
    $('#service_controls_tab').removeClass('active').addClass("nonactive");
    $('#action_service_settings').hide();
    $('#service_controls_content').hide();
 });

 $("#currencies_tab").unbind("click").bind("click", function (event) {
     $('#details_tab').removeClass('active').addClass("nonactive");
     $('#urls_tab').removeClass('active').addClass("nonactive");
     $('#currencies_tab').removeClass('nonactive').addClass("active"); 
     $('#branding_tab').removeClass('active').addClass("nonactive");
     $('#catalog_tab').removeClass('active').addClass("nonactive");

     $('#details_content').hide();
     $("#branding_actions").hide();
     $("#channel_urls").hide();
     $("#branding_tab_details").hide();
     $('#currencies_content').show();
     $('#action_currency').show();
     $("#catalog_content").hide();
     $('#catalog_links').hide();
     $("#second_line_under_planned").hide();
     $("#main_action_box").attr("style", "height: 30px");
     $('#service_controls_tab').removeClass('active').addClass("nonactive");
     $('#action_service_settings').hide();
     $('#service_controls_content').hide();
 });

 $("#catalog_tab").unbind("click").bind("click", function (event) {
   preViewCatalogPlanned(null, $("#planned_or_not"));
   });

 $("#action_currency").unbind("mouseover").bind("mouseover", function (event) {
     $('#currency_action_menu').show();
   });

 $("#action_currency").unbind("mouseout").bind("mouseout", function (event) {
     $('#currency_action_menu').hide();
   });
 
 $("#current_branding").unbind("mouseover").bind("mouseover", function (event) {
     $('#current_branding_action_menu').show();
   });

 $("#current_branding").unbind("mouseout").bind("mouseout", function (event) {
     $('#current_branding_action_menu').hide();
   });
 $("#unpublished_branding").unbind("mouseover").bind("mouseover", function (event) {
     $('#unpublished_branding_action_menu').show();
   });

 $("#unpublished_branding").unbind("mouseout").bind("mouseout", function (event) {
     $('#unpublished_branding_action_menu').hide();
   });

 $("#show_current_channel_branding").live("click", function(e){
	 $("#unpublished_branding_tab_details").hide();
	 $("#branding_tab_details").show();
	 $(this).find("a").css("color", "#000");
	 $("#show_unpublished_channel_branding a").css("color", "#2C8BBC");
 });
 $("#show_unpublished_channel_branding").live("click", function(e){
	 $("#branding_tab_details").hide();
	 $("#unpublished_branding_tab_details").show();
	 $(this).find("a").css("color", "#000");
	 $("#show_current_channel_branding a").css("color", "#2C8BBC");
 });
 function preViewCatalogPlanned(event){
     $('#details_tab').removeClass('active').addClass("nonactive");
     $('#currencies_tab').removeClass('active').addClass("nonactive");
     $('#urls_tab').removeClass('active').addClass("nonactive");
     $('#catalog_tab').removeClass('nonactive').addClass("active");
     $('#branding_tab').removeClass('active').addClass("nonactive");
     $('#details_content').hide();
     $("#channel_urls").hide();
     $("#branding_tab_details").hide();
     $('#currencies_content').hide();
     $("#branding_actions").hide();

     $('#catalog_links').show();
     $("#catalog_content").show();
     $('#action_currency').hide();
     $('#plan_date_picker').show();

     $("#catalog_current_tab").find("a").attr("style", "color: #2C8BBC");
     $("#catalog_planned_tab").find("a").attr("style", "color: #000");
     $("#catalog_history_tab").find("a").attr("style", "color: #2C8BBC");
     $("#second_line_under_planned").closest(".widget_details_actionbox").attr("style", "height: auto");
     $("#second_line_under_planned").show();
     viewCatalogPlanned();
     $('#service_controls_tab').removeClass('active').addClass("nonactive");
     $('#action_service_settings').hide();
     $('#service_controls_content').hide();
   }

 
 $("#urls_tab").unbind("click").bind("click", function (event) {
	    $('#details_tab').removeClass('active').addClass("nonactive");
	    $('#currencies_tab').removeClass('active').addClass("nonactive"); 
	    $('#catalog_tab').removeClass('active').addClass("nonactive");
	    $('#service_controls_tab').removeClass('active').addClass("nonactive");
	    $('#branding_tab').removeClass('active').addClass("nonactive");
	    $('#urls_tab').removeClass('nonactive').addClass("active");
	    

	    $('#details_content').hide();
	    $("#branding_actions").hide();
	    $('#currencies_content').hide();
	    $('#action_currency').hide();
	    $("#catalog_content").hide();
	    $('#catalog_links').hide();
	    $("#second_line_under_planned").hide();
	    $("#main_action_box").attr("style", "height: 35px");
	    
	    $('#action_service_settings').hide();
	    $('#service_controls_content').hide();
	    $("#branding_tab_details").hide();
	    $("#channel_urls").show();
	    
	});

 
 function brandingTabToggle(){
	    $('#details_tab').removeClass('active').addClass("nonactive");
	    $('#currencies_tab').removeClass('active').addClass("nonactive"); 
	    $('#catalog_tab').removeClass('active').addClass("nonactive");
	    $('#service_controls_tab').removeClass('active').addClass("nonactive");
	    $('#urls_tab').removeClass('active').addClass("nonactive");
	    $('#branding_tab').removeClass("nonactive").addClass("active");

	    $('#details_content').hide();
	    $('#currencies_content').hide();
	    $('#action_currency').hide();
	    $("#catalog_content").hide();
	    $('#catalog_links').hide();
	    $("#second_line_under_planned").hide();
	    $("#main_action_box").attr("style", "height: 35px");
	    
	    $('#action_service_settings').hide();
	    $('#service_controls_content').hide();
	    $("#channel_urls").hide();
	    $("#branding_tab_details").show();
	    $("#show_unpublished_channel_branding a").css("color", "#2C8BBC");
	    $("#show_current_channel_branding a").css("color", "#000");
	    $("#branding_actions").show();
 }
 $("#branding_tab").unbind("click").bind("click", function (event) {
	 brandingTabToggle();
	    
	});

 function setupChannelBrandingPreview(channelCode,currentLoggedInUserParam){
	$.ajax({
          type: "GET",
          url: "/portal/portal/"+currentLoggedInUserParam+"/loggedout",
          success: function(result) {
           	 window.location.href= "/portal/portal/login?previewchannelcode="+channelCode;
          },
          error: function(XMLHttpRequest) {
            
          }
        });
 }
 
function publishChannelBranding(event, current) {
	    var channelId = $(current).attr('id').substr(7);
	    
	    $.ajax({
	          type: "GET",
	          url: "/portal/portal/channels/publish/"+channelId,
	          success: function(response) {
	        	  if(response.hasOwnProperty("success")) {
	        		  $("#channelgridcontent li.active").find("#is_unpublished_icon").hide();
	        		  viewChannel($("#channelgridcontent li.active"));
	        		  brandingTabToggle();
		        	  updateTopMessagePanel(commmonmessages.editChannelBrandingSuccess, "success");
		        	  if(typeof previewModeChannelId != "undefined" && previewModeChannelId == channelId){
		        		  exitChannelBrandingPreviewMode();
		        	  }
		        	  
	        	  }else{
	        		  updateTopMessagePanel(commmonmessages.editChannelBrandingSuccess, "error");
	        	  }
	          },
	          error: function(XMLHttpRequest) {
	        	  updateTopMessagePanel(commmonmessages.editChannelBrandingSuccess, "error");
	          }
	        });
 }
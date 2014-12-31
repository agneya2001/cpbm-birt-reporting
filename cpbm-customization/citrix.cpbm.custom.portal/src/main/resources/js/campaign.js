/*
*  Copyright � 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/
$(document).ready(function() {
  
  jQuery.validator.addMethod("amount", function(value, element) {
    if ($("#campaignOffs input:radio:checked").val() == "MoneyOff") {
      if (value >= 0) {
        return true;
      } else {
        return false;
      }
    } else {
      return true;
    }
  });
  jQuery.validator.addMethod("pomooff", function(value, element) {
    if ($("#campaignOffs input:radio:checked").val() == "TimeOff") {
      if (value >= 0 && value <= 100) {
        return true;
      } else {
        return false;
      }
    } else {
      return true;
    }
  });
  jQuery.validator.addMethod("indefinite", function(value, element) {
    if ($('#indefinite:checked') != true && value >= 0) {
      return true;
    } else {
      return false;
    }
  });
  jQuery.validator.addMethod("unlimited", function(value, element) {
    if ($('#unlimited:checked') != true && value >= 0) {
      return true;
    } else {
      return false;
    }
  });

  jQuery.validator.addMethod('noSpacesAndLengthLmitCheck', function(value, element, len_limit) {
    return value.length > 0 && value.length <= 20 && /^[a-zA-Z0-9_:\[\]-]+$/.test(value);
  }, i18n.campaigns.promocodeinvalid);

  $.validator.addClassRules("priceRequired", {
    twoDecimal: true
  });
  $.validator
    .addMethod(
      "twoDecimal",
      function(value, element) {
        $(element).rules("add", {
          number: true,
          min: 0,
          max: 99999999.99

        });

        isPriceValid = (value != "" && isNaN(value) == false);
        if (isPriceValid == false) {
          return false;
        }
        return true;
      },
      i18n.campaigns.amountoffrequired);

  $("#campaignPromotionsForm").validate({
    //debug : true,
    success: "valid",
    ignoreTitle: true,
    rules: {
      "campaignPromotion.code": {
        required: true,
        minlength: 1,
        noSpacesAndLengthLmitCheck: true,
        remote: {
          url: '/portal/portal/products/validateCode',
          async: false
        }
      },
      "campaignPromotion.startDateDisplay": {
        required: true,
        dateRange: true
      },
      "campaignPromotion.endDateDisplay": {
        dateRange: true
      },
      "campaignPromotion.title": {
        required: true,
        minlength: 1
      },
      "promoCode": {
        required: true,
        minlength: 1,
        noSpacesAndLengthLmitCheck: true,
        remote: {
          url: '/portal/portal/promotions/validate_promoCode',
          async: false
        }
      },

      "campaignPromotion.durationDays": {
        required: true,
        min: function() {
          if ($("#unlimitedUsage").is(':checked'))
            return 0;
          return 1;
        },
        digits: true
      },
      "campaignPromotion.maxAccounts": {
        required: true,
        min: function() {
          if ($("#unlimitedAccounts").is(':checked'))
            return 0;
          return 1;
        },
        digits: true
      },
      "campaignPromotion.percentOff": {
        required: true,
        range: [0, 100]
      }
    },
    messages: {
      "campaignPromotion.code": {
        required: i18n.campaigns.campaigncoderequired,
        noSpacesAndLengthLmitCheck: i18n.campaigns.promocodeinvalid,
        remote: i18n.errors.common.codeNotUnique
      },
      "campaignPromotion.title": {
        required: i18n.campaigns.promotitlerequired
      },
      "promoCode": {
        required: i18n.campaigns.promocoderequired,
        noSpacesAndLengthLmitCheck: i18n.campaigns.promocodeinvalid,
        remote: i18n.errors.common.codeNotUnique
      },
      "campaignPromotion.durationDays": {
        required: i18n.campaigns.durationrequired,
        digits: i18n.campaigns.digitsrequired,
        min: i18n.campaigns.digitsmin
      },
      "campaignPromotion.percentOff": {
        required: i18n.campaigns.percentoffrequired,
        range: i18n.campaigns.percentoffrange
      },
      "campaignPromotion.maxAccounts": {
        required: i18n.campaigns.maxaccountsrequired,
        digits: i18n.campaigns.digitsrequired,
        min: i18n.campaigns.digitsmin
      },
      "campaignPromotion.endDateDisplay": {
        dateRange: i18n.campaigns.campaignEnterValidDateRange
      },
      "campaignPromotion.startDateDisplay": {
        required: i18n.campaigns.startdaterequired,
        dateRange: i18n.campaigns.campaignEnterValidDateRange
      }

    },
    errorPlacement: function(error, element) {
      var name = element.attr('id');
      name = ReplaceAll(name, ".", "\\.");
      if (name != "") {
        if (name.startsWith("discountAmountMap")) {
          $("#discountAmountMapError").show();
        } else {
          error.appendTo("#" + name + "Error");
        }
      }
    }

  });



  $("#trialPromotion").live("click", function(event) {
    if ($(this).is(':checked')) {
      $('#campaignPromotion\\.trial').val(true);
      $('#discountTypeRadios').hide();
      $('#discountTypeRadiosEditDiv').hide();
      $('#amountOffDiv').hide();
      $('#percentOffDiv').hide();
      $('#amountOffDivEdit').hide();
      $("#amountOffDivInEditMode").hide();
      $('#percentOffDivEdit').hide();
      $('.durationInPeriods').hide();
      $('.durationInDays').show();
    } else {
      $('#campaignPromotion\\.trial').val(false);
      $('#discountTypeRadios').show();
      $('#discountTypeRadiosEditDiv').show();
      $('#percentOffDiv').show();
      $('#percentOffDivEdit').show();
      $('.durationInPeriods').show();
      $('.durationInDays').hide();
      $('#campaignPromotion\\.discountType1').attr("checked", true);
    }
    return;
  });
  $("#discountTypeRadios").live("click", function(event) {
    percentDiscountType = document.getElementById("campaignPromotion.discountType1").checked;
    if (percentDiscountType == false) {
      $('#percentOffDiv').hide();
      getCurrencyForSelectedChannel();
      $('#amountOffDiv').show();
    } else {
      $('#amountOffDiv').hide();
      $('#percentOffDiv').show();
    }
  });
  $("#discountTypeRadiosEdit").live("click", function(event) {
    percentDiscountType = document.getElementById("campaignPromotion.discountTypeEdit1").checked;
    if (percentDiscountType == false) {
      $('#percentOffDivEdit').hide();
      getCurrencyForSelectedChannel();
      $('#amountOffDivEdit').hide();
      $('#amountOffDivInEditMode').show();
    } else {
      $('#amountOffDivEdit').hide();
      $('#amountOffDivInEditMode').hide();
      $('#percentOffDivEdit').show();
    }
  });
  $("#unlimitedUsage").live("click", function(event) {
    if ($(this).is(':checked')) {
      $("#liDurationDays").hide();
      $("#durationDays").val("0");
    } else {
      $("#liDurationDays").show();
      $("#durationDays").val("1");
    }
  });
  $("#unlimitedAccounts").live("click", function(event) {
    if ($(this).is(':checked')) {
      $("#liMaxAccounts").hide();
      $("#maxAccounts").val("0");
    } else {
      $("#liMaxAccounts").show();
      $("#maxAccounts").val("1");
    }
  });
  $("#indefinite1").click(function() {
    if ($(this).is(':checked')) {
      $("#campaignPromotion\\.durationDays").val("0");
      $("#campaignPromotion\\.durationDays").attr('disabled', 'on');
    } else {
      $("#campaignPromotion\\.durationDays").removeAttr('disabled');
    }
  });
  if ($("#indefinite1").is(':checked')) {
    $("#campaignPromotion\\.durationDays").val("0");
    $("#campaignPromotion\\.durationDays").attr('disabled', 'on');
  } else {
    $("#campaignPromotion\\.durationDays").removeAttr('disabled');
  }

  $('input:radio[name=campaignOff]').click(function() {
    if ($("#campaignOffs input:radio:checked").val() == "TimeOff") {
      $("#timeoffDiv").show();
      $("#moneyoffDiv").hide();
    } else if ($("#campaignOffs input:radio:checked").val() == "MoneyOff") {
      $("#campaignPromotion\\.durationDays").val("0");
      $("#campaignPromotion\\.percentOff").val("0");
      $("#moneyoffDiv").show();
      $("#timeoffDiv").hide();
    }
  });
  if ($("#campaignOffs input:radio:checked").val() == "TimeOff") {
    $("#timeoffDiv").show();
    $("#moneyoffDiv").hide();
  } else if ($("#campaignOffs input:radio:checked").val() == "MoneyOff") {
    $("#campaignPromotion\\.durationDays").val("0");
    $("#campaignPromotion\\.percentOff").val("0");
    $("#moneyoffDiv").show();
    $("#timeoffDiv").hide();
  }


  $('#editcampaigncancel').live('click', function() {
    if ($("#editcampaignDiv").length) {
      $("#editcampaignDiv").html("");
      viewCampaign($("div[id^='row'].selected"));
    }
  });

  $('#editcampaign').live('click', function() {
    $("#campaignPromotionsForm").valid();
  });


  $.startDatePicker = function() {
    //Start date should be editable only if the campaign is in scheduled state means start date is in future.
    //and while creating a new campaign
    if ($("#campPromotionState").val() == null || 'SCHEDULED' == $("#campPromotionState").val()) {
      $('#campaignPromotion\\.startDateDisplay').datepicker({
        duration: '',
        showOn: "button",
        buttonImage: "/portal/images/calendar_icon.png",
        buttonImageOnly: true,
        dateFormat: g_dictionary.friendlyDate,
        showTime: false,
        altField:"#campaignPromotion\\.startDate",
        altFormat:"mm/dd/yy",
        minDate: new Date()
      });
    }
    var startDate = $.datepicker.parseDate("mm/dd/yy", $('#campaignPromotion\\.startDate').val());
    $('#campaignPromotion\\.startDateDisplay').val($.datepicker.formatDate(g_dictionary.friendlyDate, startDate));
  };

  $.endDatePicker = function() {
    $('#campaignPromotion\\.endDateDisplay').datepicker({
      duration: '',
      showOn: "button",
      buttonImage: "/portal/images/calendar_icon.png",
      buttonImageOnly: true,
      dateFormat: g_dictionary.friendlyDate,
      showTime: false,
      altField:"#campaignPromotion\\.endDate",
      altFormat:"mm/dd/yy",
      minDate: new Date()
    });
    var endDate = $.datepicker.parseDate("mm/dd/yy", $('#campaignPromotion\\.endDate').val());
    $('#campaignPromotion\\.endDateDisplay').val($.datepicker.formatDate(g_dictionary.friendlyDate, endDate));
  };

  $("#addnewcampaigncancel").live('click', function() {
    $("#addnewcampaignDiv").html("");
  });

  $(".actions_dropdown_button").click(function() {
    if ($(".actions_dropdown:visible").length != 0) {
      $(".actions_dropdown").hide();
    } else {
      $(".actions_dropdown").show();
    }
  });
  setTimeout('$("#userForm input:visible:first").focus()', 1000);

  //validator for validating date for format dd/mm/yyyy
  /*jQuery.validator.addMethod("ddmmyyyyformat", function(value, element) {
			        return value.match(/^\d{2}\/\d{2}\/\d{4}$/);
			    }, null);*/

  // a custom method for validating the date range
  $.validator
    .addMethod(
      "dateRange",
      function() {
        if ($('#campaignPromotion\\.endDate').val() == "") {
          return true;
        }
        return new Date(
          $(
            "#campaignPromotion\\.startDate")
          .val()) <= new Date(
          $(
            "#campaignPromotion\\.endDate")
          .val());
      },
      i18n.campaigns.campaignEnterValidDateRange);
  $(".editCampaign_link").live("click", function() {
    $("#top_message_panel").hide();
    var ID = $("#campaign_id").val();
    $("#editcampaignDiv").html('');
    var actionurl = campaignsUrl + "editcampaign";
    $.ajax({
      type: "GET",
      url: actionurl,
      data: {
        Id: ID
      },
      dataType: "html",
      success: function(html) {
        $("#editcampaignDiv").html(html);
        $.startDatePicker();
        $.endDatePicker();
        var $thisPanel = $("#editcampaignDiv");
        $thisPanel.dialog({
          height: 470,
          width: 700
        });
        $thisPanel.dialog('option', 'buttons', {
          "OK": function() {
            if ($thisPanel.find("#campaignPromotionsForm").valid()) {
              $.ajax({
                type: "POST",
                url: "/portal/portal/promotions/editcampaign",
                data: $thisPanel.find("#campaignPromotionsForm").serialize(),
                dataType: "json",
                success: function(jsonObj) {
                  editCampaignDetailsToListView($("li[id^='row'].selected.campaignsList"), jsonObj);
                  $("#viewcampaignDiv").find("#top_message_panel").find("#msg").text(g_dictionary.campaignEditedSuccess);
                  $("#viewcampaignDiv").find("#top_message_panel").find("#status_icon").removeClass(
                    "erroricon").addClass("successicon").show();
                  $("#viewcampaignDiv").find("#top_message_panel").removeClass("error").addClass("success").show();
                },
                error: function(html) {
                  $("#viewcampaignDiv").find("#top_message_panel").find("#msg").text(g_dictionary.campaignEditedFailure);
                  $("#viewcampaignDiv").find("#top_message_panel").find("#status_icon").removeClass(
                    "successicon").addClass("erroricon").show();
                  $("#viewcampaignDiv").find("#top_message_panel").removeClass("success").addClass("error").show();
                  // alert(i18n.alerts.editFailure+" "+tktNumber);
                }
              });
              $(this).dialog("close");
              $(this).dialog("destroy");

            };
          },
          "Cancel": function() {
            $(this).dialog("close");
            $(this).dialog("destroy");
            $("#editcampaignDiv").html('');
          }
        });
        dialogButtonsLocalizer($thisPanel, {
          'OK': g_dictionary.dialogOK,
          'Cancel': g_dictionary.dialogCancel
        });
        $thisPanel.bind("dialogbeforeclose", function(event, ui) {
          $thisPanel.empty();
          $("#editcampaignDiv").html('');
        });
        $thisPanel.dialog("open");
        if ($('#campaignPromotion\\.discountTypeEdit').val() == 'PERCENTAGE') {
          $('#campaignPromotion.discountTypeEdit1').checked = true;
          $('#percentOffDivEdit').show();
        } else {
          $('#campaignPromotion.discountTypeEdit2').checked = true;
          $('#amountOffDivEdit').show();
        }
      },
      error: function() {
        $(".editCampaign").unbind('click');
      }
    });

  });


  //self click at the time of loading the page
  viewCampaign($("li[id^='row'].selected.campaignsList"));




  $("#details_tab").live("click", function(event) {

    $('#channels_tab').removeClass('active').addClass("nonactive");
    $('#details_tab').removeClass('nonactive').addClass("active");
    $('#associated_channel_content').hide();
    $('#details_content').show();

  });
  $("#channels_tab").live("click", function(event) {

    $('#details_tab').removeClass('active').addClass("nonactive");
    $('#channels_tab').removeClass('nonactive').addClass("active");
    $('#details_content').hide();
    $('#associated_channel_content').show();


  });

});

/**
 * Reset data row style
 * @return
 */

function resetGridRowStyle() {
  $(".widget_navigationlist").each(function() {
    $(this).removeClass("selected active");
  });
}

/**
 * View campaign details
 * @param current
 * @return
 */

function viewCampaign(current) {
  var divId = $(current).attr('id');
  if (divId == null) {
    return;
  }
  var ID = divId.substr(3);
  resetGridRowStyle();
  var cls = $(current).attr('class');
  cls = cls + " selected active";
  $(current).attr('class', cls);
  var url = campaignsUrl + "show";
  $.ajax({
    type: "GET",
    url: url,
    data: {
      Id: ID
    },
    dataType: "html",
    async: false,
    success: function(html) {
      $("#editcampaignDiv").html("");
      $("#viewcampaignDiv").html(html);
      bindActionMenuContainers();
    },
    error: function() {
      //need to handle TO-DO
    }
  });
}

function getCurrencyForSelectedChannel() {
  var actionurl = campaignsUrl + "getSupportedCurrencies";
  $.ajax({
    type: "GET",
    url: actionurl,
    data: $("#campaignPromotionsForm").serialize(),
    dataType: "html",
    success: function(html) {
      $("#supportedCurrencyDiv").html(html);
      $("#supportedCurrencyEditDiv").html(html);
    },
    error: function(jsonObj) {
      popUpDialogForAlerts("dialog_info", jsonObj.responseText);
    }
  });
}

function addCampaignDetailsToListView(json) {
  var campaignListTemplate = $("#campaignViewTemplate").clone();
  campaignListTemplate.attr('id', "row" + json.id);
  campaignListTemplate.find(".widget_navtitlebox").find('.title').text(json.code);
  var subtitleText = i18n.label.campaigns.Type + " ";
  if (json.discountType == "PERCENTAGE" && json.percentOff == 100.00) {
    subtitleText += i18n.label.campaigns.TimeOff + "; ";
  } else {
    subtitleText += i18n.label.campaigns.MoneyOff + "; ";
  }
  if (json.campaignPromotionsInChannels != null) {
    subtitleText += i18n.label.campaigns.Channel + ": ";
    for (var c = 0; c < json.campaignPromotionsInChannels.length; c++) {
      subtitleText += (json.campaignPromotionsInChannels[c].channel.code + " ");
    }
  }
  campaignListTemplate.find(".widget_navtitlebox").find(".subtitle").text(subtitleText);
  campaignListTemplate.find("#promoCode").text(json.promoCode);
  campaignListTemplate.find("#trial").text(i18nBooleanString(json.trial));
  campaignListTemplate.find("#startDate").text(dateFormat(json.startDate, g_dictionary.dateonlyFormat, false));
  campaignListTemplate.find("#endDate").text(dateFormat(json.endDate, g_dictionary.dateonlyFormat, false));
  campaignListTemplate.find("#title").text(json.title);
  campaignListTemplate.find("#enable").text(i18nBooleanString(json.enabled));
  
  var channelSubtitleText = "";
  if (json.campaignPromotionsInChannels != null) {
    for (var c = 0; c < json.campaignPromotionsInChannels.length; c++) {
      channelSubtitleText += (json.campaignPromotionsInChannels[c].channel.code + ", ");
    }
    channelSubtitleText = channelSubtitleText.substring(0, channelSubtitleText.length - 2);
  }  
  campaignListTemplate.find("#channelSubtitle").text(channelSubtitleText);
  campaignListTemplate.show();
  $(".widget_navigationlist:first").prepend(campaignListTemplate);
  var perPageValue = 14;
  var campaignCount = $(".widget_navigationlist").size();
  if (campaignCount > perPageValue) {
    $(".widget_navigationlist:last").remove();
  }
  viewCampaign(campaignListTemplate);
}

function addNewCampaignGet() {
  $("#top_message_panel").hide();
  var actionurl = campaignsUrl + "create";
  $("#addnewcampaignDiv").html("");
  $.ajax({
    type: "GET",
    url: actionurl,
    dataType: "html",
    success: function(html) {

      $("#addnewcampaignDiv").html(html);
      $.startDatePicker();
      $.endDatePicker();
      initDialog("addnewcampaignDiv", 700);
      var $thisPanel = $("#addnewcampaignDiv");
      $thisPanel.dialog({
        height: 500,
        width: 700
      });
      $thisPanel.dialog('option', 'buttons', {
        "Submit": function() {
          if ($("#campaignPromotionsForm").valid()) {
            $.ajax({
              type: "POST",
              url: "/portal/portal/promotions/create",
              data: $("#campaignPromotionsForm").serialize(),
              dataType: "json",
              async: false,
              success: function(json) {
                $thisPanel.dialog("close");
                $thisPanel.dialog("destroy");
                addCampaignDetailsToListView(json);
                $("#top_message_panel").find("#msg").text(g_dictionary.campaignCreationSuccess);
                $("#top_message_panel").find("#status_icon").removeClass("erroricon").addClass("successicon").show();
                $("#top_message_panel").removeClass("error").addClass("success").show();
                $("#non_list").hide(); 
              },
              error: function(XMLHttpRequest) {
                displayAjaxFormError(XMLHttpRequest, "campaignPromotionsForm", "main_addnew_formbox_errormsg");
                $("#top_message_panel").find("#msg").text(g_dictionary.campaignCreationFailure);
                $("#top_message_panel").find("#status_icon").removeClass("successicon").addClass("erroricon").show();
                $("#top_message_panel").removeClass("success").addClass("error").show();
              }
            });
          }
        },
        "Cancel": function() {
          $thisPanel.dialog("close");
          $thisPanel.dialog("destroy");
          $("#addnewcampaignDiv").html("");
        }
      });
      dialogButtonsLocalizer($thisPanel, {
        'Submit': g_dictionary.dialogSubmit,
        'Cancel': g_dictionary.dialogCancel
      });
      $thisPanel.dialog("open");
    }
  });
}

/**
 * Add new campaign(POST)
 * @param event
 * @param form
 * @return
 */

function addNewCampaign(event, form) {
  if (event.preventDefault) {
    event.preventDefault();
  } else {
    event.returnValue = false;
  }
  if ($("#campaignPromotionsForm").valid()) {
    var startDate = $('#campaignPromotion\\.startDate').val();
    var endDate = $('#campaignPromotion\\.endDate').val();

    //		percentDiscountType = document.getElementById("campaignPromotion.discountType1").checked;
    //		alert("percentDiscountType" , percentDiscountType);
    //		if (percentDiscountType == true) {
    //		  $('#campaignPromotion\\.discountType').val('PERCENTAGE');
    //		} else {
    //		  $('#campaignPromotion\\.discountType').val('FIXED_AMOUNT');
    //		}
    $.ajax({
      type: "POST",
      url: $(form).attr('action'),
      data: $(form).serialize(),
      dataType: "json",
      success: function(jsonResponse) {
        $.createNewCampaign(jsonResponse, startDate, endDate);
        if ($("#compaigngridcontentDiv").length > 0) {
          $("#compaigngridcontentDiv").hide();
        }
      },
      error: function(XMLHttpRequest) {
        if (XMLHttpRequest.status === AJAX_FORM_VALIDATION_FAILED_CODE) {
          displayAjaxFormError(XMLHttpRequest, "campaignPromotionsForm", "main_addnew_formbox_errormsg");
        } else if (XMLHttpRequest.status === CODE_NOT_UNIQUE_ERROR_CODE) {
          popUpDialogForAlerts("dialog_info", i18n.errors.common.codeNotUnique);
        } else {
          popUpDialogForAlerts("dialog_info", i18n.campaigns.failed_create_campaign);
        }
      }
    });

  }
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
  //window.location = "/portal/portal/tenants/alerts?tenant="+$('#tenantId').val()+"&page="+(parseInt($currentPage)+1);
  window.location = "/portal/portal/promotions/list?" + "&page=" + (parseInt($currentPage) + 1);
}

function previousClick() {
  var $currentPage = $('#current_page').val();
  //window.location = "/portal/portal/tenants/alerts?tenant="+$('#tenantId').val()+"&page="+(parseInt($currentPage)-1);
  window.location = "/portal/portal/promotions/list?" + "&page=" + (parseInt($currentPage) - 1);
}

function editCampaignDetailsToListView(current, json) {
  current.find("#endDate").text(dateFormat(json.endDate, g_dictionary.dateonlyFormat, false));
  current.find("#title").text(json.title);
  current.find("#enable").text(i18nBooleanString(json.enabled));
  viewCampaign(current);
}

/**
 * Edit Campaign (GET)
 */

function editCampaignGet(current) {
  var divId = $(current).attr('id');
  var ID = divId.substr(4);
  var actionurl = campaignsUrl + "editcampaign";
  $.ajax({
    type: "GET",
    url: actionurl,
    data: {
      Id: ID
    },
    dataType: "html",
    success: function(html) {
      $("#viewcampaignDiv").html("");
      $("#addnewcampaigncancel").click();
      $("#editcampaignDiv").html("");
      $("#editcampaignDiv").html(html);
      $.startDatePicker();
      $.endDatePicker();
      if ($('#campaignPromotion\\.discountType').val() == 'PERCENTAGE') {
        $('#campaignPromotion.discountType1').checked = true;
        $('#percentOffDiv').show();
      } else {
        $('#campaignPromotion.discountType2').checked = true;
        $('#amountOffDiv').show();
      }
    },
    error: function() {
      $(".editCampaign").unbind('click');
    }
  });
}


$("#campaignEnabled").live("click", function() {
  if ($(this).attr("checked") == "checked") {
    $('#campaignPromotion\\.enabled').val(true);
  } else {
    $('#campaignPromotion\\.enabled').val(false);
  }
});

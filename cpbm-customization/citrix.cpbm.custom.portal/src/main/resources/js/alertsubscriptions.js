/*
*  Copyright � 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/
$(document).ready(function() {
	
	$.validator
	.addMethod(
	    "greaterThanZero",
	    function(value, element) {
	      $(element).rules("add", {
	        number : true
	        
	      });   

	      if(value <=0){
	        return false;
	      }else{
	        return true;
	      }
	      
	    },
	    i18n.errors.spendLimit.greaterThanZero);
	    
	$.validator.addClassRules("greaterThanZero", {
	    greaterThanZero: true
	 });

  /**
   * Creates new product row
   */
  $.createNewAlert = function(jsonResponse) {

    if (jsonResponse == null) {
      popUpDialogForAlerts("dialog_info", i18n.alert.createNewAlert);
    } else {
      $("#createnewalertDiv").html("");

      var rowClass = "db_gridbox_rows even";
      var count = $(".countDiv").attr("id");
      var size = Number(count.substr(5));
      var selected = "";
      if (size == 0) {
        selected = "selected";
      }
      if (size % 2 == 0) {
        rowClass = "db_gridbox_rows odd " + selected;
      } else {
        rowClass = "db_gridbox_rows even " + selected;
      }
      var content = "";
      content = content + "<div class='" + rowClass + "' onclick='viewAlert(this)' id='row" + jsonResponse.id + "'>";
      content = content + "<div class='db_gridbox_columns' style='width:20%;'>";
      content = content + "<div class='db_gridbox_celltitles'>";
      if (jsonResponse.type == 'Tenant') {
        content = content + i18n.label.alertlist.type.tenant;
      } else if (jsonResponse.type == 'User') {
        content = content + i18n.label.alertlist.type.user;
      } else if (jsonResponse.type == 'Project') {
        content = content + i18n.label.alertlist.type.project;
      } else {
        content = content + i18n.label.alertlist.type.membership;
      }

      content = content + "</div>";
      content = content + "</div>";
      content = content + "<div class='db_gridbox_columns' style='width:30%;'>";
      content = content + "<div class='db_gridbox_celltitles'>";
      content = content + jsonResponse.accountHolderName;
      content = content + "</div>";
      content = content + "</div>";
      content = content + "<div class='db_gridbox_columns' style='width:49%;'>";
      content = content + "<div class='db_gridbox_celltitles' id='spendBudgetDiv" + jsonResponse.id + "'>";

      content = content + i18n.label.alertlist.spendbudget.replace("{0}", jsonResponse.percentage);
      content = content + "</div>";
      content = content + "</div>";

      content = content + "</div>";
      var oldContent = $("#alertgridcontent").html();
      oldContent = oldContent + content;
      $("#alertgridcontent").html(oldContent);
      if (size == 0) {
        $.viewAlertDetails(jsonResponse);
      }
      size = size + 1;
      $(".countDiv").attr("id", "count" + size);
      $("#row" + jsonResponse.id).click();

    }

  };


  /**
   * View Catalog details
   */
  $.viewAlertDetails = function(jsonResponse) {

    $("#editalertDiv").html("");
    //$("#viewalertDiv").html(alertDetailsHtml);
    var spendBudgetPercentageStr = i18n.label.alertlist.spendbudget.replace("{0}", jsonResponse.percentage);
    $("#spendBudgetPercentageDiv").html(spendBudgetPercentageStr);
    $('#spendvsbudgetChart').html("");
    var spend_cap = parseFloat(jsonResponse.percentage) * parseFloat($('#total_budget').val()) / 100;
    showAlertHighChart(spend_cap, jsonResponse.percentage);
    alertDetailsHtml = "";

  };



  $("#addnewalertcancel").click(function() {
    $("#createnewalertDiv").html("");
  });

  /**
   * edit page Cancel action
   */
  $("#editalertcancel").click(function() {
    $("#editalertDiv").html("");
  });



  /**
   * Update Alert row
   */
  $.editAlert = function(jsonResponse) {

    if (jsonResponse == null) {
      alert(i18n.alert.editAlert);
    } else {
      $("#editalertDiv").html("");
      var spendBudgetPercentageStr = i18n.label.alertlist.spendbudget.replace("{0}", jsonResponse.percentage);
      $("#spendBudgetDiv" + jsonResponse.id).html(spendBudgetPercentageStr);
      $("#criteria_hover" + jsonResponse.id).html(spendBudgetPercentageStr);

      $.viewAlertDetails(jsonResponse);
    }
  };


  $("#selectmember").change(function() {
    var value = $("#projectMembershipId").val();
    if (value.search("membership") != -1) {
      $("#membershipPercentageDiv").show();
      $("#projectPercentageDiv").hide();
    } else {
      $("#membershipPercentageDiv").hide();
      $("#projectPercentageDiv").hide()();
    }
  });
  jQuery.validator.addMethod("percentage", function(value, element) {
    if (value >= 0 && value <= 100) {
      return true;
    } else {
      return false;
    }
  });
  $('input:radio[name=alerttype]').click(function() {
    if ($("#alerttypes input:radio:checked").val() == "project") {
      $("#selectmember").hide();
      $("#membershipPercentageDiv").hide();
      $("#projectPercentageDiv").show();
    } else if ($("#alerttypes input:radio:checked").val() == "member") {
      $("#membershipPercentageDiv").show();
      $("#projectPercentageDiv").hide();
      $("#selectmember").show();
    }
  });

  $("#createNewProjectAlert").click(function() {
    blockUI();
    var url = $(this).attr("name");
    $.ajax({
      type: "GET",
      url: url,
      data: {},
      dataType: "html",
      success: function(html) {
        $("#createNewAlertDiv").html(html);
        unBlockUI();
      },
      error: function(html) {
        $("#createNewAlert").unbind('click');
        unBlockUI();
        return false;
      }
    });
    return false;
  });

  $("#cancelNewProjectAlert").click(function() {
    $("#createNewAlertDiv").html("");
    return false;
  });
  $("#createNewAlert").click(function() {
    $("#setAccountBudgetDiv").hide();
    $("#createNewAlertDiv").show();
    return false;
  });

  $("#cancelNewAlert").click(function() {
    $("#createNewAlertDiv").hide();
    return false;
  });
  $(".editSub").click(function() {
    var sub_id = $(this).attr("id").substring(16);
    $("#editsubscription" + sub_id).hide();
    $("#cancelEdit" + sub_id).show();
    $("#editSubSave" + sub_id).show();
    $("#alertVal" + sub_id).hide();
    $("#alertValEditable" + sub_id).show();
    $("#removesubscription" + sub_id).hide();

  });
  $(".saveSub").one('click', function() {

    blockUI();
    var sub_id = $(this).attr("id").substring(11);
    var newVal = $("#newValue" + sub_id).val();
    var url = $(this).attr("name");
    $.ajax({
      type: "POST",
      url: url,
      data: {
        'newValue': newVal
      },
      dataType: "html",
      success: function(html) {
        $("#alertVal" + sub_id).show();
        $("#alertVal" + sub_id).html(newVal);
        $("#alertValEditable" + sub_id).hide();
        $("#cancelEdit" + sub_id).hide();
        $("#editSubSave" + sub_id).hide();
        $("#editsubscription" + sub_id).show();
        $("#removesubscription" + sub_id).show();
        unBlockUI();
      },
      error: function(html) {
        unBlockUI();
        return false;
      }
    });
    return false;
  });
  $(".cancelEditSub").click(function() {
    var sub_id = $(this).attr("id").substring(10);
    $("#editsubscription" + sub_id).show();
    $("#removesubscription" + sub_id).show();
    $("#cancelEdit" + sub_id).hide();
    $("#editSubSave" + sub_id).hide();
    $("#alertVal" + sub_id).show();
    $("#alertValEditable" + sub_id).hide();
  });
  $(".removeSub").one('click', function() {

    blockUI();
    var sub_id = $(this).attr("id").substring(18);
    var url = $(this).attr("name");
    $.ajax({

      type: "POST",
      url: url,
      data: {},
      dataType: "html",
      success: function(html) {
        $("#subRow" + sub_id).hide();
        unBlockUI();
      },
      error: function(html) {
        unBlockUI();
        return false;
      }
    });
    return false;
  });
  $("#setAccountBudget").click(function() {
    $("#createNewAlertDiv").hide();
    $("#setAccountBudgetDiv").show();
    return false;
  });

  $.validator
    .addMethod(
      "twoDecimal",
      function(value, element) {
        $(element).rules("add", {
          number: true
        });
        isPriceValid = (value != "" && isNaN(value) == false && value > 0 && value <= 99999999.99);
        if (isPriceValid == false) {
          return false;
        }
        return true;
      },
      i18n.errors.spendLimit.number);


  $("#tenantForm").validate({
    success: "valid",
    ignoreTitle: true,
    rules: {
      "spendLimit": {
        required: true,
        twoDecimal: true

      },
      "tenant.spendBudget": {
        required: true,
        twoDecimal: true

      }
    },
    messages: {
      "spendLimit": {
        required: i18n.errors.spendLimit.required
      },
      "tenant.spendBudget": {
        required: i18n.errors.spendLimit.required
      }
    },
    errorPlacement: function(error, element) {
      var name = element.attr('id');
      name = name.replace(".", "\\.");
      error.appendTo("#" + name + "Error");
    }

  });
  $("#setaccountbudgetcancel").click(function() {
    $("#setaccountbudgetDiv").html("");
    return false;
  });

  initDialog("createnewalertDiv", 420);
  $("#add_alert_link").bind("click", function(event) {
    addNewAlertGet();
  });

  initDialog("setaccountbudgetDiv", 420);
  $("#set_account_budget_link").bind("click", function(event) {
    setAccountBudgetGet();
  });

  initDialog("editalertDiv", 420);
  $(".editalert_link").live("click", function(event) {
    editAlertGet(this);
  });


  initDialog("dialog_confirmation", 350, false);
  var topActionMap = {
    removealert: {
      label: dictionary.removealert,
      elementIdPrefix: "removealert",
      inProcessText: dictionary.removingAlert,
      type: "GET",
      dataType: "text",
      afterActionSeccessFn: function(resultObj) {
        $("li[id^='row'].selected.alerts").slideUp("slow", function() {
          clearDetailsPanel();
          $(this).remove();
          viewAlert($("li[id^='row'].alerts").first().addClass("selected"));
        });

      }
    }
  };

  function getConfirmationDialogButtons(command) {

    var buttonCallBacks = {};
    var actionMapItem;
    if (command == "removealert") {
      actionMapItem = topActionMap.removealert;
    }

    buttonCallBacks[dictionary.lightboxbuttonconfirm] = function() {
      $(this).dialog("close");

      var apiCommand;
      if (command == "removealert") {

        var alert_id = $('#alertId').val();
        apiCommand = alertsUrl + "alerts/remove?Id=" + alert_id;

      }

      doActionButton(actionMapItem, apiCommand);

    };

    buttonCallBacks[dictionary.lightboxbuttoncancel] = function() {
      $(this).dialog("close");
    };

    return buttonCallBacks;
  }

  $(".removealert_link").live("click", function(event) {
    $("#dialog_confirmation").text(dictionary.lightboxremovealert).dialog('option', 'buttons',
      getConfirmationDialogButtons("removealert")).dialog("open");
  });




  viewAlert($("li[id^='row'].selected.alerts"));
});

function showAlertHighChart(spend_cap, criteria_percentage) {
  var total_budget = parseFloat($('#total_budget').val()).toFixed(parseInt($('#minFractionDigits').val()));
  if (total_budget <= 0)
    return;
  var currencySymbol = $('#currency_sign').val();

  if (spend_cap == null)
    var spend_cap = parseFloat($('#spend_cap').val()).toFixed(parseInt($('#minFractionDigits').val()));
  if (criteria_percentage == null)
    //while adding alert percentage, UI is allowing only upto 2 decimal places
    var criteria_percentage = parseFloat($('#criteria_percent').val()).toFixed(2);;
  var extra_budget = total_budget - spend_cap;
  var chartTitle = dictionary.alertHighchartTitle;
  var yAxisTitle = dictionary.alertHighchartYAxisTitle + ' (' + currencySymbol + ')';
  var categories = [""];
  var alert_id = $('#alertId').val();
  var user_email = $('#user_email').val();
  var user_phone = $('#user_phone').val();
  var series = [{
    data: [{
      name: 'budget',
      
      y: parseFloat(extra_budget)
    }],
    showInLegend: false

  }, {
    name: dictionary.alertHighchartSeriesName1,
    data: [{
      name: dictionary.alertHighchartCriteriaTooltip,
      
      y: parseFloat(spend_cap)
    }],
    legendIndex: 1

  }];


  var options = {
    "numberPrefix": currencySymbol,
    "chartTitle": chartTitle,
    "yAxisTitle": yAxisTitle,
    "inverted": true,
    "min": 0,
    "max": total_budget,
    "decPoint": g_dictionary.decPoint,
    "thousandsSep": g_dictionary.thousandsSep,
    "lang": {
      printButtonTitle: g_dictionary.highChartPrint,
      exportButtonTitle: g_dictionary.highChartExport,
      downloadPNG: g_dictionary.highChartDownloadPNG,
      downloadJPEG: g_dictionary.highChartDownloadJPEG,
      downloadPDF: g_dictionary.highChartDownloadPDF,
      downloadSVG: g_dictionary.highChartDownloadSVG
    },
    "yAxisAlternateGridColor": "#EFEFEF",
    "toolTipFormatter": function() {
      if (this.point.name == "budget")
        return false;
      var pointName = typeof(this.point.name) == 'undefined' ? ': ' : '<br>' + this.point.name;

      var name = '<b>' + dictionary.alertHighchartCriteriaToolstart + ': ' + criteria_percentage + '% (' +
        currencySymbol + spend_cap + ')</b>' + pointName + currencySymbol + this.y + '<br>' + dictionary.alertHighchartCriteriaTooltipEmail +
        ':<span style="color:#086A87;"> ' + user_email + '</span><br>' + dictionary.alertHighchartCriteriaTooltipPhone +
        ': <span style="color:#086A87;">' + user_phone + '</span>';
      return '<div style="width:450px;word-wrap:break-word;white-space: pre-wrap;">' + name + '</div>';
    },
    "useHTML": true,
    "toolTipPositioner": function(){
      return { x: 20, y: 80 };
    },
    "exportEnabled": false,
    "yAxisStackLabelFormatter": function() {
      return dictionary.alertHighchartYAxisStackLabel;
    },
    "chartSpacingRight": 100,
    "chartSpacingLeft": 25,
    "rotateYAxisLabels": true,
    "legendItemClick": function(){
      return false;
    },
    "itemStyle" : {
      cursor: 'default',
      color: '#000'
    }
  };

  HighChartsUtil.renderChart('spendvsbudgetChart', series, categories, options);
}

function addNewAlertGet() {
  var actionurl = alertsUrl + "alerts/new?tenant=" + $("#tenantId").val();

  $.ajax({
    type: "GET",
    url: actionurl,
    dataType: "html",
    success: function(html) {

      $("#setaccountbudgetDiv").html("");
      $("#createnewalertDiv").html(html);

      var budget = parseFloat($("#budgetid").val());

      if (budget == null || budget == 0.0) {
        initDialogWithOK("dialog_info", 350, false);
        dialogButtonsLocalizer($("#dialog_info"), {
          'OK': window.parent.g_dictionary.dialogOK
        });
        $("#dialog_info").dialog("option", "height", 150);
        $("#dialog_info").text(dictionary.alertCreateErrorDialog).dialog("open");
        $("#createnewalertDiv").html("");

        return;
      }

      var $thisDialog = $("#createnewalertDiv");

      $thisDialog.dialog({
        height: 100,
        width: 420
      });
      $thisDialog.dialog('option', 'buttons', {
        "OK": function() {
          if ($("#subscriptionForm").valid()) {


            $.ajax({
              type: "POST",
              url: "/portal/portal/tenants/alerts/new?tenant=" + $("#tenantId").val(),
              data: $('#subscriptionForm').serialize(),
              dataType: "json",
              success: function(jsonResponse) {
                var newUrl = window.location.href.replace("&budget=true", "");
                window.location = newUrl;
                $thisDialog.dialog("close");
              },
              error: function(XMLHttpRequest) {
                if (XMLHttpRequest.status === AJAX_FORM_VALIDATION_FAILED_CODE) {
                  displayAjaxFormError(XMLHttpRequest, "subscriptionForm", "main_addnew_formbox_errormsg");
                }
              }
            });

          }


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
    error: function() {}
  });
}
function reloadAlertsPage(){
  window.location ="/portal/portal/tenants/alerts?tenant=" + effectiveTenantParam;
}

function setAccountBudgetGet() {
  var actionurl = alertsUrl + "set_account_budget?tenant=" + $("#tenantId").val();
  $.ajax({
    type: "GET",
    url: actionurl,
    dataType: "html",
    success: function(html) {
      $("#createnewalertDiv").html("");
      $("#setaccountbudgetDiv").html(html);
      var $thisDialog = $("#setaccountbudgetDiv");
      
      $thisDialog.dialog({
        height: 100,
        width: 420
      });
      $thisDialog.dialog('option', 'buttons', {
        "OK": function() {
          if ($("#tenantForm").valid()) {
            $thisDialog.dialog("close");
            $("#top_actions").show();
            $("#spinning_wheel").show();
            $("#in_process_text").text(dictionary.settingAccountBudgetProcess);

            $.ajax({
              type: "POST",
              url: "/portal/portal/tenants/set_account_budget?tenantparam=" + $("#tenantId").val(),
              data: $('#tenantForm').serialize(),
              success: function(jsonResponse) {
                $("#spinning_wheel").hide();
                popUpDialogForAlerts("dialog_info", dictionary.settingAccountBudgetSuccess, reloadAlertsPage);
              },
              error: function(XMLHttpRequest) {
                $("#spinning_wheel").hide();
                $("#top_message_panel").find("#msg").text(dictionary.settingAccountBudgetFail);
                $("#top_message_panel").addClass("error").show();
                if (XMLHttpRequest.status === AJAX_FORM_VALIDATION_FAILED_CODE) {
                  displayAjaxFormError(XMLHttpRequest, "tenantForm", "main_addnew_formbox_errormsg");
                }
              }
            });
          }
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
    error: function() {}
  });
  $("#in_process_text").text();
}

/**
 * Add new alert(POST)
 * @param event
 * @param form
 * @return
 */

function addNewAlert(event, form) {
  if (event.preventDefault) {
    event.preventDefault();
  } else {
    event.returnValue = false;
  }
  if ($("#subscriptionForm").valid()) {
    $("#addalert").attr("value", "Adding Alert..");
    $.ajax({
      type: "POST",
      url: $(form).attr('action'),
      data: $(form).serialize(),
      dataType: "json",
      success: function(jsonResponse) {
        $.createNewAlert(jsonResponse);
        if ($("#alertgridcontentDiv").length > 0) {
          $("#alertgridcontentDiv").hide();
        }
      },
      error: function(XMLHttpRequest) {
        if (XMLHttpRequest.status === AJAX_FORM_VALIDATION_FAILED_CODE) {
          displayAjaxFormError(XMLHttpRequest, "subscriptionForm", "main_addnew_formbox_errormsg");
        }
      }
    });

  }
}

/**
 * Add new alert(POST)
 * @param event
 * @param form
 * @return
 */

function addNewSpend(event, form) {
  if (event.preventDefault) {
    event.preventDefault();
  } else {
    event.returnValue = false;
  }
  if ($("#accountBudgetForm").valid()) {
    $.ajax({
      type: "POST",
      url: $(form).attr('action'),
      data: $(form).serialize(),
      dataType: "json",
      success: function(jsonResponse) {
        $.createNewSpend(jsonResponse);
      }
    });

  }
}

/**
 * Edit alert (GET)
 */

function editAlertGet(current) {
  var divId = $(current).attr('id');
  var ID = divId.substr(4);
  var actionurl = alertsUrl + "alerts/edit?tenant=" + $("#tenantId").val();
  $.ajax({
    type: "GET",
    url: actionurl,
    data: {
      Id: ID
    },
    dataType: "html",
    success: function(html) {
      alertDetailsHtml = $("#viewalertDiv").html();
      //$("#viewalertDiv").html("");
      $("#editalertDiv").html("");
      $("#editalertDiv").html(html);

      var $thisDialog = $("#editalertDiv");

      $thisDialog.dialog({
        height: 100,
        width: 420
      });
      $thisDialog.dialog('option', 'buttons', {
        "OK": function() {
          if ($("#editSubscriptionForm").valid()) {
            var newVal = $("#editSubscriptionForm").find("#tenantPercentage").val();
            $.ajax({
              type: "POST",
              url: "/portal/portal/tenants/alerts/edit?tenant=" + $('#tenantId').val() + "&Id=" + $('#alertId').val(),
              data: {
                'newValue': newVal
              },
              dataType: "json",
              success: function(jsonResponse) {
                $thisDialog.dialog("close");
                $.editAlert(jsonResponse);

              },
              error: function(XMLHttpRequest) {
                if (XMLHttpRequest.status === AJAX_FORM_VALIDATION_FAILED_CODE) {
                  displayAjaxFormError(XMLHttpRequest, "subscriptionForm", "main_addnew_formbox_errormsg");
                }
              }
            });

          }
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
    error: function() {}
  });
}

/**
 * Edit Alert POST
 * @param event
 * @param form
 * @return
 */

function editAlert(event, form) {
  if (event.preventDefault) {
    event.preventDefault();
  } else {
    event.returnValue = false;
  }
  if ($("#subscriptionForm").valid()) {
    var newVal = $("#tenantPercentage").val();
    $.ajax({
      type: "POST",
      url: $(form).attr('action'),
      data: {
        'newValue': newVal
      },
      dataType: "json",
      success: function(jsonResponse) {
        $.editAlert(jsonResponse);
      },
      error: function(XMLHttpRequest) {
        if (XMLHttpRequest.status === AJAX_FORM_VALIDATION_FAILED_CODE) {
          displayAjaxFormError(XMLHttpRequest, "subscriptionForm", "main_addnew_formbox_errormsg");
        }
      }
    });

  }
}

/**
 * Remove alert (GET)
 */

function removeAlert(current) {
  var r = confirm(i18n.confirm.removeAlert);
  if (r == false) {
    return false;
  }
  var divId = $(current).attr('id');
  var ID = divId.substr(6);
  var actionurl = alertsUrl + "alerts/remove";
  $.ajax({
    type: "GET",
    url: actionurl,
    data: {
      Id: ID
    },
    dataType: "text/hmtl",
    success: function(html) {
      if (html == "success") {
        $("#viewalertDiv").html("");
        $("#editalertDiv").html("");
        $("#row" + ID).remove();
        var count = $(".countDiv").attr("id");
        var size = Number(count.substr(5));
        size = size - 1;
        $(".countDiv").attr("id", "count" + size);
        var $alertsGrid = $("#alertgridcontent").find(".db_gridbox_rows:first");
        if ($alertsGrid != null) {
          $alertsGrid.click();
        }
        resetGridRowStyle();
      } else {
        alert(i18n.alert.removeAlert);
      }

    },
    error: function() {
      //need to handle
    }
  });
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

/**
 * View alert details
 * @param current
 * @return
 */

function viewAlert(current) {

  var divId = $(current).attr('id');
  if (divId == null) return;
  var ID = divId.substr(3);

  resetGridRowStyle();
  $(current).addClass("selected active");
  var url = alertsUrl + "alerts/view";
  $.ajax({
    type: "GET",
    url: url,
    data: {
      Id: ID
    },
    dataType: "html",
    success: function(html) {
      //$("#editalertDiv").html("");
      $("#viewalertDiv").html(html);
      bindActionMenuContainers();
      showAlertHighChart();
    },
    error: function() {
      //need to handle TO-DO
    }
  });
}
/*
$('#editalertcancel' ).live('click',function(){
    viewAlert($("div[id^='row'].selected")); 
});
*/



function clearDetailsPanel() {
  var $detailsContent = $('#viewalertDiv');

  $detailsContent.find("#account_name").text("");
  $detailsContent.find("#spendBudgetPercentageDiv").text("");
  $detailsContent.find("#spendBudgetCurrencyDiv").text("");
  $detailsContent.find("#alert_type").text("");
  $detailsContent.find("#spend_budget").text("");
  $detailsContent.find("#spendvsbudgetChart").html("");
  $("#top_actions").hide();
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
  window.location = "/portal/portal/tenants/alerts?tenant=" + $('#tenantId').val() + "&page=" + (parseInt($currentPage) +
    1);
}

function previousClick() {
  var $currentPage = $('#current_page').val();
  window.location = "/portal/portal/tenants/alerts?tenant=" + $('#tenantId').val() + "&page=" + (parseInt($currentPage) -
    1);
}

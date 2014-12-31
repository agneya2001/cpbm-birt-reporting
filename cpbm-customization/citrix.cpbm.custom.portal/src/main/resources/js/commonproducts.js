/*
*  Copyright © 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/
var SHOW_MESSAGE_TIME_BEFORE_PAGE_REFRESH = 3000;
$(document).ready(function() {

  $.validator.addClassRules("j_startDate", {
    startDate: true
  });

  $.validator
    .addMethod(
      "startDate",
      function(value, element) {
        $(element).rules("add", {
          required: true
        });
        var isTodayAllowed = $("#isTodayAllowed").val();
        var now = $.datepicker.parseDate(g_dictionary.friendlyDate, $("#date_today").val());
        var valueDate = $.datepicker.parseDate(g_dictionary.friendlyDate, value);
        if (isTodayAllowed == "true") {
          return valueDate >= now;
        } else {
          return valueDate > now;
        }
      },
      commmonmessages.startDate);

  $("#planDateForm").validate({
    errorPlacement: function(error, element) {
      var name = element.attr('id');
      name = ReplaceAll(name, ".", "\\.");
      if (name != "") {
        error.appendTo("#" + name + "Error");
      }
    }
  });
});

function initializePlanStartDatepicker(){
  $('#planstartDate').datepicker({
    duration: '',
    showOn: "button",
    changeYear: true,
    buttonImage: "/portal/images/calendar_icon.png",
    buttonImageOnly: true,
    buttonText: "",
    dateFormat: g_dictionary.friendlyDate,
    showTime: false,
    //minDate:new Date(new Date().getTime() + (24 * 60 * 60 * 1000)),
    minDate: $.datepicker.parseDate(g_dictionary.friendlyDate, $("#date_today").val()),
    beforeShow: function(dateText, inst) {
      $("#dialog_set_plan_date").data("height.dialog", 370);
      $("button").each(function() {
        $(this).attr("style", "margin-top: 170px;");
      });
      $("#ui-datepicker-div").addClass("datepicker_stlying");
      var isTodayAllowed = $("#isTodayAllowed").val();
      var pickerDate = $.datepicker.parseDate(g_dictionary.friendlyDate, $("#date_today").val());
      if (isTodayAllowed == "true") {
        $(this).datepicker("option", "minDate", pickerDate);
      } else {
        $(this).datepicker("option", "minDate", new Date(pickerDate.getTime() + (24 * 60 * 60 *
          1000)));
      }
    },
    onSelect: function(dateText, inst) {
      $(this).attr("value", dateText);
      $("#planstartDate").each(function() {
        $(this).attr("value", dateText);
      });
      $("#dialog_set_plan_date").data("height.dialog", 200);
      $("button").each(function() {
        $(this).attr('style', 'margin-top: 5px;');
      });
    },
    onClose: function(dateText, inst) {
      $("#dialog_set_plan_date").data("height.dialog", 200);
      $("button").each(function() {
        $(this).attr("style", "margin-top: 5px;");
      });
    }
  });
}

function setPlanDate(action, entityName) {
  initDialog("dialog_set_plan_date", 450);
  var $thisDialog = $("#dialog_set_plan_date");
  $thisDialog.data("height.dialog", 200);
  var actionurl = productsUrl + "setplandate";
  $.ajax({
    type: "GET",
    url: actionurl,
    dataType: "html",
    async: false,
    success: function(html) {
      $thisDialog.html("");
      $thisDialog.html(html);
      initializePlanStartDatepicker();
      $thisDialog.dialog('option', 'buttons', {
        "OK": function() {
          var productForm = $thisDialog.find("form");
          if ($(productForm).valid()) {
        	$(productForm).find("#planstartDateString").val($(productForm).find("#planstartDate").val());  
            $.ajax({
              type: "POST",
              url: $(productForm).attr('action'),
              data: $(productForm).serialize(),
              dataType: "html",
              async: false,
              success: function(status) {
                if (status == "plan_date_should_be_greater_or_equal_to_today") {
                  $thisDialog.dialog("close");
                  popUpDialogForAlerts("alert_dialog", i18n.errors.products.activationdategreaterorequaltotoday);
                } else if (status == "plan_date_should_be_greater_to_today") {
                  $thisDialog.dialog("close");
                  popUpDialogForAlerts("alert_dialog", i18n.errors.products.activationdategreaterthantoday);
                } else if (status == "no_product_added") {
                  $thisDialog.dialog("close");
                  popUpDialogForAlerts("alert_dialog", i18n.errors.products.noproductsadded);
                } else {
                  $thisDialog.dialog("close");
                  location.reload();
                }
              },
              error: function(XMLHttpRequest) {
            	popUpDialogForAlerts("alert_dialog", commmonmessages.failed_set_plan_date);
                $thisDialog.dialog("close");
              },
              complete: function() {
            	  
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
      $thisDialog.bind("dialogbeforeclose", function(event, ui) {
        $("#ui-datepicker-div").hide();
        $thisDialog.empty();
      });
      $thisDialog.dialog("open");
    },
    error: function() {}
  });
}

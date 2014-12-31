/*
*  Copyright � 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/
$(document).ready(function() {
  
  if($("#selectedChannel").val() != ''){
    $("#channelId").val($("#selectedChannel").val());
  }
  
  if($("#selectedBillingGroup").val() != ''){
    $("#billingGroupId").val($("#selectedBillingGroup").val());
  }
  
  if($("#filterSpend").val() == "productbundleUsage"){
    deactivateThirdMenuItem("spendByProduct");
    activateThirdMenuItem("spendByProductBundle");
  }
  else {
    deactivateThirdMenuItem("spendByProductBundle");
    activateThirdMenuItem("spendByProduct");
  }
  if($("#filterProductUsage").val() == "billingGroupUsage"){
    deactivateThirdMenuItem("channelusageByChannel");
    activateThirdMenuItem("channelusageByBillingGroup");
    $("#filterByChannel").hide();
    $("#filterByBillingGroup").show();
  }
  else {
    deactivateThirdMenuItem("channelusageByBillingGroup");
    activateThirdMenuItem("channelusageByChannel");
    $("#filterByChannel").show();
    $("#filterByBillingGroup").hide();
  }
  $(function() {
    $('#startDisplay').datepicker({ 
      duration: '',
      showOn: "button",
      buttonImage: "/portal/images/calendar_icon.png",
      buttonImageOnly: true,
      buttonText: i18n.text.startDateButtonText,
      dateFormat: g_dictionary.friendlyDate,
      showTime: true,
      stepMinutes: 1,
      stepHours: 1,
      maxDate: new Date(),
      time24h: false,
      altField:"#start",
      altFormat:"mm/dd/yy",
      onClose: function(dateText, inst) {
        if (dateText) {
          var endDate = $('#endDisplay').val();
          var date = $(this).datepicker("getDate");
          date.setDate(date.getDate());
          $('#endDisplay').datepicker("option", "minDate", date);
          $('#end').val(endDate);
          $('#endDisplay').val(endDate);
          $(".downloadmsg").empty();
        }
        
      }
    });
    if(typeof $('#start').val() != "undefined"){
      var startDate = $.datepicker.parseDate("mm/dd/yy", $('#start').val());
      $('#startDisplay').val($.datepicker.formatDate(g_dictionary.friendlyDate, startDate));
    }
  });

  $(function() {
    var minDate=null;
    if(typeof $('#start').val() != "undefined"){
      minDate = $.datepicker.parseDate("mm/dd/yy", $('#start').val());
    }
    if(minDate===null){
      minDate = new Date();
    }
    minDate.setDate(minDate.getDate() + 1);
    minDate = minDate || Date();
    $('#endDisplay').datepicker({
      duration: '',
      showOn: "button",
      buttonImage: "/portal/images/calendar_icon.png",
      buttonImageOnly: true,
      buttonText: i18n.text.endDateButtonText,
      dateFormat: g_dictionary.friendlyDate,
      showTime: true,
      stepMinutes: 1,
      stepHours: 1,
      time24h: false,
      minDate: minDate,
      altField:"#end",
      altFormat:"mm/dd/yy",
      maxDate: new Date(),
      onClose : function(){
        $(".downloadmsg").empty(); 
      }
    });
    if(typeof $('#end').val() != "undefined"){
      var endDate = $.datepicker.parseDate("mm/dd/yy", $('#end').val());
      $('#endDisplay').val($.datepicker.formatDate(g_dictionary.friendlyDate, endDate));
    }
  });
  

  
  $.validator
  .addMethod(
    "dateRange",
    function() {
      if ($('#end').val() == "") {
        return true;
      }
      return new Date(
        $(
          "#start")
        .val()) <= new Date(
        $(
          "#end")
        .val());
    },
    i18n.errors.reportEnterValidDateRange);
  
  $("#newRegistrationForm,#channelUsageReport").validate({
    //debug : true,
    success: "valid",
    ignoreTitle: true,
    rules: {
      "startDisplay": {
        required: true
      },
      "endDisplay": {
        required: true,
        dateRange: true
      }
    },
    messages: {
      "startDisplay": {
        required: i18n.errors.validationStartDate,
      },
      "endDisplay": {
        required: i18n.errors.validationEndDate,
        dateRange: i18n.errors.reportEnterValidDateRange
      }
    },
    errorPlacement: function(error, element) {
      var name = element.attr('id');
      name = ReplaceAll(name, ".", "\\.");
      if (error.html() != "") {
        $("#" + name + "Error").html("");
        error.appendTo("#" + name + "Error");
      }
    }
  });
  $("#reportForm").validate({
    //debug : true,
    success: "valid",
    ignoreTitle: true,
    rules: {
      "reportMonth": {
        required: true
      },
      "reportYear": {
        required: true
      }
    },
    messages: {
      "reportMonth": {
        required: i18n.errors.validationMonth
      },
      "reportYear": {
        required: i18n.errors.validationYear
      }
    },
    errorPlacement: function(error, element) {
      var name = element.attr('id');
      name = ReplaceAll(name, ".", "\\.");
      if (error.html() != "") {
        $("#" + name + "Error").html("");
        error.appendTo("#" + name + "Error");
      }
    }
  });

  $("#channelUsageReportForm").validate({
    //debug : true,
    success: "valid",
    ignoreTitle: true,
    rules: {
      "startDisplay": {
        required: true
      },
      "endDisplay": {
        required: true,
        dateRange: true
      },
      "channelId":{
        required: true
      },
      "billingGroupId":{
        required: true
      }
    },
    messages: {
      "startDisplay": {
        required: i18n.errors.validationStartDate
      },
      "endDisplay": {
        required: i18n.errors.validationEndDate,
        dateRange: i18n.errors.reportEnterValidDateRange
      },
      "channelId": {
        required: i18n.errors.validationChannel
      },
      "billingGroupId": {
        required: i18n.errors.validationBillingGroup
      }
    },
    errorPlacement: function(error, element) {
      var name = element.attr('id');
      name = ReplaceAll(name, ".", "\\.");
      if (error.html() != "") {
        $("#" + name + "Error").html("");
        error.appendTo("#" + name + "Error");
      }
    }
  });
  
  $("#customreport").change(function() {
    $("#month").val("");
    $("#year").val("");
    $("#date").val("");
    var val = $("#customreport").val();
    var text = $("#customreport option[value='" + val + "']").text();
    var type = jQuery.trim(text.substr(text.lastIndexOf('-') + 1));
    if (type == 'MONTHLY') {
      $("#dateparam").hide();
      $("#monthparam").show();
    } else if (type == 'DAILY') {
      $("#monthparam").hide();
      $("#dateparam").show();
    } else {
      $("#monthparam").hide();
      $("#dateparam").hide();
    }
    $("#reportdownload").removeAttr('href');
    $("#reportdownload").addClass("commonbuttondisabled");
    $("#reportdownload").removeClass("commonbutton");
    $("#reportemail").removeAttr('onClick');
    $("#reportemail").removeAttr('name');
    $("#reportemail").addClass("commonbuttondisabled");
    $("#reportemail").removeClass("commonbutton");
  });

  $("#customReportForm").validate({
    //debug : true,
    success: "valid",
    ignoreTitle: true,
    rules: {
      "customreport": {
        required: true
      },
      "month": {
        required: function(element) {
          var val = $("#customreport").val();
          var text = $("#customreport option[value='" + val + "']").text();
          var type = jQuery.trim(text.substr(text.lastIndexOf('-') + 1));
          return (type == 'MONTHLY');
        }
      },
      "year": {
        required: function(element) {
          var val = $("#customreport").val();
          var text = $("#customreport option[value='" + val + "']").text();
          var type = jQuery.trim(text.substr(text.lastIndexOf('-') + 1));
          return (type == 'MONTHLY');
        }
      },
      "date": {
        required: function(element) {
          var val = $("#customreport").val();
          var text = $("#customreport option[value='" + val + "']").text();
          var type = jQuery.trim(text.substr(text.lastIndexOf('-') + 1));
          return (type == 'DAILY');
        }
      }
    },
    messages: {
      "customreport": {
        required: i18n.errors.validationCustomReport
      },
      "month": {
        required: i18n.errors.validationMonth
      },
      "year": {
        required: i18n.errors.validationYear
      },
      "date": {
        required: i18n.errors.validationDate
      }
    },
    errorPlacement: function(error, element) {
      var name = element.attr('id');
      name = ReplaceAll(name, ".", "\\.");
      if (error.html() != "") {
        $("#" + name + "Error").html("");
        error.appendTo("#" + name + "Error");
      }
    }
  });

  $(function() {
    $('#date').datepicker({
      duration: '',
      showOn: "button",
      buttonImage: "/portal/images/calendar_icon.png",
      buttonImageOnly: true,
      buttonText: i18n.text.startDateButtonText,
      dateFormat: g_dictionary.friendlyDate,
      showTime: true,
      stepMinutes: 1,
      stepHours: 1,
      time24h: false
    });
  });

  $(function() {
    $("#email-dialog-modal").dialog({
      modal: true,
      resizable: false,
      autoOpen: false,
      buttons: {
        "Done": function() {
          var isValid = validateEmailIds();
          if (isValid) {
            $("#emailidsError").html("");
            $("#emailidsError").hide();
            sendEmail();
            $(this).dialog("close");
          } else {
            $("#emailidsError").show();
          }
        }
      }
    });
  });

  $("#csvgenerate").unbind("click").bind("click", function(event) {
    if($("#channelUsageReportForm").valid()){
      $(".downloadmsg").html(i18n.text.downloadstartshortly);  
    }
  }); 
  $("#channelId").unbind("click").bind("click", function(event) {
    $(".downloadmsg").empty();
  });
  $("#billingGroupId").unbind("click").bind("click", function(event) {
    $(".downloadmsg").empty();
  });
  
  $("#channelusageByBillingGroup").unbind("click").bind("click", function(event) {
    deactivateThirdMenuItem("channelusageByChannel");
    activateThirdMenuItem("channelusageByBillingGroup");
    $("#channelUsageReportForm").trigger('reset');
    $("#start").val("");
    $("#end").val("");
    $(".downloadmsg").empty();
    $("#filterByChannel").hide();
    $("#filterByBillingGroup").show();
  }); 
  
  $("#channelusageByChannel").unbind("click").bind("click", function(event) {
    deactivateThirdMenuItem("channelusageByBillingGroup");
    activateThirdMenuItem("channelusageByChannel");
    $("#channelUsageReportForm").trigger('reset');
    $("#start").val("");
    $("#end").val("");
    $(".downloadmsg").empty();
    $("#filterByBillingGroup").hide();
    $("#filterByChannel").show();
  });
  
  $("#spendByProduct").unbind("click").bind("click", function(event) {
    deactivateThirdMenuItem("spendByProductBundle");
    activateThirdMenuItem("spendByProduct");
    $("#reportForm").attr("action", "/portal/portal/reports/product_usage");
    $("#start").val("");
    $("#end").val("");
    window.location="/portal/portal/reports/product_usage";
  }); 
  
  $("#spendByProductBundle").unbind("click").bind("click", function(event) {
    deactivateThirdMenuItem("spendByProduct");
    activateThirdMenuItem("spendByProductBundle");
    $("#reportForm").attr("action", "/portal/portal/reports/productbundle_usage");
    $("#start").val("");
    $("#end").val("");
    window.location="/portal/portal/reports/productbundle_usage";
  });
  
});

function validateEmailIds() {
  var errorMsg;
  var result = true;
  var emailIds = $("#emailids").val().split(';');
  if (emailIds == null || emailIds == "") {
    errorMsg = i18n.errors.emptyEmailField;
    $("#emailidsError").html(errorMsg);
    return false;
  }
  for (var i = 0; i < emailIds.length; i++) {
    result = validate_email(emailIds[i]);
    if (!result) {
      errorMsg = emailIds[i] + ' ' + i18n.errors.invalidEmailIds;
      $("#emailidsError").html(errorMsg);
      break;
    }
  }
  return result;
}

function validate_email(newemail) {
  var apos = newemail.indexOf("@");
  var dotpos = newemail.lastIndexOf(".");
  if (apos < 1 || dotpos - apos < 2) {
    return false;
  } else {
    return true;
  }
}

function generateCustReport(form, event) {
  if (event.preventDefault) {
    event.preventDefault();
  } else {
    event.returnValue = false;
  }
  if ($("#customReportForm").valid()) {
    $.ajax({
      type: "GET",
      url: $(form).attr("action"),
      data: $(form).serialize(),
      dataType: "html",
      success: function(result) {
        $("#reportgenerate").val(i18n.text.generateButtonText);
        $("#reportgenerate").attr("disabled", false);
        if (result == 'none') {
          popUpDialogForAlerts("dialog_info", i18n.alerts.noData);
        } else if (result == 'failure') {
          popUpDialogForAlerts("dialog_info", i18n.errors.generateFailure);
        } else {
          popUpDialogForAlerts("dialog_info",i18n.alerts.generateSuccess );
          var url = $(form).attr("action");
          var downloadURL = url.substring(0, url.lastIndexOf("/")) + "/download_custom_report/" + result;
          var emailURL = url.substring(0, url.lastIndexOf("/")) + "/email_custom_report/" + result;
          $("#reportdownload").attr('href', downloadURL);
          $("#reportdownload").removeClass("commonbuttondisabled");
          $("#reportdownload").addClass("commonbutton");
          $("#reportemail").attr('onClick', 'provideEmailIds()');
          $("#reportemail").attr('name', emailURL);
          $("#reportemail").removeClass("commonbuttondisabled");
          $("#reportemail").addClass("commonbutton");
        }
      },
      error: function(result) {
        popUpDialogForAlerts("dialog_info", i18n.errors.generateFailure);
      }
    });
  }
}

function provideEmailIds(emailURL) {
  $("#email-dialog-modal").dialog('open');

}

function sendEmail() {
  var emailURL = $("#reportemail").attr('name');
  var emailLinkText = $("#reportemail").text();
  $("#reportemail").text($("#reportemail").attr('rel'));
  $.ajax({
    type: "GET",
    url: emailURL,
    data: {
      'emailIds': $("#emailids").val()
    },
    dataType: "html",
    success: function(result) {
      $("#reportemail").text(emailLinkText);
      if (result == 'failure') {
        popUpDialogForAlerts("dialog_info", i18n.errors.sendemailFailed);
      } else {
        popUpDialogForAlerts("dialog_info", i18n.alerts.sendemailSuccess);
      }
    },
    error: function(result) {
      $("#reportemail").text(emailLinkText);
      popUpDialogForAlerts("dialog_info", i18n.errors.sendemailfailed);
    }
  });
 
}

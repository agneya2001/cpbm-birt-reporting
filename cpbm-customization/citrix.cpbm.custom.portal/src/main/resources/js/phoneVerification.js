/*
*  Copyright © 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/
$(document).ready(function() {
  $("#passwdResetForm input:first").focus();
  $("#passwdResetForm").validate({
    success: "valid",
    ignoreTitle: true,
    rules: {
      "password": {
        required: true,
        password: true
      },
      "password_confirm": {
        required: true,
        equalTo: "#password"
      },
      "userEnteredPhoneVerificationPin": {
        required: true
      }
    },
    messages: {
      "password": {
        required: i18n.errors.auth.providePassword,
        password: i18n.errors.auth.password
      },
      "password_confirm": {
        required: i18n.errors.auth.confirmPassword,
        password: i18n.errors.auth.password,
        equalTo: i18n.errors.auth.passwordConfirmEqualTo
      },
      "userEnteredPhoneVerificationPin": {
        required: i18n.errors.auth.pinRequired
      }
    },
    errorPlacement: function(error, element) {
      var name = element.attr('id');
      name = ReplaceAll(name, ".", "\\.");
      error.appendTo("#" + name + "Error");
    }
  });

  $("#phoneVerificationCallByUser").click(function() {

    $("#phoneVerificationCallByUser").html("<span class='call_icon'></span>" + i18n.labels.phoneVerificationCalling);

    $.ajax({
      type: "POST",
      url: "/portal/portal/request_call_by_user",
      data: {
        "userName": null,
        "pickValFromReset": "pick"
      },
      dataType: "html",

      success: function(response) {
        popUpDialogForAlerts("dialog_info", jQuery.parseJSON(response).message);
      },

      error: function(html) {
        popUpDialogForAlerts("dialog_info", i18n.errors.auth.callFailed);
      },

      complete: function(xhr, status) {
        $("#phoneVerificationCallByUser").html("<span class='call_icon'></span>" + i18n.labels.phoneVerificationCallMe);
      }
    });
  });

  $("#phoneVerificationSMSByUser").click(function() {

    $("#phoneVerificationSMSByUser").html("<span class='text_icon'></span>" + i18n.labels.phoneVerificationSending);
    $.ajax({
      type: "POST",
      url: "/portal/portal/request_sms_by_user",
      data: {
        "userName": null,
        "pickValFromReset": "pick"
      },
      dataType: "html",

      success: function(response) {
        popUpDialogForAlerts("dialog_info", jQuery.parseJSON(response).message);
      },

      error: function(html) {
        popUpDialogForAlerts("dialog_info", i18n.errors.auth.textMessageFailed);
      },

      complete: function(xhr, status) {
        $("#phoneVerificationSMSByUser").html("<span class='text_icon'></span>" + i18n.labels.phoneVerificationTextMe);
      }
    });
  });

  function verifyPIN() {
    $("#verificationStatusSuccess").hide();
    $("#verificationStatusFailed").hide();
    var userEnteredPIN = $("#userEnteredPhoneVerificationPin").val();
    if (typeof(userEnteredPIN) == "undefined" || userEnteredPIN == "") {
      $("#verificationStatusFailed").show();
      return false;
    }
    var retVal = verifyPINStatus();
    if (!retVal) {
      $("#verificationStatusFailed").show();
      retVal = false;
    }
    return retVal;
  }

  $("#ResetsubmitButton").click(function() {
    if (typeof(phoneVerificationEnabled) != "undefined" && phoneVerificationEnabled == true) {
      var isPINVerified = verifyPIN();
      if (typeof(isPINVerified) == "undefined" || isPINVerified == false) {
        return false;
      }
    }
    if ($("#passwdResetForm").valid()) {
      $("#passwdResetForm").submit();
    }
  });

  $(':input').keypress(function(e) {
    if (e.keyCode == 13) {
      if (typeof(phoneVerificationEnabled) != "undefined" && phoneVerificationEnabled == true) {
        var isPINVerified = verifyPIN();
        if (typeof(isPINVerified) == "undefined" || isPINVerified == false) {
          return false;
        }
      }
      if ($("#passwdResetForm").valid()) {
        $("#passwdResetForm").submit();
      }
    }
  });
});

function verifyPINStatus() {
  var userName = $("#username").val();
  var userEnteredPIN = $("#userEnteredPhoneVerificationPin").val();
  var retVal = false;
  $.ajax({
    type: "GET",
    url: "/portal/portal/phoneverification/verifyPINForUnlock",
    data: {
      "PIN": userEnteredPIN
    },
    dataType: "html",
    async: false,
    success: function(result) {
      if (result == "success") {
        retVal = true;
      } else {
        retVal = false;
      }
    },
    error: function(html) {
      retVal = false;
    }
  });
  return retVal;
}

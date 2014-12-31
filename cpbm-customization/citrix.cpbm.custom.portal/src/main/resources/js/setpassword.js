/*
*  Copyright © 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/
$(document).ready(function() {
  $("#passwdResetForm input:first").focus();
  $("#passwdResetForm").validate( {
    success : "valid",
    ignoreTitle : true,
    rules : {
      "password" : {
        required : true,
        password : true
      },
      "password_confirm" : {
        required : true,
        equalTo : "#password"
      }
    },
    messages : {
      "password" : {
        required : i18n.errors.auth.providePassword,
        password : i18n.errors.auth.password
      },
      "password_confirm" : {
        required : i18n.errors.auth.confirmPassword,
        password : i18n.errors.auth.password,
        equalTo : i18n.errors.auth.passwordConfirmEqualTo
      }
    },
    errorPlacement : function(error, element) {
      var name = element.attr('id');
      name = ReplaceAll(name, ".", "\\.");
      error.appendTo("#" + name + "Error");
    }
  });

  $("#ResetsubmitButton").click(function() {
    if ($("#passwdResetForm").valid()) {
      $("#passwdResetForm").submit();
    }
  });

  $(':input').keypress(function(e) {
    if (e.keyCode == 13) {
      if ($("#passwdResetForm").valid()) {
        $("#passwdResetForm").submit();
      }
    }
  });
});
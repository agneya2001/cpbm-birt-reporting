/*
*  Copyright � 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/
$(document).ready(function() {



  $("#profileDisplayDiv").dialog({
    type: 'POST',
    autoOpen: true,
    width: 600,
    height: 350,
    modal: true,
    title: 'Profile Details',
    buttons: {
      "Cancel": function() {
        $(this).dialog('close');
      },
      "Edit": function() {

        $('#editProfileForm').submit();

        $.ajax({
          type: 'GET',
          url: form.attr('action'),
          data: form.serialize(),
          dataType: 'text',
          success: function(data) {
            form.parent().dialog('close');
          },
          error: function(request) {
            if (request.status == 412) {
              form.hide();
              message.text(request.responseText);
            }
          }
        });
      }
    }
  });

  $("#profileEditDiv").dialog({
    type: 'POST',
    autoOpen: true,
    width: 720,
    modal: true,
    title: i18n.profile.edit.title,
    buttons: {
      "Cancel": function() {
        $(this).dialog('close');
      },
      "Save": function() {
        $('#userForm').submit();
        $.ajax({
          type: 'POST',
          url: form.attr('action'),
          data: form.serialize(),
          dataType: 'text',
          success: function(data) {
            form.parent().dialog('close');
          },
          error: function(request) {
            if (request.status == 412) {
              form.hide();
              message.text(request.responseText);
            }
          }
        });
      }
    }
  });

  if ($("#user\\.atCompanyLocation1").is(":not(:checked)")) {
    $("#address").show();
  }
  $("#user\\.atCompanyLocation1").click(function() {
    if ($(this).is(':checked')) {
      $("#address").slideUp();
    } else {
      $("#address").slideDown();
    }
  });
  if ($("#user\\.trial1").is(":checked")) {
    $("#trial").show();
  }
  $("#user\\.trial1").click(function() {
    if ($(this).is(':checked')) {
      $("#trial").slideDown();
    } else {
      $("#trial").slideUp();
    }
  });
  $(".actions_dropdown_button").click(function() {
    if ($(".actions_dropdown:visible").length != 0) {
      $(".actions_dropdown").hide();
    } else {
      $(".actions_dropdown").show();
    }
  });
  $("#userForm").validate({
    //debug : true,
    success: "valid",
    ignoreTitle: true,
    rules: {
      "user.firstName": {
        required: true,
        minlength: 1
      },
      "user.lastName": {
        required: true,
        minlength: 1
      },
      "user.email": {
        required: true,
        email: true
      },
      "user.phone": {
        required: false,
        phone: true
      },
      "user.username": {
        required: true,
        minlength: 5,
        maxlength: 255,
        remote: {
          url: '/portal/portal/validate_username'
        }
      },
      "address.street1": {
        required: function() {
          return $("#user\\.atCompanyLocation1").is(':not(:checked)');
        }
      },
      "userProfile": {
        required: true

      },
      "address.postalCode": {
        required: function() {
          return $("#user\\.atCompanyLocation1").is(':not(:checked)');
        }
      },
      "address.country": {
        required: function() {
          return $("#user\\.atCompanyLocation1").is(':not(:checked)');
        }
      },
      "trialCode": {
        required: function() {
          return $("#user\\.trial1").is(':checked');
        }
      }
    },
    messages: {
      "user.firstName": {
        required: "Please specify your first name"
      },
      "user.lastName": {
        required: "Please specify your last name"
      },
      "user.email": {
        required: "We need your email address to contact you",
        email: "Your email address must be in the format of name@domain.com"
      },
      "user.username": {
        required: "A username is required",
        remote: "An account with this username already exists"
      },
      "address.street1": {
        required: "A Street Address is required"
      },
      "userProfile": {
        required: "A User Profile is required"
      },
      "address.postalCode": {
        required: "A Postal Code is required"
      },
      "trialCode": {
        required: "A Trial Code is required to create trial user "
      }
    }
  });
  $("#userPasswordForm").validate({
    //debug : true,
    success: "valid",
    ignoreTitle: true,
    rules: {
      "user.clearPassword": {
        required: function() {
          return $("#userForm").is(".profileForm");
        },
        password: true
      },
      "oldPassword": {
        required: function() {
          return $.trim($("#clearPassword").val()).length != 0;
        }
      },
      "clearPassword": {
        required: function() {
          return $.trim($("#oldPassword").val()).length != 0;
        },
        password: true
      },
      "password_confirm": {
        required: function() {
          return $.trim($(".clearPassword").val()).length != 0;
        },
        equalTo: ".clearPassword"
      }
    },
    messages: {
      "user.clearPassword": {
        required: "Provide a password"
      },
      "oldPassword": {
        required: "Provide current password"
      },
      "clearPassword": {
        required: "Provide a password"
      },
      "password_confirm": {
        required: "Confirm password",
        equalTo: "Passwords must match"
      }
    }
  });

  setTimeout('$("#userForm input:visible:first").focus()', 1000);

});

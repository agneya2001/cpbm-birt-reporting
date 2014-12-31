/*
*  Copyright � 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/
$(document).ready(function() {

  $("#syncAddressli").hide();
  $("#billingAddressLabel").hide();
  $("#secondaryCountry").hide();
  $("#secondaryState").hide();
  $("#secondaryStreet1").hide();
  $("#secondaryStreet2").hide();
  $("#secondaryCity").hide();
  $("#secondaryPostalCode").hide();

  $("#allowSecondaryId").live("click", function(event) {
    if ($(this).is(':checked')) {
      $("#syncAddressli").show();
      $("#billingAddressLabel").show();
      $("#secondaryCountry").show();
      $("#secondaryState").show();
      $("#secondaryStreet1").show();
      $("#secondaryStreet2").show();
      $("#secondaryCity").show();
      $("#secondaryPostalCode").show();
      $("#secondaryAddressDetailsDiv").show();

    } else {
      $("#syncAddressli").hide();
      $("#billingAddressLabel").hide();
      $("#secondaryCountry").hide();
      $("#secondaryState").hide();
      $("#secondaryStreet1").hide();
      $("#secondaryStreet2").hide();
      $("#secondaryCity").hide();
      $("#secondaryPostalCode").hide();
      $("#secondaryAddressDetailsDiv").hide();

    }
  });

  $("#syncAddress").live("click", function(event) {
    if ($(this).is(':checked')) {
      syncAddress();
    } else {
      $('#secondaryAddress\\.country').val('');
      $('#secondaryAddress\\.state').val('');
      $('#secondaryAddress\\.street1').val('');
      $('#secondaryAddress\\.street2').val('');
      $('#secondaryAddress\\.city').val('');
      $('#secondaryAddress\\.postalCode').val('');
      $("#secondaryAddress\\.country").change();
    }
  });
  jQuery.validator.addMethod('prePaidAmount',
    function(value) {
      return Number(value) > 0;
    }, i18n.errors.register.prePaidAmount);

  var selectedMonth = $("#creditCardExpirationMonth option:selected").val();
  $("#creditCardExpirationMonth").change(function() {
    selectedMonth = $("#creditCardExpirationMonth option:selected").val();
  });
  jQuery.validator.addMethod("checkcardExp", function(value, element) {
    var month = "00";
    if (selectedMonth != '') {
      month = selectedMonth;
    }
    var expDate = month + '/1/' + value;
    var today = new Date();
    return Date.parse(today) < Date.parse(expDate);

  }, i18n.errors.register.checkcardExp);

  jQuery.validator.addMethod("postalcode", function(postalcode, element, param) {
    if (param == 'US') {
      return this.optional(element) || postalcode.match(/(^\d{5}(-\d{4})?$)/);
    } else if (param == 'CA') {
      return this.optional(element) || postalcode.match(
        /(^[ABCEGHJKLMNPRSTVXYabceghjklmnpstvxy]{1}\d{1}[A-Za-z]{1} ?\d{1}[A-Za-z]{1}\d{1})$/);
    } else {
      return this.optional(element) || postalcode.length > 0 && /^[a-zA-Z\d]+$/.test(postalcode);
    }
  }, i18n.errors.register.postalcode);



  if ($('#user\\.address\\.country').val() == 'US' || $('#user\\.address\\.country').val() == 'CA' ||
    $('#user\\.address\\.country').val() == 'AU' || $('#user\\.address\\.country').val() == 'IN' || $(
      '#user\\.address\\.country').val() == 'JP') {
    $("#stateInput").hide();
    $("#user\\.address\\.country").defautlinkstates("#userAddressStateSelect");
    $("#userAddressStateSelect").val($('#user\\.address\\.state').val());
    if ($('#user\\.address\\.country').val() == 'JP') {
      $("#otherstateDiv").hide();
      $("#JPstateDiv").show();
    } else {
      $("#otherstateDiv").show();
      $("#JPstateDiv").hide();
    }
    $("#stateSelect").show();

  } else {
    $("#stateSelect").hide();
    $("#stateInput").show();
  }


  $("#user\\.address\\.country").change(function() {
    if ($('#user\\.address\\.country').val() == 'US' || $('#user\\.address\\.country').val() == 'CA' ||
      $('#user\\.address\\.country').val() == 'AU' || $('#user\\.address\\.country').val() == 'IN' || $(
        '#user\\.address\\.country').val() == 'JP') {
      $("#stateInput").hide();
      if ($('#user\\.address\\.country').val() == 'JP') {
        $("#otherstateDiv").hide();
        $("#JPstateDiv").show();
      } else {
        $("#otherstateDiv").show();
        $("#JPstateDiv").hide();
      }
      $("#stateSelect").show();

    } else {
      $("#stateSelect").hide();
      $("#stateInput").show();
    }

  });

  $("#user\\.address\\.country").linkToStates("#userAddressStateSelect");

  $('#userAddressStateSelect').change(function() {
    $('#user\\.address\\.state').val($('#userAddressStateSelect').val());
    $('#user\\.address\\.state').valid();
  });

  if ($('#secondaryAddress\\.country').val() == 'US' || $('#secondaryAddress\\.country').val() == 'CA' ||
    $('#secondaryAddress\\.country').val() == 'AU' || $('#secondaryAddress\\.country').val() == 'IN' || $(
      '#secondaryAddress\\.country').val() == 'JP') {
    $("#stateSecondaryInput").hide();
    $("#secondaryAddress\\.country").defautlinkstates("#tenantSecondaryAddressStateSelect");
    $("#tenantAddressStateSelect").val($('#secondaryAddress\\.state').val());
    if ($('#secondaryAddress\\.country').val() == 'JP') {
      $("#otherstateSecondaryDiv").hide();
      $("#JPSecondarystateDiv").show();
    } else {
      $("#otherstateSecondaryDiv").show();
      $("#JPSecondarystateDiv").hide();
    }
    $("#stateSecondarySelect").show();

  } else {
    $("#stateSecondarySelect").hide();
    $("#stateSecondaryInput").show();
  }

  $("#secondaryAddress\\.country").change(function() {
    $('#secondaryAddress\\.state').val('');
    if ($('#secondaryAddress\\.country').val() == 'US' || $('#secondaryAddress\\.country').val() == 'CA' ||
      $('#secondaryAddress\\.country').val() == 'AU' || $('#secondaryAddress\\.country').val() == 'IN' || $(
        '#secondaryAddress\\.country').val() == 'JP') {
      $("#stateSecondaryInput").hide();
      if ($('#secondaryAddress\\.country').val() == 'JP') {
        $("#otherstateSecondaryDiv").hide();
        $("#JPSecondarystateDiv").show();
      } else {
        $("#otherstateSecondaryDiv").show();
        $("#JPSecondarystateDiv").hide();
      }
      $("#stateSecondarySelect").show();
    } else {
      $("#stateSecondarySelect").hide();
      $("#stateSecondaryInput").show();
    }

  });

  $("#secondaryAddress\\.country").linkToStates("#tenantSecondaryAddressStateSelect");

  $('#tenantSecondaryAddressStateSelect').change(function() {
    $('#secondaryAddress\\.state').val($('#tenantSecondaryAddressStateSelect').val());
  });


  jQuery.validator.addMethod('validateState',
    function(value, element) {

      if (element.name == 'userAddressStateSelect') {
        var country = $('#user\\.address\\.country').val();
        if (country == 'US' || country == 'CA' || country == 'AU' || country == 'IN' || country == 'JP') {
          return value.length > 0;
        } else {
          return true;
        }

      }
      return true;

    }, i18n.errors.register.validateState);

  jQuery.validator.addMethod('validateConfirmEmail',
      function(value, element, param) {
    if (value.toLowerCase() == param.toLowerCase()){
      return true;       
    }
    return false;
  }, i18n.errors.register.user.confirmEmailEqualTo);

  jQuery.validator.addMethod('validateUsername',
    function(value, element) {
      return value.length > 0 && /^[a-zA-Z0-9_@\.-]+$/.test(value);
    }, i18n.errors.register.user.validateUsername);

  var formValidator = $("#registrationForm").validate({
    //debug : false,
    success: "valid",
    ignoreTitle: true,
    rules: {
      "user.firstName": {
        required: true,
        flname: true,
        maxlength: 255
      },
      "user.lastName": {
        required: true,
        flname: true,
        maxlength: 255
      },
      "user.email": {
        required: true,
        email: true
      },
      "confirmEmail": {
        required: true,
        email: true,
        validateConfirmEmail: function() {
          return $('#user\\.email').val();
        }
      },
      "user.username": {
        required: true,
        minlength: 5,
        maxlength: 255,
        validateUsername: true,
        remote: {
          url: '/portal/portal/validate_username'
        }
      },
      "trialCode": {
        required: function() {
          if (trialAcountSelected == 'true') {
            return true;
          } else {
            return false;
          }
        }
      },
      "user.clearPassword": {
        required: true,
        password: true,
        notEqualTo: "#user\\.username"
      },
      "passwordconfirm": {
        required: true,
        equalTo: "#user\\.clearPassword"
      },
      "tenant.name": {
        required: true
      },
      "user.phone": {
        required: true,
        phone: true
      },
      "currency": {
        required: true
      },
      "countryCode": {
        required: true,
        countryCode: true
      },
      "userEnteredPhoneVerificationPin": {
        required: true
      },
      "acceptedTerms": {
        required: true
      },

      "user.address.street1": {
        required: true
      },
      "user.address.city": {
        required: true
      },
      "user.address.state": {
        required: true
      },
      "user.address.postalCode": {
        required: true,
        postalcode: function() {
          return $('#user\\.address\\.country').val();
        }
      },
      "user.address.country": {
        required: true
      },
      "userAddressStateSelect": {
        validateState: true
      },
      "secondaryAddress.country": {
        required: function() {
          if ($("#allowSecondaryId").is(':checked'))
            return true;
          return false;
        }
      },
      "secondaryAddress.state": {
        required: function() {
          if ($("#allowSecondaryId").is(':checked'))
            return true;
          return false;
        }
      },
      "secondaryAddress.street1": {
        required: function() {
          if ($("#allowSecondaryId").is(':checked'))
            return true;
          return false;
        }
      },
      "secondaryAddress.city": {
        required: function() {
          if ($("#allowSecondaryId").is(':checked'))
            return true;
          return false;
        }
      },
      "secondaryAddress.postalCode": {
        required: function() {
          if ($("#allowSecondaryId").is(':checked'))
            return true;
          return false;
        }
      }
    },
    messages: {
      "user.firstName": {
        required: i18n.errors.register.user.firstName,
        flname: i18n.errors.register.user.flnameValidationError
      },
      "user.lastName": {
        required: i18n.errors.register.user.lastName,
        flname: i18n.errors.register.user.flnameValidationError
      },
      "user.email": {
        required: i18n.errors.register.user.emailRequired,
        email: i18n.errors.register.user.email
      },
      "confirmEmail": {
        required: i18n.errors.register.user.confirmEmail,
        email: i18n.errors.register.user.email,
        equalTo: i18n.errors.register.user.confirmEmailEqualTo
      },
      "user.username": {
        required: i18n.errors.register.user.username,
        remote: i18n.errors.register.user.usernameRemote,
        minlength: i18n.errors.register.user.minLengthUsername,
        validateUsername: i18n.errors.register.user.validateUsername
      },
      "user.clearPassword": {
        required: i18n.errors.register.user.clearPassword,
        password: i18n.errors.register.user.passwordValidationeError,
        notEqualTo: i18n.errors.register.user.passwordequsername
      },

      "passwordconfirm": {
        required: i18n.errors.register.user.passwordConfirm,
        password: i18n.errors.register.user.passwordValidationeError,
        equalTo: i18n.errors.register.user.passwordConfirmEqualTo
      },
      "tenant.name": {
        required: i18n.errors.register.tenant.name
      },
      "user.phone": {
        required: i18n.errors.register.user.phone,
        phone: i18n.errors.register.user.phoneValidationError
      },
      "currency": {
        required: i18n.errors.register.currency
      },
      "countryCode": {
        required: i18n.errors.register.countryCode,
        countryCode: i18n.errors.register.countryCodeValidationError
      },
      "userEnteredPhoneVerificationPin": {
        required: i18n.errors.register.phonePin
      },
      "trialCode": {
        required: i18n.errors.register.trailCodeRequired,
        remote: i18n.errors.register.trailCode
      },
      "acceptedTerms": {
        required: i18n.errors.register.acceptedTerms
      },
      "user.address.street1": {
        required: i18n.errors.register.user.address.street1
      },
      "user.address.city": {
        required: i18n.errors.register.user.address.city
      },
      "user.address.state": {
        required: i18n.errors.register.user.address.state
      },
      "user.address.postalCode": {
        required: i18n.errors.register.user.address.postalCode,
        postalCode: i18n.errors.register.user.address.postalCode
      },
      "user.address.country": {
        required: i18n.errors.register.user.address.country
      },
      "secondaryAddress.street1": {
        required: i18n.errors.register.secondaryAddress.street1
      },
      "secondaryAddress.city": {
        required: i18n.errors.register.secondaryAddress.city
      },
      "secondaryAddress.postalCode": {
        required: i18n.errors.register.secondaryAddress.postalCode,
        postalcode: i18n.errors.register.secondaryAddress.postalCode
      },
      "secondaryAddress.state": {
        required: i18n.errors.register.secondaryAddress.state
      },
      "secondaryAddress.country": {
        required: i18n.errors.register.secondaryAddress.country
      }
    },
    errorPlacement: function(error, element) {
      var name = element.attr('id');
      name = ReplaceAll(name, ".", "\\.");
      if (error.html() != "") {
        error.appendTo("#" + name + "Error");
      }
    }
  });



  $(function() {
    $('#user\\.email, #confirmEmail, #user\\.clearPassword, #passwordconfirm').bind('cut copy paste', function(e) {
      e.preventDefault();
    });
  });

  $("#backtouserinfo").click(function(event) {
    $("form").validate().cancelSubmit = true;
  });

  $("#continuebutton").click(function() {
    if ($("#registrationForm").valid()) {
      $("#registrationForm").submit();
    }
  });
  $("#registrationSubmit").click(function() {

    if ($("#registrationForm").valid()) {
      if (typeof(phoneVerificationEnabled) != "undefined" && phoneVerificationEnabled == true) {
        var isPINVerified = verifyPIN();
        if (typeof(isPINVerified) == "undefined" || isPINVerified == false) {
          return false;
        }
      }

      $("#registrationForm").submit();
    }
  });

  $("#accountTypeSubmit").click(function() {
    $("#accountTypeForm").submit();
  });


  var tncButtonCallBacks = {};
  tncButtonCallBacks[i18n.errors.register.tncDialog.buttons.ok] = function() {
    $(this).dialog('close');
  };
  $("#tncDialog").dialog({
    autoOpen: false,
    width: 400,
    modal: true,
    draggable: true,
    resizable: false,
    title: i18n.errors.register.tncDialog.title,
    open: function(event, ui) {
      $(".ui-dialog-titlebar-close").hide();
    },
    buttons: tncButtonCallBacks
  });
  $("#tncLink a").click(function() {
    $("#tncDialog").dialog('open');
  });

  $("#phoneVerificationCallByUser").click(function() {
    var username = $("#username").val();
    if (username == "" || username == null) {
      popUpDialogForAlerts("dialog_info", i18n.errors.register.user.username);
      return;
    }
    $("#phoneVerificationCallByUser").html("<span class='call_icon'></span>" + i18n.labels.phoneVerificationCalling);

    $.ajax({
      type: "POST",
      url: "/portal/portal/request_call_by_user",
      data: {
        "userName": username
      },
      dataType: "text/html",
      success: function(response) {
        popUpDialogForAlerts("dialog_info", jQuery.parseJSON(response).message );
      },
      error: function(html) {
        popUpDialogForAlerts("dialog_info", i18n.errors.register.callFailed);
      },
      complete: function(xhr, status) {
        $("#phoneVerificationCallByUser").html("<span class='call_icon'></span>" + i18n.labels.phoneVerificationCallMe);
      }
    });
  });

  $("#phoneVerificationSMSByUser").click(function() {
    var username = $("#username").val();
    if (username == "" || username == null) {
      popUpDialogForAlerts("dialog_info", i18n.errors.register.user.username );
      return;
    }

    $("#phoneVerificationSMSByUser").html("<span class='text_icon'></span>" + i18n.labels.phoneVerificationSending);
    $.ajax({
      type: "POST",
      url: "/portal/portal/request_sms_by_user",
      data: {
        "userName": username
      },
      dataType: "text/html",
      success: function(response) {
        popUpDialogForAlerts("dialog_info", jQuery.parseJSON(response).message);
      },
      error: function(html) {
        popUpDialogForAlerts("dialog_info", i18n.errors.register.textMessageFailed);
      },
      complete: function(xhr, status) {
        $("#phoneVerificationSMSByUser").html("<span class='text_icon'></span>" + i18n.labels.phoneVerificationTextMe);
      }
    });
  });

  $("#phoneVerificationCall").click(function() {
    var phoneNumber = $("#user\\.phone").val();
    var countryCode = $("#countryCode").text();
    if (phoneNumber == "" || phoneNumber == null) {
      popUpDialogForAlerts("dialog_info", i18n.errors.register.phoneDetails);
      return;
    }
    $("#phoneVerificationCall").html("<span class='call_icon'></span>" + i18n.labels.phoneVerificationCalling);

    var cntryCode = getOnlyNosFromThePhoneNoString(countryCode);
    var phoneNo = getOnlyNosFromThePhoneNoString(phoneNumber);
    $.ajax({
      type: "POST",
      url: "/portal/portal/request_call",
      data: {
        "phoneNumber": phoneNo,
        "countryCode": cntryCode
      },
      dataType: "html",
      success: function(response) {
        popUpDialogForAlerts("dialog_info", jQuery.parseJSON(response).message);
      },
      error: function(html) {
        popUpDialogForAlerts("dialog_info", i18n.errors.register.callFailed);
      },
      complete: function(xhr, status) {
        $("#phoneVerificationCall").html("<span class='call_icon'></span>" + i18n.labels.phoneVerificationCallMe);
      }
    });
  });

  $("#phoneVerificationSMS").click(function() {
    var phoneNumber = $("#user\\.phone").val();
    var countryCode = $("#countryCode").text();
    if (phoneNumber == "" || phoneNumber == null) {
      popUpDialogForAlerts("dialog_info", i18n.errors.register.phoneDetails);
      return;
    }
    var cntryCode = getOnlyNosFromThePhoneNoString(countryCode);
    var phoneNo = getOnlyNosFromThePhoneNoString(phoneNumber);

    $("#phoneVerificationSMS").html("<span class='text_icon'></span>" + i18n.labels.phoneVerificationSending);
    $.ajax({
      type: "POST",
      url: "/portal/portal/request_sms",
      data: {
        "phoneNumber": phoneNo,
        "countryCode": cntryCode
      },
      dataType: "html",
      success: function(response) {
        popUpDialogForAlerts("dialog_info", jQuery.parseJSON(response).message);
      },
      error: function(html) {
        popUpDialogForAlerts("dialog_info", i18n.errors.register.textMessageFailed);
      },
      complete: function(xhr, status) {
        $("#phoneVerificationSMS").html("<span class='text_icon'></span>" + i18n.labels.phoneVerificationTextMe);
      }
    });
  });


  function syncAddress() {
    $('#secondaryAddress\\.country').val($('#user\\.address\\.country').val());
    $("#secondaryAddress\\.country").change();
    $('#tenantSecondaryAddressStateSelect').val($('#user\\.address\\.state').val());
    $('#secondaryAddress\\.state').val($('#user\\.address\\.state').val());
    $('#secondaryAddress\\.street1').val($('#user\\.address\\.street1').val());
    $('#secondaryAddress\\.street2').val($('#user\\.address\\.street2').val());
    $('#secondaryAddress\\.city').val($('#user\\.address\\.city').val());
    $('#secondaryAddress\\.postalCode').val($('#user\\.address\\.postalCode').val());
  }

  function verifyPIN() {
    $("#verificationStatusSuccess").hide();
    $("#verificationStatusFailed").hide();
    var phoneNumber = $("#user\\.phone").val();
    var userEnteredPIN = $("#userEnteredPhoneVerificationPin").val();
    if (typeof(phoneNumber) == "undefined" || phoneNumber == "" || typeof(userEnteredPIN) == "undefined" ||
      userEnteredPIN == "") {
      $("#verificationStatusFailed .unverified").text(dictionary.phoneVerificationPinMandatory);
      $("#verificationStatusFailed").show();
      return false;
    }
    var nosInPhoneNoArray = phoneNumber.match(/\d+/g);
    var phoneNo = "";
    for (var i = 0; i <= nosInPhoneNoArray.length - 1; i++) {
      phoneNo += nosInPhoneNoArray[i];
    }
    var retVal = false;
    $.ajax({
      type: "POST",
      url: "/portal/portal/phoneverification/verify_pin",
      data: {
        "PIN": userEnteredPIN,
        "phoneNumber": phoneNo
      },
      dataType: "html",
      async: false,
      success: function(result) {
        if (result == "success") {
          $("#verificationStatusSuccess").show();
          retVal = true;
        } else {
          $("#verificationStatusFailed .unverified").text(dictionary.phoneVerificationFailed);
          $("#verificationStatusFailed").show();
          retVal = false;
        }
      },
      error: function(html) {
        $("#verificationStatusFailed").show();
        retVal = false;
      }
    });
    return retVal;
  }
});

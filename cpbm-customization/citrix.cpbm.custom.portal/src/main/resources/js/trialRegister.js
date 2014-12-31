/*
*  Copyright � 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/
$(document).ready(function() {
  $("a.resetForm").click(function() {
    $(".registration").each(function() {
      this.reset();
    });
    return true;
  });
  $(".tooltip_help").tooltip({
    effect: 'fade',
    position: 'center right',
    offset: [0, 30]
  });

  $("#tenant\\.syncBillingAddress1").click(function() {
    if ($(this).is(':checked')) {
      $("#billing_address").slideUp();
    } else {
      $("#billing_address").slideDown();
    }
  });

  jQuery.validator.addMethod("vatid", function(value, element) {

    var country = $('#tenant\\.address\\.country').val();
    if (country == 'AT') {
      return value.length == 9 && /^U/.test(value);
    } else if (country == 'BE') {
      return value.length == 9 && /\d/.test(value);
    } else if (country == 'DK') {
      return value.length == 8 && /\d/.test(value);
    } else if (country == 'FI') {
      return value.length == 8 && /\d/.test(value);
    } else if (country == 'FR') {
      return value.length == 11;
    } else if (country == 'DE') {
      return value.length == 9 && /\d/.test(value);
    } else if (country == 'EL') {
      return value.length == 9 && /\d/.test(value);
    } else if (country == 'IE') {
      return value.length == 9;
    } else if (country == 'IT') {
      return value.length == 11;
    } else if (country == 'LU') {
      return value.length == 8 && /\d/.test(value)
    } else if (country == 'NL') {
      return value.length == 12;
    }
    return true;



  }, i18n.errors.trialregister.TRVATIdMatch);

  jQuery.validator.addMethod("postalcode", function(postalcode, element, param) {
    if (param == 'US') {
      return this.optional(element) || postalcode.match(/(^\d{5}(-\d{4})?$)/);
    } else if (param == 'CA') {
      return this.optional(element) || postalcode.match(
        /(^[ABCEGHJKLMNPRSTVXYabceghjklmnpstvxy]{1}\d{1}[A-Za-z]{1} ?\d{1}[A-Za-z]{1}\d{1})$/);
    } else {
      return true;
    }
  }, i18n.errors.trialregister.TRPostalZipValid);

  $('#tenant\\.address\\.country').change(function() {
    if ($('#tenant\\.address\\.country').val() == 'US' || $('#tenant\\.address\\.country').val() == 'CA') {
      $("#stateInput").hide();
      $("#stateSelect").show();

    } else if ($('#tenant\\.address\\.country').val() != 'US' && $('#tenant\\.address\\.country').val() != 'CA') {
      $("#stateSelect").hide();
      $("#stateInput").show();
    }

  });
  $("#billingAddress\\.country").change(function() {
    if ($("#billingAddress\\.country").val() == 'US' || $("#billingAddress\\.country").val() == 'CA') {
      $("#billingStateInput").hide();
      $("#billingStateSelect").show();

    } else if ($("#billingAddress\\.country").val() != 'US' && $("#billingAddress\\.country").val() != 'CA') {
      $("#billingStateSelect").hide();
      $("#billingStateInput").show();
    }

  });
  $("#tenant\\.address\\.country").linkToStates("#tenantAddressStateSelect");
  $("#billingAddress\\.country").linkToStates("#billingAddressStateSelect");
  $('#tenantAddressStateSelect').change(function() {
    $('#tenant\\.address\\.state').val($('#tenantAddressStateSelect').val());
  });
  $('#billingAddressStateSelect').change(function() {
    $('#billingAddress\\.state').val($('#billingAddressStateSelect').val());
  });
  jQuery.validator.addMethod('validateState',
    function(value, element) {

      var disp = $("#dispositions input:radio:checked").val();

      if (element.name == 'billingAddressStateSelect') {
        var country = $('#billingAddress\\.country').val();
        if ((disp == 'NEW_ON_DEMAND' || disp == 'NEW_ON_DEMAND_PREPAID') && $("#tenant\\.syncBillingAddress1").is(
          ':not(:checked)') && (country == 'US' || country == 'CA')) {
          return value.length > 0;
        } else {
          return true;
        }

      }
      if (element.name == 'tenantAddressStateSelect') {
        var country = $('#tenant\\.address\\.country').val();
        if (country == 'US' || country == 'CA') {
          return value.length > 0;
        } else {
          return true;
        }
      }
      return true;

    }, i18n.errors.trialregister.TRStateSelect);



  var formValidator = $("#trialRegistrationForm").validate({
    //debug : false,
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
      "user.title": {
        required: true,
        minlength: 1
      },
      "user.email": {
        required: true,
        email: true
      },
      "confirmEmail": {
        required: true,
        email: true,
        equalTo: "#user\\.email"
      },
      "user.phone": {
        required: true,
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
      "user.clearPassword": {
        required: true,
        password: true,
        notEqualTo: "#user\\.username"
      },
      "password_confirm": {
        required: true,
        equalTo: "#user\\.clearPassword"
      },
      "tenant.address.street1": {
        required: true
      },
      "tenant.address.postalCode": {
        required: true,
        postalcode: function() {
          return ($('#tenant\\.address\\.country').val());
        }
      },
      "tenantAddressStateSelect": {
        validateState: true
      },
      "tenant.address.country": {
        required: true
      },
      "acceptedTerms": {
        required: true
      },
      "billingAddress.street1": {
        required: function() {
          return $("#tenant\\.syncBillingAddress").is(':not(:checked)');
        }
      },
      "billingAddress.postalCode": {
        required: function() {
          return $("#tenant\\.syncBillingAddress").is(':not(:checked)');
        },
        postalcode: function() {
          return $('#billingAddress\\.country').val();
        }
      },
      "billingAddress.country": {
        required: function() {
          return $("#tenant\\.syncBillingAddress").is(':not(:checked)');
        }
      },
      "billingAddressStateSelect": {
        validateState: true

      },
      "tenant.vatNo": {
        vatid: true
      }

    },
    messages: {
      "user.firstName": i18n.errors.trialregister.TRFirstName,
      "user.lastName": i18n.errors.trialregister.TRLastName,
      "user.title": i18n.errors.trialregister.TRTitle,
      "user.email": {
        required: i18n.errors.trialregister.TREmailAddressNeed,
        email: i18n.errors.trialregister.TREmailAddressInvalid
      },
      "confirmEmail": {
        required: i18n.errors.trialregister.TREmailConfirm,
        equalTo: i18n.errors.trialregister.TREmailMatch
      },
      "user.username": {
        required: i18n.errors.trialregister.TRUsernameRequired,
        remote: i18n.errors.trialregister.TRUsernameExists
      },
      "user.clearPassword": {
        required: i18n.errors.trialregister.TRPassword,
        notEqualTo: i18n.errors.trialregister.TRPasswordequsername
      },
      "user.phone": {
        required: i18n.errors.trialregister.TRPhoneNumReq
      },
      "password_confirm": {
        required: i18n.errors.trialregister.TRPasswordConfim,
        equalTo: i18n.errors.trialregister.TRPasswordMatch
      },
      "tenant.name": {
        required: i18n.errors.trialregister.TRCompanyNameRequired
      },
      "tenant.address.street1": {
        required: i18n.errors.trialregister.TRStreetAddressRequired
      },
      "tenant.address.postalCode": {
        required: i18n.errors.trialregister.TRPostalCodeRequired
      },
      "tenant.address.country": {
        required: i18n.errors.trialregister.TRCountrySelect
      },
      "billingAddress.street1": {
        required: i18n.errors.trialregister.TRBillingAddressRequired
      },
      "billingAddress.postalCode": {
        required: i18n.errors.trialregister.TRBillingPostalCodeRequired
      },
      "billingAddress.country": {
        required: i18n.errors.trialregister.TRBillingCountrySelect
      },
      "acceptedTerms": {
        required: i18n.errors.trialregister.TRTermsServiceAccept
      }
    },
    errorPlacement: function(error, element) {
      error.appendTo(element.parent());
    }
  });


});

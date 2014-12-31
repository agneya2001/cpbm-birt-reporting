/*
*  Copyright © 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/
$(document).ready(function() {

  $("#tokenRequestForm").submit(function() {
    if ($("#tokenRequestForm").valid()) {
      $("#submitbutton").attr('disabled', true);
    }
  });
  $("#tokenRequestForm").validate({

    //debug : false,
    success: "valid",
    ignoreTitle: true,
    rules: {
      "campaignCode": {
        required: true
      },
      "promotionSignup.name": {
        required: true
      },
      "promotionSignup.company": {
        required: true
      },
      "promotionSignup.email": {
        required: true,
        email: true
      },
      "promotionSignup.phone": {
        required: true,
        phone: true
      },
      "promotionSignup.currency": {
        required: true
      }
    },
    messages: {
      "campaignCode": {
        required: i18n.errors.trialtoken.compaignCodeReq
      },
      "promotionSignup.name": {
        required: i18n.errors.trialtoken.nameReq
      },
      "promotionSignup.company": {
        required: i18n.errors.trialtoken.companyNameReq
      },
      "promotionSignup.email": {
        required: i18n.errors.trialtoken.emailAddressNeed,
        email: i18n.errors.trialtoken.emailAddressInvalid
      },
      "promotionSignup.phone": {
        required: i18n.errors.trialtoken.phoneNumReq
      },
      "promotionSignup.currency": {
        required: i18n.errors.trialtoken.currencySel
      }
    },
    errorPlacement: function(error, element) {
      if (element.is(".inputCurrency"))
        error.appendTo("#currencyErrorLoc");
      else
        error.appendTo(element.parent());
    }


  });
});

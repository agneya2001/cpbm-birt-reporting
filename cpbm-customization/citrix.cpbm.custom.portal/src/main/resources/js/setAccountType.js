/*
*  Copyright © 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/
$(document).ready(function() {
  if ($("#accountTypes :radio:checked").length != 0) {
    $("." + $("#accountTypes :radio:checked").val().toLowerCase()).show();
  }
  $("#accountTypes input:radio").click(function() {
    $("#accountTypes :radio:not(:checked)").each(function() {
      var sections = $("." + $(this).val().toLowerCase());
      sections.hide()
        .find(":input:text").each(function() {
          $(this).val('').blur();
        });
      sections.find(":input:radio").attr('checked', false);
    });
    $("." + $(this).val().toLowerCase()).slideDown();
  });



  $("#changeAcctTypeDiv").dialog({
    autoOpen: false,
    modal: true,
    width: 600,
    height: 'auto',
    title: 'Change Account Type',
    buttons: {
      "Cancel": function() {
        $(this).dialog('close');
      },
      "Ok": function() {
        $('#setAccountTypeForm').submit();
      }
    }
  });
  var selectedMonth;
  $("#creditCardExpirationMonth").change(function() {
    selectedMonth = $("#creditCardExpirationMonth option:selected").val();

  });

  $(".changeAcctTypeAction").click(function() {
    $("#changeAcctTypeDiv").dialog('open');
  });


  jQuery.validator.addMethod('prePaidAmount',
    function(value) {
      var disp = $("#accountTypes input:radio:checked").val();
      var dd = (disp == 'ON_DEMAND_PREPAID');
      if (dd)
        return Number(value) > 0;
      else {
        return true;
      }
    }, i18n.errors.tenants.accountType.prePaidAmount.error);
  jQuery.validator.addMethod("checkcardExp", function(value, element) {
    var month = "00";
    if (selectedMonth != '') {
      month = selectedMonth;
    }
    var expDate = month + '/1/' + value;
    var today = new Date();
    return Date.parse(today) < Date.parse(expDate);



  }, 'Please provide valid expiration date ');
  $("#setAccountTypeForm").validate({
    //debug : true,
    success: "valid",
    ignoreTitle: true,
    rules: {
      "creditCard.creditCardType": {
        required: true
      },
      "creditCard.creditCardNumber": {
        required: true,
        creditcard: true
      },
      "creditCard.nameOnCard": {
        required: true
      },
      "creditCard.creditCardCVV": {
        required: true
      },
      "creditCard.creditCardExpirationMonth": {
        required: true
      },
      "creditCard.creditCardExpirationYear": {
        required: true,
        checkcardExp: true
      },
      "initialPayment": {
        required: function() {
          var disp = $("#accountTypes input:radio:checked").val();
          return (disp == 'ON_DEMAND_PREPAID');
        },
        prePaidAmount: true

      },
      "billingAddress.street1": {
        required: true
      },
      "billingAddress.postalCode": {
        required: true
      },
      "billingAddress.country": {
        required: true
      }
    },
    messages: {
      "creditCard.creditCardType": {
        required: i18n.errors.tenants.creditCard.creditCardType.error
      },
      "creditCard.creditCardNumber": {
        required: i18n.errors.tenants.creditCard.creditCardNumber.error
      },
      "creditCard.nameOnCard": {
        required: i18n.errors.tenants.creditCard.nameOnCard.error
      },
      "creditCard.creditCardCVV": {
        required: i18n.errors.tenants.creditCard.creditCardCVV.error
      },
      "creditCard.creditCardExpirationMonth": {
        required: i18n.errors.tenants.creditCard.creditCardExpirationMonth.error
      },
      "creditCard.creditCardExpirationYear": {
        required: i18n.errors.tenants.creditCard.creditCardExpirationYear.error
      },
      "initialPayment": {
        required: i18n.errors.tenants.initialPayment.error
      },
      "tenant.currency": {
        required: i18n.errors.tenants.tenant.currency.error
      },
      "billingAddress.street1": {
        required: i18n.errors.tenants.billingAddress.street1.error
      },
      "billingAddress.postalCode": {
        required: i18n.errors.tenants.billingAddress.postalCode.error
      },
      "billingAddress.country": {
        required: i18n.errors.tenants.billingAddress.country.error
      }
    },
    errorPlacement: function(error, element) {
      if (element.is(".inputs-card")) {
        if (error.html() == '') {
          $("#cardTypeErrorLoc").hide;
        } else {
          error.appendTo("#cardTypeErrorLoc");
          $("#cardTypeErrorLoc").show();
        }
      } else if (element.is(".expMonth")) {
        if (error.html() == '') {
          $("#ExpmonthErrorLoc").hide;
        } else {
          error.appendTo("#ExpmonthErrorLoc");
          $("#ExpmonthErrorLoc").show();
        }
      } else if (element.is(".cvverror")) {
        if (error.html() == '') {
          $("#cvvErrorLoc").hide;
        } else {
          error.appendTo("#cvvErrorLoc");
          $("#cvvErrorLoc").show();
        }
      } else if (element.is(".expyear")) {
        if (error.html() == '') {
          $("#expyearErrorLoc").hide;
        } else {
          error.appendTo("#expyearErrorLoc");
          $("#expyearErrorLoc").show();
        }

      } else {
        error.appendTo(element.parent());
      }
    }
  });



  setTimeout('$("#changeAccountTypeForm input:visible:first").focus()', 1000);
});

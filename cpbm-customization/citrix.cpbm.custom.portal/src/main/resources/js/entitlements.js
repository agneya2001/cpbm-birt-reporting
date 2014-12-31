/*
*  Copyright � 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/

$("#entitlementForm").validate({
  success: "valid",
  ignoreTitle: true,
  rules: {
    "productId": {
      required: true
    },
    "entitlement.includedUnits": {
      required: function() {
        return !$("#unlimitedUsage").is(":checked");
      },
      number: true,
      min: 1,
      max: 2147483647,
      digits: true

    }
  },
  messages: {
    "productId": {
      required: i18n.errors.bundleSelectProduct
    },
    "entitlement.includedUnits": {
      required: i18n.errors.bundleProvideIncludedUnits
    }
  },
  errorPlacement: function(error, element) {
    var name = element.attr('id');
    name = ReplaceAll(name, ".", "\\.");
    if (name != "") {
      error.appendTo("#" + name + "Error");
    } else {
      error.appendTo("#miscFormErrors");
    }
  }
});

function removeError() {
  $("#entitlement\\.includedUnits").removeClass("error");
}

/*
*  Copyright © 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/
/* this file is not being used anymore, will remove soon */

$(document).ready(function() {

  if ($("#productBundleEditForm").html() != null) {
    alert("setting up date for edit.");
    $(function() {
      $('#productBundleEditForm #productBundle\\.startDate').datepicker({
        duration: '',
        showTime: false
      });
    });

    $(function() {
      $('#productBundleEditForm #productBundle\\.endDate').datepicker({
        duration: '',
        showTime: false
      });
    });
  }
});

/**
 * Creates new productBundle row
 */
$.createNewProductBundle = function(jsonResponse) {

  if (jsonResponse == null) {
    popUpDialogForAlerts("dialog_info", i18n.errors.bundleBundleCreationFailed);
  } else {
    $("#addnewproductBundleDiv").html("");
    var rowClass = "db_gridbox_rows even";
    var count = $(".countDiv").attr("id");
    var size = Number(count.substr(5));
    var selected = "";
    if (size == 0) {
      selected = "selected";
    }
    if (size % 2 == 0) {
      rowClass = "db_gridbox_rows odd " + selected;
    } else {
      rowClass = "db_gridbox_rows even " + selected;
    }
    var content = "";
    content = content + "<div class='" + rowClass + "' onclick='viewProductBundle(this)' id='row" + jsonResponse.id +
      "'>";
    content = content + "<div class='db_gridbox_columns' style='width:10%;'>";
    content = content + "<div class='db_gridbox_celltitles'>";
    content = content + jsonResponse.id;
    content = content + "</div>";
    content = content + "</div>";
    content = content + "<div class='db_gridbox_columns' style='width:30%;'>";
    content = content + "<div class='db_gridbox_celltitles'>";
    content = content + jsonResponse.name;
    content = content + "</div>";
    content = content + "</div>";
    content = content + "<div class='db_gridbox_columns' style='width:19%;'>";
    content = content + "<div class='db_gridbox_celltitles'>";
    content = content + jsonResponse.cloudStackServiceOfferingId;
    content = content + "</div>";
    content = content + "</div>";
    content = content + "<div class='db_gridbox_columns' style='width:40%;'>";
    content = content + "<div class='db_gridbox_celltitles'>";
    content = content + jsonResponse.description;
    content = content + "</div>";
    content = content + "</div>";
    content = content + "</div>";
    var oldContent = $("#productBundlegridcontent").html();
    oldContent = oldContent + content;
    $("#productBundlegridcontent").html(oldContent);
    if (size == 0) {
      viewProductBundle($("#row" + jsonResponse.id));
    }
    size = size + 1;
    $(".countDiv").attr("id", "count" + size);


  }
};

/**
 * View Product details
 */
$.viewProductBundleDetails = function(jsonResponse) {
  var content = "";
  content = content + "<div class='main_detailsbox' ><div class='main_details_titlebox'>";
  content = content + "<h2>" + i18n.label.productbundle.Details + "</h2></div>";
  content = content + "<div class='main_details_contentbox'><div class='main_detailsistbox'>";

  content = content + "<div class='db_gridbox_rows detailsodd'><div class='db_gridbox_columns' style='width:20%;'>";
  content = content + "<div class='db_gridbox_celltitles details'><strong>" + i18n.label.productbundle.Name +
    "</strong></div></div>";
  content = content + "<div class='db_gridbox_columns' style='width:75%;'><div class='db_gridbox_celltitles details'>";
  content = content + jsonResponse.name + "</div></div></div>";

  content = content + "<div class='db_gridbox_rows detailsodd'><div class='db_gridbox_columns' style='width:20%;'>";
  content = content + "<div class=db_gridbox_celltitles details><strong>" + i18n.label.productbundle.servicing_offering_id +
    "</strong></div></div>";
  content = content + " <div class='db_gridbox_columns' style='width:75%;'><div class='db_gridbox_celltitles details'>";
  content = content + jsonResponse.cloudStackServiceOfferingId + "</div></div></div>";

  content = content + "<div class='db_gridbox_rows detailsodd'><div class='db_gridbox_columns' style='width:20%;'>";
  content = content + "<div class='db_gridbox_celltitles details'><strong>" + i18n.label.productbundle.description +
    "</strong></div></div>";
  content = content + " <div class='db_gridbox_columns' style='width:75%;'><div class='db_gridbox_celltitles details'>";
  content = content + jsonResponse.description + "</div></div></div></div></div>";


  content = content +
    "<div class='maindetails_footerlinksbox'><p><a href='javascript:void(0);' onclick='editProductBundleGet(this);' class='editProductBundle' id='edit" +
    jsonResponse.id + "'>" + i18n.label.productbundle.Edit + "</a> | </p>";
  content = content +
    "<p><a href='javascript:void(0);' class='removeProductBundle' onclick='removeProductBundle(this)'  id='remove" +
    jsonResponse.id + "'>" + i18n.label.productbundle.Remove + "</a></p>";
  content = content + "</div></div>";
  $("#editproductBundleDiv").html("");
  $("#viewproductBundleDiv").html(content);
};

if (JS_LOADED != true) {
  JS_LOADED = true;
  alert("setting up validation.");
  /**
   * Validate productBundle form
   */

  jQuery.validator.setDefaults({
    onfocusout: false,
    onkeyup: false,
    onclick: false,
    errorPlacement: function(error, element) {
      var name = element.attr('id');
      //console.log("for::::::::::::" , name);
      error.preppendTo($("#bundlecomponent0"));
    }
  });

  // a custom method for validating the date range
  $.validator
    .addMethod(
      "dateRange",
      function() {
        return new Date(
          $(
            "#productBundle\\.startDate")
          .val()) < new Date(
          $(
            "#productBundle\\.endDate")
          .val());
      },
      i18n.errors.bundleEnterValidDateRange);

  $.validator.addMethod("numberRequired",
    $.validator.methods.number,
    "Please enter a valid number.");
  $.validator.addClassRules("priceRequired", {
    twoDecimal: true,
    numberRequired: true
  });

  $.validator
    .addMethod(
      "twoDecimal",
      function(value, element) {
        $(element).rules("add", {
          number: true,
          digits: true
        });

        product = $(element).parent()
          .prevAll()
          .find(".productSelect");
        //console.log(product);
        //console.log(value);
        if (product.val() != "-1" && (value == "" || /^(?:\d*\.\d{1,2}|\d+)$/
          .test(value) == false)) {
          popUpDialogForAlerts("dialog_info", i18n.errors.max_twodecimal_price);
          return false;
        }
        return true;
      },
      i18n.errors.max_twodecimal_price);
} else {
  //console.log("mmmmmmmmmmm")
};

validateParams = {
  success: "valid",
  ignoreTitle: true,
  rules: {
    "productBundle.name": {
      required: true
    },
    "productBundle.startDate": {
      required: true
    },
    "productBundle.endDate": {
      required: true,
      dateRange: true
    },
    "productBundle.cloudStackServiceOfferingId": {
      number: true
    }
  },
  messages: {
    "productBundle.name": {
      required: i18n.errors.bundleBundleFormProvideName
    },
    "productBundle.startDate": {
      required: i18n.errors.bundleRateCardStartDateRequired
    },
    "productBundle.endDate": {
      required: i18n.errors.bundleRateCardEndDateRequired
    }
  },
  errorPlacement: function(error, element) {
    var name = element.attr('id');
    name = name.replace(".", "\\.");
    error.appendTo("#" + name + "Error");
  }
};


$("#addnewproductBundlecancel").click(function() {
  $("#addnewproductBundleDiv").html("");
});



/**
 * edit page Cancel action
 */
$("#editproductBundlecancel").click(function() {
  $("#editproductBundleDiv").html("");
});


/**
 * Update productBundle row
 */
$.editProductBundle = function(jsonResponse) {

  if (jsonResponse == null) {
    popUpDialogForAlerts("dialog_info", i18n.errors.bundleBundleEditFailed);
  } else {
    $("#editproductBundleDiv").html("");
    var content = "";
    content = content + "<div class='db_gridbox_columns' style='width:10%;'>";
    content = content + "<div class='db_gridbox_celltitles'>";
    content = content + jsonResponse.id;
    content = content + "</div>";
    content = content + "</div>";
    content = content + "<div class='db_gridbox_columns' style='width:30%;'>";
    content = content + "<div class='db_gridbox_celltitles'>";
    content = content + jsonResponse.name;
    content = content + "</div>";
    content = content + "</div>";
    content = content + "<div class='db_gridbox_columns' style='width:19%;'>";
    content = content + "<div class='db_gridbox_celltitles'>";
    content = content + jsonResponse.cloudStackServiceOfferingId;
    content = content + "</div>";
    content = content + "</div>";
    content = content + "<div class='db_gridbox_columns' style='width:40%;'>";
    content = content + "<div class='db_gridbox_celltitles'>";
    content = content + jsonResponse.description;
    content = content + "</div>";
    content = content + "</div>";
    $("#row" + jsonResponse.id).html(content);
    viewProductBundle($("#row" + jsonResponse.id));
  }
};

/**
 * View productBundle details
 * @param current
 * @return
 */

function viewProductBundle(current) {
  var divId = $(current).attr('id');
  var ID = divId.substr(3);
  resetGridRowStyle();
  var cls = $(current).attr('class');
  cls = cls + " selected"
  $(current).attr('class', cls);
  var url = productBundlesUrl + "view";
  $.ajax({
    type: "GET",
    url: url,
    data: {
      Id: ID
    },
    dataType: "html",
    success: function(html) {
      $("#editproductBundleDiv").html("");
      $("#viewproductBundleDiv").html(html);
    },
    error: function() {
      //need to handle TO-DO
    }
  });
}

function addNewProductBundleGet() {
  var actionurl = productBundlesUrl + "create";
  $.ajax({
    type: "GET",
    url: actionurl,
    dataType: "html",
    success: function(html) {
      $("#addnewproductBundleDiv").html(html);


    },
    error: function() {
      $(".addnewproductBundle").unbind('click');
    }
  });
}

/**
 * Add new productBundle(POST)
 * @param event
 * @param form
 * @return
 */

function addNewProductBundle(event, form) {
  if (event.preventDefault) {
    event.preventDefault();
  } else {
    event.returnValue = false;
  }
  if ($("#productBundleCreateForm").valid()) {
    $("#addbundle").attr("value", "Adding Product Bundle..");
    $.ajax({
      type: "POST",
      url: $(form).attr('action'),
      data: $(form).serialize(),
      dataType: "json",
      success: function(jsonResponse) {
        $.createNewProductBundle(jsonResponse);
      },
      error: function(XMLHttpRequest) {
        if (XMLHttpRequest.status === CODE_NOT_UNIQUE_ERROR_CODE) {
          popUpDialogForAlerts("dialog_info", i18n.errors.common.codeNotUnique);
        } else {
          popUpDialogForAlerts("dialog_info", i18n.errors.bundleBundleCreationFailed);
        }
      }
    });

  }
}
/**
 * Edit productBundle (GET)
 */

function editProductBundleGet(current) {
  var divId = $(current).attr('id');
  var ID = divId.substr(4);
  var actionurl = productBundlesUrl + "edit";
  $.ajax({
    type: "GET",
    url: actionurl,
    data: {
      Id: ID
    },
    dataType: "html",
    success: function(html) {
      $("#viewproductBundleDiv").html("");
      $("#editproductBundleDiv").html("");
      $("#editproductBundleDiv").html(html);
    },
    error: function() {
      $(".editProductBundle").unbind('click');
    }
  });
}

/**
 * Edit productBundle POST
 * @param event
 * @param form
 * @return
 */

function editProductBundle(event, form) {
  if (event.preventDefault) {
    event.preventDefault();
  } else {
    event.returnValue = false;
  }
  if ($("#productBundleEditForm").valid()) {
    $("#editbundle").attr("value", "Saving Product Bundle..");
    $.ajax({
      type: "POST",
      url: $(form).attr('action'),
      data: $(form).serialize(),
      dataType: "json",
      success: function(jsonResponse) {
        $.editProductBundle(jsonResponse);
      }

    });

  }
}


/**
 * Remove productBundle (GET)
 */

function removeProductBundle(current) {
  var r = confirm(i18n.errors.bundleDeletebundleConfirmation);
  if (r == false) {
    return false;
  }
  var divId = $(current).attr('id');
  var ID = divId.substr(6);
  var actionurl = productBundlesUrl + "remove";
  $.ajax({
    type: "GET",
    url: actionurl,
    data: {
      Id: ID
    },
    dataType: "text/hmtl",
    success: function(html) {
      if (html == "success") {
        $("#viewproductBundleDiv").html("");
        $("#editproductBundleDiv").html("");
        $("#row" + ID).remove();
        var count = $(".countDiv").attr("id");
        var size = Number(count.substr(5));
        size = size - 1;
        $(".countDiv").attr("id", "count" + size);
        resetGridRowStyle();
      } else {
        popUpDialogForAlerts("dialog_info", i18n.errors.bundleDeletebundleFailure);
      }

    },
    error: function() {
      //need to handle
    }
  });
}
/**
 * Reset data row style
 * @return
 */

function resetGridRowStyle() {
  var isEven = true;
  $(".db_gridbox_rows").each(function() {
    var calssvar = $(this).attr('class');
    if (calssvar.search("header") == -1) {
      if (isEven == true) {
        $(this).attr('class', 'db_gridbox_rows even');
        isEven = false;
      } else {
        $(this).attr('class', 'db_gridbox_rows odd');
        isEven = true;
      }
    }

  });
}

function createBundleComponent() {
  var productId = $("#productId").val();
  var chargeType = $("#chargeType").val();
  var unitOfMeasure = $("#unitOfMeasure").val();
  var includedUnits = $("#includedUnits").val();
  var taxable = $("#taxable").attr("checked");
  var price = $("#price").val();
  var currentrow = $(".currentrow").attr('id');
  currentrow = Number(currentrow.substr(3));
  var url = "/portal/portal/productBundles/createcomponent";

  $.ajax({
    type: "GET",
    url: url,
    data: {
      productId: productId,
      chargeType: chargeType,
      unitOfMeasure: unitOfMeasure,
      includedUnits: includedUnits,
      taxable: taxable,
      price: price,
      currentrow: currentrow
    },
    dataType: "html",
    success: function(html) {
      var old = $("#bundlecomponentDiv").html();
      old = old + html;
      $("#bundlecomponentDiv").html(old);

      $("#productId").val('');
      $("#chargeType").val('');
      $("#unitOfMeasure").val('');
      $("#includedUnits").val('');
      $("#taxable").attr("checked", "");
      $("#price").val('');
      currentrow = currentrow + 1;
      $(".currentrow").attr('id', "row" + currentrow);
    },
    error: function() {
      $("#add_bundle_component").unbind('click');
    }
  });

}


function deleteBundleComponent(current) {
  var divId = $(current).attr('id');
  var ID = divId.substr(6);
  //bundlecomponent1
  var currentrow = $(".currentrow").attr('id');
  currentrow = Number(currentrow.substr(3));
  var url = "/portal/portal/productBundles/deletebundle";

  $.ajax({
    type: "GET",
    url: url,
    data: {
      currentrow: ID
    },
    dataType: "html",
    success: function(html) {
      if (html == "success") {
        $("#bundlecomponent" + ID).remove();
      }
    },
    error: function() {}
  });



}

var JS_LOADED = true;

/*
*  Copyright � 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/
$(document).ready(function() {

  jQuery.validator.addMethod('allowminusone',
    function(value) {
      if (Number(value) || Number(value) == 0) {
        if (value % 1 == 0) {
          if (value >= -1) {
            return true;
          }
        }
      }
      return false;
    });


  jQuery.validator.addMethod('allownumber',
    function(value) {
      if (Number(value) || Number(value) == 0) {
        return true;
      }
      return false;
    }, accountTypeDictionary.INVALIDVALUELCEERROR);


  jQuery.validator.addMethod('allowPositiveNumberforcreditexposure',
    function(value) {
      return Number(value) >= 0 && value.length < 12;
    }, accountTypeDictionary.INVALIDVALUELCEERROR);

  jQuery.validator.addMethod('allowPositiveNumberforinitialdeposit',
    function(value) {
      return Number(value) >= 0 && value.length < 12;
    }, accountTypeDictionary.FAILEDTOEDITINITIALDEPOSITAMOUNT);

  jQuery.validator.addMethod("userLimit", function(value, element) {
    return this.optional(element) || /^[\\+-]*[0-9]*$/i.test(value);
  }, accountTypeDictionary.INVALIDVALUEUSERERROR);
  
  $.validator.addMethod("maxcurrencyPrecision", function (value, element) {
	    $(element).rules("add", {
	        number: true
	    });
	    if (!/^(?:\d*\.\d*\|\d+)$/.test(value)) {
	        var sval = value;
	        var sdecimalDigit = sval.split(".");
	        var returnVal = true;
	        if (sdecimalDigit.length == 2) { // have decimal
	            if (sdecimalDigit[1].length > currPrecision) {
	                // more characters allowed precision
	            	returnVal = false;
	            } 
	        } else if (sdecimalDigit.length > 2) {
	            //more then one decimals   
	        	returnVal = false;
	        }
	        // without decimal
	        if(returnVal && sdecimalDigit[0] != null && (Number(sdecimalDigit[0]) < 0 || sdecimalDigit[0].length > 8)){
	        	returnVal = false;
	        }
	        
	        return returnVal;
	        
	    } else { //not a valid digit pattern
	        return false;
	    }
	}, accountTypeDictionary.MAXPRECISION);

  $.validator.addClassRules("creditexposureclass", {
	 required: true,
	 number: true,
	 maxcurrencyPrecision: true
  });

  $.validator.addClassRules("initialdepositclass", {
     required: true,
     number: true,
     maxcurrencyPrecision: true
  });

  var accountTypeEditFormValidator = $("#accountTypeEditForm").validate({
    success: function(label) {
      label.remove();
    },
    ignoreTitle: true,
    rules: {
      "accountType.maxUsers": {
        userLimit: true,
        required: true,
        allowminusone: true,
        max: 99999999

      },
      "accountType.perAccountVmCount": {
        allowminusone: true
      },
      "accountType.perAccountPublicIPCount": {
        allowminusone: true
      },
      "accountType.perAccountVolumeCount": {
        allowminusone: true
      },
      "accountType.perAccountSnapshotCount": {
        allowminusone: true
      },
      "accountType.perAccountTemplateCount": {
        allowminusone: true
      },
      "accountType.perUserVmCount": {
        allowminusone: true
      },
      "accountType.perUserPublicIPCount": {
        allowminusone: true
      },
      "accountType.perUserVolumeCount": {
        allowminusone: true
      },
      "accountType.perUserSnapshotCount": {
        allowminusone: true
      },
      "accountType.perUserTemplateCount": {
        allowminusone: true
      },
      "accountType.accountRestrictionGracePeriod": {
        required: true,
        allowminusone: true
      },
      "accountType.initialDepositAmount": {
        allowPositiveNumberforinitialdeposit: true
      }
    },
    messages: {
      "accountType.maxUsers": {
        required: accountTypeDictionary.REQUIEREDMAXUSERERROR,
        allowminusone: accountTypeDictionary.INVALIDVALUEUSERERROR,
        userLimit: accountTypeDictionary.INVALIDVALUEUSERERROR,
        max: accountTypeDictionary.INVALIDVALUEUSERERROR
      },
      "accountType.perAccountVmCount": {
        allowminusone: accountTypeDictionary.INVALIDVALUEERROR
      },
      "accountType.perAccountPublicIPCount": {
        allowminusone: accountTypeDictionary.INVALIDVALUEERROR
      },
      "accountType.perAccountVolumeCount": {
        allowminusone: accountTypeDictionary.INVALIDVALUEERROR
      },
      "accountType.perAccountSnapshotCount": {
        allowminusone: accountTypeDictionary.INVALIDVALUEERROR
      },
      "accountType.perAccountTemplateCount": {
        allowminusone: accountTypeDictionary.INVALIDVALUEERROR
      },
      "accountType.perUserVmCount": {
        allowminusone: accountTypeDictionary.INVALIDVALUEERROR
      },
      "accountType.perUserPublicIPCount": {
        allowminusone: accountTypeDictionary.INVALIDVALUEERROR
      },
      "accountType.perUserVolumeCount": {
        allowminusone: accountTypeDictionary.INVALIDVALUEERROR
      },
      "accountType.perUserSnapshotCount": {
        allowminusone: accountTypeDictionary.INVALIDVALUEERROR
      },
      "accountType.perUserTemplateCount": {
        allowminusone: accountTypeDictionary.INVALIDVALUEERROR
      },
      "accountType.accountRestrictionGracePeriod": {
        required: accountTypeDictionary.REQUIEREDGRACEPERIODERROR,
        allowminusone: accountTypeDictionary.INVALIDVALUEERROR
      },
      "accountType.initialDepositAmount": {
        allowPositiveNumberforinitialdeposit: accountTypeDictionary.FAILEDTOEDITINITIALDEPOSITAMOUNT
      }
    },
    errorPlacement: function(error, element) {
      var name = element.attr('id');
      name = ReplaceAll(name, ".", "\\.");
      if (error.html() != "" || error.html() != null) {
        $('.error').empty();
        error.appendTo("#" + name + "Error");
      }
    }
  });

});

var accountTypeInitialDepositFormValidator = $("#accountTypeInitialDepositForm").validate({
  errorPlacement: function(error, element) {
    var name = element.attr('id');
    name = ReplaceAll(name, ".", "\\.");
    if (error.html() != "") {
      error.appendTo("#" + name + "Error");
    }
  }
});

/**
 * Edit tenant POST
 * @param event
 * @param form
 * @return
 */

function editAccountType(event, form) {
  if (event.preventDefault) {
    event.preventDefault();
  } else {
    event.returnValue = false;
  }
  if ($("#accountTypeEditForm").valid()) {
    $("#editAccountTypeSave").attr("value", accountTypeDictionary.SAVING);
    $.ajax({
      type: "POST",
      url: $(form).attr('action'),
      data: $(form).serialize(),
      dataType: "html",
      success: function(html) {
        $("#editAccountTypeDiv").html("");
        viewaccounttype($("div[id^='row'].selected"));
      },
      error: function() {
        $("#editAccountTypeSave").attr("value", accountTypeDictionary.SAVE);
        $("#miscFormErrors").text("");
      }
    });
  }
}

function setupAccountTypeTabs() {
  $("#details_tab").bind("click", function(event) {
    $('#details_tab').removeClass('nonactive').addClass("active");
    $('#onboarding_tab').removeClass('active').addClass("nonactive");
    $('#iaas_tab').removeClass('active').addClass("nonactive");
    $('#billing_tab').removeClass('active').addClass("nonactive");
    $('#creditexposure_tab').removeClass('active').addClass("nonactive");
    $('#initialdeposit_tab').removeClass('active').addClass("nonactive");
    $('#details_content').show();
    $('#onboarding_content').hide();
    $('#servicecontrols_content').hide();
    $('#billing_content').hide();
    $('#creditexposure_content').hide();
    $('#initialdeposit_content').hide();
  });
  $("#onboarding_tab").bind("click", function(event) {
    $('#details_tab').removeClass('active').addClass("nonactive");
    $('#onboarding_tab').removeClass('nonactive').addClass("active");
    $('#iaas_tab').removeClass('active').addClass("nonactive");
    $('#billing_tab').removeClass('active').addClass("nonactive");
    $('#creditexposure_tab').removeClass('active').addClass("nonactive");
    $('#initialdeposit_tab').removeClass('active').addClass("nonactive");
    $('#details_content').hide();
    $('#onboarding_content').show();
    $('#servicecontrols_content').hide();
    $('#billing_content').hide();
    $('#creditexposure_content').hide();
    $('#initialdeposit_content').hide();
  });
  $("#iaas_tab").bind("click", function(event) {
    $('#details_tab').removeClass('active').addClass("nonactive");
    $('#onboarding_tab').removeClass('active').addClass("nonactive");
    $('#iaas_tab').removeClass('nonactive').addClass("active");
    $('#billing_tab').removeClass('active').addClass("nonactive");
    $('#creditexposure_tab').removeClass('active').addClass("nonactive");
    $('#initialdeposit_tab').removeClass('active').addClass("nonactive");
    $('#details_content').hide();
    $('#onboarding_content').hide();
    $('#servicecontrols_content').show();
    $('#billing_content').hide();
    $('#creditexposure_content').hide();
    $('#initialdeposit_content').hide();
  });
  $("#billing_tab").bind("click", function(event) {
    $('#details_tab').removeClass('active').addClass("nonactive");
    $('#onboarding_tab').removeClass('active').addClass("nonactive");
    $('#iaas_tab').removeClass('active').addClass("nonactive");
    $('#billing_tab').removeClass('nonactive').addClass("active");
    $('#creditexposure_tab').removeClass('active').addClass("nonactive");
    $('#initialdeposit_tab').removeClass('active').addClass("nonactive");
    $('#details_content').hide();
    $('#onboarding_content').hide();
    $('#servicecontrols_content').hide();
    $('#billing_content').show();
    $('#creditexposure_content').hide();
    $('#initialdeposit_content').hide();
  });
  $("#creditexposure_tab").bind("click", function(event) {
    $('#details_tab').removeClass('active').addClass("nonactive");
    $('#onboarding_tab').removeClass('active').addClass("nonactive");
    $('#iaas_tab').removeClass('active').addClass("nonactive");
    $('#billing_tab').removeClass('active').addClass("nonactive");
    $('#creditexposure_tab').removeClass('nonactive').addClass("active");
    $('#initialdeposit_tab').removeClass('active').addClass("nonactive");
    $('#details_content').hide();
    $('#onboarding_content').hide();
    $('#servicecontrols_content').hide();
    $('#billing_content').hide();
    $('#creditexposure_content').show();
    $('#initialdeposit_content').hide();
  });
  $("#initialdeposit_tab").bind("click", function(event) {
    $('#details_tab').removeClass('active').addClass("nonactive");
    $('#onboarding_tab').removeClass('active').addClass("nonactive");
    $('#iaas_tab').removeClass('active').addClass("nonactive");
    $('#billing_tab').removeClass('active').addClass("nonactive");
    $('#creditexposure_tab').removeClass('active').addClass("nonactive");
    $('#initialdeposit_tab').removeClass('nonactive').addClass("active");
    $('#details_content').hide();
    $('#onboarding_content').hide();
    $('#servicecontrols_content').hide();
    $('#billing_content').hide();
    $('#creditexposure_content').hide();
    $('#initialdeposit_content').show();
  });

}


function viewaccounttype(current, tab) {
  $("#editAccountTypeDiv").html("");
  var divId = $(current).attr('id');
  var ID = divId.substr(3);
  resetGridRowStyle();
  $(current).addClass("selected active");
  var cls = $(current).attr('class');
  cls = cls + " selected";
  $(current).attr('class', cls);
  var url = "/portal/portal/admin/view_account_type";
  $.ajax({
    type: "GET",
    url: url,
    data: {
      id: ID,
      tab: tab
    },
    dataType: "html",
    async: false,
    success: function(html) {
      $("#viewaccounttypeDiv").html(html);
      setupAccountTypeTabs();
      changeInstances();
    },
    error: function() {
      //need to handle TO-DO
    }
  });
}

function changeInstances() {
  var serviceParam = $("#selectedService").val();
  var $instanceDropDown = $("#selectedInstance");
  $instanceDropDown.empty();
  $("#hiddeninstances option").each(function() {
    var value = $(this).val();
    var instancename = $(this).text();
    if (value.indexOf(serviceParam) == 0) {
      var index = value.indexOf(".");
      if (index > 0) {
        var instanceParam = value.substr(index + 1);
        $instanceDropDown.append($('<option></option>').val(instanceParam).html(instancename));
      }
    }
  });
  showControls();
}

function showControls() {
  //hide or show based on instance id
  var id = $("#selectedInstance").val();
  $('div[id^="instanceparam_"]').hide();
  $('div[id^=instanceparam_' + id + ']').show();
}

function addAccountTypeControlsGet(id, accounttype) {
  var actionurl = "/portal/portal/admin/add_account_type_controls";
  $.ajax({
    type: "GET",
    url: actionurl,
    data: {
      id: id,
      accounttype: accounttype
    },
    dataType: "html",
    success: function(html) {
      $("#editAccountTypeDivDetail").html("");
      $("#editAccountTypeDivOnBoard").html("");
      $("#editAccountTypeDivServiceControls").html("");
      $("#editAccountTypeDivBilling").html("");
      var $thisDialog = null;
      initDialog("editAccountTypeDivServiceControls", 700);
      $("#editAccountTypeDivServiceControls").html(html);
      $thisDialog = $("#editAccountTypeDivServiceControls");

      $thisDialog.dialog({
        height: 560,
        width: 700,
      });
      $thisDialog.dialog("open");
    },
    error: function() {

    }
  });
}

function closeAccountTypeControlsDialog() {
  $("#editAccountTypeDivServiceControls").dialog("close");
}


function saveAcccountControls() {
  $("#spinning_wheel").show();
  $form = $("#accountControlsForm");

  var uuid = $form.find("#instanceParam").val();
  var action = $form.find("#action").val();
  var accounttypeid = $form.find("#accounttypeid").val();

  var configProperties = new Array();
  $('input[id^="configproperty"]').each(function() {
    var configProperty = new Object();
    configProperty.name = $(this).attr("name");
    configProperty.value = $(this).attr("value");
    configProperties.push(configProperty);
  });
  $('input[id^="configbooleantrue"]:checked').each(function() {
    var configProperty = new Object();
    configProperty.name = $(this).attr("name");
    configProperty.value = "true";
    configProperties.push(configProperty);
  });

  $('input[id^="configbooleanfalse"]:checked').each(function() {
    var configProperty = new Object();
    configProperty.name = $(this).attr("name");
    configProperty.value = "false";
    configProperties.push(configProperty);
  });


  $.ajax({
    type: "POST",
    url: "/portal/portal/admin/save_account_type_controls",
    data: {
      "configproperties": JSON.stringify(configProperties),
      "id": uuid,
      "action": action,
      "accounttypeid": accounttypeid
    },
    dataType: "json",
    success: function(data) {
      $("#spinning_wheel").hide();

      if (data.validationResult == "SUCCESS" && data.result == "SUCCESS") {
        $("#submitbutton").removeClass("active").addClass("nonactive");
        var $limitdetails = $('div[id^=instanceparam_' + uuid + '].details');
        $('input[id^="configproperty"]').each(function() {
          var name = $(this).attr("name");
          var value = $(this).attr("value");
          $limitdetails.find('#' + name).text(value);
        });
        $('input[id^="configbooleantrue"]:checked').each(function() {
          var name = $(this).attr("name");
          $limitdetails.find('#' + name).text("true");
        });
        $('input[id^="configbooleanfalse"]:checked').each(function() {
          var name = $(this).attr("name");
          $limitdetails.find('#' + name).text("false");
        });
        closeAccountTypeControlsDialog();
      } else {
        var $resultDisplayBanner = $("#resultstring");
        $resultDisplayBanner.html(data.message);
        $resultDisplayBanner.parents("div.service_detail_subsection").show();
        $("#spinning_wheel").hide();
      }
    },
    error: function(data) {
      $("#spinning_wheel").hide();
    }
  });
}


/**
 * Edit tenant (GET)
 */

function editAccountTypeGet(current, mode) {
  var divId = $(current).attr('id');
  var ID = divId.substr(4);
  var actionurl = "/portal/portal/admin/edit_account_type";
  $.ajax({
    type: "GET",
    url: actionurl,
    data: {
      id: ID,
      mode: mode
    },
    dataType: "html",
    success: function(html) {
      $("#editAccountTypeDivDetail").html("");
      $("#editAccountTypeDivOnBoard").html("");
      $("#editAccountTypeDivServiceControls").html("");
      $("#editAccountTypeDivBilling").html("");
      var $thisDialog = null;
      var title = "";
      if (mode == 1) {
        initDialog("editAccountTypeDivDetail", 700);
        $("#editAccountTypeDivDetail").html(html);
        $thisDialog = $("#editAccountTypeDivDetail");
        title = $("#editAccountTypeDivDetail").attr("title") + " " + $("#accounttype_name" + ID).val();
        height = 70;
      } else if (mode == 2) {
        initDialog("editAccountTypeDivOnBoard", 700);
        $("#editAccountTypeDivOnBoard").html(html);
        $thisDialog = $("#editAccountTypeDivOnBoard");
        title = $("#editAccountTypeDivOnBoard").attr("title") + " " + $("#accounttype_name" + ID).val();
        height = 350;
      } else if (mode == 3) {
        initDialog("editAccountTypeDivServiceControls", 700);
        $("#editAccountTypeDivServiceControls").html(html);
        $thisDialog = $("#editAccountTypeDivServiceControls");
        title = $("#editAccountTypeDivServiceControls").attr("title") + " " + $("#accounttype_name" + ID).val();
        height = 560;
      } else if (mode == 4) {
        initDialog("editAccountTypeDivBilling", 700);

        $("#editAccountTypeDivBilling").html(html);
        $thisDialog = $("#editAccountTypeDivBilling");
        title = $("#editAccountTypeDivBilling").attr("title") + " " + $("#accounttype_name" + ID).val();
        height = 200;
      }
      $thisDialog.dialog({
        height: height,
        width: 700,
        title: title
      });
      $thisDialog.dialog('option', 'buttons', {
        "OK": function() {
          if ($("#accountTypeEditForm").valid()) {
            $.ajax({
              type: "POST",
              url: $("#accountTypeEditForm").attr('action'),
              data: $("#accountTypeEditForm").serialize(),
              dataType: "json",
              success: function(json) {
                $("#accounnttype_desc" + ID).text(makeDesc(json.selfRegistrationAllowed, json.maxUsers));
                viewaccounttype($('#row' + ID), mode);
                $thisDialog.dialog("close");
                $thisDialog.dialog("destroy");
                //TODO:Click particular tab
              },
              error: function(XMLHttpRequest) {
                $('.edit_error_msg').append('<label class="error">' + XMLHttpRequest.responseText + '</label>');
              }
            });
          }
        },
        "Cancel": function() {
          $(this).dialog("close");
          $(this).dialog("destroy");
        }
      });
      dialogButtonsLocalizer($thisDialog, {
        'OK': g_dictionary.dialogOK,
        'Cancel': g_dictionary.dialogCancel
      });
      $thisDialog.dialog("open");
    },
    error: function() {
      $(".editTenant").unbind('click');
    }
  });
}

$('#editaccounttypecancel').live('click', function() {
  if ($("#editAccountTypeDiv").length) {
    viewaccounttype($("div[id^='row'].selected"));
  }
});
$('#editcreditexposurecancel').live('click', function() {
  $("div[id^='row'].selected").click();
  $("#creditexposuretab").click();
});
$('#editinitialdepositcancel').live('click', function() {
  $("div[id^='row'].selected").click();
  $("#initialdeposittab").click();
});


/**
 * Reset data row style
 * @return
 */

function resetGridRowStyle() {
  $(".widget_navigationlist").each(function() {
    $(this).removeClass("selected active");
  });
}

/**
 * Edit initial deposit get.
 */

function editinitialdepositGet(current) {
  var divId = $(current).attr('id');
  var ID = divId.substr(4);
  var actionurl = "/portal/portal/admin/edit_initial_deposit";
  $.ajax({
    type: "GET",
    url: actionurl,
    data: {
      id: ID
    },
    dataType: "html",
    success: function(html) {
      $("#initialDepositEditDiv").html("");
      $("#initialDepositEditDiv").html(html);
      var $thisDialog = $("#initialDepositEditDiv");
      $thisDialog.dialog({
        height: 320,
        width: 700,
        title: $("#initialDepositEditDiv").attr("title") + " " + $("#accounttype_name" + ID).val()
      });
      $thisDialog.dialog('option', 'buttons', {
        "OK": function() {
          if ($("#accountTypeInitialDepositForm").valid()) {
            $.ajax({
              type: "POST",
              url: $("#accountTypeInitialDepositForm").attr('action'),
              data: $("#accountTypeInitialDepositForm").serialize(),
              dataType: "html",
              success: function(html) {
                if (html == "success") {
                  viewaccounttype($('#row' + ID), 6);
                  $thisDialog.dialog("close");
                  $thisDialog.dialog("destroy");
                }
              },
              error: function(jsonResponse) {

              }
            });
          }


        },
        "Cancel": function() {
          $(this).dialog("close");
          $(this).dialog("destroy");
        }
      });
      dialogButtonsLocalizer($thisDialog, {
        'OK': g_dictionary.dialogOK,
        'Cancel': g_dictionary.dialogCancel
      });
      $thisDialog.dialog("open");
    },
    error: function() {

    }
  });
}

/**
 * Edit initial deposit  POST
 * @param event
 * @param form
 * @return
 */

function editinitialdepositPost(event, form) {
  if (event.preventDefault) {
    event.preventDefault();
  } else {
    event.returnValue = false;
  }
  if ($("#accountTypeEditForm").valid()) {
    $("#editInitialDepositSave").attr("value", accountTypeDictionary.SAVING);
    $.ajax({
      type: "POST",
      url: $(form).attr('action'),
      data: $(form).serialize(),
      dataType: "html",
      success: function(html) {
        if (html == "success") {
          //$("div[id^='row'].selected").click();
          //$("#initialdeposittab").click();
        }
      },
      error: function(jsonResponse) {
        $("#editInitialDepositSave").attr("value", accountTypeDictionary.SAVE);
        $('#miscFormErrors').html(accountTypeDictionary.FAILEDTOEDITINITIALDEPOSITAMOUNT);
      }
    });
  }
}

/**
 * cancel
 * @return
 */

function viewinitialdeposit() {
  $("#initialdeposittab").click();
}

/**
 * Edit credit exposure get.
 */

function editcreditlimitexposureGet(current) {
  var divId = $(current).attr('id');
  var ID = divId.substr(4);
  initDialog("creditExposureLimitsDiv", 800);
  var actionurl = "/portal/portal/admin/edit_credit_exposure";
  $.ajax({
    type: "GET",
    url: actionurl,
    data: {
      id: ID
    },
    dataType: "html",
    success: function(html) {
      $("#creditExposureLimitsDiv").html("");
      $("#creditExposureLimitsDiv").html(html);
      var $thisDialog = $("#creditExposureLimitsDiv");
      $thisDialog.dialog({
        height: 320,
        width: 800,
        title: $("#creditExposureLimitsDiv").attr("title") + " " + $("#accounttype_name" + ID).val()
      });
      $thisDialog.dialog('option', 'buttons', {
        "OK": function() {
          if ($("#accountTypeCreditExposureEditForm").valid()) {
            $.ajax({
              type: "POST",
              url: $("#accountTypeCreditExposureEditForm").attr('action'),
              data: $("#accountTypeCreditExposureEditForm").serialize(),
              dataType: "html",
              success: function(html) {
                if (html == "success") {
                  viewaccounttype($('#row' + ID), 5);
                  $thisDialog.dialog("close");
                  $thisDialog.dialog("destroy");
                }
              },
              error: function(jsonResponse) {
                //TODO:		 		
              }
            });
          }


        },
        "Cancel": function() {
          $(this).dialog("close");
          $(this).dialog("destroy");
        }
      });
      dialogButtonsLocalizer($thisDialog, {
        'OK': g_dictionary.dialogOK,
        'Cancel': g_dictionary.dialogCancel
      });
      $thisDialog.dialog("open");



    },
    error: function() {

    }
  });
}

/**
 * Edit credit exposure  POST
 * @param event
 * @param form
 * @return
 */

function editcreditlimitexposure(event, form) {
  if (event.preventDefault) {
    event.preventDefault();
  } else {
    event.returnValue = false;
  }
  if ($("#accountTypeEditForm").valid()) {
    $("#editCreditExposureSave").attr("value", accountTypeDictionary.SAVING);
    $.ajax({
      type: "POST",
      url: $(form).attr('action'),
      data: $(form).serialize(),
      dataType: "html",
      success: function(html) {
        if (html == "success") {
          $("div[id^='row'].selected").click();
          $("#creditexposuretab").click();
        }
      },
      error: function(jsonResponse) {
        $("#editCreditExposureSave").attr("value", accountTypeDictionary.SAVE);
        $('#miscFormErrors').html(accountTypeDictionary.FAILEDTOEDITCREDITEXPOSURE);
      }
    });
  }
}

function onAccountTypeMouseover(current) {
  if ($(current).hasClass('active')) return
  $(current).find("#info_bubble").show();
  return false;
}

function onAccountTypeMouseout(current) {
  $(current).find("#info_bubble").hide();
  return false;
}

/*
*  Copyright � 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/
$(document).ready(function() {
  var trialAccount = false;
  var trial_selected = false;
  $("#spinning_wheel_rhs").hide();
  initDialog("editTenantDiv", 500);
  initDialog("issueCreditDiv", 700);
  initDialog("editTenantsLimitDiv", 650);
  initDialog("newTenantDiv", 900);

  initDialog("dialog_confirmation", 350, false);

  $("#trialCodeBox").hide();
  /**
   * add account wizard last step link.
   */
  $("#viewproductdetails_configure").live("click", function(event) {
    $currentDialog.dialog("close");
    $("#newTenantDiv").find(".dialog_formcontent").empty();
  });

  function getTopActionMap() {
    var topActionMap = {

      removetenant: {
        label: dictionary.removeTenant,
        inProcessText: dictionary.removingTenant,
        type: "POST",
        dataType: "html",
        afterActionSeccessFn: function(html) {
          var filterBy = "";
          if($("#l3_account_All_tab").hasClass('on')){
            filterBy = "&filterBy=All";
          }else{
            filterBy = "&filterBy=4";
          }
          window.location = tenantPageUrl + "?accountType=" + selectedAccountType + "&currentPage=" + currentPage + filterBy;
        },
        error: function() {
          popUpDialogForAlerts("dialog_info", i18n.errors.tenants.removeTenant.error);
        }
      }
    };
    return topActionMap;
  }

  function getConfirmationDialogButtons(command) {

    var buttonCallBacks = {};
    var actionMapItem;
    if (command == "removetenant") {
      actionMapItem = getTopActionMap().removetenant;
    }

    buttonCallBacks[dictionary.lightboxbuttonconfirm] = function() {
      $(this).dialog("close");

      var tenant_id = $('#tenant_param').val();

      var apiCommand;
      if (command == "removetenant") {

        apiCommand = "/portal/portal/tenants/" + tenant_id + "/delete";

      }
      doActionButton(actionMapItem, apiCommand);

    };

    buttonCallBacks[dictionary.lightboxbuttoncancel] = function() {
      $(this).dialog("close");
    };

    return buttonCallBacks;
  }



  $(".remove_tenant_link").live("click", function(event) {
    $("#dialog_confirmation").text(dictionary.lightboxremovetenant).dialog('option', 'buttons',
      getConfirmationDialogButtons("removetenant")).dialog("open");
  });

  if ($("#displayCreditCardTabVar").val() == "true") {
    $(".secondlevel_menutabs").removeClass("on");
    $("#billingDetails").addClass("on");
    $("#generalInfoDiv").hide();
    $("#billingdiv").hide();
    $("#changeAccountTypeJsp").hide();
    $("#creditCardDetailsDiv").show();
  }


  $("#changeAccountTypeCancelLink").live("click", function(event) {
    $("#changeAccountTypeJsp").find("#creditcard_update_error").hide();
    $("#changeAccountTypeJsp").find("#creditcard_update_error").find("#p_message").html("");
    $("#changeAccountTypeJsp").hide();
    $("#generalInfoDiv").show();

  });

  $(".secondlevel_menutabs").live("click", function(event) {

    if ($(this).attr('id') != "apiCredentials") {
      $(".secondlevel_menutabs").removeClass("on");
      $(this).addClass("on");
    }
    if ($(this).attr('id') == "billingDetails") {
      $("#generalInfoDiv").hide();
      $("#billingdiv").hide();
      $("#changeAccountTypeJsp").hide();
      $("#previous_tab").val("creditCardDetailsDiv");
      $("#historyDiv").hide();
      $("#creditCardDetailsDiv").show();
    }
    if ($(this).attr('id') == "contact") {
      $("#generalInfoDiv").hide();
      $("#creditCardDetailsDiv").hide();
      $("#changeAccountTypeJsp").hide();
      $("#previous_tab").val("billingdiv");
      $("#historyDiv").hide();
      $("#billingdiv").show();
    }
    if ($(this).attr('id') == "history") {
      $("#generalInfoDiv").hide();
      $("#creditCardDetailsDiv").hide();
      $("#changeAccountTypeJsp").hide();
      $("#previous_tab").val("historyDiv");
      $("#billingdiv").hide();
      $("#historyDiv").show();
    }
    if ($(this).attr('id') == "apiCredentials") {
      var previousTab = $("#previous_tab").val();
      if (previousTab != "apicredentials") {
        if ($("#doNotShowVerifyUserDiv").val() == "true") {
          var password = "dummy";
          $.ajax({
            type: 'POST',
            url: "/portal/portal/tenants/get_api_details",
            data: {
              'password': password
            },
            dataType: "json",
            success: function(data) {
              if (data.success == true) {
                $("#generalInfoDiv").hide();
                $("#creditCardDetailsDiv").hide();
                $("#changeAccountTypeJsp").hide();
                $("#billingdiv").hide();
                $("#historyDiv").hide();
                cleanCredentialsDiv();
                for (var i = 0; i < data.tenantCredentialList.length; i++) {
                  var instance = data.tenantCredentialList[i];
                  var apiCredentialsDivCloned = $("#apiCredentialsDiv").clone();
                  var tenantCredentialLiCloned = null;
                  $.each(instance, function(key, value) {
	            	if ((key == "ServiceName" || key == "ServiceUuid" || key == "InstanceName")) {
                      // Service Name, Uuid and instance need not be shown in list as they are shown in LHS
                      return;
	                }
                    tenantCredentialLiCloned = apiCredentialsDivCloned.find("#tenantCredentialLi").clone();
                    tenantCredentialLiCloned.find('#tenantCredentialLabel').text(key);
                    tenantCredentialLiCloned.find("#tenantCredentialLabel").attr("for",
                      'tenantCredentialLabel_' + key);
                    tenantCredentialLiCloned.find("#tenantCredentialLabel").attr("id", 'tenantCredentialLabel_' +
                      key);
                    tenantCredentialLiCloned.find('#liCredentialValue').text(value);
                    apiCredentialsDivCloned.find('#serviceLogo').html(
                      '<img class="apikeyLogo"  src=/portal/portal/logo/connector/' + instance.ServiceUuid +
                      '/logo>');
                    apiCredentialsDivCloned.find("#tenantCredentialUl").append(tenantCredentialLiCloned);
                    tenantCredentialLiCloned.attr('id', 'tenantCredentialLi_' + key);

                  });
                  //var cls=apiCredentialsDivCloned.find('#titleDiv').attr('class');
                  apiCredentialsDivCloned.find('#titleDiv').html('<h2>' + instance.ServiceName + '-' + instance.InstanceName +
                    '</h2>');
                  apiCredentialsDivCloned.find('#titleDiv').attr('style', 'margin-top:10px;');
                  apiCredentialsDivCloned.find('#userCredentialLi').hide();
                  apiCredentialsDivCloned.attr('id', 'apiCredentialsDiv_' + instance.ServiceUuid);
                  apiCredentialsDivCloned.show();
                  $("#userCredentialDiv").append(apiCredentialsDivCloned);
                }
                $(".secondlevel_menutabs").removeClass("on");
                $("#apiCredentials").addClass("on");
                $("#previous_tab").val("apicredentials");
                var $thisPanel = $("#verifyUserDiv");
                $thisPanel.dialog("close");

              }
            }
          });
        } else {
          showTenantPasswordVerificationBox(null);
        }
      }
    } else {
      cleanCredentialsDiv();
    }
  });


  $("#allowSecondaryId").live("click", function(event) {
    if ($(this).is(':checked')) {
      $("#syncAddressli").show();
      $("#secondaryCountry").show();
      $("#billingAddressLabel").show();
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

  $("#syncAddressCurrent").live("click", function(event) {
    if ($(this).is(':checked')) {
      $('#secondaryAddress\\.country').val($('#tenant\\.address\\.country').val());
      $("#secondaryAddress\\.country").change();
      $('#tenantSecondaryAddressStateSelect').val($('#tenant\\.address\\.state').val());
      $('#secondaryAddress\\.state').val($('#tenant\\.address\\.state').val());
      $('#secondaryAddress\\.street1').val($('#tenant\\.address\\.street1').val());
      $('#secondaryAddress\\.street2').val($('#tenant\\.address\\.street2').val());
      $('#secondaryAddress\\.city').val($('#tenant\\.address\\.city').val());
      $('#secondaryAddress\\.postalCode').val($('#tenant\\.address\\.postalCode').val());
    } else {
      $('#secondaryAddress\\.country').val($('#secondaryAddressCountry').val());
      $("#secondaryAddress\\.country").change();
      $('#tenantSecondaryAddressStateSelect').val($('#secondaryAddressState').val());
      $('#secondaryAddress\\.state').val($('#secondaryAddressState').val());
      $('#secondaryAddress\\.street1').val($('#secondaryAddressStreet').val());
      $('#secondaryAddress\\.street2').val($('#secondaryAddressStreet2').val());
      $('#secondaryAddress\\.city').val($('#secondaryAddressCity').val());
      $('#secondaryAddress\\.postalCode').val($('#secondaryAddressPostalCode').val());
    }
  });



  $("#syncAddressEdit").live("click", function(event) {
    if ($(this).is(':checked')) {
      $('#tenant\\.secondaryAddress\\.country').val($('#tenant\\.address\\.country').val());
      $("#tenant\\.secondaryAddress\\.country").change();
      $('#tenantSecondaryAddressStateSelect').val($('#tenant\\.address\\.state').val());
      $('#tenant\\.secondaryAddress\\.state').val($('#tenant\\.address\\.state').val());
      $('#tenant\\.secondaryAddress\\.street1').val($('#tenant\\.address\\.street1').val());
      $('#tenant\\.secondaryAddress\\.street2').val($('#tenant\\.address\\.street2').val());
      $('#tenant\\.secondaryAddress\\.city').val($('#tenant\\.address\\.city').val());
      $('#tenant\\.secondaryAddress\\.postalCode').val($('#tenant\\.address\\.postalCode').val());

    } else {
      $('#tenant\\.secondaryAddress\\.country').val('');
      $('#tenant\\.secondaryAddress\\.state').val('');
      $('#tenant\\.secondaryAddress\\.street1').val('');
      $('#tenant\\.secondaryAddress\\.street2').val('');
      $('#tenant\\.secondaryAddress\\.city').val('');
      $('#tenant\\.secondaryAddress\\.postalCode').val('');
      $("#tenant\\.secondaryAddress\\.country").change();
    }
  });

  $('#imglogo').change(function(e) {
    var path = document.getElementById('imglogo').value;
    var imgName = path.substring(path.lastIndexOf("\\") + 1);
    $("#logo_file_name").text(dictionary.logoName + imgName);
    $("#logo_file_name").css("display", "inline");
    $("#logo\\.errors").text("");
  });

  $('#imgfavicon').change(function(e) {
    var path = document.getElementById('imgfavicon').value;
    var faviconName = path.substring(path.lastIndexOf("\\") + 1);
    $("#favicon_file_name").text(dictionary.logoName + faviconName);
    $("#favicon_file_name").css("display", "inline");
    $("#favicon\\.errors").text("");
  });



  if (typeof($('#tenant\\.address\\.country').val()) != 'undefined') {
    if ($('#tenant\\.address\\.country').val() == 'US' || $('#tenant\\.address\\.country').val() == 'CA' ||
      $('#tenant\\.address\\.country').val() == 'AU' || $('#tenant\\.address\\.country').val() == 'IN' || $(
        '#tenant\\.address\\.country').val() == 'JP') {
      $("#stateInput").hide();
      $("#tenant\\.address\\.country").defautlinkstates("#tenantAddressStateSelect");
      $("#tenantAddressStateSelect").val($('#tenant\\.address\\.state').val());
      if ($('#tenant\\.address\\.country').val() == 'JP') {
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

  }


  if (typeof($('#tenant\\.secondaryAddress\\.country').val()) != 'undefined') {
    if ($('#tenant\\.secondaryAddress\\.country').val() == 'US' || $('#tenant\\.secondaryAddress\\.country').val() ==
      'CA' ||
      $('#tenant\\.secondaryAddress\\.country').val() == 'AU' || $('#tenant\\.secondaryAddress\\.country').val() ==
      'IN' || $('#tenant\\.secondaryAddress\\.country').val() == 'JP') {
      $("#stateSecondaryInput").hide();
      $("#tenant\\.secondaryAddress\\.country").defautlinkstates("#tenantSecondaryAddressStateSelect");
      $("#tenantSecondaryAddressStateSelect").val($('#tenant\\.secondaryAddress\\.state').val());
      if ($('#tenant\\.secondaryAddress\\.country').val() == 'JP') {
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

  }



  $("#tenant\\.secondaryAddress\\.country").change(function() {
    $('#tenant\\.secondaryAddress\\.state').val('');
    if ($('#tenant\\.secondaryAddress\\.country').val() == 'US' || $('#tenant\\.secondaryAddress\\.country').val() ==
      'CA' ||
      $('#tenant\\.secondaryAddress\\.country').val() == 'AU' || $('#tenant\\.secondaryAddress\\.country').val() ==
      'IN' || $('#tenant\\.secondaryAddress\\.country').val() == 'JP') {
      $("#stateSecondaryInput").hide();
      if ($('#tenant\\.secondaryAddress\\.country').val() == 'JP') {
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

  if (typeof($('#secondaryAddress\\.country').val()) != 'undefined') {
    if ($('#secondaryAddress\\.country').val() == 'US' || $('#secondaryAddress\\.country').val() == 'CA' ||
      $('#secondaryAddress\\.country').val() == 'AU' || $('#secondaryAddress\\.country').val() == 'IN' || $(
        '#secondaryAddress\\.country').val() == 'JP') {
      $("#stateSecondaryInput").hide();
      $("#secondaryAddress\\.country").defautlinkstates("#tenantSecondaryAddressStateSelect");
      $("#tenantSecondaryAddressStateSelect").val($('#secondaryAddress\\.state').val());
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

  }

  $("#tenant\\.address\\.country").change(function() {
    $('#tenant\\.address\\.state').val('');
    if ($('#tenant\\.address\\.country').val() == 'US' || $('#tenant\\.address\\.country').val() == 'CA' ||
      $('#tenant\\.address\\.country').val() == 'AU' || $('#tenant\\.address\\.country').val() == 'IN' || $(
        '#tenant\\.address\\.country').val() == 'JP') {
      $("#stateInput").hide();
      if ($('#tenant\\.address\\.country').val() == 'JP') {
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


  $("#tenant\\.address\\.country").linkToStates("#tenantAddressStateSelect");
  $("#tenant\\.secondaryAddress\\.country").linkToStates("#tenantSecondaryAddressStateSelect");
  $("#secondaryAddress\\.country").linkToStates("#tenantSecondaryAddressStateSelect");


  $('#tenantAddressStateSelect').change(function() {
    $('#tenant\\.address\\.state').val($('#tenantAddressStateSelect').val());
  });

  $('#tenantSecondaryAddressStateSelect').change(function() {
    $('#secondaryAddress\\.state').val($('#tenantSecondaryAddressStateSelect').val());
    $('#tenant\\.secondaryAddress\\.state').val($('#tenantSecondaryAddressStateSelect').val());

  });


  $("#issueCreditForm").validate({
    //debug : false,
    success: "valid",
    ignoreTitle: true,
    rules: {
      "credit": {
        required: true,
        twoDecimalAllowNegative: true
      },
      "comment": {
        required: true
      }
    },
    messages: {
      "credit": {
        required: i18n.errors.tenants.credit
      },
      "comment": {
        required: i18n.errors.tenants.comment
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

  $("#editAccountLimitsForm").validate({
    success: "valid",
    ignoreTitle: true,
    rules: {
      "iplimit": {
        required: true,
        limitcheck: true
      },
      "vmlimit": {
        required: true,
        limitcheck: true
      },
      "volumelimit": {
        required: true,
        limitcheck: true
      },
      "snapshotlimit": {
        required: true,
        limitcheck: true
      },
      "templatelimit": {
        required: true,
        limitcheck: true
      }
    },
    messages: {
      "iplimit": {
        required: i18n.errors.tenants.accountLimitsForm.resourceLimit.valError
      },
      "vmlimit": {
        required: i18n.errors.tenants.accountLimitsForm.resourceLimit.valError
      },
      "volumelimit": {
        required: i18n.errors.tenants.accountLimitsForm.resourceLimit.valError
      },
      "snapshotlimit": {
        required: i18n.errors.tenants.accountLimitsForm.resourceLimit.valError
      },
      "templatelimit": {
        required: i18n.errors.tenants.accountLimitsForm.resourceLimit.valError
      }
    },
    errorPlacement: function(error, element) {

      var name = element.attr('id');
      if (error.html() != "") {
        error.appendTo("#" + name + "Error");
      }
    }
  });

  jQuery.validator.addMethod("postalcode", function(postalcode, element, param) {
    if (param == 'US') {
      return this.optional(element) || postalcode.match(/(^\d{5}(-\d{4})?$)/);
    } else if (param == 'CA') {
      return this.optional(element) || postalcode.match(
        /(^[ABCEGHJKLMNPRSTVXYabceghjklmnpstvxy]{1}\d{1}[A-Za-z]{1} ?\d{1}[A-Za-z]{1}\d{1})$/);
    } else {
      return this.optional(element) || postalcode.length > 0 && /^[a-zA-Z\d]+$/.test(postalcode);
    }
  }, i18n.errors.tenants.tenant.postalCode);
  
  jQuery.validator
  .addMethod(
    "positiveInteger",
    function(value, element) {
    	return Number(value) >= 0 && Number(value) < 100000000;
    },
    g_dictionary.pricePositiveInteger);


  jQuery.validator.addMethod('limitcheck',
    function(value) {
      if (Number(value) || Number(value) == 0) {
        if (value % 1 == 0) {
          if (value >= -1) {
            return true;
          }
        }
      }
      return false;
    }, i18n.errors.tenants.accountLimitsForm.resourceLimit.valError);

  jQuery.validator.addMethod('userlimitcheck',
    function(value) {

      var url = "/portal/portal/tenants/validate_user_limit";
      var userlimit = value;
      var tenantid = $("#tenantEditForm").attr("tenantid");
      var returnValue = false;

      $.ajax({
        type: "GET",
        url: url,
        data: {
          userlimit: userlimit,
          tenantid: tenantid
        },
        dataType: "text",
        async: false,
        success: function(result) {
          if (result == 'true') {
            $('.userlimitlabel').remove();
            returnValue = true;
          }
        },
        error: function(XMLHttpRequest) {
          $('.userlimitlabel').remove();
          $('#tenant\\.maxUsersError').empty();
          $('#tenant\\.maxUsersError').append('<label class="error userlimitlabel" id="userlimitlabel">' +
            XMLHttpRequest.responseText + '</label>');
        }
      });

      return returnValue;
    });

  jQuery.validator.addMethod('prePaidAmount',
    function(value) {
      return Number(value) > 0;
    }, i18n.errors.tenants.prePaidAmount);

  jQuery.validator.addMethod('validateUsername',
    function(value, element) {
      if (requiredField(element)) {
        return value.length > 0 && /^[a-zA-Z0-9_@\.-]+$/.test(value);
      }
      return true;
    }, i18n.errors.tenants.user.validateUsername);

  jQuery.validator.addMethod('validateSuffix',
    function(value, element) {
      if (requiredField(element)) {
        return value.length > 0 && /^[a-zA-Z0-9]+$/.test(value);
      }
      return true;
    }, i18n.errors.tenants.tenant.suffixValidate);

  jQuery.validator.addMethod('validateConfirmEmail',
    function(value, element, param) {
      if (requiredField(element)) {
        if (value.toLowerCase() == param.toLowerCase())
          return true;
        return false;
      }
      return true;
    }, i18n.errors.tenants.confirmEmailEqualTo);

  jQuery.validator.addMethod("userLimit", function(value, element) {
    return this.optional(element) || /^[\\+-]*[0-9]*$/i.test(value);
  }, i18n.errors.tenants.accountLimitsForm.resourceLimit.valError);

  var resourceLimitDialogButtonOk = i18n.errors.tenants.resourceLimitDialog.buttons.ok;
  var resourceLimitDialogButtonCancel = i18n.errors.tenants.resourceLimitDialog.buttons.cancel;
  $(".resourceLimitDiv").dialog({
    autoOpen: false,
    modal: true,
    width: 600,
    height: 250,
    title: i18n.errors.tenants.resourceLimitDialog.title,
    buttons: {
      resourceLimitDialogButtonCancel: function() {
        $(this).dialog('close');
      },
      resourceLimitDialogButtonOk: function() {
        var form = $(this).find('form');
        $.ajax({
          type: 'POST',
          url: form.attr('action'),
          data: form.serialize(),
          dataType: 'text',
          success: function(data) {
            $(".resourceLimitDiv").dialog('close');
          },
          error: function(request) {
            $(this).dialog('close');
          }

        });
      }
    }
  });



  var changeOwnerDialogButtonOk = i18n.errors.tenants.changeOwnerDialog.buttons.ok;
  var changeOwnerDialogButtonCancel = i18n.errors.tenants.changeOwnerDialog.buttons.cancel;
  $(".changeOwnerDiv").dialog({
    autoOpen: false,
    modal: true,
    title: i18n.errors.tenants.changeOwnerDialog.title,
    buttons: {
      changeOwnerDialogButtonCancel: function() {
        $(this).dialog('close');
      },
      changeOwnerDialogButtonOk: function() {
        var form = $(this).find('form');
        var message = $(this).find('.changeOwnerMessage');
        if (message.text() != '') {
          message.text('');
          $(this).dialog('close');
          form.show();
          return;
        }
        $.ajax({
          type: 'POST',
          url: form.attr('action'),
          data: form.serialize(),
          dataType: 'text',
          success: function(data) {
            $(".changeOwnerDiv").dialog('close');

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
  $(".setOwnerForm").submit(function() {
    return false;
  });

  $(".changeOwnerAction").click(function() {
    $("#changeOwnerDiv").dialog('open');
  });


  $("#receivedOn").datepicker();
  $(".ui-datepicker").css({
    'z-index': 1003
  });
  var recordDepositDialogButtonSave = i18n.errors.tenants.recordDepositDialog.buttons.save;
  var recordDepositDialogButtonCancel = i18n.errors.tenants.recordDepositDialog.buttons.cancel;
  $("#recordDepositDiv").dialog({
    autoOpen: false,
    modal: true,
    width: 450,
    title: 'Confirm Deposit Received',
    close: function(event, ui) {
      location.reload(true);
    },
    buttons: {
      recordDepositDialogButtonCancel: function() {
        $(this).dialog('close');
      },
      recordDepositDialogButtonSave: function() {
        var form = $(this).find('form');
        var message = $(this).find('.recordDepositMessage');
        if (message.text() != '') {
          message.text('');
          $("#recordDepositMessageBox").hide();
          $(this).dialog('close');
          form.show();
          return;
        }
        form.validate({
          //debug : true,
          success: "valid",
          ignoreTitle: true,
          rules: {
            "receivedOn": {
              required: true,
              date: true
            },
            "amount": {
              required: true
            }
          },
          messages: {
            "receivedOn": {
              required: i18n.errors.tenants.recordDepositDialog.validate.receivedOn
            },
            "amount": {
              required: i18n.errors.tenants.recordDepositDialog.validate.amount
            }
          }
        });
        if (form.valid()) {
          $.ajax({
            type: 'POST',
            url: form.attr('action'),
            data: form.serialize(),
            dataType: 'text',
            success: function(data) {
              $("#recordDepositDiv").dialog('close');

            },
            error: function(request) {
              if (request.status == 412) {
                form.hide();
                message.text(request.responseText);
                $("#recordDepositMessageBox").show();
              }
            }
          });
        }
      }
    }
  });
  $("#recordDepositForm").submit(function() {
    return false;
  });

  $("#recordDepositAction").click(function() {
    $("#recordDepositDiv").dialog('open');
  });

  $("#showActions").click(function() {
    $("#actionsDropdown").toggle();
  });

  $(".editResourceLimits").click(function() {
    refreshDivs(".resourceLimitsDiv");
    blockUI();
    var tenantId = $(this).attr('id');
    var newurl = "/portal/portal/tenants/" + tenantId + "/resource_limit";
    $.ajax({

      type: "GET",
      url: newurl,
      dataType: "html",
      success: function(html) {
        $("#resourceLimitsDiv" + tenantId).html(html);
        unBlockUI();
      },
      error: function(html) {
        $(".resourceLimitsDiv").each(function() {
          $(this).unbind('click');
        });
        unBlockUI();
        return false;
      }

    });
    $(".resourceLimitsDiv").each(function() {
      $(this).unbind('click');
    });
    return false;
  });
  $("#resourceLimitForm").submit(function(event) {
    event.preventDefault();
    var datatype = "html";
    if ($("#resourceLimitForm").valid()) {
      blockUI();
      $.ajax({
        type: "POST",
        url: $(this).attr('action'),
        data: ($(this).serialize()),
        dataType: datatype,
        success: function(status) {
          if (status == "success") {
            $(".resourceLimitsDiv").html("");
          } else {
            $(".resourceLimitsDiv").html(i18n.errors.tenants.resourceLimitForm.error);
          }
          unBlockUI();
        },
        error: function(request) {
          unBlockUI();
          $(".resourceLimitsDiv").html(i18n.errors.tenants.resourceLimitForm.error);
        }

      });
    }
  });
  $("#resourceLimitForm").validate({
    //debug : true,
    success: "valid",
    ignoreTitle: true,
    rules: {
      "maxProjects": {
        required: true,
        number: true
      },
      "maxUsers": {
        required: true,
        number: true
      }
    },
    messages: {
      "maxProjects": {
        required: i18n.errors.tenants.resourceLimitForm.maxProjects
      },
      "maxUsers": {
        required: i18n.errors.tenants.resourceLimitForm.maxUsers
      }
    },
    errorPlacement: function(error, element) {
      if (element.is("#maxProjects")) {
        error.appendTo("#maxProjectsError");
      } else if (element.is("#maxUsers")) {
        error.appendTo("#maxUsersError");
      } else
        error.appendTo(element.parent());
    }
  });
  $("#resourcelimitcancel").click(function() {
    $(".resourceLimitsDiv").html("");
  });

  setTimeout('$("#tenantForm input:visible:first").focus()', 1000);

  /**
   * transaction history for tenant (GET)
   */
  $("#viewStateChanges_tab").live('click', function() {
    var divId = $(this).attr('refid');
    var ID = divId.substr(16);
    $("#spinning_wheel_rhs").show();
    var actionurl = "/portal/portal/tenants/stateChanges";
    $.ajax({
      type: "GET",
      url: actionurl,
      data: {
        tenant: ID
      },
      dataType: "html",
      cache: false,
      success: function(html) {
        $("#viewStateChangesDiv").html("");
        $("#viewStateChangesDiv").append($(".widget_details_actionbox").first().clone()).append(html);

        $('#viewStateChanges_tab').removeClass('nonactive').addClass("active");
        $('#details_tab').removeClass('active').addClass("nonactive");
        $('#viewLimits_tab').removeClass('active').addClass("nonactive");
        $('#viewPendingChanges_tab').removeClass('active').addClass("nonactive");

        $('#details_div').hide();
        $('#tenantAccountLimitsDivOuter').hide();
        $('#viewPendingChanges_details').hide();
        $('#viewStateChangesDiv').show();

      },
      error: function() {
        popUpDialogForAlerts("dialog_info", g_dictionary.failed);
      },
      complete: function() {
        $("#spinning_wheel_rhs").hide();
      }
    });
  });

});

var showTrialCode = false;
$.validator
  .addMethod(
    "twoDecimal",
    function(value, element) {
      $(element).rules("add", {
        number: true
      });
      isPriceValid = (value != "" && isNaN(value) == false && value >= 0 && value <= 99999999.99);
      if (isPriceValid == false) {
        return false;
      }
      return true;
    },
    i18n.errors.tenants.tenant.spendlimit);

$.validator
  .addMethod(
    "twoDecimalAllowNegative",
    function(value, element) {
      $(element).rules("add", {
        number: true
      });
      isPriceValid = (value != "" && isNaN(value) == false && value >= -99999999.99 && value <= 99999999.99);
      if (isPriceValid == false) {
        return false;
      }
      return true;
    },
    i18n.errors.tenants.tenant.spendlimit);

$.validator
.addMethod(
  "maxcurrencyPrecision",
  function(value, element) {
  $(element).rules("add", {
      number: true
    });
if (!/^(?:\d*\.\d*\|\d+)$/.test(value)) {
  var sval=value;
  var sdecimalDigit=sval.split(".");
  if(sdecimalDigit.length==2){// have decimal
     if(sdecimalDigit[1].length>currPrecision){
        // more characters allowed precision
       return false;
     }
     return true;
     }else if(sdecimalDigit.length>2) {
  //more then one decimals   
      return false;
 } 
     // without decimal
      return true;     
            
}else{//not a valid digit pattern
   return false;
  } 
 },
 i18n.errors.tenants.tenant.max_four_decimal_value);


$('#editSecondaryAddressSaveButton').live('click', function() {
  $("#tenantSecondaryAddressEditForm").validate({
    success: "valid",
    ignoreTitle: true,
    rules: {
      "secondaryAddress.country": {
        required: true
      },
      "secondaryAddress.state": {
        required: true
      },
      "secondaryAddress.street1": {
        required: true
      },
      "secondaryAddress.city": {
        required: true
      },
      "secondaryAddress.postalCode": {
        required: true,
        postalcode: function() {
            return $('#secondaryAddress\\.country').val();
          }
      }
    },
    messages: {
      "secondaryAddress.country": {
        required: i18n.errors.tenants.secondaryAddress.country
      },
      "secondaryAddress.state": {
        required: i18n.errors.tenants.secondaryAddress.state
      },
      "secondaryAddress.street1": {
        required: i18n.errors.tenants.secondaryAddress.street1
      },
      "secondaryAddress.city": {
        required: i18n.errors.tenants.secondaryAddress.city
      },
      "secondaryAddress.postalCode": {
        required: i18n.errors.tenants.secondaryAddress.postalCode
      }
    },
    ignore: "",
    errorPlacement: function(error, element) {
      var name = element.attr('id');
      name = ReplaceAll(name, ".", "\\.");
      if (error.html() != "") {
        error.appendTo("#" + name + "Error");
      }
    }
  });
});




$('#editTenantSave').live('click', function() {
  $("#tenantEditForm").validate({
    success: "valid",
    ignoreTitle: true,
    rules: {
      "tenant.tenantExtraInformation.discountPercent": {
        number: true,
        range: [0, 100]
      },
      "tenant.address.country": {
        required: true
      },

      "tenant.address.state": {
        required: true
      },
      "tenant.address.street1": {
        required: true
      },
      "tenant.address.city": {
        required: true
      },
      "tenant.name": {
        required: true
      },
      "tenant.address.postalCode": {
        required: true,
        postalcode: function() {
          return $('#tenant\\.address\\.country').val();
        }
      },
      "tenantAddressStateSelect": {
        validateState: true

      },
      "tenant.secondaryAddress.country": {
        required: function() {
          if ($("#allowSecondaryId").is(':checked'))
            return true;
          return false;
        }
      },

      "tenant.secondaryAddress.state": {
        required: function() {
          if ($("#allowSecondaryId").is(':checked'))
            return true;
          return false;
        }
      },
      "tenant.secondaryAddress.street1": {
        required: function() {
          if ($("#allowSecondaryId").is(':checked'))
            return true;
          return false;
        }
      },
      "tenant.secondaryAddress.city": {
        required: function() {
          if ($("#allowSecondaryId").is(':checked'))
            return true;
          return false;
        }
      },
      "tenant.secondaryAddress.postalCode": {
        required: function() {
          if ($("#allowSecondaryId").is(':checked'))
            return true;
          return false;
        }
      },
      "tenant.maxUsers": {
        required: true,
        limitcheck: true,
        max: 99999999,
        userLimit: true,
        userlimitcheck: true
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
      },
      "tenantSecondaryAddressStateSelect": {
        validateState: function() {
          if ($("#allowSecondaryId").is(':checked'))
            return true;
          return false;
        }

      },
      "tenant.spendLimit": {
        number: true,
        maxcurrencyPrecision : true,
        positiveInteger:true
      },
    },
    messages: {
      "tenant.address.country": {
        required: i18n.errors.tenants.tenant.country
      },
      "tenant.address.state": {
        required: i18n.errors.tenants.tenant.state
      },
      "tenant.address.street1": {
        required: i18n.errors.tenants.tenant.street1
      },
      "tenant.address.city": {
        required: i18n.errors.tenants.tenant.city
      },
      "tenant.name": {
        required: i18n.errors.tenants.tenant.name
      },
      "tenant.address.postalCode": {
        required: i18n.errors.tenants.tenant.postalCode
      },
      "tenant.secondaryAddress.country": {
        required: i18n.errors.tenants.secondaryAddress.country
      },
      "tenant.secondaryAddress.state": {
        required: i18n.errors.tenants.secondaryAddress.state
      },
      "tenant.secondaryAddress.street1": {
        required: i18n.errors.tenants.secondaryAddress.street1
      },
      "tenant.secondaryAddress.city": {
        required: i18n.errors.tenants.secondaryAddress.city
      },
      "tenant.secondaryAddress.postalCode": {
        required: i18n.errors.tenants.secondaryAddress.postalCode
      },
      "tenant.maxUsers": {
        required: i18n.errors.tenants.accountLimitsForm.resourceLimit.valError,
        userLimit: i18n.errors.tenants.accountLimitsForm.resourceLimit.valError,
        userlimitcheck: "",
        max: i18n.errors.tenants.accountLimitsForm.resourceLimit.valError,
      },
      "tenant.address.postalCode": {
        required: i18n.errors.tenants.tenant.postalCode
      },
      "secondaryAddress.country": {
        required: i18n.errors.tenants.secondaryAddress.country
      },
      "secondaryAddress.state": {
        required: i18n.errors.tenants.secondaryAddress.state
      },
      "secondaryAddress.street1": {
        required: i18n.errors.tenants.secondaryAddress.street1
      },
      "secondaryAddress.city": {
        required: i18n.errors.tenants.secondaryAddress.city
      },
      "secondaryAddress.postalCode": {
        required: i18n.errors.tenants.secondaryAddress.postalCode
      }
    },
    ignore: "",
    errorPlacement: function(error, element) {
      var name = element.attr('id');
      name = ReplaceAll(name, ".", "\\.");
      if (error.html() != "") {
        $('.userlimitlabel').remove();
        $("#" + name + "Error").empty();
        error.appendTo("#" + name + "Error");
      }
    }
  });
});

$('#editAccountLimitsSave').live('click', function() {
  $("#editAccountLimitsForm").validate({
    success: "valid",
    ignoreTitle: true,
    rules: {
      "iplimit": {
        required: true
      },
      "vmlimit": {
        required: true
      },
      "volumelimit": {
        required: true
      },
      "snapshotlimit": {
        required: true
      },
      "templatelimit": {
        required: true
      }
    },
    messages: {
      "iplimit": {
        required: i18n.errors.tenants.accountLimitsForm.resourceLimit.valError
      },
      "vmlimit": {
        required: i18n.errors.tenants.accountLimitsForm.resourceLimit.valError
      },
      "volumelimit": {
        required: i18n.errors.tenants.accountLimitsForm.resourceLimit.valError
      },
      "snapshotlimit": {
        required: i18n.errors.tenants.accountLimitsForm.resourceLimit.valError
      },
      "templatelimit": {
        required: i18n.errors.tenants.accountLimitsForm.resourceLimit.valError
      }
    },
    errorPlacement: function(error, element) {
      var name = element.attr('id');
      if (error.html() != "") {
        error.appendTo("#" + name + "Error");
      }
    }
  });
});

/**
 * View tenant details
 * @param current
 * @return
 */

function viewTenant(current) {
  var divId = $(current).attr('id');
  var ID = divId.substr(3);
  resetGridRowStyle();
  //	 emptyAllTenantDivs();
  $(current).addClass("selected active");
  var url = "/portal/portal/tenants/viewtenant";
  $("#spinning_wheel_rhs").show();
  $.ajax({
    type: "GET",
    url: url,
    data: {
      tenant: ID
    },
    dataType: "html",
    success: function(html) {
      $("#viewTenantDiv").html(html);
      bindActionMenuContainers();
      $("#spinning_wheel_rhs").hide();
    }
  });
}

function viewFirstTenant(divId) {
  var ID = divId.substr(3);
  $("#spinning_wheel_rhs").show();
  var url = "/portal/portal/tenants/viewtenant";
  $.ajax({
    type: "GET",
    url: url,
    data: {
      tenant: ID
    },
    dataType: "html",
    success: function(html) {
      $("#viewTenantDiv").html(html);
      bindActionMenuContainers();
    },
    error: function() {
      //need to handle TO-DO
    },
    complete: function() {
      $("#spinning_wheel_rhs").hide();
    }
  });
}

function emptyAllTenantDivs() {
  $("#viewTenantDiv").show();
  $("#viewTenantDiv").html("");
  $("#editTenantDiv").html("");
  $("#issueCreditDiv").html("");
  $("#viewStateChangesDiv").html("");
  $("#editTenantsLimitDiv").html("");
}

function emptyTenantDivsForAccountLimit() {
  $("#tenantAccountLimitsDivOuter").hide();
  $("#editTenantDiv").hide();
  $("#issueCreditDiv").hide();
  $("#viewStateChangesDiv").hide();
}

/**
 * Get Tenant Account Limits (GET)
 */

function listAccountLimits(current) {

  $('#viewLimits_tab').removeClass('nonactive').addClass("active");
  $('#details_tab').removeClass('active').addClass("nonactive");
  $('#viewStateChanges_tab').removeClass('active').addClass("nonactive");
  $('#viewPendingChanges_tab').removeClass('active').addClass("nonactive");

  $('#details_div').hide();
  $('#viewStateChangesDiv').hide();
  $('#viewPendingChanges_details').hide();
  changeInstances();
  $('#tenantAccountLimitsDivOuter').show();
}

function changeInstances() {
  var serviceParam = $("#selectedService").val();
  var $instanceDropDown = $("#selectedInstance");
  $instanceDropDown.removeOption(/./);
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
  var instanceParam = $("#selectedInstance").val();
  var tenantParam = $("#tenantParam").val();
  $("#tenantAccountLimitsDiv").html("");
  //$("#tenantMaxUserLimitsDiv").show();
  if (instanceParam != undefined && tenantParam != undefined) {
    refreshAccountLimts(tenantParam, instanceParam);
  }
}

function refreshAccountLimts(tenantParam, instanceParam) {
  var actionurl = "/portal/portal/tenants/list_account_limits";
  $("#spinning_wheel_rhs").show();
  $.ajax({
    type: "GET",
    url: actionurl,
    data: {
      tenantParam: tenantParam,
      instanceParam: instanceParam
    },
    dataType: "html",
    success: function(html) {
      $("#tenantAccountLimitsDiv").html(html);
      // $("#tenantMaxUserLimitsDiv").hide(); TODO remove this?
      $("#spinning_wheel_rhs").hide();
    },
    error: function(xhr, ajaxOptions, thrownError) {
    	var customStyle = {
    		'width' : '92%',
    		'float' : 'left',
    		'margin-top' : '10px'
    	};
      CPBM.showErrorWithPopover($('#tenantAccountLimitsDiv'), xhr.responseText, customStyle);
      $("#spinning_wheel_rhs").hide();
    },
    complete: function() {
    	
    }
  });
}



/**
 * Edit Account Resource Limits (GET)
 */

function editAccountLimits(current) {
  var instanceParam = $(current).attr('instanceparam');
  var tenantParam = $(current).attr('tenantuuid');
  var actionurl = "/portal/portal/tenants/edit_account_limits";
  $("#spinning_wheel_rhs").show();
  $.ajax({
    type: "GET",
    url: actionurl,
    data: {
      tenantuuid: tenantParam,
      instanceParam: instanceParam
    },
    dataType: "html",
    success: function(html) {
      $("#editTenantsLimitDiv").html("");
      $("#editTenantsLimitDiv").html(html);
      $("#spinning_wheel_rhs").hide();
      var $thisPanel = $("#editTenantsLimitDiv");
      $("#editAccountLimitsForm").attr("tenantid", tenantParam);
      //$thisPanel.dialog({ height: 100, width : 600 });
      $thisPanel.dialog("option", "title", i18n.errors.tenants.accountLimitsForm.title);
      $thisPanel.dialog('option', 'buttons', {

      }).dialog("open");
    },
    error: function(xhr, ajaxOptions, thrownError) {
      var customStyle = {
		'width' : '92%',
		'float' : 'left',
		'margin-top' : '10px'
	  };
      CPBM.showErrorWithPopover($('#tenantAccountLimitsDiv'), xhr.responseText, customStyle);	
      $("#spinning_wheel_rhs").hide();
      $(".editAccountLimits").unbind('click');
    },
    complete: function() {
    	
    }
  });
}

function saveAcccountControls() {
  $("#spinning_wheel_rhs").show();
  $form = $("#accountControlsForm");

  var instanceParam = $form.find("#instanceParam").val();
  var tenantParam = $form.find("#tenantParam").val();

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
    url: "/portal/portal/tenants/edit_account_limits",
    data: {
      "configProperties": JSON.stringify(configProperties),
      "instanceParam": instanceParam,
      "tenantParam": tenantParam
    },
    dataType: "json",
    success: function(data) {
      $("#spinning_wheel_rhs").hide();
      if (data.validationResult == "SUCCESS") {
        if (data.result == "SUCCESS") {
          $("#submitbutton").removeClass("active").addClass("nonactive");
          var $limitdetails = $('div[id^=instanceparam_' + instanceParam + '].details');
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
        }
      } else {
        //TODO Need to display error if it fails
      }
    },
    error: function(xhr, ajaxOptions, thrownError) {
      $("#spinning_wheel_rhs").hide();
      var customStyle = {
		'width' : '92%',
		'float' : 'left'
	  };
      CPBM.showErrorWithPopover($('#accountControlsForm #accountControlsFormError').show(), xhr.responseText, customStyle);
      $("#editTenantsLimitDiv").dialog("open");
    },
    complete: function(){
    	
    }
  });
  closeAccountTypeControlsDialog();
}

function addTenantInListView(jsonResponse) {
  var newTenant = $("#newTenantRowTemplate").clone();
  newTenant.attr("id", "row" + jsonResponse["tenantParam"]);
  newTenant.attr("tenantid", jsonResponse["tenantId"]);

  var isOdd = $("#grid_row_container").find(".widget_navigationlist:first").hasClass('odd');
  if (isOdd == true) {
    newTenant.addClass('even');
  } else {
    newTenant.addClass('odd');
  }
  newTenant.addClass('selected');
  newTenant.addClass('active');

  newTenant.find("#new_tenant_name").each(function() {
    $(this).text(jsonResponse["tenantName"]);
  });

  var accountType = jsonResponse["tenantAccountTypeName"];
  var iconClass = "defualt";
  if (accountType.toLowerCase() == "corporate") {
    iconClass = "corporate";
  } else if (accountType.toLowerCase() == "retail") {
    iconClass = "retail";
  } else if (accountType.toLowerCase() == "trial") {
    iconClass = "trial";
  } else if (accountType.toLowerCase() == "system") {
    iconClass = "system";
  }
  newTenant.find("#nav_icon").addClass(iconClass);

  newTenant.find("#new_tenant_account_type").text(i18nAccountTypeDictionary.accountType);
  newTenant.find("#new_tenant_account_id").text(jsonResponse["tenantAccountId"]);
  newTenant.find("#new_tenant_owner_username").text(jsonResponse["tenantOwnerUserName"]);

  newTenant.show();
  $("#grid_row_container").prepend(newTenant);

  var tenantsCount = $("#grid_row_container").find(".widget_navigationlist").size();
  // remove last element if count grater than pagination value
  if (tenantsCount > perPageValue) {
    $("#grid_row_container").find(".widget_navigationlist:last").remove();
  }
  // reset styling
  resetGridRowStyle();
  $("#grid_row_container").find(".widget_navigationlist:first").click();
  $("#grid_row_container").find("#non_list").remove();
}

function addTenantPrevious(current) {
	$("#tenantCreateErrorDiv").hide();
  var prevStep = $(current).parents(".j_tenantspopup").find('#prevstep').val();
  if (prevStep != "") {
    $(".j_tenantspopup").hide();
    $("#" + prevStep).show();
  }
  $(".text.error").val('');
  if ($("#ad_import_enabled").val() == "ad_import_enabled" && prevStep == "step1") {
    $("#user\\.username").removeAttr('readonly');
  }
}
trial_selected = false;

var filteredCountries;
function addTenantNext(current) {
	$("#tenantCreateErrorDiv").hide();
  var currentstep = $(current).parents(".j_tenantspopup").attr('id');
  var $currentstep = $("#" + currentstep);
  var nextstep = $currentstep.find("#nextstep").val();
  trial_selected = false;
  if (trialAccount && currentstep == "step2") {
    trial_selected = true;
  }

  if ($("#accountForm").valid()) {
    if (showTrialCode == false) {
      $("#trialCodeBox").hide();
    }
    if (showTrialCode == true) {
      $("#trialCodeBox").show();
    }
    


    if (currentstep == "step4") {
      $("#step5").find("#tenant_selected_channel").text(
          $("#channelParam option:selected").text());
      $("#step5").find("#tenant_selected_accounttype").text(
          $("input:checked").parents("li").find("span.j_description").text());
      
      $("#step5").find("#tenant_username").text($("#user\\.username").val());
      $("#step5").find("#tenant_firstname").text($("#user\\.firstName").val());
      $("#step5").find("#tenant_lastname").text($("#user\\.lastName").val());
      $("#step5").find("#tenant_email").text($("#user\\.email").val());
      $("#step5").find("#tenant_confirm_email").text($("#confirmEmail").val());

      $("#step5").find("#tenant_company_name").text($("#tenant\\.name").val());
      $("#step5").find("#tenant_currency").text($("#currency option:selected").text());
      $("#step5").find("#tenant_language").text($("#user\\.locale option:selected").text());
      
      $("#step5").find("#tenant_country").text($("#user\\.address\\.country option:selected").text());
      $("#step5").find("#tenant_state").text($("#user\\.address\\.state").val());
      $("#step5").find("#tenant_address1").text($("#user\\.address\\.street1").val());
      $("#step5").find("#tenant_address2").text($("#user\\.address\\.street2").val());
      $("#step5").find("#tenant_city").text($("#user\\.address\\.city").val());
      $("#step5").find("#tenant_zipCode").text($("#user\\.address\\.postalCode").val());
      $("#step5").find("#tenant_phoneNumber").text($("#user\\.countryCode").val() + "-" + $("#user\\.phone").val());
    }

    if (currentstep == "step3") {
      var $step6 = $("#step6");
      $step6.find("#successmessage").empty();
      $step6.find("#successmessage").text(confirmation_msg);
      $step6.find("#successmessage").append($("#tenant\\.name").val());
    }
    if (currentstep == "step2") {
      $("#spinning_wheel2").show();
      $.ajax({
        type: 'GET',
        url: '/portal/portal/validate_username',
        data: {
          "user.username": $("#user\\.username").val()
        },
        dataType: "json",
        async: false,
        success: function(jsonResponse) {
          if (jsonResponse == true) {
            $.ajax({
              type: 'GET',
              url: '/portal/portal/validate_email_domain',
              data: {
                "user.email": $("#user\\.email").val(),
                "channelParam": $("#channelParam").val()
              },
              dataType: "json",
              async: false,
              success: function(jsonResponse) {
                if (jsonResponse == true) {
                  $("#spinning_wheel2").hide();
                  $(".j_tenantspopup").hide();
                  $("#" + nextstep).show();
                  $("#" + nextstep).find(".dialog_formcontent").empty();
                  var firstInput = $("#" + nextstep).find(
                    'input[type=text],input[type=password],input[type=radio],input[type=checkbox],textarea,select')
                    .filter(':visible:first');
                  if (firstInput != null) {
                    firstInput.focus();
                  }
                } else
                  $("#spinning_wheel2").hide();
              },
              error: function(XMLHttpRequest) {
                $("#spinning_wheel2").hide();
              }
            });
          } else {
            $("#spinning_wheel2").hide();
          }
        },
        error: function(XMLHttpRequest) {
          $("#spinning_wheel2").hide();
        }
      });
    } else if (currentstep == "step5") {
      $("#add_tenant_next5").attr('value', dictionary.label_adding);
      $("#spinning_wheel5").show();
      $.ajax({
        type: "POST",
        url: $("#accountForm").attr('action'),
        data: $("#accountForm").serialize(),
        dataType: "json",
        async: false,
        success: function(jsonResponse) {
          addTenantInListView(jsonResponse);
          $("#step6").find("#tenantParam").val(jsonResponse['tenantParam']);
          $("#spinning_wheel5").hide();
          $(".j_tenantspopup").hide();
          $("#" + nextstep).show();
        },
        error: function(XMLHttpRequest) {
          if (XMLHttpRequest.status === AJAX_FORM_VALIDATION_FAILED_CODE) {
            var fieldErrorList = displayAjaxFormError(XMLHttpRequest,
              "accountForm",
              "main_addnew_formbox_errormsg");
            $("#add_tenant_next5").attr('value', dictionary.label_next);
            $("#spinning_wheel5").hide();
            checkErrors1(fieldErrorList, currentstep);
            if(fieldErrorList.length > 0){
            	$("#tenantCreateErrorDiv").show();
            }
          } else if (XMLHttpRequest.status === CODE_NOT_UNIQUE_ERROR_CODE) {
            popUpDialogForAlerts("dialog_info", "Not unique");
          } else {
            window.location.href = "/portal/portal/errors/error";
          }
        }
      });

    } else if (currentstep == "step6") {
      $currentDialog.dialog("close");
      $("#newTenantDiv").find(".dialog_formcontent").empty();
      //redirectToTenantsList();
    } else {
      if(currentstep == "step1"){
        selectDefaultLocale();
      }
      $(".j_tenantspopup").hide();
      $("#" + nextstep).show();
      $("#" + nextstep).find(".dialog_formcontent").empty();
      var firstInput = $("#" + nextstep).find(
        'input[type=text],input[type=password],input[type=radio],input[type=checkbox],textarea,select').filter(
        ':visible:first');
      if (firstInput != null) {
        firstInput.focus();
      }
      if ($("#ad_import_enabled").val() == "ad_import_enabled") {
        $("#add_tenant_next2").attr('disabled', 'disabled');
      }
    }
  }

}

function importAdUserToAddAccount(current) {
  var usernameforad = $("#user\\.username").val();
  actionurl = '/portal/portal/users/importfromad.json?username=' + usernameforad;
  $.ajax({
    type: "GET",
    url: actionurl,
    dataType: "json",
    success: function(userdetails) {
      $("#add_tenant_next2").removeAttr('disabled');
      $("#user\\.username").attr('readonly', 'true');
      $("#user\\.firstName").val(userdetails.firstName);
      $("#user\\.lastName").val(userdetails.lastName);
      $("#user\\.email").val(userdetails.email);
      $("#confirmEmail").val(userdetails.email);
      $("#tenant\\.name").val(userdetails.companyName);
      $("#user\\.address\\.country").val(userdetails.countryName);
      if (userdetails.countryName != null) {
        if ($('#user\\.address\\.country').val() == 'US' || $('#user\\.address\\.country').val() == 'CA' ||
          $('#user\\.address\\.country').val() == 'AU' || $('#user\\.address\\.country').val() == 'IN' || $(
            '#user\\.address\\.country').val() == 'JP') {
          $("#stateInput").hide();
          $("#user\\.address\\.country").defautlinkstates("#userAddressStateSelect");
          if (state_name_to_state_codes[userdetails.countryName + '.' + userdetails.stateName.toLowerCase()] == null) {
            $("#userAddressStateSelect").val("");
          } else {
            $("#userAddressStateSelect").val(state_name_to_state_codes[userdetails.countryName + '.' + userdetails.stateName
              .toLowerCase()]);
          }
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

      }
      $('#user\\.address\\.state').val(userdetails.stateName);
      $('#user\\.address\\.city').val(userdetails.cityName);
      $('#user\\.address\\.street1').val(userdetails.street1);
      $('#user\\.address\\.street2').val(userdetails.street2);
      $('#user\\.address\\.postalCode').val(userdetails.postalCode);
    },
    error: function(e) {
      popUpDialogForAlerts("dialog_info", e.responseText);
    }
  });
}


function checkErrors1(fieldErrorList, currentStepFlowId) {
  var stepToNavigate = 100;  
  $.each(fieldErrorList, function(index, value) {
    console.log(index + '..checkErrors1..' + value);
    var escapedValue = value.replace(/\./g, '\\.');
    var currentNum = currentStepFlowId.substring(4);
    for (var index = 2; index < currentNum; index++) {
      var currentIterStep = "#step" + index;
      var currentIterStepError = currentIterStep + "  #" + escapedValue;
      var stepDiv = $(currentIterStepError);
      var length = stepDiv.length;
      console.log('currentNum=', currentIterStepError, '..length...', length);
      if (length > 0) {
        var foundStepNum = currentIterStep.substring(5);
        if (foundStepNum < stepToNavigate) {
          stepToNavigate = foundStepNum;
        }
        break;
      }
    }
  });
  console.log('..stepToNavigate..', stepToNavigate);
  if (stepToNavigate != 100 && fieldErrorList.length > 0) {    
    $(".j_tenantspopup").hide();
    $("#step" + stepToNavigate).show();
  }
  
}

function onAddTenantLoad() {
  $("#trialCodeBox").hide();
  $("#channelParam").change(function() {

    var channelParam = $(this).val();
    $.ajax({
      type: 'GET',
      url: tenanturl + channelParam + "/list_currencies/",
      dataType: 'text',
      async: false,
      success: function(data) {
        $("#currency").html(data);

      },
      error: function(request) {
        popUpDialogForAlerts("dialog_info", 'Failed to load currencies for channel');
      }
    });

    if ($("#defaultChannel").val() != "") {
      $.ajax({
        type: 'GET',
        url: tenantUrl + channelParam + "/verify_channel_promocode/",
        dataType: 'html',
        async: false,
        success: function(result) {
          if (result == "success") {
            showTrialCode = true;
          } else {
            showTrialCode = false;
          }
        },
        error: function(request) {
          showTrialCode = false;
        }
      });
    }
  });

  $("#backtostep1").bind("click", function(event) {
    $(".j_tenantspopup").hide();
    currentstep = "step1";
    $("#step1").show();
  });
  $("#backtostep2").bind("click", function(event) {
    $(".j_tenantspopup").hide();
    currentstep = "step2";
    $("#step2").show();
  });
  $("#backtostep3").bind("click", function(event) {
    $(".j_tenantspopup").hide();
    currentstep = "step3";
    $("#step3").show();
  });
  $("#backtostep4").bind("click", function(event) {
    $(".j_tenantspopup").hide();
    currentstep = "step4";
    $("#step4").show();
  });

  $("#syncAddressli").hide();
  $("#billingAddressLabel").hide();
  $("#secondaryCountry").hide();
  $("#secondaryState").hide();
  $("#secondaryStreet1").hide();
  $("#secondaryStreet2").hide();
  $("#secondaryCity").hide();
  $("#secondaryPostalCode").hide();

  if (typeof($('#user\\.address\\.country').val()) != 'undefined') {
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
  }

    function toggleAdImport(){
      if($("#accountForm").validate().element("#user\\.username"))
      {
        $("#import_user_info_from_ad").removeAttr("disabled");
      }
      else{
        $("#import_user_info_from_ad").attr("disabled", true);
      }
    }
  
    $("#user\\.username").mouseout(function(){
      toggleAdImport();
    });
  
    $("#user\\.username").focusout(function(){
      toggleAdImport();
    });
    
    
    $("#trialCode").keyup(function(){
      $("#accountForm").validate().element("#trialCode");
    });
    
  $("#user\\.address\\.country").change(function() {
    $('#user\\.address\\.state').val('');
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
      $("#userAddressStateSelect").focus();
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


  var formValidator = $("#accountForm").validate({
    //debug : false,
    success: "valid",
    ignoreTitle: true,
    rules: {
      "channelParam": {
        required: function(element) {
          return requiredField(element);
        }
      },
      "user.firstName": {
        required: function(element) {
          return requiredField(element);
        },
        minlength: 1,
        maxlength: 255,
        flname: true
      },
      "user.lastName": {
        required: function(element) {
          return requiredField(element);
        },
        minlength: 1,
        maxlength: 255,
        flname: true
      },
      "user.title": {
        required: function(element) {
          return requiredField(element);
        },
        minlength: 1
      },
      "user.email": {
        required: function(element) {
          return requiredField(element);
        },
        email: true,
        remote: {
          url: '/portal/portal/validate_email_domain',
          data: {
            channelParam: function() {
              return $( "#channelParam" ).val();
            }
          }
        }
      },
      "confirmEmail": {
        required: function(element) {
          return requiredField(element);
        },
        email: true,
        validateConfirmEmail: function() {
          return $('#user\\.email').val();
        }
      },
      "user.username": {
        required: function(element) {
          return requiredField(element);
        },
        minlength: 5,
        maxlength: 255,
        validateUsername: true,
        remote: {
          url: '/portal/portal/validate_username'
        }
      },
      "trialCode": {
        required: function() {
          if (trial_selected) {
            return true;
          } else {
            return false;
          }
        },
        remote: {
          url: '/portal/portal/tenants/validate_trial',
          type: "get",
          async: false,
          data: {
            channelParam: function() {
              return $("#channelParam").val();
            },
            accountTypeId : function() {
              return $("#accountForm input:radio[name=accountTypeId]:checked").val();
            }
          }
        }
      },
      "user.clearPassword": {
        required: function(element) {
          return requiredField(element);
        },
        password: true,
        notEqualTo: "#user\\.username"
      },
      "passwordconfirm": {
        required: function(element) {
          return requiredField(element);
        },
        equalTo: "#user\\.clearPassword"
      },
      "tenant.name": {
        required: function(element) {
          return requiredField(element);
        }
      },
      "tenant.usernameSuffix": {
        required: function(element) {
          return requiredField(element);
        },
        minlength: 2,
        validateSuffix: true,
        remote: {
          url: '/portal/portal/validate_suffix'
        }
      },
      "user.countryCode": {
        required: function(element) {
          return requiredField(element);
        },
        countryCode: true
      },
      "user.phone": {
        required: function(element) {
          return requiredField(element);
        },
        phone: true
      },
      "currency": {
        required: function(element) {
          return requiredField(element);
        }
      },
      "user.address.street1": {
        required: function(element) {
          return requiredField(element);
        }
      },
      "user.address.city": {
        required: function(element) {
          return requiredField(element);
        }
      },
      "user.address.postalCode": {
        required: function(element) {
          return requiredField(element);
        },
        postalcode: function() {
          return $('#user\\.address\\.country').val();
        }
      },
      "secondaryAddress.country": {
        required: function(element) {
          if (requiredField(element)) {
            if ($("#allowSecondaryId").is(':checked'))
              return true;
            return false;
          }
          return false;
        }
      },
      "secondaryAddress.state": {
        required: function(element) {
          if (requiredField(element)) {
            if ($("#allowSecondaryId").is(':checked'))
              return true;
            return false;
          }
          return false;
        }
      },
      "secondaryAddress.street1": {
        required: function(element) {
          if (requiredField(element)) {
            if ($("#allowSecondaryId").is(':checked'))
              return true;
            return false;
          }
          return false;
        }
      },
      "secondaryAddress.city": {
        required: function(element) {
          if (requiredField(element)) {
            if ($("#allowSecondaryId").is(':checked'))
              return true;
            return false;
          }
          return false;
        }
      },
      "secondaryAddress.postalCode": {
        required: function(element) {
          if (requiredField(element)) {
            if ($("#allowSecondaryId").is(':checked'))
              return true;
            return false;
          }
          return false;
        },
        postalcode: function() {
          return $('#secondaryAddress\\.country').val();
        }
      },
      "user.address.country": {
        required: function(element) {
          return requiredField(element);
        }
      },
      "user.address.state": {
        required: function(element) {
          return requiredField(element);
        }
      }
    },
    messages: {
      "channelParam": {
        required: i18n.errors.tenants.channelParam
      },
      "user.firstName": {
        required: i18n.errors.tenants.user.firstName,
        flname: i18n.errors.tenants.user.flnameValidationError
      },
      "user.lastName": {
        required: i18n.errors.tenants.user.lastName,
        flname: i18n.errors.tenants.user.flnameValidationError
      },
      "user.email": {
        required: i18n.errors.tenants.user.emailRequired,
        email: i18n.errors.tenants.user.email,
        remote: i18n.errors.tenants.user.emailNotSupported
      },
      "confirmEmail": {
        required: i18n.errors.tenants.confirmEmail,
        email: i18n.errors.tenants.user.email,
        equalTo: i18n.errors.tenants.confirmEmailEqualTo
      },
      "user.username": {
        required: i18n.errors.tenants.user.username,
        minlength: i18n.errors.tenants.user.minLengthUsername,
        validateUsername: i18n.errors.tenants.user.validateUsername,
        remote: i18n.errors.tenants.user.usernameRemote
      },
      "trialCode": {
        required: i18n.errors.tenants.trailCodeRequired,
        remote: i18n.errors.tenants.trailCode
      },
      "user.clearPassword": {
        required: i18n.errors.tenants.user.clearPassword,
        password: i18n.errors.tenants.user.passwordValidationError,
        notEqualTo: i18n.errors.tenants.user.passwordequsername
      },

      "passwordconfirm": {
        required: i18n.errors.tenants.passwordconfirm,
        password: i18n.errors.tenants.user.passwordValidationError,
        equalTo: i18n.errors.tenants.passwordconfirmEqualTo
      },
      "tenant.name": {
        required: i18n.errors.tenants.tenant.name
      },
      "tenant.usernameSuffix": {
        required: i18n.errors.tenants.tenant.suffix,
        minlength: i18n.errors.tenants.tenant.suffixMinLength,
        validateSuffix: i18n.errors.tenants.tenant.suffixValidate,
        remote: i18n.errors.tenants.tenant.suffixRemoteValidate
      },
      "user.phone": {
        required: i18n.errors.tenants.user.phone,
        phone: i18n.errors.tenants.user.phoneValidationError
      },
      "user.countryCode": {
        required: i18n.errors.tenants.user.countryCode,
        countryCode: i18n.errors.tenants.user.countryCodeValidationError
      },
      "currency": {
        required: i18n.errors.tenants.currency
      },
      "user.address.street1": {
        required: i18n.errors.tenants.user.address.street1
      },
      "user.address.city": {
        required: i18n.errors.tenants.user.address.city
      },
      "user.address.postalCode": {
        required: i18n.errors.tenants.user.address.postalCode,
        postalcode: i18n.errors.tenants.user.address.postalCode
      },
      "user.address.state": {
        required: i18n.errors.tenants.user.address.state
      },
      "user.address.country": {
        required: i18n.errors.tenants.user.address.country
      },
      "secondaryAddress.street1": {
        required: i18n.errors.tenants.secondaryAddress.street1
      },
      "secondaryAddress.city": {
        required: i18n.errors.tenants.secondaryAddress.city
      },
      "secondaryAddress.postalCode": {
        required: i18n.errors.tenants.secondaryAddress.postalCode,
        postalcode: i18n.errors.tenants.secondaryAddress.postalCode
      },
      "secondaryAddress.state": {
        required: i18n.errors.tenants.secondaryAddress.state
      },
      "secondaryAddress.country": {
        required: i18n.errors.tenants.secondaryAddress.country
      }
    },
    ignore: "",
    errorPlacement: function(error, element) {
      var name = element.attr('id');
      name = ReplaceAll(name, ".", "\\.");
      if (error.html() != "") {
        error.appendTo("#" + name + "Error");
      }
    }
  });
}
/**
 * is element there in current wizard to validate or not
 * @param element
 * @returns {Boolean}
 */

function requiredField(element) {
  if ($(element).parents(".j_tenantspopup").is(':visible')) {
    return true;
  }
  return false;
}

/** Add Tenant Get
 *
 */

function addNewTenantGet() {
  //initDialog("newTenantDiv", 785);
  var actionurl = tenantUrl + "new";
  $.ajax({
    type: "GET",
    url: actionurl,
    dataType: "html",
    success: function(html) {
      var $thisDialog = $("#newTenantDiv");
      var $dialogFormContent = $thisDialog.find(".dialog_formcontent");
      $dialogFormContent.html("");
      $dialogFormContent.html(html);
      var $thisDialog = $("#newTenantDiv");
      onAddTenantLoad();
      $currentDialog = $thisDialog;
      $currentDialog.dialog('open');
      focusFirstItemInGivenContainer("step1");
    },
    error: function(e) {
      $(".widget_addbutton").unbind('click');
      if (e.responseText.indexOf("Manual Registration Not Allowed") != -1) {
        popUpDialogForAlerts("dialog_info", i18n.errors.tenants.accountType.noManualRegistrationAccountType.error);
      }
    }
  });

  var channelParam = $("#defaultChannel").val();
  if ($("#defaultChannel").val() != "") {
    $.ajax({
      type: 'GET',
      url: tenantUrl + channelParam + "/verify_channel_promocode/",
      dataType: 'html',
      async: false,
      success: function(result) {
        if (result == "success") {
          showTrialCode = true;
        } else {
          showTrialCode = false;
        }
      },
      error: function(request) {
        showTrialCode = false;
      }
    });
  }


}
/**
 * Edit tenant (GET)
 */

function editTenantGet(current) {


  var divId = $(current).attr('id');
  var tenant = divId.substr(4);
  var actionurl = "/portal/portal/tenants/edit";
  $.ajax({
    type: "GET",
    url: actionurl,
    data: {
      tenant: tenant
    },
    dataType: "html",
    success: function(html) {
      $("#editTenantDiv").html("");
      $("#editTenantDiv").html(html);
      $("#tenantEditForm").attr("tenantid", tenant);
      var $thisPanel = $("#editTenantDiv");
      $thisPanel.dialog({
        height: 100,
        width: 950
      });
      $thisPanel.dialog("option", "title", i18n.errors.tenants.tenantEditForm.title);
      $thisPanel.dialog('option', 'buttons', {

      }).dialog("open");
    },
    error: function() {
      $(".editTenant").unbind('click');
    }
  });
}

function editTenantListView(tenantUUID, tenantName) {
  var $tenantEdited = $("#grid_row_container").find("#row" + tenantUUID);
  $tenantEdited.find(".widget_navtitlebox").find(".title").text(tenantName);
  $tenantEdited.find(".account_name").text(tenantName);
  $("#grid_row_container").find("#row" + tenantUUID).click();
}

/**
 * Edit tenant POST
 * @param event
 * @param form
 * @return
 */

function editTenant(event, form) {
  if (event.preventDefault) {
    event.preventDefault();
  } else {
    event.returnValue = false;
  }
  if ($("#tenantEditForm").valid()) {
    $("#editTenantSave").attr("value", i18n.errors.tenants.tenantEditForm.saving);
    $.ajax({
      type: "POST",
      url: $(form).attr('action'),
      data: $(form).serialize(),
      dataType: "json",
      success: function(jsonResponse) {
        $('.userlimitlabel').remove();
        if ($("#editTenantDiv").length) {
          var $thisPanel = $("#editTenantDiv");
          $thisPanel.dialog("close");
          $("#editTenantSave").attr("value", i18n.errors.tenants.tenantEditForm.save);
          var tenantUUID = $(form).attr('tenantid');
          var tenantName = $(form).find("#tenant\\.name").val();
          editTenantListView(tenantUUID, tenantName);
        } else {
          window.location = "/portal/portal/tenants/editcurrent?currenttab=contact";
        }
      },
      error: function() {
        $('.userlimitlabel').remove();
        $("#editTenantSave").attr("value", i18n.errors.tenants.tenantEditForm.save);
        $("#miscFormErrors").text(i18n.errors.tenants.tenantEditForm.error);
      }
    });
  }
}

function editTenantForSecAddress(event, form) {
  if (event.preventDefault) {
    event.preventDefault();
  } else {
    event.returnValue = false;
  }
  if ($("#tenantSecondaryAddressEditForm").valid()) {
    $("#editSecondaryAddressSaveButton").attr("value", i18n.errors.tenants.tenantEditForm.saving);
    $.ajax({
      type: "POST",
      url: $(form).attr('action'),
      data: $(form).serialize(),
      dataType: "json",
      success: function(jsonResponse) {
        window.location = "/portal/portal/tenants/editcurrent";
      },
      error: function() {
        $("#editSecondaryAddressSaveButton").attr("value", i18n.errors.tenants.tenantEditForm.save);
        $("#miscFormErrors").text(i18n.errors.tenants.tenantEditForm.error);
      }
    });
  }
}

$('#backToAccountDetails').live('click', function() {
  if ($("#editTenantDiv").length) {
    viewTenant($("div[id^='row'].selected"));
  }
});



/**
 * Issue credit for tenant (GET)
 */

function issueCreditGet(current) {
  var divId = $(current).attr('id');
  var ID = divId.substr(11);

  var actionurl = "/portal/portal/tenants/issueCredit";
  $.ajax({
    type: "GET",
    url: actionurl,
    data: {
      tenant: ID
    },
    dataType: "html",
    success: function(html) {
      $("#issueCreditDiv").html("");
      $("#issueCreditDiv").html(html);
      var $thisPanel = $("#issueCreditDiv");
      $thisPanel.dialog({
        height: 100,
        width: 950
      });
      $thisPanel.dialog("option", "title", i18n.errors.tenants.issueCreditForm.title);
      $thisPanel.dialog('option', 'buttons', {

      }).dialog("open");
      $thisPanel.find("#credit").focus();
    },
    error: function() {
      $(".issueCredit").unbind('click');

    }
  });
}

$('#credit').keyup(function() {
  $("#miscFormErrors").text("");
  $("#issueCreditSave").removeAttr("disabled");

});

function viewPendingChangesGet(current) {
  $('#viewStateChanges_tab').removeClass('active').addClass("nonactive");
  $('#details_tab').removeClass('active').addClass("nonactive");
  $('#viewLimits_tab').removeClass('active').addClass("nonactive");
  $('#viewPendingChanges_tab').removeClass('nonactive').addClass("active");

  $('#details_div').hide();
  $('#tenantAccountLimitsDivOuter').hide();
  $('#viewStateChangesDiv').hide();
  $('#viewPendingChanges_details').show();
}
/**
 * Issue credit for tenant (POST)
 * @param event
 * @param form
 * @return
 */

function issueCreditPost(event, form, selectedAccount) {
  //	var ID = $("#hiddenTenantId").val();
  if (event.preventDefault) {
    event.preventDefault();
  } else {
    event.returnValue = false;
  }
  if ($("#issueCreditForm").valid()) {
    $("#issueCreditSave").attr("value", i18n.errors.tenants.issueCreditForm.saving);
    $.ajax({
      type: "POST",
      url: $(form).attr('action'),
      data: $(form).serialize(),
      dataType: "json",
      success: function(jsonResponse) {
        $("#messageBox").html(jsonResponse.message);
        $("#messageBox").attr('style', 'margin:0; display:block');
        $("#creditBalance").html(jsonResponse.creditBalance);
        $("#issueCreditDiv").html("");
        var $thisPanel = $("#issueCreditDiv");
        $thisPanel.dialog("close");
        window.location = tenantPageUrl + "?accountType=" + selectedAccountType + "&filterBy=" + filterBy +
          "&currentPage=" + currentPage  +"&selectedAccount=" + selectedAccount;
      },
      error: function(XMLHttpRequest) {
        $("#issueCreditSave").attr("value", i18n.errors.tenants.issueCreditForm.title);
        if (XMLHttpRequest.status == PRECONDITION_FAILED) {
          $("#miscFormErrors").text(i18n.errors.tenants.issueCreditForm.invalidCreditBalance);
        } else {
          $("#miscFormErrors").text(i18n.errors.tenants.issueCreditForm.error);
        }
      }

    });

  }
}

/**
 * Change state for tenant (GET)
 */

function changeStateGet(current) {
  var divId = $(current).attr('id');
  var ID = divId.substr(11);
  var actionurl = "/portal/portal/tenants/" + ID + "/changeState";
  $.ajax({
    type: "GET",
    url: actionurl,
    data: {
      Id: ID
    },
    dataType: "html",
    success: function(html) {
      $("#editTenantDiv").html("");
      $("#editTenantDiv").html(html);
      var $thisPanel = $("#editTenantDiv");
      $thisPanel.dialog({
        height: 100,
        width: 950
      });
      $thisPanel.dialog("option", "title", i18n.errors.tenants.changeStateForm.title);
      $thisPanel.dialog('option', 'buttons', {

      }).dialog("open");
    },
    error: function() {
      $(".changeState").unbind('click');

    }
  });
}


function newStateChange(elem) {
  var selectedState = $(elem).val().toLowerCase();
  $(".label_state").hide();
  $("#label_" + selectedState).show();
}

/**
 * Change State for tenant POST
 * @param event
 * @param form
 * @return
 */

function changeAccountState(event, form) {

  if (event.preventDefault) {
    event.preventDefault();
  } else {
    event.returnValue = false;
  }
  if ($("#changeStateForm").valid()) {
    var newstate = $("#new_state").val();
    var r = false;
    if (newstate == "TERMINATED") {
      r = confirm(i18n.confirm.terminateAccount);
    }
    if ((r == true && newstate == "TERMINATED") || newstate != "TERMINATED") {
      var submit = $(form).find(".submitmsg");
      if ($(submit).attr("rel") != null && $(submit).attr("rel") != "") {
        $(submit).attr("value", $(submit).attr("rel"));
        $(submit).attr("disabled", true);
      }
      $.ajax({
        type: "POST",
        url: $(form).attr('action'),
        data: $(form).serialize(),
        dataType: "json",
        success: function(jsonResponse) {
          var $thisPanel = $("#editTenantDiv");
          $thisPanel.dialog("close");
          var filterBy = "";
          if(!$("#l3_account_All_tab").hasClass('on')){
            filterBy = "&filterBy=" + jsonResponse.stateOrdinal;
          }
          window.location = tenantPageUrl + "?accountType=" + selectedAccountType + "&currentPage=" + currentPage + filterBy;
          
        },
        error: function() {
          $("#miscFormErrors").text(i18n.errors.tenants.changeStateForm.errors);
        }
      });
    }
  }

}

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
 * Update tenant row
 */
$.editTenant = function(jsonResponse) {

  if (jsonResponse == null) {
    popUpDialogForAlerts("dialog_info",i18n.errors.tenants.editAccount.error );
  } else {
    var className = $("#grid_row_container").find("#row" + jsonResponse.param).find("#nav_icon").attr('class');
    if (jsonResponse.accountType == 'SYSTEM') {
      $("#grid_row_container").find("#row" + jsonResponse.param).find("#nav_icon").removeClass(className).addClass(
        'navicon accounts system');
    } else if (jsonResponse.accountType == 'RETAIL') {
      $("#grid_row_container").find("#row" + jsonResponse.param).find("#nav_icon").removeClass(className).addClass(
        'navicon accounts retail');

    } else if (jsonResponse.accountType == 'Corporate') {
      $("#grid_row_container").find("#row" + jsonResponse.param).find("#nav_icon").removeClass(className).addClass(
        'navicon accounts corporate');
    } else if (jsonResponse.accountType == 'Trial') {

      $("#grid_row_container").find("#row" + jsonResponse.param).find("#nav_icon").removeClass(className).addClass(
        'navicon accounts trial');

    } else if (jsonResponse.accountType != 'SYSTEM' && jsonResponse.accountType != 'RETAIL' && jsonResponse.accountType !=
      'Corporate' && jsonResponse.accountType != 'Trial') {
      $("#grid_row_container").find("#row" + jsonResponse.param).find("#nav_icon").removeClass(className).addClass(
        'navicon accounts default');

    }
    viewTenant($("#row" + jsonResponse.param));
  }
};

/**
 * Update tenant row after Editing Account Limits
 */
$.editAccountLimits = function(jsonResponse) {

  if (jsonResponse == null) {
    popUpDialogForAlerts("dialog_info", i18n.errors.tenants.editAccount.error);
  } else {
    $("#editTenantsLimitDiv").html("");
    var content = "";
    content = content + "<div class='db_gridbox_columns' style='width:35%;'>";
    content = content + "<div class='db_gridbox_celltitles'>";
    content = content + jsonResponse.name;
    content = content + "</div>";
    content = content + "</div>";
    content = content + "<div class='db_gridbox_columns' style='width:12%;'>";
    content = content + "<div class='db_gridbox_celltitles'>";
    content = content + i18nAccountType(jsonResponse.accountType);
    content = content + "</div>";
    content = content + "</div>";
    content = content + "<div class='db_gridbox_columns' style='width:12%;'>";
    content = content + "<div class='db_gridbox_celltitles'>";
    content = content + jsonResponse.accountId;
    content = content + "</div>";
    content = content + "</div>";
    content = content + "<div class='db_gridbox_columns' style='width:12%;'>";
    content = content + "<div class='db_gridbox_celltitles'>";
    var state;
    $.each(statesdictionary, function(key, value) {
      if (key == jsonResponse.state) {
        state = value;
      }
    });
    content = content + state;
    content = content + "</div>";
    content = content + "</div>";
    content = content + "<div class='db_gridbox_columns' style='width:29%;'>";
    content = content + "<div class='db_gridbox_celltitles'>";
    content = content + jsonResponse.username;
    content = content + "</div>";
    content = content + "</div>";
    $("#row" + jsonResponse.param).html(content);
    viewAccountLimits($("#row" + jsonResponse.param));
  }
};

function i18nAccountType(accountType) {
  var i18nAccountType = i18nAccountTypeDictionary[accountType];
  if (i18nAccountType == null) {
    return accountType;
  }
  return i18nAccountType;
}

$("#requestAccountTypeConversionLink").live("click", function(event) {
  changeAccounttype();
});



function changeAccounttype() {
  $("#generalInfoDiv").hide();
  $("#changeAccountTypeJsp").show();
  $("#accountTypeNameSelect").focus();
}

function refreshDivs(id) {
  $(id).each(function() {
    $(this).html("");
  });
}

function blockUI() {
  $('#blockScreen').css({
    opacity: 0.7,
    'width': $(document).width(),
    'height': $(document).height()
  });
  $('#blockScreen').show();
  $('#spinner').show();
}

function unBlockUI() {
  $('#blockScreen').hide();
  $('#spinner').hide();
}

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
$(document).ready(function() {

  var showReadOnlyEditCardAddressForm = function() {
    $("#showCreditCardDetailsDiv .read").show();
    $("#showCreditCardDetailsDiv .write").hide();
    $("#showCreditCardDetailsDiv div[id$=Error]").hide();
    if ($("#ccerrormessage").val() != null && $("#ccerrormessage").val() != "") {
      $("#creditcard_update_error").find("#p_message").html($("#ccerrormessage").val());
      $("#creditcard_update_error").show();
    } else {
      $("#creditcard_update_error").hide();
      $("#creditcard_update_error").find("#p_message").html('');
    }
    $("#showCreditCardDetailsDiv .errormsg").html('');
    return true;
  };

  var showReadOnlyEditCardDetailsForm = function() {
    $("#creditCardTypeDetailsDiv .read").show();
    $("#creditCardTypeDetailsDiv .mandatory_wrapper.withlabel").hide();
    $("#creditCardTypeDetailsDiv .write").hide();
    $("#creditCardTypeDetailsDiv div[id$=Error]").hide();
    if ($("#ccerrormessage").val() != null && $("#ccerrormessage").val() != "") {
      $("#creditcard_update_error").find("#p_message").html($("#ccerrormessage").val());
      $("#creditcard_update_error").show();
    } else {
      $("#creditcard_update_error").hide();
      $("#creditcard_update_error").find("#p_message").html('');
    }
    $("#allowSecondaryIdLi").hide();
    $("#syncAddressli").hide();
    if ($("#enableEdit").val() != null) {
      $("#enableEdit").val("false");
    }
    $("#creditCardTypeDetailsDiv .errormsg").html('');
    $(".cc_icon").removeClass("active").addClass("nonactive");
    $("#" + $("#defaultCreditCardNameHidden").val()).removeClass("nonactive").addClass("active");
    $("#creditCard\\.creditCardType").val($("#defaultCreditCardNameHidden").val());
    return true;
  };
  var showReadOnlyEditTenantForm = function() {
    $("#editCurrentTenant .read").show();
    $("#editCurrentTenant .write").hide();
    $("#editCurrentTenant div[id$=Error]").hide();
    return true;
  };

  var showReadWriteEditCardAddressForm = function() {
    $("#showCreditCardDetailsDiv div[id$=Error]").show();
    $("#showCreditCardDetailsDiv .read").hide();
    $("#showCreditCardDetailsDiv .write").show();
    return true;
  };

  var showReadWriteEditCardDetailsForm = function() {
    $("#creditCardTypeDetailsDiv div[id$=Error]").show();
    $("#creditCardTypeDetailsDiv .read").hide();
    $("#creditCardTypeDetailsDiv .mandatory_wrapper.withlabel").show();
    $("#creditCardTypeDetailsDiv .write").show();
    if ($("#allowSecondaryCheckBox").val() == "true") {
      $("#allowSecondaryIdLi").show();
    }
    $("#syncAddressli").show();
    $("#enableEdit").val("true");
    $(".cc_icon").removeClass("active").addClass("nonactive");
    $("#" + $("#defaultCreditCardNameHidden").val()).removeClass("nonactive").addClass("active");
    $("#creditCard\\.creditCardType").val($("#defaultCreditCardNameHidden").val());
    return true;
  };


  var showReadWriteEditTenantForm = function() {
    $("#editCurrentTenant div[id$=Error]").show();
    $("#editCurrentTenant .read").hide();
    $("#editCurrentTenant .write").show();
    return true;
  };

  showReadOnlyEditTenantForm();
  showReadOnlyEditCardAddressForm();
  showReadOnlyEditCardDetailsForm();

  $("#edittenant a").click(function() {
    $(".error").removeClass("error");
    $("#editCurrentTenant div[id$=Error]").html('');
    $("#editCurrentTenant .write").each(function(index, element) {
      var readVal = $("#editCurrentTenant .read").eq(index).text().trim();
      if ($(this).is("input[type!=submit]")) {
        $(this).val(readVal);
      } else {
        $("input[type!=submit]", this).val(readVal);
      }
    });
    $("#editCurrentTenant #stateInput input").val($("#editCurrentTenant #stateInputReadHidden").text().trim());
    showReadWriteEditTenantForm();
  });


  $("#editCreditCardTypeDetails a").click(function() {
    $(".error").removeClass("error");
    $("#creditCardTypeDetailsDiv div[id$=Error]").html('');
    $("#creditCardTypeDetailsDiv .write").each(function(index, element) {
      var readVal = $("#creditCardTypeDetailsDiv .read").eq(index).text().trim();
      if ($(this).is("input[type!=submit]")) {
        $(this).val(readVal);
      } else {
        $("input[type!=submit]", this).val(readVal);
      }
    });
    $("#creditCardTypeDetailsDiv #stateSecondaryInput input").val($(
      "#creditCardTypeDetailsDiv #stateSecondaryInputReadHidden").text().trim());
    showReadWriteEditCardDetailsForm();
  });

  $("#editCreditCardAddress a").click(function() {
    $(".error").removeClass("error");
    $("#showCreditCardDetailsDiv div[id$=Error]").html('');
    $("#showCreditCardDetailsDiv .write").each(function(index, element) {
      var readVal = $("#showCreditCardDetailsDiv .read").eq(index).text().trim();
      if ($(this).is("input[type!=submit]")) {
        $(this).val(readVal);
      } else {
        $("input[type!=submit]", this).val(readVal);
      }
    });
    $("#showCreditCardDetailsDiv #billingStateInput input").val($(
      "#showCreditCardDetailsDiv #billingCCStateInputReadHidden").text().trim());
    showReadWriteEditCardAddressForm();
  });

  $("#editCreditCardDetailsCancel").click(function() {
    showReadOnlyEditCardDetailsForm();
  });

  $("#editCreditCardAddressCancel").click(function() {
    showReadOnlyEditCardAddressForm();
  });

  $("#edittenantcancel").click(function() {
    showReadOnlyEditTenantForm();
  });

  $("#verifyUserDiv").keypress(function(event) {
    if (event.keyCode == 13) {
      $("#verifyPassword").click();
    }
  });

  $("#verifyPassword").click(function() {
    var password = $("#password").val();
    $("#wrongPasswordError").hide();
    if (password.length == 0) {
      $("#wrongPasswordError").html(i18n.errors.tenants.passwordIsMandatory);
      $("#wrongPasswordError").show();
      $("#password").focus();
      return;
    }
    $.ajax({
      type: 'POST',
      url: "/portal/portal/tenants/get_api_details",
      data: {
        'password': password
      },
      dataType: "json",
      success: function(data) {
        if(data.userHasCloudServiceAccount != true){
          cleanCredentialsDiv();
          var $thisPanel = $("#verifyUserDiv");
          $thisPanel.dialog("close");
          popUpDialogForAlerts("dialog_info", i18n.errors.tenants.user.noServiceEnabledForApiCreds);
          return false;
          } 
        if (data.success == true) {
          $("#generalInfoDiv").hide();
          $("#creditCardDetailsDiv").hide();
          $("#changeAccountTypeJsp").hide();
          $("#billingdiv").hide();
          $("#historyDiv").hide();
          cleanCredentialsDiv();
          for (var i = 0; i < data.tenantCredentialList.length; i++) {
            var instance = data.tenantCredentialList[i];
            var apiCredentialsDivCloned = $("#apiCredentialsDiv").clone();
            var tenantCredentialLiCloned = null;
            $.each(instance, function(key, value) {
              if ((key == "ServiceName" || key == "ServiceUuid" || key == "InstanceName")) {
                // Service Name, Uuid and instance need not be shown in list as they are shown in LHS
                return;
              }
              tenantCredentialLiCloned = apiCredentialsDivCloned.find("#tenantCredentialLi").clone();
              tenantCredentialLiCloned.find('#tenantCredentialLabel').text(key);
              tenantCredentialLiCloned.find("#tenantCredentialLabel").attr("for", 'tenantCredentialLabel_' +
                key);
              tenantCredentialLiCloned.find("#tenantCredentialLabel").attr("id", 'tenantCredentialLabel_' + key);
              tenantCredentialLiCloned.find('#liCredentialValue').text(value);
              apiCredentialsDivCloned.find('#serviceLogo').html(
                '<img class="apikeyLogo" src=/portal/portal/logo/connector/' + instance.ServiceUuid + '/logo>');
              apiCredentialsDivCloned.find("#tenantCredentialUl").append(tenantCredentialLiCloned);
              tenantCredentialLiCloned.attr('id', 'tenantCredentialLi_' + key);

            });
            //var cls=apiCredentialsDivCloned.find('#titleDiv').attr('class');
            apiCredentialsDivCloned.find('#titleDiv').html('<h2>' + instance.ServiceName + '-' + instance.InstanceName +
              '</h2>');
            apiCredentialsDivCloned.find('#titleDiv').attr('style', 'margin-top:10px;');
            apiCredentialsDivCloned.find('#userCredentialLi').hide();
            apiCredentialsDivCloned.attr('id', 'apiCredentialsDiv_' + instance.ServiceUuid);
            apiCredentialsDivCloned.show();
            $("#userCredentialDiv").append(apiCredentialsDivCloned);
          }
          $(".secondlevel_menutabs").removeClass("on");
          $("#apiCredentials").addClass("on");
          $("#previous_tab").val("apicredentials");
          var $thisPanel = $("#verifyUserDiv");
          $thisPanel.dialog("close");

        } else {
          $("#wrongPasswordError").html(i18n.errors.tenants.wrongPassword);
          $("#wrongPasswordError").show();
          $("#password").val("");
        }
      },

      error: function(request) {

      },
      complete: function() {
        $("#password").val("");
      }
    });
  });

});

function onAccountMouseover(current) {
  if ($(current).hasClass('active')) return
  $(current).find("#info_bubble").show();
  return false;
}

function onAccountMouseout(current) {
  $(current).find("#info_bubble").hide();
  return false;
}

function details_tab_click(current) {

  $('#viewLimits_tab').removeClass('active').addClass("nonactive");
  $('#viewStateChanges_tab').removeClass('active').addClass("nonactive");
  $('#viewPendingChanges_tab').removeClass('active').addClass("nonactive");
  $(current).removeClass('nonactive').addClass("active");

  $('#tenantAccountLimitsDivOuter').hide();
  $('#viewStateChangesDiv').hide();
  $('#viewPendingChanges_details').hide();
  $('#details_div').show();

};

function closeChangeStateDialog(current) {
  var $thisPanel = $("#editTenantDiv");
  $thisPanel.dialog("close");
}

function closeIssueCreditDialog(current) {
  var $thisPanel = $("#issueCreditDiv");
  $thisPanel.dialog("close");
}

function closeChangeEditDialog(current) {
  var $thisPanel = $("#editTenantDiv");
  $thisPanel.dialog("close");
}

function closeAccountTypeControlsDialog() {
  $("#editTenantsLimitDiv").dialog("close");
}

function closePasswordDialog() {
  var $thisPanel = $("#verifyUserDiv");
  $thisPanel.dialog("close");
}

function cleanCredentialsDiv() {
  $("div[id*=apiCredentialsDiv_]").remove();
}

function gotoDashborad(current) {
  window.location = $(current).attr('id');
}

function filterAccounts(current) {
  var selectedfilter = $(current).attr('id');
  //var selectedfilter = document.getElementById('selectedAccountfilter').value;
  window.location = tenantPageUrl + "?filterBy=" + selectedfilter;
}

function cancelFilter() {
  $("#filterDropdownDiv").hide();

  //window.location=tenantPageUrl;
}
/**
 * Change account type.
 * @param current
 */

function changeAccountType1(current) {
  $(".j_accounttype").removeClass("active");
  $(".j_accounttype").find(".widget_radiobuttons").find("span").removeClass("checked");
  $(".j_accounttype").find(".widget_radiobuttons").find("span").addClass("unchecked");
  $(current).find("input:radio").attr("checked", true);
  $(current).addClass("active");
  $(current).find(".widget_radiobuttons").find("span").removeClass("unchecked");
  $(current).find(".widget_radiobuttons").find("span").addClass("checked");

  var accountTypeId = $("input:radio[name=accountTypeId]:checked").val();
  if (accountTypeId == "5") {
    trialAccount = true;
    $("#trialCodeBox").show();
  } else {
    trialAccount = false;
    $("#trialCodeBox").hide();
  }



  //do actions based on account type selection
  // donot show secondary address div regardless of account type
  /*
   if(accountTypeId != 4){
     $("#allowSecondaryLi").hide();
   }else{
     $("#allowSecondaryLi").show();
   }
   */
  //update confirmation wizard
  /*$step4.find("#confirmStep1").find(".j_description").text($("#accountType"+accountTypeId).find(".j_description").text());
   */
}

function editCurrentLogoCancel() {
  window.location.href = "/portal/portal/home";
}

function redirectToTenantsList() {
  var tenantParam = $("#step6").find("#tenantParam").val();
  if (tenantParam != undefined)
    window.location = tenantPageUrl + "?tenantParam=" + tenantParam;
  else
    window.location = tenantPageUrl;
}

//TODO: Move these function to a common js and refactor tenants and users js

function showTenantPasswordVerificationBox(callback) {
  $("#wrongPasswordError").hide();
  var $thisPanel = $("#verifyUserDiv");
  $thisPanel.dialog({
    height: 185,
    width: 700,
    modal: true
  });
  $thisPanel.dialog('option', 'buttons', {});
  $thisPanel.dialog("open");
  $("#password").focus();
}

function getFilteredCountriesForChannel(){
  var channelParam = $("#channelParam").val();
  $.ajax({
    type: 'GET',
    url: tenanturl + channelParam + "/filtered_countries/",
    dataType: 'json',
    async: false,
    success: function(countryList) {
      filteredCountries =  countryList;
    },
    error: function(request) {
    }
  });
}


function selectDefaultLocale(){
  var channelParam = $("#channelParam").val();
  $.ajax({
    type: 'GET',
    url: tenanturl + channelParam + "/default_locale/",
    dataType: 'text',
    async: false,
    success: function(locale) {
      $("#user\\.locale option[value=" + locale + "]").attr("selected","selected");
    },
    error: function(request) {
      
    }
  });
  getFilteredCountriesForChannel();
  var allCountries = $("#user\\.address\\.country");
  allCountries.empty();
  var html = '<option value="">'+i18n.label.choose+'</option>';
  
  for(var j = 0 ; j < filteredCountries.length ; j++){
    currentCountry = filteredCountries[j];
    html = html + '<option value="'+currentCountry.countryCode2+'">'+currentCountry.name+'</option>';
  }
  allCountries.html(html);
}

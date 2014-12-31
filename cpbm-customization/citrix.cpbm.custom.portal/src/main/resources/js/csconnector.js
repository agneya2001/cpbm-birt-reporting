/*
*  Copyright © 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/

var filledActiveCurrencies = new Array();

$(document).ready(function() {
	$("#manage_resources_iframe").bind("load", function(){
		hide_iframe_loading();
	});

  // Following line enables the popover of error message if account registratoin failed for certain services 
  $(".js_account_registration_error").popover();
  
  // Following block is responsible for auto refresh of those services on which account is still in provisioning state
  if($(".js_provisioning_service").length > 0) {
    $("body").everyTime(5000, "timerKey", function() {
      $(".js_provisioning_service").each(function() {
        var $element = $(this);
        $.ajax({
          type: "GET",
          url: "/portal/portal/connector/getHandleState",
          async: false,
          data : {
            tenant : effectiveTenantParam,
            serviceInstanceUUID : $element.attr("serviceInstanceUuid")
          },
          dataType: "html",
          success: function(handleState) {
        	  //if any dialog box is opened then it use to get closed after service activation because of force reload of the page
        	  // hence put the follwoing check as the page gets refreshed after closing of these dialog boxes
        	  // For more details refer DE7758 : Enable Service window gets closed,when another service get enabled.
            if(handleState != null && handleState != 'PROVISIONING' 
            	&& !($("#dialog_enable_service").dialog( "isOpen" )===true)
            	&& !($("#manage_user_provisioning_popup").dialog( "isOpen" )===true)
            	&& !($("#utilityrates_lightbox").dialog( "isOpen" )===true)){
              location.reload();
            }
          }, error: function() {
            // TODO
            // Ideally this should never be hit
          }
        });
      });
    }, 0);
  } else {
    $("body").stopTime("timerKey");
  }
  
  
  $(".subscibe_to_bundles_link").unbind("click").bind("click", function() {
    if(!isDelinquent){
      var serviceInstanceUUID = $(this).attr('id').substr(10);
      window.location = "/portal/portal/subscription/createsubscription?tenant=" + effectiveTenantParam +
        "&serviceInstanceUUID=" + serviceInstanceUUID;
    } else {
      if (showMakePaymentMessage != "") {
        popUpDialogForAlerts("dialog_info", showMakePaymentMessage);
        return;
      }
    }
  });
  
  
  
  $(".js_iframe_tabs").live("click", function() {
    if(isDelinquent){
      if(showMakePaymentMessage != ""){
        popUpDialogForAlerts("dialog_info", showMakePaymentMessage);
      }
      return;
    }
    $(".js_iframe_tabs").removeClass('on');
    $(this).addClass('on');
    var serviceInstanceUUID = $(this).attr('id').substr(11);
    if (serviceInstanceUUID == "all_services") {
      window.location = "/portal/portal/connector/csinstances?tenant=" + effectiveTenantParam;
    } else {
      launchMyResourcesWithServiceInstanceUUID(serviceInstanceUUID);
    }
  });
  if (typeof iframe_view != "undefined" && iframe_view && typeof service_instance_uuid_for_iframe != "undefined") {
    showResourcesIFrameWithServiceInstanceUUID(service_instance_uuid_for_iframe);
  }
  $("#backToServiceInstanceDetails").live("click", function(event) {
    $(".j_cloudservicepopup").hide();
    currentstep = "step1";
    $("#step1").show();
  });
  $("#backToaccountConfigurationDetails").live("click", function(event) {
    $(".j_cloudservicepopup").hide();
    currentstep = "stepOfAccountConfig";
    $("#stepOfAccountConfig").show();
  });
  $("#backToenableServiceForAllUsersDetails").live("click", function(event) {
    $(".j_cloudservicepopup").hide();
    currentstep = "stepOfEnableServiceUser";
    $("#stepOfEnableServiceUser").show();
  });

  $("#backToProductSelection").live("click", function(event) {
    $(".j_cloudservicepopup").hide();
    currentstep = "step3";
    $("#step3").show();
  });

  $("#backToProductCharges").live("click", function(event) {
    $(".j_cloudservicepopup").hide();
    currentstep = "step4";
    $("#step4").show();
  });


  $.validator.addClassRules("logorequired", {
    logorequired: true
  });
  $.validator
    .addMethod(
      "logorequired",
      function(value, element) {
        if (value == "") {
          return false;
        }
        return true;
      },
      dictionary.editImagePathInvalidMessage);
  $("#serviceInstanceLogoForm").validate({
    // debug : true,
    success: "valid",
    ignoreTitle: true,
    rules: {
      "logo": {
        required: true
      }
    },
    messages: {
      "logo": {
        required: dictionary.editImagePathInvalidMessage
      }
    },
    errorPlacement: function(error, element) {
      var name = element.attr('id');
      name = ReplaceAll(name, ".", "\\.");
      if (name != "") {
        error.appendTo("#" + name + "Error");
      }
    }
  });

  activateThirdMenuItem("l3_home_connectors_cs_tab");

  $(".cloud_button.active").bind("click", function(event) {
    var id = $(this).attr('id');
    var targetDiv = $('div.servicelist_extended[serviceid=' + id + ']');
    targetDiv.toggle();
    var statusLoadedForService = targetDiv.attr("statusLoadedForService");
    if(statusLoadedForService == null){
      statusLoadedForService = false;
    } else {
      statusLoadedForService = true;
    }
    if(targetDiv.css("display") != "none" && !statusLoadedForService){
      targetDiv.find("#service_instance_list li.reload").each(function(){
        reloadStatus($(this));
      });
      targetDiv.attr("statusLoadedForService", "true");
    }
  });

  $("a.filters").bind("click", function(event) {
    var category = $(this).attr('id');
    showSelectedCategory(category);
  });

  $(".servicedetails").bind("click", function(event) {
    initDialog("dialog_service_details", 720);
    var id = $(this).attr('id');
    $.ajax({
      type: "GET",
      url: connectorPath + "/" + type + "?id=" + id + "&action=view",
      dataType: "html",
      success: function(html) {
        var $thisDialog = $("#dialog_service_details");
        $thisDialog.html(html);
        $thisDialog.bind("dialogbeforeclose", function(event, ui) {
          $thisDialog.empty();
        });
        dialogButtonsLocalizer($thisDialog, {
          'OK': g_dictionary.dialogOK,
          'Cancel': g_dictionary.dialogCancel
        });
        $thisDialog.dialog('open');
      },
      error: function() {

      }
    });
  });

  $('.actionbutton').on('mouseenter', function() {
    $(this).parent().find("#action_menu").show();
    $(this).parent().find(".widget_moreactions").show();

  }).on('mouseleave', function() {
    $(this).parent().find(".widget_moreactions").hide();
  });


  $(".termsandconditions").bind("click", function(event) {
    var id = $(this).parents('.servicelist.mainbox').attr('serviceid');
    dialog_enable_service(id);
  });

  $("#tncAccept").live("click", function(event) {
    if ($(this).is(":checked")) {
      $("#tncAcceptError").text("");
    }
  });
  

  $("a.close_enable_service_wizard").live("click", function(event) {
    closeDialog();
  });
  $("a.close_enable_service_user_wizard").live("click", function(event) {
    closeEnableServiceDialog();
  });

  $("a.close_service_instance_wizard").live("click", function(event) {
    closeAddServiceInstanceDialog();
  });

  $("a.optional_settings").live("click", function(event) {
    $("#optional_settings_div").toggle();
  });

  $("a.close_edit_service_instance_wizard").live("click", function(event) {
    closeEditServiceInstanceDialog();
  });

  $("li.uploadLogo").live("click", function(event) {
    var id = $(this).parents('li').attr('serviceid');
    uploadServiceInstanceImageGet(id);
  });

  $("li.reload").live("click", function(event) {
      reloadStatus($(this));
  });

  $("li.edit").live("click", function(event) {
    var id = $(this).parents('li').attr('serviceid');
    filledActiveCurrencies = new Array();
    initDialog("dialog_edit_service_instance", 900);
    var actionurl = connectorPath + "/" + type + "?instanceId=" + id;
    $("#spinning_wheel").show();
    $.ajax({
      type: "GET",
      url: actionurl,
      dataType: 'html',
      success: function(html) {
        var $thisDialog = $("#dialog_edit_service_instance");
        $thisDialog.html(html);
        $thisDialog.bind("dialogbeforeclose", function(event, ui) {
          $thisDialog.empty();
        });
        $currentDialog = $thisDialog;
        dialogButtonsLocalizer($thisDialog, {
          'OK': g_dictionary.dialogOK,
          'Cancel': g_dictionary.dialogCancel
        });
        $currentDialog.dialog('open');
        $("#spinning_wheel").hide();
      },
      error: function(error) {
        $("#spinning_wheel").hide();
      }
    });
  });

  $(".add_button.active.add_service").live("click", function(event) {
    var id = $(this).attr('id');
    filledActiveCurrencies = new Array();
    initDialog("dialog_add_service_instance", 900);
    var actionurl = connectorPath + "/" + type + "?id=" + id;
    $("#spinning_wheel").show();
    $.ajax({
      type: "GET",
      url: actionurl,
      dataType: "html",
      success: function(html) {
        var $thisDialog = $("#dialog_add_service_instance");
        $thisDialog.html(html);
        $thisDialog.bind("dialogbeforeclose", function(event, ui) {
          $thisDialog.empty();
        });
        $currentDialog = $thisDialog;
        dialogButtonsLocalizer($thisDialog, {
          'OK': g_dictionary.dialogOK,
          'Cancel': g_dictionary.dialogCancel
        });
        $currentDialog.dialog('open');
        $("#spinning_wheel").hide();
      },
      error: function() {
        $("#spinning_wheel").hide();
      }
    });
  });

  $("li.widget_navigationlist").live("click", function(event) {
    var id = $(this).attr('id');
    var $currentStep = $(this).parents('div.j_cloudservicepopup');
    $currentStep.find('div.griddescriptionbox').hide();
    $currentStep.find('#profile_' + id).show();
    $currentStep.find("li.widget_navigationlist").removeClass("active");
    $(this).addClass("active");
  });
  $(".button_manage_service").live("click", showResourcesIFrame);
  
  $(".button_manage_user_provisioning").bind("click", function(event) {

    $("#final_step").hide();
    $("#_stepOfEnableServiceUser").show();
    var $manage_autoprovision_link = $(this);
    var auto_provision = $(this).attr('enabled');
    var $manageAutoProvision = $("#manage_user_provisioning_popup");
    $manageAutoProvision.find("#_currentServiceInstanceUUID").val($(this).attr('id'));

    $manageAutoProvision.dialog({
      width: 900,
      modal: true,
      resizable: false,
      autoOpen: false,
      buttons: {
        "OK": function() {
          $("#manage_user_provisioning_popup").find("#spinning_wheel").show();
          auto_provision = $("input[name=_enableAllUsers]:checked").val();
          var _currentServiceInstanceUUID = $(this).find("#_currentServiceInstanceUUID").val();
          var ajaxUrl = "/portal/portal/tenants/set_autoprovision";
          $.ajax({
            type : "POST",
            data : {
              "tenantparam" : effectiveTenantParam,
              "instanceUuid" : _currentServiceInstanceUUID,
              "enableAllUsers": auto_provision
            },
            url : ajaxUrl,
            success : function(data) {
              $("#manage_user_provisioning_popup").find("#spinning_wheel").hide();
              $(".ui-dialog-buttonpane button:contains('"+ g_dictionary.dialogOK +"')").hide();
              if (data.result == "SUCCESS") {
                $("a.button_manage_user_provisioning[id="+ _currentServiceInstanceUUID +"]").attr('enabled',auto_provision);
                if(auto_provision == "true")
                  $("#final_step").find("#successmessage").text(dictionary.autoProvisioningEnabled + " " + (dictionary._true));
                else
                  $("#final_step").find("#successmessage").text(dictionary.autoProvisioningEnabled + " " + (dictionary._false));
                $("#final_step").show();
                $("#_stepOfEnableServiceUser").hide();
              }
              else{
                $("#final_step").find("#successmessage").text(dictionary.autoProvisioningError);
              }
            },
            error : function(error) {
              $("#manage_user_provisioning_popup").find("#spinning_wheel").hide();
              $("#final_step").find("#successmessage").text(dictionary.autoProvisioningError);
            }
          });
        },
        "Close": function() {
          $manageAutoProvision.dialog("close");
        }
      }
    });
    dialogButtonsLocalizer($manageAutoProvision, {
      'OK': g_dictionary.dialogOK,
      'Close': g_dictionary.dialogClose
    });

    if(auto_provision == "true"){
      $("#manage_user_provisioning_popup").find("#enable_yes").attr('checked',true);
      $("#manage_user_provisioning_popup").find("#enable_no").attr('checked', false);
    }else{
      $("#manage_user_provisioning_popup").find("#enable_yes").attr('checked',false);
      $("#manage_user_provisioning_popup").find("#enable_no").attr('checked', true);
    }
    $manageAutoProvision.html($("#manage_user_provisioning_popup").html());
    $manageAutoProvision.dialog("open");
  });

  $(".utility_rates_link").unbind("click").bind("click", function() {
    var serviceInstanceUUID = $(this).attr('id').substr(7);
    viewUtilitRates(effectiveTenantParam, "utilityrates_lightbox", null, serviceInstanceUUID);
  });
  $(".subscibe_to_bundles_link").unbind("click").bind("click", function() {
    if(!isDelinquent){
      var serviceInstanceUUID = $(this).attr('id').substr(10);
      window.location = "/portal/portal/subscription/createsubscription?tenant=" + effectiveTenantParam +
      "&serviceInstanceUUID=" + serviceInstanceUUID;
    } else {
      if (showMakePaymentMessage != "") {
        popUpDialogForAlerts("dialog_info", showMakePaymentMessage);
        return;
      }
    }
  });

  $("#all_selected_usage_type").live("click", function(event) {
    var isChecked = $(event.target).attr("checked");
    var checkboxList = $("#step3").find("#productsList input:checkbox");
    checkboxList.each(function(idx, i) {
      var checkboxItem = $(i);
      if (isChecked == "checked") {
        checkboxItem.attr("checked", isChecked);
      } else {
        checkboxItem.prop("checked", false);
      }
    });
  });

  $(".learn_more_link").live("click", function(event) {
    var si_id = $(this).attr('id').substr(16);
    $(this).toggleClass("more_down");
    $(this).toggleClass("more_up");
    $("#stripped_content_" + si_id).toggle();
    $("#learn_more_content_" + si_id).toggle();
  });

  function uploadServiceInstanceImageGet(ID) {
    initDialog("dialog_upload_service_instance_image", 550);
    var actionurl = connectorPath + "/upload_logo";
    $.ajax({
      type: "GET",
      url: actionurl,
      data: {
        "Id": ID
      },
      async: false,
      cache: false,
      dataType: "html",
      success: function(html) {
        var $thisDialog = $("#dialog_upload_service_instance_image");
        $thisDialog.empty();
        $thisDialog.html(html);
        $thisDialog.dialog('option', 'buttons', {
          "OK": function() {
            if ($('#serviceInstanceLogoForm').valid()) {
              $('#serviceInstanceLogoForm').iframePostForm({
                iframeID: 'serviceInstanceLogoForm-iframe-post-form',
                dataType: 'html',
                post: function() {
                  $("#serviceInstanceLogoForm-iframe-post-form").hide();
                  return true;
                },
                complete: function(text) {
                  if (text == 'success') {
                    $thisDialog.dialog('close');
                    popUpDialogForAlerts("dialog_info", dictionary.imageUploadedSuccessfully,refreshUploadedLogoCallback);
                  } else {
                    $("#logoError").text(text);
                  }
                }
              });
              $('#serviceInstanceLogoForm').submit();
            }

          },
          "Cancel": function() {
            $("#dialog_upload_service_instance_image").empty();
            $thisDialog.dialog('close');
          }
        });
        dialogButtonsLocalizer($thisDialog, {
        	'OK': g_dictionary.dialogOK,
          'Cancel': g_dictionary.dialogCancel
        });
        $thisDialog.bind("dialogbeforeclose", function(event, ui) {
          $thisDialog.empty();
        });
        $thisDialog.dialog("open");
      },
      error: function() {}
    });
  }
});

function reloadStatus(reloadLink){
  var id = reloadLink.parents('li').attr('serviceid');
  var $currentRow = reloadLink.parents('li');
  $currentRow.find(".widget_loaderbox").show();
  $currentRow.find("#instance_icon").hide();
  $.ajax({
    type: "GET",
    url: connectorPath + "/status?id=" + id,
    async: true,
    dataType: 'json',
    global: false,
    success: function(running) {
      if (running) {
        $currentRow.find("#instance_icon").removeClass('stopped_listicon').addClass('running_listicon'); //remove existing class
      } else {
        $currentRow.find("#instance_icon").removeClass('running_listicon').addClass('stopped_listicon');
      }
      $currentRow.find(".widget_loaderbox").hide();
      $currentRow.find("#instance_icon").show();
    },
    error: function(error) {
      $currentRow.find("#instance_icon").removeClass('running_listicon').addClass('stopped_listicon');
      $currentRow.find("#instance_icon").show();
      $currentRow.find(".widget_loaderbox").hide();
    },
    complete: function(){
      //do nothing
    }
  });
}

var refreshUploadedLogoCallback = function refreshUploadedLogo(){
	location.reload();
}
function showSelectedCategory(category) {
  $('div.servicelist_extended').hide();
  if (category != "All") {
    $('div.servicelist.mainbox').hide();
    $('div.servicelist.mainbox[category=' + category + ']').show();
  } else {
    $("div.servicelist.mainbox").show();
  }
  $("#selectedcategory").val(category);
  $("a.filters").removeClass('selected');
  $('a.filters[id=' + category + ']').addClass('selected');
}

function dialog_enable_service(id) {
  initDialog("dialog_enable_service");
  var actionurl = connectorPath + "/enable_service?id=" + id;
  $("#spinning_wheel").show();
  $.ajax({
    type: "GET",
    url: actionurl,
    dataType: "html",
    success: function(html) {
      var $thisDialog = $("#dialog_enable_service");
      $thisDialog.dialog("option", {
        height: "auto",
        width: 785
      });
      $thisDialog.html(html);
      $thisDialog.bind("dialogbeforeclose", function(event, ui) {
        $thisDialog.empty();
      });
      $currentDialog = $thisDialog;
      dialogButtonsLocalizer($thisDialog, {
        'OK': g_dictionary.dialogOK,
        'Cancel': g_dictionary.dialogCancel
      });
      $currentDialog.dialog('open');
      $("#spinning_wheel").hide();
    },
    error: function() {
      $("#spinning_wheel").hide();
    }
  });
}


function goToNextStep(current) {
  var currentstep = $(current).parents(".j_cloudservicepopup").attr('id');
  var $currentstep = $("#" + currentstep);
  var nextstep = $currentstep.find("#nextstep").val();


  if (nextstep != "step4") {
    if (currentstep == "step1" && $("#tncAccept").is(':checked') == false) {
      $("#tncAcceptError").text(dictionary.tncAcceptMessage);
    } else {
      $currentstep.hide();
      $("#" + nextstep).show();
    }
  } else {
    //submit
    $("#spinning_wheel").show();
    var profiledetails = new Array();

    $('div[id^="profile_"]').each(function() {
      var roles = new Array();
      var profileid = $(this).attr('id').substr(8);
      $(this).find('input[id^="role_"]:checked').each(function() {
        var rolename = $(this).attr("id").substr(5); //Remove role_
        roles.push(rolename);
      });
      var profiledetail = new Object();
      profiledetail.profileid = profileid;
      profiledetail.roles = roles;
      profiledetails.push(profiledetail);
    });

    $.ajax({
      type: "POST",
      url: connectorPath + "/enable_service",
      data: {
        "profiledetails": JSON.stringify(profiledetails),
        "id": $("#serviceParam").val()
      },
      dataType: "text",
      success: function(status) {
        if (status == 'success') {
          $("#step4").show();
          $currentstep.hide();
        }
        $("#spinning_wheel").hide();
      },
      error: function(status) {
        $("#spinning_wheel").hide();
      }
    });
  }
}

function goToNextStepForTenant(current) {
  var currentstep = $(current).parents(".j_cloudservicepopup").attr('id');
  var $currentstep = $("#" + currentstep);
  var nextstep = $currentstep.find("#nextstep").val();
  var enableAllUsers = $("input[name=enableAllUsers]:checked").val();
  var serviceEnableForm = $(current).closest("form");
  $(serviceEnableForm).validate({
    ignoreTitle: true,
    errorPlacement: function(error, element) {
      var name = element.attr('id');
      name = ReplaceAll(name, ".", "\\.");
      if (name != "") {
        error.appendTo("#" + name + "Error");
      }
    }
  });

  var isValid = false;
  if (nextstep != "stepOfSubmitAndFinish") {
    if (currentstep == "stepOfTnc" && $("#tncAccept").is(':checked') == false) {
    	popUpDialogForAlerts("dialog_info", g_dictionary.youCanNotContinueUntilYouAcceptTheTermsAndConditions);
    	$currentstep.find("#tncAccept").focus();
        
      }
    else if (currentstep == "stepOfAccountConfig") {
      if (typeof accountConfigurationNext == 'function') { 
        isValid = accountConfigurationNext(); 
      }else{
         if($(serviceEnableForm).valid()){
           isValid = true;
         }
      }
      if(isValid == true) {
        var service_account_config_properties_list = $("#service_account_config_properties_list").val();
        var propNames = service_account_config_properties_list.split(',');
        for(var propIndex in propNames){
          var eleName = propNames[propIndex];
          if(eleName && typeof eleName =='string'){
            var inputBox = $("input[name='"+eleName+"']");
            if($(inputBox).attr('type')!='password'){
              $("#confirmAccountConfigurationDetails").find("#"+eleName).text($(inputBox).val());
            }else{
              $("#confirmAccountConfigurationDetails").find("#"+eleName).text("****");
            }
          }
        }
        for(var propIndex in propNames){
          var eleName = propNames[propIndex];
          if(eleName && typeof eleName =='string'){
            var inputBox = $("input[name="+eleName+"]:checked");
            if($(inputBox).attr('type')!='password'){
              $("#confirmAccountConfigurationDetails").find("#"+eleName).text($(inputBox).val());
            }else{
              $("#confirmAccountConfigurationDetails").find("#"+eleName).text("****");
            }
          }
        }
        $currentstep.hide();
        $("#" + nextstep).show();
      }
    }else if(currentstep == "stepOfEnableServiceUser"){
      var enableAllUsers = $("input[name=enableAllUsers]:checked").val();
      if(enableAllUsers == 'false'){
        $("#enableServiceForAllUsersDesc").text(dictionary.no);
      }else{
        $("#enableServiceForAllUsersDesc").text(dictionary.yes);
      }
      $currentstep.hide();
      $("#" + nextstep).show();
    }  
    else{
      $currentstep.hide();
      $("#" + nextstep).show();
    }
  } else {
    var propObject = new Object();
    $("#enableServiceButton").prop("disabled",true);
    $("#enableServiceButton").addClass("disabled");
    $("#prevButtonEnableServiceStep").prop("disabled",true);
    $("#prevButtonEnableServiceStep").addClass("disabled");
    
    var service_account_config_properties_list = $("#service_account_config_properties_list").val();
    var propNames = service_account_config_properties_list.split(',');
    for(var propIndex in propNames){
      var eleName = propNames[propIndex];
      if(eleName && typeof eleName =='string'){
        var inputBox = $("input[name='"+eleName+"']");
        if($(inputBox).attr('type')=='radio'){
          var eleValue = $("input[name="+eleName+"]:checked").val();
        }else{
          var eleValue = $("input[name="+eleName+"]").val();
        }
        propObject[eleName] = eleValue;
      }
    }
   
     var currentServiceInstanceUUID =$("#currentServiceInstanceUUID").val();
     var propConfigs = JSON.stringify(propObject);
     $("#stepOfReviewAndConfirm").find("#spinning_wheel").show();
      var $resultDisplayBanner = $("#validationError");
      
      var ajaxUrl = "/portal/portal/tenants/enable_service";
      $.ajax({
        type : "POST",
        data : {
          "tenantparam" : effectiveTenantParam,
          "instanceUuid" : currentServiceInstanceUUID,
          "instanceProperty" : propConfigs,
          "enableAllUsers":enableAllUsers
        },
        async: false,
        url : ajaxUrl,
        success : function(data) {
          $("#stepOfReviewAndConfirm").find("#spinning_wheel").hide();
          if (data.result == "SUCCESS") {
              $("#stepOfSubmitAndFinish").show();
              $currentstep.hide();   
          }
          else{
            $("#enableServiceButton").prop("disabled",false);
            $("#enableServiceButton").removeClass("disabled");
            $("#prevButtonEnableServiceStep").prop("disabled",false);
            $("#prevButtonEnableServiceStep").removeClass("disabled");
            $resultDisplayBanner.text(data.message);
            $resultDisplayBanner.parent("#serviceEnableError").show();
          }
        },
        error : function(error) {
          $("#stepOfReviewAndConfirm").find("#spinning_wheel").hide();
          $("#enableServiceButton").prop("disabled",false);
          $("#enableServiceButton").removeClass("disabled");
          $("#prevButtonEnableServiceStep").prop("disabled",false);
          $("#prevButtonEnableServiceStep").removeClass("disabled");
          $resultDisplayBanner.text(error.message);
          $resultDisplayBanner.text(i18n.errors.connector.createfailed);
          $resultDisplayBanner.parent("#serviceEnableError").show();
        }
      });
  }
}
function goToPreviousStepForTenant(current) {
  var currentstep = $(current).parents(".j_cloudservicepopup").attr('id');
  var $currentstep = $("#" + currentstep);
  var prevstep = $currentstep.find("#prevstep").val();

  if (prevstep != "") {
    $currentstep.hide();
    $("#" + prevstep).show();
  }
}
function goToPreviousStep(current) {
  var currentstep = $(current).parents(".j_cloudservicepopup").attr('id');
  var $currentstep = $("#" + currentstep);
  var prevstep = $currentstep.find("#prevstep").val();

  if (prevstep != "") {
    $currentstep.hide();
    $("#" + prevstep).show();
  }
}

function closeDialog() {
  $("#dialog_enable_service").dialog("close");
  window.location = "/portal/portal/connector/cs";
}
function closeEnableServiceDialog() {
  $("#dialog_enable_service_user").dialog("close");
  window.location = "/portal/portal/connector/csinstances?tenant="+effectiveTenantParam;
}

function closeAddServiceInstanceDialog() {
  $("#dialog_add_service_instance").dialog("close");
  window.location = "/portal/portal/connector/cs";
}

function closeEditServiceInstanceDialog() {
  $("#dialog_edit_service_instance").dialog("close");
  window.location = "/portal/portal/connector/cs";
}

function resolveViewForSettingFromServiceInstance(serviceInstanceUUID, currentTenantParam, serviceInstanceName) {
  if(isDelinquent){
    if(showMakePaymentMessage != ""){
      popUpDialogForAlerts("dialog_info", showMakePaymentMessage);
    }
    return;
  }

  

  initDialog("dialog_enable_service");
  var actionurl = "/portal/portal/connector/account_config_params/?serviceInstanceUUID=" + serviceInstanceUUID + "&tenant=" +
  effectiveTenantParam;
  $("#spinning_wheel").show();
  var $thisDialog = $("#dialog_enable_service");
  $.ajax({
    type: "GET",
    url: actionurl,
    dataType: "html",
    async: false,
    success: function(html) {
      $thisDialog.dialog("option", {
        height: "auto",
        width: 900
      });
      $thisDialog.html(html);
      $thisDialog.bind("dialogbeforeclose", function(event, ui) {
        $thisDialog.empty();
      });
      $currentDialog = $thisDialog;
      dialogButtonsLocalizer($thisDialog, {
        'OK': g_dictionary.dialogOK,
        'Cancel': g_dictionary.dialogCancel
      });
      $currentDialog.dialog('open');
      $("#spinning_wheel").hide();
    },
    error: function() {
      $("#spinning_wheel").hide();
    }
  });
  
  var $extra_usage_div = $thisDialog.find(".js_extra_usage_div");
  var html = "";
    html = populateUtilityRatesTable(effectiveTenantParam,serviceInstanceUUID);
    if(html != null) {
      $thisDialog.find("#utilityrate_table_bundle_details").html(html);
    } else {
      $("#table_load_spinning_wheel").hide();
      $("#utility_rates_message_box").addClass("alert-error").text(dictionary.noUtilityRateAvailable).show();
    }
    
}

function populateUtilityRatesTable(tenant, serviceInstanceUuid) {
    var currencyCode = $("#selectedcurrencytext").text();
    var returnHtml = null;
   $.ajax({
      type: "GET",
      async: false,
      url: "/portal/portal/subscription/utilityrates_table",
      data: {
        tenant: tenant,
        serviceInstanceUuid: serviceInstanceUuid,
        currencyCode: currencyCode
      },
      dataType: "html",
      cache: false,
      success: function(html) {
        $("#utilityrate_table").empty();
        $("#utilityrate_table").html(html);
        returnHtml = html
      }
    });
    return returnHtml;
  }

function resolveViewForSettingFromServiceInstance2(instanceUuid) {
  if(isDelinquent){
    if(showMakePaymentMessage != ""){
      popUpDialogForAlerts("dialog_info", showMakePaymentMessage);
    }
    return;
  }
  var $iframe_tab = $("#iframe_tab_" + instanceUuid);
  var actionurl = "/portal/portal/users/resolve_view_for_Settings?instanceUuid=" + instanceUuid;
  $("#full_page_spinning_wheel").show();
  
  var failureHandler = function(XMLHttpResponse) {
    $("#full_page_spinning_wheel").hide();
    popUpDialogForAlerts("dialog_info", g_dictionary.error_cloud_service_down);
  };
  
  $.ajax({
    type: "GET",
    url: actionurl,
    dataType: "json",
    success: function(json) {
      getStatus(instanceUuid, function() {
        $("#manage_services_info").hide();
        $("#myServicesDiv").hide();
        $(".left_filtermenu").hide();
        $("#userSubscribedServiceDetails").show();
        $("#full_page_spinning_wheel").hide();
        if (json != null && json.url != null) {
          $(".js_iframe_tabs").removeClass("on");
          $iframe_tab.addClass("on");
          $("#userOrAccountSettingsViewFrame").attr("src", json.url);
        }
      }, failureHandler);
    },
    error: function(e) {
      failureHandler(e);
    },
    complete: function() {
      
    }
  });
}

$("#backToSubscribedServiceListing").live("click", function(event) {
  $("#userOrAccountSettingsViewFrame").attr("src", "");
  $("#userSubscribedServiceDetails").hide();
  $(".js_iframe_tabs").removeClass("on");
  $("#iframe_tab_all_services").addClass("on");
  $("#manage_services_info").show();
  $("#myServicesDiv").show();
  $(".left_filtermenu").show();

});


function resolveViewForAccountSettingFromServiceInstance(instanceUuid, tenantParam, serviceInstanceName) {
  if(isDelinquent){
    if(showMakePaymentMessage != ""){
      popUpDialogForAlerts("dialog_info", showMakePaymentMessage);
    }
    return;
  }
  var actionurl = "/portal/portal/users/resolve_view_for_account_settings?instanceUuid=" + instanceUuid +
    "&tenantParam=" + tenantParam;
  $.ajax({
    type: "GET",
    url: actionurl,
    dataType: "json",
    success: function(json) {
      if (json != null && json.url != null) {
        $("#selectedInstanceH1").append(serviceInstanceName);
        $("#serviceAccountConfigDiv").show();
        $("#myServicesDiv").hide();
        $("#serviceAccountConfigViewFrame").attr("src", json.url);
      } else {
        popUpDialogForAlerts("dialog_info", dictionary.noSettingsFound);
      }
    },
    error: function(e) {
      // TODO pop up (?) message for no account specific settings are found
    }
  });
}

//Checks if the ServiceInstance/Product code is unique or not

function validate_code(event, input, codeType) {
  clearCodeError(input);
  
  if(codeType == "productCode" && !$(input).parent().parent().find('#selected_usage_type').is(':checked')) {
    return true;
  }
  
  var code = $(input).val().trim();
  if (input.defaultValue != null && input.defaultValue.trim() != "" && input.defaultValue.trim() == code) {
    return true;
  }
  var err_msg = "";
  if (code.length >= 255) {
    err_msg = dictionary.max_length_exceeded + " 64";
  }

  if(code.length == 0){
    err_msg = dictionary.product_code_empty;
  }
  
  if (code.length > 0 && !/^[a-zA-Z0-9_:\[\]-]+$/.test(code)) {
    err_msg = dictionary.code_invalid;
  }

  if (err_msg.trim().length > 0) {
    codeErrorPlacement(input, err_msg);
    return false;
  }
  var urlData = {};
  if (codeType == "serviceInstanceCode") {
    urlData = {
      "serviceInstanceCode": code
    };
  } else if (codeType == "productCode") {
    urlData = {
      "product.code": code
    };
  }
  var returnVal = false;
  $.ajax({
    type: "GET",
    url: "/portal/portal/products/validateCode",
    data: urlData,
    dataType: "html",
    async: false,
    cache: false,
    success: function(result) {
      if (result == "false") {
        codeErrorPlacement(input, dictionary.code_not_unique);
      } else {
        returnVal = true;
        clearCodeError(input);
      }
    },
    error: function(html) {
      codeErrorPlacement(input, html);
    }
  });
  return returnVal;
}

function validate_name(input) {
  clearCodeError(input);
  
  if(!$(input).parent().parent().find('#selected_usage_type').is(':checked')) {
    return true;
  }
  
  var code = $(input).val().trim();
  if (input.defaultValue != null && input.defaultValue.trim() != "" && input.defaultValue.trim() == code) {
    return true;
  }
  var err_msg = "";
  if (code.length >= 255) {
    err_msg = dictionary.max_length_exceeded + " 255";
  }

  if (code.length == 0) {
    err_msg = dictionary.product_name_invalid;
  }

  
  if (err_msg.trim().length > 0) {
    codeErrorPlacement(input, err_msg);
    return false;
  }
  return true;
}

function clearCodeError(element) {
  var name = $(element).attr('id');
  name = ReplaceAll(name, ".", "\\.");
  if (name != "") {
    $("#" + name + "Error").html("");
  }
}

function codeErrorPlacement(element, errmsg) {
  var name = $(element).attr('id');
  name = ReplaceAll(name, ".", "\\.");
  if (name != "") {
    $("#" + name + "Error").html('<label for="' + name + '" generated="true" class="error">' + errmsg + '</label>');
  }
}

function createInstance(nextstep) {
  $("#step5").find("#spinning_wheel").show();
  var uuid = $("#step5").find("#add_service_instance_next").attr('uuid');
  var action = $("#step5").find("#add_service_instance_next").attr('action');
  var configProperties = new Array();
  $('input[id^="configproperty"]').each(function() {
    var configProperty = new Object();
    configProperty.name = $(this).attr("name");
    configProperty.value = $(this).attr("value");
    configProperties.push(configProperty);
  });
  $('textarea[id^="configproperty"]').each(function() {
    var configProperty = new Object();
    configProperty.name = $(this).attr("name");
    configProperty.value = $(this).val();
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

  var quickProducts = new Array();
  var checkboxList = $("#productsList input:checked");
  checkboxList.each(function(idx, i) {
    var checkboxItem = $(i);
    var usageTypeName = checkboxItem.attr("name");
    var parentDiv = checkboxItem.parent().parent();
    var quickProduct = new Object();
    quickProduct.name = parentDiv.find("#product\\.name\\." + usageTypeName).val();
    quickProduct.code = parentDiv.find("#product\\.code\\." + usageTypeName).val().trim();
    quickProduct.scale = parentDiv.find("#product\\.scale\\." + usageTypeName).val();
    quickProduct.uom = parentDiv.find("#product\\.scale\\." + usageTypeName + " :selected").text().trim();
    quickProduct.category = parentDiv.find("#product\\.category\\." + usageTypeName).val();
    quickProduct.usageTypeId = checkboxItem.val();

    quickProduct.price = new Array();
    var activeCurrencies = $("#step4").find("#productItem\\." + usageTypeName).find(".j_pricerequired");
    activeCurrencies.each(function(idxx, index) {
      var price = new Object();
      var currency = $(index);
      price.currencyCode = currency.attr("id");
      price.currencyVal = currency.val();
      quickProduct.price.push(price);
    });
    quickProducts.push(quickProduct);

  });

  var $resultDisplayBanner = $("#validationError");
  $.ajax({
    type: "POST",
    url: connectorPath + "/create_instance",
    data: {
      "configProperties": JSON.stringify(configProperties),
      "quickProducts": JSON.stringify(quickProducts),
      "id": uuid,
      "action": action
    },
    dataType: "json",
    async: false,
    success: function(data) {
      $("#step5").find("#spinning_wheel").hide();

      if (data.validationResult == "SUCCESS") {
        if (data.result == "SUCCESS") {
          $resultDisplayBanner.css('color', 'green');
          if ($("#" + uuid).find(".add_button").attr('singleton') == "true") {
            $("#" + uuid).find(".add_button").removeClass("active").addClass("nonactive");
          }
          $(".j_cloudservicepopup").hide();
          $("#" + nextstep).show();
        }
        $resultDisplayBanner.text(data.message);
        $resultDisplayBanner.parent("#serviceInstanceError").show();
      } else {
        $resultDisplayBanner.text(data.validationResult);
        $resultDisplayBanner.parent("#serviceInstanceError").show();
      }
    },
    error: function(data) {
      $resultDisplayBanner.text(i18n.errors.connector.createfailed);
      $resultDisplayBanner.parent("#serviceInstanceError").show();
      $("#step5").find("#spinning_wheel").hide();
    }
  });
}

function addServiceInstancePrevious(current) {
  var currentstep = $(current).parents(".j_cloudservicepopup").attr('id');
  var $currentstep = $("#" + currentstep);
  if (currentstep == "step4") {
    var checkboxList = $("#productsList input:checked");
    filledActiveCurrencies = new Array();
    checkboxList.each(function(idx, i) {
      var checkboxItem = $(i);
      var usageTypeName = checkboxItem.attr("name");
      filledActiveCurrencies[usageTypeName] = new Array();
      var activeCurrencies = $("#step4").find("#productItem\\." + usageTypeName).find(".j_pricerequired");
      activeCurrencies.each(function(idxx, index) {
        var price = new Object();
        var currency = $(index);
        price.currencyCode = currency.attr("id");
        price.currencyVal = currency.val();
        filledActiveCurrencies[usageTypeName][price.currencyCode] = price.currencyVal;
      });
    });
  }
  if (currentstep == "step5") {
    $("#serviceInstanceError").hide();
    var checkboxList = $("#step3").find("#productsList input:checkbox");
    if (checkboxList.length == 0) {
      $(".j_cloudservicepopup").hide();
      $("#step2").show();
      return;
    }
  }

  var prevStep = $currentstep.find("#prevstep").val();
  if (prevStep != "") {
    $(".j_cloudservicepopup").hide();
    $("#" + prevStep).show();
  }
}

function addServiceInstanceNext(current) {

  var currentstep = $(current).parents(".j_cloudservicepopup").attr('id');
  var $step2 = $("#step2");
  var $step3 = $("#step3");
  var $step4 = $("#step4");
  var $step5 = $("#step5");
  var $step6 = $("#step6");
  var $currentstep = $("#" + currentstep);
  var nextstep = $currentstep.find("#nextstep").val();
  var serviceInstanceForm = $(current).closest("form");

  $(serviceInstanceForm).validate({
	  ignoreTitle: true,
    errorPlacement: function(error, element) {
      var name = element.attr('id');
      name = ReplaceAll(name, ".", "\\.");
      if (name != "") {
        error.appendTo("#" + name + "Error");
      }
    }
  });

  if (currentstep == "step3") {
    var checkboxList = $("#productsList input:checked");
    $step4.find("#productPriceDiv").find("#productPriceListDiv").empty();
    var isProductCodeValid = true;
    var isProductNameValid = true;
    var productCodeMap = new Array();
    checkboxList.each(function(idx, i) {
      var checkboxItem = $(i);
      var usageTypeName = checkboxItem.attr("name");
      var parentDiv = checkboxItem.parent().parent();
      var returnVal = validate_code(null, parentDiv.find("#product\\.code\\." + usageTypeName)[0], "productCode");
      if (!returnVal) {
        isProductCodeValid = false;
      }
      var prodCode = $(parentDiv.find("#product\\.code\\." + usageTypeName)[0]).val().trim();
      if (productCodeMap[prodCode] != undefined) {
        codeErrorPlacement(parentDiv.find("#product\\.code\\." + usageTypeName)[0], dictionary.code_not_unique);
        isProductCodeValid = false;
      } else {
        productCodeMap[prodCode] = "";
      }
      
      var returnVal = validate_name(parentDiv.find("#product\\.name\\." + usageTypeName));
      if (!returnVal) {
        isProductNameValid = false;
      }
      var selectedProductName = parentDiv.find("#product\\.name\\." + usageTypeName).val();
      var selectedUOM = parentDiv.find("#product\\.scale\\." + usageTypeName + " :selected").text().trim();
      var selectedCategory = parentDiv.find("#product\\.category\\." + usageTypeName + " :selected").text().trim();
      var selectedProductCode = parentDiv.find("#product\\.code\\." + usageTypeName).val();
      var productItem = $step4.find("#productPriceDiv").find("#productItem").clone();
      productItem.find("#selectedProductName").text(selectedProductName);
      productItem.find("#selectedUOM").text(selectedUOM);
      productItem.find("#selectedProductCategory").text(selectedCategory);
      productItem.find("#selectedProductCode").text(selectedProductCode);
      productItem.attr("id", "productItem." + usageTypeName);
      var usageTypeCurVals = filledActiveCurrencies[usageTypeName];
      if (usageTypeCurVals != undefined) {
        var activeCurrencies = productItem.find(".j_pricerequired");
        activeCurrencies.each(function(idxx, index) {
          var currency = $(index);
          var currencyCode = currency.attr("id");
          currency.val(usageTypeCurVals[currencyCode]);
        });
      }
      $step4.find("#productPriceDiv").find("#productPriceListDiv").append(productItem);
      productItem.show();
    });
    if (!isProductCodeValid || !isProductNameValid) {
      return;
    }
  }
  if ($(serviceInstanceForm).valid()) {
    if (currentstep == "step1") {
      var returnVal = validate_code(null, $("#configproperty_instance_code")[0], "serviceInstanceCode");
      if (!returnVal)
        return;

      if ($("#isOptionalFieldAvailable").val() == "true") {
        $step2.find("#optionalSettings").show();
      }

      $step5.find("#confirmServiceInstanceDetails").find("#name").text($("#configproperty_instance_name").val());
      $step5.find("#confirmServiceInstanceDetails").find("#name").attr("title", $("#configproperty_instance_name").val());
      $step5.find("#confirmServiceInstanceDetails").find("#code").text($("#configproperty_instance_code").val());
      $step5.find("#confirmServiceInstanceDetails").find("#code").attr("title", $("#configproperty_instance_code").val());
      $step5.find("#confirmServiceInstanceDetails").find("#service_description").text($(
        "#configproperty_instance_description").val());
      $step5.find("#confirmServiceInstanceDetails").find("#service_description").attr("title", $(
        "#configproperty_instance_description").val());

      var serviceInstanceName = $("#configproperty_instance_name").val();
      var serviceInstanceNameToDisplay = "<br>";
      var size = serviceInstanceName.length;
      var maxsize = 50;
      var count = 0;
      while (size > 50) {
        serviceInstanceNameToDisplay += serviceInstanceName.substring(count, count + maxsize) + "<br>";
        count = count + maxsize;
        size = size - 50;
      }
      serviceInstanceNameToDisplay += serviceInstanceName.substring(count) + "<br>";
      $step6.find("#successmessage").append(serviceInstanceNameToDisplay);
    }
    if (currentstep == "step2") {
      var checkboxList = $step3.find("#productsList input:checkbox");
      if (checkboxList.length == 0) {
        $(".j_cloudservicepopup").hide();
        $step5.find("#confirmProductDetails").hide();
        $step5.find("#confirmCharges").hide();
        $step5.show();
        return;
      }
      checkboxList.each(function(idx, i) {
        var checkboxItem = $(i);
        var usageTypeName = checkboxItem.attr("name");
        var parentDiv = checkboxItem.parent().parent();
        if(parentDiv.find("#product\\.code\\." + usageTypeName).val() == undefined
        		|| parentDiv.find("#product\\.code\\." + usageTypeName).val() == null
        		|| parentDiv.find("#product\\.code\\." + usageTypeName).val() == ''){
        	parentDiv.find("#product\\.code\\." + usageTypeName).val($("#configproperty_instance_code").val() + "_" +
        			usageTypeName);
      }
      });
    }
    if (currentstep == "step3") {
        $(".j_cloudservicepopup").hide();
        $step4.show();
        $(function () {
          $(".js_product_details_popover").popover({
            trigger: "hover",
            html: true,
            content: function () {
              $productPriceItem = $(this).parent().parent();
              return $productPriceItem.find(".js_info_popover").html();
            }
          });
        });
        return;
      }
    
    var inError = false;
    if (currentstep == "step4") {
      var checkboxList = $("#productsList input:checked");
      filledActiveCurrencies = new Array();
      checkboxList.each(function(idx, i) {
        var checkboxItem = $(i);
        var usageTypeName = checkboxItem.attr("name");
        filledActiveCurrencies[usageTypeName] = new Array();
        var activeCurrencies = $("#step4").find("#productItem\\." + usageTypeName).find(".j_pricerequired");
        activeCurrencies.each(function(idxx, index) {
          var price = new Object();
          var currency = $(index);
          price.currencyCode = currency.attr("id");
          price.currencyVal = currency.val();
          if (!isValidNonNegativeNo(currency.val())) {
            $("#step4").find("#priceError").html(i18n.errors.config.validPriceRequired);
            $(this).addClass("error");
            $(this).css("background-color", "#FFE4E4");
            $("#step4").find("#error_div").show();
            inError = true;
            return;
          }
          if (!isValidCurrencyPrecision(currency.val())) {
            $("#step4").find("#priceError").html(i18n.errors.config.max_four_decimal_value);
            $(this).addClass("error");
            $(this).css("background-color", "#FFE4E4");
            $("#step4").find("#error_div").show();
            inError = true;
            return;
          }
          $(this).removeClass("error");
          filledActiveCurrencies[usageTypeName][price.currencyCode] = price.currencyVal;
        });
        if(!inError){
          $currentstep.find("#error_div").hide();
        }
      });
    }
    
    if ((currentstep == "step5")) {
      //call submit
      createInstance(nextstep);
    } else if (currentstep == "step6") {
      $currentDialog.dialog("close");
      $("#dialog_add_service_instance").find(".dialog_formcontent").empty();
      $("#dialog_edit_service_instance").find(".dialog_formcontent").empty();
      window.location = "/portal/portal/connector/cs";
    } else {
      if(!inError){
        $(".j_cloudservicepopup").hide();
        $("#" + nextstep).show();
      }
    }
  }
}

function showHideUnmaskedField(show_unmasked_link) {
  var selected_field_id = $(show_unmasked_link).attr("id").replace("_show_unmasked", "");
  var masked_field = $("#" + selected_field_id).get(0);
  if ($(show_unmasked_link).attr('disabled') == 'disabled') {
    return;
  }
  if (masked_field.getAttribute('type') == 'text') {
    masked_field.setAttribute('type', 'password');
    $(show_unmasked_link).text(dictionary.viewMasked);
  } else {
    masked_field.setAttribute('type', 'text');
    $(show_unmasked_link).text(dictionary.hideMasked);
  }
}

function showHideUnmaskedLink(masked_field) {
  var value = $("#" + $(masked_field).attr("id")).val();
  var $link = $("#" + $(masked_field).attr("id") + "_show_unmasked");

  if (value != "") {
    $link.css({
      opacity: 1.0,
      visibility: "visible"
    });
    $link.attr('disabled', false);
  } else {
    $link.css({
      opacity: 0.5,
      visibility: "visible"
    });
    $link.attr('disabled', true);
  }
}

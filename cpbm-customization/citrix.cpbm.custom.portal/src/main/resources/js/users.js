/*
*  Copyright © 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/
$(document).ready(function() {
 
  $("#password_confirm").bind('paste',function(e) {
    e.preventDefault(); return false; 
  });
  
  var $selectedTab = null;

  if ($("#activeTabName").val() != null && $("#activeTabName").val() != "") {
    
    if ($("#activeTabName").val() == "emailAddresses") {
      $(".secondlevel_menutabs").removeClass("on");
      $("#emailAddresses").addClass("on");
      $("#profileDetailsDiv").hide();
      $("#loginDiv").hide();
      $("#sideLeftPanel").hide();
      $("#apiCredentialsDiv").hide();
      $("#bssApiCredentialsDiv").hide();
      $("#notPrefDiv").hide();
      $("#emailAddressesDiv").show();
      $("#myServicesDiv").hide();
    }
    if ($("#activeTabName").val() == "myServices") {
      $(".secondlevel_menutabs").removeClass("on");
      $("#myServices").addClass("on");
      $("#profileDetailsDiv").hide();
      $("#loginDiv").hide();
      $("#sideLeftPanel").hide();
      $("#apiCredentialsDiv").hide();
      $("#bssApiCredentialsDiv").hide();
      $("#notPrefDiv").hide();
      $("#emailAddressesDiv").hide();
      $("#myServicesDiv").hide();
    }

  }


  $("#addDeleteEmailLink").live("click", function(event) {
    $(".secondlevel_menutabs").removeClass("on");
    $("#emailAddresses").addClass("on");
    $("#profileDetailsDiv").hide();
    $("#loginDiv").hide();
    $("#sideLeftPanel").hide();
    $("#apiCredentialsDiv").hide();
    $("#bssApiCredentialsDiv").hide();
    $("#notPrefDiv").hide();
    $("#emailAddressesDiv").show();
    $("#myServicesDiv").hide();
  });


  $(".secondlevel_menutabs").live("click", function(event) {

    if ($(this).attr('id') != "apiCredentials") {
      $(".secondlevel_menutabs").removeClass("on");
      $(this).addClass("on");
      selectedTab = $(this).attr('id');
    }
    if ($(this).attr('id') == "apiCredentials") {
      var previousTab = $("#previous_tab").val();
      if (previousTab != "apicredentials") {
        showPasswordVerificationBox(null);
      }
    } else { //clean the cloned divs
      cleanCredentialsDiv();
    }

    if ($(this).attr('id') == "notPref") {
      $("#profileDetailsDiv").hide();
      $("#loginDiv").hide();
      $("#sideLeftPanel").hide();
      $("#apiCredentialsDiv").hide();
      $("#bssApiCredentialsDiv").hide();
      $("#emailAddressesDiv").hide();
      $("#myServicesDiv").hide();
      $("#notPrefDiv").show();
      $("#previous_tab").val("notpref");
    }
    if ($(this).attr('id') == "emailAddresses") {
      $("#profileDetailsDiv").hide();
      $("#loginDiv").hide();
      $("#sideLeftPanel").hide();
      $("#apiCredentialsDiv").hide();
      $("#bssApiCredentialsDiv").hide();
      $("#notPrefDiv").hide();
      $("#emailAddressesDiv").show();
      $("#myServicesDiv").hide();
      $("#previous_tab").val("emailAddresses");
    }
    if ($(this).attr('id') == "myServices") {
      $("#profileDetailsDiv").hide();
      $("#loginDiv").hide();
      $("#sideLeftPanel").hide();
      $("#apiCredentialsDiv").hide();
      $("#bssApiCredentialsDiv").hide();
      $("#notPrefDiv").hide();
      $("#emailAddressesDiv").hide();
      $("#myServicesDiv").show();
      $("#previous_tab").val("myServices");
    }

  });


  $(".edituser_link").live("click", function(event) {

    window.location = "/portal/portal/users/" + $('#user_param').val() + "/myprofile";
  });

  $(".myServicesDiv").click(function() {
    alert("Please write a ajax call in myService.click function of users.js");
  });

  $(".deleteUserAction").click(function() {
    var r = confirm(i18n.user.del);
    if (r == true) {
      return true;
    } else {
      return false;
    }
  });

  $(".removeUserAction").click(function() {
    var r = confirm(i18n.user.delproject);
    if (r == true) {
      return true;
    } else {
      return false;
    }
  });

  $(".addnewuser").click(function() {
    actionurl = '/portal/portal/users/new/step1?tenant=' + $(this).attr('id');


    $.ajax({
      type: "GET",
      url: actionurl,
      dataType: "html",
      success: function(html) {
        unBlockUI();
        $("#addnewuserDiv").html(html);
      },
      error: function() {
        unBlockUI();
        $(".addnewuser").unbind('click');
      }

    });
    $(".addnewuser").unbind('click');
  });

function generateUserApiKey(){
  var actionurl = '/portal/portal/users/generate_api_key';
    $.ajax({
      type: "POST",
      url: actionurl,
      dataType: "json",
      success: function(data) {
        if (data.success == true) {
          $("#bssApiKey").text(data.bssApiCredentials.apiKey);
          $("#bssSecretKey").text(data.bssApiCredentials.secretKey);
        }
      },
      error: function() {
      }
    });
}
  
$("#generateAPIKey").click(function(){
	popUpDialogForConfirmation("dialog_confirmation", i18n.user.generateAPIKey, generateUserApiKey);
});

  $("#addnewusercancel").click(function() {
    $("#addexistinguserDiv").html("");
    $("#addnewuserDiv").html("");
    $("#addprojectbutton").show();
  });

  $("#editmembershipcancel").click(function() {
    $(".editMemberStubDiv").html("");
    $(".editMemberDiv").html("");
  });



  $("#userMaxDiv").dialog({
    autoOpen: false,
    resizable: false,
    width: 400,
    height: 150,
    modal: true,
    title: i18n.user.max,
    buttons: {
      "OK": function() {
        $(this).dialog('close');
      }
    }
  });


  $("#subsribeAtmosDiv").dialog({
    autoOpen: true,
    width: 400,
    height: 150,
    modal: true,
    title: i18n.user.cloudstorage,
    buttons: {
      "OK": function() {
        $(window.location).attr('href', '/portal/portal/home');
        $(this).dialog('close');
      }
    }
  });
  $("#subsribeAtmosDiv").dialog('open');

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
    title: i18n.user.profile.edit,
    buttons: {
      "Cancel": function() {
        $(this).dialog('close');
      },
      "Save": function() {
        if (('#userForm').validate()) {
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
  jQuery.validator.addMethod('validateUsername',
    function(value, element) {
      return value.length > 0 && /^[a-zA-Z0-9_@\.-]+$/.test(value);
    }, i18n.user.validateUsername);

  var phoneNumberRequired = function() {
    if (typeof(PhoneNumberMandatory) != "undefined" && PhoneNumberMandatory == "true") {
      return true;
    } else {
      return false;
    }
  };

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
      i18n.user.spendlimit);

  $("#userForm").validate({
    //debug : true,
    success: "valid",
    ignoreTitle: true,
    ignore: [],
    rules: {
      "user.firstName": {
        required: true,
        minlength: 1,
        maxlength: 255,
        flname: true
      },
      "user.lastName": {
        required: true,
        minlength: 1,
        maxlength: 255,
        flname: true
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
      "user.username": {
        required: true,
        minlength: 5,
        maxlength: 255,
        validateUsername: true,
        remote: {
          url: '/portal/portal/users/validate_username'
        }
      },
      "user.clearPassword": {
        required: true,
        password: true,
        notEqualTo: "#username"
      },
      "clearPassword": {
        password: true,
        notEqualTo: "#username"
      },
      "password_confirm": {
        required: true,
        equalTo: "#user\\.clearPassword"
      },
      "clearPassword_confirm": {
        equalTo: "#clearPassword"
      },

      "userProfile": {
        required: true

      },
      "user.spendBudget": {
        twoDecimal: true

      },
      "phone": {
        required: phoneNumberRequired,
        phone: true
      },
      "countryCode": {
        required: phoneNumberRequired,
        countryCode: true
      },
      "userEnteredPhoneVerificationPin": {
        required: function() {
          if ($("#countryCode_readvalue").text().trim() == $("#countryCode").text() && $("#phone_readvalue").text()
            .trim() == $("#phone").val()) {
            return false;
          }
          return true;
        }
      }

    },
    messages: {

      "user.firstName": {
        required: i18n.user.firstname,
        flname: i18n.user.flnameValidationError
      },
      "user.lastName": {
        required: i18n.user.lastname,
        flname: i18n.user.flnameValidationError
      },
      "user.email": {
        required: i18n.user.email,
        email: i18n.user.emailformat
      },
      "confirmEmail": {
        required: i18n.user.confirmemail,
        email: i18n.user.emailformat,
        equalTo: i18n.user.emailmatch
      },
      "user.username": {
        required: i18n.user.username,
        minlength: i18n.user.minLengthUsername,
        validateUsername: i18n.user.validateUsername,
        remote: i18n.user.usernameexists
      },
      "user.clearPassword": {
        required: i18n.user.password,
        password: i18n.user.passwordValidationError,
        notEqualTo: i18n.user.passwordequsername
      },
      "password_confirm": {
        required: i18n.user.passwordconfirm,
        password: i18n.user.passwordValidationError,
        equalTo: i18n.user.passwordmatch
      },
      "clearPassword": {
        required: i18n.user.password,
        password: i18n.user.passwordValidationError,
        notEqualTo: i18n.user.passwordequsername
      },
      "clearPassword_confirm": {
        required: i18n.user.passwordconfirm,
        password: i18n.user.passwordValidationError,
        equalTo: i18n.user.passwordmatch
      },
      "userProfile": {
        required: i18n.user.profilerequired
      },
      "phone": {
        required: i18n.user.phone,
        phone: i18n.user.phoneValidationError
      },
      "countryCode": {
        required: i18n.errors.countryCode,
        countryCode: i18n.errors.countryCodeValidationError,
        number: i18n.errors.countryCodeNumber
      },
      "userEnteredPhoneVerificationPin": {
        required: i18n.errors.phonePin
      }
    },
    errorPlacement: function(error, element) {
      var name = element.attr('id');
      name = name.replace(/\./g, "\\.");
      if (error.html() != "") {
        error.appendTo("#" + name + "Error");
      }
    }
  });
  $("#userPasswordForm").validate({
    //debug : true,
    success: "valid",
    ignoreTitle: true,
    rules: {
      "user.clearPassword": {
        required: true,
        password: true,
        notEqualTo: "#username"
      },

      "password_confirm": {
        required: true,
        equalTo: "#user\\.clearPassword"
      },
      "user.oldPassword": {
        required: true,
        password: false
      }
    },
    messages: {
      "user.clearPassword": {
        required: i18n.user.password,
        password: i18n.user.passwordValidationError,
        notEqualTo: i18n.user.passwordequsername
      },
      "password_confirm": {
        required: i18n.user.passwordconfirm,
        password: i18n.user.passwordValidationError,
        equalTo: i18n.user.passwordmatch
      },
      "user.oldPassword": {
        required: g_dictionary.oldPasswordNullError
      }
    },
    errorPlacement: function(error, element) {
      var name = element.attr('id');
      name = name.replace(".", "\\.");
      if (error.html() != "") {
        error.appendTo("#" + name + "Error");
      }
    }
  });

  $("#editTimeZone").click(function() {
    $("#timeZone").val($("#showTimezone").text().trim());
    $("#editTimezoneSelect").show();
    $("#showTimezone").hide();
    $("#saveTimeZoneDiv").show();
    $("#editTimeZoneDiv").hide();
    $("#editLanguage").show();
    $("#showLanguage").hide();
  });
  $("#cancelTimeZoneEdit").click(function() {
    $("#editTimezoneSelect").hide();
    $("#showTimezone").show();
    $("#saveTimeZoneDiv").hide();
    $("#editTimeZoneDiv").show();
    $("#editLanguage").hide();
    $("#showLanguage").show();
  });
  $("#savePref").click(function() {
    $.ajax({
      type: "POST",
      url: "/portal/portal/users/edit_prefs?" + $("#timeZone").serialize() + "&locale=" + $("#locale").val(),
      dataType: "json",
      success: function(returnMap) {
        $("#showTimezone").html(returnMap.timeZone);
        $("#showTimezone").attr("title", returnMap.timeZone);
        $("#showLanguage").html(returnMap.locale);
        $("#showLanguage").attr("title", returnMap.locale);
        showDate = dateFormat(parseInt(returnMap.lastLogin), g_dictionary.jsDateFormat, true);
        $("#last_login_time").text(showDate);
        if (returnMap.localeChange == "true") {
          window.location.reload();
        }
      },
      error: function(returnMap) {
        return false;
      }
    });
    $("#editTimezoneSelect").hide();
    $("#showTimezone").show();
    $("#saveTimeZoneDiv").hide();
    $("#editTimeZoneDiv").show();
    $("#editLanguage").hide();
    $("#showLanguage").show();
  });


  setTimeout('$("#userForm input:visible:first").focus()', 1000);

  initDialog("dialog_confirmation", 350, false);
  var topActionMap = {

    deactivateuser: {
      label: dictionary.deactivateuser,
      inProcessText: dictionary.deactivatingUser,
      type: "POST",
      dataType: "json",
      afterActionSeccessFn: function(jsonObj) {
        $("#editUserDiv").html("");
        viewUser($("li[id^='row'].selected.users"));
        if (!jsonObj.enabled) {
          $("li[id^='row'].selected").find('#statusIcon').removeClass().addClass("widget_statusicon nostate");
        }
      }
    },

    activateuser: {
      label: dictionary.activateuser,
      inProcessText: dictionary.activatingUser,
      type: "POST",
      dataType: "json",
      afterActionSeccessFn: function(jsonObj) {
        $("#editUserDiv").html("");
        viewUser($("li[id^='row'].selected.users"));
        if (jsonObj.enabled) {
          $("li[id^='row'].selected").find('#statusIcon').removeClass().addClass("widget_statusicon running");
        }
      }
    },

    removeuser: {
      label: dictionary.removeuser,
      elementIdPrefix: "removeuser",
      inProcessText: dictionary.removingUser,
      type: "GET",
      dataType: "json",
      afterActionSeccessFn: function(resultObj) {
        $("li[id^='row'].selected.users").slideUp("slow", function() {
          clearDetailsPanel();
          $(this).remove();
          viewUser($("li[id^='row'].users").first().addClass("selected"));
        });
      }
    },

    resendverification: {
      label: dictionary.resendEmail,
      inProcessText: dictionary.resendingEmail,
      type: "GET",
      dataType: "html",
      afterActionSeccessFn: function(html) {
        //					$("#editUserDiv").html("");
        //					$("#viewUserDiv").html(html);
        $("#top_message_panel").find("#msg").text(i18n.user.versucc);
        $("#top_message_panel").addClass("success").show();
      }
    },

    requestPasswordReset: {
      label: g_dictionary.resetPasswordRequestSuccessful,
      type: "POST"
    }
  };


  function getConfirmationDialogButtons(command) {

    var buttonCallBacks = {};
    var actionMapItem;
    if (command == "removeuser") {
      actionMapItem = topActionMap.removeuser;
    }
    if (command == "activateuser") {
      actionMapItem = topActionMap.activateuser;
    }
    if (command == "deactivateuser") {
      actionMapItem = topActionMap.deactivateuser;
    }
    if (command == "resendverification") {
      actionMapItem = topActionMap.resendverification;
    }
    if (command == "requestPasswordReset") {
      actionMapItem = topActionMap.requestPasswordReset;
    }
    buttonCallBacks[dictionary.lightboxbuttonconfirm] = function() {
      $(this).dialog("close");

      var user_id = $('#user_param').val();

      var apiCommand;
      if (command == "removeuser") {

        apiCommand = usersUrl + user_id + "/delete";

      }
      if (command == "activateuser") {
        apiCommand = usersUrl + user_id + "/activate_user";
      }
      if (command == "deactivateuser") {
        apiCommand = usersUrl + user_id + "/deactivate_user";
      }
      if (command == "resendverification") {
        apiCommand = usersUrl + user_id + "/resendverification?action=ajax";
      }
      if (command == "requestPasswordReset") {
        apiCommand = "/portal/portal/users/" + $('#loggedInUserParam').val() + "/reset_password?action=ajax";
      }
      doActionButton(actionMapItem, apiCommand);

    };

    buttonCallBacks[dictionary.lightboxbuttoncancel] = function() {
      $(this).dialog("close");
    };

    return buttonCallBacks;
  }


  $(".removeuser_link").live("click", function(event) {
    $("#dialog_confirmation").html(dictionary.lightboxremoveuser).dialog('option', 'buttons',
      getConfirmationDialogButtons("removeuser")).dialog("open");
  });

  $(".activateuser_link").live("click", function(event) {
    $("#dialog_confirmation").html(dictionary.lightboxactivateuser).dialog('option', 'buttons',
      getConfirmationDialogButtons("activateuser")).dialog("open");
  });

  $(".deactivateuser_link").live("click", function(event) {
    $("#dialog_confirmation").html(dictionary.lightboxdeactivateuser).dialog('option', 'buttons',
      getConfirmationDialogButtons("deactivateuser")).dialog("open");
  });

  $(".resenduserverification_link").live("click", function(event) {
    $("#dialog_confirmation").html(dictionary.lightboxresendemail).dialog('option', 'buttons',
      getConfirmationDialogButtons("resendverification")).dialog("open");
  });

  $(".request_password_reset_link").live("click", function(event) {
    $("#dialog_confirmation").html(dictionary.lightboxrequestPasswordReset.replace("{0}", $("#username_hidden").val()))
      .dialog('option', 'buttons', getConfirmationDialogButtons("requestPasswordReset")).dialog("open");
  });

  viewUser($("li[id^='row'].selected.users"));

  if (typeof(showAddNewUserWizard) != "undefined" && showAddNewUserWizard == "true") {
    addNewUserButton();
  }

  $("#backToSubscribedServiceListing").bind("click", function(event) {
    $("#userSubscribedServiceList").show();
    $("#userSubscribedServiceDetails").hide();
    $("#userOrAccountSettingsViewFrame").attr("src", "");
  });

});

function refreshDivs(id) {
  $(id).each(function() {
    $(this).html("");
  });
}

/**
 * View User details
 * @param current
 * @return
 */

function viewUser(current) {

  //$("#top_message_panel").hide();
  var divId = $(current).attr('id');
  if (divId == null) return;
  var ID = divId.substr(3);
  resetGridRowStyle();
  var cls = $(current).attr('class');
  cls = cls + " selected active";
  $(current).attr('class', cls);
  var url = usersUrl + ID;
  $.ajax({
    type: "GET",
    url: url,
    data: {
      Id: ID
    },
    dataType: "html",
    async: false,
    success: function(html) {
      $("#editUserDiv").html("");
      $("#viewUserDiv").html(html);
      
      $("#viewUserDiv").find(".js_user_enable_service_error").popover();
      
      $("#enableServiceButton").hide();
      bindActionMenuContainers();
    },
    error: function() {
      //console.log("Error whilst viewing user by id");
    }
  });
}

/**
 * Validate Email
 * @param event
 * @param current
 * @return
 */

function validate_email(event, current, url) {
  var email = current.value;
  //var submit = $("#userForm").find("#profileDetailsSubmit");
  var submit = $("#profileDetailsSubmit");
  $(submit).attr("disabled", true);
  $.ajax({
    type: "GET",
    url: url,
    data: {
      email: email
    },
    dataType: "html",
    success: function(html) {
      if (html == "false") {
        $("#email_writevalueError").html(i18n.errors.isEmailBlacklisted);
      } else {
        if (!$("#email_writevalueError").html(""))
          $("#email_writevalueError").html("");
        $(submit).attr("disabled", false);
      }
    },
    error: function() {
      //console.log("Error whilst viewing user by id");
    }
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


var currentDetailView;

/**
 * Edit product (GET)
 */

function editUserGet(current) {
  var divId = $(current).attr('id');
  var ID = divId.substr(4);
  var actionurl = usersUrl + "edituser";
  $.ajax({
    type: "GET",
    url: actionurl,
    data: {
      Id: ID
    },
    dataType: "html",
    success: function(html) {
      currentDetailView = $("#viewproductDiv").html();
      $("#viewproductDiv").html("");
      $("#addnewproductcancel").click();
      $("#editproductDiv").html("");
      $("#editproductDiv").html(html);
    },
    error: function() {
      $(".editProduct").unbind('click');
    }
  });
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
 * Remove user (GET)
 */

function removeUser(userId) {
  var r = confirm(i18n.user.del);
  if (r != true) {
    return ;
  }
  var actionurl = usersUrl + userId + "/delete";
  $.ajax({
    type: "GET",
    url: actionurl,
    dataType: "text",
    success: function(data) {
      window.location = usersUrl + "listusersforaccount?tenant=" + tenantParam + "&secondLevel=true";
    }
  });
}
/**
 * Remove user (GET)
 */

function deActivateUser(current) {
  var r = confirm(i18n.user.deact);
  if (r == false) {
    return false;
  }
  var divId = $(current).attr('id');
  var ID = divId.substr(10);
  var actionurl = usersUrl + ID + "/deactivate?action=ajax";
  $.ajax({
    type: "GET",
    url: actionurl,
    dataType: "html",
    success: function(html) {
      $("#editUserDiv").html("");
      $("#viewUserDiv").html(html);
      location.reload();
    },
    error: function(jsonResponse) {
      popUpDialogForAlerts("dialog_info", i18n.user.deactfail + jsonResponse.responseText);
    }
  });
}

/**
 * Remove user (GET)
 */

function activateUser(current) {
  var r = confirm(i18n.user.act);
  if (r == false) {
    return false;
  }
  var divId = $(current).attr('id');
  var ID = divId.substr(8);
  var actionurl = usersUrl + ID + "/activate?action=ajax";
  $.ajax({
    type: "GET",
    url: actionurl,
    dataType: "html",
    success: function(html) {
      $("#editUserDiv").html("");
      $("#viewUserDiv").html(html);
      location.reload();
    },
    error: function(jsonResponse) {
      popUpDialogForAlerts("dialog_info", i18n.user.actfail + jsonResponse.responseText);
    }
  });
}

/**
 * Re-send email to user (GET)
 */

function resendVerificationEmail(current) {
  var r = confirm(i18n.user.ver);
  if (r == false) {
    return false;
  }
  var divId = $(current).attr('id');
  var ID = divId.substr(10);
  var actionurl = usersUrl + ID + "/resendverification?action=ajax";
  $.ajax({
    type: "GET",
    url: actionurl,
    dataType: "html",
    success: function(html) {
      //					$("#editUserDiv").html("");
      //					$("#viewUserDiv").html(html);
      $("#top_message_panel").find("#msg").text(i18n.user.versucc);
      $("#top_message_panel").addClass("success").show();
    },
    error: function(jsonResponse) {
      popUpDialogForAlerts("dialog_info", i18n.user.verfail + jsonResponse.responseText);
    }
  });
}

function addNewUserButton() {
  var maxUsersReached = $("#isUsersMaxReached").val();
  if (maxUsersReached == 'Y') {
    $("#userMaxDiv").dialog('open');
    return false;
  }
  initDialog("dialog_add_user", 785);
  var actionurl = addNewUserUrl;
  $.ajax({
    type: "GET",
    url: actionurl,
    dataType: "html",
    success: function(html) {
      var $adduserDialogDialog = $("#dialog_add_user");
      $adduserDialogDialog.dialog("option", {
        height: "auto",
        width: 785
      });
      var $dialogFormContent = $adduserDialogDialog.find(".dialog_formcontent");
      $dialogFormContent.html("");
      $dialogFormContent.html(html);
      //$adduserDialogDialog.show();
      $adduserDialogDialog.dialog('open');
      $("#add_user_form_step1 li input:first").focus();
    },
    error: function(xhr, ajaxOptions, thrownError) {
      var msg = xhr.responseText;
      $("#top_message_panel").find("#msg").html(msg); //xhr.responseText
      $("#top_message_panel").find("#status_icon").removeClass("successicon").addClass("erroricon");
      $("#top_message_panel").removeClass("success").addClass("error").show();
    }
  });

  return true;
}


function closeUserCreationWizard() {
  $("#dialog_add_user").dialog('destroy');
  window.location = "/portal/portal/users/listusersforaccount?tenant=" + $('#tenantId').val() + "&page=" + $(
    '#newUsersPageCount').val();
}

function destroyDialog() {
  $("#dialog_add_user").dialog('destroy');
}
var submit_finish = true;

function addNewUserButtonStep1Finish() {
  //initDialog("dialog_add_user", 800);
  if (!submit_finish) {
    return;
  }
  if ($("#add_user_form_step1").valid()) {
    var actionurl = step1FinishUrl;
    submit_finish = false;
    $("#inprogress_spinning_wheel_1").show();
    $.ajax({
      type: "post",
      url: actionurl,
      data: $("#add_user_form_step1").serialize(),
      dataType: "html",
      success: function(html) {
        var $thisDialog = $("#dialog_add_user");
        var $dialogFormContent = $thisDialog.find(".dialog_formcontent");
        $dialogFormContent.html("");
        $dialogFormContent.html(html);
        $currentDialog = $thisDialog;
        submit_finish = true; //$currentDialog.dialog('open');
        $("#inprogress_spinning_wheel_1").hide();
      },
      error: function() {
        $(".addnewuser").unbind('click');
        submit_finish = true;
        $("#inprogress_spinning_wheel_1").hide();
      }
    });
  }



  return true;
}


function importAdUser(current) {
  var usernameforad = $("#user\\.username").val();
  actionurl = '/portal/portal/users/importfromad.json?username=' + usernameforad;
  $.ajax({
    type: "GET",
    url: actionurl,
    dataType: "json",
    success: function(userdetails) {
      $("#submitButtonFinish").removeAttr('disabled');
      $("#ButtonEmail").removeAttr('disabled');
      $("#user\\.username").attr('readonly', 'true');
      $("#user\\.firstName").val(userdetails.firstName);
      $("#user\\.lastName").val(userdetails.lastName);
      $("#user\\.email").val(userdetails.email);
    },
    error: function(e) {
      popUpDialogForAlerts("dialog_info", e.responseText);
    }
  });
}


function addNewUserButtonStep1CustomEmail() {
  if ($("#add_user_form_step1").valid()) {
    $("input[id='submitButtonEmail']").val("CustomeEmail");
    var actionurl = step1FinishUrl;
    $.ajax({
      type: "POST",
      url: actionurl,
      data: $("#add_user_form_step1").serialize(),
      dataType: "html",
      success: function(html) {
        var $thisDialog = $("#dialog_add_user");
        var $dialogFormContent = $thisDialog.find(".dialog_formcontent");
        $dialogFormContent.html("");
        $dialogFormContent.html(html);
      },
      error: function(XMLHttpRequest) {
        $(".addnewuser").unbind('click');
      }
    });

  }
}



function addNewUserCustomEmailFinish() {
  var actionurl = customEmailFinishURL;
  $("#inprogress_spinning_wheel_2").show();
  $.ajax({
    type: "POST",
    url: actionurl,
    data: $("#add_user_form_step1").serialize(),
    dataType: "html",
    success: function(html) {
      var $thisDialog = $("#dialog_add_user");
      var $dialogFormContent = $thisDialog.find(".dialog_formcontent");
      $dialogFormContent.html("");
      $dialogFormContent.html(html);
      $("#inprogress_spinning_wheel_2").hide();
    },
    error: function(XMLHttpRequest) {
      $(".addnewuser").unbind('click');
      $("#inprogress_spinning_wheel_2").hide();
    }
  });
}





$(document).ready(function() {
  var showReadOnlyUserForm = function() {
    $("#userForm .read").show();
    $("#userForm .write").hide();
    $("#userForm div[id$=Error]").hide();
    $("#phoneVerify").hide();
    $("#verificationStatusSuccess").hide();
    return true;
  };
  var showReadWriteUserForm = function() {
    $("#userForm .write").show();
    $("#userForm .read").hide();
    $("#userForm div[id$=Error]").show();
    return true;
  };
  var showReadOnlyUserPaswwordForm = function() {
    $("#userPasswordForm .read").show();
    $("#userPasswordForm .write").hide();
    $("#userPasswordForm div[id$=Error]").hide();
    return true;
  };
  var showReadWriteUserPasswordForm = function() {
    $("#userPasswordForm div[id$=Error]").show();
    $("#userPasswordForm .read").hide();
    $("#userPasswordForm .write").show();
    return true;
  };
  showReadOnlyUserForm();
  showReadOnlyUserPaswwordForm();
  $("#editProfileDetails").click(function() {
    $("#hyphen").hide();
    $(".error").removeClass("error");
    $("#userForm div[id$=Error]").html('');
    $("#userForm .write").each(function(index, element) {
      var readVal = $("#userForm .read").eq(index).text().trim();
      if ($(this).is("input[type!=submit]")) {
        $(this).val(readVal);
      } else {
        $("input[type!=submit]", this).val(readVal);
      }
    });
    $("#phone").removeAttr("readonly");
    $("#country").removeAttr("disabled");
    showReadWriteUserForm();
  });
  $("#cancelSaveProfileDetails").click(function() {
    $("#hyphen").show();
    showReadOnlyUserForm();
  });
  $("#editPasswordForm").click(function() {
    showReadWriteUserPasswordForm();
  });
  $("#cancelSavePasswordForm").click(function() {
    $("#password_confirm").val("");
    $("#password_confirm").removeClass().addClass("text");
    $("#password_confirmError").text("");
    $("#user\\.clearPassword").val("");
    $("#user\\.clearPassword").removeClass().addClass("text");
    $("#user\\.clearPasswordError").text("");
    $("#user\\.oldPassword").val("");
    $("#user\\.oldPassword").removeClass().addClass("text");
    $("#user\\.oldPasswordError").text("");
    showReadOnlyUserPaswwordForm();
  });

  $("#countryCode").focusout(function() {
    if (!$("#userForm").validate().element("#phone")) {
      $("#phoneVerify").hide();
      return;
    }
    if ($(this).val() != $("#countryCode_readvalue").text().trim() || $("#phone").val() != $("#phone_readvalue").text()
      .trim()) {
      $("#phoneVerify").show();
      $("#verificationStatusSuccess").hide();
    } else {
      $("#phoneVerify").hide();
    }
  });
  $("#phone").focusout(function() {
    if (!$("#userForm").validate().element("#phone")) {
      $("#phoneVerify").hide();
      return;
    }
    if (typeof(phoneVerificationEnabled) != "undefined" && phoneVerificationEnabled == "true") {
      if ($(this).val() != $("#phone_readvalue").text().trim() || $("#countryCode").text() != $(
        "#countryCode_readvalue").text().trim()) {
        $("#phoneVerify").show();
      } else {
        $("#phoneVerify").hide();
      }
    } else {
      $("#phoneVerify").hide();
    }
  });


  $("#phoneVerify").click(function() {
    dialogButtonsLocalizer($("#phone-dialog-modal"), {'Done':g_dictionary.dialogDone});
    $("#phone-dialog-modal").dialog('open');
  });

  $(function() {
    $("#phone-dialog-modal").dialog({
      modal: true,
      resizable: false,
      autoOpen: false,
      width: 450,
      buttons: {
        "Done": function() {
          $("#userEnteredPhoneVerificationPin").val($("#phoneVerificationPin").val());
          var isPINVerified = $.verifyPIN();
          $("#userForm #userEnteredPhoneVerificationPin").valid();
          if (isPINVerified) {
            $(this).dialog("close");
            $("#userEnteredPhoneVerificationPinError").html("");
          } else {
            $('userForm').validate();
          }
        }
      },
      close: function() {
        $("#userEnteredPhoneVerificationPinError").html("");
      }
    });
  });

  $("#phoneVerificationCall").click(function() {
    var phoneNumber = $("#phone").val();
    var countryCode = $("#countryCode").text().trim();
    if (phoneNumber == "" || phoneNumber == null || countryCode == "" || countryCode == null) {
      popUpDialogForAlerts("dialog_info", i18n.errors.phoneDetails);
      return;
    }

    var cntryCode = getOnlyNosFromThePhoneNoString(countryCode);
    var phoneNo = getOnlyNosFromThePhoneNoString(phoneNumber);

    $("#phoneVerificationCall").html("<span class='call_icon'></span>" + i18n.labels.phoneVerificationCalling);
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
        popUpDialogForAlerts("dialog_info", i18n.errors.callFailed);
      },
      complete: function(xhr, status) {
        $("#phoneVerificationCall").html("<span class='call_icon'></span>" + i18n.labels.phoneVerificationCallMe);
      }
    });
  });

  $("#phoneVerificationSMS").click(function() {
    var phoneNumber = $("#phone").val();
    var countryCode = $("#countryCode").text().trim();
    if (phoneNumber == "" || phoneNumber == null || countryCode == "" || countryCode == null) {
      popUpDialogForAlerts("dialog_info", i18n.errors.phoneDetails);
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
        popUpDialogForAlerts("dialog_info", i18n.errors.textMessageFailed);
      },
      complete: function(xhr, status) {
        $("#phoneVerificationSMS").html("<span class='text_icon'></span>" + i18n.labels.phoneVerificationTextMe);
      }
    });
  });

  $.verifyPIN = function() {
    $("#verificationStatusSuccess").hide();
    $("#verificationStatusFailed").hide();
    var phoneNumber = $("#phone").val();
    var userEnteredPIN = $("#phoneVerificationPin").val();
    if (typeof(phoneNumber) == "undefined" || phoneNumber == "" || typeof(userEnteredPIN) == "undefined" ||
      userEnteredPIN == "") {
      $("#verificationStatusFailed").show();
      return false;
    }
    var phoneNo = getOnlyNosFromThePhoneNoString(phoneNumber);

    var phoneVerified = false;
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
          $("#phoneVerify").hide();
          phoneVerified = true;
          $("#phone").attr("readonly", "true");
          $("#country").attr("disabled", "true");
        } else {
          $("#verificationStatusFailed").show();
          phoneVerified = false;
        }
      },
      error: function(html) {
        $("#verificationStatusFailed").show();
        phoneVerified = false;
      }
    });
    return phoneVerified;
  };

  $("#country").change(function() {
    var countyCode = $("#country").val();
    $.ajax({
      type: "GET",
      url: "/portal/portal/users/ISD_code_by_country_code",
      data: {
        "countyCode": countyCode
      },
      dataType: "html",
      success: function(result) {
        $("#countryCode").text(result);
        $("#countryCodeFormValue").val(result);
      },
      error: function(html) {
        popUpDialogForAlerts("dialog_info", i18n.user.errors.isdCodeFetchFailed);
      },
      complete: function() {
        if (typeof(phoneVerificationEnabled) != "undefined" && phoneVerificationEnabled == "true") {
          if ($("#countryCode").text() != $("#countryCode_readvalue").text().trim() || $("#phone").val() != $(
            "#phone_readvalue").text().trim()) {
            $("#phoneVerify").show();
          } else {
            $("#phoneVerify").hide();
          }
        } else {
          $("#phoneVerify").hide();
        }
      }
    });
  });

  $("#verifyUserDiv").keypress(function(event) {
    if (event.keyCode == 13) {
      $("#verifyPassword").click();
    }
  });

  function verifyPasswordForApICredentials(password) {
    $.ajax({
      type: 'POST',
      url: "/portal/portal/users/verify_password;",
      data: {
        'password': password
      },
      dataType: "json",
      success: function(data) {
        if (data.success == true) {
          $("#profileDetailsDiv").hide();
          $("#loginDiv").hide();
          $("#sideLeftPanel").hide();
          $("#notPrefDiv").hide();
          $("#emailAddressesDiv").hide();
          $("#myServicesDiv").hide();
          cleanCredentialsDiv();
          if(data.bssApiCredentials !== null && typeof data.bssApiCredentials!="undefined"){
            $("#bssApiKey").text(data.bssApiCredentials.apiKey);
            $("#bssSecretKey").text(data.bssApiCredentials.secretKey);
            $("#bssEndpointURL").text(data.bssApiCredentials.bssApiEndpointURL);
            $("#bssApiCredentialsDiv").show();
          }
          for (var i = 0; i < data.userCredentialList.length; i++) {
            var instance = data.userCredentialList[i];
            var apiCredentialsDivCloned = $("#apiCredentialsDiv").clone();
            var userCredentialLiCloned = null;
            $.each(instance, function(key, value) {
              if ((key == "ServiceName" || key == "ServiceUuid" || key == "InstanceName")) {} else {
                userCredentialLiCloned = apiCredentialsDivCloned.find("#userCredentialLi").clone();
                userCredentialLiCloned.find('#userCredentialLable').text(key);
                userCredentialLiCloned.find("#userCredentialLable").attr("for", 'userCredentialLable_' + key);
                userCredentialLiCloned.find("#userCredentialLable").attr("id", 'userCredentialLable_' + key);
                userCredentialLiCloned.find('#liCredentialValue').text(value);
                apiCredentialsDivCloned.find('#serviceLogo').html(
                  '<img class="apikeyLogo" src=/portal/portal/logo/connector/' + instance.ServiceUuid + '/logo>');
                apiCredentialsDivCloned.find("#userCredentialUl").append(userCredentialLiCloned);
                userCredentialLiCloned.attr('id', 'userCredentialLi_' + key);
              }
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
          $("#wrongPasswordError").html(i18n.errors.wrongPassword);
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
  }

  $("#verifyPassword").click(function() {
    var password = $("#password").val();
    if (password.length == 0) {
      $("#wrongPasswordError").html(i18n.errors.passwordIsMandatory);
      $("#wrongPasswordError").show();
      $("#password").focus();
      return;
    }
    verifyPasswordForApICredentials(password);
  });


});


function clearDetailsPanel() {
  var $detailsContent = $('#viewUserDiv');

  $detailsContent.find("#username").text("");
  $detailsContent.find("#email").text("");
  $detailsContent.find("#status").text("");
  $detailsContent.find("#firstName").text("");
  $detailsContent.find("#lastName").text("");
  $detailsContent.find("#phone").text("");
  $detailsContent.find("#timezone").text("");
  $detailsContent.find("#profilename").text("");
  $detailsContent.find("#locale").text("");
  $detailsContent.find("#details_status_text").text("");
  $detailsContent.find("#details_status_icon").removeClass();
  $detailsContent.find("#details_profile_icon").removeClass();

  $("#top_actions").hide();
}


function showInfoBubble(current) {
  if ($(current).hasClass('active')) return
  $(current).find("#info_bubble").show();
  return false;
};

function hideInfoBubble(current) {
  $(current).find("#info_bubble").hide();
  return false;
};


function nextClick() {
  var $currentPage = $('#current_page').val();
  //window.location = "/portal/portal/tenants/alerts?tenant="+$('#tenantId').val()+"&page="+(parseInt($currentPage)+1);
  window.location = "/portal/portal/users/listusersforaccount?tenant=" + $('#tenantId').val() + "&page=" + (parseInt(
    $currentPage) + 1);
}

function previousClick() {
  var $currentPage = $('#current_page').val();
  //window.location = "/portal/portal/tenants/alerts?tenant="+$('#tenantId').val()+"&page="+(parseInt($currentPage)-1);
  window.location = "/portal/portal/users/listusersforaccount?tenant=" + $('#tenantId').val() + "&page=" + (parseInt(
    $currentPage) - 1);
}

function showPasswordVerificationBox(callback) {
  $("#wrongPasswordError").hide();
  var $thisPanel = $("#verifyUserDiv");
  $thisPanel.dialog({
    height: 200,
    width: 700,
    modal: true
  });
  $thisPanel.dialog('option', 'buttons', {});
  $thisPanel.dialog("open");
  $("#password").focus();
}

function closePasswordDialog() {
  var $thisPanel = $("#verifyUserDiv");
  $thisPanel.dialog("close");
}

function changePassword(event, form) {
  var act = "/portal/portal/users/changePassword?userParam=" + $('#loggedInUserParam').val();
  if ($("#userPasswordForm").valid()) {
    $.ajax({
      type: "POST",
      url: act,
      data: $(form).serialize(),
      dataType: "html",
      async: false,
      success: function(result) {
        if (result == "success") {
          $("#userPasswordForm .read").show();
          $("#userPasswordForm .write").hide();
          $("#userPasswordForm div[id$=Error]").hide();
        //  window.location.reload();
          updateTopMessagePanel(dictionary2.changePasswordSuccessful,result);
          $("#user\\.oldPassword").val("");
          $("#user\\.clearPassword").val("");
          $("#password_confirm").val("");
        }
        if (result == "failure") {
          $("#user\\.oldPassword").val("");
          $("#user\\.oldPassword").removeClass().addClass("text");
          $("#user\\.oldPasswordError").html(dictionary2.wrongPassword);
          $("#userPasswordForm .read").hide();
          $("#userPasswordForm .write").show();
        }
      },
      error: function(result) {
        initDialogWithOK("dialog_info", 350, false);
        $("#dialog_info").dialog("option", "height", 150);
        $("#dialog_info").text(dictionary2.changePasswordError).dialog("open");
        return false;
      }
    });
  }
}


function showUserService() {
  $("#details_content").hide();
  $('#details_tab').removeClass('active').addClass("nonactive");
  $("#viewServiceSubscriptionStatus_tab").removeClass('nonactive').addClass("active");
  $("#service_content").show();
  var instances = $("#service_content").find("input[type='checkbox']").not(":disabled");
  if(instances.length > 0){
    $("#enableServiceButton").show();
  }
}

function showUserDetails() {
  $('#details_tab').removeClass('nonactive').addClass("active");
  $("#viewServiceSubscriptionStatus_tab").removeClass('active').addClass("nonactive");
  $("#details_content").show();
  $("#enableServiceButton").hide();
  $("#service_content").hide();

}

function enableAllServiceForUser(userParam, currentTenantParam) {

  if ($("#userEnabled").val() == "false") {
    popUpDialogForAlerts("dialog_info", dictionary.userdisabled);
    return;
  }

  var instanceProperty = '{}';
  var instances = $("#service_content").find("input[type='checkbox']:checked")
      .not(":disabled");

  var instanceList = "";
  for (i = 0; i < instances.length; i++) {
    var id = instances[i].id;
    $("#service_content").find("input[id=" + id + "]").attr("disabled", true);
    $("#service_provisioning_" + id).show();
    $("#service_state_" + id).text(dictionary.provisioning);
    instanceList += id + ";";
  }

  if (instanceList.length == 0) {
    popUpDialogForAlerts("dialog_info", dictionary.selectservice);
    return;
  } else {
    var instances = $("#service_content").find("input[type='checkbox']").not(":disabled");
    if(instances.length = 0){
      $("#enableServiceButton").hide();
    }
  }

  var ajaxUrl = "/portal/portal/users/enable_services";
  $.ajax({
    type : "POST",
    data : {
      "tenantparam" : currentTenantParam,
      "userparam" : userParam,
      "instances" : instanceList
    },
    dataType : "json",
    url : ajaxUrl,
    success : function(serviceRegistrationStatus) {
      pollEnableStatus(userParam, currentTenantParam, instanceList);
    },
    error : function(status) {
      $("#enableServiceButton").show();
      popUpDialogForAlerts("dialog_info", dictionary.enableserviceerror);
    }
  });
}

function pollEnableStatus (userParam, currentTenantParam, instances)
{
  var ajaxUrl = "/portal/portal/users/enabled_services";
  $.ajax({
    type: "POST",
    data: {
      "tenantparam": currentTenantParam,
      "userparam": userParam,
      "instances" : instances
    },
    dataType: "json",
    url: ajaxUrl,
    success: function(serviceRegistrationStatus) {
      var atleastOneServiceNotProvisioned = false;
      $.each(serviceRegistrationStatus, function(key, value) {
        if (value.state == "PROVISIONING") {
          atleastOneServiceNotProvisioned = true;
        } else {
          $("#service_provisioning_" + key).hide();
          if(value.state != "ACTIVE"){
            $("#service_content").find("input[id="+key+"]").attr("disabled", false);
            var html = dictionary.provisioningerror +' (<a class="user_enable_service_error" data-toggle="popover" data-trigger="hover" data-placement="bottom" data-container="body" data-content="'
            + value.data +'">'+ g_dictionary.details +'</a>)';
            $("#service_state_" + key).html(html);
            $("#enableServiceButton").show();
            $(".user_enable_service_error").popover();
          }else{
            $("#service_state_" + key).text(dictionary.active);
          }
        }
      });
      if(atleastOneServiceNotProvisioned){
        setTimeout ("pollEnableStatus(\'" + userParam +"\',\'" + currentTenantParam + "\',\'" + instances +"\')", 2000);
      }
    },
    error: function(status) {
      
    }
  });
}

function closeThisDialog(dialogName) {
  var $thisPanel = $("#" + dialogName);
  $thisPanel.dialog("close");
}

function cleanCredentialsDiv() {
  $("div[id*=apiCredentialsDiv_]").remove();

}

function resolveViewForSettingFromServiceInstance(instanceUuid) {
  //console.log('...instance uuid...',instanceUuid);
  $("#userSubscribedServiceList").hide();
  $(".left_filtermenu").hide();
  $("#userSubscribedServiceDetails").show();
  //$("#userOrAccountSettingsViewFrame").attr("src", "http://www.espncricinfo.com/");
  var actionurl = "/portal/portal/users/resolve_view_for_Settings?instanceUuid=" + instanceUuid;
  $.ajax({
    type: "GET",
    url: actionurl,
    dataType: "json",
    success: function(json) {
      //console.log('....json....',json);
      if (json != null && json.url != null) {
        $("#userOrAccountSettingsViewFrame").attr("src", json.url);
      }
    },
    error: function(e) {
      //console.log(e.responseText);
    }
  });
}
$(document).ready(function() {
  $("a#profile_deactivateuser,a#profile_activateuser").bind("click", function(event) {
    var $this = $(this);
    var actionurl = $this.attr('callbackurl');
    var $postRequest = $.ajax({
      type: "POST",
      url: actionurl,
      dataType: "json",
      success: function() {
        window.location = window.location;
      },
      error: function(XMLHttprequest) {
        initDialogWithOK("dialog_info", 350, false);
        $("#dialog_info").dialog("option", "height", 150);
        var message = "";
        if ($this.attr("id") == "profile_deactivateuser") {
          message = i18n.user.deactfail;
        } else {
          message = i18n.user.actfail;
        }
        $("#dialog_info").text(message).dialog("open");
        return false;
      }
    });
  });
});

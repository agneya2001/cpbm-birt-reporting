/*
*  Copyright � 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/
$(document).ready(function() {

  currentPage = parseInt(currentPage);
  perPageValue = parseInt(perPageValue);
  notificationListLen = parseInt(notificationListLen);

  function nextClick(event) {

    $("#click_next").unbind("click", nextClick);
    $("#click_next").addClass("nonactive");

    currentPage = currentPage + 1;

    $("#click_previous").unbind("click").bind("click", previousClick);
    $("#click_previous").removeClass("nonactive");

    var selectedfilter = document.getElementById('selectedDatefilter').value;

    window.location = notificationUrl + "notifications?tenant=" + tenantParam + "&filterBy=" + selectedfilter +
      "&currentPage=" + currentPage;
  }

  function previousClick(event) {
    $("#click_previous").unbind("click", previousClick);
    $("#click_previous").addClass("nonactive");

    currentPage = currentPage - 1;

    $("#click_next").removeClass("nonactive");
    $("#click_next").unbind("click").bind("click", nextClick);

    var selectedfilter = document.getElementById('selectedDatefilter').value;

    window.location = notificationUrl + "notifications?tenant=" + tenantParam + "&filterBy=" + selectedfilter +
      "&currentPage=" + currentPage;
  }

  if (currentPage > 1) {
    $("#click_previous").removeClass("nonactive");
    $("#click_previous").unbind("click").bind("click", previousClick);
  }

  if (notificationListLen < perPageValue) {
    $("#click_next").unbind("click");
    $("#click_next").addClass("nonactive");

  } else if (notificationListLen == perPageValue) {

    if (currentPage < totalpages) {

      $("#click_next").removeClass("nonactive");
      $("#click_next").unbind("click").bind("click", nextClick);
    } else {
      $("#click_next").unbind("click");
      $("#click_next").addClass("nonactive");
    }
  }

  function validate_email(newemail) {
    var apos = newemail.indexOf("@");
    var dotpos = newemail.lastIndexOf(".");
    if (apos < 1 || dotpos - apos < 2) {
      return false;
    } else {
      return true;
    }
  }


  $("#userAlertEmailForm").validate({
    //debug : false,
    success: "valid",
    ignoreTitle: true,
    rules: {
      email: {
        required: true,
        maxlength: 100,
        email: true,
      }
    },
    messages: {
      email: {
        required: i18n.errors.addsecAlert.requiredEmail,
        maxlength: i18n.errors.addsecAlert.longEmail,
        email: i18n.errors.addsecAlert.validEmail
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

  $("#addsecalert").click(function() {
    var $form = $('#userAlertEmailForm');
    var email = $form.find("#email").val();
    if ($form.valid()) {
      $.ajax({
        type: 'POST',
        url: $form.attr('action'),
        data: {
          email: email
        },
        dataType: 'json',
        success: function(data) {
          addAlertDelivOpt(function() {
                $("#emailSuccessMessage").hide();
                $("#emailErrorMessage").hide();
                $("#emailSuccessMessage").html(i18n.confirm.addsecAlert.verificationEmail.replace("{0}", email));
                $("#additional_emailError").html("");
                $("#emailSuccessMessage").show();
            });
      },
        error: function(request) {
          $("#emailSuccessMessage").hide();
          $("#emailErrorMessage").hide();
          var errorMessage = jQuery.parseJSON(request.responseText);
          $("#emailErrorMessage").html(errorMessage.Error.email.Message);
          $("#emailErrorMessage").show();
        }
      });
    }
    $(this).attr('value', i18n.label.addsecAlert.addEmail);
  });

  $(".deleteAlertPref").click(function() {
    var id = $(this).attr('name');
    var buttonCallBacks = {};
    buttonCallBacks[dictionary.lightboxbuttonconfirm] = function() {
      $(this).dialog("close");
      $.ajax({
        type: 'POST',
        url: "/portal/portal/tenants/alert_prefs/" + id + "/delete",
        data: {},
        dataType: 'text',
        success: function(data) {
          if (data == "success") {
            $("#emailSuccessMessage").html(i18n.confirm.addsecAlert.deleteEmail);
            $("#additional_emailError").html("");
            $("#emailSuccessMessage").show();
            $("#div" + id).html("");
            $("#div" + id).hide();
            $("#notPrefEmailReadOnlyDivLi" + id).html("");
            $("#notPrefEmailReadOnlyDivLi" + id).hide();
            $("#addEmailTextDiv").show();
            $("#addsecalert").show();
          } else {
            $("#emailErrorMessage").html(i18n.errors.addsecAlert.failDeleteEmail);
            $("#emailErrorMessage").show();
          }
        },

        error: function(request) {
          alert(i18n.errors.addsecAlert.failDeleteEmail);
        }
      });
    };
    buttonCallBacks[dictionary.lightboxbuttoncancel] = function() {
      $(this).dialog("close");
    };
    $("#dialog_confirmation").text(dictionary.deleteconfirm).dialog('option', 'buttons', buttonCallBacks).dialog(
      "open");
  });


  $(".makePrimary").click(function() {
    var effectiveuserparam = $("#effectiveUserParam").val();
    var id = $(this).attr('name');
    var buttonCallBacks = {};
    buttonCallBacks[dictionary.lightboxbuttonconfirm] = function() {
      $(this).dialog("close");
      $.ajax({
        type: 'POST',
        url: "/portal/portal/tenants/change_primary_email",
        data: {
          'newEmail': id,
          'param': tenantParam,
          'userParam': effectiveuserparam
        },
        dataType: "json",
        success: function(data) {
        
          if (data["status"] != "failure") {
            $("#emailSuccessMessage").html(i18n.confirm.addsecAlert.makePrimary);
            $("#additional_emailError").html("");
            $("#emailSuccessMessage").show();
            $("#email-" + id).text(data["email"]);
            var $divId = $("#div" + id); 
            $divId.find("#emailVerifiedIconDiv").hide();
            $divId.find("#emailVerifiedIconDivTemplate").show();
            if(data["isVerified"] == "true"){
              $divId.find("#unverifyIcon").hide();
              $divId.find("#unverifyText").hide();
              $divId.find("#verifyIcon").show();
              $divId.find("#verifyText").show();
              $divId.find("#makePrimaryDiv").show();
            } else {
              $divId.find("#unverifyIcon").show();
              $divId.find("#unverifyText").show();
              $divId.find("#verifyIcon").hide();
              $divId.find("#verifyText").hide();
              $divId.find("#makePrimaryDiv").hide();
            
            }
            $("#addEmailTextDiv").show();
            $("#addsecalert").show();
            $("#PrimaryEmailDivId").html(data["email"]);
          } else {
            $("#emailErrorMessage").html(i18n.errors.addsecAlert.failMakePrimary);
            $("#emailErrorMessage").show();
          }
        },

        error: function(request) {
          popUpDialogForAlerts("dialog_info", i18n.errors.addsecAlert.failMakePrimary);
        }
      });
    };
    buttonCallBacks[dictionary.lightboxbuttoncancel] = function() {
      $(this).dialog("close");
    };
    $("#dialog_confirmation").text(dictionary.makeprimaryconfirm).dialog('option', 'buttons', buttonCallBacks).dialog(
      "open");
  });



  $(".verifyAlertPref").click(function() {
    var effectiveuserparam = $("#effectiveUserParam").val();
    var id = $(this).attr('name');
    var buttonCallBacks = {};
    buttonCallBacks[dictionary.lightboxbuttonconfirm] = function() {
      $(this).dialog("close");
      $.ajax({
        type: 'POST',
        url: "/portal/portal/tenants/alert_prefs/" + id + "/verify",
        data: {},
        dataType: 'text',
        success: function(data) {
          if (data == "failure") {
            addAlertDelivOpt(function() {
              $("#emailErrorMessage").html(i18n.errors.addsecAlert.failVerifyEmail);
              $("#emailErrorMessage").show();
            });
          } else {
            addAlertDelivOpt(function() {
              $("#additional_emailError").html("");
              $("#emailSuccessMessage").html(i18n.confirm.addsecAlert.verificationEmail.replace("{0}", $(
                "#email-" + id).html()));
              $("#emailSuccessMessage").show();
            });
          }
        },

        error: function(request) {
          popUpDialogForAlerts("dialog_info", i18n.errors.addsecAlert.failVerifyEmail);
        }
      });
    };
    buttonCallBacks[dictionary.lightboxbuttoncancel] = function() {
      $(this).dialog("close");
    };
    $("#dialog_confirmation").text(dictionary.verifyconfirm).dialog('option', 'buttons', buttonCallBacks).dialog(
      "open");

  });

  $("#addnewnotificationprefcancel").click(function() {
    $("#managenotificationDiv").html("");
  });



});

function addAlertDelivOpt(callback) {
  var effectiveuserparam = $("#effectiveUserParam").val();
  var actionurl = notificationUrl + "alert_prefs";
  $.ajax({
    type: "GET",
    url: actionurl,
    data: {
      'param': tenantParam,
      'userParam': effectiveuserparam
    },
    dataType: "html",
    success: function(html) {
      $("#managenotificationDiv").html("");
      $("#managenotificationDiv").html(html);
      if (callback != null && typeof callback === 'function') {
        callback();
      }
    },
    error: function() {}
  });
}

function changePrimaryEmail(callback) {
  var effectiveuserparam = $("#effectiveUserParam").val();
  var actionurl = notificationUrl + "change_primary_email";
  $.ajax({
    type: "GET",
    url: actionurl,
    data: {
      'param': tenantParam,
      'userParam': effectiveuserparam
    },
    dataType: "html",
    success: function(html) {
      $("#changeprimaryemaildiv").html(html);
      var $thisPanel = $("#changeprimaryemaildiv");
      $thisPanel.dialog({
        height: 100,
        width: 950
      });
      $thisPanel.dialog('option', 'buttons', {
        "Cancel": function() {
          $(this).dialog("close");
        }
      }).dialog("open");

      if (callback != null && typeof callback === 'function') {
        callback();
      }
    },
    error: function() {
      $("#changeprimaryemaildiv").html("");
      $(".changeprimaryemaildiv").unbind('click');
    }
  });
}

function fixupTooltipZIndex() {
  var initialIndex = 500;
  $('.widget_grid').each(function() {
    var style = $(this).attr('style');
    if (style) {
      $(this).attr('style', style + ';z-index:' + initialIndex);
    } else {
      $(this).attr('style', 'z-index:' + initialIndex);
    }
    initialIndex -= 5;
  });
  initialIndex = 1000;
  $('.widget_details_popover').each(function() {
    var style = $(this).attr('style');
    if (style) {
      $(this).attr('style', style + ';z-index:' + initialIndex);
    } else {
      $(this).attr('style', 'z-index:' + initialIndex);
    }
    initialIndex -= 5;
  });
}

/**
 * View Notification details
 * @param current
 * @return
 */

function viewNotification(current) {
  var divId = $(current).attr('id');
  var param = divId.substr(3);
  resetGridRowStyle();
  $(current).addClass("selected active");
  var url = notificationUrl + "view_notification";
  $.ajax({
    type: "GET",
    url: url,
    data: {
      date: param,
      tenant: tenantParam
    },
    dataType: "html",
    success: function(html) {
      $("#viewnotificationDiv").html(html);
      fixupTooltipZIndex();
    },
    error: function() {
      //need to handle TO-DO
    }
  });
}

function viewFirstNotification(divId) {
  var param = divId.substr(3);
  var url = notificationUrl + "view_notification";
  $.ajax({
    type: "GET",
    url: url,
    data: {
      date: param,
      tenant: tenantParam
    },
    dataType: "html",
    success: function(html) {
      $("#viewnotificationDiv").html(html);
      fixupTooltipZIndex();
    },
    error: function() {
      //need to handle TO-DO
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

function onNotificationMouseover(current) {
  if ($(current).hasClass('active')) return
  $(current).find("#info_bubble").show();
  return false;
}

function onNotificationMouseout(current) {
  $(current).find("#info_bubble").hide();
  return false;
}

function filterNotifications() {
  var selectedfilter = document.getElementById('selectedDatefilter').value;
  window.location = notificationUrl + "notifications?tenant=" + tenantParam + "&filterBy=" + selectedfilter;
}

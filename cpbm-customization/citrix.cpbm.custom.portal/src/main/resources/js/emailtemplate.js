/*
*  Copyright � 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/
$(document).ready(function() {
  if (filterBy == "") {
    activateThirdMenuItem("12_content_0_tab");
  } else if (filterBy == "0") {
    activateThirdMenuItem("12_content_0_tab");
  } else if (filterBy == "1") {
    activateThirdMenuItem("12_content_1_tab");
  } else if (filterBy == "2") {
    activateThirdMenuItem("12_content_2_tab");
  } else if (filterBy == "3") {
    activateThirdMenuItem("12_content_3_tab");
  } else if (filterBy == "4") {
    activateThirdMenuItem("12_content_4_tab");
  }

  currentPage = parseInt(currentPage);
  perPageValue = parseInt(perPageValue);
  templateListLen = parseInt(templateListLen);
  if (currentPage > 1) {
    $("#click_previous").removeClass("nonactive");
    $("#click_previous").unbind("click").bind("click", previousClick);
  }
  if (templateListLen < perPageValue) {
    $("#click_next").unbind("click");
    $("#click_next").addClass("nonactive");

  } else if (templateListLen == perPageValue) {

    if (currentPage < totalpages) {

      $("#click_next").removeClass("nonactive");
      $("#click_next").unbind("click").bind("click", nextClick);
    } else {
      $("#click_next").unbind("click");
      $("#click_next").addClass("nonactive");
    }
  }

  function nextClick(event) {
    $("#click_next").unbind("click", nextClick);
    $("#click_next").addClass("nonactive");
    currentPage = currentPage + 1;
    $("#click_previous").unbind("click").bind("click", previousClick);
    $("#click_previous").removeClass("nonactive");
    window.location = templatePageUrl + "?filterby=" + filterBy + "&currentpage=" + currentPage + "&userlang=" + $('#localelist').val()+ "&channelId=" + $('#channellist').val();
  }

  function previousClick(event) {
    $("#click_previous").unbind("click", previousClick);
    $("#click_previous").addClass("nonactive");
    currentPage = currentPage - 1;
    $("#click_next").removeClass("nonactive");
    $("#click_next").unbind("click").bind("click", nextClick);
    window.location = templatePageUrl + "?filterby=" + filterBy + "&currentpage=" + currentPage + "&userlang=" + $('#localelist').val() + "&channelId=" + $('#channellist').val();
  }
  $("#localelist").bind("change", function(event) {

    $("#selected_locale").val($(this).find("option:selected").val());
    var $current = $(".widget_navigationlist.selected.active");
    var $name = $current.attr("name");
    var $category = $current.attr("category");
    window.location = templatePageUrl + "?filterby=" + filterBy + "&currentpage=" + currentPage + "&userlang=" + $(
      this).find("option:selected").val() + "&channelId=" + $('#channellist').val();
    //viewemailtemplate($current,$name,$category,$(this).find("option:selected").val());
  });
  $("#channellist").bind("change", function(event) {

    $("#selected_channel").val($(this).find("option:selected").val());
    var $current = $(".widget_navigationlist.selected.active");
    var $name = $current.attr("name");
    var $category = $current.attr("category");
    window.location = templatePageUrl + "?filterby=" + filterBy + "&currentpage=1&userlang="+$("#selected_locale").val()+"&channelId=" + $(
      this).find("option:selected").val();
    //viewemailtemplate($current,$name,$category,$(this).find("option:selected").val());
  });
});

function onTemplateMouseover(current) {
  if ($(current).hasClass('active')) return
  $(current).find("#info_bubble").show();
  return false;
}

function onTemplateMouseout(current) {
  $(current).find("#info_bubble").hide();
  return false;
}

function viewemailtemplate(current, name, category, locale, callback) {
  locale = $("#selected_locale").val();
  var channelId = $("#selected_channel").val();
  emptyAllTemplateDivs();
  if(current != null) {
    resetGridRowStyle();
    $(current).addClass("selected active");
  }
  var url = "/portal/portal/admin/email_template/view";
  $.ajax({
    type: "GET",
    url: url,
    data: {
      name: name,
      userlang: locale,
      channelId:channelId
    },
    dataType: "html",
    cache: true,
    success: function(html) {

      $("#viewEmailTemplateDiv").html(html);
      showHideControls(category);
      bindActionMenuContainers();
      if(callback != undefined || callback !=null){
        callback();
      }
    },
    error: function() {
      //need to handle TO-DO
    }
  });
}

function showHideControls(category) {
  if (category === "EMAILS") {
    $("#sendemailtemplate_action").show();
  } else {
    $("#sendemailtemplate_action").hide();
  }
}

function sendemailtemplate(name) {
  var locale = $("#selected_locale").val();
  var channelId = $("#selected_channel").val();
  initDialog("email_id", 700);
  var $thisPanel = $("#email_id");
  $thisPanel.dialog({
    height: 100,
    width: 700
  });
  $thisPanel.dialog('option', 'title', dictionary.sendTestEmailLabel + " - " + name);
  $thisPanel.dialog('option', 'buttons', {
    "OK": function() {
      var emailInputElement = $thisPanel.find("#confirm_email_id");
      var emailId = emailInputElement.val();
      var url = "/portal/portal/admin/email_template/send";
      if (validateEmail("", $("#confirm_email_id"), $("#confirm_email_id_error"), false)) {
        $.ajax({
          type: "POST",
          url: url,
          data: {
            name: name,
            emailid: emailId,
            userlang: locale,
            channelId:channelId
          },
          dataType: "html",
          success: function(html) {
            $("#action_result_panel").find("#msg").html(dictionary.emailTemplateSendMessage);
            $("#action_result_panel").find("#status_icon").removeClass("erroricon").addClass("successicon");
            $("#action_result_panel").removeClass("error").addClass("success").show();
          },
          error: function(xhr, ajaxOptions, thrownError) {
            $("#action_result_panel").find("#msg").text(xhr.responseText);
            $("#action_result_panel").find("#status_icon").removeClass("successicon").addClass("erroricon");
            $("#action_result_panel").removeClass("success").addClass("error").show();
          }
        });
        $(this).dialog("close");
      }
    },
    "Cancel": function() {
      $(this).dialog("close");
    }

  });
  dialogButtonsLocalizer($thisPanel, {
    'OK': g_dictionary.dialogOK,
    'Cancel': g_dictionary.dialogCancel
  });
  $thisPanel.dialog("open");

}
var reset_to_default_success_callback = function reset_to_default_success_callback(){
  $("#top_message_panel").find("#msg").html(dictionary.resetToDefaultSuccess);
  $("#top_message_panel").find("#status_icon").removeClass("erroricon").addClass("successicon");
  $("#top_message_panel").removeClass("error").addClass("success").show();
}
function reset_to_default(emailTemplateId,current, name, category, locale) {
  initDialog("dialog_info", 390);
  $thisDialog = $("#dialog_info");
  $thisDialog.html(dictionary.resetToDefaultConfirmation);
  $thisDialog.dialog('option', 'buttons', {
    "No":function(){
      $(this).dialog("close");
    },
    "Yes": function() {
      $(this).dialog("close");
      var url = "/portal/portal/admin/email_template/"+emailTemplateId+"/reset_to_default";
      $.ajax({
        type: "POST",
        url: url,
        dataType: "html",
        success: function(html) {
          viewemailtemplate(current, name, category, locale,reset_to_default_success_callback);

        },
        error: function(xhr, ajaxOptions, thrownError) {
          $("#top_message_panel").find("#msg").text(dictionary.resetToDefaultError);
          $("#top_message_panel").find("#status_icon").removeClass("successicon").addClass("erroricon");
          $("#top_message_panel").removeClass("success").addClass("error").show();
        }
      });
      }
  });
  dialogButtonsLocalizer($thisDialog, {
    'Yes': g_dictionary.yes,
    'No': g_dictionary.no
  });
  $thisDialog.bind("dialogbeforeclose", function(event, ui) {
    $thisDialog.empty();
  });
  $thisDialog.dialog("open");
}
function editemailtemplate(name) {
  var locale = $("#selected_locale").val();
  var channelId = $("#selected_channel").val();
  var url = "/portal/portal/admin/email_template/edit";
  $("#edit_email_template").remove();
  $.ajax({
    type: "GET",
    url: url,
    data: {
      name: name,
      userlang: locale,
      channelId:channelId
    },
    dataType: "html",
    success: function(html) {
      $("#editEmailTemplateDiv").html(html);
      initDialog("edit_email_template", 780);
      var $thisPanel = $("#edit_email_template");
      $thisPanel.dialog({
        height: 100,
        width: 780
      });
      $thisPanel.dialog('option', 'buttons', {
        "OK": function() {
          var textareaElement = $thisPanel.find("#email_template_text");
          var emailText = textareaElement.val();
          updateemailtemplate(name, emailText);
          $(this).dialog("close");
          $(this).dialog("destroy");
          $thisPanel.remove();
        },
        "Cancel": function() {
          $(this).dialog("close");
          $(this).dialog("destroy");
          $thisPanel.remove();
          $("#editEmailTemplateDiv").html("");
        }

      });
      dialogButtonsLocalizer($thisPanel, {
        'OK': g_dictionary.dialogOK,
        'Cancel': g_dictionary.dialogCancel
      });
      $thisPanel.dialog("open");
    },
    error: function() {
      //need to handle TO-DO
    }
  });
}

function updateemailtemplate(name, emailText, locale) {
  locale = $("#selected_locale").val();
  var channelId = $("#selected_channel").val();
  $("#viewEmailTemplatePreviewDiv").html("");
  $("#email_last_updated_at").html("");
  var url = "/portal/portal/admin/email_template/update";
  $.ajax({
    type: "POST",
    url: url,
    data: {
      name: name,
      updatedtext: emailText,
      userlang: locale,
      channelId:channelId
    },
    dataType: "json",
    success: function(json) {
      if (json.parseError == false)
        $("#viewEmailTemplatePreviewDiv").html(json.emailText);
      else
        $("#viewEmailTemplatePreviewDiv").text(json.emailText);
      $("#email_last_updated_at").html("<span>" + dateFormat(json.lastUpdatedAt, g_dictionary.jsDateFormat, false) +
        "</span>");
      $("#action_result_panel").hide();
      updateTopMessagePanel(dictionary.editTemplateSuccess, "success");
    },
    error: function(xhr, ajaxOptions, thrownError) {
      updateTopMessagePanel(xhr.responseText, "error");
    }
  });
  $("#editEmailTemplateDiv").html("");
}

function emptyAllTemplateDivs() {
  $("#viewEmailTemplateDiv").show();
  $("#viewEmailTemplateDiv").html("");
  $("#editEmailTemplateDiv").html("");
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

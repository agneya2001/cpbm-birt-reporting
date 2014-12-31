/*
*  Copyright © 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/
$(document).ready(function() {

  activateThirdMenuItem("l3_home_connectors_oss_tab");


  $("a.filters").bind("click", function(event) {
    var category = $(this).attr('id');
    showSelectedCategory(category);
  });

  $(".servicedetails").bind("click", function(event) {
    var id = $(this).attr('id');
    initDialog("dialog_service_details", 720);
    $.ajax({
      type: "GET",
      url: connectorPath + "/" + type + "?id=" + id + "&action=view",
      dataType: 'html',
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
      error: function(error) {
        $("#spinning_wheel").hide();
      }
    });
  });

  $(".add_button.active.edit").live("click", function(event) {
    var id = $(this).attr('id').substr(10);

    $("#spinning_wheel").show();
    if (hasServiceConfiguration(id, false)) {

      $.ajax({
        type: "GET",
        url: connectorPath + "/" + type + "?instanceId=" + id,
        dataType: 'html',
        success: function(html) {
          $(".service_detailpanel").html(html).show();
          $(".service_listpanel").hide();
          $(".widgetcatalog_filterpanel").hide();
          $("#spinning_wheel").hide();

          var newpos = $("#main").offset();
          window.scrollTo(newpos.left, newpos.top);
        },
        error: function(error) {
          $("#spinning_wheel").hide();
        }
      });
    } else {
      $("#spinning_wheel").hide();
      displayConfiguredDialog(false, "success");
    }
  });

  $(".add_button.active.add").live("click", function(event) {
    var id = $(this).attr('id').substr(10);

    if (hasServiceConfiguration(id, true)) {
      showAddInstanceForm(id);
    } else {
      enableService(id);
    }
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

  $("a.cancel").live("click", function(event) {
    closeDialog();
  });
});

function dialog_enable_service(id) {
  initDialog("dialog_enable_service", 785);
  var actionurl = connectorPath + "/enable_service?id=" + id;
  $.ajax({
    type: "GET",
    url: actionurl,
    dataType: "html",
    success: function(html) {
      var $thisDialog = $("#dialog_enable_service");
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
    },
    error: function() {

    }
  });
}

function createInstance() {
  $("#spinning_wheel").show();
  var uuid = $("#submitbutton").attr('uuid');
  var action = $("#submitbutton").attr('action');
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
    url: connectorPath + "/create_instance",
    data: {
      "configProperties": JSON.stringify(configProperties),
      "id": uuid,
      "action": action
    },
    dataType: "json",
    success: function(data) {
      $("#spinning_wheel").hide();
      var $resultDisplayBanner = $("#resultstring");
      if (data.validationResult == "SUCCESS") {
        if (data.result == "SUCCESS") {
          $("#submitbutton").removeClass("active").addClass("nonactive");
          $configureButton = $("#configure_" + uuid);
          $configureButton.removeClass('add').addClass('edit');
          $configureButton.attr('id', 'configure_' + data.instanceid);
          $resultDisplayBanner.css('color', 'green');
        }
        $resultDisplayBanner.text(data.message);
      } else {
        $resultDisplayBanner.text(data.validationResult);
      }
      $resultDisplayBanner.parents("div.service_detail_subsection").show();
    },
    error: function(data) {
      $resultDisplayBanner.text(i18n.errors.connector.createfailed);
      $resultDisplayBanner.parents("div.service_detail_subsection").show();
      $("#spinning_wheel").hide();
    }
  });
}

function showSelectedCategory(category) {
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
  if (currentstep == "step1" && $("#tncAccept").is(':checked') == false) {
    $("#tncAcceptError").text(dictionary.tncAcceptMessage);
  } else {
    $("#spinning_wheel").show();
    var id = $("#serviceParam").val();
    $.ajax({
      type: "POST",
      url: connectorPath + "/enable_service",
      data: {
        "id": id,
        "profiledetails": ""
      },
      dataType: "text",
      success: function(status) {
        if (status == 'success') {
          showAddInstanceForm(id);
          $("#reloadlist").val(true);
          $("#dialog_enable_service").dialog("close");
        }
        $("#spinning_wheel").hide();
      },
      error: function(status) {
        $("#spinning_wheel").hide();
      }
    });
  }
}

function showAddInstanceForm(id) {
  $("#spinning_wheel").show();
  $.ajax({
    type: "GET",
    url: connectorPath + "/" + type + "?id=" + id,
    dataType: 'html',
    success: function(html) {
      $(".service_detailpanel").html(html).show();
      $(".service_listpanel").hide();
      $(".widgetcatalog_filterpanel").hide();
      $("#spinning_wheel").hide();
      var newpos = $("#main").offset();
      window.scrollTo(newpos.left, newpos.top);
    },
    error: function(error) {
      $("#spinning_wheel").hide();
    }
  });
}

function closeDialog() {
  $("#dialog_enable_service").dialog("close");
}

function displayConfiguredDialog(add, result) {

  var message = "";

  if (result == "failure") {
    message = dictionary.serviceConfigureFailure;
  } else {
    if (add) {
      message = dictionary.serviceConfigureSuccess;
    } else {
      message = dictionary.serviceAlreadyConfigured;
    }
  }
  displayDialog(message);
}

function displayDialog(message) {

  initDialog("dialog_info");
  $thisDialog = $("#dialog_info");
  $thisDialog.dialog("option", {
    height: 200,
    width: 300,
    title: dictionary.configure
  });
  $thisDialog.html(message);
  $thisDialog.dialog('option', 'buttons', {
    "OK": function() {
      $(this).dialog("close");
    }
  });
  dialogButtonsLocalizer($thisDialog, {
    'OK': g_dictionary.dialogOK
  });
  $thisDialog.bind("dialogbeforeclose", function(event, ui) {
    $thisDialog.empty();
    window.location = "/portal/portal/connector/oss";
  });
  $thisDialog.dialog("open");
}

function hasServiceConfiguration(id, add) {

  var urlPath;
  if (add) {
    urlPath = connectorPath + "/has_service_configuration?id=" + id;
  } else {
    urlPath = connectorPath + "/has_service_configuration?instanceId=" + id;
  }
  var result;
  $.ajax({
    type: "GET",
    url: urlPath,
    async: false,
    dataType: 'json',
    success: function(value) {
      result = value;
    },
    error: function(value) {
      result = false;
    }
  });
  return result;
}

function enableService(id) {

  var configProperties = new Array();
  $.ajax({
    type: "POST",
    url: connectorPath + "/create_instance",
    data: {
      "configProperties": JSON.stringify(configProperties),
      "id": id,
      "action": "add"
    },
    dataType: "json",
    success: function(data) {
      $("#spinning_wheel").hide();
      if (data.result == "SUCCESS") {
        displayConfiguredDialog(true, "success");
      } else {
        displayConfiguredDialog(true, "failure");
      }
    },
    error: function(data) {
      $("#spinning_wheel").hide();
      displayConfiguredDialog(true, "failure");
    }
  });
}

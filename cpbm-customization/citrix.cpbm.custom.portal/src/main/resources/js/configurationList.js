/*
*  Copyright © 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/

$(document).ready(function() {

  var current_tab_string = $('#module').val().toLowerCase() || 'configaccountmanagement';
  activateThirdMenuItem("l3_config_" + current_tab_string + "_tab");
});

function configuration_edit_dialog(module, component) {
  $("#spinning_wheel").show();

  initDialog("configuration_edit_panel", 750);
  var $thisPanel = $("#configuration_edit_panel");

  $.ajax({

    type: "GET",
    url: configPath + "?component=" + component + "&module=" + module,
    data: "",
    dataType: 'html',
    success: function(html) {
      $thisPanel.find(".dialog_formcontent").html(html);
      $thisPanel.dialog({
        height: "auto",
        width: 750,
        closeText: 'hide',
        autoOpen: false,

      });
      $thisPanel.dialog('option', 'buttons', {
        "Close": function() {
          $(".ui-dialog-buttonpane button:contains('"+ g_dictionary.save +"')").hide();
          $thisPanel.find(".dialog_formcontent").html("");
          $(this).dialog("close");

        },
        "Edit": function() {
          $('div[id^="valuenotedit"]').hide();
          $('div[id^="valueedit"]').show();
          $(".ui-dialog-buttonpane button:contains('"+ g_dictionary.edit +"')").hide();
          $(".ui-dialog-buttonpane button:contains('"+ g_dictionary.save +"')").show();
        },
        "Save": function() {
          if(saveAllConfigValues()) {
            $(".ui-dialog-buttonpane button:contains('"+ g_dictionary.save +"')").hide();
            $(".ui-dialog-buttonpane button:contains('"+ g_dictionary.edit +"')").show();
          }
        },
      });

      dialogButtonsLocalizer($thisPanel, {
        'Close': g_dictionary.dialogClose,
        'Edit' : g_dictionary.edit,
        'Save' : g_dictionary.save
      });
      $(".ui-dialog-buttonpane button:contains('"+ g_dictionary.save +"')").hide();
      $("#spinning_wheel").hide();
      $thisPanel.dialog("open");
      
      $('input[id^="value"]').each(function() {
        var configProperty = new Object();
        configProperty.name = $(this).attr("id").substr(5);
        var fullId = 'input[id^="value' +  configProperty.name +"\"]";
        
        if($(fullId).attr('restart') == 'true'){
          $(fullId).bind("focusout", function() {
            popUpDialogForAlerts("dialog_info", dictionary.configurationRestartRequiredAlert); 
          });
        }
      });
      
      
    },

    error: function(request) {
      $("#spinning_wheel").hide();
      initDialogWithOK("dialog_info", 350, false);
      $("#dialog_info").dialog("option", "height", 150);
      $("#dialog_info").text(dictionary.configurationFetchError).dialog("open");

    }
  });


}

function saveAllConfigValues() {
  var editSuccess = true;
	$(".error").remove();
  var configKeyValues = new Array();
  $('input[id^="value"]').each(function() {
    var configProperty = new Object();
    configProperty.name = $(this).attr("id").substr(5);
    configProperty.value = $(this).attr("value");
    configKeyValues.push(configProperty);
  }),
  // send the configKeyValues to controller
  $.ajax({
    type: "POST",
    url: "edit",
    dataType:"json",
    async: false,
    data: $("#configurationForm").serialize(),
    success: function(data) {
      if (data.status == "SUCCESS") {
        $('input[name$="value"]').each(function() {
          var configProperty = new Object();
          configProperty.name = $(this).attr("id");
          configProperty.value = $(this).attr("value");
		  if($(this).attr("isEncrypted") == "true"){
		    $("#valuenotedit" + configProperty.name).html("****");
		  }else{
		    $("#valuenotedit" + configProperty.name).text(configProperty.value);
		  }
     });
        $('div[id^="valuenotedit"]').show();
        $('div[id^="valueedit"]').hide();
      } else {
        editSuccess = false;
        $('div[id^="valueerror"]').show();
      }
    },
    error: function(XMLHttpRequest) {
      editSuccess = false;
    	  if (XMLHttpRequest.status === AJAX_FORM_VALIDATION_FAILED_CODE) {
              displayAjaxFormError(XMLHttpRequest,
                "configurationForm",
                "main_addnew_formbox_errormsg");
            }
    }
  });
  return editSuccess;
}

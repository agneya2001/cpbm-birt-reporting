/*
*  Copyright © 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/
$(document).ready(function() {
  //console.log(jspProvidedByService+'...serviceAccountConfigProperties...',emptyServiceAccountConfigProperties);
  if(emptyServiceAccountConfigProperties == 'true'){
      enableService(1);
  }else{
    $("#serviceAccountConfigParamForm").show();
    if(foundJspProvidedByService == 'true'){
      handleCustomJsp();
    }
  }

  $("#serviceAccountConfigParamForm").validate({
	  ignoreTitle: true,
      submitHandler : function() {
        var noOfErrors = $('label.error:visible').length;
        if (noOfErrors == 0) {
    if(foundJspProvidedByService == 'true'){
      enableService(2);
    }else{
          enableService(0);
    }
        }
        return false;
      }
    });


   $("#submitbutton").bind("click", function(event) {
        if($("#submitbutton").hasClass('active')){
          $('#serviceAccountConfigParamForm').submit();
        }
  });

   $("#cancelButton").bind("click", function(event) {
    window.open('/portal/portal/connector/csinstances?tenant='+effectiveTenantParam,'_parent');
  });

  $("#resultButton").bind("click", function(event) {
    window.open('/portal/portal/connector/csinstances?tenant='+effectiveTenantParam,'_parent');
  });

  function handleCustomJsp(){
    //console.log('Custom Jsp Found');
    $("#serviceAccountConfigParamForm").show();
    $.ajax({
          url : "/portal/portal/connector/load_packaged_jsp?serviceInstanceUUID="+currentServiceInstanceUUID,
          dataType : "html",
          async : false,
          success : function(html) {
      $("#accountConfigEditorJsp").html(html);
          }
        });
  }

  function enableService(enableServiceType){
  var propObject = new Object();
  if(enableServiceType == 0){
    $("input[id^=sacp_]").each(function(index) {
         var eleName = $(this).attr("name");
         var eleValue = $(this).attr('value');
            propObject[eleName] = eleValue;
    });
    $("input[id^=radio_sacp_]:checked").each(function(index) {
        var eleName = $(this).attr("name");
        var eleValue = $(this).attr('value');
	       propObject[eleName] = eleValue;
	});
  }else if(enableServiceType == 2){
    $.each( serviceAccountConfigPropertiesArray.split(','), function(index,value ) {
      if(value != null && value != ""){
        var elementId = "#"+value;
        var elementValue = $(elementId).attr('value');
        propObject[value] = elementValue;
      }
    });
  }

   var propConfigs = JSON.stringify(propObject);
    $("#spinning_wheel").show();
    var ajaxUrl = "/portal/portal/tenants/enable_service";
    $.ajax({
      type : "POST",
      data : {
        "tenantparam" : effectiveTenantParam,
        "instanceUuid" : currentServiceInstanceUUID,
        "instanceProperty" : propConfigs
      },
      url : ajaxUrl,
      success : function(status) {
        $("#spinning_wheel").hide();
        prepareSuccessResultView(status);
      },
      error : function(error) {
         $("#spinning_wheel").hide();
         $("#resultForServiceConfigParams").show();
         $("#ajax_result_text").text(service_config_message_dict.enableServiceError + "(" + error.responseText + ")");
         $("#ajax_result_display").addClass("error").show();
      }
    });
  }

  function prepareSuccessResultView(status){
    var current = 1;
    var liStyle ="";
    var final = "";
    if(status.registeredUser != undefined){
      $.each( status.registeredUser, function( key, value ) {
        var temp ="";
        if(current%2 ==0){
          liStyle = "even";
        }else{
          liStyle = "odd";
        }
        current = current +1;
        temp = temp + '<li class="row '+ liStyle + '">';
        temp = temp + '<span class="label">' + value + '</span> ';
        temp = temp + '<span class="description">' + service_config_message_dict.label_Configured + '</span></li>';
        final = final + temp;
      });
    }
    if(status.failedUsers != undefined){
      $.each( status.failedUsers, function( key, value ) {
        var temp ="";
        if(current%2 ==0){
          liStyle = "even";
        }else{
          liStyle = "odd";
        }
        current = current +1;
        temp = temp + '<li class="row '+ liStyle + '">';
        temp = temp + '<span class="label">' + value + '</span> ';
        temp = temp + '<span class="description"> Failed </span> </li>';
        final = final + temp;
      });
    }
    $("#resultForServiceConfigParams").show();
    $("#printResultsForConfigParams").append(final);
    $("#mainContentForServiceAccConfigParam").hide();
    $("#ajax_result_text").text("");
    $("#ajax_result_display").hide();
  }

});
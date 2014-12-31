/*
*  Copyright � 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/


$(document).ready(function() {
  $('#resourceType').live("change",function() {
    step2ResourceTypeChanged();
  });

  function step2ResourceTypeChanged() {
    $("#step2SelectionChanged").val("true");
    $("#skipStep3").val("false");
    $("#bundleComponents").find("li[id^=componentLeftPanel_]").each(function() {
      $(this).remove();
    });
    $("#componentValues").find("div[id^=componenttypeValues]").each(function() {
      $(this).remove();
    });

    refreshConstraints();
  }

  function refreshConstraints() {
    // this would be needed because constraints that can be chosen for
    // a bundle should be "more constrained" than the resourceType it
    // is a bundle for.
    var selectedRes = $("#resourceType option:selected").val();
    $("#productBundle\\.businessConstraint").empty();
    var constrOpts = [];

    constrOpts.push('<option value="">',
      commmonmessages.choose_label,
      "</option>");

    $.ajax({
      type: "GET",
      url: "/portal/portal/productBundles/getApplicableBusinessConstraints",
      dataType: "json",
      data: {
        "resourceType": selectedRes
      },
      async: false,
      success: function(result) {
        for (var i = 0; i < result.length; i++) {
          var constrt = result[i];
          constrOpts.push('<option value="', constrt, '">',
            commmonmessages[constrt], '</option>');
        }
        $("#productBundle\\.businessConstraint").html(constrOpts.join(''));
      },
      error: function(XMLHttpRequest) {
        //why?
      }
    });
  }

  $("#backtobundledetails").live("click", function(event) {
    $(".j_bundlespopup").hide();
    currentstep = "step1";
    $("#step1").show();
  });

  $("#backtocharges").live("click", function(event) {
    $(".j_bundlespopup").hide();
    currentstep = "step4";
    $("#step4").show();
  });

  /**
   * add product wizard last step link.
   */
  $(".close_product_wizard").live("click", function(event) {
    $currentDialog.dialog("close");
    $("#dialog_add_bundle").empty();
    $("#dialog_edit_budle").empty();
    if ($("#productBundlegridcontent").find(".j_viewbundle").size() == 1) {
      $("#product_bundle_tab").click();
    }
  });

  $("#rateCardChargesForm").validate({
    success: "valid",
    ignoreTitle: true,
    errorPlacement: function(error, element) {
      var name = element.attr('id');
      var nameAttr = element.attr('name');
      if (element.hasClass('j_pricerequired')) {
        if ($("#priceRequiredError").html().trim() == "") {
          error.appendTo("#priceRequiredError");
          $(".dialog_common_messagebox").show();
        }
      } else {
        name = ReplaceAll(name, ".", "\\.");
        if (name != "") {
          error.appendTo("#" + name + "Error");
        }
      }
    }
  });

  $(".dropdownbutton").hover(function() {
    $("#plansdropdown").show();
  }, function() {
    $("#plansdropdown").hide();
  });

  $(function() {
    $('#planstartDate').datepicker({
      duration: '',
      showOn: "button",
      buttonImage: "/portal/images/calendar_icon.png",
      buttonImageOnly: true,
      dateFormat: g_dictionary.friendlyDate,
      showTime: false,
      minDate: new Date()
    });
  });

  $(function() {
    $('#editstartDate').datepicker({
      duration: '',
      showOn: "button",
      buttonImage: "/portal/images/calendar_icon.png",
      buttonImageOnly: true,
      dateFormat: g_dictionary.friendlyDate,
      showTime: false,
      minDate: new Date()
    });
  });

  $("#ui-datepicker-div").css("z-index", "3003");

  jQuery.validator.setDefaults({
    onfocusout: false,
    onkeyup: false,
    onclick: false
  });

  $.validator.addClassRules("j_startDate", {
    startDate: true
  });


  $.validator
    .addMethod(
      "startDate",
      function(value, element) {
        $(element).rules("add", {
          required: true
        });
        var planforcurrentchargesflag = $("#planforcurrentchargesflag").val();
        if (planforcurrentchargesflag == "true") {
          var now = new Date();
          now.setHours(0, 0, 0, 0);
          return $.datepicker.parseDate(g_dictionary.friendlyDate, value) >= now;
        } else {
          var now = new Date();
          now.setHours(0, 0, 0, 0);
          return $.datepicker.parseDate(g_dictionary.friendlyDate, value) >= now;
        }

      },
      i18n.errors.bundleEnterValidStartDate);

  $("#rateCardForm").validate({
    success: "valid",
    ignoreTitle: true,
    errorPlacement: function(error, element) {
      var name = element.attr('id');
      var nameAttr = element.attr('name');
      if (element.hasClass('j_pricerequired')) {
        if ($("#priceRequiredError").html().trim() == "") {
          error.appendTo("#priceRequiredError");
          $(".dialog_common_messagebox").show();
        }
      } else {
        name = ReplaceAll(name, ".", "\\.");
        if (name != "") {
          error.appendTo("#" + name + "Error");
        }
      }
    }
  });


  $("#details_tab").live("click", function(event) {
    $(".widgets_detailstab").removeClass("active").addClass("nonactive");
    $(this).removeClass("nonactive").addClass("active");
    $("#entitlements_content").hide();
    $('#channelpricing_content').hide();
    $('#bundlepricing_content').hide();
    $('#provisioningconstraint_content').hide();
    $('#details_content').show();
  });

  initDialog("addEntitlement_div", 650);

  $("#productBundlegridcontent").sortable({
    update: function(event, ui) {
      var productBundleList = $(this).sortable('toArray').toString();
      $("#productBundleOrderData").val(productBundleList);
      $.ajax({
        type: "POST",
        url: "/portal/portal/productBundles/editproductbundleorder",
        data: $("#productBundleOrderData").serialize(),
        dataType: "json",
        success: function() {
          $("#product_bundle_tab").click();
        },
        error: function() {
          $("#product_bundle_tab").click();
        }
      });
    }
  });

  $("#unlimitedUsage").live("click", function() {
    if ($(this).is(":checked")) {
      $("#entitlement\\.includedUnits").attr("disabled", "disabled");
      $("#entitlement\\.includedUnits").val('0');
    } else {
      $("#entitlement\\.includedUnits").removeAttr("disabled");
      $("#entitlement\\.includedUnits").val('0');
    }
  });

  $(".unlimitedUsage").live("click", function() {
    var divId = $(this).attr('id');
    var ID = divId.substr(14);
    var newvalue = $("#value" + ID).val();
    if ($(this).is(":checked")) {
      $("#value" + ID).attr("disabled", "disabled");
      $("#value" + ID).val('0');
    } else {
      $("#value" + ID).removeAttr("disabled");
      $("#value" + ID).val('0');
    }
  });


  $(".morelink").live("click", function(event) {
    $(this).prevAll(".descp").switchClass("ellipsis", "dummydummy31415926535", 500);
    $(this).hide();
    $(this).nextAll(".lesslink").show();
  });

  $(".lesslink").live("click", function(event) {
    $(this).prevAll(".descp").switchClass("dummydummy31415926535", "ellipsis", 500);
    $(this).hide();
    $(this).prevAll(".morelink").show();
  });
  
  $("#bundleLogoForm").validate({
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
	        required: i18n.errors.edit_image_path_invalid_message
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
});

/**
 * View productBundle details
 * @param current
 * @return
 */

function viewProductBundle(current) {
  var divId = $(current).attr('id');
  var ID = divId.substr(3);
  resetGridRowStyle();
  $(current).addClass("selected");
  $(current).addClass("active");
  viewBundleById(ID, true);
}

function viewBundleById(ID, asynch) {
  var url = productBundlesUrl + "view";
  $.ajax({
    type: "GET",
    url: url,
    cache:false,
    async: asynch,
    data: {
      Id: ID,
      whichPlan: $("#whichPlan").val()
    },
    dataType: "html",
    success: function(html) {
      $("#editproductBundleDiv").html("");
      $("#viewproductBundleDiv").html(html);
      bindActionMenuContainers();
    },
    error: function() {
      //need to handle TO-DO
    }
  });
}

function addBundlePrevious(current) {
  var prevStep = $(current).parents(".j_bundlespopup").find('#prevstep').val();
  if (prevStep == "step3" && $("#skipStep3").val() == "true") {
    prevStep = "step2";
    $("#step2SelectionChanged").val("true");
    $("#step3EntriesFetched").val("false");
  }

  if (prevStep == "step2") {
    $("#recurringCharges").hide();
    $("#provisioningConstraintError").text("");
  }
  if (prevStep != "") {
    $(".j_bundlespopup").hide();
    $("#" + prevStep).show();
  }
}

function addBundleNext(current) {
  var currentstep = $(current).parents(".j_bundlespopup").attr('id');
  var $step5 = $("#step5");
  var $step4 = $("#step4");
  var $step6 = $("#step6");
  var $currentstep = $("#" + currentstep);
  var nextstep = $currentstep.find("#nextstep").val();
  var bundleForm = $(current).closest("form");
  var serviceInstanceId = "";
  var provisionalConstraints = null;
  var reviewStep = $step4;
  if (bundle_action == "create") {
    reviewStep = $step5;
  }
  if ($(bundleForm).valid()) {

    if (currentstep == "step1") {
      reviewStep.find("#confirmBundleDetails").find("#name").text($("#productBundle\\.name").val());
      reviewStep.find("#confirmBundleDetails").find("#name").attr("title", $("#productBundle\\.name").val());
      reviewStep.find("#confirmBundleDetails").find("#bundle_description").text($("#productBundle\\.description").val());
      reviewStep.find("#confirmBundleDetails").find("#bundle_description").attr("title", $(
        "#productBundle\\.description").val());
      reviewStep.find("#confirmBundleDetails").find("#code").text($("#productBundle\\.code").val());
      reviewStep.find("#confirmBundleDetails").find("#code").attr("title", $("#productBundle\\.code").val());

      if (bundle_action == "create") {

        if ($("#step2AlreadyReached").val() == "false") {
          var svcResTypeOpts = [];

          svcResTypeOpts.push('<option value="">',
            commmonmessages.choose_label,
            "</option>");

          // Service Bundle
          svcResTypeOpts.push('<option value="sb">',
            commmonmessages.service_bundle,
            "</option>");
          serviceInstanceId = $("#instances").find(".instance_selected").attr("id");

          // do not allow not selecting service Instance ID
          if (serviceInstanceId == null || serviceInstanceId == "") {
            $("#productBundle\\.serviceInstanceError").text(i18n.errors.serviceinstancerequired);
            $("#productBundle\\.serviceInstanceError").show();
            return;
          }

          $("#spinning_wheel_cpb").show();
          $.ajax({
            type: "GET",
            url: "/portal/portal/productBundles/getServiceResources",
            dataType: "json",
            data: {
              "serviceInstance": serviceInstanceId
            },
            async: false,
            success: function(result) {
              for (var i = 0; i < result.length; i++) {
                svcResTypeOpts.push('<option value="', result[i].id, '">',
                  l10resourceTypeAndComponentNames[result[i].resourceTypeName + "-name"], '</option>');
              }
              $("#resourceType").html(svcResTypeOpts.join(''));
            },
            error: function(XMLHttpRequest) {
              alert("Error here Handle this");
            }
          });
          $("#spinning_wheel_cpb").hide();
          $("#step2AlreadyReached").val("true");
        }
      }
    }

    if (currentstep == "step2" && $("#skipStep3").val() == "true") {
      $(".j_bundlespopup").hide();
      $("#" + nextstep).show();
      execstep4();
    }

    if (currentstep == "step2") {
      if (bundle_action == "create") {
        reviewStep.find("#confirmBundleDetails").find("#chargefrequncy").text(
          $("#chargeType option:selected").text());
      }
      if ($("input:checkbox[name=productBundle\\.trialEligibility]").is(':checked') == true) {
        reviewStep.find("#confirmBundleDetails").find("#trialeligibility").text(g_dictionary.labelTrue);
      } else {
        reviewStep.find("#confirmBundleDetails").find("#trialeligibility").text(g_dictionary.labelFalse);
      }
      if ($("input:checkbox[name=productBundle\\.notificationEnabled]").is(':checked') == true) {
        reviewStep.find("#confirmBundleDetails").find("#notificationEnabled").text(g_dictionary.labelTrue);
      } else {
        reviewStep.find("#confirmBundleDetails").find("#notificationEnabled").text(g_dictionary.labelFalse);
      }

      //XXX step3 should not show if no components
      if ($("#step2SelectionChanged").val() == "true" || (bundle_action == "edit" && $("#step3EntriesFetched").val() ==
        "false")) {
        $("#spinning_wheel_cpb").show();
        var resTypeId = null;
        if (bundle_action == "create") {
          resTypeId = $("#resourceType option:selected").val();
        }
        if (bundle_action == "edit") {
          resTypeId = $("#resourceTypeName").val();
          provisionalConstraints = JSON.parse($("#jsonProvisionalConstraints").val());
        }
        //skip step 2 for service bundle
        if (resTypeId == "sb" || resTypeId == "") {
          var $nextstepitem = $("#" + nextstep);
          nextstep = $nextstepitem.find("#nextstep").val();
          $("#spinning_wheel_cpb").hide();
          $("#spinning_wheel_cpb").hide();
          $(".j_bundlespopup").hide();
          $("#" + nextstep).show();
          $("#skipStep3").val("true");
          execstep4();
          return;
        }

        var associations = [];
        serviceInstanceId = $("#instances").find(".instance_selected").attr("id");

        $.ajax({
          type: "GET",
          url: "/portal/portal/productBundles/getAssociationTypes",
          dataType: "json",
          async: false,
          success: function(values) {
            associations = values;
          }
        });

        var errorInFetchingValues = false;
        $.ajax({
          type: "GET",
          url: "/portal/portal/productBundles/get_filters_and_components",
          data: {
            "resourceType": resTypeId
          },
          dataType: "json",
          async: false,
          success: function(components) {
            //XXX step3 should not show if no components
            if (components == null || (components["filters"].length == 0 && components["components"].length == 0)) {
              var $nextstepitem = $("#" + nextstep);
              nextstep = $nextstepitem.find("#nextstep").val();
              $("#spinning_wheel_cpb").hide();
              $(".j_bundlespopup").hide();
              $("#" + nextstep).show();
              $("#skipStep3").val("true");
              execstep4();
              return;
            }

            var compListHtml = "";
            var keys = ["filters", "components"];
            for (var index = 0; index < keys.length; index++) {
              errorInFetchingValues = false;
              var key = keys[index];
              for (var i = 0; i < components[key].length; i++) {
            	errorInFetchingValues = false;
                var component = components[key][i];

                var compValBlock = $("#componentValues").find("#componentValuesBlock").clone();
                var actionUrl;
                if (key == "filters") {
                  actionUrl = "/portal/portal/productBundles/get_filter_value";
                } else if (key == "components") {
                  actionUrl = "/portal/portal/productBundles/getComponentValue";
                }
                $.ajax({
                  type: "GET",
                  url: actionUrl,
                  data: {
                    "component": component,
                    "resourceType": resTypeId,
                    "serviceInstance": serviceInstanceId
                  },
                  dataType: "json",
                  async: false,
                  success: function(values) {
                    //XXX still there would be a case where step 3 would show blank.
                    //    think why?
                    if (values == null || values.length == 0) {
                      return;
                    }

                    compListHtml += '<li onclick="javascript:showComponentValues(' + "'" + component + "'" +
                      ');" class="widget_navigationlist" id="componentLeftPanel_' + component +
                      '"><span style="margin-top:4px;"></span><span componentid="' + component + '" class="title" title="'+l10resourceTypeAndComponentNames[component + "-name"] +'">' +
                      l10resourceTypeAndComponentNames[component + "-name"] + '</span></li>';


                    compValBlock.attr("id", "componenttypeValues." + component);

                    var compAssocHtml = "";
                    var compValuesHtml = "";

                    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    // FOR BUNDLE CREATE
                    if (bundle_action == "create") {
                      for (var k = 0; k < associations.length; k++) {
                        var checked = "";
                        if (k == 0) {
                          checked = 'checked="checked"';
                        }
                        compAssocHtml += '<div style="float:left; width:20px"><input type="radio" ' + checked +
                          ' value="' +
                          associations[k] +
                          '" class="discountTypeRadio" onclick="javascript:onAssociationRadioButtonClick(this);"></div><div style="float:left; width:120px">' +
                          '<label>' + i10associationTypes[associations[k]] + '</label></div>';
                      }
                      compValBlock.find("#compAsscoiation").html(compAssocHtml);

                      for (var j = 0; j < values.length; j++) {
                        var extras = getFormattedDisplayAttribtutesString(values[j].displayAttributes);
                        if (isBlank(extras)) {
                          extras = getFormattedAttribtutesString(values[j].attributes);
                        }

                        var displayMoreLink = "none";
                        if (extras.length > 65) {
                          displayMoreLink = "inline";
                        }

                        var oddOrEven = "odd";
                        if (j % 2 == 0) {
                          oddOrEven = "even";
                        }
                        compValuesHtml +=
                          '<div ' +
                          'style="width: 468px;" ' +
                          'class="widget_grid details ' + oddOrEven + '" ' +
                          'compValue="' + values[j].value + '" ' +
                          'compValueName="' + values[j].name + '">' +
                          '<div ' +
                          'onclick="javascript:onClickOfWidgetCheckbox(this);" ' +
                          'class="widget_checkbox widget_checkbox_wide">' +
                          '<span class="unchecked"></span>' +
                          '</div>' +
                          '<div ' +
                          'style="margin:0; width: 400px; padding-bottom: 5px;" ' +
                          'class="widget_grid_description">' +
                          '<span ' +
                          'class="ellipsis" ' +
                          'style="width: 390px;" ' +
                          'title="' + values[j].name + '"><strong>' + values[j].name + '</strong>' +
                          '</span>' +
                          '<div> ' +
                          '<span class="descp ellipsis subtxt">' + extras + '</span>' +
                          '<a class="morelink" href="javascript:void(0);" style="display:' + displayMoreLink +
                          ';">'+commmonmessages.MORE+'</a>' +
                          '<a style="display: none" class="lesslink" href="javascript:void(0);">'+commmonmessages.LESS+'</a>' +
                          '</div>' +
                          '</div>' +
                          '</div>';
                      }
                    } else if (bundle_action == "edit") {

                      ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                      // FOR BUNDLE EDIT

                      // Check if assoictaion is already there
                      var associationAlreadyThere = "";
                      if (provisionalConstraints != null) {
                        for (var m = 0; m < provisionalConstraints.length; m++) {
                          if (provisionalConstraints[m].componentName == component) {
                            associationAlreadyThere = provisionalConstraints[m].association;
                            break;
                          }
                        }
                      }

                      for (var k = 0; k < associations.length; k++) {
                        var checked = "";
                        if (k == 0 && associationAlreadyThere == "") {
                          checked = 'checked="checked"';
                        } else if (associationAlreadyThere.toLowerCase() == associations[k].toLowerCase()) {
                          checked = 'checked="checked"';
                        }
                        compAssocHtml += '<div style="float:left; width:20px"><input type="radio" ' + checked +
                          ' value="' +
                          associations[k] +
                          '" class="discountTypeRadio" onclick="javascript:onAssociationRadioButtonClick(this);"></div><div style="float:left; width:120px">' +
                          '<label>' + i10associationTypes[associations[k]] + '</label></div>';
                      }
                      compValBlock.find("#compAsscoiation").html(compAssocHtml);


                      for (var j = 0; j < values.length; j++) {
                        var extras = getFormattedDisplayAttribtutesString(values[j].displayAttributes);
                        if (isBlank(extras)) {
                          extras = getFormattedAttribtutesString(values[j].attributes);
                        }

                        var displayMoreLink = "none";
                        if (extras.length > 65) {
                          displayMoreLink = "inline";
                        }

                        var oddOrEven = "odd";
                        if (j % 2 == 0) {
                          oddOrEven = "even";
                        }

                        var constraintFound = null;

                        if (provisionalConstraints != null) {
                          for (var m = 0; m < provisionalConstraints.length; m++) {
                            var constraint = provisionalConstraints[m];
                            if (constraint.value == values[j].value && constraint.componentName == component) {
                              constraintFound = constraint;
                            }
                          }
                        }
                        if (constraintFound != null) {
                          compValuesHtml +=
                            '<div ' +
                            'style="width: 468px;" ' +
                            'class="widget_grid details ' + oddOrEven + '" ' +
                            'compValue="' + values[j].value + '" ' +
                            'dbId="' + constraintFound.id + '" ' +
                            'compValueName="' + values[j].name + '">' +
                            '<div ' +
                            'onclick="javascript:onClickOfWidgetCheckbox(this);" ' +
                            'class="widget_checkbox widget_checkbox_wide">' +
                            '<span class="checked"></span>' +
                            '</div>' +
                            '<div ' +
                            'style="margin:0; width: 400px; padding-bottom: 5px;" ' +
                            'class="widget_grid_description">' +
                            '<span ' +
                            'class="ellipsis" ' +
                            'style="width: 390px;" ' +
                            'title="' + values[j].name + '"><strong>' + values[j].name + '</strong>' +
                            '</span>' +
                            '<div> ' +
                            '<span class="descp ellipsis subtxt">' + extras + '</span>' +
                            '<a class="morelink" href="javascript:void(0);" style="display:' + displayMoreLink +
                            ';">'+commmonmessages.MORE+'</a>' +
                            '<a style="display: none" class="lesslink" href="javascript:void(0);">'+commmonmessages.LESS+'</a>' +
                            '</div>' +
                            '</div>' +
                            '</div>';
                        } else {
                          compValuesHtml +=
                            '<div ' +
                            'style="width: 468px;" ' +
                            'class="widget_grid details ' + oddOrEven + '" ' +
                            'compValue="' + values[j].value + '" ' +
                            'dbId="-1" ' +
                            'compValueName="' + values[j].name + '">' +
                            '<div ' +
                            'onclick="javascript:onClickOfWidgetCheckbox(this);" ' +
                            'class="widget_checkbox widget_checkbox_wide">' +
                            '<span class="unchecked"></span>' +
                            '</div>' +
                            '<div ' +
                            'style="margin:0; width: 400px; padding-bottom: 5px;" ' +
                            'class="widget_grid_description">' +
                            '<span ' +
                            'class="ellipsis" ' +
                            'style="width: 390px;" ' +
                            'title="' + values[j].name + '"><strong>' + values[j].name + '</strong>' +
                            '</span>' +
                            '<div> ' +
                            '<span class="descp ellipsis subtxt">' + extras + '</span>' +
                            '<a class="morelink" href="javascript:void(0);">'+commmonmessages.MORE+'</a>' +
                            '<a style="display: none" class="lesslink" href="javascript:void(0);">'+commmonmessages.LESS+'</a>' +
                            '</div>' +
                            '</div>' +
                            '</div>';
                        }

                      }
                    }

                    //push footer
                    compValuesHtml += '<div class="error" style="display: none" id="componentsError[' + "'" +
                      component + "'" + ']"></div>';

                    compValBlock.find("#compValuesList").html(compValuesHtml);
                    $("#componentValues").append(compValBlock);
                  },
                  error: function(XMLHttpRequest) {
                	$("#spinning_wheel_cpb").hide();
                	var errorObject = CPBM.parseJsonSafely(XMLHttpRequest.responseText);
                    popUpDialogForAlerts("dialog_info", errorObject.detailedMessage);
                    errorInFetchingValues = true;
                  }, 
                  complete: function(){
                	  //Adding this to override Ajaxsetup default alert
                  }
                });
                if (errorInFetchingValues) {
                  break;
                }
              }
              if (errorInFetchingValues) {
                break;
              }        
            }

            $("#bundleComponents").append(compListHtml);
          },
          error: function(XMLHttpRequest) {
        	$("#spinning_wheel_cpb").hide();
            popUpDialogForAlerts("dialog_info", XMLHttpRequest.responseText);
          }
        });
        $("#spinning_wheel_cpb").hide();
        
        // if error in ajax calls, then don't show next step
        if(errorInFetchingValues) {
        	return false;
        }

        var countOfActiveComponentSelects = 0;
        $("#bundleComponents").find("li[id^='componentLeftPanel_']").each(function() {
          if ($(this).hasClass("active")) {
            countOfActiveComponentSelects += 1;
          }
        });
        if ($("#step3AlreadyReached").val() == "false" || countOfActiveComponentSelects == 0) {
          $("#bundleComponents").find("li[id^='componentLeftPanel_']:first").click();
          $("#step3AlreadyReached").val("true");
        }
      }
      $("#step2SelectionChanged").val("false");
      $("#step3EntriesFetched").val("true");
    }


    if (currentstep == "step3" && bundle_action == "create") {
      execstep4();
    }

    if ((currentstep == "step5" && bundle_action == "create") ||
      (currentstep == "step4" && bundle_action == "edit")) {
      var components = $('*[name^=components]');

      for (var i = 0; i < components.length; i++) {
        var name = components[i].name;
        var component = name.replace(/^components\[(.*)\]$/gm, '$1');
        var componentName = component.split("'")[1];
        $("#componentsError\\[\\'" + componentName + "\\'\\]").hide();
        var association = $("#associations\\[\\'" + componentName + "\\'\\]");
        //TODO  Remove hardcore
        //XXX   This means includes none is not allowed
        if (association.val().toLowerCase() == "excludes")
          if ($.trim(components[i].value) == "") {
            $("#componentsError\\[\\'" + componentName + "\\'\\]").show();
            $("#componentsError\\[\\'" + componentName + "\\'\\]").text(i18n.errors.nullinclusionerror);
            $(".j_productspopup").hide();
            $("#step4").show();
            return;
          }
      }

      var prodBundleName = $("#productBundle\\.name").val();
      var prodBundleNameToDisplay = "";
      var size = prodBundleName.length;
      var maxsize = 50;
      var count = 0;
      while (size > 50) {
        prodBundleNameToDisplay += prodBundleName.substring(count, count + maxsize) + "<br>";
        count = count + maxsize;
        size = size - 50;
      }
      prodBundleNameToDisplay += prodBundleName.substring(count) + "<br>";
      if (bundle_action == "create") {
        $("#compAssociationJson").val(JSON.stringify(getCompAssociationValues()));
      } else {
        $("#compAssociationJson").val(JSON.stringify(getCompAssociationValuesOnEdit()));
      }
      if (bundle_action == "create")
        $step6.find("#successmessage").append(prodBundleNameToDisplay);
      else {
        $step5.find("#successmessage").append(prodBundleNameToDisplay);
      }
      $("#serviceinstanceuuid").val($("#instances").find(".instance_selected").attr("id"));

      $.ajax({
        type: "POST",
        url: $(bundleForm).attr('action'),
        data: $(bundleForm).serialize(),
        dataType: "json",
        async: false,
        success: function(bundle) {
          if (bundle_action == "create") {
            addBundleDetailsInListView(bundle);
          } else {
            editBundleDetailsInListView(bundle);
          }
          $(".j_bundlespopup").hide();
          $("#" + nextstep).show();

        },
        error: function(XMLHttpRequest) {
          if (XMLHttpRequest.status === INTERNAL_SERVER_FAILED_CODE) {
            displayAjaxFormError(XMLHttpRequest, "productBundleForm", "main_addnew_formbox_errormsg_popup");
            $(".j_productspopup").hide();
            $("#step1").show();
          }
        }
      });
    }

    if ((currentstep == "step6" && bundle_action == "create") ||
      (currentstep == "step5" && bundle_action == "edit")) {
      $currentDialog.dialog("close");
      $("#dialog_add_bundle").empty();
      $("#dialog_edit_bundle").empty();
      //if( $("#productBundlegridcontent").find(".j_viewbundle").size() == 1){
      //  window.location.reload();
      //}
    } else {
      $(".j_bundlespopup").hide();
      $("#" + nextstep).show();
    }
  }

}

function execstep4() {
  if ($("#chargeType option:selected").val() != "NONE") {
    $("#recurringCharges").show();
  }
  $("#provisioningConstraintError").text("");
}

function getCompAssociationValues() {
  var compAssociations = new Array();
  $("#componentValues").find("div[id^='componenttypeValues']").each(function() {
    var association = "INCLUDE";
    $(this).find(".discountTypeRadio").each(function() {
      if ($(this).attr("checked") == "checked") {
        association = $(this).val();
      }
    });
    var compValues = new Array();
    $(this).find("#compValuesList").find("div[class^='widget_grid details']").each(function() {
      if ($(this).find(".widget_checkbox").find("span").attr("class") == "checked") {
        var nameValMap = {};
        nameValMap["compName"] = $(this).attr("compvalue");
        nameValMap["compValueName"] = $(this).attr("compValueName");
        compValues.push(nameValMap);
      }
    });
    var compName = $(this).attr("id").substr("componenttypeValues.".length);
    if (compValues.length > 0) {
      var compAssociationAndValuesObj = new Object();
      compAssociationAndValuesObj.association = association;
      compAssociationAndValuesObj.compValues = compValues;
      compAssociationAndValuesObj.compName = compName;

      compAssociations.push(compAssociationAndValuesObj);
    }
  });
  return compAssociations;
}

function getCompAssociationValuesOnEdit() {
  var compAssociations = new Array();
  $("#componentValues").find("div[id^='componenttypeValues']").each(function() {
    var association = "INCLUDE";
    $(this).find(".discountTypeRadio").each(function() {
      if ($(this).attr("checked") == "checked") {
        association = $(this).val();
      }
    });
    var compValues = new Array();
    $(this).find("#compValuesList").find("div[class^='widget_grid details']").each(function() {
      if ($(this).find(".widget_checkbox").find("span").attr("class") == "checked") {
        var valIdNameMap = {};
        valIdNameMap["val"] = $(this).attr("compvalue");
        valIdNameMap["id"] = $(this).attr("dbId");
        valIdNameMap["valName"] = $(this).attr("compValueName");
        compValues.push(valIdNameMap);
      }
    });
    var compName = $(this).attr("id").substr("componenttypeValues.".length);
    if (compValues.length > 0) {
      for (var k = 0; k < compValues.length; k++) {
        var compAssociationAndValuesObj = new Object();
        compAssociationAndValuesObj.association = association;
        compAssociationAndValuesObj.compValue = compValues[k]["val"];
        compAssociationAndValuesObj.compValueName = compValues[k]["valName"];
        compAssociationAndValuesObj.compDbId = compValues[k]["id"];
        compAssociationAndValuesObj.compName = compName;

        compAssociations.push(compAssociationAndValuesObj);
      }
    }
  });
  return compAssociations;
}

function addNewProductBundleGet() {
  initDialog("dialog_add_bundle", 900);
  var actionurl = productBundlesUrl + "/create";
  $.ajax({
    type: "GET",
    url: actionurl,
    dataType: "html",
    data: {
      "serviceInstanceUUID": $("#instances").find(".instance_selected").attr("id")
    },
    success: function(html) {
      var $thisDialog = $("#dialog_add_bundle");
      $thisDialog.html("");
      $thisDialog.html(html);
      $thisDialog.bind("dialogbeforeclose", function(event, ui) {
        $thisDialog.empty();
      });
      $currentDialog = $thisDialog;
      $currentDialog.dialog('open');
      $currentDialog.find('#productBundle\\.name').focus();
    },
    error: function(xhr) {
      if (xhr.status == 503) {
        //CloudService Exception
        var errorMessage = null;
        var errorObjet = CPBM.parseJsonSafely(xhr.responseText);
        if(errorObjet != null){
          errorMessage = errorObjet.message;
        }else{
          errorMessage = xhr.responseText;
        }
        popUpDialogForAlerts("dialog_info", errorMessage);
      }
      $(".addnewproductBundle").unbind('click');
    }, 
    complete: function(){
      //Adding this to override Ajaxsetup default alert
    }
  });
}

var currentDetailView;

function editProductBundleGet(current) {
  $("#top_actions").find("#spinning_wheel").show();
  initDialog("dialog_edit_bundle", 785);
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
      var $thisDialog = $("#dialog_edit_bundle");
      $thisDialog.html("");
      $thisDialog.html(html);
      var $thisDialog = $("#dialog_edit_bundle");
      $currentDialog = $thisDialog;
      $("#top_actions").find("#spinning_wheel").hide();
      $thisDialog.bind("dialogbeforeclose", function(event, ui) {
        $thisDialog.empty();
      });
      $currentDialog.dialog('open');
    },
    error: function() {
      $("#top_actions").find("#spinning_wheel").hide();
    }
  });
}
/**
 * After creating new bundle add details in the starting of list grid,
 * @param bundle
 */

function addBundleDetailsInListView(bundle) {

  var $bundleListTemplate = $("#bundleviewtemplate").clone();
  $bundleListTemplate.attr('id', "row" + bundle.id);
  var isOdd = $("#productBundlegridcontent").find(".j_viewbundle:first").hasClass('odd');
  if (isOdd == true) {
    $bundleListTemplate.addClass('even');
  } else {
    $bundleListTemplate.addClass('odd');
  }
  $bundleListTemplate.addClass('selected');
  $bundleListTemplate.addClass('active');
  //TODO: fixme
  // if(bundle.type == 'COMPUTE'){
  $bundleListTemplate.find("#nav_icon").addClass('computebundles');
  // }else{
  //   $bundleListTemplate.find("#nav_icon").addClass('servicebundles');
  // }

  $bundleListTemplate.find(".widget_navtitlebox").find('.title').text(bundle.name);
  $bundleListTemplate.find(".widget_navtitlebox").find('.subtitle').text(i18nText(bundle.rateCard.chargeType.displayName));
  if (bundle.publish == 'true') {
    $bundleListTemplate.find(".widget_info_popover").find('.raw_contents').find("#info_bubble_displayname").find(
      '.raw_contents_value').find("#bundle_publish_status").text(i18n.label.productbundle.TrueText);
  } else {
    $bundleListTemplate.find(".widget_info_popover").find('.raw_contents').find("#info_bubble_displayname").find(
      '.raw_contents_value').find("#bundle_publish_status").text(i18n.label.productbundle.FalseText);
  }

  $bundleListTemplate.find(".widget_info_popover").find('.raw_contents').find("#info_bubble_code").find(
    '.raw_contents_value').find("#value").text(bundle.code);

  $bundleListTemplate.show();
  $("#productBundlegridcontent").prepend($bundleListTemplate);
  var bundlesCount = $("#productBundlegridcontent").find(".j_viewbundle").size();
  //remove last element if count grater than pagination value
  if (bundlesCount > perPageValue) {
    $("#productBundlegridcontent").find(".j_viewbundle:last").remove();
  }
  $("#productBundlegridcontent").find("#non_list").remove();

  //reset styling
  resetGridRowStyle();
  $("#productBundlegridcontent").find(
    ".j_viewbundle:first").click();

}

/**
 * Edit bundle details
 */

function editBundleDetailsInListView(bundle) {
  var $bundleListTemplate = $("#productBundlegridcontent").find("#row" + bundle.id);
  $bundleListTemplate.find("#nav_icon").removeClass('computebundles');
  $bundleListTemplate.find("#nav_icon").removeClass('servicebundles');
  //  if(bundle.type === 'COMPUTE'){
  $bundleListTemplate.find("#nav_icon").addClass('computebundles');
  //  }else{
  //    $bundleListTemplate.find("#nav_icon").addClass('servicebundles');
  //  }
  $bundleListTemplate.find(".widget_navtitlebox").find('.title').text(bundle.name);
  $bundleListTemplate.find(".widget_navtitlebox").find('.subtitle').text(i18nText(bundle.rateCard.chargeType.displayName));
  if (bundle.publish == 'true') {
    $bundleListTemplate.find(".widget_info_popover").find('.raw_contents').find("#info_bubble_displayname").find(
      '.raw_contents_value').find("#value").text(i18n.label.productbundle.TrueText);
  } else {
    $bundleListTemplate.find(".widget_info_popover").find('.raw_contents').find("#info_bubble_displayname").find(
      '.raw_contents_value').find("#value").text(i18n.label.productbundle.FalseText);
  }
  $bundleListTemplate.find(".widget_info_popover").find('.raw_contents').find("#info_bubble_code").find(
    '.raw_contents_value').find("#value").text(bundle.code);
  $("#productBundlegridcontent").find("#row" + bundle.id).click();
}



/**
 * Remove productBundle (GET)
 */

function removeProductBundle(current) {
  initDialog("common_dialog", 350, false);
  var $commonDialog = $("#common_dialog");
  $commonDialog.find("#helptext").empty();
  $commonDialog.find("#helptext").html(i18n.errors.bundleDeletebundleConfirmation);
  //$("#common_dialog").dialog('option', 'buttons', getConfirmationDialogButtons("removeplannedcharges")).dialog("open");
  $commonDialog.dialog('option', 'buttons', {
    "OK": function() {
      var divId = $(current).attr('id');
      var ID = divId.substr(6);
      var actionurl = productBundlesUrl + "remove";
      $.ajax({
        type: "GET",
        url: actionurl,
        data: {
          Id: ID
        },
        dataType: "html",
        async: false,
        success: function(html) {
          if (html == "success") {
            $("#productBundlegridcontent").find("#row" + ID).remove();
            $("#productBundlegridcontent").find(".j_viewbundle:first").click();
            $commonDialog.dialog('close');
            if ($("#productBundlegridcontent").find(".j_viewbundle").size() == 0) {
              $("#product_bundle_tab").click();
            }
          } else {
            popUpDialogForAlerts("dialog_info", i18n.errors.bundleDeletebundleFailure);
            $commonDialog.dialog('close');
          }

        },
        error: function() {
          popUpDialogForAlerts("dialog_info", i18n.errors.bundleDeletebundleFailure);
          $commonDialog.dialog('close');
        }
      });
    },
    "Cancel": function() {
      $commonDialog.dialog('close');
    }
  });
  dialogButtonsLocalizer($commonDialog, {
    'OK': g_dictionary.dialogOK,
    'Cancel': g_dictionary.dialogCancel
  });
  $commonDialog.dialog("open");

}

/**
 * Publish productBundle (GET)
 */

function publishBundle(current, publish, bundleId, bundleCode) {
  initDialog("common_dialog", 350, false);
  var $commonDialog = $("#common_dialog");
  $commonDialog.find("#helptext").empty();
  if (publish == "true") {
    $commonDialog.find("#helptext").html(
      i18n.errors.bundlePublishConfirmation);
  } else {
    $commonDialog.find("#helptext").html(
      i18n.errors.bundleunpublishConfirmation);
  }
  $commonDialog.dialog('option', 'buttons', {
    "OK": function() {
      var actionurl = productBundlesUrl + "publish";
      $.ajax({
        type: "POST",
        url: actionurl,
        data: {
          bundleCode: bundleCode,
          publish: publish
        },
        dataType: "html",
        success: function(html) {
          if (html == "success") {
            viewBundleById(bundleId, false);
            var publishStatus = $(".widget_navigationlist.selected #bundle_publish_status");
            if (publish == "true") {
              publishStatus.text(i18n.label.productbundle.TrueText);
              $("#top_message_panel").find("#msg").html(
                i18n.label.productbundle.published);
            } else {
              publishStatus.text(i18n.label.productbundle.FalseText);
              $("#top_message_panel").find("#msg").html(
                i18n.label.productbundle.unpublished);
            }

            $("#top_message_panel").removeClass("error").addClass("success")
              .show();
          } else if (html == "norcc") {
            popUpDialogForAlerts("dialog_info", i18n.errors.bundleNotPublishNoCurrentCharges);
          } else {
            popUpDialogForAlerts("dialog_info", i18n.errors.bundleNotPublishedError);
          }
        },
        error: function() {
          popUpDialogForAlerts("dialog_info", i18n.errors.bundleNotPublishedError);
        }
      });
      $commonDialog.dialog('close');
    },
    "Cancel": function() {
      $commonDialog.dialog('close');
    }
  });
  dialogButtonsLocalizer($commonDialog, {
    'OK': g_dictionary.dialogOK,
    'Cancel': g_dictionary.dialogCancel
  });
  $commonDialog.dialog("open");
}

/**
 * Reset data row style
 * @return
 */

function resetGridRowStyle() {
  $(".widget_navigationlist").each(function() {
    $(this).removeClass("selected");
    $(this).removeClass("active");
  });
}

function deleteRateCard(current, rateCard) {
  var divId = $(current).attr('id');
  var ID = divId.substr(6);
  var url = "/portal/portal/productBundles/deleteratecard";
  $.ajax({
    type: "GET",
    url: url,
    data: {
      id: ID
    },
    dataType: "html",
    success: function(html) {
      if (html == "success") {
        $("#futureRateCardDiv").remove();
      }
    },
    error: function() {}
  });
}

function getConfirmationDialogButtonsForEntitlement(current) {

  var buttonCallBacks = {};

  var divId = $(current).attr('id');
  var ID = divId.substr(8);
  var url = productBundlesUrl + "entitlement/" + ID + "/delete";
  var bundleId = $("#bundleId").attr('value');

  buttonCallBacks[commmonmessages.lightboxbuttonconfirm] = function() {
    $(this).dialog("close");
    $.ajax({
      type: "GET",
      url: url,
      data: {
        bundleId: bundleId
      },
      dataType: "json",
      success: function(jsonResponse) {
        if (jsonResponse != null) {
          var entitlementscurrentPageRecords = Number($("#entitlementscurrentPageRecords").val());
          var entitlementsCurrentPage = Number($("#entitlementsCurrentPage").val());
          var bundleCode = $('#bundleCode').val();

          if (entitlementscurrentPageRecords == 1 && entitlementsCurrentPage != 1) {
            entitlementsCurrentPage = entitlementsCurrentPage - 1;
          }
          if ($("#whichPlan").val() == "history") {
            if ($("#rpb_history_dates option:selected").val().trim() != "") {
              viewEntitlements($("#whichPlan").val(), bundleCode, entitlementsCurrentPage, $(
                "#rpb_history_dates option:selected").val().trim());
            }
          } else {
            viewEntitlements($("#whichPlan").val(), bundleCode, entitlementsCurrentPage);
          }
        }
      },
      error: function() {
        popUpDialogForAlerts("dialog_info", i18n.errors.bundleDeleteentitlementFailed);
      }
    });

  };

  buttonCallBacks[commmonmessages.lightboxbuttoncancel] = function() {
    $(this).dialog("close");
  };

  return buttonCallBacks;
}

function deleteEntitlement(current) {
  initDialog("dialog_confirmation", 380, false);
  var title = commmonmessages.lightboxremoveentitlementTitle;
  $("#dialog_confirmation").html("<br>&nbsp;&nbsp;&nbsp;&nbsp;" +
    commmonmessages.lightboxremoveentitlement).dialog('option', 'buttons', getConfirmationDialogButtonsForEntitlement(
    current))
    .dialog('option', 'title', title).dialog("open");

}

function resetProductsList(jsonResponse, action) {
  if (action == "add") {
    var option = "<option value='" + jsonResponse.id + "' >" + jsonResponse.name + "&nbsp;" + jsonResponse.uom +
      "</option>";
    $('#productId').append(option);
  } else {
    $("#productId option[value='" + jsonResponse + "']").remove();
  }

}

function createentitlement(element) {
  var current_entitlement_row = $(".currentEntitlementrow").attr('id');
  current_entitlement_row = Number(current_entitlement_row.substr(3));
  current_entitlement_row = current_entitlement_row + 1;
  var url = "/portal/portal/productBundles/create/entitlement";

  $.ajax({
    type: "GET",
    url: url,
    data: {
      current_entitlement_row: current_entitlement_row
    },
    dataType: "html",
    success: function(html) {
      $("#entitlementDiv").append(html);
      $('#add_entitlement').replaceWith(
        '<a class="deleteentitlement" href="javascript:void(0);" onclick="deleteEntitlement(this)" id="delete' +
        (current_entitlement_row - 1) + ' >Delete</a>');

      $(".currentEntitlementrow").attr('id', "row" + current_entitlement_row);
    },
    error: function() {
      $("#entitlementDiv").unbind('click');
    }
  });

}


function saveEntitlement(current) {
  var divId = $(current).attr('id');
  var ID = divId.substr(6);
  var newvalue = $("#value" + ID).val();
  var newunlimitedvalue = $("#unlimitedUsage" + ID).is(":checked");
  var intRegex = /^\d+$/;

  if (newvalue == null || newvalue.length == 0) {
    $("#valueerror" + ID).html(i18n.errors.bundleIncludedUnitsRequired);
    return true;
  } else if (isNaN(Number(newvalue)) || !intRegex.test(newvalue) || (Number(newvalue) < 0 && newunlimitedvalue == false)) {
    $("#valueerror" + ID).html(i18n.errors.bundleIncludedUnitsPositiveInteger);
    return true;
  } else if (!validatePositiveJavaInteger(newvalue)) {
    $("#valueerror" + ID).html(i18n.errors.bundleIncludedUnitsPositiveJavaInteger);
    return true;
  } else {
    if (newunlimitedvalue == true) {
      newvalue = "-1";
    }
    $("#valueerror" + ID).html("");
    var url = productBundlesUrl + "/entitlement/" + ID + "/save";
    $.ajax({

      type: "POST",
      url: url,
      data: {
        includedUnits: newvalue
      },
      dataType: 'json',
      success: function(jsonresponse) {
        if (jsonresponse != null) {
          var bundleCode = $('#bundleCode').val();
          viewEntitlements("planned", bundleCode, currentPage);
        } else {
          $("#valueedit" + ID).hide();
          $("#unlimitedvalueedit" + ID).hide();
          $("#valuenotedit" + ID).show();
          $("#savepe" + ID).hide();
          $("#editpe" + ID).show();
          $("#deletepe" + ID).show();
          $("#cancelpe" + ID).hide();
        }
      },
      error: function(request) {
        $("#valueedit" + ID).hide();
        $("#valuenotedit" + ID).show();
        $("#savepe" + ID).hide();
        $("#editpe" + ID).show();
        $("#deletepe" + ID).show();
        $("#cancelpe" + ID).hide();
      }
    });
  }
}

function editEntitlement(current) {
  var divId = $(current).attr('id');
  var ID = divId.substr(6);
  $("#valuenotedit" + ID).hide();
  $("#valueedit" + ID).show();
  $("#unlimitedvalueedit" + ID).show();
  var newunlimitedvalue = $("#unlimitedUsage" + ID).is(":checked");
  if (newunlimitedvalue == true) {
    $("#value" + ID).attr("disabled", "disabled");
    $("#value" + ID).val('0');
  }
  $("#editpe" + ID).hide();
  $("#deletepe" + ID).hide();
  $("#savepe" + ID).show();
  $("#cancelpe" + ID).show();
  $("#value" + ID).data(i18n.errors.bundleEntitlementReset, $("#value" + ID).val());
  $("#valueerror" + ID).html("");
}

function cancelEntitlement(current) {
  var divId = $(current).attr('id');
  var ID = divId.substr(8);
  $("#value" + ID).val($("#value" + ID).data(i18n.errors.bundleEntitlementReset));
  $("#valueedit" + ID).hide();
  $("#unlimitedvalueedit" + ID).hide();
  $("#valuenotedit" + ID).show();
  $("#savepe" + ID).hide();
  $("#editpe" + ID).show();
  $("#deletepe" + ID).show();
  $("#cancelpe" + ID).hide();
}

$("#productBundleForm").validate({
  // debug : true,
  success: "valid",
  ignoreTitle: true,
  rules: {
    "productBundle.code": {
      required: true,
      noSpacesAllowed: true,
      maxlength: 64,
      xRemote: {
        condition: function() {
          return $("#productBundle_code").val() != $(
            "#productBundle\\.code").val() && ($("#productBundle\\.name").val() != "");
        },
        url: '/portal/portal/products/validateCode',
        async: false
      }
    },
    "productBundle.name": {
      required: true,
      maxlength: 255,
    },
    "productBundle.description": {
      maxlength: 4000
    },
    "resourceType": {
      required: true
    },
    "productBundle.businessConstraint": {
      required: true
    },
    "chargeType": {
      required: true
    }
  },
  messages: {
    "productBundle.name": {
      required: i18n.errors.bundleBundleFormProvideName,
      maxlength: i18n.errors.maxlength,
    },
    "productBundle.description": {
      maxlength: i18n.errors.maxlength
    },
    "productBundle.code": {
      required: i18n.errors.bundleBundleFormProvideCode,
      noSpacesAllowed: i18n.errors.bundlecodevalid,
      xRemote: i18n.errors.codeNotUnique,
      remote: i18n.errors.codeNotUnique
    },
    "resourceType": {
      required: i18n.errors.bundleresourcetyperequired
    },
    "productBundle.businessConstraint": {
      required: i18n.errors.bundlebusinessconstraintrequired
    },
    "chargeType": {
      required: i18n.errors.bundlechargetyperequired
    }
  },
  errorPlacement: function(error, element) {
    var name = element.attr('id');
    name = ReplaceAll(name, ".", "\\.");
    if (name != "") {
      error.appendTo("#" + name + "Error");
    } else {
      error.appendTo("#miscFormErrors");
    }
  }
});


jQuery.validator.setDefaults({
  onfocusout: false,
  onkeyup: false,
  onclick: false
});

/*$.validator.addMethod("numberRequired",
			$.validator.methods.number,
			"Please enter a valid number.");
	*/
$.validator.addClassRules("numberRequired", {
  includedunits: true
});


$.validator.addClassRules("priceRequired", {
  required: {
        depends:function(){
            $(this).val($.trim($(this).val()));
            return true;
        }
    },
  number: true,
  twoDecimal: true,
  maxcurrencyPrecision: true
});

$.validator.addMethod("maxcurrencyPrecision", function(value, element) {
  $(element).rules("add", {
      number: true
    });
if (!/^(?:\d*\.\d*\|\d+)$/.test(value)) {
  var sval=value;
  var sdecimalDigit=sval.split(".");
  if(sdecimalDigit.length==2){// have decimal
     if(sdecimalDigit[1].replace(/0+$/, "").length>currPrecision){//truncate trailing zeros from decimal part
        // more characters allowed precision
       return false;
     }
     return true;
     }else if(sdecimalDigit.length>2) {
  //more than one decimals   
      return false;
 } 
     // without decimal
      return true;     
            
}else{//not a valid digit pattern
   return false;
  } 
 },
 i18n.errors.max_four_decimal_value);

$.validator
  .addMethod(
    "includedunits",
    function(value, element) {
      $(element).rules("add", {
        number: true
      });
      productType = $(element).parent()
        .prevAll()
        .find(".productSelect");
      isIncludedUnits = (productType.val() == "-1" || value != "");
      if (isIncludedUnits == false) {
        return false;
      }
      return true;
    },
    i18n.errors.bundleEnterValidIncludedUnits);

$.validator
  .addMethod(
    "positiveInteger",
    function(value, element) {
      return validatePositiveInteger(value);
    },
    i18n.errors.bundleIncludedUnitsPositiveInteger);

$.validator
  .addMethod(
    "twoDecimal",
    function(value, element) {
      isPriceValid = value != "" && isNaN(value) == false && Number(value) >= 0 && Number(value) <= 99999999.9999;
      if (isPriceValid == false) {
        return false;
      }
      return true;
    },
    i18n.errors.decimal_range);

// a custom method for validating the date range
$.validator
  .addMethod(
    "dateRange",
    function() {
      var endDate = $("#rateCard\\.endDate").val();
      var startDate = $("#rateCard\\.startDate").val();



      if (endDate == null || endDate == "") {
        return true;
      } else {
        return Date.parse(startDate) < Date.parse(endDate);
      }
    },
    i18n.errors.bundleEnterValidDateRange);

$.validator
  .addMethod(
    "futuredate",
    function(value, element) {
      var now = new Date();
      now.setHours(0, 0, 0, 0);
      return Date.parse(value) > Date.parse(now);
    },
    i18n.errors.bundleEnterValidStartDate);
$.validator
  .addMethod(
    "currentdate",
    function(value, element) {
      if (value == null || value == "") {
        return true;
      } else {

        var now = new Date();
        now.setHours(0, 0, 0, 0);
        return Date.parse(value) >= Date.parse(now);
      }

    },
    i18n.errors.bundleEnterValidEndDate);

function validateRateCardForm(event, form) {
  $("#rateCardForm").valid();
}

function hypervisorCheck(element) {
  isDisabled = $('#productBundle\\.hypervisor option').attr('disabled');
  if (isDisabled) {
    $('#productBundle\\.hypervisorError').html(i18n.errors.bundleSelectHypervisor);
  } else {
    $('#productBundle\\.hypervisorError').html('');
  }
}

function hypervisorChanged(element) {
  //console.log("element is",element);
  nameOfHypervisor = element.value;
  productOptions = $("." + nameOfHypervisor + "_hypervisor");
  //console.log(nameOfHypervisor);
  //console.log(productOptions);
  $('.productSelect').each(
    function() {
      //console.log($(this).find("option:selected"));
      if ($(this).find("option:selected").hasClass(nameOfHypervisor + "_hypervisor") == false && $(this).find(
        "option:selected").hasClass("_hypervisor") == false) {
        //console.log("need change...." , this.value);
        $(this).val("");
        this.value = "";
      }
    });
  if (nameOfHypervisor == "") {
    $('.productSelect').children().show();
  } else {
    $('.productSelect').children().hide();
    $('._hypervisor').show();
    productOptions.show();
  }
}

function productChanged() {
  nameOfHypervisor = $('#productBundle\\.hypervisor').val();
  //console.log(nameOfHypervisor);
  //$('.productSelect option[value=2]').hide()

  templatesOfThisHypervisorFound = false;
  if (nameOfHypervisor != "") {
    $('.productSelect').find("option:selected").each(
      function() {
        if ($(this).hasClass(nameOfHypervisor + "_hypervisor")) {
          //							console.log(nameOfHypervisor);
          //							console.log(this);
          //							console.log("    found");
          templatesOfThisHypervisorFound = true;
        } else {
          //							console.log("    active hy-menu");
        }
        if (templatesOfThisHypervisorFound || $('.category_LICENSES').size() > 0) {
          $('#productBundle\\.hypervisor option').attr('disabled', 'disabled');
        } else {
          $('#productBundle\\.hypervisor option').attr('disabled', '');
        }
      }
    );
  }
}


/**
 * Add new productBundle(POST)
 * @param event
 * @param form
 * @return
 */

function addNewEntitlement(event, form) {
  var bundleCode = $("#bundleCode").val();
  $("#entitlement_spinning_wheel").show();
  if (event.preventDefault) {
    event.preventDefault();
  } else {
    event.returnValue = false;
  }
  var returnStatus = true;
  var page = Number($("#entitlementsPages").val());
  var entitlementPerPage = $("#entitlementsPerPage").val();
  var entitlementscurrentPageRecords = $("#entitlementscurrentPageRecords").val();
  var entitlementsCurrentPage = $("#entitlementsCurrentPage").val();
  if (entitlementsCurrentPage == page && entitlementscurrentPageRecords == entitlementPerPage) {
    page = page + 1;
  }
  //var actionurl = productBundlesUrl + bundleCode + "/entitlement/create";
  if ($("#entitlementForm").valid()) {
    $.ajax({
      type: "POST",
      url: $("#entitlementForm").attr('action'),
      data: $("#entitlementForm").serialize(),
      dataType: "html",
      success: function(html) {
        viewEntitlements("planned", bundleCode, page);
        $("#productId").val("");
        $("#entitlement\\.includedUnits").attr("value", "");
        $("#unlimitedUsage").attr("checked", false);
        $("#entitlement_spinning_wheel").hide();
      },
      error: function(html) {
        if (html.status == 300) {
          popUpDialogForAlerts("dialog_info", i18n.errors.entitilement.alreadyadded);
        }
        returnStatus = false;
        $("#entitlement_spinning_wheel").hide();
      },
      complete: function(jqXHR, textStatus) {}
    });
  } else {
    returnStatus = false;
    $("#entitlement_spinning_wheel").hide();
  }
  return returnStatus;
}

function viewEntitlements(whichPlan, bundleCode, currentPage, filterDate) {
  $("#top_message_panel").hide();
  var url = productBundlesUrl + bundleCode + "/entitlements/view_filtered";
  var data = {};
  data["whichPlan"] = whichPlan;
  data["currentPage"] = currentPage;
  if (filterDate != undefined) {
    data["filterDate"] = filterDate;
  }
  $.ajax({
    type: "GET",
    url: url,
    data: data,
    dataType: "html",
    async: false,
    cache: false,
    success: function(html) {
      $("#entitlements_content").html(html);
      $("#entitlement\\.includedUnits").val("");
    },
    error: function() {
      // need to do
    }
  });
}
/**
 * Edit productBundle (GET)
 */

function addNewEntitlementGet(event, current) {
  var bundleCode = $('#bundleCode').val();
  var actionurl = productBundlesUrl + bundleCode + "/entitlement/create";
  $.ajax({
    type: "GET",
    url: actionurl,
    dataType: "html",
    success: function(html) {

      var $thisDialog = $("#addEntitlement_div");
      $thisDialog.empty();
      $thisDialog.html(html);
      $thisDialog.dialog('option', 'buttons', {
        "OK": function() {
          var status = addNewEntitlement(event, current, bundleCode);
          if (status) {
            $(this).dialog("close");
          }
        },
        "Cancel": function() {
          $(this).dialog("close");
        }
      }).dialog("open");
    },
    error: function() {
      $(".j_add_entitlement_link").unbind('click');
    }
  });
}

/**
 * Creates new productBundle row
 */
$.createNewProductBundle = function(jsonResponse) {

  if (jsonResponse == null) {
    popUpDialogForAlerts("dialog_info", i18n.errors.bundleBundleCreationFailed);
  } else {
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
    content = content + "<div class='db_gridbox_columns' style='width:5%;'>";
    content = content + "<div class='db_gridbox_celltitles'>";
    content = content + jsonResponse.sortOrder;
    content = content + "</div>";
    content = content + "</div>";
    content = content + "<div class='db_gridbox_columns' style='width:5%;'>";
    content = content + "<a class='rowmove_icon' href='#' title='top'></a>";
    content = content + "</div>";
    content = content + "<div class='db_gridbox_columns' style='width:30%;'>";
    content = content + "<div class='db_gridbox_celltitles'>";
    content = content + jsonResponse.name;
    content = content + "</div>";
    content = content + "</div>";
    content = content + "<div class='db_gridbox_columns' style='width:40%;'>";
    content = content + "<div class='db_gridbox_celltitles'>";
    if (jsonResponse.description == null || jsonResponse.description == "") {
      content = content + "";
    } else {
      content = content + jsonResponse.description;
    }

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

function showInfoBubble(current) {
  if ($(current).hasClass('active')) return
  $(current).find("#info_bubble").show();
  return false;
}

function hideInfoBubble(current) {
  $(current).find("#info_bubble").hide();
  return false;
}

function nextClick(event) {
  $("#click_next").unbind("click", nextClick);
  $("#click_next").addClass("nonactive");

  currentPage = currentPage + 1;

  $("#click_previous").unbind("click").bind("click", previousClick);
  $("#click_previous").removeClass("nonactive");
  var searchPattern = $("#productBundleSearchPanel").val();
  fetchBundleList(currentPage, searchPattern);
}

function previousClick(event) {
  $("#click_previous").unbind("click", previousClick);
  $("#click_previous").addClass("nonactive");

  currentPage = currentPage - 1;

  $("#click_next").removeClass("nonactive");
  $("#click_next").unbind("click").bind("click", nextClick);
  var searchPattern = $("#productBundleSearchPanel").val();
  fetchBundleList(currentPage, searchPattern);
}

/**
 * Edit bundle image.
 * @param current
 * @param ID
 * @return
 */

function editBundleImageGet(current, ID) {
  initDialog("dialog_edit_bundle_image", 550);
  var actionurl = productBundlesUrl + "editlogo";
  $.ajax({
    type: "GET",
    url: actionurl,
    data: {
      Id: ID
    },
    async: false,
    dataType: "html",
    cache: false,
    success: function(html) {
      var $thisDialog = $("#dialog_edit_bundle_image");
      $thisDialog.empty();
      
      
      $thisDialog.dialog('option', 'buttons', {
        "OK": function() {
          if ($('#bundleLogoForm').valid()) {
            $('#bundleLogoForm').iframePostForm({
              iframeID: 'bundleLogoForm-iframe-post-form',
              json: true,
              post: function() {
                $("#bundleLogoForm-iframe-post-form").hide();
                return true;
              },
              complete: function() {
                updatebundlelogodetails($("#bundleLogoForm-iframe-post-form"), $thisDialog);
              }
            });
            $('#bundleLogoForm').submit();
            $('#bundleLogoForm').remove();
            
          }
        },
        "Cancel": function() {
          $("#dialog_edit_budle_image").empty();
          $thisDialog.dialog('close');
        }
      });
      $thisDialog.html(html);
      dialogButtonsLocalizer($thisDialog, {
        'OK': g_dictionary.dialogOK,
        'Cancel': g_dictionary.dialogCancel
      });
      $thisDialog.dialog("open");
    },
    error: function() {}
  });
}

function updatebundlelogodetails(current, dialog) {
  response = $(current).contents().find('body');
  if (response == null || response == "null" || response == "") {
    popUpDialogForAlerts("dialog_info", i18n.errors.failed_upload_image);
    return;
  }
  try {
    var pre = response.children('pre');
    if (pre.length) response = pre.eq(0);
    returnReponse = $.parseJSON(response.html());
    if(returnReponse.errormessage!=null){
        $("#logoError").text(returnReponse.errormessage);
      }else{
        $("#logoError").text("");
        dialog.dialog("close");
        
      }
    var date = new Date();
    $("#bundleimage" + returnReponse.id).attr('src', "/portal/portal/logo/productBundles/" + returnReponse.id + "?t=" +
      date.getMilliseconds());

  } catch (e) {
    popUpDialogForAlerts("dialog_info", response.html());
  }

}

var searchBundleRequest;

function searchProductBundleByName(event) {
  $("#click_previous").unbind("click", previousClick);
  $("#click_previous").addClass("nonactive");

  currentPage = 1;
  var searchPattern = $("#productBundleSearchPanel").val();

  $("#click_next").removeClass("nonactive");
  $("#click_next").unbind("click").bind("click", nextClick);

  var url = productBundlesUrl + "searchlist";

  var data = {};
  data["currentPage"] = currentPage;
  data["namePattern"] = searchPattern;
  data["serviceInstanceUUID"] = $("#instances").find(".instance_selected").attr("id");
  if ($("#whichPlan").val() == "history") {
    if ($("#rpb_history_dates option:selected").val().trim() == "") {
      return;
    }
    data["revisionDate"] = $("#rpb_history_dates option:selected").val().trim();
  }
  data["whichPlan"] = $("#whichPlan").val();

  data["filterBy"] = productBundlefilterby;
  if (searchBundleRequest && searchBundleRequest.readyState != 4) {
    searchBundleRequest.abort();
  }
  searchBundleRequest = $.ajax({
    type: "GET",
    url: url,
    dataType: "html",
    data: data,
    success: function(html) {
      $("#productbundlelist_div").html(html);
      $("#productBundlegridcontent").find(".j_viewbundle:first").click();
    },
    error: function() {
      // need to handle
    }
  });
}

function sortorder() {
  initDialog("dialog_sortorder_productbundle", 750);
  var actionurl = productBundlesUrl + "sortbundles";
  var data = {};
  data["whichPlan"] = $("#whichPlan").val();
  if ($("#whichPlan").val() == 'history' && $("#rpb_history_dates option:selected").val().trim() != "") {
    data["historyDate"] = $("#rpb_history_dates option:selected").val().trim();
  }
  data["filterBy"] = productBundlefilterby;
  data["serviceInstanceUUID"] = $("#instances").find(".instance_selected").attr("id");
  $.ajax({
    type: "GET",
    url: actionurl,
    data: data,
    async: false,
    dataType: "html",
    success: function(html) {
      var $thisDialog = $("#dialog_sortorder_productbundle");
      $thisDialog.empty();
      $thisDialog.html(html);
      $thisDialog.dialog('option', 'buttons', {
        "Ok": function() {
          $thisDialog.dialog("close");
          $("#product_bundle_tab").click();
        }
      });
      dialogButtonsLocalizer($thisDialog, {
        'Ok': g_dictionary.dialogOK
      });
      $thisDialog.dialog("open");
    },
    error: function() {}
  });

}

function viewChannelPricing(bundleCode, currentPage, showDialog, currenciesToDisplay) {
  $("#top_message_panel").hide();
  var url = productBundlesUrl + bundleCode + "/channelPricing";

  var data = {};
  var whichPlan = $("#whichPlan").val();
  if (whichPlan == "history") {
    data["historyDate"] = $("#rpb_history_dates option:selected").val().trim();
  }

  if (currenciesToDisplay !== undefined) {
    data['currenciesToDisplay'] = currenciesToDisplay;
  }
  data["whichPlan"] = whichPlan;
  data['currentPage'] = currentPage;
  data['perPageValue'] = perPageValue;
  $.ajax({
    type: "GET",
    url: url,
    data: data,
    dataType: "html",
    async: false,
    success: function(html) {
      if (showDialog === true) {
        initDialog("dialog_view_channle_bundle_pricing", 782);
        var $thisDialog = $("#dialog_view_channle_bundle_pricing");
        $thisDialog.html("");
        $thisDialog.html(html);
        $thisDialog.dialog('option', 'buttons', {
          "OK": function() {
            $(this).dialog("close");
            $thisDialog.empty();
          }
        });
        dialogButtonsLocalizer($thisDialog, {
          'OK': g_dictionary.dialogOK
        });
        $thisDialog.bind("dialogbeforeclose", function(event, ui) {
          $thisDialog.empty();
        });
        $thisDialog.find(".widget_details_actionbox").remove();
        $thisDialog.find(".widget_subactions.action_menu_container").remove();
        $thisDialog.find(".widget_grid_cell .moretabbutton").parent().remove();
        $thisDialog.dialog("open");
      } else {
        $("#channelpricing_content").html(html);
      }
    },
    error: function() {
      // need to do
    }
  });
}
var resourceCompDetailsMap = {};

function viewProvisioningConstarintContent(bundleCode, currentPage, showDialog) {
  $("#top_message_panel").hide();
  var url = productBundlesUrl + bundleCode + "/provisioningconstraints";

  var data = {};
  var whichPlan = $("#whichPlan").val();
  if (whichPlan == "history") {
    data["historyDate"] = $("#rpb_history_dates option:selected").val().trim();
  }

  data["whichPlan"] = whichPlan;
  $.ajax({
    type: "GET",
    url: url,
    data: data,
    dataType: "html",
    async: false,
    success: function(html) {
      if (showDialog === true) {
        initDialog("dialog_view_provisioningconstraint_content", 782);
        var $thisDialog = $("#dialog_view_provisioningconstraint_content");
        $thisDialog.html("");
        $thisDialog.html(html);
        $thisDialog.dialog('option', 'buttons', {
          "OK": function() {
            $(this).dialog("close");
            $thisDialog.empty();
          }
        });
        dialogButtonsLocalizer($thisDialog, {
          'OK': g_dictionary.dialogOK
        });
        $thisDialog.bind("dialogbeforeclose", function(event, ui) {
          $thisDialog.empty();
        });
        $thisDialog.find(".widget_details_actionbox").remove();
        $thisDialog.find(".widget_subactions.action_menu_container").remove();
        $thisDialog.find(".widget_grid_cell .moretabbutton").parent().remove();
        $thisDialog.dialog("open");
      } else {
        $("#provisioningconstraint_content").html(html);
        resourceCompDetailsMap = {};
        $(".js_resource_comp_details").popover();
        $(".js_resource_comp_details").each(function() {
        	$(this).attr("data-content", commmonmessages.fecthingDetails);
        });
        $(".js_resource_comp_details").bind("hover", function(){
        	var targetId = $(this).attr("id");
        	if(resourceCompDetailsMap[targetId] != null) {
        	      var html = resourceCompDetailsMap[targetId];
        	      $(".popover-content:visible").html(html);
        	      $(this).attr("data-content", html);
        	      return;
        	    }
        	$.ajax({
        	    url: productBundlesUrl + "getResourceComponentvalues.json",
        	    dataType: "text",
        	    async: true,
        	    cache: false,
        	    data:{
        	    	serviceInstanceUUID : $(this).attr("siUuid"),
        	    	resourceType : $(this).attr("resTypeId"),
        	    	resourceComponentName: $(this).attr("compName"),
        	    	resourceComponentVal: $(this).attr("compVal")
        	    },
        	    success: function (resourceComponentValues) {
        	      if (isBlank(resourceComponentValues)) {
                  $(this).attr("data-content", commmonmessages.resourceComponentNotAvailable);
                  resourceCompDetailsMap[targetId] = commmonmessages.resourceComponentNotAvailable;
                  $(".popover-content:visible").html(commmonmessages.resourceComponentNotAvailable);
        	      } else {
        	    	  $(this).attr("data-content", resourceComponentValues);
        	    	  resourceCompDetailsMap[targetId] = resourceComponentValues;
        	    	  $(".popover-content:visible").html(resourceComponentValues);
        	      }
        	    },
        	    error: function(XMLHttpRequest) {
        	    	$(this).attr("data-content", commmonmessages.errorFecthingDetails);
        	    	$(".popover-content:visible").html(commmonmessages.errorFecthingDetails);
        	    },
        	    complete: function(xhr, status) {
        	        // Just added to prevent it from going to generic handler
        	    }
        	  });
        });
  	        
      }
    },
    error: function() {
      // need to do
    }
  });
}

function viewplannedcharges(current) {
  initDialog("dialog_view_planned_charges", 905, 600);
  var actionurl = productBundlesUrl + "/viewplannedcharges";
  $.ajax({
    type: "GET",
    url: actionurl,
    dataType: "html",
    async: false,
    success: function(html) {
      var $thisDialog = $("#dialog_view_planned_charges");
      $thisDialog.html("");
      $thisDialog.html(html);
      $thisDialog.dialog('option', 'buttons', {
        "OK": function() {
          $(this).dialog("close");
          $("#dialog_view_planned_charges").empty();
        }
      });
      dialogButtonsLocalizer($thisDialog, {
        'OK': g_dictionary.dialogOK
      });
      $thisDialog.bind("dialogbeforeclose", function(event, ui) {
        $thisDialog.empty();
      });
      $thisDialog.dialog("open");
    },
    error: function() {}
  });
}

function editplannedCharges(current) {
  initDialog("dialog_edit_planned_charges", 950, 600);
  var actionurl = productBundlesUrl + "/editplannedcharges";
  $.ajax({
    type: "GET",
    url: actionurl,
    dataType: "html",
    async: false,
    success: function(html) {
      var $thisDialog = $("#dialog_edit_planned_charges");
      $thisDialog.html("");
      $thisDialog.html(html);
      $thisDialog.dialog('option', 'buttons', {
        "OK": function() {
          $thisDialog.find("#priceRequiredError").empty();
          var rateCardForm = $thisDialog.find("form");
          var isFormValid = $(rateCardForm).valid();
          var hasError = $(rateCardForm).find(".priceRequired").hasClass('error');
          if (hasError == false) {
            $(".dialog_common_messagebox").hide();
          } else{
        	  var top = $thisDialog.find("input.error").offset().top - $thisDialog.offset().top - 81;
        	  $thisDialog.find('.plangrid_lightbox').animate({scrollTop: top},'slow');
          }
          if (isFormValid) {
            $.ajax({
              type: "POST",
              url: $(rateCardForm).attr('action'),
              data: $(rateCardForm).serialize(),
              dataType: "html",
              async: false,
              success: function(status) {
                $thisDialog.find("#priceRequiredError").html("");
                if (status == "success") {
                  $thisDialog.dialog("close");
                  $("#product_bundle_tab").click();
                }
              },
              error: function(XMLHttpRequest) {
                $thisDialog.dialog("close");
                popUpDialogForAlerts("dialog_info", i18n.errors.planchargesfailed);
              }
            });
          }

        },
        "Cancel": function() {
          $(this).dialog("close");
          $("#dialog_edit_planned_charges").empty();
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
    error: function(XMLHttpResponse) {
      if (XMLHttpResponse.status === PRECONDITION_FAILED) {
        popUpDialogForAlerts("alert_dialog", i18n.errors.uncompatible_currency_precision);
      }
    }
  });
}

function viewProductChargesHistory() {
  var url = productsUrl + $('#productCode').val() + "/viewproductchargeshistory";
  $.ajax({
    type: "GET",
    url: url,
    dataType: "html",
    async: false,
    success: function(html) {
      $("#productpricing_content").html(html);
    },
    error: function() {}
  });
}


function viewBundlePricing(current, showDialog, currenciesToDisplay) {
  $(".widgets_detailstab").removeClass("active").addClass("nonactive");
  $("#top_message_panel").hide();
  $(current).removeClass("nonactive").addClass("active");
  $('#details_content').hide();
  $('#channelpricing_content').hide();
  $('#provisioningconstraint_content').hide();
  $("#entitlements_content").hide();
  $("#tab_spinning_wheel").show();
  var whichPlan = $("#whichPlan").val();

  if (whichPlan == "current") {
    viewBundleCurrentCharges(showDialog, currenciesToDisplay);
  } else if (whichPlan == "history") {
    if ($("#rpb_history_dates option:selected").val().trim() != "") {
      getBundleHistoryData($("#rpb_history_dates option:selected").val().trim(),
        showDialog, currenciesToDisplay);
    }
  } else {
    viewBundlePlannedCharges(showDialog, currenciesToDisplay);
  }

  $("#tab_spinning_wheel").hide();
  $('#bundlepricing_content').show();
}


function viewBundleChannelPricing(current, showDialog, currenciesToDisplay) {
  $(".widgets_detailstab").removeClass("active").addClass("nonactive");
  $("li[id^='channelpricing_tab']").removeClass("nonactive").addClass("active");
  $('#details_content').hide();
  $("#entitlements_content").hide();
  $('#bundlepricing_content').hide();
  $('#provisioningconstraint_content').hide();
  $("#tab_spinning_wheel").show();
  var bundleCode = $('#bundleCode').val();
  viewChannelPricing(bundleCode, 1, showDialog, currenciesToDisplay);
  $("#tab_spinning_wheel").hide();
  $('#channelpricing_content').show();
}

function viewProvisioningConstraints(current, showDialog) {
  $(".widgets_detailstab").removeClass("active").addClass("nonactive");
  $("li[id^='provisioningconstraints_tab']").removeClass("nonactive").addClass("active");
  $('#details_content').hide();
  $("#entitlements_content").hide();
  $('#bundlepricing_content').hide();
  $('#channelpricing_content').hide();
  $("#tab_spinning_wheel").show();
  var bundleCode = $('#bundleCode').val();
  viewProvisioningConstarintContent(bundleCode, 1, showDialog);
  $("#tab_spinning_wheel").hide();
  $('#provisioningconstraint_content').show();
}

function viewBundleEntitlements(current) {
  $(".widgets_detailstab").removeClass("active").addClass("nonactive");
  $(current).removeClass("nonactive").addClass("active");
  $('#details_content').hide();
  $('#channelpricing_content').hide();
  $('#bundlepricing_content').hide();
  $('#provisioningconstraint_content').hide();
  $("#tab_spinning_wheel").show();
  var bundleCode = $('#bundleCode').val();

  var whichPlan = $("#whichPlan").val();
  if (whichPlan == "current") {
    viewEntitlements("current", bundleCode, 1);
  } else if (whichPlan == "history") {
    if ($("#rpb_history_dates option:selected").val().trim() != "") {
      viewEntitlements("history", bundleCode, 1, $("#rpb_history_dates option:selected").val().trim());
    }
  } else {
    viewEntitlements("planned", bundleCode, 1);
  }

  $("#tab_spinning_wheel").hide();
  $("#entitlements_content").show();
}

function showBundlePricingDialog(html) {
  initDialog("dialog_view_bundle_pricing", 782);
  var $thisDialog = $("#dialog_view_bundle_pricing");
  $thisDialog.html("");
  $thisDialog.html(html);
  $thisDialog.dialog('option', 'buttons', {
    "OK": function() {
      $(this).dialog("close");
      $thisDialog.empty();
    }
  });
  dialogButtonsLocalizer($thisDialog, {
    'OK': g_dictionary.dialogOK
  });
  $thisDialog.bind("dialogbeforeclose", function(event, ui) {
    $thisDialog.empty();
  });
  $thisDialog.find(".widget_details_actionbox").remove();
  $thisDialog.find(".widget_subactions.action_menu_container").remove();
  $thisDialog.find(".widget_grid_cell .moretabbutton").parent().remove();
  $thisDialog.dialog("open");
}

function viewBundleCurrentCharges(showDialog, currenciesToDisplay) {
  var url = productBundlesUrl + $('#bundleCode').val() + "/viewbundlecurrentcharges";
  var data = {};
  if (currenciesToDisplay !== undefined) {
    data['currenciesToDisplay'] = currenciesToDisplay;
  }
  $.ajax({
    type: "GET",
    url: url,
    data: data,
    dataType: "html",
    async: false,
    success: function(html) {
      if (showDialog === true) {
        showBundlePricingDialog(html);
      } else {
        $("#bundlepricing_content").html(html);
        $("#bundlepricing_content .widget_grid_cell .moretabbutton").attr('onclick', '').click(function() {
          viewBundleCurrentCharges(true)
        });
      }
      bindActionMenuContainers();
    },
    error: function() {

    }
  });
}

function addBundleCurrentCharges(current) {

  initDialog("dialog_add_bundle_default_price", 850, 600);
  var actionurl = productBundlesUrl + $('#bundleCode').val() + "/addbundlecurrentcharges";
  $.ajax({
    type: "GET",
    url: actionurl,
    dataType: "html",
    async: false,
    success: function(html) {
      var $thisDialog = $("#dialog_add_bundle_default_price");
      $thisDialog.html("");
      $thisDialog.html(html);
      $thisDialog.dialog('option', 'buttons', {
        "OK": function() {
          var rateCardChargesForm = $thisDialog.find("form");
          var isFormValid = $(rateCardChargesForm).valid();
          var hasError = $(rateCardChargesForm).find(".priceRequired").hasClass('error');
          if (hasError == false) {
            $(".dialog_common_messagebox").hide();
          } else {
            $(".dialog_common_messagebox").show();
          }
          if (isFormValid) {
            $.ajax({
              type: "POST",
              url: $(rateCardChargesForm).attr('action'),
              data: $(rateCardChargesForm).serialize(),
              dataType: "html",
              async: false,
              success: function(html) {
                $thisDialog.dialog("close");
                $("#bundlepricing_tab").click();
              },
              error: function(XMLHttpRequest) {

              }
            });
          }

        },
        "Cancel": function() {
          $(this).dialog("close");
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

function viewBundlePlannedCharges(showDialog, currenciesToDisplay) {
  var url = productBundlesUrl + $('#bundleCode').val() + "/viewbundleplannedcharges";
  var data = {};
  if (currenciesToDisplay !== undefined) {
    data['currenciesToDisplay'] = currenciesToDisplay;
  }
  $.ajax({
    type: "GET",
    data: data,
    url: url,
    dataType: "html",
    async: false,
    success: function(html) {
      if (showDialog === true) {
        showBundlePricingDialog(html);
      } else {
        $("#bundlepricing_content").html(html);
        $("#bundlepricing_content .widget_grid_cell .moretabbutton").attr('onclick', '').click(function() {
          viewBundlePlannedCharges(true);
        });
      }
      bindActionMenuContainers();
    },
    error: function() {}
  });
}

function getBundleHistoryData(historyDate, showDialog, currenciesToDisplay) {
  var url = productBundlesUrl + $('#bundleCode').val() + "/viewbundlechargeshistorydata";
  url += "?revisionDate=" + historyDate;
  if (currenciesToDisplay !== undefined) {
    url += '&currenciesToDisplay=' + currenciesToDisplay;
  }
  $.ajax({
    type: "GET",
    url: url,
    dataType: "html",
    async: false,
    success: function(html) {
      if (showDialog === true) {
        showBundlePricingDialog(html);
      } else {
        $("#bundlepricing_content").html(html);
        $("#bundlepricing_content .widget_grid_cell .moretabbutton").attr('onclick', '').click(function() {
          getBundleHistoryData(historyDate, true);
        });
      }
      bindActionMenuContainers();
    },
    error: function() {}
  });
}

function i18nText(field) {
  switch (field) {
    case "None":
      if (i18n.label.productbundle.NONE != null && i18n.label.productbundle.NONE != "undefined") {
        field = i18n.label.productbundle.NONE;
      }
      break;
    case "Annual":
      if (i18n.label.productbundle.ANNUAL != null && i18n.label.productbundle.ANNUAL != "undefined") {
        field = i18n.label.productbundle.ANNUAL;
      }
      break;
    case "Monthly":
      if (i18n.label.productbundle.MONTHLY != null && i18n.label.productbundle.MONTHLY != "undefined") {
        field = i18n.label.productbundle.MONTHLY;
      }
      break;
    case "Quarterly":
      if (i18n.label.productbundle.QUARTERLY != null && i18n.label.productbundle.QUARTERLY != "undefined") {
        field = i18n.label.productbundle.QUARTERLY;
      }
      break;
    case "true":
      if (i18n.label.productbundle.TrueText != null && i18n.label.productbundle.TrueText != "undefined") {
        field = i18n.label.productbundle.TrueText;
      }
      break;
    case "false":
      if (i18n.label.productbundle.FalseText != null && i18n.label.productbundle.FalseText != "undefined") {
        field = i18n.label.productbundle.FalseText;
      }
      break;
  }
  return field;
}

function fetchBundleList(currentPage, searchPattern) {
  var data = {};
  data["serviceInstanceUUID"] = $("#instances").find(".instance_selected").attr("id");
  if (currentPage != undefined) {
    data["currentPage"] = currentPage;
  }
  if (searchPattern != undefined) {
    data["namePattern"] = searchPattern;
  }
  if ($("#whichPlan").val() == "history") {
    if ($("#rpb_history_dates option:selected").val().trim() == "") {
      return;
    }
    data["revisionDate"] = $("#rpb_history_dates option:selected").val().trim();
  }
  data["whichPlan"] = $("#whichPlan").val();

  data["filterBy"] = productBundlefilterby;
  $.ajax({
    url: "/portal/portal/productBundles/list",
    dataType: "html",
    data: data,
    async: false,
    cache: false,
    success: function(html) {
      $("#productBundleListingDiv").empty();
      $("#productBundleListingDiv").html(html);
      $("#productBundleListingDiv").find("#productBundlegridcontent").find(".j_viewbundle:first").click();
    },
    error: function(XMLHttpResponse) {
      handleError(XMLHttpResponse);
    }
  });
}

function hideUnlimitedEntitlementCheckBoxIfNecessary(sel) {
  
  var unlimitedallow = $("#productId").find(':selected').attr('unlimitedallow');
  var product_uom = $("#productId").find(':selected').attr('uom');
  if (unlimitedallow == "true") {
    $("#unlimitedEntitlementCheckBox").show();
  } else {
    $("#unlimitedEntitlementCheckBox").hide();
    $("#entitlement\\.includedUnits").removeAttr("disabled");
    $('#unlimitedUsage').removeAttr('checked');
  }
  if(product_uom != undefined)
    $("#product_uom").text(product_uom);
  else
    $("#product_uom").text("");
  
  if($("#productId").find(':selected').hasClass("noProductSelected")){
    $("#entitlement\\.includedUnits").val("");
  }else{
    $("#entitlement\\.includedUnits").val("1");
  }

}

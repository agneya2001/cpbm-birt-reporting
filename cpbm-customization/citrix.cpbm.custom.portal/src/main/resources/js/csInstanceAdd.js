/*
*  Copyright © 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/
$(document).ready(function() {

  $("#serviceInstanceForm").validate({
    submitHandler: function() {
      var noOfErrors = $('label.error:visible').length;
      if (noOfErrors == 0) {
        createInstance();
      }
      return false;
    }
  });

  $('input[id^="configproperty"]').tooltip({
    // place tooltip on the right edge
    position: "center right",
    // a little tweaking of the position
    offset: [-2, 10],
    // use the built-in fadeIn/fadeOut effect
    effect: "fade",
    // custom opacity setting
    opacity: 0.7
  });

  $("#submitbutton").bind("click", function(event) {
    if ($("#submitbutton").hasClass('active')) {
      $('#serviceInstanceForm').submit();
    }
  });

  $("#backtolisting").bind("click", function(event) {
    var id = $(this).attr('service');
    $servicelist_mainbox = $('div.servicelist.mainbox[serviceid=' + id + ']');
    $servicelist_extended = $('div.servicelist_extended[serviceid=' + id + ']');
    $servicelist_extended_instancelist = $servicelist_extended.find("#service_instance_list");

    if ($("#backtolisting").attr("reload") == "true") {
      $.ajax({
        type: "GET",
        url: connectorPath + "/view_instances" + "?id=" + id,
        dataType: 'json',
        success: function(responseJson) {
          $servicelist_extended_instancelist.empty();

          for (var i = 0; i < responseJson.instances.length; i++) {
            var instance = responseJson.instances[i];
            var alive = responseJson.isAliveMap[instance.uuid];
            var $newInstance = $("#service_instance_default_template").clone();
            if (instance.imagePath) {
              $newInstance.find("#instance_image_id").attr("src", instance.imagePath);
            }
            $newInstance.find("#instance_name").text(instance.name);
            $newInstance.find("#instance_description").text(instance.description);
            $newInstance.find(".actionbutton").parents('li').attr("serviceid", instance.uuid);

            if (!alive) {
              $newInstance.find("#instance_icon").removeClass('running_listicon').addClass('stopped_listicon');
            }
            $newInstance.show();
            $servicelist_extended_instancelist.append($newInstance);
          }
          $servicelist_extended_instancelist.find("#service_instance_default_template").last().css('border',
            '0px');
          if (responseJson.instances.length > 0) {
            $servicelist_mainbox.find(".cloud_button").removeClass("nonactive").addClass("active");
            $servicelist_extended.show();
          }
          if (responseJson.instances.length > 0) {
            $servicelist_mainbox.find(".service_count").text(responseJson.instances.length);
          }
        },
        error: function(request) {
          $("#spinning_wheel").hide();
        }
      });
    }

    var category = $("#selectedcategory").val();
    showSelectedCategory(category);
    $('.service_detailpanel').hide();
    $(".service_listpanel").show();

    var newpos = $servicelist_mainbox.offset();
    window.scrollTo(newpos.left, newpos.top - 250);
    return false;
  });
});

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

function addServiceInstanceNext(current) {

  var currentstep = $(current).parents(".j_cloudservicepopup").attr('id');
  var $step4 = $("#step4");
  var $step5 = $("#step5");
  var $step6 = $("#step6");
  var $currentstep = $("#" + currentstep);
  var nextstep = $currentstep.find("#nextstep").val();
  var serviceInstanceForm = $(current).closest("form");

  if (currentstep == "step1") {}

  if (currentstep == "step2" && product_action == "create") {

  }
  if ($(serviceInstanceForm).valid()) {
    if (currentstep == "step1") {
      $step4.find("#confirmProductDetails").find("#name").text($("#product\\.name").val());
      $step4.find("#confirmProductDetails").find("#name").attr("title", $("#product\\.name").val());
      $step4.find("#confirmProductDetails").find("#code").text($("#product\\.code").val());
      $step4.find("#confirmProductDetails").find("#code").attr("title", $("#product\\.code").val());
      $step4.find("#confirmProductDetails").find("#product_category").text($("#categoryID option:selected").text());
      $step4.find("#confirmProductDetails").find("#product_category").attr("title", $("#categoryID option:selected").text());
      $step5.find("#confirmProductDetails").find("#name").text($("#product\\.name").val());
      $step5.find("#confirmProductDetails").find("#code").text($("#product\\.code").val());
      $step5.find("#confirmProductDetails").find("#product_category").text($("#categoryID option:selected").text());
    }
    if (currentstep == "step5") {

    }
    if ((product_action == "create" && currentstep == "step5") {
        //call submit
      } else if ((product_action == "create" && currentstep == "step6") ||
        (product_action == "edit" && currentstep == "step5")) {
        $currentDialog.dialog("close");
        $("#dialog_add_product").find(".dialog_formcontent").empty();
        $("#dialog_edit_product").find(".dialog_formcontent").empty();
      } else if (currentstep == "step4") {
        $step4.find(".common_messagebox").hide();
        $(".j_productspopup").hide();
        $("#" + nextstep).show();
      } else {
        $(".j_productspopup").hide();
        $("#" + nextstep).show();
      }
    }
  }

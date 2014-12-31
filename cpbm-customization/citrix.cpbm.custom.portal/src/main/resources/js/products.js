
/*
*  Copyright � 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/
$(document).ready(function() {

  $(".dropdownbutton").hover(function() {
    $("#plansdropdown").show();
  }, function() {
    $("#plansdropdown").hide();
  });

  $("#details_tab").live("click", function(event) {
    $(".widgets_detailstab").removeClass("active").addClass("nonactive");
    $(this).removeClass("nonactive").addClass("active");
    $('#channelpricing_content').hide();
    $('#productpricing_content').hide();
    $('#mediationrules_content').hide();
    $('#details_content').show();
  });

  /**
   * clean after closing add product dialog box.
   */
  $('#dialog_add_product').bind('dialogclose', function(event) {
    $("#dialog_add_product").find(".dialog_formcontent").empty();
    $("#dialog_edit_product").find(".dialog_formcontent").empty();
  });

  /**
   * add product wizard last step link.
   */
  $(".close_product_wizard").live("click", function(event) {
    $currentDialog.dialog("close");
    $("#dialog_add_product").empty();
    $("#dialog_edit_product").empty();
  });


  $("#backtoproducttype").bind("click", function(event) {
    $(".j_productspopup").hide();
    currentstep = "step1";
    $("#step1").show();
  });

  $("#backtoproductoffering").bind("click", function(event) {
    $(".j_productspopup").hide();
    currentstep = "step2";
    $("#step2").show();
  });
  $("#backtoproductdetails").live("click", function(event) {
    $(".j_productspopup").hide();
    currentstep = "step1";
    $("#step1").show();
  });

  $("#backtoaddcharges").live("click", function(event) {
    $(".j_productspopup").hide();
    currentstep = "step4";
    $("#step5").show();
  });

  /**
   * Bind Catagory DropDown
   */
  bindCatagoryDropDown();
  
  /**
   * Validate product form
   */

  jQuery.validator.setDefaults({
    onfocusout: false,
    onkeyup: false,
    onclick: false
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
  $.validator.addClassRules("productTypeRequired", {
    productTypeRequired: true
  });



  $.validator
    .addMethod(
      "productTypeRequired",
      function(value, element) {
        if (value == "") {
          return false;
        }
        return true;
      },
      i18n.errors.products.product_type);


  $.validator
    .addMethod(
      "twoDecimal",
      function(value, element) {
        $(element).rules("add", {
          number: true
        });
        isPriceValid = value != "" && isNaN(value) == false && Number(value) >= 0 && Number(value) <= 99999999.9999;

        if (isPriceValid == false) {
          return false;
        }
        return true;

      },
      i18n.errors.products.enter_valid_value);

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
      i18n.errors.products.max_four_decimal_value);

  jQuery.validator.addMethod('productCode', function(value, element) {
    return value.length > 0 && /^[a-zA-Z0-9_.-]+$/.test(value);
  }, i18n.errors.products.productCodeValid);
  
 





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
      i18n.errors.products.edit_image_path_invalid_message);


  $("#productLogoForm").validate({
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
        required: i18n.errors.products.edit_image_path_invalid_message
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

$("#productForm").validate({
  // debug : true,
  success: "valid",
  ignoreTitle: true,
  rules: {
    "service": {
      required: function(element) {
        return requiredField(element);
      }
    },
    "serviceInstance": {
      required: function(element) {
        return requiredField(element);
      }
    },
    "product.name": {
      required: function(element) {
        return requiredField(element);
      },
      maxlength: 255
    },
    "product.productType": {
      required: true
    },
    "product.code": {
      required: function(element) {
        return requiredField(element);
      },
      maxlength: 64,
      productCode: true,
      xRemote: {
        condition: function() {
          return $("#product_code").parents(".j_productspopup").is(':visible') && $("#product_code").val() != $(
            "#product\\.code").val() && ($("#product\\.name").val() != "");
        },
        url: '/portal/portal/products/validateCode',
        async: false
      }
    },
    "product.componentId": {
      required: function(element) {
        var baseType = productBaseTypeMap[$("input:radio[name=product\\.productType]:checked").val()];
        if (baseType == SO_BASED) {
          $('#referenceId').val($('#product\\.componentId').val());
          return requiredField(element);
        }
        return false;
      }
    },
    "referenceId": {
      required: function(element) {
        var baseType = productBaseTypeMap[$("input:radio[name=product\\.productType]:checked").val()];
        if (baseType == ID_BASED || baseType == ID_TAG_BASED) {
          return requiredField(element);
        }
        return false;
      }
    },
    "referenceTag": {
      required: function(element) {
        var baseType = productBaseTypeMap[$("input:radio[name=product\\.productType]:checked").val()];
        if (baseType == ID_TAG_BASED || baseType == TAG_BASED) {
          return requiredField(element);
        }
        return false;
      }
    },
    "productItems": {
      required: function(element) {
        var baseType = productBaseTypeMap[$("input:radio[name=product\\.productType]:checked").val()];
        if ((baseType == TEMPLATE_BASED || baseType == ISO_BASED) && !$(".j_includeUserUploadedTemplateGroup").is(
          ':checked')) {
          return requiredField(element);
        }
        return false;
      }
    }
  },
  messages: {
    "service": {
      required: "Select a service"
    },
    "serviceInstance": {
      required: "Select an Instance"
    },
    "product.name": {
      required: i18n.errors.products.name
    },
    "product.productType": {
      required: i18n.errors.products.product_type
    },
    "product.code": {
      required: i18n.errors.products.code,
      noSpacesAllowed: i18n.errors.products.productCodeValid,
      xRemote: i18n.errors.common.codeNotUnique,
      remote: i18n.errors.common.codeNotUnique
    },
    "product.componentId": {
      required: i18n.errors.products.componentIdso
    },
    "referenceId": {
      required: i18n.errors.products.componentId
    },
    "referenceTag": {
      required: i18n.errors.products.componentTag
    },
    "productItems": {
      required: i18n.errors.products.templateIso
    }
  },
  errorPlacement: function(error, element) {


    var name = element.attr('id');
    var nameAttr = element.attr('name');
    if (nameAttr == 'productItems') {
      name = nameAttr;
    }
    if (nameAttr == 'product.productType') {
      name = nameAttr;
    }
    name = ReplaceAll(name, ".", "\\.");
    if (name != "") {
        error.appendTo("#" + name + "Error");
    }
  }
});

function openDiscriminatorsDialog(current) {
  var dialogId = "dialog_discriminators_details";
  initDialog(dialogId, 600);
  var $thisDialog = $("#" + dialogId);
  $thisDialog.html($(current).find("#discriminators_details").html())
  $thisDialog.dialog('option', 'buttons', {
    "Cancel": function() {
      $(this).dialog("close");
    }
  });
  dialogButtonsLocalizer($thisDialog, {
    'Cancel': g_dictionary.dialogClose
  });
  $thisDialog.dialog("open");
}


/**
 * View product details
 *
 * @param current
 * @return
 */

function viewProduct(current) {
  var divId = $(current).attr('id');
  var ID = divId.substr(3);
  resetGridRowStyle();
  $(current).addClass("selected");
  $(current).addClass("active");
  var url = productsUrl + "viewproduct";
  $.ajax({
    type: "GET",
    url: url,
    cache:false,
    data: {
      Id: ID,
      whichPlan: $("#whichPlan").val()
    },
    dataType: "html",
    async: true,
    success: function(html) {
      $("#editproductDiv").html("");
      $("#viewproductDiv").html(html);
      bindActionMenuContainers();
    },
    error: function() {
      // need to handle TO-DO
    }
  });
}

/**
 * Reset data row style
 *
 * @return
 */

function resetGridRowStyle() {
  $(".widget_navigationlist").each(function() {
    $(this).removeClass("selected");
    $(this).removeClass("active");
  });
}

/**
 * display CloudStack ID select if productType is compute display hypervisor
 * select if productType is license (license=template)
 *
 * @param element
 * @return
 */

function productTypeChanged(selectType) {
  var selectedProductType = productBaseTypeMap[selectType];
  var label = "";
  if (selectedProductType == TEMPLATE_BASED) {
    label = i18n.label.products.Templates;
  } else if (selectedProductType == ISO_BASED) {
    label = i18n.label.products.ISOs;
  }
  $('#product\\.componentId').val('');
  $('#product\\.hypervisor').val('');
  $('#referenceId').val('');
  $('#referenceTag').val('');
  $('.j_serviceofferingselection').hide();
  $('.j_templatesSelections').hide();
  // $(".j_useruploadedtemplate").hide();
  $("#userdefinedtemplatesdiv").hide();
  $("#userdefinediosdiv").hide();
  $('#referenceIdInput').hide();
  $('#referenceTagInput').hide();
  if (selectedProductType == SO_BASED) {
    $('.j_serviceofferingselection').show();
    $(".j_serviceofferingselection:first").click();
  } else if (selectedProductType == TEMPLATE_BASED || selectedProductType == ISO_BASED) {
    resetOfferingsSelection();
    $('.j_templatesSelections').show();
    if (selectedProductType == TEMPLATE_BASED) {
      $("#userdefinedtemplatesdiv").show();
      $("#userdefinediosdiv").hide();
    } else {
      $("#userdefinediosdiv").show();
      $("#userdefinedtemplatesdiv").hide();
    }
    // $(".j_useruploadedtemplate").show();
    $('#productGroupLabel').find("#templateslabel").html(label);
    $(".j_hypervisors").hide();
    if (selectedProductType == TEMPLATE_BASED) {
      $(".j_hypervisors").show();
    }
    $("#hypervisorsselection option:first-child").attr('selected', 'selected').change();
  } else if (selectedProductType == ID_BASED) {
    $('#referenceIdInput').show();
  } else if (selectedProductType == TAG_BASED) {
    $('#referenceTagInput').show();
  } else if (selectedProductType == ID_TAG_BASED) {
    $('#referenceIdInput').show();
    $('#referenceTagInput').show();
  }
}

/**
 * Reset template selection when product type changed so that it will match with
 * confirmation screen.
 */

function resetOfferingsSelection() {

  $(".j_templatesSelections").find("input:checkbox").attr('checked', false);
  $(".j_templatesSelections").find(".j_selecttemplate").removeClass("active");
  $(".j_templatesSelections").find(".widget_checkbox").find("span").removeClass("checked");
  $(".j_templatesSelections").find(".widget_checkbox").find("span").addClass("unchecked");

  $(".j_useruploadedtemplate").find("input:checkbox").attr('checked', false);
  $(".j_useruploadedtemplate").removeClass("active");
  $(".j_useruploadedtemplate").find(".widget_checkbox").find("span").removeClass("checked");
  $(".j_useruploadedtemplate").find(".widget_checkbox").find("span").addClass("unchecked");
  $("#step5").find("#confirmofferinglist").empty();
}

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
  var searchPattern = $("#productSearchPanel").val();

  $("#click_previous").unbind("click").bind("click", previousClick);
  $("#click_previous").removeClass("nonactive");

  listProductByFilter(null, null, currentPage, searchPattern);
}

function previousClick(event) {
  $("#click_previous").unbind("click", previousClick);
  $("#click_previous").addClass("nonactive");

  currentPage = currentPage - 1;
  var searchPattern = $("#productSearchPanel").val();

  $("#click_next").removeClass("nonactive");
  $("#click_next").unbind("click").bind("click", nextClick);
  listProductByFilter(null, null, currentPage, searchPattern);
}

function fetchProductList(currentPage, searchPattern) {
  var data = {};
  data["serviceInstanceUUID"] = $("#instances").find(".instance_selected").attr("id");
  if (currentPage != undefined) {
    data["currentPage"] = currentPage;
  }
  if (searchPattern != undefined) {
    data["namePattern"] = searchPattern;
  }
  $.ajax({
    url: "/portal/portal/products/listproducts",
    dataType: "html",
    data: data,
    async: false,
    cache: false,
    success: function(html) {
      $("#productBundleListingDiv").empty();
      $("#productBundleListingDiv").html(html);
      $("#productBundleListingDiv").find("#productgridcontent").find(".j_viewproduct:first").click();
    },
    error: function(XMLHttpResponse) {
      handleError(XMLHttpResponse);
    }
  });
  bindCatagoryDropDown();
}


function fetchScalesList(serviceUuid, UomName) {
  var data = {};
  data["serviceUuid"] = serviceUuid;
  data["UomName"] = UomName;
  var scaleDropdown = $("#step4_scale");
  var conversionFactorBox = $("#step4_conversionFactor");
  var conversionFactorLabel = $("#step4_conversionFactor_label");
  var customUnitHeader = $("#step4_customUnits");
  var customUnitNameBox = $("#step4_customUnitsName");
  var customUnitNameValue = $("#step4_custom_units");
  $.ajax({
    url: "/portal/portal/products/listscales",
    dataType: "json",
    data: data,
    async: false,
    cache: false,
    success: function(json) {
      $("#originalScales").data("originalScales", json.original);
      json = json.modified;
      var defaultScale = null;
      scaleDropdown.empty();
      for (var i = 0; i < json.length; i++) {
        scaleDropdown.append('<option value="' + json[i].conversionFactor + '" id="' + json[i].name + '">' + json[i].name +
          '</option>');
        if (json[i].defaultScale == true || json[i].defaultScale == "true") {
          defaultScale = json[i].name;
        }
      }
      if (defaultScale != null) {
        $("#step4_scale option[id='" + defaultScale + "']").attr("selected", "selected");
      }
      scaleDropdown.unbind("change").bind("change", function(event) {
        if (scaleDropdown.val() != "custom") {
          var selectedScale = $("#step4_scale :selected").attr("id");
          var originalScales = $("#originalScales").data("originalScales");
          if (originalScales != null && selectedScale != null) {
            conversionFactorBox.val(originalScales[selectedScale]);
          } else {
            conversionFactorBox.val("");
          }
          conversionFactorLabel.text(scaleDropdown.val());
          conversionFactorLabel.show();
          conversionFactorBox.hide();
          customUnitHeader.hide();
          customUnitNameBox.hide();
        } else {
          conversionFactorBox.val('');
          customUnitNameValue.val('');
          customUnitHeader.show();
          customUnitNameBox.show();
          conversionFactorLabel.hide();
          conversionFactorBox.show();
        }
      });
    },
    error: function(XMLHttpResponse) {
      $("#originalScales").data("originalScales", "");
      handleError(XMLHttpResponse);
    }
  });
  scaleDropdown.append('<option value="custom" id="custom">' + g_dictionary.custom + '</option>');
  scaleDropdown.trigger('change');
}


var searchRequest;

function searchProductByName(event) {
  $("#click_previous").unbind("click", previousClick);
  $("#click_previous").addClass("nonactive");

  currentPage = 1;
  var searchPattern = $("#productSearchPanel").val();

  $("#click_next").removeClass("nonactive");
  $("#click_next").unbind("click").bind("click", nextClick);

  var url = productsUrl + "searchlist";
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
  if (currentFilterBy != null) {
    data["filterBy"] = currentFilterBy;
  }
  var category = $("#filter_dropdown").val();
  data["category"] = category;
  if (searchRequest && searchRequest.readyState != 4) {
    searchRequest.abort();
  }
  searchRequest = $.ajax({
    type: "GET",
    url: url,
    dataType: "html",
    data: data,
    success: function(html) {
      $("#productlist_div").html(html);
      $("#productgridcontent").find(".j_viewproduct:first").click();
    },
    error: function() {
      // need to handle
    }
  });
}

/**
 * Change product type.
 *
 * @param current
 */

function changeProductType(current) {
  $(".j_producttype").removeClass("active");
  $(".j_producttype").find(".widget_radiobuttons").find("span").removeClass("checked");
  $(".j_producttype").find(".widget_radiobuttons").find("span").addClass("unchecked");
  $(current).find("input:radio").attr("checked", true);
  $(current).addClass("active");
  $(current).find(".widget_radiobuttons").find("span").removeClass("unchecked");
  $(current).find(".widget_radiobuttons").find("span").addClass("checked");

  // clean confirmation wizard
  var $step5 = $("#step5");
  $step5.find("#confirmofferinglist").empty();
  // do actions based on offering selection
  productTypeChanged($(current).find("input:radio").val());
  // update confirmation wizard
  var productTypeId = $("input:radio[name=product\\.productType]:checked").val();
  $step5.find("#confirmProductType").find(".j_description").text($("#producttype" + productTypeId).find(
    ".j_description").text());
}

/**
 * Change Service offering.
 *
 * @param current
 */

function changeServiceOffering(current) {
  $(".j_serviceselection").removeClass("active");
  $(".j_serviceofferingselection").find(".widget_radiobuttons").find("span").removeClass("checked");
  $(".j_serviceofferingselection").find(".widget_radiobuttons").find("span").addClass("unchecked");
  $(current).find("input:radio").attr("checked", true);
  $(current).addClass("active");
  $(current).find(".widget_radiobuttons").find("span").removeClass("unchecked");
  $(current).find(".widget_radiobuttons").find("span").addClass("checked");
  // update confirmation wizard
  var $offeringtemplate = $("#confirmoffering").clone();
  var serviceoffetingId = $("input:radio[name=product\\.componentId]:checked").val();
  $offeringtemplate.attr('id', "confirmoffering" + serviceoffetingId);
  $offeringtemplate.find(".j_subdescription").text($("#serviceoffering" + serviceoffetingId).find(".j_description").text());
  $offeringtemplate.show();
  var $step5 = $("#step5");
  $step5.find("#confirmofferinglist").html($offeringtemplate);
}

/**
 * Change Change Hypervisor.
 *
 * @param current
 */

function changeHypervisor(current) {
  var selectedProductType = productBaseTypeMap[$("input:radio[name=product\\.productType]:checked").val()];
  var hypervisor = $(current).val();
  var groupName = "";
  if (selectedProductType == TEMPLATE_BASED) {
    groupName = "templateGroup";
  } else if (selectedProductType == ISO_BASED) {
    groupName = "isoGroup";
  }
  if (hypervisor == "") {
    $('.j_selecttemplate').hide();
    $('#' + groupName + ' .j_selecttemplate').show();
  } else {
    var templateDivs = '#' + groupName + " ." + hypervisor + "Template";
    $('.j_selecttemplate').hide();
    $(templateDivs).show();
  }
}

/**
 * Select Template.
 *
 * @param current
 */

function selectTemplate(current) {
  var currentState = $(current).find("input:checkbox").attr('checked');
  if (currentState == "checked") {
    $(current).find("input:checkbox").attr('checked', false);
    $(current).removeClass("active");
    $(current).find(".widget_checkbox").find("span").removeClass("checked");
    $(current).find(".widget_checkbox").find("span").addClass("unchecked");

    // update confirmation wizard
    var selectedtemplateId = $(current).find("input:checkbox").val();
    $("#confirmofferinglist").find("#confirmoffering" + selectedtemplateId).remove();

  } else {
    $(current).find("input:checkbox").attr('checked', true);
    $(current).addClass("active");
    $(current).find(".widget_checkbox").find("span").removeClass("unchecked");
    $(current).find(".widget_checkbox").find("span").addClass("checked");

    // update confirmation wizard
    var selectedtemplateId = $(current).find("input:checkbox").val();
    var $offeringtemplate = $("#confirmoffering").clone();
    $offeringtemplate.attr('id', "confirmoffering" + selectedtemplateId);
    $offeringtemplate.find(".j_subdescription").text($("#template" + selectedtemplateId).find(".j_description").text());
    $offeringtemplate.show();
    $("#confirmofferinglist").append($offeringtemplate);

  }
}

/**
 * Select Template.
 *
 * @param current
 */

function selecteUserUploadedTemplate(current) {
  var currentState = $(current).find("input:checkbox").attr('checked');
  if (currentState == "checked") {
    $(current).find("input:checkbox").attr('checked', false);
    $(current).removeClass("active");
    $(current).find(".widget_checkbox").find("span").removeClass("checked");
    $(current).find(".widget_checkbox").find("span").addClass("unchecked");
    // update confirmation wizard
    $("#confirmofferinglist").find("#confirmoffering_useruploadedtemplate").remove();
    // show template selection
    $('.j_templatesSelections').show();
  } else {
    $(current).find("input:checkbox").attr('checked', true);
    $(current).addClass("active");
    $(current).find(".widget_checkbox").find("span").removeClass("unchecked");
    $(current).find(".widget_checkbox").find("span").addClass("checked");

    // update confirmation wizard
    var $offeringtemplate = $("#confirmoffering").clone();
    $offeringtemplate.attr('id', "confirmoffering_useruploadedtemplate");
    var selectedProductType = productBaseTypeMap[$("input:radio[name=product\\.productType]:checked").val()];
    if (selectedProductType == TEMPLATE_BASED) {
      $offeringtemplate.find(".j_subdescription").text($(current).parents("#userdefinedtemplatesdiv").find(
        "#usertemplateslabel").text());
    } else if (selectedProductType == ISO_BASED) {
      $offeringtemplate.find(".j_subdescription").text($(current).parents("#userdefinediosdiv").find("#userisolabel").text());
    }
    $offeringtemplate.show();
    $("#confirmofferinglist").html($offeringtemplate);
    // hide template selection
    $('.j_templatesSelections').hide();

    // de-selected templates so that it will match with confiramtion
    // wizard.
    $(".j_selecttemplate").find(".widget_checkbox").find("span").addClass("unchecked");
    $(".j_selecttemplate").find("input:checkbox").attr('checked', false);
    $(".j_selecttemplate").removeClass("active");
  }

}

/**
 * is element there in current wizard to validate or not
 *
 * @param element
 * @returns {Boolean}
 */

function requiredField(element) {
  if ($(element).parents(".j_productspopup").is(':visible')) {
    return true;
  }
  return false;
}
function fun(currPrecision){

    if (!'/^(?:\d*\.\d{1,'+currPrecision+'}|\d+)$/'.test(value)) {
          return false;
        }
}

/**
 * After creating new product add details in the starting of list grid,
 *
 * @param product
 */

function editProductDetailsInListView(product) {
  var $productListTemplate = $("#productgridcontent").find("#row" + product.id);
  // $productListTemplate.find("#nav_icon").addClass(product.productType.name);
  $productListTemplate.find(".widget_navtitlebox").find('.title').text(product.name);
  $productListTemplate.find(".widget_navtitlebox").find('.subtitle').text(product.category.name);
  $productListTemplate.find(".widget_info_popover").find('.raw_contents').find("#info_bubble_displayname").find(
    '.raw_contents_value').find("#value").text(product.uom);
  $productListTemplate.find(".widget_info_popover").find('.raw_contents').find("#info_bubble_code").find(
    '.raw_contents_value').find("#value").text(product.code);
  $("#productgridcontent").find("#row" + product.id).click();
}

/**
 * After creating new product add details in the starting of list grid,
 *
 * @param product
 */

function addProductDetailsInListView(product) {
  var $productListTemplate = $("#productviewtemplate").clone();
  $productListTemplate.attr('id', "row" + product.id);
  var isOdd = $("#productgridcontent").find(".j_viewproduct:first").hasClass('odd');
  if (isOdd == true) {
    $productListTemplate.addClass('even');
  } else {
    $productListTemplate.addClass('odd');
  }
  $productListTemplate.addClass('selected');
  $productListTemplate.addClass('active');
  $productListTemplate.find(".widget_navtitlebox").find('.title').text(product.name);
  $productListTemplate.find(".widget_navtitlebox").find('.subtitle').text(product.category.name);
  $productListTemplate.find(".widget_info_popover").find('.raw_contents').find("#info_bubble_displayname").find(
    '.raw_contents_value').find("#value").text(product.uom);
  $productListTemplate.find(".widget_info_popover").find('.raw_contents').find("#info_bubble_code").find(
    '.raw_contents_value').find("#value").text(product.code);

  $productListTemplate.show();
  $("#productgridcontent").prepend($productListTemplate);
  var productsCount = $("#productgridcontent").find(".j_viewproduct").size();
  // remove last element if count grater than pagination value
  if (productsCount > perPageValue) {
    $("#productgridcontent").find(".j_viewproduct:last").remove();
  }
  // reset styling
  resetGridRowStyle();
  $("#productgridcontent").find(".j_viewproduct:first").click();
  $("#productgridcontent").find("#non_list").remove();
}

function addProductPrevious(current) {
  var prevStep = $(current).parents(".j_productspopup").find('#prevstep').val();
  if (prevStep == "step1") {
    $("#productMedRuleSelectError").html("");
  }
  if (prevStep != "") {
    $(".j_productspopup").hide();
    $("#" + prevStep).show();
  }
}

function getMediationRules() {
  var finalMedRuleList = new Array();
  $("#mediationRules").find("div[id^='addedusage_']").each(function() {
    var rule = new Object();
    var usageTypeId = $(this).find("#usagetype").attr("value");
    rule.usageTypeId = usageTypeId;
    rule.conversionFactor = $(this).find("#conversionfactor").attr("value");
    rule.operator = $(this).find("#operator").attr("value");
    var discriminatorVals = new Array();
    $('#addedDiscriminator_' + usageTypeId).find("div[id^='addedDiscriminatorValues_']").each(function() {
      var nestedRule = new Object();
      nestedRule.discriminatorId = $(this).find("#discriminatorName").attr("value");
      nestedRule.operator = $(this).find(".select1 option:selected").val();
      var selectorinput = $(this).find("div[selectorinput^='opt_']");
      if (selectorinput.attr("selectorinput") == "opt_select") {
        if (selectorinput.find("#discvalue_selectbox option:selected").index() == 0) {
          return;
        }
        nestedRule.discriminatorValue = selectorinput.find("#discvalue_selectbox option:selected").val();
        nestedRule.discriminatorValueName = selectorinput.find("#discvalue_selectbox option:selected").text().trim();
      } else {
        if (selectorinput.find("#discvalue_inputbox").val().trim() == "") {
          return;
        }
        nestedRule.discriminatorValue = selectorinput.find("#discvalue_inputbox").val();
        nestedRule.discriminatorValueName = nestedRule.discriminatorValue;
      }
      discriminatorVals.push(nestedRule);
    });
    rule.discriminatorVals = discriminatorVals;
    finalMedRuleList.push(rule);
  });
  return finalMedRuleList;
}

function getMediationRulesForEdit() {
  var finalMedRuleList = new Array();
  $("#usageTypeDisp").find("li[id^='usageTypeLeftPanel_']").each(function() {
    var rule = new Object();
    rule.medRuleId = $(this).attr("medRuleId");

    var discriminatorVals = new Array();
    var discValElement = $("#mediationRuleDiscriminators").find("#addedDiscriminator_" + $(this).attr("medRuleId"));
    if (discValElement != undefined) {
      discValElement.find("div[id^='addedDiscriminatorValues_']").each(function() {
        var nestedRule = new Object();
        if ($(this).attr("alreadyadded") == "true") {
          nestedRule.discEntityId = $(this).find("#discriminatorName").attr("discEntityId");
        } else {
          nestedRule.discriminatorId = $(this).find("#discriminatorName").attr("value");
          nestedRule.discEntityId = -1;
          nestedRule.operator = $(this).find("#discriminatorOperator option:selected").val();
          var selectorinput = $(this).find("div[selectorinput^='opt_']");
          if (selectorinput.attr("selectorinput") == "opt_select") {
            if (selectorinput.find("#discvalue_selectbox option:selected").index() == 0) {
              return;
            }
            nestedRule.discriminatorValue = selectorinput.find("#discvalue_selectbox option:selected").val();
            nestedRule.discriminatorValueName = selectorinput.find("#discvalue_selectbox option:selected").text().trim();
          } else {
            if (selectorinput.find("#discvalue_inputbox").val().trim() == "") {
              return;
            }
            nestedRule.discriminatorValue = selectorinput.find("#discvalue_inputbox").val();
            nestedRule.discriminatorValueName = nestedRule.discriminatorValue;
          }
        }
        discriminatorVals.push(nestedRule);
      });
      rule.discriminatorVals = discriminatorVals;
    }
    finalMedRuleList.push(rule);
  });
  return finalMedRuleList;
}

function addProductNext(current) {
  var currentstep = $(current).parents(".j_productspopup").attr('id');
  var $step4 = $("#step4");
  var $step5 = $("#step5");
  var $step6 = $("#step6");
  var $step7 = $("#step7");
  var $currentstep = $("#" + currentstep);
  var nextstep = $currentstep.find("#nextstep").val();
  var productForm = $(current).closest("form");
  if (currentstep == "step1") {
    if (product_action == "create") {
      if ($("#serviceInstanceId option:selected").index() == 0) {
        return false;
      }
      if ($("#usageTypesPopulated").val() == "false") {
        getUsageTypes();
        $("#usageTypesPopulated").val("true");
      }
    } else if (product_action == "edit") {
      if ($("#mediationRulesPopulated").val() == "false") {
        populateEntriesInStep2OfProductEdit();
        $("#mediationRulesPopulated").val("true");
      }
    }
  }

  if (currentstep == "step2" && product_action == "create") {
    var usageTypesList = [];
    var onlyExclude = true;
    $("#mediationRules").find("div[id^='addedusage']").each(function() {
      usageTypesList.push($(this).find("#usagetype").find(':selected'));
      if ($(this).find("#operator").attr("value") == "combine") {
        onlyExclude = false;
      }
    });
    if (usageTypesList.length == 0) {
      $("#productMedRuleSelectError").html(i18n.errors.products.product_usage_type);
      return;
    }
    if (onlyExclude) {
      $("#productMedRuleSelectError").html(i18n.errors.products.usage_type_exclude);
      return;
    }
    $("#selectedUsageType").val($("#mediationRules").find("div[id^='addedusage_']").first().find("#uom").val());
    $("#productMedRuleSelectError").html("");
    $(".j_productspopup").hide();
    $("#" + nextstep).show();

    var countOfActiveUsageTypeSelects = 0;
    $("#usageTypeDisp").find("li[id^='usageTypeLeftPanel']").each(function() {
      if ($(this).hasClass("active")) {
        countOfActiveUsageTypeSelects += 1;
      }
    });
    if ($("#step3AlreadyReached").val() == "false" || countOfActiveUsageTypeSelects == 0) {
      $("#usageTypeDisp").find("li[id^='usageTypeLeftPanel']:first").click();
      $("#step3AlreadyReached").val("true");
    }
  }
  if (currentstep == "step3" && product_action == "create") {
	  var selectedDiscriminatorHasValues = true;
	  var serviceUsageTypeIdForDiscriminator = null;
	  $("#mediationRuleDiscriminators").find("div[id^='addedDiscriminatorValues_']").each(function(){
	    	$selectedDiscriminator = $(this).find("#discvalue_selectbox");
	    	if($selectedDiscriminator.val()== ""){
	    		$selectedDiscriminator.addClass("error");
	    		selectedDiscriminatorHasValues=false;
	    		serviceUsageTypeIdForDiscriminator = $(this).parent().attr("id").split("_")[1];
	    		$(this).find(".js_error").text(i18n.errors.products.errorChooseOption).show();
	    	} else{
	    		$selectedDiscriminator.removeClass("error");
	    		$(this).find(".js_error").text("").hide();
	    	}
	    	
	    });
	  if(!selectedDiscriminatorHasValues){
		  $("#usageTypeLeftPanel_"+serviceUsageTypeIdForDiscriminator).addClass("active");
		  showUsageTypeDiscriminators(serviceUsageTypeIdForDiscriminator);
		  var top = $("#addedDiscriminator_"+serviceUsageTypeIdForDiscriminator).find(".select_desc_name_class.error").offset().top - $("#addedDiscriminator_"+serviceUsageTypeIdForDiscriminator).offset().top - 81;
		  $("#discriminatorsContainer").animate({scrollTop: top},'slow');
		  return;
	  }
    if ($("#showingScalesFor").val() != "" && $("#showingScalesFor").val() != undefined && $("#showingScalesFor").val() !=
      null) {
      if ($("#showingScalesFor").val() != $("#selectedUsageType").val()) {
        $("#usageTypeChanged").val('true');
      }
    }
    if ($("#usageTypeChanged").val() == 'true') {
      var combinedProductGeneratesUsage = $("#selectedUsageType").val();
      $("#showingScalesFor").val(combinedProductGeneratesUsage);
      $("#step4_uom").text(combinedProductGeneratesUsage);
      fetchScalesList(serviceUuid, combinedProductGeneratesUsage);
      $("#conversionFactorValuesError").text('');
      $("#usageTypeChanged").val('false');
    }
    
    if ($("#isProductDiscrete").val() == "true") {
      //Conversion factor for discrete product is set to 1 as there scales in discrete usage types are not allowed.
      //Remove setting of conversion factor to 1 whenever we enable scale for dicrete usage types/UOM.
      $("#conversionFactor").val(1);
      $("#product\\.uom").val($('div[id^="addedusage_"]').first().find('#uom').val());
      $("#step5").find("#prevstep").val('step3');
      nextstep="step5";
    }
   
  }
  if (currentstep == "step3" && product_action == "edit") {
    var jsonMediationRuleMap = JSON.parse($("#jsonMediationRuleMap").val());
    for (key in jsonMediationRuleMap) {
      $("#conversionFactor").find("#step4_uom").text(jsonMediationRuleMap[key]["uom"]);
      $("#conversionFactor").find("#step4_scale").text(jsonMediationRuleMap[key]["productUom"]);
      $("#conversionFactor").find("#step4_conversionfactor").text(jsonMediationRuleMap[key]["conversionFactor"]);
      if(jsonMediationRuleMap[key]["discrete"]){
        $("#step5").find("#prevstep").val('step3');
        nextstep="step5";
      }
      break;
    }
  }
  if (currentstep == "step4" && product_action == "create") {
    $("#conversionFactorValuesError").text('');
    var selectedScale = $("#step4_scale").val();
    var customUnits = $("#step4_custom_units").val();
    var customConversionFactor = $("#step4_conversionFactor").val();
    if (selectedScale == "custom") {
      if (customUnits == null || customUnits == "" || customUnits.indexOf('"') != -1) {
        $("#conversionFactorValuesError").text(g_dictionary.dialogInvalidUnit); //raghav
        return false;
      } else {
        $("#conversionFactorValuesError").text('');
      }

      if (customConversionFactor == null || customConversionFactor == "") {
        $("#conversionFactorValuesError").text(g_dictionary.dialogInvalidFactor); //raghav
        return false;
      } else if (!$.isNumeric($('#step4_conversionFactor').val())) {
        $("#conversionFactorValuesError").text(g_dictionary.dialogInvalidFactorValue);
        return false;
      } else if ($('#step4_conversionFactor').val() <= 0) {
          $("#conversionFactorValuesError").text(g_dictionary.dialogNumberLessThanZero);
          return false;
      } else {
        $("#conversionFactorValuesError").text('');
      }
      $("#product\\.uom").val(customUnits);
    } else {
      $("#product\\.uom").val($("#step4_scale option:selected").attr("id"));
    }
    $("#conversionFactor").val(customConversionFactor);
  }
  if ($(productForm).valid()) {
    if (currentstep == "step1") {
      $step5.find("#confirmProductDetails").find("#name").text($("#product\\.name").val());
      $step5.find("#confirmProductDetails").find("#name").attr("title", $("#product\\.name").val());
      $step5.find("#confirmProductDetails").find("#code").text($("#product\\.code").val());
      $step5.find("#confirmProductDetails").find("#code").attr("title", $("#product\\.code").val());
      $step5.find("#confirmProductDetails").find("#product_category").text($("#categoryID option:selected").text());
      $step5.find("#confirmProductDetails").find("#product_category").attr("title", $("#categoryID option:selected").text());
      $step6.find("#confirmProductDetails").find("#name").text($("#product\\.name").val());
      $step6.find("#confirmProductDetails").find("#code").text($("#product\\.code").val());
      $step6.find("#confirmProductDetails").find("#product_category").text($("#categoryID option:selected").text());
    }
    if (currentstep == "step5") {
      var prodName = $("#product\\.name").val();
      var prodNameToDisplay = "<br>";
      var size = prodName.length;
      var maxsize = 50;
      var count = 0;
      while (size > 50) {
        prodNameToDisplay += prodName.substring(count, count + maxsize) + "<br>";
        count = count + maxsize;
        size = size - 50;
      }
      prodNameToDisplay += prodName.substring(count) + "<br>";
      $step6.find("#successmessage").append(prodNameToDisplay);
    }
    if ((product_action == "create" && currentstep == "step6") ||
      (product_action == "edit" && currentstep == "step5")) {
      $step7.find("#successmessage").append($("#product\\.name").val());
      var productmediationrules;
      if (product_action == "create") {
        productmediationrules = JSON.stringify(getMediationRules());
      } else {
        productmediationrules = JSON.stringify(getMediationRulesForEdit());
      }
      $("#productmediationrules").val(productmediationrules);
      $("#serviceinstanceuuid").val($("#instances").find(".instance_selected").attr("id"));
      var conversionFactorField = $("#conversionFactor");
      conversionFactorField.removeAttr('disabled'); //to take disabled field into account while serializing, otherwise ignored by jquery
      var serializedForm = $(productForm).serialize();
      conversionFactorField.attr('disabled', 'disabled');
      $.ajax({
        type: "POST",
        url: $(productForm).attr('action'),
        data: serializedForm,
        dataType: "json",
        async: false,
        success: function(product) {
          if (product_action == "create") {
            addProductDetailsInListView(product);
          } else {
            editProductDetailsInListView(product);
          }
          $(".j_productspopup").hide();
          $("#" + nextstep).show();

        },
        error: function(XMLHttpRequest) {
          if (XMLHttpRequest.status === AJAX_FORM_VALIDATION_FAILED_CODE) {
            displayAjaxFormError(XMLHttpRequest,
              "productForm",
              "main_addnew_formbox_errormsg");
          } else if (XMLHttpRequest.status === CODE_NOT_UNIQUE_ERROR_CODE) {
            popUpDialogForAlerts("dialog_info", i18n.errors.common.codeNotUnique);
          } else {
            popUpDialogForAlerts("dialog_info", i18n.errors.products.failed_create_product);
          }
        }
      });

    } else if ((product_action == "create" && currentstep == "step7") ||
      (product_action == "edit" && currentstep == "step6")) {
      $currentDialog.dialog("close");
      $("#dialog_add_product").find(".dialog_formcontent").empty();
      $("#dialog_edit_product").find(".dialog_formcontent").empty();
    } else if (currentstep == "step5") {
      $step5.find(".common_messagebox").hide();
      $(".j_productspopup").hide();
      $("#" + nextstep).show();
    } else {
      $(".j_productspopup").hide();
      $("#" + nextstep).show();
    }
  }
}

function addNewProductGet() {
  initDialog("dialog_add_product", 900);
  var actionurl = productsUrl + "createproduct";
  $.ajax({
    type: "GET",
    url: actionurl,
    dataType: "html",
    data: {
      "serviceInstanceUUID": $("#instances").find(".instance_selected").attr("id")
    },
    // dataType : "json",
    success: function(html) {
      var $thisDialog = $("#dialog_add_product");
      $thisDialog.html("");
      $thisDialog.html(html);
      $thisDialog.bind("dialogbeforeclose", function(event, ui) {
        $thisDialog.empty();
      });
      $currentDialog = $thisDialog;
      $currentDialog.dialog('open');
    },
    error: function() {
      $(".addnewproduct").unbind('click');
    }
  });
}

function showUsageTypeDiscriminators(current) {
  $('div[id^="addedDiscriminator_"]').hide();
  $('li[id^="usageTypeLeftPanel_"]').each(function() {
    $(this).removeClass("active");
    $(this).addClass("nonactive");
  });
  $("#addedDiscriminator_" + current).show();
  $("#addedDiscriminator_" + current).find("#discriminatorName").change();
  $('li[id^="usageTypeLeftPanel_' + current + '"]').removeClass("nonactive");
  $('li[id^="usageTypeLeftPanel_' + current + '"]').addClass("active");
}

function editProductPrevious(current) {
  var prevStep = $(current).parents(".j_productspopup").find('#prevstep').val();
  if (prevStep != "") {
    $(".j_productspopup").hide();
    $("#" + prevStep).show();
  }
}

function editProductGet(current) {
  $("#top_actions").find("#spinning_wheel").show();
  initDialog("dialog_edit_product", 785);
  var divId = $(current).attr('id');
  var ID = divId.substr(4);
  var actionurl = productsUrl + "editproduct";
  $.ajax({
    type: "GET",
    url: actionurl,
    data: {
      Id: ID
    },
    dataType: "html",
    success: function(html) {
      var $thisDialog = $("#dialog_edit_product");
      $thisDialog.html("");
      $thisDialog.html(html);
      var $thisDialog = $("#dialog_edit_product");
      $thisDialog.bind("dialogbeforeclose", function(event, ui) {
        $thisDialog.empty();
      });
      $currentDialog = $thisDialog;
      $("#top_actions").find("#spinning_wheel").hide();
      $currentDialog.dialog('open');
    },
    error: function(XMLHttpResponse) {
      $("#top_actions").find("#spinning_wheel").hide();
      popUpDialogForAlerts("dialog_info",XMLHttpResponse.responseText);
    },
    complete:function(){
    } 
  });
}

function editProductImageGet(current, ID) {
  initDialog("dialog_edit_product_image", 550);
  var actionurl = productsUrl + "editlogo";
  $.ajax({
    type: "GET",
    url: actionurl,
    data: {
      "Id": ID
    },
    async: false,
    dataType: "html",
    success: function(html) {
      var $thisDialog = $("#dialog_edit_product_image");
      $thisDialog.empty();
      $thisDialog.html(html);
      $thisDialog.dialog('option', 'buttons', {
        "OK": function() {
          if ($('#productLogoForm').valid()) {
            $('#productLogoForm').iframePostForm({
              iframeID: 'productLogoForm-iframe-post-form',
              json: true,
              post: function() {
                $("#productLogoForm-iframe-post-form").hide();
                return true;
              },
              complete: function() {
                updateproductlogodetails($("#productLogoForm-iframe-post-form"), $thisDialog );
              }
            });
            $('#productLogoForm').submit();
            $('#productLogoForm').remove();
          }
        },
        "Cancel": function() {
          $("#dialog_edit_product_image").empty();
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

function sortorder() {
  initDialog("dialog_sortorder_product", 750);
  var actionurl = productsUrl + "sortproducts";
  var data = {};
  data["serviceInstanceUUID"] = $("#instances").find(".instance_selected").attr("id");
  data["whichPlan"] = $("#whichPlan").val();
  if ($("#whichPlan").val() == 'history' && $("#rpb_history_dates option:selected").val().trim() != "") {
    data['historyDate'] = $("#rpb_history_dates option:selected").val().trim();
  }
  if (currentFilterBy != null) {
    data["filterBy"] = currentFilterBy;
  }
  var category = $("#filter_dropdown").val();
  data["category"] = category;
  $.ajax({
    type: "GET",
    url: actionurl,
    async: false,
    data: data,
    dataType: "html",
    success: function(html) {
      var $thisDialog = $("#dialog_sortorder_product");
      $thisDialog.empty();
      $thisDialog.html(html);
      $thisDialog.dialog('option', 'buttons', {
        "Ok": function() {
          $thisDialog.dialog("close");
          $("#product_tab").click();
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

function updateproductlogodetails(current, dialog) {
  response = $(current).contents().find('body');
  if (response == null || response == "null" || response == "") {
    popUpDialogForAlerts("dialog_info", i18n.errors.products.failed_upload_image);
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
    $("#productimage" + returnReponse.id).attr('src', "/portal/portal/logo/product/" + returnReponse.id + "?t=" + date.getMilliseconds());

  } catch (e) {
    popUpDialogForAlerts("dialog_info", response.html());
  }

}

function viewProductCurrentCharges() {
  var url = productsUrl + $('#productCode').val() + "/viewproductcurrentcharges";
  $.ajax({
    type: "GET",
    url: url,
    dataType: "html",
    async: false,
    success: function(html) {
      $("#productpricing_content").html(html);
      bindActionMenuContainers();
    },
    error: function() {

    }
  });
}

function viewProductPlannedCharges() {
  var url = productsUrl + $('#productCode').val() + "/viewproductplannedcharges";
  $.ajax({
    type: "GET",
    url: url,
    dataType: "html",
    async: false,
    success: function(html) {
      $("#productpricing_content").html(html);
      bindActionMenuContainers();
    },
    error: function() {}
  });
}

function viewChannelPricing(current, showDialog, currenciesToDisplay) {
  $(".widgets_detailstab").removeClass("active").addClass("nonactive");
  $("li[id^='channelpricing_tab']").removeClass("nonactive").addClass("active");
  $('#details_content').hide();
  $('#productpricing_content').hide();
  $("#mediationrules_content").hide();
  $("#tab_spinning_wheel").show();
  var data = {};
  if (currenciesToDisplay != undefined) {
    data['currenciesToDisplay'] = currenciesToDisplay;
  }
  data["whichPlan"] = $("#whichPlan").val();
  if ($("#whichPlan").val() == 'history' && $("#rpb_history_dates option:selected").val().trim() != "") {
    data['historyDate'] = $("#rpb_history_dates option:selected").val().trim();
  }
  // product pricing
  var url = productsUrl + $('#productCode').val() + "/viewproductchannelpricing";
  $.ajax({
    type: "GET",
    url: url,
    data: data,
    dataType: "html",
    async: false,
    success: function(html) {
      if (showDialog === true) {
        initDialog("dialog_view_product_channel_pricing", 800);
        var $thisDialog = $("#dialog_view_product_channel_pricing");
        $thisDialog.html("");
        $thisDialog.html(html);
        $thisDialog.dialog('option', 'buttons', {
          "OK": function() {
            $(this).dialog("close");
            $("#dialog_view_product_channel_pricing").empty();
          }
        });
        dialogButtonsLocalizer($thisDialog, {
          'OK': g_dictionary.dialogOK
        });
        $thisDialog.bind("dialogbeforeclose", function(event, ui) {
          $thisDialog.empty();
        });
        $thisDialog.find(".widget_details_actionbox").remove();
        $thisDialog.find(".widget_grid_cell .moretabbutton").parent().remove();
        $thisDialog.dialog("open");
      } else {
        $("#channelpricing_content").html(html);
      }
    },
    error: function() {}
  });
  $("#tab_spinning_wheel").hide();
  $('#mediationrules_content').hide();
  $('#channelpricing_content').show();
}

function viewProductPricing(current) {
  $(".widgets_detailstab").removeClass("active").addClass("nonactive");
  $(current).removeClass("nonactive").addClass("active");
  $('#details_content').hide();
  $('#channelpricing_content').hide();
  $("#mediationrules_content").hide();
  $("#tab_spinning_wheel").show();

  var whichPlan = $("#whichPlan").val();

  if (whichPlan == "current") {
    viewProductCurrentCharges();
  } else if (whichPlan == "history") {
    viewProductChargesHistory();
  } else {
    viewProductPlannedCharges();
  }

  $("#tab_spinning_wheel").hide();
  $('#mediationrules_content').hide();
  $('#productpricing_content').show();
}

function viewMediationRules(current) {
  $(".widgets_detailstab").removeClass("active").addClass("nonactive");
  $(current).removeClass("nonactive").addClass("active");
  $('#details_content').hide();
  $('#channelpricing_content').hide();
  $("#productpricing_content").hide();
  $("#tab_spinning_wheel").show();

  var data = {};
  data["whichPlan"] = $("#whichPlan").val();
  if ($("#whichPlan").val() == 'history' && $("#rpb_history_dates option:selected").val().trim() != "") {
    data['historyDate'] = $("#rpb_history_dates option:selected").val().trim();
  }
  var url = productsUrl + $('#productCode').val() + "/viewmediationrules";

  $.ajax({
    type: "GET",
    url: url,
    dataType: "html",
    data: data,
    async: false,
    cache: false,
    success: function(html) {
      $("#mediationrules_content").html(html);
      bindActionMenuContainers();
    },
    error: function() {

    }
  });

  $("#tab_spinning_wheel").hide();
  $('#productpricing_content').hide();
  $('#details_content').hide();
  $('#channelpricing_content').hide();
  $('#mediationrules_content').show();
}


function viewplannedcharges(current) {
  initDialog("dialog_view_planned_charges", 905, 600);
  var actionurl = productsUrl + "/viewplannedcharges";
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
  initDialog("dialog_edit_planned_charges", 905, 600);
  var actionurl = productsUrl + "/editplannedcharges";
  $.ajax({
    type: "GET",
    url: actionurl,
    dataType: "html",
    data: {
      "serviceInstanceUUID": $("#instances").find(".instance_selected").attr("id")
    },
    async: false,
    success: function(html) {
      var $thisDialog = $("#dialog_edit_planned_charges");
      $thisDialog.html("");
      $thisDialog.html(html);
      $thisDialog.dialog('option', 'buttons', {
        "OK": function() {
          var productForm = $thisDialog.find("form");
          var isFormValid = $(productForm).valid();
          var hasError = $(productForm).find(".priceRequired").hasClass('error');
          if (hasError == true) {
        	  
        	  var top = $(".plangrid_lightbox").find(".priceRequired.error").offset().top - $(".plangrid_lightbox").offset().top - 81;
    		  $(".plangrid_lightbox").animate({scrollTop: top},'slow');
          }
          if (isFormValid) {
            $.ajax({
              type: "POST",
              url: $(productForm).attr('action'),
              data: $(productForm).serialize(),
              dataType: "html",
              async: false,
              success: function(status) {
                if (status == "success") {
                  $thisDialog.dialog("close");
                  $("#product_tab").click();
                }

              },
              error: function(XMLHttpRequest) {
                $thisDialog.dialog("close");

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
        popUpDialogForAlerts("alert_dialog", i18n.errors.common.uncompatible_currency_precision);
      }
    }
  });
}

function viewProductChargesHistory(data) {
  var url = productsUrl + $('#productCode').val() + "/viewproductchargeshistory";
  var data = {};
  if ($("#rpb_history_dates option:selected").val().trim() != "") {
    data['revisionDate'] = $("#rpb_history_dates option:selected").val().trim();
  }
  $.ajax({
    type: "GET",
    url: url,
    dataType: "html",
    data: data,
    async: false,
    success: function(html) {
      $("#productpricing_content").html(html);
    },
    error: function() {}
  });
}


function i18nProductTypeDisplayName(field) {
  switch (field) {
    case "Volume":
      if (i18n.label.products.type.VOLUME != null && i18n.label.products.type.VOLUME != "undefined") {
        field = i18n.label.products.type.VOLUME;
      }
      break;
    case "Secondary Storage":
      if (i18n.label.products.type.SECONDARY_STORAGE != null && i18n.label.products.type.SECONDARY_STORAGE !=
        "undefined") {
        field = i18n.label.products.type.SECONDARY_STORAGE;
      }
      break;
    case "Internet Data Transfer":
      if (i18n.label.products.type.NETWORK_BYTES != null && i18n.label.products.type.NETWORK_BYTES != "undefined") {
        field = i18n.label.products.type.NETWORK_BYTES;
      }
      break;
    case "IP Address":
      if (i18n.label.products.type.IP_ADDRESS != null && i18n.label.products.type.IP_ADDRESS != "undefined") {
        field = i18n.label.products.type.IP_ADDRESS;
      }
      break;
    case "Port Forwarding Rule":
      if (i18n.label.products.type.PORT_FORWARDING_RULE != null && i18n.label.products.type.PORT_FORWARDING_RULE !=
        "undefined") {
        field = i18n.label.products.type.PORT_FORWARDING_RULE;
      }
      break;
    case "Load Balancing Policy":
      if (i18n.label.products.type.LOAD_BALANCER_POLICY != null && i18n.label.products.type.LOAD_BALANCER_POLICY !=
        "undefined") {
        field = i18n.label.products.type.LOAD_BALANCER_POLICY;
      }
      break;
    case "Running VM":
      if (i18n.label.products.type.RUNNING_VM != null && i18n.label.products.type.RUNNING_VM != "undefined") {
        field = i18n.label.products.type.RUNNING_VM;
      }
      break;
    case "Hypervisor":
      if (i18n.label.products.type.HYPERVISOR != null && i18n.label.products.type.HYPERVISOR != "undefined") {
        field = i18n.label.products.type.HYPERVISOR;
      }
      break;
    case "ISO Group":
      if (i18n.label.products.type.ISO_GROUP != null && i18n.label.products.type.ISO_GROUP != "undefined") {
        field = i18n.label.products.type.ISO_GROUP;
      }
      break;
    case "Template Group":
      if (i18n.label.products.type.TEMPLATE_GROUP != null && i18n.label.products.type.TEMPLATE_GROUP != "undefined") {
        field = i18n.label.products.type.TEMPLATE_GROUP;
      }
      break;
    case "Stopped VM":
      if (i18n.label.products.type.STOPPED_VM != null && i18n.label.products.type.STOPPED_VM != "undefined") {
        field = i18n.label.products.type.STOPPED_VM;
      }
      break;
  }
  return field;
}

function pouplateUsageTypeDropDown(usageType, usageTypeId) {
  var serviceUsageTypeOptions = [];
  serviceUsageTypeOptions.push('<option value="">' + commmonmessages.choose_label + '</option>');
  var serviceUsageTypes = JSON.parse($("#serviceUsageTypes").val());
  if (usageType == "") {
    for (var i = 0; i < serviceUsageTypes.length; i++) {
      serviceUsageTypeOptions.push('<option value="', serviceUsageTypes[i].id, '"uom="', serviceUsageTypes[i].serviceUsageTypeUom
        .name, '"serviceUsageTypeId="', serviceUsageTypes[i].id,
        ' "isServiceUsageTypeDiscrete="', serviceUsageTypes[i].discrete, '">',
        l10discAndUsageTypeNames[serviceUsageTypes[i].usageTypeName + "-name"], '</option>');
    }
  } else {
    var availableServiceUsageTypes = [];
    for (var i = 0; i < serviceUsageTypes.length; i++) {
      var usageTypeFound = false;
      $("#mediationRules").find("div[id^='addedusage_']").each(function() {
        if ($(this).find("#usagetype").val() == serviceUsageTypes[i].id) {
          usageTypeFound = true;
        }
      });
      if (usageTypeId == serviceUsageTypes[i].id) {
        usageTypeFound = true;
      }
      if (usageTypeFound == false) {
        availableServiceUsageTypes.push(serviceUsageTypes[i]);
      }
    }

    for (var i = 0; i < availableServiceUsageTypes.length; i++) {
      if (usageType.trim().toLowerCase() == availableServiceUsageTypes[i].serviceUsageTypeUom.name.trim().toLowerCase()) {
        serviceUsageTypeOptions.push('<option value="', availableServiceUsageTypes[i].id, '"uom="',
          availableServiceUsageTypes[i].serviceUsageTypeUom.name, '"serviceUsageTypeId="', availableServiceUsageTypes[i]
          .id,' "isServiceUsageTypeDiscrete="', availableServiceUsageTypes[i].discrete, '">',
          l10discAndUsageTypeNames[availableServiceUsageTypes[i].usageTypeName + "-name"], '</option>');
      }
    }
  }
  $("#usagetype").html(serviceUsageTypeOptions.join(''));
}

function getUsageTypes() {
  var serviceInstanceUUID = $("#instances").find(".instance_selected").attr("id");
  var serviceUsageTypeOptions = [];
  serviceUsageTypeOptions.push($("#usagetype").html());
  $.ajax({
    type: "GET",
    url: "/portal/portal/products/listUsageTypes?serviceInstanceUUID=" + serviceInstanceUUID,
    dataType: "json",
    success: function(serviceUsageTypes) {
      $("#serviceUsageTypes").val(JSON.stringify(serviceUsageTypes));
      pouplateUsageTypeDropDown("", null);
    },
    error: function() {}
  });
}

function addDiscriminatorRow(current) {
  var sourceUsageTypeId = $(current).attr('id').substring(42); // add_discriminator_link_addedDiscriminator_
  // length =42
  var discriminatorRowTemplate = $("#addedDiscriminator_" + sourceUsageTypeId).find("#discriminatorRow");
  var newDiscriminatorRow = discriminatorRowTemplate.clone();

  var lastDiscAddedCount = 1;
  var lastDiscAddedId = $("#addedDiscriminator_" + sourceUsageTypeId).find("div[id^='addedDiscriminatorValues_']:last")
    .attr("id");
  if (lastDiscAddedId != undefined) {
    var spltArray = lastDiscAddedId.split("_");
    lastDiscAddedCount = parseInt(spltArray[spltArray.length - 1]) + 1;
  }
  var discriminatorRowId = "addedDiscriminatorValues_" + lastDiscAddedCount;
  newDiscriminatorRow.attr("id", discriminatorRowId);

  var jsonDiscDict = JSON.parse($("#discDict").val());
  var discriminatorId = discriminatorRowTemplate.find("#discriminatorName option:selected").val();
  var discValDict = jsonDiscDict[sourceUsageTypeId][discriminatorId]["discriminatorValues"];

  newDiscriminatorRow.find("#discriminatorNameSpan").text(discriminatorRowTemplate.find(
    "#discriminatorName option:selected").text());
  newDiscriminatorRow.find("#discriminatorNameSpan").attr("value", discriminatorRowTemplate.find(
    "#discriminatorName option:selected").val());
  newDiscriminatorRow.find("#discriminatorName").remove();
  newDiscriminatorRow.find("#discriminatorNameSpan").show();
  newDiscriminatorRow.find("#discriminatorNameSpan").attr("id", "discriminatorName");
  var alreadyAddedDiscriminator = false;
  var $discriminatorOperatorhtml;
  $("#addedDiscriminator_" + sourceUsageTypeId).find("div[id^='addedDiscriminatorValues_']").each(function() {
    if ($(this).find("#discriminatorName").text() == discriminatorRowTemplate.find(
      "#discriminatorName option:selected").text()) {
      alreadyAddedDiscriminator = true;
      $discriminatorOperatorhtml = $(this).find("#discriminatorOperatorDiv option").clone();

    }
  });

  var className = "js_addedDiscriminatorOperator_" + sourceUsageTypeId + "_" + discriminatorRowTemplate.find(
    "#discriminatorName option:selected").val();
  var discriminatorOperatorId = lastDiscAddedCount + "-" + className;
  newDiscriminatorRow.find("#discriminatorOperator").addClass(className);

  if (alreadyAddedDiscriminator == true) {
    var first_selected_option = $("." + className).val();
    newDiscriminatorRow.find("#discriminatorOperator").val(first_selected_option);
  }
  newDiscriminatorRow.find("#discriminatorOperator").attr("id", discriminatorOperatorId);
  newDiscriminatorRow.find("#discriminatorOperatorDiv").show();
  newDiscriminatorRow.find(".vm_tooltip").show();
  var count = 0;
  for (key in discValDict) {
    count += 1;
  }
  if (count > 0) {
    newDiscriminatorRow.find("#grid_cell_discvalue_selectbox").attr("selectOrInput", "opt_select");
    newDiscriminatorRow.find("#grid_cell_discvalue_selectbox").show();
    var selectHtml = newDiscriminatorRow.find("#discvalue_selectbox").html();
    for (key in discValDict) {
      selectHtml += '<option value="' + discValDict[key] + '">' + key + '</option>';
    }
    newDiscriminatorRow.find("#discvalue_selectbox").html(selectHtml);
  } else {
    newDiscriminatorRow.find("#grid_cell_discvalue_selectbox").remove();
    newDiscriminatorRow.find("#grid_cell_discvalue_inputbox").attr("selectOrInput", "opt_input");
    newDiscriminatorRow.find("#grid_cell_discvalue_inputbox").show();
  }
  var deleteDiscriminatorHtml = "<a class='delete' href='javascript:removeDiscriminatorValue(" + '"' +
    discriminatorRowId + '"' + ',"' + sourceUsageTypeId + '"' + ");'></a>";
  newDiscriminatorRow.find("#add_discriminator_link_addedDiscriminator_" + sourceUsageTypeId).parent().html(
    deleteDiscriminatorHtml);
  discriminatorRowTemplate.find("#usagetype option:eq(" + 0 + ")").attr('selected',
    'selected');
  discriminatorRowTemplate.find("#operator option:eq(" + 0 + ")").attr("selected",
    "selected");
  $("#addedDiscriminator_" + sourceUsageTypeId).append(newDiscriminatorRow);
  newDiscriminatorRow.show();
}

function addDiscriminatorRowInEdit(current) {
  var idSpltArray = $(current).attr('id').split("_");
  var sourceUsageTypeId = idSpltArray[idSpltArray.length - 1];
  var medRuleId = $(current).attr('medRuleId');
  var discriminatorRowTemplate = $("#addedDiscriminator_" + medRuleId).find("#discriminatorRow");
  var newDiscriminatorRow = discriminatorRowTemplate.clone();

  var discriminatorId = discriminatorRowTemplate.find("#discriminatorName option:selected").val();
  var lastDiscAddedCount = 1;
  var lastDiscAddedId = $("#addedDiscriminator_" + medRuleId).find("div[id^='addedDiscriminatorValues_']:last").attr(
    "id");
  if (lastDiscAddedId != undefined) {
    var spltArray = lastDiscAddedId.split("_");
    lastDiscAddedCount = parseInt(spltArray[spltArray.length - 1]) + 1;
  }
  var discriminatorRowId = "addedDiscriminatorValues_" + lastDiscAddedCount;
  newDiscriminatorRow.attr("id", discriminatorRowId);

  var jsonUsageTypeDiscriminatorMap = JSON.parse($("#jsonUsageTypeDiscriminatorMap").val());
  var discValDict = jsonUsageTypeDiscriminatorMap[sourceUsageTypeId]["discriminators"][discriminatorId][
    "discriminatorValues"
  ];

  newDiscriminatorRow.find("#discriminatorNameSpan").text(discriminatorRowTemplate.find(
    "#discriminatorName option:selected").text());
  newDiscriminatorRow.find("#discriminatorNameSpan").attr("value", discriminatorRowTemplate.find(
    "#discriminatorName option:selected").val());
  newDiscriminatorRow.find("#discriminatorName").remove();
  newDiscriminatorRow.find("#discriminatorNameSpan").show();
  newDiscriminatorRow.find("#discriminatorNameSpan").attr("id", "discriminatorName");


  newDiscriminatorRow.find("#discriminatorOperatorDiv").show();
  newDiscriminatorRow.find(".vm_tooltip").show();
  var count = 0;
  for (key in discValDict) {
    count += 1;
  }
  if (count > 0) {
    newDiscriminatorRow.find("#grid_cell_discvalue_selectbox").attr("selectOrInput", "opt_select");
    newDiscriminatorRow.find("#grid_cell_discvalue_selectbox").show();
    var selectHtml = newDiscriminatorRow.find("#discvalue_selectbox").html();
    for (key in discValDict) {
      selectHtml += '<option value="' + discValDict[key] + '">' + key + '</option>';
    }
    newDiscriminatorRow.find("#discvalue_selectbox").html(selectHtml);
  } else {
    newDiscriminatorRow.find("#grid_cell_discvalue_inputbox").attr("selectOrInput", "opt_input");
    newDiscriminatorRow.find("#grid_cell_discvalue_inputbox").show();
  }
  var deleteDiscriminatorHtml = "<a class='delete' href='javascript:removeDiscriminatorValue(" + '"' +
    discriminatorRowId + '"' + ',"' + medRuleId + '"' + ");'></a>";
  newDiscriminatorRow.find("#add_discriminator_link_" + sourceUsageTypeId).parent().html(deleteDiscriminatorHtml);
  discriminatorRowTemplate.find("#usagetype option:eq(" + 0 + ")").attr('selected',
    'selected');
  discriminatorRowTemplate.find("#operator option:eq(" + 0 + ")").attr("selected",
    "selected");
  $("#addedDiscriminator_" + medRuleId).append(newDiscriminatorRow);
  newDiscriminatorRow.show();
}

function removeDiscriminatorValue(discriminatorRowId, sourceUsageTypeId) {
  $("#addedDiscriminator_" + sourceUsageTypeId).find("#" + discriminatorRowId).remove();
}

function addUsageType(current) {
	
  var usageTypeTemplate = $("#mediationRules").find("#usageTypeAdd");
  var newUsageType = $("#mediationRules").find("#usageTypeAdded").clone();
  var usageTypeCount = $("div[id^='addedusage']").size();
  var usageId = 1;
  if (usageTypeCount > 0) {
    usageId = parseInt($("div[id^='addedusage']:last").attr("id").replace("addedusage_", "")) + 1;
  }
  var usageTypeId = "addedusage_" + usageId;
  $("#productMedRuleSelectError").html("");
  newUsageType.attr("id", usageTypeId);
  var selectedUsageTypeOptionIndex = $("#mediationRules").find("#usageTypeAdd").find("#usagetype option:selected").index();
  if (selectedUsageTypeOptionIndex == 0) {
    return;
  }

  newUsageType.find("#usagetype").text(usageTypeTemplate.find("#usagetype option:selected").text());
  newUsageType.find("#usagetype").attr("value", usageTypeTemplate.find("#usagetype option:selected").val());

  newUsageType.find("#operator").text(usageTypeTemplate.find("#operator option:selected").text());
  newUsageType.find("#operator").attr("value", usageTypeTemplate.find("#operator option:selected").val());

  newUsageType.find("#conversionfactor").text(usageTypeTemplate.find("#conversionfactor").val());
  newUsageType.find("#conversionfactor").attr("value", usageTypeTemplate.find("#conversionfactor").val());

  newUsageType.find("#uomtext").text(usageTypeTemplate.find("#uomtext").text());
  newUsageType.find("#uomtext").attr("value", usageTypeTemplate.find("#uomtext").text());

  
  newUsageType.find("#uom").val(usageTypeTemplate.find("#uom").val());
  newUsageType.find("#uom").attr("value", usageTypeTemplate.find("#uom").val());

  $("#isProductDiscrete").val(usageTypeTemplate.find("#usagetype option:selected").attr("isServiceUsageTypeDiscrete"));
  
  var usageTypeUom = $("#mediationRules").find("#usageTypeAdd").find("#usagetype option:selected").attr("uom");
  var serviceUsageTypeId = usageTypeTemplate.find("#usagetype option:selected").attr("serviceusagetypeid");


  var deleteHtml = "<a class='delete' href='javascript:removeUsageType(" + '"' + usageTypeId + '"' + ',"' +
    serviceUsageTypeId + '"' + ");'></a>";
  newUsageType.find("#operations").html(deleteHtml);

  // Reset the template html
  usageTypeTemplate.find("#conversionfactor").val("1.00");
  usageTypeTemplate.find("#uom").val("");
  usageTypeTemplate.find("#uomtext").html("");
  usageTypeTemplate.find("#usagetype option:eq(0)").attr('selected', 'selected');
  usageTypeTemplate.find("#operator option:eq(0)").attr("selected", "selected");

  // Add code for updating the next step of the product creation
  var mediationDiscriminatorsBlock = $("#mediationRuleDiscriminators");
  var newUsageTypeDiscriminatorBlock = mediationDiscriminatorsBlock.find("#usagetype_discriminator_block_template").clone();
  var serviceInstanceUUID = $("#instances").find(".instance_selected").attr("id");
  var serviceUsageTypeId = newUsageType.find("#usagetype").attr('value');

  var usageTypeDiscriminatorId = "addedDiscriminator_" + serviceUsageTypeId;
  newUsageTypeDiscriminatorBlock.attr("id", usageTypeDiscriminatorId);
  newUsageTypeDiscriminatorBlock.find("#add_discriminator_link").attr("id", "add_discriminator_link_" +
    usageTypeDiscriminatorId);
  usageTypeHtml = '<li id="usageTypeLeftPanel_' + serviceUsageTypeId +
    '" class="widget_navigationlist"  onclick="javascript:showUsageTypeDiscriminators(' + "'" + serviceUsageTypeId +
    "'" + ');">' +
    '<span class="navicon RUNNING_VM" style="margin-top:4px;"></span><span class="title" serviceUsageTypeId="' +
    serviceUsageTypeId + '" title="' + newUsageType.find("#usagetype").text() + '">' + newUsageType.find("#usagetype").text() + '</span></li>';
  $("#usageTypeDisp").append(usageTypeHtml);

  var discNamesOptionName = [];
  var actonUrl = productsUrl + "listDiscriminators?serviceInstanceUUID=" + serviceInstanceUUID + "&serviceUsageTypeId=" +
    serviceUsageTypeId;
  $("#spinning_wheel_cp").show();
  var ajaxCallSuccess = true;
  $.ajax({
    type: "GET",
    url: actonUrl,
    dataType: "json",
    async: false,
    cache: false,
    success: function(discDict) {
      var jsonDiscDict = JSON.parse($("#discDict").val());
      var hasDiscriminator=false;
      for (key in discDict) {
        hasDiscriminator=true;
        discNamesOptionName.push('<option value="', key, '" serviceUsageTypeId="', serviceUsageTypeId, '">',
          l10discAndUsageTypeNames[discDict[key]["name"] + "-name"], '</option>');
      }
      if(hasDiscriminator){
      newUsageTypeDiscriminatorBlock.find("#discriminatorName").html(
        discNamesOptionName.join(''));
      mediationDiscriminatorsBlock.append(newUsageTypeDiscriminatorBlock);
      }else{
    	  
    	  mediationDiscriminatorsBlock.append('<div id="'+usageTypeDiscriminatorId+'" class="alert alert-info pull-left" style="width:540px;">'+g_dictionary.noMediationRulesForUsageType+'</div>')
      }
      $("#step3AlreadyReached").val("false");
      jsonDiscDict[serviceUsageTypeId] = discDict;
      $("#discDict").val(JSON.stringify(jsonDiscDict));
    },
    error: function(XMLHttpResponse) {
      ajaxCallSuccess = false;
      popUpDialogForAlerts("dialog_info",XMLHttpResponse.responseText);
    },
    complete: function(){
    }
  });
  $("#spinning_wheel_cp").hide();
  if(ajaxCallSuccess){
    pouplateUsageTypeDropDown(usageTypeUom, serviceUsageTypeId);
    newUsageType.show();
    $("#mediationRules").append(newUsageType);
  }
}

function changeUom(sel) {
  var uom = $(sel).find(':selected').attr('uom');
  $(sel).parent().parent().find("#uom").val(uom);
  var message = i18n.label.products.discrete;
  if ($(sel).find(':selected').attr('isServiceUsageTypeDiscrete')== "true") {
      $(sel).parent().parent().find("#uomtext").html(uom +' ('+ message+')');
  }else{
     $(sel).parent().parent().find("#uomtext").html(uom);
 }
}

function removeUsageType(usageId, serviceUsageTypeId) {
  var usageIdNo = $("#mediationRules").find("#" + usageId).find("#usagetype").attr("value");
  if ($("#usageTypeLeftPanel_" + usageIdNo) != undefined) {
    $("#usageTypeLeftPanel_" + usageIdNo).remove();
    $("#addedDiscriminator_" + usageIdNo).remove();
  }
  $("#mediationRules").find("#" + usageId).remove();

  var usageTypeCount = $("div[id^='addedusage']").size();
  if (usageTypeCount == 0) {
    pouplateUsageTypeDropDown("", null);

    var usageTypeTemplate = $("#mediationRules").find("#usageTypeAdd");
    usageTypeTemplate.find("#conversionfactor").val("1.00");
    usageTypeTemplate.find("#uomtext").html("");
    usageTypeTemplate.find("#uom").val("");
    usageTypeTemplate.find("#usagetype option:eq(0)").attr('selected', 'selected');
    usageTypeTemplate.find("#operator option:eq(0)").attr("selected", "selected");
  } else {
    var serviceUsageTypeOptions = [];
    var serviceUsageTypes = JSON.parse($("#serviceUsageTypes").val());
    for (var i = 0; i < serviceUsageTypes.length; i++) {
      if (serviceUsageTypeId == serviceUsageTypes[i].id) {
        serviceUsageTypeOptions.push('<option value="', serviceUsageTypes[i].id, '"uom="', serviceUsageTypes[i].serviceUsageTypeUom
          .name, '"serviceUsageTypeId="', serviceUsageTypes[i].id, '">',
          l10discAndUsageTypeNames[serviceUsageTypes[i].usageTypeName + "-name"], '</option>');
      }
    }
    $("#usagetype").append(serviceUsageTypeOptions.join(''));
  }
}

function populateEntriesInStep2OfProductEdit() {
  var jsonMediationRuleMap = JSON.parse($("#jsonMediationRuleMap").val());
  var jsonUsageTypeDiscriminatorMap = JSON.parse($("#jsonUsageTypeDiscriminatorMap").val());
  for (key in jsonMediationRuleMap) {
    var newUsageType = $("#mediationRules").find("#usageTypeAdd").clone();
    var usageTypeName = jsonMediationRuleMap[key]["usageType"];
    newUsageType.find("#usagetype").html(l10discAndUsageTypeNames[usageTypeName + "-name"]);
    newUsageType.find("#conversionfactor").text(jsonMediationRuleMap[key]["conversionFactor"]);
    newUsageType.find("#operator").html(i18n.label.products['usage'+jsonMediationRuleMap[key]["operator"].toLowerCase()]);
    if(jsonMediationRuleMap[key]["discrete"]){
     var discreteMsg = i18n.label.products.discrete;
     newUsageType.find("#uom").text(jsonMediationRuleMap[key]["uom"] +' '+ discreteMsg);
    }else{
    newUsageType.find("#uom").text(jsonMediationRuleMap[key]["uom"]);
  }
    var serviceUsageTypeId = jsonMediationRuleMap[key]["usageTypeId"];
    newUsageType.attr("id", serviceUsageTypeId);
    $("#mediationRules").append(newUsageType);
    newUsageType.show();

    populateStep3EntriesOfProductEdit(jsonMediationRuleMap[key]["discriminators"], jsonUsageTypeDiscriminatorMap[
        serviceUsageTypeId]["discriminators"],
      serviceUsageTypeId, usageTypeName, key);
    $("#usageTypeDisp").find("li[id^='usageTypeLeftPanel']:first").click();
  }
}

function showUsageTypeDiscriminatorsInEdit(medRuleId) {
  $('div[id^="addedDiscriminator_"]').hide();
  $('li[id^="usageTypeLeftPanel_"]').each(function() {
    $(this).removeClass("active").addClass("nonactive");
  });
  $("#addedDiscriminator_" + medRuleId).show();
  $("#addedDiscriminator_" + medRuleId).find("#discriminatorName").change();
  $('li[id^="usageTypeLeftPanel_' + medRuleId + '"]').addClass("active");
}

function populateStep3EntriesOfProductEdit(discsAlreadyAddedDict, discDict, serviceUsageTypeId, usageTypeName,
  medRuleId) {
  // Add left side panel entries
  usageTypeHtml = '<li id="usageTypeLeftPanel_' + medRuleId + '" medRuleId="' + medRuleId +
    '" class="widget_navigationlist"  onclick="javascript:showUsageTypeDiscriminatorsInEdit(' + "'" + medRuleId + "'" +
    ');">' + '<span class="navicon RUNNING_VM" style="margin-top:4px;"></span><span class="title" serviceUsageTypeId="' +
    serviceUsageTypeId + '" title="' + l10discAndUsageTypeNames[usageTypeName + "-name"] + '">' + l10discAndUsageTypeNames[usageTypeName + "-name"] + '</span></li>';
  $("#usageTypeDisp").append(usageTypeHtml);

  var isDiscAlreadyAdded = false;
  var listOfDiscElementsAdded = [];
  var count = 1;
  for (key in discsAlreadyAddedDict) {
    isDiscAlreadyAdded = true;
    var addedDiscElement = $("#mediationRuleDiscriminators").find("#discriminatorRowForAlreadyAddedDisc").clone();
    var discriminatorRowId = "addedDiscriminatorValues_" + count;
    addedDiscElement.attr("id", discriminatorRowId);
    count += 1;

    addedDiscElement.find("#discriminatorName").text(l10discAndUsageTypeNames[discsAlreadyAddedDict[key][
      "discriminatorType"
    ] + "-name"]);
    addedDiscElement.find("#discriminatorName").attr("discEntityId", key);

    var discTypeId = discsAlreadyAddedDict[key]["discriminatorTypeId"];
    var discValueToShow = discsAlreadyAddedDict[key]["discrimniatorValue"];
    for (key1 in discDict[discTypeId]["discriminatorValues"]) {
      if (discDict[discTypeId]["discriminatorValues"][key1] == discsAlreadyAddedDict[key]["discrimniatorValue"]) {
        discValueToShow = key1;
      }
    }
    addedDiscElement.find("#discValue").text(discValueToShow);

    if (discsAlreadyAddedDict[key]["operator"].toLowerCase() == "equal_to") {
      addedDiscElement.find("#operator").text(l10discAndUsageTypeNames["INCLUDES"]);
    } else {
      addedDiscElement.find("#operator").text(l10discAndUsageTypeNames["EXCLUDES"]);
    }
    addedDiscElement.find(".delete").remove();
    addedDiscElement.show();
    listOfDiscElementsAdded.push(addedDiscElement);
  }

  if (isDiscAlreadyAdded == true) {
    var newUsageTypeDiscriminatorBlock = $("#usagetype_discriminator_block_template").clone();

    var usageTypeDiscriminatorId = "addedDiscriminator_" + medRuleId;
    newUsageTypeDiscriminatorBlock.attr("id", usageTypeDiscriminatorId);

    newUsageTypeDiscriminatorBlock.find("#add_discriminator_link").attr("medRuleId", medRuleId);
    newUsageTypeDiscriminatorBlock.find("#add_discriminator_link").attr("id", "add_discriminator_link_" +
      serviceUsageTypeId);
    var discNamesOptionName = [];
    for (key in discDict) {
      discNamesOptionName.push('<option value="', key, '" serviceUsageTypeId="', serviceUsageTypeId, '">',
        l10discAndUsageTypeNames[discDict[key]["name"] + "-name"], '</option>');
    }

    newUsageTypeDiscriminatorBlock.find("#discriminatorName").html(discNamesOptionName.join(''));
    for (var i = 0; i < listOfDiscElementsAdded.length; i++) {
      newUsageTypeDiscriminatorBlock.append(listOfDiscElementsAdded[i]);
    }
    $("#mediationRuleDiscriminators").append(newUsageTypeDiscriminatorBlock);
  }

}

function viewReferencePriceBookHistory(isProductOn) {
  if ($("#rpb_history_dates option:selected").val().trim() == "") {
    return;
  }
  var url = "/portal/portal/productBundles/list";
  if ($("#prod_bundles_container").find(".on:first").attr("id") == "product_tab") {
    url = "/portal/portal/products/listproducts";
  }
  var data = {};
  data["whichPlan"] = $("#whichPlan").val();
  data["revisionDate"] = $("#rpb_history_dates option:selected").val().trim();
  data["serviceInstanceUUID"] = data["serviceInstanceUUID"] = $("#instances").find(".instance_selected").attr("id");
  $.ajax({
    url: url,
    dataType: "html",
    data: data,
    async: false,
    cache: false,
    success: function(html) {
      if ($("#prod_bundles_container").find(".on:first").attr("id") == "product_tab") {
        $("#productBundleListingDiv").empty();
        $("#productBundleListingDiv").html(html);
        $("#productgridcontent").find(".j_viewproduct:first").click();
      } else {
        $("#productBundleListingDiv").empty();
        $("#productBundleListingDiv").html(html);
        $("#productBundleListingDiv").find("#productBundlegridcontent").find(".j_viewbundle:first").click();
      }

    },
    error: function(XMLHttpResponse) {
      handleError(XMLHttpResponse);
    }
  });
  bindCatagoryDropDown();
}

function fillInstancesList(current) {
  $(current).parents("#serviceCategories").find("a").each(function() {
    $(this).removeClass("on");
  });
  $(current).addClass("on");

  var data = {};
  data["serviceUUID"] = $(current).attr("id");
  data["whichPlan"] = $("#whichPlan").val();
  if ($("#whichPlan").val() == "history") {
    data["revisionDate"] = $("#rpb_history_dates option:selected").val().trim();
  }
  $.ajax({
    type: "GET",
    url: "/portal/portal/products/getServiceInstances",
    data: data,
    async: false,
    cache: false,
    success: function(html) {
      $("#maincontent_container").empty();
      $("#maincontent_container").html($(html).find("#maincontent_container").html());
      $(html).find("#instances").find("a:first").click();
    },
    error: function(XMLHttpResponse) {
      handleError(XMLHttpResponse);
    }
  });
}

function getProductOrBundleListing(current) {
  $(current).parents("#instances").find("a").each(function() {
    $(this).attr("style", "color: #2C8BBC;max-width: 120px;");
    $(this).removeClass("instance_selected");
  });
  $(current).attr("style", "color: #000;max-width: 120px;");
  $(current).removeClass("instance_not_selected");
  $(current).addClass("instance_selected");
  
  var $productTab = $("#prod_bundles_container").find("#product_tab");
  
  if($(current).attr("serviceHasUsageType") == "true"){
    $productTab.show();
    $productTab.removeClass("off");
    $productTab.addClass("on");
  } else {
    $productTab.hide();
    $productTab.removeClass("on");
    $productTab.addClass("off");
  }

  if ($("#prod_bundles_container").find(".on:first").attr("id") == "product_tab") {
    listProducts($("#product_tab"));
  } else {
    listBundles($("#product_bundle_tab"));
  }
}

function listProducts(current) {

  $(current).parents("#prod_bundles_container").find("li").each(function() {
    $(this).removeClass("on");
  });
  $(current).removeClass("off");
  $(current).addClass("on");
  $("#full_page_spinning_wheel").show();
  $("#all_product").show();
  $("#all_product_separater").show();
  $("#active_product").show();
  $("#active_product_separater").show();
  $("#retire_product").show();
  $("#retire_product_separater").show();
  $("#retire_product_separater").addClass("last");

  $("#all_productbundle_separater").addClass("last");
  $("#all_productbundle").hide();
  $("#publish_productbundle_separater").addClass("last");
  $("#publish_productbundle").hide();
  $("#unpublish_productbundle_separater").addClass("last");
  $("#unpublish_productbundle").hide();

  if ($("#instances").find(".instance_selected").length == 0) {
    return;
  }
  $("#all_product").css({
    'color': '#2C8BBC'
  });
  $("#active_product").css({
    'color': '#000'
  });
  $("#retire_product").css({
    'color': '#2C8BBC'
  });
  
  currentFilterBy = "active"

  var data = {};
  if ($("#whichPlan").val() == "history") {
    if ($("#rpb_history_dates option:selected").val().trim() == "") {
      return;
    }
    data["revisionDate"] = $("#rpb_history_dates option:selected").val().trim();
  }
  data["whichPlan"] = $("#whichPlan").val();
  data["serviceInstanceUUID"] = $("#instances").find(".instance_selected").attr("id");
  data["filterBy"] = currentFilterBy;
  $.ajax({
    url: "/portal/portal/products/listproducts",
    dataType: "html",
    data: data,
    async: false,
    cache: false,
    success: function(html) {
      $("#productBundleListingDiv").empty();
      $("#productBundleListingDiv").html(html);
      $("#productBundleListingDiv").find("#productgridcontent").find(".j_viewproduct:first").click();
      $("#full_page_spinning_wheel").hide();
    },
    error: function(XMLHttpResponse) {
      $("#full_page_spinning_wheel").hide();
      handleError(XMLHttpResponse);
    }
  });
  
  bindCatagoryDropDown();
}

function listBundles(current) {
  productBundlefilterby = "all";

  $(current).parents("#prod_bundles_container").find("li").each(function() {
    $(this).removeClass("on");
  });
  $(current).removeClass("off");
  $(current).addClass("on");
  $("#full_page_spinning_wheel").show();
  $("#all_product").hide();
  $("#all_product_separater").hide();
  $("#active_product").hide();
  $("#active_product_separater").hide();
  $("#retire_product").hide();
  $("#retire_product_separater").hide();

  $("#all_productbundle").show();
  $("#all_productbundle_separater").removeClass("last");
  $("#publish_productbundle").show();
  $("#publish_productbundle_separater").removeClass("last");
  $("#unpublish_productbundle").show();


  if ($("#instances").find(".instance_selected").length == 0) {
    return;
  }
  $("#all_productbundle").css({
    'color': '#000'
  });
  $("#publish_productbundle").css({
    'color': '#2C8BBC'
  });
  $("#unpublish_productbundle").css({
    'color': '#2C8BBC'
  });

  var data = {};
  if ($("#whichPlan").val() == "history") {
    if ($("#rpb_history_dates option:selected").val().trim() == "") {
      return;
    }
    data["revisionDate"] = $("#rpb_history_dates option:selected").val().trim();
  }
  data["whichPlan"] = $("#whichPlan").val();
  data["serviceInstanceUUID"] = $("#instances").find(".instance_selected").attr("id");
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
      $("#full_page_spinning_wheel").hide();
    },
    error: function(XMLHttpResponse) {
      $("#full_page_spinning_wheel").hide();
      handleError(XMLHttpResponse);
    }
  });
  
}

var currentFilterBy;

function listProductByFilter(current, filter, currentPage, searchPattern) {
  if (filter != null) {
    currentFilterBy = filter;
  } else {
    filter = currentFilterBy;
  }
  if (current != null) {
    $(current).parents("#prod_bundles_container").find("li").each(function() {
      $(this).removeClass("on");
    });
    $(current).removeClass("off");
    $(current).addClass("on");
  }

  $("#all_product").show();
  $("#active_product").show();
  $("#retire_product").show();
  $("#all_productbundle").hide();
  $("#publish_productbundle").hide();
  $("#unpublish_productbundle").hide();

  $("#all_product").css({
    'color': '#2C8BBC'
  })
  $("#active_product").css({
    'color': '#2C8BBC'
  })
  $("#retire_product").css({
    'color': '#2C8BBC'
  })

  if (filter == 'all') {
    $("#all_product").css({
      'color': '#000'
    });
  }
  if (filter == 'active') {
    $("#active_product").css({
      'color': '#000'
    });
  }

  if (filter == 'retire') {
    $("#retire_product").css({
      'color': '#000'
    });
  }
  if (filter == null) {
    $("#all_product").css({
      'color': '#000'
    });
  }

  if ($("#instances").find(".instance_selected").length == 0) {
    return;
  }

  var data = {};
  if ($("#whichPlan").val() == "history") {
    if ($("#rpb_history_dates option:selected").val().trim() == "") {
      return;
    }
    data["revisionDate"] = $("#rpb_history_dates option:selected").val().trim();
  }
  if (currentPage != undefined) {
    data["currentPage"] = currentPage;
  }
  if (searchPattern != undefined) {
    data["namePattern"] = searchPattern;
  }

  data["whichPlan"] = $("#whichPlan").val();
  data["serviceInstanceUUID"] = $("#instances").find(".instance_selected").attr("id");
  data["filterBy"] = filter;
  var category = $("#filter_dropdown").val();
  data["category"] = category;
  $.ajax({
    url: "/portal/portal/products/listproducts",
    dataType: "html",
    data: data,
    async: false,
    cache: false,
    success: function(html) {
      $("#productBundleListingDiv").empty();
      $("#productBundleListingDiv").html(html);
      $("#productBundleListingDiv").find("#productgridcontent").find(".j_viewproduct:first").click();
      $("#filter_dropdown").val(category);

      if (filter == 'all') {
        $("#all_product").css({
          'color': '#000'
        });
      }
      if (filter == 'active') {
        $("#active_product").css({
          'color': '#000'
        });
      }
      if (filter == 'retire') {
        $("#retire_product").css({
          'color': '#000'
        });
      }
    },
    error: function(XMLHttpResponse) {
      handleError(XMLHttpResponse);
    }
  });
  bindCatagoryDropDown();
}

var productBundlefilterby;

function listProductBundlesByFilter(current, filter) {
  $(current).parents("#prod_bundles_container").find("li").each(function() {
    $(this).removeClass("on");
  });
  $(current).removeClass("off");
  $(current).addClass("on");

  $("#all_product").hide();
  $("#active_product").hide();
  $("#retire_product").hide();
  $("#all_productbundle").show();
  $("#publish_productbundle").show();
  $("#unpublish_productbundle").show();

  $("#all_productbundle").css({
    'color': '#2C8BBC'
  })
  $("#publish_productbundle").css({
    'color': '#2C8BBC'
  })
  $("#unpublish_productbundle").css({
    'color': '#2C8BBC'
  })

  if (filter == 'all') {
    $("#all_productbundle").css({
      'color': '#000'
    });
  }
  if (filter == 'publish') {
    $("#publish_productbundle").css({
      'color': '#000'
    });
  }

  if (filter == 'unpublish') {
    $("#unpublish_productbundle").css({
      'color': '#000'
    });
  }

  productBundlefilterby = filter;

  if ($("#instances").find(".instance_selected").length == 0) {
    return;
  }

  var data = {};
  if ($("#whichPlan").val() == "history") {
    if ($("#rpb_history_dates option:selected").val().trim() == "") {
      return;
    }
    data["revisionDate"] = $("#rpb_history_dates option:selected").val().trim();
  }
  data["whichPlan"] = $("#whichPlan").val();
  data["serviceInstanceUUID"] = $("#instances").find(".instance_selected").attr("id");
  data["filterBy"] = filter
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

function changeDiscriminatorOperator(current) {
  var className = $(current).attr("id").split("-")[1];
  $("." + className).val($(current).val());
}

function bindCatagoryDropDown(){
	$("#filter_dropdown").unbind("change").bind("change", function(event) {
		listProductByFilter();
	});	
}

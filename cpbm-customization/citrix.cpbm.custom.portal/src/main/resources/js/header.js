/*
*  Copyright © 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/
$(document).ready(function() {

  $(document).click(function(e) {
    var target = (e && e.target) || (event && event.srcElement);
    var obj = $("#crm_drop_down").get(0);
    if ($.checkParent(target, obj)) {
      $(obj).attr("style", "display:none");
      if (users_popup_flag == true) {
        $("#userstab").attr("class", "mainmenu_button");
        users_popup_flag = false;
      }
      if (accounts_popup_flag == true) {
        $("#crmtab").attr("class", "mainmenu_button");
        accounts_popup_flag = false;
      }
    }
    if ($(target).parents("#productstab").html() == null) {
      $("#menu_dropdwon_for_products").hide();
      $("#productstab").removeClass("dropdownmenu");
    }
  });

  $.checkParent = function(t, obj) {
    while (t.parentNode) {
      if (t == obj) {
        return false;
      }
      t = t.parentNode;
    }
    return true;
  };



  $("#userprofile_button").hover(function() {
    $("#userprofile_dropdownbox").show();
  }, function() {
    $("#userprofile_dropdownbox").hide();
  });
  $("#accountmanagementtab").hover(function() {
    $(this).addClass("hover");
    $("#account_sub_menu").css('visibility', 'visible');

  }, function() {
    $(this).removeClass("hover");
    $("#account_sub_menu").css('visibility', 'hidden');

  });
  $("#admintab").hover(function() {
    $(this).addClass("hover");
    $("#admin_sub_menu").css('visibility', 'visible');

  }, function() {
    $(this).removeClass("hover");
    $("#admin_sub_menu").css('visibility', 'hidden');

  });
  $("#crmtab").hover(function() {
    $(this).addClass("hover");
    $("#crm_sub_menu").css('visibility', 'visible');

  }, function() {
    $(this).removeClass("hover");
    $("#crm_sub_menu").css('visibility', 'hidden');

  });
  $("#supporttab").hover(function() {
    $(this).addClass("hover");
    $("#support_sub_menu").css('visibility', 'visible');

  }, function() {
    $(this).removeClass("hover");
    $("#support_sub_menu").css('visibility', 'hidden');

  });

  $("#top_right_nav_balance_link").unbind("click").bind("click", function(event) {
    window.location = balance_link_path;
  });

  $("#top_right_nav_notifications_more_link").unbind("click").bind("click", function(event) {
    window.location = notifications_link_path;
  });

  $("#top_right_nav_health_link").bind("mouseover", function(event) {

    $(this).find("#top_nav_servicehealth_dropdown").show();
    return false;
  });
  $("#top_right_nav_health_link").bind("mouseout", function(event) {

    $(this).find("#top_nav_servicehealth_dropdown").hide();
    return false;
  });

  $("#top_right_nav_notifications_link").bind("mouseover", function(event) {

    $(this).find("#top_nav_notifications_dropdown").show();
    return false;
  });
  $("#top_right_nav_notifications_link").bind("mouseout", function(event) {

    $(this).find("#top_nav_notifications_dropdown").hide();
    return false;
  });
});
var accounts_popup_flag = false;
var users_popup_flag = false;

function listAccount(current) {
  $.ajax({
    type: "GET",
    url: accountPath,
    dataType: "html",
    data: {
      effectiveTenantParam: effectiveTenantParam
    },
    success: function(html) {
      var crmClass = $(current).attr('class');
      $(current).attr('class', crmClass + ' dropdown');
      $("#crm_drop_down").show();
      $("#crm_drop_down").html(html);
      accounts_popup_flag = true;
    },
    error: function() {
      //need to handle
    }
  });
}

function listUsers(current) {
  $.ajax({
    type: "GET",
    url: userPath,
    dataType: "html",
    success: function(html) {
      var userClass = $(current).attr('class');
      $(current).attr('class', userClass + ' dropdown');
      $("#crm_drop_down").show();
      $("#crm_drop_down").html(html);
      users_popup_flag = true;
    },
    error: function() {
      //need to handle
    }
  });
}

function getCurrentPageAccounts(currentPage, size, charRange, pattern) {

  var url = accountPath + "?currentPage=" + currentPage + "&charRange=" + charRange + "&effectiveTenantParam=" +
    effectiveTenantParam;
  if (size != null && size != "") {
    url = url + "&size=" + size;
  }
  if (pattern != null && pattern != "") {
    url = url + "&pattern=" + pattern;
  }

  $.ajax({
    type: "GET",
    url: url,
    dataType: "html",
    success: function(html) {
      $("#crm_drop_down").html(html);

    },
    error: function() {
      //need to handle
    }
  });

}

function getCurrentPageUsers(currentPage, size, charRange) {

  var url = userPath + "?currentPage=" + currentPage + "&charRange=" + charRange;
  if (size != null && size != "") {
    url = url + "&size=" + size;
  }
  $.ajax({
    type: "GET",
    url: url,
    dataType: "html",
    success: function(html) {
      $("#crm_drop_down").html(html);

    },
    error: function() {
      // need to handle
    }
  });

}

function searchaccounts(even, current, charRange) {
  var url = accountPath + "/searchlist?charRange=" + charRange + "&pattern=" + todb($(current).val());
  $.ajax({
    type: "GET",
    url: url,
    dataType: "html",
    success: function(html) {
      $("#accountslist").html(html);
    },
    error: function() {
      //need to handle
    }
  });
}

function view_service_health(current) {
  var divId = $(current).attr('id');
  if (divId == null) return;
  var cloudService_id = divId.replace("cloudService_", "");

  window.location = health_link_path + "?serviceinstanceuuid=" + cloudService_id;
}

function listProductsTabItems(current) {
  $(current).addClass("dropdownmenu");

  var url = productsPath + "/isCurrentAndHistoryApplicableForRPB";
  $.ajax({
    type: "GET",
    url: url,
    dataType: "json",
    async: false,
    cache: false,
    success: function(currentAndHistoryApplicabilityMap) {
      if (currentAndHistoryApplicabilityMap["isAtleastOneCloudServiceEnabled"] == "false") {
        popUpDialogForAlerts("dialog_info", $("#noServiceEnabledError").val());
        return;
      }
      $("#menu_dropdwon_for_products").find("#plannedDropdown").show();
      $("#menu_dropdwon_for_products").find("#currentDropdown").show();
      $("#menu_dropdwon_for_products").find("#historyDropdown").show();
      if (currentAndHistoryApplicabilityMap["current"] == "false") {
        $("#menu_dropdwon_for_products").find("#currentDropdown").hide();
      }
      if (currentAndHistoryApplicabilityMap["history"] == "false") {
        $("#menu_dropdwon_for_products").find("#historyDropdown").hide();
      }
      $("#menu_dropdwon_for_products").show();
    },
    error: function(html) {
      $("#menu_dropdwon_for_products").find("#plannedDropdown").show();
      $("#menu_dropdwon_for_products").find("#currentDropdown").show();
      $("#menu_dropdwon_for_products").find("#historyDropdown").show();
      $("#menu_dropdwon_for_products").show();
    }
  });
}

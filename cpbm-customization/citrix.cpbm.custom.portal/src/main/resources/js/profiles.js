/*
*  Copyright � 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/
$(document).ready(function() {

  if (selectedTab != "") {
    activateTab(selectedTab);
  } else {
    if (isOpsProfile != "" && isOpsProfile == "true") {
      activateThirdMenuItem("l3_profile_serviceprovider_tab");
      $("#customerProfiles").hide();
      $("#spProfiles").show();
      if (selectedProfile != "") {
        viewGivenProfile(selectedProfile);
      }
      if (updatedProfile_id != "") {
        viewFirstProfile(updatedProfile_id);
      }
    } else if (isOpsProfile != "" && isOpsProfile == "false") {
      activateThirdMenuItem("l3_profile_customer_tab");
      $("#spProfiles").hide();
      $("#customerProfiles").show();
      if (selectedProfile != "") {
        viewGivenProfile(selectedProfile);
      }
      if (updatedProfile_id != "") {
        viewFirstProfile(updatedProfile_id);
      }
    } else {
      activateThirdMenuItem("l3_profile_serviceprovider_tab");
      $("#customerProfiles").hide();
      $("#spProfiles").show();
    }
  }

  $(".opcheck").click(function() {

    //skip disabled
    if ($(this).find("span").hasClass('disabled'))
      return;

    var id = $(this).attr('name').substring(7);
    var rolesO = $("#roles" + id).val();
    var rstr;
    if ($(this).find("span").hasClass('unchecked')) {
      var addedRole = $(this).attr("value");
      $('input#roles' + id).attr('value', rolesO + "," + addedRole);

      $(this).find("span").removeClass('unchecked').addClass('checked');

      return;
    } else {
      var removeRole = $(this).attr("value");
      var idx = rolesO.indexOf(",");

      $(this).find("span").removeClass('checked').addClass('unchecked');

      if (idx >= 0) {
        rstr = rolesO.replace(new RegExp("\\b"+removeRole+"\\b","gi"), "");
        rstr = rstr.replace(new RegExp(",\\s*,","gi"),",");
      } else {
        rstr = rolesO.replace(removeRole, "");
      }
      if (rstr == '') {
        alert('You cannot remove all roles');
        return false;
      } else {
        $('input#roles' + id).attr('value', rstr);
      }
      return;
    }
  });

});

function viewProfile(current) {
  var id = $(current).attr("id");
  window.location = "/portal/portal/profiles/show?profileId=" + id;

  var divId = $(current).attr('id');

  $(".widget_navigationlist").each(function() {
    $(this).removeClass("selected active");
  });

  $(current).addClass("selected active");

  $(".main_listbox").each(function() {
    $(this).hide();
  });

  $("#profile" + id + "div").show();

}

function viewFirstProfile(divId) {

  $(".widget_navigationlist").each(function() {
    $(this).removeClass("selected active");
  });
  $("#" + divId).addClass("selected active");
  $(".main_listbox").each(function() {
    $(this).hide();
  });

  $("#profile" + divId + "div").show();
}

function viewGivenProfile(divId) {
  $(".widget_navigationlist").each(function() {
    $(this).removeClass("selected active");
  });
  $("#" + divId).addClass("selected active");
  $(".main_listbox").each(function() {
    $(this).hide();
  });

  $("#profile" + divId + "div").show();
}

function initialiseProfilesPage(id) {
  window.location = "/portal/portal/profiles/show?selectedTab=" + id;
}

function activateTab(id) {
  if (id == "spProfiles") {
    $("#l3_profile_customer_tab").removeClass("on").addClass("off");
    $("#l3_profile_serviceprovider_tab").removeClass("off").addClass("on");
    $("#customerProfiles").hide();
    $("#spProfiles").show();
    viewFirstProfile(ops_id);
  } else if (id == "customerProfiles") {
    $("#l3_profile_serviceprovider_tab").removeClass("on").addClass("off");
    $("#l3_profile_customer_tab").removeClass("off").addClass("on");
    $("#spProfiles").hide();
    $("#customerProfiles").show();
    viewFirstProfile(noops_id);
  }
}

function actionCancel() {
  if (selectedTab != "") {
    window.location = "/portal/portal/profiles/show?selectedTab=" + selectedTab;
  } else {
    if (isOpsProfile != "" && isOpsProfile == "true") {
      window.location = "/portal/portal/profiles/show?selectedTab=spProfiles";
    } else if (isOpsProfile != "" && isOpsProfile == "false") {
      window.location = "/portal/portal/profiles/show?selectedTab=customerProfiles";
    } else {
      window.location = "/portal/portal/profiles/show?selectedTab=spProfiles";
    }
  }
}

$(function ()  
  { $(".js_profiles_details_popover_link").popover({trigger:'hover'});  
}); 

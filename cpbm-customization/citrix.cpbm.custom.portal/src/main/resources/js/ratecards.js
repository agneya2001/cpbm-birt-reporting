/*
*  Copyright © 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/
$(document).ready(function() {
  $(function() {
    $('#startDate').datepicker({
      duration: '',
      showOn: "button",
      buttonImage: "/portal/images/calendar_icon.png",
      buttonImageOnly: true,
      dateFormat: g_dictionary.friendlyDate,
      showTime: false
    });
  });

  $("#viewPrevRateCards").click(function() {
    $(this).hide();
    $("#hidePrevRateCards").show();
    $("#prevRateCardsDiv").slideDown();
  });
  $("#hidePrevRateCards").click(function() {
    $(this).hide();
    $("#viewPrevRateCards").show();
    $("#prevRateCardsDiv").slideUp();
  });

  $(".viewrateCard").click(function() {
    var id = $(this).attr('id').substr(11);
    var detailsid = "rateCardDetails" + id;
    $(this).hide();
    $("#hideDetails" + id).show();
    $("#" + detailsid).slideDown();
  });

  $(".hiderateCard").click(function() {
    var id = $(this).attr('id').substr(11);
    var detailsid = "rateCardDetails" + id;
    $(this).hide();
    $("#viewDetails" + id).show();
    $("#" + detailsid).slideUp();
  });

  $("#ui-datepicker-div").css("z-index", "1003");

  $("#rateCardForm").validate(validateRateCardParams);


});

/**
 * Plan a Rate Card
 * @return
 */

function planRateCard(event) {
  if (event.preventDefault) {
    event.preventDefault();
  } else {
    event.returnValue = false;
  }
  var url = productBundlesUrl + thisCatalog + "/planratecard";
  var id = $("#bundleId").val();
  $.ajax({
    type: "GET",
    url: url,
    data: {
      bundleId: id
    },
    dataType: "html",
    success: function(html) {
      $("#futureRateCard").html(html);
    },
    error: function() {
      popUpDialogForAlerts("dialog_info", i18n.errors.ratecards.planning_ratecard);
    }
  });
}

/**
 * Submit a Rate Card
 * @return
 */

function submitratecard(event, form) {
  if (event.preventDefault) {
    event.preventDefault();
  } else {
    event.returnValue = false;
  }
  $.ajax({
    type: "POST",
    url: $(form).attr('action'),
    data: $(form).serialize(),
    dataType: "html",
    success: function(html) {
      var content = "";
      content = content + "<div class='main_details_titlebox rateCard' id='rateCardF' style='margin-top:10px'>";
      content = content + "<h2>" + i18n.label.ratecards.future_rate_card + "</h2> " + i18n.label.ratecards.effectiva_date +
        " |";
      content = content + "<a href='javascript:void(0);' onclick='editFutureRateCard(event);'>" + i18n.label.ratecards
        .edit + "</a> |";
      content = content + "<a href='javascript:void(0);' onclick='deleteFutureRateCard(event);'>" + i18n.label.ratecards
        .delete_rate_card + "</a>";
      content = content + "</div>";
      content = content + "<div class='rateCardDetails' id='rateCardDetailsF' style='display:block'>";
      content = content + html;
      content = content + "</div>";
      $("#futureRateCard").html(content);

    },
    error: function() {
      popUpDialogForAlerts("dialog_info", i18n.errors.ratecards.submitting_rate_card);
    }
  });
}

function deleteFutureRateCard(event) {
  if (event.preventDefault) {
    event.preventDefault();
  } else {
    event.returnValue = false;
  }
  var id = $("#bundleId").val();
  var url = productBundlesUrl + "/ratecard/delete";
  $.ajax({
    type: "POST",
    url: url,
    data: {
      bundleId: id
    },
    dataType: "text",
    success: function(html) {
      if (html == "success") {
        window.location = productBundlesUrl + "/" + id + "/manage";
      } else {
        popUpDialogForAlerts("dialog_info", i18n.errors.ratecards.deleting_rate_card);
      }
    },
    error: function() {
      popUpDialogForAlerts("dialog_info", i18n.errors.ratecards.deleting_rate_card);
    }
  });
}

/**
 * Edit a Rate Card
 * @return
 */

function editFutureRateCard(event) {
  if (event.preventDefault) {
    event.preventDefault();
  } else {
    event.returnValue = false;
  }
  var url = productBundlesUrl + thisCatalog + "/editFutureRatecard";
  var id = $("#bundleId").val();
  $.ajax({
    type: "GET",
    url: url,
    data: {
      bundleId: id
    },
    dataType: "html",
    success: function(html) {
      $("#futureRateCard").html(html);
    },
    error: function() {
      popUpDialogForAlerts("dialog_info", i18n.errors.ratecards.editting_rate_card);
    }
  });
}

/**
 * Edit a Rate Card
 * @return
 */

function editCurrentRateCard(event) {
  if (event.preventDefault) {
    event.preventDefault();
  } else {
    event.returnValue = false;
  }
  var url = productBundlesUrl + "editCurrentRatecard";
  var id = $("#bundleId").val();
  $.ajax({
    type: "GET",
    url: url,
    data: {
      bundleId: id,
      rcid: rcid
    },
    dataType: "html",
    success: function(html) {
      $("#currentRateCard").html(html);
    },
    error: function() {
      popUpDialogForAlerts("dialog_info", i18n.errors.ratecards.editting.rate.card);
    }
  });
}

/**
 * Edit a Rate Card
 * @return
 */

function editURC(event) {
  if (event.preventDefault) {
    event.preventDefault();
  } else {
    event.returnValue = false;
  }
  var id = $("#catalogid").val();
  var url = productBundlesUrl + id + "/editurc";
  var rcid = $("#cratecardId").val(); // from bundle reuse
  $.ajax({
    type: "GET",
    url: url,
    data: {
      rcid: rcid
    }, // from bundle reuse		
    dataType: "html",
    success: function(html) {
      $("#currentRateCard").html(html);
    },
    error: function() {
      popUpDialogForAlerts("dialog_info", i18n.errors.ratecards.editting.rate.card);
    }
  });
}

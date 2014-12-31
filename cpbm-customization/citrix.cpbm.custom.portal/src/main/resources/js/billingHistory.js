/*
*  Copyright © 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/
$(document).ready(function() {
  activateThirdMenuItem("l3_billing_invoices_tab");
  if (isDelinquent == true) {
    if (typeof showMakePaymentMessage!="undefined" && showMakePaymentMessage != "") {
      popUpDialogForAlerts("dialog_info", showMakePaymentMessage);
    }
  }

  $(".invoiceDetailsLink").live('click', function(event) {
    var divId = $(this).attr("id");
    var type = $(this).attr("type");
    var Id = divId.substr(9);
    var dialogId = "dialog_invoice_details_" + Id;
    var size = 700;
    if (type == "CloseOut") {
      size = 850;
    }
    initDialog(dialogId, size);
    var $thisDialog = $("#" + dialogId);
    $thisDialog.dialog('option', 'buttons', {
      "Cancel": function() {
        $(this).dialog("close");
      }
    });
    dialogButtonsLocalizer($thisDialog, {
      'Cancel': g_dictionary.dialogClose
    });
    $thisDialog.dialog("open");
  });
});

function details_tab_click(current) {

  $('#viewCharges_tab').removeClass('active').addClass("nonactive");
  $(current).removeClass('nonactive').addClass("active");
  $('#invoice_details_content').hide();

  $('#details_content').show();

};

function showInfoBubble(current) {
  if ($(current).hasClass('active')) return
  $(current).find("#info_bubble").show();
  return false;
};

function hideInfoBubble(current) {
  $(current).find("#info_bubble").hide();
  return false;
};

function nextClick() {
  var $currentPage = $('#current_page').val();
  window.location = "/portal/portal/billing/history?tenant=" + $('#tenantParam').val() + "&page=" + (parseInt(
    $currentPage) + 1);
}

function previousClick() {
  var $currentPage = $('#current_page').val();
  window.location = "/portal/portal/billing/history?tenant=" + $('#tenantParam').val() + "&page=" + (parseInt(
    $currentPage) - 1);
}

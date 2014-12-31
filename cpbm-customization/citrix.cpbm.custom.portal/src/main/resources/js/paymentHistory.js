/*
*  Copyright © 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/
$(document).ready(function() {
  activateThirdMenuItem("l3_billing_payments_tab");

  bindActionMenuContainers();
});



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
  window.location = "/portal/portal/billing/paymenthistory?tenant=" + $('#tenantParam').val() + "&page=" + (parseInt(
    $currentPage) + 1);
}

function previousClick() {
  var $currentPage = $('#current_page').val();
  window.location = "/portal/portal/billing/paymenthistory?tenant=" + $('#tenantParam').val() + "&page=" + (parseInt(
    $currentPage) - 1);
}

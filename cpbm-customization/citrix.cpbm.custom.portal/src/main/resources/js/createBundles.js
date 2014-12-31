/*
*  Copyright © 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/
$(document).ready(function() {

  //	console.log("setting up date for create.");
  $(function() {
    $('#currentRateCard\\.startDate').datepicker({
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
    $('#currentRateCard\\.endDate').datepicker({
      duration: '',
      showOn: "button",
      buttonImage: "/portal/images/calendar_icon.png",
      buttonImageOnly: true,
      dateFormat: g_dictionary.friendlyDate,
      showTime: false,
      minDate: new Date()
    });
  });

  $("#addnewproductBundlecancel").click(function() {
    $("#addnewproductBundleDiv").html("");
  });


  $("#productBundleCreateForm").validate(validateParams);

  $(".tooltip_help").tooltip({
    effect: 'fade',
    position: 'center right',
    offset: [0, 30]
  });



});

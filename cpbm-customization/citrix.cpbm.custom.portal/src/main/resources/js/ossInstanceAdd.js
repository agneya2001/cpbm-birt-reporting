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
    position: "center right",
    offset: [-2, 10],
    effect: "fade",
    opacity: 0.7
  });

  $("#submitbutton").bind("click", function(event) {
    if ($("#submitbutton").hasClass('active')) {
      $('#serviceInstanceForm').submit();
    }
  });

  $("#backtolisting").bind("click", function(event) {
    if ($("#reloadlist").val() == 'false') {
      var id = $(this).attr('service');
      $('.service_detailpanel').hide();
      var category = $("#selectedcategory").val();
      showSelectedCategory(category);
      $(".service_listpanel").show();
      $servicelist_mainbox = $('div.servicelist.mainbox[serviceid=' + id + ']');
      var newpos = $servicelist_mainbox.offset();
      window.scrollTo(newpos.left, newpos.top - 250);
      return false;
    } else {
      window.location = "/portal/portal/connector/oss";
    }
  });
});

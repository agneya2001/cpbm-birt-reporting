/*
*  Copyright © 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/
$(document).ready(function() {
  $('<img src="/portal/images/portal/move-spinner.gif" id="spinner" />').css('position', 'absolute').hide().appendTo(
    'body');

  var form = $(this).children('form');
  $("#homeObjectFilter").change(function() {
    var position = $(this).offset();
    $('#spinner').css({
      top: position.top,
      left: position.left + $(this).width() + 30
    }).fadeIn();

    $.ajax({

      type: "POST",
      url: "/portal/portal/home/activity",
      data: {
        homeObjectFilter: $("#homeObjectFilter").val()
      },
      dataType: "html",
      success: function(html) {
        $("#db_activitybox_mid").html(html);

      }

    });
    $('#spinner').fadeOut();
  });

  $("#usageFilter").change(function() {
    var position = $(this).offset();
    $('#spinner').css({
      top: position.top,
      left: position.left + $(this).width() + 30
    }).fadeIn();

    $.ajax({

      type: "POST",
      url: "/portal/portal/home/homereport",
      data: {
        spendByFilter: $("#usageFilter").val()
      },
      dataType: "html",
      success: function(html) {
        $("#piechartcontent").html(html);

      }

    });
    $('#spinner').fadeOut();
  });


  $("#projectsDiv").dialog({
    autoOpen: true,
    width: 600,
    height: 300,
    modal: true,
    title: 'Access Cloud Console',
    buttons: {
      "Cancel": function() {
        $(this).dialog('close');
        $(window.location).attr('href', '/portal/portal/home');
      }
    }
  });

});

/*
*  Copyright © 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/
$(document).ready(function() {
  $(".tree_links").click(function() {
    var id = $(this).attr("id");
    id = id + "_details";
    $(".details_content").hide();
    $("#" + id).show();
  });

  $(".inner_links").click(function() {
    var id = $(this).attr("id");
    id = id.replace(/inner_/, '') + "_details";
    $(".details_content").hide();
    $("#" + id).show();
  });

});

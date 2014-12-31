/*
*  Copyright © 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/

$(document).ready(function() {
  $("#capacity_carousel_container").show();
  $("#foo2_pag").show();
  $(function() {
    $("#capacity_carousel_container").carouFredSel({
      circular: false,
      infinite: false,
      auto: false,
      prev: {
        button: "#foo2_prev",
        key: "left"
      },
      next: {
        button: "#foo2_next",
        key: "right"
      },
      pagination: "#foo2_pag"
    });
  });

});

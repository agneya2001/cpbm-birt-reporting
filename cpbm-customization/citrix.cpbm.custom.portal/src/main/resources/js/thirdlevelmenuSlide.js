/*
*  Copyright © 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/

$(document).ready(function() {
  //Enable or leave the buttons
  $('.slider').each(function() {

    if ($('li:last', this).width() + $('li:last', this).offset().left - $('li:first', this).offset().left > $(
      '#items_container').width()) {
      // enable the buttons
      $('.thirdlevel_slidingbutton', this).css('display', 'inline');
      $('.thirdlevel_slidingbutton.prev', this).css('visibility', 'hidden');
    }

    var $div = $('#items_container');
    var selected_tab_offset = $('.on', $div).width() + $('.on', $div).offset().left - $('li:first', $div).offset().left -
      $div.width();
    var offset = Math.abs(parseInt($('ul', $div).css('marginLeft')));
    var diff = $div.width();
    var last_tab_offset = $('li:last', $div).width() + $('li:last', $div).offset().left - $('li:first', $div).offset()
      .left - $div.width();

    if (offset >= selected_tab_offset) {
      return;
    } else if (offset + diff >= selected_tab_offset) {
      diff = selected_tab_offset - offset + 30;
      if (selected_tab_offset == last_tab_offset) {
        // Hide the 'next' button
        $('.thirdlevel_slidingbutton.next', this.parentNode).css('visibility', 'hidden');
      }
    }
    $('.thirdlevel_slidingbutton.prev', this.parentNode).css('visibility', 'visible');
    //move the menu
    $("#items_container").find("ul", $(this).parent()).animate({
      marginLeft: '-=' + diff
    }, 400, 'swing');

  });

  //Actions on clicking the Next button
  $(".slider .next").click(function() {
    var $div = $('#items_container'),
      maxoffset = $('li:last', $div).width() + $('li:last', $div).offset().left - $('li:first', $div).offset().left -
        $div.width(),
      offset = Math.abs(parseInt($('ul', $div).css('marginLeft'))),
      diff = $div.width();

    if (offset >= maxoffset) return;
    else if (offset + diff >= maxoffset) {
      diff = maxoffset - offset + 20;
      // Hide this
      $(this).css('visibility', 'hidden');
    }
    $('.thirdlevel_slidingbutton.prev', this.parentNode).css('visibility', 'visible');

    $("#items_container").find("ul", $(this).parent()).animate({
      marginLeft: "-=" + diff
    }, 400, 'swing');
  });

  //Actions on clicking the Previous button
  $(".slider .prev").click(function() {
    var offset = Math.abs(parseInt($('ul', this.parentNode).css('marginLeft')));
    var diff = $('#items_container').width();
    if (offset <= 0) return;
    else if (offset - diff <= 0) {
      $(this).css('visibility', 'hidden');
      diff = offset;
    }
    $('.thirdlevel_slidingbutton.next', this.parentNode).css('visibility', 'visible');

    $("#items_container").find("ul", $(this).parent()).animate({
      marginLeft: '+=' + diff
    }, 400, 'swing');
  });
});

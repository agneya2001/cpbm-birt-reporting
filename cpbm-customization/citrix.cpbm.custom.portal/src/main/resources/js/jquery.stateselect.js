/*
 * Simple State/Province Select plugin for jQuery
 * 
 * Example:
 * $(document).ready(function() {
 *    $('#country').linkToStates('#state');
 *  });
 *
 * Copyright (c) 2008 Adam Daniels
 * Licensed under the MIT License
 *
 */
;(function($) {
  //assumes country_states is defined
  if( typeof country_states !== 'undefined' ) {  
    $.fn.extend({
  		linkToStates: function(state_select_id) {
        $(this).change(function() {
          var country = $(this).attr('value');
          $(state_select_id).removeOption(/./);
          $(state_select_id).addOption(country_states[country], false);
        });
      }
    });
    
    $.fn.extend({
  		defautlinkstates: function(state_select_id) {
        var country = $(this).attr('value');
        $(state_select_id).removeOption(/./);
        $(state_select_id).addOption(country_states[country], false);
      }
    });
  }
})(jQuery);
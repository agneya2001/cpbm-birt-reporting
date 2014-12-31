/*
*  Copyright © 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/
$(document).ready(function() {

  $.validator.addMethod('minlen', function(value, element, param) {
    return this.optional(element) || value.length >= param;
  }, function(value, element) {
    return i18n.errors.connector.fields.minlength + " " +$(element).attr('minlen');
  });
    
  $.validator.addMethod('maxlen', function(value, element, param) {
    return this.optional(element) || value.length <= param;
  }, function(value, element) {
    return i18n.errors.connector.fields.maxlength + " " + $(element).attr('maxlen');
  });
    
  jQuery.validator.addMethod("minval", function(value, element, param) {
    return this.optional(element) || (parseFloat(value) >= parseFloat(param));
  }, function(value, element) {
    return i18n.errors.connector.fields.minvalue + " " + $(element).attr('minval');
  });
  
  jQuery.validator.addMethod("maxval", function(value, element, param) {
    return this.optional(element) || parseInt(value) <= parseInt(param);
  }, function(value, element) {
    return i18n.errors.connector.fields.maxvalue + " " + $(element).attr('maxval');
  });

  jQuery.validator.addMethod("digit", function(value, element) {
    return this.optional(element) || value.length > 0 && /^[-]*[0-9][0-9]*$/.test(value);
  }, i18n.errors.connector.fields.digits);

  jQuery.validator.addMethod("password", function(value, element) {
    return this.optional(element) || value.length < 255;
  }, i18n.errors.connector.fields.password);

  jQuery.validator.addMethod("url", function(value, element) {
    return this.optional(element) || new RegExp(
      '^http(s{0,1})://[a-zA-Z0-9_/\\-\\.]+((\\.([A-Za-z/]{2,5}))|(:([0-9]{2,4})))[a-zA-Z0-9_/\\&\\?\\=\\-\\.\\~\\%]*'
    ).test(value);
  }, i18n.errors.connector.fields.url);

  jQuery.validator.addMethod("required", function(value, element) {
    return value.length > 0;
  }, i18n.errors.connector.fields.required);
   
  jQuery.validator.addMethod("regex", function(value, element, param) {
    return this.optional(element) || value.length > 0 && param.length > 0 && new RegExp(param).test(value);
  }, function(value, element) {
    var title = $(element).attr('title');
    var input_error = $(element).data("input-error");
    if(input_error){
      return input_error;
    } else if(title){
    	return title;
    }
    return i18n.errors.connector.fields.regex + $(element).attr('regex');
  });
});

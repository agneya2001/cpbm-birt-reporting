/*
*  Copyright © 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/
var CODE_NOT_UNIQUE_ERROR_CODE = 601;
var AJAX_FORM_VALIDATION_FAILED_CODE = 420;
var INVALID_AJAX_REQUEST_ERROR_CODE = 420;
var PAGINATION_PAGE_SIZE = 13;
var PAGINATION_CURRENT_PAGE_NUMBER = 1;
var INTERNAL_SERVER_FAILED_CODE = 500;
var PRECONDITION_FAILED = 412;
var ssoResponseMap = null;

$(document).ready(
  function() {
    if ($("#from_cas_login_page").val() == 'true') {
      $.ajax({
        type: "GET",
        url: "/portal/portal/getSupportedLanguages",
        async: false,
        dataType: "json",
        success: function(response) {
          var htmlToPush = '';
          for (var key in response) {
            htmlToPush = htmlToPush + '<li class="language_select_option" id=' + key + '><span class="flagicons ' +
              key + '"></span><span class="languagelist">' + response[key] + '</span></li>';
          }
          $(".catalog_signuppopoverlist").html(htmlToPush);
          $(".language_select_option").bind('click',
            function(event) {
              reload_in_another_language($(this));
            });
        }
      });

      $.ajax({
        type: "GET",
        url: "/portal/portal/isShowAnnonymousCatalog",
        async: false,
        dataType: "json",
        success: function(shouldShow) {
          if (shouldShow == true) {
            $("#language_selector_dropdown").css("left", "162px");
            $("#cas_login_view_anonymous_browsing").show();
          } else {
            $("#language_selector_dropdown").css("left", "30px");
            $("#cas_login_view_anonymous_browsing").hide();
          }
        }
      });

      $.ajax({
        type: "GET",
        url: "/portal/portal/getLoginPageUIRelatedConfigs",
        async: false,
        dataType: "json",
        success: function(configs) {
          if (configs.isDirectoryServiceAuthenticationON == "true") {
            $("#login_maintabsarea_div_id").hide();
            $("#login_main").attr('id', 'login_main_smaller_ver');
            $("#login_contentpanel_div_id").addClass('smaller_ver');
            $("#login_container_div_id").addClass('smaller_ver');
            $("#login_headerarea_div_id").addClass('smaller_ver');
            $("#login_maincontentarea_div_id").addClass('smaller_ver');
            $("#login_formbox_div_id").addClass('smaller_ver');
            $("#login_info_box").hide();
            $("#loginbg_bot_div_id").addClass('smaller_ver');
            $(".login_messages").addClass('smaller_ver');
            $(".login_infobox_top").hide();
            $(".login_infobox_mid").hide();
            $(".login_infobox_bot").hide();
          }
          if (configs.showSuffix == "true") {
            $("#login_maintabsarea_div_id").hide();
            $("#dontHaveAnAccount_p_id").hide();
            $("#dontHaveAnAccount_a_id").hide();
            $("#showSuffixControlVar").val("true");

            $("#suffix_li").show();
          }
          if (configs.showSuffixDropBox == "true") {
            var suffixList = configs.suffixList;
            var htmlToPush = '';
            for (var index in suffixList) {
              if (index == 0) {
                $("#suffix").val(suffixList[index]);
              }
              htmlToPush = htmlToPush + '<option value="' + suffixList[index] + '">' + suffixList[index] +
                '</option>';
            }
            $("#suffixDropdown").html(htmlToPush);
            $("#suffixDropdown").show();
          } else {
            $("#suffix").show();
          }
        }
      });

      $.ajax({
        type: "GET",
        url: "/portal/portal/isSystemActive",
        async: false,
        dataType: "json",
        success: function(shouldShow) {
          if (shouldShow == true) {
            $("#login_info_box").show();
            $("#signup_tab").show();
          } else {
            $("#login_info_box").hide();
            $("#signup_tab").hide();
          }
        }
      });
      $("#login_main").show();
      $("#login_main_smaller_ver").show();
    }

    if ($("#from_login_page").val() == 'true') {
      $.ajax({
        type: "GET",
        url: "/portal/portal/isSystemActive",
        async: false,
        dataType: "json",
        success: function(shouldShow) {
          if (shouldShow == true) {
            $("#login_info_box").show();
            $("#signup_tab").show();
          } else {
            $("#login_info_box").hide();
            $("#signup_tab").hide();
          }
        }
      });
      $("#login_main").show();
      $("#login_main_smaller_ver").show();
    }

    $("#accept_cookies_panel").dialog({
        width: 900,
        modal: true,
        resizable: false,
        autoOpen: true,
        closeOnEscape: false,
        dialogClass: 'no-close'
    });
    
    
    $("#accept_cookies_panel #accept_cookies").live("click", function() {
        var url = "/portal/portal/acceptCookies";
        $("#spinning_wheel_rhs").show();
        var acceptCookiesPost = $.ajax({
          type: "POST",
          url: url,
          dataType: "html"
        });
        acceptCookiesPost.done(function(state) {
          $("#accept_cookies_panel").dialog("close");
        });
        acceptCookiesPost.fail(function(XMLHttprequest) {
          $("#accept_cookies_errormsg").html(XMLHttprequest.responseText);
        });
        acceptCookiesPost.always(function(XMLHttprequest) {
          $("#spinning_wheel_rhs").hide();
        });
      });
    $("#accept_cookies_panel #reject_cookies").live("click", function() {
    	window.location = "/portal/portal/"+$("#userParam").val()+"/loggedout";
    });
  });


$("#language_selector").live('mouseover', function(event) {
  $("#language_selector_dropdown").show();
});

$("#language_selector").live('mouseout', function(event) {
  $("#language_selector_dropdown").hide();
});

$("#browse_catalogs_li").live('click', function(event) {
  var searchq = window.location.search;
  var lang = "";
  if (searchq.indexOf("lang=") != -1) {
    lang = searchq.substring(searchq.indexOf("lang="));
    window.location = "/portal/portal/catalog/browse_catalog?" + lang;
  } else {
    window.location = "/portal/portal/catalog/browse_catalog";
  }
});

$(".language_select_option").live('click', function(event) {
  reload_in_another_language($(this));
});

function reload_in_another_language(obj) {
  if ($("#from_cas_login_page").val() == 'true') {
    $.ajax({
      type: "GET",
      url: "/portal/portal/setLocale?lang=" + obj.attr('id'),
      async: false,
      dataType: "json",
      success: function(response) {}
    });
  }
  var searchq = window.location.search;
  var addr = window.location.href;
  var trimmedsearchq = "";
  var loc = "/portal/";
  if (addr.indexOf("loggedout") != -1) {
    window.location = loc + "?lang=" + obj.attr('id');
  } else {
    if (searchq.indexOf("lang=") == -1) {
      if (searchq == "")
        window.location = window.location + "?lang=" + obj.attr('id');
      else
        window.location = window.location + "&lang=" + obj.attr('id');
    } else {
      trimmedsearchq = searchq.substring(0, searchq.indexOf("lang="));
      if (searchq != "")
        loc = window.location.href.substring(0, window.location.href
          .indexOf(searchq));
      window.location = loc + trimmedsearchq + "lang=" + obj.attr('id');
    }
  }
};

$("#signup_login_li").live('click', function(event) {
  var searchq = window.location.search;
  var lang = "";
  if (searchq.indexOf("lang=") != -1) {
    lang = searchq.substring(searchq.indexOf("lang="));
    window.location = "/portal/?" + lang;
  } else
    window.location = "/portal";
});

if (typeof String.prototype.trim !== 'function') {
  String.prototype.trim = function() {
    return this.replace(/^\s+|\s+$/g, '');
  };
}

jQuery.fn.extend({
  inputAutoTitles: function(options) {
    // stick all the titles
    this.find('input[type=text]').each(function(i) {
      var title = $(this).attr('title');
      if ($(this).val() == '') {
        $(this).val(title).addClass('waiting').blur(function() {
          $(this).removeClass('active');
          if ($(this).val() == '' || $(this).val() == title)
            $(this).addClass('waiting').val(title);
        }).focus(function() {
          $(this).removeClass('waiting').addClass('active');
          if ($(this).val() == title)
            $(this).val('');
        });
        //
      } else {
        $(this).unbind('click');
      }
    });
    // clear the titles on submit
    $(this).submit(function() {
      $(this).find('[type=text]').each(function(i) {
        if ($(this).attr('title') == $(this).val())
          $(this).val('');
      });
    });
  }
});


jQuery.validator.addMethod("password", function(value, element) {
  
  if (element.className == "text j_credit_card_cvv" || element.className == "text j_credit_card_cvv error" || element
    .className == "text j_credit_card_cvv valid" || element.className == "text js_login_page_pwd" || element.className == "text js_login_page_pwd error" ||
    element.className == "text js_login_page_pwd valid") 
    return true;
  return this.optional(element) || value.length == $.trim(value).length && value.length >= 8 && /\d/.test(value) && /[A-Z]/.test(value);
}, "8 characters, one number and one uppercase character required with no leading or trailing spaces.");

jQuery.validator.addMethod("notEqualTo", function(value, element, param) {
  return (value != $(param).val());
}, "equal");

jQuery.validator.addMethod('noSpacesAllowed', function(value, element) {
  return value.length > 0 && /^[a-zA-Z0-9_:\[\]-]+$/.test(value);
}, "Only characters, digits and special symbols : - _ ] [ are allowed.");

$.validator.addMethod("xRemote", function(value, element) {
  var rule = this.settings.rules[element.name].xRemote;
  if (rule.condition && $.isFunction(rule.condition) && !rule.condition.call(this, element))
    return "dependency-mismatch";
  return $.validator.methods.remote.apply(this, arguments);
}, "Code already in use");

jQuery.validator.addMethod("phone", function(value, element) {
  return this.optional(element) || value.length > 0 && /^[\d-]+$/.test(value) && value.replace(/[^\d]/g, "").length <
    13 && value.replace(/[^\d]/g, "").length > 4;
}, "Invalid phone number.");

jQuery.validator.addMethod("countryCode", function(value, element) {
  return this.optional(element) || value.length > 0 && /^\d+$/.test(value) && value.length < 4;
}, "Invalid country code.");

jQuery.validator.addMethod("flname", function(value, element) {
  var myRegexp = /([[,\#@!$%\^&\*;:{}=_`?~(\\)<>|/\d]+)/;
  var match = myRegexp.exec(value);
  return this.optional(element) || value.length > 0 && match == null;
}, "Only characters and special symbols ' - are allowed.");

jQuery.validator.setDefaults({
  submitHandler: function(form) {
    var submit = $(form).find(".submitmsg");
    if ($(submit).attr("rel") != null && $(submit).attr("rel") != "") {
      $(submit).attr("value", $(submit).attr("rel"));
      $(submit).attr("disabled", true);
    }
    if ($(form).hasClass("ajaxform") == false) {
      form.submit();
    }
  }
});

$.ajaxSetup({
  contentType: "application/x-www-form-urlencoded;charset=utf8",
  complete: function(xhr, status) {
    if (xhr.status == 401) {
      window.location.reload(false);
    }
    if (xhr.status == 404) {
      window.location = "/portal/portal/errors/notfound";
    }
    if (xhr.status == 503) {
      //CloudService Exception
      var errorMessage = null;
      var errorObjet = CPBM.parseJsonSafely(xhr.responseText);
      if(errorObjet != null){
        errorMessage = errorObjet.message;
      }else{
        errorMessage = xhr.responseText;
      }
      alert(errorMessage);
      window.location = "/portal/portal/connector/csinstances?tenant=" + effectiveTenantParam;
    }
  }
});
/*
 * "indexOf()" function not supported on IE8(IE < version 9) for Array objects.
 * Extending Array object to support "indexOf()". START
 */
if (!Array.prototype.indexOf) {
  Array.prototype.indexOf = function(elt /* , from */ ) {
    var len = this.length >>> 0;

    var from = Number(arguments[1]) || 0;
    from = (from < 0) ? Math.ceil(from) : Math.floor(from);
    if (from < 0)
      from += len;

    for (; from < len; from++) {
      if (from in this && this[from] === elt)
        return from;
    }
    return -1;
  };
}

/*
 * "indexOf()" function not supported on IE8(IE < version 9) for Array objects.
 * Extending array object to support "indexOf()". END
 */

function formUploadBeforeSubmit(form) {
  /*
   * Call this function for Multipart forms with file uploads just before
   * submit
   */
  var thisForm = $(form);
  var csrfToken = $('input[name$="OWASP_CSRFTOKEN"]', thisForm).val();
  if (csrfToken != null) {
    thisForm.attr('action', thisForm.attr('action') + "?OWASP_CSRFTOKEN=" + csrfToken);
  }
}

$(document).ready(function() {
  $(".welcomeuser_panel").hover(function() {
    $("#welcome_menu").parent().removeClass("welcomebox_sideicon");
    $("#welcome_menu").parent().addClass("welcomebox_dropdown_arrow");
    $("#welcome_menu").show();
  }, function() {
    $("#welcome_menu").parent().addClass("welcomebox_sideicon");
    $("#welcome_menu").parent().removeClass("welcomebox_dropdown_arrow");
    $("#welcome_menu").hide();
  });

  if ($('.grid_content').length > 0) {
    $(".smallrow_odd, .smallrow_even").hover(function() {
      $(this).find('.grid_links_container').show();
    }, function() {
      $(this).find('.grid_links_container').hide();
    });
  }

});

// Don't allow pages to be loaded in a frame.
if (top.location != location) {
  top.location.href = document.location.href;
}

function getCurrentPageData(current, currentPage, size, url) {

  var perPage = $("#perPage").val();
  var url = url + "currentPage=" + currentPage + "&perPage=" + perPage + "&size=" + size;
  $(current).attr("href", url);

  return true;

}

function ReplaceAll(Source, stringToFind, stringToReplace) {
  var tempArr = Source.split(stringToFind);
  var dest = "";
  for (var i = 0; i < tempArr.length; i++) {
    if (dest == "") {
      dest = tempArr[i];
    } else {
      dest = dest.concat(stringToReplace);
      dest = dest.concat(tempArr[i]);
    }
  }
  return dest;
}

// Prevent cross-site-script(XSS) attack.

function sanitizeXSS(val) {
  if (val == null || typeof(val) != "string")
    return val;
  val = val.replace(/</g, "&lt;"); // replace < whose unicode is \u003c
  val = val.replace(/>/g, "&gt;"); // replace > whose unicode is \u003e
  return val;
}

function noNull(val) {
  if (val == null)
    return "";
  else
    return val;
}

function validatePositiveInteger(value) {
  return (Number(value) % 1) == 0 && Number(value) >= 0 && value.length < 10;
}

function validatePositiveJavaInteger(value) {
  return (Number(value) % 1) == 0 && Number(value) >= 0 && Number(value) <= 2147483647;
}

function fromdb(val) {
  return sanitizeXSS(noNull(val));
}

function todb(val) {
  return encodeURIComponent(val);
}

function getVmName(p_vmName, p_vmDisplayname) {
  if (p_vmDisplayname == null)
    return fromdb(p_vmName);
  var vmName = null;

  if (p_vmDisplayname != p_vmName) {
    vmName = fromdb(p_vmDisplayname) + "(" + p_vmName + ")";
  } else {
    vmName = p_vmName;
  }

  return vmName;
}

function setBooleanReadField(value, $field) {
  if (value == "true" || value == true)
    $field.text(g_dictionary.yes).show();
  else if (value == "false" || value == false)
    $field.text(g_dictionary.no).show();
  else
    $field.hide();
}

function setBooleanEditField(value, $field) {
  if (value == true)
    $field.val("true"); // option value, not option displayText, so no
  // localization
  else
  // value == false
    $field.val("false"); // option value, not option displayText, so no
  // localization
}

function convertHz(hz) {
  if (hz == null)
    return "";

  if (hz < 1000) {
    return formatNumber(hz) + " " + g_dictionary.MHZ;
  } else {
    return formatNumber((hz / 1000).toFixed(2)) + g_dictionary.GHZ;
  }
}

// Validation functions

function showError(isValid, field, errMsgField, errMsg) {
  if (isValid) {
    errMsgField.text("").hide();
    field.addClass("text").removeClass("error_text");
  } else {
    errMsgField.text(errMsg).show();
    field.removeClass("text").addClass("error_text");
  }
}

function showError2(isValid, field, errMsgField, errMsg, appendErrMsg) {
  if (isValid) {
    errMsgField.text("").hide();
    field.addClass("text2").removeClass("error_text2");
  } else {
    if (appendErrMsg) // append text
      errMsgField.text(errMsgField.text() + errMsg).show();
    else
    // reset text
      errMsgField.text(errMsg).show();
    field.removeClass("text2").addClass("error_text2");
  }
}

function showErrorInDropdown(isValid, field, errMsgField, errMsg, appendErrMsg) {
  if (isValid) {
    errMsgField.text("").hide();
    field.addClass("select").removeClass("error_select");
  } else {
    if (appendErrMsg) // append text
      errMsgField.text(errMsgField.text() + errMsg).show();
    else
    // reset text
      errMsgField.text(errMsg).show();
    field.removeClass("select").addClass("error_select");
  }
}

function validateDropDownBox(label, field, errMsgField, appendErrMsg) {
  var isValid = true;
  var errMsg = "";
  var value = field.val();
  if (value == null || value.length == 0) {
    errMsg = g_dictionary.required;
    isValid = false;
  }
  showErrorInDropdown(isValid, field, errMsgField, errMsg, appendErrMsg);
  return isValid;
}

function validateInteger(label, field, errMsgField, min, max, isOptional) {
  return validateNumber(label, field, errMsgField, min, max, isOptional,
    "integer");
}

function validateNumber(label, field, errMsgField, min, max, isOptional, type) {
  var isValid = true;
  var errMsg = "";
  var value = field.val();

  if (value != null && value.length != 0) {
    if (isNaN(value)) {
      errMsg = g_dictionary.invalidNumber;
      isValid = false;
    } else {
      if (type == "integer" && (value % 1) != 0) {
        errMsg = g_dictionary.invalidInteger;
        isValid = false;
      }

      if (min != null && value < min) {
        errMsg = g_dictionary.minimum + ": " + min;
        isValid = false;
      }
      if (max != null && value > max) {
        errMsg = g_dictionary.maximum + ": " + max;
        isValid = false;
      }
    }
  } else if (isOptional != true) { // required field
    errMsg = g_dictionary.required;
    isValid = false;
  }
  showError(isValid, field, errMsgField, errMsg);
  return isValid;
}

function validateString(label, field, errMsgField, isOptional, maxLength) {
  var isValid = true;
  var errMsg = "";
  var value = field.val();
  if (isOptional != true && (value == null || value.length == 0)) { // required
    // field
    errMsg = g_dictionary.required;
    isValid = false;
  } else if (value != null && value.length >= maxLength) {
    errMsg = g_dictionary.maximum + ": " + max + " character";
    isValid = false;
  } else if (value != null && value.indexOf('"') != -1) {
    errMsg = g_dictionary.doubleQuotesNotAllowed;
    isValid = false;
  }
  showError(isValid, field, errMsgField, errMsg);
  return isValid;
}

function validateEmail(label, field, errMsgField, isOptional) {
  if (validateString(label, field, errMsgField, isOptional) == false)
    return;
  var isValid = true;
  var errMsg = "";
  var value = field.val();
  if (value != null && value.length > 0) {
    myregexp = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/;
    var isMatch = myregexp.test(value);
    if (!isMatch) {
      errMsg = g_dictionary.example + ": " + "xxxxxxx@hotmail.com";
      isValid = false;
    }
  }
  showError(isValid, field, errMsgField, errMsg);
  return isValid;
}

function validateNetmask(label, field, errMsgField, isOptional) {
  if (validateString(label, field, errMsgField, isOptional) == false)
    return;
  var isValid = true;
  var errMsg = "";
  var value = field.val();
  if (value != null && value.length > 0) {
    myregexp = /^\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}$/;
    var isMatch = myregexp.test(value);
    if (!isMatch) {
      errMsg = g_dictionary.example + ": 255.255.255.0";
      isValid = false;
    }
  }
  showError(isValid, field, errMsgField, errMsg);
  return isValid;
}

function validateGateway(label, field, errMsgField, isOptional) {
  if (validateString(label, field, errMsgField, isOptional) == false)
    return;
  var isValid = true;
  var errMsg = "";
  var value = field.val();
  if (value != null && value.length > 0) {
    myregexp = /^\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}$/;
    var isMatch = myregexp.test(value);
    if (!isMatch) {
      errMsg = g_dictionary.example + ": 192.168.100.1";
      isValid = false;
    }
  }
  showError(isValid, field, errMsgField, errMsg);
  return isValid;
}

function validateIp(label, field, errMsgField, isOptional) {
  if (validateString(label, field, errMsgField, isOptional) == false)
    return;
  var isValid = true;
  var errMsg = "";
  var value = field.val();
  if (value != null && value.length > 0) {
    myregexp = /^\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}$/;
    var isMatch = myregexp.test(value);
    if (!isMatch) {
      errMsg = g_dictionary.example + ": " + "75.52.126.11";
      isValid = false;
    }
  }
  showError(isValid, field, errMsgField, errMsg);
  return isValid;
}

function validateCIDR(label, field, errMsgField, isOptional) {
  if (validateString(label, field, errMsgField, isOptional) == false)
    return;
  var isValid = true;
  var errMsg = "";
  var value = field.val();
  if (value != null && value.length > 0) {
    myregexp = /^\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}\/\d{1,2}$/;
    var isMatch = myregexp.test(value);
    if (!isMatch) {
      errMsg = g_dictionary.example + ": " + "75.52.126.11/24";
      isValid = false;
    }
  }
  showError(isValid, field, errMsgField, errMsg);
  return isValid;
}

function validateCIDRList(label, field, errMsgField, isOptional) {
  if (validateString(label, field, errMsgField, isOptional) == false)
    return;
  var isValid = true;
  var errMsg = "";
  var cidrList = field.val();

  var array1 = cidrList.split(",");
  for (var i = 0; i < array1.length; i++) {
    var value = array1[i];
    if (value != null && value.length > 0) {
      myregexp = /^\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}\/\d{1,2}$/;
      var isMatch = myregexp.test(value);
      if (!isMatch) {
        isValid = false;
      }
    }
  }
  if (isValid == false)
    errMsg = g_dictionary.example + ": 10.1.1.1/24,10.1.1.2/24";

  showError(isValid, field, errMsgField, errMsg);
  return isValid;
}

function validateAlphanumeric(label, field, errMsgField, isOptional,
  allowedSplCharacters) {
  if (validateString(label, field, errMsgField, isOptional) == false)
    return;
  var isValid = true;
  var errMsg = "";
  var value = field.val();
  if (value != null && value.length > 0) {
    if (allowedSplCharacters != undefined && allowedSplCharacters.length > 0) { // Replacing
      // Spl
      // Characters
      // with
      // an
      // Empty
      // String
      // as
      // these
      // spl
      // characters
      // like
      // "-"
      // etc
      // are
      // allowed
      // along
      // with
      // Alphanumeric
      // characters.
      value = value.replace(new RegExp("[" + allowedSplCharacters.join('') + "]", "g"), "");
    }
    if (alphanumericRegexp.test(value) == false) {
      errMsg = dictionary.onlyAlphanumericCharactersAreAllowed;
      isValid = false;
    }
  }
  showError(isValid, field, errMsgField, errMsg);
  return isValid;
}

function validatePath(label, field, errMsgField, isOptional) {
  if (validateString(label, field, errMsgField, isOptional) == false)
    return;
  var isValid = true;
  var errMsg = "";
  var value = field.val();
  if (value != null && value.length > 0) {
    myregexp = /^\//;
    var isMatch = myregexp.test(value);
    if (!isMatch) {
      errMsg = label + g_dictionary.example + ": " + "/aaa/bbb/ccc";
      isValid = false;
    }
  }
  showError(isValid, field, errMsgField, errMsg);
  return isValid;
}

function cleanErrMsg(field, errMsgField) {
  showError(true, field, errMsgField);
}

var keycode_Enter = 13;

var $currentGridRow;

function clickAnotherGridRow($thisGridRow) {
  if ($currentGridRow != null && $currentGridRow != $thisGridRow)
    $currentGridRow.removeClass("selected active");
  if ($thisGridRow.hasClass("selected") == false)
    $thisGridRow.addClass("selected active");
  $currentGridRow = $thisGridRow;

  $("body").stopTime();

  // action links at bottom (begin)
  $("#top_actions").find("#spinning_wheel").hide();
  $("#action_result_panel").find("#msg").text("");
  $("#action_result_panel").removeClass("error").addClass("success").hide();
  cancelActionPanel(currentSelectedActionLink); // collapse expanded action
  // panel
  // $("#cancel_link").click(); //cancel Edit mode
  // action links at bottom (end)
  // action buttons on top (begin)
  var $spinningWheel = $("#top_actions").find("#spinning_wheel");
  $spinningWheel.find("#in_process_text").text("");
  $spinningWheel.hide();
  $("#top_message_panel").find("#msg").text("");
  $("#top_message_panel").removeClass("error").addClass("success").hide();

  // action buttons on top (end)
}

function clickTopActionLink() {
  if ($currentGridRow == null || ($currentGridRow.hasClass("selected") == true && $currentGridRow
    .hasClass("active") == true))
    $currentGridRow.removeClass("selected active");
}

function cancelTopActionLink() {
  if ($currentGridRow != null && $currentGridRow.hasClass("selected") == false && $currentGridRow.hasClass("active") ==
    false)
    $currentGridRow.addClass("selected active");
}

// action links at bottom (begin)

function initActionLinks(actionMap, refreshGridRowFn, refreshDetailsPanelFn) {
  for (var i = 0; i < actionMap.length; i++) {
    bindToActionLink(actionMap[i], refreshGridRowFn, refreshDetailsPanelFn);
  }
}

var currentSelectedActionLink = null;

function clickActionLink(elementIdPrefix, actionLinkOnClickFn) {
  if (actionLinkOnClickFn != null)
    actionLinkOnClickFn();
  cancelActionPanel(currentSelectedActionLink);
  currentSelectedActionLink = elementIdPrefix;
  $("#" + elementIdPrefix + "_link").hide();
  $("#" + elementIdPrefix + "_text").show();
  $("#action_result_panel").hide();
  $("#top_message_panel").hide();
  $("#" + elementIdPrefix + "_panel").show();
}

function cancelActionPanel(elementIdPrefix, actionLinkOnCancelFn) {
  if (elementIdPrefix == null)
    return;
  if (actionLinkOnCancelFn != null)
    actionLinkOnCancelFn();
  $("#" + elementIdPrefix + "_text").hide();
  $("." + elementIdPrefix + "_link").show();
  $("#" + elementIdPrefix + "_panel").hide();
  if (currentSelectedActionLink == elementIdPrefix)
    currentSelectedActionLink = null;
}

function bindToActionLink(mapObj, refreshGridRowFn, refreshDetailsPanelFn) {
  $("." + mapObj.elementIdPrefix + "_link").unbind("click").bind(
    "click",
    function(event) {
      clickActionLink(mapObj.elementIdPrefix,
        mapObj.actionLinkOnClickFn);
      return false;
    });

  $("#" + mapObj.elementIdPrefix + "_panel").find("#cancel_button").unbind(
    "click").bind(
    "click",
    function(event) {
      $("#" + mapObj.elementIdPrefix + "_panel").find(
        ".agree input[type=checkbox]#accept_checkbox")
        .removeAttr("checked");
      cancelActionPanel(mapObj.elementIdPrefix,
        mapObj.actionLinkOnCancelFn);
      return false;
    });

  $("#" + mapObj.elementIdPrefix + "_panel")
    .find("#confirm_button")
    .unbind("click")
    .bind(
      "click",
      function(event) {
        if ($currentGridRow.data("jsonObj") == null)
          return false;

        var actionUrl1 = mapObj.getActionUrlFn();
        if (actionUrl1 == null)
          return false;

        $("#" + mapObj.elementIdPrefix + "_text").hide();
        $("." + mapObj.elementIdPrefix + "_link").show();
        $("#" + mapObj.elementIdPrefix + "_panel").hide();
        $("#action_link_container").find("#spinning_wheel")
          .show();

        if (mapObj.isAsync == true) {
          $
            .ajax({
              type: "GET",
              url: actionUrl1,
              async: false,
              dataType: "json",
              success: function(obj) {
                var property;
                for (property in obj) {};
                var jobId = obj[property].jobid;
                if (jobId == null)
                  return;
                var timerKey = "vmAction" + jobId;

                // Process the async job
                $("body")
                  .everyTime(
                    5000,
                    timerKey,
                    function() {
                      var actionUrl2 = cloudStackURL("command=queryAsyncJobResult&jobId=" + jobId);
                      $
                        .ajax({
                          type: "GET",
                          url: actionUrl2,
                          dataType: "json",
                          success: function(
                            json) {
                            var result = json.queryasyncjobresultresponse;
                            if (result.jobstatus == 0) {
                              return; // Job
                              // has
                              // not
                              // completed
                            } else {
                              $(
                                "body")
                                .stopTime(
                                  timerKey);
                              $(
                                "#action_link_container")
                                .find(
                                  "#spinning_wheel")
                                .hide();
                              if (result.jobstatus == 1) {
                                // Succeeded
                                var property;
                                for (property in result.jobresult) {}; // e.g.
                                // property
                                // ==
                                // "virtualmachine",
                                // "volume",
                                // "success"
                                var item4 = result.jobresult[property]; // item4
                                // might
                                // be
                                // an
                                // object
                                // or
                                // true/"true"
                                // (if
                                // property
                                // is
                                // "success")
                                if (typeof(item4) != "object") {
                                  if ((property == "success") && ((typeof(item4) == "string" && item4 == "true") ||
                                    (typeof(item4) == "boolean" && item4 == true))) {
                                    $currentGridRow
                                      .click();

                                    var msg;
                                    if (mapObj.afterActionSeccessFn != null) {
                                      msg = mapObj
                                        .afterActionSeccessFn(item4); // item4
                                      // might
                                      // be
                                      // an
                                      // object
                                      // or
                                      // true/"true".
                                      // e.g.
                                      // {
                                      // "deletevolumeresponse"
                                      // : {
                                      // "success"
                                      // :
                                      // "true"}
                                      // }
                                    }
                                    if (msg == null) {
                                      var actionlabel = $(
                                        "." + mapObj.elementIdPrefix + "_link")
                                        .find(
                                          "a")
                                        .first()
                                        .text();
                                      msg = actionlabel + " " + g_dictionary.succeeded;
                                    }

                                    $(
                                      "#action_result_panel")
                                      .find(
                                        "#msg")
                                      .html(
                                        msg);
                                    $(
                                      "#action_result_panel")
                                      .find(
                                        "#status_icon")
                                      .removeClass(
                                        "erroricon")
                                      .addClass(
                                        "successicon");
                                    $(
                                      "#action_result_panel")
                                      .removeClass(
                                        "error")
                                      .addClass(
                                        "success")
                                      .show();
                                  }
                                  return;
                                }

                                if ($currentGridRow
                                  .data("jsonObj") == null)
                                  return;

                                var idPropertyName = null;
                                if (mapObj.returnedObjectId != null) {
                                  if (mapObj.returnedObjectId != "NoReturnedObjectId")
                                    idPropertyName = mapObj.returnedObjectId;
                                  else
                                    idPropertyName = null;
                                } else {
                                  idPropertyName = "id";
                                }

                                if ((idPropertyName == null) || (item4[idPropertyName] == $currentGridRow
                                  .data("jsonObj").id)) {
                                  var msg;
                                  if (mapObj.afterActionSeccessFn != null) {
                                    msg = mapObj
                                      .afterActionSeccessFn(item4); // item4
                                    // might
                                    // be
                                    // an
                                    // object
                                    // or
                                    // true/"true".
                                    // e.g.
                                    // {
                                    // "deletevolumeresponse"
                                    // : {
                                    // "success"
                                    // :
                                    // "true"}
                                    // }
                                  }
                                  if (msg == null) {
                                    var actionlabel = $(
                                      "." + mapObj.elementIdPrefix + "_link")
                                      .find(
                                        "a")
                                      .first()
                                      .text();
                                    msg = actionlabel + " " + g_dictionary.succeeded;
                                  }

                                  $(
                                    "#action_result_panel")
                                    .find(
                                      "#msg")
                                    .html(
                                      msg);
                                  $(
                                    "#action_result_panel")
                                    .find(
                                      "#status_icon")
                                    .removeClass(
                                      "erroricon")
                                    .addClass(
                                      "successicon");
                                  $(
                                    "#action_result_panel")
                                    .removeClass(
                                      "error")
                                    .addClass(
                                      "success")
                                    .show();

                                  refreshGridRowFn(
                                    item4,
                                    $currentGridRow);

                                  // $currentGridRow.click();
                                  refreshDetailsPanelFn($currentGridRow);
                                }
                              } else if (result.jobstatus == 2) {
                                // Failed
                                var actionlabel = $(
                                  "." + mapObj.elementIdPrefix + "_link")
                                  .find(
                                    "a")
                                  .first()
                                  .text();
                                var msg = actionlabel + " " + g_dictionary.actionFailed;
                                if (result.jobresult.errortext != null && result.jobresult.errortext.length > 0)
                                  msg += (" - " + localizeCloudStackMsg(fromdb(result.jobresult.errortext)));
                                $(
                                  "#action_result_panel")
                                  .find(
                                    "#msg")
                                  .text(
                                    msg);
                                $(
                                  "#action_result_panel")
                                  .find(
                                    "#status_icon")
                                  .removeClass(
                                    "successicon")
                                  .addClass(
                                    "erroricon");
                                $(
                                  "#action_result_panel")
                                  .removeClass(
                                    "success")
                                  .addClass(
                                    "error")
                                  .show();
                              }
                            }
                          },
                          error: function(
                            response) {
                            $(
                              "body")
                              .stopTime(
                                timerKey);
                            $(
                              "#action_link_container")
                              .find(
                                "#spinning_wheel")
                              .hide();
                            $(
                              "#action_result_panel")
                              .find(
                                "#msg")
                              .text(
                                g_dictionary.errorFromAPICall + ": " + actionUrl2);
                            $(
                              "#action_result_panel")
                              .find(
                                "#status_icon")
                              .removeClass(
                                "successicon")
                              .addClass(
                                "erroricon");
                            $(
                              "#action_result_panel")
                              .removeClass(
                                "success")
                              .addClass(
                                "error")
                              .show();
                          }
                        });
                    }, 0);
              },
              error: function(XMLHttpResponse) {
                $("#action_link_container").find(
                  "#spinning_wheel").hide();

                var errorMsg = "";
                if (XMLHttpResponse.responseText != null & XMLHttpResponse.responseText.length > 0)
                  errorMsg = parseXMLHttpResponse(XMLHttpResponse);

                var actionlabel = $(
                  "." + mapObj.elementIdPrefix + "_link")
                  .find("a").first().text();
                var msg = actionlabel + " " + g_dictionary.actionFailed;
                if (errorMsg.length > 0)
                  msg += (" " + localizeCloudStackMsg(errorMsg));
                else
                  msg += (" " + g_dictionary.errorFromAPICall + ": " + actionUrl1);
                $("#action_result_panel").find(
                  "#msg").text(msg);
                $("#action_result_panel").find(
                  "#status_icon")
                  .removeClass("successicon")
                  .addClass("erroricon");
                $("#action_result_panel")
                  .removeClass("success")
                  .addClass("error").show();
              }
            });
        } else { // sync
          $
            .ajax({
              type: "GET",
              url: actionUrl1,
              async: false,
              dataType: "json",
              success: function(obj1) {
                if (typeof(obj1) != "object")
                  return;
                var propertyInObj1;
                for (propertyInObj1 in obj1) {}; // e.g. propertyInObj1 ==
                // "updatevirtualmachineresponse"
                var obj2 = obj1[propertyInObj1];
                if (typeof(obj2) != "object")
                  return;
                var propertyInObj2;
                for (propertyInObj2 in obj2) {}; // e.g. propertyInObj2 ==
                // "virtualmachine"
                var item3 = obj2[propertyInObj2]; // item3
                // might
                // be
                // an
                // object or a string
                // "success". e.g. {
                // "deletevolumeresponse"
                // : { "success" :
                // "true"} }
                $("#action_link_container").find(
                  "#spinning_wheel").hide();

                var msg;
                if (mapObj.afterActionSeccessFn != null) {
                  msg = mapObj
                    .afterActionSeccessFn(item3); // item3
                  // might
                  // be an
                  // object or a
                  // string
                  // "success".
                  // e.g. {
                  // "deletevolumeresponse"
                  // : {
                  // "success" :
                  // "true"} }
                }
                if (msg == null) {
                  var actionlabel = $(
                    "." + mapObj.elementIdPrefix + "_link")
                    .find("a").first()
                    .text();
                  msg = actionlabel + " " + g_dictionary.succeeded;
                }

                $("#action_result_panel").find(
                  "#msg").text(msg);
                $("#action_result_panel").find(
                  "#status_icon")
                  .removeClass("successicon")
                  .addClass("erroricon");
                $("#action_result_panel")
                  .removeClass("error")
                  .addClass("success").show();

                if (typeof(item3) == "object") {
                  refreshGridRowFn(item3,
                    $currentGridRow);
                  // $currentGridRow.click();
                  refreshDetailsPanelFn($currentGridRow);
                }

                if ((propertyInObj2 == "success") && ((typeof(item3) == "string" && item3 == "true") || (typeof(item3) ==
                  "boolean" && item3 == true))) {
                  $currentGridRow.click(); // this
                  // will
                  // hide
                  // action_result_panel
                  $("#action_result_panel").find(
                    "#msg").text(msg);
                  $("#action_result_panel").find(
                    "#status_icon")
                    .removeClass(
                      "successicon")
                    .addClass("erroricon");
                  $("#action_result_panel")
                    .removeClass("error")
                    .addClass("success")
                    .show(); // so, show
                  // action_result_panel
                  // (again)
                }
              },
              error: function(obj1) {
                $("#action_link_container").find(
                  "#spinning_wheel").hide();

                var actionlabel = $(
                  "." + mapObj.elementIdPrefix + "_link")
                  .find("a").first().text();
                var msg = actionlabel + " " + g_dictionary.actionFailed;
                $("#action_result_panel").find(
                  "#msg").text(msg);
                $("#action_result_panel").find(
                  "#status_icon")
                  .removeClass("successicon")
                  .addClass("erroricon");
                $("#action_result_panel")
                  .removeClass("success")
                  .addClass("error").show();
              }
            });
        }
        return false;
      });
}
// action links at bottom (end)

// top buttons (begin)

function doActionButton(actionMapItem, apiCommand) {
  var label = actionMapItem.label;
  var inProcessText = actionMapItem.inProcessText;

  var protocol_type = actionMapItem.type;
  if (protocol_type == null)
    protocol_type = "GET";

  var data_type = actionMapItem.dataType;
  if (data_type == null)
    data_type = "json";

  var isAsyncJob = actionMapItem.isAsyncJob;
  var asyncJobResponse = actionMapItem.asyncJobResponse;
  var afterActionSeccessFn = actionMapItem.afterActionSeccessFn;

  var $spinningWheel = $("#top_actions").find("#spinning_wheel");
  $spinningWheel.find("#in_process_text").html(inProcessText);
  $spinningWheel.show();

  $("#top_message_panel").find("#msg").html("");
  $("#top_message_panel").hide();

  $("#action_result_panel").find("#msg").html("");
  $("#action_result_panel").hide();

  // Async job (begin) *****
  if (isAsyncJob == true) {
    $
      .ajax({
        cache: false,
        type: protocol_type,
        url: apiCommand,
        dataType: data_type,
        success: function(json) {
          var jobId = json[asyncJobResponse].jobid;
          var timerKey = "asyncJob_" + jobId;

          $("body")
            .everyTime(
              10000,
              timerKey,
              function() {
                $
                  .ajax({
                    cache: false,
                    url: cloudStackURL("command=queryAsyncJobResult&jobId=" + jobId),
                    dataType: "json",
                    success: function(json) {
                      var result = json.queryasyncjobresultresponse;
                      if (result.jobstatus == 0) {
                        return; // Job
                        // has
                        // not
                        // completed
                      } else {
                        $("body")
                          .stopTime(
                            timerKey);
                        $spinningWheel
                          .hide();

                        if (result.jobstatus == 1) { // Succeeded

                          var msg;
                          if (actionMapItem.afterActionSeccessFn != null)
                            msg = actionMapItem
                              .afterActionSeccessFn(json);
                          if (msg == null)
                            msg = label + ": " + g_dictionary.succeeded;

                          if (actionMapItem.showResultMessage != false) {
                            $(
                              "#top_message_panel")
                              .find(
                                "#msg")
                              .html(
                                msg);
                            $(
                              "#top_message_panel")
                              .find(
                                "#status_icon")
                              .removeClass(
                                "erroricon")
                              .addClass(
                                "successicon");
                            $(
                              "#top_message_panel")
                              .removeClass(
                                "error")
                              .addClass(
                                "success")
                              .show();
                          }

                        } else if (result.jobstatus == 2) { // Failed
                          $(
                            "#top_message_panel")
                            .find(
                              "#msg")
                            .html(
                              label + ": " + g_dictionary.actionFailed + " - " + localizeCloudStackMsg(fromdb(result
                                .jobresult.errortext)));
                          $(
                            "#top_message_panel")
                            .find(
                              "#status_icon")
                            .removeClass(
                              "successicon")
                            .addClass(
                              "erroricon");
                          $(
                            "#top_message_panel")
                            .removeClass(
                              "success")
                            .addClass(
                              "error")
                            .show();
                        }
                      }
                    },
                    error: function(
                      XMLHttpResponse) {
                      $("body").stopTime(
                        timerKey);
                      $spinningWheel
                        .hide();
                      $(
                        "#top_message_panel")
                        .find(
                          "#msg")
                        .html(
                          label + ": " + g_dictionary.actionFailed);
                      $(
                        "#top_message_panel")
                        .find(
                          "#status_icon")
                        .removeClass(
                          "successicon")
                        .addClass(
                          "erroricon");
                      $(
                        "#top_message_panel")
                        .removeClass(
                          "success")
                        .addClass(
                          "error")
                        .show();
                      if (actionMapItem.afterActionFailureFn != null) {
                        actionMapItem
                          .afterActionFailureFn(label + ": " + g_dictionary.actionFailed);
                      }
                    }
                  });
              }, 0);
        },
        error: function(XMLHttpResponse) {
          $spinningWheel.hide();
          $("#top_message_panel").find("#msg").html(
            label + " " + g_dictionary.actionFailed);
          $("#top_message_panel").find("#status_icon")
            .removeClass("successicon").addClass(
              "erroricon");
          $("#top_message_panel").removeClass("success")
            .addClass("error").show();
          if (actionMapItem.afterActionFailureFn != null) {
            actionMapItem.afterActionFailureFn(label + ": " + g_dictionary.actionFailed);
          }
        },
        complete: actionMapItem.afterActionCompleteFn
      });
  }
  // Async job (end) *****
  // Sync job (begin) *****
  else {
    $.ajax({
      type: protocol_type,
      cache: false,
      url: apiCommand,
      dataType: data_type,
      async: false,
      success: function(json) {
        var msg;
        if (actionMapItem.afterActionSeccessFn != null)
          msg = actionMapItem.afterActionSeccessFn(json);
        if (msg == null)
          msg = label + ": " + g_dictionary.succeeded;
        $spinningWheel.hide();
        if (actionMapItem.showResultMessage != false) {
          $("#top_message_panel").find("#msg").html(msg);
          $("#top_message_panel").find("#status_icon").removeClass(
            "erroricon").addClass("successicon");
          $("#top_message_panel").removeClass("error").addClass(
            "success").show();
        }
      },
      error: function(XMLHttpResponse, status) {
        $spinningWheel.hide();
        $("#top_message_panel").find("#status_icon").removeClass("successicon").addClass("erroricon");
        $("#top_message_panel").removeClass("success").addClass("error").show();
        var msg = label + ": " + g_dictionary.actionFailed;
        if (actionMapItem.afterActionFailureFn != null) {
          var exceptionMsg = actionMapItem.afterActionFailureFn(XMLHttpResponse, status);
          if(isNotBlank(exceptionMsg)) {
            msg += " " + exceptionMsg;
          }
        }
        $("#top_message_panel").find("#msg").html(msg);
      },
      complete: actionMapItem.afterActionCompleteFn
    });
  }
  // Sync job (end) *****
}

function bindActionMenuContainers() {

  $(".action_menu_container").each(
    function(i, ele) {
      var $ele = $(ele);

      $ele.bind("mouseover", function(event) {
        $(this).find("#action_menu").show();
        return false;
      });
      $ele.bind("mouseout", function(event) {
        var $thisElement = $(this)[0];
        var relatedTarget1 = event.relatedTarget;
        while (relatedTarget1 != null && relatedTarget1.nodeName != "BODY" && relatedTarget1 != $thisElement) {
          relatedTarget1 = relatedTarget1.parentNode;
        }
        if (relatedTarget1 == $thisElement) {
          return;
        }

        $(this).find("#action_menu").hide();
        return false;
      });
    });
}

// top buttons (end)

// XMLHttpResponse.status
var ERROR_ACCESS_DENIED_DUE_TO_UNAUTHORIZED = 401;
var ERROR_INTERNET_NAME_NOT_RESOLVED = 12007;
var ERROR_INTERNET_CANNOT_CONNECT = 12029;
var ERROR_VMOPS_ACCOUNT_ERROR = 531;

function handleError(XMLHttpResponse, handleErrorCallback) {
  // User Not authenticated
  if (XMLHttpResponse.status == ERROR_ACCESS_DENIED_DUE_TO_UNAUTHORIZED) {
    popUpDialogForAlerts("dialog_info", "Your session has expired.");
    // $("#dialog_session_expired").dialog("open");
  } else if (XMLHttpResponse.status == ERROR_INTERNET_NAME_NOT_RESOLVED) {
    popUpDialogForAlerts("dialog_info", "Your internet name cannot be resolved.");
    // $("#dialog_error_internet_not_resolved").dialog("open");
  } else if (XMLHttpResponse.status == ERROR_INTERNET_CANNOT_CONNECT) {
    popUpDialogForAlerts("dialog_info", "The Management Server is unaccessible.  Please try again later.");
    // $("#dialog_error_management_server_not_accessible").dialog("open");
  } else if (XMLHttpResponse.status == ERROR_VMOPS_ACCOUNT_ERROR && handleErrorCallback != undefined) {
    handleErrorCallback();
  } else if (handleErrorCallback != undefined) {
    handleErrorCallback();
  } else {
    var errorMsg = localizeCloudStackMsg(fromdb(parseXMLHttpResponse(XMLHttpResponse)));
    if (errorMsg != "") {
      popUpDialogForAlerts("dialog_info", errorMsg);
    }

  }
}

function parseXMLHttpResponse(XMLHttpResponse) {
  if (isBrowserIE7() == false) {
    if (isValidJsonString(XMLHttpResponse.responseText) == false) {
      return "";
    }
  }
  if (isBrowserIE7() == true) { // Workaround for IE7 as JSON is not
    // available
    // in IE7.
    try {
      var json = jQuery.parseJSON(XMLHttpResponse.responseText);
    } catch (e) {
      return "";
    }
  } else {
    var json = JSON.parse(XMLHttpResponse.responseText);
  }
  if (json != null) {
    var property;
    for (property in json) {}
    var errorObj = json[property];
    return fromdb(errorObj.errortext);
  } else {
    return "";
  }
}

function isValidJsonString(str) {
  try {
    JSON.parse(str);
  } catch (e) {
    return false;
  }
  return true;
}

var $readonlyFields, $editFields;

function cancelEditMode($tab) {
  if ($editFields != null)
    $editFields.hide();
  if ($readonlyFields != null)
    $readonlyFields.show();
  $tab.find("#save_button, #cancel_button").hide();
}

function switchBetweenDifferentTabs(tabArray, tabContentArray,
  afterSwitchFnArray) {
  for (var tabIndex = 0; tabIndex < tabArray.length; tabIndex++) {
    switchToTab(tabIndex, tabArray, tabContentArray, afterSwitchFnArray);
  }
}

function switchToTab(tabIndex, tabArray, tabContentArray, afterSwitchFnArray) {
  tabArray[tabIndex].bind("click", function(event) {
    tabArray[tabIndex].removeClass("off").addClass("on"); // current tab
    // turns
    // on
    for (var k = 0; k < tabArray.length; k++) {
      if (k != tabIndex)
        tabArray[k].removeClass("on").addClass("off"); // other tabs
      // turns off
    }

    tabContentArray[tabIndex].show(); // current tab content shows
    for (var k = 0; k < tabContentArray.length; k++) {
      if (k != tabIndex)
        tabContentArray[k].hide(); // other tab content hide
    }
    // if(tabIndex != 0) //when switching to a tab that is not details tab
    // cancelEditMode(tabContentArray[0]); //cancel edit mode in details tab
    if (afterSwitchFnArray != null) {
      if (afterSwitchFnArray[tabIndex] != null)
        afterSwitchFnArray[tabIndex]();
    }
    return false;
  });
}

function switchBetweenDifferentTabsTwo(tabArray, tabContentArray,
  afterSwitchFnArray) {
  for (var tabIndex = 0; tabIndex < tabArray.length; tabIndex++) {
    switchToTabTwo(tabIndex, tabArray, tabContentArray, afterSwitchFnArray);
  }
}

function switchToTabTwo(tabIndex, tabArray, tabContentArray, afterSwitchFnArray) {
  tabArray[tabIndex].bind("click", function(event) {
    tabArray[tabIndex].removeClass("nonactive").addClass("active"); // current
    // tab turns
    // on
    for (var k = 0; k < tabArray.length; k++) {
      if (k != tabIndex)
        tabArray[k].removeClass("active").addClass("nonactive"); // other
      // tabs
      // turns off
    }

    tabContentArray[tabIndex].show(); // current tab content shows
    for (var k = 0; k < tabContentArray.length; k++) {
      if (k != tabIndex)
        tabContentArray[k].hide(); // other tab content hide
    }
    // if(tabIndex != 0) //when switching to a tab that is not details tab
    // cancelEditMode(tabContentArray[0]); //cancel edit mode in details tab
    if (afterSwitchFnArray != null) {
      if (afterSwitchFnArray[tabIndex] != null)
        afterSwitchFnArray[tabIndex]();
    }
    return false;
  });
}

// regular expression
var alphanumericRegexp = /^[a-zA-Z0-9_]*$/;

// dialogs

function initDialog(elementId, width1, addToActive, is_resizable) {
  var resizable = false;
  if(is_resizable != null && is_resizable == "true"){
    resizable = true;
  }
  if (width1 == null) {
    activateDialog($("#" + elementId).dialog({
      autoOpen: false,
      modal: true,
      resizable: resizable,
      zIndex: 2000,
      open: function(event) {
        $(".action_menu_container").find("#action_menu").hide();
      } // to hide the Action-menu drop-down on dialog open
    }), addToActive);
  } else {
    activateDialog($("#" + elementId).dialog({
      width: width1,
      autoOpen: false,
      modal: true,
      resizable: resizable,
      zIndex: 2000,
      open: function(event) {
        $(".action_menu_container").find("#action_menu").hide();
      } // to hide the Action-menu drop-down on dialog open
    }), addToActive);
  }
}

function initDialogWithOK(elementId, width1, addToActive) {
  var dialog;
  if (width1 == null) {
    dialog = $("#" + elementId).dialog({
      autoOpen: false,
      modal: true,
      zIndex: 2000,
      resizable: false,
      buttons: {
        "OK": function() {
          $(this).dialog("close");
        }
      }
    });
  } else {
    dialog = $("#" + elementId).dialog({
      width: width1,
      autoOpen: false,
      modal: true,
      zIndex: 2000,
      resizable: false,
      buttons: {
        "OK": function() {
          $(this).dialog("close");
        }
      }
    });
  }
  activateDialog(dialog, addToActive);

  return dialog;
}

function setTemplateStateInRightPanel(stateValue, $stateField) {
  $stateField.text(stateValue);

  if (stateValue == "Ready")
    $stateField.text(stateValue);
  else if (stateValue != null && stateValue.indexOf("%") != -1)
    $stateField.text(stateValue);
  else
    $stateField.text(stateValue);
}

// Adds a Dialog to the list of active Dialogs so that when you shift from one
// tab to another, we clean out the dialogs
var activeDialogs = new Array();

function activateDialog(dialog, addToActive) {
  if (addToActive == undefined || addToActive) {
    activeDialogs[activeDialogs.length] = dialog;
  }

  // bind Enter-Key-pressing event handler to the dialog
  dialog.keypress(function(event) {
    if (event.keyCode == keycode_Enter) {

      var activeElementTag = document.activeElement.tagName;
      if (activeElementTag == "TEXTAREA") {
        return true;
      }

      $buttons = $('[aria-labelledby$=' + dialog.attr("id") + ']').find(
        ":button");

      var activeElementFound = false;
      $buttons.each(function(index, element) {
        if (element == document.activeElement) {
          activeElementFound = true;
          element.click();
          return false;
        }
      });

      if (!activeElementFound) {
        var primaryButton = $('[aria-labelledby$=' + dialog.attr("id") + ']').find(
          ":button[data-primary]:visible:first");
        if (primaryButton.length === 0) {
          primaryButton = $('[aria-labelledby$=' + dialog.attr("id") + ']').find(
            ":button:first");
        }
        primaryButton.click();
      }
      return false; // event.preventDefault() + event.stopPropagation()
    }
  });
}

function removeDialogs() {
  for (var i = 0; i < activeDialogs.length; i++) {
    activeDialogs[i].remove();
  }
  activeDialogs = new Array();
}

function roundNumber(number, decimal) {
  var floatValue = parseFloat(number);
  // if number value is 12.6 and decimal is 0 we still need to show value
  // as 12.60 not as 13
  if ((floatValue + '').indexOf('.') != -1 && decimal == '0') {
    decimal = 2;
    var array1 = (floatValue + '').split('.');
    var decimalValue = array1[1];
    if (decimalValue.length == 1) {
      decimal = 1;
    }
  }
  return (Math.round(floatValue * Math.pow(10, decimal)) / Math.pow(10,
    decimal)).toFixed(decimal);
}

function stripTrailingZeros(number, minFractionalPart) {
  var strNumber = number.toString();
  var pointPos = strNumber.indexOf('.');
  var lenNumber = strNumber.length;

  if (pointPos == -1 || (lenNumber - pointPos) < (minFractionalPart + 2))
    return strNumber;

  return strNumber.slice(0, pointPos + minFractionalPart + 1) +
    strNumber.slice(pointPos + minFractionalPart + 1).replace(/0*$/, '');
}

function formatNumber(number) {
  return number.replace(".", g_dictionary.decPoint);
}

function displayAjaxFormError(XMLHttpRequest, formId, fieldErrorClass) {
  var fieldErrorList = [];
  var json = $.parseJSON(XMLHttpRequest.responseText);
  $("#" + formId + " ." + fieldErrorClass + " label").html("");
  for (var fieldError in json.Error) {
    fieldErrorList.push(fieldError);
    var html = "<label class=\"error\" for=\"" + fieldError + "\" generated=\"true\"></label>";
    var errorDiv = $("div[id='" + fieldError + "Error" + "']");
    errorDiv.html(html);
    errorDiv.find('label.error').text(json.Error[fieldError].Message);
  }
  return fieldErrorList;
}

function getOnlyNosFromThePhoneNoString(phoneNumber) {
  var nosInPhoneNoArray = phoneNumber.match(/\d+/g);
  var phoneNo = "";
  for (var i = 0; i <= nosInPhoneNoArray.length - 1; i++) {
    phoneNo += nosInPhoneNoArray[i];
  }
  return phoneNo;
}

function initActionLinks2(actionMap, refreshGridRowFn, refreshDetailsPanelFn) {
  for (var i = 0; i < actionMap.length; i++) {
    bindToActionLink2(actionMap[i], refreshGridRowFn, refreshDetailsPanelFn);
  }
}

var currentSelectedActionLink = null;

function clickActionLink2(elementIdPrefix, actionLinkOnClickFn,
  refreshGridRowFn, refreshDetailsPanelFn, mapObj) {
  if (actionLinkOnClickFn != null)
    actionLinkOnClickFn();
  currentSelectedActionLink = elementIdPrefix;

  $("#action_result_panel").hide();
  $("#top_message_panel").hide();
  $("#" + mapObj.elementIdPrefix + "_panel").find(".agree input[type=checkbox]#accept_checkbox").removeAttr("checked");

  var dialog = $("#" + elementIdPrefix + "_panel")
    .dialog({
      width: 675,
      autoOpen: false,
      modal: true,
      zIndex: 2000,
      buttons: {
        "OK": function() {
          if ($currentGridRow.data("jsonObj") == null)
            return false;

          var actionUrl1 = mapObj.getActionUrlFn(this);
          if (actionUrl1 == null)
            return false;

          $(this).dialog("close");
          var $spinningWheel = $("#top_actions").find(
            "#spinning_wheel");
          $spinningWheel.find("#in_process_text").text(
            mapObj.actionText);
          $spinningWheel.show();

          if (mapObj.isAsync == true) {
            $
              .ajax({
                type: "GET",
                url: actionUrl1,
                async: false,
                cache: false,
                dataType: "json",
                success: function(obj) {
                  var property;
                  for (property in obj) {};
                  var jobId = obj[property].jobid;
                  if (jobId == null)
                    return;
                  var timerKey = "vmAction" + jobId;

                  // Process the async job
                  $("body")
                    .everyTime(
                      5000,
                      timerKey,
                      function() {
                        var actionUrl2 = cloudStackURL("command=queryAsyncJobResult&jobId=" + jobId);
                        $
                          .ajax({
                            type: "GET",
                            url: actionUrl2,
                            dataType: "json",
                            cache: false,
                            success: function(
                              json) {
                              var result = json.queryasyncjobresultresponse;
                              if (result.jobstatus == 0) {
                                return; // Job
                                // has
                                // not
                                // completed
                              } else {
                                $(
                                  "body")
                                  .stopTime(
                                    timerKey);
                                $(
                                  "#top_actions")
                                  .find(
                                    "#spinning_wheel")
                                  .hide();
                                if (result.jobstatus == 1) {
                                  // Succeeded
                                  var property;
                                  for (property in result.jobresult) {}; // e.g.
                                  // property
                                  // ==
                                  // "virtualmachine",
                                  // "volume",
                                  // "success"
                                  var item4 = result.jobresult[property]; // item4
                                  // might
                                  // be
                                  // an
                                  // object
                                  // or
                                  // true/"true"
                                  // (if
                                  // property
                                  // is
                                  // "success")
                                  if (typeof(item4) != "object") {
                                    if ((property == "success") && ((typeof(item4) == "string" && item4 == "true") ||
                                      (typeof(item4) == "boolean" && item4 == true))) {
                                      $currentGridRow
                                        .click();

                                      var msg;
                                      if (mapObj.afterActionSeccessFn != null) {
                                        msg = mapObj
                                          .afterActionSeccessFn(item4); // item4
                                        // might
                                        // be
                                        // an
                                        // object
                                        // or
                                        // true/"true".
                                        // e.g.
                                        // {
                                        // "deletevolumeresponse"
                                        // : {
                                        // "success"
                                        // :
                                        // "true"}
                                        // }
                                      }
                                      if (msg == null) {
                                        var actionlabel = $(
                                          "." + mapObj.elementIdPrefix + "_link")
                                          .find(
                                            "a")
                                          .first()
                                          .text();
                                        msg = actionlabel + " " + g_dictionary.succeeded;
                                      }

                                      $(
                                        "#action_result_panel")
                                        .find(
                                          "#msg")
                                        .html(
                                          msg);
                                      $(
                                        "#action_result_panel")
                                        .find(
                                          "#status_icon")
                                        .removeClass(
                                          "erroricon")
                                        .addClass(
                                          "successicon");
                                      $(
                                        "#action_result_panel")
                                        .removeClass(
                                          "error")
                                        .addClass(
                                          "success")
                                        .show();

                                    }
                                    return;
                                  }

                                  if ($currentGridRow
                                    .data("jsonObj") == null)
                                    return;

                                  var idPropertyName = null;
                                  if (mapObj.returnedObjectId != null) {
                                    if (mapObj.returnedObjectId != "NoReturnedObjectId")
                                      idPropertyName = mapObj.returnedObjectId;
                                    else
                                      idPropertyName = null;
                                  } else {
                                    idPropertyName = "id";
                                  }
                                  if ((idPropertyName == null) || (item4[idPropertyName] == $currentGridRow
                                    .data("jsonObj").id)) {
                                    var msg;
                                    if (mapObj.afterActionSeccessFn != null) {
                                      msg = mapObj
                                        .afterActionSeccessFn(item4); // item4
                                      // might
                                      // be
                                      // an
                                      // object
                                      // or
                                      // true/"true".
                                      // e.g.
                                      // {
                                      // "deletevolumeresponse"
                                      // : {
                                      // "success"
                                      // :
                                      // "true"}
                                      // }
                                    }
                                    if (msg == null) {
                                      var actionlabel = $(
                                        "." + mapObj.elementIdPrefix + "_link")
                                        .find(
                                          "a")
                                        .first()
                                        .text();
                                      msg = actionlabel + " " + g_dictionary.succeeded;
                                    }

                                    $(
                                      "#action_result_panel")
                                      .find(
                                        "#msg")
                                      .html(
                                        msg);
                                    $(
                                      "#action_result_panel")
                                      .find(
                                        "#status_icon")
                                      .removeClass(
                                        "erroricon")
                                      .addClass(
                                        "successicon");
                                    $(
                                      "#action_result_panel")
                                      .removeClass(
                                        "error")
                                      .addClass(
                                        "success")
                                      .show();
                                    $(
                                      "#" + mapObj.elementIdPrefix + "_panel")
                                      .find(
                                        ".agree input[type=checkbox]#accept_checkbox")
                                      .removeAttr(
                                        "checked");

                                    if (mapObj.detailsLevelAction != true) {
                                      refreshGridRowFn(
                                        item4,
                                        $currentGridRow);
                                      // $currentGridRow.click();
                                      refreshDetailsPanelFn($currentGridRow);
                                    }
                                  }
                                } else if (result.jobstatus == 2) {
                                  // Failed
                                  var actionlabel = $(
                                    "." + mapObj.elementIdPrefix + "_link")
                                    .find(
                                      "a")
                                    .first()
                                    .text();
                                  var msg = actionlabel + " " + g_dictionary.actionFailed;
                                  if (result.jobresult.errortext != null && result.jobresult.errortext.length > 0)
                                    msg += (" - " + localizeCloudStackMsg(fromdb(result.jobresult.errortext)));
                                  $(
                                    "#action_result_panel")
                                    .find(
                                      "#msg")
                                    .text(
                                      msg);
                                  $(
                                    "#action_result_panel")
                                    .find(
                                      "#status_icon")
                                    .removeClass(
                                      "successicon")
                                    .addClass(
                                      "erroricon");
                                  $(
                                    "#action_result_panel")
                                    .removeClass(
                                      "success")
                                    .addClass(
                                      "error")
                                    .show();
                                  $(
                                    "#" + mapObj.elementIdPrefix + "_panel")
                                    .find(
                                      ".agree input[type=checkbox]#accept_checkbox")
                                    .removeAttr(
                                      "checked");
                                }
                              }
                            },
                            error: function(
                              response) {
                              $(
                                "body")
                                .stopTime(
                                  timerKey);
                              $(
                                "#top_actions")
                                .find(
                                  "#spinning_wheel")
                                .hide();
                              $(
                                "#action_result_panel")
                                .find(
                                  "#msg")
                                .text(
                                  g_dictionary.errorFromAPICall + ": queryAsyncJobResult");
                              $(
                                "#action_result_panel")
                                .find(
                                  "#status_icon")
                                .removeClass(
                                  "successicon")
                                .addClass(
                                  "erroricon");
                              $(
                                "#action_result_panel")
                                .removeClass(
                                  "success")
                                .addClass(
                                  "error")
                                .show();
                              $(
                                "#" + mapObj.elementIdPrefix + "_panel")
                                .find(
                                  ".agree input[type=checkbox]#accept_checkbox")
                                .removeAttr(
                                  "checked");
                            }
                          });
                      }, 0);
                },
                error: function(
                  XMLHttpResponse) {
                  $("#top_actions").find(
                    "#spinning_wheel")
                    .hide();

                  var errorMsg = "";
                  if (XMLHttpResponse.responseText != null & XMLHttpResponse.responseText.length > 0)
                    errorMsg = parseXMLHttpResponse(XMLHttpResponse);

                  var actionlabel = $(
                    "." + mapObj.elementIdPrefix + "_link")
                    .find("a").first()
                    .text();
                  var msg = actionlabel + " " + g_dictionary.actionFailed;
                  if (errorMsg.length > 0)
                    msg += (" " + localizeCloudStackMsg(errorMsg));
                  else
                    msg += (" " + g_dictionary.errorFromAPICall + ": " + mapObj.apiName);
                  $("#action_result_panel")
                    .find("#msg").text(
                      msg);
                  $("#action_result_panel")
                    .find(
                      "#status_icon")
                    .removeClass(
                      "successicon")
                    .addClass(
                      "erroricon");
                  $("#action_result_panel")
                    .removeClass(
                      "success")
                    .addClass("error")
                    .show();
                  $(
                    "#" + mapObj.elementIdPrefix + "_panel")
                    .find(
                      ".agree input[type=checkbox]#accept_checkbox")
                    .removeAttr(
                      "checked");
                }
              });
          } else { // sync
            $
              .ajax({
                type: "GET",
                url: actionUrl1,
                async: false,
                cache: false,
                dataType: "json",
                success: function(obj1) {
                  if (typeof(obj1) != "object")
                    return;
                  var propertyInObj1;
                  for (propertyInObj1 in obj1) {}; // e.g. propertyInObj1
                  // ==
                  // "updatevirtualmachineresponse"
                  var obj2 = obj1[propertyInObj1];
                  if (typeof(obj2) != "object")
                    return;
                  var propertyInObj2;
                  for (propertyInObj2 in obj2) {}; // e.g. propertyInObj2
                  // == "virtualmachine"
                  var item3 = obj2[propertyInObj2]; // item3
                  // might
                  // be
                  // an
                  // object or a
                  // string "success".
                  // e.g. {
                  // "deletevolumeresponse"
                  // : { "success" :
                  // "true"} }
                  $("#top_actions").find(
                    "#spinning_wheel")
                    .hide();

                  var msg;
                  if (mapObj.afterActionSeccessFn != null) {
                    msg = mapObj
                      .afterActionSeccessFn(item3); // item3
                    // might
                    // be an
                    // object
                    // or a
                    // string
                    // "success".
                    // e.g. {
                    // "deletevolumeresponse"
                    // : {
                    // "success"
                    // :
                    // "true"}
                    // }
                  }
                  if (msg == null) {
                    var actionlabel = $(
                      "." + mapObj.elementIdPrefix + "_link")
                      .find("a")
                      .first().text();
                    msg = actionlabel + " " + g_dictionary.succeeded;
                  }

                  $("#action_result_panel")
                    .find("#msg").text(
                      msg);
                  $("#action_result_panel")
                    .find(
                      "#status_icon")
                    .removeClass(
                      "erroricon")
                    .addClass(
                      "successicon");
                  $("#action_result_panel")
                    .removeClass(
                      "error")
                    .addClass("success")
                    .show();

                  if (typeof(item3) == "object") {
                    refreshGridRowFn(item3,
                      $currentGridRow);
                    // $currentGridRow.click();
                    refreshDetailsPanelFn($currentGridRow);
                  }

                  if ((propertyInObj2 == "success") && ((typeof(item3) == "string" && item3 == "true") || (typeof(
                    item3) == "boolean" && item3 == true))) {
                    $currentGridRow.click(); // this
                    // will
                    // hide
                    // action_result_panel
                    $(
                      "#action_result_panel")
                      .find("#msg")
                      .text(msg);
                    $(
                      "#action_result_panel")
                      .find(
                        "#status_icon")
                      .removeClass(
                        "erroricon")
                      .addClass(
                        "successicon");
                    $(
                      "#action_result_panel")
                      .removeClass(
                        "error")
                      .addClass(
                        "success")
                      .show(); // so,
                    // show
                    // action_result_panel
                    // (again)
                  }
                },
                error: function(
                  XMLHttpResponse) {
                  $("#top_actions").find(
                    "#spinning_wheel")
                    .hide();

                  var errorMsg = "";
                  if (XMLHttpResponse.responseText != null & XMLHttpResponse.responseText.length > 0)
                    errorMsg = parseXMLHttpResponse(XMLHttpResponse);

                  var actionlabel = $(
                    "." + mapObj.elementIdPrefix + "_link")
                    .find("a").first()
                    .text();
                  var msg = actionlabel + " " + g_dictionary.actionFailed;

                  if (errorMsg.length > 0)
                    msg += (" " + localizeCloudStackMsg(errorMsg));
                  else
                    msg += (" " + g_dictionary.errorFromAPICall + ": " + mapObj.apiName);

                  $("#action_result_panel")
                    .find("#msg").text(
                      msg);
                  $("#action_result_panel")
                    .find(
                      "#status_icon")
                    .removeClass(
                      "successicon")
                    .addClass(
                      "erroricon");
                  $("#action_result_panel")
                    .removeClass(
                      "success")
                    .addClass("error")
                    .show();
                  $(
                    "#" + mapObj.elementIdPrefix + "_panel")
                    .find(
                      ".agree input[type=checkbox]#accept_checkbox")
                    .removeAttr(
                      "checked");
                }
              });
          }

        },
        "Cancel": function() {
          $("#" + mapObj.elementIdPrefix + "_panel")
            .find(
              ".agree input[type=checkbox]#accept_checkbox")
            .removeAttr("checked");
          if (currentSelectedActionLink == elementIdPrefix)
            currentSelectedActionLink = null;

          $(this).dialog("close");
        }

      }
    });

  activateDialog(dialog);
  dialogButtonsLocalizer(dialog, {
    'OK': g_dictionary.dialogOK,
    'Cancel': g_dictionary.dialogCancel,
    'Close': g_dictionary.dialogClose
  });
  dialog.dialog("open");

}

var $currentSelectedVolume = null; // Global variable for Volume ID for
// snapshot
// on Instances page.
var $currentSelectedSnapshot = null; // Global variable for Snapshot ID for
// snapshots on Volumes page.

function bindToActionLink2(mapObj, refreshGridRowFn, refreshDetailsPanelFn) {
  $("." + mapObj.elementIdPrefix + "_link").unbind("click").bind(
    "click",
    function(event) {

      $currentSelectedVolume = $(this).attr('ref_volume'); // Selected
      // Volume
      // ID for snapshot
      // on Instances
      // page.

      $currentSelectedSnapshot = $(this).parents('#action_menu')
        .attr('ref_snapshot');

      clickActionLink2(mapObj.elementIdPrefix,
        mapObj.actionLinkOnClickFn, refreshGridRowFn,
        refreshDetailsPanelFn, mapObj);

    });

}

function activateThirdMenuItem(menuItem) {
  $("#" + menuItem).removeClass("off").addClass("on");
}

function deactivateThirdMenuItem(menuItem) {
  $("#" + menuItem).removeClass("on").addClass("off");
}

var l3menuTenantParam = $("#l3_tenant_param").val();


$("#l3_usage_details_tab").bind(
  "click",
  function(event) {
    $(".thirdlevel_subtab").removeClass("on").addClass("off");
    $(this).removeClass("off").addClass("on");
    window.location = "/portal/portal/billing/usageBilling?tenant=" + effectiveTenantParam;

  });
$("#l3_subscriptions_tab")
  .bind(
    "click",
    function(event) {
      $(".thirdlevel_subtab").removeClass("on").addClass("off");
      $(this).removeClass("off").addClass("on");
      if ($("#usage_billing_my_usage").val() == "true") {
        window.location = "/portal/portal/usage/subscriptions?tenant=" + effectiveTenantParam + "&perPage=14&page=1";
      } else {
        window.location = "/portal/portal/billing/subscriptions?tenant=" + effectiveTenantParam + "&perPage=14&page=1";
      }
    });

$("#l3_billing_invoices_tab").bind(
  "click",
  function(event) {
    $(".thirdlevel_subtab").removeClass("on").addClass("off");
    $(this).removeClass("off").addClass("on");
    window.location = "/portal/portal/billing/history?tenant=" + effectiveTenantParam + "&perPage=14&page=1";
  });
$("#l3_billing_payments_tab").bind(
  "click",
  function(event) {
    $(".thirdlevel_subtab").removeClass("on").addClass("off");
    $(this).removeClass("off").addClass("on");
    window.location = "/portal/portal/billing/paymenthistory?tenant=" + effectiveTenantParam + "&perPage=14&page=1";
  });

$("#l3_payment_info_tab")
  .bind(
    "click",
    function(event) {
      $(".thirdlevel_subtab").removeClass("on").addClass("off");
      $(this).removeClass("off").addClass("on");
      window.location = "/portal/portal/billing/showcreditcarddetails?tenant=" + effectiveTenantParam;
    });

$("#13_health_status_tab").bind("click", function(event) {
  var selectedServiceInstanceUUID = $("#selectedServiceInstance").find(".downarrow").attr("id");
  $(".thirdlevel_subtab").removeClass("on").addClass("off");
  $(this).removeClass("off").addClass("on");
  if (typeof selectedServiceInstanceUUID == 'undefined' || selectedServiceInstanceUUID == null ||
    selectedServiceInstanceUUID == "") {
    window.location = "/portal/portal/health";
  } else {
    window.location = "/portal/portal/health?serviceinstanceuuid=" + selectedServiceInstanceUUID;
  }
});

$("#13_health_scheduled_maintainence_tab").bind("click", function(event) {
  var selectedServiceInstanceUUID = $("#selectedServiceInstance").find(".downarrow").attr("id");
  $(".thirdlevel_subtab").removeClass("on").addClass("off");
  $(this).removeClass("off").addClass("on");
  if (typeof selectedServiceInstanceUUID == 'undefined' || selectedServiceInstanceUUID == null ||
    selectedServiceInstanceUUID == "") {
    window.location = "/portal/portal/health/health_maintainance";
  } else {
    window.location = "/portal/portal/health/health_maintainance?serviceinstanceuuid=" + selectedServiceInstanceUUID;
  }
});

$("#l3_compute_bundles_tab")
  .bind(
    "click",
    function(event) {
      $(".thirdlevel_subtab").removeClass("on").addClass("off");
      $(this).removeClass("off").addClass("on");
      window.location = "/portal/portal/subscription/createsubscription?tenant=" + l3menuTenantParam;
    });

$("#l3_compute_bundles_tab_browse_catalog").bind("click", function(event) {
  var searchq = window.location.search;
  var lang = "";
  if (searchq.indexOf("lang=") != -1) {
    lang = searchq.substring(searchq.indexOf("lang="));
    window.location = "/portal/portal/browse_catalogs?" + lang;
  } else
    window.location = "/portal/portal/browse_catalogs";
});

$("#l3_account_All_tab").bind(
  "click",
  function(event) {
    $(".thirdlevel_subtab").removeClass("on").addClass("off");
    $(this).removeClass("off").addClass("on");
    window.location = "/portal/portal/tenants/list?accountType=" + selectedAccountType + "&filterBy=All";
  });

$("#l3_service_bundles_tab")
  .bind(
    "click",
    function(event) {
      $(".thirdlevel_subtab").removeClass("on").addClass("off");
      $(this).removeClass("off").addClass("on");
      window.location = "/portal/portal/subscription/subscribeNonVmBundle?tenant=" + l3menuTenantParam;
    });

$("#l3_account_0_tab").bind(
  "click",
  function(event) {
    $(".thirdlevel_subtab").removeClass("on").addClass("off");
    $(this).removeClass("off").addClass("on");
    window.location = "/portal/portal/tenants/list?accountType=" + selectedAccountType + "&filterBy=0";
  });

$("#l3_account_1_tab").bind(
  "click",
  function(event) {
    // $(".thirdlevel_subtab").removeClass("on").addClass("off");
    $(this).removeClass("off").addClass("on");
    window.location = "/portal/portal/tenants/list?accountType=" + selectedAccountType + "&filterBy=1";
  });

$("#l3_account_2_tab").bind(
  "click",
  function(event) {
    $(".thirdlevel_subtab").removeClass("on").addClass("off");
    $(this).removeClass("off").addClass("on");
    window.location = "/portal/portal/tenants/list?accountType=" + selectedAccountType + "&filterBy=2";
  });

$("#l3_account_3_tab").bind(
  "click",
  function(event) {
    $(".thirdlevel_subtab").removeClass("on").addClass("off");
    $(this).removeClass("off").addClass("on");
    window.location = "/portal/portal/tenants/list?accountType=" + selectedAccountType + "&filterBy=3";
  });

$("#l3_account_4_tab").bind(
  "click",
  function(event) {
    $(".thirdlevel_subtab").removeClass("on").addClass("off");
    $(this).removeClass("off").addClass("on");
    window.location = "/portal/portal/tenants/list?accountType=" + selectedAccountType + "&filterBy=4";
  });

$("#l3_profile_serviceprovider_tab").bind(
  "click",
  function(event) {
    initialiseProfilesPage("spProfiles");
  });

$("#l3_profile_customer_tab").bind(
  "click",
  function(event) {
    initialiseProfilesPage("customerProfiles");
  });

$("#l3_profile_partner_tab").bind(
  "click",
  function(event) {
    initialiseProfilesPage("");
  });
$("#12_content_0_tab").bind("click", function(event) {
  $(".thirdlevel_subtab").removeClass("on").addClass("off");
  $(this).removeClass("off").addClass("on");
  window.location = "/portal/portal/admin/email_templates?filterby=0";
});

$("#12_content_1_tab").bind("click", function(event) {
  $(".thirdlevel_subtab").removeClass("on").addClass("off");
  $(this).removeClass("off").addClass("on");
  window.location = "/portal/portal/admin/email_templates?filterby=1";
});

$("#12_content_2_tab").bind("click", function(event) {
  $(".thirdlevel_subtab").removeClass("on").addClass("off");
  $(this).removeClass("off").addClass("on");
  window.location = "/portal/portal/admin/email_templates?filterby=2";
});

$("#12_content_3_tab").bind("click", function(event) {
  $(".thirdlevel_subtab").removeClass("on").addClass("off");
  $(this).removeClass("off").addClass("on");
  window.location = "/portal/portal/admin/email_templates?filterby=3";
});

$("#12_content_4_tab").bind("click", function(event) {
  $(".thirdlevel_subtab").removeClass("on").addClass("off");
  $(this).removeClass("off").addClass("on");
  window.location = "/portal/portal/admin/email_templates?filterby=4";
});

$("#l3_billing_record_deposit_tab")
  .bind(
    "click",
    function(event) {
      $(".thirdlevel_subtab").removeClass("on").addClass("off");
      $(this).removeClass("off").addClass("on");
      window.location = "/portal/portal/billing/show_record_deposit?tenant=" + l3menuTenantParam;
    });

$("#l3_config_configaccountmanagement_tab")
  .bind(
    "click",
    function(event) {
      $(".thirdlevel_subtab").removeClass("on").addClass("off");
      $(this).removeClass("off").addClass("on");
      window.location = "/portal/portal/admin/config/show_configuration?module=ConfigAccountManagement";
    });

$("#l3_config_accountprovisioning_tab")
  .bind(
    "click",
    function(event) {
      $(".thirdlevel_subtab").removeClass("on").addClass("off");
      $(this).removeClass("off").addClass("on");
      window.location = "/portal/portal/admin/config/show_configuration?module=AccountProvisioning";
    });

$("#l3_config_billing_tab")
  .bind(
    "click",
    function(event) {
      $(".thirdlevel_subtab").removeClass("on").addClass("off");
      $(this).removeClass("off").addClass("on");
      window.location = "/portal/portal/admin/config/show_configuration?module=Billing";
    });


$("#l3_config_crm_tab")
  .bind(
    "click",
    function(event) {
      $(".thirdlevel_subtab").removeClass("on").addClass("off");
      $(this).removeClass("off").addClass("on");
      window.location = "/portal/portal/admin/config/show_configuration?module=CRM";
    });

$("#l3_config_helpdesk_tab")
  .bind(
    "click",
    function(event) {
      $(".thirdlevel_subtab").removeClass("on").addClass("off");
      $(this).removeClass("off").addClass("on");
      window.location = "/portal/portal/admin/config/show_configuration?module=HelpDesk";
    });

$("#l3_config_integration_tab")
  .bind(
    "click",
    function(event) {
      $(".thirdlevel_subtab").removeClass("on").addClass("off");
      $(this).removeClass("off").addClass("on");
      window.location = "/portal/portal/admin/config/show_configuration?module=Integration";
    });

$("#l3_home_connectors_cs_tab")
  .bind(
    "click",
    function(event) {
      $(".thirdlevel_subtab").removeClass("on").addClass("off");
      $(this).removeClass("off").addClass("on");
      window.location = "/portal/portal/connector/cs";
    });

$("#l3_home_connectors_oss_tab")
  .bind(
    "click",
    function(event) {
      $(".thirdlevel_subtab").removeClass("on").addClass("off");
      $(this).removeClass("off").addClass("on");
      window.location = "/portal/portal/connector/oss";
    });

$("#l3_config_server_tab")
  .bind(
    "click",
    function(event) {
      $(".thirdlevel_subtab").removeClass("on").addClass("off");
      $(this).removeClass("off").addClass("on");
      window.location = "/portal/portal/admin/config/show_configuration?module=Server";
    });
$("#l3_config_portal_tab")
  .bind(
    "click",
    function(event) {
      $(".thirdlevel_subtab").removeClass("on").addClass("off");
      $(this).removeClass("off").addClass("on");
      window.location = "/portal/portal/admin/config/show_configuration?module=Portal";
    });
$("#l3_config_marketing_tab")
  .bind(
    "click",
    function(event) {
      $(".thirdlevel_subtab").removeClass("on").addClass("off");
      $(this).removeClass("off").addClass("on");
      window.location = "/portal/portal/admin/config/show_configuration?module=Marketing";
    });

$("#l3_config_trialmanagement_tab")
  .bind(
    "click",
    function(event) {
      $(".thirdlevel_subtab").removeClass("on").addClass("off");
      $(this).removeClass("off").addClass("on");
      window.location = "/portal/portal/admin/config/show_configuration?module=TrialManagement";
    });


function isBrowserIE7() {
  if (navigator.appVersion.indexOf('MSIE 7.') == -1)
    return false;
  return true;
}

function viewUtilitRates(tenantParam, id, url, serviceInstanceUUID) {
  var target_url = "/portal/portal/subscription/utilityrates_table?tenant=" + tenantParam + "&serviceInstanceUuid=" +
    serviceInstanceUUID;
  if (url != null) {
    target_url = url;
  }
  $("#full_page_spinning_wheel").show();
  initDialog(id, 850);
  $.ajax({
    type: "GET",
    url: target_url + "&isDialog=true",
    dataType: "html",
    cache: false,
    success: function(html) {

      var $thisDialog = $("#" + id);
      $thisDialog.dialog('option', 'minHeight', 400);
      $thisDialog.dialog('option', 'minWidth', 750);

      $thisDialog.dialog('option', 'buttons', {
        "Close": function() {
          $(this).dialog("close");
        }
      });
      dialogButtonsLocalizer($thisDialog, {
        'Close': g_dictionary.dialogClose
      });
      $("#full_page_spinning_wheel").hide();
      $thisDialog.html(html);
      $thisDialog.dialog("open");
    },
    error: function() {
      $("#full_page_spinning_wheel").hide();
    }
  });
  return false;
}

function dialogButtonsLocalizer(dialogPanel, localizedButtonLabels) {

  var buttons = dialogPanel.dialog("option", "buttons");
  var localizedButtons = {};
  $.each(buttons, function(key, value) {
    if (localizedButtonLabels[key]) {
      localizedButtons[localizedButtonLabels[key]] = value;
    } else {
      localizedButtons[key] = value;
    }
  });
  dialogPanel.dialog("option", "buttons", localizedButtons);
}

function popUpDialogForAlerts(dialog_div_id, message, callback) {
  initDialog(dialog_div_id, 390);
  $thisDialog = $("#" + dialog_div_id);
  var informationObj = CPBM.parseJsonSafely(message);
  var detailedMessage = null;
  if(informationObj){
    message = informationObj.message;
    detailedMessage = informationObj.detailedMessage;
    if(detailedMessage){
      var detailMessageHtml = '(<a id="information_popover" class="subscription_resource_error" data-content="'+detailedMessage+
      '" data-toggle="popover" data-trigger="hover" data-placement="bottom" data-container="body">'+g_dictionary.details+'</a>)';  
      message = message + detailMessageHtml;
    }
  }
  $thisDialog.html(message);
  $thisDialog.dialog('option', 'buttons', {
    "OK": function() {
      $(this).dialog("close");
      if(callback != undefined || callback !=null){
    	  callback();
      }
    }
  });
  dialogButtonsLocalizer($thisDialog, {
    'OK': g_dictionary.dialogOK
  });
  $thisDialog.bind("dialogbeforeclose", function(event, ui) {
    $thisDialog.empty();
  });
  $thisDialog.dialog("open");
  if(detailedMessage){
    $("#information_popover").popover();
  }
}

function popUpDialogForConfirmation(dialog_div_id, message, callback) {
	  initDialog(dialog_div_id, 390);
	  $thisDialog = $("#" + dialog_div_id);
	  var informationObj = CPBM.parseJsonSafely(message);
	  var detailedMessage = null;
	  if(informationObj){
	    message = informationObj.message;
	    detailedMessage = informationObj.detailedMessage;
	    if(detailedMessage){
	      var detailMessageHtml = '(<a id="information_popover" class="subscription_resource_error" data-content="'+detailedMessage+
	      '" data-toggle="popover" data-trigger="hover" data-placement="bottom" data-container="body">'+g_dictionary.details+'</a>)';  
	      message = message + detailMessageHtml;
	    }
	  }
	  $thisDialog.html(message);
	  $thisDialog.dialog('option', 'buttons', {
	    "OK": function() {
	      $(this).dialog("close");
	      if(callback != undefined || callback !=null){
	    	  callback();
	      }
	    }, 
	    "Cancel": function(){
	      $(this).dialog("close");
	    }
	  });
	  dialogButtonsLocalizer($thisDialog, {
	    'OK': g_dictionary.dialogOK,
	    'Cancel': g_dictionary.dialogCancel
	  });
	  $thisDialog.bind("dialogbeforeclose", function(event, ui) {
	    $thisDialog.empty();
	  });
	  $thisDialog.dialog("open");
	  if(detailedMessage){
	    $("#information_popover").popover();
	  }
}

function getTaxableAmount(amount) {
  var taxableAmount = amount;
  $.ajax({
    type: "GET",
    url: "/portal/portal/subscription/taxable_amount?amount=" + amount,
    async: false,
    dataType: "html",
    success: function(json) {
      taxableAmount = json;
    }
  });
  return taxableAmount;
}

function i18nUomText(uom) {
  switch (uom) {
    case "GB-Months":
      if (g_dictionary.GB_Months != null && g_dictionary.GB_Months != "undefined") {
        uom = g_dictionary.GB_Months;
      }
      break;
    case "GB":
      if (g_dictionary.GB != null && g_dictionary.GB != "undefined") {
        uom = g_dictionary.GB;
      }
      break;
    case "IP-Month":
      if (g_dictionary.IP_Month != null && g_dictionary.IP_Month != "undefined") {
        uom = g_dictionary.IP_Month;
      }
      break;
    case "Rules":
      if (g_dictionary.Rules != null && g_dictionary.Rules != "undefined") {
        uom = g_dictionary.Rules;
      }
      break;
    case "Hours":
      if (g_dictionary.Hours != null && g_dictionary.Hours != "undefined") {
        uom = g_dictionary.Hours;
      }
      break;
  }
  return uom;
}

function i18nBooleanString(field) {
  switch (field) {
    case true:
      if (g_dictionary.labelTrue != null && g_dictionary.labelTrue != "undefined") {
        field = g_dictionary.labelTrue;
      }
      break;
    case false:
      if (g_dictionary.labelFalse != null && g_dictionary.labelFalse != "undefined") {
        field = g_dictionary.labelFalse;
      }
      break;
  }
  return field;
}

function focusFirstItemInGivenContainer(container_id) {
  var firstInput = $("#" + container_id)
    .find(
      'input[type=text],input[type=password],input[type=radio],input[type=checkbox],textarea,select')
    .filter(':visible:first');
  if (firstInput != null) {
    firstInput.focus();
  }
}


// Localize the message from CloudStack

function localizeCloudStackMsg(msg) {

  var localizedMsg = "";

  if ((msg) && (language != "en")) {

    for (var i = 0; i < CloudStack_Message_Regexp.length; i++) {
      var re = new RegExp("");
      re.compile(CloudStack_Message_Regexp[i][0]);

      if (re.test(msg)) {
        // save the all matched strings (the whole string and the all partial strings)
        var execResult = re.exec(msg);

        // check that the whole message is matched
        if (execResult[0] == msg) {

          //
          // localize the message with no localizing the arguments
          //
          if (CloudStack_Message_Regexp[i].length <= 2) {
            // localize the message
            localizedMsg = msg.replace(re, CloudStack_Message_Regexp[i][1]);

            //
            // localize the message with localizing the arguments
            //
          } else {

            var key = subKey = "";

            //
            // localize the each argument
            if (CloudStack_Message_Regexp[i].length == 3) {
              // localize the message with no localizing the argument, at first
              localizedMsg = msg.replace(re, CloudStack_Message_Regexp[i][1]);

              for (var j = 0;
                (j < CloudStack_Message_Regexp[i][2].length) || (j < execResult.length - 1); j++) {

                if (CloudStack_Message_Regexp[i][2][j] != null) {
                  subKey = execResult[j + 1];
                  re.compile(subKey);
                  // create the key to the localized argument based on the value of argument
                  key = CloudStack_Message_Regexp[i][2][j] + "_" + subKey;
                  // localize the each argument
                  if (CloudStack_Message_Args[key]) {
                    localizedMsg = localizedMsg.replace(re, CloudStack_Message_Args[key]);
                  }
                }
              }

              //
              // localize the whole sentence based on the arguments
            } else if (CloudStack_Message_Regexp[i].length == 4) {

              // create the key to the localized sentence based on the value of arguments
              for (var j = 0;
                (j < CloudStack_Message_Regexp[i][2].length) || (j < execResult.length - 1); j++) {
                if (CloudStack_Message_Regexp[i][2][j]) {
                  if (key != "") key += "_";
                  key += execResult[j + 1];
                }
              }
              // localize the whole sentence
              if (CloudStack_Message_Regexp[i][3][key]) {
                localizedMsg = msg.replace(re, CloudStack_Message_Regexp[i][3][key]);
              }
            }
          }
          break;
        }
      }
    }
    if (localizedMsg == "") localizedMsg = msg;
  } else {
    localizedMsg = msg;
  }
  return localizedMsg;
}

function checkDelinquent(isDelinquent, redirectToBilling, redirectToDashBoard, showMakePaymentMessage, tenantParam) {
  if (isDelinquent == true) {
    if (redirectToBilling == true) {
      window.location = "/portal/portal/billing/history?tenant=" + tenantParam + "&action=launchvm"; //redirect them to payment page
    } else if (redirectToDashBoard == true) {
      popUpDialogForAlerts("dialog_info", showMakePaymentMessage);
      window.location = "/portal/portal/home?tenant=" + tenantParam + "&secondLevel=true"; //redirect them to user dashboard page
    }
    return false;
  }
  return true;
}

function addParametersToUrl(url, key, value) {
  if (url.indexOf("?") >= 0) {
    url = url + "&";
  } else {
    url = url + "?";
  }
  url = url + key + "=" + value;
  return url;
}

function singleSignOn(tenantParam, serviceInstanceUUID) {
  if (serviceInstanceUUID == null || serviceInstanceUUID == "") {
    return false;
  }
  var returnVal = true;
  var url = "/portal/portal/manage_resource/get_sso_cmd_string";

  if (tenantParam != null && tenantParam != "") {
    url = addParametersToUrl(url, "tenant", tenantParam);
  }
  url = addParametersToUrl(url, "serviceInstanceUUID", serviceInstanceUUID);
  $.ajax({
    type: "POST",
    url: url,
    async: false,
    dataType: "json",
    success: function(responseMap) {
      if (responseMap.status == 'fail') {
        if (responseMap.error_message != null) {
          popUpDialogForAlerts("dialog_info", responseMap.error_message);
        }
        ssoResponseMap = null;
        returnVal = responseMap.url;
      } else {
        ssoResponseMap = responseMap;
      }
    },
    error: function(XMLHttpResponse) {
      popUpDialogForAlerts("dialog_info", g_dictionary.error_single_sign_on);
      ssoResponseMap = null;
      returnVal = false;
    }
  });
  return returnVal;
}

function updateServiceInstanceItems(current, fn, tenantParam) {
  var serviceInstanceUUID = $(current).attr("id");
  $selectedServiceInstance = $("#selectedServiceInstance");
  $selectedServiceInstance.html("<span class='ellipsis ' >"+$(current).text() + "</span><div class='downarrow' id='" + $(current).attr("id") + "'><i class='fa fa-caret-down'></i></div>");
  $("#serviceInstanceDropdownlist").css('display', 'none');
  fn(serviceInstanceUUID, tenantParam);
}

function populateServiceInstances(serviceCategory, tenantParam, fn, firstTimefn) {
  if (typeof serviceCategory == 'undefined') {
    return;
  }
  actionUrl = "/portal/portal/connector/service_instance_list"
  if ($("#viewChannelCatalog").val() == 'true') {
    actionUrl = addParametersToUrl(actionUrl, "viewCatalog", true);
  }
  $.ajax({
    type: "GET",
    url: actionUrl,
    async: true,
    data: {
      category: serviceCategory,
      tenant: tenantParam
    },
    dataType: "json",
    success: function(returnData) {
      var serviceInstanceSelect = $("#serviceInstanceList");
      serviceInstanceSelect.empty();
      var selectedCloudServiceInstance = $("#selectedCloudServiceInstance").val();
      if (typeof selectedCloudServiceInstance == 'undefined' || selectedCloudServiceInstance == null ||
        selectedCloudServiceInstance == "") {
        selectedCloudServiceInstance = null;
      }
      var items = returnData["ServiceInstances"];
      if (items != null && items.length > 0) {
        $("#serviceInstanceListContainer").show();
        var serviceInstanceUUID = items[0].uuid;
        if (selectedCloudServiceInstance == null) {
          var $selectedServiceInstance = $("#selectedServiceInstance");
          $selectedServiceInstance.html("<span class='ellipsis'>"+items[0].name + "</span><div class='downarrow' id='" + items[0].uuid + "'><i class='fa fa-caret-down'></i></div>");
        }
        var serviceInstanceItem;
        var select_option;
        for (var i = 0; i < items.length; i++) {
          serviceInstanceItem = items[i];
          if (serviceInstanceItem.uuid == selectedCloudServiceInstance) {
            var $selectedServiceInstance = $("#selectedServiceInstance");
            $selectedServiceInstance.html("<span class='ellipsis'>"+serviceInstanceItem.name + "</span><div class='downarrow' id='" +
              serviceInstanceItem.uuid + "'><i class='fa fa-caret-down'></i></div>");
            serviceInstanceUUID = selectedCloudServiceInstance;
          }
          select_option = "<li class='ellipsis' title='"+serviceInstanceItem.name+"' id='" + serviceInstanceItem.uuid + "' onclick='updateServiceInstanceItems(this, " + fn +
            ", \"" + tenantParam + "\");' >" + serviceInstanceItem.name + "</li>";
          serviceInstanceSelect.append(select_option);

        }
        $("#serviceInstanceListContainer").bind("mouseover", function(event) {
          $(this).find("#serviceInstanceDropdownlist").show();
          return false;
        });
        $("#serviceInstanceListContainer").bind("mouseout", function(event) {
          var $thisElement = $(this)[0];
          var relatedTarget1 = event.relatedTarget;
          while (relatedTarget1 != null && relatedTarget1.nodeName != "BODY" && relatedTarget1 != $thisElement) {
            relatedTarget1 = relatedTarget1.parentNode;
          }
          if (relatedTarget1 == $thisElement) {
            return;
          }
          $(this).find("#serviceInstanceDropdownlist").hide();
          return false;
        });
        if (typeof firstTimefn != 'undefined') {
          firstTimefn(serviceInstanceUUID, tenantParam);
        }
      } else {
        $("#serviceInstanceListContainer").hide();
        if (typeof firstTimefn != 'undefined') {
          firstTimefn("", tenantParam);
        }
      }

    },
    error: function() {
      //need to handle TODO
    }
  });
}

function displayErrorDialog(XMLHttpResponse) {
  var textToDisplay = "Error"; //TODO localized default string
  if (XMLHttpResponse.status === INTERNAL_SERVER_FAILED_CODE) {
    textToDisplay = XMLHttpResponse.statusText + "\n" + XMLHttpResponse.responseText;
  }
  initDialogWithOK("dialog_info", 350, false);
  $("#dialog_info").dialog("option", "height", 150);
  $("#dialog_info").text(textToDisplay).dialog("open");
}

/* START js to show workflows popup */

$(".workflowDetailsPopup").live("click", function() {
  var $opener = $(this);
  var workflowUUID = $opener.attr('id').replace("workflowdetails", "");
  var workflowDetailsurl = "/portal/portal/workflow/" + workflowUUID;
  var workflowDetailsGet = $.ajax({
    type: "GET",
    url: workflowDetailsurl,
    dataType: "html",
    cache: false
  });

  $(".workflow_details_popup").each(function(index) {
    if (index != 0) {
      $(this).remove();
    }
  });
  var $workflowDetailsDialog = $(".workflow_details_popup");
  $workflowDetailsDialog.dialog({
    width: 700,
    modal: true,
    resizable: false,
    autoOpen: false,
    buttons: {
      "OK": function() {
        $workflowDetailsDialog.dialog("close");
      }
    },
    close: function(event, ui) {
      $workflowDetailsDialog.dialog("destroy").html("");
    }
  });
  dialogButtonsLocalizer($workflowDetailsDialog, {
    'OK': g_dictionary.dialogOK
  });
  workflowDetailsGet.done(function(html) {
    $workflowDetailsDialog.html(html);
    //Following line enables the popover of task memo 
    $workflowDetailsDialog.find(".js_workflow_status_error").popover();
    $workflowDetailsDialog.data('opener', $opener).dialog("open");
  });
});

$(".workflow_details_popup .workflow_activitycontainer .statusearea a").die("click").live("click", function() {
  $(this).closest(".workflow_activitycontainer").find(".error").slideToggle('slow');
});

function expandActivityDetails(bucketNumber) {
  var $divToExpand = $("#activitypanel" + bucketNumber);
  $('.activitypanel').each(function() {
    if (!$(this).hasClass("hide") && $(this).attr('id').replace("activitypanel", "") != $divToExpand.attr('id').replace(
      "activitypanel", "")) {
      $(this).addClass("hide");
    }
  });
  $divToExpand.toggleClass("hide");
}

$(".workflow_resetbutton").live("click", function() {
  var workflowUUID = $(this).closest(".workflow_details_popup").data('opener').attr('id').replace("workflowdetails",
    "")
  var resetUrl = "/portal/portal/workflow/" + workflowUUID + "/reset";

  var resetWorkflowGet = $.ajax({
    type: "POST",
    url: resetUrl,
    dataType: "text"
  });
  resetWorkflowGet.done(function(text) {
    if (text == 'true') {
      $("#workflowdetails" + workflowUUID).click();
    }
  });
});

/* END js to show workflows popup */

function isNotBlank(str) {
  if (str != null && str != "" && typeof(str) != "undefined") {
    return true;
  }
  return false;
}

function isBlank(str) {
  return !isNotBlank(str);
}


String.prototype.format = String.prototype.f = function(argList) {
  var s = this,
    i = argList.length;
  while (i--) {
    s = s.replace(new RegExp('\\{' + i + '\\}', 'gm'), argList[i]);
  }
  return s;
};

function getFormattedDisplayAttribtutesDescription(message, attributes) {
  var displayString = "";
  if (attributes != null) {
    var argsList = [];
    for (var i = 0; i < attributes.length; i++) {
      da = attributes[i];
      argsList.push(da.value);
    }
    displayString = message.f(argsList);
  }
  return displayString;
}

function getFormattedDisplayAttribtutesString(attributes, forInfo) {
  
  var separator = ", ";
  if(forInfo != null && forInfo) {
    separator = "<br/>";
  }
  
  var displayString = "";
  if (attributes != null) {
    for (var i = 0; i < attributes.length; i++) {
      da = attributes[i];
      displayString += displayString == "" ? "" : separator;
      displayString += "<b>" + da.name + "</b>: " + da.value;
    }
  }
  return displayString;
}

function getFormattedAttribtutesString(attributes, forInfo) {
  
  var separator = ", ";
  if(forInfo != null && forInfo) {
    separator = "<br/>";
  }
    
  var displayString = "";
  if (attributes != null) {
    for (var component in attributes) {
      displayString += displayString == "" ? "" : separator;
      displayString += "<b>" + component + "</b>: " + attributes[component];
    }
  }
  return displayString;
}

var showResourcesIFrameWithServiceInstanceUUID = function(serviceInstanceUUID) {

  
  var $iframe_tab = $("#iframe_tab_" + serviceInstanceUUID);
  
  $.ajax({
    url: "/portal/portal/manage_resource/get_resource_views",
    dataType: "json",
    async: false,
    data: {
      serviceInstanceUUID: serviceInstanceUUID,
      tenant: effectiveTenantParam
    },

    cache: false,
    success: function(json) {
      singleSignOn(effectiveTenantParam, serviceInstanceUUID);
      
      if (json[0].mode == "WINDOW") {
        window.open(json[0].url,'_blank');
      } else if (json[0].mode == "IFRAME") {
    	  $(".js_iframe_tabs").removeClass("on");
          $iframe_tab.addClass("on");
    	  $("#maincontent_container").hide();
    	  $("#main").css("width", "100%");
    	  $("#header").css("margin", "auto");
    	  $("#footer").css("width", "100%");
    	  $("#mainmenu_panel").css({
    	    "margin": "auto"
    	  });
    	  $("#manage_resources_container").show();
    	  $("#iframe_spinning_wheel").show();
        $("#manage_resources_iframe").attr("src", json[0].url);
      }
    },
    error: function(XMLHttpResponse) {
      handleError(XMLHttpResponse);
      popUpDialogForAlerts("dialog_info", XMLHttpResponse.responseText);
    },
    complete: function() {
      
    }
  });
}

var showResourcesIFrame = function(event) {
  if(!isDelinquent){
    var serviceInstanceUUID = $(this).attr("id");
    launchMyResourcesWithServiceInstanceUUID(serviceInstanceUUID);
  } else {
    if (showMakePaymentMessage != "") {
      popUpDialogForAlerts("dialog_info", showMakePaymentMessage);
      return;
    }
  }
}


  function arr_diff(a1, a2) {
    var a = [],
      diff = [];
    for (var i = 0; i < a1.length; i++)
      a[a1[i]] = true;
    for (var i = 0; i < a2.length; i++)
      if (a[a2[i]]) delete a[a2[i]];
      else a[a2[i]] = true;
    for (var k in a)
      diff.push(k);
    return diff;
  }

$(".doc_help_link").unbind("click").bind("click", function(e) {
  window.open(help_link_path, '_blank');
});

function launchMyResourcesWithServiceInstanceUUID(serviceInstanceUUID, reload_current_page_to_url) {
	
  if (serviceInstanceUUID != null) {
    
    var failureHandler = function(XMLHttpResponse) {
      popUpDialogForAlerts("dialog_info", g_dictionary.error_cloud_service_down);
    };
    
    var successHandler = function() {
      window.location = "/portal/portal/connector/csinstances?tenant=" + effectiveTenantParam +
      "&showIframe=true&serviceInstanceUUID=" + serviceInstanceUUID;
    };
    
    $.ajax({
      url: "/portal/portal/manage_resource/get_resource_views",
      dataType: "json",
      async: false,
      data: {
        serviceInstanceUUID: serviceInstanceUUID,
        tenant: effectiveTenantParam
      },

      cache: false,
      success: function(json) {
        if (json[0].mode == "WINDOW") {
          singleSignOn(effectiveTenantParam, serviceInstanceUUID);
          window.open(json[0].url,'_blank');
          if(reload_current_page_to_url!=null){
        	  window.location = reload_current_page_to_url;
          }
        } else if (json[0].mode == "IFRAME") {
          getStatus(serviceInstanceUUID, successHandler, failureHandler);
        }
      },
      error: function(XMLHttpResponse) {
        handleError(XMLHttpResponse);
        failureHandler(XMLHttpResponse);
      },
      complete: function() {
        
      }
    });

  } else {
    window.location = "/portal/portal/connector/csinstances?tenant=" + effectiveTenantParam;
  }
}

function swap_name_order_tab_index(first_name_css_class, last_name_css_class) {
  if (first_name_css_class != null && last_name_css_class != null) {
    var $first_name_input = $("." + first_name_css_class).find("input");
    var $last_name_input = $("." + last_name_css_class).find("input");
    var first_name_tabindex = $first_name_input.attr("tabindex");
    var last_name_tabindex = $last_name_input.attr("tabindex");
      
    if (($("." + first_name_css_class).css("display") == "table-footer-group" && $("." + last_name_css_class).css("display") == "table-header-group") || ($("." + first_name_css_class).css("float") == "right" && $("." + last_name_css_class).css("float") == "left") ) {
      $first_name_input.attr("tabindex", last_name_tabindex);
      $last_name_input.attr("tabindex", first_name_tabindex);
    }
  }
}

$(".js_close_parent").live("click",function(e){
	$(this).parent().hide();
});

function hide_iframe_loading(){
  $("#iframe_spinning_wheel").hide();
}

function getStatus(serviceInstanceUuid, successCallBack, failureCallBack, completeCallBack) {
  
  if(typeof(completeCallBack) != "function") {
    completeCallBack = function() {};
  }
  
  $.ajax({
    type : "GET",
    url : "/portal/portal/connector/status?id=" + serviceInstanceUuid,
    async : true,
    dataType : 'json',
    success : function(isActive) {
      if (isActive && typeof (successCallBack) == "function") {
        successCallBack();
      } else if(typeof (failureCallBack) == "function") {
        failureCallBack();
      }
    },
    error : function(error) {
      if (typeof (failureCallBack) == "function") {
        failureCallBack(error);
      }
    },
    complete : completeCallBack
  });
}

function updateTopMessagePanel(message, status){
  var $topMessagePanel = $("#top_message_panel"); 
  if (status=="success") {
    $topMessagePanel.find("#status_icon").removeClass("erroricon").addClass("successicon");
    $topMessagePanel.removeClass("error").addClass("success").show();
  } else if (status=="error") {
    $topMessagePanel.find("#status_icon").removeClass("successicon").addClass("erroricon");
    $topMessagePanel.removeClass("success").addClass("error").show();
  }
  $topMessagePanel.find("#msg").text(message);
}

function exitChannelBrandingPreviewMode(current){
	var anonymous_view = false;
	var reload_path = window.location.pathname;
	if((typeof current !="undefined") && (current !=null) && (current.data("view_mode")=="anonymous")){
		reload_path='/portal/portal';
	}
	$.ajax({
	    type : "GET",
	    url : "/portal/portal/exitpreviewmode",
	    async : true,
	    dataType : 'json',
	    success : function(json) {
	    	window.location = reload_path;
	    },
	    error : function(error) {
	      //TODO:Handle error here
	    }
	  });
}

$("#exit_preview_mode_link").live("click", function(event) {
	exitChannelBrandingPreviewMode($(this));
  });
window.CPBM = (function () {
  function CPBM (arg) {
       
  }
   
  var CPBM = {
      parseJsonSafely: function (response) {
        var jsonObject = null;
        try {
          jsonObject = $.parseJSON(response);
        } catch (error) {
        }
        if(!jsonObject || typeof jsonObject ==='undefined'){
          jsonObject = null;
        }
        return jsonObject;
      }, 
      showErrorWithPopover: function (targetElement, responseText, customStyle) {
    	  var errorObject = this.parseJsonSafely(responseText);
    	  
    	  var message = errorObject.message;
    	  var detailedMessage = errorObject.detailedMessage;
    	  
    	  if(message == null){
    		 message = g_dictionary.error_cloud_service_down;
    	  }
    	  
    	  if(detailedMessage == null && responseText.indexOf('errortext') != -1) {
    		  for(var response in errorObject){
     			 if(errorObject[response].errortext != null) {
     				 detailedMessage = errorObject[response].errortext;
     			 }
     		 } 
    	  }
    	  
    	  var errorMessageTemplate = $('#errorMessageTemplate').clone(true);
    	  errorMessageTemplate.find('#messageSpan').text(message);
    	  if(detailedMessage !== undefined && detailedMessage !== '') {
    		  var additionDetailsSpan = errorMessageTemplate.find('#additionalMessageSpan'); 
    		  additionDetailsSpan.find('#detailedMessageAnchor').html(g_dictionary.errorDetails);
    		  additionDetailsSpan.show();
    	  }
    	  if(customStyle !== undefined) {
    		  errorMessageTemplate.css(customStyle);
    	  }
    	  targetElement.html(errorMessageTemplate.show());
    	  if(detailedMessage !== undefined) {
    		  targetElement.find('#detailedMessageAnchor').popover({
		        trigger: "hover",
		        html: true,
		        content: detailedMessage
		      });
    	  }
      },
      getErrorMessageObject: function (message) {
    	  return "{\"message\": \""+message+"\"}";
      }
  };
  return CPBM;
}());


function reloadCurrentPage(){
	location.reload(true);
}

function isValidNonNegativeNo(value) {
  return value != "" && !isNaN(value) && Number(value) >= 0 && Number(value) <= 99999999.9999;
}

function isValidCurrencyPrecision(value) {

  var pattern = new RegExp("/^(?:\\d*\\.\\d*\\|\\d+)$/"); 

  if (!pattern.test(value)) {
    var sval = value;
    var sdecimalDigit = sval.split(".");
    if (sdecimalDigit.length == 2) {
      // have one decimal
      if (sdecimalDigit[1].length > currPrecision) {
        // more characters allowed precision
        return false;
      }
      return true;
    } else if (sdecimalDigit.length > 2) {
      // more then one decimals
      return false;
    }
    // without decimal 
    return true;
  } else {
    // not a valid digit pattern
    return false;
  }
  return true;
}

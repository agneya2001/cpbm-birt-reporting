/*
*  Copyright © 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/
$(document).ready(function() {


  /**
   * Creates new catalog row
   */
  $.createNewCatalog = function(jsonResponse) {

    if (jsonResponse == null) {
      popUpDialogForAlerts("dialog_info", i18n.errors.catalog.createNewCatalog);
    } else {
      $("#addnewcatalogDiv").html("");
      var rowClass = "db_gridbox_rows even";
      var count = $(".countDiv").attr("id");
      var size = Number(count.substr(5));
      var selected = "";
      if (size == 0) {
        selected = "selected";
      }
      if (size % 2 == 0) {
        rowClass = "db_gridbox_rows odd " + selected;
      } else {
        rowClass = "db_gridbox_rows even " + selected;
      }
      var content = "";
      content = content + "<div class='" + rowClass + "' onclick='viewCatalog(this)' id='row" + jsonResponse.id +
        "'>";
      content = content + "<div class='db_gridbox_columns' style='width:20%;'>";
      content = content + "<div class='db_gridbox_celltitles'>";
      content = content + jsonResponse.id;
      content = content + "</div>";
      content = content + "</div>";
      content = content + "<div class='db_gridbox_columns' style='width:30%;'>";
      content = content + "<div class='db_gridbox_celltitles'>";
      content = content + jsonResponse.name;
      content = content + "</div>";
      content = content + "</div>";
      content = content + "<div class='db_gridbox_columns' style='width:49%;'>";
      content = content + "<div class='db_gridbox_celltitles'>";
      if (jsonResponse.description == null || jsonResponse.description == "") {
        content = content + "";
      } else {
        content = content + jsonResponse.description;
      }
      content = content + "</div>";
      content = content + "</div>";
      content = content + "</div>";
      var oldContent = $("#cataloggridcontent").html();
      oldContent = oldContent + content;
      $("#cataloggridcontent").html(oldContent);
      if (size == 0) {
        $.viewCatalogDetails(jsonResponse);
      }
      size = size + 1;
      $(".countDiv").attr("id", "count" + size);

    }

  };


  /**
   * View Catalog details
   */
  $.viewCatalogDetails = function(jsonResponse) {
    var content = "";
    content = content + "<div class='main_detailsbox' ><div class='main_details_titlebox'>";
    content = content + "<h2>Details</h2></div>";
    content = content + "<div class='main_details_contentbox'><div class='main_detailsistbox'>";

    content = content + "<div class='db_gridbox_rows detailsodd'><div class='db_gridbox_columns' style='width:20%;'>";
    content = content + "<div class='db_gridbox_celltitles details'><strong>Name</strong></div></div>";
    content = content +
      "<div class='db_gridbox_columns' style='width:75%;'><div class='db_gridbox_celltitles details'>";
    content = content + jsonResponse.name + "</div></div></div>";

    content = content + "<div class='db_gridbox_rows detailsodd'><div class='db_gridbox_columns' style='width:20%;'>";
    content = content + "<div class=db_gridbox_celltitles details><strong>code</strong></div></div>";
    content = content +
      " <div class='db_gridbox_columns' style='width:75%;'><div class='db_gridbox_celltitles details'>";

    if (jsonResponse.code == null || jsonResponse.code == "") {
      content = content + "";
    } else {
      content = content + jsonResponse.code;
    }
    content = content + "</div></div></div>";

    content = content + "<div class='db_gridbox_rows detailsodd'><div class='db_gridbox_columns' style='width:20%;'>";
    content = content + "<div class=db_gridbox_celltitles details><strong>Description</strong></div></div>";
    content = content +
      " <div class='db_gridbox_columns' style='width:75%;'><div class='db_gridbox_celltitles details'>";

    if (jsonResponse.description == null || jsonResponse.description == "") {
      content = content + "";
    } else {
      content = content + jsonResponse.description;
    }

    content = content + "</div></div></div>";

    content = content + "<div class='db_gridbox_rows detailsodd'><div class='db_gridbox_columns' style='width:20%;'>";
    content = content + "<div class=db_gridbox_celltitles details><strong>Channel</strong></div></div>";
    content = content +
      " <div class='db_gridbox_columns' style='width:75%;'><div class='db_gridbox_celltitles details'>";

    if (jsonResponse.channels == null || jsonResponse.channels == "") {
      content = content + "";
    } else {
      for (var i in jsonResponse.channels) {
        var channel = jsonResponse.channels[i];
        content = content + channel.name + "<p>";
      }
    }
    content = content + "</div></div></div>";

    content = content +
      "<div class='maindetails_footerlinksbox'><p><a href='javascript:void(0);' onclick='editCatalogGet(this)' class='editCatalog' id='edit" +
      jsonResponse.id + "'>" + i18n.label.catalog.Edit + "</a> <span > |</span> </p>";
    content = content + "<p><a href=" + productBundlesUrl + "/" + jsonResponse.id + "/listbundles >" + i18n.label.catalog
      .manage_bundle + "</a><span > |</span></p>";
    content = content + "<p><a href=" + productBundlesUrl + "/" + jsonResponse.id + "/manageurc >" + i18n.label.catalog
      .urc_title + "</a></p>";
    content = content + "</div></div>";
    $("#editcatalogDiv").html("");
    $("#viewcatalogDiv").html(content);
  };
  /**
   * Validate catalog form
   */

  $("#catalogForm").validate({
    //debug : true,
    success: "valid",
    ignoreTitle: true,
    rules: {
      "catalog.name": {
        required: true
      },
      "currencies": {
        required: function() {
          var editflag = $("#editcatalogflag");
          if (editflag != null && editflag.val() == 'edit') {
            return false;
          } else {
            return true;
          }
        }
      },
      "catalog.code": {
        required: true,
        noSpacesAllowed: true,
        xRemote: {
          condition: function() {
            return $("#catalog_code").val() != $("#catalog\\.code").val();
          },
          url: '/portal/portal/products/validateCode',
          async: false
        }
      }
    },
    messages: {
      "catalog.name": {
        required: i18n.errors.catalog.name
      },
      "currencies": {
        required: i18n.errors.catalog.currency
      },
      "catalog.code": {
        required: i18n.errors.catalog.code,
        noSpacesAllowed: i18n.errors.catalog.catlogcodevalid,
        xRemote: i18n.errors.common.codeNotUnique
      }
    },
    errorPlacement: function(error, element) {
      var name = element.attr('id');
      name = name.replace(".", "\\.");
      if (error.html() != "") {
        error.appendTo("#" + name + "Error");
      }
    }
  });

  $("#addnewcatalogcancel").click(function() {
    $("#addnewcatalogDiv").html("");
  });



  /**
   * edit page Cancel action
   */
  $("#editcatalogcancel").click(function() {
    $("#viewcatalogDiv").html(currentDetailView);
    $("#editcatalogDiv").html("");
  });


  /**
   * Update catalog row
   */
  $.editCatalog = function(jsonResponse) {

    if (jsonResponse == null) {
      popUpDialogForAlerts("dialog_info", i18n.errors.catalog.editCatalog);
    } else {
      $("#editcatalogDiv").html("");

      var content = "";
      content = content + "<div class='db_gridbox_columns' style='width:20%;'>";
      content = content + "<div class='db_gridbox_celltitles'>";
      content = content + jsonResponse.id;
      content = content + "</div>";
      content = content + "</div>";
      content = content + "<div class='db_gridbox_columns' style='width:30%;'>";
      content = content + "<div class='db_gridbox_celltitles'>";
      content = content + jsonResponse.name;
      content = content + "</div>";
      content = content + "</div>";
      content = content + "<div class='db_gridbox_columns' style='width:49%;'>";
      content = content + "<div class='db_gridbox_celltitles'>";
      content = content + jsonResponse.description;
      content = content + "</div>";
      content = content + "</div>";
      $("#row" + jsonResponse.id).html(content);
      $("#row" + jsonResponse.id).click();

    }

  };



});

/**
 * View catalog details
 * @param current
 * @return
 */

function viewCatalog(current) {
  var divId = $(current).attr('id');
  var ID = divId.substr(3);
  resetGridRowStyle();
  var cls = $(current).attr('class');
  cls = cls + " selected";
  $(current).attr('class', cls);
  var url = productsUrl + "viewcatalog";
  $.ajax({
    type: "GET",
    url: url,
    data: {
      Id: ID
    },
    dataType: "html",
    success: function(html) {
      $("#viewcatalogDiv").html("");
      $("#editcatalogDiv").html("");
      $("#viewcatalogDiv").html(html);
    },
    error: function() {
      //need to handle TO-DO
    }
  });
}

function addNewCatalogGet() {
  var actionurl = productsUrl + "createcatalog";
  $.ajax({
    type: "GET",
    url: actionurl,
    dataType: "html",
    success: function(html) {
      $("#editcatalogcancel").click();
      $("#addnewcatalogDiv").html(html);
    },
    error: function() {
      $(".addnewcatalog").unbind('click');
    }
  });
}

/**
 * Add new catalog(POST)
 * @param event
 * @param form
 * @return
 */

function addNewCatalog(event, form) {
  if (event.preventDefault) {
    event.preventDefault();
  } else {
    event.returnValue = false;
  }
  if ($("#catalogForm").valid()) {
    $.ajax({
      type: "POST",
      url: $(form).attr('action'),
      data: $(form).serialize(),
      dataType: "json",
      success: function(jsonResponse) {
        $.createNewCatalog(jsonResponse);
        if ($("#cataloggridcontentDiv").length > 0) {
          $("#cataloggridcontentDiv").hide();
        }
      },
      error: function(XMLHttpRequest) {
        if (XMLHttpRequest.status === AJAX_FORM_VALIDATION_FAILED_CODE) {
          displayAjaxFormError(XMLHttpRequest, "catalogForm", "main_addnew_formbox_errormsg");
        } else if (XMLHttpRequest.status === CODE_NOT_UNIQUE_ERROR_CODE) {
          popUpDialogForAlerts("dialog_info", i18n.errors.common.codeNotUnique);
        } else {
          popUpDialogForAlerts("dialog_info", i18n.errors.catalog.addNewCatalog);
        }
        $("addcatalog").val(i18n.label.catalog.addCatalog);
      }
    });

  }
}

var currentDetailView;

/**
 * Edit catalog (GET)
 */

function editCatalogGet(current) {
  var divId = $(current).attr('id');
  var ID = divId.substr(4);
  var actionurl = productsUrl + "editcatalog";
  $.ajax({
    type: "GET",
    url: actionurl,
    data: {
      Id: ID
    },
    dataType: "html",
    success: function(html) {
      currentDetailView = $("#viewcatalogDiv").html();
      $("#viewcatalogDiv").html("");
      $("#addnewcatalogcancel").click();
      $("#editcatalogDiv").html("");
      $("#editcatalogDiv").html(html);
    },
    error: function() {
      $(".editCatalog").unbind('click');
    }
  });
}

/**
 * Edit catalog POST
 * @param event
 * @param form
 * @return
 */

function editCatalog(event, form) {
  if (event.preventDefault) {
    event.preventDefault();
  } else {
    event.returnValue = false;
  }
  if ($("#catalogForm").valid()) {
    $("#editcatalog").attr("value", i18n.label.catalog.saving);
    $.ajax({
      type: "POST",
      url: $(form).attr('action'),
      data: $(form).serialize(),
      dataType: "json",
      success: function(jsonResponse) {
        $.editCatalog(jsonResponse);
      }

    });

  }
}


/**
 * Remove catalog (GET)
 */

function removeCatalog(current) {
  var r = confirm(i18n.confirm.catalog.removeCatalog);
  if (r == false) {
    return false;
  }
  var divId = $(current).attr('id');
  var ID = divId.substr(6);
  var actionurl = productsUrl + "removecatalog";
  $.ajax({
    type: "GET",
    url: actionurl,
    data: {
      Id: ID
    },
    dataType: "text/hmtl",
    success: function(html) {
      if (html == "success") {
        $("#viewcatalogDiv").html("");
        $("#editcatalogDiv").html("");
        $("#row" + ID).remove();
        var count = $(".countDiv").attr("id");
        var size = Number(count.substr(5));
        size = size - 1;
        $(".countDiv").attr("id", "count" + size);
        resetGridRowStyle();
      } else {
        popUpDialogForAlerts("dialog_info", i18n.errors.catalog.removeCatalog);
      }

    },
    error: function() {
      //need to handle
    }
  });
}

/**
 * Reset data row style
 * @return
 */

function resetGridRowStyle() {
  var isEven = true;
  $(".db_gridbox_rows").each(function() {
    var calssvar = $(this).attr('class');
    if (calssvar.search("header") == -1) {
      if (isEven == true) {
        $(this).attr('class', 'db_gridbox_rows even');
        isEven = false;
      } else {
        $(this).attr('class', 'db_gridbox_rows odd');
        isEven = true;
      }
    }

  });
}

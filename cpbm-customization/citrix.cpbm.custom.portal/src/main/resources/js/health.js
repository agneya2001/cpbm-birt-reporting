/*
*  Copyright © 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/
$(document).ready(function() {
  var selectedCategory = $("#selectedCategory").val();

  if (typeof selectedCategory == 'undefined' || selectedCategory == null || selectedCategory == "") {
    selectedCategory = $('#service_category_list_container li').first().attr("category");
  }
  populateServiceInstances(selectedCategory, effectiveTenantParam, refreshStatus);

  var serviceCategoryListItems = $("#service_category_list_container li");
  serviceCategoryListItems.each(function() {
    $(this).unbind("click").bind("click", function() {
      $(".categorytabs").removeClass("current user");
      $(this).removeClass().addClass("categorytabs current user");
      selectedCategory = $(this).attr("category");

      populateServiceInstances(selectedCategory, effectiveTenantParam, refreshStatus, refreshStatus);
    });
  });

  activateThirdMenuItem("13_health_status_tab");
  initDialog("addNewStatusDiv", 700);

  $(function() {
    $('#startDateField').datetimepicker({
      duration: '',
      showSecond: true,
      stepHour: 1,
      stepMinute: 1,
      minDate: new Date(),
      dateFormat: g_dictionary.friendlyDate,
      timeFormat: 'hh:mm:ss',
      beforeShow: function(dateText, inst) {
        $("#ui-datepicker-div").css("z-index", 3003);
        if ($("#endDateField").val() != '') {
          $(this).datetimepicker("option", "maxDate", new Date(Date.parse($("#endDateField").val())));
        }
      },
      onClose: function(dateText, inst) {
        if (dateText && $('#endDateField').val() == '') {
          $('#endDateField').datetimepicker('option', 'minDate', new Date(Date.parse($(this).val())));
        }
        $("#healthStatusForm").validate().element("#startDateField");
      },
      onSelect: function(selectedDateTime) {
        if (selectedDateTime && $("#endDateField").val() == '') {
          $('#endDateField').datetimepicker('option', 'minDate', new Date(Date.parse($(this).val())));
        }
      }
    });
  });

  $(function() {
    $('#endDateField').datetimepicker({
      duration: '',
      showSecond: true,
      stepHour: 1,
      stepMinute: 1,
      minDate: new Date(),
      dateFormat: g_dictionary.friendlyDate,
      timeFormat: 'hh:mm:ss',
      beforeShow: function(dateText, inst) {
        $("#ui-datepicker-div").css("z-index", 3003);
        if ($("#startDateField").val() != '') {
          $(this).datetimepicker("option", "minDate", new Date(Date.parse($("#startDateField").val())));
        }
      },
      onClose: function(dateText, inst) {
        if (dateText && $('#startDateField').val() == '') {
          $('#startDateField').datetimepicker('option', 'maxDate', new Date(Date.parse($(this).val())));
        }
        $("#healthStatusForm").validate().element("#endDateField");
      },
      onSelect: function(selectedDateTime) {
        if (selectedDateTime && $("#startDateField").val() == '') {
          $('#startDateField').datetimepicker('option', 'maxDate', new Date(Date.parse($(this).val())));
        }
      }
    });
  });

  $("#ui-datepicker-div").css("z-index", "1003");
  $("#ui-timepicker-div").css("z-index", "1003");

  $.validator.addMethod("endDt", function(value, element) {
    var startDate = $("#startDateField").val();
    return Date.parse(startDate) < Date.parse(value);
  }, i18n.errors.endDateField.validate);

  $("#healthStatusForm").validate({
    //debug : true,
    success: "valid",
    ignoreTitle: true,
    rules: {
      "serviceNotification.notificationType": {
        required: true
      },
      "serviceNotification.subject": {
        required: true,
        maxlength: 255
      },
      "serviceNotification.description": {
        required: true,
        maxlength: 1024
      },
      "serviceInstanceUUID": {
        required: true
      },
      "serviceNotification.plannedStart": {
        required: true,
        date: true
      },
      "serviceNotification.plannedEnd": {
        required: true,
        date: true,
        endDt: true
      }
    },
    messages: {
      "serviceNotification.notificationType": {
        required: i18n.errors.notificationType.required
      },
      "serviceNotification.subject": {
        required: i18n.errors.subject.required
      },
      "serviceNotification.description": {
        required: i18n.errors.description.required
      },
      "serviceInstanceUUID": {
        required: i18n.errors.serviceInstance.required
      },
      "serviceNotification.plannedStart": {
        required: i18n.errors.startDateField.required,
        date: i18n.errors.startDateField.date
      },
      "serviceNotification.plannedEnd": {
        required: i18n.errors.endDateField.required,
        date: i18n.errors.endDateField.date
      }
    },
    errorPlacement: function(error, element) {
      var name = element.attr('id');
      name = ReplaceAll(name, ".", "\\.");
      if (error.html() != "") {
        error.appendTo("#" + name + "Error");
      }
    }
  });


  $(".viewDetails").click(function() {
    var id = $(this).attr("name");
    $("#desc" + id).slideToggle();
  });
  $(".viewStatusDetails").click(function() {
    var id = $(this).attr("name");
    $("#statusDesc" + id).slideToggle();
  });

  $("#addnewstatuscancel").click(function() {
    $("#addNewStatusDiv").html("");
  });

  $("#startDate").click(function() {
    $('#startDateField').datepicker('show');
  });
  $("#endDate").click(function() {
    $('#endDateField').datepicker('show');
  });

  currentPage = parseInt(currentPage);
  perPageValue = parseInt(perPageValue);
  dateStatusLen = parseInt(dateStatusLen);
  var selectedServiceInstanceUUID = $("#selectedCloudServiceInstance").val();

  function nextClick(event) {

    $("#click_next").unbind("click", nextClick);
    $("#click_next").addClass("nonactive");

    currentPage = currentPage + 1;

    $("#click_previous").unbind("click").bind("click", previousClick);
    $("#click_previous").removeClass("nonactive");

    var url = healthUrl.substring(0, healthUrl.length - 1) + "?page=" + currentPage;

    if (selectedServiceInstanceUUID != undefined && selectedServiceInstanceUUID != "") {
      url = url + "&serviceinstanceuuid=" + selectedServiceInstanceUUID;
    }

    window.location = url;
  }

  function previousClick(event) {
    $("#click_previous").unbind("click", previousClick);
    $("#click_previous").addClass("nonactive");

    currentPage = currentPage - 1;

    $("#click_next").removeClass("nonactive");
    $("#click_next").unbind("click").bind("click", nextClick);

    var url = healthUrl.substring(0, healthUrl.length - 1) + "?page=" + currentPage;

    if (selectedServiceInstanceUUID != undefined && selectedServiceInstanceUUID != "") {
      url = url + "&serviceinstanceuuid=" + selectedServiceInstanceUUID;
    }

    window.location = url;
  }

  if (currentPage > 1) {
    $("#click_previous").removeClass("nonactive");
    $("#click_previous").unbind("click").bind("click", previousClick);
  }

  if (dateStatusLen < perPageValue) {
    $("#click_next").unbind("click");
    $("#click_next").addClass("nonactive");

  } else if (dateStatusLen == perPageValue) {

    if (currentPage < totalpages) {

      $("#click_next").removeClass("nonactive");
      $("#click_next").unbind("click").bind("click", nextClick);
    } else {
      $("#click_next").unbind("click");
      $("#click_next").addClass("nonactive");
    }
  }

  $("#grid_row_container").find(".widget_navigationlist").unbind("click").bind("click", function() {
    viewStatusDetails($(this));
  });

  $("#grid_row_container").find(".widget_navigationlist").unbind("mouseover").bind("mouseover", function() {
    onHealthMouseover($(this));
  });

  $("#grid_row_container").find(".widget_navigationlist").unbind("mouseout").bind("mouseout", function() {
    onHealthMouseout($(this));
  });

  /*if($("#grid_row_container").children().length > 0 && $("#grid_row_container").children().first().attr("id") != "non_list"){
	  $("#grid_row_container").children().first().click();
  }*/

});

var month_names = new Array("January", "February", "March",
  "April", "May", "June", "July", "August", "September",
  "October", "November", "December");
/**
 * Creates new Status row
 */

function addNewStatusUI(jsonResponse) {


  if (jsonResponse == null) {
    popUpDialogForAlerts("dialog_info", i18n.errors.status.failedCreateStatus);
  } else {
    $("#addNewStatusDiv").html("");
    var selectedZone = $("#selectedZone").val();
    if (selectedZone == "" || selectedZone == jsonResponse.zone) {
      if (jsonResponse.notificationType != 'MAINTENANCE') {
        var rowClass = "db_gridbox_rows dotted_bottom";
        //					var count =$(".countDiv").attr("id");
        //					var size = Number(count.substr(5));
        //					if(size%2==0){
        //						rowClass="db_gridbox_rows odd ";
        //					}else{
        //						rowClass = "db_gridbox_rows even ";
        //					}
        var content = "<div class='details_commentslist' id='row" + jsonResponse.id +
          "'><div class='details_commentslist_content'>";
        content = content + "<div class='details_commentslist_content_left'>";
        if (jsonResponse.notificationType == 'RESOLUTION') {
          image = "<img src='/portal/images/normal_listicon.png' ></img>";
          content = content + "<div class='details_comments_statusicons normal'></div>";
        }
        if (jsonResponse.notificationType == 'ISSUE') {
          image = "<img src='/portal/images/perfissue_listicon.png' ></img>";
          content = content + "<div class='details_comments_statusicons peformanceissue'></div>";
        }
        if (jsonResponse.notificationType == 'DISRUPTION') {
          image = "<img src='/portal/images/disruption_listicon.png' ></img>";
          content = content + "<div class='details_comments_statusicons disruption'></div>";
        }

        content = content + "</div>";
        content = content + "<div class='details_commentslist_content_right'>";
        content = content + "<div class='details_commentslist_authorbox'>";
        var date = new Date(jsonResponse.recordedOn);
        var milliseconds = date.getTime();
        date = new Date(milliseconds + (date.getTimezoneOffset() * 60 * 1000));
        var day = date.getDate();
        var month = date.getMonth();
        var year = date.getFullYear();
        var hours = date.getHours();
        var mins = date.getMinutes();

        var am_pm = '';
        if (hours > 12) {
          hours = hours - 12;
          am_pm = 'PM';
        } else {
          am_pm = 'AM';
        }
        content = content + "<p>" + jsonResponse.recordedOn + "</p>";
        content = content + "</div>";
        content = content + "<div class='details_commentslist_description'>";
        content = content + "<p>" + jsonResponse.description + "</p>";
        content = content + "</div>";
        content = content + "<div class='details_commentslist_morebox'>";
        content = content + "</div>" + "</div>" + "</div>" + "</div>";

        var oldContent = $("#statusgridcontent").html();
        oldContent = content + oldContent;
        $("#statusgridcontent").html(oldContent);
        //					size=size+1;
        //					$(".countDiv").attr("id","count"+size);
        var temp = "";
        if (date.getDate() < 10) {
          temp = temp + "0" + date.getDate();
        } else {
          temp = temp + date.getDate();
        }
        if ((date.getMonth() + 1) < 10) {
          temp = temp + "0" + (date.getMonth() + 1);
        } else {
          temp = temp + (date.getMonth() + 1);
        }
        temp = temp + date.getFullYear();
        $("#healthimage" + temp).html(image);
        $("#healthstatus" + temp).html(jsonResponse.status);
      } else {
        window.location = "/portal/portal/health";
      }
    }
  }
};

/**
 * Update product row
 */

function editStatusUI(jsonResponse) {

  if (jsonResponse == null) {
    popUpDialogForAlerts("dialog_info", i18n.errors.status.failedEditStatu);
  } else {
    $("#editStatusDetailsDiv").html("");
    var content = "<div class='details_commentslist_content'>";
    content = content + "<div class='details_commentslist_content_left'>";
    if (jsonResponse.notificationType == 'RESOLUTION') {
      image = "<img src='/portal/images/comments_normalicon.gif' ></img>";
      content = content + "<div class='details_comments_statusicons normal'></div>";
    }
    if (jsonResponse.notificationType == 'ISSUE') {
      image = "<img src='/portal/images/comments_perfissueicon.gif' ></img>";
      content = content + "<div class='details_comments_statusicons peformanceissue'></div>";
    }
    if (jsonResponse.notificationType == 'DISRUPTION') {
      image = "<img src='/portal/images/comments_disruptionicon.gif' ></img>";
      content = content + "<div class='details_comments_statusicons disruption'></div>";
    }

    content = content + "</div>";
    content = content + "<div class='details_commentslist_content_right'>";
    content = content + "<div class='details_commentslist_authorbox'>";
    var date = new Date(jsonResponse.recordedOn);
    var day = date.getDay();
    var month = date.getMonth();
    var year = date.getFullYear();
    var hours = date.getHours();
    var mins = date.getMinutes();

    var am_pm = '';
    if (hours > 12) {
      hours = hours - 12;
      am_pm = 'PM';
    } else {
      am_pm = 'AM';
    }
    content = content + "<p>" + day + ' ' + month_names[month] + ' ' + year + ' ' + hours + ':' + mins + ' ' + am_pm +
      "</p>";
    content = content + "</div>";
    content = content + "<div class='details_commentslist_description'>";
    content = content + "<p>" + jsonResponse.description + "</p>";
    content = content + "</div>";
    content = content + "<div class='details_commentslist_morebox'>";
    content = content + "<p><a href='javascript:void(0);' onclick='editStatusDetailsGet(this)' id=edit" + jsonResponse.id +
      ">Edit</a></p>";
    content = content + "</div>" + "</div>" + "</div>";
    $("#row" + jsonResponse.id).html(content);

  }

}
/**
 * Reset data row style
 * @return
 */

function resetGridRowStyle() {
  $(".widget_navigationlist").each(function() {
    $(this).removeClass("selected active");
  });
}

var refreshStatus = function refreshServiceInstanceStatus(serviceInstanceUuid, tenantParam) {
  window.location = "/portal/portal/health?serviceinstanceuuid=" + serviceInstanceUuid;
};

/**
 * View Status details
 * @param current
 * @return
 */

function viewStatusDetails(current) {
  var divId = $(current).attr('id');
  var date = divId.substr(3);
  var selectedServiceInstanceUUID = $("#selectedServiceInstance").find(".downarrow").attr("id");
  if (typeof selectedServiceInstanceUUID == 'undefined' || selectedServiceInstanceUUID == null ||
    selectedServiceInstanceUUID == "") {
    selectedServiceInstanceUUID = "";
  }
  var dateFormat = $("#dateFormat").val();
  resetGridRowStyle();

  $(current).addClass("selected active");
  var cls = $(current).attr('class');
  cls = cls + " selected";
  $(current).attr('class', cls);

  var url = healthUrl + "show_status_details";
  $.ajax({
    type: "GET",
    url: url,
    data: {
      date: date,
      serviceinstanceuuid: selectedServiceInstanceUUID,
      dateformat: dateFormat
    },
    dataType: "html",
    success: function(html) {
      $("#viewStatusDetailsDiv").html(html);
    },
    error: function() {
      //need to handle TO-DO
    }
  });
}

function addNewStatusGet() {
  $("#addNewStatusDiv").html("");
  var selectedServiceInstanceUUID = $("#selectedServiceInstance").find(".downarrow").attr("id");
  var actionurl;
  if (typeof selectedServiceInstanceUUID == 'undefined' || selectedServiceInstanceUUID == null ||
    selectedServiceInstanceUUID == "") {
    actionurl = healthUrl + "add_status";
  } else {
    actionurl = healthUrl + "add_status?serviceinstanceuuid=" + selectedServiceInstanceUUID;
  }
  $.ajax({
    type: "GET",
    url: actionurl,
    dataType: "html",
    success: function(html) {
      $("#addNewStatusDiv").html(html);
      var $thisPanel = $("#addNewStatusDiv");
      $thisPanel.dialog({
        height: 400,
        width: 700
      });
      $thisPanel.dialog('option', 'buttons', {

      }).dialog("open");
    },
    error: function() {
      $(".addNewStatus").unbind('click');
    }
  });
}


/**
 * Add new Status(POST)
 *
 * @param event
 * @param form
 * @return
 */

function addNewStatus(event, form) {
  if (event.preventDefault) {
    event.preventDefault();
  } else {
    event.returnValue = false;
  }
  var isvalid = $("#healthStatusForm").valid();
  $("#dateFormat").attr("value", g_dictionary.dateFormatForServer);
  if (isvalid) {
    var selectedServiceInstanceUUID = $("#selectedServiceInstance").find(".downarrow").attr("id");
    var actionurl;
    if (typeof selectedServiceInstanceUUID == 'undefined' || selectedServiceInstanceUUID == null ||
      selectedServiceInstanceUUID == "") {
      actionurl = "/portal/portal/health";
    } else {
      actionurl = "/portal/portal/health?serviceinstanceuuid=" + selectedServiceInstanceUUID;
    }
    $.ajax({
      type: "POST",
      url: $(form).attr('action'),
      data: $(form).serialize(),
      dataType: "json",
      success: function(jsonResponse) {
        window.location = actionurl;
      }
    });

  }
}

/**
 * Add new Status(POST)
 * @param event
 * @param form
 * @return
 */

function editStatus(event, form) {
  if (event.preventDefault) {
    event.preventDefault();
  } else {
    event.returnValue = false;
  }
  var isvalid = $("#healthStatusForm").valid();
  if (isvalid) {
    $.ajax({
      type: "POST",
      url: $(form).attr('action'),
      data: $(form).serialize(),
      dataType: "json",
      success: function(jsonResponse) {
        editStatusUI(jsonResponse);
      }
    });

  }
}

/**
 * Reset data row style for maintenance
 * @return
 */

function resetGridRowStyleMaintenance() {
  if ($(".maintenance_gridrow").size() == 0) {
    noMaintenence = '<div class="db_gridbox_rows"><div style="width: 99%;" class="db_gridbox_columns">';
    noMaintenence = noMaintenence + '<div class="db_gridbox_celltitles">' + i18n.label.NoMaintenanceSchdule +
      '</div></div></div>';
    $('.maintenance_rightbox_content').html(noMaintenence);
  } else {
    var isOdd = true;
    $(".maintenance_gridrow").each(function() {
      if (isOdd == true) {
        $(this).attr('class', 'maintenance_gridrow odd');
        isOdd = false;
      } else {
        $(this).attr('class', 'maintenance_gridrow even');
        isOdd = true;
      }
    });
  }
}

/**
 * Remove Status (GET)
 */

function removeMaintenanceSchdule(current) {
  var r = confirm(i18n.confirm.MaintenanceSchdule.remove);
  if (r == false) {
    return false;
  }
  var divId = $(current).attr('id');
  var ID = divId.substr(6);
  var actionurl = healthUrl + ID + "/remove_status";
  $.ajax({
    type: "GET",
    url: actionurl,
    dataType: "html",
    success: function(html) {
      $("#smrow" + ID).remove();
      resetGridRowStyleMaintenance();
    },
    error: function() {
      popUpDialogForAlerts("dialog_info", i18n.errors.MaintenanceSchdule.remove);
    }
  });
}


/**
 * Add new maintenance(POST)
 *
 * @param event
 * @param form
 * @return
 */

function saveSchedMaintenance(event, form, notificationId) {
  var isvalid = $("#healthStatusForm").valid();
  var display = ($('#serviceNotification\\.zone').val() == $('#selectedZone').val());
  if (isvalid) {
    if (notificationId > 0) {
      $.ajax({
        type: "POST",
        url: $(form).attr('action'),
        data: $(form).serialize(),
        dataType: "html",
        success: function(html) {
          $("#addNewStatusDiv").html("");
          $("#smrow" + notificationId).replaceWith(html);
          resetGridRowStyleMaintenance();
        }
      });
    } else {
      $.ajax({
        type: "POST",
        url: $(form).attr('action'),
        data: $(form).serialize(),
        dataType: "html",
        success: function(html) {
          $("#addNewStatusDiv").html("");
          if (display == true) { // if current zone is same as the zone for the maintenance scheduled.
            $('.maintenance_rightbox_content').prepend(html);
            $("#noSchedulId").html("");
            resetGridRowStyleMaintenance();
          }
        }
      });
    }
  }
}

function onHealthMouseover(current) {
  if ($(current).hasClass('active')) return
  $(current).find("#info_bubble").show();
  return false;
}

function onHealthMouseout(current) {
  $(current).find("#info_bubble").hide();
  return false;
}

function closehealthDialog(current) {
  var $thisPanel = $("#addNewStatusDiv");
  $thisPanel.dialog("close");
}

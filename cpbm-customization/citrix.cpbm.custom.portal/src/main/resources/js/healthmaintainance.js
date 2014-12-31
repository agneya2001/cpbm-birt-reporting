/*
*  Copyright � 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/
$(document).ready(function() {
  var selectedCategory = $("#selectedCategory").val();

  if (typeof selectedCategory == 'undefined' || selectedCategory == null || selectedCategory == "") {
    selectedCategory = $('#service_category_list_container li').first().attr("category");
  }
  populateServiceInstances(selectedCategory, effectiveTenantParam, refreshMaintainance);

  var serviceCategoryListItems = $("#service_category_list_container li");
  serviceCategoryListItems.each(function() {
    $(this).unbind("click").bind("click", function() {
      $(".categorytabs").removeClass("current user");
      $(this).removeClass().addClass("categorytabs current user");
      selectedCategory = $(this).attr("category");

      populateServiceInstances(selectedCategory, effectiveTenantParam, refreshMaintainance, refreshMaintainance);
    });
  });

  activateThirdMenuItem("13_health_scheduled_maintainence_tab");
  initDialog("addNewStatusDiv", 700);

  $("#addNewStatusDiv").bind('dialogclose', function(event) {
    $("#startDateField").datepicker('hide');
    $("#endDateField").datepicker('hide');
  });

  $(function() {
    $('#startDateField').datetimepicker({
      duration: '',
      showSecond: true,
      stepHour: 1,
      stepMinute: 1,
      changeYear: true,
      minDate: new Date(),
      dateFormat: g_dictionary.friendlyDate,
      timeFormat: 'hh:mm:ss',
      beforeShow: function(dateText, inst) {
        $("#ui-datepicker-div").css("z-index", 3003);
        if ($("#endDateField").val() != '') {	
          $(this).datetimepicker("option", "maxDate", $("#endDateField").datetimepicker('getDate'));
        }
      },
      onClose: function(dateText, inst) {
        if (dateText && $('#endDateField').val() == '') {
          $('#endDateField').datetimepicker('option', 'minDate', $.datepicker.parseDateTime(g_dictionary.friendlyDate, 'hh:mm:ss', $(this).val()));
        }
        $("#healthStatusForm").validate().element("#startDateField");
        if($('#endDateField').val() != ''){
        	$("#healthStatusForm").validate().element("#endDateField");
        }
      },
      onSelect: function(selectedDateTime) {
        if (selectedDateTime && $("#endDateField").val() == '') {
          $('#endDateField').datetimepicker('option', 'minDate', $.datepicker.parseDateTime(g_dictionary.friendlyDate, 'hh:mm:ss', $(this).val()));
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
      changeYear: true,
      minDate: new Date(),
      dateFormat: g_dictionary.friendlyDate,
      timeFormat: 'hh:mm:ss',
      beforeShow: function(dateText, inst) {
        $("#ui-datepicker-div").css("z-index", 3003);
        if ($("#startDateField").val() != '') {
          $(this).datetimepicker("option", "minDate", $("#startDateField").datetimepicker('getDate'));
        }
      },
      onClose: function(dateText, inst) {
        if (dateText && $('#startDateField').val() == '') {
          $('#startDateField').datetimepicker('option', 'maxDate', $.datepicker.parseDateTime(g_dictionary.friendlyDate, 'hh:mm:ss', $(this).val()));
        }
        $("#healthStatusForm").validate().element("#endDateField");
      },
      onSelect: function(selectedDateTime) {
        if (selectedDateTime && $("#startDateField").val() == '') {
          $('#startDateField').datetimepicker('option', 'maxDate', $.datepicker.parseDateTime(g_dictionary.friendlyDate, 'hh:mm:ss', $(this).val()));
        }
      }
    });
  });

  $("#ui-datepicker-div").css("z-index", "3003");
  $("#ui-timepicker-div").css("z-index", "3003");

  $.validator.addMethod("endDt", function(value, element) {
    var startDate = $("#startDateField").val();
    if(startDate == null || startDate == '') {
    	return true;
    }
    var startTime = $.datepicker.parseDateTime(g_dictionary.friendlyDate, "hh:mm:ss", startDate);
    var endTime = $.datepicker.parseDateTime(g_dictionary.friendlyDate, "hh:mm:ss", value);
    return startTime.getTime() < endTime.getTime();
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
        required: true
      },
      "serviceNotification.description": {
        required: true
      },
      "serviceInstanceUUID": {
        required: true
      },
      "serviceNotification.plannedStart": {
        required: true
      },
      "serviceNotification.plannedEnd": {
        required: true,
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
      "startDateField": {
        required: i18n.errors.startDateField.required,
        date: i18n.errors.startDateField.date
      },
      "endDateField": {
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
  maintainanceListLen = parseInt(maintainanceListLen);

  function nextClick(event) {

    $("#click_next").unbind("click", nextClick);
    $("#click_next").addClass("nonactive");

    currentPage = currentPage + 1;

    $("#click_previous").unbind("click").bind("click", previousClick);
    $("#click_previous").removeClass("nonactive");

    window.location = healthUrl + 'health_maintainance?zone=' + $('#selectedZone').val() + "&currentpage=" +
      currentPage;
  }

  function previousClick(event) {
    $("#click_previous").unbind("click", previousClick);
    $("#click_previous").addClass("nonactive");

    currentPage = currentPage - 1;

    $("#click_next").removeClass("nonactive");
    $("#click_next").unbind("click").bind("click", nextClick);

    window.location = healthUrl + 'health_maintainance?zone=' + $('#selectedZone').val() + "&currentpage=" +
      currentPage;
  }

  if (currentPage > 1) {
    $("#click_previous").removeClass("nonactive");
    $("#click_previous").unbind("click").bind("click", previousClick);
  }

  if (maintainanceListLen < perPageValue) {
    $("#click_next").unbind("click");
    $("#click_next").addClass("nonactive");

  } else if (maintainanceListLen == perPageValue) {

    if (currentPage < totalpages) {

      $("#click_next").removeClass("nonactive");
      $("#click_next").unbind("click").bind("click", nextClick);
    } else {
      $("#click_next").unbind("click");
      $("#click_next").addClass("nonactive");
    }
  }

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

var refreshMaintainance = function refreshHealthMaintainance(serviceInstanceUuid, tenantParam) {
  window.location = "/portal/portal/health/health_maintainance?serviceinstanceuuid=" + serviceInstanceUuid;
};

/**
 * Reset data row style
 * @return
 */

function resetGridRowStyle() {
  $(".widget_navigationlist").each(function() {
    $(this).removeClass("selected active");
  });
}

function addSchedMaintenanceGet() {
  $("#addNewStatusDiv").html("");
  var selectedServiceInstanceUUID = $("#selectedServiceInstance").find(".downarrow").attr("id");
  var actionurl;
  if (typeof selectedServiceInstanceUUID == 'undefined' || selectedServiceInstanceUUID == null ||
    selectedServiceInstanceUUID == "") {
    actionurl = healthUrl + "add_scheduled_maintenance";
  } else {
    actionurl = healthUrl + "add_scheduled_maintenance?serviceinstanceuuid=" + selectedServiceInstanceUUID;
  }
  $.ajax({
    type: "GET",
    url: actionurl,
    dataType: "html",
    success: function(html) {
      $("#addNewStatusDiv").html(html);
      var $thisPanel = $("#addNewStatusDiv");
      $thisPanel.dialog({
        height: 100,
        width: 550
      });
      $thisPanel.dialog('option', 'buttons', {

      }).dialog("open");

    },
    error: function() {
      $(".addNewStatus").unbind('click');
    }
  });
}

function editSchedMaintenanceGet(current) {
  var divId = $(current).attr('id');
  var ID = divId.substr(4);
  $("#addNewStatusDiv").html("");
  var selectedServiceInstanceUUID = $("#selectedServiceInstance").find(".downarrow").attr("id");
  var actionurl;
  if (typeof selectedServiceInstanceUUID == 'undefined' || selectedServiceInstanceUUID == null ||
    selectedServiceInstanceUUID == "") {
    actionurl = healthUrl + "add_scheduled_maintenance";
  } else {
    actionurl = healthUrl + "add_scheduled_maintenance?serviceinstanceuuid=" + selectedServiceInstanceUUID;
  }
  $.ajax({
    type: "GET",
    url: actionurl,
    data: {
      id: ID
    },
    dataType: "html",
    success: function(html) {
      $("#addNewStatusDiv").html(html);
      var $thisPanel = $("#addNewStatusDiv");
      $thisPanel.dialog({
        height: 100,
        width: 950
      });
      $thisPanel.dialog('option', 'buttons', {}).dialog("open");
    },
    error: function() {
      $(".addNewStatus").unbind('click');
    }
  });
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
  var selectedServiceInstanceUUID = $("#selectedServiceInstance").find(".downarrow").attr("id");
  var redirecturl;
  if (typeof selectedServiceInstanceUUID == 'undefined' || selectedServiceInstanceUUID == null ||
    selectedServiceInstanceUUID == "") {
    redirecturl = healthUrl + 'health_maintainance';
  } else {
    redirecturl = healthUrl + 'health_maintainance?serviceinstanceuuid=' + selectedServiceInstanceUUID;
  }
  $.ajax({
    type: "GET",
    url: actionurl,
    dataType: "html",
    success: function(html) {
      window.location = redirecturl;
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
  var selectedServiceInstanceUUID = $("#selectedServiceInstance").find(".downarrow").attr("id");
  var redirecturl;
  if (typeof selectedServiceInstanceUUID == 'undefined' || selectedServiceInstanceUUID == null ||
    selectedServiceInstanceUUID == "") {
    redirecturl = healthUrl + 'health_maintainance';
  } else {
    redirecturl = healthUrl + 'health_maintainance?serviceinstanceuuid=' + selectedServiceInstanceUUID;
  }

  if (isvalid) {
	  $(form).find("#startDateString").val($(form).find("#startDateField").val());
	  $(form).find("#endDateString").val($(form).find("#endDateField").val());
    if (notificationId > 0) {
      $.ajax({
        type: "POST",
        url: $(form).attr('action'),
        data: $(form).serialize(),
        dataType: "html",
        success: function(html) {
          if (newSchedule == "false") {
            //Changed this to reload bcoz ordering might get changed if user changes the date
            window.location = redirecturl;
          } else {
            window.location = redirecturl;

          }
        }
      });
    } else {
      $.ajax({
        type: "POST",
        url: $(form).attr('action'),
        data: $(form).serialize(),
        dataType: "html",
        success: function(html) {
          if (newSchedule == "false") {
            //Changed this to reload bcoz ordering might get changed if user changes the date
            window.location = redirecturl;
          } else {
            window.location = redirecturl;

          }
        }
      });
    }
  }
}

function closemaintDialog(current) {
  var $thisPanel = $("#addNewStatusDiv");
  $("#startDateField").datepicker('hide');
  $("#endDateField").datepicker('hide');
  $thisPanel.dialog("close");
}

function viewMaintainanceDetails(current) {
  var divId = $(current).attr('id');
  var id = divId.substr(5);
  var selectedServiceInstanceUUID = $("#selectedServiceInstance").find(".downarrow").attr("id");
  if (typeof selectedServiceInstanceUUID == 'undefined' || selectedServiceInstanceUUID == null ||
    selectedServiceInstanceUUID == "") {
    selectedServiceInstanceUUID = "";
  }

  resetGridRowStyle();
  $(current).addClass("selected active");
  var cls = $(current).attr('class');
  cls = cls + " selected";
  $(current).attr('class', cls);
  var url = healthUrl + "maintenance_view";
  $.ajax({
    type: "GET",
    url: url,
    data: {
      serviceinstanceuuid: selectedServiceInstanceUUID,
      id: id
    },
    dataType: "html",
    success: function(html) {
      $("#viewScheduledMaintainanceDiv").html(html);
      bindActionMenuContainers();
    },
    error: function() {
      //need to handle TO-DO
    }
  });
}

function viewFirstMaintainanceDetails(id) {
  var selectedServiceInstanceUUID = $("#selectedServiceInstance").find(".downarrow").attr("id");
  if (typeof selectedServiceInstanceUUID == 'undefined' || selectedServiceInstanceUUID == null ||
    selectedServiceInstanceUUID == "") {
    selectedServiceInstanceUUID = "";
  }

  var url = healthUrl + "maintenance_view";
  $.ajax({
    type: "GET",
    url: url,
    data: {
      serviceinstanceuuid: selectedServiceInstanceUUID,
      id: id
    },
    dataType: "html",
    success: function(html) {
      $("#viewScheduledMaintainanceDiv").html(html);
      bindActionMenuContainers();
    },
    error: function() {
      //need to handle TO-DO
    }
  });
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

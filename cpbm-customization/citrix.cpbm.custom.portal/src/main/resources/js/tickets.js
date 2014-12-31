/*
*  Copyright � 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/
$(document).ready(function() {

  // If the request is coming from Dashboard Submit Ticket link then new
  // ticket popup should come up
  if (typeof showNewTicket !="undefined" && showNewTicket == "true") {
    showNewTicketDiv();
  }

  initDialog("addticketDiv", 700);
  $("#createNewTicketcancel").click(function() {
    $("#createNewTicketDiv").html("");
  });

  $("#ticketForm").validate({
    // debug : true,

    rules: {
      "ticket.description": {
        required: true,
        maxlength: 32000
      },
      "ticket.subject": {
        required: true,
        maxlength: 255
      },
      "ticket.status": {
        required: true
      }
    },
    messages: {
      "ticket.description": {
        required: i18n.errors.ticketDescription
      },
      "ticket.subject": {
        required: i18n.errors.ticketTitle
      },
      "ticket.status": {
        required: i18n.errors.ticketStatus
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


  $("#ticketFilterByStatus").change(
    function() {
      var filter = $("#ticketFilterByStatus").val();
      window.location = "/portal/portal/support/tickets?statusFilter=" + filter + "&tenant=" + $("#tenantParam").val(); //+ "&sortType=" + sortType + "&sortColumn=" + sortColumn;
    });



  $("#comment_ticket_link").live("click", function() {
    initDialog("ticketCommentDiv", 430, false);
    var $thisPanel = $("#ticketCommentDiv");
    $thisPanel.dialog({
      height: 100,
      width: 430
    });
    $thisPanel.dialog('option', 'buttons', {
      "Submit": function() {
        var $commentform = $thisPanel.find("#createTicketCommentForm");
        if ($commentform.valid()) {
          $("#top_actions").find("#spinning_wheel").show();

          $.ajax({
            type: "POST",
            //url : $("#ticketCommentDiv").attr("action"),
            url: "/portal/portal/support/tickets/" + $commentform.find('#ticketId').val() + "/comment",
            data: $commentform.serialize(),
            dataType: "html",
            cache: false,
            success: function(response) {
              if (response == "success") {
                viewTicket($("li[id^='ticket'].selected.tickets"), null, "comment");
              } else {
                $("#top_actions").find("#spinning_wheel").hide();
                popUpDialogForAlerts("dialog_info", i18n.alerts.postCommentFailure + ": " + $commentform.find('#ticketId').val());
              }

            },
            error: function(html) {
              $("#top_actions").find("#spinning_wheel").hide();
              popUpDialogForAlerts("dialog_info", i18n.alerts.postCommentFailure + ": " + $commentform.find('#ticketId').val());
            }
          });
          $(this).dialog("destroy");
          $thisPanel.remove();
        }
      },
      "Cancel": function() {
        $(this).dialog("close");
        $(this).dialog("destroy");
      }
    });
    dialogButtonsLocalizer($thisPanel, {
      'Submit': g_dictionary.dialogSubmit,
      'Cancel': g_dictionary.dialogCancel
    });
    $thisPanel.dialog("open");

  });

  $("#edit_ticket_link").live("click", function() {
    initDialog("editTicketDiv", 700, false);
    var $thisPanel = $("#editTicketDiv");
    $thisPanel.dialog({
      height: 310,
      width: 700
    });
    $thisPanel.dialog('option', 'buttons', {
      "Submit": function() {
        if ($("#ticketForm").valid()) {
          $(this).dialog("close");
          $("#top_actions").find("#spinning_wheel").show();
          $.ajax({
            type: "POST",
            url: "/portal/portal/support/tickets/edit",
            data: $("#ticketForm").serialize(),
            dataType: "text",
            cache: false,
            success: function(html) {
              viewTicket($("li[id^='ticket'].selected.tickets"));
            },
            error: function(html) {
              $("#top_actions").find("#spinning_wheel").hide();
              popUpDialogForAlerts("dialog_info", i18n.alerts.editFailure);
            }
          });
          $(this).dialog("destroy");
        }
      },
      "Cancel": function() {
        $(this).dialog("close");
        $(this).dialog("destroy");
      }
    });
    dialogButtonsLocalizer($thisPanel, {
      'Submit': g_dictionary.dialogSubmit,
      'Cancel': g_dictionary.dialogCancel
    });
    $thisPanel.dialog("open");
  });




  initDialog("dialog_confirmation", 350, false);
  var topActionMap = {

    closeTicket: {
      label: dictionary.closeTicket,
      inProcessText: dictionary.closingTicket,

      type: "POST",
      //data : {caseId:$('#case_id').val()},        
      dataType: "html",
      afterActionSeccessFn: function(result) {
        window.location.reload(true);

        //var caseNumber = $("#ticketDetailsCaseNumber").html().trim();
        //$("#closeTicket").hide();
        //$("#ticketDetailsStatus").html("Closed");
        //$("#createTicketCommentForm").hide();
        //$("#gbt_status"+caseNumber).html("Closed");
      }
    }

  };

  function getConfirmationDialogButtons(command) {

    var buttonCallBacks = {};
    var actionMapItem;

    if (command == "closeTicket") {

      actionMapItem = topActionMap.closeTicket;
    }

    buttonCallBacks[dictionary.lightboxbuttonconfirm] = function() {
      $(this).dialog("close");


      var apiCommand;
      if (command == "closeTicket") {

        apiCommand = "/portal/portal/support/tickets/close?caseNumber=" + $('#case_number').val();
      }
      doActionButton(actionMapItem, apiCommand);

    };

    buttonCallBacks[dictionary.lightboxbuttoncancel] = function() {
      $(this).dialog("close");
    };

    return buttonCallBacks;
  }

  $(".close_ticket_link").live("click", function(event) {
    $("#dialog_confirmation").text(dictionary.lightboxCloseTicket).dialog('option', 'buttons',
      getConfirmationDialogButtons("closeTicket")).dialog("open");
  });



  viewTicket($("li[id^='ticket'].selected.tickets"));


});

function refreshDivs() {
  $(".editTicketDiv").each(function() {
    $(this).html("");
  });
  $("#createNewTicketDiv").html("");
}

function refreshList() {
  $(".gridrow").each(function() {
    $(this).hide();
  });
  var status = document.getElementById("status");
  var value = status.options[status.selectedIndex].value;
  if (value == 'All') {
    $(".gridrow").each(function() {
      $(this).show();
    });
  }
  if (value == 'Closed') {
    $("." + value).each(function() {
      $(this).show();
    });
  }
  if (value == 'Open') {
    $(".gridrow").each(function() {
      $(this).show();
    });
    $(".Closed").each(function() {
      $(this).hide();
    });
  }
}

function blockUI() {
  $('#blockScreen').css({
    opacity: 0.7,
    'width': $(document).width(),
    'height': $(document).height()
  });
  $('#blockScreen').show();
  $('#spinner').show();
}

function unBlockUI() {
  $('#blockScreen').hide();
  $('#spinner').hide();
}

function showNewTicketDiv() {
  var actionurl = "/portal/portal/support/tickets/create";
  $.ajax({
    type: "GET",
    url: actionurl,
    data: {},
    dataType: "html",
    cache: false,
    success: function(html) {
      $("#addticketDiv").html(html);
      var $thisPanel = $("#addticketDiv");
      $thisPanel.dialog({
        height: 200,
        width: 700
      });
      $thisPanel.dialog('option', 'buttons', {
        "Submit": function() {
          if ($("#createTicketForm").valid()) {
            $(this).dialog("close");
            $("#grid_row_container").prepend(
              '<li class="widget_navigationlist loading" id="ticket_add_loading"><span class="navicon loading"></span><div class="widget_navtitlebox"><span class="title">' +
              'Adding Ticket' + '</span></div></li>');
            $.ajax({
              type: "POST",
              url: "/portal/portal/support/tickets/create?tenant=" + $("#tenantParam").val(),
              data: $("#createTicketForm").serialize(),
              dataType: "json",
              cache: false,
              success: function(ticket) {
                if (ticket.caseNumber != null) {
                  $("#ticket_add_loading").remove();
                  window.location = "/portal/portal/support/tickets?tenant=" + $("#tenantParam").val();

                } else {
                  $("#top_message_panel").find("#status_icon").removeClass("successicon").addClass("erroricon");
                  $("#top_message_panel").find("#msg").text(i18n.alerts.createFailure);
                  $("#top_message_panel").removeClass("success").addClass("error").show();
                  $("#ticket_add_loading").remove();

                }
              },
              error: function(html) {
                $("#top_message_panel").find("#status_icon").removeClass("successicon").addClass("erroricon");
                $("#top_message_panel").find("#msg").text(i18n.alerts.createFailure);
                $("#top_message_panel").removeClass("success").addClass("error").show();
                $("#ticket_add_loading").remove();
              }
            });
          }
        },
        "Cancel": function() {
          $(this).dialog("close");
        }
      });
      dialogButtonsLocalizer($thisPanel, {
        'Submit': g_dictionary.dialogSubmit,
        'Cancel': g_dictionary.dialogCancel
      });
      $thisPanel.dialog("open");
      var firstInput = $("#createTicketForm").find(
        'input[type=text],input[type=password],input[type=radio],input[type=checkbox],textarea,select').filter(
        ':visible:first');
      if (firstInput != null) {
        firstInput.focus();
      }
    },
    error: function() {
      $("#addticketDiv").html("");
      $("#addticket").unbind('click');
    }
  });
}

function postNewTicket(event, form) {
  if (event.preventDefault) {
    event.preventDefault();
  } else {
    event.returnValue = false;
  }

  if ($("#ticketForm").valid()) {
    //blockUI();
    $(".submit_button_loader").show();
    $.ajax({
      type: "POST",
      url: "/portal/portal/support/tickets/create?tenant=" + $("#tenantParam").val(),
      data: $(form).serialize(),
      dataType: "json",
      cache: false,
      success: function(ticket) {
        if (ticket.caseNumber != null) {
          window.location = window.location.href.replace("&showNewTicket=1", "")
          if ($("#ticketgridcontentDiv").length > 0) {
            $("#ticketgridcontentDiv").hide();
          }
        } else {
          $("#createNewTicketDiv").html(i18n.alerts.createFailure);
        }
      },
      error: function(html) {
        popUpDialogForAlerts("dialog_info", i18n.alerts.createFailure);
      }
    });
  }
}


function clearDetailsPanel() {
  $detailsContent = $("#viewTicketDiv");
  $detailsContent.find("#ticket_number").text("");
  $detailsContent.find("#ticket_title").text("");
  $detailsContent.find("#ticket_status").text("");
  $detailsContent.find("#ticket_description").text("");
  $detailsContent.find("#ticket_customer").text("");
  $detailsContent.find("#ticket_created_by").text("");
  $detailsContent.find("#ticket_created_at").text("");
  $detailsContent.find("#ticket_updated_at").text("");
  $("#top_action_menu").hide();


}

function viewTicket(current, ticketOwner, action_type) {
  if (ticketOwner == null) {
    ticketOwner = $(current).attr('username_attr');
  }
  var divId = $(current).attr('id');
  if (divId == null)
    return;
  resetGridRowStyle();
  $(current).addClass('selected active');
  var tktNumber = divId.substring(6);
  $("#top_actions").find("#spinning_wheel").show();
  $.ajax({
    type: "GET",
    cache: false,
    url: "/portal/portal/support/tickets/view?tenant=" + $("#tenantParam").val(),
    data: {
      ticketNumber: tktNumber,
    },
    dataType: "html",
    success: function(html) {
      $("#viewTicketDiv").html('');
      $("#editTicketDiv").remove();
      $("#viewTicketDiv").html(html);
     
      $("#ticket" + tktNumber).find("#info_status").text($("#viewTicketDiv").find("#ticket_status").text());
      $("#ticket" + tktNumber).find("#info_ticket_updated_at").text($("#viewTicketDiv").find("#ticket_updated_at").text());
      $("#ticket" + tktNumber).find("#displaytext_nav").text($("#viewTicketDiv").find("#ticket_title").text());
      
      var ticketStatus = $("#viewTicketDiv").find("#ticket_status").text();
      var statusIcon = "ticket_" + ticketStatus.toLowerCase();
      $("#ticket" + tktNumber).find("#status_icon").removeClass().addClass("navicon " + statusIcon);
      
      bindActionMenuContainers();
      $("#top_actions").find("#spinning_wheel").hide();
      $("#tab_comments").bind("click", function(event) {
        $('#tab_details').removeClass('active').addClass("nonactive");
        $('#tab_comments').removeClass('nonactive').addClass("active");
        $('#details_content').hide();
        $('#comments_content').show();
      });
      $("#tab_details").bind("click", function(event) {
        $('#tab_comments').removeClass('active').addClass("nonactive");

        $('#tab_details').removeClass('nonactive').addClass("active");
        $('#comments_content').hide();

        $('#details_content').show();
      });
      if (action_type == "comment")
        $("#tab_comments").click();

    },
    error: function(html) {
      $("#top_actions").find("#spinning_wheel").hide();
      popUpDialogForAlerts("dialog_info", i18n.alerts.fetchFailure + " " + tktNumber);
    }
  });
}

function resetGridRowStyle() {
  $(".widget_navigationlist").each(function() {
    $(this).removeClass("selected active");

  });
}


function editTicket(event, form) {
  if (event.preventDefault) {
    event.preventDefault();
  } else {
    event.returnValue = false;
  }
  if ($("#ticketForm").valid()) {
    $.ajax({
      type: "POST",
      url: $(form).attr("action"),
      data: $(form).serialize(),
      dataType: "html",
      cache: false,
      success: function(html) {
        $("#viewTicketDiv").html(html);
      },
      error: function(html) {
        popUpDialogForAlerts("dialog_info", i18n.alerts.editFailure);
      }
    });

  }
}

function closeTicket(caseId) {
  var r = confirm(i18n.confirm.closeTicket);
  var caseNumber = $("#ticketDetailsCaseNumber").html().trim();
  if (r == true) {
    $.ajax({
      type: "POST",
      url: "/portal/portal/support/tickets/close",
      data: {
        caseId: caseId
      },
      dataType: "html",
      cache: false,
      success: function(result) {
        if (result == "Success") {
          $("#closeTicket").hide();
          $("#ticketDetailsStatus").html("Closed");
          $("#createTicketCommentForm").hide();
          $("#gbt_status" + caseNumber).html("Closed");
        } else {
          popUpDialogForAlerts("dialog_info", i18n.alerts.closeFailure + " " + caseNumber);
        }
      },
      error: function(html) {
        popUpDialogForAlerts("dialog_info", i18n.alerts.closeFailure + " " + caseNumber);
      }
    });
  }
}

function sortTickets(obj) {
  var filter = $("#ticketFilterByStatus").val();
  if (filter === undefined || filter === '') {
    filter = 'All';
  }
  var sortColumnId = $(obj).attr('id');
  var sortType = null;
  if ($(obj).hasClass("ASC")) {
    sortType = "DESC";
  } else if ($(obj).hasClass("DESC")) {
    sortType = "ASC";
  } else {
    sortType = "DESC";
  }
  window.location = "/portal/portal/support/tickets?statusFilter=" + filter + "&tenant=" + $("#tenantParam").val() +
    "&sortType=" + sortType + "&sortColumn=" + sortColumnId;
}

function getMoreTickets() {
  $("#showMoreLoading").show();
  var queryLocator = $("#queryLocator").val();
  var filter = $("#ticketFilterByStatus").val();
  if (filter === undefined || filter === '') {
    filter = 'All';
  }
  $.ajax({
    type: "GET",
    url: "/portal/portal/support/tickets/page",
    data: {
      statusFilter: filter,
      tenant: $("#tenantParam").val(),
      sortType: sortType,
      sortColumn: sortColumn,
      queryLocator: queryLocator
    },
    dataType: "html",
    cache: false,
    success: function(result) {
      $("#showMore").remove();
      $("#ticketgridcontent").append(result);
    },
    error: function(html) {
      popUpDialogForAlerts("dialog_info", i18n.alerts.showMoreFailure);
    },
    complete: function() {
      $("#showMoreLoading").hide();
      resetGridRowStyle();
    }
  });
}

function showInfoBubble(current) {
  if ($(current).hasClass('active')) return
  $(current).find("#info_bubble").show();
//  alert($(current).find("#info_status").text());
  return false;
};

function hideInfoBubble(current) {
  $(current).find("#info_bubble").hide();
  return false;
};


function nextClick() {
  var $currentPage = $('#current_page').val();
  //window.location = "/portal/portal/tenants/alerts?tenant="+$('#tenantId').val()+"&page="+(parseInt($currentPage)+1);
  var filter = $("#ticketFilterByStatus").val();
  window.location = "/portal/portal/support/tickets?statusFilter=" + filter + "&tenant=" + $('#tenantParam').val() +
    "&page=" + (parseInt($currentPage) + 1);
}

function previousClick() {
  var $currentPage = $('#current_page').val();
  //window.location = "/portal/portal/tenants/alerts?tenant="+$('#tenantId').val()+"&page="+(parseInt($currentPage)-1);
  var filter = $("#ticketFilterByStatus").val();
  window.location = "/portal/portal/support/tickets?statusFilter=" + filter + "&tenant=" + $('#tenantParam').val() +
    "&page=" + (parseInt($currentPage) - 1);
}

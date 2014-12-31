/*
*  Copyright © 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/
$(document).ready(function() {
  /* approval popup related js start*/
  $(".taskPopup").live("click", function() {
    var taskId = $(this).attr('id').replace("taskPopup", "");
    var taskurl = "/portal/portal/tasks/approval-task/" + taskId;
    var approvalTaskGet = $.ajax({
      type: "GET",
      url: taskurl,
      dataType: "html"
    });

    var $approvalDialog = $("#approvalTask_panel");
    if (!$approvalDialog.is(':data(dialog)')) {
      $approvalDialog.dialog({
        width: 900,
        modal: true,
        resizable: false,
        autoOpen: false
      });
    }
    $("#approval_task_close,#okbutton").live("click", function() {
      $approvalDialog.dialog("close");
      location.reload(); 
    });
    approvalTaskGet.done(function(html) {
      $approvalDialog.html(html);
      $approvalDialog.dialog("open");
    });
    approvalTaskGet.fail(function(XMLHttprequest) {
      $approvalDialog.html(XMLHttprequest.responseText);
      $approvalDialog.dialog("open");
    });
  });
  $("#approvalTask_panel .dialog_formcontent.wizard #buttons input").live("click", function() {
    var taskState = $(this).attr('id').replace("approval_task_", "");
    var url = $("#approval_task_form").attr("action");
    var taskId = $("#approval_task_uuid").val();
    var taskMemo = $("#approval_task_form #memo").val();
    $("#spinning_wheel_rhs").show();
    var approvalTaskPost = $.ajax({
      type: "POST",
      url: url,
      data: {
        uuid: taskId,
        state: taskState,
        memo: taskMemo
      },
      dataType: "html"
    });
    approvalTaskPost.done(function(state) {
      $("#taskState").html(state);
        $("a#taskPopup" + taskId).removeClass("taskPopup");
        $("a#taskPopup" + taskId).addClass("deleted");
        $("#taskDiv").hide();
      
      
      $("#viewTaskDiv .taskState").html(state);

      $(".j_actionForm").hide();
      $(".j_success").show();

      if (taskState == "success") {
        $(".widgetwizard_successbox .j_approved").show();
      } else {
        $(".widgetwizard_successbox .j_rejected").show();
      }

    });
    approvalTaskPost.fail(function(XMLHttprequest) {
      $("#memo").parent().removeClass("nonmandatory_wrapper").addClass("mandatory_wrapper");
      $("#memo_errormsg").html(XMLHttprequest.responseText);
    });
    approvalTaskPost.always(function(XMLHttprequest) {
      $("#spinning_wheel_rhs").hide();
    });

  });
  /* approval popup related js end*/

  /*all tasks page js start*/
  $('#selectedTaskFilter').change(function() {
    var filter = $(this).find('option:selected').val();
    window.location.href = "/portal/portal/tasks/?tenant=" + tenantParam + "&filter=" + filter;
  });
  var $listTaskElem = $('#all_tasks ul.widget_navigationlist li.widget_navigationlist');
  $listTaskElem.mouseover(function() {
    $(this).find(".widget_info_popover").show();
  });
  $listTaskElem.mouseout(function() {
    $(this).find(".widget_info_popover").hide();
  });
  $listTaskElem.click(function() {
    var $thistaskElem = $(this);
    var taskId = $(this).attr('id').replace("row", "");
    var taskurl = "/portal/portal/tasks/" + taskId + "/?tenant=" + tenantParam;
    var taskGet = $.ajax({
      type: "GET",
      url: taskurl,
      dataType: "html"
    });
    taskGet.done(function(html) {
      $("#viewTaskDiv.widget_rightpanel").html(html);
      $listTaskElem.removeClass("selected active");
      $thistaskElem.addClass("selected active");
    });

    taskGet.fail(function(XMLHttprequest) {
      $("#viewTaskDiv.widget_rightpanel").html(XMLHttprequest.responseText);
    });
  });
  var $nextButton = $("#all_tasks #click_next").not(".nonactive");
  var $prevButton = $("#all_tasks #click_previous").not(".nonactive");
  $nextButton.click(function() {
    var filter = $("#selectedTaskFilter").find('option:selected').val();
    window.location.href = "/portal/portal/tasks/?tenant=" + tenantParam + "&filter=" + filter + "&page=" +
      nextPage;
  });
  $prevButton.click(function() {
    var filter = $("#selectedTaskFilter").find('option:selected').val();
    window.location.href = "/portal/portal/tasks/?tenant=" + tenantParam + "&filter=" + filter + "&page=" +
      prevPage;
  });
  /*all tasks page js end*/
});

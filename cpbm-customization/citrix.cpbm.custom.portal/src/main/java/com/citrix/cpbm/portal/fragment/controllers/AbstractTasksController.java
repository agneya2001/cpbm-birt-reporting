/*
 * Copyright © 2013 Citrix Systems, Inc. You may not use, copy, or modify this file except pursuant to a valid license
 * agreement from Citrix Systems, Inc.
 */
package com.citrix.cpbm.portal.fragment.controllers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.citrix.cpbm.core.workflow.model.Task;
import com.citrix.cpbm.core.workflow.service.TaskService;
import com.vmops.model.Tenant;
import com.vmops.model.User;
import com.vmops.service.exceptions.InvalidAjaxRequestException;
import com.vmops.web.controllers.AbstractAuthenticatedController;
import com.vmops.web.controllers.menu.Page;
import com.vmops.web.interceptors.UserContextInterceptor;

/**
 * @author rajanik
 */
public abstract class AbstractTasksController extends AbstractAuthenticatedController {

  /**
   * Logger.
   */
  private static Logger logger = Logger.getLogger(AbstractTasksController.class);

  @Autowired
  private TaskService taskService;

  @RequestMapping(value = "/", method = RequestMethod.GET)
  public String getTasks(@ModelAttribute("currentTenant") Tenant currentTenant,
      @RequestParam(value = "tenant", required = false) final String tenantParam,
      @RequestParam(value = "filter", defaultValue = "ALL") String filter,
      @RequestParam(value = "page", defaultValue = "1") Integer page, ModelMap map, HttpServletRequest request) {
    logger.debug("###Entering in getTasks(filter) method @GET");
    setPage(map, Page.DASHBOARD_ALL_TASKS);
    Tenant tenant = currentTenant;
    User user = getCurrentUser();
    if ((Boolean) request.getAttribute("isSurrogatedTenant")) {
      tenant = (Tenant) request.getAttribute(UserContextInterceptor.EFFECTIVE_TENANT_KEY);
      user = tenant.getOwner();
      map.addAttribute("showUserProfile", true);
    } else {
      map.addAttribute("showUserProfile", false);
    }
    map.addAttribute("userHasCloudServiceAccount", userService.isUserHasAnyActiveCloudService(user));
    Integer perPage = getDefaultPageSize();

    Task.State[] taskStates;
    if (filter.equalsIgnoreCase("COMPLETED")) {
      // success and error states
      taskStates = new Task.State[] {
          Task.State.FAILURE, Task.State.SUCCESS
      };

    } else if (filter.equalsIgnoreCase("ALL")) {
      // success, error and pending states
      taskStates = new Task.State[] {
          Task.State.FAILURE, Task.State.SUCCESS, Task.State.PENDING
      };
    } else {
      // pending states
      taskStates = new Task.State[] {
        Task.State.PENDING
      };
    }

    List<Task> tasks = taskService.getTasks(tenant, user, taskStates, page, perPage);
    Map<String, String> taskUrlMap = new HashMap<String, String>();
    for (Task task : tasks) {
      taskUrlMap.put(task.getUuid(), taskService.getActionUrl(task));
    }

    int totalCount = taskService.getTasksCount(tenant, user, taskStates);
    setPaginationValues(map, perPage , page, totalCount, null);
    
    map.addAttribute("tasks", tasks);
    map.addAttribute("tasksUrlMap", taskUrlMap);
    map.addAttribute("tenant", tenant);

    map.addAttribute("taskfilters", Arrays.asList("ALL", "PENDING", "COMPLETED"));
    map.addAttribute("currentFilter", filter);

    logger.debug("###Exiting in getTasks(filter) method @GET");
    return "tasks.all";
  }

  @RequestMapping(value = "/{taskUuid}/", method = RequestMethod.GET)
  public String getTask(@PathVariable String taskUuid, @RequestParam(value = "tenant") final String tenantParam,
      ModelMap map) {
    Map<Task, String> taskMap = taskService.getTaskMap(tenantService.get(tenantParam), taskUuid);
    Task task = taskMap.keySet().iterator().next();
    map.addAttribute("task", task);
    map.addAttribute("taskUrl", taskMap.get(task));
    return "task.view";
  }

  @RequestMapping(value = "/approval-task/{taskUuid}", method = RequestMethod.GET)
  public String getApprovalTask(@ModelAttribute("currentLocale") Locale currentLocale, @PathVariable String taskUuid,
      ModelMap map) {
    Task task = taskService.get(taskUuid);
    map.addAttribute("task", task);
    map.addAttribute("transactionType", task.getBusinessTransaction().getClass().getSimpleName());
    String taskDetails = taskService.getDetails(task, currentLocale);
    map.addAttribute("taskDetails", taskDetails);
    return "approval.task";
  }

  @RequestMapping(value = "/approval-task", method = RequestMethod.POST)
  @ResponseBody
  public String actOnApprovalTask(@RequestParam(value = "uuid", required = true) String pendingActionId,
      @RequestParam(value = "state", required = true) String state,
      @RequestParam(value = "memo", required = false) String memo, HttpServletRequest request) {
    logger.debug("### In actOnPendingAction() method starting...");
    Task.State stateChangeTo;
    if (state.equalsIgnoreCase("success")) {
      stateChangeTo = Task.State.SUCCESS;
    } else {
      if (StringUtils.isEmpty(memo)) {
        throw new InvalidAjaxRequestException(messageSource.getMessage("ui.accounts.all.pending.changes.memorequired",
            null,getCurrentUser().getLocale()));
      }
      stateChangeTo = Task.State.FAILURE;
    }
    Task currentTask = taskService.get(pendingActionId);
    Task action = taskService.completeTask(currentTask, stateChangeTo, getCurrentUser(), memo);
    logger.debug("### In actOnPendingAction() method ending.");
    return messageSource.getMessage("ui.task.state." + action.getState(), null, request.getLocale());
  }

}

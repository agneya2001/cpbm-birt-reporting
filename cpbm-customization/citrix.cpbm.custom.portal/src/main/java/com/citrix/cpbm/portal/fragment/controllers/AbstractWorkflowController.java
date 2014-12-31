/*
*  Copyright © 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/
package com.citrix.cpbm.portal.fragment.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.citrix.cpbm.workflow.activity.Activity;
import com.citrix.cpbm.workflow.model.Workflow;
import com.citrix.cpbm.workflow.model.WorkflowActivity;
import com.citrix.cpbm.workflow.service.WorkflowService;
import com.vmops.web.controllers.AbstractAuthenticatedController;

/**
 * @author damodar
 */
public class AbstractWorkflowController extends AbstractAuthenticatedController {

  /**
   * Logger.
   */
  private static Logger logger = Logger.getLogger(AbstractWorkflowController.class);

  @Autowired
  private WorkflowService workflowService;

  @RequestMapping(value = "/{workflowParam}", method = RequestMethod.GET)
  public String show(@PathVariable String workflowParam, ModelMap map) {
    logger.debug("Getting details for the workflow : " + workflowParam);
    Workflow workflow = workflowService.locate(workflowParam);
    Map<Activity.Bucket, List<WorkflowActivity>> bucketMap = new TreeMap<Activity.Bucket, List<WorkflowActivity>>();
    if (workflow.getWorkflowActivities() != null && workflow.getWorkflowActivities().size() > 0) {
      for (WorkflowActivity activity : workflow.getWorkflowActivities()) {
        if (!activity.isNullified()) {
          if (bucketMap.containsKey(activity.getBucket())) {
            bucketMap.get(activity.getBucket()).add(activity);
          } else {
            List<WorkflowActivity> activities = new ArrayList<WorkflowActivity>();
            activities.add(activity);
            bucketMap.put(activity.getBucket(), activities);
          }
        }
      }
    }
    map.put("workflow", workflow);
    map.put("bucketMap", bucketMap);
    logger.debug("Returning details for the workflow : " + workflowParam);
    return "workflow.details.popup";
  }

  @RequestMapping(value = "/{workflowParam}/reset", method = RequestMethod.POST)
  @ResponseBody
  public boolean reset(@PathVariable String workflowParam) {
    logger.debug("### In reSet() method starting...");
    workflowService.reset(workflowParam);
    logger.debug("### In reSet() method ending.");
    return true;
  }
}

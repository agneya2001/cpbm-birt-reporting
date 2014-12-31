/*
 * Copyright © 2013 Citrix Systems, Inc. You may not use, copy, or modify this file except pursuant to a valid license
 * agreement from Citrix Systems, Inc.
 */
package com.citrix.cpbm.portal.fragment.controllers;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.vmops.web.controllers.AbstractAuthenticatedController;

/**
 * The default handler for all 'static' pages, including error, help etc.
 * 
 * @author vijay
 */

public abstract class AbstractStaticPagesController extends AbstractAuthenticatedController {

  private Logger logger = Logger.getLogger(AbstractStaticPagesController.class);

  @RequestMapping(value = {
    "/errors/*"
  })
  public ModelAndView handle(HttpServletRequest request) {
    ModelAndView viewData = new ModelAndView();
    viewData.addObject("uri", request.getRequestURI());
    viewData.addObject("ref", request.getHeader("Referer"));
    return viewData;
  }

  @RequestMapping(value = "/pages/help", method = RequestMethod.GET)
  public String help(ModelMap map) {
    logger.debug("### In help()  start method...");
    map.addAttribute("tenant", getTenant());
    logger.debug("### In help()  end");
    return "main.help";
  }

  @RequestMapping(value = "/pages/faqs", method = RequestMethod.GET)
  public String faqs(ModelMap map) {
    logger.debug("### In StaticPagesController()-> faqs()  start method...");
    map.addAttribute("tenant", getTenant());
    logger.debug("### In faqs()  end");
    return "main.faqs";
  }

  @RequestMapping(value = "/pages/contactUs", method = RequestMethod.GET)
  public String contactUs(ModelMap map) {
    logger.debug("### In contactUs()  start method...");
    map.addAttribute("tenant", getTenant());
    logger.debug("### In contactUs()  end");
    return "main.contactus";
  }

  @RequestMapping(value = "/pages/tnc", method = RequestMethod.GET)
  public String termsAndConditions(ModelMap map) {
    logger.debug("### In termsAndConditions()  start method...");
    map.addAttribute("tenant", getTenant());
    logger.debug("### In termsAndConditions()  end");
    return "main.termsAndConditions";
  }

}

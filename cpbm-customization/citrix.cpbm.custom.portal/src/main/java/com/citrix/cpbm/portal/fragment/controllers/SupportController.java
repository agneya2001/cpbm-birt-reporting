/*
*  Copyright © 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/
package com.citrix.cpbm.portal.fragment.controllers;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.vmops.web.controllers.menu.Page;

/**
 * Handles Support requests.
 * 
 * @author Pallavi
 */
@Controller
@RequestMapping("/support")
public class SupportController extends AbstractSupportController {

private static Logger logger = Logger.getLogger(SupportController.class);

    private final String REPORT_CUSTOM_IAAS="iaas";

        @RequestMapping(value = {"/iaas_reports"}, method = RequestMethod.GET)
        public String faq(ModelMap map, HttpServletRequest request){
                  logger.debug("###Entering in faq() method @GET");

          setCustomPage(map, REPORT_CUSTOM_IAAS);
                  String reportURL = "/birt/index.jsp";

                  map.addAttribute("iframe", reportURL);
                  map.addAttribute("tenant", getTenant());
                  return "support.iaas";
         }

        private void setCustomPage(ModelMap map, String level2Constant) {
           // setPage(map, Page.REPORTS_CUSTOM);
            map.addAttribute(level2Constant, "on");
        }

}

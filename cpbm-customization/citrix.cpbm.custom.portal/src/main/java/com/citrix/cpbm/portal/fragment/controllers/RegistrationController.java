/*
*  Copyright © 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/
package com.citrix.cpbm.portal.fragment.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 * Controller that handles all user pages.
 * 
 * @author vijay
 */
@Controller
@SessionAttributes({
    "registration", "channelParam", "isTrialSignup"
})
public class RegistrationController extends AbstractRegistrationController {

}
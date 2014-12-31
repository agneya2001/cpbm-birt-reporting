/*
 * Copyright ¬© 2013 Citrix Systems, Inc. You may not use, copy, or modify this file except pursuant to a valid license
 * agreement from Citrix Systems, Inc.
 */
package com.citrix.cpbm.portal.fragment.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vmops.internal.service.TicketService.Capability;
import com.vmops.model.Tenant;
import com.vmops.model.Ticket;
import com.vmops.model.Ticket.TicketStatus;
import com.vmops.model.TicketComment;
import com.vmops.model.User;
import com.vmops.service.SupportService;
import com.vmops.service.exceptions.InvalidAjaxRequestException;
import com.vmops.service.exceptions.SupportServiceException;
import com.vmops.service.exceptions.TicketServiceException;
import com.vmops.web.controllers.AbstractAuthenticatedController;
import com.vmops.web.controllers.menu.Page;
import com.vmops.web.forms.TicketCommentForm;
import com.vmops.web.forms.TicketForm;

/**
 * Handles Support requests.
 * 
 * @author Pallavi
 */
/**
 * Handles Support requests.
 * 
 * @author Pallavi
 */

public abstract class AbstractSupportController extends AbstractAuthenticatedController {

  /**
   * Logger.
   */
  private static Logger logger = Logger.getLogger(AbstractSupportController.class);

  @Autowired
  private SupportService supportService;

  /**
   * This method returns the list of Tickets.
   * 
   * @param tenant
   * @param tenantParam
   * @param statusFilter
   * @param showNewTicket
   * @param sortType
   * @param sortColumn
   * @param queryLocator
   * @param page
   * @param ticketNumber
   * @param map
   * @return String
   */
  @RequestMapping(value = {
      "/", "/tickets"
  }, method = RequestMethod.GET)
  public String listTickets(@ModelAttribute("currentTenant") Tenant tenant,
      @RequestParam(value = "tenant", required = false) String tenantParam,
      @RequestParam(value = "statusFilter", required = false) String statusFilter,
      @RequestParam(value = "showNewTicket", required = false) boolean showNewTicket,
      @RequestParam(value = "sortType", required = false) String sortType,
      @RequestParam(value = "sortColumn", required = false) String sortColumn,
      @RequestParam(value = "queryLocator", required = false) String queryLocator,
      @RequestParam(value = "page", required = false, defaultValue = "1") int page,
      @RequestParam(value = "ticketNumber", required = false) String ticketNumber, ModelMap map) {
    logger.debug("Entering in listTickets");

    int perPage = getDefaultPageSize();
    map.addAttribute("defaultPageSize", getDefaultPageSize());
    map.addAttribute("current_page", page);
    User user = getCurrentUser(true);
    if (isSurrogatedTenant(user.getTenant(), tenantParam)
        && userService.hasAnyAuthority(user, "ROLE_TICKET_MANAGEMENT")) {
      map.addAttribute("showUserProfile", true);
      map.addAttribute("showHealthTab", false);
    } else {
      map.addAttribute("showHealthTab", true);
    }
    setPage(map, Page.SUPPORT_ALL_TICKETS);
    if (tenantParam != null && !tenant.getParam().equals(tenantParam)) {
      Tenant effectiveTenant = supportService.getEffectiveTenant(tenantParam);
      if (effectiveTenant != null) {
        tenant = effectiveTenant;
        user = tenant.getOwner();
      }
    }

    map.addAttribute("userHasCloudServiceAccount", userService.isUserHasAnyActiveCloudService(user));
    map.addAttribute("tenant", tenant);
    Capability capability = supportService.getTicketCapability();
    if (capability != null && capability.equals(Capability.C)) {
      TicketForm ticketForm = new TicketForm();
      map.addAttribute("ticketForm", ticketForm);
      return "support.tickets.createonly";
    } else {
      List<Ticket> tickets = new ArrayList<Ticket>();
      Integer totalTickets = 0;
      String status = "All";
      if (statusFilter != null && !statusFilter.equalsIgnoreCase("All")) {
        status = statusFilter;
        map.addAttribute("statusFilter", status.toUpperCase());
        logger.info(" filter by status = " + status);
      }

      if (ticketNumber != null) {
        Ticket tkt = null;
        try {
          tkt = supportService.get(ticketNumber);
        } catch (TicketServiceException e) {
          return "support.tickets";
        }
        if (tkt != null) {
          tickets.add(tkt);
          totalTickets = 1;
        }
      }

      else {
        List<TicketStatus> listTicketStatus = new ArrayList<Ticket.TicketStatus>();
        if (status.equalsIgnoreCase("Open")) {
          listTicketStatus.add(TicketStatus.NEW);
          listTicketStatus.add(TicketStatus.ESCALATED);
          listTicketStatus.add(TicketStatus.WORKING);
        } else if (!status.equalsIgnoreCase("All")) {
          listTicketStatus.add(TicketStatus.getTicketStatus(status));
        }
        List<User> users = new ArrayList<User>();
        users.add(user);

        if (sortType != null && sortColumn != null) {
          map.addAttribute("sortType", sortType);
          map.addAttribute("sortColumn", sortColumn);
        }
        Map<String, String> responseAttribute = new HashMap<String, String>();
        responseAttribute.put("queryLocator", queryLocator);
        List<Ticket> allTickets = new ArrayList<Ticket>();
        try {
          map.put("ticketServiceConfigured", true);
          allTickets = supportService.list(page, perPage + 1, listTicketStatus, users, sortType, sortColumn,
              responseAttribute);
          if (allTickets == null) {
            tickets = null;
            map.put("ticketServiceConfigured", false);
            return "support.tickets";
          }

        } catch (TicketServiceException e) {
          return "support.tickets";
        }
        totalTickets = allTickets.size();
        if (allTickets.size() > perPage) {
          allTickets.remove(allTickets.size() - 1);
        }
        tickets = allTickets;
        map.addAttribute("queryLocator", responseAttribute.get("queryLocator"));
        map.addAttribute("hasMore", responseAttribute.get("hasMore"));
      }

      map.addAttribute("tickets", tickets);
      if (tickets != null && tickets.size() > 0) {
        map.addAttribute("ticketForm", new TicketForm(tickets.get(0)));
        List<TicketComment> comments = new ArrayList<TicketComment>();
        try {
          for (Ticket tkt : tickets) {
            List<TicketComment> tktComments = supportService.listComments(tkt, user);
            if (tktComments != null && tktComments.size() > 0) {
              comments.addAll(tktComments);
            }
          }
        } catch (TicketServiceException e) {
          throw new InvalidAjaxRequestException(e.getMessage(), e);
        }
        if (comments != null && comments.size() > 0) {
          map.addAttribute("ticketcomments", comments);
        }
        TicketCommentForm ticketCommentForm = new TicketCommentForm();
        TicketComment comment = new TicketComment();
        comment.setParentId(tickets.get(0).getUuid());
        ticketCommentForm.setComment(comment);
        map.addAttribute("ticketCommentForm", ticketCommentForm);
      }
      if (tickets != null && tickets.size() > 0) {
        map.addAttribute("size", tickets.size());
      } else {
        map.addAttribute("size", 0);
      }

      map.addAttribute("user", user);
      map.addAttribute("showNewTicket", showNewTicket);
      map.addAttribute("statuses", TicketStatus.values());

      if (totalTickets > perPage) {
        map.addAttribute("enable_next", "True");
      } else {
        map.addAttribute("enable_next", "False");
      }

      logger.debug("Leaving in listTickets");
      return "support.tickets";
    }
  }

  /**
   * This method returns the tickets for a particular page
   * 
   * @param tenant
   * @param tenantParam
   * @param statusFilter
   * @param showNewTicket
   * @param sortType
   * @param sortColumn
   * @param queryLocator
   * @param map
   * @param request
   * @return String
   */
  @RequestMapping(value = {
      "/", "/tickets/page"
  }, method = RequestMethod.GET)
  public String listTicketsPage(@ModelAttribute("currentTenant") Tenant tenant,
      @RequestParam(value = "tenant", required = false) String tenantParam,
      @RequestParam(value = "statusFilter", required = false) String statusFilter,
      @RequestParam(value = "showNewTicket", required = false) boolean showNewTicket,
      @RequestParam(value = "sortType", required = false) String sortType,
      @RequestParam(value = "sortColumn", required = false) String sortColumn,
      @RequestParam(value = "queryLocator", required = true) String queryLocator, ModelMap map,
      HttpServletRequest request) {
    logger.debug("Entering in listTicketsPage");

    if (queryLocator == null || "null".equalsIgnoreCase(queryLocator)) {
      logger.debug("Leaving in listTicketsPage: queryLocator is null. No more data to show");
      return "support.tickets.list";
    }
    User user = getCurrentUser(true);
    if (tenantParam != null && !tenant.getParam().equals(tenantParam)) {
      Tenant effectiveTenant = supportService.getEffectiveTenant(tenantParam);
      if (effectiveTenant != null) {
        tenant = effectiveTenant;
        user = tenant.getOwner();
      }
    }
    List<Ticket> tickets = null;
    String status = "All";
    if (statusFilter != null) {
      status = statusFilter;
      logger.info(" filter by status = " + status);
    }
    Map<String, String> responseAttribute = new HashMap<String, String>();
    responseAttribute.put("queryLocator", queryLocator);

    List<TicketStatus> listTicketStatus = new ArrayList<Ticket.TicketStatus>();
    if (status.equalsIgnoreCase("Open")) {
      listTicketStatus.add(TicketStatus.NEW);
      listTicketStatus.add(TicketStatus.ESCALATED);
      listTicketStatus.add(TicketStatus.WORKING);
    } else if (!status.equalsIgnoreCase("All")) {
      listTicketStatus.add(TicketStatus.getTicketStatus(status));
    }
    List<User> users = new ArrayList<User>();
    users.add(user);
    try {
      tickets = supportService.list(0, 0, listTicketStatus, users, sortType, sortColumn, responseAttribute);
    } catch (TicketServiceException e) {
      throw new InvalidAjaxRequestException(e.getMessage(), e);
    }
    map.addAttribute("tickets", tickets);
    map.addAttribute("queryLocator", responseAttribute.get("queryLocator"));

    map.addAttribute("enable_next", "False");

    logger.debug("Leaving in listTicketsPage");
    return "support.tickets.list";
  }

  /**
   * This method is used to fetch TicketForm for creating Ticket
   * 
   * @param tenantParam
   * @param map
   * @return String
   */
  @RequestMapping(value = "/tickets/create", method = RequestMethod.GET)
  public String createTicket(@RequestParam(value = "tenant", required = false) String tenantParam, ModelMap map) {
    logger.debug("###In createTicket method starting...");
    TicketForm ticketForm = new TicketForm();
    map.addAttribute("createTicketForm", ticketForm);
    logger.debug("###In createTicket method end...");
    return "support.tickets.create";
  }

  /**
   * This method create a Ticket
   * 
   * @param tenantParam
   * @param request
   * @param ticketForm
   * @param map
   * @return Ticket
   */
  @ResponseBody
  @RequestMapping(value = "/tickets/create", method = RequestMethod.POST)
  public Ticket createTicket(@RequestParam(value = "tenant", required = false) String tenantParam,
      HttpServletRequest request, @ModelAttribute("createTicketForm") TicketForm ticketForm, ModelMap map) {
    logger.debug("###In createTicket method starting...(POST)");
    User user = getCurrentUser(true);
    Tenant tenant = user.getTenant();
    if (tenantParam != null && !tenant.getParam().equals(tenantParam)) {
      Tenant effectiveTenant = supportService.getEffectiveTenant(tenantParam);
      if (effectiveTenant != null) {
        tenant = effectiveTenant;
        user = tenant.getOwner();
      }
    }
    Ticket ticket = ticketForm.getTicket();
    try {
      ticket = supportService.create(ticket.getSubject(), ticket.getDescription(), "WEB", user);
    } catch (TicketServiceException e) {
      throw new InvalidAjaxRequestException(e.getMessage(), e);
    }
    logger.debug("###In createTicket method end...(POST)");
    return ticket;
  }

  /**
   * This method returns a particular Ticket
   * 
   * @param tenant
   * @param ticketNumber
   * @param tenantParam
   * @param map
   * @return String
   */
  @RequestMapping(value = "/tickets/view", method = RequestMethod.GET)
  public String viewTicket(@ModelAttribute("currentTenant") Tenant tenant,
      @RequestParam(value = "ticketNumber", required = true) String ticketNumber,
      @RequestParam(value = "tenant", required = false) String tenantParam, ModelMap map) {
    logger.debug("###In viewTicket method starting...(GET) with tktNumber" + ticketNumber);

    User user = getCurrentUser(true);
    if (tenantParam != null && !tenant.getParam().equals(tenantParam)) {
      Tenant effectiveTenant = supportService.getEffectiveTenant(tenantParam);
      if (effectiveTenant != null) {
        tenant = effectiveTenant;
        user = tenant.getOwner();
      }
    }

    try {
      if (ticketNumber != null) {
        Ticket ticket = supportService.get(ticketNumber);
        if (ticket != null) {
          map.addAttribute("ticket", ticket);
          map.addAttribute("ticketForm", new TicketForm(ticket));
          List<TicketComment> comments = supportService.listComments(ticket, user);
          if (comments != null && comments.size() > 0) {
            map.addAttribute("ticketcomments", comments);
          }
          TicketCommentForm ticketCommentForm = new TicketCommentForm();
          TicketComment comment = new TicketComment();
          comment.setParentId(ticket.getUuid());
          ticketCommentForm.setComment(comment);
          map.addAttribute("ticketCommentForm", ticketCommentForm);
        }
      }
    } catch (TicketServiceException e) {
      throw new InvalidAjaxRequestException(e.getMessage(), e);
    }
    map.addAttribute("tenant", tenant);
    map.addAttribute("statuses", TicketStatus.values());
    logger.debug("###In editTicket method end...(GET)");
    return "support.tickets.view";
  }

  /**
   * This method creates Ticket Comment for a Ticket
   * 
   * @param tenant
   * @param ticketCommentForm
   * @param ticketNumber
   * @param tenantParam
   * @param map
   * @return String
   */
  @RequestMapping(value = "/tickets/{ticketId}/comment", method = RequestMethod.POST)
  @ResponseBody
  public String createNewComment(@ModelAttribute("currentTenant") Tenant tenant,
      @ModelAttribute("ticketCommentForm") TicketCommentForm ticketCommentForm,
      @PathVariable(value = "ticketId") String ticketNumber,
      @RequestParam(value = "tenant", required = false) String tenantParam, ModelMap map) {
    logger.debug("###In createNewComment method starting...(POST)");
    TicketComment comment = ticketCommentForm.getComment();
    User user = getCurrentUser(true);
    if (tenantParam != null) {
      Tenant effectiveTenant = supportService.getEffectiveTenant(tenantParam);
      if (effectiveTenant != null) {
        tenant = effectiveTenant;
        user = tenant.getOwner();
      }
    }

    try {
      if (ticketNumber != null) {
        Ticket ticket = supportService.get(ticketNumber);
        if (ticket != null) {
          if (ticket.getUuid().equals(comment.getParentId())) {
            comment = supportService.createComment(ticket, comment.getComment(), user);
          }
        }
      }
    } catch (TicketServiceException e) {
      throw new InvalidAjaxRequestException(e.getMessage(), e);
    } catch (Exception e) {
      logger.error("Failed to post comment", e);
      return "failure";
    }

    logger.debug("###In createNewComment method end...(POST) ");
    return "success";
  }

  /**
   * This method is used to edit a Ticket
   * 
   * @param tenant
   * @param statusFilter
   * @param tenantParam
   * @param sortType
   * @param sortColumn
   * @param caseNumber
   * @param request
   * @param ticketForm
   * @param map
   * @return String
   */
  @RequestMapping(value = "/tickets/edit", method = RequestMethod.POST)
  @ResponseBody
  public String editTicket(@ModelAttribute("currentTenant") Tenant tenant,
      @RequestParam(value = "statusFilter", required = false) String statusFilter,
      @RequestParam(value = "tenant", required = false) String tenantParam,
      @RequestParam(value = "sortType", required = false) String sortType,
      @RequestParam(value = "sortColumn", required = false) String sortColumn,
      @RequestParam(value = "caseNumber", required = false) String caseNumber, HttpServletRequest request,
      @ModelAttribute("ticketForm") TicketForm ticketForm, ModelMap map) {
    logger.debug("###In editTicket method starting...(POST)");
    User user = getCurrentUser(true);
    if (tenantParam != null) {
      Tenant effectiveTenant = supportService.getEffectiveTenant(tenantParam);
      if (effectiveTenant != null) {
        tenant = effectiveTenant;
        user = tenant.getOwner();
      }
    }
    sortType = (sortType == null || sortType.equalsIgnoreCase("null") ? "" : sortType);
    sortColumn = (sortColumn == null || sortColumn.equalsIgnoreCase("null") ? "" : sortColumn);
    Ticket ticket = ticketForm.getTicket();
    Ticket t = null;
    try {
      t = supportService.get(caseNumber);
      ticket.setUpdatedAt(new Date());
      ticket.setUpdatedBy(user);
      if (ticket.getStatus() == null || "null".equals(ticket.getStatus().name())) {
        ticket.setStatus(t.getStatus());
      }
      supportService.update(ticket, user);
    } catch (TicketServiceException e) {
      throw new InvalidAjaxRequestException(e.getMessage(), e);
    }
    logger.debug("###In editTicket method end...(POST)");
    return "success";
  }

  /**
   * This method is used to close a Ticket
   * 
   * @param tenant
   * @param ticketNumber
   * @param request
   * @param ticketForm
   * @param map
   * @return String
   */
  @RequestMapping(value = "/tickets/close", method = RequestMethod.POST)
  @ResponseBody
  public String closeTicket(@ModelAttribute("currentTenant") Tenant tenant,
      @RequestParam(value = "caseNumber", required = true) String ticketNumber, HttpServletRequest request,
      @ModelAttribute("ticketForm") TicketForm ticketForm, ModelMap map) {
    logger.debug("###In closeTicket method starting...(POST) tkt#" + ticketNumber);
    String returnValue = "Failure";
    try {
      if (ticketNumber != null) {
        Ticket tkt = supportService.get(ticketNumber);
        tkt.setStatus(TicketStatus.CLOSED);
        tkt.setUpdatedAt(new Date());
        tkt.setUpdatedBy(getCurrentUser(true));
        supportService.update(tkt, tenant.getOwner());
        returnValue = "Success";
      }
    } catch (TicketServiceException e) {
      throw new InvalidAjaxRequestException(e.getMessage(), e);
    }
    logger.debug("###In editTicket method end...(POST)");
    return returnValue;
  }

  /**
   * This method get the Tickets count depending upon its state
   * 
   * @param tenant
   * @param tenantParam
   * @param map
   * @param session
   * @param request
   * @return String
   */
  @RequestMapping(value = {
    "/homeTicketsCount"
  }, method = RequestMethod.GET)
  public String homeTicketsCount(@ModelAttribute("currentTenant") Tenant tenant,
      @RequestParam(value = "tenantParam", required = false) String tenantParam, ModelMap map, HttpSession session,
      HttpServletRequest request) {
    logger.debug("Entering in homeTicketsCount");
    User user = getCurrentUser(true);
    if (tenantParam != null) {
      Tenant effectiveTenant = supportService.getEffectiveTenant(tenantParam);
      if (effectiveTenant != null) {
        tenant = effectiveTenant;
        user = tenant.getOwner();
      }
    }

    try {
      List<TicketStatus> listTicketStatus = new ArrayList<Ticket.TicketStatus>();
      List<User> users = new ArrayList<User>();
      users.add(user);

      List<Ticket> tickets = supportService.list(0, 0, listTicketStatus, users, null, null,
          new HashMap<String, String>());

      int newTickets = 0;
      int workingTickets = 0;
      int closedTickets = 0;
      int escalatedTickets = 0;
      for (Ticket tkt : tickets) {
        if (tkt.getStatus().equals(TicketStatus.NEW)) {
          newTickets++;
        } else if (tkt.getStatus().equals(TicketStatus.WORKING)) {
          workingTickets++;
        } else if (tkt.getStatus().equals(TicketStatus.CLOSED)) {
          closedTickets++;
        } else if (tkt.getStatus().equals(TicketStatus.ESCALATED)) {
          escalatedTickets++;
        }
      }
      map.addAttribute("new_tickets_count", newTickets);
      map.addAttribute("working_tickets_count", workingTickets);
      map.addAttribute("closed_tickets_count", closedTickets);
      map.addAttribute("escalated_tickets_count", escalatedTickets);

    } catch (SupportServiceException ex) {
    } catch (TicketServiceException e) {
      throw new InvalidAjaxRequestException(e.getMessage(), e);
    }
    logger.debug("Leaving in homeTicketsCount");
    return "home.support.tickets.count";
  }

  /**
   * This method is used to Form To Email Ticket
   * 
   * @param tenantParam
   * @param tenant
   * @param request
   * @param ticketForm
   * @param map
   * @return String
   */
  @RequestMapping(value = "/tickets/create/email", method = RequestMethod.POST)
  public String createFormToEmail(@RequestParam(value = "tenant", required = false) String tenantParam,
      @ModelAttribute("currentTenant") Tenant tenant, HttpServletRequest request,
      @ModelAttribute("ticketForm") TicketForm ticketForm, ModelMap map) {
    setPage(map, Page.SUPPORT_ALL_TICKETS);
    User user = getCurrentUser();
    if (tenantParam != null) {
      Tenant effectiveTenant = supportService.getEffectiveTenant(tenantParam);
      if (effectiveTenant != null) {
        tenant = effectiveTenant;
      }
    }
    Ticket ticket = ticketForm.getTicket();
    ticket = supportService.create(ticket.getSubject(), ticket.getDescription(), Ticket.Category.EMAIL.getName(), user);
    map.addAttribute("message",
        messageSource.getMessage("support.ticket.create.email.success", null, getSessionLocale(request)));
    map.addAttribute("tenant", tenant);
    map.addAttribute("status", true);
    map.addAttribute("ticketForm", new TicketForm());
    logger.info("Ticket has been created and details are : Description = " + ticket.getDescription() + ", Subject = "
        + ticket.getSubject());
    return "support.tickets.createonly";
  }
}

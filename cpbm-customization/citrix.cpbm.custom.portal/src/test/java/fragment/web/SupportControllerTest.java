/*
 * Copyright © 2013 Citrix Systems, Inc. You may not use, copy, or modify this file except pursuant to a valid license
 * agreement from Citrix Systems, Inc.
 */
package fragment.web;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.ModelMap;

import web.WebTestsBaseWithMockConnectors;
import web.support.DispatcherTestServlet;

import com.citrix.cpbm.platform.bootstrap.service.BootstrapActivator;
import com.citrix.cpbm.portal.fragment.controllers.SupportController;
import com.vmops.internal.service.EventService;
import com.vmops.internal.service.PrivilegeService;
import com.vmops.internal.service.TicketService;
import com.vmops.model.AccountType;
import com.vmops.model.Profile;
import com.vmops.model.Tenant;
import com.vmops.model.TenantHandle;
import com.vmops.model.Ticket;
import com.vmops.model.Ticket.Category;
import com.vmops.model.Ticket.TicketStatus;
import com.vmops.model.TicketComment;
import com.vmops.model.User;
import com.vmops.model.UserHandle;
import com.vmops.service.ChannelService;
import com.vmops.service.ProfileService;
import com.vmops.service.SupportService;
import com.vmops.service.exceptions.InvalidAjaxRequestException;
import com.vmops.service.exceptions.TicketServiceException;
import com.vmops.web.forms.TicketCommentForm;
import com.vmops.web.forms.TicketForm;
import common.MockApplicationListener;
import common.MockConnectorUtilService;
import common.MockOSSInstance;

public class SupportControllerTest extends WebTestsBaseWithMockConnectors {

  private ModelMap map;

  @Autowired
  private SupportController controller;

  @Autowired
  private ProfileService profileService;

  @Autowired
  private ChannelService channelService;

  @Autowired
  EventService eventService;

  @Autowired
  private SupportService supportService;

  @Autowired
  protected MockApplicationListener eventListener;

  @Autowired
  MockConnectorUtilService mockConnectorUtils;

  @Resource(name = "formToEmailTicketService")
  private TicketService ticketService;

  @Autowired
  private PrivilegeService privilegeService;

  private MockHttpServletRequest request;

  private Tenant tenant;

  private User user;

  @Before
  public void init() {
    prepareMock(true, new BootstrapActivator());
    setMockHandlersInSupportService();
    map = new ModelMap();
    request = new MockHttpServletRequest();
    tenant = createTestTenant(accountTypeDAO.getDefaultRegistrationAccountType(), new Date());
    tenant.setSourceChannel(channelService.getDefaultServiceProviderChannel());
    user = createTestUserInTenant(tenant);
    tenant.setOwner(user);
    tenantDAO.save(tenant);
    List<TicketStatus> listTicketStatus = new ArrayList<Ticket.TicketStatus>();
    listTicketStatus.add(TicketStatus.NEW);
    listTicketStatus.add(TicketStatus.CLOSED);
    listTicketStatus.add(TicketStatus.ESCALATED);
    listTicketStatus.add(TicketStatus.WORKING);
    List<User> users = new ArrayList<User>();
    users.add(user);
    supportService.list(0, 0, listTicketStatus, users, "", "", new HashMap<String, String>()).clear();
    createTestTicket(5, tenant, user);
  }

  @Override
  protected Tenant createTestTenant(AccountType type, Date createdAt) {
    Tenant newTenant = new Tenant("Acme Corp " + random.nextInt(), type, getRootUser(), randomAddress(), true,
        currencyValueDAO.findByCurrencyCode("USD"), getPortalUser());
    newTenant.setMaxUsers(newTenant.getAccountType().getMaxUsers());
    newTenant.setAccountId(accountNumberService.next());

    newTenant.setCreatedAt(createdAt);
    tenantDAO.save(newTenant);
    privilegeService.newTenant(newTenant);
    TenantHandle tenantHandle = new TenantHandle();
    tenantHandle.setHandle("Test" + System.currentTimeMillis());
    tenantHandle.setServiceInstanceUuid("003fa8ee-fba3-467f-a517-fd806dae8a80");
    tenantHandle.setTenant(newTenant);
    newTenant.addHandle(tenantHandle);
    tenantDAO.merge(newTenant);
    return newTenant;
  }

  @Override
  protected User createTestUserInTenant(Tenant tenant) {
    User newUser = new User("test", "user", "test@test.com", VALID_USER + random.nextInt(), VALID_PASSWORD,
        VALID_PHONE, VALID_TIMEZONE, tenant, userProfile, getPortalUser());
    userDAO.save(newUser);
    UserHandle userHandle = new UserHandle();
    userHandle.setHandle("Test" + System.currentTimeMillis());
    userHandle.setServiceInstanceUuid("003fa8ee-fba3-467f-a517-fd806dae8a80");
    userHandle.setUser(newUser);
    newUser.addHandle(userHandle);
    userDAO.merge(newUser);
    privilegeService.newUser(newUser);
    tenant.getUsers().add(newUser);
    return newUser;
  }

  // This method creates a Ticket
  private Ticket createTicket() {
    Ticket ticket = new Ticket();
    ticket.setDescription("test desc");
    ticket.setSubject("test sub");
    ticket.setCategory(Category.WEB);
    ticket.setOwner(tenant.getOwner());
    TicketForm form = new TicketForm(ticket);
    Ticket newTicket = controller.createTicket(tenant.getParam(), request, form, map);
    return newTicket;
  }

  // This method comments on a Ticket
  private String commentOnTicket(String comment, Ticket ticket) {
    TicketCommentForm ticketCommentForm = new TicketCommentForm();
    TicketComment ticketComment = new TicketComment();
    ticketComment.setComment(comment);
    ticketComment.setParentId(ticket.getUuid());
    ticketCommentForm.setComment(ticketComment);
    String response = controller.createNewComment(user.getTenant(), ticketCommentForm, ticket.getCaseNumber(), user
        .getTenant().getParam(), map);
    return response;

  }

  // This method closes the Ticket
  private String closeTicket(TicketForm form, User user) {
    form.getTicket().setStatus(TicketStatus.CLOSED);
    form.getTicket().setUpdatedAt(new Date());
    form.getTicket().setUpdatedBy(user);
    String response = controller.closeTicket(user.getTenant(), form.getTicket().getCaseNumber(), request, form, map);
    return response;

  }

  // This method creates anytype of User in Tenant
  private User createUserInTenant(Profile profile, Tenant tenant) {
    User user = new User("power", "user", "test@test.com", VALID_USER + System.currentTimeMillis(), VALID_PASSWORD,
        VALID_PHONE, VALID_TIMEZONE, tenant, profile, getPortalUser());
    userDAO.save(user);
    privilegeService.newUser(user);
    tenant.getUsers().add(user);
    return user;
  }

  @Test
  public void testSupportRouting() throws Exception {
    logger.debug("Testing routing....Started");
    DispatcherTestServlet servlet = this.getServletInstance();
    Method expected = locateMethod(controller.getClass(), "listTickets", new Class[] {
        Tenant.class, String.class, String.class, Boolean.TYPE, String.class, String.class, String.class, int.class,
        String.class, ModelMap.class
    });
    Method handler = servlet.recognize(getRequestTemplate(HttpMethod.GET, "/support/tickets"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controller.getClass(), "createTicket", new Class[] {
        String.class, ModelMap.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.GET, "/support/tickets/create"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controller.getClass(), "createTicket", new Class[] {
        String.class, HttpServletRequest.class, TicketForm.class, ModelMap.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.POST, "/support/tickets/create"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controller.getClass(), "viewTicket", new Class[] {
        Tenant.class, String.class, String.class, ModelMap.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.GET, "/support/tickets/view"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controller.getClass(), "createNewComment", new Class[] {
        Tenant.class, TicketCommentForm.class, String.class, String.class, ModelMap.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.POST, "/support/tickets/{ticketId}/comment"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controller.getClass(), "editTicket", new Class[] {
        Tenant.class, String.class, String.class, String.class, String.class, String.class, HttpServletRequest.class,
        TicketForm.class, ModelMap.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.POST, "/support/tickets/edit"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controller.getClass(), "closeTicket", new Class[] {
        Tenant.class, String.class, HttpServletRequest.class, TicketForm.class, ModelMap.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.POST, "/support/tickets/close"));
    Assert.assertEquals(expected, handler);

    logger.debug("Testing routing.... Done");
  }

  @Test
  public void testCreateTicket() throws Exception {
    Ticket ticket = new Ticket();
    ticket.setDescription("test desc");
    ticket.setSubject("test sub");
    ticket.setCategory(Category.WEB);
    ticket.setOwner(user);
    TicketForm form = new TicketForm(ticket);
    Ticket newTicket = controller.createTicket(tenant.getUuid(), request, form, map);
    Assert.assertEquals(ticket.getSubject(), newTicket.getSubject());
  }

  @Test
  public void testListTicketsAsRoot() throws Exception {
    Tenant systemTenant = controller.getCurrentUser().getTenant();
    String view = controller.listTickets(systemTenant, tenant.getUuid(), "All", false, "", "", "", 1, null, map);

    List<TicketStatus> listTicketStatus = new ArrayList<Ticket.TicketStatus>();
    listTicketStatus.add(TicketStatus.NEW);
    listTicketStatus.add(TicketStatus.CLOSED);
    listTicketStatus.add(TicketStatus.ESCALATED);
    listTicketStatus.add(TicketStatus.WORKING);

    List<User> users = new ArrayList<User>();
    users.add(user);
    Map<String, String> responseAttribute = new HashMap<String, String>();
    responseAttribute.put("queryLocator", "xyz");

    List<Ticket> tickets = supportService.list(0, 0, listTicketStatus, users, "", "", responseAttribute);

    Assert.assertEquals("support.tickets", view);
    Assert.assertTrue(map.containsKey("tickets"));
    Assert.assertTrue(map.containsValue(tickets));

    @SuppressWarnings("unchecked")
    List<String> list = (List<String>) map.get("tickets");
    Assert.assertEquals(5, list.size());
  }

  @Test
  public void testListTicketsAsMasterUser() throws Exception {
    Tenant otherTenant = tenantDAO.find(2L);
    User otherMasterUser = otherTenant.getOwner();
    userDAO.save(otherMasterUser);
    asUser(otherMasterUser);
    Tenant systemTenant = controller.getCurrentUser().getTenant();
    createTestTicket(3, otherTenant, otherMasterUser);
    asUser(user);
    systemTenant = controller.getCurrentUser().getTenant();
    String view = controller.listTickets(systemTenant, tenant.getUuid(), "All", false, "", "", "", 1, null, map);

    List<TicketStatus> listTicketStatus = new ArrayList<Ticket.TicketStatus>();
    listTicketStatus.add(TicketStatus.NEW);
    listTicketStatus.add(TicketStatus.CLOSED);
    listTicketStatus.add(TicketStatus.ESCALATED);
    listTicketStatus.add(TicketStatus.WORKING);

    List<User> users = new ArrayList<User>();
    users.add(user);
    Map<String, String> responseAttribute = new HashMap<String, String>();
    responseAttribute.put("queryLocator", "xyz");

    List<Ticket> tickets = supportService.list(0, 0, listTicketStatus, users, "", "", responseAttribute);

    Assert.assertEquals("support.tickets", view);
    Assert.assertTrue(map.containsKey("tickets"));
    Assert.assertTrue(map.containsValue(tickets));

    @SuppressWarnings("unchecked")
    List<String> list = (List<String>) map.get("tickets");
    Assert.assertEquals(5, list.size());

    asUser(otherMasterUser);
    responseAttribute.clear();
    view = controller.listTickets(systemTenant, tenant.getUuid(), "All", false, "", "", "", 1, null, map);
    Assert.assertEquals("support.tickets", view);
    Assert.assertTrue(map.containsKey("tickets"));

    @SuppressWarnings("unchecked")
    List<String> list1 = (List<String>) map.get("tickets");
    Assert.assertEquals(14, list1.size());
  }

  @Test
  public void testListTicketsAsRootOtherTenant() throws Exception {
    Tenant otherTenant = tenantDAO.find(2L);
    User otherMasterUser = otherTenant.getOwner();
    userDAO.save(otherMasterUser);
    asUser(otherMasterUser);
    Tenant systemTenant = controller.getCurrentUser().getTenant();
    createTestTicket(3, otherTenant, otherMasterUser);
    asUser(getRootUser());
    systemTenant = controller.getCurrentUser().getTenant();
    String view = controller.listTickets(systemTenant, tenant.getUuid(), "All", false, "", "", "", 1, null, map);

    List<TicketStatus> listTicketStatus = new ArrayList<Ticket.TicketStatus>();
    listTicketStatus.add(TicketStatus.NEW);
    listTicketStatus.add(TicketStatus.CLOSED);
    listTicketStatus.add(TicketStatus.ESCALATED);
    listTicketStatus.add(TicketStatus.WORKING);

    List<User> users = new ArrayList<User>();
    users.add(user);
    Map<String, String> responseAttribute = new HashMap<String, String>();
    responseAttribute.put("queryLocator", "xyz");

    List<Ticket> tickets = supportService.list(0, 0, listTicketStatus, users, "", "", responseAttribute);

    Assert.assertEquals("support.tickets", view);
    Assert.assertTrue(map.containsKey("tickets"));
    Assert.assertTrue(map.containsValue(tickets));

    @SuppressWarnings("unchecked")
    List<String> list = (List<String>) map.get("tickets");
    Assert.assertEquals(5, list.size());

    systemTenant = controller.getCurrentUser().getTenant();
    view = controller.listTickets(systemTenant, otherTenant.getUuid(), "All", false, "", "", "", 1, null, map);
    responseAttribute.clear();
    responseAttribute.put("queryLocator", "xyz");
    Assert.assertEquals("support.tickets", view);
    Assert.assertTrue(map.containsKey("tickets"));

    @SuppressWarnings("unchecked")
    List<String> list1 = (List<String>) map.get("tickets");
    Assert.assertEquals(12, list1.size());
  }

  @Test
  public void testListTicketsPageAsNormalUser() throws Exception {
    User normalUser = createTestUserInTenant(tenant);
    asUser(normalUser);
    Tenant systemTenant = controller.getCurrentUser().getTenant();
    String view = controller.listTickets(systemTenant, tenant.getUuid(), "All", false, "", "", "", 1, null, map);
    List<TicketStatus> listTicketStatus = new ArrayList<Ticket.TicketStatus>();
    listTicketStatus.add(TicketStatus.NEW);
    listTicketStatus.add(TicketStatus.CLOSED);
    listTicketStatus.add(TicketStatus.ESCALATED);
    listTicketStatus.add(TicketStatus.WORKING);

    List<User> users = new ArrayList<User>();
    users.add(user);
    Map<String, String> responseAttribute = new HashMap<String, String>();
    responseAttribute.put("queryLocator", "xyz");

    List<Ticket> tickets = supportService.list(0, 0, listTicketStatus, users, "", "", responseAttribute);

    Assert.assertEquals("support.tickets", view);
    Assert.assertTrue(map.containsKey("tickets"));
    Assert.assertTrue(map.containsValue(tickets));

    @SuppressWarnings("unchecked")
    List<String> list = (List<String>) map.get("tickets");
    Assert.assertEquals(5, list.size());
  }

  @Test
  public void testListTicketsPageAsRoot() throws Exception {
    Tenant systemTenant = controller.getCurrentUser().getTenant();
    String view = controller.listTicketsPage(systemTenant, tenant.getUuid(), "All", false, "", "", "", map, request);
    List<TicketStatus> listTicketStatus = new ArrayList<Ticket.TicketStatus>();
    listTicketStatus.add(TicketStatus.NEW);
    listTicketStatus.add(TicketStatus.CLOSED);
    listTicketStatus.add(TicketStatus.ESCALATED);
    listTicketStatus.add(TicketStatus.WORKING);

    List<User> users = new ArrayList<User>();
    users.add(user);
    Map<String, String> responseAttribute = new HashMap<String, String>();
    responseAttribute.put("queryLocator", "xyz");

    List<Ticket> tickets = supportService.list(0, 0, listTicketStatus, users, "", "", responseAttribute);

    Assert.assertEquals("support.tickets.list", view);
    Assert.assertTrue(map.containsKey("tickets"));
    Assert.assertTrue(map.containsValue(tickets));

    @SuppressWarnings("unchecked")
    List<String> list = (List<String>) map.get("tickets");
    Assert.assertEquals(5, list.size());
  }

  @Test
  public void testListTicketsPageAsMasterUser() throws Exception {
    Tenant otherTenant = tenantDAO.find(2L);
    User otherMasterUser = otherTenant.getOwner();
    userDAO.save(otherMasterUser);
    asUser(otherMasterUser);
    Tenant systemTenant = controller.getCurrentUser().getTenant();
    createTestTicket(3, otherTenant, otherMasterUser);
    asUser(user);
    systemTenant = controller.getCurrentUser().getTenant();
    String view = controller.listTicketsPage(systemTenant, tenant.getUuid(), "All", false, "", "", "", map, request);
    List<TicketStatus> listTicketStatus = new ArrayList<Ticket.TicketStatus>();
    listTicketStatus.add(TicketStatus.NEW);
    listTicketStatus.add(TicketStatus.CLOSED);
    listTicketStatus.add(TicketStatus.ESCALATED);
    listTicketStatus.add(TicketStatus.WORKING);

    List<User> users = new ArrayList<User>();
    users.add(user);
    Map<String, String> responseAttribute = new HashMap<String, String>();
    responseAttribute.put("queryLocator", "xyz");

    List<Ticket> tickets = supportService.list(0, 0, listTicketStatus, users, "", "", responseAttribute);

    Assert.assertEquals("support.tickets.list", view);
    Assert.assertTrue(map.containsKey("tickets"));
    Assert.assertTrue(map.containsValue(tickets));

    @SuppressWarnings("unchecked")
    List<String> list = (List<String>) map.get("tickets");
    Assert.assertEquals(5, list.size());

    asUser(otherMasterUser);
    responseAttribute.clear();
    view = controller.listTicketsPage(systemTenant, tenant.getUuid(), "All", false, "", "", "", map, request);
    Assert.assertEquals("support.tickets.list", view);
    Assert.assertTrue(map.containsKey("tickets"));

    @SuppressWarnings("unchecked")
    List<String> list1 = (List<String>) map.get("tickets");
    Assert.assertEquals(5, list1.size());
  }

  @Test
  public void testListTicketsPageAsRootOtherTenant() throws Exception {
    Tenant otherTenant = tenantDAO.find(2L);
    User otherMasterUser = otherTenant.getOwner();
    userDAO.save(otherMasterUser);
    asUser(otherMasterUser);
    Tenant systemTenant = controller.getCurrentUser().getTenant();
    createTestTicket(3, otherTenant, otherMasterUser);
    asUser(getRootUser());
    systemTenant = controller.getCurrentUser().getTenant();
    String view = controller.listTicketsPage(systemTenant, tenant.getUuid(), "All", false, "", "", "", map, request);
    List<TicketStatus> listTicketStatus = new ArrayList<Ticket.TicketStatus>();
    listTicketStatus.add(TicketStatus.NEW);
    listTicketStatus.add(TicketStatus.CLOSED);
    listTicketStatus.add(TicketStatus.ESCALATED);
    listTicketStatus.add(TicketStatus.WORKING);

    List<User> users = new ArrayList<User>();
    users.add(user);
    Map<String, String> responseAttribute = new HashMap<String, String>();
    responseAttribute.put("queryLocator", "xyz");

    List<Ticket> tickets = supportService.list(0, 0, listTicketStatus, users, "", "", responseAttribute);

    Assert.assertEquals("support.tickets.list", view);
    Assert.assertTrue(map.containsKey("tickets"));
    Assert.assertTrue(map.containsValue(tickets));

    @SuppressWarnings("unchecked")
    List<String> list = (List<String>) map.get("tickets");
    Assert.assertEquals(5, list.size());

    systemTenant = controller.getCurrentUser().getTenant();
    view = controller.listTicketsPage(systemTenant, otherTenant.getUuid(), "All", false, "", "", "", map, request);
    responseAttribute.clear();
    responseAttribute.put("queryLocator", "xyz");
    Assert.assertEquals("support.tickets.list", view);
    Assert.assertTrue(map.containsKey("tickets"));

    @SuppressWarnings("unchecked")
    List<String> list1 = (List<String>) map.get("tickets");
    Assert.assertEquals(9, list1.size());
  }

  @Test
  public void testListTicketsAsNormalUser() throws Exception {
    User normalUser = createTestUserInTenant(tenant);
    normalUser.setProfile(profileDAO.find(10L));
    userDAO.save(normalUser);
    asUser(normalUser);
    Tenant systemTenant = controller.getCurrentUser().getTenant();
    String view = controller.listTicketsPage(systemTenant, tenant.getUuid(), "All", false, "", "", "", map, request);
    List<TicketStatus> listTicketStatus = new ArrayList<Ticket.TicketStatus>();
    listTicketStatus.add(TicketStatus.NEW);
    listTicketStatus.add(TicketStatus.CLOSED);
    listTicketStatus.add(TicketStatus.ESCALATED);
    listTicketStatus.add(TicketStatus.WORKING);

    List<User> users = new ArrayList<User>();
    users.add(user);
    Map<String, String> responseAttribute = new HashMap<String, String>();
    responseAttribute.put("queryLocator", "xyz");

    List<Ticket> tickets = supportService.list(0, 0, listTicketStatus, users, "", "", responseAttribute);

    Assert.assertEquals("support.tickets.list", view);
    Assert.assertTrue(map.containsKey("tickets"));
    Assert.assertTrue(map.containsValue(tickets));

    @SuppressWarnings("unchecked")
    List<String> list = (List<String>) map.get("tickets");
    Assert.assertEquals(5, list.size());
  }

  @Test
  public void testCreateTicketAsNormalUser() throws Exception {
    User normalUser = createTestUserInTenant(tenant);
    asUser(normalUser);
    String view = controller.createTicket(normalUser.getTenant().getUuid(), map);
    Assert.assertEquals("support.tickets.create", view);
    TicketForm ticketForm = (TicketForm) map.get("createTicketForm");
    Assert.assertNotNull(ticketForm);
    Assert.assertNull(ticketForm.getTicket());

    Ticket ticket = new Ticket();
    ticketForm.setTicket(ticket);
    ticket.setDescription("test desc");
    ticket.setSubject("test sub");

    Ticket newTicket = controller.createTicket(tenant.getUuid(), request, ticketForm, map);
    Assert.assertEquals(ticket.getSubject(), newTicket.getSubject());
    Assert.assertEquals(normalUser, newTicket.getOwner());
    Assert.assertNotNull(newTicket.getCaseNumber());
  }

  @Test
  public void testCreateTicketAsRootForNormalUser() throws Exception {
    asRoot();
    tenantDAO.save(tenant);
    String view = controller.createTicket(tenant.getUuid(), map);
    Assert.assertEquals("support.tickets.create", view);
    TicketForm ticketForm = (TicketForm) map.get("createTicketForm");
    Assert.assertNotNull(ticketForm);
    Assert.assertNull(ticketForm.getTicket());

    Ticket ticket = new Ticket();
    ticketForm.setTicket(ticket);
    ticket.setDescription("test desc");
    ticket.setSubject("test sub");

    map.clear();
    Ticket newTicket = controller.createTicket(tenant.getUuid(), request, ticketForm, map);
    Assert.assertEquals(ticket.getSubject(), newTicket.getSubject());
    Assert.assertEquals(user, newTicket.getOwner());
    Assert.assertNotNull(newTicket.getCaseNumber());
  }

  @Test
  public void testViewTicket() throws Exception {
    asUser(user);
    String view = controller.createTicket(tenant.getUuid(), map);
    Assert.assertEquals("support.tickets.create", view);
    TicketForm ticketForm = (TicketForm) map.get("createTicketForm");
    Assert.assertNotNull(ticketForm);
    Assert.assertNull(ticketForm.getTicket());

    Ticket ticket = new Ticket();
    ticketForm.setTicket(ticket);
    ticket.setDescription("test desc");
    ticket.setSubject("test sub");

    Ticket newTicket = controller.createTicket(tenant.getUuid(), request, ticketForm, map);
    Assert.assertEquals(ticket.getSubject(), newTicket.getSubject());
    Assert.assertEquals(user, newTicket.getOwner());
    Assert.assertNotNull(newTicket.getCaseNumber());

    map.clear();
  }

  @Test
  public void testViewTicketAsOtherUser() throws Exception {
    asUser(user);
    String view = controller.createTicket(tenant.getUuid(), map);
    Assert.assertEquals("support.tickets.create", view);
    TicketForm ticketForm = (TicketForm) map.get("createTicketForm");
    Assert.assertNotNull(ticketForm);
    Assert.assertNull(ticketForm.getTicket());

    Ticket ticket = new Ticket();
    ticketForm.setTicket(ticket);
    ticket.setDescription("test desc");
    ticket.setSubject("test sub");

    Ticket newTicket = controller.createTicket(tenant.getUuid(), request, ticketForm, map);
    Assert.assertEquals(ticket.getSubject(), newTicket.getSubject());
    Assert.assertEquals(user, newTicket.getOwner());
    Assert.assertNotNull(newTicket.getCaseNumber());

    map.clear();
  }

  @Test
  public void testEditTicket() throws Exception {
    asUser(user);
    String view = controller.createTicket(tenant.getUuid(), map);
    Assert.assertEquals("support.tickets.create", view);
    TicketForm ticketForm = (TicketForm) map.get("createTicketForm");
    Assert.assertNotNull(ticketForm);
    Assert.assertNull(ticketForm.getTicket());

    Ticket ticket = new Ticket();
    ticketForm.setTicket(ticket);
    ticket.setDescription("test desc");
    ticket.setSubject("test sub");

    Ticket newTicket = controller.createTicket(tenant.getUuid(), request, ticketForm, map);
    Assert.assertEquals(ticket.getSubject(), newTicket.getSubject());
    Assert.assertEquals(user, newTicket.getOwner());
    Assert.assertNotNull(newTicket.getCaseNumber());

    map.clear();
  }

  private void createTestTicket(int count, Tenant tenant, User user) {
    for (int i = 0; i < count; i++) {
      supportService.create("base test sub " + i, "base test desc " + i, "web", user);
    }
  }

  /*
   * Description: Test view product Author: Avinash ReviewedBy:
   */

  @Test
  public void testViewTicket1() throws Exception {
    asUser(user);
    ModelMap modelMap = new ModelMap();
    TicketCommentForm ticketCommentForm = new TicketCommentForm();
    TicketComment comment = new TicketComment();
    ticketCommentForm.setComment(comment);
    Ticket ticket = new Ticket();
    TicketForm ticketForm = new TicketForm();
    ticketForm.setTicket(ticket);

    ticket = controller.createTicket(tenant.getUuid(), request, ticketForm, modelMap);
    comment.setParentId(ticket.getUuid());
    controller.createNewComment(tenant, ticketCommentForm, ticket.getCaseNumber(), tenant.getParam(), modelMap);

    String view = controller.viewTicket(tenant, ticket.getCaseNumber(), tenant.getParam(), modelMap);
    Assert.assertNotNull(modelMap);
    Assert.assertEquals(modelMap.get("ticket"), ticket);
    TicketForm ticketForm1 = (TicketForm) modelMap.get("ticketForm");
    Assert.assertEquals(ticketForm1.getTicket(), ticket);
    Assert.assertNotNull(modelMap.get("ticketcomments"));

    Assert.assertNotNull(modelMap.get("ticketCommentForm"));
    Assert.assertEquals(view, new String("support.tickets.view"));
    view = controller.viewTicket(tenant, null, tenant.getParam(), modelMap);
    Assert.assertNotNull(modelMap.get("statuses"));

  }

  @Test
  public void testAddNewComment() throws Exception {
    asUser(user);
    ModelMap modelmap = new ModelMap();
    TicketCommentForm ticketCommentForm = new TicketCommentForm();
    TicketComment comment = new TicketComment();
    ticketCommentForm.setComment(comment);
    Ticket ticket = new Ticket();
    TicketForm ticketForm = new TicketForm();
    ticketForm.setTicket(ticket);

    ticket = controller.createTicket(tenant.getUuid(), request, ticketForm, modelmap);
    comment.setParentId(ticket.getUuid());
    controller.createNewComment(tenant, ticketCommentForm, ticket.getCaseNumber(), tenant.getParam(), modelmap);
    String view = controller.viewTicket(tenant, ticket.getCaseNumber(), tenant.getParam(), modelmap);
    Assert.assertNotNull(modelmap);
    Assert.assertEquals(modelmap.get("ticket"), ticket);
    ticketForm = (TicketForm) modelmap.get("ticketForm");
    Assert.assertEquals(ticketForm.getTicket(), ticket);
    Assert.assertNotNull(modelmap.get("ticketcomments"));

    Assert.assertNotNull(modelmap.get("ticketCommentForm"));
    Assert.assertEquals(view, new String("support.tickets.view"));
    view = controller.viewTicket(tenant, null, tenant.getParam(), modelmap);
    Assert.assertNotNull(modelmap.get("statuses"));

  }

  @SuppressWarnings("unchecked")
  @Test
  public void testCloseTicketByTenant() {
    Tenant tenant = getDefaultTenant();
    asUser(tenant.getOwner());

    Ticket newTicket = createTicket();
    TicketForm form = new TicketForm(newTicket);
    Assert.assertEquals(newTicket.getStatus(), TicketStatus.NEW);

    Assert.assertEquals(closeTicket(form, tenant.getOwner()), "Success");
    controller.listTickets(tenant, tenant.getParam(), "", false, "", "", "", 1, newTicket.getCaseNumber(), map);
    List<Ticket> tickets = (List<Ticket>) map.get("tickets");
    Assert.assertNotNull(tickets);
    Assert.assertEquals(tickets.get(0).getSubject(), newTicket.getSubject());
    Assert.assertEquals(tickets.get(0).getStatus(), TicketStatus.CLOSED);

  }

  @SuppressWarnings("unchecked")
  @Test
  public void testCloseTicketByNormalUserWithRole() {
    Profile profile = profileService.findProfileByName("User");
    String authorityNames[] = {
      "ROLE_TENANT_TICKET_MANAGEMENT"
    };
    profileService.update(profile, authorityNames);

    User normalUser = createTestUserInTenant(tenant);
    asUser(normalUser);

    Ticket newTicket = createTicket();
    TicketForm form = new TicketForm(newTicket);
    Assert.assertEquals(newTicket.getStatus(), TicketStatus.NEW);

    Assert.assertEquals(closeTicket(form, normalUser), "Success");
    controller.listTickets(normalUser.getTenant(), normalUser.getTenant().getParam(), "", false, "", "", "", 1,
        newTicket.getCaseNumber(), map);
    List<Ticket> tickets = (List<Ticket>) map.get("tickets");
    Assert.assertNotNull(tickets);
    Assert.assertEquals(tickets.get(0).getSubject(), newTicket.getSubject());
    Assert.assertEquals(tickets.get(0).getStatus(), TicketStatus.CLOSED);

  }

  @SuppressWarnings("unchecked")
  @Test
  public void testCloseTicketByNormalUserWithoutRole() {

    User normalUser = createTestUserInTenant(tenant);
    asUser(normalUser);

    Ticket newTicket = createTicket();
    TicketForm form = new TicketForm(newTicket);
    Assert.assertEquals(newTicket.getStatus(), TicketStatus.NEW);

    try {
      Assert.assertEquals(closeTicket(form, normalUser), "Success");
      controller.listTickets(normalUser.getTenant(), normalUser.getTenant().getParam(), "", false, "", "", "", 1,
          newTicket.getCaseNumber(), map);
      List<Ticket> tickets = (List<Ticket>) map.get("tickets");
      Assert.assertNotNull(tickets);
      Assert.assertEquals(tickets.get(0).getStatus(), TicketStatus.CLOSED);

      Assert.fail("Normal user is able close Ticket");
    } catch (InvalidAjaxRequestException e) {
      Assert.assertEquals(e.getMessage(), new String("Current user cannot update the ticket"));

    }

  }

  @Test
  public void testCreateTicketByPowerUser() {

    Tenant tenant = getDefaultTenant();

    Profile profile = profileService.findProfileByName("Power User");
    User powerUser = createUserInTenant(profile, tenant);

    asUser(powerUser);
    try {
      Ticket newTicket = createTicket();
      Assert.assertEquals(newTicket.getStatus(), TicketStatus.NEW);
      Assert.assertEquals(newTicket.getSubject(), new String("test sub"));
      Assert.assertEquals(newTicket.getDescription(), new String("test desc"));
      Assert.assertNotNull("Case Number", newTicket.getCaseNumber());
      Assert.assertEquals(newTicket.getOwner(), powerUser);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testCloseTicketByPowerUserWithoutRole() {

    Tenant tenant = getDefaultTenant();

    Profile profile = profileService.findProfileByName("Power User");
    User powerUser = createUserInTenant(profile, tenant);

    asUser(powerUser);

    Ticket newTicket = createTicket();
    TicketForm form = new TicketForm(newTicket);
    Assert.assertEquals(newTicket.getStatus(), TicketStatus.NEW);

    try {
      Assert.assertEquals(closeTicket(form, powerUser), "Success");
      controller.listTickets(powerUser.getTenant(), powerUser.getTenant().getParam(), "", false, "", "", "", 1,
          newTicket.getCaseNumber(), map);
      List<Ticket> tickets = (List<Ticket>) map.get("tickets");
      Assert.assertNotNull(tickets);
      Assert.assertEquals(tickets.get(0).getStatus(), TicketStatus.CLOSED);
      Assert.fail("Power user is able close Ticket Without having Tenant Ticket Manangement Role");
    } catch (InvalidAjaxRequestException e) {
      Assert.assertEquals(e.getMessage(), new String("Current user cannot update the ticket"));

    }

  }

  @Test
  public void testCreateTicketByBillingAdminUser() {

    Tenant tenant = getDefaultTenant();

    Profile profile = profileService.findProfileByName("Billing Admin");
    User billingAdmin = createUserInTenant(profile, tenant);

    asUser(billingAdmin);

    try {
      Ticket newTicket = createTicket();
      Assert.assertEquals(newTicket.getStatus(), TicketStatus.NEW);
      Assert.assertEquals(newTicket.getSubject(), new String("test sub"));
      Assert.assertEquals(newTicket.getDescription(), new String("test desc"));
      Assert.assertNotNull("Case Number", newTicket.getCaseNumber());
      Assert.assertEquals(newTicket.getOwner(), billingAdmin);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testCloseTicketByBillingAdminWithoutRole() {

    Tenant tenant = getDefaultTenant();

    Profile profile = profileService.findProfileByName("Billing Admin");
    User billingAdmin = createUserInTenant(profile, tenant);

    asUser(billingAdmin);

    Ticket newTicket = createTicket();
    TicketForm form = new TicketForm(newTicket);
    Assert.assertEquals(newTicket.getStatus(), TicketStatus.NEW);

    try {
      Assert.assertEquals(closeTicket(form, billingAdmin), "Success");
      controller.listTickets(billingAdmin.getTenant(), billingAdmin.getTenant().getParam(), "", false, "", "", "", 1,
          newTicket.getCaseNumber(), map);
      List<Ticket> tickets = (List<Ticket>) map.get("tickets");
      Assert.assertNotNull(tickets);
      Assert.assertEquals(tickets.get(0).getStatus(), TicketStatus.CLOSED);
      Assert.fail("Billing Admin is able close Ticket without having Tenant Ticket Management Role");
    } catch (InvalidAjaxRequestException e) {
      Assert.assertEquals(e.getMessage(), new String("Current user cannot update the ticket"));

    }

  }

  @SuppressWarnings("unchecked")
  @Test
  public void testCloseTicketByAllUsersForTenant() {

    Profile profile[] = {
        profileService.findProfileByName("Billing Admin"), profileService.findProfileByName("Power User"),
        profileService.findProfileByName("User")
    };

    Tenant tenant = getDefaultTenant();

    asUser(tenant.getOwner());

    Ticket newTicket = createTicket();
    TicketForm form = new TicketForm(newTicket);
    Assert.assertEquals(newTicket.getStatus(), TicketStatus.NEW);

    for (Profile prof : profile) {
      User user = createUserInTenant(prof, tenant);

      try {
        asUser(user);
        Assert.assertEquals(closeTicket(form, user), "Success");
        controller.listTickets(user.getTenant(), user.getTenant().getParam(), "", false, "", "", "", 1,
            newTicket.getCaseNumber(), map);
        List<Ticket> tickets = (List<Ticket>) map.get("tickets");
        Assert.assertNotNull(tickets);
        Assert.assertEquals(tickets.get(0).getSubject(), newTicket.getSubject());
        Assert.assertEquals(tickets.get(0).getStatus(), TicketStatus.CLOSED);
        Assert.fail(user.getUsername() + "able to close ticket without having Tenant Ticket Management Role");
      } catch (InvalidAjaxRequestException e) {
        Assert.assertEquals(e.getMessage(), new String("Current user cannot update the ticket"));

      }

    }
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testCommentOnTicketByAllUsersForTenant() {

    Profile profile[] = {
        profileService.findProfileByName("Billing Admin"), profileService.findProfileByName("Power User"),
        profileService.findProfileByName("User")
    };

    Tenant tenant = getDefaultTenant();

    asUser(tenant.getOwner());

    Ticket newTicket = createTicket();
    Assert.assertEquals(newTicket.getStatus(), TicketStatus.NEW);

    for (int i = 0; i < profile.length; i++) {
      User user = createUserInTenant(profile[i], tenant);

      try {
        asUser(user);

        String comment = user.getUsername();
        Assert.assertEquals(commentOnTicket(comment, newTicket), "success");

        asUser(tenant.getOwner());
        controller.listTickets(tenant, tenant.getParam(), "", false, "", "", "", 1, newTicket.getCaseNumber(), map);
        List<TicketComment> comments = (List<TicketComment>) map.get("ticketcomments");
        Assert.assertNotNull(comments);
        Assert.assertEquals(comments.get(i).getComment().toString(), comment.toString());

      } catch (InvalidAjaxRequestException e) {
        Assert.assertEquals(e.getMessage(), new String("Current user cannot update the ticket"));
        Assert.fail();

      }

    }
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testCommentOnTicketByPowerUser() {

    Tenant tenant = getDefaultTenant();

    Profile profile = profileService.findProfileByName("Power User");
    User powerUser = createUserInTenant(profile, tenant);

    asUser(powerUser);

    Ticket newTicket = createTicket();
    Assert.assertEquals(newTicket.getStatus(), TicketStatus.NEW);

    try {

      String comment = "Power User Commenting on his Own Ticket";

      Assert.assertEquals(commentOnTicket(comment, newTicket), "success");

      controller.listTickets(powerUser.getTenant(), powerUser.getTenant().getParam(), "", false, "", "", "", 1,
          newTicket.getCaseNumber(), map);
      List<TicketComment> comments = (List<TicketComment>) map.get("ticketcomments");
      Assert.assertNotNull(comments);
      Assert.assertEquals(comments.get(0).getComment(), comment);

    } catch (TicketServiceException e) {
      e.printStackTrace();
      Assert.fail("Power user is not able to Comment on his own Ticket");
    }

  }

  @SuppressWarnings("unchecked")
  @Test
  public void testCommentOnTicketByNormalUser() {

    Tenant tenant = getDefaultTenant();

    Profile profile = profileService.findProfileByName("User");
    User normalUser = createUserInTenant(profile, tenant);

    asUser(normalUser);

    Ticket newTicket = createTicket();
    Assert.assertEquals(newTicket.getStatus(), TicketStatus.NEW);

    try {

      String comment = "Normal User Commenting on his Own Ticket";

      Assert.assertEquals(commentOnTicket(comment, newTicket), "success");

      controller.listTickets(normalUser.getTenant(), normalUser.getTenant().getParam(), "", false, "", "", "", 1,
          newTicket.getCaseNumber(), map);
      List<TicketComment> comments = (List<TicketComment>) map.get("ticketcomments");
      Assert.assertNotNull(comments);
      Assert.assertEquals(comments.get(0).getComment(), comment);

    } catch (TicketServiceException e) {
      e.printStackTrace();
      Assert.fail("Normal user is not able to Comment on his own Ticket");
    }

  }

  @SuppressWarnings("unchecked")
  @Test
  public void testCommentOnTicketByBillingAdmin() {

    Tenant tenant = getDefaultTenant();

    Profile profile = profileService.findProfileByName("Billing Admin");
    User billingAdmin = createUserInTenant(profile, tenant);

    asUser(billingAdmin);

    Ticket newTicket = createTicket();
    Assert.assertEquals(newTicket.getStatus(), TicketStatus.NEW);

    try {

      String comment = "Billing Admin Commenting on his Own Ticket";
      Assert.assertEquals(commentOnTicket(comment, newTicket), "success");

      controller.listTickets(billingAdmin.getTenant(), billingAdmin.getTenant().getParam(), "", false, "", "", "", 1,
          newTicket.getCaseNumber(), map);
      List<TicketComment> comments = (List<TicketComment>) map.get("ticketcomments");
      Assert.assertNotNull(comments);
      Assert.assertEquals(comments.get(0).getComment(), comment);

    } catch (TicketServiceException e) {
      e.printStackTrace();
      Assert.fail("Billing Adminr is not able to Comment on his own Ticket");
    }

  }

  @SuppressWarnings("unchecked")
  @Test
  public void testTicketFilterByTenant() {
    ModelMap map1 = new ModelMap();

    Tenant tenant1 = tenantService.getTenantByParam("name", "1_retail2", false);

    TicketStatus ticketStatus[] = {
        TicketStatus.NEW, TicketStatus.ESCALATED, TicketStatus.WORKING, TicketStatus.CLOSED
    };
    // Creates Tickets
    for (TicketStatus tStatus : ticketStatus) {
      asUser(tenant1.getOwner());

      Ticket newTicket = createTicket();
      TicketForm form = new TicketForm(newTicket);
      Assert.assertEquals(newTicket.getStatus(), TicketStatus.NEW);

      // Changes Status of tickets
      asRoot();
      newTicket.setStatus(tStatus);
      controller.editTicket(tenant1, "", tenant1.getParam(), "", "", newTicket.getCaseNumber(), request, form, map1);
      Assert.assertEquals(newTicket.getStatus(), tStatus);
    }

    asUser(tenant1.getOwner());
    // Check for ALL Filter
    controller.listTicketsPage(tenant, tenant.getParam(), "All", false, "", "", "", map1, request);
    List<Ticket> tickets = (List<Ticket>) map1.get("tickets");
    Assert.assertNotNull(tickets);
    Assert.assertEquals(tickets.size(), 4);

    // Checks for NEW,ESCALTED,WORKING and CLOSED filters
    for (TicketStatus tStatus : ticketStatus) {
      controller.listTicketsPage(tenant, tenant.getParam(), tStatus.getName().toString(), false, "", "", "", map1,
          request);
      tickets = (List<Ticket>) map1.get("tickets");

      Assert.assertNotNull(tickets);
      Assert.assertEquals(tickets.size(), 1);
      Assert.assertEquals(tickets.get(0).getStatus(), tStatus);
    }

  }

  // BugID: DE7492
  @Ignore
  @Test
  public void testVerifyTicketNotication() {

    Tenant tenant = getDefaultTenant();
    // Date date = new Date();
    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    String date = formatter.format(new Date());

    Long oldCount = eventService.getEventsCount(tenant.getOwner(), date);
    asUser(tenant.getOwner());

    Ticket newTicket = createTicket();
    Assert.assertEquals(newTicket.getStatus(), TicketStatus.NEW);
    Long newCount = eventService.getEventsCount(tenant.getOwner(), date);
    Assert.assertEquals(Long.valueOf((oldCount + 1)), newCount);
  }

  @Test
  public void testCreateFormToEmailTicket() {
    Profile profile[] = {
        profileService.findProfileByName("Billing Admin"), profileService.findProfileByName("Power User"),
        profileService.findProfileByName("User")
    };

    Tenant tenant = getDefaultTenant();

    Map<String, MockOSSInstance> ossTypeServiceInstanceMap = mockConnectorUtils.getOssTypeServiceInstanceMap();
    MockOSSInstance crmServiceMock = ossTypeServiceInstanceMap.get("TICKET");

    try {
      MockOSSInstance formToEmailServiceMock = new MockOSSInstance(ticketService, null, null);
      ossTypeServiceInstanceMap.put("TICKET", formToEmailServiceMock);
      setMockHandlersInSupportService();
      for (Profile prof : profile) {
        User user = createUserInTenant(prof, tenant);

        asUser(user);
        Ticket ticket = new Ticket();
        ticket.setDescription("1");
        ticket.setSubject("1");
        ticket.setCategory(Category.WEB);
        ticket.setOwner(user);
        TicketForm form = new TicketForm(ticket);

        int oldEventListSize = eventListener.getEvents().size();
        controller.createFormToEmail(user.getTenant().getParam(), user.getTenant(), request, form, map);
        int newEventListSize = eventListener.getEvents().size();
        Assert.assertEquals(oldEventListSize + 1, newEventListSize);
      }
    } catch (Exception e) {
      Assert.fail(user + "was not able to create a ticket using FormToEmail Connector." + e.getMessage());
    } finally {
      ossTypeServiceInstanceMap.put("TICKET", crmServiceMock);
    }

  }

}

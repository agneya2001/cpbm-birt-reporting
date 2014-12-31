/*
 * Copyright © 2013 Citrix Systems, Inc. You may not use, copy, or modify this file except pursuant to a valid license
 * agreement from Citrix Systems, Inc.
 */
package fragment.web;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FilenameUtils;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import web.WebTestsBaseWithMockConnectors;
import web.support.DispatcherTestServlet;

import com.citrix.cpbm.core.workflow.model.CloudServiceActivationTransaction;
import com.citrix.cpbm.core.workflow.service.BusinessTransactionService;
import com.citrix.cpbm.platform.admin.service.ConnectorConfigurationManager;
import com.citrix.cpbm.platform.admin.service.utils.ServiceInstanceConfiguration;
import com.citrix.cpbm.platform.bootstrap.service.BootstrapActivator;
import com.citrix.cpbm.platform.spi.CloudConnector;
import com.citrix.cpbm.platform.spi.CloudConnectorFactory;
import com.citrix.cpbm.platform.util.CssdkConstants;
import com.citrix.cpbm.portal.fragment.controllers.AbstractConnectorController;
import com.vmops.model.BaseServiceConfigurationMetadata;
import com.vmops.model.Configuration;
import com.vmops.model.Profile;
import com.vmops.model.ProfileAuthority;
import com.vmops.model.Service;
import com.vmops.model.ServiceInstance;
import com.vmops.model.Tenant;
import com.vmops.model.TenantHandle.State;
import com.vmops.model.User;
import com.vmops.persistence.ServiceDAO;
import com.vmops.persistence.ServiceInstanceDao;
import com.vmops.portal.config.Configuration.Names;
import com.vmops.service.ConfigurationService;
import com.vmops.service.ProfileService;
import com.vmops.web.controllers.menu.Page;
import com.vmops.web.forms.ServiceInstanceLogoForm;
import common.MockCloudInstance;

public class AbstractConnectorControllerTest extends WebTestsBaseWithMockConnectors {

  private ModelMap map;

  private final BootstrapActivator bootstrapActivator = new BootstrapActivator();

  @Autowired
  private AbstractConnectorController controller;

  @Autowired
  private ConnectorConfigurationManager connectorConfigurationManager;

  @Autowired
  private ConfigurationService configurationService;

  @Autowired
  private ServiceInstanceDao serviceInstanceDao;

  @Resource(name = "businessTransactionService")
  private BusinessTransactionService businessTransactionService;

  private String validServiceUuid = "";

  private String validServiceInstanceUuid = "";

  private BundleContext bc;

  private ServiceReference<?> mockServiceRef;

  @Autowired
  private ServiceDAO servicedao;

  @Autowired
  private ProfileService profileService;

  private MockHttpServletRequest request;

  private MockHttpServletResponse response;

  @Before
  public void init() throws Exception {
    map = new ModelMap();
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
  }

  @Override
  public void prepareMock() {
    MockCloudInstance mockCloudInstance = this.getMockCloudInstance();
    CloudConnector connector = mockCloudInstance.getCloudConnector();
    EasyMock.expect(connector.getStatus()).andReturn(Boolean.TRUE).anyTimes();
    EasyMock.expect(connector.getServiceInstanceUUID()).andReturn("003fa8ee-fba3-467f-a517-fd806dae8a80").anyTimes();
    EasyMock.replay(connector);

    mockServiceRef = EasyMock.createMock(ServiceReference.class);
    bc = EasyMock.createMock(BundleContext.class);
    try {
      bootstrapActivator.start(bc);
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  private void setValidServiceAndServiceInstance() throws Exception {
    Boolean instanceFound = false;
    // finding valid Service and ServiceInstance
    for (Service service : connectorConfigurationManager.getAllServicesByType("OSS")) {
      if (!service.getServiceInstances().isEmpty()) {
        for (ServiceInstance serviceInstance : service.getServiceInstances()) {
          validServiceUuid = service.getUuid();
          validServiceInstanceUuid = serviceInstance.getUuid();
          instanceFound = true;
          break;
        }
        if (instanceFound) {
          break;
        }
      }
    }
  }

  /**
   * This tests whether the correct url mapping is redirected to respecting controller
   * 
   * @throws Exception
   */
  @Test
  public void testRouting() throws Exception {
    logger.debug("Testing routing....");
    DispatcherTestServlet servlet = this.getServletInstance();
    Class<? extends AbstractConnectorController> controllerClass = controller.getClass();
    Method expected = locateMethod(controllerClass, "getHandleState", new Class[] {
        Tenant.class, String.class, String.class
    });
    Method handler = servlet.recognize(getRequestTemplate(HttpMethod.GET, "/connector/getHandleState"));
    Assert.assertEquals(expected, handler);
  }

  @Test
  public void testShowOSSServices() throws Exception {
    map = new ModelMap();
    setValidServiceAndServiceInstance();
    String viewService = controller.showOSSServices(validServiceUuid, "", "view", map);
    Assert.assertTrue(map.containsAttribute("viewServiceDetails"));
    Assert.assertEquals(map.get("page"), Page.HOME_CONNECTORS_OSS);
    Assert.assertEquals("main.home_oss.instance.edit", viewService);

    map = new ModelMap();
    String confService = controller.showOSSServices(validServiceUuid, "", "", map);
    Assert.assertTrue(map.containsAttribute("service_config_properties"));
    Assert.assertEquals(map.get("page"), Page.HOME_CONNECTORS_OSS);
    Assert.assertEquals("main.home_oss.instance.edit", confService);

    map = new ModelMap();
    String viewServiceInstance = controller.showOSSServices("", validServiceInstanceUuid, "view", map);
    Assert.assertTrue(map.containsAttribute("instance"));
    Assert.assertTrue(map.containsAttribute("service"));
    Assert.assertTrue(map.containsAttribute("instance_properties"));
    Assert.assertEquals(map.get("page"), Page.HOME_CONNECTORS_OSS);
    Assert.assertEquals("main.home_oss.instance.edit", viewServiceInstance);

    /*
     * map = new ModelMap(); String viewOSSServices = controller.showOSSServices("", "", "view", map);
     * Assert.assertTrue(map.containsAttribute("instance")); Assert.assertTrue(map.containsAttribute("service"));
     * Assert.assertEquals(map.get("page"), Page.HOME_CONNECTORS_OSS); Assert.assertEquals("main.home_connector_oss",
     * viewOSSServices);
     */
  }

  @Test
  public void testShowCloudServices() throws Exception {
    map = new ModelMap();
    setValidServiceAndServiceInstance();
    String viewService = controller.showCloudServices(validServiceUuid, "", "view", map);
    Assert.assertTrue(map.containsAttribute("viewServiceDetails"));
    Assert.assertEquals(map.get("page"), Page.HOME_CONNECTORS_CS);
    Assert.assertEquals("main.home_service.details", viewService);

    map = new ModelMap();
    String viewServiceInstance = controller.showCloudServices("", validServiceInstanceUuid, "view", map);
    Assert.assertTrue(map.containsAttribute("instance"));
    Assert.assertTrue(map.containsAttribute("service"));
    Assert.assertTrue(map.containsAttribute("instance_properties"));
    Assert.assertEquals("main.home_cs.instance.edit", viewServiceInstance);

    map = new ModelMap();
    String viewCloudServices = controller.showCloudServices("", "", "view", map);
    Assert.assertTrue(map.containsAttribute("categories"));
    Assert.assertTrue(map.containsAttribute("services"));
    Assert.assertEquals(map.get("page"), Page.HOME_CONNECTORS_CS);
    Assert.assertEquals("main.home_connector_cs_admin", viewCloudServices);

  }

  @Test
  public void testSaveInstance() throws Exception {
    map = new ModelMap();
    String classType = "com.citrix.cpbm.platform.spi.CloudServiceConnector";
    String filter = "(org.springframework.osgi.bean.name=OsgiServiceRef1)";
    prepareMock(classType, filter, true);
    String configProperties = "[{\"name\":\"instancename\",\"value\":\"MyCPIN\"},{\"name\":\"instancecode\",\"value\":\"InstanceCode-999\"},{\"name\":\"instancedescription\",\"value\":\"Test\"},{\"name\":\"country\",\"value\":\"India\"},{\"name\":\"password\",\"value\":\"password\"},{\"name\":\"accountid\",\"value\":\"456783\"},{\"name\":\"username\",\"value\":\"cpbm\"},{\"name\":\"endpoint\",\"value\":\"http://google.com\"}]";
    map = controller.saveInstance("b1c9fbb0-8dab-42dc-ae0a-ce1384a1e6", "add", configProperties, null, map,
        new MockHttpServletRequest());
    Assert.assertTrue(map.containsAttribute("validationResult"));
    Assert.assertTrue(map.containsAttribute("instanceid"));
    Assert.assertTrue(map.containsAttribute("result"));
    EasyMock.reset(mockServiceRef, bc);
  }

  @Test
  public void testSaveOSSInstance() throws Exception {
    map = new ModelMap();
    String classType = "com.citrix.cpbm.platform.spi.CloudServiceConnector";
    String filter = "(org.springframework.osgi.bean.name=paymentgateway)";
    prepareMock(classType, filter, true);
    String configProperties = "[{\"name\":\"merchantName\",\"value\":\"citrix_test\"},{\"name\":\"transactionKey\",\"value\":\"uh3Kajm4nIzTZs/42Mko7S5r5Ec+Qk/Kj3pG7NY3TmpZ3XtsY1U0hlaJV\"},"
        + "{\"name\":\"serverurl\",\"value\":\"https://ics2wstest.ic3.com/commerce/1.x/transactionProcessor\"}]";
    map = controller.saveInstance("gc3c6f30-a44a-4754-a8cc-ty597e0a129", "add", configProperties, null, map,
        new MockHttpServletRequest());
    Assert.assertTrue(map.containsAttribute("validationResult"));
    Assert.assertTrue(map.containsAttribute("instanceid"));
    Assert.assertTrue(map.containsAttribute("result"));
    EasyMock.reset(mockServiceRef, bc);
  }

  @Test
  public void testSaveInstanceWithoutCode() throws Exception {
    map = new ModelMap();
    String classType = "com.citrix.cpbm.platform.spi.CloudServiceConnector";
    String filter = "(org.springframework.osgi.bean.name=OsgiServiceRef1)";
    prepareMock(classType, filter, true);
    String configProperties = "[{\"name\":\"instancename\",\"value\":\"MyCPIN\"},{\"name\":\"instancedescription\",\"value\":\"Test\"},{\"name\":\"country\",\"value\":\"India\"},{\"name\":\"password\",\"value\":\"password\"},{\"name\":\"accountid\",\"value\":\"456783\"},{\"name\":\"username\",\"value\":\"cpbm\"},{\"name\":\"endpoint\",\"value\":\"http://google.com\"}]";
    map = controller.saveInstance("b1c9fbb0-8dab-42dc-ae0a-ce1384a1e6", "add", configProperties, null, map,
        new MockHttpServletRequest());
    EasyMock.reset(mockServiceRef, bc);
  }

  @Test
  public void testSaveInstanceInvalidJson() throws Exception {
    map = new ModelMap();
    String classType = "com.citrix.cpbm.platform.spi.CloudServiceConnector";
    String filter = "(org.springframework.osgi.bean.name=OsgiServiceRef1)";
    prepareMock(classType, filter, true);
    String configProperties = "[{\"name\":\"instancename\",\"value\":\"MyCPIN\",{\"name\":\"instancedescription\",\"value\":\"Test\"},{\"name\":\"country\",\"value\":\"India\"},{\"name\":\"password\",\"value\":\"password\"},{\"name\":\"accountid\",\"value\":\"456783\"},{\"name\":\"username\",\"value\":\"cpbm\"},{\"name\":\"endpoint\",\"value\":\"http://google.com\"}]";
    map = controller.saveInstance("b1c9fbb0-8dab-42dc-ae0a-ce1384a1e6", "add", configProperties, null, map,
        new MockHttpServletRequest());
    EasyMock.reset(mockServiceRef, bc);
  }

  @Test
  public void testUpdateInstance() throws Exception {
    map = new ModelMap();
    String classType = "com.citrix.cpbm.platform.spi.CloudServiceConnector";
    String filter = "(org.springframework.osgi.bean.name=OsgiServiceRef1)";
    prepareMock(classType, filter, true);
    String configProperties = "[{\"name\":\"instancename\",\"value\":\"MyCPIN\"},{\"name\":\"instancecode\",\"value\":\"InstanceCode-999\"},{\"name\":\"instancedescription\",\"value\":\"Test\"},{\"name\":\"country\",\"value\":\"India\"},{\"name\":\"password\",\"value\":\"password\"},{\"name\":\"accountid\",\"value\":\"456783\"},{\"name\":\"username\",\"value\":\"cpbm\"},{\"name\":\"endpoint\",\"value\":\"http://google.com\"}]";
    map = controller.saveInstance("4847df70-63bb-4273-a8db-30662b32d098", "update", configProperties, null, map,
        new MockHttpServletRequest());
    Assert.assertTrue(map.containsAttribute("validationResult"));
    Assert.assertTrue(map.containsAttribute("instanceid"));
    Assert.assertTrue(map.containsAttribute("result"));
  }

  @Test
  public void testviewInstance() {
    map = new ModelMap();
    String classType = "com.citrix.cpbm.platform.spi.CloudServiceConnector";
    String filter = "(org.springframework.osgi.bean.name=OsgiServiceRef1)";
    prepareMock(classType, filter, true);
    map = controller.viewInstance("b1c9fbb0-8dab-42dc-ae0a-ce1384a1e6", map);
    Assert.assertTrue(map.containsAttribute("instances"));
  }

  @Test
  public void testEnable() throws Exception {
    map = new ModelMap();
    String classType = "com.citrix.cpbm.platform.spi.CloudServiceConnector";
    String filter = "(org.springframework.osgi.bean.name=salesForceTicketingConnector)";
    prepareMock(classType, filter, true);
    map = controller.enable("d3577427-d837-4def-ae1f-8569fa518da5", true, map, new MockHttpServletRequest());
    Assert.assertTrue(map.containsAttribute("enabled"));
    Assert.assertEquals(true, map.get("enabled"));
    Assert.assertEquals("success", map.get("result"));
  }

  @SuppressWarnings("rawtypes")
  private void prepareMock(String classType, String filter, boolean adaptor) {
    CloudConnectorFactory mcc = null;

    CloudConnector cc = EasyMock.createMock(CloudConnector.class);

    if (adaptor) {
      mcc = EasyMock.createMock(CloudConnectorFactory.class);
    }
    EasyMock.reset(bc);
    EasyMock.reset(mockServiceRef);
    EasyMock.reset(cc);
    EasyMock.reset(mcc);
    try {
      EasyMock.expect(bc.getServiceReferences(classType, filter)).andReturn(new ServiceReference[] {
        mockServiceRef
      }).anyTimes();
    } catch (InvalidSyntaxException e) {
      e.printStackTrace();
    }
    EasyMock.expect(mockServiceRef.getProperty(Constants.SERVICE_ID)).andReturn(new Long(2)).anyTimes();
    EasyMock.expect(mockServiceRef.getProperty(Constants.SERVICE_RANKING)).andReturn(new Integer(1)).anyTimes();
    EasyMock.expect((Object) bc.getService(mockServiceRef)).andReturn(mcc).anyTimes();
    EasyMock.expect(mcc.initialize(EasyMock.<ServiceInstanceConfiguration> anyObject())).andReturn(cc).anyTimes();

    EasyMock.replay(bc);
    EasyMock.replay(mockServiceRef);
    EasyMock.replay(cc);
    EasyMock.replay(mcc);
  }

  @Test
  public void testGetDefaultServiceValues() {
    map = new ModelMap();
    controller.showCloudServices("fc3c6f30-a44a-4754-a8cc-9cea97e0a129", null, "add", map);
    @SuppressWarnings("unchecked")
    List<BaseServiceConfigurationMetadata> propertiesList = (List<BaseServiceConfigurationMetadata>) map
        .get("service_config_properties");
    for (BaseServiceConfigurationMetadata scm : propertiesList) {
      if (scm.getName().equals("publicProtocol")) {
        Assert.assertEquals("http", scm.getDefaultVal());
      }
      if (scm.getName().equals("publicPort")) {
        Assert.assertEquals("8080", scm.getDefaultVal());
      }
      if (scm.getName().equals("cloud.jdbc.username")) {
        Assert.assertEquals("cloud", scm.getDefaultVal());
      }
      if (scm.getName().equals("cloud.jdbc.database.schemaname")) {
        Assert.assertEquals("cloud", scm.getDefaultVal());
      }
      if (scm.getName().equals("cloud.usage.jdbc.username")) {
        Assert.assertEquals("cloud", scm.getDefaultVal());
      }
      if (scm.getName().equals("cloud.usage.jdbc.database.schemaname")) {
        Assert.assertEquals("cloud_usage", scm.getDefaultVal());
      }
    }
  }

  @Test
  public void testShowCloudServicesAsRoot() throws Exception {
    try {
      map = new ModelMap();
      Tenant tenant = tenantDAO.find(2L);
      asRoot();
      setValidServiceAndServiceInstance();
      request.setAttribute("isSurrogatedTenant", true);
      String viewService = controller.showCloudServices(tenant.getUuid(), null, null, map, request);
      Map<String, Boolean> provisioningmap = ((Map<String, Boolean>) map.get("serviceInstanceProvisioningMap"));
      Assert.assertEquals(1, provisioningmap.size());
      String uuid = (String) provisioningmap.keySet().toArray()[0];
      Assert.assertEquals(true, (boolean) provisioningmap.get(uuid));
      Assert.assertEquals(tenant, map.get("tenant"));
      List<Service> services = connectorConfigurationManager.getAllServicesByType(CssdkConstants.CLOUD);
      Assert.assertEquals(tenant.getOwner(), map.get("effectiveUser"));
      Assert.assertEquals("company_setup.connector_cs_admin", viewService);
      Assert.assertEquals(services.toString(), map.get("services").toString());
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
  }

  @Test
  public void testShowCloudServicesAsServiceProvider() throws Exception {
    try {
      map = new ModelMap();
      String[] profiles = {
          "3", "4", "5", "6", "7"
      };
      for (String profile : profiles) {
        User user = userDAO.find("3");
        Tenant tenant = tenantDAO.find(3L);
        user.setProfile(profileDAO.find(profile));
        userDAO.save(user);
        asUser(user);
        setValidServiceAndServiceInstance();
        request.setAttribute("isSurrogatedTenant", true);
        String viewService = controller.showCloudServices(tenant.getUuid(), null, null, map, request);
        Assert.assertEquals(tenant, map.get("tenant"));
        List<Service> services = connectorConfigurationManager.getAllServicesByType(CssdkConstants.CLOUD);
        Assert.assertEquals(tenant.getOwner(), map.get("effectiveUser"));
        Assert.assertEquals("company_setup.connector_cs_admin", viewService);
        Assert.assertEquals(services.toString(), map.get("services").toString());
      }
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }

  }

  @Test
  public void testShowCloudServicesForRoot() throws Exception {
    try {
      map = new ModelMap();
      Tenant tenant = tenantDAO.find(1L);
      setValidServiceAndServiceInstance();
      String viewService = controller.showCloudServices(tenant.getUuid(), null, null, map, request);
      Assert.assertEquals("redirect:/portal/home", viewService);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
  }

  @Test
  public void testUploadServiceInstanceLogo() throws Exception {

    Configuration configuration = configurationService
        .locateConfigurationByName(Names.com_citrix_cpbm_portal_settings_images_uploadPath);
    configuration.setValue("src\\test\\resources");
    configurationService.update(configuration);
    ServiceInstance serviceInstance = serviceInstanceDao.find(1L);
    Assert.assertEquals("poli.jpg", serviceInstance.getImagePath());
    MultipartFile logo = new MockMultipartFile("ServiceInstanceLogo.jpeg", "ServiceInstanceLogo.jpeg", "byte",
        "ServiceInstance".getBytes());
    ServiceInstanceLogoForm form = new ServiceInstanceLogoForm(serviceInstance);
    form.setLogo(logo);
    BindingResult result = validate(form);
    HttpServletRequest mockRequest = new MockHttpServletRequest();
    map = new ModelMap();
    ResponseEntity<String> resultString = controller.uploadServiceInstanceLogo(form, result, mockRequest, map);
    Assert.assertNotNull(resultString);
    Assert.assertEquals("success", resultString.getBody());
    serviceInstance = serviceInstanceDao.find(1L);
    Assert.assertEquals(FilenameUtils.separatorsToSystem("serviceInstance\\1\\ServiceInstanceLogo.jpeg"),
        serviceInstance.getImagePath());
  }

  @Test
  public void testUploadServiceInstanceLogoInvalidFile() throws Exception {

    Configuration configuration = configurationService
        .locateConfigurationByName(Names.com_citrix_cpbm_portal_settings_images_uploadPath);
    configuration.setValue("src\\test\\resources");
    configurationService.update(configuration);
    ServiceInstance serviceInstance = serviceInstanceDao.find(1L);
    Assert.assertEquals("poli.jpg", serviceInstance.getImagePath());
    MultipartFile logo = new MockMultipartFile("ServiceInstanceLogo.jpeg", "ServiceInstanceLogo.jpe", "byte",
        "ServiceInstance".getBytes());
    ServiceInstanceLogoForm form = new ServiceInstanceLogoForm(serviceInstance);
    form.setLogo(logo);
    BindingResult result = validate(form);
    HttpServletRequest mockRequest = new MockHttpServletRequest();
    map = new ModelMap();
    ResponseEntity<String> resultString = controller.uploadServiceInstanceLogo(form, result, mockRequest, map);
    Assert.assertNotNull(resultString);
    String error = messageSource.getMessage(result.getFieldError("logo").getCode(), null, mockRequest.getLocale());
    Assert.assertEquals(error, resultString.getBody());
    serviceInstance = serviceInstanceDao.find(1L);
    Assert.assertEquals("poli.jpg", serviceInstance.getImagePath());
  }

  @Test
  public void testUploadServiceInstanceLogoNullDirectoryPath() throws Exception {

    Configuration configuration = configurationService
        .locateConfigurationByName(Names.com_citrix_cpbm_portal_settings_images_uploadPath);
    configuration.setValue(null);
    configurationService.update(configuration);

    ServiceInstance serviceInstance = serviceInstanceDao.find(1L);
    Assert.assertEquals("poli.jpg", serviceInstance.getImagePath());
    MultipartFile logo = new MockMultipartFile("ServiceInstanceLogo.jpeg", "ServiceInstanceLogo.jpeg", "byte",
        "ServiceInstance".getBytes());
    ServiceInstanceLogoForm form = new ServiceInstanceLogoForm(serviceInstance);
    form.setLogo(logo);
    BindingResult result = validate(form);
    HttpServletRequest mockRequest = new MockHttpServletRequest();
    map = new ModelMap();
    ResponseEntity<String> resultString = controller.uploadServiceInstanceLogo(form, result, mockRequest, map);
    Assert.assertNotNull(resultString);
    String error = messageSource.getMessage(result.getFieldError("logo").getCode(), null, mockRequest.getLocale());
    Assert.assertEquals(error, resultString.getBody());
    serviceInstance = serviceInstanceDao.find(1L);
    Assert.assertEquals("poli.jpg", serviceInstance.getImagePath());
  }

  @Test
  public void testSaveInstanceWithProducts() {
    map = new ModelMap();
    asRoot();
    Service service = servicedao.find("6");
    int serviceInstancesCount = serviceInstanceDao.count();
    int productListCount = productDAO.count();
    String configProperties = "[{\"name\":\"instancename\",\"value\":\"anusha-CS\"},{\"name\":\"instancecode\",\"value\":\"anusha-CS\"},{\"name\":\"publicProtocol\",\"value\":\"http\"},{\"name\":\"publicHost\",\"value\":\"10.102.153.119\"},{\"name\":\"publicPort\",\"value\":\"8080\"},{\"name\":\"ssoKey\",\"value\":\"4bUudGCm3lAFf54EbgMRAE7b_LAdhs4MO4M8v-uvA1uEo9D1zD6eFauAtBJRrabCcLCg_uqXE-OjTMc1EeNcEA\"},{\"name\":\"apiKey\",\"value\":\"pmHmI9h5rEQdcl34Tgi7crx5DjTQs-5vR6vvwdO4F_Jsw0tKgMu2X0bALYKZYMjh9qoXG4Q0UAacNJR7vLvDcw\"},{\"name\":\"secretKey\",\"value\":\"0eAYlinSBnmBnM7RED1MRzfsC5Wnoa3199WVaF-3nVh9vFioHLXvwyDoGm3SLaVdRbOopM4CKxKBbFat44c9QA\"},{\"name\":\"parentDomainId\",\"value\":\"1\"},{\"name\":\"apiProxySuffix\",\"value\":\"ccpapi\"},{\"name\":\"cloud.jdbc.host\",\"value\":\"10.102.153.119\"},{\"name\":\"cloud.jdbc.username\",\"value\":\"cloud\"},{\"name\":\"cloud.jdbc.password\",\"value\":\"cloud\"},{\"name\":\"cloud.jdbc.database.schemaname\",\"value\":\"cloud\"},{\"name\":\"cloud.usage.jdbc.host\",\"value\":\"10.102.153.119\"},{\"name\":\"cloud.usage.jdbc.username\",\"value\":\"cloud\"},{\"name\":\"cloud.usage.jdbc.password\",\"value\":\"cloud_usage\"},{\"name\":\"cloud.usage.jdbc.database.schemaname\",\"value\":\"cloud_usage\"},{\"name\":\"adminServerList\",\"value\":\"10.102.153.119:8096\"},{\"name\":\"nonAdminServerList\",\"value\":\"10.102.153.119:8080\"},{\"name\":\"apiWhitelist\",\"value\":\"\"},{\"name\":\"apiBlacklist\",\"value\":\"\"},{\"name\":\"default.vm.locale\",\"value\":\"us\"},{\"name\":\"max.custom.disk.offering.size\",\"value\":\"1024\"},{\"name\":\"instancedescription\",\"value\":\"Instance Desc\"}]";
    String quickProducts = "[{\"name\":\"RUNNING_VM\",\"code\":\"anusha-CSRUNNING_VM\",\"scale\":\"1.0000000000\",\"uom\":\"Compute-Hours\",\"category\":\"1\",\"usageTypeId\":\"31\",\"createdBy\":\"1\",\"price\":[{\"currencyCode\":\"USD\",\"currencyVal\":\"12\"}]},{\"name\":\"ALLOCATED_VM\",\"code\":\"anusha-CSALLOCATED_VM\",\"scale\":\"1.0000000000\",\"uom\":\"Compute-Hours\",\"category\":\"1\",\"usageTypeId\":\"32\",\"createdBy\":\"1\",\"price\":[{\"currencyCode\":\"USD\",\"currencyVal\":\"15\"}]}]";

    controller.saveInstance(service.getUuid(), "add", configProperties, quickProducts, map, request);
    Assert.assertEquals(serviceInstancesCount + 1, serviceInstanceDao.count());
    Assert.assertEquals(productListCount + 2, productDAO.count());
    Assert.assertEquals(CssdkConstants.SUCCESS, map.get("validationResult"));
  }

  @Test
  public void testSaveInstanceWithoutProducts() {
    map = new ModelMap();
    asRoot();
    Service service = servicedao.find("6");
    int serviceInstancesCount = serviceInstanceDao.count();
    int productListCount = productDAO.count();
    String configProperties = "[{\"name\":\"instancename\",\"value\":\"anusha-CS\"},{\"name\":\"instancecode\",\"value\":\"anusha-CS\"},{\"name\":\"publicProtocol\",\"value\":\"http\"},{\"name\":\"publicHost\",\"value\":\"10.102.153.119\"},{\"name\":\"publicPort\",\"value\":\"8080\"},{\"name\":\"ssoKey\",\"value\":\"4bUudGCm3lAFf54EbgMRAE7b_LAdhs4MO4M8v-uvA1uEo9D1zD6eFauAtBJRrabCcLCg_uqXE-OjTMc1EeNcEA\"},{\"name\":\"apiKey\",\"value\":\"pmHmI9h5rEQdcl34Tgi7crx5DjTQs-5vR6vvwdO4F_Jsw0tKgMu2X0bALYKZYMjh9qoXG4Q0UAacNJR7vLvDcw\"},{\"name\":\"secretKey\",\"value\":\"0eAYlinSBnmBnM7RED1MRzfsC5Wnoa3199WVaF-3nVh9vFioHLXvwyDoGm3SLaVdRbOopM4CKxKBbFat44c9QA\"},{\"name\":\"parentDomainId\",\"value\":\"1\"},{\"name\":\"apiProxySuffix\",\"value\":\"ccpapi\"},{\"name\":\"cloud.jdbc.host\",\"value\":\"10.102.153.119\"},{\"name\":\"cloud.jdbc.username\",\"value\":\"cloud\"},{\"name\":\"cloud.jdbc.password\",\"value\":\"cloud\"},{\"name\":\"cloud.jdbc.database.schemaname\",\"value\":\"cloud\"},{\"name\":\"cloud.usage.jdbc.host\",\"value\":\"10.102.153.119\"},{\"name\":\"cloud.usage.jdbc.username\",\"value\":\"cloud\"},{\"name\":\"cloud.usage.jdbc.password\",\"value\":\"cloud_usage\"},{\"name\":\"cloud.usage.jdbc.database.schemaname\",\"value\":\"cloud_usage\"},{\"name\":\"adminServerList\",\"value\":\"10.102.153.119:8096\"},{\"name\":\"nonAdminServerList\",\"value\":\"10.102.153.119:8080\"},{\"name\":\"apiWhitelist\",\"value\":\"\"},{\"name\":\"apiBlacklist\",\"value\":\"\"},{\"name\":\"default.vm.locale\",\"value\":\"us\"},{\"name\":\"max.custom.disk.offering.size\",\"value\":\"1024\"},{\"name\":\"instancedescription\",\"value\":\"Instance Desc\"}]";
    String quickProducts = null;

    controller.saveInstance(service.getUuid(), "add", configProperties, quickProducts, map, request);
    Assert.assertEquals(serviceInstancesCount + 1, serviceInstanceDao.count());
    Assert.assertEquals(productListCount, productDAO.count());
    Assert.assertEquals(CssdkConstants.SUCCESS, map.get("validationResult"));

  }

  @Test
  public void testSaveInstanceWithoutMandatoryConfigurations() {
    map = new ModelMap();
    asRoot();
    Service service = servicedao.find("6");
    int serviceInstancesCount = serviceInstanceDao.count();
    String configProperties = "[{\"name\":\"instancename\",\"value\":\"anusha-CS\"},{\"name\":\"instancecode\",\"value\":\"anusha-CS\"},{\"name\":\"publicProtocol\",\"value\":\"http\"},{\"name\":\"publicHost\",\"value\":\"\"},{\"name\":\"publicPort\",\"value\":\"8080\"},{\"name\":\"ssoKey\",\"value\":\"4bUudGCm3lAFf54EbgMRAE7b_LAdhs4MO4M8v-uvA1uEo9D1zD6eFauAtBJRrabCcLCg_uqXE-OjTMc1EeNcEA\"},{\"name\":\"apiKey\",\"value\":\"pmHmI9h5rEQdcl34Tgi7crx5DjTQs-5vR6vvwdO4F_Jsw0tKgMu2X0bALYKZYMjh9qoXG4Q0UAacNJR7vLvDcw\"},{\"name\":\"secretKey\",\"value\":\"0eAYlinSBnmBnM7RED1MRzfsC5Wnoa3199WVaF-3nVh9vFioHLXvwyDoGm3SLaVdRbOopM4CKxKBbFat44c9QA\"},{\"name\":\"parentDomainId\",\"value\":\"1\"},{\"name\":\"apiProxySuffix\",\"value\":\"ccpapi\"},{\"name\":\"cloud.jdbc.host\",\"value\":\"10.102.153.119\"},{\"name\":\"cloud.jdbc.username\",\"value\":\"cloud\"},{\"name\":\"cloud.jdbc.password\",\"value\":\"cloud\"},{\"name\":\"cloud.jdbc.database.schemaname\",\"value\":\"cloud\"},{\"name\":\"cloud.usage.jdbc.host\",\"value\":\"10.102.153.119\"},{\"name\":\"cloud.usage.jdbc.username\",\"value\":\"cloud\"},{\"name\":\"cloud.usage.jdbc.password\",\"value\":\"cloud_usage\"},{\"name\":\"cloud.usage.jdbc.database.schemaname\",\"value\":\"cloud_usage\"},{\"name\":\"adminServerList\",\"value\":\"10.102.153.119:8096\"},{\"name\":\"nonAdminServerList\",\"value\":\"10.102.153.119:8080\"},{\"name\":\"apiWhitelist\",\"value\":\"\"},{\"name\":\"apiBlacklist\",\"value\":\"\"},{\"name\":\"default.vm.locale\",\"value\":\"us\"},{\"name\":\"max.custom.disk.offering.size\",\"value\":\"1024\"},{\"name\":\"instancedescription\",\"value\":\"Instance Desc\"}]";
    String quickProducts = null;

    controller.saveInstance(service.getUuid(), "add", configProperties, quickProducts, map, request);
    Assert.assertEquals(serviceInstancesCount, serviceInstanceDao.count());
    Assert.assertEquals("publicHost is required.", map.get("validationResult"));
  }

  @Test
  public void testEnableService() {

    Configuration config = configurationService
        .locateConfigurationByName("com.citrix.cpbm.portal.settings.services.datapath");
    config.setValue("src\\test\\resources\\");
    Profile opsProfile = profileService.findProfileByName("Ops Admin");
    List<ProfileAuthority> beforeAuthorityList = opsProfile.getAuthorityList();
    int beforeAuthorityListSize = beforeAuthorityList.size();
    Service service = servicedao.find("6");
    String profileDetails = "[{\"profileid\":\"2\",\"roles\":[]},{\"profileid\":\"3\",\"roles\":[\"ROLE_CLOUD_MANAGEMENT\"]},{\"profileid\":\"4\",\"roles\":[]},{\"profileid\":\"5\",\"roles\":[]},{\"profileid\":\"6\",\"roles\":[]},{\"profileid\":\"7\",\"roles\":[]},{\"profileid\":\"8\",\"roles\":[]},{\"profileid\":\"9\",\"roles\":[]},{\"profileid\":\"10\",\"roles\":[\"ROLE_USER_CLOUD_MANAGEMENT\"]},{\"profileid\":\"11\",\"roles\":[\"ROLE_ACCOUNT_CLOUD_MANAGEMENT\",\"ROLE_USER_CLOUD_MANAGEMENT\"]}]";
    String result = controller.enableService(service.getUuid(), profileDetails, map);
    Assert.assertNotNull(result);
    Assert.assertEquals("success", result);
    opsProfile = profileService.findProfileByName("Ops Admin");
    List<ProfileAuthority> afterAuthorityList = opsProfile.getAuthorityList();
    int afterAuthorityListSize = afterAuthorityList.size();
    Assert.assertEquals(beforeAuthorityListSize + 1, afterAuthorityListSize);
  }

  @Test
  public void testEnableServiceGet() {
    map = new ModelMap();
    Service service = servicedao.find("6");
    Configuration config = configurationService
        .locateConfigurationByName("com.citrix.cpbm.portal.settings.services.datapath");
    config.setValue("src\\test\\resources\\");
    MockHttpServletRequest mockRequest = new MockHttpServletRequest();
    mockRequest.setParameter("lang", "de");
    String result = controller.enableService(service.getUuid(), map, mockRequest);
    Assert.assertEquals("main.home_connector_enable", result);

    mockRequest.setParameter("lang", "en");
    result = controller.enableService(service.getUuid(), map, mockRequest);
    Assert.assertEquals("main.home_connector_enable", result);
  }

  @Test
  public void testIsAlive() {
    map = new ModelMap();
    boolean result = controller.isAlive("003fa8ee-fba3-467f-a517-ed806dae8a88", map);
    Assert.assertEquals(result, false);
  }

  @Test
  public void testfetchAccountConfigurationsParams() {
    map = new ModelMap();
    String result = controller.fetchAccountConfigurationsParams("003fa8ee-fba3-467f-a517-fd806dae8a80",
        "51e89159-9257-4340-8396-944658ba2e4a", map, request);
    Assert.assertEquals("enable.service", result);
  }

  @Test
  public void testloadPackagedJspInConnectors() {
    map = new ModelMap();
    controller.loadPackagedJspInConnector("003fa8ee-fba3-467f-a517-fd806dae8a80", response);
  }

  @Test
  public void uploadServiceInstanceLogo() {
    map = new ModelMap();
    controller.uploadServiceInstanceLogo("003fa8ee-fba3-467f-a517-fd806dae8a80", map);
  }

  @Test
  public void getServiceInstanceList() {
    map = new ModelMap();
    Tenant t = tenantService.getSystemTenant();
    request.setAttribute("isSurrogatedTenant", true);
    controller.getServiceInstanceList(t, "51e89159-9257-4340-8396-944658ba2e4a", true, "IAAS", request);
  }

  // Test Connector Tiles are getting shown in CPBM
  @SuppressWarnings("rawtypes")
  @Test
  @DirtiesContext
  public void testfetchAccountConfigurationsParamsForCustomAccountConfigEditor() {
    map = new ModelMap();
    ServiceInstance serviceInstance = serviceInstanceDao.find(1L);
    ConnectorConfigurationManager configurationManager = EasyMock.createMock(ConnectorConfigurationManager.class);
    ReflectionTestUtils.setField(controller, "connectorConfigurationManager", configurationManager);
    EasyMock.expect(configurationManager.getInstance(serviceInstance.getUuid())).andReturn(serviceInstance).anyTimes();
    EasyMock.expect(configurationManager.getJspPath(serviceInstance.getService())).andReturn("customTiles").anyTimes();
    EasyMock.replay(configurationManager);
    String result = controller.fetchAccountConfigurationsParams(serviceInstance.getUuid(),
        "51e89159-9257-4340-8396-944658ba2e4a", map, request);
    Assert.assertEquals("enable.service", result);
    Assert.assertEquals("customTiles", map.get("accountConfigEditor"));
    Assert.assertEquals("{\"id\":123456}", ((Map) map.get("accountConfigurationData")).toString());
  }

  // Test Connector Tiles are getting not shown in CPBM
  @Test
  @DirtiesContext
  public void testfetchAccountConfigurationsParamsWithoutEditor() {
    map = new ModelMap();
    ServiceInstance serviceInstance = serviceInstanceDao.find(1L);
    ConnectorConfigurationManager configurationManager = EasyMock.createMock(ConnectorConfigurationManager.class);
    ReflectionTestUtils.setField(controller, "connectorConfigurationManager", configurationManager);
    EasyMock.expect(configurationManager.getInstance(serviceInstance.getUuid())).andReturn(serviceInstance).anyTimes();
    EasyMock.expect(configurationManager.getJspPath(serviceInstance.getService())).andReturn(null).anyTimes();
    EasyMock.replay(configurationManager);
    String result = controller.fetchAccountConfigurationsParams(serviceInstance.getUuid(),
        "51e89159-9257-4340-8396-944658ba2e4a", map, request);
    Assert.assertEquals("enable.service", result);
    Assert.assertEquals(map.get("tnc"), "");
    Assert.assertNull(map.get("accountConfigEditor"));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testShowCloudServicesWfDetails() throws Exception {
    Tenant tenant = tenantService.get("CF319413-5DD7-4040-81FE-E2B1BBCF57F6");
    ServiceInstance serviceInstance = connectorManagementService.getInstance("003fa8ee-fba3-467f-a517-fd806dae8a80");
    businessTransactionService.save(new CloudServiceActivationTransaction(serviceInstance, tenant, "{}", true));
    asUser(tenant.getOwner());
    request.setAttribute("isSurrogatedTenant", false);
    controller.showCloudServices(tenant.getUuid(), null, "003fa8ee-fba3-467f-a517-fd806dae8a80", map, request);
    Assert.assertTrue(map.containsAttribute("serviceInstanceWfMap"));
    Map<ServiceInstance, Map<String, String>> serviceInstanceWfMap = (Map<ServiceInstance, Map<String, String>>) map
        .get("serviceInstanceWfMap");
    Assert.assertTrue(serviceInstanceWfMap.containsKey(serviceInstance));
    Map<String, String> wfDetails = serviceInstanceWfMap.get(serviceInstance);
    Assert.assertTrue(wfDetails.containsKey("hasWfInRunning"));
    Assert.assertEquals("true", wfDetails.get("hasWfInRunning"));
  }

  /**
   * Tests the getHandleState controller to verify whether its returning appropriate states given the tenant and service
   * instance
   */
  @Test
  public void testGetHandleState() {
    Tenant tenant = createTenantWithOwner();
    ServiceInstance serviceInstance = serviceInstanceDao.find(1L);
    String handleState = controller.getHandleState(tenant, tenant.getUuid(), serviceInstance.getUuid());

    // The handle state must be null for a newly created tenant for any service instance
    Assert.assertNull(handleState);

    tenant = tenantService.getTenantByParam("id", "2", false);
    handleState = controller.getHandleState(tenant, tenant.getUuid(), serviceInstance.getUuid());

    // State should be same as the one mentioned in csv
    Assert.assertEquals(State.ACTIVE.name(), handleState);

    // State must be terminated once tenant is terminated
    tenantService.delete(tenant.getUuid(), "test deleting", null);
    handleState = controller.getHandleState(tenant, tenant.getUuid(), serviceInstance.getUuid());
    Assert.assertEquals(State.TERMINATED.name(), handleState);
  }

  /**
   * Description: Test to Enable a Payment Gateway Service
   * 
   * @author nageswarap
   */
  @Test
  public void testEnablePaymentGatewayOSS() {
    // Creating a Model Map
    map = new ModelMap();

    // enabling the service
    map = controller.enable("gc3c6f30-a44a-4754-a8cc-ty597e0a129", true, map, new MockHttpServletRequest());

    // Asserting the service status
    Assert.assertTrue(map.containsAttribute("enabled"));
    Assert.assertEquals(true, map.get("enabled"));
    Assert.assertEquals("success", map.get("result"));
  }

  /**
   * Description: Test to Disable a Payment Gateway Service
   * 
   * @author nageswarap
   */
  @Test
  public void testDisablePaymentGatewayOSS() throws Exception {
    // Creating a Model Map
    map = new ModelMap();

    // enabling the service
    map = controller.enable("gc3c6f30-a44a-4754-a8cc-ty597e0a129", true, map, new MockHttpServletRequest());

    // Asserting the service status for enabling
    Assert.assertTrue(map.containsAttribute("enabled"));
    Assert.assertEquals(true, map.get("enabled"));
    Assert.assertEquals("success", map.get("result"));

    // Disabling the service
    map = controller.enable("gc3c6f30-a44a-4754-a8cc-ty597e0a129", false, map, new MockHttpServletRequest());

    // Asserting the service status for enabling
    Assert.assertTrue(map.containsAttribute("enabled"));
    Assert.assertEquals(false, map.get("enabled"));
    Assert.assertEquals("success", map.get("result"));
  }

  /**
   * Description: Test to Enable two services from same category
   * 
   * @author nageswarap
   */
  @Test
  public void testEnableSecondPaymentGatewayOSSInSameCategory() throws Exception {
    // Creating a Model Map
    map = new ModelMap();

    // enabling the service
    map = controller.enable("gc3c6f30-a44a-4754-a8cc-ty597e0a129", true, map, new MockHttpServletRequest());

    // Asserting the service status for enabling
    Assert.assertTrue(map.containsAttribute("enabled"));
    Assert.assertEquals(true, map.get("enabled"));
    Assert.assertEquals("success", map.get("result"));

    // enabling the second service from the same category
    map = controller.enable("gc3c6f30-a44a-4754-a8cc-ty597e0a130", true, map, new MockHttpServletRequest());

    // Asserting the service status for enabling
    Assert.assertTrue(map.containsAttribute("enabled"));
    Assert.assertEquals("failure", map.get("result"));
    Assert.assertEquals("Service in this category is already enabled", map.get("message"));
  }
}

/*
 * Copyright © 2013 Citrix Systems, Inc. You may not use, copy, or modify this file except pursuant to a valid license
 * agreement from Citrix Systems, Inc.
 */
package fragment.web;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tiles.definition.NoSuchDefinitionException;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.ExpectedException;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import web.WebTestsBase;

import com.citrix.cpbm.access.proxy.CustomProxy;
import com.citrix.cpbm.platform.admin.service.ConnectorConfigurationManager;
import com.citrix.cpbm.platform.admin.service.exceptions.ConnectorManagementServiceException;
import com.citrix.cpbm.platform.spi.CloudConnector;
import com.citrix.cpbm.platform.spi.DynamicResourceTypeMetadataRegistry;
import com.citrix.cpbm.platform.spi.FilterComponent;
import com.citrix.cpbm.platform.spi.ResourceComponent;
import com.citrix.cpbm.portal.forms.SubscriptionForm;
import com.citrix.cpbm.portal.fragment.controllers.ChannelController;
import com.citrix.cpbm.portal.fragment.controllers.ProductBundlesController;
import com.citrix.cpbm.portal.fragment.controllers.ProductsController;
import com.citrix.cpbm.portal.fragment.controllers.SubscriptionController;
import com.vmops.internal.service.SubscriptionService;
import com.vmops.model.Channel;
import com.vmops.model.ChannelRevision;
import com.vmops.model.ChargeRecurrenceFrequency;
import com.vmops.model.Configuration;
import com.vmops.model.Product;
import com.vmops.model.ProductBundle;
import com.vmops.model.ProductBundleRevision;
import com.vmops.model.ProductCharge;
import com.vmops.model.ProvisioningConstraint;
import com.vmops.model.ProvisioningConstraint.AssociationType;
import com.vmops.model.RateCard;
import com.vmops.model.RateCardCharge;
import com.vmops.model.ServiceInstance;
import com.vmops.model.ServiceResourceType;
import com.vmops.model.ServiceResourceType.ResourceConstraint;
import com.vmops.model.ServiceResourceTypeGeneratedUsage;
import com.vmops.model.Subscription;
import com.vmops.model.Tenant;
import com.vmops.persistence.ProductDAO;
import com.vmops.persistence.ServiceInstanceDao;
import com.vmops.persistence.SubscriptionDAO;
import com.vmops.portal.config.Configuration.Names;
import com.vmops.service.ChannelService;
import com.vmops.service.ConfigurationService;
import com.vmops.service.ProductBundleService;
import com.vmops.service.TenantService;
import com.vmops.web.forms.ProductBundleForm;
import com.vmops.web.forms.ProductForm;
import com.vmops.web.interceptors.UserContextInterceptor;
import common.MockCloudInstance;

public class AbstractSubscriptionControllerTest extends WebTestsBase {

  @Autowired
  private ConnectorConfigurationManager connectorConfigurationManager;

  @Autowired
  private SubscriptionController controller;

  @Autowired
  private ProductsController productsController;

  @Autowired
  private TenantService service;

  @Autowired
  private SubscriptionDAO subscriptionDAO;

  @Autowired
  private ProductBundleService bundleservice;

  @Autowired
  private SubscriptionService subscriptionService;

  @Autowired
  private ServiceInstanceDao serviceInstanceDAO;

  @Autowired
  private ChannelService channelService;

  @Autowired
  private ProductBundlesController bundleController;

  @Autowired
  private ConfigurationService configurationService;

  @Autowired
  private ChannelController channelController;

  private ModelMap map;

  private MockHttpServletRequest request;

  private MockHttpServletResponse response;

  private final FilterComponent component = new FilterComponent("zone", "122");

  private final ArrayList<FilterComponent> componentsList = new ArrayList<FilterComponent>();

  private final ResourceComponent resourceComponent = new ResourceComponent("VirtualMachine", "Template", "1");

  private final ArrayList<ResourceComponent> resourceComponentList = new ArrayList<ResourceComponent>();

  private String dateFormat = "MM/dd/yyyy";

  @Autowired
  private ProductDAO productdao;

  @Override
  public void prepareMock() {
    componentsList.add(component);
    resourceComponentList.add(resourceComponent);
    MockCloudInstance mock = this.getMockCloudInstance();
    CloudConnector connector = mock.getCloudConnector();
    DynamicResourceTypeMetadataRegistry metadataRegistry = (DynamicResourceTypeMetadataRegistry) mock
        .getMetadataRegistry();
    List<String> resourceTypes = new ArrayList<String>();
    resourceTypes.add("resource_type_name");
    resourceTypes.add("VirtualMachine");
    resourceTypes.add("Network");
    resourceTypes.add("dummyResource");
    resourceTypes.add("MRD_RT1");
    resourceTypes.add("MRD_RT2");
    resourceTypes.add("MRD_RT4");
    resourceTypes.add("PSI_RT1");
    resourceTypes.add("PSI_RT2");
    resourceTypes.add("PSI_RT3");
    resourceTypes.add("PSI_RT4");
    resourceTypes.add("PSI_RT5");
    resourceTypes.add("PSI_RT6");
    resourceTypes.add("PSI_RT7");
    resourceTypes.add("PSI_RT8");
    resourceTypes.add("MRD_RT5");
    resourceTypes.add("PSI_RT41000");
    resourceTypes.add("Volume");
    Map<String, String> discriminatorMap = new HashMap<String, String>();
    EasyMock.expect(metadataRegistry.getDiscriminatorValues(EasyMock.anyObject(String.class)))
        .andReturn(discriminatorMap).anyTimes();
    EasyMock.expect(connector.getServiceInstanceUUID()).andReturn("1111111").anyTimes();
    EasyMock.expect(connector.getStatus()).andReturn(true).anyTimes();
    EasyMock
        .expect(
            metadataRegistry.getFilterValues(EasyMock.anyObject(String.class), EasyMock.anyObject(String.class),
                EasyMock.anyObject(String.class))).andReturn(componentsList).anyTimes();
    EasyMock
        .expect(
            metadataRegistry.getResourceComponentValues(EasyMock.anyObject(String.class),
                EasyMock.anyObject(String.class), EasyMock.anyObject(String.class), EasyMock.anyObject(String.class),
                EasyMock.<Map<String, String>> anyObject(), EasyMock.<Map<String, String>> anyObject()))
        .andReturn(resourceComponentList).anyTimes();

    EasyMock
        .expect(
            metadataRegistry.getResourceComponentValues(EasyMock.anyObject(String.class),
                EasyMock.anyObject(String.class))).andReturn(resourceComponentList).anyTimes();
    EasyMock
        .expect(metadataRegistry.getResourceTypes(EasyMock.anyObject(String.class), EasyMock.anyObject(String.class)))
        .andReturn(resourceTypes).anyTimes();
    EasyMock.replay(connector, metadataRegistry);

  }

  @Before
  public void init() throws Exception {
    map = new ModelMap();
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testUtilityRatesLightbox() {
    Tenant tenant = service.getTenantByParam("id", "1", false);
    Product product = productdao.find(1L);

    request.setAttribute(UserContextInterceptor.EFFECTIVE_TENANT_KEY, tenant);
    request.setAttribute("isSurrogatedTenant", false);
    ServiceInstance instance = serviceInstanceDAO.find(1L);

    String contextString = "PSI_UD1=10";
    controller.utilityRatesLightbox(instance.getUuid(), "PSI_RT1", contextString, null, map, request);
    Assert.assertEquals("PSI_RT1", map.get("resourceTypeName"));
    Assert.assertEquals(tenant, map.get("tenant"));
    Assert.assertEquals(tenant.getCurrency(), map.get("currency"));

    Map<Object, Object> retMap = (Map<Object, Object>) map.get("retMap");

    Map<Object, Object> retVal = (Map<Object, Object>) retMap.get(instance.getService());
    Map<Product, ProductCharge> chargeMap = (Map<Product, ProductCharge>) retVal.get(instance);
    ProductCharge charge = chargeMap.get(product);
    Assert.assertEquals("USD", charge.getCurrencyValue().getCurrencyCode());
    Assert.assertEquals("20.0000", charge.getPrice().toString());
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testUtilityRatesTable() {
    try {
      Tenant tenant = tenantDAO.find(4L);
      ServiceInstance instance = serviceInstanceDAO.find(1L);
      Channel channel = channelDAO.find(1L);
      String[] generatedUsage = {
          "RUNNING_VM", "ALLOCATED_VM"
      };

      controller.utilityratesTable(tenant.getParam(), instance.getUuid(), "VirtualMachine", "PSI_UD1=10", "USD",
          "false", null, null, null, null, map, request);

      Assert.assertEquals(channelService.getCurrentRevision(channel).getStartDate(), map.get("startDate"));
      int i = 0;

      List<ServiceResourceTypeGeneratedUsage> usageList = (List<ServiceResourceTypeGeneratedUsage>) map
          .get("generatedUsageListForServiceResourceType");
      for (ServiceResourceTypeGeneratedUsage serviceResourceTypeGeneratedUsage : usageList) {
        Assert.assertEquals(generatedUsage[i], serviceResourceTypeGeneratedUsage.getUsageTypeName());
        i++;
      }
      Assert.assertNull(map.get("tenant"));
      Assert.assertEquals(currencyValueService.locateBYCurrencyCode("USD"), map.get("currency"));

      Product product = productdao.find(1L);

      Map<Object, Object> retMap = (Map<Object, Object>) map.get("retMap");
      Map<Object, Object> retVal = (Map<Object, Object>) retMap.get(instance.getService());
      Map<Product, ProductCharge> chargeMap = (Map<Product, ProductCharge>) retVal.get(instance);
      ProductCharge charge = chargeMap.get(product);
      Assert.assertEquals("USD", charge.getCurrencyValue().getCurrencyCode());
      Assert.assertEquals("20.0000", charge.getPrice().toString());

    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testUtilityrates_tableForPublicCatalog() {
    ModelMap anonymousMap = new ModelMap();
    ServiceInstance instance = serviceInstanceDAO.find(1L);
    controller.utilityratesTable(null, instance.getUuid(), "VirtualMachine", "PSI_UD1=10", "USD", "false", null, null,
        null, null, anonymousMap, request);
    Assert.assertNotNull(anonymousMap.get(UserContextInterceptor.MIN_FRACTION_DIGITS));
    Assert.assertNotNull(anonymousMap.get(UserContextInterceptor.CURRENCY_PRECISION));
    Assert.assertNotNull(anonymousMap.get(UserContextInterceptor.CURRENCY_FORMAT));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testUtilityRatesTableForATenant() {
    try {
      Tenant tenant = tenantDAO.find(4L);
      ServiceInstance instance = serviceInstanceDAO.find(1L);
      Channel channel = channelDAO.find(4L);
      String[] generatedUsage = {
          "RUNNING_VM", "ALLOCATED_VM"
      };

      request.setAttribute(UserContextInterceptor.EFFECTIVE_TENANT_KEY, tenant);
      controller.utilityratesTable(tenant.getParam(), instance.getUuid(), "VirtualMachine", "PSI_UD1=10", "JPY",
          "false", null, null, null, null, map, request);

      Assert.assertEquals(channelService.getCurrentRevision(channel).getStartDate(), map.get("startDate"));
      int i = 0;

      List<ServiceResourceTypeGeneratedUsage> usageList = (List<ServiceResourceTypeGeneratedUsage>) map
          .get("generatedUsageListForServiceResourceType");
      for (ServiceResourceTypeGeneratedUsage serviceResourceTypeGeneratedUsage : usageList) {
        Assert.assertEquals(generatedUsage[i], serviceResourceTypeGeneratedUsage.getUsageTypeName());
        i++;
      }
      Assert.assertEquals(tenant, map.get("tenant"));
      Assert.assertEquals(currencyValueService.locateBYCurrencyCode("JPY"), map.get("currency"));
      Product product = productdao.find(1L);

      Map<Object, Object> retMap = (Map<Object, Object>) map.get("retMap");
      Map<Object, Object> retVal = (Map<Object, Object>) retMap.get(instance.getService());
      Map<Product, ProductCharge> chargeMap = (Map<Product, ProductCharge>) retVal.get(instance);
      ProductCharge charge = chargeMap.get(product);
      Assert.assertEquals("JPY", charge.getCurrencyValue().getCurrencyCode());
      Assert.assertEquals("11.0000", charge.getPrice().toString());

    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testGetTaxableAmount() {
    try {
      String amount = "100.00";
      String taxableamount = controller.getTaxableAmount(amount);
      Assert.assertEquals(taxableamount, "10.0000");
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testGetTaxableAmountNegative() {
    try {
      String amount = "-100.00";
      String taxableamount = controller.getTaxableAmount(amount);
      Assert.assertEquals(BigDecimal.ZERO.toString(), taxableamount);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testCreateSubscription() {
    try {
      Tenant tenant = tenantDAO.find(4L);
      String configurationData = "{\"hostName\":\"anusha-VM\",\"displayName\":\"\",\"group\":\"\"}";
      ServiceInstance instance = serviceInstanceDAO.find(1L);
      Subscription subscription = subscriptionDAO.find(1L);
      subscription.setConfigurationData(configurationData);
      subscriptionDAO.save(subscription);
      String[] frequencyDisplayNames = {
          "None", "Monthly", "Quarterly", "Annual"
      };

      request.setAttribute(UserContextInterceptor.EFFECTIVE_TENANT_KEY, tenant);
      request.setAttribute("isSurrogatedTenant", false);

      controller.createSubscription(tenant, tenant.getParam(), instance.getUuid(), subscription.getId().toString(),
          "VirtualMachine", map, request);

      Assert.assertEquals(true, map.get("userHasCloudServiceAccount"));
      Assert.assertEquals(tenant, map.get("tenant"));
      Assert.assertEquals(instance.getUuid(), map.get("selectedCloudServiceInstance"));
      Assert.assertEquals(instance.getService().getCategory(), map.get("selectedCategory"));
      Assert.assertEquals(instance.getService(), map.get("service"));
      Assert.assertEquals(instance.getUuid(), map.get("serviceInstanceUuid"));
      Assert.assertEquals(instance.getService().getServiceResourceTypes().size(),
          ((List<ServiceResourceType>) map.get("resourceTypes")).size());
      Assert.assertEquals("VirtualMachine", map.get("resourceType"));
      Assert.assertEquals("[IAAS]", map.get("serviceCategoryList").toString());
      Assert.assertEquals(subscription, map.get("subscription"));
      Assert.assertEquals(configurationData, map.get("configurationData").toString());
      Assert.assertEquals(tenant.getSourceChannel().getName(), map.get("sourceChannelName"));

      int i = 0;
      @SuppressWarnings("unchecked")
      List<ChargeRecurrenceFrequency> chargeRecurrenceFrequencyList = (List<ChargeRecurrenceFrequency>) map
          .get("chargeRecurrenceFrequencyList");
      for (ChargeRecurrenceFrequency frequency : chargeRecurrenceFrequencyList) {
        Assert.assertEquals(frequencyDisplayNames[i], frequency.getDisplayName());
        i++;
      }
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testCreateSubscriptionWithNoId() {
    try {
      Tenant tenant = tenantDAO.find(4L);
      ServiceInstance instance = serviceInstanceDAO.find(1L);

      String[] frequencyDisplayNames = {
          "None", "Monthly", "Quarterly", "Annual"
      };

      request.setAttribute(UserContextInterceptor.EFFECTIVE_TENANT_KEY, tenant);
      request.setAttribute("isSurrogatedTenant", false);

      controller.createSubscription(tenant, tenant.getParam(), instance.getUuid(), "", "VirtualMachine", map, request);

      Assert.assertEquals(true, map.get("userHasCloudServiceAccount"));
      Assert.assertEquals(tenant, map.get("tenant"));
      Assert.assertEquals(instance.getUuid(), map.get("selectedCloudServiceInstance"));
      Assert.assertEquals(instance.getService().getCategory(), map.get("selectedCategory"));
      Assert.assertEquals(instance.getService(), map.get("service"));
      Assert.assertEquals(instance.getUuid(), map.get("serviceInstanceUuid"));
      Assert.assertEquals(instance.getService().getServiceResourceTypes().size(),
          ((List<ServiceResourceType>) map.get("resourceTypes")).size());
      Assert.assertEquals("VirtualMachine", map.get("resourceType"));
      Assert.assertEquals("[IAAS]", map.get("serviceCategoryList").toString());
      Assert.assertEquals(tenant.getSourceChannel().getName(), map.get("sourceChannelName"));

      int i = 0;
      @SuppressWarnings("unchecked")
      List<ChargeRecurrenceFrequency> chargeRecurrenceFrequencyList = (List<ChargeRecurrenceFrequency>) map
          .get("chargeRecurrenceFrequencyList");
      for (ChargeRecurrenceFrequency frequency : chargeRecurrenceFrequencyList) {
        Assert.assertEquals(frequencyDisplayNames[i], frequency.getDisplayName());
        i++;
      }
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test(expected = ConnectorManagementServiceException.class)
  public void testCreateSubscriptionWithInvalidInstanceforTenant() throws ConnectorManagementServiceException {

    Tenant tenant = tenantDAO.find(4L);
    ServiceInstance instance = serviceInstanceDAO.find(2L);
    request.setAttribute(UserContextInterceptor.EFFECTIVE_TENANT_KEY, tenant);
    request.setAttribute("isSurrogatedTenant", false);

    controller.createSubscription(tenant, tenant.getParam(), instance.getUuid(), "", "VirtualMachine", map, request);

  }

  @Test
  public void testCreateSubscriptionWithNullServiceInstance() {
    try {
      Tenant tenant = tenantDAO.find(4L);
      ServiceInstance instance = serviceInstanceDAO.find(1L);

      String[] frequencyDisplayNames = {
          "None", "Monthly", "Quarterly", "Annual"
      };

      request.setAttribute(UserContextInterceptor.EFFECTIVE_TENANT_KEY, tenant);
      request.setAttribute("isSurrogatedTenant", false);

      controller.createSubscription(tenant, tenant.getParam(), null, "", "VirtualMachine", map, request);

      Assert.assertEquals(true, map.get("userHasCloudServiceAccount"));
      Assert.assertEquals(tenant, map.get("tenant"));
      Assert.assertEquals(instance.getService(), map.get("service"));
      Assert.assertEquals(instance.getUuid(), map.get("serviceInstanceUuid"));
      Assert.assertEquals(instance.getService().getServiceResourceTypes().size(),
          ((List<ServiceResourceType>) map.get("resourceTypes")).size());
      Assert.assertEquals("VirtualMachine", map.get("resourceType"));
      Assert.assertEquals("[IAAS]", map.get("serviceCategoryList").toString());
      Assert.assertEquals(tenant.getSourceChannel().getName(), map.get("sourceChannelName"));

      int i = 0;
      @SuppressWarnings("unchecked")
      List<ChargeRecurrenceFrequency> chargeRecurrenceFrequencyList = (List<ChargeRecurrenceFrequency>) map
          .get("chargeRecurrenceFrequencyList");
      for (ChargeRecurrenceFrequency frequency : chargeRecurrenceFrequencyList) {
        Assert.assertEquals(frequencyDisplayNames[i], frequency.getDisplayName());
        i++;
      }
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testCreateSubscriptionWithNullServiceInstanceID() {
    try {
      Tenant tenant = tenantDAO.find(4L);
      ServiceInstance instance = serviceInstanceDAO.find(1L);
      String[] frequencyDisplayNames = {
          "None", "Monthly", "Quarterly", "Annual"
      };

      request.setAttribute(UserContextInterceptor.EFFECTIVE_TENANT_KEY, tenant);
      request.setAttribute("isSurrogatedTenant", false);

      controller.createSubscription(tenant, tenant.getParam(), null, "", "VirtualMachine", map, request);

      Assert.assertEquals(true, map.get("userHasCloudServiceAccount"));
      Assert.assertEquals(tenant, map.get("tenant"));
      Assert.assertEquals(instance.getService(), map.get("service"));
      Assert.assertEquals(instance.getUuid(), map.get("serviceInstanceUuid"));
      Assert.assertEquals(instance.getService().getServiceResourceTypes().size(),
          ((List<ServiceResourceType>) map.get("resourceTypes")).size());
      Assert.assertEquals("VirtualMachine", map.get("resourceType"));
      Assert.assertEquals("[IAAS]", map.get("serviceCategoryList").toString());
      Assert.assertEquals(tenant.getSourceChannel().getName(), map.get("sourceChannelName"));

      int i = 0;
      @SuppressWarnings("unchecked")
      List<ChargeRecurrenceFrequency> chargeRecurrenceFrequencyList = (List<ChargeRecurrenceFrequency>) map
          .get("chargeRecurrenceFrequencyList");
      for (ChargeRecurrenceFrequency frequency : chargeRecurrenceFrequencyList) {
        Assert.assertEquals(frequencyDisplayNames[i], frequency.getDisplayName());
        i++;
      }
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testGetFilterComponents() {
    try {
      Tenant tenant = tenantDAO.find(4L);
      ServiceInstance instance = serviceInstanceDAO.find(1L);

      request.setAttribute(UserContextInterceptor.EFFECTIVE_TENANT_KEY, getRootUser().getTenant());
      request.setAttribute("isSurrogatedTenant", true);

      List<FilterComponent> componentList = controller.getFilterComponents(tenant, tenant.getParam(),
          instance.getUuid(), "zone", true, request);
      for (FilterComponent comp : componentList) {
        Assert.assertEquals(comp.getName(), "zone");
        Assert.assertEquals(comp.getValue(), "122");
      }
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testgetFilterComponentsForBundle() {
    try {
      Tenant tenant = tenantDAO.find(4L);
      ServiceInstance instance = serviceInstanceDAO.find(1L);
      ProductBundle bundle = bundleservice.locateProductBundleById("2");

      request.setAttribute(UserContextInterceptor.EFFECTIVE_TENANT_KEY, tenant);
      request.setAttribute("isSurrogatedTenant", false);

      List<FilterComponent> componentList = controller.getFilterComponentsForBundle(tenant, tenant.getParam(),
          instance.getUuid(), "zone", bundle.getId(), null, request);
      for (FilterComponent comp : componentList) {
        Assert.assertEquals(comp.getName(), "zone");
        Assert.assertEquals(comp.getValue(), "122");
      }
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testGetResourceComponents() {
    try {
      Tenant tenant = tenantDAO.find(4L);
      ServiceInstance instance = serviceInstanceDAO.find(1L);
      List<ResourceComponent> resourceCompList = controller.getResourceComponents(tenant, tenant.getParam(),
          instance.getUuid(), "VirtualMachine", "Template", "Template=1", true, "", null, request);
      for (ResourceComponent comp : resourceCompList) {
        Assert.assertEquals(comp.getName(), "Template");
        Assert.assertEquals(comp.getValue(), "1");
      }
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testGetResourceComponentsForBundle() {
    try {
      Tenant tenant = tenantDAO.find(4L);
      ServiceInstance instance = serviceInstanceDAO.find(1L);
      ProductBundle bundle = bundleservice.locateProductBundleById("2");
      request.setAttribute(UserContextInterceptor.EFFECTIVE_TENANT_KEY, tenant);
      request.setAttribute("isSurrogatedTenant", false);

      List<ResourceComponent> resourceCompList = controller.getResourceComponentsForBundle(tenant, tenant.getParam(),
          instance.getUuid(), "VirtualMachine", "PSI_C1", null, "Template=1", null, bundle.getId(), null, request);
      for (ResourceComponent comp : resourceCompList) {
        Assert.assertEquals(comp.getName(), "Template");
        Assert.assertEquals(comp.getValue(), "1");
      }
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  // TODO ChannelParam wants channel ID! tenanParam and sub subscriptionid do
  @Test
  public void testPreviewCatalog() {
    try {
      Channel channel = channelDAO.find(4L);
      Tenant tenant = tenantDAO.find(4L);
      ServiceInstance instance = serviceInstanceDAO.find(1L);
      Subscription subscription = subscriptionDAO.find(1L);
      ChannelRevision revision = channelService.getCurrentChannelRevision(channel, false);
      String[] frequencyDisplayNames = {
          "None", "Monthly", "Quarterly", "Annual"
      };

      request.setAttribute(UserContextInterceptor.EFFECTIVE_TENANT_KEY, getRootUser().getTenant());
      request.setAttribute("isSurrogatedTenant", true);

      controller.previewCatalog(channel.getId().toString(), map, tenant.getParam(), instance.getUuid(), subscription
          .getId().toString(), revision.getRevision().toString(), null, null, "JPY", "VirtualMachine", request);

      Assert.assertEquals(channel, map.get("channel"));
      Assert.assertEquals(channelService.listCurrencies(channel.getParam()), map.get("currencies"));
      Assert.assertEquals(true, map.get("viewChannelCatalog"));
      Assert.assertEquals(revision.getRevision().toString(), map.get("revision"));
      Assert.assertEquals(currencyValueService.locateBYCurrencyCode("JPY"), map.get("selectedCurrency"));
      Assert.assertEquals(getRootUser().getTenant(), map.get("tenant"));

      int i = 0;
      @SuppressWarnings("unchecked")
      List<ChargeRecurrenceFrequency> chargeRecurrenceFrequencyList = (List<ChargeRecurrenceFrequency>) map
          .get("chargeRecurrenceFrequencyList");
      for (ChargeRecurrenceFrequency frequency : chargeRecurrenceFrequencyList) {
        Assert.assertEquals(frequencyDisplayNames[i], frequency.getDisplayName());
        i++;
      }
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testPreviewCatalogWithNullChannel() {
    try {
      Channel channel = channelDAO.find(1L);
      ServiceInstance instance = serviceInstanceDAO.find(1L);
      ChannelRevision revision = channelService.getCurrentChannelRevision(channel, false);
      String[] frequencyDisplayNames = {
          "None", "Monthly", "Quarterly", "Annual"
      };

      request.setAttribute(UserContextInterceptor.EFFECTIVE_TENANT_KEY, getRootUser().getTenant());
      request.setAttribute("isSurrogatedTenant", true);

      controller.previewCatalog(null, map, null, instance.getUuid(), null, revision.getRevision().toString(), null,
          null, "JPY", "VirtualMachine", request);

      Assert.assertEquals(channel, map.get("channel"));
      Assert.assertEquals(channelService.listCurrencies(channel.getParam()), map.get("currencies"));
      Assert.assertEquals(true, map.get("viewChannelCatalog"));
      Assert.assertEquals(revision.getRevision().toString(), map.get("revision"));
      Assert.assertEquals(currencyValueService.locateBYCurrencyCode("JPY"), map.get("selectedCurrency"));
      Assert.assertEquals(getRootUser().getTenant(), map.get("tenant"));

      int i = 0;
      @SuppressWarnings("unchecked")
      List<ChargeRecurrenceFrequency> chargeRecurrenceFrequencyList = (List<ChargeRecurrenceFrequency>) map
          .get("chargeRecurrenceFrequencyList");
      for (ChargeRecurrenceFrequency frequency : chargeRecurrenceFrequencyList) {
        Assert.assertEquals(frequencyDisplayNames[i], frequency.getDisplayName());
        i++;
      }
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testPreviewCatalogWithNullServiceInstance() {
    try {
      Channel channel = channelDAO.find(1L);
      ChannelRevision revision = channelService.getCurrentChannelRevision(channel, false);
      String[] frequencyDisplayNames = {
          "None", "Monthly", "Quarterly", "Annual"
      };

      request.setAttribute(UserContextInterceptor.EFFECTIVE_TENANT_KEY, getRootUser().getTenant());
      request.setAttribute("isSurrogatedTenant", true);

      controller.previewCatalog(null, map, null, null, null, revision.getRevision().toString(), null, null, "JPY",
          "VirtualMachine", request);

      Assert.assertEquals(channel, map.get("channel"));
      Assert.assertEquals(channelService.listCurrencies(channel.getParam()), map.get("currencies"));
      Assert.assertEquals(true, map.get("viewChannelCatalog"));
      Assert.assertEquals(revision.getRevision().toString(), map.get("revision"));
      Assert.assertEquals(currencyValueService.locateBYCurrencyCode("JPY"), map.get("selectedCurrency"));
      Assert.assertEquals(getRootUser().getTenant(), map.get("tenant"));

      int i = 0;
      @SuppressWarnings("unchecked")
      List<ChargeRecurrenceFrequency> chargeRecurrenceFrequencyList = (List<ChargeRecurrenceFrequency>) map
          .get("chargeRecurrenceFrequencyList");
      for (ChargeRecurrenceFrequency frequency : chargeRecurrenceFrequencyList) {
        Assert.assertEquals(frequencyDisplayNames[i], frequency.getDisplayName());
        i++;
      }
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testPreviewCatalogWithNullCurrency() {
    try {
      Channel channel = channelDAO.find(4L);
      Tenant tenant = tenantDAO.find(4L);
      ServiceInstance instance = serviceInstanceDAO.find(1L);
      Subscription subscription = subscriptionDAO.find(1L);
      ChannelRevision revision = channelService.getCurrentChannelRevision(channel, false);
      String[] frequencyDisplayNames = {
          "None", "Monthly", "Quarterly", "Annual"
      };

      request.setAttribute(UserContextInterceptor.EFFECTIVE_TENANT_KEY, getRootUser().getTenant());
      request.setAttribute("isSurrogatedTenant", true);

      controller.previewCatalog(channel.getId().toString(), map, tenant.getParam(), instance.getUuid(), subscription
          .getId().toString(), revision.getRevision().toString(), null, null, null, "VirtualMachine", request);

      Assert.assertEquals(channel, map.get("channel"));
      Assert.assertEquals(channelService.listCurrencies(channel.getParam()), map.get("currencies"));
      Assert.assertEquals(currencyValueDAO.findByCurrencyCode("JPY"), map.get("selectedCurrency"));
      Assert.assertEquals(true, map.get("viewChannelCatalog"));
      Assert.assertEquals(revision.getRevision().toString(), map.get("revision"));
      Assert.assertEquals(currencyValueService.locateBYCurrencyCode("JPY"), map.get("selectedCurrency"));
      Assert.assertEquals(getRootUser().getTenant(), map.get("tenant"));

      int i = 0;
      @SuppressWarnings("unchecked")
      List<ChargeRecurrenceFrequency> chargeRecurrenceFrequencyList = (List<ChargeRecurrenceFrequency>) map
          .get("chargeRecurrenceFrequencyList");
      for (ChargeRecurrenceFrequency frequency : chargeRecurrenceFrequencyList) {
        Assert.assertEquals(frequencyDisplayNames[i], frequency.getDisplayName());
        i++;
      }
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testGetUniqueResourceComponents() {
    try {
      String[] componentArray = {
          "SO", "Template", "hypervisor"
      };
      ServiceInstance instance = serviceInstanceDAO.find(1L);

      List<String> compList = controller.getUniqueResourceComponents(instance.getUuid(), "VirtualMachine");

      Assert.assertEquals(componentArray.length, compList.size());
      for (int i = 0; i < componentArray.length; i++) {
        Assert.assertTrue(compList.contains(componentArray[i]));
      }
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testAnonymousCatalog() {

    Channel channel = channelService.getChannelById("4");

    ServiceInstance instance = serviceInstanceDAO.find(1L);

    request.setAttribute(UserContextInterceptor.EFFECTIVE_TENANT_KEY, getRootUser().getTenant());
    request.setAttribute("isSurrogatedTenant", true);

    enableAnonymousCatalogBrowsing();

    // For a specific channel using channel param
    controller.anonymousCatalog(map, null, instance.getUuid(), "JPY", "VirtualMachine", channel.getCode(), request);

    verifyCatalogBrowsing(channel, map);

    // for a specific channel using channel fqdn
    Map<String, Object> globalConfig = new HashMap<String, Object>();
    globalConfig.put("channelid", channel.getId().toString());
    Channel resolvedChannel = channelService.getDefaultServiceProviderChannel();
    controller.anonymousCatalog(map, globalConfig, instance.getUuid(), "JPY", "VirtualMachine", null, request);

    verifyCatalogBrowsing(resolvedChannel, map);

    // For default channel
    Channel defChannel = channelService.getChannelById("1");
    controller.anonymousCatalog(map, null, instance.getUuid(), "JPY", "VirtualMachine", null, request);

    verifyCatalogBrowsing(defChannel, map);

    // trying to browse anonymous catalog when fqdn and channel param are those of different channel
    controller.anonymousCatalog(map, globalConfig, instance.getUuid(), "JPY", "VirtualMachine", defChannel.getCode(),
        request);
    // TODO: Add assertions after feedback from PM

  }

  private void verifyCatalogBrowsing(Channel channel, ModelMap map) {
    String[] frequencyDisplayNames = {
        "None", "Monthly", "Quarterly", "Annual"
    };

    Assert.assertEquals(channel, map.get("channel"));
    Assert.assertEquals(channelService.listCurrencies(channel.getParam()), map.get("currencies"));
    Assert.assertEquals(true, map.get("anonymousBrowsing"));
    Assert.assertEquals(currencyValueService.locateBYCurrencyCode("JPY"), map.get("selectedCurrency"));
    int i = 0;
    @SuppressWarnings("unchecked")
    List<ChargeRecurrenceFrequency> chargeRecurrenceFrequencyList = (List<ChargeRecurrenceFrequency>) map
        .get("chargeRecurrenceFrequencyList");
    for (ChargeRecurrenceFrequency frequency : chargeRecurrenceFrequencyList) {
      Assert.assertEquals(frequencyDisplayNames[i], frequency.getDisplayName());
      i++;
    }
  }

  private void enableAnonymousCatalogBrowsing() {
    Configuration config = configurationService.locateConfigurationByName(Names.com_citrix_cpbm_public_catalog_display);
    config.setValue("true");
    configurationService.update(config);
  }

  @Test
  public void testprovisionOrReconfigureSubscription() {
    try {
      Tenant tenant = tenantDAO.find(4L);
      String configurationData = "{\"hostName\":\"anusha-VM\",\"displayName\":\"\",\"group\":\"\"}";
      ServiceInstance instance = serviceInstanceDAO.find(1L);
      Subscription subscription = subscriptionDAO.find(1L);
      Subscription newSubscription = new Subscription();
      subscription.setConfigurationData(configurationData);

      request.setAttribute(UserContextInterceptor.EFFECTIVE_TENANT_KEY, tenant);
      request.setAttribute("isSurrogatedTenant", false);

      List<Subscription> subscriptionListBefore = subscriptionDAO.findByTenant(tenant, null, null);

      ProductBundle bundle = bundleservice.locateProductBundleById("2");
      SubscriptionForm form = new SubscriptionForm();
      newSubscription.setResourceType(instance.getService().getServiceResourceTypes().get(0));
      newSubscription.setConfigurationData(configurationData);
      newSubscription.setProductBundle(bundle);
      newSubscription.setCreatedBy(tenant.getOwner());
      newSubscription.setServiceInstance(instance);
      newSubscription.setTenant(tenant);
      newSubscription.setUpdatedBy(tenant.getOwner());
      newSubscription.setUser(tenant.getOwner());
      subscriptionDAO.save(newSubscription);
      com.citrix.cpbm.access.Subscription subscriptionObj = (com.citrix.cpbm.access.Subscription) CustomProxy
          .newInstance(newSubscription);

      form.setSubscription(subscriptionObj);
      BindingResult result = validate(form);

      asUser(tenant.getOwner());
      Map<String, String> responseMap = controller.provisionOrReconfigureSubscription(form, result, tenant.getParam(),
          bundle.getId().toString(), false, configurationData,
          "{\"zone_name\":\"Advanced-Zone\", \"TemplateId_name\":\"Dos\"}", instance.getUuid().toString(),
          "VirtualMachine", "zone=122", "TemplateId=56", subscription.getId().toString(), null, map, response, request);

      Assert.assertEquals(subscriptionListBefore.size() + 1, subscriptionDAO.findByTenant(tenant, null, null).size());
      Assert.assertEquals("RECONFIGURED", responseMap.get("subscriptionResultMessage"));

    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testprovisionOrReconfigureSubscriptionwithNullBundle() {
    try {
      Tenant tenant = tenantDAO.find(4L);
      String configurationData = "{\"hostName\":\"anusha-VM\",\"displayName\":\"\",\"group\":\"\"}";
      ServiceInstance instance = serviceInstanceDAO.find(1L);
      Subscription newSubscription = new Subscription();

      request.setAttribute(UserContextInterceptor.EFFECTIVE_TENANT_KEY, tenant);
      request.setAttribute("isSurrogatedTenant", false);

      List<Subscription> subscriptionListBefore = subscriptionDAO.findByTenant(tenant, null, null);

      SubscriptionForm form = new SubscriptionForm();
      newSubscription.setResourceType(instance.getService().getServiceResourceTypes().get(0));
      newSubscription.setConfigurationData(configurationData);
      newSubscription.setProductBundle(null);
      newSubscription.setCreatedBy(tenant.getOwner());
      newSubscription.setServiceInstance(instance);
      newSubscription.setTenant(tenant);
      newSubscription.setUpdatedBy(tenant.getOwner());
      newSubscription.setUser(tenant.getOwner());

      com.citrix.cpbm.access.Subscription subscriptionObj = (com.citrix.cpbm.access.Subscription) CustomProxy
          .newInstance(newSubscription);

      form.setSubscription(subscriptionObj);
      BindingResult result = validate(form);

      asUser(tenant.getOwner());
      Map<String, String> responseMap = controller.provisionOrReconfigureSubscription(form, result, tenant.getParam(),
          null, false, configurationData, "{\"zone_name\":\"Advanced-Zone\", \"TemplateId_name\":\"Dos\"}", instance
              .getUuid().toString(), "VirtualMachine", "zone=122", "TemplateId=56", null, null, map, response, request);

      Assert.assertEquals("NEWLY_CREATED", responseMap.get("subscriptionResultMessage"));

      String subscriptionUuid = responseMap.get("subscriptionId");
      Subscription subscriptionObject = subscriptionService.get(subscriptionUuid);
      Assert.assertEquals(null, subscriptionObject.getProductBundle());
      Assert.assertEquals(subscriptionListBefore.size() + 1, subscriptionDAO.findByTenant(tenant, null, null).size());

    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  @ExpectedException(NoSuchDefinitionException.class)
  public void testAnonymousCatalogFail() {
    controller.anonymousCatalog(map, null, null, "JPY", null, null, request);
  }

  private ServiceInstance getServiceInstanceByID(Long id) {
    ServiceInstance serviceInstance = null;
    List<ServiceInstance> serviceInstancesList = this.serviceInstanceDAO.getAllServiceInstances();
    for (ServiceInstance si : serviceInstancesList) {
      if (si.getId() == id) {
        serviceInstance = si;
        break;
      }
    }
    return serviceInstance;
  }

  /**
   * Description : To Test bundles with zone as provisional constraint Included , will be filtered as per the
   * Provisional constraint given on catalog page
   * 
   * @author nageswarap
   */
  @Test
  public void testBundleSubscriptionAsZoneIncludes() throws Exception {

    Tenant tenant = tenantService.getTenantByParam("id", "20", false);

    ProductBundle bundle = bundleservice.getProductBundleById(2L);
    String bundleName = bundle.getName();
    Channel channel = channelService.getChannelById("5");

    List<Map<String, Object>> provisioningConstraints = new ArrayList<Map<String, Object>>();
    Map<String, Object> pcmap1 = new HashMap<String, Object>();
    pcmap1.put("componentName", "zone");
    pcmap1.put("associationType", AssociationType.INCLUDES);
    pcmap1.put("componentValue", "123");
    pcmap1.put("displayName", "zone");
    provisioningConstraints.add(pcmap1);

    boolean status = updateBundleConstraints(bundle.getId(), channel.getId().toString(), provisioningConstraints);
    Assert.assertNotNull("syncChannel returned null", status);

    // Checking as Master User
    asUser(tenant.getOwner());

    // Getting the list of bundles on catalog page
    request.setAttribute("effectiveTenant", tenant);
    String currencyCode = channelService.listCurrencies(channel.getParam()).get(0).getCurrencyCode();
    List<ProductBundleRevision> pbrlist = bundleController.listProductBundles(tenant, tenant.getParam(),
        getServiceInstanceByID(1L).getUuid(), "VirtualMachine", "zone=123,Template=56,SO=10, hypervisor=KVM",
        "PSI_UD1=10", true, "current", channel.getId().toString(), currencyCode, null, null, request);

    // Verify the Bundle is available
    boolean bundleListingStatus = false;
    for (ProductBundleRevision pbr : pbrlist) {
      if (pbr.getProductBundle().getName().equalsIgnoreCase(bundleName)) {
        bundleListingStatus = true;
      }
    }

    // the bundle should be listed
    Assert.assertTrue("Bundle is not listing with a valid selection of zone, which is included in the bundle ",
        bundleListingStatus);

  }

  @SuppressWarnings("unchecked")
  private boolean updateBundleConstraints(long bundleID, String channelID,
      List<Map<String, Object>> provisioningConstraints) throws Exception {

    boolean pcfound = false;

    ProductBundle retrievedBundle = bundleservice.getProductBundleById(bundleID);
    retrievedBundle.setBusinessConstraint(ResourceConstraint.NONE);
    ProductBundle bundle = bundleservice.updateProductBundle(retrievedBundle, true);

    // Get the Channel
    Channel channel = channelService.getChannelById(channelID);

    if (provisioningConstraints != null) {

      int newCountOfProvisioningConstraint = provisioningConstraints.size();
      int totalNumberOfProvisioningConstraintBefore;
      int totalNumberOfProvisioningConstraintAfter;

      // Get all the provision constraints for a given bundle in a map before adding new constraints
      bundleController.viewProvisioningConstraints(bundle.getCode(), "current", null, map);
      List<ProvisioningConstraint> provisioningConstraintsBefore = (List<ProvisioningConstraint>) map
          .get("constraints");
      totalNumberOfProvisioningConstraintBefore = provisioningConstraintsBefore.size();

      // adding new constraints to the bundle
      for (Map<String, Object> pcmap : provisioningConstraints) {
        ProvisioningConstraint pc = new ProvisioningConstraint(pcmap.get("componentName").toString(),
            (AssociationType) pcmap.get("associationType"), pcmap.get("componentValue").toString(), bundle);
        pc.setRevision(channelService.getFutureRevision(null));
        pc.setComponentValueDisplayName(pcmap.get("displayName").toString());
        bundleservice.save(pc);
      }

      // Schedule Activating the Bundle
      ProductForm postForm = new ProductForm();
      postForm.setStartDate(new Date());
      BindingResult result = validate(postForm);
      productsController.setPlanDate(postForm, result, map);

      // Get all the provision constraints for a given bundle in a map after adding new provisioning constraints
      bundleController.viewProvisioningConstraints(bundle.getCode(), "current", null, map);
      List<ProvisioningConstraint> provisioningConstraintsAfter = (List<ProvisioningConstraint>) map.get("constraints");
      totalNumberOfProvisioningConstraintAfter = provisioningConstraintsAfter.size();

      // Return false in case new constraints are not added successfully
      if (totalNumberOfProvisioningConstraintAfter != totalNumberOfProvisioningConstraintBefore
          + newCountOfProvisioningConstraint) {
        return false;
      }

      // Verify newly added provisioning constraint details are matching to the total provisioning constraints present
      // in the bundle
      for (Map<String, Object> pcmap : provisioningConstraints) {// outer for loop

        pcfound = false;
        for (ProvisioningConstraint pctest : provisioningConstraintsAfter) {// inner for loop
          if (pctest.getComponentName().equalsIgnoreCase(pcmap.get("componentName").toString())
              && pctest.getValue().equalsIgnoreCase(pcmap.get("componentValue").toString())
              && pctest.getAssociation().equals(pcmap.get("associationType"))) {
            pcfound = true;
            break; // exit from inner for loop in case details match
          }
        }// end of inner for loop

        if (!pcfound) {
          break; // exit from outer for loop whenever newly added provisioning constraints details are not found
        }
      } // end of outer for loop
    } else {
      // No provisioning constraints provided to add
      pcfound = true;
    }
    return pcfound;

  }

  /**
   * Description : To Test bundles with zone as provisional constraint Included , will be filtered as per the
   * Provisional constraint given on catalog page with a invalid selection of zone which is not given in the bundle
   * 
   * @author nageswarap
   */
  @Test
  public void testResourceBundleSubscriptionAsZoneIncludesAndInvalidSelection() throws Exception {

    Tenant tenant = tenantService.getTenantByParam("id", "20", false);

    ProductBundle bundle = bundleservice.getProductBundleById(2L);
    String bundleName = bundle.getName();
    Channel channel = channelService.getChannelById("5");

    List<Map<String, Object>> provisioningConstraints = new ArrayList<Map<String, Object>>();
    Map<String, Object> pcmap1 = new HashMap<String, Object>();
    pcmap1.put("componentName", "zone");
    pcmap1.put("associationType", AssociationType.INCLUDES);
    pcmap1.put("componentValue", "123");
    pcmap1.put("displayName", "zone");
    provisioningConstraints.add(pcmap1);

    boolean status = updateBundleConstraints(bundle.getId(), channel.getId().toString(), provisioningConstraints);
    Assert.assertNotNull("syncChannel returned null", status);

    syncAndScheduleActivateAChannel(channel);

    // Checking as Master User
    asUser(tenant.getOwner());

    // Getting the list of bundles on catalog page
    request.setAttribute("effectiveTenant", tenant);
    String currencyCode = channelService.listCurrencies(channel.getParam()).get(0).getCurrencyCode();
    List<ProductBundleRevision> pbrlist = bundleController.listProductBundles(tenant, tenant.getParam(),
        getServiceInstanceByID(1L).getUuid(), "VirtualMachine", "zone=124,Template=56,SO=10, hypervisor=KVM",
        "PSI_UD1=10", true, "current", channel.getId().toString(), currencyCode, null, null, request);

    // Verify the Bundle is not available
    boolean bundleListingStatus = false;
    for (ProductBundleRevision pbr : pbrlist) {
      if (pbr.getProductBundle().getName().equalsIgnoreCase(bundleName)) {
        bundleListingStatus = true;
      }
    }

    // the bundle should not be listed
    Assert.assertFalse("Bundle is listed for a an invalid selection of zone, which is not included in the bundle",
        bundleListingStatus);

  }

  /**
   * Description : To Test bundles with zone as provisional constraint excluded , will be filtered as per the
   * Provisional constraint given on catalog page
   * 
   * @author nageswarap
   */
  @Test
  public void testResourceBundleSubscriptionAsZoneExcludes() throws Exception {

    Tenant tenant = tenantService.getTenantByParam("id", "20", false);

    ProductBundle bundle = bundleservice.getProductBundleById(2L);
    String bundleName = bundle.getName();
    Channel channel = channelService.getChannelById("5");

    List<Map<String, Object>> provisioningConstraints = new ArrayList<Map<String, Object>>();
    Map<String, Object> pcmap1 = new HashMap<String, Object>();
    pcmap1.put("componentName", "zone");
    pcmap1.put("associationType", AssociationType.EXCLUDES);
    pcmap1.put("componentValue", "123");
    pcmap1.put("displayName", "zone");
    provisioningConstraints.add(pcmap1);

    boolean status = updateBundleConstraints(bundle.getId(), channel.getId().toString(), provisioningConstraints);
    Assert.assertNotNull("syncChannel returned null", status);

    syncAndScheduleActivateAChannel(channel);

    // Checking as Master User
    asUser(tenant.getOwner());

    // Getting the list of bundles on catalog page
    request.setAttribute("effectiveTenant", tenant);
    String currencyCode = channelService.listCurrencies(channel.getParam()).get(0).getCurrencyCode();
    List<ProductBundleRevision> pbrlist = bundleController.listProductBundles(tenant, tenant.getParam(),
        getServiceInstanceByID(1L).getUuid(), "VirtualMachine", "zone=123,Template=56,SO=10, hypervisor=KVM",
        "PSI_UD1=10", true, "current", channel.getId().toString(), currencyCode, null, null, request);

    // Verify the Bundle is not available
    boolean bundleListingStatus = false;
    for (ProductBundleRevision pbr : pbrlist) {
      if (pbr.getProductBundle().getName().equalsIgnoreCase(bundleName)) {
        bundleListingStatus = true;
      }
    }

    // bundle should not be listed
    Assert.assertFalse("Bundle listed for an excluded zone filter in the bundle ", bundleListingStatus);

  }

  /**
   * Description : To Test bundles with SO as provisional constraint Included , will be filtered as per the Provisional
   * constraint given on catalog page
   * 
   * @author nageswarap
   */
  @Test
  public void testResourceBundleSubscriptionAsSOIncludes() throws Exception {

    Tenant tenant = tenantService.getTenantByParam("id", "20", false);

    ProductBundle bundle = bundleservice.getProductBundleById(2L);
    String bundleName = bundle.getName();
    Channel channel = channelService.getChannelById("5");

    boolean status = updateBundleConstraints(bundle.getId(), channel.getId().toString(), null);
    Assert.assertNotNull("syncChannel returned null", status);

    syncAndScheduleActivateAChannel(channel);

    // Checking as Master User
    asUser(tenant.getOwner());

    // Getting the list of bundles on catalog page
    request.setAttribute("effectiveTenant", tenant);
    String currencyCode = channelService.listCurrencies(channel.getParam()).get(0).getCurrencyCode();
    List<ProductBundleRevision> pbrlist = bundleController.listProductBundles(tenant, tenant.getParam(),
        getServiceInstanceByID(1L).getUuid(), "VirtualMachine", "zone=121,Template=56,SO=10, hypervisor=KVM",
        "PSI_UD1=10", true, "current", channel.getId().toString(), currencyCode, null, null, request);

    // Verify the Bundle is not available
    boolean bundleListingStatus = false;
    for (ProductBundleRevision pbr : pbrlist) {
      if (pbr.getProductBundle().getName().equalsIgnoreCase(bundleName)) {
        bundleListingStatus = true;
      }
    }

    // bundle should be listed
    Assert.assertTrue("Bundle is not listing for SO id, which is included in the bundle", bundleListingStatus);

  }

  /**
   * Description : To Test bundles with SO as provisional constraint Included , will be filtered as per the Provisional
   * constraint given on catalog page, with invalid selection of SO which is not included in the bundle
   * 
   * @author nageswarap
   */
  @Test
  public void testResourceBundleSubscriptionAsSOIncludesAndInvalidSelection() throws Exception {

    Tenant tenant = tenantService.getTenantByParam("id", "20", false);

    ProductBundle bundle = bundleservice.getProductBundleById(2L);
    String bundleName = bundle.getName();
    Channel channel = channelService.getChannelById("5");

    boolean status = updateBundleConstraints(bundle.getId(), channel.getId().toString(), null);
    Assert.assertNotNull("syncChannel returned null", status);

    syncAndScheduleActivateAChannel(channel);

    // Checking as Master User
    asUser(tenant.getOwner());

    // Getting the list of bundles on catalog page
    request.setAttribute("effectiveTenant", tenant);
    String currencyCode = channelService.listCurrencies(channel.getParam()).get(0).getCurrencyCode();
    List<ProductBundleRevision> pbrlist = bundleController.listProductBundles(tenant, tenant.getParam(),
        getServiceInstanceByID(1L).getUuid(), "VirtualMachine", "zone=121,Template=56,SO=11, hypervisor=KVM",
        "PSI_UD1=10", true, "current", channel.getId().toString(), currencyCode, null, null, request);

    // Verify the Bundle is not available
    boolean bundleListingStatus = false;
    for (ProductBundleRevision pbr : pbrlist) {
      if (pbr.getProductBundle().getName().equalsIgnoreCase(bundleName)) {
        bundleListingStatus = true;
      }
    }

    // the should not be listed
    Assert.assertFalse("Bundle is listing for SO id, which is not included in the bundle", bundleListingStatus);

  }

  /**
   * Description : To Test bundles with SO as provisional constraint Excluded , will be filtered as per the Provisional
   * constraint given on catalog page
   * 
   * @author nageswarap
   * @throws Exception
   */
  @Test
  public void testResourceBundleSubscriptionAsSOExcludes() throws Exception {

    Tenant tenant = tenantService.getTenantByParam("id", "9", false);

    ProductBundle bundle = bundleservice.getProductBundleById(13L);
    String bundleName = bundle.getName();
    Channel channel = channelService.getChannelById("3");

    List<Map<String, Object>> provisioningConstraints = new ArrayList<Map<String, Object>>();
    Map<String, Object> pcmap1 = new HashMap<String, Object>();
    pcmap1.put("componentName", "SO");
    pcmap1.put("associationType", AssociationType.EXCLUDES);
    pcmap1.put("componentValue", "20");
    pcmap1.put("displayName", "SO");
    provisioningConstraints.add(pcmap1);

    boolean status = updateBundleConstraints(bundle.getId(), channel.getId().toString(), provisioningConstraints);
    Assert.assertNotNull("syncChannel returned null", status);

    syncAndScheduleActivateAChannel(channel);

    // Checking as Master User
    asUser(tenant.getOwner());

    // Getting the list of bundles on catalog page
    request.setAttribute("effectiveTenant", tenant);
    String currencyCode = channelService.listCurrencies(channel.getParam()).get(0).getCurrencyCode();
    List<ProductBundleRevision> pbrlist = bundleController.listProductBundles(tenant, tenant.getParam(),
        getServiceInstanceByID(1L).getUuid(), "VirtualMachine", "zone=121,Template=200,SO=20, hypervisor=KVM",
        "PSI_UD1=10", true, "current", channel.getId().toString(), currencyCode, null, null, request);

    // Verify the Bundle is not available
    boolean bundleListingStatus = false;
    for (ProductBundleRevision pbr : pbrlist) {
      if (pbr.getProductBundle().getName().equalsIgnoreCase(bundleName)) {
        bundleListingStatus = true;
      }
    }

    // bundle should not be listed
    Assert.assertFalse("Bundle listed for the SO which is excluded in the bundle ", bundleListingStatus);

  }

  /**
   * Description : To Test bundles with Template as provisional constraint Included , will be filtered as per the
   * Provisional constraint given on catalog page
   * 
   * @author nageswarap
   */
  @Test
  public void testResourceBundleSubscriptionAsTemplateExcludes() throws Exception {

    Tenant tenant = tenantService.getTenantByParam("id", "20", false);

    ProductBundle bundle = bundleservice.getProductBundleById(2L);
    String bundleName = bundle.getName();
    Channel channel = channelService.getChannelById("5");

    List<Map<String, Object>> provisioningConstraints = new ArrayList<Map<String, Object>>();
    Map<String, Object> pcmap1 = new HashMap<String, Object>();
    pcmap1.put("componentName", "Template");
    pcmap1.put("associationType", AssociationType.EXCLUDES);
    pcmap1.put("componentValue", "14");
    pcmap1.put("displayName", "Template");
    provisioningConstraints.add(pcmap1);

    boolean status = updateBundleConstraints(bundle.getId(), channel.getId().toString(), provisioningConstraints);
    Assert.assertNotNull("syncChannel returned null", status);

    syncAndScheduleActivateAChannel(channel);

    // Checking as Master User
    asUser(tenant.getOwner());

    // Getting the list of bundles on catalog page
    request.setAttribute("effectiveTenant", tenant);
    String currencyCode = channelService.listCurrencies(channel.getParam()).get(0).getCurrencyCode();
    List<ProductBundleRevision> pbrlist = bundleController.listProductBundles(tenant, tenant.getParam(),
        getServiceInstanceByID(1L).getUuid(), "VirtualMachine", "zone=121,Template=14,SO=10,hypervisor=KVM",
        "PSI_UD1=10", true, "current", channel.getId().toString(), currencyCode, null, null, request);

    // Verify the Bundle is not available
    boolean bundleListingStatus = false;
    for (ProductBundleRevision pbr : pbrlist) {
      if (pbr.getProductBundle().getName().equalsIgnoreCase(bundleName)) {
        bundleListingStatus = true;
      }
    }

    // bundle should not be listed
    Assert.assertFalse("Bundle is listed for the template which is excluded in the bundle", bundleListingStatus);

  }

  /**
   * Description : To Test bundles with SO as provisional constraint Included , will be filtered as per the Provisional
   * constraint given on catalog page
   * 
   * @author nageswarap
   */
  @Test
  public void testResourceBundleSubscriptionAsTemplateIncludes() throws Exception {

    Tenant tenant = tenantService.getTenantByParam("id", "20", false);

    ProductBundle bundle = bundleservice.getProductBundleById(2L);
    String bundleName = bundle.getName();
    Channel channel = channelService.getChannelById("5");

    List<Map<String, Object>> provisioningConstraints = new ArrayList<Map<String, Object>>();
    Map<String, Object> pcmap1 = new HashMap<String, Object>();
    pcmap1.put("componentName", "Template");
    pcmap1.put("associationType", AssociationType.INCLUDES);
    pcmap1.put("componentValue", "20");
    pcmap1.put("displayName", "Template");
    provisioningConstraints.add(pcmap1);

    boolean status = updateBundleConstraints(bundle.getId(), channel.getId().toString(), provisioningConstraints);
    Assert.assertNotNull("syncChannel returned null", status);

    syncAndScheduleActivateAChannel(channel);

    // Checking as Master User
    asUser(tenant.getOwner());

    // Getting the list of bundles on catalog page
    request.setAttribute("effectiveTenant", tenant);
    String currencyCode = channelService.listCurrencies(channel.getParam()).get(0).getCurrencyCode();
    List<ProductBundleRevision> pbrlist = bundleController.listProductBundles(tenant, tenant.getParam(),
        getServiceInstanceByID(1L).getUuid(), "VirtualMachine", "zone=121,Template=20,SO=10,hypervisor=KVM",
        "PSI_UD1=10", true, "current", channel.getId().toString(), currencyCode, null, null, request);

    // Verify the Bundle is not available
    boolean bundleListingStatus = false;
    for (ProductBundleRevision pbr : pbrlist) {
      if (pbr.getProductBundle().getName().equalsIgnoreCase(bundleName)) {
        bundleListingStatus = true;
      }
    }

    // bundle should be listed
    Assert.assertTrue("Bundle is not be listed for the SO which is included in the bundle", bundleListingStatus);

  }

  /**
   * Description : To Test bundles with Template as provisional constraint Included , will be filtered as per the
   * Provisional constraint given on catalog page with invalid Template selection which is not included in the bundle
   * 
   * @author nageswarap
   */
  @Test
  public void testResourceBundleSubscriptionAsTemplateIncludesAndInvalidSelection() throws Exception {

    Tenant tenant = tenantService.getTenantByParam("id", "20", false);

    ProductBundle bundle = bundleservice.getProductBundleById(2L);
    String bundleName = bundle.getName();
    Channel channel = channelService.getChannelById("5");

    List<Map<String, Object>> provisioningConstraints = new ArrayList<Map<String, Object>>();
    Map<String, Object> pcmap1 = new HashMap<String, Object>();
    pcmap1.put("componentName", "Template");
    pcmap1.put("associationType", AssociationType.INCLUDES);
    pcmap1.put("componentValue", "20");
    pcmap1.put("displayName", "Template");
    provisioningConstraints.add(pcmap1);

    boolean status = updateBundleConstraints(bundle.getId(), channel.getId().toString(), provisioningConstraints);
    Assert.assertNotNull("syncChannel returned null", status);

    syncAndScheduleActivateAChannel(channel);

    // Checking as Master User
    asUser(tenant.getOwner());

    // Getting the list of bundles on catalog page
    request.setAttribute("effectiveTenant", tenant);
    String currencyCode = channelService.listCurrencies(channel.getParam()).get(0).getCurrencyCode();
    List<ProductBundleRevision> pbrlist = bundleController.listProductBundles(tenant, tenant.getParam(),
        getServiceInstanceByID(1L).getUuid(), "VirtualMachine", "zone=121,Template=21,SO=10,hypervisor=KVM",
        "PSI_UD1=10", true, "current", channel.getId().toString(), currencyCode, null, null, request);

    // Verify the Bundle is not available
    boolean bundleListingStatus = false;
    for (ProductBundleRevision pbr : pbrlist) {
      if (pbr.getProductBundle().getName().equalsIgnoreCase(bundleName)) {
        bundleListingStatus = true;
      }
    }

    // the bundle should not be listed
    Assert.assertFalse("Bundle is listed for template, which is not included in the bundle ", bundleListingStatus);

  }

  /**
   * Description : To Test bundles with Hypervisor as provisional constraint Included , will be filtered as per the
   * Provisional constraint given on catalog page
   * 
   * @author nageswarap
   * @throws Exception
   */
  @Test
  public void testResourceBundleSubscriptionAsHypervisorIncludes() throws Exception {

    Tenant tenant = tenantService.getTenantByParam("id", "20", false);

    ProductBundle bundle = bundleservice.getProductBundleById(2L);
    String bundleName = bundle.getName();
    Channel channel = channelService.getChannelById("5");

    List<Map<String, Object>> provisioningConstraints = new ArrayList<Map<String, Object>>();
    Map<String, Object> pcmap1 = new HashMap<String, Object>();
    pcmap1.put("componentName", "hypervisor");
    pcmap1.put("associationType", AssociationType.INCLUDES);
    pcmap1.put("componentValue", "KVM");
    pcmap1.put("displayName", "hypervisor");
    provisioningConstraints.add(pcmap1);

    boolean status = updateBundleConstraints(bundle.getId(), channel.getId().toString(), provisioningConstraints);
    Assert.assertNotNull("syncChannel returned null", status);

    syncAndScheduleActivateAChannel(channel);

    // Checking as Master User
    asUser(tenant.getOwner());

    // Getting the list of bundles on catalog page
    request.setAttribute("effectiveTenant", tenant);
    String currencyCode = channelService.listCurrencies(channel.getParam()).get(0).getCurrencyCode();
    List<ProductBundleRevision> pbrlist = bundleController.listProductBundles(tenant, tenant.getParam(),
        getServiceInstanceByID(1L).getUuid(), "VirtualMachine", "zone=121,Template=56,SO=10,hypervisor=KVM",
        "PSI_UD1=10", true, "current", channel.getId().toString(), currencyCode, null, null, request);

    // Verify the Bundle is available
    boolean bundleListingStatus = false;
    for (ProductBundleRevision pbr : pbrlist) {
      if (pbr.getProductBundle().getName().equalsIgnoreCase(bundleName)) {
        bundleListingStatus = true;
      }
    }

    // the bundle should be listed
    Assert.assertTrue("Bundle is not listed  ", bundleListingStatus);

  }

  /**
   * Description : To Test bundles with Hypervisor as provisional constraint Included , will be filtered as per the
   * Provisional constraint given on catalog page
   * 
   * @author nageswarap
   * @throws Exception
   */
  @Test
  public void testResourceBundleSubscriptionAsHypervisorIncludesAndInvalidSelection() throws Exception {

    Tenant tenant = tenantService.getTenantByParam("id", "20", false);

    ProductBundle bundle = bundleservice.getProductBundleById(2L);
    String bundleName = bundle.getName();
    Channel channel = channelService.getChannelById("5");

    List<Map<String, Object>> provisioningConstraints = new ArrayList<Map<String, Object>>();
    Map<String, Object> pcmap1 = new HashMap<String, Object>();
    pcmap1.put("componentName", "hypervisor");
    pcmap1.put("associationType", AssociationType.INCLUDES);
    pcmap1.put("componentValue", "KVM");
    pcmap1.put("displayName", "hypervisor");
    provisioningConstraints.add(pcmap1);

    boolean status = updateBundleConstraints(bundle.getId(), channel.getId().toString(), provisioningConstraints);
    Assert.assertNotNull("syncChannel returned null", status);

    syncAndScheduleActivateAChannel(channel);

    // Checking as Master User
    asUser(tenant.getOwner());

    // Getting the list of bundles on catalog page
    request.setAttribute("effectiveTenant", tenant);
    String currencyCode = channelService.listCurrencies(channel.getParam()).get(0).getCurrencyCode();
    List<ProductBundleRevision> pbrlist = bundleController.listProductBundles(tenant, tenant.getParam(),
        getServiceInstanceByID(1L).getUuid(), "VirtualMachine", "zone=121,Template=56,SO=10,hypervisor=XenServer",
        "PSI_UD1=10", true, "current", channel.getId().toString(), currencyCode, null, null, request);

    // Verify the Bundle is not available
    boolean bundleListingStatus = false;
    for (ProductBundleRevision pbr : pbrlist) {
      if (pbr.getProductBundle().getName().equalsIgnoreCase(bundleName)) {
        bundleListingStatus = true;
      }
    }

    // bundle should not be listed
    Assert.assertFalse("Bundle listed for hypervisor, which is not included in the bundle ", bundleListingStatus);

  }

  /**
   * Description : To Test bundles with hypervisor as provisional constraint excluded , will be filtered as per the
   * Provisional constraint given on catalog page
   * 
   * @author nageswarap
   */
  @Test
  public void testResourceBundleSubscriptionAsHypervisorExcludes() throws Exception {

    Tenant tenant = tenantService.getTenantByParam("id", "20", false);

    ProductBundle bundle = bundleservice.getProductBundleById(2L);
    String bundleName = bundle.getName();
    Channel channel = channelService.getChannelById("5");

    List<Map<String, Object>> provisioningConstraints = new ArrayList<Map<String, Object>>();
    Map<String, Object> pcmap1 = new HashMap<String, Object>();
    pcmap1.put("componentName", "hypervisor");
    pcmap1.put("associationType", AssociationType.EXCLUDES);
    pcmap1.put("componentValue", "KVM");
    pcmap1.put("displayName", "hypervisor");
    provisioningConstraints.add(pcmap1);

    boolean status = updateBundleConstraints(bundle.getId(), channel.getId().toString(), provisioningConstraints);
    Assert.assertNotNull("syncChannel returned null", status);

    syncAndScheduleActivateAChannel(channel);

    // Checking as Master User
    asUser(tenant.getOwner());

    // Getting the list of bundles on catalog page
    request.setAttribute("effectiveTenant", tenant);
    String currencyCode = channelService.listCurrencies(channel.getParam()).get(0).getCurrencyCode();
    List<ProductBundleRevision> pbrlist = bundleController.listProductBundles(tenant, tenant.getParam(),
        getServiceInstanceByID(1L).getUuid(), "VirtualMachine", "zone=121,Template=56,SO=10,hypervisor=KVM",
        "PSI_UD1=10", true, "current", channel.getId().toString(), currencyCode, null, null, request);

    // Verify the Bundle is not available for the channel to add
    boolean bundleListingStatus = false;
    for (ProductBundleRevision pbr : pbrlist) {
      if (pbr.getProductBundle().getName().equalsIgnoreCase(bundleName)) {
        bundleListingStatus = true;
      }
    }

    // bundle should not be listed
    Assert.assertFalse("Bundle listed for hypervisor excluded from the bundle", bundleListingStatus);

  }

  /**
   * Description : To Test bundles with Hypervisor as provisional constraint Included and SO as provisional constraint
   * Included but will be selected a different SO, will be filtered as per the Provisional constraint given on catalog
   * page
   * 
   * @author nageswarap
   * @throws Exception
   */
  @Test
  public void testResourceBundleSubscriptionAsHypervisorIncludesSOIncludesAndInvalidSelection() throws Exception {

    Tenant tenant = tenantService.getTenantByParam("id", "20", false);

    ProductBundle bundle = bundleservice.getProductBundleById(2L);
    String bundleName = bundle.getName();
    Channel channel = channelService.getChannelById("5");

    List<Map<String, Object>> provisioningConstraints = new ArrayList<Map<String, Object>>();
    Map<String, Object> pcmap1 = new HashMap<String, Object>();
    pcmap1.put("componentName", "hypervisor");
    pcmap1.put("associationType", AssociationType.INCLUDES);
    pcmap1.put("componentValue", "KVM");
    pcmap1.put("displayName", "hypervisor");
    provisioningConstraints.add(pcmap1);

    boolean status = updateBundleConstraints(bundle.getId(), channel.getId().toString(), provisioningConstraints);
    Assert.assertNotNull("syncChannel returned null", status);

    syncAndScheduleActivateAChannel(channel);

    // Checking as Master User
    asUser(tenant.getOwner());

    // Getting the list of bundles on catalog page
    request.setAttribute("effectiveTenant", tenant);
    String currencyCode = channelService.listCurrencies(channel.getParam()).get(0).getCurrencyCode();
    List<ProductBundleRevision> pbrlist = bundleController.listProductBundles(tenant, tenant.getParam(),
        getServiceInstanceByID(1L).getUuid(), "VirtualMachine", "zone=121,Template=56,SO=11,hypervisor=KVM",
        "PSI_UD1=10", true, "current", channel.getId().toString(), currencyCode, null, null, request);

    // Verify the Bundle is not available for the channel to add
    boolean bundleListingStatus = false;
    for (ProductBundleRevision pbr : pbrlist) {
      if (pbr.getProductBundle().getName().equalsIgnoreCase(bundleName)) {
        bundleListingStatus = true;
      }
    }

    // bundle should not be listed
    Assert.assertFalse("Bundle should not be listed, but listing for a invalid selection ", bundleListingStatus);

  }

  /**
   * Description : To Test bundles with SO as provisional constraint Included and Hypervisor excluded and invalid
   * selection of hypervisor, will be filtered as per the Provisional constraint given on catalog page
   * 
   * @author nageswarap
   */
  @Test
  public void testResourceBundleSubscriptionAsHypervisorExcludesSOIncludeAndInvalidHypervisorSelection()
      throws Exception {

    Tenant tenant = tenantService.getTenantByParam("id", "20", false);

    ProductBundle bundle = bundleservice.getProductBundleById(2L);
    String bundleName = bundle.getName();
    Channel channel = channelService.getChannelById("5");

    List<Map<String, Object>> provisioningConstraints = new ArrayList<Map<String, Object>>();
    Map<String, Object> pcmap1 = new HashMap<String, Object>();
    pcmap1.put("componentName", "hypervisor");
    pcmap1.put("associationType", AssociationType.EXCLUDES);
    pcmap1.put("componentValue", "KVM");
    pcmap1.put("displayName", "hypervisor");
    provisioningConstraints.add(pcmap1);

    boolean status = updateBundleConstraints(bundle.getId(), channel.getId().toString(), provisioningConstraints);
    Assert.assertNotNull("syncChannel returned null", status);

    syncAndScheduleActivateAChannel(channel);

    // Checking as Master User
    asUser(tenant.getOwner());

    // Getting the list of bundles on catalog page
    request.setAttribute("effectiveTenant", tenant);
    String currencyCode = channelService.listCurrencies(channel.getParam()).get(0).getCurrencyCode();
    List<ProductBundleRevision> pbrlist = bundleController.listProductBundles(tenant, tenant.getParam(),
        getServiceInstanceByID(1L).getUuid(), "VirtualMachine", "zone=121,Template=56,SO=10,hypervisor=XenServer",
        "PSI_UD1=10", true, "current", channel.getId().toString(), currencyCode, null, null, request);

    // Verify the Bundle is available
    boolean bundleListingStatus = false;
    for (ProductBundleRevision pbr : pbrlist) {
      if (pbr.getProductBundle().getName().equalsIgnoreCase(bundleName)) {
        bundleListingStatus = true;
      }
    }

    // bundle should be available
    Assert.assertTrue("Bundle should be listed ", bundleListingStatus);

  }

  /**
   * Description : To Test bundles with Hypervisor as provisional constraint excluded and SO included and invalid
   * selection of SO , will be filtered as per the Provisional constraint given on catalog page
   * 
   * @author nageswarap
   * @throws Exception
   */
  @Test
  public void testResourceBundleSubscriptionAsHypervisorIncludeSOExcludesAndInvalidSOSelection() throws Exception {

    Tenant tenant = tenantService.getTenantByParam("id", "9", false);

    ProductBundle bundle = bundleservice.getProductBundleById(13L);
    String bundleName = bundle.getName();
    Channel channel = channelService.getChannelById("3");

    List<Map<String, Object>> provisioningConstraints = new ArrayList<Map<String, Object>>();
    Map<String, Object> pcmap1 = new HashMap<String, Object>();
    pcmap1.put("componentName", "SO");
    pcmap1.put("associationType", AssociationType.EXCLUDES);
    pcmap1.put("componentValue", "20");
    pcmap1.put("displayName", "SO");
    provisioningConstraints.add(pcmap1);

    Map<String, Object> pcmap2 = new HashMap<String, Object>();
    pcmap2.put("componentName", "hypervisor");
    pcmap2.put("associationType", AssociationType.INCLUDES);
    pcmap2.put("componentValue", "KVM");
    pcmap2.put("displayName", "hypervisor");
    provisioningConstraints.add(pcmap2);

    boolean status = updateBundleConstraints(bundle.getId(), channel.getId().toString(), provisioningConstraints);
    Assert.assertNotNull("syncChannel returned null", status);

    syncAndScheduleActivateAChannel(channel);

    // Checking as Master User
    asUser(tenant.getOwner());

    // Getting the list of bundles on catalog page
    request.setAttribute("effectiveTenant", tenant);
    String currencyCode = channelService.listCurrencies(channel.getParam()).get(0).getCurrencyCode();
    List<ProductBundleRevision> pbrlist = bundleController.listProductBundles(tenant, tenant.getParam(),
        getServiceInstanceByID(1L).getUuid(), "VirtualMachine", "zone=121,Template=200,SO=21, hypervisor=KVM",
        "PSI_UD1=10", true, "current", channel.getId().toString(), currencyCode, null, null, request);

    // Verify the Bundle is not available for the channel to add
    boolean bundleListingStatus = false;
    for (ProductBundleRevision pbr : pbrlist) {
      if (pbr.getProductBundle().getName().equalsIgnoreCase(bundleName)) {
        bundleListingStatus = true;
      }
    }

    Assert.assertTrue("Bundle should be listed ", bundleListingStatus);

  }

  /**
   * Description : To Test bundles with Hypervisor as provisional constraint Included and SO Excluded, will be filtered
   * as per the Provisional constraint given on catalog page
   * 
   * @author nageswarap
   * @throws Exception
   */
  @Test
  public void testResourceBundleSubscriptionAsHypervisorIncludeSOExcludes() throws Exception {

    Tenant tenant = tenantService.getTenantByParam("id", "9", false);

    ProductBundle bundle = bundleservice.getProductBundleById(13L);
    String bundleName = bundle.getName();
    Channel channel = channelService.getChannelById("3");

    List<Map<String, Object>> provisioningConstraints = new ArrayList<Map<String, Object>>();
    Map<String, Object> pcmap1 = new HashMap<String, Object>();
    pcmap1.put("componentName", "SO");
    pcmap1.put("associationType", AssociationType.EXCLUDES);
    pcmap1.put("componentValue", "20");
    pcmap1.put("displayName", "SO");
    provisioningConstraints.add(pcmap1);

    Map<String, Object> pcmap2 = new HashMap<String, Object>();
    pcmap2.put("componentName", "hypervisor");
    pcmap2.put("associationType", AssociationType.INCLUDES);
    pcmap2.put("componentValue", "KVM");
    pcmap2.put("displayName", "hypervisor");
    provisioningConstraints.add(pcmap2);

    boolean status = updateBundleConstraints(bundle.getId(), channel.getId().toString(), provisioningConstraints);
    Assert.assertNotNull("syncChannel returned null", status);

    syncAndScheduleActivateAChannel(channel);

    // Checking as Master User
    asUser(tenant.getOwner());

    // Getting the list of bundles on catalog page
    request.setAttribute("effectiveTenant", tenant);
    String currencyCode = channelService.listCurrencies(channel.getParam()).get(0).getCurrencyCode();
    List<ProductBundleRevision> pbrlist = bundleController.listProductBundles(tenant, tenant.getParam(),
        getServiceInstanceByID(1L).getUuid(), "VirtualMachine", "zone=121,Template=200,SO=20, hypervisor=KVM",
        "PSI_UD1=10", true, "current", channel.getId().toString(), currencyCode, null, null, request);

    // Verify the Bundle is not available
    boolean bundleListingStatus = false;
    for (ProductBundleRevision pbr : pbrlist) {
      if (pbr.getProductBundle().getName().equalsIgnoreCase(bundleName)) {
        bundleListingStatus = true;
      }
    }

    Assert.assertFalse("Bundle should not be listed ", bundleListingStatus);

  }

  /**
   * Description : To Test bundles with SO as provisional constraint Excludes and Hypervisor Excludes , will be filtered
   * as per the Provisional constraint given on catalog page
   * 
   * @author nageswarap
   * @throws Exception
   */
  @Test
  public void testResourceBundleSubscriptionAsHypervisorExcludeSOExcludes() throws Exception {

    Tenant tenant = tenantService.getTenantByParam("id", "9", false);

    ProductBundle bundle = bundleservice.getProductBundleById(13L);
    String bundleName = bundle.getName();
    Channel channel = channelService.getChannelById("3");

    List<Map<String, Object>> provisioningConstraints = new ArrayList<Map<String, Object>>();
    Map<String, Object> pcmap1 = new HashMap<String, Object>();
    pcmap1.put("componentName", "SO");
    pcmap1.put("associationType", AssociationType.EXCLUDES);
    pcmap1.put("componentValue", "20");
    pcmap1.put("displayName", "SO");
    provisioningConstraints.add(pcmap1);

    Map<String, Object> pcmap2 = new HashMap<String, Object>();
    pcmap2.put("componentName", "hypervisor");
    pcmap2.put("associationType", AssociationType.EXCLUDES);
    pcmap2.put("componentValue", "KVM");
    pcmap2.put("displayName", "hypervisor");
    provisioningConstraints.add(pcmap2);

    boolean status = updateBundleConstraints(bundle.getId(), channel.getId().toString(), provisioningConstraints);
    Assert.assertNotNull("syncChannel returned null", status);

    syncAndScheduleActivateAChannel(channel);

    // Checking as Master User
    asUser(tenant.getOwner());

    // Getting the list of bundles on catalog page
    request.setAttribute("effectiveTenant", tenant);
    String currencyCode = channelService.listCurrencies(channel.getParam()).get(0).getCurrencyCode();
    List<ProductBundleRevision> pbrlist = bundleController.listProductBundles(tenant, tenant.getParam(),
        getServiceInstanceByID(1L).getUuid(), "VirtualMachine", "zone=121,Template=200,SO=20, hypervisor=KVM",
        "PSI_UD1=10", true, "current", channel.getId().toString(), currencyCode, null, null, request);

    // Verify the Bundle is not available for the channel to add
    boolean bundleListingStatus = false;
    for (ProductBundleRevision pbr : pbrlist) {
      if (pbr.getProductBundle().getName().equalsIgnoreCase(bundleName)) {
        bundleListingStatus = true;
      }
    }

    Assert.assertFalse("Bundle should not be listed ", bundleListingStatus);

  }

  /**
   * Description : To Test bundles with SO as provisional constraint Included , will be filtered as per the Provisional
   * constraint given on catalog page
   * 
   * @author nageswarap
   * @throws Exception
   */
  @Test
  public void testResourceBundleSubscriptionWithNoprovisionConstraints() throws Exception {

    Tenant tenant = tenantService.getTenantByParam("id", "9", false);
    Channel channel = channelService.getChannelById("3");
    ServiceResourceType resourceType = connectorConfigurationManager.getServiceResourceTypeById(1L);
    String chargeFrequency = "MONTHLY";
    String compAssociationJson = "[]";
    int noOfdays = 3;
    Calendar createdAt = Calendar.getInstance();
    createdAt.add(Calendar.DATE, 0 - noOfdays);
    int beforeBundleCount = bundleservice.getBundlesCount();
    boolean trialEligible = false;

    // Create a Bundle
    ProductBundle obtainedBundle = testCreateProductBundle("1", resourceType.getId().toString(), chargeFrequency,
        chargeFrequency + "Compute", "USD", BigDecimal.valueOf(100), createdAt.getTime(), compAssociationJson,
        ResourceConstraint.NONE, trialEligible);
    Assert.assertNotNull("The bundle is null ", obtainedBundle);
    Assert.assertEquals("the expected bundle name and the actual bundle name is not matching", chargeFrequency
        + "Compute", obtainedBundle.getName());
    Assert.assertEquals("Bundle resource type is not matching", resourceType, obtainedBundle.getResourceType());

    int afterBundleCount = bundleservice.getBundlesCount();
    Assert.assertEquals("bundle count not incremented after creating the bundle", beforeBundleCount + 1,
        afterBundleCount);

    // Schedule Activating the Bundle
    int noOfdays1 = 0;
    Calendar scheduleActivatedAt = Calendar.getInstance();
    scheduleActivatedAt.add(Calendar.DATE, 0 - noOfdays1);
    ProductForm postForm = new ProductForm();
    postForm.setStartDate(new Date());
    BindingResult result = validate(postForm);
    String scheduleActivationStatus = productsController.setPlanDate(postForm, result, map);
    Assert.assertNotNull(" scheduleActivationStatus is null ", scheduleActivationStatus);
    Assert.assertEquals("scheduleActivationStatus status is not success", "success", scheduleActivationStatus);

    // Sync channel with reference price book
    String syncStatus = channelController.syncChannel(channel.getId().toString(), map);
    Assert.assertNotNull(" syncStatus is null ", syncStatus);
    Assert.assertEquals("sync status is not success", "success", syncStatus);

    // Attaching product bundle to the channel
    String selectedProductBundles = "[" + obtainedBundle.getId().toString() + "]";
    String result1 = channelController.attachProductBundles(channel.getId().toString(), selectedProductBundles, map);
    Assert.assertNotNull(" attachProductBundles returned null ", result1);
    Assert.assertEquals("attachProductBundles status is not success", "success", result1);

    // Schedule Activating the channel
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    String currentdate = sdf.format(new Date());
    String cres = channelController.changePlanDate(channel.getId().toString(), currentdate, "MM/dd/yyyy", map);
    Assert.assertNotNull("changePlanDate returned null", cres);
    Assert.assertEquals("checking status of schudeactivation for a channel", "success", cres);

    // Checking as Master User
    asUser(tenant.getOwner());

    // Getting the list of bundles on catalog page
    request.setAttribute("effectiveTenant", tenant);
    String currencyCode = channelService.listCurrencies(channel.getParam()).get(0).getCurrencyCode();
    List<ProductBundleRevision> pbrlist = bundleController.listProductBundles(tenant, tenant.getParam(),
        getServiceInstanceByID(1L).getUuid(), "VirtualMachine", "zone=121,Template=200,SO=20, hypervisor=KVM",
        "PSI_UD1=10", true, "current", channel.getId().toString(), currencyCode, null, null, request);

    // Verify the Bundle is not available for the channel to add
    boolean bundleListingStatus1 = false;
    for (ProductBundleRevision pbr : pbrlist) {
      if (pbr.getProductBundle().getName().equalsIgnoreCase(obtainedBundle.getName())) {
        bundleListingStatus1 = true;
      }
    }

    Assert.assertTrue("Bundle should be listed ", bundleListingStatus1);

  }

  /**
   * Description : To Test bundles with SO as provisional constraint Included , will be filtered as per the Provisional
   * constraint given on catalog page
   * 
   * @author nageswarap
   * @throws Exception
   */
  @Test
  public void testResourceBundleSubscriptionWithNoprovisionConstraintsUnPublished() throws Exception {

    Tenant tenant = tenantService.getTenantByParam("id", "9", false);
    Channel channel = channelService.getChannelById("3");
    ServiceResourceType resourceType = connectorConfigurationManager.getServiceResourceTypeById(1L);
    String chargeFrequency = "MONTHLY";
    String compAssociationJson = "[]";
    int noOfdays = 3;
    Calendar createdAt = Calendar.getInstance();
    createdAt.add(Calendar.DATE, 0 - noOfdays);
    int beforeBundleCount = bundleservice.getBundlesCount();
    boolean trialEligible = false;

    // Create a Bundle
    ProductBundle obtainedBundle = testCreateProductBundle("1", resourceType.getId().toString(), chargeFrequency,
        chargeFrequency + "Compute", "USD", BigDecimal.valueOf(100), createdAt.getTime(), compAssociationJson,
        ResourceConstraint.NONE, trialEligible);
    Assert.assertNotNull("The bundle is null ", obtainedBundle);
    Assert.assertEquals("the expected bundle name and the actual bundle name is not matching", chargeFrequency
        + "Compute", obtainedBundle.getName());
    Assert.assertEquals("Bundle resource type is not matching", resourceType, obtainedBundle.getResourceType());

    int afterBundleCount = bundleservice.getBundlesCount();
    Assert.assertEquals("bundle count not incremented after creating the bundle", beforeBundleCount + 1,
        afterBundleCount);

    // Schedule Activating the Bundle
    int noOfdays1 = 0;
    Calendar scheduleActivatedAt = Calendar.getInstance();
    scheduleActivatedAt.add(Calendar.DATE, 0 - noOfdays1);
    ProductForm postForm = new ProductForm();
    postForm.setStartDate(new Date());
    BindingResult result = validate(postForm);
    String scheduleActivationStatus = productsController.setPlanDate(postForm, result, map);
    Assert.assertNotNull(" scheduleActivationStatus is null ", scheduleActivationStatus);
    Assert.assertEquals("scheduleActivationStatus status is not success", "success", scheduleActivationStatus);

    // Sync channel with reference price book
    String syncStatus = channelController.syncChannel(channel.getId().toString(), map);
    Assert.assertNotNull(" syncStatus is null ", syncStatus);
    Assert.assertEquals("sync status is not success", "success", syncStatus);

    // Attaching product bundle to the channel
    String selectedProductBundles = "[" + obtainedBundle.getId().toString() + "]";
    String result1 = channelController.attachProductBundles(channel.getId().toString(), selectedProductBundles, map);
    Assert.assertNotNull(" attachProductBundles returned null ", result1);
    Assert.assertEquals("attachProductBundles status is not success", "success", result1);

    // unpublish the bundle
    obtainedBundle.setPublish(false);
    bundleservice.updateProductBundle(obtainedBundle, false);

    // Schedule Activating the channel
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    String currentdate = sdf.format(new Date());
    String cres = channelController.changePlanDate(channel.getId().toString(), currentdate, "MM/dd/yyyy", map);
    Assert.assertNotNull("changePlanDate returned null", cres);
    Assert.assertEquals("checking status of schudeactivation for a channel", "success", cres);

    // Checking as Master User
    asUser(tenant.getOwner());

    // Getting the list of bundles on catalog page
    request.setAttribute("effectiveTenant", tenant);
    String currencyCode = channelService.listCurrencies(channel.getParam()).get(0).getCurrencyCode();
    List<ProductBundleRevision> pbrlist = bundleController.listProductBundles(tenant, tenant.getParam(),
        getServiceInstanceByID(1L).getUuid(), "VirtualMachine", "zone=121,Template=200,SO=20, hypervisor=KVM",
        "PSI_UD1=10", true, "current", channel.getId().toString(), currencyCode, null, null, request);

    // Verify the Bundle is not available for the channel to add
    boolean bundleListingStatus1 = false;
    for (ProductBundleRevision pbr : pbrlist) {
      if (pbr.getProductBundle().getName().equalsIgnoreCase(obtainedBundle.getName())) {
        bundleListingStatus1 = true;
      }
    }

    Assert.assertFalse("Bundle should not be listed ", bundleListingStatus1);

  }

  /**
   * Description : To Test bundles with SO as provisional constraint Included , will be filtered as per the Provisional
   * constraint given on catalog page
   * 
   * @author nageswarap
   * @throws Exception
   */
  @Test
  public void testResourceBundleSubscriptionWithNoprovisionConstraintsUnAttached() throws Exception {

    Tenant tenant = tenantService.getTenantByParam("id", "9", false);
    Channel channel = channelService.getChannelById("3");
    ServiceResourceType resourceType = connectorConfigurationManager.getServiceResourceTypeById(1L);
    String chargeFrequency = "MONTHLY";
    String compAssociationJson = "[]";
    int noOfdays = 3;
    Calendar createdAt = Calendar.getInstance();
    createdAt.add(Calendar.DATE, 0 - noOfdays);
    int beforeBundleCount = bundleservice.getBundlesCount();
    boolean trialEligible = false;

    // Create a Bundle
    ProductBundle obtainedBundle = testCreateProductBundle("1", resourceType.getId().toString(), chargeFrequency,
        chargeFrequency + "Compute", "USD", BigDecimal.valueOf(100), createdAt.getTime(), compAssociationJson,
        ResourceConstraint.NONE, trialEligible);
    Assert.assertNotNull("The bundle is null ", obtainedBundle);
    Assert.assertEquals("the expected bundle name and the actual bundle name is not matching", chargeFrequency
        + "Compute", obtainedBundle.getName());
    Assert.assertEquals("Bundle resource type is not matching", resourceType, obtainedBundle.getResourceType());

    int afterBundleCount = bundleservice.getBundlesCount();
    Assert.assertEquals("bundle count not incremented after creating the bundle", beforeBundleCount + 1,
        afterBundleCount);

    // Schedule Activating the Bundle
    int noOfdays1 = 0;
    Calendar scheduleActivatedAt = Calendar.getInstance();
    scheduleActivatedAt.add(Calendar.DATE, 0 - noOfdays1);
    ProductForm postForm = new ProductForm();
    postForm.setStartDate(new Date());
    BindingResult result = validate(postForm);
    String scheduleActivationStatus = productsController.setPlanDate(postForm, result, map);
    Assert.assertNotNull(" scheduleActivationStatus is null ", scheduleActivationStatus);
    Assert.assertEquals("scheduleActivationStatus status is not success", "success", scheduleActivationStatus);

    // Sync channel with reference price book
    String syncStatus = channelController.syncChannel(channel.getId().toString(), map);
    Assert.assertNotNull(" syncStatus is null ", syncStatus);
    Assert.assertEquals("sync status is not success", "success", syncStatus);

    // Schedule Activating the channel
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    String currentdate = sdf.format(new Date());
    String cres = channelController.changePlanDate(channel.getId().toString(), currentdate, "MM/dd/yyyy", map);
    Assert.assertNotNull("changePlanDate returned null", cres);
    Assert.assertEquals("checking status of schudeactivation for a channel", "success", cres);

    // Checking as Master User
    asUser(tenant.getOwner());

    // Getting the list of bundles on catalog page
    request.setAttribute("effectiveTenant", tenant);
    String currencyCode = channelService.listCurrencies(channel.getParam()).get(0).getCurrencyCode();
    List<ProductBundleRevision> pbrlist = bundleController.listProductBundles(tenant, tenant.getParam(),
        getServiceInstanceByID(1L).getUuid(), "VirtualMachine", "zone=121,Template=200,SO=20, hypervisor=KVM",
        "PSI_UD1=10", true, "current", channel.getId().toString(), currencyCode, null, null, request);

    // Verify the Bundle is not available for the channel to add
    boolean bundleListingStatus1 = false;
    for (ProductBundleRevision pbr : pbrlist) {
      if (pbr.getProductBundle().getName().equalsIgnoreCase(obtainedBundle.getName())) {
        bundleListingStatus1 = true;
      }
    }

    Assert.assertFalse("Bundle should not be listed ", bundleListingStatus1);

  }

  /*
   * Description: Private Test to create Bundles based on the parameters Author: Vinayv
   */
  private ProductBundle testCreateProductBundle(String serviceInstanceID, String resourceTypeID, String chargeType,
      String bundleName, String currencyCode, BigDecimal currencyValue, Date startDate, String jsonString,
      ResourceConstraint businessConstraint, boolean trialEligible) throws Exception {

    ServiceInstance serviceInstance = serviceInstanceDAO.find(serviceInstanceID);
    ServiceResourceType resourceType = null;
    if (!resourceTypeID.equalsIgnoreCase("ServiceBundle")) {
      resourceType = connectorConfigurationManager.getServiceResourceTypeById(Long.parseLong(resourceTypeID));
    }
    List<RateCardCharge> rateCardChargeList = new ArrayList<RateCardCharge>();
    RateCardCharge rcc = new RateCardCharge(currencyValueService.locateBYCurrencyCode(currencyCode), null,
        currencyValue, "RateCharge", getRootUser(), getRootUser(), channelService.getFutureRevision(null));
    rateCardChargeList.add(rcc);
    String chargeTypeName = chargeType;
    if (chargeType.equalsIgnoreCase("Invalid")) {
      chargeTypeName = "NONE";
    }
    RateCard rateCard = new RateCard("Rate", bundleservice.getChargeRecurrencyFrequencyByName(chargeTypeName),
        new Date(), getRootUser(), getRootUser());
    String compAssociationJson = jsonString;

    ProductBundle bundle = new ProductBundle(bundleName, bundleName, "", startDate, startDate, getRootUser());
    bundle.setBusinessConstraint(businessConstraint);
    bundle.setCode(bundleName);
    bundle.setPublish(true);
    if (!resourceTypeID.equalsIgnoreCase("ServiceBundle")) {
      bundle.setResourceType(resourceType);
    }
    bundle.setServiceInstanceId(serviceInstance);
    bundle.setTrialEligibility(trialEligible);
    bundle.setRateCard(rateCard);

    ProductBundleForm form = new ProductBundleForm(bundle);
    form.setChargeType(chargeType);
    if (!resourceTypeID.equalsIgnoreCase("ServiceBundle")) {
      form.setResourceType(resourceType.getId().toString());
    } else {
      form.setResourceType("sb");
    }
    form.setServiceInstanceUUID(serviceInstance.getUuid());
    form.setBundleOneTimeCharges(rateCardChargeList);
    if (!chargeType.equalsIgnoreCase("NONE")) {
      form.setBundleRecurringCharges(rateCardChargeList);
    }
    form.setCompAssociationJson(compAssociationJson);

    BindingResult result = validate(form);
    ProductBundle obtainedBundle = bundleController.createProductBundle(form, result, map, response);

    return obtainedBundle;
  }

  /**
   * Description : To Test network bundles with zone as provisional constraint Included , will be filtered as per the
   * Provisional constraint given on catalog page
   * 
   * @author nageswarap
   * @throws Exception
   */
  @Test
  public void testNetworkBundleSubscriptionAsZoneIncludes() throws Exception {

    boolean bundleListingStatus;
    String NetworkOffering = "NetworkOfferingwithOutVPC";

    // Retreiving tenant, bundle and channel information required to execute the test case
    Tenant tenant = tenantService.getTenantByParam("id", "6", false);
    ProductBundle bundle = bundleservice.getProductBundleById(3L);
    Channel channel = channelService.getChannelById("5");

    // Assigning provisioning constraint test values for zone
    String zone = "123";

    // creating provisioning constraint map
    List<Map<String, Object>> provisioningConstraints = new ArrayList<Map<String, Object>>();
    createProvisioningConstraintsMap(provisioningConstraints, "zone", AssociationType.INCLUDES, zone, "zone");

    // adding the provisioning constraint to the bundle and syncing the channel
    boolean status = updateBundleConstraints(bundle.getId(), channel.getId().toString(), provisioningConstraints);
    Assert.assertNotNull("syncChannel returned null", status);

    // syncing and scheduleactivating the channel
    syncAndScheduleActivateAChannel(channel);

    // creating the filter params map before subscribing to network bundle
    Map<String, Object> bundlefilterparams = bundleFilterParams(NetworkOffering, zone, "Network", 1L);

    // get the bundle listing status for the given filter params
    bundleListingStatus = getBundleListingStatusInCatalogPage(tenant, bundle, channel, bundlefilterparams);

    // verifying the bundle is listed or not for the filter params
    Assert.assertTrue("Bundle is not listing with a valid selection of zone, which is included in the bundle ",
        bundleListingStatus);
  }

  private Map<String, Object> bundleFilterParams(String NetworkOffering, String zone, String serviceResourceType,
      Long servivceInstanceId) {
    Map<String, Object> bundlefilterparams = new HashMap<String, Object>();
    bundlefilterparams.put("ServiceInstanceUUID", getServiceInstanceByID(servivceInstanceId).getUuid());
    bundlefilterparams.put("serviceResourceType", serviceResourceType);
    bundlefilterparams.put("filters", "zone=" + zone);
    bundlefilterparams.put("context", "NetworkOffering=" + NetworkOffering);
    return bundlefilterparams;
  }

  /**
   * Description : To Test network bundles with zone as provisional constraint Excludes , will be filtered as per the
   * Provisional constraint given on catalog page
   * 
   * @author nageswarap
   * @throws Exception
   */
  @Test
  public void testNetworkBundleSubscriptionAsZoneExcludes() throws Exception {

    boolean bundleListingStatus;
    String NetworkOffering = "NetworkOfferingwithOutVPC";

    // Retreiving tenant, bundle and channel information required to execute the test case
    Tenant tenant = tenantService.getTenantByParam("id", "6", false);
    ProductBundle bundle = bundleservice.getProductBundleById(3L);
    Channel channel = channelService.getChannelById("5");

    // Assigning provisioning constraint test values for zone
    String zone = "123";

    // creating provisioning constraint map
    List<Map<String, Object>> provisioningConstraints = new ArrayList<Map<String, Object>>();
    createProvisioningConstraintsMap(provisioningConstraints, "zone", AssociationType.EXCLUDES, zone, "zone");

    // adding the provisioning constraint to the bundle and syncing the channel
    boolean status = updateBundleConstraints(bundle.getId(), channel.getId().toString(), provisioningConstraints);
    Assert.assertNotNull("syncChannel returned null", status);

    // syncing and scheduleactivating the channel
    syncAndScheduleActivateAChannel(channel);

    // Schedule Activating the channel
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    String currentdate = sdf.format(new Date());
    String cres = channelController.changePlanDate(channel.getId().toString(), currentdate, "MM/dd/yyyy", map);
    Assert.assertNotNull("changePlanDate returned null", cres);
    Assert.assertEquals("checking status of schudeactivation for a channel", "success", cres);

    Map<String, Object> bundlefilterparams = bundleFilterParams(NetworkOffering, zone, "Network", 1L);

    // get the bundle listing status for the given filter params
    bundleListingStatus = getBundleListingStatusInCatalogPage(tenant, bundle, channel, bundlefilterparams);

    // verifying the bundle is listed or not for the filter params
    Assert.assertFalse(
        "Bundle is listing with a valid selection of zone when it EXCLUDES, which is included in the bundle ",
        bundleListingStatus);
  }

  /**
   * Description : To Test network bundles with zone as provisional constraint Includes and invalid selection , will be
   * filtered as per the Provisional constraint given on catalog page
   * 
   * @author nageswarap
   * @throws Exception
   */
  @Test
  public void testNetworkBundleSubscriptionAsZoneIncludesInvalidSelection() throws Exception {

    boolean bundleListingStatus;
    String NetworkOffering = "NetworkOfferingwithOutVPC";

    // Retreiving tenant, bundle and channel information required to execute the test case
    Tenant tenant = tenantService.getTenantByParam("id", "6", false);
    ProductBundle bundle = bundleservice.getProductBundleById(3L);
    Channel channel = channelService.getChannelById("5");

    // Assigning provisioning constraint test values for zone
    String zone = "123";

    // creating provisioning constraint map
    List<Map<String, Object>> provisioningConstraints = new ArrayList<Map<String, Object>>();
    createProvisioningConstraintsMap(provisioningConstraints, "zone", AssociationType.INCLUDES, zone, "zone");

    // adding the provisioning constraint to the bundle and syncing the channel
    boolean status = updateBundleConstraints(bundle.getId(), channel.getId().toString(), provisioningConstraints);
    Assert.assertNotNull("syncChannel returned null", status);

    // syncing and scheduleactivating the channel
    syncAndScheduleActivateAChannel(channel);

    // creating the filter params map before subscribing to network bundle
    Map<String, Object> bundlefilterparams = bundleFilterParams(NetworkOffering, "120", "Network", 1L);

    // get the bundle listing status for the given filter params
    bundleListingStatus = getBundleListingStatusInCatalogPage(tenant, bundle, channel, bundlefilterparams);

    // verifying the bundle is listed or not for the filter params
    // the bundle should not be listed
    Assert.assertFalse(
        "Bundle is listing with a invalid selection of zone when it Includes, which is included in the bundle ",
        bundleListingStatus);
  }

  /**
   * Description : To Test network bundles with zone as provisional constraint Excludes and invalid selection, will be
   * filtered as per the Provisional constraint given on catalog page
   * 
   * @author nageswarap
   * @throws Exception
   */
  @Test
  public void testNetworkBundleSubscriptionAsZoneExcludesInvalidSelection() throws Exception {

    boolean bundleListingStatus;
    String NetworkOffering = "NetworkOfferingwithOutVPC";

    // Retreiving tenant, bundle and channel information required to execute the test case
    Tenant tenant = tenantService.getTenantByParam("id", "6", false);
    ProductBundle bundle = bundleservice.getProductBundleById(3L);
    Channel channel = channelService.getChannelById("5");

    // Assigning provisioning constraint test values for zone
    String zone = "123";

    // creating provisioning constraint map
    List<Map<String, Object>> provisioningConstraints = new ArrayList<Map<String, Object>>();
    createProvisioningConstraintsMap(provisioningConstraints, "zone", AssociationType.EXCLUDES, zone, "zone");

    // adding the provisioning constraint to the bundle and syncing the channel
    boolean status = updateBundleConstraints(bundle.getId(), channel.getId().toString(), provisioningConstraints);
    Assert.assertNotNull("syncChannel returned null", status);

    // syncing and scheduleactivating the channel
    syncAndScheduleActivateAChannel(channel);

    // creating the filter params map before subscribing to network bundle
    Map<String, Object> bundlefilterparams = bundleFilterParams(NetworkOffering, "120", "Network", 1L);

    // get the bundle listing status for the given filter params
    bundleListingStatus = getBundleListingStatusInCatalogPage(tenant, bundle, channel, bundlefilterparams);

    // verifying the bundle is listed or not for the filter params
    Assert.assertTrue(
        "Bundle is not listing with a invalid selection of zone when it EXCLUDES, which is included in the bundle ",
        bundleListingStatus);
  }

  /**
   * Description : To Test network bundles with NetworkOffering as provisional constraint Includes , will be filtered as
   * per the Provisional constraint given on catalog page
   * 
   * @author nageswarap
   * @throws Exception
   */
  @Test
  public void testNetworkBundleSubscriptionAsNetworkOfferingIncludes() throws Exception {

    boolean bundleListingStatus;

    // Retreiving tenant, bundle and channel information required to execute the test case
    Tenant tenant = tenantService.getTenantByParam("id", "6", false);
    ProductBundle bundle = bundleservice.getProductBundleById(3L);
    Channel channel = channelService.getChannelById("5");

    // Assigning provisioning constraint test values for zone
    String zone = "123";
    String NetworkOffering = "NetworkOfferingwithOutVPC";

    // creating provisioning constraint map
    List<Map<String, Object>> provisioningConstraints = new ArrayList<Map<String, Object>>();
    createProvisioningConstraintsMap(provisioningConstraints, "NetworkOffering", AssociationType.INCLUDES,
        NetworkOffering, "NetworkOffering");

    // adding the provisioning constraint to the bundle and syncing the channel
    boolean status = updateBundleConstraints(bundle.getId(), channel.getId().toString(), provisioningConstraints);
    Assert.assertNotNull("syncChannel returned null", status);

    // syncing and scheduleactivating the channel
    syncAndScheduleActivateAChannel(channel);

    Map<String, Object> bundlefilterparams = bundleFilterParams(NetworkOffering, zone, "Network", 1L);

    // get the bundle listing status for the given filter params
    bundleListingStatus = getBundleListingStatusInCatalogPage(tenant, bundle, channel, bundlefilterparams);

    // verifying the bundle is listed or not for the filter params
    Assert
        .assertTrue(
            "Bundle is not listing with a valid selection of NetworkOffering when it INCLUDES, which is included in the bundle ",
            bundleListingStatus);
  }

  /**
   * Description : To Test network bundles with NetworkOffering as provisional constraint Includes Invalid Selection,
   * will be filtered as per the Provisional constraint given on catalog page
   * 
   * @author nageswarap
   * @throws Exception
   */
  @Test
  public void testNetworkBundleSubscriptionAsNetworkOfferingIncludesInvalidSelection() throws Exception {

    boolean bundleListingStatus;

    // Retreiving tenant, bundle and channel information required to execute the test case
    Tenant tenant = tenantService.getTenantByParam("id", "6", false);
    ProductBundle bundle = bundleservice.getProductBundleById(3L);
    Channel channel = channelService.getChannelById("5");

    // Assigning provisioning constraint test values for zone
    String zone = "123";
    String NetworkOffering = "NetworkOfferingwithOutVPC";

    // creating provisioning constraint map
    List<Map<String, Object>> provisioningConstraints = new ArrayList<Map<String, Object>>();
    createProvisioningConstraintsMap(provisioningConstraints, "NetworkOffering", AssociationType.INCLUDES,
        NetworkOffering, "NetworkOffering");

    // adding the provisioning constraint to the bundle and syncing the channel
    boolean status = updateBundleConstraints(bundle.getId(), channel.getId().toString(), provisioningConstraints);
    Assert.assertNotNull("syncChannel returned null", status);

    // syncing and scheduleactivating the channel
    syncAndScheduleActivateAChannel(channel);

    // creating the filter params map before subscribing to network bundle
    Map<String, Object> bundlefilterparams = bundleFilterParams("NetworkOfferingwithOutVPC1", zone, "Network", 1L);

    // get the bundle listing status for the given filter params
    bundleListingStatus = getBundleListingStatusInCatalogPage(tenant, bundle, channel, bundlefilterparams);

    // verifying the bundle is listed or not for the filter params
    Assert
        .assertFalse(
            "Bundle is listing with a invalid selection of NetworkOffering when it INCLUDES, which is included in the bundle ",
            bundleListingStatus);
  }

  /**
   * Description : To Test network bundles with NetworkOffering as provisional constraint Excludes , will be filtered as
   * per the Provisional constraint given on catalog page
   * 
   * @author nageswarap
   * @throws Exception
   */
  @Test
  public void testNetworkBundleSubscriptionAsNetworkOfferingExcludes() throws Exception {

    boolean bundleListingStatus;

    // Retreiving tenant, bundle and channel information required to execute the test case
    Tenant tenant = tenantService.getTenantByParam("id", "6", false);
    ProductBundle bundle = bundleservice.getProductBundleById(3L);
    Channel channel = channelService.getChannelById("5");

    // Assigning provisioning constraint test values for zone
    String zone = "123";
    String NetworkOffering = "NetworkOfferingwithOutVPC";

    // creating provisioning constraint map
    List<Map<String, Object>> provisioningConstraints = new ArrayList<Map<String, Object>>();
    createProvisioningConstraintsMap(provisioningConstraints, "NetworkOffering", AssociationType.EXCLUDES,
        NetworkOffering, "NetworkOffering");

    // adding the provisioning constraint to the bundle and syncing the channel
    boolean status = updateBundleConstraints(bundle.getId(), channel.getId().toString(), provisioningConstraints);
    Assert.assertNotNull("syncChannel returned null", status);

    // syncing and scheduleactivating the channel
    syncAndScheduleActivateAChannel(channel);

    Map<String, Object> bundlefilterparams = bundleFilterParams(NetworkOffering, zone, "Network", 1L);

    // get the bundle listing status for the given filter params
    bundleListingStatus = getBundleListingStatusInCatalogPage(tenant, bundle, channel, bundlefilterparams);

    // verifying the bundle is listed or not for the filter params
    Assert
        .assertFalse(
            "Bundle is listing with a invalid selection of NetworkOffering when it Excludes, which is included in the bundle ",
            bundleListingStatus);
  }

  /**
   * Description : To Test network bundles with NetworkOffering as provisional constraint excludes invalid selection ,
   * will be filtered as per the Provisional constraint given on catalog page
   * 
   * @author nageswarap
   * @throws Exception
   */
  @Test
  public void testNetworkBundleSubscriptionAsNetworkOfferingExcludesInvalidSelection() throws Exception {

    boolean bundleListingStatus;

    // Retreiving tenant, bundle and channel information required to execute the test case
    Tenant tenant = tenantService.getTenantByParam("id", "6", false);
    ProductBundle bundle = bundleservice.getProductBundleById(3L);
    Channel channel = channelService.getChannelById("5");

    // Assigning provisioning constraint test values for zone
    String zone = "123";
    String NetworkOffering = "NetworkOfferingwithOutVPC";

    // creating provisioning constraint map
    List<Map<String, Object>> provisioningConstraints = new ArrayList<Map<String, Object>>();
    createProvisioningConstraintsMap(provisioningConstraints, "NetworkOffering", AssociationType.EXCLUDES,
        NetworkOffering, "NetworkOffering");

    // adding the provisioning constraint to the bundle and syncing the channel
    boolean status = updateBundleConstraints(bundle.getId(), channel.getId().toString(), provisioningConstraints);
    Assert.assertNotNull("syncChannel returned null", status);

    // syncing and scheduleactivating the channel
    syncAndScheduleActivateAChannel(channel);

    // creating the filter params map before subscribing to network bundle
    Map<String, Object> bundlefilterparams = bundleFilterParams("NetworkOfferingwithOutVPC1", zone, "Network", 1L);

    // get the bundle listing status for the given filter params
    bundleListingStatus = getBundleListingStatusInCatalogPage(tenant, bundle, channel, bundlefilterparams);

    // verifying the bundle is listed or not for the filter params
    Assert
        .assertTrue(
            "Bundle is not listing with a invalid selection of NetworkOffering when it EXCLUDES, which is included in the bundle ",
            bundleListingStatus);
  }

  /**
   * Description : To Test network bundles with Zone as provisional constraint Includes and NetworkOffering as
   * provisional constraint Includes , will be filtered as per the Provisional constraint given on catalog page
   * 
   * @author nageswarap
   * @throws Exception
   */
  @Test
  public void testNetworkBundleSubscriptionAsZoneIncludesNetworkOfferingIncludes() throws Exception {

    boolean bundleListingStatus;

    // Retreiving tenant, bundle and channel information required to execute the test case
    Tenant tenant = tenantService.getTenantByParam("id", "6", false);
    ProductBundle bundle = bundleservice.getProductBundleById(3L);
    Channel channel = channelService.getChannelById("5");

    // Assigning provisioning constraint test values for zone
    String zone = "123";
    String NetworkOffering = "NetworkOfferingwithOutVPC";

    // creating provisioning constraint map
    List<Map<String, Object>> provisioningConstraints = new ArrayList<Map<String, Object>>();
    createProvisioningConstraintsMap(provisioningConstraints, "zone", AssociationType.INCLUDES, zone, "zone");
    createProvisioningConstraintsMap(provisioningConstraints, "NetworkOffering", AssociationType.INCLUDES,
        NetworkOffering, "NetworkOffering");

    // adding the provisioning constraint to the bundle and syncing the channel
    boolean status = updateBundleConstraints(bundle.getId(), channel.getId().toString(), provisioningConstraints);
    Assert.assertNotNull("syncChannel returned null", status);

    // syncing and scheduleactivating the channel
    syncAndScheduleActivateAChannel(channel);

    Map<String, Object> bundlefilterparams = bundleFilterParams(NetworkOffering, zone, "Network", 1L);

    // get the bundle listing status for the given filter params
    bundleListingStatus = getBundleListingStatusInCatalogPage(tenant, bundle, channel, bundlefilterparams);

    // verifying the bundle is listed or not for the filter params
    Assert
        .assertTrue(
            "Bundle is not listing with a valid selection of Zone  as provisional constraint Includes and NetworkOffering as provisional constraint Includes, which is included in the bundle ",
            bundleListingStatus);
  }

  /**
   * Description : To Test network bundles with Zone as provisional constraint Includes and NetworkOffering as
   * provisional constraint Excludes , will be filtered as per the Provisional constraint given on catalog page
   * 
   * @author nageswarap
   * @throws Exception
   */
  @Test
  public void testNetworkBundleSubscriptionAsZoneIncludesNetworkOfferingExcludes() throws Exception {

    boolean bundleListingStatus;

    // Retreiving tenant, bundle and channel information required to execute the test case
    Tenant tenant = tenantService.getTenantByParam("id", "6", false);
    ProductBundle bundle = bundleservice.getProductBundleById(3L);
    Channel channel = channelService.getChannelById("5");

    // Assigning provisioning constraint test values for zone
    String zone = "123";
    String NetworkOffering = "NetworkOfferingwithOutVPC";

    // creating provisioning constraint map
    List<Map<String, Object>> provisioningConstraints = new ArrayList<Map<String, Object>>();
    createProvisioningConstraintsMap(provisioningConstraints, "zone", AssociationType.INCLUDES, zone, "zone");
    createProvisioningConstraintsMap(provisioningConstraints, "NetworkOffering", AssociationType.EXCLUDES,
        NetworkOffering, "NetworkOffering");

    // adding the provisioning constraint to the bundle and syncing the channel
    boolean status = updateBundleConstraints(bundle.getId(), channel.getId().toString(), provisioningConstraints);
    Assert.assertNotNull("syncChannel returned null", status);

    // syncing and scheduleactivating the channel
    syncAndScheduleActivateAChannel(channel);

    Map<String, Object> bundlefilterparams = bundleFilterParams(NetworkOffering, zone, "Network", 1L);

    // get the bundle listing status for the given filter params
    bundleListingStatus = getBundleListingStatusInCatalogPage(tenant, bundle, channel, bundlefilterparams);

    // verifying the bundle is listed or not for the filter params
    Assert
        .assertFalse(
            "Bundle is listing with a valid selection ofZone  as provisional constraint Includes and NetworkOffering as provisional constraint Excludes, which is included in the bundle ",
            bundleListingStatus);
  }

  /**
   * Description : To Test network bundles with Zone as provisional constraint Includes and NetworkOffering as
   * provisional constraint Includes with Invalid selection , will be filtered as per the Provisional constraint given
   * on catalog page
   * 
   * @author nageswarap
   * @throws Exception
   */
  @Test
  public void testNetworkBundleSubscriptionAsZoneIncludesNetworkOfferingIncludeInvalidSelection() throws Exception {

    boolean bundleListingStatus;

    // Retreiving tenant, bundle and channel information required to execute the test case
    Tenant tenant = tenantService.getTenantByParam("id", "6", false);
    ProductBundle bundle = bundleservice.getProductBundleById(3L);
    Channel channel = channelService.getChannelById("5");

    // Assigning provisioning constraint test values for zone
    String zone = "123";
    String NetworkOffering = "NetworkOfferingwithOutVPC";

    // creating provisioning constraint map
    List<Map<String, Object>> provisioningConstraints = new ArrayList<Map<String, Object>>();
    createProvisioningConstraintsMap(provisioningConstraints, "zone", AssociationType.INCLUDES, zone, "zone");
    createProvisioningConstraintsMap(provisioningConstraints, "NetworkOffering", AssociationType.INCLUDES,
        NetworkOffering, "NetworkOffering");

    // adding the provisioning constraint to the bundle and syncing the channel
    boolean status = updateBundleConstraints(bundle.getId(), channel.getId().toString(), provisioningConstraints);
    Assert.assertNotNull("syncChannel returned null", status);

    // syncing and scheduleactivating the channel
    syncAndScheduleActivateAChannel(channel);

    // creating the filter params map before subscribing to network bundle
    Map<String, Object> bundlefilterparams = bundleFilterParams("NetworkOfferingwithOutVPC1", zone, "Network", 1L);

    // get the bundle listing status for the given filter params
    bundleListingStatus = getBundleListingStatusInCatalogPage(tenant, bundle, channel, bundlefilterparams);

    // verifying the bundle is listed or not for the filter params
    Assert
        .assertFalse(
            "Bundle is listing with a valid selection of Zone  as provisional constraint Includes and NetworkOffering as provisional constraint Includes with invalid selection, which is included in the bundle ",
            bundleListingStatus);
  }

  /**
   * Description : To Test network bundles with Zone as provisional constraint Includes with invalid selection and
   * NetworkOffering as provisional constraint Includes , will be filtered as per the Provisional constraint given on
   * catalog page
   * 
   * @author nageswarap
   * @throws Exception
   */
  @Test
  public void testNetworkBundleSubscriptionAsZoneIncludesInvalidSelectionNetworkOfferingIncludes() throws Exception {

    boolean bundleListingStatus;

    // Retreiving tenant, bundle and channel information required to execute the test case
    Tenant tenant = tenantService.getTenantByParam("id", "6", false);
    ProductBundle bundle = bundleservice.getProductBundleById(3L);
    Channel channel = channelService.getChannelById("5");

    // Assigning provisioning constraint test values for zone
    String zone = "123";
    String NetworkOffering = "NetworkOfferingwithOutVPC";

    // creating provisioning constraint map
    List<Map<String, Object>> provisioningConstraints = new ArrayList<Map<String, Object>>();
    createProvisioningConstraintsMap(provisioningConstraints, "zone", AssociationType.INCLUDES, zone, "zone");
    createProvisioningConstraintsMap(provisioningConstraints, "NetworkOffering", AssociationType.INCLUDES,
        NetworkOffering, "NetworkOffering");

    // adding the provisioning constraint to the bundle and syncing the channel
    boolean status = updateBundleConstraints(bundle.getId(), channel.getId().toString(), provisioningConstraints);
    Assert.assertNotNull("syncChannel returned null", status);

    // syncing and scheduleactivating the channel
    syncAndScheduleActivateAChannel(channel);

    // creating the filter params map before subscribing to network bundle
    Map<String, Object> bundlefilterparams = bundleFilterParams(NetworkOffering, "zone120", "Network", 1L);

    // get the bundle listing status for the given filter params
    bundleListingStatus = getBundleListingStatusInCatalogPage(tenant, bundle, channel, bundlefilterparams);

    // verifying the bundle is listed or not for the filter params
    Assert
        .assertFalse(
            "Bundle is listing with a valid selection of Zone  as provisional constraint Includes with invalid selection and NetworkOffering as provisional constraint Includes, which is included in the bundle ",
            bundleListingStatus);
  }

  /**
   * Description : To Test network bundles with Zone as provisional constraint Includes and NetworkOffering as
   * provisional constraint Excludes with invalid selection , will be filtered as per the Provisional constraint given
   * on catalog page
   * 
   * @author nageswarap
   * @throws Exception
   */
  @Test
  public void testNetworkBundleSubscriptionAsZoneIncludesNetworkOfferingExcludesInvalidSelection() throws Exception {

    boolean bundleListingStatus;

    // Retreiving tenant, bundle and channel information required to execute the test case
    Tenant tenant = tenantService.getTenantByParam("id", "6", false);
    ProductBundle bundle = bundleservice.getProductBundleById(3L);
    Channel channel = channelService.getChannelById("5");

    // Assigning provisioning constraint test values for zone
    String zone = "123";
    String NetworkOffering = "NetworkOfferingwithOutVPC";

    // creating provisioning constraint map
    List<Map<String, Object>> provisioningConstraints = new ArrayList<Map<String, Object>>();
    createProvisioningConstraintsMap(provisioningConstraints, "zone", AssociationType.INCLUDES, zone, "zone");
    createProvisioningConstraintsMap(provisioningConstraints, "NetworkOffering", AssociationType.EXCLUDES,
        NetworkOffering, "NetworkOffering");

    // adding the provisioning constraint to the bundle and syncing the channel
    boolean status = updateBundleConstraints(bundle.getId(), channel.getId().toString(), provisioningConstraints);
    Assert.assertNotNull("syncChannel returned null", status);

    // syncing and scheduleactivating the channel
    syncAndScheduleActivateAChannel(channel);

    // creating the filter params map before subscribing to network bundle
    Map<String, Object> bundlefilterparams = bundleFilterParams("NetworkOfferingwithOutVPC1", zone, "Network", 1L);

    // get the bundle listing status for the given filter params
    bundleListingStatus = getBundleListingStatusInCatalogPage(tenant, bundle, channel, bundlefilterparams);

    // verifying the bundle is listed or not for the filter params
    Assert
        .assertTrue(
            "Bundle is not listing with Zone  as provisional constraint Includes and NetworkOffering as provisional constraint Excludes with invalid selection , which is included in the bundle ",
            bundleListingStatus);
  }

  /**
   * Description : To Test network bundles with Zone as provisional constraint Excludes with invalid selection and
   * NetworkOffering as provisional constraint Includes , will be filtered as per the Provisional constraint given on
   * catalog page
   * 
   * @author nageswarap
   * @throws Exception
   */
  @Test
  public void testNetworkBundleSubscriptionAsZoneExcludesInvalidSelectionNetworkOfferingIncludes() throws Exception {

    boolean bundleListingStatus;

    // Retreiving tenant, bundle and channel information required to execute the test case
    Tenant tenant = tenantService.getTenantByParam("id", "6", false);
    ProductBundle bundle = bundleservice.getProductBundleById(3L);
    Channel channel = channelService.getChannelById("5");

    // Assigning provisioning constraint test values for zone
    String zone = "123";
    String NetworkOffering = "NetworkOfferingwithOutVPC";

    // creating provisioning constraint map
    List<Map<String, Object>> provisioningConstraints = new ArrayList<Map<String, Object>>();
    createProvisioningConstraintsMap(provisioningConstraints, "zone", AssociationType.EXCLUDES, zone, "zone");
    createProvisioningConstraintsMap(provisioningConstraints, "NetworkOffering", AssociationType.INCLUDES,
        NetworkOffering, "NetworkOffering");

    // adding the provisioning constraint to the bundle and syncing the channel
    boolean status = updateBundleConstraints(bundle.getId(), channel.getId().toString(), provisioningConstraints);
    Assert.assertNotNull("syncChannel returned null", status);

    // syncing and scheduleactivating the channel
    syncAndScheduleActivateAChannel(channel);

    // creating the filter params map before subscribing to network bundle
    Map<String, Object> bundlefilterparams = bundleFilterParams(NetworkOffering, "zone120", "Network", 1L);

    // get the bundle listing status for the given filter params
    bundleListingStatus = getBundleListingStatusInCatalogPage(tenant, bundle, channel, bundlefilterparams);

    // verifying the bundle is listed or not for the filter params
    Assert
        .assertTrue(
            "Bundle is not listing with Zone  as provisional constraint exclude with invalid selection and NetworkOffering as provisional constraint Includes, which is included in the bundle ",
            bundleListingStatus);
  }

  /**
   * Description : To Test network bundles with Zone as provisional constraint Exclude with invalid selection and
   * NetworkOffering as provisional constraint Exclude with invalid selection , will be filtered as per the Provisional
   * constraint given on catalog page
   * 
   * @author nageswarap
   * @throws Exception
   */
  @Test
  public void testNetworkBundleSubscriptionAsZoneExcludesInvalidSelectionNetworkOfferingExcludesInvalidSelection()
      throws Exception {

    boolean bundleListingStatus;

    // Retreiving tenant, bundle and channel information required to execute the test case
    Tenant tenant = tenantService.getTenantByParam("id", "6", false);
    ProductBundle bundle = bundleservice.getProductBundleById(3L);
    Channel channel = channelService.getChannelById("5");

    // Assigning provisioning constraint test values for zone
    String zone = "123";
    String NetworkOffering = "NetworkOfferingwithOutVPC";

    // creating provisioning constraint map
    List<Map<String, Object>> provisioningConstraints = new ArrayList<Map<String, Object>>();
    createProvisioningConstraintsMap(provisioningConstraints, "zone", AssociationType.EXCLUDES, zone, "zone");
    createProvisioningConstraintsMap(provisioningConstraints, "NetworkOffering", AssociationType.EXCLUDES,
        NetworkOffering, "NetworkOffering");

    // adding the provisioning constraint to the bundle and syncing the channel
    boolean status = updateBundleConstraints(bundle.getId(), channel.getId().toString(), provisioningConstraints);
    Assert.assertNotNull("syncChannel returned null", status);

    // syncing and scheduleactivating the channel
    syncAndScheduleActivateAChannel(channel);

    // creating the filter params map before subscribing to network bundle
    Map<String, Object> bundlefilterparams = bundleFilterParams("NetworkOfferingwithOutVPC1", "zone1", "Network", 1L);

    // get the bundle listing status for the given filter params
    bundleListingStatus = getBundleListingStatusInCatalogPage(tenant, bundle, channel, bundlefilterparams);

    // verifying the bundle is listed or not for the filter params
    Assert
        .assertTrue(
            "Bundle is not listing with Zone  as provisional constraint Exclude with invalid selection and NetworkOffering as provisional constraint Exclude with invalid selection, which is included in the bundle ",
            bundleListingStatus);
  }

  /**
   * Description: function to add provision constraints
   * 
   * @author nageswarap
   */
  private void createProvisioningConstraintsMap(List<Map<String, Object>> provisioningConstraints,
      String componentName, AssociationType associationType, String componentValue, String displayName) {
    Map<String, Object> pcmap = new HashMap<String, Object>();
    pcmap.put("componentName", componentName);
    pcmap.put("associationType", associationType);
    pcmap.put("componentValue", componentValue);
    pcmap.put("displayName", displayName);
    provisioningConstraints.add(pcmap);

  }

  /**
   * Description : find the bundle is available on the catalog page of a tenant for the given filter params
   * 
   * @author nageswarap
   * @param tenant
   * @param bundle
   * @param channel
   * @param bundlefilterparams
   * @return
   * @throws Exception
   */
  private boolean getBundleListingStatusInCatalogPage(Tenant tenant, ProductBundle bundle, Channel channel,
      Map<String, Object> bundlefilterparams) throws Exception {

    Long bundleID = bundle.getId();

    // Checking as Master User
    asUser(tenant.getOwner());

    // Getting the list of bundles on catalog page
    request.setAttribute("effectiveTenant", tenant);
    String currencyCode = channelService.listCurrencies(channel.getParam()).get(0).getCurrencyCode();
    List<ProductBundleRevision> pbrlist = bundleController.listProductBundles(tenant, tenant.getParam(),
        bundlefilterparams.get("ServiceInstanceUUID").toString(), bundlefilterparams.get("serviceResourceType")
            .toString(), bundlefilterparams.get("filters").toString(), bundlefilterparams.get("context").toString(),
        true, "current", channel.getId().toString(), currencyCode, null, null, request);

    // Verify the Bundle is available
    boolean bundleListingStatus = false;
    for (ProductBundleRevision pbr : pbrlist) {
      if (pbr.getPbid().equals(bundleID)) {
        bundleListingStatus = true;
        break;
      }
    }

    // returning the boolean bundleListingStatus
    return bundleListingStatus;

  }

  /**
   * @author nageswarap Description : Sync and schedule activate the channel
   * @param channel
   * @return
   * @throws Exception
   */
  private boolean syncAndScheduleActivateAChannel(Channel channel) throws Exception {

    boolean ChannelActiveateStatus = false;

    // Sync channel with reference price book
    String syncStatus = channelController.syncChannel(channel.getId().toString(), map);

    // Schedule Activating the channel
    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
    String currentdate = sdf.format(new Date());
    String scheduleActivateStatus = channelController.changePlanDate(channel.getId().toString(), currentdate,
        dateFormat, map);

    if ("success".equalsIgnoreCase(syncStatus) && "success".equalsIgnoreCase(scheduleActivateStatus)) {
      ChannelActiveateStatus = true;
    }

    return ChannelActiveateStatus;

  }

  /**
   * Description : To Test storage bundles with zone as provisional constraint Included , will be filtered as per the
   * Provisional constraint given on catalog page
   * 
   * @author nageswarap
   * @throws Exception
   */
  @Test
  public void testStorageBundleSubscriptionAsZoneIncludes() throws Exception {

    boolean bundleListingStatus;
    String storageOffering = "mediumDiskOffering";

    // Retreiving tenant, bundle and channel information required to execute the test case
    Tenant tenant = tenantService.getTenantByParam("id", "6", false);
    ProductBundle bundle = bundleservice.getProductBundleById(18009L);
    Channel channel = channelService.getChannelById("5");

    // Assigning provisioning constraint test values for zone
    String zone = "123";

    // creating provisioning constraint map
    List<Map<String, Object>> provisioningConstraints = new ArrayList<Map<String, Object>>();
    createProvisioningConstraintsMap(provisioningConstraints, "zone", AssociationType.INCLUDES, zone, "zone");

    // adding the provisioning constraint to the bundle and syncing the channel
    boolean status = updateBundleConstraints(bundle.getId(), channel.getId().toString(), provisioningConstraints);
    Assert.assertNotNull("syncChannel returned null", status);

    // syncing and scheduleactivating the channel
    syncAndScheduleActivateAChannel(channel);

    // creating the filter params map before subscribing to storage bundle
    Map<String, Object> bundlefilterparams = StoragebundleFilterParams(storageOffering, zone, "Volume", 1L);

    // get the bundle listing status for the given filter params
    bundleListingStatus = getBundleListingStatusInCatalogPage(tenant, bundle, channel, bundlefilterparams);

    // verifying the bundle is listed or not for the filter params
    Assert.assertTrue("Bundle is not listing with a valid selection of zone, which is included in the bundle ",
        bundleListingStatus);
  }

  /**
   * Description : To Test storage bundles with zone as provisional constraint Included , will be filtered as per the
   * Provisional constraint given on catalog page
   * 
   * @author nageswarap
   * @throws Exception
   */
  @Test
  public void testStorageBundleSubscriptionAsZoneIncludesInvalidSelection() throws Exception {

    boolean bundleListingStatus;
    String storageOffering = "mediumDiskOffering";

    // Retreiving tenant, bundle and channel information required to execute the test case
    Tenant tenant = tenantService.getTenantByParam("id", "8", false);
    ProductBundle bundle = bundleservice.getProductBundleById(18009L);
    Channel channel = channelService.getChannelById("4");

    // Assigning provisioning constraint test values for zone
    String zone = "123";

    // creating provisioning constraint map
    List<Map<String, Object>> provisioningConstraints = new ArrayList<Map<String, Object>>();
    createProvisioningConstraintsMap(provisioningConstraints, "zone", AssociationType.INCLUDES, zone, "zone");

    // adding the provisioning constraint to the bundle and syncing the channel
    boolean status = updateBundleConstraints(bundle.getId(), channel.getId().toString(), provisioningConstraints);
    Assert.assertNotNull("syncChannel returned null", status);

    // syncing and scheduleactivating the channel
    syncAndScheduleActivateAChannel(channel);

    // creating the filter params map before subscribing to storage bundle
    Map<String, Object> bundlefilterparams = StoragebundleFilterParams(storageOffering, "120", "Volume", 1L);

    // get the bundle listing status for the given filter params
    bundleListingStatus = getBundleListingStatusInCatalogPage(tenant, bundle, channel, bundlefilterparams);

    // verifying the bundle is listed or not for the filter params
    Assert.assertFalse("Bundle is listing with a valid selection of zone, which is included in the bundle ",
        bundleListingStatus);
  }

  /**
   * Description : To Test storage bundles with zone as provisional constraint Excluded , will be filtered as per the
   * Provisional constraint given on catalog page
   * 
   * @author nageswarap
   * @throws Exception
   */
  @Test
  public void testStorageBundleSubscriptionAsZoneExcludes() throws Exception {

    boolean bundleListingStatus;
    String storageOffering = "mediumDiskOffering";

    // Retreiving tenant, bundle and channel information required to execute the test case
    Tenant tenant = tenantService.getTenantByParam("id", "6", false);
    ProductBundle bundle = bundleservice.getProductBundleById(18009L);
    Channel channel = channelService.getChannelById("5");

    // Assigning provisioning constraint test values for zone
    String zone = "123";

    // creating provisioning constraint map
    List<Map<String, Object>> provisioningConstraints = new ArrayList<Map<String, Object>>();
    createProvisioningConstraintsMap(provisioningConstraints, "zone", AssociationType.EXCLUDES, zone, "zone");

    // adding the provisioning constraint to the bundle and syncing the channel
    boolean status = updateBundleConstraints(bundle.getId(), channel.getId().toString(), provisioningConstraints);
    Assert.assertNotNull("syncChannel returned null", status);

    // syncing and scheduleactivating the channel
    syncAndScheduleActivateAChannel(channel);

    // creating the filter params map before subscribing to storage bundle
    Map<String, Object> bundlefilterparams = StoragebundleFilterParams(storageOffering, zone, "Volume", 1L);

    // get the bundle listing status for the given filter params
    bundleListingStatus = getBundleListingStatusInCatalogPage(tenant, bundle, channel, bundlefilterparams);

    // verifying the bundle is listed or not for the filter params
    Assert.assertFalse("Bundle is  listing with a valid selection of zone, which is included in the bundle ",
        bundleListingStatus);
  }

  /**
   * Description : To Test storage bundles with zone as provisional constraint Excluded , will be filtered as per the
   * Provisional constraint given on catalog page
   * 
   * @author nageswarap
   * @throws Exception
   */
  @Test
  public void testStorageBundleSubscriptionAsZoneExcludesInvalidSelection() throws Exception {

    boolean bundleListingStatus;
    String storageOffering = "mediumDiskOffering";

    // Retreiving tenant, bundle and channel information required to execute the test case
    Tenant tenant = tenantService.getTenantByParam("id", "6", false);
    ProductBundle bundle = bundleservice.getProductBundleById(18009L);
    Channel channel = channelService.getChannelById("5");

    // Assigning provisioning constraint test values for zone
    String zone = "123";

    // creating provisioning constraint map
    List<Map<String, Object>> provisioningConstraints = new ArrayList<Map<String, Object>>();
    createProvisioningConstraintsMap(provisioningConstraints, "zone", AssociationType.EXCLUDES, zone, "zone");

    // adding the provisioning constraint to the bundle and syncing the channel
    boolean status = updateBundleConstraints(bundle.getId(), channel.getId().toString(), provisioningConstraints);
    Assert.assertNotNull("syncChannel returned null", status);

    // syncing and scheduleactivating the channel
    syncAndScheduleActivateAChannel(channel);

    // creating the filter params map before subscribing to storage bundle
    Map<String, Object> bundlefilterparams = StoragebundleFilterParams(storageOffering, "120", "Volume", 1L);

    // get the bundle listing status for the given filter params
    bundleListingStatus = getBundleListingStatusInCatalogPage(tenant, bundle, channel, bundlefilterparams);

    // verifying the bundle is listed or not for the filter params
    Assert.assertTrue("Bundle is  not listing with a valid selection of zone, which is Excluded in the bundle ",
        bundleListingStatus);
  }

  /**
   * Description : To Test storage bundles with DiskOffering as provisional constraint Included , will be filtered as
   * per the Provisional constraint given on catalog page
   * 
   * @author nageswarap
   * @throws Exception
   */
  @Test
  public void testStorageBundleSubscriptionAsDiskOfferingIncludes() throws Exception {

    boolean bundleListingStatus;
    String storageOffering = "mediumDiskOffering";

    // Retreiving tenant, bundle and channel information required to execute the test case
    Tenant tenant = tenantService.getTenantByParam("id", "6", false);
    ProductBundle bundle = bundleservice.getProductBundleById(18009L);
    Channel channel = channelService.getChannelById("5");

    // Assigning provisioning constraint test values for zone
    String zone = "123";

    // creating provisioning constraint map
    List<Map<String, Object>> provisioningConstraints = new ArrayList<Map<String, Object>>();
    createProvisioningConstraintsMap(provisioningConstraints, "diskOfferingUuid", AssociationType.INCLUDES,
        storageOffering, "diskOfferingUuid");

    // adding the provisioning constraint to the bundle and syncing the channel
    boolean status = updateBundleConstraints(bundle.getId(), channel.getId().toString(), provisioningConstraints);
    Assert.assertNotNull("syncChannel returned null", status);

    // syncing and scheduleactivating the channel
    syncAndScheduleActivateAChannel(channel);

    // creating the filter params map before subscribing to storage bundle
    Map<String, Object> bundlefilterparams = StoragebundleFilterParams(storageOffering, zone, "Volume", 1L);

    // get the bundle listing status for the given filter params
    bundleListingStatus = getBundleListingStatusInCatalogPage(tenant, bundle, channel, bundlefilterparams);

    // verifying the bundle is listed or not for the filter params
    Assert.assertTrue("Bundle is not listing with a valid selection of DiskOffering, which is included in the bundle ",
        bundleListingStatus);
  }

  /**
   * Description : To Test storage bundles with diskOffering as provisional constraint Included , will be filtered as
   * per the Provisional constraint given on catalog page
   * 
   * @author nageswarap
   * @throws Exception
   */
  @Test
  public void testStorageBundleSubscriptionAsDiskOfferingIncludesInvalidSelection() throws Exception {

    boolean bundleListingStatus;
    String storageOffering = "mediumDiskOffering";

    // Retreiving tenant, bundle and channel information required to execute the test case
    Tenant tenant = tenantService.getTenantByParam("id", "6", false);
    ProductBundle bundle = bundleservice.getProductBundleById(18009L);
    Channel channel = channelService.getChannelById("5");

    // Assigning provisioning constraint test values for zone
    String zone = "123";

    // creating provisioning constraint map
    List<Map<String, Object>> provisioningConstraints = new ArrayList<Map<String, Object>>();
    createProvisioningConstraintsMap(provisioningConstraints, "diskOfferingUuid", AssociationType.INCLUDES,
        storageOffering, "diskOfferingUuid");

    // adding the provisioning constraint to the bundle and syncing the channel
    boolean status = updateBundleConstraints(bundle.getId(), channel.getId().toString(), provisioningConstraints);
    Assert.assertNotNull("syncChannel returned null", status);

    // syncing and scheduleactivating the channel
    syncAndScheduleActivateAChannel(channel);

    // creating the filter params map before subscribing to storage bundle
    Map<String, Object> bundlefilterparams = StoragebundleFilterParams("largeDiskOffering", zone, "Volume", 1L);

    // get the bundle listing status for the given filter params
    bundleListingStatus = getBundleListingStatusInCatalogPage(tenant, bundle, channel, bundlefilterparams);

    // verifying the bundle is listed or not for the filter params
    Assert.assertFalse("Bundle is listing with a invalid selection of diskOffering, which is included in the bundle ",
        bundleListingStatus);
  }

  /**
   * Description : To Test storage bundles with diskOffering as provisional constraint Excluded , will be filtered as
   * per the Provisional constraint given on catalog page
   * 
   * @author nageswarap
   * @throws Exception
   */
  @Test
  public void testStorageBundleSubscriptionAsdiskOfferingExcludes() throws Exception {

    boolean bundleListingStatus;
    String storageOffering = "mediumDiskOffering";

    // Retreiving tenant, bundle and channel information required to execute the test case
    Tenant tenant = tenantService.getTenantByParam("id", "6", false);
    ProductBundle bundle = bundleservice.getProductBundleById(18009L);
    Channel channel = channelService.getChannelById("5");

    // Assigning provisioning constraint test values for zone
    String zone = "123";

    // creating provisioning constraint map
    List<Map<String, Object>> provisioningConstraints = new ArrayList<Map<String, Object>>();
    createProvisioningConstraintsMap(provisioningConstraints, "diskOfferingUuid", AssociationType.EXCLUDES,
        storageOffering, "diskOfferingUuid");

    // adding the provisioning constraint to the bundle and syncing the channel
    boolean status = updateBundleConstraints(bundle.getId(), channel.getId().toString(), provisioningConstraints);
    Assert.assertNotNull("syncChannel returned null", status);

    // syncing and scheduleactivating the channel
    syncAndScheduleActivateAChannel(channel);

    // creating the filter params map before subscribing to storage bundle
    Map<String, Object> bundlefilterparams = StoragebundleFilterParams(storageOffering, zone, "Volume", 1L);

    // get the bundle listing status for the given filter params
    bundleListingStatus = getBundleListingStatusInCatalogPage(tenant, bundle, channel, bundlefilterparams);

    // verifying the bundle is listed or not for the filter params
    Assert.assertFalse("Bundle is  listing with a valid selection of diskOffering, which is Excluded in the bundle ",
        bundleListingStatus);
  }

  /**
   * Description : To Test storage bundles with diskOffering as provisional constraint Excluded , will be filtered as
   * per the Provisional constraint given on catalog page
   * 
   * @author nageswarap
   * @throws Exception
   */
  @Test
  public void testStorageBundleSubscriptionAsdiskOfferingExcludesInvalidSelection() throws Exception {

    boolean bundleListingStatus;
    String storageOffering = "mediumDiskOffering";

    // Retreiving tenant, bundle and channel information required to execute the test case
    Tenant tenant = tenantService.getTenantByParam("id", "6", false);
    ProductBundle bundle = bundleservice.getProductBundleById(18009L);
    Channel channel = channelService.getChannelById("5");

    // Assigning provisioning constraint test values for zone
    String zone = "123";

    // creating provisioning constraint map
    List<Map<String, Object>> provisioningConstraints = new ArrayList<Map<String, Object>>();
    createProvisioningConstraintsMap(provisioningConstraints, "diskOfferingUuid", AssociationType.EXCLUDES,
        storageOffering, "diskOfferingUuid");

    // adding the provisioning constraint to the bundle and syncing the channel
    boolean status = updateBundleConstraints(bundle.getId(), channel.getId().toString(), provisioningConstraints);
    Assert.assertNotNull("syncChannel returned null", status);

    // syncing and scheduleactivating the channel
    syncAndScheduleActivateAChannel(channel);

    // creating the filter params map before subscribing to storage bundle
    Map<String, Object> bundlefilterparams = StoragebundleFilterParams("LargeDiskOffering", zone, "Volume", 1L);

    // get the bundle listing status for the given filter params
    bundleListingStatus = getBundleListingStatusInCatalogPage(tenant, bundle, channel, bundlefilterparams);

    // verifying the bundle is listed or not for the filter params
    Assert.assertTrue(
        "Bundle is  not listing with a invalid selection of diskOffering, which is Excluded in the bundle ",
        bundleListingStatus);
  }

  /**
   * Description : To Test storage bundles with zone as provisional constraint Included and diskOffering as provisional
   * constraint Included , will be filtered as per the Provisional constraint given on catalog page
   * 
   * @author nageswarap
   * @throws Exception
   */
  @Test
  public void testStorageBundleSubscriptionAsZoneIncludesdiskOfferingIncludes() throws Exception {

    boolean bundleListingStatus;
    String storageOffering = "mediumDiskOffering";

    // Retreiving tenant, bundle and channel information required to execute the test case
    Tenant tenant = tenantService.getTenantByParam("id", "6", false);
    ProductBundle bundle = bundleservice.getProductBundleById(18009L);
    Channel channel = channelService.getChannelById("5");

    // Assigning provisioning constraint test values for zone
    String zone = "123";

    // creating provisioning constraint map
    List<Map<String, Object>> provisioningConstraints = new ArrayList<Map<String, Object>>();
    createProvisioningConstraintsMap(provisioningConstraints, "zone", AssociationType.INCLUDES, zone, "zone");
    createProvisioningConstraintsMap(provisioningConstraints, "diskOfferingUuid", AssociationType.INCLUDES,
        storageOffering, "diskOfferingUuid");

    // adding the provisioning constraint to the bundle and syncing the channel
    boolean status = updateBundleConstraints(bundle.getId(), channel.getId().toString(), provisioningConstraints);
    Assert.assertNotNull("syncChannel returned null", status);

    // syncing and scheduleactivating the channel
    syncAndScheduleActivateAChannel(channel);

    // creating the filter params map before subscribing to storage bundle
    Map<String, Object> bundlefilterparams = StoragebundleFilterParams(storageOffering, zone, "Volume", 1L);

    // get the bundle listing status for the given filter params
    bundleListingStatus = getBundleListingStatusInCatalogPage(tenant, bundle, channel, bundlefilterparams);

    // verifying the bundle is listed or not for the filter params
    Assert
        .assertTrue(
            "Bundle is  not listing with a valid selection of diskOffering and valid selection of Zone, which is Excluded in the bundle ",
            bundleListingStatus);
  }

  /**
   * Description : To Test storage bundles with zone as provisional constraint Included and diskOffering as provisional
   * constraint Excluded , will be filtered as per the Provisional constraint given on catalog page
   * 
   * @author nageswarap
   * @throws Exception
   */
  @Test
  public void testStorageBundleSubscriptionAsZoneIncludesdiskOfferingExcludes() throws Exception {

    boolean bundleListingStatus;
    String storageOffering = "mediumDiskOffering";

    // Retreiving tenant, bundle and channel information required to execute the test case
    Tenant tenant = tenantService.getTenantByParam("id", "6", false);
    ProductBundle bundle = bundleservice.getProductBundleById(18009L);
    Channel channel = channelService.getChannelById("5");

    // Assigning provisioning constraint test values for zone
    String zone = "123";

    // creating provisioning constraint map
    List<Map<String, Object>> provisioningConstraints = new ArrayList<Map<String, Object>>();
    createProvisioningConstraintsMap(provisioningConstraints, "zone", AssociationType.INCLUDES, zone, "zone");
    createProvisioningConstraintsMap(provisioningConstraints, "diskOfferingUuid", AssociationType.EXCLUDES,
        storageOffering, "diskOfferingUuid");

    // adding the provisioning constraint to the bundle and syncing the channel
    boolean status = updateBundleConstraints(bundle.getId(), channel.getId().toString(), provisioningConstraints);
    Assert.assertNotNull("syncChannel returned null", status);

    // syncing and scheduleactivating the channel
    syncAndScheduleActivateAChannel(channel);

    // creating the filter params map before subscribing to storage bundle
    Map<String, Object> bundlefilterparams = StoragebundleFilterParams(storageOffering, zone, "Volume", 1L);

    // get the bundle listing status for the given filter params
    bundleListingStatus = getBundleListingStatusInCatalogPage(tenant, bundle, channel, bundlefilterparams);

    // verifying the bundle is listed or not for the filter params
    Assert.assertFalse("Bundle is  listing with a valid selection of diskOffering and valid selection of Zone",
        bundleListingStatus);
  }

  /**
   * Description : To Test storage bundles with zone as provisional constraint Excludes and diskOffering as provisional
   * constraint Includes , will be filtered as per the Provisional constraint given on catalog page
   * 
   * @author nageswarap
   * @throws Exception
   */
  @Test
  public void testStorageBundleSubscriptionAsZoneExcludesdiskOfferingIncludes() throws Exception {

    boolean bundleListingStatus;
    String storageOffering = "mediumDiskOffering";

    // Retreiving tenant, bundle and channel information required to execute the test case
    Tenant tenant = tenantService.getTenantByParam("id", "6", false);
    ProductBundle bundle = bundleservice.getProductBundleById(18009L);
    Channel channel = channelService.getChannelById("5");

    // Assigning provisioning constraint test values for zone
    String zone = "123";

    // creating provisioning constraint map
    List<Map<String, Object>> provisioningConstraints = new ArrayList<Map<String, Object>>();
    createProvisioningConstraintsMap(provisioningConstraints, "zone", AssociationType.EXCLUDES, zone, "zone");
    createProvisioningConstraintsMap(provisioningConstraints, "diskOfferingUuid", AssociationType.INCLUDES,
        storageOffering, "diskOfferingUuid");

    // adding the provisioning constraint to the bundle and syncing the channel
    boolean status = updateBundleConstraints(bundle.getId(), channel.getId().toString(), provisioningConstraints);
    Assert.assertNotNull("syncChannel returned null", status);

    // syncing and scheduleactivating the channel
    syncAndScheduleActivateAChannel(channel);

    // creating the filter params map before subscribing to storage bundle
    Map<String, Object> bundlefilterparams = StoragebundleFilterParams(storageOffering, zone, "Volume", 1L);

    // get the bundle listing status for the given filter params
    bundleListingStatus = getBundleListingStatusInCatalogPage(tenant, bundle, channel, bundlefilterparams);

    // verifying the bundle is listed or not for the filter params
    Assert.assertFalse("Bundle is  listing with a valid selection of diskOffering and valid selection of Zone ",
        bundleListingStatus);
  }

  /**
   * Description : To Test storage bundles with zone as provisional constraint Excludes and diskOffering as provisional
   * constraint Excludes , will be filtered as per the Provisional constraint given on catalog page
   * 
   * @author nageswarap
   * @throws Exception
   */
  @Test
  public void testStorageBundleSubscriptionAsZoneExcludesdiskOfferingExcludes() throws Exception {

    boolean bundleListingStatus;
    String storageOffering = "mediumDiskOffering";

    // Retreiving tenant, bundle and channel information required to execute the test case
    Tenant tenant = tenantService.getTenantByParam("id", "6", false);
    ProductBundle bundle = bundleservice.getProductBundleById(18009L);
    Channel channel = channelService.getChannelById("5");

    // Assigning provisioning constraint test values for zone
    String zone = "123";

    // creating provisioning constraint map
    List<Map<String, Object>> provisioningConstraints = new ArrayList<Map<String, Object>>();
    createProvisioningConstraintsMap(provisioningConstraints, "zone", AssociationType.EXCLUDES, zone, "zone");
    createProvisioningConstraintsMap(provisioningConstraints, "diskOfferingUuid", AssociationType.EXCLUDES,
        storageOffering, "diskOfferingUuid");

    // adding the provisioning constraint to the bundle and syncing the channel
    boolean status = updateBundleConstraints(bundle.getId(), channel.getId().toString(), provisioningConstraints);
    Assert.assertNotNull("syncChannel returned null", status);

    // syncing and scheduleactivating the channel
    syncAndScheduleActivateAChannel(channel);

    // creating the filter params map before subscribing to storage bundle
    Map<String, Object> bundlefilterparams = StoragebundleFilterParams(storageOffering, zone, "Volume", 1L);

    // get the bundle listing status for the given filter params
    bundleListingStatus = getBundleListingStatusInCatalogPage(tenant, bundle, channel, bundlefilterparams);

    // verifying the bundle is listed or not for the filter params
    Assert.assertFalse("Bundle is  listing with a valid selection of diskOffering and valid selection of Zone ",
        bundleListingStatus);
  }

  private Map<String, Object> StoragebundleFilterParams(String diskOffering, String zone, String serviceResourceType,
      Long servivceInstanceId) {
    Map<String, Object> bundlefilterparams = new HashMap<String, Object>();
    bundlefilterparams.put("ServiceInstanceUUID", getServiceInstanceByID(servivceInstanceId).getUuid());
    bundlefilterparams.put("serviceResourceType", serviceResourceType);
    bundlefilterparams.put("filters", "zone=" + zone);
    bundlefilterparams.put("context", "diskOfferingUuid=" + diskOffering);
    return bundlefilterparams;
  }

  /**
   * Description: test to verify the Filter Components As Anonymous user from Browse Catalog page
   * 
   * @author nageswarap
   */
  @Test
  public void testGetFilterComponentsAsAnonymous() {
    try {

      Tenant tenant = tenantService.getTenantByParam("id", "1", false);
      ServiceInstance instance = serviceInstanceDAO.find(1L);

      request.setAttribute("isSurrogatedTenant", false);

      // Checking as Anonymous User
      asAnonymous();

      // getting the resource components from the Anonymous browse catalog
      List<FilterComponent> componentList = controller.getFilterComponents(tenant, tenant.getParam(),
          instance.getUuid(), "", true, request);
      for (FilterComponent comp : componentList) {
        Assert.assertEquals(comp.getName(), "zone");
        Assert.assertEquals(comp.getValue(), "122");
      }
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testGetResourceComponentsAsAnonymous() {
    try {
      Tenant tenant = tenantService.getTenantByParam("id", "1", false);
      ServiceInstance instance = serviceInstanceDAO.find(1L);

      // Checking as Anonymous User
      asAnonymous();

      // getting the resource components from the Anonymous browse catalog
      List<ResourceComponent> resourceCompList = controller.getResourceComponents(tenant, tenant.getParam(),
          instance.getUuid(), "VirtualMachine", "Template", null, true, "", null, request);
      for (ResourceComponent comp : resourceCompList) {
        Assert.assertEquals(comp.getName(), "Template");
        Assert.assertEquals(comp.getValue(), "1");
      }
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testResourceBundleSubscriptionAsSOIncludesAsAnonymous() throws Exception {

    Tenant tenant = tenantService.getTenantByParam("id", "1", false);

    ProductBundle bundle = bundleservice.getProductBundleById(2L);
    String bundleName = bundle.getName();
    Channel channel = channelService.getChannelById("1");

    // Checking as Anonymous User
    asAnonymous();

    // Getting the list of bundles on catalog page
    request.setAttribute("effectiveTenant", tenant);
    String currencyCode = channelService.listCurrencies(channel.getParam()).get(0).getCurrencyCode();
    List<ProductBundleRevision> pbrlist = bundleController.listProductBundles(tenant, tenant.getParam(),
        getServiceInstanceByID(1L).getUuid(), "VirtualMachine", null, null, true, "current",
        channel.getId().toString(), currencyCode, null, null, request);

    // Verify the Bundle is not available
    boolean bundleListingStatus = false;
    for (ProductBundleRevision pbr : pbrlist) {
      if (pbr.getProductBundle().getName().equalsIgnoreCase(bundleName)) {
        bundleListingStatus = true;
      }
    }

    // bundle should be listed
    Assert.assertTrue("Bundle is not listing for SO id, which is included in the bundle", bundleListingStatus);

  }
}
/* Copyright (C) 2011 Cloud.com, Inc. All rights reserved. */
package fragment.web;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.map.ObjectMapper;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import web.WebTestsBase;

import com.citrix.cpbm.access.proxy.CustomProxy;
import com.citrix.cpbm.platform.spi.CloudConnector;
import com.citrix.cpbm.platform.spi.DisplayAttribute;
import com.citrix.cpbm.platform.spi.DynamicResourceTypeMetadataRegistry;
import com.citrix.cpbm.platform.spi.FilterComponent;
import com.citrix.cpbm.platform.spi.ResourceComponent;
import com.citrix.cpbm.portal.fragment.controllers.ChannelController;
import com.citrix.cpbm.portal.fragment.controllers.ProductBundlesController;
import com.citrix.cpbm.portal.fragment.controllers.ProductsController;
import com.vmops.internal.service.SubscriptionService;
import com.vmops.model.Catalog;
import com.vmops.model.CatalogProductBundle;
import com.vmops.model.Channel;
import com.vmops.model.Channel.ChannelType;
import com.vmops.model.ChargeRecurrenceFrequency;
import com.vmops.model.CurrencyValue;
import com.vmops.model.Entitlement;
import com.vmops.model.Product;
import com.vmops.model.ProductBundle;
import com.vmops.model.ProductBundleRevision;
import com.vmops.model.ProvisioningConstraint;
import com.vmops.model.ProvisioningConstraint.AssociationType;
import com.vmops.model.RateCard;
import com.vmops.model.RateCardCharge;
import com.vmops.model.RateCardComponent;
import com.vmops.model.Revision;
import com.vmops.model.ServiceInstance;
import com.vmops.model.ServiceResourceType;
import com.vmops.model.ServiceResourceType.ResourceConstraint;
import com.vmops.model.ServiceResourceTypeGroup;
import com.vmops.model.ServiceResourceTypeGroupComponent;
import com.vmops.model.Subscription;
import com.vmops.model.Tenant;
import com.vmops.model.Tenant.State;
import com.vmops.model.User;
import com.vmops.persistence.ChargeRecurrenceFrequencyDAO;
import com.vmops.persistence.ProductBundleDAO;
import com.vmops.persistence.RateCardComponentDAO;
import com.vmops.persistence.ServiceInstanceDao;
import com.vmops.persistence.ServiceResourceTypeDAO;
import com.vmops.persistence.SubscriptionDAO;
import com.vmops.persistence.TenantDAO;
import com.vmops.service.ChannelService;
import com.vmops.service.ProductBundleService;
import com.vmops.service.ProductService;
import com.vmops.service.exceptions.AjaxFormValidationException;
import com.vmops.service.exceptions.UnlimitedEntitlementNotAllowedException;
import com.vmops.utils.DateUtils;
import com.vmops.web.controllers.menu.Page;
import com.vmops.web.forms.EntitlementForm;
import com.vmops.web.forms.ProductBundleForm;
import com.vmops.web.forms.ProductBundleLogoForm;
import com.vmops.web.forms.ProductForm;
import com.vmops.web.forms.RateCardChargesForm;
import com.vmops.web.forms.RateCardComponentChargesForm;
import com.vmops.web.forms.RateCardForm;
import common.MockCloudInstance;

@SuppressWarnings("unchecked")
public class AbstractProductBundlesControllerTest extends WebTestsBase {

  @Autowired
  private ProductBundlesController bundleController;

  @Autowired
  private ProductsController productsController;

  @Autowired
  private ChannelController channelController;

  @Autowired
  private ServiceResourceTypeDAO serviceResourceTypeDAO;

  @Autowired
  private ServiceInstanceDao serviceInstanceDAO;

  @Autowired
  private ProductBundleService bundleService;

  @Autowired
  private SubscriptionService subscriptionService;

  @Autowired
  private ChargeRecurrenceFrequencyDAO chargeRecurrenceFrequencyDAO;

  @Autowired
  private ChannelService channelService;

  @Autowired
  private ProductService productService;

  @Autowired
  private RateCardComponentDAO rateCardComponentDAO;

  @Autowired
  private TenantDAO tenantDAO;

  @Autowired
  private ProductBundleDAO bundleDAO;

  @Autowired
  private SubscriptionDAO subscriptionDAO;

  private ModelMap map;

  private MockHttpServletResponse response;

  private MockHttpServletRequest request;

  private final String defaultServiceInstanceUuid = "003fa8ee-fba3-467f-a517-fd806dae8a80";

  private final String defaultResourceTypeName = "VirtualMachine";

  private final com.citrix.cpbm.access.Subscription proxySubscription = (com.citrix.cpbm.access.Subscription) CustomProxy
      .newInstance(new Subscription());

  @Before
  public void init() throws Exception {
    map = new ModelMap();
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();

  }

  @Override
  public void prepareMock() {
    Map<String, String> paramMap = new HashMap<String, String>();
    paramMap.put("zone", "Basic");
    paramMap.put("hypervisor", "Xenserver");
    Set<DisplayAttribute> displayAttrSet = new HashSet<DisplayAttribute>();
    displayAttrSet.add(new DisplayAttribute("zone", "Basic", 1));
    displayAttrSet.add(new DisplayAttribute("hypervisor", "Xenserver", 2));
    ResourceComponent resourceComponent = new ResourceComponent("VirtualMachine", "Template", "1", paramMap,
        displayAttrSet);
    ArrayList<ResourceComponent> resourceComponentList = new ArrayList<ResourceComponent>();
    resourceComponentList.add(resourceComponent);

    Map<String, String> params = new HashMap<String, String>();
    params.put("zone", "Advance");
    params.put("hypervisor", "Hyper-V");
    Set<DisplayAttribute> displayAttr = new HashSet<DisplayAttribute>();
    displayAttr.add(new DisplayAttribute("zone", "Advance", 1));
    displayAttr.add(new DisplayAttribute("hypervisor", "Hyper-V", 2));

    FilterComponent filterComponentZone = new FilterComponent("Zone", "2", params, displayAttr);
    List<FilterComponent> lstFilterComponent = new ArrayList<FilterComponent>();
    lstFilterComponent.add(filterComponentZone);

    MockCloudInstance mock = this.getMockCloudInstance();
    CloudConnector connector = mock.getCloudConnector();
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
    DynamicResourceTypeMetadataRegistry metadataRegistry = (DynamicResourceTypeMetadataRegistry) mock
        .getMetadataRegistry();
    Map<String, String> discriminatorMap = new HashMap<String, String>();
    EasyMock.expect(metadataRegistry.getDiscriminatorValues(EasyMock.anyObject(String.class)))
        .andReturn(discriminatorMap).anyTimes();
    EasyMock.expect(connector.getServiceInstanceUUID()).andReturn("1111111").anyTimes();
    EasyMock
        .expect(
            metadataRegistry.getResourceComponentValues(EasyMock.anyObject(String.class),
                EasyMock.anyObject(String.class))).andReturn(resourceComponentList).anyTimes();
    EasyMock
        .expect(
            metadataRegistry.getFilterValues(EasyMock.anyObject(String.class), EasyMock.anyObject(String.class),
                EasyMock.anyObject(String.class))).andReturn(lstFilterComponent).anyTimes();
    EasyMock
        .expect(metadataRegistry.getResourceTypes(EasyMock.anyObject(String.class), EasyMock.anyObject(String.class)))
        .andReturn(resourceTypes).anyTimes();
    EasyMock.replay(connector, metadataRegistry);

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
      resourceType = serviceResourceTypeDAO.find(resourceTypeID);
    }
    List<RateCardCharge> rateCardChargeList = new ArrayList<RateCardCharge>();
    RateCardCharge rcc = new RateCardCharge(currencyValueService.locateBYCurrencyCode(currencyCode), null,
        currencyValue, "RateCharge", getRootUser(), getRootUser(), channelService.getCurrentRevision(null));
    rateCardChargeList.add(rcc);
    String chargeTypeName = chargeType;
    if (chargeType.equalsIgnoreCase("Invalid")) {
      chargeTypeName = "NONE";
    }
    RateCard rateCard = new RateCard("Rate", chargeRecurrenceFrequencyDAO.findByName(chargeTypeName), new Date(),
        getRootUser(), getRootUser());
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
    Assert.assertNotNull(obtainedBundle);
    Assert.assertEquals(bundle.getName(), obtainedBundle.getName());

    return obtainedBundle;
  }

  /*
   * Description: Test to create Service Bundles with None, Monthly, Quarterly and Annual charge frequency Author:
   * Vinayv
   */
  @Test
  public void testCreateServiceBundleForDifferentChargeFrequency() throws Exception {

    String[] chargeFrequency = {
        "NONE", "MONTHLY", "QUARTERLY", "ANNUAL"
    };
    for (int i = 0; i < chargeFrequency.length; i++) {

      String compAssociationJson = "[{association: INCLUDES, compName: ISO, compValues:[ {compName: DOS-ISO, compValueName: 10 } ] }]";
      int noOfdays = 3;
      Calendar createdAt = Calendar.getInstance();
      createdAt.add(Calendar.DATE, 0 - noOfdays);
      int beforeBundleCount = bundleService.getBundlesCount();
      ProductBundle obtainedBundle = testCreateProductBundle("1", "ServiceBundle", chargeFrequency[i],
          chargeFrequency[i] + "Service", "USD", BigDecimal.valueOf(100), createdAt.getTime(), compAssociationJson,
          ResourceConstraint.PER_USER, true);
      Assert.assertNotNull(obtainedBundle);
      Assert.assertEquals(chargeFrequency[i] + "Service", obtainedBundle.getName());
      Assert.assertEquals(null, obtainedBundle.getResourceType());
      Assert.assertEquals(ResourceConstraint.PER_USER, obtainedBundle.getBusinessConstraint());
      int afterBundleCount = bundleService.getBundlesCount();
      Assert.assertEquals(beforeBundleCount + 1, afterBundleCount);
    }
  }

  /*
   * Description: Test to create Compute Bundles with None, Monthly, Quarterly and Annual charge frequency Author:
   * Vinayv
   */
  @Test
  public void testCreateComputeBundleForDifferentChargeFrequency() throws Exception {

    ServiceResourceType resourceType = serviceResourceTypeDAO.find("1");
    String[] chargeFrequency = {
        "NONE", "MONTHLY", "QUARTERLY", "ANNUAL"
    };
    for (int i = 0; i < chargeFrequency.length; i++) {

      String compAssociationJson = "[{association: INCLUDES, compName: ISO, compValues:[ {compName: DOS-ISO, compValueName: 10 } ] }]";
      int noOfdays = 3;
      Calendar createdAt = Calendar.getInstance();
      createdAt.add(Calendar.DATE, 0 - noOfdays);
      int beforeBundleCount = bundleService.getBundlesCount();
      boolean trialEligible = false;
      if (i == 1) {
        trialEligible = true;
      }
      ProductBundle obtainedBundle = testCreateProductBundle("1", resourceType.getId().toString(), chargeFrequency[i],
          chargeFrequency[i] + "Compute", "USD", BigDecimal.valueOf(100), createdAt.getTime(), compAssociationJson,
          ResourceConstraint.PER_USER, trialEligible);
      Assert.assertNotNull(obtainedBundle);
      Assert.assertEquals(chargeFrequency[i] + "Compute", obtainedBundle.getName());
      Assert.assertEquals(resourceType, obtainedBundle.getResourceType());
      Assert.assertEquals(ResourceConstraint.PER_USER, obtainedBundle.getBusinessConstraint());
      int afterBundleCount = bundleService.getBundlesCount();
      Assert.assertEquals(beforeBundleCount + 1, afterBundleCount);
    }
  }

  /*
   * Description: Test to create Compute Bundles with Invalid charge frequency(other than None, Monthly, Quarterly and
   * Annual) Author: Vinayv
   */
  @Test(expected = NullPointerException.class)
  public void testCreateComputeBundleWithInvalidChargeFrequency() throws Exception {

    String compAssociationJson = "[{association: INCLUDES, compName: ISO, compValues:[ {compName: DOS-ISO, compValueName: 10 } ] }]";
    int noOfdays = 3;
    Calendar createdAt = Calendar.getInstance();
    createdAt.add(Calendar.DATE, 0 - noOfdays);
    testCreateProductBundle("1", "1", "Invalid", "InvalidCompute", "USD", BigDecimal.valueOf(100), createdAt.getTime(),
        compAssociationJson, ResourceConstraint.PER_USER, true);
  }

  /*
   * Description: Test to create Compute Bundles with Null charge frequency Author: Vinayv
   */
  @Test(expected = NullPointerException.class)
  public void testCreateComputeBundleWithNullChargeFrequency() throws Exception {

    ServiceInstance serviceInstance = serviceInstanceDAO.find("1");
    ServiceResourceType resourceType = serviceResourceTypeDAO.find("1");
    List<RateCardCharge> rateCardChargeList = new ArrayList<RateCardCharge>();
    RateCardCharge rcc = new RateCardCharge(currencyValueService.locateBYCurrencyCode("USD"), catalogDAO.find(1L),
        BigDecimal.TEN, "RateCharge", getRootUser(), getRootUser(), channelService.getCurrentRevision(null));
    rateCardChargeList.add(rcc);
    RateCard rateCard = new RateCard("Rate", chargeRecurrenceFrequencyDAO.findByName("NONE"), new Date(),
        getRootUser(), getRootUser());
    String compAssociationJson = "[{association: INCLUDES, compName: ISO, compValues:[ {compName: DOS-ISO, compValueName: 10 } ] }]";

    ProductBundle bundle = new ProductBundle("NullCompute", "NullCompute", "", new Date(), new Date(), getRootUser());
    bundle.setBusinessConstraint(ResourceConstraint.NONE);
    bundle.setCode("NullCompute");
    bundle.setPublish(true);
    bundle.setResourceType(resourceType);
    bundle.setServiceInstanceId(serviceInstance);
    bundle.setTrialEligibility(true);
    bundle.setRateCard(rateCard);

    ProductBundleForm form = new ProductBundleForm(bundle);
    form.setChargeType(null);
    form.setResourceType(resourceType.getId().toString());
    form.setServiceInstanceUUID(serviceInstance.getUuid());
    form.setBundleOneTimeCharges(rateCardChargeList);
    form.setBundleRecurringCharges(rateCardChargeList);
    form.setCompAssociationJson(compAssociationJson);

    BindingResult result = validate(form);
    ProductBundle obtainedBundle = bundleController.createProductBundle(form, result, map, response);
    Assert.assertNotNull(obtainedBundle);
    Assert.assertEquals(bundle.getName(), obtainedBundle.getName());
  }

  /*
   * Description: Test to create Compute Bundles with Bundle Name as Null Author: Vinayv
   */
  @Test
  public void testCreateComputeBundleWithNullName() throws Exception {

    try {
      String compAssociationJson = "[{association: INCLUDES, compName: ISO, compValues:[ {compName: DOS-ISO, compValueName: 10 } ] }]";
      testCreateProductBundle("1", "1", "MONTHLY", null, "USD", BigDecimal.valueOf(100), new Date(),
          compAssociationJson, ResourceConstraint.PER_USER, true);
    } catch (AjaxFormValidationException e) {
      Assert.assertEquals("Ajax Form Validation Error", e.getMessage());
    }
  }

  /**
   * @author vinayv
   * @desc Test to create Compute Bundles with Resource Constraint as Null
   */
  @Test
  public void testCreateBundleWithNullResourceConstraint() throws Exception {
    try {
      String compAssociationJson = "[{association: INCLUDES, compName: ISO, compValues:[ {compName: DOS-ISO, compValueName: 10 } ] }]";
      testCreateProductBundle("1", "1", "MONTHLY", "MonthlyBundle", "USD", BigDecimal.valueOf(100), new Date(),
          compAssociationJson, null, true);
      Assert.fail("Bundle creation passed even if resource constraints is set as NULL");
    } catch (AjaxFormValidationException e) {
      Assert.assertEquals("Ajax Form Validation Error", e.getMessage());
    }
  }

  /*
   * Description: Test to create Compute Bundles with Code As Null Author: Vinayv
   */
  @Test(expected = AjaxFormValidationException.class)
  public void testCreateComputeBundleWithNullCode() throws Exception {

    ServiceInstance serviceInstance = serviceInstanceDAO.find("1");
    ServiceResourceType resourceType = serviceResourceTypeDAO.find("1");
    List<RateCardCharge> rateCardChargeList = new ArrayList<RateCardCharge>();
    RateCardCharge rcc = new RateCardCharge(currencyValueService.locateBYCurrencyCode("USD"), catalogDAO.find(1L),
        BigDecimal.TEN, "RateCharge", getRootUser(), getRootUser(), channelService.getCurrentRevision(null));
    rateCardChargeList.add(rcc);
    RateCard rateCard = new RateCard("Rate", chargeRecurrenceFrequencyDAO.findByName("MONTHLY"), new Date(),
        getRootUser(), getRootUser());
    String compAssociationJson = "[{association: INCLUDES, compName: ISO, compValues:[ {compName: DOS-ISO, compValueName: 10 } ] }]";

    ProductBundle bundle = new ProductBundle("MonthlyCompute", "MonthlyCompute", "", new Date(), new Date(),
        getRootUser());
    bundle.setBusinessConstraint(ResourceConstraint.NONE);
    bundle.setCode(null);
    bundle.setPublish(true);
    bundle.setResourceType(resourceType);
    bundle.setServiceInstanceId(serviceInstance);
    bundle.setTrialEligibility(true);
    bundle.setRateCard(rateCard);

    ProductBundleForm form = new ProductBundleForm(bundle);
    form.setChargeType("MONTHLY");
    form.setResourceType(resourceType.getId().toString());
    form.setServiceInstanceUUID(serviceInstance.getUuid());
    form.setBundleOneTimeCharges(rateCardChargeList);
    form.setBundleRecurringCharges(rateCardChargeList);
    form.setCompAssociationJson(compAssociationJson);

    BindingResult result = validate(form);
    ProductBundle obtainedBundle = bundleController.createProductBundle(form, result, map, response);
    Assert.assertNotNull(obtainedBundle);
    Assert.assertEquals(bundle.getCode(), obtainedBundle.getCode());
  }

  @Test
  public void testCreateComputeBundleWithNullCodeNoFormValidation() throws Exception {

    ServiceInstance serviceInstance = serviceInstanceDAO.find("1");
    ServiceResourceType resourceType = serviceResourceTypeDAO.find("1");
    List<RateCardCharge> rateCardChargeList = new ArrayList<RateCardCharge>();
    RateCardCharge rcc = new RateCardCharge(currencyValueService.locateBYCurrencyCode("USD"), catalogDAO.find(1L),
        BigDecimal.TEN, "RateCharge", getRootUser(), getRootUser(), channelService.getCurrentRevision(null));
    rateCardChargeList.add(rcc);
    RateCard rateCard = new RateCard("Rate", chargeRecurrenceFrequencyDAO.findByName("MONTHLY"), new Date(),
        getRootUser(), getRootUser());
    String compAssociationJson = "[{association: INCLUDES, compName: ISO, compValues:[ {compName: DOS-ISO, compValueName: 10 } ] }]";

    ProductBundle bundle = new ProductBundle("MonthlyCompute", "MonthlyCompute", "", new Date(), new Date(),
        getRootUser());
    bundle.setBusinessConstraint(ResourceConstraint.NONE);
    bundle.setCode(null);
    bundle.setPublish(true);
    bundle.setResourceType(resourceType);
    bundle.setServiceInstanceId(serviceInstance);
    bundle.setTrialEligibility(true);
    bundle.setRateCard(rateCard);

    ProductBundleForm form = new ProductBundleForm(bundle);
    form.setChargeType("MONTHLY");
    form.setResourceType(resourceType.getId().toString());
    form.setServiceInstanceUUID(serviceInstance.getUuid());
    form.setBundleOneTimeCharges(rateCardChargeList);
    form.setBundleRecurringCharges(rateCardChargeList);
    form.setCompAssociationJson(compAssociationJson);
    BindingResult result = new BeanPropertyBindingResult(form, "validation");
    try {
      bundleController.createProductBundle(form, result, map, response);
    } catch (AjaxFormValidationException e) {
      Assert.assertEquals("Ajax Form Validation Error", e.getMessage());
    }
  }

  /*
   * Description: Test to create Compute Bundles with Special Characters in Code Author: Vinayv
   */
  @Test(expected = AjaxFormValidationException.class)
  public void testCreateComputeBundleWithSplCharsInCode() throws Exception {
    ServiceInstance serviceInstance = serviceInstanceDAO.find("1");
    ServiceResourceType resourceType = serviceResourceTypeDAO.find("1");
    List<RateCardCharge> rateCardChargeList = new ArrayList<RateCardCharge>();
    RateCardCharge rcc = new RateCardCharge(currencyValueService.locateBYCurrencyCode("USD"), catalogDAO.find(1L),
        BigDecimal.TEN, "RateCharge", getRootUser(), getRootUser(), channelService.getCurrentRevision(null));
    rateCardChargeList.add(rcc);
    RateCard rateCard = new RateCard("Rate", chargeRecurrenceFrequencyDAO.findByName("MONTHLY"), new Date(),
        getRootUser(), getRootUser());
    String compAssociationJson = "[{association: INCLUDES, compName: ISO, compValues:[ {compName: DOS-ISO, compValueName: 10 } ] }]";

    ProductBundle bundle = new ProductBundle("MonthlyCompute", "MonthlyCompute", "", new Date(), new Date(),
        getRootUser());
    bundle.setBusinessConstraint(ResourceConstraint.NONE);
    bundle.setCode("$@!#%^");
    bundle.setPublish(true);
    bundle.setResourceType(resourceType);
    bundle.setServiceInstanceId(serviceInstance);
    bundle.setTrialEligibility(true);
    bundle.setRateCard(rateCard);

    ProductBundleForm form = new ProductBundleForm(bundle);
    form.setChargeType("MONTHLY");
    form.setResourceType(resourceType.getId().toString());
    form.setServiceInstanceUUID(serviceInstance.getUuid());
    form.setBundleOneTimeCharges(rateCardChargeList);
    form.setBundleRecurringCharges(rateCardChargeList);
    form.setCompAssociationJson(compAssociationJson);

    BindingResult result = validate(form);
    ProductBundle obtainedBundle = bundleController.createProductBundle(form, result, map, response);
    Assert.assertNotNull(obtainedBundle);
    Assert.assertEquals(bundle.getName(), obtainedBundle.getName());
  }

  /*
   * Description: Test to create Compute Bundles with duplicate Bundle Code Author: Vinayv
   */
  @Test(expected = AjaxFormValidationException.class)
  public void testCreateComputeBundleWithDuplicateCode() throws Exception {

    ServiceInstance serviceInstance = serviceInstanceDAO.find("1");
    ServiceResourceType resourceType = serviceResourceTypeDAO.find("1");
    ProductBundle existingBundle = bundleService.locateProductBundleById("2");
    List<RateCardCharge> rateCardChargeList = new ArrayList<RateCardCharge>();
    RateCardCharge rcc = new RateCardCharge(currencyValueService.locateBYCurrencyCode("USD"), catalogDAO.find(1L),
        BigDecimal.TEN, "RateCharge", getRootUser(), getRootUser(), channelService.getCurrentRevision(null));
    rateCardChargeList.add(rcc);
    RateCard rateCard = new RateCard("Rate", chargeRecurrenceFrequencyDAO.findByName("MONTHLY"), new Date(),
        getRootUser(), getRootUser());
    String compAssociationJson = "[{association: INCLUDES, compName: ISO, compValues:[ {compName: DOS-ISO, compValueName: 10 } ] }]";

    ProductBundle bundle = new ProductBundle("MonthlyCompute", "MonthlyCompute", "", new Date(), new Date(),
        getRootUser());
    bundle.setBusinessConstraint(ResourceConstraint.NONE);
    bundle.setCode(existingBundle.getCode());
    bundle.setPublish(true);
    bundle.setResourceType(resourceType);
    bundle.setServiceInstanceId(serviceInstance);
    bundle.setTrialEligibility(true);
    bundle.setRateCard(rateCard);

    ProductBundleForm form = new ProductBundleForm(bundle);
    form.setChargeType("MONTHLY");
    form.setResourceType(resourceType.getId().toString());
    form.setServiceInstanceUUID(serviceInstance.getUuid());
    form.setBundleOneTimeCharges(rateCardChargeList);
    form.setBundleRecurringCharges(rateCardChargeList);
    form.setCompAssociationJson(compAssociationJson);

    BindingResult result = validate(form);
    bundleController.createProductBundle(form, result, map, response);
  }

  /*
   * Description: Test to Add Entitlement to a Bundle Author: Vinayv
   */
  @Test
  public void testAddEntitlement() throws Exception {

    int noOfdays = 3;
    Calendar createdAt = Calendar.getInstance();
    createdAt.add(Calendar.DATE, 0 - noOfdays);
    ProductBundle obtainedBundle = bundleService.locateProductBundleById("3");
    String result1 = bundleController.getFilteredEntitlements(obtainedBundle.getCode(), "planned",
        new Date().toString(), 1, 100, map, request);
    Assert.assertNotNull(result1);
    Assert.assertEquals("bundle.entitlement.list", result1);
    List<Entitlement> eList = (List<Entitlement>) map.get("entitlements");
    int beforeEntitlementSize = eList.size();
    Product product = productService.locateProductById("2");
    Entitlement entitlement = new Entitlement();
    entitlement.setCreatedAt(createdAt.getTime());
    entitlement.setCreatedBy(getRootUser());
    entitlement.setIncludedUnits(0);
    entitlement.setProduct(product);
    entitlement.setProductBundle(obtainedBundle);
    entitlement.setRevision(channelService.getFutureRevision(null));
    entitlement.setUpdatedBy(getRootUser());

    EntitlementForm entitlementForm = new EntitlementForm();
    entitlementForm.setProductId(product.getId().toString());
    entitlementForm.setUnlimitedUsage(false);
    entitlementForm.setEntitlement(entitlement);

    BindingResult result = validate(entitlementForm);
    String resultString = bundleController.createEntitlement(obtainedBundle.getCode(), entitlementForm, result,
        response, map);
    Assert.assertNotNull(resultString);
    Assert.assertEquals("success", resultString);
    Entitlement obtainedEntitlement = (Entitlement) map.get("entitlement");
    Assert.assertEquals(obtainedBundle, obtainedEntitlement.getProductBundle());

    String result2 = bundleController.getFilteredEntitlements(obtainedBundle.getCode(), "planned",
        new Date().toString(), 1, 100, map, request);
    Assert.assertNotNull(result2);
    Assert.assertEquals("bundle.entitlement.list", result2);
    List<Entitlement> eList1 = (List<Entitlement>) map.get("entitlements");
    int afterEntitlementSize = eList1.size();
    Assert.assertEquals(beforeEntitlementSize + 1, afterEntitlementSize);
    for (Entitlement e : eList1) {
      Assert.assertEquals(entitlement.getProduct(), e.getProduct());
      Assert.assertEquals(entitlement.getIncludedUnits(), e.getIncludedUnits());
    }
  }

  /*
   * Description: Test to Add Unlimited Entitlement to a Bundle Author: Vinayv
   */
  @Test
  public void testAddUnlimitedEntitlement() throws Exception {

    int noOfdays = 3;
    Calendar createdAt = Calendar.getInstance();
    createdAt.add(Calendar.DATE, 0 - noOfdays);
    ProductBundle obtainedBundle = bundleService.locateProductBundleById("3");
    String result1 = bundleController.getFilteredEntitlements(obtainedBundle.getCode(), "planned",
        new Date().toString(), 1, 100, map, request);
    Assert.assertNotNull(result1);
    Assert.assertEquals("bundle.entitlement.list", result1);
    List<Entitlement> eList = (List<Entitlement>) map.get("entitlements");
    int beforeEntitlementSize = eList.size();
    Product product = productService.locateProductById("11");
    Entitlement entitlement = new Entitlement();
    entitlement.setCreatedAt(createdAt.getTime());
    entitlement.setCreatedBy(getRootUser());
    entitlement.setIncludedUnits(-1);
    entitlement.setProduct(product);
    entitlement.setProductBundle(obtainedBundle);
    entitlement.setRevision(channelService.getFutureRevision(null));
    entitlement.setUpdatedBy(getRootUser());

    EntitlementForm entitlementForm = new EntitlementForm();
    entitlementForm.setProductId(product.getId().toString());
    entitlementForm.setUnlimitedUsage(true);
    entitlementForm.setEntitlement(entitlement);

    BindingResult result = validate(entitlementForm);
    String resultString = bundleController.createEntitlement(obtainedBundle.getCode(), entitlementForm, result,
        response, map);
    Assert.assertNotNull(resultString);
    Assert.assertEquals("success", resultString);
    Entitlement obtainedEntitlement = (Entitlement) map.get("entitlement");
    Assert.assertEquals(obtainedBundle, obtainedEntitlement.getProductBundle());

    String result2 = bundleController.getFilteredEntitlements(obtainedBundle.getCode(), "planned",
        new Date().toString(), 1, 100, map, request);
    Assert.assertNotNull(result2);
    Assert.assertEquals("bundle.entitlement.list", result2);
    List<Entitlement> eList1 = (List<Entitlement>) map.get("entitlements");
    int afterEntitlementSize = eList1.size();
    Assert.assertEquals(beforeEntitlementSize + 1, afterEntitlementSize);
    for (Entitlement e : eList1) {
      Assert.assertEquals(-1, e.getIncludedUnits());
    }
  }

  @Test(expected = UnlimitedEntitlementNotAllowedException.class)
  public void testAddUnlimitedEntitlementNegative() throws Exception {

    int noOfdays = 3;
    Calendar createdAt = Calendar.getInstance();
    createdAt.add(Calendar.DATE, 0 - noOfdays);
    ProductBundle obtainedBundle = bundleService.locateProductBundleById("3");
    String result1 = bundleController.getFilteredEntitlements(obtainedBundle.getCode(), "planned",
        new Date().toString(), 1, 100, map, request);
    Assert.assertNotNull(result1);
    Assert.assertEquals("bundle.entitlement.list", result1);
    Product product = productService.locateProductById("2");
    Entitlement entitlement = new Entitlement();
    entitlement.setCreatedAt(createdAt.getTime());
    entitlement.setCreatedBy(getRootUser());
    entitlement.setIncludedUnits(-1);
    entitlement.setProduct(product);
    entitlement.setProductBundle(obtainedBundle);
    entitlement.setRevision(channelService.getFutureRevision(null));
    entitlement.setUpdatedBy(getRootUser());

    EntitlementForm entitlementForm = new EntitlementForm();
    entitlementForm.setProductId(product.getId().toString());
    entitlementForm.setUnlimitedUsage(true);
    entitlementForm.setEntitlement(entitlement);

    BindingResult result = validate(entitlementForm);
    bundleController.createEntitlement(obtainedBundle.getCode(), entitlementForm, result, response, map);
  }

  /*
   * Description: Test to Update valid Entitlement to Unlimited Entitlement for a Bundle Author: Vinayv
   */
  @Test
  public void testUpdateValidToUnlimitedEntitlement() throws Exception {

    ProductBundle obtainedBundle = bundleService.locateProductBundleById("2");
    String result1 = bundleController.getFilteredEntitlements(obtainedBundle.getCode(), "planned",
        new Date().toString(), 1, 100, map, request);
    Assert.assertNotNull(result1);
    Assert.assertEquals("bundle.entitlement.list", result1);
    List<Entitlement> eList = (List<Entitlement>) map.get("entitlements");
    int beforeEntitlementSize = eList.size();
    Entitlement entitlement = null;
    for (Entitlement e : eList) {
      if (e.getIncludedUnits() != -1) {
        entitlement = e;
        break;
      }
    }
    Entitlement obtainedEntitlement = bundleController.saveEntitlement(entitlement.getId().toString(), -1, map);
    Assert.assertNotNull(obtainedEntitlement);
    Assert.assertEquals(-1, obtainedEntitlement.getIncludedUnits());
    String result2 = bundleController.getFilteredEntitlements(obtainedBundle.getCode(), "planned",
        new Date().toString(), 1, 100, map, request);
    Assert.assertNotNull(result2);
    Assert.assertEquals("bundle.entitlement.list", result2);
    List<Entitlement> eList1 = (List<Entitlement>) map.get("entitlements");
    int afterEntitlementSize = eList1.size();
    Assert.assertEquals(beforeEntitlementSize, afterEntitlementSize);
  }

  /*
   * Description: Test to Update Unlimited Entitlement to Valid Entitlement for a Bundle Author: Vinayv
   */
  @Test
  public void testUpdateUnlimitedToValidEntitlement() throws Exception {

    ProductBundle obtainedBundle = bundleService.locateProductBundleById("2");
    String result1 = bundleController.getFilteredEntitlements(obtainedBundle.getCode(), "planned",
        new Date().toString(), 1, 100, map, request);
    Assert.assertNotNull(result1);
    Assert.assertEquals("bundle.entitlement.list", result1);
    List<Entitlement> eList = (List<Entitlement>) map.get("entitlements");
    int beforeEntitlementSize = eList.size();
    Entitlement entitlement = null;
    for (Entitlement e : eList) {
      if (e.getIncludedUnits() == -1) {
        entitlement = e;
        break;
      }
    }
    Entitlement obtainedEntitlement = bundleController.saveEntitlement(entitlement.getId().toString(), 10, map);
    Assert.assertNotNull(obtainedEntitlement);
    Assert.assertEquals(10, obtainedEntitlement.getIncludedUnits());
    String result2 = bundleController.getFilteredEntitlements(obtainedBundle.getCode(), "planned",
        new Date().toString(), 1, 100, map, request);
    Assert.assertNotNull(result2);
    Assert.assertEquals("bundle.entitlement.list", result2);
    List<Entitlement> eList1 = (List<Entitlement>) map.get("entitlements");
    int afterEntitlementSize = eList1.size();
    Assert.assertEquals(beforeEntitlementSize, afterEntitlementSize);
  }

  /*
   * Description: Test to Delete a valid Entitlement from a Bundle Author: Vinayv
   */
  @Test
  public void testDeleteValidEntitlement() throws Exception {

    ProductBundle obtainedBundle = bundleService.locateProductBundleById("2");
    String result1 = bundleController.getFilteredEntitlements(obtainedBundle.getCode(), "planned",
        new Date().toString(), 1, 100, map, request);
    Assert.assertNotNull(result1);
    Assert.assertEquals("bundle.entitlement.list", result1);
    List<Entitlement> eList = (List<Entitlement>) map.get("entitlements");
    int beforeEntitlementSize = eList.size();
    Entitlement entitlement = null;
    for (Entitlement e : eList) {
      if (e.getIncludedUnits() != -1) {
        entitlement = e;
        break;
      }
    }
    Product obtainedProduct = bundleController.deleteEntitlement(entitlement.getId().toString(), obtainedBundle.getId()
        .toString(), map);
    Assert.assertNotNull(obtainedProduct);
    Assert.assertEquals(entitlement.getProduct(), obtainedProduct);
    String result2 = bundleController.getFilteredEntitlements(obtainedBundle.getCode(), "planned",
        new Date().toString(), 1, 100, map, request);
    Assert.assertNotNull(result2);
    Assert.assertEquals("bundle.entitlement.list", result2);
    List<Entitlement> eList1 = (List<Entitlement>) map.get("entitlements");
    int afterEntitlementSize = eList1.size();
    Assert.assertEquals(beforeEntitlementSize - 1, afterEntitlementSize);
  }

  /*
   * Description: Test to Delete Unlimited Entitlement from a Bundle Author: Vinayv
   */
  @Test
  public void testDeleteUnlimitedEntitlement() throws Exception {

    ProductBundle obtainedBundle = bundleService.locateProductBundleById("2");
    String result1 = bundleController.getFilteredEntitlements(obtainedBundle.getCode(), "planned",
        new Date().toString(), 1, 100, map, request);
    Assert.assertNotNull(result1);
    Assert.assertEquals("bundle.entitlement.list", result1);
    List<Entitlement> eList = (List<Entitlement>) map.get("entitlements");
    int beforeEntitlementSize = eList.size();
    Entitlement entitlement = null;
    for (Entitlement e : eList) {
      if (e.getIncludedUnits() == -1) {
        entitlement = e;
        break;
      }
    }
    Product obtainedProduct = bundleController.deleteEntitlement(entitlement.getId().toString(), obtainedBundle.getId()
        .toString(), map);
    Assert.assertNotNull(obtainedProduct);
    Assert.assertEquals(entitlement.getProduct(), obtainedProduct);
    String result2 = bundleController.getFilteredEntitlements(obtainedBundle.getCode(), "planned",
        new Date().toString(), 1, 100, map, request);
    Assert.assertNotNull(result2);
    Assert.assertEquals("bundle.entitlement.list", result2);
    List<Entitlement> eList1 = (List<Entitlement>) map.get("entitlements");
    int afterEntitlementSize = eList1.size();
    Assert.assertEquals(beforeEntitlementSize - 1, afterEntitlementSize);
  }

  /*
   * Description: Test to unpublish a bundle Author: Vinayv
   */
  @Test
  public void testUnpublishBundle() throws Exception {

    ServiceResourceType resourceType = serviceResourceTypeDAO.find("1");
    String compAssociationJson = "[{association: INCLUDES, compName: ISO, compValues:[ {compName: DOS-ISO, compValueName: 10 } ] }]";
    int noOfdays = 3;
    Calendar createdAt = Calendar.getInstance();
    createdAt.add(Calendar.DATE, 0 - noOfdays);
    int beforeBundleCount = bundleService.getBundlesCount();
    ProductBundle obtainedBundle = testCreateProductBundle("1", resourceType.getId().toString(), "MONTHLY",
        "MONTHLYCompute", "USD", BigDecimal.valueOf(100), createdAt.getTime(), compAssociationJson,
        ResourceConstraint.PER_USER, true);
    Assert.assertNotNull(obtainedBundle);
    Assert.assertEquals("MONTHLYCompute", obtainedBundle.getName());
    Assert.assertEquals(resourceType, obtainedBundle.getResourceType());
    Assert.assertEquals(ResourceConstraint.PER_USER, obtainedBundle.getBusinessConstraint());
    int afterBundleCount = bundleService.getBundlesCount();
    Assert.assertEquals(beforeBundleCount + 1, afterBundleCount);
    Assert.assertEquals(true, obtainedBundle.getPublish().booleanValue());
    String result = bundleController.publishBundle(obtainedBundle.getCode(), "false", map);
    Assert.assertNotNull(result);
    Assert.assertEquals("success", result);
    Assert.assertEquals(false, obtainedBundle.getPublish().booleanValue());
  }

  /*
   * Description: Test to Set Plan Date for a bundle Author: Vinayv
   */
  @Test
  public void testSetPlanDateForBundle() throws Exception {

    ServiceResourceType resourceType = serviceResourceTypeDAO.find("1");
    String compAssociationJson = "[{association: INCLUDES, compName: ISO, compValues:[ {compName: DOS-ISO, compValueName: 10 } ] }]";
    int noOfdays = 3;
    Calendar createdAt = Calendar.getInstance();
    createdAt.add(Calendar.DATE, 0 + noOfdays);
    int beforeBundleCount = bundleService.getBundlesCount();
    ProductBundle obtainedBundle = testCreateProductBundle("1", resourceType.getId().toString(), "MONTHLY",
        "MONTHLYCompute", "USD", BigDecimal.valueOf(100), createdAt.getTime(), compAssociationJson,
        ResourceConstraint.PER_USER, true);
    Assert.assertNotNull(obtainedBundle);
    Assert.assertEquals("MONTHLYCompute", obtainedBundle.getName());
    Assert.assertEquals(resourceType, obtainedBundle.getResourceType());
    Assert.assertEquals(ResourceConstraint.PER_USER, obtainedBundle.getBusinessConstraint());
    int afterBundleCount = bundleService.getBundlesCount();
    Assert.assertEquals(beforeBundleCount + 1, afterBundleCount);
    productService.setReferencePriceBookFutureRevisionDate(createdAt.getTime());
    List<Revision> allRPBRevisions = productService.getAllRevisions(null);
    Revision pbr = allRPBRevisions.get(1);
    Assert.assertEquals(DateUtils.truncate(createdAt.getTime()), pbr.getStartDate());
  }

  /*
   * Description: Test to create Compute Bundles with Null Resource Type Author: Vinayv
   */
  @Test(expected = NullPointerException.class)
  public void testCreateComputeBundleWithNullResourceType() throws Exception {

    String compAssociationJson = "[{association: INCLUDES, compName: ISO, compValues:[ {compName: DOS-ISO, compValueName: 10 } ] }]";
    int noOfdays = 3;
    Calendar createdAt = Calendar.getInstance();
    createdAt.add(Calendar.DATE, 0 - noOfdays);
    testCreateProductBundle("1", null, "MONTHLY", "MONTHLYCompute", "USD", BigDecimal.valueOf(100),
        createdAt.getTime(), compAssociationJson, ResourceConstraint.PER_USER, true);
  }

  /*
   * Description: Test to create Compute Bundles with Null Business Constraint Author: Vinayv
   */
  @Test
  public void testCreateComputeBundleWithNullBusinessConstraint() throws Exception {

    try {
      ServiceResourceType resourceType = serviceResourceTypeDAO.find("1");
      int noOfdays = 3;
      Calendar createdAt = Calendar.getInstance();
      createdAt.add(Calendar.DATE, 0 - noOfdays);
      testCreateProductBundle("1", resourceType.getId().toString(), "MONTHLY", "MONTHLYCompute", "USD",
          BigDecimal.valueOf(100), createdAt.getTime(), "", ResourceConstraint.PER_USER, true);
    } catch (Exception e) {
      Assert.assertEquals("A JSONArray text must start with '[' at character 0 of ", e.getMessage());
    }
  }

  /*
   * Description: Test to create a Bundle with Template Includes Author: Vinayv
   */
  @Test
  public void testCreateBundleWithTemplateIncludes() throws Exception {

    ServiceResourceType resourceType = serviceResourceTypeDAO.find("1");
    String compAssociationJson = "[{association: INCLUDES, compName: Template, compValues:[ {compName: DOS-VM, compValueName: 10 } ] }]";
    int noOfdays = 3;
    Calendar createdAt = Calendar.getInstance();
    createdAt.add(Calendar.DATE, 0 + noOfdays);
    int beforeBundleCount = bundleService.getBundlesCount();
    ProductBundle obtainedBundle = testCreateProductBundle("1", resourceType.getId().toString(), "MONTHLY",
        "MONTHLYCompute", "USD", BigDecimal.valueOf(100), createdAt.getTime(), compAssociationJson,
        ResourceConstraint.PER_USER, true);
    Assert.assertNotNull(obtainedBundle);
    Assert.assertEquals("MONTHLYCompute", obtainedBundle.getName());
    Assert.assertEquals(resourceType, obtainedBundle.getResourceType());
    Assert.assertEquals(ResourceConstraint.PER_USER, obtainedBundle.getBusinessConstraint());
    int afterBundleCount = bundleService.getBundlesCount();
    Assert.assertEquals(beforeBundleCount + 1, afterBundleCount);
  }

  /*
   * Description: Test to create a Bundle with Template Excludes Author: Vinayv
   */
  @Test
  public void testCreateBundleWithTemplateExcludes() throws Exception {

    ServiceResourceType resourceType = serviceResourceTypeDAO.find("1");
    String compAssociationJson = "[{association: EXCLUDES, compName: Template, compValues:[ {compName: DOS-VM, compValueName: 10 } ] }]";
    int noOfdays = 3;
    Calendar createdAt = Calendar.getInstance();
    createdAt.add(Calendar.DATE, 0 + noOfdays);
    int beforeBundleCount = bundleService.getBundlesCount();
    ProductBundle obtainedBundle = testCreateProductBundle("1", resourceType.getId().toString(), "MONTHLY",
        "MONTHLYCompute", "USD", BigDecimal.valueOf(100), createdAt.getTime(), compAssociationJson,
        ResourceConstraint.PER_USER, true);
    Assert.assertNotNull(obtainedBundle);
    Assert.assertEquals("MONTHLYCompute", obtainedBundle.getName());
    Assert.assertEquals(resourceType, obtainedBundle.getResourceType());
    Assert.assertEquals(ResourceConstraint.PER_USER, obtainedBundle.getBusinessConstraint());
    int afterBundleCount = bundleService.getBundlesCount();
    Assert.assertEquals(beforeBundleCount + 1, afterBundleCount);
  }

  /*
   * Description: Test to create a Bundle with Hypervisor Includes Author: Vinayv
   */
  @Test
  public void testCreateBundleWithHypervisorIncludes() throws Exception {

    ServiceResourceType resourceType = serviceResourceTypeDAO.find("1");
    String compAssociationJson = "[{association: INCLUDES, compName: Hypervisor, compValues:[ {compName: VMWare, compValueName: 10 } ] }]";
    int noOfdays = 3;
    Calendar createdAt = Calendar.getInstance();
    createdAt.add(Calendar.DATE, 0 + noOfdays);
    int beforeBundleCount = bundleService.getBundlesCount();
    ProductBundle obtainedBundle = testCreateProductBundle("1", resourceType.getId().toString(), "MONTHLY",
        "MONTHLYCompute", "USD", BigDecimal.valueOf(100), createdAt.getTime(), compAssociationJson,
        ResourceConstraint.PER_USER, true);
    Assert.assertNotNull(obtainedBundle);
    Assert.assertEquals("MONTHLYCompute", obtainedBundle.getName());
    Assert.assertEquals(resourceType, obtainedBundle.getResourceType());
    Assert.assertEquals(ResourceConstraint.PER_USER, obtainedBundle.getBusinessConstraint());
    int afterBundleCount = bundleService.getBundlesCount();
    Assert.assertEquals(beforeBundleCount + 1, afterBundleCount);
  }

  /*
   * Description: Test to create a Bundle with Hypervisor Excludes Author: Vinayv
   */
  @Test
  public void testCreateBundleWithHypervisorExcludes() throws Exception {

    ServiceResourceType resourceType = serviceResourceTypeDAO.find("1");
    String compAssociationJson = "[{association: EXCLUDES, compName: Hypervisor, compValues:[ {compName: VMWare, compValueName: 10 } ] }]";
    int noOfdays = 3;
    Calendar createdAt = Calendar.getInstance();
    createdAt.add(Calendar.DATE, 0 + noOfdays);
    int beforeBundleCount = bundleService.getBundlesCount();
    ProductBundle obtainedBundle = testCreateProductBundle("1", resourceType.getId().toString(), "MONTHLY",
        "MONTHLYCompute", "USD", BigDecimal.valueOf(100), createdAt.getTime(), compAssociationJson,
        ResourceConstraint.PER_USER, true);
    Assert.assertNotNull(obtainedBundle);
    Assert.assertEquals("MONTHLYCompute", obtainedBundle.getName());
    Assert.assertEquals(resourceType, obtainedBundle.getResourceType());
    Assert.assertEquals(ResourceConstraint.PER_USER, obtainedBundle.getBusinessConstraint());
    int afterBundleCount = bundleService.getBundlesCount();
    Assert.assertEquals(beforeBundleCount + 1, afterBundleCount);
  }

  /*
   * Description: Test to create a Bundle with ServiceOffering Includes Author: Vinayv
   */
  @Test
  public void testCreateBundleWithServiceOfferingIncludes() throws Exception {

    ServiceResourceType resourceType = serviceResourceTypeDAO.find("1");
    String compAssociationJson = "[{association: INCLUDES, compName: ServiceOffering, compValues:[ {compName: SO, compValueName: Small Instance } ] }]";
    int noOfdays = 3;
    Calendar createdAt = Calendar.getInstance();
    createdAt.add(Calendar.DATE, 0 + noOfdays);
    int beforeBundleCount = bundleService.getBundlesCount();
    ProductBundle obtainedBundle = testCreateProductBundle("1", resourceType.getId().toString(), "MONTHLY",
        "MONTHLYCompute", "USD", BigDecimal.valueOf(100), createdAt.getTime(), compAssociationJson,
        ResourceConstraint.PER_USER, true);
    Assert.assertNotNull(obtainedBundle);
    Assert.assertEquals("MONTHLYCompute", obtainedBundle.getName());
    Assert.assertEquals(resourceType, obtainedBundle.getResourceType());
    Assert.assertEquals(ResourceConstraint.PER_USER, obtainedBundle.getBusinessConstraint());
    int afterBundleCount = bundleService.getBundlesCount();
    Assert.assertEquals(beforeBundleCount + 1, afterBundleCount);
  }

  /*
   * Description: Test to create a Bundle with ServiceOffering Excludes Author: Vinayv
   */
  @Test
  public void testCreateBundleWithServiceOfferingExcludes() throws Exception {

    ServiceResourceType resourceType = serviceResourceTypeDAO.find("1");
    String compAssociationJson = "[{association: EXCLUDES, compName: ServiceOffering, compValues:[ {compName: SO, compValueName: Small Instance } ] }]";
    int noOfdays = 3;
    Calendar createdAt = Calendar.getInstance();
    createdAt.add(Calendar.DATE, 0 + noOfdays);
    int beforeBundleCount = bundleService.getBundlesCount();
    ProductBundle obtainedBundle = testCreateProductBundle("1", resourceType.getId().toString(), "MONTHLY",
        "MONTHLYCompute", "USD", BigDecimal.valueOf(100), createdAt.getTime(), compAssociationJson,
        ResourceConstraint.PER_USER, true);
    Assert.assertNotNull(obtainedBundle);
    Assert.assertEquals("MONTHLYCompute", obtainedBundle.getName());
    Assert.assertEquals(resourceType, obtainedBundle.getResourceType());
    Assert.assertEquals(ResourceConstraint.PER_USER, obtainedBundle.getBusinessConstraint());
    int afterBundleCount = bundleService.getBundlesCount();
    Assert.assertEquals(beforeBundleCount + 1, afterBundleCount);
  }

  /*
   * Description: Test to create a Bundle with NetworkOffering Includes Author: Vinayv
   */
  @Test
  public void testCreateBundleWithNewtworkOfferingIncludes() throws Exception {

    ServiceResourceType resourceType = serviceResourceTypeDAO.find("2");
    String compAssociationJson = "[{association: INCLUDES, compName: NetworkOffering, compValues:[ {compName: NetworkOffering, compValueName: DefaultSharedNetworkOffering } ] }]";
    int noOfdays = 3;
    Calendar createdAt = Calendar.getInstance();
    createdAt.add(Calendar.DATE, 0 + noOfdays);
    int beforeBundleCount = bundleService.getBundlesCount();
    ProductBundle obtainedBundle = testCreateProductBundle("1", resourceType.getId().toString(), "MONTHLY",
        "MONTHLYCompute", "USD", BigDecimal.valueOf(100), createdAt.getTime(), compAssociationJson,
        ResourceConstraint.PER_USER, true);
    Assert.assertNotNull(obtainedBundle);
    Assert.assertEquals("MONTHLYCompute", obtainedBundle.getName());
    Assert.assertEquals(resourceType, obtainedBundle.getResourceType());
    Assert.assertEquals(ResourceConstraint.PER_USER, obtainedBundle.getBusinessConstraint());
    int afterBundleCount = bundleService.getBundlesCount();
    Assert.assertEquals(beforeBundleCount + 1, afterBundleCount);
  }

  /*
   * Description: Test to create a Bundle with NetworkOffering Excludes Author: Vinayv
   */
  @Test
  public void testCreateBundleWithNetworkOfferingExcludes() throws Exception {

    ServiceResourceType resourceType = serviceResourceTypeDAO.find("2");
    String compAssociationJson = "[{association: EXCLUDES, compName: NetworkOffering, compValues:[ {compName: NetworkOffering, compValueName: DefaultSharedNetworkOffering } ] }]";
    int noOfdays = 3;
    Calendar createdAt = Calendar.getInstance();
    createdAt.add(Calendar.DATE, 0 + noOfdays);
    int beforeBundleCount = bundleService.getBundlesCount();
    ProductBundle obtainedBundle = testCreateProductBundle("1", resourceType.getId().toString(), "MONTHLY",
        "MONTHLYCompute", "USD", BigDecimal.valueOf(100), createdAt.getTime(), compAssociationJson,
        ResourceConstraint.PER_USER, true);
    Assert.assertNotNull(obtainedBundle);
    Assert.assertEquals("MONTHLYCompute", obtainedBundle.getName());
    Assert.assertEquals(resourceType, obtainedBundle.getResourceType());
    Assert.assertEquals(ResourceConstraint.PER_USER, obtainedBundle.getBusinessConstraint());
    int afterBundleCount = bundleService.getBundlesCount();
    Assert.assertEquals(beforeBundleCount + 1, afterBundleCount);
  }

  /*
   * Description: Test to create Compute Bundles with None, Monthly, Quarterly and Annual charge frequency As Product
   * Manager Author: Vinayv
   */
  @Test
  public void testCreateComputeBundlesAsProductManager() throws Exception {

    User user = userDAO.find("3");
    user.setProfile(profileDAO.find("7"));
    userDAO.save(user);
    asUser(user);
    ServiceResourceType resourceType = serviceResourceTypeDAO.find("1");
    String[] chargeFrequency = {
        "NONE", "MONTHLY", "QUARTERLY", "ANNUAL"
    };
    for (int i = 0; i < chargeFrequency.length; i++) {

      String compAssociationJson = "[{association: INCLUDES, compName: ISO, compValues:[ {compName: DOS-ISO, compValueName: 10 } ] }, {association: EXCLUDES, compName: SO, compValues:[ {compName: SO, compValueName: SmallInstance } ] }]";
      int noOfdays = 3;
      Calendar createdAt = Calendar.getInstance();
      createdAt.add(Calendar.DATE, 0 - noOfdays);
      int beforeBundleCount = bundleService.getBundlesCount();
      boolean trialEligible = false;
      if (i == 1) {
        trialEligible = true;
      }
      ProductBundle obtainedBundle = testCreateProductBundle("1", resourceType.getId().toString(), chargeFrequency[i],
          chargeFrequency[i] + "Compute", "USD", BigDecimal.valueOf(100), createdAt.getTime(), compAssociationJson,
          ResourceConstraint.PER_USER, trialEligible);
      Assert.assertNotNull(obtainedBundle);
      Assert.assertEquals(chargeFrequency[i] + "Compute", obtainedBundle.getName());
      Assert.assertEquals(resourceType, obtainedBundle.getResourceType());
      Assert.assertEquals(ResourceConstraint.PER_USER, obtainedBundle.getBusinessConstraint());
      int afterBundleCount = bundleService.getBundlesCount();
      Assert.assertEquals(beforeBundleCount + 1, afterBundleCount);
    }
  }

  /*
   * Description: Test to create a Bundle with Template Includes As Product Manager Author: Vinayv
   */
  @Test
  public void testCreateBundleWithTemplateIncludesAsProductManager() throws Exception {

    User user = userDAO.find("3");
    user.setProfile(profileDAO.find("7"));
    userDAO.save(user);
    asUser(user);
    ServiceResourceType resourceType = serviceResourceTypeDAO.find("1");
    String compAssociationJson = "[{association: INCLUDES, compName: Template, compValues:[ {compName: Template, compValueName: DOS-VM } ] }]";
    int noOfdays = 3;
    Calendar createdAt = Calendar.getInstance();
    createdAt.add(Calendar.DATE, 0 + noOfdays);
    int beforeBundleCount = bundleService.getBundlesCount();
    ProductBundle obtainedBundle = testCreateProductBundle("1", resourceType.getId().toString(), "MONTHLY",
        "MONTHLYCompute", "USD", BigDecimal.valueOf(100), createdAt.getTime(), compAssociationJson,
        ResourceConstraint.PER_USER, true);
    Assert.assertNotNull(obtainedBundle);
    Assert.assertEquals("MONTHLYCompute", obtainedBundle.getName());
    Assert.assertEquals(resourceType, obtainedBundle.getResourceType());
    Assert.assertEquals(ResourceConstraint.PER_USER, obtainedBundle.getBusinessConstraint());
    int afterBundleCount = bundleService.getBundlesCount();
    Assert.assertEquals(beforeBundleCount + 1, afterBundleCount);
  }

  /*
   * Description: Test to create a Bundle with Template Excludes As Product Manager Author: Vinayv
   */
  @Test
  public void testCreateBundleWithTemplateExcludesAsProductManager() throws Exception {

    User user = userDAO.find("3");
    user.setProfile(profileDAO.find("7"));
    userDAO.save(user);
    asUser(user);
    ServiceResourceType resourceType = serviceResourceTypeDAO.find("1");
    String compAssociationJson = "[{association: EXCLUDES, compName: Template, compValues:[ {compName: Template, compValueName: DOS-VM } ] }]";
    int noOfdays = 3;
    Calendar createdAt = Calendar.getInstance();
    createdAt.add(Calendar.DATE, 0 + noOfdays);
    int beforeBundleCount = bundleService.getBundlesCount();
    ProductBundle obtainedBundle = testCreateProductBundle("1", resourceType.getId().toString(), "MONTHLY",
        "MONTHLYCompute", "USD", BigDecimal.valueOf(100), createdAt.getTime(), compAssociationJson,
        ResourceConstraint.PER_USER, true);
    Assert.assertNotNull(obtainedBundle);
    Assert.assertEquals("MONTHLYCompute", obtainedBundle.getName());
    Assert.assertEquals(resourceType, obtainedBundle.getResourceType());
    Assert.assertEquals(ResourceConstraint.PER_USER, obtainedBundle.getBusinessConstraint());
    int afterBundleCount = bundleService.getBundlesCount();
    Assert.assertEquals(beforeBundleCount + 1, afterBundleCount);
  }

  /*
   * Description: Test to create a Bundle with Hypervisor Includes As Product Manager Author: Vinayv
   */
  @Test
  public void testCreateBundleWithHypervisorIncludesAsProductManager() throws Exception {

    User user = userDAO.find("3");
    user.setProfile(profileDAO.find("7"));
    userDAO.save(user);
    asUser(user);
    ServiceResourceType resourceType = serviceResourceTypeDAO.find("1");
    String compAssociationJson = "[{association: INCLUDES, compName: Hypervisor, compValues:[ {compName: Hypervisor, compValueName: VMWare } ] }]";
    int noOfdays = 3;
    Calendar createdAt = Calendar.getInstance();
    createdAt.add(Calendar.DATE, 0 + noOfdays);
    int beforeBundleCount = bundleService.getBundlesCount();
    ProductBundle obtainedBundle = testCreateProductBundle("1", resourceType.getId().toString(), "MONTHLY",
        "MONTHLYCompute", "USD", BigDecimal.valueOf(100), createdAt.getTime(), compAssociationJson,
        ResourceConstraint.PER_USER, true);
    Assert.assertNotNull(obtainedBundle);
    Assert.assertEquals("MONTHLYCompute", obtainedBundle.getName());
    Assert.assertEquals(resourceType, obtainedBundle.getResourceType());
    Assert.assertEquals(ResourceConstraint.PER_USER, obtainedBundle.getBusinessConstraint());
    int afterBundleCount = bundleService.getBundlesCount();
    Assert.assertEquals(beforeBundleCount + 1, afterBundleCount);
  }

  /*
   * Description: Test to create a Bundle with Hypervisor Excludes As Product Manager Author: Vinayv
   */
  @Test
  public void testCreateBundleWithHypervisorExcludesAsProductManager() throws Exception {

    User user = userDAO.find("3");
    user.setProfile(profileDAO.find("7"));
    userDAO.save(user);
    asUser(user);
    ServiceResourceType resourceType = serviceResourceTypeDAO.find("1");
    String compAssociationJson = "[{association: EXCLUDES, compName: Hypervisor, compValues:[ {compName: Hypervisor, compValueName: VMWare } ] }]";
    int noOfdays = 3;
    Calendar createdAt = Calendar.getInstance();
    createdAt.add(Calendar.DATE, 0 + noOfdays);
    int beforeBundleCount = bundleService.getBundlesCount();
    ProductBundle obtainedBundle = testCreateProductBundle("1", resourceType.getId().toString(), "MONTHLY",
        "MONTHLYCompute", "USD", BigDecimal.valueOf(100), createdAt.getTime(), compAssociationJson,
        ResourceConstraint.PER_USER, true);
    Assert.assertNotNull(obtainedBundle);
    Assert.assertEquals("MONTHLYCompute", obtainedBundle.getName());
    Assert.assertEquals(resourceType, obtainedBundle.getResourceType());
    Assert.assertEquals(ResourceConstraint.PER_USER, obtainedBundle.getBusinessConstraint());
    int afterBundleCount = bundleService.getBundlesCount();
    Assert.assertEquals(beforeBundleCount + 1, afterBundleCount);
  }

  /*
   * Description: Test to create a Bundle with ServiceOffering Includes As Product Manager Author: Vinayv
   */
  @Test
  public void testCreateBundleWithServiceOfferingIncludesAsProductManager() throws Exception {

    User user = userDAO.find("3");
    user.setProfile(profileDAO.find("7"));
    userDAO.save(user);
    asUser(user);
    ServiceResourceType resourceType = serviceResourceTypeDAO.find("1");
    String compAssociationJson = "[{association: INCLUDES, compName: ServiceOffering, compValues:[ {compName: SO, compValueName: Small Instance } ] }]";
    int noOfdays = 3;
    Calendar createdAt = Calendar.getInstance();
    createdAt.add(Calendar.DATE, 0 + noOfdays);
    int beforeBundleCount = bundleService.getBundlesCount();
    ProductBundle obtainedBundle = testCreateProductBundle("1", resourceType.getId().toString(), "MONTHLY",
        "MONTHLYCompute", "USD", BigDecimal.valueOf(100), createdAt.getTime(), compAssociationJson,
        ResourceConstraint.PER_USER, true);
    Assert.assertNotNull(obtainedBundle);
    Assert.assertEquals("MONTHLYCompute", obtainedBundle.getName());
    Assert.assertEquals(resourceType, obtainedBundle.getResourceType());
    Assert.assertEquals(ResourceConstraint.PER_USER, obtainedBundle.getBusinessConstraint());
    int afterBundleCount = bundleService.getBundlesCount();
    Assert.assertEquals(beforeBundleCount + 1, afterBundleCount);
  }

  /*
   * Description: Test to create a Bundle with ServiceOffering Excludes As Product Manager Author: Vinayv
   */
  @Test
  public void testCreateBundleWithServiceOfferingExcludesAsProductManager() throws Exception {

    User user = userDAO.find("3");
    user.setProfile(profileDAO.find("7"));
    userDAO.save(user);
    asUser(user);
    ServiceResourceType resourceType = serviceResourceTypeDAO.find("1");
    String compAssociationJson = "[{association: EXCLUDES, compName: ServiceOffering, compValues:[ {compName: SO, compValueName: Small Instance } ] }]";
    int noOfdays = 3;
    Calendar createdAt = Calendar.getInstance();
    createdAt.add(Calendar.DATE, 0 + noOfdays);
    int beforeBundleCount = bundleService.getBundlesCount();
    ProductBundle obtainedBundle = testCreateProductBundle("1", resourceType.getId().toString(), "MONTHLY",
        "MONTHLYCompute", "USD", BigDecimal.valueOf(100), createdAt.getTime(), compAssociationJson,
        ResourceConstraint.PER_USER, true);
    Assert.assertNotNull(obtainedBundle);
    Assert.assertEquals("MONTHLYCompute", obtainedBundle.getName());
    Assert.assertEquals(resourceType, obtainedBundle.getResourceType());
    Assert.assertEquals(ResourceConstraint.PER_USER, obtainedBundle.getBusinessConstraint());
    int afterBundleCount = bundleService.getBundlesCount();
    Assert.assertEquals(beforeBundleCount + 1, afterBundleCount);
  }

  /*
   * Description: Test to create a Bundle with NetworkOffering Includes As Product Manager Author: Vinayv
   */
  @Test
  public void testCreateBundleWithNewtworkOfferingIncludesAsProductManager() throws Exception {

    User user = userDAO.find("3");
    user.setProfile(profileDAO.find("7"));
    userDAO.save(user);
    asUser(user);
    ServiceResourceType resourceType = serviceResourceTypeDAO.find("2");
    String compAssociationJson = "[{association: INCLUDES, compName: NetworkOffering, compValues:[ {compName: NetworkOffering, compValueName: DefaultSharedNetworkOffering } ] }]";
    int noOfdays = 3;
    Calendar createdAt = Calendar.getInstance();
    createdAt.add(Calendar.DATE, 0 + noOfdays);
    int beforeBundleCount = bundleService.getBundlesCount();
    ProductBundle obtainedBundle = testCreateProductBundle("1", resourceType.getId().toString(), "MONTHLY",
        "MONTHLYCompute", "USD", BigDecimal.valueOf(100), createdAt.getTime(), compAssociationJson,
        ResourceConstraint.PER_USER, true);
    Assert.assertNotNull(obtainedBundle);
    Assert.assertEquals("MONTHLYCompute", obtainedBundle.getName());
    Assert.assertEquals(resourceType, obtainedBundle.getResourceType());
    Assert.assertEquals(ResourceConstraint.PER_USER, obtainedBundle.getBusinessConstraint());
    int afterBundleCount = bundleService.getBundlesCount();
    Assert.assertEquals(beforeBundleCount + 1, afterBundleCount);
  }

  /*
   * Description: Test to create a Bundle with NetworkOffering Excludes As Product Manager Author: Vinayv
   */
  @Test
  public void testCreateBundleWithNetworkOfferingExcludesAsProductManager() throws Exception {

    User user = userDAO.find("3");
    user.setProfile(profileDAO.find("7"));
    userDAO.save(user);
    asUser(user);
    ServiceResourceType resourceType = serviceResourceTypeDAO.find("2");
    String compAssociationJson = "[{association: EXCLUDES, compName: NetworkOffering, compValues:[ {compName: NetworkOffering, compValueName: DefaultSharedNetworkOffering } ] }]";
    int noOfdays = 3;
    Calendar createdAt = Calendar.getInstance();
    createdAt.add(Calendar.DATE, 0 + noOfdays);
    int beforeBundleCount = bundleService.getBundlesCount();
    ProductBundle obtainedBundle = testCreateProductBundle("1", resourceType.getId().toString(), "MONTHLY",
        "MONTHLYCompute", "USD", BigDecimal.valueOf(100), createdAt.getTime(), compAssociationJson,
        ResourceConstraint.PER_USER, true);
    Assert.assertNotNull(obtainedBundle);
    Assert.assertEquals("MONTHLYCompute", obtainedBundle.getName());
    Assert.assertEquals(resourceType, obtainedBundle.getResourceType());
    Assert.assertEquals(ResourceConstraint.PER_USER, obtainedBundle.getBusinessConstraint());
    int afterBundleCount = bundleService.getBundlesCount();
    Assert.assertEquals(beforeBundleCount + 1, afterBundleCount);
  }

  /*
   * Description: Test to Set and View Planned Charges for a Bundle Author: Vinayv
   */
  @Test
  public void testSetAndViewPlannedCharges() throws Exception {

    int noOfdays = 3;
    Calendar createdAt = Calendar.getInstance();
    createdAt.add(Calendar.DATE, 0 + noOfdays);
    ProductBundle bundle = bundleService.locateProductBundleById("3");

    List<RateCardComponentChargesForm> nonRecurringRateCardChargesFormList = new ArrayList<RateCardComponentChargesForm>();
    RateCardComponent rcc = rateCardComponentDAO.find("18");
    List<RateCardCharge> charges = bundleService.getCatalogProductBundlePlannedCharges(null, bundle);
    RateCardComponentChargesForm nonrcccform = new RateCardComponentChargesForm();
    nonrcccform.setRcc(rcc);
    nonrcccform.setCharges(charges);
    nonRecurringRateCardChargesFormList.add(nonrcccform);
    List<RateCardComponentChargesForm> recurringRateCardChargesFormList = new ArrayList<RateCardComponentChargesForm>();
    RateCardComponent rrcc = rateCardComponentDAO.find("17");
    RateCardComponentChargesForm rcccform = new RateCardComponentChargesForm();
    rcccform.setRcc(rrcc);
    rcccform.setCharges(charges);
    recurringRateCardChargesFormList.add(rcccform);

    List<RateCardChargesForm> rateCardChargesList = new ArrayList<RateCardChargesForm>();
    RateCardChargesForm rccform = new RateCardChargesForm();
    rccform.setBundle(bundle);
    rccform.setNonRecurringRateCardChargesFormList(nonRecurringRateCardChargesFormList);
    rccform.setRecurringRateCardChargesFormList(recurringRateCardChargesFormList);
    rateCardChargesList.add(rccform);

    Map<ProductBundle, Map<RateCardComponent, List<RateCardCharge>>> currentBundleChargesMap = bundleService
        .getChargesForAllBundles(new Date());
    RateCardForm rcform = new RateCardForm();
    rcform.setPlannedDate(new Date());
    rcform.setStartDate(new Date());
    rcform.setCurrentBundleChargesMap(currentBundleChargesMap);
    rcform.setRateCardChargesList(rateCardChargesList);

    BindingResult result = validate(rcform);
    String resultString = bundleController.planCharges(rcform, result, map);
    Assert.assertNotNull(resultString);
    Assert.assertEquals("success", resultString);

    // Viewing Plannned Charges
    String result1 = bundleController.viewBundlePlannedCharges(bundle.getCode(), map, "");
    Assert.assertNotNull(result1);
    Assert.assertEquals("view.bundle.planned.charges", result1);
    RateCardChargesForm obtainedRCCFORM = (RateCardChargesForm) map.get("rateCardChargesForm");
    List<RateCardComponentChargesForm> rcccformList = obtainedRCCFORM.getRecurringRateCardChargesFormList();
    for (RateCardComponentChargesForm obtainedrcccform : rcccformList) {
      Assert.assertEquals(rrcc, obtainedrcccform.getRcc());
    }
    List<RateCardComponentChargesForm> nonrcccformList = obtainedRCCFORM.getNonRecurringRateCardChargesFormList();
    for (RateCardComponentChargesForm obtainedrcccform : nonrcccformList) {
      Assert.assertEquals(rcc, obtainedrcccform.getRcc());
    }

  }

  /*
   * Description: Test to Set, Edit and View Planned Charges for a Bundle as Root user Author: Vinayv
   */
  @Test
  public void testEditAndViewPlannedCharges() throws Exception {

    int noOfdays = 3;
    Calendar createdAt = Calendar.getInstance();
    createdAt.add(Calendar.DATE, 0 + noOfdays);
    ProductBundle bundle = bundleService.locateProductBundleById("3");

    // Setting Planned Charges
    List<RateCardComponentChargesForm> nonRecurringRateCardChargesFormList = new ArrayList<RateCardComponentChargesForm>();
    RateCardComponent rcc = rateCardComponentDAO.find("18");
    List<RateCardCharge> charges = bundleService.getCatalogProductBundlePlannedCharges(null, bundle);
    RateCardComponentChargesForm nonrcccform = new RateCardComponentChargesForm();
    nonrcccform.setRcc(rcc);
    nonrcccform.setCharges(charges);
    nonRecurringRateCardChargesFormList.add(nonrcccform);
    List<RateCardComponentChargesForm> recurringRateCardChargesFormList = new ArrayList<RateCardComponentChargesForm>();
    RateCardComponent rrcc = rateCardComponentDAO.find("17");
    RateCardComponentChargesForm rcccform = new RateCardComponentChargesForm();
    rcccform.setRcc(rrcc);
    rcccform.setCharges(charges);
    recurringRateCardChargesFormList.add(rcccform);

    List<RateCardChargesForm> rateCardChargesList = new ArrayList<RateCardChargesForm>();
    RateCardChargesForm rccform = new RateCardChargesForm();
    rccform.setBundle(bundle);
    rccform.setNonRecurringRateCardChargesFormList(nonRecurringRateCardChargesFormList);
    rccform.setRecurringRateCardChargesFormList(recurringRateCardChargesFormList);
    rateCardChargesList.add(rccform);

    Map<ProductBundle, Map<RateCardComponent, List<RateCardCharge>>> currentBundleChargesMap = bundleService
        .getChargesForAllBundles(new Date());
    RateCardForm rcform = new RateCardForm();
    rcform.setPlannedDate(new Date());
    rcform.setStartDate(new Date());
    rcform.setCurrentBundleChargesMap(currentBundleChargesMap);
    rcform.setRateCardChargesList(rateCardChargesList);

    BindingResult bindlingResultSet = validate(rcform);
    String resultStringSet = bundleController.planCharges(rcform, bindlingResultSet, map);
    Assert.assertNotNull(resultStringSet);
    Assert.assertEquals("success", resultStringSet);

    // Viewing Planned Charges Before Editing
    String result1 = bundleController.viewBundlePlannedCharges(bundle.getCode(), map, "");
    Assert.assertNotNull(result1);
    Assert.assertEquals("view.bundle.planned.charges", result1);
    RateCardChargesForm obtainedRCCFORM = (RateCardChargesForm) map.get("rateCardChargesForm");
    List<RateCardComponentChargesForm> rcccformList = obtainedRCCFORM.getRecurringRateCardChargesFormList();
    for (RateCardComponentChargesForm obtainedrcccform : rcccformList) {
      Assert.assertEquals(rrcc, obtainedrcccform.getRcc());
    }
    List<RateCardComponentChargesForm> nonrcccformList = obtainedRCCFORM.getNonRecurringRateCardChargesFormList();
    for (RateCardComponentChargesForm obtainedrcccform : nonrcccformList) {
      Assert.assertEquals(rcc, obtainedrcccform.getRcc());
    }

    // Editing Planned Charges
    List<RateCardComponentChargesForm> editNonRecurringRateCardChargesFormList = new ArrayList<RateCardComponentChargesForm>();
    RateCardComponent editrcc = rateCardComponentDAO.find("18");

    List<RateCardCharge> editcharges = bundleService.getCatalogProductBundlePlannedCharges(null, bundle);
    for (RateCardCharge rateCardCharge : editcharges) {
      if (rateCardCharge.getRateCardComponent().equals(editrcc)) {
        if (rateCardCharge.getCurrencyValue().getCurrencyCode().equalsIgnoreCase("USD")) {
          rateCardCharge.setPrice(BigDecimal.TEN);
        }
      }
    }
    RateCardComponentChargesForm editnonrcccform = new RateCardComponentChargesForm();
    editnonrcccform.setRcc(editrcc);
    editnonrcccform.setCharges(editcharges);
    editNonRecurringRateCardChargesFormList.add(editnonrcccform);

    List<RateCardComponentChargesForm> editRecurringRateCardChargesFormList = new ArrayList<RateCardComponentChargesForm>();
    RateCardComponent editrrcc = rateCardComponentDAO.find("17");
    for (RateCardCharge rateCardCharge : editcharges) {
      if (rateCardCharge.getRateCardComponent().equals(editrrcc)) {
        if (rateCardCharge.getCurrencyValue().getCurrencyCode().equalsIgnoreCase("USD")) {
          rateCardCharge.setPrice(BigDecimal.valueOf(500));
        }
      }
    }
    RateCardComponentChargesForm editrcccform = new RateCardComponentChargesForm();
    editrcccform.setRcc(editrrcc);
    editrcccform.setCharges(editcharges);
    editRecurringRateCardChargesFormList.add(editrcccform);

    List<RateCardChargesForm> editRateCardChargesList = new ArrayList<RateCardChargesForm>();
    RateCardChargesForm editrccform = new RateCardChargesForm();
    editrccform.setBundle(bundle);
    editrccform.setNonRecurringRateCardChargesFormList(editNonRecurringRateCardChargesFormList);
    editrccform.setRecurringRateCardChargesFormList(editRecurringRateCardChargesFormList);
    editRateCardChargesList.add(editrccform);

    Map<ProductBundle, Map<RateCardComponent, List<RateCardCharge>>> editCurrentBundleChargesMap = bundleService
        .getChargesForAllBundles(new Date());
    RateCardForm editrcform = new RateCardForm();
    rcform.setPlannedDate(new Date());
    rcform.setStartDate(new Date());
    rcform.setCurrentBundleChargesMap(editCurrentBundleChargesMap);
    rcform.setRateCardChargesList(editRateCardChargesList);

    BindingResult bindlingResultEdit = validate(editrcform);
    String resultStringEdit = bundleController.editPlannedCharges(editrcform, bindlingResultEdit, map);
    Assert.assertNotNull(resultStringEdit);
    Assert.assertEquals("success", resultStringEdit);

    // Viewing Planned Charges After Editing
    String result2 = bundleController.viewBundlePlannedCharges(bundle.getCode(), map, "");
    Assert.assertNotNull(result2);
    Assert.assertEquals("view.bundle.planned.charges", result2);
    RateCardChargesForm editedRCCFORM = (RateCardChargesForm) map.get("rateCardChargesForm");
    List<RateCardComponentChargesForm> editedrcccformList = editedRCCFORM.getRecurringRateCardChargesFormList();
    for (RateCardComponentChargesForm editedobtainedrcccform : editedrcccformList) {
      Assert.assertEquals(editrrcc, editedobtainedrcccform.getRcc());
      List<RateCardCharge> obtainedList = editedobtainedrcccform.getCharges();
      for (RateCardCharge obtainedRateCardCharge : obtainedList) {
        if (obtainedRateCardCharge.getCurrencyValue().getCurrencyCode().equalsIgnoreCase("USD")) {
          Assert.assertEquals(BigDecimal.valueOf(500), obtainedRateCardCharge.getPrice());
        }
      }
    }
    List<RateCardComponentChargesForm> editednonrcccformList = editedRCCFORM.getNonRecurringRateCardChargesFormList();
    for (RateCardComponentChargesForm editedobtainedrcccform : editednonrcccformList) {
      Assert.assertEquals(editrcc, editedobtainedrcccform.getRcc());
      List<RateCardCharge> obtainedList = editedobtainedrcccform.getCharges();
      for (RateCardCharge obtainedRateCardCharge : obtainedList) {
        if (obtainedRateCardCharge.getCurrencyValue().getCurrencyCode().equalsIgnoreCase("USD")) {
          Assert.assertEquals(BigDecimal.TEN, obtainedRateCardCharge.getPrice());
        }
      }
    }
  }

  /*
   * Description: Test to Set, Edit and View Planned Charges for a Bundle as Product Manager Author: Vinayv
   */
  @Test
  public void testEditAndViewPlannedChargesAsProductManager() throws Exception {

    User user = userDAO.find("3");
    user.setProfile(profileDAO.find("7"));
    userDAO.save(user);
    asUser(user);

    int noOfdays = 3;
    Calendar createdAt = Calendar.getInstance();
    createdAt.add(Calendar.DATE, 0 + noOfdays);
    ProductBundle bundle = bundleService.locateProductBundleById("3");

    // Setting Planned Charges
    List<RateCardComponentChargesForm> nonRecurringRateCardChargesFormList = new ArrayList<RateCardComponentChargesForm>();
    RateCardComponent rcc = rateCardComponentDAO.find("18");
    List<RateCardCharge> charges = bundleService.getCatalogProductBundlePlannedCharges(null, bundle);
    RateCardComponentChargesForm nonrcccform = new RateCardComponentChargesForm();
    nonrcccform.setRcc(rcc);
    nonrcccform.setCharges(charges);
    nonRecurringRateCardChargesFormList.add(nonrcccform);
    List<RateCardComponentChargesForm> recurringRateCardChargesFormList = new ArrayList<RateCardComponentChargesForm>();
    RateCardComponent rrcc = rateCardComponentDAO.find("17");
    RateCardComponentChargesForm rcccform = new RateCardComponentChargesForm();
    rcccform.setRcc(rrcc);
    rcccform.setCharges(charges);
    recurringRateCardChargesFormList.add(rcccform);

    List<RateCardChargesForm> rateCardChargesList = new ArrayList<RateCardChargesForm>();
    RateCardChargesForm rccform = new RateCardChargesForm();
    rccform.setBundle(bundle);
    rccform.setNonRecurringRateCardChargesFormList(nonRecurringRateCardChargesFormList);
    rccform.setRecurringRateCardChargesFormList(recurringRateCardChargesFormList);
    rateCardChargesList.add(rccform);

    Map<ProductBundle, Map<RateCardComponent, List<RateCardCharge>>> currentBundleChargesMap = bundleService
        .getChargesForAllBundles(new Date());
    RateCardForm rcform = new RateCardForm();
    rcform.setPlannedDate(new Date());
    rcform.setStartDate(new Date());
    rcform.setCurrentBundleChargesMap(currentBundleChargesMap);
    rcform.setRateCardChargesList(rateCardChargesList);

    BindingResult bindlingResultSet = validate(rcform);
    String resultStringSet = bundleController.planCharges(rcform, bindlingResultSet, map);
    Assert.assertNotNull(resultStringSet);
    Assert.assertEquals("success", resultStringSet);

    // Viewing Planned Charges Before Editing
    String result1 = bundleController.viewBundlePlannedCharges(bundle.getCode(), map, "");
    Assert.assertNotNull(result1);
    Assert.assertEquals("view.bundle.planned.charges", result1);
    RateCardChargesForm obtainedRCCFORM = (RateCardChargesForm) map.get("rateCardChargesForm");
    List<RateCardComponentChargesForm> rcccformList = obtainedRCCFORM.getRecurringRateCardChargesFormList();
    for (RateCardComponentChargesForm obtainedrcccform : rcccformList) {
      Assert.assertEquals(rrcc, obtainedrcccform.getRcc());
    }
    List<RateCardComponentChargesForm> nonrcccformList = obtainedRCCFORM.getNonRecurringRateCardChargesFormList();
    for (RateCardComponentChargesForm obtainedrcccform : nonrcccformList) {
      Assert.assertEquals(rcc, obtainedrcccform.getRcc());
    }

    // Editing Planned Charges
    List<RateCardComponentChargesForm> editNonRecurringRateCardChargesFormList = new ArrayList<RateCardComponentChargesForm>();
    RateCardComponent editrcc = rateCardComponentDAO.find("18");

    List<RateCardCharge> editcharges = bundleService.getCatalogProductBundlePlannedCharges(null, bundle);
    for (RateCardCharge rateCardCharge : editcharges) {
      if (rateCardCharge.getRateCardComponent().equals(editrcc)) {
        if (rateCardCharge.getCurrencyValue().getCurrencyCode().equalsIgnoreCase("USD")) {
          rateCardCharge.setPrice(BigDecimal.TEN);
        }
      }
    }
    RateCardComponentChargesForm editnonrcccform = new RateCardComponentChargesForm();
    editnonrcccform.setRcc(editrcc);
    editnonrcccform.setCharges(editcharges);
    editNonRecurringRateCardChargesFormList.add(editnonrcccform);

    List<RateCardComponentChargesForm> editRecurringRateCardChargesFormList = new ArrayList<RateCardComponentChargesForm>();
    RateCardComponent editrrcc = rateCardComponentDAO.find("17");
    for (RateCardCharge rateCardCharge : editcharges) {
      if (rateCardCharge.getRateCardComponent().equals(editrrcc)) {
        if (rateCardCharge.getCurrencyValue().getCurrencyCode().equalsIgnoreCase("USD")) {
          rateCardCharge.setPrice(BigDecimal.valueOf(500));
        }
      }
    }
    RateCardComponentChargesForm editrcccform = new RateCardComponentChargesForm();
    editrcccform.setRcc(editrrcc);
    editrcccform.setCharges(editcharges);
    editRecurringRateCardChargesFormList.add(editrcccform);

    List<RateCardChargesForm> editRateCardChargesList = new ArrayList<RateCardChargesForm>();
    RateCardChargesForm editrccform = new RateCardChargesForm();
    editrccform.setBundle(bundle);
    editrccform.setNonRecurringRateCardChargesFormList(editNonRecurringRateCardChargesFormList);
    editrccform.setRecurringRateCardChargesFormList(editRecurringRateCardChargesFormList);
    editRateCardChargesList.add(editrccform);

    Map<ProductBundle, Map<RateCardComponent, List<RateCardCharge>>> editCurrentBundleChargesMap = bundleService
        .getChargesForAllBundles(new Date());
    RateCardForm editrcform = new RateCardForm();
    rcform.setPlannedDate(new Date());
    rcform.setStartDate(new Date());
    rcform.setCurrentBundleChargesMap(editCurrentBundleChargesMap);
    rcform.setRateCardChargesList(editRateCardChargesList);

    BindingResult bindlingResultEdit = validate(editrcform);
    String resultStringEdit = bundleController.editPlannedCharges(editrcform, bindlingResultEdit, map);
    Assert.assertNotNull(resultStringEdit);
    Assert.assertEquals("success", resultStringEdit);

    // Viewing Planned Charges After Editing
    String result2 = bundleController.viewBundlePlannedCharges(bundle.getCode(), map, "");
    Assert.assertNotNull(result2);
    Assert.assertEquals("view.bundle.planned.charges", result2);
    RateCardChargesForm editedRCCFORM = (RateCardChargesForm) map.get("rateCardChargesForm");
    List<RateCardComponentChargesForm> editedrcccformList = editedRCCFORM.getRecurringRateCardChargesFormList();
    for (RateCardComponentChargesForm editedobtainedrcccform : editedrcccformList) {
      Assert.assertEquals(editrrcc, editedobtainedrcccform.getRcc());
      List<RateCardCharge> obtainedList = editedobtainedrcccform.getCharges();
      for (RateCardCharge obtainedRateCardCharge : obtainedList) {
        if (obtainedRateCardCharge.getCurrencyValue().getCurrencyCode().equalsIgnoreCase("USD")) {
          Assert.assertEquals(BigDecimal.valueOf(500), obtainedRateCardCharge.getPrice());
        }
      }
    }
    List<RateCardComponentChargesForm> editednonrcccformList = editedRCCFORM.getNonRecurringRateCardChargesFormList();
    for (RateCardComponentChargesForm editedobtainedrcccform : editednonrcccformList) {
      Assert.assertEquals(editrcc, editedobtainedrcccform.getRcc());
      List<RateCardCharge> obtainedList = editedobtainedrcccform.getCharges();
      for (RateCardCharge obtainedRateCardCharge : obtainedList) {
        if (obtainedRateCardCharge.getCurrencyValue().getCurrencyCode().equalsIgnoreCase("USD")) {
          Assert.assertEquals(BigDecimal.TEN, obtainedRateCardCharge.getPrice());
        }
      }
    }
  }

  /*
   * Description: Test for adding new bundle to the existing channel on the same bundle creation date. Author:Avinashg
   */

  @Test
  public void testAddBundletoExistingChannelOnCreationDay() throws Exception {
    ServiceResourceType resourceType = serviceResourceTypeDAO.find("1");

    String compAssociationJson = "[{association: INCLUDES, compName: ISO, compValues:[ {compName: DOS-ISO, compValueName: 10 } ] }]";

    ProductBundle productBundle = testCreateProductBundle("1", resourceType.getId().toString(), "MONTHLY",
        "testBundle", "USD", BigDecimal.valueOf(149), new Date(), compAssociationJson, ResourceConstraint.PER_USER,
        true);

    productBundle.setPublish(true);

    bundleService.updateProductBundle(productBundle, true);
    productService.setReferencePriceBookFutureRevisionDate(new Date());

    List<Channel> beforeChannelList = channelService.listChannelsByProductBundle(productBundle, 1, 50);
    Assert.assertEquals(0, beforeChannelList.size());

    Channel channel = channelService.locateChannel("Channel2");
    channelService.syncChannel(channel);

    List<Revision> allChannelRevisions = productService.getAllRevisions(channel.getCatalog());

    Revision firstChannelRevision = allChannelRevisions.get(0);

    CatalogProductBundle catalogProductBundle = new CatalogProductBundle(channel.getCatalog(), productBundle,
        getPortalUser());

    catalogProductBundle.setCreatedAt(new Date());
    catalogProductBundle.setRevision(firstChannelRevision);

    channelService.save(catalogProductBundle);

    List<Channel> afterChannelList = channelService.listChannelsByProductBundle(productBundle, 1, 50);
    Assert.assertEquals(1, afterChannelList.size());

  }

  /*
   * Description: Test for adding new bundle to the new channel on the same bundle creation date and tenant can
   * subscribe VM by using the new bundle. Author:Avinashg
   */

  @Test
  public void testAddBundletonNewChannelOnCreationDay() throws Exception {
    ServiceResourceType resourceType = serviceResourceTypeDAO.find("1");

    String compAssociationJson = "[{association: INCLUDES, compName: ISO, compValues:[ {compName: DOS-ISO, compValueName: 10 } ] }]";

    ProductBundle productBundle = testCreateProductBundle("1", resourceType.getId().toString(), "MONTHLY",
        "testBundle", "USD", BigDecimal.valueOf(149), new Date(), compAssociationJson, ResourceConstraint.PER_USER,
        true);

    productBundle.setPublish(true);

    bundleService.updateProductBundle(productBundle, true);
    productService.setReferencePriceBookFutureRevisionDate(new Date());

    List<Channel> beforeChannelList = channelService.listChannelsByProductBundle(productBundle, 1, 50);
    Assert.assertEquals(0, beforeChannelList.size());

    // Channel channel = channelService.locateChannel("Channel2");
    Channel channel = new Channel("newChannel", ChannelType.CHANNEL);
    Catalog catalog = new Catalog("Test Catalog" + new Date(), "Test Catalog", new Date(), new Date(), getPortalUser());
    catalogDAO.save(catalog);
    channel.setCatalog(catalog);
    Channel newchannel = channelService.createChannel(channel);
    channelService.syncChannel(newchannel);

    List<Revision> allChannelRevisions = productService.getAllRevisions(newchannel.getCatalog());

    Revision firstChannelRevision = allChannelRevisions.get(allChannelRevisions.size() - 1);

    CatalogProductBundle catalogProductBundle = new CatalogProductBundle(newchannel.getCatalog(), productBundle,
        getPortalUser());

    catalogProductBundle.setCreatedAt(new Date());
    catalogProductBundle.setRevision(firstChannelRevision);

    channelService.save(catalogProductBundle);

    List<Channel> afterChannelList = channelService.listChannelsByProductBundle(productBundle, 1, 50);
    Assert.assertEquals(1, afterChannelList.size());
  }

  /*
   * @author Abhaik
   * @description : Verify filter for Product Bundles by State "All" in view current tab
   */

  @Test
  public void testAllProductBundleFilterInCurrentViewForProducts() {

    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = bundleController.listProductBundles("b1c9fbb0-8dab-42dc-ae0a-ce1384a1e6",
          "003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page), null, null, "current", null, "All", map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("bundles.list", result);
      List<ProductBundle> productBundleList = (List<ProductBundle>) map1.get("productBundlesList");
      count = count + productBundleList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    // TODO Need better asserts
    Assert.assertEquals(29, count);
  }

  /*
   * @author Abhaik
   * @description : Verify filter for Product Bundles by State "publish" in view current tab
   */

  @Test
  public void testPublishProductBundleFilterInCurrentViewForProducts() {

    ProductBundle bundle = bundleService.getProductBundleById(2L);
    bundle.setPublish(false);
    bundleService.updateProductBundle(bundle, true);
    bundle = bundleService.getProductBundleById(2L);
    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = bundleController.listProductBundles("b1c9fbb0-8dab-42dc-ae0a-ce1384a1e6",
          "003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page), null, null, "current", null, "publish", map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("bundles.list", result);
      List<ProductBundle> productBundleList = (List<ProductBundle>) map1.get("productBundlesList");
      count = count + productBundleList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    // TODO Need better asserts
    Assert.assertEquals(27, count);
  }

  /*
   * @author Abhaik
   * @description : Verify filter for Product Bundles by State "Unpublish" in view current tab
   */

  @Test
  public void testUnpublishProductBundleFilterInCurrentViewForProducts() {

    ProductBundle bundle = bundleService.getProductBundleById(2L);
    bundle.setPublish(false);
    bundleService.updateProductBundle(bundle, true);
    bundle = bundleService.getProductBundleById(2L);
    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = bundleController.listProductBundles("b1c9fbb0-8dab-42dc-ae0a-ce1384a1e6",
          "003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page), null, null, "current", null, "unpublish",
          map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("bundles.list", result);
      List<ProductBundle> productBundleList = (List<ProductBundle>) map1.get("productBundlesList");
      count = count + productBundleList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    Assert.assertEquals(2, count);
  }

  /*
   * @author Abhaik
   * @description : Verify filter for Product Bundles by State "Null" in view current tab
   */

  @Test
  public void testNullProductBundleFilterInCurrentViewForProducts() {

    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = bundleController.listProductBundles("b1c9fbb0-8dab-42dc-ae0a-ce1384a1e6",
          "003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page), null, null, "current", null, null, map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("bundles.list", result);
      List<ProductBundle> productBundleList = (List<ProductBundle>) map1.get("productBundlesList");
      count = count + productBundleList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    // TODO Need better asserts
    Assert.assertEquals(29, count);
  }

  /*
   * @author Abhaik
   * @description : Verify filter for Product Bundles by State "Empty" in view current tab
   */

  @Test
  public void testEmptyProductBundleFilterInCurrentViewForProducts() {

    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = bundleController.listProductBundles("b1c9fbb0-8dab-42dc-ae0a-ce1384a1e6",
          "003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page), null, null, "current", null, "", map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("bundles.list", result);
      List<ProductBundle> productBundleList = (List<ProductBundle>) map1.get("productBundlesList");
      count = count + productBundleList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    // TODO Need better asserts
    Assert.assertEquals(29, count);
  }

  /*
   * @author Abhaik
   * @description : Verify filter for Product Bundles by State "Invalid" in view current tab
   */

  @Test
  public void testInvalidProductBundleFilterInCurrentViewForProducts() {

    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = bundleController.listProductBundles("b1c9fbb0-8dab-42dc-ae0a-ce1384a1e6",
          "003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page), null, null, "current", null, "Invalid", map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("bundles.list", result);
      List<ProductBundle> productBundleList = (List<ProductBundle>) map1.get("productBundlesList");
      count = count + productBundleList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    Assert.assertEquals(0, count);
  }

  /*
   * @author Abhaik
   * @description : Verify filter for Product Bundles by State "All" in view history tab
   */

  @Test
  public void testAllProductBundleFilterInhistoryViewForProducts() {

    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = bundleController.listProductBundles("b1c9fbb0-8dab-42dc-ae0a-ce1384a1e6",
          "003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page), null, null, "history", null, "All", map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("bundles.list", result);
      List<ProductBundle> productBundleList = (List<ProductBundle>) map1.get("productBundlesList");
      count = count + productBundleList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    // TODO Need better asserts
    Assert.assertEquals(29, count);
  }

  /*
   * @author Abhaik
   * @description : Verify filter for Product Bundles by State "publish" in view history tab
   */

  @Test
  public void testPublishProductBundleFilterInhistoryViewForProducts() {

    ProductBundle bundle = bundleService.getProductBundleById(2L);
    bundle.setPublish(false);
    bundleService.updateProductBundle(bundle, true);
    bundle = bundleService.getProductBundleById(2L);
    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = bundleController.listProductBundles("b1c9fbb0-8dab-42dc-ae0a-ce1384a1e6",
          "003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page), null, null, "history", null, "publish", map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("bundles.list", result);
      List<ProductBundle> productBundleList = (List<ProductBundle>) map1.get("productBundlesList");
      count = count + productBundleList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    // TODO Need better asserts
    Assert.assertEquals(27, count);
  }

  /*
   * @author Abhaik
   * @description : Verify filter for Product Bundles by State "Unpublish" in view history tab
   */

  @Test
  public void testUnpublishProductBundleFilterInhistorytViewForProducts() {

    ProductBundle bundle = bundleService.getProductBundleById(2L);
    bundle.setPublish(false);
    bundleService.updateProductBundle(bundle, true);
    bundle = bundleService.getProductBundleById(2L);
    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = bundleController.listProductBundles("b1c9fbb0-8dab-42dc-ae0a-ce1384a1e6",
          "003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page), null, null, "history", null, "unpublish",
          map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("bundles.list", result);
      List<ProductBundle> productBundleList = (List<ProductBundle>) map1.get("productBundlesList");
      count = count + productBundleList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    Assert.assertEquals(2, count);
  }

  /*
   * @author Abhaik
   * @description : Verify filter for Product Bundles by State "Null" in view History tab
   */

  @Test
  public void testNullProductBundleFilterInHistoryViewForProducts() {

    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = bundleController.listProductBundles("b1c9fbb0-8dab-42dc-ae0a-ce1384a1e6",
          "003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page), null, null, "history", null, null, map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("bundles.list", result);
      List<ProductBundle> productBundleList = (List<ProductBundle>) map1.get("productBundlesList");
      count = count + productBundleList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    // TODO Need better asserts
    Assert.assertEquals(29, count);
  }

  /*
   * @author Abhaik
   * @description : Verify filter for Product Bundles by State "Empty" in view History tab
   */

  @Test
  public void testEmptyProductBundleFilterInHistoryViewForProducts() {

    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = bundleController.listProductBundles("b1c9fbb0-8dab-42dc-ae0a-ce1384a1e6",
          "003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page), null, null, "history", null, "", map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("bundles.list", result);
      List<ProductBundle> productBundleList = (List<ProductBundle>) map1.get("productBundlesList");
      count = count + productBundleList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    // TODO Need better asserts
    Assert.assertEquals(29, count);
  }

  /*
   * @author Abhaik
   * @description : Verify filter for Product Bundles by State "Invalid" in view History tab
   */

  @Test
  public void testInvalidProductBundleFilterInHistoryViewForProducts() {

    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = bundleController.listProductBundles("b1c9fbb0-8dab-42dc-ae0a-ce1384a1e6",
          "003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page), null, null, "history", null, "Invalid", map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("bundles.list", result);
      List<ProductBundle> productBundleList = (List<ProductBundle>) map1.get("productBundlesList");
      count = count + productBundleList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    Assert.assertEquals(0, count);
  }

  /*
   * @author Abhaik
   * @description : Verify filter for Product Bundles by State "All" in view PlanNext tab
   */

  @Test
  public void testAllProductBundleFilterInPlanNextViewForProducts() {

    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = bundleController.listProductBundles("b1c9fbb0-8dab-42dc-ae0a-ce1384a1e6",
          "003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page), null, null, "planned", null, "All", map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("bundles.list", result);
      List<ProductBundle> productBundleList = (List<ProductBundle>) map1.get("productBundlesList");
      count = count + productBundleList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    // TODO Need better asserts
    Assert.assertEquals(29, count);
  }

  /*
   * @author Abhaik
   * @description : Verify filter for Product Bundles by State "publish" in view PlanNext tab
   */

  @Test
  public void testPublishProductBundleFilterInPlanNextViewForProducts() {

    ProductBundle bundle = bundleService.getProductBundleById(2L);
    bundle.setPublish(false);
    bundleService.updateProductBundle(bundle, true);
    bundle = bundleService.getProductBundleById(2L);
    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = bundleController.listProductBundles("b1c9fbb0-8dab-42dc-ae0a-ce1384a1e6",
          "003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page), null, null, "planned", null, "publish", map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("bundles.list", result);
      List<ProductBundle> productBundleList = (List<ProductBundle>) map1.get("productBundlesList");
      count = count + productBundleList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    // TODO Need better asserts
    Assert.assertEquals(27, count);
  }

  /*
   * @author Abhaik
   * @description : Verify filter for Product Bundles by State "All" in view PlanNext tab
   */

  @Test
  public void testUnpublishProductBundleFilterInPlanNextViewForProducts() {

    ProductBundle bundle = bundleService.getProductBundleById(2L);
    bundle.setPublish(false);
    bundleService.updateProductBundle(bundle, true);
    bundle = bundleService.getProductBundleById(2L);
    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = bundleController.listProductBundles("b1c9fbb0-8dab-42dc-ae0a-ce1384a1e6",
          "003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page), null, null, "planned", null, "unpublish",
          map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("bundles.list", result);
      List<ProductBundle> productBundleList = (List<ProductBundle>) map1.get("productBundlesList");
      count = count + productBundleList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    Assert.assertEquals(2, count);
  }

  /*
   * @author Abhaik
   * @description : Verify filter for Product Bundles by State "Null" in view PlanNext tab
   */

  @Test
  public void testNullProductBundleFilterInPlanNextViewForProducts() {

    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = bundleController.listProductBundles("b1c9fbb0-8dab-42dc-ae0a-ce1384a1e6",
          "003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page), null, null, "planned", null, null, map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("bundles.list", result);
      List<ProductBundle> productBundleList = (List<ProductBundle>) map1.get("productBundlesList");
      count = count + productBundleList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    // TODO Need better asserts
    Assert.assertEquals(29, count);
  }

  /*
   * @author Abhaik
   * @description : Verify filter for Product Bundles by State "Empty" in view PlanNext tab
   */

  @Test
  public void testEmptyProductBundleFilterInPlanNextViewForProducts() {

    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = bundleController.listProductBundles("b1c9fbb0-8dab-42dc-ae0a-ce1384a1e6",
          "003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page), null, null, "planned", null, "", map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("bundles.list", result);
      List<ProductBundle> productBundleList = (List<ProductBundle>) map1.get("productBundlesList");
      count = count + productBundleList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    // TODO Need better asserts
    Assert.assertEquals(29, count);
  }

  /*
   * @author Abhaik
   * @description : Verify filter for Product Bundles by State "Invalid" in view PlanNext tab
   */

  @Test
  public void testInvalidProductBundleFilterInPlanNextViewForProducts() {

    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = bundleController.listProductBundles("b1c9fbb0-8dab-42dc-ae0a-ce1384a1e6",
          "003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page), null, null, "planned", null, "Invalid", map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("bundles.list", result);
      List<ProductBundle> productBundleList = (List<ProductBundle>) map1.get("productBundlesList");
      count = count + productBundleList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    Assert.assertEquals(0, count);
  }

  @Test
  public void testEditProductBundle() throws Exception {

    User user = userService.get("2ee0d598-00c8-4e12-b307-1cab5305e867");
    ServiceInstance serviceInstance = serviceInstanceDAO.find(1L);
    ServiceResourceType serviceResourceType = serviceResourceTypeDAO.find(1L);
    Map<String, Set<String>> serviceDiscriminatorMap = new HashMap<String, Set<String>>();

    serviceDiscriminatorMap.clear();
    serviceDiscriminatorMap.put("Template", makeSet("210"));
    List<ProductBundleRevision> pbRevisions = bundleService.getProductBundleRevisions(serviceInstance,
        serviceResourceType, serviceDiscriminatorMap, null, user, null);
    Assert.assertNotNull("product bundle revision list cannot be null", pbRevisions);

    // Verifying presence of a Bundle in which INCLUDES constrains matches
    ProductBundle productBundle = bundleService.getProductBundleById(2L);

    Revision futureRevision = channelService.getFutureRevision(null);
    List<ProvisioningConstraint> provisioningConstraints = bundleService.getProductBundleRevision(productBundle,
        futureRevision.getStartDate(), null).getProvisioningConstraints();

    Assert.assertNotNull(provisioningConstraints);
    Assert.assertFalse(provisioningConstraints.isEmpty());

    String compAssociationJson = "[{compDbId: 1, association: INCLUDES, compName: ISO, compValue: DOS-ISO, compValueName: 10 }]";

    ProductBundleForm form = new ProductBundleForm(productBundle);
    form.setCompAssociationJson(compAssociationJson);
    BindingResult result = validate(form);

    ProductBundle obtainedProductBundle = bundleController.editProductBundle(form, result, map);
    Assert.assertNotNull(obtainedProductBundle);
    provisioningConstraints = bundleService.getProductBundleRevision(obtainedProductBundle, new Date(), null)
        .getProvisioningConstraints();
    Assert.assertNotNull(provisioningConstraints);
    Assert.assertFalse(provisioningConstraints.isEmpty());

  }

  @Test
  public void testGetComponentsContainsFilters() {
    Map<String, List<String>> componentCollection = bundleController.getServiceResourceComponents(1L);
    Assert.assertNotNull(componentCollection);
    Assert.assertTrue(componentCollection.get("filters").contains("zone"));
  }

  private Set<String> makeSet(String s) {
    Set<String> result = new HashSet<String>();
    result.add(s);
    return result;
  }

  @Test
  public void testlistProductBundlesAsATenant() {
    try {
      Boolean found = false;
      Tenant tenant = tenantDAO.find(4L);
      Channel channel = channelDAO.find(4L);
      ProductBundle bundle = bundleService.getProductBundleById(2L);
      request.setAttribute("effectiveTenant", tenant);
      String currencyCode = channelService.listCurrencies(channel.getParam()).get(0).getCurrencyCode();

      List<ProductBundleRevision> bundleList = bundleController.listProductBundles(tenant, tenant.getParam(),
          serviceInstanceDAO.find(1L).getUuid(), "VirtualMachine", "zone=122,Template=56,SO=10, hypervisor=kuchbhi",
          "PSI_UD1=10", true, "current", channel.getId().toString(), currencyCode, null, null, request);
      for (ProductBundleRevision pbr : bundleList) {
        if (bundle.getName().equals(pbr.getProductBundle().getName())) {
          found = true;
        }
      }
      Assert.assertTrue(found);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testlistProductBundlesAsRoot() {
    try {
      Boolean found = false;
      Tenant tenant = tenantService.getSystemTenant();
      Channel channel = channelDAO.find(4L);
      ProductBundle bundle = bundleService.getProductBundleById(2L);
      request.setAttribute("effectiveTenant", tenant);
      String currencyCode = channelService.listCurrencies(channel.getParam()).get(0).getCurrencyCode();

      List<ProductBundleRevision> bundleList = bundleController.listProductBundles(tenant, tenant.getParam(),
          serviceInstanceDAO.find(1L).getUuid(), "VirtualMachine", "zone=122,Template=56,SO=10, hypervisor=kuchbhi",
          "PSI_UD1=10", true, "current", channel.getId().toString(), currencyCode, null, null, request);
      for (ProductBundleRevision pbr : bundleList) {
        if (bundle.getName().equals(pbr.getProductBundle().getName())) {
          found = true;
        }
      }
      Assert.assertTrue(found);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testlistProductBundlesHistory() {
    try {
      Boolean found = false;
      Tenant tenant = tenantService.getSystemTenant();
      Channel channel = channelDAO.find(4L);
      ProductBundle bundle = bundleService.getProductBundleById(2L);
      request.setAttribute("effectiveTenant", tenant);
      String currencyCode = channelService.listCurrencies(channel.getParam()).get(0).getCurrencyCode();

      List<ProductBundleRevision> bundleList = bundleController.listProductBundles(tenant, tenant.getParam(),
          serviceInstanceDAO.find(1L).getUuid(), "VirtualMachine", "zone=122,Template=56,SO=10, hypervisor=kuchbhi",
          "PSI_UD1=10", true, "history", channel.getId().toString(), currencyCode, "04/30/2012 00:00:00",
          "MM/dd/yyyy HH:mm:ss", request);
      for (ProductBundleRevision pbr : bundleList) {
        Assert.assertEquals(bundle.getName(), pbr.getProductBundle().getName());
        found = true;
      }
      Assert.assertTrue(found);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testlistProductBundlesForAnonymousCatalog() {
    try {
      Boolean found = false;
      Channel channel = channelDAO.find(4L);
      ProductBundle bundle = bundleService.getProductBundleById(2L);
      request.setAttribute("effectiveTenant", null);
      String currencyCode = channelService.listCurrencies(channel.getParam()).get(0).getCurrencyCode();

      asAnonymous();
      List<ProductBundleRevision> bundleList = bundleController.listProductBundles(null, null,
          serviceInstanceDAO.find(1L).getUuid(), "VirtualMachine", "zone=122,Template=56,SO=10, hypervisor=kuchbhi",
          "PSI_UD1=10", true, "current", channel.getId().toString(), currencyCode, null, null, request);
      for (ProductBundleRevision pbr : bundleList) {
        if (bundle.getName().equals(pbr.getProductBundle().getName())) {
          found = true;
        }
      }
      Assert.assertTrue(found);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testListValidSubscriptionsForATenantAsRoot() {
    try {
      Tenant tenant = tenantDAO.find(3L);
      request.setAttribute("effectiveTenant", tenant);
      request.setAttribute("isSurrogatedTenant", true);
      ProductBundle bundle = bundleService.getProductBundleById(13L);

      Map<Long, ProductBundleRevision> subscriptionMap = bundleController.listValidSubscriptions(tenant,
          tenant.getParam(), serviceInstanceDAO.find(1L).getUuid(), "VirtualMachine",
          "zone=122,Template=200,SO=10, hypervisor=kuchbhi", "PSI_UD1=10", request);
      Assert.assertEquals(bundle.getName(), subscriptionMap.get(41L).getProductBundle().getName());

    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testGetBundleRevisionBySubscription() {
    try {
      Tenant tenant = tenantDAO.find(3L);
      request.setAttribute("effectiveTenant", tenant);
      request.setAttribute("isSurrogatedTenant", true);
      ProductBundle bundle = bundleService.getProductBundleById(13L);

      Map<String, Object> subscriptionMap = bundleController
          .getBundleRevisionBySubscription(tenant, "41", tenant.getParam(), serviceInstanceDAO.find(1L).getUuid(),
              "zone=122,Template=56,SO=10", "PSI_UD1=10", request);

      ProductBundleRevision productBundleRevision = (ProductBundleRevision) subscriptionMap.get("bundleRevision");
      Assert.assertEquals(bundle.getName(), productBundleRevision.getProductBundle().getName());

    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testShowBundles() {
    try {

      bundleController.showBundles(map, "current", "");
      Assert.assertEquals(getRootUser().getTenant(), map.get("tenant"));
      Assert.assertEquals(Page.PRODUCTS_BUNDLES, map.get("page"));
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testSearchByName() {
    try {
      ProductBundle bundle = bundleService.getProductBundleById(2L);
      String namePattern = bundle.getName().substring(0, 3);
      ServiceInstance instance = serviceInstanceDAO.find(1L);

      bundleController.searchByName(instance.getService().getUuid(), instance.getUuid(), "1", null, namePattern,
          "current", null, null, map);

      List<ProductBundle> bundleList = (List<ProductBundle>) map.get("productBundlesList");
      for (ProductBundle productbundle : bundleList) {
        Assert.assertEquals(namePattern, productbundle.getName().substring(0, 3));
      }
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testListProductBundlesByCatalog() {
    try {
      Boolean found = false;
      ProductBundle bundle = bundleService.getProductBundleById(2L);
      ProductBundle bundle2 = bundleService.getProductBundleById(4L);
      Channel channel = channelDAO.find(4L);

      bundleController.listProductBundlesByCatalog(channel.getCatalog().getId().toString(), map);
      Assert.assertEquals(Page.PRODUCTS_CATALOGS, map.get("page"));
      Assert.assertEquals(channel.getCatalog(), map.get("catalog"));

      List<ProductBundleRevision> productBundlesList = (List<ProductBundleRevision>) map.get("productBundlesList");
      for (ProductBundleRevision bundleRevision : productBundlesList) {
        if (bundleRevision.getProductBundle().getCode().equalsIgnoreCase(bundle.getCode())) {
          found = true;
        }
        if (bundleRevision.getProductBundle().getCode().equalsIgnoreCase(bundle2.getCode())) {
          Assert.fail("Bundle with id 4 is not attached to this catalog");
        }
      }
      Assert.assertTrue(found);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testGetServiceResourceComponents() {
    try {
      String[] componentArray = {
          "Template", "hypervisor", "SO"
      };
      ServiceResourceType resourceType = serviceResourceTypeDAO.find(1L);

      Map<String, List<String>> componentsMap = bundleController.getServiceResourceComponents(resourceType.getId());
      List<String> components = componentsMap.get("components");
      Assert.assertEquals(componentArray.length, components.size());

      Assert.assertEquals(Arrays.asList("zone"), componentsMap.get("filters"));
      for (int i = 0; i < componentArray.length; i++) {
        Assert.assertTrue(components.contains(componentArray[i]));
      }
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testGetServiceResourceComponentValues() {
    try {
      ServiceInstance instance = serviceInstanceDAO.find(1L);
      String componentName = "SO";

      Collection<ResourceComponent> componentValues = bundleController.getServiceResourceComponentValues(instance
          .getUuid().toString(), instance.getService().getServiceResourceTypes().get(0).getId(), componentName);
      for (ResourceComponent component : componentValues) {
        Assert.assertEquals("VirtualMachine", component.getResourceType());
        Assert.assertEquals("Template", component.getName());
        Assert.assertEquals("1", component.getValue());
      }
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testGetServiceResources() {
    try {
      ServiceInstance instance = serviceInstanceDAO.find(1L);
      List<ServiceResourceType> resourcesList = instance.getService().getServiceResourceTypes();

      Collection<ServiceResourceType> resourcesTypes = bundleController.getServiceResources(instance.getUuid()
          .toString());

      Assert.assertEquals(resourcesList.size(), resourcesTypes.size());

      for (ServiceResourceType resourceType : resourcesTypes) {
        Assert.assertTrue(resourcesList.contains(resourceType));
      }
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testGetAssociationTypes() {
    try {
      String[] associationList = {
          "INCLUDES", "EXCLUDES"
      };
      int index = 0;

      Collection<AssociationType> associationTypeList = bundleController.getAssociationTypes();

      for (AssociationType type : associationTypeList) {
        Assert.assertEquals(associationList[index], type.getName());
        index++;
      }
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testGetApplicableBusinessConstraints() {
    try {
      int index = 0;
      String[] constraintsArray = {
          "NONE", "PER_USER", "ACCOUNT", "SINGLETON"
      };

      ServiceInstance instance = serviceInstanceDAO.find(1L);
      ServiceResourceType resourceType = instance.getService().getServiceResourceTypes().get(0);

      Collection<ResourceConstraint> constraintsList = bundleController.getApplicableBusinessConstraints(resourceType
          .getId().toString());

      for (ResourceConstraint constraint : constraintsList) {
        Assert.assertEquals(constraintsArray[index], constraint.getName());
        index++;
      }
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testCreateProductBundle() {
    try {
      String[] frequencyArray = {
          "None", "Monthly", "Quarterly", "Annual"
      };
      ServiceInstance instance = serviceInstanceDAO.find(1L);
      ServiceResourceType resourceType = serviceResourceTypeDAO.find(1L);
      ServiceResourceTypeGroup group = resourceType.getServiceResourceGroups().get(0);

      bundleController.createProductBundle(instance.getUuid().toString(), map);

      ProductBundleForm productBundleForm = (ProductBundleForm) map.get("productBundleForm");
      List<ChargeRecurrenceFrequency> chargeFrequencyList = productBundleForm.getChargeRecurrenceFrequencyList();
      for (int i = 0; i < chargeFrequencyList.size(); i++) {
        Assert.assertEquals(frequencyArray[i], chargeFrequencyList.get(i).getDisplayName());
      }

      int size = currencyValueService.listActiveCurrencies().size();
      Assert.assertEquals(size, productBundleForm.getBundleOneTimeCharges().size());

      Assert.assertEquals(instance.getService().getServiceName(), map.get("serviceName"));

      Map<String, Set<String>> serviceResourceTypesAndComponentsMap = (Map<String, Set<String>>) map
          .get("serviceResourceTypesAndComponentsMap");
      for (ServiceResourceTypeGroupComponent component : group.getServiceResourceGroupComponents()) {
        Assert.assertTrue(serviceResourceTypesAndComponentsMap.get(resourceType.getResourceTypeName()).contains(
            component.getResourceComponentName()));
      }
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testViewProductBundle() {
    try {
      ProductBundle bundle = bundleService.getProductBundleById(2L);
      bundleController.viewProductBundle(bundle.getId().toString(), "current", map);
      Assert.assertEquals(bundle, map.get("productBundle"));
      Assert.assertEquals("current", map.get("whichPlan"));
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testEditProductBundleGet() {
    try {
      String[] frequencyArray = {
          "None", "Monthly", "Quarterly", "Annual"
      };
      ServiceInstance instance = serviceInstanceDAO.find(1L);
      ServiceResourceType resourceType = serviceResourceTypeDAO.find(1L);
      ServiceResourceTypeGroup group = resourceType.getServiceResourceGroups().get(0);

      ProductBundle bundle = bundleService.getProductBundleById(2L);

      bundleController.editProductBundle(bundle.getId().toString(), map);

      ProductBundleForm productBundleForm = (ProductBundleForm) map.get("productBundleForm");
      List<ChargeRecurrenceFrequency> chargeFrequencyList = productBundleForm.getChargeRecurrenceFrequencyList();
      for (int i = 0; i < chargeFrequencyList.size(); i++) {
        Assert.assertEquals(frequencyArray[i], chargeFrequencyList.get(i).getDisplayName());
      }

      Assert.assertEquals(instance.getService().getServiceName(), map.get("serviceName"));

      Map<String, Set<String>> serviceResourceTypesAndComponentsMap = (Map<String, Set<String>>) map
          .get("serviceResourceTypesAndComponentsMap");
      for (ServiceResourceTypeGroupComponent component : group.getServiceResourceGroupComponents()) {
        Assert.assertTrue(serviceResourceTypesAndComponentsMap.get(resourceType.getResourceTypeName()).contains(
            component.getResourceComponentName()));
      }

      String jsonProvisionalConstraints = (String) map.get("jsonProvisionalConstraints");

      ObjectMapper mapper = new ObjectMapper();
      ProvisioningConstraint[] array = mapper.readValue(jsonProvisionalConstraints, ProvisioningConstraint[].class);
      Assert.assertEquals("SO", array[0].getComponentName());
      Assert.assertEquals("INCLUDES", array[0].getAssociation().toString());
      Assert.assertEquals("10", array[0].getValue());

    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testEditBundleLogoGet() {
    try {
      ProductBundle bundle = bundleService.locateProductBundleById("2");
      bundleController.editBundleLogo(bundle.getId().toString(), map);
      Assert.assertEquals(Page.PRODUCTS_BUNDLES, map.get("page"));
      ProductBundleLogoForm bundleLogoForm = (ProductBundleLogoForm) map.get("bundleLogoForm");
      Assert.assertEquals(bundle, bundleLogoForm.getBundle());
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testEditBundleLogoPost() {
    try {
      ProductBundle bundle = bundleService.locateProductBundleById("2");
      ProductBundleLogoForm form = new ProductBundleLogoForm(bundle);
      MultipartFile logo = new MockMultipartFile("ProductBundle.jpeg", "ProductBundle.jpeg", "bytes",
          "ProductBundleLogo".getBytes());
      form.setLogo(logo);
      BindingResult result = validate(form);
      ResponseEntity<String> resultString = bundleController.editBundleLogo(form, result, request, map);
      Assert.assertNotNull(resultString);
      Assert.assertTrue(resultString.getBody().contains(
          "\"id\":" + bundle.getId() + ",\"name\":\"" + bundle.getName() + "\",\"description\":\""
              + bundle.getDescription() + "\""));
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testSortBundlesGet() {
    try {
      ServiceInstance instance = serviceInstanceDAO.find(1L);
      bundleController.sortBundles(instance.getUuid(), "planned", null, null, map);

      List<ProductBundle> bundlesList = (List<ProductBundle>) map.get("bundlesList");

      ProductBundle productBundle = bundleService.getProductBundleById(14L);
      Assert.assertEquals(productBundle, bundlesList.get(0));

      for (ProductBundle bundle : bundlesList) {
        Assert.assertTrue(bundle.getServiceInstanceId().getId().equals(instance.getId()));
      }
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testSortBundlesGetCurrent() {
    try {
      ServiceInstance instance = serviceInstanceDAO.find(1L);
      bundleController.sortBundles(instance.getUuid(), "current", null, null, map);

      ProductBundle productBundle = bundleService.getProductBundleById(14L);

      List<ProductBundle> bundlesList = (List<ProductBundle>) map.get("bundlesList");

      Assert.assertEquals(productBundle, bundlesList.get(0));

      for (ProductBundle bundle : bundlesList) {
        Assert.assertTrue(bundle.getServiceInstanceId().getId().equals(instance.getId()));
      }
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testSortBundlesGetHistory() {
    try {
      ServiceInstance instance = serviceInstanceDAO.find(1L);
      bundleController.sortBundles(instance.getUuid(), "history", "04/30/2012 00:00:00", null, map);

      ProductBundle productBundle = bundleService.getProductBundleById(2L);

      List<ProductBundle> bundlesList = (List<ProductBundle>) map.get("bundlesList");

      Assert.assertEquals(productBundle, bundlesList.get(0));

      for (ProductBundle bundle : bundlesList) {
        Assert.assertTrue(bundle.getServiceInstanceId().getId().equals(instance.getId()));
      }
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testViewBundleCurrentCharges() {
    try {
      ProductBundle bundle = bundleService.getProductBundleById(2L);

      bundleController.viewBundleCurrentCharges(bundle.getCode(), map, "3");

      RateCardChargesForm rateCardChargesForm = (RateCardChargesForm) map.get("rateCardChargesForm");

      List<RateCardComponentChargesForm> nonRecurringChargesForm = rateCardChargesForm
          .getNonRecurringRateCardChargesFormList();
      for (RateCardComponentChargesForm form : nonRecurringChargesForm) {
        Assert.assertEquals("13", form.getRcc().getId().toString());
        for (RateCardCharge charge : form.getCharges()) {
          if (charge.getCurrencyValue().getCurrencyCode().equalsIgnoreCase("EUR")) {
            Assert.assertEquals("100.0000", charge.getPrice().toString());
          }
        }
      }

      List<RateCardComponentChargesForm> recurringChargesForm = rateCardChargesForm
          .getRecurringRateCardChargesFormList();
      for (RateCardComponentChargesForm form : recurringChargesForm) {
        Assert.assertEquals("12", form.getRcc().getId().toString());
        for (RateCardCharge charge : form.getCharges()) {
          if (charge.getCurrencyValue().getCurrencyCode().equalsIgnoreCase("EUR")) {
            Assert.assertEquals("1000.0000", charge.getPrice().toString());
          }
        }
      }
      Assert.assertEquals(bundle, rateCardChargesForm.getBundle());
      Assert.assertEquals("3", map.get("currenciesToDisplay"));
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testViewBundleChannelPricing() {
    try {
      Boolean found = false;
      ProductBundle bundle = bundleService.getProductBundleById(2L);
      Channel channel = channelDAO.find(3L);

      bundleController.viewBundleChannelPricing(bundle.getCode(), "1", "2", "3", null, "current", null, map);
      Assert.assertEquals(bundle, map.get("productBundle"));
      Assert.assertEquals("True", map.get("enable_next"));

      Map<Channel, List<CurrencyValue>> channelCurrencyMap = (Map<Channel, List<CurrencyValue>>) map
          .get("channelCurrencyMap");

      List<CurrencyValue> currencyList = channelCurrencyMap.get(channel);
      for (CurrencyValue currency : currencyList) {
        if (currency.getCurrencyCode().equals("INR")) {
          found = true;
        }
      }
      Assert.assertTrue(found);

      Map<Channel, RateCardChargesForm> channelRateCardChargesFormMap = (Map<Channel, RateCardChargesForm>) map
          .get("channelRateCardChargesFormMap");
      RateCardChargesForm rateCardChargesForm = channelRateCardChargesFormMap.get(channel);

      List<RateCardComponentChargesForm> nonRecurringChargesForm = rateCardChargesForm
          .getNonRecurringRateCardChargesFormList();
      for (RateCardComponentChargesForm form : nonRecurringChargesForm) {
        Assert.assertEquals("13", form.getRcc().getId().toString());
        for (RateCardCharge charge : form.getCharges()) {
          if (charge.getCurrencyValue().getCurrencyCode().equalsIgnoreCase("USD")) {
            Assert.assertEquals("1500.0000", charge.getPrice().toString());
          }
        }
      }

      List<RateCardComponentChargesForm> recurringChargesForm = rateCardChargesForm
          .getRecurringRateCardChargesFormList();
      for (RateCardComponentChargesForm form : recurringChargesForm) {
        Assert.assertEquals("12", form.getRcc().getId().toString());
        for (RateCardCharge charge : form.getCharges()) {
          if (charge.getCurrencyValue().getCurrencyCode().equalsIgnoreCase("USD")) {
            System.err.println(form.getCharges());
            Assert.assertEquals("1000.0000", charge.getPrice().toString());
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testViewBundleChannelPricingPlanned() {
    try {
      Boolean found = false;
      ProductBundle bundle = bundleService.getProductBundleById(2L);
      Channel channel = channelDAO.find(3L);

      bundleController.viewBundleChannelPricing(bundle.getCode(), "1", "2", "3", null, "planned", null, map);
      Assert.assertEquals(bundle, map.get("productBundle"));
      Assert.assertEquals("True", map.get("enable_next"));

      Map<Channel, List<CurrencyValue>> channelCurrencyMap = (Map<Channel, List<CurrencyValue>>) map
          .get("channelCurrencyMap");

      List<CurrencyValue> currencyList = channelCurrencyMap.get(channel);
      for (CurrencyValue currency : currencyList) {
        if (currency.getCurrencyCode().equals("INR")) {
          found = true;
        }
      }
      Assert.assertTrue(found);

      Map<Channel, RateCardChargesForm> channelRateCardChargesFormMap = (Map<Channel, RateCardChargesForm>) map
          .get("channelRateCardChargesFormMap");
      RateCardChargesForm rateCardChargesForm = channelRateCardChargesFormMap.get(channel);

      List<RateCardComponentChargesForm> nonRecurringChargesForm = rateCardChargesForm
          .getNonRecurringRateCardChargesFormList();
      for (RateCardComponentChargesForm form : nonRecurringChargesForm) {
        Assert.assertEquals("13", form.getRcc().getId().toString());
        for (RateCardCharge charge : form.getCharges()) {
          if (charge.getCurrencyValue().getCurrencyCode().equalsIgnoreCase("USD")) {
            Assert.assertEquals("1500.0000", charge.getPrice().toString());
          }
        }
      }

      List<RateCardComponentChargesForm> recurringChargesForm = rateCardChargesForm
          .getRecurringRateCardChargesFormList();
      for (RateCardComponentChargesForm form : recurringChargesForm) {
        Assert.assertEquals("12", form.getRcc().getId().toString());
        for (RateCardCharge charge : form.getCharges()) {
          if (charge.getCurrencyValue().getCurrencyCode().equalsIgnoreCase("USD")) {
            Assert.assertEquals("1000.0000", charge.getPrice().toString());
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testViewBundleChannelPricingHistory() {
    try {
      Boolean found = false;
      ProductBundle bundle = bundleService.getProductBundleById(2L);
      Channel channel = channelDAO.find(3L);

      bundleController.viewBundleChannelPricing(bundle.getCode(), "1", "2", "3", null, "history",
          "04/30/2012 00:00:00", map);
      Assert.assertEquals(bundle, map.get("productBundle"));
      Assert.assertEquals("True", map.get("enable_next"));

      Map<Channel, List<CurrencyValue>> channelCurrencyMap = (Map<Channel, List<CurrencyValue>>) map
          .get("channelCurrencyMap");

      List<CurrencyValue> currencyList = channelCurrencyMap.get(channel);
      for (CurrencyValue currency : currencyList) {
        if (currency.getCurrencyCode().equals("INR")) {
          found = true;
        }
      }
      Assert.assertTrue(found);

      Map<Channel, RateCardChargesForm> channelRateCardChargesFormMap = (Map<Channel, RateCardChargesForm>) map
          .get("channelRateCardChargesFormMap");
      RateCardChargesForm rateCardChargesForm = channelRateCardChargesFormMap.get(channel);

      List<RateCardComponentChargesForm> nonRecurringChargesForm = rateCardChargesForm
          .getNonRecurringRateCardChargesFormList();
      for (RateCardComponentChargesForm form : nonRecurringChargesForm) {
        Assert.assertEquals("13", form.getRcc().getId().toString());
        for (RateCardCharge charge : form.getCharges()) {
          if (charge.getCurrencyValue().getCurrencyCode().equalsIgnoreCase("USD")) {
            Assert.assertEquals("1500.0000", charge.getPrice().toString());
          }
        }
      }

      List<RateCardComponentChargesForm> recurringChargesForm = rateCardChargesForm
          .getRecurringRateCardChargesFormList();
      for (RateCardComponentChargesForm form : recurringChargesForm) {
        Assert.assertEquals("12", form.getRcc().getId().toString());
        for (RateCardCharge charge : form.getCharges()) {
          if (charge.getCurrencyValue().getCurrencyCode().equalsIgnoreCase("USD")) {
            Assert.assertEquals("1000.0000", charge.getPrice().toString());
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testViewProvisioningConstraintsCurrent() {
    try {
      ProductBundle bundle = bundleService.getProductBundleById(2L);

      bundleController.viewProvisioningConstraints(bundle.getCode(), "current", null, map);
      Assert.assertEquals(bundle, map.get("productBundle"));

      List<ProvisioningConstraint> constrainstList = (List<ProvisioningConstraint>) map.get("constraints");

      for (ProvisioningConstraint constraint : constrainstList) {
        Assert.assertEquals("SO", constraint.getComponentName());
        Assert.assertEquals("INCLUDES", constraint.getAssociation().toString());
        Assert.assertEquals("10", constraint.getValue());
      }
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testViewProvisioningConstraintsPlanned() {
    try {
      ProductBundle bundle = bundleService.getProductBundleById(2L);

      bundleController.viewProvisioningConstraints(bundle.getCode(), "planned", null, map);
      Assert.assertEquals(bundle, map.get("productBundle"));

      List<ProvisioningConstraint> constrainstList = (List<ProvisioningConstraint>) map.get("constraints");

      for (ProvisioningConstraint constraint : constrainstList) {
        Assert.assertEquals("SO", constraint.getComponentName());
        Assert.assertEquals("INCLUDES", constraint.getAssociation().toString());
        Assert.assertEquals("10", constraint.getValue());
      }
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testViewProvisioningConstraintsHistory() {
    try {
      ProductBundle bundle = bundleService.getProductBundleById(2L);

      bundleController.viewProvisioningConstraints(bundle.getCode(), "history", "30/04/2012 00:00:00", map);
      Assert.assertEquals(bundle, map.get("productBundle"));

      List<ProvisioningConstraint> constrainstList = (List<ProvisioningConstraint>) map.get("constraints");

      for (ProvisioningConstraint constraint : constrainstList) {
        Assert.assertEquals("SO", constraint.getComponentName());
        Assert.assertEquals("INCLUDES", constraint.getAssociation().toString());
        Assert.assertEquals("10", constraint.getValue());
      }
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testViewPlannedCharges() {
    try {
      Boolean found = false;
      bundleController.viewPlannedCharges(map);

      RateCardForm rateCardForm = (RateCardForm) map.get("rateCardForm");
      Assert.assertEquals(channelService.getFutureRevision(null).getStartDate(), rateCardForm.getStartDate());

      List<RateCardChargesForm> chargeList = rateCardForm.getRateCardChargesList();
      for (RateCardChargesForm form : chargeList) {
        List<RateCardComponentChargesForm> nonRecurringChargesForm = form.getNonRecurringRateCardChargesFormList();
        for (RateCardComponentChargesForm nonRecurringCharges : nonRecurringChargesForm) {
          if (nonRecurringCharges.getRcc().getId().toString().equalsIgnoreCase("13")) {
            found = true;
          }
        }
      }
      Assert.assertTrue(found);
      found = false;
      for (RateCardChargesForm form : chargeList) {
        List<RateCardComponentChargesForm> recurringChargesForm = form.getRecurringRateCardChargesFormList();
        for (RateCardComponentChargesForm recurringCharges : recurringChargesForm) {
          if (recurringCharges.getRcc().getId().toString().equalsIgnoreCase("12")) {
            found = true;
          }
        }
      }
      Assert.assertTrue(found);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testEditPlannedCharges() {
    try {

      ProductBundle bundle = bundleService.getProductBundleById(2L);

      bundleController.editPlannedCharges(map);

      RateCardForm rateCardForm = (RateCardForm) map.get("rateCardForm");
      Assert.assertNotNull(rateCardForm.getStartDate());

      Map<ProductBundle, Map<RateCardComponent, List<RateCardCharge>>> plannedBundleChargesMap = rateCardForm
          .getCurrentBundleChargesMap();
      List<RateCardCharge> rccList = plannedBundleChargesMap.get(bundle).get(
          bundle.getRateCard().getRateCardComponents().get(0));

      for (RateCardCharge rateCardCharge : rccList) {
        Assert.assertEquals("12", rateCardCharge.getRateCardComponent().getId().toString());
      }

    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testViewBundleChargesHistoryData() {
    ProductBundle bundle = bundleService.getProductBundleById(2L);
    try {
      bundleController.viewBundleChargesHistoryData(bundle.getCode(), "04/30/2012 00:00:00", map, "3");

      RateCardChargesForm rateCardChargesForm = (RateCardChargesForm) map.get("rateCardChargesForm");
      Assert.assertEquals(bundle, rateCardChargesForm.getBundle());

      List<RateCardComponentChargesForm> nonReccuringList = rateCardChargesForm
          .getNonRecurringRateCardChargesFormList();
      for (RateCardComponentChargesForm form : nonReccuringList) {
        Assert.assertEquals("13", form.getRcc().getId().toString());
        for (RateCardCharge charge : form.getCharges()) {
          if (charge.getCurrencyValue().getCurrencyCode().equalsIgnoreCase("EUR")) {
            Assert.assertEquals("100.0000", charge.getPrice().toString());
          }
        }
      }

      List<RateCardComponentChargesForm> reccuringList = rateCardChargesForm.getRecurringRateCardChargesFormList();
      for (RateCardComponentChargesForm form : reccuringList) {
        Assert.assertEquals("12", form.getRcc().getId().toString());
        for (RateCardCharge charge : form.getCharges()) {
          if (charge.getCurrencyValue().getCurrencyCode().equalsIgnoreCase("EUR")) {
            Assert.assertEquals("1000.0000", charge.getPrice().toString());
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  /**
   * Description : To Test without schedule Activation, the Product Bunlde Should not Show for the channel to add
   * 
   * @author nageswarap
   */
  @Test
  public void testComputeBundleNotVisibleInChannelWithoutScheduleActivation() throws Exception {

    User user = userDAO.find("3");
    user.setProfile(profileDAO.find("7"));
    userDAO.save(user);
    asUser(user);

    ServiceResourceType resourceType = serviceResourceTypeDAO.find("1");
    String chargeFrequency = "MONTHLY";

    String compAssociationJson = "[{association: INCLUDES, compName: ISO, compValues:[ {compName: DOS-ISO, compValueName: 10 } ] }]";
    int noOfdays = 3;
    Calendar createdAt = Calendar.getInstance();
    createdAt.add(Calendar.DATE, 0 - noOfdays);
    int beforeBundleCount = bundleService.getBundlesCount();
    boolean trialEligible = false;

    Channel channel = channelService.getChannelById("1");

    // Create a Bundle
    ProductBundle obtainedBundle = testCreateProductBundle("1", resourceType.getId().toString(), chargeFrequency,
        chargeFrequency + "Compute", "USD", BigDecimal.valueOf(100), createdAt.getTime(), compAssociationJson,
        ResourceConstraint.PER_USER, trialEligible);
    Assert.assertNotNull("The bundle is null ", obtainedBundle);
    Assert.assertEquals("the expected bundle name and the actual bundle name is not matching", chargeFrequency
        + "Compute", obtainedBundle.getName());
    Assert.assertEquals("Bundle resource type is not matching", resourceType, obtainedBundle.getResourceType());
    Assert.assertEquals("Bundle resource constraints is not matching", ResourceConstraint.PER_USER,
        obtainedBundle.getBusinessConstraint());
    int afterBundleCount = bundleService.getBundlesCount();
    Assert.assertEquals("bundle count not incremented after creating the bundle", beforeBundleCount + 1,
        afterBundleCount);

    // Sync channel with reference price book
    String syncStatus = channelController.syncChannel(channel.getId().toString(), map);
    Assert.assertNotNull(" syncStatus is null ", syncStatus);
    Assert.assertEquals("sync status is success", "success", syncStatus);

    String bundleListResult = channelController.listbundles(channel.getId().toString(), map);
    Assert.assertNotNull("bundleListResult is null", bundleListResult);
    Assert.assertEquals("listing bundles failed in a channel to add", "productbundle.add", bundleListResult);

    // Verify the Bundle is not available for the channel to add
    List<ProductBundleRevision> bundlesToBeSent = (List<ProductBundleRevision>) map.get("productBundles");
    boolean bundleListingStatus = false;
    for (ProductBundleRevision pbr : bundlesToBeSent) {
      System.out.println(pbr.getProductBundle().getName());
      if (pbr.getProductBundle().getName().equalsIgnoreCase(obtainedBundle.getName())) {
        bundleListingStatus = true;
      }
    }

    Assert.assertFalse("Bundle is listed without schedule activation", bundleListingStatus);

  }

  /**
   * Description : To Test with schedule Activation, the Product Bundle Should Show for the channel to add
   * 
   * @author nageswarap
   */
  @Test
  public void testComputeBundleVisibleInChannelAfterScheduleActivation() throws Exception {

    User user = userDAO.find("3");
    user.setProfile(profileDAO.find("7"));
    userDAO.save(user);
    asUser(user);

    ServiceResourceType resourceType = serviceResourceTypeDAO.find("1");
    String chargeFrequency = "MONTHLY";

    String compAssociationJson = "[{association: INCLUDES, compName: ISO, compValues:[ {compName: DOS-ISO, compValueName: 10 } ] }]";
    int noOfdays = 3;
    Calendar createdAt = Calendar.getInstance();
    createdAt.add(Calendar.DATE, 0 - noOfdays);
    int beforeBundleCount = bundleService.getBundlesCount();
    boolean trialEligible = false;

    Channel channel = channelService.getChannelById("1");

    // Create a Bundle
    ProductBundle obtainedBundle = testCreateProductBundle("1", resourceType.getId().toString(), chargeFrequency,
        chargeFrequency + "Compute", "USD", BigDecimal.valueOf(100), createdAt.getTime(), compAssociationJson,
        ResourceConstraint.PER_USER, trialEligible);
    Assert.assertNotNull("The bundle is null ", obtainedBundle);
    Assert.assertEquals("the expected bundle name and the actual bundle name is not matching", chargeFrequency
        + "Compute", obtainedBundle.getName());
    Assert.assertEquals("Bundle resource type is not matching", resourceType, obtainedBundle.getResourceType());
    Assert.assertEquals("Bundle resource constraints is not matching", ResourceConstraint.PER_USER,
        obtainedBundle.getBusinessConstraint());
    int afterBundleCount = bundleService.getBundlesCount();
    Assert.assertEquals("bundle count not incremented after creating the bundle", beforeBundleCount + 1,
        afterBundleCount);

    // Schedule Activating the Bundle
    int noOfdays1 = 0;
    Calendar scheduleActivatedAt = Calendar.getInstance();
    scheduleActivatedAt.add(Calendar.DATE, 0 - noOfdays1);
    ProductForm postForm = new ProductForm();
    postForm.setStartDate(new Date());
    System.out.println("created at date" + scheduleActivatedAt.getTime().toString());
    BindingResult result = validate(postForm);
    productsController.setPlanDate(postForm, result, map);

    // Sync channel with reference price book
    String syncStatus = channelController.syncChannel(channel.getId().toString(), map);
    Assert.assertNotNull(" syncStatus is null ", syncStatus);
    Assert.assertEquals("sync status is success", "success", syncStatus);

    String bundleListResult = channelController.listbundles(channel.getId().toString(), map);
    Assert.assertNotNull("bundleListResult is null", bundleListResult);
    Assert.assertEquals("listing bundles failed in a channel to add", "productbundle.add", bundleListResult);

    // Verify the Bundle is not available for the channel to add
    List<ProductBundleRevision> bundlesToBeSent = (List<ProductBundleRevision>) map.get("productBundles");
    boolean bundleListingStatus = false;
    for (ProductBundleRevision pbr : bundlesToBeSent) {
      System.out.println(pbr.getProductBundle().getName());
      if (pbr.getProductBundle().getName().equalsIgnoreCase(obtainedBundle.getName())) {
        bundleListingStatus = true;
      }
    }

    Assert.assertTrue("Bundle not listed After schedule activation", bundleListingStatus);

  }

  /**
   * Description : Testing business constraint "Only at user level" (PER_USER) As Tenant
   * 
   * @author nageswarap
   */
  @Test
  public void testBundleBusinessConstraintPerUserAsMasterUser() throws Exception {

    Tenant tenant = tenantDAO.find("11");
    tenant.setState(State.ACTIVE);
    tenantDAO.save(tenant);

    ProductBundle bundle = bundleDAO.find("2");
    String bundleName = bundle.getName();
    bundle.setBusinessConstraint(ResourceConstraint.PER_USER);
    bundleDAO.save(bundle);

    // Get the Channel
    Channel channel = channelDAO.find("4");
    // Sync channel with reference price book
    String syncStatus = channelController.syncChannel(channel.getId().toString(), map);
    Assert.assertNotNull("syncChannel returned null", syncStatus);
    Assert.assertEquals("sync status is not successfull", "success", syncStatus);

    // logging in as Master User
    asUser(tenant.getOwner());

    request.setAttribute("effectiveTenant", tenant);
    String currencyCode = channelService.listCurrencies(channel.getParam()).get(0).getCurrencyCode();
    List<ProductBundleRevision> pbrlist = bundleController.listProductBundles(tenant, tenant.getParam(),
        serviceInstanceDAO.find(1L).getUuid(), "VirtualMachine", "zone=122,Template=56,SO=10, hypervisor=kuchbhi",
        "PSI_UD1=10", true, "current", channel.getId().toString(), currencyCode, null, null, request);

    Assert.assertNotNull("listProductBundles returned null ", pbrlist);

    // Verify the Bundle is not available for the channel to add
    boolean bundleListingStatus = false;
    for (ProductBundleRevision pbr : pbrlist) {
      if (pbr.getProductBundle().getName().equalsIgnoreCase(bundleName)) {
        bundleListingStatus = true;
      }
    }

    Assert.assertTrue("Bundle has not been listed for the master user before subscribing atleast once",
        bundleListingStatus);

    // subscribing the bundle
    Subscription expectedSubscription = subscriptionService.createSubscription(tenant.getOwner(), bundle,
        defaultServiceInstanceUuid, defaultResourceTypeName, false, true, proxySubscription,
        new HashMap<String, String>());

    Subscription subscription1 = subscriptionDAO.find(expectedSubscription.getId());

    Assert.assertNotNull("subscription not created and found returned null when queried from DAO", subscription1);
    Assert.assertEquals("subscription IDs not matched", expectedSubscription.getId(), subscription1.getId());

    List<ProductBundleRevision> upbrlist1 = bundleController.listProductBundles(tenant, tenant.getParam(),
        serviceInstanceDAO.find(1L).getUuid(), "VirtualMachine", "zone=122,Template=56,SO=10, hypervisor=kuchbhi",
        "PSI_UD1=10", true, "current", channel.getId().toString(), currencyCode, null, null, request);
    Assert.assertNotNull("listProductBundles returned null", upbrlist1);

    // Verify the Bundle is not available for the channel to add
    boolean bundleListingStatus2 = false;
    for (ProductBundleRevision pbr : upbrlist1) {
      if (pbr.getProductBundle().getName().equalsIgnoreCase(bundleName)) {
        bundleListingStatus2 = true;
      }
    }

    Assert.assertFalse("Bundle has been listed for master user after subscribing once", bundleListingStatus2);

  }

  /**
   * Description : Testing business constraint "Only at user level" (PER_USER) As NormalUser
   * 
   * @author nageswarap
   */
  @Test
  public void testBundleBusinessConstraintPerUserAsNormalUser() throws Exception {

    Tenant tenant = tenantDAO.find("11");
    tenant.setState(State.ACTIVE);
    tenantDAO.save(tenant);

    User createdUser = createTestUserInDB(tenant, true);

    ProductBundle bundle = bundleDAO.find("2");
    String bundleName = bundle.getName();
    bundle.setBusinessConstraint(ResourceConstraint.PER_USER);
    bundleDAO.save(bundle);

    // Get the Channel
    Channel channel = channelDAO.find("4");
    // Sync channel with reference price book
    String syncStatus = channelController.syncChannel(channel.getId().toString(), map);
    Assert.assertNotNull("syncChannel returned null", syncStatus);
    Assert.assertEquals("syncChannel not successfull", "success", syncStatus);

    String currencyCode = channelService.listCurrencies(channel.getParam()).get(0).getCurrencyCode();

    // Login as Normal user
    asUser(createdUser);

    // Listing the bundles before subscribing
    request.setAttribute("effectiveTenant", tenant);
    List<ProductBundleRevision> upbrlist = bundleController.listProductBundles(tenant, tenant.getParam(),
        serviceInstanceDAO.find(1L).getUuid(), "VirtualMachine", "zone=122,Template=56,SO=10, hypervisor=kuchbhi",
        "PSI_UD1=10", true, "current", channel.getId().toString(), currencyCode, null, null, request);
    Assert.assertNotNull("listProductBundles returned null", upbrlist);

    // Verify the Bundle is not available for the channel to add
    boolean bundleListingStatus1 = false;
    for (ProductBundleRevision pbr : upbrlist) {
      System.out.println(pbr.getProductBundle().getName());
      if (pbr.getProductBundle().getName().equalsIgnoreCase(bundleName)) {
        bundleListingStatus1 = true;
      }
    }

    Assert.assertTrue("Bundle has not been listed for the normal user before subscribing atleast once",
        bundleListingStatus1);

    // subscribing the bundle
    Subscription expectedSubscription = subscriptionService.createSubscription(createdUser, bundle,
        defaultServiceInstanceUuid, defaultResourceTypeName, false, true, proxySubscription,
        new HashMap<String, String>());

    Subscription subscription1 = subscriptionDAO.find(expectedSubscription.getId());

    Assert.assertNotNull("subscription not created and found returned null when queried from DAO", subscription1);
    Assert.assertEquals(expectedSubscription.getId(), subscription1.getId());

    // Listing the bundles after subscribing once
    List<ProductBundleRevision> upbrlist1 = bundleController.listProductBundles(tenant, tenant.getParam(),
        serviceInstanceDAO.find(1L).getUuid(), "VirtualMachine", "zone=122,Template=56,SO=10, hypervisor=kuchbhi",
        "PSI_UD1=10", true, "current", channel.getId().toString(), currencyCode, null, null, request);
    Assert.assertNotNull("listProductBundles returned null", upbrlist1);

    // Verify the Bundle is not available for the channel to add
    boolean bundleListingStatus2 = false;
    for (ProductBundleRevision pbr : upbrlist1) {
      System.out.println(pbr.getProductBundle().getName());
      if (pbr.getProductBundle().getName().equalsIgnoreCase(bundleName)) {
        bundleListingStatus2 = true;
      }
    }

    Assert.assertFalse("Bundle has been listed for normal user after subscribing once", bundleListingStatus2);

  }

  /**
   * Description : Testing business constraint "Only at account level; limit to 1 subscription per account" (SINGLETON)
   * As Master user
   * 
   * @author nageswarap
   */
  @Test
  public void testBundleBusinessConstraintSingletonAsMasterUser() throws Exception {

    Tenant tenant = tenantDAO.find("11");
    tenant.setState(State.ACTIVE);
    tenantDAO.save(tenant);

    ProductBundle bundle = bundleDAO.find("2");
    String bundleName = bundle.getName();
    bundle.setBusinessConstraint(ResourceConstraint.SINGLETON);
    bundleDAO.save(bundle);

    // Get the Channel
    Channel channel = channelDAO.find("4");
    // Sync channel with reference price book
    String syncStatus = channelController.syncChannel(channel.getId().toString(), map);
    Assert.assertNotNull("syncChannel returned null", syncStatus);
    Assert.assertEquals("syncChannel failed and not returned success", "success", syncStatus);

    // Checking as Master User
    asUser(tenant.getOwner());

    // ServiceInstance serviceInstance = serviceInstanceDAO.find("1");

    request.setAttribute("effectiveTenant", tenant);
    String currencyCode = channelService.listCurrencies(channel.getParam()).get(0).getCurrencyCode();
    List<ProductBundleRevision> pbrlist = bundleController.listProductBundles(tenant, tenant.getParam(),
        serviceInstanceDAO.find(1L).getUuid(), "VirtualMachine", "zone=122,Template=56,SO=10, hypervisor=kuchbhi",
        "PSI_UD1=10", true, "current", channel.getId().toString(), currencyCode, null, null, request);

    // Verify the Bundle is not available for the channel to add
    boolean bundleListingStatus = false;
    for (ProductBundleRevision pbr : pbrlist) {
      if (pbr.getProductBundle().getName().equalsIgnoreCase(bundleName)) {
        bundleListingStatus = true;
      }
    }

    Assert.assertTrue("Bundle has not been listed for the master user before subscribing atleast once",
        bundleListingStatus);

    Subscription expectedSubscription = subscriptionService.createSubscription(tenant.getOwner(), bundle,
        defaultServiceInstanceUuid, defaultResourceTypeName, false, true, proxySubscription,
        new HashMap<String, String>());

    Subscription subscription1 = subscriptionDAO.find(expectedSubscription.getId());

    Assert.assertNotNull("subscription not created and found returned null when queried from DAO", subscription1);
    Assert.assertEquals("subscription id's did not matched", expectedSubscription.getId(), subscription1.getId());

    List<ProductBundleRevision> upbrlist1 = bundleController.listProductBundles(tenant, tenant.getParam(),
        serviceInstanceDAO.find(1L).getUuid(), "VirtualMachine", "zone=122,Template=56,SO=10, hypervisor=kuchbhi",
        "PSI_UD1=10", true, "current", channel.getId().toString(), currencyCode, null, null, request);

    // Verify the Bundle is not available for the channel to add
    boolean bundleListingStatus2 = false;
    for (ProductBundleRevision pbr : upbrlist1) {
      if (pbr.getProductBundle().getName().equalsIgnoreCase(bundleName)) {
        bundleListingStatus2 = true;
      }
    }

    Assert.assertFalse("Bundle has been listed for the master user after subscribing once", bundleListingStatus2);

  }

  /**
   * Description : Testing business constraint "Only at account level; limit to 1 subscription per account" (SINGLETON)
   * As Normal user
   * 
   * @author nageswarap
   */
  @Test
  public void testBundleBusinessConstraintSingletonAsNormalUser() throws Exception {

    Tenant tenant = tenantDAO.find("11");
    tenant.setState(State.ACTIVE);
    tenantDAO.save(tenant);

    User createdUser = createTestUserInDB(tenant, true);

    ProductBundle bundle = bundleDAO.find("2");
    String bundleName = bundle.getName();
    bundle.setBusinessConstraint(ResourceConstraint.SINGLETON);
    bundleDAO.save(bundle);

    // Get the Channel
    Channel channel = channelDAO.find("4");
    // Sync channel with reference price book
    String syncStatus = channelController.syncChannel(channel.getId().toString(), map);
    Assert.assertNotNull("syncChannel returned null", syncStatus);
    Assert.assertEquals("syncChannel failed and not returned success", "success", syncStatus);

    String currencyCode = channelService.listCurrencies(channel.getParam()).get(0).getCurrencyCode();

    asUser(createdUser);

    // ServiceInstance serviceInstance = serviceInstanceDAO.find("1");
    request.setAttribute("effectiveTenant", tenant);
    List<ProductBundleRevision> upbrlist = bundleController.listProductBundles(tenant, tenant.getParam(),
        serviceInstanceDAO.find(1L).getUuid(), "VirtualMachine", "zone=122,Template=56,SO=10, hypervisor=kuchbhi",
        "PSI_UD1=10", true, "current", channel.getId().toString(), currencyCode, null, null, request);

    // Verify the Bundle is not available for the channel to add
    boolean bundleListingStatus1 = false;
    for (ProductBundleRevision pbr : upbrlist) {
      if (pbr.getProductBundle().getName().equalsIgnoreCase(bundleName)) {
        bundleListingStatus1 = true;
      }
    }

    Assert.assertFalse("Bundle has been listed for the normal user befor subscribing atleast once",
        bundleListingStatus1);

  }

  /**
   * Description : Testing business constraint "Only at account level; no limit on number of subscriptions" (ACCOUNT) As
   * Master user
   * 
   * @author nageswarap
   */
  @Test
  public void testBundleBusinessConstraintAccountAsMasterUser() throws Exception {

    Tenant tenant = tenantDAO.find("11");
    tenant.setState(State.ACTIVE);
    tenantDAO.save(tenant);

    ProductBundle bundle = bundleDAO.find("2");
    String bundleName = bundle.getName();
    bundle.setBusinessConstraint(ResourceConstraint.ACCOUNT);
    bundleDAO.save(bundle);

    // Get the Channel
    Channel channel = channelDAO.find("4");
    // Sync channel with reference price book
    String syncStatus = channelController.syncChannel(channel.getId().toString(), map);
    Assert.assertNotNull("syncChannel returned null", syncStatus);
    Assert.assertEquals("after doing syncChannel, the expected return value is success ", "success", syncStatus);

    // Checking as Master User
    asUser(tenant.getOwner());

    // ServiceInstance serviceInstance = serviceInstanceDAO.find("1");

    request.setAttribute("effectiveTenant", tenant);
    String currencyCode = channelService.listCurrencies(channel.getParam()).get(0).getCurrencyCode();
    List<ProductBundleRevision> pbrlist = bundleController.listProductBundles(tenant, tenant.getParam(),
        serviceInstanceDAO.find(1L).getUuid(), "VirtualMachine", "zone=122,Template=56,SO=10, hypervisor=kuchbhi",
        "PSI_UD1=10", true, "current", channel.getId().toString(), currencyCode, null, null, request);

    // Verify the Bundle is not available for the channel to add
    boolean bundleListingStatus = false;
    for (ProductBundleRevision pbr : pbrlist) {
      if (pbr.getProductBundle().getName().equalsIgnoreCase(bundleName)) {
        bundleListingStatus = true;
      }
    }

    Assert.assertTrue("Bundle has not been listed for the master user before subscribing subscribing atleast once",
        bundleListingStatus);

    Subscription expectedSubscription = subscriptionService.createSubscription(tenant.getOwner(), bundle,
        defaultServiceInstanceUuid, defaultResourceTypeName, false, true, proxySubscription,
        new HashMap<String, String>());

    Subscription subscription1 = subscriptionDAO.find(expectedSubscription.getId());

    Assert.assertNotNull("subscription not created and found returned null when queried from DAO", subscription1);
    Assert.assertEquals("Subscription IDs not matched", expectedSubscription.getId(), subscription1.getId());

    List<ProductBundleRevision> upbrlist1 = bundleController.listProductBundles(tenant, tenant.getParam(),
        serviceInstanceDAO.find(1L).getUuid(), "VirtualMachine", "zone=122,Template=56,SO=10, hypervisor=kuchbhi",
        "PSI_UD1=10", true, "current", channel.getId().toString(), currencyCode, null, null, request);

    // Verify the Bundle is not available for the channel to add
    boolean bundleListingStatus2 = false;
    for (ProductBundleRevision pbr : upbrlist1) {
      if (pbr.getProductBundle().getName().equalsIgnoreCase(bundleName)) {
        bundleListingStatus2 = true;
      }
    }

    Assert.assertTrue("Bundle has not been listed for the master user after subscribing once", bundleListingStatus2);

  }

  /**
   * Description : Testing business constraint "Only at account level; no limit on number of subscriptions" (ACCOUNT) As
   * Normal user
   * 
   * @author nageswarap
   */
  @Test
  public void testBundleBusinessConstraintAccountAsNormalUser() throws Exception {

    Tenant tenant = tenantDAO.find("11");
    tenant.setState(State.ACTIVE);
    tenantDAO.save(tenant);

    User createdUser = createTestUserInDB(tenant, true);

    ProductBundle bundle = bundleDAO.find("2");
    String bundleName = bundle.getName();
    bundle.setBusinessConstraint(ResourceConstraint.ACCOUNT);
    bundleDAO.save(bundle);

    // Get the Channel
    Channel channel = channelDAO.find("4");
    // Sync channel with reference price book
    String syncStatus = channelController.syncChannel(channel.getId().toString(), map);
    Assert.assertNotNull("syncChannel returned null", syncStatus);
    Assert.assertEquals("after doing syncChannel, the expected return value is success ", "success", syncStatus);

    String currencyCode = channelService.listCurrencies(channel.getParam()).get(0).getCurrencyCode();

    asUser(createdUser);

    // ServiceInstance serviceInstance = serviceInstanceDAO.find("1");
    request.setAttribute("effectiveTenant", tenant);
    List<ProductBundleRevision> upbrlist = bundleController.listProductBundles(tenant, tenant.getParam(),
        serviceInstanceDAO.find(1L).getUuid(), "VirtualMachine", "zone=122,Template=56,SO=10, hypervisor=kuchbhi",
        "PSI_UD1=10", true, "current", channel.getId().toString(), currencyCode, null, null, request);

    // Verify the Bundle is not available for the channel to add
    boolean bundleListingStatus1 = false;
    for (ProductBundleRevision pbr : upbrlist) {
      if (pbr.getProductBundle().getName().equalsIgnoreCase(bundleName)) {
        bundleListingStatus1 = true;
      }
    }

    Assert
        .assertFalse(
            "Bundle has been listed for the normal user before  subscribing atleast once with BusinessConstraint as ACCOUNT",
            bundleListingStatus1);

  }

  /**
   * Description : Testing business constraint "At any level; no limits on number of subscriptions"(NONE) As Master user
   * 
   * @author nageswarap
   */
  @Test
  public void testBundleBusinessConstraintNoneAsMasterUser() throws Exception {

    Tenant tenant = tenantDAO.find("11");
    tenant.setState(State.ACTIVE);
    tenantDAO.save(tenant);

    ProductBundle bundle = bundleDAO.find("2");
    String bundleName = bundle.getName();
    bundle.setBusinessConstraint(ResourceConstraint.NONE);
    bundleDAO.save(bundle);

    // Get the Channel
    Channel channel = channelDAO.find("4");
    // Sync channel with reference price book
    String syncStatus = channelController.syncChannel(channel.getId().toString(), map);
    Assert.assertNotNull("syncChannel returned null", syncStatus);
    Assert.assertEquals("after doing syncChannel, the expected return value is success ", "success", syncStatus);

    // Checking as Master User
    asUser(tenant.getOwner());

    // ServiceInstance serviceInstance = serviceInstanceDAO.find("1");

    request.setAttribute("effectiveTenant", tenant);
    String currencyCode = channelService.listCurrencies(channel.getParam()).get(0).getCurrencyCode();
    List<ProductBundleRevision> pbrlist = bundleController.listProductBundles(tenant, tenant.getParam(),
        serviceInstanceDAO.find(1L).getUuid(), "VirtualMachine", "zone=122,Template=56,SO=10, hypervisor=kuchbhi",
        "PSI_UD1=10", true, "current", channel.getId().toString(), currencyCode, null, null, request);

    // Verify the Bundle is not available for the channel to add
    boolean bundleListingStatus = false;
    for (ProductBundleRevision pbr : pbrlist) {
      if (pbr.getProductBundle().getName().equalsIgnoreCase(bundleName)) {
        bundleListingStatus = true;
      }
    }

    Assert.assertTrue("Bundle has been not listed for the master user before subscribing atleast once",
        bundleListingStatus);

    Subscription expectedSubscription = subscriptionService.createSubscription(tenant.getOwner(), bundle,
        defaultServiceInstanceUuid, defaultResourceTypeName, false, true, proxySubscription,
        new HashMap<String, String>());

    Subscription subscription1 = subscriptionDAO.find(expectedSubscription.getId());

    Assert.assertNotNull("subscription not created and found returned null when queried from DAO", subscription1);
    Assert.assertEquals("subscription id's did not matched", expectedSubscription.getId(), subscription1.getId());

    List<ProductBundleRevision> upbrlist1 = bundleController.listProductBundles(tenant, tenant.getParam(),
        serviceInstanceDAO.find(1L).getUuid(), "VirtualMachine", "zone=122,Template=56,SO=10, hypervisor=kuchbhi",
        "PSI_UD1=10", true, "current", channel.getId().toString(), currencyCode, null, null, request);

    // Verify the Bundle is not available for the channel to add
    boolean bundleListingStatus2 = false;
    for (ProductBundleRevision pbr : upbrlist1) {
      if (pbr.getProductBundle().getName().equalsIgnoreCase(bundleName)) {
        bundleListingStatus2 = true;
      }
    }

    Assert.assertTrue("Bundle has been not listed for the master user after subscribing once", bundleListingStatus2);

  }

  /**
   * Description : Testing business constraint "At any level; no limits on number of subscriptions"(NONE) As Normal user
   * 
   * @author nageswarap
   */
  @Test
  public void testBundleBusinessConstraintNoneAsNormalUser() throws Exception {

    Tenant tenant = tenantDAO.find("11");
    tenant.setState(State.ACTIVE);
    tenantDAO.save(tenant);

    User createdUser = createTestUserInDB(tenant, true);

    ProductBundle bundle = bundleDAO.find("2");
    String bundleName = bundle.getName();
    bundle.setBusinessConstraint(ResourceConstraint.NONE);
    bundleDAO.save(bundle);

    // Get the Channel
    Channel channel = channelDAO.find("4");
    // Sync channel with reference price book
    String syncStatus = channelController.syncChannel(channel.getId().toString(), map);
    Assert.assertNotNull("syncChannel returned null", syncStatus);
    Assert.assertEquals("after doing syncChannel, the expected return value is success ", "success", syncStatus);

    String currencyCode = channelService.listCurrencies(channel.getParam()).get(0).getCurrencyCode();

    asUser(createdUser);

    // ServiceInstance serviceInstance = serviceInstanceDAO.find("1");
    request.setAttribute("effectiveTenant", tenant);
    List<ProductBundleRevision> upbrlist = bundleController.listProductBundles(tenant, tenant.getParam(),
        serviceInstanceDAO.find(1L).getUuid(), "VirtualMachine", "zone=122,Template=56,SO=10, hypervisor=kuchbhi",
        "PSI_UD1=10", true, "current", channel.getId().toString(), currencyCode, null, null, request);

    // Verify the Bundle is not available for the channel to add
    boolean bundleListingStatus1 = false;
    for (ProductBundleRevision pbr : upbrlist) {
      if (pbr.getProductBundle().getName().equalsIgnoreCase(bundleName)) {
        bundleListingStatus1 = true;
      }
    }

    Assert.assertTrue("Bundle has been not listed for the master user before subscribing atleast once",
        bundleListingStatus1);

    Subscription expectedSubscription = subscriptionService.createSubscription(createdUser, bundle,
        defaultServiceInstanceUuid, defaultResourceTypeName, false, true, proxySubscription,
        new HashMap<String, String>());

    Subscription subscription1 = subscriptionDAO.find(expectedSubscription.getId());

    Assert.assertNotNull("subscription not created and found returned null when queried from DAO", subscription1);
    Assert.assertEquals("subscription id's did not matched", expectedSubscription.getId(), subscription1.getId());

    List<ProductBundleRevision> upbrlist1 = bundleController.listProductBundles(tenant, tenant.getParam(),
        serviceInstanceDAO.find(1L).getUuid(), "VirtualMachine", "zone=122,Template=56,SO=10, hypervisor=kuchbhi",
        "PSI_UD1=10", true, "current", channel.getId().toString(), currencyCode, null, null, request);

    // Verify the Bundle is not available for the channel to add
    boolean bundleListingStatus2 = false;
    for (ProductBundleRevision pbr : upbrlist1) {
      if (pbr.getProductBundle().getName().equalsIgnoreCase(bundleName)) {
        bundleListingStatus2 = true;
      }
    }

    Assert.assertTrue("Bundle has been not listed for the master user after subscribing once", bundleListingStatus2);

  }

  @Test
  public void testGetResourceComponentDetails() {

    String serviceInstUuid = "003fa8ee-fba3-467f-a517-fd806dae8a80";
    Long resourceTypeId = 1L;
    String resCompName = "Template";
    String resComVal = "1";
    try {
      Set<DisplayAttribute> displayAttrSet = new HashSet<DisplayAttribute>();
      displayAttrSet.add(new DisplayAttribute("zone", "Basic", 1));
      displayAttrSet.add(new DisplayAttribute("hypervisor", "Xenserver", 2));

      String actualDisplayAtt = bundleController.getResourceComponentDetails(serviceInstUuid, resourceTypeId,
          resCompName, resComVal);
      String expectedDisplayAtt = getDisplayAttributesString(displayAttrSet);

      Assert.assertNotNull(actualDisplayAtt);
      Assert.assertEquals(expectedDisplayAtt, actualDisplayAtt);

      resCompName = "zone";
      resComVal = "2";
      actualDisplayAtt = bundleController.getResourceComponentDetails(serviceInstUuid, resourceTypeId, resCompName,
          resComVal);
      Assert.assertNotNull(actualDisplayAtt);

      Set<DisplayAttribute> displayAttr = new HashSet<DisplayAttribute>();
      displayAttr.add(new DisplayAttribute("zone", "Advance", 1));
      displayAttr.add(new DisplayAttribute("hypervisor", "Hyper-V", 2));
      expectedDisplayAtt = getDisplayAttributesString(displayAttr);

      Assert.assertEquals(expectedDisplayAtt, actualDisplayAtt);

    } catch (Exception ex) {
      ex.printStackTrace();
    }

  }

  private String getDisplayAttributesString(Set<DisplayAttribute> displayAttributes) {
    String attributesString = "";
    if (displayAttributes != null) {
      for (DisplayAttribute da : displayAttributes) {
        attributesString += attributesString.equals("") ? "" : ", ";
        attributesString += da.getName() + " : " + da.getValue();
      }
    }
    return attributesString;
  }
}
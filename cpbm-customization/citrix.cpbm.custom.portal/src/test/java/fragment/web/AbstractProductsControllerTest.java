/*
 * Copyright © 2013 Citrix Systems, Inc. You may not use, copy, or modify this file except pursuant to a valid license
 * agreement from Citrix Systems, Inc.
 */
package fragment.web;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.codehaus.jettison.json.JSONException;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.ExpectedException;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import web.WebTestsBase;

import com.citrix.cpbm.platform.admin.service.ConnectorManagementService;
import com.citrix.cpbm.platform.spi.CloudConnector;
import com.citrix.cpbm.platform.spi.MetadataRegistry;
import com.citrix.cpbm.portal.fragment.controllers.ProductBundlesController;
import com.citrix.cpbm.portal.fragment.controllers.ProductsController;
import com.vmops.model.Category;
import com.vmops.model.Channel;
import com.vmops.model.Configuration;
import com.vmops.model.CurrencyValue;
import com.vmops.model.Entitlement;
import com.vmops.model.MediationRule;
import com.vmops.model.MediationRule.Operator;
import com.vmops.model.MediationRuleDiscriminator;
import com.vmops.model.Product;
import com.vmops.model.ProductBundle;
import com.vmops.model.ProductCharge;
import com.vmops.model.ProductRevision;
import com.vmops.model.Revision;
import com.vmops.model.Service;
import com.vmops.model.ServiceDiscriminator;
import com.vmops.model.ServiceInstance;
import com.vmops.model.ServiceUsageType;
import com.vmops.model.ServiceUsageTypeDiscriminator;
import com.vmops.model.ServiceUsageTypeUom;
import com.vmops.model.ServiceUsageTypeUomScale;
import com.vmops.model.User;
import com.vmops.persistence.EntitlementDAO;
import com.vmops.persistence.MediationRuleDAO;
import com.vmops.persistence.ProductBundleDAO;
import com.vmops.persistence.RevisionDAO;
import com.vmops.persistence.ServiceDAO;
import com.vmops.persistence.ServiceInstanceDao;
import com.vmops.persistence.ServiceUsageTypeDAO;
import com.vmops.portal.config.Configuration.Names;
import com.vmops.service.ChannelService;
import com.vmops.service.ConfigurationService;
import com.vmops.service.ProductBundleService;
import com.vmops.service.ProductService;
import com.vmops.service.exceptions.CurrencyPrecisionException;
import com.vmops.service.exceptions.ProductServicetException;
import com.vmops.utils.DateUtils;
import com.vmops.utils.JSONUtils;
import com.vmops.web.forms.EntitlementForm;
import com.vmops.web.forms.ProductForm;
import com.vmops.web.forms.ProductLogoForm;
import common.MockCloudInstance;

@SuppressWarnings({
    "unchecked", "deprecation"
})
public class AbstractProductsControllerTest extends WebTestsBase {

  @Autowired
  private ProductBundlesController bundleController;

  @Autowired
  private ProductBundleService bundleService;

  @Autowired
  private ServiceUsageTypeDAO serviceUsageTypeDAO;

  @Autowired
  private ProductService productService;

  @Autowired
  private ProductsController productsController;

  @Autowired
  private ProductService productsService;

  @Autowired
  private ServiceInstanceDao serviceInstanceDao;

  @Autowired
  private ChannelService channelService;

  @Autowired
  private EntitlementDAO entitlementDAO;

  @Autowired
  private ServiceInstanceDao serviceInstanceDAO;

  @Autowired
  private ServiceDAO serviceDAO;

  @Autowired
  private RevisionDAO revisionDAO;

  @Autowired
  private MediationRuleDAO mediationRuleDAO;

  @Autowired
  private ConfigurationService configurationService;

  @Autowired
  private ConnectorManagementService connectorManagementService;

  @Autowired
  private ProductBundleDAO productBundleDAO;

  private ModelMap map;

  private MockHttpServletResponse response;

  private MockHttpServletRequest request;

  @Before
  public void init() throws Exception {

    map = new ModelMap();
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
  }

  @Override
  public void prepareMock() {
    MockCloudInstance mock = this.getMockCloudInstance();
    CloudConnector connector = mock.getCloudConnector();
    MetadataRegistry metadataRegistry = mock.getMetadataRegistry();
    Map<String, String> discriminatorMap = new HashMap<String, String>();
    EasyMock.expect(metadataRegistry.getDiscriminatorValues(EasyMock.anyObject(String.class)))
        .andReturn(discriminatorMap).anyTimes();
    EasyMock.replay(connector, metadataRegistry);
  }

  /*
   * Description: Test to create a product with the set of mediation rules passed and returns the newly created product
   * Author: Vinayv
   */
  private Product productCreation(String productCode, boolean hasCharges, Long serviceUsageTypeID,
      Long serviceInstanceId, String conversionFactor, boolean hasDiscriminator, String discriminator,
      String discriminatorDisplayName) throws Exception {

    Product product = new Product("New", "New_Prod", "New_Prod", "", getRootUser());
    product.setCode(productCode);
    product.setServiceInstance(serviceInstanceDAO.find(serviceInstanceId));
    product.setUom("Hours");

    Category category = productsService.getAllCategories().get(0);
    product.setCategory(category);

    ProductForm form = new ProductForm(product);
    form.setStartDate(new Date());
    form.setServiceUUID(serviceDAO.find(6000L).getUuid());
    form.setServiceInstanceUUID(serviceInstanceDAO.find(1L).getUuid());
    form.setCategoryID(category.getId().toString());
    form.setConversionFactor("1");

    List<ProductCharge> productCharges = new ArrayList<ProductCharge>();
    if (hasCharges) {
      ProductCharge productCharge = new ProductCharge();
      productCharge.setRevision(revisionDAO.getCurrentRevision(null));
      productCharge.setCatalog(null);
      productCharge.setCurrencyValue(currencyValueDAO.find(1L));
      productCharge.setPrice(BigDecimal.valueOf(100));
      productCharge.setCreatedAt(new Date());
      productCharge.setCreatedBy(getRootUser());
      productCharge.setUpdatedBy(getRootUser());
      productsService.saveProductCharge(productCharge);
      productCharges.add(productCharge);
    }

    form.setProductCharges(productCharges);
    form.setIsNewProduct(true);

    ServiceInstance serviceInstance = serviceInstanceDAO.find(serviceInstanceId);
    Service service = serviceInstance.getService();
    Set<ServiceUsageTypeDiscriminator> sutdSet = service.getServiceUsageTypeDiscriminator();
    Long discriminatorId = 1L;
    for (ServiceUsageTypeDiscriminator sutd : sutdSet) {
      if (sutd.getDiscriminatorName().equalsIgnoreCase(discriminator)) {
        discriminatorId = sutd.getId();
        break;
      }
    }
    String jsonString = "{operator: COMBINE, usageTypeId: " + serviceUsageTypeID;
    if (hasDiscriminator) {
      jsonString = jsonString + ", discriminatorVals: [ {discriminatorId: " + discriminatorId
          + ", discriminatorValue: 125, operator: EQUAL_TO, discriminatorValueName: " + discriminatorDisplayName
          + " } ]";
    }
    jsonString = jsonString + " }";
    org.json.JSONObject mediationJason = new org.json.JSONObject(jsonString);
    String productMediationRules = "[" + mediationJason.toString() + "]";
    form.setProductMediationRules(productMediationRules);
    form.setConversionFactor(conversionFactor);
    BindingResult result = validate(form);
    Product obtainedProduct = productsController.createProduct(form, result, map, response, request);
    Assert.assertNotNull(obtainedProduct);
    Assert.assertEquals(obtainedProduct.getName(), product.getName());
    return obtainedProduct;
  }

  /*
   * Description: Test to create Discriminator under specific usage type Author: Vinay
   */
  private ServiceUsageType createDiscriminator(ServiceInstance serviceInstance, String usageType, String discriminator) {

    ServiceUsageType serviceUsageType = null;
    List<ServiceUsageType> sutlist = serviceInstance.getService().getServiceUsageTypes();
    for (ServiceUsageType sut : sutlist) {
      if (sut.getUsageTypeName().equalsIgnoreCase(usageType)) {
        serviceUsageType = sut;
        break;
      }
    }
    Service service = serviceInstance.getService();
    Set<ServiceUsageTypeDiscriminator> serviceUsageTypeDiscriminator = service.getServiceUsageTypeDiscriminator();
    ServiceUsageTypeDiscriminator e = new ServiceUsageTypeDiscriminator(discriminator, serviceUsageType);
    e.setTargetServiceId(service);
    serviceUsageTypeDiscriminator.add(e);
    serviceDAO.save(service);
    serviceDAO.flush();
    return serviceUsageType;
  }

  /*
   * Description: Test to create a product with Mediation Rule(Running_VM usage type) and
   * Discriminators(ServiceOfferingUUID, TemplateUUID, HypervisorType, RamSize, Speed, GuestOSName) as ProductManager
   * Author: Vinayv
   */
  @Test
  public void testCreatProductWithRunningVM() throws Exception {

    User user = userDAO.find("3");
    user.setProfile(profileDAO.find("7"));
    userDAO.save(user);
    asUser(user);
    String[] discriminators = {
        "ServiceOfferingUUID", "TemplateUUID", "HypervisorType", "RAMSize", "Speed", "GuestOSName"
    };
    for (int i = 0; i < discriminators.length; i++) {

      ServiceInstance serviceInstance = serviceInstanceDAO.find("1");
      ServiceUsageType serviceUsageType = createDiscriminator(serviceInstance, "RUNNING_VM", discriminators[i]);
      int beforeProductsCount = productsService.getProductsCount();
      Product obtainedProduct = productCreation("Code" + i, true, serviceUsageType.getId(), 1L, "1.00", true,
          discriminators[i], "Test_Discri" + i);
      Assert.assertNotNull(obtainedProduct);
      Assert.assertEquals(obtainedProduct.getName(), "New_Prod");
      int afterProductCount = productsService.getProductsCount();
      Assert.assertEquals(beforeProductsCount + 1, afterProductCount);
      List<ProductCharge> obtainedProductCharges = productsService.getProductCharges(obtainedProduct,
          obtainedProduct.getCreatedAt());
      for (ProductCharge pc : obtainedProductCharges) {
        Assert.assertEquals(BigDecimal.valueOf(100), pc.getPrice());
        Assert.assertEquals(currencyValueDAO.find(1L), pc.getCurrencyValue());
      }
      Map<ServiceUsageType, List<Product>> productUsageMap = productsService.getProductsByUsageType(revisionDAO
          .getCurrentRevision(null));
      List<Product> prodlist = productUsageMap.get(serviceUsageType);
      Assert.assertEquals(true, prodlist.contains(obtainedProduct));
      List<MediationRule> mrlist = mediationRuleDAO.findAllMediationRules(serviceUsageType, serviceInstance,
          revisionDAO.getCurrentRevision(null));
      for (MediationRule mr : mrlist) {
        if (mr.getProduct().equals(obtainedProduct)) {
          Set<MediationRuleDiscriminator> mrdset = mr.getMediationRuleDiscriminators();
          for (MediationRuleDiscriminator mrd : mrdset) {
            Assert.assertEquals("Test_Discri" + i, mrd.getDiscriminatorValueDisplayName());
          }
        }
      }
    }
  }

  /*
   * Description: Test to create a product with Mediation Rule(Allocated VM usage type) and
   * Discriminators(ServiceOfferingUUID, TemplateUUID, HypervisorType, RamSize, Speed, GuestOSName) as ProductManager
   * Author: Vinayv
   */
  @Test
  public void testCreatProductWithAllocatedVM() throws Exception {

    User user = userDAO.find("3");
    user.setProfile(profileDAO.find("7"));
    userDAO.save(user);
    asUser(user);
    String[] discriminators = {
        "ServiceOfferingUUID", "TemplateUUID", "HypervisorType", "RAMSize", "Speed", "GuestOSName"
    };
    for (int i = 0; i < discriminators.length; i++) {

      ServiceInstance serviceInstance = serviceInstanceDAO.find("1");
      ServiceUsageType serviceUsageType = createDiscriminator(serviceInstance, "ALLOCATED_VM", discriminators[i]);
      int beforeProductsCount = productsService.getProductsCount();
      Product obtainedProduct = productCreation("Code" + i, true, serviceUsageType.getId(), 1L, "1.00", true,
          discriminators[i], "Test_Discri" + i);
      Assert.assertNotNull(obtainedProduct);
      Assert.assertEquals(obtainedProduct.getName(), "New_Prod");
      int afterProductCount = productsService.getProductsCount();
      Assert.assertEquals(beforeProductsCount + 1, afterProductCount);
      List<ProductCharge> obtainedProductCharges = productsService.getProductCharges(obtainedProduct,
          obtainedProduct.getCreatedAt());
      for (ProductCharge pc : obtainedProductCharges) {
        Assert.assertEquals(BigDecimal.valueOf(100), pc.getPrice());
        Assert.assertEquals(currencyValueDAO.find(1L), pc.getCurrencyValue());
      }
      Map<ServiceUsageType, List<Product>> productUsageMap = productsService.getProductsByUsageType(revisionDAO
          .getCurrentRevision(null));
      List<Product> prodlist = productUsageMap.get(serviceUsageType);
      Assert.assertEquals(true, prodlist.contains(obtainedProduct));
      List<MediationRule> mrlist = mediationRuleDAO.findAllMediationRules(serviceUsageType, serviceInstance,
          revisionDAO.getCurrentRevision(null));
      for (MediationRule mr : mrlist) {
        if (mr.getProduct().equals(obtainedProduct)) {
          Set<MediationRuleDiscriminator> mrdset = mr.getMediationRuleDiscriminators();
          for (MediationRuleDiscriminator mrd : mrdset) {
            Assert.assertEquals("Test_Discri" + i, mrd.getDiscriminatorValueDisplayName());
          }
        }
      }
    }
  }

  /*
   * Description: Test to create a product with Mediation Rule(NETWORK_BYTES_SENT usage type) and
   * Discriminators(NetworkOfferingUUID, isRedundantRouterServiceEnabled, isVlanEnabled) as ProductManager Author:
   * Vinayv
   */
  @Test
  public void testCreatProductWithNetworkBytesSent() throws Exception {

    User user = userDAO.find("3");
    user.setProfile(profileDAO.find("7"));
    userDAO.save(user);
    asUser(user);
    String[] discriminators = {
        "NetworkOfferingUUID", "isRedundantRouterServiceEnabled", "isVlanEnabled"
    };
    for (int i = 0; i < discriminators.length; i++) {

      ServiceInstance serviceInstance = serviceInstanceDAO.find("1");
      ServiceUsageType serviceUsageType = createDiscriminator(serviceInstance, "NETWORK_BYTES_OUT", discriminators[i]);
      int beforeProductsCount = productsService.getProductsCount();
      Product obtainedProduct = productCreation("Code" + i, true, serviceUsageType.getId(), 1L, "1.00", true,
          discriminators[i], "Test_Discri" + i);
      Assert.assertNotNull(obtainedProduct);
      Assert.assertEquals(obtainedProduct.getName(), "New_Prod");
      int afterProductCount = productsService.getProductsCount();
      Assert.assertEquals(beforeProductsCount + 1, afterProductCount);
      List<ProductCharge> obtainedProductCharges = productsService.getProductCharges(obtainedProduct,
          obtainedProduct.getCreatedAt());
      for (ProductCharge pc : obtainedProductCharges) {
        Assert.assertEquals(BigDecimal.valueOf(100), pc.getPrice());
        Assert.assertEquals(currencyValueDAO.find(1L), pc.getCurrencyValue());
      }
      Map<ServiceUsageType, List<Product>> productUsageMap = productsService.getProductsByUsageType(revisionDAO
          .getCurrentRevision(null));
      List<Product> prodlist = productUsageMap.get(serviceUsageType);
      Assert.assertEquals(true, prodlist.contains(obtainedProduct));
      List<MediationRule> mrlist = mediationRuleDAO.findAllMediationRules(serviceUsageType, serviceInstance,
          revisionDAO.getCurrentRevision(null));
      for (MediationRule mr : mrlist) {
        if (mr.getProduct().equals(obtainedProduct)) {
          Set<MediationRuleDiscriminator> mrdset = mr.getMediationRuleDiscriminators();
          for (MediationRuleDiscriminator mrd : mrdset) {
            Assert.assertEquals("Test_Discri" + i, mrd.getDiscriminatorValueDisplayName());
          }
        }
      }
    }
  }

  /*
   * Description: Test to create a product with Mediation Rule(NETWORK_BYTES_RECEIVED usage type) and
   * Discriminators(NetworkOfferingUUID, isRedundantRouterServiceEnabled, isVlanEnabled) as ProductManager Author:
   * Vinayv
   */
  @Test
  public void testCreatProductWithNetworkBytesReceived() throws Exception {

    User user = userDAO.find("3");
    user.setProfile(profileDAO.find("7"));
    userDAO.save(user);
    asUser(user);
    String[] discriminators = {
        "NetworkOfferingUUID", "isRedundantRouterServiceEnabled", "isVlanEnabled"
    };
    for (int i = 0; i < discriminators.length; i++) {

      ServiceInstance serviceInstance = serviceInstanceDAO.find("1");
      ServiceUsageType serviceUsageType = createDiscriminator(serviceInstance, "NETWORK_BYTES_IN", discriminators[i]);
      int beforeProductsCount = productsService.getProductsCount();
      Product obtainedProduct = productCreation("Code" + i, true, serviceUsageType.getId(), 1L, "1.00", true,
          discriminators[i], "Test_Discri" + i);
      Assert.assertNotNull(obtainedProduct);
      Assert.assertEquals(obtainedProduct.getName(), "New_Prod");
      int afterProductCount = productsService.getProductsCount();
      Assert.assertEquals(beforeProductsCount + 1, afterProductCount);
      List<ProductCharge> obtainedProductCharges = productsService.getProductCharges(obtainedProduct,
          obtainedProduct.getCreatedAt());
      for (ProductCharge pc : obtainedProductCharges) {
        Assert.assertEquals(BigDecimal.valueOf(100), pc.getPrice());
        Assert.assertEquals(currencyValueDAO.find(1L), pc.getCurrencyValue());
      }
      Map<ServiceUsageType, List<Product>> productUsageMap = productsService.getProductsByUsageType(revisionDAO
          .getCurrentRevision(null));
      List<Product> prodlist = productUsageMap.get(serviceUsageType);
      Assert.assertEquals(true, prodlist.contains(obtainedProduct));
      List<MediationRule> mrlist = mediationRuleDAO.findAllMediationRules(serviceUsageType, serviceInstance,
          revisionDAO.getCurrentRevision(null));
      for (MediationRule mr : mrlist) {
        if (mr.getProduct().equals(obtainedProduct)) {
          Set<MediationRuleDiscriminator> mrdset = mr.getMediationRuleDiscriminators();
          for (MediationRuleDiscriminator mrd : mrdset) {
            Assert.assertEquals("Test_Discri" + i, mrd.getDiscriminatorValueDisplayName());
          }
        }
      }
    }
  }

  /*
   * Description: Test to create a product with Mediation Rule(VOLUME usage type) and Discriminators(diskOfferingUuid,
   * volumeType, poolType, size, volumeTags) as ProductManager Author: Vinayv
   */
  @Test
  public void testCreatProductWithVolume() throws Exception {

    User user = userDAO.find("3");
    user.setProfile(profileDAO.find("7"));
    userDAO.save(user);
    asUser(user);
    String[] discriminators = {
        "diskOfferingUuid", "volumeType", "poolType", "size", "volumeTags"
    };
    for (int i = 0; i < discriminators.length; i++) {

      ServiceInstance serviceInstance = serviceInstanceDAO.find("1");
      ServiceUsageType serviceUsageType = createDiscriminator(serviceInstance, "VOLUME", discriminators[i]);
      int beforeProductsCount = productsService.getProductsCount();
      Product obtainedProduct = productCreation("Code" + i, true, serviceUsageType.getId(), 1L, "1.00", true,
          discriminators[i], "Test_Discri" + i);
      Assert.assertNotNull(obtainedProduct);
      Assert.assertEquals(obtainedProduct.getName(), "New_Prod");
      int afterProductCount = productsService.getProductsCount();
      Assert.assertEquals(beforeProductsCount + 1, afterProductCount);
      List<ProductCharge> obtainedProductCharges = productsService.getProductCharges(obtainedProduct,
          obtainedProduct.getCreatedAt());
      for (ProductCharge pc : obtainedProductCharges) {
        Assert.assertEquals(BigDecimal.valueOf(100), pc.getPrice());
        Assert.assertEquals(currencyValueDAO.find(1L), pc.getCurrencyValue());
      }
      Map<ServiceUsageType, List<Product>> productUsageMap = productsService.getProductsByUsageType(revisionDAO
          .getCurrentRevision(null));
      List<Product> prodlist = productUsageMap.get(serviceUsageType);
      Assert.assertEquals(true, prodlist.contains(obtainedProduct));
      List<MediationRule> mrlist = mediationRuleDAO.findAllMediationRules(serviceUsageType, serviceInstance,
          revisionDAO.getCurrentRevision(null));
      for (MediationRule mr : mrlist) {
        if (mr.getProduct().equals(obtainedProduct)) {
          Set<MediationRuleDiscriminator> mrdset = mr.getMediationRuleDiscriminators();
          for (MediationRuleDiscriminator mrd : mrdset) {
            Assert.assertEquals("Test_Discri" + i, mrd.getDiscriminatorValueDisplayName());
          }
        }
      }
    }
  }

  /*
   * Description: Test to create a product with Mediation Rule(TEMPLATE usage type) and Discriminators(isPublic,
   * isFeatured, isSshKeyEnabled, isCrossZonesEnabled) as ProductManager Author: Vinayv
   */
  @Test
  public void testCreatProductWithTemplate() throws Exception {

    User user = userDAO.find("3");
    user.setProfile(profileDAO.find("7"));
    userDAO.save(user);
    asUser(user);
    String[] discriminators = {
        "isPublic", "isFeatured", "isSshKeyEnabled", "isCrossZonesEnabled"
    };
    for (int i = 0; i < discriminators.length; i++) {

      ServiceInstance serviceInstance = serviceInstanceDAO.find("1");
      ServiceUsageType serviceUsageType = createDiscriminator(serviceInstance, "TEMPLATE", discriminators[i]);
      int beforeProductsCount = productsService.getProductsCount();
      Product obtainedProduct = productCreation("Code" + i, true, serviceUsageType.getId(), 1L, "1.00", true,
          discriminators[i], "Test_Discri" + i);
      Assert.assertNotNull(obtainedProduct);
      Assert.assertEquals(obtainedProduct.getName(), "New_Prod");
      int afterProductCount = productsService.getProductsCount();
      Assert.assertEquals(beforeProductsCount + 1, afterProductCount);
      List<ProductCharge> obtainedProductCharges = productsService.getProductCharges(obtainedProduct,
          obtainedProduct.getCreatedAt());
      for (ProductCharge pc : obtainedProductCharges) {
        Assert.assertEquals(BigDecimal.valueOf(100), pc.getPrice());
        Assert.assertEquals(currencyValueDAO.find(1L), pc.getCurrencyValue());
      }
      Map<ServiceUsageType, List<Product>> productUsageMap = productsService.getProductsByUsageType(revisionDAO
          .getCurrentRevision(null));
      List<Product> prodlist = productUsageMap.get(serviceUsageType);
      Assert.assertEquals(true, prodlist.contains(obtainedProduct));
      List<MediationRule> mrlist = mediationRuleDAO.findAllMediationRules(serviceUsageType, serviceInstance,
          revisionDAO.getCurrentRevision(null));
      for (MediationRule mr : mrlist) {
        if (mr.getProduct().equals(obtainedProduct)) {
          Set<MediationRuleDiscriminator> mrdset = mr.getMediationRuleDiscriminators();
          for (MediationRuleDiscriminator mrd : mrdset) {
            Assert.assertEquals("Test_Discri" + i, mrd.getDiscriminatorValueDisplayName());
          }
        }
      }
    }
  }

  /*
   * Description: Test to create a product with Mediation Rule(SNAPSHOT usage type) and
   * Discriminator's(diskOfferingUuid, poolType, snapshotSize) as ProductManager Author: Vinayv
   */
  @Test
  public void testCreatProductWithSnapshot() throws Exception {

    User user = userDAO.find("3");
    user.setProfile(profileDAO.find("7"));
    userDAO.save(user);
    asUser(user);
    String[] discriminators = {
        "diskOfferingUuid", "poolType", "snapshotSize"
    };
    ServiceInstance serviceInstance = serviceInstanceDAO.find("1");
    Service service = serviceInstance.getService();
    ServiceUsageType sut = new ServiceUsageType(service, "SNAPSHOT",
        new ServiceUsageTypeUom("GB-Months", service, true));
    service.getServiceUsageTypes().add(sut);
    serviceDAO.save(service);
    serviceDAO.flush();
    for (int i = 0; i < discriminators.length; i++) {

      serviceInstance = serviceInstanceDAO.find("1");
      ServiceUsageType serviceUsageType = createDiscriminator(serviceInstance, "SNAPSHOT", discriminators[i]);
      int beforeProductsCount = productsService.getProductsCount();
      Product obtainedProduct = productCreation("Code" + i, true, serviceUsageType.getId(), 1L, "1.00", true,
          discriminators[i], "Test_Discri" + i);
      Assert.assertNotNull(obtainedProduct);
      Assert.assertEquals(obtainedProduct.getName(), "New_Prod");
      int afterProductCount = productsService.getProductsCount();
      Assert.assertEquals(beforeProductsCount + 1, afterProductCount);
      List<ProductCharge> obtainedProductCharges = productsService.getProductCharges(obtainedProduct,
          obtainedProduct.getCreatedAt());
      for (ProductCharge pc : obtainedProductCharges) {
        Assert.assertEquals(BigDecimal.valueOf(100), pc.getPrice());
        Assert.assertEquals(currencyValueDAO.find(1L), pc.getCurrencyValue());
      }
      Map<ServiceUsageType, List<Product>> productUsageMap = productsService.getProductsByUsageType(revisionDAO
          .getCurrentRevision(null));
      List<Product> prodlist = productUsageMap.get(serviceUsageType);
      Assert.assertEquals(true, prodlist.contains(obtainedProduct));
      List<MediationRule> mrlist = mediationRuleDAO.findAllMediationRules(serviceUsageType, serviceInstance,
          revisionDAO.getCurrentRevision(null));
      for (MediationRule mr : mrlist) {
        if (mr.getProduct().equals(obtainedProduct)) {
          Set<MediationRuleDiscriminator> mrdset = mr.getMediationRuleDiscriminators();
          for (MediationRuleDiscriminator mrd : mrdset) {
            Assert.assertEquals("Test_Discri" + i, mrd.getDiscriminatorValueDisplayName());
          }
        }
      }
    }
  }

  /*
   * Description: Test to create a product with Usage Types having no discriminator's (IP_ADDRESS, ISO, SECURITY_GROUP,
   * LOAD_BALANCER_POLICY, PORT_FORWARDING_RULE, VPN_USERS usage type having No discriminator's) as ProductManager
   * Author: Vinayv
   */
  @Test
  public void testCreatProductWithUsageTypesHavingNoDiscriminators() throws Exception {

    User user = userDAO.find("3");
    user.setProfile(profileDAO.find("7"));
    userDAO.save(user);
    asUser(user);
    String[] usageTypes = {
        "IP_ADDRESS", "ISO_STORAGE", "SECURITY_GROUP", "LOAD_BALANCER_POLICY", "PORT_FORWARDING_RULE", "VPN_USERS"
    };
    for (int i = 0; i < usageTypes.length; i++) {

      ServiceInstance serviceInstance = serviceInstanceDAO.find("1");
      Service service = serviceInstance.getService();
      ServiceUsageType serviceUsageType = new ServiceUsageType(service, usageTypes[i], new ServiceUsageTypeUom(
          "GB-Months", service, true));
      service.getServiceUsageTypes().add(serviceUsageType);
      serviceDAO.save(service);
      serviceDAO.flush();
      List<ServiceUsageType> sutlist = serviceInstance.getService().getServiceUsageTypes();
      for (ServiceUsageType sut : sutlist) {
        if (sut.getUsageTypeName().equalsIgnoreCase(usageTypes[i])) {
          serviceUsageType = sut;
          break;
        }
      }
      List<ServiceUsageTypeDiscriminator> sutdlist = serviceUsageType.getServiceUsageTypeDiscriminators();
      Assert.assertEquals(0, sutdlist.size());

      int beforeProductsCount = productsService.getProductsCount();
      Product obtainedProduct = productCreation("Code" + i, true, serviceUsageType.getId(), 1L, "1.00", false, "", "");
      Assert.assertNotNull(obtainedProduct);
      Assert.assertEquals(obtainedProduct.getName(), "New_Prod");
      int afterProductCount = productsService.getProductsCount();
      Assert.assertEquals(beforeProductsCount + 1, afterProductCount);
      List<ProductCharge> obtainedProductCharges = productsService.getProductCharges(obtainedProduct,
          obtainedProduct.getCreatedAt());
      for (ProductCharge pc : obtainedProductCharges) {
        Assert.assertEquals(BigDecimal.valueOf(100), pc.getPrice());
        Assert.assertEquals(currencyValueDAO.find(1L), pc.getCurrencyValue());
      }
      Map<ServiceUsageType, List<Product>> productUsageMap = productsService.getProductsByUsageType(revisionDAO
          .getCurrentRevision(null));
      List<Product> prodlist = productUsageMap.get(serviceUsageType);
      Assert.assertEquals(true, prodlist.contains(obtainedProduct));
      List<MediationRule> mrlist = mediationRuleDAO.findAllMediationRules(serviceUsageType, serviceInstance,
          revisionDAO.getCurrentRevision(null));
      for (MediationRule mr : mrlist) {
        if (mr.getProduct().equals(obtainedProduct)) {
          Set<MediationRuleDiscriminator> mrdset = mr.getMediationRuleDiscriminators();
          Assert.assertEquals(0, mrdset.size());
        }
      }
    }
  }

  /*
   * Description: Test to create a product with Multiple Usage Types as ProductManager Author: Vinayv
   */
  @Test
  public void testCreatProductWithMultipleUsageTypes() throws Exception {

    User user = userDAO.find("3");
    user.setProfile(profileDAO.find("7"));
    userDAO.save(user);
    asUser(user);
    String[] usageTypes = {
        "VOLUME", "TEMPLATE", "ISO", "SECURITY_GROUP", "VPN_USERS"
    };
    ServiceInstance serviceInstance = serviceInstanceDAO.find("1");
    Service service = serviceInstance.getService();
    ServiceUsageType serviceUsageType = null;
    for (int i = 0; i < usageTypes.length; i++) {

      serviceInstance = serviceInstanceDAO.find("1");
      service = serviceInstance.getService();
      serviceUsageType = new ServiceUsageType(service, usageTypes[i], new ServiceUsageTypeUom("GB-Months", service,
          true));
      service.getServiceUsageTypes().add(serviceUsageType);
      serviceDAO.save(service);
      serviceDAO.flush();
    }
    int beforeProductsCount = productsService.getProductsCount();
    Product product = new Product("New", "New_Prod", "New_Prod", "", getRootUser());
    product.setCode("New_Prod_Code");
    product.setServiceInstance(serviceInstanceDAO.find(serviceInstance.getId()));
    product.setUom("Hours");

    Category category = productsService.getCategory(1L);
    product.setCategory(category);

    ProductForm form = new ProductForm(product);
    form.setStartDate(new Date());
    form.setServiceUUID(service.getUuid());
    form.setServiceInstanceUUID(serviceInstanceDAO.find(1L).getUuid());
    form.setCategoryID(category.getId().toString());
    form.setConversionFactor("1");

    List<ProductCharge> productCharges = new ArrayList<ProductCharge>();
    ProductCharge productCharge = new ProductCharge();
    productCharge.setRevision(revisionDAO.getCurrentRevision(null));
    productCharge.setCatalog(null);
    productCharge.setCurrencyValue(currencyValueDAO.find(1L));
    productCharge.setPrice(BigDecimal.valueOf(100));
    productCharge.setCreatedAt(new Date());
    productCharge.setCreatedBy(getRootUser());
    productCharge.setUpdatedBy(getRootUser());
    productsService.saveProductCharge(productCharge);
    productCharges.add(productCharge);

    form.setProductCharges(productCharges);
    form.setIsNewProduct(true);

    String jsonString = "";
    String operator = "COMBINE";
    for (int i = 0; i < usageTypes.length; i++) {

      List<ServiceUsageType> sutlist = serviceInstance.getService().getServiceUsageTypes();
      for (ServiceUsageType sut : sutlist) {
        if (sut.getUsageTypeName().equalsIgnoreCase(usageTypes[i])) {
          serviceUsageType = sut;
          break;
        }
      }
      if (i == 2) {
        operator = "EXCLUDE";
      } else {
        operator = "COMBINE";
      }
      jsonString = "{conversionFactor: 1.00, operator: " + operator + ", usageTypeId: " + serviceUsageType.getId()
          + "}";
      jsonString = jsonString + ",";
    }
    jsonString = jsonString.substring(0, jsonString.length() - 1);
    String productMediationRules = "[" + jsonString + "]";
    form.setProductMediationRules(productMediationRules);
    BindingResult result = validate(form);
    Product obtainedProduct = productsController.createProduct(form, result, map, response, request);
    Assert.assertNotNull(obtainedProduct);
    Assert.assertEquals(obtainedProduct.getName(), product.getName());
    int afterProductCount = productsService.getProductsCount();
    Assert.assertEquals(beforeProductsCount + 1, afterProductCount);
    List<ProductCharge> obtainedProductCharges = productsService.getProductCharges(obtainedProduct,
        obtainedProduct.getCreatedAt());
    for (ProductCharge pc : obtainedProductCharges) {
      Assert.assertEquals(BigDecimal.valueOf(100), pc.getPrice());
      Assert.assertEquals(currencyValueDAO.find(1L), pc.getCurrencyValue());
    }
    Map<ServiceUsageType, List<Product>> productUsageMap = productsService.getProductsByUsageType(revisionDAO
        .getCurrentRevision(null));
    boolean cond = false;
    for (int i = 0; i < usageTypes.length; i++) {
      List<ServiceUsageType> sutlist = serviceInstance.getService().getServiceUsageTypes();
      for (ServiceUsageType sut : sutlist) {
        if (sut.getUsageTypeName().equalsIgnoreCase(usageTypes[i])) {
          serviceUsageType = sut;
          break;
        }
      }
      List<Product> prodlist = productUsageMap.get(serviceUsageType);
      if (prodlist != null && !prodlist.isEmpty() && prodlist.contains(obtainedProduct)) {
        cond = true;
      }
    }
    Assert.assertTrue(cond);
  }

  /*
   * Description: Test to create a product with Mediation Rule(Running_VM usage type) and
   * Discriminators(ServiceOfferingUUID, TemplateUUID) as Root Author: Vinayv
   */
  @Test
  public void testCreatProductAsRoot() throws Exception {

    String[] discriminators = {
        "ServiceOfferingUUID", "TemplateUUID"
    };
    for (int i = 0; i < discriminators.length; i++) {

      ServiceInstance serviceInstance = serviceInstanceDAO.find("1");
      ServiceUsageType serviceUsageType = createDiscriminator(serviceInstance, "RUNNING_VM", discriminators[i]);
      int beforeProductsCount = productsService.getProductsCount();
      Product obtainedProduct = productCreation("Code" + i, true, serviceUsageType.getId(), 1L, "1.00", true,
          discriminators[i], "Test_Discri" + i);
      Assert.assertNotNull(obtainedProduct);
      Assert.assertEquals(obtainedProduct.getName(), "New_Prod");
      int afterProductCount = productsService.getProductsCount();
      Assert.assertEquals(beforeProductsCount + 1, afterProductCount);
      List<ProductCharge> obtainedProductCharges = productsService.getProductCharges(obtainedProduct,
          obtainedProduct.getCreatedAt());
      for (ProductCharge pc : obtainedProductCharges) {
        Assert.assertEquals(BigDecimal.valueOf(100), pc.getPrice());
        Assert.assertEquals(currencyValueDAO.find(1L), pc.getCurrencyValue());
      }
      Map<ServiceUsageType, List<Product>> productUsageMap = productsService.getProductsByUsageType(revisionDAO
          .getCurrentRevision(null));
      List<Product> prodlist = productUsageMap.get(serviceUsageType);
      Assert.assertEquals(true, prodlist.contains(obtainedProduct));
      List<MediationRule> mrlist = mediationRuleDAO.findAllMediationRules(serviceUsageType, serviceInstance,
          revisionDAO.getCurrentRevision(null));
      for (MediationRule mr : mrlist) {
        if (mr.getProduct().equals(obtainedProduct)) {
          Set<MediationRuleDiscriminator> mrdset = mr.getMediationRuleDiscriminators();
          for (MediationRuleDiscriminator mrd : mrdset) {
            Assert.assertEquals("Test_Discri" + i, mrd.getDiscriminatorValueDisplayName());
          }
        }
      }
    }
  }

  /*
   * Description: Test to create a product with Product Name as Null Author: Vinayv
   */
  @Test
  public void testCreatProductWithNameAsNull() throws Exception {

    ServiceInstance serviceInstance = serviceInstanceDAO.find("1");
    Service service = serviceInstance.getService();
    ServiceUsageType serviceUsageType = null;
    List<ServiceUsageType> sutlist = serviceInstance.getService().getServiceUsageTypes();
    for (ServiceUsageType sut : sutlist) {
      if (sut.getUsageTypeName().equalsIgnoreCase("RUNNING_VM")) {
        serviceUsageType = sut;
        break;
      }
    }
    Product product = new Product("New", null, "New_Prod", "", getRootUser());
    product.setCode("New_Prod_Code");
    product.setServiceInstance(serviceInstanceDAO.find(serviceInstance.getId()));
    product.setUom("Hours");
    Category category = productsService.getCategory(1L);
    product.setCategory(category);

    ProductForm form = new ProductForm(product);
    form.setStartDate(new Date());
    form.setServiceUUID(service.getUuid());
    form.setServiceInstanceUUID(serviceInstanceDAO.find(1L).getUuid());
    form.setCategoryID(category.getId().toString());
    form.setConversionFactor("1.0");

    List<ProductCharge> productCharges = new ArrayList<ProductCharge>();
    ProductCharge productCharge = new ProductCharge();
    productCharge.setRevision(revisionDAO.getCurrentRevision(null));
    productCharge.setCatalog(null);
    productCharge.setCurrencyValue(currencyValueDAO.find(1L));
    productCharge.setPrice(BigDecimal.valueOf(100));
    productCharge.setCreatedAt(new Date());
    productCharge.setCreatedBy(getRootUser());
    productCharge.setUpdatedBy(getRootUser());
    productsService.saveProductCharge(productCharge);
    productCharges.add(productCharge);

    form.setProductCharges(productCharges);
    form.setIsNewProduct(true);

    String jsonString = "[ {conversionFactor: 1.00, operator: COMBINE, usageTypeId: " + serviceUsageType.getId()
        + "} ]";
    form.setProductMediationRules(jsonString);
    BindingResult result = validate(form);
    try {
      productsController.createProduct(form, result, map, response, request);
    } catch (Exception e) {
      Assert.assertTrue(e.getMessage().contains(
          "not-null property references a null or transient value: com.vmops.model.Product.name"));
    }
  }

  /*
   * Description: Test to create a product with Product Code as Null. Author: Vinayv
   */
  @Test(expected = Exception.class)
  public void testCreatProductWithCodeAsNull() throws Exception {

    ServiceInstance serviceInstance = serviceInstanceDAO.find("1");
    Service service = serviceInstance.getService();
    ServiceUsageType serviceUsageType = null;
    List<ServiceUsageType> sutlist = serviceInstance.getService().getServiceUsageTypes();
    for (ServiceUsageType sut : sutlist) {
      if (sut.getUsageTypeName().equalsIgnoreCase("RUNNING_VM")) {
        serviceUsageType = sut;
        break;
      }
    }
    Product product = new Product("New", "New_Prod", "New_Prod", "", getRootUser());
    product.setCode(null);
    product.setServiceInstance(serviceInstanceDAO.find(serviceInstance.getId()));
    product.setUom("Hours");

    ProductForm form = new ProductForm(product);
    form.setStartDate(new Date());
    form.setServiceUUID(service.getUuid());
    form.setServiceInstanceUUID(serviceInstanceDAO.find(1L).getUuid());

    List<ProductCharge> productCharges = new ArrayList<ProductCharge>();
    ProductCharge productCharge = new ProductCharge();
    productCharge.setRevision(revisionDAO.getCurrentRevision(null));
    productCharge.setCatalog(null);
    productCharge.setCurrencyValue(currencyValueDAO.find(1L));
    productCharge.setPrice(BigDecimal.valueOf(100));
    productCharge.setCreatedAt(new Date());
    productCharge.setCreatedBy(getRootUser());
    productCharge.setUpdatedBy(getRootUser());
    productsService.saveProductCharge(productCharge);
    productCharges.add(productCharge);

    form.setProductCharges(productCharges);
    form.setIsNewProduct(true);

    String jsonString = "[ {conversionFactor: 1.00, operator: COMBINE, usageTypeId: " + serviceUsageType.getId()
        + "} ]";
    form.setProductMediationRules(jsonString);
    BindingResult result = validate(form);
    productsController.createProduct(form, result, map, response, request);
  }

  /**
   * @Desc Test to check create product fails with null code, name by a product manager
   * @author vinayv
   */
  @Test
  public void testCreateProductNullCodeByProductManager() {
    logger.info("Entering testCreateProductNullCodeByProductManager test");
    User user = userDAO.find(8L);
    user.setProfile(profileDAO.findByName("Product Manager"));
    userDAO.merge(user);
    asUser(user);
    int beforeProductCount = productDAO.count();
    try {
      ServiceInstance serviceInstance = serviceInstanceDAO.find("1");
      Service service = serviceInstance.getService();
      ServiceUsageType serviceUsageType = null;
      List<ServiceUsageType> sutlist = serviceInstance.getService().getServiceUsageTypes();
      for (ServiceUsageType sut : sutlist) {
        if (sut.getUsageTypeName().equalsIgnoreCase("RUNNING_VM")) {
          serviceUsageType = sut;
          break;
        }
      }
      Product product = new Product("New", null, null, "", user);
      product.setCode(null);
      product.setServiceInstance(serviceInstanceDAO.find(serviceInstance.getId()));
      product.setUom("Hours");

      ProductForm form = new ProductForm(product);
      form.setStartDate(new Date());
      form.setServiceUUID(service.getUuid());
      form.setServiceInstanceUUID(serviceInstanceDAO.find(1L).getUuid());

      List<ProductCharge> productCharges = new ArrayList<ProductCharge>();
      ProductCharge productCharge = new ProductCharge();
      productCharge.setRevision(revisionDAO.getCurrentRevision(null));
      productCharge.setCatalog(null);
      productCharge.setCurrencyValue(currencyValueDAO.find(1L));
      productCharge.setPrice(BigDecimal.valueOf(100));
      productCharge.setCreatedAt(new Date());
      productCharge.setCreatedBy(getRootUser());
      productCharge.setUpdatedBy(getRootUser());
      productsService.saveProductCharge(productCharge);
      productCharges.add(productCharge);

      form.setProductCharges(productCharges);
      form.setIsNewProduct(true);

      String jsonString = "[ {conversionFactor: 1.00, operator: COMBINE, usageTypeId: " + serviceUsageType.getId()
          + "} ]";
      form.setProductMediationRules(jsonString);
      BindingResult result = validate(form);
      productsController.createProduct(form, result, map, response, request);
    } catch (Exception e) {
      Assert.assertTrue(e instanceof NullPointerException);
    }
    int afterProductCount = productDAO.count();
    Assert.assertEquals(beforeProductCount, afterProductCount);
    logger.info("Exiting testCreateProductNullCodeByProductManager test");

  }

  /*
   * Description: Test to create a product with Product Code containing special characters. Author: Vinayv
   */
  @Test(expected = Exception.class)
  public void testCreatProductWithCodeHavingSplCharacters() throws Exception {

    ServiceInstance serviceInstance = serviceInstanceDAO.find("1");
    Service service = serviceInstance.getService();
    ServiceUsageType serviceUsageType = null;
    List<ServiceUsageType> sutlist = serviceInstance.getService().getServiceUsageTypes();
    for (ServiceUsageType sut : sutlist) {
      if (sut.getUsageTypeName().equalsIgnoreCase("RUNNING_VM")) {
        serviceUsageType = sut;
        break;
      }
    }
    Product product = new Product("New", "New_Prod", "New_Prod", "", getRootUser());
    product.setCode("@Code<>");
    product.setServiceInstance(serviceInstanceDAO.find(serviceInstance.getId()));
    product.setUom("Hours");

    ProductForm form = new ProductForm(product);
    form.setStartDate(new Date());
    form.setServiceUUID(service.getUuid());
    form.setServiceInstanceUUID(serviceInstanceDAO.find(1L).getUuid());

    List<ProductCharge> productCharges = new ArrayList<ProductCharge>();
    ProductCharge productCharge = new ProductCharge();
    productCharge.setRevision(revisionDAO.getCurrentRevision(null));
    productCharge.setCatalog(null);
    productCharge.setCurrencyValue(currencyValueDAO.find(1L));
    productCharge.setPrice(BigDecimal.valueOf(100));
    productCharge.setCreatedAt(new Date());
    productCharge.setCreatedBy(getRootUser());
    productCharge.setUpdatedBy(getRootUser());
    productsService.saveProductCharge(productCharge);
    productCharges.add(productCharge);

    form.setProductCharges(productCharges);
    form.setIsNewProduct(true);

    String jsonString = "[ {conversionFactor: 1.00, operator: COMBINE, usageTypeId: " + serviceUsageType.getId()
        + "} ]";
    form.setProductMediationRules(jsonString);
    BindingResult result = validate(form);
    productsController.createProduct(form, result, map, response, request);
  }

  /*
   * Description: Test to create a product with Charges as Zero and having multiple mediations rules with Combine and
   * exclude aggregations. Author: Vinayv
   */
  @Test
  public void testCreatProductWithChargesAsZero() throws Exception {

    int expectedUsageTypesCount = 2;
    ServiceInstance serviceInstance = serviceInstanceDAO.find("1");
    Service service = serviceInstance.getService();
    ServiceUsageType runningVmServiceUsageType = null;
    ServiceUsageType allocatedVmServiceUsageType = null;
    List<ServiceUsageType> sutlist = serviceInstance.getService().getServiceUsageTypes();
    for (ServiceUsageType sut : sutlist) {
      if (sut.getUsageTypeName().equalsIgnoreCase("RUNNING_VM")) {
        runningVmServiceUsageType = sut;
        break;
      }
    }
    for (ServiceUsageType sut : sutlist) {
      if (sut.getUsageTypeName().equalsIgnoreCase("ALLOCATED_VM")) {
        allocatedVmServiceUsageType = sut;
        break;
      }
    }
    Product product = new Product("New", "New_Prod", "New_Prod", "", getRootUser());
    product.setCode("New_Prod_Code");
    product.setServiceInstance(serviceInstanceDAO.find(serviceInstance.getId()));
    product.setUom("Hours");
    Category category = productsService.getCategory(1L);
    product.setCategory(category);

    ProductForm form = new ProductForm(product);
    form.setStartDate(new Date());
    form.setServiceUUID(service.getUuid());
    form.setServiceInstanceUUID(serviceInstanceDAO.find(1L).getUuid());
    form.setCategoryID(category.getId().toString());
    form.setConversionFactor("1");

    List<ProductCharge> productCharges = new ArrayList<ProductCharge>();
    ProductCharge productCharge = new ProductCharge();
    productCharge.setRevision(revisionDAO.getCurrentRevision(null));
    productCharge.setCatalog(null);
    productCharge.setCurrencyValue(currencyValueDAO.find(1L));
    productCharge.setPrice(BigDecimal.valueOf(0.00));
    productCharge.setCreatedAt(new Date());
    productCharge.setCreatedBy(getRootUser());
    productCharge.setUpdatedBy(getRootUser());
    productsService.saveProductCharge(productCharge);
    productCharges.add(productCharge);

    form.setProductCharges(productCharges);
    form.setIsNewProduct(true);
    String jsonString = "[ {conversionFactor: 1.00, operator: COMBINE, usageTypeId: "
        + runningVmServiceUsageType.getId() + "}, {conversionFactor: 1.00, operator: EXCLUDE, usageTypeId: "
        + allocatedVmServiceUsageType.getId() + "} ]";
    form.setProductMediationRules(jsonString);
    BindingResult result = validate(form);
    Product obtainedProduct = productsController.createProduct(form, result, map, response, request);
    Assert.assertNotNull(obtainedProduct);
    List<ProductCharge> obtainedProductCharges = productsService.getProductCharges(obtainedProduct,
        obtainedProduct.getCreatedAt());
    for (ProductCharge pc : obtainedProductCharges) {
      Assert.assertEquals(BigDecimal.valueOf(0.00), pc.getPrice());
      Assert.assertEquals(currencyValueDAO.find(1L), pc.getCurrencyValue());
    }
    Map<String, Object> parameters = new HashMap<String, Object>();
    String query = "from MediationRule where product_id=" + obtainedProduct.getId();
    List<MediationRule> mediationRulesList = mediationRuleDAO.findByQuery(query, parameters, null);
    Assert.assertEquals("Expected usage type count is not matchign with the actual usage type count",
        expectedUsageTypesCount, mediationRulesList.size());
    for (int i = 0; i < mediationRulesList.size(); i++) {
      if (mediationRulesList.get(i).getServiceUsageType().getId().equals(runningVmServiceUsageType.getId())) {
        Assert.assertEquals(Operator.COMBINE, mediationRulesList.get(i).getOperator());
      }
      if (mediationRulesList.get(i).getServiceUsageType().getId().equals(allocatedVmServiceUsageType.getId())) {
        Assert.assertEquals(Operator.EXCLUDE, mediationRulesList.get(i).getOperator());
      }
    }
  }

  /**
   * @author vinayv
   * @desc Test to create a product with only exclude aggregations (Product creation should pass only if there is
   *       atleast 1 Combine aggregation)
   */

  @Test(expected = ProductServicetException.class)
  public void testCreateProductWithOnlyExcludeAggregation() throws Exception {

    ServiceInstance serviceInstance = serviceInstanceDAO.find("1");
    Service service = serviceInstance.getService();
    ServiceUsageType runningVmServiceUsageType = null;
    List<ServiceUsageType> sutlist = serviceInstance.getService().getServiceUsageTypes();
    for (ServiceUsageType sut : sutlist) {
      if (sut.getUsageTypeName().equalsIgnoreCase("RUNNING_VM")) {
        runningVmServiceUsageType = sut;
        break;
      }
    }
    Product product = new Product("New", "New_Prod", "New_Prod", "", getRootUser());
    product.setCode("New_Prod_Code");
    product.setServiceInstance(serviceInstanceDAO.find(serviceInstance.getId()));
    product.setUom("Hours");
    Category category = productsService.getCategory(1L);
    product.setCategory(category);

    ProductForm form = new ProductForm(product);
    form.setStartDate(new Date());
    form.setServiceUUID(service.getUuid());
    form.setServiceInstanceUUID(serviceInstanceDAO.find(1L).getUuid());
    form.setCategoryID(category.getId().toString());
    form.setConversionFactor("1");

    List<ProductCharge> productCharges = new ArrayList<ProductCharge>();
    ProductCharge productCharge = new ProductCharge();
    productCharge.setRevision(revisionDAO.getCurrentRevision(null));
    productCharge.setCatalog(null);
    productCharge.setCurrencyValue(currencyValueDAO.find(1L));
    productCharge.setPrice(BigDecimal.valueOf(0.00));
    productCharge.setCreatedAt(new Date());
    productCharge.setCreatedBy(getRootUser());
    productCharge.setUpdatedBy(getRootUser());
    productsService.saveProductCharge(productCharge);
    productCharges.add(productCharge);

    form.setProductCharges(productCharges);
    form.setIsNewProduct(true);
    String jsonString = "[ {operator: EXCLUDE, usageTypeId: " + runningVmServiceUsageType.getId() + "} ]";
    form.setProductMediationRules(jsonString);
    form.setConversionFactor("1.00");
    BindingResult result = validate(form);
    Product obtainedProduct = productsController.createProduct(form, result, map, response, request);
    Assert.assertNotNull(obtainedProduct);
    List<ProductCharge> obtainedProductCharges = productsService.getProductCharges(obtainedProduct,
        obtainedProduct.getCreatedAt());
    for (ProductCharge pc : obtainedProductCharges) {
      Assert.assertEquals(BigDecimal.valueOf(0.00), pc.getPrice());
      Assert.assertEquals(currencyValueDAO.find(1L), pc.getCurrencyValue());
    }
    Map<String, Object> parameters = new HashMap<String, Object>();
    String query = "from MediationRule where product_id=" + obtainedProduct.getId();
    List<MediationRule> mediationRulesList = mediationRuleDAO.findByQuery(query, parameters, null);
    Assert.assertEquals(1, mediationRulesList.size());
    for (int i = 0; i < mediationRulesList.size(); i++) {
      if (mediationRulesList.get(i).getServiceUsageType().getId().equals(runningVmServiceUsageType.getId())) {
        Assert.assertEquals(Operator.EXCLUDE, mediationRulesList.get(i).getOperator());
      }
    }
  }

  /*
   * Description: Test to create a product without specifying Usage Type. Author: Vinayv
   */
  @Test(expected = ProductServicetException.class)
  public void testCreatProductWithoutUsageTypes() throws Exception {

    ServiceInstance serviceInstance = serviceInstanceDAO.find("1");
    Service service = serviceInstance.getService();
    Product product = new Product("New", "New_Prod", "New_Prod", "", getRootUser());
    product.setCode("New_Prod_Code");
    product.setServiceInstance(serviceInstanceDAO.find(serviceInstance.getId()));
    product.setUom("Hours");
    Category category = productsService.getCategory(1L);
    product.setCategory(category);

    ProductForm form = new ProductForm(product);
    form.setStartDate(new Date());
    form.setServiceUUID(service.getUuid());
    form.setServiceInstanceUUID(serviceInstanceDAO.find(1L).getUuid());
    form.setCategoryID(category.getId().toString());
    form.setConversionFactor("1");

    List<ProductCharge> productCharges = new ArrayList<ProductCharge>();
    ProductCharge productCharge = new ProductCharge();
    productCharge.setRevision(revisionDAO.getCurrentRevision(null));
    productCharge.setCatalog(null);
    productCharge.setCurrencyValue(currencyValueDAO.find(1L));
    productCharge.setPrice(BigDecimal.valueOf(0.00));
    productCharge.setCreatedAt(new Date());
    productCharge.setCreatedBy(getRootUser());
    productCharge.setUpdatedBy(getRootUser());
    productsService.saveProductCharge(productCharge);
    productCharges.add(productCharge);
    form.setProductCharges(productCharges);
    form.setIsNewProduct(true);
    String jsonString = "[ { } ]";
    form.setProductMediationRules(jsonString);
    BindingResult result = validate(form);
    productsController.createProduct(form, result, map, response, request);
  }

  /*
   * Description: Test to add an image to a product. Author: Vinayv
   */
  @Test
  public void testEditProductLogoValid() throws Exception {

    Product product = productDAO.find(1L);
    ProductLogoForm form = new ProductLogoForm(product);
    MultipartFile logo = new MockMultipartFile("Product.jpeg", "Product.jpeg", "bytes", "ProductLogo".getBytes());
    form.setLogo(logo);
    BindingResult result = validate(form);
    ResponseEntity<String> resultString = productsController.editProductLogo(form, result, request, map);
    Assert.assertNotNull(resultString);
    String osName = System.getProperty("os.name");
    if (osName.startsWith("Windows")) {
      // The default config for saving product images in db is '/tmp'.
      // Since '/' is not for Windows
      // saving the product logo fails. So skipping this check for
      // Windows. Following assertion will
      // still happen on Hudson but will not fail on windows dev setup
      return;
    }

    Assert
        .assertTrue(resultString.getBody().contains(
            "\"id\":" + product.getId() + ",\"name\":\"" + product.getName() + "\",\"code\":\"" + product.getCode()
                + "\""));
  }

  /*
   * Description: Test to edit Product Logo form passing Invalid Logo. Author: Vinayv
   */
  @Test
  public void testEditProductLogoInValid() throws Exception {

    Product product = productDAO.find(1L);
    ProductLogoForm form = new ProductLogoForm(product);
    MultipartFile logo = new MockMultipartFile("Product.jpe", "Product.jpe", "bytes", "ProductLogo".getBytes());
    form.setLogo(logo);
    BindingResult result = validate(form);
    ResponseEntity<String> resultString = productsController.editProductLogo(form, result, request, map);
    Assert.assertNotNull(resultString);
    Assert.assertEquals("{\"errormessage\":\"File should have either .jpeg/.jpg/.png/.gif/.bmp extension\"}",
        resultString.getBody());
  }

  /*
   * Description: Test to change Product's sort Order. Author: Vinayv
   */
  @Test
  public void testChangeProductSortOrder() throws Exception {

    Product product1 = productDAO.find(1L);
    Product product2 = productDAO.find(2L);
    Long beforeSortP1 = product1.getSortOrder();
    Long beforeSortP2 = product2.getSortOrder();
    String productOrderData = product2.getId() + "," + product1.getId();
    String result = productsController.editproductsorder(productOrderData, map);
    Assert.assertNotNull(result);
    Assert.assertEquals("success", result);
    Long afterSortP1 = product1.getSortOrder();
    Long afterSortP2 = product2.getSortOrder();
    Assert.assertEquals(beforeSortP1, afterSortP2);
    Assert.assertEquals(beforeSortP2, afterSortP1);
  }

  /*
   * Description: Test to create a product and edit charges Author: Vinayv
   */
  @Test
  public void testEditProductCharges() throws Exception {

    Product product = productsService.locateProductById("1");
    String viewResult = productsController.viewProductPlannedCharges(product.getCode(), map);
    Assert.assertNotNull(viewResult);
    Assert.assertEquals("view.product.planned.charges", viewResult);
    List<ProductCharge> productChargesOriginal = (List<ProductCharge>) map.get("productChargesList");
    List<ProductCharge> productChargesEdited = (List<ProductCharge>) map.get("productChargesList");
    List<CurrencyValue> currenciesList = new ArrayList<CurrencyValue>();
    for (ProductCharge pc : productChargesEdited) {
      // pc.setPrice(BigDecimal.valueOf(500.00));
      pc.setPrice(BigDecimal.valueOf(500.00).setScale(4, BigDecimal.ROUND_HALF_UP));
      currenciesList.add(pc.getCurrencyValue());
    }
    ProductForm form = new ProductForm(product);
    Map<Product, List<ProductCharge>> currentProductChargesMap = new HashMap<Product, List<ProductCharge>>();
    currentProductChargesMap.put(product, productChargesOriginal);
    form.setCurrentProductChargesMap(currentProductChargesMap);
    form.updateProductChargesFormList(product, productChargesEdited, currenciesList,
        channelService.getFutureRevision(null), false);
    BindingResult result = validate(form);
    String editResult = productsController.editPlannedCharges(form, result, map);
    Assert.assertNotNull(editResult);
    Assert.assertEquals("success", editResult);

    String viewResult1 = productsController.viewProductPlannedCharges(product.getCode(), map);
    Assert.assertNotNull(viewResult1);
    Assert.assertEquals("view.product.planned.charges", viewResult1);
    List<ProductCharge> productChargesafter = (List<ProductCharge>) map.get("productChargesList");
    for (ProductCharge pc : productChargesafter) {
      Assert.assertEquals(BigDecimal.valueOf(500.00).setScale(4, BigDecimal.ROUND_HALF_UP), pc.getPrice());
    }
  }

  /*
   * Description: Test to View Product Planned charges for future dates Author: Vinayv
   */
  @Test
  public void testViewProductPlannedChargesForFuture() throws Exception {

    Product product = productsService.locateProductById("1");
    List<ProductCharge> productChargesList = productsService.getProductCharges(product, channelService
        .getFutureRevision(null).getStartDate());
    String viewResult = productsController.viewProductPlannedCharges(product.getCode(), map);
    Assert.assertNotNull(viewResult);
    Assert.assertEquals("view.product.planned.charges", viewResult);
    List<ProductCharge> productCharges = (List<ProductCharge>) map.get("productChargesList");
    Assert.assertEquals(productChargesList.size(), productCharges.size());
  }

  /*
   * Description: Test to edit Product with duplicate code Author: Vinayv
   * @Test public void testEditProduct() throws Exception{ ServiceInstance serviceInstance =
   * serviceInstanceDAO.find("1"); Service service = serviceInstance.getService(); ServiceUsageType serviceUsageType =
   * null; List<ServiceUsageType> sutlist = serviceInstance.getService().getServiceUsageTypes(); for(ServiceUsageType
   * sut : sutlist){ if(sut.getUsageTypeName().equalsIgnoreCase("RUNNING_VM")){ serviceUsageType = sut; break; } }
   * Product product = productsService.locateProductById("1");
   * product.setCode(productsService.locateProductById("2").getCode());
   * System.out.println("CODE1----------------"+product.getCode()); ProductForm form = new ProductForm(product); String
   * jsonString ="[ {conversionFactor: 1.00, operator: COMBINE, usageTypeId: " +serviceUsageType.getId()+"} ]";
   * form.setProductMediationRules(jsonString); BindingResult result = validate(form);
   * System.out.println("CODE----------------"+form.getProduct().getCode()); Product editedProduct =
   * productsController.editProduct(form, result, map); }
   */

  /*
   * Description: Test to search Product Author: Vinayv
   */
  @Test
  public void testSearchProduct() throws Exception {

    ServiceInstance serviceInstance = serviceInstanceDAO.find("1");
    String productName = productsService.locateProductById("1").getName();
    String result = productsController.searchListByName(serviceInstance.getUuid(), "1", "100", productName, "current",
        new Date().toString(), "All", "All", map);
    Assert.assertNotNull(result);
    Assert.assertEquals("products.search.list", result);
    List<Product> obtainedProductList = (List<Product>) map.get("productsList");
    boolean flag = false;
    for (Product p : obtainedProductList) {
      if ((p.getName().equalsIgnoreCase(productName)) && (p.getId() == 1L)) {
        flag = true;
        break;
      }
    }
    Assert.assertEquals(true, flag);

    productName = productsService.locateProductById("1").getName();
    result = productsController.searchListByName(serviceInstance.getUuid(), "1", "100", productName.substring(0, 3),
        "current", new Date().toString(), "All", "All", map);
    Assert.assertNotNull(result);
    Assert.assertEquals("products.search.list", result);
    obtainedProductList = (List<Product>) map.get("productsList");
    flag = false;
    for (Product p : obtainedProductList) {
      if ((p.getName().equalsIgnoreCase(productName)) && (p.getId() == 1L)) {
        flag = true;
        break;
      }
    }
    Assert.assertEquals(true, flag);
  }

  /*
   * @author Abhaik
   * @description : Verify Products filter "All" link and select Category is "All" for view current tab
   */
  @Test
  public void testAllFilterWithAllCategoryInCurrentViewForProducts() {

    int count = 0;
    boolean hasNext = true;
    int page = 1;
    while (hasNext) {
      String result = productsController.listProducts("003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page),
          null, null, "current", null, "All", "All", map);
      Assert.assertNotNull(result);
      Assert.assertEquals("products.list", result);
      List<Product> productList = (List<Product>) map.get("productsList");
      count = count + productList.size();
      hasNext = (Boolean) map.get("enableNext");
      page = page + 1;
    }
    Assert.assertEquals(35, count);
  }

  /*
   * @author Abhaik
   * @description : Verify Products filter "All" link and select Category value for view current tab
   */

  @Test
  public void testAllFilterWithSelectedCategoryInCurrentViewForProducts() {

    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = productsController.listProducts("003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page),
          null, null, "current", null, "All", "Categorized1", map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("products.list", result);
      List<Product> productList = (List<Product>) map1.get("productsList");
      count = count + productList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    Assert.assertEquals(2, count);
  }

  /*
   * @author Abhaik
   * @description : Verify Products filter "All" link and select Category is "Null" for view current tab
   */

  @Test
  public void testAllFilterWithNullCategoryInCurrentViewForProducts() {

    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = productsController.listProducts("003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page),
          null, null, "current", null, "All", null, map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("products.list", result);
      List<Product> productList = (List<Product>) map1.get("productsList");
      count = count + productList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    Assert.assertEquals(35, count);
  }

  /*
   * @author Abhaik
   * @description : Verify Products filter "All" link and select Category is "Empty" for view current tab
   */

  @Test
  public void testAllFilterWithEmptyCategoryInCurrentViewForProducts() {

    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = productsController.listProducts("003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page),
          null, null, "current", null, "All", "", map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("products.list", result);
      List<Product> productList = (List<Product>) map1.get("productsList");
      count = count + productList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    Assert.assertEquals(35, count);
  }

  /*
   * @author Abhaik
   * @description : Verify Products filter "All" link and select Category is Invalid for view current tab
   */

  @Test
  public void testAllFilterWithInvalidCategoryInCurrentViewForProducts() {

    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = productsController.listProducts("003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page),
          null, null, "current", null, "All", "Invalid", map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("products.list", result);
      List<Product> productList = (List<Product>) map1.get("productsList");
      count = count + productList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    Assert.assertEquals(0, count);
  }

  /*
   * @author Abhaik
   * @description : Verify Products filter Null link and select Category is Null for view current tab
   */

  @Test
  public void testNullFilterWithInvalidCategoryInCurrentViewForProducts() {

    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = productsController.listProducts("003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page),
          null, null, "current", null, null, null, map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("products.list", result);
      List<Product> productList = (List<Product>) map1.get("productsList");
      count = count + productList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    Assert.assertEquals(35, count);
  }

  /*
   * @author Abhaik
   * @description : Verify Products filter "All" link and select Category is "All" for view History tab
   */

  @Test
  public void testAllFilterWithAllCategoryInHistoryViewForProducts() {

    int count = 0;
    boolean hasNext = true;
    int page = 1;
    while (hasNext) {
      String result = productsController.listProducts("003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page),
          null, null, "history", null, "All", "All", map);
      Assert.assertNotNull(result);
      Assert.assertEquals("products.list", result);
      List<Product> productList = (List<Product>) map.get("productsList");
      count = count + productList.size();
      hasNext = (Boolean) map.get("enableNext");
      page = page + 1;
    }
    Assert.assertEquals(35, count);
  }

  /*
   * @author Abhaik
   * @description : Verify Products filter "All" link and select Category value for view History tab
   */

  @Test
  public void testAllFilterWithSelectedCategoryInHistoryViewForProducts() {

    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = productsController.listProducts("003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page),
          null, null, "history", null, "All", "Categorized1", map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("products.list", result);
      List<Product> productList = (List<Product>) map1.get("productsList");
      count = count + productList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    Assert.assertEquals(2, count);
  }

  /*
   * @author Abhaik
   * @description : Verify Products filter "All" link and select Category is "Null" for view History tab
   */

  @Test
  public void testAllFilterWithNullCategoryInHistoryViewForProducts() {

    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = productsController.listProducts("003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page),
          null, null, "history", null, "All", null, map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("products.list", result);
      List<Product> productList = (List<Product>) map1.get("productsList");
      count = count + productList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    Assert.assertEquals(35, count);
  }

  /*
   * @author Abhaik
   * @description : Verify Products filter "All" link and select Category is "Empty" for view History tab
   */

  @Test
  public void testAllFilterWithEmptyCategoryInHistoryViewForProducts() {

    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = productsController.listProducts("003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page),
          null, null, "history", null, "All", "", map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("products.list", result);
      List<Product> productList = (List<Product>) map1.get("productsList");
      count = count + productList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    Assert.assertEquals(35, count);
  }

  /*
   * @author Abhaik
   * @description : Verify Products filter "All" link and select Category is Invalid for view History tab
   */

  @Test
  public void testAllFilterWithInvalidCategoryInHistoryViewForProducts() {

    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = productsController.listProducts("003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page),
          null, null, "history", null, "All", "Invalid", map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("products.list", result);
      List<Product> productList = (List<Product>) map1.get("productsList");
      count = count + productList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    Assert.assertEquals(0, count);
  }

  /*
   * @author Abhaik
   * @description : Verify Products filter "All" link and select Category is Invalid for view History tab
   */

  @Test
  public void testNullFilterWithInvalidCategoryInHistoryViewForProducts() {

    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = productsController.listProducts("003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page),
          null, null, "history", null, null, null, map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("products.list", result);
      List<Product> productList = (List<Product>) map1.get("productsList");
      count = count + productList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    Assert.assertEquals(35, count);
  }

  /*
   * @author Abhaik
   * @description : Verify Products filter "All" link and select Category is "All" for view planned tab
   */

  @Test
  public void testAllFilterWithAllCategoryInPlanNextViewForProducts() {

    int count = 0;
    boolean hasNext = true;
    int page = 1;
    while (hasNext) {
      String result = productsController.listProducts("003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page),
          null, null, "planned", null, "All", "All", map);
      Assert.assertNotNull(result);
      Assert.assertEquals("products.list", result);
      List<Product> productList = (List<Product>) map.get("productsList");
      count = count + productList.size();
      hasNext = (Boolean) map.get("enableNext");
      page = page + 1;
    }
    Assert.assertEquals(35, count);
  }

  /*
   * @author Abhaik
   * @description : Verify Products filter "All" link and select Category value for view planned tab
   */

  @Test
  public void testAllFilterWithSelectedCategoryInPlanNextViewForProducts() {

    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = productsController.listProducts("003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page),
          null, null, "planned", null, "All", "Categorized1", map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("products.list", result);
      List<Product> productList = (List<Product>) map1.get("productsList");
      count = count + productList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    Assert.assertEquals(2, count);
  }

  /*
   * @author Abhaik
   * @description : Verify Products filter "All" link and select Category is "Null" for view planned tab
   */

  @Test
  public void testAllFilterWithNullCategoryInPlanNextViewForProducts() {

    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = productsController.listProducts("003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page),
          null, null, "planned", null, "All", null, map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("products.list", result);
      List<Product> productList = (List<Product>) map1.get("productsList");
      count = count + productList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    Assert.assertEquals(35, count);
  }

  /*
   * @author Abhaik
   * @description : Verify Products filter "All" link and select Category is "Empty" for view planned tab
   */

  @Test
  public void testAllFilterWithEmptyCategoryInPlanNextViewForProducts() {

    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = productsController.listProducts("003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page),
          null, null, "planned", null, "All", "", map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("products.list", result);
      List<Product> productList = (List<Product>) map1.get("productsList");
      count = count + productList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    Assert.assertEquals(35, count);
  }

  /*
   * @author Abhaik
   * @description : Verify Products filter "All" link and select Category is Invalid for view planned tab
   */

  @Test
  public void testAllFilterWithInvalidCategoryInPlanNextViewForProducts() {

    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = productsController.listProducts("003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page),
          null, null, "planned", null, "All", "Invalid", map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("products.list", result);
      List<Product> productList = (List<Product>) map1.get("productsList");
      count = count + productList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    Assert.assertEquals(0, count);
  }

  /*
   * @author Abhaik
   * @description : Verify Products filter "Null" link and select Category is Invalid for view planned tab
   */

  @Test
  public void testNullFilterWithInvalidCategoryInPlanNextViewForProducts() {

    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = productsController.listProducts("003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page),
          null, null, "planned", null, null, null, map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("products.list", result);
      List<Product> productList = (List<Product>) map1.get("productsList");
      count = count + productList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    Assert.assertEquals(35, count);
  }

  /*
   * @author Abhaik
   * @description : Verify Products filter "Active" link and select Category is "All" for view current tab
   */

  @Test
  public void testActiveFilterWithAllCategoryInCurrentViewForProducts() {

    int count = 0;
    boolean hasNext = true;
    int page = 1;
    while (hasNext) {
      String result = productsController.listProducts("003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page),
          null, null, "current", null, "Active", "All", map);
      Assert.assertNotNull(result);
      Assert.assertEquals("products.list", result);
      List<Product> productList = (List<Product>) map.get("productsList");
      count = count + productList.size();
      hasNext = (Boolean) map.get("enableNext");
      page = page + 1;
    }
    Assert.assertEquals(35, count);
  }

  /*
   * @author Abhaik
   * @description : Verify Products filter "Active" link and select Category value for view current tab
   */

  @Test
  public void testActiveFilterWithSelectedCategoryInCurrentViewForProducts() {

    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = productsController.listProducts("003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page),
          null, null, "current", null, "Active", "Categorized1", map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("products.list", result);
      List<Product> productList = (List<Product>) map1.get("productsList");
      count = count + productList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    Assert.assertEquals(2, count);
  }

  /*
   * @author Abhaik
   * @description : Verify Products filter "Active" link and select Category is "Null" for view current tab
   */

  @Test
  public void testActiveFilterWithNullCategoryInCurrentViewForProducts() {

    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = productsController.listProducts("003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page),
          null, null, "current", null, "Active", null, map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("products.list", result);
      List<Product> productList = (List<Product>) map1.get("productsList");
      count = count + productList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    Assert.assertEquals(35, count);
  }

  /*
   * @author Abhaik
   * @description : Verify Products filter "Active" link and select Category is "Empty" for view current tab
   */

  @Test
  public void testActiveFilterWithEmptyCategoryInCurrentViewForProducts() {

    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = productsController.listProducts("003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page),
          null, null, "current", null, "Active", "", map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("products.list", result);
      List<Product> productList = (List<Product>) map1.get("productsList");
      count = count + productList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    Assert.assertEquals(35, count);
  }

  /*
   * @author Abhaik
   * @description : Verify Products filter "Active" link and select Category is Invalid for view current tab
   */

  @Test
  public void testActiveFilterWithInvalidCategoryInCurrentViewForProducts() {

    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = productsController.listProducts("003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page),
          null, null, "current", null, "Active", "Invalid", map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("products.list", result);
      List<Product> productList = (List<Product>) map1.get("productsList");
      count = count + productList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    Assert.assertEquals(0, count);
  }

  /*
   * @author Abhaik
   * @description : Verify Products filter "Active" link and select Category is "All" for view history tab
   */

  @Test
  public void testActiveFilterWithAllCategoryInHistoryViewForProducts() {

    int count = 0;
    boolean hasNext = true;
    int page = 1;
    while (hasNext) {
      String result = productsController.listProducts("003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page),
          null, null, "history", null, "Active", "All", map);
      Assert.assertNotNull(result);
      Assert.assertEquals("products.list", result);
      List<Product> productList = (List<Product>) map.get("productsList");
      count = count + productList.size();
      hasNext = (Boolean) map.get("enableNext");
      page = page + 1;
    }
    Assert.assertEquals(35, count);
  }

  /*
   * @author Abhaik
   * @description : Verify Products filter "Active" link and select Category value for view history tab
   */

  @Test
  public void testActiveFilterWithSelectedCategoryInHistoryViewForProducts() {

    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = productsController.listProducts("003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page),
          null, null, "history", null, "Active", "Categorized1", map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("products.list", result);
      List<Product> productList = (List<Product>) map1.get("productsList");
      count = count + productList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    Assert.assertEquals(2, count);
  }

  /*
   * @author Abhaik
   * @description : Verify Products filter "Active" link and select Category is "Null" for view history tab
   */

  @Test
  public void testActiveFilterWithNullCategoryInHistoryViewForProducts() {

    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = productsController.listProducts("003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page),
          null, null, "history", null, "Active", null, map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("products.list", result);
      List<Product> productList = (List<Product>) map1.get("productsList");
      count = count + productList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    Assert.assertEquals(35, count);
  }

  /*
   * @author Abhaik
   * @description : Verify Products filter "Active" link and select Category is "Empty" for view history tab
   */

  @Test
  public void testActiveFilterWithEmptyCategoryInHistoryViewForProducts() {

    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = productsController.listProducts("003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page),
          null, null, "history", null, "Active", "", map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("products.list", result);
      List<Product> productList = (List<Product>) map1.get("productsList");
      count = count + productList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    Assert.assertEquals(35, count);
  }

  /*
   * @author Abhaik
   * @description : Verify Products filter "Active" link and select Category is Invalid for view history tab
   */

  @Test
  public void testActiveFilterWithInvalidCategoryInHistoryViewForProducts() {

    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = productsController.listProducts("003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page),
          null, null, "history", null, "Active", "Invalid", map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("products.list", result);
      List<Product> productList = (List<Product>) map1.get("productsList");
      count = count + productList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    Assert.assertEquals(0, count);
  }

  /*
   * @author Abhaik
   * @description : Verify Products filter "Active" link and select Category is "All" for view planned tab
   */

  @Test
  public void testActiveFilterWithAllCategoryInPlanNextViewForProducts() {

    int count = 0;
    boolean hasNext = true;
    int page = 1;
    while (hasNext) {
      String result = productsController.listProducts("003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page),
          null, null, "planned", null, "Active", "All", map);
      Assert.assertNotNull(result);
      Assert.assertEquals("products.list", result);
      List<Product> productList = (List<Product>) map.get("productsList");
      count = count + productList.size();
      hasNext = (Boolean) map.get("enableNext");
      page = page + 1;
    }
    Assert.assertEquals(35, count);
  }

  /*
   * @author Abhaik
   * @description : Verify Products filter "Active" link and select Category value for view planned tab
   */

  @Test
  public void testActiveFilterWithSelectedCategoryInPlanNextViewForProducts() {

    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = productsController.listProducts("003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page),
          null, null, "planned", null, "Active", "Categorized1", map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("products.list", result);
      List<Product> productList = (List<Product>) map1.get("productsList");
      count = count + productList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    Assert.assertEquals(2, count);
  }

  /*
   * @author Abhaik
   * @description : Verify Products filter "Active" link and select Category is "Null" for view planned tab
   */

  @Test
  public void testActiveFilterWithNullCategoryInPlanNextViewForProducts() {

    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = productsController.listProducts("003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page),
          null, null, "planned", null, "Active", null, map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("products.list", result);
      List<Product> productList = (List<Product>) map1.get("productsList");
      count = count + productList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    Assert.assertEquals(35, count);
  }

  /*
   * @author Abhaik
   * @description : Verify Products filter "Active" link and select Category is "Empty" for view planned tab
   */

  @Test
  public void testActiveFilterWithEmptyCategoryInPlanNextViewForProducts() {

    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = productsController.listProducts("003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page),
          null, null, "planned", null, "Active", "", map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("products.list", result);
      List<Product> productList = (List<Product>) map1.get("productsList");
      count = count + productList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    Assert.assertEquals(35, count);
  }

  /*
   * @author Abhaik
   * @description : Verify Products filter "Active" link and select Category is Invalid for view planned tab
   */

  @Test
  public void testActiveFilterWithInvalidCategoryInPlanNextViewForProducts() {

    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = productsController.listProducts("003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page),
          null, null, "planned", null, "Active", "Invalid", map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("products.list", result);
      List<Product> productList = (List<Product>) map1.get("productsList");
      count = count + productList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    Assert.assertEquals(0, count);
  }

  /*
   * @author Abhaik
   * @description : Verify Products filter "Retire" link and select Category is "All" for view current tab
   */

  @Test
  public void testRetireFilterWithAllCategoryInCurrentViewForProducts() {

    int count = 0;
    boolean hasNext = true;
    int page = 1;
    while (hasNext) {
      String result = productsController.listProducts("003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page),
          null, null, "current", null, "Retire", "All", map);
      Assert.assertNotNull(result);
      Assert.assertEquals("products.list", result);
      List<Product> productList = (List<Product>) map.get("productsList");
      count = count + productList.size();
      hasNext = (Boolean) map.get("enableNext");
      page = page + 1;
    }
    Assert.assertEquals(0, count);
  }

  /*
   * @author Abhaik
   * @description : Verify Products filter "Retire" link and select Category value for view current tab but product is
   * not Retire
   */

  @Test
  public void testRetireFilterWithSelectedCategoryInCurrentViewForProducts() {

    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = productsController.listProducts("003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page),
          null, null, "current", null, "Retire", "Categorized1", map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("products.list", result);
      List<Product> productList = (List<Product>) map1.get("productsList");
      count = count + productList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    Assert.assertEquals(0, count);
  }

  /*
   * @author Abhaik
   * @description : Verify Products filter "Retire" link and select Category is "Null" for view current tab
   */

  @Test
  public void testRetireFilterWithNullCategoryInCurrentViewForProducts() {

    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = productsController.listProducts("003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page),
          null, null, "current", null, "Retire", null, map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("products.list", result);
      List<Product> productList = (List<Product>) map1.get("productsList");
      count = count + productList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    Assert.assertEquals(0, count);
  }

  /*
   * @author Abhaik
   * @description : Verify Products filter "Retire" link and select Category is "Empty" for view current tab
   */

  @Test
  public void testRetireFilterWithEmptyCategoryInCurrentViewForProducts() {

    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = productsController.listProducts("003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page),
          null, null, "current", null, "Retire", "", map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("products.list", result);
      List<Product> productList = (List<Product>) map1.get("productsList");
      count = count + productList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    Assert.assertEquals(0, count);
  }

  /*
   * @author Abhaik
   * @description : Verify Products filter "Retire" link and select Category is Invalid for view current tab
   */

  @Test
  public void testRetireFilterWithInvalidCategoryInCurrentViewForProducts() {

    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = productsController.listProducts("003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page),
          null, null, "current", null, "Retire", "Invalid", map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("products.list", result);
      List<Product> productList = (List<Product>) map1.get("productsList");
      count = count + productList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    Assert.assertEquals(0, count);
  }

  /*
   * @author Abhaik
   * @description : Verify Products filter "Retire" link and select Category is "All" for view history tab
   */

  @Test
  public void testRetireFilterWithAllCategoryInHistoryViewForProducts() {

    int count = 0;
    boolean hasNext = true;
    int page = 1;
    while (hasNext) {
      String result = productsController.listProducts("003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page),
          null, null, "history", null, "Retire", "All", map);
      Assert.assertNotNull(result);
      Assert.assertEquals("products.list", result);
      List<Product> productList = (List<Product>) map.get("productsList");
      count = count + productList.size();
      hasNext = (Boolean) map.get("enableNext");
      page = page + 1;
    }
    Assert.assertEquals(0, count);
  }

  /*
   * @author Abhaik
   * @description : Verify Products filter "Retire" link and select Category value for view history tab but product is
   * not Retire
   */

  @Test
  public void testRetireFilterWithSelectedCategoryInHistoryViewForProducts() {

    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = productsController.listProducts("003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page),
          null, null, "history", null, "Retire", "Categorized1", map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("products.list", result);
      List<Product> productList = (List<Product>) map1.get("productsList");
      count = count + productList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    Assert.assertEquals(0, count);
  }

  /*
   * @author Abhaik
   * @description : Verify Products filter "Retire" link and select Category is "Null" for view history tab
   */

  @Test
  public void testRetireFilterWithNullCategoryInHistoryViewForProducts() {

    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = productsController.listProducts("003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page),
          null, null, "history", null, "Retire", null, map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("products.list", result);
      List<Product> productList = (List<Product>) map1.get("productsList");
      count = count + productList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    Assert.assertEquals(0, count);
  }

  /*
   * @author Abhaik
   * @description : Verify Products filter "Retire" link and select Category is "Empty" for view history tab
   */

  @Test
  public void testRetireFilterWithEmptyCategoryInHistoryViewForProducts() {

    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = productsController.listProducts("003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page),
          null, null, "history", null, "Retire", "", map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("products.list", result);
      List<Product> productList = (List<Product>) map1.get("productsList");
      count = count + productList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    Assert.assertEquals(0, count);
  }

  /*
   * @author Abhaik
   * @description : Verify Products filter "Retire" link and select Category is Invalid for view history tab
   */

  @Test
  public void testRetireFilterWithInvalidCategoryInHistoryViewForProducts() {

    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = productsController.listProducts("003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page),
          null, null, "history", null, "Retire", "Invalid", map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("products.list", result);
      List<Product> productList = (List<Product>) map1.get("productsList");
      count = count + productList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    Assert.assertEquals(0, count);
  }

  /*
   * @author Abhaik
   * @description : Verify Products filter "Retire" link and select Category is "All" for view planned tab
   */

  @Test
  public void testRetireFilterWithAllCategoryInPlanNextViewForProducts() {

    int count = 0;
    boolean hasNext = true;
    int page = 1;
    while (hasNext) {
      String result = productsController.listProducts("003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page),
          null, null, "planned", null, "Retire", "All", map);
      Assert.assertNotNull(result);
      Assert.assertEquals("products.list", result);
      List<Product> productList = (List<Product>) map.get("productsList");
      count = count + productList.size();
      hasNext = (Boolean) map.get("enableNext");
      page = page + 1;
    }
    Assert.assertEquals(0, count);
  }

  /*
   * @author Abhaik
   * @description : Verify Products filter "Retire" link and select Category value for view planned tab but product is
   * not Retire
   */

  @Test
  public void testRetireFilterWithSelectedCategoryInPlanNextViewForProducts() {

    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = productsController.listProducts("003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page),
          null, null, "planned", null, "Retire", "Categorized1", map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("products.list", result);
      List<Product> productList = (List<Product>) map1.get("productsList");
      count = count + productList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    Assert.assertEquals(0, count);
  }

  /*
   * @author Abhaik
   * @description : Verify Products filter "Retire" link and select Category is "Null" for view planned tab
   */

  @Test
  public void testRetireFilterWithNullCategoryInPlanNextViewForProducts() {

    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = productsController.listProducts("003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page),
          null, null, "planned", null, "Retire", null, map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("products.list", result);
      List<Product> productList = (List<Product>) map1.get("productsList");
      count = count + productList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    Assert.assertEquals(0, count);
  }

  /*
   * @author Abhaik
   * @description : Verify Products filter "Retire" link and select Category is "Empty" for view planned tab
   */

  @Test
  public void testRetireFilterWithEmptyCategoryInPlanNextViewForProducts() {

    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = productsController.listProducts("003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page),
          null, null, "planned", null, "Retire", "", map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("products.list", result);
      List<Product> productList = (List<Product>) map1.get("productsList");
      count = count + productList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    Assert.assertEquals(0, count);
  }

  /*
   * @author Abhaik
   * @description : Verify Products filter "Retire" link and select Category is Invalid for view planned tab
   */

  @Test
  public void testRetireFilterWithInvalidCategoryInPlanNextViewForProducts() {

    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = productsController.listProducts("003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page),
          null, null, "planned", null, "Retire", "Invalid", map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("products.list", result);
      List<Product> productList = (List<Product>) map1.get("productsList");
      count = count + productList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    Assert.assertEquals(0, count);
  }

  /*
   * @author Abhaik
   * @description : Verify Products is Retired filter "Retire" link and select Category is All for view current tab
   */

  @Test
  public void testRetiredProductRetireFilterWithAllCategoryInCurrentViewForProducts() {

    String product = productsController.retireProduct("11", "false", map);
    Assert.assertNotNull(product);
    Assert.assertEquals("success", product);
    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = productsController.listProducts("003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page),
          null, null, "current", null, "Retire", "All", map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("products.list", result);
      List<Product> productList = (List<Product>) map1.get("productsList");
      count = count + productList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    // IN Current Retired Product will come as Active Product
    Assert.assertEquals(0, count);
  }

  /*
   * @author Abhaik
   * @description : Verify Products is Retired filter "Retire" link and select Category is All for view History tab
   */

  @Test
  public void testRetiredProductRetireFilterWithAllCategoryInHistoryViewForProducts() {

    String product = productsController.retireProduct("11", "false", map);
    Assert.assertNotNull(product);
    Assert.assertEquals("success", product);
    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = productsController.listProducts("003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page),
          null, null, "history", null, "Retire", "All", map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("products.list", result);
      List<Product> productList = (List<Product>) map1.get("productsList");
      count = count + productList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    // If it Retired in future revsion it will come as active in previson
    // revison and from future revison it will come
    // as retired
    Assert.assertEquals(0, count);
  }

  /*
   * @author Abhaik
   * @description : Verify Products is Retired filter "Retire" link and select Category is All for view planned tab
   */

  @Test
  public void testRetiredProductRetireFilterWithAllCategoryInPlannedViewForProducts() {

    String product = productsController.retireProduct("11", "false", map);
    Assert.assertNotNull(product);
    Assert.assertEquals("success", product);
    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = productsController.listProducts("003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page),
          null, null, "planned", null, "Retire", "All", map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("products.list", result);
      List<Product> productList = (List<Product>) map1.get("productsList");
      count = count + productList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    // IN planned Retired Product will come
    Assert.assertEquals(1, count);
  }

  /*
   * @author Abhaik
   * @description : Verify Products is Retired filter "Retire" link and select Category is All for view current tab
   */

  @Test
  public void testRetiredProductRetireFilterWithSelectedCategoryInCurrentViewForProducts() {

    String product = productsController.retireProduct("11", "false", map);
    Assert.assertNotNull(product);
    Assert.assertEquals("success", product);
    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = productsController.listProducts("003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page),
          null, null, "current", null, "Retire", "Categorized4", map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("products.list", result);
      List<Product> productList = (List<Product>) map1.get("productsList");
      count = count + productList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    // IN current Retired Product will come as Active Product
    Assert.assertEquals(0, count);
  }

  /*
   * @author Abhaik
   * @description : Verify Products is Retired filter "Retire" link and select Category is All for view History tab
   */

  @Test
  public void testRetiredProductRetireFilterWithSelectedCategoryInHistoryViewForProducts() {

    String product = productsController.retireProduct("11", "false", map);
    Assert.assertNotNull(product);
    Assert.assertEquals("success", product);
    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = productsController.listProducts("003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page),
          null, null, "history", null, "Retire", "Categorized4", map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("products.list", result);
      List<Product> productList = (List<Product>) map1.get("productsList");
      count = count + productList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    // IN History Retired Product will come as Active Product
    Assert.assertEquals(0, count);
  }

  /*
   * @author Abhaik
   * @description : Verify Products is Retired filter "Retire" link and select Category is All for view planned tab
   */

  @Test
  public void testRetiredProductRetireFilterWithSelectedCategoryInPlannedViewForProducts() {

    String product = productsController.retireProduct("11", "false", map);
    Assert.assertNotNull(product);
    Assert.assertEquals("success", product);
    int count = 0;
    boolean hasNext = true;
    int page = 1;
    ModelMap map1 = new ModelMap();
    while (hasNext) {
      String result = productsController.listProducts("003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page),
          null, null, "planned", null, "Retire", "Categorized4", map1);
      Assert.assertNotNull(result);
      Assert.assertEquals("products.list", result);
      List<Product> productList = (List<Product>) map1.get("productsList");
      count = count + productList.size();
      hasNext = (Boolean) map1.get("enableNext");
      page = page + 1;
    }
    // If it is Retired in Future revision i will come as retired in that
    // revison
    Assert.assertEquals(1, count);
  }

  @Test
  public void testProductOrderBasedOnSameCategory() {
    int page = 1;
    ModelMap map1 = new ModelMap();
    String result = productsController.listProducts("003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page),
        null, null, "current", null, "All", "Categorized4", map1);
    Assert.assertNotNull(result);
    Assert.assertNotNull(result);
    Assert.assertEquals("products.list", result);
    List<Product> productList = (List<Product>) map1.get("productsList");
    Assert.assertEquals(2, productList.size());
    long diff = productList.get(1).getSortOrder() - productList.get(0).getSortOrder();
    Assert.assertTrue(diff > 0);
  }

  @Test
  public void testProductOrderBasedOnDifferentCategory() {
    int page = 3;
    ModelMap map1 = new ModelMap();
    String result = productsController.listProducts("003fa8ee-fba3-467f-a517-fd806dae8a80", Integer.toString(page),
        null, null, "current", null, "All", "All", map1);
    Assert.assertNotNull(result);
    Assert.assertNotNull(result);
    Assert.assertEquals("products.list", result);
    List<Product> productList = (List<Product>) map1.get("productsList");
    Assert.assertEquals(9, productList.size());
    Assert.assertTrue((productList.get(0).getSortOrder() - productList.get(1).getSortOrder()) > 0);
    Assert.assertTrue((productList.get(3).getSortOrder() - productList.get(4).getSortOrder()) < 0);
  }

  @Test
  public void testViewPlannedCharges() {

    Map<Product, List<ProductCharge>> plannedCharges = getPlanChargesofActiveProducts();

    List<CurrencyValue> activeCurrencies = currencyValueService.listActiveCurrencies();

    String result = productsController.viewPlannedCharges(map);

    Assert.assertEquals("view.planned.charges", result);
    Assert.assertEquals(activeCurrencies, map.get("currencieslist"));
    Assert.assertEquals(activeCurrencies.size(), map.get("currencieslistsize"));
    Assert.assertEquals(plannedCharges, map.get("plannedCharges"));
    Assert.assertEquals(channelService.getFutureRevision(null).getStartDate(), map.get("date"));
  }

  private Map<Product, List<ProductCharge>> getPlanChargesofActiveProducts() {
    Map<Product, List<ProductCharge>> plannedCharges = new HashMap<Product, List<ProductCharge>>();
    for (ProductRevision productRevision : channelService.getFutureChannelRevision(null, false).getProductRevisions()) {
      if (productRevision.getProduct().getRemoved() == null) {
        plannedCharges.put(productRevision.getProduct(), productRevision.getProductCharges());
      }
    }
    return plannedCharges;
  }

  private Map<Product, List<ProductCharge>> getPlanChargesofActiveProducts(String serviceInstanceUUID) {
    Map<Product, List<ProductCharge>> plannedCharges = new HashMap<Product, List<ProductCharge>>();
    for (ProductRevision productRevision : channelService.getFutureChannelRevision(null, false).getProductRevisions()) {
      if (productRevision.getProduct().getRemoved() == null
          && (productRevision.getProduct().getServiceInstance() != null && productRevision.getProduct()
              .getServiceInstance().getUuid().equals(serviceInstanceUUID))) {
        plannedCharges.put(productRevision.getProduct(), productRevision.getProductCharges());
      }
    }
    return plannedCharges;
  }

  @Test
  public void testAddProductCurrentCharges() {

    List<CurrencyValue> currencyValueList = currencyValueService.listActiveCurrencies();

    productsController.addProductCurrentCharges(productDAO.find(1L).getCode(), map);
    Assert.assertNotNull(map.get("productForm"));

    ProductForm form = (ProductForm) map.get("productForm");
    Assert.assertEquals(currencyValueList.size(), form.getProductCharges().size());
  }

  @Test
  public void testEditPlanCharges() {
    try {
      Revision futureRevision = channelService.getFutureRevision(null);
      List<CurrencyValue> activeCurrencies = currencyValueService.listActiveCurrencies();
      Map<Product, List<ProductCharge>> plannedCharges = getPlanChargesofActiveProducts(serviceInstanceDAO.find(1L)
          .getUuid());

      productsController.editPlannedCharges(serviceInstanceDAO.find(1L).getUuid(), map);
      Assert.assertEquals(activeCurrencies, map.get("currencieslist"));
      Assert.assertEquals(activeCurrencies.size(), map.get("currencieslistsize"));

      ProductForm form = (ProductForm) map.get("productForm");
      Assert.assertEquals(futureRevision.getStartDate(), form.getStartDate());
      Assert.assertEquals(plannedCharges.size(), form.getCurrentProductChargesMap().size());

    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
  }

  @ExpectedException(CurrencyPrecisionException.class)
  @Test
  public void testEditPlanChargesWithUncompatibleCurrencyPrecision() {

    Configuration precision = configurationDAO.findByName("com_citrix_cpbm_portal_appearance_currency_precision"
        .replace('_', '.'));
    precision.setValue("3");
    configurationDAO.save(precision);
    String serviceInstanceUUID = serviceInstanceDAO.find(1L).getUuid();
    for (ProductRevision productRevision : channelService.getFutureChannelRevision(null, false).getProductRevisions()) {
      // Ignore removed products in the future revision
      if (productRevision.getProduct().getRemoved() == null
          && (productRevision.getProduct().getServiceInstance() != null && productRevision.getProduct()
              .getServiceInstance().getUuid().equals(serviceInstanceUUID))) {
        List<ProductCharge> pcharges = productRevision.getProductCharges();
        ProductCharge pc = pcharges.get(0);
        pc.setPrice(new BigDecimal("5.1234"));
      }
    }

    productsController.editPlannedCharges(serviceInstanceUUID, map);

  }

  @Test
  public void testSortProducts() {
    try {

      productsController.sortProducts(serviceInstanceDAO.find(1L).getUuid(), "current", null, null, null, map);
      List<Product> productsList = (List<Product>) map.get("productsList");
      int size = getProductListSize("current", null);
      Assert.assertEquals(size, productsList.size());

      for (int i = 1; i < size; i++) {
        Assert.assertTrue(productsList.get(i).getCategory().getId() >= productsList.get(i - 1).getCategory().getId());
        if (productsList.get(i).getCategory().getId() == productsList.get(i - 1).getCategory().getId()) {
          Assert.assertTrue(productsList.get(i).getSortOrder() > productsList.get(i - 1).getSortOrder());
        }
      }

      productsController.sortProducts(serviceInstanceDAO.find(1L).getUuid(), "planned", null, null, null, map);
      productsList = (List<Product>) map.get("productsList");
      size = getProductListSize("planned", null);
      Assert.assertEquals(size, productsList.size());
      for (int i = 1; i < size; i++) {
        Assert.assertTrue(productsList.get(i).getCategory().getId() >= productsList.get(i - 1).getCategory().getId());
        if (productsList.get(i).getCategory().getId() == productsList.get(i - 1).getCategory().getId()) {
          Assert.assertTrue(productsList.get(i).getSortOrder() > productsList.get(i - 1).getSortOrder());
        }
      }

      Date date = productService.getReferencePriceBookHistoryRevisions().get(0).getStartDate();
      productsController.sortProducts(serviceInstanceDAO.find(1L).getUuid(), "history", null, null, null, map);
      productsList = (List<Product>) map.get("productsList");
      size = getProductListSize("history", date);
      Assert.assertEquals(size, productsList.size());
      for (int i = 1; i < size; i++) {
        Assert.assertTrue(productsList.get(i).getCategory().getId() >= productsList.get(i - 1).getCategory().getId());
        if (productsList.get(i).getCategory().getId() == productsList.get(i - 1).getCategory().getId()) {
          Assert.assertTrue(productsList.get(i).getSortOrder() > productsList.get(i - 1).getSortOrder());
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
  }

  private int getProductListSize(String plan, Date date) {

    Revision effectiveRevision = null;

    if (plan.equals("planned")) {
      effectiveRevision = channelService.getFutureRevision(null);
    } else if (plan.equals("current")) {
      effectiveRevision = channelService.getCurrentRevision(null);
    } else if (plan.equals("history")) {
      effectiveRevision = channelService.getRevisionForTheDateGiven(date, null);
    }
    List<Product> products = productService.listProducts(effectiveRevision);

    int size = 0;

    for (Product product : products) {
      if ((product.getRemoved() == null || (product.getRemoved() != null && effectiveRevision != null && product
          .getRemovedInRevision().getId() > effectiveRevision.getId()))
          && (product.getServiceInstance() != null && product.getServiceInstance().getUuid().toString()
              .equals(serviceInstanceDAO.find(1L).getUuid().trim()))) {
        size++;
      }
    }
    return size;
  }

  @Test
  public void testViewProductChargesHistory() {
    try {
      DateFormat formatter;
      formatter = new SimpleDateFormat("MM/dd/yyyy");
      String strDate = "01/01/2012 00:00:00";
      Date historyDate = formatter.parse(strDate);
      Product product = productDAO.find(1L);

      List<ProductCharge> productChargesList = productService.getProductCharges(product, historyDate);

      productsController.viewProductChargesHistory(product.getCode(), strDate, map);

      List<ProductCharge> actualProductChargesList = (List<ProductCharge>) map.get("productChargesList");
      Assert.assertEquals(productChargesList.size(), actualProductChargesList.size());

      CurrencyValue currency = productChargesList.get(0).getCurrencyValue();

      for (ProductCharge charge : actualProductChargesList) {
        if (charge.getCurrencyValue().equals(currency)) {
          Assert.assertEquals(productChargesList.get(0).getPrice(), charge.getPrice());
        }
      }

      Assert.assertEquals(historyDate, map.get("historyDate"));

      // when date is null
      Date date = productService.getReferencePriceBookHistoryRevisions().get(0).getStartDate();
      productChargesList = productService.getProductCharges(product, date);

      productsController.viewProductChargesHistory(product.getCode(), null, map);

      actualProductChargesList = (List<ProductCharge>) map.get("productChargesList");
      Assert.assertEquals(productChargesList.size(), actualProductChargesList.size());
      historyDate = (Date) map.get("historyDate");
      Assert.assertTrue(DateUtils.isSameDay(historyDate, date));

      currency = productChargesList.get(0).getCurrencyValue();

      for (ProductCharge charge : actualProductChargesList) {
        if (charge.getCurrencyValue().equals(currency)) {
          Assert.assertEquals(productChargesList.get(0).getPrice(), charge.getPrice());
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
  }

  @Test
  public void testSetPlanDateWithFutureDate() {
    try {
      // Case: Future Revision starts after today
      Date futureDate = DateUtils.addDays(new Date(), 3);

      ProductForm postForm = new ProductForm();
      postForm.setStartDate(futureDate);
      BindingResult result = validate(postForm);
      // http post call
      productsController.setPlanDate(postForm, result, map);

      productsController.setPlanDate(map);
      Assert.assertEquals(true, map.get("isPlanDateThere"));
      Assert.assertEquals(true, map.get("isTodayAllowed"));
      Date date = (Date) map.get("date_today");
      Assert.assertTrue(DateUtils.isSameDay(new Date(), date));

      ProductForm form = (ProductForm) map.get("planDateForm");
      Assert.assertTrue(DateUtils.isSameDay(futureDate, form.getStartDate()));
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
  }

  @Test
  public void testSetPlanDateNegativeTest() {
    try {
      // date same as start date
      ProductForm form = new ProductForm();
      Date date = DateUtils.minusOneDay(new Date());

      form.setStartDate(date);
      BindingResult result = validate(form);
      String mockResponse = productsController.setPlanDate(form, result, map);
      Assert.assertEquals("plan_date_should_be_greater_to_today", mockResponse);

    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
  }

  @Test
  public void testSetPlanDateforFutureDate() {
    try {
      // Activating today
      ProductForm form = new ProductForm();
      Date date = DateUtils.truncate(DateUtils.addDays(new Date(), 1));

      form.setStartDate(date);
      BindingResult result = validate(form);
      productsController.setPlanDate(form, result, map);
      Assert.assertEquals(date, channelService.getFutureRevision(null).getStartDate());
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
  }

  @Test
  public void testSetPlanDateforToday() {
    try {
      // Activating today
      ProductForm form = new ProductForm();
      Date date = new Date();

      form.setStartDate(date);
      BindingResult result = validate(form);
      productsController.setPlanDate(form, result, map);
      Assert.assertTrue(DateUtils.isSameDay(date, channelService.getCurrentRevision(null).getStartDate()));
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
  }

  /*
   * Description: Test Current History Applicable For RPB. Author: Avinash ReviewedBy: Atulb
   */

  @Test
  public void testIsCurrentHistoryApplicableForRPB() {

    new ModelMap();
    Map<String, String> result = productsController.isCurrentAndHistoryApplicableForRPB();
    Assert.assertNotNull(result);
    Assert.assertNotNull(result.get("current"));
    Assert.assertEquals("true", result.get("current"));
    Assert.assertEquals("true", result.get("history"));
  }

  /*
   * Description: Test Get Service Catagories. Author: Avinash ReviewedBy:Atulb
   */

  @Test
  public void testGetServiceCatagoriesPlanned() {

    ModelMap modelmap = new ModelMap();
    String result = productsController.getServiceCategories(modelmap, "planned", "");
    Assert.assertEquals(new String("products.service.categories.show"), result);
    Assert.assertNotNull(modelmap);
    Assert.assertEquals(null, modelmap.get("futurePlanDate"));
    Assert.assertEquals(null, modelmap.get("revisionDate"));
    Assert.assertEquals("IAAS", modelmap.get("selectedCategory"));
    Assert.assertEquals("NORMAL", modelmap.get("top_nav_health_status"));
    Assert.assertEquals(null, modelmap.get("historyDates"));

  }

  @Test
  public void testGetServiceCatagoriesHistory() {

    ModelMap modelmap = new ModelMap();
    String result = productsController.getServiceCategories(modelmap, "history", "");
    Assert.assertEquals(new String("products.service.categories.show"), result);
    Assert.assertNotNull(modelmap);
    Assert.assertEquals(null, modelmap.get("futurePlanDate"));
    Assert.assertEquals("2012-05-28 00:00:00.0", modelmap.get("revisionDate").toString());
    Assert.assertEquals("IAAS", modelmap.get("selectedCategory"));
    Assert.assertEquals("NORMAL", modelmap.get("top_nav_health_status"));
    Assert.assertEquals("[2012-05-28 00:00:00.0, 2012-05-01 00:00:00.0, 2001-01-01 00:00:00.0]",
        modelmap.get("historyDates").toString());
  }

  @Test
  public void testGetServiceCatagoriesRevisionDate() {

    ModelMap modelmap = new ModelMap();
    String result = productsController.getServiceCategories(modelmap, "revisionDate", "");
    Assert.assertEquals(new String("products.service.categories.show"), result);
    Assert.assertNotNull(modelmap);
    Assert.assertEquals(null, modelmap.get("futurePlanDate"));
    Assert.assertEquals(null, modelmap.get("revisionDate"));
    Assert.assertEquals("IAAS", modelmap.get("selectedCategory"));
    Assert.assertEquals("NORMAL", modelmap.get("top_nav_health_status"));
    Assert.assertEquals(null, modelmap.get("historyDates"));
  }

  /*
   * Description: Test Get Service Instances. Author: Avinash ReviewedBy:Atulb
   */

  @Test
  public void testGetServiceInstancesPlanned() {

    ModelMap modelmap = new ModelMap();
    String result = productsController.getServiceInstances(modelmap, "003fa8ee-fba3-467f-a517-fd806dae8a80", "planned",
        "");
    Assert.assertNotNull(modelmap);
    List<Service> serviceCategoryList = (List<Service>) modelmap.get("serviceCategoryList");
    Assert.assertNotNull(modelmap.get("serviceCategoryList"));
    Assert.assertEquals(5, serviceCategoryList.size());
    Assert.assertEquals(null, modelmap.get("futurePlanDate"));
    Assert.assertEquals(null, modelmap.get("revisionDate"));
    Assert.assertEquals(null, modelmap.get("historyDates"));
    Assert.assertEquals("fc3c6f30-a44a-4754-a8cc-9cea97e0a129", serviceCategoryList.get(0).getUuid());
    Assert.assertEquals("6", serviceCategoryList.get(0).getId().toString());
    Assert.assertEquals("IAAS", serviceCategoryList.get(0).getCategory());
    Assert.assertEquals("CloudPlatform", serviceCategoryList.get(0).getServiceName());
    Assert.assertEquals("Citrix", serviceCategoryList.get(0).getVendor());
    Assert.assertEquals(false, serviceCategoryList.get(0).getSingleton());
    Assert.assertEquals(false, serviceCategoryList.contains(serviceCategoryList));
    Assert.assertEquals(result, new String("products.instances.show"));

  }

  /*
   * Description: Test Get Service Instances. Author: Avinash ReviewedBy:Atulb
   */

  @Test
  public void testGetServiceInstancesHistory() {

    ModelMap modelmap = new ModelMap();

    String result = productsController.getServiceInstances(modelmap, "003fa8ee-fba3-467f-a517-fd806dae8a80", "history",
        null);
    Assert.assertNotNull(modelmap);
    List<Service> serviceCategoryList = (List<Service>) modelmap.get("serviceCategoryList");
    Assert.assertNotNull(modelmap.get("serviceCategoryList"));
    Assert.assertEquals(5, serviceCategoryList.size());
    Assert.assertEquals(null, modelmap.get("futurePlanDate"));
    Assert.assertEquals(null, modelmap.get("revisionDate"));
    Assert.assertEquals("[2012-05-28 00:00:00.0, 2012-05-01 00:00:00.0, 2001-01-01 00:00:00.0]",
        modelmap.get("historyDates").toString());
    Assert.assertEquals("fc3c6f30-a44a-4754-a8cc-9cea97e0a129", serviceCategoryList.get(0).getUuid());
    Assert.assertEquals("6", serviceCategoryList.get(0).getId().toString());
    Assert.assertEquals("IAAS", serviceCategoryList.get(0).getCategory());
    Assert.assertEquals("CloudPlatform", serviceCategoryList.get(0).getServiceName());
    Assert.assertEquals("Citrix", serviceCategoryList.get(0).getVendor());
    Assert.assertEquals(false, serviceCategoryList.get(0).getSingleton());
    Assert.assertEquals(false, serviceCategoryList.contains(serviceCategoryList));
    Assert.assertEquals(result, new String("products.instances.show"));

  }

  /*
   * Description: Test View Product Current Charges" for the selected product. Author: Avinash ReviewedBy : Atulb
   */

  @Test
  public void testViewProductCurrentCharges() throws Exception {

    ModelMap modelmap = new ModelMap();
    productsController.viewProductCurrentCharges("SYSTEM_VOLUME", modelmap);
    Assert.assertNotNull(modelmap);
    List<ProductCharge> returnedCharges = (List<ProductCharge>) modelmap.get("productChargesList");
    Assert.assertNotNull(returnedCharges);
    Assert.assertEquals(6, returnedCharges.size());
    ProductCharge euroCharge = returnedCharges.get(0);
    Assert.assertEquals(44, euroCharge.getCurrencyValue().getId().intValue());
    Assert.assertEquals(10, euroCharge.getPrice().intValue());
    ProductCharge usdCharges = returnedCharges.get(1);
    Assert.assertEquals(149, usdCharges.getCurrencyValue().getId().intValue());
    Assert.assertEquals(10, usdCharges.getPrice().intValue());
    boolean noChargesYetSet = (Boolean) modelmap.get("noChargesSetYet");
    Assert.assertEquals(false, noChargesYetSet);
    boolean hasHistoricalRevisions = (Boolean) modelmap.get("hasHistoricalRevisions");
    Assert.assertEquals(true, hasHistoricalRevisions);
    productsController.viewProductCurrentCharges("RVM", modelmap);
    Assert.assertNotNull(modelmap);
    returnedCharges = (List<ProductCharge>) modelmap.get("productChargesList");
    Assert.assertNotNull(returnedCharges);
    Assert.assertEquals(0, returnedCharges.size());
    boolean noChargesYetSetRVM = (Boolean) modelmap.get("noChargesSetYet");
    Assert.assertEquals(true, noChargesYetSetRVM);
    boolean hasHistoricalRevisionsRVM = (Boolean) modelmap.get("hasHistoricalRevisions");
    Assert.assertEquals(true, hasHistoricalRevisionsRVM);
  }

  /*
   * Description: Test create new product Author: Avinash ReviewedBy::Ankit
   */

  @Test
  public void testCreateProductHasPlannedChargesTrue() throws Exception {

    ModelMap modelmap = new ModelMap();
    int beforeCreationProductLength = productsService.getProductsCount();
    String result = productsController.createProduct(serviceInstanceDAO.find(1L).getUuid().toString(), modelmap);
    int afterCreationProductLength = productsService.getProductsCount();
    Assert.assertEquals(beforeCreationProductLength + 0, afterCreationProductLength);
    Assert.assertNotNull(modelmap);

    modelmap.get("productForm");

    Assert.assertTrue(modelmap.get("hasplannedcharges").toString(), true);
    productsService.getCurrentRevision(null);
    Assert.assertEquals("2013-01-09 00:00:00.0", modelmap.get("date").toString());
    List<CurrencyValue> activeCurrencies = currencyValueService.listActiveCurrencies();
    Assert.assertEquals(6, activeCurrencies.size());
    List<Category> categories = productsService.getAllCategories();
    Assert.assertEquals(5, categories.size());
    Assert.assertEquals(result, new String("products.new"));
    Assert.assertEquals("com.company1.service1", modelmap.get("serviceName").toString());
    Assert.assertEquals("b1c9fbb0-8dab-42dc-ae0a-ce1384a1e6", modelmap.get("serviceUuid").toString());
    Set<String> serviceUsageTypeNames = (Set<String>) modelmap.get("serviceUsageTypeNames");
    Assert.assertEquals(28, serviceUsageTypeNames.size());
    Set<String> discrimintaorNames = (Set<String>) modelmap.get("discrimintaorNames");
    Assert.assertEquals(4, discrimintaorNames.size());
  }

  /*
   * Description: Test list usage for the specific product. Author: Avinash ReviewedBy::Ankit
   */

  @Test
  public void testlistUsage() throws Exception {

    new ModelMap();
    List<ServiceUsageType> result = productsController.listUsageTypes(serviceInstanceDAO.find(1L).getUuid().toString());
    ServiceInstance serviceInstance = serviceInstanceDAO.getServiceInstance("003fa8ee-fba3-467f-a517-fd806dae8a80");
    Service service = serviceInstance.getService();
    List<ServiceUsageType> serviceUsageTypeList = service.getServiceUsageTypes();
    Assert.assertNotNull(result);
    Assert.assertEquals(serviceUsageTypeList, result);
    Assert.assertEquals(29, result.size());
    Assert.assertEquals("RUNNING_VM", serviceUsageTypeList.get(0).getUsageTypeName().toString());
    Assert.assertEquals("com.company1.service1", service.getServiceName());
    Assert.assertEquals(false, service.getSingleton());
    Assert.assertEquals("IAAS", service.getCategory());
    Assert.assertEquals("CLOUD", service.getType());
    Assert.assertEquals("b1c9fbb0-8dab-42dc-ae0a-ce1384a1e6", service.getUuid());
    Assert.assertEquals(29, service.getServiceUsageTypes().size());
    Assert.assertEquals("TEMPLATE", serviceUsageTypeList.get(1).getUsageTypeName().toString());
    Assert.assertEquals("HYPERVISOR", serviceUsageTypeList.get(2).getUsageTypeName().toString());
    Assert.assertEquals("VOLUME", serviceUsageTypeList.get(3).getUsageTypeName().toString());
    Assert.assertEquals("ALLOCATED_VM", serviceUsageTypeList.get(4).getUsageTypeName().toString());
    Assert.assertEquals("NETWORK_BYTES_IN", serviceUsageTypeList.get(5).getUsageTypeName().toString());
    Assert.assertEquals("NETWORK_BYTES_OUT", serviceUsageTypeList.get(6).getUsageTypeName().toString());
    Assert.assertEquals("MRD_UT1", serviceUsageTypeList.get(7).getUsageTypeName().toString());
    Assert.assertEquals("MRD_UT2", serviceUsageTypeList.get(8).getUsageTypeName().toString());

  }

  /*
   * Description: Test View Product product. Author: Avinash ReviewedBy::Ankit
   */

  @Test
  public void testViewProduct() throws Exception {

    ModelMap modelmap = new ModelMap();
    String result = productsController.viewProduct("1", "planned", modelmap);
    Product product = productsService.locateProduct("1", true);
    Assert.assertNotNull(result);
    Assert.assertEquals("products.view", result);
    Assert.assertNotNull(modelmap);
    Assert.assertEquals(product, modelmap.get("product"));
    Assert.assertEquals("planned", modelmap.get("whichPlan"));
    Assert.assertEquals("Volume", product.getName());
    Assert.assertEquals("SYSTEM_VOLUME", product.getCode());

  }

  /*
   * Description: Test View Product Channel Pricing Details History "planned" for the selected product. Author: Avinash
   * ReviewedBy::Ankit
   */

  @Test
  public void testViewProductChannelPricingDetailsPlanned() throws Exception {

    ModelMap modelmap = new ModelMap();
    Channel channel = channelDAO.find(1L);
    String result = productsController.viewProductChannelPricingDetails("SYSTEM_VOLUME", modelmap, "USD", "planned",
        null);
    Assert.assertNotNull(modelmap);
    Revision channelRevision = channelService.getFutureRevision(null);

    Map<Channel, List<ProductCharge>> productChannelChargesMap = (Map<Channel, List<ProductCharge>>) modelmap
        .get("productChannelChargesMap");
    List<CurrencyValue> activeCurrencies = (List<CurrencyValue>) modelmap.get("currencieslist");

    boolean found = false;
    for (CurrencyValue currencyValue : activeCurrencies) {
      if (currencyValue.getCurrencyCode().equals("USD")) {
        Assert.assertEquals("US Dollar", currencyValue.getCurrencyName());
        Assert.assertEquals(2, currencyValue.getRank());
        found = true;
        break;
      }
    }
    Assert.assertTrue(found);

    found = false;
    for (ProductCharge productCharge : productChannelChargesMap.get(channel)) {
      if (productCharge.getProduct().equals(productDAO.find(1L))) {
        Assert.assertEquals("20.0000", productCharge.getPrice().toString());
        found = true;
      }
    }
    Assert.assertTrue(found);

    Assert.assertEquals("USD", modelmap.get("currenciesToDisplay"));
    Assert.assertNotNull(productChannelChargesMap);
    Assert.assertNotNull(result);
    Assert.assertEquals("product.view.channelpricing", result);

    if (channelRevision != null) {
      Assert.assertEquals(channelRevision.getStartDate(), modelmap.get("date"));
    } else {
      Assert.assertEquals(null, modelmap.get("date"));
    }
  }

  /*
   * Description: Test View Product Channel Pricing Details History "current" for the selected product. Author: Avinash
   * ReviewedBy::Ankit
   */

  @Test
  public void testViewProductChannelPricingDetailsCurrent() throws Exception {

    ModelMap modelmap = new ModelMap();
    Channel channel = channelDAO.find(1L);
    String result = productsController.viewProductChannelPricingDetails("CODEsmallRunningVm2", modelmap, "USD",
        "current", null);
    Assert.assertNotNull(modelmap);
    Revision channelRevision = channelService.getFutureRevision(null);

    Map<Channel, List<ProductCharge>> productChannelChargesMap = (Map<Channel, List<ProductCharge>>) modelmap
        .get("productChannelChargesMap");
    List<CurrencyValue> activeCurrencies = (List<CurrencyValue>) modelmap.get("currencieslist");

    boolean found = true;
    for (CurrencyValue currencyValue : activeCurrencies) {
      if (currencyValue.getCurrencyCode().equals("USD")) {
        Assert.assertEquals("US Dollar", currencyValue.getCurrencyName());
        Assert.assertEquals(2, currencyValue.getRank());
        found = true;
        break;
      }
    }
    Assert.assertTrue(found);

    found = true;
    for (ProductCharge productCharge : productChannelChargesMap.get(channel)) {
      if (productCharge.getProduct().equals(productDAO.find(1L))) {
        Assert.assertEquals("20.0000", productCharge.getPrice().toString());
        found = true;
      }
    }
    Assert.assertTrue(found);

    Assert.assertEquals("USD", modelmap.get("currenciesToDisplay"));
    Assert.assertNotNull(productChannelChargesMap);
    Assert.assertNotNull(result);
    Assert.assertEquals("product.view.channelpricing", result);
    channelRevision = channelService.getFutureRevision(channelDAO.find(14L));
    if (channelRevision != null) {
      Assert.assertEquals("2012-05-05 00:00:00.0", modelmap.get("date").toString());
    } else {
      Assert.assertEquals(null, modelmap.get("date"));
    }
  }

  /*
   * Description: Test View Product Channel Pricing Details History "history" for the selected product. Author: Avinash
   * ReviewedBy::Ankit
   */

  @Test
  public void testViewProductChannelPricingDetailsHistory() throws Exception {

    ModelMap modelmap = new ModelMap();
    Channel channel = channelDAO.find(1L);
    String result = productsController.viewProductChannelPricingDetails("CODEsmallRunningVm2", modelmap, "USD",
        "history", null);
    Assert.assertNotNull(modelmap);
    Assert.assertNotNull(modelmap);
    Revision channelRevision = channelService.getFutureRevision(null);

    Map<Channel, List<ProductCharge>> productChannelChargesMap = (Map<Channel, List<ProductCharge>>) modelmap
        .get("productChannelChargesMap");
    List<CurrencyValue> activeCurrencies = (List<CurrencyValue>) modelmap.get("currencieslist");

    boolean found = true;
    for (CurrencyValue currencyValue : activeCurrencies) {
      if (currencyValue.getCurrencyCode().equals("USD")) {
        Assert.assertEquals("US Dollar", currencyValue.getCurrencyName());
        Assert.assertEquals(2, currencyValue.getRank());
        found = true;
        break;
      }
    }
    Assert.assertTrue(found);

    found = true;
    for (ProductCharge productCharge : productChannelChargesMap.get(channel)) {
      if (productCharge.getProduct().equals(productDAO.find(1L))) {
        Assert.assertEquals("20.0000", productCharge.getPrice().toString());
        found = true;
      }
    }
    Assert.assertTrue(found);

    Assert.assertEquals("USD", modelmap.get("currenciesToDisplay"));
    Assert.assertNotNull(productChannelChargesMap);
    Assert.assertNotNull(result);
    Assert.assertEquals("product.view.channelpricing", result);

    if (channelRevision != null) {
      Assert.assertEquals("Mon May 28 00:00:00 GMT 2012", modelmap.get("date").toString());
    } else {
      Assert.assertEquals(null, modelmap.get("date"));
    }
  }

  /*
   * Description: Test view mediation rule "current" for the selected product. Author: Avinash ReviewedBy::Ankit
   */
  @Test
  public void testViewMediationRulesPlanned() throws Exception {

    ModelMap modelmap = new ModelMap();
    Product product = productsService.locateProductByCode("CODEsmallRunningVm2");
    String result = productsController.viewMediationRules("CODEsmallRunningVm2", modelmap, "current", "");
    Assert.assertNotNull(modelmap);
    List<MediationRule> mediationRules = new ArrayList<MediationRule>();
    mediationRules = productsService.getProductRevision(product,
        channelService.getCurrentRevision(null).getStartDate(), null).getMediationRules();
    Assert.assertEquals(mediationRules, modelmap.get("mediationRules"));
    Assert.assertEquals("view.product.mediation.rules", result);
  }

  /*
   * Description: Test view mediation rule "History" for the selected product. Author: Avinash ReviewedBy::Ankit
   */

  @Test
  public void testViewMediationRulesHistory() throws Exception {

    ModelMap modelmap = new ModelMap();
    Product product = productsService.locateProductByCode("CODEsmallRunningVm2");
    String result = productsController.viewMediationRules("CODEsmallRunningVm2", modelmap, "history", "");
    Assert.assertNotNull(modelmap);
    List<Revision> revisions = productsService.getReferencePriceBookHistoryRevisions();
    String historyDate = DateUtils.getStringForCalendar(revisions.get(0).getStartDate(),
        DateUtils.getDateFormatter("MM/dd/yyyy HH:mm:ss"));
    Date date = DateUtils.truncate(DateUtils.getCalendarForDate(historyDate.toString(),
        DateUtils.getDateFormatter("MM/dd/yyyy HH:mm:ss")).getTime());
    List<MediationRule> mediationRules = new ArrayList<MediationRule>();
    mediationRules = productsService.getProductRevision(product, date, null).getMediationRules();
    Assert.assertEquals(mediationRules, modelmap.get("mediationRules"));
    Assert.assertEquals("view.product.mediation.rules", result);
  }

  /*
   * Description: Test view mediation rule "planned" for the selected product. Author: Avinash ReviewedBy:Ankit
   */

  @Test
  public void testViewMediationRulesCurrent() throws Exception {

    ModelMap modelmap = new ModelMap();
    Product product = productsService.locateProductByCode("CODEsmallRunningVm2");
    String result = productsController.viewMediationRules("CODEsmallRunningVm2", modelmap, "planned", "");
    Assert.assertNotNull(modelmap);
    List<MediationRule> mediationRules = new ArrayList<MediationRule>();
    mediationRules = productsService.getProductRevision(product, channelService.getFutureRevision(null).getStartDate(),
        null).getMediationRules();
    Assert.assertEquals(mediationRules, modelmap.get("mediationRules"));
    Assert.assertEquals("view.product.mediation.rules", result);
  }

  /*
   * Description: Test edit product logo Author: Avinash ReviewedBy:Ankit
   */
  @Test
  public void testEditProductLogo() throws Exception {

    Product product = productDAO.find(1L);
    ProductLogoForm form = new ProductLogoForm(product);
    MultipartFile logo = new MockMultipartFile("Product.jpeg", "Product.jpeg", "bytes", "ProductLogo".getBytes());
    form.setLogo(logo);
    BindingResult result = validate(form);
    Assert.assertNotNull(result);
    String resultString = productsController.editProductLogo("1", map);
    Assert.assertNotNull(resultString);
  }

  /*
   * Description: Test edit existing product product Author: Avinash ReviewedBy:Ankit
   */

  @Test
  public void testEditProductModel() throws Exception {

    ServiceInstance serviceInstance = serviceInstanceDAO.find("1");
    Service service = serviceInstance.getService();
    ServiceUsageType serviceUsageType = null;
    List<ServiceUsageType> serviceUsagetypelist = serviceInstance.getService().getServiceUsageTypes();
    for (ServiceUsageType serviceUsageType1 : serviceUsagetypelist) {
      if (serviceUsageType1.getUsageTypeName().equalsIgnoreCase("RUNNING_VM")) {
        serviceUsageType = serviceUsageType1;
        break;
      }
    }

    Product product = new Product("New", "New_Prod", "New_Prod", "", getRootUser());
    product.setCode("New");
    product.setServiceInstance(serviceInstanceDAO.find(serviceInstance.getId()));
    product.setUom("Hours");
    Category category = productsService.getCategory(1L);
    product.setCategory(category);
    ProductForm form = new ProductForm(product);
    form.setStartDate(new Date());
    form.setServiceUUID(service.getUuid());
    form.setServiceInstanceUUID(serviceInstanceDAO.find(1L).getUuid());
    form.setCategoryID(category.getId().toString());
    List<ProductCharge> productCharges = new ArrayList<ProductCharge>();
    ProductCharge productCharge = new ProductCharge();
    productCharge.setRevision(revisionDAO.getCurrentRevision(null));
    productCharge.setCatalog(null);
    productCharge.setCurrencyValue(currencyValueDAO.find(1L));
    productCharge.setPrice(BigDecimal.valueOf(100));
    productCharge.setCreatedAt(new Date());
    productCharge.setCreatedBy(getRootUser());
    productCharge.setUpdatedBy(getRootUser());
    productsService.saveProductCharge(productCharge);
    productCharges.add(productCharge);

    form.setProductCharges(productCharges);
    form.setIsNewProduct(true);

    String jsonString = "[ {conversionFactor: 1.00, operator: COMBINE, usageTypeId: " + serviceUsageType.getId()
        + "} ]";
    form.setProductMediationRules(jsonString);
    validate(form);

    ModelMap modelmap = new ModelMap();
    BindingResult bindingResult = validate(form);
    Product result1 = productsController.editProduct(form, bindingResult, modelmap);
    Assert.assertEquals(product, result1);
    Assert.assertEquals("RUNNING_VM", serviceUsagetypelist.get(0).getUsageTypeName());
    Assert.assertEquals(false, serviceUsagetypelist.get(0).getService().getSingleton());
    Assert.assertEquals("IAAS", serviceUsagetypelist.get(0).getService().getCategory());
    Assert.assertEquals("b1c9fbb0-8dab-42dc-ae0a-ce1384a1e6", serviceUsagetypelist.get(0).getService().getUuid());
    Assert.assertEquals("Company1", serviceUsagetypelist.get(0).getService().getVendor());
    Assert.assertEquals("1.2.1", serviceUsagetypelist.get(0).getService().getVendorVersion());
    Assert.assertEquals("CLOUD", serviceUsagetypelist.get(0).getService().getType());
  }

  /*
   * Description: Test list Discriminator. Author: Avinash
   */

  @Test
  public void testDiscriminators() throws Exception {
    Long usageTypeId = serviceUsageTypeDAO.find(1L).getId();

    String serviceInstanceUUID = serviceInstanceDAO.find(1L).getUuid().toString();
    asPortal();
    Set<ServiceDiscriminator> serviceDiscriminators = new HashSet<ServiceDiscriminator>();
    ServiceInstance serviceInstance = serviceInstanceDAO.getServiceInstance(serviceInstanceUUID);
    for (ServiceUsageType serviceUsageType : serviceInstance.getService().getServiceUsageTypes()) {
      if (serviceUsageType.getId().equals(usageTypeId)) {
        serviceDiscriminators = serviceInstance.getService().getServiceUsageTypeDiscriminator(serviceUsageType);
        break;
      }
    }
    Map<String, Object> finalMap = new HashMap<String, Object>();
    for (ServiceDiscriminator serviceDiscriminator : serviceDiscriminators) {
      String discriminatorName = serviceDiscriminator.getDiscriminatorName();
      Map<String, String> discriminatorValuesMap = new HashMap<String, String>();
      discriminatorValuesMap = ((CloudConnector) connectorManagementService.getServiceInstance(serviceInstanceUUID))
          .getMetadataRegistry().getDiscriminatorValues(discriminatorName);
      Map<String, Object> discriminatorValMap = new HashMap<String, Object>();
      discriminatorValMap.put("name", discriminatorName);
      discriminatorValMap.put("discriminatorValues", discriminatorValuesMap);
      finalMap.put(serviceDiscriminator.getId().toString(), discriminatorValMap);
    }
    new ModelMap();
    Map<String, Object> result = productsController.listDiscriminators(usageTypeId, serviceInstanceUUID);
    Assert.assertEquals(finalMap, result);
    Assert.assertEquals(4, serviceDiscriminators.size());

  }

  /*
   * Description: Test edit selected product. Author: Avinash
   */

  @Test
  public void testEditProduct() throws Exception {
    Product product = productsService.locateProductById("1");
    ModelMap modelmap = new ModelMap();
    productsController.editProduct("1", modelmap);
    Assert.assertNotNull(modelmap);
    ProductForm productForm = new ProductForm(product);
    productForm.setCategoryID(product.getCategory().getId().toString());
    ProductForm pf = (ProductForm) modelmap.get("productForm");
    Assert.assertEquals(productForm.getProduct(), pf.getProduct());

    Set<String> serviceUsageTypeNames = new HashSet<String>();
    Set<String> discrimintaorNames = new HashSet<String>();
    for (ServiceUsageType serviceUsageType : product.getServiceInstance().getService().getServiceUsageTypes()) {
      serviceUsageTypeNames.add(serviceUsageType.getUsageTypeName());
      for (ServiceDiscriminator serviceDiscriminator : product.getServiceInstance().getService()
          .getServiceUsageTypeDiscriminator(serviceUsageType)) {
        discrimintaorNames.add(serviceDiscriminator.getDiscriminatorName());
      }
    }

    Assert.assertEquals(product.getServiceInstance().getService().getServiceName(), modelmap.get("serviceName"));
    Assert.assertEquals(serviceUsageTypeNames, modelmap.get("serviceUsageTypeNames"));
    Assert.assertEquals(discrimintaorNames, modelmap.get("discrimintaorNames"));
    ProductRevision productRevision = productsService.getProductRevision(product, channelService
        .getFutureRevision(null).getStartDate(), null);
    List<MediationRule> mediationRules = productRevision.getMediationRules();
    productsService.getAllCategories();
    Assert.assertEquals(productsService.getAllCategories(), modelmap.get("categories"));

    Map<String, Object> mediationRuleMap = new HashMap<String, Object>();
    Map<String, Object> usageTypeDiscMap = new HashMap<String, Object>();

    for (final MediationRule mediationRule : mediationRules) {
      Map<String, Object> mediationRuleEntitiesMap = new HashMap<String, Object>();
      mediationRuleEntitiesMap.put("usageType", mediationRule.getServiceUsageType().getUsageTypeName());
      mediationRuleEntitiesMap.put("conversionFactor",
          productsService.getConversionFactor(mediationRule.getConversionFactor(), mediationRule.isMonthly()));
      mediationRuleEntitiesMap.put("operator", mediationRule.getOperator().toString().toLowerCase());
      mediationRuleEntitiesMap.put("uom", mediationRule.getServiceUsageType().getServiceUsageTypeUom().getName());
      mediationRuleEntitiesMap.put("productUom", product.getUom());
      mediationRuleEntitiesMap.put("usageTypeId", mediationRule.getServiceUsageType().getId());
      mediationRuleEntitiesMap.put("discrete", mediationRule.getServiceUsageType().getDiscrete());
      Map<String, Object> medDiscsMap = new HashMap<String, Object>();
      for (MediationRuleDiscriminator mediationRuleDiscriminator : mediationRule.getMediationRuleDiscriminators()) {
        Map<String, Object> medRuleDisEntitiesMap = new HashMap<String, Object>();
        medRuleDisEntitiesMap.put("discriminatorType", mediationRuleDiscriminator.getServiceDiscriminator()
            .getDiscriminatorName());
        medRuleDisEntitiesMap.put("discrimniatorValue", mediationRuleDiscriminator.getDiscriminatorValue());
        medRuleDisEntitiesMap.put("operator", mediationRuleDiscriminator.getOperator().toString().toLowerCase());
        medRuleDisEntitiesMap.put("discriminatorTypeId", mediationRuleDiscriminator.getServiceDiscriminator().getId());

        medDiscsMap.put(mediationRuleDiscriminator.getId().toString(), medRuleDisEntitiesMap);
      }

      mediationRuleEntitiesMap.put("discriminators", medDiscsMap);
      mediationRuleMap.put(mediationRule.getId().toString(), mediationRuleEntitiesMap);

      if (medDiscsMap.size() > 0) {
        try {
          final ServiceInstance serviceInstance = product.getServiceInstance();
          final ServiceUsageType serviceUsageType = mediationRule.getServiceUsageType();
          asPortal();
          Map<String, Object> discValueMap = new HashMap<String, Object>();
          Set<ServiceDiscriminator> usageTypeDiscriminators = serviceInstance.getService()
              .getServiceUsageTypeDiscriminator(serviceUsageType);
          for (ServiceDiscriminator usageTypeDiscriminator : usageTypeDiscriminators) {
            Map<String, String> discriminatorValMap = new HashMap<String, String>();
            discriminatorValMap = ((CloudConnector) connectorManagementService.getServiceInstance(serviceInstance
                .getUuid())).getMetadataRegistry()
                .getDiscriminatorValues(usageTypeDiscriminator.getDiscriminatorName());
            // Create map of discriminator to its values
            Map<String, Object> discVals = new HashMap<String, Object>();
            discVals.put("name", usageTypeDiscriminator.getDiscriminatorName());
            discVals.put("discriminatorValues", discriminatorValMap);
            discValueMap.put(usageTypeDiscriminator.getId().toString(), discVals);
          }

          // Add entries to the service Usage type map
          Map<String, Object> usageTypeMap = new HashMap<String, Object>();
          usageTypeMap.put("name", serviceUsageType.getUsageTypeName());
          usageTypeMap.put("discriminators", discValueMap);
          usageTypeDiscMap.put(serviceUsageType.getId().toString(), usageTypeMap);
        } catch (Exception e) {
          logger.error("Error in creating the usage discriminator map...", e);
        }
      } else {
        usageTypeDiscMap.put(mediationRule.getServiceUsageType().getId().toString(), new HashMap<String, Object>());
      }
    }

    String jsonUsageTypeDiscriminatorMap = JSONUtils.toJSONString(usageTypeDiscMap);
    String jsonMediationRuleMap = JSONUtils.toJSONString(mediationRuleMap);
    Assert.assertEquals(jsonUsageTypeDiscriminatorMap, modelmap.get("jsonUsageTypeDiscriminatorMap"));
    Assert.assertEquals(jsonMediationRuleMap, modelmap.get("jsonMediationRuleMap"));

  }

  /*
   * Description: Test Validate product Code Author: Avinash ReviewedBy:Ankit
   */
  @Test
  public void testValidateProductCode() throws Exception {

    new ModelMap();
    // boolean result =
    // productsController.validateProductCode("SYSTEM_VOLUME", "",
    // "compute_1", "trial_camp",
    // "trial_camp", "default", "SERVICE11003fa8ee");
    boolean result = productsController.validateProductCode("CODEsmallRunningVm2Unique", "", "", "", "", "", "");
    Assert.assertTrue(result);
    Assert.assertTrue(result);
    boolean result1 = productsController.validateProductCode("", "UniqueCode1", "", "", "", "", "");
    Assert.assertTrue(result1);
    Assert.assertTrue(result);
    boolean result2 = productsController.validateProductCode("", "", "UniqueCode2", "", "", "", "");
    Assert.assertTrue(result2);
    Assert.assertTrue(result);
    boolean result3 = productsController.validateProductCode("", "", "", "UniqueCode3", "", "", "");
    Assert.assertTrue(result3);
    Assert.assertTrue(result);
    boolean result4 = productsController.validateProductCode("", "", "", "", "UniqueCode4", "", "");
    Assert.assertTrue(result4);
    Assert.assertTrue(result);
    boolean result5 = productsController.validateProductCode("", "", "", "", "", "UniqueCode5", "");
    Assert.assertTrue(result5);
    Assert.assertTrue(result);
    boolean result6 = productsController.validateProductCode("", "", "", "", "", "", "UniqueCode6");
    Assert.assertTrue(result6);

  }

  @Test
  public void testListProducts() {
    try {

      Product product = productDAO.findByCode("SYSTEM_VOLUME");
      BigDecimal productCharge = null;
      List<ProductCharge> productChargesList = productService.getProductCharges(product, new Date());
      for (ProductCharge charge : productChargesList) {
        if (charge.getCurrencyValue().getCurrencyCode().equalsIgnoreCase("SGD")) {
          productCharge = charge.getPrice();
        }
      }

      String contextString = "PSI_UD1=10";
      request.setAttribute("effectiveTenant", tenantDAO.find(3L));
      List<ProductCharge> productCharges = productsController.listProducts(serviceInstanceDAO.find(1L).getUuid(),
          "PSI_RT1", contextString, false, null, null, false, null, null, null, null, request);

      boolean productFound = false;
      for (ProductCharge c : productCharges) {
        if (c.getProduct().equals(product)) {
          productFound = true;
          if (productChargesList.get(0).getCurrencyValue().getCurrencyCode().equalsIgnoreCase("SGD")) {
            Assert.assertEquals(productCharge, c.getPrice());
            break;
          }
        }
      }
      Assert.assertTrue(productFound);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
  }

  @Test
  public void testRetireProduct() {
    Product product = productDAO.find(2L);
    ProductBundle bundle = productBundleDAO.find(2L);
    Entitlement e = new Entitlement();
    e.setCreatedBy(getPortalUser());
    e.setCreatedAt(new Date());
    e.setUpdatedAt(new Date());
    e.setUpdatedBy(getPortalUser());
    e.setProductBundle(bundle);
    e.setProduct(product);
    e.setRevision(channelService.getFutureRevision(null));
    entitlementDAO.save(e);

    String resp = productsController.retireProduct(product.getId().toString(), "true", map);
    Assert.assertEquals("entitlementscheckfailed", resp);
  }

  @Test
  public void testListScales() {
    try {
      String[] serviceUsageTypeUomScale = {
          "TB-MONTHS", "GB-MONTHS", "Bytes", "TB-Days", "GB-Days"
      };

      List<ServiceUsageTypeUomScale> scales = (List<ServiceUsageTypeUomScale>) productsController.listScales(
          serviceDAO.find(6000L).getUuid(), "Bytes", map).get("modified");

      for (int i = 0; i < scales.size(); i++) {
        Assert.assertTrue(scales.get(i).getName().equalsIgnoreCase(serviceUsageTypeUomScale[i]));
      }
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
  }

  /*
   * Description: Test edit product logo Author: Avinash ReviewedBy:Atul
   */
  @Test
  public void testEditProductsLogo() throws Exception {

    Product product = productService.locateProduct("1", false);
    Assert.assertEquals("poli.jpg", product.getImagePath());
    ProductLogoForm form = new ProductLogoForm(product);
    MultipartFile logo = new MockMultipartFile("Product.jpeg", "Product.jpeg", "bytes", "ProductLogo".getBytes());
    form.setLogo(logo);
    BindingResult result = validate(form);
    Assert.assertNotNull(result);
    File currentDirectory = new File(new File(".").getAbsolutePath());
    String currentDirectoryPath = "";
    Configuration rootdir = configurationService
        .locateConfigurationByName(Names.com_citrix_cpbm_portal_settings_images_uploadPath);
    try {
      currentDirectoryPath = currentDirectory.getCanonicalPath();
    } catch (IOException e1) {
      e1.printStackTrace();
      Assert.fail();
    }
    rootdir.setValue(currentDirectoryPath);
    configurationService.update(rootdir);
    Assert.assertEquals("poli.jpg", product.getImagePath());
    productsController.editProductLogo(form, result, request, map);
    product = productService.locateProduct("1", false);
    Assert.assertNotNull(product.getImagePath());
    String productsAbsoluteDir = FilenameUtils.concat(currentDirectoryPath, "products");
    Assert.assertEquals(product.getImagePath().replace("/", "\\"), "products\\1\\Product.jpeg");
    File dir = new File(productsAbsoluteDir);
    try {
      org.apache.commons.io.FileUtils.deleteDirectory(dir);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testEditProduct1() throws Exception {

    Product product = productsService.locateProductByCode("CODEsmallRunningVm2");

    Long discriminatorId = 1L;

    String jsonString = "{conversionFactor: " + 1 + ", operator: COMBINE, usageTypeId: " + 1L;
    if (true) {
      jsonString = jsonString + ", discriminatorVals: [ {discriminatorId: " + discriminatorId
          + ", discriminatorValue: 125, operator: EQUAL_TO, discriminatorValueName: " + "Running VM" + " } ]";
    }
    jsonString = jsonString + " }";
    org.json.JSONObject mediationJason = new org.json.JSONObject(jsonString);
    String productMediationRules = "[" + mediationJason.toString() + "]";

    ServiceInstance serviceInstance = serviceInstanceDao.find("1");
    Service service = serviceInstance.getService();
    Category category = productService.getCategory(1L);

    ProductForm form = new ProductForm(product);
    form.setProductMediationRules(productMediationRules);
    form.setStartDate(new Date());
    form.setServiceUUID(service.getUuid());
    form.setServiceInstanceUUID(serviceInstanceDao.find(1L).getUuid());
    form.setCategoryID(category.getId().toString());
    form.setConversionFactor("1");
    BindingResult result = validate(form);
    Assert.assertNotNull(result);
    String oldDescription = product.getDescription();
    String newDescription = "New Description.";
    product.setDescription(newDescription);
    productsController.editProduct(form, result, map);
    product = productsService.locateProductByCode("CODEsmallRunningVm2");
    Assert.assertEquals(newDescription, product.getDescription());
    Assert.assertNotSame(oldDescription, newDescription);
  }

  /**
   * Description : To test the Procuct price with four digit decimal precision and User should be able to combine or
   * exclude the usage types
   * 
   * @author nageswarap
   */
  @Test
  public void testCreatProductWithChargesAsFourDigitPresision() {

    // Setting Expected Price
    BigDecimal expectedPrice = BigDecimal.valueOf(1.2345);
    // Getting the Service instance and Service UsageTypes
    ServiceInstance serviceInstance = serviceInstanceDAO.find("1");
    Service service = serviceInstance.getService();
    ServiceUsageType runningVmServiceUsageType = null;
    ServiceUsageType allocatedVmServiceUsageType = null;
    List<ServiceUsageType> sutlist = serviceInstance.getService().getServiceUsageTypes();
    for (ServiceUsageType sut : sutlist) {
      if (sut.getUsageTypeName().equalsIgnoreCase("RUNNING_VM")) {
        runningVmServiceUsageType = sut;
        break;
      }
    }
    for (ServiceUsageType sut : sutlist) {
      if (sut.getUsageTypeName().equalsIgnoreCase("ALLOCATED_VM")) {
        allocatedVmServiceUsageType = sut;
        break;
      }
    }

    // creating a product
    Product product = new Product("New", "New_Prod", "New_Prod", "", getRootUser());
    product.setCode("New_Prod_Code");
    product.setServiceInstance(serviceInstanceDAO.find(serviceInstance.getId()));
    product.setUom("Hours");
    Category category = productsService.getCategory(1L);
    product.setCategory(category);

    ProductForm form = new ProductForm(product);
    form.setStartDate(new Date());
    form.setServiceUUID(service.getUuid());
    form.setServiceInstanceUUID(serviceInstanceDAO.find(1L).getUuid());
    form.setCategoryID(category.getId().toString());
    form.setConversionFactor("1");

    List<ProductCharge> productCharges = new ArrayList<ProductCharge>();
    ProductCharge productCharge = new ProductCharge();
    productCharge.setRevision(revisionDAO.getCurrentRevision(null));
    productCharge.setCatalog(null);
    productCharge.setCurrencyValue(currencyValueDAO.find(1L));
    // Setting the product price with four digit product precision
    productCharge.setPrice(expectedPrice);
    productCharge.setCreatedAt(new Date());
    productCharge.setCreatedBy(getRootUser());
    productCharge.setUpdatedBy(getRootUser());
// productsService.saveProductCharge(productCharge);
    productCharges.add(productCharge);

    form.setProductCharges(productCharges);
    form.setIsNewProduct(true);
    String jsonString = "[ {conversionFactor: 1.00, operator: COMBINE, usageTypeId: "
        + runningVmServiceUsageType.getId() + "}, {conversionFactor: 1.00, operator: EXCLUDE, usageTypeId: "
        + allocatedVmServiceUsageType.getId() + "} ]";
    form.setProductMediationRules(jsonString);
    BindingResult result = null;
    try {
      result = validate(form);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail("Failed while validate form with" + e.getMessage());
    }
    Product obtainedProduct = null;
    try {
      obtainedProduct = productsController.createProduct(form, result, map, response, request);
    } catch (JSONException e) {
      e.printStackTrace();
      Assert.fail("Failed while creating product with" + e.getMessage());
    }

    Assert.assertNotNull("create product failed and object is null", obtainedProduct);
    List<ProductCharge> obtainedProductCharges = productsService.getProductCharges(obtainedProduct,
        obtainedProduct.getCreatedAt());
    Assert.assertEquals("the product charges list size is not matching", 1, obtainedProductCharges.size());
    for (ProductCharge pc : obtainedProductCharges) {
      Assert.assertEquals("Price not matching", expectedPrice, pc.getPrice());
      Assert.assertEquals("currency not matching", currencyValueDAO.find(1L), pc.getCurrencyValue());
    }

  }

  /**
   * Description : 1) Test to create a VPN_USERS Product 2) Test to Add VPN Product As Entitilement to a product bundle
   * 
   * @author nageswarap
   */
  @Test
  public void testCreateVPNUSERSProductAndAddAsEntitlement() throws Exception {

    User user = userDAO.find("3");
    user.setProfile(profileDAO.find("7"));
    userDAO.save(user);
    asUser(user);
    String usageType = "VPN_USERS";

    ServiceInstance serviceInstance = serviceInstanceDAO.find("1");
    Service service = serviceInstance.getService();
    ServiceUsageType serviceUsageType = new ServiceUsageType(service, usageType, new ServiceUsageTypeUom("GB-Months",
        service, true));
    service.getServiceUsageTypes().add(serviceUsageType);
    serviceDAO.save(service);
    serviceDAO.flush();
    List<ServiceUsageType> sutlist = serviceInstance.getService().getServiceUsageTypes();
    for (ServiceUsageType sut : sutlist) {
      if (sut.getUsageTypeName().equalsIgnoreCase(usageType)) {
        serviceUsageType = sut;
        break;
      }
    }

    Product obtainedProduct = productCreation("VPN_USERS_Code", true, serviceUsageType.getId(), 1L, "1.00", false, "",
        "");
    Assert.assertNotNull("productCreation returned null ", obtainedProduct);
    Assert.assertEquals("the given product name and obtained product name are not matching", "New_Prod",
        obtainedProduct.getName());

    ProductBundle obtainedBundle = bundleService.locateProductBundleById("3");
    String result1 = bundleController.getFilteredEntitlements(obtainedBundle.getCode(), "planned",
        new Date().toString(), 1, 100, map, request);
    Assert.assertNotNull("getFilteredEntitlements returned null", result1);
    Assert.assertEquals("getFilteredEntitlements not returned the list", "bundle.entitlement.list", result1);
    // verifying the created product new prod is comming in the entitlement list or not
    List<Entitlement> eList = (List<Entitlement>) map.get("entitlements");
    int beforeEntitlementSize = eList.size();
    Product product = productService.locateProductById(obtainedProduct.getId().toString());
    Entitlement entitlement = new Entitlement();
    entitlement.setCreatedAt(new Date());
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
    Assert.assertNotNull("createEntitlement returned null", resultString);
    Assert.assertEquals("createEntitlement not returned success", "success", resultString);
    Entitlement obtainedEntitlement = (Entitlement) map.get("entitlement");
    Assert.assertEquals("product is not added as entitlement", obtainedProduct.getName(), obtainedEntitlement
        .getProduct().getName());
    Assert.assertEquals("product is not added as entitlement", obtainedBundle, obtainedEntitlement.getProductBundle());

    // verify the entitlement is not added to a different bundle
    ProductBundle obtainedBundle1 = bundleService.locateProductBundleById("6");
    Assert.assertNotEquals("Entitlement is added to a wrong bundle", obtainedBundle1,
        obtainedEntitlement.getProductBundle());

    String result2 = bundleController.getFilteredEntitlements(obtainedBundle.getCode(), "planned",
        new Date().toString(), 1, 100, map, request);
    Assert.assertNotNull("getFilteredEntitlements returned null", result2);
    Assert.assertEquals("getFilteredEntitlements not listed the entitlements", "bundle.entitlement.list", result2);
    List<Entitlement> eList1 = (List<Entitlement>) map.get("entitlements");
    int afterEntitlementSize = eList1.size();
    Assert.assertEquals(beforeEntitlementSize + 1, afterEntitlementSize);
    for (Entitlement e : eList1) {
      Assert.assertEquals("Product is not added as entitlement", entitlement.getProduct(), e.getProduct());
      Assert.assertEquals("product usage units are not matching", entitlement.getIncludedUnits(), e.getIncludedUnits());
    }

  }

  /**
   * Description : DE7818
   * 
   * @author nageswarap
   * @throws JSONException
   */
  @Test(expected = ProductServicetException.class)
  public void testCreatProductWithOnlyExcludeAggregation() throws JSONException {

    // Getting the Service instance and Service UsageTypes
    ServiceInstance serviceInstance = serviceInstanceDAO.find("1");
    Service service = serviceInstance.getService();
    ServiceUsageType allocatedVmServiceUsageType = null;
    List<ServiceUsageType> sutlist = serviceInstance.getService().getServiceUsageTypes();
    for (ServiceUsageType sut : sutlist) {
      if (sut.getUsageTypeName().equalsIgnoreCase("ALLOCATED_VM")) {
        allocatedVmServiceUsageType = sut;
        break;
      }
    }

    // creating a product
    Product product = new Product("New", "New_Prod", "New_Prod", "", getRootUser());
    product.setCode("New_Prod_Code");
    product.setServiceInstance(serviceInstanceDAO.find(serviceInstance.getId()));
    product.setUom("Hours");
    Category category = productsService.getCategory(1L);
    product.setCategory(category);

    ProductForm form = new ProductForm(product);
    form.setStartDate(new Date());
    form.setServiceUUID(service.getUuid());
    form.setServiceInstanceUUID(serviceInstanceDAO.find(1L).getUuid());
    form.setCategoryID(category.getId().toString());
    form.setConversionFactor("1");

    List<ProductCharge> productCharges = new ArrayList<ProductCharge>();
    ProductCharge productCharge = new ProductCharge();
    productCharge.setRevision(revisionDAO.getCurrentRevision(null));
    productCharge.setCatalog(null);
    productCharge.setCurrencyValue(currencyValueDAO.find(1L));
    productCharge.setPrice(BigDecimal.valueOf(1.2345));
    productCharge.setCreatedAt(new Date());
    productCharge.setCreatedBy(getRootUser());
    productCharge.setUpdatedBy(getRootUser());
    productCharges.add(productCharge);

    form.setProductCharges(productCharges);
    form.setIsNewProduct(true);
    String jsonString = "[{conversionFactor: 1.00, operator: EXCLUDE, usageTypeId: "
        + allocatedVmServiceUsageType.getId() + "} ]";
    form.setProductMediationRules(jsonString);
    BindingResult result = null;
    try {
      result = validate(form);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail("Failed while validate form with" + e.getMessage());
    }

    int productCountBefore = productDAO.count();

    productsController.createProduct(form, result, map, response, request);

    int productCountAfter = productDAO.count();

    Assert.assertEquals(productCountBefore, productCountAfter);

  }
}

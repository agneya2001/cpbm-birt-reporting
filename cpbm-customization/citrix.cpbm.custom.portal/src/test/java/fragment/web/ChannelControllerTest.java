/*
 * Copyright © 2013 Citrix Systems, Inc. You may not use, copy, or modify this file except pursuant to a valid license
 * agreement from Citrix Systems, Inc.
 */
package fragment.web;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.ExpectedException;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import web.WebTestsBase;
import web.support.MockSessionStatus;

import com.citrix.cpbm.access.proxy.CustomProxy;
import com.citrix.cpbm.core.workflow.event.TriggerTransaction;
import com.citrix.cpbm.platform.admin.service.ConnectorConfigurationManager;
import com.citrix.cpbm.portal.forms.TenantForm;
import com.citrix.cpbm.portal.fragment.controllers.ChannelController;
import com.citrix.cpbm.portal.fragment.controllers.ProductBundlesController;
import com.citrix.cpbm.portal.fragment.controllers.ProductsController;
import com.citrix.cpbm.portal.fragment.controllers.TenantsController;
import com.vmops.event.VerifyEmailRequest;
import com.vmops.model.AccountType;
import com.vmops.model.Address;
import com.vmops.model.BillingGroup;
import com.vmops.model.Catalog;
import com.vmops.model.Channel;
import com.vmops.model.ChannelBrandingConfigurations;
import com.vmops.model.ChannelRevision;
import com.vmops.model.Configuration;
import com.vmops.model.CurrencyValue;
import com.vmops.model.Product;
import com.vmops.model.ProductBundle;
import com.vmops.model.ProductBundleRevision;
import com.vmops.model.ProductCharge;
import com.vmops.model.ProductRevision;
import com.vmops.model.RateCard;
import com.vmops.model.RateCardCharge;
import com.vmops.model.Revision;
import com.vmops.model.ServiceInstance;
import com.vmops.model.ServiceResourceType;
import com.vmops.model.ServiceResourceType.ResourceConstraint;
import com.vmops.model.SupportedCurrency;
import com.vmops.model.Tenant;
import com.vmops.model.User;
import com.vmops.persistence.CatalogProductBundleDAO;
import com.vmops.persistence.ChannelDAO;
import com.vmops.persistence.ChargeRecurrenceFrequencyDAO;
import com.vmops.persistence.RateCardComponentDAO;
import com.vmops.persistence.RevisionDAO;
import com.vmops.persistence.ServiceConfigurationMetaDataDAO;
import com.vmops.persistence.ServiceInstanceDao;
import com.vmops.persistence.ServiceResourceTypeDAO;
import com.vmops.portal.config.Configuration.Names;
import com.vmops.service.ChannelService;
import com.vmops.service.ConfigurationService;
import com.vmops.service.CurrencyValueService;
import com.vmops.service.ProductBundleService;
import com.vmops.service.ProductService;
import com.vmops.service.exceptions.BeanValidationException;
import com.vmops.service.exceptions.ChannelEditFailedException;
import com.vmops.service.exceptions.CurrencyPrecisionException;
import com.vmops.utils.DateTimeUtils;
import com.vmops.utils.DateUtils;
import com.vmops.web.forms.ChannelBrandingForm;
import com.vmops.web.forms.ChannelForm;
import com.vmops.web.forms.ChannelServiceSetting;
import com.vmops.web.forms.ChannelServiceSettingsForm;
import com.vmops.web.forms.ProductBundleForm;
import com.vmops.web.forms.ProductForm;
import com.vmops.web.forms.RateCardChargesForm;
import com.vmops.web.forms.RateCardComponentChargesForm;

import fragment.web.util.WebTestUtils;

@SuppressWarnings({
    "deprecation", "unchecked"
})
public class ChannelControllerTest extends WebTestsBase {

  private ModelMap map;

  private HttpServletResponse response;

  private HttpServletRequest request;

  @Autowired
  private ConnectorConfigurationManager connectorConfigurationManager;

  @Autowired
  private ProductBundlesController bundleController;

  @Autowired
  private ChannelService channelService;

  @Autowired
  private ChannelController channelController;

  @Autowired
  private ProductBundleService bundleService;

  @Autowired
  private CurrencyValueService currencyService;

  @Autowired
  private CatalogProductBundleDAO catalogBundleDAO;

  @Autowired
  private ProductService productService;

  @Autowired
  private RateCardComponentDAO rateCardComponentDAO;

  @Autowired
  private ChannelDAO channelDAO;

  @Autowired
  private ProductBundlesController productBundlesController;

  @Autowired
  private ProductsController productsController;

  @Autowired
  private ServiceInstanceDao serviceInstanceDAO;

  @Autowired
  private ServiceResourceTypeDAO serviceResourceTypeDAO;

  @Autowired
  private ChargeRecurrenceFrequencyDAO chargeRecurrenceFrequencyDAO;

  @Autowired
  private ConfigurationService configurationService;

  @Autowired
  private ServiceConfigurationMetaDataDAO serviceConfigurationMetaDataDAO;

  @Autowired
  RevisionDAO revisionDAO;

  @Autowired
  private TenantsController tenantsController;

  private MockSessionStatus status;

  @Before
  public void init() throws Exception {
    map = new ModelMap();
    response = new MockHttpServletResponse();
    request = new MockHttpServletRequest();
  }

  /*
   * Description: createChannelWithNameAsBlank Author: VeeramaniT
   */
  @Test(expected = BeanValidationException.class)
  public void testCreateChannelWithNameAsBlank() {

    String[] currencyvaluelist = {
        "USD", "EUR"
    };
    channelController.createChannel(WebTestUtils.createChannelForm(null, "Veera", "Veera", null, "en_US",
        "Asia/Kolkata", currencyvaluelist, map, response), null, map, response);
    Assert.fail("Channel is getting created even with blank name");
  }

  /*
   * FIXME Description: createChannelWithCodeAsBlank Author: VeeramaniT
   */
  @Test(expected = BeanValidationException.class)
  public void testCreateChannelWithCodeAsBlank() {
    String[] currencyvaluelist = {
        "USD", "EUR"
    };
    channelController.createChannel(WebTestUtils.createChannelForm("Veera", "Veera", null, null, "en_US",
        "Asia/Kolkata", currencyvaluelist, map, response), null, map, response);
  }

  /*
   * Description: Shouldn't able to Add channel with some Long Character(100) as code Author: VeeramaniT
   */
  @Test(expected = BeanValidationException.class)
  public void testCreateChannelWithCodeAsLongChar() {
    String[] currencyvaluelist = {
        "USD", "EUR"
    };
    String code = "abcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghij";
    channelController.createChannel(WebTestUtils.createChannelForm("Veera", "Veera", code, null, "en_US",
        "Asia/Kolkata", currencyvaluelist, map, response), null, map, response);
    Assert.fail("Channel is getting created even with long channel code");
  }

  /*
   * Description: User Should able to edit channel name field. Author: VeeramaniT
   */
  @Test
  public void testEditChannelName() {

    Channel existingChannel = channelDAO.find("3");
    String result = channelController.editChannel(WebTestUtils.createChannelForm(existingChannel.getId().toString(),
        "NewChannelName", existingChannel.getDescription(), existingChannel.getCode(), null, "en_US", "Asia/Kolkata",
        null, map, response), null, map, response);
    Assert.assertNotNull(result);
    Assert.assertEquals("channels.view", result);
    Channel editedChannel = channelDAO.find("3");
    Assert.assertEquals("NewChannelName", editedChannel.getName());

  }

  @Test
  public void testEditChannelNameWithDefaultConfigSet() {
    Channel existingChannel = channelDAO.find("3");
    Configuration defaultChannConfiguration = configurationService
        .locateConfigurationByName("com.citrix.cpbm.accountManagement.onboarding.default.channel");
    defaultChannConfiguration.setValue(existingChannel.getName());
    configurationService.update(defaultChannConfiguration);
    String result = channelController.editChannel(WebTestUtils.createChannelForm(existingChannel.getId().toString(),
        "NewChannelName", existingChannel.getDescription(), existingChannel.getCode(), null, "en_US", "Asia/Kolkata",
        null, map, response), null, map, response);

    defaultChannConfiguration = configurationService
        .locateConfigurationByName("com.citrix.cpbm.accountManagement.onboarding.default.channel");
    Assert.assertEquals("NewChannelName", defaultChannConfiguration.getValue());

    Assert.assertNotNull(result);
    Assert.assertEquals("channels.view", result);
    Channel editedChannel = channelDAO.find("3");
    Assert.assertEquals("NewChannelName", editedChannel.getName());

  }

  /*
   * Description : User should able to edit channel code field Author : VeeramaniT
   */
  @Test
  public void testEditchannelCode() {
    Channel existingChannel = channelDAO.find("3");
    String result = channelController.editChannel(
        WebTestUtils.createChannelForm(existingChannel.getId().toString(), existingChannel.getName(),
            existingChannel.getDescription(), "NewCode", null, "en_US", "Asia/Kolkata", null, map, response), null,
        map, response);
    Assert.assertNotNull(result);
    Assert.assertEquals("channels.view", result);
    Channel codechannel = channelDAO.find("3");
    Assert.assertEquals("NewCode", codechannel.getCode());
  }

  /*
   * Description : User shouldn't be able to edit a channel with blank name. Author : VeeramaniT
   */
  @Test(expected = Exception.class)
  public void testEditChannelWithBlankName() {
    Channel existingChannel = channelDAO.find("3");
    String result = channelController.editChannel(WebTestUtils.createChannelForm(existingChannel.getId().toString(),
        null, "Veera", "Veera", null, "en_US", "Asia/Kolkata", null, map, response), null, map, response);
    Assert.assertNotNull(result);
  }

  /*
   * Description : User shouldn't be able to edit a channel with blank name, code and description field while saving.
   * Author : VeeramaniT
   */
  @Test(expected = Exception.class)
  public void testEditChannelWithBlankValues() {
    Channel existingChannel = channelDAO.find("3");
    String result = channelController.editChannel(WebTestUtils.createChannelForm(existingChannel.getId().toString(),
        "Veera", null, null, null, "en_US", "Asia/Kolkata", null, map, response), null, map, response);
    Assert.assertNotNull(result);
    Assert.assertEquals("channels.view", result);
  }

  /*
   * Description : User should be able to delete a channel which does not have any account Author : VeeramaniT
   */
  @Test
  public void testDeleteChannelWithNoAccount() {
    String[] currencyValueList = {
        "USD", "EUR"
    };
    Channel obtainedchannel = channelController.createChannel(WebTestUtils.createChannelForm("Veera", "Veera", "Veera",
        null, "en_US", "Asia/Kolkata", currencyValueList, map, response), null, map, response);
    List<Channel> beforechannellist = channelService.getChannels();
    String deleteChannelID = obtainedchannel.getId().toString();
    String result = channelController.deletechannel(deleteChannelID, map);
    Assert.assertNotNull(result);
    Assert.assertEquals("success", result);
    List<Channel> afterchannellist = channelService.getChannels();
    Assert.assertEquals(beforechannellist.size() - 1, afterchannellist.size());

  }

  /*
   * Description : /* Description : User should be able to add one currency to an added channel Author : VeeramaniT
   */
  @Test
  public void testCreateChannelWithOneCurrency() {

    String[] currencyValueList = {
      "USD"
    };
    List<Channel> bchannel = channelService.getChannels();
    Channel obtainedchannel = channelController.createChannel(WebTestUtils.createChannelForm("Veera", "Veera", "Veera",
        null, "en_US", "Asia/Kolkata", currencyValueList, map, response), null, map, response);
    List<Channel> achannel = channelService.getChannels();
    Assert.assertNotNull(obtainedchannel);
    Assert.assertEquals("Veera", obtainedchannel.getName());
    Assert.assertEquals(bchannel.size() + 1, achannel.size());

  }

  /*
   * Description : User should be able to add multiple currencies to an added channel. Author : VeeramaniT
   */
  @Test
  public void testCreateChannelWithMultipleCurrency() {

    String[] currencyValueList = {
        "USD", "EUR", "GBP", "INR"
    };
    List<Channel> bchannel = channelService.getChannels();
    Channel obtainedChannel = channelController.createChannel(WebTestUtils.createChannelForm("Veera", "Veera", "Veera",
        null, "en_US", "Asia/Kolkata", currencyValueList, map, response), null, map, response);
    List<Channel> achannel = channelService.getChannels();
    Assert.assertNotNull(obtainedChannel);
    Assert.assertEquals("Veera", obtainedChannel.getName());
    Assert.assertEquals(bchannel.size() + 1, achannel.size());
  }

  /*
   * Description : Adding published bundle to the channel. Author : Rajkumart
   */
  @Test
  public void testAddpublishedBundleToChannel() throws JSONException {

    Channel obtainedChannel = channelDAO.find(4L);

    ChannelRevision futureChannelRevision = channelService.getFutureChannelRevision(obtainedChannel, false);
    List<String> bundleIdsAlreadyAddedToChannel = new ArrayList<String>();
    for (ProductBundleRevision productBundleRevision : futureChannelRevision.getProductBundleRevisions()) {
      bundleIdsAlreadyAddedToChannel.add(productBundleRevision.getProductBundle().getId().toString());
    }

    String result = channelController.attachProductBundles(obtainedChannel.getId().toString(), "[3]", map);
    Assert.assertEquals("success", result);

    ChannelRevision futureChannelRevision1 = channelService.getFutureChannelRevision(obtainedChannel, false);
    List<String> bundleIdsAlreadyAddedToChannel1 = new ArrayList<String>();
    for (ProductBundleRevision productBundleRevision : futureChannelRevision1.getProductBundleRevisions()) {
      bundleIdsAlreadyAddedToChannel1.add(productBundleRevision.getProductBundle().getId().toString());
    }
    Assert.assertEquals(bundleIdsAlreadyAddedToChannel.size() + 1, bundleIdsAlreadyAddedToChannel1.size());

  }

  /*
   * Description : ChannelFlow. Author : VeeramaniT
   */

  @Test
  public void testChannelFlowAsRoot() throws JSONException {

    // Step1: Creating a new Channel

    String[] currencyValueList = {
        "USD", "EUR", "GBP"
    };
    int beforeChannelCount = channelService.getChannelCount();
    Channel obtainedChannel = channelController.createChannel(WebTestUtils.createChannelForm("Veera", "Veera", "Veera",
        null, "en_US", "Asia/Kolkata", currencyValueList, map, response), null, map, response);
    Assert.assertNotNull(obtainedChannel);
    int afterChannelCount = channelService.getChannelCount();
    Assert.assertEquals(beforeChannelCount + 1, afterChannelCount);

    // Step2 : Attaching bundle to the channels

    String selectedProductBundles = "[2]";
    channelController.attachProductBundles(obtainedChannel.getId().toString(), selectedProductBundles, map);
    channelController.listbundles(obtainedChannel.getId().toString(), map);
    List<ProductBundleRevision> productbundles = (List<ProductBundleRevision>) map.get("productBundles");
    for (ProductBundleRevision productbundleRevision : productbundles) {
      ProductBundle productBundle = productbundleRevision.getProductBundle();
      Assert.assertTrue(productBundle.getId() != 2L);
    }

    // Step 3 : Edit the Bundle pricing in the channel for the added bundle

    String currencyValData = "[{\"previousvalue\":\"0.0000\",\"value\":\"5000\",\"currencycode\":\"EUR\",\"currencyId\":\"44\",\"isRecurring\":\"0\"}]";
    channelController.editCatalogProductBundlePricing(obtainedChannel.getId().toString(), "2", currencyValData, map);
    channelController.getFullChargeListing(obtainedChannel.getId().toString(), "planned", "2", null,
        DateUtils.getSimpleDateString(new java.util.Date()), map);
    Map<ProductBundleRevision, Map<CurrencyValue, Map<String, RateCardCharge>>> fullBundlePricingMap = (Map<ProductBundleRevision, Map<CurrencyValue, Map<String, RateCardCharge>>>) map
        .get("fullBundlePricingMap");

    Assert.assertNotNull(map.get("productBundleRevision"));
    for (Map.Entry<ProductBundleRevision, Map<CurrencyValue, Map<String, RateCardCharge>>> map1 : fullBundlePricingMap
        .entrySet()) {
      for (Map.Entry<CurrencyValue, Map<String, RateCardCharge>> map2 : map1.getValue().entrySet()) {
        CurrencyValue cv = map2.getKey();
        if (cv.getCurrencyCode().equalsIgnoreCase("EUR")) {
          for (Map.Entry<String, RateCardCharge> map3 : map2.getValue().entrySet()) {
            String str = map3.getKey();
            RateCardCharge rcc = map3.getValue();
            if (str.equalsIgnoreCase("catalog-onetime")) {
              Assert.assertEquals(BigDecimal.valueOf(5000), rcc.getPrice());
            }
          }
        }
      }
    }

    // Checking that newly edited price is not affected in bundle price list

    ProductBundle pb = bundleService.getProductBundleById(2L);
    productBundlesController.viewBundlePlannedCharges(pb.getCode(), map, "");
    RateCardChargesForm rateCardChargesForm = (RateCardChargesForm) map.get("rateCardChargesForm");
    List<RateCardComponentChargesForm> rateCardComponentChargesFormList = rateCardChargesForm
        .getNonRecurringRateCardChargesFormList();
    for (RateCardComponentChargesForm rcccform : rateCardComponentChargesFormList) {
      List<RateCardCharge> rccList = rcccform.getCharges();
      for (RateCardCharge rcc : rccList) {
        if (rcc.getCurrencyValue().getCurrencyCode().equalsIgnoreCase("EUR")) {
          Assert.assertTrue(rcc.getPrice() != BigDecimal.valueOf(5000));
        }
      }

    }
  }

  /*
   * Description : User shouldn't be able to edit "Next Planned Charges" date as previous date.. Author : VeeramaniT
   */
  @Test
  public void editNextPlannedChargesDateAsPreviousDate() throws ParseException {

    Channel ch = channelDAO.find("1");
    Date yesterday = DateUtils.minusOneDay(new Date());
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String s = channelController.changePlanDate(ch.getId().toString(), sdf.format(yesterday), "yyyy-MM-dd", map);
    Assert.assertTrue(s.equalsIgnoreCase("failure"));
  }

  /*
   * Description :User shouldn't be able to edit "Next Planned Charges" date as null. Author : VeeramaniT
   */

  @Test(expected = NullPointerException.class)
  public void editNextPlannedChargesDateAsNull() throws ParseException {
    Channel ch1 = channelDAO.find("1");
    channelController.changePlanDate(ch1.getId().toString(), null, "yyyy-MM-dd", map);
  }

  /*
   * Description :User shouldn't be able to edit "Next Planned Charges" date with Invalid format e.g. 1/1/203 Author :
   * VeeramaniT
   */

  @Test
  public void editNextPlannedChargesDateAsInvalid() throws ParseException {
    Channel ch2 = channelDAO.find("1");
    SimpleDateFormat sdf = new SimpleDateFormat("mm-dd");
    try {
      channelController.changePlanDate(ch2.getId().toString(), sdf.format(new Date()), "yyyy-MM-dd", map);
    } catch (Exception e) {
      Assert.assertTrue(e.getMessage().contains("Unparseable date:"));
    }
  }

  /*
   * Description :User should be able to add multiple published bundles to a channel Author : VeeramaniT
   */
  @Test
  public void addMultipleBundleToChannel() throws JSONException {
    String selectedProductBundles = "[2,3,4]";
    Channel ch1 = channelDAO.find("1");
    channelController.attachProductBundles(ch1.getId().toString(), selectedProductBundles, map);
    channelController.listbundles(ch1.getId().toString(), map);
    List<ProductBundleRevision> productbundles = (List<ProductBundleRevision>) map.get("productBundles");
    for (ProductBundleRevision productbundleRevision : productbundles) {
      ProductBundle productBundle = productbundleRevision.getProductBundle();
      Assert.assertTrue(productBundle.getId() != 2L && productBundle.getId() != 3L && productBundle.getId() != 4L);
    }
  }

  /*
   * Description :User shouldn't be able to edit prices with blank price under catalog tab for a channel Author :
   * VeeramaniT
   */
  @Test(expected = NumberFormatException.class)
  public void editChannelPricingWithBlankPrice() throws JSONException {
    String currencyValData = "[{\"previousvalue\":\"0.0000\",\"value\":\"\",\"currencycode\":\"EUR\",\"currencyId\":\"44\",\"isRecurring\":\"0\"}]";

    Channel ch1 = channelDAO.find("1");
    channelController.editCatalogProductBundlePricing(ch1.getId().toString(), "2", currencyValData, map);
  }

  /*
   * Description :User shouldn't be able to edit prices with negative price under catalog tab for a channel Author :
   * VeeramaniT
   */
  @Test
  public void editChannelPricingWithNegativeprice() throws JSONException {

    String currencyValData = "[{\"previousvalue\":\"0.0000\",\"value\":\"-5\",\"currencycode\":\"USD\",\"currencyId\":\"149\",\"isRecurring\":\"0\"}]";

    Channel ch1 = channelDAO.find("3");
    channelController.editCatalogProductBundlePricing(ch1.getId().toString(), "3", currencyValData, map);

  }

  /*
   * Description :As a user I should not able to Add / edit duplicate channel name. Author : VeeramaniT
   */

  @Test(expected = ChannelEditFailedException.class)
  public void editDuplicateChannelName() {
    Channel ch1 = channelDAO.find("3");
    Channel ch2 = channelDAO.find("4");
    String channelName = ch2.getName();
    channelController.editChannel(WebTestUtils.createChannelForm(ch1.getId().toString(), channelName, "Desc_Channel2",
        "Veera", null, "en_US", "Asia/Kolkata", null, map, response), null, map, response);
  }

  /*
   * Description :As a user I should not able to Add / edit duplicate channel Code. Author : VeeramaniT
   */
  @Test(expected = ChannelEditFailedException.class)
  public void editDuplicateChannelCode() {
    Channel ch1 = channelDAO.find("3");
    Channel ch2 = channelDAO.find("4");
    String channelCode = ch2.getCode();
    channelController.editChannel(WebTestUtils.createChannelForm(ch1.getId().toString(), "Veera", "Desc_Channel2",
        channelCode, null, "en_US", "Asia/Kolkata", null, map, response), null, map, response);
  }

  /*
   * Description : ChannelFlowAsproductManager. Author : VeeramaniT
   */

  @Test
  public void testChannelFlowAsProductManager() throws JSONException {

    // Login as Product Manager
    User user = userDAO.find("3");
    user.setProfile(profileDAO.find("7"));
    userDAO.save(user);
    asUser(user);

    // Step1: Creating a new Channel

    String[] currencyValueList = {
        "USD", "EUR", "GBP"
    };
    int beforeChannelCount = channelService.getChannelCount();
    Channel obtainedChannel = channelController.createChannel(WebTestUtils.createChannelForm("Veera", "Veera", "Veera",
        null, "en_US", "Asia/Kolkata", currencyValueList, map, response), null, map, response);
    Assert.assertNotNull(obtainedChannel);
    int afterChannelCount = channelService.getChannelCount();
    Assert.assertEquals(beforeChannelCount + 1, afterChannelCount);
    // Step2 : Attaching bundle to the channels

    String selectedProductBundles = "[2]";
    channelController.attachProductBundles(obtainedChannel.getId().toString(), selectedProductBundles, map);
    channelController.listbundles(obtainedChannel.getId().toString(), map);
    List<ProductBundleRevision> productbundles = (List<ProductBundleRevision>) map.get("productBundles");
    for (ProductBundleRevision productbundleRevision : productbundles) {
      ProductBundle productBundle = productbundleRevision.getProductBundle();
      Assert.assertTrue(productBundle.getId() != 2L);
    }

    // Step 3 : Edit the Bundle pricing in the channel for the added bundle

    String currencyValData = "[{\"previousvalue\":\"0.0000\",\"value\":\"5000\",\"currencycode\":\"EUR\",\"currencyId\":\"44\",\"isRecurring\":\"0\"}]";
    channelController.editCatalogProductBundlePricing(obtainedChannel.getId().toString(), "2", currencyValData, map);
    channelController.getFullChargeListing(obtainedChannel.getId().toString(), "planned", "2", null,
        DateUtils.getSimpleDateString(new java.util.Date()), map);
    Map<ProductBundleRevision, Map<CurrencyValue, Map<String, RateCardCharge>>> fullBundlePricingMap = (Map<ProductBundleRevision, Map<CurrencyValue, Map<String, RateCardCharge>>>) map
        .get("fullBundlePricingMap");

    Assert.assertNotNull(map.get("productBundleRevision"));
    for (Map.Entry<ProductBundleRevision, Map<CurrencyValue, Map<String, RateCardCharge>>> map1 : fullBundlePricingMap
        .entrySet()) {
      for (Map.Entry<CurrencyValue, Map<String, RateCardCharge>> map2 : map1.getValue().entrySet()) {
        CurrencyValue cv = map2.getKey();
        if (cv.getCurrencyCode().equalsIgnoreCase("EUR")) {
          for (Map.Entry<String, RateCardCharge> map3 : map2.getValue().entrySet()) {
            String str = map3.getKey();
            RateCardCharge rcc = map3.getValue();
            if (str.equalsIgnoreCase("catalog-onetime")) {
              Assert.assertEquals(BigDecimal.valueOf(5000), rcc.getPrice());
            }
          }
        }
      }
    }

    // Checking that newly edited price is not affected in bundle price list

    ProductBundle pb = bundleService.getProductBundleById(2L);
    productBundlesController.viewBundlePlannedCharges(pb.getCode(), map, "");
    RateCardChargesForm rateCardChargesForm = (RateCardChargesForm) map.get("rateCardChargesForm");
    List<RateCardComponentChargesForm> rateCardComponentChargesFormList = rateCardChargesForm
        .getNonRecurringRateCardChargesFormList();
    for (RateCardComponentChargesForm rcccform : rateCardComponentChargesFormList) {
      List<RateCardCharge> rccList = rcccform.getCharges();
      for (RateCardCharge rcc : rccList) {
        if (rcc.getCurrencyValue().getCurrencyCode().equalsIgnoreCase("EUR")) {
          Assert.assertTrue(rcc.getPrice() != BigDecimal.valueOf(5000));
        }
      }

    }
  }

  /*
   * Description : As a PM, I should not able to Delete inuse channels. Author : VeeramaniT
   */
  @Test
  public void deleteInUseChannels() {
    // Login as Product Manager

    User user = userDAO.find("3");
    user.setProfile(profileDAO.find("7"));
    userDAO.save(user);
    asUser(user);

    // trying to delete a channel which is in use.

    User us = userDAO.find("3");
    Channel ch = us.getSourceChannel();
    int beforeChannelCount = channelService.getChannelCount();
    String s = channelController.deletechannel(ch.getId().toString(), map);
    int afterChannelCount = channelService.getChannelCount();
    Assert.assertNotNull(s);
    Assert.assertEquals(beforeChannelCount, afterChannelCount);
  }

  /*
   * Description : Newly created channel should list , while adding new tenant account as root user. Author : VeeramaniT
   */
  @Test
  public void newChannelShouldListOnNewAccount() {
    // Create a New Channel
    String[] currencyValueList = {
        "USD", "EUR", "GBP"
    };
    int beforeChannelCount = channelService.getChannelCount();
    Channel obtainedChannel = channelController.createChannel(WebTestUtils.createChannelForm("Veera", "Veera", "Veera",
        null, "en_US", "Asia/Kolkata", currencyValueList, map, response), null, map, response);
    Assert.assertNotNull(obtainedChannel);
    int afterChannelCount = channelService.getChannelCount();
    Assert.assertEquals(beforeChannelCount + 1, afterChannelCount);

    // Create an Account through new Channel

    String masterUsername = "Veeramani";
    User masterUser = new User("Veeramani", " Thamizharasan", " veeramani.thamizharasan@citrix.com", masterUsername,
        "Portal123#", "9535113532", "UTC", null, null, null);
    AccountType accountType = accountTypeDAO.getAccountTypeByName("RETAIL");
    Address address = new Address("123", "street2", "city", "state", "postalCode", "country");
    Tenant tenant = new Tenant(masterUsername, accountType, masterUser, address, true,
        currencyValueService.locateBYCurrencyCode("USD"), masterUser);
    String channelParam = obtainedChannel.getParam();
    tenant.setCreatedAt(new Date());
    tenant.setCreatedBy(userService.getUserByParam("id", 2, false));
    tenant.setUpdatedBy(userService.getUserByParam("id", 2, false));
    String accountTypeId = accountType.getId().toString();
    Tenant tn = tenantService.createAccount(tenant, masterUser, channelParam, accountTypeId);
    Assert.assertEquals(obtainedChannel.getCatalog(), tn.getCatalog());
  }

  /*
   * Description : Add new channel should fail with "no currency" Author : VeeramaniT
   */
  @Test
  public void createChannelWithNoCurrency() {
    channelController.createChannel(
        WebTestUtils.createChannelForm("Veera", "Veera", "Veera", null, "en_US", "Asia/Kolkata", null, map, response),
        null, map, response);
  }

  /*
   * Description : User shouldn't be able to add Channel with blank channel name. Author : VeeramaniT
   */
  @Test(expected = BeanValidationException.class)
  public void addChannelWithBlankChannelName() {
    String[] currencyValueList = {
        "USD", "EUR"
    };
    channelController.createChannel(WebTestUtils.createChannelForm(null, "Veera", "Veera", null, "en_US",
        "Asia/Kolkata", currencyValueList, map, response), null, map, response);
  }

  /*
   * Description : Product Manager shouldn't be able to add unpublished product bundle to an active channel Author :
   * VeeramaniT
   */
  @Test
  public void testProductManagerShouldNotAddUnpublishedBundleToChannel() throws Exception {
    // Login as a product Manager
    User user = userDAO.find("3");
    user.setProfile(profileDAO.find("7"));
    userDAO.save(user);
    asUser(user);
    Channel obtainedChannel = channelDAO.find(4L);
    // Unpublishing a Bundle
    ProductBundle pb = bundleService.locateProductBundleById("2");
    String result = productBundlesController.publishBundle(pb.getCode(), "false", map);
    Assert.assertNotNull(result);
    Assert.assertEquals("success", result);
    Assert.assertEquals(false, pb.getPublish().booleanValue());

    // Attaching unpublished product bundle to the new channels
    String selectedProductBundles = "[" + pb.getId().toString() + "]";
    String result1 = channelController.attachProductBundles(obtainedChannel.getId().toString(), selectedProductBundles,
        map);
    Assert.assertNotNull(result1);
    String listBundles = channelController.listbundles(obtainedChannel.getId().toString(), map);
    Assert.assertNotNull(listBundles);
    List<ProductBundleRevision> productbundles = (List<ProductBundleRevision>) map.get("productBundles");
    for (ProductBundleRevision productbundleRevision : productbundles) {
      ProductBundle productBundle = productbundleRevision.getProductBundle();
      Assert.assertTrue(productBundle.getId() != pb.getId());
    }
  }

  @Test
  public void testEditCatalogProductBundlePricing() {
    Channel channel = channelDAO.find(4L);
    ProductBundle bundle = bundleService.getProductBundleById(2L);
    Boolean found = false;

    channelController.editCatalogProductBundlePricing(channel.getId().toString(), bundle.getId().toString(), map);

    List<CurrencyValue> actualCurrencyList = (List<CurrencyValue>) map.get("supportedCurrencies");
    List<CurrencyValue> supportedCurrencyList = channelService.listCurrencies(channel.getParam());

    for (int i = 0; i < supportedCurrencyList.size(); i++) {
      Assert.assertEquals(supportedCurrencyList.get(i).getCurrencyCode(), actualCurrencyList.get(i).getCurrencyCode());
    }

    Assert.assertEquals(channel, map.get("channel"));

    Map<ProductBundleRevision, Map<CurrencyValue, Map<String, RateCardCharge>>> fullBundlePricingMap = (Map<ProductBundleRevision, Map<CurrencyValue, Map<String, RateCardCharge>>>) map
        .get("fullBundlePricingMap");
    Map<String, RateCardCharge> rateCardMap = fullBundlePricingMap.get(
        bundleService.getProductBundleRevision(bundle, channelService.getFutureRevision(channel).getStartDate(),
            channel)).get(currencyValueService.locateBYCurrencyCode("JPY"));
    for (Map.Entry<String, RateCardCharge> entry : rateCardMap.entrySet()) {
      if (rateCardComponentDAO.find(12L).getId().toString()
          .equals(entry.getValue().getRateCardComponent().getId().toString())) {
        BigDecimal charge = new BigDecimal("400.0000");
        Assert.assertEquals(charge, entry.getValue().getPrice());
        found = true;
        break;
      }
    }
    Assert.assertTrue(found);

    ProductBundleRevision productBundleRevision = (ProductBundleRevision) map.get("productBundleRevision");
    Assert.assertEquals(
        channelService.getFutureChannelRevision(channel, false).getProductBundleRevisionsMap().get(bundle).getId(),
        productBundleRevision.getId());

  }

  @Test
  public void testViewCatalogCurrent() {
    boolean found = false;
    Channel channel = channelDAO.find(4L);
    Product product = productDAO.find(4L);
    CurrencyValue currencyValue = currencyService.locateBYCurrencyCode("JPY");
    ProductBundle bundle = bundleService.getProductBundleById(2L);

    channelController.viewCatalogCurrent(channel.getId().toString(), "1", "5", map);

    Map<Product, Map<CurrencyValue, Map<String, ProductCharge>>> fullProductPricingMap = (Map<Product, Map<CurrencyValue, Map<String, ProductCharge>>>) map
        .get("fullProductPricingMap");
    Map<String, ProductCharge> productChargeMap = fullProductPricingMap.get(product).get(currencyValue);
    for (Map.Entry<String, ProductCharge> entry : productChargeMap.entrySet()) {
      ProductCharge productCharge = entry.getValue();
      if (productCharge.getProduct().compareTo(product) == 0) {
        Assert.assertEquals(productCharge.getProduct().getPrice(), product.getPrice());
        found = true;
      }
    }
    Assert.assertTrue(found);
    found = false;

    Map<ProductBundleRevision, Map<CurrencyValue, Map<String, RateCardCharge>>> fullBundlePricingMap = (Map<ProductBundleRevision, Map<CurrencyValue, Map<String, RateCardCharge>>>) map
        .get("fullBundlePricingMap");
    Map<String, RateCardCharge> rateCardMap = fullBundlePricingMap.get(
        bundleService.getProductBundleRevision(bundle, channelService.getCurrentRevision(channel).getStartDate(),
            channel)).get(currencyValueService.locateBYCurrencyCode("JPY"));
    for (Map.Entry<String, RateCardCharge> entry : rateCardMap.entrySet()) {
      if (rateCardComponentDAO.find(12L).getId().toString()
          .equals(entry.getValue().getRateCardComponent().getId().toString())) {
        BigDecimal charge = new BigDecimal("4000.0000");
        Assert.assertEquals(charge, entry.getValue().getPrice());
        found = true;
        break;
      }
    }
    Assert.assertTrue(found);

    List<CurrencyValue> supportedCurrencies = (List<CurrencyValue>) map.get("supportedCurrencies");
    int index = 0;
    for (CurrencyValue c : channelService.listCurrencies(channel.getParam())) {
      Assert.assertEquals(c.getCurrencyCode(), supportedCurrencies.get(index).getCurrencyCode());
      index++;
    }

    List<ProductBundleRevision> productBundleRevisionList = channelService.getChannelRevision(channel,
        channelService.getCurrentRevision(channel).getStartDate(), false).getProductBundleRevisions();
    List<ProductBundleRevision> actualProductBundleRevisionList = (List<ProductBundleRevision>) map
        .get("productBundleRevisions");
    Assert.assertEquals(productBundleRevisionList.get(0).getId(), actualProductBundleRevisionList.get(0).getId());

    Assert.assertEquals(channel, map.get("channel"));

  }

  @Test
  public void testViewCatalogPlanned() {
    Boolean found = false;
    Channel channel = channelDAO.find(4L);
    Product product = productDAO.find(4L);
    ProductBundle bundle = bundleService.getProductBundleById(2L);
    CurrencyValue currencyValue = currencyService.locateBYCurrencyCode("JPY");

    channelController.viewCatalogPlanned(channel.getId().toString(), "1", "5", null, map);

    List<CurrencyValue> supportedCurrencies = (List<CurrencyValue>) map.get("supportedCurrencies");
    int index = 0;
    for (CurrencyValue c : channelService.listCurrencies(channel.getParam())) {
      Assert.assertEquals(c.getCurrencyCode(), supportedCurrencies.get(index).getCurrencyCode());
      index++;
    }
    Assert.assertEquals(channel, map.get("channel"));

    Map<Product, Map<CurrencyValue, Map<String, ProductCharge>>> fullProductPricingMap = (Map<Product, Map<CurrencyValue, Map<String, ProductCharge>>>) map
        .get("fullProductPricingMap");
    Map<String, ProductCharge> productChargeMap = fullProductPricingMap.get(product).get(currencyValue);
    for (Map.Entry<String, ProductCharge> entry : productChargeMap.entrySet()) {
      ProductCharge productCharge = entry.getValue();
      if (productCharge.getProduct().compareTo(product) == 0) {
        Assert.assertEquals(productCharge.getProduct().getPrice(), product.getPrice());
        found = true;
      }
    }
    Assert.assertTrue(found);

    Map<ProductBundleRevision, Map<CurrencyValue, Map<String, RateCardCharge>>> fullBundlePricingMap = (Map<ProductBundleRevision, Map<CurrencyValue, Map<String, RateCardCharge>>>) map
        .get("fullBundlePricingMap");
    Map<String, RateCardCharge> rateCardMap = fullBundlePricingMap.get(
        bundleService.getProductBundleRevision(bundle, channelService.getFutureRevision(channel).getStartDate(),
            channel)).get(currencyValueService.locateBYCurrencyCode("JPY"));
    for (Map.Entry<String, RateCardCharge> entry : rateCardMap.entrySet()) {
      if (rateCardComponentDAO.find(12L).getId().toString()
          .equals(entry.getValue().getRateCardComponent().getId().toString())) {
        BigDecimal charge = new BigDecimal("400.0000");
        Assert.assertEquals(charge, entry.getValue().getPrice());
        found = true;
        break;
      }
    }
    Assert.assertTrue(found);

  }

  @Test
  public void testList() {
    try {

      String channelName = channelDAO.find(4L).getName();
      org.apache.commons.configuration.Configuration config = new PropertiesConfiguration("pagination.properties");
      int perPage = config.getInt("DEFAULT_PAGE_SIZE");
      List<Channel> channels = channelService.getChannels(1, perPage, channelName.substring(0, 4), null);

      channelController.list("1", channelName.substring(0, 4), null, map);

      Assert.assertEquals(channels, map.get("channels"));
      Assert.assertEquals(channels.size(), map.get("channelsize"));
      Assert.assertEquals(1, map.get("current_page"));

      int totalSize = channelService.count(null, null);
      if (totalSize - (1 * perPage) > 0) {
        Assert.assertTrue((Boolean) map.get("enable_next"));
      } else {
        Assert.assertFalse((Boolean) map.get("enable_next"));
      }

      channelController.searchChannelByPattern("1", channelName.substring(0, 4), null, map);
      Assert.assertEquals(channels, map.get("channels"));
      Assert.assertEquals(channels.size(), map.get("channelsize"));
      Assert.assertEquals(1, map.get("current_page"));

      if (totalSize - (1 * perPage) > 0) {
        Assert.assertTrue((Boolean) map.get("enable_next"));
      } else {
        Assert.assertFalse((Boolean) map.get("enable_next"));
      }

      channels = channelService.getChannels(1, perPage, null, "1");
      channelController.list("1", null, "1", map);
      Assert.assertEquals(channels, map.get("channels"));
      Assert.assertEquals(channels.size(), map.get("channelsize"));

      channels = channelService.getChannels(1, perPage, "fail", "1");
      channelController.list("1", "fail", "1", map);
      Assert.assertEquals(channels, map.get("channels"));
      Assert.assertEquals(channels.size(), map.get("channelsize"));

    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testEditChannel() {
    try {
      Channel channel = channelDAO.find(4L);
      channelController.editChannel(channel.getId().toString(), map);
      Assert.assertEquals(channel, ((ChannelForm) map.get("channelForm")).getChannel());
      Assert.assertEquals(channelService.listCurrencies(channel.getParam()), map.get("currencies"));
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testEditChannelPost() {
    try {
      Channel channel = channelDAO.find(4L);
      String channelStr = "channel988";
      channelController.editChannel(WebTestUtils.createChannelForm(channel.getId().toString(), channelStr, channelStr,
          channelStr, null, "en_US", "Asia/Kolkata", null, map, response), null, map, response);
      channel = channelDAO.find(4L);
      Assert.assertEquals(channelStr, channel.getName());
      Assert.assertEquals(channelStr, channel.getCode());
      Assert.assertEquals(channelStr, channel.getDescription());
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testCreateChannelGet() {
    try {
      Boolean found = false;
      channelController.createChannel(map, response);

      Assert.assertNotSame(HttpStatus.PRECONDITION_FAILED.value(), response.getStatus());

      List<Channel> channelList = (List<Channel>) map.get("channels");
      for (Channel channel : channelList) {
        if (channelDAO.find(4L).getCode().equals(channel.getCode())) {
          found = true;
        }

      }
      Assert.assertTrue(found);
      Assert.assertEquals(currencyValueService.listActiveCurrencies(), map.get("currencies"));
      Assert.assertEquals(config.getPublicHost(), map.get("publicHost"));
      Assert.assertEquals(config.getPublicProtocol(), map.get("publicProtocol"));
      Assert.assertEquals(config.getPublicPort(), map.get("publicPort"));

      Assert.assertEquals(config.getValue(Names.com_citrix_cpbm_portal_marketing_support_url),
          map.get("marketing_support_url"));

      Assert.assertEquals(config.getValue(Names.com_citrix_cpbm_portal_marketing_blog_url),
          map.get("marketing_blog_url"));

      Assert.assertEquals(config.getValue(Names.com_citrix_cpbm_portal_marketing_forum_url),
          map.get("marketing_forum_url"));

      Assert.assertEquals(config.getValue(Names.com_citrix_cpbm_portal_marketing_contact_url),
          map.get("marketing_contact_url"));

      Assert.assertEquals(config.getValue(Names.com_citrix_cpbm_portal_marketing_privacy_url),
          map.get("marketing_privacy_url"));

      Assert.assertEquals(config.getValue(Names.com_citrix_cpbm_portal_marketing_help_url),
          map.get("marketing_help_url"));

      Assert
          .assertEquals(config.getValue(Names.com_citrix_cpbm_portal_marketing_tou_url), map.get("marketing_tou_url"));

      Assert.assertEquals(DateTimeUtils.getAvailableTimeZones(), map.get("supportedZoneList"));

      Assert.assertEquals(config.getDefaultLocale(), map.get("defaultLocale"));

      Assert.assertEquals(config.getValue(Names.com_citrix_cpbm_portal_settings_default_timezone),
          map.get("defaultTimeZone"));

      Assert.assertEquals(config.getValue(Names.com_citrix_cpbm_portal_settings_helpdesk_phone),
          map.get("help_desk_phone"));

      Assert.assertEquals(config.getValue(Names.com_citrix_cpbm_portal_addressbook_helpDeskEmail),
          map.get("help_desk_email"));
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testCreateChannelWithExistingChannelCode() {
    try {
      Channel channel = channelDAO.find(4L);
      String[] currencyValues = {
          currencyValueService.locateBYCurrencyCode("USD").getCurrencyName(),
          currencyValueService.locateBYCurrencyCode("INR").getCurrencyName()
      };
      channelController.createChannel(WebTestUtils.createChannelForm(channel.getCode(), channel.getCode(),
          channel.getCode(), null, "en_US", "Asia/Kolkata", currencyValues, map, response), null, map, response);
      Assert.assertEquals(200, response.getStatus());

    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testValidateChannelName() {
    try {
      Channel channel = channelDAO.find(4L);
      String result = channelController.validateChannelName(channel.getName());
      Assert.assertFalse(Boolean.valueOf(result));

      String channelStr = "channel9";
      result = channelController.validateChannelName(channelStr);
      Assert.assertTrue(Boolean.valueOf(result));

    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testEditChannelCurrencyGet() {
    try {
      Channel channel = channelDAO.find(4L);
      channelController.editChannelCurrency(channel.getId().toString(), map);

      List<CurrencyValue> availableCurrencies = (List<CurrencyValue>) map.get("availableCurrencies");
      int size = availableCurrencies.size();
      List<CurrencyValue> currencyList = channelService.listCurrencies(channel.getParam());
      availableCurrencies.removeAll(currencyList);
      Assert.assertEquals(size, availableCurrencies.size());

    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testEditChannelCurrencyPost() {
    try {
      Channel channel = channelDAO.find(4L);

      channelController.editChannelCurrency(channel.getId().toString(), map);
      List<CurrencyValue> availableCurrencies = (List<CurrencyValue>) map.get("availableCurrencies");
      String currencyCode = availableCurrencies.get(0).getCurrencyCode();
      String currencyCodeArray = "['" + currencyCode + "']";

      String status = channelController.editChannelCurrency(channel.getId().toString(), currencyCodeArray, map);
      Assert.assertEquals("success", status);
      Assert.assertTrue(channel.getCatalog().getSupportedCurrencyValuesByOrder()
          .contains(currencyValueService.locateBYCurrencyCode(currencyCode)));

    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testEditChannelCurrencyPostNegative() {
    try {
      Channel channel = channelDAO.find(4L);

      channelController.editChannelCurrency(channel.getId().toString(), map);
      List<CurrencyValue> availableCurrencies = (List<CurrencyValue>) map.get("availableCurrencies");
      String activeCurrencyCode = availableCurrencies.get(0).getCurrencyCode();

      List<CurrencyValue> activeCurrencyList = currencyService.listActiveCurrencies();
      List<CurrencyValue> currencyList = currencyService.getCurrencyValues();
      currencyList.removeAll(activeCurrencyList);
      String currencyCodeArray = "['" + currencyList.get(0).getCurrencyCode() + "','" + activeCurrencyCode + "']";

      String status = channelController.editChannelCurrency(channel.getId().toString(), currencyCodeArray, map);
      Assert.assertEquals("success", status);
      Assert.assertFalse(channel.getCatalog().getSupportedCurrencyValuesByOrder()
          .contains(currencyValueService.locateBYCurrencyCode(currencyList.get(0).getCurrencyCode())));
      Assert.assertTrue(channel.getCatalog().getSupportedCurrencyValuesByOrder()
          .contains(currencyValueService.locateBYCurrencyCode(activeCurrencyCode)));

    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testEditCatalogProductPricing() {
    try {
      Channel channel = channelDAO.find(4L);
      Boolean found = false;
      Product product = productDAO.find(4L);
      CurrencyValue currencyValue = currencyService.locateBYCurrencyCode("JPY");

      channelController.editCatalogProductPricing(channel.getId().toString(), map);

      Assert.assertEquals(channelService.getFutureRevision(channel).getStartDate(), map.get("planDate"));
      Assert.assertEquals(channel.getCatalog().getSupportedCurrencyValuesByOrder(), map.get("supportedCurrencies"));

      Map<Product, Map<CurrencyValue, Map<String, ProductCharge>>> fullProductPricingMap = (Map<Product, Map<CurrencyValue, Map<String, ProductCharge>>>) map
          .get("fullProductPricingMap");
      Map<String, ProductCharge> productChargeMap = fullProductPricingMap.get(product).get(currencyValue);
      for (Map.Entry<String, ProductCharge> entry : productChargeMap.entrySet()) {
        ProductCharge productCharge = entry.getValue();
        if (productCharge.getProduct().compareTo(product) == 0) {
          Assert.assertEquals(productCharge.getProduct().getPrice(), product.getPrice());
          found = true;
        }
      }
      Assert.assertTrue(found);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @ExpectedException(CurrencyPrecisionException.class)
  @Test
  public void testEditCatalogProductPricingWithUnCompatibleprecision() {
    Channel channel = channelDAO.find(4L);
    Configuration precision = configurationDAO.findByName("com_citrix_cpbm_portal_appearance_currency_precision"
        .replace('_', '.'));
    precision.setValue("3");
    configurationDAO.save(precision);

    List<ProductRevision> catalogProductRevisions = channelService.getChannelRevision(channel,
        channelService.getFutureRevision(channel).getStartDate(), false).getProductRevisions();

    for (ProductRevision productRevision : catalogProductRevisions) {
      for (ProductCharge productCharge : productRevision.getProductCharges()) {
        productCharge.setPrice(new BigDecimal("3.1234"));

      }
    }
    channelController.editCatalogProductPricing(channel.getId().toString(), map);
  }

  @ExpectedException(CurrencyPrecisionException.class)
  @Test
  public void testEditCatalogProductBundlePricingWithUnCompatibleprecision() {
    Channel channel = channelDAO.find(4L);
    Configuration precision = configurationDAO.findByName("com_citrix_cpbm_portal_appearance_currency_precision"
        .replace('_', '.'));
    precision.setValue("3");
    configurationDAO.save(precision);

    ProductBundle bundle = bundleService.getProductBundleById(2L);
    List<ProductBundleRevision> catalogProductBundleRevisions = new ArrayList<ProductBundleRevision>();

    catalogProductBundleRevisions = channelService.getChannelRevision(channel,
        channelService.getFutureRevision(channel).getStartDate(), false).getProductBundleRevisions();

    ProductBundleRevision catalaogProductBundleRevision = catalogProductBundleRevisions.get(0);

    for (RateCardCharge rcc : catalaogProductBundleRevision.getRateCardCharges()) {
      rcc.setPrice(new BigDecimal("4.1234"));
    }
    channelController.editCatalogProductBundlePricing(channel.getId().toString(), bundle.getId().toString(), map);

  }

  @Test
  public void testEditCatalogProductPricingPost() {
    try {
      Channel channel = channelDAO.find(4L);
      Product product = productDAO.find(4L);
      String currencyValData = "[{'previousvalue':'14.0000','value':'200.0000','currencycode':'JPY','currencyId':'71','productId':'4'}]";
      channelController.editCatalogProductPricing(channel.getId().toString(), currencyValData, map);

      boolean productChargeSet = false;
      List<ProductCharge> prodCharges = productService.getCatalogPlannedChargesForAllProducts(channel.getCatalog());
      for (ProductCharge productCharge : prodCharges) {
        if (productCharge.getProduct().equals(product)
            && productCharge.getCurrencyValue().getCurrencyCode().equals("JPY")) {
          productChargeSet = true;
          Assert.assertEquals(0, productCharge.getPrice().compareTo(BigDecimal.valueOf(200.00)));
        }
      }
      Assert.assertTrue(productChargeSet);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testShowDatePicker() {
    try {
      Channel channel = channelDAO.find(4L);
      Revision futureRevision = channelService.getFutureRevision(channel);

      channelController.showDatePicker(channel.getId().toString(), map);

      if (futureRevision != null && futureRevision.getStartDate() != null
          && futureRevision.getStartDate().after(new Date())) {
        Assert.assertEquals(true, map.get("planDateInFuture"));
        Assert.assertEquals(futureRevision.getStartDate(), map.get("plan_date"));
      }

      Assert.assertTrue(DateUtils.isSameDay(DateUtils.addOneDay(new Date()), (Date) map.get("date_tomorrow")));
      Assert.assertEquals(new Date().getTime() / 1000, ((Date) map.get("date_today")).getTime() / 1000);
      Assert.assertEquals(channel, map.get("channel"));
      Assert.assertEquals(true, map.get("isTodayAllowed"));

    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testGetNextSetOfBundles() {
    boolean found = false;
    Channel channel = channelDAO.find(4L);
    ProductBundle bundle = bundleService.getProductBundleById(2L);

    channelController.getNextSetOfBundles(channel.getId().toString(), "5", "planned", "1", map);

    List<CurrencyValue> currencyList = channelService.listCurrencies(channel.getParam());

    List<SupportedCurrency> supportedCurrencyList = (List<SupportedCurrency>) map.get("supportedCurrencies");
    Assert.assertEquals(currencyList.size(), supportedCurrencyList.size());

    Assert.assertTrue((Boolean) map.get("toalloweditprices"));
    Assert.assertEquals(1, map.get("actiontoshow"));
    Assert.assertEquals(channel, map.get("channel"));

    Map<ProductBundleRevision, Map<CurrencyValue, Map<String, RateCardCharge>>> fullBundlePricingMap = (Map<ProductBundleRevision, Map<CurrencyValue, Map<String, RateCardCharge>>>) map
        .get("fullBundlePricingMap");
    ProductBundleRevision productBundleRevision = bundleService.getProductBundleRevision(bundle, channelService
        .getFutureRevision(channel).getStartDate(), channel);
    Map<String, RateCardCharge> rateCardMap = fullBundlePricingMap.get(productBundleRevision).get(
        currencyValueService.locateBYCurrencyCode("JPY"));
    for (Map.Entry<String, RateCardCharge> entry : rateCardMap.entrySet()) {
      if (rateCardComponentDAO.find(12L).getId().toString()
          .equals(entry.getValue().getRateCardComponent().getId().toString())) {
        BigDecimal charge = new BigDecimal("400.0000");
        Assert.assertEquals(charge, entry.getValue().getPrice());
        found = true;
        break;
      }
    }
    Assert.assertTrue(found);
  }

  @Test
  public void testViewCatalogHistory() {
    try {
      boolean found = false;
      Channel channel = channelDAO.find(4L);
      Product product = productDAO.find(4L);
      CurrencyValue currencyValue = currencyService.locateBYCurrencyCode("JPY");

      channelController.viewCatalogHistory(channel.getId().toString(), "30/04/2012", "MM/dd/yyyy", "true", map);

      Map<Product, Map<CurrencyValue, Map<String, ProductCharge>>> fullProductPricingMap = (Map<Product, Map<CurrencyValue, Map<String, ProductCharge>>>) map
          .get("fullProductPricingMap");
      Map<String, ProductCharge> productChargeMap = fullProductPricingMap.get(product).get(currencyValue);
      for (Map.Entry<String, ProductCharge> entry : productChargeMap.entrySet()) {
        ProductCharge productCharge = entry.getValue();
        if (productCharge.getProduct().compareTo(product) == 0) {
          Assert.assertEquals(productCharge.getProduct().getPrice(), product.getPrice());
          found = true;
        }
      }
      Assert.assertTrue(found);
      found = false;

      Map<ProductBundleRevision, Map<CurrencyValue, Map<String, RateCardCharge>>> fullBundlePricingMap = (Map<ProductBundleRevision, Map<CurrencyValue, Map<String, RateCardCharge>>>) map
          .get("fullBundlePricingMap");
      BigDecimal charge = new BigDecimal("4000.0000");
      for (Map.Entry<ProductBundleRevision, Map<CurrencyValue, Map<String, RateCardCharge>>> bundlepricemap : fullBundlePricingMap
          .entrySet()) {
        for (Map.Entry<CurrencyValue, Map<String, RateCardCharge>> map2 : bundlepricemap.getValue().entrySet()) {
          CurrencyValue cv = map2.getKey();
          if (cv.getCurrencyCode().equalsIgnoreCase("JPY")) {
            for (Map.Entry<String, RateCardCharge> entry : map2.getValue().entrySet()) {
              if (rateCardComponentDAO.find(12L).getId().toString()
                  .equals(entry.getValue().getRateCardComponent().getId().toString())) {
                Assert.assertEquals(charge, entry.getValue().getPrice());
                found = true;
                break;
              }
            }
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
  public void testsyncChannel() {
    try {
      Channel channel = channelDAO.find(4L);
      Product product = productDAO.find(4L);

      productService.removeProductById(product.getId().toString(), true);

      productService.setReferencePriceBookFutureRevisionDate(new Date());
      Assert.assertTrue(DateUtils.isSameDay(new Date(), channelService.getCurrentRevision(null).getStartDate()));

      channelController.syncChannel(channel.getId().toString(), map);

      Boolean found = false;
      List<ProductRevision> catalogProductRevisions = channelService.getFutureChannelRevision(channel, false)
          .getProductRevisions();
      for (ProductRevision productRevision : catalogProductRevisions) {
        for (ProductCharge productCharge : productRevision.getProductCharges()) {
          if (productCharge.getProduct().getCode().equals(product.getCode())) {
            Assert.fail();
            found = true;
          }
        }
      }
      Assert.assertFalse(found);

    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testCurrentGetFullChargeListing() {
    try {
      boolean found = false;
      Channel channel = channelDAO.find(4L);
      Product product = productDAO.find(4L);
      CurrencyValue currencyValue = currencyService.locateBYCurrencyCode("JPY");

      channelController.getFullChargeListing(channel.getId().toString(), "current", null, "MM/dd/yyyy", "05/02/2012",
          map);

      Map<Product, Map<CurrencyValue, Map<String, ProductCharge>>> fullProductPricingMap = (Map<Product, Map<CurrencyValue, Map<String, ProductCharge>>>) map
          .get("fullProductPricingMap");
      Map<String, ProductCharge> productChargeMap = fullProductPricingMap.get(product).get(currencyValue);
      for (Map.Entry<String, ProductCharge> entry : productChargeMap.entrySet()) {
        ProductCharge productCharge = entry.getValue();
        if (productCharge.getProduct().compareTo(product) == 0) {
          Assert.assertEquals(product.getPrice(), productCharge.getProduct().getPrice());
          found = true;
        }
      }
      Assert.assertTrue(found);
      Assert.assertNull(map.get("fullBundlePricingMap"));
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testPlannedGetFullChargeListing() {
    try {
      boolean found = false;
      Channel channel = channelDAO.find(4L);
      Product product = productDAO.find(4L);
      CurrencyValue currencyValue = currencyService.locateBYCurrencyCode("JPY");

      String currencyValData = "[{'previousvalue':'14.0000','value':'200.0000','currencycode':'JPY','currencyId':'71','productId':'4'}]";
      channelController.editCatalogProductPricing(channel.getId().toString(), currencyValData, map);

      channelController.getFullChargeListing(channel.getId().toString(), "planned", null, "MM/dd/yyyy", "05/02/2012",
          map);

      Map<Product, Map<CurrencyValue, Map<String, ProductCharge>>> fullProductPricingMap = (Map<Product, Map<CurrencyValue, Map<String, ProductCharge>>>) map
          .get("fullProductPricingMap");
      Map<String, ProductCharge> productChargeMap = fullProductPricingMap.get(product).get(currencyValue);
      for (Map.Entry<String, ProductCharge> entry : productChargeMap.entrySet()) {
        ProductCharge productCharge = entry.getValue();
        if (productCharge.getProduct().compareTo(product) == 0
            && productCharge.getCurrencyValue().getCurrencyCode().equals("JPY")
            && channel.getCatalog().equals(productCharge.getCatalog())) {
          Assert.assertEquals(new BigDecimal("200.0000"), productCharge.getPrice());
          found = true;
        }
      }
      Assert.assertTrue(found);
      Assert.assertNull(map.get("fullBundlePricingMap"));
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testSaveChannelSettings() {
    Channel channel = channelDAO.find(13L);
    ChannelServiceSettingsForm channelServiceSettingsForm = new ChannelServiceSettingsForm();
    channelServiceSettingsForm.setChannelId(channel.getId().toString());
    ChannelServiceSetting channelServiceSetting1 = new ChannelServiceSetting("channelSettings2", "values1", 81L);
    ChannelServiceSetting channelServiceSetting2 = new ChannelServiceSetting("channelSettings1", "values2", 82L);
    List<ChannelServiceSetting> channelServiceSettings = new ArrayList<ChannelServiceSetting>();
    channelServiceSettings.add(channelServiceSetting1);
    channelServiceSettings.add(channelServiceSetting2);
    channelServiceSettingsForm.setChannelServiceSettings(channelServiceSettings);
    channelServiceSettingsForm.setMode("create");
    channelServiceSettingsForm.setServiceInstanceUUID("3c34c33e-7a28-4791-b31e-417925ede02a");
    String result = channelController.saveChannelSettings(channelServiceSettingsForm, map);
    Assert.assertTrue(StringUtils.isNotEmpty(result));
    Assert.assertEquals("success", result);
    channelServiceSettingsForm.setMode("NotCreate");
    result = channelController.saveChannelSettings(channelServiceSettingsForm, map);
    Assert.assertTrue(StringUtils.isNotEmpty(result));
    Assert.assertEquals("success", result);

  }

  @Test
  public void testEditChannelSettings() {
    Channel channel = channelDAO.find(13L);
    ChannelServiceSettingsForm channelServiceSettingsForm = new ChannelServiceSettingsForm();
    channelServiceSettingsForm.setChannelId(channel.getId().toString());
    ChannelServiceSetting channelServiceSetting1 = new ChannelServiceSetting("channelSettings2", "values1", 81L);
    ChannelServiceSetting channelServiceSetting2 = new ChannelServiceSetting("channelSettings1", "values2", 82L);
    List<ChannelServiceSetting> channelServiceSettings = new ArrayList<ChannelServiceSetting>();
    channelServiceSettings.add(channelServiceSetting1);
    channelServiceSettings.add(channelServiceSetting2);
    channelServiceSettingsForm.setChannelServiceSettings(channelServiceSettings);
    channelServiceSettingsForm.setMode("create");
    channelServiceSettingsForm.setServiceInstanceUUID("3c34c33e-7a28-4791-b31e-417925ede02a");
    String result = channelController.saveChannelSettings(channelServiceSettingsForm, map);
    Assert.assertTrue(StringUtils.isNotEmpty(result));
    Assert.assertEquals("success", result);
    channelServiceSetting1 = new ChannelServiceSetting("channelSettings2", "changevalues1", 81L);
    channelServiceSetting2 = new ChannelServiceSetting("channelSettings1", "changevalues2", 82L);
    result = channelController.editChannelSettings(channelServiceSettingsForm, channel.getId().toString(),
        "3c34c33e-7a28-4791-b31e-417925ede02a", map);
    Assert.assertTrue(StringUtils.isNotEmpty(result));
    Assert.assertEquals("channel.service.edit.settings", result);
  }

  @Test
  public void testGetChannelSettings() {
    Channel channel = channelDAO.find(13L);
    ChannelServiceSettingsForm channelServiceSettingsForm = new ChannelServiceSettingsForm();
    channelServiceSettingsForm.setChannelId(channel.getId().toString());
    ChannelServiceSetting channelServiceSetting1 = new ChannelServiceSetting("channelSettings2", "values1", 81L);
    ChannelServiceSetting channelServiceSetting2 = new ChannelServiceSetting("channelSettings1", "values2", 82L);
    List<ChannelServiceSetting> channelServiceSettings = new ArrayList<ChannelServiceSetting>();
    channelServiceSettings.add(channelServiceSetting1);
    channelServiceSettings.add(channelServiceSetting2);
    channelServiceSettingsForm.setChannelServiceSettings(channelServiceSettings);
    channelServiceSettingsForm.setMode("create");
    channelServiceSettingsForm.setServiceInstanceUUID("3c34c33e-7a28-4791-b31e-417925ede02a");
    String result = channelController.saveChannelSettings(channelServiceSettingsForm, map);
    Assert.assertTrue(StringUtils.isNotEmpty(result));
    Assert.assertEquals("success", result);
    channelServiceSettingsForm.setMode("NotCreate");
    result = channelController.saveChannelSettings(channelServiceSettingsForm, map);
    Assert.assertTrue(StringUtils.isNotEmpty(result));
    Assert.assertEquals("success", result);
    result = channelController.getChannelSettings(channelServiceSettingsForm, channel.getId().toString(),
        "3c34c33e-7a28-4791-b31e-417925ede02a", map);
    Assert.assertTrue(StringUtils.isNotEmpty(result));
    Assert.assertEquals("channel.service.settings", result);
    Assert.assertTrue(MapUtils.isNotEmpty(map));
    Assert.assertEquals(1, map.get("serviceSettingsCount"));
    Assert.assertEquals(channel.getId().toString(), map.get("serviceSettingsChanelID"));
    Assert.assertEquals("3c34c33e-7a28-4791-b31e-417925ede02a", map.get("serviceSettingsInstanceUUID"));

  }

  @Test
  public void testNegativeGetChannelSettings() {
    Channel channel = channelDAO.find(13L);
    ChannelServiceSettingsForm channelServiceSettingsForm = new ChannelServiceSettingsForm();
    channelServiceSettingsForm.setChannelId(channel.getId().toString());
    ChannelServiceSetting channelServiceSetting1 = new ChannelServiceSetting("channelSettings2", "values1", 81L);
    ChannelServiceSetting channelServiceSetting2 = new ChannelServiceSetting("channelSettings1", "values2", 82L);
    List<ChannelServiceSetting> channelServiceSettings = new ArrayList<ChannelServiceSetting>();
    channelServiceSettings.add(channelServiceSetting1);
    channelServiceSettings.add(channelServiceSetting2);
    channelServiceSettingsForm.setChannelServiceSettings(channelServiceSettings);
    channelServiceSettingsForm.setMode("create");
    channelServiceSettingsForm.setServiceInstanceUUID("3c34c33e-7a28-4791-b31e-417925ede02a");
    String result = channelController.saveChannelSettings(channelServiceSettingsForm, map);
    Assert.assertTrue(StringUtils.isNotEmpty(result));
    Assert.assertEquals("success", result);
    channelServiceSettingsForm.setMode("NotCreate");
    result = channelController.saveChannelSettings(channelServiceSettingsForm, map);
    Assert.assertTrue(StringUtils.isNotEmpty(result));
    Assert.assertEquals("success", result);
    ChannelServiceSettingsForm channelServiceSettingsNewForm = new ChannelServiceSettingsForm();
    result = channelController.getChannelSettings(channelServiceSettingsNewForm, channel.getId().toString(),
        "003fa8ee-fba3-467f-a517-ed806dae8a89", map);
    Assert.assertTrue(StringUtils.isNotEmpty(result));
    Assert.assertEquals("channel.service.settings", result);
    Assert.assertTrue(MapUtils.isNotEmpty(map));
    Assert.assertEquals(0, map.get("serviceSettingsCount"));
  }

  /**
   * @Desc Test to check channel creation fails with Duplicate channel code
   * @author vinayv
   */
  @Test
  public void testCreateChannelWithDuplicateCode() {
    logger.info("Entering testCreateChannelWithDuplicateCode test");
    int beforeCount = channelDAO.count();
    String[] currencyvaluelist = {
        "USD", "EUR"
    };
    Channel channel = channelController.createChannel(WebTestUtils.createChannelForm("channelName", "channelDesc",
        channelService.getChannelById("1").getCode(), null, "en_US", "Asia/Kolkata", currencyvaluelist, map, response),
        null, map, response);
    Assert.assertNull(channel);
    Assert.assertEquals(200, response.getStatus());
    int afterCount = channelDAO.count();
    Assert.assertEquals(beforeCount, afterCount);
    logger.info("Exiting testCreateChannelWithDuplicateCode test");
  }

  @Test
  public void testUploadChannelLogo() throws Exception {

    setConfiguration(Names.com_citrix_cpbm_portal_settings_images_uploadPath, "src\\test\\resources");
    String fileName = "logo_file.jpg";
    ChannelBrandingForm channelBrandingForm = new ChannelBrandingForm();

    MultipartFile logo = new MockMultipartFile(fileName, fileName, "bytes", fileName.getBytes());

    channelBrandingForm.setLogo(logo);
    channelBrandingForm.setChannelId("1");
    channelBrandingForm.setPublish(false);
    BindingResult result = validate(channelBrandingForm);
    ResponseEntity<String> actualResult = channelController.editChannelBranding(channelBrandingForm, result, request,
        response, map);
    JSONObject actualResultMap = new JSONObject(actualResult.getBody());
    Assert.assertEquals("logo_file.jpg", actualResultMap.get("unpublished_logo"));
    Assert.assertEquals("poli.jpg", actualResultMap.get("favicon"));
    Assert.assertEquals(JSONObject.NULL, actualResultMap.get("css"));

    Channel channel = channelService.getChannelById("1");

    Assert.assertEquals(fileName, channel.getUnPublishedChannelBrandingConfigurations().getLogoImageFileName());
    Assert.assertNull(channel.getUnPublishedChannelBrandingConfigurations().getFavIconImageFileName());
    Assert.assertNull(channel.getUnPublishedChannelBrandingConfigurations().getCssFileName());

    map = new ModelMap();
    channelController.editChannelBranding(channel.getId().toString(), map);
    channelBrandingForm = (ChannelBrandingForm) map.get("channelBrandingForm");

    Assert.assertEquals(fileName, channelBrandingForm.getChannel().getUnPublishedChannelBrandingConfigurations()
        .getLogoImageFileName());

  }

  @Test
  public void testUploadChannelFavicon() throws Exception {
    setConfiguration(Names.com_citrix_cpbm_portal_settings_images_uploadPath, "src\\test\\resources");
    ChannelBrandingForm channelBrandingForm = new ChannelBrandingForm();

    String fileName = "fav_file.ico";
    MultipartFile favicon = new MockMultipartFile(fileName, fileName, "bytes", fileName.getBytes());

    channelBrandingForm.setFavicon(favicon);
    channelBrandingForm.setChannelId("1");
    channelBrandingForm.setPublish(false);

    BindingResult result = validate(channelBrandingForm);
    ResponseEntity<String> actualResult = channelController.editChannelBranding(channelBrandingForm, result, request,
        response, map);
    JSONObject actualResultMap = new JSONObject(actualResult.getBody());
    Assert.assertEquals(JSONObject.NULL, actualResultMap.get("unpublished_logo"));
    Assert.assertEquals(fileName, actualResultMap.get("unpublished_favicon"));
    Assert.assertEquals(JSONObject.NULL, actualResultMap.get("unpublished_css"));

    Channel channel = channelService.getChannelById("1");

    Assert.assertNull(channel.getUnPublishedChannelBrandingConfigurations().getLogoImageFileName());
    Assert.assertEquals(fileName, channel.getUnPublishedChannelBrandingConfigurations().getFavIconImageFileName());
    Assert.assertNull(channel.getUnPublishedChannelBrandingConfigurations().getCssFileName());

    map = new ModelMap();
    channelController.editChannelBranding(channel.getId().toString(), map);
    channelBrandingForm = (ChannelBrandingForm) map.get("channelBrandingForm");

    Assert.assertEquals(fileName, channelBrandingForm.getChannel().getUnPublishedChannelBrandingConfigurations()
        .getFavIconImageFileName());

  }

  @Test
  public void testUploadChannelCss() throws Exception {
    setConfiguration(Names.com_citrix_cpbm_portal_settings_images_uploadPath, "src\\test\\resources");

    ChannelBrandingForm channelBrandingForm = new ChannelBrandingForm();

    String fileName = "css_file.css";
    MultipartFile css = new MockMultipartFile(fileName, fileName, "bytes", fileName.getBytes());

    channelBrandingForm.setCss(css);
    channelBrandingForm.setChannelId("1");
    channelBrandingForm.setPublish(false);
    BindingResult result = validate(channelBrandingForm);
    ResponseEntity<String> actualResult = channelController.editChannelBranding(channelBrandingForm, result, request,
        response, map);
    JSONObject actualResultMap = new JSONObject(actualResult.getBody());
    Assert.assertEquals(JSONObject.NULL, actualResultMap.get("unpublished_logo"));
    Assert.assertEquals(JSONObject.NULL, actualResultMap.get("unpublished_favicon"));
    Assert.assertEquals(fileName, actualResultMap.get("unpublished_css"));

    Channel channel = channelService.getChannelById("1");

    Assert.assertNull(channel.getUnPublishedChannelBrandingConfigurations().getLogoImageFileName());
    Assert.assertNull(channel.getUnPublishedChannelBrandingConfigurations().getFavIconImageFileName());
    Assert.assertEquals(fileName, channel.getUnPublishedChannelBrandingConfigurations().getCssFileName());

    map = new ModelMap();
    channelController.editChannelBranding(channel.getId().toString(), map);
    channelBrandingForm = (ChannelBrandingForm) map.get("channelBrandingForm");

    Assert.assertEquals(fileName, channelBrandingForm.getChannel().getUnPublishedChannelBrandingConfigurations()
        .getCssFileName());

    channelController.getChannelCSS(channel.getId().toString(), "", map, response);
    // Verifying that this call does not throw any exception

  }

  @Test
  public void testChannelBranding() throws Exception {

    // Setting image upload path
    Configuration configuration = configurationService
        .locateConfigurationByName(Names.com_citrix_cpbm_portal_settings_images_uploadPath);
    configuration.setValue("src\\test\\resources");
    configurationService.update(configuration);

    ChannelBrandingForm channelBrandingForm = new ChannelBrandingForm();

    MultipartFile logo = new MockMultipartFile("logo_file.jpg", "logo_file.jpg", "bytes", "logo_file.jpg".getBytes());
    MultipartFile favicon = new MockMultipartFile("icon_file.ico", "icon_file.ico", "bytes", "icon_file.ico".getBytes());
    MultipartFile css = new MockMultipartFile("css_file.css", "css_file.css", "bytes", "css_file.css".getBytes());

    channelBrandingForm.setFavicon(favicon);
    channelBrandingForm.setLogo(logo);
    channelBrandingForm.setCss(css);
    channelBrandingForm.setChannelId("1");
    channelBrandingForm.setPublish(false);
    BindingResult result = validate(channelBrandingForm);
    ResponseEntity<String> actualResult = channelController.editChannelBranding(channelBrandingForm, result, request,
        response, map);
    JSONObject actualResultMap = new JSONObject(actualResult.getBody());

    Assert.assertEquals("logo_file.jpg", actualResultMap.get("unpublished_logo"));
    Assert.assertEquals("icon_file.ico", actualResultMap.get("unpublished_favicon"));
    Assert.assertEquals("css_file.css", actualResultMap.get("unpublished_css"));

    // Verifying channel branding elements by fetching the channel after branding has been set
    Channel channel = channelService.getChannelById("1");
    Assert.assertEquals("logo_file.jpg", channel.getUnPublishedChannelBrandingConfigurations().getLogoImageFileName());
    Assert.assertEquals("icon_file.ico", channel.getUnPublishedChannelBrandingConfigurations()
        .getFavIconImageFileName());
    Assert.assertEquals("css_file.css", channel.getUnPublishedChannelBrandingConfigurations().getCssFileName());

    // Verifying that the other channel is not effected
    channel = channelService.getChannelById("3");
    Assert.assertNull(channel.getChannelBrandingConfigurations().getLogoImageFileName());
    Assert.assertNull(channel.getChannelBrandingConfigurations().getFavIconImageFileName());
    Assert.assertNull(channel.getChannelBrandingConfigurations().getCssFileName());

    // Resetting channel branding elements
    map = new ModelMap();
    String res = channelController.resetchannelbrandings("1", map);

    Assert.assertEquals(new String("success"), res.toString());

    // Verifying channel branding elements after it has been reset
    channel = channelService.getChannelById("1");
    Assert.assertNull(channel.getChannelBrandingConfigurations().getLogoImageFileName());
    Assert.assertNull(channel.getChannelBrandingConfigurations().getFavIconImageFileName());
    Assert.assertNull(channel.getChannelBrandingConfigurations().getCssFileName());

  }

  @Test
  public void testChannelBrandingWithNoImagePathSet() throws Exception {

    // Setting image upload path
    Configuration configuration = configurationService
        .locateConfigurationByName(Names.com_citrix_cpbm_portal_settings_images_uploadPath);
    configuration.setValue(null);
    configurationService.update(configuration);

    ChannelBrandingForm channelBrandingForm = new ChannelBrandingForm();

    MultipartFile logo = new MockMultipartFile("logo_file.jpg", "logo_file.jpg", "bytes", "logo_file.jpg".getBytes());
    MultipartFile favicon = new MockMultipartFile("icon_file.ico", "icon_file.ico", "bytes", "icon_file.ico".getBytes());
    MultipartFile css = new MockMultipartFile("css_file.css", "css_file.css", "bytes", "css_file.css".getBytes());

    channelBrandingForm.setFavicon(favicon);
    channelBrandingForm.setLogo(logo);
    channelBrandingForm.setCss(css);
    channelBrandingForm.setChannelId("1");

    BindingResult result = validate(channelBrandingForm);
    ResponseEntity<String> actualResult = channelController.editChannelBranding(channelBrandingForm, result, request,
        response, map);
    JSONObject actualResultMap = new JSONObject(actualResult.getBody());
    Assert.assertNotNull(actualResultMap.get("errormessage"));
    Assert.assertEquals("Custom image upload directory is not set".toLowerCase(), actualResultMap.get("errormessage")
        .toString().toLowerCase());

  }

  // @Test
  public void testChannelBrandingWithWithLargeLogo() throws Exception {

    // Setting image upload path
    Configuration configuration = configurationService
        .locateConfigurationByName(Names.com_citrix_cpbm_portal_settings_images_uploadPath);
    configuration.setValue("src\\test\\resources");
    configurationService.update(configuration);

    ChannelBrandingForm channelBrandingForm = new ChannelBrandingForm();

    byte[] largeByte = new byte[1024];

    // MultipartFile logo = new MockMultipartFile("logo_file.jpg", "logo_file.jpg", "bytes", largeByte);
    MultipartFile logo = EasyMock.createNiceMock(MultipartFile.class);
    EasyMock.expect(logo.getSize()).andReturn(1024l * 1024l * 1024l * 1024l * 1024l);
    EasyMock.expect(logo.getOriginalFilename()).andReturn("logo_file.jpg");
    EasyMock.replay(logo);

    MultipartFile favicon = new MockMultipartFile("icon_file.ico", "icon_file.ico", "bytes", largeByte);
    MultipartFile css = new MockMultipartFile("css_file.css", "css_file.css", "bytes", largeByte);

// EasyMock.expect(logo.getSize()).andReturn(1000L);
// EasyMock.replay(logo);

    System.err.println(logo.getSize());
    channelBrandingForm.setFavicon(favicon);
    channelBrandingForm.setLogo(logo);
    channelBrandingForm.setCss(css);
    channelBrandingForm.setChannelId("1");

    BindingResult result = validate(channelBrandingForm);
    ResponseEntity<String> actualResult = channelController.editChannelBranding(channelBrandingForm, result, request,
        response, map);
    JSONObject actualResultMap = new JSONObject(actualResult.getBody());
    System.err.println(actualResultMap.get("errormessage"));
    Assert.assertNull(actualResultMap.get("logo"));
    Assert.assertNull(actualResultMap.get("favicon"));
    Assert.assertNull(actualResultMap.get("css"));

  }

  @Test
  public void testCreateChannelWithFQDN() {

    String[] currencyValueList = {
      "USD"
    };
    List<Channel> bchannel = channelService.getChannels();
    Channel obtainedchannel = channelController.createChannel(WebTestUtils.createChannelForm("myName", "myDesc",
        "myCode", "myfqdnprefix", "en_US", "Asia/Kolkata", currencyValueList, map, response), null, map, response);
    List<Channel> achannel = channelService.getChannels();
    Assert.assertNotNull(obtainedchannel);
    Assert.assertEquals("myName", obtainedchannel.getName());
    Assert.assertEquals(bchannel.size() + 1, achannel.size());

    Channel fetchedChannel = channelService.getChannelById(obtainedchannel.getId().toString());

    Assert.assertEquals("myfqdnprefix", fetchedChannel.getFqdnPrefix());

  }

  @Test
  public void testEditChannelFQDN() {

    String[] currencyValueList = {
      "USD"
    };
    List<Channel> bchannel = channelService.getChannels();
    Channel obtainedchannel = channelController.createChannel(WebTestUtils.createChannelForm("myName", "myDesc",
        "myCode", "myfqdnprefix", "en_US", "Asia/Kolkata", currencyValueList, map, response), null, map, response);
    List<Channel> achannel = channelService.getChannels();
    Assert.assertNotNull(obtainedchannel);
    Assert.assertEquals("myName", obtainedchannel.getName());
    Assert.assertEquals(bchannel.size() + 1, achannel.size());

    Channel fetchedChannel = channelService.getChannelById(obtainedchannel.getId().toString());
    Assert.assertEquals("myfqdnprefix", fetchedChannel.getFqdnPrefix());

    channelController.editChannel(WebTestUtils.createChannelForm(fetchedChannel.getId().toString(), "ChannelName",
        "Desc_Channel2", fetchedChannel.getCode(), "newfqdn", "en_US", "Asia/Kolkata", null, map, response), null, map,
        response);

    fetchedChannel = channelService.getChannelById(fetchedChannel.getId().toString());

    fetchedChannel = channelService.getChannelById(obtainedchannel.getId().toString());
    Assert.assertEquals("newfqdn", fetchedChannel.getFqdnPrefix());

  }

  @Test
  public void testAddChannelDuplicateFQDN() {

    String[] currencyValueList = {
      "USD"
    };
    List<Channel> bchannel = channelService.getChannels();
    Channel obtainedchannel = channelController.createChannel(WebTestUtils.createChannelForm("myName", "myDesc",
        "myCode", "myfqdnprefix", "en_US", "Asia/Kolkata", currencyValueList, map, response), null, map, response);
    List<Channel> achannel = channelService.getChannels();
    Assert.assertNotNull(obtainedchannel);
    Assert.assertEquals("myName", obtainedchannel.getName());
    Assert.assertEquals(bchannel.size() + 1, achannel.size());

    Channel fetchedChannel = channelService.getChannelById(obtainedchannel.getId().toString());
    Assert.assertEquals("myfqdnprefix", fetchedChannel.getFqdnPrefix());

    obtainedchannel = channelController.createChannel(WebTestUtils.createChannelForm("myName1", "myDesc1", "myCode1",
        "myfqdnprefix", "en_US", "Asia/Kolkata", currencyValueList, map, response), null, map, response);

    Assert.assertNull(obtainedchannel);

  }

  @Test(expected = ChannelEditFailedException.class)
  public void testEditChannelDuplicateFQDN() {

    String[] currencyValueList = {
      "USD"
    };
    List<Channel> bchannel = channelService.getChannels();
    Channel obtainedchannel = channelController.createChannel(WebTestUtils.createChannelForm("myName", "myDesc",
        "myCode", "myfqdnprefix", "en_US", "Asia/Kolkata", currencyValueList, map, response), null, map, response);
    List<Channel> achannel = channelService.getChannels();
    Assert.assertNotNull(obtainedchannel);
    Assert.assertEquals("myName", obtainedchannel.getName());
    Assert.assertEquals(bchannel.size() + 1, achannel.size());

    Channel fetchedChannel = channelService.getChannelById(obtainedchannel.getId().toString());
    Assert.assertEquals("myfqdnprefix", fetchedChannel.getFqdnPrefix());

    channelController.editChannel(WebTestUtils.createChannelForm("3", "ChannelName", "Desc_Channel2",
        fetchedChannel.getCode(), "myfqdnprefix", "en_US", "Asia/Kolkata", null, map, response), null, map, response);

  }

  /**
   * Test to validate bad fqdn in channel creation
   */
  @Test(expected = BeanValidationException.class)
  public void testCreateChannelWithBadFQDN() {

    String[] currencyValueList = {
      "USD"
    };
    Channel createdChannel = channelController.createChannel(WebTestUtils.createChannelForm("myName", "myDesc",
        "myCode", "_12mybadprefix_", "en_US", "Asia/Kolkata", currencyValueList, map, response), null, map, response);
    Assert.assertNull(createdChannel);
    createdChannel = channelController.createChannel(WebTestUtils.createChannelForm("myName", "myDesc", "myCode",
        "-12mybadprefix-", "en_US", "Asia/Kolkata", currencyValueList, map, response), null, map, response);
    Assert.assertNull(createdChannel);

    // FIXME uncomment and fix me

    createdChannel = channelController.createChannel(WebTestUtils.createChannelForm("myName", "myDesc", "myCode",
        "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwzyx", "en_US", "Asia/Kolkata", currencyValueList, map,
        response), null, map, response);
    Assert.assertNull(createdChannel);
    createdChannel = channelController.createChannel(WebTestUtils.createChannelForm("myName", "myDesc", "myCode",
        "ab.def", "en_US", "Asia/Kolkata", currencyValueList, map, response), null, map, response);
    Assert.assertNull(createdChannel);

  }

  /**
   * Test to validate bad fqdn in channel edit
   */
  @Test(expected = BeanValidationException.class)
  public void testEditchannelWithBadFQDN() {

    testCreateChannelWithBadFQDN("_badfqdn_", "Error: Channel edit should have failed because of use of underscore");

    testCreateChannelWithBadFQDN("-badfqdnpref-",
        "Error: Channel edit should have failed because of start and end with hyphen");

    testCreateChannelWithBadFQDN("abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwzyx",
        "Error: Channel edit should have failed because of large fqdn prefix");

    testCreateChannelWithBadFQDN("abc.net", "Error: Channel edit should have failed because of use of dot");
  }

  private void testCreateChannelWithBadFQDN(String fqdn, String message) {
    Channel existingChannel = channelDAO.find("3");
    try {
      channelController.editChannel(
          WebTestUtils.createChannelForm(existingChannel.getId().toString(), existingChannel.getName(),
              existingChannel.getDescription(), "NewCode", fqdn, "en_US", "Asia/Kolkata", null, map, response), null,
          map, response);
      Assert.fail(message);
    } catch (ChannelEditFailedException e) {
    }
  }

  @Test
  public void testCreateChannelWithoutLocaleAndTZ() {
    String[] currencyvaluelist = {
        "USD", "EUR"
    };

    Channel obtainedchannel = channelController.createChannel(
        WebTestUtils.createChannelForm("Veera", "Veera", "Veera", null, null, null, currencyvaluelist, map, response),
        null, map, response);
    Assert.assertEquals(200, response.getStatus());
    Assert.assertEquals(obtainedchannel.getChannelBrandingConfigurations().getDefaultLocale(), null);
    Assert.assertEquals(obtainedchannel.getChannelBrandingConfigurations().getDefaultTimeZone(), null);

    Channel existingChannel = channelDAO.find("3");
    channelController.editChannel(
        WebTestUtils.createChannelForm(existingChannel.getId().toString(), "NewChannelName",
            existingChannel.getDescription(), existingChannel.getCode(), null, null, null, null, map, response), null,
        map, response);
    Assert.assertEquals(200, response.getStatus());
    Assert.assertEquals(existingChannel.getChannelBrandingConfigurations().getDefaultLocale(), null);
    Assert.assertEquals(existingChannel.getChannelBrandingConfigurations().getDefaultTimeZone(), null);
  }

  @Test
  public void testUploadChannelLogoWithIncorrectFile() throws Exception {
    String fileName = "logo_file.xyz";
    ResponseEntity<String> actualResult = createMockMultipartFileInput(fileName, 1);
    JSONObject actualResultMap = new JSONObject(actualResult.getBody());
    Assert.assertNotNull(actualResultMap.get("logo"));
  }

  @Test
  public void testUploadChannelCssWithIncorrectFile() throws Exception {
    String fileName = "css_file.xyz";
    ResponseEntity<String> actualResult = createMockMultipartFileInput(fileName, 2);
    JSONObject actualResultMap = new JSONObject(actualResult.getBody());
    Assert.assertNotNull(actualResultMap.get("css"));
  }

  @Test
  public void testUploadChannelFaviconWithIncorrectFile() throws Exception {
    String fileName = "fav_file.xyz";
    ResponseEntity<String> actualResult = createMockMultipartFileInput(fileName, 3);
    JSONObject actualResultMap = new JSONObject(actualResult.getBody());
    Assert.assertNotNull(actualResultMap.get("favicon"));
  }

  private ResponseEntity<String> createMockMultipartFileInput(String inputFile, int fileType) throws Exception {
    Configuration configuration = configurationService
        .locateConfigurationByName(Names.com_citrix_cpbm_portal_settings_images_uploadPath);
    configuration.setValue("src\\test\\resources");
    configurationService.update(configuration);

    ChannelBrandingForm channelBrandingForm = new ChannelBrandingForm();

    MultipartFile multipartFile = new MockMultipartFile(inputFile, inputFile, "bytes", inputFile.getBytes());

    switch (fileType) {
      case 1:
        channelBrandingForm.setLogo(multipartFile);
        break;
      case 2:
        channelBrandingForm.setCss(multipartFile);
        break;
      case 3:
        channelBrandingForm.setFavicon(multipartFile);
        break;

      default:
        break;
    }

    channelBrandingForm.setChannelId("1");

    BindingResult result = validate(channelBrandingForm);
    ResponseEntity<String> actualResult = channelController.editChannelBranding(channelBrandingForm, result, request,
        response, map);
    return actualResult;
  }

  @Test
  public void testCreateChannelWithBranding() throws Exception {

    List<Channel> bchannel = channelService.getChannels();

    ChannelForm channelForm = new ChannelForm();
    Channel channel = new Channel();
    channel.setCode("code");
    channel.setName("channelName");
    channel.setFqdnPrefix("fqdnpref");

    ChannelBrandingConfigurations channelBrandingConfigurations = createChannelBrandingConfigurations();

    channel.setChannelBrandingConfigurations(channelBrandingConfigurations);

    channelForm.setChannel(channel);
    BindingResult result = validate(channelForm);
    Channel obtainedchannel = channelController.createChannel(channelForm, result, map, response);

    List<Channel> achannel = channelService.getChannels();
    Assert.assertNotNull(obtainedchannel);
    Assert.assertEquals("channelName", obtainedchannel.getName());
    Assert.assertEquals(bchannel.size() + 1, achannel.size());

    Channel fetchedChannel = channelService.getChannelById(obtainedchannel.getId().toString());
    Assert.assertEquals("code", fetchedChannel.getCode());
    Assert.assertEquals("channelName", fetchedChannel.getName());
    assertChannelBrandingAfterModification(fetchedChannel);

    Assert.assertEquals("fqdnpref", fetchedChannel.getFqdnPrefix());

    channelController.editChannelBranding(fetchedChannel.getId().toString(), map);
    ChannelBrandingForm channelBrandingForm = (ChannelBrandingForm) map.get("channelBrandingForm");
    Assert.assertNotNull(channelBrandingForm);
    Assert.assertEquals(fetchedChannel, channelBrandingForm.getChannel());

  }

  /**
   * Test to verify update channel level URLs.
   */
  @Test
  public void testEditchannelWithValidURL() {

    String targetChannelId = "3";
    Channel existingChannel = channelService.getChannelById(targetChannelId);

    assertChannelBrandingBeforeModification(existingChannel);
    ChannelForm channelForm = WebTestUtils.createChannelForm(existingChannel.getId().toString(),
        existingChannel.getName(), existingChannel.getDescription(), "NewCode", "pref", "en_US", "Asia/Kolkata", null,
        map, response);

    ChannelBrandingConfigurations channelBrandingConfigurations = createChannelBrandingConfigurations();

    channelForm.getChannel().setChannelBrandingConfigurations(channelBrandingConfigurations);
    channelController.editChannel(channelForm, null, map, response);

    // Channel obtainedchannel = channelService.getChannelById("3");
    Channel fetchedChannel = channelService.getChannelById(targetChannelId);
    assertChannelBrandingAfterModification(fetchedChannel);

  }

  /**
   * Test to verify update channel level URLs with Invalid values.
   */
  @Test(expected = BeanValidationException.class)
  public void testEditchannelWithInvalidURL() {
    try {
      Channel existingChannel = channelDAO.find("3");

      ChannelForm channelForm = WebTestUtils.createChannelForm(existingChannel.getId().toString(),
          existingChannel.getName(), existingChannel.getDescription(), "NewCode", "pref", "en_US", "Asia/Kolkata",
          null, map, response);

      ChannelBrandingConfigurations channelBrandingConfigurations = new ChannelBrandingConfigurations();
      channelBrandingConfigurations.setBlogUrl("xyz");

      channelForm.getChannel().setChannelBrandingConfigurations(channelBrandingConfigurations);
      channelController.editChannel(channelForm, null, map, response);

      Assert.fail("Channel creation is working even with invalid URL");
    } catch (ChannelEditFailedException e) {
    }
  }

  /**
   * Editing the Channel and verifying the pre configured values.
   */

  @Test
  public void testEditChannelGet() throws Exception {

    List<Channel> bchannel = channelService.getChannels(null, null, null);

    ChannelForm channelForm = new ChannelForm();
    Channel channel = new Channel();
    channel.setCode("code");
    channel.setName("channelName");

    ChannelBrandingConfigurations channelBrandingConfigurations = createChannelBrandingConfigurations();

    channel.setChannelBrandingConfigurations(channelBrandingConfigurations);

    channelForm.setChannel(channel);
    BindingResult result = validate(channelForm);
    Channel obtainedchannel = channelController.createChannel(channelForm, result, map, response);

    List<Channel> achannel = channelService.getChannels(null, null, null);
    Assert.assertNotNull(obtainedchannel);
    Assert.assertEquals("channelName", obtainedchannel.getName());
    Assert.assertEquals(bchannel.size() + 1, achannel.size());

    obtainedchannel.getId();

    channelController.editChannel(obtainedchannel.getId().toString(), map);
    Assert.assertEquals(obtainedchannel, ((ChannelForm) map.get("channelForm")).getChannel());
    Assert.assertEquals(channelService.listCurrencies(obtainedchannel.getParam()), map.get("currencies"));
    channelForm = (ChannelForm) map.get("channelForm");
    System.out.println("Printing ................." + channelForm);
    assertChannelBrandingAfterModification(channelForm.getChannel());

  }

  /**
   * Create Channel having invalid email value.
   */

  @Test(expected = BeanValidationException.class)
  public void testCreateChannelWithInvalidEmail() throws Exception {

    ChannelForm channelForm = new ChannelForm();
    Channel channel = new Channel();
    channel.setCode("code");
    channel.setName("channelName");

    ChannelBrandingConfigurations channelBrandingConfigurations = new ChannelBrandingConfigurations();

    channelBrandingConfigurations.setHelpDeskEmail("helpEmail_citrix.com");

    channel.setChannelBrandingConfigurations(channelBrandingConfigurations);

    channelForm.setChannel(channel);
    BindingResult result = validate(channelForm);
    channelController.createChannel(channelForm, result, map, response);

  }

  /**
   * Create Channel having invalid help Desk Ph No.
   */

  @Test(expected = BeanValidationException.class)
  public void testCreateChannelWithInvalidHelpdeskNo() throws Exception {

    List<Channel> bchannel = channelService.getChannels(null, null, null);
    System.out.println(bchannel.size());
    ChannelForm channelForm = new ChannelForm();
    Channel channel = new Channel();
    channel.setCode("code");
    channel.setName("channelName");

    ChannelBrandingConfigurations channelBrandingConfigurations = new ChannelBrandingConfigurations();
    channelBrandingConfigurations.setHelpDeskPhone("-919887878788");
    channel.setChannelBrandingConfigurations(channelBrandingConfigurations);

    channelForm.setChannel(channel);
    BindingResult result = validate(channelForm);
    channelController.createChannel(channelForm, result, map, response);
  }

  /**
   * Edit Channel with invalid Help Desk Ph no.
   */

  @Test(expected = BeanValidationException.class)
  public void testEditChannelWithInvalidHelpdeskNo() throws Exception {

    Channel channel = channelDAO.find(3L);
    String channelHelpdeskNo = channel.getChannelBrandingConfigurations().getHelpDeskPhone();
    System.out.println(channelHelpdeskNo);

    ChannelForm channelForm = WebTestUtils.createChannelForm(channel.getId().toString(), channel.getName(),
        channel.getDescription(), "NewCode", "pref", "en_US", "Asia/Kolkata", null, map, response);

    ChannelBrandingConfigurations channelBrandingConfigurations = new ChannelBrandingConfigurations();
    channelBrandingConfigurations.setHelpDeskPhone("-919887878788");
    channelForm.getChannel().setChannelBrandingConfigurations(channelBrandingConfigurations);

    channelController.editChannel(channelForm, null, map, response);
  }

  /**
   * Edit Channel with invalid email value.
   */

  @Test(expected = BeanValidationException.class)
  public void testEditChannelWithInvalidEmail() throws Exception {

    Channel channel = channelDAO.find(3L);
    String channelHelpdeskEmail = channel.getChannelBrandingConfigurations().getHelpDeskEmail();

    ChannelForm channelForm = WebTestUtils.createChannelForm(channel.getId().toString(), channel.getName(),
        channel.getDescription(), "NewCode", "pref", "en_US", "Asia/Kolkata", null, map, response);

    ChannelBrandingConfigurations channelBrandingConfigurations = new ChannelBrandingConfigurations();
    channelBrandingConfigurations.setHelpDeskEmail("helpEmail_citrix.com");
    channelForm.getChannel().setChannelBrandingConfigurations(channelBrandingConfigurations);

    channelController.editChannel(channelForm, null, map, response);
    Assert.assertEquals(channelHelpdeskEmail, channel.getChannelBrandingConfigurations().getHelpDeskEmail());

  }

  /**
   * Time zone and locale field for channel is editable and can be changed at any time. New time zone and locale should
   * be effective for next account creation after update. Timezone and locale should not change for users that are
   * already created before the change.
   **/

  @Test
  public void testEditChannelLocaleAndTimezoneNotAffectingExistingUsersDefault() throws Exception {

    Locale userDefaultLocale;
    Locale userCurrentLocale;
    String userDefaultTimeZone;
    String userCurrentTimeZone;

    Channel channel = channelService.getChannelById("5");
    User userstr = userDAO.find(4L);

    userDefaultLocale = userstr.getLocale();
    userDefaultTimeZone = userstr.getTimeZone();

    channelController.editChannel(
        WebTestUtils.createChannelForm(channel.getId().toString(), channel.toString(), channel.toString(),
            channel.toString(), null, "ja", "Pacific/Rarotonga", null, map, response), null, map, response);

    Assert.assertEquals("ja",
        channelService.getValue(Names.com_citrix_cpbm_portal_settings_default_locale, channel.getUuid()));

    Assert.assertEquals("Pacific/Rarotonga",
        channelService.getValue(Names.com_citrix_cpbm_portal_settings_default_timezone, channel.getUuid()));

    User userstr1 = userDAO.find(4L);

    userCurrentLocale = userstr1.getLocale();
    userCurrentTimeZone = userstr1.getTimeZone();

    Assert.assertEquals(userCurrentLocale, userDefaultLocale);
    Assert.assertEquals(userCurrentTimeZone, userDefaultTimeZone);

  }

  private void assertChannelBrandingBeforeModification(Channel fetchedChannel) {
    Assert.assertEquals(null, fetchedChannel.getChannelBrandingConfigurations().getBlogUrl());
    Assert.assertEquals(null, fetchedChannel.getChannelBrandingConfigurations().getContactUrl());
    Assert.assertEquals(null, fetchedChannel.getChannelBrandingConfigurations().getForumUrl());
    Assert.assertEquals(null, fetchedChannel.getChannelBrandingConfigurations().getHelpUrl());
    Assert.assertEquals(null, fetchedChannel.getChannelBrandingConfigurations().getPrivacyUrl());
    Assert.assertEquals(null, fetchedChannel.getChannelBrandingConfigurations().getSupportUrl());
    Assert.assertEquals(null, fetchedChannel.getChannelBrandingConfigurations().getTouUrl());

    Assert.assertEquals("ja", fetchedChannel.getChannelBrandingConfigurations().getDefaultLocale());
    Assert.assertEquals("Pacific/Rarotonga", fetchedChannel.getChannelBrandingConfigurations().getDefaultTimeZone());

    Assert.assertEquals("support@cloud.com", fetchedChannel.getChannelBrandingConfigurations().getHelpDeskEmail());
    Assert.assertEquals("993242344435", fetchedChannel.getChannelBrandingConfigurations().getHelpDeskPhone());

    Assert.assertEquals("http://www.blog.com",
        channelService.getValue(Names.com_citrix_cpbm_portal_marketing_blog_url, fetchedChannel.getUuid()));
    Assert.assertEquals("http://www.contact.com",
        channelService.getValue(Names.com_citrix_cpbm_portal_marketing_contact_url, fetchedChannel.getUuid()));
    Assert.assertEquals("http://www.forum.com",
        channelService.getValue(Names.com_citrix_cpbm_portal_marketing_forum_url, fetchedChannel.getUuid()));
    Assert.assertEquals("http://support.citrix.com/proddocs/topic/cloudportal/ccpb-business-manager.html",
        channelService.getValue(Names.com_citrix_cpbm_portal_marketing_help_url, fetchedChannel.getUuid()));
    Assert.assertEquals("http://www.home.com",
        channelService.getValue(Names.com_citrix_cpbm_portal_marketing_home_url, fetchedChannel.getUuid()));
    Assert.assertEquals("http://www.marketing.com",
        channelService.getValue(Names.com_citrix_cpbm_portal_marketing_marketing_url, fetchedChannel.getUuid()));
    Assert.assertEquals("http://www.privacy.com",
        channelService.getValue(Names.com_citrix_cpbm_portal_marketing_privacy_url, fetchedChannel.getUuid()));
    Assert.assertEquals("http://www.support.com",
        channelService.getValue(Names.com_citrix_cpbm_portal_marketing_support_url, fetchedChannel.getUuid()));
    Assert.assertEquals("http://www.tou.com",
        channelService.getValue(Names.com_citrix_cpbm_portal_marketing_tou_url, fetchedChannel.getUuid()));

    Assert.assertEquals("ja",
        channelService.getValue(Names.com_citrix_cpbm_portal_settings_default_locale, fetchedChannel.getUuid()));
    Assert.assertEquals("Pacific/Rarotonga",
        channelService.getValue(Names.com_citrix_cpbm_portal_settings_default_timezone, fetchedChannel.getUuid()));

    Assert.assertEquals("support@cloud.com",
        channelService.getValue(Names.com_citrix_cpbm_portal_addressbook_helpDeskEmail, fetchedChannel.getUuid()));
    Assert.assertEquals("993242344435",
        channelService.getValue(Names.com_citrix_cpbm_portal_settings_helpdesk_phone, fetchedChannel.getUuid()));
  }

  private void assertChannelBrandingAfterModification(Channel fetchedChannel) {
    Assert.assertEquals("http://www.blogurl.com", fetchedChannel.getChannelBrandingConfigurations().getBlogUrl());
    Assert.assertEquals("http://www.contacturl.com", fetchedChannel.getChannelBrandingConfigurations().getContactUrl());
    Assert.assertEquals("http://www.forumurl.com", fetchedChannel.getChannelBrandingConfigurations().getForumUrl());
    Assert.assertEquals("http://www.helpurl.com", fetchedChannel.getChannelBrandingConfigurations().getHelpUrl());
    Assert.assertEquals("http://www.privacyurl.com", fetchedChannel.getChannelBrandingConfigurations().getPrivacyUrl());
    Assert.assertEquals("http://www.supporturl.com", fetchedChannel.getChannelBrandingConfigurations().getSupportUrl());
    Assert.assertEquals("http://www.touurl.com", fetchedChannel.getChannelBrandingConfigurations().getTouUrl());

    Assert.assertEquals("en_US", fetchedChannel.getChannelBrandingConfigurations().getDefaultLocale());
    Assert.assertEquals(VALID_TIMEZONE, fetchedChannel.getChannelBrandingConfigurations().getDefaultTimeZone());

    Assert.assertEquals("blackdomain.com", fetchedChannel.getChannelBrandingConfigurations().getBlacklistdomains());
    Assert.assertEquals("whitedomain.com", fetchedChannel.getChannelBrandingConfigurations().getWhitelistdomains());
    Assert.assertEquals("PK,AF", fetchedChannel.getChannelBrandingConfigurations().getBlacklistcountries());
    Assert.assertEquals("IN,US", fetchedChannel.getChannelBrandingConfigurations().getWhitelistcountries());

    Assert.assertEquals("helpEmail@citrix.com", fetchedChannel.getChannelBrandingConfigurations().getHelpDeskEmail());
    Assert.assertEquals("+919293949596", fetchedChannel.getChannelBrandingConfigurations().getHelpDeskPhone());
    Assert.assertEquals(true, fetchedChannel.getChannelBrandingConfigurations().getSignupAllowed());

    Assert.assertEquals("http://www.blogurl.com",
        channelService.getValue(Names.com_citrix_cpbm_portal_marketing_blog_url, fetchedChannel.getUuid()));
    Assert.assertEquals("http://www.contacturl.com",
        channelService.getValue(Names.com_citrix_cpbm_portal_marketing_contact_url, fetchedChannel.getUuid()));
    Assert.assertEquals("http://www.forumurl.com",
        channelService.getValue(Names.com_citrix_cpbm_portal_marketing_forum_url, fetchedChannel.getUuid()));
    Assert.assertEquals("http://www.helpurl.com",
        channelService.getValue(Names.com_citrix_cpbm_portal_marketing_help_url, fetchedChannel.getUuid()));
    Assert.assertEquals("http://www.privacyurl.com",
        channelService.getValue(Names.com_citrix_cpbm_portal_marketing_privacy_url, fetchedChannel.getUuid()));
    Assert.assertEquals("http://www.supporturl.com",
        channelService.getValue(Names.com_citrix_cpbm_portal_marketing_support_url, fetchedChannel.getUuid()));
    Assert.assertEquals("http://www.touurl.com",
        channelService.getValue(Names.com_citrix_cpbm_portal_marketing_tou_url, fetchedChannel.getUuid()));

    Assert.assertEquals("en_US",
        channelService.getValue(Names.com_citrix_cpbm_portal_settings_default_locale, fetchedChannel.getUuid()));
    Assert.assertEquals(VALID_TIMEZONE,
        channelService.getValue(Names.com_citrix_cpbm_portal_settings_default_timezone, fetchedChannel.getUuid()));

    Assert.assertEquals("helpEmail@citrix.com",
        channelService.getValue(Names.com_citrix_cpbm_portal_addressbook_helpDeskEmail, fetchedChannel.getUuid()));
    Assert.assertEquals("+919293949596",
        channelService.getValue(Names.com_citrix_cpbm_portal_settings_helpdesk_phone, fetchedChannel.getUuid()));
  }

  private ChannelBrandingConfigurations createChannelBrandingConfigurations() {
    ChannelBrandingConfigurations channelBrandingConfigurations = new ChannelBrandingConfigurations();
    channelBrandingConfigurations.setBlogUrl("http://www.blogurl.com");
    channelBrandingConfigurations.setContactUrl("http://www.contacturl.com");
    channelBrandingConfigurations.setForumUrl("http://www.forumurl.com");
    channelBrandingConfigurations.setHelpUrl("http://www.helpurl.com");
    channelBrandingConfigurations.setPrivacyUrl("http://www.privacyurl.com");
    channelBrandingConfigurations.setSupportUrl("http://www.supporturl.com");
    channelBrandingConfigurations.setTouUrl("http://www.touurl.com");

    channelBrandingConfigurations.setDefaultLocale("en_US");
    channelBrandingConfigurations.setDefaultTimeZone(VALID_TIMEZONE);

    channelBrandingConfigurations.setHelpDeskEmail("helpEmail@citrix.com");
    channelBrandingConfigurations.setHelpDeskPhone("+919293949596");
    channelBrandingConfigurations.setBlacklistdomains("blackdomain.com");
    channelBrandingConfigurations.setWhitelistdomains("whitedomain.com");
    channelBrandingConfigurations.setBlacklistcountries("PK,AF");
    channelBrandingConfigurations.setWhitelistcountries("IN,US");
    channelBrandingConfigurations.setSignupAllowed(true);
    return channelBrandingConfigurations;
  }

  /**
   * Description : to test the newly created channel is available for creating a tenant
   * 
   * @author nageswarap
   * @throws Exception
   */
  @Test
  public void testNewChannelIsAvailabletoCreateTenant() throws Exception {

    String expectedChannelName = "Veera" + random;
    String expectedTenantName = "ACME Corp";

    // Creating a Channel
    String[] currencyValueList = {
      "USD"
    };

    Channel obtainedchannel = channelController.createChannel(WebTestUtils.createChannelForm(expectedChannelName,
        "Veera", "Veera" + random, null, "en_US", "Asia/Kolkata", currencyValueList, map, response), null, map,
        response);
    Assert.assertNotNull("createChannel returned null", obtainedchannel);
    Assert.assertEquals("The Channnle not created with the given name", expectedChannelName, obtainedchannel.getName());

    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    String currentdate = sdf.format(new Date());
    System.out.println("currentdate  :: " + currentdate);
    String cres = channelController.changePlanDate(obtainedchannel.getId().toString(), currentdate, "MM/dd/yyyy", map);
    Assert.assertNotNull("changePlanDate returned null", cres);
    Assert.assertEquals("checking status of schudeactivation for a channel", "success", cres);

    // Creating a Tenant
    TenantForm form = new TenantForm((com.citrix.cpbm.access.Tenant) CustomProxy.newInstance(new Tenant()));
    form.setAccountTypeId(tenantService.getDefaultRegistrationAccountType().getId().toString());

    com.citrix.cpbm.access.Tenant newTenant = form.getTenant();
    newTenant.setName(expectedTenantName);
    newTenant.setAddress(new Address("steve", "creek", "cupertino", "ca", "95014", "US"));
    List<CurrencyValue> activeCurrencies = currencyValueService.listActiveCurrencies();
    for (CurrencyValue currencyValue : activeCurrencies) {
      newTenant.setCurrency(currencyValue);
      break;
    }

    com.citrix.cpbm.access.User newUser = form.getUser();
    newUser.setFirstName("test");
    newUser.setLastName("user");
    newUser.setEmail("test@test.com");
    newUser.setUsername(VALID_USER + random.nextInt());
    newUser.getObject().setClearPassword(VALID_PASSWORD);
    newUser.setPhone(VALID_PHONE);
    newUser.setTimeZone(VALID_TIMEZONE);
    newUser.setProfile(userProfile);
    newUser.getObject().setCreatedBy(getPortalUser());

    newUser.setAddress(new Address("steve", "creek", "cupertino", "ca", "95014", "US"));
    form.setAllowSecondary(true);
    form.setSecondaryAddress(new Address("steve", "creek", "cupertino", "CHAN", "95014", "IN"));
    // Setting the newly created channel in
    form.setChannelParam(obtainedchannel.getParam());

    status = new MockSessionStatus();
    BindingResult result = validate(form);
    Assert.assertEquals("validating that the form has no errors", 0, result.getErrorCount());
    tenantsController.create(form, result, map, status, new MockHttpServletRequest());

    Tenant createdTenant = tenantDAO.find(newTenant.getId());
    Assert.assertNotNull("createdTenant failed and found null", createdTenant);
    Assert.assertEquals("tenant objects not matched", newTenant, createdTenant);
    Assert.assertEquals("given tenant name and obtained tenant name not matched", expectedTenantName,
        createdTenant.getName());
    Assert.assertTrue("MockSessionStatus not complete", status.isComplete());
    Assert.assertEquals("events count not matched", 2, eventListener.getEvents().size());
    Assert.assertTrue("VerifyEmailRequest not generated",
        eventListener.getEvents().get(0).getPayload() instanceof VerifyEmailRequest);
    Assert.assertTrue("TriggerTransaction not generated",
        eventListener.getEvents().get(1).getPayload() instanceof TriggerTransaction);
    Assert.assertEquals("events not generated for tenant", newUser.getObject(), eventListener.getEvents().get(0)
        .getSource());
    Assert.assertEquals("the tenant sourchannel is not the given channel", expectedChannelName, createdTenant
        .getSourceChannel().getName());

  }

  /**
   * @Desc Test to create channel with overridden value for black list and White list domain name
   * @author subodh
   * @throws Exception
   */
  @Test
  public void testCreateChannelWithBlacklistAndWhitelistEmailDomain() throws Exception {
    logger.info("Entering testCreateChannelWithBlacklistAndWhitelistEmailDomain");

    List<Channel> bchannel = channelService.getChannels(null, null, null);

    ChannelForm channelForm = new ChannelForm();
    Channel channel = new Channel();
    channel.setCode("code");
    channel.setName("channelName");

    ChannelBrandingConfigurations channelBrandingConfigurations = new ChannelBrandingConfigurations();
    channelBrandingConfigurations.setBlacklistdomains("rediff.com , yahoo.com");
    channelBrandingConfigurations.setWhitelistdomains("rediff.com");

    channel.setChannelBrandingConfigurations(channelBrandingConfigurations);

    channelForm.setChannel(channel);
    BindingResult result = validate(channelForm);
    Channel obtainedchannel = channelController.createChannel(channelForm, result, map, response);
    Channel fetchedChannel = channelService.getChannelById(obtainedchannel.getId().toString());

    List<Channel> achannel = channelService.getChannels(null, null, null);
    Assert.assertEquals("code", fetchedChannel.getCode());
    Assert.assertEquals("rediff.com , yahoo.com", fetchedChannel.getChannelBrandingConfigurations()
        .getBlacklistdomains());
    Assert.assertEquals("rediff.com", fetchedChannel.getChannelBrandingConfigurations().getWhitelistdomains());
    Assert.assertEquals(bchannel.size() + 1, achannel.size());
    logger.info("Exiting testCreateChannelWithBlacklistDomain");

  }

  /**
   * @Desc Test to create channel with overridden value for black list and White list domain name
   * @author subodh
   * @throws Exception
   */
  @Test
  public void testCreateChannelWithBlacklistAndWhitelistEmailDomainWithCapitalLetters() throws Exception {
    logger.info("Entering testCreateChannelWithBlacklistAndWhitelistEmailDomainWithCapitalLetters");

    List<Channel> bchannel = channelService.getChannels(null, null, null);

    ChannelForm channelForm = new ChannelForm();
    Channel channel = new Channel();
    channel.setCode("code");
    channel.setName("channelName");

    ChannelBrandingConfigurations channelBrandingConfigurations = new ChannelBrandingConfigurations();
    channelBrandingConfigurations.setBlacklistdomains("REDIFF.com , YAHOO.com");
    channelBrandingConfigurations.setWhitelistdomains("GMAIL.com");

    channel.setChannelBrandingConfigurations(channelBrandingConfigurations);

    channelForm.setChannel(channel);
    BindingResult result = validate(channelForm);
    Channel obtainedchannel = channelController.createChannel(channelForm, result, map, response);
    Channel fetchedChannel = channelService.getChannelById(obtainedchannel.getId().toString());

    List<Channel> achannel = channelService.getChannels(null, null, null);
    Assert.assertEquals("code", fetchedChannel.getCode());
    Assert.assertEquals("rediff.com , yahoo.com", fetchedChannel.getChannelBrandingConfigurations()
        .getBlacklistdomains().toLowerCase());
    Assert.assertEquals("gmail.com", fetchedChannel.getChannelBrandingConfigurations().getWhitelistdomains()
        .toLowerCase());
    Assert.assertEquals(bchannel.size() + 1, achannel.size());
    logger.info("Exiting testCreateChannelWithBlacklistAndWhitelistEmailDomainWithCapitalLetters");

  }

  /**
   * @Desc Test to create channel will pick default value from configuration for Black list email-domain and countries
   * @author subodh
   * @throws Exception
   */
  @Test
  public void testCreateChannelWillTakeDefaultValueForBlacklistEmailDomain() throws Exception {

    logger.info("Entering testCreateChannelwithWhiteListAndBlacklistEmailDomainWithoutOverride");

    Configuration configuration = configurationService
        .locateConfigurationByName(Names.com_citrix_cpbm_accountManagement_onboarding_emailDomain_blacklist);

    configuration.setValue("test.com");
    configurationService.update(configuration);
    configuration = configurationService
        .locateConfigurationByName(Names.com_citrix_cpbm_accountManagement_onboarding_emailDomain_whitelist);
    configuration.setValue("white.com");
    configurationService.update(configuration);

    ChannelForm channelForm = new ChannelForm();
    Channel channel = new Channel();
    channel.setCode("code");
    channel.setName("channelName");

    ChannelBrandingConfigurations channelBrandingConfigurations = new ChannelBrandingConfigurations();
    channel.setChannelBrandingConfigurations(channelBrandingConfigurations);
    channelForm.setChannel(channel);
    BindingResult result = validate(channelForm);
    Channel obtainedchannel = channelController.createChannel(channelForm, result, map, response);
    Channel fetchedChannel = channelService.getChannelById(obtainedchannel.getId().toString());

    String channelBlackListValue = fetchedChannel.getChannelBrandingConfigurations().getBlacklistdomains();
    String channelWhiteListValue = fetchedChannel.getChannelBrandingConfigurations().getWhitelistdomains();

    Assert.assertNull(channelBlackListValue);
    Assert.assertNull(channelWhiteListValue);

  }

  /**
   * @Desc Test to create channel with overridden value for black list and White list countries name
   * @author subodh
   * @throws Exception
   */
  @Test
  public void testCreateChannelWithBlacklistAndWhitelistCountries() throws Exception {
    logger.info("Entering testCreateChannelWithBlacklistAndWhitelistCountries");

    List<Channel> bchannel = channelService.getChannels(null, null, null);

    ChannelForm channelForm = new ChannelForm();
    Channel channel = new Channel();
    channel.setCode("code");
    channel.setName("channelName");

    ChannelBrandingConfigurations channelBrandingConfigurations = new ChannelBrandingConfigurations();
    channelBrandingConfigurations.setBlacklistcountries("PK,ES");
    channelBrandingConfigurations.setWhitelistcountries("IN,JP");

    channel.setChannelBrandingConfigurations(channelBrandingConfigurations);

    channelForm.setChannel(channel);
    BindingResult result = validate(channelForm);
    Channel obtainedchannel = channelController.createChannel(channelForm, result, map, response);
    Channel fetchedChannel = channelService.getChannelById(obtainedchannel.getId().toString());

    List<Channel> achannel = channelService.getChannels(null, null, null);
    Assert.assertEquals("code", fetchedChannel.getCode());
    Assert.assertEquals("PK,ES", fetchedChannel.getChannelBrandingConfigurations().getBlacklistcountries());
    Assert.assertEquals("IN,JP", fetchedChannel.getChannelBrandingConfigurations().getWhitelistcountries());
    Assert.assertEquals(bchannel.size() + 1, achannel.size());
    logger.info("Exiting testCreateChannelWithBlacklistAndWhitelistCountries");

  }

  /**
   * @Desc Test to Edit channel for black list and White list domain name
   * @author Avinash
   * @throws Exception
   */
  @Test
  public void testEditChannelWithBlacklistAndWhitelistEmailDomain() throws Exception {
    logger.info("Entering testEditChannelWithBlacklistAndWhitelistEmailDomain");

    Channel channel = channelService.getChannelById("3");
    ChannelForm channelForm = (WebTestUtils.createChannelForm(channel.getId().toString(), channel.getName(),
        channel.getDescription(), "NewCode", "pref", "en_US", "Asia/Kolkata", null, map, response));

    ChannelBrandingConfigurations channelBrandingConfiguration = new ChannelBrandingConfigurations();
    channelBrandingConfiguration.setBlacklistdomains("test.com");
    channelBrandingConfiguration.setWhitelistdomains("citrix.com");
    channelForm.getChannel().setChannelBrandingConfigurations(channelBrandingConfiguration);
    channelController.editChannel(channelForm, null, map, response);
    Channel fetchedChannel = channelService.getChannelById("3");
    Assert.assertEquals("test.com", fetchedChannel.getChannelBrandingConfigurations().getBlacklistdomains());
    Assert.assertEquals("citrix.com", fetchedChannel.getChannelBrandingConfigurations().getWhitelistdomains());
    logger.info("Exiting testEditChannelWithBlacklistAndWhitelistEmailDomain");

  }

  /**
   * @Desc Test to Edit channel for black list and White list Countries
   * @author Avinash
   * @throws Exception
   */
  @Test
  public void testEditChannelWithBlacklistAndWhitelistCountries() throws Exception {
    logger.info("Entering testEditChannelWithBlacklistAndWhitelistCountries");

    Channel channel = channelService.getChannelById("3");
    ChannelForm channelForm = (WebTestUtils.createChannelForm(channel.getId().toString(), channel.getName(),
        channel.getDescription(), "NewCode", "pref", "en_US", "Asia/Kolkata", null, map, response));

    ChannelBrandingConfigurations channelBrandingConfiguration = new ChannelBrandingConfigurations();
    channelBrandingConfiguration.setBlacklistcountries("PK");
    channelBrandingConfiguration.setWhitelistcountries("JP");
    channelForm.getChannel().setChannelBrandingConfigurations(channelBrandingConfiguration);
    channelController.editChannel(channelForm, null, map, response);
    Channel fetchedChannel = channelService.getChannelById("3");
    Assert.assertEquals("PK", fetchedChannel.getChannelBrandingConfigurations().getBlacklistcountries());
    Assert.assertEquals("JP", fetchedChannel.getChannelBrandingConfigurations().getWhitelistcountries());
    logger.info("Exiting testEditChannelWithBlacklistAndWhitelistCountries");

  }

  @Test
  public void testViewChannel() throws Exception {

    Channel existingChannel = channelDAO.findAll(null).get(0);

    String view = channelController.viewChannel(existingChannel.getId().toString(), map);

    Assert.assertEquals("channels.view", view);
    Assert.assertTrue(map.containsKey("defaultLocaleValue"));
    Assert.assertTrue(map.containsKey("defaultTimeZone"));
    Assert.assertTrue(map.containsKey("global_default_locale"));
    Assert.assertTrue(map.containsKey("global_default_timezone"));
    Assert.assertTrue(map.containsKey("help_desk_phone"));
    Assert.assertTrue(map.containsKey("help_desk_email"));
    Assert.assertTrue(map.containsKey("global_signup_allowed"));
    Assert.assertTrue(map.containsKey("whitelistcountries"));
    Assert.assertTrue(map.containsKey("blacklistcountries"));
    Assert.assertTrue(map.containsKey("whitelistdomains"));
    Assert.assertTrue(map.containsKey("blacklistdomains"));
    Assert.assertTrue(map.containsKey("marketing_support_url"));
    Assert.assertTrue(map.containsKey("marketing_blog_url"));
    Assert.assertTrue(map.containsKey("marketing_forum_url"));
    Assert.assertTrue(map.containsKey("marketing_contact_url"));
    Assert.assertTrue(map.containsKey("marketing_privacy_url"));
    Assert.assertTrue(map.containsKey("marketing_help_url"));
    Assert.assertTrue(map.containsKey("marketing_tou_url"));
    Assert.assertTrue(map.containsKey("channelLogoPath"));
    Assert.assertTrue(map.containsKey("isHistoryThere"));
    Assert.assertTrue(map.containsKey("isCurrentThere"));
    Assert.assertTrue(map.containsKey("supportedCurrencies"));
    Assert.assertTrue(map.containsKey("currenciestoadd"));
    Assert.assertTrue(map.containsKey("futureRevisionDate"));
    Assert.assertTrue(map.containsKey("effectiveDate"));
    Assert.assertTrue(map.containsKey("isChannelDeletionAllowed"));
    Assert.assertTrue(map.containsKey("cloudService"));
    Assert.assertTrue(map.containsKey("publicHost"));
    Assert.assertTrue(map.containsKey("services"));
    Assert.assertTrue(map.containsKey("instances"));

  }

  @Test
  public void testCreateUnpublished() throws Exception {

    List<Channel> bchannel = channelService.getChannels();

    ChannelForm channelForm = new ChannelForm();
    Channel channel = new Channel();
    channel.setCode("code");
    channel.setName("channelName");
    // channel.setId(100L);

    ChannelBrandingConfigurations channelBrandingConfigurations = createChannelBrandingConfigurations();

    channel.setChannelBrandingConfigurations(channelBrandingConfigurations);

    channelForm.setChannel(channel);
    BindingResult result = validate(channelForm);
    Channel obtainedchannel = channelController.createChannel(channelForm, result, map, response);

    List<Channel> achannel = channelService.getChannels();
    Assert.assertNotNull(obtainedchannel);
    Assert.assertEquals("channelName", obtainedchannel.getName());
    Assert.assertEquals(bchannel.size() + 1, achannel.size());

    Channel fetchedChannel = channelService.getChannelById(obtainedchannel.getId().toString());
    Assert.assertEquals("code", fetchedChannel.getCode());
    Assert.assertEquals("channelName", fetchedChannel.getName());
    assertChannelBrandingAfterModification(fetchedChannel);

  }

  /**
   * Description : Test to verify the product bundles in channels current revision/ current tab
   * 
   * @author nageswarap
   */
  @Test
  public void testViewBundlesInCatalogCurrentRevision() {
    try {

      String expectedChannelName = "Veera" + random;

      // Creating a Channel
      String[] currencyValueList = {
        "USD"
      };

      Channel channel = channelController.createChannel(WebTestUtils.createChannelForm(expectedChannelName, "Veera",
          "Veera", null, "en_US", "Asia/Kolkata", currencyValueList, map, response), null, map, response);
      Assert.assertNotNull("createChannel returned null", channel);
      Assert.assertEquals("The Channnle not created with the given name", expectedChannelName, channel.getName());

      ServiceResourceType resourceType = connectorConfigurationManager.getServiceResourceTypeById(1L);
      String chargeFrequency = "MONTHLY";
      String compAssociationJson = "[]";
      int noOfdays = 3;
      Calendar createdAt = Calendar.getInstance();
      createdAt.add(Calendar.DATE, 0 - noOfdays);
      int beforeBundleCount = bundleService.getBundlesCount();
      boolean trialEligible = false;

      // Create a Bundle
      ProductBundle obtainedBundle = testCreateProductBundle("1", resourceType.getId().toString(), chargeFrequency,
          chargeFrequency + "Compute", "USD", BigDecimal.valueOf(100), createdAt.getTime(), compAssociationJson,
          ResourceConstraint.NONE, trialEligible);
      Assert.assertNotNull("The bundle is null ", obtainedBundle);
      Assert.assertEquals("the expected bundle name and the actual bundle name is not matching", chargeFrequency
          + "Compute", obtainedBundle.getName());
      Assert.assertEquals("Bundle resource type is not matching", resourceType, obtainedBundle.getResourceType());

      int afterBundleCount = bundleService.getBundlesCount();
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
      String attachBundleStatus = channelController.attachProductBundles(channel.getId().toString(),
          selectedProductBundles, map);
      Assert.assertNotNull(" attachBundleStatus is null ", attachBundleStatus);
      Assert.assertEquals("attachBundleStatus status is not success", "success", attachBundleStatus);

      // Schedule Activate Channel
      SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
      String currentdate = sdf.format(new Date());
      String cres = channelController.changePlanDate(channel.getId().toString(), currentdate, "MM/dd/yyyy", map);
      Assert.assertNotNull("changePlanDate returned null", cres);
      Assert.assertEquals("checking status of schudeactivation for a channel", "success", cres);

      channelController.viewCatalogCurrent(channel.getId().toString(), "1", "10", map);

      boolean found = false;
      List<ProductBundleRevision> productBundleRevisions = (List<ProductBundleRevision>) map
          .get("productBundleRevisions");
      for (ProductBundleRevision productbundleRevision : productBundleRevisions) {
        if (productbundleRevision.getProductBundle().getName().equalsIgnoreCase(obtainedBundle.getName())) {
          found = true;
        }
      }

      Assert.assertTrue("The Given bundle not found in viewCatalogCurrent", found);

    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  /**
   * Description : Test to verify the product bundles in channels planned revision/ planned tab
   * 
   * @author nageswarap
   */
  @Test
  public void testViewBundlesInCatalogPlannedRevision() {
    try {

      String expectedChannelName = "Veera";

      // Creating a Channel
      String[] currencyValueList = {
        "USD"
      };

      Channel channel = channelController.createChannel(WebTestUtils.createChannelForm(expectedChannelName, "Veera",
          "Veera", null, "en_US", "Asia/Kolkata", currencyValueList, map, response), null, map, response);
      Assert.assertNotNull("createChannel returned null", channel);
      Assert.assertEquals("The Channnle not created with the given name", expectedChannelName, channel.getName());

      ServiceResourceType resourceType = connectorConfigurationManager.getServiceResourceTypeById(1L);
      String chargeFrequency = "MONTHLY";
      String compAssociationJson = "[]";
      int noOfdays = 3;
      Calendar createdAt = Calendar.getInstance();
      createdAt.add(Calendar.DATE, 0 - noOfdays);
      int beforeBundleCount = bundleService.getBundlesCount();
      boolean trialEligible = false;

      // Create a Bundle
      ProductBundle obtainedBundlecurrent = testCreateProductBundle("1", resourceType.getId().toString(),
          chargeFrequency, "CurrentCompute", "USD", BigDecimal.valueOf(100), createdAt.getTime(), compAssociationJson,
          ResourceConstraint.NONE, trialEligible);
      Assert.assertNotNull("The bundle is null ", obtainedBundlecurrent);

      ProductBundle obtainedBundleplanned = testCreateProductBundle("1", resourceType.getId().toString(),
          chargeFrequency, "PlannedCompute", "USD", BigDecimal.valueOf(100), createdAt.getTime(), compAssociationJson,
          ResourceConstraint.NONE, trialEligible);
      Assert.assertNotNull("The bundle is null ", obtainedBundleplanned);

      int afterBundleCount = bundleService.getBundlesCount();
      Assert.assertEquals("bundle count not incremented after creating the bundle", beforeBundleCount + 2,
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
      Assert.assertEquals("scheduleActivationStatus is not success", "success", scheduleActivationStatus);

      // Sync channel with reference price book
      String syncStatus = channelController.syncChannel(channel.getId().toString(), map);
      Assert.assertNotNull(" syncStatus is null ", syncStatus);
      Assert.assertEquals("sync status is not success", "success", syncStatus);

      // Attaching product bundle to the channel
      String selectedProductBundles = "[" + obtainedBundlecurrent.getId().toString() + "]";
      String attachBundleStatus = channelController.attachProductBundles(channel.getId().toString(),
          selectedProductBundles, map);
      Assert.assertNotNull(" attachBundleStatus is null ", attachBundleStatus);
      Assert.assertEquals("attachBundleStatus is not success", "success", attachBundleStatus);

      // schedule activating the channel
      SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
      String currentdate = sdf.format(new Date());
      String cres = channelController.changePlanDate(channel.getId().toString(), currentdate, "MM/dd/yyyy", map);
      Assert.assertNotNull("changePlanDate returned null", cres);
      Assert.assertEquals("checking status of schudeactivation for a channel", "success", cres);

      channelController.viewCatalogCurrent(channel.getId().toString(), "1", "10", map);

      boolean currentFound = false;
      boolean plannedFound = false;

      List<ProductBundleRevision> productBundleRevisions = (List<ProductBundleRevision>) map
          .get("productBundleRevisions");
      for (ProductBundleRevision productbundleRevision : productBundleRevisions) {
        ProductBundle productBundle = productbundleRevision.getProductBundle();
        if (productBundle.getId() == obtainedBundlecurrent.getId()) {
          currentFound = true;
        }
        if (productBundle.getId() == obtainedBundleplanned.getId()) {
          plannedFound = true;
        }

      }

      Assert.assertTrue("current product bundle not found in current tab", currentFound);
      Assert.assertFalse("planned product bundle found in current tab", plannedFound);

      // Sync channel with reference price book
      String syncStatus1 = channelController.syncChannel(channel.getId().toString(), map);
      Assert.assertNotNull(" syncStatus is null ", syncStatus1);
      Assert.assertEquals("sync status is not success", "success", syncStatus1);

      // Attaching product bundle to the channel
      String selectedProductBundles1 = "[" + obtainedBundleplanned.getId().toString() + "]";
      String result11 = channelController
          .attachProductBundles(channel.getId().toString(), selectedProductBundles1, map);
      Assert.assertNotNull(" attachProductBundles is null ", result11);
      Assert.assertEquals("attachProductBundles status is not success", "success", result11);

      // schedule activating channel to future date
      int noOfdays11 = 3;
      Calendar createdAt11 = Calendar.getInstance();
      createdAt11.add(Calendar.DATE, noOfdays11);
      SimpleDateFormat sdf1 = new SimpleDateFormat("MM/dd/yyyy");
      String currentdate1 = sdf1.format(createdAt11.getTime());
      String cres1 = channelController.changePlanDate(channel.getId().toString(), currentdate1, "MM/dd/yyyy", map);
      Assert.assertNotNull("changePlanDate returned null", cres1);
      Assert.assertEquals("checking status of schudeactivation for a channel", "success", cres1);

      channelController.viewCatalogPlanned(channel.getId().toString(), "1", "10", "true", map);

      currentFound = false;
      plannedFound = false;

      productBundleRevisions = (List<ProductBundleRevision>) map.get("productBundleRevisions");
      for (ProductBundleRevision productbundleRevision : productBundleRevisions) {
        ProductBundle productBundle = productbundleRevision.getProductBundle();
        if (productBundle.getId() == obtainedBundlecurrent.getId()) {
          currentFound = true;
        }
        if (productBundle.getId() == obtainedBundleplanned.getId()) {
          plannedFound = true;
        }

      }

      Assert.assertTrue("current product bundle not found in planned tab", currentFound);
      Assert.assertTrue("planned product bundle not found in planned tab", plannedFound);

    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  /**
   * Description : Test to verify the product bundles in channels history revision/ history tab
   * 
   * @author nageswarap
   */

  @Test
  public void testViewBundlesInCatalogHistory() {
    try {

      String expectedChannelName = "Veera";

      // Creating a Channel
      String[] currencyValueList = {
        "USD"
      };

      Channel channel = channelController.createChannel(WebTestUtils.createChannelForm(expectedChannelName, "Veera",
          "Veera", null, "en_US", "Asia/Kolkata", currencyValueList, map, response), null, map, response);
      Assert.assertNotNull("createChannel returned null", channel);
      Assert.assertEquals("The Channnle not created with the given name", expectedChannelName, channel.getName());
      channel = channelDAO.find(9L);

      ServiceResourceType resourceType = connectorConfigurationManager.getServiceResourceTypeById(1L);
      String chargeFrequency = "MONTHLY";
      String compAssociationJson = "[]";
      int noOfdays = 3;
      Calendar createdAt = Calendar.getInstance();
      createdAt.add(Calendar.DATE, 0 - noOfdays);
      int beforeBundleCount = bundleService.getBundlesCount();
      boolean trialEligible = false;

      // Create a Bundle

      ProductBundle obtainedBundlehistory = testCreateProductBundle("1", resourceType.getId().toString(),
          chargeFrequency, "HistoryCompute", "USD", BigDecimal.valueOf(100), createdAt.getTime(), compAssociationJson,
          ResourceConstraint.NONE, trialEligible);
      Assert.assertNotNull("The bundle is null ", obtainedBundlehistory);

      ProductBundle obtainedBundlecurrent = testCreateProductBundle("1", resourceType.getId().toString(),
          chargeFrequency, "CurrentCompute", "USD", BigDecimal.valueOf(100), createdAt.getTime(), compAssociationJson,
          ResourceConstraint.NONE, trialEligible);
      Assert.assertNotNull("The bundle is null ", obtainedBundlecurrent);

      int afterBundleCount = bundleService.getBundlesCount();
      Assert.assertEquals("bundle count not incremented after creating the bundle", beforeBundleCount + 2,
          afterBundleCount);

      // Schedule Activating the Bundle
      int noOfdays1 = 0;
      Calendar scheduleActivatedAt = Calendar.getInstance();
      scheduleActivatedAt.add(Calendar.DATE, 0 - noOfdays1);
      ProductForm postForm = new ProductForm();
      postForm.setStartDate(new Date());
      BindingResult result = validate(postForm);
      productsController.setPlanDate(postForm, result, map);

      // Sync channel with reference price book
      String syncStatus = channelController.syncChannel(channel.getId().toString(), map);
      Assert.assertNotNull(" syncStatus is null ", syncStatus);
      Assert.assertEquals("sync status is success", "success", syncStatus);

      // Attaching product bundle to the channel
      String selectedProductBundles = "[" + obtainedBundlehistory.getId().toString() + "]";
      channelController.attachProductBundles(channel.getId().toString(), selectedProductBundles, map);

      // schedule activating channel
      SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
      String currentdate = sdf.format(new Date());
      String cres = channelController.changePlanDate(channel.getId().toString(), currentdate, "MM/dd/yyyy HH:mm:ss",
          map);
      Assert.assertNotNull("changePlanDate returned null", cres);
      Assert.assertEquals("checking status of schudeactivation for a channel", "success", cres);

      ArrayList<Revision> revisions = (ArrayList<Revision>) revisionDAO.locateAllCatalogRevisions(channel.getCatalog());
      for (Revision r : revisions) {
        System.out.println("start date" + r.getStartDate());
      }
      Date currentRevisionStartDate = channelService.getCurrentRevision(channel).getStartDate();
      System.out.println("Current revi" + currentRevisionStartDate);
      Thread.sleep(10000);

      // Sync channel with reference price book
      String syncStatus1 = channelController.syncChannel(channel.getId().toString(), map);
      Assert.assertNotNull(" syncStatus is null ", syncStatus1);
      Assert.assertEquals("sync status is success", "success", syncStatus1);

      // Attaching product bundle to the channel
      String selectedProductBundles1 = "[" + obtainedBundlecurrent.getId().toString() + "]";
      channelController.attachProductBundles(channel.getId().toString(), selectedProductBundles1, map);

      String currentdate1 = sdf.format(new Date());
      String cres1 = channelController.changePlanDate(channel.getId().toString(), currentdate1, "MM/dd/yyyy HH:mm:ss",
          map);
      channelDAO.refresh(channel);
      catalogDAO.refresh(channel.getCatalog());
      Assert.assertNotNull("changePlanDate returned null", cres1);
      Assert.assertEquals("checking status of schudeactivation for a channel", "success", cres1);
      Thread.sleep(10000);
      revisions = (ArrayList<Revision>) revisionDAO.locateAllCatalogRevisions(channel.getCatalog());
      for (Revision r : revisions) {
        System.out.println("start date" + r.getStartDate());
      }
      Catalog catalog = channel.getCatalog();
      currentRevisionStartDate = channelService.getCurrentRevision(catalog.getChannel()).getStartDate();
      System.out.println("Current revi exact" + currentRevisionStartDate);

      channelController
          .viewCatalogHistory(channel.getId().toString(), currentdate, "MM/dd/yyyy HH:mm:ss", "false", map);

      Date currentRevisionStartDate1 = channelService.getCurrentRevision(channel).getStartDate();
      System.out.println("Current revi1" + currentRevisionStartDate1);
      boolean historyFound = false;
      boolean currentFound = false;

      Set<ProductBundleRevision> productBundleRevisions = (Set<ProductBundleRevision>) map
          .get("productBundleRevisions");
      for (ProductBundleRevision productbundleRevision : productBundleRevisions) {
        ProductBundle productBundle = productbundleRevision.getProductBundle();
        if (productBundle.getId() == obtainedBundlecurrent.getId()) {
          currentFound = true;
        }
        if (productBundle.getId() == obtainedBundlehistory.getId()) {
          historyFound = true;
        }

      }

      Assert.assertTrue("history product bundle not found in history tab", historyFound);
      Assert.assertFalse("current product bundle found in history tab", currentFound);

    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  /**
   * Description : Test to verify that editing the product price in catalog will effect the product pricing
   * 
   * @author nageswarap
   */

  @Test
  public void testEditCatalogProductPricingnotEffectProductPricing() {

    Channel channel = channelDAO.find(4L);
    Product product = productDAO.find(4L);

    List<ProductCharge> pcharges = productService.getProductCharges(product, new Date());
    BigDecimal previousCharge = BigDecimal.ZERO;
    BigDecimal expectedNewCharge = BigDecimal.valueOf(200.0000);

    for (ProductCharge pcharge : pcharges) {
      if (pcharge.getCurrencyValue().getCurrencyCode().equals("JPY")) {
        if (pcharge.getProduct().getId() == 4L) {
          previousCharge = pcharge.getPrice();
        }
      }

    }

    try {

      String currencyValData = "[{'previousvalue':'" + previousCharge + "','value':'" + expectedNewCharge
          + "','currencycode':'JPY','currencyId':'71','productId':'4'}]";
      channelController.editCatalogProductPricing(channel.getId().toString(), currencyValData, map);

      boolean productChargeSet = false;
      List<ProductCharge> prodCharges = productService.getCatalogPlannedChargesForAllProducts(channel.getCatalog());

      for (ProductCharge productCharge : prodCharges) {
        if (productCharge.getProduct().equals(product)
            && productCharge.getCurrencyValue().getCurrencyCode().equals("JPY")) {
          Assert.assertEquals("Product price is not set in catalog", expectedNewCharge, productCharge.getPrice());
          productChargeSet = true;
        }
      }
      Assert.assertTrue("Product price is not set in catalog", productChargeSet);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }

    List<ProductCharge> pcharges1 = productService.getProductCharges(product, new Date());

    for (ProductCharge pcharge : pcharges1) {
      if (pcharge.getCurrencyValue().getCurrencyCode().equals("JPY")) {
        if (pcharge.getProduct().getId() == 4L) {
          Assert.assertEquals("Product price changed after editing in catalog", previousCharge, pcharge.getPrice());
        }
      }

    }

  }

  /**
   * Description : Test to verify that editing the product price in catalog can be done when channel caching is enabled
   * 
   * @author nageswarap
   */

  @Test
  public void testEditCatalogProductPricingEnableCashing() {

    Channel channel = channelDAO.find(4L);
    Product product = productDAO.find(4L);
    BigDecimal expectedCharge = BigDecimal.valueOf(200.0000);

    Configuration conf = configurationService.locateConfigurationByName("com.citrix.cpbm.channel.enable.caching");
    if (conf.getValue().equalsIgnoreCase("FALSE")) {
      conf.setValue("TRUE");
      configurationService.update(conf);
    }
    Assert.assertEquals("com.citrix.cpbm.channel.enable.caching configuration is false", "TRUE", conf.getValue());

    try {

      String currencyValData = "[{'previousvalue':'14.0000','value':'" + expectedCharge
          + "','currencycode':'JPY','currencyId':'71','productId':'4'}]";
      channelController.editCatalogProductPricing(channel.getId().toString(), currencyValData, map);

      boolean productChargeSet = false;
      List<ProductCharge> prodCharges = productService.getCatalogPlannedChargesForAllProducts(channel.getCatalog());

      System.out.println("prodCharges list size : " + prodCharges.size());
      for (ProductCharge productCharge : prodCharges) {
        if (productCharge.getProduct().equals(product)
            && productCharge.getCurrencyValue().getCurrencyCode().equals("JPY")) {
          Assert.assertEquals("Product price is not set in catalog", expectedCharge, productCharge.getPrice());
          productChargeSet = true;
        }
      }
      Assert.assertTrue("Product price is not set in catalog", productChargeSet);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  /**
   * Description : Test to verify that editing the product bundle in catalog can be done when channel caching is enabled
   * 
   * @author nageswarap
   */

  @Test
  public void testEditCatalogProductBundlePricingWithenablecashing() throws JSONException {

    Channel obtainedChannel = channelDAO.find(4L);

    Configuration conf = configurationService.locateConfigurationByName("com.citrix.cpbm.channel.enable.caching");
    if (conf.getValue().equalsIgnoreCase("FALSE")) {
      conf.setValue("TRUE");
      configurationService.update(conf);
    }
    Assert.assertEquals("com.citrix.cpbm.channel.enable.caching configuration is false", "TRUE", conf.getValue());

    String currencyValData = "[{\"previousvalue\":\"0.0000\",\"value\":\"5000\",\"currencycode\":\"EUR\",\"currencyId\":\"44\",\"isRecurring\":\"0\"}]";
    channelController.editCatalogProductBundlePricing(obtainedChannel.getId().toString(), "2", currencyValData, map);
    channelController.getFullChargeListing(obtainedChannel.getId().toString(), "planned", "2", null,
        DateUtils.getSimpleDateString(new java.util.Date()), map);
    Map<ProductBundleRevision, Map<CurrencyValue, Map<String, RateCardCharge>>> fullBundlePricingMap = (Map<ProductBundleRevision, Map<CurrencyValue, Map<String, RateCardCharge>>>) map
        .get("fullBundlePricingMap");

    Assert.assertNotNull(map.get("productBundleRevision"));
    for (Map.Entry<ProductBundleRevision, Map<CurrencyValue, Map<String, RateCardCharge>>> map1 : fullBundlePricingMap
        .entrySet()) {
      for (Map.Entry<CurrencyValue, Map<String, RateCardCharge>> map2 : map1.getValue().entrySet()) {
        CurrencyValue cv = map2.getKey();
        if (cv.getCurrencyCode().equalsIgnoreCase("EUR")) {
          for (Map.Entry<String, RateCardCharge> map3 : map2.getValue().entrySet()) {
            String str = map3.getKey();
            RateCardCharge rcc = map3.getValue();
            if (str.equalsIgnoreCase("catalog-onetime")) {
              Assert.assertEquals(BigDecimal.valueOf(5000), rcc.getPrice());
            }
          }
        }
      }
    }

  }

  /*
   * Description: Private Test to create Bundles based on the parameters Author: Vinayv
   */
  private ProductBundle testCreateProductBundle(String serviceInstanceID, String resourceTypeID, String chargeType,
      String bundleName, String currencyCode, BigDecimal currencyValue, Date startDate, String jsonString,
      ResourceConstraint businessConstraint, boolean trialEligible) throws Exception {

    ServiceInstance serviceInstance = null;
    List<ServiceInstance> serviceInstanceList = connectorConfigurationManager.getAllServiceInstances();
    for (ServiceInstance si : serviceInstanceList) {
      if (si.getId().toString().equalsIgnoreCase(serviceInstanceID)) {
        serviceInstance = si;
      }
    }

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
    RateCard rateCard = new RateCard("Rate", bundleService.getChargeRecurrencyFrequencyByName(chargeTypeName),
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
   * Edit Channel with invalid email domain value.
   */

  @Test
  public void testEditChannelWithInvalidEmailDomains() {
    try {
      editChannelEmailDomains("abc@xxx", null);
      Assert.fail("No error occured");
    } catch (BeanValidationException e) {
    }

    try {
      editChannelEmailDomains("rediffmail", null);
      Assert.fail("No error occured");
    } catch (BeanValidationException e) {
    }

    try {
      editChannelEmailDomains("abc`sh.com", null);
      Assert.fail("No error occured");
    } catch (BeanValidationException e) {
    }

    try {
      editChannelEmailDomains(null, "abcsh.com;");
      Assert.fail("No error occured");
    } catch (BeanValidationException e) {
    }
    try {
      editChannelEmailDomains(null, "abc@sh.com");
      Assert.fail("No error occured");
    } catch (BeanValidationException e) {
    }
    try {
      editChannelEmailDomains(null, "abc@xxx");
      Assert.fail("No error occured");
    } catch (BeanValidationException e) {
    }

    try {
      editChannelEmailDomains(null, "rediffmail");
      Assert.fail("No error occured");
    } catch (BeanValidationException e) {
    }

    try {
      editChannelEmailDomains(null, "abc`sh.com");
      Assert.fail("No error occured");
    } catch (BeanValidationException e) {
    }

    try {
      editChannelEmailDomains("abcsh.com;", null);
      Assert.fail("No error occured");
    } catch (BeanValidationException e) {
    }
    try {
      editChannelEmailDomains("abc@sh.com;", null);
      Assert.fail("No error occured");
    } catch (BeanValidationException e) {
    }

    try {
      editChannelEmailDomains("abc@xxx", "bb@ss");
      Assert.fail("No error occured");
    } catch (BeanValidationException e) {
    }
  }

  @Test
  public void testCreateChannelWithInvalidEmailDomains() {
    try {
      createChannelWithEmailDomain("abc@xxx", null);
      Assert.fail("No error occured");
    } catch (BeanValidationException e) {
    }

    try {
      createChannelWithEmailDomain("rediffmail", null);
      Assert.fail("No error occured");
    } catch (BeanValidationException e) {
    }

    try {
      createChannelWithEmailDomain("abc`sh.com", null);
      Assert.fail("No error occured");
    } catch (BeanValidationException e) {
    }

    try {
      createChannelWithEmailDomain(null, "abcsh.com;");
      Assert.fail("No error occured");
    } catch (BeanValidationException e) {
    }
    try {
      createChannelWithEmailDomain(null, "abc@sh.com");
      Assert.fail("No error occured");
    } catch (BeanValidationException e) {
    }
    try {
      createChannelWithEmailDomain(null, "abc@xxx");
      Assert.fail("No error occured");
    } catch (BeanValidationException e) {
    }

    try {
      createChannelWithEmailDomain(null, "rediffmail");
      Assert.fail("No error occured");
    } catch (BeanValidationException e) {
    }

    try {
      createChannelWithEmailDomain(null, "abc`sh.com");
      Assert.fail("No error occured");
    } catch (BeanValidationException e) {
    }

    try {
      createChannelWithEmailDomain("abcsh.com;", null);
      Assert.fail("No error occured");
    } catch (BeanValidationException e) {
    }
    try {
      createChannelWithEmailDomain("abc@sh.com;", null);
      Assert.fail("No error occured");
    } catch (BeanValidationException e) {
    }

    try {
      createChannelWithEmailDomain("abc@xxx", "bb@ss");
      Assert.fail("No error occured");
    } catch (BeanValidationException e) {
    }
  }

  private void editChannelEmailDomains(String whitelistDomains, String blacklistDomain) {
    Channel channel = channelDAO.find(3L);

    ChannelForm channelForm = WebTestUtils.createChannelForm(channel.getId().toString(), channel.getName(),
        channel.getDescription(), "NewCode", "pref", "en_US", "Asia/Kolkata", null, map, response);

    ChannelBrandingConfigurations channelBrandingConfigurations = new ChannelBrandingConfigurations();
    channelBrandingConfigurations.setWhitelistdomains(whitelistDomains);
    channelBrandingConfigurations.setBlacklistdomains(blacklistDomain);

    channelForm.getChannel().setChannelBrandingConfigurations(channelBrandingConfigurations);

    channelController.editChannel(channelForm, null, map, response);

  }

  private void createChannelWithEmailDomain(String whiteEmailDomain, String blackEmailDomain) {

    ChannelForm channelForm = new ChannelForm();
    Channel channel = new Channel();
    channel.setCode("code");
    channel.setName("channelName");

    ChannelBrandingConfigurations channelBrandingConfigurations = new ChannelBrandingConfigurations();

    channelBrandingConfigurations.setBlacklistdomains(blackEmailDomain);
    channelBrandingConfigurations.setWhitelistdomains(whiteEmailDomain);
    channel.setChannelBrandingConfigurations(channelBrandingConfigurations);

    channelForm.setChannel(channel);
    BindingResult result = null;
    try {
      result = validate(channelForm);
    } catch (Exception e) {
      Assert.fail();
    }
    channelController.createChannel(channelForm, result, map, response);

  }

  /**
   * Test to verify update channel level country list with Invalid values.
   */
  @Test
  public void testEditchannelWithInvalidCountry() {
    String[] countryList = {
        "I", "IND", "A'", "##", ",IN", "IN,U", "USD,US"
    };

    int failureCount = 0;
    for (String country : countryList) {
      try {
        editChannelWithCountry(country, null);
        Assert.fail("Channel created with Invalid countries.");
      } catch (BeanValidationException e) {
        failureCount++;
      }
    }
    Assert.assertEquals(countryList.length, failureCount);

    failureCount = 0;
    for (String country : countryList) {
      try {
        editChannelWithCountry(null, country);
        Assert.fail("Channel created with Invalid countries.");
      } catch (BeanValidationException e) {
        failureCount++;
      }
    }
    Assert.assertEquals(countryList.length, failureCount);

  }

  private void editChannelWithCountry(String blacklistCountry, String whitelistCountry) {
    Channel existingChannel = channelDAO.find("3");

    ChannelForm channelForm = WebTestUtils.createChannelForm(existingChannel.getId().toString(),
        existingChannel.getName(), existingChannel.getDescription(), "NewCode", "pref", "en_US", "Asia/Kolkata", null,
        map, response);

    ChannelBrandingConfigurations channelBrandingConfigurations = new ChannelBrandingConfigurations();
    channelBrandingConfigurations.setBlacklistcountries(blacklistCountry);
    channelBrandingConfigurations.setWhitelistcountries(whitelistCountry);
    // Invalid beacuse code should be 2 digit country code
    channelForm.getChannel().setChannelBrandingConfigurations(channelBrandingConfigurations);
    channelController.editChannel(channelForm, null, map, response);
  }

  /**
   * Create Channel having invalid country code.
   */

  @Test
  public void testCreateChannelWithInvalidCountry() throws Exception {

    String[] countryList = {
        "I", "IND", "A'", "##", ",IN", "IN,U", "USD,US"
    };

    int failureCount = 0;
    for (String country : countryList) {
      try {
        createChannelWithCountries(country, null);
        Assert.fail("Channel created with Invalid countries.");
      } catch (BeanValidationException e) {
        failureCount++;
      }
    }
    Assert.assertEquals(countryList.length, failureCount);

    failureCount = 0;
    for (String country : countryList) {
      try {
        createChannelWithCountries(null, country);
        Assert.fail("Channel created with Invalid countries.");
      } catch (BeanValidationException e) {
        failureCount++;
      }
    }
    Assert.assertEquals(countryList.length, failureCount);

  }

  private void createChannelWithCountries(String blacklistCountries, String whitelistCountries) throws Exception {
    ChannelForm channelForm = new ChannelForm();
    Channel channel = new Channel();
    channel.setCode("code");
    channel.setName("channelName");

    ChannelBrandingConfigurations channelBrandingConfigurations = new ChannelBrandingConfigurations();

    channelBrandingConfigurations.setBlacklistcountries(blacklistCountries);
    channelBrandingConfigurations.setWhitelistcountries(whitelistCountries);

    channel.setChannelBrandingConfigurations(channelBrandingConfigurations);

    channelForm.setChannel(channel);
    BindingResult result = validate(channelForm);
    channelController.createChannel(channelForm, result, map, response);
  }

  @Test
  public void testValidateChannelFQDNPrefix() {

    Map<String, Object> map;
    map = channelController.validateChannelFQDNPrefix("ch1");
    Assert.assertNotNull(map.get("valid"));
    Assert.assertEquals(true, map.get("valid"));

    map = channelController.validateChannelFQDNPrefix("");
    Assert.assertNotNull(map.get("valid"));
    Assert.assertEquals(true, map.get("valid"));

    map = channelController.validateChannelFQDNPrefix("chn1"); // already used fqdn prefix
    Assert.assertNotNull(map.get("valid"));
    Assert.assertEquals(false, map.get("valid"));

  }

  @Test
  public void testCreateUnpublishedChannelBrandings() {
    String tempDirectory = System.getProperty("java.io.tmpdir");
    tempDirectory = FilenameUtils.concat(tempDirectory, "channel_brandings");
    Configuration dataPathConfig = configurationService
        .locateConfigurationByName(Names.com_citrix_cpbm_portal_settings_images_uploadPath);
    if (StringUtils.isBlank(dataPathConfig.getValue())) {
      dataPathConfig.setValue(tempDirectory);
      configurationService.update(dataPathConfig);
    }

    String channelId = "4";
    String faviconFileName = "fav.ico";
    String logoFileName = "logo.png";
    String cssFileName = "css1.css";
    MultipartFile faviconFile = new MockMultipartFile(faviconFileName, "favicon".getBytes());
    MultipartFile cssFile = new MockMultipartFile(cssFileName, "css".getBytes());
    MultipartFile logoFile = new MockMultipartFile(logoFileName, "logo".getBytes());
    try {
      channelService.createUnpublishedChannelBrandings(channelId, faviconFile, cssFile, logoFile);
    } catch (IOException e) {
      org.junit.Assert.fail();
      e.printStackTrace();
    }

    Map<String, String> publishMap = new HashMap<String, String>();
    publishMap = channelController.publishChannelBrandings(channelId);
    Assert.assertEquals(logoFileName, publishMap.get("logo"));
    Assert.assertEquals(faviconFileName, publishMap.get("favicon"));
    Assert.assertEquals(cssFileName, publishMap.get("css"));

  }

  /**
   * Author: Abhaik
   */
  @Test
  public void testCreateChannelWithNewBillingGroup() throws Exception {

    List<Channel> channelsBeforeRun = channelService.getChannels(0, 0, "", null);

    List<BillingGroup> billingGroupsBeforeRun = (List<BillingGroup>) channelService.getBillingGroups();

    ChannelForm channelForm = new ChannelForm();
    Channel channel = new Channel();
    channel.setCode("code");
    channel.setName("channelName");

    BillingGroup billingGroup = new BillingGroup();
    billingGroup.setName("BillingGroupTest");
    billingGroup.setDescription("BillingGroupTest");

    channel.setBillingGroup(billingGroup);

    ChannelBrandingConfigurations channelBrandingConfigurations = createChannelBrandingConfigurations();
    channel.setChannelBrandingConfigurations(channelBrandingConfigurations);

    channelForm.setChannel(channel);
    BindingResult result = validate(channelForm);
    Channel obtainedchannel = channelController.createChannel(channelForm, result, map, response);

    List<Channel> channelsAfterRun = channelService.getChannels(0, 0, "", null);
    Assert.assertNotNull(obtainedchannel);
    Assert.assertEquals("channelName", obtainedchannel.getName());
    Assert.assertEquals(channelsBeforeRun.size() + 1, channelsAfterRun.size());

    Channel fetchedChannel = channelService.getChannelById(obtainedchannel.getId().toString());
    Assert.assertEquals(channel.getCode(), fetchedChannel.getCode());
    Assert.assertEquals(channel.getName(), fetchedChannel.getName());

    List<BillingGroup> billingGroupsAfterRun = (List<BillingGroup>) channelService.getBillingGroups();
    Assert.assertEquals(billingGroupsBeforeRun.size() + 1, billingGroupsAfterRun.size());
    Assert.assertNotNull(fetchedChannel.getBillingGroup());
    Assert.assertEquals(billingGroup.getName(), fetchedChannel.getBillingGroup().getName());
    Assert.assertEquals(billingGroup.getDescription(), fetchedChannel.getBillingGroup().getDescription());

  }

  /**
   * Author: Abhaik
   */
  @Test
  public void testCreateChannelWithExistingBillingGroup() throws Exception {

    List<Channel> channelsBeforeRun = channelService.getChannels(0, 0, "", null);

    List<BillingGroup> billingGroupsBeforeRun = (List<BillingGroup>) channelService.getBillingGroups();

    ChannelForm channelForm = new ChannelForm();
    Channel channel = new Channel();
    channel.setCode("code");
    channel.setName("channelName");

    BillingGroup billingGroup = new BillingGroup();
    billingGroup.setName("cat2");
    billingGroup.setDescription("cat2");

    channel.setBillingGroup(billingGroup);

    ChannelBrandingConfigurations channelBrandingConfigurations = createChannelBrandingConfigurations();
    channel.setChannelBrandingConfigurations(channelBrandingConfigurations);

    channelForm.setChannel(channel);
    BindingResult result = validate(channelForm);
    Channel obtainedchannel = channelController.createChannel(channelForm, result, map, response);

    List<Channel> channelsAfterRun = channelService.getChannels(0, 0, "", null);
    Assert.assertNotNull(obtainedchannel);
    Assert.assertEquals("channelName", obtainedchannel.getName());
    Assert.assertEquals(channelsBeforeRun.size() + 1, channelsAfterRun.size());

    Channel fetchedChannel = channelService.getChannelById(obtainedchannel.getId().toString());
    Assert.assertEquals(channel.getCode(), fetchedChannel.getCode());
    Assert.assertEquals(channel.getName(), fetchedChannel.getName());

    List<BillingGroup> billingGroupsAfterRun = (List<BillingGroup>) channelService.getBillingGroups();
    Assert.assertEquals(billingGroupsBeforeRun.size(), billingGroupsAfterRun.size());
    Assert.assertNotNull(fetchedChannel.getBillingGroup());
    Assert.assertEquals(billingGroup.getName(), fetchedChannel.getBillingGroup().getName());
    Assert.assertEquals(null, fetchedChannel.getBillingGroup().getDescription());

  }

  /**
   * Author: Abhaik
   */
  @Test
  public void testCreateChannelWithoutBillingGroup() throws Exception {

    List<Channel> channelsBeforeRun = channelService.getChannels(0, 0, "", null);

    List<BillingGroup> billingGroupsBeforeRun = (List<BillingGroup>) channelService.getBillingGroups();

    ChannelForm channelForm = new ChannelForm();
    Channel channel = new Channel();
    channel.setCode("code");
    channel.setName("channelName");

    ChannelBrandingConfigurations channelBrandingConfigurations = createChannelBrandingConfigurations();
    channel.setChannelBrandingConfigurations(channelBrandingConfigurations);

    channelForm.setChannel(channel);
    BindingResult result = validate(channelForm);
    Channel obtainedchannel = channelController.createChannel(channelForm, result, map, response);

    List<Channel> channelsAfterRun = channelService.getChannels(0, 0, "", null);
    Assert.assertNotNull(obtainedchannel);
    Assert.assertEquals("channelName", obtainedchannel.getName());
    Assert.assertEquals(channelsBeforeRun.size() + 1, channelsAfterRun.size());

    Channel fetchedChannel = channelService.getChannelById(obtainedchannel.getId().toString());
    Assert.assertEquals(channel.getCode(), fetchedChannel.getCode());
    Assert.assertEquals(channel.getName(), fetchedChannel.getName());

    List<BillingGroup> billingGroupsAfterRun = (List<BillingGroup>) channelService.getBillingGroups();
    Assert.assertEquals(billingGroupsBeforeRun.size(), billingGroupsAfterRun.size());
    Assert.assertNotNull(fetchedChannel.getBillingGroup());
    Assert.assertEquals("cat1", fetchedChannel.getBillingGroup().getName());
    Assert.assertEquals(null, fetchedChannel.getBillingGroup().getDescription());

  }

  /**
   * Author: Abhaik
   */
  @Test
  public void testCreateChannelEmptyStringBillingGroup() throws Exception {

    List<Channel> channelsBeforeRun = channelService.getChannels(0, 0, "", null);

    List<BillingGroup> billingGroupsBeforeRun = (List<BillingGroup>) channelService.getBillingGroups();

    ChannelForm channelForm = new ChannelForm();
    Channel channel = new Channel();
    channel.setCode("code");
    channel.setName("channelName");

    BillingGroup billingGroup = new BillingGroup();
    billingGroup.setName("");
    billingGroup.setDescription("");

    channel.setBillingGroup(billingGroup);

    ChannelBrandingConfigurations channelBrandingConfigurations = createChannelBrandingConfigurations();
    channel.setChannelBrandingConfigurations(channelBrandingConfigurations);

    channelForm.setChannel(channel);
    BindingResult result = validate(channelForm);
    Channel obtainedchannel = channelController.createChannel(channelForm, result, map, response);

    List<Channel> channelsAfterRun = channelService.getChannels(0, 0, "", null);
    Assert.assertNotNull(obtainedchannel);
    Assert.assertEquals("channelName", obtainedchannel.getName());
    Assert.assertEquals(channelsBeforeRun.size() + 1, channelsAfterRun.size());

    Channel fetchedChannel = channelService.getChannelById(obtainedchannel.getId().toString());
    Assert.assertEquals(channel.getCode(), fetchedChannel.getCode());
    Assert.assertEquals(channel.getName(), fetchedChannel.getName());

    List<BillingGroup> billingGroupsAfterRun = (List<BillingGroup>) channelService.getBillingGroups();
    Assert.assertEquals(billingGroupsBeforeRun.size(), billingGroupsAfterRun.size());
    Assert.assertNotNull(fetchedChannel.getBillingGroup());
    Assert.assertEquals("cat1", fetchedChannel.getBillingGroup().getName());
    Assert.assertEquals(null, fetchedChannel.getBillingGroup().getDescription());

  }

  /**
   * Author: Abhaik
   */
  @Test(expected = BeanValidationException.class)
  public void testCreateChannelWithBillingGroupAsLongChar() throws Exception {

    ChannelForm channelForm = new ChannelForm();
    Channel channel = new Channel();
    channel.setCode("code");
    channel.setName("channelName");

    BillingGroup billingGroup = new BillingGroup();
    billingGroup
        .setName("More then 256 char aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
    billingGroup
        .setDescription("More then 256 char aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");

    channel.setBillingGroup(billingGroup);

    ChannelBrandingConfigurations channelBrandingConfigurations = createChannelBrandingConfigurations();

    channel.setChannelBrandingConfigurations(channelBrandingConfigurations);

    channelForm.setChannel(channel);
    BindingResult result = validate(channelForm);
    channelController.createChannel(channelForm, result, map, response);

  }

  /**
   * Author: Abhaik
   */
  @Test
  public void testEditChannelWithNewBillingGroup() {

    List<BillingGroup> billingGroupsBeforeRun = (List<BillingGroup>) channelService.getBillingGroups();

    String targetChannelId = "3";
    Channel existingChannel = channelService.getChannelById(targetChannelId);

    Channel modifiedChannel = new Channel();
    modifiedChannel.setCatalog(existingChannel.getCatalog());
    modifiedChannel.setChannelBrandingConfigurations(existingChannel.getChannelBrandingConfigurations());
    modifiedChannel.setCode(existingChannel.getCode());
    modifiedChannel.setDescription(existingChannel.getDescription());
    modifiedChannel.setName(existingChannel.getName());
    modifiedChannel.setType(existingChannel.getType());
    BillingGroup bg = new BillingGroup();
    modifiedChannel.setBillingGroup(bg);
    modifiedChannel.getBillingGroup().setName("EditBillingGroup");

    ChannelForm channelForm = new ChannelForm();

    channelForm.setChannel(modifiedChannel);
    channelForm.setChannelId(existingChannel.getId().toString());

    channelController.editChannel(channelForm, null, map, response);

    Channel fetchedChannel = channelService.getChannelById(targetChannelId);

    List<BillingGroup> billingGroupsAfterRun = (List<BillingGroup>) channelService.getBillingGroups();
    Assert.assertEquals(billingGroupsBeforeRun.size() + 1, billingGroupsAfterRun.size());
    Assert.assertEquals(existingChannel.getBillingGroup().getName(), fetchedChannel.getBillingGroup().getName());
    Assert.assertEquals(existingChannel.getBillingGroup().getDescription(), fetchedChannel.getBillingGroup()
        .getDescription());

  }

  /**
   * Author: Abhaik
   */
  @Test
  public void testEditChannelWithExistingBillingGroup() {

    List<BillingGroup> billingGroupsBeforeRun = (List<BillingGroup>) channelService.getBillingGroups();

    String targetChannelId = "3";
    Channel existingChannel = channelService.getChannelById(targetChannelId);

    ChannelForm channelForm = new ChannelForm();
    Channel modifiedChannel = new Channel();
    modifiedChannel.setCatalog(existingChannel.getCatalog());
    modifiedChannel.setChannelBrandingConfigurations(existingChannel.getChannelBrandingConfigurations());
    modifiedChannel.setCode(existingChannel.getCode());
    modifiedChannel.setDescription(existingChannel.getDescription());
    modifiedChannel.setName(existingChannel.getName());
    modifiedChannel.setType(existingChannel.getType());
    BillingGroup bg = new BillingGroup();
    modifiedChannel.setBillingGroup(bg);
    modifiedChannel.getBillingGroup().setName("cat2");

    ChannelBrandingConfigurations channelBrandingConfigurations = createChannelBrandingConfigurations();
    channelBrandingConfigurations.setChannel(existingChannel);
    existingChannel.setChannelBrandingConfigurations(channelBrandingConfigurations);

    channelForm.setChannel(modifiedChannel);
    channelForm.setChannelId(existingChannel.getId().toString());
    channelForm.setBillingGroupId(existingChannel.getBillingGroup().getId());

    channelController.editChannel(channelForm, null, map, response);

    Channel fetchedChannel = channelService.getChannelById(targetChannelId);

    List<BillingGroup> billingGroupsAfterRun = (List<BillingGroup>) channelService.getBillingGroups();
    Assert.assertEquals(billingGroupsBeforeRun.size(), billingGroupsAfterRun.size());
    Assert.assertEquals(existingChannel.getBillingGroup().getName(), fetchedChannel.getBillingGroup().getName());
    Assert.assertEquals(existingChannel.getBillingGroup().getDescription(), fetchedChannel.getBillingGroup()
        .getDescription());
  }

  /**
   * Author: Abhaik
   */
  @Test
  public void testEditChannelWithEmptyBillingGroup() {

    List<BillingGroup> billingGroupsBeforeRun = (List<BillingGroup>) channelService.getBillingGroups();

    String targetChannelId = "7";
    Channel existingChannel = channelService.getChannelById(targetChannelId);

    ChannelForm channelForm = new ChannelForm();
    Channel modifiedChannel = new Channel();
    modifiedChannel.setCatalog(existingChannel.getCatalog());
    modifiedChannel.setChannelBrandingConfigurations(existingChannel.getChannelBrandingConfigurations());
    modifiedChannel.setCode(existingChannel.getCode());
    modifiedChannel.setDescription(existingChannel.getDescription());
    modifiedChannel.setName(existingChannel.getName());
    modifiedChannel.setType(existingChannel.getType());
    BillingGroup bg = new BillingGroup();
    modifiedChannel.setBillingGroup(bg);
    modifiedChannel.getBillingGroup().setName("");

    ChannelBrandingConfigurations channelBrandingConfigurations = createChannelBrandingConfigurations();
    channelBrandingConfigurations.setChannel(existingChannel);
    existingChannel.setChannelBrandingConfigurations(channelBrandingConfigurations);

    channelForm.setChannel(modifiedChannel);
    channelForm.setChannelId(existingChannel.getId().toString());
    channelForm.setBillingGroupId(existingChannel.getBillingGroup().getId());

    channelController.editChannel(channelForm, null, map, response);

    Channel fetchedChannel = channelService.getChannelById(targetChannelId);

    List<BillingGroup> billingGroupsAfterRun = (List<BillingGroup>) channelService.getBillingGroups();
    Assert.assertEquals(billingGroupsBeforeRun.size(), billingGroupsAfterRun.size());
    Assert.assertEquals(existingChannel.getBillingGroup().getName(), fetchedChannel.getBillingGroup().getName());
    Assert.assertEquals(existingChannel.getBillingGroup().getDescription(), fetchedChannel.getBillingGroup()
        .getDescription());
  }

  /**
   * Author: Abhaik
   */
  @Test(expected = BeanValidationException.class)
  public void testEditChannelWithBillingGroupAsLongChar() {

    String targetChannelId = "3";
    Channel existingChannel = channelService.getChannelById(targetChannelId);

    ChannelForm channelForm = new ChannelForm();
    Channel modifiedChannel = new Channel();
    modifiedChannel.setCatalog(existingChannel.getCatalog());
    modifiedChannel.setChannelBrandingConfigurations(existingChannel.getChannelBrandingConfigurations());
    modifiedChannel.setCode(existingChannel.getCode());
    modifiedChannel.setDescription(existingChannel.getDescription());
    modifiedChannel.setName(existingChannel.getName());
    modifiedChannel.setType(existingChannel.getType());
    BillingGroup bg = new BillingGroup();
    modifiedChannel.setBillingGroup(bg);
    modifiedChannel
        .getBillingGroup()
        .setName(
            "More then 256 char aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");

    ChannelBrandingConfigurations channelBrandingConfigurations = createChannelBrandingConfigurations();
    channelBrandingConfigurations.setChannel(existingChannel);
    existingChannel.setChannelBrandingConfigurations(channelBrandingConfigurations);

    channelForm.setChannel(modifiedChannel);
    channelForm.setChannelId(existingChannel.getId().toString());
    channelForm.setBillingGroupId(existingChannel.getBillingGroup().getId());

    channelController.editChannel(channelForm, null, map, response);
  }
}
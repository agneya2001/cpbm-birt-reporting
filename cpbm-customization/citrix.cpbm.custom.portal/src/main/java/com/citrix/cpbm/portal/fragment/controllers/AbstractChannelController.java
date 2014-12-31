/*
 * Copyright © 2013 Citrix Systems, Inc. You may not use, copy, or modify this file except pursuant to a valid license
 * agreement from Citrix Systems, Inc.
 */
package com.citrix.cpbm.portal.fragment.controllers;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.citrix.cpbm.platform.admin.service.ConnectorConfigurationManager;
import com.citrix.cpbm.platform.spi.CloudConnectorFactory.ConnectorType;
import com.vmops.internal.service.HtmlSanitizerService;
import com.vmops.internal.service.UsageService;
import com.vmops.model.Catalog;
import com.vmops.model.Channel;
import com.vmops.model.Channel.ChannelType;
import com.vmops.model.ChannelBrandingConfigurations;
import com.vmops.model.ChannelSettingServiceConfigMetadata;
import com.vmops.model.Configuration;
import com.vmops.model.Country;
import com.vmops.model.CurrencyValue;
import com.vmops.model.Product;
import com.vmops.model.ProductBundle;
import com.vmops.model.ProductBundleRevision;
import com.vmops.model.ProductCharge;
import com.vmops.model.ProductRevision;
import com.vmops.model.RateCardCharge;
import com.vmops.model.Revision;
import com.vmops.model.Service;
import com.vmops.model.ServiceInstance;
import com.vmops.model.ServiceInstanceConfig;
import com.vmops.model.SupportedCurrency;
import com.vmops.model.UnPublishedChannelBrandingConfigurations;
import com.vmops.portal.config.Configuration.Names;
import com.vmops.service.ChannelService;
import com.vmops.service.ConfigurationService;
import com.vmops.service.CurrencyValueService;
import com.vmops.service.ProductBundleService;
import com.vmops.service.ProductService;
import com.vmops.service.exceptions.CurrencyPrecisionException;
import com.vmops.service.exceptions.InvalidAjaxRequestException;
import com.vmops.service.exceptions.NoSuchChannelException;
import com.vmops.service.exceptions.ServiceException;
import com.vmops.utils.DateTimeUtils;
import com.vmops.utils.DateUtils;
import com.vmops.utils.JSONUtils;
import com.vmops.web.controllers.AbstractAuthenticatedController;
import com.vmops.web.controllers.menu.Page;
import com.vmops.web.forms.ChannelBrandingForm;
import com.vmops.web.forms.ChannelForm;
import com.vmops.web.forms.ChannelServiceSetting;
import com.vmops.web.forms.ChannelServiceSettingsForm;
import com.vmops.web.forms.ServiceList;
import com.vmops.web.validators.ChannelBrandingFormValidator;

public class AbstractChannelController extends AbstractAuthenticatedController {

// CHECKSTYLE:OFF

  @Autowired
  private HtmlSanitizerService htmlSanitizerService;

  @Autowired
  protected ChannelService channelService;

  @Autowired
  protected CurrencyValueService currencyValueService;

// CHECKSTYLE:ON

  @Autowired
  private ProductService productService;

  @Autowired
  private ProductBundleService productBundleService;

  @Autowired
  private ConfigurationService configurationService;

  @Autowired
  private UsageService usageService;

// CHECKSTYLE:OFF
  @Autowired
  protected ConnectorConfigurationManager connectorConfigurationManager;

// CHECKSTYLE:ON

  private static final int BUNDLES_PER_PAGE = 5;

  class ProductSortOrderSort implements Comparator<Product> {

    @Override
    public int compare(Product prod1, Product prod2) {
      if (prod1.getSortOrder() > prod2.getSortOrder()) {
        return 1;
      } else if (prod1.getSortOrder() == prod2.getSortOrder()) {
        return Integer.valueOf(prod1.getId().toString()) - Integer.valueOf(prod2.getId().toString());
      } else {
        return -1;
      }
    }
  }

  class CurrencyValueSort implements Comparator<CurrencyValue> {

    @Override
    public int compare(CurrencyValue currVal1, CurrencyValue currVal2) {
      if (currVal1.getRank() == currVal2.getRank()) {
        return 0;
      } else if (currVal1.getRank() < currVal2.getRank()) {
        return 1;
      } else {
        return -1;
      }
    }
  }

  private List<ProductBundleRevision> getProductBundles(List<ProductBundleRevision> productBundleRevisions,
      String currentPage, String perPageCount) {
    int pageNo;
    int perPage;

    try {
      pageNo = Integer.parseInt(currentPage);
    } catch (NumberFormatException nFE) {
      pageNo = 1;
    }

    try {
      perPage = Integer.parseInt(perPageCount);
      if (perPage > BUNDLES_PER_PAGE) {
        perPage = BUNDLES_PER_PAGE;
      }
    } catch (NumberFormatException nFE) {
      perPage = BUNDLES_PER_PAGE;
    }

    List<ProductBundleRevision> productBundleRevisionList = new ArrayList<ProductBundleRevision>();
    int count = 0;
    int toStartFrom = (pageNo - 1) * perPage;
    for (ProductBundleRevision productBundleRevision : productBundleRevisions) {
      ProductBundle productBundle = productBundleRevision.getProductBundle();
      if (productBundle.getRemoved() != null) {
        continue;
      }
      if (count >= toStartFrom) {
        productBundleRevisionList.add(productBundleRevision);
        if (productBundleRevisionList.size() == perPage || count == productBundleRevisionList.size()) {
          break;
        }
      }
      count += 1;
    }

    logger.debug("Leaving listProductBundlesByCatalog");
    return productBundleRevisionList;
  }

  private Map<Product, Map<CurrencyValue, Map<String, ProductCharge>>> getProductChargeMap(Channel channel,
      String timeline, Date date, boolean forUpdate) {

    // Structure is of the form::
    //
    // "Product":{ "CurrencyVal(forUSD)":{ "catalog": ProductCharge, "rpb":
// ProductCharge },
    // "CurrencyVal(forINR)": ...

    Map<Product, Map<CurrencyValue, Map<String, ProductCharge>>> fullProductPricingMap = new TreeMap<Product, Map<CurrencyValue, Map<String, ProductCharge>>>(
        new ProductSortOrderSort());

    List<ProductRevision> catalogProductRevisions = new ArrayList<ProductRevision>();
    Map<Product, ProductRevision> rpbProductRevisionMap = new HashMap<Product, ProductRevision>();
    if (timeline.equals("current")) {
      catalogProductRevisions = channelService.getChannelRevision(channel,
          channelService.getCurrentRevision(channel).getStartDate(), false).getProductRevisions();

      rpbProductRevisionMap = channelService.getChannelRevision(null,
          channelService.getCurrentRevision(channel).getStartDate(), false).getProductRevisionsMap();

    } else if (timeline.equals("planned")) {
      catalogProductRevisions = channelService.getChannelRevision(channel,
          channelService.getFutureRevision(channel).getStartDate(), false).getProductRevisions();

      rpbProductRevisionMap = channelService.getChannelRevision(null,
          channelService.getFutureRevision(channel).getStartDate(), false).getProductRevisionsMap();

    } else if (timeline.equals("history")) {
      catalogProductRevisions = channelService.getChannelRevision(channel, date, false).getProductRevisions();

      rpbProductRevisionMap = channelService.getChannelRevision(null, date, false).getProductRevisionsMap();
    }

    for (ProductRevision productRevision : catalogProductRevisions) {
      ProductRevision rpbProductRevision = rpbProductRevisionMap.get(productRevision.getProduct());
      Map<CurrencyValue, Map<String, ProductCharge>> currencyProductPriceMap = new TreeMap<CurrencyValue, Map<String, ProductCharge>>(
          new CurrencyValueSort());
      for (ProductCharge productCharge : productRevision.getProductCharges()) {
        if (currencyProductPriceMap.get(productCharge.getCurrencyValue()) == null) {
          currencyProductPriceMap.put(productCharge.getCurrencyValue(), new LinkedHashMap<String, ProductCharge>());
        }
        if (forUpdate) {
          try {
            productCharge.setPrice(productCharge.getPrice().setScale(
                Integer.parseInt(config.getValue(Names.com_citrix_cpbm_portal_appearance_currency_precision)),
                BigDecimal.ROUND_UNNECESSARY));
          } catch (ArithmeticException aex) {
            logger.error("ArithmeticException while editing the product charge, Possible Cause- "
                + "the currency precision level was reduced " + aex);
            throw new CurrencyPrecisionException(aex);
          }
        }
        currencyProductPriceMap.get(productCharge.getCurrencyValue()).put("catalog", productCharge);
        if (rpbProductRevision == null) {
          currencyProductPriceMap.get(productCharge.getCurrencyValue()).put("rpb", null);
        }
      }
      if (rpbProductRevision != null) {
        for (ProductCharge productCharge : rpbProductRevision.getProductCharges()) {
          if (currencyProductPriceMap.get(productCharge.getCurrencyValue()) != null) {
            if (forUpdate) {
              try {
                productCharge.setPrice(productCharge.getPrice().setScale(
                    Integer.parseInt(config.getValue(Names.com_citrix_cpbm_portal_appearance_currency_precision)),
                    BigDecimal.ROUND_UNNECESSARY));
              } catch (ArithmeticException aex) {
                logger.error("ArithmeticException while editing the product charge, Possible Cause- "
                    + "the currency precision level was reduced " + aex);
                throw new CurrencyPrecisionException(aex);
              }
            }
            currencyProductPriceMap.get(productCharge.getCurrencyValue()).put("rpb", productCharge);
          }
        }
      }
      fullProductPricingMap.put(productRevision.getProduct(), currencyProductPriceMap);
    }

    return fullProductPricingMap;
  }

  private Map<ProductBundleRevision, Map<CurrencyValue, Map<String, RateCardCharge>>> getBundlePricingMap(
      Channel channel, String timeline, Date date, boolean forUpdate) {
    // Structure is of the form::
    //
    // "ProductBundleRevision":{ "CurrencyVal(forUSD)":{ "catalog-onetime":
// RateCardCharge,
    // "rpb-onetime":RateCardCharge,
    // "catalog-recurring": RateCardCharge,
    // "rpb-recurring": RateCardCharge},
    // "CurrencyVal(forINR)": ...
    // ....

    Map<ProductBundleRevision, Map<CurrencyValue, Map<String, RateCardCharge>>> fullBundlePricingMap = new LinkedHashMap<ProductBundleRevision, Map<CurrencyValue, Map<String, RateCardCharge>>>();

    List<ProductBundleRevision> catalogProductBundleRevisions = new ArrayList<ProductBundleRevision>();
    Map<ProductBundle, ProductBundleRevision> rpbProductBundleRevisionMap = new HashMap<ProductBundle, ProductBundleRevision>();
    if (timeline.equals("current")) {
      catalogProductBundleRevisions = channelService.getChannelRevision(channel,
          channelService.getCurrentRevision(channel).getStartDate(), false).getProductBundleRevisions();

      rpbProductBundleRevisionMap = channelService.getChannelRevision(null,
          channelService.getCurrentRevision(channel).getStartDate(), false).getProductBundleRevisionsMap();

    } else if (timeline.equals("planned")) {
      catalogProductBundleRevisions = channelService.getChannelRevision(channel,
          channelService.getFutureRevision(channel).getStartDate(), false).getProductBundleRevisions();

      rpbProductBundleRevisionMap = channelService.getChannelRevision(null,
          channelService.getFutureRevision(channel).getStartDate(), false).getProductBundleRevisionsMap();
    } else if (timeline.equals("history")) {
      catalogProductBundleRevisions = channelService.getChannelRevision(channel, date, false)
          .getProductBundleRevisions();

      rpbProductBundleRevisionMap = channelService.getChannelRevision(null, date, false).getProductBundleRevisionsMap();
    }

    for (ProductBundleRevision catalaogProductBundleRevision : catalogProductBundleRevisions) {

      Map<CurrencyValue, Map<String, RateCardCharge>> currencyProductBundlePriceMap = new TreeMap<CurrencyValue, Map<String, RateCardCharge>>(
          new CurrencyValueSort());

      ProductBundleRevision rpbProductBundleRevision = rpbProductBundleRevisionMap.get(catalaogProductBundleRevision
          .getProductBundle());

      for (RateCardCharge rcc : catalaogProductBundleRevision.getRateCardCharges()) {
        if (currencyProductBundlePriceMap.get(rcc.getCurrencyValue()) == null) {
          currencyProductBundlePriceMap.put(rcc.getCurrencyValue(), new LinkedHashMap<String, RateCardCharge>());
        }

        if (forUpdate) {
          try {
            rcc.setPrice(rcc.getPrice().setScale(
                Integer.parseInt(config.getValue(Names.com_citrix_cpbm_portal_appearance_currency_precision)),
                BigDecimal.ROUND_UNNECESSARY));
          } catch (ArithmeticException aex) {
            logger.error("ArithmeticException while editing the product charge, Possible Cause- "
                + "the currency precision level was reduced " + aex);
            throw new CurrencyPrecisionException(aex);

          }
        }
        if (rcc.getRateCardComponent().isRecurring()) {
          currencyProductBundlePriceMap.get(rcc.getCurrencyValue()).put("catalog-recurring", rcc);
        } else {
          currencyProductBundlePriceMap.get(rcc.getCurrencyValue()).put("catalog-onetime", rcc);
        }
      }
      for (RateCardCharge rcc : rpbProductBundleRevision.getRateCardCharges()) {
        if (currencyProductBundlePriceMap.get(rcc.getCurrencyValue()) != null) {
          if (forUpdate) {
            try {
              rcc.setPrice(rcc.getPrice().setScale(
                  Integer.parseInt(config.getValue(Names.com_citrix_cpbm_portal_appearance_currency_precision)),
                  BigDecimal.ROUND_UNNECESSARY));
            } catch (ArithmeticException aex) {
              logger.error("ArithmeticException while editing the product charge, Possible Cause- "
                  + "the currency precision level was reduced " + aex);
              throw new CurrencyPrecisionException(aex);
            }
          }
          if (rcc.getRateCardComponent().isRecurring()) {
            currencyProductBundlePriceMap.get(rcc.getCurrencyValue()).put("rpb-recurring", rcc);
          } else {
            currencyProductBundlePriceMap.get(rcc.getCurrencyValue()).put("rpb-onetime", rcc);
          }
        }
      }
      fullBundlePricingMap.put(catalaogProductBundleRevision, currencyProductBundlePriceMap);
    }
    return fullBundlePricingMap;
  }

  @RequestMapping(value = "/list", method = RequestMethod.GET)
  public String list(@RequestParam(value = "page", required = false, defaultValue = "1") String currentPage,
      @RequestParam(value = "namePattern", required = false, defaultValue = "") String namePattern,
      @RequestParam(value = "billingGroup", required = false) String billingGroupId, ModelMap map) {
    logger.debug("### list method starting...(GET)");
    int page;
    int perPage;
    try {
      page = Integer.parseInt(currentPage);
    } catch (NumberFormatException nFE) {
      page = 1;
    }
    try {
      perPage = 13;// getDefaultPageSize();
      if (perPage > 14) {
        perPage = 14;
      }
    } catch (NumberFormatException nFE) {
      perPage = 14;
    }

    setPage(map, Page.CHANNELS);

    List<Channel> channels = channelService.getChannels(page, perPage, namePattern, billingGroupId);
    map.addAttribute("channels", channels);
    map.addAttribute("channelsize", channels.size());

    int totalSize = channelService.count(null, billingGroupId);
    if (totalSize - (page * perPage) > 0) {
      map.addAttribute("enable_next", true);
    } else {
      map.addAttribute("enable_next", false);
    }
    map.addAttribute("current_page", page);

    map.addAttribute("billingGroups", channelService.getBillingGroups());
    map.addAttribute("channelCreationAllowed", true);
    if (!channelService.isChannelCreationAllowed()) {
      map.addAttribute("channelCreationAllowed", false);
    }
    map.addAttribute("defaultChannel", channelService.getDefaultServiceProviderChannel());
    logger.debug("### list method ending...(GET)");
    return "channels.list";
  }

  /**
   * List all the products
   * 
   * @return
   */
  @RequestMapping(value = "/searchchannel", method = RequestMethod.GET)
  public String searchChannelByPattern(
      @RequestParam(value = "currentPage", required = false, defaultValue = "1") String currentPage,
      @RequestParam(value = "namePattern", required = false) String namePattern,
      @RequestParam(value = "billingGroup", required = false) String billingGroupId, ModelMap map) {
    logger.debug("### searchChannelByPattern method starting...(GET)");

    list(currentPage, namePattern, billingGroupId, map);

    logger.debug("### searchChannelByPattern method ending...(GET)");
    return "channels.search";
  }

  @RequestMapping(value = ("/viewchannel"), method = RequestMethod.GET)
  public String viewChannel(@RequestParam(value = "Id", required = true) String id, ModelMap map) {
    logger.debug("### viewchannel method starting...(GET)");

    Channel channel = channelService.getChannelById(id);
    map.addAttribute("channel", channel);
    String defaultLocaleCode = channelService.getDefaultLocale(channel);
    map.addAttribute("defaultLocale", defaultLocaleCode);
    Locale defaultLocale = LocaleUtils.toLocale(channelService.getDefaultLocale(channel));
    map.addAttribute("defaultLocaleValue", getLocaleDisplayName(defaultLocale));
    map.addAttribute("defaultTimeZone",
        DateTimeUtils.getTimeZoneDisplayName(channelService.getDefaultTimeZone(channel)));

    String blackListCountries = channel.getChannelBrandingConfigurations().getBlacklistcountries();
    String whiteListCountries = channel.getChannelBrandingConfigurations().getWhitelistcountries();

    if (StringUtils.isBlank(blackListCountries)) {
      blackListCountries = config.getValue(Names.com_citrix_cpbm_accountManagement_onboarding_country_blacklist);
      map.addAttribute("blacklistcountries_global", true);
    }
    map.addAttribute("blacklistcountries", getCountriesByCodes(blackListCountries));
    if (StringUtils.isBlank(whiteListCountries)) {
      whiteListCountries = config.getValue(Names.com_citrix_cpbm_accountManagement_onboarding_country_whitelist);
      map.addAttribute("whitelistcountries_global", true);
    }
    map.addAttribute("whitelistcountries", getCountriesByCodes(whiteListCountries));

    Locale globalDefaultLocale = new Locale(config.getDefaultLocale());
    map.addAttribute("global_default_locale", getLocaleDisplayName(globalDefaultLocale));
    map.addAttribute("global_default_timezone", config.getValue(Names.com_citrix_cpbm_portal_settings_default_timezone));
    map.addAttribute("help_desk_phone", config.getValue(Names.com_citrix_cpbm_portal_settings_helpdesk_phone));
    map.addAttribute("help_desk_email", config.getValue(Names.com_citrix_cpbm_portal_addressbook_helpDeskEmail));
    map.addAttribute("global_signup_allowed", config.getValue(Names.com_citrix_cpbm_portal_settings_allow_signup));

    map.addAttribute("whitelistdomains",
        config.getValue(Names.com_citrix_cpbm_accountManagement_onboarding_emailDomain_whitelist));
    map.addAttribute("blacklistdomains",
        config.getValue(Names.com_citrix_cpbm_accountManagement_onboarding_emailDomain_blacklist));
    map.addAttribute("marketing_support_url", config.getValue(Names.com_citrix_cpbm_portal_marketing_support_url));
    map.addAttribute("marketing_blog_url", config.getValue(Names.com_citrix_cpbm_portal_marketing_blog_url));
    map.addAttribute("marketing_forum_url", config.getValue(Names.com_citrix_cpbm_portal_marketing_forum_url));
    map.addAttribute("marketing_contact_url", config.getValue(Names.com_citrix_cpbm_portal_marketing_contact_url));
    map.addAttribute("marketing_privacy_url", config.getValue(Names.com_citrix_cpbm_portal_marketing_privacy_url));
    map.addAttribute("marketing_help_url", config.getValue(Names.com_citrix_cpbm_portal_marketing_help_url));
    map.addAttribute("marketing_tou_url", config.getValue(Names.com_citrix_cpbm_portal_marketing_tou_url));

    Configuration dataConfig = configurationService
        .locateConfigurationByName(Names.com_citrix_cpbm_portal_settings_services_datapath);
    String logoPath = "";
    if (StringUtils.isNotBlank(dataConfig.getValue()) && channel.getChannelBrandingConfigurations() != null
        && StringUtils.isNotBlank(channel.getChannelBrandingConfigurations().getLogoImageFileName())) {
      logoPath = channel.getChannelBrandingConfigurations().getLogoImageFileName();
    }
    map.addAttribute("channelLogoPath", logoPath);
    boolean currenciestoadd = true;
    if (currencyValueService.listActiveCurrencies().size() == channel.getCatalog().getSupportedCurrencies().size()) {
      currenciestoadd = false;
    }

    List<CurrencyValue> listSupportedCurrencies = channel.getCatalog().getSupportedCurrencyValuesByOrder();

    map.addAttribute("isHistoryThere", false);
    List<Date> historyDatesForCatalog = productService.getHistoryDates(channel.getCatalog());
    if (historyDatesForCatalog != null && historyDatesForCatalog.size() > 0) {
      map.addAttribute("isHistoryThere", true);
    }

    map.addAttribute("isCurrentThere", false);
    Revision currentRevision = channelService.getCurrentRevision(channel);
    if (currentRevision != null && currentRevision.getStartDate() != null
        && (currentRevision.getStartDate().getTime() <= (new Date()).getTime())) {
      map.addAttribute("isCurrentThere", true);
    }

    map.addAttribute("supportedCurrencies", listSupportedCurrencies);
    map.addAttribute("currenciestoadd", currenciestoadd);
    map.addAttribute("futureRevisionDate", channelService.getFutureRevision(channel).getStartDate());
    map.addAttribute("effectiveDate", channelService.getFutureRevision(channel).getStartDate());
    map.addAttribute("isChannelDeletionAllowed", channelService.isChannelDeletionAllowed(channel));
    map.addAttribute("cloudService", getServiceAndServiceInstanceList().getServices());
    map.addAttribute("publicHost", config.getPublicHost());

    List<Service> services = connectorConfigurationManager.getAllServicesByType(ConnectorType.CLOUD.toString());
    if (CollectionUtils.isNotEmpty(services)) {
      boolean foundInstance = false;
      map.addAttribute("services", true);
      for (Service service : services) {
        if (CollectionUtils.isNotEmpty(service.getServiceInstances())) {
          foundInstance = true;
          break;
        }
      }
      if (foundInstance) {
        map.addAttribute("instances", true);
      } else {
        map.addAttribute("instances", false);
      }
    } else {
      map.addAttribute("services", false);
      map.addAttribute("instances", false);
    }
    map.addAttribute("previewModeInCurrentChannel", channel.getUnPublishedChannelBrandingConfigurations());
    map.addAttribute("isDefaultChannel",
        channelService.getDefaultServiceProviderChannel().getUuid().equals(channel.getUuid()));
    logger.debug("### viewchannel method end...(GET)");
    return "channels.view";
  }

  @RequestMapping(value = ("/editchannel"), method = RequestMethod.GET)
  public String editChannel(@RequestParam(value = "Id", required = true) String id, ModelMap map) {
    logger.debug("### editChannel method starting...(GET)");

    Channel channel = channelService.getChannelById(id);
    ChannelForm channelForm = new ChannelForm(channel);
    map.addAttribute("channelForm", channelForm);

    String blackListCountries = channel.getChannelBrandingConfigurations().getBlacklistcountries();
    String whiteListCountries = channel.getChannelBrandingConfigurations().getWhitelistcountries();

    if (StringUtils.isBlank(blackListCountries)) {
      blackListCountries = config.getValue(Names.com_citrix_cpbm_accountManagement_onboarding_country_blacklist);

    }
    map.addAttribute("channel_blacklistcountries", getCountriesByCodes(blackListCountries));
    if (StringUtils.isBlank(whiteListCountries)) {
      whiteListCountries = config.getValue(Names.com_citrix_cpbm_accountManagement_onboarding_country_whitelist);

    }
    map.addAttribute("channel_whitelistcountries", getCountriesByCodes(whiteListCountries));

    map.addAttribute("supportedLocaleList", this.getLocaleDisplayName(listSupportedLocales()));
    map.addAttribute("supportedZoneList", DateTimeUtils.getAvailableTimeZones());
    map.addAttribute("channelLocale", channel.getChannelBrandingConfigurations().getDefaultLocale());
    map.addAttribute("channelTimeZone", channelService.getDefaultTimeZone(channel));
    Locale defaultLocale = new Locale(config.getDefaultLocale());
    map.addAttribute("defaultLocaleValue", getLocaleDisplayName(defaultLocale));
    map.addAttribute("defaultTimeZone", config.getValue(Names.com_citrix_cpbm_portal_settings_default_timezone));
    map.addAttribute("help_desk_phone", config.getValue(Names.com_citrix_cpbm_portal_settings_helpdesk_phone));
    map.addAttribute("help_desk_email", config.getValue(Names.com_citrix_cpbm_portal_addressbook_helpDeskEmail));
    map.addAttribute("signup_allowed", config.getValue(Names.com_citrix_cpbm_portal_settings_allow_signup));
    map.addAttribute("whitelistcountries",
        config.getValue(Names.com_citrix_cpbm_accountManagement_onboarding_country_whitelist));
    map.addAttribute("blacklistcountries",
        config.getValue(Names.com_citrix_cpbm_accountManagement_onboarding_country_blacklist));
    map.addAttribute("whitelistdomains",
        config.getValue(Names.com_citrix_cpbm_accountManagement_onboarding_emailDomain_whitelist));
    map.addAttribute("blacklistdomains",
        config.getValue(Names.com_citrix_cpbm_accountManagement_onboarding_emailDomain_blacklist));

    map.addAttribute("currencies", channel.getCatalog().getSupportedCurrencyValuesByOrder());
    map.addAttribute("billingGroups", channelService.getBillingGroups());
    map.addAttribute("selectedCategory", channel.getBillingGroup() != null ? channel.getBillingGroup().getName() : null);
    map.addAttribute("publicHost", config.getPublicHost());
    map.addAttribute("publicProtocol", config.getPublicProtocol());
    map.addAttribute("publicPort", config.getPublicPort());
    map.addAttribute("marketing_support_url", config.getValue(Names.com_citrix_cpbm_portal_marketing_support_url));
    map.addAttribute("marketing_blog_url", config.getValue(Names.com_citrix_cpbm_portal_marketing_blog_url));
    map.addAttribute("marketing_forum_url", config.getValue(Names.com_citrix_cpbm_portal_marketing_forum_url));
    map.addAttribute("marketing_contact_url", config.getValue(Names.com_citrix_cpbm_portal_marketing_contact_url));
    map.addAttribute("marketing_privacy_url", config.getValue(Names.com_citrix_cpbm_portal_marketing_privacy_url));
    map.addAttribute("marketing_help_url", config.getValue(Names.com_citrix_cpbm_portal_marketing_help_url));
    map.addAttribute("marketing_tou_url", config.getValue(Names.com_citrix_cpbm_portal_marketing_tou_url));
    if (channel.equals(channelService.getDefaultServiceProviderChannel())
        && Boolean.valueOf(config.getValue(Names.com_citrix_cpbm_portal_settings_allow_signup))) {
      channel.getChannelBrandingConfigurations().setSignupAllowed(true);
      map.addAttribute("isDefaultChannel", true);
    }
    List<Country> allCountries = countryService.getCountries(null, null, null, null, null, null, null);
    map.addAttribute("all_countries", allCountries);
    logger.debug("### editChannel method ending...(GET)");
    return "channels.edit";
  }

  @RequestMapping(value = ("/editchannel"), method = RequestMethod.POST)
  public String editChannel(@ModelAttribute("channelForm") ChannelForm form, BindingResult result, ModelMap map,
      HttpServletResponse response) {
    logger.debug("### editChannel method starting...(POST)");

    Channel origChannel = channelService.getChannelById(form.getChannelId());
    Channel modifiedChannel = form.getChannel();

    channelService.updateChannel(origChannel, modifiedChannel);

    logger.debug("### editChannel method ending...(POST)");
    return "channels.view";
  }

  @RequestMapping(value = ("/createchannel"), method = RequestMethod.GET)
  public String createChannel(ModelMap map, HttpServletResponse response) {
    logger.debug("### createChannel method starting...(GET)");
    // Add precondition
    if (!channelService.isChannelCreationAllowed()) {
      response.setStatus(HttpStatus.PRECONDITION_FAILED.value());
      logger.debug("### createChannel(GET) method ending. PreConditions failed.");
      return null;
    }
    ChannelForm channelForm = new ChannelForm();
    map.addAttribute("channelForm", channelForm);
    map.addAttribute("channels", channelService.getChannels(null, null, null));
    map.addAttribute("currencies", currencyValueService.listActiveCurrencies());
    map.addAttribute("billingGroups", channelService.getBillingGroups());
    map.addAttribute("publicHost", config.getPublicHost());
    map.addAttribute("publicProtocol", config.getPublicProtocol());
    map.addAttribute("publicPort", config.getPublicPort());
    map.addAttribute("marketing_support_url", config.getValue(Names.com_citrix_cpbm_portal_marketing_support_url));
    map.addAttribute("marketing_blog_url", config.getValue(Names.com_citrix_cpbm_portal_marketing_blog_url));
    map.addAttribute("marketing_forum_url", config.getValue(Names.com_citrix_cpbm_portal_marketing_forum_url));
    map.addAttribute("marketing_contact_url", config.getValue(Names.com_citrix_cpbm_portal_marketing_contact_url));
    map.addAttribute("marketing_privacy_url", config.getValue(Names.com_citrix_cpbm_portal_marketing_privacy_url));
    map.addAttribute("marketing_help_url", config.getValue(Names.com_citrix_cpbm_portal_marketing_help_url));
    map.addAttribute("marketing_tou_url", config.getValue(Names.com_citrix_cpbm_portal_marketing_tou_url));
    map.addAttribute("supportedLocaleList", this.getLocaleDisplayName(listSupportedLocales()));
    map.addAttribute("supportedZoneList", DateTimeUtils.getAvailableTimeZones());
    map.addAttribute("defaultLocale", config.getDefaultLocale());

    Locale defauleLocale = new Locale(config.getDefaultLocale());
    map.addAttribute("defaultLocaleValue", getLocaleDisplayName(defauleLocale));

    map.addAttribute("defaultTimeZone", config.getValue(Names.com_citrix_cpbm_portal_settings_default_timezone));
    map.addAttribute("help_desk_phone", config.getValue(Names.com_citrix_cpbm_portal_settings_helpdesk_phone));
    map.addAttribute("help_desk_email", config.getValue(Names.com_citrix_cpbm_portal_addressbook_helpDeskEmail));
    map.addAttribute("signup_allowed", config.getValue(Names.com_citrix_cpbm_portal_settings_allow_signup));
    map.addAttribute("whitelistcountries",
        config.getValue(Names.com_citrix_cpbm_accountManagement_onboarding_country_whitelist));
    map.addAttribute("blacklistcountries",
        config.getValue(Names.com_citrix_cpbm_accountManagement_onboarding_country_blacklist));
    map.addAttribute("whitelistdomains",
        config.getValue(Names.com_citrix_cpbm_accountManagement_onboarding_emailDomain_whitelist));
    map.addAttribute("blacklistdomains",
        config.getValue(Names.com_citrix_cpbm_accountManagement_onboarding_emailDomain_blacklist));
    List<Country> allCountries = countryService.getCountries(null, null, null, null, null, null, null);
    map.addAttribute("all_countries", allCountries);

    logger.debug("### createChannel method end...(GET)");
    return "channels.create";
  }

  /**
   * @param form
   * @param result
   * @param map
   * @param response
   * @return
   */
  @RequestMapping(value = ("/createchannel"), method = RequestMethod.POST)
  @ResponseBody
  public Channel createChannel(@ModelAttribute("channelForm") ChannelForm form, BindingResult result, ModelMap map,
      HttpServletResponse response) {

    logger.debug("### createChannel method starting...(POST)");

    List<CurrencyValue> currencyValues = new ArrayList<CurrencyValue>();
    for (String currency : form.getCurrencies()) {

      if (StringUtils.isNotBlank(currency)) {
        currencyValues.add(currencyValueService.locateBYCurrencyCode(currency));
      }
    }
    Channel channel = form.getChannel();

    channel.setType(ChannelType.valueOf("CHANNEL"));
    channel.getChannelBrandingConfigurations().setChannel(channel);

    channel = channelService.createChannel(channel, currencyValues);
    logger.debug("### createChannel method end...(POST)");
    return channel;
  }

  @RequestMapping(value = "/validate_channelname")
  @ResponseBody
  public String validateChannelName(@RequestParam("channelName") final String channelName) {
    logger.debug("### validateChannelName start and channelname is : " + channelName);

    try {
      channelService.locateChannel(channelName);
    } catch (NoSuchChannelException ex) {
      logger.debug(channelName + ": doesn't exist in the db channel table");
      if (config.getBooleanValue(Names.com_citrix_cpbm_portal_directory_service_enabled)
          && config.getValue(Names.com_citrix_cpbm_directory_mode).equals("push")) {
        try {
          channelService.locateChannelInDirectoryService(channelName);
          return Boolean.FALSE.toString();
        } catch (NoSuchChannelException exc) {
          logger.debug(channelName + ": doesn't exist in the directory server names in channel table");
          return Boolean.TRUE.toString();
        }
      }
      return Boolean.TRUE.toString();
    }

    logger.debug("### validateChannelName method end");
    return Boolean.FALSE.toString();
  }

  @RequestMapping(value = "/validate_fqdn_prefix")
  @ResponseBody
  public Map<String, Object> validateChannelFQDNPrefix(@RequestParam("fqdnPrefix") final String fqdnPrefix) {
    logger.debug("### validateChannelFQDN start and channelfqdn prefix is : " + fqdnPrefix);
    Map<String, Object> validateMap = new HashMap<String, Object>();
    // FIXME: Implement method for validate fqdn
    validateMap.put("valid", true);
    if (StringUtils.isNotBlank(fqdnPrefix)) {
      Channel channel = channelService.findByFqdnPrefix(fqdnPrefix);
      if (channel != null) {
        validateMap.put("valid", false);
      }
    }
    logger.debug("### validateChannelFQDN method end");
    return validateMap;
  }

  @RequestMapping(value = ("/deletechannel"), method = RequestMethod.POST)
  @ResponseBody
  public String deletechannel(@RequestParam(value = "Id", required = true) String channelId, ModelMap map) {
    logger.debug("### deletechannel method starting...(POST)");

    boolean status = false;
    try {
      status = channelService.removeChannelById(channelId);
    } catch (ServiceException ex) {
      throw new InvalidAjaxRequestException(ex.getMessage());
    }

    logger.debug("### deletechannel method end...(POST)");
    return status ? "success" : "failure";
  }

  @RequestMapping(value = ("/resetchannelbrandings"), method = RequestMethod.POST)
  @ResponseBody
  public String resetchannelbrandings(@RequestParam(value = "Id", required = true) String channelId, ModelMap map) {
    logger.debug("### resetchannelbrandings method starting...(POST)");
    try {
      channelService.resetChannelBrandingsToDefault(channelService.getChannelById(channelId));
    } catch (ServiceException ex) {
      throw new InvalidAjaxRequestException(ex.getMessage());
    }
    logger.debug("### resetchannelbrandings method end...(POST)");
    return "success";
  }

  @RequestMapping(value = ("/editchannelcurrency"), method = RequestMethod.GET)
  public String editChannelCurrency(@RequestParam(value = "channelId", required = true) String channelId, ModelMap map) {
    logger.debug("### editcurrency method starting...(GET)");

    Channel channel = channelService.getChannelById(channelId);
    Set<SupportedCurrency> supportedCurrencies = channel.getCatalog().getSupportedCurrencies();
    List<CurrencyValue> availableCurrencies = currencyValueService.listActiveCurrencies();
    if (supportedCurrencies != null) {
      for (SupportedCurrency supportedCurrency : supportedCurrencies) {
        availableCurrencies.remove(supportedCurrency.getCurrency());
      }
    }
    map.addAttribute("availableCurrencies", availableCurrencies);

    logger.debug("### editcurrency method end...(GET)");
    return "channels.currency.edit";
  }

  @RequestMapping(value = ("/editchannelcurrency"), method = RequestMethod.POST)
  @ResponseBody
  public String editChannelCurrency(@RequestParam(value = "channelId", required = true) String channelId,
      @RequestParam(value = "currencyCodeArray", required = true) String currencyCodeArray, ModelMap map)
      throws JSONException {
    logger.debug("### editcurrency method starting...(POST)");

    if (currencyCodeArray.equals("null")) {
      return "success";
    }

    boolean status = false;
    try {
      channelService.addCurrencyToChannel(channelId, currencyCodeArray);
      status = true;
    } catch (ServiceException ex) {
      throw new InvalidAjaxRequestException(ex.getMessage());
    }

    logger.debug("### editcurrency method end...(POST)");
    return status ? "success" : "failure";
  }

  @RequestMapping(value = "/listbundles", method = RequestMethod.GET)
  public String listbundles(@RequestParam(value = "channelId", required = true) String channelId, ModelMap map) {
    logger.debug("### listbundles method starting...(GET)");

    Channel channel = channelService.getChannelById(channelId);
    Revision futureRevision = channelService.getFutureRevision(channel);
    List<ProductBundleRevision> productBundleRevisions = channelService.getFutureChannelRevision(channel, false)
        .getProductBundleRevisions();
    List<ProductBundle> productBundlesInChannel = new ArrayList<ProductBundle>();
    for (ProductBundleRevision productBundleRevision : productBundleRevisions) {
      productBundlesInChannel.add(productBundleRevision.getProductBundle());
    }
    List<ProductBundleRevision> globalProductBundleRevisions = channelService.getChannelRevision(
        null,
        channelService.getChannelReferenceCatalogRevision(channel, futureRevision).getReferenceCatalogRevision()
            .getStartDate(), false).getProductBundleRevisions();

    List<ProductBundleRevision> bundlesToBeSent = new ArrayList<ProductBundleRevision>();
    for (ProductBundleRevision productBundleRevision : globalProductBundleRevisions) {
      if (productBundleRevision.getProductBundle().getPublish()
          && !productBundlesInChannel.contains(productBundleRevision.getProductBundle())) {
        bundlesToBeSent.add(productBundleRevision);
      }
    }

    map.addAttribute("productBundles", bundlesToBeSent);
    logger.debug("### listbundles method ending...(GET)");
    return "productbundle.add";
  }

  @RequestMapping(value = ("/attachproductbundles"), method = RequestMethod.POST)
  @ResponseBody
  public String attachProductBundles(@RequestParam(value = "channelId", required = true) String channelId,
      @RequestParam(value = "selectProductBundles", required = true) String selectedProductBundles, ModelMap map)
      throws JSONException {
    logger.debug("### attachProductBundles method starting...(POST)");

    if (selectedProductBundles.equals("null")) {
      return "success";
    }

    boolean status = false;
    try {
      Channel channel = channelService.getChannelById(channelId);
      JSONArray jsonArray = new JSONArray(selectedProductBundles);
      List<ProductBundle> productBundlesToAttach = new ArrayList<ProductBundle>();
      for (int index = 0; index < jsonArray.length(); index++) {
        String bundleId = jsonArray.getString(index);
        ProductBundle productBundle = productBundleService.getProductBundleById(Long.parseLong(bundleId));
        productBundlesToAttach.add(productBundle);
      }
      productBundleService.addBundlesToChannel(channel, productBundlesToAttach);
      status = true;
    } catch (ServiceException ex) {
      throw new InvalidAjaxRequestException(ex.getMessage());
    }
    logger.debug("### attachProductBundles method end...(POST)");
    return status ? "success" : "failure";
  }

  @RequestMapping(value = ("/editcatalogproductpricing"), method = RequestMethod.GET)
  public String editCatalogProductPricing(@RequestParam(value = "channelId", required = true) String channelId,
      ModelMap map) {
    logger.debug("### editCatalogProductPricing method starting...(GET)");

    Channel channel = channelService.getChannelById(channelId);
    Catalog catalog = channel.getCatalog();

    Map<Product, Map<CurrencyValue, Map<String, ProductCharge>>> fullProductPricingMap = getProductChargeMap(channel,
        "planned", null, true);

    map.addAttribute("planDate", channelService.getFutureRevision(channel).getStartDate());
    map.addAttribute("supportedCurrencies", catalog.getSupportedCurrencyValuesByOrder());
    map.addAttribute("fullProductPricingMap", fullProductPricingMap);

    logger.debug("### editCatalogProductPricing method end...(GET)");
    return "catalog.edit.channelpricing";
  }

  @RequestMapping(value = ("/editcatalogproductpricing"), method = RequestMethod.POST)
  public String editCatalogProductPricing(@RequestParam(value = "channelId", required = true) String channelId,
      @RequestParam(value = "currencyValData", required = true) String currencyValData, ModelMap map)
      throws JSONException {
    logger.debug("### editCatalogProductPricing method starting...(POST)");

    boolean status = false;

    try {
      Channel channel = channelService.getChannelById(channelId);
      JSONArray jsonArray = new JSONArray(currencyValData);
      List<ProductCharge> productCharges = new ArrayList<ProductCharge>();
      for (int index = 0; index < jsonArray.length(); index++) {
        JSONObject jsonObj = jsonArray.getJSONObject(index);
        BigDecimal previousVal = new BigDecimal(jsonObj.get("previousvalue").toString());
        BigDecimal newVal = new BigDecimal(jsonObj.get("value").toString());
        String currencyCode = jsonObj.get("currencycode").toString();
        String productId = jsonObj.get("productId").toString();
        if (!previousVal.equals(newVal)) {
          Product product = productService.locateProductById(productId);
          CurrencyValue currencyValue = currencyValueService.locateBYCurrencyCode(currencyCode);
          ProductCharge productCharge = new ProductCharge();
          productCharge.setProduct(product);
          productCharge.setCurrencyValue(currencyValue);
          productCharge.setPrice(newVal);
          productCharges.add(productCharge);
        }
      }
      productService.updatePricesForProducts(productCharges, channel);
      status = true;

    } catch (ServiceException ex) {
      throw new InvalidAjaxRequestException(ex.getMessage());
    }

    logger.debug("### editCatalogProductPricing method starting...(POST)");
    return status ? "success" : "failure";
  }

  @RequestMapping(value = ("/editcatalogproductbundlepricing"), method = RequestMethod.GET)
  public String editCatalogProductBundlePricing(@RequestParam(value = "channelId", required = true) String channelId,
      @RequestParam(value = "bundleId", required = true) String bundleId, ModelMap map) {
    logger.debug("### editCatalogProductBundlePricing method starting...(GET)");

    Channel channel = channelService.getChannelById(channelId);
    Catalog catalog = channel.getCatalog();

    Revision futureRevision = channelService.getFutureRevision(channel);
    Map<ProductBundleRevision, Map<CurrencyValue, Map<String, RateCardCharge>>> fullBundlePricingMap = getBundlePricingMap(
        channel, "planned", futureRevision.getStartDate(), true);

    ProductBundle productBundle = productBundleService.locateProductBundleById(bundleId);
    ProductBundleRevision productBundleRevision = channelService.getFutureChannelRevision(channel, false)
        .getProductBundleRevisionsMap().get(productBundle);

    map.addAttribute("supportedCurrencies", catalog.getSupportedCurrencyValuesByOrder());
    map.addAttribute("channel", channel);
    map.addAttribute("productBundleRevision", productBundleRevision);
    map.addAttribute("fullBundlePricingMap", fullBundlePricingMap);

    logger.debug("### editCatalogProductBundlePricing method ending...(GET)");
    return "channels.bundle.price.edit";
  }

  @RequestMapping(value = ("/editcatalogproductbundlepricing"), method = RequestMethod.POST)
  public String editCatalogProductBundlePricing(@RequestParam(value = "channelId", required = true) String channelId,
      @RequestParam(value = "bundleId", required = true) String bundleId,
      @RequestParam(value = "currencyValData", required = true) String currencyValData, ModelMap map)
      throws JSONException {
    logger.debug("### editCatalogProductBundlePricing method starting...(POST)");

    boolean status = false;
    try {
      Channel channel = channelService.getChannelById(channelId);
      ProductBundle productBundle = productBundleService.locateProductBundleById(bundleId);

      JSONArray jsonArray = new JSONArray(currencyValData);

      Revision futureRevision = channelService.getFutureRevision(channel);
      for (int index = 0; index < jsonArray.length(); index++) {
        JSONObject jsonObj = jsonArray.getJSONObject(index);
        BigDecimal previousVal = new BigDecimal(jsonObj.get("previousvalue").toString());
        BigDecimal newVal = new BigDecimal(jsonObj.get("value").toString());
        String currencyCode = jsonObj.get("currencycode").toString();
        String isRecurring = jsonObj.getString("isRecurring").toString();
        boolean isRecurringCharge = false;
        if (isRecurring.equals("1")) {
          isRecurringCharge = true;
        }

        if (!previousVal.equals(newVal)) {
          CurrencyValue currencyValue = currencyValueService.locateBYCurrencyCode(currencyCode);
          productBundleService.updatePriceForBundle(productBundle, channel, currencyValue, newVal, futureRevision,
              isRecurringCharge);
        }
      }

      status = true;
    } catch (ServiceException ex) {
      throw new InvalidAjaxRequestException(ex.getMessage());
    }
    logger.debug("### editCatalogProductBundlePricing method ending...(POST)");
    return status ? "success" : "failure";
  }

  @RequestMapping(value = ("/viewcatalogcurrent"), method = RequestMethod.GET)
  public String viewCatalogCurrent(@RequestParam(value = "channelId", required = true) String channelId,
      @RequestParam(value = "page", required = false, defaultValue = "1") String currentPage,
      @RequestParam(value = "perPage", required = false, defaultValue = "5") String perPageCount, ModelMap map) {
    logger.debug("### viewCatalogCurrent method starting...(GET)");

    Channel channel = channelService.getChannelById(channelId);
    Catalog catalog = channel.getCatalog();

    Revision currentRevision = channelService.getCurrentRevision(channel);
    Map<Product, Map<CurrencyValue, Map<String, ProductCharge>>> fullProductPricingMap = getProductChargeMap(channel,
        "current", currentRevision.getStartDate(), false);

    Map<ProductBundleRevision, Map<CurrencyValue, Map<String, RateCardCharge>>> fullBundlePricingMap = getBundlePricingMap(
        channel, "current", null, false);

    List<ProductBundleRevision> productBundleRevisions = getProductBundles(new ArrayList<ProductBundleRevision>(
        fullBundlePricingMap.keySet()), currentPage, perPageCount);

    Date lastSyncDate = null;
    if (channelService.getChannelReferenceCatalogRevision(channel, currentRevision) != null) {
      lastSyncDate = channelService.getChannelReferenceCatalogRevision(channel, currentRevision)
          .getReferenceCatalogRevision().getStartDate();
    }
    map.addAttribute("supportedCurrencies", catalog.getSupportedCurrencyValuesByOrder());
    map.addAttribute("fullProductPricingMap", fullProductPricingMap);
    map.addAttribute("noOfProducts", fullProductPricingMap.size());
    map.addAttribute("productBundleRevisions", productBundleRevisions);
    map.addAttribute("fullBundlePricingMap", fullBundlePricingMap);
    map.addAttribute("channel", channel);
    map.addAttribute("effectiveDate", currentRevision.getStartDate());
    map.addAttribute("lastSyncDate", lastSyncDate);
    logger.debug("### viewCatalogCurrent method ending...(GET)");
    return "catalog.current";
  }

  @RequestMapping(value = ("/viewcatalogplanned"), method = RequestMethod.GET)
  public String viewCatalogPlanned(@RequestParam(value = "channelId", required = true) String channelId,
      @RequestParam(value = "page", required = false, defaultValue = "1") String currentPage,
      @RequestParam(value = "perPage", required = false, defaultValue = "5") String perPageCount,
      @RequestParam(value = "editpriceisvalid", required = false, defaultValue = "0") String editpriceisvalid,
      ModelMap map) {
    logger.debug("### viewCatalogPlanned method starting...(GET)");
    Channel channel = channelService.getChannelById(channelId);
    Catalog catalog = channel.getCatalog();

    Revision futureRevision = channelService.getFutureRevision(channel);
    Map<Product, Map<CurrencyValue, Map<String, ProductCharge>>> fullProductPricingMap = getProductChargeMap(channel,
        "planned", futureRevision.getStartDate(), false);

    Map<ProductBundleRevision, Map<CurrencyValue, Map<String, RateCardCharge>>> fullBundlePricingMap = getBundlePricingMap(
        channel, "planned", null, false);

    List<ProductBundleRevision> productBundleRevisions = getProductBundles(new ArrayList<ProductBundleRevision>(
        fullBundlePricingMap.keySet()), currentPage, perPageCount);

    int sizeOfPublishedProductBundles = 0;
    for (ProductBundleRevision productBundleRevision : channelService.getChannelRevision(
        null,
        channelService.getChannelReferenceCatalogRevision(channel, futureRevision).getReferenceCatalogRevision()
            .getStartDate(), false).getProductBundleRevisions()) {
      if (productBundleRevision.getProductBundle().getPublish()) {
        ++sizeOfPublishedProductBundles;
      }
    }

    int sizeOfPublishedBundlesAddedToCatalog = 0;
    for (ProductBundleRevision productBundleRevision : fullBundlePricingMap.keySet()) {
      if (productBundleRevision.getProductBundle().getPublish()) {
        sizeOfPublishedBundlesAddedToCatalog += 1;
      }
    }

    boolean bundlestoadd = true;
    if (sizeOfPublishedBundlesAddedToCatalog == sizeOfPublishedProductBundles) {
      bundlestoadd = false;
    }

    List<CurrencyValue> listSupportedCurrencies = catalog.getSupportedCurrencyValuesByOrder();
    Date lastSyncDate = null;
    if (channelService.getChannelReferenceCatalogRevision(channel, futureRevision) != null) {
      lastSyncDate = channelService.getChannelReferenceCatalogRevision(channel, futureRevision)
          .getReferenceCatalogRevision().getStartDate();
    }
    map.addAttribute("supportedCurrencies", listSupportedCurrencies);
    map.addAttribute("channel", channel);
    map.addAttribute("fullProductPricingMap", fullProductPricingMap);
    map.addAttribute("noOfProducts", fullProductPricingMap.size());
    map.addAttribute("productBundleRevisions", productBundleRevisions);
    map.addAttribute("bundlestoadd", bundlestoadd);
    map.addAttribute("fullBundlePricingMap", fullBundlePricingMap);
    map.addAttribute("toalloweditprices", true);
    map.addAttribute("effectiveDate", futureRevision.getStartDate());
    map.addAttribute("lastSyncDate", lastSyncDate);
    logger.debug("### viewCatalogPlanned method ending...(GET)");
    return "catalog.planned";
  }

  @RequestMapping(value = ("/viewcataloghistory"), method = RequestMethod.GET)
  public String viewCatalogHistory(@RequestParam(value = "channelId", required = true) String channelId,
      @RequestParam(value = "historyDate", required = false, defaultValue = "") String historyDate,
      @RequestParam(value = "dateFormat", required = false) String dateFormat,
      @RequestParam(value = "showProductHistory", required = false) String showProductHistory, ModelMap map) {
    logger.debug("### viewCatalogHistory method starting...(GET)");

    Channel channel = channelService.getChannelById(channelId);
    Catalog catalog = channel.getCatalog();

    List<Date> historyDatesForCatalog = productService.getHistoryDates(catalog);

    map.addAttribute("noHistory", false);
    map.addAttribute("supportedCurrencies", catalog.getSupportedCurrencyValuesByOrder());
    map.addAttribute("catalogHistoryDates", historyDatesForCatalog);

    if (historyDatesForCatalog == null || historyDatesForCatalog.size() == 0) {
      map.addAttribute("noHistory", true);
    } else {
      Date historyDateObj = null;
      if (historyDate != null && !historyDate.isEmpty()) {
        DateFormat formatter = new SimpleDateFormat(dateFormat);
        try {
          historyDateObj = formatter.parse(historyDate);
        } catch (ParseException e) {
          throw new InvalidAjaxRequestException(e.getMessage());
        }
      } else {
        historyDateObj = historyDatesForCatalog.get(0);
      }

      Map<Product, Map<CurrencyValue, Map<String, ProductCharge>>> fullProductPricingMap = getProductChargeMap(channel,
          "history", historyDateObj, false);

      Map<ProductBundleRevision, Map<CurrencyValue, Map<String, RateCardCharge>>> fullBundlePricingMap = getBundlePricingMap(
          channel, "history", historyDateObj, false);

      map.addAttribute("noOfProducts", fullProductPricingMap.size());
      map.addAttribute("fullProductPricingMap", fullProductPricingMap);
      map.addAttribute("productBundleRevisions", fullBundlePricingMap.keySet());
      map.addAttribute("fullBundlePricingMap", fullBundlePricingMap);
      map.addAttribute("chosenHistoryDate", historyDateObj);
    }
    if (showProductHistory != null) {
      map.addAttribute("showProductHistory", true);
    } else {
      map.addAttribute("showProductHistory", false);
    }
    logger.debug("### viewCatalogHistory method ending...(GET)");
    return "catalog.history";
  }

  @RequestMapping(value = ("/changeplandate"), method = RequestMethod.POST)
  @ResponseBody
  public String changePlanDate(@RequestParam(value = "channelId", required = true) String channelId,
      @RequestParam(value = "newDate", required = true) String newDate,
      @RequestParam(value = "dateFormat", required = true) String dateFormat, ModelMap map) throws ParseException {
    logger.debug("### changePlanDate method starting...(POST)");
    boolean status = false;
    try {
      Channel channel = channelService.getChannelById(channelId);
      DateFormat formatter = new SimpleDateFormat(dateFormat);
      Date planDate = formatter.parse(newDate);
      Revision revision = channelService.setRevisionDate(planDate, channel);
      if (revision != null) {
        status = true;
      }
    } catch (ServiceException ex) {
      throw new InvalidAjaxRequestException(ex.getMessage());
    }

    logger.debug("### changePlanDate method ending...(POST)");
    return status ? "success" : "failure";
  }

  /**
   * Pops up the datepicker
   * 
   * @return
   */
  @RequestMapping(value = "/showdatepicker", method = RequestMethod.GET)
  public String showDatePicker(@RequestParam(value = "channelId", required = true) String channelId, ModelMap map) {
    logger.debug("### showDatePicker method starting...(GET)");

    Channel channel = channelService.getChannelById(channelId);

    Revision futureRevision = channelService.getFutureRevision(channel);
    map.addAttribute("plan_date", null);
    map.addAttribute("planDateInFuture", false);
    if (futureRevision != null && futureRevision.getStartDate() != null
        && futureRevision.getStartDate().after(new Date())) {
      map.addAttribute("planDateInFuture", true);
      map.addAttribute("plan_date", futureRevision.getStartDate());
    }
    map.addAttribute("date_tomorrow", DateUtils.addOneDay(new Date()));
    map.addAttribute("date_today", new Date());

    map.addAttribute("channel", channel);
    // always allow today
    map.addAttribute("isTodayAllowed", true);

    logger.debug("### showDatePicker method ending...(GET)");
    return "channels.datepicker";
  }

  @RequestMapping(value = ("/getnextsetofbundles"), method = RequestMethod.GET)
  public String getNextSetOfBundles(@RequestParam(value = "channelId", required = true) String channelId,
      @RequestParam(value = "lastBundleNo", required = true) String lastBundleNo,
      @RequestParam(value = "which", required = true) String which,
      @RequestParam(value = "editpriceisvalid", required = false, defaultValue = "0") String editpriceisvalid,
      ModelMap map) {
    logger.debug("### getNextSetOfBundles method starting...(GET)");

    Channel channel = channelService.getChannelById(channelId);

    int pageNo = -1;
    boolean nothingtosend = false;
    try {
      pageNo = Integer.parseInt(lastBundleNo) / BUNDLES_PER_PAGE;
      if (pageNo <= 0) {
        nothingtosend = true;
      }
    } catch (NumberFormatException nFE) {
      nothingtosend = true;
    }

    // Case 1: Case of say 12 being the last bundle no, means that we already
// have reached the end, otherwise, we would
    // have got last bundle no as a multiple of bundlesPerPage.
    // Case 2: Now suppose we get 10 as the last bundle no, and it is a multiple
// of bundlesPerPage and suppose 10 is the
    // total count, then we will get size of the productbundles from the call to
// get list of the same as 0.

    // Case 1
    if (Integer.parseInt(lastBundleNo) % BUNDLES_PER_PAGE != 0) {
      nothingtosend = true;
    }

    Map<ProductBundleRevision, Map<CurrencyValue, Map<String, RateCardCharge>>> fullBundlePricingMap = new HashMap<ProductBundleRevision, Map<CurrencyValue, Map<String, RateCardCharge>>>();
    int actiontoshow = 0;
    if (!nothingtosend) {
      if (which.equals("planned")) {
        fullBundlePricingMap = getBundlePricingMap(channel, "planned", null, false);
        actiontoshow = 1;
      } else if (which.equals("current")) {
        fullBundlePricingMap = getBundlePricingMap(channel, "current", null, false);
      }
    }

    boolean toalloweditprices = false;
    if (which.equals("planned")) {
      toalloweditprices = true;
    }

    List<ProductBundleRevision> productBundleRevisions = getProductBundles(new ArrayList<ProductBundleRevision>(
        fullBundlePricingMap.keySet()), new Integer(pageNo + 1).toString(), new Integer(BUNDLES_PER_PAGE).toString());

    map.addAttribute("supportedCurrencies", channel.getCatalog().getSupportedCurrencyValuesByOrder());
    map.addAttribute("toalloweditprices", toalloweditprices);
    map.addAttribute("productBundleRevisions", productBundleRevisions);
    map.addAttribute("fullBundlePricingMap", fullBundlePricingMap);
    map.addAttribute("actiontoshow", actiontoshow);
    map.addAttribute("channel", channel);
    map.addAttribute("lastBundleNo", Integer.parseInt(lastBundleNo));
    setPage(map, Page.CHANNELS);

    logger.debug("### getNextSetOfBundles method end...(GET)");
    return "channel.bundle.scroll.view";
  }

  @RequestMapping(value = "/getfulllistingofcharges", method = RequestMethod.GET)
  public String getFullChargeListing(@RequestParam(value = "channelId", required = true) String channelId,
      @RequestParam(value = "currentHistoryPlanned", required = true, defaultValue = "") String currentHistoryPlanned,
      @RequestParam(value = "bundleId", required = false, defaultValue = "") String bundleId,
      @RequestParam(value = "dateFormat", required = false, defaultValue = "") String dateFormat,
      @RequestParam(value = "historyDate", required = false, defaultValue = "") String date, ModelMap map) {
    logger.debug("### getfulllistingofcharges method starting...(GET)");

    Channel channel = channelService.getChannelById(channelId);
    Catalog catalog = channel.getCatalog();
    Date historyDate = null;
    if (currentHistoryPlanned.equals("history")) {
      DateFormat formatter = new SimpleDateFormat(dateFormat);
      try {
        historyDate = formatter.parse(date);
      } catch (ParseException e) {
        throw new InvalidAjaxRequestException(e.getMessage());
      }
    }

    if (bundleId == null || bundleId.equals("")) {
      List<Product> productsList = new ArrayList<Product>();
      Map<Product, Map<CurrencyValue, Map<String, ProductCharge>>> fullProductPricingMap = null;
      if (currentHistoryPlanned.equals("current")) {
        fullProductPricingMap = getProductChargeMap(channel, "current", null, false);
        productsList = productService.listProducts(null, null, channelService.getCurrentRevision(channel));
      } else if (currentHistoryPlanned.equals("planned")) {
        fullProductPricingMap = getProductChargeMap(channel, "planned", null, false);
        productsList = productService.listProducts(null, null, channelService.getFutureRevision(channel));
      } else if (currentHistoryPlanned.equals("history")) {
        fullProductPricingMap = getProductChargeMap(channel, "history", historyDate, false);
        productsList = productService.listProducts(null, null,
            channelService.getRevisionForTheDateGiven(historyDate, channel));
      }

      map.addAttribute("fullProductPricingMap", fullProductPricingMap);
      map.addAttribute("currencies", catalog.getSupportedCurrencyValuesByOrder());
      map.addAttribute("totalproducts", productsList.size());
      map.addAttribute("noDialog", false);

      logger.debug("### getfulllistingofcharges method end...(GET)");
      return "catalog.utilities";

    } else {

      ProductBundle bundle = productBundleService.getProductBundleById(Long.parseLong(bundleId));
      ProductBundleRevision productBundleRevision = null;
      Map<ProductBundleRevision, Map<CurrencyValue, Map<String, RateCardCharge>>> fullBundlePricingMap = null;
      if (currentHistoryPlanned.equals("current")) {
        fullBundlePricingMap = getBundlePricingMap(channel, "current", null, false);
        productBundleRevision = channelService.getCurrentChannelRevision(channel, false).getProductBundleRevisionsMap()
            .get(bundle);
      } else if (currentHistoryPlanned.equals("planned")) {
        fullBundlePricingMap = getBundlePricingMap(channel, "planned", null, false);
        productBundleRevision = channelService.getFutureChannelRevision(channel, false).getProductBundleRevisionsMap()
            .get(bundle);
      } else if (currentHistoryPlanned.equals("history")) {
        fullBundlePricingMap = getBundlePricingMap(channel, "history", historyDate, false);
        channelService.getChannelRevision(channel, historyDate, false);
        productBundleRevision = channelService.getChannelRevision(channel, historyDate, false)
            .getProductBundleRevisionsMap().get(bundle);
      }

      map.addAttribute("productBundleRevision", productBundleRevision);
      map.addAttribute("fullBundlePricingMap", fullBundlePricingMap);
      map.addAttribute("productBundle", bundle);
      map.addAttribute("currencies", channel.getCatalog().getSupportedCurrencyValuesByOrder());
      map.addAttribute("noDialog", false);

      logger.debug("### getfulllistingofcharges method end...(GET)");
      return "catalog.bundle";
    }
  }

  @RequestMapping(value = ("/syncchannel"), method = RequestMethod.POST)
  @ResponseBody
  public String syncChannel(@RequestParam(value = "channelId", required = true) String channelId, ModelMap map) {
    logger.debug("### syncChannel method starting...(POST)");

    boolean status = true;
    Channel channel = channelService.getChannelById(channelId);
    try {
      channelService.syncChannel(channel);
    } catch (ServiceException ex) {
      status = false;
      throw new InvalidAjaxRequestException(ex.getMessage());
    }

    logger.debug("### syncChannel method end...(POST)");
    return status ? "success" : "failure";
  }

  @RequestMapping(value = ("/servicesettings"), method = RequestMethod.GET)
  public String getChannelSettings(
      @ModelAttribute("viewChannelServiceSettingsForm") ChannelServiceSettingsForm channelServiceSettingsForm,
      @RequestParam(value = "channelId", required = true) String channelId,
      @RequestParam(value = "instanceUUID", required = true) String instanceUUID, ModelMap map) {
    logger.debug("### getChannelSettings method starting...(GET)");
    ServiceInstance serviceInstance = connectorConfigurationManager.getInstanceByUUID(instanceUUID);
    if (serviceInstance != null) {
      Channel channel = channelService.getChannelById(channelId);
      List<ServiceInstanceConfig> serviceInstanceConfigList = channelService.getChannelSettingsList(channel,
          instanceUUID);

      channelServiceSettingsForm.setChannelServiceSettings(getChannelSettings(serviceInstanceConfigList,
          channelServiceSettingsForm, serviceInstance));
      map.addAttribute("viewChannelServiceSettingsForm", channelServiceSettingsForm);
      map.addAttribute("serviceSettingsChanelID", channelId);
      map.addAttribute("serviceSettingsInstanceUUID", instanceUUID);
      if (serviceInstance.getService().getChannelSettingServiceConfigMetadata().isEmpty()) {
        map.addAttribute("serviceSettingsCount", 0);
      } else {
        map.addAttribute("serviceSettingsCount", 1);
      }
    }
    logger.debug("### getChannelSettings method ending...(GET)");
    return "channel.service.settings";
  }

  @RequestMapping(value = ("/editservicesettings"), method = RequestMethod.GET)
  public String editChannelSettings(
      @ModelAttribute("channelServiceSettingsForm") ChannelServiceSettingsForm channelServiceSettingsForm,
      @RequestParam(value = "channelId", required = true) String channelId,
      @RequestParam(value = "instanceUUID", required = true) String instanceUUID, ModelMap map) {
    logger.debug("### editChannelSettings method starting...(GET)");
    ServiceInstance serviceInstance = connectorConfigurationManager.getInstanceByUUID(instanceUUID);
    Channel channel = channelService.getChannelById(channelId);
    List<ServiceInstanceConfig> serviceInstanceConfigList = channelService
        .getChannelSettingsList(channel, instanceUUID);
    channelServiceSettingsForm.setChannelServiceSettings(getChannelSettings(serviceInstanceConfigList,
        channelServiceSettingsForm, serviceInstance));
    channelServiceSettingsForm.setChannelId(channelId);
    channelServiceSettingsForm.setServiceInstanceUUID(instanceUUID);
    map.addAttribute("channelServiceSettingsForm", channelServiceSettingsForm);
    logger.debug("### editChannelSettings method ending...(GET)");
    return "channel.service.edit.settings";
  }

  @RequestMapping(value = ("/editservicesettings"), method = RequestMethod.POST)
  @ResponseBody
  public String saveChannelSettings(
      @ModelAttribute("channelServiceSettingsForm") ChannelServiceSettingsForm channelServiceSettingsForm, ModelMap map) {
    logger.debug("### editChannelSettings method starting...(POST)");

    ServiceInstance serviceInstance = connectorConfigurationManager.getInstanceByUUID(channelServiceSettingsForm
        .getServiceInstanceUUID());
    Channel channel = channelService.getChannelById(channelServiceSettingsForm.getChannelId());
    boolean create = "create".equals(channelServiceSettingsForm.getMode());
    for (ChannelServiceSetting channelServiceSetting : channelServiceSettingsForm.getChannelServiceSettings()) {
      if (create) {
        ServiceInstanceConfig serviceInstanceConfig = new ServiceInstanceConfig();
        ChannelSettingServiceConfigMetadata channelSettingServiceConfigMetadata = getMatchingChannelServiceConfigMetadata(
            channelServiceSetting.getServiceConfigMetaDataId(), serviceInstance.getService()
                .getChannelSettingServiceConfigMetadata());
        serviceInstanceConfig.setServiceConfigMetadata(channelSettingServiceConfigMetadata);
        serviceInstanceConfig.setName(channelSettingServiceConfigMetadata.getName());
        serviceInstanceConfig.setValue(channelServiceSetting.getValue());
        serviceInstanceConfig.setServiceInstanceConfigurer(channel);
        serviceInstanceConfig.setService(serviceInstance.getService());
        serviceInstanceConfig.setServiceInstance(serviceInstance);
        serviceInstance.getServiceInstanceConfig().add(serviceInstanceConfig);
      } else {
        for (ServiceInstanceConfig serviceInstanceConfig : serviceInstance.getServiceInstanceConfig()) {
          if (serviceInstanceConfig.getServiceConfigMetadata().getId()
              .equals(channelServiceSetting.getServiceConfigMetaDataId())
              && serviceInstanceConfig.getServiceInstanceConfigurer().equals(channel)) {
            serviceInstanceConfig.setValue(channelServiceSetting.getValue());
          }
        }
      }
    }
    connectorConfigurationManager.updateServiceInstance(serviceInstance);
    logger.debug("### editChannelSettings method ending...(GET)");
    return "success";
  }

  /**
   * Pops up the Edit Channel Branding dialog
   * 
   * @param channelId
   * @param map
   * @return
   */
  @RequestMapping(value = ("/editchannelbranding"), method = RequestMethod.GET)
  public String editChannelBranding(@RequestParam(value = "channelId", required = true) String channelId, ModelMap map) {
    logger.debug("### edit Channel Branding method starting...(GET)");
    Channel channel = channelService.getChannelById(channelId);
    ChannelBrandingForm channelBrandingForm = new ChannelBrandingForm(channel);

    map.addAttribute("channelBrandingForm", channelBrandingForm);
    map.addAttribute("currentLoggedInUserParam", getCurrentUser().getUuid());
    map.addAttribute("previewModeInCurrentChannel", channel.getUnPublishedChannelBrandingConfigurations());
    logger.debug("### edit Channel Branding method end...(GET)");
    return "channel.editbranding";
  }

  @RequestMapping(value = ("/editchannelbranding"), method = RequestMethod.POST)
  public ResponseEntity<String> editChannelBranding(@ModelAttribute("channelBrandingForm") ChannelBrandingForm form,
      BindingResult result, HttpServletRequest request, HttpServletResponse response, ModelMap map)
      throws JsonGenerationException, JsonMappingException, IOException {
    logger.debug("### edit Channel Branding method starting...(POST)");

    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.add("Content-Type", "text/plain; charset=utf-8");

    Map<String, String> mapresult = new HashMap<String, String>();
    Locale locale = getCurrentUser().getLocale();
    String fileSize = checkFileUploadMaxSizeException(request);
    if (fileSize != null) {
      result.reject("error.image.max.upload.size.exceeded", new Object[] {
        fileSize
      }, "");
      mapresult.put("errormessage", messageSource.getMessage("error.image.max.upload.size.exceeded", new Object[] {
        fileSize
      }, locale));
      return new ResponseEntity<String>(JSONUtils.toJSONString(mapresult), responseHeaders, HttpStatus.OK);
    }
    String uploadDir = config.getValue(Names.com_citrix_cpbm_portal_settings_images_uploadPath);
    if (uploadDir != null && !uploadDir.trim().equals("")) {
      Channel channel = channelService.getChannelById(form.getChannelId());

      ChannelBrandingFormValidator validator = new ChannelBrandingFormValidator();
      validator.validate(form, result, htmlSanitizerService);
      if (result.hasErrors()) {

        List<FieldError> l = result.getFieldErrors();
        for (FieldError f : l) {
          mapresult.put(f.getField(), messageSource.getMessage(f.getCode(), null, locale));
        }
        setPage(map, Page.CHANNELS);
        return new ResponseEntity<String>(JSONUtils.toJSONString(mapresult), responseHeaders, HttpStatus.OK);
      } else {

        MultipartFile faviconFile = form.getFavicon();
        MultipartFile cssFile = form.getCss();
        MultipartFile logoFile = form.getLogo();

        try {
          if (form.isPublish()) {
            if (faviconFile != null && faviconFile.getSize() > 0) {
              channelService.uploadChannelFavicon(channel, faviconFile);
            }
            if (cssFile != null && cssFile.getSize() > 0) {
              channelService.uploadChannelCss(channel, cssFile);
            }
            if (logoFile != null && logoFile.getSize() > 0) {
              channelService.uploadChannelLogo(channel, logoFile);
            }
            channelService.removeUnpublishedChannelBrandings(channel.getId().toString());
          } else {
            channelService
                .createUnpublishedChannelBrandings(channel.getId().toString(), faviconFile, cssFile, logoFile);
          }
        } catch (IOException e) {
          logger.debug("###IO Exception in writing Channel Branding files");

          setPage(map, Page.CHANNELS);
          mapresult.put("errormessage", messageSource.getMessage("message.failed.to.upload.file", null, locale));
          return new ResponseEntity<String>(JSONUtils.toJSONString(mapresult), responseHeaders, HttpStatus.OK);
        }
      }
      logger.debug("### edit Channel Branding method ending (Success)...(POST)");
      Channel channelUpdated = channelService.getChannelById(form.getChannelId());
      ChannelBrandingConfigurations cfg = channelUpdated.getChannelBrandingConfigurations();
      UnPublishedChannelBrandingConfigurations unpublished_cfg = channelUpdated
          .getUnPublishedChannelBrandingConfigurations();

      mapresult.put("success", "true");
      mapresult.put("favicon", cfg.getFavIconImageFileName());
      mapresult.put("css", cfg.getCssFileName());
      mapresult.put("logo", cfg.getLogoImageFileName());
      mapresult.put("channelId", channelUpdated.getId().toString());
      mapresult.put("channelCode", channelUpdated.getCode().toString());
      mapresult.put("unpublished_css", unpublished_cfg.getCssFileName());
      mapresult.put("unpublished_logo", unpublished_cfg.getLogoImageFileName());
      mapresult.put("unpublished_favicon", unpublished_cfg.getFavIconImageFileName());

      return new ResponseEntity<String>(JSONUtils.toJSONString(mapresult), responseHeaders, HttpStatus.OK);
    } else {

      result.reject("error.custom.image.upload.dir", new Object[] {
        fileSize
      }, "");
      setPage(map, Page.CHANNELS);
      logger.debug("### edit Channel Branding method ending (No Upload Dir Defined)...(POST)");
      mapresult.put("errormessage", messageSource.getMessage("error.custom.image.upload.dir", null, locale));
      return new ResponseEntity<String>(JSONUtils.toJSONString(mapresult), responseHeaders, HttpStatus.OK);

    }
  }

  @RequestMapping(value = ("/channel/{channelId}/css"), method = RequestMethod.GET)
  public String getChannelCSS(@PathVariable String channelId,
      @RequestParam(value = "unpublished", required = false) String unpublished, ModelMap map,
      HttpServletResponse response) {
    Channel channel = channelService.getChannelById(channelId);
    String channelBrandingFilesDir = config.getValue(Names.com_citrix_cpbm_portal_settings_images_uploadPath);
    String imagePath = channel.getChannelBrandingConfigurations().getCssFileName();
    if (StringUtils.isNotBlank(unpublished) && unpublished.equals("true")) {
      imagePath = channel.getUnPublishedChannelBrandingConfigurations().getCssFileName();
    }

    if (channelBrandingFilesDir != null && !channelBrandingFilesDir.trim().equals("") && imagePath != null
        && !imagePath.trim().equals("")) {

      try {
        String absoluteImagePath = FilenameUtils.concat(channelBrandingFilesDir,
            com.vmops.portal.config.Configuration.CHANNEL_BRANDING_FILES);
        if (StringUtils.isNotBlank(unpublished) && unpublished.equals("true")) {
          absoluteImagePath = FilenameUtils.concat(absoluteImagePath,
              com.vmops.portal.config.Configuration.UNPUBLISHED_CHANNEL_BRANDING_FILES);
        }
        absoluteImagePath = FilenameUtils.concat(absoluteImagePath, channel.getId().toString());
        absoluteImagePath = FilenameUtils.concat(absoluteImagePath, imagePath);
        response.setContentType("text/" + FilenameUtils.getExtension(imagePath));
        downloadFile(absoluteImagePath, imagePath, response);

      } catch (IOException e) {
        logger.error(e.getMessage(), e);
      }

    } else {
      return "downloadCss.error";
    }
    return null;

  }

  @RequestMapping(value = ("/publish/{channelId}"), method = RequestMethod.GET)
  @ResponseBody
  public Map<String, String> publishChannelBrandings(@PathVariable String channelId) {
    Map<String, String> mapresult = new HashMap<String, String>();
    try {
      channelService.removeUnpublishedChannelBrandings(channelId);
      Channel channel = channelService.getChannelById(channelId);
      mapresult.put("success", "true");
      mapresult.put("favicon", channel.getChannelBrandingConfigurations().getFavIconImageFileName());
      mapresult.put("css", channel.getChannelBrandingConfigurations().getCssFileName());
      mapresult.put("logo", channel.getChannelBrandingConfigurations().getLogoImageFileName());
      mapresult.put("channelId", channelId);
    } catch (IOException e) {
      e.printStackTrace();
      mapresult.put("success", "fail");
    }
    return mapresult;
  }

  private ServiceList getServiceAndServiceInstanceList() {
    logger.debug("### editChannelSettings method starting...(POST)");
    ServiceList serviceListObj = new ServiceList();
    List<com.vmops.web.forms.Service> services = new ArrayList<com.vmops.web.forms.Service>();
    List<Service> serviceList = this.getEnabledCloudServices();
    if (CollectionUtils.isNotEmpty(serviceList)) {
      for (Service service : serviceList) {
        com.vmops.web.forms.Service serviceObj = new com.vmops.web.forms.Service();
        List<com.vmops.web.forms.ServiceInstance> serviceInstances = new ArrayList<com.vmops.web.forms.ServiceInstance>();
        serviceObj.setServicename(messageSource.getMessage(service.getServiceName() + ".service.name", null, null));
        for (ServiceInstance serviceInstance : service.getServiceInstances()) {
          com.vmops.web.forms.ServiceInstance serviceInsObj = new com.vmops.web.forms.ServiceInstance();
          serviceInsObj.setInstancename(serviceInstance.getName());
          serviceInsObj.setInstanceuuid(serviceInstance.getUuid());
          serviceInstances.add(serviceInsObj);
        }
        serviceObj.setInstances(serviceInstances);
        services.add(serviceObj);
      }
      serviceListObj.setServices(services);
    }
    logger.debug("### editChannelSettings method ending...(GET)");
    return serviceListObj;
  }

  private ChannelSettingServiceConfigMetadata getMatchingChannelServiceConfigMetadata(Long id,
      Set<ChannelSettingServiceConfigMetadata> channelSettingServiceConfigMetadatas) {
    for (ChannelSettingServiceConfigMetadata channelSettingServiceConfigMetadata : channelSettingServiceConfigMetadatas) {
      if (channelSettingServiceConfigMetadata.getId().equals(id)) {
        return channelSettingServiceConfigMetadata;
      }
    }
    return null;
  }

  private List<ChannelServiceSetting> getChannelSettings(List<ServiceInstanceConfig> serviceInstanceConfigList,
      ChannelServiceSettingsForm channelServiceSettingsForm, ServiceInstance serviceInstance) {
    ChannelServiceSetting channelSetting = null;
    List<ChannelServiceSetting> channelServiceSettings = new ArrayList<ChannelServiceSetting>();
    if (CollectionUtils.isEmpty(serviceInstanceConfigList)) {
      channelServiceSettingsForm.setMode("create");
      Set<ChannelSettingServiceConfigMetadata> serviceConfigMetadatas = serviceInstance.getService()
          .getChannelSettingServiceConfigMetadata();
      for (ChannelSettingServiceConfigMetadata channelSettingServiceConfigMetadata : serviceConfigMetadatas) {
        String value = StringUtils.isNotBlank(channelSettingServiceConfigMetadata.getDefaultVal()) ? channelSettingServiceConfigMetadata
            .getDefaultVal() : null;
        channelSetting = new ChannelServiceSetting(channelSettingServiceConfigMetadata.getName(), value,
            channelSettingServiceConfigMetadata.getId());
        channelSetting.setValidationClass(channelSettingServiceConfigMetadata.getValidations().getClassValidations());
        channelSetting.setServiceName(serviceInstance.getService().getServiceName());
        channelSetting.setPropertyType(channelSettingServiceConfigMetadata.getType());
        channelSetting.setPropertyOrder(channelSettingServiceConfigMetadata.getPropertyOrder());
        channelSetting.setReconfigurable(channelSettingServiceConfigMetadata.getReconfigurable());
        channelServiceSettings.add(channelSetting);
      }
    } else {
      for (ServiceInstanceConfig serviceInstanceConfig : serviceInstanceConfigList) {
        channelSetting = new ChannelServiceSetting(serviceInstanceConfig.getName(), serviceInstanceConfig.getValue(),
            serviceInstanceConfig.getServiceConfigMetadata().getId());
        channelSetting.setValidationClass(serviceInstanceConfig.getServiceConfigMetadata().getValidations()
            .getClassValidations());
        channelSetting.setServiceName(serviceInstance.getService().getServiceName());
        channelSetting.setPropertyType(serviceInstanceConfig.getServiceConfigMetadata().getType());
        channelSetting.setPropertyOrder(serviceInstanceConfig.getServiceConfigMetadata().getPropertyOrder());
        channelSetting.setReconfigurable(((ChannelSettingServiceConfigMetadata) serviceInstanceConfig
            .getServiceConfigMetadata()).getReconfigurable());
        channelServiceSettings.add(channelSetting);
      }
    }
    Collections.sort(channelServiceSettings, new ChannelServiceSettingComparator());
    return channelServiceSettings;
  }

  public class ChannelServiceSettingComparator implements Comparator<ChannelServiceSetting> {

    @Override
    public int compare(ChannelServiceSetting o1, ChannelServiceSetting o2) {
      return (o1.getPropertyOrder() < o2.getPropertyOrder() ? -1 : (o1.getPropertyOrder() == o2.getPropertyOrder() ? 0
          : 1));
    }
  }

  private List<Service> getEnabledCloudServices() {
    List<ServiceInstance> cloudTypeServiceInstances = connectorManagementService.getCloudTypeServiceInstances();
    Set<Service> services = new HashSet<Service>();
    List<Service> cloudServices = new ArrayList<Service>();
    for (ServiceInstance instance : cloudTypeServiceInstances) {
      services.add(instance.getService());
    }
    if (cloudServices.addAll(services)) {
      return cloudServices;
    }
    return null;
  }

  private List<Country> getCountriesByCodes(String countryCodes) {
    List<Country> filteredCountryList = new ArrayList<Country>();

    List<String> countryList = new ArrayList<String>();
    if (!StringUtils.isBlank(countryCodes)) {
      String[] countries = countryCodes.split("\\s*,\\s*");
      countryList = Arrays.asList(countries);
    }
    if (countryList.size() > 0) {
      for (String code : countryList) {
        filteredCountryList.add(countryService.locateCountryByCode(code));
      }
    }
    return filteredCountryList;
  }
}

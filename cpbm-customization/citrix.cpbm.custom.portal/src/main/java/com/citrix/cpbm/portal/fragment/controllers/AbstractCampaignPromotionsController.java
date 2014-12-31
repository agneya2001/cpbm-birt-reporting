/*
 * Copyright © 2013 Citrix Systems, Inc. You may not use, copy, or modify this file except pursuant to a valid license
 * agreement from Citrix Systems, Inc.
 */
package com.citrix.cpbm.portal.fragment.controllers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vmops.model.CampaignPromotion;
import com.vmops.model.CampaignPromotion.DiscountType;
import com.vmops.model.CampaignPromotion.State;
import com.vmops.model.CampaignPromotionDiscountAmount;
import com.vmops.model.Channel;
import com.vmops.model.CurrencyValue;
import com.vmops.service.ChannelService;
import com.vmops.service.CurrencyValueService;
import com.vmops.service.PromotionService;
import com.vmops.service.exceptions.AjaxFormValidationException;
import com.vmops.service.exceptions.InvalidAjaxRequestException;
import com.vmops.web.controllers.AbstractAuthenticatedController;
import com.vmops.web.controllers.menu.Page;
import com.vmops.web.forms.CampaignPromotionsForm;
import com.vmops.web.validators.CampaignPromotionsFormValidator;

public abstract class AbstractCampaignPromotionsController extends AbstractAuthenticatedController {

// CHECKSTYLE:OFF
  @Autowired
  protected PromotionService promotionService;

  @Autowired
  protected ChannelService channelService;

// CHECKSTYLE:ON
  @Autowired
  private CurrencyValueService currencyValueService;

  /**
   * This method is used to view a campaign promotion
   * 
   * @param promotionId
   * @param map
   * @return String
   */
  @RequestMapping(value = "/show", method = RequestMethod.GET)
  public String show(@RequestParam(value = "Id", required = true) String promotionId, ModelMap map) {
    logger.debug("### show method starting...(GET)");
    CampaignPromotion campaignPromotion = promotionService.locateCampaignById(promotionId);
    map.addAttribute("campaign", campaignPromotion);
    logger.debug("### show method ending...(GET)");
    return "promotions.view";
  }

  /**
   * This method is used to get details for creating a campaign
   * 
   * @param map
   * @return String
   */
  @RequestMapping(value = "/create", method = RequestMethod.GET)
  public String createCampaigns(ModelMap map) {
    logger.debug("### createPromotions method starting...(GET)");
    setPage(map, Page.CAMPAIGNS);
    CampaignPromotion cp = new CampaignPromotion();
    CampaignPromotionsForm campaignPromotionsForm = new CampaignPromotionsForm();
    campaignPromotionsForm.setCampaignPromotion(cp);
    map.addAttribute("campaignPromotionsForm", campaignPromotionsForm);
    map.addAttribute("channels", channelService.getChannels(null, null, null));
    map.addAttribute("PERCENTAGE", DiscountType.PERCENTAGE);
    map.addAttribute("FIXED_AMOUNT", DiscountType.FIXED_AMOUNT);
    List<CurrencyValue> supportedCurrencies = currencyValueService.listActiveCurrencies();
    map.addAttribute("supportedCurrencies", supportedCurrencies);
    logger.debug("### createPromotions method ending...(GET)");
    return "promotions.new";
  }

  /**
   * This method will return the union of supported currencies for the list of channels.
   * 
   * @param form
   * @param map
   * @return String
   */
  @RequestMapping(value = "/getSupportedCurrencies", method = RequestMethod.GET)
  public String getSupportedCurrencyForChannels(@ModelAttribute("campaignPromotionsForm") CampaignPromotionsForm form,
      ModelMap map) {
    logger.debug("### getSupportedCurrencyForChannels method starting...(GET)");
    Set<CurrencyValue> currValSet = new HashSet<CurrencyValue>(currencyValueService.listActiveCurrencies());
    if (form.getDiscountAmountMap() != null && form.getDiscountAmountMap().size() > 0) {
      form.getDiscountAmountMap().clear();
    }
    map.put("supportedCurrenciesForChannel", currValSet);
    logger.debug("### getSupportedCurrencyForChannels method ending...(GET)");
    return "promotions.new.supportedCurrency";
  }

  /**
   * This method is used to create a campaign promotion
   * 
   * @param form
   * @param result
   * @param map
   * @param response
   * @return CampaignPromotion
   */
  @RequestMapping(value = "/create", method = RequestMethod.POST)
  @ResponseBody
  public CampaignPromotion createCampaigns(@ModelAttribute("campaignPromotionsForm") CampaignPromotionsForm form,
      BindingResult result, ModelMap map, HttpServletResponse response) {

    logger.debug("### createPromotions method starting...(POST)");

    CampaignPromotionsFormValidator validator = new CampaignPromotionsFormValidator();
    validator.validate(form, result);
    if (result.hasErrors()) {
      throw new AjaxFormValidationException(result);
    }

    CampaignPromotion promotion = form.getCampaignPromotion();
    if (form.isUnlimited()) {
      promotion.setMaxAccounts(0);
    }
    promotion.setPromoCode(form.getPromoCode());

    CampaignPromotion campaignPromotion = promotionService.createCampaignPromotion(promotion, form.getChannelIdLst(),
        form.getDiscountAmountMap());
    return campaignPromotion;
  }

  /**
   * This method is used to fetch the list of campaigns.
   * 
   * @param editedId
   * @param page
   * @param map
   * @return String
   */
  @RequestMapping(value = "/list", method = RequestMethod.GET)
  public String list(@RequestParam(value = "editedId", required = false) String editedId,
      @RequestParam(value = "page", required = false, defaultValue = "1") Integer page, ModelMap map) {
    logger.debug("### list method starting...");
    Integer perPage = getDefaultPageSize();
    map.addAttribute("pageSize", getDefaultPageSize());

    setPage(map, Page.CAMPAIGNS);
    List<CampaignPromotion> campaignsList = promotionService.listCampaignPromotions(0, 0);

    int totalCampaigns = campaignsList != null && campaignsList.size() > 0 ? campaignsList.size() : 0;

    campaignsList = promotionService.listCampaignPromotions(page, perPage);
    map.addAttribute("campaignsList", campaignsList);
    map.addAttribute("campaignsListSize", campaignsList.size());
    map.addAttribute("idToBeSelected", editedId);

    map.addAttribute("enable_next", "False");

    if (totalCampaigns - (page * perPage) > 0) {
      map.addAttribute("enable_next", "True");
    } else {
      map.addAttribute("enable_next", "False");
    }
    map.addAttribute("current_page", page);

    // Following attribute decides whether to show the 'Add new campaign' link in UI
    if (channelService.getDefaultServiceProviderChannel() == null) {
      map.addAttribute("atleastOneChannelPresent", false);
    } else {
      map.addAttribute("atleastOneChannelPresent", true);
    }

    logger.debug("### list method ending...");
    return "promotions.list";
  }

  /**
   * This method is used to fetch campaign promotion for editing it.
   * 
   * @param id
   * @param map
   * @return String
   */
  @RequestMapping(value = "/editcampaign", method = RequestMethod.GET)
  public String edit(@RequestParam(value = "Id", required = true) String id, ModelMap map) {
    logger.debug("### edit method starting...");
    CampaignPromotion promotion = promotionService.locateCampaignById(id);
    CampaignPromotionsForm campaignPromotionsForm = new CampaignPromotionsForm(promotion);
    campaignPromotionsForm.setPromoCode(promotion.getPromoCode());
    List<CurrencyValue> supportedCurrencies = currencyValueService.listActiveCurrencies();
    Map<String, BigDecimal> discountAmountMap = new HashMap<String, BigDecimal>();
    if (promotion.getCampaignPromotionDiscountAmount() != null
        && promotion.getCampaignPromotionDiscountAmount().size() > 0) {
      for (CampaignPromotionDiscountAmount discountAmount : promotion.getCampaignPromotionDiscountAmount()) {
        discountAmountMap.put(discountAmount.getCurrencyValue().getCurrencyCode(), discountAmount.getDiscount());
      }
    } else {
      for (CurrencyValue currencyValue : supportedCurrencies) {
        discountAmountMap.put(currencyValue.getCurrencyCode(), new BigDecimal(0));
      }
    }
    campaignPromotionsForm.setDiscountAmountMap(discountAmountMap);
    map.addAttribute("campaignPromotionsForm", campaignPromotionsForm);
    map.addAttribute("channels", channelService.getChannels(null, null, null));
    map.addAttribute("restrictedEdit", promotion.getState() == State.ACTIVE);
    map.addAttribute("PERCENTAGE", DiscountType.PERCENTAGE);
    map.addAttribute("FIXED_AMOUNT", DiscountType.FIXED_AMOUNT);
    logger.debug("### edit method ending...");
    return "promotions.edit";
  }

  /**
   * This method is used to edit the campaign promotion
   * 
   * @param form
   * @param result
   * @param map
   * @return CampaignPromotion
   */
  @RequestMapping(value = "/editcampaign", method = RequestMethod.POST)
  @ResponseBody
  public CampaignPromotion edit(@ModelAttribute("campaignPromotionsForm") CampaignPromotionsForm form,
      BindingResult result, ModelMap map) {
    logger.debug("### edit method starting...");

    CampaignPromotion promotion = form.getCampaignPromotion();
    promotion.setPromoCode(form.getPromoCode());
    
    List<String> channelIds = new ArrayList<String>();
    List<String> formChannelIds = form.getChannelIdLst();
    if (formChannelIds == null) {
      if (promotion.getSupportedChannels() != null && promotion.getSupportedChannels().size() > 0) {
        for (Channel channel : promotion.getSupportedChannels()) {
          channelIds.add(channel.getId().toString());
        }
      }
    } else {
      channelIds = formChannelIds;
    }
    try {
      promotion = promotionService.editCampaignPromotion(promotion, channelIds, form.getDiscountAmountMap());
    } catch (IllegalArgumentException e) {
      logger.debug(e, e);
      throw new InvalidAjaxRequestException(e);
    }
    logger.debug("### edit method ending...");
    map.clear();
    return promotion;
  }

  /**
   * This method is used to validate promo code for campaign
   * 
   * @param promoCode
   * @return String
   */
  @RequestMapping("/validate_promoCode")
  @ResponseBody
  public String validateCampaignPromoCode(@RequestParam("promoCode") final String promoCode) {
    logger.debug("In validateCampaignPromoCode() method start and campaignCode is : " + promoCode);

    if (!productService.isCodeUnique(promoCode)) {
      logger.debug("###  promo code is NOT unique ");
      return Boolean.FALSE.toString();
    }
    return Boolean.TRUE.toString();
  }

}

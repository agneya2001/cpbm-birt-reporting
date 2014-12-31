/*
 * Copyright © 2013 Citrix Systems, Inc. You may not use, copy, or modify this file except pursuant to a valid license
 * agreement from Citrix Systems, Inc.
 */
package web.Integration;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import web.WebTestsBase;
import web.support.MockSessionStatus;

import com.citrix.cpbm.access.proxy.CustomProxy;
import com.citrix.cpbm.core.workflow.model.TenantStateChangeTransaction;
import com.citrix.cpbm.portal.forms.TenantForm;
import com.citrix.cpbm.portal.fragment.controllers.AbstractTenantController;
import com.vmops.event.PortalEvent;
import com.vmops.model.Address;
import com.vmops.model.CampaignPromotion;
import com.vmops.model.CurrencyValue;
import com.vmops.model.PromotionToken;
import com.vmops.model.Tenant;
import com.vmops.model.User;
import com.vmops.persistence.AccountTypeDAO;
import com.vmops.persistence.CampaignPromotionDAO;
import com.vmops.persistence.PromotionTokenDAO;
import com.vmops.service.exceptions.TrialCodeInvalidException;

/**
 * @author anushab
 */
public class TrialAccountCreationTest extends WebTestsBase {

  @Autowired
  private AbstractTenantController controller;

  @Autowired
  private AccountTypeDAO atdao;

  @Autowired
  private PromotionTokenDAO promotionTokenDao;

  @Autowired
  private CampaignPromotionDAO cpdao;

  private final MockSessionStatus status = new MockSessionStatus();

  private final ModelMap map = new ModelMap();

  private PromotionToken promotionToken;

  private final String code = "TestCode";

  @Before
  public void init() throws Exception {
    CampaignPromotion campaignPromotion = new CampaignPromotion();
    campaignPromotion.setCode(code);
    campaignPromotion.setTitle("TestCPTitle");
    campaignPromotion.setCreateBy(getRootUser());
    campaignPromotion.setUpdateBy(getRootUser());
    campaignPromotion.setTrial(true);
    campaignPromotion.setStartDate(new Date());
    campaignPromotion.setEndDate(new Date());
    cpdao.save(campaignPromotion);
    promotionToken = new PromotionToken(campaignPromotion, code);
    promotionToken.setCreateBy(getRootUser());
    promotionTokenDao.save(promotionToken);
  }

  public BindingResult valid(TenantForm form) throws Exception {
    // Validate form
    BindingResult result = validate(form);
    Assert.assertEquals("validating that the form has no errors", 0, result.getErrorCount());
    return result;
  }

  private TenantForm setTrialTenantForm(String promocode) {
    TenantForm form = new TenantForm((com.citrix.cpbm.access.Tenant) CustomProxy.newInstance(new Tenant()));
    com.citrix.cpbm.access.Tenant newTenant = form.getTenant();
    // Set Account Type as Trial
    form.setAccountTypeId("5");
    // Setting Company Name
    newTenant.setName("ACME Corp");
    // Setting User's Address
    newTenant.setAddress(new Address("steve", "creek", "cupertino", "ca", "95014", "US"));
    // Setting user's currency
    List<CurrencyValue> activeCurrencies = currencyValueService.listActiveCurrencies();
    for (CurrencyValue currencyValue : activeCurrencies) {
      newTenant.setCurrency(currencyValue);
      break;
    }
    // Create User
    User user = new User("test", "user", "test@test.com", VALID_USER + random.nextInt(), VALID_PASSWORD, VALID_PHONE,
        VALID_TIMEZONE, null, null, null);
    form.setUser((com.citrix.cpbm.access.User) CustomProxy.newInstance(user));
    form.setConfirmEmail("test@test.com");
    form.setTrialCode(promocode);
    form.setTenant(newTenant);
    form.getUser().setAddress(new Address("steve", "creek", "cupertino", "ca", "95014", "US"));
    return form;
  }

  @Test
  public void createTrialAccountwithExpiredPromocode() {
    try {
      // Creates a new Trial Tenant
      TenantForm form = new TenantForm((com.citrix.cpbm.access.Tenant) CustomProxy.newInstance(new Tenant()));
      form = setTrialTenantForm("expired_code");
      BindingResult result = valid(form);
      controller.create(form, result, map, status, new MockHttpServletRequest());
      Tenant found = tenantDAO.findByAccountId(form.getTenant().getAccountId());
      Assert.assertNull(found);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }

  }

  @Test
  public void createTrialAccount() throws TrialCodeInvalidException {
    try {
      // Creates a new Trial Tenant
      TenantForm form = new TenantForm((com.citrix.cpbm.access.Tenant) CustomProxy.newInstance(new Tenant()));
      form = setTrialTenantForm(promotionToken.getCode());
      BindingResult result = valid(form);
      controller.create(form, result, map, status, new MockHttpServletRequest());
      Tenant found = tenantDAO.findByAccountId(form.getTenant().getAccountId());
      Assert.assertNotNull(found);
      Assert.assertEquals(form.getTenant(), found);
      Assert.assertEquals(found.getAccountType().getId(), new Long("5"));
      Assert.assertEquals(found.getOwner().getUsername(), form.getUser().getUsername());
      Assert.assertTrue(status.isComplete());
      Assert.assertEquals(2, eventListener.getEvents().size());
      PortalEvent event = eventListener.getEvents().get(1);
      Assert.assertTrue(event.getSource() instanceof TenantStateChangeTransaction);
      Assert.assertEquals(form.getTenant(), ((TenantStateChangeTransaction) event.getSource()).getTenant());
    } catch (TrialCodeInvalidException e) {
      e.printStackTrace();
      e.getMessage();
      Assert.fail();
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }

  }

  @Test
  public void createTrialAccountWithInvalidPromocode() throws TrialCodeInvalidException {
    String invalidPromocode = "abc";
    try {
      // Creates a new Trial Tenant
      TenantForm form = new TenantForm((com.citrix.cpbm.access.Tenant) CustomProxy.newInstance(new Tenant()));
      form = setTrialTenantForm(invalidPromocode);
      BindingResult result = valid(form);
      controller.create(form, result, map, status, new MockHttpServletRequest());
      Tenant found = tenantDAO.findByAccountId(form.getTenant().getAccountId());
      Assert.assertNull(found);
    } catch (TrialCodeInvalidException e) {
      if (!e.getMessage().contains("Promote code " + invalidPromocode + " doesn't exist")) {
        Assert.fail();
      }
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
  }
}

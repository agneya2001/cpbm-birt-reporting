/*
 * Copyright © 2013 Citrix Systems, Inc. You may not use, copy, or modify this file except pursuant to a valid license
 * agreement from Citrix Systems, Inc.
 */
package fragment.web;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;

import web.WebTestsBaseWithMockConnectors;

import com.citrix.cpbm.portal.fragment.controllers.ProfilesController;
import com.vmops.model.Profile;
import com.vmops.model.ProfileAuthority;
import com.vmops.model.SecurityContextScope.Scope;
import com.vmops.model.User;
import com.vmops.persistence.ProfileDAO;
import com.vmops.service.ProfileService;
import com.vmops.web.forms.ProfileForm;

public class AbstractProfilesControllerTest extends WebTestsBaseWithMockConnectors {

  @Autowired
  private ProfilesController profilesController;

  @Autowired
  private ProfileDAO profileDAO;

  @Autowired
  private ProfileService profileService;

  private ModelMap map;

  /**
   * Author: vinayv Description: Test to get ServiceProvider and Customer users profiles
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testShowProfiles() {

    map = new ModelMap();
    String result = profilesController.showProfiles(null, null, map);
    Assert.assertNotNull(result);
    Assert.assertEquals("profiles.show", result);
    List<ProfileForm> allProfileList = (List<ProfileForm>) map.get("allProfileList");
    Assert.assertNotNull(allProfileList);
    Assert.assertEquals(profileDAO.count(), allProfileList.size());
    List<Profile> globalProfileList = (List<Profile>) profileService.listAllProfilesOfClass(Scope.GLOBAL);
    List<ProfileForm> opsProfileList = (List<ProfileForm>) map.get("opsProfileList");
    Assert.assertEquals(globalProfileList.size(), opsProfileList.size());
    List<Profile> tenantProfileList = (List<Profile>) profileService.listAllProfilesOfClass(Scope.TENANT);
    List<ProfileForm> nonOpsProfileList = (List<ProfileForm>) map.get("nonOpsProfileList");
    Assert.assertEquals(tenantProfileList.size(), nonOpsProfileList.size());
    String profileID = (String) map.get("selectedProfile");
    Assert.assertEquals(null, profileID);
  }

  /**
   * Author: vinayv Description: Test to get ServiceProvider and Customer users profiles with profileId
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testShowProfilesWithProfileId() {

    map = new ModelMap();
    String result = profilesController.showProfiles("2", null, map);
    Assert.assertNotNull(result);
    Assert.assertEquals("profiles.show", result);
    List<ProfileForm> allProfileList = (List<ProfileForm>) map.get("allProfileList");
    Assert.assertNotNull(allProfileList);
    Assert.assertEquals(profileDAO.count(), allProfileList.size());
    List<Profile> globalProfileList = (List<Profile>) profileService.listAllProfilesOfClass(Scope.GLOBAL);
    List<ProfileForm> opsProfileList = (List<ProfileForm>) map.get("opsProfileList");
    Assert.assertEquals(globalProfileList.size(), opsProfileList.size());
    List<Profile> tenantProfileList = (List<Profile>) profileService.listAllProfilesOfClass(Scope.TENANT);
    List<ProfileForm> nonOpsProfileList = (List<ProfileForm>) map.get("nonOpsProfileList");
    Assert.assertEquals(tenantProfileList.size(), nonOpsProfileList.size());
    String profileID = (String) map.get("selectedProfile");
    Assert.assertEquals("2", profileID);
  }

  /**
   * Author: vinayv Description: Test to edit ServiceProvider users profiles
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testEditServiceProviderUserProfiles() {

    map = new ModelMap();
    Profile profile = profileService.getProfile(4L);
    String authorityNames = ",ROLE_TICKET_MANAGEMENT";
    String result = profilesController.editProfile(map, "4", authorityNames);
    Assert.assertNotNull(result);
    Assert.assertEquals("profiles.show", result);
    List<ProfileAuthority> authorityList = profile.getAuthorityList();
    Assert.assertEquals(1, authorityList.size());
    List<ProfileForm> allProfileList = (List<ProfileForm>) map.get("allProfileList");
    Assert.assertNotNull(allProfileList);
    Assert.assertEquals(profileDAO.count(), allProfileList.size());
    List<Profile> globalProfileList = (List<Profile>) profileService.listAllProfilesOfClass(Scope.GLOBAL);
    List<ProfileForm> opsProfileList = (List<ProfileForm>) map.get("opsProfileList");
    Assert.assertEquals(globalProfileList.size(), opsProfileList.size());
    List<Profile> tenantProfileList = (List<Profile>) profileService.listAllProfilesOfClass(Scope.TENANT);
    List<ProfileForm> nonOpsProfileList = (List<ProfileForm>) map.get("nonOpsProfileList");
    Assert.assertEquals(tenantProfileList.size(), nonOpsProfileList.size());
    Assert.assertTrue(map.containsAttribute("systemProfileName"));
  }

  /**
   * Author: vinayv Description: Test to edit Customer users profiles
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testEditCustomerUserProfiles() {

    map = new ModelMap();
    Profile profile = profileService.getProfile(9L);
    String authorityNames = ",ROLE_USER";
    String result = profilesController.editProfile(map, "9", authorityNames);
    Assert.assertNotNull(result);
    Assert.assertEquals("profiles.show", result);
    List<ProfileAuthority> authorityList = profile.getAuthorityList();
    Assert.assertEquals(1, authorityList.size());
    List<ProfileForm> allProfileList = (List<ProfileForm>) map.get("allProfileList");
    Assert.assertNotNull(allProfileList);
    Assert.assertEquals(profileDAO.count(), allProfileList.size());
    List<Profile> globalProfileList = (List<Profile>) profileService.listAllProfilesOfClass(Scope.GLOBAL);
    List<ProfileForm> opsProfileList = (List<ProfileForm>) map.get("opsProfileList");
    Assert.assertEquals(globalProfileList.size(), opsProfileList.size());
    List<Profile> tenantProfileList = (List<Profile>) profileService.listAllProfilesOfClass(Scope.TENANT);
    List<ProfileForm> nonOpsProfileList = (List<ProfileForm>) map.get("nonOpsProfileList");
    Assert.assertEquals(tenantProfileList.size(), nonOpsProfileList.size());
  }

  /**
   * Author: vinayv Description: Test to edit profiles logging in as User
   */
  @Test
  public void testEditProfilesAsUser() {
    try {
      User user = userDAO.find(3L);
      asUser(user);
      map = new ModelMap();
      String authorityNames = ",ROLE_TICKET_MANAGEMENT";
      profilesController.editProfile(map, "4", authorityNames);
    } catch (Exception e) {
      Assert.assertEquals("Access is denied", e.getMessage());
    }
  }
}

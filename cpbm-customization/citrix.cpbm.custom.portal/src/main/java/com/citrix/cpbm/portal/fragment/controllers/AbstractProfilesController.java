/*
 * Copyright © 2013 Citrix Systems, Inc. You may not use, copy, or modify this file except pursuant to a valid license
 * agreement from Citrix Systems, Inc.
 */
package com.citrix.cpbm.portal.fragment.controllers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.vmops.model.Authority;
import com.vmops.model.Profile;
import com.vmops.model.ProfileAuthority;
import com.vmops.model.SecurityContextScope.Scope;
import com.vmops.service.AuthorityService;
import com.vmops.service.ProfileService;
import com.vmops.service.UserService.Handle;
import com.vmops.web.controllers.AbstractAuthenticatedController;
import com.vmops.web.controllers.menu.Page;
import com.vmops.web.forms.ProfileForm;
import com.vmops.web.forms.SelectedAuthority;

public abstract class AbstractProfilesController extends AbstractAuthenticatedController {

  @Autowired
  private ProfileService profileService;

  @Autowired
  private AuthorityService authorityService;

  private Logger logger = Logger.getLogger(AbstractProfilesController.class);

  @RequestMapping(value = "/show", method = RequestMethod.GET)
  public String showProfiles(@RequestParam(value = "profileId", required = false) String profileId,
      @RequestParam(value = "selectedTab", required = false) String selectedTab, ModelMap map) {

    logger.debug("###Entering in showProfiles method @GET");

    List<ProfileForm> opsProfileList = getProfileFormList(profileService.listAllProfilesOfClass(Scope.GLOBAL), true);

    List<ProfileForm> nonOpsProfileList = getProfileFormList(profileService.listAllProfilesOfClass(Scope.TENANT), false);
    List<ProfileForm> allProfileList = new ArrayList<ProfileForm>(opsProfileList);
    allProfileList.addAll(nonOpsProfileList);
    map.addAttribute("allProfileList", allProfileList);
    setPage(map, Page.ADMIN_PROFILES);
    map.addAttribute("opsProfileList", opsProfileList);
    map.addAttribute("nonOpsProfileList", nonOpsProfileList);
    map.addAttribute("authorityTypes", authorityService.getAllAuthorities());
    map.addAttribute("selectedProfile", profileId);
    map.addAttribute("selectedTab", selectedTab);
    if (StringUtils.isNotEmpty(profileId)) {
      Profile profile = profileService.getProfile(Long.valueOf(profileId));
      map.addAttribute("isOpsProfile", profile.isOperationsProfile());
    } else {
      map.addAttribute("isOpsProfile", "");
    }
    String systemProfileName = userService.getSystemUser(Handle.PORTAL).getProfile().getName();
    String rootProfileName = tenantService.getSystemTenant().getOwner().getProfile().getName();

    map.addAttribute("systemProfileName", systemProfileName);
    map.addAttribute("rootProfileName", rootProfileName);

    logger.debug("###Exiting showProfiles method @GET");

    return "profiles.show";
  }

  @RequestMapping(value = "/{profileId}/edit", method = RequestMethod.POST)
  public String editProfile(ModelMap map, @PathVariable String profileId, @RequestParam("roles") String authorityNames) {

    logger.debug("###Entering in editProfile method @POST profileid:" + profileId);

    setPage(map, Page.ADMIN_PROFILES);
    try {

      Profile profile = profileService.getProfile(Long.valueOf(profileId));

      if (authorityNames.startsWith(",")) {
        authorityNames = authorityNames.substring(1);
      }
      String[] authorityTypes = authorityNames.split(",");

      profileService.save(profile, authorityTypes);

      map.addAttribute("updatedProfile", profile.getId());
      map.addAttribute("isOpsProfile", profile.isOperationsProfile());
    } catch (HibernateOptimisticLockingFailureException ex) {
      logger.error(ex);
      return ex.getMessage();

    } catch (DataAccessException ex) {
      logger.error(ex);
      return ex.getMessage();
    }

    List<ProfileForm> opsProfileList = getProfileFormList(profileService.listAllProfilesOfClass(Scope.GLOBAL), true);
    List<ProfileForm> nonOpsProfileList = getProfileFormList(profileService.listAllProfilesOfClass(Scope.TENANT), false);
    List<ProfileForm> allProfileList = new ArrayList<ProfileForm>(opsProfileList);
    allProfileList.addAll(nonOpsProfileList);
    map.addAttribute("allProfileList", allProfileList);
    map.addAttribute("opsProfileList", opsProfileList);
    map.addAttribute("nonOpsProfileList", nonOpsProfileList);

    map.addAttribute("authorityTypes", authorityService.getAllAuthorities());
    String systemProfileName = userService.getSystemUser(Handle.PORTAL).getProfile().getName();
    String rootProfileName = tenantService.getSystemTenant().getOwner().getProfile().getName();

    map.addAttribute("systemProfileName", systemProfileName);
    map.addAttribute("rootProfileName", rootProfileName);

    logger.debug("###Exiting editProfile method @POST");

    return "profiles.show";
  }

  private List<ProfileForm> getProfileFormList(Collection<Profile> profileList, boolean ops) {

    logger.debug("###Entering in getProfileFormList method");

    List<ProfileForm> formList = new ArrayList<ProfileForm>();

    ProfileForm rootProfileForm = null;
    for (Profile profile : profileList) {
      // gets a list of all applicable authorities for a profile
      Collection<Authority> authorityScopeLst = authorityService.getAllApplicableAuthorities(profile);

      ProfileForm profileForm = new ProfileForm(profile);
      String profileName = profile.getName().trim();
      if (!profileName.equalsIgnoreCase(privilegeService.getOwnerProfile().getName())
          && !profileName.equalsIgnoreCase("Root")
          && !profileName.equalsIgnoreCase(privilegeService.getUserProfile().getName())) {
        profileForm.setProfileDelete(true);
      }

      // gets a list of all enabled authorities for a profile.
      // why return type is ProfileAuthority?
      List<ProfileAuthority> authList = profile.getAuthorityList();

      int pos = -1;
      int arrLength = authorityService.getAllAuthorities().size();
      String[] authorityNames = new String[arrLength];
      boolean[] authorities = new boolean[arrLength];
      List<SelectedAuthority> authorityType = new ArrayList<SelectedAuthority>();
      String authorityStr = "";
      for (Authority authorityScope : authorityScopeLst) {
        boolean found = false;
        boolean required = false;
        String serviceName = authorityScope.getCpbmService() == null ? "" : authorityScope.getCpbmService()
            .getServiceName();

        pos++;
        for (ProfileAuthority auth : authList) {
          if (auth.getAuthorityScope().equals(authorityScope)) {
            if (auth.getAuthorityScope().isAuthorityRequired()) {
              required = true;
            }
            if (authorityStr.equals("")) {
              authorityStr = auth.getAuthorityScope().getAuthority();
            } else {
              authorityStr = authorityStr + "," + auth.getAuthorityScope().getAuthority();
            }
            found = true;
          }
        }

        authorityNames[pos] = authorityScope.getAuthority();
        authorities[pos] = found;
        profileForm.setSelected(found);

        authorityType.add(new SelectedAuthority(authorityScope.getAuthority(), found, "", required, serviceName));
      }

      profileForm.setTypeList(authorityType);
      profileForm.setAuthorities(authorities);
      profileForm.setAuthorityNames(authorityNames);
      profileForm.setAuthorityStr(authorityStr);
      if (profileForm.getProfile().getName().equalsIgnoreCase("Root")) {
        rootProfileForm = profileForm;
      } else {
        formList.add(profileForm);
      }
    }
    if (rootProfileForm != null) {
      formList.add(rootProfileForm);
    }

    Collections.sort(formList);

    logger.debug("###Exiting getProfileFormList method");

    return formList;
  }

}

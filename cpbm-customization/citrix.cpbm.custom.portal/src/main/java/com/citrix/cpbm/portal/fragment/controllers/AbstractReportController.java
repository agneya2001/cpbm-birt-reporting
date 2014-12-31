/*
 * Copyright © 2013 Citrix Systems, Inc. You may not use, copy, or modify this file except pursuant to a valid license
 * agreement from Citrix Systems, Inc.
 */
package com.citrix.cpbm.portal.fragment.controllers;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vmops.constants.CustomReportsConstants;
import com.vmops.internal.service.UsageService;
import com.vmops.model.AccountType;
import com.vmops.model.BillingGroup;
import com.vmops.model.Channel;
import com.vmops.model.Event;
import com.vmops.model.Event.Category;
import com.vmops.model.Event.Scope;
import com.vmops.model.Event.Severity;
import com.vmops.model.Event.Source;
import com.vmops.model.Product;
import com.vmops.model.Report;
import com.vmops.model.ScheduledCustomReports;
import com.vmops.portal.config.Configuration;
import com.vmops.portal.config.Configuration.Names;
import com.vmops.portal.reports.GenericReport;
import com.vmops.reports.CustomerRankReport;
import com.vmops.reports.MonthlyProductBundleUsage;
import com.vmops.reports.MonthlyProductUsage;
import com.vmops.reports.NewRegistrationReport;
import com.vmops.service.ReportService;
import com.vmops.service.SequenceService;
import com.vmops.service.TenantService;
import com.vmops.usage.model.ChannelUsage;
import com.vmops.web.controllers.AbstractAuthenticatedController;
import com.vmops.web.controllers.menu.Page;
import com.vmops.web.forms.ChannelUsageReportForm;
import com.vmops.web.forms.CustomerRankReportForm;
import com.vmops.web.forms.NewRegistrationReportForm;
import com.vmops.web.validators.ChannelUsageReportFormValidator;
import com.vmops.web.validators.CustomerRankReportValidator;
import com.vmops.web.validators.NewRegistrationReportValidator;

public abstract class AbstractReportController extends AbstractAuthenticatedController {

  @Autowired
  private DataSource dataSource;

  @Autowired
  private ReportService reportService;

  @Autowired
  private TenantService tenantService;

  @Autowired
  private UsageService usageService;

  @Autowired
  private Configuration config;

  @Autowired
  private SequenceService sequenceService;

// CHECKSTYLE:OFF
  protected String emailDelemeter = ";";

  public static final String csvSeperator = ",";

  public static final String quotes = "\"";

  public static final String blank = "";

// CHECKSTYLE:ON
  @RequestMapping(value = {
      "", "/new_registrations"
  }, method = {
    RequestMethod.GET
  })
  public String newRegistrations(@RequestParam(value = "start", required = false) String start,
      @RequestParam(value = "end", required = false) String end, ModelMap modelMap, HttpServletRequest request) {
    logger.debug("###Entering in NewRegistrations(modelMap) with start:" + start);
    setPage(modelMap, Page.REPORTS_HOME);
    getRegistrationReport(start, end, modelMap, getSessionLocale(request));
    NewRegistrationReportForm newRegistrationForm = new NewRegistrationReportForm();
    modelMap.addAttribute("registrationFormReport", newRegistrationForm);
    logger.debug("###Exiting NewRegistrations(modelMap) method @GET");
    return "report.newRegistration";
  }

  @RequestMapping(value = {
      "", "/new_registrations"
  }, method = {
    RequestMethod.POST
  })
  public String newRegistrations(ModelMap modelMap, HttpServletRequest request,
      @ModelAttribute(value = "registrationFormReport") NewRegistrationReportForm newRegistrationForm,
      BindingResult result) {
    logger.debug("###Entering in NewRegistrations(modelMap) method @POST");
    setPage(modelMap, Page.REPORTS_HOME);
    NewRegistrationReportValidator newRegistrationValidator = new NewRegistrationReportValidator();
    newRegistrationValidator.validate(newRegistrationForm, result);
    if (result.hasErrors()) {
      modelMap.addAttribute("registrationFormReport", newRegistrationForm);
      List<FieldError> l = result.getFieldErrors();
      for (FieldError f : l) {
        if (f.getCode().equals("js.report.errors.validationDate")) {
          return "report.newRegistration";
        }
      }
    }
    getRegistrationReport(newRegistrationForm.getStartDate(), newRegistrationForm.getEndDate(), modelMap,
        getSessionLocale(request));
    logger.debug("###Exiting NewRegistrations(modelMap) method @POST");
    return "report.newRegistration";
  }

  @RequestMapping(value = "/customer_rank", method = {
    RequestMethod.GET
  })
  public String customersRank(@RequestParam(value = "month", required = false) String month,
      @RequestParam(value = "year", required = false) String year, ModelMap modelMap, HttpServletRequest request) {
    logger.debug("###Entering in customerRank(modelMap) method @GET");
    String status = getReport(month, year, null, modelMap, CustomerRankReport.class, Page.REPORTS_CUSTOMER_RANK,
        getSessionLocale(request));
    if (status.equalsIgnoreCase("failure")) {
      return "failure";
    }
    CustomerRankReportForm customerRankReportForm = new CustomerRankReportForm();
    modelMap.addAttribute("customerRankReport", customerRankReportForm);
    modelMap.addAttribute("rotateYAxisLabels", true);
    modelMap.addAttribute("formatNumber", true);

    logger.debug("###Exiting customerRank(modelMap) method @GET");
    return "report.customerRank";
  }

  @RequestMapping(value = "/customer_rank", method = {
    RequestMethod.POST
  })
  public String customersRank(ModelMap modelMap, HttpServletRequest request,
      @ModelAttribute(value = "customerRankReport") CustomerRankReportForm customerRankReportForm, BindingResult result) {
    logger.debug("###Entering in customerRank(modelMap) method @POST");
    CustomerRankReportValidator customerRankReportValidator = new CustomerRankReportValidator();
    customerRankReportValidator.validate(customerRankReportForm, result);
    if (result.hasErrors()) {
      setPage(modelMap, Page.REPORTS_CUSTOMER_RANK);
      modelMap.addAttribute("customerRankReport", customerRankReportForm);
      List<FieldError> l = result.getFieldErrors();
      for (FieldError f : l) {
        if (f.getCode().equals("js.report.errors.validationMonth")
            || f.getCode().equals("js.report.errors.validationYear")) {
          addMonthAndYearToMap(modelMap, getSessionLocale(request));
          return "report.customerRank";
        }
      }
    }
    String status = getReport(customerRankReportForm.getReportMonth(), customerRankReportForm.getReportYear(), null,
        modelMap, CustomerRankReport.class, Page.REPORTS_CUSTOMER_RANK, getSessionLocale(request));
    if (status.equalsIgnoreCase("failure")) {
      return "failure";
    }
    modelMap.addAttribute("customerRankReport", customerRankReportForm);
    modelMap.addAttribute("rotateYAxisLabels", true);
    modelMap.addAttribute("formatNumber", true);
    logger.debug("###Exiting customerRank(modelMap) method @POST");
    return "report.customerRank";
  }

  @RequestMapping(value = "/product_usage", method = {
    RequestMethod.GET
  })
  public String productUsage(@RequestParam(value = "month", required = false) String month,
      @RequestParam(value = "year", required = false) String year, ModelMap modelMap, HttpServletRequest request) {
    logger.debug("###Entering in productUsage(modelMap) method @GET");
    String status = getReport(month, year, null, modelMap, MonthlyProductUsage.class, Page.REPORTS_PRODUCT_USAGE,
        getSessionLocale(request));
    if (status.equalsIgnoreCase("failure")) {
      return "failure";
    }
    CustomerRankReportForm customerRankReportForm = new CustomerRankReportForm();
    List<Channel> channels = channelService.getChannels(null, null, "", null);
    modelMap.addAttribute("channels", channels);
    modelMap.addAttribute("customerRankReport", customerRankReportForm);
    modelMap.addAttribute("usageReportName", "productUsage");
    modelMap.addAttribute("rotateXAxisLabels", true);
    logger.debug("###Exiting productUsage(modelMap) method @GET");
    return "report.usageSpend";
  }

  @RequestMapping(value = "/product_usage", method = {
    RequestMethod.POST
  })
  public String productUsage(ModelMap modelMap, HttpServletRequest request,
      @ModelAttribute(value = "customerRankReport") CustomerRankReportForm customerRankReportForm, BindingResult result) {
    logger.debug("###Entering in productUsage(modelMap) method @POST");
    CustomerRankReportValidator customerRankReportValidator = new CustomerRankReportValidator();
    customerRankReportValidator.validate(customerRankReportForm, result);
    if (result.hasErrors()) {
      setPage(modelMap, Page.REPORTS_PRODUCT_USAGE);
      modelMap.addAttribute("customerRankReport", customerRankReportForm);
      List<FieldError> l = result.getFieldErrors();
      for (FieldError f : l) {
        if (f.getCode().equals("js.report.errors.validationMonth")
            || f.getCode().equals("js.report.errors.validationYear")) {
          addMonthAndYearToMap(modelMap, getSessionLocale(request));
          return "report.usageSpend";
        }
      }
    }
    String status = getReport(customerRankReportForm.getReportMonth(), customerRankReportForm.getReportYear(),
        customerRankReportForm.getChannelId(), modelMap, MonthlyProductUsage.class, Page.REPORTS_PRODUCT_USAGE,
        getSessionLocale(request));
    if (status.equalsIgnoreCase("failure")) {
      return "failure";
    }
    List<Channel> channels = channelService.getChannels(null, null, "", null);
    modelMap.addAttribute("channels", channels);
    modelMap.addAttribute("customerRankReport", customerRankReportForm);
    modelMap.addAttribute("usageReportName", "productUsage");
    modelMap.addAttribute("rotateXAxisLabels", true);
    logger.debug("###Exiting productUsage(modelMap) method @POST");
    return "report.usageSpend";
  }

  @RequestMapping(value = "/productbundle_usage", method = {
    RequestMethod.GET
  })
  public String productBundleUsage(@RequestParam(value = "month", required = false) String month,
      @RequestParam(value = "year", required = false) String year, ModelMap modelMap, HttpServletRequest request) {
    logger.debug("###Entering in productBundleUsage(modelMap) method @GET");
    String status = getReport(month, year, null, modelMap, MonthlyProductBundleUsage.class, Page.REPORTS_PRODUCT_USAGE,
        getSessionLocale(request));
    if (status.equalsIgnoreCase("failure")) {
      return "failure";
    }
    CustomerRankReportForm customerRankReportForm = new CustomerRankReportForm();
    modelMap.addAttribute("customerRankReport", customerRankReportForm);
    modelMap.addAttribute("usageReportName", "productbundleUsage");
    modelMap.addAttribute("rotateXAxisLabels", true);
    logger.debug("###Exiting productBundleUsage(modelMap) method @GET");
    return "report.usageSpend";
  }

  @RequestMapping(value = "/productbundle_usage", method = {
    RequestMethod.POST
  })
  public String productBundleUsage(ModelMap modelMap, HttpServletRequest request,
      @ModelAttribute(value = "customerRankReport") CustomerRankReportForm customerRankReportForm, BindingResult result) {
    logger.debug("###Entering in productBundleUsage(modelMap) method @POST");
    CustomerRankReportValidator customerRankReportValidator = new CustomerRankReportValidator();
    customerRankReportValidator.validate(customerRankReportForm, result);
    if (result.hasErrors()) {
      setPage(modelMap, Page.REPORTS_PRODUCT_USAGE);
      modelMap.addAttribute("customerRankReport", customerRankReportForm);
      List<FieldError> l = result.getFieldErrors();
      for (FieldError f : l) {
        if (f.getCode().equals("js.report.errors.validationMonth")
            || f.getCode().equals("js.report.errors.validationYear")) {
          return "report.usageSpend";
        }
      }
    }
    String status = getReport(customerRankReportForm.getReportMonth(), customerRankReportForm.getReportYear(), null,
        modelMap, MonthlyProductBundleUsage.class, Page.REPORTS_PRODUCT_USAGE, getSessionLocale(request));
    if (status.equalsIgnoreCase("failure")) {
      return "failure";
    }
    modelMap.addAttribute("customerRankReport", customerRankReportForm);
    modelMap.addAttribute("usageReportName", "productbundleUsage");
    modelMap.addAttribute("rotateXAxisLabels", true);
    logger.debug("###Exiting productBundleUsage(modelMap) method @POST");
    return "report.usageSpend";
  }

  @RequestMapping(value = "/channel_usage", method = {
    RequestMethod.GET
  })
  public String channelUsage(ModelMap modelMap, HttpServletRequest request) {
    logger.debug("###Entering in Channel Usage Report(modelMap) ");
    setPage(modelMap, Page.REPORTS_CHANNEL_USAGE);

    ChannelUsageReportForm channelUsageReportForm = new ChannelUsageReportForm();
    List<Channel> channels = channelService.getChannels(null, null, "", null);
    modelMap.addAttribute("channels", channels);
    Collection<BillingGroup> billingGroups = channelService.getBillingGroups();
    modelMap.addAttribute("billingGroups", billingGroups);
    modelMap.addAttribute("channelUsageReportForm", channelUsageReportForm);

    logger.debug("###Exiting Channel Usage Report(modelMap) method @GET");
    return "report.channelUsage";
  }

  @RequestMapping(value = "/channel_usage", method = {
    RequestMethod.POST
  })
  public String channelUsage(ModelMap modelMap, HttpServletRequest request,
      @ModelAttribute(value = "channelUsageReportForm") ChannelUsageReportForm channelUsageReportForm,
      BindingResult result, HttpServletResponse response) {
    logger.debug("###Entering in ChannelUsage(modelMap) method @POST");

    modelMap.put("noRecordsFound", false);
    ChannelUsageReportFormValidator channelUsageReportFormValidator = new ChannelUsageReportFormValidator();
    channelUsageReportFormValidator.validate(channelUsageReportForm, result);
    if (result.hasErrors()) {
      setPage(modelMap, Page.REPORTS_CHANNEL_USAGE);
      modelMap.addAttribute("channelUsageReportForm", channelUsageReportForm);
      List<FieldError> l = result.getFieldErrors();
      for (FieldError f : l) {
        if (f.getCode().equals("js.report.errors.validationStartDate")
            || f.getCode().equals("js.report.errors.validationEndDate")) {
          return "report.channelUsage";
        }
      }
    }
    try {
      Channel channel = null;
      if (StringUtils.isNotBlank(channelUsageReportForm.getChannelId())) {
        channel = channelService.getChannelById(channelUsageReportForm.getChannelId());
        modelMap.addAttribute("usageReportName", "channelUsage");
      }

      BillingGroup billingGroup = null;
      if (StringUtils.isNotBlank(channelUsageReportForm.getBillingGroupId())) {
        billingGroup = channelService.getBillingGroup(Long.parseLong(channelUsageReportForm.getBillingGroupId()));
        modelMap.addAttribute("usageReportName", "billingGroupUsage");
      }
      response.setContentType("text/csv");

      SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
      Date startDate = sdf.parse(channelUsageReportForm.getStartDate());
      Date endDate = sdf.parse(channelUsageReportForm.getEndDate());

      sdf = new SimpleDateFormat("MMddyyyy");

      List<ChannelUsage> lstAggUsage = new ArrayList<ChannelUsage>();
      String filenameSuffix = "";
      if (channel != null) {
        lstAggUsage = usageService.getAggregatedUsage(channel, startDate, endDate, null);
        filenameSuffix += channel.getName();
      } else if (billingGroup != null) {
        lstAggUsage = usageService.getAggregatedUsage(billingGroup, startDate, endDate, null);
        filenameSuffix += billingGroup.getName();
      }

      String portalTempPath = config.getValue(Names.com_citrix_cpbm_portal_settings_temp_path);
      String filename = portalTempPath + File.separator + filenameSuffix + System.currentTimeMillis() + ".csv";
      if (CollectionUtils.isNotEmpty(lstAggUsage)) {
        writeToCSV(lstAggUsage, filename, request);
        String targetFilename = filenameSuffix + "_" + sdf.format(startDate) + "_" + sdf.format(endDate) + ".csv";
        downloadFile(filename, targetFilename, response);
      } else {
        modelMap.put("noRecordsFound", true);
        modelMap.put("selectedChannel", channelUsageReportForm.getChannelId());
        modelMap.put("selectedBillingGroup", channelUsageReportForm.getBillingGroupId());
      }
    } catch (ParseException pe) {
      logger.error("Error while parsing start and end date", pe);
      return "downloadCss.error";
    } catch (IOException e) {
      logger.error(e.getMessage(), e);
      return "downloadCss.error";
    }

    List<Channel> channels = channelService.getChannels(null, null, "", null);
    modelMap.addAttribute("channels", channels);
    Collection<BillingGroup> billingGroups = channelService.getBillingGroups();
    modelMap.addAttribute("billingGroups", billingGroups);
    modelMap.addAttribute("channelUsageReportForm", channelUsageReportForm);
    setPage(modelMap, Page.REPORTS_CHANNEL_USAGE);
    logger.debug("###Exiting Channel usage(modelMap) method @POST");
    return "report.channelUsage";
  }

  @RequestMapping(value = "/custom_reports", method = RequestMethod.GET)
  public String customReports(ModelMap modelMap, HttpServletRequest request) throws Exception {
    logger.debug("###Entering in customReports(modelMap) method @GET");
    List<ScheduledCustomReports> list = reportService.getAllScheduledCustomReports();
    Map<String, String> reportMapping = new HashMap<String, String>();
    for (ScheduledCustomReports schedReport : list) {
      reportMapping.put("" + schedReport.getId(),
          schedReport.getCustomReport().getName() + " - " + schedReport.getFrequency());
    }
    modelMap.addAttribute("reportMapping", reportMapping);

    setPage(modelMap, Page.REPORTS_CUSTOM);
    modelMap.addAttribute("tenant", getCurrentUser().getTenant());
    addMonthAndYearToMap(modelMap, getSessionLocale(request));
    logger.debug("###Exiting customReports(modelMap) method @GET");
    return "report.customReports";
  }

  @RequestMapping(value = "/generate_custom_reports", method = RequestMethod.GET)
  @ResponseBody
  public String generateCustomReports(@RequestParam(value = "customreport", required = true) String reportId,
      @RequestParam(value = "date", required = true) String date,
      @RequestParam(value = "month", required = true) String month,
      @RequestParam(value = "year", required = true) String year, HttpServletRequest request,
      HttpServletResponse response, ModelMap modelMap) throws Exception {
    logger.debug("Enter generateCustomReports");
    ScheduledCustomReports schedReport = reportService.getScheduledCustomReportById(reportId);
    StringBuffer result = new StringBuffer();
    String type = schedReport.getFrequency();
    Calendar cal = Calendar.getInstance();
    if (date != null) {
      SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
      Date dt = sdf.parse(date);
      cal.setTime(dt);
    }
    String targetFileName = reportService.generateCustomReport(schedReport, type, sequenceService, result,
        CustomReportsConstants.REPORT_GENERATE_MODE, cal, month, year);
    return targetFileName;
  }

  @RequestMapping(value = "/download_custom_report/{filename}", method = RequestMethod.GET)
  @ResponseBody
  public void downloadCustomReport(@PathVariable(value = "filename") String filename, ModelMap modelMap,
      HttpServletResponse response) {
    logger.debug("###Entering downloadCustomReport method @ with file name:" + filename);

    String portalTempPath = config.getValue(Names.com_citrix_cpbm_portal_settings_temp_path);
    File targetZipFile = new File(portalTempPath + File.separator + filename + ".zip");

    response.setContentType("application/zip");
    response.setHeader("Content-Disposition", "attachment; filename=" + filename + ".zip");
    OutputStream out = null;
    InputStream is = null;
    try {
      out = response.getOutputStream();
      is = new BufferedInputStream(new FileInputStream(targetZipFile));
      FileCopyUtils.copy(is, response.getOutputStream());
      out.flush();
    } catch (IOException e) {
      logger.error(e.getMessage(), e);
    } finally {
      try {
        if (is != null) {
          is.close();
        }
        out.close();
      } catch (IOException e) {
        logger.error("Error while closing streams", e);
      }

    }
    logger.debug("###Exiting downloadCustomReport method");
  }

  @RequestMapping(value = "/email_custom_report/{filename}", method = RequestMethod.GET)
  @ResponseBody
  public String emailCustomReport(@PathVariable(value = "filename") String filename,
      @RequestParam(value = "emailIds", required = true) String emailIds, ModelMap modelMap,
      HttpServletResponse response) {
    logger.debug("###Entering emailCustomReport method @ with file name:" + filename);
    try {
      String portalTempPath = config.getValue(Names.com_citrix_cpbm_portal_settings_temp_path);
      File targetZipFile = new File(portalTempPath + File.separator + filename + ".zip");
      reportService.sendReportAsEmail(getCurrentUser().getUsername(), StringUtils.split(emailIds, emailDelemeter),
          "Test", null, targetZipFile);
      String message = "report.sent";
      String messageArguments = getCurrentUser().getUsername();
      Event alert = new Event(new Date(), message, messageArguments, getCurrentUser(), Source.PORTAL, Scope.USER,
          Category.ACCOUNT, Severity.INFORMATION, true);
      eventService.createEvent(alert, false);
    } catch (Exception e) {
      logger.error("Error in sending report in email", e);
      return "failure";
    }
    logger.debug("###Exiting emailCustomReport method @");
    return "success";
  }

  @RequestMapping(value = "/generate_CSV", method = RequestMethod.POST)
  @ResponseBody
  public void generateCSV(@RequestParam(value = "csvdata", required = true) String csvdata, ModelMap modelMap,
      HttpServletResponse response) {
    logger.debug("###Entering generateCSV method @");

    response.setContentType("application/csv");
    response.setHeader("Content-Disposition", "attachment; filename=report.csv");
    response.addHeader("Cache-Control", "no-store, no-cache");
    OutputStream out;
    try {
      out = response.getOutputStream();
      response.setContentLength(csvdata.getBytes().length);
      out.write(csvdata.getBytes());
      out.flush();
      out.close();
    } catch (IOException e) {
      logger.error(e.getMessage(), e);
    }
    logger.debug("###Exiting generateCSV method @");
  }

  private void getRegistrationReport(String start, String end, ModelMap modelMap, Locale locale) {
    Report report = null;
    HashMap<String, Object> parameters = new HashMap<String, Object>();
    Calendar cal = Calendar.getInstance();
    DateFormat format = new SimpleDateFormat("MM/dd/yyyy");
    if (start == null) {
      cal.set(Calendar.DATE, 1);
      start = format.format(cal.getTime());
    }
    if (end == null) {
      cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
      end = format.format(cal.getTime());
    }

    parameters.put("startDate", start);
    parameters.put("endDate", end);
    List<AccountType> accountTypeList = tenantService.getAllAccountTypes();
    accountTypeList.remove(tenantService.getAccountTypeByName("SYSTEM"));
    GenericReport nrr = new NewRegistrationReport(parameters, dataSource, accountTypeList, locale, messageSource);
    report = reportService.generateReport(nrr);
    modelMap.addAttribute("report", report);
    modelMap.addAttribute("tenant", getCurrentUser().getTenant());
  }

  @SuppressWarnings("rawtypes")
  private String getReport(String month, String year, String channelId, ModelMap modelMap, Class genericReportKlass,
      Page page, Locale locale) {
    Report report = null;
    HashMap<String, Object> parameters = new HashMap<String, Object>();
    Calendar cal = Calendar.getInstance();
    if (month == null) {
      month = Integer.toString(cal.get(Calendar.MONTH) + 1);
    }
    if (year == null) {
      year = Integer.toString(cal.get(Calendar.YEAR));
    }

    parameters.put("month", month);
    parameters.put("year", year);
    parameters.put("channelId", channelId);
    parameters
        .put("defaultCurrency", messageSource.getMessage(
            "currency.symbol." + config.getValue(Names.com_citrix_cpbm_portal_settings_default_currency), null, locale));
    Object[] args = new Object[] {
        parameters, dataSource, config, locale, messageSource
    };

    Class[] parameterTypes = new Class[] {
        Map.class, DataSource.class, Configuration.class, Locale.class, MessageSource.class
    };
    GenericReport genericReport;
    try {
      genericReport = (GenericReport) ConstructorUtils.invokeConstructor(genericReportKlass, args, parameterTypes);
      report = reportService.generateReport(genericReport);

      setPage(modelMap, page);
      modelMap.addAttribute("report", report);
      modelMap.addAttribute("tenant", getCurrentUser().getTenant());
      addMonthAndYearToMap(modelMap, locale);
    } catch (NoSuchMethodException e) {
      return "failure";
    } catch (IllegalAccessException e) {
      return "failure";
    } catch (InvocationTargetException e) {
      return "failure";
    } catch (InstantiationException e) {
      return "failure";
    }
    return "success";
  }

  private void addMonthAndYearToMap(ModelMap modelMap, Locale locale) {
    Calendar cal = Calendar.getInstance();
    String monthsArr[] = new DateFormatSymbols(locale).getMonths();
    List<String> months = new ArrayList<String>();
    for (String mon : monthsArr) {
      if (mon != null && mon.length() > 0) {
        months.add(mon);
      }
    }
    modelMap.addAttribute("months", months);
    List<String> years = new ArrayList<String>();
    years.add(Integer.toString(cal.get(Calendar.YEAR) - 3));
    years.add(Integer.toString(cal.get(Calendar.YEAR) - 2));
    years.add(Integer.toString(cal.get(Calendar.YEAR) - 1));
    years.add(Integer.toString(cal.get(Calendar.YEAR)));
    modelMap.addAttribute("years", years);
  }

  private void writeToCSV(List<ChannelUsage> lstChannelAggUsage, String filename, HttpServletRequest request) {
    try {
      String channelName = messageSource.getMessage("label.csv.header.channel.name", null,
          this.getSessionLocale(request));
      String channelCode = messageSource.getMessage("label.csv.header.channel.code", null,
          this.getSessionLocale(request));
      String serviceInstance = messageSource.getMessage("label.csv.header.service.instance", null,
          this.getSessionLocale(request));
      String productName = messageSource.getMessage("label.csv.header.product.name", null,
          this.getSessionLocale(request));
      String productCode = messageSource.getMessage("label.csv.header.product.code", null,
          this.getSessionLocale(request));
      String productCategory = messageSource.getMessage("label.csv.header.product.category", null,
          this.getSessionLocale(request));
      String uom = messageSource.getMessage("label.csv.header.product.uom", null, this.getSessionLocale(request));
      String usage = messageSource.getMessage("label.csv.header.product.usage", null, this.getSessionLocale(request));
      String start = messageSource.getMessage("label.csv.header.start.date", null, this.getSessionLocale(request));
      String end = messageSource.getMessage("label.csv.header.end.date", null, this.getSessionLocale(request));

      String header = channelName + csvSeperator + channelCode + csvSeperator + serviceInstance + csvSeperator
          + productName + csvSeperator + productCode + csvSeperator + productCategory + csvSeperator + uom
          + csvSeperator + usage + csvSeperator + start + csvSeperator + end;

      BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename)));
      bw.write(header);
      bw.newLine();
      SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
      for (ChannelUsage channelUsage : lstChannelAggUsage) {
        StringBuilder oneLine = new StringBuilder();
        oneLine.append(quotes + channelUsage.getChannel().getName() + quotes);
        oneLine.append(csvSeperator);
        oneLine.append(quotes + channelUsage.getChannel().getCode() + quotes);
        oneLine.append(csvSeperator);

        Product product = channelUsage.getProduct();
        oneLine.append(product != null ? quotes + product.getServiceInstance().getName() + quotes : blank);
        oneLine.append(csvSeperator);
        oneLine.append(product != null ? quotes + product.getName() + quotes : blank);
        oneLine.append(csvSeperator);
        oneLine.append(product != null ? quotes + product.getCode() + quotes : blank);
        oneLine.append(csvSeperator);
        oneLine.append(product != null ? quotes + product.getCategory().getName() + quotes : blank);
        oneLine.append(csvSeperator);
        oneLine.append(product != null ? quotes + product.getUom() + quotes : blank);

        oneLine.append(csvSeperator);
        oneLine.append(String.format("%.10f", channelUsage.getUsage()));
        oneLine.append(csvSeperator);
        oneLine.append(sdf.format(channelUsage.getStartDate()));
        oneLine.append(csvSeperator);
        oneLine.append(sdf.format(channelUsage.getEndDate()));
        bw.write(oneLine.toString());
        bw.newLine();
      }
      bw.flush();
      bw.close();
    } catch (UnsupportedEncodingException e) {
      logger.error("Unsupported Encoding Exception while writting to CSV", e);
    } catch (FileNotFoundException e) {
      logger.error("File not found Exception while writting to CSV", e);
    } catch (IOException e) {
      logger.error("IOException while writting to CSV", e);
    }
  }
}

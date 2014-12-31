/*
 * Copyright © 2013 Citrix Systems, Inc. You may not use, copy, or modify this file except pursuant to a valid license
 * agreement from Citrix Systems, Inc.
 */
/**
 * @author vinayv
 **/
package fragment.web;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jettison.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import web.WebTestsBaseWithMockConnectors;
import web.support.DispatcherTestServlet;

import com.citrix.cpbm.portal.fragment.controllers.ReportController;
import com.vmops.model.BillingGroup;
import com.vmops.model.Channel;
import com.vmops.model.Report;
import com.vmops.service.ChannelService;
import com.vmops.web.forms.ChannelUsageReportForm;
import com.vmops.web.forms.CustomerRankReportForm;
import com.vmops.web.forms.NewRegistrationReportForm;

public class AbstractReportControllerTest extends WebTestsBaseWithMockConnectors {

  @Autowired
  private ReportController reportController;

  private ModelMap map;

  private MockHttpServletResponse response;

  private MockHttpServletRequest request;

  @Autowired
  private ChannelService channelService;

  @Before
  public void init() throws Exception {

    map = new ModelMap();
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
  }

  @Test
  public void testCustomReportForDateWithData() throws Exception {

    String result = reportController.generateCustomReports("1", "05/01/2012", null, null, request, response, map);
    Assert.assertNotNull(result);
    Assert.assertTrue(result.contains("testReport"));
  }

  @Test
  public void testCustomReportForDateWithoutData() throws Exception {

    String result = reportController.generateCustomReports("1", "05/03/2012", null, null, request, response, map);
    Assert.assertNotNull(result);
    Assert.assertTrue(result.contains("none"));
  }

  @Test
  public void testCustomReportWithInvalidDate() {

    try {
      reportController.generateCustomReports("1", "05-03-2012", null, null, request, response, map);
    } catch (Exception e) {
      Assert.assertTrue(e.getMessage().contains("Unparseable date"));
    }
  }

  @Test
  public void testCustomReportWithInvalidId() {

    try {
      reportController.generateCustomReports("2", "05-03-2012", null, null, request, response, map);
    } catch (Exception e) {
      Assert.assertNull(e.getMessage());
    }
  }

  @Test
  public void testEmailCustomReport() throws Exception {

    String reportFilename = reportController.generateCustomReports("1", "05/01/2012", null, null, request, response,
        map);
    Assert.assertNotNull(reportFilename);
    Assert.assertTrue(reportFilename.contains("testReport"));
    String result = reportController.emailCustomReport(reportFilename, "vinay,vegesna@test.com", map, response);
    Assert.assertNotNull(result);
    Assert.assertEquals("success", result);
  }

  @Test
  public void testDownloadCustomReport() throws Exception {

    String reportFilename = reportController.generateCustomReports("1", "05/01/2012", null, null, request, response,
        map);
    Assert.assertNotNull(reportFilename);
    Assert.assertTrue(reportFilename.contains("testReport"));
    reportController.downloadCustomReport(reportFilename, map, response);
  }

  @Test
  public void testCustomReportGet() throws Exception {

    request.setParameter("lang", Locale.getDefault().toString());
    String result = reportController.customReports(map, request);
    Assert.assertNotNull(result);
    Assert.assertEquals("report.customReports", result);
  }

  @SuppressWarnings("deprecation")
  @Test
  public void testGenerateCSV() throws Exception {
    NewRegistrationReportForm form = new NewRegistrationReportForm();
    form.setStartDate("04/30/2012");
    form.setEndDate("05/01/2012");
    BindingResult result = validate(form);
    request.setParameter("lang", Locale.getDefault().toString());
    String resultString = reportController.newRegistrations(map, request, form, result);
    Assert.assertNotNull(resultString);
    Assert.assertEquals("report.newRegistration", resultString);
    Report report = (Report) map.get("report");
    DateFormat format = new SimpleDateFormat("dd MMM, yyyy");
    String start = format.format(new Date("04/30/2012"));
    String end = format.format(new Date("05/01/2012"));
    Assert.assertEquals("New Registrations " + start + " - " + end, report.getTitle());
    String reportData = report.getData();
    reportController.generateCSV(reportData, map, response);
  }

  /**
   * Author: vinayv Description: Test to generate and validate New Registration Report.
   */
  @SuppressWarnings("deprecation")
  @Test
  public void testNewRegistrations() throws Exception {

    int count = 0;
    NewRegistrationReportForm form = new NewRegistrationReportForm();
    form.setStartDate("04/30/2012");
    form.setEndDate("05/01/2012");
    BindingResult result = validate(form);
    request.setParameter("lang", Locale.getDefault().toString());
    String resultString = reportController.newRegistrations(map, request, form, result);
    Assert.assertNotNull(resultString);
    Assert.assertEquals("report.newRegistration", resultString);
    Report report = (Report) map.get("report");
    DateFormat format = new SimpleDateFormat("dd MMM, yyyy");
    String start = format.format(new Date("04/30/2012"));
    String end = format.format(new Date("05/01/2012"));
    Assert.assertEquals("New Registrations " + start + " - " + end, report.getTitle());
    String reportData = report.getData().replace("\\", "");

    JSONObject jsonObj = new JSONObject(reportData);
    org.codehaus.jettison.json.JSONArray jsonArray = (org.codehaus.jettison.json.JSONArray) jsonObj.get("series");
    for (int i = 0; i < jsonArray.length(); i++) {
      JSONObject jsonObject = jsonArray.getJSONObject(i);
      org.codehaus.jettison.json.JSONArray jsonArr = jsonObject.getJSONArray("data");
      count = count + jsonArr.getInt(0);
    }
    Assert.assertEquals(15, count);
  }

  /**
   * Author: vinayv Description: Test to generate and validate New Registration Report with Null Start and End Dates.
   */
  @Test
  public void testNewRegistrationsWithNullDates() throws Exception {

    int count = 0;
    NewRegistrationReportForm form = new NewRegistrationReportForm();
    form.setStartDate(null);
    form.setEndDate(null);
    BindingResult result = validate(form);
    request.setParameter("lang", Locale.getDefault().toString());
    String resultString = reportController.newRegistrations(map, request, form, result);
    Assert.assertNotNull(resultString);
    Assert.assertEquals("report.newRegistration", resultString);
    Report report = (Report) map.get("report");
    Assert.assertTrue(report.getTitle().contains("New Registrations"));
    Calendar cal = Calendar.getInstance();
    DateFormat format = new SimpleDateFormat("dd MMM, yyyy");
    cal.set(Calendar.DATE, 1);
    String start = format.format(cal.getTime());
    cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
    String end = format.format(cal.getTime());
    Assert.assertEquals("New Registrations " + start + " - " + end, report.getTitle());
    String reportData = report.getData().replace("\\", "");

    JSONObject jsonObj = new JSONObject(reportData);
    org.codehaus.jettison.json.JSONArray jsonArray = (org.codehaus.jettison.json.JSONArray) jsonObj.get("series");
    for (int i = 0; i < jsonArray.length(); i++) {
      JSONObject jsonObject = jsonArray.getJSONObject(i);
      org.codehaus.jettison.json.JSONArray jsonArr = jsonObject.getJSONArray("data");
      if (jsonArr.length() > 0) {
        count = count + jsonArr.getInt(0);
      }
    }
    Assert.assertEquals(0, count);
  }

  /**
   * Author: vinayv Description: Test to generate and validate New Registration Report with Invalid Start Date.
   */
  @Test
  public void testNewRegistrationsWithInvalidStartDate() throws Exception {

    NewRegistrationReportForm form = new NewRegistrationReportForm();
    form.setStartDate("start");
    form.setEndDate("05/01/13");
    BindingResult result = validate(form);
    request.setParameter("lang", Locale.getDefault().toString());
    String resultString = reportController.newRegistrations(map, request, form, result);
    Assert.assertNotNull(resultString);
    Assert.assertEquals("report.newRegistration", resultString);
    Report report = (Report) map.get("report");
    Assert.assertNull(report);
  }

  /**
   * Author: vinayv Description: Test to generate and validate New Registration Report with Invalid End Date.
   */
  @Test
  public void testNewRegistrationsWithInvalidEndDate() throws Exception {

    NewRegistrationReportForm form = new NewRegistrationReportForm();
    form.setStartDate("04/30/2013");
    form.setEndDate("endDate");
    BindingResult result = validate(form);
    request.setParameter("lang", Locale.getDefault().toString());
    String resultString = reportController.newRegistrations(map, request, form, result);
    Assert.assertNotNull(resultString);
    Assert.assertEquals("report.newRegistration", resultString);
    Report report = (Report) map.get("report");
    Assert.assertNull(report);
  }

  /**
   * Author: vinayv Description: Test to generate and validate New Registration Report using GET.
   */
  @SuppressWarnings("deprecation")
  @Test
  public void testNewRegistrationsGet() throws Exception {

    int count = 0;
    request.setParameter("lang", Locale.getDefault().toString());
    String resultString = reportController.newRegistrations("04/30/2012", "05/01/2012", map, request);
    Assert.assertNotNull(resultString);
    Assert.assertEquals("report.newRegistration", resultString);
    Report report = (Report) map.get("report");
    DateFormat format = new SimpleDateFormat("dd MMM, yyyy");
    String start = format.format(new Date("04/30/2012"));
    String end = format.format(new Date("05/01/2012"));
    Assert.assertEquals("New Registrations " + start + " - " + end, report.getTitle());
    String reportData = report.getData().replace("\\", "");

    JSONObject jsonObj = new JSONObject(reportData);
    org.codehaus.jettison.json.JSONArray jsonArray = (org.codehaus.jettison.json.JSONArray) jsonObj.get("series");
    for (int i = 0; i < jsonArray.length(); i++) {
      JSONObject jsonObject = jsonArray.getJSONObject(i);
      org.codehaus.jettison.json.JSONArray jsonArr = jsonObject.getJSONArray("data");
      count = count + jsonArr.getInt(0);
    }
    Assert.assertEquals(15, count);
  }

  /**
   * Author: vinayv Description: Test to generate and validate New Registration Report with Null Start and End Dates
   * using GET.
   */
  @Test
  public void testNewRegistrationsWithNullDatesGet() throws Exception {

    int count = 0;
    request.setParameter("lang", Locale.getDefault().toString());
    String resultString = reportController.newRegistrations(null, null, map, request);
    Assert.assertNotNull(resultString);
    Assert.assertEquals("report.newRegistration", resultString);
    Report report = (Report) map.get("report");
    Assert.assertTrue(report.getTitle().contains("New Registrations"));
    Calendar cal = Calendar.getInstance();
    DateFormat format = new SimpleDateFormat("dd MMM, yyyy");
    cal.set(Calendar.DATE, 1);
    String start = format.format(cal.getTime());
    cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
    String end = format.format(cal.getTime());
    Assert.assertEquals("New Registrations " + start + " - " + end, report.getTitle());
    String reportData = report.getData().replace("\\", "");

    JSONObject jsonObj = new JSONObject(reportData);
    org.codehaus.jettison.json.JSONArray jsonArray = (org.codehaus.jettison.json.JSONArray) jsonObj.get("series");
    for (int i = 0; i < jsonArray.length(); i++) {
      JSONObject jsonObject = jsonArray.getJSONObject(i);
      org.codehaus.jettison.json.JSONArray jsonArr = jsonObject.getJSONArray("data");
      if (jsonArr.length() > 0) {
        count = count + jsonArr.getInt(0);
      }
    }
    Assert.assertEquals(0, count);
  }

  /**
   * Author: vinayv Description: Test to generate and validate New Registration Report with Invalid Start Date using
   * GET.
   */
  @Test
  public void testNewRegistrationsWithInvalidStartDateGet() throws Exception {

    int count = 0;
    request.setParameter("lang", Locale.getDefault().toString());
    String resultString = reportController.newRegistrations("startDate", null, map, request);
    Assert.assertNotNull(resultString);
    Assert.assertEquals("report.newRegistration", resultString);
    Report report = (Report) map.get("report");
    String reportData = report.getData().replace("\\", "");
    JSONObject jsonObj = new JSONObject(reportData);
    org.codehaus.jettison.json.JSONArray jsonArray = (org.codehaus.jettison.json.JSONArray) jsonObj.get("series");
    for (int i = 0; i < jsonArray.length(); i++) {
      JSONObject jsonObject = jsonArray.getJSONObject(i);
      org.codehaus.jettison.json.JSONArray jsonArr = jsonObject.getJSONArray("data");
      if (jsonArr.length() > 0) {
        count = count + jsonArr.getInt(0);
      }
    }
    Assert.assertEquals(0, count);
  }

  /**
   * Author: vinayv Description: Test to generate and validate New Registration Report with Invalid End Date using GET.
   */
  @Test
  public void testNewRegistrationsWithInvalidEndDateGET() throws Exception {

    int count = 0;
    request.setParameter("lang", Locale.getDefault().toString());
    String resultString = reportController.newRegistrations(null, "endDate", map, request);
    Assert.assertNotNull(resultString);
    Assert.assertEquals("report.newRegistration", resultString);
    Report report = (Report) map.get("report");
    String reportData = report.getData().replace("\\", "");
    JSONObject jsonObj = new JSONObject(reportData);
    org.codehaus.jettison.json.JSONArray jsonArray = (org.codehaus.jettison.json.JSONArray) jsonObj.get("series");
    for (int i = 0; i < jsonArray.length(); i++) {
      JSONObject jsonObject = jsonArray.getJSONObject(i);
      org.codehaus.jettison.json.JSONArray jsonArr = jsonObject.getJSONArray("data");
      if (jsonArr.length() > 0) {
        count = count + jsonArr.getInt(0);
      }
    }
    Assert.assertEquals(0, count);
  }

  /**
   * Author: vinayv Description: Test to generate and validate Customers Rank Report with valid Month and Year.
   */
  @SuppressWarnings("deprecation")
  @Test
  public void testCustomersRank() throws Exception {

    request.setParameter("lang", Locale.getDefault().toString());
    CustomerRankReportForm form = new CustomerRankReportForm();
    form.setReportMonth("7");
    form.setReportYear("2013");
    BindingResult result = validate(form);
    String resultString = reportController.customersRank(map, request, form, result);
    Assert.assertNotNull(resultString);
    Assert.assertEquals("report.customerRank", resultString);
    Report report = (Report) map.get("report");
    DateFormat format = new SimpleDateFormat("MMM yyyy");
    String date = format.format(new Date("07/01/2013"));
    Assert.assertEquals("Top Customers, " + date, report.getTitle());
    String reportData = report.getData().replace("\\", "");
    JSONObject jsonObj = new JSONObject(reportData);
    org.codehaus.jettison.json.JSONArray jsonArray = (org.codehaus.jettison.json.JSONArray) jsonObj.get("series");
    for (int i = 0; i < jsonArray.length(); i++) {
      JSONObject jsonObject = jsonArray.getJSONObject(i);
      org.codehaus.jettison.json.JSONArray jsonArr = jsonObject.getJSONArray("data");
      Assert.assertEquals(500, jsonArr.getInt(0));
    }
  }

  /**
   * Author: vinayv Description: Test to generate and validate Customers Rank Report with Null Month and Year.
   */
  @Test
  public void testCustomersRankWithNullMonthAndYear() throws Exception {

    request.setParameter("lang", Locale.getDefault().toString());
    CustomerRankReportForm form = new CustomerRankReportForm();
    form.setReportMonth(null);
    form.setReportYear(null);
    BindingResult result = validate(form);
    String resultString = reportController.customersRank(map, request, form, result);
    Assert.assertNotNull(resultString);
    Assert.assertEquals("report.customerRank", resultString);
    Report report = (Report) map.get("report");
    Assert.assertNull(report);
  }

  /**
   * Author: vinayv Description: Test to generate and validate Customers Rank Report with Invalid Month.
   */
  @Test
  public void testCustomersRankWithInvalidMonth() throws Exception {

    map.clear();
    request.setParameter("lang", Locale.getDefault().toString());
    CustomerRankReportForm form = new CustomerRankReportForm();
    form.setReportMonth("month");
    form.setReportYear("2013");
    BindingResult result = validate(form);
    String resultString = reportController.customersRank(map, request, form, result);
    Assert.assertNotNull(resultString);
    Assert.assertEquals("report.customerRank", resultString);
    Report report = (Report) map.get("report");
    Assert.assertNull(report);
  }

  /**
   * Author: vinayv Description: Test to generate and validate Customers Rank Report with Invalid Year.
   */
  @Test
  public void testCustomersRankWithInvalidYear() throws Exception {

    map.clear();
    request.setParameter("lang", Locale.getDefault().toString());
    CustomerRankReportForm form = new CustomerRankReportForm();
    form.setReportMonth("7");
    form.setReportYear("year");
    BindingResult result = validate(form);
    String resultString = reportController.customersRank(map, request, form, result);
    Assert.assertNotNull(resultString);
    Assert.assertEquals("report.customerRank", resultString);
    Report report = (Report) map.get("report");
    Assert.assertNull(report);
  }

  /**
   * Author: vinayv Description: Test to generate and validate Customers Rank Report with valid Month and Year using
   * GET.
   */
  @SuppressWarnings("deprecation")
  @Test
  public void testCustomersRankGet() throws Exception {

    request.setParameter("lang", Locale.getDefault().toString());
    String resultString = reportController.customersRank("7", "2013", map, request);
    Assert.assertNotNull(resultString);
    Assert.assertEquals("report.customerRank", resultString);
    Report report = (Report) map.get("report");
    DateFormat format = new SimpleDateFormat("MMM yyyy");
    String date = format.format(new Date("07/01/2013"));
    Assert.assertEquals("Top Customers, " + date, report.getTitle());
    String reportData = report.getData().replace("\\", "");
    JSONObject jsonObj = new JSONObject(reportData);
    org.codehaus.jettison.json.JSONArray jsonArray = (org.codehaus.jettison.json.JSONArray) jsonObj.get("series");
    for (int i = 0; i < jsonArray.length(); i++) {
      JSONObject jsonObject = jsonArray.getJSONObject(i);
      org.codehaus.jettison.json.JSONArray jsonArr = jsonObject.getJSONArray("data");
      Assert.assertEquals(500, jsonArr.getInt(0));
    }
  }

  /**
   * Author: vinayv Description: Test to generate and validate Customers Rank Report with Null Month and Year using GET.
   */
  @Test
  public void testCustomersRankWithNullMonthAndYearGet() throws Exception {

    request.setParameter("lang", Locale.getDefault().toString());
    String resultString = reportController.customersRank(null, null, map, request);
    Assert.assertNotNull(resultString);
    Assert.assertEquals("report.customerRank", resultString);
    Report report = (Report) map.get("report");
    Calendar cal = Calendar.getInstance();
    String month = Integer.toString(cal.get(Calendar.MONTH) + 1);
    String year = Integer.toString(cal.get(Calendar.YEAR));
    Map<String, Object> reportParams = report.getParams();
    String monthParam = (String) reportParams.get("month");
    String yearParam = (String) reportParams.get("year");
    Assert.assertEquals(month, monthParam);
    Assert.assertEquals(year, yearParam);
  }

  /**
   * Author: vinayv Description: Test to generate and validate Customers Rank Report with Invalid Month using GET.
   */
  @Test
  public void testCustomersRankWithInvalidMonthGet() throws Exception {
    try {
      request.setParameter("lang", Locale.getDefault().toString());
      reportController.customersRank("month", "2013", map, request);
    } catch (Exception e) {
      Assert.assertEquals("For input string: \"month\"", e.getMessage());
    }
  }

  /**
   * Author: vinayv Description: Test to generate and validate Customers Rank Report with Invalid Year using GET.
   */
  @Test
  public void testCustomersRankWithInvalidYearGet() throws Exception {
    try {
      request.setParameter("lang", Locale.getDefault().toString());
      reportController.customersRank("7", "year", map, request);
    } catch (Exception e) {
      Assert.assertEquals("For input string: \"year\"", e.getMessage());
    }
  }

  /**
   * Author: vinayv Description: Test to generate and validate Product Usage Report with valid Month and Year.
   */
  @Test
  public void testProductUsageReport() throws Exception {

    request.setParameter("lang", Locale.getDefault().toString());
    CustomerRankReportForm form = new CustomerRankReportForm();
    form.setReportMonth("1");
    form.setReportYear("2013");
    BindingResult result = validate(form);
    String resultString = reportController.productUsage(map, request, form, result);
    Assert.assertNotNull(resultString);
    Assert.assertEquals("report.usageSpend", resultString);
    String reportName = (String) map.get("usageReportName");
    Assert.assertEquals("productUsage", reportName);
    Report report = (Report) map.get("report");
    Assert.assertEquals("Monthly Usage by Product for January 2013", report.getTitle());
    String reportData = report.getData().replace("\\", "");
    JSONObject jsonObj = new JSONObject(reportData);
    org.codehaus.jettison.json.JSONArray jsonArray = (org.codehaus.jettison.json.JSONArray) jsonObj.get("usageMap");
    for (int i = 0; i < jsonArray.length(); i++) {
      JSONObject jsonObject = jsonArray.getJSONObject(i);
      String data = (String) jsonObject.get("data");
      String prodName = (String) jsonObject.get("productname");
      if (prodName.equalsIgnoreCase("largerunningvm1")) {
        Assert.assertEquals("150.00000000000000000000", data);
      }
    }
  }

  /**
   * Author: vinayv Description: Test to generate and validate Product Usage Report with Null Month and Year.
   */
  @Test
  public void testProductUsageReportWithNullMonthAndYear() throws Exception {

    request.setParameter("lang", Locale.getDefault().toString());
    CustomerRankReportForm form = new CustomerRankReportForm();
    form.setReportMonth(null);
    form.setReportYear(null);
    BindingResult result = validate(form);
    String resultString = reportController.productUsage(map, request, form, result);
    Assert.assertNotNull(resultString);
    Assert.assertEquals("report.usageSpend", resultString);
    Report report = (Report) map.get("report");
    Assert.assertNull(report);
  }

  /**
   * Author: vinayv Description: Test to generate and validate Product Usage Report with Invalid Month.
   */
  @Test
  public void testProductUsageReportWithInvalidMonth() throws Exception {

    request.setParameter("lang", Locale.getDefault().toString());
    CustomerRankReportForm form = new CustomerRankReportForm();
    form.setReportMonth("month");
    form.setReportYear("2013");
    BindingResult result = validate(form);
    String resultString = reportController.productUsage(map, request, form, result);
    Assert.assertNotNull(resultString);
    Assert.assertEquals("report.usageSpend", resultString);
    String reportName = (String) map.get("usageReportName");
    Assert.assertEquals(null, reportName);
    Report report = (Report) map.get("report");
    Assert.assertNull(report);
  }

  /**
   * Author: vinayv Description: Test to generate and validate Product Usage Report with Invalid Year.
   */
  @Test
  public void testProductUsageReportWithInvalidYear() throws Exception {

    request.setParameter("lang", Locale.getDefault().toString());
    CustomerRankReportForm form = new CustomerRankReportForm();
    form.setReportMonth("1");
    form.setReportYear("year");
    BindingResult result = validate(form);
    String resultString = reportController.productUsage(map, request, form, result);
    Assert.assertNotNull(resultString);
    Assert.assertEquals("report.usageSpend", resultString);
    String reportName = (String) map.get("usageReportName");
    Assert.assertEquals(null, reportName);
    Report report = (Report) map.get("report");
    Assert.assertNull(report);
  }

  /**
   * Author: vinayv Description: Test to generate and validate Product Usage Report with valid Month and Year using GET.
   */
  @Test
  public void testProductUsageReportGet() throws Exception {

    request.setParameter("lang", Locale.getDefault().toString());
    String resultString = reportController.productUsage("1", "2013", map, request);
    Assert.assertNotNull(resultString);
    Assert.assertEquals("report.usageSpend", resultString);
    String reportName = (String) map.get("usageReportName");
    Assert.assertEquals("productUsage", reportName);
    Report report = (Report) map.get("report");
    Assert.assertEquals("Monthly Usage by Product for January 2013", report.getTitle());
    String reportData = report.getData().replace("\\", "");
    JSONObject jsonObj = new JSONObject(reportData);
    org.codehaus.jettison.json.JSONArray jsonArray = (org.codehaus.jettison.json.JSONArray) jsonObj.get("usageMap");
    for (int i = 0; i < jsonArray.length(); i++) {
      JSONObject jsonObject = jsonArray.getJSONObject(i);
      String data = (String) jsonObject.get("data");
      String prodName = (String) jsonObject.get("productname");
      if (prodName.equalsIgnoreCase("largerunningvm1")) {
        Assert.assertEquals("150.00000000000000000000", data);
      }
    }
  }

  /**
   * Author: vinayv Description: Test to generate and validate Product Usage Report with Null Month and Year using GET.
   */
  @Test
  public void testProductUsageReportWithNullMonthAndYearGet() throws Exception {

    request.setParameter("lang", Locale.getDefault().toString());
    String resultString = reportController.productUsage(null, null, map, request);
    Assert.assertNotNull(resultString);
    Assert.assertEquals("report.usageSpend", resultString);
    String reportName = (String) map.get("usageReportName");
    Assert.assertEquals("productUsage", reportName);
    Report report = (Report) map.get("report");
    Assert.assertTrue(report.getTitle().contains("Monthly Usage by Product"));
    Calendar cal = Calendar.getInstance();
    String month = Integer.toString(cal.get(Calendar.MONTH) + 1);
    String year = Integer.toString(cal.get(Calendar.YEAR));
    Map<String, Object> reportParams = report.getParams();
    String monthParam = (String) reportParams.get("month");
    String yearParam = (String) reportParams.get("year");
    Assert.assertEquals(month, monthParam);
    Assert.assertEquals(year, yearParam);
  }

  /**
   * Author: vinayv Description: Test to generate and validate Product Usage Report with Invalid Month using GET.
   */
  @Test
  public void testProductUsageReportWithInvalidMonthGet() throws Exception {
    try {
      request.setParameter("lang", Locale.getDefault().toString());
      reportController.productUsage("month", "2013", map, request);
    } catch (Exception e) {
      Assert.assertEquals("For input string: \"month\"", e.getMessage());
    }
  }

  /**
   * Author: vinayv Description: Test to generate and validate Product Usage Report with Invalid Year using GET.
   */
  @Test
  public void testProductUsageReportWithInvalidYearGet() throws Exception {
    try {
      request.setParameter("lang", Locale.getDefault().toString());
      reportController.productUsage("1", "year", map, request);
    } catch (Exception e) {
      Assert.assertEquals("For input string: \"year\"", e.getMessage());
    }
  }

  /**
   * Author: vinayv Description: Test to generate and validate Product Bundle Usage Report with valid Month and Year.
   */
  @Test
  public void testProductBundleUsageReport() throws Exception {

    request.setParameter("lang", Locale.getDefault().toString());
    CustomerRankReportForm form = new CustomerRankReportForm();
    form.setReportMonth("1");
    form.setReportYear("2013");
    BindingResult result = validate(form);
    String resultString = reportController.productBundleUsage(map, request, form, result);
    Assert.assertNotNull(resultString);
    Assert.assertEquals("report.usageSpend", resultString);
    String reportName = (String) map.get("usageReportName");
    Assert.assertEquals("productbundleUsage", reportName);
    Report report = (Report) map.get("report");
    Assert.assertEquals("Monthly Usage by Product Bundle for January 2013", report.getTitle());
    String reportData = report.getData().replace("\\", "");
    JSONObject jsonObj = new JSONObject(reportData);
    org.codehaus.jettison.json.JSONArray jsonArray = (org.codehaus.jettison.json.JSONArray) jsonObj.get("usageMap");
    for (int i = 0; i < jsonArray.length(); i++) {
      JSONObject jsonObject = jsonArray.getJSONObject(i);
      String data = (String) jsonObject.get("data");
      String prodName = (String) jsonObject.get("productname");
      if (prodName.equalsIgnoreCase("compute_1")) {
        Assert.assertEquals("0.00000000000000", data);
      }
    }
  }

  /**
   * Author: vinayv Description: Test to generate and validate Product Bundle Usage Report with Null Month and Year.
   */
  @Test
  public void testProductBundleUsageReportWithNullMonthAndYear() throws Exception {

    request.setParameter("lang", Locale.getDefault().toString());
    CustomerRankReportForm form = new CustomerRankReportForm();
    form.setReportMonth(null);
    form.setReportYear(null);
    BindingResult result = validate(form);
    String resultString = reportController.productBundleUsage(map, request, form, result);
    Assert.assertNotNull(resultString);
    Assert.assertEquals("report.usageSpend", resultString);
    Report report = (Report) map.get("report");
    Assert.assertNull(report);
  }

  /**
   * Author: vinayv Description: Test to generate and validate Product Bundle Usage Report with Invalid Month.
   */
  @Test
  public void testProductBundleUsageReportWithInvalidMonth() throws Exception {

    request.setParameter("lang", Locale.getDefault().toString());
    CustomerRankReportForm form = new CustomerRankReportForm();
    form.setReportMonth("month");
    form.setReportYear("2013");
    BindingResult result = validate(form);
    String resultString = reportController.productBundleUsage(map, request, form, result);
    Assert.assertNotNull(resultString);
    Assert.assertEquals("report.usageSpend", resultString);
    String reportName = (String) map.get("usageReportName");
    Assert.assertEquals(null, reportName);
    Report report = (Report) map.get("report");
    Assert.assertNull(report);
  }

  /**
   * Author: vinayv Description: Test to generate and validate Product Bundle Usage Report with Invalid Year.
   */
  @Test
  public void testProductBundleUsageReportWithInvalidYear() throws Exception {

    request.setParameter("lang", Locale.getDefault().toString());
    CustomerRankReportForm form = new CustomerRankReportForm();
    form.setReportMonth("1");
    form.setReportYear("year");
    BindingResult result = validate(form);
    String resultString = reportController.productBundleUsage(map, request, form, result);
    Assert.assertNotNull(resultString);
    Assert.assertEquals("report.usageSpend", resultString);
    String reportName = (String) map.get("usageReportName");
    Assert.assertEquals(null, reportName);
    Report report = (Report) map.get("report");
    Assert.assertNull(report);
  }

  /**
   * Author: vinayv Description: Test to generate and validate Product Bundle Usage Report with valid Month and Year
   * using GET.
   */
  @Test
  public void testProductBundleUsageReportGet() throws Exception {

    request.setParameter("lang", Locale.getDefault().toString());
    String resultString = reportController.productBundleUsage("1", "2013", map, request);
    Assert.assertNotNull(resultString);
    Assert.assertEquals("report.usageSpend", resultString);
    String reportName = (String) map.get("usageReportName");
    Assert.assertEquals("productbundleUsage", reportName);
    Report report = (Report) map.get("report");
    Assert.assertEquals("Monthly Usage by Product Bundle for January 2013", report.getTitle());
    String reportData = report.getData().replace("\\", "");
    JSONObject jsonObj = new JSONObject(reportData);
    org.codehaus.jettison.json.JSONArray jsonArray = (org.codehaus.jettison.json.JSONArray) jsonObj.get("usageMap");
    for (int i = 0; i < jsonArray.length(); i++) {
      JSONObject jsonObject = jsonArray.getJSONObject(i);
      String data = (String) jsonObject.get("data");
      String prodName = (String) jsonObject.get("productname");
      if (prodName.equalsIgnoreCase("compute_1")) {
        Assert.assertEquals("0.00000000000000", data);
      }
    }
  }

  /**
   * Author: vinayv Description: Test to generate and validate Product Bundle Usage Report with Null Month and Year
   * using GET.
   */
  @Test
  public void testProductBundleUsageReportWithNullMonthAndYearGet() throws Exception {

    request.setParameter("lang", Locale.getDefault().toString());
    String resultString = reportController.productBundleUsage(null, null, map, request);
    Assert.assertNotNull(resultString);
    Assert.assertEquals("report.usageSpend", resultString);
    Report report = (Report) map.get("report");
    Calendar cal = Calendar.getInstance();
    String month = Integer.toString(cal.get(Calendar.MONTH) + 1);
    String year = Integer.toString(cal.get(Calendar.YEAR));
    Map<String, Object> reportParams = report.getParams();
    String monthParam = (String) reportParams.get("month");
    String yearParam = (String) reportParams.get("year");
    Assert.assertEquals(month, monthParam);
    Assert.assertEquals(year, yearParam);
  }

  /**
   * Author: vinayv Description: Test to generate and validate Product Bundle Usage Report with Invalid Month using GET.
   */
  @Test
  public void testProductBundleUsageReportWithInvalidMonthGet() throws Exception {
    try {
      request.setParameter("lang", Locale.getDefault().toString());
      reportController.productBundleUsage("month", "2013", map, request);
    } catch (Exception e) {
      Assert.assertEquals("For input string: \"month\"", e.getMessage());
    }
  }

  /**
   * Author: vinayv Description: Test to generate and validate Product Bundle Usage Report with Invalid Year using GET.
   */
  @Test
  public void testProductBundleUsageReportWithInvalidYearGet() throws Exception {
    try {
      request.setParameter("lang", Locale.getDefault().toString());
      reportController.productBundleUsage("1", "year", map, request);
    } catch (Exception e) {
      Assert.assertEquals("For input string: \"year\"", e.getMessage());
    }
  }

  @Test
  public void testRouting() throws Exception {
    logger.debug("Testing routing....");
    DispatcherTestServlet servlet = this.getServletInstance();
    Class<? extends ReportController> controllerClass = reportController.getClass();
    Method expected = locateMethod(controllerClass, "channelUsage", new Class[] {
        ModelMap.class, HttpServletRequest.class
    });
    Method handler = servlet.recognize(getRequestTemplate(HttpMethod.GET, "/reports/channel_usage"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controllerClass, "channelUsage", new Class[] {
        ModelMap.class, HttpServletRequest.class, ChannelUsageReportForm.class, BindingResult.class,
        HttpServletResponse.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.POST, "/reports/channel_usage"));
    Assert.assertEquals(expected, handler);
  }

  @Test
  public void testChannelUsageGet() {
    String result = reportController.channelUsage(map, request);
    Assert.assertEquals("report.channelUsage", result);

    List<Channel> channels = channelService.getChannels(null, null, "", null);
    Assert.assertNotNull(channels);
    Assert.assertEquals(channels, (List<Channel>) map.get("channels"));

    Collection<BillingGroup> billingGroups = channelService.getBillingGroups();
    Assert.assertEquals(billingGroups, (Collection<Channel>) map.get("billingGroups"));
  }

  /**
   * Author: Abhaik
   */
  @Test
  public void testChannelUsagePost() throws Exception {
    ChannelUsageReportForm channelUsageReportForm = new ChannelUsageReportForm();
    channelUsageReportForm.setChannelId("3");
    channelUsageReportForm.setEndDate("02/01/2013");
    channelUsageReportForm.setStartDate("01/01/2013");

    BindingResult bindingResult = validate(channelUsageReportForm);
    Assert.assertFalse(bindingResult.hasErrors());
    String result = reportController.channelUsage(map, request, channelUsageReportForm, bindingResult, response);
    Assert.assertEquals("report.channelUsage", result);

    Assert.assertEquals("text/csv", response.getContentType());
    List<Channel> channels = channelService.getChannels(null, null, "", null);
    Assert.assertEquals(channels, (List<Channel>) map.get("channels"));
    Assert.assertEquals(channelUsageReportForm, (ChannelUsageReportForm) map.get("channelUsageReportForm"));

    Assert.assertEquals("channelUsage", map.get("usageReportName"));

    Assert.assertEquals(false, map.get("noRecordsFound"));

  }

  /**
   * Author: Abhaik
   */
  @Test
  public void testBillingGroupUsagePost() throws Exception {
    ChannelUsageReportForm channelUsageReportForm = new ChannelUsageReportForm();
    channelUsageReportForm.setBillingGroupId("1");
    channelUsageReportForm.setEndDate("02/01/2013");
    channelUsageReportForm.setStartDate("01/01/2013");

    BindingResult bindingResult = validate(channelUsageReportForm);
    Assert.assertFalse(bindingResult.hasErrors());
    String result = reportController.channelUsage(map, request, channelUsageReportForm, bindingResult, response);
    Assert.assertEquals("report.channelUsage", result);

    Assert.assertEquals("text/csv", response.getContentType());
    List<Channel> channels = channelService.getChannels(null, null, "", null);
    Assert.assertEquals(channels, (List<Channel>) map.get("channels"));
    Assert.assertEquals(channelUsageReportForm, (ChannelUsageReportForm) map.get("channelUsageReportForm"));

    Assert.assertEquals("billingGroupUsage", map.get("usageReportName"));

    Assert.assertEquals(false, map.get("noRecordsFound"));

  }

  /**
   * Author: Abhaik
   */
  @Test
  public void testChannelUsageWithNoRecords() throws Exception {
    ChannelUsageReportForm channelUsageReportForm = new ChannelUsageReportForm();
    channelUsageReportForm.setChannelId("1");
    channelUsageReportForm.setEndDate("13/5/3000");
    channelUsageReportForm.setStartDate("12/5/3000");

    BindingResult bindingResult = validate(channelUsageReportForm);
    Assert.assertFalse(bindingResult.hasErrors());
    String result = reportController.channelUsage(map, request, channelUsageReportForm, bindingResult, response);
    Assert.assertEquals("report.channelUsage", result);

    Assert.assertEquals("text/csv", response.getContentType());
    List<Channel> channels = channelService.getChannels(null, null, "", null);
    Assert.assertEquals(channels, (List<Channel>) map.get("channels"));

    Assert.assertEquals("channelUsage", map.get("usageReportName"));

    Assert.assertEquals(true, map.get("noRecordsFound"));

  }

  /**
   * Author: Abhaik
   */
  @Test
  public void testBillingGroupUsageWithNoRecords() throws Exception {
    ChannelUsageReportForm channelUsageReportForm = new ChannelUsageReportForm();
    channelUsageReportForm.setBillingGroupId("1");
    channelUsageReportForm.setEndDate("13/5/3000");
    channelUsageReportForm.setStartDate("12/5/3000");

    BindingResult bindingResult = validate(channelUsageReportForm);
    Assert.assertFalse(bindingResult.hasErrors());
    String result = reportController.channelUsage(map, request, channelUsageReportForm, bindingResult, response);
    Assert.assertEquals("report.channelUsage", result);

    Assert.assertEquals("text/csv", response.getContentType());
    List<Channel> channels = channelService.getChannels(null, null, "", null);
    Assert.assertEquals(channels, (List<Channel>) map.get("channels"));

    Assert.assertEquals("billingGroupUsage", map.get("usageReportName"));

    Assert.assertEquals(true, map.get("noRecordsFound"));

  }

  /**
   * Author: Abhaik
   */
  @Test
  public void testChannelUsageWithEndDatePrecedsStartDate() throws Exception {
    ChannelUsageReportForm channelUsageReportForm = new ChannelUsageReportForm();
    channelUsageReportForm.setChannelId("1");
    channelUsageReportForm.setEndDate("10/5/2014");
    channelUsageReportForm.setStartDate("30/5/2014");

    BindingResult bindingResult = validate(channelUsageReportForm);
    Assert.assertFalse(bindingResult.hasErrors());
    String result = reportController.channelUsage(map, request, channelUsageReportForm, bindingResult, response);
    Assert.assertEquals("report.channelUsage", result);

    Assert.assertEquals("text/csv", response.getContentType());
    List<Channel> channels = channelService.getChannels(null, null, "", null);
    Assert.assertEquals(channels, (List<Channel>) map.get("channels"));

    Assert.assertEquals("channelUsage", map.get("usageReportName"));

    Assert.assertEquals(true, map.get("noRecordsFound"));

  }

  /**
   * Author: Abhaik
   */
  @Test
  public void testBillingGroupUsageWithEndDatePrecedsStartDate() throws Exception {
    ChannelUsageReportForm channelUsageReportForm = new ChannelUsageReportForm();
    channelUsageReportForm.setBillingGroupId("1");
    channelUsageReportForm.setEndDate("10/5/2014");
    channelUsageReportForm.setStartDate("30/5/2014");

    BindingResult bindingResult = validate(channelUsageReportForm);
    Assert.assertFalse(bindingResult.hasErrors());
    String result = reportController.channelUsage(map, request, channelUsageReportForm, bindingResult, response);
    Assert.assertEquals("report.channelUsage", result);

    Assert.assertEquals("text/csv", response.getContentType());
    List<Channel> channels = channelService.getChannels(null, null, "", null);
    Assert.assertEquals(channels, (List<Channel>) map.get("channels"));

    Assert.assertEquals("billingGroupUsage", map.get("usageReportName"));

    Assert.assertEquals(true, map.get("noRecordsFound"));

  }

  /**
   * Author: Abhaik
   */
  @Test
  public void testChannelUsageWithInvalidStringEndDate() throws Exception {
    ChannelUsageReportForm channelUsageReportForm = new ChannelUsageReportForm();
    channelUsageReportForm.setChannelId("1");
    channelUsageReportForm.setEndDate("enddate");
    channelUsageReportForm.setStartDate("10/5/2014");

    BindingResult bindingResult = validate(channelUsageReportForm);
    Assert.assertFalse(bindingResult.hasErrors());
    String result = reportController.channelUsage(map, request, channelUsageReportForm, bindingResult, response);
    Assert.assertEquals("downloadCss.error", result);

  }

  /**
   * Author: Abhaik
   */
  @Test
  public void testChannelUsageWithInvalidStringStartDate() throws Exception {
    ChannelUsageReportForm channelUsageReportForm = new ChannelUsageReportForm();
    channelUsageReportForm.setChannelId("1");
    channelUsageReportForm.setEndDate("10/5/2014");
    channelUsageReportForm.setStartDate("startdate");

    BindingResult bindingResult = validate(channelUsageReportForm);
    Assert.assertFalse(bindingResult.hasErrors());
    String result = reportController.channelUsage(map, request, channelUsageReportForm, bindingResult, response);
    Assert.assertEquals("downloadCss.error", result);

  }

  /**
   * Author: Abhaik
   */
  @Test
  public void testBillingGroupUsageWithInvalidStringEndDate() throws Exception {
    ChannelUsageReportForm channelUsageReportForm = new ChannelUsageReportForm();
    channelUsageReportForm.setBillingGroupId("1");
    channelUsageReportForm.setEndDate("enddate");
    channelUsageReportForm.setStartDate("5/10/2014");

    BindingResult bindingResult = validate(channelUsageReportForm);
    Assert.assertFalse(bindingResult.hasErrors());
    String result = reportController.channelUsage(map, request, channelUsageReportForm, bindingResult, response);
    Assert.assertEquals("downloadCss.error", result);

  }

  /**
   * Author: Abhaik
   */
  @Test
  public void testBillingGroupUsageWithInvalidStringStartDate() throws Exception {
    ChannelUsageReportForm channelUsageReportForm = new ChannelUsageReportForm();
    channelUsageReportForm.setBillingGroupId("1");
    channelUsageReportForm.setEndDate("5/10/2014");
    channelUsageReportForm.setStartDate("startdate");

    BindingResult bindingResult = validate(channelUsageReportForm);
    Assert.assertFalse(bindingResult.hasErrors());
    String result = reportController.channelUsage(map, request, channelUsageReportForm, bindingResult, response);
    Assert.assertEquals("downloadCss.error", result);

  }

}

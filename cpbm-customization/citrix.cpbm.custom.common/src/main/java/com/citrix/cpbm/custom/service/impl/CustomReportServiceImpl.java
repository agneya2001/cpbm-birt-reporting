/*
 * Copyright © 2013 Citrix Systems, Inc. You may not use, copy, or modify this file except pursuant to a valid license
 * agreement from Citrix Systems, Inc.
 */
package com.citrix.cpbm.custom.service.impl;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import com.vmops.constants.CustomReportsConstants;
import com.vmops.model.AccountType;
import com.vmops.model.Report;
import com.vmops.model.ScheduledCustomReports;
import com.vmops.portal.config.Configuration;
import com.vmops.portal.config.Configuration.Names;
import com.vmops.portal.reports.GenericReport;
import com.vmops.service.CustomReportService;
import com.vmops.service.SequenceService;
import com.vmops.service.TenantService;
import com.vmops.utils.transfer.impl.FtpConfiguration;
import com.vmops.utils.transfer.impl.FtpTransfer;

/**
 * @author Damoderr
 */
@Service("customReportService")
public class CustomReportServiceImpl implements CustomReportService {

  /**
   * Logger.
   */
  private static Logger logger = Logger.getLogger(CustomReportServiceImpl.class);

  /**
   * App configuration.
   */
  @Autowired
  private Configuration config;

  /**
   * Message source for the messages.
   */
  @Autowired
  private MessageSource messageSource;

  @Autowired
  private TenantService tenantService;

  @Autowired
  private DataSource dataSource;

  @Override
  public String generateCustomReport(ScheduledCustomReports schedReport, String type, SequenceService sequenceService,
      StringBuffer result, String mode, Calendar date, String month, String year) {
    String portalTempPath = config.getValue(Names.com_citrix_cpbm_portal_settings_temp_path);
    String targetFileName = Calendar.getInstance().getTimeInMillis() + "_" + schedReport.getCustomReport().getName();
    logger.debug("targetFileName : " + portalTempPath + File.separator + targetFileName);
    File targetZipFile = new File(portalTempPath + File.separator + targetFileName + ".zip");
    String reportName = schedReport.getCustomReport().getName();
    String className = schedReport.getCustomReport().getClassName();
    logger.debug("Class for the report: " + reportName + " is: " + className);
    if (className != null) {
      Map<String, Object> params = new HashMap<String, Object>();
      params.put("type", type);
      params.put("sequenceService", sequenceService);
      params.put("config", config);
      params.put("messageSource", messageSource);
      if (type.equalsIgnoreCase("MONTHLY")) {
        params.put(CustomReportsConstants.MONTH, month);
        params.put(CustomReportsConstants.YEAR, year);
      } else if (type.equalsIgnoreCase("DAILY")) {
        params.put(CustomReportsConstants.DATE, date);
      }
      // TODO push it to DB configuration other than ApplicationResources.properties
      String limit = messageSource.getMessage("config.reports.account.types." + type.toLowerCase() + ".limit", null,
          null, null);

      if (!StringUtils.isEmpty(limit)) {
        params.put(CustomReportsConstants.LIMIT, limit);
      }
      logger.debug("Fetch Limit is : " + limit);
      Class<?> paramtypes[] = new Class[2];

      paramtypes[0] = Map.class;
      paramtypes[1] = DataSource.class;
      Object initargs[] = new Object[2];
      initargs[0] = params;
      initargs[1] = dataSource;
      try {
        List<AccountType> accountTypes = tenantService.getAllAccountTypes();
        SimpleDateFormat format = new SimpleDateFormat("yyyyddMM");
        int sequenceCounter = 1;
        // This is global for each Report (i.e common for all account types. If at all we
        // want a clear separation then push this inside for loop and add account type to
        // the file name format
        List<File> fileArray = new ArrayList<File>();
        for (AccountType currentAccountType : accountTypes) {
          logger.info("### Generating Report for account type : " + currentAccountType.getName());
          if (currentAccountType.getId() != 1 && !currentAccountType.isTrial()) {
            // Not generating for SYSTEM Type and trial accounts
            if (currentAccountType.getId() == 3) {
              // Right now hard coding here but need to think on it how to get this data in above if
              params.put("is_sales_data", Boolean.FALSE);
            } else {
              params.put("is_sales_data", Boolean.TRUE);
            }
            params.put("account_type", currentAccountType);
            boolean toBeContinued = true;
            // This is just to make sure that for each account type we are passing null
            // initially other wise same key can be used carefully.
            Object lastRecordId = null;
            while (toBeContinued) {
              // This is to iterate through
              logger.debug("### Iteration Number " + sequenceCounter + " for account type "
                  + currentAccountType.getName());
              params.put(CustomReportsConstants.LAST_RECORD_ID, lastRecordId);
              GenericReport customReport = (GenericReport) Class.forName(className).getConstructor(paramtypes)
                  .newInstance(initargs);
              Report report = customReport.getReport();
              String numberOfRecordsWritten = report.getAttributes().get(CustomReportsConstants.NUMBER_OF_RECORDS);
              if (StringUtils.isEmpty(numberOfRecordsWritten) || Integer.parseInt(numberOfRecordsWritten) > 0) {
                File file = report.getReportFile();
                lastRecordId = report.getParams().get(CustomReportsConstants.LAST_RECORD_ID);
                // TODO: read this from propery file or configuration Db if already present
                String fileName = reportName + format.format(new Date()).toString() + "_%1$05d" + "." + "%2$s";
                fileName = String.format(fileName, sequenceCounter++, report.getFileType());
                if (CustomReportsConstants.REPORT_TRANSFER_MODE.equalsIgnoreCase(mode)) {
                  FtpConfiguration fc = new FtpConfiguration();
                  fc.setSourceResouceUrl(file.getAbsolutePath());
                  fc.setTargetResourceUrl(schedReport.getDeliveryUrl() + fileName);
                  FtpTransfer ft = new FtpTransfer();
                  ft.setConfig(fc);
                  ft.transfer();
                  // Deleting the temp file from that location for future use
                  file.delete();
                } else {
                  File newFile = new File(portalTempPath + "/" + fileName);
                  logger.debug("Is renaming of the report file successful -> " + file.renameTo(newFile));
                  fileArray.add(newFile);
                }

              }
              if (StringUtils.isEmpty(limit) || StringUtils.isEmpty(numberOfRecordsWritten)
                  || Integer.parseInt(numberOfRecordsWritten) == 0
                  || params.get(CustomReportsConstants.LAST_RECORD_ID) == null) {
                // To capture the use case when limit is not given(will fetch all in one go)/no records are written
                // in the last call
                // (Here contract between This and reporting class is that When reporting class is using Limit value
                // it should add number_of_records to the Report other wise it should not
                toBeContinued = false;
              }
            }
          }
        }
        if (!CustomReportsConstants.REPORT_TRANSFER_MODE.equalsIgnoreCase(mode)) {
          if (fileArray.size() > 0) {
            FtpTransfer.zipFile(fileArray, targetZipFile);
            for (File tempFile : fileArray) {
              tempFile.delete();
            }
          } else {
            return "none";
          }
        }
        result.append("Report " + reportName + " has been generated successfuly \n");
      } catch (InstantiationException e) {
        logger.error("Error while instantiating the class " + className, e);
        result.append("Report " + reportName + " failed to generate \n");
        return "failure";
      } catch (IllegalAccessException e) {
        logger.error("Error while instantiating the class " + className, e);
        result.append("Report " + reportName + " failed to generate \n");
        return "failure";
      } catch (ClassNotFoundException e) {
        logger.error("Error while instantiating the class " + className, e);
        result.append("Report " + reportName + " failed to generate \n");
        return "failure";
      } catch (Exception e) {
        logger.error("Error generating report " + reportName, e);
        result.append("Report " + reportName + " failed to generate \n");
        return "failure";
      }
    } else {
      logger.error("No class associated with report " + schedReport.getCustomReport().getName());
      result.append("No class associated with report " + schedReport.getCustomReport().getName() + " \n");
      return "failure";
    }
    if (CustomReportsConstants.REPORT_TRANSFER_MODE.equalsIgnoreCase(mode)) {
      return "success";
    } else {
      return targetFileName;
    }
  }

}

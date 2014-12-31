/*
 * Copyright © 2013 Citrix Systems, Inc. You may not use, copy, or modify this file except pursuant to a valid license
 * agreement from Citrix Systems, Inc.
 */
package com.citrix.cpbm.custom.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.vmops.constants.CustomReportsConstants;
import com.vmops.model.AccountType;
import com.vmops.model.PaymentMode;
import com.vmops.model.Report;
import com.vmops.model.TenantChange.Action;
import com.vmops.reports.AbstractReport;

public class CustomerReport extends AbstractReport {

  private static Logger logger = Logger.getLogger(CustomerReport.class);

  private String lastAddedRecord = null;

  public CustomerReport(Map<String, Object> params, DataSource reportDataSource) {
    super(params, reportDataSource);
  }

  @Override
  protected Statement getStatement(Connection connection) throws SQLException {

    String type = (String) report.getParams().get("type");

    // Filter Section Coming from Params
    StringBuilder filter = new StringBuilder();
    if (type.equalsIgnoreCase("MONTHLY")) {
      filter.append(" AND DATE_FORMAT(t.created_at, '%Y%c') = '")
          .append(report.getParams().get(CustomReportsConstants.YEAR))
          .append(report.getParams().get(CustomReportsConstants.MONTH)).append("'");
    } else if (type.equalsIgnoreCase("DAILY")) {
      Calendar cal = (Calendar) report.getParams().get("date");
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      // Decreasing by one day as it should pick up yesterday's data for today's run
      cal.add(Calendar.DATE, -1);
      filter.append(" AND (DATE(t.created_at) = '").append(sdf.format(cal.getTime())).append("' OR (tch.action = '");
      filter.append(Action.CONVERTED.name()).append("' AND DATE(tch.created_at) = '").append(sdf.format(cal.getTime()))
          .append("')) ");
      // Put the run date back
      cal.add(Calendar.DATE, 1);
    }

    // Account Type Details Filter if coming from params
    AccountType accountType = (AccountType) this.report.getParams().get("account_type");
    if (accountType != null) {
      filter.append(" AND t.account_type = ").append(accountType.getId());
    }

    // LastRecord Id Section If coming from params
    String lastRecordId = (String) this.report.getParams().get("last_record_id");
    if (StringUtils.isNotEmpty(lastRecordId)) {
      filter.append(" AND t.id > ").append(lastRecordId);
    }

    // Limit Section If coming from Params
    String fetchSize = (String) this.report.getParams().get("limit");
    String limit = "";
    if (StringUtils.isNotEmpty(fetchSize)) {
      limit = " ORDER BY t.id LIMIT " + fetchSize;
    }

    StringBuffer query = new StringBuffer();
    query.append("SELECT  t.id AS UniqueId, t.account_id as AccountNum, ");
    query.append("  t.name AS name, ");
    query.append(" atype.payment_modes AS PaymentMode, ");
    query.append("  a.postal_code AS ZipCode1,  ");
    query.append("  a.street1 AS Address1 ");
    query.append("FROM tenants t LEFT JOIN tenant_change_history tch ON tch.tenant_id = t.id");
    query.append(" AND tch.action = '").append(Action.CONVERTED.name())
        .append("' AND tch.old_account_type IN (SELECT id FROM account_types WHERE trial = 1), ");
    query.append("  addresses a, account_types atype ");
    query.append("WHERE t.address_id=a.id AND atype.id = t.account_type ");
    query.append(filter);
    query.append(limit);

    logger.debug("About to fire a query: " + query.toString());

    return connection.prepareStatement(query.toString());
  }

  @Override
  protected ResultSet executeQuery(Statement statement) throws SQLException {
    PreparedStatement pstmt = (PreparedStatement) statement;
    return pstmt.executeQuery();
  }

  @Override
  protected String getHeader(Statement pstmt) throws SQLException {
    int cols = ((PreparedStatement) pstmt).getMetaData().getColumnCount();
    StringBuilder sb = new StringBuilder();
    for (int i = 1; i < cols; i++) {
      // Because first column is reporting purpose
      if (i == 1) {
        sb.append("\"").append(((PreparedStatement) pstmt).getMetaData().getColumnLabel(i + 1)).append("\"");
      } else {
        sb.append(",\"").append(((PreparedStatement) pstmt).getMetaData().getColumnLabel(i + 1)).append("\"");
      }
    }
    return sb.toString();
  }

  @Override
  protected List<String> getRecord(Statement statement, ResultSet rs) throws SQLException {
    int cols = ((PreparedStatement) statement).getMetaData().getColumnCount();
    List<String> records = new ArrayList<String>();
    StringBuilder sb = new StringBuilder();
    for (int i = 1; i < cols; i++) {
      String value = StringUtils.EMPTY;
      value = rs.getString(i + 1);
      if ("PaymentMode".equalsIgnoreCase(((PreparedStatement) statement).getMetaData().getColumnLabel(i + 1))) {
        value = PaymentMode.getEnabledModes(Long.parseLong(value.trim())).get(0).name();
      }
      if (i == 1) {
        sb.append("\"").append(value).append("\"");
      } else {
        sb.append(",\"").append(value).append("\"");
      }
    }
    lastAddedRecord = rs.getString(1);
    records.add(sb.toString());
    return records;
  }

  @Override
  protected void setFileType() {
    report.setFileType("csv");
  }

  @Override
  protected Object getUniqueId(ResultSet rs) throws SQLException {
    return rs.getString(1);
  }

  @Override
  protected void updateWithLastAddedRecrod() {
    this.report.getParams().put("last_record_id", lastAddedRecord);
  }

  @Override
  public Report getFusionReport() {
    // TODO
    return null;
  }
}

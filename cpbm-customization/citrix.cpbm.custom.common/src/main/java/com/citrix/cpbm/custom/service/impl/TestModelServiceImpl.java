/*
 * Copyright © 2013 Citrix Systems, Inc. You may not use, copy, or modify this file except pursuant to a valid license
 * agreement from Citrix Systems, Inc.
 */
package com.citrix.cpbm.custom.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.citrix.cpbm.custom.model.TestModel;
import com.citrix.cpbm.custom.persistence.TestModelDAO;
import com.citrix.cpbm.custom.service.TestModelService;

@Service("testModelService")
public class TestModelServiceImpl implements TestModelService {

  @Autowired
  private TestModelDAO testModelDAO;

  @Override
  public List<TestModel> listTestModel() {
    return testModelDAO.findAll(null);
  }

  @Override
  @Transactional
  public void save(TestModel testModel) {
    testModelDAO.save(testModel);
  }

}

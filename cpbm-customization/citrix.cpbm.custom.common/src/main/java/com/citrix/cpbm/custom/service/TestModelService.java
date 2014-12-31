/*
*  Copyright © 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/ 
package com.citrix.cpbm.custom.service;

import java.util.List;

import com.citrix.cpbm.custom.model.TestModel;

public interface TestModelService {

  public List<TestModel> listTestModel();

  public void save(TestModel testModel);
}

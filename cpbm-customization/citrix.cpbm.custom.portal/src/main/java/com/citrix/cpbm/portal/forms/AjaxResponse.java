/*
*  Copyright © 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/
package com.citrix.cpbm.portal.forms;

/**
 * 
 * @author ashishku
 *
 */
public class AjaxResponse {
  
  /**
   * Status
   */
  public enum Status {

    SUCCESS("TRUE"),

    FAILURE("FALSE");

    private Status(String status) {
      this.status = status;
    }

    private String status;

    /**
     * @return the status
     */
    public String getStatus() {
      return status;
    }
  };
  
  private Status status;
  
  private String message;
  
  
  public AjaxResponse() {
    super();
  }

  public AjaxResponse(Status status, String message) {
    super();
    this.status = status;
    this.message = message;
  }



  public Status getStatus() {
    return status;
  }

  
  public void setStatus(Status status) {
    this.status = status;
  }

  
  public String getMessage() {
    return message;
  }

  
  public void setMessage(String message) {
    this.message = message;
  }
  
  

}

/*
*  Copyright © 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/
/**
 * 
 */
package com.citrix.cpbm.custom.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.vmops.model.EntityBase;
import com.vmops.model.User;

@Entity
@Table(name = "testmodel")
public class TestModel implements EntityBase {

  /**
   * Serial version UID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Primary key.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id = 0L;

  /**
   * creationDate.
   */
  @Column(name = "creation_date", insertable = false, updatable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date creationDate;

  /**
   * User involved.
   */
  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  /**
   * Empty Constructor.
   */
  public TestModel() {
  }

  /**
   * Constructor.
   * 
   * @param creationDate Date
   * @param currentState String
   * @param user User
   */

  public TestModel(Date creationDate, User user) {
    super();
    this.creationDate = creationDate;
    this.user = user;
  }

  /*
   * (non-Javadoc)
   * @see com.vmops.model.EntityBase#getId()
   */
  @Override
  public Long getId() {
    return id;
  }

  /**
   * @return the creationDate
   */
  public Date getCreationDate() {
    return creationDate;
  }

  /**
   * @param generatedAt Date
   */
  public void setCreationDate(Date generatedAt) {
    this.creationDate = generatedAt;
  }

  /**
   * @return the user
   */
  public User getUser() {
    return user;
  }

  /**
   * @param user the user to set
   */
  public void setUser(User user) {
    this.user = user;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((creationDate == null) ? 0 : creationDate.hashCode());
    result = prime * result + ((user == null) ? 0 : user.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    TestModel other = (TestModel) obj;
    if (creationDate == null) {
      if (other.creationDate != null) {
        return false;
      }
    } else if (!creationDate.equals(other.creationDate)) {
      return false;
    }
    if (user == null) {
      if (other.user != null) {
        return false;
      }
    } else if (!user.equals(other.user)) {
      return false;
    }
    return true;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "TestModel [" + (user != null ? "user=" + user : "") + "]";
  }

}

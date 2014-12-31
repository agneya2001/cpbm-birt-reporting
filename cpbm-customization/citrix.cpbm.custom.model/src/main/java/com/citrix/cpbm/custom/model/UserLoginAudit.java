/*
*  Copyright © 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/
package com.citrix.cpbm.custom.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.vmops.model.EntityBase;
import com.vmops.model.User;

/**
 * UserLoginAudit model to capture user login details like
 * <ul>
 * <li>ip address from which the user has logged in.</li>
 * <li>time at which user has logged in.</li>
 * </ul>
 * 
 * @author Shiv Prasad Khillar
 */
@Entity
@Table(name = "user_login_audit")
public class UserLoginAudit implements EntityBase {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /**
   * Primary key.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id = 0L;

  /** The user. */
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "userid", nullable = false, updatable = false)
  @NotNull
  private User user;

  /** The remote ip. */
  @Column(name = "remote_ip", length = 255)
  @Size(max = 100)
  private String remoteIp;

  /** The created at. */
  @Column(name = "created_at", nullable = false, updatable = false)
  @Temporal(TemporalType.TIMESTAMP)
  @NotNull
  private Date createdAt = new Date();

  /**
   * Version number. used for Optimistic Concurrency Control.
   */
  @Column(name = "version")
  @Version
  private long version = 0;

  /**
   * Instantiates a new user login audit.
   */
  public UserLoginAudit() {
    super();
  }

  /**
   * Instantiates a new user login audit.
   * 
   * @param user the user
   * @param remoteIp the remote ip
   */
  public UserLoginAudit(User user, String remoteIp) {
    super();
    this.user = user;
    this.remoteIp = remoteIp;
  }

  /**
   * Gets the id.
   * 
   * @return the id
   * @see com.vmops.model.EntityBase#getId()
   */
  @Override
  public Long getId() {
    return null;
  }

  /**
   * Sets the id.
   * 
   * @param id the new id
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Gets the user.
   * 
   * @return the user
   */
  public User getUser() {
    return this.user;
  }

  /**
   * Sets the user.
   * 
   * @param user the new user
   */
  public void setUser(User user) {
    this.user = user;
  }

  /**
   * Gets the remote ip.
   * 
   * @return the remote ip
   */
  public String getRemoteIp() {
    return this.remoteIp;
  }

  /**
   * Sets the remote ip.
   * 
   * @param remoteIp the new remote ip
   */
  public void setRemoteIp(String remoteIp) {
    this.remoteIp = remoteIp;
  }

  /**
   * Gets the created at.
   * 
   * @return the created at
   */
  public Date getCreatedAt() {
    return this.createdAt;
  }

  /**
   * Sets the created at.
   * 
   * @param createdAt the new created at
   */
  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  /**
   * Gets the version.
   * 
   * @return the version
   */
  public long getVersion() {
    return this.version;
  }

  /**
   * Sets the version.
   * 
   * @param version the new version
   */
  public void setVersion(long version) {
    this.version = version;
  }

  /**
   * Hash code.
   * 
   * @return the int
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.createdAt == null) ? 0 : this.createdAt.hashCode());
    result = prime * result + ((this.remoteIp == null) ? 0 : this.remoteIp.hashCode());
    result = prime * result + ((this.user == null) ? 0 : this.user.hashCode());
    return result;
  }

  /**
   * Equals.
   * 
   * @param obj the obj
   * @return true, if successful
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
    UserLoginAudit other = (UserLoginAudit) obj;
    if (this.createdAt == null) {
      if (other.createdAt != null) {
        return false;
      }
    } else if (!this.createdAt.equals(other.createdAt)) {
      return false;
    }
    if (this.remoteIp == null) {
      if (other.remoteIp != null) {
        return false;
      }
    } else if (!this.remoteIp.equals(other.remoteIp)) {
      return false;
    }
    if (this.user == null) {
      if (other.user != null) {
        return false;
      }
    } else if (!this.user.equals(other.user)) {
      return false;
    }
    return true;
  }

  /**
   * To string.
   * 
   * @return the string
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("UserLoginAudit [");
    if (this.id != null) {
      builder.append("id=");
      builder.append(this.id);
      builder.append(", ");
    }
    if (this.user != null) {
      builder.append("user=");
      builder.append(this.user);
      builder.append(", ");
    }
    if (this.remoteIp != null) {
      builder.append("remoteIp=");
      builder.append(this.remoteIp);
      builder.append(", ");
    }
    if (this.createdAt != null) {
      builder.append("createdAt=");
      builder.append(this.createdAt);
      builder.append(", ");
    }
    builder.append("version=");
    builder.append(this.version);
    builder.append("]");
    return builder.toString();
  }

}

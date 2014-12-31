<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!-- Start Grid -->
       <div class="main_listbox" >
          <div class="main_gridpanel" style="border:1px solid #CCCCCC;margin:2px 0 0 13px;">
            <!-- Header -->
            <div class="widget_grid inline header" >
                  <div class="widget_grid_cell" style="width:30%;">
                      <span class="header"><spring:message code="label.name"/></span>
                     </div>
                     <div class="widget_grid_cell" style="width:30%;">
                      <span class="header"><spring:message code="label.value"/></span>
                     </div>
                     <div class="widget_grid_cell" style="width:25%;">
                      <span class="header"><spring:message code="ui.configuration.list.page.description"/></span>
                     </div>
                 </div>
                 <!-- Header end -->
                 <!-- Data Start-->
             
                 <div class="main_gridlistbox configurations_box" style="height:315px;">
                 <form:form commandName="configurationForm" id="configurationForm">
                 <c:forEach var="configuration" items="${configurationForm.configurations}" varStatus="status">
                   
                  <c:choose>
                  <c:when test="${status.index % 2 == 0}">
                      <c:set var="rowClass" value="odd"/>
                  </c:when>
                  <c:otherwise>
                      <c:set var="rowClass" value="even"/>
                  </c:otherwise>
                  </c:choose> 
                  <c:set var="enable_service_config" value="${fn:toLowerCase(configuration.module)}.${fn:toLowerCase(fn:replace(configuration.component, ' ', ''))}.enabled" />
                  <c:if  test='${configuration.name != enable_service_config}'>            
                  <div class="<c:out value="widget_grid ${rowClass}"/>">
                    <div class="widget_grid_cell" style="width:30%;" title="<c:out value="${configuration.label}"/>" >
                       <span class="celltext" style="margin-left:8px;">
                          <c:out value="${configuration.label}"/>
                        </span>
                    </div>
                    <div class="widget_grid_cell" style="width:30%;">
                       <div class="values">
                          <div id="valuenotedit<c:out value="${configuration.id}"/>">
                            <c:choose>
                                 <c:when test="${configuration.isEncryptionRequired == true}">
                               <c:out value="****"/>
                              </c:when>
                                 <c:otherwise>
                                  <c:out value="${configuration.value}" />
                                 </c:otherwise>
                            </c:choose> 
                          </div>  
                                <div id="valueedit<c:out value="${configuration.id}"/>" style="display: none;">
								<input type="hidden" name="configurations[${status.index}].id" value="${configuration.id}"/>
								<input type="hidden" name="configurations[${status.index}].name" value="${configuration.name}"/>
                                  <c:choose>
                                    <c:when test="${configuration.isEncryptionRequired == true}">
                                     <input type="password" name="configurations[${status.index}].value" id="${configuration.id}" class="configtext" style="padding: 0px; margin-bottom: 0px;" value="${configuration.value}" isEncrypted="true" restart="<c:out value="${configuration.isRestartRequired}"/>"/>
                                    </c:when>
                                     <c:otherwise>
                                      <input type="text" name="configurations[${status.index}].value" id="${configuration.id}" class="configtext" value="${configuration.value}" restart="<c:out value="${configuration.isRestartRequired}"/>"/>
                                     </c:otherwise>
                                  </c:choose> 
                                  <div class="configuration_error_container" id="configurations[${status.index}].valueError"></div>  
                                </div> 
                        </div>
                    </div>
                    <div class="widget_grid_cell" style="width:37%;">
                       <span class="celltext" style="margin-left:10px;">
                          <c:catch var ="catchException"> 
                           <spring:message code="config.${configuration.name}.description"/>     
                          </c:catch>
                          <c:if test = "${catchException!=null}">
                          </c:if>                                      
                        </span>
                    </div>
                  
                   </div>
                  </c:if>
                  </c:forEach>
                    
                 <!-- Data end -->
                 </form:form>
          </div>
       </div>
       <!-- End Grid -->
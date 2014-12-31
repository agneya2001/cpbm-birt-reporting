<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

        <c:set var="optional_field_available" value="false" />
        <c:forEach var="instance_property" items="${instance_properties}" varStatus="status">
          <c:if test="${(!instance_property.serviceConfigMetadata.validations.required)}">
            <c:set var="optional_field_available" value="true" />
          </c:if>
          <c:if test="${(mandatory_fields eq 'Y' && instance_property.serviceConfigMetadata.validations.required)
                          || (mandatory_fields eq 'N' && !instance_property.serviceConfigMetadata.validations.required)}">
              <c:choose>
                <c:when test="${status.index % 2 == 0}">
                  <c:set var="rowClass" value="odd" />
                </c:when>
                <c:otherwise>
                  <c:set var="rowClass" value="even" />
                </c:otherwise>
              </c:choose>
              <li>
                <span class="label"><spring:message  code="${service.serviceName}.${instance_property.serviceConfigMetadata.name}.name"/></span> 
                <c:choose>
                  <c:when test="${instance_property.serviceConfigMetadata.type=='Boolean'}">
                    <div>
                      <ul>
                        <li style="padding:0;margin:20px 0 0 3px;width:300px;">
                          <span><input type="radio" style="width:25px;" id="configbooleantrue<c:out value="${instance_property.serviceConfigMetadata.id}"/>" name="${instance_property.serviceConfigMetadata.name}" <c:if test="${instance_property.value == 'true'}"> checked </c:if> /><spring:message  code="label.true"/></span>
                          <span><input type="radio" style="width:25px;" id="configbooleanfalse<c:out value="${instance_property.serviceConfigMetadata.id}"/>" name="${instance_property.serviceConfigMetadata.name}" <c:if test="${instance_property.value == 'false'}"> checked </c:if> /><spring:message  code="label.false"/></span>
                        </li>
                      </ul>
                    </div>
                  </c:when>
                  <c:otherwise>
                    <c:choose>
                      <c:when test="${instance_property.serviceConfigMetadata.validations.required}">
                        <div class="mandatory_wrapper">
                      </c:when>
                      <c:otherwise>
                        <div style="float:left;">
                      </c:otherwise>
                    </c:choose>
                    <c:choose>
                      <c:when test="${instance_property.serviceConfigMetadata.isEncrypted}">
                        <ul>
                          <li style="padding:0;margin:10px 0 0 10px;">
                            <input type="password"  id="configproperty<c:out value="${instance_property.serviceConfigMetadata.id}"/>" name="${instance_property.serviceConfigMetadata.name}"  
                            title="<spring:message  code="${service.serviceName}.${instance_property.serviceConfigMetadata.name}.tooltip"/>" style="margin:0;"
                            class="text ${instance_property.serviceConfigMetadata.validations.classValidations}" ${instance_property.serviceConfigMetadata.validations.validations} onkeyup="showHideUnmaskedLink(this)"
                            value="${instance_property.value}"/>
                          </li>
                          <!--[if !(IE 8)]><!-->
                          <li style="padding:0;margin:0 0 0 10px;">
                            <a <c:if test="${not empty instance_property.value}">style="cursor: pointer; visibility: 'visible'; opacity: 1.0;" </c:if>
                              <c:if test="${empty instance_property.value}">disabled=true style="cursor: pointer; visibility: 'visible'; opacity: 0.5;" </c:if>
                              id="configproperty<c:out value="${instance_property.serviceConfigMetadata.id}"/>_show_unmasked" onclick="showHideUnmaskedField(this)"><spring:message  code="label.show"/></a>
                          </li>
                          <!--<![endif]-->
                        </ul>
                      </c:when>
                      <c:otherwise>
                      <c:set var="instance_property_value" value="${instance_property.value}"></c:set>
                        <input type="text"  id="configproperty<c:out value="${instance_property.serviceConfigMetadata.id}"/>" name="${instance_property.serviceConfigMetadata.name}" 
                        title="<spring:message  code="${service.serviceName}.${instance_property.serviceConfigMetadata.name}.tooltip"/>"
                        class="text ${instance_property.serviceConfigMetadata.validations.classValidations}" ${instance_property.serviceConfigMetadata.validations.validations} 
                        value="<c:out value="${instance_property_value}" escapeXml="true"></c:out>"/>
                      </c:otherwise>
                    </c:choose>
                    </div>
                    <div class="main_addnew_formbox_errormsg_popup" id="configproperty<c:out value='${instance_property.serviceConfigMetadata.id}'/>Error"></div>
                  </c:otherwise>
                </c:choose>
              </li>
              </c:if>
            </c:forEach>
            <input type="hidden" id="isOptionalFieldAvailable" name="isOptionalFieldAvailable" value="${optional_field_available}" />
            
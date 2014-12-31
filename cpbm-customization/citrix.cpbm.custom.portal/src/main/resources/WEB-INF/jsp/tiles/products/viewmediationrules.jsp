<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div class="widget_details_actionbox">
    <ul class="widget_detail_actionpanel" style="float:left;">
    </ul>
</div>

<div class="widget_inline_chargesbox">
    <div class="widget_grid inline subheader">
       <div class="widget_grid_cell header borders" style="width: 200px;">
           <span class="subheader"><spring:message code="label.name"/></span>
       </div>
        <div class="widget_grid_cell borders" style="width: 150px;">
               <span class="subheader"><spring:message code="ui.label.operator"/></span>
         </div>
         <div class="widget_grid_cell borders" style="width: 150px;">
             <span class="subheader"><spring:message code="ui.label.conversion.factor"/></span>
         </div>
         <div class="widget_grid_cell">
             <span class="subheader"><spring:message code="ui.label.discriminators"/></span>
         </div>
    </div>
</div>

  <c:forEach items="${mediationRules}" var="mediationRule" varStatus="status">
  <c:set var="convFactor" value="${mediationRule.conversionFactor}" />
  <c:if test="${mediationRule.monthly==true}">
    <c:set var="convFactor" value="${mediationRule.conversionFactor * hoursInMonths}" />
  </c:if>
     <c:choose>
        <c:when test="${status.index % 2 == 0}">
          <c:set var="rowClass" value="odd"/>
        </c:when>
        <c:otherwise>
          <c:set var="rowClass" value="even"/>
        </c:otherwise>
      </c:choose>
  
      <div class="<c:out value="widget_grid inline ${rowClass}"/>">
       
  
          <div class="widget_grid_cell subheader borders" style="height:27px; width:200px" title="<spring:message code="${mediationRule.serviceInstance.service.serviceName}.UsageType.${mediationRule.serviceUsageType.usageTypeName}.name"/>">
             <span class="celltext right ellipsis" style="width: 80%;"><spring:message code="${mediationRule.serviceInstance.service.serviceName}.UsageType.${mediationRule.serviceUsageType.usageTypeName}.name"/></span>
          </div>
  
           <div class="widget_grid_cell borders" style="height:27px; width: 150px;">
              <span class="celltext">
              <spring:message htmlEscape="false" code="ui.label.${fn:toLowerCase(mediationRule.operator)}" />
              </span>
           </div>
  
           <div class="widget_grid_cell borders" style="height:27px; width: 143px;">
              <span class="celltext">
                 <c:choose>
                 <c:when test="${isProductDiscrete}">
                   <fmt:formatNumber minFractionDigits="0" value="${convFactor}"/>
                 </c:when>
                 <c:otherwise>
                    <c:out value="${convFactor}" />
                 </c:otherwise>
                 </c:choose>
              </span>
           </div>
  
           <div class="widget_grid_cell" style="height:27px;">
              <span class="celltext">
                 <c:if test="${fn:length(mediationRule.mediationRuleDiscriminators) != 0}">
                    <a href="javascript:void(0);" onclick="openDiscriminatorsDialog(this);"  id="<c:out value='${status.index}'/>"><spring:message code="ui.label.discriminators.view"/>
                      <div  id="discriminators_details" style="display: none; overflow: hidden;">
                         <div class="dialog_formcontent" style="width: 550px;height:300px">
                            <div class="details_lightboxtitlebox">
                            </div>

                            <div class="details_lightboxformbox">
                               <div class="widget_grid inline subheader">
                                  <div class="widget_grid_cell header borders" style="width: 200px;">
                                      <span class="subheader"><spring:message code="ui.label.discriminator.type"/></span>
                                  </div>
                                  <div class="widget_grid_cell borders" style="width: 100px;">
                                      <span class="subheader"><spring:message code="ui.label.operator"/></span>
                                  </div>

                                  <div class="widget_grid_cell" style="width: 240px;">
                                      <span class="subheader"><spring:message code="ui.label.discriminator.value"/></span>
                                  </div>

                                  <c:forEach items="${mediationRule.mediationRuleDiscriminators}" var="mediationRuleDiscriminator" varStatus="status2">
                                      <c:choose>
                                         <c:when test="${status2.index % 2 == 0}">
                                           <c:set var="rowClass2" value="odd"/>
                                         </c:when>
                                         <c:otherwise>
                                             <c:set var="rowClass2" value="even"/>
                                         </c:otherwise>
                                      </c:choose>
                                      <div class="<c:out value="widget_grid inline ${rowClass2}"/>">
                                      
                                          <div class="widget_grid_cell subheader borders" style="height:27px; width:200px" title="<spring:message code="${mediationRule.serviceInstance.service.serviceName}.UsageType.Discriminator.${mediationRuleDiscriminator.serviceDiscriminator.discriminatorName}.name"/>">
                                              <span class="celltext right ellipsis" style="width: 80%;"><spring:message code="${mediationRule.serviceInstance.service.serviceName}.UsageType.Discriminator.${mediationRuleDiscriminator.serviceDiscriminator.discriminatorName}.name"/></span>
                                          </div>

                                          <div class="widget_grid_cell borders" style="height:27px; width: 100px;">
                                                 <span class="celltext">
                                                    <c:choose>
                                                      <c:when test="${mediationRuleDiscriminator.operator == 'EQUAL_TO'}">
                                                        <spring:message htmlEscape="false" code="ui.label.includes" />
                                                      </c:when>
                                                      <c:otherwise>
                                                        <spring:message htmlEscape="false" code="ui.label.excludes" />
                                                      </c:otherwise>
                                                    </c:choose>
                                                 </span>
                                          </div>

                                          <div class="widget_grid_cell" style="height:27px; width: 240px;">
                                                 <span class="celltext ellipsis" style="width:90%;" title="<c:out value="${mediationRuleDiscriminator.discriminatorValueDisplayName}" />">
                                                    <c:out value="${mediationRuleDiscriminator.discriminatorValueDisplayName}" />
                                                 </span>
                                          </div>
                                        </div>
                                    </c:forEach>
                               </div>
                            </div>
                         </div>
                      </div>
                    </a>
                 </c:if>
              </span>
          </div>
      </div>
  
  </c:forEach>

<div id="dialog_discriminators_details" style="display:none" title="<spring:message code='ui.label.discriminators'/>"  />
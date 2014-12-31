<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<script type="text/javascript" src="<%=request.getContextPath() %>/js/ossInstanceAdd.js"></script>



<c:choose>
	<c:when test="${viewServiceDetails}">
		<div class="dialog_formcontent ">
			<div class="dialog_detailsbox" style="padding:0px;">
				<div class="mainbox">
					<div class="logobox"><img src="/portal/portal/logo/connector/${service.uuid}/logo"/></div>
					<div class="descriptionbox">
						<h2><spring:message  code="${service.serviceName}.service.name"/></h2>
						<span class="detail1"><spring:message  code="label.category"/>: <spring:message  code="${service.serviceName}.service.category"/></span>
						<p><spring:message  code="${service.serviceName}.service.description"/></p>
					</div>
				</div>
			</div>
		</div>
	</c:when>
	<c:otherwise>
		<span id="backtolisting" service="${service.uuid}" class="title_listall_arrow biggerback" style="float:right;"><spring:message  code="label.backtolisting"/></span>
	
		<div class="service_descriptionbox">
			<div class="service_descriptionbox logobox"><img src="/portal/portal/logo/connector/${service.uuid}/logo"/></div>
			<div class="service_descriptionbox textbox">
				<h3><spring:message  code="${service.serviceName}.service.name"/></h3>
				<br><br><h5><spring:message  code="label.category"/>: <spring:message  code="${service.serviceName}.service.category"/></h5>
				<p><span class="label"><spring:message  code="${service.serviceName}.service.description"/></span></p>
			</div>
		</div>
	
		<div class="service_detail_subsection" style="display:none;border:none;background:none;">
			<div class="contentarea" style="padding:0px">
				<li class="row" style="border:none">
					<span class="description" id="resultstring" style="width: 100%; text-align: center; color: red;"/>
				</li>
			</div>
		</div>
	
		<form id="serviceInstanceForm">
			<c:choose>
				<c:when test="${service_config_properties!=null}">
					<div class="service_detail_subsection">
						<div class="header">
							<h4><spring:message  code="label.configuration"/></h4>
						</div>
						<div class="contentarea">
							<c:forEach var="service_config_property" items="${service_config_properties}" varStatus="status">
								<c:choose>
									<c:when test="${status.index % 2 == 0}">
										<c:set var="rowClass" value="odd" />
									</c:when>
									<c:otherwise>
										<c:set var="rowClass" value="even" />
									</c:otherwise>
								</c:choose>
								<li class="row ${rowClass}">
									<span class="label"><spring:message  code="${service.serviceName}.${service_config_property.name}.name"/></span> 
									<span class="description">
										<c:choose>
											<c:when test="${service_config_property.type=='Boolean'}">
												<span style="padding-left:6px;"/><span><input type="radio" id="configbooleantrue<c:out value="${service_config_property.id}"/>" name="${service_config_property.name}" checked /><spring:message  code="label.true"/></span>
												<span><input type="radio" id="configbooleanfalse<c:out value="${service_config_property.id}"/>" name="${service_config_property.name}"/><spring:message  code="label.false"/></span>
											</c:when>
											<c:otherwise>
												<c:choose>
													<c:when test="${service_config_property.validations.required}">
														<span style="color: rgb(255, 0, 0); padding-left:2px;float:left">*</span>
													</c:when>
													<c:otherwise>
														<span style="padding-left:9px;"/> 
													</c:otherwise>
												</c:choose>
												<c:choose>
													<c:when test="${service_config_property.isEncrypted}">
														<input type="password"  id="configproperty<c:out value="${service_config_property.id}"/>" name="${service_config_property.name}"  
                            title="<spring:message  code="${service.serviceName}.${service_config_property.name}.tooltip"/>"
                            class="text ${service_config_property.validations.classValidations}" ${service_config_property.validations.validations} />
													</c:when>
													<c:otherwise>
														<input type="text"  id="configproperty<c:out value="${service_config_property.id}"/>" name="${service_config_property.name}" 
														title="<spring:message  code="${service.serviceName}.${service_config_property.name}.tooltip"/>"
														class="text ${service_config_property.validations.classValidations}" ${service_config_property.validations.validations}/>
													</c:otherwise>
												</c:choose>	
											</c:otherwise>
										</c:choose>
									</span>
								</li>
							</c:forEach>
						</div>
						<div class="submitpanel">
							<a id="submitbutton" uuid="${service.uuid}" action="save" class="button active" href="javascript:void(0);"><spring:message  code="label.add"/></a>
						</div>
					</div>
				</c:when>
				<c:otherwise>
					<div class="service_detail_subsection">
						<div class="header">
							<h4><spring:message  code="label.configuration"/></h4>
						</div>
						<div class="contentarea">
							<c:forEach var="instance_property" items="${instance_properties}" varStatus="status">
								<c:choose>
									<c:when test="${status.index % 2 == 0}">
										<c:set var="rowClass" value="odd" />
									</c:when>
									<c:otherwise>
										<c:set var="rowClass" value="even" />
									</c:otherwise>
								</c:choose>
								<li class="row ${rowClass}">
									<span class="label"><spring:message  code="${service.serviceName}.${instance_property.serviceConfigMetadata.name}.name"/></span> 
									<span class="description">
										<c:choose>
											<c:when test="${instance_property.serviceConfigMetadata.type=='Boolean'}">
												<span style="padding-left:6px;"/></span><span><input type="radio" id="configbooleantrue<c:out value="${instance_property.serviceConfigMetadata.id}"/>" name="${instance_property.serviceConfigMetadata.name}" checked /><spring:message  code="label.true"/></span>
												<span><input type="radio" id="configbooleanfalse<c:out value="${instance_property.serviceConfigMetadata.id}"/>" name="${instance_property.serviceConfigMetadata.name}"/><spring:message  code="label.false"/></span>
											</c:when>
											<c:otherwise>
												<c:choose>
													<c:when test="${instance_property.serviceConfigMetadata.validations.required}">
														<span style="color: rgb(255, 0, 0); padding-left:2px; float: left;">*</span>
													</c:when>
													<c:otherwise>
														<span style="padding-left:7px;"/>
													</c:otherwise>
												</c:choose>
												<c:set var="textFieldType" value="text"/>
												<c:if test="${instance_property.serviceConfigMetadata.isEncrypted}">
													<c:set var="textFieldType" value="password"/>
												</c:if>
												<input  type="${textFieldType}" id="configproperty<c:out value="${instance_property.serviceConfigMetadata.id}"/>" 
														name="${instance_property.serviceConfigMetadata.name}" value="${instance_property.value}"
														title="<spring:message  code="${service.serviceName}.${instance_property.serviceConfigMetadata.name}.tooltip"/>"
														class="text ${instance_property.serviceConfigMetadata.validations.classValidations}" ${instance_property.serviceConfigMetadata.validations.validations} />
											</c:otherwise>
										</c:choose>
									</span>
								</li>
							</c:forEach>
						</div>
						<div class="submitpanel">
							<a id="submitbutton" uuid="${instance.uuid}" action="update" class="button active" href="javascript:void(0);"><spring:message  code="label.save"/></a>
						</div>
					</div>
				</c:otherwise>
			</c:choose>
		</form>
	</c:otherwise>
</c:choose>
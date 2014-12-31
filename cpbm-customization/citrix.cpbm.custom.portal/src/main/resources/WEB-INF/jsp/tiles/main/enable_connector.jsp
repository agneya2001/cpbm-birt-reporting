<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<div class="dialog_formcontent wizard">
	<div class="widget_wizardcontainer fourstepswizard">
		<input type="hidden" value="${uuid}" name="serviceParam" id="serviceParam">
		
		<!--step 1 starts here-->
		<div style="" class="j_cloudservicepopup" id="step1">
			<input type="hidden" value="step2" name="nextstep" id="nextstep">
			<input type="hidden" value="" name="prevstep" id="prevstep">
			<c:if test="${cloudService}">
			<div class="widgetwizard_stepsbox">
				<div class="widgetwizard_steps_contentcontainer fourstepswizard">
					<div class="widgetwizard_stepscenterbar fourstepswizard">
						<ul>
							<li class="widgetwizard_stepscenterbar fourstepswizard first">
								<span class="steps active">
									<span class="stepsnumbers active">1</span>
								</span>
								<span class="stepstitle">
									<spring:message code="terms.and.conditions"/>
								</span>
							</li>
							<li class="widgetwizard_stepscenterbar fourstepswizard">
								<span class="steps">
									<span class="stepsnumbers">2</span>
								</span>
								<span class="stepstitle">
									<spring:message code="label.service.provider.profile"/>
								</span>
							</li>
							<li class="widgetwizard_stepscenterbar fourstepswizard">
								<span class="steps">
									<span class="stepsnumbers">3</span>
								</span>
								<span class="stepstitle">
									<spring:message code="label.customer.profile"/>
								</span>
							</li>
							<li class="widgetwizard_stepscenterbar fourstepswizard last">
								<span class="steps last">
									<span class="stepsnumbers last">4</span>
								</span>
								<span class="stepstitle last"><spring:message code="label.newUserStep2.finish"/></span>
							</li>
						</ul>
					</div>
				</div>
			</div>
			</c:if>
			<div class="widgetwizard_contentarea fourstepswizard">
				<div class="widgetwizard_boxes fullheight fourstepswizard">
					<div class="widgetwizard_titleboxes">
						<h2><spring:message code="terms.and.conditions"/></h2>
						<span><spring:message code="ui.message.accept.tnc"/></span>
					</div>
					<div class="widgetwizard_tncbox fourstepswizard">
						<div class="default_spacer">
							<p>${tnc}</p>
						</div>
					</div>
					<div class="widgetwizard_acceptbox">
						<input type="checkbox" class="checkbox" id="tncAccept">
						<span class="text"><spring:message code="ui.label.i.agree"/></span>
						<div class="main_addnew_formbox_errormsg" id="tncAcceptError" style="float: none"></div>
					</div>
				</div>
			</div>
			<div class="widgetwizard_nextprevpanel fourstepswizard" id="buttons">
				<input type="button" id="enable_service_next" name="<spring:message code="label.next.step"/>" value="<spring:message code="ui.label.accept"/>" onclick="goToNextStep(this)"
					class="widgetwizard_nextprevpanel nextbutton">
				<a class="cancel close_enable_service_wizard" href="javascript:void(0);"><spring:message code="label.cancel"/></a>
			</div>
		</div>
		<!--step 1 ends here-->

		<c:if test="${cloudService}">
		<!--step 2 starts here-->
		<div style="display: none;" class="j_cloudservicepopup" id="step2">
			<input type="hidden" value="step3" name="nextstep" id="nextstep">
			<input type="hidden" value="step1" name="prevstep" id="prevstep">
			<div class="widgetwizard_stepsbox">
				<div class="widgetwizard_steps_contentcontainer fourstepswizard">
					<div class="widgetwizard_stepscenterbar fourstepswizard">
						<ul>
							<li class="widgetwizard_stepscenterbar fourstepswizard first">
								<span class="steps completedsteps">
									<span class="stepsnumbers completedsteps">1</span>
								</span>
								<span class="stepstitle completedsteps">
									<spring:message code="terms.and.conditions"/>
								</span>
							</li>
							<li class="widgetwizard_stepscenterbar fourstepswizard">
								<span class="steps active">
									<span class="stepsnumbers active">2</span>
								</span>
								<span class="stepstitle active">
									<spring:message code="label.service.provider.profile"/>
								</span>
							</li>
							<li class="widgetwizard_stepscenterbar fourstepswizard">
								<span class="steps">
									<span class="stepsnumbers">3</span>
								</span>
								<span class="stepstitle">
									<spring:message code="label.customer.profile"/>
								</span>
							</li>
							<li class="widgetwizard_stepscenterbar fourstepswizard last">
								<span class="steps last">
									<span class="stepsnumbers last">4</span>
								</span>
								<span class="stepstitle last"><spring:message code="label.newUserStep2.finish"/></span>
							</li>
						</ul>
					</div>
				</div>
			</div>
			<div class="widgetwizard_contentarea fourstepswizard">
				<div class="widgetwizard_boxes fullheight fourstepswizard">
					<div class="widgetwizard_titleboxes">
						<h2><spring:message code="label.service.provider.profile"/></h2>
					</div>
					<div class="widgetwizard_gridmenubox">
						<div class="row header">
							<div style="width: 90%;" class="gridcell header">
								<span class="gridtext header"><spring:message code="ui.profiles.show.page.service.provider"/></span>
							</div>
						</div>
						<div class="widgetwizard_navigationlist widget_navigationlist">
                <c:set var="activeClass" value="active"/>
                <c:forEach var="profile" items="${opsProfiles}">
                  <c:if test="${profile.name != 'System'}">
                    <li class="widget_navigationlist ${activeClass}" id="${profile.id}" title="<spring:message code="profileName.${fn:replace(profile.name, ' ', '')}"/>">
                      <c:choose>
                        <c:when test="${'root' eq fn:toLowerCase(profile.name)}">
                          <span style="margin-top: 4px;" class='navicon ${fn:replace((fn:toLowerCase(profile.name)), " ", "")}user'></span>
                        </c:when>
                        <c:otherwise>
                          <span style="margin-top: 4px;" class='navicon ${fn:replace((fn:toLowerCase(profile.name)), " ", "")}'></span>
                        </c:otherwise>
                      </c:choose>
                      <span class="title"><spring:message code="profileName.${fn:replace(profile.name, ' ', '')}"/></span>
                    </li>
                    <c:set var="activeClass" value=""/>
                  </c:if>                   
                </c:forEach>
              </div>
					</div>
					
					<c:set var="style" value="" />
					<c:forEach var="profile" items="${opsProfiles}" varStatus="status">
						<c:if test="${profile.name != 'System'}">							
							<div class="widgetwizard_detailsbox fivestepswizard gridbox griddescriptionbox" style="${style}"
								id="profile_${profile.id}">
								<div class="row header">
									<div style="width: 3%;" class="gridcell">
										<span class="gridtext"></span>
									</div>
									<div style="width: 80%;" class="gridcell header">
										<span class="gridtext header"><spring:message code="ui.profiles.show.page.role.name"/></span>
									</div>
									<div style="width: 15%;" class="gridcell header">
										<span class="gridtext header"><spring:message code="ui.profiles.show.page.allow"/></span>
									</div>
								</div>
								<c:forEach var="role" items="${globalRoles}">
									<c:set var="key" value="${profile.scope.name}.${role.scope}"/>
									<c:if test="${subScopeMap[key]}">
										<div class="row">
											<div style="width: 3%;" class="gridcell">
												<span class="gridtext"></span>
											</div>
											<div style="width: 82%;" class="gridcell">
												<span class="gridtext"> <spring:message code="${serviceName}.profiles.${role.name}" /> </span>
											</div>
											<div style="width: 13%;" class="gridcell">
												<span class="gridtext">
													<c:choose>
														<c:when test="${profile.scope.name eq 'GLOBAL_ADMIN'}">
															<input type="checkbox" style="" id="disabled_role_${role.name}" checked disabled>
														</c:when>
														<c:otherwise>
															<input type="checkbox" style="" id="role_${role.name}">
														</c:otherwise>
													</c:choose>
												</span>
											</div>
										</div>
									</c:if>
								</c:forEach>
							</div>
							<c:set var="style" value="display: none" />
						</c:if>
					</c:forEach>
				</div>
			</div>
			<div class="widgetwizard_nextprevpanel fourstepswizard" id="buttons">
				<input type="button" id="enable_service_previous" name="<spring:message code="label.previous.step"/>" value="<spring:message code="label.previous.step"/>" onclick="goToPreviousStep(this)"
					class="widgetwizard_nextprevpanel prevbutton">
				<input type="button" id="enable_service_next" name="<spring:message code="label.next.step"/>" value="<spring:message code="label.next.step"/>" onclick="goToNextStep(this)"
					class="widgetwizard_nextprevpanel nextbutton">
				<a class="cancel close_enable_service_wizard" href="javascript:void(0);"><spring:message code="label.cancel"/></a>
			</div>
		</div>
		<!--step 2 ends here-->

		<!--step 3 starts here-->
		<div style="display: none;" class="j_cloudservicepopup" id="step3">
			<input type="hidden" value="step4" name="nextstep" id="nextstep">
			<input type="hidden" value="step2" name="prevstep" id="prevstep">
			<div class="widgetwizard_stepsbox">
				<div class="widgetwizard_steps_contentcontainer fourstepswizard">
					<div class="widgetwizard_stepscenterbar fourstepswizard">
						<ul>
							<li class="widgetwizard_stepscenterbar fourstepswizard first">
								<span class="steps completedsteps">
									<span class="stepsnumbers completedsteps">1</span>
								</span>
								<span class="stepstitle completedsteps">
									<spring:message code="terms.and.conditions"/>
								</span>
							</li>
							<li class="widgetwizard_stepscenterbar fourstepswizard">
								<span class="steps completedsteps">
									<span class="stepsnumbers completedsteps">2</span>
								</span>
								<span class="stepstitle completedsteps">
									<spring:message code="label.service.provider.profile"/>
								</span>
							</li>
							<li class="widgetwizard_stepscenterbar fourstepswizard">
								<span class="steps active">
									<span class="stepsnumbers active">3</span>
								</span>
								<span class="stepstitle">
									<spring:message code="label.customer.profile"/>
								</span>
							</li>
							<li class="widgetwizard_stepscenterbar fourstepswizard last">
								<span class="steps last">
									<span class="stepsnumbers last">4</span>
								</span>
								<span class="stepstitle last"><spring:message code="label.newUserStep2.finish"/></span>
							</li>
						</ul>
					</div>
				</div>
			</div>
			<div class="widgetwizard_contentarea fourstepswizard">
				<div class="widgetwizard_boxes fullheight fourstepswizard">
					<div class="widgetwizard_titleboxes">
						<h2><spring:message code="label.customer.profile"/></h2>
					</div>
					<div class="widgetwizard_gridmenubox">
						<div class="row header">
							<div style="width: 90%;" class="gridcell header">
								<span class="gridtext header"><spring:message code="ui.profiles.show.page.customer"/></span>
							</div>
							
						</div>
						<div class="widgetwizard_navigationlist widget_navigationlist">
                <c:set var="activeClass" value="active"/> 
                <c:forEach var="profile" items="${nonOpsProfiles}">
                  <li class="widget_navigationlist ${activeClass}" id="${profile.id}" title="<spring:message code="profileName.${fn:replace(profile.name, ' ', '')}"/>">
                    <span style="margin-top: 4px;" class='navicon ${fn:replace((fn:toLowerCase(profile.name)), " ", "")}'></span>
                    <span class="title"><spring:message code="profileName.${fn:replace(profile.name, ' ', '')}"/></span>
                  </li>
                  <c:set var="activeClass" value=""/>
                </c:forEach>
              </div>
					</div>
					<c:forEach var="profile" items="${nonOpsProfiles}" varStatus="status">
						<c:set var="style" value="" />
						<c:if test="${!status.first}">
							<c:set var="style" value="display: none" />
						</c:if>
						<div class="widgetwizard_detailsbox fivestepswizard gridbox griddescriptionbox" style="${style}"
							id="profile_${profile.id}">
							<div class="row header">
								<div style="width: 3%;" class="gridcell">
									<span class="gridtext"></span>
								</div>
								<div style="width: 80%;" class="gridcell header">
									<span class="gridtext header"><spring:message code="ui.profiles.show.page.role.name"/></span>
								</div>
								<div style="width: 15%;" class="gridcell header">
									<span class="gridtext header"><spring:message code="ui.profiles.show.page.allow"/></span>
								</div>
							</div>
							<c:forEach var="role" items="${tenantRoles}">
								<c:set var="key" value="${profile.scope.name}.${role.scope}" />
								<c:if test="${subScopeMap[key]}">
									<div class="row">
										<div style="width: 3%;" class="gridcell">
											<span class="gridtext"></span>
										</div>
										<div style="width: 82%;" class="gridcell">
											<span class="gridtext"><spring:message code="${serviceName}.profiles.${role.name}" /> </span>
										</div>
										<div style="width: 13%;" class="gridcell">
											<span class="gridtext">
												<c:choose>
													<c:when test="${profile.scope.name eq 'TENANT_ADMIN'}">
														<input type="checkbox" style="" id="disabled_role_${role.name}" checked disabled>
													</c:when>
													<c:otherwise>
														<input type="checkbox" style="" id="role_${role.name}">
													</c:otherwise>
												</c:choose>
											</span>
										</div>
									</div>
								</c:if>
							</c:forEach>
						</div>
					</c:forEach>
				</div>
			</div>
			<div class="widgetwizard_nextprevpanel fourstepswizard" id="buttons">
				<input type="button" id="enable_service_previous" name="<spring:message code="label.previous.step"/>" value="<spring:message code="label.previous.step"/>" onclick="goToPreviousStep(this)"
					class="widgetwizard_nextprevpanel prevbutton">
				<input type="button" id="enable_service_next" name="<spring:message code="label.submit"/>" value="<spring:message code="label.submit"/>"  onclick="goToNextStep(this)"
					data-primary="" class="widgetwizard_nextprevpanel nextbutton">
				<a class="cancel close_enable_service_wizard" href="javascript:void(0);"><spring:message code="label.cancel"/></a>
			</div>
		</div>
		<!--step 3 ends here-->

		<!--step 4 starts here-->
		<div style="display: none;" class="j_cloudservicepopup" id="step4">
			<input type="hidden" value="" name="nextstep" id="nextstep">
			<input type="hidden" value="step3" name="prevstep" id="prevstep">
			<div class="widgetwizard_stepsbox">
				<div class="widgetwizard_steps_contentcontainer fourstepswizard">
					<div class="widgetwizard_stepscenterbar fourstepswizard">
						<ul>
							<li class="widgetwizard_stepscenterbar fourstepswizard first">
								<span class="steps completedsteps">
									<span class="stepsnumbers completedsteps">1</span>
								</span>
								<span class="stepstitle completedsteps">
									<spring:message code="terms.and.conditions"/>
								</span>
							</li>
							<li class="widgetwizard_stepscenterbar fourstepswizard">
								<span class="steps completedsteps">
									<span class="stepsnumbers completedsteps">2</span>
								</span>
								<span class="stepstitle completedsteps">
									<spring:message code="label.service.provider.profile"/>
								</span>
							</li>
							<li class="widgetwizard_stepscenterbar fourstepswizard">
								<span class="steps completedsteps">
									<span class="stepsnumbers completedsteps">3</span>
								</span>
								<span class="stepstitle completedsteps">
									<spring:message code="label.customer.profile"/>
								</span>
							</li>
							<li class="widgetwizard_stepscenterbar fourstepswizard last">
								<span class="steps active" style="margin-left:40px">
									<span class="stepsnumbers active">4</span>
								</span>
								<span class="stepstitle last"><spring:message code="label.newUserStep2.finish"/></span>
							</li>
						</ul>
					</div>
				</div>
			</div>
			<div style="margin-top: 0;height:440px" class="widgetwizard_contentarea">
				<div class="widgetwizard_contentarea fourstepswizard" style="height:350px;margin-left:20px">
					<div class="widgetwizard_boxes fullheight fourstepswizard" style="height:370px">
						<div class="widgetwizard_successbox">
							<div class="widgetwizard_successbox">
								<div class="widget_resulticon success"></div>
								<p id="successmessage"><spring:message code="ui.message.enable.service.success.cs"/></p>
								<p><a href="/portal/portal/connector/cs"><spring:message code="ui.message.goto.services"/></a></p>								
							</div>
						</div>
					</div>
				</div>
				<div class="widgetwizard_nextprevpanel fourstepswizard" id="buttons" style="width:705px">
					<input type="button" id="enable_service_next" name="<spring:message code="label.close"/>" value="<spring:message code="label.close"/>" onclick="closeDialog()"
						class="widgetwizard_nextprevpanel submitbutton">
				</div>
			</div>
		</div>
		<!--step 4 ends here-->
	</c:if>
	</div>

	<div class="ui-resizable-handle ui-resizable-n" unselectable="on" style="-moz-user-select: none;"></div>
	<div class="ui-resizable-handle ui-resizable-e" unselectable="on" style="-moz-user-select: none;"></div>
	<div class="ui-resizable-handle ui-resizable-s" unselectable="on" style="-moz-user-select: none;"></div>
	<div class="ui-resizable-handle ui-resizable-w" unselectable="on" style="-moz-user-select: none;"></div>
	<div class="ui-resizable-handle ui-resizable-se ui-icon ui-icon-gripsmall-diagonal-se ui-icon-grip-diagonal-se"
		style="z-index: 1001; -moz-user-select: none;" unselectable="on"></div>
	<div class="ui-resizable-handle ui-resizable-sw" style="z-index: 1002; -moz-user-select: none;" unselectable="on"></div>
	<div class="ui-resizable-handle ui-resizable-ne" style="z-index: 1003; -moz-user-select: none;" unselectable="on"></div>
	<div class="ui-resizable-handle ui-resizable-nw" style="z-index: 1004; -moz-user-select: none;" unselectable="on"></div>
</div>
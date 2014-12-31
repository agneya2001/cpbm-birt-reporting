<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/validator.js"></script>

<style type="text/css">
.dialog_formcontent label.error {
	margin-left: 265px;
}

.warning_reconfigurable {
  margin-left: 265px;
}
.tooltip {
    position: fixed;
}

</style>
<script type="text/javascript">

$(document).ready(function() {
	$("#channelServiceSettingsForm").validate({
		submitHandler : function() {
			var noOfErrors = $("#channelServiceSettingsForm").find('label.error:visible').length;
			if (noOfErrors == 0) {
				saveChannelServiceSettings();
			}
			return false;
		}
	});
	
	$('input[id^="channelServiceSettings"]').tooltip({
		position : "center right",
		placement: "bottom",
		offset : [ 0, 0 ],
		effect : "fade",
		opacity : 0.7
	});
});
</script>

<div class="dialog_formcontent">
	<div class="details_lightboxtitlebox"></div>
	<div class="details_lightboxformbox">
		<form:form commandName="channelServiceSettingsForm" style="float:left">
			<form:hidden path="channelId"/>
			<form:hidden path="serviceInstanceUUID"/>
			<form:hidden path="mode"/>
			<ul>
				<c:forEach var="entry" items="${channelServiceSettingsForm.channelServiceSettings}" varStatus="status">
					<li style="height: 28px;margin: 10px 0 10px 0; width: 550px;">
						<label style="width: 260px">
								<spring:message code="${entry.serviceName}.ChannelSettings.${entry.name}.name" />
						</label>
						<c:choose>
							<c:when test="${entry.propertyType=='Boolean'}">
								<span style="margin-left:10px;margin-top:3px;"> 
									<form:radiobutton path="channelServiceSettings[${status.index}].value" value="true"/>
										<spring:message code="label.true" />
								</span>
								<span style="margin-left:10px;margin-top:3px;">
									<form:radiobutton path="channelServiceSettings[${status.index}].value" value="false"/>
										<spring:message code="label.false" />
								</span>
							</c:when>
							<c:otherwise>
								<spring:message code="${entry.serviceName}.ChannelSettings.${entry.name}.tooltip" var="tooltip"/>
								<form:input path="channelServiceSettings[${status.index}].value" class="text ${entry.validationClass}" title="${tooltip}" />
							</c:otherwise>
						</c:choose>
						<form:hidden path="channelServiceSettings[${status.index}].name"/>
						<form:hidden path="channelServiceSettings[${status.index}].serviceConfigMetaDataId"/>
						<c:if test="${channelServiceSettingsForm.mode =='edit' && !entry.reconfigurable && not empty entry.value}">
							<span class="warning_reconfigurable" style="margin-left:280px;">
									<spring:message code="ui.connector.channel.reconfigurable.property.warning" />
							</span>
						</c:if>
					</li>
				</c:forEach>
			</ul>
		</form:form>
	</div>
</div>
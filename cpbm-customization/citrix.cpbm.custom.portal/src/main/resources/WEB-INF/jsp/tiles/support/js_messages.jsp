<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<script language="javascript">
if( typeof i18n === 'undefined' ) {
  var i18n = {};
}
if( typeof i18n.errors === 'undefined' ) {
  i18n.errors = {};
}

i18n = {
    errors: {
      ticketDescription  	: '<spring:message javaScriptEscape="true" code="js.error.tickets.description"/>', 
      ticketTitle  			: '<spring:message javaScriptEscape="true" code="js.error.tickets.title"/>', 
      ticketStatus  		: '<spring:message javaScriptEscape="true" code="js.error.tickets.status"/>',      
      commentBody  			: '<spring:message javaScriptEscape="true" code="js.error.tickets.comments.body"/>',
      ticketTitleMaxlength  : '<spring:message javaScriptEscape="true" code="js.error.tickets.maxlength.title"/>'
	},
	alerts:{
      createFailure  		: '<spring:message javaScriptEscape="true" code="js.alert.tickets.create.failure"/>',
      fetchFailure  		: '<spring:message javaScriptEscape="true" code="js.alert.tickets.fetch.failure"/>',
      postCommentFailure  	: '<spring:message javaScriptEscape="true" code="js.alert.tickets.post.comment.failure"/>',
      editFailure  			: '<spring:message javaScriptEscape="true" code="js.alert.tickets.edit.failure"/>',
      closeFailure  		: '<spring:message javaScriptEscape="true" code="js.alert.tickets.close.failure"/>',
      showMoreFailure		: '<spring:message javaScriptEscape="true" code="js.alert.tickets.show.more.failure"/>'
	} ,
	confirm:{
		closeTicket			: '<spring:message javaScriptEscape="true" code="js.confirm.tickets.close"/>'
	},
	labels:{
		ticketDetails				: '<spring:message javaScriptEscape="true" code="ui.label.support.tickets.ticket.view.details.header"/>'
	}
};

</script>
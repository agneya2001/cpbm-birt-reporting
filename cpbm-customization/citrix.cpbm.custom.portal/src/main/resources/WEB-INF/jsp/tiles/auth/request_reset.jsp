<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<script type="text/javascript">
<!--
$(document).ready(function() {
	  $("#loginForm").validate( {
//      debug : true,
      success : "valid",
      ignoreTitle : true,
      rules : {
        "username": {
          required : true
        }
      },
      messages: {
        "username" : {
          required: '<spring:message javaScriptEscape="true" code="js.user.username"/>'
        }
      },
      errorPlacement: function(error, element) { 
        var name = element.attr('id');
        name =ReplaceAll(name,".","\\.");     
          error.appendTo( "#"+name+"Error" );
      }
    });
	
  setTimeout('$("#loginForm input:first").focus();', 1000);
  $("#submitButton").click(function() {
	    
	    if($("#loginForm").valid()){
	      $("#loginForm").submit();
	     }
	    
	});
  $('.login_button').click(function() {
    $("#loginForm").submit();
  });
  $(':input').keypress(function(e) {
    if (e.keyCode == 13) {
      $("#loginForm").submit();
    }
  });
});

//-->
</script>
            	<div class="login_headerarea">
                	<div class="login_headerarea_left">
                    	<%@ include file="../shared/channel_logo.jsp" %>
                    </div>
                   
                </div>
                <div class="login_maincontentarea">
                	<div class="login_maincontentarea_titlepanel">
                    	<h1><spring:message code="label.reset.password.request"/></h1>
                    </div>                 
				   <div class="login_formbox">
						<form id="loginForm" method="post" action="<%= request.getContextPath() %>/portal/reset_password" name="passwordReset">
					    <ol>
					      <li>
						      <label for="username"><spring:message code="label.login.username"/></label>    
                              <spring:message code="label.userInfo.username.tooltip" var="i18nUsernameTooltip"/>
                              <input class="text" tabindex="1" name="username" id="username" title="<c:out value="${i18nUsernameTooltip}"/>"/>
						      <div class="login_formbox_errormsg" id="usernameError"></div>
					      </li>
					       <li>
	                <div class="login_buttonscontainer">
                     <div style="margin: 10px 20px 0px 10px;float: left;">
                        <a tabindex="3" href="<%= request.getContextPath() %>/portal/login"><spring:message code="label.tenants.cancel"/></a></div>
      	               	<a id="submitButton" class="logincommonbutton" tabindex="2" href="#" ><spring:message code="label.reset.password.request.submit"/></a>
 		                </div>
	                </div>
					      </li>
					    </ol>  
						</form>
						
				  </div>	
    </div>  

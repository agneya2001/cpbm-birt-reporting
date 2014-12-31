<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<script type="text/javascript">

var dictionary = { 
      
};

</script>

        <div class="maintitlebox">
            <h1><spring:message code="label.newUserStep1.addUser"/></h1>
        </div>
        <div id="step3" class="wizard_box small" style="display: block;">
            <div class="wizard_box_top small">
                <div class="wizardbox_maintabbox">
                  <spring:message code="label.newUserStep4.confirmation"/>
                </div>
                <div class="user_wizard step3">
                    <span class="steps1text"><spring:message code="label.newUserStep1.addUser"/></span>
                    <span class="steps2text"><spring:message code="label.newUserStep2.credentials"/></span>
                    <span class="steps3text"><spring:message code="label.newUserStep2.customize"/></span>
                </div>
            </div>
         </div>
         <div class="wizard_box_bot small">
         <div class="registration_formbox">  
          <div style="margin:20px 20px 20px 20px;">
						 <h2>
               <spring:message code="label.newUserStep4.thankyou" arguments="${currentUser.firstName}, ${currentUser.lastName}"/>
               <br/><br/>
               <spring:message code="label.newUserStep4.message"/>
               <c:if test="${tenant.state == 'ACTIVE'}"> 
               <spring:message code="label.newUserStep4.emailconfirmation"/>
               </c:if>
             </h2>
					 </div>
				</div>
				</div>




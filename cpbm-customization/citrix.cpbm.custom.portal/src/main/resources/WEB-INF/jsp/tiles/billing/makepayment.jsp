<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<script type="text/javascript">
$("#makePaymentInvoiceForm").validate({
    //debug : false,
      success : "valid",
      ignoreTitle : true,
     errorPlacement: function(error, element) { 
      var name = element.attr('id');
      name =ReplaceAll(name,".","\\.");  
      if(error.html() !=""){
        error.appendTo( "#"+name+"Error" );
      }
    }
  });
</script>
 <!--  Make Payment starts here-->
<div class="dialog_formcontent">
   
    <spring:url value="/portal/billing/make_payment/{invoiceParam}" var="make_invoice_payment_path" htmlEscape="false" >  
        <spring:param name="invoiceParam"><c:out value="${activity.param}"/></spring:param>   
    </spring:url>         
     
    <form class="ajaxform" id="makePaymentInvoiceForm" name="makePaymentInvoiceForm" action="<c:out value="${make_invoice_payment_path}"/>" >
     <!-- Edit fields -->
              <div class="db_gridbox_columns" style="width:30%;">
                  <div class="db_gridbox_celltitles details"><strong><spring:message code="label.make.payment.invoice.balance.amount"/></strong></div>
              </div>
              <div class="db_gridbox_columns" style="width:60%;">
              <div class="db_gridbox_celltitles details">
              <c:out value="${activity.tenant.currency.sign}" />
	              <fmt:formatNumber pattern="${currencyFormat}"   value="${activity.balanceAmount}" minFractionDigits="${minFractionDigits}"  />
              </div>
                  
              </div>
			<input class ="priceRequired" id="invoicePayAmount" type="hidden" name="invoicePayAmount" value="<c:out value="${activity.balanceAmount}"/>" readonly="true" disabled="true"/>

       <div class="main_addnew_formbox_errormsg" id="miscFormErrors" style="margin:10px 0 0 5px"></div>
    
    </form>
</div>
<!--  Make Payment ends here-->

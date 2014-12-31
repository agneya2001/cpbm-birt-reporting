<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<script type="text/javascript">
  $(document).ready(function() {

    $("#paymentForm").validate({
      //debug : false,
      success : "valid",
      ignoreTitle : true,
      rules : {
        "payAmount" : {
          required : true,
          twoDecimal : true
        },
            "paymentMemo" : {
              maxlength : 255
            }
        
      },
      messages : {
        "payAmount" : {
          twoDecimal : i18n.errors.priceRequired,
          required : i18n.errors.invoicePayAmount 
        }

      },
      errorPlacement : function(error, element) {
        var name = element.attr('id');
        name = ReplaceAll(name, ".", "\\.");
        if (error.html() != "") {
          error.appendTo("#" + name + "Error");
        }
      }
    });
  });
</script>

<div class="dialog_formcontent">
	<form:form id="paymentForm">
		<div class="db_gridbox_columns" style="width: 35%;">
			<div class="db_gridbox_celltitles details">
				<strong><spring:message code="label.record.payment.invoice.payment.amount" /></strong>
			</div>
		</div>
		<div class="mandatory_wrapper" style="width: 60%;">
			<p style="float: left; margin-top: 10px; color: black">
				<c:out value="${salesLedgerRecord.tenant.currency.sign}" />
			</p>
			<input class="text" id="payAmount" name="payAmount" style="width: 175px;"/>
		</div>
		<div class="main_addnew_formbox_errormsg" id="payAmountError" style="margin: 4px 0px 0 111px; width: 60%"></div>
		<div class="db_gridbox_columns" style="width: 35%;">
			<div class="db_gridbox_celltitles details">
				<strong><spring:message code="label.record.payment.invoice.payment.memo" /></strong>
			</div>
		</div>
		<div class="db_gridbox_columns" style="width: 60%;">
			<p style="float: left; margin-top: 10px; color: black">				
			</p>
			<textarea style="width: 175px; height: 80px;" tabindex="2" cols="20" rows="3" class="longtextbox"name="paymentMemo" id="paymentMemo"></textarea>
		</div>
		<div class="main_addnew_formbox_errormsg" id="paymentMemoError" style="margin: 4px 0px 0 111px; width: 60%"></div>
	</form:form>
</div>
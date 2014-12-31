<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>


<script type="text/javascript">

$.validator
.addMethod(
    "startDateCheck",
    function(value, element) {
        $(element).rules("add", {
          required : true
        });
        var now = new Date();
        now.setHours(0, 0, 0, 0);
        return $.datepicker.parseDate(g_dictionary.friendlyDate, value) >= now;
    },
    commmonmessages.startDate);


$("#campaignPromotionsForm").validate( {
  //debug : true,
    success : "valid",
    ignoreTitle : true,
    rules : {
      "campaignPromotion.code":{
      required :true,
      minlength : 1,      
      noSpacesAndLengthLmitCheck : true,
      remote:{
        url : '/portal/portal/products/validateCode',
        async:false
      }
    },
    "campaignPromotion.startDateDisplay":{
      required :true,
      dateRange: true,
      startDateCheck:true
    },
    "campaignPromotion.endDateDisplay":{
      dateRange :true
      },       
    "campaignPromotion.title" : {
            required :true,
        minlength : 1
            },
    "promoCode" : {           
      required:true,
      minlength : 1,      
      noSpacesAndLengthLmitCheck : true,
          remote: {
              url : '/portal/portal/promotions/validate_promoCode',
              async:false
            }
        },

          "campaignPromotion.durationDays" : {
            required : true,
            min : function(){
              if ($("#unlimitedUsage").is(':checked'))
                return 0;
              return 1;
            },
            max:99999999,
            digits : true       
          },
          "campaignPromotion.maxAccounts" : {
            required : true,
            min : function(){
              if ($("#unlimitedAccounts").is(':checked'))
                return 0;
              return 1;
            },
            max:99999999,
            digits : true       
          },
        "campaignPromotion.percentOff" : {
          required : true,
          range : [0, 100]
        }
  },
    messages: {
    "campaignPromotion.code": {
      required: i18n.campaigns.campaigncoderequired,
      noSpacesAndLengthLmitCheck: i18n.campaigns.promocodeinvalid,
      remote: i18n.errors.common.codeNotUnique
    } ,
    "campaignPromotion.title": {
      required :i18n.campaigns.promotitlerequired
    } ,
    "promoCode": {
      required :i18n.campaigns.promocoderequired,
      noSpacesAndLengthLmitCheck: i18n.campaigns.promocodeinvalid,
      remote: i18n.errors.common.codeNotUnique
    } ,
    "campaignPromotion.durationDays": {
      required : i18n.campaigns.durationrequired,
      digits : i18n.campaigns.digitsrequired,
      min : i18n.campaigns.digitsmin
    },    
    "campaignPromotion.percentOff": {
      required : i18n.campaigns.percentoffrequired,
      range : i18n.campaigns.percentoffrange
    },    
    "campaignPromotion.maxAccounts": {
      required : i18n.campaigns.maxaccountsrequired,
      digits : i18n.campaigns.digitsrequired,
      min : i18n.campaigns.digitsmin
    },    
      "campaignPromotion.endDateDisplay":{
      dateRange : i18n.campaigns.campaignEnterValidDateRange
      },
      "campaignPromotion.startDateDisplay":{
      required: i18n.campaigns.startdaterequired,
      dateRange : i18n.campaigns.campaignEnterValidDateRange,
      startDateCheck:commmonmessages.startDate
      }
    
   },
    errorPlacement: function(error, element) {
      var name = element.attr('id');
      name =ReplaceAll(name,".","\\."); 
      if (name != "") {
        if (name.startsWith("discountAmountMap")) {
          $("#discountAmountMapError").show();
        } else {
          error.appendTo("#" + name + "Error");
        }
      }
    }

  });


</script>
<script type="text/javascript">
$('select').change(function(){
//if All is selected with some other channels then it should get deselected. 
  if($("#channelMultiSelect").val().length > 1){
    $("#selectAll").removeAttr("selected");
  }
    getCurrencyForSelectedChannel();
});
</script>

<style>
.ui-datepicker-trigger {
    margin-left : 5px;
    margin-top : 0px;
}
#ui-datepicker-div{
  z-index:9999;
}
</style>
<!--  Add new Campaign starts here-->
<div class="dialog_formcontent" >
    <spring:url value="/portal/promotions/create" var="create_campaign_path" htmlEscape="false" /> 
    <form:form
      commandName="campaignPromotionsForm" id="campaignPromotionsForm" cssClass="ajaxform" action="${create_campaign_path}">
      <div class="details_lightboxtitlebox">
     </div>
        <div class="details_lightboxformbox add_edit_campaign">
          <ul>
            <li><form:label path=""><spring:message code="ui.campaigns.label.create.code"/></form:label>
            <div class="mandatory_wrapper">
              <form:input cssClass="text" path="campaignPromotion.code" cssStyle="margin:0 5px 0 10px;" /></div>
              <input id="campaignPromotion_code" type="hidden" 
              value="<c:out  value="${campaignPromotionsForm.campaignPromotion.code}" />"/>
              <div class="main_addnew_formbox_errormsg add_edit_campaign" id="campaignPromotion.codeError"></div>
            </li>
            <li><form:label path=""><spring:message code="ui.campaigns.label.create.title"/></form:label>
              <div class="mandatory_wrapper">
                <form:input cssClass="text" path="campaignPromotion.title" cssStyle="margin:0 5px 0 10px;"/>
              </div>
              <div class="main_addnew_formbox_errormsg add_edit_campaign" id="campaignPromotion.titleError"></div>
            </li>
            <li>
              <form:label path=""><spring:message code="ui.campaigns.label.create.promo.code"/></form:label>
              <div class="mandatory_wrapper">
                <form:input cssClass="text" path="promoCode" cssStyle="margin:0 5px 0 10px;"></form:input>
                <input id="promoCode_old" type="hidden" value="<c:out  value="${campaignPromotionsForm.promoCode}" />"/>
              </div>
              <div class="main_addnew_formbox_errormsg add_edit_campaign" id="promoCodeError"></div>
            </li>
            <li>
              <form:label path=""><spring:message code="ui.campaigns.label.create.startdate"/></form:label>
              <div class="mandatory_wrapper">
                <form:input type="hidden" path="campaignPromotion.startDate"/>
                <input class="text" id="campaignPromotion.startDateDisplay" name="campaignPromotion.startDateDisplay" style="margin:0 5px 0 10px;" readonly="readonly"/>
              </div>
              <div class="main_addnew_formbox_errormsg add_edit_campaign" id="campaignPromotion.startDateError"></div>
              <div class="main_addnew_formbox_errormsg add_edit_campaign" id="campaignPromotion.startDateDisplayError"></div>
            </li>   
            <li>
              <form:label path=""><spring:message code="ui.campaigns.label.create.enddate"/></form:label>
              <div class="nonmandatory_wrapper">
                <form:input type="hidden" path="campaignPromotion.endDate" />
                <input class="text" id="campaignPromotion.endDateDisplay" name="campaignPromotion.endDateDisplay" style="margin:0 5px 0 10px;" readonly="readonly"/>
              </div>
              <div class="main_addnew_formbox_errormsg add_edit_campaign" id="campaignPromotion.endDateError"></div>
              <div class="main_addnew_formbox_errormsg add_edit_campaign" id="campaignPromotion.endDateDisplayError"></div>
            </li>                    
          <li style="width:430px;">
            <form:label path=""><spring:message code="ui.campaigns.label.create.trial"/></form:label>
              <input type="checkbox" class="checkbox " id="trialPromotion" name="trial" style="margin:0 5px 0 10px;"/>
              <form:input path="campaignPromotion.trial" type="hidden"/>
          </li>          
          <li style="width:430px;">
            <form:label path=""><spring:message code="ui.campaigns.label.create.unlimitedaccount"/></form:label>
              <input type="checkbox" class="checkbox " id="unlimitedAccounts" style="margin:0 5px 0 10px;" name="unlimitedAccounts" checked/>
          </li>          
          <li id="liMaxAccounts"  style="display:None">
            <form:label path=""><spring:message code="ui.campaigns.label.create.max.accounts"/></form:label>
            <div class="mandatory_wrapper">
              <form:input cssClass="text" id="maxAccounts"  path="campaignPromotion.maxAccounts" cssStyle="margin:0 5px 0 10px;"/>
            </div>
            <div class="main_addnew_formbox_errormsg" id="maxAccountsError"></div>            
          </li> 
          <li style="width:430px;">
            <form:label path=""><spring:message code="ui.campaigns.label.create.unlimitedduration"/></form:label>
              <input type="checkbox" class="checkbox " id="unlimitedUsage" style="margin:0 5px 0 10px;" name="unlimitedUsage" checked/>
          </li>          
          <li id="liDurationDays" style="display:None">
            <form:label cssClass="durationInDays" path="" cssStyle="display:None"><spring:message code="ui.campaigns.label.create.duration"/></form:label>
            <form:label cssClass="durationInPeriods" path=""><spring:message code="ui.campaigns.label.create.duration.billingperiod"/></form:label>
            <div class="mandatory_wrapper">
              <form:input cssClass="text" id="durationDays"  path="campaignPromotion.durationDays" cssStyle="margin:0 5px 0 10px;"/>
            </div>
            <div class="main_addnew_formbox_errormsg" style="margin: 5px 0 0 115px" id="durationDaysError"></div>            
          </li> 

            <li style="width:430px;">
            <label><spring:message code="ui.campaigns.label.create.add.channel" />:</label>
            <div class="mandatory_wrapper">
              &nbsp;&nbsp;
              <form:select id="channelMultiSelect" multiple="multiple" path="channelIdLst">
              <option value="" id="selectAll" selected="selected"><spring:message code="ui.campaigns.label.create.channel.type"/></option>
                <c:forEach items="${channels}" var="channel">
                      <option value="<c:out value="${channel.id}" />"><c:out value="${channel.name}" /></option>
                </c:forEach>
              </form:select>
            </div>
        </li>
          

          <li id="discountTypeRadios">
            <input id="campaignPromotion.discountType" type="hidden" value="<c:out  value="${campaignPromotionsForm.campaignPromotion.discountType}" />"/>
            <div style="float:left; width:20px">
              <form:radiobutton  path="campaignPromotion.discountType" cssClass="discountTypeRadio" value="${PERCENTAGE}"  />
            </div>
            <div style="float:left; width:120px">
              <form:label  path=""><spring:message code="ui.campaigns.label.create.discount.type.percent.off"/></form:label>
            </div>
            <div style="float:left; width:20px">
              <form:radiobutton   path="campaignPromotion.discountType" value="${FIXED_AMOUNT}"/>
            </div>
            <div style="float:left; width:120px">
              <form:label  path=""><spring:message code="ui.campaigns.label.create.discount.type.amount.off"/></form:label>
            </div>
          </li>

          <li id="percentOffDiv">
            <form:label path=""><spring:message code="ui.campaigns.label.create.discount.percent"/></form:label>
            <div class="mandatory_wrapper">
              <form:input cssClass="text" id="percentOff"  path="campaignPromotion.percentOff" style="margin:0 5px 0 10px;"/>
            </div>
            <div class="main_addnew_formbox_errormsg add_edit_campaign" id="percentOffError"></div>            
          </li> 
          <li id="amountOffDiv" style="display:none;">
            <div id="supportedCurrencyDiv"></div>
            
          </li>
        </ul>
       </div>

    </form:form>
  </div>

<!--  Add new product ends here-->


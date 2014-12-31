<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="csrf" uri="http://www.owasp.org/index.php/Category:OWASP_CSRFGuard_Project/Owasp.CsrfGuard.tld"%>
<script type="text/javascript">
var campaignsUrl = "<%=request.getContextPath() %>/portal/promotions/";
</script>
<script type="text/javascript">
$('select').change(function(){
//if All is selected with some other channels then it should get deselected. 
  if($("#channelMultiSelectEdit").val().length > 1){
    $("#selectAllEdit").removeAttr("selected");
  }
  percentDiscountType = document.getElementById("campaignPromotion.discountTypeEdit1").checked;
  if (percentDiscountType == false) {
  $('#amountOffDivEdit').hide();
  getCurrencyForSelectedChannel();
  $("#amountOffDivInEditMode").show();
  }
});
</script>

<script type="text/javascript">
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
      dateRange: true
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
      xRemote: {
           condition:function() {
               return  $("#promoCode_old").val() != $("#promoCodeId").val();
                },
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
            digits : true       
          },
          "campaignPromotion.maxAccounts" : {
            required : true,
            min : function(){
              if ($("#unlimitedAccounts").is(':checked'))
                return 0;
              return 1;
            },
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
      dateRange : i18n.campaigns.campaignEnterValidDateRange
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
<style>
#ui-datepicker-div{
  z-index:9999;
}
</style>
<!-- New Edit campaign -->


<div class="dialog_formcontent">
  <div class="details_lightboxtitlebox"></div>
    <div class="details_lightboxformbox add_edit_campaign">
      <spring:url value="/portal/promotions/editcampaign" var="edit_campaign_path" htmlEscape="false" /> 
      <form:form commandName="campaignPromotionsForm"  id="campaignPromotionsForm"  action="${edit_campaign_path}">
        <form:hidden path="campaignPromotion.id"/>
        <input id="campaignPromotion_name" type="hidden" value="<c:out  value="${campaignPromotionsForm.campaignPromotion.code}" />"/>
        <ul> 
          <li>
            <form:label path="campaignPromotion.title" ><spring:message code="ui.campaigns.label.create.title"/></form:label>
            <div class="mandatory_wrapper">
              <form:input cssClass="text"  path="campaignPromotion.title" tabindex="1" cssStyle="margin:0 5px 0 10px;"/>              
            </div>
            <div class="main_addnew_formbox_errormsg add_edit_campaign" id="campaignPromotion.titleError"></div>
          </li> 
          <li>
            <form:label path="" ><spring:message code="ui.campaigns.label.create.promo.code"/></form:label>
            <div class="mandatory_wrapper">
              <form:input cssClass="text" id="promoCodeId" path="promoCode" tabindex="2" readonly="${restrictedEdit}" disabled="${restrictedEdit}" style="margin:0 5px 0 10px;"/>
              <input id="promoCode_old" type="hidden" value="<c:out  value="${campaignPromotionsForm.promoCode}" />"/>
            </div>
             <div class="main_addnew_formbox_errormsg add_edit_campaign" id="promoCodeIdError"></div>
          </li> 
          <li style="width:430px;">
          <form:label path="campaignPromotion.trial" ><spring:message code="ui.campaigns.label.create.trial"/></form:label>
            <div class="mandatory_wrapper" >
              <input type="checkbox" class="checkbox" id="trialPromotion" tabindex="23" style="margin:0 5px 0 10px;" name="trial" <c:if test="${restrictedEdit}"> disabled="true"</c:if> <c:if test="${campaignPromotionsForm.campaignPromotion.trial == true}"> checked</c:if>/>
              <form:input path="campaignPromotion.trial" type="hidden"/>
             </div>                         
          </li>  

          <li style="width:430px;">
          <form:label path="" ><spring:message code="ui.campaigns.label.create.unlimitedduration"/></form:label>
            <div class="mandatory_wrapper" >
              <input type="checkbox" class="checkbox" style="margin:0 5px 0 10px;" id="unlimitedUsage" name="unlimitedUsage" <c:if test="${restrictedEdit}"> disabled="true"</c:if> <c:if test="${campaignPromotionsForm.campaignPromotion.durationDays == 0}"> checked</c:if>/>
             </div>                         
          </li>  
          
          <li  id="liDurationDays" <c:if test="${campaignPromotionsForm.campaignPromotion.durationDays == 0}"> style=display:None </c:if> >
          <c:if test="${campaignPromotionsForm.campaignPromotion.trial == false}">
             <form:label path="" ><spring:message code="ui.campaigns.label.create.duration"/></form:label>
          </c:if>
          <c:if test="${campaignPromotionsForm.campaignPromotion.trial == true}">
             <form:label path="" ><spring:message code="ui.campaigns.label.create.duration.billingperiod"/></form:label>
          </c:if>
            <div class="mandatory_wrapper" >
            <form:input cssClass="text"  id="durationDays" style="margin:0 5px 0 10px;" path="campaignPromotion.durationDays" tabindex="4" readonly="${restrictedEdit}" disabled="${restrictedEdit}"/>
             </div> 
              <div class="main_addnew_formbox_errormsg add_edit_campaign" id="durationDaysError"></div>                        
          </li>  
          
          <li style="width:430px;">
          <form:label path="" ><spring:message code="ui.campaigns.label.create.unlimitedaccount"/></form:label>
            <div class="mandatory_wrapper" >
              <input type="checkbox" class="checkbox" style="margin:0 5px 0 10px;" id="unlimitedAccounts" name="unlimitedAccounts" <c:if test="${restrictedEdit}"> disabled="true"</c:if> <c:if test="${campaignPromotionsForm.campaignPromotion.maxAccounts == 0}"> checked</c:if>/>
             </div>                         
          </li>  
          <li id="liMaxAccounts" <c:if test="${campaignPromotionsForm.campaignPromotion.maxAccounts == 0}"> style=display:None </c:if>>
          <form:label path="campaignPromotion.maxAccounts" ><spring:message code="ui.campaigns.label.create.max.accounts"/></form:label>
            <div class="mandatory_wrapper" >
              <form:input cssClass="text"  style="margin:0 5px 0 10px;" id="maxAccounts" path="campaignPromotion.maxAccounts" readonly="${restrictedEdit}" disabled="${restrictedEdit}"/>
             </div>     
              <div class="main_addnew_formbox_errormsg add_edit_campaign" id="maxAccountsError"></div>                    
          </li>  
          <li>
          <form:label path="campaignPromotion.startDate" ><spring:message code="ui.campaigns.label.create.startdate"/></form:label>
            <div class="mandatory_wrapper" >
               <form:input type="hidden" path="campaignPromotion.startDate"/>
               <input class="text" style="margin:0 5px 0 10px;" id="campaignPromotion.startDateDisplay" name="campaignPromotion.startDateDisplay" tabindex="5" readonly="${restrictedEdit}" disabled="${restrictedEdit}"/>
              </div>
              <div class="main_addnew_formbox_errormsg add_edit_campaign" id="campaignPromotion.startDateError" ></div>
              <div class="main_addnew_formbox_errormsg add_edit_campaign" id="campaignPromotion.startDateDisplayError"></div>
          </li>  
          
          <li>
          <form:label path="campaignPromotion.endDate" ><spring:message code="ui.campaigns.label.create.enddate"/></form:label>
            <div class="nonmandatory_wrapper" >
               <form:input type="hidden" path="campaignPromotion.endDate" />
               <input class="text" style="margin:0 5px 0 10px;" id="campaignPromotion.endDateDisplay" name="campaignPromotion.endDateDisplay" tabindex="6" readonly="readonly"/>
             </div>
             <div class="main_addnew_formbox_errormsg add_edit_campaign" id="campaignPromotion.endDateError"></div>                         
              <div class="main_addnew_formbox_errormsg add_edit_campaign" id="campaignPromotion.endDateDisplayError"></div>
          </li>  

          <li style="width:430px;">
          <form:label path="" ><spring:message code="ui.campaigns.label.create.add.channel"/></form:label>
             <div class="nonmandatory_wrapper">
              &nbsp;&nbsp;
              <form:select id="channelMultiSelectEdit" multiple="multiple" path="channelIdLst" disabled="${restrictedEdit}">
              <option id="selectAllEdit" value=""><spring:message code="ui.campaigns.label.create.channel.type"/></option>
                <c:forEach items="${channels}" var="channel">
                 <c:set var="selectChannelId" value="-1"></c:set>
                  <c:forEach items="${campaignPromotionsForm.campaignPromotion.campaignPromotionsInChannels}" var="campPromoChannel">
                     <c:if test="${channel.id == campPromoChannel.channel.id}">
                        <option value="<c:out value="${channel.id}" />" selected="selected">
                        <c:out value="${channel.name}" /></option>
                        <c:set var="selectChannelId" value="${channel.id}"></c:set>
                      </c:if>
                      </c:forEach> 
                      <c:if test="${channel.id ne selectChannelId}">
                        <option value="<c:out value="${channel.id}" />">
                        <c:out value="${channel.name}" /></option>
                    </c:if>
                </c:forEach>
              </form:select>
            </div>
                                   
          </li>  
          <li style="width:430px;">
          <form:label path="campaignPromotion.endDate" ><spring:message code="ui.campaigns.label.edit.enabled"/></form:label>
            <div class="mandatory_wrapper" >
               <form:hidden path="campaignPromotion.enabled"/>
              <input type="checkbox" class="checkbox" style="margin:0 5px 0 10px;" id="campaignEnabled" value="true"<c:if test="${campaignPromotionsForm.campaignPromotion.enabled}"> checked</c:if>/>
             </div>                         
          </li>  
          <li id="discountTypeRadiosEditDiv">
            <form:label path="" ><spring:message code="ui.campaigns.label.create.discount.type"/></form:label>
           <div class="nonmandatory_wrapper" >
             <div id="discountTypeRadiosEdit" class="db_gridbox_columns" >
              <input id="campaignPromotion.discountTypeEdit" type="hidden" value="<c:out  value="${campaignPromotionsForm.campaignPromotion.discountType}" />"/>
              <div style="float:left; width:20px;">
                <form:radiobutton  id="campaignPromotion.discountTypeEdit1" path="campaignPromotion.discountType" cssClass="discountTypeRadio" value="${PERCENTAGE}"
                readonly="${restrictedEdit}" disabled="${restrictedEdit}"/>
              </div>
              <div style="float:left; width:100px">
                <form:label  path=""><spring:message code="ui.campaigns.label.create.discount.type.percent.off"/></form:label>
              </div>
              <div style="float:left; width:20px">
                <form:radiobutton  id="campaignPromotion.discountTypeEdit2" path="campaignPromotion.discountType" cssClass="discountTypeRadio" value="${FIXED_AMOUNT}" 
                readonly="${restrictedEdit}" disabled="${restrictedEdit}"/>
              </div>
              <div style="float:left; width:100px">
                <form:label  path=""><spring:message code="ui.campaigns.label.create.discount.type.amount.off"/></form:label>
              </div>
             </div>
             </div>
          </li> 

          <li id="percentOffDivEdit" style="display:none">
          <form:label path="campaignPromotion.percentOff" ><spring:message code="ui.campaigns.label.create.discount.percent"/></form:label>
            <div class="mandatory_wrapper" >
              <form:input cssClass="text" cssStyle="margin:0 5px 0 10px;" id="percentOff" path="campaignPromotion.percentOff" tabindex="3" readonly="${restrictedEdit}" disabled="${restrictedEdit}"/>
             </div> 
              <div class="main_addnew_formbox_errormsg add_edit_campaign" id="percentOffError"></div>                        
          </li>  

          <li id="amountOffDivEdit" style="display:none; width:550px;">
            <div class="widget_details_inlinegrid" >
              <div class="widget_grid inline header widget_navtitlebox">
                <span  class="title" style="margin-left:5px;width: auto;color:#FFF;"><spring:message code="ui.campaigns.label.create.discount.amount"></spring:message> 
                </span>
              </div>
              <div class="widget_inline_chargesbox">
                <div class="widget_grid inline subheader">
                  <c:forEach var="entry" items="${campaignPromotionsForm.campaignPromotion.campaignPromotionDiscountAmount}">
                    <div class="widget_grid_cell" style="width:100px;">
                      <div class="widget_flagbox" style="float:left;padding:0;margin:5px 0 0 5px;">
                        <div class="widget_currencyflag">
                          <img src="../../images/flags/<c:out value="${entry.currencyValue.currencyCode}"/>.gif" alt="" />
                        </div>
                      </div>
                      <span class="subheader"><c:out value="${entry.currencyValue.currencyCode}"/>&nbsp;(&nbsp;<c:out value="${entry.currencyValue.sign}" />&nbsp;)</span>
                    </div>
                  </c:forEach>
                </div>
                <div class="<c:out value="widget_grid inline odd"/>">
                  <c:forEach var="amount" items="${campaignPromotionsForm.campaignPromotion.campaignPromotionDiscountAmount}">
                    <div class="widget_grid_cell" style="width:100px;">
                       <div class="mandatory_wrapper" style="margin:5px 0 0 0;">
                        <input class="text priceRequired j_pricerequired" style="width:70%;margin:5px 5px 5px 5px;"
                          id="discountAmountMap[<c:out value='${amount.currencyValue.currencyCode}'/>]"
                          name="discountAmountMap[<c:out value='${amount.currencyValue.currencyCode}'/>]"
                          value="<c:out value="${amount.discount}"/>"
                          readonly="${restrictedEdit}" disabled="${restrictedEdit}"
                           />
                      </div>
                    </div>
                  </c:forEach>
                </div> 
              </div>
          </div>
            <div class="main_addnew_formbox_errormsg" id="discountAmountMapError" style="display:none" style="margin:10px 0 0 5px">
              <form:label path=""><spring:message code="js.errors.campaigns.amountoff.required"/></form:label>
            </div>   
          </li>  
          <li id="amountOffDivInEditMode" style="display:none;">
            <div id="supportedCurrencyEditDiv"></div>
          </li>
          
  </ul>
<input id="campPromotionState" type="hidden" value="<c:out  value="${campaignPromotionsForm.campaignPromotion.state}" />"/>
  
</form:form>
</div>
</div>
<!--  Add ProductBundle ends here-->



<!-- End of new edit campaign -->



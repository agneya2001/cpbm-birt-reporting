<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<spring:message code="dateonly.short.format" var="dateonly_format"/>
<fmt:formatDate var="displayPlanDate" value="${plannedDate}" pattern="${dateonly_format}"  />

<script language="javascript">

if( typeof commmonmessages === 'undefined' ) {
  var commmonmessages = {};
}
commmonmessages = {
    lightboxbuttoncancel: '<spring:message javaScriptEscape="true" code="label.cancel"/>',  
    lightboxbuttonconfirm: '<spring:message javaScriptEscape="true" code="label.confirm"/>',
    removingplannedcharges: '<spring:message  javaScriptEscape="true" code="message.removing.planned.charges"/>',
    lightboxremoveplanneddate1: '<spring:message  javaScriptEscape="true" code="js.confirm.removePlanneddate1"/>',    
    removeplanneddate: '<spring:message javaScriptEscape="true" code="label.default.catalog.remove.plan.date"/>',
    removingplanneddate: '<spring:message javaScriptEscape="true" code="message.removing.planned.date"/>' ,
    failed_to_remove_planned_date:'<spring:message javaScriptEscape="true" code="js.errors.products.failed.to.remove.planned.date"/>',
    failed_to_remove_planned_charges:'<spring:message javaScriptEscape="true" code="js.errors.products.failed.to.remove.planned.charges"/>',
    failed_set_plan_date:'<spring:message javaScriptEscape="true" code="js.error.product.set.plan.date.failed"/>',
    removedplannedcharges_successfully: '<spring:message javaScriptEscape="true" code="js.products.remove.planned.charges.successfully"/>',
    removedplanneddate_successfully: '<spring:message javaScriptEscape="true" code="js.products.remove.planned.date.successfully"/>',
    startDate:'<spring:message javaScriptEscape="true" code="js.errors.products.enter.valid.start.date"/>',
    activated_successfully: '<spring:message javaScriptEscape="true" code="js.products.ratecard.activated.successfully"/>',
    activating: '<spring:message javaScriptEscape="true" code="message.activating.charges"/>',
    choose_label: '<spring:message javaScriptEscape="true" code="label.choose"/>',
    editChannelBranding:'<spring:message javaScriptEscape="true" code="label.edit.channel.branding"/>',
    editChannelBrandingSuccess:'<spring:message javaScriptEscape="true" code="message.edit.channel.branding.success"/>',
    resetChannelBrandingSuccess:'<spring:message javaScriptEscape="true" code="message.reset.channel.branding.success"/>',
    channel_add_success: '<spring:message javaScriptEscape="true" code="label.channel.add.success"/>',
    channel_edit_success: '<spring:message javaScriptEscape="true" code="label.channel.edit.success"/>',
    channel_add_error: '<spring:message javaScriptEscape="true" code="label.channel.add.error"/>',
    channel_edit_error: '<spring:message javaScriptEscape="true" code="label.channel.edit.error"/>',
    labelDefault:'<spring:message javaScriptEscape="true" code="label.default"/>',
    labelDownloadCss:'<spring:message javaScriptEscape="true" code="label.download.css"/>',
    labelNone:'<spring:message javaScriptEscape="true" code="label.none"/>',
    dialogAddChannelTitle:'<spring:message javaScriptEscape="true" code="label.channel.add"/>',
    dialogEditChannelTitle:'<spring:message javaScriptEscape="true" code="label.channel.edit"/>',
    noUrlSet:'<spring:message javaScriptEscape="true" code="message.no.url.set"/>'
};

var i18n = {
  errors: {
    catalog: {
      createNewCatalog : '<spring:message javaScriptEscape="true" code="js.errors.catalog.createNewCatalog"/>',
      name : '<spring:message javaScriptEscape="true" code="js.errors.catalog.name"/>',
      currency : '<spring:message javaScriptEscape="true" code="js.errors.catalog.currency"/>',
      code : '<spring:message javaScriptEscape="true" code="js.errors.catalog.code"/>',
      catlogcodevalid : '<spring:message javaScriptEscape="true" code="js.errors.catalog.catlogcodevalid"/>',
      editCatalog : '<spring:message javaScriptEscape="true" code="js.errors.catalog.editCatalog"/>',
      addNewCatalog : '<spring:message javaScriptEscape="true" code="js.errors.catalog.addNewCatalog"/>',
      removeCatalog : '<spring:message javaScriptEscape="true" code="js.errors.catalog.removeCatalog"/>'
    },
    ratecards: {
        planning_ratecard : '<spring:message javaScriptEscape="true" code="js.errors.ratecards.error.planning.ratecard"/>',
        submitting_rate_card: '<spring:message javaScriptEscape="true" code="js.errors.ratecards.error.submitting.rate.card"/>',
        deleting_rate_card: '<spring:message javaScriptEscape="true" code="js.errors.ratecards.error.deleting.rate.card"/>',
        editting_rate_card: '<spring:message javaScriptEscape="true" code="js.errors.ratecards.error.editting.rate.card"/>'
      }
    ,
    products: {
    	failed_create_product: '<spring:message javaScriptEscape="true" code="js.errors.product.failed.create.product"/>',
    	enter_valid_value: '<spring:message javaScriptEscape="true" code="js.errors.priceRequired"/>',
      max_four_decimal_value: '<spring:message javaScriptEscape="true" arguments="${currencyFractionalDigitsLimit}"  code="js.errors.maxFourDecimalPrice"/>',
    	name: '<spring:message javaScriptEscape="true" code="js.errors.product.name"/>',
        displayname: '<spring:message javaScriptEscape="true" code="js.errors.product.displayname"/>',
        code: '<spring:message javaScriptEscape="true" code="js.errors.product.code"/>',
        productCodeValid: '<spring:message javaScriptEscape="true" code="js.errors.product.productCodeValid"/>',
    	product_type: '<spring:message javaScriptEscape="true" code="js.errors.product.product.type"/>',
    	product_uom: '<spring:message javaScriptEscape="true" code="js.errors.product.product.uom"/>',
    	product_usage_type: '<spring:message javaScriptEscape="true" code="js.errors.product.product.usage.type"/>',
    	usage_type_exclude: '<spring:message javaScriptEscape="true" code="js.errors.product.product.usage.type.exclude"/>',
    	componentIdso: '<spring:message javaScriptEscape="true" code="js.errors.product.service.offering"/>',
    	componentId: '<spring:message javaScriptEscape="true" code="js.errors.product.component.id"/>',
    	componentTag: '<spring:message javaScriptEscape="true" code="js.errors.product.component.tag"/>',
    	templateIso : '<spring:message javaScriptEscape="true" code="js.errors.product.template.iso"/>',
    	chargeType: '<spring:message javaScriptEscape="true" code="js.errors.product.charge.type"/>',
    	price: '<spring:message javaScriptEscape="true" code="js.errors.product.number"/>',
    	failed_edit_product: '<spring:message javaScriptEscape="true" code="js.errors.product.failed.edit.product"/>',
      delete_confirm_message:'<spring:message javaScriptEscape="true" code="js.errors.product.delete.product.confirm.message"/>',
      edit_image_path_invalid_message:'<spring:message javaScriptEscape="true" code="ui.product.image.error.invalid.path"/>',
      failed_upload_image:'<spring:message javaScriptEscape="true" code="ui.image.failed"/>',
      pricesNotSetForAllProducts: '<spring:message javaScriptEscape="true" code="js.errors.prices.not.set.for.all.products"/>',
      editplanneddatesuccess: '<spring:message javaScriptEscape="true" code="js.edit.planned.date.success"/>',
      activationdategreaterorequaltotoday: '<spring:message javaScriptEscape="true" code="js.rpb.activation.error.date.greater.than.equal.to.today"/>',
      activationdategreaterthantoday: '<spring:message javaScriptEscape="true" code="js.rpb.activation.error.date.greater.than.today"/>',
      noproductsadded: '<spring:message javaScriptEscape="true" code="js.rpb.activation.error.no.product.in.future.plan"/>',
      errorChooseOption: '<spring:message javaScriptEscape="true" code="message.error.choose.option"/>'
      
    },
    channels: {
      failed_create_channel: '<spring:message javaScriptEscape="true" code="js.errors.channel.failed.create.channel"/>',
      failed_edit_channel: '<spring:message javaScriptEscape="true" code="js.errors.channel.failed.edit.channel"/>',
      name: '<spring:message javaScriptEscape="true" code="js.errors.channel.name"/>',
      code: '<spring:message javaScriptEscape="true" code="js.errors.channel.code"/>',
      channel_name_not_unique: '<spring:message javaScriptEscape="true" code="js.errors.channel.name.notunique"/>',
      channel_code_not_unique: '<spring:message javaScriptEscape="true" code="js.errors.channel.code.notunique"/>',
      channel_fqdn_not_unique: '<spring:message javaScriptEscape="true" code="js.errors.channel.fqdn.prefix.notunique"/>',
      failed_delete_channel: '<spring:message javaScriptEscape="true" code="js.errors.channel.failed.delete.channel"/>',
      min_length_required:"<spring:message javaScriptEscape="true" code='js.errors.channel.length.lowerLimit'/>",
      max_length_exceeded:"<spring:message javaScriptEscape="true" code='js.errors.channel.length.upperLimit'/>",
      code_invalid:"<spring:message javaScriptEscape="true" code='js.errors.channel.catalogcode.invalid'/>",
      fqdn_invalid:"<spring:message javaScriptEscape="true" code='js.errors.channel.fqdn.prefix.invalid'/>",
      name_invalid:"<spring:message javaScriptEscape="true" code='js.errors.channel.name.invalid'/>",
      channel_currency_required:'<spring:message javaScriptEscape="true" code="js.errors.catalog.currency"/>',
      help_desk_phone:'<spring:message javaScriptEscape="true" code="js.errors.help_desk_phone"/>',
      help_desk_email:'<spring:message javaScriptEscape="true" code="js.errors.help_desk_email"/>',
      domainemails:'<spring:message javaScriptEscape="true" code="js.errors.domainemails"/>',
      url:'<spring:message javaScriptEscape="true" code="js.errors.channel.url"/>', 
      validPriceRequired: '<spring:message javaScriptEscape="true" code="js.errors.channel.price.error"/>',
      max_four_decimal_value: '<spring:message javaScriptEscape="true" arguments="${currencyFractionalDigitsLimit}"  code="js.errors.maxFourDecimalPrice"/>',
      edit_image_path_invalid_message: '<spring:message javaScriptEscape="true" code="ui.image.error.invalid.path"/>',
      file_path_invalid_message: '<spring:message javaScriptEscape="true" code="ui.error.file.invalid.path"/>',
      failed_upload_image: '<spring:message javaScriptEscape="true" code="ui.image.failed"/>',
      products_bundles_not_activated: '<spring:message javaScriptEscape="true" code="js.error.products.bundles.not.activated"/>',
      failed_create_channel_precondition: '<spring:message javaScriptEscape="true" code="js.errors.channel.failed.create.channel_precondition"/>',
      failed_to_sync_channel: '<spring:message javaScriptEscape="true" code="js.errors.channel.failed.sync"/>',
      failed_upload_file: '<spring:message javaScriptEscape="true" code="message.failed.to.upload.file"/>',
      max_length_billingGroup: '<spring:message javaScriptEscape="true" code="js.errors.length.upperLimit"/>'
    },
    common:{
      codeNotUnique: '<spring:message javaScriptEscape="true" code="js.errors.common.CodeNotUnique"/>',
      uncompatible_currency_precision: '<spring:message javaScriptEscape="true" code="js.error.uncompatible.currency.precision"/>',
      reportEnterValidDateRange : '<spring:message javaScriptEscape="true" code="js.errors.campaigns.campaignEnterValidDateRange"/>',
      validationStartDate   : '<spring:message javaScriptEscape="true" code="js.report.errors.validationStartDate"/>',
      validationEndDate     : '<spring:message javaScriptEscape="true" code="js.report.errors.validationEndDate"/>'
     
    }
  },
  campaigns: {      
    failed_create_campaign : '<spring:message javaScriptEscape="true" code="js.errors.campaign.failed.create.campaign"/>',
    startdaterequired : '<spring:message javaScriptEscape="true" code="js.errors.campaigns.startdate.required"/>',
    enddaterequired : '<spring:message javaScriptEscape="true" code="js.errors.campaigns.enddate.required"/>',
    campaigncoderequired : '<spring:message javaScriptEscape="true" code="js.errors.campaigns.campaigncode.required"/>',
    promocodeexists : '<spring:message javaScriptEscape="true" code="js.errors.campaigns.promocode.exists"/>',
    promotitlerequired : '<spring:message javaScriptEscape="true" code="js.errors.campaigns.description.required"/>',
    promocoderequired : '<spring:message javaScriptEscape="true" code="js.errors.campaigns.promocode.required"/>',
    durationrequired : '<spring:message javaScriptEscape="true" code="js.errors.campaigns.duration.required"/>',
    maxaccountsrequired : '<spring:message javaScriptEscape="true" code="js.errors.campaigns.maxaccounts.required"/>',
    percentoffrequired : '<spring:message javaScriptEscape="true" code="js.errors.campaigns.percentoff.required"/>',
    amountoffrequired : '<spring:message javaScriptEscape="true" code="js.errors.campaigns.amountoff.required"/>',
    percentoffrange : '<spring:message javaScriptEscape="true" code="js.errors.campaigns.percentoff.range"/>',
    digitsrequired : '<spring:message javaScriptEscape="true" code="js.errors.campaigns.digits.required"/>',
    digitsmin : '<spring:message javaScriptEscape="true" code="js.errors.campaigns.digits.min"/>',
    promocodeinvalid : '<spring:message javaScriptEscape="true" code="i18n.errors.campaigns.promocodeinvalid"/>',
    campaignEnterValidDateRange : '<spring:message javaScriptEscape="true" code="js.errors.campaigns.campaignEnterValidDateRange"/>',
    enterValidDate : '<spring:message javaScriptEscape="true" code="js.errors.startDateField.date"/>'
  },
  label: {
    catalog : {
      addCatalog: '<spring:message javaScriptEscape="true" code="ui.products.label.create.add.catalog"/>',
      saving : '<spring:message javaScriptEscape="true" code="js.label.catalog.saving"/>',
      manage_bundle : '<spring:message javaScriptEscape="true" code="ui.products.label.view.catalog.manage.bundle"/>',
      urc_title: '<spring:message javaScriptEscape="true" code="ui.products.label.view.catalog.urc"/>',
      remove: '<spring:message javaScriptEscape="true" code="ui.products.label.view.remove"/>',
      Edit: '<spring:message javaScriptEscape="true" code="ui.products.label.view.edit"/>'
    },
    ratecards : {
    	future_rate_card : '<spring:message javaScriptEscape="true" code="label.bundle.list.rate.card.future.title"/>',
    	plan_rate_card : '<spring:message javaScriptEscape="true" code="label.bundle.list.ratecard.plan.title"/>',
    	effectiva_date : '<spring:message javaScriptEscape="true" code="label.bundle.list.ratecard.effective.date"/>',
    	edit : '<spring:message javaScriptEscape="true" code="ui.products.label.view.edit"/>',
      delete_rate_card : '<spring:message javaScriptEscape="true" code="label.bundle.list.entitlement.delete"/>'
      }
    ,
    products : {
      	AddProduct: '<spring:message javaScriptEscape="true" code="ui.products.label.create.add.product"/>',
    	Details: '<spring:message javaScriptEscape="true" code="ui.products.label.view.details"/>',
    	Name: '<spring:message javaScriptEscape="true" code="ui.products.label.create.name"/>',
    	Couldstack_ID: '<spring:message javaScriptEscape="true" code="js.errors.product.cloud.stack.id"/>',
    	Stand_Alone: '<spring:message javaScriptEscape="true" code="js.errors.product.stand.alone"/>',
    	Display_Name: '<spring:message javaScriptEscape="true" code="ui.products.label.create.product.displayname"/>',
    	Type: '<spring:message javaScriptEscape="true" code="ui.products.label.create.product.type"/>',
    	Hypervisor: '<spring:message javaScriptEscape="true" code="label.hypervisor"/>',
    	Edit: '<spring:message javaScriptEscape="true" code="ui.products.label.view.edit"/>',
    	Remove: '<spring:message javaScriptEscape="true" code="ui.products.label.view.remove"/>',
    	Templates: '<spring:message javaScriptEscape="true" code="js.errors.product.templates"/>',
    	ISOs: '<spring:message javaScriptEscape="true" code="js.errors.product.isos"/>',
    	discrete : '<spring:message javaScriptEscape="true" code="ui.products.label.discrete"/>',
    	usagecombine:'<spring:message javaScriptEscape="true" code="ui.label.combine" />',
    	usageexclude:'<spring:message javaScriptEscape="true" code="ui.label.exclude" />',
      type: {
        VOLUME: '<spring:message javaScriptEscape="true" code="product.type.VOLUME"/>',
        SECONDARY_STORAGE: '<spring:message javaScriptEscape="true" code="product.type.SECONDARY_STORAGE"/>',
        NETWORK_BYTES: '<spring:message javaScriptEscape="true" code="product.type.NETWORK_BYTES"/>',
        IP_ADDRESS: '<spring:message javaScriptEscape="true" code="product.type.IP_ADDRESS"/>',
        PORT_FORWARDING_RULE: '<spring:message javaScriptEscape="true" code="product.type.PORT_FORWARDING_RULE"/>',
        LOAD_BALANCER_POLICY: '<spring:message javaScriptEscape="true" code="product.type.LOAD_BALANCER_POLICY"/>',
        RUNNING_VM: '<spring:message javaScriptEscape="true" code="product.type.RUNNING_VM"/>',
        HYPERVISOR: '<spring:message javaScriptEscape="true" code="product.type.HYPERVISOR"/>',
        ISO_GROUP: '<spring:message javaScriptEscape="true" code="product.type.ISO_GROUP"/>',
        TEMPLATE_GROUP: '<spring:message javaScriptEscape="true" code="product.type.TEMPLATE_GROUP"/>',
        STOPPED_VM: '<spring:message javaScriptEscape="true" code="product.type.STOPPED_VM"/>'
      }
    },
    campaigns :{
        Details: '<spring:message javaScriptEscape="true" code="ui.campaigns.label.view.details"/>',
        Code: '<spring:message javaScriptEscape="true" code="ui.campaigns.label.create.code"/>',
        Title: '<spring:message javaScriptEscape="true" code="ui.campaigns.label.create.title"/>',
        Promo_Code: '<spring:message javaScriptEscape="true" code="ui.campaigns.label.create.promo.code"/>',
        Duration: '<spring:message javaScriptEscape="true" code="ui.campaigns.label.create.duration"/>',
        startdate: '<spring:message javaScriptEscape="true" code="ui.campaigns.label.create.startdate"/>',
        enddate: '<spring:message javaScriptEscape="true" code="ui.campaigns.label.create.enddate"/>',
        Edit: '<spring:message javaScriptEscape="true" code="ui.campaigns.label.view.edit"/>',
        Type: '<spring:message javaScriptEscape="true" code="ui.label.type.subtitle"/>',
        TimeOff: '<spring:message javaScriptEscape="true" code="ui.campaigns.label.campaign.type.time.off"/>',
        MoneyOff: '<spring:message javaScriptEscape="true" code="ui.campaigns.label.campaign.type.money.off"/>',
        Channel: '<spring:message javaScriptEscape="true" code="ui.campaigns.label.create.add.channel"/>'
    }  
  },
  confirm : {
    catalog : {
      removeCatalog : '<spring:message javaScriptEscape="true" code="js.confirm.catalog.removeCatalog"/>'
    }
  }  
};
</script>

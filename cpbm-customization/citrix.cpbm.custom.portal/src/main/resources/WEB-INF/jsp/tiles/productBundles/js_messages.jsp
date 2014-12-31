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
    lightboxremoveentitlement: '<spring:message  javaScriptEscape="true" code="js.confirm.removeEntitlement"/>',
    lightboxremoveentitlementTitle: '<spring:message  javaScriptEscape="true" code="js.confirm.removeEntitlementTitle"/>',
    lightboxactivenowconfirmmessage:'<spring:message javaScriptEscape="true" code="js.confirm.activatenow"/>',
    activatenow:'<spring:message javaScriptEscape="true" code="label.activate.now"/>',
    activated_successfully: '<spring:message javaScriptEscape="true" code="js.products.ratecard.activated.successfully"/>',
    activating: '<spring:message javaScriptEscape="true" code="message.activating.charges"/>',
    editplanneddatesuccess: '<spring:message javaScriptEscape="true" code="js.edit.planned.date.success"/>',
    choose_label: '<spring:message javaScriptEscape="true" code="label.choose"/>',
    service_bundle: '<spring:message javaScriptEscape="true" code="bundle.type.service"/>',
    SINGLETON: '<spring:message javaScriptEscape="true" code="label.bundle.business.constraint.SINGLETON"/>',
    ACCOUNT: '<spring:message javaScriptEscape="true" code="label.bundle.business.constraint.ACCOUNT"/>',
    PER_USER: '<spring:message javaScriptEscape="true" code="label.bundle.business.constraint.PER_USER"/>',
    NONE: '<spring:message javaScriptEscape="true" code="label.bundle.business.constraint.NONE"/>',
    MORE: '<spring:message javaScriptEscape="true" code="label.more"/>',
    fecthingDetails: '<spring:message javaScriptEscape="true" code="bundle.provisioning.fetching.details"/>',
    errorFecthingDetails: '<spring:message javaScriptEscape="true" code="bundle.provisioning.error.fetching.details"/>',
    resourceComponentNotAvailable: '<spring:message javaScriptEscape="true" code="resource.component.not.available"/>',
    LESS: '<spring:message javaScriptEscape="true" code="label.less"/>'
};


if( typeof i18n === 'undefined' ) {
    var i18n = {};
  }
i18n = {
	    errors: {
        bundleSelectProduct  : '<spring:message javaScriptEscape="true" code="js.errors.bundle.select.product"/>',
        edit_image_path_invalid_message:'<spring:message javaScriptEscape="true" code="ui.product.image.error.invalid.path"/>',
        bundleProvideIncludedUnits  : '<spring:message javaScriptEscape="true" code="js.errors.bundle.provide.included.units"/>', 
        bundleDeletebundleConfirmation  : '<spring:message javaScriptEscape="true" code="js.errors.bundle.deletebundle.confirmation"/>',
        bundleunpublishConfirmation  : '<spring:message javaScriptEscape="true" code="js.errors.bundle.unpublish.confirmation"/>',
        bundlePublishConfirmation  : '<spring:message javaScriptEscape="true" code="js.errors.bundle.publishbundle.confirmation"/>',
        bundleNotPublishNoCurrentCharges  : '<spring:message javaScriptEscape="true" code="js.errors.bundle.publishbundle.no.current.charges"/>',
        bundleNotPublishedError  : '<spring:message javaScriptEscape="true" code="js.errors.bundle.publishbundle.error"/>',
        bundleDeletebundleFailure  : '<spring:message javaScriptEscape="true" code="js.errors.bundle.deletebundle.failure"/>', 
        bundleDeleteentitlementFailed  : '<spring:message javaScriptEscape="true" code="js.errors.bundle.deleteentitlement.failed"/>', 
        bundleIncludedUnitsRequired  : '<spring:message javaScriptEscape="true" code="js.errors.bundle.included.units.required"/>', 
        bundleIncludedUnitsPositiveInteger : '<spring:message javaScriptEscape="true" code="js.errors.positive.integer"/>',
        bundleIncludedUnitsPositiveJavaInteger : '<spring:message javaScriptEscape="true" code="js.errors.positive.java.integer"/>',
        bundleEntitlementReset  : '<spring:message javaScriptEscape="true" code="js.errors.bundle.entitlement.reset"/>', 
        bundleBundleFormProvideName  : '<spring:message javaScriptEscape="true" code="js.errors.bundle.bundle.form.provide.name"/>', 
        bundlecodevalid  : '<spring:message javaScriptEscape="true" code="js.errors.bundle.bundle.form.provide.bundlecodevalid"/>',
        bundleBundleFormProvideCode  : '<spring:message javaScriptEscape="true" code="js.errors.bundle.bundle.form.provide.code"/>',
        bundleBundleFormAlreadyExists  : '<spring:message javaScriptEscape="true" code="js.errors.bundle.bundle.form.already.exists"/>', 
        bundleBundleFormError  : '<spring:message javaScriptEscape="true" code="js.errors.bundle.bundle.form.error"/>', 
        bundleRateCardStartDateRequired  : '<spring:message javaScriptEscape="true" code="js.errors.bundle.rate.card.start.date.required"/>', 
        bundleEnterValidIncludedUnits  : '<spring:message javaScriptEscape="true" code="js.errors.bundle.enter.valid.included.units"/>',
        max_four_decimal_value: '<spring:message javaScriptEscape="true" arguments="${currencyFractionalDigitsLimit}"  code="js.errors.maxFourDecimalPrice"/>',
        bundleEnterValidValue  : '<spring:message javaScriptEscape="true" code="js.errors.bundle.enter.valid.value"/>', 
        bundleEnterValidDateRange  : '<spring:message javaScriptEscape="true" code="js.errors.bundle.enter.valid.date.range"/>', 
        bundleEnterValidStartDate  : '<spring:message javaScriptEscape="true" code="js.errors.bundle.enter.valid.start.date"/>', 
        bundleEnterValidEndDate  : '<spring:message javaScriptEscape="true" code="js.errors.bundle.enter.valid.end.date"/>', 
        max_twodecimal_price  : '<spring:message javaScriptEscape="true" code="js.errors.bundle.bundle.edit.number.with.max.of.twodecimals"/>',
        decimal_range:'<spring:message javaScriptEscape="true" code="js.errors.bundle.bundle.price.range"/>',
        bundleBundleCreationFailed  : '<spring:message javaScriptEscape="true" code="js.errors.bundle.bundle.creation.failed"/>', 
        bundleEntitlementFilterDate  : '<spring:message javaScriptEscape="true" code="js.errors.bundle.entitlement.filter.date"/>', 
        bundleBundleSaveFailed  : '<spring:message javaScriptEscape="true" code="js.errors.bundle.bundle.save.failed"/>', 
        bundleBundleEditFailed  : '<spring:message javaScriptEscape="true" code="js.errors.bundle.bundle.edit.failed"/>',
        bundleRateCardEndDateRequired  : '<spring:message javaScriptEscape="true" code="js.errors.bundle.bundle.enter.end.date"/>',
        failed_upload_image:'<spring:message javaScriptEscape="true" code="ui.image.failed"/>',
        codeNotUnique: '<spring:message javaScriptEscape="true" code="js.errors.common.CodeNotUnique"/>',
        price: '<spring:message javaScriptEscape="true" code="js.errors.product.number"/>',
        validPriceRequired: '<spring:message javaScriptEscape="true" code="js.errors.priceRequired"/>',
        planchargesfailed: '<spring:message javaScriptEscape="true" code="js.errors.bundles.plan.charges.failed"/>',
        failed_to_remove_planned_charges:'<spring:message javaScriptEscape="true" code="js.errors.products.failed.to.remove.planned.charges"/>',
        removingplannedcharges: '<spring:message javaScriptEscape="true" code="message.removing.planned.charges"/>',
        removedplannedcharges_successfully: '<spring:message javaScriptEscape="true" code="js.products.remove.planned.charges.successfully"/>',
        lightboxbuttoncancel: '<spring:message javaScriptEscape="true" code="label.cancel"/>',  
        lightboxbuttonconfirm: '<spring:message javaScriptEscape="true" code="label.confirm"/>', 
        bundletyperequired: '<spring:message javaScriptEscape="true" code="js.error.bundle.type.required"/>',
        bundleresourcetyperequired: '<spring:message javaScriptEscape="true" code="js.error.bundle.resource.type.required"/>',
        bundlebusinessconstraintrequired: '<spring:message javaScriptEscape="true" code="js.error.bundle.business.constraint.required"/>',
        bundlechargetyperequired: '<spring:message javaScriptEscape="true" code="js.error.bundle.charge.type.required"/>',
        provisioningConstraintRequired: '<spring:message javaScriptEscape="true" code="js.error.bundle.provisioning.constraint.required"/>',
        maxlength: '<spring:message javaScriptEscape="true" code="js.errors.length.upperLimit"/>',
        uncompatible_currency_precision: '<spring:message javaScriptEscape="true" code="js.error.uncompatible.currency.precision"/>',
        pricesNotSetForAllProducts: '<spring:message javaScriptEscape="true" code="js.errors.prices.not.set.for.all.products"/>',
        ratecards: {
            planning_ratecard : '<spring:message javaScriptEscape="true" code="js.errors.ratecards.error.planning.ratecard"/>',
            submitting_rate_card: '<spring:message javaScriptEscape="true" code="js.errors.ratecards.error.submitting.rate.card"/>',
            deleting_rate_card: '<spring:message javaScriptEscape="true" code="js.errors.ratecards.error.deleting.rate.card"/>',
            editting_rate_card: '<spring:message javaScriptEscape="true" code="js.errors.ratecards.error.editting.rate.card"/>'
          },
	    entitilement : {
        	    alreadyadded : '<spring:message javaScriptEscape="true" code="js.errors.bundle.entitlement.product.already.added"/>'
        },
        nullexclusionerror: '<spring:message javaScriptEscape="true" code="js.errors.bundle.bundle.excludes.no.component"/>',
        serviceinstancerequired: '<spring:message javaScriptEscape="true" code="js.errors.bundle.bundle.service.instance.required"/>'
	},
	 label: {	   
	    productbundle : {
	      Details: '<spring:message javaScriptEscape="true" code="ui.products.label.view.details"/>',
	      Name: '<spring:message javaScriptEscape="true" code="ui.products.label.create.name"/>',
	      description: '<spring:message javaScriptEscape="true" code="ui.products.label.create.description"/>',
	      servicing_offering_id: '<spring:message javaScriptEscape="true" code="ui.products.label.second.level.productbundle.service.offering.id"/>',
	      Display_Name: '<spring:message javaScriptEscape="true" code="ui.products.label.create.product.displayname"/>',
	      Type: '<spring:message javaScriptEscape="true" code="ui.products.label.create.product.type"/>',
	      Hypervisor: '<spring:message javaScriptEscape="true" code="label.hypervisor"/>',
	      Edit: '<spring:message javaScriptEscape="true" code="ui.products.label.view.edit"/>',
	      Remove: '<spring:message javaScriptEscape="true" code="ui.products.label.view.remove"/>',
	      Templates: '<spring:message javaScriptEscape="true" code="js.errors.product.templates"/>',
	      ISOs: '<spring:message javaScriptEscape="true" code="js.errors.product.isos"/>',	
	      NONE: '<spring:message javaScriptEscape="true" code="charge.type.NONE"/>',      
	      MONTHLY: '<spring:message javaScriptEscape="true" code="charge.type.MONTHLY"/>',
	      ANNUAL: '<spring:message javaScriptEscape="true" code="charge.type.ANNUAL"/>',
	      QUARTERLY: '<spring:message javaScriptEscape="true" code="charge.type.QUARTERLY"/>',
	      FalseText: '<spring:message javaScriptEscape="true" code="label.false"/>',
	      TrueText: '<spring:message javaScriptEscape="true" code="label.true"/>',
	      unlimited: '<spring:message javaScriptEscape="true" code="label.bundle.list.entitlement.unlimited"/>',
	      published: '<spring:message javaScriptEscape="true" code="label.bundle.published"/>',
	      unpublished: '<spring:message javaScriptEscape="true" code="label.bundle.unpublished.successfully"/>'
	      },
	      ratecards : {
	        future_rate_card : '<spring:message javaScriptEscape="true" code="label.bundle.list.rate.card.future.title"/>',
	        plan_rate_card : '<spring:message javaScriptEscape="true" code="label.bundle.list.ratecard.plan.title"/>',
	        effectiva_date : '<spring:message javaScriptEscape="true" code="label.bundle.list.ratecard.effective.date"/>',
	        edit : '<spring:message javaScriptEscape="true" code="ui.products.label.view.edit"/>',
	        delete_rate_card : '<spring:message javaScriptEscape="true" code="label.bundle.list.entitlement.delete"/>'
	        }
	  } 

 
};

var i10associationTypes = new Array();
i10associationTypes['INCLUDES']='<spring:message javaScriptEscape="true" code="ui.label.includes"/>';
i10associationTypes['EXCLUDES']='<spring:message javaScriptEscape="true" code="ui.label.excludes"/>';
</script>
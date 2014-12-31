/*
*  Copyright � 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/
$(document).ready(function() {
  $(".cc_icon").click(function() {
    if($(this).attr('class').indexOf("edit_page")!=-1 && $("#enableEdit").val()=="false"){
      return;
    }
    $("#creditCard\\.creditCardType").val($(this).attr('id'));
    $(".cc_icon").removeClass("active").addClass("nonactive");
    $(this).removeClass("nonactive").addClass("active");
   });
  
  if ($("#showPrimaryAddressOnEditCreditCard").val()=="true") {
    $('#creditCard\\.creditCardAddress\\.country').val($('#primaryAddressCountry').val());
    $('#creditCard\\.creditCardAddress\\.state').val($('#primaryAddressState').val());
    $('#creditCard\\.creditCardAddress\\.city').val($('#primaryAddressCity').val());
    $('#creditCard\\.creditCardAddress\\.street1').val($('#primaryAddressStreet').val());
    $('#creditCard\\.creditCardAddress\\.postalCode').val($('#primaryAddressPostalCode').val());
    if ($("#secondaryAddressPresent").val()=="true") {
     $("#syncAddresslifromeditcreditcard").show();
    }
  }
  
  $("#syncAddressEditCreditCard").live("click", function(event){
    if ($(this).is(':checked')) {
      $('#creditCard\\.creditCardAddress\\.country').val($('#secondaryAddressCountry').val());
      $('#creditCard\\.creditCardAddress\\.country').change();
      $('#billingAddressStateSelect').val($('#secondaryAddressState').val());
      $('#creditCard\\.creditCardAddress\\.state').val($('#secondaryAddressState').val());
      $('#creditCard\\.creditCardAddress\\.city').val($('#secondaryAddressCity').val());
      $('#creditCard\\.creditCardAddress\\.street1').val($('#secondaryAddressStreet').val());
      $('#creditCard\\.creditCardAddress\\.postalCode').val($('#secondaryAddressPostalCode').val());
      
    }
    else{
      $('#creditCard\\.creditCardAddress\\.country').val('');
      $('#creditCard\\.creditCardAddress\\.country').change();
      $('#billingAddressStateSelect').val('');
      $('#creditCard\\.creditCardAddress\\.state').val('');
      $('#creditCard\\.creditCardAddress\\.city').val('');
      $('#creditCard\\.creditCardAddress\\.street1').val('');
      $('#creditCard\\.creditCardAddress\\.postalCode').val('');
      
    }
    });
	
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
	$.validator.addClassRules("priceRequired", {
		twoDecimal : true
	});	
	$.validator
			.addMethod(
					"twoDecimal",
					function(value, element) {
						$(element).rules("add", {
							number : true
							
						});		
						isPriceValid = value != "" && isNaN(value) == false && value>0 && value <= 99999999.99;
						
						if (isPriceValid == false ){
							return false;
						}
						return true;
					},
					i18n.errors.priceRequired);
	
	if($('#billingAddressForm #creditCard\\.creditCardAddress\\.country').val()=='US' || $('#billingAddressForm #creditCard\\.creditCardAddress\\.country').val() == 'CA' 
		|| $('#billingAddressForm #creditCard\\.creditCardAddress\\.country').val() == 'AU' || $('#billingAddressForm #creditCard\\.creditCardAddress\\.country').val() == 'IN' || $('#billingAddressForm #creditCard\\.creditCardAddress\\.country').val() == 'JP'){
	      $("#billingAddressForm  #billingStateInput").hide();
	      $("#billingAddressForm #creditCard\\.creditCardAddress\\.country").defautlinkstates("#billingAddressStateSelect"); 
	      $("#billingAddressForm #billingAddressStateSelect").val($('#billingAddressForm #creditCard\\.creditCardAddress\\.state').val());
	      if($('#billingAddressForm #creditCard\\.creditCardAddress\\.country').val() == 'JP'){
	    	  $("#billingAddressForm  #otherstateDivCC").hide();
	    	  $("#billingAddressForm  #JPstateDivCC").show();
	      }else{
	    	  $("#billingAddressForm  #otherstateDivCC").show();
	    	  $("#billingAddressForm  #JPstateDivCC").hide();
	      }
	      $("#billingAddressForm  #billingStateSelect").show();
	      
	    }
	    else if($('#billingAddressForm #creditCard\\.creditCardAddress\\.country').val()!='US' && $('#billingAddressForm #creditCard\\.creditCardAddress\\.country').val() != 'CA' 
	    	&& $('#billingAddressForm #creditCard\\.creditCardAddress\\.country').val() != 'AU' && $('#billingAddressForm #creditCard\\.creditCardAddress\\.country').val() != 'IN' && $('#billingAddressForm #creditCard\\.creditCardAddress\\.country').val() != 'JP'){
	      $("#billingAddressForm  #billingStateSelect").hide();
	      $("#billingAddressForm  #billingStateInput").show();
	    }
	
	$('#billingAddressForm #creditCard\\.creditCardAddress\\.country').change(function(){
		 $('#billingAddressForm #creditCard\\.creditCardAddress\\.state').val('');
	    if($('#billingAddressForm #creditCard\\.creditCardAddress\\.country').val()=='US' || $('#billingAddressForm #creditCard\\.creditCardAddress\\.country').val() == 'CA' 
	    	|| $('#billingAddressForm #creditCard\\.creditCardAddress\\.country').val() == 'AU' || $('#billingAddressForm #creditCard\\.creditCardAddress\\.country').val() == 'IN' || $('#billingAddressForm #creditCard\\.creditCardAddress\\.country').val() == 'JP'){
	      $("#billingAddressForm  #billingStateInput").hide();
	      if($('#billingAddressForm #creditCard\\.creditCardAddress\\.country').val() == 'JP'){
	    	  $("#billingAddressForm  #otherstateDivCC").hide();
	    	  $("#billingAddressForm  #JPstateDivCC").show();
	      }else{
	    	  $("#billingAddressForm  #otherstateDivCC").show();
	    	  $("#billingAddressForm  #JPstateDivCC").hide();
	      }
	      $("#billingAddressForm  #billingStateSelect").show();
	      
	    }
	    else if($('#billingAddressForm #creditCard\\.creditCardAddress\\.country').val()!='US' && $('#billingAddressForm #creditCard\\.creditCardAddress\\.country').val() != 'CA' 
	    	&& $('#billingAddressForm #creditCard\\.creditCardAddress\\.country').val() != 'AU' || $('#billingAddressForm #creditCard\\.creditCardAddress\\.country').val() != 'IN' || $('#billingAddressForm #creditCard\\.creditCardAddress\\.country').val() != 'JP'){
	      $("#billingAddressForm  #billingStateSelect").hide();
	      $("#billingAddressForm  #billingStateInput").show();
	    }
	    
	  });
	$("#billingAddressForm #creditCard\\.creditCardAddress\\.country").linkToStates("#billingAddressStateSelect");
	$('#billingAddressForm #billingAddressStateSelect').change(function(){
	    $('#billingAddressForm #creditCard\\.creditCardAddress\\.state').val($('#billingAddressStateSelect').val());
	  }); 
    
	 jQuery.validator.addMethod('validateState', 
			    function (value, element, param) {
            var formId = param; 
		  		if(element.name == 'billingAddressStateSelect'){
		  			var country =$("#"+formId).find('#creditCard\\.creditCardAddress\\.country').val(); 
		  			if(country =='US'   || country =='CA' || country =='AU' || country =='IN' || country =='JP'){
			    		return value.length > 0;
			    	}  else {
				        return true;
				        }
		  		}		  		
		  		return true;       	  
		    	
			    }, i18n.errors.validateState); 
	   
  if($('#billingForm #creditCard\\.creditCardAddress\\.country').val()=='US' || $('#billingForm #creditCard\\.creditCardAddress\\.country').val() == 'CA' 
    || $('#billingForm #creditCard\\.creditCardAddress\\.country').val() == 'AU' || $('#billingForm #creditCard\\.creditCardAddress\\.country').val() == 'IN' || $('#billingForm #creditCard\\.creditCardAddress\\.country').val() == 'JP'){
        $("#billingForm  #billingStateInput").hide();
        $("#billingForm #creditCard\\.creditCardAddress\\.country").defautlinkstates("#billingAddressStateSelect"); 
        $("#billingForm #billingAddressStateSelect").val($('#billingForm #creditCard\\.creditCardAddress\\.state').val());
        if($('#billingForm #creditCard\\.creditCardAddress\\.country').val() == 'JP'){
          $("#billingForm  #otherstateDivCC").hide();
          $("#billingForm  #JPstateDivCC").show();
        }else{
          $("#billingForm  #otherstateDivCC").show();
          $("#billingForm  #JPstateDivCC").hide();
        }
        $("#billingForm  #billingStateSelect").show();
        
      }
      else if($('#billingForm #creditCard\\.creditCardAddress\\.country').val()!='US' && $('#billingForm #creditCard\\.creditCardAddress\\.country').val() != 'CA' 
        && $('#billingForm #creditCard\\.creditCardAddress\\.country').val() != 'AU' && $('#billingForm #creditCard\\.creditCardAddress\\.country').val() != 'IN' && $('#billingForm #creditCard\\.creditCardAddress\\.country').val() != 'JP'){
        $("#billingForm  #billingStateSelect").hide();
        $("#billingForm  #billingStateInput").show();
      }
  $('#billingForm #creditCard\\.creditCardExpirationMonth').change(function(){
    $('#billingForm').valid();
  });
  
  $('#billingForm #creditCard\\.creditCardExpirationYear').change(function(){
    $('#billingForm').valid();
  });
  
  $('#billingForm #creditCard\\.creditCardAddress\\.country').change(function(){
     $('#billingForm #creditCard\\.creditCardAddress\\.state').val('');
      if($('#billingForm #creditCard\\.creditCardAddress\\.country').val()=='US' || $('#billingForm #creditCard\\.creditCardAddress\\.country').val() == 'CA' 
        || $('#billingForm #creditCard\\.creditCardAddress\\.country').val() == 'AU' || $('#billingForm #creditCard\\.creditCardAddress\\.country').val() == 'IN' || $('#billingForm #creditCard\\.creditCardAddress\\.country').val() == 'JP'){
        $("#billingForm  #billingStateInput").hide();
        if($('#billingForm #creditCard\\.creditCardAddress\\.country').val() == 'JP'){
          $("#billingForm  #otherstateDivCC").hide();
          $("#billingForm  #JPstateDivCC").show();
        }else{
          $("#billingForm  #otherstateDivCC").show();
          $("#billingForm  #JPstateDivCC").hide();
        }
        $("#billingForm  #billingStateSelect").show();
        
      }
      else if($('#billingForm #creditCard\\.creditCardAddress\\.country').val()!='US' && $('#billingForm #creditCard\\.creditCardAddress\\.country').val() != 'CA' 
        && $('#billingForm #creditCard\\.creditCardAddress\\.country').val() != 'AU' || $('#billingForm #creditCard\\.creditCardAddress\\.country').val() != 'IN' || $('#billingForm #creditCard\\.creditCardAddress\\.country').val() != 'JP'){
        $("#billingForm  #billingStateSelect").hide();
        $("#billingForm  #billingStateInput").show();
      }
      
    });
  $("#billingForm #creditCard\\.creditCardAddress\\.country").linkToStates("#billingAddressStateSelect");
  $('#billingForm #billingAddressStateSelect').change(function(){
      $('#billingForm #creditCard\\.creditCardAddress\\.state').val($('#billingAddressStateSelect').val());
    }); 


  if($('#setAccountTypeForm #creditCard\\.creditCardAddress\\.country').val()=='US' || $('#setAccountTypeForm #creditCard\\.creditCardAddress\\.country').val() == 'CA' 
    || $('#setAccountTypeForm #creditCard\\.creditCardAddress\\.country').val() == 'AU' || $('#setAccountTypeForm #creditCard\\.creditCardAddress\\.country').val() == 'IN' || $('#setAccountTypeForm #creditCard\\.creditCardAddress\\.country').val() == 'JP'){
        $("#setAccountTypeForm #billingStateInput").hide();
        $("#setAccountTypeForm #creditCard\\.creditCardAddress\\.country").defautlinkstates("#setAccountTypeForm #billingAddressStateSelect"); 
        $("#setAccountTypeForm #billingAddressStateSelect").val($('#setAccountTypeForm #creditCard\\.creditCardAddress\\.state').val());
        if($('#setAccountTypeForm #creditCard\\.creditCardAddress\\.country').val() == 'JP'){
          $("#setAccountTypeForm #otherstateDivCC").hide();
          $("#setAccountTypeForm #JPstateDivCC").show();
        }else{
          $("#setAccountTypeForm #otherstateDivCC").show();
          $("#setAccountTypeForm #JPstateDivCC").hide();
        }
        $("#setAccountTypeForm #billingStateSelect").show();
        
      }
      else if($('#setAccountTypeForm #creditCard\\.creditCardAddress\\.country').val()!='US' && $('#setAccountTypeForm #creditCard\\.creditCardAddress\\.country').val() != 'CA' 
        && $('#setAccountTypeForm #creditCard\\.creditCardAddress\\.country').val() != 'AU' && $('#setAccountTypeForm #creditCard\\.creditCardAddress\\.country').val() != 'IN' && $('#setAccountTypeForm #creditCard\\.creditCardAddress\\.country').val() != 'JP'){
        $("#setAccountTypeForm #billingStateSelect").hide();
        $("#setAccountTypeForm #billingStateInput").show();
      }
  
  $('#setAccountTypeForm #creditCard\\.creditCardExpirationMonth').change(function(){
    $('#setAccountTypeForm').valid();
  });
  
  $('#setAccountTypeForm #creditCard\\.creditCardExpirationYear').change(function(){
    $('#setAccountTypeForm').valid();
  });
  
  
  $('#setAccountTypeForm #creditCard\\.creditCardAddress\\.country').change(function(){
     $('#setAccountTypeForm #creditCard\\.creditCardAddress\\.state').val('');
      if($('#setAccountTypeForm #creditCard\\.creditCardAddress\\.country').val()=='US' || $('#setAccountTypeForm #creditCard\\.creditCardAddress\\.country').val() == 'CA' 
        || $('#setAccountTypeForm #creditCard\\.creditCardAddress\\.country').val() == 'AU' || $('#setAccountTypeForm #creditCard\\.creditCardAddress\\.country').val() == 'IN' || $('#setAccountTypeForm #creditCard\\.creditCardAddress\\.country').val() == 'JP'){
        $("#setAccountTypeForm #billingStateInput").hide();
        if($('#setAccountTypeForm #creditCard\\.creditCardAddress\\.country').val() == 'JP'){
          $("#setAccountTypeForm #otherstateDivCC").hide();
          $("#setAccountTypeForm #JPstateDivCC").show();
        }else{
          $("#setAccountTypeForm #otherstateDivCC").show();
          $("#setAccountTypeForm #JPstateDivCC").hide();
        }
        $("#setAccountTypeForm #billingStateSelect").show();
        
      }
      else if($('#setAccountTypeForm #creditCard\\.creditCardAddress\\.country').val()!='US' && $('#setAccountTypeForm #creditCard\\.creditCardAddress\\.country').val() != 'CA' 
        && $('#setAccountTypeForm #creditCard\\.creditCardAddress\\.country').val() != 'AU' || $('#setAccountTypeForm #creditCard\\.creditCardAddress\\.country').val() != 'IN' || $('#setAccountTypeForm #creditCard\\.creditCardAddress\\.country').val() != 'JP'){
        $("#setAccountTypeForm #billingStateSelect").hide();
        $("#setAccountTypeForm #billingStateInput").show();
      }
      
    });
  $("#setAccountTypeForm #creditCard\\.creditCardAddress\\.country").linkToStates("#setAccountTypeForm #billingAddressStateSelect");
  $('#setAccountTypeForm #billingAddressStateSelect').change(function(){
      $('#setAccountTypeForm #creditCard\\.creditCardAddress\\.state').val($('#setAccountTypeForm #billingAddressStateSelect').val());
    }); 

	$(".actions_dropdown_button").click(function() {
		 if ( $(".actions_dropdown:visible").length != 0) {
	      $(".actions_dropdown").hide( ); 
		 }	  
		 else {
		  $(".actions_dropdown").show( );
		}
	  });
	 
	  jQuery.validator.addMethod("checkcardExp", function(value, element, param) {
	    var formId = param;
	    if ($('#creditCard\\.creditCardExpirationMonth').val()=="" || $('#creditCard\\.creditCardExpirationYear').val()=="")
	      return true;
	    var expDate = $("#"+formId).find('#creditCard\\.creditCardExpirationMonth').val() + '/30/' +
	       $("#"+formId).find('#creditCard\\.creditCardExpirationYear').val();
	    var today =  new Date();
	    return Date.parse(today) < Date.parse(expDate);
	  }, i18n.errors.checkcardExp);
	
	
	var formValidator = $("#billingForm").validate( {
		//debug : false,
	    success : "valid",
	    ignoreTitle : true,
	    rules: {
	    	"creditCard.creditCardType" : {
	    		required: true	       
	    	},
            "creditCard.creditCardNumber" : {
              required: true,
              creditcard2: function(){ 
                var val1 = $("#billingForm #creditCard\\.creditCardType").val();
                
                return val1;
              } 
            },
            "creditCard.creditCardCVV" : {
            	required:true,
                digits: true,
                minlength: function(){ 
                	if($("#billingForm #creditCard\\.creditCardType").val() == "AMEX"){
                  		return 4;
                  	}else{
                  		return 3;
                  	}
                	}        ,
                maxlength: function(){ 
              	if($("#billingForm #creditCard\\.creditCardType").val() == "AMEX"){
              		return 4;
              	}else{
              		return 3;
              	}
            	} 
            },
            "creditCard.firstNameOnCard" : {
              required: true,
              maxlength:100
            },            
            "creditCard.lastNameOnCard" : {
            	required: true,
              maxlength:100
            },
            "creditCard.creditCardExpirationMonth" : {
              required: true
            },
            "creditCard.creditCardExpirationYear" : {
              required:  function(element){
                          return $('#billingForm #creditCard\\.creditCardExpirationMonth').val()!="";
                        },
              checkcardExp:'billingForm'
            },
            "creditCard.creditCardAddress.street1" : {
                required:true,
                maxlength:100
              }, 
              "creditCard.creditCardAddress.city" : {
                  required:true,
                  maxlength:100
                }, 
              "creditCard.creditCardAddress.state" : {
                maxlength: 100
              },
              "creditCard.creditCardAddress.postalCode" : {
                required:true,
                maxlength:100,
                postalcode: function() {
                    return $('#creditCard\\.creditCardAddress\\.country').val();
                  }
              },
              "creditCard.creditCardAddress.country" : {
                required:true
              },
              "billingAddressStateSelect" : {
            	  validateState:'billingForm'
                
              }
          },
          messages: {
        	 "creditCard.creditCardType" : {
              required: i18n.errors.creditCard.creditCardType
            },
            "creditCard.creditCardNumber" : {
              required: i18n.errors.creditCard.creditCardNumber,
							creditcard2: i18n.errors.creditCard.creditCardNumberValid
            },
            "creditCard.creditCardCVV" : {
              required: i18n.errors.creditCard.creditCardCVV,
              digits: i18n.errors.creditCard.creditCardCVVDigits
            },
            "creditCard.creditCardExpirationMonth" : {
              required: i18n.errors.creditCard.creditCardExpirationMonth
            },
            "creditCard.creditCardExpirationYear" : {
              required: i18n.errors.creditCard.creditCardExpirationYear
            },
            "creditCard.firstNameOnCard" : {
              required: i18n.errors.creditCard.firstNameOnCard,
              maxlength: i18n.errors.creditCard.maxLength
            },
            "creditCard.lastNameOnCard" : {
            	required: i18n.errors.creditCard.lastNameOnCard,
              maxlength: i18n.errors.creditCard.maxLength
            },
            "creditCard.creditCardAddress.street1" : {
              required:i18n.errors.creditCard.creditCardAddress.street1,
              maxlength: i18n.errors.creditCard.maxLength
            }, 
            "creditCard.creditCardAddress.city" : {
              required:i18n.errors.creditCard.creditCardAddress.city,
              maxlength: i18n.errors.creditCard.maxLength
            }, 
            "creditCard.creditCardAddress.postalCode" : {
            	required:i18n.errors.creditCard.creditCardAddress.postalCode,
              maxlength: i18n.errors.creditCard.maxLength
            },
            "creditCard.creditCardAddress.state" : {
                maxlength: i18n.errors.creditCard.maxLength
              },
            "creditCard.creditCardAddress.country" : {
            	required:i18n.errors.creditCard.creditCardAddress.country
            }
          },
          ignore:"",
          errorPlacement: function(error, element) {
        	  var name = element.attr('id');
        	  if(name == 'creditCard.creditCardExpirationMonth' || name == 'creditCard.creditCardExpirationYear'){
        	    name='creditCardExpirationMonth';
        	  }
        	  if(name == 'creditCard.creditCardCVV'){
              error.width(130);
            }
          	name =ReplaceAll(name,".","\\.");  
          	if(error.html() !=""){
          		error.appendTo( "#billingForm #"+name+"Error" );
          	}
          }
	    
	});
	
	var setAccountTypeFormValidator = $("#setAccountTypeForm").validate( {
//		debug : false,
	    success : "valid",
	    ignoreTitle : true,
	    rules: {
		    	"creditCard.creditCardType" : {
		         required: true	       
	    		},
	            "creditCard.creditCardNumber" : {
	              required: true,
	              creditcard2: function(){ 
	                var val1 = $("#setAccountTypeForm #creditCard\\.creditCardType").val();
	                
	                return val1;
	              } 
	            },
	            "creditCard.creditCardCVV" : {
	            	required:true,
	                digits: true,
	                minlength: function(){ 
	                	if($("#setAccountTypeForm #creditCard\\.creditCardType").val() == "AMEX"){
	                  		return 4;
	                  	}else{
	                  		return 3;
	                  	}
	                	}        ,
	                maxlength: function(){ 
	              	if($("#setAccountTypeForm #creditCard\\.creditCardType").val() == "AMEX"){
	              		return 4;
	              	}else{
	              		return 3;
	              	}
	            	} 
	            },
	            "creditCard.firstNameOnCard" : {
	                required: true,
                  maxlength:100
	              },            
	              "creditCard.lastNameOnCard" : {
	              	required: true,
                  maxlength:100
	              },            
	            "creditCard.creditCardExpirationYear" : {
	                required:  function(element){
                  return $('#setAccountTypeForm #creditCard\\.creditCardExpirationMonth').val()!="";
                },
                checkcardExp:'setAccountTypeForm'
	            },
	            "creditCard.creditCardExpirationMonth" : {
	                required: true
	             },
	             "creditCard.creditCardAddress.street1" : {
	                required:true,
                  maxlength:100
	              }, 
	              "creditCard.creditCardAddress.city" : {
	                  required:true,
                    maxlength:100
	                }, 
	              "creditCard.creditCardAddress.postalCode" : {
	                required:true,
                  maxlength:100,
                  postalcode: function() {
                      return $('#creditCard\\.creditCardAddress\\.country').val();
                    }
	              },
                "creditCard.creditCardAddress.state" : {
                maxlength: 100
              },
	              "creditCard.creditCardAddress.country" : {
	                required:true
	              },
                "billingAddressStateSelect" : {
                  validateState:'setAccountTypeForm'                
                }

          },
	      messages: {
	        	 "creditCard.creditCardType" : {
	              required: i18n.errors.creditCard.creditCardType
	            },
	            "creditCard.creditCardNumber" : {
	              required: i18n.errors.creditCard.creditCardNumber
	            },
	            "creditCard.creditCardCVV" : {
	              required: i18n.errors.creditCard.creditCardCVV,
	              digits: i18n.errors.creditCard.creditCardCVVDigits
	            },
	            "creditCard.creditCardExpirationMonth" : {
	              required: i18n.errors.creditCard.creditCardExpirationMonth
	            },
	            "creditCard.creditCardExpirationYear" : {
	              required: i18n.errors.creditCard.creditCardExpirationYear
	            },
	            "creditCard.firstNameOnCard" : {
	              required: i18n.errors.creditCard.fistNameOnCard,
                maxlength: i18n.errors.creditCard.maxLength
	            },
	            "creditCard.lastNameOnCard" : {
	            	required: i18n.errors.creditCard.lastNameOnCard,
                maxlength: i18n.errors.creditCard.maxLength
	            },
	            "creditCard.creditCardAddress.street1" : {
	              required:i18n.errors.creditCard.creditCardAddress.street1,
                maxlength: i18n.errors.creditCard.maxLength
	            },
              "creditCard.creditCardAddress.state" : {
                maxlength: i18n.errors.creditCard.maxLength
              },
	            "creditCard.creditCardAddress.city" : {
	              required:i18n.errors.creditCard.creditCardAddress.city,
                maxlength: i18n.errors.creditCard.maxLength
	            }, 
	            "creditCard.creditCardAddress.postalCode" : {
	            	required:i18n.errors.creditCard.creditCardAddress.postalCode,
                maxlength: i18n.errors.creditCard.maxLength
	            },
	            "creditCard.creditCardAddress.country" : {
	            	required:i18n.errors.creditCard.creditCardAddress.country
	            }
	          },
	          ignore:"",
          errorPlacement: function(error, element) {
	            var name = element.attr('id');
	            if(name == 'creditCard.creditCardExpirationMonth' || name == 'creditCard.creditCardExpirationYear'){
	              name='creditCardExpirationMonth';
	            }
	            if(name == 'creditCard.creditCardCVV'){
	              error.width(130);
	            }
	            name =ReplaceAll(name,".","\\.");  
	            if(error.html() !=""){
	              error.appendTo( "#setAccountTypeForm #"+name+"Error" );
	            }
          }
	});
	
	var formValidator = $("#billingAddressForm").validate( {
		//debug : false,
	    success : "valid",
	    ignoreTitle : true,
	    rules: {
            "creditCard.creditCardAddress.street1" : {
              required:true,
              maxlength:100
            }, 
            "creditCard.creditCardAddress.city" : {
                required:true,
                maxlength:100
              }, 
            "creditCard.creditCardAddress.postalCode" : {
              required:true,
              maxlength:100,
              postalcode: function() {
                  return $('#creditCard\\.creditCardAddress\\.country').val();
                }
            },
            "creditCard.creditCardAddress.state" : {
            	required:true,
                maxlength: 100
              },
            "creditCard.creditCardAddress.country" : {
              required:true
            },
            "billingAddressStateSelect" : {
          	  validateState:'billingAddressForm'
              
            }
          },
          messages: {
            "creditCard.creditCardAddress.street1" : {
              required:i18n.errors.creditCard.creditCardAddress.street1,
              maxlength: i18n.errors.creditCard.maxLength
            }, 
            "creditCard.creditCardAddress.city" : {
                required:i18n.errors.creditCard.creditCardAddress.city,
                maxlength: i18n.errors.creditCard.maxLength
              }, 
            "creditCard.creditCardAddress.postalCode" : {
              required:i18n.errors.creditCard.creditCardAddress.postalCode,
              maxlength: i18n.errors.creditCard.maxLength
            },
            "creditCard.creditCardAddress.state" : {
            	required:i18n.errors.creditCard.creditCardAddress.state,
                maxlength: i18n.errors.creditCard.maxLength
              },
            "creditCard.creditCardAddress.country" : {
              required:i18n.errors.creditCard.creditCardAddress.country
            }
          },
          ignore:"",
          errorPlacement: function(error, element) {
        	  var name = element.attr('id');
          	name =ReplaceAll(name,".","\\.");  
          	if(error.html() !=""){
          		error.appendTo( "#billingAddressForm #"+name+"Error" );
          	}
          }
	    
	});	


	
	 $("#makePaymentForInvoiceForm").validate( {
			//debug : false,
		    success : "valid",
		    ignoreTitle : true,
		    rules: {
		 		"invoicePayAmount": {
         			required: true,
         			number: true
       			}
	          },
	          messages: {
	        	  "invoicePayAmount" : i18n.errors.invoicePayAmount
	          },
	          errorPlacement: function(error, element) {	          	
	                  error.appendTo( element.parent() );
	          }
		    
		});
	 if(typeof invoicesPage !='undefined' && invoicesPage == true){
		 viewbillingActivity($("li[id^='row'].selected.invoices")); 
	 } else if(typeof paymentsPage !='undefined' && paymentsPage == true ){
		 viewPaymentDetails($("li[id^='row'].selected.invoices")); 
	 }
	  
	 $("#selectedUserfilter").unbind("change").bind("change", function(event){ 
		 viewbillingActivity($("li[id^='row'].selected.invoices"));
	 });
	 
});

/**
 * Edit Credit Card Details POST
 * 
 * @param event
 * @param form
 * @return
 */
function editCreditCardDetails(event, form, tenantparam, show_deposit_record_message,initialDepositAmount) {
  if (event.preventDefault) {
    event.preventDefault();
  } else {
    event.returnValue = false;
  }
  var formid = $(form).attr('id');
  if ($("#" + formid).valid() ) {
    $("#addCreditCardButton").attr("value",i18n.errors.tenants.tenantEditForm.saving);
    if (show_deposit_record_message == true || show_deposit_record_message == 'true') {
      initDialog("dialog_confirmation", 350, false);
      var $thisPanel = $("#dialog_confirmation");
      $("#dialog_confirmation").text(dictionary.payInitialDepositMessage + " " + initialDepositAmount + ". " + dictionary.doYouWantToProceed).dialog('option', 'buttons', {
        "OK": function () {
          $(this).dialog("close");
          $.ajax( {
            type : "POST",
            url : $(form).attr('action'),
            data : $(form).serialize(),
            dataType : "json",
            success : function(json) {
              if(json.redirecturl)
                window.location= json.redirecturl; 
            },
            error : function(xhr, ajaxOptions, thrownError){
              $("#creditcard_update_error").show();
              $("#creditcard_update_error").find("#p_message").html(xhr.responseText);     
              var submit = $(form).find(".submitmsg");
              $("#addCreditCardButton").attr("value",i18n.errors.tenants.tenantEditForm.save);
              $("#editCreditCardAddressSaveButton").attr("value",i18n.errors.tenants.tenantEditForm.save);
              $("#editCreditCardDetailsSaveButton").attr("value",i18n.errors.tenants.tenantEditForm.save);
              $("#editCreditCardAddressSaveButton").attr("disabled", false);
              $("#addCreditCardButton").attr("disabled", false);
              $("#editCreditCardDetailsSaveButton").attr("disabled", false);
            }
          });
      },
        "Cancel": function() {
          $(this).dialog("close");
          $("#addCreditCardButton").attr("value",i18n.errors.tenants.tenantEditForm.save);
          return false;
        }
      });
      dialogButtonsLocalizer($thisPanel, {'OK':g_dictionary.dialogOK, 'Cancel': g_dictionary.dialogCancel}); 
      $thisPanel.dialog("open");
      
    } else{
      $.ajax( {
        type : "POST",
        url : $(form).attr('action'),
        data : $(form).serialize(),
        dataType : "json",
        success : function(json) {
          if(json.redirecturl)
            window.location= json.redirecturl; 
        },
        error : function(xhr, ajaxOptions, thrownError){
          $("#creditcard_update_error").show();
          $("#creditcard_update_error").find("#p_message").html(xhr.responseText);     
          var submit = $(form).find(".submitmsg");
          $("#addCreditCardButton").attr("value",i18n.errors.tenants.tenantEditForm.save);
          $("#editCreditCardAddressSaveButton").attr("value",i18n.errors.tenants.tenantEditForm.save);
          $("#editCreditCardDetailsSaveButton").attr("value",i18n.errors.tenants.tenantEditForm.save);
          $("#editCreditCardAddressSaveButton").attr("disabled", false);
          $("#addCreditCardButton").attr("disabled", false);
          $("#editCreditCardDetailsSaveButton").attr("disabled", false);
        }
      });
    }
  }
}

/**
* Change Account Type POST
* 
* @param event
* @param form
* @return
*/
function changeAccountType(event, form, tenantparam) {
 if (event.preventDefault) {
   event.preventDefault();
 } else {
   event.returnValue = false;
 }
 var formid = $(form).attr('id');
 
 
 if (($("#addCreditCardDivForAccountTypeChange").is(":visible")  && $("#" + formid).valid()) || !$("#addCreditCardDivForAccountTypeChange").is(":visible")) {
   var data = {};
   if (!$("#addCreditCardDivForAccountTypeChange").is(":visible")) {
     var $formdata = $(form);
     $formdata.find("#addCreditCardDivForAccountTypeChange").empty();
     data = $formdata.serialize();
     changeAccountTypeSubmit(form, data);
   } else {
     data = $(form).serialize();
     if(showInitialDeposit) {
       var initialDepositAmount = $("#addCreditCardDivForAccountTypeChange").find("#initialDepositAmount_msg").find("#initialDepositAmount").text();
       initDialog("dialog_confirmation", 350, false);
       var $thisPanel = $("#dialog_confirmation");
       $("#dialog_confirmation").text(dictionary.payInitialDepositMessage + " " + initialDepositAmount + ". " + dictionary.doYouWantToProceed).dialog('option', 'buttons', {
         "OK": function () {
           $(this).dialog("close");
           changeAccountTypeSubmit(form, data);
         },
         "Cancel": function() {
           $(this).dialog("close");
           var submit = $(form).find(".submitmsg");
           if ($(submit).attr("rel") != null && $(submit).attr("rel") != "") {
             $(submit).attr("value", g_dictionary.save);
             $(submit).attr("disabled", false);
           }
           return false;
         }
       });
       dialogButtonsLocalizer($thisPanel, {'OK':g_dictionary.dialogOK, 'Cancel': g_dictionary.dialogCancel}); 
       $thisPanel.dialog("open");
     } else {
       changeAccountTypeSubmit(form, data);
     }
   }

 }
}

function changeAccountTypeSubmit(form, data) {
  $.ajax({
    type : "POST",
    url : $(form).attr('action'),
    data : data,
    dataType : "json",
    success : function(json) {
      if(json.redirecturl)
        window.location = json.redirecturl; 
    },
    error : function(xhr, ajaxOptions, thrownError){
      $("#changeAccountTypeJsp").find("#creditcard_update_error").show();
      $("#changeAccountTypeJsp").find("#creditcard_update_error").find("#p_message").html(xhr.responseText);    
      var submit = $(form).find(".submitmsg");
      if ($(submit).attr("rel") != null && $(submit).attr("rel") != "") {
        $(submit).attr("value", g_dictionary.save);
        $(submit).attr("disabled", false);
      }
    }
  });
}

function refreshDivs(id){
	$(id).each( function() {
			$(this).html("");
		});
}
function blockUI(){
$('#blockScreen').css({ opacity: 0.7, 'width':$(document).width(),'height':$(document).height()});
$('#blockScreen').show();
$('#spinner').show();
}
function unBlockUI(){
$('#blockScreen').hide();
$('#spinner').hide();
}

function changeAccountTypeGet(current,tenantParam){
  var today = new Date().getTime();
	 var url = "/portal/portal/billing/changeaccounttype";
	 $.ajax( {
			type : "GET",
			url : url,
			data:{tenant:tenantParam,currTime:today},
			dataType : "html",
			success : function(html) {				
				$("#changeAccountTypeDiv").html("");
				$("#changeAccountTypeDiv").html(html);
				accountTypeChanged($("#accountTypeNameSelect"));
			},error:function(){	
				//need to handle TO-DO
			}
	 });
	
}


function viewbillingActivity(current){
  var type=$(current).attr("bill_type");
  var divId = $(current).attr('id');
  if(divId==null) return;
  var ID=divId.substr(3);
  resetGridRowStyle();
  $(current).addClass("selected active");
  var url = "/portal/portal/billing/"+ID+"/viewbillingactivity"+"?tenant="+$("#tenantParam" ).val();
  var selectedUserParam = $("#selectedUserfilter option:selected").val();
  
  if(typeof selectedUserParam!="undefined"){
	  url = "/portal/portal/billing/"+ID+"/viewbillingactivity?user="+selectedUserParam+"&tenant="+$("#tenantParam" ).val();
  }
  
  $.ajax( {
    type : "GET",
    url : url,
    data:{type:type},
    async:false,
    dataType : "html",
    success : function(html) {				
    $("#updateBillingActivityDiv").html("");
    $("#viewBillingActivityDiv").html("");
    $("#viewBillingActivityDiv").html(html);	
  	bindActionMenuContainers();
  	
  },error:function(){	
  	//need to handle TO-DO
  		}
   });
}

var showInitialDeposit = false;
function accountTypeChanged(accountType) {
	$.ajax( {
		type : "GET",
		url : "/portal/portal/tenants/get_account_type",	
		async: false,
		data: {
		  accountTypeName : $(accountType).val(),
		  tenant : $("#tenantParam").val()
	  },
		dataType : "json",
		success : function(jsonObj) {
			var accountType = jsonObj;
			var paymentMode = accountType.paymentModes;
			var autoPayRequired = accountType.autoPayRequired;
			showInitialDeposit = accountType.show_deposit_record_message;
			if(showInitialDeposit) {
			  $("#addCreditCardDivForAccountTypeChange").find("#initialDepositAmount_msg").find("#initialDepositAmount").text(accountType.initialDepositAmount);
			  $("#addCreditCardDivForAccountTypeChange").find("#initialDepositAmount_msg").show();
			} else {
			  $("#addCreditCardDivForAccountTypeChange").find("#initialDepositAmount_msg").hide();
			}
			
			if(currentPaymentMode != paymentMode && paymentMode == 2 && autoPayRequired){
				$("#addCreditCardDivForAccountTypeChange").show();
			}
			else{
			  $("#addCreditCardDivForAccountTypeChange").hide();
			}
			if(accountType.manualActivation){
				$("#deferredActivationDiv").show();
			}
			else{
			  $("#deferredActivationDiv").hide();
			}
		}	
	});			
	
}


/**
 * Reset data row style
 * @return
 */
function resetGridRowStyle(){
  $(".widget_navigationlist").each(function(){
    $(this).removeClass("selected active");   
    
  });
}

function chargeBack(current) {
	$( "#action_result_panel").hide();
	$( "#top_message_panel").hide();
	initDialog( "issueChargeBackDiv", 420);
	var divId = $(current).attr('id' );
	var id = divId.substr(21);
	var actionurl = "/portal/portal/billing/chargeback" ;
	var $spinningWheel = $("#spinning_wheel" );

	var $thisDialog = $("#issueChargeBackDiv" );
	$thisDialog.dialog({
		height : 200,
		width : 420
	});
	$thisDialog.dialog( 'option', 'buttons', {
		"OK" : function () {
			$( this).dialog("close" );

			$spinningWheel.find( "#in_process_text").text(dictionary.issuingChargeBack);
			$spinningWheel.show();
			$.ajax({
				type : "POST",
				url : actionurl,
				data : {
					slrparam : id,
					tenantParam : $( "#tenantParam").val()
				},
				dataType : "html",
				success : function(jsonResponse) {
					if (jsonResponse == "success" ) {
						var msg = dictionary.chargeBackSuccess;
						$( "#action_result_panel").find("#msg" ).text(msg);
						$( "#action_result_panel").find("#status_icon" ).removeClass("erroricon").addClass( "successicon");
						$spinningWheel.hide();
						$( "#action_result_panel").removeClass("error" ).addClass("success").show();
					} else {
						var msg = dictionary.chargeBackFailure;
						$( "#action_result_panel").find("#msg" ).text(msg);
						$( "#action_result_panel").find("#status_icon" ).removeClass("successicon").addClass( "erroricon");
						$spinningWheel.hide();
						$( "#action_result_panel").removeClass("success" ).addClass("error").show();
					}
				},
				error : function(jsonResponse, status, error) {
					$spinningWheel.hide();
					var msg = dictionary.chargeBackFailure + " : " + jsonResponse.responseText;
					$( "#action_result_panel").find("#msg" ).text(jsonResponse.status + ': ' + msg);
					$( "#action_result_panel").find("#status_icon" ).removeClass("successicon").addClass( "erroricon");
					$( "#action_result_panel").removeClass("success" ).addClass("error").show();
				}
			});
		},
		"Cancel" : function () {
			$( this).dialog("close" );
			return false;
		}
	});
	dialogButtonsLocalizer($thisDialog, {'OK':g_dictionary.dialogOK, 'Cancel': g_dictionary.dialogCancel}); 
	$thisDialog.dialog("open");
}

function cancelCredit(current) {
	cancelCredit(current, true);
}
function cancelRecordedPayment(current) {
	cancelCredit(current, false);
}

function cancelCredit(current, cancelCredit) {
	$( "#action_result_panel").hide();
	$( "#top_message_panel").hide();
	initDialog( "cancelCreditDiv", 420);
	var divId = $(current).attr('id' );
	var id = divId.substr(21);
	var actionurl = "/portal/portal/tenants/" + $("#tenantParam" ).val() + "/cancel_credit" ;
	var $spinningWheel = $("#spinning_wheel" );
	
	var $thisDialog = $("#cancelCreditDiv" );
	$thisDialog.dialog({
		height : 200,
		width : 420
	});
	
	if (!cancelCredit){
		$thisDialog.dialog( 'option', 'title', dictionary.cancelPaymentTitle);
		$thisDialog.find('label').text(dictionary.cancelPaymentConfirm);
	}
	
	$thisDialog.dialog( 'option', 'buttons', {
		"OK" : function () {
			$( this).dialog("close" );
			if (cancelCredit){
				$spinningWheel.find( "#in_process_text").text(dictionary.cancellingCredit);
			}else{
				$spinningWheel.find( "#in_process_text").text(dictionary.issuingChargeBack);
			}
			$spinningWheel.show();
			$.ajax({
				type : "POST",
				url : actionurl,
				data : {
					slrUuid : id,
					comment : "",
				},
				dataType : "html",
				success : function(jsonResponse) {
					if (jsonResponse == "success" ) {
						var msg = dictionary.cancelCreditSuccess;
						$( "#action_result_panel").find("#msg" ).text(msg);
						$( "#action_result_panel").find("#status_icon" ).removeClass("erroricon").addClass( "successicon");
						$spinningWheel.hide();
						$( "#action_result_panel").removeClass("error" ).addClass("success").show();
						$("#"+divId).hide();
						
					} else {
						var msg = dictionary.cancelCreditFailure;
						$( "#action_result_panel").find("#msg" ).text(msg);
						$( "#action_result_panel").find("#status_icon" ).removeClass("successicon").addClass( "erroricon");
						$spinningWheel.hide();
						$( "#action_result_panel").removeClass("success" ).addClass("error").show();
					}
				},
				error : function(jsonResponse, status, error) {
					$spinningWheel.hide();
					var msg = dictionary.cancelCreditFailure + " : " + jsonResponse.responseText;
					$( "#action_result_panel").find("#msg" ).text(jsonResponse.status + ': ' + msg);
					$( "#action_result_panel").find("#status_icon" ).removeClass("successicon").addClass( "erroricon");
					$( "#action_result_panel").removeClass("success" ).addClass("error").show();
				}
			});
		},
		"Cancel" : function () {
			$( this).dialog("close" );
			return false;
		}
	});
	dialogButtonsLocalizer($thisDialog, {'OK':g_dictionary.dialogOK, 'Cancel': g_dictionary.dialogCancel}); 
	$thisDialog.dialog("open");
}

function recordPayment(){
	recordOrMakePayment(false);
}

function makePayment(){
	recordOrMakePayment(true);
}

var paymentInProgress = false;

function recordOrMakePayment(makePayment) {
  if(paymentInProgress){
    return false;
  }
	$( "#action_result_panel").hide();
	$( "#top_message_panel").hide();
  
	initDialog( "recordOrMakePaymentFormDiv", 420);
	var $thisDialog = $("#recordOrMakePaymentFormDiv");
	
	$thisDialog.find("#payAmount" ).val('');
  $thisDialog.find("#paymentMemo" ).val('');
  $("#payAmountError").text("");
  $("#paymentMemoError").text("");
	var $spinningWheel = $("#spinning_wheel");
	
	$thisDialog.dialog({
		height : 200,
		width : 420
	});
	var actionurl = "" ;
	if (makePayment){
		actionurl = "/portal/portal/billing/make_payment" ;
		$thisDialog.dialog( 'option', 'title', dictionary.makePayment);
	} else{
		actionurl = "/portal/portal/billing/recordpayment" ;
		$thisDialog.dialog( 'option', 'title', dictionary.recordPayment);
	}

	$thisDialog.dialog( 'option', 'buttons', {
		"OK" : function () {
			var form = $(this).find('#paymentForm');
			if ($(form).valid()) {
				if (makePayment){
					$spinningWheel.find( "#in_process_text").text(dictionary.makingPayment);
				}else{
					$spinningWheel.find( "#in_process_text").text(dictionary.recordingPayment);
				}
				$thisDialog.dialog( "close");
				paymentInProgress = true;
				$spinningWheel.show();
				
				$.ajax({
					type : "POST",
					url : actionurl,
					data : {
						amount : $(form).find("#payAmount" ).val(),
						memo : $(form).find("#paymentMemo" ).val(),
						tenantParam : $("#tenantParam" ).val()
					},
					dataType : "text",
					success : function(data) {
						if (data == "success" ) {
							
							popUpDialogForAlerts("dialog_info", dictionary.paymentSuccessMessage, reloadCurrentPage);
							
						} else {
							var msg = dictionary.paymentFailureMessage;
							$( "#action_result_panel").find("#msg" ).text(msg);
							$( "#action_result_panel").find("#status_icon" ).removeClass("successicon").addClass( "erroricon");
							$( "#action_result_panel").removeClass("success" ).addClass("error").show();
						}
						$spinningWheel.hide();
						paymentInProgress = false;
					},
					error : function() {
						var msg = dictionary.paymentFailureMessage;
						$( "#action_result_panel").find("#msg" ).text(msg);
						$( "#action_result_panel").find("#status_icon" ).removeClass("successicon").addClass( "erroricon");
						$( "#action_result_panel").removeClass("success" ).addClass("error").show();
						$spinningWheel.hide();
						paymentInProgress = false;
					},
					complete : function() {
						$spinningWheel.hide();
						paymentInProgress = false;
					}
				});
			}
		},
		"Cancel" : function () {
			$( this).dialog("close" );
			return false;
		}
	});
	dialogButtonsLocalizer($thisDialog, {'OK':g_dictionary.dialogOK, 'Cancel': g_dictionary.dialogCancel}); 
	$thisDialog.dialog("open");
}

function viewPaymentDetails(current) {
	var divId = $(current).attr('id' );
	if (divId == null)
		return;
	var ID = divId.substr(3);
	resetGridRowStyle();
	$(current).addClass( "selected active");
	
	var url = "/portal/portal/billing/" + ID + "/viewslr" ;
	if($(current).hasClass("js_initialDeposit")) {
	  url = "/portal/portal/billing/viewInitialDeposit?tenant="+ $("#tenantParam").val();
	}
	$.ajax({
		type : "GET",
		url : url,
		dataType : "html",
		success : function(html) {
			$( "#updateBillingActivityDiv").html("");
			$( "#viewBillingActivityDiv").html("");
			$( "#viewBillingActivityDiv").html(html);
			bindActionMenuContainers();
		},
		error : function() {
			// need to handle TO-DO
		}
	});
}
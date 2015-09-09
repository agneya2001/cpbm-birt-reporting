<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<script type="text/javascript">
var productsUrl = "<%=request.getContextPath() %>/portal/products/";
var productretireurl = "<%=request.getContextPath() %>/portal/products/retireproduct";

initDialog("dialog_retire_product", 550);
function retirePrdouct(current){
  initDialog("dialog_retire_product", 550);
  $("#dialog_retire_product").dialog("option", "closeOnEscape", false);
  $("#dialog_retire_product").dialog("option", "minHeight", 215);
  $("#dialog_retire_product").dialog("option", "right", 20);
  $("#dialog_retire_product").dialog("option", "zIndex", 2000);
  $("#dialog_retire_product").dialog("option", "buttons", 
       {
        "Yes" : function() {
          $(this).dialog("close");
          $(this).dialog("destroy"); 
          initDialog("dialog_retire_product_nochosen", 390);
          $("#dialog_retire_product_nochosen").dialog("option", "closeOnEscape", false);
          $("#dialog_retire_product_nochosen").dialog("option", "buttons",
            {"Ok": function(){
              $(this).dialog("close");
              $.ajax({
				            url : productretireurl,
				            dataType : "html",
				            data : {"productId" : $("#productId").val(),
				                    "checkforentitlements" : "false"},
				            async : false,
				            cache : false,
				            success : function(result) {
				              if(result == "success"){
				                initDialog("dialog_retire_product_success", 390);
				                $("#dialog_retire_product_success").dialog("option", "closeOnEscape", false);
				                $("#dialog_retire_product_success").dialog("option", "buttons",
				                  {"Ok": function(){
				                    $(this).dialog("close");
				                    $("#product_tab").click();
				                  }
				                });
				                dialogButtonsLocalizer($("#dialog_retire_product_success"), {'Ok': g_dictionary.dialogOk});
				                $("#dialog_retire_product_success").dialog("open");           
				              } else {
				                initDialog("dialog_retire_product_failure", 390);
				                $("#dialog_retire_product_failure").dialog("option", "closeOnEscape", false);
				                $("#dialog_retire_product_failure").dialog("option", "buttons",
				                  {"Ok": function(){
				                    $(this).dialog("close");
				                  }
				                });
                dialogButtonsLocalizer($("#dialog_retire_product_failure"), {'Ok': g_dictionary.dialogOk});
                $("#dialog_retire_product_failure").dialog("open");               
              }             
            },
            error : function() {
              initDialog("dialog_retire_product_failure", 390);
              $("#dialog_retire_product_failure").dialog("option", "closeOnEscape", false);
              $("#dialog_retire_product_failure").dialog("option", "buttons",
                {"Ok": function(){
                  $(this).dialog("close");
                }
              });
              dialogButtonsLocalizer($("#dialog_retire_product_failure"), {'Ok': g_dictionary.dialogOk});
              $("#dialog_retire_product_failure").dialog("open");             
            }
          });
            }
            });
          dialogButtonsLocalizer($("#dialog_retire_product_nochosen"), {'Ok': g_dictionary.dialogOk});
          $("#dialog_retire_product_nochosen").dialog("open");          
        },
        "No" : function() {
          $(this).dialog("close");
          $(this).dialog("destroy"); 
          $.ajax({
                  url : productretireurl,
                  dataType : "html",
                  data : {"productId" : $("#productId").val(),
                          "checkforentitlements" : "true"},
                  async : false,
                  cache : false,
                  success : function(result) {
                    if(result == "success"){
                      initDialog("dialog_retire_product_success", 390);
                      $("#dialog_retire_product_success").dialog("option", "closeOnEscape", false);
                      $("#dialog_retire_product_success").dialog("option", "buttons",
                        {"Ok": function(){
                          $(this).dialog("close");
                          $("#product_tab").click();
                        }
                      });
                      dialogButtonsLocalizer($("#dialog_retire_product_success"), {'Ok': g_dictionary.dialogOk});
                      $("#dialog_retire_product_success").dialog("open");
                    } else if (result == "entitlementscheckfailed") {
                        initDialog("dialog_retire_product_yeschosen_fail", 390);
                        $("#dialog_retire_product_yeschosen_fail").dialog("option", "closeOnEscape", false);
                        $("#dialog_retire_product_yeschosen_fail").dialog("option", "buttons",
                          {"Ok": function(){
                            $(this).dialog("close");
                          }
                        });
                        dialogButtonsLocalizer($("#dialog_retire_product_yeschosen_fail"), {'Ok': g_dictionary.dialogOk});
                        $("#dialog_retire_product_yeschosen_fail").dialog("open");                
                      } else {
                      initDialog("dialog_retire_product_failure", 390);
                      $("#dialog_retire_product_failure").dialog("option", "closeOnEscape", false);
                      $("#dialog_retire_product_failure").dialog("option", "buttons",
                        {"Ok": function(){
                          $(this).dialog("close");
                        }
                      });
                      dialogButtonsLocalizer($("#dialog_retire_product_failure"), {'Ok': g_dictionary.dialogOk});
                      $("#dialog_retire_product_failure").dialog("open");
                    }
                  },
                  error : function() {
                    initDialog("dialog_retire_product_failure", 390);
                    $("#dialog_retire_product_failure").dialog("option", "closeOnEscape", false);
                    $("#dialog_retire_product_failure").dialog("option", "buttons",
                      {"Ok": function(){
                        $(this).dialog("close");
                      }
                    });
                    dialogButtonsLocalizer($("#dialog_retire_product_failure"), {'Ok': g_dictionary.dialogOk});
                    $("#dialog_retire_product_failure").dialog("open");
                  }
                });
        }
      });
  dialogButtonsLocalizer($("#dialog_retire_product"), {'Yes': g_dictionary.yes, 'No': g_dictionary.no});
  $("#dialog_retire_product").dialog("open");
}

</script>
<!-- Start View Product Details --> 

<div class="widget_actionbar">
  <div class="widget_actionarea" id="top_actions" >
      <div id="spinning_wheel" style="display:none;">
            <div class="widget_blackoverlay widget_rightpanel">
            </div>
            <div class="widget_loadingbox widget_rightpanel">
              <div class="widget_loaderbox">
                <span class="bigloader"></span>
              </div>
              <div class="widget_loadertext">
                <p id="in_process_text"><spring:message code="label.loading"/> &hellip;</p>
              </div>
            </div>
      </div>
      <c:if test="${whichPlan == 'planned'}">
        <div class="widget_moreactions action_menu_container" title="<spring:message code='manage'/>">
           <!--Actions popover starts here-->
           <div class="widget_actionpopover" id="action_menu"  style="display:none;">
               <div class="widget_actionpopover_top"></div>
                 <div class="widget_actionpopover_mid">
                   <ul class="widget_actionpoplist">
                     <li onclick="editProductGet(this)" id="<c:out value="edit${product.id}"/>"><a href="javascript:void(0);"  class="editProduct" >
                        <spring:message code="ui.products.label.view.edit"/></a>
                     </li>
                     <c:if test="${product.removed eq null}">
                       <li onclick="retirePrdouct(this)" id="<c:out value="retire${product.id}"/>"><a href="javascript:void(0);"  class="editProduct" >
                          <spring:message code="ui.product.retire.label"/></a>
                       </li>
                     </c:if>
                   </ul>
                 </div>
                 <div class="widget_actionpopover_bot"></div>
             </div>
             <!--Actions popover ends here-->
        </div>
      </c:if>
  </div>
</div>

<div class="top_notifications">
  <div id="top_message_panel" class="common_messagebox widget" style="display:none;">
    <button type="button" class="close js_close_parent" >&times;</button>
    <span id="status_icon"></span><p id="msg"></p>
  </div>
  <div id="action_result_panel" class="common_messagebox widget" style="display:none;">
    <button type="button" class="close js_close_parent" >&times;</button>
    <span id="status_icon"></span><p id="msg"></p>
  </div>
</div>
<div class="widget_browser">
    <div id="spinning_wheel" style="display:none">
      <div class="widget_loadingpanel">
      </div>
      <div class="maindetails_footer_loadingbox first">
        <div class="maindetails_footer_loadingicon"></div>
        <p id="in_process_text"></p>
      </div>
    </div>

  <div class="widget_browsermaster">
      <div class="widget_browser_contentarea">
          <div class="widget_browsergrid_wrapper master">
                                      
                <div class="widget_grid master even first">
                    <div class="widget_grid_labels">
                        <span><spring:message code="ui.products.label.create.name"/></span>
                    </div>
                    <div class="widget_grid_description">
                        <span class = "ellipsis" title = "<c:out value="${product.name}"></c:out>" id="productname"><c:out value="${product.name}"></c:out></span>
                    </div>
                </div>
                                        
                <div class="widget_grid master">
                    <div class="widget_grid_labels">
                        <span><spring:message code="ui.products.label.create.product.code"/></span>
                    </div>
                    <div class="widget_grid_description">
                        <span class = "ellipsis" title = "<c:out value="${product.code}"></c:out>"  id="productcode"><c:out value="${product.code}"></c:out></span>
                    </div>
                </div>   
                <div class="widget_grid master ">
                     <div class="widget_grid_labels">
                        <span><spring:message code="ui.products.label.create.product.category"/></span>
                     </div>
                     <div class="widget_grid_description" >
                        <span  class = "ellipsis" title = "<c:out value="${product.category.name}"></c:out>" id="category"><c:out value="${product.category.name}"></c:out></span>
                     </div>
                </div>
            </div>

            <div class="widget_masterbigicons defaultbox">
              <div class="thumbnail_defaultcontainer">
              <div class="thumbnail_defaulticon product">
              <c:choose>
                <c:when test="${not empty product.imagePath}">
                <img src="/portal/portal/logo/product/<c:out value="${product.id}"/>" id="productimage<c:out value="${product.id}"/>" style="height:99px;width:99px;" />
                </c:when>
                <c:otherwise>
                  <img src="<%=request.getContextPath() %>/images/default_productsicon.png" id="productimage<c:out value="${product.id}"/>" style="height:99px;width:99px;" />
                </c:otherwise>
              </c:choose>              
              </div>
              </div>
              <div class="widget_masterbigicons_linkbox default"><a href="javascript:void(0);"  onclick="editProductImageGet(this,<c:out value="${product.id}" />)" class=" default editProductLogo">
              <spring:message code="ui.products.label.view.editimage"/></a>
              </div>
            </div>
     </div>
  </div>

  <div class="widget_browser_contentarea">
    <ul class="widgets_detailstab">
        <li class="widgets_detailstab active" id="details_tab"><spring:message code="label.details"/></li>
         <li class="widgets_detailstab nonactive" onclick="viewProductPricing(this);"  id="productpricing_tab"><spring:message code="ui.label.product.pricing"/></li>
         <li class="widgets_detailstab nonactive" onclick="viewChannelPricing(this, false, 4);"  id="channelpricing_tab"><spring:message code="ui.label.channel.pricing"/></li>
         <li class="widgets_detailstab nonactive" onclick="viewMediationRules(this);" id="mediationrules_tab"><spring:message code="ui.label.mediation.rules"/></li>
    </ul>

    <div id="details_content">

      <div class="widget_details_actionbox">
        <ul class="widget_detail_actionpanel" style="float:left;">
        </ul>
      </div>

      <div class="widget_browsergrid_wrapper details" >
         <div class="widget_grid details even">
            <div class="widget_grid_labels">
                <span><spring:message code="ui.products.label.create.listorder"/></span>
            </div>
            <div class="widget_grid_description">
                <span id="productname"><c:out value="${product.sortOrder}"></c:out></span>
            </div>
        </div>

         <div class="widget_grid details odd">
             <div class="widget_grid_labels">
                <span><spring:message code="ui.products.label.create.description"/></span>
            </div>
            <div class="widget_grid_description">
                <span class = "ellipsis" title = "<c:out value="${product.description}"></c:out>" id="description"><c:out value="${product.description}"></c:out></span>
            </div>
        </div>
        <div class="widget_grid details even">
            <div class="widget_grid_labels">
                  <span><spring:message code="ui.products.label.create.product.uom"/></span>
            </div>
            <div class="widget_grid_description">
                  <span class = "ellipsis" title = "<c:out value="${product.uom}"></c:out>"  id="productuom"><c:out value="${product.uom}"></c:out></span>
            </div>
        </div>
        
          <div class="widget_grid details odd">
             <div class="widget_grid_labels">
                <span><spring:message code="ui.products.label.create.type"/></span>
            </div>
            <div class="widget_grid_description">
              <c:choose>
              <c:when test="${product.discrete}">
              
                   <span class = "ellipsis" title="<spring:message code="ui.products.label.type"/>" id="descrete"><spring:message code="ui.products.label.discrete"/></span>
          
             </c:when>
            <c:otherwise>
                  <span class = "ellipsis" title="<spring:message code="ui.products.label.type"/>" id="metered"><spring:message code="ui.products.label.metered"/></span>
          
            
             </c:otherwise>
            </c:choose>
            </div>
        </div>

      </div>
    </div>

      <input type="hidden" value="<c:out value='${product.code}'/>" id="productCode"/>
      <input type="hidden" value="<c:out value='${product.id}'/>" id="productId"/>

      <div id="productpricing_content" style="display:none;" >
      </div>

       <div id="channelpricing_content" style="display:none;">
       </div> 

      <div id="mediationrules_content" style="display:none;">
      </div> 

      <div id="tab_spinning_wheel" class="rightpanel_mainloader_panel" style="display: none;">
        <div class="rightpanel_mainloaderbox">
            <div class="rightpanel_mainloader_animatedicon">
            </div>
            <p>
              <spring:message code="label.loading"/> &hellip;
            </p>
        </div>
      </div>

  </div>
</div>



<div id="dialog_edit_product_image" title='<spring:message code="title.custom.product.image"/>' style="display: none">
</div>

<div id="dialog_add_product_default_price" title='<spring:message code="label.add.current.charges"/>' style="display: none">
</div>

<div id="dialog_view_product_channel_pricing"  title='<spring:message code="ui.label.channel.pricing"/>' style="display: none;">
</div>

<div id="dialog_retire_product" title='<spring:message htmlEscape="false" code="ui.products.label.retire.product"/>' style="display: none;">
  <div id="yes_no_div">
    <span class="catalog_datepicker_dialog_titles" style="margin-top: 30px; margin-left: 30px;"><spring:message htmlEscape="false" code="ui.product.retire.question.text"/></span>
  </div>
</div>

<div id="dialog_retire_product_nochosen" title='<spring:message htmlEscape="false" code="ui.products.label.retire.product"/>' style="display: none;">
  <div id="ok_chosen_on_yes">
    <span class="catalog_datepicker_dialog_titles" style="margin-top: 30px; margin-left: 30px; margin-right: 30px;"><spring:message htmlEscape="false" code="ui.product.retire.no.replacement.warning.message"/></span>
  </div>
</div>

<div id="dialog_retire_product_yeschosen_fail" title='<spring:message htmlEscape="false" code="ui.products.label.retire.product"/>' style="display: none;">
  <div id="ok_chosen_on_no">
    <span class="catalog_datepicker_dialog_titles" style="margin-top: 20px; margin-left: 20px;"><spring:message htmlEscape="false" code="ui.product.retire.replacment.error.message"/></span>
  </div>
</div>

<div id="dialog_retire_product_success" title='<spring:message htmlEscape="false" code="ui.products.label.retire.product"/>' style="display: none;">
  <div>
    <span class="catalog_datepicker_dialog_titles" style="margin-top: 30px; margin-left: 30px;"><spring:message htmlEscape="false" code="ui.product.retiring.succeeded"/></span>
  </div>
</div>

<div id="dialog_retire_product_failure" title='<spring:message htmlEscape="false" code="ui.products.label.retire.product"/>' style="display: none;">
  <div>
     <span class="catalog_datepicker_dialog_titles" style="margin-top: 30px; margin-left: 30px;"><spring:message htmlEscape="false" code="ui.product.retiring.failed"/></span>
  </div>
</div>

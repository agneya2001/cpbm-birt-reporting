/*
*  Copyright © 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/
$(document).ready(function () {
	initDialogWithOK("tncDialog", 750);
	  $(".tncLink").live("click",function () {
       dialogButtonsLocalizer( $("#tncDialog"), {'OK':g_dictionary.dialogOK});
       $("#tncDialog").dialog({ height: 100, width : 700 });
		  $("#tncDialog").dialog("open");
		    
		  });
});

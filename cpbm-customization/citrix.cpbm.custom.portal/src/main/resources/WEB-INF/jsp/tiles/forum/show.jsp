<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<style>
  .Wait1 {
    background:#fff;
    background-image:url(/portal/images/portal/bigrotation2.gif);
    background-repeat: no-repeat;
    background-position: center 250px;
    margin:0 0 0 -10px;
    width:1041px;
    height:968px;
    border:none;
  }
</style>


 <script type="text/javascript">
 function forum_onload(){
	 var theWaitCell = document.getElementById('Wait1');
	 theWaitCell.style.visibility = "hidden";
	 theWaitCell.className="Wait2";

 }
 </script>
 
 <div id="Wait1" class="Wait1" style="visibility: visible; " >
 </div>
  <iframe onload="forum_onload();" src="<c:out value="${forumContext}" />" width="100%" height="740" frameborder="0" >
  
  
   </iframe>
 

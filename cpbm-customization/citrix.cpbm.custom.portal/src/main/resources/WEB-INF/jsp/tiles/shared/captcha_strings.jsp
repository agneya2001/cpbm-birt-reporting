<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<script language="javascript">
// for localize the strings of reCAPTCHA
var Recaptcha_Strings = {
  visual_challenge : '<spring:message javaScriptEscape="true" code="RecaptchaStrings.visual_challenge"/>',
  audio_challenge : '<spring:message javaScriptEscape="true" code="RecaptchaStrings.audio_challenge"/>',
  refresh_btn : '<spring:message javaScriptEscape="true" code="RecaptchaStrings.refresh_btn"/>',
  instructions_visual : '<spring:message javaScriptEscape="true" code="RecaptchaStrings.instructions_visual"/>',
  instructions_context : '<spring:message javaScriptEscape="true" code="RecaptchaStrings.instructions_context"/>',
  instructions_audio : '<spring:message javaScriptEscape="true" code="RecaptchaStrings.instructions_audio"/>',
  help_btn : '<spring:message javaScriptEscape="true" code="RecaptchaStrings.help_btn"/>',
  play_again : '<spring:message javaScriptEscape="true" code="RecaptchaStrings.play_again"/>',
  cant_hear_this : '<spring:message javaScriptEscape="true" code="RecaptchaStrings.cant_hear_this"/>',
  incorrect_try_again : '<spring:message javaScriptEscape="true" code="RecaptchaStrings.incorrect_try_again"/>',
  image_alt_text : '<spring:message javaScriptEscape="true" code="RecaptchaStrings.image_alt_text"/>'
}
</script>

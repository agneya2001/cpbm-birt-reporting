package com.citrix.cpbm.portal.fragment.controllers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.vmops.web.controllers.AbstractAuthenticatedController;
import com.vmops.web.interceptors.PortalThemeResolver;

public class AbstractChannelThemeController extends AbstractAuthenticatedController {

  private static final String IMAGE_PATTERN = "(jpg|jpeg|png|gif|bmp|ico)$";

  private static final String IMAGE_FILE_TYPE = "image";

  private static final String CSS_FILE_TYPE = "css";

  private static final String CSS_CONTENT_TYPE = "text/css";

  private static final String IMAGE_CONTENT_TYPE = "image/";

  @RequestMapping(value = "/{channelid}/{filename}.{suffix}")
  public void locateResource(@ModelAttribute("globalConfiguration") Map<String, Object> globalConfiguration,
      @PathVariable String channelid, @PathVariable String filename, @PathVariable String suffix,
      HttpServletResponse response, HttpServletRequest httpServletRequest) {
    Pattern pattern = Pattern.compile(IMAGE_PATTERN);
    Matcher matcher = pattern.matcher(suffix);
    PortalThemeResolver portalThemeResolver = getChannelThemeResolver(httpServletRequest);
    String type = CSS_FILE_TYPE;

    if (matcher.matches()) {
      type = IMAGE_FILE_TYPE;
    }
    String detinationFilename = filename + "." + suffix;
    boolean hasProductCrudAuthority = (Boolean) globalConfiguration.get("hasProductCrudAuthority");
    prepareReponse(channelid, detinationFilename, type, httpServletRequest, response, portalThemeResolver,
        hasProductCrudAuthority);

  }

  private void prepareReponse(String channelid, String detinationFilename, String type,
      HttpServletRequest httpServletRequest, HttpServletResponse response, PortalThemeResolver portalThemeResolver,
      boolean hasProductCrudAuthority) {
    FileInputStream fileinputstream = null;
    String uploadRootDir = null;
    String previewChannelCode = portalThemeResolver.getPreviewChannelCode(httpServletRequest);
    if (StringUtils.isBlank(previewChannelCode) || !hasProductCrudAuthority) {
      uploadRootDir = config.getChannelBrandingDirectory(channelid, false);
    } else {
      uploadRootDir = config.getChannelBrandingDirectory(channelid, true);
    }
    if (StringUtils.isNotBlank(uploadRootDir)) {
      try {
        if (StringUtils.isNotBlank(detinationFilename)) {
          String absoluteImagePath = FilenameUtils.concat(uploadRootDir, detinationFilename);
          fileinputstream = new FileInputStream(absoluteImagePath);
          if (fileinputstream != null) {
            int numberBytes = fileinputstream.available();
            byte bytearray[] = new byte[numberBytes];
            fileinputstream.read(bytearray);
            if ("image".equals(type)) {
              response.setContentType(IMAGE_CONTENT_TYPE + FilenameUtils.getExtension(detinationFilename));
            } else {
              response.setContentType(CSS_CONTENT_TYPE);
            }
            // TODO:Set Cache headers for browser to force browser to cache to reduce load
            OutputStream outputStream = response.getOutputStream();
            response.setContentLength(numberBytes);
            outputStream.write(bytearray);
            outputStream.flush();
            outputStream.close();
            fileinputstream.close();
            return;
          }
        }
      } catch (FileNotFoundException e) {
        logger.error("###File not found exception for." + detinationFilename);
      } catch (IOException e) {
        logger.debug("###IO Exception for." + detinationFilename);
      }
    }
    response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
    response.setHeader("Location", detinationFilename);
  }
}

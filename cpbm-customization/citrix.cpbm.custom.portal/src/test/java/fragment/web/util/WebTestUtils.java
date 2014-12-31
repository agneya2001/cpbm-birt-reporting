package fragment.web.util;

import java.util.Arrays;

import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.ModelMap;

import com.vmops.model.Channel;
import com.vmops.model.ChannelBrandingConfigurations;
import com.vmops.web.forms.ChannelForm;

public class WebTestUtils {

  public static ChannelForm createChannelForm(String channelName, String description, String code, String fqdnPrefix,
      String locale, String timeZone, String[] currencyValueList, ModelMap map, HttpServletResponse servletResponse) {
    Channel channel = new Channel();
    channel.setName(channelName);
    channel.setDescription(description);
    channel.setFqdnPrefix(fqdnPrefix);
    channel.setCode(code);
    ChannelBrandingConfigurations channelBrandingConfigurations = new ChannelBrandingConfigurations();
    channelBrandingConfigurations.setChannel(channel);
    channelBrandingConfigurations.setDefaultLocale(locale);
    channelBrandingConfigurations.setDefaultTimeZone(timeZone);
    channel.setChannelBrandingConfigurations(channelBrandingConfigurations);

    ChannelForm channelForm = new ChannelForm(channel);
    if (currencyValueList != null) {
      channelForm.setCurrencies(Arrays.asList(currencyValueList));
    }
    return channelForm;

  }

  public static ChannelForm createChannelForm(String id, String channelName, String description, String code,
      String fqdnPrefix, String locale, String timeZone, String[] currencyValueList, ModelMap map,
      HttpServletResponse servletResponse) {
    ChannelForm channelForm = createChannelForm(channelName, description, code, fqdnPrefix, locale, timeZone,
        currencyValueList, map, servletResponse);
    channelForm.setChannelId(id);
    return channelForm;

  }
}

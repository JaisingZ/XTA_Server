package push;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.jiguang.commom.ClientConfig;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.SMS;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.audience.AudienceTarget;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosAlert;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import com.google.gson.JsonObject;

public class JPush {
    protected static final Logger LOG = LoggerFactory.getLogger(JPush.class);

    // demo App defined in resources/jpush-api.conf 
    private static final String appKey ="3f3fd40ab7eab687e5087ede";
    private static final String masterSecret = "f086d504b08929a3ddcfe199";

    public static String DEVICE_TOKEN = "";
    public static Map<String, String> map_extras = new HashMap<>();

    public static void launch(JSONObject jsonObject) {
        handleJson(jsonObject);
        buildPushObject_android_tag_alertWithTitle();
    }

    private static void handleJson(JSONObject jsonObject) {
        try {
            DEVICE_TOKEN = String.valueOf(jsonObject.get("device_token"));
            map_extras.clear();
            map_extras.put("type", String.valueOf(jsonObject.get("type")));
            map_extras.put("id", String.valueOf(jsonObject.get("id")));
            map_extras.put("name", String.valueOf(jsonObject.get("name")));
            map_extras.put("location", String.valueOf(jsonObject.get("location")));
//            map_extras.put("space", String.valueOf(jsonObject.get("space")));
//            map_extras.put("lat", String.valueOf(jsonObject.get("lat")));
//            map_extras.put("lot", String.valueOf(jsonObject.get("lot")));
//            map_extras.put("time", String.valueOf(jsonObject.get("time")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static Message buildPushObject_android_registerid() {
        return Message.newBuilder()
                .setMsgContent(map_extras.toString())
                .build();
    }

    public static PushPayload buildPushObject_android_tag_alertWithTitle() {
        return PushPayload.newBuilder()
                .setPlatform(Platform.android())
                .setAudience(Audience.registrationId(DEVICE_TOKEN))
                .setNotification(Notification.android("", "", map_extras))
                .build();
    }

}


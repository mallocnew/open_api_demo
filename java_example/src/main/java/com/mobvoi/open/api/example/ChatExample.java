// Copyright 2023 Mobvoi Inc. All Rights Reserved.
package com.mobvoi.open.api.example;

import com.alibaba.fastjson.JSONObject;
import com.mobvoi.open.api.tool.SignatureUtil;
import com.mobvoi.open.api.tool.SseClient;
import io.joshworks.restclient.http.HttpResponse;
import io.joshworks.restclient.http.Unirest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author sunlonggen
 * @date 2023/6/19 10:02
 **/
public class ChatExample {

  private static final String CHAT_STREAM_URL = "https://open-ka.mobvoi.com/api/chat/v2/chat";

  private static final String SET_UP_URL = "https://open.mobvoi.com/api/chat/v1/setup";

  private static final String SET_UP_UPDATE_URL = "https://open.mobvoi.com/api/chat/v1/setupUpdate";

  private static final String GET_SET_UP_STATUS_URL = "https://open.mobvoi.com/api/chat/v1/getSetupStatus";

  private static final String SUMMARY_URL = "https://open.mobvoi.com/api/chat/v1/summary";

  private static final String QA_MOBVOI_STREAM_URL = "https://open-ka.mobvoi.com/api/chat/v1/qaMobvoiStream";

  private static final String APPKEY = "你的appkey";

  private static final String SECRET = "你的appsecret";

  private static String timestamp;

  private static String signature;

  static {
    timestamp = System.currentTimeMillis()/1000 +"";
    signature = SignatureUtil.getSignature(APPKEY,SECRET,timestamp);
  }

  public static void main(String[] args) throws Exception {
    chat();
    chatStream();
    setup();
    setupUpdate();
    getStatus();
    summary();
    qaMobvoiStream();
  }

  private static void chatStream() throws Exception {
    Map<String,Object> param = new HashMap<>();
    param.put("appkey",APPKEY);
    param.put("signature",signature);
    param.put("timestamp",timestamp);
    param.put("model","uclai-small");
    param.put("temperature",0.6);
    param.put("max_tokens",2000);
    param.put("top_p",0.9);
    param.put("n",1);
    param.put("presence_penalty",0);
    param.put("frequency_penalty",0);
    param.put("stream",true);

    List<Map<String,Object>> messageList = new ArrayList<>();
    Map<String,Object> messageMap = new HashMap<>();
    messageMap.put("content","帮忙写一篇200字关于汪汪队的故事");
    messageMap.put("role","user");
    messageList.add(messageMap);

    param.put("messages",messageList);

    SseClient.ssePost(CHAT_STREAM_URL, JSONObject.toJSONString(param), new EventSourceListener() {
      @Override
      public void onClosed(@NotNull EventSource eventSource) {
        System.out.println("请求关闭");
        eventSource.cancel();
      }

      @Override
      public void onEvent(@NotNull EventSource eventSource, @Nullable String id, @Nullable String type, @NotNull String data) {
        System.out.println(data);
      }

      @Override
      public void onFailure(@NotNull EventSource eventSource, @Nullable Throwable t, @Nullable Response response) {
        try {
          String errMsg = response.body().string();
          System.out.println(errMsg);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }

      @Override
      public void onOpen(@NotNull EventSource eventSource, @NotNull Response response) {
        System.out.println("请求打开");
      }
    });
  }

  private static void chat(){
    Map<String,Object> param = new HashMap<>();
    param.put("appkey",APPKEY);
    param.put("signature",signature);
    param.put("timestamp",timestamp);
    param.put("model","uclai-small");
    param.put("temperature",0.6);
    param.put("max_tokens",2000);
    param.put("top_p",0.9);
    param.put("n",1);
    param.put("presence_penalty",0);
    param.put("frequency_penalty",0);
    param.put("stream",false);
    List<Map<String,Object>> messageList = new ArrayList<>();
    Map<String,Object> messageMap = new HashMap<>();
    messageMap.put("content","帮忙写一篇250字关于汪汪队的故事");
    messageMap.put("role","user");
    messageList.add(messageMap);

    param.put("messages",messageList);
    HttpResponse<String> response = Unirest.post(CHAT_STREAM_URL)
        .header("content-type", "application/json")
        .body(JSONObject.toJSONString(param))
        .asString();
    System.out.println(response.body());
  }

  private static void setup(){
    HttpResponse<String> response = Unirest.post(SET_UP_URL)
        .field("appkey",APPKEY)
        .field("signature",signature)
        .field("timestamp",timestamp)
        .field("roleId","1645708146818744321")
        .field("instruction","1")
        .field("roleName","AI英语老师，帮您解答各种英文相关的问题。")
        .field("callBackUrl","")
        .field("context","你现在扮演一个英语老师。注意：无论对方说什么，你只能用英文回复。你的任务是：当对话中，对方的英文内容出现问题时及时指出问题，给出正确的用法和建议的说法。详细讲解单词要点，同时给出实用的例句。当对方提出想做对话练习的时候，你能随机给一个出一个英语对话的场景，比如：天气话题、旅行话题、食物话题等。并在接下来的对话中，保持在这个场景中进行练习，在对方出现语法错误、用词不当时给出建议和例句。你的语言风格轻松幽默，且使用的词汇容易理解。你只回答和英语有关的问题。")
        .field("file", Collections.singleton(new File("/test.txt")))
        .asString();
    System.out.println(response.body());
  }

  private static void setupUpdate(){
    HttpResponse<String> response = Unirest.post(SET_UP_UPDATE_URL)
        .field("appkey",APPKEY)
        .field("signature",signature)
        .field("timestamp",timestamp)
        .field("roleId","1645708146818744321")
        .field("fileId","81026d269ed1cad7177bc9a86bb4ead9")
        .field("callBackUrl","")
        .field("files", Collections.singleton(new File("/test.txt")))
        .asString();
    System.out.println(response.body());
  }

  private static void getStatus(){
    Map<String,Object> params = new HashMap<>();
    params.put("appKey",APPKEY);
    params.put("roleId","1645708146818744321");
    params.put("fileId","81026d269ed1cad7177bc9a86bb4ead9");
    HttpResponse<String> response = Unirest.post(GET_SET_UP_STATUS_URL)
        .header("content-type", "application/json")
        .body(JSONObject.toJSONString(params))
        .asString();
    System.out.println(response.body());
  }

  private static void summary() throws Exception {
    Map<String,Object> param = new HashMap<>();
    param.put("appkey",APPKEY);
    param.put("signature",signature);
    param.put("timestamp",timestamp);
    param.put("roleId","1645708146818744321");
    param.put("fileId","81026d269ed1cad7177bc9a86bb4ead9");

    SseClient.ssePost(SUMMARY_URL, JSONObject.toJSONString(param), new EventSourceListener() {
      @Override
      public void onClosed(@NotNull EventSource eventSource) {
        System.out.println("请求关闭");
        eventSource.cancel();
      }

      @Override
      public void onEvent(@NotNull EventSource eventSource, @Nullable String id, @Nullable String type, @NotNull String data) {
        System.out.println(data);
      }

      @Override
      public void onFailure(@NotNull EventSource eventSource, @Nullable Throwable t, @Nullable Response response) {
        try {
          String errMsg = response.body().string();
          System.out.println(errMsg);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }

      @Override
      public void onOpen(@NotNull EventSource eventSource, @NotNull Response response) {
        System.out.println("请求打开");
      }
    });
  }

  private static void qaMobvoiStream() throws Exception {
    Map<String,Object> param = new HashMap<>();
    param.put("appkey",APPKEY);
    param.put("signature",signature);
    param.put("timestamp",timestamp);
    param.put("roleId","1645708146818744321");
    param.put("fileId","81026d269ed1cad7177bc9a86bb4ead9");
    param.put("question","写200字的小说");
    param.put("sessionId","1122334455");

    SseClient.ssePost(QA_MOBVOI_STREAM_URL, JSONObject.toJSONString(param), new EventSourceListener() {
      @Override
      public void onClosed(@NotNull EventSource eventSource) {
        System.out.println("请求关闭");
        eventSource.cancel();
      }

      @Override
      public void onEvent(@NotNull EventSource eventSource, @Nullable String id, @Nullable String type, @NotNull String data) {
        System.out.println(data);
      }

      @Override
      public void onFailure(@NotNull EventSource eventSource, @Nullable Throwable t, @Nullable Response response) {
        try {
          String errMsg = response.body().string();
          System.out.println(errMsg);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }

      @Override
      public void onOpen(@NotNull EventSource eventSource, @NotNull Response response) {
        System.out.println("请求打开");
      }
    });
  }
}

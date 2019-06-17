package com.example.spy;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkController {

    //再封裝一層CallBack 增加前端使用者彈性
    //且在Callback 實施Loading頁面以限制使用者在Loading時的行為
    public abstract static class NetworkControllerCallback implements Callback {
        public abstract void onSuccess(JSONObject responseJson);
        public abstract void onFailure(String errMsg);

        private Handler mainThreadHandler;
        private Dialog loadingDialog;

        public NetworkControllerCallback() {
            mainThreadHandler = new Handler(Looper.getMainLooper());
        }

        public NetworkControllerCallback enableLoadingDialog(Context context) {
            loadingDialog = new Dialog(context, R.style.FullScreenDialog);
            loadingDialog.setCancelable(false);
            loadingDialog.setContentView(LayoutInflater.from(context).inflate(R.layout.dialog_loading, null));
            loadingDialog.show();
            return this;
        }

        @Override
        public void onResponse(Call call, Response response) {
            try {
                JSONObject responseJsonObj = new JSONObject(response.body().string());

                if (responseJsonObj.getInt("status") != -1) {
                    NetworkControllerCallback.this.onFailure(responseJsonObj.getString("msg"));
                } else {
                    NetworkControllerCallback.this.onSuccess(responseJsonObj);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(Call call,final IOException e) {
            NetworkControllerCallback.this.onFailure(e.getMessage());

            mainThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, e.getMessage());
                    NetworkControllerCallback.this.onFailure(e.getMessage());
                    onComplete();
                }
            });
        }
    }

    private static final String TAG = "NetworkController";
    //

    // Root URL
    private static final String API_ROOT = "https://script.google.com/macros/s/AKfycbx139voITd8knGT5xlVBZESmPxGM61jEQoPcSTz-0y3kKc-MKJr/exec";
    private static final String TEST_ROOT = "http://104.199.254.24:3000";

    // API 路由
    private static final String API_ROUTE = "https://script.google.com/macros/s/AKfycbx139voITd8knGT5xlVBZESmPxGM61jEQoPcSTz-0y3kKc-MKJr/exec/api";
    private static final String TEST_ROUTE = "/test";

    //mediaType
    private  static final MediaType JSON = MediaType.parse("applications/json; charset=utf-8");

    // Const
    private static final String KEY_TPKEN = "x-access-token";

    // 單一實例 Singleton instance
    private static NetworkController networkController;



    // 屬性
    private OkHttpClient client;
    private String token;

    // 建構
    public static NetworkController getInstance() {
        // Singleton Pattern
        if(networkController == null) {
            networkController = new NetworkController();
            //設置實例參數
            networkController.client = new OkHttpClient(); //用來多次呼叫api的HttpClient 實體
        }
        return  networkController;
    }



    //API
    public void postRiddle (NetworkControllerCallback callback) {
        FormBody formBody = new FormBody.Builder()
                .add("command" , "getRiddle")
                .build();
        Request request =  new Request.Builder()
                .url(API_ROOT)
                .post(formBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public void testPost(String test, NetworkControllerCallback callback) {
        JSONObject jsonObject =new JSONObject();
        try {
            jsonObject.put("test", test);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());

        Request request =  new Request.Builder()
                .url(TEST_ROOT + TEST_ROUTE +"/a")
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);
    }
}

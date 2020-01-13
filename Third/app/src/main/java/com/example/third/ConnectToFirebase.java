package com.example.third;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;


public class ConnectToFirebase {
    public void sendMessage(String token){
        //메시지 가공
        JsonObject jsonObj = new JsonObject();
        //token
        Gson gson = new Gson();
        JsonElement jsonElement = gson.toJsonTree(token);
        jsonObj.add("to", jsonElement);
        //Notification
        JsonObject notification = new JsonObject();
        jsonObj.add("notification", notification);

        /*발송*/
        final MediaType mediaType = MediaType.parse("application/json");
        OkHttpClient httpClient = new OkHttpClient();
        try {
            Request request = new Request.Builder().url("https://fcm.googleapis.com/fcm/send")
                    .addHeader("Content-Type", "application/json; UTF-8")
                    .addHeader("Authorization", "key=" + "AAAA6jRKzQQ:APA91bGrso0kAV1HSFf9W34h5MIPMq9pa3sxMGUOaG-0Fv9eZ69YY9l0JFCcRzZS7AY_2cpnmRS73-7lxQrLYTApAkimEMqf9XV70dihouxMnsq8pa4-pBL9rm-EpI7Usx7VCyCknlmU")
                    .post(RequestBody.create(mediaType, jsonObj.toString())).build();
            Response response = httpClient.newCall(request).execute();
            String res = response.body().string();
            Log.d("tag", "notification response " + res);
        } catch (IOException e) {
            Log.d("tag", "Error in sending message to FCM server " + e);
        }
    }
}

package com.example.hangman;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;

public class PushNotificationManager extends FirebaseMessagingService {

    public static String MT;

    public PushNotificationManager() {

    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        MT =  token;
        System.out.println("Refreshed token: " + token);
        Log.d("SEcTag", "OnComplete: Token::::::::::::::::::::::::::::: " + token);
    }
}
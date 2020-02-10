package com.example.mapchatapp;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class KeyService extends Service {
    private final IBinder myBinder = new MyBinder();
    private KeyPair myKeyPair;
    private SharedPreferences sharePref;
    PublicKey myPublicKey;
    PrivateKey myPrivateKey;

    public KeyService() {
    }

    public class MyBinder extends Binder {
        //return binder
        KeyService getService(){
            return KeyService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        sharePref = PreferenceManager.getDefaultSharedPreferences(this);
        KeyFactory kf = null;
        try {
            kf = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (null != sharePref) {
            //Try to retrieve Key Pair from SharePreferences if exists
            String publicKey = sharePref.getString(getString(R.string.my_public_key), null);
            String privateKey = sharePref.getString(getString(R.string.my_private_key), null);
            if (null != publicKey) {
                //-----Convert String Public Key to PublicKey
                byte[] publicBytes = Base64.getDecoder().decode(publicKey.getBytes());
                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
                try {
                    myPublicKey = kf.generatePublic(keySpec);
                } catch (InvalidKeySpecException e) {
                    e.printStackTrace();
                }
                //-----Convert String Private Key to PrivateKey
                byte[] privateBytes = Base64.getDecoder().decode(privateKey.getBytes());
                keySpec = new X509EncodedKeySpec(privateBytes);
                try {
                    myPrivateKey = kf.generatePrivate(keySpec);
                } catch (InvalidKeySpecException e) {
                    e.printStackTrace();
                }
            }
        } else {
            throw new NullPointerException();
        }
        return myBinder;
    }

    public KeyPair getMyKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
        keygen.initialize(2048);
        if (null == myKeyPair){
            myKeyPair = keygen.generateKeyPair();
            SharedPreferences.Editor editor = sharePref.edit();
            editor.putString(getString(R.string.my_public_key),Base64.getEncoder().encodeToString(myKeyPair.getPublic().getEncoded()));
            editor.putString(getString(R.string.my_private_key),Base64.getEncoder().encodeToString(myKeyPair.getPrivate().getEncoded()));
            editor.commit();
            return null;
        } else {
            return myKeyPair;
        }
    }

    public void storePublicKey(String partnerName, String publicKey){
        //Store key to SharePreferences
        try {
            SharedPreferences.Editor editor = sharePref.edit();
            editor.putString(partnerName, publicKey);
            editor.commit();
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    public RSAPublicKey getPublicKey(String partnerName) throws NoSuchAlgorithmException, InvalidKeySpecException {
        //Try to retrieve key from SharePreferences if exists
        String publicKey = sharePref.getString(partnerName, null);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        if (null != publicKey){
            //Convert String key to RSAPublicKey
            byte[] publicBytes = Base64.getDecoder().decode(publicKey);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
            RSAPublicKey key = (RSAPublicKey) keyFactory.generatePublic(keySpec);
            return key;
        } else {
            return null;
        }
    }

    public String returnMyPublicKeyFromSharePref(){
        return sharePref.getString(getString(R.string.my_public_key),null);
    }

    public String returnMyPrivateKeyFromSharePref(){
        return sharePref.getString(getString(R.string.my_private_key),null);
    }

    public void resetMyKeyPair() {
        SharedPreferences.Editor editor = sharePref.edit();
        editor.remove(getString(R.string.my_public_key));
        editor.remove(getString(R.string.my_private_key));
        editor.apply();
        myKeyPair = null;
        myPublicKey = null;
        myPrivateKey = null;
    }

    public void resetKey(String partnerName){
        SharedPreferences.Editor editor = sharePref.edit();
        editor.remove(partnerName);
        editor.apply();
    }
}

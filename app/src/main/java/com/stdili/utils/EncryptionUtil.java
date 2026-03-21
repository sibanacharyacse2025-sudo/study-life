package com.stdili.utils;

import android.content.Context;
import android.util.Log;
import com.google.crypto.tink.Aead;
import com.google.crypto.tink.KeyTemplates;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.aead.AeadConfig;
import com.google.crypto.tink.integration.android.AndroidKeysetManager;

public class EncryptionUtil {
    private static final String TAG = "EncryptionUtil";
    private static final String KEYSET_NAME = "stdili_keyset";
    private static final String PREFERENCE_FILE = "stdili_prefs";
    private static final String MASTER_KEY_URI = "android-keystore://stdili_master_key";

    private Aead aead;

    public EncryptionUtil(Context context) {
        try {
            AeadConfig.register();

            KeysetHandle keysetHandle = new AndroidKeysetManager.Builder()
                    .withSharedPref(context, KEYSET_NAME, PREFERENCE_FILE)
                    .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
                    .withMasterKeyUri(MASTER_KEY_URI)
                    .build()
                    .getKeysetHandle();

            aead = keysetHandle.getPrimitive(Aead.class);
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize encryption", e);
        }
    }

    public String encrypt(String plaintext) {
        if (aead == null) return plaintext; // Fallback if encryption fails
        try {
            byte[] ciphertext = aead.encrypt(plaintext.getBytes("UTF-8"), null);
            return android.util.Base64.encodeToString(ciphertext, android.util.Base64.DEFAULT);
        } catch (Exception e) {
            Log.e(TAG, "Encryption failed", e);
            return plaintext;
        }
    }

    public String decrypt(String ciphertext) {
        if (aead == null) return ciphertext; // Fallback if decryption fails
        try {
            byte[] plaintext = aead.decrypt(android.util.Base64.decode(ciphertext, android.util.Base64.DEFAULT), null);
            return new String(plaintext, "UTF-8");
        } catch (Exception e) {
            Log.e(TAG, "Decryption failed", e);
            return ciphertext;
        }
    }
}
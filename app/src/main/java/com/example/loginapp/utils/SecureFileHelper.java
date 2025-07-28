package com.example.loginapp.utils;

import android.content.Context;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class SecureFileHelper {
    public static void writeEncryptedFile(Context context, String fileName, String plainText) throws Exception {
        String encrypted = SecureStorageHelper.encrypt(plainText);
        File file = new File(context.getFilesDir(), fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(encrypted.getBytes());
        }
    }

    public static String readEncryptedFile(Context context, String fileName) throws Exception {
        File file = new File(context.getFilesDir(), fileName);
        byte[] buffer = new byte[(int) file.length()];
        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(buffer);
        }
        return SecureStorageHelper.decrypt(new String(buffer));
    }
}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iucosoft.farmacy.utils;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author Serguei
 */
public class MD5 {

    public static String getMD5(char[] password) {

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(toBytes(password));
            BigInteger number = new BigInteger(1, messageDigest);
            String hashtext = number.toString(16);
            // Now we need to zero pad it if you actually want the full 32 chars.
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }


private static byte[] toBytes(char[] characters){
   ByteArrayOutputStream bOut = new ByteArrayOutputStream();
   PrintWriter writer = new PrintWriter(bOut);
   writer.print(characters);
   writer.close();
 
   return bOut.toByteArray();
}

}


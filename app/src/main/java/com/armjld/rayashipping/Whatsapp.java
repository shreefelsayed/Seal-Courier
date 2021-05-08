package com.armjld.rayashipping;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.telephony.SmsManager;
import android.widget.Toast;

import java.net.URLEncoder;

public class Whatsapp {

    public void openWhats(String phone, String msg, Context mContext) {
        launchWhatsappBusiness(phone, msg, mContext);
    }

   @SuppressLint("QueryPermissionsNeeded")
   private void launchWhatsappBusiness(String phone, String msg, Context mContext) {
       // --- Try To Launch Business Account
       try{
           PackageManager packageManager = mContext.getPackageManager();
           Intent i = new Intent(Intent.ACTION_VIEW);
           String url = "https://api.whatsapp.com/send?phone="+ "+2" + phone +"&text=" + URLEncoder.encode(msg, "UTF-8");
           i.setPackage("com.whatsapp.w4b");
           i.setData(Uri.parse(url));
           if (i.resolveActivity(packageManager) != null) {
               mContext.startActivity(i);
           } else {
               launchWhatsapp(phone, msg, mContext);
           }
       } catch(Exception e) {
           launchWhatsapp(phone, msg, mContext);
       }
   }

    @SuppressLint("QueryPermissionsNeeded")
    private void launchWhatsapp(String phone, String msg, Context mContext) {
        // --- Try To Launch Account
        try{
            PackageManager packageManager = mContext.getPackageManager();
            Intent i = new Intent(Intent.ACTION_VIEW);
            String url = "https://api.whatsapp.com/send?phone="+ "+2" + phone +"&text=" + URLEncoder.encode(msg, "UTF-8");
            i.setPackage("com.whatsapp.w4b");
            i.setData(Uri.parse(url));
            if (i.resolveActivity(packageManager) != null) {
                mContext.startActivity(i);
            } else {
                launchGBWhatsapp(phone, msg, mContext);
            }
        } catch(Exception e) {
            launchGBWhatsapp(phone, msg, mContext);
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void launchGBWhatsapp(String phone, String msg, Context mContext) {
        // --- Try To Launch GB Account
        try{
            PackageManager packageManager = mContext.getPackageManager();
            Intent i = new Intent(Intent.ACTION_VIEW);
            String url = "https://api.whatsapp.com/send?phone="+ "+2" + phone +"&text=" + URLEncoder.encode(msg, "UTF-8");
            i.setPackage("com.gbwhatsapp");
            i.setData(Uri.parse(url));
            if (i.resolveActivity(packageManager) != null) {
                mContext.startActivity(i);
            } else {
                Toast.makeText(mContext, "No Whatsapp", Toast.LENGTH_SHORT).show();
            }
        } catch(Exception e) {
            Toast.makeText(mContext, "No Whatsapp", Toast.LENGTH_SHORT).show();
        }
    }

    public void sendSMS(String phoneNo, String msg, Context mContext) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(mContext, "Message Sent", Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(mContext,ex.getMessage().toString(), Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }


}

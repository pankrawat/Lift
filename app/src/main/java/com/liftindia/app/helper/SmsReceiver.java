package com.liftindia.app.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

//////////////////////////////////////////////////////////////////////////////////////////////
//																						    //
//	--> At Instance Level															    	//
//	SmsReceiver smsReceiver;													    		//
//																			    			//
//	--> In onCreate()															    		//
// 	smsReceiver = new SmsReceiver();											    		//
//	IntentFilter intentFilter=new IntentFilter("android.provider.Telephony.SMS_RECEIVED");	//
//	intentFilter.setPriority(778777);														//
//	registerReceiver(smsReceiver,intentFilter);												//
//																				    		//
//	--> In onDestroy()																    	//
//	unregisterReceiver(smsReceiver);													    //
//																					    	//
//////////////////////////////////////////////////////////////////////////////////////////////

public class SmsReceiver extends BroadcastReceiver {
    final String PDUS = "pdus";
    int OTP_DIGIT_COUNT = 4; // change otp digit count
//    String PATTERN = "(|^)\\d{" + OTP_DIGIT_COUNT + "}";
    SmsMessage smsMessages[];
    String msg_from;
    String msgBody;

    public SmsReceiver(int OTP_DIGIT_COUNT) {
        this.OTP_DIGIT_COUNT = OTP_DIGIT_COUNT;
    }

    public interface OnSmsReceived {
        void onParseCompleted(String otp);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("Sms Receiver", "Called");
        final Bundle bundle = intent.getExtras();
        String PATTERN = "(|^)\\d{" + OTP_DIGIT_COUNT + "}";
        Pattern p = Pattern.compile(PATTERN);
        try {
            /**Extract sms*/
            if (bundle != null) {
                final Object[] pdus = (Object[]) bundle.get(PDUS);
                smsMessages = new SmsMessage[pdus.length];
                for (int i = 0; i < smsMessages.length; i++)   //Msg Read
                {
                    smsMessages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    msg_from = smsMessages[i].getOriginatingAddress();
                    msgBody = smsMessages[i].getMessageBody();
                }

                /*Now extract the otp*/
                if (msgBody != null) {
                    Matcher m = p.matcher(msgBody);
                    if (m.find()) {
                        ((OnSmsReceived) context).onParseCompleted(m.group(0));
                        //otp read successfully
                    } else {
                        //something went wrong
                    }
                }
            }
        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" + e);

        }
    }
}

        /*try {
            if (bundle != null) {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (int i = 0; i < pdusObj.length; i++) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();


                    String regex = "[0-9]+";
                    //if(phoneNumber.equals("+18663581902") || phoneNumber.equals("59011")){
                    //Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
                    Pattern pattern = Pattern.compile("(|^)\\d{4}");
                    Matcher matcher = pattern.matcher(message);
                    if (matcher.matches()) {
                        if (message.length() == 6) {
                            ((OnSmsReceived) context).onParseCompleted(message);
                        }
                    }
                    //}
                    //Log.e("SmsReceiver", "senderNum: " + senderNum + "; message: " + message);
                    //int duration = Toast.LENGTH_LONG;
                    //Toast toast = Toast.makeText(context, "senderNum: " + senderNum + ", message: " + message, duration);
                    //toast.show();

                }
            }

        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" + e);
        }*/

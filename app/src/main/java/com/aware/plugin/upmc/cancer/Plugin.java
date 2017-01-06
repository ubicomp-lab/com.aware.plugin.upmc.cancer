package com.aware.plugin.upmc.cancer;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.aware.Aware;
import com.aware.ESM;
import com.aware.providers.ESM_Provider;
import com.aware.ui.PermissionsHandler;
import com.aware.ui.esms.ESMFactory;
import com.aware.ui.esms.ESM_Radio;
import com.aware.utils.Aware_Plugin;
import com.aware.utils.Scheduler;

import org.json.JSONException;

import java.util.Random;

public class Plugin extends Aware_Plugin {

    public static String ACTION_CANCER_SURVEY = "ACTION_CANCER_SURVEY";
    public static String ACTION_CANCER_EMOTION = "ACTION_CANCER_EMOTION";

    @Override
    public void onCreate() {
        super.onCreate();

        TAG = "UPMC Cancer";

        DATABASE_TABLES = Provider.DATABASE_TABLES;
        TABLES_FIELDS = Provider.TABLES_FIELDS;
        CONTEXT_URIS = new Uri[]{Provider.Symptom_Data.CONTENT_URI, Provider.Motivational_Data.CONTENT_URI};

//        IntentFilter filter = new IntentFilter();
//        filter.addAction(ACTION_CANCER_SURVEY);
//        filter.addAction(ACTION_CANCER_EMOTION);
//        filter.addAction(ESM.ACTION_AWARE_ESM_QUEUE_COMPLETE);
//        registerReceiver(surveyListener, filter);

        REQUIRED_PERMISSIONS.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        REQUIRED_PERMISSIONS.add(Manifest.permission.ACCESS_FINE_LOCATION);
        REQUIRED_PERMISSIONS.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        REQUIRED_PERMISSIONS.add(Manifest.permission.READ_CALL_LOG);
        REQUIRED_PERMISSIONS.add(Manifest.permission.READ_CONTACTS);
        REQUIRED_PERMISSIONS.add(Manifest.permission.READ_SMS);
        REQUIRED_PERMISSIONS.add(Manifest.permission.READ_PHONE_STATE);

        Aware.startPlugin(this, "com.aware.plugin.upmc.cancer");
    }

    public static class SurveyListener extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_CANCER_SURVEY)) {
                Intent surveyService = new Intent(context, Survey.class);
                context.startService(surveyService);
            }

            if (intent.getAction().equals(ACTION_CANCER_EMOTION)) {
//                //Check if we already had something waiting but the participant has not answered
//                //If we do, set the old as expired
//                Cursor pending_esms = context.getContentResolver().query(ESM_Provider.ESM_Data.CONTENT_URI, null, ESM_Provider.ESM_Data.STATUS + "=" + ESM.STATUS_NEW, null, null);
//                boolean pending = false;
//                if (pending_esms != null && pending_esms.getCount() > 0) {
//                    pending = true;
//                }
//                if (pending_esms != null && !pending_esms.isClosed()) pending_esms.close();
//
//                if (pending) {
//                    ContentValues newStatus = new ContentValues();
//                    newStatus.put(ESM_Provider.ESM_Data.STATUS, ESM.STATUS_EXPIRED);
//                    context.getContentResolver().update(ESM_Provider.ESM_Data.CONTENT_URI, newStatus, ESM_Provider.ESM_Data.STATUS + "=" + ESM.STATUS_NEW, null);
//                }

                try {
                    ESMFactory factory = new ESMFactory();

                    ESM_Radio angry = new ESM_Radio();
                    angry.addRadio("NO");
                    angry.addRadio("No");
                    angry.addRadio("Yes");
                    angry.addRadio("YES");
                    angry.setSubmitButton("Next");
                    angry.setNotificationRetry(3);
                    angry.setNotificationTimeout(10);
                    angry.setTitle("Angry/frustrated");
                    angry.setInstructions("Are you angry/frustrated?");

                    ESM_Radio happy = new ESM_Radio();
                    happy.addRadio("NO");
                    happy.addRadio("No");
                    happy.addRadio("Yes");
                    happy.addRadio("YES");
                    happy.setSubmitButton("Next");
                    happy.setNotificationRetry(3);
                    happy.setNotificationTimeout(10);
                    happy.setTitle("Happy");
                    happy.setInstructions("Are you happy?");

                    ESM_Radio stressed = new ESM_Radio();
                    stressed.addRadio("NO");
                    stressed.addRadio("No");
                    stressed.addRadio("Yes");
                    stressed.addRadio("YES");
                    stressed.setSubmitButton("Thanks!");
                    stressed.setNotificationRetry(3);
                    stressed.setNotificationTimeout(10);
                    stressed.setTitle("Stressed/nervous");
                    stressed.setInstructions("Are you stressed/nervous?");

                    factory.addESM(angry);
                    factory.addESM(happy);
                    factory.addESM(stressed);

                    Intent esm = new Intent(ESM.ACTION_AWARE_QUEUE_ESM);
                    esm.putExtra(ESM.EXTRA_ESM, factory.build());
                    context.sendBroadcast(esm);

                } catch (JSONException e) { e.printStackTrace(); }
            }

//            if( intent.getAction().equals(ESM.ACTION_AWARE_ESM_QUEUE_COMPLETE) ) {
//
//                Calendar now = Calendar.getInstance();
//                now.setTimeInMillis(System.currentTimeMillis());
//
//                Calendar today = Calendar.getInstance();
//                today.setTimeInMillis(System.currentTimeMillis());
//                today.set(Calendar.HOUR_OF_DAY, 1);
//                today.set(Calendar.MINUTE, 0);
//                today.set(Calendar.SECOND, 0);
//
//                int total = 0;
//                Cursor esms_count = context.getContentResolver().query(ESM_Provider.ESM_Data.CONTENT_URI, null, ESM_Provider.ESM_Data.TRIGGER + " LIKE '" + context.getPackageName() + "' AND " + ESM_Provider.ESM_Data.TIMESTAMP + " > " + today.getTimeInMillis(), null, null);
//                if( esms_count != null ) {
//                    total = esms_count.getCount()/3;
//                }
//                if( esms_count != null && ! esms_count.isClosed() ) esms_count.close();
//
//                if( total < Integer.parseInt(Aware.getSetting(context, Settings.PLUGIN_UPMC_CANCER_MAX_PROMPTS)) ) {
//                    int start;
//                    int end;
//
//                    if ( now.get(Calendar.HOUR_OF_DAY) >= Integer.parseInt(Aware.getSetting(context, Settings.PLUGIN_UPMC_CANCER_EVENING_HOUR)) || now.get(Calendar.HOUR_OF_DAY) + 2 >= Integer.parseInt(Aware.getSetting(context, Settings.PLUGIN_UPMC_CANCER_EVENING_HOUR))) {
//                        //too late in the evening, set to beginning of the day
//                        start = 8;
//                        end = 10;
//                    } else {
//                        //still time today
//                        start = now.get(Calendar.HOUR_OF_DAY) + 1;
//                        end = now.get(Calendar.HOUR_OF_DAY) + 2;
//                    }
//
//                    int random_hour = getRandomNumberRangeInclusive(start, end);
//
//                    //Schedule for sometime in the next 2 hours
//                    try {
//                        Scheduler.Schedule schedule = new Scheduler.Schedule("cancer_emotion");
//                        schedule.addHour(random_hour);
//                        schedule.setActionType(Scheduler.ACTION_TYPE_BROADCAST)
//                                .setActionClass(ACTION_CANCER_EMOTION);
//                        Scheduler.saveSchedule(context, schedule);
//
//                        if( DEBUG ) Log.d(TAG, "Scheduled random to: " + random_hour);
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    try {
//                        //Done for today, schedule more for tomorrow again
//                        int random_hour = getRandomNumberRangeInclusive(8, 10);
//
//                        Scheduler.Schedule schedule = new Scheduler.Schedule("cancer_emotion");
//                        schedule.addHour(random_hour);
//                        schedule.setActionType(Scheduler.ACTION_TYPE_BROADCAST)
//                                .setActionClass(ACTION_CANCER_EMOTION);
//                        Scheduler.saveSchedule(context, schedule);
//
//                        if( DEBUG ) Log.d(TAG, "Scheduled random to: " + random_hour);
//
//                    }catch ( JSONException e ) {
//                        e.printStackTrace();
//                    }
//                }
//            }
        }
    }

    private static SurveyListener surveyListener = new SurveyListener();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        boolean permissions_ok = true;
        for (String p : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) {
                permissions_ok = false;
                break;
            }
        }

        if (permissions_ok) {
            Aware.setSetting(this, Settings.STATUS_PLUGIN_UPMC_CANCER, true);

            if (Aware.getSetting(this, Settings.PLUGIN_UPMC_CANCER_MAX_PROMPTS).length() == 0) {
                Aware.setSetting(this, Settings.PLUGIN_UPMC_CANCER_MAX_PROMPTS, 8);
            }
            if (DEBUG)
                Log.d(TAG, "Max questions per day: " + Aware.getSetting(this, Settings.PLUGIN_UPMC_CANCER_MAX_PROMPTS));

            if (Aware.getSetting(this, Settings.PLUGIN_UPMC_CANCER_PROMPT_INTERVAL).length() == 0) {
                Aware.setSetting(this, Settings.PLUGIN_UPMC_CANCER_PROMPT_INTERVAL, 30);
            }
            if (DEBUG)
                Log.d(TAG, "Minimum interval between questions: " + Aware.getSetting(this, Settings.PLUGIN_UPMC_CANCER_PROMPT_INTERVAL) + " minutes");

            if (intent != null && intent.getExtras() != null && intent.getBooleanExtra("schedule", false)) {
                final int morning_hour = Integer.parseInt(Aware.getSetting(this, Settings.PLUGIN_UPMC_CANCER_MORNING_HOUR));
                final int morning_minute = Integer.parseInt(Aware.getSetting(this, Settings.PLUGIN_UPMC_CANCER_MORNING_MINUTE));
                final int evening_hour = Integer.parseInt(Aware.getSetting(this, Settings.PLUGIN_UPMC_CANCER_EVENING_HOUR));
                final int evening_minute = Integer.parseInt(Aware.getSetting(this, Settings.PLUGIN_UPMC_CANCER_EVENING_MINUTE));
                final int max_prompts = Integer.parseInt(Aware.getSetting(this, Settings.PLUGIN_UPMC_CANCER_MAX_PROMPTS));
                final int min_interval = Integer.parseInt(Aware.getSetting(this, Settings.PLUGIN_UPMC_CANCER_PROMPT_INTERVAL));

                try {
                    Scheduler.Schedule schedule = new Scheduler.Schedule("cancer_survey_morning");
                    schedule.addHour(morning_hour)
                            .addMinute(morning_minute)
                            .setActionIntentAction(Plugin.ACTION_CANCER_SURVEY)
                            .setActionType(Scheduler.ACTION_TYPE_BROADCAST);
                    Scheduler.saveSchedule(this, schedule);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {

                    Scheduler.Schedule schedule = new Scheduler.Schedule("cancer_survey_evening");
                    schedule.addHour(evening_hour)
                            .addMinute(evening_minute)
                            .setActionType(Scheduler.ACTION_TYPE_BROADCAST)
                            .setActionClass(Plugin.ACTION_CANCER_SURVEY);
                    Scheduler.saveSchedule(this, schedule);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    Scheduler.Schedule schedule = new Scheduler.Schedule("cancer_emotion");
                    schedule.random(max_prompts, min_interval);
                    schedule.setActionType(Scheduler.ACTION_TYPE_BROADCAST)
                            .setActionIntentAction(Plugin.ACTION_CANCER_EMOTION);

                    Scheduler.saveSchedule(this, schedule);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Intent permissions = new Intent(this, PermissionsHandler.class);
            permissions.putExtra(PermissionsHandler.EXTRA_REQUIRED_PERMISSIONS, REQUIRED_PERMISSIONS);
            permissions.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(permissions);
        }

//        TAG = "UPMC-Cancer";
//        DEBUG = Aware.getSetting(getApplicationContext(), Aware_Preferences.DEBUG_FLAG).equals("true");
//
//        if( intent != null && intent.getExtras() != null && intent.getBooleanExtra("schedule", false) ) {
//            if (Aware.getSetting(this, Settings.PLUGIN_UPMC_CANCER_MORNING_HOUR).length() > 0 && Aware.getSetting(this, Settings.PLUGIN_UPMC_CANCER_EVENING_HOUR).length() > 0) {
//                final int morning_hour = Integer.parseInt(Aware.getSetting(this, Settings.PLUGIN_UPMC_CANCER_MORNING_HOUR));
//                final int evening_hour = Integer.parseInt(Aware.getSetting(this, Settings.PLUGIN_UPMC_CANCER_EVENING_HOUR));
//
//                try {
//
//                    Scheduler.Schedule schedule = new Scheduler.Schedule("cancer_survey_morning");
//                    schedule.addHour(morning_hour)
//                            .setActionType(Scheduler.ACTION_TYPE_BROADCAST)
//                            .setActionClass(Plugin.ACTION_CANCER_SURVEY);
//                    Scheduler.saveSchedule(this, schedule);
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                try {
//
//                    Scheduler.Schedule schedule = new Scheduler.Schedule("cancer_survey_evening");
//                    schedule.addHour(evening_hour)
//                            .setActionType(Scheduler.ACTION_TYPE_BROADCAST)
//                            .setActionClass(Plugin.ACTION_CANCER_SURVEY);
//                    Scheduler.saveSchedule(this, schedule);
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                try {
//
//                    //Schedule for sometime in the next 2 hours
//                    Calendar now = Calendar.getInstance();
//                    now.setTimeInMillis(System.currentTimeMillis());
//
//                    int start;
//                    int end;
//
//                    if( now.get(Calendar.HOUR_OF_DAY) >= Integer.parseInt(Aware.getSetting(this, Settings.PLUGIN_UPMC_CANCER_EVENING_HOUR)) || now.get(Calendar.HOUR_OF_DAY) + 2 >= Integer.parseInt(Aware.getSetting(this, Settings.PLUGIN_UPMC_CANCER_EVENING_HOUR)) ) {
//                        //too late today, set to the beginning of the day
//                        start = 8;
//                        end = 10;
//                    } else {
//                        //still time today
//                        start = now.get(Calendar.HOUR_OF_DAY) + 1;
//                        end = now.get(Calendar.HOUR_OF_DAY) + 2;
//                    }
//
//                    int random_hour = getRandomNumberRangeInclusive(start, end);
//
//                    Scheduler.Schedule schedule = new Scheduler.Schedule("cancer_emotion");
//                    schedule.addHour(random_hour);
//                    schedule.setActionType(Scheduler.ACTION_TYPE_BROADCAST)
//                            .setActionClass(ACTION_CANCER_EMOTION);
//                    Scheduler.saveSchedule(this, schedule);
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }

        return super.onStartCommand(intent, flags, startId);
    }

//    public static int getRandomNumberRangeInclusive(int min, int max) {
//        Random foo = new Random();
//        return foo.nextInt((max + 1) - min) + min;
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(surveyListener);

        Aware.setSetting(this, Settings.STATUS_PLUGIN_UPMC_CANCER, false);
        Aware.stopAWARE();
    }
}

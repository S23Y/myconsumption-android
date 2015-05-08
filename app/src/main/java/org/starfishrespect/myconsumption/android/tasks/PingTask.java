package org.starfishrespect.myconsumption.android.tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Task that checks if a server is alive
 */
public class PingTask extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = "PingTask";

    public interface PingResultCallback {
        public void pingResult(String url, boolean accessible);
    }

    private PingResultCallback pingResultCallback;
    private String url;

    private PingTask(String url) {
        super();
        this.url = url;
    }

    public static void ping(String url, PingResultCallback callback) {
        if (callback == null) {
            throw new NullPointerException("Callback must not be null !");
        }
        PingTask task = new PingTask(url);
        task.pingResultCallback = callback;
        task.execute();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        Log.d(TAG, "pinging...");
        try {
            InetAddress in = InetAddress.getByName(url);
            return in.isReachable(1500);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        Log.d(TAG, "Ping status is " + aBoolean);
        pingResultCallback.pingResult(url, aBoolean);
    }
}

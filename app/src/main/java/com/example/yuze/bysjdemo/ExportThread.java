package com.example.yuze.bysjdemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;

/**
 * Created by yuze on 2018/4/12.
 */

public class ExportThread extends Thread {
    private ProgressDialog mProgress;
    private Location lThis;
    private long footprintId = -1;
    private boolean email = false;

    public ExportThread(Location a, long t, ProgressDialog m, boolean b) {
        super();
        mProgress = m;
        footprintId = t;
        lThis = a;
        email = b;
    }

    public void run() {
        try {
            String fname;
            Cursor ltrip;
            int cntProgress;
            if (footprintId == -1) {
                fname = "/sdcard/all-andtriplog.gpx";
                ltrip = lThis.mAndTripLogDB.getAllTripId();
                cntProgress = lThis.mAndTripLogDB.getAllTripTrkCount();
            } else {
                fname = "/sdcard/" + footprintId + "-andtriplog.gpx";
                ltrip = lThis.mAndTripLogDB.getTripId(footprintId);
                cntProgress = lThis.mAndTripLogDB.getTripTrkCount(footprintId);
            }
            GpsFileWriter fw = new GpsFileWriter(fname, false);
            int cnt = ltrip.getCount();
            mProgress.setProgress(0);
            mProgress.setMax(cntProgress);
            for (int i = 0; i < cnt; i++) {
                if (!isInterrupted()) {
                    ltrip.moveToPosition(i);
                    long id = ltrip.getLong(0);
                    fw.startTrk("Trip " + id);
                    Cursor l = lThis.mAndTripLogDB.getTripData(id);
                    fw.fromCursorString(l, mProgress, this);
                    fw.stopTrk();
                } else {
                    Log.w("trip", "Interruped");
                }
            }
            fw.close();
            if (email && !isInterrupted()) {
                Intent i = new Intent(android.content.Intent.ACTION_SEND);
                i.setType("image/jpeg");
                i.putExtra(android.content.Intent.EXTRA_SUBJECT, "The gpx file");
                i.putExtra(android.content.Intent.EXTRA_STREAM, Uri.parse("file://" + fname));
                lThis.startActivity(Intent.createChooser(i, "Send footprint to..."));
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.w("trip", "Unable to save");
        }
        mProgress.dismiss();
        mProgress.setProgress(0);
    }
}



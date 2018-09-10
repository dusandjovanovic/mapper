package com.dushan.dev.mapper.Threads;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.dushan.dev.mapper.R;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.UUID;

public class BluetoothReceiverService extends Service {

    private BluetoothAdapter mBluetoothAdapter;
    private AcceptThread THREAD;


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (mBluetoothAdapter != null) {
            THREAD = new AcceptThread();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    THREAD.run();
                }
            }).start();

        } else {
            Toast.makeText(getApplicationContext(), "Bluetooth service unavailable.", Toast.LENGTH_SHORT).show();
        }
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    public void onDestroy() {
        THREAD.cancel();
        super.onDestroy();

    }

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        private AcceptThread() {
            BluetoothServerSocket tmp = null;

            try {
                UUID uuid = UUID.fromString(getApplicationContext().getResources().getString(R.string.BASE_UUID));
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(getApplicationContext().getResources().getString(R.string.app_name), uuid);
            } catch (IOException e) {
            }
            mmServerSocket = tmp;

        }

        public void run() {
            BluetoothSocket socket = null;
            while (true) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    break;
                }

                if (socket != null) {
                    try {
                        String id = FirebaseAuth.getInstance().getUid();
                        try {
                            byte[] idByteArray = id.getBytes();
                            socket.getOutputStream().write(idByteArray);
                        } catch (NullPointerException ex) {

                        }
                    } catch (IOException ex) {

                    }

                    try {
                        mmServerSocket.close();
                    } catch (IOException e) {
                        break;
                    }

                    break;
                }
            }
        }

        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
            }
        }
    }
}

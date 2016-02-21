package com.fairy.mic4pc;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.media.AudioRecord;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BluetoothSender {
    private static final String TAG = "BluetoothSender";
    private BluetoothDevice rDevice = null;
    private BluetoothSocket mSocket = null;
    private Sender mSender = null;
    private AudioRecord mAudioRecord = null;
    private static UUIDGen mUUIDGen = null;
    private Handler mHandler = null;
    private long time0;

    public BluetoothSender(Handler h, BluetoothDevice device, AudioRecord ar) {
        Log.e(TAG, "BluetoothSender create");
        mHandler = h;
        rDevice = device;
        mUUIDGen = new UUIDGen(rDevice.getAddress());
        mAudioRecord = ar;
    }

    public void connect() {
        new Thread() {
            public void run() {
                Log.e(TAG, "start");
                while (true) {
                    try {
//                        mSocket = rDevice
//                                .createRfcommSocketToServiceRecord(mUUIDGen.next());
//                        System.out.println("connect");
//                        mSocket.connect();
//                        time0 = new Date().getTime();
//                        mSocket = (BluetoothSocket) rDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(rDevice, 1);
//                        time0 = new Date().getTime();
//                        mSocket.connect();

                        Class<?> clazz = rDevice.getClass();
                        Class<?>[] paramTypes = new Class<?>[]{Integer.TYPE};

                        Method m = clazz.getMethod("createRfcommSocket", paramTypes);
                        Object[] params = new Object[]{Integer.valueOf(1)};

                        mSocket = (BluetoothSocket) m.invoke(rDevice, params);
                        mSocket.connect();

                        break;
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } finally {
                        if (!mSocket.isConnected()) {
                            show(CommConst.CONNECT_FAIL);
                            BluetoothSender.this.stop();
                            return;
                        }

                    }
                }
                try {
                    DataOutputStream bo = new DataOutputStream(mSocket.getOutputStream());

                    bo.writeInt(MyAudioRecord.hz);
//                    bo.writeInt(MyAudioRecord.bits);
//                    bo.writeInt(MyAudioRecord.channel);
                    bo.writeInt(MyAudioRecord.bufSize);
                    bo.flush();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                show(CommConst.CONNECT_SUCCESS);
            }
        }.start();

    }

    public void pause() {
        Log.e(TAG, "BluetoothSender Pause");
        if (mSender != null && mSender.isAlive()){
            mSender.pause();
        }
    }

    public void stop() {
        Log.e(TAG, "BluetoothSender Stop");
        if (mSender != null && mSender.isAlive())
            mSender.kill();
        if (mSocket != null)
            try {
                mSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
    }

    public void start() {
        Log.e(TAG, "start");
        if (mSender == null) {
            mSender = new Sender(mSocket, mAudioRecord);
            mSender.start();
        } else {
            mSender.restart();
        }
    }

    private void show(char warn) {
        Bundle bundle = new Bundle();
        bundle.putChar(CommConst.YOUR_TYPE, warn);
        Message msg = mHandler.obtainMessage(warn);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    private class Sender extends Thread {
        private DataOutputStream dataout;
        private AudioRecord mAudioRecord = null;

        public boolean isPauseFlag() {
            return pauseFlag;
        }

        public void setPauseFlag(boolean pauseFlag) {
            this.pauseFlag = pauseFlag;
        }

        public boolean isStopFlag() {
            return stopFlag;
        }

        public void setStopFlag(boolean stopFlag) {
            this.stopFlag = stopFlag;
        }

        private volatile boolean pauseFlag;
        private volatile boolean stopFlag;

        public Sender(BluetoothSocket socket, AudioRecord ar) {
            try {
                Log.e(TAG, "sender xxxx");
                mAudioRecord = ar;
                dataout = new DataOutputStream(socket.getOutputStream());
                pauseFlag = false;
                stopFlag = false;
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

//        public byte[] intToByteArray(long t) {
//            int len = 8;
//            byte[] result = new byte[len];
//            for (int i = 0; i < len; i++) {
//                result[i] = (byte) ((t >> (len - 1 - i) * 8) & 0xFF);
//            }
//            return result;
//        }

//        public byte[] newByte(long t, byte[] bytes) {
//            byte[] ans = new byte[8 + bytes.length];
//            byte[] tmp = intToByteArray(t);
//            for (int i = 0; i < 8; i++)
//                ans[i] = tmp[i];
//            for (int i = 0; i < bytes.length; i++)
//                ans[i + 8] = bytes[i];
//            return ans;
//        }

        @SuppressWarnings("finally")
        public void run() {
            try {
                Log.e(TAG, "Sender reading");
                byte[] bytesCopy;
                mAudioRecord.startRecording();
                while (true) {
                    mAudioRecord.read(MyAudioRecord.bytes, 0, MyAudioRecord.bufSize);
                    bytesCopy = MyAudioRecord.bytes.clone();
                    //String temp=new String(bytesCopy);
                    //System.out.println(temp);
//                    byte[] tempbyte;
//                    if (!pauseFlag) {
//                        tempbyte = newByte(new Date().getTime() - time0, bytesCopy);
//                    } else {
//                        tempbyte = newByte(0, bytesCopy);
//                    }
//                    if (!stopFlag) {
//                        dataout.write(tempbyte, 0, tempbyte.length);
//                        dataout.flush();
//                    }
                    if (!stopFlag) {
                        dataout.write(bytesCopy, 0, bytesCopy.length);
                        dataout.flush();
                    }
                    //mAudioRecord.byteList.add(bytesCopy);
                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.e(TAG, "Sender crash");
                BluetoothSender.this.show(CommConst.LOST_SERVER);
            } finally {
                BluetoothSender.this.stop();
                Log.e(TAG, "Sender stop");
                return;
            }
        }

        public void pause() {
            Log.e(TAG, "Sender pause");
            pauseFlag = true;
        }

        public void restart() {
            Log.e(TAG, "Sender resume");
            pauseFlag = false;
        }

        public void kill() {
            Log.e(TAG, "Sender kill");
            pauseFlag = true;
            stopFlag = true;
            byte[] tempbyte = new byte[648];
            for (int i = 0; i < 648; i++) {
                tempbyte[i] = (byte) 0xEF;
            }
            try {
                dataout.write(tempbyte, 0, tempbyte.length);
                dataout.flush();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

}

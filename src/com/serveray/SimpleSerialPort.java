package com.serveray;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


public class SimpleSerialPort {

    private InputStream in;
    private OutputStream out;
    private SerialPort serialPort;
    private boolean isOpen;

    public SimpleSerialPort() {
        super();
    }

    void connect(String portName) throws Exception {
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        if (portIdentifier.isCurrentlyOwned()) {
            System.out.println("Error: Port is currently in use");
        } else {
            CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);

            if (commPort instanceof SerialPort) {
                serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

                in = serialPort.getInputStream();
                out = serialPort.getOutputStream();

                isOpen = true;
                (new Thread(new SerialReader(in, this))).start();

            } else {
                System.out.println("Error: Only serial ports are handled by this example.");
            }
        }
    }

    public void send(String data) {
        try {
            out.write(data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        if (serialPort != null) {
            isOpen = false;
            in = null;
            out = null;

            serialPort.close();
            serialPort = null;
        }
    }

    public static class SerialReader implements Runnable {
        InputStream in;
        SimpleSerialPort port;

        public SerialReader(InputStream in, SimpleSerialPort serialPort) {
            this.in = in;
            this.port = serialPort;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int len = -1;
            try {
                while ((len = this.in.read(buffer)) > -1 && port.isOpen) {
                    if (len > 0) port.trigger(new String(buffer, 0, len));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    List<OnReceiveListener> onReceiveListeners = new ArrayList<OnReceiveListener>();

    public void addOnReceiveListener(OnReceiveListener onReceiveListener) {
        onReceiveListeners.add(onReceiveListener);
    }

    public void trigger(String msg) {
        for (int i = 0; i < onReceiveListeners.size(); i++) {
            onReceiveListeners.get(i).onEvent(msg);
        }
    }
}
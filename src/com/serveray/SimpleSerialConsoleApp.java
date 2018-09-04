package com.serveray;

import gnu.io.CommPortIdentifier;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Enumeration;

public class SimpleSerialConsoleApp {
    private static JFrame mainFrame;
    private JPanel pnlMain;
    private JTextField txtMsg;
    private JButton sendButton;
    private JTextArea txtLog;
    private JComboBox cbxPorts;
    private JButton connectButton;
    private JScrollPane scpLog;
    private SimpleSerialPort serialPort;

    public SimpleSerialConsoleApp() {

        Enumeration ports = CommPortIdentifier.getPortIdentifiers();

        while (ports.hasMoreElements()) {
            CommPortIdentifier cpIdentifier = (CommPortIdentifier) ports.nextElement();
            cbxPorts.addItem(cpIdentifier.getName());
        }


        serialPort = new SimpleSerialPort();
        try {

            serialPort.addOnReceiveListener(new OnReceiveListener() {
                @Override
                public void onEvent(String msg) {
                    txtLog.append(msg);
                    txtLog.setCaretPosition(txtLog.getDocument().getLength());
                }
            });
        } catch (Exception err) {
            err.printStackTrace();
        }


        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMsg();
            }
        });

        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (connectButton.getText() == "Connect") {
                    try {
                        serialPort.connect(cbxPorts.getSelectedItem().toString());
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }

                    connectButton.setText("Disconnect");
                } else {
                    serialPort.disconnect();
                    connectButton.setText("Connect");
                }
            }
        });

        txtMsg.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
//                super.keyPressed(e);
                if (e.getKeyCode() == 10) {
                    sendMsg();
                }
            }
        });
    }

    private void sendMsg() {
        serialPort.send(txtMsg.getText() + "\r\n");
        txtMsg.setText("");
    }

    public static void main(String[] args) {

        mainFrame = new JFrame("Simple Serial Console App");
        mainFrame.setContentPane(new SimpleSerialConsoleApp().pnlMain);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.pack();
        mainFrame.setVisible(true);

    }
}

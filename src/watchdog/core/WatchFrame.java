/*
 * Copyright 2016 Anurag Gautam
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package watchdog.core;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by Dell on 15-05-2016.
 */
public class WatchFrame extends JFrame {
    private JTextArea textArea1;
    private JButton startServiceButton;
    private JButton stopServiceButton;
    private JPanel panel1;
    private Thread thread;
    private Observer observer;

    public WatchFrame() {
        super("WatchDog [Beta]");
        startServiceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = textArea1.getText();
                if (text != null && !text.isEmpty()) {
                    handleInput(text);
                    textArea1.setEnabled(false);
                    startServiceButton.setEnabled(false);
                    stopServiceButton.setEnabled(true);
                }
            }
        });
        stopServiceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (thread.isAlive()) {
                    observer.shutdown();
                }
                textArea1.setEnabled(true);
                startServiceButton.setEnabled(true);
                stopServiceButton.setEnabled(false);
            }
        });

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationByPlatform(true);
        add(panel1);
        pack();
    }

    private void handleInput(String text) {
        String[] dirs = text.replaceAll("[\n\t\r ]+", " ").trim().split(",");
        System.out.println(Arrays.toString(dirs));
        try {
            observer = new Observer(path -> {
                System.out.println(path);
                return null;
            }, dirs);
            thread = new Thread(observer);
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        WatchFrame wf = new WatchFrame();

        SwingUtilities.invokeLater(() -> {
            wf.setVisible(true);
        });
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}

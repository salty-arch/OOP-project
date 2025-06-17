package org.database.util;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Utility class for GUI transitions
public class TransitionUtils {

    public static void fadeIn(JFrame frame) {
        Timer timer = new Timer(20, null);
        timer.addActionListener(new ActionListener() {
            float opacity = 0f;

            @Override
            public void actionPerformed(ActionEvent e) {
                opacity += 0.05f;
                if (opacity >= 1f) {
                    opacity = 1f;
                    timer.stop();
                }
                frame.setOpacity(opacity);
            }
        });

        frame.setOpacity(0f); // start fully transparent
        frame.setVisible(true);
        timer.start();
    }
}

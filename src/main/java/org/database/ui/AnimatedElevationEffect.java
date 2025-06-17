package org.database.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Timer;  // Changed from java.util.Timer to javax.swing.Timer

class HoverButtonExample {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Hover Button Example");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(300, 200);

            JButton button = new JButton("Hover Me") {
                private boolean isHovered = false;
                private int shadowSize = 0;

                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    // Dynamic shadow based on hover state
                    if (isHovered) {
                        g2.setColor(new Color(0, 0, 0, 30));
                        for (int i = 0; i < shadowSize; i++) {
                            g2.fillRoundRect(i, i, getWidth() - i * 2, getHeight() - i * 2, 15, 15);
                        }
                    }

                    // Button background
                    g2.setColor(isHovered ? new Color(52, 152, 219) : new Color(41, 128, 185));
                    g2.fillRoundRect(shadowSize, shadowSize,
                            getWidth() - shadowSize * 2,
                            getHeight() - shadowSize * 2, 15, 15);

                    // Paint the text
                    g2.setColor(Color.WHITE);
                    FontMetrics fm = g2.getFontMetrics();
                    Rectangle textBounds = fm.getStringBounds(this.getText(), g2).getBounds();
                    int textX = (getWidth() - textBounds.width) / 2;
                    int textY = (getHeight() - textBounds.height) / 2 + fm.getAscent();
                    g2.drawString(getText(), textX, textY);

                    g2.dispose();
                }

                {
                    // Initial setup
                    setContentAreaFilled(false);
                    setBorderPainted(false);
                    setFocusPainted(false);
                    setForeground(Color.WHITE);
                    setFont(new Font("Arial", Font.BOLD, 14));

                    addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseEntered(MouseEvent e) {
                            isHovered = true;
                            Timer timer = new Timer(10, new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent evt) {
                                    if (shadowSize < 5) {
                                        shadowSize++;
                                        repaint();
                                    } else {
                                        ((Timer) evt.getSource()).stop();
                                    }
                                }
                            });
                            timer.start();
                        }

                        @Override
                        public void mouseExited(MouseEvent e) {
                            Timer timer = new Timer(10, new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent evt) {
                                    if (shadowSize > 0) {
                                        shadowSize--;
                                        repaint();
                                    } else {
                                        isHovered = false;
                                        ((Timer) evt.getSource()).stop();
                                    }
                                }
                            });
                            timer.start();
                        }
                    });
                }
            };

            button.setPreferredSize(new Dimension(150, 50));
            frame.add(button, BorderLayout.CENTER);
            frame.setVisible(true);
        });
    }
}
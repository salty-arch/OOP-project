package org.database.ui;

import org.database.util.ProgramHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class UserActivityFrame extends JFrame {

    private static final Color PRIMARY_COLOR = new Color(70, 130, 180);
    private static final Color BACKGROUND_COLOR = new Color(240, 245, 249);
    private static final Color CARD_COLOR = Color.WHITE;

    public UserActivityFrame() {
        setTitle("User Activity Log");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        List<String> activityLogs = ProgramHelper.getAllUserActivities();

        if (activityLogs.isEmpty()) {
            JLabel noDataLabel = new JLabel("No activity records found.");
            noDataLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            contentPanel.add(noDataLabel);
        } else {
            for (String activity : activityLogs) {
                contentPanel.add(createActivityCard(activity));
                contentPanel.add(Box.createVerticalStrut(10));
            }
        }

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane);

        setVisible(true);
    }

    private JPanel createActivityCard(String logText) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel label = new JLabel("<html>" + logText.replaceAll("\n", "<br>") + "</html>");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        card.add(label, BorderLayout.CENTER);
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        return card;
    }
}

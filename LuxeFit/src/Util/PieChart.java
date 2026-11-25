package Util;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.sql.ResultSet;

import Model.MySQL;

public class PieChart {

    public static void drawPieChart(JPanel panel, String title, String sql, String keyCol, String valueCol) {
        try {

            DefaultPieDataset dataset = new DefaultPieDataset();
            ResultSet rs = MySQL.executeSearch(sql);

            while (rs.next()) {
                String key = rs.getString(keyCol);
                double value = rs.getDouble(valueCol);
                dataset.setValue(key, value);
            }

            JFreeChart chart = ChartFactory.createPieChart(
                    title,
                    dataset,
                    true,   
                    true,   
                    false   
            );

            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new Dimension(400, 500));
            chartPanel.setMouseWheelEnabled(true);

            panel.setLayout(new BorderLayout());
            panel.removeAll();
            panel.add(chartPanel, BorderLayout.CENTER);
            panel.revalidate();
            panel.repaint();

        } catch (Exception e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(panel, "Failed to load pie chart: " + e.getMessage());
        }
    }
}

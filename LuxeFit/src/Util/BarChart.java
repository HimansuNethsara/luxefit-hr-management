package Util;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import javax.swing.JPanel;
import java.sql.ResultSet;
import Model.MySQL;

public class BarChart {

    public static void drawBarChart(JPanel panel, String title, String xLabel, String yLabel, String sql, String categoryCol, String valueCol) {
    try {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        ResultSet rs = MySQL.executeSearch(sql);

        while (rs.next()) {
            String category = rs.getString(categoryCol);
            double value = rs.getDouble(valueCol);
            dataset.addValue(value, yLabel, category);
        }

        JFreeChart chart = ChartFactory.createBarChart(title, xLabel, yLabel, dataset);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(507, 400));
        chartPanel.setMouseWheelEnabled(true);

        panel.setLayout(new java.awt.BorderLayout());
        panel.removeAll();
        panel.add(chartPanel, java.awt.BorderLayout.CENTER);
        panel.revalidate();
        panel.repaint();

    } catch (Exception e) {
        e.printStackTrace();
        javax.swing.JOptionPane.showMessageDialog(panel, "Failed to load bar chart: " + e.getMessage());
    }
}

}

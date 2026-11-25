package Util;
import java.awt.Color;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class TableRowColorRenderer extends DefaultTableCellRenderer {

    private final int targetColumnIndex;
    private final Map<String, Color> colorMap = new HashMap<>();

    public TableRowColorRenderer(int targetColumnIndex) {
        this.targetColumnIndex = targetColumnIndex;

        colorMap.put("PRESENT", new Color(40, 130, 80));
        colorMap.put("LATE", new Color(180, 120, 0));
        colorMap.put("ON LEAVE", new Color(30, 100, 160));
        colorMap.put("ABSENT", new Color(150, 40, 40));

    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (!isSelected) {
            Object cellValue = table.getValueAt(row, targetColumnIndex);
            if (cellValue != null) {
                String key = cellValue.toString().trim().toUpperCase();
                c.setBackground(colorMap.getOrDefault(key, Color.WHITE));
            } else {
                c.setBackground(Color.WHITE);
            }
        } else {
            c.setBackground(table.getSelectionBackground());
        }

        return c;
    }
}

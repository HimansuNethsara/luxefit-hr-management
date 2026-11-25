package Util;

import java.util.HashMap;
import javax.swing.JTable;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;
import net.sf.jasperreports.view.JasperViewer;

public class ReportGenerator {
    
    public static void generateReport(String reportPath, HashMap<String, Object> params, JTable table) {
        try {
            JRTableModelDataSource dataSource = new JRTableModelDataSource(table.getModel());
            JasperPrint jasperPrint = JasperFillManager.fillReport(reportPath, params, dataSource);
            JasperViewer.viewReport(jasperPrint, false);
        } catch (JRException e) {
            e.printStackTrace();
        }
    }
    
}



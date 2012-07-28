package cu.rst.util;

import java.io.File;
import java.io.FileWriter;
import java.util.Date;

/**
 * 
 */

/**
 * @author parthy chandrasekaran
 *
 */
public class GNUPlotDataDumper
{
    FileWriter m_fw;
    int m_numColumns;
    int m_colCounter;
    
    public GNUPlotDataDumper(int numColumns) throws Exception
    {
        m_fw = new FileWriter(new File("gnuplot_" + (new Date()).getTime()) + ".dat");
        m_numColumns = numColumns;
        m_colCounter = 0;
    }
    
    public void addColumnData(double data) throws Exception
    {
        addColumnData(Double.toString(data));
    }
    
    public void addColumnData(String data) throws Exception
    {
        m_fw.write(data);
        m_colCounter++;
        if(m_colCounter < m_numColumns) m_fw.write(" ");
        if(m_colCounter == m_numColumns)
        {
            m_fw.write(new String("\n"));
            m_colCounter = 0;
        }
        m_fw.flush();
    }
    
    public void flush() throws Exception
    {
        m_fw.flush();
    }
    
    public void flushClose() throws Exception
    {
        m_fw.flush();
        m_fw.close();
    }
    
    public static void main(String[] args) throws Exception
    {
//        GNUPlotDataDumper dumper = new GNUPlotDataDumper(3);
//        dumper.addColumnData(1.0);
//        dumper.addColumnData(234);
//        dumper.addColumnData(211);
//        dumper.addColumnData(1.0);
//        dumper.addColumnData(234);
//        dumper.addColumnData(211);
//        dumper.flushClose();
        
    }
    
    
}

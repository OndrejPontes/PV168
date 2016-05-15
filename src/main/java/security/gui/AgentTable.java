package security.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.table.AbstractTableModel;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import security.logic.entity.Agent;

public class AgentTable extends AbstractTableModel{

    final static Logger log = LoggerFactory.getLogger(AgentTable.class);

    private List<Agent> agents = new ArrayList<>();

    @Override
    public int getRowCount() {
        return agents.size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }
    
    public Agent getAgent(int index) {
        return agents.get(index);
    }

    @Override
    public String getColumnName(int columnIndex) {

        switch (columnIndex) {
            case 0:
                return "name";
            case 1:
                return "rating";
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Agent agent = agents.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return agent.getName();
            case 1:
                return agent.getRating();
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return String.class;
            case 1:
                return int.class;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }
    
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Agent agent = agents.get(rowIndex);
        switch (columnIndex) {
            case 0:
                agent.setName((String) aValue);
                break;
            case 1:
                agent.setRating((int) aValue);
                break;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
        fireTableCellUpdated(rowIndex, columnIndex);
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        switch(columnIndex) {
            case 0:
            case 1:
                return false;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    public void addAgent(Agent input) {
        agents.add(input);
        int lastRow = agents.size() - 1;

        fireTableRowsInserted(lastRow, lastRow);
    }
    
    public void updateAgent(Agent agent, int rowIndex) {
        agents.set(rowIndex, agent);
        fireTableRowsUpdated(rowIndex, rowIndex);
    }
    
    public void deleteAgent(int rowIndex) {
        agents.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }
    
    public void deleteAgents(int[] rowsToDelete) {
        for(int i : rowsToDelete) {
            deleteAgent(i);
        }
    }
    
    public void setAgents(List<Agent> agentsToAdd) {
        agents = agentsToAdd;
        fireTableDataChanged();
    }
}

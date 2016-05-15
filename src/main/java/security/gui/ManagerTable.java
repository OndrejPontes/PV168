package security.gui;

import java.math.BigInteger;

import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import security.logic.entity.Manager;
import security.logic.entity.Mission;
import security.logic.model.AgentModel;
import security.logic.model.MissionModel;

public class ManagerTable extends AbstractTableModel {
    final static Logger log = LoggerFactory.getLogger(ManagerTable.class);
    private static MissionModel missionModel = AppCommons.getMissionModel();
    private static AgentModel agentModel = AppCommons.getAgentModel();

    private List<Manager> managers = new ArrayList<>();

    @Override
    public int getRowCount() {
        return managers.size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }
    
    public Manager getManager(int index) {
        return managers.get(index);
    }

    @Override
    public String getColumnName(int columnIndex) {

        switch (columnIndex) {
            case 0:
                return "agent";
            case 1:
                return "mission";
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Manager manager = managers.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return agentModel.findById(manager.getAgent()).getName();

            case 1:
                return missionModel.findById(manager.getMission()).getName();
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch(columnIndex) {
            case 0:
            case 1:
                return String.class;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
        
    }
    
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Manager manager = managers.get(rowIndex);
        switch(columnIndex) {
            case 0:
                manager.setAgent((Long) aValue);
                break;
            case 1:
                manager.setMission((Long) aValue);
                break;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
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
    
    public void updateManager(Manager manager, int rowIndex) {
        managers.set(rowIndex, manager);
        fireTableRowsUpdated(rowIndex, rowIndex);
    }
    
    public void addManager(Manager manager) {
        managers.add(manager);
        int lastRow = managers.size() - 1;
        fireTableRowsInserted(lastRow, lastRow);
    }
    
    public void deleteManager(int rowIndex) {
        managers.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }
    
    public void deleteManagers(int[] rowsToDelete) {
        for(int i : rowsToDelete) {
            deleteManager(i);
        }
    }
    
    public void setManagers(List<Manager> managersToadd) {
        managers = managersToadd;
        fireTableDataChanged();
    }
}
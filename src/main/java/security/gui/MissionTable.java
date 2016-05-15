package security.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import security.logic.entity.Mission;

public class MissionTable extends AbstractTableModel {
    
    final static Logger log = LoggerFactory.getLogger(MissionTable.class);
    
    private List<Mission> missions = new ArrayList<>();

    @Override
    public int getRowCount() {
        return missions.size();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }
    
    public Mission getMission(int index) {
        return missions.get(index);
    }
    
    @Override
    public String getColumnName(int columnIndex) {

        switch (columnIndex) {
            case 0:
                return "name";
            case 1:
                return "target";
            case 2:
                return "from";
            case 3:
                return "to";
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Mission mission = missions.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return mission.getName();
            case 1:
                return mission.getTarget();
            case 2:
                return mission.getFrom();
            case 3:
                return mission.getTo();
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
            case 2:
            case 3:
                return Date.class;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
        
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Mission mission = missions.get(rowIndex);
        switch(columnIndex) {
            case 0:
                mission.setName((String) aValue);
                break;
            case 1:
                mission.setTarget((String)aValue);
                break;
            case 2:
                mission.setFrom((Date)aValue);
                break;
            case 3:
                mission.setTo((Date)aValue);
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
            case 2:
            case 3:
                return false;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }
    
    public void updateMission(Mission mission, int rowIndex) {
        missions.set(rowIndex, mission);
        fireTableRowsUpdated(rowIndex, rowIndex);
    }
    
    public void addMission(Mission mission) {
        missions.add(mission);
        int lastRow = missions.size() - 1;
        fireTableRowsInserted(lastRow, lastRow);
    }
    
    public void deleteMission(int rowIndex) {
        missions.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }
    
    public void deleteMissions(int[] rowsToDelete) {
        for(int i : rowsToDelete) {
            deleteMission(i);
        }
    }
    
    public void setMissions(List<Mission> missionsToAdd) {
        missions = missionsToAdd;
        fireTableDataChanged();
    }
}

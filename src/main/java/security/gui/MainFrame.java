package security.gui;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import javax.sql.DataSource;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.UnsupportedLookAndFeelException;
import org.apache.commons.dbcp2.BasicDataSource;
import org.jdatepicker.JDatePicker;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import security.db.DBUtils;
import security.db.DataLabelFormater;
import security.db.SpringConfig;
import security.logic.entity.Agent;
import security.logic.entity.Manager;
import security.logic.entity.Mission;
import security.logic.exception.ServiceFailureException;
import security.logic.model.*;
import sun.security.x509.X500Name;

public class MainFrame extends javax.swing.JFrame {

    private final static Logger log = LoggerFactory.getLogger(MainFrame.class);
    private static DataSource ds = AppCommons.getDataSource();
    private static MissionModel missionModel   = AppCommons.getMissionModel();
    private static AgentModel agentModel = AppCommons.getAgentModel();
    private static ManagerModel managerModel = AppCommons.getManagerModel();
    private final MissionTable missionTable;
    private AgentTable agentsTable;
    private ManagerTable managerTable;
    private List<JComboBox> comboBoxofAgnets = new ArrayList<>();
    private List<JComboBox> comboBoxOfMissions = new ArrayList<>();
    private FindAllAgentsWorker findAllAgentsWorker;
    private FindAllMissionsWorker findAllMissionsWorker;
    private FindAllManagersWorker findAllManagersWorker;
    private DefaultComboBoxModel agentsComboBoxModel = new DefaultComboBoxModel();
    private DefaultComboBoxModel missionsComboBoxModel = new DefaultComboBoxModel();
    
    public MissionTable getMissionTable() {
        return missionTable;
    }

    public AgentTable getAgentTable() {
        return agentsTable;
    }

    public ManagerTable getManagerTable() {
        return managerTable;
    }

    public JTable getJTableMission() {
        return JTableMissions;
    }

    public JTable getJTableAgent() {
        return JTableAgents;
    }

    public JTable getJTableManager() {
        return JTableManager;
    }

    public JButton getMissionUpdateButton() {
        return jButton5;
    }
    
    public javax.swing.JButton getAgentUpdateButton() {
        return jButton2;
    }
    
    public javax.swing.JButton getManagerUpdateButton() {
        return jButton8;
    }

    JDatePickerImpl setDatePicker() {
        UtilDateModel model = new UtilDateModel();
        model.setDate(2016, 01, 01);
        Properties p = new Properties();
        p.put("text.today", "Day");
        p.put("text.month", "Month");
        p.put("text.year", "Year");
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DataLabelFormater());
        return datePicker;
    }

    private class FindAllAgentsWorker extends SwingWorker<List<Agent>, Integer> {

        @Override
        protected List<Agent> doInBackground() throws Exception {
            return agentModel.findAll();
    
        }

        @Override
        protected void done() {
            try{
                log.debug("Changing agent model - all agents are loaded from database.");
                agentsTable.setAgents(get());
            }catch(ExecutionException ex) {
                log.error("Exception was thrown in FindAllAgentsWorker in method doInBackGround " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("Method doInBackground has been interrupted in FindAllAgentsWorker " + ex.getCause());
                throw new RuntimeException("Operation interrupted in FindAllAgentsWorker");
            }
        }
    }
    
    private class FindAllMissionsWorker extends SwingWorker<List<Mission>, Integer> {
        
        @Override
        protected List<Mission> doInBackground() throws Exception {
            return missionModel.findAll();
        }

        @Override
        protected void done() {
            try{
                log.debug("Changing mission model - all missions are loaded from database.");
                missionTable.setMissions(get());
            }catch(ExecutionException ex) {
                log.error("Exception was thrown in FindAllMissionsWorker in method doInBackGround " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("Method doInBackground has been interrupted in FindAllMissionsWorker " + ex.getCause());
                throw new RuntimeException("Operation interrupted in FindAllMissionsWorker");
            }
        }
    }
    
    private class FindAllManagersWorker extends SwingWorker<List<Manager>, Integer> {
        
        @Override
        protected List<Manager> doInBackground() throws Exception {
            return managerModel.findAll();
        }

        @Override
        protected void done() {
            try{
                log.debug("Changing manager model - all managers are loaded from database.");
                managerTable.setManagers(get());
            }catch(ExecutionException ex) {
                log.error("Exception was thrown in FindAllManagersWorker in method doInBackGround " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("Method doInBackground has been interrupted in FindAllManagersWorker " + ex.getCause());
                throw new RuntimeException("Operation interrupted in FindAllManagersWorker");
            }
        }
    }
    
   
    
    private class DeleteAgentWorker extends SwingWorker<int[], Void> {

        @Override
        protected int[] doInBackground() {
            int[] selectedRows = JTableAgents.getSelectedRows();
            List<Integer> toDeleteRows = new ArrayList<>();
            if (selectedRows.length >= 0) {
                for (int i = selectedRows.length-1; i >= 0; i--) {
                    Agent agent = agentsTable.getAgent(selectedRows[i]);
                    try {
                        agentModel.delete(agent.getId());
                        toDeleteRows.add(selectedRows[i]);
                    }catch(Exception x) {
                        throw new ServiceFailureException(agent.toString());
                    }   
                }
                return convert(toDeleteRows);
            }
            return null;
        }

        @Override
        protected void done() {
            try {
                int[] indexes = get();
                log.debug("Deleting agents finished");
                if (indexes != null && indexes.length != 0) {
                    agentsTable.deleteAgents(indexes);
                }
            }catch (ExecutionException ex) {
                JOptionPane.showMessageDialog(rootPane, "Cannot delete agent" + ": " + ex.getCause().getMessage(), null, JOptionPane.INFORMATION_MESSAGE);
                log.error("Exception thrown in doInBackground of DeleteAgentWorker: " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("doInBackground of DeleteAgentWorker interrupted: " + ex.getCause());
                throw new RuntimeException("Operation interrupted.. DeleteAgentWorker");
            }
        }
    }
    
    private class DeleteMissionWorker extends SwingWorker<int[], Void> {

        @Override
        protected int[] doInBackground() {
            int[] selectedRows = JTableMissions.getSelectedRows();
            List<Integer> toDeleteRows = new ArrayList<>();
            if (selectedRows.length >= 0) {
                for (int i = selectedRows.length-1; i >= 0; i--) {
                    Mission mission = missionTable.getMission(selectedRows[i]);
                    try {
                        missionModel.delete(mission.getId());
                        toDeleteRows.add(selectedRows[i]);
                    }catch (Exception ex) {
                        throw new ServiceFailureException(mission.toString());
                    }
                }
                return convert(toDeleteRows);
            }
            return null;
        }

        @Override
        protected void done() {
            try {
                int[] indexes = get();
                log.debug("Deleting missions finished");
                if (indexes != null && indexes.length != 0) {
                    missionTable.deleteMissions(indexes);
                }
            } catch (ExecutionException ex) {
                JOptionPane.showMessageDialog(rootPane, "Cannot delete mission" + ": " + ex.getCause().getMessage(), null, JOptionPane.INFORMATION_MESSAGE);
                log.error("Exception thrown in doInBackground of DeleteMissionWorker: " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("doInBackground of DeleteMissionWorker interrupted: " + ex.getCause());
                throw new RuntimeException("Operation interrupted.. DeleteMissionWorker");
            }
        }
    }
    
    private class DeleteManagerWorker extends SwingWorker<int[], Void> {

        @Override
        protected int[] doInBackground() {
            int[] selectedRows = JTableManager.getSelectedRows();
            List<Integer> toDeleteRows = new ArrayList<>();
            if (selectedRows.length >= 0) {
                for (int i = selectedRows.length-1; i >= 0; i--) {
                    Manager manager = managerTable.getManager(selectedRows[i]);
                    try {
                        managerModel.delete(manager.getId());
                        toDeleteRows.add(selectedRows[i]);
                    }catch (Exception ex) {
                        log.error("Cannot delete manager." + ex);
                    }
                }
                return convert(toDeleteRows);
            }
            return null;
        }

        @Override
        protected void done() {
            try {
                int[] indexes = get();
                log.debug("Deleting managers finished");
                if (indexes != null && indexes.length != 0) {
                    managerTable.deleteManagers(indexes);
                }
            } catch (ExecutionException ex) {
                log.error("Exception thrown in doInBackground of DeleteManagerWorker: " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("doInBackground of DeleteManagerWorker interrupted: " + ex.getCause());
                throw new RuntimeException("Operation interrupted.. DeleteManagerWorker");
            }
        }
    }
    
    private void createDB() {
        try {
            DBUtils.executeSqlScript(ds, SpringConfig.class.getResourceAsStream("/createTables.sql"));
        } catch (SQLException ex) {
            log.error("DB creation problem " + ex.getCause());
            JOptionPane.showMessageDialog(null, "Database error");
            System.exit(1);
        }
    }
    private void insertIntoDB() {
        try {
            DBUtils.executeSqlScript(ds, SpringConfig.class.getResourceAsStream("/insertValues.sql"));
        } catch (SQLException ex) {
            log.error("DB insert values problem " + ex.getCause());
            deleteDB();
            JOptionPane.showMessageDialog(null, "Database error");
            System.exit(1);
        }
    }
    private static void deleteDB() {
        try {
            DBUtils.executeSqlScript(ds, SpringConfig.class.getResourceAsStream("/dropTables.sql"));
        } catch (SQLException ex) {
            log.error("DB drop problem " + ex.getCause());
        }
    }
   
    private int[] convert(List<Integer> o) {
        int[] result = new int[o.size()];
        for (int i = 0; i < o.size(); i++) {
            result[i] = o.get(i);
        }
        return result;
    }
    
    public MainFrame() {
        initComponents();
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        createDB();
        insertIntoDB();
        
        missionTable = (MissionTable)JTableMissions.getModel();
        findAllAgentsWorker = new FindAllAgentsWorker();
        findAllAgentsWorker.execute();
        
        agentsTable = (AgentTable)JTableAgents.getModel();
        findAllMissionsWorker = new FindAllMissionsWorker();
        findAllMissionsWorker.execute();
        
        managerTable = (ManagerTable)JTableManager.getModel();
        findAllManagersWorker = new FindAllManagersWorker();
        findAllManagersWorker.execute();
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPasswordField1 = new javax.swing.JPasswordField();
        rootPanel = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        JTableAgents = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        JTableMissions = new javax.swing.JTable();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        JTableManager = new javax.swing.JTable();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();

        jPasswordField1.setText("jPasswordField1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("texts"); // NOI18N
        setTitle(bundle.getString("title")); // NOI18N
        setMinimumSize(new java.awt.Dimension(960, 530));

        rootPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                rootPanelMouseReleased(evt);
            }
        });

        JTableAgents.setModel(new AgentTable());
        jScrollPane1.setViewportView(JTableAgents);

        jButton1.setText("Create");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Update");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Delete");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(100, 100, 100)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 135, Short.MAX_VALUE)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 36, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jButton1)
                    .addComponent(jButton3))
                .addGap(32, 32, 32))
        );

        rootPanel.addTab("Agents", jPanel3);

        JTableMissions.setModel(new MissionTable());
        jScrollPane2.setViewportView(JTableMissions);

        jButton4.setText("Create");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setText("Update");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setText("Delete");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(100, 100, 100)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 135, Short.MAX_VALUE)
                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 36, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton5)
                    .addComponent(jButton4)
                    .addComponent(jButton6))
                .addGap(32, 32, 32))
        );

        rootPanel.addTab("Missions", jPanel4);

        JTableManager.setModel(new ManagerTable());
        jScrollPane3.setViewportView(JTableManager);

        jButton7.setText("Create");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setText("Update");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton9.setText("Delete");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(100, 100, 100)
                .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 135, Short.MAX_VALUE)
                .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 36, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton8)
                    .addComponent(jButton7)
                    .addComponent(jButton9))
                .addGap(32, 32, 32))
        );

        rootPanel.addTab("Manager", jPanel5);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(rootPanel)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(rootPanel)
        );

        setSize(new java.awt.Dimension(702, 405));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ManagerCreateForm(MainFrame.this, null, -1, "Create").setVisible(true);
            }
        });
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MissionCreateForm(MainFrame.this, null, -1, "Create").setVisible(true);
            }
        });
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AgentCreateForm(MainFrame.this, null, -1, "Create").setVisible(true);
            }
        });
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                int selectedRow = JTableManager.getSelectedRow();
                new ManagerCreateForm(MainFrame.this, managerModel.findById(managerTable.getManager(selectedRow).getId()), selectedRow, "Update").setVisible(true);
            }
        });
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        DeleteAgentWorker w = new DeleteAgentWorker();
        w.execute();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void rootPanelMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rootPanelMouseReleased

    }//GEN-LAST:event_rootPanelMouseReleased

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                int selectedRow = JTableAgents.getSelectedRow();
                new AgentCreateForm(MainFrame.this, agentModel.findById(agentsTable.getAgent(selectedRow).getId()), selectedRow, "Update").setVisible(true);
            }
        });
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                int selectedRow = JTableMissions.getSelectedRow();
                new MissionCreateForm(MainFrame.this, missionModel.findById(missionTable.getMission(selectedRow).getId()), selectedRow, "Update").setVisible(true);
            }
        });
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        DeleteMissionWorker w = new DeleteMissionWorker();
        w.execute();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        DeleteManagerWorker w = new DeleteManagerWorker();
        w.execute();
    }//GEN-LAST:event_jButton9ActionPerformed
   
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        deleteDB();
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
        
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable JTableAgents;
    private javax.swing.JTable JTableManager;
    private javax.swing.JTable JTableMissions;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPasswordField jPasswordField1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane rootPanel;
    // End of variables declaration//GEN-END:variables
}

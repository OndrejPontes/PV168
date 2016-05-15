package security.gui;

import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import security.logic.entity.Agent;
import security.logic.entity.Manager;
import security.logic.entity.Mission;
import security.logic.model.AgentModel;
import security.logic.model.ManagerModel;
import security.logic.model.MissionModel;

/**
 *
 * @author opontes
 */
public class ManagerCreateForm extends javax.swing.JFrame {

    private final static Logger log = LoggerFactory.getLogger(MainFrame.class);
    private static ManagerModel managerModel = AppCommons.getManagerModel();
    private MainFrame context;
    private ManagerTable managerTable;
    private String action;
    private Manager manager;
    private int rowIndex;
    private DefaultComboBoxModel agentsComboBoxModel = new DefaultComboBoxModel();
    private DefaultComboBoxModel missionsComboBoxModel = new DefaultComboBoxModel();
    private static MissionModel missionModel = AppCommons.getMissionModel();
    private static AgentModel agentModel = AppCommons.getAgentModel();
    private AgentsComboWorker agentsComboWorker;
    private MissionsComboWorker missionsComboWorker;
    
    public ManagerCreateForm(MainFrame context, Manager manager, int rowIndex, String action) {
        initComponents();
        setLocationRelativeTo(null);
        this.action = action;
        this.manager = manager;
        this.rowIndex = rowIndex;
        this.context = context;
        managerTable = context.getManagerTable();

        jButton1.setText(action);
        jComboBox1.setModel(getAgentsComboBoxModel());
        agentsComboWorker = new AgentsComboWorker();
        agentsComboWorker.execute();
        jComboBox2.setModel(getMissionsComboBoxModel());
        missionsComboWorker = new MissionsComboWorker();
        missionsComboWorker.execute();

        if (manager != null) {
            jComboBox1.setSelectedItem(manager.getAgent());
            jComboBox2.setSelectedItem(manager.getMission());
        }

        this.setVisible(true);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    
    public class AgentsComboWorker extends SwingWorker<List<Agent>, Integer> {

        @Override
        protected List<Agent> doInBackground() throws Exception {
            return agentModel.findAll();
        }

        @Override
        protected void done() {
            try {
                List<Agent> agents = get();
                agentsComboBoxModel.removeAllElements();
                for (Agent agent : agents) {
                    agentsComboBoxModel.addElement(agent);
                }
            } catch (ExecutionException ex) {
                log.error("Exception thrown in doInBackground of AgentsComboWorker: " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("doInBackground of AgentsComboWorker interrupted: " + ex.getCause());
                throw new RuntimeException("Operation interrupted.. AgentsComboWorker");
            }
        }
    }
    
    public DefaultComboBoxModel getMissionsComboBoxModel() {
        return missionsComboBoxModel;
    }

    public DefaultComboBoxModel getAgentsComboBoxModel() {
        return agentsComboBoxModel;
    }

    public class MissionsComboWorker extends SwingWorker<List<Mission>, Integer> {

        @Override
        protected List<Mission> doInBackground() throws Exception {
            return missionModel.findAll();
        }

        @Override
        protected void done() {
            try {
                List<Mission> missions = get();
                missionsComboBoxModel.removeAllElements();
                for (Mission mission : missions) {
                    missionsComboBoxModel.addElement(mission);
                }
            } catch (ExecutionException ex) {
                log.error("Exception thrown in doInBackground of MissionsComboWorker: " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("doInBackground of MissionsComboWorker interrupted: " + ex.getCause());
                throw new RuntimeException("Operation interrupted.. MissionsComboWorker");
            }
        }
    }
    
    
    private class CreateManagerWorker extends SwingWorker<Manager, Integer> {

        @Override
        protected Manager doInBackground() throws Exception {
            log.debug("Creating new manager in doInBackground " + manager);
            Manager manager = getManagerFromForm();
            if (manager == null) {
                log.error("Wrong data");
                throw new IllegalArgumentException("Wrong data");
            }

            managerModel.create(manager);
 
            return manager;
        }

        @Override
        protected void done() {
            try {
                Manager manager = get();
                managerTable.addManager(manager);
                log.info("Manager " + manager + " has been created");
                dispose();
            } catch (IllegalArgumentException ex) {
                warningMessageBox(ex.getMessage());
                return;
            }catch (ExecutionException ex) {
                log.error("Exception thrown in doInBackground of ManagerWorker: " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("doInBackground of ManagerWorker interrupted: " + ex.getCause());
                throw new RuntimeException("Operation interrupted in creating new manager");
            }
        }
        
    }
    
    private class UpdateManagerWorker extends SwingWorker<Manager, Integer> {

        @Override
        protected Manager doInBackground() throws Exception {
        
            log.debug("Updating titleDeed in doInBackground " + manager);
            Manager manager = getManagerFromForm();
            if (manager == null) {
                log.error("Wrong data");
                throw new IllegalArgumentException("Wrong data");
            }
            managerModel.update(manager);
            return manager;
        }

        @Override
        protected void done() {
            try {
                Manager manager = get();
                managerTable.updateManager(manager, rowIndex);
                log.info("Manager " + manager + " has been updated");
                context.getJTableManager().getSelectionModel().clearSelection();
                context.getManagerUpdateButton().setEnabled(false);
                ManagerCreateForm.this.dispose();
            } catch (IllegalArgumentException ex) {
                warningMessageBox(ex.getMessage());
                return;
            }catch (ExecutionException ex) {
                log.error("Exception thrown in doInBackground of ManagerDeedWorker: " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("doInBackground of UpdateManagerWorker interrupted: " + ex.getCause());
                throw new RuntimeException("Operation interrupted in updating new manager");
            }
        }
        
    }
    
    private Manager getManagerFromForm(){
        Agent agent = (Agent)jComboBox1.getSelectedItem();
        Mission mission = (Mission)jComboBox2.getSelectedItem();
        
        
        if (manager == null) {
            manager = new Manager();
        }
        
        manager.setAgent(agent.getId());
        manager.setMission(mission.getId());
        
        return manager;
    }
    
    private void warningMessageBox(String message) {
        log.debug("Showed warning message box with message " + message);
        JOptionPane.showMessageDialog(rootPane, message,null,JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jComboBox2 = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Manager");
        setMinimumSize(new java.awt.Dimension(500, 200));
        setResizable(false);

        jLabel1.setText("Mission");

        jLabel2.setText("Agent");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jButton1.setText("Create");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1))
                        .addGap(38, 38, 38)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jComboBox2, 0, 226, Short.MAX_VALUE)
                            .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(52, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addContainerGap())
        );

        setSize(new java.awt.Dimension(410, 226));
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if (action.equals("Create")) {
            CreateManagerWorker worker = new ManagerCreateForm.CreateManagerWorker();
            worker.execute();
        }else if(action.equals("Update")) {
            UpdateManagerWorker worker = new ManagerCreateForm.UpdateManagerWorker();
            worker.execute();
        }
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    // End of variables declaration//GEN-END:variables
}

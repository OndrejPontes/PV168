package security.gui;

import java.time.DateTimeException;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import org.jdatepicker.impl.JDatePickerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import security.logic.entity.Mission;
import security.logic.model.MissionModel;

/**
 *
 * @author opontes
 */
public class MissionCreateForm extends javax.swing.JFrame {

    private static MissionModel missionModel = AppCommons.getMissionModel();
    private MainFrame context;
    private MissionTable missionTable;
    private Mission mission;
    private String action;
    private int rowIndex;
    private JDatePickerImpl datePickerFrom;
    private JDatePickerImpl datePickerTo;
    private final static Logger log = LoggerFactory.getLogger(MainFrame.class);
    private ResourceBundle rb = ResourceBundle.getBundle("texts");
    
    public MissionCreateForm(MainFrame context, Mission mission, int rowIndex, String action) {
        initComponents();
        setLocationRelativeTo(null);
        this.context = context;
        this.mission = mission;
        this.rowIndex = rowIndex;
        this.action = action;
        this.missionTable = context.getMissionTable();
        jButton1.setText(action);
        
        datePickerFrom = this.context.setDatePicker();
        datePickerFrom.setVisible(true);
        datePickerFrom.setBounds(0, 0, 205, 30);
        datePickerFrom.setLocale(Locale.getDefault());
        jPanel1.add(datePickerFrom);
        
        datePickerTo = this.context.setDatePicker();
        datePickerTo.setVisible(true);
        datePickerTo.setBounds(0, 40, 205, 30);
        datePickerTo.setLocale(Locale.getDefault());
        jPanel1.add(datePickerTo);
        

        if (mission != null){
            jTextField1.setText(mission.getName());
            jTextField2.setText(mission.getTarget());
            Date from = mission.getFrom();
            datePickerFrom.getModel().setDate(from.getYear(), from.getMonth() - 1,from.getDay());
            datePickerFrom.getModel().setSelected(true);
            Date to = mission.getTo();
            datePickerTo.getModel().setDate(to.getYear(), to.getMonth() - 1,to.getDay());
            datePickerTo.getModel().setSelected(true);
        }
        this.setVisible(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    }
    
    private class CreateMissionWorker extends SwingWorker<Mission, Integer> {

        @Override
        protected Mission doInBackground() throws Exception {
            log.debug("Creating new mission in doInBackground " + mission);
            Mission o = getMissionFromForm();
            if (o == null) {
                log.error("Wrong data");
                throw new IllegalArgumentException("Wrong data");
            }
            missionModel.create(o);
            return o;
        }

        @Override
        protected void done() {
            try {
                Mission o = get();
                missionTable.addMission(o);
                log.info("Mission " + o + " has been created");
                dispose();
            } catch (IllegalArgumentException ex) {
                warningMessageBox(ex.getMessage());
                return;
            }catch (ExecutionException ex) {
                log.error("Exception thrown in doInBackground of CreateMisionWorker: " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("doInBackground of CreateMisionWorker interrupted: " + ex.getCause());
                throw new RuntimeException("Operation interrupted in creating new mission");
            }
        }
        
    }
    
    private class UpdateMissionWorker extends SwingWorker<Mission, Integer> {

        @Override
        protected Mission doInBackground() throws Exception {
            log.debug("Creating new mission in doInBackground " + mission);
            Mission o = getMissionFromForm();
            if (o == null) {
                log.error("Wrong data");
                throw new IllegalArgumentException("Wrong data");
            }
            missionModel.update(o);
            return o;
        }

        @Override
        protected void done() {
            try {
                Mission o = get();
                missionTable.updateMission(o, rowIndex);
                log.info("Mission " + o + " has been updated");
                context.getJTableMission().getSelectionModel().clearSelection();
                context.getMissionUpdateButton().setEnabled(false);
                dispose();
            } catch (IllegalArgumentException ex) {
                warningMessageBox(ex.getMessage());
                return;
            }catch (ExecutionException ex) {
                log.error("Exception thrown in doInBackground of UpdateMissionWorker: " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("doInBackground of UpdateMissionWorker interrupted: " + ex.getCause());
                throw new RuntimeException("Operation interrupted in updating new mission");
            }
        }
        
    }
    private Mission getMissionFromForm(){
        
        String name = jTextField1.getText();
        if (name == null || name.length() == 0) {
            warningMessageBox("Fill name");
            return null;
        }
        
        String target = jTextField2.getText();
        if (target == null || target.length() == 0) {
            warningMessageBox("Fill target");
            return null;
        }
        
        Date from;
        Date to;
        try{
            from = (Date)datePickerFrom.getModel().getValue();
            to = (Date)datePickerTo.getModel().getValue();
        }catch(DateTimeException ex) {
            log.debug("An error occured when parsing date in wrong format " + ex.getCause());
            warningMessageBox("Wrong data");
            to = null;
            from = null;
            return null;
        }catch(NullPointerException ex) {
            warningMessageBox("Select date");
            to = null;
            from = null;
            return null;
        }
        if (mission == null) {
            mission = new Mission();
        }
        mission.setName(name);
        mission.setTarget(target);
        mission.setFrom(from);
        mission.setTo(to);
        return mission;
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
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Mission");
        setLocation(new java.awt.Point(0, 0));
        setMinimumSize(new java.awt.Dimension(300, 200));
        setResizable(false);

        jLabel1.setText("Name");

        jLabel2.setText("Target");

        jLabel3.setText("From");

        jLabel4.setText("To");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 84, Short.MAX_VALUE)
        );

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
                    .addComponent(jButton1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING))
                        .addGap(54, 54, 54)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE)
                            .addComponent(jTextField2, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(65, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addComponent(jLabel3)
                        .addGap(33, 33, 33)
                        .addComponent(jLabel4))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap(30, Short.MAX_VALUE))
        );

        setSize(new java.awt.Dimension(410, 330));
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if (action.equals("Create")) {
            CreateMissionWorker worker = new CreateMissionWorker();
            worker.execute();
        }else if(action.equals("Update")) {
            UpdateMissionWorker worker = new UpdateMissionWorker();
            worker.execute();
        }
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables
}

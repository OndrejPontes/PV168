/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package security.gui;

import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import security.logic.entity.Agent;
import security.logic.model.AgentModel;

/**
 *
 * @author opontes
 */
public class AgentCreateForm extends javax.swing.JFrame {

    private static AgentModel agentModel = AppCommons.getAgentModel();
    private MainFrame context;
    private AgentTable agentTable;
    private Agent agent;
    private String text;
    private int rowIndex;
    private final static Logger log = LoggerFactory.getLogger(MainFrame.class);
    private ResourceBundle rb = ResourceBundle.getBundle("texts");
    
    
    public AgentCreateForm(MainFrame context, Agent agent, int rowIndex, String text) {
        initComponents();
        setLocationRelativeTo(null);
        this.context = context;
        this.agent = agent;
        this.rowIndex = rowIndex;
        this.text = text;
        this.agentTable = context.getAgentTable();
        jButton1.setText(text);
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        if (agent != null){
            jTextField1.setText(agent.getName());
            jTextField2.setText(Integer.toString(agent.getRating()));
        }
        this.setVisible(true);
    }
    
    private class CreateAgentWorker extends SwingWorker<Agent, Integer> {

        @Override
        protected Agent doInBackground() throws Exception {
            log.debug("Creating new agent in doInBackground " + agent);
            Agent o = getAgentFromForm();
            if (o == null) {
                log.error(rb.getString("wrong-enter-data"));
                throw new IllegalArgumentException("wrong-enter-data");
            }
            agentModel.create(o);
            return o;
        }

        @Override
        protected void done() {
            try {
                Agent o = get();
                agentTable.addAgent(o);
                log.info("Agent " + o + " has been created");
                dispose();
            } catch (IllegalArgumentException ex) {
                warningMessageBox(ex.getMessage());
                return;
            }catch (ExecutionException ex) {
                log.error("Exception thrown in doInBackground of AgentWorker: " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("doInBackground of AgentWorker interrupted: " + ex.getCause());
                throw new RuntimeException("Operation interrupted in creating new agent");
            }
        }
        
    }
    
    private class UpdateAgentWorker extends SwingWorker<Agent, Integer> {

        @Override
        protected Agent doInBackground() throws Exception {
            log.debug("Creating new agent in doInBackground " + agent);
            Agent o = getAgentFromForm();
            if (o == null) {
                log.error("Wrong Data");
                throw new IllegalArgumentException("Wrong Data");
            }
            agentModel.update(o);
            return o;
        }

        @Override
        protected void done() {
            try {
                Agent o = get();
                agentTable.updateAgent(o, rowIndex);
                log.info("Agent " + o + " has been updated");
                context.getJTableAgent().getSelectionModel().clearSelection();
                context.getAgentUpdateButton().setEnabled(false);
                dispose();
            } catch (IllegalArgumentException ex) {
                warningMessageBox(ex.getMessage());
                return;
            }catch (ExecutionException ex) {
                log.error("Exception thrown in doInBackground of UpdateAgentWorker: " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("doInBackground of UpdateAgentWorker interrupted: " + ex.getCause());
                throw new RuntimeException("Operation interrupted in updating new agent");
            }
        }
        
    }
    
    private Agent getAgentFromForm(){
        
        String name = jTextField1.getText();
        if (name == null || name.length() == 0) {
            warningMessageBox("Fill name");
            return null;
        }
        
        int rating = Integer.parseInt(jTextField2.getText());
        if (rating < 0 || rating > 10) {
            warningMessageBox("Pick rating from 1 to 10");
            return null;
        }
        
        if (agent == null) {
            agent = new Agent();
        }
        agent.setName(name);
        agent.setRating(rating);
        return agent;
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
        jButton1 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Agent");

        jLabel1.setText("Name");

        jLabel2.setText("Rating");

        jButton1.setText("Create");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1))
                        .addGap(70, 70, 70)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField1)
                            .addComponent(jTextField2, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1)))
                .addGap(66, 66, 66))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(23, 23, 23))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if (text.equals("Create")) {
            CreateAgentWorker worker = new CreateAgentWorker();
            worker.execute();
        }else if(text.equals("Update")) {
            UpdateAgentWorker worker = new UpdateAgentWorker();
            worker.execute();
        }
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables
}

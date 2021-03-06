/*
 * Copyright 2005-2006, Dave Johnson
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.manning.blogapps.chapter10.blogclientui;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;

/**
 * Entry point for blog client UI, calls BlogClientFrame on successful login.
 * @author  Dave Johnson
 */
public class LoginFrame extends javax.swing.JFrame {

	private String mTitle = "BlogClient Login";
    private String mUsername = "";            
    private String mPassword = "";         
    private String mServerURL = "";
    private String mType = "Atom";
            
    public LoginFrame() {
        try {
            Properties config = new Properties();
            String homedir = System.getProperty("user.home");
            config.load(new FileInputStream(
                    homedir + File.separator+"blogclient.properties"));        
            mUsername = config.getProperty("username");
            mServerURL = config.getProperty("url");
            mType = config.getProperty("type");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        initComponents();
        setTitle(mTitle);
        mUsernameField.setText(mUsername);
        mServerURLField.setText(mServerURL);
        mTypeCombo.setModel(new DefaultComboBoxModel(new String[] {"Atom","MetaWeblog"}));
        mTypeCombo.setSelectedItem(mType);
        
        Dimension ssize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(
           (ssize.width/2) - (this.getSize().width/2),
           (ssize.height/2) - (this.getSize().height/2) );
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        mUsernameField = new javax.swing.JTextField();
        mUsernameLabel = new javax.swing.JLabel();
        mPasswordLabel = new javax.swing.JLabel();
        mServerLabel = new javax.swing.JLabel();
        mServerURLField = new javax.swing.JTextField();
        mLoginButton = new javax.swing.JButton();
        mCancelButton = new javax.swing.JButton();
        mTypeCombo = new javax.swing.JComboBox();
        mTypeLabel = new javax.swing.JLabel();
        mPasswordField = new javax.swing.JPasswordField();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("RSS and Atom in Action - Blog Client");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 362;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(20, 8, 0, 0);
        getContentPane().add(mUsernameField, gridBagConstraints);

        mUsernameLabel.setText("Username");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 28;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(20, 20, 0, 0);
        getContentPane().add(mUsernameLabel, gridBagConstraints);

        mPasswordLabel.setText("Password");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 31;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 20, 0, 0);
        getContentPane().add(mPasswordLabel, gridBagConstraints);

        mServerLabel.setText("Server URL");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 24;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 20, 0, 0);
        getContentPane().add(mServerLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 362;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 10, 0, 42);
        getContentPane().add(mServerURLField, gridBagConstraints);

        mLoginButton.setText("Login");
        mLoginButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mLoginButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(23, 90, 22, 0);
        getContentPane().add(mLoginButton, gridBagConstraints);

        mCancelButton.setText("Cancel");
        mCancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mCancelButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(23, 5, 22, 0);
        getContentPane().add(mCancelButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 84;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 10, 0, 0);
        getContentPane().add(mTypeCombo, gridBagConstraints);

        mTypeLabel.setText("Protocol");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 30, 0, 0);
        getContentPane().add(mTypeLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 362;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 10, 0, 42);
        getContentPane().add(mPasswordField, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void mCancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mCancelButtonActionPerformed
        setVisible(false);
        System.exit(-1);
    }//GEN-LAST:event_mCancelButtonActionPerformed

    private void mLoginButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mLoginButtonActionPerformed
        setVisible(false);
        mUsername = mUsernameField.getText();
        mPassword = mPasswordField.getText();
        mServerURL = mServerURLField.getText();
        mType = (String)mTypeCombo.getSelectedItem();
        try {
            Properties config = new Properties();
            config.setProperty("username", mUsername);
            config.setProperty("url", mServerURL);
            config.setProperty("type", mType);
            String homedir = System.getProperty("user.home");
            FileOutputStream fos = 
               new FileOutputStream(homedir + File.separator+"blogclient.properties");
            config.save(fos, "# RSS and Atom in Action");
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        final JFrame frame = this;
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    BlogClientFrame frame = 
                        newBlogClientFrame(mUsername, mPassword, mServerURL, mType);
                    frame.setVisible(true);
                } catch (Exception e) {
                    frame.setVisible(true);
                    frame.setTitle("LOGIN ERROR - TRY AGAIN");
                }
            }
        });
    }//GEN-LAST:event_mLoginButtonActionPerformed
    
    protected BlogClientFrame newBlogClientFrame(
       String username, String password, String url, String type) {
       return new BlogClientFrame(username, password, url, type); 
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LoginFrame().setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton mCancelButton;
    private javax.swing.JButton mLoginButton;
    private javax.swing.JPasswordField mPasswordField;
    private javax.swing.JLabel mPasswordLabel;
    private javax.swing.JLabel mServerLabel;
    private javax.swing.JTextField mServerURLField;
    private javax.swing.JComboBox mTypeCombo;
    private javax.swing.JLabel mTypeLabel;
    private javax.swing.JTextField mUsernameField;
    private javax.swing.JLabel mUsernameLabel;
    // End of variables declaration//GEN-END:variables
    
}

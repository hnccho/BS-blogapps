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
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.manning.blogapps.chapter10.blogclient.*;
import java.util.Arrays;
import java.util.Iterator;
import javax.swing.DefaultListModel;

/**
 * Form for editing a single blog entry.
 * @author Dave Johnson
 */
public class BlogClientPanel extends javax.swing.JPanel implements BlogClientTab {

	private Blog blogSite = null;
    private BlogClientFrame blogClientFrame = null;
    private BlogEntry entry = null;
    
    /** Creates new form BlogClientPanel */
    public BlogClientPanel() {
        initComponents();
    }
 
    /** Inject BlogSite dependency.*/
    public void setBlog(Blog blogSite) {
        this.blogSite = blogSite;
        try {
            List categories = blogSite.getCategories();
            if (categories != null) setCategories(categories);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }    
   
    /** Inject BlogClientFrame dependency */
    public void setBlogClientFrame(BlogClientFrame frame) {
        this.blogClientFrame = frame;
    }
    
    public void setCategories(List cats) {
        DefaultListModel model = new DefaultListModel();
        for (Iterator iter=cats.iterator(); iter.hasNext();) {
            model.addElement(iter.next());
        }
        mCategoryList.setModel(model);
    }

    /** Load blog entry into form */
    public void loadEntry(String id) {
        try {
            entry = blogSite.getEntry(id);
            List allCats = blogSite.getCategories();
            List entryCats = entry.getCategories();
            if (allCats != null && entryCats != null) {
                setCategories(allCats);
                for (Iterator iter=entryCats.iterator(); iter.hasNext();) {
                    BlogEntry.Category cat = (BlogEntry.Category)iter.next();
                    mCategoryList.setSelectedValue(cat, true);
                }
            } else if (entryCats != null) {
                setCategories(entryCats);
                for (Iterator iter=entryCats.iterator(); iter.hasNext();) {
                    BlogEntry.Category cat = (BlogEntry.Category)iter.next();
                    mCategoryList.setSelectedValue(cat, true);
                }
            }
            if (entry.getTitle() != null) {
                mTitleField.setText(entry.getTitle());
            }
            if (entry.getPublicationDate() != null) {
                mPubDateField.setText(entry.getPublicationDate().toString());
            }
            if (entry.getContent() != null) {
                mTextArea.setText(entry.getContent().getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /** Publish entry to blog */
    public void publishButtonPressed() {
        try {
            postEntry(true);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
    
    /** Save entry to blog as draft */
    public void draftButtonPressed() {
        postEntry(false);
    }
    
    /** New button was pressed to start new entry */
    public void newButtonPressed() {
        reset();
        blogClientFrame.reset();
    }
    
    /** Delete current entry */
    public void deleteButtonPressed() {
        if (entry != null) {
            try {
               entry.delete();
               blogClientFrame.reset();
               reset();
            }
            catch (Exception e) {
               e.printStackTrace();
               JOptionPane.showMessageDialog(blogClientFrame, "ERROR deleting entry");
            }
        }
    }
    
    /** Upload image and add <img> tag to blog entry */
    public void uploadButtonPressed() {
        JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showOpenDialog(blogClientFrame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String fileName = chooser.getSelectedFile().getName();
            try {
                int lastDot = fileName.lastIndexOf(".");
                String ext = fileName.substring(lastDot+1);
                BlogResource res = blogSite.newResource(
                    fileName, "image/"+ext, chooser.getSelectedFile());
                res.save();
                mTextArea.setText(mTextArea.getText() 
                    + "<img src=\"" + res.getContent().getSrc() + "\" />");
            }
            catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(blogClientFrame, "ERROR uploading image");
            }
        }        
    }
    
    /** New button was pressed to start new entry */
    public void reset() {
        entry = null;
        mTitleField.setText("");
        mCategoryList.clearSelection();
        mPubDateField.setText("");
        mUpdateDateField.setText("");
        mTextArea.setText("");
    }
    
    /** Called by tabbed container */
    public void onSelected() {
        // nothing to do
    }

    /** Post entry to blog */
    public void postEntry(boolean publish) {
        try {
            boolean hasContent = mTextArea.getText() != null 
                && mTextArea.getText().trim().length() > 0;
            boolean hasTitle = mTitleField.getText() != null 
                && mTitleField.getText().trim().length() > 0;
            if (hasTitle || hasContent) {
                boolean newEntry = false;
                if (entry == null) {
                    entry = blogSite.newEntry();
                    newEntry = true;
                }
                BlogEntry.Content content = new BlogEntry.Content(mTextArea.getText());
                content.setType("text/html");
                entry.setContent(content);
                
                entry.setTitle(mTitleField.getText());
                if (mCategoryList.getSelectedValues().length > 0) {
                    entry.setCategories(
                        Arrays.asList(mCategoryList.getSelectedValues()));
                }
                entry.setDraft(!publish);
                entry.save();
                JOptionPane.showMessageDialog(blogClientFrame, "Entry saved");
                reset();
            }
            else {
                JOptionPane.showMessageDialog(
                    blogClientFrame, "Must have either title or content");
            }
        } 
        catch (Exception e) {
            // TODO: indicate error to user
            e.printStackTrace();
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        mTitleField = new javax.swing.JTextField();
        mTitleLabel = new javax.swing.JLabel();
        mCategoryLabel = new javax.swing.JLabel();
        mScrollPane = new javax.swing.JScrollPane();
        mTextArea = new javax.swing.JTextArea();
        mPublishButton = new javax.swing.JButton();
        mNewButton = new javax.swing.JButton();
        mDraftButton = new javax.swing.JButton();
        mPubDateLabel = new javax.swing.JLabel();
        mUpdateDateLabel = new javax.swing.JLabel();
        mPubDateField = new javax.swing.JLabel();
        mUpdateDateField = new javax.swing.JLabel();
        mDeleteButton = new javax.swing.JButton();
        mUploadImage = new javax.swing.JButton();
        mCategoryListScroll = new javax.swing.JScrollPane();
        mCategoryList = new javax.swing.JList();

        setLayout(new java.awt.GridBagLayout());

        setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 382;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(mTitleField, gridBagConstraints);

        mTitleLabel.setText("Title");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 12);
        add(mTitleLabel, gridBagConstraints);

        mCategoryLabel.setText("Category");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 12);
        add(mCategoryLabel, gridBagConstraints);

        mTextArea.setLineWrap(true);
        mScrollPane.setViewportView(mTextArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 469;
        gridBagConstraints.ipady = 159;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(mScrollPane, gridBagConstraints);

        mPublishButton.setText("Publish");
        mPublishButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mPublishButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(mPublishButton, gridBagConstraints);

        mNewButton.setText("New");
        mNewButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mNewButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(mNewButton, gridBagConstraints);

        mDraftButton.setText("Save as Draft");
        mDraftButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mDraftButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(mDraftButton, gridBagConstraints);

        mPubDateLabel.setText("Pub Date");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 12);
        add(mPubDateLabel, gridBagConstraints);

        mUpdateDateLabel.setText("Update Date");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 12);
        add(mUpdateDateLabel, gridBagConstraints);

        mPubDateField.setFont(new java.awt.Font("Lucida Grande", 2, 12));
        mPubDateField.setText("None");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(mPubDateField, gridBagConstraints);

        mUpdateDateField.setFont(new java.awt.Font("Lucida Grande", 2, 13));
        mUpdateDateField.setText("None");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(mUpdateDateField, gridBagConstraints);

        mDeleteButton.setText("Delete");
        mDeleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mDeleteButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(mDeleteButton, gridBagConstraints);

        mUploadImage.setText("Upload Image...");
        mUploadImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mUploadImageActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(mUploadImage, gridBagConstraints);

        mCategoryList.setMinimumSize(new java.awt.Dimension(20, 20));
        mCategoryListScroll.setViewportView(mCategoryList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(mCategoryListScroll, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents

    private void mDeleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mDeleteButtonActionPerformed
        deleteButtonPressed();
    }//GEN-LAST:event_mDeleteButtonActionPerformed

    private void mUploadImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mUploadImageActionPerformed
        uploadButtonPressed();
    }//GEN-LAST:event_mUploadImageActionPerformed

    private void mNewButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mNewButtonActionPerformed
        newButtonPressed();
    }//GEN-LAST:event_mNewButtonActionPerformed

    private void mDraftButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mDraftButtonActionPerformed
        draftButtonPressed();
    }//GEN-LAST:event_mDraftButtonActionPerformed

    private void mPublishButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mPublishButtonActionPerformed
        publishButtonPressed();
    }//GEN-LAST:event_mPublishButtonActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel mCategoryLabel;
    private javax.swing.JList mCategoryList;
    private javax.swing.JScrollPane mCategoryListScroll;
    private javax.swing.JButton mDeleteButton;
    private javax.swing.JButton mDraftButton;
    private javax.swing.JButton mNewButton;
    private javax.swing.JLabel mPubDateField;
    private javax.swing.JLabel mPubDateLabel;
    private javax.swing.JButton mPublishButton;
    private javax.swing.JScrollPane mScrollPane;
    private javax.swing.JTextArea mTextArea;
    private javax.swing.JTextField mTitleField;
    private javax.swing.JLabel mTitleLabel;
    private javax.swing.JLabel mUpdateDateField;
    private javax.swing.JLabel mUpdateDateLabel;
    private javax.swing.JButton mUploadImage;
    // End of variables declaration//GEN-END:variables
}

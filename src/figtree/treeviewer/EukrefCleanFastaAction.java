/*
 * TreePaneListener.java
 *
 * Copyright (C) 2006-2014 Andrew Rambaut
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package figtree.treeviewer;

import figtree.treeviewer.*;
import figtree.application.FigTreeFrame;

import java.util.*;
import java.awt.event.*;
import jam.toolbar.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.io.File;
import java.io.FileReader;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import jebl.evolution.graphs.Node;
import jebl.evolution.taxa.Taxon;
import jebl.evolution.trees.*;

/**
 * @author Serafim Nenarokov
 * @version $Id$
 *
 * $HeadURL$
 *
 * $LastChangedBy$
 * $LastChangedDate$
 * $LastChangedRevision$
 */
public class EukrefCleanFastaAction extends EukrefBaseAction {
  protected FigTreeFrame frame;
  protected ExtendedTreeViewer treeViewer;
  protected Integer mode;
  public final static String ATTR = "eukref_remove";
  public final static Integer TO_DROP = 1;
  public final static Integer TO_KEEP = 2;


  public EukrefCleanFastaAction(String label){
    super(label);
  }

  public void initEnvironment(FigTreeFrame frame, ExtendedTreeViewer treeViewer, Integer toDrop){
    this.frame = frame;
    this.treeViewer = treeViewer;

    if (toDrop == EukrefCleanFastaAction.TO_DROP) {
      this.mode = EukrefCleanFastaAction.TO_DROP;
    } else {
      this.mode = EukrefCleanFastaAction.TO_KEEP;
    }
  }

	public void actionPerformed(ActionEvent e){
    FileDialog chooser = new FileDialog(frame, "Select source fasta file of the current tree", FileDialog.LOAD);
    chooser.setMultipleMode(false);
    chooser.setVisible(true);
    chooser.dispose();

    if (chooser.getFile() != null){
      String path = chooser.getDirectory() + chooser.getFile();
      HashMap<String, ArrayList<String>> seqs = readFasta(path);

      String error = getTreeErrorMessage(seqs);
      if (error.length() > 0){
        JOptionPane.showMessageDialog(null, error, "Inconsistency found!", JOptionPane.ERROR_MESSAGE);
        return;
      }

      seqs = filterSeqs(seqs);

      FileDialog saver = new FileDialog(frame, "Save new fasta as...", FileDialog.SAVE);
      saver.setVisible(true);
      saver.dispose();

      String outPath = saver.getDirectory() + saver.getFile();
      writeFasta(seqs, outPath);
    }
  }

  private HashMap<String, ArrayList<String>> readFasta(String path) {
    HashMap<String, ArrayList<String>> result = new HashMap<String, ArrayList<String>>();
    boolean first = true;
    String currentHeader = null;
    ArrayList<String> currentSeq = new ArrayList<String>();

    try {
      Scanner sc = new Scanner(new File(path));

      while (sc.hasNextLine()) {
        String line = sc.nextLine().trim();

        if (line.charAt(0) == '>') {
          if (first){
            // save header to current var
            currentHeader = line.substring(1);
            first = false;
          } else {
            // dump current vars to hash
            result.put(currentHeader, currentSeq);
            currentHeader = line.substring(1);
            currentSeq = new ArrayList<String>();
          }
        } else {
          // append current line to currentSeq
          currentSeq.add(line);
        }
      }

      result.put(currentHeader, currentSeq);

    } catch (FileNotFoundException fnfe) {
      System.out.println(fnfe.getMessage());
    }

    return result;
  }

  private HashMap<String, ArrayList<String>> filterSeqs(HashMap<String, ArrayList<String>> src_seqs){
    HashMap<String, ArrayList<String>> result = new HashMap<String, ArrayList<String>>();
    ArrayList<String> toDrop = getSeqsForDropping();


    for (Map.Entry<String, ArrayList<String>> entry : src_seqs.entrySet()) {
      if (!toDrop.contains(entry.getKey())){
        result.put(entry.getKey(), entry.getValue());
      }
    }

    return result;
  }

  private ArrayList<String> getSeqsForDropping(){
    Tree tree = treeViewer.getCurrentTree();
    ArrayList<String> result = new ArrayList<String>();
    Set<Node> nodes = tree.getExternalNodes();

    for (Node node : nodes) {
      if ((isDropping() && attributeIsSet(node)) || (isKeeping() && !attributeIsSet(node))) {
        result.add(tree.getTaxon(node).getName());
      }
    }

    return result;
  }

  private boolean attributeIsSet(Node node) {
    Object toRemoveAttr = node.getAttribute(EukrefMarkTaxaAction.ATTR);
    return toRemoveAttr != null && (toRemoveAttr.toString() == "true");
  }

  private void writeFasta(HashMap<String, ArrayList<String>> seqs, String path) {
    try {
      FileWriter fw = new FileWriter(path);

      for (Map.Entry<String, ArrayList<String>> entry : seqs.entrySet()) {
        fw.write(">" + entry.getKey() + "\n");
        for (String line : entry.getValue()){
          fw.write(line + "\n");
        }
      }

      fw.close();
    } catch (IOException fnfe) {
      System.out.println(fnfe.getMessage());
    }
  }

  private boolean isKeeping() {
    return this.mode == EukrefCleanFastaAction.TO_KEEP;
  }

  private boolean isDropping() {
    return this.mode == EukrefCleanFastaAction.TO_DROP;
  }

  private String getTreeErrorMessage(HashMap<String, ArrayList<String>> file_seqs) {
    String missingInTreeMsg = "";
    String missingInFileMsg = "";

    ArrayList<String> missingInTree = getMissingInTreeSeqs(file_seqs);
    if (missingInTree.size() > 0) {
      missingInTreeMsg = "Sequences found in fasta file, but not found in the tree:\n";
      for (String name : missingInTree) {
        missingInTreeMsg += ">" + name + "\n";
      }
    }

    ArrayList<String> missingInFile = getMissingInFileSeqs(file_seqs);
    if (missingInFile.size() > 0) {
      missingInFileMsg = "Sequences found in the tree, but not found in fasta:\n";
      for (String name : missingInFile) {
        missingInFileMsg += ">" + name + "\n";
      }
    }

    String result = "";

    if (missingInTreeMsg.length() > 0 || missingInFileMsg.length() > 0) {
      result += missingInTreeMsg;
      if (missingInTreeMsg.length() > 0 && missingInFileMsg.length() > 0) {
        result += "\n\n";
      }
      result += missingInFileMsg;
    }

    return result;
  }

  private ArrayList<String> getMissingInTreeSeqs(HashMap<String, ArrayList<String>> file_seqs) {
    ArrayList<String> missing = new ArrayList<String>();
    ArrayList<String> treeSeqs = getTreeNodeNames();

    for (Map.Entry<String, ArrayList<String>> entry : file_seqs.entrySet()) {
      if (!treeSeqs.contains(entry.getKey())) {
        missing.add(entry.getKey());
      }
    }

    return missing;
  }

  private ArrayList<String> getMissingInFileSeqs(HashMap<String, ArrayList<String>> file_seqs) {
    ArrayList<String> missing = new ArrayList<String>();
    ArrayList<String> treeSeqs = getTreeNodeNames();
    ArrayList<String> fileSeqs = new ArrayList<String>();

    for (Map.Entry<String, ArrayList<String>> entry : file_seqs.entrySet()) {
      fileSeqs.add(entry.getKey());
    }

    for (String treeSeq : treeSeqs){
      if (!fileSeqs.contains(treeSeq)) {
        missing.add(treeSeq);
      }
    }

    return missing;
  }

  private ArrayList<String> getTreeNodeNames() {
    ArrayList<String> result = new ArrayList<String>();
    Tree tree = treeViewer.getCurrentTree();

    for (Node node : tree.getExternalNodes()) {
      result.add(tree.getTaxon(node).getName());
    }

    return result;
  }
}

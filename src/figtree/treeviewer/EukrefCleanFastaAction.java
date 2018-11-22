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
  public final static String ATTR = "eukref_remove";

  public EukrefCleanFastaAction(String label, String toolTipText, Icon icon){
    super(label, toolTipText, icon);
  }

  public void initEnvironment(FigTreeFrame frame, ExtendedTreeViewer treeViewer){
    this.frame = frame;
    this.treeViewer = treeViewer;
  }

	public void actionPerformed(ActionEvent e){
    FileDialog chooser = new FileDialog(frame, "Select source fasta file of the current tree", FileDialog.LOAD);
    chooser.setMultipleMode(false);
    chooser.setVisible(true);
    chooser.dispose();

    if (chooser.getFile() != null){
      String path = chooser.getDirectory() + chooser.getFile();
      HashMap<String, ArrayList<String>> seqs = readFasta(path);
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
    ArrayList<String> toRemove = getSeqsForRemoval();


    for (Map.Entry<String, ArrayList<String>> entry : src_seqs.entrySet()) {
      if (!toRemove.contains(entry.getKey())){
        result.put(entry.getKey(), entry.getValue());
      }
    }

    return result;
  }

  private ArrayList<String> getSeqsForRemoval(){
    Tree tree = treeViewer.getCurrentTree();
    ArrayList<String> result = new ArrayList<String>();
    Set<Node> nodes = tree.getExternalNodes();

    for (Node node : nodes) {
      Object toRemoveAttr = node.getAttribute(EukrefRemoveTaxaAction.ATTR);
      if (toRemoveAttr != null && (toRemoveAttr.toString() == "true")) {
        result.add(tree.getTaxon(node).getName());
      }
    }

    return result;
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
}

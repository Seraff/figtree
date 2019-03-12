package figtree.treeviewer;

import figtree.treeviewer.*;

import java.util.*;
import java.awt.event.*;
import jam.toolbar.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import jebl.evolution.graphs.Node;
import jebl.evolution.taxa.Taxon;
import jebl.evolution.trees.*;

import java.io.*;
import java.io.File;
import java.io.FileReader;

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

public class MsPhyloManager {
  public final static String SPECS_GROUP_PATH = "specs_groups.csv";
  public final static String GROUP_COLOR_PATH = "group_color.csv";
  public final static String DELIMITER = ",";
  private static HashMap<String, String> spec_colors = new HashMap<String, String>();
  protected ExtendedTreeViewer treeViewer;

  public MsPhyloManager(ExtendedTreeViewer treeViewer){
    this.treeViewer = treeViewer;
    initFiles();
  }

  private void initFiles(){
    HashMap<String, String> spec_groups = parseFile(SPECS_GROUP_PATH);
    HashMap<String, String> groups_colors = parseFile(GROUP_COLOR_PATH);
    Tree tree = treeViewer.getCurrentTree();

    for (Map.Entry<String, String> entry : spec_groups.entrySet()) {
      this.spec_colors.put(entry.getKey(), groups_colors.get(entry.getValue()));
    }

    // highlight with colors
    for (Node node : tree.getExternalNodes()) {
      String taxa_name = tree.getTaxon(node).getName();
      if (spec_colors.containsKey(taxa_name)){
        String color_hex = spec_colors.get(taxa_name);
        treeViewer.hilightSpecificNode(node, new Color(Integer.parseInt(color_hex, 16)));
      }
    }
  }

  private HashMap<String, String> parseFile(String path){
    HashMap<String, String> result = new HashMap<String, String>();

    try {
      Scanner sc = new Scanner(new File(path));
      while (sc.hasNextLine()) {
        String line = sc.nextLine().trim();
        String[] element = line.split(DELIMITER);
        result.put(element[0], element[1]);
      }
    } catch (FileNotFoundException fnfe) {
      System.out.println(fnfe.getMessage());
    }

    return result;
  }
}

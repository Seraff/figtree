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
public class EukrefAnnotateTaxaAction extends ToolbarAction {
  private ExtendedTreeViewer treeViewer;
  public final static String NAME_ATTR = "clade_name";
  public final static String TAXONOMY_ATTR = "taxonomy";

  public EukrefAnnotateTaxaAction(String label, String toolTipText, Icon icon){
    super(label, toolTipText, icon);
  }

	public void actionPerformed(ActionEvent e){
    if (treeViewer.hasSelection()){
      RootedTree tree = (RootedTree)treeViewer.getCurrentTree();

      for (Node node : treeViewer.getSelectedTips()){
        ArrayList<Node> taxonomy = new ArrayList<Node>();

        // Gathering ancestors
        taxonomy.add(node);
        Node currentNode = node;
        while (!tree.isRoot(currentNode)){
          Node parent = tree.getParent(currentNode);
          currentNode = parent;
          taxonomy.add(parent);
        }

        Collections.reverse(taxonomy);
        String label = buildTaxonomy(taxonomy);

        Taxon taxon = tree.getTaxon(node);
        if (!taxon.getName().isEmpty() && !label.isEmpty()) {
          label += ";";
          label += taxon.getName();
          node.setAttribute(TAXONOMY_ATTR, label);
        }
      }

      treeViewer.fireAnnotationsChanged();
    }
  }

  public void initTreeViewer(ExtendedTreeViewer treeViewer){
    this.treeViewer = treeViewer;
  }

  private String buildTaxonomy(ArrayList<Node> nodes){
    String label = "";

    for (Node node : nodes) {
      Object attr = node.getAttribute(NAME_ATTR);

      if (atrrExists(attr)) {
        if (label.length() > 0) {
          label += ";";
        }

        label += attr.toString();
      }
    }

    return label;
  }

  private boolean atrrExists(Object attr){
    return attr != null && !attr.toString().isEmpty();
  }
}

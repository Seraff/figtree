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
public class EukrefMarkTaxaAction extends EukrefBaseAction {
  public final static String ATTR = "eukref_mark";

  public EukrefMarkTaxaAction(String label){
    super(label);
  }

  public void initTreeViewer(ExtendedTreeViewer treeViewer){
    this.treeViewer = treeViewer;
  }

  public void actionPerformed(ActionEvent e){
    Set<Node> nodes = treeViewer.getSelectedTips();

    if (nodes.size() > 0){
      for (Node node : nodes) {
        Object value = node.getAttribute(ATTR);

        if (value != null && value.toString() == "true"){
          node.removeAttribute(ATTR);
        } else {
          node.setAttribute(ATTR, true);
        }
      }

      treeViewer.fireAnnotationsChanged();
    }
  }

  public void unmarkAll() {
    Tree tree = treeViewer.getCurrentTree();
    Set<Node> nodes = tree.getExternalNodes();

    for (Node node : nodes) {
      Object value = node.getAttribute(ATTR);

      if (value != null && value.toString() == "true"){
        node.removeAttribute(ATTR);
      }
    }

    treeViewer.fireAnnotationsChanged();
  }
}

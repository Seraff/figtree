
package figtree.treeviewer;

import jam.panels.OptionsPanel;
import jam.util.IconUtils;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.Component;
import java.awt.event.*;
import java.awt.Dimension;
import java.awt.Color;
import java.util.*;
import org.fife.ui.autocomplete.*;
import java.io.*;
import java.net.*;
import jebl.util.Attributable;

import figtree.treeviewer.*;
import figtree.ui.components.WholeNumberField;
import figtree.ui.components.RealNumberField;
import figtree.treeviewer.ExtendedTreeViewer;
import figtree.treeviewer.*;
import figtree.application.FigTreeFrame;
import jebl.evolution.graphs.Node;

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
public class EukrefTaxonomyAnnotationDialog{
  public final String TITLE = "Annotate taxonomy of selected taxa";
  private JFrame frame;
  private TreeViewer treeViewer;
  private OptionsPanel options;
  private EukrefTaxonomyTableModel model;
  private JTable table;

  public EukrefTaxonomyAnnotationDialog(JFrame frame, TreeViewer treeViewer) {
      this.frame = frame;
      this.treeViewer = treeViewer;
  }

  public int showDialog() {
    options = new OptionsPanel(6, 6);

    Set<Node> selected = treeViewer.getSelectedTips();

    // Init table
    model = new EukrefTaxonomyTableModel(selected);
    table = new JTable(model);
    LineBorder border = new LineBorder(Color.DARK_GRAY);
    table.setBorder(border);
    table.setGridColor(Color.LIGHT_GRAY);
    table.setCellSelectionEnabled(false);
    options.add(table);

    JPopupMenu popupMenu = new JPopupMenu();
    JMenuItem deleteItem = new JMenuItem("Insert column before");
    deleteItem.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
          model.insertColumn(table.getSelectedColumn());
        }
    });
    popupMenu.add(deleteItem);
    table.setComponentPopupMenu(popupMenu);

    Icon icon = IconUtils.getIcon(frame.getClass(), "images/eukref64x64.png");
    JOptionPane optionPane = new JOptionPane(options,
              JOptionPane.QUESTION_MESSAGE,
              JOptionPane.OK_CANCEL_OPTION,
              icon,
              null,
              null);

    final JDialog dialog = optionPane.createDialog(frame, TITLE);
    dialog.setResizable(true);
    dialog.pack();
    dialog.setVisible(true);

    int result = JOptionPane.CANCEL_OPTION;

    Integer value = (Integer)optionPane.getValue();
    if (value != null && value.intValue() != -1) {
        result = value.intValue();
    }

    return result;
  }

  public void assignAnnotation() {
    model.assignAnnotation();
  }
}

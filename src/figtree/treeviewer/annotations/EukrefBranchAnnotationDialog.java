
package figtree.treeviewer.annotations;

import jam.panels.OptionsPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.*;
import java.util.*;

import jebl.util.Attributable;
import figtree.ui.components.WholeNumberField;
import figtree.ui.components.RealNumberField;
import figtree.treeviewer.ExtendedTreeViewer;

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
public class EukrefBranchAnnotationDialog{
  public String TITLE = "Annotate Branch";
  public final static String CODE = "clade_name";
  private JFrame frame;
  private OptionsPanel options;
  private JComponent valueField = null;
  private Attributable branch = null;

  public EukrefBranchAnnotationDialog(JFrame frame) {
      this.frame = frame;
  }

  public int showDialog(Attributable branch) {
    this.branch = branch;
    options = new OptionsPanel(6, 6);

    valueField = new JTextField();
    options.addComponentWithLabel("Value:", valueField);
    ((JTextField)valueField).setColumns(20);
    Object oldValue = null;
    oldValue = branch.getAttribute(CODE);
    if (oldValue != null) {
        ((JTextField)valueField).setText((String)oldValue);
    }

    JOptionPane optionPane = new JOptionPane(options,
              JOptionPane.QUESTION_MESSAGE,
              JOptionPane.OK_CANCEL_OPTION,
              null,
              null,
              null);
    optionPane.setBorder(new EmptyBorder(12, 12, 12, 12));

    final JDialog dialog = optionPane.createDialog(frame, TITLE);
    dialog.pack();
    dialog.setVisible(true);

    int result = JOptionPane.CANCEL_OPTION;

    Integer value = (Integer)optionPane.getValue();
    if (value != null && value.intValue() != -1) {
        result = value.intValue();
    }

    return result;
  }

  public Object getValue() {
    String value = ((JTextField)valueField).getText().trim();
    if (value.length() == 0) {
        return null;
    }
    return value;
  }
}


package figtree.treeviewer.annotations;

import jam.panels.OptionsPanel;
import jam.util.IconUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.*;
import java.awt.Dimension;
import java.util.*;
import org.fife.ui.autocomplete.*;
import java.io.*;
import java.net.*;


import jebl.util.Attributable;
import figtree.ui.components.WholeNumberField;
import figtree.ui.components.RealNumberField;
import figtree.treeviewer.ExtendedTreeViewer;
import figtree.treeviewer.*;
import figtree.application.FigTreeFrame;

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
  public static ArrayList<String> taxaNames = null;
  public String TITLE = "Annotate Branch";
  public final static String CODE = "clade_name";
  private JFrame frame;
  private OptionsPanel options;
  private JTextField valueField = null;
  private Attributable branch = null;

  public EukrefBranchAnnotationDialog(JFrame frame) {
      this.frame = frame;
      if (taxaNames == null){
        initTaxaNames();
      }
  }

  public int showDialog(Attributable branch) {
    this.branch = branch;
    options = new OptionsPanel(6, 6);


    valueField = new JTextField();
    options.addComponentWithLabel("Value:", valueField);
    valueField.setColumns(20);
    Object oldValue = null;
    oldValue = branch.getAttribute(CODE);
    if (oldValue != null) {
        valueField.setText((String)oldValue);
    }

    // label = new JLabel();
    options.addLabel("Tip: use Ctrl+Space to view suggestions");

    DefaultCompletionProvider provider = new DefaultCompletionProvider();
    initProviderOptions(provider);

    AutoCompletion ac = new AutoCompletion(provider);
    ac.setAutoActivationDelay(100);
    ac.setAutoCompleteEnabled(true);
    ac.setAutoActivationEnabled(true);
    ac.setAutoCompleteSingleChoices(false);

    ac.install(valueField);

    Icon icon = IconUtils.getIcon(frame.getClass(), "images/eukref64x64.png");
    JOptionPane optionPane = new JOptionPane(options,
              JOptionPane.QUESTION_MESSAGE,
              JOptionPane.OK_CANCEL_OPTION,
              icon,
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

  private static void initProviderOptions(DefaultCompletionProvider provider){
    for (String name : taxaNames){
      provider.addCompletion(new BasicCompletion(provider, name));
    }

  }

  private static void initTaxaNames(){
    String path = "/figtree/resources/taxa_names.txt";
    InputStream stream = EukrefBranchAnnotationDialog.class.getResourceAsStream(path);
    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

    taxaNames = new ArrayList<String>();

    try {
      String line;

      while((line = reader.readLine()) != null) {
        line = line.trim();
        taxaNames.add(line);

        stream.close();
      }
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }
}

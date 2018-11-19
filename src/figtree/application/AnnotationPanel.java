/*
 * AnnotationPanel.java

 */

package figtree.application;

import jam.controlpalettes.ControlPalette;
import jam.disclosure.SlideOpenPanel;
import figtree.treeviewer.*;
import figtree.treeviewer.painters.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.awt.*;
import java.lang.reflect.Field;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

/**
 * Annotation Panel
 *
 * @author Serafim Nenarokov
 * @version $Id$
 *
 * $HeadURL$
 *
 * $LastChangedBy$
 * $LastChangedDate$
 * $LastChangedRevision$
 */
public class AnnotationPanel extends JPanel {
  public final static int CONTROL_PALETTE_WIDTH = 200;

  private boolean drag = false;
  private Point dragLocation  = new Point();

  private final TreeViewer treeViewer;
  // private final TreesController treesController;
  private final ControlPalette controlPalette;
  // private final SlideOpenPanel slideOpenPanel;

  public AnnotationPanel(JFrame frame, final ExtendedTreeViewer treeViewer, ControlPalette controlPalette) {
    System.out.println("annotation panel");

    this.treeViewer = treeViewer;
    this.controlPalette = controlPalette;

    setBackground(Color.RED);
    setOpaque(true);
    setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
    setLayout(new BorderLayout());

    // setMinimumSize(200);
    // controlPalette.setPreferredWidth(CONTROL_PALETTE_WIDTH);

    JLabel l = new JLabel();
    l.setText("label text");
    add(l);

    // JScrollPane scrollPane = new JScrollPane();
    // scrollPane.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GREEN));
    // scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    // scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    // scrollPane.setViewportView(controlPalette.getPanel());

    // slideOpenPanel = new SlideOpenPanel(treeViewer);

    // JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, slideOpenPanel, scrollPane);
    // splitPane.putClientProperty("Quaqua.SplitPane.style", "bar");
    // splitPane.setOneTouchExpandable(true);
    // add(splitPane, BorderLayout.CENTER);
    // int w = splitPane.getLeftComponent().getPreferredSize().width;
    // splitPane.getRightComponent().setMinimumSize(new Dimension(100, 0));

  }
}

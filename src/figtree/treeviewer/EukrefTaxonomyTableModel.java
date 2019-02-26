package figtree.treeviewer;

import javax.swing.table.*;
import javax.swing.event.TableModelEvent;
import java.util.*;
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
public class EukrefTaxonomyTableModel extends DefaultTableModel{
  protected boolean allInOne = false;
  protected ArrayList<Node> nodes = new ArrayList<Node>();
  public final static String TAXONOMY_ATTR = "taxonomy";
  protected boolean initializationFinished = false;

  public EukrefTaxonomyTableModel(Set<Node> selectedTips) {
    super();
    nodes.addAll(selectedTips);
    ArrayList<ArrayList<String>> rep = oneByOneRepresentation();

    setColumnCount(calcColumnCount(rep));
    setRowCount(nodes.size());

    initValues(rep);
    initializationFinished = true;

    for (Node tip : selectedTips) {
      System.out.println(tip.getAttribute(TAXONOMY_ATTR));
    }
  }

  public void fireTableChanged(TableModelEvent e) {
    super.fireTableChanged(e);

    if (initializationFinished) {
      System.out.println("fireTableChanged");
      Vector data = getDataVector();

      for (int i=0; i<data.size(); i++){
          Vector el = (Vector)data.elementAt(i);
          String label = String.join(";", el);
          System.out.println(label);
          nodes.get(i).setAttribute(TAXONOMY_ATTR, label);
      }

    }
  }

  public ArrayList<ArrayList<String>> allInOneRepresentation() {
    return new ArrayList<ArrayList<String>>();
  }

  public ArrayList<ArrayList<String>> oneByOneRepresentation() {
    ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();

    for (Node node : nodes) {
      Object attr = node.getAttribute(TAXONOMY_ATTR);
      if (atrrExists(attr)) {
        ArrayList<String> taxonomy = new ArrayList<String>(Arrays.asList(attr.toString().split(";")));
        result.add(taxonomy);
      }
    }

    return result;
  }

  public ArrayList<ArrayList<String>> listRepresentation() {
    if (allInOne) {
      return allInOneRepresentation();
    } else {
      return oneByOneRepresentation();
    }
  }

  public void insertColumn(int id) {
    if (id > -1) {
      TableColumn col = new TableColumn(getColumnCount());
      Vector data = getDataVector();

      for (int i=0; i<data.size(); i++){
          Vector el = (Vector)data.elementAt(i);
          el.add(id, "");
      }

      setColumnCount(getColumnCount()+1);
      fireTableStructureChanged();
    }
  }

  public void assignAnnotation() {
    // Vector data = getDataVector();

    // for (int i=0; i<data.size(); i++){
    //     Vector el = (Vector)data.elementAt(i);
    //     el.add(id, "");
    // }

    // for (int i=0; i<representation.size(); i++) {
    //   String label = String.join(";", representation.get(i));
    //   nodes[i].setAttribute(TAXONOMY_ATTR, label);
    // }
  }

  protected boolean atrrExists(Object attr){
    return attr != null && !attr.toString().isEmpty();
  }

  protected int calcColumnCount(ArrayList<ArrayList<String>> representation){
    int length = 0;
    for (ArrayList<String> taxa : representation) {
      if (taxa.size() > length) {
        length = taxa.size();
      }
    }

    if (length == 0) {
      return 1;
    } else {
      return length;
    }
  }

  protected void initValues(ArrayList<ArrayList<String>> representation){
    for (int i=0; i<representation.size(); i++) {
      for (int j=0; j<representation.get(i).size(); j++) {
        setValueAt(representation.get(i).get(j), i, j);
      }
    }
  }
}

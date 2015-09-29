package model;

import java.util.List;
import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataListener;

public class ListEnum implements ComboBoxModel<Object>{

	private List<String> list;
	private String selection = null;

	public ListEnum(List<String> list){
		this.list = list;
	}

	public void addListDataListener(ListDataListener l) {
	}

	public Object getElementAt(int index) {
		return list.get(index);
	}

	public int getSize() {
		return list.size();
	}

	public void removeListDataListener(ListDataListener l) {
	}

	public void setSelectedItem(Object anItem) {
		selection = (String) anItem; // to select and register an
	} // item from the pull-down list

	// Methods implemented from the interface ComboBoxModel
	public Object getSelectedItem() {
		return selection; // to add the selection to the combo box
	}
	
	// Récupère l'index d'un item associé
	public int getIndexOf(String text){
		for(int i = 0; i < list.size(); i++)
			if(list.get(i).toString().equals(text))
				return i;
		return -1;
	}

}

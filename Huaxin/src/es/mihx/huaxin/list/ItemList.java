package es.mihx.huaxin.list;

import java.util.ArrayList;

import es.mihx.huaxin.model.Item;

public class ItemList extends ArrayList<Item> {

	private static final long serialVersionUID = 2531509156350679749L;
	
	public Item getItem(long id){
		for(int i=0;i<this.size();i++)
			if(this.get(i).getId() == id)
				return this.get(i);
		
		return null;
	}
}

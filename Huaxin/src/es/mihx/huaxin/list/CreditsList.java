package es.mihx.huaxin.list;

import java.util.ArrayList;

import es.mihx.huaxin.model.CreditOption;

public class CreditsList extends ArrayList<CreditOption> {
	private static final long serialVersionUID = -5756707304137950263L;
	
	public ArrayList<Integer> getIds(){
		ArrayList<Integer> array = new ArrayList<Integer>();
		
		for(int i=0;i<this.size();i++){
			array.add(this.get(i).getId());
		}
		
		return array;
	}
	
	public ArrayList<String> getNames(){
		ArrayList<String> array = new ArrayList<String>();
		
		for(int i=0;i<this.size();i++){
			array.add(this.get(i).getName());
		}
		
		return array;
	}

	public String getName(int categoryId) {
		for(int i=0;i<this.size();i++)
			if(this.get(i).getId() == categoryId)
				return this.get(i).getName();
		
		return null;
	}
	
	public int getPos(int categoryId){
		for(int i=0;i<this.size();i++)
			if(this.get(i).getId() == categoryId)
				return i;
		
		return 0;
	}
}

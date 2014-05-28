package es.mihx.huaxin.model;

public class CreditOption {

	private int id;
	private String name;
	private int num_credits;
	private double price;
	private String text;
	private String sku;

	public CreditOption(int id, String name, int num_credits, double price,
			String text, String sku) {
		this.id = id;
		this.name = name;
		this.num_credits = num_credits;
		this.price = price;
		this.text = text;
		this.sku = sku;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNum_credits() {
		return num_credits;
	}

	public void setNum_credits(int num_credits) {
		this.num_credits = num_credits;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

}

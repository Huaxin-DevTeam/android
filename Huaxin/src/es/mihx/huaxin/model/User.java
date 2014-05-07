package es.mihx.huaxin.model;

public class User {
	
	private int id;
	private String phone;
	private String email;
	private int num_credits;
	private String token;
	private String device_id;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public int getNum_credits() {
		return num_credits;
	}
	public void setNum_credits(int num_credits) {
		this.num_credits = num_credits;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getDevice_id() {
		return device_id;
	}
	public void setDevice_id(String device_id) {
		this.device_id = device_id;
	}
	
	
	
}

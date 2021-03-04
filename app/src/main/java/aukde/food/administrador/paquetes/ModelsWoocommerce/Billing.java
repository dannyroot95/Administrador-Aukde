package aukde.food.administrador.paquetes.ModelsWoocommerce;

import java.io.Serializable;

public class Billing implements Serializable {

	private String last_name;
	private String first_name;
	private String address_1;
	private String email;
	private String phone;

	public Billing(String last_name, String first_name, String address_1, String email, String phone) {
		this.last_name = last_name;
		this.first_name = first_name;
		this.address_1 = address_1;
		this.email = email;
		this.phone = phone;
	}

	public String getLastName(){
		return last_name;
	}

	public String getFirstName(){
		return first_name;
	}

	public String getAddress_1() {
		return address_1;
	}

	public String getEmail() {
		return email;
	}

	public String getPhone() {
		return phone;
	}
}

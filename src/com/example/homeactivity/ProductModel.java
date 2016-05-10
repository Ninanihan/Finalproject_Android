package com.example.homeactivity;

public class ProductModel {
	
	private int id;
	private String imageurl;
	private String age;
	private String breeds;
	private String city;
	private String father;
	private String mother;
	private String injectionurl;
	private int health;
	private int price;
	private int gender;
	private String user_email;
	
	public int getId(){
		return id;
	}
	public void setId(int id){
		this.id = id;
	}
	public int getHealth(){
		return health;
	}
	public void setHealth(int health){
		this.health = health;
	}
	public int getGender(){
		return gender;
	}
	public void setGender(int gender){
		this.gender = gender;
	}
	
	public int getPrice(){
		return price;
	}
	public void setPrice(int price){
		this.price = price;
	}
	public String getAge(){
		return age;
	}
	public void setAge(String age){
		this.age = age;
	}
	public String getBreeds(){
		return breeds;
	}
	public void setBreeds(String breeds){
		this.breeds = breeds;
	}
	public String getCity(){
		return city;
	}
	public void setCity(String city){
		this.city = city;
	}
	public String getImageurl(){
		return imageurl;
	}
	public void setImageurl(String imageurl){
		this.imageurl = imageurl;
	}
	public String getFather(){
		return father;
	}
	public void setFather(String father){
		this.father = father;
	}
	public String getMother(){
		return mother;
	}
	public void setMother(String mother){
		this.mother = mother;
	}
	public String getInjectionurl(){
		return injectionurl;
	}
	public void setInjectionurl(String injectionurl){
		this.injectionurl = injectionurl;
	}
	public String getUseremail(){
		return user_email;
	}
	public void setUseremail(String user_email){
		this.user_email = user_email;
	}
	
	

}

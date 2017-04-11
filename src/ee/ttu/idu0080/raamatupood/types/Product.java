package ee.ttu.idu0080.raamatupood.types;

import java.io.Serializable;
import java.math.BigDecimal;

public class Product implements Serializable{
	
	private int productCode;
	private BigDecimal price;
	private String name;
	private String description;
	
	public Product(int productCode, String name, String description, BigDecimal price){
		this.productCode = productCode;
		this.price = price;
		this.name = name;
		this.description = description;
		
	}
	
	public int getProductCode() {
		return productCode;
	}
	public void setProductCode(int productCode) {
		this.productCode = productCode;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public String toString() {
		return "Product [productCode=" + productCode + ", price=" + price + ", name=" + name + ", description="
				+ description + "]";
	}


}

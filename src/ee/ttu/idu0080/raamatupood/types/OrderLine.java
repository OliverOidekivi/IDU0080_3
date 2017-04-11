package ee.ttu.idu0080.raamatupood.types;

import java.io.Serializable;
import java.util.List;

public class OrderLine implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Product product;
	private long amount;
	
	
	public OrderLine(Product product, long amount){
		this.product = product;
		this.amount = amount; 
		
	}
	
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	public long getAmount() {
		return amount;
	}
	public void setAmount(long amount) {
		this.amount = amount;
	}
	
	@Override
	public String toString() {
		return "OrderLine [product=" + product + ", amount=" + amount + "]";
	}

	
}

package ee.ttu.idu0080.raamatupood.types;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Order implements Serializable {

	private static final long serialVersionUID = 1L;
	public List<OrderLine> orderLines; 
	
	public Order(){
		orderLines = new ArrayList<OrderLine>();
	}

	public List<OrderLine> getOrderLines() {
		return orderLines;
	}

	public void addOrderLines(OrderLine orderLine) {
		this.orderLines.add(orderLine);
	}

	
}

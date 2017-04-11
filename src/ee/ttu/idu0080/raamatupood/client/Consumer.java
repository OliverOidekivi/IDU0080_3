package ee.ttu.idu0080.raamatupood.client;

import java.math.BigDecimal;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;

import ee.ttu.idu0080.raamatupood.server.EmbeddedBroker;
import ee.ttu.idu0080.raamatupood.types.Car;
import ee.ttu.idu0080.raamatupood.types.Order;
import ee.ttu.idu0080.raamatupood.types.OrderLine;

/**
 * JMS sĆµnumite tarbija. Ć�hendub broker-i urlile
 * 
 * @author Allar Tammik
 * @date 08.03.2010
 */
public class Consumer {
	private static final Logger log = Logger.getLogger(Consumer.class);
	private String SUBJECT = "Tellimuse.edastamine";
	private String SUBJECT2 = "Tellimuse.vastus";
	private String user = ActiveMQConnection.DEFAULT_USER;
	private String password = ActiveMQConnection.DEFAULT_PASSWORD;
	private String url = EmbeddedBroker.URL;
	Session session = null;
	Destination destination = null;
	

	public static void main(String[] args) {
		Consumer consumerTool = new Consumer();
		consumerTool.run();
	}

	public void run() {
		Connection connection = null;
		try {
			log.info("Connecting to URL: " + url);
			log.info("Consuming queue : " + SUBJECT);

			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
					user, password, url);
			connection = connectionFactory.createConnection();

			// Kui ühendus kaob, lõµpetatakse Consumeri töö veateatega.
			connection.setExceptionListener(new ExceptionListenerImpl());

			// KĆ¤ivitame Ć¼henduse
			connection.start();

			// 2. Loome sessiooni
			/*
			 * createSession vĆµtab 2 argumenti: 1. kas saame kasutada
			 * transaktsioone 2. automaatne kinnitamine
			 */
			session = connection.createSession(false,
					Session.AUTO_ACKNOWLEDGE);

			// Loome teadete sihtkoha (jĆ¤rjekorra). Parameetriks jĆ¤rjekorra nimi
			destination = session.createQueue(SUBJECT);

			// 3. Teadete vastuvĆµtja
			MessageConsumer consumer = session.createConsumer(destination);

			// Kui teade vastu vĆµetakse kĆ¤ivitatakse onMessage()
			consumer.setMessageListener(new MessageListenerImpl());

		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	/**
	 * KĆ¤ivitatakse, kui tuleb sĆµnum
	 */
	class MessageListenerImpl implements javax.jms.MessageListener {

		public void onMessage(Message message) {
			try {
				if (message instanceof TextMessage) {
					TextMessage txtMsg = (TextMessage) message;
					String msg = txtMsg.getText();
					log.info("Received: " + msg);
				} else if (message instanceof ObjectMessage) {
					ObjectMessage objectMessage = (ObjectMessage) message;
					Order order = (Order) objectMessage.getObject();
					int productCount = 0;
					BigDecimal itemCount = BigDecimal.ZERO;
					BigDecimal orderPrice = BigDecimal.ZERO;
						
					for(OrderLine orderLine : order.getOrderLines()){
						log.info("Product received: " + orderLine.getProduct().getName());
						log.info("Product code: " + orderLine.getProduct().getProductCode());
						log.info("Product description: " + orderLine.getProduct().getDescription());
						log.info("Product price: " + orderLine.getProduct().getPrice());
						productCount++;
						
						BigDecimal itemAmount = new BigDecimal(orderLine.getAmount()+"");
						BigDecimal linePrice = new BigDecimal(orderLine.getProduct().getPrice()+"");
						linePrice = linePrice.multiply(itemAmount);
						orderPrice = orderPrice.add(linePrice);
						
						itemCount = itemCount.add(itemAmount);
						log.info("Product quantity: " + itemAmount);
						log.info("Orderline price: " + linePrice);
					}
						
					log.info("Recieved: " + productCount);
					String msg = "Number of products " + productCount + " number of items "+ itemCount +" order price " + orderPrice.toString();
					
					destination = session.createQueue(SUBJECT2);
					
					MessageProducer producer = session.createProducer(destination);
					try {
						sendAnswer(session, producer, msg);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else {
					log.info("Received: " + message);
				}

			} catch (JMSException e) {
				log.warn("Caught: " + e);
				e.printStackTrace();
			}
		}
	}
	
	protected void sendAnswer(Session session, MessageProducer producer, String msg)
		throws Exception {
			TextMessage message = session.createTextMessage(msg);
			log.debug("Sending message: " + message.getText());
			producer.send(message);
	}
	
	/**
	 * KĆ¤ivitatakse, kui tuleb viga.
	 */
	class ExceptionListenerImpl implements javax.jms.ExceptionListener {

		public synchronized void onException(JMSException ex) {
			log.error("JMS Exception occured. Shutting down client.");
			ex.printStackTrace();
		}
	}

}
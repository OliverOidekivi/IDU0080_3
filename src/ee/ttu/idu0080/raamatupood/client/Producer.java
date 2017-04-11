package ee.ttu.idu0080.raamatupood.client;

import java.math.BigDecimal;
import java.util.Date;

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
import ee.ttu.idu0080.raamatupood.types.Product;

/**
 * JMS sĆµnumite tootja. Ć�hendub brokeri url-ile
 * 
 * @author Allar Tammik
 * @date 08.03.2010
 */
public class Producer {
	private static final Logger log = Logger.getLogger(Producer.class);
	public static final String SUBJECT = "Tellimuse.edastamine"; // jĆ¤rjekorra nimi
	public static final String SUBJECT2 = "Tellimuse.vastus"; 

	private String user = ActiveMQConnection.DEFAULT_USER;// brokeri jaoks vaja
	private String password = ActiveMQConnection.DEFAULT_PASSWORD;

	long sleepTime = 1000; // 1000ms

	//private int messageCount = 10;
	private long timeToLive = 1000000;
	private String url = EmbeddedBroker.URL;
	//private Session session = null;
	private Destination destination = null;
	

	public static void main(String[] args) {
		Producer producerTool = new Producer();
		producerTool.run();
	}

	public void run() {
		Connection connection = null;
		try {
			log.info("Connecting to URL: " + url);
			log.debug("Sleeping between publish " + sleepTime + " ms");
			if (timeToLive != 0) {
				log.debug("Messages time to live " + timeToLive + " ms");
			}

			// 1. Loome Ć¼henduse
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
					user, password, url);
			connection = connectionFactory.createConnection();
			// KĆ¤ivitame yhenduse
			connection.start();

			// 2. Loome sessiooni
			/*
			 * createSession vĆµtab 2 argumenti: 1. kas saame kasutada
			 * transaktsioone 2. automaatne kinnitamine
			 */
			Session session = connection.createSession(false,
					Session.AUTO_ACKNOWLEDGE);

			// Loome teadete sihtkoha (jĆ¤rjekorra). Parameetriks jĆ¤rjekorra nimi
			Destination destination = session.createQueue(SUBJECT);

			// 3. Loome teadete saatja
			MessageProducer producer = session.createProducer(destination);

			// producer.setDeliveryMode(DeliveryMode.PERSISTENT);
			producer.setTimeToLive(timeToLive);

			// 4. teadete saatmine 
			sendLoop(session, producer);

		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	protected void sendLoop(Session session, MessageProducer producer)
			throws Exception {

		ObjectMessage objectMessage = session.createObjectMessage();
		Order order = new Order();
		order.addOrderLines(new OrderLine(new Product(1, "Nexus 5X", "Google nexus phone by LG", new BigDecimal(250)), 5));
		order.addOrderLines(new OrderLine(new Product(2, "Nexus 6P", "Google nexus phone by Huawei", new BigDecimal(450)), 3));
		order.addOrderLines(new OrderLine(new Product(3, "Samsung KS9800", "Samsung 4K TV", new BigDecimal(3000)), 1));
		order.addOrderLines(new OrderLine(new Product(4, "BenQ BL3201PT", "BenQ 4K computer screen", new BigDecimal(850)), 4));
		objectMessage.setObject(order); 
		log.debug("Sending message: Order");
		producer.send(objectMessage);
		Thread.sleep(sleepTime);

		log.info("Connecting to URL: " + url);
		log.info("Consuming queue : " + SUBJECT2);
		destination = session.createQueue(SUBJECT2);
		MessageConsumer consumer = session.createConsumer(destination);

		// Kui teade vastu vĆµetakse kĆ¤ivitatakse onMessage()
		consumer.setMessageListener(new MessageListenerImpl());
	}

	/*private String createMessageText(int index) {
		return "Message: " + index + " sent at: " + (new Date()).toString();
	}*/
	
	class MessageListenerImpl implements javax.jms.MessageListener {

		public void onMessage(Message message) {
			try {
				
				if (message instanceof TextMessage) {
					TextMessage txtMsg = (TextMessage) message;
					String msg = txtMsg.getText();
					log.info("Received: " + msg);
				} else {
					log.info("Received: " + message);
				}

			} catch (JMSException e) {
				log.warn("Caught: " + e);
				e.printStackTrace();
			}
		}
	
}
}


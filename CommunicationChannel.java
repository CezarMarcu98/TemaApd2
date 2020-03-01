import javax.lang.model.type.NullType;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Semaphore;

/**
 * Class that implements the channel used by headquarters and space explorers to communicate.
 */
public class CommunicationChannel {

	/**
	 * Creates a {@code CommunicationChannel} object.
	 */
	Map<Integer,Boolean> destination= new HashMap<Integer,Boolean>(4);
	int parent1;
	Map<Integer,Integer> parent= new HashMap<Integer,Integer>();
	Message aux1;

	ArrayBlockingQueue<Message> toHeadquarters = new ArrayBlockingQueue<>(250000);
	ArrayBlockingQueue<Message> toShip = new ArrayBlockingQueue<>(2500000);

	public CommunicationChannel() {

	}
	/**
	 * Puts a message on the space explorer channel (i.e., where space explorers write to and
	 * headquarters read from).
	 *
	 * @param message bag mesaj in canal de la se
	 */
	public void putMessageSpaceExplorerChannel(Message message) {

		try {

			//toHeadquarters.remove(message);
			toHeadquarters.put(message);
		} catch (InterruptedException e) {
		}


	}

	/**
	 * Gets a message from the space explorer channel (i.e., where space explorers write to and
	 * headquarters read from).
	 *
	 * @return message from the space explorer channel
	 * scot mesaj de la se
	 */
	public Message getMessageSpaceExplorerChannel() {
		try {
			return toHeadquarters.take();
		} catch (InterruptedException e) {
			return null;
		}

	}

	/**
	 * Puts a message on the headquarters channel (i.e., where headquarters write to and
	 * space explorers read from).
	 *
	 * @param message message to be put on the channel
	 */
	public void putMessageHeadQuarterChannel(Message message) {
			int thid= (int)Thread.currentThread().getId();
		try {
			 if(!message.getData().equals("END") && !message.getData().equals("EXIT")) {
				 if (message.getData().equals("NO_PARENT")) {
					 destination.put(thid, false);
					 parent.put(thid, message.getCurrentSolarSystem());
				 } else {
					 if (destination.get(thid) == true) {
						 parent.replace(thid, message.getCurrentSolarSystem());
						 destination.replace(thid, false);
					 } else {
						 aux1 = new Message(parent.get(thid), message.getCurrentSolarSystem(), message.getData());
						 //toShip.remove(aux1);
						 toShip.put(aux1);
						 destination.replace(thid, true);

					 }
				 }
			 }
		} catch (InterruptedException e) {
		}
	}

	/**
	 * Gets a message from the headquarters channel (i.e., where headquarters write to and
	 * space explorer read from).
	 *
	 * @return message from the header quarter channel
	 */
	public Message getMessageHeadQuarterChannel() {

		try {
			return toShip.take();
		} catch (InterruptedException e) {
			return null;
		}

	}
}
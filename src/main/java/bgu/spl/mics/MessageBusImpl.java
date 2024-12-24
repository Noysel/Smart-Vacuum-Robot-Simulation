package bgu.spl.mics;

import java.util.Queue;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only one public method (in addition to getters which can be public solely for unit testing) may be added to this class
 * All other methods and members you add the class must be private.
 */
public class MessageBusImpl implements MessageBus {
	
	private List<QuadMSQ> listMsqs;
	private int nextIndex;

	public MessageBusImpl() {
		listMsqs = new ArrayList<>();
		nextIndex = 0;
	}

	@Override

	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		m.subscribeEvent(type, null); // CHECKKKKK
		for (int i = 0; i < listMsqs.size(); i++) {
			if (listMsqs.get(i).getMicroService() == m) {
				listMsqs.get(i).getEventSub().add(type);
			}
		}
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		m.subscribeBroadcast(type, null); // CHECKKKKK
		for (int i = 0; i < listMsqs.size(); i++) {
			if (listMsqs.get(i).getMicroService() == m) {
				listMsqs.get(i).getBroadcastSub().add(type);
			}
		}

	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		

	}

	@Override
	public void sendBroadcast(Broadcast b) {
		for (int i = 0; i < listMsqs.size(); i++) {
			if (listMsqs.get(i).getBroadcastSub().contains(b)) {
				listMsqs.get(i).getMessageQueue().add(b);
				notifyAll();
			}
		}

	}
	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		for (int i = nextIndex; i < listMsqs.size() % listMsqs.size(); i++) {
			if (listMsqs.get(i).getEventSub().contains(e.getClass()))  {
				listMsqs.get(i).getMessageQueue().add(e);
				nextIndex = i + 1;
				notifyAll();
				break;
			}
		}
		return new Future<>();
	}

	@Override
	public void register(MicroService m) {
		listMsqs.add(new QuadMSQ(m));
	}

	@Override
	public void unregister(MicroService m) { // CHECK IF NEEDED TO REMOVE REFERNECES
		for (int i = 0; i < listMsqs.size(); i++) {
			if (listMsqs.get(i).getMicroService() == m) {
				listMsqs.remove(i);
				break;
			}
		}
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		boolean isFound = false;
		for (QuadMSQ quad : listMsqs) {
			if (quad.getMicroService() == m) 
				isFound = true;
				while (quad.getMessageQueue().isEmpty()) {
					try {
						this.wait();
					}
					catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				}
				return (Message)quad.getMessageQueue().poll();
		}
		if (!isFound) {
			throw new IllegalStateException();
		}
		return null;
	}
}

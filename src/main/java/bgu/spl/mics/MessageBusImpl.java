package bgu.spl.mics;

import java.util.Queue;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus
 * interface.
 * Write your implementation here!
 * Only one public method (in addition to getters which can be public solely for
 * unit testing) may be added to this class
 * All other methods and members you add the class must be private.
 */
public class MessageBusImpl implements MessageBus {

	private ConcurrentHashMap<MicroService, BlockingQueue<Message>> msqMap;
	private ConcurrentHashMap<Class<? extends Message>, BlockingQueue<MicroService>> subMessageMap;
	private ConcurrentHashMap<Event<?>, Future<?>> eventFutureMap;

	private static class SingletonHolder {
		private volatile static MessageBusImpl instance = new MessageBusImpl();
	}

	public static MessageBusImpl getInstance() {
		return SingletonHolder.instance;
	}

	public MessageBusImpl() {
		msqMap = new ConcurrentHashMap<>();
		subMessageMap = new ConcurrentHashMap<>();
		eventFutureMap = new ConcurrentHashMap<>();
	}

	@Override

	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		subMessageMap.computeIfAbsent(type, k -> new LinkedBlockingQueue<>()).add(m);
		System.out.println(m.getName() + " subscribed to event: " + type.getName());

		// subMessageMap.computeIfAbsent(type, placeHolder -> new
		// LinkedBlockingQueue<>()).add(m);
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		subMessageMap.computeIfAbsent(type, k -> new LinkedBlockingQueue<>()).add(m);
		System.out.println(m.getName() + " subscribed to " + type.getName());
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> void complete(Event<T> e, T result) {
		Future<T> future = (Future<T>) eventFutureMap.get(e);
		if (future != null) {
			future.resolve(result);
		}
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		BlockingQueue<MicroService> qMS = subMessageMap.get(b.getClass());
		if (qMS == null) {
			System.out.println("qMS is null" + b.getClass());
		}
		if (qMS != null) {
			synchronized (qMS) {
				qMS.forEach(ms -> {
					BlockingQueue<Message> qMessages = msqMap.get(ms);
					if (qMessages != null) {
						qMessages.add(b);
					}
				});
			}
		}
	}

	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		BlockingQueue<MicroService> qMS = subMessageMap.get(e.getClass());
		Future<T> future = new Future<>();
		synchronized (qMS) {
			if (qMS == null || qMS.isEmpty()) {
				return null;
			}
			MicroService ms = qMS.poll();
			if (ms == null) {
				System.err.println("No MicroService available to handle event: " + e.getClass().getName());
			}
			
			if (ms != null) {	
				BlockingQueue<Message> qMessages = msqMap.get(ms);
				if (qMessages == null) {
					System.out.println(ms.getName()+ "Queue is null for " + e.getClass());
				}
				if (qMessages != null) {
					//System.out.println(e.getClass() + " added to: " + ms.getName());
					qMessages.add(e);
					qMS.add(ms); // Round-robin logic
					eventFutureMap.put(e, future);
				}
			}
		}
		return future;
	}

	@Override
	public void register(MicroService m) {
		msqMap.putIfAbsent(m, new LinkedBlockingQueue<>());
	}

	@Override
	public void unregister(MicroService m) {
		for (BlockingQueue<MicroService> queue : subMessageMap.values()) {
			synchronized (queue) {
				queue.remove(m);
			}
		}
		msqMap.remove(m);
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		if (!msqMap.containsKey(m)) {
			throw new IllegalStateException();
		}
		BlockingQueue<Message> queue = msqMap.get(m);
		//System.out.println(m.getName() + " Waiting Queue size: " + queue.size());
		return queue.take(); // This will block until a message is available.
	}

	public boolean isRegistered(MicroService m) {
		return msqMap.containsKey(m);
	}
}

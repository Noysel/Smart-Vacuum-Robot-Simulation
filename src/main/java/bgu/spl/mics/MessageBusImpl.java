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
	private ConcurrentHashMap<MicroService, List<Queue<MicroService>>> refMap;
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
		refMap = new ConcurrentHashMap<>();
		subMessageMap = new ConcurrentHashMap<>();
		eventFutureMap = new ConcurrentHashMap<>();
	}

	@Override

	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		subMessageMap.computeIfAbsent(type, placeHolder -> new LinkedBlockingQueue<>()).add(m);
		refMap.computeIfAbsent(m, placeHolder -> new CopyOnWriteArrayList<>()).add(subMessageMap.get(type));
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		subMessageMap.computeIfAbsent(type, placeHolder -> new LinkedBlockingQueue<>()).add(m);
		refMap.computeIfAbsent(m, placeHolder -> new CopyOnWriteArrayList<>()).add(subMessageMap.get(type));

	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		Future<T> future = (Future<T>) eventFutureMap.get(e);
		if (future != null) {
			future.resolve(result);
		}
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		BlockingQueue<MicroService> qMS = subMessageMap.get(b);
		if (!qMS.isEmpty()) {
			for (MicroService ms : qMS) {
				BlockingQueue<Message> qMessages = msqMap.get(ms);
                qMessages.add(b);
            }
		}
	}

	@Override
	public <T> Future<T> sendEvent(Event<T> e) {

		BlockingQueue<MicroService> qMS = subMessageMap.get(e);
		if (qMS.isEmpty()) {
			return null;
		}
		MicroService ms = qMS.poll();
		msqMap.get(ms).add(e);
		qMS.add(ms); // Ensures that the order of MS will be according to Round Robbin
		Future<T> future = new Future<>();
		eventFutureMap.put(e, future);
		return future;

	}

	@Override
	public void register(MicroService m) {
		msqMap.put(m, new LinkedBlockingQueue<>());
	}

	@Override
	public void unregister(MicroService m) {
		List<Queue<MicroService>> listOfPointers = refMap.get(m);
		for (Queue<MicroService> q : listOfPointers) {
			q.remove(m);
		}
		msqMap.remove(m);
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		if (!msqMap.containsKey(m)) {
			throw new IllegalStateException();
		}
		BlockingQueue<Message> queue = msqMap.get(m);
		return queue.take(); // This will block until a message is available.
	}
}

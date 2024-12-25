package bgu.spl.mics;

import java.util.Queue;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus
 * interface.
 * Write your implementation here!
 * Only one public method (in addition to getters which can be public solely for
 * unit testing) may be added to this class
 * All other methods and members you add the class must be private.
 */
public class MessageBusImpl implements MessageBus {

	private ConcurrentHashMap<MicroService, ConcurrentLinkedQueue<Message>> msqMap;
	private ConcurrentHashMap<MicroService, List<Queue<MicroService>>> refMap;
	private ConcurrentHashMap<Class<? extends Message>, ConcurrentLinkedQueue<MicroService>> subMessageMap;
	private ConcurrentHashMap<Event<?>, Future<?>> eventFutureMap;

	private static class Holder {
		private static MessageBusImpl instance = new MessageBusImpl();
	}

	public static MessageBusImpl getInstance() {
		return Holder.instance;
	}

	public MessageBusImpl() {
		msqMap = new ConcurrentHashMap<>();
		refMap = new ConcurrentHashMap<>();
		subMessageMap = new ConcurrentHashMap<>();
		eventFutureMap = new ConcurrentHashMap<>();
	}

	@Override

	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		subMessageMap.computeIfAbsent(type, placeHolder -> new ConcurrentLinkedQueue<>()).add(m);
		refMap.computeIfAbsent(m, placeHolder -> new CopyOnWriteArrayList<>()).add(subMessageMap.get(type));
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		subMessageMap.computeIfAbsent(type, placeHolder -> new ConcurrentLinkedQueue<>()).add(m);
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
		Queue<MicroService> q = subMessageMap.get(b);
		if (!q.isEmpty()) {
			for (MicroService ms : q) {
				msqMap.get(ms).add(b);
			}
			notifyAll();
		}
	}

	@Override
	public <T> Future<T> sendEvent(Event<T> e) {

		Queue<MicroService> q = subMessageMap.get(e);
		if (q.isEmpty()) {
			return null;
		}
		MicroService ms = q.poll();
		msqMap.get(ms).add(e);
		q.add(ms); // Ensures that the order of MS will be according to Round Robbin
		Future<T> future = new Future<>();
		eventFutureMap.put(e, future);
		notifyAll();
		return future;

	}

	@Override
	public void register(MicroService m) {
		msqMap.put(m, new ConcurrentLinkedQueue<>());
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
		Message msg = null;
		if (!msqMap.contains(m)) {
			throw new IllegalStateException();
		}
		if (msqMap.get(m).isEmpty()) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		msg = msqMap.get(m).poll();
		return msg;
	}
}

package com.namehillsoftware.handoff;

import com.namehillsoftware.handoff.rejections.UnhandledRejectionsReceiver;

import java.util.Collections;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

public abstract class SingleMessageBroadcaster<Resolution> extends CancellableBroadcaster<Resolution> {

	private static volatile UnhandledRejectionsReceiver unhandledRejectionsReceiver;

	protected static synchronized void setUnhandledRejectionsReceiver(UnhandledRejectionsReceiver receiver) {
		SingleMessageBroadcaster.unhandledRejectionsReceiver = receiver;
	}

	private final Queue<RespondingMessenger<Resolution>> actualQueue = new ConcurrentLinkedQueue<>();

	private final AtomicReference<Message<Resolution>> atomicMessage = new AtomicReference<>();

	@SuppressWarnings("unchecked")
	private Queue<RespondingMessenger<Resolution>> unhandledErrorQueue = new ConcurrentLinkedQueue<>(Collections.singleton((RespondingMessenger<Resolution>)UnhandledRejectionDispatcher.instance));

	private final AtomicReference<Queue<RespondingMessenger<Resolution>>> recipients = new AtomicReference<>(unhandledErrorQueue);

	protected final void awaitResolution(RespondingMessenger<Resolution> recipient) {
		recipients.compareAndSet(unhandledErrorQueue, actualQueue);

		unhandledErrorQueue = null;

		recipients.get().offer(recipient);

		final Message<Resolution> messageSnapshot = atomicMessage.get();
		if (messageSnapshot != null)
			dispatchMessage(messageSnapshot);
	}

	@Override
	protected final void resolve(Resolution resolution, Throwable rejection) {
		atomicMessage.compareAndSet(null, new Message<>(resolution, rejection));
		dispatchMessage(atomicMessage.get());
	}

	private void dispatchMessage(Message<Resolution> message) {
		RespondingMessenger<Resolution> r;
		while ((r = recipients.get().poll()) != null)
			r.respond(message);
	}

	private static final class UnhandledRejectionDispatcher implements RespondingMessenger {

		private static final UnhandledRejectionDispatcher instance = new UnhandledRejectionDispatcher();

		@Override
		public void respond(Message message) {
			if (message.rejection != null && unhandledRejectionsReceiver != null)
				unhandledRejectionsReceiver.newUnhandledRejection(message.rejection);
		}
	}
}

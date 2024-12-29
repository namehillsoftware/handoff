package com.namehillsoftware.handoff.promises.queued;

import com.namehillsoftware.handoff.Messenger;
import com.namehillsoftware.handoff.cancellation.Cancellable;
import com.namehillsoftware.handoff.cancellation.CancellationResponse;
import com.namehillsoftware.handoff.promises.MessengerOperator;
import com.namehillsoftware.handoff.promises.Promise;
import com.namehillsoftware.handoff.promises.propagation.CancellationProxy;
import com.namehillsoftware.handoff.promises.queued.cancellation.CancellableMessageWriter;
import com.namehillsoftware.handoff.promises.queued.cancellation.CancellablePreparedMessengerOperator;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;

public class QueuedPromise<Resolution> extends Promise<Resolution> {
	public QueuedPromise(MessengerOperator<Resolution> task, Executor executor) {
		executor.execute(new QueuedMessenger(task));
	}

	public QueuedPromise(CancellableMessageWriter<Resolution> task, Executor executor) {
		this(new CancellablePreparedMessengerOperator<>(task), executor);
	}

	public QueuedPromise(MessageWriter<Resolution> task, Executor executor) {
		this(new PreparedMessengerOperator<>(task), executor);
	}

	private final class QueuedMessenger implements
		Messenger<Resolution>,
		Runnable,
		Cancellable {

		private final MessengerOperator<Resolution> task;
		private final CancellationProxy cancellationProxy = new CancellationProxy();
		private final AtomicReference<CancellationResponse> cancellationResponse = new AtomicReference<>();

		QueuedMessenger(MessengerOperator<Resolution> task) {
			this.task = task;
			cancellationProxy.doCancel(this);
			QueuedPromise.this.awaitCancellation(cancellationProxy);
		}

		@Override
		public void sendResolution(Resolution resolution) {
			resolve(resolution);
		}

		@Override
		public void sendRejection(Throwable error) {
			reject(error);
		}

		@Override
		public void awaitCancellation(CancellationResponse cancellationResponse) {
			if (this.cancellationResponse.getAndSet(cancellationResponse) == null) {
				cancellationProxy.doCancel(this);
			}
		}

		@Override
		public void run() {
			try {
				task.send(this);
			} finally {
				cancellationResponse.lazySet(null);
			}
		}

		@Override
		public void cancel() {
			final CancellationResponse cancellationResponse = this.cancellationResponse.get();
			if (cancellationResponse != null)
				cancellationResponse.cancellationRequested();
		}
	}
}

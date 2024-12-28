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
		super(new Execution.QueuedMessengerResponse<>(task, executor));
	}

	public QueuedPromise(CancellableMessageWriter<Resolution> task, Executor executor) {
		this(new CancellablePreparedMessengerOperator<>(task), executor);
	}

	public QueuedPromise(MessageWriter<Resolution> task, Executor executor) {
		this(new PreparedMessengerOperator<>(task), executor);
	}

	private static class Execution {
		static final class QueuedMessengerResponse<Resolution> implements
			MessengerOperator<Resolution>,
			Messenger<Resolution>,
			Runnable,
			Cancellable {

			private final MessengerOperator<Resolution> task;
			private final Executor executor;
			private final CancellationProxy cancellationProxy = new CancellationProxy();
			private final AtomicReference<CancellationResponse> cancellationResponse = new AtomicReference<>();

			private Messenger<Resolution> resultMessenger;

			QueuedMessengerResponse(MessengerOperator<Resolution> task, Executor executor) {
				this.task = task;
				this.executor = executor;
			}

			@Override
			public void send(Messenger<Resolution> resultMessenger) {
				resultMessenger.awaitCancellation(cancellationProxy);
				this.resultMessenger = resultMessenger;
				executor.execute(this);
			}

			@Override
			public void sendResolution(Resolution resolution) {
				resultMessenger.sendResolution(resolution);
			}

			@Override
			public void sendRejection(Throwable error) {
				resultMessenger.sendRejection(error);
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
}

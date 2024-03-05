package com.namehillsoftware.handoff.promises.queued;

import com.namehillsoftware.handoff.Messenger;
import com.namehillsoftware.handoff.cancellation.CancellableMessengerOperator;
import com.namehillsoftware.handoff.cancellation.CancellationToken;
import com.namehillsoftware.handoff.promises.MessengerOperator;
import com.namehillsoftware.handoff.promises.Promise;
import com.namehillsoftware.handoff.promises.queued.cancellation.CancellableMessageWriter;

import java.util.concurrent.Executor;

public class QueuedPromise<Resolution> extends Promise<Resolution> {
	public QueuedPromise(MessengerOperator<Resolution> task, Executor executor) {
		super(new Execution.QueuedMessengerResponse<>(task, executor));
	}

	public QueuedPromise(CancellableMessageWriter<Resolution> task, Executor executor) {
		super(new Execution.QueuedCancellableMessengerResponse<>(new Execution.PreparedCancellableMessengerOperator<>(task), executor));
	}

	public QueuedPromise(MessageWriter<Resolution> task, Executor executor) {
		this(new PreparedMessengerOperator<>(task), executor);
	}

	private static class Execution {
		static final class QueuedMessengerResponse<Resolution> implements
			MessengerOperator<Resolution>,
			Runnable {

			private final MessengerOperator<Resolution> task;
			private final Executor executor;
			private Messenger<Resolution> resultMessenger;

			QueuedMessengerResponse(MessengerOperator<Resolution> task, Executor executor) {
				this.task = task;
				this.executor = executor;
			}

			@Override
			public void send(Messenger<Resolution> resultMessenger) {
				this.resultMessenger = resultMessenger;
				executor.execute(this);
			}

			@Override
			public void run() {
				task.send(resultMessenger);
			}
		}

		static final class QueuedCancellableMessengerResponse<Resolution> implements
			CancellableMessengerOperator<Resolution>,
			Runnable {

			private final CancellableMessengerOperator<Resolution> task;
			private final Executor executor;
			private Messenger<Resolution> resultMessenger;

			QueuedCancellableMessengerResponse(CancellableMessengerOperator<Resolution> task, Executor executor) {
				this.task = task;
				this.executor = executor;
			}

			@Override
			public void send(Messenger<Resolution> resultMessenger) {
				this.resultMessenger = resultMessenger;
				executor.execute(this);
			}

			@Override
			public void run() {
				task.send(resultMessenger);
			}

			@Override
			public void cancellationRequested() {
				task.cancellationRequested();
			}
		}

		static final class PreparedCancellableMessengerOperator<Result> extends CancellationToken implements CancellableMessengerOperator<Result> {

			private final CancellableMessageWriter<Result> writer;

			public PreparedCancellableMessengerOperator(CancellableMessageWriter<Result> writer) {
				this.writer = writer;
			}

			@Override
			public void send(Messenger<Result> messenger) {
				try {
					messenger.sendResolution(writer.prepareMessage(this));
				} catch (Throwable rejection) {
					messenger.sendRejection(rejection);
				}
			}
		}
	}
}

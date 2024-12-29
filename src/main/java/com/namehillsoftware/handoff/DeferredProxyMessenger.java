package com.namehillsoftware.handoff;

import com.namehillsoftware.handoff.cancellation.Cancellable;
import com.namehillsoftware.handoff.cancellation.CancellationResponse;
import com.namehillsoftware.handoff.promises.MessengerOperator;
import com.namehillsoftware.handoff.promises.propagation.CancellationProxy;

import java.util.concurrent.atomic.AtomicReference;

public abstract class DeferredProxyMessenger<Resolution> implements
	MessengerOperator<Resolution>,
	Messenger<Resolution>,
	Runnable,
	Cancellable {

	private final MessengerOperator<Resolution> task;
	private final CancellationProxy cancellationProxy = new CancellationProxy();
	private final AtomicReference<CancellationResponse> cancellationResponse = new AtomicReference<>();

	private Messenger<Resolution> resultMessenger;

	public DeferredProxyMessenger(MessengerOperator<Resolution> task) {
		this.task = task;
	}

	@Override
	public final void send(Messenger<Resolution> resultMessenger) {
		resultMessenger.awaitCancellation(cancellationProxy);
		this.resultMessenger = resultMessenger;
		execute(this);
	}

	protected abstract void execute(Runnable runnable);

	@Override
	public final void sendResolution(Resolution resolution) {
		resultMessenger.sendResolution(resolution);
	}

	@Override
	public final void sendRejection(Throwable error) {
		resultMessenger.sendRejection(error);
	}

	@Override
	public final void awaitCancellation(CancellationResponse cancellationResponse) {
		if (this.cancellationResponse.getAndSet(cancellationResponse) == null)
			cancellationProxy.doCancel(this);
	}

	@Override
	public final void run() {
		try {
			task.send(this);
		} finally {
			cancellationResponse.lazySet(null);
		}
	}

	@Override
	public final void cancel() {
		final CancellationResponse cancellationResponse = this.cancellationResponse.get();
		if (cancellationResponse != null)
			cancellationResponse.cancellationRequested();
	}
}

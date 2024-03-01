package com.namehillsoftware.handoff.promises;

import com.namehillsoftware.handoff.Message;
import com.namehillsoftware.handoff.RespondingMessenger;
import com.namehillsoftware.handoff.promises.propagation.CancellationProxy;
import com.namehillsoftware.handoff.promises.response.EventualAction;
import com.namehillsoftware.handoff.promises.response.ImmediateResponse;

class PromisedEventualAction<Resolution>
extends
	Promise<Resolution>
implements
	RespondingMessenger<Resolution>,
	ImmediateResponse<Throwable, Void> {

	private final CancellationProxy cancellationProxy = new CancellationProxy();
	private final EventualAction onFulfilled;

	PromisedEventualAction(EventualAction onFulfilled) {
		this.onFulfilled = onFulfilled;
		promisedCancellation().must(cancellationProxy);
	}

	@Override
	public final void respond(Message<Resolution> message) {
		try {
			final Promise<?> promisedAction = onFulfilled.promiseAction();
			promisedAction.then(new InternalResolutionProxy<>(message), this);
			cancellationProxy.doCancel(promisedAction);
		} catch (Throwable error) {
			reject(error);
		}
	}

	@Override
	public Void respond(Throwable throwable) {
		reject(throwable);
		return null;
	}

	private final class InternalResolutionProxy<IgnoredResolution> implements ImmediateResponse<IgnoredResolution, Void> {

		private final Message<Resolution> message;

		InternalResolutionProxy(Message<Resolution> message) {
			this.message = message;
		}

		@Override
		public Void respond(IgnoredResolution resolution) {
			if (message.rejection == null)
				resolve(message.resolution);
			else
				reject(message.rejection);
			return null;
		}
	}
}

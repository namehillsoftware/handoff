package com.namehillsoftware.handoff.promises;

import com.namehillsoftware.handoff.Messenger;
import com.namehillsoftware.handoff.cancellation.CancellationResponse;
import com.namehillsoftware.handoff.promises.aggregation.AggregateCancellation;
import com.namehillsoftware.handoff.promises.aggregation.CollectedErrorExcuse;
import com.namehillsoftware.handoff.promises.aggregation.CollectedResultsResolver;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CancellationException;

final class Resolutions {

	static final class AggregatePromiseResolver<Resolution> implements MessengerOperator<Collection<Resolution>> {

		private final Collection<Promise<Resolution>> promises;

		AggregatePromiseResolver(Collection<Promise<Resolution>> promises) {
			this.promises = promises;
        }

		@Override
		public void send(Messenger<Collection<Resolution>> messenger) {
			if (promises.isEmpty()) {
				messenger.sendResolution(Collections.emptyList());
				return;
			}

			final CollectedErrorExcuse<Resolution> errorHandler = new CollectedErrorExcuse<>(messenger, promises);
			if (errorHandler.isRejected()) return;

			final CollectedResultsResolver<Resolution> resolver = new CollectedResultsResolver<>(messenger, promises);
			messenger.awaitCancellation(new AggregateCancellation<>(messenger, promises, resolver));
		}
	}

	static final class HonorFirstPromise<Resolution> extends Promise.Proxy<Resolution> implements CancellationResponse {

		HonorFirstPromise(Collection<Promise<Resolution>> promises) {
			for (Promise<Resolution> promise : promises) {
                proxy(promise);
			}

			awaitCancellation(this);
		}

		@Override
		public void cancellationRequested() {
			reject(new CancellationException());
			getCancellationProxy().cancellationRequested();
		}
	}
}

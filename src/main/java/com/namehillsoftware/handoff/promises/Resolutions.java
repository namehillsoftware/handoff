package com.namehillsoftware.handoff.promises;

import com.namehillsoftware.handoff.Messenger;
import com.namehillsoftware.handoff.promises.aggregation.AggregateCancellation;
import com.namehillsoftware.handoff.promises.aggregation.CollectedErrorExcuse;
import com.namehillsoftware.handoff.promises.aggregation.CollectedResultsResolver;
import com.namehillsoftware.handoff.promises.response.ImmediateAction;
import com.namehillsoftware.handoff.promises.response.ImmediateResponse;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CancellationException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

final class Resolutions {

	static final class AggregatePromiseResolver<Resolution> implements MessengerOperator<Collection<Resolution>> {

		private final Collection<PromiseLike<Resolution>> promises;

		AggregatePromiseResolver(Collection<PromiseLike<Resolution>> promises) {
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
			messenger.promisedCancellation().must(new AggregateCancellation<>(messenger, promises, resolver));
		}
	}

	static final class HonorFirstPromise<Resolution> extends Promise<Resolution> implements ImmediateAction {

		private final Collection<PromiseLike<Resolution>> promises;
		private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

        private boolean isCancelled;

		HonorFirstPromise(Collection<PromiseLike<Resolution>> promises) {
			this.promises = promises;

			ImmediateResponse<Resolution, Void> promiseProxy = resolution -> {
				resolve(resolution);
				return null;
			};

			ImmediateResponse<Throwable, Void> errorProxy = resolution -> {
				final Lock readLock = readWriteLock.readLock();
				readLock.lock();
				try {
					if (isCancelled) return null;
				} finally {
					readLock.unlock();
				}

				reject(resolution);

				return null;
			};

			for (PromiseLike<Resolution> promise : promises) {
                promise.then(promiseProxy, errorProxy);
			}

			promisedCancellation().must(this);
		}

		@Override
		public void act() {
			final Lock writeLock = readWriteLock.writeLock();
			writeLock.lock();
			try {
				isCancelled = true;
			} finally {
				writeLock.unlock();
			}

			for (PromiseLike<Resolution> promise : promises) promise.cancel();
			reject(new CancellationException());
		}
	}
}

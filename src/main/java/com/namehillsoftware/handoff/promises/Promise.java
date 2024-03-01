package com.namehillsoftware.handoff.promises;

import com.namehillsoftware.handoff.Messenger;
import com.namehillsoftware.handoff.SingleMessageBroadcaster;
import com.namehillsoftware.handoff.rejections.UnhandledRejectionsReceiver;

import java.util.Arrays;
import java.util.Collection;

public class Promise<Resolution> extends PromiseLike<Resolution> {
	private final CancellationPromise cancellationPromise = new CancellationPromise();

	public Promise(MessengerOperator<Resolution> messengerOperator) {
		messengerOperator.send(new PromiseMessenger());
	}

	public Promise(Resolution passThroughResult) {
		resolve(passThroughResult);
	}

	public Promise(Throwable rejection) {
		reject(rejection);
	}

	public Promise() {}

	protected final PromiseLike<Void> promisedCancellation() {
		return cancellationPromise;
	}

	@Override
	protected final void respondToCancellation() {
		cancellationPromise.doCancellation();
	}

	@SuppressWarnings("unchecked")
	public static <Resolution> Promise<Resolution> empty() {
		return LazyEmptyPromiseHolder.emptyPromiseInstance;
	}

	@SafeVarargs
	public static <Resolution> Promise<Collection<Resolution>> whenAll(PromiseLike<Resolution>... promises) {
		return whenAll(Arrays.asList(promises));
	}

	public static <Resolution> Promise<Collection<Resolution>> whenAll(Collection<PromiseLike<Resolution>> promises) {
		return new Promise<>(new Resolutions.AggregatePromiseResolver<>(promises));
	}

	@SafeVarargs
	public static <Resolution> Promise<Resolution> whenAny(PromiseLike<Resolution>... promises) {
		return whenAny(Arrays.asList(promises));
	}

	public static <Resolution> Promise<Resolution> whenAny(Collection<PromiseLike<Resolution>> promises) {
		return new Resolutions.HonorFirstPromise<>(promises);
	}

	public static class Rejections {
		public static void setUnhandledRejectionsReceiver(UnhandledRejectionsReceiver receiver) {
			SingleMessageBroadcaster.setUnhandledRejectionsReceiver(receiver);
		}
	}

	private static class LazyEmptyPromiseHolder {
		@SuppressWarnings("rawtypes")
		private static final Promise emptyPromiseInstance = new Promise<>((Object) null);
	}

	private class PromiseMessenger implements Messenger<Resolution> {

		@Override
		public void sendResolution(Resolution resolution) {
			resolve(resolution);
		}

		@Override
		public void sendRejection(Throwable error) {
			reject(error);
		}

		@Override
		public PromiseLike<Void> promisedCancellation() {
			return Promise.this.promisedCancellation();
		}
	}

	private static class CancellationPromise extends PromiseLike<Void> {
		public void doCancellation() {
			resolve(null);
		}
	}
}

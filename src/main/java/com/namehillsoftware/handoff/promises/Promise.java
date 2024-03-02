package com.namehillsoftware.handoff.promises;

import com.namehillsoftware.handoff.Messenger;
import com.namehillsoftware.handoff.RespondingMessenger;
import com.namehillsoftware.handoff.SingleMessageBroadcaster;
import com.namehillsoftware.handoff.cancellation.CancellationToken;
import com.namehillsoftware.handoff.promises.response.*;
import com.namehillsoftware.handoff.rejections.UnhandledRejectionsReceiver;

import java.util.Arrays;
import java.util.Collection;

public class Promise<Resolution> extends SingleMessageBroadcaster<Resolution> {

	private final CancellationToken cancellationToken;

	public Promise(MessengerOperator<Resolution> messengerOperator) {
		this();
		messengerOperator.send(new PromiseMessenger());
	}

	public Promise(Resolution passThroughResult) {
		this();
		resolve(passThroughResult);
	}

	public Promise(Throwable rejection) {
		this();
		reject(rejection);
	}

	public Promise() {
		this(new CancellationToken());
	}

	protected Promise(CancellationToken cancellationToken) {
		this.cancellationToken = cancellationToken;
		initialize(cancellationToken);
	}

	private <NewResolution, PromisedResolution extends Promise<NewResolution> & RespondingMessenger<Resolution>> PromisedResolution then(PromisedResolution onFulfilled) {
		awaitResolution(onFulfilled);

		return onFulfilled;
	}

	public final <NewResolution> Promise<NewResolution> then(ImmediateResponse<Resolution, NewResolution> onFulfilled) {
		return then(new PromiseImmediateResponse<>(onFulfilled));
	}

	public final <NewResolution> Promise<NewResolution> then(ImmediateCancellableResponse<Resolution, NewResolution> onFulfilled) {
		return then(new PromiseImmediateCancellableResponse<>(onFulfilled));
	}

	public final <NewResolution> Promise<NewResolution> then(ImmediateResponse<Resolution, NewResolution> onFulfilled, ImmediateResponse<Throwable, NewResolution> onRejected) {
		return then(new PromiseImmediateResponse<>(onFulfilled, onRejected));
	}

	public final <NewResolution> Promise<NewResolution> then(ImmediateCancellableResponse<Resolution, NewResolution> onFulfilled, ImmediateCancellableResponse<Throwable, NewResolution> onRejected) {
		return then(new PromiseImmediateCancellableResponse<>(onFulfilled, onRejected));
	}

	public final <NewResolution> Promise<NewResolution> eventually(PromisedResponse<Resolution, NewResolution> onFulfilled) {
		return then(new PromisedEventualResponse<>(onFulfilled));
	}

	public final <NewResolution> Promise<NewResolution> eventually(PromisedResponse<Resolution, NewResolution> onFulfilled, PromisedResponse<Throwable, NewResolution> onRejected) {
		return then(new PromisedEventualResponse<>(onFulfilled, onRejected));
	}

	public final <NewResolution> Promise<NewResolution> excuse(ImmediateResponse<Throwable, NewResolution> onRejected) {
		return then(new RejectedResponsePromise<>(onRejected));
	}

	public final <NewResolution> Promise<NewResolution> excuse(ImmediateCancellableResponse<Throwable, NewResolution> onRejected) {
		return then(new RejectedCancellableResponsePromise<>(onRejected));
	}

	public final <NewResolution> Promise<NewResolution> eventuallyExcuse(PromisedResponse<Throwable, NewResolution> onRejected) {
		return then(new PromisedEventualRejection<>(onRejected));
	}

	public final Promise<Resolution> must(ImmediateAction onAny) {
		return then(new ImmediateActionResponse<>(onAny));
	}

	public final Promise<Resolution> inevitably(EventualAction onAny) {
		final PromisedEventualAction<Resolution> promisedEventualAction = new PromisedEventualAction<>(onAny);
		awaitResolution(promisedEventualAction);
		return promisedEventualAction;
	}

	@Override
	protected final void respondToCancellation() {
		if (cancellationToken != null)
			cancellationToken.cancel();

		cancellationRequested();
	}

	protected void initialize(CancellationToken cancellationToken) {}

	protected void cancellationRequested() {}

	@SuppressWarnings("unchecked")
	public static <Resolution> Promise<Resolution> empty() {
		return LazyEmptyPromiseHolder.emptyPromiseInstance;
	}

	@SafeVarargs
	public static <Resolution> Promise<Collection<Resolution>> whenAll(Promise<Resolution>... promises) {
		return whenAll(Arrays.asList(promises));
	}

	public static <Resolution> Promise<Collection<Resolution>> whenAll(Collection<Promise<Resolution>> promises) {
		return new Promise<>(new Resolutions.AggregatePromiseResolver<>(promises));
	}

	@SafeVarargs
	public static <Resolution> Promise<Resolution> whenAny(Promise<Resolution>... promises) {
		return whenAny(Arrays.asList(promises));
	}

	public static <Resolution> Promise<Resolution> whenAny(Collection<Promise<Resolution>> promises) {
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
		public CancellationToken cancellation() {
			return cancellationToken;
		}
	}
}

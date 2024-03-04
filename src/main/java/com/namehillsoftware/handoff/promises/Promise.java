package com.namehillsoftware.handoff.promises;

import com.namehillsoftware.handoff.Messenger;
import com.namehillsoftware.handoff.RespondingMessenger;
import com.namehillsoftware.handoff.SingleMessageBroadcaster;
import com.namehillsoftware.handoff.cancellation.CancellationResponse;
import com.namehillsoftware.handoff.cancellation.PromisedCancellationToken;
import com.namehillsoftware.handoff.promises.response.*;
import com.namehillsoftware.handoff.rejections.UnhandledRejectionsReceiver;

import java.util.Arrays;
import java.util.Collection;

public class Promise<Resolution> extends SingleMessageBroadcaster<Resolution> {

	public Promise(MessengerOperator<Resolution> messengerOperator) {
		final PromiseMessenger messenger = new PromiseMessenger();
		awaitCancellation(messenger);
		messengerOperator.send(messenger);
	}

	public Promise(Resolution passThroughResult) {
		resolve(passThroughResult);
	}

	public Promise(Throwable rejection) {
		reject(rejection);
	}

	protected Promise() {}

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

	public final Promise<Resolution> must(ImmediateCancellableAction onAny) {
		return then(new ImmediateCancellableActionResponse<>(onAny));
	}

	public final Promise<Resolution> inevitably(EventualAction onAny) {
		return then(new PromisedEventualAction<>(onAny));
	}

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

	public static final class Rejections {
		public static void setUnhandledRejectionsReceiver(UnhandledRejectionsReceiver receiver) {
			SingleMessageBroadcaster.setUnhandledRejectionsReceiver(receiver);
		}
	}

	private static final class LazyEmptyPromiseHolder {
		@SuppressWarnings("rawtypes")
		private static final Promise emptyPromiseInstance = new Promise<>((Object) null);
	}

	private final class PromiseMessenger extends PromisedCancellationToken implements Messenger<Resolution>, CancellationResponse {
		@Override
		public void sendResolution(Resolution resolution) {
			resolve(resolution);
		}

		@Override
		public void sendRejection(Throwable error) {
			reject(error);
		}
	}
}

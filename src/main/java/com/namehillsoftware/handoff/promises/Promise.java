package com.namehillsoftware.handoff.promises;

import com.namehillsoftware.handoff.Messenger;
import com.namehillsoftware.handoff.RespondingMessenger;
import com.namehillsoftware.handoff.SingleMessageBroadcaster;
import com.namehillsoftware.handoff.cancellation.Cancellable;
import com.namehillsoftware.handoff.cancellation.CancellationResponse;
import com.namehillsoftware.handoff.errors.HandoffStackTraceFiltering;
import com.namehillsoftware.handoff.promises.propagation.CancellationProxy;
import com.namehillsoftware.handoff.promises.propagation.ProvideProxyablePromise;
import com.namehillsoftware.handoff.promises.response.*;
import com.namehillsoftware.handoff.rejections.UnhandledRejectionsReceiver;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullUnmarked;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;

@NullUnmarked
public class Promise<Resolution> extends SingleMessageBroadcaster<Resolution> {

	public Promise(@NonNull MessengerOperator<Resolution> messengerOperator) {
		messengerOperator.send(new PromiseMessenger());
	}

	public Promise(@Nullable Resolution passThroughResult) {
		resolve(passThroughResult);
	}

	public Promise(@NonNull Throwable rejection) {
		reject(rejection);
	}

	protected Promise() {}

	private <NewResolution, PromisedResolution extends Promise<NewResolution> & RespondingMessenger<Resolution>> PromisedResolution then(PromisedResolution onFulfilled) {
		awaitResolution(onFulfilled);

		return onFulfilled;
	}

	public final <NewResolution> @NonNull Promise<NewResolution> then(@NonNull ImmediateResponse<Resolution, NewResolution> onFulfilled) {
		return then(new PromiseImmediateResponse<>(onFulfilled));
	}

	public final <NewResolution> @NonNull Promise<NewResolution> then(@NonNull ImmediateCancellableResponse<Resolution, NewResolution> onFulfilled) {
		return then(new PromiseImmediateCancellableResponse<>(onFulfilled));
	}

	public final <NewResolution> @NonNull Promise<NewResolution> then(@NonNull ImmediateResponse<Resolution, NewResolution> onFulfilled, @NonNull ImmediateResponse<@NonNull Throwable, NewResolution> onRejected) {
		return then(new PromiseImmediateResponse<>(onFulfilled, onRejected));
	}

	public final <NewResolution> @NonNull Promise<NewResolution> then(@NonNull ImmediateCancellableResponse<Resolution, NewResolution> onFulfilled, @NonNull ImmediateCancellableResponse<@NonNull Throwable, NewResolution> onRejected) {
		return then(new PromiseImmediateCancellableResponse<>(onFulfilled, onRejected));
	}

	public final <NewResolution> @NonNull Promise<NewResolution> eventually(@NonNull PromisedResponse<Resolution, NewResolution> onFulfilled) {
		return then(new PromisedEventualResponse<>(onFulfilled));
	}

	public final <NewResolution> @NonNull Promise<NewResolution> eventually(@NonNull PromisedResponse<Resolution, NewResolution> onFulfilled, @NonNull PromisedResponse<@NonNull Throwable, NewResolution> onRejected) {
		return then(new PromisedEventualResponse<>(onFulfilled, onRejected));
	}

	public final <NewResolution> @NonNull Promise<NewResolution> excuse(@NonNull ImmediateResponse<@NonNull Throwable, NewResolution> onRejected) {
		return then(new RejectedResponsePromise<>(onRejected));
	}

	public final <NewResolution> @NonNull Promise<NewResolution> excuse(@NonNull ImmediateCancellableResponse<@NonNull Throwable, NewResolution> onRejected) {
		return then(new RejectedCancellableResponsePromise<>(onRejected));
	}

	public final <NewResolution> @NonNull Promise<NewResolution> eventuallyExcuse(@NonNull PromisedResponse<@NonNull Throwable, NewResolution> onRejected) {
		return then(new PromisedEventualRejection<>(onRejected));
	}

	public final @NonNull Promise<Resolution> must(@NonNull ImmediateAction onAny) {
		return then(new ImmediateActionResponse<>(onAny));
	}

	public final @NonNull Promise<Resolution> must(@NonNull ImmediateCancellableAction onAny) {
		return then(new ImmediateCancellableActionResponse<>(onAny));
	}

	public final @NonNull Promise<Resolution> inevitably(@NonNull EventualAction onAny) {
		return then(new PromisedEventualAction<>(onAny));
	}

	@SuppressWarnings("unchecked")
	public static <Resolution> @NonNull Promise<@Nullable Resolution> empty() {
		return LazyEmptyPromiseHolder.emptyPromiseInstance;
	}

	@SafeVarargs
	public static <Resolution> @NonNull Promise<Collection<Resolution>> whenAll(@NonNull Promise<Resolution>... promises) {
		return whenAll(Arrays.asList(promises));
	}

	public static <Resolution> @NonNull Promise<Collection<Resolution>> whenAll(@NonNull Collection<Promise<Resolution>> promises) {
		return new Promise<>(new Resolutions.AggregatePromiseResolver<>(promises));
	}

	@SafeVarargs
	public static <Resolution> @NonNull Promise<Resolution> whenAny(@NonNull Promise<Resolution>... promises) {
		return whenAny(Arrays.asList(promises));
	}

	public static <Resolution> @NonNull Promise<Resolution> whenAny(@NonNull Collection<Promise<Resolution>> promises) {
		return new Resolutions.HonorFirstPromise<>(promises);
	}

	public static final class Rejections {
		public static void setUnhandledRejectionsReceiver(UnhandledRejectionsReceiver receiver) {
			SingleMessageBroadcaster.setUnhandledRejectionsReceiver(receiver);
		}

		public static void toggleStackTraceFiltering(boolean enabled) {
			HandoffStackTraceFiltering.toggleStackTraceFiltering(enabled);
		}
	}

	public static class Proxy<Resolution> extends Promise<Resolution> implements CancellationResponse {

		private final ResolutionProxy<Resolution> resolutionProxy = new ResolutionProxy<>(this);
		private final RejectionProxy rejectionProxy = new RejectionProxy(this);
		private final CancellationProxy cancellationProxy = new CancellationProxy();

		public Proxy(@NonNull ProvideProxyablePromise<Resolution> provideProxyablePromise) {
			this();
			proxy(provideProxyablePromise.provide(cancellationProxy));
		}

		protected Proxy() {
			awaitCancellation(this);
		}

		@Override
		public void cancellationRequested() {
			cancellationProxy.cancellationRequested();
		}

		protected final void proxy(@NonNull Promise<Resolution> promise) {
			try {
				proxyResult(promise);
				doCancel(promise);
			} catch (Throwable throwable) {
				reject(throwable);
			}
		}

		protected final void proxyResult(@NonNull Promise<Resolution> promise) {
			try {
				promise.then(resolutionProxy, rejectionProxy);
			} catch (Throwable throwable) {
				reject(throwable);
			}
		}

		protected final void proxyResolution(@NonNull Promise<Resolution> promise) {
			try {
				promise.then(resolutionProxy);
			} catch (Throwable throwable) {
				reject(throwable);
			}
		}

		protected final void proxyRejection(@NonNull Promise<Resolution> promise) {
			try {
				promise.excuse(rejectionProxy);
			} catch (Throwable throwable) {
				reject(throwable);
			}
		}

		protected final void doCancel(@NonNull Cancellable cancellable) {
			cancellationProxy.doCancel(cancellable);
		}

		protected boolean isCancelled() {
			return cancellationProxy.isCancelled();
		}

		public static final class ResolutionProxy<Resolution> implements ImmediateResponse<Resolution, Void> {

			private final Promise<Resolution> promise;

			public ResolutionProxy(Promise<Resolution> promise) {
				this.promise = promise;
			}

			@Override
			public Void respond(Resolution resolution) {
				promise.resolve(resolution);
				return null;
			}
		}

		public static final class RejectionProxy implements ImmediateResponse<@NonNull Throwable, Void> {

			private final Promise<?> promise;

			public RejectionProxy(Promise<?> promise) {
				this.promise = promise;
			}

			@Override
			public Void respond(Throwable throwable) {
				promise.reject(throwable);
				return null;
			}
		}
	}

	private static final class LazyEmptyPromiseHolder {
		@SuppressWarnings("rawtypes")
		private static final Promise emptyPromiseInstance = new Promise<>((Object) null);
	}

	private final class PromiseMessenger implements Messenger<Resolution> {
		@Override
		public void sendResolution(Resolution resolution) {
			resolve(resolution);
		}

		@Override
		public void sendRejection(@NonNull Throwable error) {
			reject(error);
		}

		@Override
		public void awaitCancellation(@NonNull CancellationResponse cancellationResponse) {
			Promise.this.awaitCancellation(cancellationResponse);
		}
	}
}

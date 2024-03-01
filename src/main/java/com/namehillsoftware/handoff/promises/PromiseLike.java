package com.namehillsoftware.handoff.promises;

import com.namehillsoftware.handoff.Messenger;
import com.namehillsoftware.handoff.SingleMessageBroadcaster;
import com.namehillsoftware.handoff.promises.response.EventualAction;
import com.namehillsoftware.handoff.promises.response.ImmediateAction;
import com.namehillsoftware.handoff.promises.response.ImmediateResponse;
import com.namehillsoftware.handoff.promises.response.PromisedResponse;
import com.namehillsoftware.handoff.rejections.UnhandledRejectionsReceiver;

import java.util.Arrays;
import java.util.Collection;

public abstract class PromiseLike<Resolution> extends SingleMessageBroadcaster<Resolution> {

	private <NewResolution> PromiseLike<NewResolution> then(PromiseResponse<Resolution, NewResolution> onFulfilled) {
		awaitResolution(onFulfilled);

		return onFulfilled;
	}

	public final <NewResolution> PromiseLike<NewResolution> then(ImmediateResponse<Resolution, NewResolution> onFulfilled) {
		return then(new PromiseImmediateResponse<>(onFulfilled));
	}

	public final <NewResolution> PromiseLike<NewResolution> then(ImmediateResponse<Resolution, NewResolution> onFulfilled, ImmediateResponse<Throwable, NewResolution> onRejected) {
		return then(new PromiseImmediateResponse<>(onFulfilled, onRejected));
	}

	public final <NewResolution> PromiseLike<NewResolution> eventually(PromisedResponse<Resolution, NewResolution> onFulfilled) {
		return then(new PromisedEventualResponse<>(onFulfilled));
	}

	public final <NewResolution> PromiseLike<NewResolution> eventually(PromisedResponse<Resolution, NewResolution> onFulfilled, PromisedResponse<Throwable, NewResolution> onRejected) {
		return then(new PromisedEventualResponse<>(onFulfilled, onRejected));
	}

	public final <NewResolution> PromiseLike<NewResolution> excuse(ImmediateResponse<Throwable, NewResolution> onRejected) {
		return then(new RejectedResponsePromise<>(onRejected));
	}

	public final <NewResolution> PromiseLike<NewResolution> eventuallyExcuse(PromisedResponse<Throwable, NewResolution> onRejected) {
		return then(new PromisedEventualRejection<>(onRejected));
	}

	public final PromiseLike<Resolution> must(ImmediateAction onAny) {
		return then(new ImmediateActionResponse<>(onAny));
	}

	public final PromiseLike<Resolution> inevitably(EventualAction onAny) {
		final PromisedEventualAction<Resolution> promisedEventualAction = new PromisedEventualAction<>(onAny);
		awaitResolution(promisedEventualAction);
		return promisedEventualAction;
    }
}

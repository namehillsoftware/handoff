package com.namehillsoftware.handoff.promises.aggregation;

import com.namehillsoftware.handoff.Messenger;
import com.namehillsoftware.handoff.promises.Promise;
import com.namehillsoftware.handoff.promises.response.ImmediateResponse;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;

@NullMarked
public class CollectedErrorExcuse<TResult> implements ImmediateResponse<Throwable, Throwable> {

	private final Messenger<Collection<TResult>> messenger;
	private @Nullable Throwable error;

	public CollectedErrorExcuse(Messenger<Collection<TResult>> messenger, Collection<Promise<TResult>> promises) {
		this.messenger = messenger;
		for (Promise<TResult> promise : promises) promise.excuse(this);
	}

	@Override
	public Throwable respond(Throwable throwable) {
		this.error = throwable;
		attemptRejection();
		return throwable;
	}

	private void attemptRejection() {
		if (error != null)
			messenger.sendRejection(error);
	}

	public boolean isRejected() {
		return error != null;
	}
}

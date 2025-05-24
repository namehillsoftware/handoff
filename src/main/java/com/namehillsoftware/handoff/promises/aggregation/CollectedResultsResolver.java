package com.namehillsoftware.handoff.promises.aggregation;

import com.namehillsoftware.handoff.Messenger;
import com.namehillsoftware.handoff.promises.Promise;
import com.namehillsoftware.handoff.promises.response.ImmediateResponse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class CollectedResultsResolver<TResult> implements ImmediateResponse<TResult, Void> {
	private final ConcurrentLinkedQueue<ResultBox<TResult>> collectedResults = new ConcurrentLinkedQueue<>();
	private final int expectedSize;
	private final AtomicInteger remainingResults;
	private final Messenger<Collection<TResult>> collectionMessenger;

	public CollectedResultsResolver(Messenger<Collection<TResult>> collectionMessenger, Collection<Promise<TResult>> promises) {
		this.collectionMessenger = collectionMessenger;

		expectedSize = promises.size();
		remainingResults = new AtomicInteger(expectedSize);

		for (Promise<TResult> promise : promises) promise.then(this);

		if (promises.isEmpty())
			collectionMessenger.sendResolution(Collections.emptyList());
	}

	@Override
	public Void respond(TResult result) {
		collectedResults.add(new ResultBox<>(result));

		if (remainingResults.decrementAndGet() <= 0 && collectionMessenger != null)
			collectionMessenger.sendResolution(getResults());

		return null;
	}

	public final Collection<TResult> getResults() {
		final ArrayList<TResult> results = new ArrayList<>(expectedSize);
		for (ResultBox<TResult> box : collectedResults) {
			results.add(box.result);
		}
		return results;
	}

	private static class ResultBox<TResult> {
		final TResult result;
		ResultBox(TResult result) {
			this.result = result;
		}
	}
}

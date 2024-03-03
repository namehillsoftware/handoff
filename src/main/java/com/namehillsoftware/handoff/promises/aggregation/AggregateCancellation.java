package com.namehillsoftware.handoff.promises.aggregation;

import com.namehillsoftware.handoff.Messenger;
import com.namehillsoftware.handoff.errors.AggregateCancellationException;
import com.namehillsoftware.handoff.promises.Promise;
import com.namehillsoftware.handoff.promises.response.ImmediateAction;

import java.util.ArrayList;
import java.util.Collection;

public class AggregateCancellation<TResult> implements ImmediateAction {

	private final Messenger<Collection<TResult>> collectionMessenger;
	private final Collection<Promise<TResult>> promises;
	private final CollectedResultsResolver<TResult> resultCollector;

	public AggregateCancellation(Messenger<Collection<TResult>> messenger, Collection<Promise<TResult>> promises, CollectedResultsResolver<TResult> resultCollector) {
		this.collectionMessenger = messenger;
		this.promises = promises;
		this.resultCollector = resultCollector;
	}

	@Override
	public void act() {
		for (Promise<?> promise : promises) promise.cancel();

		collectionMessenger.sendRejection(new AggregateCancellationException(new ArrayList<>(resultCollector.getResults())));
	}
}

package com.namehillsoftware.handoff.promises.GivenAPromiseThatResolves.AndAlwaysFinishesWithAnAction;

import com.namehillsoftware.handoff.promises.Promise;
import com.namehillsoftware.handoff.promises.queued.QueuedPromise;
import com.namehillsoftware.handoff.promises.queued.cancellation.TestExecutors;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by david on 10/17/16.
 */
public class WhenTheActionIsCancelled {

	private static volatile String nextReturningPromiseResult;
	private static volatile boolean isCalled;
	private static volatile boolean isCancelled;

	@BeforeClass
	public static void before() throws InterruptedException {
		final CountDownLatch testReadyLatch = new CountDownLatch(1);

		final Promise<String> promisedMustAction = new QueuedPromise<>(cs -> {
			testReadyLatch.await();
			return "test";
		}, TestExecutors.TEST_EXECUTOR)
			.must((cancellationSignal) -> {
				isCancelled = cancellationSignal.isCancelled();
				isCalled = true;
			});

		promisedMustAction.cancel();

		testReadyLatch.countDown();

		promisedMustAction.then(r -> nextReturningPromiseResult = r);
	}

	@Test
	public void thenTheCancellationIsSet() {
		assertThat(isCancelled).isTrue();
	}

	@Test
	public void thenTheNextActionReturnsAPromiseOfTheCorrectType() {
		assertEquals("test", nextReturningPromiseResult);
	}

	@Test
	public void thenTheAlwaysConditionIsCalled() {
		assertTrue(isCalled);
	}
}

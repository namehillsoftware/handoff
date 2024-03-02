package com.namehillsoftware.handoff.promises.GivenAPromiseThatResolves.AndAlwaysFinishesWithAnAction;

import com.namehillsoftware.handoff.promises.Promise;
import com.namehillsoftware.handoff.promises.queued.QueuedPromise;
import com.namehillsoftware.handoff.promises.queued.cancellation.TestExecutors;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by david on 10/17/16.
 */
public class WhenTheActionIsCancelled {

	private static String nextReturningPromiseResult;
	private static boolean isCalled;
	private static boolean isCancelled;

	@BeforeClass
	public static void before() throws InterruptedException {
		final CountDownLatch testReadyLatch = new CountDownLatch(1);

		final Promise<String> promisedMustAction = new QueuedPromise<>(() -> {
			testReadyLatch.await();
			return "test";
		}, TestExecutors.TEST_EXECUTOR)
			.must((cancellationSignal) -> {
				isCancelled = true;
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
		Assert.assertEquals("test", nextReturningPromiseResult);
	}

	@Test
	public void thenTheAlwaysConditionIsCalled() {
		Assert.assertTrue(isCalled);
	}
}

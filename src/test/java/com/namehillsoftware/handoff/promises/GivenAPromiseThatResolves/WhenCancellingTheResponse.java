package com.namehillsoftware.handoff.promises.GivenAPromiseThatResolves;

import com.namehillsoftware.handoff.promises.Promise;
import com.namehillsoftware.handoff.promises.queued.QueuedPromise;
import com.namehillsoftware.handoff.promises.queued.cancellation.TestExecutors;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class WhenCancellingTheResponse {
	private static String nextReturningPromiseResult;

	private static boolean isCancelled;

	@BeforeClass
	public static void before() {
		final CountDownLatch countDownLatch = new CountDownLatch(2);

		final Promise<String> response = new QueuedPromise<>(() -> {
			countDownLatch.await(30, TimeUnit.SECONDS);
			return "test";
		}, TestExecutors.TEST_EXECUTOR)
			.then((nextResult, cancellationSignal) -> {
				countDownLatch.await(30, TimeUnit.SECONDS);
				isCancelled = cancellationSignal.isCancelled();
				return nextReturningPromiseResult = nextResult;
			});

		// Use a count-down latch to ensure the continuation runs on the executor thread (to avoid deadlocks)
		countDownLatch.countDown();

		response.cancel();

		countDownLatch.countDown();
	}

	@Test
	public void thenTheNextActionReturnsAPromiseOfTheCorrectType() {
		assertThat(nextReturningPromiseResult).isEqualTo("test");
	}

	@Test
	public void thenTheResponseIsCancelled() {
		assertThat(isCancelled).isTrue();
	}
}

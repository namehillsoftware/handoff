package com.namehillsoftware.handoff.promises.GivenAPromiseThatResolves;

import com.namehillsoftware.handoff.promises.Promise;
import com.namehillsoftware.handoff.promises.queued.QueuedPromise;
import com.namehillsoftware.handoff.promises.queued.cancellation.TestExecutors;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import static org.assertj.core.api.Assertions.assertThat;

public class WhenCancellingTheResponse {
	private static String nextReturningPromiseResult;

	private static boolean isCancelled;

	@BeforeClass
	public static void before() throws InterruptedException {
		// Use latches to ensure correct order of execution.
		final CountDownLatch cancellationLatch = new CountDownLatch(1);
		final CountDownLatch resultLatch = new CountDownLatch(1);
		final Promise<String> response = new QueuedPromise<>(() -> {
			cancellationLatch.await();
			return "test";
		}, TestExecutors.TEST_EXECUTOR)
			.then((nextResult, cancellationSignal) -> {
				isCancelled = cancellationSignal.isCancelled();
				nextReturningPromiseResult = nextResult;
				resultLatch.countDown();
				return nextResult;
			});

		response.cancel();
		cancellationLatch.countDown();

		resultLatch.await();
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

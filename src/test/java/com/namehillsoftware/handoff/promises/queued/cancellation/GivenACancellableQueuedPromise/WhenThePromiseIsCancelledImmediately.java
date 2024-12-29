package com.namehillsoftware.handoff.promises.queued.cancellation.GivenACancellableQueuedPromise;


import com.namehillsoftware.handoff.promises.queued.QueuedPromise;
import com.namehillsoftware.handoff.promises.queued.cancellation.TestExecutors;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class WhenThePromiseIsCancelledImmediately {
	private static final Throwable thrownException = new Exception();
	private static volatile Throwable caughtException;

	@BeforeClass
	public static void before() throws InterruptedException {
		final QueuedPromise<String> cancellablePromise = new QueuedPromise<>(cs -> {
			if (cs.isCancelled())
				throw thrownException;

			return "";
		}, TestExecutors.TEST_EXECUTOR);

		cancellablePromise.cancel();

		final CountDownLatch rejectionLatch = new CountDownLatch(1);
		cancellablePromise.then(
				(r) -> {
					rejectionLatch.countDown();
					return null;
				},
				(exception) -> {
					caughtException = exception;
					rejectionLatch.countDown();
					return null;
				});

		rejectionLatch.await(10, TimeUnit.SECONDS);
	}

	@Test
	public void thenTheRejectionIsCorrect() {
		assertThat(caughtException).isEqualTo(thrownException);
	}
}

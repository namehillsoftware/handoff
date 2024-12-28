package com.namehillsoftware.handoff.promises.queued.cancellation.GivenACancellableQueuedPromise.AndMultipleCancellationResponsesAreAttached;


import com.namehillsoftware.handoff.promises.queued.QueuedPromise;
import com.namehillsoftware.handoff.promises.queued.cancellation.TestExecutors;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class WhenThePromiseIsCancelledImmediately {
	private static final Throwable thrownException = new Exception();
	private static Throwable caughtException;
	private static volatile boolean isUnexpectedCancellationCalled = false;

	@BeforeClass
	public static void before() throws InterruptedException {
		final CountDownLatch rejectionLatch = new CountDownLatch(1);

		final QueuedPromise<String> cancellablePromise = new QueuedPromise<>((messenger) -> {
			messenger.awaitCancellation(() -> messenger.sendRejection(thrownException));

			try {
				rejectionLatch.await();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			messenger.awaitCancellation(() -> isUnexpectedCancellationCalled = true);
		}, TestExecutors.TEST_EXECUTOR);

		cancellablePromise.cancel();

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

	@Test
	public void thenTheUnexpectedCancellationIsNotCalled() {
		assertThat(isUnexpectedCancellationCalled).isFalse();
	}
}

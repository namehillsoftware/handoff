package com.namehillsoftware.handoff.promises.GivenAPromiseThatResolves.AndTheContinuationIsCancelled.AndTheCancellationCreatesAnException;

import com.namehillsoftware.handoff.promises.Promise;
import com.namehillsoftware.handoff.promises.queued.QueuedPromise;
import com.namehillsoftware.handoff.promises.queued.cancellation.TestExecutors;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by david on 10/17/16.
 */

public class WhenTheCancellationIsCalled {

	private static boolean unhandledRejection;

	@BeforeClass
	public static void before() throws InterruptedException, TimeoutException {
		final CountDownLatch rejectionHandledLatch = new CountDownLatch(1);
		Promise.Rejections.setUnhandledRejectionsReceiver(rejection -> {
			unhandledRejection = true;
			rejectionHandledLatch.countDown();
		});

		final CountDownLatch countDownLatch = new CountDownLatch(1);
		final Promise<Object> promise = new QueuedPromise<>(cs -> {
			if (!countDownLatch.await(10, TimeUnit.SECONDS))
				throw new TimeoutException();

			return new Object();
		}, TestExecutors.TEST_EXECUTOR)
			.eventually(o -> new Promise<>((m) -> m.awaitCancellation(() -> m.sendRejection(new Exception()))));

		promise.excuse(e -> {
			rejectionHandledLatch.countDown();
			return null;
		});

		promise.cancel();

		countDownLatch.countDown();

		if (!rejectionHandledLatch.await(10, TimeUnit.SECONDS))
			throw new TimeoutException();
	}

	@Test
	public void thenTheExceptionIsHandled() {
		assertThat(unhandledRejection).isFalse();
	}
}

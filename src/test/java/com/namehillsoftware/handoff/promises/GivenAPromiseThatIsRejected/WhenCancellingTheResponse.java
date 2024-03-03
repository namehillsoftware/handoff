package com.namehillsoftware.handoff.promises.GivenAPromiseThatIsRejected;

import com.namehillsoftware.handoff.promises.Promise;
import com.namehillsoftware.handoff.promises.queued.QueuedPromise;
import com.namehillsoftware.handoff.promises.queued.cancellation.TestExecutors;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class WhenCancellingTheResponse {
	private static Throwable caughtException;

	private static boolean isCancelled;

	@BeforeClass
	public static void before() {
		final CountDownLatch countDownLatch = new CountDownLatch(2);

		final Promise<Throwable> response = new QueuedPromise<>(() -> {
			countDownLatch.await(30, TimeUnit.SECONDS);
			throw new Exception("whoops");
		}, TestExecutors.TEST_EXECUTOR)
			.excuse((err, cancellationSignal) -> {
				countDownLatch.await(30, TimeUnit.SECONDS);
				isCancelled = cancellationSignal.isCancelled();
				return caughtException = err;
			});

		// Use a count-down latch to ensure the continuation runs on the executor thread (to avoid deadlocks)
		countDownLatch.countDown();

		response.cancel();

		countDownLatch.countDown();
	}

	@Test
	public void thenTheRejectionIsCaught() {
		assertThat(caughtException).isNotNull();
	}

	@Test
	public void thenTheResponseIsCancelled() {
		assertThat(isCancelled).isTrue();
	}
}

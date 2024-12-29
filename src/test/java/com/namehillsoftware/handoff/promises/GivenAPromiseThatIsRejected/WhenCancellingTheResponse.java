package com.namehillsoftware.handoff.promises.GivenAPromiseThatIsRejected;

import com.namehillsoftware.handoff.promises.Promise;
import com.namehillsoftware.handoff.promises.queued.QueuedPromise;
import com.namehillsoftware.handoff.promises.queued.cancellation.TestExecutors;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import static org.assertj.core.api.Assertions.assertThat;

public class WhenCancellingTheResponse {
	private static Throwable caughtException;

	private static volatile boolean isCancelled;

	@BeforeClass
	public static void before() throws InterruptedException {
		// Use latches to ensure correct order of execution.
		final CountDownLatch cancellationLatch = new CountDownLatch(1);
		final CountDownLatch resultLatch = new CountDownLatch(1);

		final Promise<Void> queuedPromise = new QueuedPromise<>(() -> {
			cancellationLatch.await();
			throw new Exception("whoops");
		}, TestExecutors.TEST_EXECUTOR);

		final Promise<Throwable> promisedResponse = queuedPromise
			.excuse((err, cancellationSignal) -> {
				isCancelled = cancellationSignal.isCancelled();
				caughtException = err;
				resultLatch.countDown();
				return err;
			});

		promisedResponse.cancel();
		cancellationLatch.countDown();

		resultLatch.await();
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

package com.namehillsoftware.handoff.promises.GivenAPromiseThatResolvesInTheFuture;

import com.namehillsoftware.handoff.promises.Promise;
import com.namehillsoftware.handoff.promises.response.ImmediateAction;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

/**
 * Created by david on 10/30/16.
 */

public class WhenThePromiseIsCancelledBeforeResolution {
	private static Object result;
	private static Object expectedResult;
	private static ImmediateAction cancellationRunnable;

	@BeforeClass
	public static void before() throws InterruptedException {
		expectedResult = new Object();

		final CountDownLatch latch = new CountDownLatch(1);
		final Promise<Object> promise = new Promise<>((messenger) -> {
			final Thread myNewThread = new Thread(() -> {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					messenger.sendRejection(e);
					return;
				}
				messenger.sendResolution(expectedResult);
				latch.countDown();
			});

			cancellationRunnable = spy(new ThreadCanceller(myNewThread));
			messenger.cancellation().must(cancellationRunnable);

			myNewThread.start();
		});

		promise.cancel();

		promise.then(r -> result = r);

		latch.await(1000, TimeUnit.MILLISECONDS);
	}

	@Test
	public void thenTheExpectedResultIsNotPresent() {
		Assert.assertNotEquals(expectedResult, result);
	}

	@Test
	public void thenTheCancellableIsCalled() {
		verify(cancellationRunnable, times(1)).act();
	}

	private static class ThreadCanceller implements ImmediateAction {
		private final Thread myNewThread;

		ThreadCanceller(Thread myNewThread) {
			this.myNewThread = myNewThread;
		}

		@Override
		public void act() {
			myNewThread.interrupt();
		}
	}
}

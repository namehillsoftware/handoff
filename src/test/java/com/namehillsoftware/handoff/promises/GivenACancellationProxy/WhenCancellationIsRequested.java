package com.namehillsoftware.handoff.promises.GivenACancellationProxy;

import com.namehillsoftware.handoff.promises.Promise;
import com.namehillsoftware.handoff.promises.propagation.CancellationProxy;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.CancellationException;

import static org.assertj.core.api.Assertions.assertThat;

public class WhenCancellationIsRequested {
	private static Throwable unhandledError;
	private static CancellationException exception;
	private static boolean isCancelled;

	@BeforeClass
	public static void act() {
		Promise.Rejections.setUnhandledRejectionsReceiver(e -> unhandledError = e);
		final CancellationProxy cancellationProxy = new CancellationProxy();
		cancellationProxy.doCancel(() -> isCancelled = true);
		cancellationProxy.cancel();
		cancellationProxy
			.excuse(e -> {
				if (e instanceof CancellationException)
					exception = (CancellationException) e;
				return null;
			});
		cancellationProxy.cancellationRequested();
	}

	@Test
	public void thenTheCancellableIsNotCalled() {
		assertThat(isCancelled).isFalse();
	}

	@Test
	public void thenTheCancellationExceptionIsThrown() {
		assertThat(exception).isNotNull();
	}

	@Test
	public void thenTheUnhandledErrorIsNull() {
		assertThat(unhandledError).isNull();
	}
}

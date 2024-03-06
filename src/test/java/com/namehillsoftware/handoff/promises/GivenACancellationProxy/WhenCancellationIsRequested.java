package com.namehillsoftware.handoff.promises.GivenACancellationProxy;

import com.namehillsoftware.handoff.promises.propagation.CancellationProxy;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.CancellationException;

import static org.assertj.core.api.Assertions.assertThat;

public class WhenCancellationIsRequested {
	private static CancellationException exception;

	@BeforeClass
	public static void act() {
		final CancellationProxy cancellationProxy = new CancellationProxy();
		cancellationProxy.cancel();
		cancellationProxy
			.excuse(e -> {
				if (e instanceof CancellationException)
					exception = (CancellationException) e;
				return null;
			});
	}

	@Test
	public void thenTheCancellationExceptionIsThrown() {
		assertThat(exception).isNotNull();
	}
}

package com.namehillsoftware.handoff.promises.GivenAPromiseThatIsCancelled;

import com.namehillsoftware.handoff.cancellation.CancellationResponse;
import com.namehillsoftware.handoff.promises.Promise;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class WhenTheCancellationIsCalledTwice {

	private static final CancellationResponse mockCancel = mock(CancellationResponse.class);

	@BeforeClass
	public static void before() {
		final Promise<String> cancellablePromise =
			new Promise<>((messenger) ->{}, mockCancel);

		cancellablePromise.cancel();
		cancellablePromise.cancel();
	}

	@Test
	public void thenTheCancellationIsCalledOnce() throws Throwable {
		verify(mockCancel, times(1)).cancellationRequested();
	}
}

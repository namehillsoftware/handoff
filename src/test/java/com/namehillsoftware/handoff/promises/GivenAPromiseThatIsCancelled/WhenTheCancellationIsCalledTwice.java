package com.namehillsoftware.handoff.promises.GivenAPromiseThatIsCancelled;

import com.namehillsoftware.handoff.promises.Promise;
import com.namehillsoftware.handoff.promises.response.ImmediateAction;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class WhenTheCancellationIsCalledTwice {

	private static final ImmediateAction mockCancel = mock(ImmediateAction.class);

	@BeforeClass
	public static void before() {
		final Promise<String> cancellablePromise =
			new Promise<>((messenger) -> messenger.promisedCancellation().must(mockCancel));

		cancellablePromise.cancel();
		cancellablePromise.cancel();
	}

	@Test
	public void thenTheCancellationIsCalledOnce() {
		verify(mockCancel, times(1)).act();
	}
}

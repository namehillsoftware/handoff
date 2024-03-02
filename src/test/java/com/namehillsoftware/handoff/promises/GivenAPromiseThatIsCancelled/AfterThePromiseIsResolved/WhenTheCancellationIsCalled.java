package com.namehillsoftware.handoff.promises.GivenAPromiseThatIsCancelled.AfterThePromiseIsResolved;

import com.namehillsoftware.handoff.promises.Promise;
import com.namehillsoftware.handoff.promises.response.ImmediateAction;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * Created by david on 10/17/16.
 */

public class WhenTheCancellationIsCalled {

	private static final ImmediateAction mockCancel = mock(ImmediateAction.class);
	private static final Object expectedResult = new Object();
	private static Object result;

	@BeforeClass
	public static void before() {
		final Promise<Object> cancellablePromise = new Promise<>(
			(messenger) -> {
				messenger.promisedCancellation().must(mockCancel);
				messenger.sendResolution(expectedResult);
			});

		cancellablePromise.then(result -> WhenTheCancellationIsCalled.result = result);

		cancellablePromise.cancel();
	}

	@Test
	public void thenTheCancellationIsNotCalled() {
		verify(mockCancel, times(0)).act();
	}

	@Test
	public void thenTheResultIsCorrect() {
		Assert.assertEquals(expectedResult, result);
	}
}

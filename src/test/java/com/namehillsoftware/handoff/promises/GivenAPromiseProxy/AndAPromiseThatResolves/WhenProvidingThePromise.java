package com.namehillsoftware.handoff.promises.GivenAPromiseProxy.AndAPromiseThatResolves;

import com.namehillsoftware.handoff.promises.Promise;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class WhenProvidingThePromise {

	private static final Object ExpectedResult = new Object();

	private static Object Result;

	@BeforeClass
	public static void act() {
		final Promise.Proxy<Object> proxyPromise = new Promise.Proxy<>(cancellationProxy -> new Promise<>(ExpectedResult));
		proxyPromise.then(r -> Result = r);
	}

	@Test
	public void thenTheResultIsCorrect() {
		assertThat(Result).isEqualTo(ExpectedResult);
	}
}

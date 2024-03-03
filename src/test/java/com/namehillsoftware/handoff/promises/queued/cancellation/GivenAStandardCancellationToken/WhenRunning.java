package com.namehillsoftware.handoff.promises.queued.cancellation.GivenAStandardCancellationToken;


import com.namehillsoftware.handoff.cancellation.CancellationToken;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class WhenRunning {

	private static final CancellationToken cancellationToken = new CancellationToken();

	@BeforeClass
	public static void before() {
		cancellationToken.cancel();
	}

	@Test
	public void thenTheTokenIsCancelled() {
		assertThat(cancellationToken.isCancelled()).isTrue();
	}
}

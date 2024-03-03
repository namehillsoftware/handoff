package com.namehillsoftware.handoff.promises.queued.cancellation;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TestExecutors {
	public static Executor TEST_EXECUTOR = Executors.newCachedThreadPool();
}

package com.namehillsoftware.handoff.promises.response;

import com.namehillsoftware.handoff.promises.Promise;
import org.jspecify.annotations.NonNull;

public interface EventualAction {
	@NonNull Promise<?> promiseAction();
}

package com.namehillsoftware.handoff.errors;

import com.namehillsoftware.handoff.promises.response.ImmediateResponse;
import org.jspecify.annotations.NonNull;

public final class RejectionDropper implements ImmediateResponse<@NonNull Throwable, Void> {

	private RejectionDropper() {}

	@Override
	public Void respond(@NonNull Throwable throwable) throws Throwable {
		return null;
	}

	public static class Instance {

		private static final RejectionDropper instance = new RejectionDropper();

		public static RejectionDropper get() {
			return instance;
		}
	}
}

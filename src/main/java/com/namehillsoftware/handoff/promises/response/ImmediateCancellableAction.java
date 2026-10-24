package com.namehillsoftware.handoff.promises.response;

import com.namehillsoftware.handoff.cancellation.CancellationSignal;
import org.jspecify.annotations.NonNull;

public interface ImmediateCancellableAction {
    void act(@NonNull CancellationSignal cancellationSignal) throws Throwable;
}

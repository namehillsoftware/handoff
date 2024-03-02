package com.namehillsoftware.handoff.promises.response;

import com.namehillsoftware.handoff.cancellation.CancellationSignal;

public interface ImmediateCancellableAction {
    void act(CancellationSignal cancellationSignal) throws Throwable;
}

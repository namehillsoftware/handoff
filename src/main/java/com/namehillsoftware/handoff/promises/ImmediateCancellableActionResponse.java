package com.namehillsoftware.handoff.promises;

import com.namehillsoftware.handoff.cancellation.CancellationSignal;
import com.namehillsoftware.handoff.promises.response.ImmediateCancellableAction;

final class ImmediateCancellableActionResponse<Resolution> extends ImmediatelyRespondingMessenger<Resolution, Resolution> {

    private final ImmediateCancellableAction response;

    ImmediateCancellableActionResponse(ImmediateCancellableAction response) {
        this.response = response;
    }

    @Override
    protected void respond(Resolution resolution, CancellationSignal cancellationSignal) throws Throwable {
        response.act(cancellationSignal);
        resolve(resolution);
    }

    @Override
    protected void respond(Throwable reason, CancellationSignal cancellationSignal) throws Throwable {
        response.act(cancellationSignal);
        reject(reason);
    }
}

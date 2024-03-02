package com.namehillsoftware.handoff.promises;

import com.namehillsoftware.handoff.cancellation.CancellationSignal;
import com.namehillsoftware.handoff.promises.response.ImmediateAction;

final class ImmediateActionResponse<Resolution> extends ImmediatelyRespondingMessenger<Resolution, Resolution> {

    private final ImmediateAction response;

    ImmediateActionResponse(ImmediateAction response) {
        this.response = response;
    }

    @Override
    protected void respond(Resolution resolution, CancellationSignal cancellationSignal) {
        response.act();
        resolve(resolution);
    }

    @Override
    protected void respond(Throwable reason, CancellationSignal cancellationSignal) {
        response.act();
        reject(reason);
    }
}

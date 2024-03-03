package com.namehillsoftware.handoff.promises;

import com.namehillsoftware.handoff.promises.response.ImmediateAction;

final class ImmediateActionResponse<Resolution> extends ImmediatelyRespondingMessenger<Resolution, Resolution> {

    private final ImmediateAction response;

    ImmediateActionResponse(ImmediateAction response) {
        this.response = response;
    }

    @Override
    protected void respond(Resolution resolution) throws Throwable {
        response.act();
        resolve(resolution);
    }

    @Override
    protected void respond(Throwable reason) throws Throwable {
        response.act();
        reject(reason);
    }
}

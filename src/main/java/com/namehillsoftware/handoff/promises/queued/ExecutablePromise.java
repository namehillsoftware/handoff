package com.namehillsoftware.handoff.promises.queued;

public abstract class ExecutablePromise<Resolution> extends DispatchablePromise<Resolution> implements Runnable {

    public ExecutablePromise() {
        super();
    }

    @Override
    public final void run() {
        dispatchMessage();
    }
}

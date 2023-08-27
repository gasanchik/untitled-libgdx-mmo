package com.hasanchik.shared.misc;

import lombok.Getter;

import java.util.function.Consumer;

@Getter
public class FixedTimeStepExecutor {
    private float lastUpdateTime = System.nanoTime();
    private final float fixedTimeStep;
    private float accumulator;
    private float alpha;

    public FixedTimeStepExecutor(float fixedTimeStep) {
        this.fixedTimeStep = fixedTimeStep;
    }

    public void update(Consumer<Float> consumer) {
        long currentTime = System.nanoTime();
        float deltaTime = (currentTime - lastUpdateTime) / 1_000_000_000f;
        lastUpdateTime = currentTime;

        //Fixed simulation timestep
        accumulator += Math.min(0.25f, deltaTime);
        while (accumulator >= fixedTimeStep) {
            consumer.accept(deltaTime);
            accumulator -= fixedTimeStep;
        }

        alpha = accumulator / fixedTimeStep;
    }
}

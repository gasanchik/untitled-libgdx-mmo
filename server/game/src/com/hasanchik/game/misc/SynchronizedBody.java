package com.hasanchik.game.misc;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import lombok.Getter;

public class SynchronizedBody {
    @Getter
    private Body body;
    
    SynchronizedBody(Body body) {
        this.body = body;
    }

    // CreateFixture methods
    
    public synchronized Fixture createFixture(FixtureDef def) {
        return body.createFixture(def);
    }

    public synchronized Fixture createFixture(Shape shape, float density) {
        return body.createFixture(shape, density);
    }

    // DestroyFixture method
    public synchronized void destroyFixture(Fixture fixture) {
        body.destroyFixture(fixture);
    }

    // SetTransform method
    
    public synchronized void setTransform(Vector2 position, float angle) {
        body.setTransform(position, angle);
    }

    // GetTransform method
    
    public synchronized Transform getTransform() {
        return body.getTransform();
    }

    // GetPosition method
    
    public synchronized Vector2 getPosition() {
        return body.getPosition();
    }

    // GetAngle method
    
    public synchronized float getAngle() {
        return body.getAngle();
    }

    // GetWorldCenter method
    
    public synchronized Vector2 getWorldCenter() {
        return body.getWorldCenter();
    }

    // GetLocalCenter method
    
    public synchronized Vector2 getLocalCenter() {
        return body.getLocalCenter();
    }

    // SetLinearVelocity method
    
    public synchronized void setLinearVelocity(Vector2 v) {
        body.setLinearVelocity(v);
    }

    // GetLinearVelocity method
    
    public synchronized Vector2 getLinearVelocity() {
        return body.getLinearVelocity();
    }

    // SetAngularVelocity method
    
    public synchronized void setAngularVelocity(float omega) {
        body.setAngularVelocity(omega);
    }

    // GetAngularVelocity method
    
    public synchronized float getAngularVelocity() {
        return body.getAngularVelocity();
    }

    // ApplyForce method
    
    public synchronized void applyForce(Vector2 force, Vector2 point, boolean wake) {
        body.applyForce(force, point, wake);
    }

    // ApplyForceToCenter method
    
    public synchronized void applyForceToCenter(Vector2 force, boolean wake) {
        body.applyForceToCenter(force, wake);
    }

    // ApplyTorque method
    
    public synchronized void applyTorque(float torque, boolean wake) {
        body.applyTorque(torque, wake);
    }

    // ApplyLinearImpulse method
    
    public synchronized void applyLinearImpulse(Vector2 impulse, Vector2 point, boolean wake) {
        body.applyLinearImpulse(impulse, point, wake);
    }

    // ApplyAngularImpulse method
    
    public synchronized void applyAngularImpulse(float impulse, boolean wake) {
        body.applyAngularImpulse(impulse, wake);
    }

    // GetMass method
    
    public synchronized float getMass() {
        return body.getMass();
    }

    // GetInertia method
    
    public synchronized float getInertia() {
        return body.getInertia();
    }

    // GetMassData method
    
    public synchronized MassData getMassData() {
        return body.getMassData();
    }

    // SetMassData method
    
    public synchronized void setMassData(MassData data) {
        body.setMassData(data);
    }

    // ResetMassData method
    
    public synchronized void resetMassData() {
        body.resetMassData();
    }

    // GetWorldPoint method
    
    public synchronized Vector2 getWorldPoint(Vector2 localPoint) {
        return body.getWorldPoint(localPoint);
    }

    // GetWorldVector method
    
    public synchronized Vector2 getWorldVector(Vector2 localVector) {
        return body.getWorldVector(localVector);
    }

    // GetLocalPoint method
    
    public synchronized Vector2 getLocalPoint(Vector2 worldPoint) {
        return body.getLocalPoint(worldPoint);
    }

    // GetLocalVector method
    
    public synchronized Vector2 getLocalVector(Vector2 worldVector) {
        return body.getLocalVector(worldVector);
    }

    // GetLinearVelocityFromWorldPoint method
    
    public synchronized Vector2 getLinearVelocityFromWorldPoint(Vector2 worldPoint) {
        return body.getLinearVelocityFromWorldPoint(worldPoint);
    }

    // GetLinearVelocityFromLocalPoint method
    
    public synchronized Vector2 getLinearVelocityFromLocalPoint(Vector2 localPoint) {
        return body.getLinearVelocityFromLocalPoint(localPoint);
    }

    // GetLinearDamping method
    
    public synchronized float getLinearDamping() {
        return body.getLinearDamping();
    }

    // SetLinearDamping method
    
    public synchronized void setLinearDamping(float linearDamping) {
        body.setLinearDamping(linearDamping);
    }

    // GetAngularDamping method
    
    public synchronized float getAngularDamping() {
        return body.getAngularDamping();
    }

    // SetAngularDamping method
    
    public synchronized void setAngularDamping(float angularDamping) {
        body.setAngularDamping(angularDamping);
    }

    // GetGravityScale method
    
    public synchronized float getGravityScale() {
        return body.getGravityScale();
    }

    // SetGravityScale method
    
    public synchronized void setGravityScale(float scale) {
        body.setGravityScale(scale);
    }

    // SetType method
    
    public synchronized void setType(BodyDef.BodyType type) {
        body.setType(type);
    }

    // GetType method
    
    public synchronized BodyDef.BodyType getType() {
        return body.getType();
    }

    // SetBullet method
    
    public synchronized void setBullet(boolean flag) {
        body.setBullet(flag);
    }

    // IsBullet method
    
    public synchronized boolean isBullet() {
        return body.isBullet();
    }

    // SetSleepingAllowed method
    
    public synchronized void setSleepingAllowed(boolean flag) {
        body.setSleepingAllowed(flag);
    }

    // IsSleepingAllowed method
    
    public synchronized boolean isSleepingAllowed() {
        return body.isSleepingAllowed();
    }

    // SetAwake method
    
    public synchronized void setAwake(boolean flag) {
        body.setAwake(flag);
    }

    // IsAwake method
    
    public synchronized boolean isAwake() {
        return body.isAwake();
    }

    // SetFixedRotation method
    
    public synchronized void setFixedRotation(boolean flag) {
        body.setFixedRotation(flag);
    }

    // IsFixedRotation method
    
    public synchronized boolean isFixedRotation() {
        return body.isFixedRotation();
    }

    // GetFixtureList method
    
    public synchronized Array<Fixture> getFixtureList() {
        return body.getFixtureList();
    }

    // GetJointList method
    
    public synchronized Array<JointEdge> getJointList() {
        return body.getJointList();
    }
}
package dev.m4nd3l.craftmine.renderer;

import dev.m4nd3l.craftmine.coordinates.EntityCoordinates;
import dev.m4nd3l.craftmine.renderer.input.Keyboard;
import dev.m4nd3l.craftmine.renderer.input.KeyboardKeys;
import dev.m4nd3l.craftmine.renderer.input.Mouse;
import dev.m4nd3l.craftmine.renderer.input.MouseKeys;
import dev.m4nd3l.craftmine.renderer.opengl.ShaderProgram;
import dev.m4nd3l.craftmine.renderer.opengl.shaders.uniforms.Matrix4fUniform;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class Camera {

    float FOV, nearPlane, farPlane;
    int width, height;

    float speed = 8f;
    float sensitivity = 100.0f;

    Vector3f cameraPosition,
            cameraOrientation = new Vector3f(0.0f, 0.0f, -1.0f),
            upDirection = new Vector3f(0.0f, 1.0f, 0.0f);
    Matrix4f viewMatrix = new Matrix4f();
    Matrix4f projectionMatrix = new Matrix4f();

    public Camera(float FOV, float nearPlane, float farPlane, int width, int height, Vector3f cameraPosition) {
        this.FOV = FOV;
        this.nearPlane = nearPlane;
        this.farPlane = farPlane;
        this.width = width;
        this.height = height;
        this.cameraPosition = cameraPosition;
    }

    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public void processKeyboard(@NotNull Keyboard keyboard, float deltaTime) {
        float velocity = speed * deltaTime;

        if (keyboard.isKeyDown(KeyboardKeys.W))
            cameraPosition.add(new Vector3f(cameraOrientation).mul(velocity));

        if (keyboard.isKeyDown(KeyboardKeys.A))
            cameraPosition.add(
                    new Vector3f(cameraOrientation)
                            .cross(upDirection)
                            .normalize()
                            .mul(-velocity)
            );

        if (keyboard.isKeyDown(KeyboardKeys.S))
            cameraPosition.add(new Vector3f(cameraOrientation).mul(-velocity));

        if (keyboard.isKeyDown(KeyboardKeys.D))
            cameraPosition.add(
                    new Vector3f(cameraOrientation)
                            .cross(upDirection)
                            .normalize()
                            .mul(velocity)
            );

        if (keyboard.isKeyDown(KeyboardKeys.SPACE))
            cameraPosition.add(new Vector3f(upDirection).mul(velocity));

        if (keyboard.isKeyDown(KeyboardKeys.LEFT_SHIFT))
            cameraPosition.add(new Vector3f(upDirection).mul(-velocity));

        if (keyboard.isKeyDown(KeyboardKeys.LEFT_CONTROL)) speed = 14.0f;
        else speed = 8.0f;

    }

    public void processMouseMovement(Mouse mouse, long glfwWindow) {
        if (mouse.isButtonDown(MouseKeys.LEFT)) {

            GLFW.glfwSetInputMode(glfwWindow, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_HIDDEN);

            double mouseX = mouse.getX();
            double mouseY = mouse.getY();

            float rotationX = sensitivity * (float) (mouseY - height / 2.0f) / height;
            float rotationY = sensitivity * (float) (mouseX - width / 2.0f) / width;

            Vector3f right = new Vector3f(cameraOrientation)
                    .cross(upDirection)
                    .normalize();

            Vector3f newOrientation = new Vector3f(cameraOrientation)
                    .rotateAxis((float) Math.toRadians(-rotationX),
                            right.x, right.y, right.z);

            float angle = newOrientation.angle(upDirection);

            if (Math.abs(angle - Math.toRadians(90.0f)) <= Math.toRadians(85.0f)) {
                cameraOrientation.set(newOrientation);
            }

            cameraOrientation.rotateAxis(
                    (float) Math.toRadians(-rotationY),
                    upDirection.x, upDirection.y, upDirection.z
            );

            GLFW.glfwSetCursorPos(glfwWindow, width / 2.0, height / 2.0);

        } else {
            GLFW.glfwSetInputMode(glfwWindow, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
        }
    }

    public void uploadUniforms(ShaderProgram shader) {
        shader.uploadUniform(new Matrix4fUniform("viewMatrix", shader.getShaderID(), getViewMatrix()));
        shader.uploadUniform(new Matrix4fUniform("projectionMatrix", shader.getShaderID(), getProjectionMatrix()));
    }


    public void updateMatrices() {
        Vector3f center = new Vector3f(cameraPosition).add(cameraOrientation);
        viewMatrix.identity().lookAt(cameraPosition, center, upDirection);
        projectionMatrix.identity().perspective(
                (float) Math.toRadians(FOV), (float) width / (float) height, nearPlane, farPlane);
    }

    public EntityCoordinates getEntityPosition() { return new EntityCoordinates(cameraPosition.x, cameraPosition.y, cameraPosition.z); }

    public Camera resizeWindow(int width, int height) { this.width = width; this.height = height; updateMatrices(); return this; }

    public Camera setSpeed(float speed) { this.speed = speed; return this; }
    public Camera setSensitivity(float sensitivity) { this.sensitivity = sensitivity; return this; }
    public Camera setFOV(float FOV) { this.FOV = FOV; updateMatrices(); return this; }
    public Camera setNearPlane(float nearPlane) { this.nearPlane = nearPlane; updateMatrices(); return this; }
    public Camera setFarPlane(float farPlane) { this.farPlane = farPlane; updateMatrices(); return this; }

}
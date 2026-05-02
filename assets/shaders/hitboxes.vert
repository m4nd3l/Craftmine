#version 330 core

layout(location = 0) in vec3 aPosition;

uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

void main() {
    gl_Position = projectionMatrix * viewMatrix * vec4(aPosition, 1.0);
}
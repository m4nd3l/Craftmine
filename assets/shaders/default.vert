#version 330 core

layout(location = 0) in vec3 aPosition;
layout(location = 1) in vec3 aNormal;
layout(location = 2) in vec3 aColor;
layout(location = 3) in vec2 aUV;      // Tiling UV (width, height)
layout(location = 4) in vec2 aUVMin;    // Atlas Min (from uploadToGPU location 4)
layout(location = 5) in vec2 aUVMax;    // Atlas Max (from uploadToGPU location 5)

out vec3 vNormal;
out vec3 vColor;
out vec2 vUV;
out vec2 vUVMin;
out vec2 vUVMax;

uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

void main() {
    vNormal = aNormal;
    vColor = aColor;
    vUV = aUV;
    vUVMin = aUVMin;
    vUVMax = aUVMax;
    gl_Position = projectionMatrix * viewMatrix * vec4(aPosition, 1.0);
}
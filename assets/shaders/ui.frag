#version 330 core

in vec4 fColor;
in vec2 fTexCoords;

out vec4 fragColor;

uniform sampler2D uTexture;

void main() {
    vec4 texColor = texture(uTexture, fTexCoords);
    fragColor = texColor * fColor;
}
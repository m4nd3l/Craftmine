#version 330 core

in vec3 vNormal;
in vec3 vColor;
in vec2 vUV;
in vec2 vUVMin;
in vec2 vUVMax;

out vec4 finalColor;

uniform sampler2D blockTexture;

void main() {
    // Correctly calculate the tiled UV within the atlas bounds
    vec2 atlasRange = vUVMax - vUVMin;
    vec2 actualUV = vUVMin + fract(vUV) * atlasRange;

    vec4 texColor = texture(blockTexture, actualUV);

    // Discard transparent pixels (important for leaves/grass)
    if(texColor.a < 0.1) discard;

    vec3 sunDir = normalize(vec3(0.4, 1.0, 0.2));
    float diffuse = max(dot(vNormal, sunDir), 0.5);

    finalColor = vec4(texColor.rgb * vColor * diffuse, texColor.a);
}
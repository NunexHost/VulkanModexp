#version 450

float linear_fog_fade(float vertexDistance, float fogStart, float fogEnd) {
    if (vertexDistance <= fogStart) {
        return 1.0;
    } else if (vertexDistance >= fogEnd) {
        return 0.0;
    }

    return smoothstep(fogEnd, fogStart, vertexDistance);
}

layout(binding = 2) uniform sampler2D Sampler0;

layout(binding = 1) uniform UBO{
<<<<<<< HEAD
=======
    vec4 ColorModulator;
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
    float FogStart;
    float FogEnd;
};

layout(location = 0) in float vertexDistance;
layout(location = 1) in vec4 vertexColor;
layout(location = 2) in vec2 texCoord0;

layout(location = 0) out vec4 fragColor;

void main() {
<<<<<<< HEAD
    vec4 color = texture(Sampler0, texCoord0) * vertexColor;
=======
    vec4 color = texture(Sampler0, texCoord0) * vertexColor * ColorModulator;
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
    if (color.a < 0.1) {
        discard;
    }
    fragColor = color * linear_fog_fade(vertexDistance, FogStart, FogEnd);
}

/*
#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;

in float vertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;

out vec4 fragColor;
*/


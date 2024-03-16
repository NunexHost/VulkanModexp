#version 450

float linear_fog_fade(float vertexDistance, float fogStart, float fogEnd) {
    if (vertexDistance <= fogStart) {
        return 1.0;
    } else if (vertexDistance >= fogEnd) {
        return 0.0;
    }

    return smoothstep(fogEnd, fogStart, vertexDistance);
}

layout(binding = 1) uniform UBO{
<<<<<<< HEAD
=======
    vec4 ColorModulator;
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
    float FogStart;
    float FogEnd;
};

layout(location = 0) in vec4 vertexColor;
layout(location = 1) in float vertexDistance;

layout(location = 0) out vec4 fragColor;

void main() {
<<<<<<< HEAD
    fragColor = vertexColor * linear_fog_fade(vertexDistance, FogStart, FogEnd);
=======
    fragColor = vertexColor * ColorModulator * linear_fog_fade(vertexDistance, FogStart, FogEnd);
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
}

/*
#version 150

#moj_import <fog.glsl>

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;

in float vertexDistance;
in vec4 vertexColor;

out vec4 fragColor;


*/

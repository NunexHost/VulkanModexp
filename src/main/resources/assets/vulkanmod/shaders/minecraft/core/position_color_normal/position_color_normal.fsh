//#version 150
//
//#moj_import <fog.glsl>
//
//uniform vec4 ColorModulator;
//uniform float FogStart;
//uniform float FogEnd;
//uniform vec4 FogColor;
//
//in float vertexDistance;
//in vec4 vertexColor;
//in vec4 normal;
//
//out vec4 fragColor;
//
//void main() {
//    vec4 color = vertexColor * ColorModulator;
//    if (color.a < 0.1) {
//        discard;
//    }
//    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
//}

#version 450

#include <fog.glsl>

layout(binding = 2) uniform sampler2D Sampler0;

layout(binding = 1) uniform UBO{
    vec4 ColorModulator;
    float FogStart;
    float FogEnd;
    vec4 FogColor;
};

layout(location = 0) in float vertexDistance;
layout(location = 1) in vec4 vertexColor;
<<<<<<< HEAD
=======
layout(location = 2) in vec4 normal;
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74

layout(location = 0) out vec4 fragColor;

void main() {
<<<<<<< HEAD
    vec4 color = vertexColor;
=======
    vec4 color = vertexColor * ColorModulator;
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
    if (color.a < 0.1) {
        discard;
    }
    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}
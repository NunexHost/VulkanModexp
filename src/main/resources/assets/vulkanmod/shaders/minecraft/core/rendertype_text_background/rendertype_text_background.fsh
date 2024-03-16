#version 450

vec4 linear_fog(vec4 inColor, float vertexDistance, float fogStart, float fogEnd, vec4 fogColor) {
    if (vertexDistance <= fogStart) {
        return inColor;
    }

    float fogValue = vertexDistance < fogEnd ? smoothstep(fogStart, fogEnd, vertexDistance) : 1.0;
    return vec4(mix(inColor.rgb, fogColor.rgb, fogValue * fogColor.a), inColor.a);
}

layout(binding = 1) uniform UBO{
<<<<<<< HEAD
=======
    vec4 ColorModulator;
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
    vec4 FogColor;
    float FogStart;
    float FogEnd;
};

layout(location = 0) in vec4 vertexColor;
<<<<<<< HEAD
layout(location = 1) in float vertexDistance;
=======
layout(location = 1) in vec2 texCoord0;
layout(location = 2) in float vertexDistance;
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

//#version 150
//
//#moj_import <fog.glsl>
//
//uniform sampler2D Sampler0;
//
//uniform vec4 ColorModulator;
//uniform float FogStart;
//uniform float FogEnd;
//uniform vec4 FogColor;
//
//in float vertexDistance;
//in vec4 vertexColor;
//in vec2 texCoord0;
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

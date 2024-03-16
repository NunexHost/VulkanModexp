#version 450
<<<<<<< HEAD
layout (constant_id = 0) const bool USE_FOG = true;
=======

>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
vec4 linear_fog(vec4 inColor, float vertexDistance, float fogStart, float fogEnd, vec4 fogColor) {
    if (vertexDistance <= fogStart) {
        return inColor;
    }

    float fogValue = vertexDistance < fogEnd ? smoothstep(fogStart, fogEnd, vertexDistance) : 1.0;
    return vec4(mix(inColor.rgb, fogColor.rgb, fogValue * fogColor.a), inColor.a);
}

layout(binding = 2) uniform sampler2D Sampler0;

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
layout(location = 1) in vec2 texCoord0;
<<<<<<< HEAD
layout(location = 2) in float vertexDistance;
=======
layout(location = 2) in vec2 texCoord1;
layout(location = 3) in vec3 normal;
layout(location = 4) in float vertexDistance;
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74

layout(location = 0) out vec4 fragColor;

void main() {
<<<<<<< HEAD
    vec4 color = texture(Sampler0, texCoord0) * vertexColor;
    if (color.a < 0.1) {
        discard;
    }
    fragColor = USE_FOG ? linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor) : color;
=======
    vec4 color = texture(Sampler0, texCoord0) * vertexColor * ColorModulator;
    if (color.a < 0.1) {
        discard;
    }
    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
}

/*
#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in float vertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;
in vec2 texCoord1;
in vec4 normal;

out vec4 fragColor;
*/


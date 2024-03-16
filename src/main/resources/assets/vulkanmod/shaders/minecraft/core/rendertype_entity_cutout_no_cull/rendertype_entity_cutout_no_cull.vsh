#version 450
<<<<<<< HEAD
layout (constant_id = 0) const bool USE_FOG = true;
=======

>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
#include "light.glsl"

layout(location = 0) in vec3 Position;
layout(location = 1) in vec4 Color;
layout(location = 2) in vec2 UV0;
layout(location = 3) in ivec2 UV1;
layout(location = 4) in ivec2 UV2;
layout(location = 5) in vec3 Normal;

layout(binding = 0) uniform UniformBufferObject {
   mat4 MVP;
   mat4 ModelViewMat;
   vec3 Light0_Direction;
   vec3 Light1_Direction;
};

layout(binding = 3) uniform sampler2D Sampler1;
layout(binding = 4) uniform sampler2D Sampler2;

layout(location = 0) out vec4 vertexColor;
layout(location = 1) out vec4 lightMapColor;
layout(location = 2) out vec4 overlayColor;
layout(location = 3) out vec2 texCoord0;
<<<<<<< HEAD
layout(location = 4) out float vertexDistance;
=======
layout(location = 4) out vec3 normal;
layout(location = 5) out float vertexDistance;
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74

void main() {
    gl_Position = MVP * vec4(Position, 1.0);

<<<<<<< HEAD
    vertexDistance = USE_FOG ? length((ModelViewMat * vec4(Position, 1.0)).xyz) : 0.0f;
=======
    vertexDistance = length((ModelViewMat * vec4(Position, 1.0)).xyz);
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
    vertexColor = minecraft_mix_light(Light0_Direction, Light1_Direction, Normal, Color);
    lightMapColor = texelFetch(Sampler2, UV2 / 16, 0);
    overlayColor = texelFetch(Sampler1, UV1, 0);
    texCoord0 = UV0;
<<<<<<< HEAD
    //normal = (MVP * vec4(Normal, 0.0)).xyz;
}
=======
    normal = (MVP * vec4(Normal, 0.0)).xyz;
}
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74

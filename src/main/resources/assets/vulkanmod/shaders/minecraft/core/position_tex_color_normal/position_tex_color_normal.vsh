#version 450
<<<<<<< HEAD
layout (constant_id = 0) const bool USE_FOG = true;
layout(location = 0) in vec3 Position;
layout(location = 1) in vec2 UV0;
layout(location = 2) in vec4 Color;

=======

layout(location = 0) in vec3 Position;
layout(location = 1) in vec2 UV0;
layout(location = 2) in vec4 Color;
layout(location = 3) in vec3 Normal;
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74

layout(binding = 0) uniform UniformBufferObject {
   mat4 MVP;
   mat4 ModelViewMat;
};

layout(location = 0) out vec4 vertexColor;
layout(location = 1) out vec2 texCoord0;
layout(location = 2) out float vertexDistance;
<<<<<<< HEAD
=======
layout(location = 3) out vec3 normal;
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74

void main() {
    gl_Position = MVP * vec4(Position, 1.0);

    texCoord0 = UV0;
<<<<<<< HEAD
    vertexDistance = USE_FOG ? length((ModelViewMat * vec4(Position, 1.0)).xyz) : 0.0f;
=======
    vertexDistance = length((ModelViewMat * vec4(Position, 1.0)).xyz);
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
    vertexColor = Color;
    //normal = (MVP * vec4(Normal, 0.0)).xyz;
}



#version 450

layout(location = 0) in vec3 Position;
layout(location = 1) in vec4 Color;
layout(location = 2) in vec3 Normal;

layout(binding = 0) uniform UniformBufferObject {
    mat4 MVP;
    mat4 ModelViewMat;
};

layout(location = 0) out float vertexDistance;
layout(location = 1) out vec4 vertexColor;
<<<<<<< HEAD
=======
layout(location = 2) out vec4 normal;
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74

void main() {
    gl_Position = MVP * vec4(Position, 1.0);

    vertexDistance = length((ModelViewMat * vec4(Position, 1.0)).xyz);
    vertexColor = Color;
<<<<<<< HEAD
    //normal = MVP * vec4(Normal, 0.0);
=======
    normal = MVP * vec4(Normal, 0.0);
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
}

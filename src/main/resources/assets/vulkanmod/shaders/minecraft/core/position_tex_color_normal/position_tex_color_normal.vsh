#version 450
layout (constant_id = 0) const bool USE_FOG = true;
layout(location = 0) in vec3 Position;
layout(location = 1) in vec2 UV0;
layout(location = 2) in vec4 Color;


layout(binding = 0) uniform UniformBufferObject {
   mat4 MVP;
   mat4 ModelViewMat;
};

layout(location = 0) out vec4 vertexColor;
layout(location = 1) out vec2 texCoord0;
layout(location = 2) out float vertexDistance;

void main() {
    gl_Position = MVP * vec4(Position, 1.0);

    texCoord0 = UV0;
    vertexDistance = USE_FOG ? length((ModelViewMat * vec4(Position, 1.0)).xyz) : 0.0f;
    vertexColor = Color;
    //normal = (MVP * vec4(Normal, 0.0)).xyz;
}



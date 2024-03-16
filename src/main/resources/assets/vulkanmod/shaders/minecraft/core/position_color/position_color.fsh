#version 450

layout(location = 0) in vec4 vertexColor;

<<<<<<< HEAD
=======
layout(binding = 1) uniform UBO{
    vec4 ColorModulator;
};
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74

layout(location = 0) out vec4 fragColor;

void main() {
    vec4 color = vertexColor;
    if (color.a == 0.0) {
        discard;
    }
<<<<<<< HEAD
    fragColor = color;
=======
    fragColor = color * ColorModulator;
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
}

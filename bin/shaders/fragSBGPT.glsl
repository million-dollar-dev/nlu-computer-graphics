#version 430
in vec2 fragTexCoord;
out vec4 color;

uniform sampler2D skybox;

void main(void) {
    color = texture(skybox, fragTexCoord);
}

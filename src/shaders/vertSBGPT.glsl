#version 430
layout(location=0) in vec3 vPosition;
layout(location=1) in vec2 texCoord;

out vec2 fragTexCoord;

uniform mat4 mv_matrix;
uniform mat4 p_matrix;

void main(void) {
    fragTexCoord = texCoord;
    gl_Position = p_matrix * mv_matrix * vec4(vPosition, 1.0);
}

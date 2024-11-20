#version 430

// Đầu vào từ ứng dụng
layout(location = 0) in vec3 vertPos;     // Vị trí đỉnh
layout(location = 1) in vec3 vertNormal;  // Vector pháp tuyến
layout(location = 2) in vec2 texCoord;    // Tọa độ UV

// Đầu ra tới fragment shader
out vec2 tc;                 // Tọa độ UV cho hiệu ứng bàn cờ
out vec3 varyingNormal;      // Vector pháp tuyến sau khi biến đổi
out vec3 varyingLightDir;    // Vector hướng ánh sáng
out vec3 varyingVertPos;     // Vị trí đỉnh trong không gian thế giới
out vec3 varyingViewDir;     // Vector hướng từ camera tới đỉnh

// Cấu trúc ánh sáng
struct PositionalLight {
    vec4 ambient;  
    vec4 diffuse;  
    vec4 specular;  
    vec3 position;
};

// Uniforms
uniform mat4 m_matrix;       // Ma trận mô hình
uniform mat4 v_matrix;       // Ma trận view
uniform mat4 p_matrix;       // Ma trận chiếu
uniform mat4 norm_matrix;    // Ma trận chuyển đổi vector pháp tuyến
uniform PositionalLight light;

// Main function
void main(void) {
    // Chuyển đổi vị trí đỉnh sang không gian thế giới
    vec4 worldPos = m_matrix * vec4(vertPos, 1.0);
    varyingVertPos = worldPos.xyz;

    // Vector hướng ánh sáng (từ nguồn sáng đến đỉnh)
    varyingLightDir = normalize(light.position - varyingVertPos);

    // Vector pháp tuyến sau khi biến đổi
    varyingNormal = normalize((norm_matrix * vec4(vertNormal, 0.0)).xyz);

    // Vector hướng từ camera tới đỉnh
    varyingViewDir = normalize(-v_matrix[3].xyz - varyingVertPos);

    // Gán tọa độ UV cho hiệu ứng bàn cờ
    tc = texCoord;

    // Chuyển đổi vị trí đỉnh sang không gian clip
    gl_Position = p_matrix * v_matrix * worldPos;
}

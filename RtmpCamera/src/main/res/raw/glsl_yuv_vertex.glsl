attribute vec4 vPosition;
attribute vec2 fPosition;
uniform mat4 vMatrix;

varying vec2 texPosition;

void main() {
    gl_Position = vMatrix * vPosition;
    texPosition = fPosition;
}
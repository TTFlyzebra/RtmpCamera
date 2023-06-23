#extension GL_OES_EGL_image_external : require

precision mediump float;

varying vec2 texPosition;
uniform samplerExternalOES vTexture;
void main() {
    gl_FragColor = texture2D(vTexture, texPosition);
}
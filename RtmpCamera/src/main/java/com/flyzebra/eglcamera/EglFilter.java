/**
 * FileName: CameraFilter
 * Author: FlyZebra
 * Email:flycnzebra@gmail.com
 * Date: 2023/6/23 8:55
 * Description:
 */
package com.flyzebra.eglcamera;

import android.content.Context;
import android.opengl.GLES11Ext;
import android.opengl.GLES30;
import android.opengl.Matrix;

import com.flyzebra.camera.R;
import com.flyzebra.utils.GLShaderUtils;
import com.flyzebra.utils.MatrixUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public class EglFilter {
    private Context mContext;
    protected int glProgram;
    protected int vPosition;
    protected int fPosition;
    protected int vMatrix;
    protected FloatBuffer vertexBuffer;
    protected FloatBuffer textureBuffer;
    public final float[] OM = MatrixUtils.getOriginalMatrix();
    protected float[] vMatrixBase = new float[]{
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1
    };
    private float[] vMatrixData = Arrays.copyOf(OM, 16);
    private int mTextureId = 0;

    private AtomicBoolean isMirror = new AtomicBoolean(true);

    public EglFilter(Context context) {
        mContext = context;
    }

    public void setTextureId(int textureId) {
        mTextureId = textureId;
    }

    public void onCreated() {
        float[] vertexData = {
                -1.0f, +1.0f, 0.0f,
                -1.0f, -1.0f, 0.0f,
                +1.0f, +1.0f, 0.0f,
                +1.0f, -1.0f, 0.0f,
        };
        vertexBuffer = ByteBuffer.allocateDirect(vertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
        vertexBuffer.position(0);
        float[] textureData = {   // in counterclockwise order:
                1.0f, 1.0f,
                0.0f, 1.0f,
                1.0f, 0.0f,
                0.0f, 0.0f,
        };
        textureBuffer = ByteBuffer.allocateDirect(textureData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(textureData);
        textureBuffer.position(0);

        glProgram = GLShaderUtils.createProgram(
                GLShaderUtils.readRawTextFile(mContext, R.raw.glsl_oes_vertex),
                GLShaderUtils.readRawTextFile(mContext, R.raw.glsl_oes_fagment));
        vPosition = GLES30.glGetAttribLocation(glProgram, "vPosition");
        fPosition = GLES30.glGetAttribLocation(glProgram, "fPosition");
        vMatrix = GLES30.glGetUniformLocation(glProgram, "vMatrix");
    }

    public void onDraw() {
        GLES30.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);

        System.arraycopy(vMatrixBase, 0, vMatrixData, 0, vMatrixBase.length);

        isMirror.set(EglCamera.cameraID.endsWith("0"));
        if (isMirror.get()) Matrix.scaleM(vMatrixData, 0, 1f, -1f, 1f);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTextureId);

        GLES30.glUseProgram(glProgram);
        GLES30.glUniformMatrix4fv(vMatrix, 1, false, vMatrixData, 0);

        GLES30.glEnableVertexAttribArray(vPosition);
        GLES30.glVertexAttribPointer(vPosition, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer);
        GLES30.glEnableVertexAttribArray(fPosition);
        GLES30.glVertexAttribPointer(fPosition, 2, GLES30.GL_FLOAT, false, 0, textureBuffer);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4);
        GLES30.glDisableVertexAttribArray(vPosition);
        GLES30.glDisableVertexAttribArray(fPosition);
    }

    public void onDestory() {

    }

}

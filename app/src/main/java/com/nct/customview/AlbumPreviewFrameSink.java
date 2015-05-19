package com.nct.customview;

import java.nio.ByteBuffer;
import java.util.concurrent.locks.ReentrantLock;

public interface AlbumPreviewFrameSink {
	void setNextFrame(ByteBuffer buf);
	ReentrantLock getFrameLock();
	void setPreviewFrameSize(int textureSize, int realWidth, int realHeight);
	public void setMode(int pMode);
}
package com.uniquext.android.application;

import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.nio.ByteBuffer;

import static android.media.MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible;

/**
 * 　 　　   へ　　　 　／|
 * 　　    /＼7　　　 ∠＿/
 * 　     /　│　　 ／　／
 * 　    │　Z ＿,＜　／　　   /`ヽ
 * 　    │　　　 　　ヽ　    /　　〉
 * 　     Y　　　　　   `　  /　　/
 * 　    ｲ●　､　●　　⊂⊃〈　　/
 * 　    ()　 へ　　　　|　＼〈
 * 　　    >ｰ ､_　 ィ　 │ ／／      去吧！
 * 　     / へ　　 /　ﾉ＜| ＼＼        比卡丘~
 * 　     ヽ_ﾉ　　(_／　 │／／           消灭代码BUG
 * 　　    7　　　　　　　|／
 * 　　    ＞―r￣￣`ｰ―＿
 * ━━━━━━━━━━感觉萌萌哒━━━━━━━━━━
 *
 * @author uniquext
 * @description $
 * @date 2021-03-08  18:06
 */
public class ClipUtils {
//
//    public void test() {
//        MediaExtractor extractor = null;
//        MediaCodec codec = null;
//        try {
//            extractor = new MediaExtractor();
//            extractor.setDataSource(fileName);
//            int trackCount = extractor.getTrackCount();
//            MediaFormat videoFormat = null;
//            for (int i = 0; i < trackCount; i++) {
//                MediaFormat trackFormat = extractor.getTrackFormat(i);
//                if (trackFormat.getString(MediaFormat.KEY_MIME).contains("video")) {
//                    videoFormat = trackFormat;
//                    extractor.selectTrack(i);
//                    break;
//                }
//            }
//            if (videoFormat == null) {
//                Log.d(TAG, "Can not get video format");
//                return;
//            }
//
//            int imageFormat = ImageFormat.YUV_420_888;
//            int colorFormat = COLOR_FormatYUV420Flexible;
//            videoFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, colorFormat);
//            videoFormat.setInteger(MediaFormat.KEY_WIDTH, videoFormat.getInteger(MediaFormat.KEY_WIDTH) / 4);
//            videoFormat.setInteger(MediaFormat.KEY_HEIGHT, videoFormat.getInteger(MediaFormat.KEY_HEIGHT) / 4);
//
//            long duration = videoFormat.getLong(MediaFormat.KEY_DURATION);
//
//            codec = MediaCodec.createDecoderByType(videoFormat.getString(MediaFormat.KEY_MIME));
//            ImageReader imageReader = ImageReader
//                    .newInstance(
//                            videoFormat.getInteger(MediaFormat.KEY_WIDTH),
//                            videoFormat.getInteger(MediaFormat.KEY_HEIGHT),
//                            imageFormat,
//                            3);
//            final ImageReaderHandlerThread imageReaderHandlerThread = new ImageReaderHandlerThread();
//
//            imageReader.setOnImageAvailableListener(new MyOnImageAvailableListener(callBack), imageReaderHandlerThread.getHandler());
//            codec.configure(videoFormat, imageReader.getSurface(), null, 0);
//            codec.start();
//            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
//            long timeOut = 5 * 1000;//10ms
//            boolean inputDone = false;
//            boolean outputDone = false;
//            ByteBuffer[] inputBuffers = null;
//            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
//                inputBuffers = codec.getInputBuffers();
//            }
//            //开始进行解码。
//            int count = 1;
//            while (!outputDone) {
//                if (requestStop) {
//                    return;
//                }
//                if (!inputDone) {
//                    //feed data
//                    int inputBufferIndex = codec.dequeueInputBuffer(timeOut);
//                    if (inputBufferIndex >= 0) {
//                        ByteBuffer inputBuffer;
//                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//                            inputBuffer = codec.getInputBuffer(inputBufferIndex);
//                        } else {
//                            inputBuffer = inputBuffers[inputBufferIndex];
//                        }
//                        int sampleData = extractor.readSampleData(inputBuffer, 0);
//                        if (sampleData > 0) {
//                            long sampleTime = extractor.getSampleTime();
//                            codec.queueInputBuffer(inputBufferIndex, 0, sampleData, sampleTime, 0);
//                            //继续
//                            if (interval == 0) {
//                                extractor.advance();
//                            } else {
//                                extractor.seekTo(count * interval * 1000, MediaExtractor.SEEK_TO_PREVIOUS_SYNC);
//                                count++;
////                                        extractor.advance();
//                            }
//                        } else {
//                            //小于0，说明读完了
//                            codec.queueInputBuffer(inputBufferIndex, 0, 0, 0L, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
//                            inputDone = true;
//                            Log.d(TAG, "end of stream");
//                        }
//                    }
//                }
//                if (!outputDone) {
//                    //get data
//                    int status = codec.dequeueOutputBuffer(bufferInfo, timeOut);
//                    if (status ==
//                            MediaCodec.INFO_TRY_AGAIN_LATER) {
//                        //继续
//                    } else if (status == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
//                        //开始进行解码
//                    } else if (status == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
//                        //同样啥都不做
//                    } else {
//                        //在这里判断，当前编码器的状态
//                        if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
//                            Log.d(TAG, "output EOS");
//                            outputDone = true;
//                        }
//                        boolean doRender = (bufferInfo.size != 0);
//                        long presentationTimeUs = bufferInfo.presentationTimeUs;
//                        if (lastPresentationTimeUs == 0) {
//                            lastPresentationTimeUs = presentationTimeUs;
//                        } else {
//                            long diff = presentationTimeUs - lastPresentationTimeUs;
//                            if (interval != 0) {
//                                if (diff < interval * 1000) {
//                                    doRender = false;
//                                } else {
//                                    lastPresentationTimeUs = presentationTimeUs;
//                                }
//                                Log.d(TAG,
//                                        "diff time in ms =" + diff / 1000);
//                            }
//                        }
//                        //有数据了.因为会直接传递给Surface，所以说明都不做好了
//                        Log.d(TAG, "surface decoder given buffer " + status +
//                                " (size=" + bufferInfo.size + ")" + ",doRender = " + doRender + ", presentationTimeUs=" + presentationTimeUs);
//                        //直接送显就可以了
//                        codec.releaseOutputBuffer(status, doRender);
//                    }
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (codec != null) {
//                codec.stop();
//                codec.release();
//            }
//            if (extractor != null) {
//                extractor.release();
//            }
//        }
//    }
//
//    private String TAG = "TAG";
//
//    private class MyOnImageAvailableListener implements ImageReader.OnImageAvailableListener {
//        private final BitmapCallBack callBack;
//
//        private MyOnImageAvailableListener(BitmapCallBack callBack) {
//            this.callBack = callBack;
//        }
//
//        @Override
//        public void onImageAvailable(ImageReader reader) {
//            Log.i(TAG, "in OnImageAvailable");
//            Image img = null;
//            try {
//                img = reader.acquireLatestImage();
//                if (img != null) {
//                    //这里得到的YUV的数据。需要将YUV的数据变成Bitmap
//                    Image.Plane[] planes = img.getPlanes();
//                    if (planes[0].getBuffer() == null) {
//                        return;
//                    }
//
////                    Bitmap bitmap = getBitmap(img);
//                    Bitmap bitmap = getBitmapScale(img, 8);
////                    Bitmap bitmap = getBitmapFromNv21(img);
//                    if (callBack != null && bitmap != null) {
//                        Log.d(TAG, "onComplete bitmap ");
//                        callBack.onComplete(bitmap);
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                if (img != null) {
//                    img.close();
//                }
//            }
//
//        }
//
//        @NonNull
//        private Bitmap getBitmapScale(Image img, int scale) {
//            int width = img.getWidth() / scale;
//            int height = img.getHeight() / scale;
//            final byte[] bytesImage = getDataFromYUV420Scale(img, scale);
//            Bitmap bitmap = null;
//            bitmap = Bitmap.createBitmap(height, width, Bitmap.Config.ARGB_8888);
//            bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(bytesImage));
//            return bitmap;
//        }
//
//        private byte[] getDataFromYUV420Scale(Image image, int scale) {
//            int width = image.getWidth();
//            int height = image.getHeight();
//            // Read image data
//            Image.Plane[] planes = image.getPlanes();
//
//            byte[] argb = new byte[width / scale * height / scale * 4];
//
//            //值得注意的是在Java层传入byte[]以RGBA顺序排列时，libyuv是用ABGR来表示这个排列
//            //libyuv表示的排列顺序和Bitmap的RGBA表示的顺序是反向的。
//            // 所以实际要调用libyuv::ABGRToI420才能得到正确的结果。
//            YuvUtils.yuvI420ToABGRWithScale(
//                    argb,
//                    planes[0].getBuffer(), planes[0].getRowStride(),
//                    planes[1].getBuffer(), planes[1].getRowStride(),
//                    planes[2].getBuffer(), planes[2].getRowStride(),
//                    width, height,
//                    scale
//            );
//            return argb;
//        }
//    }

}

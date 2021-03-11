package com.uniquext.android.videotrimmer;

import android.os.Parcel;
import android.os.Parcelable;

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
 * @date 2021-03-10  21:05
 */
public class VideoInfo implements Parcelable {


    public static final Creator<VideoInfo> CREATOR = new Creator<VideoInfo>() {
        @Override
        public VideoInfo createFromParcel(Parcel in) {
            return new VideoInfo(in);
        }

        @Override
        public VideoInfo[] newArray(int size) {
            return new VideoInfo[size];
        }
    };
    public final String mimeType;
    public final long size;
    public final long duration;
    public final String name;
    public final String path;


    public VideoInfo(String name, String path, String mimeType, long duration, long size) {
        this.name = name.split("\\.")[0];
        this.path = path;
        this.mimeType = mimeType;
        this.duration = duration;
        this.size = size;
    }

    protected VideoInfo(Parcel in) {
        name = in.readString();
        path = in.readString();
        mimeType = in.readString();
        duration = in.readLong();
        size = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(path);
        dest.writeString(mimeType);
        dest.writeLong(duration);
        dest.writeLong(size);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "VideoInfo{" +
                "mimeType='" + mimeType + '\'' +
                ", size=" + size +
                ", duration=" + duration +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}

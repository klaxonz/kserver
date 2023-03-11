package com.klaxon.kserver.entity.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 网页视频存储任务表
 * @TableName t_task
 */
@TableName(value ="t_task")
@Data
public class Task implements Serializable {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Object id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 网页id
     */
    private Integer webpageId;

    /**
     * 视频下载进度
     */
    private Integer videoProgress;

    /**
     * 音频下载进度
     */
    private Integer audioProgress;

    /**
     * 视频总长度
     */
    private Integer videoLength;

    /**
     * 音频总长度
     */
    private Integer audioLength;

    /**
     * 视频已下载长度
     */
    private Integer videoDownloadedLength;

    /**
     * 音频已下载长度
     */
    private Integer audioDownloadedLength;

    /**
     * 视频下载路径
     */
    private String videoPath;

    /**
     * 音频下载路径
     */
    private String audioPath;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        Task other = (Task) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getWebpageId() == null ? other.getWebpageId() == null : this.getWebpageId().equals(other.getWebpageId()))
            && (this.getVideoProgress() == null ? other.getVideoProgress() == null : this.getVideoProgress().equals(other.getVideoProgress()))
            && (this.getAudioProgress() == null ? other.getAudioProgress() == null : this.getAudioProgress().equals(other.getAudioProgress()))
            && (this.getVideoLength() == null ? other.getVideoLength() == null : this.getVideoLength().equals(other.getVideoLength()))
            && (this.getAudioLength() == null ? other.getAudioLength() == null : this.getAudioLength().equals(other.getAudioLength()))
            && (this.getVideoDownloadedLength() == null ? other.getVideoDownloadedLength() == null : this.getVideoDownloadedLength().equals(other.getVideoDownloadedLength()))
            && (this.getAudioDownloadedLength() == null ? other.getAudioDownloadedLength() == null : this.getAudioDownloadedLength().equals(other.getAudioDownloadedLength()))
            && (this.getVideoPath() == null ? other.getVideoPath() == null : this.getVideoPath().equals(other.getVideoPath()))
            && (this.getAudioPath() == null ? other.getAudioPath() == null : this.getAudioPath().equals(other.getAudioPath()))
            && (this.getFilePath() == null ? other.getFilePath() == null : this.getFilePath().equals(other.getFilePath()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getWebpageId() == null) ? 0 : getWebpageId().hashCode());
        result = prime * result + ((getVideoProgress() == null) ? 0 : getVideoProgress().hashCode());
        result = prime * result + ((getAudioProgress() == null) ? 0 : getAudioProgress().hashCode());
        result = prime * result + ((getVideoLength() == null) ? 0 : getVideoLength().hashCode());
        result = prime * result + ((getAudioLength() == null) ? 0 : getAudioLength().hashCode());
        result = prime * result + ((getVideoDownloadedLength() == null) ? 0 : getVideoDownloadedLength().hashCode());
        result = prime * result + ((getAudioDownloadedLength() == null) ? 0 : getAudioDownloadedLength().hashCode());
        result = prime * result + ((getVideoPath() == null) ? 0 : getVideoPath().hashCode());
        result = prime * result + ((getAudioPath() == null) ? 0 : getAudioPath().hashCode());
        result = prime * result + ((getFilePath() == null) ? 0 : getFilePath().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", userId=").append(userId);
        sb.append(", webpageId=").append(webpageId);
        sb.append(", videoProgress=").append(videoProgress);
        sb.append(", audioProgress=").append(audioProgress);
        sb.append(", videoLength=").append(videoLength);
        sb.append(", audioLength=").append(audioLength);
        sb.append(", videoDownloadedLength=").append(videoDownloadedLength);
        sb.append(", audioDownloadedLength=").append(audioDownloadedLength);
        sb.append(", videoPath=").append(videoPath);
        sb.append(", audioPath=").append(audioPath);
        sb.append(", filePath=").append(filePath);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
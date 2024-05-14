package com.example.prashantadvait_foundation.network;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseModel {

        @SerializedName("id")
        private String id;

        @SerializedName("title")
        private String title;

        @SerializedName("language")
        private String language;

        @SerializedName("thumbnail")
        private Thumbnail thumbnail;

        @SerializedName("mediaType")
        private int mediaType;

        @SerializedName("coverageURL")
        private String coverageURL;

        @SerializedName("publishedAt")
        private String publishedAt;

        @SerializedName("publishedBy")
        private String publishedBy;

        @SerializedName("backupDetails")
        private BackupDetails backupDetails;

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getLanguage() {
            return language;
        }

        public Thumbnail getThumbnail() {
            return thumbnail;
        }

        public int getMediaType() {
            return mediaType;
        }

        public String getCoverageURL() {
            return coverageURL;
        }

        public String getPublishedAt() {
            return publishedAt;
        }

        public String getPublishedBy() {
            return publishedBy;
        }

        public BackupDetails getBackupDetails() {
            return backupDetails;
        }

        public static class Thumbnail {
            @SerializedName("id")
            private String id;

            @SerializedName("version")
            private int version;

            @SerializedName("domain")
            private String domain;

            @SerializedName("basePath")
            private String basePath;

            @SerializedName("key")
            private String key;

            @SerializedName("qualities")
            private List<Integer> qualities;

            @SerializedName("aspectRatio")
            private double aspectRatio;

            public String getId() {
                return id;
            }

            public int getVersion() {
                return version;
            }

            public String getDomain() {
                return domain;
            }

            public String getBasePath() {
                return basePath;
            }

            public String getKey() {
                return key;
            }

            public List<Integer> getQualities() {
                return qualities;
            }

            public double getAspectRatio() {
                return aspectRatio;
            }
        }

        public static class BackupDetails {
            @SerializedName("pdfLink")
            private String pdfLink;

            @SerializedName("screenshotURL")
            private String screenshotURL;

            public String getPdfLink() {
                return pdfLink;
            }

            public String getScreenshotURL() {
                return screenshotURL;
            }
        }
    }


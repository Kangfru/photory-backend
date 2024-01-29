package com.ot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotNull;

@ConfigurationProperties(prefix = "info")
@Getter
@Setter
public class AppConfig {

    private String host;

    private TmApi tmApi;

    private Aws aws;

    private Upload upload;

    @Getter
    @Setter
    public static class TmApi {
        @NotNull
        private String host;

        // V3
        @NotNull
        private String apiKey;
        // V4
        @NotNull
        private String apiToken;
    }

    @Getter
    @Setter
    public static class Aws {
        @NotNull
        private String accessKey;

        @NotNull
        private String secretKey;

        private S3 s3;

        private Ses ses;

        @Getter
        @Setter
        public static class S3 {

            private String accessKey;

            private String secretKey;

            private String bucket;

            private String rootPath;

        }
        @Getter
        @Setter
        public static class Ses {
            private String accessKey;

            private String secretKey;
        }

    }

    @Getter
    @Setter
    public static class Upload {

        private String local;

    }

}

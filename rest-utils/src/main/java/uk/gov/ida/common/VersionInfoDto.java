package uk.gov.ida.common;

public class VersionInfoDto {


    private String buildNumber;
    private String gitCommit;
    private String createdDate;

    // Needed for JAXB
    private VersionInfoDto() {
    }

    public VersionInfoDto(String buildNumber, String gitCommit, String createdDate) {
        this.buildNumber = buildNumber;
        this.gitCommit = gitCommit;
        this.createdDate = createdDate;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public String getBuildNumber() {
        return buildNumber;
    }

    public String getGitCommit() {
        return gitCommit;
    }
}

package uk.gov.ida.cache;

public interface AssetCacheConfiguration {

    public boolean shouldCacheAssets();

    public String getAssetsCacheDuration();
}

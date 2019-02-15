package uk.gov.ida.cache;

public interface AssetCacheConfiguration {

    boolean shouldCacheAssets();

    String getAssetsCacheDuration();
}

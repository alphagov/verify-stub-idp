package uk.gov.ida.stub.idp.repositories.infinispan;

import uk.gov.ida.shared.dropwizard.infinispan.util.InfinispanCacheManager;

import javax.inject.Inject;

public class InfinispanUserRepository extends MapUserRepository {

    @Inject
    public InfinispanUserRepository(InfinispanCacheManager infinispanCacheManager) {
        super(infinispanCacheManager.getCache("stub_idp_users"));
    }

}

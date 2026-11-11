package com.enchantea.application.dlv;

import com.enchantea.domain.dlv.Handoff;
import com.enchantea.foundation.dlv.PersistedHandoff;
import com.enchantea.foundation.dlv.PersistedHandoffRepository;
import org.springframework.stereotype.Repository;

@Repository
public class JpaHandoffRepository implements HandoffRepository {
    private final PersistedHandoffRepository HANDOFFS;

    public JpaHandoffRepository(PersistedHandoffRepository handoffs) {
        this.HANDOFFS = handoffs;
    }

    @Override
    public Handoff save(Handoff handoff) {
        HANDOFFS.save(new PersistedHandoff(
                handoff.getOrderId(),
                handoff.getCourierId(),
                handoff.getType(),
                handoff.getTimestamp()));
        return handoff;
    }
}

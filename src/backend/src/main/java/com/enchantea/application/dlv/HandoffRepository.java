package com.enchantea.application.dlv;

import com.enchantea.domain.dlv.Handoff;

public interface HandoffRepository {
    Handoff save(Handoff handoff);
}

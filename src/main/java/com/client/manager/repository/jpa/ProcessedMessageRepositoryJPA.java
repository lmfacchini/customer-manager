package com.client.manager.repository.jpa;

import com.client.manager.repository.ProcessedMessageRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ProcessedMessageRepositoryJPA implements ProcessedMessageRepository{
    private final EntityManager entityManager;


    @Override
    public boolean registerIfAbsent(UUID eventId, String eventType) {
        int insertedRows = entityManager
                .createNativeQuery("""
                INSERT INTO prcssd_mssgs (
                    event_id,
                    evnt_tpe,
                )
                VALUES (
                    :evnt_id,
                    :eventType,
                )
                ON CONFLICT (event_id) DO NOTHING
                """)
                .setParameter("eventId", eventId)
                .setParameter("eventType", eventType)
                .executeUpdate();

        return insertedRows == 1;
    }
}

package uk.gov.ida.eventemitter;

import com.google.inject.Inject;

import java.util.EnumMap;

public class EventHasher {

    public static final String PID_REMOVED = "Plaintext pid has been removed.";

    private final Sha256Util sha256Util;

    @Inject
    public EventHasher(final Sha256Util sha256Util) {
        this.sha256Util = sha256Util;
    }

    public Event replacePersistentIdWithHashedPersistentId(final Event event) {
        final EnumMap<EventDetailsKey, String> details = event.getDetails();

        if (details != null) {
            final String pid = details.get(EventDetailsKey.pid);

            if (pid != null) {
                final String idpEntityId = details.get(EventDetailsKey.idp_entity_id);
                final EnumMap<EventDetailsKey, String> newDetails = new EnumMap<>(EventDetailsKey.class);

                if (idpEntityId == null) {
                    newDetails.put(EventDetailsKey.pid, PID_REMOVED);
                } else {
                    final String hashedPid = sha256Util.hash(idpEntityId, pid);
                    newDetails.put(EventDetailsKey.pid, hashedPid);
                }

                for (EventDetailsKey e : details.keySet()) {
                    if (e != EventDetailsKey.pid) {
                        newDetails.put(e, details.get(e));
                    }
                }

                return new EventMessage(
                    event.getEventId(),
                    event.getTimestamp(),
                    event.getEventType(),
                    event.getOriginatingService(),
                    event.getSessionId(),
                    newDetails);
            }
        }

        return event;
    }
}

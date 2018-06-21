package servletimpl;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.*;

public class SessionManager {
    private static SessionManager instance;
    private final ScheduledExecutorService scheduler;
    private final ScheduledFuture<?> cleanerHandle;
    private final int CLEAN_INTERVAL = 60; // seconds
    private final int SESSION_TIMEOUT = 10; // minutes
    private Map<String, HttpSessionImpl> sessions = new ConcurrentHashMap<String, HttpSessionImpl>();
    private SessionIdGenerator sessionIdGenerator;

    static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    synchronized HttpSessionImpl getSession(String id) {
        HttpSessionImpl ret = sessions.get(id);
        if (ret != null) {
            ret.access();
        }
        return ret;
    }

    HttpSessionImpl createSession() {
        String id = this.sessionIdGenerator.generateSessionId();
        HttpSessionImpl session = new HttpSessionImpl(id);
        sessions.put(id, session);
        return session;
    }

    private synchronized void cleanSessions() {
        for (Iterator<String> it = sessions.keySet().iterator();
             it.hasNext();) {
            String id = it.next();
            HttpSessionImpl session = this.sessions.get(id);
            if (session.getLastAccessedTime()
                    < (System.currentTimeMillis()
                    - (SESSION_TIMEOUT * 60 * 1000))) {
                it.remove();
            }
        }
    }

    private SessionManager() {
        scheduler = Executors.newSingleThreadScheduledExecutor();

        Runnable cleaner = new Runnable() {
            public void run() {
                cleanSessions();
            }
        };
        this.cleanerHandle
                = scheduler.scheduleWithFixedDelay(cleaner,
                CLEAN_INTERVAL, CLEAN_INTERVAL,
                TimeUnit.SECONDS);
        this.sessionIdGenerator = new SessionIdGenerator();
    }
}

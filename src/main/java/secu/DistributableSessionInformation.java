package secu;


import org.springframework.security.core.session.SessionInformation;
import org.springframework.util.Assert;
import java.io.Serializable;
import java.util.Date;

/**
 * Representa al registro de una sesión en un entorno distribuido
 * @see DistributableSessionRegistryImpl
 * @author altuure
 */
public class DistributableSessionInformation implements Serializable {
    //~ Instance fields ----------------------------------------------------------------------------

    private static final long serialVersionUID = -3847700859593297877L;
    private Date lastRequest;
    private Object principal;
    private String sessionId;
    private boolean expired = false;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Crea un nuevo objeto DistributableSessionInformation.
     *
     * @param principal requerido.
     * @param sessionId requerido.
     * @param lastRequest requerido.
     */
    public DistributableSessionInformation(Object principal, String sessionId, Date lastRequest) {
        this();
        Assert.notNull(principal, "Principal required");
        Assert.hasText(sessionId, "SessionId required");
        Assert.notNull(lastRequest, "LastRequest required");
        this.principal = principal;
        this.sessionId = sessionId;
        this.lastRequest = lastRequest;
    }
    /**
     * Crea un nuevo objeto DistributableSessionInformation.
     */
    public DistributableSessionInformation() {
        super();
    }
    /**
     * Crea un nuevo objeto DistributableSessionInformation.
     *
     * @param bSessionInformation requerido.
     * @param lastRequest requerido.
     */
    public DistributableSessionInformation(SessionInformation bSessionInformation, Date lastRequest) {
        this(bSessionInformation.getPrincipal(), bSessionInformation.getSessionId(), lastRequest);
    }
    /**
     * Crea un nuevo objeto DistributableSessionInformation.
     *
     * @param bSessionInformation requerido.
     * @param lastRequest requerido.
     * @param expired requerido.
     */
    public DistributableSessionInformation(SessionInformation bSessionInformation,
        Date lastRequest, boolean expired) {
        this(bSessionInformation.getPrincipal(), bSessionInformation.getSessionId(), lastRequest);
        this.expired = expired;
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DistributableSessionInformation other = (DistributableSessionInformation) obj;
        if (expired != other.expired) {
            return false;
        }
        if (lastRequest == null) {
            if (other.lastRequest != null) {
                return false;
            }
        } else if (!lastRequest.equals(other.lastRequest)) {
            return false;
        }
        if (principal == null) {
            if (other.principal != null) {
                return false;
            }
        } else if (!principal.equals(other.principal)) {
            return false;
        }
        if (sessionId == null) {
            if (other.sessionId != null) {
                return false;
            }
        } else if (!sessionId.equals(other.sessionId)) {
            return false;
        }
        return true;
    }
    /**
     * @return the lastRequest
     */
    public Date getLastRequest() {
        return lastRequest;
    }

    /**
     * @return the principal
     */
    public Object getPrincipal() {
        return principal;
    }

    /**
     * @return the sessionId
     */
    public String getSessionId() {
        return sessionId;
    }
    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = (PRIME * result) + (expired ? 1231 : 1237);
        result = (PRIME * result) + ((lastRequest == null) ? 0 : lastRequest.hashCode());
        result = (PRIME * result) + ((principal == null) ? 0 : principal.hashCode());
        result = (PRIME * result) + ((sessionId == null) ? 0 : sessionId.hashCode());
        return result;
    }
    /**
     * @return the expired
     */
    public boolean isExpired() {
        return expired;
    }
    /**
     * Devuelve un SessionInformation a partir de la informacion en el registro
     * @param sessionRegistry la instancia de sessionRegistry
     * @return SessionInformation
     */
    public SessionInformation sessionInformation(
        final DistributableSessionRegistryImpl sessionRegistry) {
        return new SessionInformation(principal, sessionId, getLastRequest()) {
                private static final long serialVersionUID = 1275099596254144857L;
                public void refreshLastRequest() {
                    super.refreshLastRequest();
                    sessionRegistry.refreshLastRequest(getSessionId());
                }
                public void expireNow() {
                    super.expireNow();
                    sessionRegistry.expireSession(getSessionId());
                }
                public boolean isExpired() {
                    return expired;
                }
            };
    }
}

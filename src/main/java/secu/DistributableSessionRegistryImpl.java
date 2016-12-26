package secu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.jgroups.JChannel;
import org.jgroups.blocks.ReplicatedHashMap;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.session.HttpSessionDestroyedEvent;
import org.springframework.util.Assert;

/**
 * Implementacion de SessionRegistry para un ambiente distribuido (cluster).
 * Mantiene instancias de DistributableSessionInformation. Modificado del
 * artículo de Mert Can Akkan Clustering Acegi via JGroups
 * (DistributedHashtable)
 * http://www.altuure.com/2007/12/23/clustering-acegi-via-jgroups-distributedhashtable/,
 * actualizado a spring security y ReplicatedHashMap
 */
public class DistributableSessionRegistryImpl implements SessionRegistry, ApplicationListener<SessionDestroyedEvent> {
	// ~ Static fields/initializers
	// -----------------------------------------------------------------

	private static final Logger logger = Logger.getLogger(DistributableSessionRegistryImpl.class);

	// ~ Instance fields
	// ----------------------------------------------------------------------------

	@SuppressWarnings("unchecked")
	private Map sessionIds = new HashMap(); // <sessionId:Object,DistributableSessionInformation>
	private String channelName = "security-cluster-patagonia";
	private String clusterOptions = "/tcp.xml";//"/udp.xml";// null;
	private boolean distributable = true;// false;
	// private int timeout = 5000;
	private int timeout = 0;

	// ~ Methods
	// ------------------------------------------------------------------------------------

	/**
	 * Destroy invocado por el container
	 * 
	 * @throws ChannelException
	 *             si ocurre un problema.
	 */
	@SuppressWarnings("unchecked")
	public void destroy() {
		System.out.println("DESTROY DE LA DISTRIBUTABLE SESSION");
		if (sessionIds instanceof ReplicatedHashMap) {
			ReplicatedHashMap o = (ReplicatedHashMap) sessionIds;
			o.getChannel().close();
			o.stop();
		}
	}

	/**
	 * Expires the session
	 * 
	 * @param sessionId
	 *            the session id
	 */
	@SuppressWarnings("unchecked")
	public void expireSession(String sessionId) {
		System.out.println("EXPIRE SESSION");
		Assert.hasText(sessionId, "SessionId required as per interface contract");

		SessionInformation info = getSessionInformation(sessionId);
		if (info != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("expiring session for sessionId:" + sessionId);
			}
			sessionIds.put(sessionId, new DistributableSessionInformation(info, new Date(), true));
		}
	}

	/**
	 * @see org.springframework.security.concurrent.SessionRegistry#getAllPrincipals()
	 */
	@SuppressWarnings("unchecked")
	public List<Object> getAllPrincipals() {
		System.out.println("GET ALL PRINCIPALS");
		Collection collection = sessionIds.values();
		List principals = new ArrayList();
		for (Iterator iterator = collection.iterator(); iterator.hasNext();) {
			DistributableSessionInformation sessionInformation = (DistributableSessionInformation) iterator.next();
			principals.add(sessionInformation.getPrincipal());
		}
		return principals;
	}

	/**
	 * @see org.springframework.security.concurrent.SessionRegistry#getAllSessions(java.lang.Object,
	 *      boolean)
	 */
	@SuppressWarnings("unchecked")
	public List<SessionInformation> getAllSessions(Object principal, boolean includeExpiredSessions) {
		System.out.println("GET ALLSESSIONS");
		Set sessionsUsedByPrincipal = getSessionIds(principal);

		List list = new ArrayList();

		synchronized (sessionsUsedByPrincipal) {
			for (Iterator iter = sessionsUsedByPrincipal.iterator(); iter.hasNext();) {
				String sessionId = (String) iter.next();
				SessionInformation sessionInformation = getSessionInformation(sessionId);

				if (sessionInformation == null) {
					continue;
				}

				if (includeExpiredSessions || !sessionInformation.isExpired()) {
					list.add(sessionInformation);
				}
			}
		}

		return list;
	}

	/**
	 * @see org.springframework.security.concurrent.SessionRegistry#getSessionInformation(java.lang.String)
	 */
	public SessionInformation getSessionInformation(String sessionId) {
		System.out.println("GET SESSION INFORMATION");
		Assert.hasText(sessionId, "SessionId required as per interface contract");

		DistributableSessionInformation sessionInformation = ((DistributableSessionInformation) sessionIds.get(sessionId));
		if (sessionInformation == null) {
			return null;
		}
		return sessionInformation.sessionInformation(this);
	}

	/**
	 * Init invocado por el container
	 * 
	 * @throws Exception
	 * 
	 * @throws ChannelException
	 *             si ocurre un problema.
	 */
	@SuppressWarnings("unchecked")
	public void init() throws Exception {
		System.out.println("INIT");
		if (!distributable) {
			return;
		}
		JChannel jChannel;
		ReplicatedHashMap hashtable;

		if (clusterOptions == null) {
			jChannel = new JChannel();
		} else {
			jChannel = new JChannel(Thread.currentThread().getContextClassLoader().getResource(clusterOptions));

		}

		Assert.hasText(channelName, "channelName debe ser seteado");
		hashtable = new ReplicatedHashMap(jChannel);
		jChannel.setName(channelName);
		jChannel.connect(channelName);
		hashtable.setTimeout(timeout);
		hashtable.setBlockingUpdates(true);

		hashtable.addNotifier(new RHMNotifier());
		sessionIds = hashtable;

		hashtable.start(timeout);

	}

	/**
	 * @see org.springframework.security.concurrent.SessionRegistry#refreshLastRequest(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public void refreshLastRequest(String sessionId) {
		System.out.println("REFRESH LAST REQUEST");
		Assert.hasText(sessionId, "SessionId required as per interface contract");

		SessionInformation info = getSessionInformation(sessionId);

		if (info != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("refreshing last request timestamp for sessionId: " + sessionId);
			}
			sessionIds.put(sessionId, new DistributableSessionInformation(info, new Date()));
		}
	}

	/**
	 * @see org.springframework.security.concurrent.SessionRegistry#registerNewSession(java.lang.String,
	 *      java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public void registerNewSession(String sessionId, Object principal) {
		System.out.println("REGISTER NEW SESSION");
		Assert.hasText(sessionId, "SessionId required as per interface contract");
		Assert.notNull(principal, "Principal required as per interface contract");

		if (logger.isDebugEnabled()) {
			logger.debug("registering new session sessionId: " + sessionId + " principal: " + principal);
		}

		if (getSessionInformation(sessionId) != null) {
			removeSessionInformation(sessionId);
		}

		sessionIds.put(sessionId, new DistributableSessionInformation(principal, sessionId, new Date()));
	}

	/**
	 * @see org.springframework.security.concurrent.SessionRegistry#removeSessionInformation(java.lang.String)
	 */
	public void removeSessionInformation(String sessionId) {
		System.out.println("REMOVE SESSION INFORMATION");
		Assert.hasText(sessionId, "SessionId required as per interface contract");

		SessionInformation info = getSessionInformation(sessionId);

		if (info != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("removing session information sessionId: " + sessionId);
			}
			sessionIds.remove(sessionId);
		}
	}

	/**
	 * @param channelName
	 *            the channelName to set
	 */
	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	/**
	 * @param clusterOptions
	 *            the clusterOptions to set
	 */
	public void setClusterOptions(String clusterOptions) {
		this.clusterOptions = clusterOptions;
	}

	/**
	 * @param distributable
	 *            the distributable to set
	 */
	public void setDistributable(boolean distributable) {
		this.distributable = distributable;
	}

	/**
	 * @param timeout
	 *            the timeout to set
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	@SuppressWarnings("unchecked")
	private Set getSessionIds(Object principal) {
		System.out.println("GET SESSION IDS");
		Collection collection = sessionIds.values();
		Set sessionIds = new HashSet();
		for (Iterator iterator = collection.iterator(); iterator.hasNext();) {
			DistributableSessionInformation sessionInformation = (DistributableSessionInformation) iterator.next();
			if (sessionInformation.getPrincipal().equals(principal)) {
				sessionIds.add(sessionInformation.getSessionId());
			}
		}
		return sessionIds;
	}

	@Override
	public void onApplicationEvent(SessionDestroyedEvent event) {
		System.out.println("ON APPLICATION EVENT");
		if (event instanceof HttpSessionDestroyedEvent) {
			String sessionId = ((HttpSession) event.getSource()).getId();
			removeSessionInformation(sessionId);
		}

	}
}

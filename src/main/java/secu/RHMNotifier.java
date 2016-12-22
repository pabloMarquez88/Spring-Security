package secu;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jgroups.View;
import org.jgroups.blocks.ReplicatedHashMap;

/**
 * @author pantusap
 *
 */
@SuppressWarnings("unchecked")
public class RHMNotifier implements ReplicatedHashMap.Notification {
	// ~ Static fields/initializers
	// -----------------------------------------------------------------

	private static final Logger logger = Logger.getLogger(RHMNotifier.class);

	// ~ Methods
	// ------------------------------------------------------------------------------------

	/**
	 * @see org.jgroups.blocks.ReplicatedHashMap.Notification#contentsCleared()
	 */
	public void contentsCleared() {
		System.out.println("A");
		if (logger.isDebugEnabled()) {
			logger.debug("contentsCleared");
		}

	}

	/**
	 * @see org.jgroups.blocks.ReplicatedHashMap.Notification#contentsSet(java.util.Map)
	 */
	public void contentsSet(Map map) {
		System.out.println("B");
		if (logger.isDebugEnabled()) {
			logger.debug("contentsSet: " + map);
		}

	}

	/**
	 * @see org.jgroups.blocks.ReplicatedHashMap.Notification#entrySet(java.io.Serializable,
	 *      java.io.Serializable)
	 */
	public void entrySet(Serializable key, Serializable value) {
		System.out.println("C");
		if (logger.isDebugEnabled()) {
			logger.debug("entrySet key: " + key + " value: " + value);
		}

	}

	/**
	 * @see org.jgroups.blocks.ReplicatedHashMap.Notification#entryRemoved(java.io.Serializable)
	 */
	public void entryRemoved(Object key) {
		System.out.println("D");
		if (logger.isDebugEnabled()) {
			logger.debug("entryRemoved key: " + key);
		}

	}

	public void entrySet(Object arg0, Object arg1) {
		System.out.println("E");
		if (logger.isDebugEnabled()) {
			logger.debug("entry set: " + arg0 + " " + arg1);
		}

	}

	/**
	 * @see org.jgroups.blocks.ReplicatedHashMap.Notification#viewChange(org.jgroups.View,
	 *      java.util.Vector, java.util.Vector)
	 */
	@Override
	public void viewChange(View view, List newMembers, List oldMembers) {
		System.out.println("F");
		if (logger.isDebugEnabled()) {
			logger.debug("viewChange view: " + view + "newMembers: " + newMembers + " oldMembers: " + oldMembers);
		}

	}

}

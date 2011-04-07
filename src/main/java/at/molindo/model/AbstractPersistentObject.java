package at.molindo.model;

import java.io.InvalidClassException;
import java.io.ObjectStreamException;
import java.io.Serializable;

public abstract class AbstractPersistentObject implements IPersistentObject, Serializable {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(AbstractPersistentObject.class);

	private static final long serialVersionUID = 1L;

	private static IEntityLoader LOADER;

	public static synchronized void setEntityLoader(final IEntityLoader loader) {
		LOADER = loader;
	}

	private static AbstractPersistentObject loadEntity(final Class<? extends AbstractPersistentObject> entity, final Serializable id) throws ObjectStreamException {
		if (LOADER != null) {
			if (LOADER instanceof IMappedEntityLoader
					&& !((IMappedEntityLoader) LOADER).isMapped(entity)) {
				return null;
			}
			return LOADER.loadEntity(entity, id);
		} else {
			log.error("no property loader available. Remember to call "
					+ AbstractPersistentObject.class
					+ ".setPropertyLoader(..) on application startup");
			return null;
		}
	}

	public Object writeReplace() throws ObjectStreamException {
		if (isTransient()) {
			return this;
		} else {
			Class<? extends AbstractPersistentObject> cls = getClass();
			return new ReplaceHolder(cls, getId());
		}
	}

	public boolean isTransient() {
		return isTransient(this);
	}

	public interface IEntityLoader {
		public AbstractPersistentObject loadEntity(final Class<? extends AbstractPersistentObject> entity, final Serializable id) throws ObjectStreamException;
	}

	public interface IMappedEntityLoader extends IEntityLoader {
		public boolean isMapped(Class<? extends IPersistentObject> entity);
	}

	public static final class ReplaceHolder implements Serializable, IPersistentObject {

		private static final long serialVersionUID = 1L;

		private final String _entityName;
		private final Serializable _id;

		public ReplaceHolder(final Class<? extends AbstractPersistentObject> entity, final Serializable id) {
			_entityName = entity.getName();
			_id = id;
		}

		public Serializable getId() {
			return _id;
		}

		@SuppressWarnings("unchecked")
		public Class<? extends AbstractPersistentObject> getEntity() throws ClassNotFoundException {
			return (Class<? extends AbstractPersistentObject>) Class.forName(_entityName);
		}

		public final Object readResolve() throws ObjectStreamException {
			try {
				return loadEntity(getEntity(), getId());
			} catch (final ClassNotFoundException e) {
				throw new InvalidClassException(_entityName, e.getClass().getSimpleName() + ": "
						+ e.getMessage());
			}
		}
	}

	public static boolean isTransient(final IPersistentObject obj) {
		if (obj == null || isTransientId(obj.getId())) {
			return true;
		}
		if (LOADER instanceof IMappedEntityLoader) {
			return !((IMappedEntityLoader) LOADER).isMapped(obj.getClass());
		}
		return false;
	}

	public static boolean isTransientId(final Serializable id) {
		if (id == null) {
			return true;
		}
		if ((id instanceof Integer || id instanceof Long) && ((Number) id).longValue() == 0L) {
			return true;
		}
		return false;
	}

	@Override
	public abstract int hashCode();

	@Override
	public abstract boolean equals(Object o);

}

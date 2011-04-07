package at.molindo.svc;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import at.molindo.model.IPersistentObject;
import at.molindo.svc.exception.PropertyException;

public interface IPersistentObjectService {

	void saveOrUpdateAll(Collection<? extends IPersistentObject> objects) throws PropertyException;

}

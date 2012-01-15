package model.manager.exports.xml;

import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.Expression;

public class EnumDelegate extends DefaultPersistenceDelegate {
	@SuppressWarnings("unchecked")
	@Override
	protected Expression instantiate(Object oldInstance, Encoder out) {
		Enum e = (Enum) oldInstance;
		return new Expression(Enum.class, "valueOf", new Object[] {
				e.getClass(), e.name() });
	}

	@Override
	protected boolean mutatesTo(Object oldInstance, Object newInstance) {
		return oldInstance == newInstance;
	}
}

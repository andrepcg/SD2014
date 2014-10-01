package Util;

public class Objecto {

	/**
	 * @uml.property  name="obj"
	 */
	private Object obj;

	public Objecto(Object obj) {
		this.obj = obj;
	}

	/**
	 * @return
	 * @uml.property  name="obj"
	 */
	public Object getObj() {
		return obj;
	}

	/**
	 * @param obj
	 * @uml.property  name="obj"
	 */
	public void setObj(Object obj) {
		this.obj = obj;
	}

}

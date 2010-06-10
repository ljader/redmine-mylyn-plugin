package net.sf.redmine_mylyn.api.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="settings")
public class Settings implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private boolean useIssueDoneRatio = true;

	public boolean isUseIssueDoneRatio() {
		return useIssueDoneRatio;
	}

	public void setUseIssueDoneRatio(boolean useIssueDoneRatio) {
		this.useIssueDoneRatio = useIssueDoneRatio;
	}
	
}

package net.sf.redmine_mylyn.internal.api.parser.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import net.sf.redmine_mylyn.internal.api.parser.adapter.type.EmbededPropertyType;

public class EmbededPropertyAdapter extends XmlAdapter<EmbededPropertyType, Integer> {

	@Override
	public EmbededPropertyType marshal(Integer arg0) throws Exception {
		return null;
	}

	@Override
	public Integer unmarshal(EmbededPropertyType arg0) throws Exception {
		return arg0.id;
	}
}

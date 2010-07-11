package net.sf.redmine_mylyn.internal.api.parser.adapter;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import net.sf.redmine_mylyn.internal.api.parser.adapter.type.CFbyTrackerType;

public class CFbyTrackerAdapter extends XmlAdapter<CFbyTrackerType, Map<Integer, int[]>> {

	@Override
	public CFbyTrackerType marshal(Map<Integer, int[]> map) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Integer, int[]> unmarshal(CFbyTrackerType type) throws Exception {
		int len = type.entrys==null ? 0 : type.entrys.size();
		Map<Integer, int[]> map = new HashMap<Integer, int[]>(len);

		for(len--; len>=0; len--) {
			map.put(type.entrys.get(len).trackerId, type.entrys.get(len).idList);
		}
		
		return map;
	}

}

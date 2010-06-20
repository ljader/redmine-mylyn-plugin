package net.sf.redmine_mylyn.api.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;


@XmlRootElement(name="version", namespace="http://redmin-mylyncon.sf.net/api")
@XmlAccessorType(XmlAccessType.NONE)
public class RedmineServerVersion {
	
	@XmlElement(namespace="http://redmin-mylyncon.sf.net/api")
	public SubVersion plugin;

	@XmlElement(namespace="http://redmin-mylyncon.sf.net/api")
	public SubVersion redmine;

	public enum Release {
		REDMINE_1_0(1, 0),
		PLUGIN_2_7(2, 7);

		public final int major;
		public final int minor;
		public final int tiny;

		Release(int major, int minor) {
			this(major, minor, 0);
		}

		Release(int major, int minor, int tiny) {
			this.major = major;
			this.minor = minor;
			this.tiny = tiny;
		}
	}
	
	@XmlAccessorType(XmlAccessType.NONE)
	public static class SubVersion implements Comparable<Release> {

		public int major;

		public int minor;

		public int tiny;
		
		private String versionString;

		@XmlValue
		public String getVersionString() {
			return versionString;
		}

		public void setVersionString(String versionString) {
			this.versionString = versionString;

			String[] parts = versionString.split("\\.");
			if (parts != null && parts.length >= 3) {
				try {
					major = Integer.parseInt(parts[0]);
					minor = Integer.parseInt(parts[1]);
					tiny = Integer.parseInt(parts[2]);
				} catch (NumberFormatException e) {
				}
			}
		}

		public int compareTo(Release release) {
			if (major < release.major) {
				return -1;
			}
			if (major > release.major) {
				return 1;
			}
			if (minor < release.minor) {
				return -1;
			}
			if (minor > release.minor) {
				return 1;
			}
			if (tiny < release.tiny) {
				return -1;
			}
			if (tiny > release.tiny) {
				return 1;
			}
			return 0;
		}

		public int compareTo(SubVersion version) {
			if (major < version.major) {
				return -1;
			}
			if (major > version.major) {
				return 1;
			}
			if (minor < version.minor) {
				return -1;
			}
			if (minor > version.minor) {
				return 1;
			}
			if (tiny < version.tiny) {
				return -1;
			}
			if (tiny > version.tiny) {
				return 1;
			}
			return 0;
		}

		@Override
		public String toString() {
			return String.format("%d.%d.%d", major, minor, tiny);
		}

	}

}
